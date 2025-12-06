package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import java.util.Arrays;
import mpi.MPI;
import mpi.MPIException;

public class MPI_3_7 {
    public static void main(String[] args) throws MPIException, InterruptedException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];

        for (int i = 0; i < n; i++)
            a[i] = i;

        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));

        int[] q = new int[n * np];

        MPI.COMM_WORLD.allGatherv(a, n, MPI.INT,
                q, new int[]{n, n}, new int[]{2, 0}, MPI.INT);

        Thread.sleep(60 * myrank);

        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 4 java -cp out/production/MPI_3_7 MPI_3_7 4

Output:
myrank = 2 : a = [2, 2, 2, 2]
myrank = 1 : a = [1, 1, 1, 1]
myrank = 3 : a = [3, 3, 3, 3]
myrank = 0 : a = [0, 0, 0, 0]
myrank = 0 : q = [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3]
myrank = 1 : q = [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3]
myrank = 2 : q = [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3]
myrank = 3 : q = [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3]
*/