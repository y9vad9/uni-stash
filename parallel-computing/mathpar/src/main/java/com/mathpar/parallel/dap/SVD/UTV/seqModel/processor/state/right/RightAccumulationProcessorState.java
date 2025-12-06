package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.right;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RightAccumulationProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RightProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.FreeProcessorState;

public class RightAccumulationProcessorState extends ProcessorState<RightAccumulationProcessor> {
    @Override
    public void action(RightAccumulationProcessor processor) {
        processor.computeAccumulatedMatrix();
    }

    @Override
    protected boolean isReadyForTransition(RightAccumulationProcessor processor) {
        return processor.isAccumulated();
    }

    @Override
    protected void finalAction(RightAccumulationProcessor processor) {
        processor.sendAccumulatedResults();
    }

    @Override
    protected ProcessorState<?> nextState(RightAccumulationProcessor processor) {
        return new FreeProcessorState();
    }
}
