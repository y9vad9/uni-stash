/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.mathpar.students.ukma17i41.bosa.parallel.engine;
package com.mathpar.parallel.dap.multiply;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;

public class MultiplyAdd extends Multiply {
  private final static MpiLogger LOGGER = MpiLogger.getLogger(MultiplyAdd.class);

    private int[][] _arcs = new int[][] {{1, 0, 0, 1, 4, 1, 2, 1, 0, 2, 6, 1, 3, 0, 0, 3, 5, 1, 4, 1, 0, 4, 7, 1,
            5, 2, 0, 5, 4, 1, 6, 3, 0, 6, 6, 1, 7, 2, 0, 7, 5, 1, 8, 3, 0, 8, 7, 1},
        {2, 0, 2}, {9, 0, 0}, {4, 0, 2}, {9, 0, 1}, {6, 0, 2}, {9, 0, 2}, {8, 0, 2}, {9, 0, 3}, {}};

    public MultiplyAdd() {
        inData = new Element[3];
        outData =  new Element[1];
        type = 1;
        resultForOutFunctionLength = 5;
        inputDataLength = 3;
        outputDataLength = 1;
        number = cnum++;
        arcs = _arcs;
    }

    @Override
    public void sequentialCalc(Ring ring) {
       // LOGGER.info("in sequentialCalc indata = " + inData[0] );//+ ",  "+inData[1] +"inData[2] = " + inData[2]);
        //LOGGER.info("sequentialCalc MAdd, inData[2] = " + inData[2]);
       outData[0] = ((MatrixS)inData[0]).multiply((MatrixS)inData[1], ring).add((MatrixS)inData[2], ring);
    }

    @Override
    //input key : 1 - full, 2 - main, 3- notmain
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixS[] res = new MatrixS[8];
        MatrixS ms = (MatrixS) input[0];
        MatrixS ms1 = (MatrixS) input[1];
        Array.concatTwoArrays(ms.split(), ms1.split(), res);
        return res;
    }

    @Override
    public void independentCalc(Ring ring, Amin amin) {
        amin.resultForOutFunction[4] = inData[2];
    }

    @Override
    public MatrixS[] outputFunction(Element[] input, Ring ring) {

        MatrixS[] resmat = new MatrixS[input.length];
        for (int i = 0; i < input.length; i++) {
            resmat[i] = (MatrixS) input[i];
        }
        MatrixS[] res = new MatrixS[] {MatrixS.join(resmat).add((MatrixS) input[4], ring)};

        return res;

    }
    
}
