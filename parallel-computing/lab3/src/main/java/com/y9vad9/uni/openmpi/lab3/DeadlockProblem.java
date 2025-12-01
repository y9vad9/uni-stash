package com.y9vad9.uni.openmpi.lab3;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;

public class DeadlockProblem {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int n = 200_000; // великий → гарантований дедлок

        int[] a = new int[n];
        int[] b = new int[n];

        if (rank == 0) {
            Random rnd = new Random();
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(100);

            MPI.COMM_WORLD.send(a, n, MPI.INT, 1, 0);
            MPI.COMM_WORLD.recv(b, n, MPI.INT, 1, 1);

            System.out.println("Rank 0: b[0] = " + b[0]);
        }

        if (rank == 1) {
            Random rnd = new Random();
            for (int i = 0; i < n; i++) b[i] = rnd.nextInt(100);

            MPI.COMM_WORLD.send(b, n, MPI.INT, 0, 1);
            MPI.COMM_WORLD.recv(a, n, MPI.INT, 0, 0);

            System.out.println("Rank 1: a[0] = " + a[0]);
        }

        MPI.Finalize();
    }
}
