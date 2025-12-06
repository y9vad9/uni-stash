package com.mathpar.matrix;

import com.mathpar.number.Ring;
import com.mathpar.number.*;
import com.mathpar.polynom.*;
import java.util.*;

/**
 *
 * @author peresl
 */
public class charPolynomMatrixD {

    /** data matrix */
    public MatrixD A;
    /** характеристический полином матрицы А*/
    public Element F;

    public charPolynomMatrixD() {
    }

    public charPolynomMatrixD(MatrixD A) {
        this.A = A;
    }

    public charPolynomMatrixD(MatrixD A, Polynom F) {
        this.A = A;
        this.F = F;
    }

    /**
     * Вычисление хар. полинома матрицы this,
     * используя алгоритм Сейфуллина,
     * не содержит делений и сокращений
     * @return Element[] - коэффициенты характеристического полинома,
     * начиная с коэффициента перед x^n и заканчивая свободным членом
     * @author Pereslavtseva
     */
    public Element[] charPolSeifullin(Ring ring) {
        // return массив Fcoefs
        //System.out.println("ringseif" + ring.MOD32);
        Element[][] M = A.M;
        int n = M.length;
        Element[][] f = new Element[n][n];
        Element[] g = new Element[n];
        Element[] h = new Element[n];
        int t;
        int p;
        f[0][0] = M[0][0];
        for (t = 1; t < n; t++) {
            for (int i = 0; i <= t; i++) {
                h[i] = M[i][t];
            }
            for (p = 0; p < t; p++) {
                f[p][t] = f[p][t - 1].add(h[t], ring);
                g[t] = f[p][t - 1];
                for (int i = 0; i < t; i++) {
                    g[i] = h[i].negate(ring);
                }
                if (p < t) {
                    for (int i = 0; i <= t; i++) {
                        h[i] = ring.numberZERO;
                        for (int j = 0; j <= t; j++) {
                            h[i] = h[i].add(M[i][j].multiply(g[j], ring), ring);
                        }
                    }
                } else {
                    h[t] = ring.numberZERO;
                    for (int i = 0; i <= t; i++) {
                        h[t] = h[t].add(M[t][i].multiply(g[i], ring), ring);
                    }
                }
            }
            f[t][t] = h[t];
        }
        Element[] Fcoef = new Element[n + 1];
        //-pered nech stepen
        for (int i = 0; i < n; i++) {
            Fcoef[i + 1] = (n - i - 1) % 2 == 0 ? f[i][n - 1]
                    : f[i][n - 1].negate(ring);
        }
        Fcoef[0] = n % 2 == 0 ? ring.numberONE : ring.numberMINUS_ONE;
        //коэффициенты хар. полинома по убыванию степени: X^n, ..., X, X^0.
        return Fcoef;
    }

    /**
     * Вычисление хар. полинома матрицы this,
     * используя алгоритм Сейфуллина,
     * не содержит делений и сокращений
     * @return Polynom - характеристического полином от одной переменной
     * с коэффициентами Element
     * @author Pereslavtseva
     */
    public Polynom charPolSeifullinP(Ring ring) {
        Element[] Fcoef = charPolSeifullin(ring);
        int k = Fcoef.length;
        int Fpow[] = new int[k];
        for (int i = 0; i < k - 1; i++) {
            Fpow[i] = k - i - 1;
        }
        return new Polynom(Fpow, Fcoef);
    }

    /**
     * Вычисление присоединенной матрицы для this,
     * используя алгоритм Сейфуллина,
     * не содержит делений и сокращений
     * @return MatrixD - присоединенная матрица = this*
     * @author Pereslavtseva
     */
    public MatrixD adjointSeifullin(Ring ring) {
        // return массив Fcoefs
        Element[][] M = A.M;
        int n = M.length;

        Element[][] f = new Element[n][n];
        Element[] g = new Element[n];
        Element[] h = new Element[n];
        int t;
        int p;
        f[0][0] = M[0][0];
        for (t = 1; t < n - 1; t++) {
            for (int i = 0; i <= t; i++) {
                h[i] = M[i][t];
            }
            for (p = 0; p < t; p++) {
                f[p][t] = f[p][t - 1].add(h[t], ring);
                g[t] = f[p][t - 1];
                for (int i = 0; i < t; i++) {
                    g[i] = h[i].negate(ring);
                }
                if (p < t) {
                    for (int i = 0; i <= t; i++) {
                        h[i] = NumberZ.ZERO;
                        for (int j = 0; j <= t; j++) {
                            h[i] = h[i].add(M[i][j].multiply(g[j], ring), ring);
                        }
                    }
                } else {
                    h[t] = NumberZ.ZERO;
                    for (int i = 0; i <= t; i++) {
                        h[t] = h[t].add(M[t][i].multiply(g[i], ring), ring);
                    }
                }
            }
            f[t][t] = h[t];
        }


        MatrixD hD = A.copy();
        MatrixD gD = new MatrixD();
        for (p = 0; p < n - 1; p++) {
            f[p][n - 1] = f[p][n - 2].add(hD.M[n - 1][n - 1], ring);
            gD = hD.negate(ring);
            for (int i = 0; i < n; i++) {
                gD.M[i][i] = gD.M[i][i].add(f[p][n - 1], ring);
            }

            if (p < n - 2) {
                hD = A.multCU(gD, ring);
            }

        }

        return gD;
    }

    /**
     * Верхняя оценка количества бит наибольшего коэффициента хар. полинома
     * полиномиальной матрицы
     * (по алгоритму Леверрье-Фаддеева)
     * @return int[] --  количество полиномиальных модулей для каждой переменной (x1, x2,...,xt),
     * на первом месте - количество простых модулей из массива Newton.prims
     * @author Pereslavtseva+
     */
    public int[] complexityPolynomialMatrix(Ring ring) {
        int i = 0, n = A.M.length;
        //произведение числовых модулей
        Element proisv = NumberZ.ONE;
        // k - кол-во переменных, ip - кол-во числовых модулей,
        //km - наибольшее число мономов
        int k = 0, km = 1;
        //наибольший числовой коэффициент
        Element a = NumberZ.ZERO;
        //количество полиномиальных модулей для каждой переменной
        int powmax[] = new int[0];
        for (i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Polynom ax = A.M[i][j] instanceof Polynom ? (Polynom) A.M[i][j]
                        : new Polynom(A.M[i][j]);
                int km1 = ax.coeffs.length;
                //Вычисление km -  наибольшее число мономов
                if (km1 > km) {
                    km = km1;
                }
                int k1 = km1 == 0 ? 0 : ax.powers.length / km1;
                //Вычисление k - кол-во переменных
                if (k1 > k) {
                    k = k1;
                }
                for (int t = 0; t < km1; t++) {
                    Element a1 = ax.coeffs[t];
                    //Вычисление наибольшего числового коэффициента а
                    if (a1.abs(ring).compareTo(a, ring) == 1) {
                        a = a1;
                    }
                }
                //Вычисление максимальных степеней для каждой переменной
                int l1 = powmax.length;
                int[] pow = ax.degrees();
                int l2 = pow.length;
                if (l2 <= l1) {
                    for (int t = 0; t < l2; t++) {
                        int s = pow[t];
                        if (s > powmax[t]) {
                            powmax[t] = s;
                        }
                    }
                } else {
                    for (int t = 0; t < l1; t++) {
                        int s = pow[t];
                        if (s > powmax[t]) {
                            powmax[t] = s;
                        }
                    }
                    int[] temp = new int[l2];
                    System.arraycopy(powmax, 0, temp, 0, l1);
                    System.arraycopy(pow, l1, temp, l1, l2 - l1);
                    powmax = new int[l2];
                    System.arraycopy(temp, 0, powmax, 0, l2);

                }
                //System.out.println("ij= "+i+j);
                //acm.printM(powmax);
                //System.out.println();
            }
        }
        //System.out.println("km= "+km);

        for (int t = 0; t < powmax.length; t++) {
            int b = powmax[t];
            powmax[t] = b * n + 1;
        }
        //acm.printM(powmax);

        //i -- кол-во простых числовых модулей
        i = 0;
        double d = Math.log(2);
        NumberZ u = (NumberZ) a.abs(ring);
        int b = u.bitLength();
        b++;
        //System.out.println("nbits= "+b);
        double f = n * (Math.log(n) / d + b + Math.log(km) / d) - Math.log(km) / d;

