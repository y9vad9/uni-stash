
package com.mathpar.number;
import com.mathpar.polynom.*;

/**
 * Класс арифметики сверток полиномов одной переменной, полученных при
 * дискретном преобразовании Фурье в кольце Z_p. Позволяет заменить
 * арифметические действия над полиномами действиями над их
 * свертками.
 * @author Lapaev Aleksey
 */
public class FFTPolZ extends Element implements Cloneable{

    FFTData fd;
    /**
     * Возвращает копию this.
     * @return
     */
    @Override
    public Object clone() {

        return new FFTPolZ((int[][])transvections.clone(), this.fd);
    }

    int n; //количество точек, на которых производится свертка

    int modulesAmount;//количество модулей
    //int[] modules;//модули
    public int[][] transvections;//свертки
    final long LONG_MASK = 0xffffffffL;

    NumberZ module;
    //==========================constructors====================================

    /**
     * Создает представителя класса из Polynom
     * @param pol - полином, который надо представить в виде свертки
     * @param n - количество точек для вычисления свертки
     * @param modulesAmount - количество модулей, по которым будут производиться
     * вычисления
     *
     */
    public FFTPolZ(Polynom pol, int n, int modulesAmount) {
        this.modulesAmount = modulesAmount;
        FFT f = new FFT();
        this.n = f.determineN(n);
        transvections = new int[modulesAmount][];
        for (int i = 0; i < modulesAmount; i++) {
            f.init(this.n, i);
            transvections[i] = f.fft(pol);
        //  transvections[i] = f.fftInv(transvections[i]);
        }

    }

      /**
     * Конструирует класс FFTPolZ
     * @param pol - полином
     * @param n - количество точек, на которой вычисляется преобразование Фурье
     * @param module - число, которые должно быть меньше
     * произведения всех простых модулей, в кольцах которых
     * считается ФФТ.
     *
     */

    public FFTPolZ(Polynom pol, int n, NumberZ module) {
        NumberZ tmpModule = NumberZ.ONE;
        int[][] ps =  FFTData.primes;
        NumberZ simmod = module.shiftLeft(1);
        int c = 0;
        try {
        while (tmpModule.compareTo(simmod) < 1) {
            tmpModule = tmpModule.multiply(NumberZ.valueOf(ps[c++][0]));
        }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("Not enough modules");
        }
        this.modulesAmount = c;
        FFT f = new FFT();
        this.n = f.determineN(n);
        transvections = new int[modulesAmount][];
        for (int i = 0; i < modulesAmount; i++) {
            f.init(this.n, i);
            transvections[i] = f.fft(pol);
        //  transvections[i] = f.fftInv(transvections[i]);
        }
        this.module = tmpModule;
    }

    private static FFT t = new FFT();
    public static FFTPolZ zero(int n, int modulesAmount) {
      //  return new FFTPolZ(new Polynom(new int[]{0},
        //        new BigInteger[]{BigInteger.ZERO}), n, modulesAmount);
        return new FFTPolZ(new int[modulesAmount][t.determineN(n)]);
    }
     public static FFTPolZ one(int n, int modulesAmount) {
        return new FFTPolZ(new Polynom(new int[]{0},
                new NumberZ[]{NumberZ.ONE}), n, modulesAmount);
    }

    public static FFTPolZ zero(FFTData fd) {
      //  return new FFTPolZ(new Polynom(new int[]{0},
        //        new BigInteger[]{BigInteger.ZERO}), n, modulesAmount);
        return new FFTPolZ(new int[fd.r][fd.n]);
    }
     public static FFTPolZ one(FFTData fd) {
        return new FFTPolZ(new Polynom(new int[]{0},
                new NumberZ[]{NumberZ.ONE}), fd);
    }



    /**
     * Получение количества модулей, необходимого для мажорирования 2^bits.
     * Модули берутся из math.FFTData.primes[][0].
     * @param bits
     * @return количество модулей
     */

    public static int getModulesAmount(int bits) {
        NumberZ tmpModule = NumberZ.ONE;
        int[][] ps =  FFTData.primes;
        NumberZ simmod = NumberZ.ONE.shiftLeft(bits+1);
        int c = 0;
        try {
        while (tmpModule.compareTo(simmod) < 1) {
            tmpModule = tmpModule.multiply(NumberZ.valueOf(ps[c++][0]));
        }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("Not enough modules");
        }
        return c;
    }



    //--------------------------------------------------------------------------
    /**
     * Конструктор класса из уже вычисленных сверток. Не производится никаких
     * проверок
     * @param transvections - свертки полинома
     */
    public FFTPolZ(int[][] transvections, FFTData fd) {
        this.transvections = transvections;
        modulesAmount = transvections.length;
        n = transvections[0].length;
        this.fd = fd;
    }

    public FFTPolZ(int[][] transvections) {
        this.transvections = transvections;
        modulesAmount = transvections.length;
        n = transvections[0].length;
        //this.fd = fd;
    }

