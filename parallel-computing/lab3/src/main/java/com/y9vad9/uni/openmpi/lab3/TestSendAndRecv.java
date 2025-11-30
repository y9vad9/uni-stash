package com.y9vad9.uni.openmpi.lab3;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;

public class TestSendAndRecv {
    public static void main(String[] args) throws MPIException {

        // Ініціалізація MPI
        MPI.Init(args);

        // Визначення номера процесора
        int myRank = MPI.COMM_WORLD.getRank();

        // Визначення кількості процесорів у групі
        int np = MPI.COMM_WORLD.getSize();

        int n = 5;              // Кількість елементів у повідомленні
        int[] a = new int[6];   // Масив для повідомлень

        if (myRank == 0) {
            // Процесор 0 отримує повідомлення від інших процесорів
            for (int j = 1; j < np; j++) {
                MPI.COMM_WORLD.recv(a, n, MPI.INT, j, 6); // Отримання повідомлення
                System.out.println("Дані отримані від процесора " + a[0] + ":");

                for (int i = 1; i < n; i++) {
                    System.out.println("a[" + i + "]= " + a[i]);
                }

                System.out.println();
            }
        } else {
            // Інші процесори формують повідомлення
            a[0] = myRank; // Адресат повідомлення

            Random rand = new Random();
            for (int i = 1; i < n; i++) {
                a[i] = rand.nextInt(); // Запис випадкового числа
                System.out.println("a(" + myRank + ")[" + i + "]= " + a[i]);
            }

            MPI.COMM_WORLD.send(a, n, MPI.INT, 0, 6); // Відправка повідомлення процесору 0
        }

        // Завершення паралельної частини
        MPI.Finalize();
    }
}
