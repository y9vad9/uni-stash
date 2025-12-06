package com.mathpar.students.OLD.ukma2019.Morenets.MPI_3;

import java.util.Arrays;
import java.util.Random;
import mpi.*;

public class MPI_3_1 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        double[] a = new double[n];

        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = new Random().nextDouble();
            }

            System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));
        }

        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.bcast(a, a.length, MPI.DOUBLE, 0);

        if (myrank != 0)
            System.out.println("myrank = " + myrank + " : a = " + Arrays.toString(a));

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 4 java -cp out/production/MPI_3_1 MPI_3_1 4

Output:
myrank = 0 : a = [0.3727186204125321, 0.82095236367175621, 0.023096432615028, 0.40195261414537485]
myrank = 2 : a = [0.3727186204125321, 0.82095236367175621, 0.023096432615028, 0.40195261414537485]
myrank = 1 : a = [0.3727186204125321, 0.82095236367175621, 0.023096432615028, 0.40195261414537485]
myrank = 3 : a = [0.3727186204125321, 0.82095236367175621, 0.023096432615028, 0.40195261414537485]
 */
