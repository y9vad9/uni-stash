package com.mathpar.students.KAU.kryvokhyzha;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestScatterv {
    public static void main(String[] args)
            throws MPIException {

        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        int n = 4;
        // визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        // оголошуємо масив цілих чисел
        int[] a = new int[n*np];
        if(myrank == 0){
            for (int i = 0; i < a.length; i++)
                a[i] = myrank*10+i;
            System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        }
        // оголошуємо масив, в який будуть записуватися
        // прийняті процесором елементи
        int[] q = new int[n];
        MPI.COMM_WORLD.scatterv(a, new int[]{n, n, n, n},
                new int[]{0, 8, 4, 12}, MPI.INT, q, n, MPI.INT, 0);
        // роздруковуємо отримані масиви і номера процесорів
        System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
        // завершення паралельної частини
        MPI.Finalize();
    }
}

/*
command:
mpirun --hostfile hostfile java -cp /home/kryvokhyzha/IdeaProjects/DAP/target/classes com/mathpar/NAUKMA/examples/TestScatterv

result:
myrank = 0: a = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
myrank = 0: q = [0, 1, 2, 3]
myrank = 1: q = [8, 9, 10, 11]
myrank = 3: q = [12, 13, 14, 15]
myrank = 2: q = [4, 5, 6, 7]
 */
