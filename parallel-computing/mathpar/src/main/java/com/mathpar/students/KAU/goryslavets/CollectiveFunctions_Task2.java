package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/*

Task:

2. Напишiть програму для збору масиву чисел з усiх процесорiв
на процесорi номер 1. Протестуйте програму на 4, 8, 12
процесорах.

*/

/*

Run commands:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 4 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task2
$ mpirun --hostfile /home/dmytro/dap/hostfile -np 8 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task2
$ mpirun --hostfile /home/dmytro/dap/hostfile -np 12 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task2

*/

public class CollectiveFunctions_Task2 {
    public static void main(String[] args) throws MPIException {

        MPI.Init(args);

        int myRank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();

        int n = 5;
        int[] a = new int[n];

        for (int i = 0; i < n; i++) a[i] = myRank * 10 + i;

        System.out.println("myRank = " + myRank + " : a = " + Arrays.toString(a));

        int[] q = new int[n * np]; // array to collect elements in

        // collecting elements on core 1
        MPI.COMM_WORLD.gather(a, n, MPI.INT, q, n, MPI.INT, 1);

        if (myRank == 1) System.out.println("myRank = " + myRank + " : q = " + Arrays.toString(q));

        MPI.Finalize();

    }
}

/*

Output for 4 cores:

myRank = 3 : a = [30, 31, 32, 33, 34]
myRank = 0 : a = [0, 1, 2, 3, 4]
myRank = 2 : a = [20, 21, 22, 23, 24]
myRank = 1 : a = [10, 11, 12, 13, 14]
myRank = 1 : q = [0, 1, 2, 3, 4, 10, 11, 12, 13, 14, 20, 21, 22, 23, 24, 30, 31, 32, 33, 34]


Output for 8 cores:

myRank = 6 : a = [60, 61, 62, 63, 64]
myRank = 0 : a = [0, 1, 2, 3, 4]
myRank = 1 : a = [10, 11, 12, 13, 14]
myRank = 7 : a = [70, 71, 72, 73, 74]
myRank = 2 : a = [20, 21, 22, 23, 24]
myRank = 5 : a = [50, 51, 52, 53, 54]
myRank = 4 : a = [40, 41, 42, 43, 44]
myRank = 3 : a = [30, 31, 32, 33, 34]
myRank = 1 : q = [0, 1, 2, 3, 4, 10, 11, 12, 13, 14, 20, 21, 22, 23, 24, 30, 31, 32, 33, 34, 40, 41, 42, 43, 44, 50, 51, 52, 53, 54, 60, 61, 62, 63, 64, 70, 71, 72, 73, 74]

Output for 12 cores:

myRank = 2 : a = [20, 21, 22, 23, 24]
myRank = 8 : a = [80, 81, 82, 83, 84]
myRank = 1 : a = [10, 11, 12, 13, 14]
myRank = 3 : a = [30, 31, 32, 33, 34]
myRank = 0 : a = [0, 1, 2, 3, 4]
myRank = 11 : a = [110, 111, 112, 113, 114]
myRank = 7 : a = [70, 71, 72, 73, 74]
myRank = 6 : a = [60, 61, 62, 63, 64]
myRank = 5 : a = [50, 51, 52, 53, 54]
myRank = 10 : a = [100, 101, 102, 103, 104]
myRank = 4 : a = [40, 41, 42, 43, 44]
myRank = 9 : a = [90, 91, 92, 93, 94]
myRank = 1 : q = [0, 1, 2, 3, 4, 10, 11, 12, 13, 14, 20, 21, 22, 23, 24, 30, 31, 32, 33, 34, 40, 41, 42, 43, 44, 50, 51, 52, 53, 54, 60, 61, 62, 63, 64, 70, 71, 72, 73, 74, 80, 81, 82, 83, 84, 90, 91, 92, 93, 94, 100, 101, 102, 103, 104, 110, 111, 112, 113, 114]


*/