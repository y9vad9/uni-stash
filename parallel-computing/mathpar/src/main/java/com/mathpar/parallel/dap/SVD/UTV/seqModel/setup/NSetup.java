package com.mathpar.parallel.dap.SVD.UTV.seqModel.setup;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.CompositeProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator.CompositeCommunicator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RunnableProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;
import org.javatuples.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NSetup extends ProcessorsSetup {

    public NSetup(int processors, Ring ring) {
        super(processors, ring);
    }

    @Override
    public RunnableProcessor getProcessor(int rank) {
        Pair<List<Integer>, List<Integer>> ranks = getVirtualProcessorRanks(rank);
        // create setup here to generate common local transport for virtual processors
        SquareSetup squareSetup = new SquareSetup(processorsInRow*processorsInRow, ring);

        List<Integer> allVRanks = new LinkedList<>(ranks.getValue0());
        allVRanks.addAll(ranks.getValue1());

        List<RunnableProcessor> processors = allVRanks.stream()
                .map(virtualRank -> {
                    RunnableProcessor p = squareSetup.getProcessor(virtualRank);

                    CompositeCommunicator communicator =
                            new CompositeCommunicator(squareSetup.getGlobalTransport(),
                                    getGlobalTransport(),
                                    this::realRank);

                    p.setCommunicator(communicator);
                    return p;
                })
                .collect(Collectors.toList());

        return new CompositeProcessor(rank, processorsInRow, processors);
    }

    public Pair<List<Integer>, List<Integer>> getVirtualProcessorRanks(int rank){
        List<Integer> colRanks = new LinkedList<>();
        List<Integer> rowRanks = new LinkedList<>();

        for (int i = 0; i < processorsInRow - (rank + 1); i++) {
            rowRanks.add((processorsInRow - (rank + 1))*processorsInRow + i);
        }

        for (int i = 0; i < rank + 1; i++) {
            colRanks.add(processorsInRow*i + rank);
        }

        return new Pair<>(rowRanks, colRanks);
    }

    @Override
    public List<RunnableProcessor> getProcessorForSimulation() {
        return IntStream.range(0, processorsInRow)
                .mapToObj(this::getProcessor)
                .collect(Collectors.toList());
    }

    @Override
    protected Transport defineGlobalTransport() {
        return new LocalTransport(processorsInRow);
    }

    protected int realRank(int virtualRank){
        int row = virtualRank/processorsInRow;
        int col = virtualRank%processorsInRow;

        if(row == col){
            return row;
        } else if(row < col){
            return col;
        }

        return processorsInRow - row - 1;
    }
}