    //============================public methods================================
    //-----------------------arithmetics----------------------------------------
    /**
     * Метод, выполняющий проверку на допустимость арифметических
     * операций между свертками a и  this
     */
    private void check(FFTPolZ a) {
        //количество использованных модулей в a и this должно быть равным
        if (a.modulesAmount != this.modulesAmount) {
            throw new RuntimeException("Modules amount is differ: " +
                    a.modulesAmount + " and " + modulesAmount);
        }
        //количечество точек так же должно быть равным
        if (a.n != this.n) {
            throw new RuntimeException("N is differ: " +
                    a.n + " and " + n);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Прибавление свертки a  к свертке this
     */
    public FFTPolZ add(FFTPolZ a) {
        check(a);
        int[][] res = new int[modulesAmount][n];
        for (int i = 0; i < modulesAmount; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = (int) FFT.mod((transvections[i][j] & LONG_MASK) +
                        (a.transvections[i][j] & LONG_MASK),
                        FFTData.primes[i][0]);
            }
        }
        return new FFTPolZ(res,fd);
    }

    //--------------------------------------------------------------------------
    /**
     * Вычитание их свертки this свертки a
     *
     * @param a
     * @return
     */
    public FFTPolZ subtract(FFTPolZ a) {
        check(a);
        int[][] res = new int[modulesAmount][n];
        for (int i = 0; i < modulesAmount; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = (int) FFT.mod((transvections[i][j] & LONG_MASK) -
                        (a.transvections[i][j] & LONG_MASK),
                        FFTData.primes[i][0]);
            }
        }
        return new FFTPolZ(res,fd);
    }

    //--------------------------------------------------------------------------
    /**
     * Умножение свертки a на свертку this
     */
    public FFTPolZ multiply(FFTPolZ a) {
        check(a);
        int[][] res = new int[modulesAmount][n];
        for (int i = 0; i < modulesAmount; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = (int) FFT.mod((transvections[i][j] & LONG_MASK) *
                        (a.transvections[i][j] & LONG_MASK),
                        FFTData.primes[i][0]);
            }
        }
        return new FFTPolZ(res,fd);
    }

    /**
     * Возвращает свертку исходного полинома, взятого с противоположным знаком.
     * @return
     */

     public FFTPolZ negate() {
        int[][] res = new int[modulesAmount][n];
        for (int i = 0; i < modulesAmount; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = FFTData.primes[i][0] - transvections[i][j];
            }
        }
        return new FFTPolZ(res,fd);
    }

    //--------------------------------------------------------------------------
    /**
     * Точное деление свертки this на свертку a.
     * @param a
     * @return
     */
    public FFTPolZ divideE(FFTPolZ a) {
        check(a);
        int[][] res = new int[modulesAmount][n];
        for (int i = 0; i < modulesAmount; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = (int) FFT.mod(
                        (transvections[i][j] & LONG_MASK) *
                        (NFunctionZ32.p_Inverse(a.transvections[i][j] & LONG_MASK,  FFTData.primes[i][0] & LONG_MASK)),
                        FFTData.primes[i][0]);
            }
        }

        return new FFTPolZ(res,fd);
    }



    public boolean isZero() {
        for (int i = 0; i < transvections.length; i++)
            for (int j = 0; j < transvections[0].length; j++)
                if (FFT.mod(transvections[i][j]&LONG_MASK,FFTData.primes[i][0] & LONG_MASK)!=0)
                    return false;
        return true;
    }



      /**
     * Создает представителя класса из Polynom
     * @param pol - полином, который надо представить в виде свертки
     * @param n - количество точек для вычисления свертки
     * @param modulesAmount - количество модулей, по которым будут производиться
     * вычисления
     *
     */
    public FFTPolZ(Polynom pol, FFTData fd) {
        this.modulesAmount = fd.r;
        FFT f = new FFT();
        this.n = fd.n;
        this.fd = fd;
       // this.n = f.determineN(n);
        transvections = new int[modulesAmount][];

        for (int i = 0; i < modulesAmount; i++) {
         //   f.init(this.n, i);
            f.init(i, fd);
            transvections[i] = f.fft(pol);
        //  transvections[i] = f.fftInv(transvections[i]);
        }

    }










     //===================================recovery===============================
    /**
     * Получение полинома по его свертке.
     * @return
     */
    public Polynom toPolynomcache(Lagrange recoverer) {
        FFT f = new FFT();
     //   f.determineN(n);
      //  int[] p = new int[modulesAmount];
        int[][] res = new int[modulesAmount][];
        for (int i = 0; i < modulesAmount; i++) {
            f.init(i, fd);
            res[i] = f.fftIntInvDelLeadZeroes(transvections[i]);
        }
      //  for (int i = 0; i < modulesAmount; i++) {
      //      p[i] =(int) FFTData.primes[i][0];
       // }
        int[] respow = new int[res[0].length];
        for (int i = 0; i < respow.length; i++)
            respow[i] = respow.length - i - 1;
        return new Polynom(respow, recoverer.recoveryLagrange(res));
    }

    /**
     * Получение полинома по его свертке с учетом знака коэффициентов.
     * @return
     */
    public Polynom toPolynomSignedCache(Lagrange recoverer) {
        Ring ring=Ring.ringR64xyzt;ring.setDefaulRing();
        Polynom tmp = toPolynomcache(recoverer);
        //if (module == null) {
            module = recoverer.module;
       //     for (int i = 0; i < this.modulesAmount; i++)
       //         module = module.multiply(BigInteger.valueOf(FFTData.primes[i][0]));
       // }
        NumberZ hm = module.shiftRight(1);
        for (int i = 0; i < tmp.coeffs.length; i++)
        if (tmp.coeffs[i].compareTo(hm,ring)>-1)
            tmp.coeffs[i] = tmp.coeffs[i].subtract(module,ring);
        return tmp;
    }


    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public boolean equals(Element x, Ring r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isZero(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isOne(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
