/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.meshkova;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;

/**
 *
 * @author meshkova
 */
public class TestPDec {
    public static void main(String[] args) {
   //     Random r = new Random();
        Ring ring = new Ring("R64[x]");
        ring.FLOATPOS = 5;
        int n = 3;
  //      Element[] bb = new Element[n];
        Element[][] aa = new Element[n][n];
        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                aa[i][j] = new NumberZ(r.nextInt(5));
//            }
        }
        aa[0][0] = new NumberR64(4);
        aa[0][1] = new NumberR64(-3);
        aa[0][2] = new NumberR64(2);
        aa[1][0] = new NumberR64(8);
        aa[1][1] = new NumberR64(-8);
        aa[1][2] = new NumberR64(7);
        aa[2][0] = new NumberR64(12);
        aa[2][1] = new NumberR64(-5);
        aa[2][2] = new NumberR64(5);
//        bb[0] = new NumberR64(0.5);
//        bb[1] = new NumberR64(2);
//        bb[2] = new NumberR64(1);
        MatrixD A = new MatrixD(aa);
        System.out.println("A = " + A.toString(ring));

        PDecomposition p = new PDecomposition(A, ring);
        p.decomposition(ring);

        System.out.println("P = " + p.P.toString(ring));
        System.out.println(" ");
        System.out.println("PAQ = " + p.ADec.toString(ring));
        System.out.println(" ");
        System.out.println("Q = " + p.Q.toString(ring));
    }
}
