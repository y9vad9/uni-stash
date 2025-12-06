package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVar;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVarConfig;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVarMatrix;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;

// ./runclass.sh -np 4 com.mathpar.parallel.dap.test.MultiplyVarTest

public class MultiplyVarTest {

    private final static MpiLogger LOGGER = MpiLogger.getLogger(MultiplyVarTest.class);
    private static final Ring ring = new Ring("Z[]");
    private static final int matrixSize = 64;

    public static void main(String[] args) throws MPIException, InterruptedException, IOException, ClassNotFoundException {
        MPI.Init(args);

        DispThread disp = new DispThread( 1, args,MPI.COMM_WORLD, ring);

        test1(args, disp);
        test2(args, disp);
        test3(args, disp);
        test4(args, disp);

        disp.counter.DoneThread();
        disp.counter.thread.join();
        MPI.Finalize();
    }

    /**
     * This test checks multiplication of small matrix (embedded in down right quarter) and bigger matrix
     */
    private static void test1(String[] args, DispThread dispThread) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        MatrixS smallMatrix = getSmallerMatrix();
        MatrixS bigMatrix = getBiggerMatrix();
        int rank = MPI.COMM_WORLD.getRank();
        Element[] data = new MatrixS[8];
        data[3] = smallMatrix;
        data[4] = bigMatrix;

        byte[] config = MultiplyVarConfig.Builder.startWith()
                .multiplicationBlock()
                .negateMultiplication()
                .setFirst(MultiplyVarMatrix.Builder.newBuilder().addQuarter(MultiplyVarMatrix.Quarter.IV).set().done().build())
                .setSecond(MultiplyVarMatrix.Builder.newBuilder().addFull().transpose().set().done().build())
                .build().getData();

        dispThread.execute(2, 0,config, args, data, ring);

