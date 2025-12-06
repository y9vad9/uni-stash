package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import java.util.Arrays;
import mpi.MPI;
import mpi.MPIException;

public class MPI_3_9 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        int[] a = new int[size];

        for (int i = 0; i < size; i++)
            a[i] = myrank;

        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));

        int[] q = new int[size];

        int sendSizes[] = new int[size];
        int recvSizes[] = new int[size];

        Arrays.fill(sendSizes, 1);
        Arrays.fill(recvSizes, 1);

        int sendOffsets[] = new int[size];
        int recvOffsets[] = new int[size];

        for (int i = 0; i < size; i++)
        {
            sendOffsets[i] = i;
            recvOffsets[i] = i;
        }

        MPI.COMM_WORLD.allToAllv(a, sendSizes, sendOffsets, MPI.INT, q, recvSizes, recvOffsets, MPI.INT);

        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 4 java -cp out/production/MPI_3_9 MPI_3_9

Output:
myrank = 2: a = [2, 2, 2, 2]
myrank = 3: a = [3, 3, 3, 3]
myrank = 0: a = [0, 0, 0, 0]
myrank = 1: a = [1, 1, 1, 1]
myrank = 1: q = [0, 1, 2, 3]
myrank = 0: q = [0, 1, 2, 3]
myrank = 2: q = [0, 1, 2, 3]
myrank = 3: q = [0, 1, 2, 3]
*/