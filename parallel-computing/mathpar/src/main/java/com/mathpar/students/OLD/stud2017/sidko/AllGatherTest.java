package com.mathpar.students.OLD.stud2017.sidko;
import java.util.Arrays;
import mpi.*;
public class AllGatherTest {
        public static void main(String[] args)throws Exception {
            MPI.Init(args);
            Thread t = new Thread();
            int myrank = MPI.COMM_WORLD.getRank();
            int np = MPI.COMM_WORLD.getSize();
            int n = Integer.parseInt(args[0]);
            int[] a = new int[n];
            for(int i = 0; i < n; i++) a[i] = myrank;
            System.out.println("myrank = " + myrank + " : a = "
                    + Arrays.toString(a));
            int[] q = new int[n * np];
            MPI.COMM_WORLD.allGather(a, n, MPI.INT, q, n, MPI.INT);
            t.sleep(60 * myrank);

            System.out.println("myrank = " + myrank + " : q = "
                    + Arrays.toString(q));
            MPI.Finalize();
        }
    }


