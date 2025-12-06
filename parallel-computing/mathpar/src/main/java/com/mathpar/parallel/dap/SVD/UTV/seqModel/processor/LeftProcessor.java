package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.SeqUTV;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;
import org.javatuples.Pair;


public class LeftProcessor extends Processor {
    public LeftProcessor(int rank, int processorsInRowNumber, Transport transport, Ring ring) {
        super(rank, processorsInRowNumber, transport, ring);
    }

    @Override
    public void sendToAccumulator() {
        sendLeftMatrixToAccumulator();
    }

    public void eliminateColumn(){
        Pair<MatrixS[], MatrixS> UT = SeqUTV.eliminateColumnExtendedResult(data, eliminationLevel(), 1, ring);

        data = UT.getValue1();
        leftMatrix = UT.getValue0();
        leftMatrixStorage.add(leftMatrix);

    }

    @Override
    public boolean receiveRowBackFromUpNeighbor(){

        if(coordinator.isNextToDiagonal()){

            Element[] row = (Element[]) communicator.receiveArray(coordinator.upNeighbor(), Transport.Tag.ROW_WITH_EXTRA_ELEMENT);

            if(row == null) return false;
            if(row.length != data.size + 1) throw new IllegalStateException("row must be equal to data length + extra element");

            Element[] newRow = new Element[data.size];
            System.arraycopy(row, 0, newRow, 0, data.size);
            Utils.putRow(data, newRow, 0, ring);
            extraColumn[0] = row[row.length -1];

            return true;
        }else
            return super.receiveRowBackFromUpNeighbor();

    }

    @Override
    public void multiplyRight2x2Matrix() {

        if(coordinator.isNextToDiagonal()){
            Element[] col = data.getCol(data.size - 1, ring);

            MatrixS colsMatrix = Utils.matrixByColumn(col, extraColumn);
            MatrixS result = colsMatrix.multiply(right2x2Matrix, ring);

            System.arraycopy(result.getCol(1, ring), 1, extraColumn, 1, data.size-1);
            System.arraycopy(result.getCol(0, ring), 1, col, 1, data.size - 1);
            Utils.putColumn(data, col, data.size - 1, ring);
        }else{

            super.multiplyRight2x2Matrix();
        }
    }



    public void sendElementToDiagonal(){
        Element element = data.getElement(0, blockSize() - 1, ring);

        communicator.send(element, coordinator.upNeighbor(), Transport.Tag.ELEMENT);
    }
}
