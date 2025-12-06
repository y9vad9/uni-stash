package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator.*;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

import java.util.ArrayList;
import java.util.List;

public class DiagonalAccumulationProcessor extends DiagonalProcessor implements Accumulator, DataInitializer {
    private List<Integer> waitingList;
    private Element[][] accumulator;

    private MatrixAccumulator leftMatrixAccumulator;
    private MatrixCollector leftMatrixCollector;

    private MatrixAccumulator rightMatrixAccumulator;
    private MatrixCollector rightMatrixCollector;

    private MatrixS[] result;


    public DiagonalAccumulationProcessor(int rank, int processorsInRowNumber, Transport transport, Ring ring) {
        super(rank, processorsInRowNumber, transport, ring);
        accumulator = new Element[coordinator.processorsInRow()][3];
        result = new MatrixS[3];
    }


    @Override
    public void send3DiagonalMatrixToAccumulator() {
        accumulateResult(data, leftElement, rightElement, coordinator.getCurrentProcessorRow());
        waitingList = coordinator.getDiagonalProcessorsExceptCurrent();
    }

    @Override
    protected void sendLeftMatrixToAccumulator() {
        leftMatrixAccumulator = new LeftMatrixAccumulator(this);
        leftMatrixAccumulator.setInitData(leftMatrixStorage.getResult());
        leftMatrixCollector = new LeftMatrixCollector(this);
    }

    @Override
    protected void sendRightMatrixToAccumulator() {
        rightMatrixAccumulator = new RightMatrixAccumulator(this);
        rightMatrixAccumulator.setInitData(rightMatrixStorage.getResult());
        rightMatrixCollector = new RightMatrixCollector(this);
    }

    public boolean collectMain(){
        Element[] result;
        List<Integer> received = new ArrayList<>();
        for(Integer procNum: waitingList){
            result = (Element[]) communicator.receiveArray(procNum, Transport.Tag.DIAGONAL_ACCUMULATION);

            if(result != null){
                received.add(procNum);
                accumulateResult((MatrixS) result[0], result[1], result[2], coordinator.getProcessorRow(procNum));
            }
        }
        waitingList.removeAll(received);

        if(waitingList.isEmpty()){
            buildResult();
        }

        return waitingList.isEmpty();
    }

    public boolean collectLeft(){
        return collect(leftMatrixAccumulator, leftMatrixCollector, 0);
    }

    public boolean collectRight(){
        return collect(rightMatrixAccumulator, rightMatrixCollector, 2);
    }

    private boolean collect(MatrixAccumulator accumulator, MatrixCollector collector, int resultIndex){
        if(!accumulator.isAccumulated()){
            boolean isAccumulated = accumulator.accumulate();

            if(isAccumulated){
                collector.setCurrentChunk(accumulator.getResult());
            }
        } else if(!collector.isCollected()){
            boolean isCollected = collector.collect();

            if(isCollected){
                result[resultIndex] = collector.getResult();
            }
        }

        return accumulator.isAccumulated() && collector.isCollected();
    }



    public void buildResult(){
        result[1] = Utils.gather3DiagonalMatrix(accumulator, wholeDataSize(), ring);

    }

    @Override
    public boolean hasAccumulator() {
        return true;
    }


    private void accumulateResult(MatrixS matrix, Element left, Element right, int row){
        accumulator[row][1] = matrix;

        if(left != null)
            accumulator[row + 1][0] = left;

        if(right != null)
            accumulator[row][2] = right;
    }


    @Override
    public MatrixS[] getResult() {
        return result;
    }

    @Override
    public void setInitData(MatrixS source) {
        MatrixS[] chunks = splitDataToChunks(source);

        sendDataToProcessors(chunks);
    }

    private MatrixS[] splitDataToChunks(MatrixS data){
        int processorsInRow = coordinator.processorsInRow();
        MatrixS[] splitData = new MatrixS[processorsInRow*processorsInRow];

        arrangeData(data, 0, processorsInRow, processorsInRow, splitData);

        return splitData;
    }

    private void arrangeData(MatrixS data, int startPos, int currentInRow, int totalInRow, MatrixS[] result){
        MatrixS[] split = data.split();
        int secondPos = startPos + currentInRow/2;
        int thirdPos = startPos + (currentInRow/2)*totalInRow;
        int fourthPos = thirdPos + currentInRow/2;

        if (currentInRow == 2){
            result[startPos]  = split[0];
            result[secondPos] = split[1];
            result[thirdPos]  = split[2];
            result[fourthPos] = split[3];
            return;
        }

        arrangeData(split[0], startPos,  currentInRow/2, totalInRow, result);
        arrangeData(split[1], secondPos, currentInRow/2, totalInRow, result);
        arrangeData(split[2], thirdPos,  currentInRow/2, totalInRow, result);
        arrangeData(split[3], fourthPos, currentInRow/2, totalInRow, result);

    }

    private void sendDataToProcessors(MatrixS[] chunks){
        MatrixS data = (MatrixS) communicator.scatter(chunks, coordinator.getRank());

        setData(data);
    }
}
