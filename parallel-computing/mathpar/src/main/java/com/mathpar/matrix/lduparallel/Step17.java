package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step17 implements Callable<Element> {
    private final MatrixS A22_3;
    private final LDUMWParallel F22;
    private final Element as;
    private final Ring ring;

    public Step17(MatrixS a22_3, LDUMWParallel f22, Element as, Ring ring) {
        A22_3 = a22_3;
        F22 = f22;
        this.as = as;
        this.ring = ring;
    }

    @Override
    public Element call() throws Exception {
        F22.getLDU(A22_3, as, ring);
        return F22.a_n;
    }
}
