package com.mathpar.parallel.dap.multiply.MatrixS;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;

public class MultiplyExtendedSWin extends MatrSMultStrassWin7 {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MultiplyExtendedSWin.class);
    private static int[][] _arcs = new int[][]{
            {3, 0, 0, 4, 1, 0, 2, 2, 1, 5, 3, 1,
                    1, 4, 0, 1, 5, 1, 2, 6, 0, 3, 7, 1,
                    4, 8, 1, 5, 9, 0, 6, 10, 0, 6, 11, 1,
                    7, 12, 0, 7, 13, 1, 8, 14 ,7 , 8, 15 ,8 }, // input function
            {8, 0, 0}, // P1
            {8, 0, 1}, // P2
            {8, 0, 2}, // P3
            {8, 0, 3}, // P4
            {8, 0, 4}, // P5
            {8, 0, 5}, // P6
            {8, 0, 6}, // P7
            {} // output function
    };

    public MultiplyExtendedSWin() {
        inData =  new Element[2];
        outData =  new Element[2];
        arcs = _arcs;
        type = 9;
        resultForOutFunctionLength = 9;
        inputDataLength = 2;
        number = cnum++;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        // LOGGER.info("in sequentialCalc indata = " + inData[0] + ",  "+inData[1]);
        MatrixS b = ((MatrixS) inData[0]).transpose();
        // LOGGER.info("b = " +b);
        outData[1] = b;
        MatrixS bbT =  b.multiply((MatrixS) inData[0], ring);
        // LOGGER.info("bbT= " + bbT);
        outData[0] = ((MatrixS)inData[1]).subtract(bbT, ring);
        // LOGGER.info(" outData[0] = " + outData[0]);
    }

    @Override
    //input key : 1 - full, 2 - main, 3- notmain
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring r) {

        MatrixS[] inputMatr = new MatrixS[16];

            MatrixS ms = ((MatrixS) input[0]).transpose();
            MatrixS ms1 = (MatrixS) input[0];

            MatrixS[] aSplit = ms.split();
            MatrixS A11 = aSplit[0];
            MatrixS A12 = aSplit[1];
            MatrixS A21 = aSplit[2];
            MatrixS A22 = aSplit[3];

            MatrixS[] bSplit = ms1.split();
            MatrixS B11 = bSplit[0];
            MatrixS B12 = bSplit[1];
            MatrixS B21 = bSplit[2];
            MatrixS B22 = bSplit[3];


            inputMatr[0] = A11;
            inputMatr[1] = A22;
            inputMatr[2] = B11;
            inputMatr[3] = B22;
            inputMatr[4] = A11.add(A22, r); // S1
            inputMatr[5] = B11.add(B22, r); // S2
            inputMatr[6] = A21.add(A22, r); // S3
            inputMatr[7] = B12.subtract(B22, r); // S4
            inputMatr[8] = B21.subtract(B11, r); // S5
            inputMatr[9] = A11.add(A12, r); // S6
            inputMatr[10] = A21.subtract(A11, r); // S7
            inputMatr[11] = B11.add(B12, r); // S8
            inputMatr[12] = A12.subtract(A22, r); // S9
            inputMatr[13] = B21.add(B22, r); // S10
            inputMatr[14] = ms;
            inputMatr[15] = (MatrixS) input[1];

        return inputMatr;
    }

    @Override
    public MatrixS[] outputFunction(Element[] input, Ring r) {

        MatrixS P1 = (MatrixS) input[0];
        MatrixS P2 = (MatrixS) input[1];
        MatrixS P3 = (MatrixS) input[2];
        MatrixS P4 = (MatrixS) input[3];
        MatrixS P5 = (MatrixS) input[4];
        MatrixS P6 = (MatrixS) input[5];
        MatrixS P7 = (MatrixS) input[6];
        MatrixS b = (MatrixS)input[7];
        MatrixS delta = (MatrixS)input[8];

        MatrixS[] C_matrix = new MatrixS[]{
                P1.add(P4, r).subtract(P5, r).add(P7, r), // C11
                P3.add(P5, r), // C12
                P2.add(P4, r), // C21
                P1.subtract(P2, r).add(P3, r).add(P6, r) // C22
        };


        MatrixS[] res = new MatrixS[] {delta.add(MatrixS.join(C_matrix).negate(r),r), b};
        // LOGGER.info("res in outputFunction = " + res[0]);
        return res;

    }
}
