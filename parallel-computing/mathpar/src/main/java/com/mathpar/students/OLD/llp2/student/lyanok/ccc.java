package com.mathpar.students.OLD.llp2.student.lyanok;

import java.math.BigInteger;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import mpi.*;

/**
 *
 * @author kireev
 */
public class ccc {

    public static void TestSend2(int n) {
        long t1 = System.currentTimeMillis();
        int myrank = 0;
        int np = 0;
        //int n=8;
        try {
            myrank =  MPI.COMM_WORLD.getRank();
            np =  MPI.COMM_WORLD.getSize();
            Object[][] a = new Object[n][2 * np];
            Object[][] b = new Object[n][2];
            Object[][] c = new Object[n][2 * np];
            BigInteger[][] d = new BigInteger[n][2 * np];
            if (myrank == 0) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < 2 * np; j++) {
                        a[i][j] = new BigInteger(4, new Random());
                        //System.out.print("  " + a[i][j]);
                    }
                    //System.out.println("");
                }
                for (int k = 0; k < n; k++) {
                    for (int i = 0; i < np; i++) {
                        //!!!! MPI.COMM_WORLD.Send(a[k], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
                    }
                }
                //System.out.println("Proc num " + myrank + "Massiv otpravlen");
            }
            for (int k1 = 0; k1 < n; k1++) {
                //!!!! MPI.COMM_WORLD.Recv(b[k1], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
            }
            //System.out.println("Proc num " + myrank + "Massiv prinayt");
            for (int k2 = 0; k2 < n; k2++) {
                //!!!! MPI.COMM_WORLD.Send(b[k2], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
            }
            if (myrank == 0) {
                for (int k3 = 0; k3 < n; k3++) {
                    for (int i = 0; i < np; i++) {
                        //!!!! MPI.COMM_WORLD.Recv(c[k3], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
                    }
                }
                //System.out.println("Proc num " + myrank + "Massiv prinayt nazad");
            }
            if (myrank == 0) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < 2 * np; j++) {
                        d[i][j] = ((BigInteger) a[i][j]).subtract((BigInteger) c[i][j]);
                        //System.out.print("  " + d[i][j]);
                        if (!(d[i][j].equals(BigInteger.ZERO))) {
                            System.out.println("Error");
                            break;
                        }
                    }
                    //System.out.println("");
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("\t" + t + "\t ");
            }
        } catch (MPIException ex) {
            Logger.getLogger(ccc.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void TestIsend(int n) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank =  MPI.COMM_WORLD.getRank();
            int np = MPI.COMM_WORLD.getSize();
            Object[][] a = new Object[n][2 * np];
            Object[][] b = new Object[n][2];
            Object[][] c = new Object[n][2 * np];
            BigInteger[][] d = new BigInteger[n][2 * np];
            if (myrank == 0) {
                //System.out.println("IsendRecv--------------------");
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < 2 * np; j++) {
                        a[i][j] = new BigInteger(4, new Random());
                        //System.out.print("  " + a[i][j]);
                    }
                    //System.out.println("");
                }
                for (int k = 0; k < n; k++) {
                    for (int i = 0; i < np; i++) {
                        //!!!! MPI.COMM_WORLD.Isend(a[k], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
                    }
                }
                //System.out.println("Proc num " + myrank + "Массив отправлен");
            }
            for (int k1 = 0; k1 < n; k1++) {
                //!!!! MPI.COMM_WORLD.Recv(b[k1], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
            }
            //System.out.println("Proc num " + myrank + "Массив принят");
            for (int k2 = 0; k2 < n; k2++) {
                //!!!! MPI.COMM_WORLD.Isend(b[k2], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
            }
            if (myrank == 0) {
                for (int k3 = 0; k3 < n; k3++) {
                    for (int i = 0; i < np; i++) {
                        //!!!! MPI.COMM_WORLD.Recv(c[k3], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
                    }
                }
                // System.out.println("Proc num " + myrank + "Массив принят назад");
            }
            if (myrank == 0) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < (2 * np); j++) {
                        d[i][j] = ((BigInteger) a[i][j]).subtract((BigInteger) c[i][j]);
                        if (!(d[i][j].equals(BigInteger.ZERO))) {
                            System.out.println("Error");
                            break;
                        }
                        //System.out.print("  " + d[i][j]);
                    }
                    //System.out.println("");
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("  " + t + "\t");
            }
        } catch (MPIException ex) {
            Logger.getLogger(ccc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void TestIrecv(int n) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank =  MPI.COMM_WORLD.getRank();
            int np =  MPI.COMM_WORLD.getSize();
            Object[][] a = new Object[n][2 * np];
            Object[][] b = new Object[n][2];
            Object[][] c = new Object[n][2 * np];
            BigInteger[][] d = new BigInteger[n][2 * np];
            if (myrank == 0) {
                //System.out.println("SendIrecv-----------------------");
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < 2 * np; j++) {
                        a[i][j] = new BigInteger(4, new Random());
                    }
                }
                for (int k = 0; k < n; k++) {
                    for (int i = 0; i < np; i++) {
                        //!!!! MPI.COMM_WORLD.Send(a[k], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
                    }
                }
                //System.out.println("Proc num " + myrank + "Массив отправлен");
            }
            Request s1 = null;
            for (int k1 = 0; k1 < n; k1++) {
 //!!!!                s1 = //!!!! MPI.COMM_WORLD.Irecv(b[k1], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
 //!!!!                s1.Wait();
            }
            //System.out.println("Proc num " + myrank + "Массив принят");
            for (int k2 = 0; k2 < n; k2++) {
                //!!!! MPI.COMM_WORLD.Send(b[k2], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
            }
            if (myrank == 0) {
                for (int k3 = 0; k3 < n; k3++) {
                    for (int i = 0; i < np; i++) {
  //!!!!                       s1 = //!!!! MPI.COMM_WORLD.Irecv(c[k3], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
 //!!!!                        s1.Wait();
                    }
                }
                // System.out.println("Proc num " + myrank + "Массив принят назад");
            }
            if (myrank == 0) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < (2 * np); j++) {
                        d[i][j] = ((BigInteger) a[i][j]).subtract((BigInteger) c[i][j]);
                        if (!(d[i][j].equals(BigInteger.ZERO))) {
                            System.out.println("Error");
                            break;
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("      " + t + "\t");
            }
        } catch (MPIException ex) {
            Logger.getLogger(ccc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void TestIsendIrecv(int n) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank = MPI.COMM_WORLD.getRank();
            int np =  MPI.COMM_WORLD.getSize();
            Object[][] a = new Object[n][2 * np];
            Object[][] b = new Object[n][2];
            Object[][] c = new Object[n][2 * np];
            BigInteger[][] d = new BigInteger[n][2 * np];
            if (myrank == 0) {
                //System.out.println("IsendIrecv--------------------");
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < 2 * np; j++) {
                        a[i][j] = new BigInteger(4, new Random());
                    }
                }
                for (int k = 0; k < n; k++) {
                    for (int i = 0; i < np; i++) {
                        //!!!! MPI.COMM_WORLD.Isend(a[k], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
                    }
                }
                //System.out.println("Proc num " + myrank + "Массив отправлен");
            }
            Request s1 = null;
            for (int k1 = 0; k1 < n; k1++) {
 //!!!!                s1 = //!!!! MPI.COMM_WORLD.Irecv(b[k1], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
 //!!!!                s1.Wait();
            }
            //System.out.println("Proc num " + myrank + "Массив принят");
            for (int k2 = 0; k2 < n; k2++) {
                //!!!! MPI.COMM_WORLD.Isend(b[k2], 0, 2, //!!!! MPI.OBJECT, 0, 3000);
            }
            if (myrank == 0) {
                for (int k3 = 0; k3 < n; k3++) {
                    for (int i = 0; i < np; i++) {
  //!!!!                       s1 = //!!!! MPI.COMM_WORLD.Irecv(c[k3], i * 2, 2, //!!!! MPI.OBJECT, i, 3000);
 //!!!!                        s1.Wait();
                    }
                }
                //System.out.println("Proc num " + myrank + "Массив принят назад");
            }
            if (myrank == 0) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < (2 * np); j++) {
                        d[i][j] = ((BigInteger) a[i][j]).subtract((BigInteger) c[i][j]);
                        if (!(d[i][j].equals(BigInteger.ZERO))) {
                            System.out.println("Error");
                            break;
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("  " + t + "\t");
            }
        } catch (MPIException ex) {
            Logger.getLogger(ccc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void TestScatterGather(int n) {
        try {
            long t1 = System.currentTimeMillis();
            int myrank =  MPI.COMM_WORLD.getRank();
            int np =  MPI.COMM_WORLD.getSize();
            Object[][] a = new Object[n][2 * np];
            Object[][] b = new Object[n][2];
            Object[][] c = new Object[n][2 * np];
            BigInteger[][] d = new BigInteger[n][2 * np];
            if (myrank == 0) {
                //System.out.println("ScatterGather----------------------");
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < 2 * np; j++) {
                        a[i][j] = new BigInteger(4, new Random());
                    }
                }
            }
            for (int k = 0; k < n; k++) {
                //!!!! MPI.COMM_WORLD.Scatter(a[k], 0, 2, //!!!! MPI.OBJECT, b[k], 0, 2, //!!!! MPI.OBJECT, 0);
            }
            //System.out.println("Proc num " + myrank + "Массив отправлен");
            for (int k1 = 0; k1 < n; k1++) {
                //!!!! MPI.COMM_WORLD.Gather(b[k1], 0, 2, //!!!! MPI.OBJECT, c[k1], 0, 2, //!!!! MPI.OBJECT, 0);
            }
            //System.out.println("Proc num " + myrank + "Массив принят назад");
            if (myrank == 0) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < (2 * np); j++) {
                        d[i][j] = ((BigInteger) a[i][j]).subtract((BigInteger) c[i][j]);
                        if (!(d[i][j].equals(BigInteger.ZERO))) {
                            System.out.println("Error");
                            break;
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            long t = t2 - t1;
            if (myrank == 0) {
                System.out.print("      \t" + t);
            }
        } catch (MPIException ex) {
            Logger.getLogger(ccc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws MPIException {
        try {
            int e = 2; //число экспериментов
            int s = 2; //коэффициент умножения
            int n = 3200;//число строк в матрице
            int k = 1;
            MPI.Init(new String[]{""});
            int myrank = MPI.COMM_WORLD.getRank();
            int c = MPI.COMM_WORLD.getSize();
            if (myrank == 0) {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("N   SendRecv   IsendRecv   SendIrecv   IsendIrecv   ScatterGather      Matrix");
                System.out.println("--------------------------------------------------------------------------------");
            }
            for (int i = 1; i <= e; i++) {
                if (myrank == 0) {
                    System.out.print(k);
                }
                //!!!! MPI.COMM_WORLD.Barrier();
                TestSend2(n);
                //!!!! MPI.COMM_WORLD.Barrier();
                TestIsend(n);
                //!!!! MPI.COMM_WORLD.Barrier();
                TestIrecv(n);
                //!!!! MPI.COMM_WORLD.Barrier();
                TestIsendIrecv(n);
                //!!!! MPI.COMM_WORLD.Barrier();
                TestScatterGather(n);
                if (myrank == 0) {
                    System.out.print("           " + "\t" + n + "*" + 2 * c);
                    k++;
                    System.out.println("");
                    System.out.println("--------------------------------------------------------------------------------");
                }
                n = n * s;
                //System.out.println("n="+n);
            }
            //!!!! MPI.Finalize();
        } catch (MPIException ex) {
            Logger.getLogger(ccc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
