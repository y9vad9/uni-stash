package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.SeqUTV;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator.MatrixStorage;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator.Communicator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.state.ProcessorState;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

public abstract class Processor implements RunnableProcessor{
    protected ProcessorState<Processor> state;
    protected int iteration;
    protected Communicator communicator;
    protected Coordinator coordinator;

    protected Ring ring;

    protected MatrixS data;
    protected Element[] extraRow;
    protected Element[] extraColumn;

    protected MatrixS[] leftMatrix;
    protected MatrixS[] rightMatrix;
    protected MatrixS left2x2Matrix;
    protected MatrixS right2x2Matrix;

    protected MatrixStorage leftMatrixStorage;
    protected MatrixStorage rightMatrixStorage;

    public Processor(int rank, int processorsInRowNumber, Transport transport, Ring ring){
        this.communicator = new Communicator(this, transport);
        this.coordinator = new Coordinator(this, rank, processorsInRowNumber);
        this.ring = ring;
        this.state = ProcessorState.initial(this);
        this.leftMatrixStorage = new MatrixStorage();
        this.rightMatrixStorage = new MatrixStorage();
    }

    public void tick(){
        state.action(this);
        state.next(this);
    }

    public void run(){
        while(!state.isFinal())
            tick();
    }

    public boolean hasAccumulator(){
        return false;
    }

    public void setState(ProcessorState state){
        this.state = state;
    }

    public void setData(MatrixS data){
        this.data = data;
    }

    public MatrixS getData(){
        return data;
    }

    public void nextIteration(){
        iteration++;
    }

    protected void sendLeftMatrixToAccumulator(){
        MatrixS[][] accumulated = leftMatrixStorage.getResult();
        communicator.send(accumulated, coordinator.leftAccumulator(), Transport.Tag.MATRIX_ACCUMULATION);
    }

    protected void sendRightMatrixToAccumulator(){
        MatrixS[][] accumulated = rightMatrixStorage.getResult();
        communicator.send(accumulated, coordinator.rightAccumulator(), Transport.Tag.MATRIX_ACCUMULATION);
    }

    public abstract void sendToAccumulator();

    public void eliminateElementInRow(){
        int row = eliminationLevel();
        Element c = data.getElement(row, data.size -1, ring);
        Element d = extraColumn[row];

        MatrixS right2x2 = SeqUTV.rotationRight(c, d, ring);

        Element[] lastColumn = data.getCol(data.size -1, ring);

        MatrixS colsJoin =  Utils.matrixByColumn(lastColumn, extraColumn);
        MatrixS updatedCols = colsJoin.multiply(right2x2, ring);

        Utils.putColumn(data, updatedCols.getCol(0, ring), data.size - 1, ring);
        extraColumn = updatedCols.getCol(1, ring);

        rightMatrixStorage.add(right2x2);
        right2x2Matrix = right2x2;
    }

    public void eliminateElementInColumn(){
        int col = eliminationLevel();
        Element a = data.getElement(data.size - 1, col, ring);
        Element b = extraRow[col];

        MatrixS left2x2 = SeqUTV.rotationLeft(a, b, ring);

        Element[] lastRow = data.getRow(data.size - 1, ring);

        MatrixS rowsJoin = Utils.matrixByRows(lastRow, extraRow);
        MatrixS updatedRows = left2x2.multiply(rowsJoin, ring);

        Utils.putRow(data, updatedRows.getRow(0, ring), data.size - 1, ring);
        extraRow = updatedRows.getRow(1, ring);

        leftMatrixStorage.add(left2x2);
        left2x2Matrix = left2x2;
    }

    public boolean receiveInitialData(){
        data = (MatrixS) communicator.scatter(coordinator.mainAccumulator());

        return data != null;
    }

    public boolean receiveRowFromDownNeighbor(){
        extraRow = (Element[]) communicator.receiveArray(coordinator.downNeighbor(), Transport.Tag.ROW);

        return extraRow != null;
    }

    public void returnRowToDownNeighbor(){
        communicator.send(extraRow, coordinator.downNeighbor(), Transport.Tag.ROW);
    }

    public boolean receiveColumnFromRightNeighbor(){
        extraColumn = (Element[]) communicator.receiveArray(coordinator.rightNeighbor(), Transport.Tag.COLUMN);

        return extraColumn != null;
    }


