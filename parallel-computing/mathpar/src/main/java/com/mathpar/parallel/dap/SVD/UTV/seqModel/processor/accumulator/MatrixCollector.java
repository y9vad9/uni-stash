package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator;

import com.mathpar.matrix.MatrixS;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator.Communicator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

import java.util.Iterator;
import java.util.List;

public abstract class MatrixCollector {
    protected Coordinator coordinator;
    private Communicator communicator;
    private MatrixS[] matrixChunks;
    private MatrixS result;
    private List<Integer> waitingList;


    public MatrixCollector(Processor processor) {
        this.coordinator = processor.getCoordinator();
        this.communicator = processor.getCommunicator();
        this.waitingList = generateWaitingList();
        this.matrixChunks = new MatrixS[waitingList.size()];
    }

    public void setCurrentChunk(MatrixS matrix){
        matrixChunks[0] = matrix;
        waitingList.remove(Integer.valueOf(coordinator.getRank()));
    }

    public boolean collect(){
        Iterator<Integer> iterator = waitingList.iterator();

        while (iterator.hasNext()){
            int source = iterator.next();
            MatrixS chunk = (MatrixS) communicator.receive(source, Transport.Tag.ACCUMULATION_RESULT);

            if(chunk != null){
                iterator.remove();
                int index = Math.max(coordinator.getProcessorRow(source),coordinator.getProcessorColumn(source));

                matrixChunks[index] = chunk;
            }
        }

        if(isAllReceived() && !isComputed()){
            result = compute(matrixChunks);
        }

        return isAllReceived() && isComputed();
    }



    private boolean isAllReceived(){
        return  waitingList.isEmpty();
    }

    private boolean isComputed(){
        return result != null;
    }

    public boolean isCollected(){
        return isAllReceived() && isComputed();
    }


    public MatrixS getResult() {
        return result;
    }

    protected abstract List<Integer> generateWaitingList();
    protected abstract MatrixS compute(MatrixS[] matrixChunks);
}
