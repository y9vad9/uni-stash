package com.mathpar.parallel.dap.multiply.MatrixD;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;

public class MultiplyExtendedDWin extends MatrDMultStrassWin7 {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MultiplyExtendedDWin.class);
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

    public MultiplyExtendedDWin() {
        inData =  new Element[2];
        outData =  new Element[2];
        arcs = _arcs;
        type = 14;
        resultForOutFunctionLength = 9;
        inputDataLength = 2;
        number = cnum++;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        // LOGGER.info("in sequentialCalc indata = " + inData[0] + ",  "+inData[1]);
        MatrixD b = ((MatrixD) inData[0]).transpose(ring);
        // LOGGER.info("b = " +b);
        outData[1] = b;
        MatrixD bbT =  b.multiplyMatr((MatrixD) inData[0], ring);
        // LOGGER.info("bbT= " + bbT);
        outData[0] = ((MatrixD) inData[1]).subtract(bbT, ring);
        // LOGGER.info(" outData[0] = " + outData[0]);
    }

    @Override
    public MatrixD[] inputFunction(Element[] input, Amin amin, Ring r) {

        MatrixD[] inputMatr = new MatrixD[16];

            MatrixD ms = ((MatrixD) input[0]).transpose(r);
            MatrixD ms1 = (MatrixD) input[0];

            MatrixD[] aSplit = ms.splitTo4();
            MatrixD A11 = aSplit[0];
            MatrixD A12 = aSplit[1];
            MatrixD A21 = aSplit[2];
            MatrixD A22 = aSplit[3];

            MatrixD[] bSplit = ms1.splitTo4();
            MatrixD B11 = bSplit[0];
            MatrixD B12 = bSplit[1];
            MatrixD B21 = bSplit[2];
            MatrixD B22 = bSplit[3];

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

                inputMatr[15] = (MatrixD) input[1];
           
        return inputMatr;
    }

    @Override
    public MatrixD[] outputFunction(Element[] input, Ring r) {

        MatrixD P1 = (MatrixD) input[0];
        MatrixD P2 = (MatrixD) input[1];
        MatrixD P3 = (MatrixD) input[2];
        MatrixD P4 = (MatrixD) input[3];
        MatrixD P5 = (MatrixD) input[4];
        MatrixD P6 = (MatrixD) input[5];
        MatrixD P7 = (MatrixD) input[6];
        MatrixD b = (MatrixD) input[7];
        MatrixD delta = (MatrixD) input[8];

        MatrixD[] C_matrix = new MatrixD[]{
                P1.add(P4, r).subtract(P5, r).add(P7, r), // C11
                P3.add(P5, r), // C12
                P2.add(P4, r), // C21
                P1.subtract(P2, r).add(P3, r).add(P6, r) // C22
        };


        MatrixD[] res = new MatrixD[] {delta.add(MatrixD.join(C_matrix).negate(r),r), b};
        // LOGGER.info("res in outputFunction = " + res[0]);
        return res;

    }
}
