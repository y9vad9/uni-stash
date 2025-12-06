
package com.mathpar.number;

/**
 *
 * <p>Title: Class NumberZkaratcuba</p>
 *
 * <p>Description: Класс NumberZkaratcuba содержит методы, реализующие умножение
 * больших целых чисел по схеме Карацубы.
В <a href="Karacuba_comment.pdf"> приложении </a> описана схема умножения целых чисел по аалгоритму Карацубы.
Числа хранятся в массиве int[] по
 * основанию p=2<sup>32</sup> в порядке Big Endian (то есть старшие цифры хранятся в начале массива). Например, число
 * 31 * 2<sup>96</sup> + 45 * 2<sup>64</sup> + 20 * 2<sup>32</sup> + 10 будет представлено в следующем виде:
 * {31, 45, 20, 10}.
 * Число может занимать не весь массив int[], а только его часть. Для этого
 * во входынх параметрах метода надо передать offset - отступ от нулевого
 * элемента в массиве и len - длину используемой части массива.
 *
 * Схема умножения целых чисел реализована с помощью методов:
 * multiplyK1 - умножение чисел с одинаковыми длинами, равными N=2^n.
 * multiplyK2 - умножение чисел с длинами, равными N=2^n и M=2^m
 * multiplyK3 - умножение чисел с длинами, равными N=2^n и M, M < N
 * multiplyK4 - умножение чисел с длинами, равными N и M=2^m, N > M
 * multiplyK5 - умножение чисел с произвольными длинами.
 *
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Parrallel Computer Algebra Laboratory, Institute of Physics,
 * Mathematics and Computing,
 * TSU named by G.R. Derzhavin
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class NumberZkaratcuba {
    /**
        Струтура данных, используемая функцией multiplyK1.
	Рекурсивный алгоритм K1 умножает 2 числа, длины которых равны некоторой степени числа 2. Для вычисления
	результат умножения на каждом уровне рекурсии вычисляются A, B, C, s1, s2. Трехмерный массив parts
	устроен следующим образом: первый индекс - это глубина рекурсии, второй индекс - номер коэффициента, а в одномерных
	массивах parts[][] хранятся непосредственно A, B, C, s1 = (a<sub>1</sub>, a<sub>2</sub>, ..., a<sub>n/2</sub>) +
(a<sub>n/2 + 1</sub>, a<sub>n/2+2</sub>, ..., a<sub>n</sub>), s2 = (b<sub>1</sub>, b<sub>2</sub>, ..., b<sub>n/2</sub>) +
(b<sub>n/2 + 1</sub>, b<sub>n/2+2</sub>, ..., b<sub>n</sub>) в следующем порядке: <br>
	parts[][0] - A <br>
	parts[][1] - C <br>
	parts[][2] - B <br>
	parts[][3] - s1 <br>
	parts[][4] - s2. <br>
	Перед вызовом multiplyK1 parts необходимо проинициализивровать. Для этого необходимо определить глуюину рекурсии
	(пусть len - длина входных чисел, тогда глубина рекурсии есть log<sub>2</sub>(len)) и максимальную длину результатов
	промежуточных вычислений. Из алгоритма K1 очевидно, что эта длина не будет превоходить len + 1, где len - длины
	входных чисел.

     */
    int[][][] parts;

    /**
         Переменная, хранящая длину одномерных массивов parts[][]
     */
    int strW;

    /**
     Размеры квантов для функций multiplyK1, multuplyK3, multiplyK4, multiplyK5.
     Если одна из длин двух чисел меньше, чем размер кванта, то используется
     стандартное умножение.
Размеры квантов подбираются в соответсвии с конфигурации компьютера.
     */
    int quantK1 = 16; //квант multiplyK1
    int quantK3 = 63; //квант multiplyK3
    int quantK4 = 63; //квант multiplyK4
    int quantK5 = 63; //квант multiplyK5

    /**
          Маска, вырезающая из long младцшие 32 бита
     */
    final long LONG_MASK = 0xffffffffL;


    /**
     * Инициализация массива parts и переменной strW: parts[depth][5][width];
     * strW=width;
     * @param depth int
     * @param width int
     */
    void initStruct(int depth, int width) {
        parts = new int[depth][5][width];
        strW = width;
    }

    /**
     * Конструктор, позволяющий изменить стандартные
     * размеры квантов для умножения
     * @param quantK1 int
     * @param quantK3 int
     * @param quantK4 int
     * @param quantK5 int
     */
    public NumberZkaratcuba(int quantK1, int quantK3, int quantK4, int quantK5) {
        this.quantK1 = quantK1;
        this.quantK3 = quantK3;
        this.quantK4 = quantK4;
        this.quantK5 = quantK5;
    }

    public NumberZkaratcuba() {}

