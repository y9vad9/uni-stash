package com.mathpar.parallel.dap.cholesky.MatrixS;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.MatrixS.MatrSMult4;

import java.util.ArrayList;

public class MatrSCholFact4 extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSCholFact4.class);
    private static int[][] _arcs = new int[][]{
            {1, 0, 0, 2, 1, 1, 3, 2, 1},//0. inputFunction
            {7, 0, 0, 2, 1, 0, 5, 1, 1, 7, 1, 3},//1. Cholesky()
            {3, 0, 0},//2. Multiply()
            {4, 0, 0, 7, 1, 1, 5, 1, 0},//3. MultiplyExtended()
            {7, 0, 2, 7, 1, 5, 6, 1, 0},//4. Cholesky()
            {6, 0, 1},//5. Multiply()
            {7, 0, 4}, //6. MultiplyMinus()
            {}};//7. OutputFunction

    public MatrSCholFact4() {
        inData = new Element[1];
        outData = new Element[2];
        arcs = _arcs;
        type = 19;
        resultForOutFunctionLength = 6;
        inputDataLength = 1;
        number = cnum++;
    }


    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<Drop>();
        amin.add(new MatrSCholFact4());
        amin.add(new MatrSMult4());
        amin.get(1).key = 0;
        //amin.add(new MultiplyExtendedS4());
        amin.add(new MatrSMult4());
        amin.get(2).key = 2;
        amin.add(new MatrSCholFact4());
        amin.add(new MatrSMult4());
        amin.get(4).key = 0;
        amin.add(new MatrSMult4());
        amin.get(5).key = 1;

        return amin;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        MatrixS ms = (MatrixS) inData[0];
        MatrixS[] cholresult = ms.choleskyFactorize(ring);
        outData[0] = cholresult[0];
        outData[1] = cholresult[1];
    }

    @Override
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixS ms = (MatrixS) input[0];
        MatrixS[] blocks = ms.split();
        MatrixS[] res = new MatrixS[3];
        res[0] = blocks[0];
        res[1] = blocks[1];
        res[2] = blocks[3];
        return res;
    }

    @Override
    public Element[] outputFunction(Element[] input, Ring ring) {
        MatrixS[] resCh = new MatrixS[4];
        resCh[0] = (MatrixS) input[0];
        resCh[1] = MatrixS.zeroMatrix(resCh[0].size);
        // LOGGER.info("zeroMatrix = " + resCh[1]);
        resCh[2] = (MatrixS) input[1];
        resCh[3] = (MatrixS) input[2];

        MatrixS[] resInv = new MatrixS[4];

        resInv[0] = (MatrixS) input[3];
        resInv[1] = MatrixS.zeroMatrix(resInv[0].size);
        resInv[2] = (MatrixS) input[4];
        resInv[3] = (MatrixS) input[5];

        MatrixS[] res = new MatrixS[]{MatrixS.join(resCh), MatrixS.join(resInv)};
        return res;

    }


}
