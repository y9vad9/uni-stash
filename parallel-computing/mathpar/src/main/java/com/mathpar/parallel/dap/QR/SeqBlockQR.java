package com.mathpar.parallel.dap.QR;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.Arrays;

public class SeqBlockQR {



    public static MatrixS[] compute(MatrixS source, Ring ring){
        if(source.isZero(ring))
            return new MatrixS[]{MatrixS.scalarMatrix(source.size, ring.numberONE(), ring), source};
        if(source.size == 2) return computeLeaf(source, ring);

        MatrixS Q, R;
        MatrixS[] ABCD = source.split();

        MatrixS[] QR1 = compute(ABCD[2], ring);

        MatrixS[] QP1 = computeQP(ABCD[0], QR1[1], ring);

        Q = QP1[0].transpose();
        if(!QR1[0].isOne(ring)) {
            MatrixS diagQ1 = MatrixS.embedDownRightQuarter(QR1[0], ring);
            Q = diagQ1.multiply(Q, ring);
        }
        MatrixS[] BD = update(Q.transpose(), ABCD[1], ABCD[3], ring);

        MatrixS[] QR2 = compute(BD[1], ring);
        if(!QR2[0].isOne(ring)) {
            MatrixS diagQ2 = MatrixS.embedDownRightQuarter(QR2[0], ring);
            Q = Q.multiply(diagQ2, ring);
        }
        R = matrixR(QP1[1], BD[0], QR2[1]);

        return new MatrixS[]{Q, R};
    }


    static MatrixS[] computeLeaf(MatrixS source, Ring ring){
        if(source.size != 2) throw new IllegalArgumentException("QR Leaf: Matrix size is not equal to 2");
        MatrixS Q, R;
        Element a = source.getElement(0, 0, ring);
        Element b = source.getElement(1, 0, ring);

        if(b.isZero(ring)){
            return new MatrixS[]{MatrixS.scalarMatrix(2, ring.numberONE(), ring), source};
        }
        else if (a.isZero(ring)) {
            return exchangeRows(source, ring);
        }

        Element abSqrt = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);
        Element s = b.negate(ring).divide(abSqrt, ring);
        Element c = a.divide(abSqrt, ring);

        Q = MatrixS.rotationMatrix(s, c, ring);
        R = Q.multiply(source, ring);

