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
 * решение СЛАУ с помощью LU разложения
 *
 */
public class TestSolveSLAU {

    public static void main(String[] args) {
//        Random r = new Random();
        Ring ring = new Ring("R64[x]");
        ring.FLOATPOS = 5;
        int n = 3;
        Element[] bb = new Element[n];
        Element[][] aa = new Element[n][n];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                aa[i][j] = new NumberZ(r.nextInt(5));
//            }
//        }
        aa[0][0] = new NumberR64(4);
        aa[0][1] = new NumberR64(-3);
        aa[0][2] = new NumberR64(2);
        aa[1][0] = new NumberR64(8);
        aa[1][1] = new NumberR64(-8);
        aa[1][2] = new NumberR64(7);
        aa[2][0] = new NumberR64(12);
        aa[2][1] = new NumberR64(-5);
        aa[2][2] = new NumberR64(5);
        bb[0] = new NumberR64(0);
        bb[1] = new NumberR64(-12);
        bb[2] = new NumberR64(4);

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
