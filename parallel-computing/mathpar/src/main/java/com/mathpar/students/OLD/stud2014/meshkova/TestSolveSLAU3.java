/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.meshkova;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;

/**
 *
 * @author meshkova
 */
public class TestSolveSLAU3 {
        public static void main(String[] args) {
        Ring ring = new Ring("R64[x]");
        ring.FLOATPOS = 5;
        int n = 3;
        Element[] bb = new Element[n];
        Element[][] aa = new Element[n][n];

        aa[0][0] = new NumberR64(0);
        aa[0][1] = new NumberR64(1);
        aa[0][2] = new NumberR64(7);
        aa[1][0] = new NumberR64(4);
        aa[1][1] = new NumberR64(3);
        aa[1][2] = new NumberR64(2);
        aa[2][0] = new NumberR64(2);
        aa[2][1] = new NumberR64(1);
        aa[2][2] = new NumberR64(3);
        bb[0] = new NumberR64(9);
        bb[1] = new NumberR64(10);
        bb[2] = new NumberR64(6);
        MatrixD A = new MatrixD(aa);
        VectorS B = new VectorS(bb,-1);
            System.out.println("A= \n" + A.toString(ring));
            System.out.println("B=" + B.toString(ring));
            PLUQsteps LU = new PLUQsteps(A, ring);
             LU.solveLU(ring);
             //b умножаем на P
           VectorS PB = LU.P.multiplyByColumn(B, ring);
            SolveSLAU SLAU = new SolveSLAU(LU, PB);
            Element[] x = SLAU.solveSLAU(ring);
            /*
             * вектор х на Q
             * res решение СЛАУ
             */
            VectorS res = LU.Q.multiplyByColumn(new VectorS(x), ring);
            System.out.println(LU.toString(ring));
            System.out.println("SOLVE SLAU = \n" + res.toStringColumn());
    }
}
