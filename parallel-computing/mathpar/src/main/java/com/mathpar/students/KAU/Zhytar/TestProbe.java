package com.mathpar.students.KAU.Zhytar;


import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

public class TestProbe {
    public static void main(String[] args) throws MPIException, InterruptedException {
        MPI.Init(args);
        int n = 5;
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        if (rank == 0) {
            int[] array = new int[n];
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.send(array, n, MPI.INT, i, 1);
                System.out.println("rank = " + rank + " відправлено до " + i);
            }
        } else {
            Status st = null;
            while (st == null) {
                st = MPI.COMM_WORLD.probe(0, 1);
            }
            System.out.println("st.getCount(MPI.INT) = " + st.getCount(MPI.INT));
            System.out.println("st.getTag() = " + st.getTag());
            System.out.println("st.getSource() = " + st.getSource());
            int[] array = new int[n];
            MPI.COMM_WORLD.recv(array, n, MPI.INT, 0, 1);
            System.out.println("rank = " + rank + " отримано");
        } MPI.Finalize();
    }
}

/*
    Command to run:
    mpirun  --hostfile hostfile  -np 7 java  -cp ./target/classes com/mathpar/students/KAU/Zhytar/TestProbe
 */

/*
    Result:
    st.getCount(MPI.INT) = 5
    st.getTag() = 1
    st.getSource() = 0
    rank = 1 отримано
    rank = 0 відправлено до 1
    rank = 0 відправлено до 2
    rank = 0 відправлено до 3
    rank = 0 відправлено до 4
    rank = 0 відправлено до 5
    rank = 0 відправлено до 6
    st.getCount(MPI.INT) = 5
    st.getTag() = 1
    st.getSource() = 0
    st.getCount(MPI.INT) = 5
    st.getCount(MPI.INT) = 5st.getTag() = 1st.getCount(MPI.INT) = 5


    st.getSource() = 0
    st.getTag() = 1
    st.getTag() = 1
    st.getSource() = 0
    st.getSource() = 0
    st.getCount(MPI.INT) = 5
    st.getTag() = 1
    st.getSource() = 0
    rank = 2 отримано
    rank = 3 отримано
    rank = 4 отримано
    rank = 5 отримано
    rank = 6 отримано
 */
