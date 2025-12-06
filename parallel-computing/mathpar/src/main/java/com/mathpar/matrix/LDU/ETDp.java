package com.mathpar.matrix.LDU;

import com.mathpar.matrix.LDU.TO.*;
import java.io.File;
import java.io.IOException;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NFunctionZ32;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mathpar.matrix.LDU.ETDUtils.join;
import static com.mathpar.matrix.LDU.ETDp.resplitTime;
import static com.mathpar.matrix.LDU.ETDp.resplits;
import static com.mathpar.matrix.LDU.ETDUtils.split;


/**
 *
 * @author ridkeim
 */
public class ETDp implements Serializable{

    
    private int det_sign, rank, maxrow, maxcol, div, divrow, divcol;
    private MatrixS L, U, M, V, K, W, blocks[];
    private Track track;
    private Element[] d;
    private Element a;
    private boolean isZero, hasZeroBlock;
    Ring ring;
    private boolean resplited = false;
    private final static String WRONG_RESULT_MSG = "wrong result";
    public static long time = 0;
    public static long resplitTime = 0;
    public static long resplits = 0;
    private static boolean useResplit = false;
    private final int min_zero_bloc_size_proc = 25;
    private final static MatrixS zero = MatrixS.zeroMatrix();
    
    
    private ETDp(Ring ring) {
        L = zero;
        U = zero;
        M = zero;
        V = zero;
        K = zero;
        W = zero;
        track = new Track();
        this.ring = ring;
        a = this.ring.numberONE;
        d = new Element[0];
    }
     
    public ETDp(MatrixS T,Ring ring) {
        this(ring);        
        maxrow = T.size;
        maxcol = T.colNumb;
        divrow= bit(maxrow);
//        divrow = (maxrow > 1) ? maxrow & (maxrow >> 1) : 1;
        divcol = bit(maxcol);
//        divcol = (maxcol > 1) ? maxcol & (maxcol >> 1) : 1;
        div = Math.min(divrow, divcol);
        divrow = divcol = div;
        isZero = T.isZero(ring);
        if(!isZero && ring.algebra[0] != getTypeOfElemntsFromMatrixS(T)){
            try{
                T = T.toMatrixS(ring.numberONE(), ring);
            }catch(Exception e){
                int t= 0;
            }
        }
        if (!isZero) {
            blocks = split(T, div);
        } else {
            blocks = new MatrixS[] {zero, zero, zero, zero};
        }
        hasZeroBlock = blocks[0].isZero(ring);
    }
    private ETDp(MatrixS T, Element ak, Ring ring) {
        this(T,ring);
        a = ak;        
    }
    
    int bit(int x){
        if(x>4){
            return 4;
        }else if (x>2) {
            return 2;
        } return 1;        
    }
    private ETDp(MatrixS[] S, Element ak, Ring ring) {
        this(ring);
        int r1 = Math.max(S[0].size, S[1].size);
        int r2 = Math.max(S[2].size, S[3].size);
        int c1 = Math.max(S[2].colNumb, S[0].colNumb);
        int c2 = Math.max(S[1].colNumb, S[3].colNumb);
        divrow = r1;
        maxrow = r1 + r2;
        divcol = c1;
        maxcol = c1 + c2;
        div = Math.min(r1, c1);
        blocks = S;
        isZero = true;
        a = ak;
        for (MatrixS block : blocks) {
            isZero &= block.isZero(ring);
        }
        hasZeroBlock = blocks[0].isZero(ring);
        if (!isZero && useResplit) {
            resplit();
        }
    }

    private ETDp(MatrixS[] S, Element ak, int div, Ring ring) {
        this(S, ak, ring);
        if (!resplited) {
            int r2 = Math.max(S[2].size, S[3].size);
            int c2 = Math.max(S[1].colNumb, S[3].colNumb);
            this.maxrow = div + r2;
            this.maxcol = div + c2;
            this.div = div;
        }
    }

    private ETDp(MatrixS[] S, Element ak, int divrow, int divcol, Ring ring) {
        this(S, ak, ring);
        if (!resplited) {
            int r2 = Math.max(S[2].size, S[3].size);
            int c2 = Math.max(S[1].colNumb, S[3].colNumb);
            this.divrow = divrow;
            this.divcol = divcol;
            this.maxrow = divrow + r2;
            this.maxcol = divcol + c2;
            this.div = Math.min(divcol, divrow);
        }
    }

    private Element getA1() {
        return (d.length != 0) ? d[d.length - 1] : a;
    }
    
    public Track getTrack(){
        return track;
    }
    public NumberZ getDet(){
        return (NumberZ)d[d.length-1].toNumber(Ring.Z,ring);
    }
    public NumberZ getMod(){
        return ring.MOD;
    }

    public void getLdu() {
        if (isZero) {
            return;
        }
        int size = Math.min(maxcol, maxrow);
        switch (size) {
            case 1:
                sizeOne();
                break;
            default:
                if (blocks[0].isZero(ring) && hasZeroBlock) {
                    firstZeroBlock();
                } else {
                    withOutZeroBlock();
                }
        }
    }

    private void multiplyDivide(Element s, Element den) {
        L = L.multiplyDivide(s, den, ring);
        U = U.multiplyDivide(s, den, ring);
        W = W.multiplyDivide(s, den, ring);
        K = K.multiplyDivide(s, den, ring);
        V = V.multiplyDivide(s, den, ring);
        M = M.multiplyDivide(s, den, ring);
        a = a.multiply(s, ring).divide(den, ring);
        for (int i = 0; i < d.length; i++) {
            d[i] = d[i].multiply(s, ring).divide(den, ring);
        }
    }

