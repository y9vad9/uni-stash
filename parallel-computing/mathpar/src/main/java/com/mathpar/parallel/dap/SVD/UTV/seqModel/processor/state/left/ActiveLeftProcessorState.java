package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.left;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.LeftProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.FreeProcessorState;


public class ActiveLeftProcessorState extends ProcessorState<LeftProcessor>{
    private int section = 0;
    @Override
    public void action(LeftProcessor processor) {
        Coordinator coordinator = processor.getCoordinator();

        if(section == 0){
            if(!coordinator.isLastInLine()){
                if(processor.receiveRowFromDownNeighbor()){
                    processor.eliminateElementInColumn();
                    processor.propagateLeft2x2MatrixToRightNeighbors();

                    if(processor.getLastIterationAsActive() != processor.getIteration())
                        processor.returnRowToDownNeighbor();
//
                    section++;
                }
            }else{
                section++;
            }
        }

        if(section == 1){
            processor.eliminateColumn();
//            processor.sendRowToUpNeighbor();
            processor.propagateLeftMatrixToRightNeighbors();
//            processor.sendToAccumulator();

            if(processor.getLastIterationAsActive() == processor.getIteration()){

                if(coordinator.isNextToDiagonal()){
                    processor.sendElementToDiagonal();
                }else{
                    processor.sendRowToUpNeighbor();
                }

                processor.nextIteration();
                section = 0;
                return;
            }

            // reordered to send one element instead element array
            processor.sendRowToUpNeighbor();
            section++;
        }

        if(coordinator.isNextToDiagonal()){
            if(section == 2){
                if(processor.receiveColumnFromRightNeighbor()){
                    section++;
                }
            }

            if(section == 3){
                if(processor.receiveRowBackFromUpNeighbor())
                    section++;
            }
        }else{
            if(section == 2){
                if(processor.receiveRowBackFromUpNeighbor())
                    section++;
            }

            if(section == 3){
                if(processor.receiveColumnFromRightNeighbor()){
                    section++;
                }
            }
        }



        if(section == 4){
            if(processor.receiveRight2x2Matrix()){
                processor.multiplyRight2x2Matrix();
                processor.returnColumnToRightNeighbor();
                section++;

                if(processor.getLastIterationAsActive() - 1 == processor.getIteration()){
                    processor.nextIteration();
                    section = 0;
                    return;
                }
            }
        }

        if(section == 5){
            if(processor.receiveRightMatrix()){
                processor.multiplyRightMatrix();

                processor.nextIteration();
                section = 0;
            }
        }
    }

    @Override
    protected void finalAction(LeftProcessor processor) {
        processor.sendToAccumulator();
    }

    @Override
    protected boolean isReadyForTransition(LeftProcessor processor) {
        return processor.getLastIterationAsActive() < processor.getIteration();
    }

    @Override
    protected ProcessorState<?> nextState(LeftProcessor processor) {
        if(processor.hasAccumulator())
            return new LeftAccumulationProcessorState();
        else
            return new FreeProcessorState();
    }

}
