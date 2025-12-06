package com.mathpar.matrix;

import com.mathpar.func.F;
import com.mathpar.number.*;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.stat.MS.AELDU;
import com.mathpar.polynom.Polynom;
import mpi.MPIException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * <B>Класс разреженных квадратных матриц c коэффициентами Element.</B>
 * <p>
 * Содержит арифметические матричные операции для квадратных матриц: <br>
 * add, subtractSquareMat, negate, multiplySquareMat, multiplyRecursive (параллельная процедура)
 * <p>  для прямоугольных матриц: <br>
 * add, add(MatrixS b, int m),
 * <p>  операции умножения и точного деления на число <br>
 * multiplyByScalar, divideByNumber,
 * <p> вычисление скалярного произведения матричных векторов квадратных матриц
 * <br> scalarMultiply(A1,A2,B1,B2), <br>
 * а также вычисляются следующие матричные функции: <br>
 * * det - детерминант, <br>
 * * adjoint - присоединенная матрица,<br>
 * * kernal - ядро оператора, <br>
 * * toEchelonForm - эшелонная форма,<br>
 * * transpose - транспонирование, <br>
 * * rem - остаток по модулю p  в интервале [-p+1, p-1], <br>
 * * mod - остаток по модулю p  в интервале[(-p+1)/2,(p-1)/2].
 * <p> Методы определения типа матрицы, возвращающие ДА/НЕТ: <br>
 * isZero - матрица нулевая? <br>
 * isScalarMatrix  - матрица скалярная (т.е. вида a*I)? <br>
 * isEqual -  матрицы равны? <p>
 * Операции  copy, columnsNumber, возвращающие копию матрицы и число столбцов,
 * expandToPow2with0, expandToPow2with1, дополняющие до квадратной матрица
 * порядка степени числа 2 нулевыми или  нулевыми и единичным блоком,  и др.
 * <p>
 * Содержатся все эти операции по модулю простого числа p. Названия этих
 * методов отличаются тем, что к названиям добавляется окончание "Mod".
 * <p>
 * SMALLESTBLOCK - статическое поле типа int - определяет размер матрицы,
 * которая должна умножаться процедурой multiplySquareMat (последовательной процедурой).
 * При размере превышающем SMALLESTBLOCK матрица умножается процедурой
 * multiplyRecursive (параллельная рекурсивная процедура). <br>
 * set_BorderMatrixSize(int numberOfVar) - метод для установки значения этого поля.
 * <p>
 * * p - cтатическое поле Element - определяет простой модуль, т.е. кольцо Z/pZ.
 * <br> set_p(Element mod) - metod для установкb значения этого поля. <br>
 * <p>
 * Copyright: Copyright (c) 2009<br>
 * Company:  ParCA  <br>
 *
 * @author gennadi
 * @version 2.0
 */
public class MatrixS extends Element implements Serializable {
    static final long serialVersionUID = 10000000000101L;
    /**
     * число строк в матрице, обычно это степень 2-х
     */
    public int size = 0;

    public int colSize = -1;
    /**
     * число столбцов в матрице, т.е. номер последнего ненулевого столбца
     */
    public int colNumb = 0;
    /**
     * Ненулевые элементы в строках матрицы, они могут быть неупорядочены,
     * M.length - число строк в матрице M,
     * число столбцов, в общем случае, неопределено
     */
    public Element[][] M;
    /**
     * Номера столбцов соответствующих элементов матрицы M.
     * col.length=M.length, col[i].length=M[i].length  для свех i.
     */
    public int[][] col;
//    /** Установка граничного порядка матрицы, для параллельного умножения
//     * @param s  значение граничного порядка матриц для инициализации.
//     */
//    public static void set_BorderMatrixSize(int s, Ring ring) {
//        ring.SMALLESTBLOCK = s;
//    }


    public MatrixS() {
    }

    /**
     * Конструктор
     *
     * @param r массив элементов матрицы
     * @param c номера столбцов соответствующих элементов
     */


    public MatrixS(Element[][] r, int[][] c) {
        size = c.length;
        M = r;
        col = c;
        int len = col.length;
        int m = 0;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < col[i].length; j++) {
                m = Math.max(m, col[i][j]);
            }
        }
        colNumb = ++m;
    }


    /**
     * Конструктор
     *
     * @param n - число строк в матрице
     * @param r массив элементов матрицы
     * @param c номера столбцов соответствующих элементов
     */
    public MatrixS(int size, int colNumb, Element[][] r, int[][] c) {
        this.size = size;
        this.colNumb = colNumb;
        M = r;
        col = c;
    }

    /**
     * Конструктор матрицы   размера 1x1.
     *
     * @param d единственный элемент матрицы
     */
    public MatrixS(Element d) {
        size = 1;
        colNumb = 1;
        M = new Element[][]{{d}};
        col = new int[][]{{0}};
    }

    @Override
    public boolean isItNumber() {
        return false;
    }

    /**
     * Является ли матрица нулевой?
     *
     * @return true   если  M=0,   false  если не равна 0.
     */
    @Override
    public Boolean isZero(Ring r) {
        for (int i = 0; i < M.length; i++) {
            if ((M[i] != null) && (M[i].length != 0)) {
                for (int j = 0; j < M[i].length; j++) {
                    if (!M[i][j].isZero(r)) {
                        return false;
                    }
                }
                M[i] = new Element[0];
                col[i] = new int[0];
            }
        }
        return true;
    }

    /**
     * Является ли данная матрица единичной?
     *
     * @return true если матриц единичная,  false - если матрица не единичная.
     */
    @Override
    public Boolean isOne(Ring ring) {
        if ((M[0] == null) || (M[0].length != 1) || (size != M.length)) {
            return false;
        }
        Element M0 = ring.numberONE;
        for (int i = 0; i < M.length; i++) {
            Element[] MM = M[i];
            if ((MM.length != 1) || (!MM[0].isOne(ring)) || (col[i][0] != i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Element mat, Ring r) {
        MatrixS matS = (MatrixS) mat;
        return (subtract(matS, r)).isZero(r);
    }
//
//    /**  Возвращает Element myOne если матрица ненулевая,  иначе null
//     *
//     * @return  {Element myOne}, but if M=0 then returns null .
//     */
//    public Element myOne(Ring ring) {
//        for (int i = 0; i < M.length; i++) {
//            if (M[i].length != 0) {
//                return M[i][0].ring.numberONE;
//            }
//        }
//        return null;
//    }

    /**
     * Возвращает Element One если матрица ненулевая,  иначе null
     *
     * @return {Element ONE}, but if M=0 then returns null .
     */
    @Override
    public Element one(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            if (M[i].length != 0) {
                return M[i][0].one(ring);
            }
        }
        return null;
    }

    /**
     * Равны ли матрицы?
     *
     * @return true если матрицы равны,  false - если матрицы не равны.
     */
    public boolean isEqual(MatrixS b, Ring r) {
        if ((size != b.size) || (colNumb != colNumb)) {
            //   if (col.length!=b.col.length)
            return false;
        } else {
            return (subtract(b, r).isZero(r));
        }
    }

    /**
     * Является ли данная матрица скалярной?
     *
     * @return true если матриц cкалярная,  false - если матрица не скалярная.
     */
    public boolean isScalarMatrix() {
        if ((M[0].length != 1) || (size != M.length)) {
            return false;
        }
        Element M0 = M[0][0];
        for (int i = 1; i < M.length; i++) {
            Element[] MM = M[i];
            if ((MM.length != 1) || (MM[0] != M0) || (col[i][0] != i)) {
                return false;
            }
        }
        return true;
    }


    public MatrixS multiplyMT(MatrixS b, int totalThreadsForMult, ElementBufferAllocator buffers) {
        int resN = size;
        int resM = b.colNumb;
        MatrixS res = new MatrixS();
        res.M = new Element[resN][];
        res.col = new int[resN][];
        buffers.checkBuffurSize(b.colNumb);
        buffers.setCounter(0);

        matrixMTrunner[] mtRunner = new matrixMTrunner[totalThreadsForMult];
        for (int i = 0; i < totalThreadsForMult; i++) {
            mtRunner[i] = new matrixMTrunner(this, b, res, buffers, i);
        }
        for (int i = 0; i < totalThreadsForMult; i++) {
            mtRunner[i].start();
        }
        for (int i = 0; i < totalThreadsForMult; i++) {
            try {
                mtRunner[i].join();
            } catch (InterruptedException e) {
                System.out.println("Exception while wait created thread, " + e.toString());
            }
        }
        int maxI = -1, maxJ = -1;
        for (int i = 0; i < totalThreadsForMult; i++) {
            maxI = Math.max(mtRunner[i].maxI, maxI);
            maxJ = Math.max(mtRunner[i].maxJ, maxJ);
        }
        buffers.setCounter(0);
        res.size = maxI + 1;
        res.colNumb = maxJ + 1;
        return res;
    }

        public String toPrintAsIs(Ring ring) {    
        StringBuilder sb=new StringBuilder();
        sb.append("(matrixS): size=").append(size )
                .append("; colNumb=").append(colNumb);
        sb.append(" ;M.length=").append(M.length);
        if (M.length!=col.length){sb.append("\\n M.length!=col.length: ")
                .append(M.length).append(" != ").append(col.length);}
        else{
            for (int i = 0; i < M.length; i++) {
             if ((M[i]==null)||(col[i]==null)){
              if (M[i]==null)
               {sb.append("\n FOR i=").append(i)
                .append(": M[i]==null");}
                if (col[i]==null){sb.append("  FOR i=").append(i)
                   .append(": col[i]==null");}
                break;
              }
              if (M[i].length!=col[i].length){sb.append("\\n FOR i=").append(i)
                .append(": M[i].length!=col[i].length: ")
                .append(M[i].length).append(" != ").append(col[i].length);
                break;
              }
               sb.append("\n[<").append(i).append(">[");
               for (int j = 0; j < M[i].length; j++) {                
                 sb.append(" (").append(col[i][j]);sb.append(")");sb.append(M[i][j]);
                 sb.append("]");
            }}}
        return sb.append("(END)").toString();
    }
    
    /**
     * To array which size is: Size x Size
     *
     * @param ring
     * @return
     */
    public Element[][] toSquaredArray(int Size, Ring ring) {
        Element[][] a = toScalarArray(ring);
        int N = Size;
        int n = a.length;  // rows number
        Element[][] b = new Element[N][];
        Element zero = ring.numberZERO;
        Element[] R = new Element[N];
        for (int i = 0; i < N; i++) {
            R[i] = zero;
        }
        int m = (n != 0) ? a[0].length : 0;       // columns number
        if (N > m) {
            for (int i = 0; i < n; i++) {
                Element[] bR = new Element[N];
                System.arraycopy(a[i], 0, bR, 0, m);
                System.arraycopy(R, 0, bR, m, N - m);
                b[i] = bR;
            }
        } else {
            for (int i = 0; i < n; i++) { b[i] = a[i]; }
        }
        if (N > n) {
            for (int i = n; i < N; i++) { b[i] = R; }
        }
        return b;
    }

    /**
     * To array which size is: this.size x this.size
     *
     * @param ring
     * @return
     */
    public Element[][] toSquaredArray(Ring ring) {
        return toSquaredArray(size, ring);
    }

    public Element[][] toScalarArray(Ring ring, Element zero) {
        int maxCol = 0;
        int len = Math.min(size, col.length);
        for (int i = 0; i < len; i++) {
            int[] CC = col[i];
            for (int j = 0; j < CC.length; j++) {
                maxCol = Math.max(maxCol, CC[j]);
            }
        }
        maxCol++;
        Element[][] res = new Element[size][maxCol];
        for (int i = 0; i < len; i++) {
            Element[] RR = res[i];
            Element[] MM = M[i];
            int[] CC = col[i];
            for (int j = 0; j < maxCol; j++) {
                RR[j] = zero;
            }
            for (int j = 0; j < CC.length; j++) {
                RR[CC[j]] = MM[j];
            }
        }
        for (int i = len; i < size; i++) {
            Element[] RR = res[i];
            for (int j = 0; j < maxCol; j++) {
                RR[j] = zero;
            }
        }
        return res;
    }


    /**
     * Преобразование матрицы в двумерный плотный массив.
     * все строки одной длины
     *
     * @param ring --Ring
     * @return матрица в виде массива Element[][]
     */
    public Element[][] toScalarArray(Ring ring) {
        Element one = ring.numberONE;
        if (one != null) {
            Element zero = one.zero(ring);
            int maxCol = 0;
            int len = Math.min(size, col.length);
            for (int i = 0; i < len; i++) {
                int[] CC = col[i];
                if(CC!=null){
                for (int j = 0; j < CC.length; j++) {
                    maxCol = Math.max(maxCol, CC[j]);
                }}
            }
            maxCol++;
            Element[][] res = new Element[size][maxCol];
            for (int i = 0; i < len; i++) {
                Element[] RR = res[i];
                Element[] MM = M[i];
                int[] CC = col[i];
                for (int j = 0; j < maxCol; j++) {
                    RR[j] = zero;
                }
                if(CC!=null){
                for (int j = 0; j < CC.length; j++) {
                    RR[CC[j]] = MM[j];
                }}
            }
            for (int i = len; i < size; i++) {
                Element[] RR = res[i];
                for (int j = 0; j < maxCol; j++) {
                    RR[j] = zero;
                }
            }
            return res;
        } else {
            return new Element[0][0];
        }
    }

    public MatrixS(VectorS d, Ring r) {
        MatrixS s = new MatrixS(new Element[][]{d.V}, r);
        M = s.M;
        col = s.col;
        size = s.size;
        colNumb = s.colNumb;
    }

    /**
     * Конструктор
     *
     * @param r массив элементов матрицы
     * @param c номера столбцов соответствующих элементов
     */
    public MatrixS(VectorS[] s, Ring ring) {
        Element[][] res = new Element[s[0].V.length][s.length];
        for (int i = 0; i < s.length; i++) {
            VectorS el = s[i];
            for (int j = 0; j < el.V.length; j++) {
                res[j][i] = el.V[j];
            }
        }
        MatrixS result = new MatrixS(res, ring);
        size = result.size;
        M = result.M;
        col = result.col;
        colNumb = result.colNumb;
    }

    public MatrixS(Element[] d, Ring r) {
        MatrixS s = new MatrixS(new Element[][]{d}, r);
        M = s.M;
        col = s.col;
        size = s.size;
        colNumb = s.colNumb;
    }

    public MatrixS(MatrixD d, Ring r) {
        MatrixS s = new MatrixS(d.M, r);
        M = s.M;
        col = s.col;
        size = s.size;
        colNumb = s.colNumb;
    }

    public MatrixS(F f, Ring r) {
        MatrixD d = new MatrixD(f);
        MatrixS s = new MatrixS(d.M, r);
        M = s.M;
        col = s.col;
        size = s.size;
        colNumb = s.colNumb;
    }

    public MatrixS(Polynom[] p) {
        int n = p.length;
        Element[][] MM = new Element[n][];
        int[][] CC = new int[n][];
        for (int i = 0; i < n; i++) {
            MM[i] = p[i].coeffs;
            CC[i] = p[i].powers;
        }
        M = MM;
        col = CC;
        size = n;
        colNumb = n;
    }

    /**
     * Конструктор матрицы из двумерного плотного массива типа Element.
     *
     * @param A входная матрица в ввиде плотного двумерного массива
     */
    public MatrixS(Element[][] A, Ring r) {
        size = A.length;
        colNumb = 0;
        for (int i = 0; i < size; i++) {
            colNumb = Math.max(colNumb, A[i].length);
        }
        M = new Element[size][];
        Element[] MM = new Element[colNumb];
        col = new int[size][];
        int[] CC = new int[colNumb];
        for (int i = 0; i < size; i++) {
            Element[] AA = A[i];
            int pos = 0;
            for (int j = 0; j < colNumb; j++) {
                if (!AA[j].isZero(r)) {
                    MM[pos] = AA[j];
                    CC[pos++] = j;
                }
            }
            Element[] Mi = new Element[pos];
            System.arraycopy(MM, 0, Mi, 0, pos);
            int[] Ci = new int[pos];
            System.arraycopy(CC, 0, Ci, 0, pos);
            M[i] = Mi;
            col[i] = Ci;
        }
    }

    /**
     * Конструктор матрицы из двумерного плотного массива типа long.
     *
     * @param A входная матрица в ввиде плотного двумерного массива
     */
    public MatrixS(long[][] A, Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        int n = A.length, m = A[0].length;
        size = n;
        colNumb = m;
        M = new Element[n][];
        Element[] MM = new Element[m];
        col = new int[n][];
        int[] CC = new int[m];
        for (int i = 0; i < n; i++) {
            long[] AA = A[i];
            int pos = 0;
            for (int j = 0; j < A[i].length; j++) {
                if (AA[j] != 0) {
                    MM[pos] = one.valOf(AA[j], ring);
                    CC[pos++] = j;
                }
            }
            Element[] Mi = new Element[pos];
            System.arraycopy(MM, 0, Mi, 0, pos);
            int[] Ci = new int[pos];
            System.arraycopy(CC, 0, Ci, 0, pos);
            M[i] = Mi;
            col[i] = Ci;
        }
    }


    /**
     * Конструктор, создающий матрицу MatrixS из двумерного плотного массива.
     *
     * @param A типа int[][], входная матрица
     */
    public MatrixS(int[][] A, Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        int n = A.length;
        int m = A[0].length;
        size = n;
        colNumb = m;
        long[][] B = new long[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                B[i][j] = (long) A[i][j];
            }
        }
        MatrixS MM = new MatrixS(B, ring);
        M = MM.M;
        col = MM.col;
    }

    /**
     * Установить элемент матрицы в определенное ненулевое значение
     * Для установки нулевого элемента пользуйтесь функцией putZeroElement(int i, int j)
     * @param a значение элемента
     * @param i номер строки
     * @param j номер столбца
     */
    public void putElement(Element a, int i, int j) {
        int m = col.length;
        if (i >= m) {
            int[] z = new int[0];
            int[][] col1 = new int[i + 1][0];
            Element[][] M1 = new Element[i + 1][0];
            System.arraycopy(M, 0, M1, 0, m);
            M = M1;
            System.arraycopy(col, 0, col1, 0, m);
            col = col1;
        }
        int[] temp = col[i];
        int length = temp.length;
        for (int k = 0; k < length; k++) {
            if (temp[k] == j) {
                M[i][k] = a;
                return;
            }
        }
        int[] CC = new int[length + 1];
        CC[length] = j;
        Element[] MM = new Element[length + 1];
        MM[length] = a;
        System.arraycopy(temp, 0, CC, 0, length);
        col[i] = CC;
        System.arraycopy(M[i], 0, MM, 0, length);
        M[i] = MM;
        if (++j > colNumb) {
            colNumb = j;
        }
    }

    /**
     * Установить элемент матрицы в значение 0
     * @param i номер строки
     * @param j номер столбца
     */
    public void putZeroElement(int i, int j) {
        int m = col[i].length; if (m==0) return;
        int[] temp = col[i]; 
        int k = 0;
        a:{for (; k < m; k++) {if (temp[k] == j)  break a;}}
        if (k==m) return;
        int[] newcol= new int[m-1];
        Element[] newM= new Element[m-1];
            System.arraycopy(M[i], 0, newM, 0, k);
            System.arraycopy(M[i], k+1, newM, k, m-k-1);
            System.arraycopy(temp, 0, newcol, 0, k);
            System.arraycopy(temp, k+1, newcol, k, m-k-1);
            M[i]=newM; col[i]=newcol;
    }

    /**
     * Возвращает указанный элемент матрицы.
     *
     * @param i номер строки
     * @param j номер столбца
     * @return элемент матрицы в позиции (i,j)
     */
    public Element getElement(int i, int j, Ring ring) {
        Element one = ring.numberONE;
        if ((i > size) || (j > colNumb)) {
            return null;
        }
        if (i > col.length) {
            return one.myZero(ring);
        }
        int[] temp = col[i];
        int length = temp.length;
        for (int k = 0; k < length; k++) {
            if (temp[k] == j) {
                return M[i][k];
            }
        }
        return (one == null) ? null : one.myZero(ring);
    }

    public Element getMaxElement(Ring ring) {
        Element max = getElement(0, 0, ring).abs(ring);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < colNumb; col++) {
                Element curr = getElement(row, col, ring).abs(ring);
                Element result = max.subtract(curr, ring);

                if (result.isNegative()) {
                    max = curr;
                }
            }
        }
        return max;
    }

    /**
     * Возвращает указанную строку матрицы квадратной.
     *
     * @param i номер строки
     * @return элемент матрицы в позиции (i,j)
     */
    public Element[] getRow(int i, Ring ring) {
        int len = colNumb;
        Element[] e = new Element[len];
        for (int j = 0; j < len; j++) {
            e[j] = getElement(i, j, ring);
        }
        return e;
    }

    public Element[] getCol(int j, Ring ring) {
        int len = M.length;
        Element[] e = new Element[len];
        for (int i = 0; i < len; i++) {
            e[i] = getElement(i, j, ring);
        }
        return e;
    }

    public Element[] getColWithoutLastEl(int j, Ring ring) {
        int len = M.length - 1;
        Element[] e = new Element[len];
        for (int i = 0; i < len; i++) {
            e[i] = getElement(i, j, ring);
        }
        return e;
    }

    /**
     * Возвращает из строки rowNumb все элементы с
     * номерами столбцов от startCol до endCol включительно.
     * Найденные элементы возвращаются в матрице, которая содержит одну строку
     *
     * @param rowNumb  номер строки
     * @param startCol номер столбца
     * @param endCol   номер последнего столбца
     * @return матрица, содержащая одну строку с найденными элементами
     */
    public MatrixS getTrackOfRow(int rowNumb, int startCol, int endCol) {
        if ((rowNumb > size) || (endCol > colNumb) || (startCol > endCol)) {
            return null;
        }
        int n = col.length;
        if (rowNumb >= n) {
            return zeroMatrix(1);
        }
        int[] colR = col[rowNumb];
        Element[] MR = M[rowNumb];
        int m = colR.length;
        Element[] MM = new Element[m];
        int[] cc = new int[m];
        Element[][] Mnew = new Element[1][];
        int[][] colnew = new int[1][];
        int s = 0;
        for (int i = 0; i < m; i++) {
            int k = colR[i];
            if ((k >= startCol) && (k <= endCol)) {
                cc[s] = k - startCol;
                MM[s++] = MR[i];
            }
        }
        if (s < m) {
            Element[] MM1 = new Element[s];
            int[] cc1 = new int[s];
            System.arraycopy(MM, 0, MM1, 0, s);
            System.arraycopy(cc, 0, cc1, 0, s);
            Mnew[0] = MM1;
            colnew[0] = cc1;
        } else {
            Mnew[0] = MM;
            colnew[0] = cc;
        }
        return new MatrixS(1, endCol + 1 - startCol, Mnew, colnew);
    }

    /**
     * Возвращает подматрицу, построенную на строках от startRow до endRow и
     * столбцах от startCol до endCol включительно.
     *
     * @param int      startRow номер первой строки подматрицы
     * @param int      endRow номер последней строки подматрицы
     * @param startCol номер первого столбца подматрицы
     * @param endCol   номер последнего столбца подматрицы
     * @return подматрица, содержащая указанный блок
     */
    public MatrixS getSubMatrix(int startRow, int endRow, int startCol, int endCol) {
        if ((endRow > size) || (endCol > colNumb) || (startCol > endCol) || (startRow > endRow)) {
            return null;
        }
        int r = endRow - startRow + 1;
        Element[][] Mnew = new Element[r][];
        int[][] colnew = new int[r][];
        int s = 0;
        for (int i = startRow; i <= endRow; i++) {
            MatrixS Row = getTrackOfRow(i, startCol, endCol);
            Mnew[s] = Row.M[0];
            colnew[s++] = Row.col[0];
        }
        int nr = s - 1;
        while ((nr >= 0) && (colnew[nr].length == 0)) {
            nr--;
        }
        nr++;
        if (nr < s - 1) {
            Element[][] M1 = new Element[nr][];
            int[][] c1 = new int[nr][];
            System.arraycopy(Mnew, 0, M1, 0, nr);
            System.arraycopy(colnew, 0, c1, 0, nr);
            return new MatrixS(r, endCol + 1 - startCol, M1, c1);
        }
        return new MatrixS(r, endCol + 1 - startCol, Mnew, colnew);
    }

    public static MatrixS scalarMatrix(int n, int m, Element a, Ring ring) {
        if (a.isZero(ring)) {
            return zeroMatrix(n);
        }
        int col_numb = Math.min(n, m);
        Element[][] r = new Element[col_numb][1];
        int[][] c = new int[Math.min(n, m)][1];
        for (int i = 0; i < col_numb; i++) {
            r[i][0] = a;
            c[i][0] = i;
        }
        return new MatrixS(n, col_numb, r, c);
    }


    /**
     * Построение скалярной матрицы данного порядка c элементом a на диагонали.
     *
     * @param n порядок матрицы.
     * @param a число на диагонали
     */
    public static MatrixS scalarMatrix(int n, Element a, Ring ring) {
        if (a.isZero(ring)) {
            return zeroMatrix(n);
        }
        Element[][] r = new Element[n][1];
        int[][] c = new int[n][1];
        for (int i = 0; i < n; i++) {
            r[i][0] = a;
            c[i][0] = i;
        }
        return new MatrixS(n, n, r, c);
    }


    public static MatrixS antiDiagonalMatrix(int n, Element a, Ring ring) {
        if (a.isZero(ring)) {
            return zeroMatrix(n);
        }
        Element[][] r = new Element[n][1];
        int[][] c = new int[n][1];
        for (int i = 0; i < n; i++) {
            r[i][0] = a;
            c[i][0] = (n - 1) - i;
        }
        return new MatrixS(n, n, r, c);
    }

 
    /** Транспонирование матрицы, у которой последний столбец ненулевой.
     * Число столбцов находится методом columnsNumber.
     */
