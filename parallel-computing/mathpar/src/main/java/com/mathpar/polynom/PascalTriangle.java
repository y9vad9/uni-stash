package com.mathpar.polynom;
import com.mathpar.number.Complex;
import com.mathpar.number.NumberR;
import com.mathpar.number.NumberZ;

/**
 * @author Yuri Valeev
 */
public class PascalTriangle {
    //Переменные для кэша в кольце Z
    private static int lastNZ; //номер последней вычисленной строки в
    //кэше pasTrgZ,
    private static int maxNZ=0; //текущий максимальный
    //номер строки в кэше pasTrgZ которая со
    private static NumberZ[][] pasTrgZ=null; //кэш (левая половина треугольника Паскаля)
    //для числового кольца Z,
    //pasTrgZ[i] - половина i-й строки обычного треугольника Паскаля, длина
    // i-й строки кэша= (i+2-i%2)/2
    private final static int MIN_ROWS_Z = 10;// число заполненных строк в матрице Паскаля
    // при инициалихзации
    private final static int MAX_ROWS_Z = 100;// Число строк в матрице Паскаля при инициализации
    private final static int ROWS_INC_Z = 1000;// На столько приростает число строк в тр Паскаля
    private final static int LIMIT_Z = 2101; // Недопустимо большое число строк в тр. Паскаля


    //Переменные для кэша в кольце R64
    private static int lastNR;
    private static int maxNR=0;
    private static int maxNR64=0;
    private static double[][] pasTrgDouble;
    private final static int MIN_ROWS_R = 10;
    private final static int MAX_ROWS_R = 1000;
    private final static int ROWS_INC_R = 1000;
    private final static int LIMIT_R = 2000;


    //Переменные для кэша в кольце Z_p (long)
    private static int lastNLp;
    private static int maxNLp=0;
    private static long[][] pasTrgLp;
    private static long module;
    private final static int MIN_ROWS_Lp = 10;
    private final static int MAX_ROWS_Lp = 1000;
    private final static int ROWS_INC_Lp = 1000;
    private final static int LIMIT_Lp = 2000;

   // private static NumberR[][] pasTrgRb;


    /* initCacheZ - создаем кэш pasTrgZ размером MAX_ROWS_Z = максимальное число строк
     * в кэше (i-я строка в кэше - это половина i-й строки треугольника Паскаля)
     * и вычислим в кэше строки от 0 до MIN_ROWS_Z-й включительно.
     */
    public static void initCacheZ() {if (maxNZ!=0) return;
        maxNZ = MAX_ROWS_Z; //установим максимальное nчисло строк в кэше Z
        pasTrgZ = new NumberZ[maxNZ + 1][0]; //создаем массив кэша pasTrgZ
        pasTrgZ[0] = new NumberZ[] {NumberZ.POSCONST[1]}; //вычисляем 0-ю строку
        pasTrgZ[1] = new NumberZ[] {NumberZ.ONE, NumberZ.ONE};
        lastNZ = 0; //записываем номер последней вычисленной строки=0
        growPasTrgZ(MIN_ROWS_Z); //вычисляем строки от 1 до MIN_ROWS_Z с помощью
        //метода growPasTrgZ
    }