//==============================================================================

    /**
     * Суммирование двух чисел v1 и v2 с записью результата в массив res
     * Возвращает true, если длина результата больше, чем максимум из len1
     * и len2
	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для первого числа
	@param len2 int - длина первого числа
	@param res int[] -   в этот массив будет записана сумма чисел v1 и v2
	@return true, если длина результат сложения больше, чем len1 + len2 и false, если
	длина результат равна len1 + len2
     */
    public boolean sum(int[] v1, int offset1, int len1, int[] v2, int offset2,
                       int len2, int[] res) {
        //начальные позиции в массивах v1 и v2 соотетственно
        int x = len1 + offset1;
        int y = len2 + offset2;
        int rstart = res.length - 1;
        long sum = 0;
        //складываем совпадающие части массивов
        while ((x > offset1) && (y > offset2)) {
            sum = (v1[--x] & LONG_MASK) +
                  (v2[--y] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }

        //добавляем старшие цифры числа v1 (если длина v1 больше длины v2)
        while (x > offset1) {
            sum = (v1[--x] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }
        //добавляем старшие цифры числа v2 (если длина v2 больше длины v1)
        while (y > offset2) {
            sum = (v2[--y] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }

        sum >>>= 32;

        //проверяем, был ли после последнего сложения перенос бита
        if ((sum) > 0) {
            /* если был, то записываем его в массив res на позицию
                       res.length - max(v1.length - v2.length) - 2
             */

            res[rstart--] += (int) sum;
            return true;
        }
        return false;
    }

//==============================================================================

    /**
     * Суммирование двух чисел v1 и v2 с записью результата в массив res,
     * начиная с позиции res.length - shift - 1
	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для первого числа
	@param len2 int - длина первого числа
	@param res int[] -   в этот массив будет записана сумма чисел v1 и v2
	@param shift int


     */

    public void sumShift(int[] v1, int offset1, int len1, int[] v2, int offset2,
                         int len2, int[] res, int shift) {
        int x = len1 + offset1;
        int y = len2 + offset2;
        int rstart = res.length - 1 - shift;
        long sum = 0;
        for (int i = res.length - 1; ((i > rstart) && (x > offset1)); i--) {
            res[i] = v1[--x];
        }

        while ((x + shift > offset1) && (y > offset2)) {
            sum = (v1[--x] & LONG_MASK) + (v2[--y] & LONG_MASK) +
                  (sum >>> 32);
            res[rstart--] = (int) sum;
        }

        while (x > offset1) {
            sum = (v1[--x] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }

        while (y > offset2) {
            sum = (v2[--y] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }

        sum >>>= 32;
        if (sum > 0) {
            sum = (res[rstart] & LONG_MASK) + sum;
            res[rstart--] = (int) sum;
        }
    }

//==============================================================================


    /**
     * Суммирование трех чисел v1, v2 и v3 с записью результата в массив res,
     * начиная с позиции res.length - shift - 1. Числа v2 и v3 определяются
     * переменными offset2 и len2. При этом в массиве res будет записано число:
     * res = v1 + ((v2+v3) << (shift*32))

	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param v3 int[]
	@param offset2 int - отступ для v2 и v3 числа
	@param len2 int - длина v2 и v3 числа
	@param res int[] -   в этот массив будет записан результат
	@param shift int

     */

    public void sumShift(int[] v1, int offset1, int len1, int[] v2, int[] v3,
                         int offset2,
                         int len2, int[] res, int shift) {
        //начальная позиция в массиве v1
        int x = len1 + offset1;
        //начальная позиция в массивах v2 и v3
        int y = len2 + offset2;
        //начальная позиция в массиве res
        int rstart = res.length - 1 - shift;
        long sum = 0;

        // запись в res тех элементов v1, к которым при суммировании c
        // (v1+v2) << (shift*32) будут прибавляться нули
        for (int i = res.length - 1; ((i > rstart) && (x > offset1)); i--) {
            res[i] = v1[--x];
        }
        // суммирование совпадающих частей массивов
        while ((x + shift > offset1) && (y > offset2)) {
            sum = (v1[--x] & LONG_MASK) + (v2[--y] & LONG_MASK) +
                  (v3[y] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }

        //добавление старших чисел из v1
        while (x > offset1) {
            sum = (v1[--x] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }
        //добавление старших чисел из (v1+v2) << (shift*32)
        while (y > offset2) {
            sum = (v2[--y] & LONG_MASK) + (v3[y] & LONG_MASK) + (sum >>> 32);
            res[rstart--] = (int) sum;
        }

        sum >>>= 32;
        // проверка, был ли последний перенос
        if (sum > 0) {
            sum = (res[rstart] & LONG_MASK) + sum;
            res[rstart--] = (int) sum;
        }
    }

//==============================================================================


    /**
     * Умножение двух чисел v1 и v2 с записью в массив res. Массив res будет
     * перезаписан

	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для первого числа
	@param len2 int - длина первого числа
	@param res int[] -   в этот массив будет записано произведение чисел v1 и v2
	@param shift int

     */

    public void multiply(int[] v1, int offset1, int len1, int[] v2, int offset2,
                         int len2, int[] res) {
        //позиция в массиве v1
        int xLen = len1 + offset1;
        //позиция в массиве v2
        int yLen = len2 + offset2;
        //позиция в массиве res
        int rLen = res.length - 1;
        long carry = 0;
        int k = rLen;
        int xLen1 = xLen - 1;
        long product = 0;

        // первая итерация вынесена из основного цикла, чтобы
        // почистить массив res

        for (int j = yLen - 1; j >= offset2; ) {
            product = (v2[j--] & LONG_MASK) *
                      (v1[xLen1] & LONG_MASK) + carry;
            res[k--] = (int) product;
            carry = product >>> 32;
        }
        res[k] = (int) carry;
        int c = 1;


        for (int i = xLen - 2; i >= offset1; i--) {
            carry = 0;
            k = rLen - (c++);
            for (int j = yLen - 1; j >= offset2; ) {
                product = (v2[j--] & LONG_MASK) *
                          (v1[i] & LONG_MASK) +
                          (res[k] & LONG_MASK) + carry;
                res[k--] = (int) product;
                carry = product >>> 32;
            }
            res[k] = (int) carry;
        }
    }

//==============================================================================

    /**
     * Умножение двух чисел v1 и v2 стандартным способом с записью в массив res.
     * При этом res = res + ((v1*v2) << (shift * 32)).

	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для второго числа
	@param len2 int - длина второго числа
	@param res int[] -   в этот массив будет записана произведение чисел v1 и v2
	@param shift int

     */

    public void multiplySum(int[] v1, int offset1, int len1, int[] v2,
                            int offset2,
                            int len2, int[] res, int shift) {

        int xLen = len1 + offset1;
        int yLen = len2 + offset2;
        int rLen = res.length - 1 - shift;

        long carry = 0;
        int c = 0;

        for (int i = xLen - 1; i >= offset1; i--) {
            carry = 0;
            int k = rLen - (c++);
            for (int j = yLen - 1; j >= offset2; ) {
                long product = (v2[j--] & LONG_MASK) *
                               (v1[i] & LONG_MASK) +
                               (res[k] & LONG_MASK) + carry;
                res[k--] = (int) product;
                carry = product >>> 32;
            } while (carry > 0) {
                carry = (res[k] & LONG_MASK) + carry;
                res[k--] = (int) carry;
                carry >>>= 32;
            }
        }
    }

//==============================================================================

    /**
     * Записывает в массив res разность чисел v1 и v2
     * res = v1 - v2.

	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для второго числа
	@param len2 int - длина второго числа
	@param res int[] -   в этот массив будет записана разность чисел v1 и v2
	@param shift int

     */
    public void subtract(int[] v1, int offset1, int len1, int[] v2, int offset2,
                         int len2, int[] res) {
        long diff = 0;
        int x = len1 + offset1;
        int y = len2 + offset2;
        int rstart = res.length - 1;
        while (y > offset2) {
            diff = (v1[--x] & LONG_MASK) -
                   (v2[--y] & LONG_MASK) - ((int) - (diff >> 32));
            res[rstart--] = (int) diff;
        }

        while (x > offset1) {
            diff = (v1[--x] & LONG_MASK) - ((int) - (diff >> 32));
            res[rstart--] = (int) diff;
        }
    }

//==============================================================================


    /**
     * Записывает в массив res разность v1 и суммы v2+v3
     * res = v1 - v2 - v3

	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для v2 и v3
	@param len2 int - длина v2 и v3
	@param res int[] -   в этот массив будет записан результат

     */
    public void subtract(int[] v1, int offset1, int len1, int[] v2, int[] v3,
                         int offset2,
                         int len2, int[] res) {
        long diff = 0;
        int x = len1 + offset1;
        int y = len2 + offset2;
        int rstart = res.length - 1;
        while (y > offset2) {
            y--;
            diff = (v1[--x] & LONG_MASK) -
                   (v2[y] & LONG_MASK) - (v3[y] & LONG_MASK) -
                   ((int) - (diff >>> 32));

            res[rstart--] = (int) diff;
        }

        while (x > offset1) {
            diff = (v1[--x] & LONG_MASK) - ((int) - (diff >>> 32));
            res[rstart--] = (int) diff;
        }
    }

//==============================================================================


    int global_shift = 0; //показывает, начиная с какой позиции к массиву
    //res будет прибавляться результат умножения

    /**
     * Рекурсивная реализация формулы умножения Карацубы.
     * Пусть p - основание системы счисления. Тогда
     * (a*p+b)(c*p+d) = a*c*p^2 + ( (a+b)*(c+d) - a*c - b*d )*p + b*d
     * a,b,c,d < p
     * Числа, хранящиеся в массивах v1 и v2 определяются offset1 и offset2
     * соответственно, их длины равны len. Предполагается, что len составляет
     * какую-то степень числа 2 и что массив parts уже инициализирован.
     * level - уровень рекурсии
     */
    public void multiplyK1(int[] v1, int offset1, int[] v2, int offset2,
                           int len,
                           int[] res, int level) {

        /* если длина меньше размера кванта, то используется
               стандартное умножение
         */

        if (len <= quantK1) {
            multiply(v1, offset1, len, v2, offset2, len, res);
        }
        else {

            int hLen = len >>> 1;

            /* получение ссылок на массивы для хранение старшей, средней и
            младщей части результата */
            int[] a = parts[level][0];//A
            int[] b = parts[level][2];//B
            int[] c = parts[level][1];//C

            /* полчуение ссылок на хранение сумм страших и младших половин
            v1 и v2 */
            int[] sum1 = parts[level][3];//s1
            int[] sum2 = parts[level][4];//s2

            /* вычисление старшей и младшей части результата */
            multiplyK1(v1, offset1, v2, offset2, hLen, a, level + 1);//вычисление A
            multiplyK1(v1, offset1 + hLen, v2, offset2 + hLen, hLen, c,
                       level + 1);//вычисление C

            /* вычисление сумм страших и младших половин v1 и v2.
             f1 - флаг, отвечающий за наличие переполнения в sum1
             f2 - флаг, отвечающий за наличие переполнения в sum2
             */
            boolean f1 = sum(v1, offset1, hLen, v1, offset1 + hLen, hLen, sum1);//s1
            boolean f2 = sum(v2, offset2, hLen, v2, offset2 + hLen, hLen, sum2);//s2


            int buf = strW - hLen;
            int buf1 = buf - hLen;

            /* вычисление среднего коэффициента.
Полагаем, что длины s1 и s2  есть hLen.
*/
            multiplyK1(sum1, buf, sum2, buf, hLen,
                       b, level + 1);


//проверяем возможное переполнение при вычислении s1 и s2
            if ((f1) && (f2)) {
                b[buf1 - 1] = 1;
                sumShift(b, buf - 1, hLen + 1, sum2, sum1, buf, hLen, b, hLen);
                //sumShift(b, buf - 1, hLen + 1, sum1, buf, hLen, b, hLen);
            }
            else {

                if (f1)
                    sumShift(b, buf, hLen, sum2, buf, hLen, b, hLen);
                if (f2)
                    sumShift(b, buf, hLen, sum1, buf, hLen, b, hLen);
            }

            subtract(b, buf1 - 1, len + 1, a, c, buf1, len, b);//B


            if (level != 0)
		//сборка магнитуды результата
                concateMags(a, b, c, res, len);
            else
		/*сборка магнитуды результата, при этом магнитуда будет прибавлена к массиву res начиная с позиции
		res.length - global_shift  - 1
*/
               concateMagsSum(a, b, c, res, len, global_shift);
            b[buf1 - 1] = 0;
            sum1[buf - 1] = 0;
            sum2[buf - 1] = 0;
        }

    }

//==============================================================================

    /**
	Вычисляет результат по формуле:
       res = (v1<<(shl*32)) + (v2<<(shl*16)) + v3
	@param v1 int[]
	@param v2 int[]
	@param v2 int[]
	@param res int[]
	@param shl int
     */
    void concateMags(int[] v1, int[] v2, int[] v3, int[] res, int shl) {
        int len1 = strW;
        int len2 = strW;
        int len3 = strW;
        int pos1 = len1 - 1;
        int pos2 = len2 - 1;
        int pos3 = len3 - 1;
        int i = res.length - 1;
        int halfShl = shl >>> 1;
        long sum = 0;
        int buf = len3 - halfShl;
        int buf1 = len3 - shl;
        while (pos3 >= buf) {
            sum = (sum >>> 32) + (v3[pos3--] & LONG_MASK);
            res[i--] = (int) sum;
        }

        while (pos3 >= buf1) {
            sum = (v3[pos3--] & LONG_MASK) + (v2[pos2--] & LONG_MASK) +
                  (sum >>> 32);
            res[i--] = (int) sum;

        }

        while (pos2 >= buf1) {
            sum = (v2[pos2--] & LONG_MASK) + (v1[pos1--] & LONG_MASK) +
                  (sum >>> 32);
            res[i--] = (int) sum;
            pos3--;

        }

        while ((pos1 >= buf1) && (i > 0)) {
            sum = (v1[pos1--] & LONG_MASK) + (v2[pos2--] & LONG_MASK) +
                  (sum >>> 32);
            res[i--] = (int) sum;
        }

        sum >>>= 32;
        if (sum > 0) {
            res[i--] = (int) sum;
        }
    }

//==============================================================================

    /**
	 Вычисляет результат по формуле
         res = res + (((v1<<(shl*32)) + (v2<<(shl*16)) + v3)<<(shift*32))
	@param v1 int[]
	@param v2 int[]
	@param v2 int[]
	@param res int[]
	@param shl int
	@param shift int
     */
    void concateMagsSum(int[] v1, int[] v2, int[] v3, int[] res, int shl,
                        int shift) {
        int len1 = v1.length;
        int len2 = v2.length;
        int len3 = v3.length;
        int pos1 = len1 - 1;
        int pos2 = len2 - 1;
        int pos3 = len3 - 1;
        int i = res.length - 1 - shift;
        int halfShl = shl >> 1;
        long sum = 0;
        int buf = len3 - halfShl;
        int buf1 = len3 - shl;
        while (pos3 >= buf) {
            sum = (res[i] & LONG_MASK) + (sum >>> 32) + (v3[pos3--] & LONG_MASK);
            res[i--] = (int) sum;
        }

        while (pos3 >= buf1) {
            sum = (v3[pos3--] & LONG_MASK) + (v2[pos2--] & LONG_MASK) +
                  (sum >>> 32) +
                  (res[i] & LONG_MASK);
            res[i--] = (int) sum;

        }

        while ((pos2 >= buf1 - 1)) {
            sum = (v1[pos1--] & LONG_MASK) + (v2[pos2--] & LONG_MASK) +
                  (sum >>> 32) + (res[i] & LONG_MASK);
            res[i--] = (int) sum;
        }

        while ((pos1 >= buf1)) {
            sum = (v1[pos1--] & LONG_MASK) + (res[i] & LONG_MASK) +
                  (sum >>> 32);
            res[i--] = (int) sum;
        }
        sum >>>= 32;
        while (sum > 0) {
            sum = (res[i] & LONG_MASK) + sum;
            res[i--] = (int) sum;
            sum >>>= 32;
        }
    }

//==============================================================================


    /**
     * Умножение двух чисел в случае, когда длины чисел len1 и len2
     * необзательно равны смежду собой и составляют какие-то степени числа 2.

	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для первого числа
	@param len2 int - длина первого числа
	@param res int[] -   в этот массив будет записано ((v1*v2) << (shift*32)) + res
	@param  shift int

     */
    void multiplyK2(int[] v1, int offset1, int len1, int[] v2, int offset2,
                    int len2, int[] res, int shift) {
        int n = len1;
        int m = len2;
        int iter = n / m;
        int buf = global_shift;

        /* определение степени ближайшей слева степени 2 числа m */
        int deg = -1;
        {
            int m1 = m;
            while (m1 > 0) {
                m1 >>>= 1;
                deg++;
            }
        }

        global_shift += shift;
        int offset_v1 = n - m + offset1; //offset для v1
        for (int i = 0; i < iter; i++) {
            multiplyK1(v1, offset_v1, v2, offset2, m, res, 0);
            global_shift += m;
            offset_v1 -= m;
        }
        global_shift = buf;
    }

//==============================================================================

    /**
     * Умножение чисел в случае, когда len1 составляет степень числа 2
     * и len1 > len2

	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для первого числа
	@param len2 int - длина первого числа
	@param res int[] -   в этот массив будет записано ((v1*v2) << (shift*32)) + res
	@param  shift int

     */
    void multiplyK3(int[] v1, int offset1, int len1, int[] v2, int offset2,
                    int len2, int[] res, int shift) {
        if ((len2 <= quantK3)) {
            multiplySum(v1, offset1, len1, v2, offset2, len2, res,
                        shift);
        } else {
            int n = len1;
            int m = len2;
            int deg = -1;

            /* определение ближайшей к m слева степени числа 2 */
            while (m > 0) {
                m >>>= 1;
                deg++;
            }
            m = len2;
            int deg_m = 1 << deg;
            initStruct(deg, m + 1);
            multiplyK2(v1, offset1, len1, v2, offset2 + m - deg_m, deg_m, res,
                       shift);
            if (m > deg_m)
                multiplyK3(v1, offset1, len1, v2, offset2, m - deg_m, res,
                           shift + deg_m);
        }
    }

//==============================================================================


    /**
     * Умножение двух чисел в случае, когда len1>len2 и len2 составляет степень
     * числа 2.
	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для второго числа
	@param len2 int - длина второго числа
	@param res int[] -   в этот массив будет записано ((v1*v2) << (shift*32)) + res
	@param  shift int

     */

    void multiplyK4(int v1[], int offset1, int len1, int[] v2, int offset2,
                    int len2, int[] res, int shift) {
        if (len2 <= quantK4) {
            multiplySum(v1, offset1, len1, v2, offset2, len2, res,
                        shift);

        }
        else {
            int n = len1;
            int m = len2;
            int deg = -1;
            while (m > 0) {
                m >>>= 1;
                deg++;
            }
            m = len2;
            int deg_m = 1 << deg;
            initStruct(deg, m + 1);
            //количество умножение типа K1
            int iter = n / m;
            int buf = global_shift;
            int offset_v1 = offset1 + n - m;
            //сдвиг результата умножения K1 при прибавлении к массиву res
            global_shift += shift;
            for (int i = 0; i < iter; i++) {
                multiplyK1(v1, offset_v1, v2, offset2, m, res, 0);
                global_shift += m;
                offset_v1 -= m;
            }
            global_shift = buf;
            multiplyK3(v2, offset2, m, v1, offset1, n - iter * m, res,
                       shift + iter * m);
        }
    }


//==============================================================================

    /**
     * Умножение двух чисел в случае, когда len1 > len2
	@param v1 int[]
	@param offset1 int - отступ для первого числа
	@param len1 int - длина первого числа
	@param v2 int[]
	@param offset2 int - отступ для второго числа
	@param len2 int - длина второго числа
	@param res int[] -   в этот массив будет записано ((v1*v2) << (shift*32)) + res
	@param  shift int


     */
    void multiplyK5(int[] v1, int offset1, int len1, int[] v2, int offset2,
                    int len2, int[] res, int shift) {
        if (len2 <= quantK5) {
            multiplySum(v1, offset1, len1, v2, offset2, len2, res,
                        shift);
        } else {
            int m = len2;
            int deg = -1;

            //определение ближайшей к m слева степени числа 2.
            while (m > 0) {
                m >>>= 1;
                deg++;
            }
            m = len2;
            int deg_m = 1 << deg;
            multiplyK4(v1, offset1, len1, v2, offset2 + m - deg_m, deg_m, res,
                       shift);
            multiplyK5(v1, offset1, len1, v2, offset2, m - deg_m, res,
                       shift + deg_m);
        }
    }

//==============================================================================

    /**
     * Следующие 5 методов являются оболочками к вышенаписанным
     * multiplyK1 multiplyK2 multiplyK3 multiplyK4 multiplyK5 и реализуют
     * связь с классом BigInteger.
     */


    NumberZ multiplyK1(NumberZ a, NumberZ b) {
    int n = a.mag.length;
    strW = n << 1;
    int[] res = new int[strW];
    int depth = 0;
    while (n > 0) {
        n >>>= 1;
        depth++;
    }
    n = a.mag.length;
    parts = new int[depth][5][strW];
    int[] v1 = a.mag;
    int[] v2 = b.mag;
    multiplyK1(v1, 0, v2, 0, n, res, 0);
    return new NumberZ(res, a.signum * b.signum);
}

//==============================================================================


    NumberZ multiplyK2(NumberZ a, NumberZ b) {
        int[] res = new int[a.mag.length + b.mag.length];
        multiplyK2(a.mag, 0, a.mag.length, b.mag, 0, b.mag.length, res, 0);
        return new NumberZ(res, a.signum * b.signum);

    }

//==============================================================================


    NumberZ multiplyK3(NumberZ a, NumberZ b) {
        int[] res = new int[a.mag.length + b.mag.length];
        multiplyK3(a.mag, 0, a.mag.length, b.mag, 0, b.mag.length, res, 0);
        return new NumberZ(res, a.signum * b.signum);
    }

//==============================================================================


    NumberZ multiplyK4(NumberZ a, NumberZ b) {
        int[] res = new int[a.mag.length + b.mag.length];
        multiplyK4(a.mag, 0, a.mag.length, b.mag, 0, b.mag.length, res, 0);
        return new NumberZ(res, a.signum * b.signum);
    }

    //==============================================================================

    NumberZ multiplyK5(NumberZ a, NumberZ b) {
        int[] res = new int[a.mag.length + b.mag.length];
        multiplyK5(a.mag, 0, a.mag.length, b.mag, 0, b.mag.length, res, 0);
        return new NumberZ(res, a.signum * b.signum);
    }

} //NumberZkaratcuba
