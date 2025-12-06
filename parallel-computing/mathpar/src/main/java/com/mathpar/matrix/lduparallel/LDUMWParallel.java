
package com.mathpar.matrix.lduparallel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;

import java.util.Random;
import java.util.concurrent.*;

public class LDUMWParallel {
    final static ExecutorService executor = Executors.newFixedThreadPool(10);
    int n; // size of matrix == 2^N
    MatrixS L;
    MatrixS D; // ==Ddenom   new sense of this matrix! (denom-of-each-elems)
    MatrixS Dhat;  //  L Dhat M = I, W Dhat U = I
    MatrixS Dbar;// Dbar *Dbar^T= Ibar,  Dbar^T *Dbar = Jbar, 
    MatrixS U;
    MatrixS M;
    MatrixS W;
    MatrixS I;
    MatrixS Ibar;
    MatrixS J;
    MatrixS Jbar;
    Element a_n; // determinant

    public LDUMWParallel(MatrixS A) {
        n = A.size;
    }

    ;

    /**
     * MDUMWParallel is the main algorithm of matrix A decomposition.
     * The matrices {L,D,U,M,W I, J, [det], Dinv} are returned.
     * You can obtain:
     * A=LSU,
     * Inverse(A)=A^{-1}= WDM such that: A*A^{-1}*A=A and A^{-1}*A*A^{-1}=A^{-1}.
     * GInverse(A) is obtained due to 3 calls of MDUMWParallel();
     * Elements of matrix D are inverse of integers, and all other matrices are integer matrices.
     * The integer matrix Dinv is a matrix which consists of all denominators of the matrix D.
     * The equality D^{+}=Dinv.transpose() is true.
     * The matrix [det] is a matrix with one element det:
     * det=det(A) for full rank matrix and det is a corner minor of rank(A) size for other cases.
     *
     * @param A    - is a matrix  for the decomposition
     * @param ring Ring
     * @return MatrixS[] {L,D,U,M,W I, J, [det], Dinv} -resulting multipliers
     */
    public static MatrixS[] LDUMWParallel(MatrixS A, Ring ring) throws Exception {
        // envelop with 2^n
        int As = A.size;
        int Aclmn = A.colNumb;
        int s = Math.max(As, Aclmn);
        int b = s;
        for (int i = 0; i < s; i++) {
            b = b >> 1;
            if (b == 0) {
                b = 1 << i;
                break;
            }
        }
        Boolean flag; //flag is true for size== 2^n
        if (b != s) {
            flag = false;
            b = b << 1;
        } else {
            flag = (As == Aclmn);
        }
        A.size = b;
        A.colNumb = b; // b=2^n
        LDUMWParallel FF = new LDUMWParallel(A);
        FF.getLDU(A, ring.numberONE, ring);
        if (!flag) {
            FF.L.size = As;
            FF.U.colNumb = Aclmn;
            FF.W.size = Aclmn;
            FF.M.colNumb = As;
        }

        return new MatrixS[]{
                FF.L, invForD(FF.D, ring), FF.U, FF.M, FF.W, FF.I, FF.J, new MatrixS(FF.a_n), FF.D};
    }


    /**
     * Factors of inverse matrix and psevdo inverse matrix of A:
     * Inverse(A)=A^{-1} such that: A*A^{-1}*A=A and A^{-1}*A*A^{-1}=A^{-1}.
     * For the full rank matrix A: A*A^{-1}=A^{-1}*A=I.
     * we obtain   {L,D,U,M,W,det} due to  MDUMWParallel(A,ring);
     * Then we can use the identity: A^{-1}=W/det *D* M/det;
     * We return the matrices { W , D, M, [det]},
     * where W and M -integer matrices, [det] is a matrix with one element det:
     * det=det(A) for full rank matrix and det is a corner minor of rank(A) size for others.
     * * @param A - is a matrix  for inversion
     *
     * @param ring Ring
     * @return MatrixS[] { W , D, M, [det]}
     */
    public static MatrixS[] Inverse4F(MatrixS A, Ring ring) throws Exception {
        MatrixS[] res = LDUMWParallel(A, ring);
        return new MatrixS[]{res[4], res[1], res[3], res[7]};
    }

    /**
     * The inverse or psevdo inverse matrix for the matrix A:
     * Inverse(A)=A^{-1} such that: A*A^{-1}*A=A and A^{-1}*A*A^{-1}=A^{-1}.
     * For the full rank matrix A: A*A^{-1}=A^{-1}*A=I.
     * we obtain  {L,D,U,M,W, det}  due to  MDUMWParallel(A,ring);
     * We return the matrices A^{-1}=W/det *D* M/det;
     * * @param A - is a matrix  for inversion
     *
     * @param ring Ring
     * @return inverse(A) or psevdo inverse (A)
     */
    public static MatrixS Inverse(MatrixS A, Ring ring) throws Exception {
        MatrixS[] f = Inverse4F(A, ring);
        Element an = f[3].M[0][0];
        return f[0].divideByNumbertoFraction(an, ring).multiply(f[1], ring)
                .multiply(f[2].divideByNumbertoFraction(an, ring), ring);
    }

    /**
     * GInverse11F is the 11 components of the generalize inverse
     * Moore-Pennrose matrix.
     * First: we obtain  L,D,U,I,J,D^{+} due to  MDUMWParallel(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=MDUMWParallel(u), {DETl,  Wl, Dl,Ml}=MDUMWParallel(l)
     * Factorization of GInverse is equals
     * U^{T}J*(Wu/DETu)*Du*(Mu/DETu)*D^{+}*(Wl/DETl)*Dl*(Ml/DETl)*IL^{T}.
     *
     * @param A    input matrix
     * @param ring Ring
     * @return { DETu, U^{T}*J, Wu, Du,Mu, D^{+}, Wl, Dl,Ml, I*L^{T}, DETl}
     */
    public static MatrixS[] GInverse11F(MatrixS A, Ring ring) throws Exception {
        MatrixS[] res = LDUMWParallel(A, ring);
        MatrixS L = res[0];
        MatrixS D = res[1];
        MatrixS U = res[2];
        MatrixS I = res[5];
        MatrixS J = res[6];
        MatrixS Ut = U.transpose();
        MatrixS Lt = L.transpose();
        MatrixS UtJ = Ut.multiply(J, ring);
        MatrixS ILt = I.multiply(Lt, ring);
        MatrixS JU = J.multiply(U, ring);
        MatrixS LI = L.multiply(I, ring);
        MatrixS U2 = JU.multiply(UtJ, ring);
        MatrixS L2 = ILt.multiply(LI, ring);
        MatrixS[] resU = LDUMWParallel(U2, ring);
        MatrixS[] resL = LDUMWParallel(L2, ring);
        return new MatrixS[]{resU[7], UtJ, resU[4], resU[1], resU[3],
                res[8].transpose(), resL[4], resL[1], resL[3], ILt, resL[7]};
    }

