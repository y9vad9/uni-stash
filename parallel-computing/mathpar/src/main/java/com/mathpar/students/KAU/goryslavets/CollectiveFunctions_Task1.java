package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/*

Task:

1. Напишiть програму для пересилання масиву чисел з процесора
номер 2 iншим процесорам групи. Протестуйте програму на 4,
8, 12 процесорах.

*/

/*

Run commands:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 4 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task1
$ mpirun --hostfile /home/dmytro/dap/hostfile -np 8 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task1
$ mpirun --hostfile /home/dmytro/dap/hostfile -np 12 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task1

*/


public class CollectiveFunctions_Task1 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myRank = MPI.COMM_WORLD.getRank();

        int n = 5;
        int[] a = new int[n];

        if (myRank == 2) {
            for (int i = 0; i < n; i++) {
                a[i] = myRank * 10 + i;
            }

            System.out.println("myRank = " + myRank + " : a = " + Arrays.toString(a));
        }

        MPI.COMM_WORLD.bcast(a, a.length, MPI.INT, 2);

        if (myRank != 2) System.out.println("myRank = " + myRank + " : a = " + Arrays.toString(a));

        MPI.Finalize();
    }
}

/*

Output for 4 cores:

myRank = 2 : a = [20, 21, 22, 23, 24]
myRank = 1 : a = [20, 21, 22, 23, 24]
myRank = 3 : a = [20, 21, 22, 23, 24]
myRank = 0 : a = [20, 21, 22, 23, 24]

Output for 8 cores:

myRank = 2 : a = [20, 21, 22, 23, 24]
myRank = 4 : a = [20, 21, 22, 23, 24]
myRank = 6 : a = [20, 21, 22, 23, 24]
myRank = 0 : a = [20, 21, 22, 23, 24]
myRank = 3 : a = [20, 21, 22, 23, 24]
myRank = 5 : a = [20, 21, 22, 23, 24]
myRank = 7 : a = [20, 21, 22, 23, 24]
myRank = 1 : a = [20, 21, 22, 23, 24]

Output for 12 cores:

myRank = 2 : a = [20, 21, 22, 23, 24]
myRank = 3 : a = [20, 21, 22, 23, 24]
myRank = 4 : a = [20, 21, 22, 23, 24]
myRank = 6 : a = [20, 21, 22, 23, 24]
myRank = 7 : a = [20, 21, 22, 23, 24]
myRank = 8 : a = [20, 21, 22, 23, 24]
myRank = 11 : a = [20, 21, 22, 23, 24]
myRank = 5 : a = [20, 21, 22, 23, 24]
myRank = 10 : a = [20, 21, 22, 23, 24]
myRank = 1 : a = [20, 21, 22, 23, 24]
myRank = 9 : a = [20, 21, 22, 23, 24]
myRank = 0 : a = [20, 21, 22, 23, 24]

*/
