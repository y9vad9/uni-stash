package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

public class MPI_3_10 {
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

        System.out.println("myrank = " + rank + ", size = " + size + "\n");

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 4 java -cp out/production/MPI_3_10 MPI_3_10 4

Output:
myrank = 0, size = 4

myrank = 3, size = 4

myrank = 2, size = 4

myrank = 1, size = 4

*/