package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;

public class InitialProcessorState extends ProcessorState<Processor> {

    @Override
    public ProcessorState<?> nextState(Processor processor) {
        return ProcessorState.initialized(processor);
    }

    @Override
    public void action(Processor processor) {
        processor.receiveInitialData();
    }

    @Override
    protected boolean isReadyForTransition(Processor processor) {

        return processor.getData() != null;
    }


}