    private void sizeOne() {
        if (0 == div) {
            blocks = split(join(new MatrixS[][] {{blocks[0], blocks[1]}, {blocks[2], blocks[3]}}, new int[] {divrow, maxrow - divrow}, new int[] {divcol, maxcol - divcol}), 1);
            divcol = divrow = div = 1;
        }
        Element a_first = (blocks[0].isZero(ring)) ? ring.numberZERO : blocks[0].getElement(0, 0, ring);
        if (a_first.isZero(ring)) {
            if (maxcol > 1) {
                int first = maxcol;
                for (int i = 0; i < blocks[1].col[0].length; i++) {
                    first = (blocks[1].col[0][i] < first) ? blocks[1].col[0][i] : first;
                }
                a_first = blocks[1].getElement(0, first, ring);
                V = split(blocks[1], 1, first + 1)[1];
                V.colNumb = maxcol - 1;
                track.setColumnPermutatuion(track.getBlockToEndPermutation(0, maxcol - first - divcol, maxcol));
            } else {
                int first = 0;
                for (int i = 0; i < blocks[2].size; i++) {
                    if (blocks[2].M[i].length > 0) {
                        first = i;
                        break;
                    }
                }
                a_first = blocks[2].getElement(first, 0, ring);
                Element[][] Mm = new Element[maxrow - 1][];
                int[][] cols = new int[maxrow - 1][];
                for (int i = 0; i < cols.length; i++) {
                    if (i < blocks[2].size - first - 1) {
                        Mm[i] = blocks[2].M[i + first + 1];
                        cols[i] = blocks[2].col[i + first + 1];
                    } else {
                        Mm[i] = new Element[0];
                        cols[i] = new int[0];
                    }
                }
                track.setRowPermutation(track.getBlockToEndPermutation(0, maxrow - first - divrow, maxrow));
                M = new MatrixS(maxrow - 1, maxcol, Mm, cols);
            }
        } else {
            M = blocks[2];
            V = blocks[1];
        }
        L = new MatrixS(a_first);
        K = new MatrixS(a);
        U = new MatrixS(a_first);
        W = new MatrixS(a);
        d = new Element[] {a_first};
        rank = 1;
    }

    private void firstZeroBlockMin() {

        if (0 != div) {

            ETDp f = new ETDp(blocks[2], a, ring);
            f.getLdu();

            ETDp s = new ETDp(blocks[1], a,ring);
            s.getLdu();
            if (0 != f.rank) {
                s.multiplyDivide(f.getA1(), a);
            }
            track.appendPermutations(f.track, s.track, f.rank, s.rank, divrow, maxrow, divcol, maxcol);            

            blocks = split(blocks[3].permutationOfRows(f.track.getTransposedRowPermutation()).permutationOfColumns(s.track.getTransposedColumnPermutation()), f.rank, s.rank);

            MatrixS V7, V8, M7, D2;
            V7 = f.K.multiplyDivRecursive(blocks[0], f.a, ring);
            V8 = f.K.multiplyDivRecursive(blocks[1], f.a, ring);
            M7 = calculateMatrixForNextStep(blocks[2], V7, f.M, f.d, f.a).multiplyDivRecursive(s.W, s.a, ring);
            MatrixS D1 = calculateMatrixForNextStep(blocks[3], V8, f.M, f.d, f.a);
            D2 = calculateMatrixForNextStep(D1, s.V, M7, s.d, s.a);

            ETDp t = new ETDp(D2, s.getA1(),ring);
            t.getLdu();
            track.appendPermutation(t.track, f.rank+s.rank, maxrow, maxcol);
            getResultWithThree(f, s, t, M7, V7, V8);

        } else {
            ETDp f = null;
            if (0 == divrow && ((maxrow - divrow) > 0) && (divcol > 0)) {
                MatrixS[] tm = split(blocks[2], divcol);
                blocks[0] = tm[0];
                blocks[2] = tm[2];
                tm = split(blocks[3], divcol, maxcol - divcol);
                blocks[1] = tm[0];
                blocks[3] = tm[2];
                f = new ETDp(blocks, a,ring);
            } else if (0 == divcol && ((maxcol - divcol) > 0) && (divrow > 0)) {
                MatrixS[] tm = split(blocks[1], divrow);
                blocks[0] = tm[0];
                blocks[1] = tm[1];
                tm = split(blocks[3], maxrow - divrow, divrow);
                blocks[2] = tm[0];
                blocks[3] = tm[1];
                f = new ETDp(blocks, a,ring);
            } else {
                f = new ETDp(blocks[3], a,ring);
            }
            f.getLdu();

            track = f.track;    
            L = f.L;
            M = f.M;
            K = f.K;

            U = f.U;
            V = f.V;
            W = f.W;

            d = f.d;
            det_sign = f.det_sign;
            rank = f.rank;
        }
    }

