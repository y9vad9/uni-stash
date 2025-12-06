package com.y9vad9.uni.openmpi.lab4;

import java.util.Arrays;
import mpi.*;

public class TestBcast {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int n = 5;
        int[] a = new int[n];

        if (myrank == 0) {
            for (int i = 0; i < n; i++) a[i] = 10 + i; // прибираємо myrank -> значення завжди нульове
            System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));
        }

        MPI.COMM_WORLD.bcast(a, a.length, MPI.INT, 0);

        if (myrank != 0)
            System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));

        MPI.Finalize();
    }
}