        long lf = (long) f;
        if (f - lf > 0) {
            lf++;
        }
        lf++;//т.к. [-p,p]


//для 28-битных модулей из number.Newton.prims[]
        i = (int) lf / 28 + 1;
        int[] compl = new int[powmax.length + 1];
        System.arraycopy(powmax, 0, compl, 0, powmax.length);
        compl[powmax.length] = i;
        return compl;
    }

    /**
     * Верхняя оценка количества бит наибольшего коэффициента хар. полинома
     * целочисленной матрицы
     * (по Pernet, Dumas, Wan)
     * @return int[0] --  количество  простых модулей из массива Newton.prims
     * @author Pereslavtseva+
     */
    public int[] complexityNumberZMatrix(Ring ring) {
        int i = 0, n = A.M.length;
        Element max = A.M[0][0];
        Element m;
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                m = A.M[j][k];
                if (m.abs(ring).compareTo(max, ring) > 0) {
                    max = m;
                }
            }
        }

        NumberZ u = (NumberZ) max;
        int b = u.bitLength();

        double d = Math.log(2);
        double f = n / 2. * (Math.log(n) / d + 2 * b + 0.21163175);

        long lf = (long) f;
        if (f - lf > 0) {
            lf++;
        }
        lf++;//т.к. [-k,k]

        //для 28-битных модулей из number.Newton.prims[]
        i = (int) lf / 28 + 1;
        return new int[]{i};
    }

    /**
     * Верхняя оценка количества бит наибольшего коэффициента хар. полинома
     * целочисленной матрицы
     * (по Pernet, Dumas, Wan)
     * @return int[0] --  количество  простых модулей из массива Newton.prims
     * @author Pereslavtseva+
     */
    public int[] complexityNumberZMatrix(int[][] ma, Ring ring) {
        int i = 0, n = ma.length;
        int max = ma[0][0];
        int m;
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                m = ma[j][k];
                if (Math.abs(m) > max) {
                    max = m;
                }
            }
        }

        //NumberZ u = (NumberZ) max;
        //int b = u.bitLength();
        int b = 32;
        double d = Math.log(2);
        double f = n / 2. * (Math.log(n) / d + 2 * b + 0.21163175);

        long lf = (long) f;
        if (f - lf > 0) {
            lf++;
        }
        lf++;//т.к. [-k,k]

        //для 28-битных модулей из number.Newton.prims[]
        i = (int) lf / 28 + 1;
        return new int[]{i};
    }
    /**
     * Верхняя оценка количества бит наибольшего коэффициента хар. полинома
     * полиномиальной матрицы 
     * (по Pernet, Dumas, Wan)
     * @return long --  кол-во бит в наибольшем числовом коэффициенте характ. полинома
     * @author Pereslavtseva+
     */
    public long complexityCoeffOfCharPolPolynomialMatrix(Ring ring) {
       int i = 0, n = A.M.length;
        //произведение числовых модулей
        Element proisv = NumberZ.ONE;
        // k - кол-во переменных, ip - кол-во числовых модулей,
        //km - наибольшее число мономов
        int k = 0, km = 1;
        //наибольший числовой коэффициент
        Element a = NumberZ.ZERO;
        //количество полиномиальных модулей для каждой переменной
        int powmax[] = new int[0];
        for (i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Polynom ax = A.M[i][j] instanceof Polynom ? (Polynom) A.M[i][j]
                        : new Polynom(A.M[i][j]);
                int km1 = ax.coeffs.length;
                //Вычисление km -  наибольшее число мономов
                if (km1 > km) {
                    km = km1;
                }
                int k1 = km1 == 0 ? 0 : ax.powers.length / km1;
                //Вычисление k - кол-во переменных
                if (k1 > k) {
                    k = k1;
                }
                for (int t = 0; t < km1; t++) {
                    Element a1 = ax.coeffs[t];
                    //Вычисление наибольшего числового коэффициента а
                    if (a1.abs(ring).compareTo(a, ring) == 1) {
                        a = a1;
                    }
                }
                //Вычисление максимальных степеней для каждой переменной
                int l1 = powmax.length;
                int[] pow = ax.degrees();
                int l2 = pow.length;
                if (l2 <= l1) {
                    for (int t = 0; t < l2; t++) {
                        int s = pow[t];
                        if (s > powmax[t]) {
                            powmax[t] = s;
                        }
                    }
                } else {
                    for (int t = 0; t < l1; t++) {
                        int s = pow[t];
                        if (s > powmax[t]) {
                            powmax[t] = s;
                        }
                    }
                    int[] temp = new int[l2];
                    System.arraycopy(powmax, 0, temp, 0, l1);
                    System.arraycopy(pow, l1, temp, l1, l2 - l1);
                    powmax = new int[l2];
                    System.arraycopy(temp, 0, powmax, 0, l2);

                }
                //System.out.println("ij= "+i+j);
                //acm.printM(powmax);
                //System.out.println();
            }
        }
        //System.out.println("km= "+km);

        for (int t = 0; t < powmax.length; t++) {
            int b = powmax[t];
            powmax[t] = b * n + 1;
        }
        //acm.printM(powmax);

        //i -- кол-во простых числовых модулей
        i = 0;
        double d = Math.log(2);
        NumberZ u = (NumberZ) a.abs(ring);
        int b = u.bitLength();
        b++;
        //System.out.println("nbits= "+b);
        double f = n * (Math.log(n) / d + b + Math.log(km) / d) - Math.log(km) / d;

        long lf = (long) f;
        if (f - lf > 0) {
            lf++;
        }
        lf++;
        return lf;
    }
    /**
     * Верхняя оценка количества бит наибольшего коэффициента хар. полинома
     * матрицы типа MatrixD
     * (по Pernet, Dumas, Wan)
     * @return long --  кол-во бит в наибольшем числовом коэффициенте характ. полинома
     * @author Pereslavtseva+
     */
    public long complexityCoeffOfCharPolNumberZMatrix(Ring ring) {
        int i = 0, n = A.M.length;
        Element max = A.M[0][0];
        Element m;
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                m = A.M[j][k];
                if (m.abs(ring).compareTo(max, ring) > 0) {
                    max = m;
                }
            }
        }
        
        NumberZ u = (NumberZ) max;
        int b = u.bitLength();

        double d = Math.log(2);
        double f = n / 2. * (Math.log(n) / d + 2 * b + 0.21163175);

        long lf = (long) f;
        if (f - lf > 0) {
            lf++;
        }
        lf++;//т.к. [-k,k]
        return lf;
    }
    /**
     * Верхняя оценка количества бит наибольшего коэффициента хар. полинома
     * целочисленной матрицы
     * (по Pernet, Dumas, Wan)
     * @return long --  кол-во бит в наибольшем числовом коэффициенте характ. полинома
     * @author Pereslavtseva+
     */
    public long complexityCoeffOfCharPolNumberZMatrix(int[][] ma, Ring ring) {
        int i = 0, n = ma.length;
        int max = ma[0][0];
        int m;
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                m = ma[j][k];
                if (Math.abs(m) > max) {
                    max = m;
                }
            }
        }

        //NumberZ u = (NumberZ) max;
        //int b = u.bitLength();
        int b = 32;
        double d = Math.log(2);
        double f = n / 2. * (Math.log(n) / d + 2 * b + 0.21163175);
        //lf -- кол-во бит в наибольшем числовом коэффициенте характ. полинома
        long lf = (long) f;
        if (f - lf > 0) {
            lf++;
        }
        lf++;//т.к. [-k,k]

        return lf;
    }

    /**
     * Вычисление хар. полинома модулярно
     * алгоритм Danilevsky
     *
     * @return PolynomZ характеристический полином
     * @author Pereslavtseva
     */
//    public Polynom[] characteristicPolynomialCRT(Ring ring) {
//
//        int n = A.M.length;
//
//        //модули
//        int[] compl= A.M[0][0] instanceof NumberZ? complexityNumberZMatrix(ring):
//            complexityPolynomialMatrix(ring);
//        //System.out.println("i=  "+i);
//
//        //
//        Polynom[] F = characteristicPolynomialCRT(compl,0);
//
//        return F;
//    }
    /**
     * Вычисление хар. полинома модулярно
     * для модулей val={[0,m_1),...,[0,m_i-1),[m_i1,m_i2),[a,a+1),[b,b+1),...,[k0,k0+1)},
     * |m_i1-m_i2}>1
     * j=2i
     * алгоритм Danilevsky
     *
     * @return PolynomZ характеристический полином
     * @author Pereslavtseva
     */
