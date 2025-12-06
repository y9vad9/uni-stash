/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import mpi.*;

/**
 *
 * @author ridkeim
 */

public class ScatterGather {
    public static int myrank;
    public static int np;
    private static int numb;
    public static long time;
    public ScatterGather(int row) throws MPIException {
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
        time = testScatterGather(a, b);
           if (myrank == 0) {
               if (Main.min > time) {
                   Main.min = time;
               }
               if (Main.max < time) {
                   Main.max = time;
               }
           }


    }

    public ScatterGather(int row,int mult) throws MPIException {
        numb = mult;
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
        time = testScatterGather(a, b);
           if (myrank == 0) {
               if (Main.min > time) {
                   Main.min = time;
               }
               if (Main.max < time) {
                   Main.max = time;
               }
           }

    }
     private long testScatterGather(int[][] a, int[][] b) throws MPIException {
        long time1 = (myrank==0)?System.currentTimeMillis():-1;
        int[] int1 = new int[np];
        int[] int2 = new int[np];
        int[][] c = new int[a.length][numb];
        for(int i = 0; i< np;i++){
            int1[i] = numb;
            int2[i]= i*numb;
        }
        for (int i = 0; i < a.length; i++) {
//!!!!             MPI.COMM_WORLD.Scatterv(a[i], 0, int1, int2,  MPI.INT, c[i], 0, numb,  MPI.INT, 0);
 //!!!!           MPI.COMM_WORLD.Gather(c[i], 0, c[0].length,  MPI.INT, b[i], 0, c[i].length,  MPI.INT, 0);
        }
       // time1 = System.currentTimeMillis() - time1;
        time1 = (myrank==0)?System.currentTimeMillis() - time1:-1;
        if (IsendIrecv.testRes(a, b)){
            return time1;
        }


        return -1;
    }

    public long getTime() {
        return time;
    }
}
