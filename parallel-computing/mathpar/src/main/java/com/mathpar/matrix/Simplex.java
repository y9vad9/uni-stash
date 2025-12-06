package com.mathpar.matrix;

import com.mathpar.number.*;

/**
 * @author mikhailmatveev
 */
public class Simplex {

    private static MatrixD a;
    private static VectorS b, c;
    private static Element z;
    private static Ring ring;

    /*
     * subscripts_b - Массив индексов по базисным переменным
     * subscripts_n - Массив индексов по небазисным переменным
     * indeces      - Массив позиций в subscripts_b и subscripts_n
     *
     * Пример: a_{i,j} = a[indeces[subscripts_b[i]]][indeces[subscripts_n[j]]]
     *
     * Инвариант1: indeces[subscripts_b[i]] <--> Arrays.binarySearch(subscripts_b, i);
     * Инвариант2: indeces[subscripts_n[j]] <--> Arrays.binarySearch(subscripts_n, j);
     *
     * Только выгодней использовать массив индексов, чем бинарный поиск, т.к.
     * солжность обращения к элементу массива обычно занимает время O(1), в то
     * время как бинарный поиск имеет сложность O(n) = log_2(n).
     */
    private static int[] subscripts_b, subscripts_n, indeces;

    /**
     * Проверка размерностей задачи ЛП
     *
     * @param a Матрица условий
     * @param b Вектор ограничений
     * @param c Вектор коэффициентов целевой функции
     */
    private static void check_dimensions(MatrixD a, VectorS b, VectorS c) {
        String err01 = "CONDITION MATRIX and LIMITS VECTOR dimensions mismatch!";
        String err02 = "CONDITION MATRIX and OBJECTIVE FUNCTION COEFFICIENTS VECTOR dimensions mismatch!";

        if (a != null && b != null && c != null) {

            if (a.M.length != b.V.length) {
                throw new IllegalArgumentException(err01);
            }

            for (int i = 0; i < a.M.length; i++) {
                if (c.V.length != a.M[i].length) {
                    throw new IllegalArgumentException(err02);
                }
            }

        }
    }

    /**
     * Алгоритм обмена двух элементов, принадлежащих двум различным массивам со
     * вставкой в нужную позицию при условии, что массив изначально
     * отсортирован.
     *
     * @param x Массив x
     * @param y Массив y
     * @param i Индекс элемента в массиве x
     * @param j Индекс элемента в массиве y
     */
    private static void exchange(int[] x, int[] y, int i, int j) {
        int n = 0, b = 0;
        if (i < j) {
            for (int k = 0; k < x.length && x[k] < j; k++) {
                n = k;
            }
            int len = Math.abs(n - indeces[i]);
            System.arraycopy(x, indeces[i] + 1, x, indeces[i], len);
            x[n] = j;
            for (int k = y.length - 1; k >= 0 && y[k] > i; k--) {
                b = k;
            }
            len = Math.abs(b - indeces[j]);
            System.arraycopy(y, b, y, b + 1, len);
            y[b] = i;
        }
        if (i > j) {
            for (int k = x.length - 1; k >= 0 && x[k] > j; k--) {
                n = k;
            }
            int len = Math.abs(n - indeces[i]);
            System.arraycopy(x, n, x, n + 1, len);
            x[n] = j;
            for (int k = 0; k < y.length && y[k] < i; k++) {
                b = k;
            }
            len = Math.abs(b - indeces[j]);
            System.arraycopy(y, indeces[j] + 1, y, indeces[j], len);
            y[b] = i;
        }
    }

