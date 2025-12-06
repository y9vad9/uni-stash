package com.mathpar.students.OLD.ukma2019.Morenets.MPI_2;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;
import mpi.Intracomm;

public class MPI_2_7 {

    public static void main(String[] args) throws MPIException, InterruptedException {
        MPI.Init(args);

        Intracomm WORLD = MPI.COMM_WORLD;

        int rank = WORLD.getRank();

        int size = Integer.parseInt(args[0]);
        int[] arr = new int[size];
        Arrays.fill(arr, rank);

        int procNum = WORLD.getSize();
        int[] res = new int[size * procNum];

        WORLD.allGather(arr, size, MPI.INT, res, size, MPI.INT);

        Thread.sleep(size * rank);

        System.out.println("Proc " + rank + " received:");

        for (int el : res)
            System.out.println(el);

        System.out.println();

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 3 java -cp out/production/MPI_2_7 MPI_2_7 4

Output:
Proc 0 received:
0
0
0
0
1
1
1
1
2
2
2
2

Proc 1 received:
0
0
0
0
1
1
1
1
2
2
2
2

Proc 2 received:
0
0
0
0
1
1
1
1
2
2
2
2
*/
