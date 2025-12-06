package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator;

import com.mathpar.matrix.MatrixS;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;

import java.util.List;

public class LeftMatrixCollector extends MatrixCollector {
    public LeftMatrixCollector(Processor processor) {
        super(processor);
    }

    @Override
    protected List<Integer> generateWaitingList() {
        return coordinator.getProcessorsInColumn();
    }

    @Override
    protected MatrixS compute(MatrixS[] matrixChunks) {
        return Utils.collectByRowLines(matrixChunks);
    }
}
