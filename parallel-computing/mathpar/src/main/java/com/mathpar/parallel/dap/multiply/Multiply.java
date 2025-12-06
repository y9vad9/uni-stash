/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.mathpar.students.ukma17i41.bosa.parallel.engine;
package com.mathpar.parallel.dap.multiply;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;

import java.util.ArrayList;

/**import com.mathpar.parallel.dap.multiply.MultiplyExtended;
 *
 * @author alla
 */
public class Multiply extends Drop {
  private final static MpiLogger LOGGER = MpiLogger.getLogger(Multiply.class);

    //Зв'язки в графі між дропами, перше значення трійки відповідає за номер дропу в графі,
    // друге - за позицію у вихідному масиві(результат який буде прописуватись в інші дропи),
    // третє - за позицію у вхідному масиві дропа (куди буде прописуватись результат)
    private static int[][] _arcs = new int[][] {
            //Зв'язки від вхідної функції до всіх інших дропів
            {1, 0, 0,   1, 4, 1,   2, 1, 0,   2, 6, 1,   3, 0, 0,   3, 5, 1,   4, 1, 0,   4, 7, 1,
            5, 2, 0,   5, 4, 1,   6, 3, 0,   6, 6, 1,   7, 2, 0,   7, 5, 1,   8, 3, 0,   8, 7, 1},
            {2, 0, 2},//з дропу Множення(дроп 1 в аміні) в дроп номер 2 в цьому аміні, з нульової позиції на другу позицію
            {9, 0, 0},//з дропу Множення з додаванням(дроп 2 в аміні) в дроп номер 9 (масив для вихідної функції), з нульової позиції на нульову позицію
            {4, 0, 2},//з дропу Множення (дроп 3 в аміні) в дроп номер 4 в цьому аміні, з нульової позиції на другу позицію
            {9, 0, 1},//з дропу Множення з додаванням(дроп 4 в аміні) в дроп номер 9 (масив для вихідної функції), з нульової позиції на першу позицію
            {6, 0, 2},//з дропу Множення (дроп 5 в аміні) в дроп номер 6 в цьому аміні, з нульової позиції на другу позицію
            {9, 0, 2},//з дропу Множення з додаванням(дроп 6 в аміні) в дроп номер 9 (масив для вихідної функції), з нульової позиції на другу позицію
            {8, 0, 2},//з дропу Множення (дроп 7 в аміні) в дроп номер 8 в цьому аміні, з нульової позиції на другу позицію
            {9, 0, 3},//з дропу Множення з додаванням(дроп 8 в аміні) в дроп номер 9 (масив для вихідної функції), з нульової позиції на третю позицію
            {}};//відповідає за вихідну функцію, тому пустий

    public Multiply() {
        //Має 2 вхідні матриці та 1 вихідну
        inData =  new Element[2];
        outData =  new Element[1];
        //Дроп множення має тип 0
        type = 0;
        //кількість блоків, для формування результату
        resultForOutFunctionLength = 4;
        inputDataLength = 2;
        outputDataLength = 1;
        //унікальний номер дропа
        number = cnum++;
        arcs = _arcs;
    }

    //Розгортання аміну з дропами, відповідно до графу, для обрахунку поточного дропа.
    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<Drop>();

        amin.add(new Multiply());
        amin.add(new MultiplyAdd());
        amin.add(new Multiply());
        amin.add(new MultiplyAdd());
        amin.add(new Multiply());
        amin.add(new MultiplyAdd());
        amin.add(new Multiply());
        amin.add(new MultiplyAdd());

        return amin;
    }

    //Послідовний обрахунок листових вершин
    @Override
    public void sequentialCalc(Ring ring) {
       // LOGGER.info("in sequentialCalc indata = " + inData[0] + ",  "+inData[1]);
        MatrixS A =  (MatrixS)inData[0];
        MatrixS B =  (MatrixS)inData[1];
        MatrixS C = A.multiply(B, ring);

        outData[0] = C;
    }

    @Override
    //Вхідна функція дропа, розбиває вхідні дані на блоки.
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring ring) {

        MatrixS[] res = new MatrixS[8];
        MatrixS ms = (MatrixS) input[0];
        MatrixS ms1 = (MatrixS) input[1];
        Array.concatTwoArrays(ms.split(), ms1.split(), res);
        return res;

    }

    //Вихідна функція дропа, яка збирає блоки в результат
    @Override
    public MatrixS[] outputFunction(Element[] input, Ring ring) {
        MatrixS[] resmat = new MatrixS[input.length];
        for (int i = 0; i < input.length; i++) {
            resmat[i] = (MatrixS) input[i];
        }
        MatrixS[] res = new MatrixS[] {MatrixS.join(resmat)};
        return res;
    }
    //Перевіряє чи є дроп листовим
    @Override
    public boolean isItLeaf() {
        MatrixS ms = (MatrixS) inData[0];
        return ms.isItLeaf(leafSize,leafdensity);
    }

}
