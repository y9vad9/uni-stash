package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices;

import java.io.Serializable;

public class BroadcastData implements Serializable {
  private static final long serialVersionUID = 1L;

  private double[][] matrixA;
  private double[][] matrixB;

  public BroadcastData() {
  }

  public BroadcastData(double[][] A, double[][] B) {
    this.matrixA = A;
    this.matrixB = B;
  }

  public double[][] getMatrixA() {
    return matrixA;
  }

  public double[][] getMatrixB() {
    return matrixB;
  }
}
