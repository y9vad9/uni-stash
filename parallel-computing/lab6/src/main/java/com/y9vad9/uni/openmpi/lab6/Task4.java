package com.y9vad9.uni.openmpi.lab6;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/**
 * <h2>Завдання</h2>
 *
 * Напишіть програму для пересилання масиву чисел iз
 * процесора номер 2 іншим процесорам групи. Причому
 * процесор 0 повинен отримати одне число, процесор 1 – два
 * числа, процесор 2 – чотири числа i так далi. Протестуйте
 * програму на 4, 8, 12 процесорах.
 */
public class Task4 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        // Процесор 2 готує масив для розсилки
        int totalNumbers = 0;
        int[] sendCounts = new int[size];
        int[] displs = new int[size];

        for (int i = 0; i < size; i++) {
            // логіка: 0 -> 1 число, 1 -> 2 числа, 2 -> 4 числа, 3 -> 8 і т.д.
            sendCounts[i] = (int) Math.pow(2, i);
            totalNumbers += sendCounts[i];
        }

        // зсуви
        displs[0] = 0;
        for (int i = 1; i < size; i++) {
            displs[i] = displs[i - 1] + sendCounts[i - 1];
        }

        int[] sendBuffer = null;
        if (rank == 2) {
            sendBuffer = new int[totalNumbers];
            for (int i = 0; i < totalNumbers; i++) {
                sendBuffer[i] = 100 + i; // заповнення унікальними числами
            }
            System.out.println("PROCESS 2: sendBuffer = " + Arrays.toString(sendBuffer));
        }

        // Буфер для отримання числа(ів) у кожного процесора
        int[] recvBuffer = new int[sendCounts[rank]];

        // Використовуємо Scatterv з root = 2
        MPI.COMM_WORLD.scatterv(sendBuffer, sendCounts, displs, MPI.INT,
                recvBuffer, recvBuffer.length, MPI.INT,
                2);

        System.out.println("PROCESS " + rank + ": received = " + Arrays.toString(recvBuffer));

        MPI.Finalize();
    }
}
