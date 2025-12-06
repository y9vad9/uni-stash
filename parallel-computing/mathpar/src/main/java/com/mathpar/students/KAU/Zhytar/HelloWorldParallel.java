package com.mathpar.students.KAU.Zhytar;

import mpi.MPI;
import mpi.MPIException;

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
    Command to run:
    mpirun  --hostfile hostfile  -np 7 java  -cp ./target/classes com/mathpar/students/KAU/Zhytar/HelloWorldParallel
 */

/*
    Result:
    Proc num 0 Hello World
    Proc num 1 Hello World
    Proc num 2 Hello World
    Proc num 3 Hello World
    Proc num 4 Hello World
    Proc num 6 Hello World
    Proc num 5 Hello World
 */
