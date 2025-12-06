package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part2;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;
import java.util.Random;

// COMMAND: mpirun -np 2 java -cp out/production/Task_3_1_mpi Task_3_1_mpi 4

// OUTPUT:
//myrank = 0 : a = [0.03154582725826893, 0.9022268971599082, 0.9685250865622432, 0.3255315519228673]
//myrank = 1 : a = [0.03154582725826893, 0.9022268971599082, 0.9685250865622432, 0.3255315519228673]


public class Task_3_1_mpi {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        // Input parameter - an array size
        int n = Integer.parseInt(args[0]);
        double[] a = new double[n];
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = new Random().nextDouble();
            }
            System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));
        }
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.bcast(a, a.length, MPI.DOUBLE, 0);
        if (myrank != 0) {
            System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));
        }
        MPI.Finalize();
    }
}
