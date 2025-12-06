package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DiagonalProcessor;

public class PassiveDiagonalProcessorState extends ProcessorState<DiagonalProcessor> {

    private int section;

    @Override
    public void action(DiagonalProcessor processor) {
        Coordinator coordinator = processor.getCoordinator();
        if(!coordinator.isLastDiagonal()){
            if(section == 0){
                if(processor.receiveColumnFromRightNeighbor())
                    section++;
            }
            if(section==1){
                if(processor.receiveRowFromDownNeighbor())
                    section++;
            }
            if(section==2){
                if(processor.receiveElementFromNextDiagonalNeighbor())
                    section++;
            }

            if(section==3){
                if(processor.receiveLeft2x2Matrix())
                    section++;
            }

            if(section==4){
                if(processor.receiveRight2x2Matrix()) {
                    processor.multiply2x2Matrices();
                    processor.returnRowWithExtraElementToDownNeighbor();
                    processor.returnColumnWithExtraElementToRightNeighbor();
                    section++;
                }
            }

        }else if (section == 0) section = 5;


        if(section==5) {
            if(processor.receiveLeftMatrix()){
                processor.multiplyLeftMatrix();
                section++;
            }
        }

        if(section==6) {
            if(processor.receiveRightMatrix()) {
                processor.multiplyRightMatrix();

                // do not send column and row on (startActive-1) iteration
                if(processor.getIteration() == processor.getFirstIterationAsActive() - 1){
                    processor.nextIteration();
                    section = 0;
                    return;
                }

                processor.sendColumnToLeftNeighbor();
                processor.sendRowToUpNeighbor();
                processor.sendElementToPreviousDiagonal();
                section++;
            }
        }

        if(section==7){
            if(processor.receiveRowBackFromUpNeighbor())
                section++;
        }
        if(section==8) {
            if(processor.receiveColumnBackFromLeftNeighbor()) {
                processor.nextIteration();
                section = 0;
            }
        }
    }

    @Override
    protected boolean isReadyForTransition(DiagonalProcessor processor) {
        return  processor.getFirstIterationAsActive() == processor.getIteration();
    }

    @Override
    protected ProcessorState<?> nextState(DiagonalProcessor processor) {
        Coordinator coordinator = processor.getCoordinator();

        if(coordinator.isLastDiagonal())
            return new ActiveLastDiagonalProcessorState();
        else
            return new ActiveDiagonalProcessorState();
    }


}
