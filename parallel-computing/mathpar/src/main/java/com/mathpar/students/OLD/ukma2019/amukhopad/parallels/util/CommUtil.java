package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util;

import java.io.Serializable;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Log;
import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch.Message;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

public class CommUtil {
  public static <T extends Serializable> void sendToAll(T data, MessageType msgTag) {
    byte[] serializedData = SerializeUtil.writeObject(data);
    Log.debug("Broadcasting " + serializedData.length + " bytes " + data.getClass().getSimpleName());
    try {
      for (int i = 0; i < ProcessorUtil.getTotalProcesses(); i++) {
        if (i != ProcessorUtil.getCurrentId()) {
          MPI.COMM_WORLD.send(serializedData, serializedData.length, MPI.BYTE, i, msgTag.ordinal());
        }
      }
    } catch (MPIException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T extends Serializable> void send(T data, int processId, MessageType msgTag) {
    byte[] serializedData = SerializeUtil.writeObject(data);
    Log.debug("Sending " + msgTag + " " + serializedData.length + " bytes "
        + data.getClass().getSimpleName()
        + " from " + ProcessorUtil.getCurrentId() +  " to " + processId);
    try {
      MPI.COMM_WORLD.send(serializedData, serializedData.length, MPI.BYTE, processId, msgTag.ordinal());
    } catch (MPIException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> Message<T> listen(MessageType msgTag, Class<T> type) {
    Log.debug("Listening for " + msgTag);
    return receive(MPI.ANY_SOURCE, msgTag, type);
  }

  public static int receiveInt(int processId, MessageType msgTag) {
    return receive(processId, msgTag, Integer.TYPE).getData();
  }

  public static <T> Message<T> receive(int fromProcId, MessageType msgTag, Class<T> type) {
    byte[] buffer;

    try {
      Status status = MPI.COMM_WORLD.probe(fromProcId, msgTag.ordinal());
      fromProcId = status.getSource();
      int count = status.getCount(MPI.BYTE);
      buffer = new byte[count];
      Log.debug("Receiving " + count + " bytes " + type.getSimpleName() + " from " + fromProcId);
      MPI.COMM_WORLD.recv(buffer, count, MPI.BYTE, fromProcId, msgTag.ordinal());
    } catch (MPIException e) {
      throw new RuntimeException(e);
    }

    T deserialized = SerializeUtil.readObject(buffer, type);
    return new Message<T>()
        .setData(deserialized)
        .setSource(fromProcId)
        .setTag(msgTag);
  }
}
