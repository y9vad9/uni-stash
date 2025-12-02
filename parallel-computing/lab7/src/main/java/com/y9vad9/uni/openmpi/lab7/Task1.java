package com.y9vad9.uni.openmpi.lab7;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/**
 * <h2>Завдання/h2>
 *
 * Напишiть програму для пересилання масиву об’єктiв iз
 * процесора номер 1 iншим процесорам групи за допомогою
 * процедур sendArrayOfObjects i recvArrayOfObjects.
 * Протестуйте програму на 4, 8, 12 процесорах.
 */
public class Task1 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        // Масив об'єктів, який буде відправлено
        Object[] data;

        if (rank == 1) {
            data = new Object[]{ "Яблуко", "Банан", "Вишня" };
            System.out.println("Процесор 1: відправляю " + Arrays.toString(data));
            // Відправка всім іншим
            for (int dest = 0; dest < size; dest++) {
                if (dest != 1) {
                    Transport.sendObjects(data, dest, 100);
                }
            }
        } else {
            // Кожен інший процес отримує
            Object[] received = Transport.recvObjects(3, 1, 100);
            System.out.println("Процесор " + rank + ": отримав " + Arrays.toString(received));
        }

        MPI.Finalize();
    }
}