    /* growPasTrgZ - вычисляет все строки кэша pasTrgZ до newLastN-й включительно,
     * пропуская уже вычисленные строки (т.е. если newLastN-я строка уже вычислена,
     * то выход, а если нет, то начать вычислять строки с последней вычисленной
     * строки).
     * Параметры:
     * newLastN - строка, до которой нужно вычислить все невычисленные строки в кэше
     */
    public static void growPasTrgZ(int newLastN) {
        if (newLastN > maxNZ) { //если мы хотим вычислить больше строк, чем может
            //поместится в кэше, то создаем новый кэш большего размера
            maxNZ = newLastN + ROWS_INC_Z;
            if (maxNZ > LIMIT_Z) {
                throw new ArithmeticException(
                        "growPasTrgZ: WARNING: It was requested to calculate more than limit=" +
                        LIMIT_Z +
                        " rows of Pascal triangle. Operation refused.");
            }
            NumberZ[][] oldPasTrgZ = pasTrgZ;
            pasTrgZ = new NumberZ[maxNZ][];

            System.arraycopy(oldPasTrgZ, 0, pasTrgZ, 0, lastNZ + 1);
        }
        if (newLastN > lastNZ) { //еслм мы хотим вычислить
            //строки, которых нет в кэше, то
            int nStrLen = pasTrgZ[lastNZ].length; //иначе находим длину последней
            //вычисленной строки
            for (int n = lastNZ + 1; n <= newLastN; n++) { //и начинаем вычисление
                //со следующей после последней
                if (n % 2 == 1) { //если номер строки нечетный, то
                    //при переходе от строки с четным номером к
                    //строке с нечетным, размер половины строки
                    //треугольника Паскаля не меняется, т.е.
                    //не меняется nStrLen
                    pasTrgZ[n] = new NumberZ[nStrLen]; //создаем n-ю строку кэша
                    //размером nStrLen
                    pasTrgZ[n][0] = NumberZ.POSCONST[1]; //0-й элемент=1
                    for (int k = 1; k < nStrLen; k++) { //вычислим n-ю строку через (n-1)-ю
                        pasTrgZ[n][k] = pasTrgZ[n - 1][k - 1].add(pasTrgZ[n - 1][k]);
                    }
                } else { //иначе если номер строки четный, то
                    //при переходе от строки с нечетным номером к
                    //строке с четным, размер половины строки
                    //треугольника Паскаля увеличится на 1, т.е.
                    //nStrLen++
                    nStrLen++;
                    pasTrgZ[n] = new NumberZ[nStrLen]; //создаем n-ю строку кэша
                    //размером nStrLen
                    pasTrgZ[n][0] = NumberZ.POSCONST[1]; //0-й элемент=1
                    for (int k = 1; k < nStrLen-1; k++) { //вычислим n-ю строку кроме
                        //последнего элемента через (n-1)-ю
                        pasTrgZ[n][k] = pasTrgZ[n - 1][k - 1].add(pasTrgZ[n - 1][k]);
                    }
                    //последний элемент n-й строки в 2 раза больше последнего элемента
                    //(n-1)-й строки
                    pasTrgZ[n][nStrLen - 1] =  pasTrgZ[n - 1][nStrLen - 2].shiftLeft(1);
                }
            }
            //Вычислив все невычисленные строки кэша от 0 до lastN, установим
            //новый номер последней вычисленной строки кэша
            lastNZ = newLastN;
        }
    }


    /* binomialZ - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgZ.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     */
    public static NumberZ binomialZ(int n, int k) {
        if (k < pasTrgZ[n].length) { //если k-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, то
            return pasTrgZ[n][k]; //извлечь из n-й строки кэша k-й элемент
        } else { //иначе воспользуемся формулой:
            //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, тогда
            return pasTrgZ[n][n - k]; //извлечь из n-й строки кэша (n-k)-й элемент
        }
    }


    /* binomialFR - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgZ.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     * /
       public static Fraction binomialFR(int n,int k){
      if (k < pasTrgZ[n].length) { //если k-й элемент попадает в левую половину
                                  //n-й строки треугольника Паскаля, то
        return new Fraction(pasTrgZ[n][k]);      //извлечь из n-й строки кэша k-й элемент
      } else {                    //иначе воспользуемся формулой:
        //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
        //n-й строки треугольника Паскаля, тогда
        return new Fraction(pasTrgZ[n][n - k]);  //извлечь из n-й строки кэша (n-k)-й элемент
      }
       }

     */





