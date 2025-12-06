
package com.mathpar.number;

import com.mathpar.polynom.Polynom;

/**
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Parrallel Computer Algebra Laboratory, Institute of Physics,
 * Mathematics and Computing,
 * TSU named by G.R. Derzhavin
 * </p>
 *
 * @author A. Lapaev
 * @version 1.0
 */
public class FFT {

    /*
     * Введем некоторые обозначения, используемые в комментариях:
     * Пусть <i_0 ... i_{k-1}> - запись числа i в двоичной системе счисления.
     * a^b - число a в степени b.
     * a mod b - остаток от деления числа a на число b.
     * lg(a) - логарифм a по основанию 2.
     */
    //================================data======================================
    public int n; //количество точек, на котором будет выполняться
    //преобразование Фурье

    public int twoDeg;//twoDeg = lg(n)
    public int p = 40961; //модуль, по которому производятся вычисления

    public int primitiveRoot = 3; //примитивный корень из 1 в кольце Z_p

    public int alpha; //корень из 1 степени n

    public int[] reverse; //reverse[<i_0 ... i_{k-1}>] = <i_{k-1} ... i_0>

    int[] alphaDegrees; //степени переменной alpha модулю p. При этом
    //alphaDegrees[i] = alpha^i, i=0...n;

    public final static long LONG_MASK = 0xffffffffL;//маска для приведения к типу long
    /**
     * В каждой строке находится простое число p, степень 2, которую содержит
     * p-1 и примитивный корень из 1 к кольце Z_p.
     */
    int[][] prdata = {
        {40961, 13, 3},
        {2130706433, 24, 3},
        {2146959361, 19, 19},
        {2132279297, 19, 5},
        {2142502913, 18, 3},
        {2135162881, 18, 7},
        {2134638593, 18, 3},
        {2130444289, 18, 17},
        {2125725697, 18, 5},
        {2147352577, 17, 5},
        {2146041857, 17, 3},
        {2144468993, 17, 3},
        {2135818241, 17, 3},
        {2135031809, 17, 3},
        {2128740353, 17, 3},
        {2126118913, 17, 5},
    };
    /**
     * Каждая строка массива содержит простое число p, примитивный корень из
     * единицы и корни из 1 степени n, причем alphas[][i] = корень из
     * единицы степени 2^i
     */
    int[][] alphas = FFTData.primes;
    //=================================end data section=========================
    //==============================constructors================================

    public FFT() {
    }

    /**
     * Пусть x = <x_{n-1}, ..., x_0>. Тогда res=<x_0,..., x_{n-1}>
     * @param x
     * @return
     */


    //--------------------------------------------------------------------------
    /**
     * Вычисление корня из 1 степени n в кольце Z_p
     * @param n
     * @return
     */
    public int alphaDetermination(int pNum) {
        return alphas[pNum][twoDeg + 1];
    }

    //--------------------------------------------------------------------------
    /**
     * Табулирует степени alpha от 0 до n с записью результата
     * в массив alphaDegrees.
     * При этом alphaDegrees[i] = (alpha^i) mod p;
     */
    public void alphaDegreesTabulation(int n) {
        this.alphaDegrees = new int[n + 1];
        alphaDegrees[0] = 1;
        long pl = p & LONG_MASK;
        for (int i = 1; i <= n; i++) {
            alphaDegrees[i] = (int) mod(((alpha & LONG_MASK) * (alphaDegrees[i - 1] & LONG_MASK)), pl);

        }

    }

