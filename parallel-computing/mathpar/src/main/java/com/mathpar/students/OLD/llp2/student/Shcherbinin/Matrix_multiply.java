/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import java.util.Arrays;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.number.SubsetZ;
import mpi.*;
import com.mathpar.number.NumberZ;

/**
 *
 * @author ridkeim
 */
//mpirun C java -cp /home/ridkeim/NetBeansProjects/mathpar/target/classes:$CLASSPATH llp2.student.Shcherbinin.Matrix_multiply
public class Matrix_multiply {

//    public static ParallelDebug pd;
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        MatrixS a = new MatrixS(NumberZ.ONE);
        MatrixS b = new MatrixS(NumberZ.ONE);
        int m = 100;
        int n = 100;
        int k = 90;
        if (MPI.COMM_WORLD.getRank() == 0) {
            try {
                switch (args.length) {
                    case 1:
                        n = Integer.parseInt(args[0]);
                        break;
                    case 2:
                        m = Integer.parseInt(args[0]);
                        n = Integer.parseInt(args[1]);
                        k = Integer.parseInt(args[2]);
                        break;
                    case 3:
                        m = Integer.parseInt(args[0]);
                        n = Integer.parseInt(args[1]);
                        k = Integer.parseInt(args[2]);

                        break;
                    case 4:
                        if (args[1].equals(args[2])) {
                            m = Integer.parseInt(args[0]);
                            n = Integer.parseInt(args[1]);
                            k = Integer.parseInt(args[3]);
                        }
                }
            } catch (Exception e) {
            }
//            pd.paddEvent("", null);

//            pd.paddEvent("in the begining", "stat mult");
//        MatrixS[] ab = split(a1, false, getNumb(n, p));
//        for (int i = 0; i < ab.length; i++) {
        }
//        MatrixS d2 = multPar(a1, b1);
//        //!!!! MPI.COMM_WORLD.Barrier();
//        for (int i = 1; i < //!!!! MPI.COMM_WORLD.Size(); i++) {
//            multPar(a1, b1);
//            //!!!! MPI.COMM_WORLD.Barrier();
//        }

//        MatrixS d3 = new MatrixS(NumberZ.ONE);
//        for (int i = 1; i < //!!!! MPI.COMM_WORLD.Size()+1; i++) {
//            if (//!!!! MPI.COMM_WORLD.getRank() == 0) {
//                System.out.println("runnig task on "+i+" processor(s)");
        a = getRandomMatrix(m, n);
        b = getRandomMatrix(n, k);
//                long time = System.currentTimeMillis();
//                d3 = a.multiply(b, Ring.ringR64xyzt);
//                time = System.currentTimeMillis() - time;
//                System.out.println("time without MPI = " + time + "ms");
//            }
        MatrixS d2 = multPar(a, b);
//            //!!!! MPI.COMM_WORLD.Barrier();
//            if (//!!!! MPI.COMM_WORLD.getRank() == 0) {
//                System.out.println("checking result = " + d3.equals(d2, Ring.ringR64xyzt));
//            }
//    }

//        multPar(a1, b1);
//        pd.generateDebugLog();
        //!!!! MPI.Finalize();
    }

    static String tS(int[][] a) {
        String res[] = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = Arrays.toString(a[i]) + "\n";
        }
        return Arrays.toString(res);
    }

    /**
     * Блочное умножение матриц.
     *
     * @param a первая матрица
     * @param b вторая матрица
     * @param byrows парметр разбивки одной из матриц, по строкам или по
     * столбцам
     * @param numbproc количество процессоров
     * @return произведение матриц
     */
    public static MatrixS multPar(MatrixS a, MatrixS b) throws MPIException {

        long time = System.currentTimeMillis();
        int myrank = MPI.COMM_WORLD.getRank();
        int size =  MPI.COMM_WORLD.getSize();
        System.out.println("n = " + myrank + " a = " + a + " b =" + b);
//        MatrixS[] rbuff = new MatrixS[2];

        boolean byrows = (a.size > b.colNumb) ? true : false;
        boolean[] brows = new boolean[]{byrows};
//!!!!        MPI.COMM_WORLD.Bcast(brows, 0, brows.length, //!!!! MPI.BOOLEAN, 0);
        byrows = brows[0];
        MatrixS[] rbuff = new MatrixS[]{a, b};
        if (byrows) { // Разбиваем первую матрицу на блоки строк

            // Отсылаем вторую матрицу целиком
            if (myrank == 0) {
                rbuff[1] = b;
                for (int i = 1; i < size; i++) {
//!!!!                  MPI.COMM_WORLD.Isend(rbuff, 1, 1, //!!!! MPI.OBJECT, i, i); //Отправка остальным процессорам второй матрицы
                }
            } else {
//!!!!                MPI.COMM_WORLD.Recv(rbuff, 1, 1, //!!!! MPI.OBJECT, 0, myrank);//Прием остальными процессорами второй матрицы
            }
            if (myrank == 0) {
                MatrixS[] temp = split(a, byrows, getNumb(a.size, size));// Разбика первой матрицы на блоки строк
                rbuff[0] = temp[0];
                for (int i = 1; i < size; i++) {
 //!!!!                   MPI.COMM_WORLD.Isend(temp, i, 1, //!!!! MPI.OBJECT, i, i);// Рассылка первой матрицы остальным процессорам
                }
            } else {
//!!!!                 MPI.COMM_WORLD.Recv(rbuff, 0, 1, //!!!! MPI.OBJECT, 0, myrank);// Прием остальными процессорами
            }
        } else {
            if (myrank == 0) {
                rbuff[0] = a;
                for (int i = 1; i < size; i++) {
//!!!!                    //!!!! MPI.COMM_WORLD.Isend(rbuff, 0, 1, //!!!! MPI.OBJECT, i, i);
                }
            } else {
//!!!!                //!!!! MPI.COMM_WORLD.Recv(rbuff, 0, 1, //!!!! MPI.OBJECT, 0, myrank);
            }
            if (myrank == 0) {
                MatrixS[] temp = split(b, byrows, getNumb(b.colNumb, size));// Разбика первой матрицы на блоки строк
                rbuff[0] = temp[0];
                for (int i = 1; i < size; i++) {
//!!!!                    //!!!! MPI.COMM_WORLD.Isend(temp, i, 1, //!!!! MPI.OBJECT, i, i);// Рассылка первой матрицы остальным процессорам
                }
            } else {
 //!!!!               //!!!! MPI.COMM_WORLD.Recv(rbuff, 1, 1, //!!!! MPI.OBJECT, 0, myrank);// Прием остальными процессорами
            }
        }
        MatrixS[] res = new MatrixS[size];
        res[myrank] = rbuff[0].multiply(rbuff[1], Ring.ringR64xyzt);
//        System.out.println("proc " + myrank + ": job done!");
//        //!!!! MPI.COMM_WORLD.Gather(res, 0, 1, //!!!! MPI.OBJECT, temp, myrank, 1, //!!!! MPI.OBJECT, 0);
        if (myrank != 0) {
//!!!!            //!!!! MPI.COMM_WORLD.Isend(res, myrank, 1, //!!!! MPI.OBJECT, 0, myrank);// Рассылка результата
            time = 0;
        } else {
            for (int i = 1; i < size; i++) {
//!!!!                //!!!! MPI.COMM_WORLD.Recv(res, i, 1, //!!!! MPI.OBJECT, i, i);// Прием остальными процессорами
            }
            MatrixS result = concatMatrixS(res, byrows);
            time = System.currentTimeMillis() - time;
            System.out.println("time with MPI = " + time + "ms");
            long time1 = System.currentTimeMillis();
            MatrixS r = a.multiply(b, Ring.ringR64xyzt);
            time1 = System.currentTimeMillis() - time1;
            System.out.println("time without MPI = " + time1 + "ms");
            return result;
        }
        return new MatrixS(NumberZ.ONE);//concatMatrixS(temp, byrows);
    }

    /**
     * Получения массива разбивки
     *
     * @param numb количество строк или столбцов матрицы
     * @param proc количесвто используемых процессоров
     * @return массив разбивки
     */
    public static int[] getNumb(int numb, int proc) {
        SubsetZ set = new SubsetZ(new int[]{0, numb - 1});
        SubsetZ[] set0 = set.divideOnParts(proc);
        int res[] = new int[set0.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = set0[i].cardinalNumber();
        }
        return res;
    }

    /**
     * Разбивка матрицы на блоки.
     *
     * @param a входная матрица
     * @param byrows параметр разбивки: true - разбивка по строкам, false -
     * разбивка по столбцам,
     * @param numb массив разбивки, numb.length = a.size | a.colNumb в
     * зависимости от параметра byrows
     * @return массив матриц, полученый из исходной путем разбивки построкам или
     * столбцам
     */
    public static MatrixS[] split(MatrixS a, boolean byrows, int[] numb) {
        MatrixS[] res = new MatrixS[numb.length];
        if (byrows) {
            int rows = 0;
            for (int i = 0; i < numb.length; i++) {
                res[i] = a.getSubMatrix(rows, rows + numb[i] - 1, 0, a.colNumb);
                rows += numb[i];
            }
        } else {
            int cols = 0;
            for (int i = 0; i < numb.length; i++) {
                res[i] = a.getSubMatrix(0, a.size - 1, cols, cols + numb[i] - 1);
                cols += numb[i];

            }
        }
        return res;
    }

    /**
     * Склейка матриц из массива в одну матрицу
     *
     * @param a масиив исходных матриц
     * @param byrows параметр склейки: true - склейка по строкам, false -
     * склейка по столбцам,
     * @return матрица склееная из входного массива
     */
    public static MatrixS concatMatrixS(MatrixS[] a, boolean byrows) {
        if (byrows) {
            int colNum = a[a.length - 1].colNumb;
            int size = 0;
            for (int i = 0; i < a.length; i++) {
                size += a[i].size;
            }
            int[][] col = new int[size][0];
            Element[][] M = new Element[size][0];
            int row = 0;
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].size; j++) {
                    col[row] = a[i].col[j];
                    M[row] = a[i].M[j];
                    row += 1;
                }
            }
            return new MatrixS(size, colNum, M, col);
        } else {

            int size = a[a.length - 1].size;
            int colNum = 0;
            for (int i = 0; i < a.length; i++) {
                colNum += a[i].colNumb;
            }
            int[][] col = new int[size][colNum];
            Element[][] M = new Element[size][colNum];

            int cols = 0;
            int row = 0;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < a.length; j++) {


                    if (cols != 0) {
                        for (int k = 0; k < a[j].col[i].length; k++) {
                            a[j].col[i][k] += cols;
                        }
                    }
                    System.arraycopy(a[j].col[i], 0, col[i], cols, a[j].col[i].length);
                    System.arraycopy(a[j].M[i], 0, M[i], cols, a[j].M[i].length);
                    for (int k = cols; k < cols + a[j].colNumb; k++) {
                        if (M[i][k] == null) {
                            M[i][k] = NumberZ.ZERO;
                            col[i][k] = k;
                        }
                    }


                    cols += a[j].colNumb;
                }
                cols = 0;
            }
