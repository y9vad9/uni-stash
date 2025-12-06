package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step12 implements Callable<Object[]> {
    private MatrixS A22_2;
    private final MatrixS W;
    private final MatrixS Dbar;
    private final Element al;
    private final Element ak;
    private final Element am;
    private final Element ak2;
    private final Element a;
    private final Ring ring;
    private final MatrixS I;
    private final MatrixS Ibar;
    private final MatrixS Dhat;

    public Step12(MatrixS a22_2, MatrixS w,
                  MatrixS dbar, Element al,
                  Element ak, Element am,
                  Element ak2, Element a,
                  Ring ring, MatrixS i,
                  MatrixS ibar,
                  MatrixS dhat) {

        A22_2 = a22_2;
        W = w;
        Dbar = dbar;
        this.al = al;
        this.ak = ak;
        this.am = am;
        this.ak2 = ak2;
        this.a = a;
        this.ring = ring;
        I = i;
        Ibar = ibar;
        Dhat = dhat;
    }

    @Override
    public Object[] call() throws Exception {
        A22_2 = A22_2.multiply(W.multiply(Dbar, ring), ring);
        Element lambda = al.divideToFraction(ak, ring);
        Element as = lambda.multiply(am, ring);
        MatrixS A22_3 = A22_2.divideByNumber(ak2, ring).divideByNumber(a, ring);
        MatrixS I12lambdaM2 = (I.divideByNumbertoFraction(lambda, ring)).add(Ibar, ring);// step12
        MatrixS invD12hat = I12lambdaM2.multiply(Dhat, ring);// step12

        Object[] res = new Object[4];
        res[0] = lambda;
        res[1] = as;
        res[2] = A22_3;
        res[3] = invD12hat;

        return res;
    }
}
