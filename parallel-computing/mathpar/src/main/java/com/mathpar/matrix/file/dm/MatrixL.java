
package com.mathpar.matrix.file.dm;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;


/** Класс матриц над 64-битными целыми числами в плотном формате (DM). Матрица хранится в двумерном массиве <tt>M</tt>.
 * В данном классе также реализованы методы работы с матрицами перестановки, хранящимися особым образом.
 * Выделяется матрица перестановок типа <tt>E</tt> -- квадратная матрица перестановок общего типа и матрица
 * перестановок типа <tt>I</tt> -- диагональная матрица перестановок. Матрица типа <tt>E</tt> хранится в
 * двух массивах типа int[], первый из которых есть список строк, на которых в матрице стоит 1, а второй --
 * список соответствующих столбцов. Для матрицы типа <tt>I</tt> достаточно хранить только массив строк.
 * <p>Title: Algebra with matrices </p>
 * <p>Description: матрицы над 64-битными целыми числами</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: TSU ParCA lab</p>
 * @author TSU ParCA lab
 * @version n/a
 */


public class MatrixL {
    /** Массив коэффициентов */
    public long M[][]; //матрица

    public MatrixL() {}




    /** Создание матрицы из массива коэффициентов long[][].
     *  @param x типа long[][] -- коэффициенты матррицы  <br>
     *  <b>  Пример использования:                       </b> <br>
     *  <CODE>  import matrix.MatrixL;                   <br>
     *          class Example{                           <br>
     *  <ul>    public static void main(String[] args){
     *  <ul>    long[][] m = {{22,13},{13,22}};          <br>
     *          MatrixL mat = new MatrixL(m);          </ul>
     *        }                                          </ul>
     *     }                                             </CODE> <br>
     *  Данная программа создает матрицу размера <tt>2 x 2</tt> из элементов
     * массива <tt>m</tt>.
     */
    //______________________________________________________________________________

    public MatrixL(long x[][]) {
        M = x;
    }




    /** Конструктор нулевой матрицы размера <tt>n x m</tt>.
     *  @param n типа int -- число строк матрицы
     *  @param m типа int -- число столбцов матрицы  <br>
     *  <b>  Пример использования:                   </b> <br>
     *  <CODE> import matrix.MatrixL;                <br>
     *         class Example{                        <br>
     *  <ul>   public static void main(String[] args){
     *  <ul>   int n = 5, m=3;                       <br>
     *         MatrixL mat = new MatrixL(n, m);      </ul>
     *        }                                      </ul>
     *       }                                       </CODE> <br>
     *  Данная программа инициализирует нулевую матрицу размера <tt>5 x3</tt>.
     */
    //______________________________________________________________________________

    public MatrixL(int n, int m) {
        M = new long[n][m];
    }


    public MatrixD toMatrixD(){      
        Ring ring =new Ring("Z64[]");
        MatrixD mtrx=new MatrixD(M,ring);
        return mtrx;
    }

    /** Создает случайную прямоугольную матрицу размера <tt>order1 x order2</tt>
     *  плотности <tt>den/10000</tt> по модулю. Выборка элементов матрицы с использованием
     *  схемы Бернулли (частота появления   ненулевых элементов матрицы стремится
     *  по вероятности к вероятности появления ненулевого элемента в каждой
     *  выборке, равной <tt>den/10000</tt>). Элементы результирующей матрицы на длиннее 32 бит.
     *  Число <tt>den</tt> равно плотности, умноженной на <tt>10000</tt>.
     *  @param order1 типа int -- количество строк данной матрицы
     *  @param order2 типа int -- количество столбцов данной матрицы
     *  @param den типа int -- плотность матрицы, умноженная на 10000
     *  @param p типа long, модуль
     *  @param ran типа java.util.Random -- экземпляр класса Random
     */


    public MatrixL(int order1, int order2, int den, long p,
                   java.util.Random ran) {
        M = new long[order1][order2];
        if (den == 10000) {
            for (int i = 0; i < order1; i++) {
                for (int j = 0; j < order2; j++) {
                    M[i][j] = ran.nextInt() % p;
                }
            }
            return;
        }
        // если нужна разреженная матрица, то используем схему Бернулли с order1*order2 испытаниями,
        // вероятность успеха -- den/10000.
        for (int i = 0; i < order1; i++) {
            for (int j = 0; j < order2; j++) {
                M[i][j] = (Math.round(ran.nextFloat() * 10000) /
                           (10000 - den + 1)); // опыт
                if (M[i][j] != 0) {
                    // если нужна более точная формула, то вставить \\ while(M[i][j] == 0) M[i][j] = ran.nextInt() % p;
                    M[i][j] = ran.nextInt() % p;
                }
            }
        }
    }