    /**
     * GInverse5F is the 5 factors of the generalize inverse
     * Moore-Pennrose matrix
     * First: we obtain  L,D,U,I,J,D^{+} due to  MDUMWParallel(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=MDUMWParallel(u), {DETl,  Wl, Dl,Ml}=MDUMWParallel(l)
     * Factorization of GInverse is equals =  U^{T}J*uu*D^{+}*ll*IL^{T}
     * with uu=(Wu/DETu)*Du*(Mu/DETu) and  ll=(Wl/DETl)*Dl*(Ml/DETl)
     *
     * @param A    input matrix
     * @param ring Ring
     * @return {U^{T}J, uu, D^{+}, ll' IL^{T}}
     */
    public static MatrixS[] GInverse5F(MatrixS A, Ring ring) throws Exception {
        MatrixS[] f = GInverse11F(A, ring);
        Element au = f[0].M[0][0];
        Element al = f[10].M[0][0];
        MatrixS Uw = f[2].divideByNumbertoFraction(au, ring);
        MatrixS Um = f[4].divideByNumbertoFraction(au, ring);
        MatrixS Lw = f[6].divideByNumbertoFraction(al, ring);
        MatrixS Lm = f[8].divideByNumbertoFraction(al, ring);
        MatrixS UU = Uw.multiply(f[3], ring).multiply(Um, ring);
        MatrixS LL = Lw.multiply(f[7], ring).multiply(Lm, ring);
        MatrixS DD = UU.multiply(f[5], ring).multiply(LL, ring);
        return new MatrixS[]{f[1], UU, f[5], LL, f[9]};
    }

    /**
     * GInverse3F is the 3 factors of the generalize inverse
     * Moore-Pennrose matrix
     * First: we obtain  L,D,U,I,J,D^{+} due to  MDUMWParallel(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=MDUMWParallel(u), {DETl,  Wl, Dl,Ml}=MDUMWParallel(l)
     * Factorization of GInverse is equals =  U^{T}J*dd*IL^{T}
     * with uu=(Wu/DETu)*Du*(Mu/DETu),   ll=(Wl/DETl)*Dl*(Ml/DETl),
     * dd=uu*D^{+}*ll.
     *
     * @param A    input matrix
     * @param ring Ring
     * @return {U^{T}J, dd, IL^{T}}
     */
    public static MatrixS[] GInverse3F(MatrixS A, Ring ring) throws Exception {
        MatrixS[] f = GInverse5F(A, ring);
        MatrixS DD = f[1].multiply(f[2], ring).multiply(f[3], ring);
        return new MatrixS[]{f[0], DD, f[4]};
    }

    /**
     * GInverse3F is the 3 factors of the generalize inverse
     * Moore-Pennrose matrix
     * First: we obtain  L,D,U,I,J,D^{+} due to  MDUMWParallel(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=MDUMWParallel(u), {DETl,  Wl, Dl,Ml}=MDUMWParallel(l)
     * GInverse =  U^{T}J*dd*IL^{T}
     * with uu=(Wu/DETu)*Du*(Mu/DETu),   ll=(Wl/DETl)*Dl*(Ml/DETl),
     * dd=uu*D^{+}*ll.
     *
     * @param A    input matrix
     * @param ring Ring
     * @return GInverse
     */
    public static MatrixS GInverse(MatrixS A, Ring ring) throws Exception {
        MatrixS[] f = GInverse3F(A, ring);
        return f[0].multiply(f[1], ring).multiply(f[2], ring);
    }

    /**
     * LSU is the basic matrix A decomposition.
     * A=LSU,
     * L - lower triangular, U - upper triangular,
     * D  - truncated weighted permutation matrix.
     * If elements of matrix A are integers then
     * elements of matrix D are inverse of integers,
     * L and U are integer matrices.
     *
     * @param A    - is a matrix  for the decomposition
     * @param ring Ring
     * @return MatrixS[] {L,D,U} -resulting multipliers
     */
    public static MatrixS[] LDU(MatrixS A, Ring ring) throws Exception {
        MatrixS[] res = LDUMWParallel(A, ring);
        return new MatrixS[]{res[0], res[1], res[2]};
    }

