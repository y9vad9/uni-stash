package com.mathpar.students.OLD.ukma2019.Morenets.MPI_2;

import java.util.Random;
import mpi.*;

public class MPI_2_4 {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        Intracomm WORLD = MPI.COMM_WORLD;

        int rank = WORLD.getRank();

        int arrSize = Integer.parseInt(args[0]);
        double[] arr = new double[arrSize];

        if (rank == 0) {
            for (int i = 0; i < arrSize; i++) {
                arr[i] = new Random().nextDouble();

                System.out.println("a[" + i + "]= " + arr[i]);
            }
        }

        WORLD.barrier();

        WORLD.bcast(arr, arrSize, MPI.DOUBLE, 0);

        if (rank != 0) {
            for (int i = 0; i < arrSize; i++)
                System.out.println("a[" + i + "]= " + arr[i]);
        }

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 3 java -cp out/production/MPI_2_4 MPI_2_4 5

Output:
a[0]= 0.9143282809762258
a[1]= 0.7605655701353785
a[2]= 0.5054959032748024
a[3]= 0.9117239120552821
a[4]= 0.6323712999659526
a[0]= 0.9143282809762258
a[1]= 0.7605655701353785
a[2]= 0.5054959032748024
a[3]= 0.9117239120552821
a[4]= 0.6323712999659526
a[0]= 0.9143282809762258
a[1]= 0.7605655701353785
a[2]= 0.5054959032748024
a[3]= 0.9117239120552821
a[4]= 0.6323712999659526
*/