    /**
     * Возвращает преобразование Фурье для полинома Z_p[x,y]. Модуль p не
     * должен превосходить 2^15.
     * @param seq
     * @return
     */
    public int[][] fft(int[][] seq) {
        int[][][] res = new int[2][n][n];
        int trigger = 1,
                mask_f = 1,
                mask_omega = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[0][i][j] = seq[reverse[i]][reverse[j]];
            }
        }

        for (int i = 0; i < twoDeg; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    res[trigger][j][k] = (res[trigger ^ 1][j][k & (~mask_f)] +
                            res[trigger ^ 1][j][k | mask_f] *
                            alphaDegrees[(1 << (twoDeg - i - 1)) *
                            (k & mask_omega)]) % p;
                }
            }
            trigger ^= 1;
            mask_f <<= 1;
            mask_omega = (mask_omega << 1) ^ 1;

        }
        mask_f = 1;
        mask_omega = 1;

        for (int i = 0; i < twoDeg; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    res[trigger][j][k] = (res[trigger ^ 1][j & (~mask_f)][k] +
                            res[trigger ^ 1][j | mask_f][k] *
                            alphaDegrees[(1 << (twoDeg - i - 1)) *
                            (j & mask_omega)]) % p;
                }
            }


            trigger ^= 1;
            mask_f <<= 1;
            mask_omega = (mask_omega << 1) ^ 1;

        }
        return res[trigger ^ 1];
    }

    /**
     * Возвращает обратное преобразование Фурье для полинома из Z_p[x,y]
     * @param seq
     * @return
     */
    public int[][] fft_inverse(int[][] seq) {
        int[][][] res = new int[2][n][n];
        int trigger = 1,
                mask_f = 1,
                mask_omega = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[0][i][j] = seq[reverse[i]][reverse[j]];
            }
        }

        for (int i = 0; i < twoDeg; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    res[trigger][j][k] = (res[trigger ^ 1][j][k & (~mask_f)] +
                            res[trigger ^ 1][j][k | mask_f] *
                            alphaDegrees[n - ((1 << (twoDeg - i - 1)) *
                            (k & mask_omega))]) % p;
                }
            }
            trigger ^= 1;
            mask_f <<= 1;
            mask_omega = (mask_omega << 1) ^ 1;

        }




        mask_f = 1;
        mask_omega = 1;

        for (int i = 0; i < twoDeg - 1; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    res[trigger][j][k] = (res[trigger ^ 1][j & (~mask_f)][k] +
                            res[trigger ^ 1][j | mask_f][k] *
                            alphaDegrees[n - ((1 << (twoDeg - i - 1)) *
                            (j & mask_omega))]) % p;
                }
            }
            trigger ^= 1;
            mask_f <<= 1;
            mask_omega = (mask_omega << 1) ^ 1;

        }

        int inv = p - ((p - 1) >>> twoDeg);
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                res[trigger][j][k] = (((res[trigger ^ 1][j & (~mask_f)][k] +
                        res[trigger ^ 1][j | mask_f][k] *
                        alphaDegrees[n - (j & mask_omega)]) % p) * inv) %
                        p * inv % p;
            }
        }


        return res[trigger];
    }

    //--------------------------------------------------------------------------
    /**
     * Возвращает n mod m в отрезке [0, m-1]
     * @param n
     * @param m
     * @return
     */
    public static long mod(long n, long m) {
        long res = n % m;
        return res >= 0 ? res : m + res;
    }
    //опеделение n: n=2^k &&  n > val > n/2

    public int determineN(int val) {
        int k = -1;
        int fl = 0;
        while (val > 0) {
            fl += (val & 1) == 1 ? 1 : 0;
            val >>>= 1;
            k++;
        }
        k += fl > 1 ? 1 : 0;
        twoDeg = k;
        return 1 << k;

    }

    /**
     * Инициализация структур данных для вычисление сверток
     * по модулю alphas[pNum][0]
     * @param pNum -- номер строки в массиве alphas
     */
    public void init(int pNum) {
        alpha = alphaDetermination(pNum);
        alphaDegreesTabulation(n);
    }

    //==========================================================================
    /**
     * Стандартное умножение двух чисел по модулю p.
     * @param v1
     * @param v2
     * @return
     */
    public int[] mulSMod(int[] v1, int[] v2) {
        int[] res = new int[v1.length + v2.length];
        for (int i = 0; i < v1.length; i++) {
            for (int j = 0; j < v2.length; j++) {
                res[i + j] = (res[i + j] + v1[i] * v2[j]) % p;
            }
        }
        return res;
    }

    //==========================================================================
    public int[] mulSMod(int[] v1, int[] v2, int pNum) {
        int[] res = new int[v1.length + v2.length];
        p = alphas[pNum][0];
        for (int i = 0; i < v1.length; i++) {
            for (int j = 0; j < v2.length; j++) {
                res[i + j] = (int) mod(((res[i + j] & LONG_MASK) +
                        mod((v1[i] & LONG_MASK) * (v2[j] & LONG_MASK),
                        p & LONG_MASK)), p & LONG_MASK);
            }
        }
        return res;
    }

    //==========================================================================
    public NumberZ[] mulS(NumberZ[] v1, NumberZ[] v2) {
        NumberZ[] res = new NumberZ[v1.length + v2.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = NumberZ.ZERO;
        }
        for (int i = 0; i < v1.length; i++) {
            for (int j = 0; j < v2.length; j++) {
                res[i + j] = res[i + j].add(v1[i].multiply(v2[j]));
            }
        }
        return res;
    }
    //==========================================================================

    /**
     * Удаление начальных нулей в массиве v.
     * @param v
     * @return
     */
    @SuppressWarnings("empty-statement")
    public int[] delLeadZeroes(int[] v) {
        int i = v.length;
        while (i > 0 && v[--i] == 0) {
            ;
        }
        i++;
        if (i < v.length) {
            int[] v_norm = new int[i];
            System.arraycopy(v, 0, v_norm, 0, i);
            return v_norm;
        } else {
            return v;
        }

    }

    //==========================================================================
    /**
     * Стандартное умножение по модулю p с удалением начальных нулей в
     * результате.
     * @param v1
     * @param v2
     * @return
     */
    public int[] mulSModDelLeadZeros(int[] v1, int[] v2) {
        return delLeadZeroes(mulSMod(v1, v2));
    }

    public int[] mulSModDelLeadZeros(int[] v1, int[] v2, int pNum) {
        return delLeadZeroes(mulSMod(v1, v2, pNum));
    }

    //==========================================================================
    /**
     * Проверка равенства двух массивов с удалением начальных нулей.
     * @param v1
     * @param v2
     * @return
     */
    public boolean arraysIsEqDelLeadZeroes(int[] v1, int[] v2) {
        int v1_norm[] = delLeadZeroes(v1),
                v2_norm[] = delLeadZeroes(v2);
        return arraysIsEq(v1, v2);

    }

    //==========================================================================
    /**
     * Проверка равенства двух массивов.
     * @param v1
     * @param v2
     * @return
     */
    public boolean arraysIsEq(int[] v1, int[] v2) {
        if (v1.length != v2.length) {
            return false;
        }
        int i = 0;
        while (i < v1.length && v1[i] == v2[i]) {
            i++;
        }
        return i == v1.length;
    }

    //==========================================================================
    /**
     * Вычисляет быстрое пребразование Фурье. Модуль p может быть 31-битным.
     * @param v
     * @return
     */
    public int[] fft(int[] v) {
        int[][] res = new int[2][v.length];
        for (int i = 0; i < n; i++) {
            res[0][i] = v[reverse[i]];
        }
        int trigger = 1;
        int add = 1;
        long pl = p & LONG_MASK;
        for (int i = 0; i < twoDeg; i++) {
            for (int first = 0; first < (v.length >>> (i + 1)); first++) {
                for (int last = 0; last < 1 << i; last++) {
                    int ind1 = (first << (i + 1)) + last;
                    int ind2 = (first << (i + 1)) + add + last;
                    long add1 = (res[trigger ^ 1][ind1] & LONG_MASK);
                    long add2 = (res[trigger ^ 1][ind2] & LONG_MASK) *
                            (alphaDegrees[(last << (twoDeg - i - 1))] & LONG_MASK);
                    res[trigger][ind1] = (int) mod(add1 + add2, pl);
                    res[trigger][ind2] = (int) mod(add1 - add2, pl);
                }
            }
            trigger ^= 1;
            add <<= 1;
        }
        return res[trigger ^ 1];
    }
    //==========================================================================

    /**
     * Вычисляет обратное преобразование Фурье. Модуль p не должен
     * превосходить 2^31-1.
     * @param v
     * @param prime
     * @return
     */
    public int[] fftInv(int[] v) {
        int[][] res = new int[2][v.length];
        for (int i = 0; i < n; i++) {
            res[0][i] = v[reverse[i]];
        }
        int trigger = 1;
        int add = 1;
        long pl = p & LONG_MASK;
        for (int i = 0; i < twoDeg - 1; i++) {
            for (int first = 0; first < (v.length >>> (i + 1)); first++) {
                for (int last = 0; last < 1 << i; last++) {
                    int ind1 = (first << (i + 1)) + last;
                    int ind2 = (first << (i + 1)) + add + last;
                    long add1 = (res[trigger ^ 1][ind1] & LONG_MASK);
                    long add2 = (res[trigger ^ 1][ind2] & LONG_MASK) *
                            (alphaDegrees[n - (last << (twoDeg - i - 1))] & LONG_MASK);
                    res[trigger][ind1] = (int) mod(add1 + add2, pl);
                    res[trigger][ind2] = (int) mod(add1 - add2, pl);
                }
            }
            trigger ^= 1;
            add <<= 1;
        }
        int inv = p - ((p - 1) >>> twoDeg);
        for (int first = 0; first < (v.length >>> (twoDeg)); first++) {
            for (int last = 0; last < 1 << (twoDeg - 1); last++) {
                int ind1 = (first << (twoDeg)) + last;
                int ind2 = (first << (twoDeg)) + add + last;
                long add1 = (res[trigger ^ 1][ind1] & LONG_MASK);
                long add2 = (res[trigger ^ 1][ind2] & LONG_MASK) *
                        (alphaDegrees[n - last] & LONG_MASK);
                res[trigger][ind1] = (int) mod(mod(add1 + add2, pl) * inv, pl);
                res[trigger][ind2] = (int) mod(mod(add1 - add2, pl) * inv, pl);

            }
        }
        return res[trigger];
    }

    //==========================================================================
    /**
     * Вычисление корней из 1 по модулям prdata[][0]
     */
    public void computeAlphas() {
        computeAlphas(prdata);
    }

    public void computeAlphas(int[][] prdata) {
        for (int k = 0; k < prdata.length; k++) {
            int p1 = prdata[k][0];
            int primitiveRoot_ = prdata[k][2];
            System.out.print("{" + p1 + ", " + primitiveRoot_ + ", ");
            System.out.print(p1 - 1 + ",");
            for (int n_ = 2; n_ <= prdata[k][1]; n_++) {
                int res = 1;
                for (int i = 0; i < (p1 - 1) / (1 << n_); i++) {
                    res = (int) (((res & LONG_MASK) *
                            (primitiveRoot_ & LONG_MASK)) % (p1 & LONG_MASK));
                }
                System.out.print(res + (n_ < prdata[k][1] ? "," : ""));
            }
            System.out.print("}");
            if (k < prdata.length - 1) {
                System.out.println(",");
            }
        }
    }
    //==========================================================================

    public int[] multiplyFMod(int[] v1, int[] v2, int pNum) {
        p = alphas[pNum][0];
        init(pNum);
        return delLeadZeroes(__multiplyFMod(v1, v2));
    }

    //==========================================================================
    public int[] multiplyFModBI(int[] v1, int[] v2, int pNum) {
        p = alphas[pNum][0];
        alpha = alphaDetermination(pNum);
        alphaDegreesTabulation(n);
        return __multiplyFMod(v1, v2);
    }

    //============================================================================
    public int[] __multiplyFMod(int[] v1, int[] v2) {
        int res[] = new int[n];
        int v1_transformed[] = fft(v1),
                v2_trasformed[] = fft(v2);

        for (int i = 0; i < n; i++) {
            res[i] = (int) (((v1_transformed[i] & LONG_MASK) *
                    (v2_trasformed[i] & LONG_MASK)) % (p & LONG_MASK));
        }
        res = fftInv(res);
        return res;
    }

    //==========================================================================
    /** Нахождение индекса максимально элемента в массиве v
     * @param v
     * @return
     */
    int maxCoeff(NumberZ[] v) {
        int maxInd = 0;
        for (int i = 1; i < v.length; i++) {
            if (v[maxInd].abs().compareTo(v[i].abs()) == -1) {
                maxInd = i;
            }
        }
        return maxInd;
    }






    public void init(int n, int t) {
        this.n = n;
                p = alphas[t][0];
        init(t);
        reverse = new int[n];

        for (int i = 0; i < n; i++) {
            reverse[i] = FFTData.intReverse(i,twoDeg);
        }
    }

   public int[] fft(NumberZ[] v) {
        int[] v_ = new int[n];
        NumberZ bp = NumberZ.valueOf(p);
        for (int i = 0; i < v.length; i++) {
            NumberZ tmp =   v[i].mod(bp);
            v_[i] = tmp.mag.length == 0?0:(int) v[i].mod(bp).mag[0];

        }
        return fft(v_);
    }


   public int[] fft(Polynom v) {
        int[] v_ = new int[n];
        NumberZ bp = NumberZ.valueOf(p);
        for (int i = 0; i < v.coeffs.length; i++)
            v_[v.powers.length>0?v.powers[i]:0] = ((NumberZ)(v.coeffs[i].mod(bp, com.mathpar.number.Ring.ringR64xyzt))).intValue();
        return fft(v_);
    }

    public int[] fftIntInvDelLeadZeroes(int[] v) {
        int[] v_ = new int[n];
        NumberZ bp = NumberZ.valueOf(p);
        for (int i = 0; i < v.length; i++)
            v_[i] = v[i];
        return delLeadZeroes(fftInv(v_));
    }

       public int[] fftIntInv(int[] v) {
        int[] v_ = new int[n];
        NumberZ bp = NumberZ.valueOf(p);
        for (int i = 0; i < v.length; i++)
            v_[i] = v[i];
        return fftInv(v_);
    }

        public int[] fftInt(int[] v) {
        int[] v_ = new int[n];
        NumberZ bp = NumberZ.valueOf(p);
      //  for (int i = 0; i < v.length; i++)
        //    v_[i] = v[i];
        return delLeadZeroes(fft(v));
    }

     public void init(int t, FFTData fd) {
        this.n = fd.n;
        this.twoDeg = fd.log2n;
        p = alphas[t][0];
        //init(t);
        reverse = fd.reverse;
        alphaDegrees = fd.alphas[t];


    }


    //=========================end public methods section=======================
}