    /**
     * This is the kernel of MDUMWParallel decomposition.
     * It is recursive procedure, which is worked with
     * dynamic variables of this class object.
     *
     * @param T    is input matrix for the decomposition
     * @param a    is minor of previouse step (or 1 for the first step)
     * @param ring
     */
    public void getLDU(MatrixS T, Element a, Ring ring) throws ExecutionException, InterruptedException {
        Element ONE = ring.numberONE;
        //    System.out.println("A="+T);
        if (T.isZero(ring)) {
            computeZeroMatrix(T, a, ring, ONE);
            return;
        }
        if (n == 1) {
            computeMatrixWithSizeOne(T, a, ring, ONE);
            return;
        }
        MatrixS[] A = T.split();
        MatrixS A11 = A[0];
        MatrixS A12 = A[1];
        MatrixS A21 = A[2];
        MatrixS A22 = A[3];
        LDUMWParallel F11 = new LDUMWParallel(A11);
        F11.getLDU(A11, a, ring);
        Element ak = F11.a_n;
        Element ak2 = ak.multiply(ak, ring);

//        MatrixS U2 = (F11.J.multiply(F11.M, ring)).multiply(A12, ring); // step2
//        MatrixS A12_0 = F11.M.multiply(A12, ring);// step3
//        MatrixS A21_0 = A21.multiply(F11.W, ring);// step3
//        MatrixS A12_2 = F11.Dbar.multiply(A12_0, ring).divideByNumber(a, ring);// step4
//        MatrixS A21_2 = A21_0.multiply(F11.Dbar, ring).divideByNumber(a, ring);// step4
//        MatrixS L3 = (A21.multiply(F11.W.multiply(F11.I, ring), ring)); // step5


//        LDUMWParallel F12 = new LDUMWParallel(A12_2);// step6
//        F12.getLSU(A12_2, ak, ring);// step6
//        Element am = F12.a_n;// step6
//        MatrixS A12_1 = F11.Dhat.multiplyByNumber(ak, ring).multiply(A12_0, ring);// step7
//        MatrixS A21_1 = A21_0.multiplyByNumber(ak, ring).multiply(F11.Dhat, ring);// step7
//        MatrixS D11PLUS = F11.D.transpose();// step7
//        MatrixS A22_0 = A21_1.multiply(D11PLUS.multiply(A12_1, ring), ring);// step7
//        LDUMWParallel F21 = new LDUMWParallel(A21_2);// step8
//        F21.getLSU(A21_2, ak, ring);// step8
//        Element al = F21.a_n;// step8

//        MatrixS A22_1 = (A22.multiplyByNumber(ak2, ring).multiplyByNumber(a, ring)// step9
//                .subtract(A22_0, ring)).divideByNumber(ak, ring).divideByNumber(a, ring);// step9
//        MatrixS A22_2 = (F21.Dbar.multiply(F21.M, ring)).multiply(A22_1, ring);// step9
//        MatrixS U21xU11 = F21.U.multiply(F11.U, ring);// step10
//        MatrixS U1_m1 = F11.W.multiply(F11.Dhat.multiply(F21.W.multiply(F21.Dhat, ring), ring), ring);// step11

//        A22_2 = A22_2.multiply(F12.W.multiply(F12.Dbar, ring), ring);// step12
//        lambda = al.divideToFraction(ak, ring);// step12
//        as = lambda.multiply(am, ring);// step12
//        A22_3 = A22_2.divideByNumber(ak2, ring).divideByNumber(a, ring);// step12
//        MatrixS I12lambdaM2 = (F12.I.divideByNumbertoFraction(lambda, ring)).add(F12.Ibar, ring);// step12
//        MatrixS invD12hat = I12lambdaM2.multiply(F12.Dhat, ring);// step12
//        U2 = U2.divideByNumber(ak, ring);// step13
//        MatrixS U2H = F21.J.multiply(F21.M, ring).multiply(A22_1, ring);// step13
//        U2H = U2H.divideByNumber(al, ring).divideByNumber(a, ring);// step13
//        U2 = U2.add(U2H, ring);// step13
//        MatrixS L3H2 = F21.Dbar.multiply(F21.M, ring).multiply(A22_1, ring);// step14

//        MatrixS DM12DM12 = invD12hat.multiply(F12.M, ring).multiply(F11.Dhat.multiply(F11.M, ring), ring);// step15
//        MatrixS I12lambda = (F12.I.multiplyByNumber(lambda, ring)).add(F12.Ibar, ring);// step16
//        MatrixS L12tilde = F12.L.multiply(I12lambda, ring);// step16
//        MatrixS L11xL12tilde = F11.L.multiply(L12tilde, ring);// step16
//        MatrixS L3H1 = (L3H2.multiply(F12.W.multiply(F12.I, ring), ring));// step18
//        L3H1 = L3H1.divideByNumber(am, ring)// step18
//                .divideByNumber(ak, ring).divideByNumber(a, ring);// step18
//        L3 = (L3.divideByNumber(ak, ring)).add(L3H1, ring);// step18

        //---------------------------------------STEP2----------------------------------------------------
        Callable<MatrixS> step2Callable = new Step2(F11.J, F11.M, A12, ring);
        MatrixS U2 = null;
        //--------------------------------------END STEP2-------------------------------------------------


        //---------------------------------------STEP3----------------------------------------------------
        Callable<MatrixS[]> step3Callable = new Step3(F11.M, F11.Dbar, A12, a, ring);
        MatrixS A12_0 = null;
        MatrixS A12_2 = null;
        //--------------------------------------END STEP3-------------------------------------------------


        //---------------------------------------STEP4----------------------------------------------------

        Callable<MatrixS[]> step4Callable = new Step4(A21, F11.W, F11.Dbar, ring, a);
        MatrixS A21_0 = null;
        MatrixS A21_2 = null;
        //--------------------------------------END STEP4-------------------------------------------------


        //---------------------------------------STEP5----------------------------------------------------
//        Callable<MatrixS> step5Callable = new Step5(A21, F11.W, F11.I, ring);
//        MatrixS L3 = null;
        //--------------------------------------END STEP5-------------------------------------------------

        Future<MatrixS> futureStep2 = executor.submit(step2Callable);
        Future<MatrixS[]> futureStep3 = executor.submit(step3Callable);
        Future<MatrixS[]> futureStep4 = executor.submit(step4Callable);
//        Future<MatrixS> futureStep5 = executor.submit(step5Callable);
        MatrixS L3 = (A21.multiply(F11.W.multiply(F11.I, ring), ring)); // step5
        try {

            U2 = futureStep2.get(); // STEP2
            MatrixS[] step3Res = futureStep3.get(); // STEP3
            MatrixS[] step4Res = futureStep4.get(); // STEP4
//            L3 = futureStep5.get(); // STEP5


            A12_0 = step3Res[0]; // STEP3
            A12_2 = step3Res[1]; // STEP3

            A21_0 = step4Res[0]; // STEP4
            A21_2 = step4Res[1]; // STEP4
        } catch (Exception e) {
            executor.shutdown();
            throw e;
        }

        //---------------------------------------STEP6----------------------------------------------------
        LDUMWParallel F12 = new LDUMWParallel(A12_2);
        Callable<Element> step6Callable = new Step6(A12_2, F12, ak, ring);
        Element am = null;
        //---------------------------------------END STEP6------------------------------------------------


        //---------------------------------------STEP7----------------------------------------------------
//        Callable<MatrixS> step7Callable = new Step7(F11.Dhat, ak, A12_0, A21_0, F11.D, ring);
//        MatrixS A22_0 = null;
        //---------------------------------------END STEP7------------------------------------------------


        //---------------------------------------STEP8----------------------------------------------------
        LDUMWParallel F21 = new LDUMWParallel(A21_2);
        Callable<Element> step8Callable = new Step8(A21_2, F21, ak, ring);
        Element al = null;
        //---------------------------------------END STEP8------------------------------------------------


        Future<Element> futureStep6 = executor.submit(step6Callable);
//        Future<MatrixS> futureStep7 = executor.submit(step7Callable);
        Future<Element> futureStep8 = executor.submit(step8Callable);

        MatrixS A12_1 = F11.Dhat.multiplyByNumber(ak, ring).multiply(A12_0, ring);// step7
        MatrixS A21_1 = A21_0.multiplyByNumber(ak, ring).multiply(F11.Dhat, ring);// step7
        MatrixS D11PLUS = F11.D.transpose();// step7
        MatrixS A22_0 = A21_1.multiply(D11PLUS.multiply(A12_1, ring), ring);// step7

        try {
            am = futureStep6.get();
            System.out.println("--------------------");
            System.out.println(
                    ";  L SIZE: " + F12.L.size +
                    ";  D SIZE: " + F12.D.size +
                    ";  U SIZE: " + F12.U.size +
                    ";  M SIZE: " + F12.M.size +
                    ";  W SIZE: " + F12.W.size +
                    ";  Dhat SIZE: " + F12.Dhat.size +
                    ";  Dbar SIZE: " + F12.Dbar.size +
                    ";  I SIZE: " + F12.I.size +
                    ";  Ibar SIZE: " + F12.Ibar.size +
                    ";  J SIZE: " + F12.J.size +
                    ";  Jbar SIZE: " + F12.Jbar.size
            );
            System.out.println("--------------------");
//            A22_0 = futureStep7.get();
            al = futureStep8.get();

        } catch (Exception e) {
            executor.shutdown();
            throw e;
        }

        //---------------------------------------STEP9----------------------------------------------------
//        Callable<MatrixS[]> step9Callable = new Step9(
//                A22, ak2, a, A22_0, ak,
//                F21.Dbar, F21.M, ring);
//        MatrixS A22_1 = null;
//        MatrixS A22_2 = null;
        //---------------------------------------END STEP9------------------------------------------------


        //---------------------------------------STEP10---------------------------------------------------
        Callable<MatrixS> step10Callable = new Step10(F21.U, F11.U, ring);
        MatrixS U21xU11 = null;
        //---------------------------------------END STEP10-----------------------------------------------


        //---------------------------------------STEP11---------------------------------------------------
        Callable<MatrixS> step11Callable = new Step11(F11.W, F11.Dhat, F21.W, F21.Dhat, ring);
        MatrixS U1_m1 = null;
        //---------------------------------------END STEP11-----------------------------------------------


//        Future<MatrixS[]> futureStep9 = executor.submit(step9Callable);
        Future<MatrixS> futureStep10 = executor.submit(step10Callable);
        Future<MatrixS> futureStep11 = executor.submit(step11Callable);

        MatrixS A22_1 = (A22.multiplyByNumber(ak2, ring).multiplyByNumber(a, ring)// step9
                .subtract(A22_0, ring)).divideByNumber(ak, ring).divideByNumber(a, ring);// step9
        MatrixS A22_2 = (F21.Dbar.multiply(F21.M, ring)).multiply(A22_1, ring);// step9

        try {
//            MatrixS[] step9 = futureStep9.get();
//            A22_1 = step9[0];
//            A22_2 = step9[1];

            U21xU11 = futureStep10.get();
            U1_m1 = futureStep11.get();

        } catch (Exception e) {
            executor.shutdown();
            throw e;
        }


        //---------------------------------------STEP12---------------------------------------------------
        Callable<Object[]> step12Callable = new Step12(
                A22_2, F12.W, F12.Dbar,
                al, ak, am, ak2, a, ring,
                F12.I, F12.Ibar, F12.Dhat);
        Element lambda = null;
        Element as = null;
        MatrixS A22_3 = null;
        MatrixS invD12hat = null;
        //---------------------------------------END STEP12-----------------------------------------------


        //---------------------------------------STEP13---------------------------------------------------
        Callable<MatrixS> step13Callable = new Step13(U2, ak, F21.J, F21.M, A22_1, al, a, ring);
        //---------------------------------------END STEP13-----------------------------------------------


        //---------------------------------------STEP14---------------------------------------------------
//        Callable<MatrixS> step14Callable = new Step14(F21.Dbar, F21.M, A22_1, ring);
//        MatrixS L3H2 = null;
        //---------------------------------------END STEP14-----------------------------------------------

        Future<Object[]> futureStep12 = executor.submit(step12Callable);
        Future<MatrixS> futureStep13 = executor.submit(step13Callable);

        MatrixS L3H2 = F21.Dbar.multiply(F21.M, ring).multiply(A22_1, ring);// step14

        try {
            Object[] step12Res = futureStep12.get();
            U2 = futureStep13.get();

            lambda = (Element) step12Res[0];
            as = (Element) step12Res[1];
            A22_3 = (MatrixS) step12Res[2];
            invD12hat = (MatrixS) step12Res[3];
        } catch (Exception e) {
            executor.shutdown();
            throw e;
        }

        //---------------------------------------STEP15---------------------------------------------------
        Callable<MatrixS> step15Callable = new Step15(invD12hat, F12.M, F11.Dhat, F11.M, ring);
        MatrixS DM12DM12 = null;
        //---------------------------------------END STEP15-----------------------------------------------


        //---------------------------------------STEP16---------------------------------------------------
        Callable<MatrixS> step16Callable = new Step16(F12.I, lambda, F12.Ibar, F12.L, F11.L, ring);
        MatrixS L11xL12tilde = null;
        //---------------------------------------END STEP16-----------------------------------------------


        //---------------------------------------STEP17---------------------------------------------------
        LDUMWParallel F22 = new LDUMWParallel(A22_3);
        Callable<Element> step17Callable = new Step17(A22_3, F22, as, ring);
        //---------------------------------------END STEP17-----------------------------------------------


        //---------------------------------------STEP18---------------------------------------------------
        Callable<MatrixS> step18Callable = new Step18(L3H2, F12.W, F12.I, am, ak, a, L3, ring);
        //---------------------------------------END STEP18-----------------------------------------------


        //---------------------------------------STEP19---------------------------------------------------

        //---------------------------------------END STEP19-----------------------------------------------

        Future<MatrixS> futureStep15 = executor.submit(step15Callable);
        Future<MatrixS> futureStep16 = executor.submit(step16Callable);
        Future<Element> futureStep17 = executor.submit(step17Callable);
        Future<MatrixS> futureStep18 = executor.submit(step18Callable);

        MatrixS U2prim = U1_m1.multiply(U2.negate(ring), ring);// step19

        try {
            DM12DM12 = futureStep15.get();
            L11xL12tilde = futureStep16.get();
            a_n = futureStep17.get();
            L3 = futureStep18.get();
        } catch (Exception e) {
            executor.shutdown();
            throw e;
        }
        // here   --- F11.Dbar


//        if (n > 2)
//            System.out.println("A12_0=" + A12_0 + "; A12_1=" + A12_1 + "; A12_2=" + A12_2 + "; A21_0=" + A21_0 + "; A21_1=" + A21_1 + "; A21_2=" + A21_2 + ";");


//        LDUMWParallel F22 = new LDUMWParallel(A22_3);
//        F22.getLSU(A22_3, as, ring);
//        a_n = F22.a_n;
        MatrixS J12lambda = (F12.J.multiplyByNumber(lambda, ring)).add(F12.Jbar, ring);
//        MatrixS I12lambda = (F12.I.multiplyByNumber(lambda, ring)).add(F12.Ibar, ring);
//        MatrixS L12tilde = F12.L.multiply(I12lambda, ring);
        MatrixS U12tilde = J12lambda.multiply(F12.U, ring);
        Element lambda2 = lambda.multiply(lambda, ring);


//        MatrixS L3H1 = (L3H2.multiply(F12.W.multiply(F12.I, ring), ring));
//
//        L3H1 = L3H1.divideByNumber(am, ring)
//                .divideByNumber(ak, ring).divideByNumber(a, ring);
//
//        L3 = (L3.divideByNumber(ak, ring)).add(L3H1, ring);
        MatrixS[] LL = new MatrixS[]{L11xL12tilde,
                MatrixS.zeroMatrix(), L3, F21.L.multiply(F22.L, ring)};
        L = MatrixS.join(LL);

        MatrixS[] UU = new MatrixS[]{U21xU11, U2,
                MatrixS.zeroMatrix(), F22.U.multiply(U12tilde, ring)};
        U = MatrixS.join(UU);
        D = MatrixS.join(new MatrixS[]{F11.D,
                F12.D.multiplyByNumber(lambda2, ring), F21.D, F22.D});
        IJMap(a, ring);

//        if (n > 2)
//            System.out.println("A22_0=" + A22_0 + "; A22_1=" + A22_1 + "; A22_2=" + A22_2 + "; A22_3=" + A22_3 + "; U2=" + U2 + "; L3=" + L3 + ";");


//        MatrixS I12lambdaM2 = (F12.I.divideByNumbertoFraction(lambda, ring)).add(F12.Ibar, ring);
//        MatrixS invD12hat = I12lambdaM2.multiply(F12.Dhat, ring);
        MatrixS L3prim = L3.negate(ring).multiply(DM12DM12, ring);
        MatrixS DhUnit = DtoUnit(D, ring.numberONE, ring).add(Dbar, ring).transpose();
        MatrixS[] Eprim = DhUnit.split();

//        MatrixS U2prim = U1_m1.multiply(U2.negate(ring), ring);
        // Du=
        MatrixS D11prim = DtoUnit(F11.D, ring.numberONE, ring).add(F11.Dbar, ring);
        MatrixS D12prim = DtoUnit(F21.D, ak, ring).add(F21.Dbar.multiplyByNumber(a, ring), ring);
        MatrixS D21prim = DtoUnit(F12.D, al, ring).add(F12.Dbar.multiplyByNumber(a, ring), ring);
        MatrixS D22prim = DtoUnit(F22.D, as, ring).add(F22.Dbar.multiplyByNumber(a, ring), ring);

        MatrixS V11A = F21.W.multiply(F21.Dbar, ring).multiply(Eprim[0], ring);
        MatrixS V11B = F11.W.multiply(D11prim, ring).multiply(V11A, ring);
        MatrixS V11 = V11B.multiplyByNumber(new Fraction(a_n, ak.multiply(al, ring)), ring);
        MatrixS V12A = F21.W.multiply(D12prim, ring).multiply(Eprim[1], ring);
        MatrixS V12B = F11.W.multiply(F11.Dbar, ring).multiply(V12A, ring);
        MatrixS V12 = V12B.multiplyByNumber(new Fraction(a_n, ak.multiply(al, ring).multiply(a, ring)), ring);
        MatrixS V21A = F12.W.multiply(D21prim, ring).multiply(F22.W, ring).multiply(F22.Dbar, ring);
        MatrixS V21B = V21A.multiply(Eprim[2], ring);
        MatrixS V21 = V21B.multiplyByNumber(new Fraction(ring.numberONE, am.multiply(a, ring)), ring);
        MatrixS V22A = F12.W.multiply(F12.Dbar, ring).multiply(F22.W, ring).multiply(D22prim, ring);
        MatrixS V22B = V22A.multiply(Eprim[3], ring);
        MatrixS V22 = V22B.multiplyByNumber(new Fraction(ring.numberONE, a.multiply(am, ring)), ring);


        W = MatrixS.join(new MatrixS[]{V11.add(U2prim.multiply(V21, ring), ring),
                V12.add(U2prim.multiply(V22, ring), ring), V21, V22});
//        if (n > 2)
//            System.out.println("V11=" + V11 + "; V12=" + V12 + "; V21=" + V21 + "; V22=" + V22 + "; U2prim=" + U2prim + "; L3prim=" + L3prim + ";");


        MatrixS N11A = Eprim[0].multiply(F12.Dbar, ring).multiply(F12.M, ring);
        MatrixS N11B = N11A.multiply(D11prim, ring).multiply(F11.M, ring);
        MatrixS N11 = N11B.multiplyByNumber(new Fraction(a_n, ak.multiply(am, ring)), ring);
        MatrixS N21A = Eprim[2].multiply(D21prim, ring).multiply(F12.M, ring);
        MatrixS N21B = N21A.multiply(F11.Dbar, ring).multiply(F11.M, ring);
        MatrixS N21 = N21B.multiplyByNumber(new Fraction(a_n, ak.multiply(am, ring).multiply(a, ring)), ring);
        MatrixS N12A = Eprim[1].multiply(F22.Dbar, ring).multiply(F22.M, ring);
        MatrixS N12B = N12A.multiply(D12prim, ring).multiply(F21.M, ring);
        MatrixS N12 = N12B.multiplyByNumber(new Fraction(ring.numberONE, al.multiply(a, ring)), ring);
        MatrixS N22A = Eprim[3].multiply(D22prim, ring).multiply(F22.M, ring);
        MatrixS N22B = N22A.multiply(F21.Dbar, ring).multiply(F21.M, ring);
        MatrixS N22 = N22B.multiplyByNumber(new Fraction(ring.numberONE, a.multiply(al, ring)), ring);

        M = MatrixS.join(new MatrixS[]{N11.add(N12.multiply(L3prim, ring), ring),
                N12, N21.add(N22.multiply(L3prim, ring), ring), N22});

//       
        MatrixS mM = MatrixS.join(new MatrixS[]{N11B, N12B, N21B, N22B});
        MatrixS wW = MatrixS.join(new MatrixS[]{V11B, V12B, V21B, V22B});
        MatrixS Dpr = MatrixS.join(new MatrixS[]{D11prim, D12prim, D21prim, D22prim});
//        if (n > 2)
//            System.out.println("Dpr==" + Dpr + "Eprim==" + DhUnit + "wW=====" + wW + "N11=" + N11 + "; N12=" + N12 + "; N21=" + N21 + "; N22=" + N22 + ";");

//
//          MatrixS LL1=L3.multiply(F22.Dhat, ring);
//                  MatrixS LL2=LL1.multiply(F22.M, ring);
//                  MatrixS LL3=LL2.multiply(F21.Dhat, ring);
//                                MatrixS LL4=LL3.multiply(F21.M, ring);
//        if (n >= 2) {
//            System.out.println("A=" + T + "; L=" + L + "; U=" + U + "; M=" + M + "; W=" + W + "; \\hat D=" + Dhat + "; D=" + invForD(D, ring) + ": L12tilde==  " + L12tilde + U12tilde);
//        }
//        if (n > 2) {
//            System.out.println("ak+al+am+as+a_n+lambda=" + ak + "  " + al + "  " + am + "  " + as + "  " + a_n + "  " + lambda);
//        }

    }

