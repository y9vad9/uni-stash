package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DiagonalProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.FreeProcessorState;

public class ActiveLastDiagonalProcessorState extends ProcessorState<DiagonalProcessor> {
    @Override
    public void action(DiagonalProcessor processor) {
        processor.computeSequentialUTV();
        processor.sendToAccumulator();
        processor.send3DiagonalMatrixToAccumulator();
    }

    @Override
    protected boolean isReadyForTransition(DiagonalProcessor processor) {
        return true;
    }

    @Override
    protected ProcessorState<?> nextState(DiagonalProcessor processor) {
        return new FreeProcessorState();
    }
}
