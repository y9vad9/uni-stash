package com.mathpar.parallel.dap.multiply.MatrixS;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.AdjMatrixS;
import com.mathpar.matrix.LSUWM;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.core.Drop;
import com.mathpar.parallel.dap.ldumw.LdumwDto;

import java.util.ArrayList;

public class MatrSMult4 extends Drop {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(MatrSMult4.class);

    private static int[][] _arcs = new int[][]{
            {1, 0, 0, 1, 4, 1, 1, 1, 2, 1, 6, 3, 2, 0, 0, 2, 5, 1, 2, 1, 2, 2, 7, 3,
                    3, 2, 0, 3, 4, 1, 3, 3, 2, 3, 6, 3, 4, 2, 0, 4, 5, 1, 4, 3, 2, 4, 7, 3},
            {5, 0, 0},
            {5, 0, 1},
            {5, 0, 2},
            {5, 0, 3},
            {}};

    public MatrSMult4() {

        //Дроп має тип 5
        type = 5;

        //унікальний номер дропа
        number = cnum++;
        arcs = _arcs;
    }

    //Розгортання аміну з дропами, відповідно до графу, для обрахунку поточного дропа.
    @Override
    public ArrayList<Drop> doAmin() {
        ArrayList<Drop> amin = new ArrayList<Drop>();

        amin.add(new MatrSMultiplyScalar());
        amin.add(new MatrSMultiplyScalar());
        amin.add(new MatrSMultiplyScalar());
        amin.add(new MatrSMultiplyScalar());

        return amin;
    }

    /**
     * key that starts on 77 (M in ascii) is a mldtsv custom key,
     * the next 2 digits form the number of the step
     */

    @Override
    public void setVars(){
        switch (key){
            // a*b
            case(0):
            // -a*b
            case(1):
            case(7708):
            case(7709):
            case(7713):
            case(7720):
            case (102):
            case (114):
            case (110):
            case (123): {
                inputDataLength = 2;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }

            case(2): {
                inputDataLength = 2;
                outputDataLength = 2;
                resultForOutFunctionLength = 5;
                break;
            }

            case(7702):
            case(7707):
            case(7710):
            case(7718):
            case(7724): {
                inputDataLength = 3;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }

            case(7703): {
                inputDataLength = 4;
                outputDataLength = 2;
                resultForOutFunctionLength = 4;
                break;
            }

            case(7705): {
                inputDataLength = 6;
                outputDataLength = 1;
                resultForOutFunctionLength = 5;
                break;
            }

            case(7711):
            case(7721):
            case(7725): {
                inputDataLength = 4;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }

            case(7712): {
                inputDataLength = 7;
                outputDataLength = 3;
                resultForOutFunctionLength = 5;
                break;
            }
            //drop 5
            case (4):
            case(7714):
            case(7717):
            case(7722):
            case(7723): {
                inputDataLength = 6;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }

            case(7716): {
                inputDataLength = 7;
                outputDataLength = 1;
                resultForOutFunctionLength = 5;
                break;
            }
            case (103): {
                inputDataLength = 3;
                outputDataLength = 2;
                resultForOutFunctionLength = 4;
                break;
            }
            case (104): {
                inputDataLength = 3;
                outputDataLength = 2;
                resultForOutFunctionLength = 4;
                break;
            }
            case (105): {
                inputDataLength = 2;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }
            case (107):
            case (116):
            case (121): {
                inputDataLength = 3;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }
            case (109): {
                inputDataLength = 5;
                outputDataLength = 2;
                resultForOutFunctionLength = 5;
                break;
            }
            case (112): {
                inputDataLength = 5;
                outputDataLength = 4;
                resultForOutFunctionLength = 8;
                break;
            }
            case (113): {
                inputDataLength = 4;
                outputDataLength = 1;
                resultForOutFunctionLength = 5;
                break;
            }
            case (118): {
                inputDataLength = 5;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }

            case(7719): {
                inputDataLength = 5;
                outputDataLength = 1;
                resultForOutFunctionLength = 4;
                break;
            }

        }

        inData = new Element[inputDataLength];
        outData = new Element[outputDataLength];

        //LOGGER.info(inData);
    }