    private void computeMatrixWithSizeOne(MatrixS T, Element a, Ring ring, Element ONE) {
        a_n = T.getElement(0, 0, ring);
        Element aan = a_n.multiply(a, ring);
        Element an_an = a_n.multiply(a_n, ring);
        L = new MatrixS(a_n);
        D = new MatrixS(aan);
        Element a2Inv = (an_an.isOne(ring) || an_an.isMinusOne(ring))
                ? an_an : new Fraction(ring.numberONE, an_an);
        Dhat = new MatrixS(a2Inv);
        Dbar = MatrixS.zeroMatrix(n);
        U = new MatrixS(a_n);
        M = new MatrixS(a_n);
        W = new MatrixS(a_n);
        Jbar = Dbar;
        Ibar = Dbar;
        I = new MatrixS(ONE);
        J = I;
    }

        private void computeZeroMatrix(MatrixS T, Element a, Ring ring, Element ONE) {
        D = MatrixS.zeroMatrix(n);
        L = MatrixS.scalarMatrix(n, ONE, ring);
        U = MatrixS.scalarMatrix(n, ONE, ring);
        M = MatrixS.scalarMatrix(n, a, ring);
        W = MatrixS.scalarMatrix(n, a, ring);
        Element aInv = (a.isOne(ring) || a.isMinusOne(ring))
                ? a : new Fraction(ring.numberONE, a);
        Dhat = MatrixS.scalarMatrix(n, aInv, ring);
        a_n = a;
        Dbar = MatrixS.scalarMatrix(n, ONE, ring);
        I = MatrixS.zeroMatrix(n);
        J = MatrixS.zeroMatrix(n);
        Jbar = MatrixS.scalarMatrix(n, ONE, ring);
        Ibar = MatrixS.scalarMatrix(n, ONE, ring);
//        if (n == 2) {
//            System.out.println("A==========" + T + "; L=" + L + "; U=" + U + "; M=" + M + "; W=" + W + "; \\hat D=" + Dhat + "; D=" + invForD(D, ring) + ":   ");
//        }
    }

