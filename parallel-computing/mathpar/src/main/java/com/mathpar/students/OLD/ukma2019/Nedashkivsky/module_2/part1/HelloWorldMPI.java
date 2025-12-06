package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part1;

import mpi.MPI;
import mpi.MPIException;


// COMMAND: mpirun -np 2 java -cp out/production/HelloWorldMPI HelloWorldMPI

// OUTPUT:
// Proc num 0 Hello World
// Proc num 1 Hello World


public class HelloWorldMPI {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        System.out.println("Proc num " + myrank + " Hello World");
        MPI.Finalize();
    }

}
