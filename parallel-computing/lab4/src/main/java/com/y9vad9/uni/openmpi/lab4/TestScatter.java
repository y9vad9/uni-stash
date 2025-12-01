package com.y9vad9.uni.openmpi.lab4;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestScatter {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = 6;
        int[] a = new int[n * np];

        if (myrank == 0) {
            for (int i = 0; i < n; i++)
                // myrank Завжди дорівнює нулю в прикладі, тож `myrank * 10 +` просто пропускаємо
                a[i] = i;
            System.out.println("myrank = " + myrank + ":a = " + Arrays.toString(a));
        }

        int[] q = new int[n / 2];

        MPI.COMM_WORLD.scatter(a, n / 2, MPI.INT, q, n / 2, MPI.INT, 0);
        System.out.println("myrank = " + myrank + ":q = " + Arrays.toString(q));

        MPI.Finalize();
    }
}