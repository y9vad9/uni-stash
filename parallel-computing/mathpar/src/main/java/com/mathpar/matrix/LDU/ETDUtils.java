/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.util.Random;
/**
 *
 * @author ridkeim
 */
public class ETDUtils {
    public static MatrixS join(MatrixS[][] S, int[] rows, int[] cols) {
        int n = 0;
        for (int i : rows) {
            n += i;
        }
        Element[][] r = new Element[n][0];
        int[][] c = new int[n][0];
        int[] colss = new int[cols.length];
        for (int i = 1; i < colss.length; i++) {
            colss[i] += cols[i - 1] + colss[i - 1];
        }
        //перебираем ряды матриц
        int numrow = 0;
        for (int k = 0; k < S.length; k++) {
            //перебираем строки матриц
            for (int j = 0; j < rows[k]; j++) {
                //перебираем матрицы
                int numbs = 0;
                int[] num = new int[S[k].length];

                for (int i = 0; i < S[k].length; i++) {
                    MatrixS tm = S[k][i];
                    if (tm.M.length <= j) {
                        continue;
                    }
                    int tmps = tm.M[j].length;
                    num[i] = tmps;
                    numbs += tmps;
                }

                r[numrow] = new Element[numbs];
                c[numrow] = new int[numbs];
                int t = 0;
                for (int i = 0; i < S[k].length; i++) {
                    MatrixS tm = S[k][i];
                    if (tm.M.length <= j) {
                        continue;
                    }
                    System.arraycopy(tm.M[j], 0, r[numrow], t, num[i]);
                    System.arraycopy(tm.col[j], 0, c[numrow], t, num[i]);
                    t += num[i];
                }

                t = num[0];
                for (int i = 1; i < num.length; i++) {
                    for (int l = 0; l < num[i]; l++) {
                        c[numrow][t] += colss[i];
                        t++;
                    }
                }
                numrow++;
            }
        }
        MatrixS res = new MatrixS(n, colss[colss.length - 1] + cols[colss.length - 1], r, c);
        return res;

    }
    public static MatrixS[] split(MatrixS T, int rowlen, int collen) {
        MatrixS[] res = new MatrixS[4];
        int len1 = Math.min(rowlen, T.col.length); // rows for upper blocks
        int len2 = Math.max(T.col.length - rowlen, 0); // rows for bound blocks
        int colNumb0 = Math.min(collen, T.colNumb);
        int colNumb1 = Math.max(T.colNumb - collen, 0);
        int[][] c0 = new int[len1][0],
                c1 = new int[len1][0],
                c3 = new int[len2][0],
                c2 = new int[len2][0];
        Element[][] r0 = new Element[len1][0],
                r1 = new Element[len1][0],
                r2 = new Element[len2][0],
                r3 = new Element[len2][0];
        for (int i = 0; i < len1; i++) {
            if ((T.M[i] != null) && (T.M[i].length != 0)) {
                Element[][] R2 = new Element[2][0];
                int[][] C2 = new int[2][0];
                toHalveRow(T.M[i], R2, T.col[i], C2, colNumb0);
                c0[i] = C2[0];
                c1[i] = C2[1];
                r0[i] = R2[0];
                r1[i] = R2[1];
            }
        }
        for (int i = 0; i < len2; i++) {
            int i_len = rowlen + i;
            if (T.M[i_len]!=null && T.M[i_len].length != 0) {
                Element[][] R2 = new Element[2][0];
                int[][] C2 = new int[2][0];
                toHalveRow(T.M[i_len], R2, T.col[i_len], C2, colNumb0);
                c2[i] = C2[0];
                c3[i] = C2[1];
                r2[i] = R2[0];
                r3[i] = R2[1];
            }
        }
        res[0] = new MatrixS(len1, colNumb0, r0, c0);
        res[1] = new MatrixS(len1, colNumb1, r1, c1);
        res[2] = new MatrixS(len2, colNumb0, r2, c2);
        res[3] = new MatrixS(len2, colNumb1, r3, c3);
        return res;
    }

    public static MatrixS[] split(MatrixS T, int len) {
        return split(T, len, len);
    }
    