    ;

//    static MatrixS   DhatToUnit1(MatrixS D, Ring ring){ 
//        Element[][] MI=new Element[D.M.length][];
//        Element[] one = new Element[]{ring.numberONE};
//        for (int i = 0; i<D.M.length/2; i++){MI[i]=one; }   
//        return new MatrixS(D.size,D.colNumb,  MI, D.col);
//    }    

    //    static MatrixS   DhatToUnit2(MatrixS D, Ring ring){
//        Element[][] MI=new Element[D.M.length][];
//        Element[] one = new Element[]{ring.numberONE};
//        for (int i = D.M.length/2; i<D.M.length; i++){MI[i]=one; }   
//        return new MatrixS(D.size,D.colNumb,  MI, D.col);
//    }    
    static MatrixS DtoUnit(MatrixS D, Element e, Ring ring) {
        Element[][] MI = new Element[D.M.length][0];
        Element[] one = new Element[]{e};
        for (int i = 0; i < D.M.length; i++) {
            if (D.M[i].length > 0) MI[i] = one;
        }
        return new MatrixS(D.size, D.colNumb, MI, D.col);
    }


    /**
     * Using matrix D we are constructed I, J, Ibar, Jbar, Dhat.
     *
     * @param ring - Ring
     */
    void IJMap(Element a, Ring ring) {
        int[] forMaxCol = new int[1];
        MatrixS[] IandJ = doIJfromD(D, forMaxCol, ring);
        I = IandJ[0];
        J = IandJ[1];
        int maxCol = forMaxCol[0];
        Ibar = makeIbar(I, ring);
        Jbar = makeIbar(J, ring);
        maxCol = Math.max(maxCol, D.col.length);
        Element[][] Md = new Element[maxCol][];
        int[][] cold = new int[maxCol][];
        Element[][] MdB = new Element[maxCol][0];
        int[][] coldB = new int[maxCol][0];
        System.arraycopy(D.col, 0, cold, 0, D.col.length);
        //  Element an_an=a_n.multiply(a_n, ring);
        for (int i = 0; i < D.M.length; i++)
            if (D.M[i].length > 0) {
                Md[i] = new Element[]{a.divideToFraction(D.M[i][0].multiply(a_n, ring), ring)};
            }
        if (Ibar.isZero(ring)) {
            Dbar = MatrixS.zeroMatrix(D.size);
        } else {
            int maxColN = 0;
            // new fraction 1 divide a_n
            Element a_nInv = (a_n.isOne(ring) || a_n.isMinusOne(ring)) ? a_nInv = a_n
                    : new Fraction(ring.numberONE, a_n);
            int j = 0;
            while (Jbar.col[j].length == 0) {
                j++;
            }
            // i - runs in Ibar? j runs in Jbar. We build the diagonal in the square Ibar x Jbar
            for (int i = 0; i < Ibar.col.length; i++) {
                if (Ibar.col[i].length > 0) {
                    cold[i] = new int[]{j};
                    Md[i] = new Element[]{a_nInv};
                    coldB[i] = new int[]{j};
                    MdB[i] = new Element[]{ring.numberONE};
                    j++;
                    maxColN = j;
                    while ((j < Jbar.col.length) && (Jbar.col[j].length == 0)) {
                        j++;
                    }
                }
            }
            Dbar = new MatrixS(D.size, maxColN, MdB, coldB);
        }
        Dhat = new MatrixS(D.size, maxCol, Md, cold);
    }

