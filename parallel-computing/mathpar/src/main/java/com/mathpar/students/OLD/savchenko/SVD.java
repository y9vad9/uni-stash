package com.mathpar.students.OLD.savchenko;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;
import com.mathpar.students.OLD.savchenko.exception.WrongDimensionsException;

public class SVD {

    public static MatrixD[] getSVD(MatrixD A, Ring ring, boolean blockQR) throws WrongDimensionsException {
        double globalStart = System.nanoTime();

        // 1. QR-разложение входной матрицы A.
        MatrixD[] qr;
        double st = System.nanoTime();
        if (blockQR) {
            qr = BlockQR.blockQR(A, ring);
        } else {
            qr = givensQR(A, ring);
        }
        double en = System.nanoTime();
        double lastTimeSec = ((en - st) / 1000000000);
        System.out.println("Time for QR: " + lastTimeSec + " seconds.");
        MatrixD Q = qr[0];
        MatrixD R = qr[1];
//        System.out.println("Матрица Q = ");
//        System.out.println(Q.toString() + "\n");
//        System.out.println("Правая треугольная матрица R = ");
//        System.out.println(R.toString() + "\n");
//        System.out.println("---------- Проверка: Матрица Q*R = ");
//        System.out.println(Q.multiplyMatr(R, ring).toString() + "\n");

        // 2. Приведение матрицы R к двухдиагональному виду (D2).
        MatrixD Rt = R.transpose(ring);
        st = System.nanoTime();
        MatrixD[] lr = leftTriangleToBidiagonal(Rt, ring);
        en = System.nanoTime();
        lastTimeSec = ((en - st) / 1000000000);
        System.out.println("Time for D2: " + lastTimeSec + " seconds.");
        MatrixD L1 = lr[0];
        MatrixD R1 = lr[1];
        MatrixD D2 = L1.multiplyMatr(Rt, ring);
        D2 = D2.multiplyMatr(R1, ring);
//        System.out.println("D2 = \n" + D2.toString() + "\n");

        // 3. Приведение матрицы D2 к диагональному виду (D1).
        st = System.nanoTime();
        lr = bidiagonalToDiagonal(D2, ring);
        en = System.nanoTime();
        lastTimeSec = ((en - st) / 1000000000);
        System.out.println("Time for D2 ---> D1: " + lastTimeSec + " seconds.");
        MatrixD L2 = lr[0];
        MatrixD R2 = lr[1];
        MatrixD D1 = L2.multiplyMatr(D2, ring);
        D1 = D1.multiplyMatr(R2, ring);
//        Utils.removeNonDiagonalValues(D1, ring);
//        System.out.println("D1 = \n" + D1.toString() + "\n");

        // 4. Расчет SVD разложения для входной матрицы A.
        MatrixD U = Q.multiplyMatr(R1, ring).multiplyMatr(R2, ring);
        MatrixD V = L2.multiplyMatr(L1, ring);
        MatrixD A1 = U.multiplyMatr(D1, ring).multiplyMatr(V, ring);
//        System.out.println("Проверка SVD разложения. U*D1*V = \n");
//        System.out.println(A1.toString());

        double globalEnd = System.nanoTime();
        double globalTime = ((globalEnd - globalStart) / 1000000000);
        System.out.println("Time all: " + globalTime + " seconds.");
        return new MatrixD[] {U, D1, V, A1};
    }

    /**
     * Returns matrices Q, R such that Q*R = A
     */
    public static MatrixD[] givensQR(MatrixD A, Ring ring) throws WrongDimensionsException {
        int colCounter = 1;
        int n = A.rowNum();
        MatrixD Q = MatrixD.ONE(n, ring);
        MatrixD R = A.copy();
        MatrixD GTemp;

        for (int i=0; i<n-1; i++) {
//            System.out.println("ИТЕРАЦИЯ " + i  + "\n");
            for (int j=n-1; j>colCounter-1; j--) {
                if (Math.abs(R.getElement(j, i).doubleValue()) > 0) {
//                    System.out.println("ОБНУЛЯЕМ ЭЛЕМЕНТ " + j + ", " + i + "\n");
                    GTemp = Utils.getGivensRotationMatrix(n, j-1, j, R.getElement(j-1, i), R.getElement(j, i), ring);
//                    System.out.println("МАТРИЦА ВРАЩЕНИЯ = " + "\n");
//                    System.out.println(GTemp.toString()+ "\n");
                    Q = Utils.rightMultiplyMatrixToGivens(Q, GTemp, j-1, j, ring);
                    R = Utils.leftMultiplyGivensToMatrix(GTemp.transpose(ring), R, j-1, j, ring);
//                    System.out.println("МАТРИЦА ВРАЩЕНИЯ t * Temp = " + "\n");
//                    System.out.println(R.toString() + "\n");
                }
            }
            colCounter++;
        }

        return new MatrixD[]{Q, R};
    }

