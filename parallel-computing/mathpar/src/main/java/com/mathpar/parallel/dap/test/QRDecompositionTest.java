package com.mathpar.parallel.dap.test;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.QR.SeqBlockQR;
import com.mathpar.parallel.dap.core.DispThread;
import mpi.MPIException;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;


//  ./runclass.sh -np 4 com.mathpar.parallel.dap.test.QRDecompositionTest
//mpirun -np 4 java -cp /Users/apple/openmpi/lib/mpi.jar:target/qr-test.jar -Xmx4g com.mathpar.parallel.dap.test.QRDecompositionTest -size=64:256 -leaf=32  -density=80 -nocheck > debug.log
// mpirun -np 4 java -cp /Users/apple/openmpi/lib/mpi.jar:target/qr-test.jar -Xmx4g com.mathpar.parallel.dap.test.QRDecompositionTest -size=64:1024 -leaf=32  -density=1,5,10,50,100 > debug.log

public class QRDecompositionTest extends DAPTest {

    protected QRDecompositionTest() {
        this("qr_test");
    }

    protected QRDecompositionTest(String fileName) {
        super(fileName, 4, 0);
    }

    @Override
    protected MatrixS[] initData(int size, double density, int maxBits, Ring ring) {
        return new MatrixS[]{matrix(size, density, maxBits, ring)};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        MatrixS matrix = (MatrixS) initData[0];
        MatrixS Q = (MatrixS) resultData[0];
        MatrixS R = (MatrixS) resultData[1];

        runTask(dispThread, 0, 0, args, new Element[]{Q, R}, ring);
        Element[] results = dispThread.getResult();
        MatrixS QR = (MatrixS) results[0];

        MatrixS check = QR.subtract(matrix, ring);
        boolean succeed = check.isZero(ring);

        Element element = check.max(ring);

        return new Pair<>(succeed, element);
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        return SeqBlockQR.compute((MatrixS) data[0], ring);
    }

    public static void main(String[] args) throws InterruptedException, MPIException {
        QRDecompositionTest test = new QRDecompositionTest();

        test.runTests(args);
    }
}
