package com.mathpar.students.OLD.llp2.students;

import java.util.Random;
import com.mathpar.parallel.utils.parallel_debugger.ParallelDebug;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;
import mpi.*;

//mpirun C java -cp /home/mixail/mathpar/target/classes:/home/mixail/lam_mpi/mpi/lib/classes -Djava.library.path=$LD_LIBRARY_PATH students.llp2.students.MatrixMu14Kulesh
public class MatrixMu14Kulesh {

    public static void main(String[] args) throws MPIException {
        //!!!! MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();// число всех процессоров
        Ring ring = new Ring("R[x]");
        int ord = 4;
        int den = 10000;
        MatrixS[] a1 = new MatrixS[size];
        VectorS B;
        ParallelDebug pred = new ParallelDebug(MPI.COMM_WORLD);
        Random rnd = new Random();
        if (rank == 0) {
            MatrixS A = new MatrixS(ord, ord, den, new int[] {5}, rnd, NumberR.ONE, ring);
            System.out.println("A" + A.toString(ring));
            MatrixS B_rnd = new MatrixS(1, ord, den, new int[] {5}, rnd, NumberR.ONE, ring);
            B = new VectorS(B_rnd.toScalarArray(ring)[0]);
            System.out.println("B" + B.toString(ring));

            int al = A.size;
            SubsetZ aa = new SubsetZ(new int[] {0, al});
            SubsetZ[] numA = aa.divideOnParts(size + 1);
            System.out.println("size=" + size);

            a1 = new MatrixS[size];
            for (int i = 0; i < numA.length - 1; i++) {
                int end = numA[i].toArray()[1];
                if (end > al) {
                    end = al - 1;
                    System.out.println("end=" + end);
                }
                a1[i] = A.getSubMatrix(numA[i].toArray()[0], end, 0, A.colNumb);
                pred.paddEvent("отсылка",
                        a1[i].toString(ring) + " ; " + B.toString(ring));

                System.out.println("a1[" + i + "] = " + a1[i].toString(ring));
            }
            MatrixS a = a1[0];
            VectorS ab = (VectorS) a.multiply(B, ring);
            for (int i = 1; i < a1.length; i++) {
                Object[] send = new Object[] {a1[i], B};
                //!!!! MPI.COMM_WORLD.Send(send, 0, 2, //!!!! MPI.OBJECT, i, 000);
                System.out.println("0 send to " + i + " ;a1="
                        + a1[i].toString(ring) + ";b=" + B.toString(ring));
            }
            Element[] res1 = new Element[A.M.length];
            res1[0] = ab;
            int point = 0;
            VectorS d = new VectorS();
            for (int i = 0; i < size; i++) {
                if (i == 0) {
                    d = ab;
                } else {
                    Object[] res2 = new Object[1];
                    //!!!! MPI.COMM_WORLD.Recv(res2, 0, 1, //!!!! MPI.OBJECT, i, 111);
                    d = (VectorS) res2[0];
                }
                for (int j = 0; j < d.V.length; j++) {
                    res1[j + point] = d.V[j];
                }
                point += d.V.length;
                System.out.println("end=" + Array.toString(res1));
                pred.paddEvent("результат M*V", Array.toString(res1));
            }

        } else {
            Object[] recv = new Object[2];
            //!!!! MPI.COMM_WORLD.Recv(recv, 0, 2, //!!!! MPI.OBJECT, 0, 000);
            System.out.println("recv " + rank);
            MatrixS a = (MatrixS) recv[0];
            VectorS b = (VectorS) recv[1];

            VectorS res = (VectorS) a.multiply(b, ring);
            System.out.println("res=" + res + "   my=" + rank);
            pred.paddEvent("прием", a.toString(ring) + " ; " + b.toString(ring));

            Object[] send1 = new Object[] {res};
            //!!!! MPI.COMM_WORLD.Send(send1, 0, 1, //!!!! MPI.OBJECT, 0, 111);
        }
        pred.generateDebugLog();
        //!!!! MPI.Finalize();
    }
}
