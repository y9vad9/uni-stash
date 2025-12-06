package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator;

import com.mathpar.matrix.MatrixS;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;

import java.util.List;

public class RightMatrixCollector extends MatrixCollector {
    public RightMatrixCollector(Processor processor) {
        super(processor);
    }

    @Override
    protected List<Integer> generateWaitingList() {
        return coordinator.getProcessorsInRow();
    }

    @Override
    protected MatrixS compute(MatrixS[] matrixChunks) {
        return Utils.collectByColLines(matrixChunks);
    }
}
