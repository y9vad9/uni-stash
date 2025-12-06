package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step10 implements Callable<MatrixS> {
    private final MatrixS U21;
    private final MatrixS U11;
    private final Ring ring;

    public Step10(MatrixS u21, MatrixS u11, Ring ring) {
        U21 = u21;
        U11 = u11;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        return U21.multiply(U11, ring);
    }
}
