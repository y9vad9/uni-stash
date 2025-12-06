/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import mpi.*;

/**
 *
 * @author scherbinin
 */
public class SendRecv {

    public static int myrank;
    public static int np;
    private static int numb;
    public static long time;

    public SendRecv(int row) throws MPIException {
        numb = 2;
        myrank =  MPI.COMM_WORLD.getRank();
        np =  MPI.COMM_WORLD.getSize();
        int[][] a = new int[row][np * numb];
        int[][] b = new int[row][np * numb];
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = (int) (100 * (Math.random()));
                }
            }
        }
        time = testSendRecv(a, b);
        if (myrank == 0) {
            if (Main.min > time) {
                Main.min = time;
            }
            if (Main.max < time) {
                Main.max = time;
            }
        }

    }

    public SendRecv(int row, int mult) throws MPIException {
        numb = mult;
        myrank =  MPI.COMM_WORLD.getRank();
        np =MPI.COMM_WORLD.getSize();
        int[][] a = new int[row][np * numb];
        int[][] b = new int[row][np * numb];
        if (myrank == 0) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = (int) (100 * (Math.random()));
                }
            }
        }
        time = testSendRecv(a, b);

        if (myrank == 0) {
            if (Main.min > time) {
                Main.min = time;
            }
            if (Main.max < time) {
                Main.max = time;
            }
        }

    }

    private long testSendRecv(int[][] a, int[][] b) throws MPIException {
        long time1 = (myrank == 0) ? System.currentTimeMillis() : -1;
        int[][] c = new int[a.length][numb];
        if (myrank == 0) {
            for (int j = 0; j < a.length; j++) {
                for (int i = 0; i < np; i++) {
                    if (i == 0) {
                        System.arraycopy(a[j], 0, c[j], 0, numb);
                    } else {
                        //!!!! MPI.COMM_WORLD.Send(a[j], i * numb, numb, //!!!! MPI.INT, i, j * np + i);
                    }
                }
            }
        } else {
            for (int i = 0; i < c.length; i++) {
                //!!!! MPI.COMM_WORLD.Recv(c[i], 0, numb, //!!!! MPI.INT, 0, i * np + myrank);
            }
        }
       // //!!!! MPI.COMM_WORLD.Barrier();
        if (myrank == 0) {
        for (int i = 0; i < c.length; i++) {
                System.arraycopy(c[i], 0, b[i], 0, numb);
            }
        } else {
                for (int i = 0; i < c.length; i++) {
                //!!!! MPI.COMM_WORLD.Send(c[i], 0, numb, //!!!! MPI.INT, 0, (myrank+i*np));
            }
        }
        if (myrank == 0) {
                for (int j = 1; j < np; j++) {
                  for (int i = 0; i < b.length; i++) {
                    //!!!! MPI.COMM_WORLD.Recv(b[i], j * numb, numb, //!!!! MPI.INT, j, (i * np + j));
                }
            }
        }
        time1 = (myrank == 0) ? System.currentTimeMillis() - time1 : -1;
       //  if (IsendIrecv.testRes(a, b)){
        return time1;
       // }
       // return -1;
    }

    public long getTime() {
        return time;
    }
}
