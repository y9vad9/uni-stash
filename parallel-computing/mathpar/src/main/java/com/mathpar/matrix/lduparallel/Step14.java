package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step14 implements Callable<MatrixS> {
    private final MatrixS Dbar;
    private final MatrixS M;
    private final MatrixS A22_1;
    private final Ring ring;

    public Step14(MatrixS dbar, MatrixS m, MatrixS a22_1, Ring ring) {
        Dbar = dbar;
        M = m;
        A22_1 = a22_1;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        return Dbar.multiply(M, ring).multiply(A22_1, ring);
    }
}
