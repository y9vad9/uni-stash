package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.right;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RightProcessor;

public class PassiveRightProcessorState extends ProcessorState<RightProcessor> {
    private int section;

    @Override
    public void action(RightProcessor processor) {
        Coordinator coordinator = processor.getCoordinator();
        if(!coordinator.isLastInLine()){
            if(section==0){
                if(processor.receiveColumnFromRightNeighbor())
                    section++;
            }

            if(section == 1){
                if(processor.receiveRight2x2Matrix()){
                    processor.multiplyRight2x2Matrix();
                    processor.returnColumnToRightNeighbor();
                    section++;
                }
            }
        }else if(section == 0) section = 2;

        if(section == 2){
            if(processor.receiveRightMatrix()){
                processor.multiplyRightMatrix();
                processor.sendColumnToLeftNeighbor();
                section++;
            }
        }

        if(coordinator.isNextToDiagonal()){
            if(section == 3){
                if(processor.receiveRowFromDownNeighbor()){
                    section++;
                }
            }

            if(section == 4){
                if(processor.receiveColumnBackFromLeftNeighbor())
                    section++;
            }
        }else{
            if(section == 3){
                if(processor.receiveColumnBackFromLeftNeighbor())
                    section++;
            }

            if(section == 4){
                if(processor.receiveRowFromDownNeighbor()){
                    section++;
                }
            }
        }


        if(section == 5){
            if(processor.receiveLeft2x2Matrix()){
                processor.multiplyLeft2x2Matrix();
                processor.returnRowToDownNeighbor();
                section++;
            }
        }

        if(section == 6){
            if(processor.receiveLeftMatrix()){
                processor.multiplyLeftMatrix();

                if(processor.getFirstIterationAsActive() - 1 == processor.getIteration()){
                    processor.nextIteration();
                    section = 0;
                    return;
                }

                processor.sendRowToUpNeighbor();
                section++;
            }
        }


        if(section == 7){
            if(processor.receiveRowBackFromUpNeighbor()){
                processor.nextIteration();
                section = 0;
            }
        }

    }

    @Override
    protected boolean isReadyForTransition(RightProcessor processor) {
        return processor.getFirstIterationAsActive() == processor.getIteration();
    }

    @Override
    protected ProcessorState<?> nextState(RightProcessor processor) {
        return new ActiveRightProcessorState();
    }
}
