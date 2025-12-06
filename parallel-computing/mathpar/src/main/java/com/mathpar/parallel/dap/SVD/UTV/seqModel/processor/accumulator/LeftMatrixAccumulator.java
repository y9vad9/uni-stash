package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;

import java.util.List;

public class LeftMatrixAccumulator extends MatrixAccumulator {

    public LeftMatrixAccumulator(Processor processor) {
        super(processor);
    }

    @Override
    protected List<Integer> getWaitingList() {
        return coordinator.getProcessorsInRowToDiagonal();
    }

    @Override
    protected MatrixS genAccumulator() {
        return Utils.rowLineMatrix(
                coordinator.processorsInRow(),
                processor.blockSize(),
                coordinator.getCurrentProcessorRow(),
                processor.getRing());
    }


    @Override
    protected MatrixS multiplyMatrix(MatrixS[] matrices, MatrixS matrix, Ring ring) {
        return Utils.multiply(matrices, matrix, ring);
    }

    @Override
    protected int indexOfChunk(int prank) {
        return coordinator.getProcessorColumn(prank);
    }

    @Override
    protected int nextNeighbor() {
        return coordinator.upNeighbor();
    }

    @Override
    protected int previousNeighbor() {
        return coordinator.downNeighbor();
    }

    @Override
    protected Element[] multiplyLastLine(MatrixS matrix2x2, Element[] line, MatrixS mainMatrix, Ring ring) {
        return Utils.multiplyLastRow(matrix2x2, line, mainMatrix, ring);
    }

    @Override
    protected void putFirstLine(MatrixS matrix, Element[] line, Ring ring) {
        Utils.putRow(matrix, line, 0, ring);
    }

    @Override
    protected Element[] getFirstLine(MatrixS matrix, Ring ring) {
        return Utils.getRow(matrix, 0, ring);
    }
}
