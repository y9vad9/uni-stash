package com.y9vad9.uni.openmpi.lab2;

import mpi.MPI;
import mpi.MPIException;

public class HelloWorldParallel {
    public static void main(String[] args) throws MPIException {
        // Інiцiалiзацiя паралельної частини
        MPI.Init(args);

        // Визначення номера процесора
        int myRank = MPI.COMM_WORLD.getRank();
        System.out.println("Proc num " + myRank + " Hello World");

        // Завершення паралельної частини
        MPI.Finalize();
    }
}
