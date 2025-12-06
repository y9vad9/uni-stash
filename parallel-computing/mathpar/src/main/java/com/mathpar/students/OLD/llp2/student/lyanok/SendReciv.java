package com.mathpar.students.OLD.llp2.student.lyanok;

import java.util.Random;
import mpi.*;
/**
 *
 * @author lyana
 */
public class SendReciv {

    static public void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int n = 4;
        int m = 8;
        if (myrank == 0) {
            //выполняется на нулевом процессоре
            int A[][] = new int[n][m];
            //создание матрицы А
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    A[i][j] = new Random().nextInt(10);
                    System.out.print(A[i][j] + "   ");
                }
                System.out.println("");
            }
            int B[] = new int[2 * n];
            int k = 0;
            int h = 0;
            //рассылка частей матрицы А
            for (int i = 2; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    B[k] = A[j][i];
                    k++;
                }
                if (k == 2 * n) {
                    k = 0;
                    h++;
                    //!!!! MPI.COMM_WORLD.Send(B, 0, 2 * n, //!!!! MPI.INT, h, 2000);
                }
            }
            //сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

//            int massiv[] = new int[2 * n];
//            for (int i = 1; i < n; i++) {
//                //!!!! MPI.COMM_WORLD.Recv(massiv, 0, 2 * n, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
//                for (int i1 = 0; i1 < 2; i1++) {
//                    for (int j = 0; j < n; j++) {
//                        C[j][i1 + i * 2] = massiv[j + i1 * n];
//                    }
//                }
//            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print(C[i][j] + "   ");
                }
                System.out.println("");
            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
//                  System.out.print((C[i][j]-A[i][j])+"   ");
                    if (C[i][j] - A[i][j] != 0) {
                        System.out.println("Массивы не равны");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
                        System.out.println("Массивы  равны, все прошло успешно");
                        break h;
                    }
                }
                System.out.println("");
            }

        }
        int massiv[] = new int[2 * n];
        //!!!! MPI.COMM_WORLD.Recv(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
        int B[][] = new int[n][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < n; j++) {
                B[j][i] = massiv[j + i * n];
            }
        }
        System.out.println("полученный массив на процессоре " + myrank);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(B[i][j] + "  ");
            }
            System.out.println("");
        }
        //!!!! MPI.COMM_WORLD.Send(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000);


        //!!!! MPI.Finalize();
    }
}
