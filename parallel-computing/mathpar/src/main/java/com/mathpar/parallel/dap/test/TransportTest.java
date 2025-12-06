package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Transport;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;

// ./runclass.sh -np 2 com.mathpar.parallel.dap.test.TransportTest

public class TransportTest {
    private static MpiLogger LOGGER = MpiLogger.getLogger(TransportTest.class);
    private static int iterations = 100;
    private static Ring ring = new Ring("Z[]");
    private static int size = 512;
    static int[] density = {100, 200, 400, 800, 1600, 3200, 6400, 10000};
    private static Random random = new Random();


    public static void main(String[] args) throws MPIException, IOException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        double t1 = MPI.wtime();
        for (int i = 0; i < iterations; i++) {

            if (rank == 0) {
                Transport.sendObject(matrix(i), 1,MPI.COMM_WORLD ,  Transport.Tag.RESULT);

            }

            if (rank == 1) {

                Object result = Transport.recvObject(0,MPI.COMM_WORLD,  Transport.Tag.RESULT);

                if(result == null)
                    LOGGER.error("result is null, i="+i);
            }

            MPI.COMM_WORLD.barrier();
        }
        double t2 = MPI.wtime();

        if(rank == 0){
            LOGGER.info("time = " + (t2 - t1) + "s");
        }

        MPI.Finalize();
    }


    private static MatrixS matrix(int i){
        return new MatrixS(size, size, density[i%density.length], new int[]{5}, random, ring.numberONE(), ring);
    }

}