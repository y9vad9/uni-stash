package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.cholesky;

import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixUtil.generatePositiveDefinite;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixUtil.multiply;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixUtil.submatrix;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixUtil.subtract;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixUtil.transpose;
import static java.lang.String.format;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixTask;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices.MatrixUtil;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Driver;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Executor;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Log;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Message;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.CommUtil;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.ProcessorUtil;

public class CholeskyDecomposition {
  private static Map<Integer, MatrixTask> taskResults;

  public static void main(String[] args) {
    taskResults = new ConcurrentHashMap<>();
    Executor.start(MatrixTask.class, taskResults);

    int matrixRank = Integer.parseInt(args[1]);

    if (ProcessorUtil.getCurrentId() == 0) {
      Driver.start(MatrixTask.class);

      double[][] A = generatePositiveDefinite(matrixRank);
      Log.driver(format("A = \n%s\n", MatrixUtil.toString(A, matrixRank)));

      double[][] rec = cholesky(A);
      Log.driver(format("Answer = \n%s\n", MatrixUtil.toString(rec, matrixRank)));

    }

    while (true) {
      Message<MatrixTask> task = ProcessorUtil.waitForTask(MatrixTask.class);
      Log.exec("Recieved parallelMultiply from " + task.getSource());
      MatrixTask data = task.getData();
      MatrixTask answer = null;

      if (data instanceof TriangleSolveBlock) {
        TriangleSolveBlock casted = (TriangleSolveBlock) data;
        answer = handleRtrsm(casted);
      } else if (data instanceof CholBlock) {
        CholBlock casted = (CholBlock) data;
        double[][] result = cholesky(casted.getBlock());
        answer = new CholBlock(result);
      }

      CommUtil.send(answer, task.getSource(), MessageType.TASK_RETURN);

      ProcessorUtil.reportFree();
    }

  }

  private static double[][] cholesky(double[][] A) {
    int n = A.length;
    double[][] L = new double[n][n];
    if (n == 1) {
      L[0][0] = Math.sqrt(A[0][0]);
    } else {
      int newSize = n / 2;

      double[][] A11 = submatrix(A, 0, 0, newSize);
      double[][] A21 = submatrix(A, newSize, 0, newSize);
      double[][] A22 = submatrix(A, newSize, newSize, newSize);

      double[][] L11 = cholesky(A11);
      double[][] L21 = rtrsm(A21, transpose(L11));
      A22 = subtract(A22, multiply(L21, transpose(L21)));

      double[][] L22 = cholesky(A22);

      merge(L, L11, L21, L22);
    }

    return L;
  }

  private static double[][] rtrsm(double[][] A, double[][] U) {
    int n = A.length;
    double[][] X = new double[n][n];
    if (n == 1) {
      X[0][0] = A[0][0] / U[0][0];
    } else {
      int newSize = n / 2;

      double[][] U11 = submatrix(U, 0, 0, newSize);
      double[][] U12 = submatrix(U, 0, newSize, newSize);
      double[][] U22 = submatrix(U, newSize, newSize, newSize);

      double[][] A11 = submatrix(A, 0, 0, newSize);
      double[][] A12 = submatrix(A, 0, newSize, newSize);
      double[][] A21 = submatrix(A, newSize, 0, newSize);
      double[][] A22 = submatrix(A, newSize, newSize, newSize);

      int X1 = sendRtrsm(new TriangleSolveBlock(A11, A12, U11, U12, U22));
      int X2 = sendRtrsm(new TriangleSolveBlock(A21, A22, U11, U12, U22));

      while (!taskResults.containsKey(X1) || !taskResults.containsKey(X2)) {
        Log.exec("Waiting for subtasks to finish");
        ProcessorUtil.sleep(1);
      }

      TriangleSolveResultBlock resultX1 = (TriangleSolveResultBlock) taskResults.get(X1);
      TriangleSolveResultBlock resultX2 = (TriangleSolveResultBlock) taskResults.get(X2);

      merge(X, resultX1.getX1(), resultX1.getX2(), resultX2.getX1(), resultX2.getX2());
    }

    return X;
  }

  public static int sendCholesky(CholBlock block) {
    int procId = ProcessorUtil.requestProcessor(block.getBlockSize());
    if (procId != -1) {
      CommUtil.send(block, procId, MessageType.TASK_NEW);
      return procId;
    }

    int key = System.identityHashCode(block);
    double[][] result = cholesky(block.getBlock());
    taskResults.put(key, new CholBlock(result));

    return key;
  }

  public static int sendRtrsm(TriangleSolveBlock block) {
    int procId = ProcessorUtil.requestProcessor(block.getBlockSize());
    if (procId != -1) {
      Log.exec("Sending RTRSM to proc" + procId);
      CommUtil.send(block, procId, MessageType.TASK_NEW);
      return procId;
    }

    int key = System.identityHashCode(block);
    TriangleSolveResultBlock result = handleRtrsm(block);
    taskResults.put(key, result);

    return key;
  }

  public static TriangleSolveResultBlock handleRtrsm(TriangleSolveBlock block) {
    double[][] X1 = rtrsm(block.getA1(), block.getU11());
    double[][] X2 = rtrsm(subtract(block.getA2(), multiply(X1, block.getU12())), block.getU22());

    return new TriangleSolveResultBlock(X1, X2);
  }

  private static double[][] merge(double[][] L, double[][] L11, double[][] L21, double[][] L22) {
    int blockSize = L11.length;

    for (int row = 0; row < blockSize; row++) {
      for (int col = 0; col < blockSize; col++) {
        L[row][col] = L11[row][col];
        L[row + blockSize][col] = L21[row][col];
        L[row + blockSize][col + blockSize] = L22[row][col];
      }
    }

    return L;
  }

  private static double[][] merge(double[][] L, double[][] L11, double[][] L12, double[][] L21, double[][] L22) {
    int blockSize = L11.length;

    for (int row = 0; row < blockSize; row++) {
      for (int col = 0; col < blockSize; col++) {
        L[row][col] = L11[row][col];
        L[row][col + blockSize] = L12[row][col];
        L[row + blockSize][col] = L21[row][col];
        L[row + blockSize][col + blockSize] = L22[row][col];
      }
    }

    return L;
  }
}
