package com.mathpar.parallel.dap.cholesky.MatrixD;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.MatrixD.MatrDMultStrassWin7;
import com.mathpar.parallel.dap.multiply.MatrixD.MultiplyExtendedDWin;

import java.util.ArrayList;

public class MatrDCholFactStrassWin7 extends Drop {

    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrDCholFactStrassWin7.class);
    private static int[][] _arcs = new int[][]{
            {1, 0, 0, 2, 1, 1, 3, 2, 1},//0. inputFunction
            {7, 0, 0, 2, 1, 0, 5, 1, 1, 7, 1, 3},//1. Cholesky()
            {3, 0, 0},//2. Multiply()
            {4, 0, 0, 7, 1, 1, 5, 1, 0},//3. MultiplyExtended()
            {7, 0, 2, 7, 1, 5, 6, 1, 0},//4. Cholesky()
            {6, 0, 1},//5. Multiply()
            {7, 0, 4}, //6. MultiplyMinus()
            {}};//7. OutputFunction

    public MatrDCholFactStrassWin7() {
        inData = new Element[1];
        outData = new Element[2];
        arcs = _arcs;
        type = 22;
        resultForOutFunctionLength = 6;
        inputDataLength = 1;
        number = cnum++;
    }


    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<Drop>();
        amin.add(new MatrDCholFactStrassWin7());
        amin.add(new MatrDMultStrassWin7());
        amin.get(1).key = 0;
        amin.add(new MultiplyExtendedDWin());
        amin.add(new MatrDCholFactStrassWin7());
        amin.add(new MatrDMultStrassWin7());
        amin.get(4).key = 0;
        amin.add(new MatrDMultStrassWin7());
        amin.get(5).key = 1;

        return amin;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        MatrixD ms = (MatrixD) inData[0];
        MatrixD[] cholresult = ms.choleskyFactorize(ring);
        outData[0] = cholresult[0];
        outData[1] = cholresult[1];
    }

    @Override
    public MatrixD[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixD ms = (MatrixD) input[0];
        MatrixD[] blocks = ms.splitTo4();
        MatrixD[] res = new MatrixD[3];
        res[0] = blocks[0];
        res[1] = blocks[1];
        res[2] = blocks[3];
        return res;
    }

    @Override
    public Element[] outputFunction(Element[] input, Ring ring) {
        MatrixD[] resCh = new MatrixD[4];
        resCh[0] = (MatrixD) input[0];
        resCh[1] = MatrixD.zeroMatrixD(resCh[0].M.length, resCh[0].M.length, ring);
        // LOGGER.info("zeroMatrix = " + resCh[1]);
        resCh[2] = (MatrixD) input[1];
        resCh[3] = (MatrixD) input[2];

        MatrixD[] resInv = new MatrixD[4];

        resInv[0] = (MatrixD) input[3];
        resInv[1] = MatrixD.zeroMatrixD(resInv[0].M.length, resInv[0].M.length, ring);
        resInv[2] = (MatrixD) input[4];
        resInv[3] = (MatrixD) input[5];

        MatrixD[] res = new MatrixD[]{MatrixD.join(resCh), MatrixD.join(resInv)};
        return res;

    }

    @Override
    public boolean isItLeaf() {
        MatrixD ms = (MatrixD)inData[0];
        return (ms.M.length <= leafSize);
    }

}
