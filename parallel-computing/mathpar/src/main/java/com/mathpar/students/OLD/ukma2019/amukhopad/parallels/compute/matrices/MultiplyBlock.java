package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices;

import java.io.Serializable;

public class MultiplyBlock implements Serializable, MatrixTask {
  private static final long serialVersionUID = 1L;

  private int rowA;
  private int colA;
  private int rowB;
  private int colB;

  public MultiplyBlock() {
  }

  public MultiplyBlock(int rowA, int colA, int rowB, int colB, int blockSize) {
    this.colA = colA;
    this.rowA = rowA;
    this.rowB = rowB;
    this.colB = colB;
    this.blockSize = blockSize;
  }

  public int getRowA() {
    return rowA;
  }

  public int getColA() {
    return colA;
  }

  public int getRowB() {
    return rowB;
  }

  public int getColB() {
    return colB;
  }

  public int getBlockSize() {
    return blockSize;
  }

  private int blockSize;
}
