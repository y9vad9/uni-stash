package com.mathpar.students.OLD.llp2.students;

//import helloworld//!!!! MPI.*;
//mpirun C java -cp /home/klochneva/mathpar/target/classes -Djava.library.path=$LD_LIBRARY_PATH llp2.student.MatrixSPovMul
/*  @author klochneva
 */

/**
 *Параллельная программа возведения матрицы M в степень n (n>1) на 2 процесорах по бинарному алгоритму
 * Входные данные: матрица и степень считываются из файла из домашней директории
 * Возвращает: M в степени n
 * @author klochneva
 */
public class MatrixSPovMul {

//    public static void main(String[] args)
//            throws MPIException, FileNotFoundException, IOException {
//        // System.out.println("Hello");
//        MPI mpi = new MPI();
//        //!!!! MPI.Init(args);//инициализация MPI
//        int myrank = //!!!! MPI.COMM_WORLD.getRank(); //определеине номера процессор
//        int proc = //!!!! MPI.COMM_WORLD.Size();
//        int root = 0;
//        int pow = 2;
//        int root1 = 1;
//        int[] G2 = new int[]{4, 5, 6, 7};
//        MatrixS M = null;
//        Ring ring = null;
//        if (myrank == root) {
//            ring = new Ring("Z[x]");
//            Random rnd = new Random();
//            int size = 2;
//            int n = 3;
//
//            M = new MatrixS(size, size, 1000, new int[]{6, 6}, rnd, NumberZ.ONE, ring);
//
//            System.out.println("M1 = " + M + " pow = " + pow + " size = " + size);
//        }
//        MatrixS Result = MatrixPow((MatrixS) M, pow, root, root1, mpi, ring);
//
//        //!!!! MPI.Finalize();
//    }
//
//    //////////////////////Для 2-х процессоров//////////////////////////////////////////////
//    public static MatrixS MatrixPow(MatrixS M, int pow, int root, int root1, MPI mpi, Ring ring) throws MPIException {
//
//        int myrank = //!!!! MPI.COMM_WORLD.getRank(); //определеине номера процессор]
//        int proc = //!!!! MPI.COMM_WORLD.Size();
//        Element res = null;
//        Element temp = null;
//        //   System.out.println(" Mat = "+ M.toString(ring)+"  "+myrank);
//        if (myrank == 0) {
//
//            Element one = M.myOne(ring);
//            llp2.Transport.sendObject(M, 1, 2);
//            llp2.Transport.sendObject(one, 1, 3);
//            llp2.Transport.sendObject(pow, 1, 4);
//
//
//            //------------
//            //проверка матрицы на равенство 1
//            long time1 = System.currentTimeMillis();
//            if (M.isOne(ring)) {
//                System.out.println("Result = " + one);
//                //!!!! MPI.Finalize();
//                return (MatrixS) one;
//            }
//            res = one;
//            temp = M;
//            if ((pow & 1) == 1) {
//                res = temp;
//            }
//            pow >>>= 1;
//            while (pow != 0) {
//                if ((pow & 1) == 1) {
//                    Object ob = llp2.Transport.recvObject(1, 2);//прием от первого процессора
//                    res = (res.multiply((MatrixS) ob, ring));
//
////                        MatrixD OB = new MatrixD(((MatrixS)ob).toSquaredArray(ring));
////                        MatrixD RES = new MatrixD(((MatrixS)res).toScalarArray(ring));
//
//                }
//                pow >>>= 1;
//            }
//            System.out.println("Result = " + res);
//            long time2 = System.currentTimeMillis() - time1;
//            System.out.println("time=" + time2);
//
//
//
//        } else {
//            //MatrixS S = (MatrixS) Transport.recvObject(0, 2);
//
//            M = (MatrixS) llp2.Transport.recvObject(0, 2);
//            MatrixS one = (MatrixS) llp2.Transport.recvObject(0, 3);
//            int n = (Integer) llp2.Transport.recvObject(0, 4);
//            //------------
//            //проверка матрицы на равенство 1
//
//            if (M.isOne(ring)) {
//                System.out.println("Result = " + one);
//                //!!!! MPI.Finalize();
//                return one;
//            }
//            res = one;
//            temp = M;
//            if ((n & 1) == 1) {
//                res = temp;
//            }
//            n >>>= 1;
//            while (n != 0) {
//
//                temp = (temp.multiply(temp, ring));
//
//                if ((n & 1) == 1) {
//                    llp2.Transport.sendObject(temp, 0, 2);//посылка нулевому процессору результата умножения
//
//                }
//                n >>>= 1;
//            }
//        }
//        return (myrank == root) ? (MatrixS) res : null;
//
//    }


}
