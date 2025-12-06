package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.QR.SeqBlockQR;
import com.mathpar.parallel.dap.QR.SeqQR;
import mpi.MPIException;

public class QRDecompositionSeqTest extends QRDecompositionTest {
    public static final MpiLogger LOGGER = MpiLogger.getLogger(QRDecompositionSeqTest.class);

    public QRDecompositionSeqTest() {
        super("qr_seq");
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {

        return SeqBlockQR.sequentialQR((MatrixS) data[0], ring);
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        QRDecompositionTest test = new QRDecompositionSeqTest();

        test.runTests(args);
    }
}
