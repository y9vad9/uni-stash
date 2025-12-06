package com.mathpar.parallel.dap.ldumw.test;

import com.mathpar.matrix.LSUWM;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.ldumw.LdumwDto;
import com.mathpar.parallel.dap.test.DAPTest;
import mpi.MPIException;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.Random;

public class LdumwFactTest extends DAPTest {

    protected LdumwFactTest() {
        super("LdumwFactTest", 23, 0);
        ring = new Ring("Zp32[]");
        ring.setMOD32(97L);
    }

    @Override
    protected Element[] initData(int size, double density, int maxBits, Ring ring) {
        MatrixS A = matrix(size, density, 5, ring);
        return new Element[]{A, ring.numberONE};
    }

    @Override
    protected Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring) {
        LdumwDto ldumwDto = (LdumwDto) resultData[0];
        //ldumwDto.setD(LdumwFact.invForD(ldumwDto.D(), ring));

        MatrixS A = (MatrixS) initData[0];
        //LOGGER.info("init = " + A);

        //LOGGER.info("ldumwDto L = " + ldumwDto.L() +  "D = " +ldumwDto.D()  + "U = " + ldumwDto.U() );

      //  LOGGER.info("ldumwDtoSequential L = " + ldumwDtoSequential.L() +  "D = " +ldumwDtoSequential.D()  + "U = " + ldumwDtoSequential.U() );

       // LOGGER.info("Check = " + ldumwDto.L().multiply(ldumwDto.D(), ring).multiply(ldumwDto.U(), ring));
/*
        MatrixS[] res=LDUMW.LSUWMIJdetS(A,ring);
        MatrixS L=res[0]; MatrixS D=res[1]; MatrixS U=res[2];
        MatrixS M=res[3]; MatrixS MMM=M; MatrixS W=res[4];
        MatrixS I=res[5];  MatrixS J=res[6]; MatrixS Ann=res[7]; MatrixS Dinv=res[8];

        System.out.println("L="+L);  System.out.println("D="+D);
        System.out.println("U="+U);
        LOGGER.info("Check seq = " + L.multiply(D, ring).multiply(U, ring));*/
        //System.out.println("M="+M);
       // System.out.println("W="+W);


        MatrixS AmLDU =ldumwDto.L().multiply(ldumwDto.D(), ring).multiply(ldumwDto.U(), ring).subtract(A,ring);

       /* if (ldumwDto.equals(ldumwDtoSequential)) {
            return new Pair<>(true, null);
        }*/

        return new Pair<>(AmLDU.isZero(ring), AmLDU.maxAbs(ring));
    }

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        LdumwFactTest test = new LdumwFactTest();
        test.runTests(args);
    }

    @Override
    protected int dispRunsOnOtherProc() {
        return 0;
    }

    @Override
    protected MatrixS matrix(int size, double density, int maxBits, Ring ring){
        /*int [][]mat = {{0, 0,  0,  0, 0,  15, 0,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 21, 0,  0, 0,  12, 0,  8, 0, 14, 3, 0,  0, 0, 0,  0 },
                {0, 7,  0,  0, 0,  0,  0,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  9, 0, 0,  0, 0,  0, 0, 0,  12},
                {0, 0,  0,  0, 0,  0,  2,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 21, 0,  28, 0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  5, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  0, 0, 0,  0, 22, 0, 0, 0,  0 },
                {0, 0,  6,  0, 0,  0,  9,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  2, 0,  0,  0,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  0, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  7, 0, 0,  0, 0,  0, 0, 0,  0 },
                {0, 0,  0,  0, 0,  0,  0,  0, 0, 0,  0, 0,  0, 0, 17, 0 },
                {0, 0,  17, 0, 0,  0,  0,  0, 0, 0,  0, 0,  5, 0, 10, 31}};




               */

        int [][]mat1 =    {{27, 3},
               {20, 5}};

        //int [][]mat1 =    {{7}};
        int [][]mat =       {{0,  0,  0,  0,  0,  27, 0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  16, 0,  0,  0, 0,  14, 0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  16, 0,  0,  0,  0,  0,  21, 0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  10},
 {0,  0,  0,  0,  0,  0,  0,  0, 26, 0,  0,  18, 0,  0,  0,  0,  0,  0,  27, 13, 0,  0,  0,  16, 0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  11, 0,  0 },
 {0,  0,  0,  0,  0,  0,  2,  0, 3,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  27, 0,  0,  0,  0,  0,  0, 10, 0,  0,  0,  0,  13, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  9, 0,  0,  10, 0,  0,  0 },
 {0,  0,  0,  0,  0,  24, 0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  16, 0,  0,  11, 0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  13, 0,  0,  0,  0,  0,  0,  0,  0,  31, 0,  0, 29, 0,  13, 0,  0,  0 },
 {0,  0,  0,  0,  1,  0,  0,  0, 25, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  18, 0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  18, 0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  23, 0,  0,  0,  0,  7,  27, 0, 0,  0,  24, 0,  13, 0 },
 {0,  0,  0,  0,  29, 0,  0,  0, 0,  0,  11, 0,  25, 0,  0,  0,  18, 5,  0,  0,  0,  2,  0,  10, 0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  24, 0,  0,  0,  0,  0,  28, 0,  0, 0,  0,  15, 0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  25, 0,  0,  0,  0,  0, 0,  0,  0,  0,  19, 0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 21, 0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  28},
 {0,  0,  0,  0,  0,  0,  0,  0, 1,  31, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  7,  0,  7,  0, 0,  0,  0,  19, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  18, 0,  2,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  4,  0,  30, 0,  0,  20, 0,  0,  0,  0, 0,  0,  0,  0,  24, 0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  9,  0,  31, 0,  27, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {12, 0,  0,  0,  0,  0,  0,  0, 25, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  25, 0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  5,  22, 0,  0,  0,  0,  1,  0,  0,  0,  30, 0, 0,  0,  0,  0,  0,  0 },
 {14, 0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  19, 16, 0,  0,  0,  0,  0, 0,  0,  21, 0,  0,  0,  0,  5,  0,  0,  0,  21, 0,  0,  30, 0,  0,  0, 0,  0,  12, 0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  20, 0, 0,  0,  0,  0,  0,  0,  0,  0,  0,  6,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  21, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  21, 0,  0,  0,  0, 0,  0,  0,  0,  13, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  6,  0,  16, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  17, 0,  0, 0,  22, 0,  17, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, 0,  0,  0,  0,  0,  0 },
 {0,  0,  0,  0,  7,  0,  0,  0, 0,  0,  0,  0,  0,  0,  0,  20, 0,  0,  22, 0,  0,  0,  0,  0,  0,  0, 0,  17, 0,  0,  0,  0 },
 {0,  0,  0,  0,  0,  0,  0,  0, 11, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,  0,  29, 0,  0,  0 }};
      //  MatrixS matrix = new MatrixS(mat, ring);
         MatrixS matrix = new MatrixS(size, size, density, new int[]{maxBits}, new Random(),ring.numberONE(), ring);
        // LOGGER.trace("bef matrix = " + matrix);
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if(!matrix.getElement(i,j, ring).equals(ring.numberZERO))
//                    matrix.putElement( ring.numberONE.divide(matrix.getElement(i,j, ring),  ring), i, j);
//            }
//        }
        //LOGGER.info("matrix = " + matrix);
        return matrix;
    }

    @Override
    protected Element[] sequentialExecute(Element[] data, Ring ring) {

        MatrixS A = (MatrixS) data[0];
        Element a = data[1];

        LOGGER.info("A= "+A);
        LOGGER.info("a= "+a);

        LdumwDto FF = LSUWM.LDUWMIJdetDto(A, ring);

        LOGGER.info("Check seq = " + FF.L().multiply(FF.D(), ring).multiply(FF.U(), ring));

        return new Element[] {FF, FF.A_n()};
    }
}
