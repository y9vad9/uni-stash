package com.mathpar.parallel.dap.QR;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVar;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVarConfig;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVarMatrix;

import java.util.ArrayList;

public class QRDecomposition extends Drop {
    private static int[][] _arcs = new int[][]{
            {2,0,0, 4,1,4, 1,2,0, 4,3,6}, // input
            {3,0,3, 2,1,1}, // QR1
            {3,0,4, 7,1,1}, // QP
            {6,0,0, 4,0,0}, // Multiply
            {7,1,2, 5,3,0}, // Multiply
            {6,0,7, 7,1,3}, // QR2
            {7,0,0}, // Multiply
            {}
    };

    public QRDecomposition() {
        inData =  new Element[1];
        outData =  new Element[2];
        inputDataLength = 1;
        resultForOutFunctionLength = 4;
        arcs = _arcs;

        type = 4;
    }

    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<>();

        amin.add(new QRDecomposition());
        amin.add(new QPDecomposition());
        amin.add(new MultiplyVar(
                MultiplyVarConfig.Builder.startWith()
                .multiplicationBlock()
                .setFirst(MultiplyVarMatrix.Builder.newBuilder().addQuarter(MultiplyVarMatrix.Quarter.IV).set().done().build())
                .setSecond(MultiplyVarMatrix.Builder.newBuilder().addFull().transpose().set().done().build())
                .build()
        ));
        amin.add(new MultiplyVar(
                MultiplyVarConfig.Builder.startWith()
                .multiplicationBlock()
                .setFirst(MultiplyVarMatrix.Builder.newBuilder().addFull().transpose().set().done().build())
                .setSecond(
                        MultiplyVarMatrix.Builder.newBuilder()
                        .addQuarter(MultiplyVarMatrix.Quarter.I).set().done()
                        .addQuarter(MultiplyVarMatrix.Quarter.III).set().done()
                        .build()
                )
                .build()
        ));
        amin.add(new QRDecomposition());
        amin.add(new MultiplyVar(
                MultiplyVarConfig.Builder.startWith()
                .multiplicationBlock()
                .setFirst(MultiplyVarMatrix.Builder.newBuilder().addFull().set().done().build())
                .setSecond(MultiplyVarMatrix.Builder.newBuilder().addQuarter(MultiplyVarMatrix.Quarter.IV).set().done().build())
                .build()
        ));

        return amin;
    }

    @Override
    public void sequentialCalc(Ring ring) {

        MatrixS source = (MatrixS) inData[0];

        MatrixS[] QR = SeqBlockQR.compute(source, ring);

        outData[0] = QR[0];
        outData[1] = QR[1];
    }

    @Override
    public Element[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixS matrix = (MatrixS) input[0];

        return matrix.split();
    }

    @Override
    public Element[] outputFunction(Element[] input, Ring ring) {
        MatrixS[] result = new MatrixS[2];
        result[0] = (MatrixS) input[0];

        MatrixS[] R = new MatrixS[4];
        R[0] = (MatrixS) input[1];
        R[1] = (MatrixS) input[2];
        R[3] = (MatrixS) input[3];
        R[2] = MatrixS.zeroMatrix(R[0].size);

        result[1] = MatrixS.join(R);

        return result;
    }
}