// public MatrixS transpose() {return (transpose(columnsNumber()));
// }

    /**
     * Транспонирование матрицы.
     */
  /* public MatrixS transpose() {
        int n = col.length;
        int m = Math.max(n, colNumb);
        return transpose(m);
    }*/
        /**
     * Транспонирование матрицы.
     *
     * @param m число столбцов в this матрице
     */    
  /*  public MatrixS transpose(int m) {
        int n = col.length;
        int[] Ctmp;
        int[] rowLength = new int[m]; 
              // for computation of the lengthes of new rows
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            for (int j = 0; j < Ctmp.length; j++) {
                rowLength[Ctmp[j]] += 1;
            }
        }
        {int j=m-1; while((j>=0)&&(rowLength[j]==0)) {m--;j--;}}
        Element[][] A = new Element[m][0]; // new M
        int[][] C = new int[m][0]; // new col
        int[] poin = new int[m]; // pointers on the positions in new M & col
        for (int i = 0; i < m; i++) {
            int k = rowLength[i];
            C[i] = new int[k];
            A[i] = new Element[k];
        }
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            Element[] Mi = M[i]; // по строкам исходной матрицы
            for (int j = 0; j < Ctmp.length; j++) {
                int t = Ctmp[j];
                A[t][poin[t]] = Mi[j];
                C[t][poin[t]++] = i;
            }
        }
       int cn=col.length;//  new colNumb
       {int j=cn-1; while((j>=0)&&(col[j].length==0)) {cn--;j--;}}
       
       return new MatrixS(Math.max(m, size),cn, A, C);
    }


    public MatrixS transpose_s( int m) { // m=size
        int n = col.length;
        int[] Ctmp;
        int[] rowLength = new int[m]; // for computation of the lengthes of new rows
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            for (int j = 0; j < Ctmp.length; j++) {
                rowLength[Ctmp[j]] += 1;
            }
        }
        Element[][] A = new Element[m][]; // new M
        int[][] C = new int[m][]; // new col
        int[] poin = new int[m]; // pointers on the positions in new M & col
        for (int i = 0; i < m; i++) {
            int k = rowLength[i];
            C[i] = new int[k];
            A[i] = new Element[k];
        }
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            Element[] Mi = M[i]; // по строкам исходной матрицы
            for (int j = 0; j < Ctmp.length; j++) {
                int t = Ctmp[j];
                A[t][poin[t]] = Mi[j];
                C[t][poin[t]++] = i;
            }
        }
        return new MatrixS(colNumb, size, A, C);
    }

*/
    /**  Транспонирование матрицы.
     *   число строк в выходной матрице = this.colnumb
     * @return  this transposed
     */
    //public MatrixS transpose() {return transpose(colNumb);}
    /**
     *  Транспонирование матрицы.
     * @param m  число строк в выходной матрице
     *  в дихотомических алгоритмах берется m=size
     * @return  this transposed
     */
    /*public MatrixS transpose(int m) {
        // int m = colNumb;
        int n = col.length;
        int[] Ctmp;
        int[] rowLength = new int[m]; // for computation of the lengthes of new rows
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            for (int j = 0; j < Ctmp.length; j++) {
                rowLength[Ctmp[j]] += 1;
            }
        }
        Element[][] A = new Element[m][]; // new M
        int[][] C = new int[m][]; // new col
        int[] poin = new int[m]; // pointers on the positions in new M & col
        for (int i = 0; i < m; i++) {
            int k = rowLength[i];
            C[i] = new int[k];
            A[i] = new Element[k];
        }
        int newColNumb=0;
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            Element[] Mi = M[i]; // по строкам исходной матрицы
            for (int j = 0; j < Ctmp.length; j++) {newColNumb=i;
                int t = Ctmp[j];
                A[t][poin[t]] = Mi[j];
                C[t][poin[t]++] = i;
            }
        }
        return new MatrixS(m, newColNumb+1, A, C);
    }

*/





    /**  Транспонирование матрицы.
     *   число строк в выходной матрице = this.colnumb
     * @return  this transposed
     */
    public MatrixS transpose() {return transpose(colNumb);}
    /**
     *  Транспонирование матрицы.
     * @param m  число строк в выходной матрице
     *  в дихотомических алгоритмах надо ставить m=size
     * @return  this transposed
     */
    public MatrixS transpose(int m) {
        // int m = colNumb;
        int n = col.length;
        int[] Ctmp;
        int[] rowLength = new int[m]; // for computation of the lengthes of new rows
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            for (int j = 0; j < Ctmp.length; j++) {
                rowLength[Ctmp[j]] += 1;
            }
        }
        Element[][] A = new Element[m][]; // new M
        int[][] C = new int[m][]; // new col
        int[] poin = new int[m]; // pointers on the positions in new M & col
        for (int i = 0; i < m; i++) {
            int k = rowLength[i];
            C[i] = new int[k];
            A[i] = new Element[k];
        }
        int newColNumb=0;
        for (int i = 0; i < n; i++) {
            Ctmp = col[i];
            Element[] Mi = M[i]; // по строкам исходной матрицы
            for (int j = 0; j < Ctmp.length; j++) {newColNumb=i;
                int t = Ctmp[j];
                A[t][poin[t]] = Mi[j];
                C[t][poin[t]++] = i;
            }
        }
        return new MatrixS(m, newColNumb+1, A, C);
    }
    /**
     * Сумма матриц в общем случае.
     *
     * @param b матрица слагаемое
     */
    public MatrixS add(MatrixS b, Ring ring) {
        int m = Math.max(colNumb, b.colNumb);
        int n = Math.min(col.length, b.col.length);
        int n_length = Math.max(col.length, b.col.length);
        Element[][] sumM = new Element[n_length][]; // new M
        int[][] sumC = new int[n_length][]; // new col
        if (ring.flags.length < m) {
            ring.flags = new int[m];
            for (int i = 0; i < m; i++) {
                ring.flags[i] = -1;
            }
        }
        int[] Cb, Ca;
        Element[] Ma, Mb;
        for (int i = 0; i < n; i++) {
            Ma = M[i];
            Mb = b.M[i];
            Ca = col[i];
            Cb = b.col[i];
            int na = Ma.length, nb = Mb.length;
            if ((na == 0) && (nb == 0)) {
                sumC[i] = new int[0];
                sumM[i] = new Element[0];
            } else if (na == 0) {
                int[] CC = new int[nb];
                Element[] MM = new Element[nb];
                System.arraycopy(Cb, 0, CC, 0, nb);
                System.arraycopy(Mb, 0, MM, 0, nb);
                sumC[i] = CC;
                sumM[i] = MM;
            } else if (nb == 0) {
                int[] CC = new int[na];
                Element[] MM = new Element[na];
                System.arraycopy(Ca, 0, CC, 0, na);
                System.arraycopy(Ma, 0, MM, 0, na);
                sumC[i] = CC;
                sumM[i] = MM;
            } else {                //   //  // наконец, общий случай:
                int[] CC = new int[m];
                Element[] MM = new Element[m];
                System.arraycopy(Ca, 0, CC, 0, na);
                System.arraycopy(Ma, 0, MM, 0, na);
                for (int j = 0; j < na; j++) {
                    ring.flags[CC[j]] = j;
                }
                for (int j = 0; j < nb; j++) {
                    int Cbj = Cb[j];
                    int jj = ring.flags[Cbj];
                    if (jj == -1) {
                        ring.flags[Cbj] = na;
                        CC[na] = Cbj;
                        MM[na++] = Mb[j];
                    } else {
                        MM[jj] = MM[jj].add(Mb[j], ring);
                    }
                }
                int jj = 0, j = 0;
                while ((j < na) && (!MM[j].isZero(ring))) {
                    ring.flags[CC[j++]] = -1;
                }
                // затираем флаги до первого 0 в рез.
                if (j < na) {
                    ring.flags[CC[j]] = -1;
                    jj = j;
                    j++;
                    // Попался 0. Поджимаем все нули и трем флаги
                    for (; j < na; j++) {
                        if (!MM[j].isZero(ring)) {
                            MM[jj] = MM[j];
                            CC[jj++] = CC[j];
                        }
                        ring.flags[CC[j]] = -1;
                    }
                } else {
                    jj = na;
                }
                if (jj == m) {
                    sumC[i] = CC;
                    sumM[i] = MM;
                } else {
                    int[] CC1 = new int[jj];
                    Element[] MM1 = new Element[jj]; //так как jj ненулей
                    System.arraycopy(CC, 0, CC1, 0, jj);
                    System.arraycopy(MM, 0, MM1, 0, jj);
                    sumC[i] = CC1;
                    sumM[i] = MM1;
                }
            }
        }
        if (col.length > n) {
            for (int k = n; k < n_length; k++) {
                sumM[k] = M[k];
                sumC[k] = col[k];
            }
        } else if (b.col.length > n) {
            for (int k = n; k < n_length; k++) {
                sumM[k] = b.M[k];
                sumC[k] = b.col[k];
            }
        }
        return new MatrixS(size, m, sumM, sumC);
    }

    /**
     * Разность матриц в общем случае.
     *
     * @param b матрица вычитаемое
     */
    public MatrixS subtract(MatrixS b, Ring ring) {
        int m = Math.max(colNumb, b.colNumb);
        int n = Math.min(col.length, b.col.length);
        int n_length = Math.max(col.length, b.col.length);
        Element[][] sumM = new Element[n_length][]; // new M
        int[][] sumC = new int[n_length][]; // new col
        if (ring.flags.length < m) {
            ring.flags = new int[m];
            for (int i = 0; i < m; i++) {
                ring.flags[i] = -1;
            }
        }
        int[] Cb, Ca;
        Element[] Ma, Mb;
        for (int i = 0; i < n; i++) {
            Ma = M[i];
            Mb = b.M[i];
            Ca = col[i];
            Cb = b.col[i];
            int na = Ma.length, nb = Mb.length;
            if ((na == 0) && (nb == 0)) {
                sumC[i] = new int[0];
                sumM[i] = new Element[0];
            } else if (na == 0) {
                Element[] MM = new Element[nb];
                for (int j = 0; j < nb; j++) {
                    MM[j] = (Mb[j].negate(ring));
                }
                sumC[i] = Cb;
                sumM[i] = MM;
            } else if (nb == 0) {
                sumC[i] = Ca;
                sumM[i] = Ma;
            } else {  // наконец, общий случай
                int[] CC = new int[m];
                Element[] MM = new Element[m];
                System.arraycopy(Ca, 0, CC, 0, na);
                System.arraycopy(Ma, 0, MM, 0, na);
                for (int j = 0; j < na; j++) {
                    ring.flags[CC[j]] = j;
                }
                for (int j = 0; j < nb; j++) {
                    int Cbj = Cb[j];
                    int jj = ring.flags[Cbj];
                    if (jj == -1) {
                        ring.flags[Cbj] = na;
                        CC[na] = Cbj;
                        MM[na++] = Mb[j].negate(ring);
                    } else {
                        MM[jj] = MM[jj].subtract(Mb[j], ring);
                    }
                }
                int jj = 0, j = 0;
                while ((j < na) && (!MM[j].isZero(ring))) {
                    ring.flags[CC[j++]] = -1;
                }
                // затираем флаги до первого 0 в рез.
                if (j < na) {
                    ring.flags[CC[j]] = -1;
                    jj = j;
                    j++;
                    // Попался 0. Поджимаем все нули и трем флаги
                    for (; j < na; j++) {
                        if (!MM[j].isZero(ring)) {
                            MM[jj] = MM[j];
                            CC[jj++] = CC[j];
                        }
                        ring.flags[CC[j]] = -1;
                    }
                } else {
                    jj = na;
                }
                if (jj == m) {
                    sumC[i] = CC;
                    sumM[i] = MM;
                } else {
                    int[] CC1 = new int[jj];
                    Element[] MM1 = new Element[jj]; // так как jj ненулей
                    System.arraycopy(CC, 0, CC1, 0, jj);
                    System.arraycopy(MM, 0, MM1, 0, jj);
                    sumC[i] = CC1;
                    sumM[i] = MM1;
                }
            }
        }
        if (col.length > n) {
            for (int k = n; k < n_length; k++) {
                sumM[k] = M[k];
                sumC[k] = col[k];
            }
        } else {
            if (b.col.length > n) {
                for (int k = n; k < n_length; k++) {
                    int k_length = b.col[k].length;
                    Element[] MM1 = new Element[k_length];
                    for (int t = 0; t < k_length; t++) {
                        MM1[t] = b.M[k][t].negate(ring);
                    }
                    sumM[k] = MM1;
                    sumC[k] = b.col[k];
                }
            }
        }
        return new MatrixS(size, m, sumM, sumC);

    }

    /**
     * Возвращает матрицу минус один того же размера, что и входная
     *
     * @return диагональная матрица с элементами минус один на диагонали
     */
    public MatrixS myMinusOne(Ring ring) {
        return scalarMatrix(size, ring.numberMINUS_ONE, ring);
    }

    /**
     * Возвращает  единичную матрицу того же размера, что и входная
     *
     * @return единичная матрица
     */
    @Override
    public MatrixS myOne(Ring ring) {
        return scalarMatrix(size, ring.numberONE, ring);
    }

    /**
     * Обращение знака матрицы. Создается новая матрица.
     *
     * @return матрица, полученная обращением знака у каждого элемента
     */
    @Override
    public MatrixS negate(Ring ring) {
        int n = M.length;
        Element[][] r = new Element[n][];
        for (int i = 0; i < n; i++) {
            int m = col[i].length;
            Element[] rr = new Element[m];
            Element[] MM = M[i];
            r[i] = rr;
            for (int j = 0; j < m; j++) {
                rr[j] = MM[j].negate(ring);
            }
        }
        return new MatrixS(size, colNumb, r, col);
    }

    /**
     * Произведение матрицы <code>IplusB</code> на матрицу <code>this</code>(A).<p>
     * <p>
     * Матрица <code>IplusB</code> получается из единичной матрицы заменой
     * столбца с индексом <code>indexB</code> на вектор <code>B</code><p>
     * Матрица <code>this</code> квадратная. Полученное произведение матриц - это квадратная
     * матрицы, с тем же числом строк <code>numberOfVar</code>, что и исходня матрица <code>this</code>(A).
     * Если число компонент вектора мало, то недостающие компоненты принимаются равными нулю,
     * но если число компонент вектора <code>B</code> превышает число строк <code>numberOfVar</code> матрицы <code>this</code>
     * то возвращается результат null.
     *
     * @param B      столбец
     * @param indexB -индекс столбца B
     * @return (IplusB * this)
     */
    public MatrixS IplusBmultA(VectorS B, int indexB, Ring ring) {
        int n = col.length - 1; // Number of non zero rows in this matrix
        int n1 = col.length;
        if ((indexB > n) || (col[indexB].length == 0)) {
            return this;
        }
        Element[] V = B.V;
        int lastBEl = V.length - 1;
        if (lastBEl > size - 1) {
            return null; // Let us find last B element which non Zero.
        }
        while ((lastBEl >= 0) && (V[lastBEl].isZero(ring))) {
            lastBEl--;
        }
        // Spacial case when B=0
        if (lastBEl == -1) {
            if (indexB == n) {
                n1--;
            }
            Element[][] sumM = new Element[n1][]; // new M
            int[][] sumC = new int[n1][]; // new col
            System.arraycopy(col, 0, sumC, 0, n1);
            System.arraycopy(M, 0, sumM, 0, n1);
            if (indexB < n) {
                sumM[indexB] = new Element[0];
                sumC[indexB] = new int[0];
            }
            return new MatrixS(size, colNumb, sumM, sumC);
        }
        // Common case B!=0 и M[indexB]- ненулевая строка
        int N = Math.max(lastBEl + 1, n1); // наибольшее из числа ненулевых
        //  строк матрицы this и ненулевых элементов V
        int N1 = N - 1;
        Element[][] sumM = new Element[N][0]; // new M
        int[][] sumC = new int[N][0]; // new col
        int m = colNumb;
        // Прибавим пространство флагов, если оно мало
        if (ring.flags.length < m) {
            ring.flags = new int[m];
            for (int i = 0; i < m; i++) {
                ring.flags[i] = -1;
            }
        }
        int[] Ca;
        int[] Cb = col[indexB];
        int nb = Cb.length;
        Element[] MindexB = M[indexB];
        Element[] Mb = new Element[nb], Ma;
        // n2 - наибольший номер для которого строка
        // матрицы this ненулевая и елемент вектора B ненулевой.
        int n2 = Math.min(lastBEl + 1, col.length);
        for (int i = 0; i < n2; i++) {
            Element Vi = V[i];
            if (Vi.isZero(ring)) {
                if (i == indexB) {
                    sumM[i] = new Element[0];
                    sumC[i] = new int[0];
                } else {
                    sumM[i] = M[i];
                    sumC[i] = col[i];
                }
            } // Елемент вектора нулевой
            else {
                Ma = M[i];
                Ca = col[i];
                int na = Ma.length;
                for (int j = 0; j < nb; j++) {
                    Mb[j] = MindexB[j].multiply(Vi, ring);
                }
                if ((na == 0) || (i == indexB)) {
                    sumC[i] = Cb;
                    sumM[i] = Mb;
                    Mb = new Element[nb];
                } // Строка this нулевая
                else {  // наконец, общий случай

                    int[] CC = new int[m];
                    Element[] MM = new Element[m];
                    System.arraycopy(Ca, 0, CC, 0, na);
                    System.arraycopy(Ma, 0, MM, 0, na);
                    for (int j = 0; j < na; j++) {
                        ring.flags[CC[j]] = j;
                    }
                    for (int j = 0; j < nb; j++) {
                        int Cbj = Cb[j];
                        int jj = ring.flags[Cbj];
                        if (jj == -1) {
                            ring.flags[Cbj] = na;
                            CC[na] = Cbj;
                            MM[na++] = Mb[j];
                        } else {
                            MM[jj] = MM[jj].add(Mb[j], ring);
                        }
                    }
                    int jj = 0, j = 0;
                    while ((j < na) && (!MM[j].isZero(ring))) {
                        ring.flags[CC[j++]] = -1;
                    }
                    // затираем флаги до первого 0 в рез.
                    if (j < na) {
                        ring.flags[CC[j]] = -1;
                        jj = j;
                        j++;
                        // Попался 0. Поджимаем все нули и трем флаги
                        for (; j < na; j++) {
                            if (!MM[j].isZero(ring)) {
                                MM[jj] = MM[j];
                                CC[jj++] = CC[j];
                            }
                            ring.flags[CC[j]] = -1;
                        }
                    } else {
                        jj = na;
                    }
                    if (jj == m) {
                        sumC[i] = CC;
                        sumM[i] = MM;
                    } else {
                        int[] CC1 = new int[jj];
                        Element[] MM1 = new Element[jj]; // так как jj "ненулей"
                        System.arraycopy(CC, 0, CC1, 0, jj);
                        System.arraycopy(MM, 0, MM1, 0, jj);
                        sumC[i] = CC1;
                        sumM[i] = MM1;
                    }
                }
            }
        }
        // Если в this matrix остались ненулевые строки
        if (lastBEl < col.length - 1) {
            for (int i = n2; i < col.length - 1; i++) {
                sumM[i] = M[i];
                sumC[i] = col[i];
            }
            if (indexB >= n2) {
                sumM[indexB] = new Element[0];
                sumC[indexB] = new int[0];
            }
        } // Если в векторе остались ненулевые элементы
        for (int i = n2; i < lastBEl; i++) {
            Element Vi = V[i];
            if (Vi.isZero(ring)) {
                sumM[i] = new Element[0];
                sumC[i] = new int[0];
            } else {
                for (int j = 0; j < nb; j++) {
                    Mb[j] = MindexB[j].multiply(Vi, ring);
                }
                sumC[i] = Cb;
                sumM[i] = Mb;
                Mb = new Element[nb];
            }
        }
        while ((N1 >= 0) && (sumC[N1].length == 0)) {
            N1--; // Подчистим пустые строчки
        }
        if (N1 + 1 == sumC.length) {
            return new MatrixS(size, colNumb, sumM, sumC);
        }
        Element[][] sumM1 = new Element[N][]; // new M1
        int[][] sumC1 = new int[N][]; // new col1
        System.arraycopy(sumM, 0, sumM1, 0, N);
        System.arraycopy(sumC, 0, sumC1, 0, N);
        return new MatrixS(size, colNumb, sumM1, sumC1);
    }

    /**
     * Копирование матрицы (создание дубликата)
     *
     * @return - дубликат матрицы
     */
    public MatrixS copy() {
        int n = M.length;
        int[][] c = new int[n][];
        for (int i = 0; i < n; i++) {
            int m = col[i].length;
            int[] CC = col[i];
            int[] cc = new int[m];
            System.arraycopy(CC, 0, cc, 0, m);
            c[i] = cc;
        }
        Element[][] r = new Element[n][];
        for (int i = 0; i < n; i++) {
            int m = M[i].length;
            Element[] MM = M[i];
            Element[] rr = new Element[m];
            System.arraycopy(MM, 0, rr, 0, m);
            r[i] = rr;
        }
        return new MatrixS(size, colNumb, r, c);
    }

    /** Изменения знака матрицы на противоположный.
     *  Входная матрица не сохраняется.
     *    УДАЛИТЬ!!!!!
     public void negateThis() {
     for (int i = 0; i < M.length; i++) {
     for (int j = 0; j < M[i].length; j++) {
     M[i][j] = M[i][j].negate();
     }
     }
     }
     */
    /**
     * Процедура вычисления скалярного произведения векторов A1*A2+B1*B2.
     * Рекурсивный параллельный вариант
     *
     * @param A1 1-ая компонента вектора А
     * @param A2 2-ая компонента вектора А
     * @param B1 1-ая компонента вектора B
     * @param B2 2-ая компонента вектора B
     * @return (A1 * B1 + A2 * B2)
     */
    public MatrixS scalarMultiply(
        MatrixS A1, MatrixS A2, MatrixS B1,
        MatrixS B2, Ring ring
    ) {
        return A1.multiplyRecursive(B1, ring).add(A2.multiplyRecursive(B2, ring), ring);
    }

    public MatrixS scalarMultiplyThreaded(
            MatrixS A1, MatrixS A2, MatrixS B1,
            MatrixS B2, Ring ring
    ) {
        return A1.multiplyRecursiveThreaded(B1, ring).add(A2.multiplyRecursiveThreaded(B2, ring), ring);
    }

    /**
     * Процедура вычисления скалярного произведения векторов A1*A2+B1*B2
     * c точным делением на число d.
     * Рекурсивный параллельный вариант
     *
     * @param A1 1-ая компонента вектора А
     * @param A2 2-ая компонента вектора А
     * @param B1 1-ая компонента вектора B
     * @param B2 2-ая компонента вектора B
     * @param d  делитель суммы
     * @return (A1 * B1 + A2 * B2)/d
     */
    private MatrixS scalarMultiplyDiv(
        MatrixS A1, MatrixS A2,
        MatrixS B1, MatrixS B2, Element d, Ring r
    ) {
        return (A1.multiplyRecursive(B1, r).add(A2.multiplyRecursive(B2, r), r)).divideByNumber(
            d,
            r
        );
    }

    private MatrixS scalarMultiplyDivThreaded(
            MatrixS A1, MatrixS A2,
            MatrixS B1, MatrixS B2, Element d, Ring r
    ) {
        return (A1.multiplyRecursiveThreaded(B1, r).add(A2.multiplyRecursiveThreaded(B2, r), r)).divideByNumber(
                d,
                r
        );
    }


    /**
     * Процедура вычисления скалярного произведения векторов (A1,A2) и (B1,B2):
     * A1*A2+B1*B2
     * c делением на число d нацело и умножением на число g.
     * Рекурсивный параллельный вариант
     *
     * @param A1 1-ая компонента вектора А
     * @param A2 2-ая компонента вектора А
     * @param B1 1-ая компонента вектора B
     * @param B2 2-ая компонента вектора B
     * @param d  (число) делитель суммы
     * @param g  (число) множитель частного
     * @return (( A1 * B1 + A2 * B2)/d)*g
     */
    private MatrixS scalarMultiplyDivMul(
        MatrixS A1, MatrixS A2, MatrixS B1,
        MatrixS B2, Element d, Element g, Ring r
    ) {
        return (A1.multiplyRecursive(B1, r).add(A2.multiplyRecursive(B2, r), r)).divideMultiply(
            d,
            g,
            r
        );
    }

    private MatrixS scalarMultiplyDivMulThreaded(
            MatrixS A1, MatrixS A2, MatrixS B1,
            MatrixS B2, Element d, Element g, Ring r
    ) {
        return (A1.multiplyRecursiveThreaded(B1, r).add(A2.multiplyRecursiveThreaded(B2, r), r)).divideMultiply(
                d,
                g,
                r
        );
    }

    private static ExecutorService service;

    private static void initService() {
        if(service == null) {
            synchronized (MatrixS.class) {
                if(service == null) {
                    service = Executors.newFixedThreadPool(4);
                }
            }
        }
    }

    /**
     * Умножения   матриц.
     * Рекурсивный параллельный вариант
     */
    public MatrixS multiplyRecursive(MatrixS b, Ring ring) {
        if (b.M.length <= ring.SMALLESTBLOCK) {
            return multiplySmallBlock(b, ring);
        }
        long a = System.currentTimeMillis();
        MatrixS[] A = split();
        //System.out.println("split a = " + (System.currentTimeMillis()-a) +" size = " +size);
        a = System.currentTimeMillis();
        MatrixS[] B = b.split();
        //System.out.println("split b = " + (System.currentTimeMillis()-a+" size = " +size));
        MatrixS[] R = new MatrixS[4];
       // a = System.currentTimeMillis();
        R[0] = scalarMultiply(A[0], A[1], B[0], B[2], ring);
        R[1] = scalarMultiply(A[0], A[1], B[1], B[3], ring);
        R[2] = scalarMultiply(A[2], A[3], B[0], B[2], ring);
        R[3] = scalarMultiply(A[2], A[3], B[1], B[3], ring);
      // System.out.println("scalarMultiply 4 times = " + (System.currentTimeMillis()-a));
        return join(R);
    }

    public MatrixS multiplyRecursiveThreaded(MatrixS b, Ring ring) {
        if (b.M.length <= ring.SMALLESTBLOCK) {
            return multiplySmallBlock(b, ring);
        }
        initService();
        MatrixS[] A = split();
        MatrixS[] B = b.split();
        MatrixS[] R = new MatrixS[4];
        Future<?> calcR0 = service.submit(() -> {
            R[0] = scalarMultiplyThreaded(A[0], A[1], B[0], B[2], ring);
        });
        Future<?> calcR1 = service.submit(() -> {
            R[1] = scalarMultiplyThreaded(A[0], A[1], B[1], B[3], ring);
        });
        Future<?> calcR2 = service.submit(() -> {
            R[2] = scalarMultiplyThreaded(A[2], A[3], B[0], B[2], ring);
        });

        R[3] = scalarMultiplyThreaded(A[2], A[3], B[1], B[3], ring);

        while(! (calcR0.isDone() && calcR1.isDone() && calcR2.isDone())) {}

        return join(R);
    }

    /**
     * Умножение матриц с последующим делением на число d нацело.
     * Рекурсивный параллельный вариант
     *
     * @param b матрица сомножитель
     * @param d делитель
     * @return (this * b)/d
     */
    public MatrixS multiplyDivRecursive(MatrixS b, Element d, Ring r) {
        if (b.M.length <= r.SMALLESTBLOCK) {
            return (multiply(b, r).divideByNumber(d, r));
        }
        MatrixS[] A = split();
        MatrixS[] B = b.split();
        MatrixS[] R = new MatrixS[4];
        R[0] = scalarMultiplyDiv(A[0], A[1], B[0], B[2], d, r);
        R[1] = scalarMultiplyDiv(A[0], A[1], B[1], B[3], d, r);
        R[2] = scalarMultiplyDiv(A[2], A[3], B[0], B[2], d, r);
        R[3] = scalarMultiplyDiv(A[2], A[3], B[1], B[3], d, r);
        return join(R);
    }

    public MatrixS multiplyDivRecursiveThreaded(MatrixS b, Element d, Ring r) {
        if (b.M.length <= r.SMALLESTBLOCK) {
            return (multiplySmallBlock(b, r).divideByNumber(d, r));
        }
        initService();
        MatrixS[] A = split();
        MatrixS[] B = b.split();
        MatrixS[] R = new MatrixS[4];
        Future<?> calcR0 = service.submit(() -> {
            R[0] = scalarMultiplyDivThreaded(A[0], A[1], B[0], B[2], d, r);
        });
        Future<?> calcR1 = service.submit(() -> {
            R[1] = scalarMultiplyDivThreaded(A[0], A[1], B[1], B[3], d, r);
        });
        Future<?> calcR2 = service.submit(() -> {
            R[2] = scalarMultiplyDivThreaded(A[2], A[3], B[0], B[2], d, r);
        });
        R[3] = scalarMultiplyDivThreaded(A[2], A[3], B[1], B[3], d, r);

        while(! (calcR0.isDone() && calcR1.isDone() && calcR2.isDone())) {}
        return join(R);
    }

    /**
     * Умножение матриц с делением на число d нацело и умножением на число g.
     * Рекурсивный параллельный вариант
     *
     * @param b матрица сомножитель
     * @param d делитель
     * @param g множитель
     * @return (( this * b)/d)*g
     */
    public MatrixS multiplyDivMulRecursive(MatrixS b, Element d, Element g, Ring r) {
        if (b.M.length <= r.SMALLESTBLOCK) {
            return multiply(b, r).divideMultiply(d, g, r);
        }
        MatrixS[] A = split();
        MatrixS[] B = b.split();
        MatrixS[] R = new MatrixS[4];
        R[0] = scalarMultiplyDivMul(A[0], A[1], B[0], B[2], d, g, r);
        R[1] = scalarMultiplyDivMul(A[0], A[1], B[1], B[3], d, g, r);
        R[2] = scalarMultiplyDivMul(A[2], A[3], B[0], B[2], d, g, r);
        R[3] = scalarMultiplyDivMul(A[2], A[3], B[1], B[3], d, g, r);
        return join(R);
    }

    public MatrixS multiplyDivMulRecursiveThreaded(MatrixS b, Element d, Element g, Ring r) {
        if (b.M.length <= r.SMALLESTBLOCK) {
            return multiplySmallBlock(b, r).divideMultiply(d, g, r);
        }
        initService();
        MatrixS[] A = split();
        MatrixS[] B = b.split();
        MatrixS[] R = new MatrixS[4];
        Future<?> calcR0 = service.submit(() -> {
            R[0] = scalarMultiplyDivMulThreaded(A[0], A[1], B[0], B[2], d, g, r);
        });
        Future<?> calcR1 = service.submit(() -> {
            R[1] = scalarMultiplyDivMulThreaded(A[0], A[1], B[1], B[3], d, g, r);
        });
        Future<?> calcR2 = service.submit(() -> {
            R[2] = scalarMultiplyDivMulThreaded(A[2], A[3], B[0], B[2], d, g, r);
        });
        R[3] = scalarMultiplyDivMulThreaded(A[2], A[3], B[1], B[3], d, g, r);

        while(! (calcR0.isDone() && calcR1.isDone() && calcR2.isDone())) {}
        return join(R);
    }

    /**
     * Число столбцов матрицы. (Номер последнего ненулевого столбца + 1)
     * Не параллельный вариант.
     *
     * @return число столбцов матрицы
     */
    public int columnsNumber() {
        int n = col.length;
        int m = 0;
        for (int i = 0; i < n; i++) {
            int[] tmp = col[i];
            for (int j = 0; j < col[i].length; j++) {
                m = Math.max(m, tmp[j]);
            }
        }
        return (++m);
    }

    /** Умножение квадратных матриц.
     *  Не параллельный вариант для листового блока.
     *  Желательно использовать другое умножение с известным числом столбцов
     */
//  public MatrixS multiplySquareMat(MatrixS b) {
//    return multiply(b);
//  }
    /** Умножение прямоугольных матриц при неизвестном числе столбцов сомножителя.
     *  Число столбцов вычисляется процедурой columnsNumber;
     *  Не параллельный вариант для листового блока.
     *  Желательно использовать другое умножение с известным числом столбцов.
     */
