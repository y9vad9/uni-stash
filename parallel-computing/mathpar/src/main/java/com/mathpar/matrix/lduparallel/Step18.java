package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step18 implements Callable<MatrixS> {
    private final MatrixS L3H2;
    private final MatrixS W12;
    private final MatrixS I12;
    private final Element am;
    private final Element ak;
    private final Element a;
    private final MatrixS L3;
    private final Ring ring;

    public Step18(MatrixS l3H2, MatrixS w12, MatrixS i12, Element am, Element ak, Element a, MatrixS l3, Ring ring) {
        L3H2 = l3H2;
        W12 = w12;
        I12 = i12;
        this.am = am;
        this.ak = ak;
        this.a = a;
        L3 = l3;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        MatrixS L3H1 = (L3H2.multiply(W12.multiply(I12, ring), ring));

        L3H1 = L3H1.divideByNumber(am, ring)
                .divideByNumber(ak, ring).divideByNumber(a, ring);

        return (L3.divideByNumber(ak, ring)).add(L3H1, ring);
    }
}
