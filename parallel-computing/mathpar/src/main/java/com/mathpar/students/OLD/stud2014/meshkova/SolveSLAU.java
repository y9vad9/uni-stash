/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.meshkova;

import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;

/**
 * @author meshkova
 * решение СЛАУ с помощью LU разложения
 * В - вектор(правая часть системы)
 * у - вспомогательный вектор нахождения решения
 * х - вектор решения СЛАУ
 */
public class SolveSLAU {
    public PLUQsteps LU;
    public VectorS B;   //   public VectorC B;
    public Element[] x;
    public Element[] y;

    public SolveSLAU(PLUQsteps LU, VectorS B) {  //VectorC
        this.B = B;
        this.LU = LU;
    }
    /*
     * метод вычисления суммы для нахождения в-ра у
     */
    public Element addOfProductY(int i, Ring ring) {
        Element result = ring.numberZERO();
        for (int k = 0; k < i; k++) {
            result = result.add(LU.L.M[i][k].multiply(y[k], ring), ring);
        }
        return result;
    }
    /*
     * метод вычисления суммы для нахождения в-ра х
     */
    public Element addOfProductX(int i, int n, Ring ring) {
        Element result = ring.numberZERO();
        for (int k = i + 1; k < n; k++) {
            result = result.add(LU.U.M[i][k].multiply(x[k], ring), ring);
        }
        return result;
    }
    /*
     * Вычисление
     */
    public Element[] solveSLAU(Ring ring) {
        x = new Element[LU.A.M.length];
        y = new Element[LU.A.M.length];
        Element[] m = new Element[LU.A.M.length];
             /*
              * нахождение y(i)
              */
        for (int i = 0; i < x.length; i++) {
            y[i] = B.V[i].subtract(addOfProductY(i, ring), ring);
        }
        /*
         * нахождение x(i)
         * m(i) - временная переменная для вычисления x(i)
         */
        for (int i = x.length - 1; i >= 0; i--) {
            m[i] = y[i].subtract(addOfProductX(i, x.length, ring), ring);
            x[i] = ring.numberONE().divide(LU.U.M[i][i], ring).multiply(m[i], ring);
        }
        return x;
    }
}
