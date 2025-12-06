package com.mathpar.parallel.dap.multiply.MatrixD.Tests;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.multiply.MatrixS.Tests.MatrSMultStrassWin7Test;
import com.mathpar.parallel.dap.test.DAPTest;
import mpi.MPIException;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.Random;

public class MatrDMultStrassWin7Test extends DAPTest {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrDMultStrassWin7Test.class);
    protected static int defaultMultSize = 4;

    protected MatrDMultStrassWin7Test() {
        super("MatrSMultStrassWin7", 11, 0);
        // ring = new Ring("R[]");
    }

    @Override
    protected MatrixD[] initData(int size, double density, int maxBits, Ring ring) {
        return new MatrixD[]{new MatrixD(matrix(size, density, maxBits, ring)), new MatrixD(matrix(size, density, maxBits, ring))};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        MatrixD matrixA = (MatrixD) initData[0];
        // LOGGER.trace("matrixA = "+matrixA);
        MatrixD matrixB = (MatrixD) initData[1];
        // LOGGER.trace("matrixB = "+matrixB);
        MatrixD res = (MatrixD) resultData[0];
        //  LOGGER.trace("res = "+res.toString(ring));

        MatrixD check = matrixA.multiplyMatr(matrixB, ring);

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
    protected Element[] sequentialExecute(Element[] data, Ring r) {
        return new Element[] {((MatrixD)data[0]).multiplyStrassWin((MatrixD)data[1],ring)};
    }

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        MatrDMultStrassWin7Test test = new MatrDMultStrassWin7Test();
        test.runTests(args);
    }
}
