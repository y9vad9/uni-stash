package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/*

Run command:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 2 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/TestScatter

 */

public class TestScatter {
    public static void main(String[] args) throws MPIException{
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        // визначенння числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = 6;
        // оголошуємо масив об'єктів
        int[] a = new int[n];
        // заповнюємо цей масив на нульовому процесорі
        if (myrank == 0){
            for (int i = 0; i < n; i++){
                a[i] = i ;
            }
            System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        }
        // оголошуємо масив, в який будуть записуватись
        // прийняті процесором елементи
        int[] q = new int[n/2];
        MPI.COMM_WORLD.scatter(a, 3, MPI.INT, q, n/2, MPI.INT, 0);
        // роздруковуємо отримані масиви і номера процесорів
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        // завершення паралельної частини
        MPI.Finalize();
    }
}

/*

Result:

myrank = 0: a = [0, 1, 2, 3, 4, 5]
myrank = 0: q = [0, 1, 2]
myrank = 1: q = [3, 4, 5]

 */