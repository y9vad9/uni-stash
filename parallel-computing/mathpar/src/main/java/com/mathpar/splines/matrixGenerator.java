/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.splines;

/**
 *
 * @author yuri
 */
public class matrixGenerator {

    public static void main(String[] args) {
        // генерация матрицы для двумерного полинома

        int pow = 4;       // степень полинома (4 - кубика)
        int sz = 8;         // 2 - одномерный, 4 - двумерный, 8 - трёхмерный.

        int[][] out = new int[sz * sz][sz * sz];

        int[][] xyz = new int[][]{
            {0, 0, 0},
            {1, 0, 0},
            {1, 1, 0},
            {0, 1, 0},
            {0, 0, 1},
            {1, 0, 1},
            {1, 1, 1},
            {0, 1, 1}
        };
        int[][] diff = new int[][]{
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0},
            {1, 1, 0},
            {0, 0, 1},
            {1, 0, 1},
            {0, 1, 1},
            {1, 1, 1},};

        for (int i = 0; i < sz * sz; i++) {
            int k = i % sz;
            int x = xyz[k][0];
            int y = xyz[k][1];
            int z = xyz[k][2];

            for (int j = 0; j < sz * sz; j++) {
                int xp = j % pow;
                int yp = (j / pow) % pow;
                int zp = (j / (pow * pow)) % pow;
                int dk = i / sz;
                int dx = diff[dk][0];
                int dy = diff[dk][1];
                int dz = diff[dk][2];
                int res = 1;
                while (dx > 0) {
                    res *= xp--;
                    dx--;
                }
                for (int l = 0; l < xp; l++) {
                    res *= x;
                }
                while (dy > 0) {
                    res *= yp--;
                    dy--;
                }
                for (int l = 0; l < yp; l++) {
                    res *= y;
                }
                while (dz > 0) {
                    res *= zp--;
                    dz--;
                }
                for (int l = 0; l < zp; l++) {
                    res *= z;
                }
                System.out.print(res + ", ");
                out[i][j] = res;
            }
            System.out.println();
        }
/*        MatrixS m1 = new MatrixS(out);//, NumberZ.ONE);
        MatrixS ms = m1.adjoint();
        ms = ms.divideByNumber(ms.det());

        System.out.println(ms);*/
    }
}