        Q = Q.transpose();
        return new MatrixS[]{Q, R};
    }

    private static MatrixS[] exchangeRows(MatrixS source, Ring ring){
        source = source.copy();
        Element[] elements = source.M[0];
        source.M[0] = Arrays.stream(source.M[1]).map(el -> el.negate(ring)).toArray(Element[]::new);
        source.M[1] = elements;

        int[] cols = source.col[0];
        source.col[0] = source.col[1];
        source.col[1] = cols;

        return new MatrixS[]{MatrixS.rotationMatrix(ring.numberONE().negate(ring), ring.numberZERO(), ring), source};
    }

    static MatrixS[] computeQP(MatrixS up, MatrixS down, Ring ring){
        if(up.isZero(ring) && down.isZero(ring))
            return new MatrixS[]{MatrixS.scalarMatrix(2* up.size, ring.numberONE(), ring), up};
        if (up.size == 1) return computeQPLeaf(up, down, ring);
        MatrixS Q, P;
        MatrixS[] ABCD = up.split();
        MatrixS[] EFGH = down.split();

        MatrixS[] QP_1 = computeQP(ABCD[2], EFGH[0], ring);
        MatrixS[] DF = update(QP_1[0], ABCD[3], EFGH[1], ring);

        MatrixS[] QP_2 = computeQP(ABCD[0], QP_1[1], ring);
        MatrixS[] BD = update(QP_2[0], ABCD[1], DF[0], ring);

        MatrixS[] QP_3 = computeQP(DF[1], EFGH[3], ring);

        MatrixS[] QP_4 = computeQP(BD[1], QP_3[1], ring);

        MatrixS Q1 = MatrixS.embedDiagonalCenter(QP_1[0], ring);
        MatrixS Q23 = MatrixS.embedDiagonalBlocks(QP_2[0], QP_3[0], ring);
        MatrixS Q4 = MatrixS.embedDiagonalCenter(QP_4[0], ring);

        Q = Q23.multiply(Q1, ring);
        Q = Q4.multiply(Q, ring);
        P = matrixR(QP_2[1], BD[0], QP_4[1]);

        return new MatrixS[]{Q, P};
    }

    static MatrixS[] computeQPLeaf(MatrixS A, MatrixS B, Ring ring){
        if(A.size != 1 || B.size != 1){
            throw new IllegalArgumentException("QP Leaf: Matrix size is not equal to 1");
        }

        MatrixS Q, P;
        Element a = A.getElement(0, 0, ring);
        Element b = B.getElement(0, 0, ring);


        if(b.isZero(ring)){
            Q = MatrixS.scalarMatrix(2, 2, ring.numberONE(), ring);
            P = A;
        } else if (a.isZero(ring)) {
            Q = MatrixS.rotationMatrix(ring.numberONE(), ring.numberZERO(), ring);
            P = B.negate(ring);
        } else {
            Element abSqrt = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);
            Element s = b.negate(ring).divide(abSqrt, ring);
            Element c = a.divide(abSqrt, ring);
            Element _a = c.multiply(a, ring).subtract(s.multiply(b, ring), ring);

            Q = MatrixS.rotationMatrix(s, c, ring);
            P = MatrixS.scalarMatrix(1, _a, ring);
        }

        return new MatrixS[]{Q, P};
    }

    private static MatrixS[] update(MatrixS Q, MatrixS B, MatrixS D, Ring ring){
        MatrixS[] result = Q.multiply(
                MatrixS.embedBlocksOfColumn(B, D, ring), ring)
                .split();
        return new MatrixS[]{result[0], result[2]};
    }

    private static MatrixS matrixR(MatrixS a, MatrixS b, MatrixS d){
        MatrixS zero = MatrixS.zeroMatrix(a.size);
        return MatrixS.join(new MatrixS[]{a, b, zero, d});
    }


    public static MatrixS[] sequentialQP(MatrixS A, MatrixS B, Ring ring){
        MatrixS R = MatrixS.embedBlocksOfColumn(A, B,ring);
        MatrixS Q = MatrixS.scalarMatrix(R.size, ring.numberONE(), ring);

        Element a, b, abSqrt, sin, cos;
        MatrixS rotationMatrix;
        for (int col = 0; col < R.size/2; col++) {

            for (int row = R.size/2 + col; row >  col; row--) {

                a = R.getElement(row -  1, col, ring);
                b = R.getElement(row, col, ring);

                abSqrt = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);
                sin = b.negate(ring).divide(abSqrt, ring);
                cos = a.divide(abSqrt, ring);
                rotationMatrix = MatrixS.rotationMatrix(sin, cos, ring);

                rotationMatrix = MatrixS.embedDiagonal(rotationMatrix, R.size, row - 1, ring);

                Q = rotationMatrix.multiply(Q, ring);
                R = rotationMatrix.multiply(R, ring);
            }

        }


        return new MatrixS[]{Q, R.split()[0]};
    }

    public static MatrixS[] sequentialQR(MatrixS R, Ring ring){
        MatrixS Q = MatrixS.scalarMatrix(R.size, ring.numberONE(), ring);

        Element a, b, abSqrt, sin, cos;
        MatrixS rotationMatrix;
        for (int col = 0; col < R.size; col++) {

            for (int row = R.size - 1; row >  col; row--) {

                a = R.getElement(row -  1, col, ring);
                b = R.getElement(row, col, ring);

                if (b.isZero(ring)) continue;

                abSqrt = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);
                sin = b.negate(ring).divide(abSqrt, ring);
                cos = a.divide(abSqrt, ring);
                rotationMatrix = MatrixS.rotationMatrix(sin, cos, ring);

                rotationMatrix = MatrixS.embedDiagonal(rotationMatrix, R.size, row - 1, ring);

                Q = rotationMatrix.multiply(Q, ring);
                R = rotationMatrix.multiply(R, ring);
            }

        }

        return new MatrixS[]{Q.transpose(), R};
    }
}
