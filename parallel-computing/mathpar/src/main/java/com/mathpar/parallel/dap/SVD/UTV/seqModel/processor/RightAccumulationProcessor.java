package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator.MatrixAccumulator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator.RightMatrixAccumulator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

public class RightAccumulationProcessor extends RightProcessor{
    private MatrixAccumulator matrixAccumulator;

    public RightAccumulationProcessor(int rank, int processorsInRowNumber, Transport transport, Ring ring) {
        super(rank, processorsInRowNumber, transport, ring);
    }

    @Override
    public void sendRightMatrixToAccumulator() {
        matrixAccumulator = new RightMatrixAccumulator(this);
        matrixAccumulator.setInitData(rightMatrixStorage.getResult());
    }


    @Override
    public boolean hasAccumulator() {
        return true;
    }

    public boolean computeAccumulatedMatrix(){
        return matrixAccumulator.accumulate();
    }

    public boolean isAccumulated(){
        return matrixAccumulator.isAccumulated();
    }

    public void sendAccumulatedResults(){
        matrixAccumulator.sendResultToRootProcessor();
    }
}
