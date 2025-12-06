/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.tatarintsev;

import java.util.Random;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;

/**
 *
 * @author heckfy
 */
public class Test {
    //final static JFrame f = new JFrame();
    public static void main(String[] args) {
        Ring r = new Ring("R64[x]");
        Random num = new Random();
        //Генератор случайной матрицы вида MatrixD и вызов метода, который ее рисует.
        MatrixD A = new MatrixD(20, 20, 100, new int[] {7}, num, r);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                A.M[i][j]=A.M[i][j].subtract(new NumberR64("0.50"),r);

            }
        }
        System.out.println(A);
        DrawMatrix dm = new DrawMatrix(A, 800, 600);
        dm.draw();

        //Генератор случайной матрицы вида MatrixS и вызов метода, который ее рисует.
        //MatrixS B = new MatrixS(1000, 1000, 10, new int[] {7}, num, r.numberONE, r);
        //System.out.println("B=" + B);
        //plgraphicsS(B, r);
    }
}
