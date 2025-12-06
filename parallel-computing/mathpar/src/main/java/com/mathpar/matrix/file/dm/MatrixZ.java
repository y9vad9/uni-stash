
package com.mathpar.matrix.file.dm;


import com.mathpar.number.*;


/** Целочисленные матрицы в плотном формате (DM).
 *Cодержит методы стандартной матричной арифметики.
 * Проверки совпадения размеров аргументов не производится.
 * <p>Title: </p>
 * <p>Description: matrix algebra component</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: TSU ParCA lab</p>
 * @author TSU ParCA lab
 * @version n/a
 */

public class MatrixZ {
    /** Массив коэффициентов */
    public NumberZ M[][]; //матрица

    public MatrixZ() {}




    /**
     * Конструктор матрицы MatrixZ из массива коэффициентов типа NumberZ[]
     * @param x типа NumberZ[], массив коэффициентов <br>
     * <b>    Пример использования                   </b> <br>
     * <CODE> import matrix.MatrixZ;                 <br>
     *        import math.NumberZ;           <br>
     *        class Example{                         <br>
     * <ul>   public static void main(String[] args){
     * <ul>   NumberZ[][] mas = {{NumberZ.ONE, NumberZ.ZERO},
     *        {NumberZ.ZERO, NumberZ.ONE}};    <br>
     *        MatrixZ matr = new MatrixZ(mas);       </ul>
     *       }                                       </ul>
     *       }                                       </CODE> <br>
     * В этом примере матрица <tt>matr</tt> инициализируется с использованием
     * массива коэффициентов <tt>mas</tt>.
     */
    //______________________________________________________________________________
    public MatrixZ(NumberZ x[][]) {
        M = x;
    }




    /**
     * Конструктор нулевой матрицы размера <tt> n x m </tt>.
     * @param n типа int, число строк матрицы
     * @param m типа int, число столбцов матрицы. <br>
     * <b>     Пример использования               </b> <br>
     * <CODE>  import matrix.MatrixZ;             <br>
     *         class Example{                     <br>
     * <ul>    public static void main(String[] args){
     * <ul>    MatrixZ matr = new MatrixZ(12,12); </ul>
     *         }                                  </ul>
     *         }                                  </CODE> <br>
     * В этом примере инициализируется нулевая матрица <tt>matr</tt>
     *  размера <tt>12 x 12</tt>.
     */

    public MatrixZ(int n, int m) {
        M = new NumberZ[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                M[i][j] = NumberZ.ZERO;
            }
        }
    }




    /**
     * Конструктор случайной прямоугольной матрицы данной плотности с
     * коэффициентами из отрезка <tt> [0, 2<sup>AmountOfBits</sup> - 1] </tt>.
     * выборка происходит с использованием схемы Бернулли.
     * @param lenr типа int, число строк матрицы
     * @param lenc типа int, число столбцов матрицы
     * @param density типа int, плотность матрицы, умноженная на 10000
     * @param AmountOfBits типа int, число бит в коэффициентах
     * @param ran java.util.Random, экземпляр класса Random. <br>
     * <b>     Пример использования               </b> <br>
     * <CODE>  import matrix.MatrixZ;             <br>
     *         import java.util.Random();         <br>
     *         class Example{                     <br>
     * <ul>    public static void main(String[] args){
     * <ul>    Random rnd = new Random();         <br>
     *         MatrixZ matr = new MatrixZ(32, 24,
     *            5000, 32, rnd);                 </ul>
     *        }                                   </ul>
     *        }                                   </CODE> <br>
     * В этом примере инициализируется матрица <tt>matr</tt> размера
     * <tt>32 x 24</tt>, плотности <tt>0.5</tt>, с <tt>32</tt>  бит на каждый
     * элемент.
     */
    public MatrixZ(int lenr, int lenc, int density, int AmountOfBits,
                   java.util.Random ran) {
        int[][] m1 = new int[lenr][lenc];
        M = new NumberZ[lenr][lenc];
        if (density == 10000) {
            for (int i = 0; i < lenr; i++)
                for (int j = 0; j < lenc; j++)
                    M[i][j] = new NumberZ(AmountOfBits, ran);
            return;
        }
        for (int i = 0; i <= lenr - 1; i++) {
            for (int j = 0; j <= lenc - 1; j++) {
                m1[i][j] = (Math.round(ran.nextFloat() * 10000) /
                            (10000 - density + 1));
                if (m1[i][j] == 0) {
                    M[i][j] = NumberZ.ZERO;
                } else {
                    M[i][j] = new NumberZ(AmountOfBits, ran);
                }
            }
        }
    }




    /**
     * Процедура, возвращающая единичную матрицу заданного размера.
     * @param k типа int, порядок матрицы
     * @return <tt> E </tt> <br>
     * <b>    Пример использования                    </b> <br>
     * <CODE> import matrix.MatrixZ;                  <br>
     *        class Example{                          <br>
     * <ul>   public static void main(String[] args){
     * <ul>   MatrixZ matr1 = MatrixZ.ONE(32);        </ul>
     *        }                                       </ul>
     *        }                                       </CODE> <br>
     * В этом примере единичная матрица порядка <tt>32</tt> записывается
     * в переменную <tt>matr1</tt>.
     */
