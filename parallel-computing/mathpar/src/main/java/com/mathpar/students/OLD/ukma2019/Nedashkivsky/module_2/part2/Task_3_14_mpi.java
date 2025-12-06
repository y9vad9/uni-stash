package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part2;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

// COMMAND: mpirun -np 2 java -cp out/production/Task_3_14_mpi Task_3_14_mpi 2

// OUTPUT:
//myrank = 1: a = [0, 1]
//myrank = 0: a = [0, 1]
//myrank = 1: q = [0, 2]
//myrank = 0: q = [0, 1]


public class Task_3_14_mpi {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.scan(a, q, n, MPI.INT, MPI.SUM);
        for (int j = 0; j < np; j++)
            if (myrank == j) {
                System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
            }
        MPI.Finalize();
    }
}
