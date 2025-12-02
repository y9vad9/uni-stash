package com.y9vad9.uni.openmpi.lab7;

import mpi.MPI;
import mpi.MPIException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class Transport {
    public static void sendObject(Object a, int proc, int tag)
        throws MPIException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(a);
        byte[] tmp = bos.toByteArray();
        MPI.COMM_WORLD.send(tmp, tmp.length, MPI.BYTE, proc, tag);
    }

    public static Object recvObject(int proc, int tag)
        throws MPIException, IOException, ClassNotFoundException {
        mpi.Status st = MPI.COMM_WORLD.probe(proc, tag);
        int size = st.getCount(MPI.BYTE);
        byte[] tmp = new byte[size];
        MPI.COMM_WORLD.recv(tmp, size, MPI.BYTE, proc, tag);

        ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }

    public static void sendObjects(Object[] a, int proc, int tag)
        throws MPIException {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            for (Object obj : a) oos.writeObject(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        byte[] temp = bos.toByteArray();
        ByteBuffer buf = MPI.newByteBuffer(temp.length);
        buf.put(temp);
        MPI.COMM_WORLD.iSend(buf, temp.length, MPI.BYTE, proc, tag);
    }

    public static Object[] recvObjects(int m, int proc, int tag)
        throws MPIException {
        mpi.Status s = MPI.COMM_WORLD.probe(proc, tag);
        int n = s.getCount(MPI.BYTE);
        byte[] arr = new byte[n];
        MPI.COMM_WORLD.recv(arr, n, MPI.BYTE, proc, tag);

        Object[] res = new Object[m];
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ObjectInputStream ois = new ObjectInputStream(bis);
            for (int i = 0; i < m; i++) res[i] = ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static Object bcastObject(Object o, int root)
        throws IOException, MPIException, ClassNotFoundException {
        byte[] tmp = null;
        int[] size = new int[1];
        int rank = MPI.COMM_WORLD.getRank();

        if (rank == root) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            tmp = bos.toByteArray();
            size[0] = tmp.length;
        }

        MPI.COMM_WORLD.bcast(size, 1, MPI.INT, root);
        if (rank != root) tmp = new byte[size[0]];

        MPI.COMM_WORLD.bcast(tmp, tmp.length, MPI.BYTE, root);

        if (rank != root) {
            ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        }
        return o;
    }

    public static void bcastObjectArray(Object[] o, int count, int root)
        throws IOException, MPIException, ClassNotFoundException {
        byte[] tmp = null;
        int[] size = new int[1];
        int rank = MPI.COMM_WORLD.getRank();

        if (rank == root) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            for (int i = 0; i < count; i++) oos.writeObject(o[i]);
            tmp = bos.toByteArray();
            size[0] = tmp.length;
        }

        MPI.COMM_WORLD.bcast(size, 1, MPI.INT, root);
        if (rank != root) tmp = new byte[size[0]];

        MPI.COMM_WORLD.bcast(tmp, tmp.length, MPI.BYTE, root);

        if (rank != root) {
            ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
            ObjectInputStream ois = new ObjectInputStream(bis);
            for (int i = 0; i < count; i++) o[i] = ois.readObject();
        }
    }
}
