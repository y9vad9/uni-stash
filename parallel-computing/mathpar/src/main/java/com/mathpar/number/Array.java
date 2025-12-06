package com.mathpar.number;

import com.mathpar.func.*;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.polynom.*;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mathpar.number.Element.LESS;
/*
 * ParCa.utils.Array	v.0.9  24.02.2007
 * Copyright 2007 ParCA.  All rights reserved.
 */

/**
 * This class contains various methods for array sorting. This class also
 * contains a static factory that allows arrays to be viewed as lists.
 *
 * @version 0.1, 24.04.2009 Possible Exceptions in some methods: (need to check
 * later) throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
 * throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
 * <tt>toIndex &gt; a.length</tt>
 */
public class Array {
    //public Array(){}
    public static void p(String s) {
        System.out.println(s);
    }
//
//    ++++++++++++++++++++++++  S O R T I N G ++++++++++++++++++++++
//

    /**
     * This method returns sorted up array a. The array a is not changed.
     *
     * @param a the array of numbers to be sorted.
     *
     * @return sorted up array a.
     */
    public static int[] sortUp(int[] a) {
        int n = a.length;
        int[] p = sortPosUp(a);
        int[] b = new int[n];
        for (int i = 0; i < n; i++) {
            b[i] = a[p[i]];
        }
        return b;
    }

    /**
     * This method returns sorted Matrix a, according sorting up row N. The
     * array a[][] is not changed.
     *
     * @param a the sorted matrix of int numbers to be sorted.
     * @param N integer - main row number, which will be sorted up
     *
     * @return sorted matrix a.
     */
    public static int[][] sortMatrixAccordingRowN(int[][] a, int N) {
        int n = a.length;
        int[] b = a[N];
        int m = b.length;
        int[] p = sortPosUp(b);
        int[][] c = new int[n][m];
        for (int j = 0; j < n; j++) {
            int[] aa = a[j];
            int[] cc = new int[m];
            for (int i = 0; i < m; i++) {
                cc[i] = aa[p[i]];
            }
            c[j] = cc;
        }
        return c;
    }

    /**
     * This method returns sorted up array a. The array a is not changed.
     *
     * @param a the array of numbers to be sorted.
     *
     * @return sorted up array a.
     */
    public static long[] sortUp(long[] a) {
        int n = a.length;
        int[] p = sortPosUp(a);
        long[] b = new long[n];
        for (int i = 0; i < n; i++) {
            b[i] = a[p[i]];
        }
        return b;
    }// ----------------------------------

    public static Element[] sortUp(Element[] a, Ring ring) {
        int n = a.length;
        int[] p = sortPosUp(a, ring);
        Element[] b = new Element[n];
        for (int i = 0; i < n; i++) {
            b[i] = a[p[i]];
        }
        return b;
    }//------------------------------------

    public static boolean equals(Element[] el1, Element[] el2, Ring ring) {
        if (el1 == el2) {
            return true;
        }
        if (el2 == null || el2 == null) {
            return false;
        }

        int length = el1.length;
        if (el2.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            Element e1 = el1[i];
            Element e2 = el2[i];
            if (!(e1 == null ? e2 == null : e1.equals(e2, ring))) {
                return false;
            }
        }

        return true;
    }

    /////////////////////////////  SORT POSITIONS  ///////////////////////////////////// ///////////////////////////////////////////////////////////////////////////////////
    /**
     * SortPosUp returns the array of the positions of sorted array a. The
     * complexity of the algorithm is n*log(n), where n=a.length.
     * a[sortPosUp[0]] - is a smallest elament (a[sortPosDown[0]] - is a biggest
     * elament ) a[sortPosUp[n-1] - is a biggest Element (a[sortPosUp[n-1] - is
     * a smallest Element ) among a[first] and a[first+number-1]. The array a
     * does not changes.
     *
     * <p>The sorting algorithm is a "junction sorting". We sort each track of
     * the length 2, then each track of the length 4,8,16,...
     *
     * @param a the array to be sorted.
     *
     * @return sorted indeces from 0 to (a.length-1).
     */
// ///////////////////// int-Array /////////////////////////////////////////////////
    public static int[] sortPosUp(int[] a) {
        int number = a.length;
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = i;
        }
        int[] tmp = new int[number];
        sortPosUp(a, pos, number, tmp);
        return pos;
    }// ----------------------------

    public static int[] sortPosUp(long[] a) {
        int number = a.length;
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = i;
        }
        int[] tmp = new int[number];
        sortPosUp(a, pos, number, tmp);
        return pos;
    }// -----------------------------

    public static int[] sortPosUp(Element[] a, Ring ring) {
        int number = a.length;
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = i;
        }
        int[] tmp = new int[number];
        sortPosUp(a, pos, number, tmp, ring);
        return pos;
    }// -----------------------------

    /**
     * Sorts the positions of specified range of the specified array into
     * numerical order. The range which positions to be sorted extends from
     * index <tt>first</tt>, inclusive, to index <tt>first+number</tt>,
     * exclusive. (If <tt>number==0</tt>, the range to be sorted is empty.)
     *
     * <p>The sorting algorithm is a "junction sorting".
     *
     * @param a the array to be sorted.
     * @param first the index of the first Element (inclusive) to be sorted.
     * @param number the number of sorted Elements.
     *
     * @return sorted indeces (the numbers from <tt>first<tt> to
     * <tt>first+n-1<tt>)
     */
    public static int[] sortPosUp(int[] a, int first, int number) {
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = first + i;
        }
        int[] tmp = new int[number];
        sortPosUp(a, pos, number, tmp);
        return pos;
    }

    /**
     * Sorts the positions of specified range of the specified array into
     * numerical order. The range which positions to be sorted extends from
     * index <tt>first</tt>, inclusive, to index <tt>first+number</tt>,
     * exclusive. (If <tt>number==0</tt>, the range to be sorted is empty.)
     *
     * <p>The sorting algorithm is a "junction sorting".
     *
     * @param a the array to be sorted.
     * @param first the index of the first Element (inclusive) to be sorted.
     * @param number the number of sorted Elements.
     *
     * @return sorted indeces (the numbers from <tt>first<tt> to
     * <tt>first+n-1<tt>)
     */
    public static int[] sortPosUp(long[] a, int first, int number) {
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = first + i;
        }
        int[] tmp = new int[number];
        sortPosUp(a, pos, number, tmp);
        return pos;
    } // -------------------------------------------------

    public static int[] sortPosUp(Element[] a, int first, int number, Ring ring) {
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = first + i;
        }
        int[] tmp = new int[number];
        sortPosUp(a, pos, number, tmp, ring);
        return pos;
    } // -------------------------------------------------

    public static void sortPosUp(int[] a, int[] pos) {
        int n = pos.length;
        int[] tmp = new int[n];
        sortPosUp(a, pos, n, tmp);
    } // ------------------------------------

    public static void sortPosUp(long[] a, int[] pos) {
        int n = pos.length;
        int[] tmp = new int[n];
        sortPosUp(a, pos, n, tmp);
    }//--------------------------------------

    public static void sortPosUp(Element[] a, int[] pos, Ring ring) {
        int n = pos.length;
        int[] tmp = new int[n];
        sortPosUp(a, pos, n, tmp, ring);
    }//--------------------------------------

    /**
     * SortPosUp returns the array of the positions of sorted up array
     * {a[pos[0]],...,a[pos[n-1]]}. The complexity of the algorithm is n*log(n),
     * where n &lt; pos.length. a[pos[sortPos[0]] - is a smallest elament in
     * this array a[pos[sortPos[n-1]] - is a biggest Element in this array. The
     * array a does not changes. <p>Algorithm: We sort each track of the length
     * 2, then each track of the length 4,8,16,... The sorting algorithm is a
     * "junction sorting".
     *
     * @param a the main array, some of its Elements to be sorted.
     * @param n the number of array a Elements to be sorted.
     * @param pos the array with positions of sorted Elements.
     * @param tmp the temporary array of the length equals n. It returns the
     * sorted pos array in the place of pos array.
     */
    public static void sortPosUp(long[] a, int[] pos, int n, int[] tmp) {
        if (n > pos.length) {
            n = pos.length;
        }
        if (n == 1) {
            tmp[0] = pos[0];
            return;
        }
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current pointer in tmp vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[pos[u1]] < a[pos[u2]]) {
                        tmp[k++] = pos[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        tmp[k++] = pos[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    while (u2 < w2) {
                        tmp[k++] = pos[u2++];
                    }
                } else {
                    while (u1 < w1) {
                        tmp[k++] = pos[u1++];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        while (u1 < n) {
                            tmp[k++] = pos[u1++];
                        }
                        break;
                    }
                }
            }         // Have done sorting with current track of numberOfVar s
            int[] m = tmp;
            tmp = pos;
            pos = m;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                tmp[v] = pos[v];
            }
        }
    }

    /**
     * SortPosUp returns the array of the positions of sorted up array
     * {a[pos[0]],...,a[pos[n-1]]}. The complexity of the algorithm is n*log(n),
     * where n &lt; pos.length. a[pos[sortPos[0]] - is a smallest elament in
     * this array a[pos[sortPos[n-1]] - is a biggest Element in this array. The
     * array a does not changes. <p>Algorithm: We sort each track of the length
     * 2, then each track of the length 4,8,16,... The sorting algorithm is a
     * "junction sorting".
     *
     * @param a the main array, some of its Elements to be sorted.
     * @param n the number of array a Elements to be sorted.
     * @param pos the array with positions of sorted lements.
     * @param tmp the temporary array of the length equals n. It returns the
     * sorted pos array in the place of pos array.
     */
    public static void sortPosUp(int[] a, int[] pos, int n, int[] tmp) {
        // int n=pos.length;
        int length = a.length;
        if (n > pos.length) {
            n = pos.length;
        }
        if (n == 1) {
            tmp[0] = pos[0];
            return;
        }
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in tmp vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[pos[u1]] < a[pos[u2]]) {
                        tmp[k++] = pos[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        tmp[k++] = pos[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    while (u2 < w2) {
                        tmp[k++] = pos[u2++];
                    }
                } else {
                    while (u1 < w1) {
                        tmp[k++] = pos[u1++];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        while (u1 < n) {
                            tmp[k++] = pos[u1++];
                        }
                        break;
                    }
                }
            }          // Have done sorting with current track of numberOfVar s
            int[] m = tmp;
            tmp = pos;
            pos = m;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                tmp[v] = pos[v];
            }
        }
    }//-----------------------------------------------------------------------

    public static void sortPosUp(Element[] a, int[] pos, int n, int[] tmp, Ring ring) {
        //   System.out.println(Array.toString(a) + Array.toString(pos));
        // int n=pos.length;
        int length = a.length;
        if (n > pos.length) {
            n = pos.length;
        }
        if (n == 1) {
            tmp[0] = pos[0];
            return;
        }
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in tmp vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    //   System.out.println(a[pos[u1]]+ "    "+a[pos[u2]]+"    ");
                    if ((a[pos[u1]]).compareTo(a[pos[u2]], ring) < 0) {
                        tmp[k++] = pos[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        tmp[k++] = pos[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    while (u2 < w2) {
                        tmp[k++] = pos[u2++];
                    }
                } else {
                    while (u1 < w1) {
                        tmp[k++] = pos[u1++];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        while (u1 < n) {
                            tmp[k++] = pos[u1++];
                        }
                        break;
                    }
                }
            }          // Have done sorting with current track of numberOfVar s
            int[] m = tmp;
            tmp = pos;
            pos = m;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                tmp[v] = pos[v];
            }
        }
    }//-----------------------------------------------------------------------