//  public MatrixS multiply(MatrixS b) {
//    int m=b.columnsNumber(); // число столбцов у матрицы b
//    return multiply(b,m);
//  }

    /**
     * Умножение  матриц на плотный вектор b.
     *
     * @param b       - vector
     * @param rowNumb number of rows in result
     * @return this*b
     */
    public Element[] multiply(Element[] b, int rowNumb, Ring ring) {
        Element[] res = new Element[rowNumb];
        Element zero = b[0].zero(ring);
        int last = Math.min(rowNumb, M.length);
        int i = 0;
        for (; i < last; i++) {
            Element w = zero;
            if (this.M[i] != null) {
                int k = this.M[i].length;
                //    if(k<b.length){ring.exception.append("Error in MatrixS multiplication by Array."); return null;}

                for (int j = 0; j < k; j++) {
                    w = w.add(M[i][j].multiply(b[col[i][j]], ring), ring);
                }
            }
            res[i] = w;
        }
        while (i < rowNumb) {
            res[i++] = zero;
        }
        return res;
    }

    public MatrixS multiply(MatrixD b, Ring ring) {
        return multiply(new MatrixS(b), ring);
    }

    @Override
    public Element subtract(Element b, Ring ring) {
        return subtract((MatrixS) b, ring);
    }

    @Override
    public Element add(Element b, Ring ring) {
        return add((MatrixS) b, ring);
    }

    @Override
    public Element multiply(Element b, Ring r) {
        if (b instanceof MatrixS) {
            return multiply((MatrixS) b, r);
        }
        if (b instanceof VectorS) {
            return multiplyByColumn((VectorS) b, r);
        }
        if (b instanceof MatrixD) {
            return multiply((MatrixD) b, r);
        }
        return multiplyByNumber(b, r);
    }

    /**
     * Умножение прямоугольных матриц.
     * Не параллельный вариант для листового блока.
     *
     * @param b матрица-сомножитель
     * @param m число столбцов  матрицы b
     * @return this * b
     */
    public MatrixS multiplySmallBlock(
        MatrixS b,
        Ring ring
    ) { //p(col.length+" this="+this.toString()); p(b.col.length+" b="+b.toString());
        int n = col.length;
        int N = b.col.length;
        if ((N == 0) || (n == 0)) { return zeroMatrix(size); }
        int m = b.colNumb;
        Element[][] sumM = new Element[n][]; // new M
        int[][] sumC = new int[n][]; // new col
        int[] flags  = new int[m];
            for (int i = 0; i < m; i++) {flags[i] = -1; }
        int[] Cb, Ca;
        Element[] Ma, Mb;
        Element aa;
        int k = 0;
        int nb = 0;
        for (int i = 0; i < n; i++) { // sumC[i] = CC1; sumM[i] = MM1;
            Ma = M[i];
            Ca = col[i];
            int na = (Ma == null) ? 0 : Ma.length;
            int j = 0;
            while ((j < na) && ((k = Ca[j]) >= N)) {
                j++;
            }
            if (na == j) {
                sumC[i] = new int[0];
                sumM[i] = new Element[0];
            } // i row of product is empty
            else {
                aa = Ma[j];
                Mb = b.M[k];
                Cb = b.col[k];
                nb = Mb.length;
                if (na - 1 == j) {
                    sumC[i] = Cb;   //  only one element at the i-row of this matrix
                    if (aa.isOne(ring)) {
                        sumM[i] = Mb;
                    } else {
                        Element[] MM = new Element[nb];
                        sumM[i] = MM;
                        for (int p = 0; p < nb; p++) {
                            MM[p] = aa.multiply(Mb[p], ring);
                        }
                    }
                } else {
                    int[] CCn = new int[m];  // more then one element at the i-row of this matrix
                    Element[] MMn = new Element[m];
                    System.arraycopy(Cb, 0, CCn, 0, nb);
                    for (int p = 0; p < nb; p++) {
                        flags[Cb[p]] = p;
                        MMn[p] = aa.multiply(Mb[p], ring);
                    } //first row
                    for (j++; j < na; j++) {
                        if ((k = Ca[j]) < N) {
                            aa = Ma[j];
                            k = Ca[j];
                            Mb = b.M[k];
                            Cb = b.col[k];
                            int nb1 = Mb.length;
                            for (int s = 0; s < nb1; s++) {
                                int Cbs = Cb[s];
                                int ss = flags[Cbs];
                                if (ss == -1) {
                                    flags[Cbs] = nb;
                                    CCn[nb] = Cbs;
                                    MMn[nb++] = aa.multiply(Mb[s], ring);
                                } else {
                                    MMn[ss] = MMn[ss].add(aa.multiply(Mb[s], ring), ring);
                                }
                            }
                        }
                    }
                    int jj = 0, t = 0;
                    while ((t < nb) && (!MMn[t].isZero(ring))) {
                        flags[CCn[t++]] = -1;
                    }
                    // затираем флаги до первого 0 в рез.
                    if (t < nb) {
                        flags[CCn[t]] = -1;
                        jj = t++;
                        // Попался 0. Поджимаем все нули и трем флаги
                        for (; t < nb; t++) {
                            if (!MMn[t].isZero(ring)) {
                                MMn[jj] = MMn[t];
                                CCn[jj++] = CCn[t];
                            }
                            flags[CCn[t]] = -1;
                        }
                    } else {
                        jj = nb;
                    }
                    if (jj == m) {
                        sumC[i] = CCn;
                        sumM[i] = MMn;
                    } else {
                        int[] CC1 = new int[jj];
                        Element[] MM1 = new Element[jj]; //так как jj ненулей
                        System.arraycopy(CCn, 0, CC1, 0, jj);
                        System.arraycopy(MMn, 0, MM1, 0, jj);
                        sumC[i] = CC1;
                        sumM[i] = MM1;
                    }
                }
            }
        }
        return new MatrixS(size, m, sumM, sumC);
    }


    /**
     * Умножение прямоугольных матриц.
     * Не параллельный вариант для листового блока.
     *
     * @param b матрица-сомножитель
     * @param m число столбцов  матрицы b
     * @return this * b
     */
    public MatrixS multiply(
            MatrixS b,
            Ring ring
    ) { //p(col.length+" this="+this.toString()); p(b.col.length+" b="+b.toString());
        int n = col.length;
        int N = b.col.length;
        if ((N == 0) || (n == 0)) { return zeroMatrix(size); }
        int m = b.colNumb;
        Element[][] sumM = new Element[n][]; // new M
        int[][] sumC = new int[n][]; // new col
        if (ring.flags.length < m) {
            ring.flags = new int[m];
            for (int i = 0; i < m; i++) {
                ring.flags[i] = -1;
            }
        } //flags of elems
        int[] Cb, Ca;
        Element[] Ma, Mb;
        Element aa;
        int k = 0;
        int nb = 0;
        for (int i = 0; i < n; i++) { // sumC[i] = CC1; sumM[i] = MM1;
            Ma = M[i];
            Ca = col[i];
            int na = (Ma == null) ? 0 : Ma.length;
            int j = 0;
            while ((j < na) && ((k = Ca[j]) >= N)) {
                j++;
            }
            if (na == j) {
                sumC[i] = new int[0];
                sumM[i] = new Element[0];
            } // i row of product is empty
            else {
                aa = Ma[j];
                Mb = b.M[k];
                Cb = b.col[k];
                nb = Mb.length;
                if (na - 1 == j) {
                    sumC[i] = Cb;   //  only one element at the i-row of this matrix
                    if (aa.isOne(ring)) {
                        sumM[i] = Mb;
                    } else {
                        Element[] MM = new Element[nb];
                        sumM[i] = MM;
                        for (int p = 0; p < nb; p++) {
                            MM[p] = aa.multiply(Mb[p], ring);
                        }
                    }
                } else {
                    int[] CCn = new int[m];  // more then one element at the i-row of this matrix
                    Element[] MMn = new Element[m];
                    System.arraycopy(Cb, 0, CCn, 0, nb);
                    for (int p = 0; p < nb; p++) {
                        ring.flags[Cb[p]] = p;
                        MMn[p] = aa.multiply(Mb[p], ring);
                    } //first row
                    for (j++; j < na; j++) {
                        if ((k = Ca[j]) < N) {
                            aa = Ma[j];
                            k = Ca[j];
                            Mb = b.M[k];
                            Cb = b.col[k];
                            int nb1 = Mb.length;
                            for (int s = 0; s < nb1; s++) {
                                int Cbs = Cb[s];
                                int ss = ring.flags[Cbs];
                                if (ss == -1) {
                                    ring.flags[Cbs] = nb;
                                    CCn[nb] = Cbs;
                                    MMn[nb++] = aa.multiply(Mb[s], ring);
                                } else {
                                    MMn[ss] = MMn[ss].add(aa.multiply(Mb[s], ring), ring);
                                }
                            }
                        }
                    }
                    int jj = 0, t = 0;
                    while ((t < nb) && (!MMn[t].isZero(ring))) {
                        ring.flags[CCn[t++]] = -1;
                    }
                    // затираем флаги до первого 0 в рез.
                    if (t < nb) {
                        ring.flags[CCn[t]] = -1;
                        jj = t++;
                        // Попался 0. Поджимаем все нули и трем флаги
                        for (; t < nb; t++) {
                            if (!MMn[t].isZero(ring)) {
                                MMn[jj] = MMn[t];
                                CCn[jj++] = CCn[t];
                            }
                            ring.flags[CCn[t]] = -1;
                        }
                    } else {
                        jj = nb;
                    }
                    if (jj == m) {
                        sumC[i] = CCn;
                        sumM[i] = MMn;
                    } else {
                        int[] CC1 = new int[jj];
                        Element[] MM1 = new Element[jj]; //так как jj ненулей
                        System.arraycopy(CCn, 0, CC1, 0, jj);
                        System.arraycopy(MMn, 0, MM1, 0, jj);
                        sumC[i] = CC1;
                        sumM[i] = MM1;
                    }
                }
            }
        }
        return new MatrixS(size, m, sumM, sumC);
    }



    /**
     *
     * @param b
     * @param ring
     * @return result of multiplication with guaranteed least column at 0 pos in col array
     */

    public MatrixS multiplyLeastColumnFirst(
            MatrixS b,
            Ring ring
    ) { //p(col.length+" this="+this.toString()); p(b.col.length+" b="+b.toString());
        int n = col.length;
        int N = b.col.length;
        if ((N == 0) || (n == 0)) { return zeroMatrix(size); }
        int m = b.colNumb;
        Element[][] sumM = new Element[n][]; // new M
        int[][] sumC = new int[n][]; // new col
        if (ring.flags.length < m) {
            ring.flags = new int[m];
            for (int i = 0; i < m; i++) {
                ring.flags[i] = -1;
            }
        } //flags of elems
        int[] Cb, Ca;
        Element[] Ma, Mb;
        Element aa;
        int k = 0;
        int nb = 0;
        int minColIndex = 0;
        int minCol;
        int tmpColValue;
        for (int i = 0; i < n; i++) { // sumC[i] = CC1; sumM[i] = MM1;
            Ma = M[i];
            Ca = col[i];
            minCol = b.colNumb;
            int na = (Ma == null) ? 0 : Ma.length;
            int j = 0;
            while ((j < na) && ((k = Ca[j]) >= N)) {
                j++;
            }
            if (na == j) {
                sumC[i] = new int[0];
                sumM[i] = new Element[0];
            } // i row of product is empty
            else {
                aa = Ma[j];
                Mb = b.M[k];
                Cb = b.col[k];
                nb = Mb.length;
                if (na - 1 == j) {
                    sumC[i] = Cb;   //  only one element at the i-row of this matrix
                    if (aa.isOne(ring)) {
                        sumM[i] = Mb;
                    } else {
                        Element[] MM = new Element[nb];
                        sumM[i] = MM;
                        for (int p = 0; p < nb; p++) {
                            MM[p] = aa.multiply(Mb[p], ring);
                        }
                    }
                } else {
                    int[] CCn = new int[m];  // more then one element at the i-row of this matrix
                    Element[] MMn = new Element[m];
                    System.arraycopy(Cb, 0, CCn, 0, nb);
                    for (int p = 0; p < nb; p++) {
                        ring.flags[Cb[p]] = p;
                        MMn[p] = aa.multiply(Mb[p], ring);
                    } //first row
                    for (j++; j < na; j++) {
                        if ((k = Ca[j]) < N) {
                            aa = Ma[j];
                            k = Ca[j];
                            Mb = b.M[k];
                            Cb = b.col[k];
                            int nb1 = Mb.length;
                            for (int s = 0; s < nb1; s++) {
                                int Cbs = Cb[s];
                                int ss = ring.flags[Cbs];
                                if (ss == -1) {
                                    ring.flags[Cbs] = nb;
                                    CCn[nb] = Cbs;
                                    MMn[nb++] = aa.multiply(Mb[s], ring);
                                } else {
                                    MMn[ss] = MMn[ss].add(aa.multiply(Mb[s], ring), ring);
                                }
                            }
                        }
                    }
                    int jj = 0, t = 0;
                    while ((t < nb) && (!MMn[t].isZero(ring))) {
                        ring.flags[CCn[t++]] = -1;
                    }
                    // затираем флаги до первого 0 в рез.
                    if (t < nb) {
                        ring.flags[CCn[t]] = -1;
                        jj = t++;
                        // Попался 0. Поджимаем все нули и трем флаги
                        for (; t < nb; t++) {
                            if (!MMn[t].isZero(ring)) {
                                MMn[jj] = MMn[t];
                                tmpColValue = CCn[t];
                                CCn[jj] = tmpColValue;
                                if(minCol > tmpColValue){
                                    minCol = tmpColValue;
                                    minColIndex = jj;
                                }
                                jj++;
                            }
                            ring.flags[CCn[t]] = -1;
                        }
                    } else {
                        jj = nb;
                    }
                    if (jj == m) {
                        sumC[i] = CCn;
                        sumM[i] = MMn;
                    } else {
                        int[] CC1 = new int[jj];
                        Element[] MM1 = new Element[jj]; //так как jj ненулей
                        System.arraycopy(CCn, 0, CC1, 0, jj);
                        System.arraycopy(MMn, 0, MM1, 0, jj);
                        sumC[i] = CC1;
                        sumM[i] = MM1;
                    }
                }
            }

//            sortRowColumns(sumM, sumC, i);
//            makeLeastColumnFirst(sumM, sumC, i);
            swapFirstColToLeastColumn(sumM, sumC, i, minColIndex);

        }
        return new MatrixS(size, m, sumM, sumC);
    }

    public void sortUpColumns(){
        for (int row = 0; row < size; row++) {
            makeLeastColumnFirst(M, col, row);
        }
    }

    private static void makeLeastColumnFirst(Element[][] elements, int[][] cols, int row){
        int length = cols[row].length;
        if (length == 1 || length == 0) return;

        int leastColIndex = Array.minElementIndex(cols[row]);

        swapFirstColToLeastColumn(elements, cols, row, leastColIndex);
    }

    private static void swapFirstColToLeastColumn(Element[][] elements, int[][] cols, int row, int leastColIndex){
        int[] rowCols = cols[row];
        Element[] rowElements = elements[row];
        if(leastColIndex == 0 ||  rowCols.length <= leastColIndex) return;
        Element el = rowElements[0];
        rowElements[0] = rowElements[leastColIndex];
        rowElements[leastColIndex] = el;

        int col = rowCols[0];
        rowCols[0] = rowCols[leastColIndex];
        rowCols[leastColIndex] = col;
    }

    private static void sortRowColumns(Element[][] elements, int[][] cols, int row){
        if(!isNotSorted(cols[row])) return;

        int length = cols[row].length;
        int[] sortedPos = Array.sortPosUp(cols[row]);
        if(length == 0) return;

        Element[] elem = new Element[length];
        int[] col = new int[length];


        for (int colIndex = 0; colIndex < cols[row].length; colIndex++) {
            elem[colIndex] = elements[row][sortedPos[colIndex]];
            col[colIndex] = cols[row][sortedPos[colIndex]];
        }

        elements[row] = elem;
        cols[row] = col;
    }

    private static boolean isNotSorted(int[] cols){
        for (int i = 0; i < cols.length - 1; i++)
            if( cols[i] > cols[i + 1])
                return true;

        return false;
    }

    /**
     * Процедура умножения  матрицы на число.
     * Последовательная процедура.
     *
     * @param s число сомножитель
     * @return this * s
     */
    public MatrixS multiplyByNumber(Element s, Ring ring) {
        int n = M.length;
        if (s.isZero(ring)) {
            return zeroMatrix(n);
        }
        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            int m = Mi.length;
            Element[] ri = new Element[m];
            r[i] = ri;
            for (int j = 0; j < m; j++) {
                ri[j] = Mi[j].multiply(s, ring);
            }
        }
        return new MatrixS(size, colNumb, r, col);
    }

    /**
     * Процедура умножения  матрицы на вектор.
     * При этом число компонент у результата равно this.numberOfVar.
     * Все компоненты c номерами от this.M.length до this.numberOfVar-1 равны нулю.
     * Последовательная процедура.
     *
     * @param v -- векор, который умножается справа на матрицу this
     * @return this * v
     */
    public VectorS multiplyByColumn(VectorS v, Ring ring) {

        int n = M.length; // number of rows
        Element[] R = new Element[size];
        Element[] V = v.V;
        int m = V.length;
        Element zero = V[0].zero(ring);
        for (int i = 0; i < n; i++) {
            Element[] MM = M[i];
            int[] cc = col[i];
            Element temp = zero;
            for (int j = 0; j < MM.length; j++) {
                temp = temp.add(MM[j].multiply(V[cc[j]], ring), ring);
            }
            R[i] = temp;
        }
        for (int i = m; i < size; i++) {
            R[i] = zero;
        }

        return new VectorS(R);
    }

    public VectorS multiplyByFractionColumn(VectorS v, Ring ring) {

        int n = M.length; // number of rows
        Element[] R = new Element[size];
        Fraction[] V = (Fraction[]) v.V;
        int m = V.length;
        Element zero = V[0].zero(ring);
        for (int i = 0; i < n; i++) {
            Element[] MM = M[i];
            int[] cc = col[i];
            Element temp = zero;
            for (int j = 0; j < MM.length; j++) {
                temp = temp.add(V[cc[j]].multiply(MM[j], ring), ring);
            }
            R[i] = temp;
        }
        for (int i = m; i < size; i++) {
            R[i] = zero;
        }

        return new VectorS(R);
    }

    /**
     * Процедура умножения  матрицы на вектор.
     * При этом число компонент у результата равно числу строк матрицы this.M.
     * Последовательная процедура.
     *
     * @param v -- векор, который умножается справа на матрицу
     * @return this * v
     */
    public VectorS multiplyByColumnShort(VectorS s, Ring ring) {
        int n = M.length;
        Element[] R = new Element[n];
        Element[] V = s.V;
        int m = V.length;
        Element zero = V[0].zero(ring);
        for (int i = 0; i < n; i++) {
            Element[] MM = M[i];
            int[] cc = col[i];
            Element temp = zero;
            for (int j = 0; j < MM.length; j++) {
                temp = temp.add(MM[i].multiply(V[cc[i]], ring), ring);
            }
            R[i] = temp;
        }
        return new VectorS(R);
    }

    /**
     * Процедура точного деления матрицы на число с умножением на другое число
     *
     * @param d типа Element, делитель
     * @param s типа Element, множитель
     * @return (this / d) * s
     */
    public MatrixS divideMultiply(Element d, Element s, Ring ring) {
        int n = M.length;
        if (s.isZero(ring)) { return zeroMatrix(n); }
        if (s.isOne(ring)) { return divideByNumber(d, ring); }
        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            int m = Mi.length;
            Element[] ri = new Element[m];
            r[i] = ri;
            for (int j = 0; j < m; j++) {
                ri[j] = (Mi[j].divide(d, ring)).multiply(s, ring);
            }
        }
        return new MatrixS(size, colNumb, r, col);
    }

    /**
     * Процедура умножения матрицы на число s с точным делением на число d.
     *
     * @param s множитель
     * @param d делитель
     * @return (this * s)/d
     */
    public MatrixS multiplyDivide(Element s, Element d, Ring ring) {
        int n = M.length;
        if (s.isZero(ring)) { return zeroMatrix(n); }
        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            int m = Mi.length;
            Element[] ri = new Element[m];
            r[i] = ri;
            for (int j = 0; j < m; j++) {
                ri[j] = (Mi[j].multiply(s, ring)).divide(d, ring);
                //  System.out.println("tttttttttttttt="+ri[j]);
            }
        }
        return new MatrixS(size, colNumb, r, col);
    }

    /**
     * Процедура точного деления матрицы на число
     *
     * @param s типа Element, делитель
     * @return this * s
     */
    public MatrixS divideByNumber(Element s, Ring ring) {
        if (s.isOne(ring)) { return this; }
        if (s.isMinusOne(ring)) { return negate(ring); }
        int n = M.length;
        int m = 0;
        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            m = Mi.length;
            Element[] ri = new Element[m];
            r[i] = ri;
            for (int j = 0; j < m; j++) {
//               System.out.println("sssss = "+Mi[j]+"    " + s.toString(ring));
                ri[j] = Mi[j].divideExact(s, ring);
            }
        }
        return new MatrixS(size, colNumb, r, col);
    }

    /**
     * Процедура точного деления матрицы на число
     *
     * @param s типа Element, делитель
     * @return this * s
     */
    public MatrixS divideByNumbertoFraction(Element s, Ring ring) {
        int n = M.length;
        int m = 0;
        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            m = Mi.length;
            Element[] ri = new Element[m];
            r[i] = ri;
            for (int j = 0; j < m; j++) {
                ri[j] = Mi[j].divideToFraction(s, ring);
            }
        }
        return new MatrixS(size, colNumb, r, col);
    }


    /**
     * Процедура разбиения матрицы  на 4 равных квадратных блока.
     * [0]=(00), [1]=(01), [2]=(10), [3]=(11);
     *
     * @return массив который содержит четыре блока исходной матрицы
     */
    public MatrixS[] split() {
        MatrixS[] res = new MatrixS[4];
        int len = size / 2;
        int len1 = Math.min(len, col.length); // rows for upper blocks
        int len2 = Math.max(col.length - len, 0); // rows for bound blocks
        int colNumb0 = Math.min(len, colNumb);
        int colNumb1 = Math.max(colNumb - len, 0);
        int[][] c0 = new int[len1][0], c1 = new int[len1][0],
            c3 = new int[len2][0],
            c2 = new int[len2][0];
        Element[][] r0 = new Element[len1][0], r1 = new Element[len1][0],
            r2 = new Element[len2][0],
            r3 = new Element[len2][0];
        for (int i = 0; i < len1; i++) {
            if ((M[i] != null) && (M[i].length != 0)) {
                Element[][] R2 = new Element[2][0];
                int[][] C2 = new int[2][0];
                toHalveRow(M[i], R2, col[i], C2, len);
                c0[i] = C2[0];
                c1[i] = C2[1];
                r0[i] = R2[0];
                r1[i] = R2[1];
            }
        }
        for (int i = 0; i < len2; i++) {
            int i_len = len + i;
            if (M[i_len].length != 0) {
                Element[][] R2 = new Element[2][0];
                int[][] C2 = new int[2][0];
                toHalveRow(M[i_len], R2, col[i_len], C2, len);
                c2[i] = C2[0];
                c3[i] = C2[1];
                r2[i] = R2[0];
                r3[i] = R2[1];
            }
        }
        res[0] = new MatrixS(len, colNumb0, r0, c0);
        res[1] = new MatrixS(len, colNumb1, r1, c1);
        res[2] = new MatrixS(len, colNumb0, r2, c2);
        res[3] = new MatrixS(len, colNumb1, r3, c3);
        return res;
    }

    /**
     * Разбиения матрицы на 4 блока
     * Столбцы матрицы упорядочены по возрастанию
     * .
     *
     * @return Массив, который содержит четыре блока исходной матрицы
     */
    public MatrixS[] splitExtended() {
        // Находим высоту верхних и нижних блоков.
        int hightUpper = size >> 1;
        int lenLeftover = size % 2;
        int hightBottom = hightUpper + lenLeftover;

        // Создаём необходимые массивы для новых матриц.
        int[][] colUL = new int[hightUpper][0];
        int[][] colUR = new int[hightUpper][0];
        int[][] colBL = new int[hightBottom][0];
        int[][] colBR = new int[hightBottom][0];
        Element[][] mxUL = new Element[hightUpper][0];
        Element[][] mxUR = new Element[hightUpper][0];
        Element[][] mxBL = new Element[hightBottom][0];
        Element[][] mxBR = new Element[hightBottom][0];

        // Распределяем элементы исходной матрицы между
        // верхними и нижними частями. 
        distributeBetweenParts(colUL, colUR, mxUL, mxUR, 0);
        distributeBetweenParts(colBL, colBR, mxBL, mxBR, hightUpper);
        // Возвращаем массив с четырьмя новыми матрицами.
        return new MatrixS[]{
                new MatrixS(mxUL, colUL), new MatrixS(mxUR, colUR),
                new MatrixS(mxBL, colBL), new MatrixS(mxBR, colBR),
        };
    }

    /**
     * Процедура распределения элементов матрицы по новым блокам.
     *
     * @param colL Столбцы левой матрицы
     * @param colR Столбцы правой матрицы
     * @param mxL Элементы левой матрицы
     * @param mxR Элементы правой матрицы
     * @param from С какой высоты исходной м-цы осуществлять распределение
     */
    private void distributeBetweenParts(
            @NotNull final int[][] colL, @NotNull final int[][] colR,
            @NotNull final Element[][] mxL, @NotNull final Element[][] mxR,
            final int from
    ) {
 
        int target = (size >> 1); // + (colNumb % 2 - 1);
        for (int i = 0, k = from; i < colL.length; ++i, ++k) {
            if((col[k]==null)||(col[k].length==0)){
            colL[i] = new int[0];
            colR[i] = new int[0];
            mxL[i] = new Element[0];
            mxR[i] = new Element[0];
            }else{ 
            int j=Array.findPosOfMedian(col[k],target);
            int widthLeft =   j;
            int widthRight = col[k].length - widthLeft;
            colL[i] = new int[widthLeft];
            colR[i] = new int[widthRight];
            mxL[i] = new Element[widthLeft];
            mxR[i] = new Element[widthRight];
            int[] rightPart = Arrays.stream(col[k]).map(x -> x - (target)).toArray();
            System.arraycopy(col[k], 0, colL[i], 0, widthLeft);
            System.arraycopy(rightPart, widthLeft, colR[i], 0, widthRight);
            System.arraycopy(M[k], 0, mxL[i], 0, widthLeft);
            System.arraycopy(M[k], widthLeft, mxR[i], 0, widthRight);
        }     
        }
                    
    }
 
    
    
    
    void toHalveRow(Element[] r, Element[][] r2, int[] c, int[][] c2, int len) {
        int m = c.length;
        int[] C0 = new int[m];
        int[] C1 = new int[m];
        Element[] R0 = new Element[m];
        Element[] R1 = new Element[m];
        int p0 = 0; int p1 = 0;
        for (int j = 0; j < m; j++) {
            int cj = c[j];
            if (cj < len) { C0[p0] = cj;  R0[p0++] = r[j];
            } else {
                C1[p1] = (cj -= len);
                R1[p1++] = r[j];
            }
        }
        if (p0 == m) {c2[0] = C0;  r2[0] = R0;
        } else if (p0 != 0) {
            int[] CC = new int[p0];
            Element[] RR = new Element[p0];
            System.arraycopy(C0, 0, CC, 0, p0);
            System.arraycopy(R0, 0, RR, 0, p0);
            c2[0] = CC; r2[0] = RR;
        }
        if (p1 == m) { c2[1] = C1;  r2[1] = R1;
        } else if (p1 != 0) {
            int[] CC = new int[p1];
            Element[] RR = new Element[p1];
            System.arraycopy(C1, 0, CC, 0, p1);
            System.arraycopy(R1, 0, RR, 0, p1);
            c2[1] = CC;
            r2[1] = RR;
        }
    }
//-------

    /**
     * Создание матрицы из четырех квадратных блоков одинакового размера,
     * по порядку:  {{0,1},{2,3}}.
     *
     * @param b массив из четырех блоков матрицы,
     * @return матрица, полученная из данных четырех блоков
     */
    public static MatrixS join(MatrixS[] b) {
        int len = b[0].size;
        int size = (len << 1);
        int len2 = Math.max(b[2].M.length, b[3].M.length);
        int len1 = Math.max(b[0].M.length, b[1].M.length);
        int n = (len2 == 0) ? len1 : len + len2;

        int col2 = Math.max(b[1].colNumb, b[3].colNumb);
        int col1 = Math.max(b[0].colNumb, b[2].colNumb);
        int colNumb = (col2 == 0) ? col1 : len + col2;
        Element[][] r = new Element[n][0];
        int[][] c = new int[n][0];

        Element[] R0 = null;
        Element[] R1 = null;
        int[] C0 = null;
        int[] C1 = null;
        for (int i = 0; i < len1; i++) {
            int m = 0;
            int k = 0;
            if (b[0].M.length > i) {
                C0 = b[0].col[i];
                R0 = b[0].M[i];
                m = C0.length;
            }
            if (b[1].M.length > i) {
                C1 = b[1].col[i];
                R1 = b[1].M[i];
                k = C1.length;
            }
            int mk = m + k;
            Element[] r0 = new Element[mk];
            int[] c0 = new int[mk];
            if (m > 0) {
                System.arraycopy(C0, 0, c0, 0, m);
                System.arraycopy(R0, 0, r0, 0, m);
            }
            if (k > 0) {
                System.arraycopy(C1, 0, c0, m, k);
                System.arraycopy(R1, 0, r0, m, k);
                for (int s = m; s < mk; s++) { c0[s] += len; }
            }
            r[i] = r0;
            c[i] = c0;
        }
        int ii = len;
        for (int i = 0; i < len2; i++) {
            int m = 0;
            int k = 0;
            if (b[2].M.length > i) {
                C0 = b[2].col[i];
                R0 = b[2].M[i];
                m = C0.length;
            }
            if (b[3].M.length > i) {
                C1 = b[3].col[i];
                R1 = b[3].M[i];
                k = C1.length;
            }
            int mk = m + k;
            Element[] r0 = new Element[mk];
            int[] c0 = new int[mk];
            if (m > 0) {
                System.arraycopy(C0, 0, c0, 0, m);
                System.arraycopy(R0, 0, r0, 0, m);
            }
            if (k > 0) {
                System.arraycopy(C1, 0, c0, m, k);
                System.arraycopy(R1, 0, r0, m, k);
                for (int s = m; s < mk; s++) { c0[s] += len; }
            }
            r[ii] = r0;
            c[ii++] = c0;
        }
        MatrixS res = new MatrixS(size, colNumb, r, c);
        return res;
    }

    public static MatrixS embedDownRightQuarter(MatrixS block, Ring ring) {
        return embedDiagonal(
            block,
            2 * block.size,
            ring.numberONE(),
            block.size,
            ring.numberONE(),
            ring
        );
    }

    public static MatrixS embedUpLeftQuarter(MatrixS block, Element afterBlock, Ring ring) {
        return embedDiagonal(block, 2 * block.size, ring.numberONE(), 0, afterBlock, ring);
    }

    public static MatrixS embedUpLeftQuarter(MatrixS block, Ring ring) {
        return embedUpLeftQuarter(block, ring.numberONE(), ring);
    }

    public static MatrixS embedDiagonalCenter(MatrixS block, Ring ring) {
        return embedDiagonal(
            block,
            2 * block.size,
            ring.numberONE(),
            block.size / 2,
            ring.numberONE(),
            ring
        );
    }

    public static MatrixS embedDiagonal(
        MatrixS block,
        int newMatrixSize,
        int blockOffset,
        Ring ring
    ) {

        if (newMatrixSize < block.size + blockOffset) {
            throw new IllegalArgumentException("Offset is out range");
        }

        return embedDiagonal(
            block,
            newMatrixSize,
            ring.numberONE(),
            blockOffset,
            ring.numberONE(),
            ring
        );
    }

    /**
     * Embed block on diagonal
     *
     * @param block         matrix block
     * @param newMatrixSize new matrix size
     * @param beforeBlock   diagonal element before block
     * @param blockOffset   diagonal offset before block
     * @param afterBlock    diagonal element after block
     * @return MatrixS
     */

    public static MatrixS embedDiagonal(
        MatrixS block, int newMatrixSize, Element beforeBlock,
        int blockOffset, Element afterBlock, Ring ring
    ) {
        Element[][] elements = new Element[newMatrixSize][];
        int[][] columns = new int[newMatrixSize][];

        for (int row = 0; row < blockOffset; row++) {
            if (beforeBlock.isZero(ring)) {
                elements[row] = new Element[0];
                columns[row] = new int[0];
            } else {
                elements[row] = new Element[1];
                elements[row][0] = beforeBlock;
                columns[row] = new int[1];
                columns[row][0] = row;
            }
        }

        for (int row = blockOffset; row < blockOffset + block.size; row++) {
            int size = block.M[row - blockOffset].length;
            columns[row] = new int[size];
            elements[row] = new Element[size];
            System.arraycopy(block.M[row - blockOffset], 0, elements[row], 0, size);
            for (int col = 0; col < size; col++) {
                columns[row][col] = block.col[row - blockOffset][col] + blockOffset;
            }
        }

        for (int row = blockOffset + block.size; row < newMatrixSize; row++) {
            if (afterBlock.isZero(ring)) {
                elements[row] = new Element[0];
                columns[row] = new int[0];
            } else {
                elements[row] = new Element[1];
                elements[row][0] = beforeBlock;
                columns[row] = new int[1];
                columns[row][0] = row;
            }
        }

        return new MatrixS(newMatrixSize, newMatrixSize, elements, columns);
    }


    public static MatrixS embedDiagonalBlocks(MatrixS A, MatrixS B, Ring ring) {

        MatrixS zero = MatrixS.zeroMatrix(A.size);

        return join(new MatrixS[]{A, zero, zero, B});
    }

    public static MatrixS embedBlocksOfColumn(MatrixS A, MatrixS B, Ring ring) {
        MatrixS zero = MatrixS.zeroMatrix(A.size);

        return join(new MatrixS[]{A, zero, B, zero});
    }

    public static MatrixS rotationMatrix(Element sin, Element cos, Ring ring){
        int sinNonZero = sin.isZero(ring) ? 0 : 1;
        int cosNonZero = cos.isZero(ring) ? 0 : 1;
        int nonZeros = sinNonZero + cosNonZero;

        int[][] cols = new int[2][nonZeros];
        Element[][] elements = new Element[2][nonZeros];

        if (nonZeros == 2){
            cols[0][0] = 0;
            cols[0][1] = 1;
            cols[1][0] = 0;
            cols[1][1] = 1;

            elements[0][0] = cos;
            elements[0][1] = sin.negate(ring);
            elements[1][0] = sin;
            elements[1][1] = cos;
        } else if (sinNonZero == 1) {
            cols[0][0] = 1;
            cols[1][0] = 0;

            elements[0][0] = sin.negate(ring);
            elements[1][0] = sin;
        } else if (cosNonZero == 1) {
            cols[0][0] = 0;
            cols[1][0] = 1;

            elements[0][0] = cos;
            elements[1][0] = cos;
        }

        return new MatrixS(2,2, elements, cols);
    }
