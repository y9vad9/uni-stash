package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices;

import java.io.Serializable;

public class SumBlock implements Serializable, Comparable<SumBlock>, MatrixTask {
  private static final long serialVersionUID = 1L;

  private double[][] C;

  private MultiplyBlock A;
  private MultiplyBlock B;
  private int rowC;
  private int colC;
  private int blockSize;
  public SumBlock() {
  }

  public SumBlock(double[][] c, MultiplyBlock a, MultiplyBlock b, int rowC, int colC) {
    this.C = c;
    this.A = a;
    this.B = b;
    this.rowC = rowC;
    this.colC = colC;
    this.blockSize = a.getBlockSize();
  }

  @Override
  public int compareTo(SumBlock other) {
    return other.blockSize - this.blockSize;
  }

  @Override
  public String toString() {
    return "\nSumBlock{" +
        ", rowC=" + rowC +
        ", colC=" + colC +
        ", blockSize=" + blockSize +
        "}";
  }

  public MultiplyBlock getA() {
    return A;
  }

  public MultiplyBlock getB() {
    return B;
  }

  public double[][] getC() {
    return C;
  }

  public int getRowC() {
    return rowC;
  }

  public int getColC() {
    return colC;
  }

  public int getBlockSize() {
    return blockSize;
  }
}
