package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices;

import java.text.DecimalFormat;

public class MatrixUtil {

  public static String toString(double[][] matrix, int n) {
    StringBuilder buff = new StringBuilder();
    DecimalFormat f = new DecimalFormat();
    f.setMaximumFractionDigits(3);

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        String num = f.format(matrix[i][j]);
        buff.append(String.format("\t%s", num));

        for (int c = 0; c <= (4 - num.length()); c++) {
          buff.append(" ");
        }
      }
      buff.append("\n");
    }
    return buff.toString();
  }

  public static double[][] add(double[][] A, double[][] B) {
    double[][] C = new double[A.length][A.length];
    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A[0].length; j++) {
        C[i][j] = A[i][j] + B[i][j];
      }
    }
    return C;
  }

  public static double[][] subtract(double[][] A, double[][] B) {
    double[][] C = new double[A.length][A.length];
    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A[0].length; j++) {
        C[i][j] = A[i][j] - B[i][j];
      }
    }
    return C;
  }

  public static double[][] multiply(double[][] A, double[][] B) {
    double[][] ans = new double[A.length][A[0].length];

    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A[0].length; j++) {
        for (int k = 0; k < A.length; k++) {
          ans[i][j] += A[i][k] * B[k][j];
        }
      }
    }
    return ans;
  }

  public static double[][] transpose(double[][] matrix) {
    double[][] newMatrix = new double[matrix[0].length][matrix.length];

    for (int i = 0; i < matrix.length; i++) {
      for(int j = 0; j < matrix[0].length; j++) {
        newMatrix[j][i] = matrix[i][j];
      }
    }
    return newMatrix;
  }

  public static double[][] scale(double[][] matrix, double scale) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        matrix[i][j] = matrix[i][j] * scale;
      }
    }
    return matrix;
  }

  public static double[][] identity(int size) {
    return identity(size, 1);
  }

  public static double[][] identity(int size, int val) {
    double[][] matrix = new double[size][size];
    for (int i = 0; i < size; i++) {
      matrix[i][i] = val;
    }
    return matrix;
  }

  public static double[][] generatePositiveDefinite(int n) {
    double[][] matrix = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        matrix[i][j] =  Math.random();
      }
    }
    return add(scale(add(matrix, transpose(matrix)), 0.5), identity(n, n));
  }

  public static double[][] generate(int n) {
    return generate(n, -10, 10);
  }

  public static double[][] generate(int n, int to) {
    return generate(n, 0, to);
  }

  public static double[][] generateNormalized(int n) {
    double[][] matrix = generate(n);
    return normalizeForDivideAndConquer(matrix);
  }

  public static double[][] generate(int n, int from, int to) {
    double[][] matrix = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        matrix[i][j] = (int) (Math.random() * (to - from) + from);
      }
    }
    return matrix;
  }

  public static double[][] submatrix(double[][] A, int row, int col, int n) {
    double[][] submatrix = new double[n][n];

    for (int i = 0; i < n; i++) {
      System.arraycopy(A[i + row], col, submatrix[i], 0, n);
    }

    return submatrix;
  }

  public static double[][] normalizeForDivideAndConquer(double[][] matrix) {
    int n = (int) Math.pow(2, Math.ceil(Math.log(matrix.length) / Math.log(2)));

    double[][] newMatrix = new double[n][n];

    for (int i = 0; i < matrix.length; i++) {
      double[] row = matrix[i];
      System.arraycopy(row, 0, newMatrix[i], 0, row.length);
    }

    return newMatrix;
  }
}

