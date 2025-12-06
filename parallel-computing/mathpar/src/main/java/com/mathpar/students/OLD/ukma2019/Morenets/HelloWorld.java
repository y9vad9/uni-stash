package com.mathpar.students.OLD.ukma2019.Morenets;

import mpi.*;
//mpirun -np 2 java -cp /home/teacher/stemedu/target/classes com.mathpar.students.ukma.Morenets/HelloWorld
public class HelloWorld{
public static void main(String[] args)
throws MPIException { 
MPI.Init(args);
int myrank = MPI.COMM_WORLD.getRank();
System.out.println("Proc num " + myrank + " Hello World");
 
MPI.Finalize();
}
}