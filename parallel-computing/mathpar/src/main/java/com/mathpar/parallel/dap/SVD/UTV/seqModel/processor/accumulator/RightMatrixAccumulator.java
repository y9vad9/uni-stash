package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;

import java.util.List;

public class RightMatrixAccumulator extends MatrixAccumulator {
    public RightMatrixAccumulator(Processor processor) {
        super(processor);
    }

    @Override
    protected MatrixS multiplyMatrix(MatrixS[] matrices, MatrixS matrix, Ring ring) {
        return Utils.multiply(matrix, matrices, ring);
    }

    @Override
    protected int indexOfChunk(int prank) {
        return coordinator.getProcessorRow(prank);
    }

    @Override
    protected List<Integer> getWaitingList() {
        return coordinator.getProcessorsInColumnToDiagonal();
    }

    @Override
    protected MatrixS genAccumulator() {
        return Utils.colLineMatrix(
                coordinator.processorsInRow(),
                processor.blockSize(),
                coordinator.getCurrentProcessorColumn(),
                processor.getRing());
    }

    @Override
    protected int nextNeighbor() {
        return coordinator.leftNeighbor();
    }

    @Override
    protected int previousNeighbor() {
        return coordinator.rightNeighbor();
    }

    @Override
    protected Element[] multiplyLastLine(MatrixS matrix2x2, Element[] line, MatrixS mainMatrix, Ring ring) {
        return Utils.multiplyLastColumn(mainMatrix, line, matrix2x2, ring);
    }

    @Override
    protected void putFirstLine(MatrixS matrix, Element[] line, Ring ring) {
        Utils.putColumn(matrix, line, 0, ring);
    }

    @Override
    protected Element[] getFirstLine(MatrixS matrix, Ring ring) {
        return Utils.getCol(matrix, 0, ring);
    }
}
