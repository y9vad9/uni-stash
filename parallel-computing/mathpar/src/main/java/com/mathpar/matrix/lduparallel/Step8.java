package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step8 implements Callable<Element> {
    private final MatrixS A21_2;
    private final LDUMWParallel F21;
    private final Element ak;
    private final Ring ring;

    public Step8(MatrixS a21_2, LDUMWParallel f21, Element ak, Ring ring) {
        A21_2 = a21_2;
        F21 = f21;
        this.ak = ak;
        this.ring = ring;
    }

    @Override
    public Element call() throws Exception {
        F21.getLDU(A21_2, ak, ring);
        return F21.a_n;
    }
}
