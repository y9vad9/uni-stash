package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator.LeftMatrixAccumulator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator.MatrixAccumulator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

public class LeftAccumulationProcessor extends LeftProcessor {
    private MatrixAccumulator matrixAccumulator;

    public LeftAccumulationProcessor(int rank, int processorsInRowNumber, Transport transport, Ring ring) {
        super(rank, processorsInRowNumber, transport, ring);
    }

    @Override
    public void sendLeftMatrixToAccumulator() {
        matrixAccumulator = new LeftMatrixAccumulator(this);
        matrixAccumulator.setInitData(leftMatrixStorage.getResult());
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
