package com.mathpar.parallel.dap.inverseTriangle.MatrixS;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.MatrixS.MatrSMultStrassWin7;

import java.util.ArrayList;

public class MatrSTriangInvStrassWin7 extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSTriangInvStrassWin7.class);
    private static int[][] _arcs = new int[][]{
            {1, 0, 0, 2, 2, 0, 3, 1, 0},
            {3, 0, 1, 5, 0, 0}, // Inversion
            {4, 0, 0, 5, 0, 2}, // Inversion
            {4, 0, 1}, // Multiply
            {5, 0, 1}, // MultiplyMinus
            {}
    };

    public MatrSTriangInvStrassWin7() {
        inData = new Element[1];;
        outData = new Element[1];;
        arcs = _arcs;
        type = 16;
        resultForOutFunctionLength = 3;
        inputDataLength = 1;
        number = cnum++;
    }

    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<>();
        amin.add(new MatrSTriangInvStrassWin7());
        amin.add(new MatrSTriangInvStrassWin7());
        amin.add(new MatrSMultStrassWin7());
        amin.add(new MatrSMultStrassWin7());
        amin.get(3).key = 1;

        return amin;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        outData[0] = ((MatrixS) inData[0]).inverseLowTriangle(ring);
    }

    @Override
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixS ms = (MatrixS) input[0];
        MatrixS[] blocks = ms.split();
        MatrixS[] res = new MatrixS[3];
        res[0] = blocks[0];
        res[1] = blocks[2];
        res[2] = blocks[3];

        return res;
    }

    @Override
    public Element[] outputFunction(Element[] input, Ring ring) {
        MatrixS[] resInv = new MatrixS[4];

        resInv[0] = (MatrixS) input[0];
        resInv[1] = MatrixS.zeroMatrix(resInv[0].size);
        resInv[2] = (MatrixS) input[1];
        resInv[3] = (MatrixS) input[2];

        return new MatrixS[]{MatrixS.join(resInv)};
    }
}
