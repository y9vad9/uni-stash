package com.mathpar.students.OLD.llp2.student.matveeva;

import java.util.ArrayList;

import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 * @author Юля
 *
 * В классе Eigenvector реализован алгоритм вычисления собственных значений и
 * собственных векторов линейного оператора, заданного матрицей линейного оператора.
 *
 */
public class Eigenvector {

    /**
     * Функция matrS возвращает матрицу типа MatrixS, полученную в результате
     * вычитания из чисел на главной диагонали заданной матрицы m значения l.
     * @param m - матрица типа MatrixD
     * @param l - значение типа Element
     * @param ring - задаваемое кольцо
     * @return матрицу, полученную в результате вычитания из чисел на главной
     * диагонали матрицы m числа l.
     */
    public static MatrixS matrS(MatrixD m, Element l, Ring ring) {
        MatrixS e = MatrixS.scalarMatrix(m.M.length, m.M[0].length, l, ring);
        MatrixS matr = new MatrixS(m, ring);
        MatrixS res = matr.subtract(e, ring);
        return res;
    }

    public static MatrixS matrPol(MatrixD m, Ring ring) {
        // System.out.println("this = " + m.toString(ring));
        // System.out.println("Размерность = " + m.M.length + "x" + m.M[0].length);
        Polynom p = new Polynom("x", ring);
        //Создание диагональной матрицы с полиномом "x" на диагонали
        MatrixS e = MatrixS.scalarMatrix(m.M.length, m.M[0].length, p, ring);
        MatrixS mS = new MatrixS(m, ring);
        MatrixS res = mS.subtract(e, ring);
        return res;
    }
    /**
     * Разбиение матрицы f на векторы и создание массива ненулевых векторов.
     * @param f - матрица типа MatrixS
     * @param ring - задаваемое кольцо
     * @return массив ненулевых векторов
     */
    public static VectorS[] matrixSToVectorS(MatrixS f, Ring ring) {
        MatrixS fTrans = f.transpose();
        int lenf = fTrans.size;
        Element[][] e = new Element[lenf][lenf];
        for (int i = 0; i < fTrans.size; i++) {
            e[i]=fTrans.getRow(i, ring);
        }
        VectorS[] v = new VectorS[lenf];
        ArrayList<VectorS> ar = new ArrayList<VectorS>();
        for (int i = 0; i < lenf; i++) {
            v[i] = new VectorS(e[i]);
            if (!v[i].isZero(ring)) {
                ar.add(v[i]);
            }
        }
        VectorS[] res = new VectorS[ar.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = ar.get(i);
        }
        return res;
    }
    /**
     * Вычисление собственных вектров линейного оператора, заданного матрицей m.
     * Из чисел на главной диагонали матрицы  m вычитается собственное e значение
     * линейного оператора и для полученной матрицы вычисляется ядро. Ненулевые
     * столбцы ядра и есть собственные значения.
     * @param m - матрица линейного оператора типа MatrixD
     * @param e - собственное значение линейного оператора типа Element
     * @param ring - задаваемое кольцо
     * @return массив собственных векторов
     */
    public static VectorS[] eigenvector(MatrixD m, Element e, Ring ring) {
        int lenM = m.M.length;
        MatrixS mS = matrS(m, e, ring);
        int len=mS.size;
        MatrixS ker = mS.kernel(m.M[0].length, ring);
//        for (int i = 0; i < ker.size; i++) {
//            if (ker.M[i].length!=0) {
//                len++;
//            }
//        }
        VectorS[] v = new VectorS[len];
        if (ker==null) {
            MatrixS one = MatrixS.scalarMatrix(m.M.length, m.M[0].length, ring.numberONE, ring);
            v = matrixSToVectorS(one, ring);
        } else {
            v = matrixSToVectorS(ker, ring);
        }
        return v;
    }

    public static Element delta(MatrixS m, Element el, VectorS vS, Ring ring){
        VectorS mvS = (VectorS) m.multiply(vS, ring);
        VectorS elvS = (VectorS)vS.multiply(el, ring);
        VectorS res = mvS.subtract(elvS, ring);
        Element delta = deltaError(res, ring);
        return delta;
    }
    public static Element deltaError(VectorS vS, Ring ring){
        Element max = ring.numberZERO;
        for (int i = 0; i < vS.length(); i++) {
            if (max.compareTo(vS.V[i].abs(ring), -2, ring)) {
                max=vS.V[i];
            }
        }
        return max;
    }

