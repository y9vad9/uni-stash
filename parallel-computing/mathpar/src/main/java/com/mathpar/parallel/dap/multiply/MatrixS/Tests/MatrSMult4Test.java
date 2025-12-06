package com.mathpar.parallel.dap.multiply.MatrixS.Tests;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.test.DAPTest;
import mpi.MPIException;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.Random;

public class MatrSMult4Test extends DAPTest {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSMult4Test.class);
    protected MatrSMult4Test() {
        super("MatrSMult4", 5, 0);
        //ring = new Ring("R[]");
    }

    @Override
    protected Element[] initData(int size, double density, int maxBits, Ring ring) {
        MatrixS A = matrix(size, density, maxBits, ring);
        MatrixS B = matrix(size, density, maxBits, ring);
        return new Element[]{A, B};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        MatrixS matrixA = (MatrixS) initData[0];
        // LOGGER.trace("matrixA = "+matrixA);
        MatrixS matrixB = (MatrixS) initData[1];
        // LOGGER.trace("matrixB = "+matrixB);
        MatrixS res = (MatrixS) resultData[0];
        //LOGGER.info("res = "+res.toString(ring));

        MatrixS check = matrixA.multiply(matrixB, ring);

        check = check.subtract(res, ring);
        boolean succeed = check.isZero(ring);

        Element element = check.max(ring);

        return new Pair<>(succeed, element);
    }

    @Override
    protected int dispRunsOnOtherProc() {
        return 0;
    }

    @Override
    protected MatrixS matrix(int size, double density, int maxBits, Ring ring){
        MatrixS matrix = new MatrixS(size, size, density, new int[]{maxBits}, new Random(),ring.numberONE(), ring);
        // LOGGER.trace("bef matrix = " + matrix);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(!matrix.getElement(i,j, ring).equals(ring.numberZERO))
                    matrix.putElement( ring.numberONE.divide(matrix.getElement(i,j, ring),  ring), i, j);
            }
        }
        //LOGGER.info("matrix = " + matrix);
        return matrix;
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        MatrixS result = ((MatrixS)data[0]).multiplyRecursive((MatrixS)data[1],ring);
        return new Element[] {result};
    }

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        MatrSMult4Test test = new MatrSMult4Test();
        test.runTests(args);
    }
}