    //Послідовний обрахунок листових вершин
    @Override
    public void sequentialCalc(Ring ring) {
        // LOGGER.info("in sequentialCalc indata = " + inData[0] + ",  "+inData[1]);


        switch (key){
            case(0):
            case(7713): {
                MatrixS A = (MatrixS) inData[0];
                MatrixS B = (MatrixS) inData[1];
                LOGGER.info("bef multiplyRecursive " + (System.currentTimeMillis()- DispThread.executeTime));
                outData[0] =A.multiplyRecursive(B, ring);
                LOGGER.info("after multiplyRecursive " + (System.currentTimeMillis()- DispThread.executeTime));
                //LOGGER.info("7713 mult = " + outData[0]);
                break;
            }
            case(1): {
                MatrixS A = (MatrixS) inData[0];
                MatrixS B = (MatrixS) inData[1];
                outData[0] = A.multiplyRecursive(B, ring).negate(ring);
                break;
            }
            case(2): {
                MatrixS b = ((MatrixS) inData[0]).transpose();
                outData[1] = b;
                MatrixS bbT =  b.multiplyRecursive((MatrixS) inData[0], ring);
                outData[0] = ((MatrixS)inData[1]).subtract(bbT, ring);
                break;
            }
            case(7702):
            case(7718): {
                MatrixS A = (MatrixS) inData[0];
                MatrixS B = (MatrixS) inData[1];
                Element d = inData[2];
                outData[0] = A.multiplyDivRecursive(B, d.negate(ring), ring);
               // LOGGER.info("7718, 7702 mult = " + outData[0]);
                break;
            }

            case(7703): {
                AdjMatrixS m11 = (AdjMatrixS) inData[0];
                MatrixS M12 = (MatrixS) inData[1];
                Element d0 = inData[2];
                Element finalN = inData[3];
                MatrixS M12_1 = m11.A.multiplyDivRecursive(M12, d0, ring);
                MatrixS M12_2 = M12_1.multiplyLeftI(Array.involution(m11.Ei, (int) finalN.value));
                outData[0] = M12_1;
                outData[1] = M12_2;
               // LOGGER.info("7703 mult = " + outData[0]);
               // LOGGER.info("7703 mult = " + outData[1]);
                break;
            }

            case(7705): {
                MatrixS M22 = (MatrixS) inData[0];
                Element d11 = inData[1];
                MatrixS M21 = (MatrixS) inData[2];
                MatrixS M12_1 = (MatrixS) inData[3];
                AdjMatrixS m11 = (AdjMatrixS) inData[4];
                Element d0 = inData[5];
                outData[0] = ((M22.multiplyByNumber(d11, ring))
                        .subtract(M21.multiplyRecursive(M12_1.multiplyLeftE(m11.Ej, m11.Ei), ring), ring))
                        .divideByNumber(d0, ring);
               // LOGGER.info("7705 mult = " + outData[0]);
                break;
            }

            case(7707): {
                MatrixS A = ((AdjMatrixS) inData[0]).S;
                MatrixS B = (MatrixS) inData[1];
                Element d = inData[2];
                outData[0] = A.multiplyDivRecursive(B, d.negate(ring), ring);
              //  LOGGER.info("7707 mult = " + outData[0]);
                break;
            }

            case(7708):
            case(7720): {
                MatrixS A1 = ((AdjMatrixS) inData[0]).A;
                MatrixS A2 = ((AdjMatrixS) inData[1]).A;
                outData[0] = A1.multiplyRecursive(A2, ring);
              //  LOGGER.info("7720, 7708 mult = " + outData[0]);
                break;
            }

            case(7709): {
                MatrixS A21 = ((AdjMatrixS) inData[0]).A;
                MatrixS M22_1 = (MatrixS) inData[1];
                outData[0] = A21.multiplyRecursive(M22_1, ring).negate(ring);
              //  LOGGER.info("7709 mult = " + outData[0]);
                break;
            }

            case(7710): {
                AdjMatrixS m11 = (AdjMatrixS) inData[0];
                AdjMatrixS m21 = (AdjMatrixS) inData[1];
                Element d11 = inData[2];
                outData[0] = m11.S.multiplyDivRecursive(m21.A.multiplyLeftE(m21.Ej, m21.Ei), d11, ring);
              //  LOGGER.info("7710 mult = " + outData[0]);
                break;
            }

            case(7711):
            case(7721): {
                MatrixS M22_1 = (MatrixS) inData[0];
                MatrixS A1 = (MatrixS) inData[1];
                AdjMatrixS m12 = (AdjMatrixS) inData[2];
                Element d11 = inData[3];
                outData[0] = M22_1.multiplyDivRecursive(A1.multiplyLeftE(m12.Ej, m12.Ei), d11, ring);
               // LOGGER.info("7721, 7711 mult = " + outData[0]);
                break;
            }

            case(7712): {
                MatrixS B = (MatrixS) inData[0];
                MatrixS y12 = (MatrixS) inData[1];
                Element d11 = inData[2];
                Element d21 = inData[3];
                Element d12 = inData[4];
                AdjMatrixS m21 = (AdjMatrixS) inData[5];
                Element finalN = inData[6];
                Element d11_2 = d11.multiply(d11, ring);
                MatrixS M22_2 = B.multiplyDivRecursive(y12, d11_2, ring);
                Element ds = d12.multiply(d21, ring).divide(d11, ring);
                MatrixS M22_3 = M22_2.multiplyLeftI(Array.involution(m21.Ei, (int) finalN.value));
                outData[0] = M22_2;
                outData[1] = ds;
                outData[2] = M22_3;
               // LOGGER.info("7712 mult = " + outData[0]);
               // LOGGER.info("7712 mult = " + outData[2]);
                break;
            }

            case(7714): {
                MatrixS M21 = (MatrixS) inData[0];
                AdjMatrixS m11 = (AdjMatrixS) inData[1];
                Element d0 = inData[2];
                Element d12 = inData[3];
                MatrixS K2 = (MatrixS) inData[4];
                Element d11 = inData[5];
                outData[0] = (M21.multiplyDivMulRecursive(m11.A.multiplyLeftE(m11.Ej, m11.Ei), d0, d12, ring).add(K2, ring))
                        .divideByNumber(d11.negate(ring), ring);
               // LOGGER.info("7714 mult = " + outData[0]);
                break;
            }

            case(7716): {
                MatrixS Q1 = (MatrixS) inData[0];
                MatrixS M12_1 = (MatrixS) inData[1];
                AdjMatrixS m11 = (AdjMatrixS) inData[2];
                Element d21 = inData[3];
                Element d11 = inData[4];
                MatrixS y12 = (MatrixS) inData[5];
                AdjMatrixS m12 = (AdjMatrixS) inData[6];
                outData[0] = (
                ((Q1.subtract((M12_1.multiplyLeftI(m11.Ei).multiplyByNumber(d21, ring)), ring))
                    .divideByNumber(d11, ring).multiplyRecursive(y12, ring))
                        .add((m12.S).multiplyByNumber(d21, ring), ring)
                )
                        .divideByNumber(d11, ring);

               // LOGGER.info("7716 mult = " + outData[0]);
                break;
            }

            case(7717): {
                MatrixS A1 = (MatrixS) inData[0];
                MatrixS M12_1 = (MatrixS) inData[1];
                AdjMatrixS m11 = (AdjMatrixS) inData[2];
                AdjMatrixS m12 = (AdjMatrixS) inData[3];
                Element d11 = inData[4];
                Element d22 = inData[5];
                outData[0] = (A1.subtract((M12_1.multiplyLeftI(m11.Ei)).
                        multiplyDivRecursive(A1.multiplyLeftE(m12.Ej, m12.Ei), d11, ring), ring)
                ).divideMultiply(d11, d22, ring);
              //  LOGGER.info("7717 mult = " + outData[0]);
                break;
            }

            case(7719): {
                MatrixS M22_2 = (MatrixS) inData[0];
                AdjMatrixS m21 = (AdjMatrixS) inData[1];
                MatrixS y22 = (MatrixS) inData[2];
                Element ds = inData[3];
                AdjMatrixS m22 = (AdjMatrixS) inData[4];
                outData[0] = ((M22_2.multiplyLeftI(m21.Ei))
                        .multiplyDivRecursive(y22, ds.negate(ring), ring)).add(m22.S, ring);
              //  LOGGER.info("7719 mult = " + outData[0]);
                break;
            }

            case(7722): {
                MatrixS A2 = (MatrixS) inData[0];
                MatrixS M22_2 = (MatrixS) inData[1];
                AdjMatrixS m21 = (AdjMatrixS) inData[2];
                AdjMatrixS m22 = (AdjMatrixS) inData[3];
                Element ds = inData[4];
                Element d21 = inData[5];
                outData[0] = (A2.subtract((M22_2.multiplyLeftI(m21.Ei)).
                        multiplyDivRecursive(A2.multiplyLeftE(m22.Ej, m22.Ei), ds, ring), ring)
                ).divideByNumber(d21, ring);
              //  LOGGER.info("7722 mult = " + outData[0]);
                break;
            }

            case(7723): {
                AdjMatrixS m11 = (AdjMatrixS) inData[0];
                AdjMatrixS m21 = (AdjMatrixS) inData[1];
                Element d11 = inData[2];
                Element d22 = inData[3];
                MatrixS K1 = (MatrixS) inData[4];
                Element d21 = inData[5];
                outData[0] = (m11.S.multiplyDivMulRecursive(m21.A.multiplyLeftE(m21.Ej, m21.Ei), d11, d22, ring).add(K1, ring))
                        .divideByNumber(d21.negate(ring), ring);
              //  LOGGER.info("7723 mult = " + outData[0]);
                break;
            }

            case(7724): {
                MatrixS P = (MatrixS) inData[0];
                MatrixS G = (MatrixS) inData[1];
                Element d12 = inData[2];
                outData[0] = P.multiplyDivRecursive(G, d12, ring);
              //  LOGGER.info("7724 mult = " + outData[0]);
                break;
            }

            case(7725): {
                MatrixS L = (MatrixS) inData[0];
                MatrixS F = (MatrixS) inData[1];
                MatrixS G = (MatrixS) inData[2];
                Element d12 = inData[3];
                outData[0] = (L.add(F.multiplyRecursive(G, ring), ring)).divideByNumber(d12, ring);
               // LOGGER.info("7725 mult = " + outData[0]);
                break;
            }
            case (102): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A12 = (MatrixS) inData[1];
//                LOGGER.info("-------------------------102-------------------------------");
//                LOGGER.info("seq 102 A12: " + A12);
                MatrixS X_U2 = (F11.J().multiplyRecursive(F11.M(), ring))
                        .multiplyRecursive(A12, ring)
                        .divideByNumber(F11.A_n(), ring);


                /*LOGGER.info("F11.J(): " + F11.J());
                LOGGER.info("F11.M(): " + F11.M());
                LOGGER.info("F11.A_n(): " + F11.A_n());
                LOGGER.info("A12: " + A12);
                LOGGER.info("X_U2: " + X_U2);*/
                outData[0] = X_U2;
                break;
            }
            case (103): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A12 = (MatrixS) inData[1];
                Element a = inData[2];

//                LOGGER.info("-------------------------103-------------------------------");
                MatrixS A12_0 = F11.M().multiplyRecursive(A12, ring);
                MatrixS A12_2 = F11.Dbar().multiplyRecursive(A12_0, ring)
                        .divideByNumber(a, ring);

                /*LOGGER.info(" F11.M()=" + F11.M());
                LOGGER.info(" A12=" + A12);
                LOGGER.info("A12_0: " + A12_0);
                LOGGER.info("A12_2: " + A12_2);
                LOGGER.info("F11.Dbar(): " + A12_0);
                LOGGER.info("A12_0: " + 0);*/

                outData[0] = A12_0;
                outData[1] = A12_2;
                break;
            }
            case (104): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A21 = (MatrixS) inData[1];
                Element a = inData[2];

//                LOGGER.info("-------------------------104-------------------------------");
                MatrixS A21_0 = A21.multiplyRecursive(F11.W(), ring);
                MatrixS A21_2 = A21_0.multiplyRecursive(F11.Dbar(), ring)
                        .divideByNumber(a, ring);

//                LOGGER.info(" S A21_0=" + A21_0);
//                                LOGGER.info(" S A21_2=" + A21_2);
//                LOGGER.info("-------------------------/104-------------------------------");
                outData[0] = A21_0;
                outData[1] = A21_2;
               // LOGGER.info("A21_0: " + A21_0);
                //LOGGER.info("A21_2: " + A21_2);
                break;
            }
            case (105): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A21 = (MatrixS) inData[1];
