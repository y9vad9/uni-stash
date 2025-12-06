/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import mpi.*;
import com.mathpar.parallel.utils.MPITransport;

/**
 *
 * @author scherbinin
 */
//mpirun C java -cp /home/scherbinin/NetBeansProjects/mpi/build/classes:$CLASSPATH mpitest.IsendIrecv
//mpirun C java -cp /home/ridkeim/NetBeansProjects/mpi/build/classes:$CLASSPATH mpitest.IsendIrecv
public class IsendIrecv {

    public static int myrank;
    public static int np;
    private static int numb;
    public static long time;

    public IsendIrecv(int row) throws MPIException {
        numb = 2;
        myrank =MPI.COMM_WORLD.getRank();
        np =  MPI.COMM_WORLD.getSize();
        int[][] a = new int[row][np * numb];
        int[][] b = new int[row][np * numb];
        if (myrank == 0) {
//            System.out.println("массив а = ");
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = (int) (100 * (Math.random()));
                }
//                System.out.println(Arrays.toString(a[i]));
            }
        }
        time = testIsendIrecv(a, b);
           if (myrank == 0) {
               if (Main.min > time) {
                   Main.min = time;
               }
               if (Main.max < time) {
                   Main.max = time;
               }
           }
    }

    public IsendIrecv(int row, int mult) throws MPIException {
        numb = mult;
        myrank =  MPI.COMM_WORLD.getRank();
        np = MPI.COMM_WORLD.getSize();
        int[][] a = new int[row][np * numb];
        int[][] b = new int[row][np * numb];
        if (myrank == 0) {
//            System.out.println("массив а = ");
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = (int) (100 * (Math.random()));
                }
//                System.out.println(Arrays.toString(a[i]));
            }
        }
        time = testIsendIrecv(a, b);
                   if (myrank == 0) {
               if (Main.min > time) {
                   Main.min = time;
               }
               if (Main.max < time) {
                   Main.max = time;
               }
           }
    }

    private long testIsendIrecv(int[][] a, int[][] b) throws MPIException {
        long time1 = (myrank == 0) ? System.currentTimeMillis() : -1;
        int[][] c = new int[a.length][numb];
        if (myrank == 0) {
            for (int j = 0; j < a.length; j++) {
                for (int i = 0; i < np; i++) {
                    if (i == 0) {
                        System.arraycopy(a[j], 0, c[j], 0, numb);
                    } else {
                        MPITransport.iSendOld(a[j],i*numb,numb, MPI.INT,i,j*np+i);
//!!!!                         MPI.COMM_WORLD.Isend(a[j], i * numb, numb,  MPI.INT, i, j * np + i);
                    }
                }
            }
        } else {
            for (int i = 0; i < c.length; i++) {
                  MPITransport.iRecvOld(c[i],0,numb, MPI.INT,0,i*np+myrank);
//!!!!                Request Irecv = //!!!! MPI.COMM_WORLD.Irecv(c[i], 0, numb, //!!!! MPI.INT, 0, i * np + myrank);

 //!!!!               while (Irecv.Test() == null) {
 //               }
            }
        }
         MPI.COMM_WORLD.barrier();
        if (myrank == 0) {
        for (int i = 0; i < c.length; i++) {
                System.arraycopy(c[i], 0, b[i], 0, numb);
            }
        } else {
                for (int i = 0; i < c.length; i++) {
                    MPITransport.iSendOld(c[i],0,numb, MPI.INT,0,(myrank+i*np));
//!!!!                //!!!! MPI.COMM_WORLD.Isend(c[i], 0, numb, //!!!! MPI.INT, 0, (myrank+i*np));
            }
        }
        if (myrank == 0) {
                for (int j = 1; j < np; j++) {
                  for (int i = 0; i < b.length; i++) {
                      MPITransport.iRecvOld(b[i], j * numb, numb,  MPI.INT, j, (i * np + j));
 //!!!!                   Request Irecv = //!!!! MPI.COMM_WORLD.Irecv(b[i], j * numb, numb, //!!!! MPI.INT, j, (i * np + j));
//!!!!                    while (Irecv.Test() == null) {
//!!!!                   }
                }
            }
        }
        time1 = (myrank == 0) ? System.currentTimeMillis() - time1 : -1;
      //   if (IsendIrecv.testRes(a, b)){
        return time1;
      //  }
     //   return -1;
    }

    public static boolean testRes(int[][] a, int[][] b) {
        boolean flag = true;
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                if (a[i][j] != b[i][j]) {
                    flag = false;
                    break;
                }
            }
            if (!flag) {
                break;
            }
        }
        return flag;
    }
    public static boolean testRes(int[] a, int[] b) {
        for (int i = 0; i < b.length; i++) {
            if (a[i]!= b[i]) {
                    return false;
                }
            }
        return true;
    }


    public long getTime() {
    return time;
    }
}