    // Возвращает матрицы L, R. Матрица D2 = L*A*R имеет двухдиагональный вид.
    public static MatrixD[] leftTriangleToBidiagonal(MatrixD A, Ring ring) throws WrongDimensionsException {
        if (A.rowNum() != A.colNum())
            throw new WrongDimensionsException();

        int n = A.rowNum();
        MatrixD left;
        MatrixD right;
        MatrixD Temp = A.copy();
        MatrixD L = MatrixD.ONE(n, ring);
        MatrixD R = MatrixD.ONE(n, ring);

//        System.out.println("Обнуляем элементы в i-том столбце снизу вверх и i-той строке строке (если это не 'верхний y') \n");

        for (int col=0; col<(n-1); col++) {
            for (int row=(n-1); row>(col); row--) {
                left = Utils.getGivensRotationMatrix(n, row-1, row, Temp.getElement(row-1, col), Temp.getElement(row, col), ring);
                left = left.transpose(ring);
                L = Utils.leftMultiplyGivensToMatrix(left, L, row-1, row, ring);
                Temp = Utils.leftMultiplyGivensToMatrix(left, Temp, row-1, row, ring);
                // System.out.println("Испортился ноль в " + (row-1) + ", " + row);
                if (row > (col+1)) {                                                        // Убираем y-ки если это не "верхний" y
                    int i = row-1;
                    int j = row;
                    right = Utils.getGivensRotationMatrix(n, j-1, j, Temp.getElement(i, j-1), Temp.getElement(i, j), ring);
                    R = Utils.rightMultiplyMatrixToGivens(R, right, j-1, j, ring);
                    Temp = Utils.rightMultiplyMatrixToGivens(Temp, right, j-1, j, ring);
                }
            }
//            System.out.println("После " + (col+1) + " итерации матрица имеет вид \n");
//            System.out.println(Temp.toString() + "\n");
        }

        return new MatrixD[]{L, R};
    }

    // Возвращает матрицы L, R, D1. Матрица D1 = L*A*R имеет диагональный вид.
    public static MatrixD[] bidiagonalToDiagonal(MatrixD A, Ring ring) throws WrongDimensionsException {
        if (A.rowNum() != A.colNum())
            throw new WrongDimensionsException();

        int n = A.rowNum();
        MatrixD left;
        MatrixD right;
        MatrixD Temp = A.copy();
        MatrixD L = MatrixD.ONE(n, ring);
        MatrixD R = MatrixD.ONE(n, ring);

//        System.out.println("Матрица имеет двухдиагональный вид. \n " +
//                "Применяем последовательное обнуление верхней/нижней диагонали, пока |элементы| > epsilon \n");
        boolean side = true;
        int iterations = 0;

        while (!Utils.checkSecondDiagonalValues(Temp, n, ring)) {
            if (side) {                                                  // right
                for (int i=0; i<(n-1); i++) {
                    int j = i+1;
                    if (!Temp.getElement(i, j).isZero(ring)) {
                        right = Utils.getGivensRotationMatrix(n, j - 1, j, Temp.getElement(i, j - 1), Temp.getElement(i, j), ring);
                        R = Utils.rightMultiplyMatrixToGivens(R, right, j-1, j, ring);
                        Temp = Utils.rightMultiplyMatrixToGivens(Temp, right, j-1, j, ring);
                        iterations++;
                    }
                }
            } else {                                                     // left
                for (int j=0; j<(n-1); j++) {
                    int i = j+1;
                    if (!Temp.getElement(i, j).isZero(ring)) {
                        left = Utils.getGivensRotationMatrix(n, i - 1, i, Temp.getElement(i - 1, j), Temp.getElement(i, j), ring);
                        left = left.transpose(ring);
                        L = Utils.leftMultiplyGivensToMatrix(left, L, i-1, i, ring);
                        Temp = Utils.leftMultiplyGivensToMatrix(left, Temp, i-1, i, ring);
                        iterations++;
                    }
                }
            }
            side = !side;
//            System.out.println("После " + iterations + " итерации матрица имеет вид \n");
//            System.out.println(Temp.toString() + "\n");
        }

        //System.out.println("        Количество итераций для получения диагональной матрицы = " + iterations + ".");
        return new MatrixD[]{L, R, Temp};
    }

}