//-------
    /** Создание матрицы из четырех квадратных блоков одинакового размера,
     *  по порядку:  {{0,1},{2,3}}.
     * @param b массив из четырех блоков матрицы,
     * @return матрица, полученная из данных четырех блоков
     *
    public static MatrixS join(MatrixS[] b) {
    int len = b[0].numberOfVar;
    int numberOfVar = (len << 1);
    int len2 = Math.max(b[2].M.length, b[3].M.length);
    int len1 = Math.max(b[0].M.length, b[1].M.length);
    int n = (len2 == 0) ? len1 : len + len2;
    Element[][] r = new Element[n][0];
    int[][] c = new int[n][0];

    Element[] R0 = null;
    Element[] R1 = null;
    int[] C0 = null;
    int[] C1 = null;
    for (int i = 0; i < len1; i++) {
    int m = 0;
    int k = 0;
    if (b[0].M.length > i) {
    C0 = b[0].col[i];
    R0 = b[0].M[i];
    m = C0.length;
    }
    if (b[1].M.length > i) {
    C1 = b[1].col[i];
    R1 = b[1].M[i];
    k = C1.length;
    }
    int mk = m + k;
    Element[] r0 = new Element[mk];
    int[] c0 = new int[mk];
    if (m > 0) {
    System.arraycopy(C0, 0, c0, 0, m);
    System.arraycopy(R0, 0, r0, 0, m);
    }
    if (k > 0) {
    System.arraycopy(C1, 0, c0, m, k);
    System.arraycopy(R1, 0, r0, m, k);
    for (int s = m; s < mk; s++) {
    c0[s] += len;
    }
    }
    r[i] = r0;
    c[i] = c0;
    }
    int ii = len;
    for (int i = 0; i < len2; i++) {
    int m = 0;
    int k = 0;
    if (b[2].M.length > i) {
    C0 = b[2].col[i];
    R0 = b[2].M[i];
    m = C0.length;
    }
    if (b[3].M.length > i) {
    C1 = b[3].col[i];
    R1 = b[3].M[i];
    k = C1.length;
    }
    int mk = m + k;
    Element[] r0 = new Element[mk];
    int[] c0 = new int[mk];
    if (m > 0) {
    System.arraycopy(C0, 0, c0, 0, m);
    System.arraycopy(R0, 0, r0, 0, m);
    }
    if (k > 0) {
    System.arraycopy(C1, 0, c0, m, k);
    System.arraycopy(R1, 0, r0, m, k);
    for (int s = m; s < mk; s++) {
    c0[s] += len;
    }
    }
    r[ii] = r0;
    c[ii++] = c0;
    }
    MatrixS res = new MatrixS(numberOfVar, n, r, c);
    return res;
    } */
    /**
     * Дополнение матрицы снизу и справа нулями до матрицы размера степени двойки.
     * Возвращается квадратная матрица порядка 2^s, где s - наименьшее натуральное
     * число такое, что 2^s не меньше, чем число строк и число столбцов матрицы.
     *
     * @return матрица, дополненная нулями.
     */
    public MatrixS expandToPow2with0() {
        //    System.out.println("size="+size+"  "+ colNumb+"  "+M.length);
        int n = Math.max(size, colNumb);
        int hb = Integer.highestOneBit(n);
        if (hb != n) {
            hb <<= 1;
        }
        Element[][] MM = new Element[n][0];
        int[][] cc = new int[n][0];
        for (int i = 0; i < M.length; i++) {
            MM[i] = M[i];
            cc[i] = col[i];
        }
        return new MatrixS(hb, colNumb, MM, cc);
    }

    public MatrixS expandToPow2with0(int sizeClm) {
        int n = Math.max(col.length, sizeClm);
        n = Math.max(n, size);
        int hb = Integer.highestOneBit(n);
        if ((hb==size)&&(hb==n)) return this;
        else{ if(hb!=n) hb <<= 1;
            Element[][] MM=new Element[hb][0];int[][] cc=new int[hb][0];
            System.arraycopy(M, 0, MM, 0, M.length);
            System.arraycopy(col, 0, cc, 0, col.length);
            return new MatrixS(hb, colNumb, MM, cc); }
    }

    /** Go backfrom expand matrices in Cholesky and in LSU
     *
     * @param mats - thr resulting matrices of a size 2^N
     * @param n - rows number
     * @param m  - columns number
     */
    public static void backFromExpand(MatrixS[] mats, int n, int m) {
        int k=mats.length; Element[] Eempty=new Element[0]; int[] intEmpty=new int[0];
        if (k<3){  for (int i = n; i < mats[0].M.length; i++) {mats[0].M[i]=Eempty;mats[0].col[i]=intEmpty;}
            mats[0].size=n;   mats[0].colNumb=n;
        }
        if (k==2){
            for (int i = n; i < mats[0].M.length; i++) {mats[1].M[i]=Eempty; mats[1].col[i]=intEmpty;}
            mats[1].size=n; mats[1].colNumb=n;
        }
        if (k==3){
            for (int i = n; i < mats[0].M.length; i++) {
                mats[0].M[i]=Eempty; mats[1].M[i]=Eempty; mats[2].M[i]=Eempty;
                mats[0].col[i]=intEmpty;  mats[1].col[i]=intEmpty; mats[2].col[i]=intEmpty;
            }
            mats[0].size=n;  mats[1].size=n; mats[2].size=Math.min(m, n);
            mats[0].colNumb=Math.min(n, mats[0].colNumb);
            mats[1].colNumb=Math.min(n, mats[1].colNumb);
            mats[1].colNumb=Math.min(m, mats[1].colNumb);
            //       System.out.println(mats[2]+"mats[2].colNumb)--="+m+mats[2].colNumb);
            mats[2].colNumb=Math.max(m, mats[2].colNumb);
        }
    }

    public static void backFromExpand(MatrixS mats, int n, int m) {
        Element[] Eempty=new Element[0]; int[] intEmpty=new int[0];
        for (int i = n; i < mats.M.length; i++) {mats.M[i]=Eempty;mats.col[i]=intEmpty;}
        mats.size=n;   mats.colNumb=Math.min(m, mats.colNumb);
    }

    /** Maximum absolute value (\abs(a_{i,j})) of the all
     *  matrix elements
     *
     * @param r ring
     * @return max_{this} abs(a_{i,j})
     */
    @Override
    public Element maxAbs(Ring ring) {
        Element res=ring.numberZERO;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                res = M[i][j].abs(ring).max(res, ring);
            }
        }
        return res;
    }


    /** Дополнение матрицы снизу и справа нулями до матрицы размера ближайшей
     * степени двойки. Возвращается квадратная матрица порядка 2^k,
     * где k - наименьшее натуральное число такое, что 2^k
     * не меньше, чем число строк  и  число столбцов исходной матрицы.
     * Число столбцов у исходной матрицы предварительно вычисляется.
     * @return матрица, дополненная нулями.
     */
    //public MatrixS expandToPow2with0(){return expandToPow2with0(columnsNumber());}

    /**
     * Дополнение квадратной матрицы единичным диагональным блоком и двумя нулевыми блоками
     * до ближайшей степени двойки.
     * Возвращается квадратная матрица порядка 2^s, где s - наименьшее натуральное
     * число такое, что 2^s не меньше, чем число строк.
     *
     * @param one -- Element=1
     * @return новая матрица, дополненная нулевыми и единичным блоком
     */
    public MatrixS expandToPow2with1(Element one) {
        int n = M.length;
        int m = Integer.highestOneBit(size);
        if (m != size) {
            m <<= 1;
        }
        if (m == n) {
            return this;
        }
        Element[][] r = new Element[m][];
        int[][] c = new int[m][];
        Element[] unitL = new Element[]{one};
        System.arraycopy(M, 0, r, 0, M.length);
        System.arraycopy(col, 0, c, 0, col.length);
        for (int i = M.length; i < m; i++) {
            r[i] = unitL;
            c[i] = new int[]{i};
        }
        return new MatrixS(m, colNumb, r, c);
    }

    /** Дополнение матрицы единичным диагональным блоком и двумя нулевыми блоками
     *  до матрицы порядка ближайшей степени двойки.
     *  Возвращается квадратная матрица порядка 2^s, где s - наименьшее натуральное
     *  число такое, что 2^s не меньше, чем число строк и число столбцов матрицы.
     *  Число столбцов у исходной матрицы предварительно вычисляется
     *  @param one -- Element=1
     *  @return новая матрица, дополненная нулевыми и единичным блоком
     */
