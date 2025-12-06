package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part1;

import mpi.MPI;
import mpi.MPIException;

// COMMAND: mpirun -np 2 java -cp out/production/Task_2_9_mpi Task_2_9_mpi

// OUTPUT:
//a = 0
//a = 1
//rank = 0
//myrank = 0 ; 0
//
//myrank = 0 ; 0
//
//myrank = 1 ; 1
//
//myrank = 1 ; 0


public class Task_2_9_mpi {
    public static void main(String[] args) throws MPIException {
        // Initialization MPI
        MPI.Init(args);
        // Definition a processor amount
        int myrank = MPI.COMM_WORLD.getRank();
        // Declaration an array of objects
        int[] a = new int[2];
        // Filling these array on a first processor
        if (myrank == 0) {
            for (int i = 0; i < 2; i++) {
                a[i] = i;
                System.out.println("a = " + a[i] + " ");
            }
            System.out.println("rank = " + myrank);
        }
        // Declaration an array in which elements accepted by the processor
        // will be recorded
        int[] q = new int[2];
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.scatter(a, 1, MPI.INT, q, 1, MPI.INT, 0);
        // Printing received arrays and numbers of processors
        for (int i = 0; i < q.length; i++)
            System.out.println("myrank = " + myrank + " ; " + q[i] + "\n");
        // Completion a parallel part
        MPI.Finalize();
    }
}
