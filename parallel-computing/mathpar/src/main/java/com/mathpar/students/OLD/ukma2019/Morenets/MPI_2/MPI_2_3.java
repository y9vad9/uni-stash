package com.mathpar.students.OLD.ukma2019.Morenets.MPI_2;

import java.nio.IntBuffer;
import java.util.Random;
import mpi.*;

public class MPI_2_3 {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        Intracomm WORLD = MPI.COMM_WORLD;

        int myRank = WORLD.getRank();

        int elemNum = Integer.parseInt(args[0]);
        IntBuffer buffer = MPI.newIntBuffer(elemNum);

        WORLD.barrier();

        int tag = 3000;

        if (myRank == 0) {
            for (int i = 0; i < elemNum; i++)
                buffer.put(new Random().nextInt(10));

            int procNum = WORLD.getSize();
            for (int i = 1; i < procNum; i++)
                WORLD.iSend(buffer, buffer.capacity(), MPI.INT, i, tag);

            System.out.println("proc num = " + myRank + " Array sent.");
        } else {
            WORLD.recv(buffer, buffer.capacity(), MPI.INT, 0, tag);

            System.out.println("proc num = " + myRank + " Array received.");
        }

        MPI.Finalize();
    }
}

/*
Command: mpirun -np 3 java -cp out/production/MPI_2_3 MPI_2_3 5

Output:
proc num = 0 Array sent.
proc num = 1 Array received.
proc num = 2 Array received.
*/
