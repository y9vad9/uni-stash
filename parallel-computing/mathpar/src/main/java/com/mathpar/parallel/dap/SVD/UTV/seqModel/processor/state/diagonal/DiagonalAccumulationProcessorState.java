package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DiagonalAccumulationProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.FreeProcessorState;

import java.util.Arrays;

public class DiagonalAccumulationProcessorState extends ProcessorState<DiagonalAccumulationProcessor> {
    private boolean accumulatedMain = false;
    private boolean accumulatedLeft = false;
    private boolean accumulatedRight = false;

    @Override
    public void action(DiagonalAccumulationProcessor processor) {

        if (!accumulatedMain)
            accumulatedMain = processor.collectMain();

        if (!accumulatedLeft)
            accumulatedLeft = processor.collectLeft();

        if (!accumulatedRight)
            accumulatedRight = processor.collectRight();
    }

    @Override
    protected boolean isReadyForTransition(DiagonalAccumulationProcessor processor) {
        return accumulatedMain && accumulatedLeft && accumulatedRight;
    }

    @Override
    protected ProcessorState<?> nextState(DiagonalAccumulationProcessor processor) {
        return new FreeProcessorState();
    }

}
