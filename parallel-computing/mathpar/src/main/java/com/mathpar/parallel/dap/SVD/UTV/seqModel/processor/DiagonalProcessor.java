package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.SeqUTV;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;
import org.javatuples.Triplet;

public class DiagonalProcessor extends Processor {

    protected Element leftElement;
    protected Element rightElement;
    protected Element extraElement;

    public DiagonalProcessor(int rank, int processorsInRowNumber, Transport transport, Ring ring) {
        super(rank, processorsInRowNumber, transport, ring);
    }

    @Override
    public void sendToAccumulator() {
        sendLeftMatrixToAccumulator();
        sendRightMatrixToAccumulator();
    }

    public void eliminate() {
        eliminateElementInRowAndColumn();
        returnColumnWithExtraElementToRightNeighbor();
        returnRowWithExtraElementToDownNeighbor();
        propagateLeft2x2MatrixToRightNeighbors();
        propagateRight2x2MatrixToDownNeighbors();

        if (getLastIterationAsActive() - 1 == getIteration()){
            nextIteration();
            return;
        }

        eliminateRowAndColumn();
        propagateRightMatrixToDownNeighbors();
        propagateLeftMatrixToRightNeighbors();

        nextIteration();
    }


    public boolean receiveElementFromNextDiagonalNeighbor() {
        extraElement = (Element) communicator.receive(coordinator.nextDiagonalProcessor(), Transport.Tag.ELEMENT);

        return extraElement != null;
    }

    public void multiply2x2Matrices() {
        multiplyLeft2x2Matrix();
        multiplyLeft2x2ToCommonColumn();
        multiplyRight2x2Matrix();
        multiplyRight2x2ToCommonRow();

    }

    private void multiplyLeft2x2ToCommonColumn(){
        Element colElement = extraColumn[extraColumn.length - 1];
        Element[] col = new Element[]{colElement, extraElement};
        Element[] result = left2x2Matrix.multiply(col, 2, ring);

        extraColumn[extraColumn.length - 1] = result[0];
        extraElement = result[1];
    }

    private void multiplyRight2x2ToCommonRow(){
        Element rowElement = extraRow[extraRow.length - 1];
        Element[][] row = new Element[][]{{rowElement, extraElement}};
        int[][] cols = new int[][]{{0,1}};
        MatrixS matrix = new MatrixS(row, cols);

        MatrixS result = matrix.multiply(right2x2Matrix, ring);

        extraRow[extraRow.length - 1] = result.getElement(0, 0, ring);
        extraElement = result.getElement(0, 1, ring);
    }

    public void returnRowWithExtraElementToDownNeighbor() {
        Element[] row = new Element[extraRow.length + 1];

        System.arraycopy(extraRow, 0, row, 0, extraRow.length);
        row[row.length - 1] = extraElement;

        communicator.send(row, coordinator.downNeighbor(), Transport.Tag.ROW_WITH_EXTRA_ELEMENT);

    }

    public void returnColumnWithExtraElementToRightNeighbor() {
        Element[] col = new Element[extraColumn.length + 1];

        System.arraycopy(extraColumn, 0, col, 0, extraColumn.length);
        col[col.length - 1] = extraElement;

        communicator.send(col, coordinator.rightNeighbor(), Transport.Tag.COLUMN_WITH_EXTRA_ELEMENT);
    }

    public void sendElementToPreviousDiagonal() {
        Element element = data.getElement(0, 0, ring);

        communicator.send(element, coordinator.previousDiagonalProcessor(), Transport.Tag.ELEMENT);
    }


    public void eliminateElementInRowAndColumn() {
        computeRight2x2Matrix();
        computeLeft2x2Matrix();
        multiply2x2Matrices();
    }

    private void computeRight2x2Matrix(){
        int row = eliminationLevel();
        Element c = data.getElement(row, data.size -1, ring);
        Element d = extraColumn[row];
        right2x2Matrix = SeqUTV.rotationRight(c, d, ring);
        rightMatrixStorage.add(right2x2Matrix);
    }

    private void computeLeft2x2Matrix(){
        int col = eliminationLevel();
        Element a = data.getElement(data.size - 1, col, ring);
        Element b = extraRow[col];
        left2x2Matrix = SeqUTV.rotationLeft(a, b, ring);
        leftMatrixStorage.add(left2x2Matrix);
    }

    public void eliminateRowAndColumn() {
        Triplet<MatrixS[], MatrixS, MatrixS[]> tempUTV = SeqUTV.eliminateRowAndColumnExtendedResult(data, eliminationLevel(), ring);
        leftMatrix = tempUTV.getValue0();
        data = tempUTV.getValue1();
        rightMatrix = tempUTV.getValue2();

        leftMatrixStorage.add(leftMatrix);
        rightMatrixStorage.add(rightMatrix);
    }

    public void computeSequentialUTV() {
        Triplet<MatrixS[][], MatrixS, MatrixS[][]> UTV = SeqUTV.computeExtendedResult(data, ring);
        leftMatrixStorage.add(UTV.getValue0());
        data = UTV.getValue1();
        rightMatrixStorage.add(UTV.getValue2());
    }

    public boolean receive3DiagonalElementFromDownNeighbor(){
        leftElement = (Element) communicator.receive(coordinator.downNeighbor(), Transport.Tag.ELEMENT);
        return leftElement != null;
    }

    public boolean receive3DiagonalElementFromRightNeighbor(){
        rightElement = (Element) communicator.receive(coordinator.rightNeighbor(), Transport.Tag.ELEMENT);
        return rightElement != null;
    }

    public void send3DiagonalMatrixToAccumulator() {
        Element[] results = new Element[]{data, leftElement, rightElement};
        communicator.send(results, coordinator.mainAccumulator(), Transport.Tag.DIAGONAL_ACCUMULATION);
    }



}
