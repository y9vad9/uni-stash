package com.mathpar.students.OLD.llp2.student.lyanok;

import java.util.Random;
import mpi.*;

/**
 *
 * @author lyanochka
 */
public class zzzzz {

    public static void S_R(String[] args, int myrank, int n, int m, int A[][]) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        if (myrank == 0) {
            //выполняется на нулевом процессоре
            int B[] = new int[n * m / 4];
//            int m = 2 * n;
            int k = 0;
            int h = 0;
            //рассылка частей матрицы А
            for (int i = m / 4; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    B[k] = A[j][i];
                    k++;
                }
                if (k == B.length) {
                    k = 0;
                    h++;
                    //!!!! MPI.COMM_WORLD.Send(B, 0, n * m / 4, //!!!! MPI.INT, h, 2000);
                }
            }
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[n * m / 4];
            for (int i = 1; i < n; i++) {
                //!!!! MPI.COMM_WORLD.Recv(massiv, 0, n * m / 4, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
                for (int i1 = 0; i1 < m / 4; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * m / 4] = massiv[j + i1 * n];
                    }
                }
            }
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < m; j++) {
////                    System.out.print(C[i][j] + "   ");
//                }
////                System.out.println("");
//            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
//                  System.out.print((C[i][j]-A[i][j])+"   ");
                    if (C[i][j] - A[i][j] != 0) {
//                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
//                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
//                System.out.println("");
            }

        } else {
            int massiv[] = new int[n * m / 4];
            //!!!! MPI.COMM_WORLD.Recv(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
            int B[][] = new int[n][m / 4];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
////            System.out.println("poluchennyy massiv na prosessore " + myrank);
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < 2; j++) {
////                    System.out.print(B[i][j] + "  ");
//                }
////                System.out.println("");
//            }
            //!!!! MPI.COMM_WORLD.Send(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000);
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.print(" \t S_R=  " + time);
        }
    }

    public static void S_I(String[] args, int myrank, int n, int m, int A[][]) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        if (myrank == 0) {
            int B[] = new int[n * m / 4];
//            int m = 2 * n;
//            выполняется на нулевом процессоре
            int k = 0;
            int h = 0;
//            рассылка частей матрицы А
            for (int i = m / 4; i < m; i++) {
                for (int j = 0; j < n; j++) {
//                    System.out.println(" i = "+i +  " j = "+ j+" k= "+k+" m= "+m + " n= "+n +" B="+B.length+" A="+A.length+" ++"+A[0].length);
                    B[k] = A[j][i];
                    k++;
                }
                if (k == B.length) {
                    k = 0;
                    h++;
                    //!!!! MPI.COMM_WORLD.Send(B, 0, n * m / 4, //!!!! MPI.INT, h, 2000);
                }
            }
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[n * m / 4];
            for (int i = 1; i < n; i++) {
                Request rr;

            //!!!!    rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, n * m / 4, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
            //!!!!    rr.Wait();
                for (int i1 = 0; i1 < m / 4; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * m / 4] = massiv[j + i1 * n];
                    }
                }
            }
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < m; j++) {
////                    System.out.print(C[i][j] + "   ");
//                }
////                System.out.println("");
//            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
//                  System.out.print((C[i][j]-A[i][j])+"   ");
                    if (C[i][j] - A[i][j] != 0) {
//                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
//                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
//                System.out.println("");
            }

        } else {
            int massiv[] = new int[n * m / 4];
            Request rr;
         //!!!!   rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
         //!!!!   rr.Wait();
            int B[][] = new int[n][m / 4];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
//            System.out.println("poluchennyy massiv na prosessore " + myrank);
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < m/4; j++) {
////                    System.out.print(B[i][j] + "  ");
//                }
////                System.out.println("");
//            }
            //!!!! MPI.COMM_WORLD.Send(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000);
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.print(" \t S_I=  " + time);
        }
    }

    public static void I_I(String[] args, int myrank, int n, int m, int A[][]) throws MPIException {

        long time = 0;
        time = System.currentTimeMillis();

        if (myrank == 0) {
//            выполняется на нулевом процессоре

            int B[] = new int[n * m / 4];
//            int m = 2 * n;
            int k = 0;
            int h = 0;
//            рассылка частей матрицы А
            for (int i = m / 4; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    B[k] = A[j][i];
                    k++;
                }
                if (k == B.length) {
                    k = 0;
                    h++;
                    //!!!! MPI.COMM_WORLD.Isend(B, 0, n * m / 4, //!!!! MPI.INT, h, 2000);
                }
            }
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[n * m / 4];
            for (int i = 1; i < n; i++) {
                Request rr;

            //!!!!    rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, n * m / 4, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
             //!!!!   rr.Wait();
                for (int i1 = 0; i1 < m / 4; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * m / 4] = massiv[j + i1 * n];
                    }
                }
            }
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < m; j++) {
////                    System.out.print(C[i][j] + "   ");
//                }
////                System.out.println("");
//            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
//                  System.out.print((C[i][j]-A[i][j])+"   ");
                    if (C[i][j] - A[i][j] != 0) {
//                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
//                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
//                System.out.println("");
            }

        } else {
            int massiv[] = new int[n * m / 4];
            Request rr;
         //!!!!   rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
         //!!!!   rr.Wait();
            int B[][] = new int[n][m / 4];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
//            System.out.println("poluchennyy massiv na prosessore " + myrank);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m / 4; j++) {
//                    System.out.print(B[i][j] + "  ");
                }
