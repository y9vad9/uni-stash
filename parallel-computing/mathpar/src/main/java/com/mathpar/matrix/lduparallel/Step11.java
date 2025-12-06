package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step11 implements Callable<MatrixS> {
    private final MatrixS W11;
    private final MatrixS Dhat11;
    private final MatrixS W21;
    private final MatrixS Dhat21;
    private final Ring ring;

    public Step11(MatrixS w11, MatrixS dhat11, MatrixS w21, MatrixS dhat21, Ring ring) {
        W11 = w11;
        Dhat11 = dhat11;
        W21 = w21;
        Dhat21 = dhat21;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        return W11.multiply(Dhat11.multiply(W21.multiply(Dhat21, ring), ring), ring);
    }
}
