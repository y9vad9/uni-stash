package com.mathpar.parallel.dap.test;

import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.setup.NMPISetup;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.setup.ProcessorsSetup;
import mpi.MPIException;

// mpirun -np 4 java -cp /Users/apple/openmpi/lib/mpi.jar:target/qr-test.jar -Xmx4g com.mathpar.parallel.dap.test.UTVnTest -size=16 -density=100 > debug.log
public class UTVnTest extends UTVn2Test {
    public UTVnTest() {
        super("utv_n");
    }

    @Override
    protected ProcessorsSetup getSetup(int processors, Ring ring) {
        return new NMPISetup(processors, ring);
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        DAPTest test = new UTVnTest();

        test.runTests(args);
    }
}
