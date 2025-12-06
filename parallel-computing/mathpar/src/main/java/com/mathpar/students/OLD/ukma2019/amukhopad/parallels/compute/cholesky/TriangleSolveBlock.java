package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.cholesky;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixTask;

public class TriangleSolveBlock implements MatrixTask {
  private double[][] A1;
  private double[][] A2;

  private double[][] U11;
  private double[][] U12;
  private double[][] U22;

  public TriangleSolveBlock(double[][] a1, double[][] a2, double[][] u11, double[][] u12, double[][] u22) {
    A1 = a1;
    A2 = a2;
    U11 = u11;
    U12 = u12;
    U22 = u22;
  }

  public double[][] getA1() {
    return A1;
  }

  public TriangleSolveBlock setA1(double[][] a1) {
    A1 = a1;
    return this;
  }

  public double[][] getA2() {
    return A2;
  }

  public TriangleSolveBlock setA2(double[][] a2) {
    A2 = a2;
    return this;
  }

  public double[][] getU11() {
    return U11;
  }

  public TriangleSolveBlock setU11(double[][] u11) {
    U11 = u11;
    return this;
  }

  public double[][] getU12() {
    return U12;
  }

  public TriangleSolveBlock setU12(double[][] u12) {
    U12 = u12;
    return this;
  }

  public double[][] getU22() {
    return U22;
  }

  public TriangleSolveBlock setU22(double[][] u22) {
    U22 = u22;
    return this;
  }

  @Override
  public int getBlockSize() {
    return A1.length;
  }
}
