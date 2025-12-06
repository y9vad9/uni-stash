package com.mathpar.parallel.dap.cholesky.MatrixS.Tests;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.test.DAPTest;
import mpi.MPIException;
import org.javatuples.Pair;

import java.util.Random;

public class MatrSCholFact4Test extends DAPTest {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSCholFact4Test.class);
    MatrixS initMatrix;

    protected MatrSCholFact4Test() {
        super("MatrSCholFact4", 19, 0);
        // ring = new Ring("R[]");
        //ring.setAccuracy(180);
        // ring.setMachineEpsilonR(160);
        //;
        //ring.setFLOATPOS(190);
    }

    @Override
    protected MatrixS[] initData(int size, double density, int maxBits, Ring ring) {
        return new MatrixS[]{matrix(size, density, maxBits, ring)};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        //MatrixS matrix = (MatrixS) initData[0];
        MatrixS L = (MatrixS) resultData[0];
        //MatrixS Linv = (MatrixS) resultData[1];

        //  MatrixS LLT = L.multiply(L.transpose(), ring);

        //LOGGER.trace("initMatrix = "+initMatrix.multiply(initMatrix.transpose(), ring));
        // LOGGER.trace("L*L^T = "+L.multiply(L.transpose(), ring));
        MatrixS check = initMatrix.subtract(L, ring);
//        Element[] resultCheck = runTask(dispThread, 0, args, new Element[]{L, L.transpose()}, ring);
//        MatrixS check = (MatrixS) resultCheck[0];
        boolean succeed = check.isZero(ring);

        Element element = check.max(ring);

        return new Pair<>(succeed, element);
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        MatrSCholFact4Test test = new MatrSCholFact4Test();
        test.runTests(args);
    }

    @Override
    protected int dispRunsOnOtherProc() {
        return 0;
    }

    @Override
    protected MatrixS matrix(int size, double density, int maxBits, Ring ring){
        MatrixS matrix = new MatrixS(size, size, density, new int[] {maxBits}, new Random(), ring.numberONE(), ring);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i < j) {
                    matrix.putElement(ring.numberZERO, i, j);
                }
                else
                if (i == j) {
                    if (matrix.getElement(i, j, ring).isZero(ring)) {
                        matrix.putElement(ring.numberONE, i, j);
                    }
                }
            }
        }

        initMatrix = matrix;
        MatrixS res = matrix.multiply(matrix.transpose(), ring);

        return res;
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        return  ((MatrixS) data[0]).choleskyFactorize4(ring);
    }
}
