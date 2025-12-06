
package com.mathpar.number;
import java.util.Arrays;
/**
 * Целочисленные закрытые интервалы, упорядоченные по возрастанию
 * Для хранения интервалов используется список IntList
 * @author sasha
 */
public class Interval {

    /** Метод подсчета общего количества целых чисел в интервалах
     *
     * @param a
     * @return
     */
    public static int getTotalLength(IntList a) {
        int b = 0;
        for (int i = 0; i < a.size; i += 2) {
            b += a.arr[i + 1] - a.arr[i] + 1;
        }
        return b;
    }

// Метод слияния соседних интервалов, когда конец одного подходит к началу другого
    private static IntList junction(IntList res) {
        IntList res2 = new IntList(res.size);
        int i = 0;
        int j;
        while (i < res.size) {
            j = i + 2;
            while (j < res.size && res.arr[j] == res.arr[j-1]+1) j+= 2;
            res2.add(res.arr[i]);
            res2.add(res.arr[j - 1]);
            i = j;
        }
        return res2;
    }

    // Метод слияния двух списков интервалов
    public static IntList join(IntList a1, IntList a2) {
        IntList res = new IntList(a1.size + a2.size);
        int i = 0; //a1
        int j = 0; //a2
        while (i < a1.size && j < a2.size) {
            if (a1.arr[i + 1] < a2.arr[j]) {
               res.add(a1.arr[i]); res.add(a1.arr[i+1]);i+=2;}
            else {res.add(a2.arr[j]); res.add(a2.arr[j+1]); j+=2;}
        }
        if (i == a1.size) {
            System.arraycopy(a2.arr, j, res.arr, res.size, a2.size - j);
            res.size = res.size + a2.size - j;}
        if (j == a2.size) {
            System.arraycopy(a1.arr, i, res.arr, res.size, a1.size - i);
            res.size = res.size + a1.size - i;}
        return junction(res);
    }

    public static IntList subtraction(IntList a1, IntList a2) {
        for (int i = 0; i < a1.size; i = i + 2) {
            for (int j = 0; j < a2.size; j = j + 2) {
                if (a2.arr[j] == a1.arr[i] && a2.arr[j + 1] == a1.arr[i + 1]) {
                    System.arraycopy(a1.arr, i + 2, a1.arr, i, a1.arr.length - (i + 2));
                    a1.size = a1.size - 2;
                }
                if (a2.arr[j] > a1.arr[i] && a2.arr[j + 1] < a1.arr[i + 1]) {
                    int[] a = new int[]{a2.arr[j + 1] + 1, a1.arr[i + 1]};
                    IntList a3 = new IntList(2);
                    a3.add(a);
                    a1.arr[i + 1] = a2.arr[j] - 1;
                    a1 = join(a3, a1);
                }
                if (a2.arr[j] == a1.arr[i] && a2.arr[j + 1] < a1.arr[i + 1]) {
                    a1.arr[i] = a2.arr[j + 1] + 1;
                }

                if (a2.arr[j] > a1.arr[i] && a2.arr[j + 1] == a1.arr[i + 1]) {
                    a1.arr[i + 1] = a2.arr[j] - 1;
                }
            }
        }
        return a1;
    }

// Разделение списка на нужное количество частей.
    public static IntList[] divideOnParts(IntList procs, int nchast) {
        IntList[] res = new IntList[nchast];
        int countProcs = getTotalLength(procs);
        int part = countProcs / nchast;
        int ost = countProcs % nchast;
        int i = 0;
        int j = 1;
        int begin = procs.arr[i];
        int end = procs.arr[j];
        for (int k = 0; k < nchast; k++) {
            IntList result = new IntList();
            res[k] = result;
            int chast = part;
            if (k < ost) {
                chast++;
            }

            while (chast != 0) {
                if (end - begin + 1 <= chast) {
                    result.add(begin);
                    result.add(end);
                    chast = chast - (end - begin + 1);
                    i += 2;
                    j += 2;
                    begin = procs.arr[i];
                    end = procs.arr[j];
                } else {
                    result.add(begin);
                    result.add(begin + chast - 1);
                    begin += chast;
                    chast = 0;
                }
            }
            Arrays.sort(result.arr, 0, result.size);
        }
        return res;
    }

    // Метод распечатки списка процессоров
    public static void printListProcs(IntList a) {
        for (int i = 0; i < a.size; i++) {
            System.out.print(a.arr[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < a.size / 2; i++) {
            if (a.arr[2 * i + 1] - a.arr[2 * i] == 0) {
                System.out.print(a.arr[2 * i] + ", ");
            } else {
                System.out.print(a.arr[2 * i] + "-" + a.arr[2 * i + 1] + ", ");
            }
        }
        System.out.println();
        System.out.println("Количество процессоров в списке: " + getTotalLength(a));
    }
}



