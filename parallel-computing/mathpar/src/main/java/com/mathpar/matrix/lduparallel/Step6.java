package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step6 implements Callable<Element> {
    private final MatrixS A12_2;
    private final LDUMWParallel F12;
    private final Element ak;
    private final Ring ring;

    public Step6(MatrixS a12_2, LDUMWParallel f12, Element ak, Ring ring) {
        A12_2 = a12_2;
        F12 = f12;
        this.ak = ak;
        this.ring = ring;
    }

    @Override
    public Element call() throws Exception {
        F12.getLDU(A12_2, ak, ring);
        return F12.a_n;
    }
}
