package com.y9vad9.uni.openmpi.lab1;

import mpi.MPI;
import mpi.MPIException;

/**
 * <h2>Команда для запуску</h2>
 * <pre>
 * mpijavac -d build/classes HelloWorldParallel.java
 * mpirun -np 16 --hostfile <HOSTFILE_PATH> --bind-to none java -cp build/classes \
 *     -Djava.library.path=<MPI_LIB_PATH>
 *     --enable-native-access=ALL-UNNAMED com.y9vad9.uni.openmpi.lab1.HelloWorldParallel
 * </pre>
 *
 * <h2>Протокол</h2>
 * <pre>
 * Proc num 2 Hello World
 * Proc num 1 Hello World
 * Proc num 3 Hello World
 * Proc num 6 Hello World
 * Proc num 15 Hello World
 * Proc num 4 Hello World
 * Proc num 8 Hello World
 * Proc num 0 Hello World
 * Proc num 9 Hello World
 * Proc num 11 Hello World
 * Proc num 14 Hello World
 * Proc num 5 Hello World
 * Proc num 13 Hello World
 * Proc num 7 Hello World
 * Proc num 12 Hello World
 * Proc num 10 Hello World
 * </pre>
 */
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
