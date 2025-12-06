package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import java.util.ArrayList;
import java.util.List;

public class Coordinator {

    private Processor processor;

    protected int rank;
    protected int processorsInRowNumber;

    public Coordinator(Processor processor, int rank, int processorsInRowNumber){
        this.processor = processor;
        this.rank = rank;
        this.processorsInRowNumber = processorsInRowNumber;
    }

    public int leftAccumulator(){
        return getCurrentProcessorRow()*processorsInRow();
    }

    public final int mainAccumulator(){
        return 0;
    }

    public int rightAccumulator(){
        return getCurrentProcessorColumn();
    }

    public final int leftNeighbor(){
        return leftNeighbor(rank);
    }
    private int leftNeighbor(int current){ return current - 1;}

    public final int downNeighbor(){
        return  downNeighbor(rank);
    }
    private int downNeighbor(int current){
        return  current + processorsInRow();
    }

    public final int rightNeighbor(){
        return rightNeighbor(rank);
    }

    public int rightNeighbor(int current){
        return current + 1;
    }

    public final int upNeighbor(){
        return upNeighbor(rank);
    }
    private int upNeighbor(int current){
        return current - processorsInRow();
    }

    public int currentActiveLeftProcessor(){

        return rank - rank%processorsInRow() + iterationShift();
    }

    public int currentActiveUpProcessor(){
        return rank%processorsInRow() + iterationShift() * processorsInRow();
    }

    public final int activeDiagonalProcessor(){
        return iterationShift()*(processorsInRow() + 1);
    }

    public int previousDiagonalProcessor(){
        int prev = (currentProcessorLevel() - 1)*(processorsInRow() + 1);

        return Math.max(prev, 0);
    }

    public int nextDiagonalProcessor(){
        int next = (currentProcessorLevel() + 1)*(processorsInRow() + 1);

        return Math.min(next, processorsTotal() - 1);
    }

    public List<Integer> downNeighbors(){
        ArrayList<Integer> array = new ArrayList<>();
        int current = downNeighbor();
        int end = downLastNeighbor();
        while(current <= end){
            array.add(current);
            current = downNeighbor(current);
        }
        return array;
    }

    public int downLastNeighbor(){
        int row = processorsInRow();
        return row*row - row + rank%row;
    }

    public List<Integer> rightNeighbors(){
        ArrayList<Integer> array = new ArrayList<>();
        int current = rightNeighbor();
        int end = rightLastNeighbor();
        while(current <= end){
            array.add(current);
            current = rightNeighbor(current);
        }
        return array;
    }

    public List<Integer> getDiagonalProcessorsExceptCurrent(){
        ArrayList<Integer> array = new ArrayList<>();
        int current = 0;
        int end = processorsTotal();
        int row = processorsInRow();
        while(current <= end){
            if(current != rank)
                array.add(current);
            current += row + 1;
        }
        return array;
    }


    public List<Integer> getProcessorsInRowToDiagonal(){
        ArrayList<Integer> array = new ArrayList<>();
        int current = getCurrentProcessorRow()*processorsInRow();
        int end = getCurrentProcessorRow()*(processorsInRow() + 1);
        while(current <= end){
//            if(current != rank)
                array.add(current);
            current = rightNeighbor(current);
        }
        return array;
    }

    public List<Integer> getProcessorsInColumnToDiagonal(){
        ArrayList<Integer> array = new ArrayList<>();
        int current = getCurrentProcessorColumn();
        int end = getCurrentProcessorColumn()*(processorsInRow() + 1);
        while(current <= end){
//            if(current != rank)
                array.add(current);
            current = downNeighbor(current);
        }
        return array;

    }

    public List<Integer> getProcessorsInRow(){
        ArrayList<Integer> array = new ArrayList<>();
        int current = getCurrentProcessorRow()*processorsInRow();
        int end = current + processorsInRow() - 1;
        while(current <= end){
//            if(current != rank)
            array.add(current);
            current = rightNeighbor(current);
        }
        return array;
    }

    public List<Integer> getProcessorsInColumn(){
        ArrayList<Integer> array = new ArrayList<>();
        int current = getCurrentProcessorColumn();
        int end = current + processorsInRow()*(processorsInRow() - 1);
        while(current <= end){
//            if(current != rank)
            array.add(current);
            current = downNeighbor(current);
        }
        return array;

    }
    public int rightLastNeighbor(){
        return rank - rank%processorsInRow() + processorsInRow() - 1;
    }

    public final int processorsInRow(){
        return processorsInRowNumber;
    }

    public int getRank() {
        return rank;
    }

    public boolean isLastInLine(){
        return isLastInLine(getRank());
    }

    public boolean isLastInLine(int rank){
        int col = getProcessorColumn(rank);
        int row = getProcessorRow(rank);
        int last = processorsInRow() - 1;
        return Math.max(col, row) == last;
    }

    public final int processorsTotal(){
        return processorsInRowNumber*processorsInRowNumber;
    }

    public final int activeProcessorsInRow(){
        return processorsInRow() - iterationShift();
    }

    public int getProcessorRow(int rank){
        return rank/processorsInRow();
    }

    public int getCurrentProcessorRow(){
        return getProcessorRow(getRank());
    }

    public int getProcessorColumn(int rank){
        return rank%processorsInRow();
    }

    public int getCurrentProcessorColumn(){
        return getProcessorColumn(getRank());
    }

    private int iterationShift(){
        return processor.iterationShift();
    }

    public int currentProcessorLevel(){
        int row = getCurrentProcessorRow();
        int col = getCurrentProcessorColumn();

        return Math.min(row, col);
    }

    public int amountOfLeftProcessorsInRow(){
       return getCurrentProcessorRow();
    }

    public int amountOfRightProcessorsInCol(){
        return getCurrentProcessorColumn();
    }

    public boolean isNextToDiagonal(){
        boolean isLeftToDiagonal = rightNeighbor()%(processorsInRow() + 1) == 0;
        boolean isRightToDiagonal = leftNeighbor()%(processorsInRow() + 1) == 0;
        return isLeftToDiagonal || isRightToDiagonal;
    }

    public boolean isDiagonal(int procNum){
        return procNum%(processorsInRow() + 1) == 0;
    }

    public boolean isDiagonalProcessor(){
        return isDiagonal(rank);
    }

    public boolean isFirstDiagonal(){
        return rank == 0;
    }

    public boolean isLastDiagonal(){
        return rank == processorsTotal() - 1;
    }

    public boolean isLeftProcessor(int rank){
        return getProcessorRow(rank) > getProcessorColumn(rank);
    }

    public boolean isRightProcessor(int rank){
        return getProcessorRow(rank) < getProcessorColumn(rank);
    }


    public boolean isLeftProcessor(){
        return isLeftProcessor(rank);
    }

    public boolean isRightProcessor(){
        return isRightProcessor(rank);
    }

    public boolean isOnFirstProcessorColumn(){
        return rank%processorsInRow() == 0;
    }

    public boolean isOnFirstProcessorRow(){
        return rank < processorsInRow();
    }

    public boolean isMainAccumulator(){
        return rank == mainAccumulator();
    }

    //    protected List<Integer> upNeighbors(){
//        ArrayList<Integer> processors = new ArrayList<>();
//
//        // TODO generate list of up processors with respect to iteration shift
//
//        return processors;
//    }
//
//    protected List<Integer> leftNeighbors(){
//        ArrayList<Integer> processors = new ArrayList<>();
//
//        // TODO generate list of left processors with respect to iteration shift
//
//        return processors;
//    }

}