    /* initCacheR64 - создаем кэш pasTrgZ размером MAX_ROWS_R = максимальное число строк
     * в кэше (i-я строка в кэше - это половина i-й строки треугольника Паскаля)
     * и вычислим в кэше строки от 0 до MIN_ROWS_R-й включительно.
     */
    public static void initCacheR64() {if (maxNR64!=0) return;
        initCacheZ();
        maxNR64 = MAX_ROWS_R; //установим максимальное число строк в кэше R
        pasTrgDouble = new double[maxNR + 1][]; //создаем массив кэша pasTrgDouble
        pasTrgDouble[0] = new double[1]; //вычисляем 0-ю строку
        pasTrgDouble[0][0] = 1;
        lastNR = 0; //записываем номер последней вычисленной строки=0
        growPasTrgR64(MIN_ROWS_R); //вычисляем строки от 1 до MIN_ROWS_R с помощью
        //метода growPasTrgR64
    }


//    /* initCacheR64 - создаем кэш pasTrgZ размером MAX_ROWS_R = максимальное число строк
//     * в кэше (i-я строка в кэше - это половина i-й строки треугольника Паскаля)
//     * и вычислим в кэше строки от 0 до MIN_ROWS_R-й включительно.
//     */
//    public static void initCacheR() {if (maxNR!=0) return;
//        maxNR = MAX_ROWS_R; //установим максимальное число строк в кэше R
//        pasTrgRb = new NumberR[maxNR + 1][]; //создаем массив кэша pasTrgDouble
//        pasTrgRb[0] = new NumberR[1]; //вычисляем 0-ю строку
//        pasTrgRb[0][0] = NumberR.ONE;
//        lastNR = 0; //записываем номер последней вычисленной строки=0
//        growPasTrgR(MIN_ROWS_R); //вычисляем строки от 1 до MIN_ROWS_R с помощью
//        //метода growPasTrgR64
//    }


    /* growPasTrgR64 - вычисляет все строки кэша pasTrgDouble до newLastN-й включительно,
     * пропуская уже вычисленные строки (т.е. если newLastN-я строка уже вычислена,
     * то выход, а если нет, то начать вычислять строки с последней вычисленной
     * строки).
     * Параметры:
     * newLastN - строка, до которой нужно вычислить все невычисленные строки в кэше
     */
    public static void growPasTrgR64(int newLastN) {
        if (newLastN > maxNR) { //если мы хотим вычислить больше строк, чем может
            //поместится в кэше, то создаем новый кэш большего размера
            maxNR = newLastN + ROWS_INC_R;
            if (maxNR > LIMIT_R) {
                throw new ArithmeticException(
                        "growPasTrgR: WARNING: It was requested to calculate more than limit=" +
                        LIMIT_R +
                        " rows of Pascal triangle. Operation refused.");
            }
            double[][] oldPasTrgR64 = pasTrgDouble;
            pasTrgDouble = new double[maxNR][];
            System.arraycopy(oldPasTrgR64, 0, pasTrgDouble, 0, lastNR + 1);
        }
        if (newLastN > lastNR) { //еслм мы хотим вычислить
            //строки, которых нет в кэше, то
            growPasTrgZ(newLastN);
            for (int i = lastNR; i <= newLastN; i++) {
                pasTrgDouble[i] = new double[pasTrgZ[i].length];
                for (int j = 0; j < pasTrgZ[i].length; j++) {
                    pasTrgDouble[i][j] = pasTrgZ[i][j].doubleValue();
                }
            }
            lastNR = newLastN;
        }
    }


    /* binomialR64 - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgDouble.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     */
    public static double binomialR64(int n, int k) {
        if (k < pasTrgDouble[n].length) { //если k-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, то
            return pasTrgDouble[n][k]; //извлечь из n-й строки кэша k-й элемент
        } else { //иначе воспользуемся формулой:
            //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, тогда
            return pasTrgDouble[n][n - k]; //извлечь из n-й строки кэша (n-k)-й элемент
        }
    }


    /* binomialC64 - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgDouble.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     */
    public static Complex binomialC64(int n, int k) {
        if (k < pasTrgDouble[n].length) { //если k-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, то
            return new Complex((int)pasTrgDouble[n][k]); //извлечь из n-й строки кэша k-й элемент
        } else { //иначе воспользуемся формулой:
            //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, тогда
            return new Complex((int)pasTrgDouble[n][n - k]); //извлечь из n-й строки кэша (n-k)-й элемент
        }
    }