    /**
     * doIJfromD: We constract matrices I and J, using matrix D as input
     *
     * @param D          imput D-type matrix
     * @param forMaxColN for returns the number of columns in the D matrix
     * @param ring       Ring
     * @return MatrixS[] {I,J} and MaxColN in array of int[0]
     */
    static MatrixS[] doIJfromD(MatrixS D, int[] forMaxColN, Ring ring) {
        Element[][] MI = new Element[D.M.length][];
        Element[] one = new Element[]{ring.numberONE};
        Element[] zero = new Element[0];
        int[] zeroI = new int[0];
        for (int i = 0; i < D.M.length; i++) {
            MI[i] = (D.M[i].length > 0) ? one : zero;
        }
        int[][] colI = new int[D.M.length][];
        int maxCol = 0, maxColOut = 0;
        for (int i = 0; i < D.col.length; i++) {
            if (D.col[i].length > 0) {
                colI[i] = new int[]{i};
                maxColOut = Math.max(maxCol, i);
                MI[i] = one;
                maxCol = Math.max(maxCol, D.col[i][0]);
            } else {
                colI[i] = zeroI;
                MI[i] = zero;
            }
        }
        ;
        maxCol++;
        maxColOut++;
        int mmax = Math.max(maxCol, maxColOut);
        Element[][] MJ = new Element[maxCol][0];
        int[][] colJ = new int[maxCol][0];
        for (int i = 0; i < D.col.length; i++) {
            if (D.col[i].length > 0) {
                int cc = D.col[i][0];
                colJ[cc] = new int[]{cc};
                MJ[cc] = one;
            }
        }
        MatrixS I = new MatrixS(D.size, mmax, MI, colI);
        MatrixS J = new MatrixS(D.size, mmax, MJ, colJ);
        forMaxColN[0] = maxCol;
        return new MatrixS[]{I, J};
    }

