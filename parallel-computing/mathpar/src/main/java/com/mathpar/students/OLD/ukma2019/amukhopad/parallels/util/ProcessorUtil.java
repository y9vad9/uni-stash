package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util;

import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType.PROC_ALLOCATE;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType.PROC_FREE;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType.PROC_REQUEST;
import static com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType.TASK_NEW;

import java.io.Serializable;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Log;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Message;
import mpi.MPI;
import mpi.MPIException;

public class ProcessorUtil {

  public static <T extends Serializable> int requestProcessor(T data) {
    CommUtil.send(data, 0, PROC_REQUEST);
    return CommUtil.receiveInt(0, PROC_ALLOCATE);
  }

  public static int requestProcessor() {
    Log.exec("Requesting processor");
    CommUtil.send(getCurrentId(), 0, PROC_REQUEST);
    return CommUtil.receiveInt(0, PROC_ALLOCATE);
  }

  public static <T> Message<T> waitForTask(Class<T> taskDataType) {
    Log.exec("Waiting for task");
    return CommUtil.listen(TASK_NEW, taskDataType);
  }

  public static void reportFree() {
    Log.exec("Report Free");
    CommUtil.send(getCurrentId(), 0, PROC_FREE);
  }

  public static int getCurrentId() {
    try {
      return MPI.COMM_WORLD.getRank();
    } catch (MPIException e) {
      throw new RuntimeException(e);
    }
  }

  public static void sleep(double sec) {
    try {
      Thread.sleep((long) (sec * 1000));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static Thread startDaemon(Runnable runnable) {
    Thread daemon = new Thread(() -> {
      while (true) {
        runnable.run();
      }
    });
    daemon.setDaemon(true);
    daemon.start();

    return daemon;
  }

  public static int getTotalProcesses() {
    try {
      return MPI.COMM_WORLD.getSize();
    } catch (MPIException e) {
      throw new RuntimeException(e);
    }
  }
}
