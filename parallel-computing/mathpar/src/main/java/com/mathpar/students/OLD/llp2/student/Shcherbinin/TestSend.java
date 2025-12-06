package com.mathpar.students.OLD.llp2.student.Shcherbinin;
import mpi.*;
import java.math.BigInteger;
import java.util.Random;
//mpirun C java -cp /home/scherbinin/NetBeansProjects/mpi_Test/build/classes mpi_test.TestSend 100
public class TestSend {
    	public static void main(String[ ] args) throws MPIException {
		 MPI.Init(args);
		int myrank =  MPI.COMM_WORLD.getRank();
		int np =  MPI.COMM_WORLD.getSize();
                int n = Integer.parseInt("8");
		Object[] a = new Object[n];
		for (int i = 0; i < n; i++)
		a[i] = new BigInteger(10000, new Random());
		 MPI.COMM_WORLD.barrier();
		if (myrank == 0) {
			for (int i = 1; i < np; i++){
//!!!!                                MPI.COMM_WORLD.Isend(a, 0, a.length, MPI.OBJECT, i, 3000);
			}
                        System.out.println("Proc num " +myrank + " отправлен");
		}
		else {
			//!!!! MPI.COMM_WORLD.Recv(a, 0, a.length,MPI.OBJECT, 0, 3000);
			System.out.println("Proc num " +myrank + " принят");
		}
		MPI.Finalize();
	}
}