    private void firstZeroBlock() {
        if (((0 == divrow) && (0 == divcol)) || ((blocks[2].size <= divrow) && (blocks[1].colNumb <= divcol))) {
            firstZeroBlockMin();
        } else {
            MatrixS tmpblocks[] = split(blocks[2], divrow, divcol);
            MatrixS C0 = tmpblocks[0];
            MatrixS C1 = tmpblocks[2];
            tmpblocks = split(blocks[1], divrow, divcol);
            MatrixS B0 = tmpblocks[0];
            MatrixS B1 = tmpblocks[1];
            tmpblocks = split(blocks[3], divrow, divcol);
            MatrixS D1 = tmpblocks[3];
            int newcol = Math.min(maxcol, 2 * divcol);
            int newrow = Math.min(maxrow, 2 * divrow);
            //TODO: обхеденить с withOutZeroBlock!
            ETDp f = new ETDp(new MatrixS[] {blocks[0], B0, C0, tmpblocks[0]}, a,ring);
            f.getLdu();

            C1 = join(new MatrixS[][] {{C1, tmpblocks[2]}}, new int[] {maxrow - newrow}, new int[] {divcol, newcol - divcol});
            B1 = join(new MatrixS[][] {{B1}, {tmpblocks[1]}}, new int[] {divrow, newrow - divrow}, new int[] {maxcol - newcol});

            track.appendPermutation(f.track, 0, maxrow, maxcol);

            tmpblocks = split(C1.permutationOfColumns(f.track.getTransposedColumnPermutation()), 0, f.rank);
            C0 = tmpblocks[2];
            C1 = tmpblocks[3];
            tmpblocks = split(B1.permutationOfRows(f.track.getTransposedRowPermutation()), f.rank, 0);
            B0 = tmpblocks[1];
            B1 = tmpblocks[3];

            C0 = C0.multiplyDivRecursive(f.W, f.a, ring);
            B0 = f.K.multiplyDivRecursive(B0, f.a, ring);
            C1 = calculateMatrixForNextStep(C1, f.V, C0, f.d, f.a);
            B1 = calculateMatrixForNextStep(B1, B0, f.M, f.d, f.a);
            D1 = calculateMatrixForNextStep(D1, B0, C0, f.d, f.a);

            ETDp s = new ETDp(new MatrixS[] {zero, B1, C1, D1}, f.getA1(), newrow - f.rank, newcol - f.rank,ring);
            s.getLdu();

            track.appendPermutation(s.track, f.rank, maxrow, maxcol);

            C0 = join(new MatrixS[][] {{f.M}, {C0}}, new int[] {newrow - f.rank, maxrow - newrow}, new int[] {f.rank}).permutationOfRows(s.track.getTransposedRowPermutation());
            B0 = join(new MatrixS[][] {{f.V, B0}}, new int[] {f.rank}, new int[] {newcol - f.rank, maxcol - newcol}).permutationOfColumns(s.track.getTransposedColumnPermutation());

            getResultWithTwo(f, s, C0, B0);
        }
    }

    private void withOutZeroBlock() {
        ETDp f = new ETDp(blocks[0], a,ring);
        f.getLdu();
        
        track.appendPermutation(f.track, 0, maxrow, maxcol);

        MatrixS tmpblocks[];
        tmpblocks = split(blocks[1].permutationOfRows(f.track.getTransposedRowPermutation()), f.rank, 0);
        MatrixS B0, B1, C0, C1, D1;
        B0 = tmpblocks[1];
        B1 = tmpblocks[3];
        tmpblocks = split(blocks[2].permutationOfColumns(f.track.getTransposedColumnPermutation()), 0, f.rank);
        C0 = tmpblocks[2];
        C1 = tmpblocks[3];

        C0 = C0.multiplyDivRecursive(f.W, f.a, ring);
        B0 = f.K.multiplyDivRecursive(B0, f.a, ring);
        C1 = calculateMatrixForNextStep(C1, f.V, C0, f.d, f.a);
        B1 = calculateMatrixForNextStep(B1, B0, f.M, f.d, f.a);
        D1 = calculateMatrixForNextStep(blocks[3], B0, C0, f.d, f.a);

        ETDp s = new ETDp(new MatrixS[] {zero, B1, C1, D1}, f.getA1(),ring);
        s.getLdu();

        track.appendPermutation(s.track, f.rank, maxrow, maxcol);

        C0 = join(new MatrixS[][] {{f.M}, {C0}}, new int[] {div - f.rank, maxrow - div}, new int[] {f.rank}).permutationOfRows(s.track.getTransposedRowPermutation());
        B0 = join(new MatrixS[][] {{f.V, B0}}, new int[] {f.rank}, new int[] {div - f.rank, maxcol - div}).permutationOfColumns(s.track.getTransposedColumnPermutation());

        getResultWithTwo(f, s, C0, B0);
    }

