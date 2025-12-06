package com.mathpar.students.OLD.stud2017.sidko;
import java.util.Arrays;
import mpi.*;
public class ScattervTest {
        public static void main(String[] args)
                throws MPIException {
            MPI.Init(args);
            int myrank = MPI.COMM_WORLD.getRank();
            int n = Integer.parseInt(args[0]);
            int np = MPI.COMM_WORLD.getSize();
            int[] a = new int[n*np];
            if(myrank == 0){
                for (int i = 0; i < a.length; i++) a[i] = i;
                System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
            }
            int[] q = new int[n];
            MPI.COMM_WORLD.barrier();
            MPI.COMM_WORLD.scatterv(a, new int[]{n, n},
                    new int[]{0, 4}, MPI.INT, q, n, MPI.INT, 0);
            System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
            MPI.Finalize();
        }
    }