        if(rank == 0){
            MatrixS result = (MatrixS) dispThread.getResult()[0];
            MatrixS ONE = MatrixS.scalarMatrix(smallMatrix.size, ring.numberONE(), ring);
            MatrixS ZERO = MatrixS.scalarMatrix(smallMatrix.size, ring.numberZERO(), ring);
            MatrixS A = MatrixS.join(new MatrixS[]{ONE, ZERO, ZERO, smallMatrix});
            MatrixS B = bigMatrix;

            MatrixS seqResult = A.multiply(B.transpose(), ring).negate(ring);

            boolean isPassed = result.subtract(seqResult, ring).isZero(ring);

            LOGGER.info("test 1 "+testResultLabel(isPassed));
        }
    }

    private static void test2(String[] args, DispThread dispThread) throws MPIException, InterruptedException, IOException, ClassNotFoundException {
        int smallSize = matrixSize/2;
        MatrixS centerMatrix = getSmallerMatrix();
        MatrixS bigMatrix = getBiggerMatrix();
        MatrixS q1Matrix = getSmallerMatrix();
        int rank = MPI.COMM_WORLD.getRank();
        Element[] data = new MatrixS[12];
        data[0] = centerMatrix;
        data[4] = bigMatrix;
        data[8] = q1Matrix;


        byte[] config = MultiplyVarConfig.Builder.startWith()
                .multiplicationBlock()
                .negateMultiplication()
                .setFirst(MultiplyVarMatrix.Builder.newBuilder().addCenter().transpose().set().done().build())
                .setSecond(MultiplyVarMatrix.Builder.newBuilder().addFull().transpose().set().done().build())
                .additionBlock()
                .negate()
                .set(MultiplyVarMatrix.Builder.newBuilder()
                .addQuarter(MultiplyVarMatrix.Quarter.I).set().done()
                .addQuarter(MultiplyVarMatrix.Quarter.III).transpose().copyOf(0).done().build())
                .build().getData();

        dispThread.execute(2,0, config, args, data, ring);

        if(rank == 0){
            MatrixS result = (MatrixS) dispThread.getResult()[0];

            MatrixS ZERO = MatrixS.scalarMatrix(smallSize, ring.numberZERO(), ring);

            MatrixS A = MatrixS.embedDiagonalCenter(centerMatrix, ring);
            MatrixS B = bigMatrix;
            MatrixS C = MatrixS.join(new MatrixS[]{q1Matrix, ZERO, centerMatrix.transpose(), ZERO});
            MatrixS seqResult = A.transpose().multiply(B.transpose(), ring).negate(ring);
            seqResult = seqResult.add(C.negate(ring), ring);

            boolean isPassed = result.subtract(seqResult, ring).isZero(ring);

            LOGGER.info("test 2 "+testResultLabel(isPassed));
        }
    }

    private static void test3(String[] args, DispThread dispThread) throws MPIException, InterruptedException, IOException, ClassNotFoundException {
        int smallSize = matrixSize/2;
        MatrixS centerMatrix = getSmallerMatrix();
        MatrixS bigMatrix = getBiggerMatrix();
        int rank = MPI.COMM_WORLD.getRank();
        Element[] data = new MatrixS[12];
        data[0] = centerMatrix;
        data[4] = bigMatrix;


        byte[] config = MultiplyVarConfig.Builder.startWith()
                .multiplicationBlock()
                .negateMultiplication()
                .setFirst(MultiplyVarMatrix.Builder.newBuilder().addCenter().transpose().set().done().build())
                .setSecond(MultiplyVarMatrix.Builder.newBuilder().addFull().transpose().set().done().build())
                .additionBlock()
                .set(MultiplyVarMatrix.Builder.newBuilder()
                        .addQuarter(MultiplyVarMatrix.Quarter.I).copyOf(0).done().build())
                .build().getData();

        dispThread.execute(2, 0, config, args, data, ring);

        if(rank == 0){
            MatrixS result = (MatrixS) dispThread.getResult()[0];

            MatrixS ONE = MatrixS.scalarMatrix(smallSize, ring.numberONE(), ring);
            MatrixS ZERO = MatrixS.scalarMatrix(smallSize, ring.numberZERO(), ring);

            MatrixS A = MatrixS.embedDiagonalCenter(centerMatrix, ring);
            MatrixS B = bigMatrix;
            MatrixS C = MatrixS.join(new MatrixS[]{centerMatrix, ZERO, ZERO, ONE});
            MatrixS seqResult = A.transpose().multiply(B.transpose(), ring).negate(ring);
            seqResult = seqResult.add(C, ring);


//            LOGGER.info("Result: "+result);
//            LOGGER.info("Check: "+seqResult);

            boolean isPassed = result.subtract(seqResult, ring).isZero(ring);

            LOGGER.info("test 3 "+testResultLabel(isPassed));
        }
    }

    private static void test4(String[] args, DispThread dispThread) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        MatrixS centerMatrix = getSmallerMatrix();
        MatrixS bigMatrix = getBiggerMatrix();
        int rank = MPI.COMM_WORLD.getRank();
        Element[] data = new MatrixS[12];
        data[0] = centerMatrix;
        data[4] = bigMatrix;


        byte[] config = MultiplyVarConfig.Builder.startWith()
                .multiplicationBlock()
                .negateMultiplication()
                .setFirst(MultiplyVarMatrix.Builder.newBuilder().addCenter().transpose().set().done().build())
                .setSecond(MultiplyVarMatrix.Builder.newBuilder().addFull().transpose().set().done().build())
                .additionBlock()
                .set(MultiplyVarMatrix.Builder.newBuilder()
                        .addQuarter(MultiplyVarMatrix.Quarter.I).copyOf(0).done().build())
                .build().getData();


        dispThread.execute(2, 0, config, args, data, ring);

        if(rank == 0){
            MatrixS result = (MatrixS) dispThread.getResult()[0];

            MultiplyVar drop = new MultiplyVar(new MultiplyVarConfig(config));
            drop._setInData(data);
            drop.sequentialCalc(ring);
            MatrixS seqResult = (MatrixS) drop._getOutputData()[0];

            boolean isPassed = result.subtract(seqResult, ring).isZero(ring);

            LOGGER.info("test 4 "+testResultLabel(isPassed));
        }
    }

    private static MatrixS getSmallerMatrix(){
        Random random = new Random();

        return new MatrixS(matrixSize/2, matrixSize/2, 5500, new int[]{5}, random, ring.numberONE(), ring);
    }

    private static MatrixS getBiggerMatrix(){
        Random random = new Random();
        return new MatrixS(matrixSize, matrixSize, 5000, new int[]{5}, random, ring.numberONE(), ring);
    }


    private static String testResultLabel(boolean isPassed){
        return isPassed ? "is passed" : "is failed";
    }
}
