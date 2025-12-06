package com.mathpar.students.OLD.llp2.students;

import com.mathpar.number.*;
import java.util.Random;
import com.mathpar.parallel.utils.parallel_debugger.ParallelDebug;
import com.mathpar.matrix.MatrixS;
import mpi.*;

public class MatrixMul4Dolgov {

    static int tag = 0;
    static int mod = 13;

    public static MatrixS add(MatrixS a, MatrixS b, Ring ring) {

        return (a.add(b, ring));

    }

    public static void main(String[] args) throws MPIException {
        Ring ring = new Ring("Z[x]");
        MPI.Init(new String[0]); // старт MPI
        // получение номера узла

        int rank = MPI.COMM_WORLD.getRank();
        int p = MPI.COMM_WORLD.getSize();
        ParallelDebug pred = new ParallelDebug(MPI.COMM_WORLD);
        int kol = 7;
        // pred.paddEvent("прием", "сумма");
        if (rank == 0) {
            // программа выполняется на нулевом процессоре

            MatrixS[] RES = new MatrixS[kol];
            int ord = 4;
            int den = 10000;
            // представитель класса случайного генератора
            Random rnd = new Random();
            // ord = размер матрицы, den = плотность
            MatrixS A = new MatrixS(ord, ord, den, new int[] {kol, kol}, rnd,
                    NumberZ.ONE, ring);
            // A.M.length;
            System.out.println("A" + A);
            MatrixS B = new MatrixS(ord, ord, den, new int[] {kol, kol}, rnd,
                    NumberZ.ONE, ring);
            System.out.println("B" + B);
            int al = A.size;
            int bl = B.size;

            SubsetZ aa = new SubsetZ(new int[] {0, al});
            SubsetZ bb = new SubsetZ(new int[] {0, bl});

            SubsetZ[] numA = aa.divideOnParts(p + 1);// разбивает АА на р+1
            // частей
            SubsetZ[] numB = bb.divideOnParts(p + 1);

            MatrixS[] a1 = new MatrixS[p];
            MatrixS[] b1 = new MatrixS[p];

            for (int i = 0; i < numA.length - 1; i++) {
                int end = numA[i].toArray()[1];
                if (end > al) {
                    end = al - 1;
                    System.out.println("end=" + end);
                }
                a1[i] = A.getSubMatrix(numA[i].toArray()[0], end, 0, A.colNumb);
                System.out.println("a1[" + i + "] = " + a1[i].toString(ring));
            }

            for (int i = 0; i < numB.length - 1; i++) {
                int end = numB[i].toArray()[1];
                if (end > bl) {
                    end = bl - 1;
                }
                b1[i] = B.getSubMatrix(numB[i].toArray()[0], end, 0, B.colNumb);
                System.out.println("b1[" + i + "] = " + b1[i].toString(ring));
            }

            MatrixS a0 = a1[0];
            MatrixS b0 = b1[0];
            MatrixS ab = a0.add(b0, ring);

            RES[0] = ab;
            System.out.println("0 = " + ab);
            Object[] send = new Object[2];
            if (rank == 0) {

                for (int i = 1; i < al; i++) {

                    send = new Object[] {a1[i], b1[i]};
                    //!!!! MPI.COMM_WORLD.Send(send, 0, 2, //!!!! MPI.OBJECT, i, 000);
                    pred.paddEvent("послал",
                            a1[i].toString(ring) + ";" + b1[i].toString(ring));

                    System.out.println("send to " + i);
                }

                // pred.paddEvent("отсылка ", ""+ Array.toString(send));
                for (int u = 1; u < p; u++) {
                    Object[] recv = new Object[1];
                    //!!!! MPI.COMM_WORLD.Recv(recv, 0, 1, //!!!! MPI.OBJECT, u, 111);

                    MatrixS s1 = (MatrixS) recv[0];
                    pred.paddEvent("принял " + rank, "" + s1);
                    RES[u] = s1;
                    System.out.println(u + "=" + s1);

                }
            }
            MatrixS[] new1 = {RES[0], RES[1], RES[2], RES[3]};

            System.out.println("!!!!!=" + Array.toString(new1));

        } else {
            Object[] recv = new Object[2];
            //!!!! MPI.COMM_WORLD.Recv(recv, 0, 2, //!!!! MPI.OBJECT, 0, 000);

            MatrixS s1 = (MatrixS) recv[0];

            MatrixS s2 = (MatrixS) recv[1];
            MatrixS res = s1.add(s2, ring);
            pred.paddEvent("принял " + rank, "" + res);
            System.out.println("recv " + rank);
            Object res1 = new Object[] {res};

            //!!!! MPI.COMM_WORLD.Send(res1, 0, 1, //!!!! MPI.OBJECT, 0, 111);
            pred.paddEvent("передал", "" + res.toString(ring));

        }

        pred.generateDebugLog();

		//!!!! MPI.Finalize();
    }
}
