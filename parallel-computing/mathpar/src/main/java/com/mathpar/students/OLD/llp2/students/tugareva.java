package com.mathpar.students.OLD.llp2.students;


import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.matrix.*;

class tugareva {
/**
 *
 * @param A
 * @param ring
 * @return
 */
    public static MatrixS JordanForm(MatrixS A, Ring ring) { //параметры матрица А - квадратная, тип MatrixS; ring - C[x]

        ring = new Ring("C64[x]");
        Element d1 = ring.numberONE;
        MatrixS Final = null;//итоговая матрица
        int rank = A.rank(ring);//ранг матрицы
        int str = A.size; //количество строк исходной матрицы
        int stolb = A.colNumb; //количество столбцов исходной матрицы
        if (str != stolb) {//ищем ЖНФ только для квадратных матриц
            System.out.println("Это не квадратная матрица");
            return A;
        }
        MatrixD A1 = new MatrixD(A, true, ring);//создаем матрицу A1 типа MatrixD
        Polynom p = A1.characteristicPolynomP(ring); // характеристический полином
        if (p.coeffs[0].isNegative()) {//если первый коэффициент характеристического полинома отрицательный, меняем знак
            p = (Polynom) p.negate(ring);
        }
        Polynom[] P = p.factorOfPol_inC(ring).multin;//массив множителей характеристического полинома
        MatrixS[] NewA = new MatrixS[str];
        MatrixS[] AA = new MatrixS[str];
        Element[] k1 = new Element[P.length];
        int[][] kletka = new int[str][str];
        int sum = 0;
        MatrixS Ji = null;
        MatrixS[][] Kl = new MatrixS[str][];
        MatrixS[] Klrow = new MatrixS[str];
        int chis_kor = 0;
        int blNiRoot ;
        for (int i = 0; i < P.length; i++) {
            blNiRoot = 0;
            if (P[i].coeffs.length > 1) {
                k1[i] = P[i].coeffs[1].negate(ring);//корни характеристического полинома
            }
            NewA[i] = MatrixS.scalarMatrix(str, k1[i], ring);// матрица lambda*E
            AA[i] = A.subtract(NewA[i], ring);//матрица A-lambda*E
            AA[i] = (MatrixS) AA[i].toNewRing(ring.algebra[0], ring);
            Element ai = k1[i];
            int Npos = 0;// порядковый номер блока i-го корня
            for (int j = 0; j < str; j++) {
                MatrixS FSL = (MatrixS) AA[i].pow(j, ring);
                int rank1 = ((MatrixS) AA[i].pow(j, ring)).rank(ring);
                MatrixS SSL = (MatrixS) AA[i].pow(j + 1, ring);
                int rank2 = ((MatrixS) AA[i].pow(j + 1, ring)).rank(ring);
                MatrixS TSL = (MatrixS) AA[i].pow(j + 2, ring);
                int rank3 = ((MatrixS) AA[i].pow(j + 2, ring)).rank(ring);
                kletka[i][j] = rank1 - 2 * rank2 + rank3;
                if (kletka[i][j] > 0) {
                    blNiRoot += kletka[i][j];
                    int poryadok = j + 1;
                    Ji = MatrixS.zhordan(poryadok, ai, ring);//построение жордановых клеток
                    for (int v = 0; v < kletka[i][j]; v++) {
                        Klrow[Npos++] = Ji;//массив жордановых клеток
                        sum = sum + poryadok;
                    }
                }
            }
            Kl[i] = new MatrixS[blNiRoot];
            System.arraycopy(Klrow, 0, Kl[i], 0, blNiRoot);
        }
        Final = MatrixS.makeBlockDiagonalMatrix(Kl, sum);
        return Final;
    }
}

