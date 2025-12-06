package com.mathpar.students.OLD.savchenko;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;
import com.mathpar.students.OLD.savchenko.exception.WrongDimensionsException;

public class QrAlgorithm {

    public static void main(String[] args) throws WrongDimensionsException {
        Ring ring = new Ring("R64[x]");
        ring.setFLOATPOS(100);
        ring.setMachineEpsilonR64(30);                                              // машинный эпсилон
        System.out.println("0 = " + ring.MachineEpsilonR64.toString(ring));
        MatrixD A = TestData.getTestMatrix(64, ring);

        MatrixD[] qra = getSVD(A, ring, true);
        MatrixD Eig = qra[0];
        MatrixD U = qra[1];
    }

    public static MatrixD[] getSVD(MatrixD A, Ring ring, boolean blockQr) throws WrongDimensionsException {
        int rowNum = A.rowNum();
        int colNum = A.colNum();
        if ((rowNum != colNum) || !Utils.isPowerOfTwo(rowNum) || rowNum == 0)
            throw new WrongDimensionsException();

        double st = System.nanoTime();

        MatrixD At = A.transpose(ring);
        MatrixD AAt = A.multiplyMatr(At, ring);

        int n = AAt.rowNum();

        MatrixD[] qra = qrAlgorithm(AAt, ring, blockQr);
        MatrixD Eig = qra[0];
        MatrixD U = qra[1];

        MatrixD L = MatrixD.ONE(n, ring);
        for (int i = 0; i < n; i++) {
            L.M[i][i] = Eig.getElement(i, 0).sqrt(ring);
        }

        MatrixD L_obr = L.copy();
        for (int i = 0; i < n; i++) {
            L_obr.M[i][i] = ring.numberONE.divide(L_obr.getElement(i, i), ring);
        }

        MatrixD V = (L_obr.multiplyMatr(U.transpose(ring), ring).multiplyMatr(A, ring)).transpose(ring);
        MatrixD Vt = V.transpose(ring);

        double en = System.nanoTime();
        double lastTimeSec = ((en - st) / 1000000000);
        System.out.println("Time for SVD: " + lastTimeSec + " seconds.");

        //System.out.println("U = \n" + U.toString() + "\n");
        //System.out.println("L = \n" + L.toString() + "\n");
        //System.out.println("Vt = \n" + Vt.toString() + "\n");

        MatrixD check = ((U.multiplyMatr(L, ring)).multiplyMatr(Vt, ring));
        //System.out.println("U*L*Vt = \n" + check.toString() + "\n");

        return new MatrixD[] {U, L, Vt, check};
    }

    public static MatrixD[] qrAlgorithm(MatrixD A, Ring ring, boolean blockQr) throws WrongDimensionsException {

        MatrixD Ak = A.copy();
        MatrixD Qu = MatrixD.ONE(A.colNum(), ring);

        double epsilon = 0.000001;
        double lastValue;
        int c = 0;

        do {
            MatrixD diagVals = getDiagVals(Ak, ring);
            MatrixD[] qr;
            if (blockQr) {
                qr = BlockQR.blockQR(Ak, ring);
            } else {
                qr = SVD.givensQR(A, ring);
            }
            MatrixD Q = qr[0];
            MatrixD R = qr[1];
            MatrixD tmp = R.multiplyMatr(Q, ring);
//            if (tmp.getElement(0, 0).isNaN() || Q.getElement(0, 0).isNaN())
//                break;
            Ak = tmp;
            Qu = Qu.multiplyMatr(Q, ring);
            MatrixD diagAfter = getDiagVals(Ak, ring).negate(ring);
            diagAfter = diagVals.add(diagAfter, ring);
            lastValue = ((MatrixD) (diagAfter.abs(ring))).max(ring).doubleValue();
            c++;
            //System.out.println("Temp A " + c + " = \n" + Ak.toString() + "\n");
            //System.out.println(lastValue);
        } while (notTriang(Ak, ring) && (lastValue > epsilon));

        //System.out.println("        Количество итераций в QR алгоритме =  " + c);

        return new MatrixD[] {getDiagVals(Ak, ring), Qu};
    }

    private static MatrixD getDiagVals(MatrixD A, Ring ring) {
        MatrixD d = new MatrixD(A.rowNum(), 1, 1, ring);
        for (int i = 0; i < A.rowNum(); i++) {
            d.M[i][0] = A.getElement(i, i);
        }
        return d;
    }

    public static boolean isMatrixUpperTriangular(MatrixD A, Ring ring) {
        for (int i = 1; i < A.rowNum(); i++) {
            for (int j = 0; j < i; j++) {
                if (!A.getElement(i, j).isZero(ring)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean notTriang(MatrixD ak, Ring ring) {
        for (int i=1; i<ak.rowNum(); i++) {
            for (int j=0; j<i; j++) {
                if (!ak.M[i][j].isZero(ring)){
                    //System.out.println("i = " + i + " j = " + j);
                    return true;
                }
            }
        }
        return false;
    }
}
