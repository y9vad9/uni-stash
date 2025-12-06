
package com.mathpar.matrix.file.sparse;


import com.mathpar.matrix.MatrixS;
import java.util.Random;
import com.mathpar.number.NumberZ;
import com.mathpar.matrix.AdjMatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TestAdjMS {
    public static void main(String[] args) {Ring ring=Ring.ringZxyz;
        Random rnd = new Random(12345);
        int N = 32;
        //ms
        NumberZ zero = NumberZ.ZERO;
        NumberZ one = NumberZ.ONE;
        MatrixS ms = randomMS(N, N, 5000, new int[] {2}, rnd, one, zero, ring);
        //msRight
        AdjMatrixS adjRight = new AdjMatrixS(ms, one,ring);
        checkAdjMSResult(ms, adjRight.A, adjRight.S, ring);
    }




    private static void checkAdjMSResult(MatrixS m, MatrixS A, MatrixS S, Ring ring) {
        if (!A.multiply(m, ring).isEqual(S,ring)) {
            throw new RuntimeException("A*m != S");
        }
    }




    private static MatrixS randomMS(int r, int c, int density,
                                    int[] randomType,
                                    Random ran,
                                    Element one,
                                    Element zero, Ring ring) {
        return new MatrixS(randomScalarArr2d(r, c, density, randomType, ran,
                                             one, zero,ring),ring);
    }




    private static Element[][] randomScalarArr2d(int r, int c, int density,
                                                int[] randomType,
                                                Random ran,
                                                Element one,
                                                Element zero, Ring ring) {
        int m1;
        Element[][] M = new Element[r][c];
        if (density == 10000) {
            for (int i = 0; i < r; i++)
                for (int j = 0; j < c; j++)
                    M[i][j] = one.random(randomType, ran, ring);
            return M;
        }
        for (int i = 0; i <= r - 1; i++) {
            for (int j = 0; j <= c - 1; j++) {
                m1 = (Math.round(ran.nextFloat() * 10000) /
                      (10000 - density + 1));
                if (m1 == 0) {
                    M[i][j] = zero;
                } else {
                    M[i][j] = one.random(randomType, ran, ring);
                }
            }
        }
        return M;
    }
}
