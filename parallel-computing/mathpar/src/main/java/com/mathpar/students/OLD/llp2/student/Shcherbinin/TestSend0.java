package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import java.util.Arrays;
import mpi.*;
//mpirun C java -cp /home/scherbinin/NetBeansProjects/mpi/build/classes:$CLASSPATH mpitest.TestSend0:CLASSPATH

public class TestSend0 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int np =  MPI.COMM_WORLD.getSize();
        Object[] b = new Object[2];

        if (myrank == 0) {
            Object[] a = new Object[8];
            for (int i = 0; i < a.length; i++) {
                a[i] = (int) 100 * (Math.random());
            }
            for (int i = 0; i < np; i++) {
 //!!!!               MPI.COMM_WORLD.Isend(a, i * 2, 2,  MPI.OBJECT, i, 3000);
            }
            System.out.println("Массив" + Arrays.toString(a) + " на Proc num " + myrank + " отправлен");
        }
      MPI.COMM_WORLD.barrier();
   //!!!!               MPI.COMM_WORLD.Isend(a, i * 2, 2,  MPI.OBJECT, i, 3000);
   //!!!!     MPI.COMM_WORLD.Recv(b, 0, b.length, MPI.OBJECT, 0, 3000);
        System.out.println("Массив " + Arrays.toString(b) + "Proc num " + myrank + " принят");

         MPI.Finalize();
    }
}
