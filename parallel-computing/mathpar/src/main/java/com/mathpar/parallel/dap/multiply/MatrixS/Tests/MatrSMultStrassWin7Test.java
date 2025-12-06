package com.mathpar.parallel.dap.multiply.MatrixS.Tests;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.test.DAPTest;
import mpi.MPIException;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.Random;

//import static org.apache.logging.log4j.core.util.ExtensionLanguageMapping.VM;

public class MatrSMultStrassWin7Test extends DAPTest {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSMultStrassWin7Test.class);
    protected static int defaultMultSize = 4;

    protected MatrSMultStrassWin7Test() {
        super("MatrSMultStrassWin7", 6, 0);
       // ring = new Ring("R[]");
    }

    @Override
    protected MatrixS[] initData(int size, double density, int maxBits, Ring ring) {
        return new MatrixS[]{matrix(size, density, maxBits, ring), matrix(size, density, maxBits, ring)};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        MatrixS matrixA = (MatrixS) initData[0];
        // LOGGER.trace("matrixA = "+matrixA);
        MatrixS matrixB = (MatrixS) initData[1];
        // LOGGER.trace("matrixB = "+matrixB);
        MatrixS res = (MatrixS) resultData[0];
        //  LOGGER.trace("res = "+res.toString(ring));

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
        long currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory bef creating: " + DispThread.bytesToMegabytes(currentMemory));


        MatrixS matrix = new MatrixS(size, size, density, new int[]{maxBits}, new Random(),ring.numberONE(), ring);


        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory ____ btes: " + currentMemory);
        // LOGGER.trace("bef matrix = " + matrix);
      /*  for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(!matrix.getElement(i,j, ring).equals(ring.numberZERO))
                    matrix.putElement( ring.numberONE.divide(matrix.getElement(i,j, ring),  ring), i, j);
            }
        }*/
       // currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory after cr: " + DispThread.bytesToMegabytes(currentMemory));
        //System.out.println("Used memory after cr btes: " + currentMemory);
        //LOGGER.info("matrix = " + matrix);
        return matrix;
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring r) {

        return new Element[]{((MatrixS) data[0]).multiplyStrassWin((MatrixS) data[1], ring)};
    }

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        MatrSMultStrassWin7Test test = new MatrSMultStrassWin7Test();
        test.runTests(args);
    }
}