// public MatrixS expandToPow2with1(Element one)
//        {return expandToPow2with1(columnsNumber(),one);}

    /**
     * ПЕРЕСТАНОВКА СТРОК, в результате умножения на E слева.
     * Процедура умножения на матрицу типа Е слева.
     * E - обобщенная матрица перестановки (неполного ранга).
     * Процедура не параллелится!
     *
     * @param Ei номера строк матрицы E
     * @param Ej номера столбцов матрицы E
     * @return E*this    - типа MatrixS
     */
    public MatrixS multiplyLeftE(int[] Ei, int[] Ej) {
        int k = Ei.length;
        if (k == 0) {
            return zeroMatrix(size);
        }
        int N = col.length;
        int n = 0;
        for (int s = 0; s < k; s++) {
            if (Ei[s] > n) {
                n = Ei[s];
            }
        }
        n++;
        Element[][] MM = new Element[n][0];
        int[][] cc = new int[n][0];
        for (int i = 0; i < k; i++) {
            if (Ej[i] < N) {
                MM[Ei[i]] = M[Ej[i]];
                cc[Ei[i]] = col[Ej[i]];
            }
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Умножение слева на матрицу D*E или на обратную к ней E^T* D^(-1)
     *
     * @param Ei        =Ej для транспонированной  =Ei(умножение на E)
     * @param Ej        =Ei для транспонированной  =Ej(умножение на E)
     * @param D         =(a11,a11*a22,..) для транспонированной    =(a11,a11*a22,..)^-1 (умножение на E)
     * @param transpose =true(умножение на транспонированную к E), =fasle(умножение на E)
     * @param ring
     * @return D*E*this (при transpose=false)   или  E^T* D^(-1)*this (при transpose=true)
     */
    public MatrixS multiplyLeftDE(int[] Ei, int[] Ej, Element[] D, boolean transpose, Ring ring) {
        int k = Ei.length;
        if (k == 0) {
            return zeroMatrix(size);
        }
        int N = col.length;
        int n = 0;
        for (int s = 0; s < k; s++) {
            if (Ei[s] > n) {
                n = Ei[s];
            }
        }
        n++;
        Element[][] MM = new Element[n][0];
        int[][] cc = new int[n][0];
        for (int i = 0; i < k; i++) {
            if (Ej[i] < N) {
                int ll = M[Ej[i]].length;
                Element[] row = new Element[ll];
                Element[] MM1 = M[Ej[i]];
                if (transpose) {
                    for (int j = 0; j < ll; j++) {
                        row[j] = MM1[j].multiply(D[j], ring);
                    }
                } else {
                    for (int j = 0; j < ll; j++) {
                        row[j] = MM1[j].multiply(D[i], ring);
                    }
                }
                MM[Ei[i]] = row;
                cc[Ei[i]] = col[Ej[i]];
            }
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Multiplication of dense diagonal matrix D by matrix E.
     * The length of the arrays   Ei, Ej, D must be equal.
     *
     * @param Ei   - array of rows number
     * @param Ej   - array of columns number
     * @param D    - array of all diagonal elements in the rows Ei
     * @param size - size of square matrixS
     * @param ring - Ring
     * @return - matrixS which is equals D*E
     */
    public static MatrixS DE(int[] Ei, int[] Ej, Element[] D, int size, Ring ring) {
        int k = Ei.length;
        if (k == 0) {
            return zeroMatrix(size);
        }
        int N = size;
        int n = 0;
        int m = 0;
        for (int s = 0; s < k; s++) {
            if (Ei[s] > n) {
                n = Ei[s];
            }
            if (Ej[s] > m) {
                m = Ej[s];
            }
        }
        n++;
        m++;
        Element[][] MM = new Element[n][0];
        int[][] cc = new int[n][0];
        for (int i = 0; i < k; i++) {
            MM[Ei[i]] = new Element[]{D[i]};
            cc[Ei[i]] = new int[]{Ej[i]};
        }
        return new MatrixS(size, m, MM, cc);
    }

//  ssh 22  ftp 21 http 80 - apach jsp//

    public MatrixS multiplyLeft_barI(int[] Ei) {
        int[] bar = new int[size - Ei.length];
        int[] row = new int[size];
        for (int i = 0; i < Ei.length; i++) {
            row[Ei[i]] = 1;
        }
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (row[i] == 0) {
                bar[j++] = i;
            }
        }
        return multiplyLeftI(bar);
    }

    /**
     * Процедура умножения на матрицу типа I слева.
     * I - диагональная матрица с 0-ми и 1-ми на диагонали.
     * Процедура не параллелится!
     *
     * @param Ei номера строк матрицы типа I (строки с 1 на диагонали)
     * @return I*this
     */
    public MatrixS multiplyLeftI(int[] Ei) {
        int k = Ei.length;
        int n = col.length;
        while ((--n >= 0) && (col[n].length == 0)) {
        }
        n++;
        if ((k == 0) || (n == 0)) {
            return zeroMatrix(size);
        }
        if (k == size) {
            return this;
        }
        int rowNumb = Math.min(Array.max(Ei) + 1, n);
        Element[][] MM = new Element[rowNumb][0];
        int[][] cc = new int[rowNumb][0];
        for (int i = 0; i < k; i++) {
            int h = Ei[i];
            if (h < n) {
                MM[h] = M[h];
                cc[h] = col[h];
            }
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Процедура умножения на матрицу типа I слева.
     * I - диагональная матрица с 0-ми и 1-ми на диагонали.
     * Процедура не параллелится!
     *
     * @param Ei номера строк матрицы типа I (строки с 1 на диагонали)
     * @return I*this
     */
    private MatrixS multiplyRight_BarI_d(int[] Ej, Element d, Ring ring) {
        int[] E_stack = new int[size];
        for (int i = 0; i < Ej.length; i++) {
            E_stack[Ej[i]] = 1;
        }
        Element[][] MM = new Element[M.length][0];
        int[][] cc = new int[M.length][0];
        for (int i = 0; i < col.length; i++) {
            int zero_n = 0;
            for (int j = 0; j < col[i].length; j++) {
                if (E_stack[col[i][j]] == 1) {
                    zero_n++;
                }
            }
            Element[] Mi = new Element[M[i].length - zero_n];
            int[] ci = new int[M[i].length - zero_n];
            int j1 = 0;
            for (int j = 0; j < col[i].length; j++) {
                if (E_stack[col[i][j]] != 1) {
                    ci[j1] = col[i][j];
                    Mi[j1++] = M[i][j].multiply(d, ring);
                }
            }
            MM[i] = Mi;
            cc[i] = ci;
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Процедура умножения на матрицу типа   bar_I справа.
     * I - диагональная матрица с 0-ми и 1-ми на диагонали.
     * Процедура не параллелится!
     *
     * @param Ej -- matrix I
     * @return this*(bar_I)
     */
    public MatrixS multiplyRight_barI(int[] Ej) {
        int[] E_stack = new int[size];
        for (int i = 0; i < Ej.length; i++) {
            E_stack[Ej[i]] = 1;
        }
        return multiplyR_b_nb_I(E_stack);
    }

    /**
     * Процедура умножения на матрицу типа I   справа.
     * I - диагональная матрица с 0-ми и 1-ми на диагонали.
     * Процедура не параллелится!
     *
     * @param Ej -- matrix I
     * @return this*I
     */
    public MatrixS multiplyRight_I(int[] Ej) {
        int[] E_stack = new int[size];
        for (int i = 0; i < size; i++) {
            E_stack[i] = 1;
        }
        for (int i = 0; i < Ej.length; i++) {
            E_stack[Ej[i]] = 0;
        }
        return multiplyR_b_nb_I(E_stack);
    }

    private MatrixS multiplyR_b_nb_I(int[] E_stack) {
        Element[][] MM = new Element[M.length][0];
        int[][] cc = new int[M.length][0];
        for (int i = 0; i < col.length; i++) {
            int zero_n = 0;
            for (int j = 0; j < col[i].length; j++) {
                if (E_stack[col[i][j]] == 1) {
                    zero_n++;
                }
            }
            Element[] Mi = new Element[M[i].length - zero_n];
            int[] ci = new int[M[i].length - zero_n];
            int j1 = 0;
            int[] col_i = col[i];
            for (int j = 0; j < col_i.length; j++) {
                if (E_stack[col_i[j]] != 1) {
                    ci[j1] = col_i[j];
                    Mi[j1++] = M[i][j]; //.multiply(d, ring);
                }
            }
            MM[i] = Mi;
            cc[i] = ci;
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Перестановка, которая сдвигает все ненулевые строки матрицы вверх.
     *
     * @param Ei -- matrix I для данной матрицы
     * @return {EI,EJ}, which is a E matrix:
     * матрица E*A - имеет все ненулевые строки расположенными вверху матрицы
     */
    public static int[][] moveMatrToUpPos(int[][] E, int n) {
        int[] Ei = E[0];
        int N = Ei.length;
        int[] EJ = new int[n];
        int[] EI = new int[n];
        if (N == 0) { return new int[][]{EI, EJ}; }
        for (int i = 0; i < n; i++) {
            EI[i] = i;
        }
        boolean sorted = true;
        for (int i = 1; i < N; i++) {
            if (Ei[i] < Ei[i - 1]) {
                sorted = false;
                break;
            }
        }

        int[] EJ1;
        if (sorted) { EJ1 = Ei; } else {
            int[][] F = Array.sortMatrixAccordingRowN(E, 0);
            E[0] = F[0];
            E[1] = F[1];
            EJ1 = E[0];
        }
        System.arraycopy(EJ1, 0, EJ, 0, N);
        int prev = -1;
        int j = N; // current position in array EJ
        for (int i = 0; i < N; i++) {
            int p = EJ1[i] - prev - 1;
            if (p > 0) {
                for (int k = 1; k <= p; k++) { EJ[j++] = prev + k; }
            }
            prev = EJ1[i];
        }
        while (j < n) { EJ[j] = j++; }
        return new int[][]{EI, EJ};
    }

    /**
     * Перестановка, которая сдвигает влево все ведушие стодбцы матрицы
     * Эшелонного вида. Полученая матрица является плотной диагональной матрицей.
     *
     * @param Ei -- matrix I для данной матрицы
     * @return {EI,EJ}, which is a E matrix:
     * матрица A*У=I имеет диагональный вид.
     */
    public static int[][] moveEchelonToDiagForm(int[][] E, int n) {
        System.out.println("EEEEEEEEEEEEEEEEEJ=  " + Array.toString(E[0]));
        System.out.println("EEEEEEEEEEEEEEEEEJ=  " + Array.toString(E[1]));
        int[] Ei = E[0];
        int[] Ej = E[1];
        int N = E[0].length;
        int[] EJ = new int[n];
        int[] EI = new int[n];
        for (int i = 0; i < n; i++) {
            EI[i] = i;
            EJ[i] = i;
        }
        if (N == 0) { return new int[][]{EI, EJ}; }
        for (int i = 0; i < N; i++) {
            if (Ei[i] != Ej[i]) {
                int temp = EJ[Ei[i]];
                EJ[Ei[i]] = EJ[Ej[i]];
                EJ[Ej[i]] = temp;
            }
        }
        System.out.println("EEEEEEEEEEEEEEEEEJ=  " + Array.toString(EJ));
        return new int[][]{EI, EJ};
    }

    /**
     * Удаление диагональных элементов в каждой строке
     */
    private MatrixS eraseDiagonal() {
        int SMlength = M.length;
        Element[][] MM = new Element[SMlength][0];
        int[][] ccol = new int[SMlength][0];
        for (int i = 0; i < SMlength; i++) {
            int MiLength = M[i].length;
            if (MiLength > 1) {
                MiLength--;
                Element[] Mi_ = M[i];
                int[] Ci_ = col[i];
                Element[] Mi = new Element[MiLength];
                int[] Ci = new int[MiLength];
                int j = 0;
                while (Ci_[j] < i) {
                    Ci[j] = Ci_[j];
                    Mi[j] = Mi_[j];
                    j++;
                }
                System.arraycopy(Ci_, j + 1, Ci, j, MiLength - j);
                System.arraycopy(Mi_, j + 1, Mi, j, MiLength - j);
                MM[i] = Mi;
                ccol[i] = Ci;
            }
        }
        return new MatrixS(size, colNumb, MM, ccol);
    }

    /**
     * Умножаем слева на транспонированную матрицу Е и удаляем Ej-столбцы,
     * для этого просто удаляем все диагональные элементы
     *
     * @return -- результат умножения на транспонированную Е без диагональных элементов
     */
    public MatrixS ES_eraseDi(int[] Ei, int[] Ej) {
        return multiplyLeftE(Ej, Ei).eraseDiagonal();
        // Аргументы (Ej,Ei) приводят к умножению на транспонированную к Е, а аргументы (Ei,Ej) - к умнож. на Е
        // Затем удаляем диагональные элементы в каждой строке
    }

    /**
     * Вычисляется выражение A*Y- A*bar_I(Ej) *d,
     * где bar_I(Ej) -- диагональная матрица с единичными элементами, дополнительными к Ej,
     * d - скаляр, A -- входная матрица
     *
     * @param Y  -- матрица
     * @param Ej --  матрицы вида Е (диагональная матриц)
     * @param d  =-- число
     * @return -- this* Y- this*bar_I(Ej) *d
     */
    public MatrixS A_ES_min_dI(MatrixS Y, int[] Ej, Element d, Ring r) {
        // Y= E^T * S * Ij == (E^T*S)*(Ej_inv),  то есть  X.eraseDiagonal == X - X.multiplyRightI(int[] Ej)
        return multiplyRight_BarI_d(Ej, d.negate(r), r).add(multiply(Y, r), r);
    }


    /**
     * Вычисление ядра оператора:  Yij = -dij I + Eij^T Sij.
     * Причем в матрице Eij^T * Sij на диагональных позициях стоят числа d=dij,
     * Sij - входная матрица (this).  Процедура не параллелится!!!!!
     *
     * @param d  - диагональный элемент (dij)
     * @param Ei номера строк матрицы E
     * @param Ej номера столбцов матрицы E
     * @return Eij^T * S - d * I типа MatrixS
     */
    public MatrixS ES_min_dI(Element d, int[] Ei, int[] Ej, Ring r) {
        if ((Ei.length == 0) || (isZero(r))) { return scalarMatrix(size, d.negate(r), r); }
        if (size == 1) { return zeroMatrix(size); }
        MatrixS ES = multiplyLeftE(Ej, Ei);
        int n = ES.col.length;
        Element[][] MM = new Element[size][];
        int[][] cc = new int[size][];
        Element[] dd = new Element[]{d.negate(r)};
        for (int i = 0; i < n; i++) {
            Element[] Mi = ES.M[i];
            int[] ci = ES.col[i];
            int cil = ci.length;
            int m1 = cil - 1;
            if (cil == 0) {
                MM[i] = dd;
                cc[i] = new int[]{i};
            } else {
                Element[] temp = new Element[m1];
                MM[i] = temp;
                int[] temp_c = new int[m1];
                cc[i] = temp_c;
                int j = 0;
                while (ci[j] != i) { j++; }
                int m1j = m1 - j;
                System.arraycopy(Mi, 0, temp, 0, j);
                System.arraycopy(Mi, j + 1, temp, j, m1j);
                System.arraycopy(ci, 0, temp_c, 0, j);
                System.arraycopy(ci, j + 1, temp_c, j, m1j);
            }
        }
        for (int i = n; i < size; i++) {
            MM[i] = dd;
            cc[i] = new int[]{i};
        }
        return new MatrixS(size, size, MM, cc);
    }

    /**
     * Добавление к матрице справа  столбца VectorS v.
     *
     * @param v добавляемый столбец.
     * @return матрица, полученная добавлением столбца.
     */
    public MatrixS appendColumn(VectorS v, Ring ring) {
        int n = M.length;
        Element[] V = v.V;
        int N = V.length;
        int m = N - 1;
        if (size < V.length) {
            return null;
        }
        while ((m >= 0) && (V[m].isZero(ring))) {
            m--;
        }
        if (m < 0) {
            return this;
        }
        int nN = Math.min(n, N);
        Element[][] r;
        int[][] c;
        if (m <= n) {
            r = new Element[n][];
            c = new int[n][];
        } else {
            r = new Element[m][];
            c = new int[m][];
        }
        for (int i = 0; i < nN; i++) {
            if (!V[i].isZero(ring)) {
                int l = col[i].length;
                Element[] MM = M[i];
                int[] CC = col[i];
                Element[] rr = new Element[l + 1];
                int[] cc = new int[l + 1];
                System.arraycopy(MM, 0, rr, 0, l);
                rr[l] = V[i];
                System.arraycopy(CC, 0, cc, 0, l);
                cc[l] = colNumb;
                r[i] = rr;
                c[i] = cc;
            } else {
                r[i] = M[i];
                c[i] = col[i];
            }
        }
        for (int i = nN; i < m; i++) {
            if (!V[i].isZero(ring)) {
                r[i] = new Element[]{V[i]};
                c[i] = new int[]{colNumb};
            }
        }
        return new MatrixS(size, colNumb + 1, r, c);
    }

    /**
     * Вычисление присоединенной матрицы, эшелонной матрицы и матрицы
     * ведущих столбцов
     *
     * @param EiEj исходная и финальная матрица ведущих элементов
     * @return присоединенная матрица и эшелонная матрица
     */
    public MatrixS[] adjointEchelon(int[][] EiEj, Element one, Ring ring) {
        MatrixS gg = this;
        AdjMatrixS x = new AdjMatrixS(expandToPow2with0(), one, ring);
        EiEj[0] = x.Ei;
        EiEj[1] = x.Ej;
        return permutAdjAndEchel(x.A, x.S, EiEj, ring);
    }

    public MatrixS[] mldtsvAdjointEchelon(int[][] EiEj, Element one, Ring ring) {
        MatrixS gg = this;
        MldtsvAdjMatrixS x = new MldtsvAdjMatrixS(expandToPow2with0(), one, ring);
        EiEj[0] = x.Ei;
        EiEj[1] = x.Ej;
        return permutAdjAndEchel(x.A, x.S, EiEj, ring);
    }

    public MatrixS[] adjointEchelon2(int[][] EiEj, Element one, Ring ring) {
        MatrixS gg = this;
        AdjMatrixSThreadsMolodtsov x = new AdjMatrixSThreadsMolodtsov(expandToPow2with0(), one, ring);
        EiEj[0] = x.Ei;
        EiEj[1] = x.Ej;
        return permutAdjAndEchel(x.A, x.S, EiEj, ring);
    }

    public int rank(Ring ring) {
        AdjMatrixS x = new AdjMatrixS(expandToPow2with0(), ring.numberONE, ring);
        return x.Ei.length;
    }

    /**
     * Перестановка строк в присоединенной и эшелонной матрицах,
     * так чтобы ведущий элемент первого ведущего столбца был в первой строке,
     * второго - во второй, и так далее.
     *
     * @param B    присоединенная матрица (B[0]) и эшелонная матрица (B[1])
     * @param EiEj E-матрица ведущих элементов
     * @return массив из двух матриц: присоединенной и эшелонной
     * В массиве EiEj[1] возвращаются номера ведущих столбцов
     * ведущими строками являются 1,2,3,....
     */
    private MatrixS[] permutAdjAndEchel(MatrixS Adj, MatrixS Ech, int[][] EiEj, Ring ring) {
        int[][] RC = Array.sortMatrixAccordingRowN(EiEj, 1);
        // сортируем столбцы двустрочной матрицы EiEj по строке 1 (нижней)
        // т.е. Е-матрицу располагаем в порядке возрастания номеров столбцов
        int[] row = RC[0];
        int[] colm = RC[1];
        EiEj[0] = RC[0];
        EiEj[1] = RC[1]; //возвращаем новую матрицу ведущих элементов
        int n = colm.length;              // rank of achelon matrix
        Element[][] MS = Ech.M;
        int[][] cS = Ech.col; // Echelon
        Element[][] SS = new Element[n][0];
        int[][] cc = new int[n][0]; //New Echelon
        Element[][] MA = Adj.M;
        int[][] cA = Adj.col; // Adjoint
        Element[][] AA = new Element[n][0];
        int[][] ccAA = new int[n][0]; // New Adjoin

        for (int i = 0; i < n; i++) {
            int rowI = row[i];

            SS[i] = MS[rowI];
            cc[i] = cS[rowI];
            AA[i] = MA[rowI];
            ccAA[i] = cA[rowI];
        }
        //   isEvenPermutation();
        // переставляем местами строки так, чтобы ведущий элемент первого
        // ведущего столбца был в первой строке, второго - во второй.....

        if (!Array.isEvenPermutation(row)) { //Меняем знаки у всех элементов
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < AA[i].length; j++) {
                    AA[i][j] = AA[i][j].negate(ring);
                }
                for (int j = 0; j < SS[i].length; j++) {
                    SS[i][j] = SS[i][j].negate(ring);
                }
            }
        }
        return new MatrixS[]{new MatrixS(size, size, AA, ccAA),
                             new MatrixS(size, colNumb, SS, cc)};
    }

    /**
     * Присоединенная матрица
     * Вычисление присоединенной матрицы для невырожденного блока
     * максимального размера с наименьшими номерами строк и столбцов
     *
     * @return присоединенная матрица
     */
    public MatrixS adjoint(Ring ring) {
        Element one = ring.numberONE;
        if (one == null) {
            return this;
        }
        int[][] EiEj = new int[2][];
        MatrixS M2 = adjointEchelon(EiEj, one, ring)[0];
        return (new MatrixS(size, M2.colNumb, M2.M, M2.col));
    }

    /**
     * Присоединенная матрица и детерминант.
     * Вычисление присоединенной матрицы для невырожденного блока
     * максимального размера с наименьшими номерами строк и столбцов
     *
     * @param oneDet[0] is a unit "myOne" for the Element - before,
     *                  is a det - after the computation.
     * @return присоединенная матрица
     */
    public MatrixS adjointDet(Element[] oneDet, Ring ring) {
        Element one = oneDet[0];
        int row_numb_before = M.length;
        int[][] EiEj = new int[2][];
        MatrixS[] adjointGen = adjointEchelon(EiEj, one, ring);
        int row_numb_after = EiEj[0].length;
        oneDet[0] = ((row_numb_after == 0) || (row_numb_after < row_numb_before))
            ? one.myZero(ring) : adjointGen[1].M[0][0];
        MatrixS M2 = adjointGen[0];
        return (new MatrixS(size, M2.colNumb, M2.M, M2.col));

    }

    /**
     * Обращение матрицы в поле ring, либо в Q, если ring=Z.
     *
     * @return inverse matrix
     */
    @Override
    public MatrixS inverse(Ring ring) {
        Element one = ring.numberONE;
        if (one instanceof NumberZ) {
            one = new Fraction(one, one);
        }
        return inverse(one, ring);
    }

    /**
     * Обращение матрицы в поле, которое представлено единицей oneInField
     * например, в NumberZp32, NumberZp, NumberR, NumberC64,...
     *
     * @param oneInField --  Element one in the field Element
     * @return inverse matrix
     */
    public MatrixS inverse(Element oneInField, Ring ring) {
        int[][] EiEj = new int[2][];
        MatrixS[] adjointGen = adjointEchelon(EiEj, ring.numberONE, ring);
        int row_numb_after = EiEj[0].length;
        if ((row_numb_after == 0) || (row_numb_after < size)) {
            return null;
        }
        Element det = adjointGen[1].M[0][0];
        boolean FlagNotField = false;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                if ((M[i][j].numbElementType() > ring.algebra[0])) {
                    FlagNotField = true;
                    break;
                }
            }
        }
        if (FlagNotField || (ring.algebra[0] == Ring.Z) || (ring.algebra[0] == Ring.Z64)) {
            return adjointGen[0].divideByNumbertoFraction(det, ring);
        }
        Element invDet = det.inverse(ring);
        return adjointGen[0].multiplyByNumber(invDet, ring);
    }

    /**
     * Обращение матрицы в Fraction --  поле отношений Fraction.
     * например, в NumberQ, Rational
     * @param oneInScalarField --  is one in the Field
     * @return inverse matrix
     *
    public MatrixS inverse(Fraction oneInFractionField) {
    Element one = oneInFractionField.num.one(ring);
    int[][] EiEj = new int[2][];
    MatrixS[] adjointGen =  adjointEchelon(EiEj, one);
    int row_numb_after = EiEj[0].length;
    if ((row_numb_after == 0) || (row_numb_after < numberOfVar)) return null;
    Element det = adjointGen[1].M[0][0];
    MatrixS adj = adjointGen[0];
    int[][] col = adj.col;
    Element[][] adjArr = adj.M;
    int n = M.length;
    Fraction[][] inv = new Fraction[n][];
    for (int i = 0; i < n; i++) {
    int m = adjArr[i].length;
    Fraction[] invS = new Fraction[m];
    Element[] adjS = adjArr[i];
    for (int j = 0; j < m; j++) {
    invS[j] = new Fraction(adjS[j], det);
    }
    inv[i] = invS;
    }
    return new MatrixS(inv, col);
    }
     */
    /**
     * Обратная матрица
     *
     * @return обратная матрица или null(для необратимой)
     */
    public MatrixS inverseInFractions(Ring ring) {
        Element[] det = new Element[]{ring.numberONE};
        if (det[0] == null) {
            return null;
        }
        MatrixS S = adjointDet(det, ring);
        Element DET = det[0]; // DET in det
        if (DET.isZero(ring)) {
            return null;
        }
        int len = S.col.length;
        Element[] Row;
        for (int i = 0; i < len; i++) {
            Row = S.M[i];
            for (int j = 0; j < Row.length; j++) {
                Row[j] = (new Fraction(Row[j], ring)).divide(DET, ring);
            }
        }
        return S;
    }
//   public MatrixS extratNumbers(Ring ring) {
//       for (int i = 0; i < M.length; i++) {
//           Element[] S=M[i];
//           for (int j = 0; j < S.length; j++) {
//               Element E=S[j]; 
//               
//           }
//           
//       }
//       
//        Element[] det = new Element[]{ring.numberONE};
//        if (det[0] == null) {
//            return null;
//        }
//        MatrixS S = adjointDet(det, ring);
//        Element DET = det[0]; // DET in det
//        if (DET.isZero(ring)) { return null; }
//        int len = S.col.length;
//        Element[] Row;
//        for (int i = 0; i < len; i++) {
//            Row = S.M[i];
//            for (int j = 0; j < Row.length; j++) {
//                Row[j] = (new Fraction(Row[j],ring)).divide(DET, ring);
//            }
//        }
//        return S;
//    }


    /**
     * Вычисление присоединенной матрицы и ранга матрицы.
     * Вычисление присоединенной матрицы и списка столбцов colm[0],
     * содержащего ведущие элементы матрицы ступенчатой формы (adjoint * this):
     * (0,colm[0][0]),(1,colm[0][1]),...,(s,colm[0][s]),
     * где s=colm[0].length-1.
     * Ранг исходной матрицы:   rank(this)=colm[0].length.
     *
     * @param colm пустой массив для записи результата
     * @return присоединенная матрица
     */
    public MatrixS adjoint(int[][] colm, Ring ring) {
        Element one = ring.numberONE;
        if (one == null) {
            return null;
        }
        int[][] EiEj = new int[2][];
        MatrixS B = adjointEchelon(EiEj, one, ring)[0];
        colm[0] = EiEj[0];
        return B;
    }

    /**
     * Детерминант матрицы
     *
     * @param one - One of matrix element type
     * @return детерминант
     */
    public Element det(Element one, Ring ring) {
        int[][] EiEj = new int[2][];
        if (ring.flags.length < size) {
            ring.flags = new int[size];
            for (int i = 0; i < size; i++) {
                ring.flags[i] = -1;
            }
        }
        MatrixS S = adjointEchelon(EiEj, one, ring)[1];
        int row_numb_after = EiEj[0].length;
        return ((row_numb_after == 0) || (row_numb_after < colNumb))
            ? one.zero(ring) : S.M[0][0];
    }

    /**
     * Детерминант матрицы или null, когда матрица нулевая.
     * Если ранг матрицы меньше числа строк, то возвращается
     * результат 0, если матрица нулевая - то результат равен "null".
     *
     * @return детерминант или null
     */
    public Element det(Ring ring) {
        Element one = ring.numberONE;
        if (one == null) {
            return null;
        }
        return det(one, ring);
    }

    /**
     * Вычисление ядра линейного оператора
     *
     * @return --ядро оператора
     */
    public MatrixS kernel(int colNumb, Ring ring) {
        int[][] notZeroClms = new int[1][];
        return kernel(colNumb, notZeroClms, ring);
    }

    /**
     * Вычисление ядра линейного оператора
     *
     * @param notZeroClms -- int[][]: in first row it contents the list of nonZero rows in Squared kernel
     * @param ring        - Ring
     * @return --ядро оператора
     */
    public MatrixS kernel(int colNumb, int[][] notZeroClms, Ring ring) {
        int m = colNumb;
        Integer[] sizeOfNullSpase = new Integer[1];
        MatrixS Res = kernel(m, sizeOfNullSpase, ring);
        if (Res == null) { return myOne(ring); }
        return Res.deleteZeroColumns(notZeroClms, ring);
    }

    /**
     * Решение системы линейных уравнений в поле
     * *
     *
     * @param m -- порядковый номер столбца свободных членов
     *          в расширенной матрице системе
     * @return --матрицу, содержащую в последнем столбце
     * частное решение системы, а все остальные столбца образуют базис нуль-пространства
     * основной матрицы коэффициентов
     */
    public VectorS[] solve(int m, Ring ring) {
        Integer[] sizeNullSp = new Integer[1];
        MatrixS kernel = kernel(m, sizeNullSp, ring);
        Element det = null;
        if (kernel == null) { return null; }
        int[][] ccol = kernel.col;
        int i = ccol.length - 1;
        start:
        for (; i > 0; i--) {
            int j = ccol[i].length - 1;
            while ((j >= 0) && (ccol[i][j] != m - 1)) { j--; }
            if (j >= 0) {
                det = kernel.M[i][j].negate(ring); // сохранили определитель
                Element MM[][] = new Element[][]{kernel.M[i]};
                ccol[i] = Array.delOneElementInMatrixSRow(MM, ccol[i], j);
                kernel.M[i] = MM[0];
                break start;
            }
        }
        MatrixS res = new MatrixS(kernel.size, kernel.colNumb, kernel.M, ccol);
        int sNS = sizeNullSp[0];
        VectorS[] vv = new VectorS[sNS];
        int count = 0;
        for (int j = kernel.colNumb - sNS; j < kernel.colNumb; j++) {
            vv[count] = new VectorS(res.getColWithoutLastEl(j, ring));
            count++;
        }
        int type = ring.algebra[0];
        if ((type == Ring.Q) || (type <= Ring.Z) || ((type >= Ring.CZ64) && (type <= Ring.CZ)) ||
            (type == Ring.CQ)) { vv[sNS - 1] = vv[sNS - 1].divideToFraction(det, ring); } else {
            vv[sNS - 1] = vv[sNS - 1].divide(det, ring);
        }
        return vv;


    }
//    /**
//     *Вычисление ядра линейного оператора
//     * Для нулевой матрицы возвращается "null" вместо единичной матрицы m*m.
//     *
//     * @param m -- число столбцов в операторе
//     * @return --ядро оператора
//     */
//    public MatrixS kernel(int m, Ring ring) {
//        return kernel(m, null, ring);}

    /**
     * @param m               - число столбцов входной матрицы -- число строк у kernel
     * @param sizeOfNullSpase -- число столбцов у kernel
     * @param ring            - Ring
     * @return - kernel
     */
    public MatrixS kernel(int m, Integer[] sizeOfNullSpase, Ring ring) {
        Element one = ring.numberONE;
        AdjMatrixS x = new AdjMatrixS(expandToPow2with0(), one, ring);
        int[][] EiEj = new int[2][];
        EiEj[0] = x.Ei;
        EiEj[1] = x.Ej;
        MatrixS B = x.S;
        // Let us find DET:
        if (EiEj[0].length == 0) {
            return null;
        }
        if (sizeOfNullSpase != null) { sizeOfNullSpase[0] = m - EiEj[0].length; }
        int i = EiEj[0][0];
        int j = EiEj[1][0];
        int p = 0;
        while (B.col[i][p] != j) {
            p++;
        }
        Element det = B.M[i][p];
        //    System.out.println("det====="+det.toString(ring));    //   ########################16/02/2013
        return B.kernelGeneral(EiEj[0], EiEj[1], det, m, ring);
    }

    /**
     * Вычисление ядра линейного оператора размера 2^k x 2^k.
     * Для нулевой матрицы возвращается "null" вместо единичной матрицы m*m.
     *
     * @param colNumb_A -- число столбцов во входной матрице
     * @return --ядро оператора
     */
    public MatrixS kernelGeneral(int[] Ei, int[] Ej, Element det, int colNumb_A, Ring ring) {
        MatrixS S = multiplyLeftE(Ej, Ei).eraseDiagonal();
        Element[][] MM = new Element[colNumb_A][];
        int[][] ccol = new int[colNumb_A][];
        int[] flag = new int[colNumb_A];
        Element[] Det = new Element[]{det.negate(ring)};
        for (int i = 0; i < Ej.length; i++) {
            int j = Ej[i];
            MM[j] = S.M[j];
            ccol[j] = S.col[j];
            flag[j] = 1;
        }
        for (int i = 0; i < colNumb_A; i++) {
            if (flag[i] == 0) {
                MM[i] = Det;
                ccol[i] = new int[]{i};
            }
        }
        return new MatrixS(colNumb_A, colNumb_A, MM, ccol);
    }


    /**
     * Приведение матрицы к ступенчатой форме
     * <p>
     * Ступенчатая форма имеет вид:
     * || D у 0 s t 0 t r ||
     * || 0 0 D y z 0 r k ||
     * || 0 0 0 0 0 D x y ||,
     * где D- определитель ведущего блока,
     * которым в примере является блок с диаг. элементами 11, 23, 36.
     *
     * @return -- ступенчатая форма матрицы
     */
    public MatrixS toEchelonForm(Ring ring) {
        Element one = ring.numberONE;
        if (one == null) {
            return this;
        }
        int[][] EiEj = new int[2][];
        return adjointEchelon(EiEj, one, ring)[1];
    }

    public MatrixS toGroebnerEchelonForm(Element oneOfMatrixEl, Ring ring) {
        AdjMatrixS x = new AdjMatrixS(expandToPow2with0(), oneOfMatrixEl, ring);
        return x.S;
    }

    public MatrixS toGroebnerEchelonForm(Ring ring) {
        Element one = ring.numberONE;
        if (one == null) {
            return null;
        }
        AdjMatrixS x = new AdjMatrixS(expandToPow2with0(), one, ring);
        return x.S;
    }

    public void sort() {
        for (int i = 0; i < col.length; i++) {
            for (int j = 0; j < col[i].length - 1; j++) {
                if (col[i][j] > col[i][j + 1]) {
                    int[] sort = Array.sortPosUp(col[i]);
                    Element[] scalar = new Element[sort.length];
                    for (int k = 0; k < sort.length; k++) {
                        scalar[k] = M[i][sort[k]];
                        sort[k] = col[i][sort[k]];
                    }
                    col[i] = sort;
                    M[i] = scalar;
                    break;
                }
            }
        }
    }

    /**
     * Results is a pair of MatrixS
     * (A-adjoint matrix, S=A*this - echelon form -- the product of the adjont matrix and  this matrix).
     * Example:
     * MatrixS M = new MatrixS(new Element[][] {{2,3},{5,7}});
     * int [][] EiEj=new int[2][];
     * MatrixS[] B=M.adjointExtended(EiEj, M[0][0].one(ring));
     * if (B[1].subtractSquareMat(B[0].multCU(M)).isZero(ring))
     * System.out._println("VERY GOOD");
     * else System.out._println("BAD result");
     * if (EiEj[0].length!=0) // I.e. input matrix M was not Zero metrix
     * System.out._println("Det="+B[1].M[EiEj[0][0]][EiEj[1][0]]);
     *
     * @param EiEj an empty array.
     *             Two rows Ei, Ej, which fixed the matrix E, are returned in this array:
     *             Ei = EiEj[0], Ej = EiEj[1].
     * @return {A,S} -- pair of MatrixS
     */
    private MatrixS[] adjointExtended(int[][] EiEj, Element one, Ring ring) {
        AdjMatrixS x = new AdjMatrixS(this, one, ring);
        EiEj[0] = x.Ei;
        EiEj[1] = x.Ej;
        return (new MatrixS[]{x.A, x.S});
    }

    @Override
    public String toString() {
        return toString(Ring.ringR64xyzt);
    }

    @Override
    public String toString(Ring r) {
        //Element[][] sArr = toScalarArray(r);
        Element[][] sArr = toSquaredArray(r);

        return Array.toString(sArr, r);
    }

    public static String toStringMatrixArray(MatrixS[] matrs, String eq, Ring ring) {
        Element[][][] matrArr = new Element[matrs.length][][];
        for (int i = 0; i < matrs.length; i++) {
            matrArr[i] = matrs[i].toSquaredArray(ring);
        }
        return Array.toStringMatrixArray(matrArr, eq, ring);
    }

    /**
     * Print to String all data structures of MatrixS:
     * numberOfVar, colNumb, matrix M and matrix col.
     *
     * @return String with these 4 data structures
     */
    public String print(Ring r) {
        StringBuffer str =
            new StringBuffer("MatrixS print: size =" + size + " colNumb="
                             + colNumb + "\n matrix M=");
        str.append(Array.toString(M, r) + " matrix col=\n");
        str.append(Array.toString(col));
        return str.toString();
    }

// ///////////////////////////////////////////////////////////////////////////////////////
// ////////////////////////////////////////////////////////////////////////////////////////////////
// ***********************************************
    /**
     * Процедура умножения матриц с использованием алгоритма Штрассена.
     * @param b типа MatrixS, сомножитель
     * @return <tt> this * b </tt>  <br>
     * <b> Пример использования </b> <br>
     * <CODE> import matrix.*; <br>
     * import java.util.Random(); <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul>  Random rnd = new Random(); <br>
     * MatrixS matr1 = new MatrixS(32, 5000, rnd).convSM(); <br>
     * MatrixS matr2 = new MatrixS(32, 4000, rnd).convSM(); <br>
     * MatrixS msum = matr1.multS(matr2);</ul>
     * } </ul> } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и записываются в <tt>msum</tt>.
     *
    // ---------------------------------------------------------------------
    public MatrixS multS(MatrixS b) {


    if (M.length > 2) {
    MatrixS[] tb = this.split();
    MatrixS[] bb = b.split();
    MatrixS t1 = (tb[0].add(tb[3])).multS(bb[0].add(bb[3])),
    t2 = (tb[2].add(tb[3])).multS(bb[0]),
    t3 = tb[0].multS(bb[1].subtractSquareMat(bb[3])),
    t4 = tb[3].multS(bb[2].subtractSquareMat(bb[0])),
    t5 = (tb[0].add(tb[1])).multS(bb[3]),
    t6 = (tb[2].subtractSquareMat(tb[0])).multS(bb[0].add(bb[1])),
    t7 = (tb[1].subtractSquareMat(tb[3])).multS(bb[2].add(bb[3]));
    bb[0] = t1.add(t4.add(t7.subtractSquareMat(t5)));
    bb[1] = t3.add(t5);
    bb[2] = t2.add(t4);
    bb[3] = t1.add(t3.add(t6.subtractSquareMat(t2)));
    return join(bb);
    }
    else { // квант умножения Штрассена для SM
    long a11 = 0, a12 = 0, a21 = 0, a22 = 0,
    b11 = 0, b12 = 0, b21 = 0, b22 = 0;
    switch(M[0].length){
    case 1: {if(Mpos[0][0] == 0) {a11 = M[0][0]; a12 = 0;}
    else {a11 = 0; a12 = M[0][0];}} break;
    case 2: {a11 = M[0][0]; a12 = M[0][1];} break;
    }
    switch(M[1].length){
    case 1: {if(Mpos[1][0] == 0) {a21 = M[1][0]; a22 = 0;}
    else {a21 = 0; a22 = M[1][0];}} break;
    case 2: {a21 = M[1][0]; a22 = M[1][1];} break;
    }
    switch(b.M[0].length){
    case 1: {if(b.Mpos[0][0] == 0) {b11 = b.M[0][0]; b12 = 0;}
    else {b11 = 0; b12 = b.M[0][0];}} break;
    case 2: {b11 = b.M[0][0]; b12 = b.M[0][1];} break;
    }
    switch(b.M[1].length){
    case 1: {if(b.Mpos[1][0] == 0) {b21 = b.M[1][0]; b22 = 0;}
    else {b21 = 0; b22 = b.M[1][0];}} break;
    case 2: {b21 = b.M[1][0]; b22 = b.M[1][1];} break;
    }
    long t1 = (a11 + a22) * (b11 + b22),
    t2 = (a21 + a22) * b11,
    t3 = a11 * (b12 - b22),
    t4 = a22 * (b21 - b11),
    t5 = (a11 + a12) * b22,
    t6 = (a21 - a11) * (b11 + b12),
    t7 = (a12 - a22) * (b21 + b22);
    b11 = t1 + t4 + t7 - t5;
    b12 = t3 + t5;
    b21 = t2 + t4;
    b22 = t1 + t3 + t6 - t2;

    // получаем из четырех чисел -- компонентов ответа матрицу типа ZSM
    long[][] r = new long[2][];
    int[][] rp = new int[2][], c = new int[2][];
    //int l1 = Math.abs(b11.signum()), l2 = l1 + Math.abs(b12.signum());
    int l1,l2;
    if(b11 == 0) l1 = 0; else l1 = 1;
    if(b12 == 0) l2 = l1; else l2 = l1+1;
    r[0] = new long[l2];
    rp[0] = new int[l2];
    switch(l2){
    case 1: {if(l1 != 0) {r[0][0] = b11; rp[0][0] = 0;}
    else {r[0][0] = b12; rp[0][0] = 1;}} break;
    case 2: {r[0][0] = b11; rp[0][0] = 0; r[0][1] = b12; rp[0][1] = 1;} break;
    }
    int l3;
    if(b21 == 0) l3 = 0; else l3 = 1;
    int l4 = l1 + l3;
    c[0] = new int[l4 << 1];
    switch(l4) {
    case 1:{c[0][1] = 0; if(l3 == 0) {c[0][0] = 0;} else {c[0][0] = 1;}} break;
    case 2:{c[0][0] = 0; c[0][1] = 0; c[0][2] = 1; c[0][3] = 0;} break;
    }
    if(b22 == 0) l4 = l3; else l4 = l3+1;
    r[1] = new long[l4];
    rp[1] = new int[l4];
    switch(l4){
    case 1:{if(l3 != 0) {r[1][0] = b21; rp[1][0] = 0;}
    else {r[1][0] = b22; rp[1][0] = 1;}} break;
    case 2:{r[1][0] = b21; rp[1][0] = 0; r[1][1] = b22; rp[1][1] = 1;} break;
    }
    l2 = l2-l1; l4 = l2 + l4 - l3;
    c[1] = new int[l4<<1];
    switch(l4){
    case 1:{} break;
    case 2:{c[1][0] = 0; c[1][1] = l1; c[1][2] = l3;} break;
    }
    return new MatrixS(r, rp, c);
    }
    //return null;
    }
     */
    /**
     * Процедура пересылки матрицы MatrixS целиком на данный узел с данным тэгом
     *
     * @param node типа int, номер процессора - получателя
     * @param tag  типа int, тэг, передаваемый получателю
     * @param node типа int, номер процессора - отправителя
     * @param tag  типа int, тэг, передаваемый получателю
     * @return полученная матрица типа MatrixS
     * @thMs MPIException
     * <p>
     * // -------------------------------------------------------------
     * public void send(int node, int tag) thMs
     * MPIException {
     * int len = 0, len1 = M.length;
     * for (int i = 0; i < M.length; i++) {
     * len += M[i].length;
     * len1 += col[i].length;
     * }
     * long[] Rbuf = new long[len];
     * int[] RPbuf = new int[len + M.length], Cbuf = new int[len1];
     * len = 0; // теперь len отвечает за текущую позицию упаковки массивов в одномерный буфер M
     * len1 = 0; // бегунок для Mpos
     * int len2 = 0; // позиция для col
     * for (int i = 0; i < M.length; i++) {
     * RPbuf[len1] = M[i].length;
     * len1++;
     * Cbuf[len2] = col[i].length;
     * len2++;
     * for (int j = 0; j < M[i].length; j++) {
     * Rbuf[len] = M[i][j];
     * RPbuf[len1] = Mpos[i][j];
     * len++;
     * len1++;
     * }
     * for (int j = 0; j < col[i].length; j++) {
     * Cbuf[len2] = col[i][j];
     * len2++;
     * }
     * }
     * int[] srv = {M.length, Rbuf.length, RPbuf.length, Cbuf.length};
     * // посылка данных
     * MPI.COMM_WORLD.Send(srv, 0, 4, MPI.INT, node, tag);
     * MPI.COMM_WORLD.Send(Rbuf, 0, Rbuf.length, MPI.LONG, node, tag);
     * MPI.COMM_WORLD.Send(RPbuf, 0, RPbuf.length, MPI.INT, node, tag);
     * MPI.COMM_WORLD.Send(Cbuf, 0, Cbuf.length, MPI.INT, node, tag);
     * }
     * <p>
     * <p>
     * /** Процедура приема матрицы MatrixS с данного узла и с данным тэгом
     * @thMs MPIException
     * <p>
     * // ---------------------------------------------------------------
     * public static MatrixS recv(int node, int tag) thMs MPIException {
     * int[] srv = new int[4];
     * MPI.COMM_WORLD.Recv(srv, 0, 4, MPI.INT, node, tag);
     * long[] Rbuf = new long[srv[1]];
     * int[] RPbuf = new int[srv[2]], Cbuf = new int[srv[3]];
     * MPI.COMM_WORLD.Recv(Rbuf, 0, srv[1], MPI.LONG, node, tag);
     * MPI.COMM_WORLD.Recv(RPbuf, 0, srv[2], MPI.INT, node, tag);
     * MPI.COMM_WORLD.Recv(Cbuf, 0, srv[3], MPI.INT, node, tag);
     * // построение массивов -- компонентов результата
     * long[][] r = new long[srv[0]][];
     * int[][] rp = new int[srv[0]][], c = new int[srv[0]][];
     * int len = 0, len1 = 0, len2 = 0;
     * for (int i = 0; i < srv[0]; i++) {
     * r[i] = new long[RPbuf[len1]];
     * rp[i] = new int[RPbuf[len1]];
     * len1++;
     * for (int j = 0; j < rp[i].length; j++) {
     * r[i][j] = Rbuf[len];
     * len++;
     * rp[i][j] = RPbuf[len1];
     * len1++;
     * }
     * c[i] = new int[Cbuf[len2]];
     * len2++;
     * for (int j = 0; j < c[i].length; j++) {
     * c[i][j] = Cbuf[len2];
     * len2++;
     * }
     * }
     * return new MatrixS(r, rp, c);
     * }
     */
    public VectorS oneSysSolvForFraction(Ring ring) {
        Element[] r = oneSysSolv_and_Det(ring);
        int len = M.length;
        Element[] r1 = new Element[len]; // old r.length
        for (int i = 0; i < len; i++) { //old r.length
            r1[i] = (new Fraction(r[i], r[r.length - 1])).cancel(ring);
        }
        return new VectorS(r1);
    }

    public Element[] oneSysSolv_and_Det(Ring ring) {
        Element det = M[0][0];
        int m = colNumb - 1;
        Element[] res = new Element[m + 1];
        res[m] = det;
        int n = col.length;
        for (int i = 0; i < n; i++) {
            int cil = col[i].length - 1;
            if (col[i][cil] == m) {
                res[i] = M[i][cil];
            } else {
                res[i] = M[0][0].zero(ring);
            }
        }
        int i1 = 0;
        int i2 = 0;
        for (int i = 0; i < n; i++) {
            i2 = i;
            while ((i2 < m) && (col[i][0] != i1)) {
                res[i2++] = M[0][0].zero(ring);
                i1++;
            }
            i1++;
        }
        return res;
    }

    public boolean isSysSolvable() {
        int n = col.length;
        int i = n - 1;
        while (col[i].length == 0) {
            i--;
        }
        return (col[i][0] == colNumb - 1) ? false : true;
    }

    public MatrixS toMatrixS(Element one, Ring ring) {

        int n = M.length;
        int[][] c = new int[n][];
        Element[][] NM = new Element[n][];
        int lastRow = 0; // last nonzero row
        for (int i = 0; i < n; i++) {
            int m = M[i].length;
            if (m != 0) {
                Element[] MM = M[i];
                int[] CC = col[i];
                Element[] mm = new Element[m];
                int[] cc = new int[m];
                int s = 0;
                for (int j = 0; j < m; j++) {
                    mm[s] = MM[j].toNumber(one.numbElementType(), ring);
                    if (!mm[s].isZero(ring)) {
                        cc[s++] = CC[j];
                    }
                }
                if (s != 0) {
                    lastRow = i;
                    if (s == m) {
                        c[i] = cc;
                        NM[i] = mm;
                    } else if (s != 0) {
                        Element[] mm1 = new Element[s];
                        int[] cc1 = new int[s];
                        System.arraycopy(mm, 0, mm1, 0, s);
                        System.arraycopy(cc, 0, cc1, 0, s);
                        c[i] = cc1;
                        NM[i] = mm1;
                    }
                }
            }
        }
        lastRow++;
        if (lastRow != n) {
            int[][] c1 = new int[lastRow][];
            Element[][] NM1 = new Element[lastRow][];
            System.arraycopy(c, 0, c1, 0, lastRow);
            System.arraycopy(NM, 0, NM1, 0, lastRow);
            return new MatrixS(size, colNumb, NM1, c1);
        } else {
            return new MatrixS(size, colNumb, NM, c);
        }
    }

    public MatrixS toPolMatrixS(int typeOfPolynom, Ring ring) {
        int n = M.length;
        int[][] c = new int[n][];
        Element[][] NM = new Element[n][];
        int lastRow = 0; // last nonzero row
        for (int i = 0; i < n; i++) {
            int m = M[i].length;
            if (m != 0) {
                Element[] MM = M[i];
                int[] CC = col[i];
                Element[] mm = new Element[m];
                int[] cc = new int[m];
                int s = 0;
                for (int j = 0; j < m; j++) {
                    mm[s] = ((Polynom) MM[j]).toPolynom(typeOfPolynom, ring);
                    if (!mm[s].isZero(ring)) {
                        cc[s++] = CC[j];
                    }
                }
                if (s != 0) {
                    lastRow = i;
                    if (s == m) {
                        c[i] = cc;
                        NM[i] = mm;
                    } else if (s != 0) {
                        Element[] mm1 = new Element[s];
                        int[] cc1 = new int[s];
                        System.arraycopy(mm, 0, mm1, 0, s);
                        System.arraycopy(cc, 0, cc1, 0, s);
                        c[i] = cc1;
                        NM[i] = mm1;
                    }
                }
            }
        }
        lastRow++;
        if (lastRow != n) {
            int[][] c1 = new int[lastRow][];
            Element[][] NM1 = new Element[lastRow][];
            System.arraycopy(c, 0, c1, 0, lastRow);
            System.arraycopy(NM, 0, NM1, 0, lastRow);
            return new MatrixS(size, colNumb, NM1, c1);
        } else {
            return new MatrixS(size, colNumb, NM, c);
        }
    }

    /**
     * Значение полиномиальной матрицы при значениях переменных
     * полиномов определенных в массиве значений valVars
     *
     * @param valVars - значение переменных в полиномах
     * @return числовая матрица, полученная после подстановки
     * значений переменных в ее элементы (полиномы).
     */
    public MatrixS valOfPolMatrixS(Element[] valVars, Ring r) {
        //Element one= valVars[0].one(ring);
        int n = M.length;
        int[][] c = new int[n][];
        Element[][] NM = new Element[n][];
        int lastRow = 0; // last nonzero row
        for (int i = 0; i < n; i++) {
            int m = M[i].length;
            if (m != 0) {
                Element[] MM = M[i];
                int[] CC = col[i];
                Element[] mm = new Element[m];
                int[] cc = new int[m];
                int s = 0;
                for (int j = 0; j < m; j++) {
                    mm[s] = ((Polynom) MM[j]).value(valVars, r);
                    if (!mm[s].isZero(r)) {
                        cc[s++] = CC[j];
                    }
                }
                if (s != 0) {
                    lastRow = i;
                    if (s == m) {
                        c[i] = cc;
                        NM[i] = mm;
                    } else if (s != 0) {
                        Element[] mm1 = new Element[s];
                        int[] cc1 = new int[s];
                        System.arraycopy(mm, 0, mm1, 0, s);
                        System.arraycopy(cc, 0, cc1, 0, s);
                        c[i] = cc1;
                        NM[i] = mm1;
                    }
                }
            }
        }
        lastRow++;
        if (lastRow != n) {
            int[][] c1 = new int[lastRow][];
            Element[][] NM1 = new Element[lastRow][];
            System.arraycopy(c, 0, c1, 0, lastRow);
            System.arraycopy(NM, 0, NM1, 0, lastRow);
            return new MatrixS(size, colNumb, NM1, c1);
        } else {
            return new MatrixS(size, colNumb, NM, c);
        }
    }


    /*
    public MatrixL toMatrixL() {
    int len = col.length;
    if (len == 0) {return (new MatrixL(new long[][] { {0} }));
    }
    int m = 0;
    for (int i = 0; i < len; i++) {
    for (int j = 0; j < col[i].length; j++) {
    m = Math.max(m, col[i][j]);
    }
    }
    m++;
    long[][] res = new long[numberOfVar][colNumb];
    for (int i = 0; i < len; i++) {
    long[] resI = res[i];
    Element[] MM = M[i];
    int[] CC = col[i];
    for (int j = 0; j < MM.length; j++) {
    resI[CC[j]] = MM[j].longValue();
    }
    }
    return new MatrixL(res);
    }

     */

    /**
     * Constructor of random matrixS of  polynomials or numbers
     *
     * @param r          -- row numbers
     * @param c          -- column numbers
* @param density    -- is an integer of range 0,1...100 (100=100%);
     *                       of range -1...-100 (-100=1%, -50=0.5%,-1=0.01%).
     * @param randomType -- array of:
     *                   [maxPowers_1_var,.., maxPowers-last_var,  type of coeffs, density of polynomials, nbits]
     *                   The density is an integer of range 0,1...100.
     * @param ran        -- Random issue
     * @param one        -- one of the matrix elements ring
     * @return array2d of Elements
     */
    public MatrixS(
        int r, int c, int density,
        int[] randomType,
        Random ran,
        Element one, Ring ring
    ) {
        this(randomScalarArr2d(r, c, density, randomType, ran, ring), ring);
    }

    public MatrixS(
            int r, int c, double density,
            int[] randomType,
            Random ran,
            Element one, Ring ring
    ) {
        this(randomArray2d(r, c, density, randomType, ran, ring), ring);
    }

    /** Method for construction of random 2D array of  polynomials or numbers

     * @param r -- row numbers
     * @param c -- column numbers
     * @param Density -- is an integer of range 0,1...10000.
     * @param randomType -- array of:
     *   [maxPowers_1_var,.., maxPowers-last_var,  type of coeffs, density of polynomials, nbits]
     *   The density is an integer of range 0,1...100.
     * @param ran -- Random issue
     * @param one -- one of the matrix elements ring
     * @return array2d of Elements
     */
    public static Element[][] randomScalarArr2d(int r, int c, int Density,
                                                int[] randomType,
                                                Random ran,
                                                Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        //System.out.println("" + Array.toString(randomType));
        if (randomType.length > 2) {
            one = Polynom.polynomFromNumber(one, ring);
        }
        double density = (Density >= 0) ? Density / 100.0 : 0.01 / (Density);
        Element zero = one.myZero(ring);
        Element[][] M = new Element[r][c];
        if (density == 1.0) {
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    M[i][j] = one.random(randomType, ran, ring);//one(ring);
                }
            }
            return M;
        }
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                double m1 = ran.nextDouble();
                M[i][j] = (m1 > density) ? zero : one.random(randomType, ran, ring);
            }
        }
        return M;
    }

    /** Method for construction of random 2D array of  polynomials or numbers
     * @param r -- row number of rows
     * @param c --  number of columns
     * @param Density --  density in %:  100, 2,  0.001.
     * @param randomType -- array of:
     *   [maxPowers_1_var,.., maxPowers-last_var,  type of coeffs, density of polynomials, nbits]
     *   The density is an integer of range 0,1...100.
     * @param ran -- Random issue
     * @return 2d-array of Elements:  Element[][]
     */
    public static Element[][] randomArray2d(int r, int c, double Density,
                                            int[] randomType, Random ran,  Ring ring) {
        long currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory 1 btes: " + DispThread.bytesToMegabytes(currentMemory));

        Element one = Ring.oneOfType(ring.algebra[0]);

        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory 2 btes: " + DispThread.bytesToMegabytes(currentMemory));
        if (randomType.length > 2) {one = Polynom.polynomFromNumber(one, ring); }
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory 3 btes: " + DispThread.bytesToMegabytes(currentMemory));
        Element zero = one.myZero(ring);
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory 4 btes: " + DispThread.bytesToMegabytes(currentMemory));

        Element[][] M = new Element[r][c];

        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory 5 btes: " + DispThread.bytesToMegabytes(currentMemory));
        double density= Density*0.01;
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory 6 btes: " + DispThread.bytesToMegabytes(currentMemory));

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                double m1 = ran.nextDouble();
                M[i][j] = (m1 > density) ? zero : one.random(randomType, ran, ring);
            }
        }
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used memory 7 btes: " + DispThread.bytesToMegabytes(currentMemory));
        return M;
    }

    /**
     * Процедура сокращения всех элементов матрицы над NumberQ.
     * Последовательная процедура.
     *
     * @param ring
     * @param s    число сомножитель
     * @return this * s
     */
    public MatrixS cancel(Ring ring) {
        int n = M.length;

        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            int m = Mi.length;
            Element[] ri = new Element[m];
            r[i] = ri;
            for (int j = 0; j < m; j++) {
                ri[j] = (Mi[j] instanceof Fraction)
                    ? ((Fraction) Mi[j]).cancel(ring) : Mi[j];

            }
        }
        return new MatrixS(size, colNumb, r, col);
    }

    /**
     * Вычисление обобщенной обратной матрицы методом Эрмита
     *
     * @return обобщенная обратная матрица А+
     */
    public MatrixS GenInvers(Ring ring) {
        MatrixS Atr = this.transpose();
        MatrixS AAtr = this.multiply(Atr, ring);  // M= (A*A^T)^2
        MatrixS M = AAtr.multiply(AAtr, ring);
        MatrixS E = M.adjoint(ring).cancel(ring); //  E= Ad(M)
        MatrixS P = E.multiply(M, ring);    // pt= (EM)^T
        MatrixS Ptr = P.transpose();
        MatrixS F = Ptr.adjoint(ring).cancel(ring); // F=Ad(pt)
        MatrixS Ftr = F.transpose();               //
        MatrixS R = F.multiply(Ptr, ring);  // R= F*F^T;
        Element coef = R.M[0][0].multiply(R.M[0][0], ring);
        MatrixS MR = Ftr.multiply(R.multiply(E, ring), ring);
        MatrixS AtMR = Atr.multiply(MR, ring);
        MatrixS Moor = AtMR.multiply(AAtr, ring);
        MatrixS MoorPen = Moor.divideByNumbertoFraction(coef, ring);
        return MoorPen;
    }

    @Override
    public Element toNumber(int numberType, Ring ring) {
        return M[0][0].toNumber(numberType, ring);
    }

    @Override
    public int numbElementType() {
        return Ring.MatrixS;
    }


    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        if (x.numbElementType() < Ring.MatrixS) {
            return 1;
        }
        MatrixS X = (MatrixS) x;
        if (size != X.size) {
            return (int) Math.signum(size - X.size);
        }
        Element[][] mm = M;
        Element[][] xm = X.M;
        int row = mm.length;
        int rowx = xm.length;
        if (row != rowx) {
            return (int) Math.signum(row - rowx);
        }

        for (int i = 0; i < row; i++) {
            int row1 = mm[i].length;
            int rowx1 = xm[i].length;
            if (row1 != rowx1) {
                return (int) Math.signum(row1 - rowx1);
            }
            for (int j = 0; j < row1; j++) {
                if (mm[i][j].compareTo(xm[i][j], ring) != 0) {
                    return mm[i][j].compareTo(xm[i][j], ring);
                }
            }
        }
        return 0;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        int n = M.length;
        int[][] c = new int[n][];
        Element[][] NM = new Element[n][];
        int lastRow = 0; // last nonzero row
        for (int i = 0; i < n; i++) {
            int m = M[i].length;
            if (m != 0) {
                Element[] MM = M[i];
                int[] CC = col[i];
                Element[] mm = new Element[m];
                int[] cc = new int[m];
                int s = 0;
                for (int j = 0; j < m; j++) {
                    mm[s] = MM[j].toNewRing(Algebra, r);
                    if (!mm[s].isZero(r)) {
                        cc[s++] = CC[j];
                    }
                }
                if (s != 0) {
                    lastRow = i;
                    if (s == m) {
                        c[i] = cc;
                        NM[i] = mm;
                    } else if (s != 0) {
                        Element[] mm1 = new Element[s];
                        int[] cc1 = new int[s];
                        System.arraycopy(mm, 0, mm1, 0, s);
                        System.arraycopy(cc, 0, cc1, 0, s);
                        c[i] = cc1;
                        NM[i] = mm1;
                    }
                }
            }
        }
        lastRow++;
        if (lastRow == 1) { return zeroMatrix(); }
        if (lastRow != n) {
            int[][] c1 = new int[lastRow][];
            Element[][] NM1 = new Element[lastRow][];
            System.arraycopy(c, 0, c1, 0, lastRow);
            System.arraycopy(NM, 0, NM1, 0, lastRow);
            return new MatrixS(size, colNumb, NM1, c1);
        } else {
            return new MatrixS(size, colNumb, NM, c);
        }
    }

    /**
     * След матрицы.
     *
     * @return Element
     */
    public Element trace(Ring ring) {

        Element res = ring.numberZERO();
        //System.out.println("res = "+res);
        int len = col.length;
        for (int i = 0; i < len; i++) {
            int[] CC = col[i];
            for (int j = 0; j < CC.length; j++) {
                if (CC[j] == i) {
                    //System.out.println("M[i][j]= "+M[i][j]);
                    res = res.add(M[i][j], ring);
                    //System.out.println("res = "+res);
                    break;
                }
            }

        }
        return res;

    }

    /**
     * Вычитание из диагональных элементов матрицы ее следа.
     * Нужно для вычисления характеристического полинома с помощью алгоритма Фаддеева
     *
     * @return Element
     */
    public MatrixS diag_trace(Element trace, Ring ring) {

        int len = col.length;
        int[] res = new int[len];
        res[0] = -1;
        int k = 0;
        MatrixS this_trace = copy();
        for (int i = 0; i < len; i++) {
            int[] CC = this_trace.col[i];
            int j = 0;
            for (j = 0; j < CC.length; j++) {
                if (CC[j] == i) {
                    this_trace.M[i][j] = this_trace.M[i][j].subtract(trace, ring);
                    break;
                }
            }
            if (j == CC.length) {
                res[k] = i;
                k++;
            }

        }
        if (res[0] == -1) {
            return this_trace;
        } else {
            k = 0;
            Element[][] ma = new Element[len][];
            int colma[][] = new int[len][];
            Element[] temp;

            for (int i = 0; i < len; i++) {
                temp = this_trace.M[i];
                if (i == res[k]) {
                    ma[i] = new Element[temp.length + 1];
                    System.arraycopy(temp, 0, ma[i], 0, temp.length);
                    ma[i][temp.length] = trace.negate(ring);
                    colma[i] = new int[temp.length + 1];
                    System.arraycopy(col[i], 0, colma[i], 0, temp.length);
                    colma[i][temp.length] = i;
                    k++;
                } else {
                    ma[i] = new Element[temp.length];
                    System.arraycopy(temp, 0, ma[i], 0, temp.length);
                    colma[i] = new int[temp.length];
                    System.arraycopy(col[i], 0, colma[i], 0, temp.length);
                }
            }

            return new MatrixS(ma, colma);
        }

    }