    /**
     * Алгортим замещения Переписывает исходную задачу ЛП в новую (может
     * выполняться многократно на одной и той же итерации симплекс-алгоритма).
     *
     * @param e Индекс выводимой переменной (которая станет базисной)
     * @param l Индекс вводимой переменной (которая станет небазисной)
     */
    private static void replacement(int e, int l) {

        int[] subscripts_n_new = new int[subscripts_n.length];
        System.arraycopy(subscripts_n, 0, subscripts_n_new, 0, subscripts_n.length);

        int[] subscripts_b_new = new int[subscripts_b.length];
        System.arraycopy(subscripts_b, 0, subscripts_b_new, 0, subscripts_b.length);

        int[] indeces_new = new int[indeces.length];
        System.arraycopy(indeces, 0, indeces_new, 0, indeces.length);

        MatrixD a_new = new MatrixD(new Element[a.M.length][a.M[0].length]);
        VectorS b_new = new VectorS(new Element[b.V.length]);

//        System.out.println("");
//        System.out.println("e: " + e + ", l: " + l);
//        System.out.println("subscripts_n: " + Arrays.toString(subscripts_n));
//        System.out.println("subscripts_b: " + Arrays.toString(subscripts_b));
//        System.out.println("indeces: " + Arrays.toString(indeces));
//
//        System.out.println("");
//        System.out.println("exchange");
//        System.out.println("");
        // Переменные x_e и x_l меняются ролями
        // Базисная переменная x_l становится небазисной, а небазисная переменная x_e становится базисной
        exchange(subscripts_n_new, subscripts_b_new, e, l);

        // Изменяем позиции ключей в массиве indeces_new
        for (int i = 0; i < subscripts_n_new.length; i++) {
            indeces_new[subscripts_n_new[i]] = i;
        }

        for (int i = 0; i < subscripts_b_new.length; i++) {
            indeces_new[subscripts_b_new[i]] = i;
        }

//        System.out.println("subscripts_n_new: " + Arrays.toString(subscripts_n_new));
//        System.out.println("subscripts_b_new: " + Arrays.toString(subscripts_b_new));
//        System.out.println("indeces_new: " + Arrays.toString(indeces_new));
//        System.out.println("");
        // Вычисление коэффициентов уравнения для новой базисной переменной x_e
        b_new.V[indeces_new[e]]
                = b.V[indeces[l]].
                divide(a.M[indeces[l]][indeces[e]], ring);

        for (int i = 0; i < subscripts_n.length; i++) {
            int j = subscripts_n[i];
            if (j != e) {
                a_new.M[indeces_new[e]][indeces_new[j]]
                        = a.M[indeces[l]][indeces[j]].
                        divide(a.M[indeces[l]][indeces[e]], ring);
            }
        }

        a_new.M[indeces_new[e]][indeces_new[l]]
                = ring.numberONE.divide(a.M[indeces[l]][indeces[e]], ring);

        // Вычисление коэффициентов остальных ограничений
        for (int i = 0; i < subscripts_b.length; i++) {
            int j = subscripts_b[i];
            if (j != l) {
                b_new.V[indeces_new[j]]
                        = b.V[indeces[j]].
                        subtract(a.M[indeces[j]][indeces[e]].
                                multiply(b_new.V[indeces_new[e]], ring), ring);

                for (int k = 0; k < subscripts_n.length; k++) {
                    int m = subscripts_n[k];
                    if (m != e) {
                        a_new.M[indeces_new[j]][indeces_new[m]]
                                = a.M[indeces[j]][indeces[m]].
                                subtract(a.M[indeces[j]][indeces[e]].
                                        multiply(a_new.M[indeces_new[e]][indeces_new[m]], ring), ring);
                    }
                }

                a_new.M[indeces_new[j]][indeces_new[l]]
                        = a.M[indeces[j]][indeces[e]].
                        multiply(a_new.M[indeces_new[e]][indeces_new[l]], ring).
                        negate(ring);
            }
        }

        // Вычисление оптимального значения целевой функции
        Element z_new
                = z.
                add(c.V[indeces[e]].
                        multiply(b_new.V[indeces_new[e]], ring), ring);

        // Вычисление коэффициентов целевой функции
        VectorS c_new
                = new VectorS(new Element[c.V.length]);

        for (int i = 0; i < subscripts_n.length; i++) {
            int j = subscripts_n[i];
            if (j != e) {
                c_new.V[indeces_new[j]]
                        = c.V[indeces[j]].
                        subtract(c.V[indeces[e]].
                                multiply(a_new.M[indeces_new[e]][indeces_new[j]], ring), ring);
            }
        }

        c_new.V[indeces_new[l]]
                = c.V[indeces[e]].
                multiply(a_new.M[indeces_new[e]][indeces_new[l]], ring).
                negate(ring);

        // Получение новых множеств базисных и небазисных переменных
        subscripts_n = subscripts_n_new;
        subscripts_b = subscripts_b_new;
        indeces = indeces_new;

        // Переписываем исходную задачу ЛП
        a = a_new;
        b = b_new;
        c = c_new;
        z = z_new;

//        System.out.println("");
//        System.out.println("Matrix");
//        System.out.println(a.toString(ring));
//        System.out.println("Limits vector");
//        System.out.println(b.toString(ring));
//        System.out.println("Objective function coefficients");
//        System.out.println(c.toString(ring));
//        System.out.println("Objective function value");
//        System.out.println(z.toString(ring));
//        System.out.println("---------------------------------------------");
    }