    public static void main(String[] args) throws Exception {
        Ring ring = new Ring("R[x]"); ring.setMachineEpsilonR(100/150);
        ring.setFLOATPOS(50);
        Element g=ring.numberONE.divide(ring.posConst[3], ring);
        System.out.println("ONE THIRED="+g.toString(ring));
        Polynom ppp=new Polynom("x^8-55x^7-518x^6-7185x^5+223116x^4+10609920x^3+105326241x^2+660380880x+6414767996", ring);
    Element[] allComplexRoots= ppp.rootsOfPol_inC(new int[1][0],ring);  // new GI 19-dec-2014
        System.out.println("hhhhhh");
        // ring.setAccuracy(100);
       //// ring.setZERO_R(80);
 //       ring.MachineEpsilonR64 = new NumberR64("0.00000000000000001");
   //     ring.MachineEpsilonR = new NumberR("0.00000000000000001");
       // long[][] mat = new long[][]{{1, 0, 0}, {0, 0, 0}, {0, 0, 0}}; // считает
        //long[][] mat = new long[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}; // считает
        //long[][] mat = new long[][]{{3, 0, 0}, {1, 2, -1}, {1, -1, 2}};
        //long[][] mat = new long[][]{{0, 1, 2}, {4, 0, 1}, {3, -1, 1}};
        //long[][] mat = new long[][]{{2, -1, 3}, {1, 1, -2}, {0, 3, -7}};// считает 3 собственных вектора
        //long[][] mat = new long[][]{{1, 2, 3}, {2, 3, 2}, {3, 4, 5}};
        //long[][] mat = new long[][]{{1, -3, 4}, {4, -7, 8}, {6, -7, 7}}; //считает 2 собственных вектора несимвольно
        //long[][] mat = new long[][]{{1, -3, 3}, {3, -5, 3}, {6, -6, 4}};
        //long[][] mat = new long[][]{{2, -1, 2}, {5, -3, 3}, {-1, 0, -2}}; //считает 1 собственный вектор несимвольно
        //long[][] mat = new long[][]{{4, -1, -2}, {2, 1, -2}, {1, -1, 1}}; // не вычисляет корни характеристического полинома
        //long[][] mat = new long[][]{{1, -3, 4,1,5}, {4, -7, 8,3,2}, {6, -7, 7,-5,4},{3, -1, 2,2,3},{-2, 3, 8,-1,5}};
        /*
         * считает 2 собственных вектора несимвольно
         * косяк с нулевым столбцом в символах
         */
        //long[][] mat = new long[][]{{2, 1, -1, 0}, {0, 0, 0, 0}, {2, 0, -1, 0}, {0, 1, 0, 0}};
        //long[][] mat = new long[][]{{3, -1,0, 0}, {1, 1, 0, 0}, {3, 0, -5,-3}, {4, -1, 3, 1}};
        //long[][] mat = new long[][]{{1, 0,2,-1}, {0, 1, 4, -2}, {2, -1, 0,1}, {2, -1, -1, 2}};
        ///long[][] mat = new long[][]{{10, 2,3,1,1}, {0, 12,1,2,1}, {0, 0,11,1,-1}, {0, 0,0,9,1},{0, 0,0,0,15}};
        //long[][] mat = new long[][]{{1,0, 0, 0,0,0,1}, {0,1, 0, 0,0,0,2}, {0,0, 1, 0,0,0,3},
        //{0,0, 0, 1,0,0,4},{0,0, 0, 0,1,0,5},{0,0, 0, 0,0,1,6},{1,2, 3, 4,5,6,7}}; // считает
         /* Задаем матрицы такие, что характеристический полином имеет нецелочисленные корни
         *  Изначально получены рандомом
         */
        //long[][] mat = new long[][]{{19, 1, 2}, {10, 8, 18}, {26, 11, 15}}; // считает
        //long[][] mat = new long[][]{{4, 20, 15}, {9, 6, 1}, {2, 15, 17}}; // считает
        //long[][] mat = new long[][]{{1, 7,10,11}, {14, 28, 4, 6}, {30, 0, 28,28}, {16, 31, 16, 16}};
        //long[][] mat = new long[][]{{22,14, 13,4,29},{12,4, 24,24,11}, {20,24,14, 26, 31}, {17, 24,21, 17,26}, {1, 23,6, 18, 13}};
       long[][] mat = new long[][]{{0,8,0, 7,6,2,0,0},{0,0,0, 0,0,14,0,0}, {0,7,2, 0,8,5,0,10}, {15,0,26, 24,5,0,28,27},
       {0,28,24, 13,0,4,27,0},{30,0,29, 26,24,16,7,19},{0,0,12, 28,0,0,13,4},{31,1,0, 3,0,0,0,0}};
        /*
        * [0 27 25 9 2]
          [24 18 23 17 8]
          [17 23 12 27 27]
          [25 26 7 26 3]
          [15 23 28 3 14]
        */
        /*[0 29 0 0 0 0]
          [17 14 7 0 30 9]
          [25 26 0 27 0 17]
          [2 19 15 17 20 18]
          [9 0 0 0 15 16]
          [7 12 5 21 0 0]
         *
         */
        MatrixD A = new MatrixD(mat, ring);
        System.out.println("МАТРИЦА ЛИНЕЙНОГО ОПЕРАТОРА  = " + A);
        charPolynomMatrixD AA = new charPolynomMatrixD(A);
        Polynom charPol = AA.charPolSeifullinP(ring);
        charPol = charPol.ordering(ring);
        charPol = (Polynom) charPol.toNewRing(ring.algebra[0], ring);
        System.out.println("ХАРАКТЕРИСТИЧЕСКИЙ ПОЛИНОМ = " + charPol.toString(ring));
        IndexedList i1 = charPol.roots_inC(ring);
        int[][] multiplicity=new int[1][0];
        Element[] list= ppp.rootsOfPol_inC( multiplicity, ring);  // new GI 19-dec-2014
        // Собственные значения
        // Кратность собственных значений
        int[] ind = multiplicity[0];
        VectorS[] vS=null;
        Element delta =null;
        System.out.println("Собственные значения: " + Array.toString(i1.list) +
                ", кратность:  " + Array.toString(i1.ind));

        // ring.setMachineEpsilonR(100);
        for (int i = 0; i < list.length; i++) {
            vS = eigenvector(A, list[i], ring);
            System.out.println("СОБСТВЕННОЕ ЗНАЧЕНИЕ = " + list[i] +
                    ", СОБСТВЕННЫЕ ВЕКТОРЫ = " + Array.toString(vS));
            System.out.println( ", СОБСТВЕННЫЕ ВЕКТОРЫ = " + Array.toString(vS));
            MatrixS MS = new MatrixS(A, ring);
            for (int j = 0; j < vS.length; j++) {
                delta = delta(MS, list[i], vS[j], ring);
            }
           //// System.out.println("ПОГРЕШНОСТЬ = "+delta);
            //(587838.88-10108862.48\i)
        }
    }
}
 