    public static void toHalveRow(Element[] r, Element[][] r2, int[] c, int[][] c2, int len) {
        int m = c.length;
        int[] C0 = new int[m];
        int[] C1 = new int[m];
        Element[] R0 = new Element[m];
        Element[] R1 = new Element[m];
        int p0 = 0;
        int p1 = 0;
        for (int j = 0; j < m; j++) {
            int cj = c[j];
            if (cj < len) {
                C0[p0] = cj;
                R0[p0++] = r[j];
            } else {
                C1[p1] = (cj -= len);
                R1[p1++] = r[j];
            }
        }
        if (p0 == m) {
            c2[0] = C0;
            r2[0] = R0;
        } else if (p0 != 0) {
            int[] CC = new int[p0];
            Element[] RR = new Element[p0];
            System.arraycopy(C0, 0, CC, 0, p0);
            System.arraycopy(R0, 0, RR, 0, p0);
            c2[0] = CC;
            r2[0] = RR;
        }
        if (p1 == m) {
            c2[1] = C1;
            r2[1] = R1;
        } else if (p1 != 0) {
            int[] CC = new int[p1];
            Element[] RR = new Element[p1];
            System.arraycopy(C1, 0, CC, 0, p1);
            System.arraycopy(R1, 0, RR, 0, p1);
            c2[1] = CC;
            r2[1] = RR;
        }
    }
    
    public static MatrixS randomMatrixS(int size, int numb,int zeroP, Ring ring) {
        Random rnd = new Random();
        NumberZ[][] a = new NumberZ[size][size];
        int zeroNumb = 0;
        numb = Math.abs(numb);
        int numb_dif = Math.abs(numb/2);
        int numb_rnd = numb-numb_dif;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if(rnd.nextInt(100)<zeroP){
                    a[i][j] = new NumberZ("0");
                    zeroNumb++;
                }else{
                    a[i][j] = new NumberZ((rnd.nextInt(numb_rnd)+numb_dif)*(rnd.nextInt(2)==0?-1:1));
                }
            }
        }
        double proc = (zeroNumb==0)?0:(((double) zeroNumb*100)/((double) size*size));
//        System.out.println("initial zero = "+zeroP);
//        System.out.print("zero = ");
//        System.out.printf("%.2f",proc);
        MatrixS A = new MatrixS(a, ring);
        return A;
    }
    
//    public static MatrixS join(MatrixS A, MatrixS B, MatrixS C, MatrixS D) {
//        return join(new MatrixS[] {A, B, C, D});
//    }
//
//    public static MatrixS join(MatrixS[] b) {
//        int len2 = Math.max(b[2].M.length, b[3].M.length);
//        int len1 = Math.max(b[0].M.length, b[1].M.length);
//        int n = (len2 == 0) ? len1 : len1 + len2;
//        int col2 = Math.max(b[1].colNumb, b[3].colNumb);
//        int col1 = Math.max(b[0].colNumb, b[2].colNumb);
//        int colNumb = (col2 == 0) ? col1 : col1 + col2;
//        Element[][] r = new Element[n][0];
//        int[][] c = new int[n][0];
//        Element[] R0 = null;
//        Element[] R1 = null;
//        int[] C0 = null;
//        int[] C1 = null;
//        for (int i = 0; i < len1; i++) {
//            int m = 0;
//            int k = 0;
//            if (b[0].M.length > i) {
//                C0 = b[0].col[i];
//                R0 = b[0].M[i];
//                m = C0.length;
//            }
//            if (b[1].M.length > i) {
//                C1 = b[1].col[i];
//                R1 = b[1].M[i];
//                k = C1.length;
//            }
//            int mk = m + k;
//            Element[] r0 = new Element[mk];
//            int[] c0 = new int[mk];
//            if (m > 0) {
//                System.arraycopy(C0, 0, c0, 0, m);
//                System.arraycopy(R0, 0, r0, 0, m);
//            }
//            if (k > 0) {
//                System.arraycopy(C1, 0, c0, m, k);
//                System.arraycopy(R1, 0, r0, m, k);
//                for (int s = m; s < mk; s++) {
//                    c0[s] += col1;
//                }
//            }
//            r[i] = r0;
//            c[i] = c0;
//        }
//        int ii = len1;
//        for (int i = 0; i < len2; i++) {
//            int m = 0;
//            int k = 0;
//            if (b[2].M.length > i) {
//                C0 = b[2].col[i];
//                R0 = b[2].M[i];
//                m = C0.length;
//            }
//            if (b[3].M.length > i) {
//                C1 = b[3].col[i];
//                R1 = b[3].M[i];
//                k = C1.length;
//            }
//            int mk = m + k;
//            Element[] r0 = new Element[mk];
//            int[] c0 = new int[mk];
//            if (m > 0) {
//                System.arraycopy(C0, 0, c0, 0, m);
//                System.arraycopy(R0, 0, r0, 0, m);
//            }
//            if (k > 0) {
//                System.arraycopy(C1, 0, c0, m, k);
//                System.arraycopy(R1, 0, r0, m, k);
//                for (int s = m; s < mk; s++) {
//                    c0[s] += len1;
//                }
//            }
//            r[ii] = r0;
//            c[ii++] = c0;
//        }
//        MatrixS res = new MatrixS(n, colNumb, r, c);
//        return res;
//    }
}
