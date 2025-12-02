package com.y9vad9.uni.openmpi.lab6;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;
import java.util.Arrays;

/**
 * <h2>Завдання</h2>
 *
 * Напишiть програму для обчислення середнього значення
 * великого масиву чисел, використовуючи колективнi команди.
 */
public class Task6 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        // Розмір великого масиву
        int totalSize = 1000;

        // Визначаємо локальний розмір для кожного процесора
        int localSize = totalSize / size;
        int[] localArray = new int[localSize];
        Random rnd = new Random(rank); // різні зерна для різних процесорів

        // Заповнюємо локальний масив випадковими числами
        for (int i = 0; i < localSize; i++) {
            localArray[i] = rnd.nextInt(100); // числа від 0 до 99
        }

        // Обчислюємо локальну суму
        int localSum = Arrays.stream(localArray).sum();

        // Масив для збереження глобальної суми
        int[] globalSum = new int[1];

        // Використовуємо колективну команду reduce для обчислення суми на процесорі 0
        MPI.COMM_WORLD.reduce(new int[]{localSum}, globalSum, 1, MPI.INT, MPI.SUM, 0);

        // Процесор 0 обчислює середнє
        if (rank == 0) {
            double average = (double) globalSum[0] / totalSize;
            System.out.println("Average value = " + average);
        }

        MPI.Finalize();
    }
}