//    public Element[] characteristicPolynomialCRT(int[] val, int j, Ring ring) {
//
//        Element[] pol;
//        if (j>-1){
//            int med = (val[j]+val[j+1])>>1;
//            int[] val1 = new int [val.length];
//            int[] val2 = new int [val.length];
//            System.arraycopy(val, 0, val1, 0, val.length);
//            val1[j+1] = med;
//            System.arraycopy(val, 0, val2, 0, val.length);
//            val2[j] = med;
//            int j1 = med - val1[j]==1? j-2: j;
//            int j2 =  val2[j+1] - med==1? j-2: j;
//            Element[] pol1 = characteristicPolynomialCRT(val1, j1, ring);
//            Element[] pol2 = characteristicPolynomialCRT(val2, j2, ring);
//            pol = recovery(pol1,val1,j1,pol2,val2,j2);
//        }
//        else{
//            NumberZp32 [] point = new NumberZp32 [(val.length-2)>>2];
//            int v = 0;
//            for (int i = 0; i < val.length-2; i++) {
//                point [v] = new NumberZp32 (val[i]);
//                v++;
//            }
//            long p = val[val.length-2];
//            long [][]M = A.valueOf(point, p);
//            Polynom pol1 = Polynom.polynom_one(NumberZp32.ONE);
//            charPolDanil(M,val[val.length-2],pol1, ring);
//            pol = pol1.coeffs;
//        }
//        return pol;
//    }
    /**the fastest algorithm!
     * Вычисление хар. полинома модулярно
     * Danilevsky
     * for long[][]
     *
     * @author Pereslavtseva+
     *
     */
    public void charPolDanil(long[][] A, Polynom pol, Ring ring) {
//        Ring ring = Ring.defaultR64Ring;
        long p = ring.MOD32;
        //System.out.println("ringdanil"+ring.MOD32);
        int n = A.length, k = 0;
        long[] coef = new long[n + 1];
        for (int i = 0; i < n + 1; i++) {
            coef[i] = 0;
        }
        while (k < n) {
            k = charPolDanilevsky(k, A, p, pol, ring);

        }
    }

    /**
     *
     * Вычисление хар. полинома модулярно
     * Danilevski
     * for long[][]
     *
     * @author Pereslavtseva+
     */
    private int charPolDanilevsky(int t, long[][] A, long p, Polynom pol, Ring ring) {
        //ring = new Ring("Zp32");
        //ring.MOD32=p;
        //NumberZp32.MOD = p;
        int n = A.length, k = 0, r = 0, j, i = 0;
        long a, aInv, sum, s, pr;
        long mas[] = new long[n - t];
        for (k = t; k < n - 1; k++) {

            if (A[k + 1][k] == 0) {
                for (i = k + 2; i < n; i++) {
                    if (A[i][k] != 0) {
                        //вед эл-т=0, есть ниже !=0 эл-т
                        r = 1;
                        break;
                    }
                }
                //вед эл-т=0, ниже все эл-ты=0
                if (i == n) {
                    k++;
                    break;
                }
                /*else
                //вед эл-т=0, есть ниже !=0 эл-т
                r = 1;*/
            }
            //moveStr(k+1,i)
            if (r == 1) {
                long[] M1 = A[i];
                A[i] = A[k + 1];
                A[k + 1] = M1;
            }

            aInv = com.mathpar.number.NFunctionZ32.p_Inverse(A[k + 1][k], p);
            sum = 0;
            s = 0;

            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) {
                    continue;
                }
                //
                sum = sum + A[k + 1][j] * A[j][k];
                s = s + A[0][j] * A[j][k];
                //sum = sum + A[k + 1][j] * A[j][k];
                //s = s + A[0][j] * A[j][k];
                if (j - k - 2 % 128 == 0) {
                    sum = com.mathpar.number.NFunctionZ32.mod(sum, p);
                    s = com.mathpar.number.NFunctionZ32.mod(s, p);
                }
            }
            sum = com.mathpar.number.NFunctionZ32.mod(sum, p);
            pr = com.mathpar.number.NFunctionZ32.mod(aInv * sum, p);
            s = com.mathpar.number.NFunctionZ32.mod(s, p);
            //long mas[] = new long[n - t];
            //k+1-й столбец после умножения слева, он будет i-тым столбцом
            if (r == 1) {

                for (j = t; j < n; j++) {
                    mas[j - t] = j == k + 1
                            ? com.mathpar.number.NFunctionZ32.mod(A[k + 1][k + 1] * aInv, p)
                            : com.mathpar.number.NFunctionZ32.mod(A[j][k + 1]
                            - com.mathpar.number.NFunctionZ32.mod(A[j][k]
                            * com.mathpar.number.NFunctionZ32.mod(A[k + 1][k
                            + 1] * aInv, p), p),
                            p);
                }
            }

            //column k+1

            //str t
            A[t][k + 1] = (r == 1) ? A[t][i] * A[k + 1][k]
                    - A[t][k] * A[k + 1][i] + s - A[t][k] * pr
                    : A[t][k + 1] * A[k + 1][k]
                    - A[t][k] * A[k + 1][k + 1] + s - A[t][k] * pr;
            A[t][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[t][k + 1], p);
            //str t+1...k
            for (j = t + 1; j < k + 1; j++) {
                s = 0;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = s + A[j][m] * A[m][k];
                    if (m - k - 2 % 128 == 0) {
                        s = com.mathpar.number.NFunctionZ32.mod(s, p);
                    }
                }
                s = com.mathpar.number.NFunctionZ32.mod(s, p);

                A[j][k + 1] = r == 1
                        ? A[j - 1][k] + A[j][i] * A[k + 1][k]
                        - A[j][k] * A[k + 1][i] + s - A[j][k] * pr
                        : A[j - 1][k] + A[j][k + 1] * A[k + 1][k]
                        - A[j][k] * A[k + 1][k + 1] + s - A[j][k] * pr;
                A[j][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[j][k + 1], p);
            }

            //str k+2...n-1
            for (j = k + 2; j < n; j++) {
                s = 0;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = s + (A[j][m] * A[m][k]) % p;
                    if (m - k - 2 % 128 == 0) {
                        s = com.mathpar.number.NFunctionZ32.mod(s, p);
                    }
                }
                s = com.mathpar.number.NFunctionZ32.mod(s, p);

                A[j][k + 1] = r == 1
                        ? A[j][i] * A[k + 1][k]
                        - A[j][k] * A[k + 1][i] + s - A[j][k] * pr
                        : A[j][k + 1] * A[k + 1][k]
                        - A[j][k] * A[k + 1][k + 1] + s - A[j][k] * pr;
                A[j][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[j][k + 1], p);
            }

            //str k+1
            A[k + 1][k + 1] = r == 1 ? A[k][k] + pr + A[k + 1][i]
                    : A[k][k] + pr + A[k + 1][k + 1];
            A[k + 1][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[k + 1][k + 1], p);


            //column k+2 ... n-1
            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) //column i
                {
                    for (int m = t; m < n; m++) {
                        A[m][j] = mas[m - t];
                    }
                } else {
                    a = com.mathpar.number.NFunctionZ32.mod(A[k + 1][j] * aInv, p);
                    for (int m = t; m < n; m++) {
                        A[m][j] = m == (k + 1) ? a
                                : com.mathpar.number.NFunctionZ32.mod(A[m][j] - A[m][k] * a, p);
                    }
                }
            }
            //System.out.println("new");
            //acm.print(A);
        }
        k++;
        //System.out.println("k= "+k);
        //pol
        int[] pow = new int[k - t + 1];
        Element[] coef = new Element[k - t + 1];
        pow[0] = k - t;
        coef[0] = (k - t) % 2 == 0 ? NumberZp32.ONE : NumberZp32.MINUS_ONE;
        for (j = 1; j < k - t + 1; j++) {
            pow[j] = k - j;
            coef[j] = new NumberZp32(A[k - j][k - 1]);
        }
        if ((k - t) % 2 == 0) {
            for (j = 1; j < k - t + 1; j++) {
                coef[j] = coef[j].negate(ring);
            }
        }
        Polynom pol1 = new Polynom(pow, coef);
        Polynom pol2;
        //mod p
        //ring.MOD32=p;
        pol2 = pol.mulSS(pol1, ring);
        pol.coeffs = pol2.coeffs;
        pol.powers = pol2.powers;

        return k;
    }

    /**the fastest algorithm!
     * Вычисление хар. полинома модулярно
     * Danilevsky
     * for long[][]
     *
     * @author Pereslavtseva+
     *
     */
    public void charPolDanil(int[][] A, Polynom pol, Ring ring) {
//        Ring ring = Ring.defaultRing;

        long p = ring.MOD32;
        int n = A.length, k = 0;
        long[] coef = new long[n + 1];
        for (int i = 0; i < n + 1; i++) {
            coef[i] = 0;
        }
        while (k < n) {
            k = charPolDanilevsky(k, A, p, pol, ring);

        }
    }

    /**
     *
     * Вычисление хар. полинома модулярно
     * Danilevski
     * for long[][]
     *
     * @author Pereslavtseva+
     */
    private int charPolDanilevsky(int t, int[][] A, long p, Polynom pol, Ring ring) {
        //ring = new Ring("Zp32");
        //ring.MOD32=p;
        //NumberZp32.MOD = p;
        int n = A.length, k = 0, r = 0, j, i = 0;
        int a, aInv, sum, s, pr;
        int mas[] = new int[n - t];
        for (k = t; k < n - 1; k++) {

            if (A[k + 1][k] == 0) {
                for (i = k + 2; i < n; i++) {
                    if (A[i][k] != 0) {
                        //вед эл-т=0, есть ниже !=0 эл-т
                        r = 1;
                        break;
                    }
                }
                //вед эл-т=0, ниже все эл-ты=0
                if (i == n) {
                    k++;
                    break;
                }
                /*else
                //вед эл-т=0, есть ниже !=0 эл-т
                r = 1;*/
            }
            //moveStr(k+1,i)
            if (r == 1) {
                int[] M1 = A[i];
                A[i] = A[k + 1];
                A[k + 1] = M1;
            }

            aInv = (int) com.mathpar.number.NFunctionZ32.p_Inverse(A[k + 1][k], p);
            sum = 0;
            s = 0;

            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) {
                    continue;
                }
                //
                sum = sum + A[k + 1][j] * A[j][k];
                s = s + A[0][j] * A[j][k];
                //sum = sum + A[k + 1][j] * A[j][k];
                //s = s + A[0][j] * A[j][k];
                if (j - k - 2 % 128 == 0) {
                    sum = (int) com.mathpar.number.NFunctionZ32.mod(sum, p);
                    s = (int) com.mathpar.number.NFunctionZ32.mod(s, p);
                }
            }
            sum = (int) com.mathpar.number.NFunctionZ32.mod(sum, p);
            pr = (int) com.mathpar.number.NFunctionZ32.mod(aInv * sum, p);
            s = (int) com.mathpar.number.NFunctionZ32.mod(s, p);
            //long mas[] = new long[n - t];
            //k+1-й столбец после умножения слева, он будет i-тым столбцом
            if (r == 1) {

                for (j = t; j < n; j++) {
                    mas[j - t] = j == k + 1
                            ? (int) com.mathpar.number.NFunctionZ32.mod(A[k + 1][k + 1] * aInv, p)
                            : (int) com.mathpar.number.NFunctionZ32.mod(A[j][k + 1]
                            - com.mathpar.number.NFunctionZ32.mod(A[j][k]
                            * com.mathpar.number.NFunctionZ32.mod(A[k + 1][k
                            + 1] * aInv, p), p),
                            p);
                }
            }

            //column k+1

            //str t
            A[t][k + 1] = (r == 1) ? A[t][i] * A[k + 1][k]
                    - A[t][k] * A[k + 1][i] + s - A[t][k] * pr
                    : A[t][k + 1] * A[k + 1][k]
                    - A[t][k] * A[k + 1][k + 1] + s - A[t][k] * pr;
            A[t][k + 1] = (int) com.mathpar.number.NFunctionZ32.mod(A[t][k + 1], p);
            //str t+1...k
            for (j = t + 1; j < k + 1; j++) {
                s = 0;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = s + A[j][m] * A[m][k];
                    if (m - k - 2 % 128 == 0) {
                        s = (int) com.mathpar.number.NFunctionZ32.mod(s, p);
                    }
                }
                s = (int) com.mathpar.number.NFunctionZ32.mod(s, p);

                A[j][k + 1] = r == 1
                        ? A[j - 1][k] + A[j][i] * A[k + 1][k]
                        - A[j][k] * A[k + 1][i] + s - A[j][k] * pr
                        : A[j - 1][k] + A[j][k + 1] * A[k + 1][k]
                        - A[j][k] * A[k + 1][k + 1] + s - A[j][k] * pr;
                A[j][k + 1] = (int) com.mathpar.number.NFunctionZ32.mod(A[j][k + 1], p);
            }

            //str k+2...n-1
            for (j = k + 2; j < n; j++) {
                s = 0;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = (int) (s + (A[j][m] * A[m][k]) % p);
                    if (m - k - 2 % 128 == 0) {
                        s = (int) com.mathpar.number.NFunctionZ32.mod(s, p);
                    }
                }
                s = (int) com.mathpar.number.NFunctionZ32.mod(s, p);

                A[j][k + 1] = r == 1
                        ? A[j][i] * A[k + 1][k]
                        - A[j][k] * A[k + 1][i] + s - A[j][k] * pr
                        : A[j][k + 1] * A[k + 1][k]
                        - A[j][k] * A[k + 1][k + 1] + s - A[j][k] * pr;
                A[j][k + 1] = (int) com.mathpar.number.NFunctionZ32.mod(A[j][k + 1], p);
            }

            //str k+1
            A[k + 1][k + 1] = r == 1 ? A[k][k] + pr + A[k + 1][i]
                    : A[k][k] + pr + A[k + 1][k + 1];
            A[k + 1][k + 1] = (int) com.mathpar.number.NFunctionZ32.mod(A[k + 1][k + 1], p);


            //column k+2 ... n-1
            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) //column i
                {
                    for (int m = t; m < n; m++) {
                        A[m][j] = mas[m - t];
                    }
                } else {
                    a = (int) com.mathpar.number.NFunctionZ32.mod(A[k + 1][j] * aInv, p);
                    for (int m = t; m < n; m++) {
                        A[m][j] = m == (k + 1) ? a
                                : (int) com.mathpar.number.NFunctionZ32.mod(A[m][j] - A[m][k] * a, p);
                    }
                }
            }
            //System.out.println("new");
            //acm.print(A);
        }
        k++;
        //System.out.println("k= "+k);
        //pol
        int[] pow = new int[k - t + 1];
        Element[] coef = new Element[k - t + 1];
        pow[0] = k - t;
        coef[0] = (k - t) % 2 == 0 ? NumberZp32.ONE : NumberZp32.MINUS_ONE;
        for (j = 1; j < k - t + 1; j++) {
            pow[j] = k - j;
            coef[j] = new NumberZp32(A[k - j][k - 1]);
        }
        if ((k - t) % 2 == 0) {
            for (j = 1; j < k - t + 1; j++) {
                coef[j] = coef[j].negate(ring);
            }
        }
        Polynom pol1 = new Polynom(pow, coef);
        Polynom pol2;
        //mod p
        //ring.MOD32=p;
        pol2 = pol.mulSS(pol1, ring);
        pol.coeffs = pol2.coeffs;
        pol.powers = pol2.powers;

        return k;
    }

    /**
     * Вычисление хар. полинома модулярно
     * Danilevsky
     * for long[][]
     * bitlength (p) =32
     * mod после каждой операции
     * @author Pereslavtseva+
     */
    public void charPolDanil1(long[][] A, long p, Polynom pol, Ring ring) {
        int n = A.length, k = 0;
        long[] coef = new long[n + 1];
        for (int i = 0; i < n + 1; i++) {
            coef[i] = 0;
        }
        while (k < n) {
            k = charPolDanilevsky1(k, A, p, pol, ring);

        }

    }

    /**
     *
     * Вычисление хар. полинома модулярно
     * Danilevski
     * for long[][]
     * bitlength (p) =32
     * mod после каждой операции
     * @author Pereslavtseva+
     */
    private int charPolDanilevsky1(int t, long[][] A, long p, Polynom pol, Ring ring) {
        //ring = new Ring("Zp32");
        ring.MOD32 = p;
        //  NumberZp32.MOD = p;
        int n = A.length, k = 0, r = 0, j, i = 0;
        long a, aInv, sum, s, pr;
        for (k = t; k < n - 1; k++) {

            if (A[k + 1][k] == 0) {
                for (i = k + 2; i < n; i++) {
                    if (A[i][k] != 0) {
                        //вед эл-т=0, есть ниже !=0 эл-т
                        r = 1;
                        break;
                    }
                }
                //вед эл-т=0, ниже все эл-ты=0
                if (i == n) {
                    k++;
                    break;
                }
                /*else
                //вед эл-т=0, есть ниже !=0 эл-т
                r = 1;*/
            }
            //moveStr(k+1,i)
            if (r == 1) {
                long[] M1 = A[i];
                A[i] = A[k + 1];
                A[k + 1] = M1;
            }

            aInv = com.mathpar.number.NFunctionZ32.p_Inverse(A[k + 1][k], p);
            sum = 0;
            s = 0;

            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) {
                    continue;
                }
                //
                sum = sum + A[k + 1][j] * A[j][k];
                s = s + A[0][j] * A[j][k];
                //sum = sum + A[k + 1][j] * A[j][k];
                //s = s + A[0][j] * A[j][k];
                if (j - k - 2 % 128 == 0) {
                    sum = com.mathpar.number.NFunctionZ32.mod(sum, p);
                    s = com.mathpar.number.NFunctionZ32.mod(s, p);
                }
            }
            sum = com.mathpar.number.NFunctionZ32.mod(sum, p);
            pr = com.mathpar.number.NFunctionZ32.mod(aInv * sum, p);
            s = com.mathpar.number.NFunctionZ32.mod(s, p);
            long mas[] = new long[n - t];
            //k+1-й столбец после умножения слева, он будет i-тым столбцом
            if (r == 1) {

                for (j = t; j < n; j++) {
                    mas[j - t] = j == k + 1
                            ? com.mathpar.number.NFunctionZ32.mod(A[k + 1][k + 1] * aInv, p)
                            : com.mathpar.number.NFunctionZ32.mod(A[j][k + 1]
                            - com.mathpar.number.NFunctionZ32.mod(A[j][k]
                            * com.mathpar.number.NFunctionZ32.mod(A[k + 1][k
                            + 1] * aInv, p), p),
                            p);
                }
            }

            //column k+1

            //str t
            A[t][k + 1] = r == 1 ? A[t][i] * A[k + 1][k]
                    - A[t][k] * A[k + 1][i] + s - A[t][k] * pr
                    : A[t][k + 1] * A[k + 1][k]
                    - A[t][k] * A[k + 1][k + 1] + s - A[t][k] * pr;
            A[t][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[t][k + 1], p);
            //str t+1...k
            for (j = t + 1; j < k + 1; j++) {
                s = 0;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = s + A[j][m] * A[m][k];
                    if (m - k - 2 % 128 == 0) {
                        s = com.mathpar.number.NFunctionZ32.mod(s, p);
                    }
                }
                s = com.mathpar.number.NFunctionZ32.mod(s, p);

                A[j][k + 1] = r == 1
                        ? A[j - 1][k] + A[j][i] * A[k + 1][k]
                        - A[j][k] * A[k + 1][i] + s - A[j][k] * pr
                        : A[j - 1][k] + A[j][k + 1] * A[k + 1][k]
                        - A[j][k] * A[k + 1][k + 1] + s - A[j][k] * pr;
                A[j][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[j][k + 1], p);
            }

            //str k+2...n-1
            for (j = k + 2; j < n; j++) {
                s = 0;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = s + (A[j][m] * A[m][k]) % p;
                    if (m - k - 2 % 128 == 0) {
                        s = com.mathpar.number.NFunctionZ32.mod(s, p);
                    }
                }
                s = com.mathpar.number.NFunctionZ32.mod(s, p);

                A[j][k + 1] = r == 1
                        ? A[j][i] * A[k + 1][k]
                        - A[j][k] * A[k + 1][i] + s - A[j][k] * pr
                        : A[j][k + 1] * A[k + 1][k]
                        - A[j][k] * A[k + 1][k + 1] + s - A[j][k] * pr;
                A[j][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[j][k + 1], p);
            }

            //str k+1
            A[k + 1][k + 1] = r == 1 ? A[k][k] + pr + A[k + 1][i]
                    : A[k][k] + pr + A[k + 1][k + 1];
            A[k + 1][k + 1] = com.mathpar.number.NFunctionZ32.mod(A[k + 1][k + 1], p);


            //column k+2 ... n-1
            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) //column i
                {
                    for (int m = t; m < n; m++) {
                        A[m][j] = mas[m - t];
                    }
                } else {
                    a = com.mathpar.number.NFunctionZ32.mod(A[k + 1][j] * aInv, p);
                    for (int m = t; m < n; m++) {
                        A[m][j] = m == (k + 1) ? a
                                : com.mathpar.number.NFunctionZ32.mod(A[m][j] - A[m][k] * a, p);
                    }
                }
            }
            //System.out.println("new");
            //acm.print(A);
        }
        k++;
        //System.out.println("k= "+k);
        //pol
        int[] pow = new int[k - t + 1];
        Element[] coef = new Element[k - t + 1];
        pow[0] = k - t;
        coef[0] = (k - t) % 2 == 0 ? NumberZp32.ONE : NumberZp32.MINUS_ONE;
        for (j = 1; j < k - t + 1; j++) {
            pow[j] = k - j;
            coef[j] = new NumberZp32(A[k - j][k - 1]);
        }
        if ((k - t) % 2 == 0) {
            for (j = 1; j < k - t + 1; j++) {
                coef[j] = coef[j].negate(ring);
            }
        }
        Polynom pol1 = new Polynom(pow, coef);
        Polynom pol2;
        //mod p
        //NumberZp32.setMod(p);
        pol2 = pol.mulSS(pol1, ring);
        pol.coeffs = pol2.coeffs;
        pol.powers = pol2.powers;
        /*try {
        System.out.println(pol.toString(new Ring("Z[x]")));
        }
        catch (PolynomException ex) {
        ex.printStackTrace();
        }*/

        return k;
    }

    /**
     * Вычисление хар. полинома модулярно
     * Danilevsky
     * for NumberZp32[][]
     *
     * @author Pereslavtseva+
     */
    public void charPolDanil(NumberZp32[][] A, long p, Polynom pol, Ring ring) {
        int n = A.length, k = 0;
        long[] coef = new long[n + 1];
        for (int i = 0; i < n + 1; i++) {
            coef[i] = 0;
        }
        //ring = new Ring("Zp32");
        ring.MOD32 = p;
        //      NumberZp32.MOD = p;
        while (k < n) {
            k = charPolDanilevsky(k, A, pol, ring);

        }
    }

    /**
     *
     * Вычисление хар. полинома модулярно
     * Danilevsky
     * for NumberZp32[][]
     *
     * @author Pereslavtseva+
     */
    private int charPolDanilevsky(int t, NumberZp32[][] A, Polynom pol, Ring ring) {
        int n = A.length, k = 0, r = 0, j, i = 0;
        NumberZp32 a, aInv, sum, s, pr;
        for (k = t; k < n - 1; k++) {

            if (A[k + 1][k].isZero(ring)) {
                for (i = k + 2; i < n; i++) {
                    if (!A[i][k].isZero(ring)) {
                        //вед эл-т=0, есть ниже !=0 эл-т
                        r = 1;
                        break;
                    }
                }
                //вед эл-т=0, ниже все эл-ты=0
                if (i == n) {
                    k++;
                    break;
                }
                /*else
                //вед эл-т=0, есть ниже !=0 эл-т
                r = 1;*/
            }
            //moveStr(k+1,i)
            if (r == 1) {
                NumberZp32[] M1 = A[i];
                A[i] = A[k + 1];
                A[k + 1] = M1;
            }

            aInv = A[k + 1][k].inverse(ring);
            sum = NumberZp32.ZERO;
            s = NumberZp32.ZERO;

            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) {
                    continue;
                }
                //
                sum = sum.add(A[k + 1][j].multiply(A[j][k], ring), ring);
                s = s.add(A[0][j].multiply(A[j][k], ring), ring);
            }
            pr = aInv.multiply(sum, ring);
            NumberZp32 mas[] = new NumberZp32[n - t];
            //k+1-й столбец после умножения слева, он будет i-тым столбцом
            if (r == 1) {

                for (j = t; j < n; j++) {
                    mas[j - t] = j == k + 1 ? A[k + 1][k + 1].multiply(aInv, ring)
                            : A[j][k + 1].subtract(A[j][k].multiply(A[k + 1][k + 1], ring).multiply(aInv, ring), ring);
                }
            }

            //column k+1

            //str t
            A[t][k + 1] = r == 1 ? A[t][i].multiply(A[k + 1][k], ring).subtract(
                    A[t][k].multiply(A[k + 1][i], ring), ring).add(s, ring).subtract(A[t][k].multiply(pr, ring), ring)
                    : A[t][k + 1].multiply(A[k + 1][k], ring).subtract(A[t][k].multiply(A[k + 1][k + 1], ring), ring).
                    add(s, ring).subtract(A[t][k].multiply(pr, ring), ring);
            //str t+1...k
            for (j = t + 1; j < k + 1; j++) {
                s = NumberZp32.ZERO;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = s.add(A[j][m].multiply(A[m][k], ring), ring);

                }

                A[j][k + 1] = r == 1
                        ? A[j - 1][k].add(A[j][i].multiply(A[k + 1][k], ring), ring).subtract(
                        A[j][k].multiply(A[k + 1][i], ring), ring).add(s, ring).subtract(A[j][k].multiply(pr, ring), ring)
                        : A[j - 1][k].add(A[j][k + 1].multiply(A[k + 1][k], ring), ring).subtract(
                        A[j][k].multiply(A[k + 1][k + 1], ring), ring).add(s, ring).subtract(A[j][k].multiply(pr, ring), ring);
            }

            //str k+2...n-1
            for (j = k + 2; j < n; j++) {
                s = NumberZp32.ZERO;
                for (int m = k + 2; m < n; m++) {
                    if (r == 1 && m == i) {
                        continue;
                    }
                    s = s.add(A[j][m].multiply(A[m][k], ring), ring);

                }

                A[j][k + 1] = r == 1
                        ? A[j][i].multiply(A[k + 1][k], ring).subtract(
                        A[j][k].multiply(A[k + 1][i], ring), ring).add(s, ring).subtract(A[j][k].multiply(pr, ring), ring)
                        : A[j][k + 1].multiply(A[k + 1][k], ring).subtract(
                        A[j][k].multiply(A[k + 1][k + 1], ring), ring).add(s, ring).subtract(A[j][k].multiply(pr, ring), ring);
            }

            //str k+1
            A[k + 1][k + 1] = r == 1 ? A[k][k].add(pr, ring).add(A[k + 1][i], ring)
                    : A[k][k].add(pr, ring).add(A[k + 1][k + 1], ring);

            //column k+2 ... n-1
            for (j = k + 2; j < n; j++) {
                if (r == 1 && j == i) //column i
                {
                    for (int m = t; m < n; m++) {
                        A[m][j] = mas[m - t];
                    }
                } else {
                    a = A[k + 1][j].multiply(aInv, ring);
                    for (int m = t; m < n; m++) {
                        A[m][j] = m == (k + 1) ? a : A[m][j].subtract(A[m][k].multiply(a, ring), ring);
                    }
                }
            }
            //System.out.println("new");
            //acm.print(A);
        }
        k++;
        //System.out.println("k= "+k);
        //pol
        int[] pow = new int[k - t + 1];
        Element[] coef = new Element[k - t + 1];
        pow[0] = k - t;
        coef[0] = (k - t) % 2 == 0 ? NumberZp32.ONE : NumberZp32.MINUS_ONE;
        for (j = 1; j < k - t + 1; j++) {
            pow[j] = k - j;
            coef[j] = A[k - j][k - 1];
        }
        if ((k - t) % 2 == 0) {
            for (j = 1; j < k - t + 1; j++) {
                coef[j] = coef[j].negate(ring);
            }
        }
        Polynom pol1 = new Polynom(pow, coef);
        Polynom pol2;
        pol2 = pol.mulSS(pol1, ring);
        pol.coeffs = pol2.coeffs;
        pol.powers = pol2.powers;
        /*try {
        System.out.println(pol.toString(new Ring("Z[x]")));
        }
        catch (PolynomException ex) {
        ex.printStackTrace();
        }*/

        return k;
    }

    // для процессора с номером myrank и высоты графа алгоритма boundlev для отрезков номеров модулей
    //compl ={[b0,e0),[b1,e1),...,[bk,ek)}
    //вычисляет номера его модулей и указатель j, записанный на последнем месте в val
    //
    public static int[] intervalsOfModules(int myrank, int[] compl, int boundlev, Ring ring) {

//        int [] compl;
//        if (A.M[0][0] instanceof NumberZ)
//            compl = complexityNumberZMatrix(ring);
//        else compl = complexityPolynomialMatrix(ring);
        int t = compl.length;
        int j = t << 1;
        int[] val = new int[j + 1];
        for (int i = 0; i < t; i++) {
            val[(i << 1) + 1] = compl[i];
        }
        j -= 2;// указатель на третий с конца элемент
        while (boundlev > 0 && j >= 0) {
            int begin = val[j];
            int end = val[j + 1];
            while (end - begin > 1 && boundlev > 0) {
                int med = (begin + end) >> 1;
                if (myrank % 2 == 0) {
                    end = med;
                } else {
                    begin = med;
                }
                myrank /= 2;
                boundlev--;
            }
            val[j] = begin;
            val[j + 1] = end;
            j -= 2;
        }
        val[val.length - 1] = j;
        return val;
    }


