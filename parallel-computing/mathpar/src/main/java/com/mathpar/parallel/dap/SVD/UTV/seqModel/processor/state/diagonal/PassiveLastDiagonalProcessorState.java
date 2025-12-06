package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DiagonalProcessor;

public class PassiveLastDiagonalProcessorState extends ProcessorState<DiagonalProcessor> {
    private int section;
    @Override
    public boolean isReadyForTransition(DiagonalProcessor processor) {
        return processor.getFirstIterationAsActive() == processor.getIteration();
    }

    @Override
    protected ProcessorState<?> nextState(DiagonalProcessor processor) {
        return new ActiveLastDiagonalProcessorState();
    }


    @Override
    public void action(DiagonalProcessor processor) {
        if(section==0) {
            if(processor.receiveLeftMatrix()){
                processor.multiplyLeftMatrix();
                section++;
            }
        }

        if(section==1) {
            if(processor.receiveRightMatrix()) {
                processor.multiplyRightMatrix();

                if(processor.getFirstIterationAsActive() - 1 == processor.getIteration()){
                    processor.nextIteration();
                    return;
                }

                processor.sendColumnToLeftNeighbor();
                processor.sendRowToUpNeighbor();
                processor.sendElementToPreviousDiagonal();
                section++;
            }
        }

        if(section==2){if(processor.receiveRowBackFromUpNeighbor()) section++;}
        if(section==3) {
            if(processor.receiveColumnBackFromLeftNeighbor()) {
                processor.nextIteration();
                section = 0;
            }
        }
    }

}