//                System.out.println("");
            }
            //!!!! MPI.COMM_WORLD.Isend(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000);
        }

        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.print(" \t I_I=  " + time);
        }
    }

    public static void I_R(String[] args, int myrank, int n, int m, int A[][]) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        if (myrank == 0) {
//            выполняется на нулевом процессоре
            int B[] = new int[n * m / 4];
//            int m = 2 * n;
            int k = 0;
            int h = 0;
//            рассылка частей матрицы А
            for (int i = m / 4; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    B[k] = A[j][i];
                    k++;
                }
                if (k == B.length) {
                    k = 0;
                    h++;
                    //!!!! MPI.COMM_WORLD.Isend(B, 0, n * m / 4, //!!!! MPI.INT, h, 2000);
                }
            }
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[n * m / 4];
            for (int i = 1; i < n; i++) {
                //!!!! MPI.COMM_WORLD.Recv(massiv, 0, n * m / 4, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
                for (int i1 = 0; i1 < m / 4; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * 2] = massiv[j + i1 * n];
                    }
                }
            }
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < m; j++) {
////                    System.out.print(C[i][j] + "   ");
//                }
////                System.out.println("");
//            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
//                  System.out.print((C[i][j]-A[i][j])+"   ");
                    if (C[i][j] - A[i][j] != 0) {
//                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
//                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
//                System.out.println("");
            }

        } else {
            int massiv[] = new int[n * m / 4];
            //!!!! MPI.COMM_WORLD.Recv(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
            int B[][] = new int[n][m / 4];
            for (int i = 0; i < m / 4; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
//            System.out.println("poluchennyy massiv na prosessore "+ myrank);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m / 4; j++) {
//                    System.out.print(B[i][j] + "  ");
                }
//                System.out.println("");
            }
            //!!!! MPI.COMM_WORLD.Isend(massiv, 0, n * m / 4, //!!!! MPI.INT, 0, 2000);
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.print(" \t I_R=  " + time);
        }
    }

    public static void S_G(String[] args, int myrank, int n, int m, int A[][]) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();

        int np = n * m / 4;
//        int[][] a = new int[n][np];
        int[][] b = new int[n][m / 4];
        int[][] c = new int[n][np];
        int[] int1 = new int[n];
        int[] int2 = new int[n];


        if (myrank == 0) {
//            for (int j = 0; j < n; j++) {
//                for (int i = 0; i < np; i++) {
//                    A[j][i] = new Random().nextInt(100);
//                }
//            }
        }
        for (int i = 0; i < n; i++) {
            int1[i] = m / 4;
            int2[i] = i * m / 4;
        }

        for (int i = 0; i < n; i++) {
            //!!!! MPI.COMM_WORLD.Scatterv(A[i], 0, int1, int2, //!!!! MPI.INT, b[i], 0, b[0].length, //!!!! MPI.INT, 0);
            //!!!! MPI.COMM_WORLD.Gatherv(b[i], 0, b[0].length, //!!!! MPI.INT, c[i], 0, int1, int2, //!!!! MPI.INT, 0);
        }

        if (myrank == 0) {
            c:
            for (int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[0].length; j++) {
                    A[i][j] -= c[i][j];
                    if (A[i][j] != 0) {

                        break c;
                    }

                }

            }
        }





//        int[][] b = new int[4][2 * m];
//        int[][] c = new int[4][m/4];
//        for (int i = 0; i < n; i++) {
//            //!!!! MPI.COMM_WORLD.Scatterv(A[i], 0, new int[]{2, 2, 2, 2}, new int[]{0, 2, 4, 6}, //!!!! MPI.INT, c[i], 0, 2, //!!!! MPI.INT, 0);
//            //!!!! MPI.COMM_WORLD.Gatherv(c[i], 0, c[0].length, //!!!! MPI.INT, b[i], 0, new int[]{2, 2, 2, 2}, new int[]{0, 2, 4, 6}, //!!!! MPI.INT, 0);
//        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.print(" \t  S_G = " + time);
        }
    }

    public static void main(String[] args) throws MPIException {
         MPI.Init(args);
        int size =  MPI.COMM_WORLD.getSize();
        int myrank =  MPI.COMM_WORLD.getRank();
        int n = 4;
        int m = 1028 * n;
        int s = 2;
        int k = 1;

//        int B[] = new int[2 * n];
//        int C[][] = new int[n][m];
//        int massiv[] = new int[2 * n];
        //создание матрицы А
        if (myrank == 0) {
            System.out.println("\t S_R \t\t S_I \t\t I_I \t\t I_R \t\t S_G");

        }
        for (int i = 1; i < 10; i++) {
            int A[][] = new int[4][m];
//            System.out.println("mmmmmmmmmmmmmm = "+m);
            for (int t = 0; t < n; t++) {
                for (int j = 0; j < m; j++) {
                    A[t][j] = new Random().nextInt(100);
//                    System.out.print(A[i][j] + "   ");
                }
//                System.out.println("");
            }
            if (myrank == 0) {
                System.out.print(k);
            }
            //!!!! MPI.COMM_WORLD.Barrier();
            S_R(args, myrank, n, m, A);
            //!!!! MPI.COMM_WORLD.Barrier();
            S_I(args, myrank, n, m, A);
            //!!!! MPI.COMM_WORLD.Barrier();
            I_I(args, myrank, n, m, A);
            //!!!! MPI.COMM_WORLD.Barrier();
            I_R(args, myrank, n, m, A);
            //!!!! MPI.COMM_WORLD.Barrier();
            S_G(args, myrank, n, m, A);
            if (myrank == 0) {

                System.out.print("           " + "\t" + m * size);
                k++;
                System.out.println("");
                System.out.println("___________________________________________________________________________");
            }
            m = m * s;
        }
        //!!!! MPI.Finalize();
    }
}
