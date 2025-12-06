package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step9 implements Callable<MatrixS[]> {
    private final MatrixS A22;
    private final Element ak2;
    private final Element a;
    private final MatrixS A22_0;
    private final Element ak;
    private final MatrixS Dbar;
    private final MatrixS M;
    private final Ring ring;

    public Step9(MatrixS a22, Element ak2, Element a, MatrixS a22_0,
                 Element ak, MatrixS dbar, MatrixS m, Ring ring) {
        A22 = a22;
        this.ak2 = ak2;
        this.a = a;
        A22_0 = a22_0;
        this.ak = ak;
        Dbar = dbar;
        M = m;
        this.ring = ring;
    }

    @Override
    public MatrixS[] call() throws Exception {
        MatrixS[] res = new MatrixS[2];
        MatrixS A22_1 = (A22.multiplyByNumber(ak2, ring).multiplyByNumber(a, ring)
                .subtract(A22_0, ring)).divideByNumber(ak, ring).divideByNumber(a, ring);
        MatrixS A22_2 = (Dbar.multiply(M, ring)).multiply(A22_1, ring);

        res[0] = A22_1;
        res[1] = A22_2;
        return res;
    }
}
