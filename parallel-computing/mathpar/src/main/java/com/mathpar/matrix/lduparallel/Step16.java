package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step16 implements Callable<MatrixS> {
    private final MatrixS I12;
    private final Element lambda;
    private final MatrixS Ibar12;
    private final MatrixS L12;
    private final MatrixS L11;
    private final Ring ring;

    public Step16(MatrixS i12, Element lambda, MatrixS ibar12, MatrixS l12, MatrixS l11, Ring ring) {
        I12 = i12;
        this.lambda = lambda;
        Ibar12 = ibar12;
        L12 = l12;
        L11 = l11;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        MatrixS I12lambda = (I12.multiplyByNumber(lambda, ring)).add(Ibar12, ring);
        MatrixS L12tilde = L12.multiply(I12lambda, ring);

        return L11.multiply(L12tilde, ring);
    }
}
