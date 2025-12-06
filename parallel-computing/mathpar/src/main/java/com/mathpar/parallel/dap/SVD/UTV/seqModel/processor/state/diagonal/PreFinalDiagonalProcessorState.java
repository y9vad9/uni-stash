package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DiagonalProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.FreeProcessorState;


public class PreFinalDiagonalProcessorState extends ProcessorState<DiagonalProcessor> {
    private int section = 0;

    @Override
    public void action(DiagonalProcessor processor) {

        if(section == 0){
            if(processor.receive3DiagonalElementFromDownNeighbor()){
                section++;
            }
        }

        if(section == 1){
            if(processor.receive3DiagonalElementFromRightNeighbor()){
                processor.send3DiagonalMatrixToAccumulator();
                processor.nextIteration();
                section++;
            }
        }
    }

    @Override
    protected void finalAction(DiagonalProcessor processor) {
        processor.sendToAccumulator();
    }

    @Override
    protected boolean isReadyForTransition(DiagonalProcessor processor) {
        return  processor.getLastIterationAsActive() + 1 == processor.getIteration();
    }

    @Override
    protected ProcessorState<?> nextState(DiagonalProcessor processor) {
        if(processor.hasAccumulator()){
            return new DiagonalAccumulationProcessorState();
        }else{
            return new FreeProcessorState();
        }
    }


}
