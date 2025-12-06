package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;
import mpi.MPIException;

/*

Run command:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 8 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/HelloWorldParallel

 */

public class HelloWorldParallel {
    public static void main(String[] args) throws MPIException {
        //iнiцiалiзацiя паралельної частини
        MPI.Init(args);
        //визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        System.out.println("Proc num " + myrank + " Hello World");
        //завершення паралельної частини
        MPI.Finalize();
    }
}

/*

Result:

Proc num 3 Hello World
Proc num 5 Hello World
Proc num 4 Hello World
Proc num 2 Hello World
Proc num 7 Hello World
Proc num 1 Hello World
Proc num 6 Hello World
Proc num 0 Hello World

 */
