package com.y9vad9.uni.openmpi.lab4;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestScatterv {
    public static void main(String[] args)
        throws MPIException {

        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = 4; // розмір частини для кожного процесора

        int[] a = new int[n * np]; // тільки процесор 0 ініціалізує
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) a[i] = i;
            System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        }

        int[] q = new int[n]; // кожен процесор отримає n елементів

        // counts та displacements динамічно підлаштовані під кількість процесорів np
        int[] counts = new int[np];
        int[] displacements = new int[np];
        for (int i = 0; i < np; i++) {
            counts[i] = n;
            displacements[i] = i * n;
        }

        MPI.COMM_WORLD.scatterv(a, counts, displacements, MPI.INT, q, n, MPI.INT, 0);
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        MPI.Finalize();
    }
}
