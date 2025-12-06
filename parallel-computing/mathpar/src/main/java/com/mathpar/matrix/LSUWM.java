
package com.mathpar.matrix;
import com.mathpar.number.*;
import com.mathpar.parallel.dap.ldumw.LdumwDto;
// import com.mathpar.polynom.Polynom;
import java.util.Random;

public class LSUWM {
    int n; // size of matrix == 2^N
    MatrixS L;
    MatrixS S; // ==Ddenom   new sense of this matrix! (denom-of-each-elems)
    MatrixS Shat;  //  L Shat M = I, W Shat U = I
    MatrixS Sbar;// Sbar *Sbar^T= Ibar,  Sbar^T *Sbar = Jbar, 
    MatrixS U;
    MatrixS M;
    MatrixS W;
    MatrixS I;
    MatrixS Ibar;
    MatrixS J;
    MatrixS Jbar;
    Element a_n; // determinant 

    public LSUWM(MatrixS A) {
        n = A.size;
    }

        /**
     * LSUWM is the main algorithm of matrix A decomposition.
     * The matrices {L,S,U,M,W I, J, [det], Dinv} are returned.
     * You can obtain:
     * A=LSU,
     * Inverse(A)=A^{-1}= WDM such that: A*A^{-1}*A=A and A^{-1}*A*A^{-1}=A^{-1}.
     * GInverse(A) is obtained due to 3 calls of LSUWM();
     * Elements of matrix D are inverse of integers, and all other matrices are integer matrices.
     * The integer matrix Dinv is a matrix which consists of all denominators of the matrix D.
     * The equality D^{+}=Dinv.transpose() is true.
     * The matrix [det] is a matrix with one element det:
     * det=det(A) for full rank matrix and det is a corner minor of rank(A) size for other cases.
     *
     * @param A    - is a matrix  for the decomposition
     * @param ring Ring
     * @return MatrixS[] {L,S,U,M,W I, J, [det], Dinv} -resulting multipliers
     */
//   public static MatrixS[] LSUWMdet(MatrixS A, Ring ring){
//        // envelop with 2^n
//        int As= A.size; int Aclmn= A.colNumb;      
//        System.out.println("Aclmn===="+Aclmn);
//        int s=Math.maxAbs(As,Aclmn); int b=s;
//        for (int i = 0; i < s; i++) {b=b>>1; if (b==0){b=1<<i; break;}}
//        Boolean flag; //flag is true for size== 2^n
//        if(b!=s){flag=false;b=b<<1;}else {flag=(As==Aclmn);}
//        A.size=b; A.colNumb=b; // b=2^n
//        LSUWM FF = new LSUWM(A); FF.getLSU(A, ring.numberONE, ring);
//        if(!flag){ FF.L.size=As; FF.U.size=As; FF.U.colNumb=Aclmn; 
//                   FF.W.size=As; FF.M.size=As;  }
//        return new MatrixS[]{
//            FF.L, invForS(FF.D, ring), FF.U, FF.W, FF.M ,  new MatrixS(FF.a_n ) };         }
//        
    public static MatrixS[] LSUWMdet(MatrixS A, Ring ring) {
        int n = A.size;
        int m = A.colNumb;
        A = A.expandToPow2with0(A.colNumb);
        LSUWM FF = new LSUWM(A);
        FF.getLSU(A, ring.numberONE, ring);
        MatrixS[] ms = new MatrixS[]{FF.L, invForS(FF.S, ring), FF.U, FF.W, FF.M, new MatrixS(FF.a_n)};
        if (!((n == A.size) && (m == A.colNumb))) backFromExpandLSU5(ms, n, m);
        return ms;
    }

