package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step13 implements Callable<MatrixS> {
    private MatrixS U2;
    private final Element ak;
    private final MatrixS J;
    private final MatrixS M;
    private final MatrixS A22_1;
    private final Element al;
    private final Element a;
    private final Ring ring;

    public Step13(MatrixS u2, Element ak, MatrixS j, MatrixS m, MatrixS a22_1, Element al, Element a, Ring ring) {
        U2 = u2;
        this.ak = ak;
        J = j;
        M = m;
        A22_1 = a22_1;
        this.al = al;
        this.a = a;
        this.ring = ring;
    }


    @Override
    public MatrixS call() throws Exception {
        U2 = U2.divideByNumber(ak, ring);
        MatrixS U2H = J.multiply(M, ring).multiply(A22_1, ring);
        U2H = U2H.divideByNumber(al, ring).divideByNumber(a, ring);
        U2 = U2.add(U2H, ring);
        return U2;
    }
}
