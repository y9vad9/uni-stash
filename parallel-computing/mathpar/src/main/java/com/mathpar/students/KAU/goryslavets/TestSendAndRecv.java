package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;
import mpi.MPIException;

import java.util.Random;

/*

Run command:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 4 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/TestSendAndRecv

 */

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

Result:

a[0]= 0.40831926188006074
a[1]= 0.04539527513159547
a[2]= 0.6372811118594954
a[3]= 0.560119611552241
a[4]= 0.8898127664009071
Proc num 0 масив вiдправлено

a[0]= 0.40831926188006074
a[1]= 0.04539527513159547
a[2]= 0.6372811118594954
a[3]= 0.560119611552241
a[4]= 0.8898127664009071
Proc num 3 масив прийнято

a[0]= 0.40831926188006074
a[1]= 0.04539527513159547
a[2]= 0.6372811118594954
a[3]= 0.560119611552241
a[4]= 0.8898127664009071
Proc num 2 масив прийнято

a[0]= 0.40831926188006074
a[1]= 0.04539527513159547
a[2]= 0.6372811118594954
a[3]= 0.560119611552241
a[4]= 0.8898127664009071
Proc num 1 масив прийнято

 */
