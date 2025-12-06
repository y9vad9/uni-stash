package com.mathpar.students.OLD.llp2.student.lyanok;

import java.util.Arrays;
import java.util.Random;
import mpi.*;

public class TestSend1 {

    public static void main(String[] args) throws MPIException {
         MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int n = MPI.COMM_WORLD.getSize();
        int m = 8;
        int a[][] = new int[4][m];
        int b[][] = new int[4][2];
        int c[][] = new int[4][m];
        if (myrank == 0) {
            for (int j = 0; j < n; j++) {
                for (int i = 0; i < m; i++) {
                    a[j][i] = new Random().nextInt(100);
                }
            }
        }
//с 0-го отправляем массив на все процессоры
        if (myrank == 0) {
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < n; i++) {

                    //!!!! MPI.COMM_WORLD.Send(a[j], i * 2, 2, //!!!! MPI.INT, i, 3000);
                }
                System.out.println("Proc num " + myrank + " Массив отправлен");
                for (int k = 0; k < a.length; k++) {
                    System.out.println("Proc num= " + myrank + "  :  " + Arrays.toString(a[k]));
                }
            }

        }
//принимаем то, что послал 0-й процессор
        for (int i = 0; i < 4; i++) {
            //!!!! MPI.COMM_WORLD.Recv(b[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            System.out.println("Proc num= " + myrank + ": Массив принят");
            for (int k = 0; k < b.length; k++) {
                System.out.println("Proc num= " + myrank + "  :  " + Arrays.toString(b[k]));
            }

        }
        //!!!! MPI.COMM_WORLD.Barrier();
//отправляем массив на проверку
        for (int i = 0; i < 4; i++) {
            //!!!! MPI.COMM_WORLD.Send(b[i], 0, 2, //!!!! MPI.INT, 0, 2000);
            for (int k = 0; k < b.length; k++) {
                System.out.println("Proc num= " + myrank + " массив отправлен на проверку :  " + Arrays.toString(b[k]));
            }
        }
//прием на проверку
        if (myrank == 0) {
            for (int i = 0; i <  MPI.COMM_WORLD.getSize(); i++) {
                for (int j = 0; j < 4; j++) {
                    //!!!! MPI.COMM_WORLD.Recv(c[j], i * 2, 2, //!!!! MPI.INT, i, 2000);
                    System.out.println("Proc num =" + myrank + "    массив принят на проверку:   " + Arrays.toString(c[j]));
                }
            }
            //проверка
            h:
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    a[i][j] -= c[i][j];
                    if (a[i][j] != 0) {
                        System.out.println("Ошибка!! Массивы не равны");
                        break h;
                    }

                }
                if (i == a.length - 1) {
                    System.out.println("Все прошло успешно");
                }
            }
        }
        //!!!! MPI.Finalize();
    }
}
