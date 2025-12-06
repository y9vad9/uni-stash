package com.mathpar.students.OLD.llp2.students;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import mpi.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.parallel_debugger.ParallelDebug;

/**
 *
 * @author klochneva
 */
public class MatrixSPovMulti {

    public static void main(String[] args)
            throws MPIException, FileNotFoundException, IOException {
        MPI mpi = new MPI();
        MPI.Init(args);//инициализация MPI
        ParallelDebug pd = new ParallelDebug(MPI.COMM_WORLD);   // ЗАПУСКАЕМ ОТЛАДЧИК с именем pd
        int myrank = MPI.COMM_WORLD.getRank(); //определеине номера процессор
        int proc = MPI.COMM_WORLD.getSize();// определяем количество процессоров
        SubsetZ All_proc = new SubsetZ(new int[] {0, proc - 1});
        int root = 0;//главный процессор
        int pow = 3;
        int P = proc - 1;
        SubsetZ[] For_comms = All_proc.divideOnParts(2);
        int[] G1 = For_comms[0].toFullArray();
        int[] G2 = For_comms[1].toFullArray();
        System.out.println(" G1 = " + Array.toString(G1));
        System.out.println(" G2 = " + Array.toString(G2));

//        int[] G1 = new int[proc / 2];
//        int[] G2 = new int[proc / 2];
//        for (int i = 0; i <= (proc - 1); i++) {
//            if (i < proc / 2) {
//                G1[i] = i;
//            }
//            if (i >= proc / 2) {
//                G2[i - proc / 2] = i;
//            }
//        }
//        int[] G1 = new int[]{0, 1, 2, 3};// 1 группа процессоров
//        int[] G2 = new int[]{4, 5, 6, 7};// 2 группа процессоров
        MatrixS M = null;
        MatrixS ResultMul = null;
        MatrixS Posled = null;
        Ring ring = null;
        Element one = null;
        Object[] MM = new Object[2];
        Object[] MM_Ring = new Object[1];
        if (myrank == root) {
            ring = new Ring("Z[x]");
            Random rnd = new Random();
            int size = 6;
            M = new MatrixS(size, size, 25, new int[] {2, 2}, rnd, NumberZ.ONE, ring);
            one = M.myOne(ring);
            MM[0] = M;
            MM_Ring[0] = ring;
            MM[1] = one;
//           long time3 = System.currentTimeMillis();

            Posled = (MatrixS) M.pow(pow, ring);
//           long time4 = System.currentTimeMillis() - time3;
//           System.out.println("time_posled = "+time4);
        }
        //!!!! MPI.COMM_WORLD.Bcast(MM, 0, 2, //!!!! MPI.OBJECT, root);// процессор root отправляет массив ММ всем процессора
        //!!!! MPI.COMM_WORLD.Bcast(MM_Ring, 0, 1, //!!!! MPI.OBJECT, root);//процессор root отправляет массив ММ_Ring всем процессорам
        long time1 = System.currentTimeMillis();
        ResultMul = MatrixPovMul((MatrixS) MM[0], pow, (Element) MM[1], G1, G2, mpi, root, (Ring) MM_Ring[0]);

        long time2 = System.currentTimeMillis() - time1;
        if (myrank == 0) {
            System.out.println("Posledov === " + Posled);
            System.out.println("ResultMul = " + ResultMul);
            System.out.println("Проверка!!!!! " + ResultMul.subtract(Posled, Ring.ringR64xyzt));
            System.out.println("time_parall = " + time2);
        }
        //!!!! MPI.Finalize();
    }

    public static MatrixS MatrixPovMul(MatrixS M, int pow, Element one, int[] G1, int[] G2, MPI mpi, int root, Ring ring) throws MPIException, IOException {
        int myrank = MPI.COMM_WORLD.getRank();
        int proc = MPI.COMM_WORLD.getSize();

       //!!!!MPI.Group g1 = MPI.COMM_WORLD.getGroup().incl(G1);
        //!!!!Intracomm COMM_NEW1 =  MPI.COMM_WORLD.Creat(g1);
        //!!!!MPI.Group g2 =  MPI.COMM_WORLD.Group().Incl(G2);
        //!!!!Intracomm COMM_NEW2 = //!!!! MPI.COMM_WORLD.Creat(g2);
        MPI.COMM_WORLD.barrier();
        int size1 = 0; //!!!!g1.Size();
        int size2 = 0; //!!!!g2.Size();
        Element res = null;
        Element temp = null;
        MatrixD TEMP = null;
        MatrixD RES = null;
        MatrixD OB = null;
        MatrixS T = null;
        Object[] POW = new Object[1];
        POW[0] = new Integer(pow);
        Object[] OB_send = new Object[1];
        Object[] RES_send = new Object[1];
        Object[] TEMP_send = new Object[1];
        Object[] OB_new = new Object[1];
        Object[] RES_new = new Object[1];
        Object[] TEMP_new = new Object[1];
        int n = (Integer) POW[0];
        int[] send = new int[4];
        int[] dis = new int[4];
        int rootG2 = G2[0];
        ////!!!! MPI.COMM_WORLD.Barrier();
        if (myrank < rootG2) {

            //long time1 = System.currentTimeMillis();
            res = one;
            temp = M;

            if ((n & 1) == 1) {

                res = temp;
            }
            n >>>= 1;

            while (n != 0) {

                if ((n & 1) == 1) {

                    if (myrank == root) {

                      //!!!!  Object ob = Transport.recvObject(rootG2, 0);// ???????????     //прием от процессора proc/2
                        //!!!!     OB = new MatrixD(((MatrixS) ob).toScalarArray(ring));
                        RES = new MatrixD(((MatrixS) res).toScalarArray(ring));
                        //System.out.println("");
                        OB_send[0] = OB;

                        RES_send[0] = RES;

                    }

                //!!!!    COMM_NEW1.Bcast(OB_send, 0, 1, //!!!! MPI.OBJECT, root);
                 //!!!!   COMM_NEW1.Bcast(RES_send, 0, 1, //!!!! MPI.OBJECT, root);
//
                 //!!!!   RES = Pashin_new.multiplyPar((MatrixD) RES_send[0], (MatrixD) OB_send[0], COMM_NEW1, root, ring);
                }
                n >>>= 1;
            }

            //long time2 = System.currentTimeMillis() - time1;
            //System.out.println("time=" + time2);
        } else {

            res = one;
            temp = M;

            if ((n & 1) == 1) {
                res = temp;

            }

            n >>>= 1;

            while (n != 0) {

                if (myrank == rootG2) {

                    TEMP = new MatrixD(((MatrixS) res).toScalarArray(ring));

                    TEMP_send[0] = TEMP;

//
                }

             //!!!!   COMM_NEW2.Bcast(TEMP_send, 0, 1, //!!!! MPI.OBJECT, 0); // proc/2  замена на 0
              //!!!!  TEMP = Pashin_new.multiplyPar((MatrixD) TEMP_send[0], (MatrixD) TEMP_send[0], COMM_NEW2, 0, ring);
                if (myrank == rootG2) {

                    if ((n & 1) == 1) {
                        T = new MatrixS(TEMP, ring);
                        //!!!!      Transport.sendObject(T, root, 0);//посылка процессору root результата умножения

                    }
                }
                n >>>= 1;
            }

        }

        MatrixS result = (myrank == root) ? new MatrixS(RES, ring) : null;
        return result;

    }
}
