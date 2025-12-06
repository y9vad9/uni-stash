package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step2 implements Callable<MatrixS> {
    private final MatrixS J;
    private final MatrixS M;
    private final MatrixS A12;
    private final Ring ring;

    public Step2(MatrixS j, MatrixS m, MatrixS a12, Ring ring) {
        J = j;
        M = m;
        A12 = a12;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        return (J.multiply(M, ring)).multiply(A12, ring);

    }
}