    /* initCacheLp32 - создаем кэш pasTrgZ размером MAX_ROWS_Lp = максимальное число строк
     * в кэше (i-я строка в кэше - это половина i-й строки треугольника Паскаля)
     * и вычислим в кэше строки от 0 до MIN_ROWS_Lp-й включительно.
     */
    public static void initCacheLp32(long mod) {
        if (mod != module) {
            module = mod;
            maxNLp = MAX_ROWS_Lp; //установим максимальное число строк в кэше Z_p(Long)
            pasTrgLp = new long[maxNLp + 1][]; //создаем массив кэша pasTrgZp
            pasTrgLp[0] = new long[1]; //вычисляем 0-ю строку
            pasTrgLp[0][0] = 1;
            lastNLp = 0; //записываем номер последней вычисленной строки=0
            growPasTrgLp32(MIN_ROWS_Lp, mod); //вычисляем строки от 1 до MIN_ROWS_Lp с помощью
            //метода growPasTrgLp32
        }
    }


    /* growPasTrgLp32 - вычисляет все строки кэша pasTrgLp до newLastN-й включительно,
     * пропуская уже вычисленные строки (т.е. если newLastN-я строка уже вычислена,
     * то выход, а если нет, то начать вычислять строки с последней вычисленной
     * строки).
     * Параметры:
     * newLastN - строка, до которой нужно вычислить все невычисленные строки в кэше
     * mod      - модуль кольца Z_p(Long)
     */
    public static void growPasTrgLp32(int newLastN, long mod) {
        if (newLastN > maxNLp) { //если мы хотим вычислить больше строк, чем может
            //поместится в кэше, то создаем новый кэш большего размера
            maxNLp = newLastN + ROWS_INC_Lp;
            if (maxNLp > LIMIT_Lp) {
                throw new ArithmeticException(
                        "growPasTrgLp: WARNING: It was requested to calculate more than limit=" +
                        LIMIT_Lp +
                        " rows of Pascal triangle. Operation refused.");
            }
            long[][] oldPasTrgLp = pasTrgLp;
            pasTrgLp = new long[maxNLp][];
            System.arraycopy(oldPasTrgLp, 0, pasTrgLp, 0, lastNLp + 1);
        }
        if (newLastN > lastNLp) { //еслм мы хотим вычислить
            //строки, которых нет в кэше, то
            int nStrLen = pasTrgLp[lastNLp].length; //иначе находим длину последней
            //вычисленной строки
            for (int n = lastNLp + 1; n <= newLastN; n++) { //и начинаем вычисление
                //со следующей после последней
                if (n % 2 == 1) { //если номер строки нечетный, то
                    //при переходе от строки с четным номером к
                    //строке с нечетным, размер половины строки
                    //треугольника Паскаля не меняется, т.е.
                    //не меняется nStrLen
                    pasTrgLp[n] = new long[nStrLen]; //создаем n-ю строку кэша
                    //размером nStrLen
                    pasTrgLp[n][0] = 1; //0-й элемент=1
                    for (int k = 1; k < nStrLen; k++) { //вычислим n-ю строку через (n-1)-ю
                        pasTrgLp[n][k] = (pasTrgLp[n - 1][k - 1] + pasTrgLp[n -
                                          1][k]) % mod;
                    }
                } else { //иначе если номер строки четный, то
                    //при переходе от строки с нечетным номером к
                    //строке с четным, размер половины строки
                    //треугольника Паскаля увеличится на 1, т.е.
                    //nStrLen++
                    nStrLen++;
                    pasTrgLp[n] = new long[nStrLen]; //создаем n-ю строку кэша
                    //размером nStrLen
                    pasTrgLp[n][0] = 1; //0-й элемент=1
                    for (int k = 1; k < nStrLen - 1; k++) { //вычислим n-ю строку кроме
                        //последнего элемента через (n-1)-ю
                        pasTrgLp[n][k] = (pasTrgLp[n - 1][k - 1] +
                                          pasTrgLp[n - 1][k]) % mod;
                    }
                    //последний элемент n-й строки в 2 раза больше последнего элемента
                    //(n-1)-й строки
                    pasTrgLp[n][nStrLen -
                            1] = (pasTrgLp[n - 1][nStrLen - 2] * 2) % mod;
                }
            }
            //Вычислив все невычисленные строки кэша от 0 до lastNZp, установим
            //новый номер последней вычисленной строки кэша
            lastNLp = newLastN;
        }
    }