    /**
     * It makes Ibar from I
     *
     * @param II   - MatrixS I
     * @param ring - Ring
     * @return Ibar
     */
    public static MatrixS makeIbar(MatrixS II, Ring ring) {
        int colNumb = 0;
        int len = (II.M.length < II.size) ? II.size : II.M.length;
        Element[][] MI = new Element[len][];
        int[][] colI = new int[len][];
        Element[] one = new Element[]{ring.numberONE};
        Element[] zero = new Element[0];
        int[] zeroI = new int[0];
        int i = 0;
        for (; i < II.M.length; i++) {
            if (II.col[i].length > 0) {
                MI[i] = zero;
                colI[i] = zeroI;
            } else {
                MI[i] = one;
                colI[i] = new int[]{i};
                colNumb = i;
            }
        }
        for (; i < II.size; i++) {
            MI[i] = one;
            colI[i] = new int[]{i};
            colNumb = i;
        }
        return new MatrixS(II.size, colNumb + 1, MI, colI);
    }

    /**
     * Приводим D-матрицу из "компактной формы" к стандартной
     * (Знаменатели хранились как целые числа, теперь их заменят дроби,
     * и эти целые числа пойдут в знаменатель)
     * нулевая матрица вернет нулевую!
     *
     * @param Di - компактная диагональная матрица
     * @return диагональная матрица в обычном виде
     */
    static MatrixS invForD(MatrixS Di, Ring ring) {
        int len = Di.M.length;
        Element[][] MI = new Element[len][];
        for (int i = 0; i < len; i++) {
            if (Di.col[i].length > 0) {
                Element ee = Di.M[i][0];
                Element enew;
                if (ee.isNegative()) {
                    enew = (ee.isMinusOne(ring)) ? ee :
                            new Fraction(ring.numberMINUS_ONE, ee.negate(ring));
                } else {
                    enew = (ee.isOne(ring)) ? ee : new Fraction(ring.numberONE, ee);
                }
                MI[i] = new Element[]{enew};
            } else {
                MI[i] = Di.M[i];
            }
        }
        return new MatrixS(Di.size, Di.colNumb, MI, Di.col);
    }

    /**
     * Приводим Dhat-матрицу к ее обратной - транспонируем
     * и меняем значения на обратные
     *
     * @param Di -  диагональная матрица Dhat  полного ранга
     * @return обратная к диагональной матрице Dhat (полного ранга)
     */
    static MatrixS DhatInverse(MatrixS Di, Ring ring) {
        int len = Di.M.length;
        int[][] c = new int[len][];
        Element[][] MI = new Element[len][];
        for (int i = 0; i < len; i++) {
            Element ee = Di.M[i][0];
            Element enew;
            if (ee instanceof Fraction) {
                Fraction ff = (Fraction) ee;
                enew = new Fraction(ff.denom, ff.num);
            } else {
                if (ee.isOne(ring) || ee.isMinusOne(ring)) enew = ee;
                else {
                    enew = new Fraction(ring.numberONE, ee);
                }
            }
            int row = Di.col[i][0];
            MI[row] = new Element[]{enew};
            c[row] = new int[]{i};
        }
        return new MatrixS(Di.size, Di.colNumb, MI, c);
    }