//    /**
//     * Умножение блока this=A12 на L_11 слева, так чтобы полчить L_11_12
//     * @param L
//     * @param d0
//     * @param ring
//     * @return
//     */
//    public MatrixS multL_A12(MatrixS L, Element d0, Ring ring){ MatrixS res=null;
//       int n=this.numberOfVar;
//    return res;}

    public MatrixS multiplyRightE(int[] Ei, int[] Ej) {
        System.out.println("t5his=" + this + "  " + Array.toString(Ei) + "  " + Array.toString(Ej));
        int[] EI = new int[size];
        for (int i = 0; i < EI.length; i++) {
            EI[i] = -1;
        }
        for (int i = 0; i < Ej.length; i++) {
            EI[Ei[i]] = Ej[i];
        }
        Element[][] MM = new Element[M.length][0];
        int[][] cc = new int[M.length][0];
        for (int i = 0; i < col.length; i++) {
            int[] ci = col[i].clone();
            int zero_n = 0;
            for (int j = 0; j < col[i].length; j++) {
                ci[j] = EI[ci[j]];
                if (ci[j] == -1) {
                    zero_n++;
                }
            }
            if (zero_n != 0) {
                Element[] Mi = new Element[M[i].length - zero_n];
                int[] cci = new int[M[i].length - zero_n];
                int j1 = 0;
                for (int j = 0; j < ci.length; j++) {
                    if (ci[j] != -1) {
                        cci[j1] = ci[j];
                        Mi[j1++] = M[i][j];
                    }
                }
                MM[i] = Mi;
                cc[i] = cci;
            } else {
                MM[i] = M[i];
                cc[i] = ci;
            }
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Умножение справа на полную диагональную матрицу без нулевых элементов
     *
     * @param D -- диагональная матрица
     * @return
     */
    public MatrixS multiplyRightD(Element[] D, Ring ring) {
        int Dl = D.length;
        Element[][] MM = new Element[M.length][0];
        for (int i = 0; i < col.length; i++) {
            int n = 0;
            Element[] Mii = M[i];
            int[] cii = col[i];
            Element[] Mi = new Element[Mii.length];
            for (int j = 0; j < Mii.length; j++) {
                if (cii[j] < Dl) {
                    Mi[j] = Mii[j].multiply(D[cii[j]], ring);
                } else {
                    Mi[j] = NumberZ.ZERO;
                    n++;
                }
            }
            MM[i] = Mi;
        }
        return new MatrixS(size, colNumb, MM, col);
    }

    public MatrixS multiplyRightDE(int[] Ei, int[] Ej, Element[] D, Ring ring) {
        // System.out.println("rrr="+numberOfVar+"  "+Array.toString(Ei)+"  "+Array.toString(Ej));
        int[] EI = new int[size];
        Element[] DD = new Element[size];
        for (int i = 0; i < EI.length; i++) { EI[i] = -1; }
        for (int i = 0; i < Ej.length; i++) {
            EI[Ei[i]] = Ej[i];
        }
        int[] sort = Array.sortUp(Ei);
        for (int i = 0; i < sort.length; i++) { DD[sort[i]] = D[i]; }
        Element[][] MM = new Element[M.length][0];
        int[][] cc = new int[M.length][0];
        for (int i = 0; i < col.length; i++) {
            Element[] Mi = new Element[M[i].length];
            int[] ci = col[i].clone();
            int zero_n = 0;
            for (int j = 0; j < col[i].length; j++) {
                int ii = EI[ci[j]];
                if (ii == -1) {
                    zero_n++;
                } else {
                    Mi[j] = DD[ci[j]].multiply(M[i][j], ring);
                }
                ci[j] = ii;
            }
            if (zero_n != 0) {
                Element[] NMi = new Element[M[i].length - zero_n];
                int[] cci = new int[M[i].length - zero_n];
                int j1 = 0;
                for (int j = 0; j < ci.length; j++) {
                    if (ci[j] != -1) {
                        cci[j1] = ci[j];
                        NMi[j1++] = Mi[j];
                    }
                }
                MM[i] = NMi;
                cc[i] = cci;
            } else {
                MM[i] = Mi;
                cc[i] = ci;
            }
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Нулевая матрица размера 0x0
     *
     * @return - нулевая матрица размера 1.
     */
    public static MatrixS zeroMatrix() {
        return zeroMatrix;
    }

    public static MatrixS zeroMatrix = new MatrixS(0, 0, new Element[0][0], new int[0][0]);

    /**
     * Нулевая квадратная матрица данного порядка
     *
     * @param n типа int, количество строк и столбцов матрицы
     * @return нулевая матрица порядка n
     */
    public static MatrixS zeroMatrix(int n) {
        return new MatrixS(n, 0, new Element[n][0], new int[n][0]);
    }

    public static MatrixS Eij2MatrixS_Z(int[] Ei, int[] Ej, int N) {
        MatrixS res = MatrixS.zeroMatrix(N);
        for (int i = 0; i < Ei.length; i++) { res.putElement(NumberZ.ONE, Ei[i], Ej[i]); }
        return res;
    }
//   /**
//    *  Join of Ei and Ej matrix
//    * @param mi = [m11Ei,m12Ei,m21Ei,m22Ei]
//    * @param mj = [m11Ej,m12Ej,m21Ej,m22Ej]
//    * @param N -- block numberOfVar
//    * @return  [Ei,Ej]
//    */
//   public static int[][] joinEij( int[][] mi, int[][] mj, int N) {
//     int len=0; for(int i=0;i<4;i++) len+=mi[i].length;
//     int[] Ei=new int[len]; int[] Ej=new int[len];
//       int j=0; int[]t=mi[0];  int[]f=mj[0];
//                        for (int i = 0; i < t.length;) { Ei[j] = t[i];   Ej[j++]=f[i++];}
//       t=mi[1];f=mj[1]; for (int i = 0; i < t.length;) { Ei[j] = t[i];   Ej[j++]=f[i++]+N;}
//       t=mi[2];f=mj[2]; for (int i = 0; i < t.length;) { Ei[j] = t[i]+N; Ej[j++]=f[i++];}
//       t=mi[3];f=mj[3]; for (int i = 0; i < t.length;) { Ei[j] = t[i]+N; Ej[j++]=f[i++]+N;}
//       int[][] res=new int[2][]; res[0]=Ei; res[1]=Ej;
//       return res;
//    }

    /**
     * Join of Ei and Ej matrix
     *
     * @param mi = [m11Ei,m12Ei,m21Ei,m22Ei]
     * @param mj = [m11Ej,m12Ej,m21Ej,m22Ej]
     * @param N  -- block size
     * @return [Ei, Ej]
     */
    public static int[][] joinEij(int[][] mi, int[][] mj, int N) {
        int len = 0;
        for (int i = 0; i < 4; i++) {
            len += mi[i].length;
        }
        int[] Ei = new int[len];
        int[] Ej = new int[len];
        int j = 0;
        int[] t = mi[0];
        int[] f = mj[0];
        for (int i = 0; i < t.length; ) {
            Ei[j] = t[i];
            Ej[j++] = f[i++];
        }
        t = mi[1];
        f = mj[1];
        for (int i = 0; i < t.length; ) {
            Ei[j] = t[i];
            Ej[j++] = f[i++] + N;
        }
        t = mi[2];
        f = mj[2];
        for (int i = 0; i < t.length; ) {
            Ei[j] = t[i] + N;
            Ej[j++] = f[i++];
        }
        t = mi[3];
        f = mj[3];
        for (int i = 0; i < t.length; ) {
            Ei[j] = t[i] + N;
            Ej[j++] = f[i++] + N;
        }
        int[][] res = new int[2][];
        res[0] = Ei;
        res[1] = Ej;
        return res;
    }

    /**
     * Join of diagonal matrix with diagonal blocks a and b
     *
     * @param a -- left upper block
     * @param b -- right bottom block
     * @return
     */
    public static MatrixS joinDiag(MatrixS a, MatrixS b) {
        int len = a.size;
        int a_len = a.M.length;
        int b_len = b.M.length;
        int size = (len << 1);
        int n = (b_len == 0) ? a_len : len + b_len;
        int colNumb = (b.colNumb == 0) ? a.colNumb : len + b.colNumb;
        Element[][] r = new Element[n][0];
        int[][] c = new int[n][0];
        if (a_len > 0) {
            System.arraycopy(a.M, 0, r, 0, a_len);
            System.arraycopy(a.col, 0, c, 0, a_len);
        }
        if (b_len > 0) {
            System.arraycopy(b.M, 0, r, len, b_len);
        }
        for (int i = 0; i < b_len; i++) {
            int[] c1 = new int[b.col[i].length];
            int[] c0 = b.col[i];
            for (int s = 0; s < c1.length; s++) {
                c1[s] = c0[s] + len;
            }
            c[i + len] = c1;
        }
        MatrixS res = new MatrixS(size, colNumb, r, c);
        return res;
    }

    @Override
    public Object clone() {
        int[][] c = new int[col.length][];
        for (int j = 0; j < col.length; j++) {
            int l = col[j].length;
            c[j] = new int[l];
            System.arraycopy(col[j], 0, c[j], 0, l);
        }
        return new MatrixS(size, colNumb, M, c);
    }

    public Object cloneElements() {
        int n = this.M.length;
        Element[][] M1 = new Element[n][];
        for (int i = 0; i < this.M.length; i++) {
            M1[i] = new Element[this.M[i].length];
            for (int j = 0; j < this.M[i].length; j++) {
                M1[i][j] = (Element) this.M[i][j].clone();
            }
        }
        return new MatrixS(size, colNumb, M1, col);
    }

    /**
     * Integrate of each
     *
     * @param ring
     * @param s    число сомножитель
     * @return this
     */
    @Override
    public MatrixS integrate(Ring ring) {
        int n = M.length;
        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            int m = Mi.length;
            Element[] ri = new Element[m];
            r[i] = ri;
            for (int j = 0; j < m; j++) {
                ri[j] = Mi[j].integrate(ring);
            }
        }
        return new MatrixS(size, colNumb, r, col);
    }

    /**
     * Перестановка столбцов матрицы в соответствии с новыми номерами
     * столбцов Ec={i1,i2,..in,j1,j2,..,jn}}.
     * Перестановки: i1-->j1,i2-->j2,..,in-->jn),
     *
     * @param Ec - номера переставляемых столбцов.
     * @return the matrix after permutation of columns
     */
    public MatrixS permutationOfColumns(int[] Ec) {
        int n = (Ec.length >> 1);
        if (n == 0) {
            return this;  // n -- половинка длины Ec
        }
        int[] colNumbs = new int[colNumb];
        if (MaxPositiveNumber < colNumb) { newMaxPositiveNumbers(4 * colNumb); }
        System.arraycopy(positiveNumbers, 0, colNumbs, 0, colNumb);
        int ii = n;
        int newMaxCol = 0;
        for (int k = 0; k < n; k++) {
            int i = Ec[k];
            int j = Ec[ii++];
            if (i < colNumb) {
                colNumbs[i] = j;
                newMaxCol = Math.max(newMaxCol, j);
            }
        }
        // newMaxCol -- наибольший новый номер столбца
        // colNumbs[i] -- это новый номер столбца для i-го столбца
        // теперь меняем номера "не глядя"
        int[][] cc = new int[col.length][0];
        for (int i = 0; i < col.length; i++) {
            int[] ci = new int[col[i].length];
            System.arraycopy(col[i], 0, ci, 0, ci.length);
            cc[i] = ci;
            for (int j = 0; j < ci.length; j++) {
                ci[j] = colNumbs[ci[j]];
            }
        }
        return new MatrixS(size, Math.max(colNumb, newMaxCol + 1), M, cc);
    }

    /**
     * Перестановка n столбцов матрицы в обратном порядке
     *
     * @param n - число переставляемых столбцов.
     * @return the matrix after permutation of columns
     */
    public MatrixS permutationOfColumns(int n) {
        int[] Ec = new int[2 * n];
        for (int i = 0; i < n; i++) { Ec[i] = i; }
        int k = n - 1;
        for (int i = n; i < 2 * n; i++) { Ec[i] = k--; }
        return permutationOfColumns(Ec);
    }

    /**
     * Перестановка строк матрицы в соответствии с новыми номерами
     * строк Er={i1,j1;i2,j2;i3,j3.... in,i{n+1}}.
     * Перестановки: i1-->j1,i2-->j2,..,in-->j{n+1}),
     *
     * @param Er - номера переставляемых строк.
     * @return the matrix after permutation of rows
     * (очень компактная процедура)
     */
    public MatrixS permutationOfRows(int[] Er) {
        int k = (Er.length >> 1);
        if (k == 0) {
            return this; // k - number of permutations
        }
        int Er_length = Er.length;
        int n = col.length - 1;// max(col.length-1,max);
        for (int s = k; s < Er.length; s++) { if (Er[s] > n) { n = Er[s]; } }
        n++;
        Element[][] MM = new Element[n][0];
        System.arraycopy(M, 0, MM, 0, M.length);
        int[][] cc = new int[n][0];
        System.arraycopy(col, 0, cc, 0, col.length);
        int ii = k;
        for (int i = 0; i < k; i++) {
            int p = Er[i];
            int q = Er[ii++];
            if (p < col.length) {
                MM[q] = M[p];
                cc[q] = col[p];
            } else {
                MM[q] = new Element[0];
                cc[q] = new int[0];
            }
        }
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Перестановка строк матрицы в обратном порядке
     *
     * @param n - new number of rows (if this.M.length>n you will get trancated matrix!)
     * @return the matrix after permutation of rows
     * (очень компактная процедура)
     */
    public MatrixS permutationOfRows(int n) {
        //   System.out.println("size00="+size+"  "+ colNumb+"  "+M.length);
        int k = this.col.length;
        int m = Math.min(k, n);
        Element[][] MM = new Element[n][0];
        int[][] cc = new int[n][0];
        int p = n - 1;
        for (int i = 0; i < m; i++) {
            MM[p] = M[i];
            cc[p--] = col[i];
        }
        //      System.out.println("size01="+size+"  "+ colNumb+"  "+MM.length);
        return new MatrixS(Math.max(n, size), colNumb, MM, cc);
    }

    /**
     * Multiplication of permutations.
     *
     * @param e0 - first  permutation: {i0,i1,..,ik,j0,j1,..,jk} i0-->j0, ..
     * @param e1 - second  permutation: {i0,i1,..,ik,j0,j1,..,jk} i0-->j0, ..
     * @param N  - the track 0..(N-1) was permuted
     * @return - result of two permutations: first, then second
     */
    public static int[] multPermutations(int[] e0Inp, int[] e1Inp, int N) {
        int[] e1 = e1Inp.clone();
        int[] e0 = e0Inp.clone();
        int l0 = (e0.length >> 1);
        int l1 = (e1.length >> 1);
        if (l1 == 0) { return e0; }
        if (l0 == 0) { return e1; }
        int[] pos = new int[N];
        int[] back = new int[N];
        int[] res1 = new int[N];
        int[] res2 = new int[N];
        if (MaxPositiveNumber < N) { newMaxPositiveNumbers(4 * N); }
        System.arraycopy(positiveNumbers, 0, pos, 0, N); // this is a track 0..N-1
        int iN = l1;
        for (int i = 0; i < l1; i++) {
            int ei = e1[i];
            pos[ei] = e1[iN];
            back[ei] = iN++;
        } // new value for e1-permut
        int m = 0; // resulting pointer
        iN = l0;
        for (int i = 0; i < l0; i++) {
            int s1 = e0[i];
            int s2 = e0[iN++];
            if (pos[s2] != s1) {
                res1[m] = s1;
                res2[m++] = pos[s2];
            }
            if (back[s2] > 0) { e1[back[s2]] = -1; }
        }
        iN = l1;
        for (int i = 0; i < l1; i++) {
            if (e1[iN] != -1) {
                res1[m] = e1[i];
                res2[m++] = e1[iN];
            }
            iN++;
        }
        int[] res = new int[2 * m];
        System.arraycopy(res1, 0, res, 0, m);
        System.arraycopy(res2, 0, res, m, m);
        return res;
    }

    public static int[] multPermutationsVer1_(int[] e0Inp, int[] e1Inp, int N) {
        int[] e0 = e0Inp.clone();
        int[] e1 = e1Inp.clone();
        int l0 = (e0.length >> 1);
        int l1 = (e1.length >> 1);
        if (l1 == 0) { return e0; }
        if (l0 == 0) { return e1; }
        int[] pos = new int[N];
        int[] back = new int[N];
        int[] res1 = new int[N];
        int[] res2 = new int[N];
        if (MaxPositiveNumber < N) { newMaxPositiveNumbers(4 * N); }
        System.arraycopy(positiveNumbers, 0, pos, 0, N); // this is a track 0..N-1
        int iN = l1;
        for (int i = 0; i < l1; i++) {
            int ei = e1[i];
            pos[ei] = e1[iN];
            back[ei] = iN++;
        } // new value for e1-permut
        int m = 0; // resulting pointer
        iN = l0;
        for (int i = 0; i < l0; i++) {
            int s1 = e0[i];
            int s2 = e0[iN++];
            if (pos[s2] != s1) {
                res1[m] = s1;
                res2[m++] = pos[s2];
            }
            if (back[s2] > 0) { e1[back[s2]] = -1; }
        }
        iN = l1;
        for (int i = 0; i < l1; i++) {
            if (e1[iN] != -1) {
                res1[m] = e1[i];
                res2[m++] = e1[iN];
            }
            iN++;
        }
        int[] res = new int[2 * m];
        System.arraycopy(res1, 0, res, 0, m);
        System.arraycopy(res2, 0, res, m, m);
        return res;
    }

    public static int[] multPermutationsMoveUp0(int[] e0Inp, int[] e1Inp, int N) {
        int n = N >> 1;
        int[] e0 = e0Inp.clone();
        for (int i = 0; i < e0.length; i++) {
            e0[i] += n;
        }
        System.out.println(
            "e0,e1=======" + Array.toString(e0Inp) + Array.toString(e0) + Array.toString(e1Inp));
        return multPermutations(e0, e1Inp, N);
    }

    public static int[] transposePermutation(int[] Er) {
        int n = Er.length;
        int N = (n >> 1);
        int[] tr = new int[n];
        System.arraycopy(Er, 0, tr, N, N);
        System.arraycopy(Er, N, tr, 0, N);
        return tr;
    }

    /**
     * Multiplications of all permutations of One step.
     * For column permutation the order of blocks is: 0,1,2,3
     * For row permutation the order of blocks is: 0,2,1,3
     *
     * @param e  big block of size 2Nx2N
     * @param e0 block 0
     * @param e1 block 1
     * @param e2 block 2
     * @param e3 block 3
     * @param N  - blockSoze
     * @return
     */
    public static int[] permutationsOfOnestep(
        int[] e,
        int[] e00,
        int[] e01,
        int[] e10,
        int[] e11,
        int N
    ) {
        int[] ee0 = multPermutations(e00, e10, N);
        int l0 = ee0.length >> 1;
        int[] ee1 = multPermutations(e01, e11, N);
        int l1 = ee1.length >> 1;
        int[] ee = new int[2 * (l0 + l1)];
        for (int i = 0; i < ee1.length; i++) { ee1[i] += N; }
        System.arraycopy(ee0, 0, ee, 0, l0);
        System.arraycopy(ee1, 0, ee, l0, l1);
        System.arraycopy(ee0, l0, ee, l0 + l1, l0);
        System.arraycopy(ee1, l1, ee, 2 * l0 + l1, l1);
        return multPermutations(ee, e, N << 1);
    }

    public static MatrixS makeBlockDiagonalMatrix(MatrixS[][] Kl, int rowNumb) {
        //Ring ring = new Ring("C64[x]");
        int beg = 0;
        int shift = 0;
        Element[][] Mres = new Element[rowNumb][];
        int[][] colRes = new int[rowNumb][];
        int kx = 0, ky = 0;
        int i = 0;
        while (i < rowNumb) {
            System.out.println("000=" + beg + "  " + shift + "  " + kx + "  " + ky);
            System.out.println("i=" + i);
            // System.out.println("Kl" + i + "=" + Array.toString());
            int s = Kl[kx][ky].M.length;
            System.out.println("s=" + s);
            for (int j = 0; j < s; j++) {
                System.out.println("j=" + j + "beg=" + beg);
                Mres[beg + j] = Kl[kx][ky].M[j];
                System.out.println("Mres=" + beg + j + Array.toString(Mres[beg + j]));
                colRes[beg + j] = new int[Kl[kx][ky].col[j].length];
                for (int k = 0; k < Kl[kx][ky].col[j].length; k++) {
                    colRes[beg + j][k] = Kl[kx][ky].col[j][k] + shift;
                }
                i++;

            }
            beg += s;
            //System.out.println("BEG="+beg);
            shift += s;
            ky++;
            System.out.println("beg=" + beg + "  " + shift + "  " + kx + "  " + ky);
            if (Kl[kx].length == ky) {
                ky = 0;
                kx++;
                if (Kl.length == kx) {
                    System.out.println("tyui");
                    break;
                }
            }
            //System.out.println("shift="+shift);
        }
        return new MatrixS(Mres, colRes);
    }

    public static MatrixS zhordan(int n, Element a, Ring ring) {
        Element one = ring.numberONE;
        //if (a.isZero(ring)) {
        //    return null;
        //}
        Element[][] r = new Element[n][];
        for (int i = 0; i < n - 1; i++) {
            r[i] = new Element[2];
        }
        r[n - 1] = new Element[1];
        int[][] c = new int[n][];
        for (int i = 0; i < n - 1; i++) {
            c[i] = new int[2];
        }
        c[n - 1] = new int[1];
        for (int i = 0; i < n - 1; i++) {
            r[i][0] = a;
            r[i][1] = one;
            c[i][0] = i;
            c[i][1] = i + 1;
        }
        r[n - 1][0] = a;
        c[n - 1][0] = n - 1;
        return new MatrixS(n, n, r, c);
    }

    /**
     * Put the vector b at the place of column n
     *
     * @param b = vector
     * @param n = number of column
     * @return matrix this with column n which obtaned from vector b
     */
    public MatrixS append(VectorS b, int n) {
        int k = 0;
        for (int i = 0; i < size; i++) {
            putElement(b.V[k], i, n);
            k++;
        }
        return this;
    }

    public MatrixS append(VectorS b) {
        int n = size + 1;
        append(b, n);
        //size *= 2;
        return this;
    }

    /**
     * Multiply from right side by the permutation matrix E
     * Cтолбцы переезжают i-> j, i+1->j+1.., i+l-1->j+l-1. Остальные удаляются.
     *
     * @param I -- the first of rows in E
     * @param J -- the first of columns in E
     * @param L -- number of elements in E
     * @return result of multiplication = this*E
     */
    public MatrixS AmulE(int I, int J, int L) {
        if (L == 0) { return MatrixS.zeroMatrix(size); }
        int IL = I + L - 1;
        int delta = J - I;
        int Mlen = M.length;
        Element[][] MM = M.clone();
        int[][] cc = new int[col.length][0];
        for (int i = 0; i < col.length; i++) {
            cc[i] = col[i].clone();
            int[] ci = cc[i];
            Element[] MMi = MM[i];
            Element[] Mi = M[i];
            int new_i = 0;
            for (int j = 0; j < ci.length; j++) {
                int cj = ci[j];
                if ((cj >= I) && (cj <= IL)) {
                    ci[new_i] = cj + delta;
                    MMi[new_i++] = Mi[j];
                }
            }
            if (new_i != ci.length) {
                Element[] Mi_new = new Element[new_i];
                System.arraycopy(Mi, 0, Mi_new, 0, new_i);
                int[] ci_new = new int[new_i];
                System.arraycopy(ci, 0, ci_new, 0, new_i);
                MM[i] = Mi_new;
                cc[i] = ci_new;
            }
        }
        System.out.println("thisRes=" + new MatrixS(size, Math.max(colNumb, J + L), MM, cc));
        return new MatrixS(size, Math.max(colNumb, J + L), MM, cc);
    }

    /**
     * Cтолбцы переезжают i-> j, i+1->j+1.., i+l-1->j+l-1.
     * Остальные moveNumb сдвигаются на B и возвращаются во второй матрице.
     *
     * @param I        -- the first of rows in E
     * @param J        -- the first of columns in E
     * @param L        -- number of elements in E
     * @param B        - величина сдвига всех первых moveNumb из оставшихся столбцов влево.
     * @param moveNumb число сдвигаемых из оставшихся
     *                 Для L12: B=L;
     *                 Для L22: B=L+l_12+shiftRR; Если индекс остающегося элемента оказывается меньше нуля, то он удаляется.
     * @return две части входной матрицы: сдвинутая часть и оставшаяся часть, у которой moveNumb сдвинуты на B
     */


    public MatrixS[] AmulEDop(int I, int J, int L, int B, int moveNumb) {
        if (L == 0) {
            return new MatrixS[]{MatrixS.zeroMatrix(size), this.moveColumns(B, 0, moveNumb)};
        }
        int IL = I + L - 1;
        int delta = J - I;
        int collen = col.length;
        Element[][] MM = new Element[M.length][0];
        Element[][] MMD = new Element[M.length][0];
        int[][] cc = new int[collen][0];
        int[][] ccD = new int[collen][0];
        int BmoveNumb = B + moveNumb;
        for (int i = 0; i < collen; i++) {
            cc[i] = col[i].clone();
            int[] ci = cc[i];
            ccD[i] = col[i].clone();
            int[] ciD = ccD[i];
            Element[] Mi = M[i];
            Element[] MMi = new Element[Mi.length];
            Element[] MMiD = new Element[Mi.length];
            int new_i = 0;
            int new_iD = 0;
            for (int j = 0; j < ci.length; j++) {
                int cj = ci[j];
                if ((cj >= I) && (cj <= IL)) {
                    ci[new_i] = cj + delta;
                    MMi[new_i++] = Mi[j];
                } else {
                    if (cj - B >= 0) {
                        ciD[new_iD] = (cj < BmoveNumb) ? (cj - B) : cj;
                        MMiD[new_iD++] = Mi[j];
                    }
                }
            }
            if (new_i != ci.length) {
                Element[] Mi_new = new Element[new_i];
                System.arraycopy(MMi, 0, Mi_new, 0, new_i);
                int[] ci_new = new int[new_i];
                System.arraycopy(ci, 0, ci_new, 0, new_i);
                MM[i] = Mi_new;
                cc[i] = ci_new;
            } else { MM[i] = MMi; }
            if (new_iD != ci.length) {
                Element[] Mi_new = new Element[new_iD];
                System.arraycopy(MMiD, 0, Mi_new, 0, new_iD);
                int[] ci_new = new int[new_iD];
                System.arraycopy(ciD, 0, ci_new, 0, new_iD);
                MMD[i] = Mi_new;
                ccD[i] = ci_new;
            } else { MMD[i] = MMiD; }
        }
        return new MatrixS[]{new MatrixS(size, Math.max(colNumb, J + L), MM, cc),
                             new MatrixS(size, colNumb, MMD, ccD)};
    }

    /**
     * Multiply from left side by the permutation matrix E
     * Cтроки переезжают j-> i, j+1->i+1.., j+l-1->i+l-1. Остальные удаляются.
     *
     * @param i -- the first of rows in E
     * @param j -- the first of columns in E
     * @param l -- number of elements in E
     * @return result of multiplication = this*E
     */
    public MatrixS EmulA(int i, int j, int l) {
        if (M.length == 0) { return this; }
        if (l == 0) { return MatrixS.zeroMatrix(size); }
        int il = Math.max(i + l, size);
        Element[][] MM = new Element[il][0];
        int[][] cc = new int[il][0];
        System.arraycopy(M, j, MM, i, l);
        System.arraycopy(col, j, cc, i, l);
        return new MatrixS(size, colNumb, MM, cc);
    }

    public MatrixS EmulA(AdjEchelon m) {
        int i = m.I;
        int j = m.J;
        int l = m.rank;
        return EmulA(i, j, l);
    }

    public MatrixS ETmulA(AdjEchelon m) {
        int i = m.I;
        int j = m.J;
        int l = m.rank;
        return EmulA(j, i, l);
    }

    public MatrixS ImulA(AdjEchelon m) {
        int i = m.I;
        int l = m.rank;
        return EmulA(i, i, l);
    }

    public MatrixS barImulA(AdjEchelon m) {
        int i = m.I;
        int l = m.rank;
        int N = M.length;
        if (l >= N) { return MatrixS.zeroMatrix(size); }
        if (i == 0) { return EmulA(l, l, N - l); }
        if ((l == 0) || (i >= N)) { return this; }
        Element[][] MM = new Element[N][0];
        int[][] cc = new int[N][0];
        int il = i + l;
        System.arraycopy(M, 0, MM, 0, i);
        System.arraycopy(M, il, MM, il, N - il);
        System.arraycopy(col, 0, cc, 0, i);
        System.arraycopy(col, il, cc, il, N - il);
        return new MatrixS(size, colNumb, MM, cc);
    }

    /**
     * Вычисление ядра оператора:  Yij = -dij I + Eij^T Sij.
     * Причем в матрице Eij^T * Sij на диагональных позициях стоят числа d=dij,
     * Sij - входная матрица (this).  Процедура не параллелится!!!!!
     *
     * @param d  - диагональный элемент (dij)
     * @param Ei номера строк матрицы E
     * @param Ej номера столбцов матрицы E
     * @return Eij^T * S - d * I типа MatrixS
     */
    public MatrixS ES_min_dI(Element d, AdjEchelon m, Ring r) {
        if ((m.rank == 0) || (isZero(r))) { return scalarMatrix(size, d.negate(r), r); }
        if (size == 1) { return zeroMatrix(size); }
        MatrixS ES = ETmulA(m);//  multiplyLeftE(Ej, Ei);
        int n = ES.col.length;
        Element[][] MM = new Element[size][];
        int[][] cc = new int[size][];
        Element[] dd = new Element[]{d.negate(r)};
        for (int i = 0; i < n; i++) {
            Element[] Mi = ES.M[i];
            int[] ci = ES.col[i];
            int cil = ci.length;
            int m1 = cil - 1;
            if (cil == 0) {
                MM[i] = dd;
                cc[i] = new int[]{i};
            } else {
                Element[] temp = new Element[m1];
                MM[i] = temp;
                int[] temp_c = new int[m1];
                cc[i] = temp_c;
                int j = 0;
                while (ci[j] != i) { j++; }
                int m1j = m1 - j;
                System.arraycopy(Mi, 0, temp, 0, j);
                System.arraycopy(Mi, j + 1, temp, j, m1j);
                System.arraycopy(ci, 0, temp_c, 0, j);
                System.arraycopy(ci, j + 1, temp_c, j, m1j);
            }
        }
        for (int i = n; i < size; i++) {
            MM[i] = dd;
            cc[i] = new int[]{i};
        }
        return new MatrixS(size, size, MM, cc);
    }

    public MatrixS ETmulA(AELDU m) {
        int i = m.I;
        int j = m.J;
        int l = m.rank;
        return EmulA(j, i, l);
    }

    public MatrixS ImulA(AELDU m) {
        int i = m.I;
        int l = m.rank;
        return EmulA(i, i, l);
    }

    public MatrixS barImulA(AELDU m) {
        int i = m.I;
        int l = m.rank;
        int N = M.length;
        if (l >= N) { return MatrixS.zeroMatrix(size); }
        if (i == 0) { return EmulA(l, l, N - l); }
        if ((l == 0) || (i >= N)) { return this; }
        Element[][] MM = new Element[N][0];
        int[][] cc = new int[N][0];
        int il = i + l;
        System.arraycopy(M, 0, MM, 0, i);
        System.arraycopy(M, il, MM, il, N - il);
        System.arraycopy(col, 0, cc, 0, i);
        System.arraycopy(col, il, cc, il, N - il);
        return new MatrixS(size, colNumb, MM, cc);
    }

    public MatrixS ES_min_dI(Element d, AELDU m, Ring r) {
        if ((m.rank == 0) || (isZero(r))) { return scalarMatrix(size, d.negate(r), r); }
        if (size == 1) { return zeroMatrix(size); }
        MatrixS ES = ETmulA(m);
        int n = ES.col.length;
        Element[][] MM = new Element[size][];
        int[][] cc = new int[size][];
        Element[] dd = new Element[]{d.negate(r)};
        for (int i = 0; i < n; i++) {
            Element[] Mi = ES.M[i];
            int[] ci = ES.col[i];
            int cil = ci.length;
            int m1 = cil - 1;
            if (cil == 0) {
                MM[i] = dd;
                cc[i] = new int[]{i};
            } else {
                Element[] temp = new Element[m1];
                MM[i] = temp;
                int[] temp_c = new int[m1];
                cc[i] = temp_c;
                int j = 0;
                while (ci[j] != i) { j++; }
                int m1j = m1 - j;
                System.arraycopy(Mi, 0, temp, 0, j);
                System.arraycopy(Mi, j + 1, temp, j, m1j);
                System.arraycopy(ci, 0, temp_c, 0, j);
                System.arraycopy(ci, j + 1, temp_c, j, m1j);
            }
        }
        for (int i = n; i < size; i++) {
            MM[i] = dd;
            cc[i] = new int[]{i};
        }
        return new MatrixS(size, size, MM, cc);
    }


    /**
     * Insertm rows of matrix this into the matrix B.
     *
     * @param B           - target vatrix
     * @param firstInThis - first row in this matrix
     * @param firstInB-   first row in B matrix
     * @param number      - number of rows
     * @return new matrix B
     */
    public MatrixS insertRowsIn(MatrixS B, int firstInThis, int firstInB, int number) {
        if (number == 0) { return B; }
        for (int i = 0; i < number; i++) {
            B.col[firstInB] = col[firstInThis];
            B.M[firstInB++] = M[firstInThis++];
        }
        B.colNumb = Math.max(B.colNumb, colNumb);
        return B;
    }

    /**
     * To move rows of matrix .
     *
     * @param fromRow - the first row  for moving
     * @param toRow-  new position of the firswt row
     * @param number  - number of rows
     */
//  public MatrixS moveRows(int fromRow, int toRow, int number ){if(fromRow!=toRow)
//    for (int i = 0; i < number; i++) {col[toRow]=col[fromRow]; M[toRow++ ]=M[fromRow ];
//     col[fromRow]=emptyIntRow;  M[fromRow++]=emptyElementRow;
//    }return this;
//    }
    public MatrixS moveRows(int fromRow, int toRow, int number) {
        if ((fromRow != toRow) && (number > 0)) {
            //   System.out.println("VVVVV="+fromRow+toRow+number+this.size);
            System.arraycopy(M, fromRow, M, toRow, number);
            System.arraycopy(col, fromRow, col, toRow, number);
            if (fromRow < toRow) {
                int i = fromRow;
                while (i < Math.min(toRow, fromRow + number)) {
                    col[i] = emptyIntRow;
                    M[i++] = emptyElementRow;
                }
            } else {
                int i = fromRow + number - 1;
                while (i >= Math.max(toRow + number, fromRow)) {
                    col[i] = emptyIntRow;
                    M[i--] = emptyElementRow;
                }
            }
        }
        return this;
    }

    public static int[] emptyIntRow = new int[0];
    public static Element[] emptyElementRow = new Element[0];

    /**
     * To move columns of the matrix .
     *
     * @param fromCol - the first column  for moving
     * @param toCol-  new position of the first column
     * @param number  - number of columns
     */
    public MatrixS moveColumns(int fromCol, int toCol, int number) {
        int shift = toCol - fromCol;
        if ((number == 0) || (shift == 0)) { return this; }
        colNumb = Math.max(colNumb, toCol + number);
        int maxCol = fromCol + number;
        for (int j = 0; j < col.length; j++) {
            int[] cj = col[j];
            for (int i = 0; i < cj.length; i++) {
                if ((cj[i] >= fromCol) && (cj[i] < maxCol)) { cj[i] += shift; }
            }
        }
        return this;
    }

    /**
     * To move columns of the matrix .
     *
     * @param fromCol - the first column  for moving
     * @param toCol-  new position of the first column
     * @param number  - number of columns
     */
    public MatrixS cloneWithMoveColumns(int fromCol, int toCol, int number) {
        int shift = toCol - fromCol;
        if ((number == 0) || (shift == 0)) { return this; }
        int[][] c = new int[col.length][];
        int colNumb1 = Math.max(colNumb, toCol + number + 1);
        int maxCol = fromCol + number;
        for (int j = 0; j < col.length; j++) {
            int[] cj = col[j].clone();
            c[j] = cj;
            for (int i = 0; i < cj.length; i++) {
                if ((cj[i] >= fromCol) && (cj[i] <= maxCol)) { cj[i] += shift; }
            }
        }
        return new MatrixS(size, colNumb1, M, c);
    }

    /**
     * Bruhat decomposition in the domain
     *
     * @param n    -- size of square matrix
     * @param ring -- the commutative domain
     * @return three matrices {V,w,U} of matrixS-tipe:
     * where <B>V</B> and <B>U</B> is a triangular matrices in domain,
     * <B>w</B> is an inverse matrix for some diagonal matrix
     * in the domain mulpiplied by permutation matrix.
     * Each of the {L,D,U} matrix has the same rank as initial matrix
     */
    public MatrixS[] BruhatDecomposition(Ring ring) {
        //         int n = Math.max(size, colNumb);
//          int hb = Integer.highestOneBit(n);
//          if (hb != n) {hb <<= 1;}
//
        MatrixS[] yy = permutationOfRows(size).LDU(ring);
        MatrixS V = yy[0].permutationOfRows(size);
        V = V.permutationOfColumns(size);
        MatrixS w = yy[1].permutationOfRows(size);
        V.size = size;
        w.size = size;
        yy[2].size = size;
        return new MatrixS[]{V, w, yy[2]};
    }

    /**
     * LSU decomposition in the domain
     *
     * @param ring -- the commutative domain
     * @return three matrices {L,D,U} of matrixS-tipe:
     * where <B>L</B> and <B>U</B> is a triangular matrices in domain,
     * <B>D</B> is an inverse matrix for some diagonal matrix
     * in the domain mulpiplied by permutation matrix.
     * Each of the {L,D,U} matrix has the same rank as the initial matrix.
     */
    public MatrixS[] LDU(Ring ring) {
        int StartSize = (size);
        //  System.out.println("==================="+this.size+this.colNumb);
        MatrixS T = expandToPow2with0();
//    System.out.println("==================="+T.size+T.colNumb);
        AELDU x = new AELDU(T, ring.numberONE, 0, 0, ring);
        Element[] DDg = AELDU.GaussDiag(x.D, ring);
        int N = this.size;
        Element[][] Del = new Element[N][0];
        int[][] Dcolm = new int[N][0];
        for (int i = 0; i < x.rank; i++) {
            Del[i] = new Element[]{DDg[i]};
            Dcolm[i] = new int[]{i};
        }
        MatrixS d = new MatrixS(N, x.rank, Del, Dcolm);
        int[] EcT = MatrixS.transposePermutation(x.Ec);
        int[] ErT = MatrixS.transposePermutation(x.Er);

        MatrixS colPerm = MatrixS.scalarMatrix(N, ring.numberONE, ring).permutationOfRows(EcT);
        MatrixS rowPerm = MatrixS.scalarMatrix(N, ring.numberONE, ring).permutationOfRows(ErT);
        MatrixS L = x.L.permutationOfRows(ErT).permutationOfColumns(ErT);
        MatrixS U = x.U.permutationOfRows(EcT).permutationOfColumns(EcT);
        MatrixS D = d.permutationOfRows(ErT).permutationOfColumns(EcT);
        L.size = StartSize;
        D.size = StartSize;
        U.size = StartSize;
        colPerm.size = StartSize;
        rowPerm.size = StartSize;
        colPerm.colNumb = StartSize;
        rowPerm.colNumb = StartSize;
        int[][] lcol = new int[StartSize][];
        System.arraycopy(L.col, 0, lcol, 0, StartSize);
        L.col = lcol;
        int[][] dcol = new int[StartSize][];
        System.arraycopy(D.col, 0, dcol, 0, StartSize);
        D.col = dcol;
        int[][] ucol = new int[StartSize][];
        System.arraycopy(U.col, 0, ucol, 0, StartSize);
        U.col = ucol;
        int[][] ccol = new int[StartSize][];
        System.arraycopy(colPerm.col, 0, ccol, 0, StartSize);
        colPerm.col = ccol;
        int[][] rcol = new int[StartSize][];
        System.arraycopy(rowPerm.col, 0, rcol, 0, StartSize);
        rowPerm.col = rcol;
        return new MatrixS[]{L, D, U, rowPerm, colPerm};
    }

    public MatrixS[] LDUP(int minsize, Ring ring) throws MPIException {
        int StartSize = (size);
//    System.out.println("==================="+this.size+this.colNumb);
        MatrixS T = expandToPow2with0();
//    System.out.println("==================="+T.size+T.colNumb);
        AELDU x = new AELDU(T, ring.numberONE, 0, 0, minsize, ring);
        Element[] DDg = AELDU.GaussDiag(x.D, ring);
        int N = this.size;
        Element[][] Del = new Element[N][0];
        int[][] Dcolm = new int[N][0];
        for (int i = 0; i < x.rank; i++) {
            Del[i] = new Element[]{DDg[i]};
            Dcolm[i] = new int[]{i};
        }
        MatrixS d = new MatrixS(N, x.rank, Del, Dcolm);
        int[] EcT = MatrixS.transposePermutation(x.Ec);
        int[] ErT = MatrixS.transposePermutation(x.Er);
        MatrixS L = x.L.permutationOfRows(ErT).permutationOfColumns(ErT);
        MatrixS U = x.U.permutationOfRows(EcT).permutationOfColumns(EcT);
        MatrixS D = d.permutationOfRows(ErT).permutationOfColumns(EcT);
        L.size = StartSize;
        D.size = StartSize;
        U.size = StartSize;
        return new MatrixS[]{L, D, U};
    }

    @Override
    public MatrixS expand(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].expand(ring);
            }
        }
        return this;
    }

    @Override
    public Element integrate(int num, Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].integrate(num, ring);
            }
        }
        return this;
    }

    @Override
    public Element D(int num, Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].D(num, ring).expand(ring);
            }
        }
        return this;
    }

    @Override
    public boolean isComplex(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                if (M[i][j].isComplex(ring)) { return true; }
            }
        }
        return false;
    }


    @Override
    public Element factorLnExp(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].factorLnExp(ring);
            }
        }
        return this;
    }

    @Override
    public Element expandLn(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].expandLn(ring);
            }
        }
        return this;
    }

    @Override
    public Element factor(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].expandLn(ring);
            }
        }
        return this;
    }

    public int[] adamar(Ring r) {
        int nvars = r.varNames.length;
        int result[] = new int[nvars + 1];
        if (nvars > 0) {
            Element[] ci = new Element[this.size];
            int[] len = new int[this.size];
            int[] pow_row = new int[nvars];
            int[] pow = new int[nvars];
            int len_max = 0;
            for (int i = 0; i < this.size; i++) {
                ci[i] = r.numberZERO;
                len[i] = 0;
                for (int j = 0; j < this.M[i].length; j++) {
                    Polynom p = (Polynom) this.M[i][j];
                    if (len[i] < p.coeffs.length) {
                        len[i] = p.coeffs.length;
                        if (len[i] > len_max) { len_max = len[i]; }
                    }
                    Element max = r.numberZERO;
                    for (int k = 0; k < p.coeffs.length; k++) {
                        if (!max.compareTo(p.coeffs[k], 2, r)) { max = p.coeffs[k]; }
                        // if (k*nvars<p.powers.length){
                        int step = k * nvars + nvars;
                        if (p.powers.length < step) { step = p.powers.length; }
                        for (int pi = k * nvars; pi < (step); pi++) {
                            if (pow_row[pi % nvars] < p.powers[pi]) {
                                pow_row[pi % nvars] = p.powers[pi];
                            }
                        }
                    }
                    ci[i] = ci[i].add(max, r);
                }
                for (int pp = 0; pp < pow.length; pp++) {
                    pow[pp] = pow[pp] + pow_row[pp];
                    pow_row[pp] = 0;
                }
            }
            Element res = r.numberONE;
            Element tmp = r.numberONE;
            for (int i = 0; i < ci.length; i++) {
                tmp = ci[i].multiply(new NumberZ(len[i]), r);
                res = res.multiply(tmp, r);
            }
            res = res.divide(new NumberZ(len_max), r);
            res = res.abs(r);
            int bits = ((NumberZ) res).bitLength();
            //  System.out.println("bits="+bits);
            //  System.out.println("res="+bits/28);
            for (int i = 0; i < nvars; i++) {
                result[i] = pow[i];
                //System.out.println("pow="+pow[i]);
            }
            if (bits / 28 == 0) { result[result.length - 1] = 1; } else {
                result[result.length - 1] = bits / 28;
            }
//          for (int i=0; i<ci.length; i++) System.out.println("ci="+ci[i]);
//          for (int i=0; i<len.length; i++) System.out.println("len="+len[i]);
//          System.out.println("len_max="+len_max);
//          System.out.println("ii="+ii);
        } else {
        }
        return result;
    }

    public Element adamarNumberNotZero(Ring r) {
        if (0 == this.size || 0 == this.colNumb) {
            return r.numberZERO();
        } else {
            Element res = r.numberONE();
            for (Element[] rows : this.M) {
                switch (rows.length) {
                    case 0:
                        //                    return r.numberZERO();
                        break;
                    case 1:
                        res = res.multiply(rows[0].abs(r), r);
                        break;
                    default:
                        Element res_row = r.numberZERO();
                        for (Element row_element : rows) {
                            res_row = res_row.add(row_element.absSquare(r), r);
                        }
                        Element sqrt = res_row.toNumber(Ring.R64, Ring.ringR64xyzt)
                                              .sqrt(Ring.ringR64xyzt)
                                              .ceil(Ring.ringR64xyzt)
                                              .toNumber(r.algebraNumb, r);
                        res = res.multiply(sqrt, r);
                }
            }
            return res.multiply(new NumberZ("2"), r);
        }
    }

    public void deleteLastRow() {
        M[size - 1] = new Element[0];
        col[size - 1] = new int[0];
        size--;
    }

    /**
     * Вычисляет матрицу M = I + A + A^2 + ...
     *
     * @param ring
     * @return
     */
    public MatrixS closure(Ring ring) {
        int type = ring.algebra[0];
        MatrixD thiss = new MatrixD(this, ring, 0);
        switch (type) {
            case Ring.R64MaxPlus:
            case Ring.RMaxPlus:
            case Ring.ZMaxPlus:
            case Ring.R64MinPlus:
            case Ring.RMinPlus:
            case Ring.ZMinPlus:
            case Ring.R64MaxMult:
            case Ring.RMaxMult:
            case Ring.ZMaxMult:
            case Ring.R64MinMult:
            case Ring.RMinMult:
            case Ring.R64MaxMin:
            case Ring.RMaxMin:
            case Ring.ZMaxMin:
            case Ring.R64MinMax:
            case Ring.RMinMax:
            case Ring.ZMinMax:
                MatrixD rez = (MatrixD.ONE(M.length, ring)).add(thiss, ring);
                MatrixD A1 = thiss.copy();
                for (int i = 2; i < M.length; i++) {
                    A1 = thiss.multCU(A1, ring);
                    rez = rez.add(A1, ring);
                }
                return new MatrixS(rez, ring);
            default:
                MatrixD An = thiss.negate(ring);
                for (int i = 0; i < thiss.M.length; i++) {
                    An.M[i][i] = An.M[i][i].add(ring.numberONE, ring);
                }
                MatrixS As = new MatrixS(An, ring);
                As = As.inverse(ring);
                return As;
        }
    }

    /**
     * Maximum absolute value (\abs(a_{i,j})) of the all
     * matrix elements
     *
     * @param r ring
     * @return max_{this} abs(a_{i,j})
     */
    public Element max(Ring r) {
        int i = 0, n = M.length;
        //System.out.println("M[0].length " +M[0].length);
        Element max = new Element(0);
        for (int k = 0; k < n; k++) {
            if (M[k].length != 0) {
                max = M[k][0];
                break;
            }
            if (k == (n - 1) && M[k].length == 0) { return r.numberZERO; }
        }


        Element m;
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < M[j].length; k++) {
                m = M[j][k];
                if (m.abs(r).compareTo(max, r) == 1) {
                    max = m.abs(r);
                }
            }
        }
        return max;
    }
    // KORNYAK PART ------------------------------------------

    /**
     * Kornyak movement
     *
     * @return int[a, b] - new position of point A(i,j)
     */
    public static int[] permutActionOnPoint(int[] A, int perm[]) {
        int res[] = new int[2];
        int a = Math.min(A[0], A[1]);
        boolean firsIsMin = true;
        if (a != A[0]) { firsIsMin = !firsIsMin; }
        int b = Math.max(A[0], A[1]);
        int n = perm.length / 2;
        int i = 0;
        for (; i < n; i++) {
            if (a == perm[i]) {
                a = perm[i + n];
                break;
            }
        }
        for (; i < n; i++) {
            if (b == perm[i]) {
                b = perm[i + n];
                break;
            }
        }
        return (firsIsMin) ? new int[]{a, b} : new int[]{b, a};
    }


        /** Procedure  mod from Matrix. The interval= [0,1,..,s-1].

          *  Последовательная процедура.

          *  @param s число - mod

         *  @return this * dod(s)

          */

                public MatrixS mod(Element s, Ring ring) {

                int n = M.length;

                if (s.isZero(ring)) {return zeroMatrix(n);}

                Element[][] r = new Element[n][0];

                for (int i = 0; i < n; i++) {

                        Element[] Mi = M[i];

                        int m = Mi.length;

                        Element[] ri = new Element[m];

                        r[i] = ri;

                        for (int j = 0; j < m; j++) {ri[j] = Mi[j].mod(s, ring);}

                   }

                return new MatrixS(size, colNumb, r, col);

            }



            /** Procedure  Mod from Matrix. The interval= [-(s-1)/2,..0,..,(s-1)/2].

      *  Последовательная процедура.

     *  @param s число - mod

      *  @return this * dod(s)

      */

        public MatrixS Mod(Element s, Ring ring) {

            int n = M.length;

               if (s.isZero(ring)) {return zeroMatrix(n);}

                Element[][] r = new Element[n][0];

                for (int i = 0; i < n; i++) {

                        Element[] Mi = M[i];

                        int m = Mi.length;

                        Element[] ri = new Element[m];

                        r[i] = ri;

                        for (int j = 0; j < m; j++) {ri[j] = Mi[j].Mod(s, ring);}

                   }

                return new MatrixS(size, colNumb, r, col);

           }




    /**
     * Orbit of group.
     *
     * @param A     - initial point
     * @param perms - list of permutations
     * @return all points in the orbit
     */
    public static MatrixS orbit(int[] A, int[][] perms, int N, Ring ring) {
        int res[][] = new int[N * N][2];
        res[0] = A;
        int f1 = 0;
        int f2 = 0;
        MatrixS M = MatrixS.zeroMatrix(N);
        M.putElement(NumberZ.ONE, A[0], A[1]);
        while (f2 >= f1) {
            int i = res[f1][0];
            int j = res[f1][1];
            for (int k = 0; k < perms.length; k++) {
                int[] New = permutActionOnPoint(res[f1], perms[k]);
                int I = New[0];
                int J = New[1];
                Element e = M.getElement(I, J, ring);
                if (e.isZero(ring)) {
                    M.putElement(NumberZ.ONE, I, J);
                    res[++f2] = New;
                }
            }
            f1++;
        }
        return M;
    }

    public static MatrixS[] orbits(int[][] perms, int N, Ring ring) {
        int maxVN = N * N;
        MatrixS[] listM = new MatrixS[maxVN];
        int k = 0; // matix numb
        MatrixS Sum = MatrixS.zeroMatrix(N);
        int i = 0;
        int j = 0;
        while (i < N) {
            int[] A = new int[]{i, j};
            MatrixS M = orbit(A, perms, N, ring);
            listM[k++] = M;
            Sum = M.add(Sum, ring);
            j++;
            if (j == N) {
                j = 0;
                i++;
            }
            while ((i < N) && (!(Sum.getElement(i, j, ring)).isZero(ring))) {
                j++;
                if (j == N) {
                    j = 0;
                    i++;
                }
            }
        }
        MatrixS[] vectM = new MatrixS[k];
        System.arraycopy(listM, 0, vectM, 0, k);
        return vectM;
    }

    /**
     * Case of Vector[] in the input data of permutations
     *
     * @param perms Vector[] - permutations
     * @param N     - matrix size
     * @param ring1 array with one ring, which must be returned
     * @return kornyakMatrix of NхN
     */
    public static MatrixS kornyakMatrix(VectorS[] perms, int N, Ring[] ring1) {
        int[][] perm = new int[perms.length][];
        for (int i = 0; i < perms.length; i++) {
            int k = perms[i].V.length;
            perm[i] = new int[k];
            for (int j = 0; j < k; j++) {
                perm[i][j] = perms[i].V[j].intValue();
            }
        }
        return kornyakMatrix(perm, N, ring1);
    }

    public static MatrixS kornyakMatrix(int[][] perms, int N, Ring[] ring1) {
        Ring ring = new Ring("Z[]");
        MatrixS[] vectM = orbits(perms, N, ring);
        int v = vectM.length - 1;
        String[] varNames = new String[v];
        char a = 'a';
        for (int i = 0; i < v; i++) {
            char b = (char) (i + a);
            varNames[i] = "" + b;
        }
        ring = new Ring(new int[]{1}, new int[]{v - 1}, varNames);
        ring1[0] = ring;
        MatrixS Kor = vectM[0];
        for (int i = 0; i < v; i++) {
            Kor = vectM[i + 1].multiplyByNumber(ring.varPolynom[i], ring).add(Kor, ring);
        }
        return Kor;
    }


    /**
     * autor Kryuchkov Alexey
     * замена местеми k и l столбцы
     *
     * @param matrix
     * @param k
     * @param l
     * @return
     */
    public void permutationOfColumns(int k, int l) {

        Element temp;
        for (int i = 0; i < size; i++) {
            temp = M[i][k];
            M[i][k] = M[i][l];
            M[i][l] = temp;
        }
    }

    /**
     * author Kryuchkov Alexey
     * замена местеми k и l строки
     *
     * @param matrix
     * @param k
     * @param l
     * @return
     */
    public void permutationOfRows(int k, int l) {
        Element[] temp = M[k];
        M[k] = M[l];
        M[l] = temp;
    }

    public int adamarBitCount(Ring ring) {
        NumberZ num = (NumberZ) adamarSquareNumber(ring);
        return (num.bitCount() + 1) / 2 + 1;
    }

