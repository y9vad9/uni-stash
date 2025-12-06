package com.mathpar.parallel.dap.adjmatrix.MatrixS;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.AdjMatrixS;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.cholesky.MatrixS.MatrSCholFact4;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.MatrixS.MatrSMult4;

import java.util.ArrayList;

public class MatrSAdjMatrix extends Drop {

    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSAdjMatrix.class);
    private static int[][] _arcs = new int[][]{
            {1,0,0,  2,2,0,  3,1,1,  5,3,0,  5,2,2,  14,2,0,
                    /* todo dependency on d0 and finalN*/ 1,4,1,  2,4,2,  3,4,2,  3,5,3,  5,4,5,  12,5,6,  14,4,2},
            // 0 inputFunction; I(M, d0), O( M[0], M[1], M[2], M[3], d0, finalN)
            {2,1,1,  3,0,0,  4,2,1,  5,2,1,  5,0,4,  6,2,1,  7,0,0,  7,2,2,  8,0,1,  10,0,0,
                    10,2,2,  11,2,3,  12,2,2,  14,0,1,  14,2,5,  16,0,2,  16,2,4,  17,0,2,  17,2,4,  23,0,0,  23,2,2,  26,0,10},
            // 1, here is done y11; I( M[0], d0  ), O ( m11, y11, d11 )
            {4,0,0  }, // 2; I( M[2], y11, d0 ), O ( M21_1 )
            {5,0,3,  6,1,0,  16,0,1,  17,0,1}, // 3; I( m11, M[1], d0, finalN ), O ( M12_1, M12_2 )
            {7,1,1,  9,0,0,  10,0,1,  12,2,3,  12,0,5,  16,2,3,  19,0,1,  20,0,1,  22,0,2,  22,2,5,  23,0,1,  23,2,5,  26,2,6,  26,0,8},
            // 4; I( M21_1, d11 ), O ( m21, y21, d21 )
            {9,0,1,  11,0,0,  13,0,1}, // 5; I( M[3], d11, M[2], M12_1, m11, d0 ), O ( M22_1 )
            {8,0,0,  11,0,2,  12,1,1,  12,2,4,  14,2,3,  16,1,5,  16,0,6,  17,0,3,  24,2,2,  25,2,3,  26,0,11}, // 6; I( M12_2, d11 ), O ( m12, y12, d12 )
            {26,0,4}, // 7; I( m11, y21, d11 ), O ( M11_2 )
            {11,0,1,  17,0,0}, // 8; I( m12, m11 ), O ( A1 )
            {12,0,0}, // 9; I( m21, M22_1 ), O ( B )
            {13,0,0}, // 10; I( m11, m21, d11 ), O ( Q )
            {14,0,4}, // 11; I( M22_1, A1, m12, d11 ), O ( K2 )
            {15,2,0,  15,1,1,  18,1,2,  19,0,0,  19,1,3,  21,1,3,  22,0,1,  22,1,4}, // 12; I( B, y12, d11, d21, d12, m21, finalN ), O ( M22_2, ds, M22_3 )
            {16,0,0}, // 13; I( Q, M22_1 ), O ( Q1 )
            {24,0,1,  25,0,2}, // 14; I( M[2], m11, d0, d12, K2, d11 ), O ( G )
            {17,2,5,  18,1,1,  19,1,2,  19,0,4,  20,0,0,  21,0,2,  22,0,3,  23,2,3,  26,2,5,  26,0,12}, // 15; I( M22_3, ds ), O ( m22,y22,d22 )
            {18,0,0,  21,0,0}, // 16; I( Q1, M12_1, m11, d21, d11, y12, m12 ), O ( M12_2_new )
            {25,0,0}, // 17; I( A1, M12_1, m11, m12, d11, d22 ), O ( L )
            {26,0,7}, // 18; I( M12_2_new , y22, ds ), O ( M12_3 )
            {26,0,9}, // 19; I( M22_2, m21, y22, ds, m22 ), O ( M22_3_new )
            {21,0,1,  22,0,0}, // 20; I( m22, m21 ), O ( A2 )
            {23,0,4}, // 21; I( M12_2_new, A2, m22, ds ), O ( K1 )
            {24,0,0,  26,0,3}, // 22; I( A2, M22_2, m21, m22, ds, d21 ), O ( P )
            {25,0,1,  26,0,1}, // 23; I( m11, m21, d11, d22, K1, d21 ), O ( F )
            {26,0,2}, // 24; I( P, G, d12 ), O ( P1 )
            {26,0,0}, // 25; I( L, F, G, d12 ), O ( F1 )
            {}, // 26 outputFunction; I( F1, F, P1, P, M11_2, d22, d21, M12_3, m21, M22_3_new, m11, m12, m22, finalN ), O ( m, y, d)
    };

    public MatrSAdjMatrix() {
        arcs = _arcs;
        type = 7701;
        number = cnum++;
        inputDataLength = 2;
        outputDataLength = 3;
        inData = new Element[inputDataLength];
        outData = new Element[outputDataLength];
        resultForOutFunctionLength = 14;
    }

    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<Drop>();
        // step 1
        amin.add(new MatrSAdjMatrix());
        amin.add(new MatrSMult4());
        amin.get(1).key = 7702;
        amin.add(new MatrSMult4());
        amin.get(2).key = 7703;
        // step 4
        amin.add(new MatrSAdjMatrix());
        amin.add(new MatrSMult4());
        amin.get(4).key = 7705;
        // step 6
        amin.add(new MatrSAdjMatrix());
        amin.add(new MatrSMult4());
        amin.get(6).key = 7707;
        amin.add(new MatrSMult4());
        amin.get(7).key = 7708;
        amin.add(new MatrSMult4());
        amin.get(8).key = 7709;
        amin.add(new MatrSMult4());
        amin.get(9).key = 7710;
        amin.add(new MatrSMult4());
        amin.get(10).key = 7711;
        amin.add(new MatrSMult4());
        amin.get(11).key = 7712;
        amin.add(new MatrSMult4());
        amin.get(12).key = 7713;
        amin.add(new MatrSMult4());
        amin.get(13).key = 7714;
        //step 15
        amin.add(new MatrSAdjMatrix());
        amin.add(new MatrSMult4());
        amin.get(15).key = 7716;
        amin.add(new MatrSMult4());
        amin.get(16).key = 7717;
        amin.add(new MatrSMult4());
        amin.get(17).key = 7718;
        amin.add(new MatrSMult4());
        amin.get(18).key = 7719;
        amin.add(new MatrSMult4());
        amin.get(19).key = 7720;
        amin.add(new MatrSMult4());
        amin.get(20).key = 7721;
        amin.add(new MatrSMult4());
        amin.get(21).key = 7722;
        amin.add(new MatrSMult4());
        amin.get(22).key = 7723;
        amin.add(new MatrSMult4());
        amin.get(23).key = 7724;
        amin.add(new MatrSMult4());
        amin.get(24).key = 7725;

        return amin;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        MatrixS m = (MatrixS) inData[0];
        Element d0 = inData[1];
        //LOGGER.info("in seqcalc det0 = " + d0);
        //LOGGER.info("in seqcalc m = " + m);
        AdjMatrixS adjM = new AdjMatrixS(m, d0,  ring);
        Element resD = adjM.Det;
        MatrixS y = adjM.S.ES_min_dI(resD, adjM.Ei, adjM.Ej, ring);
        //LOGGER.info("in seqcalc adj = " + adjM.A);
       // LOGGER.info("in seqcalc det = " + adjM.Det);
        outData[0] = adjM;
        outData[1] = y;
        outData[2] = resD;
    }

    @Override
    public Element[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixS m = (MatrixS) input[0];
        MatrixS[] splitted = m.split();
        Element[] res = new Element[6];
        System.arraycopy(splitted, 0, res, 0, 4);
        res[4] = input[1];
        // todo ???
        int N = m.size;
        int finalN = N >>> 1;
        res[5] = new Element(finalN);
        amin.resultForOutFunction[13] = new Element(finalN);
        return res;
    }

    @Override
    public Element[] outputFunction(Element[] input, Ring ring) {
        MatrixS[] aParts = new MatrixS[4];
        for (int i = 0; i < 4; i++) {
            aParts[i] = (MatrixS) input[i];
        }
        MatrixS A = MatrixS.join(aParts);

        MatrixS[] sParts = new MatrixS[4];
        MatrixS M11_2 = (MatrixS) input[4];
        Element d22 = input[5];
        Element d21 = input[6];
        MatrixS M12_3 = (MatrixS) input[7];
        AdjMatrixS m21 = (AdjMatrixS) input[8];
        MatrixS M22_3_new = (MatrixS) input[9];
        AdjMatrixS m11 = (AdjMatrixS) input[10];
        AdjMatrixS m12 = (AdjMatrixS) input[11];
        AdjMatrixS m22 = (AdjMatrixS) input[12];
        Element finalN = input[13];
        int N = (int) finalN.value;

        sParts[0] = M11_2.multiplyDivide(d22, d21, ring);
        sParts[1] = M12_3;
        sParts[2] = m21.S.multiplyDivide(d22, d21, ring);
        sParts[3] = M22_3_new;
        MatrixS S = MatrixS.join(sParts);

        int[] Ei = new int[m11.Ei.length+m12.Ei.length+m21.Ei.length+m22.Ei.length];
        int[] Ej = new int[Ei.length];
        int j=0;
        for (int i = 0; i < m11.Ei.length;) {Ei[j] = m11.Ei[i];
            Ej[j++] = m11.Ej[i++];  }
        for (int i = 0; i < m12.Ei.length;) {Ei[j] = m12.Ei[i];
            Ej[j++] = m12.Ej[i++]+N;}
        for (int i = 0; i < m21.Ei.length;) {Ei[j] = m21.Ei[i]  +N;
            Ej[j++] = m21.Ej[i++];  }
        for (int i = 0; i < m22.Ei.length;) {Ei[j] = m22.Ei[i]  +N;
            Ej[j++] = m22.Ej[i++]+N;}
        // output
        AdjMatrixS res = new AdjMatrixS(A, Ei, Ej, S, d22);
        Element d = res.Det;
        MatrixS y = res.S.ES_min_dI(d, res.Ei, res.Ej, ring);
        return new Element[] {res, y, d};
    }

}
