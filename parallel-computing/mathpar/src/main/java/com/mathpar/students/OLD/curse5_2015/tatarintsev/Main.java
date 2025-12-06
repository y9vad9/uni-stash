package com.mathpar.students.OLD.curse5_2015.tatarintsev;

import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.matrix.*;
import mpi.*;
import com.mathpar.number.*;
import java.util.Random;


public class Main {
    static int size_of_little_matrix;
    static Ring ring;
    public static void main(String[] args) throws MPIException, InterruptedException {

        MPI.Init(args);
        int size = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int length = Integer.parseInt(args[1]);
        int myrank = MPI.COMM_WORLD.getRank();


        size_of_little_matrix = n;
        for(int i=0; size > Math.pow(8, i); i++) {
            size_of_little_matrix /= 2;
        }

        MatrixD A = null;
        MatrixD B = null;
        ring = new Ring("Z[]");
        if(myrank == 0) {
            System.out.println("size_of_little_matrix = "+ size_of_little_matrix);
            A = new MatrixD(n, n, 10000, new int[] {length}, new Random(), ring);
            B = new MatrixD(n, n, 10000, new int[] {length}, new Random(), ring);
        }

        FactoryMultiplyMatrix f = new FactoryMultiplyMatrix();

        long time = System.currentTimeMillis();
        DispThread disp = new DispThread(0, f, 2, 10, args, new Object[]{A, B});


        if (myrank==0){
            System.out.println("time1 = "+(System.currentTimeMillis() - time));
            TaskMultiplyMatrix ab = (TaskMultiplyMatrix)disp.GetStartTask();
            time = System.currentTimeMillis();
            MatrixD res = (MatrixD) ab.a.multiply(ab.b, ring);
            System.out.println("time2 = "+(System.currentTimeMillis() - time));


            MatrixD sub = ab.c.subtract(res, ring);


            if (sub.isZero(ring)) {
                System.out.println("ok");
            } else{
                System.out.println("error");
           }
        }
        MPI.Finalize();
    }
}
