package com.mathpar.parallel.dap.adjmatrix.MatrixS.Tests;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.AdjMatrixS;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.multiply.MatrixS.Tests.MatrSMult4Test;
import com.mathpar.parallel.dap.test.DAPTest;
import mpi.MPIException;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

public class MatrSAdjMatrixTest extends DAPTest {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSMult4Test.class);
    protected MatrSAdjMatrixTest() {
        super("MatrSAdjMatrix", 7701, 0);
        ring = new Ring("Zp32[]");
        ring.setMOD32(97L);
    }

    @Override
    protected Element[] initData(int size, double density, int maxBits, Ring ring) {
        MatrixS M = matrix(size, density, maxBits, ring);
        return new Element[]{M, ring.numberONE};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        MatrixS initM = (MatrixS) initData[0];
        LOGGER.info("Input matrix: " + initM);
        AdjMatrixS resAdj = (AdjMatrixS) resultData[0];
        AdjMatrixS resAdj1 = new AdjMatrixS(initM, ring.numberONE,  ring);
        LOGGER.info("Adj matrix = " + resAdj.A);
        LOGGER.info("Adj matrix seq = " + resAdj1.A);
        LOGGER.info("Output matrix det = " + resAdj.Det);
        LOGGER.info("Output matrix det seq = " + resAdj1.Det);
        MatrixS divided = resAdj.A.divideByNumbertoFraction(resAdj.Det, ring);
        MatrixS rr = initM.multiply(divided, ring);
        MatrixS divided1 = resAdj1.A.divideByNumbertoFraction(resAdj1.Det, ring);
        MatrixS rr1 = initM.multiply(divided1, ring);
        LOGGER.info("Output: " + rr);
        LOGGER.info("Output seq: " + rr1);
        boolean succeed = rr.isOne(ring);
        return new Pair<>(succeed, rr.maxAbs(ring));
    }

    @Override
    protected int dispRunsOnOtherProc() {
        return 0;
    }

    @Override
    protected MatrixS matrix(int size, double density, int maxBits, Ring ring){
       /* int [][]mat = {{20, 21, 2,  16, 0,  16, 15, 11},
                {24, 31, 22, 3,  3,  6,  14, 13},
                {12, 12, 2,  10, 20, 28, 6,  12},
                {10, 16, 15, 25, 25, 11, 30, 15},
                {1,  5,  30, 7,  9,  13, 16, 29},
                {19, 11, 2,  15, 18, 4,  7,  4 },
                {24, 2,  4,  16, 19, 19, 7,  22},
                {26, 17, 30, 14, 3,  3,  6,  7 }};*/
        //MatrixS matrix = new MatrixS(mat, ring);
        MatrixS matrix = new MatrixS(size, size, density, new int[]{maxBits}, new Random(),ring.numberONE(), ring);
        // LOGGER.trace("bef matrix = " + matrix);
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if(!matrix.getElement(i,j, ring).equals(ring.numberZERO))
//                    matrix.putElement( ring.numberONE.divide(matrix.getElement(i,j, ring),  ring), i, j);
//            }
//        }
        //LOGGER.info("matrix = " + matrix);
        return matrix;
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        MatrixS m = (MatrixS) data[0];
        Element d0 = data[1];
        AdjMatrixS adjM = new AdjMatrixS(m, d0,  ring);
        Element resD = adjM.Det;
        MatrixS y = adjM.S.ES_min_dI(resD, adjM.Ei, adjM.Ej, ring);
        return new Element[] {adjM, y, resD};
    }

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        MatrSAdjMatrixTest test = new MatrSAdjMatrixTest();
        test.runTests(args);
    }
}
