package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part2;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

// COMMAND: mpirun -np 2 java -cp out/production/Task_3_2_mpi Task_3_2_mpi 4

// OUTPUT:
//myrank = 0 : a = [0, 0, 0, 0]
//myrank = 1 : a = [1, 1, 1, 1]
//myrank = 1 : q = [0, 0, 0, 0, 1, 1, 1, 1]


public class Task_3_2_mpi {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = myrank;
        System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));
        int[] q = new int[n * np];
        MPI.COMM_WORLD.gather(a, n, MPI.INT, q, n, MPI.INT, np - 1);
        if (myrank == np - 1) {
            System.out.println("myrank = " + myrank + " : q = " + Arrays.toString(q));
        }
        MPI.Finalize();
    }
}
