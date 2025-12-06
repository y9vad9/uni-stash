package com.mathpar.students.OLD.savchenko.trash;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;
import com.mathpar.students.OLD.savchenko.TestData;
import com.mathpar.students.OLD.savchenko.Utils;
import com.mathpar.students.OLD.savchenko.exception.WrongDimensionsException;

public class BlockQRExample4x4 {

    public static void main(String[] args) throws WrongDimensionsException {
        Ring ring = new Ring("R64[x]");
        MatrixD test = TestData.getTestMatrix(4, ring);
        System.out.println(test.toString() + "\n");
        blockQR(test, ring);
    }

    public static MatrixD[] blockQR(MatrixD matrix, Ring ring) throws WrongDimensionsException {
        if ((matrix.rowNum() != matrix.colNum()) || !Utils.isPowerOfTwo(matrix.rowNum()))
            throw new WrongDimensionsException();
        MatrixD M = matrix.copy();
        int n = M.rowNum();

        /**
         * 1. QR Разложение блока C
         */
        MatrixD M1 = M.copy();
        MatrixD Q1 = Utils.getGivensRotationMatrix(n, 2, 3, M1.getElement(2, 0), M1.getElement(3, 0), ring);
        Q1 = Q1.transpose(ring);
        M1 = Q1.multiplyMatr(M1, ring);
        System.out.println("M1 = \n" + M1.toString() + "\n");

        /**
         * 2. Обнуление параллелограмма
         */
        MatrixD M2 = M1.copy();
        MatrixD Q2 = MatrixD.oneMatrixD(n, n, ring);

        // 1. Q_ld
        MatrixD Q_ld = Utils.getGivensRotationMatrix(n, 1, 2, M2.getElement(1, 0), M2.getElement(2, 0), ring);
        //System.out.println("Q_ld = \n" + Q_ld.toString() + "\n");
        M2 = Q_ld.transpose(ring).multiplyMatr(M2, ring);
        Q2 = Q_ld.transpose(ring).multiplyMatr(Q2, ring);
        //System.out.println("M2 = \n" + M2.toString() + "\n");

        // 2. Q_lu & 3. Q_rd
        MatrixD Q_lu = Utils.getGivensRotationMatrix(n, 0, 1, M2.getElement(0, 0), M2.getElement(1, 0), ring);
        //System.out.println("Q_lu = \n" + Q_lu.toString() + "\n");
        M2 = Q_lu.transpose(ring).multiplyMatr(M2, ring);
        Q2 = Q_lu.transpose(ring).multiplyMatr(Q2, ring);
        //System.out.println("M2 = \n" + M2.toString() + "\n");

        MatrixD Q_rd = Utils.getGivensRotationMatrix(n, 2, 3, M2.getElement(2, 1), M2.getElement(3, 1), ring);
        //System.out.println("Q_rd = \n" + Q_rd.toString() + "\n");
        M2 = Q_rd.transpose(ring).multiplyMatr(M2, ring);
        Q2 = Q_rd.transpose(ring).multiplyMatr(Q2, ring);
        //System.out.println("M2 = \n" + M2.toString() + "\n");

        // 4. Q_ru
        MatrixD Q_ru = Utils.getGivensRotationMatrix(n, 1, 2, M2.getElement(1, 1), M2.getElement(2, 1), ring);
        //System.out.println("Q_ru = \n" + Q_ru.toString() + "\n");
        M2 = Q_ru.transpose(ring).multiplyMatr(M2, ring);
        Q2 = Q_ru.transpose(ring).multiplyMatr(Q2, ring);
        System.out.println("M2 = \n" + M2.toString() + "\n");

//        System.out.println("Test: \n" + Q2.multiplyMatr(M1, ring).toString());

        /**
         * 3. QR разложение блока D
         */
        MatrixD M3 = M2.copy();
        MatrixD Q3 = Utils.getGivensRotationMatrix(n, 2, 3, M3.getElement(2, 2), M3.getElement(3, 2), ring);
        Q3 = Q3.transpose(ring);
        M3 = Q3.multiplyMatr(M3, ring);
        System.out.println("M3 = \n" + M3.toString() + "\n");

        return null;
    }

}
