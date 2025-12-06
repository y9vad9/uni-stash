package com.mathpar.students.OLD.ukma2019.Morenets.MPI_2;

import mpi.*;

public class MPI_2_1 {

    public static void main(String[] args) throws MPIException
    {
            MPI.Init(args);

            int myRank = MPI.COMM_WORLD.getRank();

            System.out.println(String.format("Proc num %s Hello World", myRank));

            MPI.Finalize();
    }
}

// Command: mpirun -np 2 java -cp out/production/MPI.HelloWorld MPI_2_1

// Output:
// Proc num 0 Hello World
// Proc num 1 Hello World
