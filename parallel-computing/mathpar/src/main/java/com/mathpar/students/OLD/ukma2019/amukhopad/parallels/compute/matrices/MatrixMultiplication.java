package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.compute.matrices;

import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType.TASK_NEW;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.CommUtil.receive;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.CommUtil.sendToAll;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Driver;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Executor;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Log;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Message;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.CommUtil;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.ProcessorUtil;

public class MatrixMultiplication {
  private static double[][] A;
  private static double[][] B;
  private static Map<Integer, SumBlock> results;

  public static void main(String[] args) {
    results = new ConcurrentHashMap<>();
    Executor.start(SumBlock.class, results);

    if (ProcessorUtil.getCurrentId() == 0) {
      int matrixRank = Integer.parseInt(args[1]);
      A = MatrixUtil.generateNormalized(matrixRank);
      B = MatrixUtil.generateNormalized(matrixRank);

      Log.driver(format("A = \n%s\n", MatrixUtil.toString(A, matrixRank)));
      Log.driver(format("B = \n%s\n", MatrixUtil.toString(B, matrixRank)));

      Driver.start(SumBlock.class);
      Log.driver("Driver is up.");

      BroadcastData data = new BroadcastData(A, B);

      sendToAll(data, MessageType.INITIAL_DATA);

      Log.driver("Sent matrices to all processes");

      double[][] iterative = MatrixUtil.multiply(A, B);
      Log.driver("Interative = \n\n" + MatrixUtil.toString(iterative, matrixRank));


      Log.driver("Started executor");

      double[][] ans = parallelMultiply(A, B);
      Log.driver("Parallel = \n\n" + MatrixUtil.toString(ans, matrixRank));


    } else {
      Log.exec("Starting receiving matricies");
      BroadcastData data = receive(0, MessageType.INITIAL_DATA, BroadcastData.class).getData();
      A = data.getMatrixA();
      B = data.getMatrixB();
      Log.exec("Recieved matricies");
    }

    while (true) {
      Message<SumBlock> task = ProcessorUtil.waitForTask(SumBlock.class);
      Log.exec("Recieved parallelMultiply from " + task.getSource());
      SumBlock data = task.getData();

      SumBlock sum = localMultiply(A, B, data);

      CommUtil.send(sum, task.getSource(), MessageType.TASK_RETURN);

      ProcessorUtil.reportFree();
    }
  }

  public static double[][] parallelMultiply(double[][] A, double[][] B) {
    return parallelMultiply(A, B, 0, 0, 0, 0, A.length);
  }

  public static double[][] parallelMultiply(double[][] A, double[][] B, int rowA, int colA, int rowB, int colB, int n) {
    double[][] C = new double[n][n];

    List<Integer> jobIds = new ArrayList<>();

    if (n == 1) {
      C[0][0] = A[rowA][colA] * B[rowB][colB];
      return C;
    } else {
      int newSize = n / 2;

      jobIds.add(sendMultiply(new SumBlock(C,
          new MultiplyBlock(rowA, colA, rowB, colB, newSize),
          new MultiplyBlock(rowA, colA + newSize, rowB + newSize, colB, newSize),
          0, 0)));

      jobIds.add(sendMultiply(new SumBlock(C,
          new MultiplyBlock(rowA, colA, rowB, colB + newSize, newSize),
          new MultiplyBlock(rowA, colA + newSize, rowB + newSize, colB + newSize, newSize),
          0, newSize)));

      jobIds.add(sendMultiply(new SumBlock(C,
          new MultiplyBlock(rowA + newSize, colA, rowB, colB, newSize),
          new MultiplyBlock(rowA + newSize, colA + newSize, rowB + newSize, colB, newSize),
          newSize, 0)));

      jobIds.add(sendMultiply(new SumBlock(C,
          new MultiplyBlock(rowA + newSize, colA, rowB, colB + newSize, newSize),
          new MultiplyBlock(rowA + newSize, colA + newSize, rowB + newSize, colB + newSize, newSize),
          newSize, newSize)));
    }

    while (jobIds.stream().anyMatch(id -> !results.containsKey(id))) {
      Log.exec("Waiting for subtasks to finish " + results.size());
      Log.debug(jobIds.toString());
      Log.debug(results.keySet().toString());
      ProcessorUtil.sleep(1);
    }

    List<SumBlock> jobResults = jobIds.stream()
        .map(results::get)
        .collect(Collectors.toList());

    return join(jobResults, C, n / 2);
  }

  private static int sendMultiply(SumBlock task) {
    int key = System.identityHashCode(task);
    if (task.getBlockSize() >= 4) {
      int procId = ProcessorUtil.requestProcessor(task);
      if (procId != -1) {
        CommUtil.send(task, procId, TASK_NEW);
        Log.exec("Sent parallelMultiply to " + procId);
        return procId;
      }
    }

    SumBlock result = localMultiply(A, B, task);
    results.put(key, result);
    return key;
  }

  private static SumBlock localMultiply(double[][] A, double[][] B, SumBlock sum) {
    MultiplyBlock a = sum.getA();
    MultiplyBlock b = sum.getB();

    add(sum.getC(),
        parallelMultiply(A, B, a.getRowA(), a.getColA(), a.getRowB(), a.getColB(), sum.getBlockSize()),
        parallelMultiply(A, B, b.getRowA(), b.getColA(), b.getRowB(), b.getColB(), sum.getBlockSize()),
        sum.getRowC(), sum.getColC());

    Log.debug("Subtask" + sum + '\n' + MatrixUtil.toString(sum.getC(), sum.getC().length));

    return sum;
  }

  private static double[][] join(List<SumBlock> results, double[][] C, int partitionSize) {

    for (SumBlock b : results) {
      for (int row = 0; row < partitionSize; row++) {
        for (int col = 0; col < partitionSize; col++) {
          C[row + b.getRowC()][col + b.getColC()] = b.getC()[row + b.getRowC()][col + b.getColC()];
        }
      }
    }

    return C;
  }

  private static double[][] add(double[][] C, double[][] A, double[][] B, int rowC, int colC) {
    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A[0].length; j++) {
        C[i + rowC][j + colC] = A[i][j] + B[i][j];
      }
    }
    return C;
  }
}
