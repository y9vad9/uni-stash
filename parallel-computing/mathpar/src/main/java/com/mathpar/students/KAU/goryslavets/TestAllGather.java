package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;

import java.util.Arrays;

/*

Run command:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 8 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/TestAllGather

 */

public class TestAllGather {
    public static void main(String[] args)throws Exception {
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        // визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = 2;
        int[] a = new int[n];
        for(int i = 0; i < n; i++) {
            a[i] = myrank*10+i;
        }
        System.out.println("myrank = " + myrank + " : a = "+ Arrays.toString(a));
        int[] q = new int[n * np];
        MPI.COMM_WORLD.allGather(a, n, MPI.INT, q, n, MPI.INT);
        System.out.println("myrank = " + myrank + " : q = "+ Arrays.toString(q));
        // завершення паралельної частини
        MPI.Finalize();
    }
}

/*

Result:

myrank = 0 : a = [0, 1]
myrank = 3 : a = [30, 31]
myrank = 4 : a = [40, 41]
myrank = 5 : a = [50, 51]
myrank = 6 : a = [60, 61]
myrank = 7 : a = [70, 71]
myrank = 1 : a = [10, 11]
myrank = 2 : a = [20, 21]
myrank = 2 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]
myrank = 7 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]
myrank = 1 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]
myrank = 0 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]
myrank = 3 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]
myrank = 5 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]
myrank = 6 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]
myrank = 4 : q = [0, 1, 10, 11, 20, 21, 30, 31, 40, 41, 50, 51, 60, 61, 70, 71]

 */