    public static void backFromExpandLSU5(MatrixS[] mats, int n, int m) {
        Element[] Eempty = new Element[0];
        int[] intEmpty = new int[0];
        for (int i = n; i < mats[0].M.length; i++) {
            mats[0].M[i] = Eempty;
            mats[1].M[i] = Eempty;
            mats[0].col[i] = intEmpty;
            mats[1].col[i] = intEmpty;
            mats[4].M[i] = Eempty;
            mats[4].col[i] = intEmpty;
        }
        for (int i = m; i < mats[2].M.length; i++) {
            mats[2].M[i] = Eempty;
            mats[2].col[i] = intEmpty;
            mats[3].M[i] = Eempty;
            mats[3].col[i] = intEmpty;
        }
        mats[0].size = n;
        mats[1].size = n;
        mats[2].size = m;
        mats[3].size = m;
        mats[0].colNumb = n;
        mats[2].colNumb = m;
        mats[3].colNumb = m;
        mats[4].size = n;
        mats[4].colNumb = n;
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
     * @return MatrixS[] {L,S,U} -resulting multipliers
     */
    public static MatrixS[] LSU(MatrixS A, Ring ring) {
        MatrixS[] res = LSUWMdet(A, ring);
        return new MatrixS[]{res[0], res[1], res[2]};
    }

    /**
     * Factors of inverse matrix and psevdo inverse matrix of A:
     * Inverse(A)=A^{-1} such that: A*A^{-1}*A=A and A^{-1}*A*A^{-1}=A^{-1}.
     * For the full rank matrix A: A*A^{-1}=A^{-1}*A=I.
     * we obtain   {L,S,U,M,W,det} due to  LSUWM(A,ring);
     * Then we can use the identity: A^{-1}=W/det *D* M/det;
     * We return the matrices { W , D, M, [det]},
     * where W and M -integer matrices, [det] is a matrix with one element det:
     * det=det(A) for full rank matrix and det is a corner minor of rank(A) size for others.
     * * @param A - is a matrix  for inversion
     *
     * @param ring Ring
     * @return MatrixS[] { W , D, M, [det]}
     */
    public static MatrixS[] Inverse4F(MatrixS A, Ring ring) {
        MatrixS[] res = LSUWMdet(A, ring);
        return new MatrixS[]{res[3], res[1], res[4], res[5]};
    }

    /**
     * The inverse or psevdo inverse matrix for the matrix A:
     * Inverse(A)=A^{-1} such that: A*A^{-1}*A=A and A^{-1}*A*A^{-1}=A^{-1}.
     * For the full rank matrix A: A*A^{-1}=A^{-1}*A=I.
     * we obtain  {L,S,U,M,W, det}  due to  LSUWM(A,ring);
     * We return the matrices A^{-1}=W/det *D* M/det;
     * * @param A - is a matrix  for inversion
     *
     * @param ring Ring
     * @return inverse(A) or psevdo inverse (A)
     */
    public static MatrixS pseudoInverse(MatrixS A, Ring ring) {
        MatrixS[] f = Inverse4F(A, ring);
        Element an = f[3].M[0][0];
        MatrixS RES = f[0].divideByNumbertoFraction(an, ring);
        RES = RES.multiply(f[1], ring);
        MatrixS RES1 = f[2].divideByNumbertoFraction(an, ring);
        RES = RES.multiply(RES1, ring);
        return RES; //  (MatrixS) RES.value(ring.page, ring);
    }

    /**
     * GInverse11F is the 11 components of the generalize inverse
     * Moore-Pennrose matrix.
     * First: we obtain  L,S,U,I,J,D^{+} due to  LSUWM(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=LSUWM(u), {DETl,  Wl, Dl,Ml}=LSUWM(l)
     * Factorization of GInverse is equals
     * U^{T}J*(Wu/DETu)*Du*(Mu/DETu)*D^{+}*(Wl/DETl)*Dl*(Ml/DETl)*IL^{T}.
     *
     * @param A    input matrix
     * @param ring Ring
     * @return { DETu, U^{T}*J, Wu, Du,Mu, D^{+}, Wl, Dl,Ml, I*L^{T}, DETl}
     */
    public static MatrixS[] GInverse11F(MatrixS A, Ring ring) {
        MatrixS[] res = LSUWMIJdetS(A, ring);
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
        MatrixS[] resU = LSUWMIJdetS(U2, ring);
        MatrixS[] resL = LSUWMIJdetS(L2, ring);
        return new MatrixS[]{resU[7], UtJ, resU[4], resU[1], resU[3],
                res[8].transpose(), resL[4], resL[1], resL[3], ILt, resL[7]};
    }

    public static MatrixS[] LSUWMIJdetS(MatrixS A, Ring ring) {
        MatrixS A1 = A.expandToPow2with0(A.colNumb);
        LSUWM FF = new LSUWM(A1);
        FF.getLSU(A1, ring.numberONE, ring);
        MatrixS[] rr = new MatrixS[]{
                FF.L, invForS(FF.S, ring), FF.U, FF.W, FF.M, FF.I, FF.J, new MatrixS(FF.a_n), FF.S};
        return rr;
    }

    public static LdumwDto LDUWMIJdetDto(MatrixS A, Ring ring) {
        MatrixS A1 = A.expandToPow2with0(A.colNumb);
        LSUWM FF = new LSUWM(A1);
        FF.getLSU(A1, ring.numberONE, ring);
        return new LdumwDto(
                FF.L, invForS(FF.S, ring), FF.Shat, FF.Sbar,
                FF.U, FF.M, FF.W, FF.I,
                FF.Ibar, FF.J, FF.Jbar, FF.a_n, FF.S
        );
    }

    public static LdumwDto LDUWMIJdetD(MatrixS A, Element a, Ring ring) {
        MatrixS A1 = A.expandToPow2with0(A.colNumb);
        LSUWM FF = new LSUWM(A1);
        FF.getLSU(A1, a, ring);
        return new LdumwDto(
                FF.L, FF.S, FF.Shat, FF.Sbar,
                FF.U, FF.M, FF.W, FF.I,
                FF.Ibar, FF.J, FF.Jbar, FF.a_n, FF.S
        );
    }


    /**
     * GInverse5F is the 5 factors of the generalize inverse
     * Moore-Pennrose matrix
     * First: we obtain  L,S,U,I,J,D^{+} due to  LSUWM(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=LSUWM(u), {DETl,  Wl, Dl,Ml}=LSUWM(l)
     * Factorization of GInverse is equals =  U^{T}J*uu*D^{+}*ll*IL^{T}
     * with uu=(Wu/DETu)*Du*(Mu/DETu) and  ll=(Wl/DETl)*Dl*(Ml/DETl)
     *
     * @param A    input matrix
     * @param ring Ring
     * @return {U^{T}J, uu, D^{+}, ll' IL^{T}}
     */
    public static MatrixS[] GInverse5F(MatrixS A, Ring ring) {
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
     * First: we obtain  L,S,U,I,J,D^{+} due to  LSUWM(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=LSUWM(u), {DETl,  Wl, Dl,Ml}=LSUWM(l)
     * Factorization of GInverse is equals =  U^{T}J*dd*IL^{T}
     * with uu=(Wu/DETu)*Du*(Mu/DETu),   ll=(Wl/DETl)*Dl*(Ml/DETl),
     * dd=uu*D^{+}*ll.
     *
     * @param A    input matrix
     * @param ring Ring
     * @return {U^{T}J, dd, IL^{T}}
     */
    public static MatrixS[] GInverse3F(MatrixS A, Ring ring) {
        MatrixS[] f = GInverse5F(A, ring);
        MatrixS DD = f[1].multiply(f[2], ring).multiply(f[3], ring);
        return new MatrixS[]{f[0], DD, f[4]};
    }

    /**
     * genInverse is the generalize inverse Moore-Pennrose matrix
     * BUT it much complicated than ajont Ermit algorithm !!
     * First: we obtain  L,S,U,I,J,D^{+} due to  LSUWM(A,ring);
     * Second: We compute u=J*U*U^{T}*J, l=I*L^{T}*L*I and obtain
     * {DETu, Wu, Du,Mu}=LSUWM(u), {DETl,  Wl, Dl,Ml}=LSUWM(l)
     * GInverse =  U^{T}J*dd*IL^{T}
     * with uu=(Wu/DETu)*Du*(Mu/DETu),   ll=(Wl/DETl)*Dl*(Ml/DETl),
     * dd=uu*D^{+}*ll.
     *
     * @param A    input matrix
     * @param ring Ring
     * @return GInverse
     */
    public static MatrixS genInverse(MatrixS A, Ring ring) {
        int n = A.size;
        MatrixS[] f = GInverse3F(A, ring);
        MatrixS gi = f[0].multiply(f[1], ring).multiply(f[2], ring);
        if (gi.size != n) {
            MatrixS.backFromExpand(gi, n, n);
        }
        return gi;
    }

    static public Element doFraction(Element a, Element b, Ring ring) {
        int ra = ring.algebra[0];
        if ((ra == Ring.Zp32) || (ra == Ring.R) || (ra == Ring.R64) || (ra == Ring.Zp) || (ra == Ring.Complex))
            return a.divide(b, ring);
        return new Fraction(a, b);
    }


    /**
     * This is the kernel of LSUWM decomposition.
     * It is recursive procedure, which is worked with
     * dynamic variables of this class object.
     *
     * @param T    is input matrix for the decomposition
     * @param a    is minor of previouse step (or 1 for the first step)
     * @param ring
     */
    public void getLSU(MatrixS T, Element a, Ring ring) {
        Element ONE = ring.numberONE;
        if (T.isZero(ring)) {
            S = MatrixS.zeroMatrix(n);
            L = MatrixS.scalarMatrix(n, ONE, ring);
            U = MatrixS.scalarMatrix(n, ONE, ring);
            M = MatrixS.scalarMatrix(n, a, ring);
            W = MatrixS.scalarMatrix(n, a, ring);
            Element aInv = (a.isOne(ring) || a.isMinusOne(ring))
                    ? a : doFraction(ring.numberONE, a, ring);
            Shat = MatrixS.scalarMatrix(n, aInv, ring);
            a_n = a;
            Sbar = MatrixS.scalarMatrix(n, ONE, ring);
            I = MatrixS.zeroMatrix(n);
            J = MatrixS.zeroMatrix(n);
            Jbar = MatrixS.scalarMatrix(n, ONE, ring);
            Ibar = MatrixS.scalarMatrix(n, ONE, ring);
            //System.out.println( "L = "+L  + "; D= "+D + ";  U= " +  U+ "; M= "+  M +";  W= "+  W +";  Shat= "+ Shat);

            return;
        }
        if (n == 1) {
            a_n = T.getElement(0, 0, ring);
            Element aan = a_n.multiply(a, ring);
            Element an_an = a_n.multiply(a_n, ring);
            L = new MatrixS(a_n);
            S = new MatrixS(aan);
            Element a2Inv = (an_an.isOne(ring) || an_an.isMinusOne(ring))
                    ? an_an : doFraction(ring.numberONE, an_an, ring);
            Shat = new MatrixS(a2Inv);
            Sbar = MatrixS.zeroMatrix(n);
            U = new MatrixS(a_n);
            M = new MatrixS(a_n);
            W = new MatrixS(a_n);
            Jbar = Sbar;
            Ibar = Sbar;
            I = new MatrixS(ONE);
            J = I;
            // System.out.println( "L = "+L  + "; D= "+D + ";  U= " +  U+ "; M= "+  M +";  W= "+  W +";  Shat= "+ Shat);
            return;
        }

        MatrixS[] A = T.split();
        MatrixS A11 = A[0];
        MatrixS A12 = A[1];
        MatrixS A21 = A[2];
        MatrixS A22 = A[3];
        LSUWM F11 = new LSUWM(A11);
        F11.getLSU(A11, a, ring);
        Element ak = F11.a_n;
        Element ak2 = ak.multiply(ak, ring);
        MatrixS A12_0 = F11.M.multiply(A12, ring);
        MatrixS A12_1 = F11.Shat.multiplyByNumber(ak, ring).multiply(A12_0, ring);
        MatrixS A21_0 = A21.multiply(F11.W, ring);

        MatrixS A21_1 = A21_0.multiplyByNumber(ak, ring).multiply(F11.Shat, ring);
        MatrixS A12_2 = F11.Sbar.multiply(A12_0, ring).divideByNumber(a, ring);
        // here   --- F11.Sbar

        MatrixS A21_2 = A21_0.multiply(F11.Sbar, ring).divideByNumber(a, ring);
        LSUWM F21 = new LSUWM(A21_2);
        F21.getLSU(A21_2, ak, ring);
        Element al = F21.a_n;
        LSUWM F12 = new LSUWM(A12_2);
        F12.getLSU(A12_2, ak, ring);
        Element am = F12.a_n;
        Element lambda = al.divideToFraction(ak, ring);

        Element as = lambda.multiply(am, ring);
        MatrixS D11PLUS = F11.S.transpose();

        MatrixS A22_0 = A21_1.multiply(D11PLUS.multiply(A12_1, ring), ring);
        MatrixS A22_1 = (A22.multiplyByNumber(ak2, ring).multiplyByNumber(a, ring)
                .subtract(A22_0, ring)).divideByNumber(ak, ring).divideByNumber(a, ring);

        MatrixS A22_2 = (F21.Sbar.multiply(F21.M, ring)).multiply(A22_1, ring);
        A22_2 = A22_2.multiply(F12.W.multiply(F12.Sbar, ring), ring);
        MatrixS A22_3 = A22_2.divideByNumber(ak2, ring).divideByNumber(a, ring);
        // System.out.println("A21_1 = "+ A21_1+ "D11PLUS= "+ D11PLUS+ "A12_1 = "+ A12_1   );
        //     System.out.println("A22_0 = "+ A22_0+ "A22_1 = "+ A22_1 + "A22_2 = "+ A22_2  + "; ak2 = "+ak2+";  a= " +  a   );


        LSUWM F22 = new LSUWM(A22_3);
        F22.getLSU(A22_3, as, ring);
        a_n = F22.a_n;
        MatrixS J12lambda = (F12.J.multiplyByNumber(lambda, ring)).add(F12.Jbar, ring);
        MatrixS I12lambda = (F12.I.multiplyByNumber(lambda, ring)).add(F12.Ibar, ring);
        MatrixS L12tilde = F12.L.multiply(I12lambda, ring);
        MatrixS U12tilde = J12lambda.multiply(F12.U, ring);
        Element lambda2 = lambda.multiply(lambda, ring);


        MatrixS U2 = (F11.J.multiply(F11.M, ring)).multiply(A12, ring);
        U2 = U2.divideByNumber(ak, ring);
        MatrixS U2H = F21.J.multiply(F21.M, ring).multiply(A22_1, ring);
        U2H = U2H.divideByNumber(al, ring).divideByNumber(a, ring);
        U2 = U2.add(U2H, ring);
        MatrixS L3H2 = (A22_1.multiply(F12.W.multiply(F12.I, ring), ring));
        MatrixS L3H1 = F21.Sbar.multiply(F21.M, ring).multiply(L3H2, ring);
        L3H1 = L3H1.divideByNumber(am, ring)
                .divideByNumber(ak, ring).divideByNumber(a, ring);
        MatrixS L3 = (A21.multiply(F11.W.multiply(F11.I, ring), ring));
        L3 = (L3.divideByNumber(ak, ring)).add(L3H1, ring);
        MatrixS[] LL = new MatrixS[]{F11.L.multiply(L12tilde, ring),
                MatrixS.zeroMatrix(), L3, F21.L.multiply(F22.L, ring)};
        L = MatrixS.join(LL);
        //      System.out.println( "F11.L = "+F11.L  + "; L12tilde = "+L12tilde +
        //        ";   L3= " +  L3 + "; F21.L= "+  F21.L+ ";  F22.L= "+  F22.L   );

        MatrixS[] UU = new MatrixS[]{F21.U.multiply(F11.U, ring), U2,
                MatrixS.zeroMatrix(), F22.U.multiply(U12tilde, ring)};
        U = MatrixS.join(UU);
        S = MatrixS.join(new MatrixS[]{F11.S,
                F12.S.multiplyByNumber(lambda2, ring), F21.S, F22.S});
        IJMap(a, ring);

        Element invLambda = doFraction(ONE, lambda, ring);
        MatrixS I12lambdaM2 = (F12.I.multiplyByNumber(invLambda, ring)).add(F12.Ibar, ring);

        MatrixS invD12hat = I12lambdaM2.multiply(F12.Shat, ring);
        MatrixS L3prim = L3.negate(ring).multiply(invD12hat, ring).multiply(F12.M, ring).multiply(F11.Shat.multiply(F11.M, ring), ring);
        MatrixS DhUnit = StoUnit(S, ring.numberONE, ring).add(Sbar, ring).transpose();
        MatrixS[] Eprim = DhUnit.split();

        MatrixS U2prim = F11.W.multiply(F11.Shat.multiply(F21.W.multiply(F21.Shat.multiply(U2.negate(ring), ring), ring), ring), ring);
        // Du=
        MatrixS D11prim = StoUnit(F11.S, ring.numberONE, ring).add(F11.Sbar, ring);
        MatrixS D12prim = StoUnit(F21.S, ak, ring).add(F21.Sbar.multiplyByNumber(a, ring), ring);
        MatrixS D21prim = StoUnit(F12.S, al, ring).add(F12.Sbar.multiplyByNumber(a, ring), ring);
        MatrixS D22prim = StoUnit(F22.S, as, ring).add(F22.Sbar.multiplyByNumber(a, ring), ring);

        //      MatrixS V11A = D11prim.multiply(F21.Sbar, ring);
        //     V11A = F21.W.multiply(V11A, ring);

        //             F21.W.multiply(F21.Sbar, ring).multiply(Eprim[0], ring);
        //     MatrixS V11B =F11.W.multiply(D11prim, ring).multiply(V11A, ring);


        MatrixS V11A = F21.W.multiply(F21.Sbar, ring).multiply(Eprim[0], ring);
        MatrixS V11B = F11.W.multiply(D11prim, ring).multiply(V11A, ring);
        MatrixS V11 = V11B.multiplyByNumber(doFraction(a_n, ak.multiply(al, ring), ring), ring);
        MatrixS V12A = F21.W.multiply(D12prim, ring).multiply(Eprim[1], ring);
        MatrixS V12B = F11.W.multiply(F11.Sbar, ring).multiply(V12A, ring);
        MatrixS V12 = V12B.multiplyByNumber(doFraction(a_n, ak.multiply(al, ring).multiply(a, ring), ring), ring);
        MatrixS V21A = F12.W.multiply(D21prim, ring).multiply(F22.W, ring).multiply(F22.Sbar, ring);
        MatrixS V21B = V21A.multiply(Eprim[2], ring);
        MatrixS V21 = V21B.multiplyByNumber(doFraction(ring.numberONE, am.multiply(a, ring), ring), ring);
        MatrixS V22A = F12.W.multiply(F12.Sbar, ring).multiply(F22.W, ring).multiply(D22prim, ring);
        MatrixS V22B = V22A.multiply(Eprim[3], ring);
        MatrixS V22 = V22B.multiplyByNumber(doFraction(ring.numberONE, a.multiply(am, ring), ring), ring);


        W = MatrixS.join(new MatrixS[]{V11.add(U2prim.multiply(V21, ring), ring),
                V12.add(U2prim.multiply(V22, ring), ring), V21, V22});

        MatrixS N11A = Eprim[0].multiply(F12.Sbar, ring).multiply(F12.M, ring);
        MatrixS N11B = N11A.multiply(D11prim, ring).multiply(F11.M, ring);
        MatrixS N11 = N11B.multiplyByNumber(doFraction(a_n, ak.multiply(am, ring), ring), ring);
        MatrixS N21A = Eprim[2].multiply(D21prim, ring).multiply(F12.M, ring);
        MatrixS N21B = N21A.multiply(F11.Sbar, ring).multiply(F11.M, ring);
        MatrixS N21 = N21B.multiplyByNumber(doFraction(a_n, ak.multiply(am, ring).multiply(a, ring), ring), ring);
        MatrixS N12A = Eprim[1].multiply(F22.Sbar, ring).multiply(F22.M, ring);
        MatrixS N12B = N12A.multiply(D12prim, ring).multiply(F21.M, ring);
        MatrixS N12 = N12B.multiplyByNumber(doFraction(ring.numberONE, al.multiply(a, ring), ring), ring);
        MatrixS N22A = Eprim[3].multiply(D22prim, ring).multiply(F22.M, ring);
        MatrixS N22B = N22A.multiply(F21.Sbar, ring).multiply(F21.M, ring);
        MatrixS N22 = N22B.multiplyByNumber(doFraction(ring.numberONE, a.multiply(al, ring), ring), ring);

        M = MatrixS.join(new MatrixS[]{N11.add(N12.multiply(L3prim, ring), ring),
                N12, N21.add(N22.multiply(L3prim, ring), ring), N22});

//       
        MatrixS mM = MatrixS.join(new MatrixS[]{N11B, N12B, N21B, N22B});
        MatrixS wW = MatrixS.join(new MatrixS[]{V11B, V12B, V21B, V22B});
        MatrixS Dpr = MatrixS.join(new MatrixS[]{D11prim, D12prim, D21prim, D22prim});

    }

    ;

    static public MatrixS StoUnit(MatrixS D, Element e, Ring ring) {
        Element[][] MI = new Element[D.M.length][0];
        Element[] one = new Element[]{e};
        for (int i = 0; i < D.M.length; i++) {
            if (D.M[i].length > 0) MI[i] = one;
        }
        return new MatrixS(D.size, D.colNumb, MI, D.col);
    }

    /**
     * Using matrix D we are constructed I, J, Ibar, Jbar, Shat.
     *
     * @param ring - Ring
     */
    void IJMap(Element a, Ring ring) {
        int[] forMaxCol = new int[1];
        MatrixS[] IandJ = doIJfromS(S, forMaxCol, ring);
        I = IandJ[0];
        J = IandJ[1];
        int maxCol = forMaxCol[0];
        Ibar = makeIbar(I, ring);
        Jbar = makeIbar(J, ring);
        maxCol = Math.max(maxCol, S.col.length);
        Element[][] Md = new Element[maxCol][];
        int[][] cold = new int[maxCol][];
        Element[][] MdB = new Element[maxCol][0];
        int[][] coldB = new int[maxCol][0];
        System.arraycopy(S.col, 0, cold, 0, S.col.length);
        for (int i = 0; i < S.M.length; i++)
            if (S.M[i].length > 0) {
                Md[i] = new Element[]{a.divideToFraction(S.M[i][0].multiply(a_n, ring), ring)};
            }
        if (Ibar.isZero(ring)) {
            Sbar = MatrixS.zeroMatrix(S.size);
        } else {
            int maxColN = 0;
            // new fraction 1 divide a_n
            Element a_nInv = (a_n.isOne(ring) || a_n.isMinusOne(ring)) ? a_nInv = a_n
                    : doFraction(ring.numberONE, a_n, ring);
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
            Sbar = new MatrixS(S.size, maxColN, MdB, coldB);
        }
        Shat = new MatrixS(S.size, maxCol, Md, cold);
    }

    /**
     * doIJfromS: We constract matrices I and J, using matrix D as input
     *
     * @param D          imput D-type matrix
     * @param forMaxColN for returns the number of columns in the D matrix
     * @param ring       Ring
     * @return MatrixS[] {I,J} and MaxColN in array of int[0]
     */
    static MatrixS[] doIJfromS(MatrixS D, int[] forMaxColN, Ring ring) {
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
    static MatrixS invForS(MatrixS Di, Ring ring) {
        int len = Di.M.length;
        Element[][] MI = new Element[len][];
        for (int i = 0; i < len; i++) {
            if (Di.col[i].length > 0) {
                Element ee = Di.M[i][0];
                Element enew;
                if (ee.isNegative()) {
                    enew = (ee.isMinusOne(ring)) ? ee :
                            doFraction(ring.numberMINUS_ONE, ee.negate(ring), ring);
                } else {
                    enew = (ee.isOne(ring)) ? ee : doFraction(ring.numberONE, ee, ring);
                }
                MI[i] = new Element[]{enew};
            } else {
                MI[i] = Di.M[i];
            }
        }
        return new MatrixS(Di.size, Di.colNumb, MI, Di.col);
    }

    /**
     * Приводим Shat-матрицу к ее обратной - транспонируем
     * и меняем значения на обратные
     *
     * @param Di -  диагональная матрица Shat  полного ранга
     * @return обратная к диагональной матрице Shat (полного ранга)
     */
    static MatrixS ShatInverse(MatrixS Di, Ring ring) {
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
                    enew = doFraction(ring.numberONE, ee, ring);
                }
            }
            int row = Di.col[i][0];
            MI[row] = new Element[]{enew};
            c[row] = new int[]{i};
        }
        return new MatrixS(Di.size, Di.colNumb, MI, c);
    }


    // *********************************************
    public static void main(String[] args) {
        Ring ring = new Ring("Z[]");
        ring.setMOD32(97L);
        int[][] qq = {{24, 11}, {19, 10}};
        int[][] q1q =
                {{0, 0, 0, 27, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 9, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 11, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 9, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0}};
        int[][] mat = {{0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 14, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {0, 0, 0, 0, 0, 0, 0, 0, 26, 0, 0, 18, 0, 0, 0, 0, 0, 0, 27, 13, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0},
                {0, 0, 0, 0, 0, 0, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 27, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 10, 0, 0, 0},
                {0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 31, 0, 0, 29, 0, 13, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 7, 27, 0, 0, 0, 24, 0, 13, 0},
                {0, 0, 0, 0, 29, 0, 0, 0, 0, 0, 11, 0, 25, 0, 0, 0, 18, 5, 0, 0, 0, 2, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 28, 0, 0, 0, 0, 15, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 18, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 30, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 31, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {12, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 22, 0, 0, 0, 0, 1, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0},
                {14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 19, 16, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 5, 0, 0, 0, 21, 0, 0, 30, 0, 0, 0, 0, 0, 12, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 17, 0, 0, 0, 22, 0, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0, 22, 0, 0, 0, 0, 0, 0, 0, 0, 17, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 29, 0, 0, 0}};

        int density = 100;
        int r = 128;
        int[] randomType = new int[]{5};
        boolean good = true;
        long tt1 = 0, tt2 = 0;
        int w = 1;
        while ((w < 2)) // && (good))
        {
            w++;
            MatrixS tmp = //new MatrixS(mat, ring);
              new MatrixS(r,r,density, randomType, new Random(), ring.numberONE(), ring);
            long starttime = System.currentTimeMillis();
            MatrixS[] res = LSUWMIJdetS(tmp, ring);
            long endtime = System.currentTimeMillis() - starttime;
            MatrixS L = res[0];
            MatrixS D = res[1];
            MatrixS U = res[2];
            MatrixS M = res[3];
            MatrixS MMM = M;
            MatrixS W = res[4];
            MatrixS I = res[5];
            MatrixS J = res[6];
            MatrixS Ann = res[7];
            MatrixS Dinv = res[8];
            /*System.out.println("tmp=" + tmp);
            System.out.println("L=" + L);
            System.out.println("D=" + D);
            System.out.println("U=" + U);
            System.out.println("M=" + M);
            System.out.println("W=" + W);
            System.out.println("I=" + I);
            System.out.println("J=" + J);
            System.out.println("Ann=" + Ann);
            System.out.println("Dinv=" + Dinv);
            System.out.println("L.multiply(D, ring).multiply(U, ring) = " + L.multiply(D, ring).multiply(U, ring));*/
            System.out.println("size = " + r + " time = " + endtime);
            MatrixS AmLDU = L.multiply(D, ring).multiply(U, ring).subtract(tmp, ring);
            if (AmLDU.isZero(ring)) { // System.out.println(" "+AmLDU+tmp+L+D+U+W+M);
                System.out.println("VERY GOOD");
            } else {
                Element err = AmLDU.maxAbs(ring); // System.out.println(" "+AmLDU+tmp+L +D+U+W +M +"NOT GOOD");
                System.out.println(" " + err + " NOT GOOD");
                System.exit(1);
            }
        }
    }
}
//
//     long t1=System.currentTimeMillis();
//     MatrixS Inv=  genInverse(tmp, ring);
//    long t2=System.currentTimeMillis();
//    MatrixS Io=  tmp.genInverse(ring); //  tmp.inverseInFractions(ring);
//    long t3=System.currentTimeMillis();
//     tt1+=t2-t1;  tt2+=t3-t2;
//              System.out.println(" t new --t old=   "+ (t2-t1)+"   "+(t3-t2) +"   "+ tt1+"   "+tt2  );
//// System.out.println("  "+Inv+Io);
//    Inv=Inv.subtract(Io, ring);
//         MatrixS AInvAminA= tmp.multiply(Inv, ring).multiply(tmp, ring).subtract(tmp, ring);
//        MatrixS G=  genInverse(tmp, ring);
//        MatrixS AGA =tmp.multiply(G, ring).multiply(tmp, ring) ;
//        MatrixS AGIAminA =tmp.multiply(G, ring).multiply(tmp, ring).subtract(tmp, ring);
//        MatrixS AGminAGt =tmp.multiply(G, ring).subtract(tmp.multiply(G, ring).transpose(), ring);
//        MatrixS GAminGAt =G.multiply(tmp, ring).subtract(G.multiply(tmp, ring).transpose(), ring);
//
//
//
//
//        if(Inv.isZero(ring)){ good=true;
//              System.out.println("All VERY GOOD !!!!!!!!!!!!!!!! count="+w);}
//       else  { System.out.println(" ?????????"+AmLDU.isZero(ring)+AGIAminA.isZero(ring));
//                   System.out.println("RESTMP="+tmp);
//                   System.out.println("RESAGA="+AGA);
//                   System.out.println("AGIAminA="+AGIAminA);
//                 System.out.println(" GInv ="+Inv);
//                 System.out.println(" GInvOLD ="+Io);
//                 MatrixS pi=pseudoInverse(tmp,ring);
//                 MatrixS pp=tmp.multiply(pi, ring);
//                     System.out.println(" pi and prod ="+pi+pp);
//
//            good=false;
//       System.exit(1);}
////    }}
////}