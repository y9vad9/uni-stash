package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.util.concurrent.Callable;

public class Step15 implements Callable<MatrixS> {
    private final MatrixS invD12hat;
    private final MatrixS M12;
    private final MatrixS Dhat11;
    private final MatrixS M11;
    private final Ring ring;

    public Step15(MatrixS invD12hat, MatrixS m12, MatrixS dhat11, MatrixS m11, Ring ring) {
        this.invD12hat = invD12hat;
        M12 = m12;
        Dhat11 = dhat11;
        M11 = m11;
        this.ring = ring;
    }

    @Override
    public MatrixS call() throws Exception {
        return invD12hat.multiply(M12, ring).multiply(Dhat11.multiply(M11, ring), ring);
    }
}
