/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.meshkova;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

/**
 *
 * @author meshkova
 */
public class PLUQsteps {

    public MatrixD A;
    public MatrixD L = null;
    public MatrixD U = null;
    public MatrixD P;
    public MatrixD Q;
    public MatrixD ADec;

    public PLUQsteps(MatrixD A, Ring ring) {
        this.A = A;
    }
    /*
     * метод вычисления элементов матриц L и U
     * (u11 u12 . . u1n)
     * (l21 u22 . . u2n)
     * (  .   .    .   )
     * (ln1 ln2  .. unn)
     * subtract вычитание
     * divide деление
     * add сложение
     * multiply умножение
     *
     * k - счетчик номера шага
     */

    public void solveLUTransform(Ring ring) {
        PDecomposition p = new PDecomposition(A, ring);
        int m = A.M.length;
        for (int k = 0; k < m; k++) {
            p.StepDecomposition(k, k, ring);
            ADec = p.ADec;
            for (int t = k + 1; t < m; t++) {
                ADec.M[k][t] = ADec.M[k][t];
                ADec.M[t][k] = ADec.M[t][k].divide(ADec.M[k][k], ring);
            }

            for (int i = k + 1; i < m; i++) {
                for (int j = k + 1; j < m; j++) {
                    ADec.M[i][j] = ADec.M[i][j].subtract(ADec.M[i][k].multiply(ADec.M[k][j], ring), ring);
                }
            }
        }
        P = p.P;
        Q = p.Q;
    }
    /*
     *  метод нахождения L и U в явном виде
     */

    public void solveLU(Ring ring) {
        solveLUTransform(ring);
        int m = A.M.length;
        L = new MatrixD(new Element[m][m]);
        U = new MatrixD(new Element[m][m]);
        //матрица L
        for (int i = 0; i < L.M.length; i++) {
            for (int j = 0; j < L.M.length; j++) {
                if (i < j) {
                    L.M[i][j] = ring.numberZERO();
                } else {
                    L.M[i][j] = ADec.M[i][j];
                }
            }
        }
        //Заполнение главной диагонали у L значениями = 1
        for (int i = 0; i < L.M.length; i++) {
            L.M[i][i] = ring.numberONE();
        }
        //матриа U
        for (int i = 0; i < U.M.length; i++) {
            for (int j = 0; j < U.M.length; j++) {
                if (i > j) {
                    U.M[i][j] = ring.numberZERO();
                } else {
                    U.M[i][j] = ADec.M[i][j];
                }
            }
        }
    }

    public String toString(Ring ring) {
        String l = this.L.toString(ring);
        String u = this.U.toString(ring);
        return "L = " + l + "\n" + "U = " + u;
    }
}
