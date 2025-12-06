package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import java.util.Arrays;
import mpi.*;

public class MPI_3_6 {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];

        for(int i = 0; i < n; i++)
            a[i] = myrank;

        System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));

        int[] q = new int[n * np];

        MPI.COMM_WORLD.allGather(a, n, MPI.INT, q, n, MPI.INT);

        Thread.sleep(60 * myrank);

        System.out.println("myrank = " + myrank + " : q = " + Arrays.toString(q));

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 4 java -cp out/production/MPI_3_6 MPI_3_6 4

Output:
myrank = 0: a = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
myrank = 0: q = [0, 1, 2, 3]
myrank = 3: q = [12, 13, 14, 15]
myrank = 1: q = [4, 5, 6, 7]
myrank = 2: q = [8, 9, 10, 11]
*/