package com.mathpar.number;

import java.util.Arrays;

/**
 * <p>Title: ParCA</p> <p>Description: ParCA - parallel computer algebra
 * system</p> <p>Copyright: Copyright (c) ParCA Tambov, 2005</p> <p>Company:
 * ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class IntList {

    private final static int INITIAL_CAPACITY = 32;
    public int[] arr;
    public int size = 0;

    public IntList(int capacity) {
        arr = new int[capacity];
    }

    public IntList() {
        this(INITIAL_CAPACITY);
    }

    public IntList(int capacity, int[] a, int s) {
        this(capacity);
        System.arraycopy(a, 0, arr, 0, s);
        size = s;
    }

    public void add(int num) {
        if (size == arr.length) {
            int[] newArr = new int[arr.length * 2];
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            arr = newArr;
        }
        arr[size++] = num;
    }

    public void add(int[] a) {
        if (size + a.length > arr.length) {
            int needLen = size + a.length;
            int len = arr.length;
            while (len < needLen) {
                len *= 2;
            }
            int[] newArr = new int[len];
            System.arraycopy(arr, 0, newArr, 0, size);
            arr = newArr;
        }
        System.arraycopy(a, 0, arr, size, a.length);
        size += a.length;
    }

    public void add(IntList l) {
        add(l.arr);
    }

    public void set(int pos, int num) {
        arr[pos] = num;
    }

    /**
     * Вставляет в позицию pos число num.
     *
     * @param pos int
     * @param num int
     */
    public void insert(int pos, int num) {
        if (pos < 0 || pos > size) {
            throw new IllegalArgumentException(
                    String.format("Error: pos=%d: pos<0 or pos>size=%d", pos, size));
        }
        //Случай pos==size -- это добавление в конец
        if (pos == size) {
            add(num);
            return;
        }
        //Если 0<=pos<size
        if (size == arr.length) {
            //1. Не хватает места в arr
            int[] newArr = new int[arr.length * 2];
            //копируем из arr от 0 до pos не включительно в newArr, в pos запишем
            //число и копируем остаток от pos до size в newArr
            System.arraycopy(arr, 0, newArr, 0, pos);
            newArr[pos] = num;
            System.arraycopy(arr, pos, newArr, pos + 1, size - pos);
        } else {
            //2. Хватает места в arr
            //Сместить arr вправо на 1: [pos,size-1]-->[pos+1,size]
            for (int i = size; i > pos; i--) {
                arr[i] = arr[i - 1];
            }
            arr[pos] = num;
        }
        size++;
    }

    public int[] toArray() {
        if (size == arr.length) {
            return arr;
        }
        int[] resArr = new int[size];
        System.arraycopy(arr, 0, resArr, 0, size);
        return resArr;
    }

    public boolean identic(Object o) {
        IntList l2 = (IntList) o;
        return size == l2.size && eqIntArrays(arr, l2.arr);
    }

    private static boolean eqIntArrays(int[] a1, int[] a2) {
        if (a1.length != a2.length) {
            return false;
        }
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        int[] truncatedArr = new int[size];
        System.arraycopy(arr, 0, truncatedArr, 0, size);
        return Arrays.toString(truncatedArr);
    }
}
