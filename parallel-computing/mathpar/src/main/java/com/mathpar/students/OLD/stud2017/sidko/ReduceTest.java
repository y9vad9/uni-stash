package com.mathpar.students.OLD.stud2017.sidko;
import java.util.Arrays;
import mpi.MPI;
import mpi.MPIException;
public class ReduceTest {
        public static void main(String[] args)
                throws MPIException {
            MPI.Init(args);
            int myrank = MPI.COMM_WORLD.getRank();
            int n = Integer.parseInt(args[0]);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = myrank;
            System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
            int[] q = new int[n];
            MPI.COMM_WORLD.reduce(a, q, n, MPI.INT, MPI.PROD, 0);
            if (myrank == 0)
                System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
            MPI.Finalize();
        }}

