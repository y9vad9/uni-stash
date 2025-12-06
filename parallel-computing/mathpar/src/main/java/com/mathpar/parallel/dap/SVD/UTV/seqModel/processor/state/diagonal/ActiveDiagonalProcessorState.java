package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DiagonalProcessor;

public class ActiveDiagonalProcessorState extends ProcessorState<DiagonalProcessor> {
    protected int section;

    @Override
    protected ProcessorState<?> nextState(DiagonalProcessor processor) {
        return new PreFinalDiagonalProcessorState();
    }


    @Override
    public void action(DiagonalProcessor processor) {

        if(section == 0){
            if(processor.receiveRowFromDownNeighbor()){
                section++;
            }
        }
        if(section == 1){
            if(processor.receiveColumnFromRightNeighbor()){
                section++;
            }
        }
        if(section == 2){
            if(processor.receiveElementFromNextDiagonalNeighbor()){
                processor.eliminate();
                clearSection();
            }
        }


    }


    @Override
    protected boolean isReadyForTransition(DiagonalProcessor processor) {
        return processor.getLastIterationAsActive() == processor.getIteration();
    }


    private void clearSection(){
        section = 0;
    }

}