// **********************************************************************************
//___________________________________________________________________________________
// This method  returns sorted down array a. The array a has not changed.
    public static int[] sortDown(int[] a) {
        int n = a.length;
        int[] p = sortPosDown(a);
        int[] b = new int[n];
        n--;
        for (int i = 0; i <= n; i++) {
            b[i] = a[p[i]];
        }
        return b;
    }// ------------------------------

    public static long[] sortDown(long[] a) {
        int n = a.length;
        int[] p = sortPosDown(a);
        long[] b = new long[n];
        n--;
        for (int i = 0; i <= n; i++) {
            b[i] = a[p[i]];
        }
        return b;
    }//-----------------------------------------

    public static Element[] sortDown(Element[] a, Ring ring) {
        int n = a.length;
        int[] p = sortPosDown(a, ring);
        Element[] b = new Element[n];
        n--;
        for (int i = 0; i <= n; i++) {
            b[i] = a[p[i]];
        }
        return b;
    }//-----------------------------------------

    /**
     * SortPosDown returns the array of the positions of sorted array a. The
     * complexity of the algorithm is n*log(n), where n=a.length.
     * a[sortPosDown[0]] - is a biggest elament and a[sortPosUp[n-1] - is a
     * smallest Element among a[first] and a[first+number-1]. The array a does
     * not changes.
     *
     * <p>The sorting algorithm is a "junction sorting". We sort each track of
     * the length 2, then each track of the length 4,8,16,...
     *
     * @param a the array to be sorted.
     *
     * @return sorted indeces from 0 to (a.length-1).
     */
    public static int[] sortPosDown(int[] a) {
        int number = a.length;
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = i;
        }
        int[] tmp = new int[number];
        sortPosDown(a, pos, number, tmp);
        return pos;
    }

    /**
     * SortPosDown returns the array of the positions of sorted array a. The
     * complexity of the algorithm is n*log(n), where n=a.length.
     * a[sortPosDown[0]] - is a biggest elament and a[sortPosUp[n-1] - is a
     * smallest Element The array a does not changes.
     *
     * <p>The sorting algorithm is a "junction sorting". We sort each track of
     * the length 2, then each track of the length 4,8,16,...
     *
     * @param a the array to be sorted.
     *
     * @return sorted indeces from 0 to (a.length-1).
     */
    public static int[] sortPosDown(long[] a) {
        int number = a.length;
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = i;
        }
        int[] tmp = new int[number];
        sortPosDown(a, pos, number, tmp);
        return pos;
    }//-----------------------------------------------

    public static int[] sortPosDown(Element[] a, Ring ring) {
        int number = a.length;
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = i;
        }
        int[] tmp = new int[number];
        sortPosDown(a, pos, number, tmp, ring);
        return pos;
    }//-----------------------------------------------

    /**
     * SortPosDown returns the array of the positions of sorted array a. The
     * complexity of the algorithm is n*log(n), where n=a.length.
     * a[sortPosDown[0]] - is a biggest elament and a[sortPosUp[n-1] - is a
     * smallest Element among a[first] and a[first+number-1]. The array a does
     * not changes. <p>The sorting algorithm is a "junction sorting". We sort
     * each track of the length 2, then each track of the length 4,8,16,...
     *
     * @param a the array to be sorted.
     *
     * @return sorted indeces from 0 to (a.length-1).
     *
     * @param first the index of the first Element to be sorted.
     * @param number the number of sorted Elements.
     *
     * @return sorted indeces (the numbers <tt>first<tt> to <tt>first+n-1<tt>)
     */
    public static int[] sortPosDown(int[] a, int first, int number) {
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = first + i;
        }
        int[] tmp = new int[number];
        sortPosDown(a, pos, number, tmp);
        return pos;
    }

    /**
     * SortPosDown returns the array of the positions of sorted array a. The
     * complexity of the algorithm is n*log(n), where n=a.length.
     * a[sortPosDown[0]] - is a biggest elament and a[sortPosUp[n-1] - is a
     * smallest Element among a[first] and a[first+number-1]. The array a does
     * not changes. <p>The sorting algorithm is a "junction sorting". We sort
     * each track of the length 2, then each track of the length 4,8,16,...
     *
     * @param a the array to be sorted.
     * @param first the index of the first Element to be sorted.
     * @param number the number of sorted Elements.
     *
     * @return sorted indeces from 0 to (a.length-1).
     */
    public static int[] sortPosDown(long[] a, int first, int number) {
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = first + i;
        }
        int[] tmp = new int[number];
        sortPosDown(a, pos, number, tmp);
        return pos;
    }//--------------------------------------------------------

    public static int[] sortPosDown(Element[] a, int first, int number, Ring ring) {
        int[] pos = new int[number];
        for (int i = 0; i < number; i++) {
            pos[i] = first + i;
        }
        int[] tmp = new int[number];
        sortPosDown(a, pos, number, tmp, ring);
        return pos;
    }//--------------------------------------------------------

    /**
     * SortPosDown returns sorted array pos which is the array of positions of
     * array a Elements. The complexity of the algorithm is n*log(n), where
     * n=a.length. a[pos[0]] - is a biggest elament and a[pos[pos.length-1]] -
     * is a smallest Element The array a does not changes. <p>The sorting
     * algorithm is a "junction sorting". We sort each track of the length 2,
     * then each track of the length 4,8,16,...
     *
     * @param a the initial array.
     * @param pos the array of positions of some Elements of array a which have
     * to be sorted.
     */
    public static void sortPosDown(int[] a, int[] pos) {
        int n = pos.length;
        int[] tmp = new int[n];
        sortPosDown(a, pos, n, tmp);
    }

    /**
     * SortPosDown returns sorted array pos which is the array of positions of
     * array a Elements. The complexity of the algorithm is n*log(n), where
     * n=a.length. a[pos[0]] - is a biggest elament and a[pos[pos.length-1]] -
     * is a smallest Element The array a does not changes. <p>The sorting
     * algorithm is a "junction sorting". We sort each track of the length 2,
     * then each track of the length 4,8,16,...
     *
     * @param a the initial array.
     * @param pos the array of positions of some Elements of array a which have
     * to be sorted.
     */
    public static void sortPosDown(long[] a, int[] pos) {
        int n = pos.length;
        int[] tmp = new int[n];
        sortPosDown(a, pos, n, tmp);
    }

    public static void sortPosDown(Element[] a, int[] pos, Ring ring) {
        int n = pos.length;
        int[] tmp = new int[n];
        sortPosDown(a, pos, n, tmp, ring);
    }

    /**
     * SortPosDown returns the array of the positions of sorted down array
     * {a[pos[0]],...,a[pos[n-1]]}. The complexity of the algorithm is n*log(n),
     * where n &lt; pos.length. a[pos[sortPos[0]] - is a biggest elament in this
     * array a[pos[sortPos[n-1]] - is a smallest Element in this array. The
     * array a does not changes. <p>Algorithm: We sort each track of the length
     * 2, then each track of the length 4,8,16,... The sorting algorithm is a
     * "junction sorting".
     *
     * @param a the main array, some of its Elements to be sorted.
     * @param n the number of array a Elements to be sorted.
     * @param pos the array with positions of sorted lements.
     * @param tmp the temporary array of the length equals n. It returns the
     * sorted pos array in the place of pos array.
     */
    public static void sortPosDown(long[] a, int[] pos, int n, int[] tmp) {
        int length = a.length;
        if (n > pos.length) {
            n = pos.length;
        }
        if (n == 1) {
            tmp[0] = pos[0];
            return;
        }
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in tmp vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[pos[u1]] > a[pos[u2]]) {
                        tmp[k++] = pos[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        tmp[k++] = pos[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    while (u2 < w2) {
                        tmp[k++] = pos[u2++];
                    }
                } else {
                    while (u1 < w1) {
                        tmp[k++] = pos[u1++];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        while (u1 < n) {
                            tmp[k++] = pos[u1++];
                        }
                        break;
                    }
                }
            }          // Have done sorting with current track of numberOfVar s
            int[] m = tmp;
            tmp = pos;
            pos = m;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                tmp[v] = pos[v];
            }
        }
    }

    /**
     * SortPosDown returns the array of the positions of sorted down array
     * {a[pos[0]],...,a[pos[n-1]]}. The complexity of the algorithm is n*log(n),
     * where n &lt; pos.length. a[pos[sortPos[0]] - is a biggest elament in this
     * array a[pos[sortPos[n-1]] - is a smallest Element in this array. The
     * array a does not changes. <p>Algorithm: We sort each track of the length
     * 2, then each track of the length 4,8,16,... The sorting algorithm is a
     * "junction sorting".
     *
     * @param a the main array, some of its Elements to be sorted.
     * @param n the number of array a Elements to be sorted.
     * @param pos the array with positions of sorted lements.
     * @param tmp the temporary array of the length equals n. It returns the
     * sorted pos array in the place of pos array.
     */
    public static void sortPosDown(int[] a, int[] pos, int n, int[] tmp) {
        // int n=pos.length;
        int length = a.length;
        if (n > pos.length) {
            n = pos.length;
        }
        if (n == 1) {
            tmp[0] = pos[0];
            return;
        }
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in tmp vector
        boolean flag = false; // flag of even number of permutations of pos and tmp arrays
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[pos[u1]] > a[pos[u2]]) {
                        tmp[k++] = pos[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        tmp[k++] = pos[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    while (u2 < w2) {
                        tmp[k++] = pos[u2++];
                    }
                } else {
                    while (u1 < w1) {
                        tmp[k++] = pos[u1++];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        while (u1 < n) {
                            tmp[k++] = pos[u1++];
                        }
                        break;
                    }
                }
            }          // Have done sorting with current track of numberOfVar s
            int[] m = tmp;
            tmp = pos;
            pos = m;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                tmp[v] = pos[v];
            }
        }
    } // ----------------------------------------------------------------------------

    /**
     * Сортирует массив указателей pos на элементы массива a в порядке убывания
     * величин элементов массива a. Массив a не изменяется. Массив tmp играет
     * вспомогательную роль и изменяется в процессе вычислений.
     *
     * @param a - главный массив. Сравниваются величины его элементов. На его
     * элементы указывают элементы массива pos.
     * @param pos - позиции в массиве a, которые будут расставлены по убыванию
     * величины элементов массива a
     * @param n - число элементов в pos, которые рассматриваются, остальные не
     * учитываются. Обычно n=pos.length.
     * @param tmp - вспомогательный массив, tmp.lentrh=pos.lenth
     */
    public static void sortPosDown(Element[] a, int[] pos, int n, int[] tmp, Ring ring) {
        //int length=a.length;
        if (n > pos.length) {
            n = pos.length;
        }
        if (n == 1) {
            return;
        }
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in tmp vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[pos[u1]].compareTo(a[pos[u2]], ring) > 0) {
                        tmp[k++] = pos[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        tmp[k++] = pos[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    while (u2 < w2) {
                        tmp[k++] = pos[u2++];
                    }
                } else {
                    while (u1 < w1) {
                        tmp[k++] = pos[u1++];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        while (u1 < n) {
                            tmp[k++] = pos[u1++];
                        }
                        break;
                    }
                }
            }          // Have done sorting with current track of numberOfVar s
            int[] m = tmp;
            tmp = pos;
            pos = m;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            System.arraycopy(pos, 0, tmp, 0, n);
        }
    }//-------------------------------------------------------------------------

    
    /**
     * Joining of two sorted arrays b[0]..b[pointer] (with array of positions
     * "bP") and a[nsp[0]]..a[nsp[n2]]. The total length is equal n=b.length.
     * (1). Let min=a[nsp[0]] -- the smallest Element in the second array. We
     * find all Elements in array b, that grater then min and move them to the
     * end of the b array. (2). Then we scan this part of b array and second
     * array and joind them in the b array. The resulted array is b.
     */
    private static void jointPosUp(long[] a, long[] b, int[] bP, int[] nsp, int n2, int pointer) {
        int n = b.length;
        int aP = nsp[0]; // the pointer to the first (smallest) Element in the second array
        long min = a[aP]; // the smallest Element in the second array
        int i = pointer, t1 = n;
        while ((i >= 0) && (b[i] > min)) {
            b[--t1] = b[i];
            bP[t1] = bP[i--];
        }
        i++;
        int t2 = 0;
        if ((n > t1) && (n2 > 0)) {
            while (true) {
                if (b[t1] < a[aP]) {
                    b[i] = b[t1];
                    bP[i++] = bP[t1++];
                    if (t1 == n) {
                        break;
                    }
                } else {
                    b[i] = a[aP];
                    bP[i++] = aP;
                    aP = nsp[++t2];
                    if (t2 == n2) {
                        break;
                    }
                }
            }
        }
        while (t2 < n2) {
            aP = nsp[t2++];
            bP[i] = aP;
            b[i++] = a[aP];
        }
        // The first array is empty. We move the Elements of the second array.
    }//---------------------------------------------------------------------------

    private static void jointPosUp(int[] a, int[] b, int[] bP, int[] nsp, int n2, int pointer) {
        int n = b.length;
        int aP = nsp[0]; // the pointer to the first (smallest) Element in the second array
        int min = a[aP]; // the smallest Element in the second array
        int i = pointer, t1 = n;
        while ((i >= 0) && (b[i] > min)) {
            b[--t1] = b[i];
            bP[t1] = bP[i--];
        }
        i++;
        int t2 = 0;
        if ((n > t1) && (n2 > 0)) {
            while (true) {
                if (b[t1] < a[aP]) {
                    b[i] = b[t1];
                    bP[i++] = bP[t1++];
                    if (t1 == n) {
                        break;
                    }
                } else {
                    b[i] = a[aP];
                    bP[i++] = aP;
                    aP = nsp[++t2];
                    if (t2 == n2) {
                        break;
                    }
                }
            }
        }
        while (t2 < n2) {
            aP = nsp[t2++];
            bP[i] = aP;
            b[i++] = a[aP];
        }
        // The first array is empty. We move the Elements of the second array.
    }//---------------------------------------------------------------------------

    private static void jointPosUp(Element[] a, Element[] b, int[] bP, int[] nsp, int n2, int pointer, Ring ring) {
        int n = b.length;
        int aP = nsp[0]; // the pointer to the first (smallest) Element in the second array
        Element min = a[aP]; // the smallest Element in the second array
        int i = pointer, t1 = n;
        while ((i >= 0) && (b[i].compareTo(min, ring) > 0)) {
            b[--t1] = b[i];
            bP[t1] = bP[i--];
        }
        i++;
        int t2 = 0;
        if ((n > t1) && (n2 > 0)) {
            while (true) {
                if (b[t1].compareTo(a[aP], ring) < 0) {
                    b[i] = b[t1];
                    bP[i++] = bP[t1++];
                    if (t1 == n) {
                        break;
                    }
                } else {
                    b[i] = a[aP];
                    bP[i++] = aP;
                    aP = nsp[++t2];
                    if (t2 == n2) {
                        break;
                    }
                }
            }
        }
        while (t2 < n2) {
            aP = nsp[t2++];
            bP[i] = aP;
            b[i++] = a[aP];
        }
        // The first array is empty. We move the Elements of the second array.
    }//---------------------------------------------------------------------------

    /**
     * Слияние без повторений двух массивов элементов, Element [] a , Element []
     * b --> Element [] res с суммированием сопутствующих массивов типа int[].
     * Если полученный сопутствующий элемент int равен нулю, то ни он, ни
     * элемент res в результат не записываются.
     *
     * @param a -- первый сливаемый массив Element[]
     * @param b -- второй сливаемый массив Element[]
     * @param aa -- сопутствующий a массив int[]
     * @param bb -- сопутствующий b массив int[]
     * @param powers -- сопутствующий массив power[0] для результата
     *
     * @return -- результат слияния без повторений массивов a и b при
     * совпадениях элементов в a[] и b[] сопутствующие элементы в aa[] и b[]
     * складываются
     */
    public static Element[] jointUp(Element[] a, Element[] b, int aa[], int bb[], int[][] powers, Ring ring) {
        int nA = a.length;
        int nB = b.length;
        int n = nA + nB;
        Element[] res = new Element[n];
        int[] pow = new int[n];
        int mA = 0, mB = 0, m = 0;
        while ((mA < nA) && (mB < nB)) {
            int comp = a[mA].compareTo(b[mB], ring);
            if (comp > 0) {
                res[m] = b[mB];
                pow[m] = bb[mB];
                mB++;
                m++;
            } else if (comp < 0) {
                res[m] = a[mA];
                pow[m] = aa[mA];
                mA++;
                m++;
            } else {
                res[m] = b[mB];
                pow[m] = bb[mB] + aa[mA];
                mB++;
                mA++;
                if (pow[m] != 0) {
                    m++;
                }
            }
        }
        while ((mA < nA)) {
            res[m] = a[mA];
            pow[m] = aa[mA];
            mA++;
            m++;
        }
        while ((mB < nB)) {
            res[m] = b[mB];
            pow[m] = bb[mB];
            mB++;
            m++;
        }
        if (m < n) {
            Element[] res1 = new Element[m];
            int[] pow1 = new int[m];
            for (int i = 0; i < m; i++) {
                res1[i] = res[i];
                pow1[i] = pow[i];
            }
            powers[0] = pow1;
            return res1;
        }
        //  System.out.println(": " + pow);
        powers[0] = pow;
        return res;
    }

    /**
     * It returns the array of the positions of sorted up array
     * {a[pos[0]],...,a[pos[n-1]]}. The complexity of the algorithm is n*log(n),
     * where n &lt; pos.length. a[pos[sortPos[0]] - is a smallest elament in
     * this array a[pos[sortPos[n-1]] - is an n-th smallest Element in this
     * array. The array a does not changes. <p>Algorithm: We sort each track of
     * the length 2, then each track of the length 4,8,16,... The sorting
     * algorithm is a "junction sorting".
     *
     * @param a the main array, some of its Elements to be sorted.
     * @param n the number of array a Elements to be sorted.
     */
    public static int[] smallests(long[] a, int n) {
        int len = a.length;
        if (n > len) {
            n = len;
        }
        int n1 = n - 1;
        long[] b = new long[n]; // main sorted part
        int[] bP = sortPosUp(a, 0, n); // main sorted part
        for (int s = 0; s < n; s++) {
            b[s] = a[bP[s]];
        }
        int[] nsp = new int[n];     // addition notsorted part
        int[] sp = new int[n];     // addition sorted part
        int j = n;         // pointer in a
        int i = 0;         // pointer in nsp  (not sorted)
        int pointer = n1;  // pointer in sp  (sorted)
        long biggest = 0;  // the biggest Element in c (not sorted)
        while (j < len) {
            long b_pointer = b[pointer];
            while (j < len) {
                if (a[j] < b_pointer) {
                    nsp[i++] = j;
                    biggest = a[j];
                    break;
                } else {
                    j++;
                }
            }
            if (j == len) {
                break;
            }
            if (biggest >= b[pointer - 1]) {
                b[n1] = biggest;
                bP[n1] = j++;
                i = 0;
                pointer = n1;
            } else {
                begin:
                {
                    while (++j < len) {
                        if (pointer == 0) {
                            sortPosUp(a, nsp, n, sp);
                            for (int i1 = 0; i1 < n; i1++) {
                                int aP = nsp[i1];
                                b[i1] = a[aP];
                                bP[i1] = aP;
                            }
                            i = 0;
                            pointer = n1;
                            j++;
                            break begin;
                        } else if (a[j] < b[pointer]) {
                            nsp[i++] = j;
                            if (a[j] > biggest) {
                                biggest = a[j];
                            }
                            if (biggest > b[--pointer]) {
                                sortPosUp(a, nsp, i, sp);
                                jointPosUp(a, b, bP, nsp, --i, pointer);
                                i = 0;
                                pointer = n1;
                                j++;
                                break begin;
                            }
                            if (pointer == 0) {
                                j--;
                            }
                        }
                    }
                }
            }
        }
        if (i > 0) {
            sortPosUp(a, nsp, i, sp);
            jointPosUp(a, b, bP, nsp, i, --pointer);
        }
        return bP;
    }//---------------------------------------------------------------------

    public static int[] smallests(int[] a, int n) {
        int len = a.length;
        if (n > len) {
            n = len;
        }
        int n1 = n - 1;
        int[] b = new int[n]; // main sorted part
        int[] bP = sortPosUp(a, 0, n); // main sorted part
        for (int s = 0; s < n; s++) {
            b[s] = a[bP[s]];
        }
        int[] nsp = new int[n];     // addition notsorted part
        int[] sp = new int[n];     // addition sorted part
        int j = n;         // pointer in a
        int i = 0;         // pointer in nsp  (not sorted)
        int pointer = n1;  // pointer in sp  (sorted)
        int biggest = 0;  // the biggest Element in c (not sorted)
        while (j < len) {
            int b_pointer = b[pointer];
            while (j < len) {
                if (a[j] < b_pointer) {
                    nsp[i++] = j;
                    biggest = a[j];
                    break;
                } else {
                    j++;
                }
            }
            if (j == len) {
                break;
            }
            if (biggest >= b[pointer - 1]) {
                b[n1] = biggest;
                bP[n1] = j++;
                i = 0;
                pointer = n1;
            } else {
                begin:
                {
                    while (++j < len) {
                        if (pointer == 0) {
                            sortPosUp(a, nsp, n, sp);
                            for (int i1 = 0; i1 < n; i1++) {
                                int aP = nsp[i1];
                                b[i1] = a[aP];
                                bP[i1] = aP;
                            }
                            i = 0;
                            pointer = n1;
                            j++;
                            break begin;
                        } else if (a[j] < b[pointer]) {
                            nsp[i++] = j;
                            if (a[j] > biggest) {
                                biggest = a[j];
                            }
                            if (biggest > b[--pointer]) {
                                sortPosUp(a, nsp, i, sp);
                                jointPosUp(a, b, bP, nsp, --i, pointer);
                                i = 0;
                                pointer = n1;
                                j++;
                                break begin;
                            }
                            if (pointer == 0) {
                                j--;
                            }
                        }
                    }
                }
            }
        }
        if (i > 0) {
            sortPosUp(a, nsp, i, sp);
            jointPosUp(a, b, bP, nsp, i, --pointer);
        }
        return bP;
    }

    public static int[] smallests(Element[] a, int n, Ring ring) {
        int len = a.length;
        if (n > len) {
            n = len;
        }
        int n1 = n - 1;
        Element[] b = new Element[n]; // main sorted part
        int[] bP = sortPosUp(a, 0, n, ring); // main sorted part
        for (int s = 0; s < n; s++) {
            b[s] = a[bP[s]];
        }
        int[] nsp = new int[n];     // addition notsorted part
        int[] sp = new int[n];     // addition sorted part
        int j = n;         // pointer in a
        int i = 0;         // pointer in nsp  (not sorted)
        int pointer = n1;  // pointer in sp  (sorted)
        Element biggest = a[0].zero(ring);  // the biggest Element in c (not sorted)
        while (j < len) {
            Element b_pointer = b[pointer];
            while (j < len) {
                if (a[j].compareTo(b_pointer, ring) < 0) {
                    nsp[i++] = j;
                    biggest = a[j];
                    break;
                } else {
                    j++;
                }
            }
            if (j == len) {
                break;
            }
            if ((biggest.compareTo(b[pointer - 1], ring) > 0) || (biggest.compareTo(b[pointer - 1], ring) == 0)) {
                b[n1] = biggest;
                bP[n1] = j++;
                i = 0;
                pointer = n1;
            } else {
                begin:
                {
                    while (++j < len) {
                        if (pointer == 0) {
                            sortPosUp(a, nsp, n, sp, ring);
                            for (int i1 = 0; i1 < n; i1++) {
                                int aP = nsp[i1];
                                b[i1] = a[aP];
                                bP[i1] = aP;
                            }
                            i = 0;
                            pointer = n1;
                            j++;
                            break begin;
                        } else if (a[j].compareTo(b[pointer], ring) < 0) {
                            nsp[i++] = j;
                            if (a[j].compareTo(biggest, ring) > 0) {
                                biggest = a[j];
                            }
                            if (biggest.compareTo(b[--pointer], ring) > 0) {
                                sortPosUp(a, nsp, i, sp, ring);
                                jointPosUp(a, b, bP, nsp, --i, pointer, ring);
                                i = 0;
                                pointer = n1;
                                j++;
                                break begin;
                            }
                            if (pointer == 0) {
                                j--;
                            }
                        }
                    }
                }
            }
        }
        if (i > 0) {
            sortPosUp(a, nsp, i, sp, ring);
            jointPosUp(a, b, bP, nsp, i, --pointer, ring);
        }
        return bP;
    }//---------------------------------------------------------------------

    /**
     * Сливает два сортированных по убыванию массива указателей на массив "a" в
     * первый из них с сохранением сортировки.
     *
     * @param a -- массив, значения элементов которого сортируются по убыванию
     * (сам не изменяется!)
     * @param pos1 -- первый сортированный по убыванию массив указателей, его
     * длина n2+n1 (результат будет в этом массиве)
     * @param pos2 -- второй сортированный по убыванию массив указателей
     * @param n2 -- число сливаемых элементов во втором массиве
     * @param n1 -- число сливаемых элементов в первом массиве
     */
    static void jointPosDown(int[] a, int[] pos1,
            int[] pos2, int n2, int n1) {
        int n = pos1.length;
        int aP = pos2[0]; // the pointer to the first (biggest) Element in the second array
        long max = a[aP]; // the biggest Element in the second array
        int t1 = n;
        int t2 = 0;
        while ((n1 >= 0) && (a[pos1[n1]] < (max))) {
            pos1[--t1] = pos1[n1--];
        }
        // пересылаем все указатели меньше max в конец массива pos1
        if ((n > t1) && (n2 > 0)) {
            while (true) {
                if (a[pos1[t1]] > (a[aP])) {
                    pos1[++n1] = pos1[t1++];
                    if (t1 == n) {
                        break;
                    }
                } else {
                    pos1[++n1] = aP;
                    aP = pos2[++t2];
                    if (t2 == n2) {
                        break;
                    }
                }
            }
        }
        while (t2 < n2) {
            aP = pos2[t2++];
            pos1[++n1] = aP;
        }
    } //--------------------------------------------------------------------------

    /**
     * Сливает два сортированных по убыванию массива указателей на массив "a" в
     * первый из них с сохранением сортировки.
     *
     * @param a -- массив, значения элементов которого сортируются по убыванию
     * (сам не изменяется!)
     * @param pos1 -- первый сортированный по убыванию массив указателей, его
     * длина n2+n1 (результат будет в этом массиве)
     * @param pos2 -- второй сортированный по убыванию массив указателей
     * @param n2 -- число сливаемых элементов во втором массиве
     * @param n1 -- число сливаемых элементов в первом массиве
     */
    static void jointPosDown(long[] a, int[] pos1,
            int[] pos2, int n2, int n1) {
        int n = pos1.length;
        int aP = pos2[0]; // the pointer to the first (biggest) Element in the second array
        long max = a[aP]; // the biggest Element in the second array
        int t1 = n;
        int t2 = 0;
        while ((n1 >= 0) && (a[pos1[n1]] < (max))) {
            pos1[--t1] = pos1[n1--];
        }
        // пересылаем все указатели меньше max в конец массива pos1
        if ((n > t1) && (n2 > 0)) {
            while (true) {
                if (a[pos1[t1]] > (a[aP])) {
                    pos1[++n1] = pos1[t1++];
                    if (t1 == n) {
                        break;
                    }
                } else {
                    pos1[++n1] = aP;
                    aP = pos2[++t2];
                    if (t2 == n2) {
                        break;
                    }
                }
            }
        }
        while (t2 < n2) {
            aP = pos2[t2++];
            pos1[++n1] = aP;
        }
    } //--------------------------------------------------------------------------

    /**
     * Сливает два сортированных по убыванию массива указателей на массив "a" в
     * первый из них с сохранением сортировки.
     *
     * @param a -- массив, значения элементов которого сортируются по убыванию
     * (сам не изменяется!)
     * @param pos1 -- первый сортированный по убыванию массив указателей, его
     * длина n2+n1 (результат будет в этом массиве)
     * @param pos2 -- второй сортированный по убыванию массив указателей
     * @param n2 -- число сливаемых элементов во втором массиве
     * @param n1 -- число сливаемых элементов в первом массиве
     */
    static void jointPosDown(Element[] a, int[] pos1,
            int[] pos2, int n2, int n1, Ring ring) {
        int n = pos1.length;
        int aP = pos2[0]; // the pointer to the first (biggest) Element in the second array
        Element max = a[aP]; // the biggest Element in the second array
        int t1 = n;
        int t2 = 0;
        while ((n1 >= 0) && (a[pos1[n1]].compareTo(max, ring) < 0)) {
            pos1[--t1] = pos1[n1--];
        }
        // пересылаем все указатели меньше max в конец массива pos1
        if ((n > t1) && (n2 > 0)) {
            while (true) {
                if (a[pos1[t1]].compareTo(a[aP], ring) > 0) {
                    pos1[++n1] = pos1[t1++];
                    if (t1 == n) {
                        break;
                    }
                } else {
                    pos1[++n1] = aP;
                    aP = pos2[++t2];
                    if (t2 == n2) {
                        break;
                    }
                }
            }
        }
        while (t2 < n2) {
            aP = pos2[t2++];
            pos1[++n1] = aP;
        }
    } //--------------------------------------------------------------------------

    /*
     //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
     public static  int[] biggests(long[] a, int n){
     int len=a.length; if (n>len)n=len;
     int n1=n-1;
     long[] b = new long[n]; // main sorted part
     int[]  bP= sortPosDown(a,0,n); // main sorted part
     for(int s=0;s<n;s++)b[s]=a[bP[s]];
     int[]  nsp=new int[n];     // addition notsorted part
     int[]  sp=new int[n];     // addition sorted part
     int j=n;         // pointer in a
     int i=0;         // pointer in nsp  (not sorted)
     int pointer=n1;  // pointer in sp  (sorted)
     long smallest=0;  // the smallest Element in c (not sorted)
     while(j<len){
     long b_pointer=b[pointer];
     while(j<len)if (a[j]>b_pointer){nsp[i++]=j; smallest=a[j]; break;}else j++;
     if (j==len)break;
     if (smallest<=b[pointer-1]){b[n1]=smallest; bP[n1]=j++;i=0;pointer=n1;}
     else{
     begin:{while(++j<len)
     if (pointer==0){sortPosDown(a,nsp,n,sp);
     for(int i1=0;i1<n;i1++){int aP=nsp[i1]; b[i1]=a[aP]; bP[i1]=aP;}
     i=0; pointer=n1; j++;
     break begin;}
     else
     if(a[j]>b[pointer]){nsp[i++]=j;
     if (a[j]<smallest) smallest=a[j];
     if (smallest>b[--pointer]){
     sortPosDown(a,nsp,i,sp); jointPosDown(a, b, bP, nsp, --i, pointer);
     i=0; pointer=n1; j++;
     break begin;
     }
     if(pointer==0)j--;
     }
     }
     }
     }
     if(i>0){sortPosDown(a,nsp,i,sp);  jointPosDown(a, b, bP, nsp, i, --pointer);}
     return bP;
     }//--------------------------------------------------------------------------

     public static  int[] biggests(int[] a, int n){
     int len=a.length; if (n>len)n=len;
     int n1=n-1;
     int[] b = new int[n]; // main sorted part
     int[]  bP= sortPosDown(a,0,n); // main sorted part
     for(int s=0;s<n;s++)b[s]=a[bP[s]];
     int[]  nsp=new int[n];     // addition notsorted part
     int[]  sp=new int[n];     // addition sorted part
     int j=n;         // pointer in a
     int i=0;         // pointer in nsp  (not sorted)
     int pointer=n1;  // pointer in sp  (sorted)
     int smallest=0;  // the smallest Element in c (not sorted)
     while(j<len){
     int b_pointer=b[pointer];
     while(j<len)if (a[j]>b_pointer){nsp[i++]=j; smallest=a[j]; break;}else j++;
     if (j==len)break;
     if (smallest<=b[pointer-1]){b[n1]=smallest; bP[n1]=j++;i=0;pointer=n1;}
     else{
     begin:{while( j<len)
     if (pointer==0){sortPosDown(a,nsp,n,sp);
     for(int i1=0;i1<n;i1++){int aP=nsp[i1]; b[i1]=a[aP]; bP[i1]=aP;}
     i=0; pointer=n1; j++;
     break begin;}
     else
     if(a[j]>b[pointer]){nsp[i++]=j;
     if (a[j]<smallest) smallest=a[j];
     if (smallest>b[--pointer]){
     sortPosDown(a,nsp,i,sp); jointPosDown(a, b, bP, nsp, --i, pointer);
     i=0; pointer=n1; j++;
     break begin;
     }
     if(pointer==0)j--;
     }
     }
     }
     }
     if(i>0){sortPosDown(a,nsp,i,sp);  jointPosDown(a, b, bP, nsp, i, --pointer);}
     return bP;
     }//----------------------------------------------------------------------
     */
    /**
     * Returns sorted biggest n Elements of array "a" in int[] array of
     * positions of these Elements. The array a does not changes.
     *
     * @param a - the main array.
     * @param n - the number of needed biggest positions.
     */
    public static int[] biggests(Element[] a, int n, Ring ring) {
        int len = a.length;
        if (n > len) {
            n = len;
        }
        int n1 = n - 1;
        int[] bP = sortPosDown(a, 0, n, ring); // main sorted part
        int[] nsp = new int[n];     // addition notsorted part
        int[] sp = new int[n];     // addition sorted part
        int j = n1;         // pointer in a
        int i = 0;         // pointer in nsp  (not sorted)
        int pointer = n1;  // pointer in sp  (sorted)
        Element zero = a[0].zero(ring), smallest = zero;// the smallest Element in nsp (not sorted)
        while (j < len) {
            Element b_pointer = a[bP[pointer]];
            while (++j < len) {
                if (a[j].compareTo(b_pointer, ring) > 0) {
                    nsp[i++] = j;
                    smallest = a[j];
                    pointer--;
                    break;
                }
            }// если текущий больше последнего (b_pointer in b), то бросить его в несортир. и smallest поставить на него
            if (j == len) {
                break;
            }
            if (!(smallest.compareTo(a[bP[pointer]], ring) > 0)) {
                bP[n1] = j;
                i = 0;
                pointer = n1;
            } // Если smallest оказался между первым и вторым с конца, то просто ставим его в конец bP, ИНАЧЕ...
            else {
                while (++j < len) {
                    if ((pointer < 0) || (a[j].compareTo(smallest, ring)) > 0) {
                        System.arraycopy(nsp, 0, bP, pointer + 1, n - pointer - 1);
                        sortPosDown(a, bP, n, sp, ring);
                        i = 0;
                        pointer = n1;
                        smallest = zero;
                    } // Если исчерпался bP, то обновляем его из nsp
                    if (a[j].compareTo(a[bP[pointer]], ring) > 0) {
                        nsp[i++] = j; // вбросили
                        if (a[j].compareTo(smallest, ring) < 0) {
                            smallest = a[j]; //исправили smallest
                        }
                        if ((smallest.compareTo(a[bP[pointer--]], ring) < 0)) {
                            sortPosDown(a, nsp, i, sp, ring);
                            jointPosDown(a, bP, nsp, i, pointer, ring);
                            i = 0;
                            pointer = n1;
                        }
                    }
                }
            }
        }        // Заключительное слияние, если nsp не пуст
        if (i > 0) {
            sortPosDown(a, nsp, i, sp, ring);
            jointPosDown(a, bP, nsp, i, pointer, ring);
        }
        return bP;
    }//--------------------------------------------------------------------------

    /**
     * Returns sorted biggest n Elements of array "a" in int[] array of
     * positions of these Elements. The array a does not changes.
     *
     * @param a - the main array.
     * @param n - the number of needed biggest positions.
     */
    public static int[] biggests(int[] a, int n) {
        int len = a.length;
        if (n > len) {
            n = len;
        }
        int n1 = n - 1;
        int[] bP = sortPosDown(a, 0, n); // main sorted part
        int[] nsp = new int[n];     // addition notsorted part
        int[] sp = new int[n];     // addition sorted part
        int j = n1;         // pointer in a
        int i = 0;         // pointer in nsp  (not sorted)
        int pointer = n1;  // pointer in sp  (sorted)
        int zero = 0, smallest = zero;// the smallest Element in nsp (not sorted)
        while (j < len) {
            int b_pointer = a[bP[pointer]];
            while (++j < len) {
                if (a[j] > (b_pointer)) {
                    nsp[i++] = j;
                    smallest = a[j];
                    pointer--;
                    break;
                }
            }// если текущий больше последнего (b_pointer in b), то бросить его в несортир. и smallest поставить на него
            if (j == len) {
                break;
            }
            if (!(smallest > (a[bP[pointer]]))) {
                bP[n1] = j;
                i = 0;
                pointer = n1;
            } // Если smallest оказался между первым и вторым с конца, то просто ставим его в конец bP, ИНАЧЕ...
            else {
                while (++j < len) {
                    if ((pointer < 0) || (a[j] > (smallest))) {
                        System.arraycopy(nsp, 0, bP, pointer + 1, n - pointer - 1);
                        sortPosDown(a, bP, n, sp);
                        i = 0;
                        pointer = n1;
                        smallest = zero;
                    } // Если исчерпался bP, то обновляем его из nsp
                    if (a[j] > (a[bP[pointer]])) {
                        nsp[i++] = j; // вбросили
                        if (a[j] < (smallest)) {
                            smallest = a[j]; //исправили smallest
                        }
                        if ((smallest < (a[bP[pointer--]]))) {
                            sortPosDown(a, nsp, i, sp);
                            jointPosDown(a, bP, nsp, i, pointer);
                            i = 0;
                            pointer = n1;
                        }
                    }
                }
            }
        }        // Заключительное слияние, если nsp не пуст
        if (i > 0) {
            sortPosDown(a, nsp, i, sp);
            jointPosDown(a, bP, nsp, i, pointer);
        }
        return bP;
    }//--------------------------------------------------------------------------

    /**
     * Returns sorted biggest n Elements of array "a" in int[] array of
     * positions of these Elements. The array a does not changes.
     *
     * @param a - the main array.
     * @param n - the number of needed biggest positions.
     */
    public static int[] biggests(long[] a, int n) {
        int len = a.length;
        if (n > len) {
            n = len;
        }
        int n1 = n - 1;
        int[] bP = sortPosDown(a, 0, n); // main sorted part
        int[] nsp = new int[n];     // addition notsorted part
        int[] sp = new int[n];     // addition sorted part
        int j = n1;         // pointer in a
        int i = 0;         // pointer in nsp  (not sorted)
        int pointer = n1;  // pointer in sp  (sorted)
        long zero = 0, smallest = zero;// the smallest Element in nsp (not sorted)
        while (j < len) {
            long b_pointer = a[bP[pointer]];
            while (++j < len) {
                if (a[j] > (b_pointer)) {
                    nsp[i++] = j;
                    smallest = a[j];
                    pointer--;
                    break;
                }
            }// если текущий больше последнего (b_pointer in b), то бросить его в несортир. и smallest поставить на него
            if (j == len) {
                break;
            }
            if (!(smallest > (a[bP[pointer]]))) {
                bP[n1] = j;
                i = 0;
                pointer = n1;
            } // Если smallest оказался между первым и вторым с конца, то просто ставим его в конец bP, ИНАЧЕ...
            else {
                while (++j < len) {
                    if ((pointer < 0) || (a[j] > (smallest))) {
                        System.arraycopy(nsp, 0, bP, pointer + 1, n - pointer - 1);
                        sortPosDown(a, bP, n, sp);
                        i = 0;
                        pointer = n1;
                        smallest = zero;
                    } // Если исчерпался bP, то обновляем его из nsp
                    if (a[j] > (a[bP[pointer]])) {
                        nsp[i++] = j; // вбросили
                        if (a[j] < (smallest)) {
                            smallest = a[j]; //исправили smallest
                        }
                        if ((smallest < (a[bP[pointer--]]))) {
                            sortPosDown(a, nsp, i, sp);
                            jointPosDown(a, bP, nsp, i, pointer);
                            i = 0;
                            pointer = n1;
                        }
                    }
                }
            }
        }        // Заключительное слияние, если nsp не пуст
        if (i > 0) {
            sortPosDown(a, nsp, i, sp);
            jointPosDown(a, bP, nsp, i, pointer);
        }
        return bP;
    }//--------------------------------------------------------------------------

    /**
     * This method sorting up the first n Elements of array a. Put n=a.length
     * for the sorting of the whole array. The sorting method used the form of
     * "next" list. It needs more 10-20% of time then sort, but in special case
     * (for about sorted array) may be quickly. The util.Arrays.sort is better
     * then sortUp method in 1.6 times for the array of 10^6 random numbers.
     *
     * @param a - the main array.
     * @param n - the number of the first Element for sorting up.
     */
    public static void sortUp(long[] a, int n) {
        long[] m = new long[n]; // m- positions in the vector a after odd stepts of sorting
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in m vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[u1] < a[u2]) {
                        m[k++] = a[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        m[k++] = a[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    for (; u2 < w2; u2++) {
                        m[k++] = a[u2];
                    }
                } else {
                    for (; u1 < w1; u1++) {
                        m[k++] = a[u1];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        for (; u1 < n; u1++) {
                            m[k++] = a[u1];
                        }
                        break;
                    }
                }
            }               // Have done sorting with current track of numberOfVar s
            long[] mt = m;
            m = a;
            a = mt;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                m[v] = a[v];
            }
        } // Changing arrays if even rotations done
    }
