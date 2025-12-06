package com.mathpar.students.KAU.Zhytar;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestReduce {
    public static void main(String[] args) throws MPIException {
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        int n = 5;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = myrank*10+i;
        }
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.reduce(a, q, n, MPI.INT, MPI.SUM, 0);
        if (myrank == 0)
            System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
        // завершення паралельної частини
        MPI.Finalize();
    }
}

/*
    Command to run:
    mpirun  --hostfile hostfile  -np 7 java  -cp ./target/classes com/mathpar/students/KAU/Zhytar/TestReduce
 */

/*
    Result:
    myrank = 0: a = [0, 1, 2, 3, 4]
    myrank = 2: a = [20, 21, 22, 23, 24]
    myrank = 3: a = [30, 31, 32, 33, 34]
    myrank = 5: a = [50, 51, 52, 53, 54]
    myrank = 6: a = [60, 61, 62, 63, 64]
    myrank = 1: a = [10, 11, 12, 13, 14]
    myrank = 4: a = [40, 41, 42, 43, 44]
    myrank = 0: q = [210, 217, 224, 231, 238]
 */