    /**
     * Процедура создания единичной матрицы данного порядка.
     * @param ord типа int, порядок создаваемой матрицы
     * @return E<sub>n</sub>
     */
    public static MatrixL ONE(int ord) {
        long[][] r = new long[ord][ord];
        for (int i = 0; i < ord; i++)
            r[i][i] = 1;
        return new MatrixL(r);
    }




    /** Вычисляет сумму матриц <tt> (this + x)%p </tt>
     *  @param x типа MatrixL -- слагаемое матрицы <tt> this </tt>
     *  @param p типа long -- модуль
     *  @return <tt> (this + x)%p </tt>               <br>
     *  <b>   Пример использования:                   </b> <br>
     *  <CODE>   import matrix.MatrixL;               <br>
     *           class Example{                       <br>
     *  <ul>     public static void main(String[] args){
     *  <ul>     long[][] m1 = {{0,2},{3,2}};         <br>
     *           long[][] m2 = {{1,1},{1,1}};         <br>
     *           MatrixL mat1 = new MatrixL(m1),
     *                   mat2 = new MatrixL(m2);      <br>
     *           MatrixL mat3 = mat1.add(mat2, 17);   </ul>
     *       }                                        </ul>
     *    }                                           </CODE> <br>
     *  В этом примере в матрицу <tt>mat3</tt> записывается результат сложения
     * матриц <tt>mat1</tt> и <tt>mat2</tt> по модулю <tt> 17 </tt>, созданных из
     * массивов коэффициентов <tt> m1 </tt> и <tt> m2 </tt>.
     */
    //___________________________________________________________________________

