package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch;

import static java.lang.String.format;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.CommUtil;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.ProcessorUtil;

public class Driver<T> implements Runnable {
  private Queue<Integer> freeProcsQueue;
  private Queue<Message<T>> requestQueue;
  private int numberOfProcesses;
  private Class<T> dataType;

  private Driver() {

  }

  private Driver(Class<T> dataType) {
    this.numberOfProcesses = ProcessorUtil.getTotalProcesses();
    this.freeProcsQueue = new ArrayBlockingQueue<>(numberOfProcesses);
    this.requestQueue = new PriorityBlockingQueue<>(numberOfProcesses);
    this.dataType = dataType;
  }

  public static <D> Driver<D> create(Class<D> dataType) {
    return new Driver<>(dataType);
  }

  public static <D> Thread start(Class<D> dataType) {
    return ProcessorUtil.startDaemon(Driver.create(dataType));
  }

  @Override
  public void run() {
    for (int rank = 1; rank < ProcessorUtil.getTotalProcesses(); rank++) {
      freeProcsQueue.offer(rank);
    }

    Log.driver("Starting free processor report listener");
    ProcessorUtil.startDaemon(() -> {
      Message<Void> report = CommUtil.listen(MessageType.PROC_FREE, Void.class);
      Log.driver("[Free Listener] free: " + report.getSource());
      freeProcsQueue.offer(report.getSource());
      Log.driver("[Free Listener] FreeProcsQueue: " + freeProcsQueue.toString());
    });

    ProcessorUtil.sleep(5);
    Log.driver("Starting processor request listener");
    ProcessorUtil.startDaemon(() -> {
      Message<T> request = CommUtil.listen(MessageType.PROC_REQUEST, dataType);
      Log.driver("[Request Listener] request from " + request.getSource());
      requestQueue.offer(request);
      Log.driver("[Request Listener] RequestQueue: " + requestQueue.toString());
    });

    while (true) {
      if (!requestQueue.isEmpty()) {
        int allocatedProcId = freeProcsQueue.isEmpty() ? -1 : freeProcsQueue.poll();

        int senderProcId = requestQueue.poll().getSource();
        Log.driver(format("Allocating process %d to process %d\n", allocatedProcId, senderProcId));
        CommUtil.send(allocatedProcId, senderProcId, MessageType.PROC_ALLOCATE);
      }
    }
  }
}

