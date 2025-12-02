package com.y9vad9.uni.openmpi.lab6;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/**
 * <h2>Завдання</h2>
 *
 * Напишіть програму для пересилання масиву чисел з процесора
 * номер 2 іншим процесорам групи. Протестуйте програму на 4,
 * 8, 12 процесорах.
 */
public class Task1 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        int n = 5;                 // розмір масиву
        int[] data = new int[n];  // масив для передачі/отримання

        if (rank == 2) {
            for (int i = 0; i < n; i++) data[i] = 100 + i;
            System.out.println("Rank 2: відправляю " + Arrays.toString(data));
        }

        // Всі процеси беруть участь у колективній передачі
        MPI.COMM_WORLD.bcast(data, n, MPI.INT, 2);

        if (rank != 2) {
            System.out.println("Rank " + rank + ": отримав " + Arrays.toString(data));
        }

        MPI.Finalize();
    }
}
