package com.y9vad9.uni.openmpi.lab3;

import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

import java.nio.IntBuffer;
import java.util.Random;

public class DeadlockSolution {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int n = 200_000;

        // Звичайні масиви
        int[] a = new int[n];
        int[] b = new int[n];

        // Direct-буфери для iSend/iRecv
        IntBuffer bufA = MPI.newIntBuffer(n);
        IntBuffer bufB = MPI.newIntBuffer(n);

        if (rank == 0) {
            Random rnd = new Random();
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(100);

            // копіюємо масив у direct-буфер
            bufA.clear();
            bufA.put(a);
            bufA.flip();

            // неблокувальний send
            Request sendReq =
                MPI.COMM_WORLD.iSend(bufA, n, MPI.INT, 1, 0);

            // блокувальний recv у звичайний масив
            MPI.COMM_WORLD.recv(b, n, MPI.INT, 1, 1);

            sendReq.waitFor();

            System.out.println("Rank 0: b[0] = " + b[0]);
        }

        if (rank == 1) {
            Random rnd = new Random();
            for (int i = 0; i < n; i++) b[i] = rnd.nextInt(100);

            bufB.clear();
            bufB.put(b);
            bufB.flip();

            Request sendReq =
                MPI.COMM_WORLD.iSend(bufB, n, MPI.INT, 0, 1);

            MPI.COMM_WORLD.recv(a, n, MPI.INT, 0, 0);

            sendReq.waitFor();

            System.out.println("Rank 1: a[0] = " + a[0]);
        }

        MPI.Finalize();
    }
}
