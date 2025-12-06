package com.mathpar.students.KAU.Tolstikov;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;

public class TestSendAndRecv {
    public static void main(String[] args) throws MPIException {
        //iнiцiалiзацiя MPI
        MPI.Init(args);
        //визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        //визначення кiлькостi процесорiв у групi
        int np = MPI.COMM_WORLD.getSize();
        //вхiдний параметр - розмiр масиву
        int n = 5;
        double[] a = new double[n];
        //синхронiзацiя процесорiв
        // MPI.COMM_WORLD.barrier();
        // якщо процесор з номером 0
        if (myrank == 0) {
            for (int i = 0; i < n; i++) {
                a[i] = (new Random()).nextDouble();
                System.out.println("a[" + i + "]= " + a[i]);
            }
            //передання 0-процесором елементiв усiм iншим процесорам у групi
            for (int i = 1; i < np; i++) {
                MPI.COMM_WORLD.send(a, n, MPI.DOUBLE, i, 3000);
            }
            System.out.println("Proc num " + myrank + " масив вiдправлено" + "\n");
        } else {
            //приймання i-м процесором повiдомлення вiд
            // процесора з номером 0 та тегом 3000.
            MPI.COMM_WORLD.recv(a, n, MPI.DOUBLE, 0, 3000);
            for (int i = 0; i < n; i++) {
                System.out.println("a[" + i + "]= " + a[i]);
            }
            System.out.println("Proc num " + myrank + " масив прийнято" + "\n");
        }

        // завершення паралельної частини
        MPI.Finalize();
    }
}

/*
    Command to run:
    mpirun --hostfile /home/user/hostfile -np 7 java -cp /home/vladislav/dap/target/classes com/mathpar/students/KAU/Tolstikov/TestSendAndRecv
*/

/*
    Result:

    a[0]= 0.35902184994804
    a[1]= 0.4736770720412794
    a[2]= 0.9085516463343913
    a[3]= 0.21730083021577629
    a[4]= 0.5789679239133484
    Proc num 0 масив вiдправлено

    a[0]= 0.35902184994804
    a[1]= 0.4736770720412794
    a[0]= 0.35902184994804
    a[1]= 0.4736770720412794
    a[2]= 0.9085516463343913
    a[3]= 0.21730083021577629
    a[4]= 0.5789679239133484
    a[2]= 0.9085516463343913Proc num 3 масив прийнято


    a[3]= 0.21730083021577629
    a[0]= 0.35902184994804
    a[1]= 0.4736770720412794a[0]= 0.35902184994804
    a[1]= 0.4736770720412794
    a[2]= 0.9085516463343913
    a[3]= 0.21730083021577629
    a[4]= 0.5789679239133484
    Proc num 2 масив прийнято

    a[0]= 0.35902184994804
    a[1]= 0.4736770720412794
    a[2]= 0.9085516463343913
    a[3]= 0.21730083021577629
    a[4]= 0.5789679239133484
    Proc num 6 масив прийнято


    a[2]= 0.9085516463343913
    a[3]= 0.21730083021577629
    a[4]= 0.5789679239133484
    Proc num 5 масив прийнято

    a[0]= 0.35902184994804
    a[1]= 0.4736770720412794
    a[2]= 0.9085516463343913
    a[3]= 0.21730083021577629
    a[4]= 0.5789679239133484
    Proc num 1 масив прийнято

    a[4]= 0.5789679239133484
    Proc num 4 масив прийнято
*/
