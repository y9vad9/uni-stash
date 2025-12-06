package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.cholesky;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixTask;

public class CholBlock implements MatrixTask {
  private double[][] block;

  public CholBlock(double[][] block) {
    this.block = block;
  }

  @Override
  public int getBlockSize() {
    return block.length;
  }

  public double[][] getBlock() {
    return block;
  }

  public CholBlock setBlock(double[][] block) {
    this.block = block;
    return this;
  }
}
