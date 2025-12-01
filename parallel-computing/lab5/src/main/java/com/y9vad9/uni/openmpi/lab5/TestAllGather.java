package com.y9vad9.uni.openmpi.lab5;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestAllGather {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = 2;

        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = myrank * 10 + i;
        System.out.println("myrank = " + myrank + " : a = "
            + Arrays.toString(a));

        int[] q = new int[n * np];
        MPI.COMM_WORLD.allGather(a, n, MPI.INT, q, n, MPI.INT);
        System.out.println("myrank = " + myrank + " : q = "
            + Arrays.toString(q));

        MPI.Finalize();
    }
}