    // *********************************************
    public static void main(String[] args) throws Exception {
        int times = 1;//Integer.parseInt(args[0]);
        int size = 16;//Integer.parseInt(args[1]);
        String mode = "novalidation";//args[2];
        Ring ring = new Ring("Z[]");
        int[][] qq =
//  {{4, 0, 2, 0, 3, 0, 0},
// {0, 0, 0, 0, 4, 7, 0},
// {4, 0, 4, 3, 0, 0, 0},
// {0, 0, 0, 0, 0, 1, 0},
// {5, 0, 7, 0, 1, 0, 0},
// {0, 0, 0, 0, 0, 0, 8},
// {0, 2, 0, 3, 0, 0, 0}};
//                {{0, 0, 3, 0},
//                        {2, 0, 1, 0},
//                        {0, 0, 0, 0},
//                        {1, 4, 0, 1}};

                {{0, 0, 0, 0, 0, 0, 7, 0},
                        {0, 0, 0, 0, 0, 0, 0, 1},
                        {0, 0, 3, 5, 0, 0, 6, 0},
                        {0, 5, 0, 0, 0, 3, 1, 0},
                        {0, 3, 4, 0, 0, 0, 0, 0},
                        {0, 0, 7, 0, 0, 7, 0, 5},
                        {0, 0, 0, 3, 0, 0, 0, 0},
                        {3, 0, 0, 0, 1, 0, 0, 0}};


        //       new int   [][] {{4,0},{7,0}};
        //     new int   [][] {{4,0,0,2},{0,5,0,0},{0,0,0,0},{3,3,0,7}};
        //   new int   [][] {{4,20,0,0},{0,0,10,0},{0,0,1,0},{3,15,0,0}};
//     int[][] pp1=  new int   [][] {{1,0,0,0},{0,1,0,0},{0,0,0,0},{0,0,0,0}};
//  int[][] pp2=  new int   [][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
//  MatrixS P1 =     new MatrixS(pp1, ring);
//    MatrixS P2 =     new MatrixS(pp2, ring);
//      MatrixS P =   P2.multiply(P1, ring);
        int r = 16;
        int c = 16;
        int density = 25;

        int[] randomType = new int[]{2};
        boolean good = true;
        int w = 1;
        MatrixS L = null;
        MatrixS D = null;
        MatrixS U = null;
        long startTimeMillis = System.currentTimeMillis();
        while ((w < 2) && (good)) {
            MatrixS tmp = null;
            w++;
            if (mode.equals("validate")) {
                tmp = new MatrixS(qq, ring);
            } else {
                Random rnd = new Random(0);
                tmp = new MatrixS(size, size, 10000, new int[]{5}, rnd, NumberZ.ONE, ring);
            }
            MatrixS[] res = null;
            for (int i = 0; i < times; i++) {
                res = LDUMWParallel(tmp, ring);
            }
//
            L = res[0];
            D = res[1];
            U = res[2];
            MatrixS M = res[3];
            MatrixS MMM = M;
            //  System.out.println("MMM=%%%%%%%%%%%%%%%%%%="+MMM);
            MatrixS W = res[4];
            MatrixS I = res[5];
            MatrixS J = res[6];
            MatrixS Ann = res[7];
            MatrixS Dinv = res[8];
//        System.out.println("tmp="+tmp);
//            System.out.println("L=" + L);
//            System.out.println("D=" + D);
//            System.out.println("U=" + U);
            //  System.out.println("M="+M);
            //  System.out.println("W="+W);
//       System.out.println("I="+I);System.out.println("J="+J);
            //  System.out.println("Ann="+Ann);    // System.out.println("Dinv="+Dinv);

            MatrixS AmLDU = L.multiply(D, ring).multiply(U, ring).subtract(tmp, ring);
//              long t1=System.currentTimeMillis();
            //  MatrixS Inv=  GInverse(tmp, ring);   // LSU(tmp, ring);
            System.out.println("w=" + w);
            if (AmLDU.isZero(ring)) {
                System.out.println("GOOD??==" + AmLDU.isZero(ring));
            } else {
                System.out.println("Bad!!");
                System.exit(1);
            }

//               long t2=System.currentTimeMillis();
//        MatrixS Io=  tmp.GenInvers(ring); //  tmp.inverseInFractions(ring);
//              long t3=System.currentTimeMillis();
//                System.out.println(Inv.subtract(Io, ring));
//              System.out.println(" t new --t old=   "+ (t2-t1)+"   "+(t3-t2)  );
//


//        MatrixS AInvAminA= tmp.multiply(Inv, ring).multiply(tmp, ring).subtract(tmp, ring);
//        MatrixS G=  GInverse(tmp, ring);
//        MatrixS AGIAminA =tmp.multiply(G, ring).multiply(tmp, ring).subtract(tmp, ring);
//        MatrixS AGminAGt =tmp.multiply(G, ring).subtract(tmp.multiply(G, ring).transpose(), ring);
//        MatrixS GAminGAt =G.multiply(tmp, ring).subtract(G.multiply(tmp, ring).transpose(), ring);
//       if (AmLDU.isZero(ring)&& AInvAminA.isZero(ring) && AGminAGt.isZero(ring)
//              && GAminGAt.isZero(ring) && AGIAminA.isZero(ring)) { good=true;
//              System.out.println("All VERY GOOD !!!!!!!!!!!!!!!! count="+w);}
//       else  { System.out.println(" ?????????"+AmLDU.isZero(ring)+AGIAminA.isZero(ring));
//            good=false; }
//             // System.out.println("MMM="+MMM);

        }
        long currentTimeMillis = System.currentTimeMillis();
        if (mode.equals("validate")) {
            testAnswer(L, D, U);
        }

        System.out.println("Time im millis: " + (currentTimeMillis - startTimeMillis));
        executor.shutdown();
    }

    private static void testAnswer(MatrixS l, MatrixS d, MatrixS u) {
        Ring ring = new Ring("Z[]");
        int[][] lArr =
                {{-2100, 0, 0, 0, 0, 0, 0, 0},
                        {0, -2100, 0, 0, 0, 0, 0, 0},
                        {0, 0, 15, 0, 0, 0, 0, 0},
                        {0, 0, 0, 5, 0, 0, 0, 0},
                        {0, 0, 20, 3, -300, 0, 0, 0},
                        {-315, -10500, 35, 0, -525, -21315, 0, 0},
                        {1161, 0, 0, 0, 135, 1701, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 45}};

//        int[][] dArr =
//                {{0, 0, 0, 0, 0, 0, (1 / 630000), 0},
//                        {0, 0, 0, 0, 0, 0, 0, (1 / 4410000)},
//                        {0, 0, (1 / 75), 0, 0, 0, 0, 0},
//                        {0, (1 / 5), 0, 0, 0, 0, 0, 0},
//                        {0, 0, 0, (-1 / 13500), 0, 0, 0, 0},
//                        {0, 0, 0, 0, 0, (1 / 44761500), 0, 0},
//                        {0, 0, 0, 0, 0, 0, 0, 0},
//                        {(1 / 675), 0, 0, 0, 0, 0, 0, 0}};

        int[][] uArr =
                {{45, 0, 0, 0, 15, 0, 0, 0},
                        {0, 5, 0, 0, 0, 3, 1, 0},
                        {0, 0, 15, 25, 0, 0, 30, 0},
                        {0, 0, 0, -300, 0, -81, -387, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0},
                        {0, 0, 0, 0, 0, -21315, 0, 0},
                        {0, 0, 0, 0, 0, 0, -2100, 0},
                        {0, 0, 0, 0, 0, 0, 0, -2100}};


        MatrixS lValid = new MatrixS(lArr, ring);
//        MatrixS dValid = new MatrixS(dArr, ring);
        MatrixS uValid = new MatrixS(uArr, ring);
        for (int i = 0; i < lValid.size; i++) {
            for (int j = 0; j < lValid.size; j++) {
                if (!l.getElement(i, j, ring).equals(lValid.getElement(i, j, ring))) {
                    throw new AssertionError("L is incorrect");
                }
            }
        }
//        for (int i = 0; i < dValid.size; i++) {
//            for (int j = 0; j < dValid.size; j++) {
//                if (!d.getElement(i, j, ring).equals(dValid.getElement(i, j, ring))) {
//                    throw new AssertionError("D is incorrect");
//                }
//            }
//        }
        for (int i = 0; i < uValid.size; i++) {
            for (int j = 0; j < uValid.size; j++) {
                if (!u.getElement(i, j, ring).equals(uValid.getElement(i, j, ring))) {
                    throw new AssertionError("U is incorrect");
                }
            }
        }
        System.out.println("Tested L and U");
    }
}