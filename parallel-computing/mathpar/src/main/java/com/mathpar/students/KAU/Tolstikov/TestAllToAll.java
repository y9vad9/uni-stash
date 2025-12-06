package com.mathpar.students.KAU.Tolstikov;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestAllToAll {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = 4;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = myrank*10+i;
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.allToAll(a, 1, MPI.INT, q, 1, MPI.INT);
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
        MPI.Finalize();
    }
}

/*
    Command to run:
    mpirun --hostfile /home/user/hostfile -np 4 java -cp /home/vladislav/dap/target/classes com/mathpar/students/KAU/Tolstikov/TestAllToAll
*/

/*
    Result:
    myrank = 1: a = [10, 11, 12, 13]
    myrank = 0: a = [0, 1, 2, 3]
    myrank = 3: a = [30, 31, 32, 33]
    myrank = 2: a = [20, 21, 22, 23]
    myrank = 3: q = [3, 13, 23, 33]myrank = 2: q = [2, 12, 22, 32]

    myrank = 0: q = [0, 10, 20, 30]
    myrank = 1: q = [1, 11, 21, 31]
 */
