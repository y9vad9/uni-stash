package com.mathpar.students.KAU.kryvokhyzha;

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
command:
mpirun --hostfile hostfile java -cp /home/kryvokhyzha/IdeaProjects/DAP/target/classes com/mathpar/NAUKMA/examples/HelloWorldParallel

result:
Proc num 0 Hello World
 */
