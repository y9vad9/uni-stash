package com.mathpar.parallel.dap.SVD.UTV.seqModel.setup.order;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RunnableProcessor;

import java.util.List;

public interface ProcessorOrder {


    List<RunnableProcessor> apply(List<RunnableProcessor> processors);

}
