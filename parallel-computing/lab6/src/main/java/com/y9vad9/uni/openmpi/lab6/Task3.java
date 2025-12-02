package com.y9vad9.uni.openmpi.lab6;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/**
 * <h2>Завдання</h2>
 *
 * Напишiть програму для збору масиву чисел з усiх процесорiв
 * на процесорi номер 3. Причому процесор 0 пересилає п’ять
 * чисел, процесор 1 – десять чисел, процесор 2 – п’ятнадцять
 * чисел i так далi. Протестуйте програму на 4, 8, 12 процесорах.
 */
public class Task3 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        // Кожен процес формує масив:
        // rank 0 -> 5 чисел
        // rank 1 -> 10 чисел
        // rank 2 -> 15 чисел
        // ...
        int n = (rank + 1) * 5;
        int[] local = new int[n];
        for (int i = 0; i < n; i++) local[i] = rank * 100 + i;

        System.out.println("rank = " + rank + " : local = " + Arrays.toString(local));

        // ---------- Підготовка до Gatherv ----------
        int[] recvCounts = null;
        int[] displs = null;

        // Лише процес 3 готує таблиці
        if (rank == 3) {
            recvCounts = new int[size];
            displs = new int[size];

            for (int i = 0; i < size; i++) {
                recvCounts[i] = (i + 1) * 5;
            }

            // обчислюємо зсуви
            displs[0] = 0;
            for (int i = 1; i < size; i++) {
                displs[i] = displs[i - 1] + recvCounts[i - 1];
            }
        }

        // Загальний розмір приймача на процесі 3
        int total = 0;
        if (rank == 3) {
            for (int c : recvCounts) total += c;
        }

        int[] gathered = (rank == 3) ? new int[total] : null;

        // ---------- Виконання збору ----------
        MPI.COMM_WORLD.gatherv(
            local, n, MPI.INT,
            gathered, recvCounts, displs, MPI.INT,
            3
        );

        // ---------- Результат ----------
        if (rank == 3) {
            System.out.println("PROCESS 3: gathered = " + Arrays.toString(gathered));
        }

        MPI.Finalize();
    }
}