package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import java.util.Arrays;
import mpi.MPI;
import mpi.MPIException;

public class MPI_3_14 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];

        for (int i = 0; i < n; i++)
            a[i] = i;

        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));

        int[] q = new int[n];

        MPI.COMM_WORLD.scan(a, q, n, MPI.INT, MPI.SUM);

        for (int j = 0; j < np; j++) {
            if (myrank == j)
                System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
        }

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 4 java -cp out/production/MPI_3_14 MPI_3_14 4

Output:
myrank = 0: a = [0, 1, 2, 3]
myrank = 2: a = [0, 1, 2, 3]
myrank = 0: q = [0, 1, 2, 3]
myrank = 1: a = [0, 1, 2, 3]
myrank = 3: a = [0, 1, 2, 3]
myrank = 1: q = [0, 2, 4, 6]
myrank = 2: q = [0, 3, 6, 9]
myrank = 3: q = [0, 4, 8, 12]
*/