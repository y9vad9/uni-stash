package com.mathpar.parallel.dap.ldumw;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.Ring;

import java.util.Objects;

public class LdumwDto extends Element {
    private final MatrixS L;
    private MatrixS D;
    private MatrixS Dhat;
    private MatrixS Dbar;
    private final MatrixS U;
    private MatrixS M;
    private MatrixS W;
    private MatrixS I;
    private MatrixS Ibar;
    private MatrixS J;
    private MatrixS Jbar;
    public final Element a_n;
    public MatrixS D_inv;


    public LdumwDto(MatrixS l, MatrixS d, MatrixS u, Element a_n) {
        L = l;
        D = d;
        U = u;
        this.a_n = a_n;
    }

    public LdumwDto(MatrixS l, MatrixS d, MatrixS dhat, MatrixS dbar,
                    MatrixS u, MatrixS m, MatrixS w, MatrixS i,
                    MatrixS ibar, MatrixS j, MatrixS jbar, Element a_n, MatrixS dinv) {
        L = l;
        D = d;
        Dhat = dhat;
        Dbar = dbar;
        U = u;
        M = m;
        W = w;
        I = i;
        Ibar = ibar;
        J = j;
        Jbar = jbar;
        this.a_n = a_n;
        D_inv = dinv;
    }

    public LdumwDto(MatrixS l, MatrixS d, MatrixS dhat, MatrixS dbar,
                    MatrixS u, MatrixS m, MatrixS w, MatrixS i,
                    MatrixS ibar, MatrixS j, MatrixS jbar, Element a_n) {
        L = l;
        D = d;
        Dhat = dhat;
        Dbar = dbar;
        U = u;
        M = m;
        W = w;
        I = i;
        Ibar = ibar;
        J = j;
        Jbar = jbar;
        this.a_n = a_n;
    }
    static Element doFraction(Element a, Element b, Ring ring) {int ra=ring.algebra[0];
        if((ra==Ring.Zp32)||(ra==Ring.R)||(ra==Ring.R64)||(ra==Ring.Zp)||(ra==Ring.Complex)) return a.divide(b, ring);
        return new Fraction(a, b);
    }

    void IJMap (Element a, Ring ring){
        int[] forMaxCol= new int[1];
        MatrixS[] IandJ= doIJfromD(D, forMaxCol, ring);
        I=IandJ[0];J=IandJ[1]; int maxCol=forMaxCol[0];
        Ibar=makeIbar(I, ring);
        Jbar=makeIbar(J, ring);
        maxCol=Math.max(maxCol, D.col.length);
        Element[][] Md=new Element[maxCol][];
        int[][] cold = new int[maxCol][];
        Element[][] MdB=new Element[maxCol][0];
        int[][] coldB = new int[maxCol][0];
        System.arraycopy(D.col, 0, cold, 0, D.col.length);
        for (int i = 0; i<D.M.length; i++) if(D.M[i].length>0){
            Md[i]=new Element[]{a.divideToFraction(D.M[i][0].multiply(a_n, ring), ring)};
        }
        if (Ibar.isZero(ring)) { Dbar = MatrixS.zeroMatrix(D.size);}
        else {int maxColN = 0;
            // new fraction 1 divide a_n
            Element a_nInv = (a_n.isOne(ring) || a_n.isMinusOne(ring)) ? a_nInv = a_n
                    : doFraction(ring.numberONE, a_n, ring);
            int j = 0;
            while (Jbar.col[j].length == 0) {j++;}
            // i - runs in Ibar? j runs in Jbar. We build the diagonal in the square Ibar x Jbar
            for (int i = 0; i < Ibar.col.length; i++) {
                if (Ibar.col[i].length > 0) {
                    cold[i] = new int[]{j};
                    Md[i] = new Element[]{a_nInv};
                    coldB[i] = new int[]{j};
                    MdB[i] = new Element[]{ring.numberONE};
                    j++;
                    maxColN = j;
                    while ((j < Jbar.col.length) && (Jbar.col[j].length == 0)) {j++;}
                }
            }
            Dbar = new MatrixS(D.size, maxColN, MdB, coldB);
        }
        Dhat = new MatrixS(D.size, maxCol, Md, cold);
    }

