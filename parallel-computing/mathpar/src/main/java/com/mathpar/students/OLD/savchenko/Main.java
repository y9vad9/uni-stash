package com.mathpar.students.OLD.savchenko;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.NumberR;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.students.OLD.savchenko.exception.WrongDimensionsException;

public class Main {

    public static void main(String[] args) throws WrongDimensionsException {
        //runExperiment1();
        //runExperiment2();
        //runExperiment3();
        runExperiment4();
    }

    /**
     * Сравнение реализованых алгоритмов SVD разложения.
     */
    public static void runExperiment4() throws WrongDimensionsException {
        Ring ring = new Ring("R64[x]");
        ring.setFLOATPOS(100);
        ring.setMachineEpsilonR64(40);

        int[] dimensions = {8, 16, 32, 64, 128};

        MatrixD A, check, difference;
        MatrixD[] svd;

        for (int i : dimensions) {
            System.out.println("Dimension = " + i);

            System.out.println("1. Standard SVD algorithm with simple QR");
            A = TestData.getTestMatrix(i, ring);
            svd = SVD.getSVD(A, ring, false);
            check = svd[3].multiplyByScalar(ring.numberMINUS_ONE, ring);
            difference = A.add(check, ring);
            System.out.println("diff = " + difference.max(ring).abs(ring).toString(ring) + ". \n");

            System.out.println("2. Standard SVD algorithm with Block QR");
            A = TestData.getTestMatrix(i, ring);
            svd = SVD.getSVD(A, ring, true);
            check = svd[3].multiplyByScalar(ring.numberMINUS_ONE, ring);
            difference = A.add(check, ring);
            System.out.println("diff = " + difference.max(ring).abs(ring).toString(ring) + ". \n");

            System.out.println("3. New SVD by QR-algorithm with Block QR");
            A = TestData.getTestMatrix(i, ring);
            svd = QrAlgorithm.getSVD(A, ring, true);
            check = svd[3].multiplyByScalar(ring.numberMINUS_ONE, ring);
            difference = A.add(check, ring);
            System.out.println("diff = " + difference.max(ring).abs(ring).toString(ring) + ". \n");

            System.out.println("------------------------------------------------------------");
        }
    }

    /**
     * Сравнение обычного и блочного алгоритма QR разложения
     */
    public static void runExperiment3() throws WrongDimensionsException {
        Ring ring = new Ring("R64[x]");
        ring.setFLOATPOS(100);                                                  // количество выводимых знаков после точки

        int[] dimensions = {8, 16, 32, 64, 128, 256, 512, 1024};
        MatrixD mainMatrix = TestData.getTestMatrix(dimensions[dimensions.length-1], ring);
        MatrixD A, check, difference;
        double st, en, lastTimeSec;

        MatrixD[] qr;

        for (int i : dimensions) {
            System.out.println("Simple QR n = " + i);
            A = Utils.getSubMatrix(mainMatrix, 0, i-1, 0, i-1);
            st = System.nanoTime();
            qr = SVD.givensQR(A, ring);
            en = System.nanoTime();
            lastTimeSec = ((en - st) / 1000000000);
            check = qr[0].multiplyMatr(qr[1], ring);
            check = check.multiplyByScalar(ring.numberMINUS_ONE, ring);
            difference = A.add(check, ring);
            System.out.println("diff = " + difference.max(ring).abs(ring).toString(ring) + ". " +
                    "Time elapsed: " + lastTimeSec + " seconds.");

            System.out.println("-------------------------------------------------------");

            System.out.println("BlockQR n = " + i);
            A = Utils.getSubMatrix(mainMatrix, 0, i-1, 0, i-1);
            st = System.nanoTime();
            qr = BlockQR.blockQR(A, ring);
            en = System.nanoTime();
            lastTimeSec = ((en - st) / 1000000000);
            check = qr[0].multiplyMatr(qr[1], ring);
            check = check.multiplyByScalar(ring.numberMINUS_ONE, ring);
            difference = A.add(check, ring);
            System.out.println("diff = " + difference.max(ring).abs(ring).toString(ring) + ". " +
                    "Time elapsed: " + lastTimeSec + " seconds.");

            System.out.println("***************************************************************************************************************");
        }
    }

    /**
     * Сравнение ошибки и времени в зависимости от точности и размера матрицы для NumberR64 (Double)
     */
    public static void runExperiment2() throws WrongDimensionsException {
        Ring ring = new Ring("R64[x]");
        ring.setFLOATPOS(100);                                                  // количество выводимых знаков после точки

        int[] dimensions = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        int[] machineEpsilon = {20, 40};
        MatrixD mainMatrix = TestData.getTestMatrix(dimensions[dimensions.length-1], ring);

        for (int j : machineEpsilon) {
            ring.setMachineEpsilonR64(j);                                       // машинный ноль
            NumberR64 zero = ring.MachineEpsilonR64;
            System.out.println("MachineEpsilonR64 = " + j /*zero.toString(ring)*/ + "\n");

            for (int i : dimensions) {
                MatrixD A = Utils.getSubMatrix(mainMatrix, 0, i-1, 0, i-1);
                MatrixD[] svd = SVD.getSVD(A, ring, false);
                MatrixD A1 = svd[3].multiplyByScalar(ring.numberMINUS_ONE, ring);
                MatrixD difference = A.add(A1, ring);
                System.out.println("n = " + i + ". " +
                        "Difference = " + difference.max(ring).abs(ring).toString(ring));

                System.out.println("------------------------------------------------------------------------------------");
            }

            System.out.println("******************************************************************");
        }
    }

    /**
     * Сравнение времени и ошибки в зависимости от от точности и машинного нуля в NumberR.
     */
    public static void runExperiment1() throws WrongDimensionsException {
        Ring ring = new Ring("R[x]");
        ring.setFLOATPOS(100);                                              // количество выводимых знаков после точки

        int n = 30;
        int[] accuracy = {100, 80, 60, 40, 20};

        MatrixD A = TestData.getTestMatrix(n, ring);
        MatrixD[] svd;
        MatrixD A1;
        MatrixD difference;

        for (int i = 0; i < accuracy.length; i++) {
            System.out.println("Эксперимент №" + (i+1));
            System.out.println("Accuracy = " + accuracy[i]);
            System.out.println("Machine epsilon = " + (accuracy[i] - 10));

            ring.setAccuracy(accuracy[i]);                                  // количество знаков после точки
            ring.setMachineEpsilonR(accuracy[i] - 10);                      // машинный ноль

            NumberR zero = ring.MachineEpsilonR;
            System.out.println("Машинный ноль = " + zero.toString(ring) + "\n");

            svd = SVD.getSVD(A, ring, false);
            A1 = svd[3].multiplyByScalar(ring.numberMINUS_ONE, ring);
            difference = A.add(A1, ring);
            System.out.println("n = " + n + ". " +
                    "Difference = " + difference.max(ring).abs(ring).toString(ring));

            System.out.println("------------------------------------------------------------------------------------");
        }
    }

}