    private void getResultWithTwo(ETDp f, ETDp s, MatrixS C0, MatrixS B0) {
        rank = f.rank + s.rank;
        d = new Element[rank];
        System.arraycopy(f.d, 0, d, 0, f.rank);
        System.arraycopy(s.d, 0, d, f.rank, s.rank);
        int cols[] = new int[] {f.rank, s.rank};
        int lrows[] = new int[] {maxrow - rank};
        int ucols[] = new int[] {maxcol - rank};
        MatrixS[] tmpblocks = split(C0, s.rank, f.rank);
        C0 = tmpblocks[0];
        MatrixS C1 = tmpblocks[2];
        tmpblocks = split(B0, f.rank, s.rank);
        B0 = tmpblocks[0];
        MatrixS B1 = tmpblocks[1];
        MatrixS[][] S = new MatrixS[][] {
            {f.L, zero},
            {C0, s.L},};
        L = join(S, cols, cols);

        MatrixS K1 = multiplyCenterDRecursive(s.K.multiply(C0, ring), f.K, f.d, f.a, ring);

        S = new MatrixS[][] {
            {f.K, zero},
            {K1, s.K},};
        K = join(S, cols, cols);

        S = new MatrixS[][] {
            {f.U, B0},
            {zero, s.U},};
        U = join(S, cols, cols);

        K1 = multiplyCenterDRecursive(f.W, B0.multiply(s.W, ring), f.d, f.a, ring);

        S = new MatrixS[][] {
            {f.W, K1},
            {zero, s.W},};
        W = join(S, cols, cols);

        S = new MatrixS[][] {
            {B1},
            {s.V},};
        V = join(S, cols, ucols);

        S = new MatrixS[][] {
            {C1, s.M}};
        M = join(S, lrows, cols);
    }

    private void getResultWithThree(ETDp f, ETDp s, ETDp t, MatrixS M7, MatrixS V7, MatrixS V8) {

        rank = f.rank + s.rank + t.rank;
        int cols[] = new int[] {f.rank, s.rank, t.rank};
        int lrows[] = new int[] {maxrow - div - f.rank - t.rank, div - s.rank};
        int ucols[] = new int[] {maxcol - div - s.rank - t.rank, div - f.rank};
        d = new Element[rank];
        System.arraycopy(f.d, 0, d, 0, f.rank);
        System.arraycopy(s.d, 0, d, f.rank, s.rank);
        System.arraycopy(t.d, 0, d, s.rank + f.rank, t.rank);
        int[] ter = t.track.getTransposedRowPermutation();
        int[] tec = t.track.getTransposedColumnPermutation();

        MatrixS[] Mm = split(f.M.permutationOfRows(ter), t.rank, f.rank);
        MatrixS M2 = Mm[0];
        MatrixS M22 = Mm[2];
        Mm = split(M7.permutationOfRows(ter), t.rank, s.rank);
        M7 = Mm[0];
        MatrixS M77 = Mm[2];

        Mm = split(V8.permutationOfColumns(tec), f.rank, t.rank);
        V8 = Mm[0];
        MatrixS V88 = Mm[1];
        Mm = split(s.V.permutationOfColumns(tec), s.rank, t.rank);
        MatrixS V3 = Mm[0];
        MatrixS V33 = Mm[1];

        MatrixS[][] S = new MatrixS[][] {
            {f.L, zero, zero},
            {zero, s.L, zero},
            {M2, M7, t.L}
        };
        L = join(S, cols, cols);

        MatrixS K1, K2, K3;

        K1 = zero;
        K2 = multiplyCenterDRecursive(t.K.multiply(M2, ring), f.K, f.d, f.a, ring);
        K3 = multiplyCenterDRecursive(t.K.multiply(M7, ring), s.K, s.d, s.a, ring);

        S = new MatrixS[][] {
            {f.K, zero,},
            {K1, s.K,},
            {K2, K3, t.K},};
        K = join(S, cols, cols);

        S = new MatrixS[][] {
            {f.U, V7, V8},
            {zero, s.U, V3},
            {zero, zero, t.U}
        };
        U = join(S, cols, cols);

        K1 = multiplyCenterDRecursive(f.W, V7.multiply(s.W, ring), f.d, f.a, ring);

        int[] fs = new int[] {f.rank, s.rank};
        S = new MatrixS[][] {
            {V8},
            {V3},};
        MatrixS V83 = join(S, fs, new int[] {t.rank});

        S = new MatrixS[][] {
            {f.W, K1}
        };
        MatrixS fWK1 = join(S, new int[] {f.rank}, fs);

        K2 = multiplyCenterDRecursive(fWK1, V83.multiply(t.W, ring), d, f.a, ring);
        K3 = multiplyCenterDRecursive(s.W, V3.multiply(t.W, ring), s.d, s.a, ring);

        S = new MatrixS[][] {
            {f.W, K1, K2},
            {zero, s.W, K3},
            {zero, zero, t.W},};
        W = join(S, cols, cols);

        S = new MatrixS[][] {
            {M22, M77, t.M},
            {zero, s.M, zero}};
        M = join(S, lrows, cols);

        S = new MatrixS[][] {
            {V88, f.V},
            {V33, zero},
            {t.V, zero}
        };
        V = join(S, cols, ucols);
    }

    private void resplit() {
        long times = System.currentTimeMillis();
        int row_proc = (maxrow != 0) ? (divrow * 100) / maxrow : 100;
        int col_proc = (maxcol != 0) ? (divcol * 100) / maxcol : 100;
        if ((div != 0) && (divrow == divcol) && (col_proc < min_zero_bloc_size_proc) && (row_proc < min_zero_bloc_size_proc)) {
            resplited = true;
            resplits++;
            MatrixS tmp = join(new MatrixS[][] {{blocks[0], blocks[1]}, {blocks[2], blocks[3]}}, new int[] {divrow, maxrow - divrow}, new int[] {divcol, maxcol - divcol});
            divrow = (maxrow > 1) ? maxrow / 2 : 1;
            divcol = (maxcol > 1) ? maxcol / 2 : 1;
            div = Math.min(divrow, divcol);
            divrow = divcol = div;
            isZero = tmp.isZero(ring);
            if (!isZero) {
                blocks = split(tmp, div);
            } else {
                blocks = new MatrixS[] {zero, zero, zero, zero};
            }
            hasZeroBlock = blocks[0].isZero(ring);
        }
        resplitTime += (System.currentTimeMillis() - times);
    }