//            MatrixS res = new MatrixS(M, Ring.ringR64xyzt);
//            res.size = size;
//            res.colNumb = colNum;
            return new MatrixS(size, colNum, M, col);
        }
    }

    public static MatrixS getRandomMatrix(int m, int n) {
        int[][] a = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if ((i == j) | (i == 2 * j)) {
                    a[i][j] = 0;
                } else {
                    a[i][j] = (int) (100 * Math.random() - 100);
                }
            }
        }
        return new MatrixS(a, Ring.ringR64xyzt);
    }

    private static MatrixS mult_(MatrixS a, MatrixS b, int size, int rank, int minSize) throws MPIException {
        size =  MPI.COMM_WORLD.getSize();
        int allproc = size;
        rank = MPI.COMM_WORLD.getRank();
        int c0 = a.size * b.colNumb / size;
        int min = minSize * minSize;
        if (c0 < min) {
            int numsize = a.size * b.colNumb / min;
            int k = (int) Math.round(Math.sqrt(numsize));
            int newsize = k * k;
            if (newsize < size) {
                size = newsize;
            } else {
                size = (k - 1) * (k - 1);
            }
        }
        int[][] s = getN_(a.size, b.colNumb, size, rank);
        MatrixS a0 = a.getSubMatrix(s[0][0], s[0][0] + s[1][0] - 1, 0, a.colNumb);
        MatrixS b0 = b.getSubMatrix(0, b.size - 1, s[0][1], s[0][1] + s[1][1]);
//        for (int i = 0; i < s.length; i++) {
//            System.out.println(Arrays.toString(s[i]));
//
//        }
        MatrixS[] C = new MatrixS[size];
        C[rank] = a0.multiply(b0, Ring.ringR64xyzt);
        for (int i = 0; i < size; i++) {
//!!!!            //!!!! MPI.COMM_WORLD.Bcast(C, i, 1, //!!!! MPI.OBJECT, i);
        }
        System.out.println("a0 on " + rank + " = " + a0 + "\nb0 on " + rank + " = " + b0);


//        NumberZ[] n  = getN(s, c, sz);
//        s=s.divide(n[0]);
//        c=c.divide(n[1]);


        return con(C, s[2]);

    }

    private static int[] getN(int a, int b, int sz) {
        NumberZ sz1 = new NumberZ(sz);
        NumberZ n = new NumberZ(a).GCD(sz1);
        int[] tmp = new int[]{sz, 1};
        if (n.isOne(Ring.ringR64xyzt)) {
            n = new NumberZ(b).GCD(sz1);
            if (n.isOne(Ring.ringR64xyzt)) {
                return tmp;
            } else {
                int k = n.intValue();
                tmp = getN(a, b / k, sz / k);
                tmp[1] *= k;
            }
        } else {
            int k = n.intValue();
            tmp = getN(a / k, b, sz / k);
            tmp[0] *= k;
        }
        return tmp;
    }

    private static int[][] getN_(int a, int b, int sz, int rank) {
        int[] s = getN(a, b, sz);
        int[] s1 = new int[]{a / s[0], b / s[1]};
        int x = rank / s[1];
        int y = rank - x * s[1];
        int[] s2 = new int[]{s1[0] * x, s1[1] * y};
        return new int[][]{s2, s1, s};
    }

    private static MatrixS con(MatrixS[] A, int[] rows, int[] cols) {
        MatrixS res = new MatrixS();
        if (rows.length == 1) {
            res = concatMatrixS(A, false);
        } else {
            if (cols.length == 1) {
                res = concatMatrixS(A, true);
            } else {
                MatrixS[] C = new MatrixS[rows.length];
                for (int j = 0; j < C.length; j++) {
                    MatrixS[] B = new MatrixS[cols.length];
                    for (int i = 0; i < B.length; i++) {
                        B[i] = A[j * B.length + i];
                    }
                    C[j] = concatMatrixS(B, false);
                }
                res = concatMatrixS(C, true);
            }
        }
        return res;
    }

    private static MatrixS con(MatrixS[] A, int[] s) {
        MatrixS res = null;
        if (s[0] == 1) {
            res = concatMatrixS(A, false);
        } else {
            if (s[1] == 1) {
                res = concatMatrixS(A, true);
            } else {
                MatrixS[] C = new MatrixS[A.length / s[0]];
                for (int j = 0; j < C.length; j++) {
                    MatrixS[] B = new MatrixS[A.length / s[1]];
                    for (int i = 0; i < B.length; i++) {
                        B[i] = A[j * B.length + i];
                    }
                    C[j] = concatMatrixS(B, false);
                }
                res = concatMatrixS(C, true);
            }
        }
        return res;
    }

    public static MatrixS con(MatrixS[] a, int[] rows, int[] cls, boolean byrows) {
        if (byrows) {
            return concatMatrixS(a, byrows);
        } else {
            int size = rows.length * rows[0];
            int colNum = cls.length * cls[0];
//            for (int i = 0; i < a.length; i++) {
//                colNum += a[i].colNumb;
//            }
            int[][] col = new int[size][colNum];
            Element[][] M = new Element[size][colNum];

            int cols = 0;
            int row = 0;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < a.length; j++) {


                    if (cols != 0) {
                        for (int k = 0; k < a[j].col[i].length; k++) {
                            a[j].col[i][k] += cols;
                        }
                    }
                    System.arraycopy(a[j].col[i], 0, col[i], cols, a[j].col[i].length);
                    System.arraycopy(a[j].M[i], 0, M[i], cols, a[j].M[i].length);
                    for (int k = cols; k < cols + a[j].colNumb; k++) {
                        if (M[i][k] == null) {
                            M[i][k] = NumberZ.ZERO;
                            col[i][k] = k;
                        }
                    }

                    cols += a[j].colNumb;
                }
                cols = 0;
            }
