package com.y9vad9.uni.openmpi.lab2;

import mpi.MPI;
import mpi.MPIException;
import java.nio.IntBuffer;
import java.util.Random;

public class TestISendAndIRecv {
    public static void main(String[] args) throws MPIException {
        // iнiцiалiзацiя MPI
        MPI.Init(args);

        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();

        // визначення кiлькостi процесорiв у групi
        int np = MPI.COMM_WORLD.getSize();

        // розмiр масиву
        int n = 5;
        IntBuffer b = MPI.newIntBuffer(n);

        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                b.put(new Random().nextInt(10));
            }

            for (int i = 1; i < np; i++) {
                MPI.COMM_WORLD.iSend(b, b.capacity(), MPI.INT, i, 3000);
            }

            System.out.println("proc num = " + myrank + " масив вiдправлено");

        } else {
            MPI.COMM_WORLD.recv(b, b.capacity(), MPI.INT, 0, 3000);
            System.out.println("proc num = " + myrank + " масив отримано");
        }

        // завершення паралельної частини
        MPI.Finalize();
    }
}
