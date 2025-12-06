package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.right;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RightProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.FreeProcessorState;

public class ActiveRightProcessorState extends ProcessorState<RightProcessor> {
    private int section;
    @Override
    public void action(RightProcessor processor) {
        Coordinator coordinator = processor.getCoordinator();
        if(!coordinator.isLastInLine()){
            if(section == 0){
                if(processor.receiveColumnFromRightNeighbor()){
                    processor.eliminateElementInRow();
                    processor.propagateRight2x2MatrixToDownNeighbors();

                    if(processor.getLastIterationAsActive() != processor.getIteration())
                        processor.returnColumnToRightNeighbor();
//                    processor.sendRight2x2MatrixToAccumulator();
                    section++;
                }
            }
        } else if(section == 0) section++;

        if(section == 1){
            processor.eliminateRow();
//            processor.sendColumnToLeftNeighbor();
            processor.propagateRightMatrixToDownNeighbors();
//            processor.sendToAccumulator();


            if(processor.getLastIterationAsActive() == processor.getIteration()){

                if(coordinator.isNextToDiagonal()){
                    processor.sendElementToDiagonal();
                }else{
                    processor.sendColumnToLeftNeighbor();
                }

                processor.nextIteration();
                section = 0;
                return;
            }
            // reordered to send one element instead element array
            processor.sendColumnToLeftNeighbor();
            section++;
        }

        // reorder when is near diagonal
        if(coordinator.isNextToDiagonal()){

            if(section == 2){
                if(processor.receiveRowFromDownNeighbor())
                    section++;
            }

            if(section == 3){
                if(processor.receiveColumnBackFromLeftNeighbor()){
                    section++;
                }
            }

        }else{

            if(section == 2){
                if(processor.receiveColumnBackFromLeftNeighbor()){
                    section++;
                }
            }

            if(section == 3){
                if(processor.receiveRowFromDownNeighbor())
                    section++;
            }

        }


        if(section == 4){
            if(processor.receiveLeft2x2Matrix()) {
                processor.multiplyLeft2x2Matrix();
                processor.returnRowToDownNeighbor();
                section++;

                if(processor.getLastIterationAsActive() - 1 == processor.getIteration()){
                    processor.nextIteration();
                    section = 0;
                    return;
                }
            }
        }

        if(section == 5){
            if(processor.receiveLeftMatrix()){
                processor.multiplyLeftMatrix();
                processor.nextIteration();
                section = 0;
            }
        }
    }

    @Override
    protected boolean isReadyForTransition(RightProcessor processor) {
        return processor.getLastIterationAsActive() < processor.getIteration();
    }

    @Override
    protected void finalAction(RightProcessor processor) {
        processor.sendToAccumulator();
    }

    @Override
    protected ProcessorState<?> nextState(RightProcessor processor) {
        if(processor.hasAccumulator())
            return new RightAccumulationProcessorState();
        else
            return new FreeProcessorState();
    }
}