    /* binomialLp32 - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgZ.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     */
    public static long binomialLp32(int n, int k) {
        if (k < pasTrgLp[n].length) { //если k-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, то
            return pasTrgLp[n][k]; //извлечь из n-й строки кэша k-й элемент
        } else { //иначе воспользуемся формулой:
            //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, тогда
            return pasTrgLp[n][n - k]; //извлечь из n-й строки кэша (n-k)-й элемент
        }
    }


    /* binomialR - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgZ.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     */
    public static NumberR binomialR(int n, int k) {
        if (k < pasTrgZ[n].length) { //если k-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, то
            return new NumberR(pasTrgZ[n][k]); //извлечь из n-й строки кэша k-й элемент
        } else { //иначе воспользуемся формулой:
            //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, тогда
            return new NumberR(pasTrgZ[n][n - k]); //извлечь из n-й строки кэша (n-k)-й элемент
        }
    }


    /* binomialСb - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgZ.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     */
    public static Complex binomialC(int n, int k) {
        if (k < pasTrgZ[n].length) { //если k-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, то
            return new Complex(pasTrgZ[n][k],NumberZ.ZERO); //извлечь из n-й строки кэша k-й элемент
        } else { //иначе воспользуемся формулой:
            //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, тогда
            return new Complex(pasTrgZ[n][n - k],NumberZ.ZERO); //извлечь из n-й строки кэша (n-k)-й элемент
        }
    }


    /* binomialZi - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgDouble.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     * /
      public static Gauss binomialZi(int n,int k){
     if (k < pasTrgDouble[n].length) { //если k-й элемент попадает в левую половину
                                    //n-й строки треугольника Паскаля, то
     return new Gauss(pasTrgZ[n][k]);      //извлечь из n-й строки кэша k-й элемент
        } else {                    //иначе воспользуемся формулой:
          //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
          //n-й строки треугольника Паскаля, тогда
          return new Gauss(pasTrgZ[n][n - k]);  //извлечь из n-й строки кэша (n-k)-й элемент
        }
         }
     */

//    public static void growPasTrgR(int newLastN) {
//        if (newLastN > maxNR) { //если мы хотим вычислить больше строк, чем может
//            //поместится в кэше, то создаем новый кэш большего размера
//            maxNR = newLastN + ROWS_INC_R;
//            if (maxNR > LIMIT_R) {
//                throw new ArithmeticException(
//                        "growPasTrgR: WARNING: It was requested to calculate more than limit=" +
//                        LIMIT_R +
//                        " rows of Pascal triangle. Operation refused.");
//            }
//            NumberR[][] oldPasTrgRb = pasTrgRb;
//            pasTrgRb = new NumberR[maxNR][];
//            System.arraycopy(oldPasTrgRb, 0, pasTrgRb, 0, lastNR + 1);
//        }
//        if (newLastN > lastNR) { //еслм мы хотим вычислить
//            //строки, которых нет в кэше, то
//            growPasTrgZ(newLastN);
//            for (int i = lastNR; i <= newLastN; i++) {
//                pasTrgRb[i] = new NumberR[pasTrgZ[i].length];
//                for (int j = 0; j < pasTrgZ[i].length; j++) {
//                    pasTrgRb[i][j] = new NumberR(pasTrgZ[i][j]);
//                }
//            }
//            lastNR = newLastN;
//        }
//    }

