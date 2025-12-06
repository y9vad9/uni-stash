package com.mathpar.parallel.dap.SVD.UTV.seqModel.setup;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.MPIVRankTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

public class NMPISetup extends NSetup {
    public NMPISetup(int processors, Ring ring) {
        super(processors, ring);
    }

    @Override
    protected Transport defineGlobalTransport() {
        return new MPIVRankTransport(this::realRank);
    }
}
