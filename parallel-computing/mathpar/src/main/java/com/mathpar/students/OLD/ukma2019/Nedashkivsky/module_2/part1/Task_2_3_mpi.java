package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part1;

import mpi.MPI;
import mpi.MPIException;

import java.nio.IntBuffer;
import java.util.Random;

// COMMAND: mpirun -np 4 java -cp out/production/Task_2_3_mpi Task_2_3_mpi 2

// OUTPUT:
//proc num = 0 array was sent
//proc num = 1 array was received
//proc num = 2 array was received
//proc num = 3 array was received


public class Task_2_3_mpi {
    public static void main(String[] args) throws MPIException {
        // Initialization MPI
        MPI.Init(args);
        // Definition a processor amount
        int myrank = MPI.COMM_WORLD.getRank();
        // Definition a processor amount in a group
        int np = MPI.COMM_WORLD.getSize();
        // Input parameter - an array size
        int n = Integer.parseInt(args[0]);

        IntBuffer b = MPI.newIntBuffer(n);
        // Processors synchronization
        MPI.COMM_WORLD.barrier();
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                b.put(new Random().nextInt(10));
            }
            for (int i = 1; i < np; i++) {
                MPI.COMM_WORLD.iSend(b, b.capacity(), MPI.INT, i, 3000);
            }
            System.out.println("proc num = " + myrank + " array was sent");
        } else {
            MPI.COMM_WORLD.recv(b, b.capacity(), MPI.INT, 0, 3000);
            System.out.println("proc num = " + myrank + " array was received");
        }
        // Completion a parallel part
        MPI.Finalize();
    }
}
