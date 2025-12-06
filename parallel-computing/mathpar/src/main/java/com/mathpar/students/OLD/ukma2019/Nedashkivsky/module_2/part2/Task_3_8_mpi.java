package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part2;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

// COMMAND: mpirun -np 2 java -cp out/production/Task_3_8_mpi Task_3_8_mpi 2

// OUTPUT:
//np: 2
//myrank = 1: a = [1, 1]
//np: 2
//myrank = 0: a = [0, 0]
//myrank = 0: q = [0, 1]
//myrank = 1: q = [0, 1]


public class Task_3_8_mpi {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        System.out.println("np: " + np);
        int n = Integer.parseInt(args[0]);
        int [] a = new int[n];
        for(int i = 0; i < n; i++) a[i] = myrank;
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.allToAll(a, 1, MPI.INT, q, 1, MPI.INT);
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
        MPI.Finalize();
    }
}