//            MatrixS res = new MatrixS(M, Ring.ringR64xyzt);
//            res.size = size;
//            res.colNumb = colNum;
            return new MatrixS(size, colNum, M, col);
        }
    }

    public static MatrixS multiply(MatrixS A, MatrixS B, int minSize, Ring ring) throws MPIException {
        if (A.size * B.colNumb <= minSize * minSize) {
            return A.multiply(B, ring);
        } else {
            return mult(A, B, minSize, ring);
        }
    }

    public static MatrixS multiplyDiv(MatrixS A, MatrixS B, Element D, int minSize, Ring ring) throws MPIException {
        if (A.size * B.colNumb <= minSize * minSize) {
            return A.multiply(B, ring).divideByNumber(D, ring);
        } else {
            return multDiv(A, B, D, minSize, ring);
        }
    }

    private static MatrixS multDiv(MatrixS A, MatrixS B, Element D, int minSize, Ring ring) throws MPIException {
        int size =  MPI.COMM_WORLD.getSize();
//        System.out.println("A.size===="+A.size+" size= "+size);
        int rank =  MPI.COMM_WORLD.getRank();
        int useproc = getProc(size);
//        System.out.println("size="+size+" use= "+useproc);

        while (A.size * B.size / useproc < minSize * minSize) {
            useproc /= 2;
            if (useproc <= 1) {
                return A.multiply(B, ring);
            }
        }

//        System.out.println("usep="+useproc);
        int k = getPow(useproc);
//        System.out.println("k="+k);
        int k1 = k / 2;
        int k2 = k - k1;
        int prow = (int) Math.pow(2, k2);
        int pcol = (int) Math.pow(2, k1);
        int[] irow = getNumb(A.size, prow);
        int[] icol = getNumb(A.size, pcol);
        MatrixS[] C = new MatrixS[size];
        if (rank < useproc) {
//            System.out.println("irow="+Arrays.toString(irow));
//            System.out.println("icol="+Arrays.toString(icol));
            C[rank] = getMult(A, B, irow, icol, rank, ring).divideByNumber(D, ring);

//            System.out.println("C["+rank+"]="+C[rank]);
        } else {
//            System.out.println("proc "+rank+" sleeping");
        }
        for (int i = 0; i < useproc; i++) {
//!!!!            //!!!! MPI.COMM_WORLD.Bcast(C, i, 1, //!!!! MPI.OBJECT, i);
        }
        return con(C, irow, icol);
    }

    private static MatrixS mult(MatrixS A, MatrixS B, int minSize, Ring ring) throws MPIException {
        int size =  MPI.COMM_WORLD.getSize();
//        System.out.println("A.size===="+A.size+" size= "+size);
        int rank = MPI.COMM_WORLD.getRank();
        int useproc = getProc(size);
//        System.out.println("size="+size+" use= "+useproc);
        if (A.isZero(ring) || B.isZero(ring)) {
            return new MatrixS(new int[A.size][B.colNumb], ring);
        }
        while (A.size * B.size / useproc < minSize * minSize) {
            useproc /= 2;
            if (useproc <= 1) {
                return A.multiply(B, ring);
            }
        }

//        System.out.println("usep="+useproc);
        int k = getPow(useproc);
//        System.out.println("k="+k);
        int k1 = k / 2;
        int k2 = k - k1;
        int prow = (int) Math.pow(2, k2);
        int pcol = (int) Math.pow(2, k1);
        int[] irow = getNumb(A.size, prow);
        int[] icol = getNumb(A.size, pcol);
        MatrixS[] C = new MatrixS[size];

        if (rank < useproc) {
//            System.out.println("irow="+Arrays.toString(irow));
//            System.out.println("icol="+Arrays.toString(icol));
            C[rank] = getMult(A, B, irow, icol, rank, ring);
//            System.out.println("C["+rank+"]="+C[rank]);
        } else {
//            System.out.println("proc "+rank+" sleeping");
        }
        for (int i = 0; i < useproc; i++) {
//!!!!            //!!!! MPI.COMM_WORLD.Bcast(C, i, 1, //!!!! MPI.OBJECT, i);
        }
        return con(C, irow, icol);
    }

    private static int getProc(int size) {
        int n = 1;
        while (size > 1) {
            size /= 2;
            n *= 2;
        }
        return n;
    }

    private static int getPow(int size) {
        int k = 0;
        while (size > 1) {
            size /= 2;
            k++;
        }
        return k;
    }

    ;
    private static MatrixS getMult(MatrixS A, MatrixS B, int[] rows, int[] cols, int rank, Ring ring) {
        int row = rank / cols.length;
        int col = rank - cols.length * row;
//        System.out.println(row*rows[row]+" "+(row*rows[row]+rows[row]-1)+" "+0+" "+(A.colNumb-1));
//        System.out.println(0+" "+(B.size-1)+" "+col*cols[col]+" "+(col*cols[col]+cols[col]-1)+" "+B.colNumb);
//        System.out.println("B="+B);
        MatrixS a0 = null;
        MatrixS b0 = null;
        try {
            a0 = A.getSubMatrix(row * rows[row], row * rows[row] + rows[row] - 1, 0, A.colNumb - 1);
            b0 = B.getSubMatrix(0, B.size - 1, col * cols[col], col * cols[col] + cols[col] - 1);

        } catch (Exception e) {
        }
        if (a0 == null) {
            int t = A.size;
            int rowstart = row * rows[row] + 1;
            int rowend = row * rows[row] + rows[row];

            int l = rowend - rowstart + 1;
            Element[][] M = new Element[l][A.colNumb];
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < A.colNumb; j++) {
                    M[i][j] = NumberZ.ZERO;
                }
            }
            a0 = new MatrixS(M, ring);
