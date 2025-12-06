package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step3 implements Callable<MatrixS[]> {
    private final MatrixS M;
    private final MatrixS Dbar;
    private final MatrixS A12;
    private final Element a;
    private final Ring ring;


    public Step3(MatrixS m, MatrixS dbar, MatrixS a12, Element a, Ring ring) {
        M = m;
        Dbar = dbar;
        A12 = a12;
        this.a = a;
        this.ring = ring;
    }

    @Override
    public MatrixS[] call() throws Exception {
        MatrixS[] res = new MatrixS[2];

        res[0] = M.multiply(A12, ring);
        res[1] = Dbar.multiply(res[0], ring).divideByNumber(a, ring);;

        return res;
    }
}
