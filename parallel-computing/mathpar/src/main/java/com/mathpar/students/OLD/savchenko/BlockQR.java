package com.mathpar.students.OLD.savchenko;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;
import com.mathpar.students.OLD.savchenko.exception.WrongDimensionsException;

import java.util.concurrent.atomic.AtomicInteger;

public class BlockQR {

    private static AtomicInteger counter = new AtomicInteger(1);

    public static void main(String[] args) throws WrongDimensionsException {
        Ring ring = new Ring("R64[x]");
        MatrixD A = TestData.getTestMatrix(32, ring);

        MatrixD[] QR = blockQR(A, ring);
        MatrixD Q = QR[0];
        MatrixD R = QR[1];
        MatrixD check = Q.multiplyMatr(R, ring);

        System.out.println("A = \n" + A.toString() + "\n");
        //System.out.println("Q = \n" + Q.toString() + "\n");
        //System.out.println("R = \n" + R.toString() + "\n");
        System.out.println("check = \n" + check.toString() + "\n");

        System.out.println("Threads created: " + counter.get());
    }

    public static MatrixD[] blockQR(MatrixD A, Ring ring) throws WrongDimensionsException {
        int rowNum = A.rowNum();
        int colNum = A.colNum();
        if ((rowNum != colNum) || !Utils.isPowerOfTwo(rowNum) || rowNum == 0)
            throw new WrongDimensionsException();

        if (rowNum == 2)
            return SVD.givensQR(A, ring);

        MatrixD M = A.copy();
        MatrixD ones = MatrixD.ONE(M.rowNum(), ring);

        /** 1. QR Разложение блока C */
        MatrixD C = Utils.block4(M, 'C');
        MatrixD D = Utils.block4(M, 'D');
        MatrixD[] Q1R1 = blockQR(C, ring);
        MatrixD Q1 = Q1R1[0];
        MatrixD R1 = Q1R1[1];
        MatrixD D1 = Q1.transpose(ring).multiplyMatr(D, ring);
        M = Utils.insertMatrixToMatrix(M, R1, M.rowNum() / 2, 0);
        M = Utils.insertMatrixToMatrix(M, D1, M.rowNum() / 2, M.colNum() / 2);
        Q1 = Utils.insertMatrixToMatrix(ones, Q1, M.rowNum() / 2, M.colNum() / 2);
        //System.out.println("M = \n" + M.toString() + "\n");

        /** 2. Взять левую часть матрицы M и выполнить blockQrForParallelogram + применить изменения к правой стороне */
        MatrixD AC = Utils.getSubMatrix(M, 0, M.rowNum() - 1, 0, (M.colNum() / 2) - 1);
        MatrixD BD = Utils.getSubMatrix(M, 0, M.rowNum() - 1, M.colNum() / 2, M.colNum() - 1);
        MatrixD[] Q2R2 = blockQrForParallelogram(AC, ring);
        MatrixD Q2 = Q2R2[0];
        MatrixD R2 = Q2R2[1];
        BD = Q2.transpose(ring).multiplyMatr(BD, ring);
        M = Utils.insertMatrixToMatrix(M, R2, 0, 0);
        M = Utils.insertMatrixToMatrix(M, BD, 0, M.colNum() / 2);
        //System.out.println("M = \n" + M.toString() + "\n");

        /** 3. QR разложение блока D */
        D = Utils.block4(M, 'D');
        MatrixD[] Q3R3 = blockQR(D, ring);
        MatrixD Q3 = Q3R3[0];
        MatrixD R3 = Q3R3[1];
        M = Utils.insertMatrixToMatrix(M, R3, M.rowNum() / 2, M.colNum() / 2);
        Q3 = Utils.insertMatrixToMatrix(ones, Q3, M.rowNum() / 2, M.colNum() / 2);
        //System.out.println("M = \n" + M.toString() + "\n");

        MatrixD Q = Q1.multiplyMatr(Q2, ring).multiplyMatr(Q3, ring);
        //System.out.println("CHECK: \n" + Q.multiplyMatr(M, ring).toString());

        return new MatrixD[] {Q, M};
    }

