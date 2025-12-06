package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part1;

import mpi.MPI;
import mpi.MPIException;

// COMMAND: mpirun -np 2 java -cp out/production/Task_2_10_mpi Task_2_10_mpi

// OUTPUT:
//myrank = 0; 0
//myrank = 0; 1
//myrank = 0; 2
//myrank = 1; 1
//myrank = 1; 2
//myrank = 1; 85097
//myrank = 1; 852482
//myrank = 0; 852482

public class Task_2_10_mpi {
    public static void main(String[] args) throws MPIException {
        // Initialization MPI
        MPI.Init(args);
        // Definition a processor amount
        int myrank = MPI.COMM_WORLD.getRank();
        int n = 4;
        if (args.length != 0)
        {
            n = Integer.parseInt(args[0]);
        }
        // Declaration an array of objects
        int[] a = new int[n];
        // Filling these array on a first processor
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++)
                a[i] = i;
        }
        // Declaration an array in which elements accepted by the processor will be recorded
        int[] q = new int[n];
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.scatterv(a, new int[]{3, 2, 1, 1},
                new int[]{0, 1, 2, 0}, MPI.INT, q, n, MPI.INT, 0);
        // Printing received arrays and numbers of processors which has accepted
        for (int i = 0; i < q.length; i++)
            System.out.print("myrank = " + myrank + "; " + q[i] + "\n");
        // Completion a parallel part
        MPI.Finalize();
    }
}