//                LOGGER.info("-------------------------105-------------------------------");

                MatrixS X_L3 = (A21.multiplyRecursive(F11.W()
                        .multiplyRecursive(F11.I(), ring), ring))
                        .divideByNumber(F11.A_n(), ring);
//                LOGGER.info("-------------------------/105-------------------------------");
                outData[0] = X_L3;
             //   LOGGER.info("X_L3: " + X_L3);
                break;
            }
            case (107): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A21_0 = (MatrixS) inData[1];
                MatrixS A12_0 = (MatrixS) inData[2];
//                LOGGER.info("-------------------------107-------------------------------");


                MatrixS A21_1 = A21_0.multiplyByNumber(F11.A_n(), ring)
                        .multiplyRecursive(F11.Dhat(), ring);
                MatrixS A12_1 = F11.Dhat().multiplyByNumber(F11.A_n(), ring).multiplyRecursive(A12_0, ring);

                MatrixS D11PLUS = F11.D().transpose();
                MatrixS A22_0 = A21_1.multiplyRecursive(D11PLUS
                        .multiplyRecursive(A12_1, ring), ring);



//                LOGGER.info("-------------------------/107-------------------------------");
                outData[0] = A22_0;

               /* LOGGER.info("A21_1 = " + A21_1);
                LOGGER.info("D11PLUS = " + D11PLUS);
                LOGGER.info("A12_1 = " + A12_1);
                LOGGER.info("A22_0: " + A22_0);
                LOGGER.info("A21_0: " + A21_0);
                LOGGER.info("A12_0: " + A12_0);
                LOGGER.info("F11.A_n(): " + F11.A_n());
                LOGGER.info("F11.Dhat(): " + F11.Dhat());
                LOGGER.info("F11.D(): " + F11.D());
                LOGGER.info("A12_1: " + A12_1);*/



                break;
            }
            case (109): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A22 = (MatrixS) inData[1];
                MatrixS A22_0 = (MatrixS) inData[2];
                LdumwDto F21 = ((LdumwDto) inData[3]);
                Element a = inData[4];

