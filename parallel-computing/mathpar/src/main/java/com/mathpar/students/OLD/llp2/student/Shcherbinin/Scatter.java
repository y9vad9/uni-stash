/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;
import mpi.*;

/**
 *
 * @author ridkeim
 */
public class Scatter {

public static void main(String[ ] args) throws MPIException {
MPI.Init(args);
int myrank = MPI.COMM_WORLD.getRank();
int np =  MPI.COMM_WORLD.getSize();
int n = 5;
Object[] a = new Object[n];
if(myrank==0){
for (int i = 0; i < n; i++)
a[i]=i;
System.out.println("a="+ a[0] + a[1] + a[2] + ";r=" + myrank);
}
Object[] q = new Object[n];
 MPI.COMM_WORLD.barrier();
 //!!!!//!!!! MPI.COMM_WORLD.Scatterv(a, 0, new int[]{3,2,1,1},
//!!!! new int[]{0,1,2,0},PI.OBJECT, q, 0, q.length,MPI.OBJECT, 0);
for (int i=0; i<q.length; i=i+1)
    System.out.print("r=" + myrank + "; "+q[i] + "\n");
 MPI.Finalize();

}

}
