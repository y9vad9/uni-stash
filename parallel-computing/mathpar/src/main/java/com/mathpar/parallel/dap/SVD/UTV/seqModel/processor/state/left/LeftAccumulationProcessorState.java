package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.left;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.LeftAccumulationProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.FreeProcessorState;

public class LeftAccumulationProcessorState extends ProcessorState<LeftAccumulationProcessor> {

    @Override
    public void action(LeftAccumulationProcessor processor) {
        processor.computeAccumulatedMatrix();
    }


    @Override
    protected void finalAction(LeftAccumulationProcessor processor) {
        processor.sendAccumulatedResults();
    }

    @Override
    protected boolean isReadyForTransition(LeftAccumulationProcessor processor) {
        return processor.isAccumulated();
    }

    @Override
    protected ProcessorState<?> nextState(LeftAccumulationProcessor processor) {
        return new FreeProcessorState();
    }
}
