package com.mathpar.parallel.dap.inverseTriangle.MatrixD;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.MatrixD.MatrDMultStrassWin7;

import java.util.ArrayList;

public class MatrDTriangInvStrassWin7 extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrDTriangInvStrassWin7.class);
    private static int[][] _arcs = new int[][]{
            {1, 0, 0, 2, 2, 0, 3, 1, 0},
            {3, 0, 1, 5, 0, 0}, // Inversion
            {4, 0, 0, 5, 0, 2}, // Inversion
            {4, 0, 1}, // Multiply
            {5, 0, 1}, // MultiplyMinus
            {}
    };

    public MatrDTriangInvStrassWin7() {
        inData = new Element[1];
        outData = new Element[1];
        arcs = _arcs;
        type = 18;
        resultForOutFunctionLength = 3;
        inputDataLength = 1;
        number = cnum++;
    }

    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<>();
        amin.add(new MatrDTriangInvStrassWin7());
        amin.add(new MatrDTriangInvStrassWin7());
        amin.add(new MatrDMultStrassWin7());
        amin.add(new MatrDMultStrassWin7());
        amin.get(3).key = 1;

        return amin;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        outData[0] = ((MatrixD) inData[0]).inverseLowTriangle(ring);
    }

    @Override
    public MatrixD[] inputFunction(Element[] input, Amin amin, Ring ring) {

        MatrixD ms = (MatrixD) input[0];
        MatrixD[] blocks = ms.splitTo4();
        MatrixD[] res = new MatrixD[3];
        res[0] = blocks[0];
        res[1] = blocks[2];
        res[2] = blocks[3];

        return res;
    }

    @Override
    public Element[] outputFunction(Element[] input, Ring ring) {
        MatrixD[] resInv = new MatrixD[4];

        resInv[0] = (MatrixD) input[0];
        resInv[1] = MatrixD.zeroMatrixD(resInv[0].M.length, resInv[0].M.length, ring);
        resInv[2] = (MatrixD) input[1];
        resInv[3] = (MatrixD) input[2];

        return new MatrixD[]{MatrixD.join(resInv)};
    }

    @Override
    public boolean isItLeaf() {
        return (((MatrixD) inData[0]).M.length <= leafSize);
    }

}
