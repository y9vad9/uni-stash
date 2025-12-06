/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;

/**
 *
 * @author ridkeim
 */
public class T1 {
    public static void main(String[] args) {
        MatrixS A = new MatrixS(new Element[][]{new Element[]{new NumberZ(1)},new Element[]{new NumberZ(1),new NumberZ(2)}},
                                new int[][]{new int[]{0},new int[]{0,1}});
        System.out.println(A);
        MatrixS D = new MatrixS(new Element[][]{new Element[]{new NumberZ(1)},new Element[]{new NumberZ(1),new NumberZ(2)}},
                                new int[][]{new int[]{0},new int[]{0,1}});
        MatrixS ds = Matrix_multiply.concatMatrixS(new MatrixS[]{A,D}, false);
        System.out.println("ds="+ds);
    }
}