// //////////////////////////////////////////////////////////
    public static MatrixZ ONE(int k) {
        NumberZ[][] mas = new NumberZ[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < i; j++) {
                mas[i][j] = NumberZ.ZERO;
            }
            mas[i][i] = NumberZ.ONE;
            for (int j = i + 1; j < k; j++) {
                mas[i][j] = NumberZ.ZERO;
            }
        }
        return new MatrixZ(mas);
    }




    /**
     * Вычисление суммы матриц.
     * @param x типа MatrixZ, слагаемое
     * @return <tt> this + x </tt> <br>
     * <b>    Пример использования                            </b> <br>
     * <CODE> import matrix.MatrixZ;                          <br>
     *        import java.util.Random;                        <br>
     *        class Example{                                  <br>
     * <ul>   public static void main(String[] args){
     * <ul>   Random rnd = new Random();                      <br>
     *        MatrixZ matr1 = new MatrixZ(32, 5000, 32, rnd); <br>
     *        MatrixZ matr2 = new MatrixZ(32, 4000, 16, rnd); <br>
     *        MatrixZ msum = matr1.add(matr2);                </ul>
     *       }                                                </ul>
     *       }                                                </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> суммируются и
     * записываются в <tt>msum</tt>.
     */
    //__________________________________________________________________________

    public MatrixZ add(MatrixZ x) {
        int n = M.length;
        int m = M[0].length;
        NumberZ[][] z = new NumberZ[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].add(x.M[i][j]);
            }
        }
        return new MatrixZ(z);
    }




    /**
     * Вычисление разности матриц.
     * @param x типа MatrixZ, вычитаемое
     * @return <tt> this - x </tt> <br>
     * <b>     Пример использования                            </b> <br>
     * <CODE>  import matrix.MatrixZ;                          <br>
     *         import java.util.Random();                      <br>
     *         class Example{                                  <br>
     * <ul>    public static void main(String[] args){
     * <ul>    Random rnd = new Random();                      <br>
     *         MatrixZ matr1 = new MatrixZ(32, 5000, 32, rnd); <br>
     *         MatrixZ matr2 = new MatrixZ(32, 4000, 16, rnd); <br>
     *         MatrixZ msum = matr1.subtract(matr2);           </ul>
     *         }                                               </ul>
     *         }                                               </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> вычитаются и
     * записываются в <tt>msum</tt>.
     */
    //__________________________________________________________________________

    public MatrixZ subtract(MatrixZ x) {
        int n = M.length;
        int m = M[0].length;
        NumberZ[][] z = new NumberZ[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].subtract(x.M[i][j]);
            }
        }
        return new MatrixZ(z);
    }




    /**
     * Процедура вычисления произведения матриц. Используется стандартная
     * нерекурсивная схема умножения.
     * @param x типа MatrixZ, сомножитель
     * @return <tt> this * x </tt> <br>
     * <b>     Пример использования                            </b> <br>
     * <CODE>  import matrix.MatrixZ;                          <br>
     *         import java.util.Random;                        <br>
     *         class Example{                                  <br>
     * <ul>    public static void main(String[] args){
     * <ul>    Random rnd = new Random(); <br>
     *         MatrixZ matr1 = new MatrixZ(32, 5000, 32, rnd); <br>
     *         MatrixZ matr2 = new MatrixZ(32, 4000, 16, rnd); <br>
     *         MatrixZ msum = matr1.multCU(matr2);             </ul>
     *         }                                               </ul>
     *         }                                               </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
     * записываются в <tt>msum</tt>.
     */

    public MatrixZ multCU(MatrixZ x) {
        NumberZ[][] z = new NumberZ[M.length][x.M[0].length];
        NumberZ res;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < x.M[0].length; j++) {
                res = NumberZ.ZERO;
                for (int k = 0; k < M[0].length; k++) {
                    res = res.add(M[i][k].multiply(x.M[k][j]));
                }
                z[i][j] = res;
            }
        }
        return new MatrixZ(z);
    }




    /**
     * Процедура сравнения двух матриц. Ответ: да или нет.
     * @param b типа MatrixZ, сравниваемая матрица
     * @return <tt> this == b </tt> типа boolean
     */
    public boolean equals(Object m) {
        MatrixZ b = (MatrixZ) m;
        if (M.length != b.M.length) {
            return false;
        }
        for (int i = 0; i < M.length; i++) {
            if (M[i].length != b.M[i].length) {
                return false;
            }
        }
        for (int i = 0; i < M.length; i++)
            for (int j = 0; j < M[i].length; j++)
                if (!M[i][j].equals(b.M[i][j]))
                    return false;
        return true;
    }


}
