package com.mathpar.parallel.dap.ldumw;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.LSUWM;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.multiply.MatrixS.MatrSMult4;

import java.util.ArrayList;

import static com.mathpar.matrix.LSUWM.StoUnit;

public class LdumwFact extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(LdumwFact.class);
    private static int[][] arcs_ = new int[][]{
            {1, 0, 0, 1, 4, 1, 2, 1, 1, 3, 1, 1, 3, 4, 2, 4, 2, 1, 4, 4, 2, 5, 2, 1, 9, 3, 1, 9, 4, 4, 11, 4, 4, 12, 4, 3, 16, 4, 4},//0. inputFunction
            {2, 0, 0, 3, 0, 0, 4, 0, 0, 5, 0, 0, 6, 1, 1, 7, 0, 0, 8, 1, 1, 9, 0, 0, 10, 0, 0, 11, 0, 3, 14, 0, 0, 16, 0, 2, 19, 0, 0},//1. F11 = LSU
            {12, 0, 2},//2. X_U2
            {6, 1, 0, 7, 0, 2},//3. A12_0  and  A12_2
            {7, 0, 1, 8, 1, 0},//4. A21_0  and  A21_2
            {16, 0, 3},//5. X_L3
            {11, 0, 1, 14, 0, 1, 16, 0, 1, 17, 0, 1, 19, 0, 2}, //6. F12(am)
            {9, 0, 2}, //7. A22_0
            {9, 0, 3, 10, 0, 1, 11, 0, 2, 12, 0, 0, 13, 0, 0, 18, 0, 0, 19, 0, 1}, //8. F21(al)
            {11, 1, 0, 12, 0, 1, 13, 0, 1}, //9. A22_1  and  X_A22_2
            {19, 0, 5}, //10. UU
            {14, 0, 2, 15, 1, 1, 15, 2, 0, 17, 0, 2, 19, 0, 4, 19, 3, 8, 19, 1, 9}, //11. lambda  and  as  and  A22_3  and  invD12hat
            {19, 0, 6}, //12. U2
            {16, 0, 0}, //13. Y_L3 (L3H2)
            {19, 0, 12}, //14. X_L
            {17, 0, 0, 18, 0, 1, 19, 0, 3}, //15. F22
            {19, 0, 11}, //16. L3
            {19, 0, 7}, //17. X_U
            {19, 0, 10}, //18. LL
            {}};//19. OutputFunction

    public LdumwFact() {
        arcs = arcs_;
        type = 23;
        number = cnum++;
        inputDataLength = 2;
        outputDataLength = 2;
        inData = new Element[inputDataLength];
        outData = new Element[outputDataLength];
        resultForOutFunctionLength = 13;
    }

    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<>();
        // step 1
        amin.add(new LdumwFact());
        // step 2
        amin.add(new MatrSMult4());
        amin.get(1).key = 102;
        // step 3
        amin.add(new MatrSMult4());
        amin.get(2).key = 103;
        // step 4
        amin.add(new MatrSMult4());
        amin.get(3).key = 104;
        // step 5
        amin.add(new MatrSMult4());
        amin.get(4).key = 105;
        // step 6
        amin.add(new LdumwFact());
        // step 7
        amin.add(new MatrSMult4());
        amin.get(6).key = 107;
        // step 8
        amin.add(new LdumwFact());
        // step 9
        amin.add(new MatrSMult4());
        amin.get(8).key = 109;
        // step 10
        amin.add(new MatrSMult4());
        amin.get(9).key = 110;
        // step 11
        amin.add(new MatrSMult4());
        amin.get(10).key = 112;
        // step 12
        amin.add(new MatrSMult4());
        amin.get(11).key = 113;
        // step 13
        amin.add(new MatrSMult4());
        amin.get(12).key = 114;
        // step 14
        amin.add(new MatrSMult4());
        amin.get(13).key = 116;
        // step 15
        amin.add(new LdumwFact());
        // step 16
        amin.add(new MatrSMult4());
        amin.get(15).key = 118;
        // step 17
        amin.add(new MatrSMult4());
        amin.get(16).key = 121;
        // step 18
        amin.add(new MatrSMult4());
        amin.get(17).key = 123;

        return amin;
    }


    @Override
    public void sequentialCalc(Ring ring) {
        MatrixS A = (MatrixS) inData[0];

        Element a = inData[1];
        //LOGGER.info("A = " + A);
        //LOGGER.info("a = " + a);
        LdumwDto FF = LSUWM.LDUWMIJdetD(A, a, ring);

       /* LOGGER.info("FF = " + FF);
        LOGGER.info("FFdhat = " + FF.Dhat());
        LOGGER.info("M = " + FF.M());
        LOGGER.info("W = " + FF.W());
        LOGGER.info("I = " + FF.I());
        LOGGER.info("J = " + FF.J());
        LOGGER.info("a_n = " + FF.a_n);
        LOGGER.info("Dinv = " + FF.D_inv);
        LOGGER.info("Check = " + FF.L().multiply(FF.D(), ring).multiply(FF.U(), ring));*/

        outData[0] = FF;
        outData[1] = FF.A_n();
    }

    @Override
    public Element[] inputFunction(Element[] input, Amin amin, Ring ring) {
        MatrixS A = (MatrixS) input[0];
        Element a = input[1];
        MatrixS[] split = A.split();
        Element[] res = new Element[5];
        System.arraycopy(split, 0, res, 0, 4);

        res[4] = a;
        return res;
    }

    public Element[] outputFunction(Element[] input, Ring ring) {
        Element ONE = ring.numberONE;
        MatrixS A = (MatrixS) inData[0];
        Element a = inData[1];
        Element a_n;
        MatrixS L;
        MatrixS D;
        MatrixS U;
        MatrixS M;
        MatrixS W;
        MatrixS Dhat;
        MatrixS Dbar;
        MatrixS I;
        MatrixS J;
        MatrixS Jbar;
        MatrixS Ibar;
        LdumwDto ldumw;
        int n = A.size;
        if (A.isZero(ring)) {
            D = MatrixS.zeroMatrix(n);
            L = MatrixS.scalarMatrix(n, ONE, ring);
            U = MatrixS.scalarMatrix(n, ONE, ring);
            M = MatrixS.scalarMatrix(n, a, ring);
            W = MatrixS.scalarMatrix(n, a, ring);
            Element aInv = (a.isOne(ring) || a.isMinusOne(ring))
                    ? a : LSUWM.doFraction(ring.numberONE, a, ring);
            Dhat = MatrixS.scalarMatrix(n, aInv, ring);
            a_n = a;
            Dbar = MatrixS.scalarMatrix(n, ONE, ring);
            I = MatrixS.zeroMatrix(n);
            J = MatrixS.zeroMatrix(n);
            Jbar = MatrixS.scalarMatrix(n, ONE, ring);
            Ibar = MatrixS.scalarMatrix(n, ONE, ring);

            ldumw = new LdumwDto(
                    L, D, Dhat, Dbar,
                    U, M, W, I, Ibar,
                    J, Jbar, a_n
            );
            return new Element[]{ldumw, ldumw.A_n()};
        }
        if (n == 1) {
            a_n = A.getElement(0, 0, ring);
            Element aan = a_n.multiply(a, ring);
            Element an_an = a_n.multiply(a_n, ring);
            L = new MatrixS(a_n);
            D = new MatrixS(aan);
            //System.out.println("DDropOut11 = " + D);
            Element a2Inv = (an_an.isOne(ring) || an_an.isMinusOne(ring))
                    ? an_an : LSUWM.doFraction(ring.numberONE, an_an, ring);
            Dhat = new MatrixS(a2Inv);
            Dbar = MatrixS.zeroMatrix(n);
            U = new MatrixS(a_n);
            M = new MatrixS(a_n);
            W = new MatrixS(a_n);
            Jbar = Dbar;
            Ibar = Dbar;
            I = new MatrixS(ONE);
            J = I;
            ldumw = new LdumwDto(
                    L, D, Dhat, Dbar,
                    U, M, W, I, Ibar,
                    J, Jbar, a_n
            );
            return new Element[]{ldumw, ldumw.A_n()};
        }


        LdumwDto F11 = (LdumwDto) input[0];
        LdumwDto F21 = (LdumwDto) input[1];
        LdumwDto F12 = (LdumwDto) input[2];
        LdumwDto F22 = (LdumwDto) input[3];
        Element lambda = input[4];

        MatrixS UU = (MatrixS) input[5];
        MatrixS U2 = (MatrixS) input[6];
        MatrixS X_U = (MatrixS) input[7];
        MatrixS invD12hat = (MatrixS) input[8];
        Element as = input[9];

        MatrixS LL = (MatrixS) input[10];
        MatrixS L3 = (MatrixS) input[11];
        MatrixS X_L = (MatrixS) input[12];

        Element lambda2 = lambda.multiply(lambda, ring);
        L = MatrixS.join(new MatrixS[]{X_L, MatrixS.zeroMatrix(), L3, LL});
        D = MatrixS.join(new MatrixS[]{
                F11.D(), F12.D().multiplyByNumber(lambda2, ring),
                F21.D(), F22.D()});

       // System.out.println("DDropOut = " + D);

        U = MatrixS.join(new MatrixS[]{UU, U2, MatrixS.zeroMatrix(), X_U});


        ldumw = new LdumwDto(L, D, U, F22.A_n());
        ldumw.IJMap(a, ring);

        //TODO algo by standard
        Element am = F12.A_n();
        Element al = F21.A_n();
        Element ak = F11.A_n();
        a_n = ldumw.A_n();

        MatrixS L1_m1 = invD12hat // Done
                .multiply(F12.M(), ring) // Done
                .multiply(F11.Dhat() // Done
                        .multiply(F11.M(), ring), ring); // Done
        MatrixS L3prim = L3.negate(ring).multiply(L1_m1, ring);
        MatrixS DhUnit = StoUnit(D, ring.numberONE, ring);

        DhUnit = DhUnit.add(ldumw.Dbar(), ring).transpose();

        MatrixS[] Eprim = DhUnit.split();

        MatrixS U2prim = F11.W()
                .multiply(F11.Dhat()
                        .multiply(F21.W()
                                .multiply(F21.Dhat(), ring), ring), ring);//Done U1_m1
        U2prim = U2prim.multiply(U2.negate(ring), ring);

        // Du=
        MatrixS D11prim = StoUnit(F11.D(), ring.numberONE, ring).add(F11.Dbar(), ring);
        MatrixS D12prim = StoUnit(F21.D(), ak, ring).add(F21.Dbar().multiplyByNumber(a, ring), ring);
        MatrixS D21prim = StoUnit(F12.D(), al, ring).add(F12.Dbar().multiplyByNumber(a, ring), ring);
        MatrixS D22prim = StoUnit(F22.D(), as, ring).add(F22.Dbar().multiplyByNumber(a, ring), ring);

        MatrixS V11A = F21.W().multiply(F21.Dbar(), ring).multiply(Eprim[0], ring);
        MatrixS V11B = F11.W().multiply(D11prim, ring).multiply(V11A, ring);
        MatrixS V11 = V11B.multiplyByNumber(LSUWM.doFraction(a_n, ak.multiply(al, ring), ring), ring);
        MatrixS V12A = F21.W().multiply(D12prim, ring).multiply(Eprim[1], ring);
        MatrixS V12B = F11.W().multiply(F11.Dbar(), ring).multiply(V12A, ring);
        MatrixS V12 = V12B.multiplyByNumber(LSUWM.doFraction(a_n, ak.multiply(al, ring).multiply(a, ring), ring), ring);
        MatrixS V21A = F12.W().multiply(D21prim, ring).multiply(F22.W(), ring).multiply(F22.Dbar(), ring);
        MatrixS V21B = V21A.multiply(Eprim[2], ring);
        MatrixS V21 = V21B.multiplyByNumber(LSUWM.doFraction(ring.numberONE, am.multiply(a, ring), ring), ring);
        MatrixS V22A = F12.W().multiply(F12.Dbar(), ring).multiply(F22.W(), ring).multiply(D22prim, ring);
        MatrixS V22B = V22A.multiply(Eprim[3], ring);
        MatrixS V22 = V22B.multiplyByNumber(LSUWM.doFraction(ring.numberONE, a.multiply(am, ring), ring), ring);


        W = MatrixS.join(new MatrixS[]{V11.add(U2prim.multiply(V21, ring), ring),
                V12.add(U2prim.multiply(V22, ring), ring), V21, V22});

        MatrixS N11A = Eprim[0].multiply(F12.Dbar(), ring).multiply(F12.M(), ring);
        MatrixS N11B = N11A.multiply(D11prim, ring).multiply(F11.M(), ring);
        MatrixS N11 = N11B.multiplyByNumber(LSUWM.doFraction(a_n, ak.multiply(am, ring), ring), ring);
        MatrixS N21A = Eprim[2].multiply(D21prim, ring).multiply(F12.M(), ring);
        MatrixS N21B = N21A.multiply(F11.Dbar(), ring).multiply(F11.M(), ring);
        MatrixS N21 = N21B.multiplyByNumber(LSUWM.doFraction(a_n, ak.multiply(am, ring).multiply(a, ring), ring), ring);
        MatrixS N12A = Eprim[1].multiply(F22.Dbar(), ring).multiply(F22.M(), ring);
        MatrixS N12B = N12A.multiply(D12prim, ring).multiply(F21.M(), ring);
        MatrixS N12 = N12B.multiplyByNumber(LSUWM.doFraction(ring.numberONE, al.multiply(a, ring), ring), ring);
        MatrixS N22A = Eprim[3].multiply(D22prim, ring).multiply(F22.M(), ring);
        MatrixS N22B = N22A.multiply(F21.Dbar(), ring).multiply(F21.M(), ring);
        MatrixS N22 = N22B.multiplyByNumber(LSUWM.doFraction(ring.numberONE, a.multiply(al, ring), ring), ring);

        M = MatrixS.join(new MatrixS[]{N11.add(N12.multiply(L3prim, ring), ring),
                N12, N21.add(N22.multiply(L3prim, ring), ring), N22});

        ldumw.setM(M);
        ldumw.setW(W);
        //ldumw.setD(LdumwFact.invForD(ldumw.D(), ring));
       // LOGGER.info("in output");
        return new Element[]{ldumw, ldumw.A_n()};
    }

    public static MatrixS invForD(MatrixS Di, Ring ring) {
        int len = Di.M.length;
        Element[][] MI = new Element[len][];
        for (int i = 0; i < len; i++) {
            if (Di.col[i].length > 0) {
                Element ee = Di.M[i][0];
                Element enew;
                if (ee.isNegative()) {
                    enew = (ee.isMinusOne(ring)) ? ee :
                            LSUWM.doFraction(ring.numberMINUS_ONE, ee.negate(ring), ring);
                } else {
                    enew = (ee.isOne(ring)) ? ee : LSUWM.doFraction(ring.numberONE, ee, ring);
                }
                MI[i] = new Element[]{enew};
            } else {
                MI[i] = Di.M[i];
            }
        }
        return new MatrixS(Di.size, Di.colNumb, MI, Di.col);
    }

    @Override
    public void setLeafSize(int dataSize) {
        leafSize = dataSize;
    }


    @Override
    public Element[] recentCalc(Ring ring) {
        LdumwDto ldumwDto = (LdumwDto) outData[0];
        ldumwDto.setD(LdumwFact.invForD(ldumwDto.D(), ring));
        outData[0] = ldumwDto;
        return outData;
    };
}
