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

public class MatrSTriangInv4Test extends DAPTest {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSTriangInv4Test.class);

    protected MatrSTriangInv4Test() {
        super("MatrSTriangInv4", 15, 0);

        // this.ring = defaultRing;
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        //Ring defaultRing = new Ring("R[]");
        // defaultRing.setAccuracy(200);
        new MatrSTriangInv4Test().runTests(args);
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
            Ring ring
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


        LOGGER.info("mat = " + mat);
        MatrixS inv = mat.inverseLowTriangle( ring);
        LOGGER.info("inv = " + inv);
        MatrixS check = mat.multiply(inv, ring);
        //System.out.println("CHECK Matrix= " + check);
        LOGGER.info("CHECK = " + check.isOne(ring));

        return mat;
    }
    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        return new Element[] {((MatrixS) data[0]).inverseLowTriangle4(ring)};
    }
}
