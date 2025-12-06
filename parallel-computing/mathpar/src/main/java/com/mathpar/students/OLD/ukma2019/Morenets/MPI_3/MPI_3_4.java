package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import java.util.Arrays;
import mpi.*;

public class MPI_3_4 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n * np];

        if (myrank == 0){
            for (int i = 0; i < a.length; i++)
                a[i] = i ;

            System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        }

        int[] q = new int[n];

        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.scatter(a, n, MPI.INT, q, n, MPI.INT, 0);

        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 2 java -cp out/production/MPI_3_4 MPI_3_4 4

Output:
myrank = 0: a = [0, 0, 0, 0]
myrank = 1: a = [1, 1, 1, 1]
myrank = 1: q = [0, 0, 1, 1, 1, 1, 0, 0]
*/