    /**
     * Симплекс-алгоритм. Максимизирует линейную функцию (c, x) -> max, где c =
     * (c_1, c_2, ... , c_n)
     *
     * При условиях a_11, a_12, ... , a_1n <= b_1 a_21, a_22, ... , a_2n <= b_2
     * ...................... .. ... a_m1, a_m2, ... , a_mn <= b_m
     *
     * @throws ArithmeticException LP-problem is unbounded!
     * @return Базисное решение задачи ЛП
     */
    private static VectorS simplex() {

        // Итерации симплекс-алгоритма
        int n = 0;

        // Массив строгих ограничений
        Element[] delta = new Element[b.V.length];

        while (n < c.V.length) {
            if (!c.V[n].isNegative() && !c.V[n].isZero(ring)) {

                // Индекс небазисной переменной
                int e = subscripts_n[n];

                for (int i = 0; i < subscripts_b.length; i++) {
                    int j = subscripts_b[i];
                    Element temp = a.M[indeces[j]][indeces[e]];
                    if (!temp.isNegative() && !temp.isZero(ring)) {
                        delta[indeces[j]] = b.V[indeces[j]].divide(temp, ring);
                    } else {
                        delta[indeces[j]] = Element.POSITIVE_INFINITY;
                    }
                }

                // Выбор строгих ограничений
                int index_min = 0;
                Element min = delta[index_min];
                for (int i = 1; i < delta.length; i++) {
                    if (delta[i].subtract(min, ring).isNegative()) {
                        index_min = i;
                        min = delta[index_min];
                    }
                }

                // Индекс базисной переменной
                int l = subscripts_b[index_min];

                if (delta[indeces[l]].equals(Element.POSITIVE_INFINITY)) {
                    throw new ArithmeticException("This problem has not solution.");
                } else {
                    replacement(e, l);/// all to all c , ведущий бросает всем b, n=0
                }
            } else {
                n++; /// all to all c , ведущий бросает всем b, n=n, элемент а ведущий и i,j
            }
        }
        VectorS x = new VectorS(subscripts_n.length + subscripts_b.length, ring.numberZERO);

        for (int i = 0; i < subscripts_b.length; i++) {
            x.V[subscripts_b[i]] = b.V[indeces[subscripts_b[i]]];
        }

        return x;
    }

