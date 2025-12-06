package com.mathpar.students.OLD.ukma2019.Nedashkivsky.module_2.part1;

import mpi.MPI;
import mpi.MPIException;

// COMMAND: mpirun -np 2 java -cp out/production/Task_2_1_mpi Task_2_1_mpi

// OUTPUT:
// Proc num 0 Hello World
// Proc num 1 Hello World

public class Task_2_1_mpi {
    public static void main(String[] args) throws MPIException
    {
        MPI.Init(args);

        int myRank = MPI.COMM_WORLD.getRank();

        System.out.println(String.format("Proc num %s Hello World", myRank));

        MPI.Finalize();
    }
}
