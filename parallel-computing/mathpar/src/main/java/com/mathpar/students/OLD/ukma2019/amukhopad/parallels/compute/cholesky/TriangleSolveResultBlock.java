package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.cholesky;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixTask;

public class TriangleSolveResultBlock implements MatrixTask {
  private double[][] X1;
  private double[][] X2;

  public TriangleSolveResultBlock(double[][] x1, double[][] x2) {
    X1 = x1;
    X2 = x2;
  }

  public double[][] getX1() {
    return X1;
  }

  public TriangleSolveResultBlock setX1(double[][] x1) {
    X1 = x1;
    return this;
  }

  public double[][] getX2() {
    return X2;
  }

  public TriangleSolveResultBlock setX2(double[][] x2) {
    X2 = x2;
    return this;
  }

  @Override
  public int getBlockSize() {
    return X1.length;
  }
}
