package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import java.util.Arrays;
import mpi.MPI;
import mpi.MPIException;

public class MPI_3_13 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        int size = MPI.COMM_WORLD.getSize();

        for (int i = 0; i < n; i++)
            a[i] = i;

        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));

        int[] q = new int[n];

        int[] recvSizes = new int[size];
        Arrays.fill(recvSizes, 1);

        MPI.COMM_WORLD.reduceScatter(a, q, recvSizes, MPI.INT, MPI.SUM);

        if (myrank == 0)
            System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 4 java -cp out/production/MPI_3_13 MPI_3_13 4

Output:
myrank = 2: a = [0, 1, 2, 3]
myrank = 3: a = [0, 1, 2, 3]
myrank = 0: a = [0, 1, 2, 3]
myrank = 1: a = [0, 1, 2, 3]
myrank = 0: q = [0, 0, 0, 0]

*/