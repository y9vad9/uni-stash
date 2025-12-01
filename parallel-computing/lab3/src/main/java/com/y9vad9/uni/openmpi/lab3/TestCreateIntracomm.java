package com.y9vad9.uni.openmpi.lab3;

import mpi.Group;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;

import java.util.ArrayList;
import java.util.Arrays;

public class TestCreateIntracomm {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();

        ArrayList<Integer> s = new ArrayList<>();
        s.add(0);
        s.add(1);
        s.add(2);

        ArrayList<Integer> s1 = new ArrayList<>();
        s1.add(3);
        s1.add(4);

        Group g = MPI.COMM_WORLD.getGroup().incl(new int[]{0, 1, 2});
        Group g2 = MPI.COMM_WORLD.getGroup().incl(new int[]{3, 4});

        Intracomm COMM_NEW = MPI.COMM_WORLD.create(g);
        Intracomm COMM_NEW_1 = MPI.COMM_WORLD.create(g2);

        int n = 5;

        int[] a = new int[n];
        if (rank == 0 || rank == 3) {
            for (int i = 0; i < n; i++) {
                a[i] = rank * 10 + i;
            }
            System.out.println("rank = " + rank +
                ": a = " + Arrays.toString(a));
        }
        if (s.contains(rank))
            COMM_NEW.bcast(a, a.length, MPI.INT, 0);
        if (s1.contains(rank))
            COMM_NEW_1.bcast(a, a.length, MPI.INT, 0);

        if (rank != 0 && rank != 3)
            System.out.println("rank = " + rank + ": a = " +
                Arrays.toString(a));

        MPI.Finalize();
    }
}
