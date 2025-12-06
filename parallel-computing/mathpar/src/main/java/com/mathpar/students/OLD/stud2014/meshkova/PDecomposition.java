/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.meshkova;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

/**
 * @author meshkova
 * P - матрица перестановок строк (умножается на А слева)
 * Q - матрица перестановок столбцов(умножается на А справа)
 * ADec - матрица композиции матриц PAQ
 */
public class PDecomposition {
    public MatrixD P;
    public MatrixD Q;
    public MatrixD ADec;

    public PDecomposition(MatrixD A, Ring ring) {
        P = new MatrixD(new Element[A.M.length][]);
        Q = new MatrixD(new Element[A.M.length][]);
        /*
         * заполненние матриц P,Q
         */
        for (int i = 0; i < P.M.length; i++) {
            P.M[i] = new Element[A.M[i].length];
            Q.M[i] = new Element[A.M[i].length];
            for (int j = 0; j < P.M[i].length; j++) {
                if (i != j) {
                    P.M[i][j] = ring.numberZERO();
                    Q.M[i][j] = ring.numberZERO();
                } else {
                    P.M[i][j] = ring.numberONE();
                    Q.M[i][j] = ring.numberONE();
                }
            }
        }
        ADec = A;
    }

    /**
     * перестановки P
     * накопление перестановок P
     * произведения этих перестановок на матрицу A : ADec
     *
     * @param n - номер строки с максимальным по abs элементом
     * @param m - номер заменяемой строки
     */
    public void replaceRow(int n, int m) {
        if (n != m) {
            Element[] buf1 = this.P.M[n];
            Element[] buf2 = this.P.M[m];
            Element[] buf3 = this.ADec.M[n];
            Element[] buf4 = this.ADec.M[m];
            this.P.M[m] = buf1;
            this.P.M[n] = buf2;
            this.ADec.M[m] = buf3;
            this.ADec.M[n] = buf4;
        }
    }

    /**
     * перестановки Q
     * накопление перестановок Q и произведения этих перестановок на
     * матрицу A в ADec
     * @param n - номер строки с максимальным по abs элементом
     * @param m - номер заменяемой строки
     */
    public void replaceCol(int n, int m) {
        if (n != m) {
            Element[] buf1 = new Element[Q.M.length];
            Element[] buf2 = new Element[Q.M.length];
            Element[] buf3 = new Element[Q.M.length];
            Element[] buf4 = new Element[Q.M.length];
            for (int i = 0; i < Q.M.length; i++) {
                buf1[i] = this.Q.M[i][n];
                buf2[i] = this.Q.M[i][m];
                buf3[i] = this.ADec.M[i][n];
                buf4[i] = this.ADec.M[i][m];
            }
            for (int i = 0; i < Q.M.length; i++) {
                this.Q.M[i][n] = buf2[i];
                this.Q.M[i][m] = buf1[i];
                this.ADec.M[i][n] = buf4[i];
                this.ADec.M[i][m] = buf3[i];
            }
        }
    }
/*
 * метод, отвечающий за переход к следующему диагональному элементу
 */
    public void decomposition(Ring ring) {
        int n = ADec.M.length;
        for (int i = 0; i < n; i++) {
            StepDecomposition(i, i, ring);
        }
    }
/*
 * нахождение max элемента и сохранение его позиции
 */
    public void StepDecomposition(int m, int k, Ring ring) {
        int n = ADec.M.length;
        int i = 0;
        int j = 0;
        int row = -1;
        int col = -1;
        Element max = ring.numberMINUS_ONE;
        for (i = m; i < n; i++) {
            for (j = k; j < n; j++) {
                if (Math.abs(ADec.M[i][j].doubleValue()) > max.doubleValue()) {
                    max = ADec.M[i][j];
                    row = i;
                    col = j;
                }
            }
        }
        replaceRow(row, m);
        replaceCol(k, col);
    }
}
