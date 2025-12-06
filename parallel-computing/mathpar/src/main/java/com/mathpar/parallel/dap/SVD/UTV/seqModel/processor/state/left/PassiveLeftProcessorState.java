package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.left;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.LeftProcessor;

public class PassiveLeftProcessorState extends ProcessorState<LeftProcessor> {
    private int section;
    @Override
    public void action(LeftProcessor processor) {
        Coordinator coordinator = processor.getCoordinator();
        if(!coordinator.isLastInLine()){
            if(section == 0){
                if(processor.receiveRowFromDownNeighbor())
                    section++;
            }
            if(section == 1){
                if(processor.receiveLeft2x2Matrix()){
                    processor.multiplyLeft2x2Matrix();
                    processor.returnRowToDownNeighbor();
                    section++;
                }
            }
        }else if(section == 0) section = 2;

        if(section == 2){
            if(processor.receiveLeftMatrix()){
                processor.multiplyLeftMatrix();
                processor.sendRowToUpNeighbor();
                section++;
            }
        }

        if(coordinator.isNextToDiagonal()){

            if(section == 3){
                if(processor.receiveColumnFromRightNeighbor()){
                    section++;
                }
            }

            if(section == 4){
                if(processor.receiveRowBackFromUpNeighbor()){
                    section++;
                }
            }

        }else{
            if(section == 3){
                if(processor.receiveRowBackFromUpNeighbor()){
                    section++;
                }
            }

            if(section == 4){
                if(processor.receiveColumnFromRightNeighbor()){
                    section++;
                }
            }
        }


        if(section == 5){
            if(processor.receiveRight2x2Matrix()){
                processor.multiplyRight2x2Matrix();
                processor.returnColumnToRightNeighbor();
                section++;
            }
        }
        if(section == 6){
            if(processor.receiveRightMatrix()){
                processor.multiplyRightMatrix();

                // do not send column on iteration start-1
                if(processor.getFirstIterationAsActive() - 1 == processor.getIteration()){
                    processor.nextIteration();
                    section = 0;
                    return;
                }
                processor.sendColumnToLeftNeighbor();
                section++;
            }
        }

        if(section == 7){
            if(processor.receiveColumnBackFromLeftNeighbor()){
                processor.nextIteration();
                section = 0;
            }
        }
    }

    @Override
    protected boolean isReadyForTransition(LeftProcessor processor) {
        return processor.getFirstIterationAsActive() == processor.getIteration();
    }

    @Override
    protected ProcessorState<?> nextState(LeftProcessor processor) {
        return new ActiveLeftProcessorState();
    }
}