//    public NumberZ adamarNumber(Ring ring) {
//        NumberR res = adamarSquareNumber(ring);
//        return (NumberZ) res.sqrt(ring).toNumber(Ring.Z, ring);
//
//    }

    public Element adamarSquareNumber(Ring ring) {
        Element res = ring.numberONE;
        for (Element[] ds : M) {
            VectorS vecS = new VectorS(ds);
            res = res.multiply(vecS.innerSquare(ring), ring);
        }
        return res;
    }

    public MatrixS deleteZeroColumns(int[][] notZeroClms, Ring ring) {
        int[] newCol = Array.jointListsOfInts(col, colNumb);
        newCol = Array.sortUp(newCol);
        notZeroClms[0] = newCol;
        int n = col.length;
        int[] pos = new int[colNumb];
        for (int i = 0; i < newCol.length; i++) { pos[newCol[i]] = i; }
        int[][] c = new int[n][];
        for (int i = 0; i < n; i++) {
            int[] r = col[i];
            int[] row = new int[r.length];
            for (int j = 0; j < r.length; j++) { row[j] = pos[r[j]]; }
            c[i] = row;
        }
        return new MatrixS(size, colNumb, M, c);
    }
 /** Умножение прямоугольных матриц с сохранением упорядоченных столбцов. 
     * Cтолбцы во всех строках результвта будут отсортированы по возрастанию.
     * Не параллельный вариант (для листового блока). В случае общей памяти 
     * легко параллелится по каждой строке.  Дополнительные затраты сводятся к
     * сортировке массива, длина которого равна числу треков возрастания столбцов, и плюс
     * переписывание (слиянием) всех элементов.
     *  Число столбцов в матрице b должно быть обязательно указано в параметре b.colNumb.
     *  @param b матрица-сомножитель
     *  @return  this * b
     */
    public MatrixS multiplySorted(MatrixS b, Ring ring) { //p(col.length+" this="+this.toString()); p(b.col.length+" b="+b.toString());
        int n = col.length;
        int N = b.col.length;
        int m=b.colNumb;
        if ((N == 0) || (n == 0)) {return zeroMatrix(size);}
        int[] border= new int[N]; // position of the last element in each track-row in current row
        Element[][] sumM = new Element[n][]; // new M
        int[][] sumC = new int[n][]; // new col
        if (ring.flags.length < m) {
            ring.flags = new int[m];
            for (int i = 0; i < m; i++) {
                ring.flags[i] = -1;
            }
         } //flags of elems
        int[] Cb, Ca;
        Element[] Ma, Mb;
        Element aa;
        int k = 0;
        int nb = 0;
        for (int i = 0; i < n; i++) { //n=A.M.length sumC[i] = CC1; sumM[i] = MM1;
            int lastP=0; // counter for tracks
            Ma = M[i];
            Ca = col[i];
            int na = (Ca!=null)? Ca.length: 0;
            int j = 0; // a-row index
            while ((j < na) && (
                    ((k = Ca[j]) >= N)||
                     (b.M[Ca[j]].length==0)
                       )){j++;} // пропускаем высокие номера столбцов в А так как 0-строки в В
            if (na == j) {sumC[i] = new int[0]; sumM[i] = new Element[0]; } // i-row of product is empty
            else { k = Ca[j];
                aa = Ma[j]; // main element in row A
                Mb = b.M[k];
                Cb = b.col[k]; // main row in B
                nb = Mb.length; // number of elements in B-row
                if (na - 1 == j) { //  only one element at the i-row of this matrix A
                    sumC[i] = Cb;
                    if (aa.isOne(ring)) { sumM[i] = Mb;}
                    else { Element[] MM = new Element[nb];
                            sumM[i] = MM;
                        for (int p = 0; p < nb; p++) {MM[p] = aa.multiply(Mb[p], ring);}
                    }
                } else { // more then 1 element in A row
                    int[] CCn = new int[m];  // more then one element at the i-row of this matrix
                    Element[] MMn = new Element[m]; // m= the biggest nubmer of column in B
                    System.arraycopy(Cb, 0, CCn, 0, nb);
                    border[0]=nb; //  first track has nb elements
                    for (int p = 0; p < nb; p++) {
                        ring.flags[Cb[p]] = p;
                        MMn[p] = aa.multiply(Mb[p], ring);
                    } //first row
                    for (j++; j < na; j++) {
                        if (((k = Ca[j]) < N)&&(b.M[k].length>0)) { // other element corresponding to zero-rows in B
                            aa = Ma[j];
                            k = Ca[j];
                            Mb = b.M[k];
                            Cb = b.col[k];
                            int nb1 = Mb.length;
                            for (int s = 0; s < nb1; s++) { // run in the B row
                                int Cbs = Cb[s];
                                int ss = ring.flags[Cbs];
                                if (ss == -1) {
                                    ring.flags[Cbs] = nb;
                                    CCn[nb] = Cbs;
                                    MMn[nb++] = aa.multiply(Mb[s], ring);
                                } else {
                                    MMn[ss] = MMn[ss].add(aa.multiply(Mb[s], ring), ring);
                                }
                            }  if(nb> border[lastP]){lastP++; border[lastP]=nb;} // new border
                        }
                    }
                    int tracNumb=lastP+1; // Полное число треков
                    int[] pointer= new int[tracNumb]; pointer[0]=0; // первый трек начинается с 0
                    int[] fCols =new int[tracNumb]; fCols[0]=CCn[0];
                    for (int p=1;p<tracNumb;p++){pointer[p]=border[p-1]; fCols[p]=CCn[pointer[p]];}
                        // initial positions of pointers (border of i-track is equals pointer to the  i+1-track)
                    int[] order=Array.sortPosUp(fCols); // номера столбцов в начале треков сортируются (это единственная сортировка)
                    int[] CCnS = new int[nb];  // sorted -- сюда делаем merdge
                    Element[] MMnS = new Element[nb]; // sorted -- сюда делаем merdge
                    int start=0; // номер первого трека
                    int nw=0;
                    int ns=0;
                    while(start+1<tracNumb) { // Пока текущий трек не является последним
                        ns = pointer[order[start]]; // младший элемент в младшем треке
                        int ns1 = pointer[order[start + 1]]; // младший элемент в следующем треке
                        if (!MMn[ns].isZero(ring)) {MMnS[nw] = MMn[ns]; CCnS[nw++] = CCn[ns];}
                        ring.flags[CCn[ns++]] = -1;
                        int my_border=border[order[start]];
                        if (ns == my_border) {start++;} // закончился трек. увеличиваем start
                        else{     
                                while (( ns  < my_border)&&(CCn[ns] < CCn[ns1])) {
                                    if ((MMn[ns]!=null)&&(!MMn[ns].isZero(ring))) {MMnS[nw] = MMn[ns]; CCnS[nw++] = CCn[ns];}
                                    ring.flags[CCn[ns++]] = -1;
                                }   // бежим внутри трека
                                if (ns == my_border) { start++; } // закончился трек. увеличиваем start
                                else {
                                    pointer[order[start]] = ns;   // меняем положение пойнтера
                                    int next = start + 2;         // меняем позицию всего трека
                                    while ((next < tracNumb) && (CCn[ns] > CCn[pointer[order[next]]])) {next++;}
                                    // нашли для старого трека новую позицию и вставляем его в новое место
                                    int temp = order[start];
                                    System.arraycopy(order, start + 1, order, start, next - start - 1);
                                    order[next - 1] = temp;
                                }
                            }
                        }
                    ns=pointer[order[start]]; // перепишем последний трек
                    while (( ns < border[order[start]])&&(nw<nb)) {
                        if(!MMn[ns].isZero(ring)) {MMnS[nw] = MMn[ns]; CCnS[nw++] = CCn[ns]; } 
                        ring.flags[CCn[ns++]] = -1;  
                    }
                    if (nb == nw) {sumC[i] = CCnS; sumM[i] = MMnS;
                    } else {
                        int[] CC1 = new int[nw];
                        Element[] MM1 = new Element[nw]; //так как nw ненулей
                        System.arraycopy(CCnS, 0, CC1, 0, nw);
                        System.arraycopy(MMnS, 0, MM1, 0, nw);
                        sumC[i] = CC1;
                        sumM[i] = MM1;
                    } 
                }
            }
        }
        return new MatrixS(size, m, sumM, sumC);
    }

    /**
     * Сумма матриц  с упорялоченными столбцами.
     * @param b матрица слагаемое
     * @param ring
     * @return Сумма матриц this and b
     */
    public MatrixS addSorted(MatrixS b, Ring ring) {
        int colN = Math.max(colNumb, b.colNumb);
        int n = Math.min(col.length, b.col.length);
        int n_length = Math.max(col.length, b.col.length);
        Element[][] sumM = new Element[n_length][]; // new M
        int[][] sumC = new int[n_length][]; // new col
        for (int i = 0; i < n; i++) {
            int ni=col[i].length;
            int bni=b.col[i].length;
            if(ni==0){ sumM[i]=b.M[i]; sumC[i]=b.col[i];}
            else if (bni==0){sumM[i]=M[i]; sumC[i]=col[i];}
            else {int nn=ni+bni; int[][] newC=new int[1][nn];
                  sumM[i]=Array.addSortedRows(M[i],b.M[i],col[i],b.col[i], newC, ring);
                  int m=sumM[i].length;
                  if (m<nn){ int[] newC1=new int[m];; 
                     System.arraycopy(newC[0], 0, newC1, 0, m); sumC[i]=newC1;}
                  else sumC[i]=newC[0];
           }}
         if(col.length>b.col.length){for (int i = n; i < col.length; i++) {
                 sumM[i]=M[i]; sumC[i]=col[i];}}
         else {if (col.length<b.col.length){for (int i = n; i < b.col.length; i++) {
                 sumM[i]=b.M[i]; sumC[i]=b.col[i];}}}
         return new MatrixS(n_length, colN, sumM, sumC);
         }
     
         /**
     * Сумма матриц  с упорялоченными столбцами.
     * @param b матрица слагаемое
     * @param ring
     * @return Сумма матриц this and b
     */
    public MatrixS subtractSorted(MatrixS b, Ring ring) {
        int colN = Math.max(colNumb, b.colNumb);
        int n = Math.min(col.length, b.col.length);
        int n_length = Math.max(col.length, b.col.length);
        Element[][] sumM = new Element[n_length][]; // new M
        int[][] sumC = new int[n_length][]; // new col
        for (int i = 0; i < n; i++) {
            int ni=col[i].length;
            int bni=b.col[i].length;
            if(bni==0){sumM[i]=M[i]; sumC[i]=col[i];}
            else if (bni==0){sumM[i]=new Element[b.M.length]; sumC[i]=b.col[i];
                for (int j = 0; j < b.M.length; j++) {
                    sumM[i][j]=M[i][j].negate(ring);
                }
}
            else {  int[][] newC=new int[1][ ];
                  sumM[i]=Array.subtractSortedRows(M[i],b.M[i],col[i],b.col[i], newC, ring);
                  sumC[i]=newC[0];
             }
        }
        if(col.length>b.col.length){for (int i = n; i < col.length; i++) {
            sumM[i]=M[i]; sumC[i]=col[i];}}
        else {if (col.length<b.col.length){for (int i = n; i < b.col.length; i++) {
            Element[] tmp= new Element[b.M[i].length]; 
            for (int j = 0; j < b.M[i].length; j++) {
                tmp[j]=b.M[i][j].negate(ring);
            }  sumC[i]=b.col[i];sumM[i]=tmp;
        }}}
        return new MatrixS(n_length, colN, sumM, sumC);
        }

    /**  Get number of non-zero Elements in this matrix
     * @return long  - number of non-zero Elements
     */
    public  long getNumberOfElements() {
        long res=0;
        for (int j =0; j < col.length; j++) {res+= col[j].length;}
        return res;
    }

    /**  Is this matrix has leaf size?
     * (1 - Yes  0 - No (0= it is larger than leaf)
     * @return int  - 1 - Yes  0 - No
     */

    public  boolean isItLeaf(int leafSize, double leafdensity) {
        //System.out.println("leafdensity = " + leafdensity);
        //System.out.println("leafSize = " + leafSize);
        if (leafSize>=size) return true; // little size

        long elNumb =getNumberOfElements();
        long lsls =  ((long)leafSize)*leafSize;  if (elNumb>=lsls) return false; // many non-zero elements
        double cc = (double)size; cc= (colSize==-1)?  cc*cc: cc*(double)colSize;
        double dens= ((elNumb==0)||(cc==0)) ? 0.0 :  ((double)elNumb)/cc ;
        return (dens <= leafdensity)? true: false;   // if little density than it is a leaf
    }