    private MatrixS calculateMatrixForNextStep(MatrixS a, MatrixS b, MatrixS c, Element[] d, Element a0) {
        if (a.size == 0 || a.colNumb == 0) {
            return a;
        }
        if (c.isZero(ring) || b.isZero(ring)) {
            if (d.length == 0) {
                return a;
            } else {
                return a.multiplyDivide(d[d.length - 1], a0, ring);
            }
        }
        Element[][] Mn = new Element[a.size][Math.max(a.colNumb, b.colNumb)];
        for (int i = 0; i < Mn.length; i++) {
            Element[] mnn = Mn[i];
            Element[] cn = c.M[i];
            int[] cc = c.col[i];
            int[] ncc = new int[d.length];
            for (int j = 0; j < ncc.length; j++) {
                ncc[j] = -1;
            }
            for (int j = 0; j < mnn.length; j++) {
                mnn[j] = ring.numberZERO();
            }
            for (int j = 0; j < a.M[i].length; j++) {
                mnn[a.col[i][j]] = a.M[i][j];
            }
            for (int j = 0; j < cc.length; j++) {
                ncc[cc[j]] = j;
            }
            for (int j = 0; j < ncc.length; j++) {
                if (ncc[j] >= 0) {
                    Element[] bm = b.M[j];
                    int[] bc = b.col[j];
                    int[] nbc = new int[b.colNumb];
                    for (int k = 0; k < nbc.length; k++) {
                        nbc[k] = -1;
                    }
                    for (int k = 0; k < bm.length; k++) {
                        nbc[bc[k]] = k;
                        Element akkij = mnn[bc[k]].multiply(d[j], ring);
                        Element aikkj = cn[ncc[j]].multiply(bm[k], ring);
                        Element ak_1 = (j == 0) ? a0 : d[j - 1];
                        Element aij_1 = akkij.
                                subtract(aikkj, ring).
                                divide(ak_1, ring);
                        mnn[bc[k]] = aij_1;
                    }
                    for (int k = 0; k < nbc.length; k++) {
                        if (nbc[k] == -1) {
                            mnn[k] = mnn[k].multiply(d[j], ring).divide((j == 0) ? a0 : d[j - 1], ring);
                        }
                    }
                } else {
                    for (int k = 0; k < mnn.length; k++) {
                        mnn[k] = mnn[k].multiply(d[j], ring).divide((j == 0) ? a0 : d[j - 1], ring);
                    }
                }
            }
        }
        return new MatrixS(Mn, ring);
    }

    public static MatrixS multiplyCenterD(MatrixS a, MatrixS b, Element[] d, Element a0,Ring r) {
        return multiplyCenterD(a, b, d, a0, true, r);
    }
    
