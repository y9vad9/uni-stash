package com.y9vad9.uni.openmpi.lab4;

import mpi.MPI;
import java.util.Arrays;
import java.util.Random;

public class Lab4 {
    public static void main(String[] args) throws Exception {
        // Ініціалізація MPI
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = 1;

        int[] a = new int[n];
        Random rnd = new Random();

        // Статуси одиниць
        String[] status = {
            "Готовий до роботи",
            "Потреба в тех. обслуговуванні",
            "Порушення в живленні",
            "Недостатня кількість матеріалів",
            "Критичний стан. Потрібна повна заміна"
        };

        // Відповідні команди для статусів
        String[] command = {
            "Продовжити роботу в штатному режимі",
            "Надіслати запит про обслуговування до M6",
            "Надіслати запит про діагностику до M6",
            "Переключитися на резервний канал зв'язку",
            "Надіслати запит про обслуговування до M6"
        };

        // Кожен процесор генерує свій статус
        for (int i = 0; i < n; i++) {
            if (myrank == 0) {
                a[i] = 0; // головний блок завжди готовий
            } else {
                a[i] = rnd.nextInt(status.length); // випадковий статус
            }
        }

        // Вивід локального статусу
        System.out.println("Unit " + myrank + ": |Status| " + Arrays.toString(a) + " " + status[a[0]]);

        int[] q = new int[n * np];

        // Збір статусів на процесорі 0
        MPI.COMM_WORLD.gather(a, n, MPI.INT, q, n, MPI.INT, 0);

        if (myrank == 0) {
            System.out.println("Unit " + myrank + ": |received statuses| " + Arrays.toString(q));
        }

        // Розсилка команд назад кожному процесору залежно від статусу
        MPI.COMM_WORLD.scatter(q, 1, MPI.INT, a, 1, MPI.INT, 0);

        // Вивід отриманої команди
        if (myrank != 0) {
            System.out.println("Unit " + myrank + ": |status received| " + Arrays.toString(a) +
                " " + status[a[0]] +
                " |command| " + command[a[0]]);
        }

        MPI.Finalize();
    }
}
