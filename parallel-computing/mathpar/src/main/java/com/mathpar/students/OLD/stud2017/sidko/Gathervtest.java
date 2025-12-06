package com.mathpar.students.OLD.stud2017.sidko;
import java.util.Arrays;
import mpi.MPI;
import mpi.MPIException;

public class Gathervtest {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        int n = Integer.parseInt(args[0]);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = myrank+10;
        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n * np];
        MPI.COMM_WORLD.gatherv(a, n, MPI.INT, q, new int[]{n, n}, new int[]{3, 0}, MPI.INT, np - 1);
        if(myrank == np-1)
            System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));

        MPI.Finalize();
        }
    }