//                LOGGER.info("-------------------------109-------------------------------");

                Element ak = F11.A_n();
                Element ak2 = ak.multiply(ak, ring);
                MatrixS A22_1 = (A22.multiplyByNumber(ak2, ring)
                        .multiplyByNumber(a, ring)
                        .subtract(A22_0, ring))
                        .divideByNumber(ak, ring)
                        .divideByNumber(a, ring);

                MatrixS X_A22_2 = (F21.Dbar().multiplyRecursive(F21.M(), ring)).multiplyRecursive(A22_1, ring);
//                LOGGER.info("-------------------------/109-------------------------------");
                outData[0] = A22_1;
                outData[1] = X_A22_2;
              /*  LOGGER.info("A22_1: " + A22_1);
                LOGGER.info("X_A22_2: " + X_A22_2);

                LOGGER.info("A22: " + A22);
                LOGGER.info("A22_0: " + A22_0);

                LOGGER.info("ak: " + ak);
                LOGGER.info("ak2: " + ak2);
                LOGGER.info("a: " + a);

                LOGGER.info("F21.Dbar(): " + F21.Dbar());
                LOGGER.info("F21.M(): " + F21.M());*/
                break;
            }
            case (110): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                LdumwDto F21 = ((LdumwDto) inData[1]);

                MatrixS UU = F21.U().multiplyRecursive(F11.U(), ring);
                outData[0] = UU;
                //LOGGER.info("UU: " + UU);
                break;
            }
            case (112): {
                MatrixS X_A22_2 = (MatrixS) inData[0];
                LdumwDto F12 = ((LdumwDto) inData[1]);
                LdumwDto F21 = ((LdumwDto) inData[2]);
                LdumwDto F11 = ((LdumwDto) inData[3]);
                Element a = inData[4];

                Element al = F21.A_n();
                Element am = F12.A_n();
                Element ak = F11.A_n();
                MatrixS A22_2 = X_A22_2.multiplyRecursive(F12.W()
                        .multiplyRecursive(F12.Dbar(), ring), ring);
                Element lambda = al.divideToFraction(ak, ring);
                Element as = lambda.multiply(am, ring);
                Element ak2 = ak.multiply(ak, ring);

                Element invLambda = LSUWM.doFraction(ring.numberONE, lambda, ring);

                MatrixS I12lambdaM2=(F12.I().multiplyByNumber(invLambda, ring)).add(F12.Ibar(), ring);
                MatrixS invD12hat = I12lambdaM2.multiplyRecursive(F12.Dhat(), ring);
               /* LOGGER.info("I12lambdaM2 -- = "+ A22_2);
                LOGGER.info("invD12hat -- = "+ ak2);
                LOGGER.info("F12.Ibar() -- = "+ F12.Ibar());


                LOGGER.info("am -- = "+ am);
                LOGGER.info("al -- = "+ al);
                LOGGER.info("ak -- = "+ ak);

                LOGGER.info("lambda -- = "+ lambda);
                LOGGER.info("X_A22_2 -- = "+ X_A22_2);
                LOGGER.info("F12.W() -- = "+ F12.W());
                LOGGER.info("FF12.Dbar() -- = "+ F12.Dbar());



                LOGGER.info("A22_2 -- = "+ A22_2);
                LOGGER.info("ak2 -- = "+ ak2);
                LOGGER.info("a -- = "+ a);*/
                MatrixS A22_3 = A22_2.divideByNumber(ak2, ring).divideByNumber(a, ring);

                outData[0] = lambda;
                outData[1] = as;
                outData[2] = A22_3;
                outData[3] = invD12hat;
                /*LOGGER.info("lambda: " + lambda);
                LOGGER.info("as: " + as);
                LOGGER.info("A22_3: " + A22_3);
                LOGGER.info("invD12hat: " + invD12hat);*/

                break;
            }
            case (113): {
                LdumwDto F21 = ((LdumwDto) inData[0]);
                MatrixS A22_1 = (MatrixS) inData[1];
                MatrixS U2 = (MatrixS) inData[2];//X_U2
                Element a = inData[3];

//                LOGGER.info("-------------------------113-------------------------------");
                Element al = F21.A_n();
                MatrixS U2H = F21.J().multiplyRecursive(F21.M(), ring)
                        .multiplyRecursive(A22_1, ring);
                U2H = U2H.divideByNumber(al, ring).divideByNumber(a, ring);
                U2 = U2.add(U2H, ring);
//                LOGGER.info("-------------------------/113-------------------------------");
                outData[0] = U2;
               // LOGGER.info("U2: " + U2);
                break;
            }
            case (114): {
                LdumwDto F21 = ((LdumwDto) inData[0]);
                MatrixS A22_1 = (MatrixS) inData[1];
//                LOGGER.info("-------------------------114-------------------------------");
                MatrixS Y_L3 = F21.Dbar().multiplyRecursive(F21.M(), ring)
                        .multiplyRecursive(A22_1, ring); // L3H1
//                LOGGER.info("-------------------------/114-------------------------------");
                outData[0] = Y_L3;
              //  LOGGER.info("Y_L3: " + Y_L3);
                break;
            }
            case (116): {

//                LOGGER.info("-------------------------116-------------------------------");
                LdumwDto F11 = ((LdumwDto) inData[0]);
                LdumwDto F12 = ((LdumwDto) inData[1]);
                Element lambda = inData[2];

                MatrixS I12lambda = (F12.I()
                        .multiplyByNumber(lambda, ring))
                        .add(F12.Ibar(), ring);
                MatrixS L12tilde = F12.L().multiplyRecursive(I12lambda, ring);
                MatrixS X_L = F11.L().multiplyRecursive(L12tilde, ring);

//                LOGGER.info("-------------------------/116-------------------------------");
                outData[0] = X_L;
                //LOGGER.info("X_L: " + X_L);
                break;
            }
            case (118): {
                MatrixS Y_L3 = (MatrixS) inData[0];
                LdumwDto F12 = ((LdumwDto) inData[1]);
                LdumwDto F11 = ((LdumwDto) inData[2]);
                MatrixS X_L3 = (MatrixS) inData[3];
                Element a = inData[4];
//                LOGGER.info("-------------------------118-------------------------------");
                Element am = F12.A_n();
                Element ak = F11.A_n();
                MatrixS L3H2 = (F12.W().multiplyRecursive(F12.I(), ring));

                Y_L3 = Y_L3.multiplyRecursive(L3H2, ring);
                Y_L3 = Y_L3.divideByNumber(am, ring)
                        .divideByNumber(ak, ring)
                        .divideByNumber(a, ring);

                MatrixS L3 = X_L3.add(Y_L3, ring);
//                LOGGER.info("-------------------------/118-------------------------------");
                outData[0] = L3;
               // LOGGER.info("L3: " + L3);
                break;
            }
            case (121): {
                LdumwDto F22 = ((LdumwDto) inData[0]);
                LdumwDto F12 = ((LdumwDto) inData[1]);
                Element lambda = inData[2];
//                LOGGER.info("-------------------------121-------------------------------");
                MatrixS J12lambda = (F12.J().multiplyByNumber(lambda, ring))
                        .add(F12.Jbar(), ring);
                MatrixS U12tilde = J12lambda.multiplyRecursive(F12.U(), ring);
                MatrixS X_U = F22.U().multiplyRecursive(U12tilde, ring);
//                LOGGER.info("-------------------------/121-------------------------------");
                outData[0] = X_U;
               // LOGGER.info("X_U: " + X_U);
                break;
            }

            case (123): {
                LdumwDto F21 = ((LdumwDto) inData[0]);
                LdumwDto F22 = ((LdumwDto) inData[1]);
//                LOGGER.info("seq F21.L(): " + F21.L());
//                LOGGER.info("seq F22.L(): " + F22.L());

                MatrixS LL = F21.L().multiplyRecursive(F22.L(), ring);
//                LOGGER.info("-------------------------/123-------------------------------");
                outData[0] = LL;
            //    LOGGER.info("LL: " + LL);
                break;
            }
        }
    }

    @Override
    //Вхідна функція дропа, розбиває вхідні дані на блоки.
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring ring) {
        //LOGGER.info(input[0]);
        MatrixS[] res = new MatrixS[8];
        MatrixS v1;
        MatrixS v2;

        if (input[0] instanceof MatrixS && input[1] instanceof MatrixS) {
            v1 = (MatrixS) input[0];
            v2 = (MatrixS) input[1];
        }

        switch (key) {
            case(0):
            case(1):
            case(7702):
            case(7712):
            case(7713):
            case(7718):
            case(7724):
            default: {
                v1 = (MatrixS) input[0];
                v2 = (MatrixS) input[1];
                //LOGGER.info("inputf mult 7702,7712,7713,7718,7724, v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(2):{
                v1 =  ((MatrixS) input[0]).transpose();
                v2 = (MatrixS) input[0];
                amin.resultForOutFunction[4] = v1;
                break;
            }

            //todo
            case(7703):{
                v1 = ((AdjMatrixS) input[0]).A;
                v2 = (MatrixS) input[1];
              //  LOGGER.info("inputf mult 7703 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7705): {
                v1 = (MatrixS) inData[2];
                MatrixS M12_1 = (MatrixS) inData[3];
                AdjMatrixS m11 = (AdjMatrixS) inData[4];
                v2 = M12_1.multiplyLeftE(m11.Ej, m11.Ei);
              //  LOGGER.info("inputf mult 7705 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7707): {
                v1 = ((AdjMatrixS) input[0]).S;
                v2 = (MatrixS) inData[1];
              //  LOGGER.info("inputf mult 7707 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7708):
            case(7720): {
                v1 = ((AdjMatrixS) input[0]).A;
                v2 = ((AdjMatrixS) input[1]).A;
              //  LOGGER.info("inputf mult 7708,7720 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }

            case(7709): {
                v1 = ((AdjMatrixS) input[0]).A;
                v2 = (MatrixS) inData[1];
              //  LOGGER.info("inputf mult 7709 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7710): {
                MatrixS S11 = ((AdjMatrixS) input[0]).S;
                AdjMatrixS m21 = (AdjMatrixS) inData[1];
                v1 = S11;
                v2 = m21.A.multiplyLeftE(m21.Ej, m21.Ei);
              //  LOGGER.info("inputf mult 7710 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7711):
            case(7721): {
                MatrixS M22_1 = (MatrixS) inData[0];
                MatrixS A1 = (MatrixS) inData[1];
                AdjMatrixS m12 = (AdjMatrixS) inData[2];
                v1 = M22_1;
                v2 = A1.multiplyLeftE(m12.Ej, m12.Ei);
              //  LOGGER.info("inputf mult 7711, 7721 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7714): {
                v1 = (MatrixS) inData[0];
                AdjMatrixS m11 = (AdjMatrixS) inData[1];
                v2 = m11.A.multiplyLeftE(m11.Ej, m11.Ei);
              //  LOGGER.info("inputf mult 7714 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7716): {
                MatrixS Q1 = (MatrixS) inData[0];
                MatrixS M12_1 = (MatrixS) inData[1];
                AdjMatrixS m11 = (AdjMatrixS) inData[2];
                Element d21 = inData[3];
                Element d11 = inData[4];
                v1 = (Q1.subtract((M12_1.multiplyLeftI(m11.Ei).multiplyByNumber(d21, ring)), ring))
                        .divideByNumber(d11, ring);
                v2 = (MatrixS) inData[5];
              //  LOGGER.info("inputf mult 7716 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7717):
            case(7722): {
                MatrixS A1 = (MatrixS) inData[0];
                MatrixS M12_1 = (MatrixS) inData[1];
                AdjMatrixS m11 = (AdjMatrixS) inData[2];
                AdjMatrixS m12 = (AdjMatrixS) inData[3];
                v1 = M12_1.multiplyLeftI(m11.Ei);
                v2 = A1.multiplyLeftE(m12.Ej, m12.Ei);
              //  LOGGER.info("inputf mult 7717,7722 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7719): {
                MatrixS M22_2 = (MatrixS) inData[0];
                AdjMatrixS m21 = (AdjMatrixS) inData[1];
                MatrixS y22 = (MatrixS) inData[2];
                v1 = M22_2.multiplyLeftI(m21.Ei);
                v2 = y22;
              //  LOGGER.info("inputf mult 7719 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7723): {
                v1 = ((AdjMatrixS) input[0]).S;
                AdjMatrixS m21 = (AdjMatrixS) inData[1];
                v2 = m21.A.multiplyLeftE(m21.Ej, m21.Ei);
              //  LOGGER.info("inputf mult 7723 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case(7725): {
                v1 = (MatrixS) inData[1];
                v2 = (MatrixS) inData[2];
              //  LOGGER.info("inputf mult 7725 v1 = "+ v1 + " ,v2 = "+ v2);
                break;
            }
            case (102):
            case (103): {
                LdumwDto F11 = (LdumwDto) input[0];
//                LOGGER.info("F11.M(): " + F11.M());
                v1 = F11.M(); // M
                v2 = (MatrixS) input[1]; // A12
//                LOGGER.info("-------------------------102 103 input-------------------------------");
                break;
            }
            case (104):
            case (105): {
                LdumwDto F11 = (LdumwDto) input[0];
                v1 = (MatrixS) input[1]; // A21
                v2 = F11.W(); // W
//                LOGGER.info("-------------------------104 105 input-------------------------------");
                break;
            }
            case (107): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A21_0 = (MatrixS) inData[1];
                MatrixS A12_0 = (MatrixS) inData[2];

//                LOGGER.info("-------------------------107 input-------------------------------");

                MatrixS A21_1 = A21_0.multiplyByNumber(F11.A_n(), ring).multiplyRecursive(F11.Dhat(), ring);

                MatrixS A12_1 = F11.Dhat().multiplyByNumber(F11.A_n(), ring).multiplyRecursive(A12_0, ring);

                MatrixS D11PLUS = F11.D().transpose();


                v1 = A21_1.multiplyRecursive(D11PLUS, ring);
                v2 = A12_1;
                break;
            }
            case (109): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A22 = (MatrixS) inData[1];
                MatrixS A22_0 = (MatrixS) inData[2];
                LdumwDto F21 = ((LdumwDto) inData[3]);
                Element a = inData[4];

//                LOGGER.info("-------------------------109 input-------------------------------");
                Element ak = F11.A_n();
                Element ak2 = ak.multiply(ak, ring);
                MatrixS A22_1 = (A22.multiplyByNumber(ak2, ring)
                        .multiplyByNumber(a, ring)
                        .subtract(A22_0, ring))
                        .divideByNumber(ak, ring)
                        .divideByNumber(a, ring);

//                LOGGER.info("-------------------------/109 input-------------------------------");

                v1 = F21.Dbar().multiplyRecursive(F21.M(), ring);
                v2 = A22_1;

                amin.resultForOutFunction[4] = A22_1;
                break;
            }
            case (110): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                LdumwDto F21 = ((LdumwDto) inData[1]);
//                LOGGER.info("-------------------------110 input-------------------------------");
                v1 = F21.U();
                v2 = F11.U();
                break;
            }
            case (112): {
                LdumwDto F12 = (LdumwDto) input[1];
//                LOGGER.info("-------------------------112 input-------------------------------");
                v1 = (MatrixS) input[0];
                v2 = F12.W();
                break;
            }
            case (113): {
                LdumwDto F21 = ((LdumwDto) inData[0]);
                MatrixS A22_1 = (MatrixS) inData[1];
//                LOGGER.info("-------------------------113 input-------------------------------");
                v1 = F21.J().multiplyRecursive(F21.M(), ring);
                v2 = A22_1;
                break;
            }
            case (114): {
                LdumwDto F21 = ((LdumwDto) inData[0]);
                MatrixS A22_1 = (MatrixS) inData[1];
//                LOGGER.info("-------------------------114 input-------------------------------");
                v1 = F21.Dbar().multiplyRecursive(F21.M(), ring);
                v2 = A22_1;
                break;
            }
            case (116): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                LdumwDto F12 = ((LdumwDto) inData[1]);
                Element lambda = inData[2];
//                LOGGER.info("-------------------------116 input-------------------------------");

                MatrixS I12lambda = (F12.I().multiplyByNumber(lambda, ring)).add(F12.Ibar(), ring);
                MatrixS L12tilde = F12.L().multiplyRecursive(I12lambda, ring);

                v1 = F11.L();
                v2 = L12tilde;
                break;
            }
            case (118): {
                MatrixS Y_L3 = (MatrixS) inData[0];
                LdumwDto F12 = ((LdumwDto) inData[1]);
//                LOGGER.info("-------------------------118-------------------------------");
                MatrixS L3H2 = (F12.W().multiplyRecursive(F12.I(), ring));
//                LOGGER.info("118 input Y_L3: " + Y_L3);
//                LOGGER.info("118 input L3H2: " + L3H2);
//                LOGGER.info("-------------------------118 input-------------------------------");
                v1 = Y_L3;
                v2 = L3H2;
                break;
            }
            case (121): {
                LdumwDto F22 = ((LdumwDto) inData[0]);
                LdumwDto F12 = ((LdumwDto) inData[1]);
                Element lambda = inData[2];

//                LOGGER.info("-------------------------121 input-------------------------------");

                MatrixS J12lambda = F12.J().multiplyByNumber(lambda, ring).add(F12.Jbar(), ring);
                MatrixS U12tilde = J12lambda.multiplyRecursive(F12.U(), ring);

                v1 = F22.U();
                v2 = U12tilde;
                break;
            }
            case (123): {
                LdumwDto F21 = ((LdumwDto) inData[0]);
                LdumwDto F22 = ((LdumwDto) inData[1]);
//                LOGGER.info("-------------------------123 input-------------------------------");
                v1 = F21.L();
                v2 = F22.L();

                break;
            }
        }

        Array.concatTwoArrays(v1.split(), v2.split(), res);

        return res;

    }

    @Override
    public void independentCalc(Ring ring, Amin amin)
    {
        switch (key) {
            case (0):
            case (1):
            case (2):
            case (102):
            case (103):
            case (107):
            case (109):
            case (110):
            case (114):
            case (116):
            case (121):
            case (118):
            case (105):
            case (104):
            case (123):
            case (7702):
            case (7703):
            case (7707):
            case (7708):
            case (7709):
            case (7710):
            case (7711):
            case (7713):
            case (7714):
            case (7717):
            case (7718):
            case (7719):
            case (7720):
            case (7721):
            case (7722):
            case (7723):
            case (7724):
            case (7725):
                break;

            case (7705): {
                MatrixS s1 = ((MatrixS) inData[0]).multiplyByNumber(inData[1], ring);
                amin.resultForOutFunction[4] = s1;
               /* LOGGER.info("indepcalc 7705  inData[0] = " + inData[0]);
                LOGGER.info("indepcalc 7705  inData[1] = " + inData[1]);
                LOGGER.info("indepcalc 7705 = " + s1);*/
                break;
            }
            case (7712): {
                Element ds = inData[3].multiply(inData[4], ring).divide(inData[2], ring);
                amin.resultForOutFunction[4] = ds;
                //LOGGER.info("indepcalc 7712 = " + ds);
                break;
            }
            case(7716): {
                MatrixS S12 = ((AdjMatrixS) inData[6]).S;
                Element d21 = inData[3];
                amin.resultForOutFunction[4] = S12.multiplyByNumber(d21, ring);
               // LOGGER.info("indepcalc 7712 = " + amin.resultForOutFunction[4]);
                break;
            }
            case (112): {
                LdumwDto F12 = ((LdumwDto) inData[1]);
                LdumwDto F21 = ((LdumwDto) inData[2]);
                LdumwDto F11 = ((LdumwDto) inData[3]);

//                LOGGER.info("-------------------------112 indepen-------------------------------");

                Element al = F21.A_n();
                Element am = F12.A_n();
                Element ak = F11.A_n();

                Element lambda = al.divideToFraction(ak, ring);
                Element as = lambda.multiply(am, ring);
                Element ak2 = ak.multiply(ak, ring);


                Element invLambda = LSUWM.doFraction(ring.numberONE,lambda, ring);

                MatrixS I12lambdaM2=(F12.I().multiplyByNumber(invLambda, ring)).add(F12.Ibar(), ring);
                MatrixS invD12hat = I12lambdaM2.multiplyRecursive(F12.Dhat(), ring);

                amin.resultForOutFunction[4] = lambda;
                amin.resultForOutFunction[5] = as;
                amin.resultForOutFunction[6] = invD12hat;
                amin.resultForOutFunction[7] = ak2;
                break;
            }
            case (113): {
                LdumwDto F21 = ((LdumwDto) inData[0]);
                Element a = inData[3];


//                LOGGER.info("-------------------------113 indepen-------------------------------");
                Element al = F21.A_n();
                amin.resultForOutFunction[4] = al.multiply(a, ring);

                break;
            }

        }
        return;
    }

    //Вихідна функція дропа, яка збирає блоки в результат
    @Override
    public Element[] outputFunction(Element[] input, com.mathpar.number.Ring ring) {
        MatrixS[] resmat = new MatrixS[4];
        for (int i = 0; i < 4; i++) {
            resmat[i] = (MatrixS) input[i];
        }

        Element[] res = new Element[outputDataLength];
        switch (key){
            case(0):
            case (107):
            case (110):
            case (114):
            case (116):
            case (121):
            case (123):
            case(7708):
            case(7713):
            case(7720): {
                res = new MatrixS[]{MatrixS.join(resmat)};
               // LOGGER.info("outfunc res 7708 7713 7720  = "+ res[0]);
                break;
            }
            case(1):
            case(7709): {
                res = new MatrixS[]{MatrixS.join(resmat).negate(ring)};
               // LOGGER.info("outfunc res 7709  = "+ res[0]);
                break;
            }
            case (2): {
                res = new Element[]{inData[1].add(MatrixS.join(resmat).negate(ring), ring), input[4]};

                break;
            }
            case(7702):
            case(7707):
            case(7718): {
                Element d = inData[2];
                MatrixS result1 = MatrixS.join(resmat).divideByNumber(d.negate(ring), ring);
                res = new MatrixS[]{result1};
               // LOGGER.info("outfunc res 7702 7707 7718  = "+ res[0]);
                break;
            }
            case(7703): {
                AdjMatrixS m11 = (AdjMatrixS) inData[0];
                Element d0 = inData[2];
                Element finalN = inData[3];
                MatrixS M12_1 = MatrixS.join(resmat).divideByNumber(d0, ring);
                MatrixS M12_2 = M12_1.multiplyLeftI(Array.involution(m11.Ei, (int) finalN.value));
                res = new MatrixS[]{M12_1, M12_2};
               // LOGGER.info("outfunc res 7703  = "+ res[0]);
                break;
            }
            case(7705): {
                Element d0 = inData[5];
                MatrixS s1 = (MatrixS) input[4];
                MatrixS s2 = MatrixS.join(resmat);
                MatrixS result1 = s1.subtract(s2, ring).divideByNumber(d0, ring);
                res = new MatrixS[]{result1};
              //  LOGGER.info("outfunc res 7705  = "+ res[0]);
                break;
            }
            case(7710):
            case(7724): {
                Element d11 = inData[2];
                MatrixS result1 = MatrixS.join(resmat).divideByNumber(d11, ring);
                res = new MatrixS[]{result1};
               // LOGGER.info("outfunc res 7724 7710  = "+ res[0]);
                break;
            }
            case(7711):
            case(7721): {
                Element d11 = inData[3];
                MatrixS result1 = MatrixS.join(resmat).divideByNumber(d11, ring);
                res = new MatrixS[]{result1};
               // LOGGER.info("outfunc res 7711 7721  = "+ res[0]);
                break;
            }
            case(7712): {
                Element d11 = inData[2];
                AdjMatrixS m21 = (AdjMatrixS) inData[5];
                Element finalN = inData[6];
                MatrixS M22_2 = MatrixS.join(resmat).divideByNumber(d11.
                        multiply(d11, ring), ring);

                MatrixS M22_3 = M22_2.multiplyLeftI(Array.involution(m21.Ei, (int) finalN.value));
                res = new Element[]{M22_2, input[4], M22_3};

               // LOGGER.info("outfunc res 7712 = "+ M22_2 + " --- " + M22_3);
                break;
            }
            case(7714):
            case(7723): {
                Element d0 = inData[2];
                Element d12 = inData[3];
                MatrixS K2 = (MatrixS) inData[4];
                Element d11 = inData[5];
                MatrixS s1 = MatrixS.join(resmat).divideMultiply(d0, d12, ring);
                MatrixS s2 = s1.add(K2, ring);
                MatrixS result1 = s2.divideByNumber(d11.negate(ring), ring);
                res = new MatrixS[]{result1};
                //LOGGER.info("outfunc res 7723  = "+ res[0]);

                break;
            }
            case(7716): {
                Element d11 = inData[4];
                MatrixS s2 = (MatrixS) input[4];
                MatrixS s3 = MatrixS.join(resmat).add(s2, ring);
                MatrixS result1 = s3.divideByNumber(d11, ring);;
                res = new MatrixS[]{result1};
              //  LOGGER.info("outfunc res 7716  = "+ res[0]);
                break;
            }
            case(7717): {
                MatrixS A1 = (MatrixS) inData[0];
                Element d11 = inData[4];
                Element d22 = inData[5];
                MatrixS s1 = MatrixS.join(resmat).divideByNumber(d11, ring);
                MatrixS s2 = A1.subtract(s1, ring);
                MatrixS result1 = s2.divideMultiply(d11, d22, ring);
                res = new MatrixS[]{result1};
             //   LOGGER.info("outfunc res 7717  = "+ res[0]);
                break;
            }
            case (7719): {
                Element ds = inData[3];
                AdjMatrixS m22 = (AdjMatrixS) inData[4];
                MatrixS s1 = MatrixS.join(resmat).divideByNumber(ds.negate(ring), ring);
                MatrixS result1 = s1.add(m22.S, ring);
                res = new MatrixS[]{result1};
              //  LOGGER.info("outfunc res 7719  = "+ res[0]);
                break;
            }
            case (7722): {
                MatrixS A2 = (MatrixS) inData[0];
                Element ds = inData[4];
                Element d21 = inData[5];
                MatrixS s1 = MatrixS.join(resmat).divideByNumber(ds, ring);
                MatrixS s2 = A2.subtract(s1, ring);
                MatrixS result1 = s2.divideByNumber(d21, ring);
                res = new MatrixS[]{result1};
              //  LOGGER.info("outfunc res 7722  = "+ res[0]);
                break;
            }
            case (7725): {
                Element d12 = inData[3];
                MatrixS L = (MatrixS) inData[0];
                MatrixS s1 = L.add(MatrixS.join(resmat), ring);
                MatrixS result1 = s1.divideByNumber(d12, ring);
                res = new MatrixS[]{result1};
              //  LOGGER.info("outfunc res 7725  = "+ res[0]);
                break;
            }
            case (102): {
                LdumwDto F11 = (LdumwDto) inData[0];

                res = new Element[]{
                        F11.J().multiplyRecursive(MatrixS.join(resmat), ring).divideByNumber(F11.A_n(), ring)
                };
                break;
            }
            case (103): {
                LdumwDto F11 = (LdumwDto) inData[0];
                Element a = inData[2];
                MatrixS A12_0 = MatrixS.join(resmat);
                MatrixS A12_2 = F11.Dbar().multiplyRecursive(A12_0, ring).divideByNumber(a, ring);
                res = new Element[]{
                        A12_0, A12_2
                };
                break;
            }
            case (104): {
                LdumwDto F11 = (LdumwDto) inData[0];
                Element a = inData[2];
                MatrixS A21_0 = MatrixS.join(resmat);
                MatrixS A21_2 = A21_0.multiplyRecursive(F11.Dbar(), ring).divideByNumber(a, ring);

                res = new Element[]{
                        A21_0, A21_2
                };
                break;
            }
            case (105): {
                LdumwDto F11 = ((LdumwDto) inData[0]);
                MatrixS A21xW21 = MatrixS.join(resmat);
                Element X_L3 = A21xW21.multiply(F11.I(), ring)
                        .divideByNumber(F11.A_n(), ring);
                res = new Element[]{
                        X_L3
                };
                break;
            }
            case (109): {
                res = new Element[]{
                        input[4], MatrixS.join(resmat)
                };
                break;
            }

            case (112): {
                LdumwDto F12 = (LdumwDto) inData[1];
                Element a = inData[4];
                MatrixS A22_2 = MatrixS.join(resmat).multiplyRecursive(F12.Dbar(), ring);
                MatrixS A22_3 = A22_2.divideByNumber(input[7], ring).divideByNumber(a, ring);

                //LOGGER.info("A22_3 outputfunc = " +A22_3);
                res = new Element[]{
                        input[4], input[5], A22_3, input[6]
                };
                break;
            }
            case (113): {
                MatrixS matrixS = MatrixS.join(resmat);
                MatrixS X_U2 = (MatrixS) inData[2];
                Element ala = input[4];
                res = new Element[]{
                        matrixS.divideByNumber(ala, ring).add(X_U2, ring)
                };
                break;
            }
            case (118): {
                LdumwDto F12 = ((LdumwDto) inData[1]);
                LdumwDto F11 = ((LdumwDto) inData[2]);
                MatrixS X_L3 = (MatrixS) inData[3];
                Element a = inData[4];
                Element am = F12.A_n();
                Element ak = F11.A_n();

                MatrixS Y_L3 = MatrixS.join(resmat);
                Y_L3 = Y_L3.divideByNumber(am, ring)
                        .divideByNumber(ak, ring)
                        .divideByNumber(a, ring);

                MatrixS L3 = X_L3.add(Y_L3, ring);
                res = new Element[]{
                        L3
                };
                break;
            }
        }

        return res;
    }

    //Перевіряє чи є дроп листовим
    @Override
    public boolean isItLeaf() {
        MatrixS ms;
        if (inData[0] instanceof LdumwDto) {
            LdumwDto ldumwDto = (LdumwDto) inData[0];
            return (ldumwDto.L().isItLeaf(leafSize,leafdensity));
        }

        switch(key){
            case(0):
            case(1):
            case(2):
            case(7702):
            case(7705):
            case(7711):
            case(7712):
            case(7713):
            case(7714):
            case(7716):
            case(7717):
            case(7718):
            case(7719):
            case(7721):
            case(7722):
            case(7724):
            case(7725):
            default: {
                ms = (MatrixS) inData[0];
                break;
            }
            case(7703):
            case(7707):
            case(7709): {
                ms = (MatrixS) inData[1];
                break;
            }
            case(7708):
            case(7720):{
                ms = ((AdjMatrixS) inData[0]).A;
                break;
            }
            case(7710):
            case(7723): {
                ms = ((AdjMatrixS) inData[0]).S;
                break;
            }
        }

        return ms.isItLeaf(leafSize,leafdensity);
    }


}
