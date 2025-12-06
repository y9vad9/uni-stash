package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.QR.SeqQR;
import mpi.MPIException;


// mpirun -np 4 java -cp /Users/apple/openmpi/lib/mpi.jar:target/qr-test.jar -Xmx4g com.mathpar.parallel.dap.test.QRDecompositionSparseMatrixTest -size=1024 -leaf=32  -density=10:100  > debug.log
public class QRDecompositionSparseMatrixTest extends QRDecompositionTest {

    public static final MpiLogger LOGGER = MpiLogger.getLogger(QRDecompositionSparseMatrixTest.class);

    public QRDecompositionSparseMatrixTest() {
        super("qr_sparse");
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        SeqQR seqQR = new SeqQR();
        LOGGER.info("Seq execute");
        return seqQR.compute((MatrixS) data[0], ring);
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        QRDecompositionTest test = new QRDecompositionSparseMatrixTest();

        test.runTests(args);
    }
}
