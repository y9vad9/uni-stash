package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;

public class FreeProcessorState extends ProcessorState<Processor> {


    @Override
    public void action(Processor processor) {

    }

    @Override
    protected boolean isReadyForTransition(Processor processor) {
        return false;
    }

    @Override
    protected ProcessorState<?> nextState(Processor processor) {
        return null;
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}
