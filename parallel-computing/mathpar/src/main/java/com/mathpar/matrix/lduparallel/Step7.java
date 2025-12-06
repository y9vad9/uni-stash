package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step7 implements Callable<MatrixS> {
    private final MatrixS Dhat;
    private final Element ak;
    private final MatrixS A12_0;
    private final MatrixS A21_0;
    private final MatrixS D;
    private final Ring ring;

    public Step7(MatrixS dhat, Element ak, MatrixS a12_0, MatrixS a21_0, MatrixS d, Ring ring) {
        Dhat = dhat;
        this.ak = ak;
        A12_0 = a12_0;
        A21_0 = a21_0;
        D = d;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {

        MatrixS A21_1 = A21_0.multiplyByNumber(ak, ring).multiply(Dhat, ring);
        MatrixS A12_1 = Dhat.multiplyByNumber(ak, ring).multiply(A12_0, ring);
        MatrixS D11PLUS = D.transpose();

        return A21_1.multiply(D11PLUS.multiply(A12_1, ring), ring);
    }
}
