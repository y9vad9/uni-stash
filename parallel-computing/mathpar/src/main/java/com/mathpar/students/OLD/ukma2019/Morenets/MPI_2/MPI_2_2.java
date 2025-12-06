package com.mathpar.students.OLD.ukma2019.Morenets.MPI_2;

import mpi.*;

import java.util.Random;

public class MPI_2_2 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        final Intracomm WORLD = MPI.COMM_WORLD;

        WORLD.barrier();

        final int arraySize = Integer.parseInt(args[0]);

        double[] array = new double[arraySize];

        final int myRank = WORLD.getRank();

        final int msgTag = 3000;

        if (myRank == 0) {
            for (int i = 0; i < arraySize; ++i) {
                array[i] = new Random().nextDouble();

                System.out.println("a[" + i + "]= " + array[i]);
            }

            for (int procNum = 1; procNum < WORLD.getSize(); ++procNum)
                WORLD.send(array, arraySize, MPI.DOUBLE, procNum, msgTag);

            System.out.println("Proc num " + myRank +" Array sent\n");
        } else {
            WORLD.recv(array, arraySize, MPI.DOUBLE, 0, msgTag);

            for (int i = 0; i < arraySize; ++i)
                System.out.println("a[" + i + "]= " + array[i]);

            System.out.println("Proc num " + myRank +" Array received\n");
        }

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 3 java -cp out/production/MPI.2.2 MPI_2_2 5

Output:
a[0]= 0.7453919044010927
a[1]= 0.29248519683239405
a[2]= 0.4728562004208485
a[3]= 0.6875779943440042
a[4]= 0.9819462549482042
Proc num 0 Array sent

a[0]= 0.7453919044010927
a[1]= 0.29248519683239405
a[2]= 0.4728562004208485
a[3]= 0.6875779943440042
a[4]= 0.9819462549482042
a[0]= 0.7453919044010927
a[1]= 0.29248519683239405
a[2]= 0.4728562004208485
a[3]= 0.6875779943440042
a[4]= 0.9819462549482042
Proc num 1 Array received

Proc num 2 Array received
*/
