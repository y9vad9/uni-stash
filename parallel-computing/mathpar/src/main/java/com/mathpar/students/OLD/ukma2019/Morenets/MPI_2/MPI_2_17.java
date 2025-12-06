package com.mathpar.students.OLD.ukma2019.Morenets.MPI_2;

import mpi.*;

import java.util.Random;

public class MPI_2_17 {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        Intracomm WORLD = MPI.COMM_WORLD;

        int ranks[] = new int[WORLD.getSize()];

        for (int i = 0; i < ranks.length; i++)
            ranks[i] = i;

        Intracomm COMM_NEW = MPI.COMM_WORLD.create(MPI.COMM_WORLD.getGroup().incl(ranks));

        int rank = COMM_NEW.getRank();

        int size = Integer.parseInt(args[0]);
        double[] arr = new double[size];

        if (rank == 0) {
            for (int i = 0; i < size; i++) {
                arr[i] = new Random().nextDouble();
                System.out.println("arr[" + i + "]= " + arr[i]);
            }
        }

        COMM_NEW.barrier();
        COMM_NEW.bcast(arr, arr.length, MPI.DOUBLE, 0);

        if (rank != 0) {
            System.out.println();

            for (int i = 0; i < size; i++)
                System.out.println("arr[" + i + "]= " + arr[i]);
        }

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 3 java -cp out/production/MPI_2_17 MPI_2_17 2

Output:
arr[0]= 0.10963907084053193
arr[1]= 0.5592333649643298


arr[0]= 0.10963907084053193
arr[1]= 0.5592333649643298
arr[0]= 0.10963907084053193
arr[1]= 0.5592333649643298
*/