/**
     * Вычисление определителя матрицы this,
     * используя алгоритм Сейфуллина,
     * не содержит делений и сокращений
     * @return Element - определитель,
     * @author Pereslavtseva
     */
    public Element detSeifullin(Ring ring) {
        // return массив Fcoefs
        Element[][] M = A.M;
        int n = M.length;
        Element[][] f = new Element[n][n];
        Element[] g = new Element[n];
        Element[] h = new Element[n];
        int t;
        int p;
        f[0][0] = M[0][0];
        for (t = 1; t < n-1; t++) {
            for (int i = 0; i <= t; i++) {
                h[i] = M[i][t];
            }
            for (p = 0; p < t; p++) {
                f[p][t] = f[p][t - 1].add(h[t],ring);
                g[t] = f[p][t - 1];
                for (int i = 0; i < t; i++) {
                    g[i] = h[i].negate(ring);
                }
                if (p < t) {
                    for (int i = 0; i <= t; i++) {
                        h[i] = ring.numberZERO;// NumberZ.ZERO;
                        for (int j = 0; j <= t; j++) {
                            h[i] = h[i].add(M[i][j].multiply(g[j],ring),ring);
                        }
                    }
                } else {
                    h[t] = ring.numberZERO;//NumberZ.ZERO;
                    for (int i = 0; i <= t; i++) {
                        h[t] = h[t].add(M[t][i].multiply(g[i],ring),ring);
                    }
                }
            }
            f[t][t] = h[t];
        }
        for (int i = 0; i < n; i++) {
            h[i] = M[i][n - 1];
        }
        for (p = 0; p < n-1; p++) {
            f[p][n-1] = f[p][n-2].add(h[n-1], ring);
            g[n-1] = f[p][n-2];
            for (int i = 0; i < n-1; i++) {
                g[i] = h[i].negate(ring);
            }
            if (p < n-1) {
                for (int i = 0; i < n; i++) {
                    h[i] = ring.numberZERO;// NumberZ.ZERO;
                    for (int j = 0; j < n; j++) {
                        h[i] = h[i].add(M[i][j].multiply(g[j], ring), ring);
                    }
                }
            } else {
                h[n-1] = ring.numberZERO;//NumberZ.ZERO;
                for (int i = 0; i < n; i++) {
                    h[n-1] = h[n-1].add(M[n-1][i].multiply(g[i], ring), ring);
                }
            }
        }
        Element dF = h[n-1];
        return ((ring.algebra[0]==Ring.Zp32)||(ring.algebra[0]==Ring.Zp))?  dF.Mod(ring): dF;
    }


    public void charPolKrylov(Ring ring) {

        F = Polynom.polynom_one(ring.numberONE);
        charPolKrylov(2, ring);


    }

    private void charPolKrylov(int begin, Ring ring) {
        int n = A.M.length;
        MatrixS S = new MatrixS(A, ring); //Находим в матрице нулевые строки.
        int aa[] = new int[n];
        int cc = 0;

        for (int i = 0; i < n; i++) {

            if (S.M[i].length==0) {

                aa[cc] = i;
                cc++;

            }
        }
        int[] w = new int[cc];
        System.arraycopy(aa, 0, w, 0, cc);

        if (cc>0){
        A.del(w, ring);


            Polynom q = cc%2 ==1 ? new Polynom(new int[]{cc}, new Element[]{ring.numberMINUS_ONE})
                    :new Polynom(new int[]{cc}, new Element[]{ring.numberONE});
      F=F.multiply(q, ring);

        }

        n = A.M.length;
        int k;
        for (k = begin; k <= n; k++) {
            System.out.println("k" + k);
            int l = 0;
            int m = n / k;
            int j = k - 2;
            int m1 = n / (k - 1);
            int len = m1;
            if (len * (k - 1) != n) {
                len++;
            }
            int[] d = new int[len];
            for (int i = 0; i < len - 1; i++) {
                d[i] = k - 1;
            }
            d[len - 1] = (n - m1 * (k - 1) != 0) ? n - m1 * k : k - 1;
            int a = 0;
             int c = m * k == n ? (m-1) * k : m * k;
            while (j < c && a == 0) {
                int i;
                 //System.out.println("j " + j);
                for (i = c; i < n; i++) {
                    //System.out.println("c"+c);
                    if (A.M[i][j].isZero(ring) == false) {
                        break;
                    }
                }
                if (i < n) {
                    a = 1;
                }
                if (a == 0) {
                    j += d[l];
                    l++;
                }
            }
            if (j == m * k) {
                 //System.out.println("++++++++++++++++++++++++++++++++++++++++");
                MatrixD C = A.submatrix(m * (k - 1), n-m * (k - 1), m * (k - 1), n-m * (k - 1), ring);
                //MatrixD C = A.submatrix(0,m*k,0, m*k, ring);
                //System.out.println("c" + C);
                MatrixD G = new MatrixD();//превращаем С в полиномиальную матрицу(домножаем х на элементы главной диагонали)
                G.M = new Polynom[C.M.length][C.M.length];
                G.M[0][0] = new Polynom(new int[]{1, 0}, new Element[]{ring.numberMINUS_ONE, C.M[0][0]});

                for (j = 1; j < C.M.length; j++) {
                    G.M[0][j] = new Polynom(C.M[0][j]);
                }
                for (int i = 1; i < C.M.length; i++) {
                    for (j = i - 1; j < C.M.length; j++) {
                        if (i == j) {
                            G.M[i][j] = new Polynom(new int[]{1, 0}, new Element[]{ring.numberMINUS_ONE, C.M[i][j]});
                        } else {
                            G.M[i][j] = new Polynom(C.M[i][j]);
                        }
                    }
                }
                //System.out.println("G" + G);
                Polynom pol = detKT(G, ring);
                pol = (Polynom) F.multiply(pol, ring);
                A = A.submatrix(0, m * (k - 1), 0, m * (k - 1), ring);
                n = A.M.length;
                //System.out.println("A++" + A);
                charPolKrylov(begin, ring);
                F = (Polynom) F.multiply(pol, ring);
                //System.out.println("F++" + F);
                return;
            } else {
//
                MatrixD K = MatrixD.ZERO(n, ring);
                j = k - 1;
//
                m = n / k;

                len = m;
                if (m * k != n) {
                    len++;
                }
                d = new int[len];
                for (int i = 0; i < m; i++) {
                    d[i] = k;
                }
                d[len - 1] = (n - m * k != 0) ? n - m * k : k;
                //System.out.println("d" + Array.toString(d));
                int h = k - 2;
                l = 0;
                while (j < A.M.length) {  ///////////////////////// Находим матрицу K

                     //System.out.println("jjjj " + A.M.length);
                    for (int i = 0; i < A.M.length; i++) {
                        //System.out.println("lll="+l);
                        K.M[i][j] = A.M[i][h];

                    }
                   int r =0;

                    for (int i=0; i< A.M.length; i++) {
                        if ((i+1)%k==0) continue;
                        K.M[r][i]=ring.numberONE;
                    r++;
                     //System.out.println("i" +i);
                     //System.out.println("r" +r);
                    }

                    j += d[l];
                    h += d[l] - 1;
                    l++;

                }



               //Находим столбцы из матрицы A, которые входят в состав К
               Element[][]KK = new Element[n][m];
                 for (int i = 0; i < n; i++) {
                     for (int o = 0; o < m; o++) {
                       KK[i][o] = NumberZ.ZERO;
                     }
                }
                 MatrixD PartK = new MatrixD(KK);
                     //System.out.println("K" +K.toString());
                     j=0;
                     h = k-2 ;
                     l = 0;
                     while (j<m) {

                 for (int i = 0; i < A.M.length; i++) {
                        PartK.M[i][j] = A.M[i][h];

                    }

                    j ++;
                    h += d[l]-1 ;
                    l++;}
                     System.out.println("PartK"+PartK);
           //Вычисляем всевозможные определители, найденного куска из матрицы К
                 MatrixD BlM = PartK.submatrix(PartK.M.length-m,m,0,m, ring);
                   //System.out.println("BlM"+BlM);
               //int q=0;
                 MatrixD ObrK = MatrixD.ZERO(n, m, ring);
                  //System.out.println("obrK"+ObrK);
                   for (int o = 0; o < m; o++) {
                      MatrixD CopyBlM = BlM.copy();
                       for (int i = 0; i < PartK.M.length-m; i++) {
                          System.arraycopy(PartK.M[i], 0, CopyBlM.M[o], 0, m);
                           //System.out.println("Cop"+CopyBlM);
                           System.out.println("o  "+o+" i "+i);
                           System.out.println("det "+CopyBlM.det(ring));
                           //ObrK = new MatrixD(KK);
                     //Записываем определители найденных кусков на опеределенные места в будущей матрице К-1
                        ObrK.M[i<<1][o]=CopyBlM.det(ring);
                        //System.out.println("obrK"+ObrK);

                       }

                }
                   System.out.println("blmmmm   "+BlM);
                 for (int i = 0; i < m; i++) {
                     for (int o = 0; o < m; o++) {
                         ObrK.M[o<<1+1][i]=(BlM.Minor(i,o,ring)).det(ring);
                     }
                }
                System.out.println("ObrK"+ObrK);

//          int s=0;
//                for (int i = 0; i < n; i++) {
//                   K.M[s][i]=ring.numberONE;
//
//                s++;}
//                MatrixS K = new MatrixS();
//                K.M = new Element[m+1][];
//                K.col = new int[n][];
//                for (int i = 0; i < n; i++) {
//                    K.M[i][0]=ring.numberONE;
//                    System.arraycopy(A.M[i], 0, K.M[i], 1, m);
//                    K.col[0][0]=i*k;
//                }
//                for (int t = 1; t < m; t++) {
//                    K.col[n][m]=
//                }
        System.out.println("K=" + K.toString(ring));


                MatrixS DD = new MatrixS(K, ring);
                  //long time = System.currentTimeMillis();
                MatrixS K1 = DD.inverse(ring);

//                MatrixS SXS = DD.multiply(K1, ring);
                //System.out.println("SXS"+ SXS.toString(ring));
//                System.out.println("???"+(K1.M[0][0] instanceof NumberZp32));
                //System.out.println("K1"+K1);
                MatrixD KMinus = new MatrixD(K1,ring,0);
      //System.out.println("KMinus1"+KMinus);
               // System.out.println("+"+(System.currentTimeMillis()-time));
                //System.out.println("KMIn=" + KMinus.toStringTex(ring));
                //MatrixD K2 = new MatrixD(K1, ring);
                //MatrixS DD2 = new MatrixS(K2.M, ring);
                MatrixS AA = new MatrixS(A.M, ring);
                //long time1 = System.currentTimeMillis();
                //A = K2.multCU(A, ring).multCU(K, ring);
                AA = K1.multiply(AA, ring).multiply(DD, ring);
               // System.out.print(" + "+(System.currentTimeMillis()-time1));
                A = new MatrixD(AA, ring,0);

        //System.out.println("AAAA="+ A.toString(ring));
            }
        }
        // System.out.println("nnn" + n);
        // массив степеней для полинома степени n+1
        int[] pow = new int[n + 1];
//коэффициентов
        Element[] coef = new Element[n + 1];
        pow[0] = n;
        coef[0] = n % 2 == 0 ? ring.numberONE : ring.numberMINUS_ONE;
        for (int j = 1; j < n + 1; j++) {
            pow[j] = n - j;
            coef[j] = A.M[n - j][n - 1];
            if (coef[j].abs(ring).compareTo(new NumberZp32(ring.MOD32 >> 1)) != -1) {
                if (coef[j].isNegative()) {
                    coef[j] = coef[j].add(new NumberZp32(ring.MOD32), ring);
                } else {
                    coef[j] = coef[j].subtract(new NumberZp32(ring.MOD32), ring);
                }
            }
        }
        if (n % 2 == 0) {
            for (int j = 1; j < n + 1; j++) {
                coef[j] = coef[j].multiply(ring.numberMINUS_ONE, ring);
            }

        }

        Polynom pol = new Polynom(pow, coef);
        F = F.multiply(pol, ring);
        System.out.println("pol" + F);

    }
    //Вычисление определителя матрицы полиномов
    //имеющей КТ вид
    //Формула Хессенберга, k - размерность кв. м-цы

    public Polynom detKT(MatrixD Ax, Ring ring) {
        int k = Ax.M.length;
        Polynom[] detK = new Polynom[k + 1];
        detK[0] = Polynom.polynom_one(ring.numberONE);
        detK[1] = (Polynom) Ax.M[0][0];

        if (k > 1) {
            for (int t = 2; t < k + 1; t++) {
                Polynom S = Polynom.polynom_zero(ring.numberZERO);
                Polynom S0 = Polynom.polynom_zero(ring.numberZERO);
                for (int i = 0; i < t - 1; i++) {
                    Polynom Pj = ((Polynom) Ax.M[i + 1][i]).negateThis(ring);
                    for (int j = i + 2; j < t; j++) {
                        Pj = ((Polynom) Ax.M[j][j - 1]).negateThis(ring).multiply(Pj, ring);
                        if (Pj.compareTo(Polynom.polynom_zero(ring.numberZERO)) == 0) {
                            break;
                        }
                    }


                    S0 = (Polynom) Ax.M[i][t - 1].multiply(Pj, ring);
                    if (i != 0) {
                        S0 = S0.multiply(detK[i], ring);
                    }

                    S = S.add(S0, ring);

                }

                //Новый полином, его коэффициенты
                detK[t] = (Polynom) Ax.M[t - 1][t - 1].multiply(detK[t - 1], ring).add(S, ring);

            }
        }

        return detK[k];
    }

    /**
  * Модулярный алгоритм вычисления характеристического полинома числовой матрицы.
  * В конечном поле используется алгоритм Данилевского
  *
  * @param args
  * @return Polynom
  */

    public Polynom charPolynomialMod(Ring ring){
        System.out.println("ring = "+ring);
        Element[][] Ma = A.M;
        int n = Ma.length;
        int [] compl = (Ma[0][0] instanceof Polynom)?
                complexityPolynomialMatrix(ring)
                : complexityNumberZMatrix(ring);
        if (compl.length>1){
            System.out.println("этот метод только для числовой матрицы!");
            return null;
        }
        int numberOfPrims = compl[0];
        System.out.println("numberOfPrims = "+numberOfPrims );
     //   com.mathpar.number.Newton.initCache();
        Ring ring32 = new Ring("Zp32[x,y]");
        long[][] Amod;
        Polynom [] rem = new Polynom [numberOfPrims];
        NumberZ mu = NumberZ.ONE;
        for (int i = 0; i < numberOfPrims; i++) {
            ring32.MOD32 = com.mathpar.number.NFunctionZ32.primes[i];
            Amod = A.valueOf(null, ring);
            //System.out.println("Amod= "+ new MatrixD(Amod,ring32));
            Polynom pol = Polynom.polynom_one(ring32.numberONE);
            charPolDanil(Amod, pol, ring);
            //System.out.println("pol = "+pol);
            rem[i] = pol;
            mu = mu.multiply(new NumberZ(ring32.MOD32));
        }
        //System.out.println("mu = "+mu);
        Polynom res = com.mathpar.number.Newton.recoveryNewtonPolynomZ(null,rem);
        NumberZ hmu = mu.divide(new NumberZ(2));
        for (int i = 0; i < res.coeffs.length; i++) {
            res.coeffs[i] = res.coeffs[i].Mod(mu, ring);
            if (res.coeffs[i].abs(ring).compareTo(hmu)!=-1)
                if (res.coeffs[i].isNegative()) res.coeffs[i]=res.coeffs[i].add(mu, ring);
                else res.coeffs[i]=res.coeffs[i].subtract(mu, ring);
        }
        return res;
    }

