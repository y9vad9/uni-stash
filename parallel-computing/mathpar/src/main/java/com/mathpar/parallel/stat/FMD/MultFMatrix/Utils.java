/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.FMD.MultFMatrix;

import java.util.Arrays;

/**
 *
 * @author vladimir
 */
public class Utils {

    private static int[][] proc_matrix;

    public static void main(String[] args) {

        final NodeModel modeul = new NodeModel(0, 64);
        initProcMatrix(modeul, 3);
        for (int[] i : proc_matrix) {
            System.out.println("proc_matrix " + Arrays.toString(i));
        }
        System.out.println("" + Math.log(64) / Math.log(4));
    }

  private static void initProcMatrix(final NodeModel node, final int dep) {
        int count_proc = (int) (Math.pow(4, dep) > node.size ? node.size : Math.pow(4, dep));
        int k = (int) Math.pow(2, (int) (Math.log(count_proc) / Math.log(4)));// размерность матрицы k*k  кол-во процессоров
//        int y = node.size;
//        while (y > 2) {
//            y >>= 1;
//            k++;
//        }

        proc_matrix = new int[k][k];//матрица процессоров
        int t = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                proc_matrix[i][j] = t;
           //    process.add(t);
                t++;
            }
        }
        for(int[] i: proc_matrix){
            System.out.println("proc"  + Arrays.toString(i));
        }
      // proc_matrix_invert = new int[k][k];
    }

    public static final class NodeModel {

        public final int rank;
        public final int size;

        public NodeModel(int rank, int size) {
            this.rank = rank;
            this.size = size;
        }
    }
}
