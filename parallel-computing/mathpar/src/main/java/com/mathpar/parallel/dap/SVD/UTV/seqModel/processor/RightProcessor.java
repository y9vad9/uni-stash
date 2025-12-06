package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.SeqUTV;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;
import org.javatuples.Pair;

public class RightProcessor extends Processor {
    public RightProcessor(int rank, int processorsInRowNumber, Transport transport, Ring ring) {
        super(rank, processorsInRowNumber, transport, ring);
    }

    @Override
    public void sendToAccumulator() {
        sendRightMatrixToAccumulator();
    }


    public void eliminateRow(){

        Pair<MatrixS, MatrixS[]> TV = SeqUTV.eliminateRowExtendedResult(data, eliminationLevel(), 1, ring);

        data = TV.getValue0();
        rightMatrix = TV.getValue1();
        rightMatrixStorage.add(rightMatrix);

    }


    @Override
    public boolean receiveColumnBackFromLeftNeighbor(){

        if(coordinator.isNextToDiagonal()){
            Element[] column = (Element[]) communicator.receiveArray(coordinator.leftNeighbor(), Transport.Tag.COLUMN_WITH_EXTRA_ELEMENT);

            if(column == null) return false;
            if(column.length != data.size + 1) throw new IllegalStateException("column must be equal to data length + extra element");

            Element[] col = new Element[data.size];
            System.arraycopy(column, 0, col, 0, data.size);

            Utils.putColumn(data, col, 0, ring);

            extraRow[0] = column[data.size];

            return true;
        }

        return super.receiveColumnBackFromLeftNeighbor();
    }


    @Override
    public void multiplyLeft2x2Matrix() {

        if(coordinator.isNextToDiagonal()){

            Element[] row = data.getRow(data.size - 1, ring);
            MatrixS rowMatrix = Utils.matrixByRows(row, extraRow);

            MatrixS result = left2x2Matrix.multiply(rowMatrix, ring);

            System.arraycopy(result.getRow(0, ring), 1, row, 1, data.size - 1);
            Utils.putRow(data, row, data.size - 1, ring);

            System.arraycopy(result.getRow(1, ring), 1, extraRow, 1, data.size - 1);
        }else{
            super.multiplyLeft2x2Matrix();
        }
    }

    public void sendElementToDiagonal(){
        Element element = data.getElement( blockSize() - 1, 0,  ring);

        communicator.send(element, coordinator.leftNeighbor(), Transport.Tag.ELEMENT);
    }

}
