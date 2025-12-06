package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step4 implements Callable<MatrixS[]> {
    private final  MatrixS A21;
    private final  MatrixS W;
    private final  MatrixS Dbar;
    private final  Ring ring;
    private final  Element a;
    public Step4(MatrixS a21, MatrixS w, MatrixS dbar, Ring ring, Element a) {
        A21 = a21;
        W = w;
        Dbar = dbar;
        this.ring = ring;
        this.a = a;
    }

    @Override
    public MatrixS[] call() throws Exception {
        MatrixS[] res = new MatrixS[2];
        res[0] = A21.multiply(W, ring); // STEP4
        res[1] = res[0].multiply(Dbar, ring).divideByNumber(a, ring); // STEP4
        return res;
    }

}
