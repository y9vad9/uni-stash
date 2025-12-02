package com.y9vad9.uni.openmpi.lab7;

import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Arrays;

/**
 * <h2>Завдання/h2>
 *
 * Напишiть програму для пересилання масиву об’єктiв iз
 * процесора номер 3 iншим процесорам групи за допомогою
 * процедури bcastObject. Протестуйте програму на 4, 8, 12
 * процесорах.
 */
public class Task2 {
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        Object[] data = null;

        if (rank == 3) {
            data = new Object[]{"Книга", "Ручка", "Олівець"};
            System.out.println("Процесор 3: відправляю " + Arrays.toString(data));
        }

        // Broadcast від процесора 3 усім процесорам, включно з собою
        Object[] received = (Object[]) Transport.bcastObject(data, 3);

        if (rank != 3) {
            System.out.println("Процесор " + rank + ": отримав " + Arrays.toString(received));
        }

        MPI.Finalize();
    }
}
