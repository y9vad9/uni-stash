package com.mathpar.students.OLD.stud2014.agapov;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Array;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;

public class GraphGame extends com.mathpar.func.F {
    final static double pi = Math.PI;

    public static double[][] circle(double R, int N) {
        double[][] A = new double[2][N];
        for (int i = 0; i < N; i++) {
            A[0][i] = R * Math.cos((2 * pi * i) / N);
            A[1][i] = R * Math.sin((2 * pi * i) / N);
        }
        return A;
    }

    public static void game(double A[][], int B[][], double rad) {
        int n = A[0].length;
        double x, y, r, f, f1;
        double o = Math.pow(10, 2);
        NumberR64[] Fx = new NumberR64[n];
        NumberR64[] Fy = new NumberR64[n];
        for (int i = 0; i < n; i++) {
            Fx[i] = NumberR64.ZERO;
            Fy[i] = NumberR64.ZERO;
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                x = A[0][i] - A[0][j];
                y = A[1][i] - A[1][j];
                NumberR64 X = new NumberR64(x);
                NumberR64 Y = new NumberR64(y);
                r = x * x + y * y;
                r = Math.sqrt(r);
                if (r < 1) {
                    f1 = Math.pow(r, 2);
                } else {
                    f1 = 0;
                }
                NumberR64 F1 = new NumberR64(f1);
                Fx[i] = Fx[i].add(X.multiply(F1));
                Fy[i] = Fy[i].add(Y.multiply(F1));
                Fx[j] = Fx[j].subtract(X.multiply(F1));
                Fy[j] = Fy[j].subtract(Y.multiply(F1));
                if (B[i][j] != 0) {
                    if (r >= 1) {
                        X = X.multiply(NumberR64.MINUS_ONE);
                        Y = Y.multiply(NumberR64.MINUS_ONE);
                        f = Math.pow(r, -5);
                    } else {
                        f = 0;
                    }
                    NumberR64 F = new NumberR64(f);
                    Fx[i] = Fx[i].add(X.multiply(F));
                    Fy[i] = Fy[i].add(Y.multiply(F));
                    Fx[j] = Fx[j].subtract(X.multiply(F));
                    Fy[j] = Fy[j].subtract(Y.multiply(F));
                }
            }
        }
        System.out.println("Fx = " + Array.toString(Fx, Ring.ringZxyz));
        System.out.println("Fy = " + Array.toString(Fy, Ring.ringZxyz));
        for (int i = 0; i < n; i++) {
            A[0][i] = A[0][i] +  Fx[i].value;
            A[1][i] = A[1][i] +  Fy[i].value;
        }
    }

    public static void play(int B[][]) {
        Ring ring = new Ring("R64[]");
        MatrixD I = new MatrixD(B, ring);
        double r = 1;
        double C[][];
        int n = B.length;
        C = circle(r, n);
        MatrixD M = new MatrixD(C, ring);
        String path = "C:\\Users\\user\\Desktop\\graph\\graph0.png";
        drawGraph(I,M,path);
        System.out.println("KРУГ = " + Array.toString(C));
        for (int i = 0; i < 5; i++) {
            game(C, B, r);
            MatrixD M1 = new MatrixD(C, ring);
            path = "C:\\Users\\user\\Desktop\\graph\\graph" + (i+1) + ".png";
            drawGraph(I,M1,path);
            System.out.println("OTBET = " + Array.toString(C));
        }
        System.out.println("B = " + Array.toString(B));
    }

    public static void main(String[] args) {
        int[][] B = new int[][] {{0, 1, 1, 0}, {1, 0, 1, 1}, {1, 1, 0, 0}, {1, 1, 1, 0}};
        play(B);
    }
}
