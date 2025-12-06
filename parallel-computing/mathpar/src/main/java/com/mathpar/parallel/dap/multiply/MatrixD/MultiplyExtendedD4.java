package com.mathpar.parallel.dap.multiply.MatrixD;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;

public class MultiplyExtendedD4 extends MatrDMult4 {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MultiplyExtendedD4.class);
    private static int[][] _arcs = new int[][] {{1, 0, 0, 1, 4, 1, 1, 1, 2, 1, 6, 3, 2, 0, 0, 2, 5, 1, 2, 1, 2, 2, 7, 3,
            3, 2, 0, 3, 4, 1, 3, 3, 2, 3, 6, 3, 4, 2, 0, 4, 5, 1, 4, 3, 2, 4, 7, 3, 5, 8, 4, 5, 9, 5},
            {5, 0, 0},
            {5, 0, 1},
            {5, 0, 2},
            {5, 0, 3}, {}};

    public MultiplyExtendedD4() {
        inData =  new Element[2];
        outData =  new Element[2];
        arcs = _arcs;
        type = 13;
        resultForOutFunctionLength = 6;
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
        outData[0] = ((MatrixD)inData[1]).subtract(bbT, ring);
        // LOGGER.info(" outData[0] = " + outData[0]);
    }

    @Override

    public MatrixD[] inputFunction(Element[] input, Amin amin, Ring ring) {

        MatrixD[] res = new MatrixD[10];

            MatrixD ms = ((MatrixD) input[0]).transpose(ring);
            MatrixD ms1 = (MatrixD) input[0];

            Array.concatTwoArrays(ms.splitTo4(), ms1.splitTo4(), res);
            res[8] = ms;
            res[9] = (MatrixD) input[1];

        return res;
    }

    @Override
    public MatrixD[] outputFunction(Element[] input, Ring ring) {

        //LOGGER.info("input length = " + input.length);
        MatrixD delta = (MatrixD)input[5];
        MatrixD b = (MatrixD)input[4];
        MatrixD[] resmat = new MatrixD[input.length];
        for (int i = 0; i < input.length; i++) {
            resmat[i] = (MatrixD) input[i];
        }
        MatrixD[] res = new MatrixD[] {delta.add(MatrixD.join(resmat).negate(ring),ring), b};
        // LOGGER.info("res in outputFunction = " + res[0]);
        return res;

    }
}
