package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part2;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

// COMMAND: mpirun -np 2 java -cp out/production/Task_3_10_mpi Task_3_10_mpi

// OUTPUT:
//myrank: 0
//size: 2
//myrank: 1
//size: 2


public class Task_3_10_mpi {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int n = Integer.parseInt(args[0]);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        if (rank == 0) {
            int[] array = new int[n];
            for (int i = 1; i < size; i++)
                MPI.COMM_WORLD.send(array, n, MPI.INT, i, 1);
        } else {
            Status st = null;
            while (st == null) {
                st = MPI.COMM_WORLD.probe(0, 1);
            }
            int[] array = new int[n];
            MPI.COMM_WORLD.recv(array, n, MPI.INT, 0, 1);
        }
        System.out.println("myrank: " + rank);
        System.out.println("size: " + size);
        MPI.Finalize();
    }
}
