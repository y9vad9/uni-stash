package com.mathpar.students.OLD.llp2.student.lyanok;

import java.util.Random;
import mpi.*;
/**
 *
 * @author lyanochka
 */
public class main {

    static public void SendResiv(String[] args) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int n = 4;
        int m = 8;
        if (myrank == 0) {
//            выполняется на нулевом процессоре
            int A[][] = new int[n][m];
//            создание матрицы А
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
//            рассылка частей матрицы А
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
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[2 * n];
            for (int i = 1; i < n; i++) {
                //!!!! MPI.COMM_WORLD.Recv(massiv, 0, 2 * n, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
                for (int i1 = 0; i1 < 2; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * 2] = massiv[j + i1 * n];
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print(C[i][j] + "   ");
                }
                System.out.println("");
            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print((C[i][j] - A[i][j]) + "   ");
                    if (C[i][j] - A[i][j] != 0) {
                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
                System.out.println("");
            }

        } else {
            int massiv[] = new int[2 * n];
            //!!!! MPI.COMM_WORLD.Recv(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
            int B[][] = new int[n][2];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
            System.out.println("poluchennyy massiv na prosessore " + myrank);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    System.out.print(B[i][j] + "  ");
                }
                System.out.println("");
            }
            //!!!! MPI.COMM_WORLD.Send(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000);
        }

        //!!!! MPI.Finalize();

        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("time SendResiv=  " + time);
        }
    }

    public static void SendIresiv(String[] args) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
         MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int n = 4;
        int m = 8;
        if (myrank == 0) {
//            выполняется на нулевом процессоре
            int A[][] = new int[n][m];
//            создание матрицы А
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
//            рассылка частей матрицы А
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
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[2 * n];
            for (int i = 1; i < n; i++) {
                Request rr;

            //!!!!    rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, 2 * n, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
            //!!!!    rr.Wait();
                for (int i1 = 0; i1 < 2; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * 2] = massiv[j + i1 * n];
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print(C[i][j] + "   ");
                }
                System.out.println("");
            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print((C[i][j] - A[i][j]) + "   ");
                    if (C[i][j] - A[i][j] != 0) {
                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
                System.out.println("");
            }

        } else {
            int massiv[] = new int[2 * n];
            Request rr;
         //!!!!   rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
          //!!!!  rr.Wait();
            int B[][] = new int[n][2];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
            System.out.println("poluchennyy massiv na prosessore " + myrank);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    System.out.print(B[i][j] + "  ");
                }
                System.out.println("");
            }
            //!!!! MPI.COMM_WORLD.Send(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000);
        }

        //!!!! MPI.Finalize();

        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("time SendIresiv " + time);
        }
    }

    public static void IsendIresiv(String[] args) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
         MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int n = 4;
        int m = 8;
        if (myrank == 0) {
//            выполняется на нулевом процессоре
            int A[][] = new int[n][m];
//            создание матрицы А
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
//            рассылка частей матрицы А
            for (int i = 2; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    B[k] = A[j][i];
                    k++;
                }
                if (k == 2 * n) {
                    k = 0;
                    h++;
                    //!!!! MPI.COMM_WORLD.Isend(B, 0, 2 * n, //!!!! MPI.INT, h, 2000);
                }
            }
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[2 * n];
            for (int i = 1; i < n; i++) {
                Request rr;

             //!!!!   rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, 2 * n, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
             //!!!!   rr.Wait();
                for (int i1 = 0; i1 < 2; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * 2] = massiv[j + i1 * n];
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print(C[i][j] + "   ");
                }
                System.out.println("");
            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print((C[i][j] - A[i][j]) + "   ");
                    if (C[i][j] - A[i][j] != 0) {
                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
                System.out.println("");
            }

        } else {
            int massiv[] = new int[2 * n];
            Request rr;
          //!!!!  rr = //!!!! MPI.COMM_WORLD.Irecv(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
         //!!!!   rr.Wait();
            int B[][] = new int[n][2];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
            System.out.println("poluchennyy massiv na prosessore " + myrank);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    System.out.print(B[i][j] + "  ");
                }
                System.out.println("");
            }
            //!!!! MPI.COMM_WORLD.Isend(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000);
        }

        //!!!! MPI.Finalize();

        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("time IsendIresiv " + time);
        }
    }

    public static void IsendResiv(String[] args) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();

        MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int n = 4;
        int m = 8;
        if (myrank == 0) {
//            выполняется на нулевом процессоре
            int A[][] = new int[n][m];
//            создание матрицы А
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
//            рассылка частей матрицы А
            for (int i = 2; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    B[k] = A[j][i];
                    k++;
                }
                if (k == 2 * n) {
                    k = 0;
                    h++;
                    //!!!! MPI.COMM_WORLD.Isend(B, 0, 2 * n, //!!!! MPI.INT, h, 2000);
                }
            }
