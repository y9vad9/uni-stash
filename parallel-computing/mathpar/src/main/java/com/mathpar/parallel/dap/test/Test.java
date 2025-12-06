package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;

public class Test {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(Test.class);

    public static void main(String[] args) throws MPIException, InterruptedException, IOException, ClassNotFoundException {


        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();

        int[] density = {100, 200, 400, 800, 1600, 3200, 6400, 10000};

        int n = 64;
        int task = 0;
        int numberOfMatrixes = 1;
        Ring ring = new Ring("Z[]");

        MatrixS res2 = null;
        DispThread disp = new DispThread(1, args,MPI.COMM_WORLD, ring);

        for (int i = 0; i < numberOfMatrixes; i++) {
            MatrixS A = null;
            MatrixS B = null;
            if(rank == 0) {
                Random r = new Random();
                A = new MatrixS(n, n, 1000, new int[]{5}, r, NumberZ.ONE, ring);
                B = new MatrixS(n, n, 1000, new int[]{5}, r, NumberZ.ONE, ring);
            }

            Element[] init = new MatrixS[]{A, B};

            disp.execute(task, 0, args, init, ring);

            if (rank == 0) {
                res2 = (MatrixS) disp.getResult()[0];
                MatrixS res1 = A.multiply(B, ring);
                LOGGER.info("Test number " + i + " --- " + res1.subtract(res2, ring).isZero(ring));

            }
            LOGGER.info("Terminal is Empty = " + disp.isEmptyTerminal());
            LOGGER.info("used memory = " + disp.getUsedMemory());
        }

        disp.counter.DoneThread();
        disp.counter.thread.join();
        MPI.Finalize();
    }
}
