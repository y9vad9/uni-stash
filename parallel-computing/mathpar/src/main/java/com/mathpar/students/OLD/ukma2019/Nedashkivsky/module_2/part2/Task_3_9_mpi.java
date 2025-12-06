package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part2;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

// COMMAND: mpirun -np 2 java -cp out/production/Task_3_9_mpi Task_3_9_mpi 4

// OUTPUT:
//myrank = 0: a = [0, 0, 0, 0]
//myrank = 1: a = [1, 1, 1, 1]
//myrank = 0: q = [0, 1, 0, 0]
//myrank = 1: q = [0, 1, 0, 0]


public class Task_3_9_mpi {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = myrank;
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.allToAllv(a, new int[]{1, 1,1,1}, new int[]{0,1, 2,3}, MPI.INT, q, new int[]{1, 1,1,1}, new int[]{0, 1,2,3}, MPI.INT);
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
        MPI.Finalize();
    }
}
