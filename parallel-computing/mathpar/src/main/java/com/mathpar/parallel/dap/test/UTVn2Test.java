package com.mathpar.parallel.dap.test;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.SeqUTV;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Accumulator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DataInitializer;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RunnableProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.setup.ProcessorsSetup;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.setup.SquareMPISetup;
import com.mathpar.parallel.dap.core.DispThread;
import mpi.MPIException;
import org.javatuples.Pair;

//mpirun -np 4 java -cp /Users/apple/openmpi/lib/mpi.jar:target/qr-test.jar -Xmx4g com.mathpar.parallel.dap.test.UTVn2Test -size=16 -density=100 -nocheck > debug.log

public class UTVn2Test extends DAPTest {
    private UTVn2Test() {
        super("utv_n2", 0, 0);
    }

    protected UTVn2Test(String reportFile){
        super(reportFile, 0, 0);
    }

    @Override
    protected Element[] execute(DispThread dispThread, int taskType, int key, String[] args, Element[] data, Ring ring) {
        ProcessorsSetup setup = getSetup(poolSize, ring);
        RunnableProcessor processor = setup.getProcessor(rank);
        if(rank == root){
            ((DataInitializer) processor).setInitData((MatrixS) data[0]);
        }

        processor.run();

        if(rank == root){
            return ((Accumulator) processor).getResult();
        }

        return new Element[0];
    }

    protected ProcessorsSetup getSetup(int processors, Ring ring){
        return new SquareMPISetup(poolSize, ring);
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        MatrixS input = (MatrixS) data[0];
        return SeqUTV.compute(input, ring);
    }

    @Override
    protected MatrixS[] initData(int size, double density, int maxBits, Ring ring) {
        return new MatrixS[]{matrix(size, density, maxBits, ring)};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        MatrixS A = (MatrixS) initData[0];
        MatrixS U = (MatrixS) resultData[0];
        MatrixS T = (MatrixS) resultData[1];
        MatrixS V = (MatrixS) resultData[2];

        Element[] check1 = runTask(dispThread, 0, 0, args, new Element[]{T, V.transpose()}, ring);
        MatrixS TV = (MatrixS) check1[0];
        Element[] check2 = runTask(dispThread, 0, 0, args, new Element[]{U.transpose(), TV}, ring);
        MatrixS resultCheck = (MatrixS) check2[0];
        MatrixS diff = A.subtract(resultCheck, ring);


        return new Pair<>(diff.isZero(ring), diff.max(ring));
    }

    @Override
    protected int dispRunsOnOtherProc() {
        return 2;
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        DAPTest test = new UTVn2Test();

        test.runTests(args);
    }
}
