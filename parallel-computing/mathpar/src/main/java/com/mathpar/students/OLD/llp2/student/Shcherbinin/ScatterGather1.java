/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import java.util.Arrays;
import mpi.*;

/**
 *
 * @author ridkeim
 */

public class ScatterGather1 {
    /**
     * @param myrunk номер процессора
     */
    private int myrank;
    /**
     * @param np общее кол-во процессоров
     */
    private int np;
    /**
     * @param numb кол-во элементов на процессор в строке
     */
    private int numb;
    /**
     * @param time счетчик времени
     */
    private long time;


    public ScatterGather1(int row, int mult) throws MPIException {
        numb = mult;
        myrank = MPI.COMM_WORLD.getRank();
        np = MPI.COMM_WORLD.getSize();

        int[][] a = new int[row][np * numb];
        int[][] b = new int[row][np * numb];
        int[] a0 = new int[a.length*a[0].length];
        int[] b0 = new int[a.length*a[0].length];
        if (myrank == 0) {

            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = (int) (100 * (Math.random()));
                }
                System.out.println(Arrays.toString(a[i]));
            }
            a0 = toArr(a, np);
            System.out.println("Arr "+Arrays.toString(a0));

        }
        try {
            time = testScatterGather(a0, b0);
        } catch (Exception e) {
            time = -1;
        }
        if(myrank == 0){
            System.out.println("b0 = "+Arrays.toString(b0) );
        }
    }
     private long testScatterGather(int[] a, int[] b) throws MPIException {
        long time1 = System.currentTimeMillis();
        int[] int1 = new int[np];
        int[] int2 = new int[np];
        int[] c = new int[a.length/np];
        for(int i = 0; i< np;i++){
            int1[i] = a.length/np;
            int2[i]= i*a.length/np;
        }

            //!!!! MPI.COMM_WORLD.Scatterv(a, 0, int1, int2, //!!!! MPI.INT, c, 0, a.length/np, //!!!! MPI.INT, 0);
            //!!!! MPI.COMM_WORLD.Gather(c, 0, c.length, //!!!! MPI.INT, b, 0, c.length, //!!!! MPI.INT, 0);

        time1 = System.currentTimeMillis() - time1;
        if (IsendIrecv.testRes(a, b)){
            return time1;
        }
        return -1;
    }
    public static int[] toArr(int[][] a, int np){
        int nmb = a[0].length/np;
            int[] a0 = new int[a.length*a[0].length];
            for (int i=0; i<np;i++){
                for(int j=0;j<a.length;j++){
                    for (int k = 0; k < nmb; k++) {
                        int n = i*np+j*nmb+k;
                        int l = i*nmb+k;

                        a0[n]=a[j][l];
                    }
                }
            }


        return a0;
    }
    public long getTime(){
        return time;
    }

}