    public static MatrixS multiplyCenterDRecursive(MatrixS a, MatrixS b, Element[] d, Element a0, Ring r){
        if(a.isZero(r)|| b.isZero(r)){
            return MatrixS.zeroMatrix(a.size);
        }
        if(a.size <= 1 || b.colNumb <= 1){
            return multiplyCenterD(a, b, d, a0, true,r);
        }else{
            MatrixS[] blocks_a = split(a, a.size/2,d.length);
            MatrixS[] blocks_b = split(b, d.length,b.colNumb/2);
            MatrixS a11 = multiplyCenterDRecursive(blocks_a[0], blocks_b[0], d, a0,r);
            MatrixS a12 = multiplyCenterDRecursive(blocks_a[0], blocks_b[1], d, a0,r);
            MatrixS a21 = multiplyCenterDRecursive(blocks_a[2], blocks_b[0], d, a0,r);
            MatrixS a22 = multiplyCenterDRecursive(blocks_a[2], blocks_b[1], d, a0,r);
            MatrixS result = join(new MatrixS[][]{{a11,a12},{a21,a22}}, new int[]{a.size/2,a.size-a.size/2}, new int[]{b.colNumb/2,b.colNumb-b.colNumb/2});
            return result;
        }
    }
    public static MatrixS multiplyCenterD(MatrixS a, MatrixS b, Element[] d, Element a0, boolean negate,Ring r) {
        // исправить negate в коде везде вызовы со значением true.
        int n = a.col.length;
        int N = b.col.length;
        if ((N == 0) || (n == 0)) {
            return MatrixS.zeroMatrix(a.size);
        }
        int m = b.colNumb;
        Element[][] sumM = new Element[n][]; // new M
        int[][] sumC = new int[n][]; // new col
        int[] flags = new int[m];
        for (int i = 0; i < m; i++) {
            flags[i] = -1;
        }
        int[] Cb, Ca;
        Element[] Ma, Mb;
        Element aa;
        for (int i = 0; i < n; i++) {
            Ma = a.M[i];
            Ca = a.col[i];
            SortedMap<Integer, Integer> map = new TreeMap<>();
            for (int j = 0; j < Ca.length; j++) {
                if (Ca[j] < N) {
                    map.put(Ca[j], j);
                }
            }
            int column = 0, index = 0;
            Element denum;
            switch (map.size()) {
                case 0:
                    sumC[i] = new int[0];
                    sumM[i] = new Element[0];
                    break;
                case 1:
                    for (Map.Entry<Integer, Integer> entrySet : map.entrySet()) {
                        column = entrySet.getKey();
                        index = entrySet.getValue();
                    }
                    aa = Ma[index];
                    Mb = b.M[column];
                    Cb = b.col[column];
                    sumC[i] = Cb;
                    sumM[i] = new Element[Mb.length];
                    denum = d[column].multiply((column > 0) ? d[column - 1] : a0, r);
//                    Element without_negate = new NumberZp((NumberZ)denum);
//                    if (negate) {
//                        denum = denum.negate(r);
//                    }
                    for (int j = 0; j < Mb.length; j++) {
                        sumM[i][j] = aa.multiply(Mb[j], r).divide(denum, r).negate(r);                        
                    }
                    break;
                default:
                    Element[] MMn = new Element[m];
                    for (Map.Entry<Integer, Integer> entrySet : map.entrySet()) {
                        column = entrySet.getKey();
                        index = entrySet.getValue();
                        aa = Ma[index];
                        Mb = b.M[column];
                        Cb = b.col[column];
                        denum = (column > 0) ? d[column - 1] : a0;
                        for (int j = 0; j < Mb.length; j++) {
                            int column_b = Cb[j];
                            int prev_column = flags[column_b];
                            flags[column_b] = column;
                            if (prev_column == -1) {
                                MMn[column_b] = aa.multiply(Mb[j], r).divide(denum, r);
                            } else {
                                Element a_before = MMn[column_b].multiply(d[column], r);
                                if (prev_column != column - 1) {
                                    a_before = a_before.multiply(denum, r).divide(d[prev_column], r);
                                    // check this!!
                                }
                                MMn[column_b] = a_before.add(aa.multiply(Mb[j], r), r).divide(denum, r);
                            }
                        }
                    }
                    int t = 0;
                    int[] CCn = new int[m];
                    for (int l = 0; l < flags.length; l++) {
                        if (flags[l] != -1) {
                            if (MMn[l].signum() != 0) {
                                Element m_result = MMn[l].divide(d[flags[l]], r);
                                CCn[t] = l;
                                MMn[t] = (negate) ? m_result.negate(r) : m_result;
                                t++;
                            }
                            flags[l] = -1;
                        }
                    }
                    if (t == m) {
                        sumC[i] = CCn;
                        sumM[i] = MMn;
                    } else {
                        int[] CC1 = new int[t];
                        Element[] MM1 = new Element[t]; //так как jj ненулей
                        System.arraycopy(CCn, 0, CC1, 0, t);
                        System.arraycopy(MMn, 0, MM1, 0, t);
                        sumC[i] = CC1;
                        sumM[i] = MM1;
                    }
                    break;
            }
        }
        return new MatrixS(a.size, m, sumM, sumC);
    }
    
    public static MatrixS[] LDU(MatrixS S, Ring r) {
        MatrixS[] result = null;
        try {
            result = LDU(S, r, false, false);
        } catch (Exception e) {
        }
        return result;
    }

    private static MatrixS[] LDU(MatrixS S, Ring r, boolean useResplit_, boolean checkResult) throws Exception {
        ETDp a = new ETDp(S,r);
        resplitTime = 0;
        useResplit = useResplit_;
        resplits = 0;
        int row = S.size;
        int col = S.colNumb;
        int r0 = Math.max(row, col);
        Long timeZero = System.currentTimeMillis();
        a.getLdu();
        MatrixS L, D, U;
        int el = r0 - a.rank;
        MatrixS E = MatrixS.scalarMatrix(el, r.numberONE, r);
        if(el == 0){
            L = a.L;
            U = a.U;
        }else{
            L = join(new MatrixS[][] {{a.L, zero}, {a.M, E}},
                    new int[] {a.rank, el},
                    new int[] {a.rank, el});
            U = join(new MatrixS[][] {{a.U, a.V}, {zero, E}},
                    new int[] {a.rank, el},
                    new int[] {a.rank, el});
        }
        D = a.generateD(S.size);
        int[] er = a.track.getRowPermutation();
        int[] ec = a.track.getColumnPermutation();
        
        L = L.permutationOfRows(er).permutationOfColumns(er);
        D = D.permutationOfRows(er).permutationOfColumns(ec);
        U = U.permutationOfRows(ec).permutationOfColumns(ec);
//        long time_ = System.currentTimeMillis() - timeZero;
//        System.out.println("size "+S.size+" time "+time_);
        if (checkResult) {
            Ring ring1 = new Ring(Ring.Q, 0);
            MatrixS LDU = L.multiply(D, ring1).multiply(U, ring1);
            if (!LDU.subtract(S, r).isZero(r)) {
                throw new Exception(WRONG_RESULT_MSG);
            }
        }
        return new MatrixS[] {L, D, U};
    }
    
