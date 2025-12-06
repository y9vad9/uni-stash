package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.matrix.MatrixS;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator.Communicator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.setup.order.ProcessorOrder;

import java.util.List;
import java.util.Optional;

public class CompositeProcessor implements RunnableProcessor, Accumulator, DataInitializer {

    private int rank;
    private int processorsInRow;
    private ProcessorOrder order;
    private List<RunnableProcessor> virtualProcessors;

    public CompositeProcessor(int rank, int processorsInRow, List<RunnableProcessor> processors) {
        this(rank, processorsInRow, processors, x -> x);
    }

    public CompositeProcessor(int rank, int processorsInRow, List<RunnableProcessor> processors, ProcessorOrder order) {
        this.rank = rank;
        this.processorsInRow = processorsInRow;
        this.virtualProcessors = processors;
        this.order = order;
    }


    @Override
    public void tick() {

        for (RunnableProcessor p: virtualProcessors) {
            p.tick();
        }
    }

    @Override
    public void run() {

        while(!isDone()){
            tick();
        }
    }

    @Override
    public boolean isDone() {
        return virtualProcessors.stream()
                .map(RunnableProcessor::isDone)
                .reduce(true, (s, c) -> s && c);
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setCommunicator(Communicator communicator) {
        for (RunnableProcessor x: virtualProcessors) {
            x.setCommunicator(communicator);
        }
    }

    @Override
    public MatrixS[] getResult() {
        if(rank != 0) throw new UnsupportedOperationException("Result can be get only from 0 processor");
        Optional<RunnableProcessor> root = virtualProcessors.stream()
                .filter(p -> p.getRank() == 0)
                .findFirst();

        if(root.isPresent()){
            return ((Accumulator) root.get()).getResult();
        }else{
            throw new RuntimeException("Cannot find root processor where it suppose to be");
        }

    }

    @Override
    public void setInitData(MatrixS source) {
        if(rank != 0) throw new UnsupportedOperationException("Init data only can be set on 0 processor");
        Optional<RunnableProcessor> root = virtualProcessors.stream()
                .filter(p -> p.getRank() == 0)
                .findFirst();

        if(root.isPresent()){
            ((DataInitializer) root.get()).setInitData(source);
        }else{
            throw new RuntimeException("Cannot find root processor where it suppose to be");
        }
    }
}
