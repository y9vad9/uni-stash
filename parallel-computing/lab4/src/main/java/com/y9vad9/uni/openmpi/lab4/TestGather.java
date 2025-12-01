package com.y9vad9.uni.openmpi.lab4;

import mpi.MPI;

import java.util.Arrays;

public class TestGather {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = 5;
        int[] a = new int[n];

        for (int i = 0; i < n; i++) a[i] = myrank * 10 + i;
        System.out.println("myrank = " + myrank + " : a = "
            + Arrays.toString(a));

        int[] q = new int[n * np];
        MPI.COMM_WORLD.gather(a, n, MPI.INT, q, n, MPI.INT, np - 1);

        if (myrank == np - 1)
            System.out.println("myrank = " + myrank + " : q = "
                + Arrays.toString(q));

        MPI.Finalize();
    }
}