    public static MatrixS[] ETDmod(MatrixS T,int result_id) throws IOException, Exception{
        int decomposition_count =0;         
        NumberZ det = (NumberZ) T.det(Ring.ringZxyz);
        Element hadamard = T.adamarNumberNotZero(Ring.ringZxyz);    
        NumberZ[] my_primes = NFunctionZ32.primesBigLimit(hadamard, 1);
        System.out.println("primes.count"+my_primes.length);
        Map<Track,ArrayList<ETDpTO>> tracks = new TreeMap<>();
        Map<Track,ArrayList<Map.Entry<NumberZ,NumberZ>>> dets = new TreeMap<>();
        Map<Track,NumberZ> recovered_dets = new TreeMap<>();
        int position = 0;
        Track track = null;
        while((track==null) && position<my_primes.length){
            Ring ring = new Ring(new int[]{Ring.Zp}, new int[]{}, new String[]{});
            ring.setMOD(my_primes[position]);
            ETDp etdp = new ETDp(T, ring);
            
//            System.out.println("founding decomposition with module primes["+(position)+"]="+my_primes[position]);
            etdp.getLdu();
            System.out.println("founded №"+position);
            NumberZ recovered_det_old = recovered_dets.get(etdp.getTrack());
            NumberZ recovered_det = recoverDet(dets.get(etdp.getTrack()),etdp.getMod(),etdp.getDet());
            if(recovered_det_old!=null && (recovered_det.compareTo(recovered_det_old)==0)){
                track = etdp.getTrack();
                continue;
            }
            ArrayList<ETDpTO> p = tracks.get(etdp.getTrack());
            ArrayList<Map.Entry<NumberZ, NumberZ>> d = dets.get(etdp.getTrack());
            if(p==null){
                p = new ArrayList<>();                    
            }
            if(d==null){
                d = new ArrayList<>();
            }
            p.add(etdp.getTO(result_id));
            d.add(new AbstractMap.SimpleEntry<>(etdp.getMod(), etdp.getDet()));
            if(p.size()==1){
                tracks.put(etdp.getTrack(), p);
            }
            if(d.size()==1){
                dets.put(etdp.getTrack(), d);
            }
            recovered_dets.put(etdp.getTrack(), recovered_det);
            if(track==null){
                if(recovered_det.compareTo(hadamard, 2, Ring.ringZxyz)){
                    System.out.println("Recovered max det is bigger than Hadamar");
//                    throw new Exception();
                }
                if(position==my_primes.length-1){
//                    System.out.println("It is not enough primes, generate new primes");
                    my_primes = extendPrimes(2*my_primes.length);
                }
            }
            decomposition_count++;
            position++;
        }        
//        System.out.println("founded decompositon count is "+decomposition_count);
        ArrayList<ETDpTO> etd_for_recovery;
        if(tracks.containsKey(track)){
            etd_for_recovery = tracks.get(track);
        }else{
            etd_for_recovery = new ArrayList<>();
        }
        ETDpTO etd_recovered = ETDpTO.getInstance(result_id);
        etd_recovered.restore(etd_for_recovery,0);
        return etd_recovered.generateResult(T.size,track);
    };
    
    public static MatrixS[] ETD(MatrixS S, int result_id){
        ETDp etd = new ETDp(S, Ring.ringZxyz);
        etd.getLdu();
        ETDpTO result = etd.getTO(result_id);        
        return result.generateResult(S.size, etd.getTrack());
    }
    
    private static NumberZ recoverDet(ArrayList<Map.Entry<NumberZ, NumberZ>> value,NumberZ new_mod,NumberZ new_rem) {
        int size = 0;
        if(value != null){
            size = value.size();
        }
        if(size == 0){
            return new_rem;
        }else{
            NumberZ[] rem = new NumberZ[value.size()+1];
            NumberZ[] mod = new NumberZ[value.size()+1];
            int t = 0;
            for (Map.Entry<NumberZ, NumberZ> entry : value) {
                mod[t] = entry.getKey();
                rem[t] = entry.getValue();
                t++;
            }
            mod[t]=new_mod;
            rem[t]=new_rem;
            return Newton.recoveryNewton(mod, rem);
        }
    }
    
    private MatrixS generateD(int size) {
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        if (d.length > 0) {
            M[0] = new Element[] {new Fraction(ring.numberONE(), d[0])};
            col[0] = new int[] {0};
        }
        for (int i = 1; i < d.length; i++) {
            M[i] = new Element[] {new Fraction(a, d[i - 1].multiply(d[i], ring))};
            col[i] = new int[] {i};
        }
        for (int i = d.length; i < size; i++) {
            M[i] = new Element[0];
            col[i] = new int[0];
        }
        return new MatrixS(size, d.length, M, col);
    }

    public static MatrixS generateD(Element[] d, Element aa) {
        int size = d.length;
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        if (d.length > 0) {
            M[0] = new Element[] {new Fraction(Ring.ringZxyz.numberONE(), aa.multiply(d[0], Ring.ringZxyz))};
            col[0] = new int[] {0};
        }
        for (int i = 1; i < d.length; i++) {
            M[i] = new Element[] {new Fraction(Ring.ringZxyz.numberONE(), d[i - 1].multiply(d[i], Ring.ringZxyz))};
            col[i] = new int[] {i};
        }
        for (int i = d.length; i < size; i++) {
            M[i] = new Element[0];
            col[i] = new int[0];
        }
        return new MatrixS(size, d.length, M, col);
    }
    
    public static int getTypeOfElemntsFromMatrixS(MatrixS T){
        for (Element[] rows : T.M) {
            if(null == rows || 0 == rows.length){
                continue;
            }else{
                return rows[0].numbElementType();
            }
        }
        return -1;
    }
    
