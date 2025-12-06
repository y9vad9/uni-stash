package com.mathpar.parallel.dap.multiply.MatrixD;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;

import java.util.ArrayList;

public class MatrDMultiplyScalar extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrDMultiplyScalar.class);

    private static int[][] _arcs = new int[][]{
            //Зв'язки від вхідної функції до всіх інших дропів
            {1, 0, 0, 1, 1, 1, 2, 2, 0, 2, 3, 1},
            {3, 0, 0},
            {3, 0, 1},
            {}};

    public MatrDMultiplyScalar() {
        //Має 2 вхідні матриці та 1 вихідну
        inData = new Element[4];
        outData = new Element[1];
        //Дроп має тип 32
        type = 12;
        //кількість блоків, для формування результату
        resultForOutFunctionLength = 2;
        inputDataLength = 4;
        //унікальний номер дропа
        number = cnum++;
        arcs = _arcs;
    }

    //Розгортання аміну з дропами, відповідно до графу, для обрахунку поточного дропа.
    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<Drop>();

        amin.add(new MatrDMult4());
        amin.add(new MatrDMult4());

        return amin;
    }

    //Послідовний обрахунок листових вершин
    @Override
    public void sequentialCalc(Ring ring) {
        // LOGGER.info("in sequentialCalc indata = " + inData[0] + ",  "+inData[1]);
        MatrixD A = (MatrixD) inData[0];
        MatrixD B = (MatrixD) inData[1];
        MatrixD C = (MatrixD) inData[2];
        MatrixD D = (MatrixD) inData[3];
        MatrixD R = A.multiplyMatr(B, ring).add(C.multiplyMatr(D, ring), ring);

        outData[0] = R;
    }

    @Override
    //Вхідна функція дропа, розбиває вхідні дані на блоки.
    public MatrixD[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixD[] res = {(MatrixD) input[0], (MatrixD) input[1], (MatrixD) input[2], (MatrixD) input[3]};
        return res;
    }

    //Вихідна функція дропа, яка збирає блоки в результат
    @Override
    public MatrixD[] outputFunction(Element[] input, com.mathpar.number.Ring ring) {

        MatrixD A = (MatrixD) input[0];
        MatrixD B = (MatrixD) input[1];

        MatrixD[] res = new MatrixD[]{A.add(B, ring)};
        return res;
    }

    //Перевіряє чи є дроп листовим
    @Override
    public boolean isItLeaf() {
        MatrixD ms = (MatrixD) inData[0];
        return (ms.M.length <= leafSize);
    }
}