// -----------------------------------------------------------------------------------

    public static void sortUp(int[] a, int n) {
        int[] m = new int[n];// m- positions in the vector a after odd stepts of sorting
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in m vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[u1] < a[u2]) {
                        m[k++] = a[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        m[k++] = a[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    for (; u2 < w2; u2++) {
                        m[k++] = a[u2];
                    }
                } else {
                    for (; u1 < w1; u1++) {
                        m[k++] = a[u1];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        for (; u1 < n; u1++) {
                            m[k++] = a[u1];
                        }
                        break;
                    }
                }
            }               // Have done sorting with current track of numberOfVar s
            int[] mt = m;
            m = a;
            a = mt;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                m[v] = a[v];
            }
        } // Changing arrays if even rotations done
    }
// ---------------------------------------------------------------------------------------

    public static void sortUp(Element[] a, int n, Ring ring) {
        Element[] m = new Element[n];// m- positions in the vector a after ord stepts of sorting
        int u1;
        int u2; // current pointers in the first and second parts of the current  s track
        int w1;
        int w2; // the end pointer in the first and second parts of the current s track
        int s = 1;        // s- numberOfVar of the each part of the carrent s track;
        int s2 = 2;       // s2= 2*s - the numberOfVar of the current s track
        int k;          // current poinet in m vector
        boolean flag = false;
        while (s < n) {
            u1 = 0;
            u2 = s;
            w1 = s;
            w2 = s2;
            k = 0;
            if (n < w2) {
                w2 = n;
            }
            while (true) { // Start sorting with current tracks of numberOfVar s
                while (true) {   // Current s tracks are: (u1,w1), (u2,w2)
                    if (a[u1].compareTo(a[u2], ring) < 0) {
                        m[k++] = a[u1++];
                        if (u1 == w1) {
                            break;
                        }
                    } else {
                        m[k++] = a[u2++];
                        if (u2 == w2) {
                            break;
                        }
                    }
                }
                if (u1 == w1) {
                    for (; u2 < w2; u2++) {
                        m[k++] = a[u2];
                    }
                } else {
                    for (; u1 < w1; u1++) {
                        m[k++] = a[u1];
                    }
                }
                if (u2 == n) {
                    break;
                }
                w1 += s2;
                w2 += s2;
                u1 += s;
                u2 += s;
                if (n < w2) {
                    w2 = n;
                    if (n <= w1) {
                        for (; u1 < n; u1++) {
                            m[k++] = a[u1];
                        }
                        break;
                    }
                }
            }               // Have done sorting with current track of numberOfVar s
            Element[] mt = m;
            m = a;
            a = mt;
            s = s2;
            s2 <<= 1;
            flag = !flag;
        }
        if (flag) {
            for (int v = 0; v < n; v++) {
                m[v] = a[v];
            }
        } // Changing arrays if even rotations done
    }//---------------------------------------------------------------------
    // ###############################################################################################################################################
    // ###############################################################################################################################################

    /**
     * Involution: дополнение к множеству ei в множестве {0,1,...,N-1}.
     *
     * @param ei массив хранящий элементы подмножества в {0,1,...,N-1}.
     * @param N order of universal set.
     *
     * @return <tt> involution </tt> -- complimentary subset for ei.
     */
    public static int[] involution(int[] ei, int N) {
        int N1 = ei.length;
        int[] ind = new int[N];
        int[] Dei = new int[N - N1];
        if (N1 < N) {
            for (int j = 0; j < N1; j++) {
                ind[ei[j]] = 1;
            }
            int k = 0;
            for (int j = 0; j < N; j++) {
                if (ind[j] == 0) {
                    Dei[k++] = j;
                }
            }
        }
        return Dei;
    }

    /**
     * It joins of two sorted arrays a and b without doubling Elements in
     * resulting array: equal Elements will appear just one times.
     *
     * @param a - входной отсортированный по возрастанию массив без повторов
     * элементов
     * @param b - входной отсортированный по возрастанию массив без повторов
     * элементов
     *
     * @return слитый из a и b отсортированный по возрастанию массив без
     * повторов элементов
     */
    public static int[] joinSortedArraysWithoutDoubling(int[] a, int b[]) {
        int w1 = a.length;
        int w2 = b.length;
        int l = w1 + w2;
        int[] c = new int[l];
        int u1 = 0;
        int u2 = 0; // current pointers in the first and second arrays
        int k = 0;            // current poinet in c vector
        while ((u2 < w2) && (u1 < w1)) {    // Start join
            if (a[u1] < b[u2]) {
                c[k++] = a[u1++];
            } else {
                if (a[u1] > b[u2]) {
                    c[k++] = b[u2++];
                } else {
                    c[k++] = b[u2++];
                    u1++;
                }
            }
        }
        if (u2 < w2) {
            for (; u2 < w2; u2++) {
                c[k++] = b[u2];
            }
        }
        if (u1 < w1) {
            for (; u1 < w1; u1++) {
                c[k++] = a[u1];
            }
        }
        //   int[] d=c;
        //   if (k<l) d=java.util.Arrays.copyOf(c,k);
        int d[] = new int[k];
        System.arraycopy(c, 0, d, 0, k);
        return (k < l) ? d : c;
        //  return (k<l) ? java.util.Arrays.copyOf(c,k) : c;
    }

    public static int[] joinSortedArraysWithoutDoublingGB(int[] a, int b[]) {
        int w1 = a.length;
        int w2 = b.length;
        int l = w1 + w2;
        int[] c = new int[l];
        int u1 = 0;
        int u2 = 0;
        int k = 0;
        while ((u2 < w2) && (u1 < w1)) {
            if (a[u1] > b[u2]) {
                c[k++] = a[u1++];
            } else if (a[u1] < b[u2]) {
                c[k++] = b[u2++];
            } else {
                c[k++] = b[u2++];
                u1++;
            }
        }
        if (u2 < w2) {
            for (; u2 < w2; u2++) {
                c[k++] = b[u2];
            }
        }
        if (u1 < w1) {
            for (; u1 < w1; u1++) {
                c[k++] = a[u1];
            }
        }
        int d[] = new int[k];
        System.arraycopy(c, 0, d, 0, k);
        return (k < l) ? d : c;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    PRINT  ARRAY OF NUMBERS and SCALARS (int,long,Object)
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Converting to String an array of numbers.
     *
     * @param p - an array of numbers
     */
    public static String toString(int[] p) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < p.length - 1; i++) {
            str.append(p[i] + ", ");
        }
        if (p.length > 0) {
            str.append(p[p.length - 1]);
        }
        str.append("]");
        return str.toString();
    }

    /**
     * Converting to String an array of numbers.
     *
     * @param p - an array of numbers
     */
    public static String toString(long[] p) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < p.length - 1; i++) {
            str.append(p[i] + ", ");
        }
        if (p.length > 0) {
            str.append(p[p.length - 1]);
        }
        str.append("]");
        return str.toString();
    }

    /**
     * Converting to String an array of numbers.
     *
     * @param p - an array of numbers
     */
    public static String toString(double[] p) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < p.length - 1; i++) {
            str.append(p[i] + ", ");
        }
        if (p.length > 0) {
            str.append(p[p.length - 1]);
        }
        str.append("]");
        return str.toString();
    }

    /**
     * Converting to String an array of Scalars.
     *
     * @param p - an array of Scalars
     *
     * @return
     */
    public static String toString(Object[] p) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < p.length - 1; i++) {
            str.append(p[i] + ", ");
        }
        if (p.length > 0) {
            str.append(p[p.length - 1]);
        }
        str.append("]");
        return str.toString();
    }

    public static String toString(Element[] p, Ring ring) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < p.length - 1; i++) {
            str.append(p[i].toString(ring) + ", ");
        }
        if (p.length > 0) {
            str.append(p[p.length - 1].toString(ring));
        }
        str.append("]");
        return str.toString();
    }

    public static String toString(Element[] p) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < p.length - 1; i++) {
            str.append(p[i] + ", ");
        }
        if (p.length > 0) {
            str.append(p[p.length - 1]);
        }
        str.append("]");
        return str.toString();
    }

    /**
     * Печать двумерного массива скаляров в виде матрицы
     *
     * @param matr - двумерный массив скаляров
     */
