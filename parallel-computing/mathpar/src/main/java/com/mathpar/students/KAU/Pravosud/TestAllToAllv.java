package com.mathpar.students.KAU.Pravosud;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/*
    Command to run:
    mpirun  --hostfile hostfile  -np 4 java  -cp ./target/classes com/mathpar/students/KAU/Pravosud/TestAllToAllv
 */

/*
    Result:
    myrank = 0: a = [0, 1, 2, 3]
    myrank = 2: a = [20, 21, 22, 23]
    myrank = 3: a = [30, 31, 32, 33]
    myrank = 1: a = [10, 11, 12, 13]
    myrank = 0: q = [30, 20, 10, 0]
    myrank = 1: q = [31, 21, 11, 1]
    myrank = 2: q = [32, 22, 12, 2]
    myrank = 3: q = [33, 23, 13, 3]
 */

public class TestAllToAllv {
    public static void main(String[] args) throws MPIException {
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        int n = 4;
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = myrank*10+i;
        }
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        Integer[] q = new Integer[n];
        MPI.COMM_WORLD.allToAllv(a, new int[]{1, 1, 1, 1},
                new int[]{0, 1, 2, 3}, MPI.INT, q, new int[]{1, 1, 1, 1},
                new int[]{3, 2, 1, 0}, MPI.INT);

        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        // завершення паралельної частини
        MPI.Finalize();
    }
}
