package com.mathpar.parallel.dap.multiply.MatrixD;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;


import java.util.ArrayList;

public class MatrDMultStrassWin7  extends Drop {
    private final static int[][] _arcs = {
            {       3, 0, 0,    4, 1, 0,    2, 2, 1,    5, 3, 1,
                    1, 4, 0,    1, 5, 1,    2, 6, 0,    3, 7, 1,
                    4, 8, 1,    5, 9, 0,    6, 10, 0,   6, 11, 1,
                    7, 12, 0,   7, 13, 1}, // input function
            {8, 0, 0}, // P1
            {8, 0, 1}, // P2
            {8, 0, 2}, // P3
            {8, 0, 3}, // P4
            {8, 0, 4}, // P5
            {8, 0, 5}, // P6
            {8, 0, 6}, // P7
            {} // output function
    };

    public MatrDMultStrassWin7() {
        inData = new Element[2];
        outData = new Element[1];
        type = 11;
        resultForOutFunctionLength = 7;
        inputDataLength = 2;
        number = cnum++;
        arcs = _arcs;
    }

    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<>();
        amin.add(new MatrDMultStrassWin7()); // P1
        amin.add(new MatrDMultStrassWin7()); // P2
        amin.add(new MatrDMultStrassWin7()); // P3
        amin.add(new MatrDMultStrassWin7()); // P4
        amin.add(new MatrDMultStrassWin7()); // P5
        amin.add(new MatrDMultStrassWin7()); // P6
        amin.add(new MatrDMultStrassWin7()); // P7
        return amin;
    }

    @Override
    public void sequentialCalc(Ring r) {
        MatrixD A = (MatrixD) inData[0];
        MatrixD B = (MatrixD) inData[1];
        MatrixD C = A.multiplyMatr(B, r);

        switch (key){
            case(0): outData[0] = C; break;
            case(1): outData[0] = C.negate(r); break;
        }
    }

    @Override
    public MatrixD[] inputFunction(Element[] input, Amin amin, Ring r) {
        MatrixD[] aSplit = ((MatrixD) input[0]).splitTo4();
        MatrixD A11 = aSplit[0];
        MatrixD A12 = aSplit[1];
        MatrixD A21 = aSplit[2];
        MatrixD A22 = aSplit[3];

        MatrixD[] bSplit = ((MatrixD) input[0]).splitTo4();
        MatrixD B11 = bSplit[0];
        MatrixD B12 = bSplit[1];
        MatrixD B21 = bSplit[2];
        MatrixD B22 = bSplit[3];

        MatrixD[] inputMatr = new MatrixD[14];
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

        MatrixD[] C_matrix = new MatrixD[]{
                P1.add(P4, r).subtract(P5, r).add(P7, r), // C11
                P3.add(P5, r), // C12
                P2.add(P4, r), // C21
                P1.subtract(P2, r).add(P3, r).add(P6, r) // C22
        };

        MatrixD[] res;
        if (key == 0) res = new MatrixD[]{MatrixD.join(C_matrix)};
        else
            res = new MatrixD[]{MatrixD.join(C_matrix).negate(r)};
        return res;

    }

    @Override
    public boolean isItLeaf() {
        return ((MatrixD) inData[0]).M.length <= leafSize;
    }

}