// public static String toString(Object[][] matr ) {
//     StringBuffer str=new StringBuffer("\n[");
//     int s; // Number of letters in one Object
//     boolean bb=true;
//     int m_length=matr.length;
//     if (m_length==0) s=3;
//     else{
//     int max=0;
//        for (int i = 0; i < m_length; i++) {
//            for (int j = 0; j < matr[i].length; j++) {
//                if (bb) {bb=false;
//                max = matr[i][j].toString().length();}
//                if (matr[i][j].toString().length() > max) max=matr[i][j].toString().length();}}
//     if (bb) s=2; else s = max +1;} m_length--;
//     String zap=",";
//        for (int i = 0; i < m_length; i++) {str.append("[");
//            for (int j = 0; j < matr[i].length-1; j++) str.append(printf(s, matr[i][j], zap, r));
//            if(matr[i].length!=0) str.append(printf(s, matr[i][matr[i].length-1],""));
//            str.append(("],\n "));}
//       if(m_length!=-1) {str.append("[");
//            for (int j = 0; j < matr[m_length].length-1; j++) str.append(printf(s, matr[m_length][j],zap));
//               if(matr[m_length].length!=0) str.append(printf(s, matr[m_length][matr[m_length].length-1],""));
//            str.append(("]"));
//        }
//       str.append(("]\n"));
//    return str.toString();
//    }
    /**
     * Array of matrices of the type Element[][] converts to Row-String form
     *
     * @param matrArr Array of matrices
     * @param r Ring
     *
     * @return String -- Row-String form of matrices
     */
    public static String toStringMatrixArray(Element[][][] matrArr, String eq, Ring r) {
        StringBuffer str = new StringBuffer("//");
        int matrNumb = matrArr.length;
        int[] rowsNumbs = new int[matrNumb];   // Number of rows in each matrix ***
        for (int i = 0; i < matrNumb; i++) {
            rowsNumbs[i] = matrArr[i].length;
        }
        int rowNumb = Array.max(rowsNumbs);     // Число строк на печати
        String[][] mstr = new String[rowNumb][];
        if (rowNumb == 0) {
            return str.toString();  // return "" if no any rows
        }
        int[][] max_clm_size = new int[matrNumb][]; // Number of symbol in each column
        String[][][] elem = new String[matrNumb][][]; // All elements in String-form
        for (int i = 0; i < matrNumb; i++) {
            max_clm_size[i] = new int[matrArr[i][0].length];
            elem[i] = new String[rowsNumbs[i]][matrArr[i][0].length];
        }
        for (int k = 0; k < matrNumb; k++) {
            for (int i = 0; i < rowsNumbs[k]; i++) {
                for (int j = 0; j < matrArr[k][i].length; j++) {
                    elem[k][i][j] = (matrArr[k][i][j] instanceof Polynom) ? ((Polynom) matrArr[k][i][j]).toStringF(r) : matrArr[k][i][j].toString(r); // выводим полиномы над функциями
                    max_clm_size[k][j] = Math.max(max_clm_size[k][j], elem[k][i][j].length());
                }
            }
        }
        int[] matrlength = new int[matrNumb];
        for (int i = 0; i < rowNumb; i++) {
            for (int k = 0; k < matrNumb; k++) {
                if ((matrArr[k].length > i) && (matrArr[k][i].length > 0)) {
                    str = str.append("[");
                    int length_ki = matrArr[k][i].length - 1;
                    for (int j = 0; j <= length_ki; j++) {
                        str = str.append(elem[k][i][j]);
                        if (j != length_ki) {
                            str = str.append(",");
                        }
                        int cl = elem[k][i][j].length();
                        while (cl < max_clm_size[k][j]) {
                            str = str.append(' ');
                            cl++;
                        }
                    }
                    str = str.append("]");
                } else {
                    for (int j = 0; j < matrlength[k]; j++) {
                        str = str.append(" ");
                    }
                }
                if (i == 0) {
                    if (k == 0) {
                        str = str.append(eq);
                    }
                    matrlength[k] = str.length();
                } else {
                    if (k == 0) {
                        str.append(" ");
                    }
                }
            }
            if (i == 0) {
                for (int ii = matrlength.length - 1; ii > 0; ii--) {
                    matrlength[ii] -= matrlength[ii - 1];
                }
            }
            if (i != rowNumb - 1) {
                str = str.append("\n//");
            }
        }
        return str.toString();
    }

    public static String toString(Element[][] matr, Ring r) {
        return ("\n[" + toStringMatrixForm(matr, r) + "]");
    }

    public static String toStringMatrixForm(Element[][] matr, Ring r) {
        StringBuffer str = new StringBuffer();
        // int s; // Number of letters in one Object
        //  boolean bb = true;
        int m_length = matr.length;
        String[][] mstr = new String[m_length][];
        if (matr.length == 0) {
            return str.toString();
        }
        int max_clm_sizeL=matr[0].length;
        for (int i = 1; i < matr.length; i++) {
            max_clm_sizeL=Math.max(max_clm_sizeL, matr[i].length);
        }
        int[] max_clm_size = new int[max_clm_sizeL];
        boolean polynomObject = true;   // let's find notPolynomial object
        pE:
        for (int i = 0; i < m_length; i++) {
            Object[] mm = matr[i];
            for (int j = 0; j < matr[i].length; j++) {
                if ((mm[j] instanceof F) || (mm[j] instanceof Fname)) {
                    polynomObject = false;
                    break pE;
                }
            }
        }
        if (polynomObject) {
            for (int i = 0; i < m_length; i++) {
                String[] row = new String[matr[i].length];
                mstr[i] = row;
                for (int j = 0; j < matr[i].length; j++) {
                    /**/ row[j] = (matr[i][j]).toString(r); // выводим полиномы
                    max_clm_size[j] = Math.max(
                            max_clm_size[j], 
                            row[j].length());
                }
            }
        } else {
            for (int i = 0; i < m_length; i++) {
                String[] row = new String[matr[i].length];
                mstr[i] = row;
                for (int j = 0; j < matr[i].length; j++) {
                    row[j] = matr[i][j].toString(r); //(matr[i][j] instanceof Polynom) ? ((Polynom) matr[i][j]).toStringF(r) : matr[i][j].toString(r); // выводим полиномы над функциями
                    max_clm_size[j] = Math.max(max_clm_size[j], row[j].length());
                }
            }
        }
        for (int i = 0; i < m_length; i++) {
            str = str.append("[");
            String[] row = mstr[i];
            int li = matr[i].length - 1;
            for (int j = 0; j <= li; j++) {
                str = str.append(row[j]);
                if (j != li) {
                    str = str.append(", ");
                }
                int cl = row[j].length();
                while (cl < max_clm_size[j]) {
                    str = str.append(' ');
                    cl++;
                }
            }
            str = str.append("]");
            if (i != m_length - 1) {
                str = str.append("\n ");
            }
        }
        return str.toString();
    }

    /**
     * Печать скаляра a с дополнением справа до s символов пробелами
     *
     * @param s - общее число символов на печати
     * @param a - печатаемый Скаляр
     */
    public static StringBuffer printf(int s, Element a, String zap, Ring r) {
        String astr = a.toString(r);
        StringBuffer str = new StringBuffer(astr + zap);
        int d = astr.length();
        while (d++ < s) {
            str.append(" ");
        }
        return str;
    }

    public static StringBuffer printf(int s, long a, String zap) {
        StringBuffer str = new StringBuffer(a + zap);
        int d = ("" + a).length();
        while (d++ < s) {
            str.append(" ");
        }
        return str;
    }

    /**
     * Возвращает строку, содержащая двумерный массив в виде матрицы
     *
     * @param matr -- входной двумерный массив
     *
     * @return -- строка, содержащая двумерный массив в виде матрицы
     */
    public static String toString(int[][] matr) {
        NumberZ64[][] ll = new NumberZ64[matr.length][0];
        for (int i = 0; i < matr.length; i++) {
            ll[i] = new NumberZ64[matr[i].length];
            for (int j = 0; j < matr[i].length; j++) {
                ll[i][j] = NumberZ64.valueOf(matr[i][j]);
            }
        }
        return Array.toString(ll, Ring.ringR64xyzt);
    }

    /**
     * Возвращает строку, содержащая двумерный массив в виде матрицы
     *
     * @param matr -- входной двумерный массив
     *
     * @return -- строка, содержащая двумерный массив в виде матрицы
     */
    public static String toString(long[][] matr) {
        NumberZ64[][] ll = new NumberZ64[matr.length][];
        for (int i = 0; i < matr.length; i++) {
            ll[i] = new NumberZ64[matr[i].length];
            for (int j = 0; j < matr[i].length; j++) {
                ll[i][j] = NumberZ64.ONE.valOf(matr[i][j]);
            }
        }
        return Array.toString(ll, Ring.ringR64xyzt);
    }

    /**
     * Возвращает строку, содержащая двумерный массив в виде матрицы
     *
     * @param matr -- входной двумерный массив
     *
     * @return -- строка, содержащая двумерный массив в виде матрицы
     */
    public static String toString(double[][] matr) {
        NumberR64[][] ll = new NumberR64[matr.length][];
        for (int i = 0; i < matr.length; i++) {
            ll[i] = new NumberR64[matr[i].length];
            for (int j = 0; j < matr[i].length; j++) {
                ll[i][j] = NumberR64.ONE.valOf(matr[i][j], Ring.ringZxyz);
            }
        }
        return Array.toString(ll, Ring.ringZxyz);
    }

    /**
     * Converting to String some Objects of the array of Object, only such
     * Objects of array p, which are pointed by the array pos.
     *
     * @param p - array of numbers
     * @param pos - indeces of the array p
     */
    public static String toString(long[] p, int[] pos) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < pos.length - 1; i++) {
            str.append(p[pos[i]] + ", ");
        }
        if (pos.length > 0) {
            str.append(p[pos[p.length - 1]]);
        }
        str.append("]");
        return str.toString();
    }

    /**
     * Converting to String some Objects of the array of Object, only such
     * Objects of array p, which are pointed by the array pos.
     *
     * @param p - array of numbers
     * @param pos - indeces of the array p
     */
    public static String toString(double[] p, int[] pos) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < pos.length - 1; i++) {
            str.append(p[pos[i]] + ", ");
        }
        if (pos.length > 0) {
            str.append(p[pos[p.length - 1]]);
        }
        str.append("]");
        return str.toString();
    }

    /**
     * Converting to String some Objects of the array of Object, only such
     * Objects of array p, which are pointed by the array pos.
     *
     * @param p - array of numbers
     * @param pos - indeces of the array p
     */
    public static String toString(int[] p, int[] pos) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < pos.length - 1; i++) {
            str.append(p[pos[i]] + ", ");
        }
        if (pos.length > 0) {
            str.append(p[pos[p.length - 1]]);
        }
        str.append("]");
        return str.toString();
    }

    /**
     * Converting to String some Objects of the array of Object, only such
     * Objects of array p, which are pointed by the array pos.
     *
     * @param p - array of numbers
     * @param pos - indeces of the array p
     */
    public static String toString(Object[] p, int[] pos) {
        StringBuffer str = new StringBuffer("[");
        for (int i = 0; i < pos.length - 1; i++) {
            str.append(p[pos[i]] + ", ");
        }
        if (pos.length > 0) {
            str.append(p[pos[p.length - 1]]);
        }
        str.append("]");
        return str.toString();
    }//------------------------------------------------

    /**
     * Is equal two arrays?
     *
     * @param a -- first array
     * @param b -- second array
     *
     * @return true/false -- equal / not equal
     */
    public static boolean isEqual(int[] a, int[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEqual(long[] a, long[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEqual(Element[] a, Element[] b, Ring ring) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i].compareTo(b[i], ring) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEqual(Element[][] a, Element[][] b, Ring ring) {
        if (a.length != b.length) {
            return false;
        }
        Element[] aa;
        Element[] bb;
        for (int k = 0; k < a.length; k++) {
            aa = a[k];
            bb = b[k];
            if (!isEqual(aa, bb, ring)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEqual(int[][] a, int[][] b) {
        if (a.length != b.length) {
            return false;
        }
        int[] aa;
        int[] bb;
        for (int k = 0; k < a.length; k++) {
            aa = a[k];
            bb = b[k];
            if (!isEqual(aa, bb)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEqual(long[][] a, long[][] b) {
        if (a.length != b.length) {
            return false;
        }
        long[] aa;
        long[] bb;
        for (int k = 0; k < a.length; k++) {
            aa = a[k];
            bb = b[k];
            if (!isEqual(aa, bb)) {
                return false;
            }
        }
        return true;
    }

    public static Element maxabs(Element[] a, Ring r) {
        Element max = r.numberZERO;
        if (a.length != 0) {
            max = a[0].abs(r);
            for (int k = 1; k < a.length; k++) {
                Element ak = a[k].abs(r);
                if (max.subtract(ak, r).isNegative())
                    max = ak;
            }
        }
        return max;
    }
    public static Element max(Element[] a, Ring r) {
        Element max = r.numberZERO;
        if (a.length != 0) {
            max = a[0];
            for (int k = 1; k < a.length; k++)
                if (a[k].compareTo(max, r) > 0)
                    max = a[k];
        }
        return max;
    }

    public static Element min(Element[] a, Ring r) {
        Element min = r.numberZERO;
        if (a.length != 0) {
            min = a[0];
            for (int k = 1; k < a.length; k++)
                if (a[k].compareTo(min, r) < 0)
                    min = a[k];
        }
        return min;
    }

    public static long max(long[] a) {
        long max = 0;
        if (a.length != 0) {
            max = a[0];
            for (int k = 1; k < a.length; k++) {
                max = Math.max(max, a[k]);
            }
        }
        return max;
    }

    public static long min(long[] a) {
        long min = 0;
        if (a.length != 0) {
            min = a[0];
            for (int k = 1; k < a.length; k++) {
                min = Math.min(min, a[k]);
            }
        }
        return min;
    }

    public static int max(int[] a) {
        int max = 0;
        if (a.length != 0) {
            max = a[0];
            for (int k = 1; k < a.length; k++) {
                max = Math.max(max, a[k]);
            }
        }
        return max;
    }

    public static int min(int[] a) {
        int min = 0;
        if (a.length != 0) {
            min = a[0];
            for (int k = 1; k < a.length; k++) {
                min = Math.min(min, a[k]);
            }
        }
        return min;
    }

    public static int minElementIndex(int[] array){
        if(array.length == 0) return -1;
        int index = 0;
        int min = array[0];

        for (int i = 0; i < array.length; i++) {
            if(min > array[i]){
                min = array[i];
                index = i;
            }
        }

        return index;
    }
//*******************************************************************************************************************************
//************ Sazhneva *************************************

    /**
     * сортирует массив полиномов а по модулю
     *
     * @param a PolynomQ[]
     *
     * @return PolynomQ[]
     */
    public static Polynom[] sortUpMod(Polynom[] a, Ring ring) {
        int n = a.length;
        Polynom[] c = new Polynom[n];
        System.arraycopy(a, 0, c, 0, n);
        c = Mod(c, ring);
        int[] p = sortPosUp(c, ring);
        Polynom[] b = new Polynom[n];
        for (int i = 0; i < n; i++) {
            b[i] = a[p[i]];
        }
        return b;
    }

    /**
     * берет массив полиномов а по модулю
     *
     * @param a PolynomQ[]
     *
     * @return PolynomQ[]
     */
    public static Polynom[] Mod(Polynom[] a, Ring ring) {
        int n = a.length;
        Polynom[] c = new Polynom[n];
        System.arraycopy(a, 0, c, 0, n);
        for (int k = 0; k < n; k++) {
            if ((c[k].powers.length == 0)
                    & (((c[k].coeffs[0])).signum() == -1)) {
                c[k] = (Polynom) a[k].negate(ring);
            }
        }
        return c;
    }

    public static int[] sortPosUpMod(Polynom[] a, Ring ring) {
        int n = a.length;
        Polynom[] c = new Polynom[n];
        System.arraycopy(a, 0, c, 0, n);
        c = Mod(c, ring);
        int[] p = sortPosUp(c, ring);
        return p;
    }

    /**
     * Конструктор случайного массива скаляров
     *
     * @param m -- число элементов
     * @param density -- плотность массива в 0.01%, т.е. (100%==10000)
     * @param randomType -- [maxPow1, maxPow2,..,maxPowN,
     * DemsityOfPlynom(100%==100), NumbOfBits]
     * @param ran -- java.util.Random - переменная случайного генератора
     * @param one -- единица для элементов матрицы
     *
     * @return -- Element[][] -- случайный двумерный массив скаляров
     */
    public static Element[] randomArrey(int m, int density,
            int[] randomType, java.util.Random ran, Ring ring) {
        Element one = ring.numberONE();
        Element zero = one.myZero(ring);
        int m1;
        Element one_one = one.one(ring);
        Element[] M = new Element[m];
        if (density == 10000) {
            for (int j = 0; j < m; j++) {
                M[j] = one.random(randomType, ran, ring);
            }
        } else {
            for (int j = 0; j < m; j++) {
                m1 = (Math.round(ran.nextFloat() * 10000) / (10000 - density + 1));
                if (m1 == 0) {
                    M[j] = zero;
                } else {
                    M[j] = one.random(randomType, ran, ring);
                }
            }
        }
        return M;
    }

    /**
     * Конструктор случайного двумерного массива скаляров
     *
     * @param n -- число строк
     * @param m -- число столбцов
     * @param density -- плотность массива в 0.01%, т.е. (100%==10000)
     * @param randomType -- [maxPow1, maxPow2,..,maxPowN,
     * DemsityOfPlynom(100%==100), NumbOfBits]
     * @param ran -- java.util.Random - переменная случайного генератора
     * @param one -- единица для элементов матрицы
     *
     * @return -- Object[][] -- случайный двумерный массив скаляров
     */
    public static Object[][] randomMatrix(int n, int m, int density,
            int[] randomType, java.util.Random ran, Ring ring) {
        Object[][] M = new Object[n][];
        for (int i = 0; i < n; i++) {
            M[i] = randomArrey(m, density, randomType, ran, ring);
        }
        return M;
    }

    public static Object[] copyOf(Object[] arr, int m) {
        Object[] res = new Object[m];
        System.arraycopy(arr, 0, res, 0, m);
        return res;
    }

    public static Element[] copyOf(Element[] arr, int m) {
        Element[] res = new Element[m];
        System.arraycopy(arr, 0, res, 0, m);
        return res;
    }

    public static int[] copyOf(int[] arr, int m) {
        int[] res = new int[m];
        System.arraycopy(arr, 0, res, 0, m);
        return res;
    }

    public static double[] copyOf(double[] arr, int m) {
        double[] res = new double[m];
        System.arraycopy(arr, 0, res, 0, m);
        return res;
    }

    public static long[] copyOf(long[] arr, int m) {
        long[] res = new long[m];
        System.arraycopy(arr, 0, res, 0, m);
        return res;
    }

    public static void siftRight(Element[] arr, int s) {
        int n=arr.length; Element[ ] t = new Element[s];
        System.arraycopy(arr, n-s, t, 0, s);
        System.arraycopy(arr, 0, arr, s, n-s);
        System.arraycopy(t, 0, arr, 0, s);
    }

    public static void siftLeft(Element[] arr, int s) {
        int n=arr.length; Element[ ] t = new Element[s];
        System.arraycopy(arr, 0, t, 0, s);
        System.arraycopy(arr,  s, arr, 0, n-s);
        System.arraycopy(t, 0, arr, n-s, s);
    }
    public static void siftLeft(Element[][] arr, int s) {
        for (int i = 0; i < arr.length; i++)  siftLeft(arr[i], s);
    }
    
    public static void siftRight(Element[][] arr, int s) {
        for (int i = 0; i < arr.length; i++)  siftRight(arr[i], s);
    }
           
    public static void orderingWithSign(Element[] arr, Ring ring) {
        int s = arr.length;
        Element[] pos = new Element[s];
        Element[] neg = new Element[s];
        int posL = 0;
        int negL = 0;
        for (int i = 0; i < s; i++) {
            if (arr[i].isNegative()) {
                neg[negL++] = arr[i];
            } else {
                pos[posL++] = arr[i];
            }

        }
        Element[] p = new Element[posL];
        Element[] n = new Element[negL];
        System.arraycopy(pos, 0, p, 0, posL);
        System.arraycopy(neg, 0, n, 0, negL);
        p = Array.sortUp(p, ring);
        n = Array.sortUp(n, ring);
        int j = 0;
        for (int i = 0; i < negL; i++) {
            arr[j++] = n[i];
        }
        for (int i = 0; i < posL; i++) {
            arr[j++] = p[i];
        }
    }
  /**
   * Сортируются и сливаются масcивы P и Q, а затем вычеркиваются
   * числа попавшие в массив D cокрестностью (delta>=0)
   * Все массивы имеют неотрицательные числа
   * @param p  сливаем
   * @param q сливаем
   * @param d удаляем их из pUq
   * @param delta  - окрестность
   * @return
   */
  public static int[] jointPQdelD(int[] p, int[] q, int[] d, int delta){
      delta=3;
  if (p==null){p=new int[0];} else p=sortUp(p);
  if (q==null){q=new int[0];} else q=sortUp(q);
  if (d==null){d=new int[0];} else d=sortUp(d);
  int[] pq= joinSortedArraysWithoutDoubling(p,q);
      int j=0; int no=0;
      for (int i = 0; i < d.length; i++) {int k=d[i];
          while((j<pq.length)&&(k>pq[j]+delta))j++;
          if (j==pq.length) break;
          if (k>=(pq[j]-delta)) {pq[j]=-1; no++;}
      }
      int[] res = new int[pq.length-no];
      j=0;
      for (int i = 0; i < pq.length; i++) {if(pq[i]>-1){res[j++]=pq[i];
      }}
  return res;
  }
  /**
   * Удаление в строке матрицы MatrixS одного элемента под номером m
   * @param mass Element[0][] row of MatrixS, which situated in the first row
   * @param k int[] col of this row
   * @param m the position in the row the Element to delete (0,1,..,mass.length-1)
   * @return int[] - new col-row AND in mass[0][] -- new row of Element,
   * which length is less by one then length of input row .
   */

   public static int[] delOneElementInMatrixSRow(Element[][] mass, int[] k, int m) {
        Element[] mm=mass[0];
        int len1=mm.length - 1;
        Element[] a = new Element[len1];
        int[] kk = new int[len1];
      if (m!=0) { System.arraycopy(k[0], 0, kk, 0, m);
                  System.arraycopy(mass[0], 0, a, 0, m); }
       if (m<len1){
           System.arraycopy(   k[0], m+1,  kk, m, len1-m);
           System.arraycopy(mass[0], m+1,  a,  m, len1-m);
       }
       mass[0]=a;
    return kk;
   }

    public static Element[] delColumnsForMatrixD(Element[] mass, int[] k, Ring ring) {
        Element[] a = new Element[mass.length - k.length];
        int m = 0;
        for (int i = 0; i < mass.length; i++) {
            int j = 0;
            while (j < k.length) {if (i == k[j])  break;
                if (i<k[j]){a[m] = mass[i]; m++;break;}
                j++;
            }
        if(j==k.length){a[m] = mass[i];m++; }
//if(j<k.length){  continue;}
        }
  return a;
    }
    public static boolean isEvenPermutation(int[] a){ int n=a.length;
    for (int i=0; i<n; i++){if (a[i]>=n) 
       {int[] pos=sortPosUp(a);return isEvenPermutationIntArray(pos); }}
    return isEvenPermutationIntArray(a);  
    }
    
    
     public static boolean isEvenPermutationIntArray(int []a){
       boolean []tmp=new boolean[a.length];
       int totalEvenCycles=0;
       for (int i=0; i<a.length; i++){
           if (!tmp[i] && a[i]!=i){
               int curPos=a[i];
               tmp[i]=true;
               int curCycleLen=1;
               while (curPos!=i){
                   curCycleLen++;
                   tmp[curPos]=true;
                   curPos=a[curPos];
               }
               if (curCycleLen%2==0)  totalEvenCycles++;
           }
       }
       return (totalEvenCycles%2==0);
   }
/**
 * Transormation of int[] to byte[]
 * @param mag --- int[]
 * @return  ---   byte[]
 */
    public static byte[] intsToBytes(int[] mag ){
         int ll= mag.length;
         byte[] magB= new byte[4*ll]; int j=0;
         for (int i = 0; i < ll; i++) { int a=mag[i];
            magB[j++]=(byte)(a&15); 
            magB[j++]=(byte)((a>>8)&15);
            magB[j++]=(byte)((a>>12)&15);
            magB[j++]=(byte)((a>>4)&15);
        }   return magB;
     }
  
        public static int[] jointListsOfInts(int[][] col, int maxNumbBound) {
        int n =col.length;
        int[] res=new int[maxNumbBound];
        int i=0;
        while((i<n)&&(col[i].length==0))i++;
        if (i==n)return new int[0];
        System.arraycopy(col[i], 0, res, 0, col[i].length); 
        sortUp(res,col[i].length);
        int J=col[i].length,I=0,K=0 ; i++;
        for (; i < n; i++) {  
            int[] t=col[i]; K=0;
            h: for (int j = 0; j < t.length; j++) {int Ti=t[j];
                  I=K;          
                  if(Ti==res[I])  continue h;
                  while((I<J)&&(Ti>res[I])){I++;}
                  if((I<J)&&(Ti==res[I]))  continue h;
                  int UP=I; I=K-1;  
                  while((0<=I)&&(Ti<res[I])){I--;}
                  if((0<=I)&&(Ti==res[I]))   continue h;
                  if(UP==K){if(I<K)UP=I+1;}
                  if(UP!=J) System.arraycopy(res, UP, res, UP+1, J-UP);
                  res[UP]=Ti; J++;  if (UP!=J-1)K=UP+1;else K=UP;
             }
          } 
         if (J==maxNumbBound)return res;
         int[] Res2=new int[J];
         System.arraycopy(res, 0, Res2, 0, J);
         return Res2; 
    }
      /**  Addition of two sorted matrixS rows
     * 
     * @param a Elements of 1 row
     * @param b Elements of 1 row
     * @param aa columns of 1 row
     * @param bb columns of 2 row
     * @param newC columns of new row (with lenght =aa.lenght+bb.lenght)
     * @param ring  Ring
     * @return  Elements of 1 row PLUS Elements of 2 row
     */
    public static Element[]  addSortedRows(Element[] a, Element[] b, int aa[], int bb[], int newC[][], Ring ring) {
        int nA = a.length;
        int nB = b.length;
        int n = nA + nB;
        Element[] res = new Element[n];
        int[] rr = new int[n];
        int i=0, na=0, nb=0;
        while((na<nA)&&(nb<nB)){
            if(aa[na]<bb[nb]){res[i]=a[na]; rr[i++]=aa[na++];}
            else if(aa[na]>bb[nb]){res[i]=b[nb]; rr[i++]=bb[nb++];}
            else {Element temp=a[na].add(b[nb], ring); 
                  if (!temp.isZero(ring)){res[i]=temp; rr[i++]=aa[na];}
                  na++; nb++; 
            };
        }
        while(na<nA){res[i]=a[na]; rr[i++]=aa[na++];}
        while(nb<nB){res[i]=b[nb]; rr[i++]=bb[nb++];}
        if (i<n) {Element[] res1 = new Element[i];int[] rr1=new int[i];
            System.arraycopy(res, 0, res1, 0, i);
            System.arraycopy(rr, 0, rr1, 0, i);
            newC[0]=rr1; // new column number
            return res1;
        }  newC[0]=rr; // new column number
        return res;
      }   
  
    
  /**  Subtracion of two sorted matrixS rows
     * 
     * @param a Elements of 1 row
     * @param b Elements of 1 row
     * @param aa columns of 1 row
     * @param bb columns of 2 row
     * @param newC columns of new row (with lenght =aa.lenght+bb.lenght)
     * @param ring  Ring
     * @return  Elements of 1 row MINUS Elements of 2 row
     */
    public static Element[] subtractSortedRows(Element[] a, Element[] b, int aa[], int bb[], int newC[][], Ring ring) {
        int nA = a.length;
        int nB = b.length;
        int n = nA + nB;
        Element[] res = new Element[n];
        int[] rr = new int[n];  
        int i=0, na=0, nb=0;
        while((na<nA)&&(nb<nB)){
            if(aa[na]<bb[nb]){res[i]=a[na]; rr[i++]=aa[na++];}
            else {if(aa[na]>bb[nb]){res[i]=b[nb].negate(ring); rr[i++]=bb[nb++];}
                  else {Element temp=a[na].subtract(b[nb], ring); 
                       if (!temp.isZero(ring)){res[i]=temp; rr[i++]=aa[na];}
                       na++; nb++; }
            }
        }
        while(na<nA){res[i]=a[na]; rr[i++]=aa[na++];}
        while(nb<nB){res[i]=b[nb].negate(ring); rr[i++]=bb[nb++];}
        if (i<n) {Element[] res1 = new Element[i];int[] rr1=new int[i];
            System.arraycopy(res, 0, res1, 0, i);
            System.arraycopy(rr, 0, rr1, 0, i);
            newC[0]=rr1; // new column number
            return res1;
        }  newC[0]=rr; // new column number
        return res;
      }       
        
        
    public static void main(String[] args) {
//       Polynom p1=new Polynom(); Element e= new Element();
//            NumberR64 f, h;
//         f=new  NumberR64(4);
//         h =new NumberR64(4);
//        int[] p=null; //new int[]{10,50,100};
   Ring r=new Ring("Z[]");
   int[] w=  new int[]{2, 0, 7, 3}; 
  // System.arraycopy( r.posConst, 0, w, 0, r.posConst.length);

  int[] res= Array.sortPosUp(w); 
    //     System.out.println("res="+toString(res));
    //    int[] q= new int[]{3 ,500,200,100};
//        int[] d=null; //new int[]{2,49,97};
//        int delta=1;
//        int[] res = jointPQdelD(p,q,d,delta);

       //  int res1= f.compareTo(h);
         
     //   System.out.println("res1="+  isEvenPermutation(q));
int[] ttt=new int[100];  for (int i = 0; i < 100; i++) {ttt[i]=10 *i; }
 for (int i = 50; i < 100; i++) {ttt[i]=100*i; }
 ttt[20]=209; ttt[40]=409;
   int a=Array.findPosOfMedian(ttt,  22); System.out.println("a="+a);  
    }
    public static boolean isEmptyArray(ArrayList<Drop>[] vokzal) {
        for (int i = 0; i < vokzal.length; i++) {
            if (vokzal[i].size()!=0) {
               // System.out.println("not empty level = " + i);
                return false;
            }

        }

        return true;
    }

    public static boolean isEmpty(Element[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                return false;
            }
        }
        return true;
    }

    public static int maxPos(ArrayList<Element> a, Ring ring) {
        int maxP = 0;

        if (a.size() != 0) {
            Element max = a.get(0);

            for (int k = 1; k < a.size(); k++) {
                Element max1 = max.max(a.get(k), ring);

                if (max.compareTo(max1, LESS, ring)) {
                    maxP=k;
                    max=max1;
                }
            }
        }

        return maxP;
    }

    public static Element[] concatTwoArrays(Element[] elem1, Element[] elem2, Element[] result) {
        System.arraycopy(elem1, 0, result, 0, elem1.length);
        System.arraycopy(elem2, 0, result, elem1.length, elem2.length);
        return result;
    }
    
          /** Find Position of Median Element  in Sorted Array
         * 
         * @param t - array of sorted integers
         * @param  tm -  element in this array, which position  must be returned
         * @return position of  median, or element 
         *  which is grater then median (if median absent)
         */ 
        public static int findPosOfMedian(int[] t, int tm){  
        int l=t.length; int l1=l-1; int a=0; int ta=t[a];  if(ta>=tm)return 0;      
        int b=l1; int tb=t[b];   if(tb<tm)return l; if(tb==tm)return l1; 
        int www=5*tm;  int k=0; int tk=0;int TM=0;
        while(true){  www-=1;  if(www==0) {System.out.println(Array.toString(t)); return -1;}
                  int m= ((tm*(b-a)+a*tb-b*ta))/(tb-ta)  ;
                  k=(a+b)/2; tk=t[k];TM=t[m];
           //     System.out.println(m+ " "+a+" "+b+" "+t[m]+" "+t[a]+" "+t[b]);
                  if(tk==tm){return k;}
                  else if(tk>tm){b=k; tb=t[b];
                     if(TM<tm){a=m;ta=TM;}if(t[a+1]>=tm) return a+1;}
                  else { a=k;ta=t[a]; 
                    if(TM>=tm){b=m;tb=TM;} if(t[b-1]<tm) return b;}           
        }
    } 
    
    
}
