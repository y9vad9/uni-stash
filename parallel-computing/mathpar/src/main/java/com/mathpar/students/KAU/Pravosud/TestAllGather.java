package com.mathpar.students.KAU.Pravosud;

/*
    Command to run:
    mpirun  --hostfile hostfile  -np 4 java  -cp ./target/classes com/mathpar/students/KAU/Pravosud/TestAllGather
 */

/*
    Result:
    myrank = 1 : a = [10, 11]
    myrank = 0 : a = [0, 1]myrank = 3 : a = [30, 31]

    myrank = 2 : a = [20, 21]
    myrank = 2 : q = [0, 1, 10, 11, 20, 21, 30, 31]
    myrank = 3 : q = [0, 1, 10, 11, 20, 21, 30, 31]
    myrank = 0 : q = [0, 1, 10, 11, 20, 21, 30, 31]
    myrank = 1 : q = [0, 1, 10, 11, 20, 21, 30, 31]
 */

import mpi.MPI;

import java.util.Arrays;

public class TestAllGather {
    public static void main(String[] args)throws Exception {
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        // визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = 2;
        int[] a = new int[n];
        for(int i = 0; i < n; i++) {
            a[i] = myrank*10+i;
        }
        System.out.println("myrank = " + myrank + " : a = "+ Arrays.toString(a));
        int[] q = new int[n * np];
        MPI.COMM_WORLD.allGather(a, n, MPI.INT, q, n, MPI.INT);
        System.out.println("myrank = " + myrank + " : q = "+ Arrays.toString(q));
        // завершення паралельної частини
        MPI.Finalize();
    }
}