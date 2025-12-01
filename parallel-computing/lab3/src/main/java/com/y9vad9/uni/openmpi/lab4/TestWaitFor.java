package com.y9vad9.uni.openmpi.lab4;

import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TestWaitFor {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        int[] data = new int[1];

        // Створюємо direct buffer для передачі через MPI
        ByteBuffer direct = ByteBuffer.allocateDirect(Integer.BYTES);
        IntBuffer buf = direct.asIntBuffer();

        Request request;
        if (rank == 0) {
            data[0] = 42;

            // Нова версія приймає Buffer, тому записуємо дані в direct buffer
            buf.put(0, data[0]);

            request = MPI.COMM_WORLD.iSend(buf, 1, MPI.INT, 1, 99);
            request.waitFor();

            System.out.println("Rank 0: вiдправлено " + data[0]);
        } else if (rank == 1) {
            // Нова версія приймає Buffer, тому використовуємо direct buffer
            request = MPI.COMM_WORLD.iRecv(buf, 1, MPI.INT, 0, 99);
            request.waitFor();

            // Читаємо результат
            data[0] = buf.get(0);

            System.out.println("Rank 1: отримано " + data[0]);
        }

        MPI.Finalize();
    }
}
