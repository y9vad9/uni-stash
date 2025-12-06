package com.mathpar.parallel.dap.multiply.MatrixS;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.core.Drop;

import java.util.ArrayList;

public class MatrSMultStrassWin7 extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSMultStrassWin7.class);
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

    public MatrSMultStrassWin7() {
        inData =  new Element[2];
        outData =  new Element[1];
        type = 6;
        resultForOutFunctionLength = 7;
        inputDataLength = 2;
        outputDataLength = 1;
        number = cnum++;
        arcs = _arcs;
    }

    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<>();
        amin.add(new MatrSMultStrassWin7()); // P1
        amin.add(new MatrSMultStrassWin7()); // P2
        amin.add(new MatrSMultStrassWin7()); // P3
        amin.add(new MatrSMultStrassWin7()); // P4
        amin.add(new MatrSMultStrassWin7()); // P5
        amin.add(new MatrSMultStrassWin7()); // P6
        amin.add(new MatrSMultStrassWin7()); // P7
        return amin;
    }

    @Override
    public void sequentialCalc(Ring r) {
        MatrixS A = (MatrixS) inData[0];
        MatrixS B = (MatrixS) inData[1];

       // LOGGER.info("bef multiplyRecursive " + (System.currentTimeMillis()- DispThread.executeTime));

        MatrixS C = A.multiplyRecursive(B, r);

        //LOGGER.info("after multiplyRecursive " + (System.currentTimeMillis()- DispThread.executeTime));

        switch (key){
            case(0): outData[0] =C; break;
            case(1): outData[0] = C.negate(r); break;
        }
    }

    @Override
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring r) {
       long currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory bef first split: " + DispThread.bytesToMegabytes(currentMemory));
        MatrixS[] aSplit = ((MatrixS) input[0]).split();
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory after first split: " + DispThread.bytesToMegabytes(currentMemory));
        MatrixS A11 = aSplit[0];
        MatrixS A12 = aSplit[1];
        MatrixS A21 = aSplit[2];
        MatrixS A22 = aSplit[3];

         currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory bef second split: " + DispThread.bytesToMegabytes(currentMemory));
        MatrixS[] bSplit = ((MatrixS) input[1]).split();
         currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory after second split: " + DispThread.bytesToMegabytes(currentMemory));
        MatrixS B11 = bSplit[0];
        MatrixS B12 = bSplit[1];
        MatrixS B21 = bSplit[2];
        MatrixS B22 = bSplit[3];

        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory bef calc prep matrix: " + DispThread.bytesToMegabytes(currentMemory));
        MatrixS[] inputMatr = new MatrixS[14];
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
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory after calc prep matrix: " + DispThread.bytesToMegabytes(currentMemory));
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

        MatrixS[] C_matrix = new MatrixS[]{
                P1.add(P4, r).subtract(P5, r).add(P7, r), // C11
                P3.add(P5, r), // C12
                P2.add(P4, r), // C21
                P1.subtract(P2, r).add(P3, r).add(P6, r) // C22
        };

        MatrixS[] res;
        if (key == 0) res = new MatrixS[]{MatrixS.join(C_matrix)};
        else
            res = new MatrixS[]{MatrixS.join(C_matrix).negate(r)};
        return res;
    }
}
