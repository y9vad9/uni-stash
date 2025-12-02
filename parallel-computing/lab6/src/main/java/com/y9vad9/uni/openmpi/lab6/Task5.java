package com.y9vad9.uni.openmpi.lab6;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;

/**
 * <h2>Завдання</h2>
 *
 * Напишіть програму для пересилання масиву чисел iз
 * процесора номер 2 іншим процесорам групи. Причому
 * процесор 0 повинен отримати одне число, процесор 1 – два
 * числа, процесор 2 – чотири числа i так далi. Протестуйте
 * програму на 4, 8, 12 процесорах.
 */
public class Task5 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        Random rnd = new Random();
        int myValue = rnd.nextInt(100); // кожен процесор має своє випадкове значення
        System.out.println("PROCESS " + rank + ": myValue = " + myValue);

        // Процесор 2 буде отримувачем мінімального значення
        int[] minValue = new int[1];
        int[] localValue = new int[]{myValue};

        // Використовуємо reduce з операцією MIN та root = 2
        MPI.COMM_WORLD.reduce(localValue, minValue, 1, MPI.INT, MPI.MIN, 2);

        if (rank == 2) {
            System.out.println("PROCESS 2: minimum value = " + minValue[0]);
        }

        MPI.Finalize();
    }
}
