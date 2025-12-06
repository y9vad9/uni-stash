
package com.mathpar.matrix.file.sparse;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import java.util.Random;
import com.mathpar.number.NumberZ;
import com.mathpar.matrix.AdjMatrixS;
import com.mathpar.matrix.file.utils.BaseMatrixDir;
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
public class TestAdjSFMS {
    public static void main(String[] args) throws Exception {
        Ring ring=Ring.ringZxyz;
        BaseMatrixDir.clearMatrixDir();
        Random rnd=new Random(12345);
        int N=16;
        //ms
        NumberZ zero=NumberZ.ZERO;
        NumberZ one=NumberZ.ONE;
        MatrixS ms=randomMS(N,N,5000,new int[]{2},rnd,one,zero,ring);

        AdjSFMS adj2;
        SFileMatrixS sfms;
        //depth=0
        //ms-->sfms
        /*sfms=new SFileMatrixS(0,ms,zero);
        //adj2=op(sfms)
        adj2=new AdjSFMS(sfms,one,N,0);
        checkAdjSFMSResult(sfms,adj2.A,adj2.S);
        //adjRight=?=adj2
        assertEquals("adjRight!=adj2", true, adj2.equalsToAdjMS(adjRight));*/

        //depth=1
        //ms-->sfms
        sfms=new SFileMatrixS(1,ms,zero, ring);
        //adj2=op(sfms)
        adj2=new AdjSFMS(sfms,one,N,1,ring);
        checkAdjSFMSResult(sfms,adj2.A,adj2.S,ring);

        //depth=3
        //ms-->sfms
        sfms=new SFileMatrixS(3,ms,zero, ring);
        //adj2=op(sfms)
        adj2=new AdjSFMS(sfms,one,N,3,ring);
        checkAdjSFMSResult(sfms,adj2.A,adj2.S, ring);

    }

    private static void checkAdjSFMSResult(SFileMatrixS m, SFileMatrixS A, SFileMatrixS S, Ring ring)
        throws Exception{
        if (!A.multiplyRecursive(m, ring).equals(S)) {
            throw new RuntimeException("File: A*m != S");
        }
    }


    private static MatrixS randomMS(int r, int c, int density,
                                    int[] randomType,
                                    Random ran,
                                    Element one,
                                    Element zero,Ring ring) {
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
