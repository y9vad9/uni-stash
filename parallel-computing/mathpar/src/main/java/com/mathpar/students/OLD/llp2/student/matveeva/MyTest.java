
package com.mathpar.students.OLD.llp2.student.matveeva;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mathpar.students.OLD.llp2.student.message.ParallelDebug;
import mpi.*;

//mpirun C java -cp /home/matveeva/mathpar/target/classes:$CLASSPATH llp2.student.matveeva.MyTest
/**
 *
 * @author matveeva
 */
public class MyTest {

     public static void SendReceive(int n1) {
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
            }
            long t1 = System.currentTimeMillis();
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = n - 1; j >= 0; j--) {
                        //!!!! MPI.COMM_WORLD.Send(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                    }
                    //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Recv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
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

        } catch (MPIException ex) {
            Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void IsendReceive(int n1) {
        try {
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
            }
            long t1 = System.currentTimeMillis();
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = n - 1; j >= 0; j--) {
                        //!!!! MPI.COMM_WORLD.Isend(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                    }
                    //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Recv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                    for (int j = 0; j < n; j++) {
                        //!!!! MPI.COMM_WORLD.Recv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
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
        } catch (MPIException ex) {
            Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void SendIreceive(int n1) {
        try {
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
            }
            long t1 = System.currentTimeMillis();
            Request s1 = null;
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = n - 1; j >= 0; j--) {
                        //!!!! MPI.COMM_WORLD.Send(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                    }
 //!!!!                   s1 = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
   //!!!!                 s1.Wait();
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
         //!!!!           s1 = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
          //!!!!          s1.Wait();
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Send(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                    for (int j = 0; j < n; j++) {
              //!!!!          s1 = //!!!! MPI.COMM_WORLD.Irecv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
              //!!!!          s1.Wait();
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
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
        } catch (MPIException ex) {
            Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void IsendIreceive(int n1) {
        try {
            int myrank =  MPI.COMM_WORLD.getRank();
            int n = MPI.COMM_WORLD.getSize();
            int[][] a = new int[n1][2 * n];
            int[][] c = new int[n1][2];
            int[][] b = new int[n1][2 * n];
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = 0; j < 2 * n; j++) {
                        a[i][j] = new Random().nextInt(100);
                    }
                }
            }
            long t1 = System.currentTimeMillis();
            Request s1 = null;
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    for (int j = n - 1; j >= 0; j--) {
                        //!!!! MPI.COMM_WORLD.Isend(a[i], j * 2, 2, //!!!! MPI.INT, j, i);
                    }
                 //!!!!   s1 = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                 //!!!!   s1.Wait();
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
            //!!!!        s1 = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, 2, //!!!! MPI.INT, 0, i);
             //!!!!       s1.Wait();
                }
            }
            if (myrank != 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                }
            }
            if (myrank == 0) {
                for (int i = 0; i < n1; i++) {
                    //!!!! MPI.COMM_WORLD.Isend(c[i], 0, 2, //!!!! MPI.INT, 0, i);
                    for (int j = 0; j < n; j++) {
                  //!!!!      s1 = //!!!! MPI.COMM_WORLD.Irecv(b[i], j * 2, 2, //!!!! MPI.INT, j, i);
                  //!!!!     s1.Wait();
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
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
        } catch (MPIException ex) {
            Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ScatterGather(int n1) {
        try {
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
            long t1 = System.currentTimeMillis();
            for (int i = 0; i < a.length; i++) {
                //!!!! MPI.COMM_WORLD.Scatterv(a[i], 0, a1, b1, //!!!! MPI.INT, c[i], 0, 2, //!!!! MPI.INT, 0);
                //!!!! MPI.COMM_WORLD.Gatherv(c[i], 0, c[0].length, //!!!! MPI.INT, b[i], 0, a1, b1, //!!!! MPI.INT, 0);
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t");
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
        } catch (MPIException ex) {
            Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws MPIException {
        try {
            int t, s, n;
            n = 20; // количество строк в матрице
            t = 4; // количество экспериментов
            s = 2; // коэффициент умножения
            //!!!! MPI.Init(new String[]{""});
            int size =  MPI.COMM_WORLD.getSize();
            int myrank =  MPI.COMM_WORLD.getRank();
            ParallelDebug pd=new ParallelDebug( MPI.COMM_WORLD);
            if (myrank == 0) {
                System.out.println("-------------------------------------------------------------------------------------------------");
                System.out.print("    dim \t");
                System.out.print("IsendReceive \t ");
                System.out.print("SendReceive \t ");
                System.out.print("SendIreceive \t ");
                System.out.print("IsendIreceive \t ");
                System.out.print("ScatterGather \t ");
                System.out.println("-------------------------------------------------------------------------------------------------");
            }
            for (int i = 0; i <= t; i++) {
                if (myrank == 0) {
                    System.out.print("  [" + n + "][" + 2 * size + "] \t");
                }
                //!!!! MPI.COMM_WORLD.Barrier();
                IsendReceive(n);
                pd.paddEvent("после выполнения процедуры IsendReceive", "near the end");
                //!!!! MPI.COMM_WORLD.Barrier();
                SendReceive(n);
                pd.paddEvent("после выполнения процедуры SendReceive", "near the end");
                //!!!! MPI.COMM_WORLD.Barrier();
                SendIreceive(n);
                pd.paddEvent("после выполнения процедуры SendIreceive", "near the end");
                //!!!! MPI.COMM_WORLD.Barrier();
                IsendIreceive(n);
                pd.paddEvent("после выполнения процедуры IsendIreceive", "near the end");
                //!!!! MPI.COMM_WORLD.Barrier();
                ScatterGather(n);
                pd.paddEvent("после выполнения процедуры ScatterGather", "near the end");
                n = n * s;
                if (myrank == 0) {
                    System.out.println("");
                }
            }
            if (myrank == 0) {
                System.out.println("-------------------------------------------------------------------------------------------------");
            }
              pd.generateDebugLog();
            //!!!! MPI.Finalize();
        } catch (MPIException ex) {
            Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
