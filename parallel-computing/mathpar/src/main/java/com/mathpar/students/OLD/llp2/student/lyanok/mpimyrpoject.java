package com.mathpar.students.OLD.llp2.student.lyanok;

import java.util.Random;
import mpi.*;

/**
 *
 * @author lyanochka
 */
//mpirun C java -cp /home/lyanochka/NetBeansProjects/mpi/build/classes:$CLASSPATH //!!!! MPI.main
//mpirun C java -cp /home/lyanochka/NetBeansProjects/programs/mpi/build/classes:$CLASSPATH //!!!! MPI.main
public class mpimyrpoject {

    public static void SendReceive(String[] args, int myrank, int n, int[][] a, int[][] b, int[][] c) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < n; j++) {
                    //!!!! MPI.COMM_WORLD.Send(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                }
                //  System.out.println(Arrays.toString(a[i]));
                // System.out.println("Proc num " + myrank + " Массив a отправлен ");
            }
        }
        for (int i = 0; i < c.length; i++) {
            //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
            //System.out.print(Arrays.toString(c[i]));
            // System.out.println("Proc num "+ myrank + " Массив temp"+i+" принят");
        }
        for (int i = 0; i < c.length; i++) {
            //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, i);
            //System.out.print(Arrays.toString(c[i]));
            //  System.out.println("Proc num " + myrank + " Массив temp отправлен ");
        }
        if (myrank == 0) {
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < n; j++) {
                    //!!!! MPI.COMM_WORLD.Recv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
                }
                //  System.out.println(Arrays.toString(b[i]));
                //  System.out.println("Proc num " + myrank + " Массив b принят ");
            }
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("  time  " + time);
        }
        if (myrank == 0) {
            // time = System.currentTimeMillis() - time;
            //  System.out.println("time = " + time);
            check:
            {
                for (int i = 0; i < b.length; i++) {
                    for (int j = 0; j < b[i].length; j++) {
                        a[i][j] = a[i][j] - b[i][j];
                        if (a[i][j] != 0) {
                            System.out.println("ошибка!!!");
                            break check;
                        }
                    }
                }
                //  System.out.println("проверка прошла благополучно!!!");
            }
        }
    }

    public static void IsendReceive(String[] args, int myrank, int n, int[][] a, int[][] b, int[][] c) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < n; j++) {
                    //!!!! MPI.COMM_WORLD.Isend(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                }
                //  System.out.println(Arrays.toString(a[i]));
                // System.out.println("Proc num " + myrank + " Массив a отправлен ");
            }
        }
        for (int i = 0; i < c.length; i++) {
            //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
            //  System.out.print(Arrays.toString(c[i]));
            // System.out.println("Proc num " + myrank + " Массив temp" + i + " принят");
        }
        for (int i = 0; i < c.length; i++) {
            //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, i);
            // System.out.print(Arrays.toString(c[i]));
            // System.out.println("Proc num " + myrank + " Массив temp отправлен ");
        }
        if (myrank == 0) {
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < n; j++) {
                    //!!!! MPI.COMM_WORLD.Recv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
                }
                // System.out.println(Arrays.toString(b[i]));
                // System.out.println("Proc num " + myrank + " Массив b принят ");
            }
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("  time = " + time);
        }
        if (myrank == 0) {
            // time = System.currentTimeMillis() - time;
            //  System.out.println("time = " + time);
            check:
            {
                for (int i = 0; i < b.length; i++) {
                    for (int j = 0; j < b[i].length; j++) {
                        a[i][j] = a[i][j] - b[i][j];
                        if (a[i][j] != 0) {
                            System.out.println("ошибка!!!");
                            break check;
                        }
                    }
                }
                // System.out.println("проверка прошла благополучно!!!");
            }
        }
    }

    public static void SendIreceive(String[] args, int myrank, int n, int[][] a, int[][] b, int[][] c) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < n; j++) {
                    //!!!! MPI.COMM_WORLD.Send(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                }
                //  System.out.println(Arrays.toString(a[i]));
                //  System.out.println("Proc num " + myrank + " Массив a отправлен ");
            }
        }
        for (int i = 0; i < c.length; i++) {
//!!!!            Request Irecv = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
     //!!!!       while (Irecv.Test() == null) {
                // System.out.print(Arrays.toString(c[i]));
                //  System.out.println("Proc num " + myrank + " Массив temp" + i + " принят");
     //!!!!       }
        }
        for (int i = 0; i < c.length; i++) {
            //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, i);
            // System.out.print(Arrays.toString(c[i]));
            // System.out.println("Proc num " + myrank + " Массив temp отправлен ");
        }
        if (myrank == 0) {
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < n; j++) {
                  //!!!!  Request Irecv = //!!!! MPI.COMM_WORLD.Irecv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
                 //!!!!   while (Irecv.Test() == null) {
                        //   System.out.print(Arrays.toString(c[i]));
                        //  System.out.println("Proc num " + myrank + " Массив temp" + i + " принят");
              //!!!!      }
                }
            }
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("  time  " + time);
        }
        if (myrank == 0) {
            // time = System.currentTimeMillis() - time;
            //  System.out.println("time = " + time);
            check:
            {
                for (int i = 0; i < b.length; i++) {
                    for (int j = 0; j < b[i].length; j++) {
                        a[i][j] = a[i][j] - b[i][j];
                        if (a[i][j] != 0) {
                            System.out.println("ошибка!!!");
                            break check;
                        }
                    }
                }
                // System.out.println("проверка прошла благополучно!!!");
            }
        }
    }

    public static void IsendIreceive(String[] args, int myrank, int n, int[][] a, int[][] b, int[][] c) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < n; j++) {
                    //!!!! MPI.COMM_WORLD.Isend(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                }
                // System.out.println(Arrays.toString(a[i]));
                // System.out.println("Proc num " + myrank + " Массив a отправлен ");
            }
        }
        for (int i = 0; i < c.length; i++) {
        //!!!!    Request Irecv = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
         //!!!!   while (Irecv.Test() == null) {
                //  System.out.print(Arrays.toString(c[i]));
                // System.out.println("Proc num " + myrank + " Массив temp" + i + " принят");
         //!!!!   }
        }
        for (int i = 0; i < c.length; i++) {
            //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, i);
            // System.out.print(Arrays.toString(c[i]));
            // System.out.println("Proc num " + myrank + " Массив temp отправлен ");
        }
        if (myrank == 0) {
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < n; j++) {
                //!!!!    Request Irecv = //!!!! MPI.COMM_WORLD.Irecv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
                //!!!!    while (Irecv.Test() == null) {
                        //  System.out.print(Arrays.toString(c[i]));
                        //  System.out.println("Proc num " + myrank + " Массив temp" + i + " принят");
                //!!!!    }
                }
                // System.out.println(Arrays.toString(b[i]));
                // System.out.println("Proc num " + myrank + " Массив b принят ");
            }
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println("  time = " + time);
        }
        if (myrank == 0) {
            // time = System.currentTimeMillis() - time;
            //  System.out.println("time = " + time);
            check:
            {
                for (int i = 0; i < b.length; i++) {
                    for (int j = 0; j < b[i].length; j++) {
                        a[i][j] = a[i][j] - b[i][j];
                        if (a[i][j] != 0) {
                            System.out.println("ошибка!!!");
                            break check;
                        }
                    }
                }
                // System.out.println("проверка прошла благополучно!!!");
            }
        }
    }

    public static void ScattervGatherv(String[] args, int n, int myrank, int[][] a, int[][] b, int[][] c) throws MPIException {
        long time = 0;
        time = System.currentTimeMillis();
        int[] a1 = new int[n];
        int[] b1 = new int[n];
        if (myrank == 0) {
            for (int i = 0; i < a1.length; i++) {
                a1[i] = 2;
                b1[i] = 2 * i;
            }
        }
        for (int i = 0; i < a.length; i++) {
            //!!!! MPI.COMM_WORLD.Scatterv(a[i], 0, a1, b1, //!!!! MPI.INT, c[i], 0, 2, //!!!! MPI.INT, 0);
            //!!!! MPI.COMM_WORLD.Gatherv(c[i], 0, c[0].length, //!!!! MPI.INT, b[i], 0, a1, b1, //!!!! MPI.INT, 0);
        }
        time = System.currentTimeMillis() - time;
        if (myrank == 0) {
            System.out.println(" time = " + time);
        }
        if (myrank == 0) {
            check:
            {
                for (int i = 0; i < b.length; i++) {
                    for (int j = 0; j < b[i].length; j++) {
                        a[i][j] = a[i][j] - b[i][j];
                        if (a[i][j] != 0) {
                            System.out.println("ошибка!!!");
                            break check;
                        }
                    }
                }
                //  System.out.println("проверка прошла благополучно!!!");
            }
        }
    }

    public static long time(String[] args, int myrank, int n, int[][] a, int[][] b, int[][] c) throws MPIException {
        long time1 = 0;
        long time2 = 0;
        long time3 = 0;
        long time4 = 0;
        if (myrank == 0) {
            time1 = System.currentTimeMillis();
        }
        if (myrank == 1) {
            time2 = System.currentTimeMillis();
        }
        if (myrank == 2) {
            time3 = System.currentTimeMillis();
        }
        if (myrank == 3) {
            time4 = System.currentTimeMillis();
        }
        IsendIreceive(args, myrank, n, a, b, c);
        if (myrank == 0) {
            time1 = System.currentTimeMillis() - time1;
            System.out.println("time1 " + time1);
        }
        if (myrank == 1) {
            time2 = System.currentTimeMillis() - time2;
            System.out.println("time2 " + time2);
        }
        if (myrank == 2) {
            time3 = System.currentTimeMillis() - time3;
            System.out.println("time3 " + time3);
        }
        if (myrank == 3) {
            time4 = System.currentTimeMillis() - time4;
            System.out.println("time4 " + time4);
        }
        //  System.out.println("time = "+ time);
        //   return time;}
        return time1;
    }

    public static int[][] getmass(int a, int b) {
        int[][] res = new int[a][b];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] = new Random().nextInt(100);
            }
        }
        return res;
    }

    public static void main(String[] args) throws MPIException {
         MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int n =  MPI.COMM_WORLD.getSize();
        int[][] a = new int[4][2 * n];
        int[][] b = new int[4][2 * n];
        int[][] c = new int[4][2];
        long time = 0;
        if (myrank == 0) {
            //a = new int[4][2 * n];
            //  b = new int[4][2 * n];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = new Random().nextInt(100);
                }
            }
            //  time = System.currentTimeMillis();
        }
