package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part1;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;

// COMMAND: mpirun -np 4 java -cp out/production/Task_2_4_mpi Task_2_4_mpi 2

// OUTPUT:
//array[0]= 0.29641297445400416
//array[1]= 0.6831723960070969
//array[0]= 0.29641297445400416
//array[0]= 0.29641297445400416
//array[1]= 0.6831723960070969
//array[1]= 0.6831723960070969
//array[0]= 0.29641297445400416
//array[1]= 0.6831723960070969


public class Task_2_4_mpi {
    public static void main(String[] args) throws MPIException {
        // Initialization MPI
        MPI.Init(args);
        // Definition a processor amount
        int myrank = MPI.COMM_WORLD.getRank();
        // Input parameter - an array size
        int n = Integer.parseInt(args[0]);
        double[] a = new double[n];
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = new Random().nextDouble();
                System.out.println("array[" + i + "]= " + a[i]);
            }
        }
        MPI.COMM_WORLD.barrier();
        // Sending a data from processor with the number of 0 to others
        MPI.COMM_WORLD.bcast(a, a.length, MPI.DOUBLE, 0);
        if (myrank != 0) {
            for (int i = 0; i < n; i++) {
                System.out.println("array[" + i + "]= " + a[i]);
            }
        }
        // Completion a parallel part
        MPI.Finalize();
    }
}