    /**
     * Решение задачи ЛП при помощи симплекс-алгоритма, заданной в стандартной
     * форме.
     *
     * @param a Матрица условий
     * @param b Вектор ограничений
     * @param c Вектор коээфициентов целевой функции
     * @param ring Кольцо, в котором происходят вычисления
     *
     * @return Оптимальное решение задачи ЛП
     */
    private static VectorS simplex(MatrixD a, VectorS b, VectorS c, Ring ring) {

//         Инициализировать статические поля
        Simplex.a = a;
        Simplex.b = b;
        Simplex.c = c;

        Simplex.z = ring.numberZERO;
        Simplex.ring = ring;

//         Инициализация массивов индексов базисных и небазисных переменных
        subscripts_n = new int[c.V.length];
        subscripts_b = new int[b.V.length];
        indeces = new int[subscripts_n.length + subscripts_b.length];

        for (int i = 0; i < subscripts_n.length; i++) {
            subscripts_n[i] = i;
            indeces[i] = i;
        }

        for (int i = 0, j = subscripts_n.length; i < subscripts_b.length; i++, j++) {
            subscripts_b[i] = j;
            indeces[j] = i;
        }

        long t1 = System.nanoTime();

//         Вызвать симплекс-алгоритм и получить базисный вектор
        VectorS basis = simplex();

        long t2 = System.nanoTime() - t1;
        System.out.println("Simplex algorithm run time: " + (t2 / 1E+9) + " s");

//         Оптимальное решение задачи ЛП
        VectorS optimal = new VectorS(c.V.length, ring.numberZERO);
        System.arraycopy(basis.V, 0, optimal.V, 0, optimal.V.length);

        return optimal;
    }

//    private static VectorS simplex_par(MatrixD conditions, VectorS limits,
//            VectorS objective_function_coefficients, Ring ring) {
//
//        return null;
//    }
    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи: целевая функция максимизируется
     *
     * @param a Матрица условий
     * @param signs Массив знаков условий (Element.EQUAL, Element.LESS_OR_EQUAL,
     * Element.GREATER_OR_EQUAL)
     * @param b Вектор ограничений
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_max(MatrixD a, int[] signs, VectorS b, VectorS c, Ring ring) {

        String err01 = "The number of SIGNS differs from the number of LIMITS!";
        String err02 = "Incorrect condition sign in SIGNS array!";

        if (signs.length != b.V.length) {
            throw new IllegalArgumentException(err01);
        }

        // Защита от "дурака" :-)
        check_dimensions(a, b, c);

        // Проверим, стоит ли нам делать копию исходной задачи, если
        // все знаки в условии "<=". Если это так, то просто вызываем
        // симплекс-алгоритм для этой исходной задачи.
        int n_le = 0;
        for (int i = 0; i < signs.length; i++) {
            if (signs[i] == Element.LESS_OR_EQUAL) {
                n_le++;
            }
        }

        if (n_le == signs.length) {
            return simplex(a, b, c, ring);
        }

        // Если есть и другие знаки в условиях, то сначала
        // посчитаем количество знаков "=", чтобы определить сколько
        // новых строк в матрице и новых элементов в векторе придется добавить.
        int n_eq = 0;
        for (int i = 0; i < signs.length; i++) {
            if (signs[i] == Element.EQUAL) {
                n_eq++;
            }
        }

        Element[][] m = new Element[a.M.length + n_eq][c.V.length];
        Element[] v = new Element[b.V.length + n_eq];

        int j = 0;
        for (int i = 0; i < signs.length; i++) {
            switch (signs[i]) {
                case Element.LESS_OR_EQUAL: {
                    System.arraycopy(a.M[i], 0, m[j], 0, a.M[i].length);
                    v[j++] = b.V[i];
                }
                break;

                case Element.GREATER_OR_EQUAL: {
                    for (int k = 0; k < a.M[i].length; k++) {
                        m[j][k] = a.M[i][k].negate(ring);
                    }
                    v[j++] = b.V[i].negate(ring);
                }
                break;

                case Element.EQUAL: {
                    System.arraycopy(a.M[i], 0, m[j], 0, a.M[i].length);
                    v[j++] = b.V[i];

                    for (int k = 0; k < a.M[i].length; k++) {
                        m[j][k] = a.M[i][k].negate(ring);
                    }
                    v[j++] = b.V[i].negate(ring);
                }
                break;

                default:
                    throw new IllegalArgumentException(err02);
            }
        }

        MatrixD a_new = new MatrixD(m);
        VectorS b_new = new VectorS(v);

        return simplex(a_new, b_new, c, ring);
    }

    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи: целевая функция минимизируется
     *
     * @param a Матрица условий
     * @param signs Массив знаков условий (Element.EQUAL, Element.LESS_OR_EQUAL,
     * Element.GREATER_OR_EQUAL)
     * @param b Вектор ограничений
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_min(MatrixD a, int[] signs, VectorS b, VectorS c, Ring ring) {

        return simplex_max(a, signs, b, c.negate(ring), ring);
    }

    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи: целевая функция максимизируется
     *
     * @param a_le Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} <= b_i
     * @param a_eq Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} = b_i
     * @param a_ge Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} >= b_i
     * @param b_le Вектор ограничений для матрицы a_le
     * @param b_eq Вектор ограничений для матрицы a_eq
     * @param b_ge Вектор ограничений для матрицы a_ge
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_max(MatrixD a_le, MatrixD a_eq, MatrixD a_ge,
            VectorS b_le, VectorS b_eq, VectorS b_ge, VectorS c, Ring ring) {

        // Проверка на существование объектов
        int size_le = (a_le != null && b_le != null) ? b_le.V.length : 0;
        int size_eq = (a_eq != null && b_eq != null) ? b_eq.V.length : 0;
        int size_ge = (a_ge != null && b_ge != null) ? b_ge.V.length : 0;

        // Объединение всех трех матриц и векторов
        // в одну большую матрицу и вектор
        Element[][] m = new Element[size_le + 2 * size_eq + size_ge][c.V.length];
        Element[] v = new Element[size_le + 2 * size_eq + size_ge];

        // Обработка условий
        if (size_le != 0) {

            check_dimensions(a_le, b_le, c);

            for (int i = 0; i < size_le; i++) {
                System.arraycopy(a_le.M[i], 0, m[i], 0, a_le.M[i].length);
            }

            System.arraycopy(b_le.V, 0, v, 0, b_le.V.length);
        }

        MatrixD m_eq = null;

        if (size_eq != 0) {

            check_dimensions(a_eq, b_eq, c);

            m_eq = a_eq.negate(ring);

            for (int i = 0, j = size_le; i < size_eq; i++, j++) {
                System.arraycopy(a_eq.M[i], 0, m[j], 0, a_eq.M[i].length);
            }

            for (int i = 0, j = size_le + size_eq; i < size_eq; i++, j++) {
                System.arraycopy(m_eq.M[i], 0, m[j], 0, m_eq.M[i].length);
            }

            System.arraycopy(b_eq.V, 0, v, size_le, b_eq.V.length);

            VectorS v_eq = b_eq.negate(ring);
            System.arraycopy(v_eq.V, 0, v, size_le + size_eq, v_eq.V.length);
        }

        MatrixD m_ge = null;

        if (size_ge != 0) {

            check_dimensions(a_ge, b_ge, c);

            m_ge = a_ge.negate(ring);

            for (int i = 0, j = size_le + 2 * size_eq; i < size_ge; i++, j++) {
                System.arraycopy(m_ge.M[i], 0, m[j], 0, m_ge.M[i].length);
            }

            VectorS v_ge = b_ge.negate(ring);
            System.arraycopy(v_ge.V, 0, v, size_le + 2 * size_eq, v_ge.V.length);
        }

        MatrixD a_new = new MatrixD(m);
        VectorS b_new = new VectorS(v);

        return simplex(a_new, b_new, c, ring);
    }

    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи:
     *
     * @param a_le Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} <= b_i
     * @param a_eq Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} = b_i
     * @param b_le Вектор ограничений для матрицы a_le
     * @param b_eq Вектор ограничений для матрицы a_eq
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_max(MatrixD a_le, MatrixD a_eq,
            VectorS b_le, VectorS b_eq, VectorS c, Ring ring) {

        return simplex_max(a_le, a_eq, null, b_le, b_eq, null, c, ring);
    }

    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи: целевая функция максимизируется
     *
     * @param a_le Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} <= b_i
     * @param b_le Вектор ограничений для матрицы a_le
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_max(MatrixD a_le, VectorS b_le, VectorS c, Ring ring) {

        return simplex_max(a_le, null, null, b_le, null, null, c, ring);
    }

    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи: Целевая функция минимизируется
     *
     * @param a_le Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} <= b_i
     * @param a_eq Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} = b_i
     * @param a_ge Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} >= b_i
     * @param b_le Вектор ограничений для матрицы a_le
     * @param b_eq Вектор ограничений для матрицы a_eq
     * @param b_ge Вектор ограничений для матрицы a_ge
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_min(MatrixD a_le, MatrixD a_eq, MatrixD a_ge,
            VectorS b_le, VectorS b_eq, VectorS b_ge, VectorS c, Ring ring) {

        return simplex_max(a_le, a_eq, a_ge, b_le, b_eq, b_ge, c.negate(ring), ring);
    }

    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи: Целевая функция минимизируется
     *
     * @param a_le Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} <= b_i
     * @param a_eq Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} = b_i
     * @param b_le Вектор ограничений для матрицы a_le
     * @param b_eq Вектор ограничений для матрицы a_eq
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_min(MatrixD a_le, MatrixD a_eq,
            VectorS b_le, VectorS b_eq, VectorS c, Ring ring) {

        return simplex_max(a_le, a_eq, null, b_le, b_eq, null, c.negate(ring), ring);
    }

    /**
     * Пользовательский метод, который решает задачу ЛП в общем виде Условие
     * задачи: Целевая функция минимизируется
     *
     * @param a_le Матрица условий, в которой все строки подчиняются правилу
     * a_{ij} <= b_i
     * @param b_le Вектор ограничений для матрицы a_le
     * @param c Вектор коэффициентов для целевой функции
     * @param ring Кольцо
     *
     * @return Оптимальное решение задачи ЛП
     */
    public static VectorS simplex_min(MatrixD a_le, VectorS b_le, VectorS c, Ring ring) {

        return simplex_max(a_le, null, null, b_le, null, null, c.negate(ring), ring);
    }

    // Test Simplex
    public static void main(String[] args) {

// Seqential Algorithm Tests
        ring = new Ring("R64[]");
        ring.setFLOATPOS(20);

// Example 1
//        long[][] long_a = {
//            {1, 1, 3},
//            {2, 2, 5},
//            {4, 1, 2}
//        };
//
//        long[] long_b = {30, 24, 36};
//        long[] long_c = {3, 1, 2};
//
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Example 2
//        long[][] long_a = {
//            {5, 2, 3},
//            {1, 6, 2},
//            {4, 0, 3}
//        };
//
//        long[] long_b = {25, 20, 18};
//        long[] long_c = {6, 5, 9};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Example 3
//        long[][] long_a = {
//            {5, 9},
//            {0, 4},
//            {7, 6},
//        };
//
//        long[] long_b = {6, 1, 7};
//        long[] long_c = {2, 4};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
//        long[][] long_a = {
//            {8, 2},
//            {4, 0},
//            {0, 4},
//        };
//
//        long[] long_b = {9, 1, 3};
//        long[] long_c = {7, 5};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
//        long[][] long_a = {
//            {-1, -1},
//            { 2, -1},
//            { 1, -2},
//        };
//
//        long[] long_b = {-7, 1, 2};
//        long[] long_c = { 3, 6};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Example 4
//        long[][] long_a = {
//            { 3, -2},
//            {-1,  2},
//            { 3,  2}
//        };
//
//        long[] long_b = {6, 4, 12};
//        long[] long_c = {1, 2};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Example 5
//        long[][] long_a = {
//            {-1,  1},
//            { 1,  2},
//            { 4, -3}
//        };
//
//        long[] long_b = {2, 7, 6};
//        long[] long_c = {2, 1};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Example 6
//        long[][] long_a = {
//            {1, -1,  2, -1},
//            {2,  1, -1,  0},
//        };
//
//        long[] long_b = {6, -1};
//        long[] long_c = {7, 0, 1, -4};
//
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Example 7
//        long[][] long_a = {
//            {1, 2, 3},
//            {4, 5, 6},
//            {7, 8, 9}
//        };
//
//        long[] long_b = {1, 1, 1};
//        long[] long_c = {1, 5, 9};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Example 8
//        long[][] long_a = {
//            { 4, -1},
//            { 2,  1},
//            {-5,  2}
//        };
//
//        long[] long_b = {8, 10, 2};
//        long[] long_c = {1, 1};
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
// Exapmple 9
//        long[][] long_a = {
//            {  3, 122,  18,  49, 172},
//            { 76, 100, 199,  51, 195},
//            { 96, 174, 167,  65, 254},
//            {224, 116,  96, 244,  68},
//            {248, 238, 108,  84,  85}
//        };
//
//        long[] long_b = {56,  247, 255, 136, 139};
//        long[] long_c = {192, 208, 186, 247,   4};
//
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
//Q[]: (447289143/1815538) = 246.3672712991961611379106358556
//R[], FLOATPOS = 30: 246.367271299196161137910635856
//R64[], FLOATPOS = 30: 246.367271299196180000000000000000
// Example 10
//        10.1
//        long[][] long_a = {
//            {4, -1},
//            {2,  1},
//            {5, -2}
//        };
//
//        long[] long_b = {8, 10, -2};
//        long[] long_c = {1, 1};
//        long[] long_b = {8, 10, -2};
//        long[] long_c = {1, 1};
//        int[] signs = {
//            Element.LESS_OR_EQUAL,
//            Element.LESS_OR_EQUAL,
//            Element.GREATER_OR_EQUAL
//        };
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
//        10.2
        long[][] long_a_le = {
            {4, -1},
            {2, 1}
        };
        MatrixD a_le = new MatrixD(long_a_le, ring);

        long[][] long_a_ge = {
            {5, -2}
        };
        MatrixD a_ge = new MatrixD(long_a_ge, ring);

        long[] long_b_le = {8, 10};
        VectorS b_le = new VectorS(long_b_le, ring);

        long[] long_b_ge = {-2};
        VectorS b_ge = new VectorS(long_b_ge, ring);

        long[] long_c = {1, 1};
        VectorS c = new VectorS(long_c, ring);

//        long[][] long_a_eq = {
//            {-5, 1, 1}
//        };
//
//        long[][] long_a_ge = {
//            { 0, 1, -1},
//            {-8, 1,  2}
//        };
//
//        long[] long_b_eq = {4};
//        long[] long_b_ge = {-2, 6};
//
//        long[] long_c = {6, -2, -8};
//
//        MatrixD a_eq = new MatrixD(long_a_eq, ring);
//        MatrixD a_ge = new MatrixD(long_a_ge, ring);
//
//        VectorS b_eq = new VectorS(long_b_eq, ring);
//        VectorS b_ge = new VectorS(long_b_ge, ring);
//
//        VectorS c = new VectorS(long_c, ring);
//        long[][] long_a = {
//            {1, -1,  2, -1},
//            {2,  1, -1,  0}
//        };
//
//        long[] long_b = {6, -1};
//        long[] long_c = {7,  0, 1, -4};
//
//        int[] signs = {
//            Element.LESS_OR_EQUAL,
//            Element.EQUAL,
//        };
//        long[][] long_a = {
//            {-2,  1, 1},
//            {-1,  1, 3},
//            { 1, -3, 1}
//        };
//
//        long[] long_b = { 4,  6,  2};
//        long[] long_c = {-2, -4, -2};
//
//        int[] signs = {
//            Element.LESS_OR_EQUAL,
//            Element.LESS_OR_EQUAL,
//            Element.LESS_OR_EQUAL,
//        };
//
//        MatrixD a = new MatrixD(long_a, ring);
//        VectorS b = new VectorS(long_b, ring);
//        VectorS c = new VectorS(long_c, ring);
//        10.4
//        long[][] long_a_le = {
//            {1, -1,  2, -1}
//        };
//
//        long[][] long_a_eq = {
//            {2,  1, -1,  0}
//        };
//
//        long[] long_b_le = { 6};
//        long[] long_b_eq = {-1};
//
//        long[] long_c = {7, 0, 1, -4};
//
//        MatrixD a_le = new MatrixD(long_a_le, ring);
//        MatrixD a_eq = new MatrixD(long_a_eq, ring);
//
//        VectorS b_le = new VectorS(long_b_le, ring);
//        VectorS b_eq = new VectorS(long_b_eq, ring);
//
//        VectorS c = new VectorS(long_c, ring);
// Example 11
//        MatrixD a = new MatrixD(new double[][] {
//            {0.554, 0.5714, 0.3522, 0.785, 0.6943, 0.3946, 0.1335, 0.1367},
//            {0.2858, 0.9649, 0.9593, 0.8579, 0.5828, 0.1172, 0.3149, 0.9429},
//            {0.8375, 0.5611, 0.6793, 0.2903, 0.5416, 0.1561, 0.9738, 0.9986},
//            {0.4795, 0.2283, 0.7034, 0.259, 0.4068, 0.227, 0.9944, 0.4582},
//            {0.0819, 0.5959, 0.0402, 0.7023, 0.3291, 0.5987, 0.3018, 0.3459},
//            {0.7828, 0.9753, 0.1802, 0.2689, 0.9764, 0.732, 0.775, 0.583},
//            {0.4767, 0.0702, 0.7747, 0.1666, 0.8377, 0.445, 0.489, 0.9321},
//            {0.248, 0.0113, 0.52, 0.0077, 0.8824, 0.2631, 0.6021, 0.0812}
//        }, ring);
//
//        VectorS b = new VectorS(new double[] {
//            0.1311, 0.4998, 0.6887, 0.9996, 0.078, 0.8655, 0.4543, 0.3397
//        }, ring);
//
//        VectorS c = new VectorS(new double[] {
//            0.825, 0.0964, 0.9998, 0.0778, 0.5211, 0.7872, 0.6938, 0.6701
//        }, ring);
// Example 12 (Big size task with random fill)
//        Random random = new Random(System.nanoTime());
//        MatrixD a = new MatrixD(2048, 2048, 100, new int[] {0, 32}, random, ring);
//        VectorS b = new VectorS(2048, 100, new int[] {0, 32}, random, ring);
//        VectorS c = new VectorS(2048, 100, new int[] {0, 32}, random, ring);
//        Random random = new Random(System.nanoTime());
//        MatrixD a_le = new MatrixD(512, 512, 100, new int[] {0, 64}, random, ring);
//        MatrixD a_eq = new MatrixD(512, 512, 100, new int[] {0, 64}, random, ring);
//        MatrixD a_ge = new MatrixD(512, 512, 100, new int[] {0, 64}, random, ring);
//
//        VectorS b_le = new VectorS(512, 100, new int[] {0, 64}, random, ring);
//        VectorS b_eq = new VectorS(512, 100, new int[] {0, 64}, random, ring);
//        VectorS b_ge = new VectorS(512, 100, new int[] {0, 64}, random, ring);
//
//        VectorS c = new VectorS(512, 100, new int[] {0, 64}, random, ring);
//        System.out.println("INITIAL TASK");
//        System.out.println("Matrix\n" + a.toString(ring));
//        System.out.println("Limits vector\n" + b.toString(ring));
//        System.out.println("Objective function coefficients\n" + c.toString(ring));
        try {
//            VectorS result = Simplex.simplex_max(a, signs, b, c, ring);
//            VectorS result = Simplex.simplex_max(a, signs, b, c, ring);
            VectorS result = Simplex.simplex_max(a_le, null, a_ge, b_le, null, b_ge, c, ring);
//            VectorS result = Simplex.simplex(a, b, c, ring);
            System.out.println("OPTIMAL SOLUTION\n" + result.toString(ring));
//            System.out.println("OPTIMAL OBJECTIVE FUNCTION VALUE\n" + z.toString(ring));

            // Checking for accuracy...
            Element t = ring.numberZERO;
            for (int i = 0; i < c.V.length; i++) {
                t = t.add(c.V[i].multiply(result.V[i], ring), ring);
            }

            // Print values...
            System.out.println("OPTIMAL OBJECTIVE FUNCTION VALUE\n" + z.toString(ring));
            System.out.println("CHECKING VALUE\n" + t.toString(ring));

            // If differecce -> 0, then everything is OK!
            Element e = z.subtract(t, ring);
            System.out.println("Accuracy: " + e.toString(ring));
        } catch (Exception e) {
            System.out.println("Simplex: " + e.getMessage());
        }
    }
}
