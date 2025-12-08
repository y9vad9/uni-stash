package com.y9vad9.uni.openmpi.finale;

import com.mathpar.matrix.MatrixD;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TransportObjs {

    /** Відправка об'єкта, що реалізує Serializable, конкретному процесу */
    public static void sendSerializable(Serializable obj, int dest, int tag, Intracomm com)
        throws MPIException {
        byte[] data = serialize(obj); // перетворюємо об'єкт у байти
        com.send(data, data.length, MPI.BYTE, dest, tag); // відправляємо байти
    }

    /** Отримання Serializable об'єкта від конкретного процесу */
    public static <T extends Serializable> T recvSerializable(int source, int tag, Intracomm com)
        throws MPIException {
        Status s = com.probe(source, tag); // перевіряємо, скільки байт прийде
        int count = s.getCount(MPI.BYTE);

        byte[] buf = new byte[count];
        com.recv(buf, count, MPI.BYTE, source, tag); // отримуємо байти

        return (T) deserialize(buf); // перетворюємо байти назад в об'єкт
    }

    /**
     * Простий broadcast: root відправляє всім, інші отримують
     * @param obj об'єкт, який розсилаємо
     * @param root процес, який розсилає
     * @param com комунікатор
     */
    public static Serializable recvOrBcast(Serializable obj, int root, Intracomm com)
        throws MPIException {
        int rank = com.getRank();
        int size = com.getSize();

        if (rank == root) {
            // Root розсилає всім іншим
            for (int i = 0; i < size; i++) {
                if (i != root) sendSerializable(obj, i, 9999, com);
            }
            return obj;
        } else {
            // Інші процеси отримують
            return recvSerializable(root, 9999, com);
        }
    }

    /** Broadcast спеціально для матриці MatrixD */
    public static MatrixD bcastMatrix(MatrixD mat, int root, Intracomm com) throws MPIException {
        int rank = com.getRank();
        // root передає матрицю, інші null
        Serializable obj = (rank == root) ? (Serializable) mat : null;
        obj = recvOrBcast(obj, root, com); // колективна операція
        return (MatrixD) obj;
    }

    /**
     * AllGather для Serializable об'єктів.
     * Кожен процес надсилає свій об'єкт, кожен отримує всі об'єкти.
     *
     * @param send масив з 1 елементом — об'єкт, який надсилаємо
     * @param recv масив для отримання (розмір = кількість процесів)
     * @param comm комунікатор
     */
    public static void AllGatherObjs(Object[] send, Object[] recv, Intracomm comm)
        throws MPIException {

        int rank = comm.getRank();
        int size = comm.getSize();

        // 1) Серіалізуємо локальний об'єкт у байти
        byte[] sendBytes = serialize(send[0]);
        int[] sendLength = new int[]{sendBytes.length};

        // 2) Всі процеси обмінюються розмірами масивів
        int[] allLengths = new int[size];
        comm.allGather(sendLength, 1, MPI.INT, allLengths, 1, MPI.INT);

        // 3) Знаходимо максимальний розмір, щоб вирівняти байтові масиви
        int maxLength = 0;
        for (int len : allLengths) if (len > maxLength) maxLength = len;

        // 4) Підготовка буфера для allGather
        byte[] sendBufFixed = new byte[maxLength];
        System.arraycopy(sendBytes, 0, sendBufFixed, 0, sendBytes.length);

        byte[] recvBuf = new byte[maxLength * size]; // буфер для всіх об'єктів

        // 5) Виконуємо AllGather
        comm.allGather(sendBufFixed, maxLength, MPI.BYTE, recvBuf, maxLength, MPI.BYTE);

        // 6) Десеріалізуємо кожен блок і поміщаємо в recv[]
        for (int i = 0; i < size; i++) {
            int len = allLengths[i];
            byte[] slice = new byte[len];
            System.arraycopy(recvBuf, i * maxLength, slice, 0, len);
            recv[i] = deserialize(slice);
        }
    }

    /**
     * Збирає локальні матриці від всіх процесів на root
     * @param local локальна матриця
     * @param root процес, який збирає
     * @param com комунікатор
     * @return масив матриць на root, null на інших
     */
    public static MatrixD[] gatherMatrix(MatrixD local, int root, Intracomm com) throws MPIException {
        int rank = com.getRank();
        int size = com.getSize();

        // Серіалізація локальної матриці
        byte[] localBytes = serialize(local);
        int localLen = localBytes.length;

        // Збираємо розміри масивів на root
        int[] lengths = new int[size];
        if (rank == root) {
            com.gather(new int[]{localLen}, 1, MPI.INT, lengths, 1, MPI.INT, root);
        } else {
            com.gather(new int[]{localLen}, 1, MPI.INT, null, 0, MPI.INT, root);
        }

        MatrixD[] result = null;

        if (rank == root) {
            // root готує масив для отриманих матриць
            result = new MatrixD[size];

            // максимальний розмір блоку
            int maxLen = 0;
            for (int l : lengths) if (l > maxLen) maxLen = l;

            byte[] sendBuf = new byte[maxLen];
            System.arraycopy(localBytes, 0, sendBuf, 0, localLen);

            byte[] recvBuf = new byte[maxLen * size];

            // Gather серіалізованих матриць
            com.gather(sendBuf, maxLen, MPI.BYTE, recvBuf, maxLen, MPI.BYTE, root);

            // Десеріалізація кожної матриці
            for (int i = 0; i < size; i++) {
                int len = lengths[i];
                byte[] slice = new byte[len];
                System.arraycopy(recvBuf, i * maxLen, slice, 0, len);
                result[i] = (MatrixD) deserialize(slice);
            }
        } else {
            // не-root: надсилає свій буфер
            int maxLen = localLen; // буде проігноровано, root визначає max
            byte[] sendBuf = new byte[maxLen];
            System.arraycopy(localBytes, 0, sendBuf, 0, localLen);

            com.gather(sendBuf, maxLen, MPI.BYTE, null, 0, MPI.BYTE, root);
        }

        return result;
    }

    /** Серіалізація об'єкта у байти */
    private static byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    /** Десеріалізація байтів у об'єкт */
    private static Object deserialize(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
