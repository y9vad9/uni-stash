/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.mathpar.students.ukma17i41.bosa.parallel.engine;
package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;
import com.mathpar.number.Element;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.io.IOException;
import java.util.Random;

import com.mathpar.parallel.dap.core.DispThread;
import mpi.MPIException;
import org.javatuples.Pair;

public class MultiplyTest extends DAPTest {
  private final static MpiLogger LOGGER = MpiLogger.getLogger(MultiplyTest.class);
    protected MultiplyTest() {
        super("multiply", 0, 0);
        ring = new Ring("R[]");
    }

    @Override
    protected MatrixS[] initData(int size, double density, int maxBits, Ring ring) {
        return new MatrixS[]{matrix(size, density, maxBits, ring), matrix(size, density, maxBits, ring)};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args,  Element[] initData, Element[] resultData, Ring ring) {
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
        return new Element[] {data[0].multiply(data[1],ring)};
    }

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        MultiplyTest test = new MultiplyTest();
        test.runTests(args);
    }
}
