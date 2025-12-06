package com.mathpar.parallel.dap.cholesky;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.Random;

public class MaxElement {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MaxElement.class);
    public static void main(String[] args){
        int size = Integer.parseInt(args[0]);
        int density = Integer.parseInt(args[1]);

        Ring ring = new Ring("R[]");
        Element max = findMaxElement(createMatrix(size,  density, ring), ring);
        LOGGER.trace("Max element in matrix of size " + size+" is " + max);

    }


    protected static Element findMaxElement(MatrixS matrix, Ring ring) {
        return matrix.max(ring);
    }

    protected static MatrixS createMatrix(int size, int density, Ring ring){
        MatrixS matrix = new MatrixS(size, size, density, new int[] {3}, new Random(), ring.numberONE(), ring);
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
        MatrixS res = matrix.multiply(matrix.transpose(), ring);
        return res;
    }
}
