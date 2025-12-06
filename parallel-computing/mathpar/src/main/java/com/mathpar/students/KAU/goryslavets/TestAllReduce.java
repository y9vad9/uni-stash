package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/*

Run command:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 4 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/TestAllReduce

 */

public class TestAllReduce {
    public static void main(String[] args) throws MPIException {
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        // визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = 5;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = myrank*10+i;
        }
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n];
        MPI.COMM_WORLD.allReduce(a, q, n, MPI.INT, MPI.PROD);
        for (int j = 0; j < np; j++) {
            if (myrank == j) {
                System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
            }
        }
        // завершення параалельної частини
        MPI.Finalize();
    }
}

/*

Result:

myrank = 0: a = [0, 1, 2, 3, 4]
myrank = 3: a = [30, 31, 32, 33, 34]
myrank = 1: a = [10, 11, 12, 13, 14]
myrank = 2: a = [20, 21, 22, 23, 24]
myrank = 0: q = [0, 7161, 16896, 29601, 45696]
myrank = 2: q = [0, 7161, 16896, 29601, 45696]
myrank = 3: q = [0, 7161, 16896, 29601, 45696]
myrank = 1: q = [0, 7161, 16896, 29601, 45696]

 */
