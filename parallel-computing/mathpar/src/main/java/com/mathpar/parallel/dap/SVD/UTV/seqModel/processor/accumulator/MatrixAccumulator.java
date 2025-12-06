package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator.Communicator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Coordinator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class MatrixAccumulator {
    protected Processor processor;
    protected Coordinator coordinator;
    protected Communicator communicator;

    protected MatrixS[][][] matrices;

    private int accumulationBlock;
    private int accumulationIteration;

    protected MatrixS accumulator;

    private List<Integer> waitingList;


    private int section;

    public MatrixAccumulator(Processor processor){
        this.processor = processor;
        this.communicator = processor.getCommunicator();
        this.coordinator = processor.getCoordinator();
        this.waitingList = getWaitingList();
        this.accumulator = genAccumulator();
        this.matrices = new MatrixS[waitingList.size()][][];
    }

    public void setInitData(MatrixS[][] firstChunk){
        this.matrices[0] = firstChunk;
        waitingList.remove(Integer.valueOf(coordinator.getRank()));
    }

    public boolean accumulate(){

        if(section == 0)
            if(!coordinator.isLastInLine()){
                if(receiveAccumulationLineFromPreviousNeighbor()){
                    section++;
                }
            }else{
                section++;
            }

        if(section == 1){
            multiplyAccumulatedMatrices();
            section++;
        }

        if(section == 2){
            if(!coordinator.isMainAccumulator()){
                if(receiveAccumulationLineBackFromNextNeighbor()){
                    section++;
                }
            } else {
                section++;
            }

        }

        if(section == 3){
            if(isReadyToNextBlockAccumulation()){
                if(receiveAccumulatedMatrices()){
                    section = 0;
                }
            }else{
                section = 0;
            }
        }

        return isAccumulated();
    }

    private void multiplyAccumulatedMatrices(){
        MatrixS[] line = matrices[accumulationBlock][accumulationIteration];


        if(!coordinator.isLastInLine()){
            line = Arrays.copyOfRange(line, 1, line.length);
        }

        accumulator = multiplyMatrix(line, accumulator, processor.getRing());

        if(accumulationBlock != matrices.length - 1 && !coordinator.isFirstDiagonal() && !(accumulationBlock == matrices.length - 2 && accumulationIteration == matrices[accumulationBlock].length - 1))
            sendAccumulatedLineToNextNeighbor();

        accumulationIteration++;

        if(matrices[accumulationBlock].length == accumulationIteration){
            accumulationIteration = 0;
            matrices[accumulationBlock] = null;
            accumulationBlock++;
        }


    }

    protected abstract MatrixS multiplyMatrix(MatrixS[] matrices, MatrixS matrix, Ring ring);

    private void sendAccumulatedLineToNextNeighbor(){

        Element[] line = getFirstLine(accumulator, processor.getRing());

        communicator.send(line, nextNeighbor(), Transport.Tag.ACCUMULATION_LINE);
    }



    private boolean receiveAccumulationLineBackFromNextNeighbor(){

        if(accumulationBlock == matrices.length - 1 || coordinator.isFirstDiagonal())
            return true;

        Element[] line = (Element[]) communicator.receiveArray(nextNeighbor(), Transport.Tag.ACCUMULATION_LINE);

        if(line != null) {
            putFirstLine(accumulator, line, processor.getRing());
        }

        return line != null;
    }

    private boolean receiveAccumulationLineFromPreviousNeighbor(){

        Element[] line =  (Element[]) communicator.receiveArray(previousNeighbor(), Transport.Tag.ACCUMULATION_LINE);

        if(line != null){
            MatrixS matrix2x2 =  matrices[accumulationBlock][accumulationIteration][0];
            line =  multiplyLastLine(matrix2x2, line, accumulator, processor.getRing());
            communicator.send(line, previousNeighbor(), Transport.Tag.ACCUMULATION_LINE);
        }

        return line != null;
    }

    public void sendResultToRootProcessor() {
        communicator.send(accumulator,
                coordinator.mainAccumulator(),
                Transport.Tag.ACCUMULATION_RESULT);
    }

    private boolean isReadyToNextBlockAccumulation(){
        return accumulationIteration == 0;
    }

    private boolean receiveAccumulatedMatrices(){


        Iterator<Integer> iterator = waitingList.iterator();

        while (iterator.hasNext()){
            int source = iterator.next();
            MatrixS[][] m = (MatrixS[][]) communicator.receive2dArray(source, Transport.Tag.MATRIX_ACCUMULATION);

            if(m != null){
                iterator.remove();
                int index = indexOfChunk(source);

                matrices[index] = m;
            }
        }

        return isAllReceived() || (accumulationBlock + 1 < matrices.length) && (matrices[accumulationBlock + 1] != null);
    }

    protected abstract int indexOfChunk(int prank);

    private boolean isAllReceived(){
        return waitingList.isEmpty();
    }

    private boolean isComputed(){
        return accumulationBlock == matrices.length && accumulationIteration == 0;
    }

    public boolean isAccumulated(){
        return isAllReceived() && isComputed();
    }

    public MatrixS getResult(){
        return accumulator;
    }

    protected abstract List<Integer> getWaitingList();
    protected abstract MatrixS genAccumulator();

    protected abstract int nextNeighbor();
    protected abstract int previousNeighbor();
    protected abstract Element[] multiplyLastLine(MatrixS matrix2x2, Element[] line, MatrixS mainMatrix, Ring ring);
    protected abstract void putFirstLine(MatrixS matrix, Element[] line, Ring ring);
    protected abstract Element[] getFirstLine(MatrixS matrix, Ring ring);
}