public static void main(String args[]) {

        System.out.println("This is a test from parallel"                + "characteristic polynoms!");

          Ring r = new Ring("Z[x]");
         Random ran = new Random();
         int n = 6;
         int nb = 7;
        int d = 100;
         int randomType[] = new int[] {d,nb};
//         Element s[][] = MatrixS.randomScalarArr2d(n, n, d, randomType, ran, ring1);
//         MatrixD B = new MatrixD(s);
//         charPolynomMatrixD q =  new  charPolynomMatrixD(B);
//         System.out.println("A= "+q.A);
//         Polynom f = q.charPolynomialMod(ring1);
//
//         System.out.println("f= "+f);
//         Polynom g = B.characteristicPolynomP(ring1);
//         System.out.println("g= "+g);

//     public static void main(String args[]) throws FileNotFoundException, IOException, MPIException {
//        //MPI.Init(args);
//        //mpirun C java -cp /home/bob/mathpar10Oct2012_int/target/classes/matrix.charPolynomMatrixD
//		//int myrank = MPI.COMM_WORLD.getRank();
//		//int np = MPI.COMM_WORLD.Size();
//                 //if (myrank == 0) {
//         Ring ring = new Ring("Zp32[x,y,z]");
//        Newton.initCache();
//        ring.MOD32 = Newton.prims[0];
//        //System.out.println("ring" + ring.MOD32);
        long[][] b = new long[][]{{15, 5, 24,26,15,43,75,49,45,53,72,50,10,75},{41,47,83,21,42,50,45,94,18,52,42,26,13,67},{32,31,68,30,37,8,12,27,43,22,61,12,25,65},{84,26,85,41,37,29,7,77,92,26,43,73,37,13},{91,8,45,2,29,89,45,73,95,36,51,7,75,34},{55,36,52,77,64,46,82,4,84,29,25,35,18,46},{64,23,61,12,83,26,37,45,67,1,4,34,58,44},{53,48,69,53,58,91,83,71,26,60,18,60,62,18},{19,25,14,60,33,88,39,94,14,81,85,83,30,70},{87,20,11,26,86,42,30,94,35,88,66,22,33,18},{4,91,5,13,32,66,8,51,11,37,6,27,32,18},{60,81,62,26,16,11,17,11,26,28,18,10,16,0},{27,74,41,30,95,18,91,10,10,36,15,74,65,83},{41,93,13,81,39,7,39,19,86,86,59,9,64,59}};
        MatrixD QQ = new MatrixD(b,r);
        System.out.println(""+QQ);
//        //long[][] b = new long[][]{{1,2,2,5},{1,2,2,4},{0,1,4,2},{1,3,1,6}};
//        //long[][] b = new long[][]{{6,2,4,2},{0,0,0,0},{0,0,0,0},{1,5,1,2}};
//        //long[][] b = new long[][]{{0, 0, 0}, {103, 54, 31}, {86, 114, 74}};//стоп
//        //long[][] b = new long[][]{{1,1,2,2,3,3,4,4},{1,1,2,2,2,3,1,2},{1,1,2,2,1,3,1,2},{1,1,2,2,3,3,4,4},{4,1,2,3,3,4,2,1},{1,6,2,5,3,5,4,3},{5,6,7,5,4,0,4,4},{8,4,7,4,0,0,8,2}};
//        //long[][] b = new long[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
//        long[][] b = new long[][]{{1,8,2,8,7,1},{12,1,5,2,4,5},{2,1,8,7,9,9},{2,1,3,4,6,7},{5,2,1,4,6,1},{0,2,0,7,27,1}};
//        //System.out.println("b" + Array.toString(b));
// //long[][] b = new long[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
////     int [] type =new int[]{3};
////       MatrixD A = new MatrixD(10, 10, 100, type, new Random(7), ring);
////        System.out.println("aaaaaa ===  "+A);
//        String name = "/home/bob/timesDanKr";
//        //File file = new File(name);
//        //FileOutputStream fileOut = new FileOutputStream(file);
//        //String s = "tKrilov                                       tDanilevski" + "\n";
//        //fileOut.write(s.getBytes());
//        Random rand = new Random(4);
//        int n = 10;
//        for (n = 40; n < 52; n += 500) {
//            System.out.println("n = " + n);
//
// long totmem = Runtime.getRuntime().totalMemory();
//
//            long[][] mass = new long[n][n];
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < n; j++) {
//                    mass[i][j] = rand.nextInt();
//
//                }
//            }
//
//              long totmem1 = Runtime.getRuntime().totalMemory();
//              //System.out.println("freee11111 "+freemem1);
//             //System.out.println("wwwwwwwwwww "+(totmem1-totmem));
//
//            //MatrixD A = new MatrixD(mass, ring);
//            //System.out.println("mass" + mass[n - 1][0]);
////
//        //System.out.println("aaaaaa ===  "+A);
//            MatrixD A = new MatrixD(b, ring);
//   System.out.println("aaaaaa ===  "+A.toString(ring));//(b, ring);
//         // MatrixD T = A.copy();
//            charPolynomMatrixD ad = new charPolynomMatrixD(A);
//             //charPolynomMatrixD at = new charPolynomMatrixD(T);
//        //long time = System.currentTimeMillis();
//
//            //long totalmem = Runtime.getRuntime().totalMemory();
//            //System.out.println("memorytotal  " + totalmem);
//            //long freemem = Runtime.getRuntime().freeMemory();
//            //System.out.println("memoryfree  " + freemem);
//
//           ad.charPolKrylov(ring);
//            //System.out.println("time_Krylov = "+(System.currentTimeMillis()-time));
//           //long time1 = System.currentTimeMillis();
//            //Polynom as = ad.charPolSeifullinP(ring);
//            //System.out.println("time_seif = "+(System.currentTimeMillis()-time1));
//           //System.out.println("seif " + as);
//             //long memory = totalmem-freemem;
//            //System.out.println("memory  " + memory);
//            //System.out.println("polKrylov  " + ad.F);
//            //System.out.println("aaaaaa ===  "+A);
//            //System.out.println("mass1" + mass[n - 1][0]);
////            System.out.println("");
////            long t2=System.currentTimeMillis()-time;
//
//            //long[][] bbb = new long[][]{{26,70,67},{103,54,31},{86,114,74}};
//               Polynom Fd = Polynom.polynom_one(ring.numberONE);
//         NumberZp32[][] d = new NumberZp32[n][n];
//
//
//
//
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < n; j++) {
//                    d[i][j] = new NumberZp32(mass[i][j]);
//                }
//            }
            //time = System.currentTimeMillis();
        //ad.charPolDanil(d, ring.MOD32, Fd, ring);
          //System.out.println("danil"+ad.F);
          // System.out.println("time_Danilevski = "+(System.currentTimeMillis()-time));

       // } //цикл

              //A = new MatrixD(mass, ring);
           // System.out.println("d" + d[n - 1][0]);

            //System.out.println("danil  " + Fd);
            //Polynom as = ad.charPolSeifullinP(ring);
            //Polynom c = (Polynom) ad.F.subtract(as, ring);
            //System.out.println("c   "+c);
           // System.out.println("seif  " + at.charPolSeifullinP(ring).subtract(ad.F, ring));
        //long t3 = System.currentTimeMillis()-time;
           // System.out.println("time_Danilevski = "+(System.currentTimeMillis()-time));
        // s = t2  + "  " + t3 +" " + "\n";
            //fileOut.write(s.getBytes());


//            System.out.println("");
//        }----------
            //System.out.println("ad" + (Polynom) ad.F);

//         Polynom one = Polynom.polynomZone;
//         Ring ring1 = Ring.defaultZRing;
//         Random ran = new Random();
//         int n = 100;
//         int nb = 10;
//        int d = 100;
//         int randomType[] = new int[] {1,d,nb};
//         Element s[][] = MatrixS.randomScalarArr2d(n, n, d, randomType, ran, ring1);
//         MatrixD B = new MatrixD(s);
//         charPolynomMatrixD q =  new  charPolynomMatrixD(B);
//         int compl [] = q.complexityPolynomialMatrix(ring1);
//         System.out.println("A= "+Arrays.toString(compl));


//Деление полинома на части, определенные разбиением первого полинома
//    Ring ring = new Ring("R[x,y,z]");
//    Random rnd=new Random();
//    int[] y1 = new int[]{5,3,70,12};
//
//    Polynom s4 = new Polynom();
//
//    Polynom p1 = s4.random(y1, rnd, ring).deleteZeroCoeff(ring);
//    Polynom p2 = s4.random(y1, rnd, ring).deleteZeroCoeff(ring);
//    Polynom[] mon=p1.last_sub_polynoms(3);
//    for(int i=0;i<mon.length;i++){
//    System.out.println("res1="+mon[i].toString(ring));}
//
//
//    System.out.println("p1="+p1.toString(ring));
//    System.out.println("p2="+p2.toString(ring));
//    System.out.println("________________________________________________");
//
//    Polynom[] newp2=p2.sub_monom_pol(mon, ring);
//    for(int i=0;i<newp2.length;i++){
//    System.out.println("res2="+newp2[i].toString(ring));}
//    //        MPI.Finalize();
            // }



            //System.out.println("A= "+A);
//        Polynom q = one.random(randomType, ran, ring);
//         System.out.println("q= "+q);

        //}
        //fileOut.close();

    }
//                 else{ System.out.println("asdasd");
//                 }
                 // MPI.Finalize();
     }

// end of class charPolynomMatrixD