//                System.out.println("AAA==="+a0);

        }
        if (b0 == null) {
            int t = B.columnsNumber();
            int colstart = col * cols[col] + 1;
            int colend = col * cols[col] + cols[col];
//            if(colend>t){
            int l = colend - colstart + 1;
            Element[][] M = new Element[B.size][l];
            for (int i = 0; i < B.size; i++) {
                for (int j = 0; j < l; j++) {
                    M[i][j] = NumberZ.ZERO;
                }
            }
            b0 = new MatrixS(M, ring);
//                if(b0==null){
//                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//
//            }

//            System.out.println("AAA="+t);
        }
//        System.out.println("a0"+a0);
//        System.out.println("b0"+b0);
//        System.out.println("row="+row+" col= "+col+"\na0 on ["+rank+"]="+a0+"\n a0 par"+a0.size+" "+a0.colNumb+"\n b0 on ["+rank+"]="+b0+"\n b0 par"+b0.size+" "+b0.colNumb);
//        for (int i = 0; i < a0.size; i++) {
//            System.out.println("a["+i+"]M="+Arrays.toString(a0.M[i]));
//            System.out.println("a["+i+"]C="+Arrays.toString(a0.col[i]));
//        }

//        for (int i = 0; i < b0.size; i++) {
//            System.out.println("b["+i+"]M="+Arrays.toString(b0.M[i]));
//            System.out.println("b["+i+"]C="+Arrays.toString(b0.col[i]));
//        }

        return a0.multiply(b0, ring);
    }
}