    public void returnColumnToRightNeighbor(){
        communicator.send(extraColumn, coordinator.rightNeighbor(), Transport.Tag.COLUMN);
    }

    public void sendRowToUpNeighbor(){
        Element[] row = data.getRow(0, ring);

        communicator.send(row, coordinator.upNeighbor(), Transport.Tag.ROW);
    }

    public void sendColumnToLeftNeighbor(){
        Element[] col = data.getCol(0, ring);

        communicator.send(col, coordinator.leftNeighbor(), Transport.Tag.COLUMN);
    }

    public void propagateRightMatrixToDownNeighbors(){
        communicator.propagate(rightMatrix, coordinator.downNeighbors(), Transport.Tag.RIGHT_MATRIX);
    }

    public void propagateLeftMatrixToRightNeighbors(){
        communicator.propagate(leftMatrix, coordinator.rightNeighbors(), Transport.Tag.LEFT_MATRIX);
    }

    public void propagateRight2x2MatrixToDownNeighbors(){
        communicator.propagate(right2x2Matrix, coordinator.downNeighbors(), Transport.Tag.RIGHT_2x2_MATRIX);
    }

    public void propagateLeft2x2MatrixToRightNeighbors(){
        communicator.propagate(left2x2Matrix, coordinator.rightNeighbors(), Transport.Tag.LEFT_2x2_MATRIX);
    }

    public boolean receiveLeftMatrix(){
        leftMatrix = (MatrixS[]) communicator.receiveArray(coordinator.currentActiveLeftProcessor(), Transport.Tag.LEFT_MATRIX);

        return leftMatrix != null;
    }

    public boolean receiveRightMatrix(){
        rightMatrix = (MatrixS[]) communicator.receiveArray(coordinator.currentActiveUpProcessor(), Transport.Tag.RIGHT_MATRIX);

        return rightMatrix != null;
    }

    public boolean receiveLeft2x2Matrix(){
        left2x2Matrix = (MatrixS) communicator.receive(coordinator.currentActiveLeftProcessor(), Transport.Tag.LEFT_2x2_MATRIX);

        return left2x2Matrix != null;
    }

    public boolean receiveRight2x2Matrix(){
        right2x2Matrix = (MatrixS) communicator.receive(coordinator.currentActiveUpProcessor(), Transport.Tag.RIGHT_2x2_MATRIX);

        return right2x2Matrix != null;
    }

    public boolean receiveColumnBackFromLeftNeighbor(){
        Element[] column = (Element[]) communicator.receiveArray(coordinator.leftNeighbor(), Transport.Tag.COLUMN);

        if(column == null) return false;

        Utils.putColumn(data, column, 0, ring);

        return true;
    }

    public boolean receiveRowBackFromUpNeighbor(){
        Element[] row = (Element[]) communicator.receiveArray(coordinator.upNeighbor(), Transport.Tag.ROW);

        if(row == null) return false;

        Utils.putRow(data, row, 0, ring);

        return true;
    }


    public void multiplyLeft2x2Matrix(){
        extraRow = Utils.multiplyLastRow(left2x2Matrix, extraRow, data, ring);
    }

    public void multiplyRight2x2Matrix(){
        extraColumn = Utils.multiplyLastColumn(data, extraColumn, right2x2Matrix, ring);
    }

    public void multiplyLeftMatrix(){

        data = Utils.multiply(leftMatrix, data, ring);
    }

    public void multiplyRightMatrix(){

        data = Utils.multiply(data, rightMatrix, ring);
    }



    public boolean isDone(){
        return state.isFinal();
    }


    public final int blockSize(){
        return data.size;
    }

    public int iterationShift(){
        return getIteration()/blockSize();
    }

    public final int eliminationLevel(){
        return iteration%blockSize();
    }

    public int wholeDataSize(){
        return blockSize()*coordinator.processorsInRow();
    }

    public int getIteration(){
        return iteration;
    }


    public int getLastIterationAsActive(){
        return (coordinator.currentProcessorLevel() + 1)* blockSize() - 1;
    }

    public int getFirstIterationAsActive(){
        return coordinator.currentProcessorLevel()* blockSize();
    }

    public Coordinator getCoordinator(){
        return coordinator;
    }

    public int getRank(){
        return coordinator.getRank();
    }

    public Ring getRing() {
        return ring;
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
        communicator.setProcessor(this);
    }
}
