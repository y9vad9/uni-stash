
package com.mathpar.students.OLD.llp2.student.pochtarkov;

import com.mathpar.students.OLD.llp2.student.message.ParallelDebug;

import java.util.Random;

import mpi.*;
//import sun.applet.Main;

//mpirun C java -cp /home/matveeva/NetBeansProjects/mpi/build/classes:$CLASSPATH //!!!! MPI.Test4
//mpirun C java -cp /home/julia/NetBeansProjects/programs/mpi/build/classes:$CLASSPATH //!!!! MPI.Test4

/**
 *
 * @author Julia
 */
public class NewClass {

    public static void SendReceive(int n1,ParallelDebug pd) {
        long t1 = System.currentTimeMillis();
        int myrank = 0;
        int n = 0;
        try {
            myrank =  MPI.COMM_WORLD.getRank();
            n =  MPI.COMM_WORLD.getSize();
            int[][] a = new int[n1][2 * n];
            int[][] c = new int[n1][2];
            int[][] b = new int[n1][2 * n];
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < 2 * n; j++) {
                        a[i][j] = new Random().nextInt(100);
                    }
                }
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Send(a[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                    }
                }
            }
            pd.paddEvent(myrank+" проц после выполнения команды Send", "near the end");
            pd.generateDebugLog();
            for (int i = 0; i < n1; i++) {
                //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            }
            for (int i = 0; i < n1; i++) {
                //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Recv(b[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                    }
                }
            }
            if (myrank == 0) {
                check:
                {
                    for (int i = 0; i < n1; i++) {
                        for (int j = 0; j < 2 * n; j++) {
                            a[i][j] = a[i][j] - b[i][j];
                            if (a[i][j] != 0) {
                                System.out.println("Ошибка!!!");
                                break check;
                            }
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
            }
        } catch (MPIException ex) {
          //  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void IsendReceive(int n1) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank = MPI.COMM_WORLD.getRank();
            int n =  MPI.COMM_WORLD.getSize();
            int[][] a = new int[n1][2 * n];
            int[][] c = new int[n1][2];
            int[][] b = new int[n1][2 * n];
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < 2 * n; j++) {
                        a[i][j] = new Random().nextInt(100);
                    }
                }
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Isend(a[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                    }
                }
            }
            for (int i = 0; i < n1; i++) {
                //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            }
            for (int i = 0; i < n1; i++) {
                //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Recv(b[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                    }
                }
            }
            if (myrank == 0) {
                check:
                {
                    for (int i = 0; i < n1; i++) {
                        for (int j = 0; j < 2 * n; j++) {
                            a[i][j] = a[i][j] - b[i][j];
                            if (a[i][j] != 0) {
                                System.out.println("Ошибка!!!");
                                break check;
                            }
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
            }
        } catch (MPIException ex) {
         //   Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void SendIreceive(int n1) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank =  MPI.COMM_WORLD.getRank();
            int n =  MPI.COMM_WORLD.getSize();
            int[][] a = new int[n1][2 * n];
            int[][] c = new int[n1][2];
            int[][] b = new int[n1][2 * n];
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < 2 * n; j++) {
                        a[i][j] = new Random().nextInt(100);
                    }
                }
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Send(a[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                    }
                }
            }
            Request s1 = null;
            for (int i = 0; i < n1; i++) {
             //!!!!   s1 = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            //!!!!    s1.Wait();
            }
            for (int i = 0; i < n1; i++) {
                //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                 //!!!!       s1 = //!!!! MPI.COMM_WORLD.Irecv(b[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                 //!!!!       s1.Wait();
                    }
                }
            }
            if (myrank == 0) {
                check:
                {
                    for (int i = 0; i < n1; i++) {
                        for (int j = 0; j < 2 * n; j++) {
                            a[i][j] = a[i][j] - b[i][j];
                            if (a[i][j] != 0) {
                                System.out.println("Ошибка!!!");
                                break check;
                            }
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
            }
        } catch (MPIException ex) {
          //  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void IsendIreceive(int n1) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank = MPI.COMM_WORLD.getRank();
            int n =  MPI.COMM_WORLD.getSize();
            int[][] a = new int[n1][2 * n];
            int[][] c = new int[n1][2];
            int[][] b = new int[n1][2 * n];
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < 2 * n; j++) {
                        a[i][j] = new Random().nextInt(100);
                    }
                }
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Isend(a[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                    }
                }
            }
            Request s1 = null;
            for (int i = 0; i < n1; i++) {
             //!!!!   s1 = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
              //!!!!  s1.Wait();
            }
            for (int i = 0; i < n1; i++) {
                //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, 3000);
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < n; j++) {
                    //!!!!    s1 = //!!!! MPI.COMM_WORLD.Irecv(b[i], j * 2, 2, //!!!! MPI.INT, j, 3000);
                     //!!!!   s1.Wait();
                    }
                }
            }
            if (myrank == 0) {
                check:
                {
                    for (int i = 0; i < n1; i++) {
                        for (int j = 0; j < 2 * n; j++) {
                            a[i][j] = a[i][j] - b[i][j];
                            if (a[i][j] != 0) {
                                System.out.println("Ошибка!!!");
                                break check;
                            }
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
            }
        } catch (MPIException ex) {
         //   Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ScatterGather(int n1) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank =  MPI.COMM_WORLD.getRank();
            int n =  MPI.COMM_WORLD.getSize();
            int[][] a = new int[n1][2 * n];
            int[][] c = new int[n1][2];
            int[][] b = new int[n1][2 * n];
            int[] a1 = new int[n];
            int[] b1 = new int[n];
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < 2 * n; j++) {
                        a[i][j] = new Random().nextInt(100);
                    }
                }
            }
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
            if (myrank == 0) {
                check:
                {
                    for (int i = 0; i < n1; i++) {
                        for (int j = 0; j < 2 * n; j++) {
                            a[i][j] = a[i][j] - b[i][j];
                            if (a[i][j] != 0) {
                                System.out.println("Ошибка!!!");
                                break check;
                            }
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
            }
        } catch (MPIException ex) {
          //  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws MPIException {
        try {
            int t,s,n;
            n = 20; // количество строк в матрице
            t = 2; // количество экспериментов
            s = 2; // коэффициент умножения
             MPI.Init(new String[]{""});
            int size =  MPI.COMM_WORLD.getSize();
            int myrank =  MPI.COMM_WORLD.getRank();
            ParallelDebug pd=new ParallelDebug( MPI.COMM_WORLD);
            if (myrank == 0) {
                System.out.println("----------------------------------------------------------------------------------------------");
                System.out.print("    dim \t");
                System.out.print("IsendReceive \t ");
                System.out.print("SendReceive \t ");
                System.out.print("SendIreceive \t ");
                System.out.print("IsendIreceive \t ");
                System.out.print("ScatterGather \t ");
                System.out.println("----------------------------------------------------------------------------------------------");
            }
            for (int i = 0; i <= t; i++) {
                if (myrank == 0) {
                    System.out.print("  [" + n + "][" + 2 * size + "] \t");
                }
                //!!!! MPI.COMM_WORLD.Barrier();
                IsendReceive(n);
                //!!!! MPI.COMM_WORLD.Barrier();
                SendReceive(n,pd);
                //!!!! MPI.COMM_WORLD.Barrier();
                SendIreceive(n);
                //!!!! MPI.COMM_WORLD.Barrier();
                IsendIreceive(n);
                //!!!! MPI.COMM_WORLD.Barrier();
                ScatterGather(n);
                n = n * s;
                if (myrank == 0) {
                    System.out.println("");
                }
            }
            if (myrank == 0) {
                System.out.println("----------------------------------------------------------------------------------------------");
            }
            //!!!! MPI.Finalize();
        } catch (MPIException ex) {
          //  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
