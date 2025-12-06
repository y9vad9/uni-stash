package com.mathpar.students.OLD.ukma2019.Morenets.MPI_2;

import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;

public class MPI_2_9 {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        Intracomm WORLD = MPI.COMM_WORLD;

        int rank = WORLD.getRank();

        int procNum = WORLD.getSize();
        int[] arr = new int[procNum];

        if (rank == 0) {
            for (int i = 0; i < procNum; i++){
                arr[i] = i;
                System.out.println("arr[" + i + "] = " + arr[i]);
            }

            System.out.println();
        }

        int[] res = new int[procNum];

        WORLD.barrier();
        WORLD.scatter(arr, 1, MPI.INT, res, 1, MPI.INT, 0);

        System.out.println("Proc " + rank + " received:");

        for (int el : res)
            System.out.println(el);

        System.out.println();

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 3 java -cp out/production/MPI_2_9 MPI_2_9

Output:
arr[0] = 0
arr[1] = 1
arr[2] = 2

Proc 0 received:
0
0
0

Proc 1 received:
1
0
0

Proc 2 received:
2
0
0

*/