    public static MatrixS  makeIbar(MatrixS II, Ring ring){ int colNumb=0;
        int len=(II.M.length < II.size)? II.size: II.M.length;
        Element[][] MI=new Element[len][];
        int[][] colI=new int[len][];
        Element[] one = new Element[]{ring.numberONE};
        Element[] zero = new Element[0];
        int[] zeroI = new int[0];
        int i = 0;
        for (; i<II.M.length; i++){
            if(II.col[i].length>0){MI[i]= zero; colI[i]=zeroI;}
            else {MI[i]= one; colI[i]=new int[]{i};colNumb=i; }
        }
        for (; i<II.size; i++){MI[i]= one; colI[i]=new int[]{i}; colNumb=i; }
        return new MatrixS(II.size, colNumb+1, MI, colI);
    }

    static MatrixS[]  doIJfromD (MatrixS D, int[] forMaxColN, Ring ring){
        Element[][] MI=new Element[D.M.length][];
        Element[] one = new Element[]{ring.numberONE};
        Element[] zero = new Element[0];
        int[] zeroI = new int[0];
        for (int i = 0; i<D.M.length; i++){MI[i]= (D.M[i].length>0)? one: zero;}
        int[][] colI = new int[D.M.length][];
        int maxCol=0, maxColOut=0;
        for (int i = 0; i<D.col.length; i++){
            if (D.col[i].length>0){ colI[i]=new int[]{i};
                maxColOut=Math.max (maxCol, i );
                MI[i]=one; maxCol=Math.max (maxCol, D.col[i][0]);}
            else {colI[i]=zeroI; MI[i]=zero;}
        };   maxCol++; maxColOut++;int mmax=Math.max (maxCol,maxColOut);
        Element[][] MJ=new Element[maxCol][0];
        int[][] colJ = new int[maxCol][0];
        for (int i = 0; i<D.col.length; i++){
            if (D.col[i].length>0){  int cc=D.col[i][0];
                colJ[cc]=new int[]{cc}; MJ[cc]=one;} }
        MatrixS I=new MatrixS(D.size, mmax,  MI, colI);
        MatrixS J=new MatrixS(D.size, mmax,  MJ, colJ);
        forMaxColN[0]=maxCol;
        return new MatrixS[]{I,J};
    }
    public MatrixS L() {
        return L;
    }

    public MatrixS D() {
        return D;
    }

    public MatrixS Dhat() {
        return Dhat;
    }

    public MatrixS Dbar() {
        return Dbar;
    }

    public MatrixS U() {
        return U;
    }

    public MatrixS M() {
        return M;
    }

    public MatrixS W() {
        return W;
    }

    public MatrixS I() {
        return I;
    }

    public MatrixS Ibar() {
        return Ibar;
    }

    public MatrixS J() {
        return J;
    }

    public MatrixS Jbar() {
        return Jbar;
    }

    public Element A_n() {
        return a_n;
    }

    public void setD(MatrixS d) {
        D = d;
    }
    public void setDhat(MatrixS dhat) {
        Dhat = dhat;
    }

    public void setDbar(MatrixS dbar) {
        Dbar = dbar;
    }

    public void setM(MatrixS m) {
        M = m;
    }

    public void setW(MatrixS w) {
        W = w;
    }

    public void setI(MatrixS i) {
        I = i;
    }

    public void setIbar(MatrixS ibar) {
        Ibar = ibar;
    }

    public void setJ(MatrixS j) {
        J = j;
    }

    public void setJbar(MatrixS jbar) {
        Jbar = jbar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdumwDto ldumwDto = (LdumwDto) o;
        Ring ring = new Ring("Z[]");

        return L.subtract(ldumwDto.L(), ring).isZero(ring) &&
                D.subtract(ldumwDto.D(), ring).isZero(ring) &&
                U.subtract(ldumwDto.U(), ring).isZero(ring) &&
                M.subtract(ldumwDto.M(), ring).isZero(ring) &&
                W.subtract(ldumwDto.W(), ring).isZero(ring);

    }

    @Override
    public int hashCode() {
        return Objects.hash(L, D, Dhat, Dbar, U, M, W, I, Ibar, J, Jbar, a_n);
    }

    @Override
    public String toString() {
        return "LdumwDto{" +
                "L=" + L +
                ", D=" + D +
                ", U=" + U +
                ", M=" + M +
                ", W=" + W +
                '}';
    }
}
