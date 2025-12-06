package com.mathpar.students.KAU.kryvokhyzha;

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

            // Процедура Status.getCount визначає кiлькiсть уже отриманих елементiв повiдомлення типу type.
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
command:
mpirun --hostfile hostfile java -cp /home/kryvokhyzha/IdeaProjects/DAP/target/classes com/mathpar/NAUKMA/examples/TestProbe

result:
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
rank = 0 відправлено до 1
rank = 0 відправлено до 2
rank = 0 відправлено до 3
rank = 0 відправлено до 4
rank = 0 відправлено до 5
rank = 0 відправлено до 6
rank = 0 відправлено до 7
rank = 0 відправлено до 8
rank = 0 відправлено до 9
rank = 0 відправлено до 10
rank = 0 відправлено до 11
rank = 0 відправлено до 12
rank = 0 відправлено до 13
rank = 0 відправлено до 14
rank = 0 відправлено до 15
rank = 1 отримано
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
rank = 13 отримано
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
st.getCount(MPI.INT) = 5
st.getCount(MPI.INT) = 5
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getCount(MPI.INT) = 5
st.getTag() = 1st.getTag() = 1
st.getSource() = 0

st.getCount(MPI.INT) = 5st.getCount(MPI.INT) = 5
st.getCount(MPI.INT) = 5rank = 3 отримано
st.getTag() = 1rank = 8 отримано

st.getSource() = 0
st.getTag() = 1
st.getSource() = 0
st.getTag() = 1
st.getSource() = 0

st.getSource() = 0
st.getTag() = 1

st.getSource() = 0
rank = 10 отримано
st.getCount(MPI.INT) = 5
st.getTag() = 1
st.getSource() = 0
st.getSource() = 0
rank = 15 отриманоrank = 5 отримано
rank = 2 отримано

rank = 9 отримано
rank = 12 отримано
rank = 6 отриманоrank = 4 отримано
rank = 14 отримано
rank = 11 отримано

rank = 7 отримано

 */
