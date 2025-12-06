package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step5 implements Callable<MatrixS> {
    private final MatrixS A21;
    private final MatrixS W;
    private final MatrixS I;
    private final Ring ring;

    public Step5(MatrixS a21, MatrixS w, MatrixS i, Ring ring) {
        A21 = a21;
        W = w;
        I = i;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        return A21.multiply(W.multiply(I, ring), ring);
    }

}
