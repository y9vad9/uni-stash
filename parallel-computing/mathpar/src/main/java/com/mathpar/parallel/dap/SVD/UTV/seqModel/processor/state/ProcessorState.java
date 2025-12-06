package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state;


import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal.ActiveDiagonalProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal.PassiveDiagonalProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.diagonal.PassiveLastDiagonalProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.left.ActiveLeftProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.left.PassiveLeftProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.right.ActiveRightProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.right.PassiveRightProcessorState;

public abstract class ProcessorState<T extends Processor> {

    public void next(T processor){
        if(isReadyForTransition(processor)){
            finalAction(processor);
            ProcessorState<?> state = nextState(processor);
            processor.setState(state);
        }
    }
    public abstract void action(T processor);
    protected abstract boolean isReadyForTransition(T processor);
    protected abstract ProcessorState<?> nextState(T processor);
    protected void finalAction(T processor){}


    public boolean isFinal(){
        return false;
    }

    public static ProcessorState<Processor> initial(Processor processor){
        if(processor.getCoordinator().isFirstDiagonal())
            return (ProcessorState<Processor>) initialized(processor);
        return new InitialProcessorState();
    }

    public static ProcessorState<?> initialized(Processor processor){
        ProcessorState<?> state;
        Coordinator coordinator = processor.getCoordinator();
        int rank = coordinator.getRank();
        int total = coordinator.processorsTotal();
        int inRow = coordinator.processorsInRow();

        if(coordinator.isFirstDiagonal())
            state = new ActiveDiagonalProcessorState();
//        else if(coordinator.isLastDiagonal())
//            state = new PassiveLastDiagonalProcessorState();
        else if(coordinator.isDiagonalProcessor())
            state = new PassiveDiagonalProcessorState();
        else if(coordinator.isOnFirstProcessorColumn())
            state = new ActiveLeftProcessorState();
        else if(coordinator.isOnFirstProcessorRow())
            state = new ActiveRightProcessorState();
        else if(coordinator.isLeftProcessor())
            state = new PassiveLeftProcessorState();
        else
            state = new PassiveRightProcessorState();

        return state;
    }
}
