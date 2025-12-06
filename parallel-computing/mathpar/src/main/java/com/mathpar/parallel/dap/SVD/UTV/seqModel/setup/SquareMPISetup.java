package com.mathpar.parallel.dap.SVD.UTV.seqModel.setup;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.MPITransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

public class SquareMPISetup extends SquareSetup {
    public SquareMPISetup(int processors, Ring ring) {
        super(processors, ring);
    }

    @Override
    protected Transport defineGlobalTransport() {
        return new MPITransport();
    }
}
