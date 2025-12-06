package com.mathpar.parallel.dap.SVD.UTV.seqModel.setup;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RunnableProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

import java.util.List;

public abstract class ProcessorsSetup {

    protected Transport globalTransport;
    protected Ring ring;
    protected int processorsInRow;

    public ProcessorsSetup(int processors, Ring ring) {
        this.processorsInRow = processors;
        this.ring = ring;
        this.globalTransport = defineGlobalTransport();
    }

    public abstract RunnableProcessor getProcessor(int rank);

    public abstract List<RunnableProcessor> getProcessorForSimulation();

    protected abstract Transport defineGlobalTransport();

    public Transport getGlobalTransport() {
        return globalTransport;
    }
}
