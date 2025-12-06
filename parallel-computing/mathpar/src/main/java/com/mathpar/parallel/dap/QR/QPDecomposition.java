package com.mathpar.parallel.dap.QR;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVarMatrix;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVar;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVarConfig;

import java.util.ArrayList;

public class QPDecomposition extends Drop {
    private static int num = 0;
    private static int[][] _args = new int[][]{
            {3,0,0, 5,1,4, 1,2,0, 2,3,4, 1,4,1, 2,5,6, 4,7,1}, // input
            {2,0,0, 6,0,4, 3,1,1}, // QP1
            {5,1,6, 4,3,0}, // MultiplyVar
            {5,0,0, 8,0,0, 10,1,1}, // QP2
            {6,0,3, 7,1,1}, // QP3
            {10,1,2, 7,3,0}, // MultiplyVar
            {8,0,4}, // MultiplyVar
            {9,0,0, 10,1,3}, // QP4
            {9,0,4}, // MultiplyVar
            {10,0,0}, // MultiplyVar
            {}
    };

    public QPDecomposition() {
        inData =  new Element[2];
        outData =  new Element[2];
        resultForOutFunctionLength = 4;
        inputDataLength = 2;

        arcs = _args;

        type = 3;

        number = num++;
    }

    @Override
    public ArrayList<Drop> doAmin() {

        ArrayList<Drop> amin = new ArrayList<>();

        amin.add(new QPDecomposition());
        amin.add(new MultiplyVar(
            MultiplyVarConfig.Builder.startWith()
            .multiplicationBlock()
            .setFirst(MultiplyVarMatrix.Builder.newBuilder().addFull().set().done().build())
            .setSecond(MultiplyVarMatrix.Builder.newBuilder()
                    .addQuarter(MultiplyVarMatrix.Quarter.I).set().done()
                    .addQuarter(MultiplyVarMatrix.Quarter.III).set().done()
                    .build()
            )
            .build()
        ));
        amin.add(new QPDecomposition());
        amin.add(new QPDecomposition());
        amin.add(new MultiplyVar(
            MultiplyVarConfig.Builder.startWith()
                    .multiplicationBlock()
                    .setFirst(MultiplyVarMatrix.Builder.newBuilder().addFull().set().done().build())
                    .setSecond(
                            MultiplyVarMatrix.Builder.newBuilder()
                                    .addQuarter(MultiplyVarMatrix.Quarter.I).set().done()
                                    .addQuarter(MultiplyVarMatrix.Quarter.III).set().done()
                                    .build()
                    )
                    .build()
        ));
        amin.add(new MultiplyVar(
            MultiplyVarConfig.Builder.startWith()
                    .multiplicationBlock()
                    .setFirst(MultiplyVarMatrix.Builder.newBuilder().addQuarter(MultiplyVarMatrix.Quarter.IV).set().done().build())
                    .setSecond(MultiplyVarMatrix.Builder.newBuilder().addCenter().set().done().build())
                    .build()
        ));
        amin.add(new QPDecomposition());
        amin.add(new MultiplyVar(
            MultiplyVarConfig.Builder.startWith()
                    .multiplicationBlock()
                    .setFirst(MultiplyVarMatrix.Builder.newBuilder().addQuarter(MultiplyVarMatrix.Quarter.I).set().done().build())
                    .setSecond(MultiplyVarMatrix.Builder.newBuilder().addFull().set().done().build())
                    .build()
        ));
        amin.add(new MultiplyVar(
            MultiplyVarConfig.Builder.startWith()
                    .multiplicationBlock()
                    .setFirst(MultiplyVarMatrix.Builder.newBuilder().addCenter().set().done().build())
                    .setSecond(MultiplyVarMatrix.Builder.newBuilder().addFull().set().done().build())
                    .build()
        ));

        return amin;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        MatrixS A = (MatrixS) inData[0];
        MatrixS B = (MatrixS) inData[1];

        MatrixS[] QP = SeqBlockQR.computeQP(A, B, ring);

        outData[0] = QP[0];
        outData[1] = QP[1];
    }

    @Override
    public Element[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixS[] result = new MatrixS[8];
        MatrixS[] matrix1Split = ((MatrixS) input[0]).split();
        MatrixS[] matrix2Split = ((MatrixS) input[1]).split();

        Array.concatTwoArrays(matrix1Split, matrix2Split, result);

        return result;
    }

    @Override
    public Element[] outputFunction(Element[] input, Ring ring) {
        MatrixS[] result = new MatrixS[2];

        result[0] = (MatrixS) input[0];

        MatrixS[] R = new MatrixS[4];
        R[0] = (MatrixS) input[1];
        R[1] = (MatrixS) input[2];
        R[2] = MatrixS.zeroMatrix(R[0].size);
        R[3] = (MatrixS) input[3];

        result[1] = MatrixS.join(R);

        return result;
    }
}