public MatrixS[] choleskyFactorize(Ring ring) {
      
       if (size == 1) {
            Element value = this.getElement(0, 0, ring);
            if(value.isNegative()||value.isZero(ring)) 
               {return new MatrixS[]
               {MatrixS.zeroMatrix(1),   MatrixS.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixS[]{ new MatrixS(sqrtVal),
                       new MatrixS(ring.numberONE.divide(sqrtVal,ring))};}
        } else {
         //  System.out.println("O input = "+ inputMatrix);
            MatrixS[] inputMatrixBlocks = this.split(); //(alpha, beta, beta , gamma)
            MatrixS[] decomposedA = inputMatrixBlocks[0].choleskyFactorize(ring);
            MatrixS bT = decomposedA[1].multiply(inputMatrixBlocks[1], ring);
        //   System.out.println("O bT = "+ bT);
            MatrixS b = bT.transpose(bT.size);
        //   System.out.println("O b = "+ b);
          // System.out.println("O inputMatrixBlocks[3] = "+ inputMatrixBlocks[3]);
         //  System.out.println("O b.multiplySorted(bT, ring) = "+ b.multiplySorted(bT, ring));
            MatrixS beta = inputMatrixBlocks[3].subtract(b.multiply(bT, ring), ring);
        //   System.out.println("O beta = "+ beta.toString(ring));
            MatrixS[] decomposedC = beta.choleskyFactorize(ring);
            MatrixS z = decomposedC[1].multiply(b, ring)
                    .multiply(decomposedA[1], ring).negate(ring);
          // System.out.println("O z = "+ z);
            MatrixS[] LBlocks = new MatrixS[4];
            MatrixS[] LinvBlocks = new MatrixS[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

            MatrixS L = MatrixS.join(LBlocks);
            MatrixS Linv = MatrixS.join(LinvBlocks);
            return new MatrixS[]{L, Linv};
        }
    }


    public MatrixS[] choleskyFactorize4(Ring ring) {

        if (size == 1) {
            Element value = this.getElement(0, 0, ring);
            if(value.isNegative()||value.isZero(ring))
            {return new MatrixS[]
                    {MatrixS.zeroMatrix(1),   MatrixS.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixS[]{ new MatrixS(sqrtVal),
                        new MatrixS(ring.numberONE.divide(sqrtVal,ring))};}
        } else {
            //  System.out.println("O input = "+ inputMatrix);
            MatrixS[] inputMatrixBlocks = this.split(); //(alpha, beta, beta , gamma)
            MatrixS[] decomposedA = inputMatrixBlocks[0].choleskyFactorize4(ring);
            MatrixS bT = decomposedA[1].multiplyRecursive(inputMatrixBlocks[1], ring);
            //   System.out.println("O bT = "+ bT);
            MatrixS b = bT.transpose(bT.size);
            //   System.out.println("O b = "+ b);
            // System.out.println("O inputMatrixBlocks[3] = "+ inputMatrixBlocks[3]);
            //  System.out.println("O b.multiplySorted(bT, ring) = "+ b.multiplySorted(bT, ring));
            MatrixS beta = inputMatrixBlocks[3].subtract(b.multiplyRecursive(bT, ring), ring);
            //   System.out.println("O beta = "+ beta.toString(ring));
            MatrixS[] decomposedC = beta.choleskyFactorize4(ring);
            MatrixS z = decomposedC[1].multiplyRecursive(b, ring).multiplyRecursive(decomposedA[1], ring).negate(ring);
            // System.out.println("O z = "+ z);
            MatrixS[] LBlocks = new MatrixS[4];
            MatrixS[] LinvBlocks = new MatrixS[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

            MatrixS L = MatrixS.join(LBlocks);
            MatrixS Linv = MatrixS.join(LinvBlocks);
            return new MatrixS[]{L, Linv};
        }
    }

    public MatrixS[] choleskyFactorize8(Ring ring) {

        if (size == 1) {
            Element value = this.getElement(0, 0, ring);
            if(value.isNegative()||value.isZero(ring))
            {return new MatrixS[]
                    {MatrixS.zeroMatrix(1),   MatrixS.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixS[]{ new MatrixS(sqrtVal),
                        new MatrixS(ring.numberONE.divide(sqrtVal,ring))};}
        } else {
            //  System.out.println("O input = "+ inputMatrix);
            MatrixS[] inputMatrixBlocks = this.split(); //(alpha, beta, beta , gamma)
            MatrixS[] decomposedA = inputMatrixBlocks[0].choleskyFactorize8(ring);
            MatrixS bT = decomposedA[1].multiply8(inputMatrixBlocks[1], ring);
              // System.out.println("bT = "+ bT);
            MatrixS b = bT.transpose(bT.size);
            // System.out.println("O inputMatrixBlocks[3] = "+ inputMatrixBlocks[3]);
            //  System.out.println("O b.multiplySorted(bT, ring) = "+ b.multiplySorted(bT, ring));
            MatrixS beta = inputMatrixBlocks[3].subtract(b.multiply8(bT, ring), ring);
            //   System.out.println("O beta = "+ beta.toString(ring));
            MatrixS[] decomposedC = beta.choleskyFactorize8(ring);
            MatrixS z = decomposedC[1].multiply8(b, ring).multiply8(decomposedA[1], ring).negate(ring);
            // System.out.println("O z = "+ z);
            MatrixS[] LBlocks = new MatrixS[4];
            MatrixS[] LinvBlocks = new MatrixS[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

            MatrixS L = MatrixS.join(LBlocks);
            MatrixS Linv = MatrixS.join(LinvBlocks);
            return new MatrixS[]{L, Linv};
        }
    }


    public MatrixS[] choleskyFactorizeWin(Ring ring) {
        if (size == 1) {
            Element value = this.getElement(0, 0, ring);
            if(value.isNegative()||value.isZero(ring))
            {return new MatrixS[]
                    {MatrixS.zeroMatrix(1),   MatrixS.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixS[]{ new MatrixS(sqrtVal),
                        new MatrixS(ring.numberONE.divide(sqrtVal,ring))};}
        } else {
            //  System.out.println("O input = "+ inputMatrix);
            MatrixS[] inputMatrixBlocks = this.split(); //(alpha, beta, beta , gamma)
            MatrixS[] decomposedA = inputMatrixBlocks[0].choleskyFactorizeWin(ring);
            MatrixS bT = decomposedA[1].multiplyStrassWin(inputMatrixBlocks[1], ring);

            //System.out.println("bT = "+ bT);
            MatrixS b = bT.transpose(bT.size);
          //  System.out.println("b = "+ b);

            // System.out.println("O inputMatrixBlocks[3] = "+ inputMatrixBlocks[3]);
            //  System.out.println("O b.multiplySorted(bT, ring) = "+ b.multiplySorted(bT, ring));
            MatrixS beta = inputMatrixBlocks[3].subtract(b.multiplyStrassWin(bT, ring), ring);
            //   System.out.println("O beta = "+ beta.toString(ring));
            MatrixS[] decomposedC = beta.choleskyFactorizeWin(ring);
            MatrixS z = decomposedC[1].multiplyStrassWin(b, ring).multiplyStrassWin(decomposedA[1], ring).negate(ring);
            // System.out.println("O z = "+ z);
            MatrixS[] LBlocks = new MatrixS[4];
            MatrixS[] LinvBlocks = new MatrixS[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] = MatrixS.zeroMatrix(decomposedA[0].size);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

           // System.out.println("decomposedA[0].size = " + decomposedA[0].size);

            MatrixS L = MatrixS.join(LBlocks);
            MatrixS Linv = MatrixS.join(LinvBlocks);
            return new MatrixS[]{L, Linv};
        }
    }

    /**
     * Matrix norm, which is maximal of absolute value of all elements.
     * @param ring  Ring
     * @return Matrix norm
     */
    public Element normMaxAbs( Ring ring) {
        Element norm=ring.numberZERO; Element tmp;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
              tmp=M[i][j].abs(ring);
              if (norm.subtract(tmp, ring).isNegative()) norm=tmp;
        }} return norm;
    }

/** Inverse of invertible low triangular matrix of size 2^n, n>0.
 * 
 * @param ring Ring
 * @return inverse of matrix
 */

public   MatrixS inverseLowTriangle(  Ring ring) {
    if (size == 2) {
        Element a= ring.numberONE.divide(M[0][0], ring);
        Element b=   M[1][0] ;
        if(col[1].length==2){ Element c= M[1][1];Element d;
            if(col[1][1]==1){ d=ring.numberONE.divide(c, ring);}
            else{d=ring.numberONE.divide(b, ring);b=c;}
            b=d.multiply(b, ring).multiply(a, ring).negate(ring);
            return new MatrixS(2, 2, new Element[][]{{a},{b,d}}, new int[][]{{0},{0,1}});
        }else { b=ring.numberONE.divide(b, ring);
            return new MatrixS(2,2,new Element[][]{{a},{b}}, new int[][]{{0},{1}});
        }
    }// end of size 2
    else{
        MatrixS[] blocks = this.split();
        MatrixS inv0= blocks[0].inverseLowTriangle( ring);
        MatrixS inv4= blocks[3].inverseLowTriangle( ring);
        MatrixS inv3 = inv4.multiply(blocks[2], ring).multiply(inv0, ring).negate(ring);
        return MatrixS.join(new MatrixS[]{inv0, blocks[1],inv3,inv4}) ;
    }
}
    public   MatrixS inverseLowTriangle4(  Ring ring) {
     if (size == 2) { 
       Element a= ring.numberONE.divide(M[0][0], ring);
       Element b=   M[1][0] ;
       if(col[1].length==2){ Element c= M[1][1];Element d;
           if(col[1][1]==1){ d=ring.numberONE.divide(c, ring);}
           else{d=ring.numberONE.divide(b, ring);b=c;}
           b=d.multiply(b.multiply(a, ring), ring).negate(ring);
           return new MatrixS(2, 2, new Element[][]{{a},{b,d}}, new int[][]{{0},{0,1}});
         }else { b=ring.numberONE.divide(b, ring);
           return new MatrixS(2,2,new Element[][]{{a},{b}}, new int[][]{{0},{1}});
         }
         }// end of size 2
     else{
        MatrixS[] blocks = this.split();
        MatrixS inv0= blocks[0].inverseLowTriangle4( ring);
        MatrixS inv4= blocks[3].inverseLowTriangle4( ring);
        MatrixS inv3 = inv4.multiplyRecursive(blocks[2], ring).multiplyRecursive(inv0, ring).negate(ring);
        return MatrixS.join(new MatrixS[]{inv0, blocks[1],inv3,inv4}) ;
    }    
    }

    public   MatrixS inverseLowTriangle8(  Ring ring) {
        if (size == 2) {
            Element a= ring.numberONE.divide(M[0][0], ring);
            Element b=   M[1][0] ;
            if(col[1].length==2){ Element c= M[1][1];Element d;
                if(col[1][1]==1){ d=ring.numberONE.divide(c, ring);}
                else{d=ring.numberONE.divide(b, ring);b=c;}
                b=d.multiply(b.multiply(a, ring), ring).negate(ring);
                return new MatrixS(2, 2, new Element[][]{{a},{b,d}}, new int[][]{{0},{0,1}});
            }else { b=ring.numberONE.divide(b, ring);
                return new MatrixS(2,2,new Element[][]{{a},{b}}, new int[][]{{0},{1}});
            }
        }// end of size 2
        else{
            MatrixS[] blocks = this.split();
            MatrixS inv0= blocks[0].inverseLowTriangle8( ring);
            MatrixS inv4= blocks[3].inverseLowTriangle8( ring);
            MatrixS inv3 = inv4.multiply8(blocks[2], ring).multiply8(inv0, ring).negate(ring);
            return MatrixS.join(new MatrixS[]{inv0, blocks[1],inv3,inv4}) ;
        }
    }
    public  MatrixS inverseLowTriangleWin(  Ring ring) {
        if (size == 2) {
            Element a= ring.numberONE.divide(M[0][0], ring);
            Element b=   M[1][0] ;
            if(col[1].length==2){ Element c= M[1][1];Element d;
                if(col[1][1]==1){ d=ring.numberONE.divide(c, ring);}
                else{d=ring.numberONE.divide(b, ring);b=c;}
                b=d.multiply(b.multiply(a, ring), ring).negate(ring);
                return new MatrixS(2, 2, new Element[][]{{a},{b,d}}, new int[][]{{0},{0,1}});
            }else { b=ring.numberONE.divide(b, ring);
                return new MatrixS(2,2,new Element[][]{{a},{b}}, new int[][]{{0},{1}});
            }
        }// end of size 2
        else{
            MatrixS[] blocks = this.split();
            MatrixS inv0= blocks[0].inverseLowTriangleWin( ring);
            MatrixS inv4= blocks[3].inverseLowTriangleWin( ring);
            MatrixS inv3 = inv4.multiplyStrassWin(blocks[2], ring).multiplyStrassWin(inv0, ring).negate(ring);
            return MatrixS.join(new MatrixS[]{inv0, blocks[1],inv3,inv4}) ;
        }
    }

    public MatrixS multiplyScalar(MatrixS A, MatrixS B, MatrixS C, MatrixS D, Ring ring) {
        if(A.size <= ring.SMALLESTBLOCK) return (A.multiplySmallBlock(B, ring)).
                add(C.multiplySmallBlock(D, ring), ring);
        return A.multiplyRecursive(B, ring).add(C.multiplyRecursive(D, ring), ring);
    }

    public MatrixS multiply8(MatrixS B, Ring ring){
        if(size <= ring.SMALLESTBLOCK) return this.multiplySmallBlock(B, ring);

        MatrixS[] AA = this.split();
        MatrixS[] BB = B.split();

        MatrixS[] DD = new MatrixS[4];
        DD[0] = AA[0].multiply8(BB[0], ring).add(AA[1].multiply8(BB[2], ring), ring);
        DD[1] = AA[0].multiply8(BB[1], ring).add(AA[1].multiply8(BB[3], ring), ring);
        DD[2] = AA[2].multiply8(BB[0], ring).add(AA[3].multiply8(BB[2], ring), ring);
        DD[3] = AA[2].multiply8(BB[1], ring).add(AA[3].multiply8(BB[3], ring), ring);

        return MatrixS.join(DD);
    }

    public MatrixS multiplyStrassWin(MatrixS b, Ring r) {

   // System.out.println("a = " + a + " b = " + b);
        if (size == 1) {
            Element[][] res = new Element[size][size];
            res[0][0] = this.getElement(0, 0, r).multiply(b.getElement(0, 0, r), r);
            return new MatrixS(res, r);
        }
        if (size== 2) {
            Element[][] res = new Element[size][size];
            res[0][0] = this.getElement(0, 0, r).multiply(b.getElement(0, 0, r), r).add(this.getElement(0, 1, r).multiply(b.getElement(1, 0, r), r), r);
            res[0][1] = this.getElement(0, 0, r).multiply(b.getElement(0, 1, r), r).add(this.getElement(0, 1, r).multiply(b.getElement(1, 1, r), r), r);
            res[1][0] = this.getElement(1, 0, r).multiply(b.getElement(0, 0, r), r).add(this.getElement(1, 1, r).multiply(b.getElement(1, 0, r), r), r);
            res[1][1] = this.getElement(1, 0, r).multiply(b.getElement(0, 1, r), r).add(this.getElement(1, 1, r).multiply(b.getElement(1, 1, r), r), r);
            return new MatrixS(res, r);
        }

        MatrixS[] splitted_a = this.split();
        MatrixS[] splitted_b = b.split();

        MatrixS s1 = splitted_a[2].add(splitted_a[3], r); // a21 + a22
        MatrixS s2 = s1.subtract(splitted_a[0], r); // s1 - a11
        MatrixS s3 = splitted_a[0].subtract(splitted_a[2], r); // a11 - a21
        MatrixS s4 = splitted_a[1].subtract(s2, r); // a12 - s2

        MatrixS s5 = splitted_b[1].subtract(splitted_b[0], r); // b12 - b11
        MatrixS s6 = splitted_b[3].subtract(s5, r); // b22 - s5
        MatrixS s7 = splitted_b[3].subtract(splitted_b[1], r); // b22 - b12
        MatrixS s8 = s6.subtract(splitted_b[2], r); // s6 - b21

        MatrixS p1 = multiply_func(s2, s6, r); // s2 * s6
        MatrixS p2 = multiply_func(splitted_a[0], splitted_b[0], r); // a11 * b11
        MatrixS p3 = multiply_func(splitted_a[1], splitted_b[2], r); // a12 * b21
        MatrixS p4 = multiply_func(s3, s7, r); // s3 * s7
        MatrixS p5 = multiply_func(s1, s5, r); // s1 * s5
        MatrixS p6 = multiply_func(s4, splitted_b[3], r); // s4 * b22
        MatrixS p7 = multiply_func(splitted_a[3], s8, r); // a22 * s8

        MatrixS T1 = p1.add(p2, r);
        MatrixS T2 = T1.add(p4, r);

        MatrixS C11 = p2.add(p3, r);
        MatrixS C12 = T1.add(p5, r).add(p6, r);
        MatrixS C21 = T2.subtract(p7, r);
        MatrixS C22 = T2.add(p5, r);

        return join(new MatrixS[] {C11, C12, C21, C22});

    }

    private static MatrixS multiply_func(MatrixS a, MatrixS b, Ring r) {
        if (a.size <= 1) {
            return a.multiply(b, r);
        }
        return a.multiplyStrassWin(b, r);
    }

    
    /** Euclidean norm squared.
 * @param ring Ring
 * @return sum_{i,j} of a_{i,j}^2
 */
    public   Element norm2(  Ring ring) {
        Element sum=ring.numberZERO;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                sum=sum.add(M[i][j].multiply(M[i][j], ring), ring);
            }
        } return sum;}
    
   /** Euclidean norm squared.
   * @param ring Ring
   * @return sum_{i,j} of a_{i,j}^2
   */
    public   Element normEuclidean(  Ring ring) {
        return norm2(ring).sqrt(ring);
    }
      
    
    
    
//    public static void main(String[] args) {
//
//        Ring ring = new Ring("R[]");
//        ring.setAccuracy(30);
//        ring.setMachineEpsilonR(10);
//        ring.setFLOATPOS(40);
//       ring = new Ring("Z[]");
//
//       MatrixS mat =  new MatrixS(5, 5, 1000, new int[]{5}, new Random(),ring.numberONE(), ring);
//        //MatrixS mat = new MatrixS(matrix, ring);
//       // AdjDet rr= new AdjDet(mat, ring.numberONE,ring;
//      // MatrixS[] mm= mat.adjointEchelon(new int[2][0], ring.numberONE, ring);
//
//     //   System.out.println(mm[1]);
//      //  System.out.println(matrix.length+"  "+matrix[0].length);
//      //  System.out.println(mm[1].size+"  "+mm[0].M.length);
//
//        ArrayList<Element> arr = new ArrayList<>();
//        arr.add(0, null);
//        arr.add(1, mat);
//
//
//
//        }


    public static void main(String[] args) {

        Ring ring = new Ring("Z[]");

//        int [][]matrix = {{1, 2, 3, 4}, {5,6, 7, 8}, {9,10,11,12}, {13,14,15,16}};
        double singleSum = 0;
        double multiSum = 0;
        double multiSum2 = 0;
        double it = 1;//Double.parseDouble(args[0]);
        int size = 4;//Integer.parseInt(args[1]);
        for(int i = 0; i < it; i++){
            int[][] matrix = get2xMatrix(size);

            MatrixS mat = new MatrixS(matrix, ring);
//        System.out.println("Matrix:" + mat);
            long t1_start=System.currentTimeMillis();
            int[][] EIEJ = new int[2][];
            MatrixS[] result = mat.adjointEchelon(EIEJ, ring.numberONE, ring);
            long t1_end = System.currentTimeMillis();
//        System.out.println("Adjoint matrix (non-threaded execution): " + result[0]);
            long time = t1_end- t1_start;
            singleSum += time;
            System.out.println("Non-threaded execution duration: " + time);
            t1_start=System.currentTimeMillis();
            EIEJ = new int[2][];
            result = mat.mldtsvAdjointEchelon(EIEJ, ring.numberONE, ring);
            t1_end = System.currentTimeMillis();
            time = t1_end- t1_start;
            multiSum += time;
//        System.out.println("Adjoint matrix (threaded execution): " + result[0]);
            System.out.println("Threaded execution duration: " + time);

            t1_start=System.currentTimeMillis();
            EIEJ = new int[2][];
            result = mat.adjointEchelon2(EIEJ, ring.numberONE, ring);
            t1_end = System.currentTimeMillis();
            time = t1_end- t1_start;
            multiSum2 += time;
//            System.out.println("Adjoint matrix (threaded execution): " + result[0]);
            System.out.println("Threaded 2 execution duration: " + time);
        }
        double averageSingle = singleSum/it;
        double averageMulti = multiSum/it;
        double averageMulti2 = multiSum2/it;
        double multBetterThan1 = 100 - (averageMulti/averageSingle) * 100;
        double mult2BetterThan1 = 100 - (averageMulti2/averageSingle) * 100;

        System.out.println("Ring smallestBlock: " + ring.SMALLESTBLOCK);
        System.out.println("Matrix size (2^N): N = " + size);
        System.out.println("Average single: " + averageSingle);
        System.out.println("Average multi: " + averageMulti);
        System.out.println("Average multi2: " + averageMulti2);
        System.out.printf("Multi is %.3f %% faster than single \n", multBetterThan1);
        System.out.printf("Multi2 is %.3f %% faster than single \n", mult2BetterThan1);
//        MatrixS A = result[0];
//        MatrixS S = result[1];
//        Element d = S.M[0][0];
//        MatrixS id = mat.multiply(A, ring).divideByNumber(d, ring);
//        System.out.println(A.multiply(mat, ring)); //=== S
//        System.out.println("d: " + d);
//        System.out.println("ID: " + id);
//        System.out.println("First element: " + A);
//        System.out.println("Second element: " + S);
        System.exit(0);
    }

    public static int[][] get2xMatrix(int power) {
        int side = (int) Math.pow(2, power);
        int[][] res = new int[side][side];
        int counter = 0;
        for(int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                res[i][j] = counter++;
            }
        }
        return res;
    }

}
/** ???????????????????  thread -- matrixMTrunner ? */
class matrixMTrunner extends Thread {

    public MatrixS a, b, c;
    ElementBufferAllocator buffers;
    int thisThNumb;

    public int maxI, maxJ;

    public matrixMTrunner(
            MatrixS a,
            MatrixS b,
            MatrixS c,
            ElementBufferAllocator buffers,
            int thNumb
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.buffers = buffers;
        this.thisThNumb = thNumb;
    }

    public void run() {
        int resN = a.size, resM = b.colNumb;
        int[] usedColumnsSt = buffers.getIntBuffer(thisThNumb);
        boolean[] usedColumns = buffers.getBooleanBuffer(thisThNumb);
        Element[] bigRow = buffers.getElementBuffer(thisThNumb);
        Ring ring = buffers.getRing();
        int i = buffers.getNextCounterValue();
        maxI = maxJ = 0;
        while (i < resN) {
            int stCnt = 0;
            int cntUniq = 0;
            int curRowLenA = 0;
            if (a.M[i] != null) {
                curRowLenA = a.M[i].length;
            }
            for (int j = 0; j < curRowLenA; j++) {
                int curRowLenB = 0, curAi = i, curBi = a.col[i][j];
                if (b.M[curBi] != null) {
                    curRowLenB = b.M[curBi].length;
                }
                for (int k = 0; k < curRowLenB; k++) {
                    int curBj = b.col[curBi][k];
                    if (!usedColumns[curBj]) {
                        usedColumns[curBj] = true;
                        usedColumnsSt[stCnt++] = curBj;
                    }
                    Element tmpElem = a.M[curAi][j].multiply(b.M[curBi][k], ring);
                    if (bigRow[curBj].isZero(ring)) {
                        cntUniq++;
                    }
                    bigRow[curBj] = bigRow[curBj].add(tmpElem, ring);
                    if (bigRow[curBj].isZero(ring)) {
                        cntUniq--;
                        bigRow[curBj] = ring.numberZERO;
                    }
                }
            }
            c.M[i] = new Element[cntUniq];
            c.col[i] = new int[cntUniq];
            if (cntUniq > 0) {
                boolean isNoneZeroExist = false;
                for (int j = 0; j < stCnt; j++) {
                    int curCol = usedColumnsSt[j];
                    if (!bigRow[curCol].isZero(ring)) {
                        maxJ = Math.max(maxJ, curCol);
                        isNoneZeroExist = true;
                        c.M[i][j] = bigRow[curCol];
                        c.col[i][j] = curCol;
                        bigRow[curCol] = ring.numberZERO;
                    }
                }
                if (isNoneZeroExist) {
                    maxI = Math.max(maxI, i);
                }
            }
            for (int j = 0; j < stCnt; j++) {
                usedColumns[usedColumnsSt[j]] = false;
            }
            i = buffers.getNextCounterValue();
        }
    }
}




/*
Cholesky

int N  = A.length;
        double[][] L = new double[N][N];

        for (int i = 0; i < N; i++)  {
            for (int j = 0; j <= i; j++) {
                double sum = 0.0;
                for (int k = 0; k < j; k++) {
                    sum += L[i][k] * L[j][k];
                }
                if (i == j) L[i][i] = Math.sqrt(A[i][i] - sum);
                else        L[i][j] = 1.0 / L[j][j] * (A[i][j] - sum);
            }
            if (L[i][i] <= 0) {
                throw new RuntimeException("Matrix not positive definite");
            }
        }
        return L;
 */