    public ETDpTO getTO(int result_id){
        ETDpTO result= null;
        switch(result_id){
            case ETDpTO.RESULT_LDU:
                result= new ETDpLDUTO(L, U, V, M, d, ring.MOD, -1);
                break;
            case ETDpTO.RESULT_WDK:
                result = new ETDpKDWTO(K, W, d, ring.MOD, -1);
                break;
            case ETDpTO.RESULT_LDUWDK:
                result = new ETDpLDUKDWTO(L, U, V, M, K, W, d, ring.MOD, -1);
                break;
            case ETDpTO.RESULT_PLDUQWDK:
                result = new ETDpPQTO(L, U, V, M, K, W, d, ring.MOD, -1);
                break;
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception{
//        MatrixS t = findMatrix(8);
        test();
        
    }
  public static MatrixS adjmod(MatrixS T) throws Exception{
        MatrixS[] results = ETDmod(T, ETDpKDWTO.RESULT_WDK);
        Ring ring1 = new Ring(new int[]{Ring.Q}, new int[]{}, new String[]{});
        MatrixS res = results[0].multiply(results[1], ring1).multiply(results[2], ring1);
//        MatrixS res1 = join(new MatrixS[][] {{res, zero}, {zero, zero}},
//                new int[] {a.rank, el},
//                new int[] {a.rank, el});
//        res = res1.permutationOfRows(ec).permutationOfColumns(er);
//        MatrixS adj = T.adjoint(Ring.ringZxyz);
//        System.out.println(res);
//        System.out.println(adj);
        return res;
  }
    private static void debug(File f) {
        File[] files = f.listFiles();
        for (File file : files) {
            try {
                MatrixS a = null;
                try (ObjectInputStream obin = new ObjectInputStream(new FileInputStream(file))) {
                    a = (MatrixS) obin.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(ETDp.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (a != null) {
                    LDU(a, Ring.ringZxyz, false, false);
                    System.out.println("time without resplit " + time);
                    LDU(a, Ring.ringZxyz, true, false);
                    System.out.println("time with resplit " + time);
                    System.out.println("resplit time " + resplitTime);
                    file.delete();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ETDp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ETDp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static NumberZ[] extendPrimes(int length) throws IOException{
        return NFunctionZ32.primesBig(length);
    }
    



public static MatrixS adj(MatrixS T,Ring r){
        ETDp a = new ETDp(T,r);
        resplits = 0;
        int row = T.size;
        long time11 = System.currentTimeMillis();
        a.getLdu();
        time11 = System.currentTimeMillis()-time11;
        System.out.println("LDUtime = "+time11);
        MatrixS Wd, D, Kd;
        int el = row - a.rank;
        System.out.println((el==0)?"full rank":"not full rank");
        MatrixS E = MatrixS.scalarMatrix(el, r.numberONE, r);
        if(el!=0){
            Wd = join(new MatrixS[][] {{a.W, zero}, {zero, zero}},
                    new int[] {a.rank, el},
                    new int[] {a.rank, el});
            Kd = join(new MatrixS[][] {{a.K, zero}, {zero, zero}},
                    new int[] {a.rank, el},
                    new int[] {a.rank, el});
        }else{
            Wd = a.W;
            Kd = a.K;
        }
//        D = a.generateD(T.size);
        int[] er = a.track.getRowPermutation();
        int[] ec = a.track.getColumnPermutation();
        Ring ring1 = new Ring(new int[]{Ring.Q}, new int[]{}, new String[]{});
        D = a.generateD(a.rank).multiplyByNumber(new NumberZ(a.track.getSignum()).multiply(a.d[a.d.length-1],Ring.ringZxyz),ring1);
        Wd = Wd.permutationOfRows(ec).permutationOfColumns(er);
        D = D.permutationOfRows(er).permutationOfColumns(ec);
        Kd = Kd.permutationOfRows(ec).permutationOfColumns(er);
        
        
        MatrixS res = Wd.multiply(D, ring1).multiply(Kd, ring1);
//        MatrixS res1 = join(new MatrixS[][] {{res, zero}, {zero, zero}},
//                new int[] {a.rank, el},
//                new int[] {a.rank, el});
//        res = res1.permutationOfRows(ec).permutationOfColumns(er);
//        MatrixS adj = T.adjoint(Ring.ringZxyz);
//        System.out.println(res);
//        System.out.println(adj);
        return res;
    }

    public static void test() throws Exception{
        //<editor-fold defaultstate="collapsed" desc="comment">
        int[][] ab = new int[][]{
            {12, 13, 15, 14},
            {3, 13, 12, 11},
            {10, 4, 15, 14},
            {10, 15, 7, 1}, 
        };
        int[][] aa = new int[][]{
            {3,4},
            {3,1},
        };
        
        //</editor-fold>
        MatrixS a = ETDUtils.randomMatrixS(25, Integer.MAX_VALUE, 60, Ring.ringZxyz);
        Long time = System.currentTimeMillis();
        MatrixS[] ETDmodLDUWDK = ETD.ETDmodLDUWDK(a);
        System.out.println("time = "+ (System.currentTimeMillis()-time));
        for (MatrixS ETDmodLDUWDK1 : ETDmodLDUWDK) {
            System.out.println(ETDmodLDUWDK1);
        }
        
    }
    
    public static MatrixS findMatrix(int size, int p){
        while(true){
            MatrixS a = ETDUtils.randomMatrixS(size, p, 60, Ring.ringZpX);
            ETDp et = new ETDp(a, Ring.ringZpX);
            et.getLdu();
            int sign = et.getTrack().getSignum();
            if( size == et.d.length &&sign<0){
                return a;
            }
        }
        
    }
}