//       time = time(args, myrank, n, a, b, c);
//        System.out.println(time);
        for (int i = 2; i < 128; i = i * 4) {


            if (myrank == 0) {
                System.out.print("IsendIreceive ");
            }
            // //!!!! MPI.COMM_WORLD.Barrier();
            IsendIreceive(args, myrank, n, a, b, c);
            //!!!! MPI.COMM_WORLD.Barrier();
            if (myrank == 0) {
                System.out.print("SendIreceive ");
            }
            SendIreceive(args, myrank, n, a, b, c);
            //!!!! MPI.COMM_WORLD.Barrier();
            if (myrank == 0) {
                System.out.print("IsendReceive ");
            }
            IsendReceive(args, myrank, n, a, b, c);
            //!!!! MPI.COMM_WORLD.Barrier();
            if (myrank == 0) {
                System.out.print("SendReceive ");
            }
            SendReceive(args, myrank, n, a, b, c);
            //!!!! MPI.COMM_WORLD.Barrier();
            if (myrank == 0) {
                System.out.print("ScattervGatherv [4]*[" + a[0].length + "]");
            }
            ScattervGatherv(args, n, myrank, a, b, c);
            a = getmass(4, 2 * i * n);
            b = getmass(4, 2 * i * n);
            c = new int[4][2];


        }
//        if (myrank == 0) {
//            time = System.currentTimeMillis() - time;
//            System.out.println("time = " + time);
//            check:
//            {
//                for (int i = 0; i < b.length; i++) {
//                    for (int j = 0; j < b[i].length; j++) {
//                        a[i][j] = a[i][j] - b[i][j];
//                        if (a[i][j] != 0) {
//                            System.out.println("ошибка!!!");
//                            break check;
//                        }
//                    }
//                }
//                System.out.println("проверка прошла благополучно!!!");
//            }
//        }
        //!!!! MPI.Finalize();
    }
}
