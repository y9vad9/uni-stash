package com.mathpar.students.KAU.Pravosud;

/*
    Command to run:
    mpirun  --hostfile hostfile  -np 4 java  -cp ./target/classes com/mathpar/students/KAU/Pravosud/TestReduceScatter
 */

/*
    Result:
    myrank = 2: a = [20, 21, 22, 23, 24, 25, 26, 27, 28, 29]
    myrank = 0: a = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]myrank = 1: a = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19]

    myrank = 3: a = [30, 31, 32, 33, 34, 35, 36, 37, 38, 39]
    myrank = 1: q = [64, 68, 0, 0, 0, 0, 0, 0, 0, 0]
    myrank = 3: q = [84, 88, 92, 96, 0, 0, 0, 0, 0, 0]
    myrank = 0: q = [60, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    myrank = 2: q = [72, 76, 80, 0, 0, 0, 0, 0, 0, 0]
 */

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestReduceScatter {
    public static void main(String[] args) throws MPIException {
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        int n = 10;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = myrank*10+i;
        }
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.reduceScatter(a, q, new int[]{1, 2, 3, 4},
                MPI.INT, MPI.SUM);

       // if (myrank == 0) {
            System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
       // }
        // завершення паралельної частини
        MPI.Finalize();
    }
}
