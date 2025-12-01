package com.y9vad9.uni.openmpi.lab5;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestAllToAllv {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int n = 4;
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = myrank * 10 + i;

        System.out.println("myrank = " + myrank + ":a = " + Arrays.toString(a));

        int[] q = new int[n];
        MPI.COMM_WORLD.allToAllv(a, new int[]{1, 1, 1, 1},
            new int[]{0, 1, 2, 3}, MPI.INT, q,
            new int[]{1, 1, 1, 1}, new int[]{3, 2, 1, 0}, MPI.INT);

        System.out.println("myrank = " + myrank + ":q = " + Arrays.toString(q));
        MPI.Finalize();
    }
}