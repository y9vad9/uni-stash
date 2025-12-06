package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator.Communicator;

public interface RunnableProcessor {

    void tick();
    void run();
    boolean isDone();
    int getRank();
    void setCommunicator(Communicator communicator);
}