//            сбор результатов
            int C[][] = new int[n][m];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    C[j][i] = A[j][i];
                }
            }

            int massiv[] = new int[2 * n];
            for (int i = 1; i < n; i++) {
                //!!!! MPI.COMM_WORLD.Recv(massiv, 0, 2 * n, //!!!! MPI.INT, i, 2000); // получили сообщение и записали его в массив massiv
                for (int i1 = 0; i1 < 2; i1++) {
                    for (int j = 0; j < n; j++) {
                        C[j][i1 + i * 2] = massiv[j + i1 * n];
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print(C[i][j] + "   ");
                }
                System.out.println("");
            }

            h:
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print((C[i][j] - A[i][j]) + "   ");
                    if (C[i][j] - A[i][j] != 0) {
                        System.out.println("Massivy ne ravny");
                        break h;
                    }
                    if (C[i][j] - A[i][j] == 0) {
                        System.out.println("Massivy ravny vse proshlo uspeshno");
                        break h;
                    }
                }
                System.out.println("");
            }

        } else {
            int massiv[] = new int[2 * n];
            //!!!! MPI.COMM_WORLD.Recv(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000); // получили сообщение и записали его в массив massiv
            int B[][] = new int[n][2];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    B[j][i] = massiv[j + i * n];
                }
            }
            System.out.println("poluchennyy massiv na prosessore " + myrank);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    System.out.print(B[i][j] + "  ");
                }
                System.out.println("");
            }
            //!!!! MPI.COMM_WORLD.Isend(massiv, 0, 2 * n, //!!!! MPI.INT, 0, 2000);
        }



        //!!!! MPI.Finalize();


        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("time IsendResiv " + time);
        }
    }

    public static void ScattervGatherv(String[] args) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
         MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int n =  MPI.COMM_WORLD.getSize();
        int[][] a = new int[4][2 * n];
        int[][] b = new int[4][2 * n];
        int[][] c = new int[4][2];
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = new Random().nextInt(100);
                }
            }
        }
        for (int i = 0; i < n; i++) {
            //!!!! MPI.COMM_WORLD.Scatterv(a[i], 0, new int[]{2, 2, 2, 2}, new int[]{0, 2, 4, 6}, //!!!! MPI.INT, c[i], 0, 2, //!!!! MPI.INT, 0);
            //!!!! MPI.COMM_WORLD.Gatherv(c[i], 0, c[0].length, //!!!! MPI.INT, b[i], 0, new int[]{2, 2, 2, 2}, new int[]{0, 2, 4, 6}, //!!!! MPI.INT, 0);
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("time  " + time);
        }

    }

    static public void main(String[] args) throws MPIException {
//      System.out.print("ScattervGatherv ");
//      ScattervGatherv(args);
//      //!!!! MPI.COMM_WORLD.Barrier();
//        System.out.println("hghmkhkhbo ");
        SendResiv(args);
//        IsendResiv(args);
//        SendIresiv(args);
//        IsendIresiv(args);
//        ScattervGatherv(args);
    }
//  public static int[][] getmass(int a,int b){
//        int[][] res = new int[a][b];
//        for (int i = 0; i < res.length; i++) {
//                for (int j = 0; j < res[i].length; j++) {
//                    res[i][j] = new Random().nextInt(100);
//                }
//            }
//        return res;
//    }
//    public static void main(String[] args) throws MPIException {
//        //!!!! MPI.Init(args);
//        int myrank = //!!!! MPI.COMM_WORLD.getRank();
//        int n = //!!!! MPI.COMM_WORLD.Size();
//        int[][] a = new int[4][2 * n];
//        int[][] b = new int[4][2 * n];
//        int[][] c = new int[4][2];
//        long time = 0;
//        if (myrank == 0) {
//            //a = new int[4][2 * n];
//            //  b = new int[4][2 * n];
//            for (int i = 0; i < a.length; i++) {
//                for (int j = 0; j < a[i].length; j++) {
//                    a[i][j] = new Random().nextInt(100);
//                }
//            }
//            //  time = System.currentTimeMillis();
//        }
////       time = time(args, myrank, n, a, b, c);
////        System.out.println(time);
//        for (int i = 2; i < 128; i=i*4) {
//
//
//        if (myrank == 0) System.out.print("IsendIreceive ");
//       // //!!!! MPI.COMM_WORLD.Barrier();
//        IsendIreceive(args, myrank, n, a, b, c);
//        //!!!! MPI.COMM_WORLD.Barrier();
//        if (myrank == 0) System.out.print("SendIreceive ");
//        SendIreceive(args, myrank, n, a, b, c);
//        //!!!! MPI.COMM_WORLD.Barrier();
//        if (myrank == 0) System.out.print("IsendReceive ");
//        IsendResive(args, myrank, n, a, b, c);
//        //!!!! MPI.COMM_WORLD.Barrier();
//        if (myrank == 0) System.out.print("SendResive ");
//        SendResive(args, myrank, n, a, b, c);
//        //!!!! MPI.COMM_WORLD.Barrier();
//        if (myrank == 0) {
//            System.out.print("ScattervGatherv [4]*["+a[0].length+"]");
//        }
//        ScattervGatherv(args, n, myrank, a, b, c);
//                a = getmass(4, 2*i*n);
//                b = getmass(4, 2*i*n);
//                c = new int[4][2];
//
//
//        }
//    }
}
