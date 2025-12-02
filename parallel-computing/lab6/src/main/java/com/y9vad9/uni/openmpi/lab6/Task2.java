package com.y9vad9.uni.openmpi.lab6;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/**
 * <h2>Завдання</h2>
 *
 * Напишiть програму для збору масиву чисел з усiх процесорiв
 * на процесорi номер 1. Протестуйте програму на 4, 8, 12
 * процесорах.
 */
public class Task2 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        // розмір локального масиву кожного процесора
        int n = 3;

        // локальний масив процесора
        int[] local = new int[n];
        for (int i = 0; i < n; i++) {
            local[i] = rank * 10 + i;
        }

        System.out.println("rank " + rank + ": local = " + Arrays.toString(local));

        // масив-наприймач лише у процесора 1
        int[] collected = null;
        if (rank == 1) {
            collected = new int[n * size];
        }

        // збираємо дані на процесор 1
        MPI.COMM_WORLD.gather(
            local, n, MPI.INT,
            collected, n, MPI.INT,
            1   // кореневий процес
        );

        // виводимо результат на процесорі 1
        if (rank == 1) {
            System.out.println("rank 1: collected = " + Arrays.toString(collected));
        }

        MPI.Finalize();
    }
}