    /* growPasTrgLp - вычисляет все строки кэша pasTrgLp до newLastN-й включительно,
     * пропуская уже вычисленные строки (т.е. если newLastN-я строка уже вычислена,
     * то выход, а если нет, то начать вычислять строки с последней вычисленной
     * строки).
     * Параметры:
     * newLastN - строка, до которой нужно вычислить все невычисленные строки в кэше
     * mod      - модуль кольца Z_p(Long)
     */
    public static void growPasTrgLp(int newLastN, long mod) {
        if (newLastN > maxNLp) { //если мы хотим вычислить больше строк, чем может
            //поместится в кэше, то создаем новый кэш большего размера
            maxNLp = newLastN + ROWS_INC_Lp;
            if (maxNLp > LIMIT_Lp) {
                throw new ArithmeticException(
                        "growPasTrgLp: WARNING: It was requested to calculate more than limit=" +
                        LIMIT_Lp +
                        " rows of Pascal triangle. Operation refused.");
            }
            long[][] oldPasTrgLp = pasTrgLp;
            pasTrgLp = new long[maxNLp][];
            System.arraycopy(oldPasTrgLp, 0, pasTrgLp, 0, lastNLp + 1);
        }
        if (newLastN > lastNLp) { //еслм мы хотим вычислить
            //строки, которых нет в кэше, то
            int nStrLen = pasTrgLp[lastNLp].length; //иначе находим длину последней
            //вычисленной строки
            for (int n = lastNLp + 1; n <= newLastN; n++) { //и начинаем вычисление
                //со следующей после последней
                if (n % 2 == 1) { //если номер строки нечетный, то
                    //при переходе от строки с четным номером к
                    //строке с нечетным, размер половины строки
                    //треугольника Паскаля не меняется, т.е.
                    //не меняется nStrLen
                    pasTrgLp[n] = new long[nStrLen]; //создаем n-ю строку кэша
                    //размером nStrLen
                    pasTrgLp[n][0] = 1; //0-й элемент=1
                    for (int k = 1; k < nStrLen; k++) { //вычислим n-ю строку через (n-1)-ю
                        pasTrgLp[n][k] = (pasTrgLp[n - 1][k - 1] + pasTrgLp[n -
                                          1][k]) % mod;
                    }
                } else { //иначе если номер строки четный, то
                    //при переходе от строки с нечетным номером к
                    //строке с четным, размер половины строки
                    //треугольника Паскаля увеличится на 1, т.е.
                    //nStrLen++
                    nStrLen++;
                    pasTrgLp[n] = new long[nStrLen]; //создаем n-ю строку кэша
                    //размером nStrLen
                    pasTrgLp[n][0] = 1; //0-й элемент=1
                    for (int k = 1; k < nStrLen - 1; k++) { //вычислим n-ю строку кроме
                        //последнего элемента через (n-1)-ю
                        pasTrgLp[n][k] = (pasTrgLp[n - 1][k - 1] +
                                          pasTrgLp[n - 1][k]) % mod;
                    }
                    //последний элемент n-й строки в 2 раза больше последнего элемента
                    //(n-1)-й строки
                    pasTrgLp[n][nStrLen -
                            1] = (pasTrgLp[n - 1][nStrLen - 2] * 2) % mod;
                }
            }
            //Вычислив все невычисленные строки кэша от 0 до lastNZp, установим
            //новый номер последней вычисленной строки кэша
            lastNLp = newLastN;
        }
    }

    /* binomialLp - вычисляет число сочетаний из n по k, извлекая это значение из кэша
     * pasTrgZ.
     * Параметры:
     *  n - номер строки кэша
     *  k - номер элемента в строке
     *
     * При вызове soch(n,k) n-я строка кэша должна быть вычислена, поэтому
     * перед вызовом метода soch(n,k) нужно вызвать метод calculateAllStrsToN(n),
     * чтобы быть уверенным, что n-я строка кэша вычислена.
     */
    public static long binomialLp(int n, int k) {
        if (k < pasTrgLp[n].length) { //если k-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, то
            return pasTrgLp[n][k]; //извлечь из n-й строки кэша k-й элемент
        } else { //иначе воспользуемся формулой:
            //soch(n,k)=soch(n,n-k), где (n-k)-й элемент попадает в левую половину
            //n-й строки треугольника Паскаля, тогда
            return pasTrgLp[n][n - k]; //извлечь из n-й строки кэша (n-k)-й элемент
        }
    }
}
