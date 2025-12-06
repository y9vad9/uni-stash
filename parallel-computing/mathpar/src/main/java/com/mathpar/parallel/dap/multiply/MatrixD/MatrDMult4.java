package com.mathpar.parallel.dap.multiply.MatrixD;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;

import java.util.ArrayList;

public class MatrDMult4 extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrDMult4.class);
    private static int[][] _arcs = new int[][]{
            {1, 0, 0, 1, 4, 1, 1, 1, 2, 1, 6, 3, 2, 0, 0, 2, 5, 1, 2, 1, 2, 2, 7, 3,
                    3, 2, 0, 3, 4, 1, 3, 3, 2, 3, 6, 3, 4, 2, 0, 4, 5, 1, 4, 3, 2, 4, 7, 3},
            {5, 0, 0},
            {5, 0, 1},
            {5, 0, 2},
            {5, 0, 3},
            {}};

    public MatrDMult4() {
        //Має 2 вхідні матриці та 1 вихідну
        inData = new Element[2];
        outData = new Element[1];
        //Дроп має тип 28
        type = 10;
        //кількість блоків, для формування результату
        resultForOutFunctionLength = 4;
        inputDataLength = 2;
        //унікальний номер дропа
        number = cnum++;
        arcs = _arcs;
    }

    //Розгортання аміну з дропами, відповідно до графу, для обрахунку поточного дропа.
    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<Drop>();

        amin.add(new MatrDMultiplyScalar());
        amin.add(new MatrDMultiplyScalar());
        amin.add(new MatrDMultiplyScalar());
        amin.add(new MatrDMultiplyScalar());

        return amin;
    }

    //Послідовний обрахунок листових вершин
    @Override
    public void sequentialCalc(Ring ring) {
        // LOGGER.info("in sequentialCalc indata = " + inData[0] + ",  "+inData[1]);
        MatrixD A = (MatrixD) inData[0];
        MatrixD B = (MatrixD) inData[1];
        MatrixD C = A.multiplyMatr(B, ring);

        switch (key){
            case(0): outData[0] = C; break;
            case(1): outData[0] = C.negate(ring); break;
        }
    }

    @Override
    //Вхідна функція дропа, розбиває вхідні дані на блоки.
    public MatrixD[] inputFunction(Element[] input, Amin amin, Ring ring) {

        MatrixD[] res = new MatrixD[8];
        MatrixD ms = (MatrixD) input[0];
        MatrixD ms1 = (MatrixD) input[1];
        Array.concatTwoArrays(ms.splitTo4(), ms1.splitTo4(), res);
        return res;

    }

    //Вихідна функція дропа, яка збирає блоки в результат
    @Override
    public MatrixD[] outputFunction(Element[] input, com.mathpar.number.Ring ring) {
        MatrixD[] resmat = new MatrixD[input.length];
        for (int i = 0; i < input.length; i++) {
            resmat[i] = (MatrixD) input[i];
        }

        MatrixD[] res;
        if (key == 0) res = new MatrixD[]{MatrixD.join(resmat)};
        else
            res = new MatrixD[]{MatrixD.join(resmat).negate(ring)};

        return res;
    }

    //Перевіряє чи є дроп листовим
    @Override
    public boolean isItLeaf() {
        MatrixD ms = (MatrixD) inData[0];
        return (ms.M.length <= leafSize);
    }

}
