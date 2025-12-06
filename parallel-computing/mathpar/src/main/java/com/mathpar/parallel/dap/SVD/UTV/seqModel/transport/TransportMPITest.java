package com.mathpar.parallel.dap.SVD.UTV.seqModel.transport;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;
import mpi.Comm;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;


// mpirun -np 4 java -cp /Users/apple/openmpi/lib/mpi.jar:target/qr-test.jar -Xmx4g com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.TransportMPITest
public class TransportMPITest {

    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        mpiScatterTest(args);
    }


    public static void mpiScatterTest(String[] args) throws MPIException, IOException, ClassNotFoundException {
        MPI.Init(args);

        final int size = Integer.parseInt(args[0]);
        final int root = Integer.parseInt(args[1]);

        Intracomm comm = MPI.COMM_WORLD;

        int rank = comm.getRank();
        int pool = comm.getSize();
        MatrixS[] matrices = null;

        System.out.println(String.format("rank[%d]/%d",rank, pool));

        if(rank == root){
            matrices = IntStream.range(0, pool).mapToObj(i -> matrix(size)).toArray(MatrixS[]::new);
        }

        MPITransport transport = new MPITransport();

        long t1 = System.currentTimeMillis();

        MatrixS result = (MatrixS) transport.scatterExtended(matrices,  root, comm);

        long t2 = System.currentTimeMillis();

        System.out.println(String.format("rank[%d] %s time=%d",rank, result != null, t2-t1));

        comm.barrier();
        MPI.Finalize();
    }


    public static MatrixS matrix(int size) {
        Ring ring = new Ring("R64[]");
        return new MatrixS(size, size, 8000, new int[]{5}, new Random(System.currentTimeMillis()), ring.numberONE(), ring);
    }
}
