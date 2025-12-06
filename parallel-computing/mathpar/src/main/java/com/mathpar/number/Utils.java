
package com.mathpar.number;
import com.mathpar.number.NumberR;
import com.mathpar.number.NumberZ;
import java.util.Random;

public class Utils {

    static void printFormattedVector(int[] v) {
        System.out.print("{");
        for (int i = 0; i < v.length - 1; i++) {
            System.out.print(v[i] + ", ");
        }
        System.out.print(v[v.length - 1]);
        System.out.println("}");
    }

//------------------------------------------------------------------------------

    static void printFormattedVector(String mes, int[] v) {
        System.out.print(mes);
        System.out.print("{");
        for (int i = 0; i < v.length - 1; i++) {
            System.out.print(v[i] + ", ");
        }
        System.out.print(v[v.length - 1]);
        System.out.println("}");
    }


    static void print(double[][] matr) {
        double max = Math.abs(matr[0][0]);
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                max = Math.max(max, matr[i][j]);
            }
        }
        String t = NumberR64.toString(max);
        String s = "%" + String.valueOf(t.indexOf(".") + 12) + "."
                   + String.valueOf(t.length() - t.indexOf(".")) + "f   ";
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                System.out.printf(s, matr[i][j]);
            }
            System.out.println();
        }
    }

//==============================================================================

    static void print(long[][] matr) {
        long max = Math.abs(matr[0][0]);
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                max = Math.max(max, matr[i][j]);
            }
        }
        String t = NumberZ64.toString(max);
        String s = "%" + String.valueOf(t.length() + 3) + "d";
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                System.out.printf(s, matr[i][j]);
            }
            System.out.println();
        }

    }

//==============================================================================


    static void print(int[][] matr) {
        long max = Math.abs(matr[0][0]);
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                max = Math.max(max, matr[i][j]);
            }
        }
        String t = NumberZ64.toString(max);
        String s = "%" + String.valueOf(t.length() + 3) + "d";
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                System.out.printf(s, matr[i][j]);

            }
            System.out.println();
        }

    }

//==============================================================================

    static void print(NumberZ[][] matr) {
        NumberZ max = matr[0][0].abs();
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                max = matr[i][j].max(max.abs());
            }
        }

        String t = max.toString();
        String s = "%" + String.valueOf(t.length() + 3) + "d";
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                System.out.printf(s, matr[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

//==============================================================================

    static void print(NumberR[][] matr) {
        NumberR max = matr[0][0].abs();
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                max = matr[i][j].max(max.abs());
            }
        }
        String t = max.toString();
        String s = "%" + String.valueOf(t.indexOf(".") + 12) + "."
                   + String.valueOf(t.length() - t.indexOf(".")) + "f   ";
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[i].length; j++) {
                System.out.printf(s, matr[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    static void print(NumberZ[] vect) {
        for (int i = 0; i < vect.length; i++) {
            System.out.print(vect[i] + " ");
        }
        System.out.println();
    }

    static void print(int[] vect) {
        for (int i = 0; i < vect.length; i++) {
            System.out.print((vect[i]) + " ");
        }
        System.out.println();
    }

    static void printBin(NumberZ val) {
        for (int i = val.mag.length - 1; i > -1; i--) {
            System.out.print(Integer.toBinaryString(val.mag[i]) + " ");
        }
        System.out.println();
    }

    static void printBack(int[] vect) {
        for (int i = vect.length - 1; i > -1; i--) {
            System.out.print(((long) vect[i]) + " ");
        }
        System.out.println();
    }

    static void printBinIntAr(int[] ar) {
        for (int i = 0; i < ar.length; i++)
            System.out.print(Integer.toBinaryString(ar[i]) + " ");
        System.out.println();

    }


    static void print(byte[] ar) {
        for (int i = 0; i < ar.length; i++)
            System.out.print((ar[i]) + " ");
        System.out.println();

    }

    //==========================================================================

    static int[] generateIntArRnd(int n, Random rnd) {
        int[] res = new int[n];
        for (int i = 0; i < res.length; i++) {
            res[i] = rnd.nextInt();
        }
        return res;
    }

    //==========================================================================

    static int[] generateIntArRnd(int n) {
        int[] res = new int[n];
        Random rnd = new Random();
        for (int i = 0; i < res.length; i++) {
            res[i] = rnd.nextInt();
        }
        return res;
    }


}
