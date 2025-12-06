package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part1;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;

// COMMAND: mpirun -np 4 java -cp out/production/Task_2_2_mpi Task_2_2_mpi 2

// OUTPUT:
//array[0]= 0.019576279484985193
//array[1]= 0.11935677043789705
//Proc num 0 array send
//
//array[0]= 0.019576279484985193
//array[1]= 0.11935677043789705
//Proc num 3 array recieved
//
//array[0]= 0.019576279484985193
//array[1]= 0.11935677043789705
//Proc num 2 array recieved
//
//array[0]= 0.019576279484985193
//array[1]= 0.11935677043789705
//Proc num 1 array recieved


public class Task_2_2_mpi {
    public static void main(String[] args) throws MPIException {
        // Initialization MPI
        MPI.Init(args);
        // Definition a processor amount
        final int myrank = MPI.COMM_WORLD.getRank();
        // Definition a processor amount in a group
        final int np = MPI.COMM_WORLD.getSize();
        // Input parameter - an array size
        int n = 4;
        if (args.length != 0) {
            n = Integer.parseInt(args[0]);
        }

        double[] a = new double[n];
        // Processors synchronization
        MPI.COMM_WORLD.barrier();
        // If the processor's number equals to 0
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = (new Random()).nextDouble();
                System.out.println("array[" + i + "]= " + a[i]);
            }
            // Sending elements by 0's processor
            for (int i = 1; i < np; i++) {
                MPI.COMM_WORLD.send(a, n, MPI.DOUBLE, i, 3000);
            }
            System.out.println("Proc num " + myrank + " array send" + "\n");
        } else {
            // Getting a message to processor with number i from processor with number 0
            MPI.COMM_WORLD.recv(a, n, MPI.DOUBLE, 0, 3000);

            for (int i = 0; i < n; i++) {
                System.out.println("array[" + i + "]= " + a[i]);
            }

            System.out.println("Proc num " + myrank + " array recieved" + "\n");
        }
        MPI.Finalize();
    }
}
