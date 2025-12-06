package com.mathpar.students.OLD.curse5_2015.gladyshev;

import mpi.*;
import com.mathpar.parallel.ddp.engine.*;
import com.mathpar.number.Ring;
import com.mathpar.matrix.MatrixD;
import java.util.Random;

class multiply {

    public static void main(String[] args) throws Exception{
        MPI.Init(args);
        Ring ring = new Ring("Z[]");

/*        MatrixD a = new MatrixD(new int[][]{
                {2, 5, 7, 1},
                {1, 5, 8, 2},
                {3, 7, 5, 4},
                {1, 6, 8, 1}}, ring);
        MatrixD b = new MatrixD(new int[][]{
                {1, 6, 8, 2},
                {4, 7, 1, 4},
                {3, 7, 9, 1},
                {5, 1, 2, 8}}, ring);
*/
        int n = Integer.parseInt(args[0]);
        int length = Integer.parseInt(args[1]);
        MatrixD a = new MatrixD(n, n, 100, new int[]{length}, new Random(), ring);
        MatrixD b = new MatrixD(n, n, 100, new int[]{length}, new Random(), ring);
        factory f = new factory();

        long time = System.currentTimeMillis();
        DispThread disp = new DispThread(0, f, 7, 10, args, new Object[]{a, b, ring});
        task ab = (task)disp.GetStartTask();
        int myrank = MPI.COMM_WORLD.getRank();
        if (myrank == 0){
            System.out.println("time1 = "+(System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
            MatrixD res = (MatrixD) ab.A.multiply(ab.B, ring);
            System.out.println("time2 = "+(System.currentTimeMillis() - time));
            MatrixD sub = ab.C.subtract(res, ring);
            if (sub.isZero(ring)) {
               System.out.println("oK!!!");
            } else{
                System.out.println("error");
            }
//            System.out.println("A"+ab.A);
//            System.out.println("B"+ab.B);
//            System.out.println("C"+ab.C);
        }
        MPI.Finalize();
    }
}
