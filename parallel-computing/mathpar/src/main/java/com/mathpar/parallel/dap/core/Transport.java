package com.mathpar.parallel.dap.core;

import com.mathpar.log.MpiLogger;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Transport {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(Transport.class);
    private static final long serialVersionUID = 12358903454875L;
    static long startsend = 0;
    static long endsend = 0;
    public enum Tag{
        TASK, FREE_PROC, PROC_STATE, RESULT, FINAL, REQUEST_TO_APPROVE, APPROVAL, CANCEL
    }
    // теги:
    //     0: сообщение содержит задачу
    //     1: сообщение содержит свободные узлы
    //     2: сообщение содержит состояние процесора
    //     3: сообщение содержит результат задачи
    //     4: сообщение содержит доп компоненти
    //     5: сообщение содержит команду на завершение (вся задача посчитана)




    public static Status probeAny(Intracomm COMM) throws MPIException {
        return COMM.iProbe(MPI.ANY_SOURCE, MPI.ANY_TAG);
    }


    public static void sendIntArray(int[] data, int destination, Intracomm COMM, Tag tag) throws MPIException {
        startsend = System.currentTimeMillis();

        COMM.send(data, data.length, MPI.INT, destination, tag.ordinal());

        endsend = System.currentTimeMillis();
        //DispThread.sleepSendTime += endsend - startsend;
    }

    public static void iSendIntArray(int[] data, int destination,Intracomm COMM, Tag tag) throws MPIException {
        IntBuffer b = MPI.newIntBuffer(data.length);
        for (int i = 0; i < data.length; i++){
            b.put(data[i]);
        }

        startsend = System.currentTimeMillis();

        COMM.iSend(b, data.length, MPI.INT, destination, tag.ordinal());

        endsend = System.currentTimeMillis();
       // DispThread.sleepSendTime += endsend - startsend;
    }

    public static int[] receiveIntArray(int size, int source, Intracomm COMM, Tag tag) throws MPIException {
        int[] array = new int[size];
        COMM.recv(array, size, MPI.INT, source, tag.ordinal());
        return array;
    }

   /* public static int[] iReceiveIntArray(int size, int source, Tag tag) throws MPIException {

        IntBuffer b = MPI.newIntBuffer(size);
        MPI.COMM_WORLD.iRecv(b, size, MPI.INT, source, tag.ordinal());
        int []arr = new int[size];
        for (int i = 0; i < size; i++){
            //LOGGER.info("here");
            arr[i] = b.get(i);
        }
        return arr;
    }*/

    public static void sendObject(Object a, int proc,Intracomm COMM, Tag tag) throws MPIException, IOException {
        //LOGGER.info("in sendObject ");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(a);
        byte[] tmp = bos.toByteArray();
       // LOGGER.info(String.format("send to=%d bytes=%d tag=%s", proc, tmp.length, tag));
      //  ByteBuffer buf = MPI.newByteBuffer(tmp.length);
      //  buf.put(tmp);
        startsend = System.currentTimeMillis();

        //LOGGER.info("bef send ");
        COMM.send(tmp, tmp.length, MPI.BYTE, proc, tag.ordinal());

        endsend = System.currentTimeMillis();

       // LOGGER.info("time sending = " + (endsend-startsend));
       // DispThread.sleepSendTime += endsend - startsend;
    }



    public static Object recvObject(int proc,Intracomm COMM, Tag tag) throws MPIException, IOException {
        Status st = COMM.probe(proc, tag.ordinal());
        int size = st.getCount(MPI.BYTE);

        byte[] arr = new byte[size];
       // ByteBuffer buff = MPI.newByteBuffer(size);
        COMM.recv(arr, size, MPI.BYTE, proc, tag.ordinal());
       // buff.get(arr, 0, size);
        LOGGER.trace(String.format("receive from=%d bytes=%d tag=%s", proc, arr.length, tag));
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInputStream ois = null;
        Object res = null;
        try {
            ois = new ObjectInputStream(bis);
            res = ois.readObject();
        } catch (EOFException e) {
            // nothing to do
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void sendObjects(Object[] a, int proc,Intracomm COMM, Tag tag) throws MPIException {
        byte[] tempBytes = new byte[0];
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            for (int i = 0; i < a.length; i++) oos.writeObject(a[i]);
            tempBytes = bos.toByteArray();
        } catch (Exception ex) {
            Logger.getLogger(Transport.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.trace(String.format("send array to=%d bytes=%d tag=%s", proc, tempBytes.length, tag));
        ByteBuffer buf = MPI.newByteBuffer(tempBytes.length);
        buf.put(tempBytes);
        startsend = System.currentTimeMillis();

        COMM.send(buf, tempBytes.length, MPI.BYTE, proc, tag.ordinal());

        endsend = System.currentTimeMillis();
        //DispThread.sleepSendTime += endsend - startsend;
    }

    public static Object[] recvObjects(int m, int proc, Intracomm COMM, Tag tag) throws MPIException, IOException {
        ObjectInputStream ois = null;
        Status s = COMM.probe(proc, tag.ordinal());
        int n = s.getCount(MPI.BYTE);

        byte[] arr = new byte[n];
        ByteBuffer buffer = MPI.newByteBuffer(n);
        COMM.recv(buffer, n, MPI.BYTE, proc, tag.ordinal());
        buffer.get(arr, 0, n);
        LOGGER.trace(String.format(" receive array from=%d bytes=%d tag=%s", proc, arr.length, tag));
        Object[] res = new Object[m];
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ois = new ObjectInputStream(bis);
            for (int i = 0; i < m; i++)
                res[i] =  ois.readObject();

        } catch (EOFException e){
            // nothing to do
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