    /**
     * Рекурсивная процедура для обнуления внутреннего параллелограмма
     * Matrix A (2n * n)
     */
    protected static MatrixD[] blockQrForParallelogram(MatrixD A, Ring ring) throws WrongDimensionsException {
        int rowNum = A.rowNum();
        int colNum = A.colNum();
        if (!(rowNum == 2*colNum))
            throw new WrongDimensionsException();

        if (rowNum == 4) {
            return blockQrForBasicMatrix(A, ring);
        } else {
            MatrixD input = A.copy();
            int n = rowNum;

            // Block 1
            MatrixD block_1 = Utils.getBlock(input, 1);
            MatrixD[] Q1R1 = blockQrForParallelogram(block_1, ring);
            MatrixD Q1 = Q1R1[0];
            MatrixD R1 = Q1R1[1];
            MatrixD aff_b1 = Utils.getBlock(input, 4);
            aff_b1 = Q1.transpose(ring).multiplyMatr(aff_b1, ring);
            input = Utils.insertMatrixToMatrix(input, R1, input.rowNum() / 4, 0);
            input = Utils.insertMatrixToMatrix(input, aff_b1, input.rowNum() / 4, input.colNum()/2);
            //System.out.println(input.toString() + "\n");

            // Block 2 & Block 3
            MatrixD block_2 = Utils.getBlock(input, 2);
            MatrixD block_3 = Utils.getBlock(input, 3);
            MatrixD[] Q2R2 = new MatrixD[2];
            MatrixD[] Q3R3 = new MatrixD[2];
            Thread[] threads = new Thread[2];
            Thread blockExecuter_2 = new BlockExecuter(block_2, Q2R2, ring);
            threads[0] = blockExecuter_2;
            Thread blockExecuter_3 = new BlockExecuter(block_3, Q3R3, ring);
            threads[1] = blockExecuter_3;
            for (Thread t : threads) {
                t.start();
            }
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            counter.addAndGet(2);
            MatrixD Q2 = Q2R2[0];
            MatrixD R2 = Q2R2[1];
            MatrixD aff_b2 = Utils.getSubMatrix(input, 0, (input.rowNum() / 2) - 1, input.colNum() / 2, input.colNum() - 1);
            aff_b2 = Q2.transpose(ring).multiplyMatr(aff_b2, ring);
            input = Utils.insertMatrixToMatrix(input, R2, 0, 0);
            input = Utils.insertMatrixToMatrix(input, aff_b2, 0, input.colNum() / 2);
            MatrixD Q3 = Q3R3[0];
            MatrixD R3 = Q3R3[1];
            input = Utils.insertMatrixToMatrix(input, R3, input.rowNum() / 2, input.colNum() / 2);
            //System.out.println(input.toString() + "\n");

            // Block 4
            MatrixD block_4 = Utils.getBlock(input, 4);
            MatrixD[] Q4R4 = blockQrForParallelogram(block_4, ring);
            MatrixD Q4 = Q4R4[0];
            MatrixD R4 = Q4R4[1];
            input = Utils.insertMatrixToMatrix(input, R4, input.rowNum() / 4, input.colNum() / 2);
            //System.out.println(input.toString() + "\n");

            MatrixD ones = MatrixD.ONE(n, ring);

            MatrixD Q_ru = Utils.insertMatrixToMatrix(ones, Q4, n / 4, n / 4);
            //System.out.println("Q_ru = \n" + Q_ru.toString() + "\n");
            MatrixD Q_lu_Q_rd = Utils.insertMatrixToMatrix(ones, Q2, 0, 0);
            Q_lu_Q_rd = Utils.insertMatrixToMatrix(Q_lu_Q_rd, Q3, n/2, n/2);
            //System.out.println("Q_lu_Q_rd = \n" + Q_lu_Q_rd.toString() + "\n");
            MatrixD Q_ld = Utils.insertMatrixToMatrix(ones, Q1, n / 4, n / 4);
            //System.out.println("Q_ld = \n" + Q_ld.toString() + "\n");

            MatrixD Q = Q_ld.multiplyMatr(Q_lu_Q_rd, ring).multiplyMatr(Q_ru, ring);
            //System.out.println(Q.multiplyMatr(input, ring).toString());

            return new MatrixD[] {Q, input};
        }
    }

    /**
     * Обнуление параллелограмма внутри матрицы размера 4 * 2
     */
    private static MatrixD[] blockQrForBasicMatrix(MatrixD A, Ring ring) throws WrongDimensionsException {
        int rowNum = A.rowNum();
        int colNum = A.colNum();
        if (!(rowNum == 4 && colNum == 2))
            throw new WrongDimensionsException();

        MatrixD M2 = A.copy();
        MatrixD Q;

        // 1. Q_ld
        MatrixD Q_ld = Utils.getGivensRotationMatrix(rowNum, 1, 2, M2.getElement(1, 0), M2.getElement(2, 0), ring);
        //System.out.println("Q_ld = \n" + Q_ld.toString() + "\n");
        M2 = Q_ld.transpose(ring).multiplyMatr(M2, ring);
        //System.out.println("M2 = \n" + M2.toString() + "\n");

        // 2. Q_lu
        MatrixD Q_lu = Utils.getGivensRotationMatrix(rowNum, 0, 1, M2.getElement(0, 0), M2.getElement(1, 0), ring);
        //System.out.println("Q_lu = \n" + Q_lu.toString() + "\n");
        M2 = Q_lu.transpose(ring).multiplyMatr(M2, ring);
        //System.out.println("M2 = \n" + M2.toString() + "\n");

        // 3. Q_rd
        MatrixD Q_rd = Utils.getGivensRotationMatrix(rowNum, 2, 3, M2.getElement(2, 1), M2.getElement(3, 1), ring);
        //System.out.println("Q_rd = \n" + Q_rd.toString() + "\n");
        M2 = Q_rd.transpose(ring).multiplyMatr(M2, ring);
        //System.out.println("M2 = \n" + M2.toString() + "\n");

        // 4. Q_ru
        MatrixD Q_ru = Utils.getGivensRotationMatrix(rowNum, 1, 2, M2.getElement(1, 1), M2.getElement(2, 1), ring);
        //System.out.println("Q_ru = \n" + Q_ru.toString() + "\n");
        M2 = Q_ru.transpose(ring).multiplyMatr(M2, ring);

        Q = Q_ld.multiplyMatr(Q_lu, ring).multiplyMatr(Q_rd, ring).multiplyMatr(Q_ru, ring);

        //System.out.println("M2 = \n" + M2.toString() + "\n");
        //System.out.println("Q * M2 = \n" + Q.multiplyMatr(M2, ring).toString());

        return new MatrixD[] {Q, M2};
    }

}

class BlockExecuter extends Thread {
    MatrixD input;
    MatrixD[] output;
    Ring ring;

    public BlockExecuter(MatrixD input, MatrixD[] output, Ring ring) {
        this.input = input;
        this.output = output;
        this.ring = ring;
    }

    @Override
    public void run() {
        try {
            MatrixD[] qr = BlockQR.blockQrForParallelogram(input, ring);
            output[0] = qr[0];
            output[1] = qr[1];
        } catch (WrongDimensionsException e) {
            e.printStackTrace();
        }
    }
}
