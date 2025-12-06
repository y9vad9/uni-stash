package com.mathpar.students.KAU.kryvokhyzha;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestGatherv {
    public static void main(String[] args) throws MPIException {
        // ініціалізація MPI
        MPI.Init(args);
        // визначення номера процесора
        int myrank = MPI.COMM_WORLD.getRank();
        // визначення числа процесорів в групі
        int np = MPI.COMM_WORLD.getSize();
        int n = 5;
        int[] a = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = myrank*10+i;
        }

        System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        int[] q = new int[n * np];

        MPI.COMM_WORLD.gatherv(a, n, MPI.INT, q,
                new int[]{n, n, n, n}, new int[]{0, 5, 10, 15},
                MPI.INT, np - 1);

        if (myrank == np - 1) {
            System.out.println("myrank = " + myrank + ": q = " + Arrays.toString(q));
        }
        // завершення паралельної частини
        MPI.Finalize();
    }
}

/*
comments:
1.
IN - recvcounts - массив целых чисел (по размеру группы), содержащий количества элементов, которые получены от каждого из процессов (используется только корневым процессом)
IN - displs - массив целых чисел (по размеру группы). Элемент i определяет смещение относительно recvbuf, в котором размещаются данные из процесса i (используется только корневым процессом)

2.
Выполнение MPI_GATHERV будет давать такой же результат, как если бы каждый процесс, включая корневой, послал бы корневому процессу сообщение
    MPI_Send(sendbuf, sendcount, sendtype, root, ...),
и корневой процесс выполнил бы n операций приема
    MPI_Recv(recvbuf + displs[i] * extern(recvtype), recvcounts[i], recvtype, i, ...).
*/

/*
command:
mpirun --hostfile hostfile java -cp /home/kryvokhyzha/IdeaProjects/DAP/target/classes com/mathpar/NAUKMA/examples/TestGatherv

result:
myrank = 0: a = [0, 1, 2, 3, 4]
myrank = 1: a = [10, 11, 12, 13, 14]
myrank = 3: a = [30, 31, 32, 33, 34]
myrank = 2: a = [20, 21, 22, 23, 24]
myrank = 3: q = [0, 1, 2, 3, 4, 10, 11, 12, 13, 14, 20, 21, 22, 23, 24, 30, 31, 32, 33, 34]
 */
