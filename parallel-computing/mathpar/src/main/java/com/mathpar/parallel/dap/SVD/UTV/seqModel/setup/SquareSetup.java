package com.mathpar.parallel.dap.SVD.UTV.seqModel.setup;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.*;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

import java.util.Arrays;
import java.util.List;

public class SquareSetup extends ProcessorsSetup {
    public SquareSetup(int processors, Ring ring) {
        super((int) Math.round(Math.sqrt(processors)), ring);
    }

    @Override
    public RunnableProcessor getProcessor(int rank) {
        int row = rank/processorsInRow;
        int col = rank%processorsInRow;

        if(row == col){

            if(row==0)
                return new DiagonalAccumulationProcessor(rank, processorsInRow, globalTransport, ring);

            return new DiagonalProcessor(rank, processorsInRow, globalTransport, ring);

        }else

        if(row < col){
            if(row == 0)
                return new RightAccumulationProcessor(rank, processorsInRow, globalTransport, ring);

            return new RightProcessor(rank, processorsInRow, globalTransport, ring);
        }

        else{

            if(col == 0)
                return new LeftAccumulationProcessor(rank, processorsInRow, globalTransport, ring);

            return new LeftProcessor(rank, processorsInRow, globalTransport, ring);
        }

    }

    @Override
    public List<RunnableProcessor> getProcessorForSimulation() {
        RunnableProcessor[] processors = new Processor[processorsInRow*processorsInRow];

        for (int col = 0; col < processorsInRow; col++) {
            for (int row = 0; row < processorsInRow; row++) {
                int rank = processorsInRow*row + col;
                processors[rank] = getProcessor(rank);
            }
        }

        return Arrays.asList(processors);
    }

    @Override
    protected Transport defineGlobalTransport() {
        return new LocalTransport(this.processorsInRow*this.processorsInRow);
    }
}
