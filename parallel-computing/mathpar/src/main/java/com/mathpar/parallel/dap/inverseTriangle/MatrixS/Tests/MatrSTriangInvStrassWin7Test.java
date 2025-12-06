package com.mathpar.parallel.dap.inverseTriangle.MatrixS.Tests;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.test.DAPTest;
import mpi.MPIException;
import org.javatuples.Pair;

import java.util.Random;

public class MatrSTriangInvStrassWin7Test extends DAPTest {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSTriangInvStrassWin7Test.class);

    protected MatrSTriangInvStrassWin7Test() {
        super("MatrSTriangInvStrassWin7", 16, 0);

        // this.ring = defaultRing;
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        //Ring defaultRing = new Ring("R[]");
        // defaultRing.setAccuracy(200);
        new MatrSTriangInvStrassWin7Test().runTests(args);
    }

    @Override
    protected MatrixS[] initData(int size, double density, int maxBitsForElements, Ring ring) {
        return new MatrixS[]{matrix(size, density, maxBitsForElements, ring)};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(
            DispThread dispThread,
            String[] args,
            Element[] initData,
            Element[] resultData,
            com.mathpar.number.Ring ring
    ) {
        MatrixS initial = (MatrixS) initData[0];
        MatrixS inverted = (MatrixS) resultData[0];

        LOGGER.info(" initial= "+ initial);
        LOGGER.info(" inverted= "+ inverted);

        Element[] results = {initial.multiply(inverted, ring)};
        MatrixS check = (MatrixS) results[0];
        MatrixS errors = check.multiply(inverted, ring)
                .subtract(MatrixS.scalarMatrix(initial.size, ring.numberONE, ring), ring);

        boolean isFlawless = errors.isZero(ring);
        Element maxError = errors.max(ring);

        return new Pair<>(isFlawless, maxError);
    }
    @Override
    protected int dispRunsOnOtherProc() {
        return 0;
    }

    @Override
    protected MatrixS matrix(int size, double density, int maxBitsForElements, Ring ring) {

        MatrixS mat = new MatrixS(size, size, density, new int[]{maxBitsForElements}, new Random(), ring.numberONE(), ring);

        for (int row = 0; row < size; row++) {
            for (int col = row + 1; col < size; col++) {
                mat.putZeroElement(row, col);
            }
        }
        Element elem = ring.numberONE;
        for (int diag = 0; diag < mat.size; diag++) {
            elem = ring.numberONE.add(elem, ring);
            if (mat.getElement(diag, diag, ring).isZero(ring)) {
                mat.putElement(elem, diag, diag);
            }
        }

        return mat;
    }
    @Override
    protected Element[] sequentialExecute(Element[] data, com.mathpar.number.Ring ring) {
        return new Element[] {((MatrixS) data[0]).inverseLowTriangleWin(ring)};
    }
}