    public MatrixL add(MatrixL x, long p) {
        long[][] z = new long[M.length][M[0].length];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                z[i][j] = (M[i][j] + x.M[i][j]) % p;
                //if(z[i][j] >=p) z[i][j] -=p;
                // if(z[i][j] <=-p) z[i][j] +=p;
            }
        }
        return new MatrixL(z);
    }




    /** Вычисляет разность матриц <tt> (this - x)%p </tt>.
     *  Коэффициенты результата -- из интервала <tt>(-p,p)</tt>.
     *  @param x типа MatrixL -- вычитаемое из матрицы <tt> this </tt>
     *  @param p типа long -- модуль
     *  @return <tt> (this - x)%p </tt>                  <br>
     *  <b>      Пример использования:                   </b> <br>
     *  <CODE>   import matrix.MatrixL;                  <br>
     *           class Example{                          <br>
     *  <ul>     public static void main(String[] args){
     *  <ul>     long[][] m1 = {{0,2},{3,2}};            <br>
     *           long[][] m2 = {{1,1},{1,1}};            <br>
     *           MatrixL mat1 = new MatrixL(m1),
     *                   mat2 = new MatrixL(m2);         <br>
     *           MatrixL mat3 = mat1.subtract(mat2, 17); </ul>
     *         }                                         </ul>
     *        }                                          </CODE> <br>
     * В этом примере в матрицу <tt>mat3</tt> записывается результат вычитания из
     * матрицы <tt>mat1</tt> матрицы <tt>mat2</tt> по модулю <tt> 17 </tt>,
     * созданных из массивов коэффициентов <tt> m1 </tt> и <tt> m2 </tt>.
     */
    //______________________________________________________________________________

    public MatrixL subtract(MatrixL x, long p) {
        long[][] z = new long[M.length][M[0].length];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                z[i][j] = (M[i][j] - x.M[i][j]) % p;
                // if(z[i][j] >=p) z[i][j] -=p;
                // if(z[i][j] <=-p) z[i][j] +=p;
            }
        }
        return new MatrixL(z);
    }




    /** Умножение на матрицу MatrixL по mod p.
     *  @param x типа MatrixL, сомножитель матрицы
     *  @param p типа long, модуль
     *  @return <tt> (this * x)%p </tt> <br>
     *  <b>      Пример использования:                  </b> <br>
     *  <CODE>   import matrix.MatrixL;                 <br>
     *           class Example{                         <br>
     *  <ul>     public static void main(String[] args){
     *  <ul>     long[][] m1 = {{22,13},{13,22}};       <br>
     *           long[][] m2 = {{0,2},{2,3}};           <br>
     *           MatrixL mat1 = new MatrixL(m1),
     *            mat2 = new MatrixL(m2);               <br>
     *           MatrixL mat3 = mat1.multCU(mat2, 17);  </ul>
     *          }                                       </ul>
     *          }                                       </CODE> <br>
     * В этом примере в матрицу <tt>mat3</tt> записывается результат
     * произведения матриц <tt>mat1</tt> и <tt>mat2</tt> по модулю
     *  <tt>17</tt> в указанном прядке, созданных из  массивов коэффициентов
     *  <tt> m1 </tt> и <tt> m2 </tt>.
     */
    //______________________________________________________________________________

    public MatrixL multCU(MatrixL x, long p) {
        int InnModPrConst_128 = 128;
        int n = M.length;
        int m = M[0].length;
        int l = x.M[0].length;
        long[][] z = new long[n][l];
        long Mij;
        if (m <= InnModPrConst_128) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < l; j++) {
                    Mij = 0L;
                    for (int k = 0; k < m; k++) {
                        Mij += M[i][k] * x.M[k][j];
                    }
                    z[i][j] = Mij % p;
                }
            }
        } else {
            int kB, kE;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < l; j++) {
                    Mij = 0L;
                    kE = 0;
                    while (kE < m) {
                        kB = kE;
                        kE += InnModPrConst_128 - 1;
                        if (kE > m) {
                            kE = m;
                        }
                        for (int k = kB; k < kE; k++) {
                            Mij += M[i][k] * x.M[k][j];
                        }
                        Mij %= p;
                    }
                    z[i][j] = Mij;
                }
            }
        }
        return new MatrixL(z);

    }

    public MatrixL multByNumber(Element number, long mod){
        long k = number.longValue();
        long[][] res = new long[M.length][M[0].length];
        for(int i=0; i<M.length; i++){
            for(int j=0; j<M[i].length ; j++){
                res[i][j] = (M[i][j] * k)%mod;
            }
        }
        return new MatrixL(res);
    }




    /**
     * Процедура изменения знака всех элементов матрицы на
     * противоположный по модулю.
     * @param p типа long, модуль
     * @return <tt>-this % p</tt> типа MatrixL
     */

    public MatrixL negate(long p) {
        long[][] res = new long[M.length][M.length];
        for (int i = 0; i < M.length; i++)
            for (int j = 0; j < M.length; j++)
                res[i][j] = -M[i][j];
        return new MatrixL(res);
    }




    public boolean equals(Object o) {
        return equals( (MatrixL) o);
    }




    /**
     * Процедура сравнения двух матриц.
     * @param b типа MatrixL, сравниваемая матрица
     * @return <tt>this == b</tt> типа boolean
     */
    public boolean equals(MatrixL b) {
        if (M.length != b.M.length)
            return false;
        for (int i = 0; i < M.length; i++) {
            if (M[i].length != b.M[i].length)
                return false;
            for (int j = 0; j < M[i].length; j++)
                if (M[i][j] != b.M[i][j])
                    return false;
        }
        return true;
    }




    /**
     * Процедура сравнения двух матриц по модулю
     * @param b типа MatrixL, вторая сравниваемая матрица
     * @param p типа long, модуль
     * @return <tt>this == b mod p</tt> типа boolean
     */
    public boolean equals(MatrixL b, long p) {
        if (M.length != b.M.length)
            return false;
        for (int i = 0; i < M.length; i++) {
            if (M[i].length != b.M[i].length)
                return false;
            for (int j = 0; j < M[i].length; j++)
                if ( (M[i][j] - b.M[i][j]) % p != 0)
                    return false;
        }
        return true;
    }




    /**
     * Сравнение с нулем матрицы по модулю.
     * @param p типа long, модуль
     * @return <tt>(this == 0)%p</tt> типа boolean
     */
    public boolean isZERO(long p) {
        int m = M.length, n = M[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (M[i][j] % p != 0) {
                    return false;
                }
            }
        }
        return true;
    }




    /**
     * Сравнение с нулем матрицы по модулю.
     * @return <tt>this == 0</tt> типа boolean
     */
    public boolean isZERO() {
        int m = M.length, n = M[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (M[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

}



