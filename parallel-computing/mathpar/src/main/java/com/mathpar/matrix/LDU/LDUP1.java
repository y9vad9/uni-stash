package com.mathpar.matrix.LDU;

import java.util.Random;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.Ring;

/**
 *
 * @author ridkeim
 */
public class LDUP1 {
    int one = 1;
    int n;
    int r;
    public Element[] d = new Element[0];
    public MatrixS L1;
    public MatrixS L2;
    public MatrixS D;
    public MatrixS U1;
    public MatrixS U2;
    public MatrixS M;
    public MatrixS W;
    public MatrixS A;
    Element a;
    Element a_s;
    public static MatrixS zero = MatrixS.zeroMatrix();
    /**
     * Перестановка строк матрицы S. {i1,.... in,j1...jn} Отсортирована по
     * возрастанию первая половина {i1,.... in}, - это строки отправки.
     * Тождественная перестановка -- это пустой массив.
     */
    public int[] Er = new int[] {};
    /**
     * Перестановка столбцов матрицы S. {i1,.... in,j1...jn} Тождественная
     * перестановка -- это пустой массив.
     */
    public int[] Ec = new int[] {};
    static Ring ring = Ring.ringZxyz;

    public LDUP1(MatrixS T, Element a0) {
        A = T;
        n = Math.min(A.size, A.colNumb);
        L1 = MatrixS.zeroMatrix();
        L2 = MatrixS.zeroMatrix();
        D = MatrixS.zeroMatrix();
        U1 = MatrixS.zeroMatrix();
        U2 = MatrixS.zeroMatrix();
        M = MatrixS.zeroMatrix();
        W = MatrixS.zeroMatrix();
        r = 0;
        a = a0;
        a_s = a0;
    }

    public void getLduQ(int row) {
        if (!A.isZero(ring)) {
            switch (n) {
                case 1:
                    a_s = A.getElement(0, 0, ring);
                    L1 = new MatrixS(a_s);
                    D = new MatrixS(new Fraction(a, a_s.multiply(a, ring))).cancel(ring);
                    U1 = new MatrixS(a_s);
                    M = new MatrixS(a);
                    W = new MatrixS(a);
                    d = new Element[] {a_s};
                    r = 1;
                    break;
                case 2:
                    Element a00 = A.getElement(0, 0, ring);
                    Element a01 = A.getElement(0, 1, ring);
                    Element a10 = A.getElement(1, 0, ring);
                    if (a00.isZero(ring)) {
                        if (a01.isZero(ring) && a10.isZero(ring)) {
                            Ec = blocktoEnd(0, 1, 2);
                            Er = blocktoEnd(0, 1, 2);
                        } else {
                            if (a10.isZero(ring)) {
                                Ec = blocktoEnd(0, 1, 2);
                            } else {
                                Er = blocktoEnd(0, 1, 2);
                            }
                        }
                    }
                    MatrixS tmp = A.permutationOfColumns(Ec).permutationOfRows(Er);
                    a00 = tmp.getElement(0, 0, ring);
                    a01 = tmp.getElement(0, 1, ring);
                    a10 = tmp.getElement(1, 0, ring);
                    Element a_n = tmp.det(ring).divide(a, ring);
                    if (!a_n.isZero(ring)) {
                        L1 = new MatrixS(new Element[][] {
                            {a00, ring.numberZERO},
                            {a10, a_n}}, ring);
                        U1 = new MatrixS(new Element[][] {
                            {a00, a01},
                            {ring.numberZERO, a_n}}, ring);
                        d = new Element[] {a00, a_n};
                        D = getD(d, a, n);
                        M = new MatrixS(new Element[][] {
                            {a, ring.numberZERO},
                            {a10.negate(ring), a00}}, ring);
                        W = new MatrixS(new Element[][] {
                            {a, a01.negate(ring)},
                            {ring.numberZERO, a00}}, ring);
                        r = 2;
                        a_s = a_n;
                    } else {
                        L1 = new MatrixS(a00);
                        L2 = new MatrixS(a10);
                        D = new MatrixS(new Fraction(a, a00.multiply(a, ring))).cancel(ring);
                        U1 = new MatrixS(a00);
                        U2 = new MatrixS(a01);
                        M = new MatrixS(a);
                        W = new MatrixS(a);
                        d = new Element[] {a00};
                        r = 1;
                        a_s = a00;
                    }
                    break;
                default:
                    int div = row / 2;
                    MatrixS[] blocks = split(A, div);
//                    if (blocks[0].isZero(ring)) {
//                        getl(blocks);
//                        return;
//                    }
                    LDUP1 f = new LDUP1(blocks[0], a);
                    f.getLdu(div, div);
                    blocks[1] = blocks[1].permutationOfRows(f.Er);
                    blocks[2] = blocks[2].permutationOfColumns(f.Ec);
                    if (f.r == div) {
                        MatrixS L = blocks[2].multiplyDivRecursive(f.W, a, ring);
                        MatrixS U = f.M.multiplyDivRecursive(blocks[1], a, ring);
                        MatrixS DU = f.D.multiplyByNumber(f.a_s, ring).multiplyRecursive(U, ring);
                        MatrixS LDU = L.multiplyRecursive(DU, ring);
                        blocks[3] = blocks[3].multiplyByNumber(f.a_s, ring);
                        blocks[3] = blocks[3].subtract(LDU, ring).divideByNumber(f.a, ring);
                        LDUP1 second = new LDUP1(blocks[3], f.a_s);
                        second.getLdu(row - f.r, row - f.r);
                        if (second.r != 0) {
                            r = f.r + second.r;
                            L = L.permutationOfRows(second.Er);
                            U = U.permutationOfColumns(second.Ec);
                            d = new Element[r];
                            System.arraycopy(f.d, 0, d, 0, f.r);
                            System.arraycopy(second.d, 0, d, f.r, second.r);
                            if (second.L2.size != 0) {
                                MatrixS[] Ls = splitHorizontal(L, second.r);
                                MatrixS[] Us = splitVertical(U, second.r);
                                L = Ls[0];
                                U = Us[0];
                                L2 = joinVertical(Ls[1], second.L2);
                                U2 = joinHorizontal(Us[1], second.U2);
                            }
                            MatrixS Mkssn_0 = second.M.multiplyRecursive(L, ring);
                            MatrixS Wkssn_0 = f.W.multiplyRecursive(f.D.multiply(U, ring), ring);
                            MatrixS DM = f.D.multiplyRecursive(f.M, ring);
                            MatrixS Mkssn = Mkssn_0.multiplyDivMulRecursive(DM, a, ring.numberMINUS_ONE, ring);
                            MatrixS Wkssn = Wkssn_0.multiplyDivMulRecursive(second.W, a, ring.numberMINUS_ONE, ring);
                            //result
                            int[] secEr = getE(second.Er, f.d.length);
                            int[] secEc = getE(second.Ec, f.d.length);
                            Er = MatrixS.multPermutations(f.Er, secEr, row);
                            Ec = MatrixS.multPermutations(f.Ec, secEc, row);
                            D = getD(d, a, d.length);
                            L1 = join(
                                    f.L1, zero,
                                    L, second.L1);
                             U1 = join(
                                    f.U1, U,
                                    zero, second.U1);
                            M = join(
                                    f.M, zero,
                                    Mkssn, second.M);

                            W = join(
                                    f.W, Wkssn,
                                    zero, second.W);
                            a_s = second.a_s;
                        } else {
                            L1 = f.L1;
                            L2 = L;
                            U1 = f.U1;
                            U2 = U;
                            M = f.M;
                            W = f.W;
                            D = f.D;
                            d = f.d;
                            r = f.r;
                            Er = f.Er;
                            Ec = f.Ec;
                            a_s = f.a_s;
                            r = f.r;
                        }
                    } else {
                        MatrixS blockC[] = splitVertical(blocks[2], f.r);
                        MatrixS blockB[] = splitHorizontal(blocks[1], f.r);
                        MatrixS C0 = blockC[0].multiplyDivRecursive(f.W, a, ring);
                        MatrixS B0 = f.M.multiplyDivRecursive(blockB[0], a, ring);
                        MatrixS C1 = blockC[1].subtract(C0.multiply(f.D.multiply(f.U2, ring), ring), ring).multiplyDivide(f.a_s, f.a, ring);
                        MatrixS B1 = blockB[1].subtract(f.L2.multiply(f.D.multiply(B0, ring), ring), ring).multiplyDivide(f.a_s, f.a, ring);
                        MatrixS D1 = blocks[3].subtract(C0.multiply(f.D.multiply(B0, ring), ring), ring).multiplyDivide(f.a_s, f.a, ring);
                        if (C1.isZero(ring) ^ B1.isZero(ring)) {
                            // или B1 или С1 нулевая
                            LDUP1 s;
                            LDUP1 t;
                            MatrixS U11, U12, U13, L11, L12, L13;
                            MatrixS U21, U22, U23, U24, U25, U26, L21, L22, L23, L24, L25, L26;
                            int ucols[], lrows[];
                            if (!C1.isZero(ring)) {
                                s = new LDUP1(C1, f.a_s);
                                s.getLdu(row - div, div - f.r);
                                MatrixS[] Cs = splitHorizontal(C0.permutationOfRows(s.Er), s.r);
                                MatrixS[] V0 = splitVertical(f.U2.permutationOfColumns(s.Ec), s.r);
                                MatrixS[] Ds = splitHorizontal(D1.permutationOfRows(s.Er), s.r);
                                Ds[0] = s.M.multiplyDivRecursive(Ds[0], s.a, ring);
                                Ds[1] = Ds[1].subtract(s.L2.multiplyRecursive(s.D.multiplyRecursive(Ds[0], ring), ring), ring).multiplyDivide(s.a_s, s.a, ring);
                                t = new LDUP1(Ds[1], s.a_s);
                                t.getLdu(row - div - s.r, row - div);
                                Er = MatrixS.multPermutations(MatrixS.multPermutations(Er, f.Er, row), MatrixS.multPermutations(blocktoEnd(f.r, row - div, row), getE(s.Er, f.r), row), row);
                                Er = MatrixS.multPermutations(Er, getE(t.Er, f.r + s.r), row);
                                Ec = MatrixS.multPermutations(MatrixS.multPermutations(Ec, f.Ec, row), MatrixS.multPermutations(getE(s.Ec, f.r), blocktoEnd(f.r + s.r, row - div, row), row), row);
                                Ec = MatrixS.multPermutations(Ec, getE(t.Ec, f.r + s.r), row);
                                MatrixS[] Bt = splitVertical(B0.permutationOfColumns(t.Ec), t.r);
                                MatrixS[] Dt = splitVertical(Ds[0].permutationOfColumns(t.Ec), t.r);
                                MatrixS[] Ct = splitHorizontal(Cs[1].permutationOfRows(t.Er), t.r);
                                MatrixS[] N1 = splitHorizontal(s.L2.permutationOfRows(t.Er), t.r);
                                U11 = V0[0];
                                U12 = Bt[0];
                                U13 = Dt[0];
                                L11 = Cs[0];
                                L12 = Ct[0];
                                L13 = N1[0];
                                U21 = Bt[1];
                                U22 = V0[1];
                                U23 = Dt[1];
                                U24 = s.U2;
                                U25 = t.U2;
                                U26 = zero;
                                L21 = Ct[1];
                                L22 = N1[1];
                                L23 = t.L2;
                                L24 = f.L2;
                                L25 = zero;
                                L26 = zero;
                                ucols = new int[] {row - div - t.r, div - f.r - s.r};
                                lrows = new int[] {row - div - s.r - t.r, div - f.r};
                            } else {
                                s = new LDUP1(B1, f.a_s);
                                s.getLdu(div - f.r, row - div);
                                MatrixS[] Bs = splitVertical(B0.permutationOfColumns(s.Ec), s.r);
                                MatrixS[] N0 = splitHorizontal(f.L2.permutationOfRows(s.Er), s.r);
                                MatrixS[] Ds = splitVertical(D1.permutationOfColumns(s.Ec), s.r);
                                Ds[0] = Ds[0].multiplyDivRecursive(s.W, s.a, ring);
                                Ds[1] = Ds[1].subtract(Ds[0].multiplyRecursive(s.D.multiplyRecursive(s.U2, ring), ring), ring).multiplyDivide(s.a_s, s.a, ring);
                                t = new LDUP1(Ds[1], s.a_s);
                                t.getLdu(row - div, row - div - s.r);
                                Er = MatrixS.multPermutations(MatrixS.multPermutations(Er, f.Er, row), MatrixS.multPermutations(getE(s.Er, f.r), blocktoEnd(f.r + s.r, row - div, row), row), row);
                                Er = MatrixS.multPermutations(Er, getE(t.Er, f.r + s.r), row);
                                Ec = MatrixS.multPermutations(MatrixS.multPermutations(Ec, f.Ec, row), MatrixS.multPermutations(blocktoEnd(f.r, row - div, row), getE(s.Ec, f.r), row), row);
                                Ec = MatrixS.multPermutations(Ec, getE(t.Ec, f.r + s.r), row);
                                MatrixS[] Bt = splitVertical(Bs[1].permutationOfColumns(t.Ec), t.r);
                                MatrixS[] V1 = splitVertical(s.U2.permutationOfColumns(t.Ec), t.r);
                                MatrixS[] Ct = splitHorizontal(C0.permutationOfRows(t.Er), t.r);
                                MatrixS[] Dt = splitHorizontal(Ds[0].permutationOfRows(t.Er), t.r);
                                U11 = Bs[0];
                                U12 = Bt[0];
                                U13 = V1[0];
                                L11 = N0[0];
                                L12 = Ct[0];
                                L13 = Dt[0];
                                U21 = Bt[1];
                                U22 = f.U2;
                                U23 = V1[1];
                                U24 = zero;
                                U25 = t.U2;
                                U26 = zero;
                                L21 = Ct[1];
                                L22 = Dt[1];
                                L23 = t.L2;
                                L24 = N0[1];
                                L25 = s.L2;
                                L26 = zero;
                                ucols = new int[] {row - div - s.r - t.r, div - f.r};
                                lrows = new int[] {row - div - t.r, div - f.r - s.r};
                            }
                            MatrixS WD = f.W.multiply(f.D, ring);
                            MatrixS W01 = WD.multiplyRecursive(U11, ring).multiplyDivMulRecursive(s.W, f.a, ring.numberMINUS_ONE, ring);
                            MatrixS W02 = WD.multiplyRecursive(U12, ring).add(W01.multiplyDivMulRecursive(s.D, f.a_s, f.a, ring).multiplyRecursive(U13, ring), ring).multiplyDivMulRecursive(t.W, f.a, ring.numberMINUS_ONE, ring);
                            MatrixS W12 = s.W.multiplyRecursive(s.D, ring).multiplyRecursive(U13, ring).multiplyDivMulRecursive(t.W, f.a_s, ring.numberMINUS_ONE, ring);
                            MatrixS DM = f.D.multiply(f.M, ring);
                            MatrixS M10 = s.M.multiplyRecursive(L11, ring).multiplyDivMulRecursive(DM, f.a, ring.numberMINUS_ONE, ring);
                            MatrixS M20 = t.M.multiplyDivMulRecursive(L12.multiplyRecursive(DM, ring).add(L13.multiplyDivMulRecursive(s.D, f.a_s, f.a, ring).multiplyRecursive(M10, ring), ring), f.a, ring.numberMINUS_ONE, ring);
                            MatrixS M21 = t.M.multiplyDivMulRecursive(L13.multiplyRecursive(s.D, ring).multiplyRecursive(s.M, ring), f.a_s, ring.numberMINUS_ONE, ring);
                            int[] cols = new int[] {f.r, s.r, t.r};
                            MatrixS[][] S = new MatrixS[][] {
                                {f.M, zero, zero},
                                {M10, s.M, zero},
                                {M20, M21, t.M}
                            };
                            M = join(S, cols, cols);
                            S = new MatrixS[][] {
                                {f.W, W01, W02},
                                {zero, s.W, W12},
                                {zero, zero, t.W}
                            };
                            W = join(S, cols, cols);
                            S = new MatrixS[][] {
                                {f.L1, zero, zero},
                                {L11, s.L1, zero},
                                {L12, L13, t.L1}
                            };
                            L1 = join(S, cols, cols);
                            S = new MatrixS[][] {
                                {f.U1, U11, U12},
                                {zero, s.U1, U13},
                                {zero, zero, t.U1}
                            };
                            U1 = join(S, cols, cols);
                            S = new MatrixS[][] {
                                {L21, L22, L23},
                                {L24, L25, L26},};
                            L2 = join(S, lrows, cols);
                            S = new MatrixS[][] {
                                {U21, U22},
                                {U23, U24},
                                {U25, U26}
                            };
                            U2 = join(S, cols, ucols);
                            r = f.r + s.r + t.r;
                            d = new Element[r];
                            System.arraycopy(f.d, 0, d, 0, f.r);
                            System.arraycopy(s.d, 0, d, f.r, s.r);
                            System.arraycopy(t.d, 0, d, s.r + f.r, t.r);
                            D = getD(d, a, r);
                            a_s = t.a_s;
                        } else {
                            if (C1.isZero(ring)) {
                                //C1 и В1 нулевые
                                LDUP1 s = new LDUP1(D1, f.a_s);
                                s.getLdu(row - div, row - div);
                                Er = MatrixS.multPermutations(MatrixS.multPermutations(Er, f.Er, row), MatrixS.multPermutations(blocktoEnd(f.r, row - div, row), getE(s.Er, f.r), row), row);
                                Ec = MatrixS.multPermutations(MatrixS.multPermutations(Ec, f.Ec, row), MatrixS.multPermutations(blocktoEnd(f.r, row - div, row), getE(s.Ec, f.r), row), row);
                                MatrixS[] Cs = splitHorizontal(C0.permutationOfRows(s.Er), s.r);
                                MatrixS[] Bs = splitVertical(B0.permutationOfColumns(s.Ec), s.r);
                                L1 = join(f.L1, zero, Cs[0], s.L1);
                                U1 = join(f.U1, Bs[0], zero, s.U1);
                                L2 = join(Cs[1], s.L2, f.L2, zero);
                                U2 = join(Bs[1], f.U2, s.U2, zero);
                                MatrixS Mksn = s.M.multiplyRecursive(Cs[0], ring).multiplyRecursive(f.D, ring).multiplyDivMulRecursive(f.M, f.a, ring.numberMINUS_ONE, ring);
                                MatrixS Wksn = f.W.multiplyRecursive(f.D, ring).multiplyRecursive(Bs[0], ring).multiplyDivMulRecursive(s.W, f.a, ring.numberMINUS_ONE, ring);
                                W = join(f.W, Wksn, zero, s.W);
                                M = join(f.M, zero, Mksn, s.M);
                                r = f.r + s.r;
                                d = new Element[r];
                                System.arraycopy(f.d, 0, d, 0, f.r);
                                System.arraycopy(s.d, 0, d, f.r, s.r);
                                D = getD(d, a, r);
                                a_s = s.a_s;
                            } else {
                                //C1 и В1 не нулевые
                                MatrixS U11, U12, U13, U14, U15, U16, L11, L12, L13, L14, L15, L16;
                                MatrixS U21, U22, U23, U24, U25, U26, U27, U28, L21, L22, L23, L24, L25, L26, L27, L28;
                                LDUP1 s = new LDUP1(C1, f.a_s);
                                s.getLdu(row - div, div - f.r);
                                Er = MatrixS.multPermutations(MatrixS.multPermutations(Er, f.Er, row), MatrixS.multPermutations(blocktoEnd(f.r, row - div, row), getE(s.Er, f.r), row), row);
                                Ec = MatrixS.multPermutations(MatrixS.multPermutations(Ec, f.Ec, row), MatrixS.multPermutations(getE(s.Ec, f.r), blocktoEnd(f.r + s.r, row - div, row), row), row);
                                MatrixS[] Cs = splitHorizontal(C0.permutationOfRows(s.Er), s.r);
                                L11 = Cs[0];
                                MatrixS[] V0 = splitVertical(f.U2.permutationOfColumns(s.Ec), s.r);
                                U11 = V0[0];
                                U22 = V0[1];
                                MatrixS[] Ds = splitHorizontal(D1.permutationOfRows(s.Er), s.r);
                                Ds[0] = s.M.multiplyDivRecursive(Ds[0], s.a, ring);
                                Ds[1] = Ds[1].subtract(s.L2.multiplyRecursive(s.D.multiplyRecursive(Ds[0], ring), ring), ring).multiplyDivide(s.a_s, s.a, ring);
                                if (B1.size > 2) {
                                    int tssss = 0;
                                }
//                                LDUP1 t1 = new LDUP1(B1, s.a);
//                                t1.getLdu(div - f.r,row-div);
                                B1 = B1.multiplyDivide(s.a_s, s.a, ring);
                                LDUP1 t = new LDUP1(B1, s.a_s);
                                t.getLdu(div - f.r, row - div);
                                Er = MatrixS.multPermutations(Er, MatrixS.multPermutations(blocktoEnd(f.r + s.r, div - f.r, row), getE(t.Er, f.r + s.r), n), row);

                                Ec = MatrixS.multPermutations(Ec, getE(t.Ec, f.r + s.r), row);
                                MatrixS[] Dt = splitVertical(Ds[0].permutationOfColumns(t.Ec), t.r);
                                U14 = Dt[0];
                                MatrixS[] N0 = splitHorizontal(f.L2.permutationOfRows(t.Er), t.r);
                                L12 = N0[0];
                                L13 = zero;
                                L25 = N0[1];
                                L26 = zero;
                                L27 = t.L2;
                                L28 = zero;
                                MatrixS[] Bt = splitVertical(B0.permutationOfColumns(t.Ec), t.r);
                                U12 = Bt[0];
                                MatrixS[] Df = splitVertical(Ds[1].permutationOfColumns(t.Ec), t.r);
                                Df[0] = Df[0].multiplyDivRecursive(t.W, t.a, ring);
                                Df[1] = Df[1].subtract(Df[0].multiplyRecursive(t.D.multiplyRecursive(t.U2, ring), ring), ring).multiplyDivide(t.a_s, t.a, ring);
                                LDUP1 fh = new LDUP1(Df[1], t.a_s);
                                fh.getLdu(row - div - s.r, row - div - t.r);
                                Ec = MatrixS.multPermutations(Ec, getE(fh.Ec, f.r + s.r + t.r), row);
                                Er = MatrixS.multPermutations(Er, MatrixS.multPermutations(blocktoEnd(f.r + s.r + t.r, row - div - s.r, row), getE(fh.Er, f.r + s.r + t.r), row), row);
                                MatrixS[] Cf = splitHorizontal(Cs[1].permutationOfRows(fh.Er), fh.r);
                                L14 = Cf[0];
                                L21 = Cf[1];
                                MatrixS[] N1 = splitHorizontal(s.L2.permutationOfRows(fh.Er), fh.r);
                                L15 = N1[0];
                                L22 = N1[1];
                                MatrixS[] Dff = splitHorizontal(Df[0].permutationOfRows(fh.Er), fh.r);
                                L16 = Dff[0];
                                L23 = Dff[1];
                                L24 = fh.L2;
                                MatrixS[] Bf = splitVertical(Bt[1].permutationOfColumns(fh.Ec), fh.r);
                                U13 = Bf[0];
                                U21 = Bf[1];
                                MatrixS[] Dtf = splitVertical(Dt[1].permutationOfColumns(fh.Ec), fh.r);
                                U15 = Dtf[0];
                                U23 = Dtf[1];
                                MatrixS[] V2 = splitVertical(t.U2.permutationOfColumns(fh.Ec), fh.r);
                                U16 = V2[0];
                                U25 = V2[1];
                                U24 = s.U2;
                                U26 = zero;
                                U27 = fh.U2;
                                U28 = zero;
//                                L11 = Cs[0];L12=N0[0];L13=zero;L14=Cf[0];L15=N1[0];L16=Dff[0];
//                                U11 = V0[0];U12 = Bt[0];U13=Bf[0];U14=Dt[0];U15=Dtf[0];U16=V2[0];
//                                L21 = Cf[1];L22 = N1[1];L23=Dff[1];L24=fh.L2;L25=N1[1];L26=zero;L27=t.L2;L28=zero;
//                                U21 = Bf[1]; U22 = V0[1];U23 = Dtf[1];U24=s.U2;U25=V2[1];U26=zero;U27=fh.U2;U28=zero;
                                MatrixS WD = f.W.multiply(f.D, ring);
                                MatrixS W01 = WD.multiplyRecursive(U11, ring).multiplyDivMulRecursive(s.W, f.a, ring.numberMINUS_ONE, ring);
                                MatrixS W02 = WD.multiplyRecursive(U12, ring).add(W01.multiplyDivMulRecursive(s.D, f.a_s, f.a, ring).multiplyRecursive(U14, ring), ring).multiplyDivMulRecursive(t.W, f.a, ring.numberMINUS_ONE, ring);
                                MatrixS W12 = s.W.multiplyRecursive(s.D, ring).multiplyRecursive(U14, ring).multiplyDivMulRecursive(t.W, f.a_s, ring.numberMINUS_ONE, ring);
                                MatrixS W03 = WD.multiplyRecursive(U13, ring).add(W01.multiplyRecursive(s.D, ring).multiplyDivMulRecursive(U15, f.a_s, f.a, ring), ring).add(W02.multiplyDivMulRecursive(t.D, s.a_s, f.a, ring).multiplyRecursive(U16, ring), ring).multiplyDivMulRecursive(fh.W, f.a, ring.numberMINUS_ONE, ring);
                                MatrixS W13 = s.W.multiplyDivMulRecursive(s.D, f.a_s, f.a, ring).multiplyRecursive(U15, ring).add(W12.multiplyDivMulRecursive(t.D, s.a_s, f.a, ring).multiplyRecursive(U16, ring), ring).multiplyDivMulRecursive(fh.W, f.a, ring.numberMINUS_ONE, ring);
                                MatrixS W23 = t.W.multiplyRecursive(t.D, ring).multiplyRecursive(U16, ring).multiplyDivMulRecursive(fh.W, s.a_s, ring.numberMINUS_ONE, ring);
                                MatrixS DM = f.D.multiply(f.M, ring);
                                MatrixS M10 = s.M.multiplyRecursive(L11, ring).multiplyDivMulRecursive(DM, f.a, ring.numberMINUS_ONE, ring);
                                MatrixS M20 = t.M.multiplyDivMulRecursive(L12.multiplyRecursive(DM, ring).add(L13.multiplyDivMulRecursive(s.D, f.a_s, f.a, ring).multiplyRecursive(M10, ring), ring), f.a, ring.numberMINUS_ONE, ring);
                                MatrixS M21 = t.M.multiplyDivMulRecursive(L13.multiplyRecursive(s.D, ring).multiplyRecursive(s.M, ring), f.a_s, ring.numberMINUS_ONE, ring);
                                MatrixS mm = L14.multiplyRecursive(DM, ring).add(L15.multiplyDivMulRecursive(s.D, f.a_s, f.a, ring).multiplyRecursive(M10, ring), ring).add(L16.multiplyDivMulRecursive(t.D, s.a_s, f.a, ring).multiplyRecursive(M20, ring), ring);
                                MatrixS M30 = fh.M.multiplyDivMulRecursive(mm, f.a, ring.numberMINUS_ONE, ring);
                                mm = L15.multiplyDivMulRecursive(s.D, f.a_s, f.a, ring).multiplyRecursive(s.M, ring).add(L16.multiplyDivMulRecursive(t.D, s.a_s, f.a, ring).multiplyRecursive(M21, ring), ring);
                                MatrixS M31 = fh.M.multiplyDivMulRecursive(mm, f.a, ring.numberMINUS_ONE, ring);
                                mm = L16.multiplyDivMulRecursive(t.D, s.a_s, f.a, ring).multiplyRecursive(t.M, ring);
                                MatrixS M32 = fh.M.multiplyDivMulRecursive(mm, f.a, ring.numberMINUS_ONE, ring);
                                r = f.r + s.r + t.r + fh.r;
                                int cols[] = new int[] {f.r, s.r, t.r, fh.r};
                                int lrows[] = new int[] {row - div - s.r - fh.r, div - f.r - t.r};
                                int ucols[] = new int[] {row - div - t.r - fh.r, div - f.r - s.r};
                                MatrixS[][] S = new MatrixS[][] {
                                    {f.M, zero, zero, zero},
                                    {M10, s.M, zero, zero},
                                    {M20, M21, t.M, zero},
                                    {M30, M31, M32, fh.M},};
                                M = join(S, cols, cols);
                                S = new MatrixS[][] {
                                    {f.W, W01, W02, W03},
                                    {zero, s.W, W12, W13},
                                    {zero, zero, t.W, W23},
                                    {zero, zero, zero, fh.W},};
                                W = join(S, cols, cols);
                                S = new MatrixS[][] {
                                    {f.L1, zero, zero},
                                    {L11, s.L1, zero},
                                    {L12, L13, t.L1, zero},
                                    {L14, L15, L16, fh.L1}
                                };
                                L1 = join(S, cols, cols);
                                S = new MatrixS[][] {
                                    {f.U1, U11, U12, U13},
                                    {zero, s.U1, U14, U15},
                                    {zero, zero, t.U1, U16},
                                    {zero, zero, zero, fh.U1}
                                };
                                U1 = join(S, cols, cols);
                                S = new MatrixS[][] {
                                    {L21, L22, L23, L24},
                                    {L25, L26, L27, L28},};
                                L2 = join(S, lrows, cols);
                                S = new MatrixS[][] {
                                    {U21, U22},
                                    {U23, U24},
                                    {U25, U26},
                                    {U27, U28}
                                };
                                U2 = join(S, cols, ucols);
                                d = new Element[r];
                                System.arraycopy(f.d, 0, d, 0, f.r);
                                System.arraycopy(s.d, 0, d, f.r, s.r);
                                System.arraycopy(t.d, 0, d, s.r + f.r, t.r);
                                System.arraycopy(fh.d, 0, d, t.r + s.r + f.r, fh.r);
                                D = getD(d, a, r);
                                a_s = fh.a_s;
                            }
                        }
                    }
            }
        }
    }

    public void getl(MatrixS[] S) {
        int n = S[1].size;
        int k = S[3].size;
        LDUP1 f = new LDUP1(S[2], a);
        f.getLdu(k, n);
        if (f.r == 0) {
            Ec = MatrixS.multPermutations(Ec, blocktoEnd(0, k, n + k), n + k);
        } else {
            Ec = MatrixS.multPermutations(Ec, blocktoEnd(f.r, k, n + k), n + k);
            Er = MatrixS.multPermutations(Er, getE(f.Er, n), n + k);
            Er = MatrixS.multPermutations(Er, blocktoEnd(0, f.r, n + f.r), n + k);
            S[3] = S[3].permutationOfRows(f.Er);
        }
        MatrixS[] Df = splitHorizontal(S[3], f.r);
        Df[0] = f.M.multiplyDivRecursive(Df[0], f.a, ring);
        Df[1] = Df[1].subtract(f.L2.multiplyRecursive(f.D.multiplyRecursive(Df[0], ring), ring), ring).divideMultiply(f.a, f.a_s, ring);
        if (f.r >= 0) {
            S[1] = S[1].divideMultiply(f.a, f.a_s, ring);
        }
        LDUP1 s = new LDUP1(S[1], f.a_s);
        s.getLdu(n, k);
        if (s.r == 0) {
            Er = MatrixS.multPermutations(Er, blocktoEnd(f.r, k - f.r, n + k), n + k);
        } else {
            Er = MatrixS.multPermutations(Er, blocktoEnd(f.r + s.r, k - f.r, n + k), n + k);
        }
        MatrixS[] Dfs = splitVertical(Df[0].permutationOfColumns(s.Ec), s.r);
        MatrixS[] Ds = splitVertical(Df[1].permutationOfColumns(s.Ec), s.r);
        Ds[0] = Ds[0].multiplyDivRecursive(s.W, s.a, ring);
        Ds[1] = Ds[1].subtract(Ds[0].multiplyRecursive(s.D, ring).multiplyRecursive(s.L2, ring), ring).multiplyDivide(s.a_s, s.a.multiply(f.a, ring), ring);
        LDUP1 t = new LDUP1(Ds[1], s.a_s);
        t.getLdu(k - f.r, k - s.r);
        MatrixS N0[] = splitHorizontal(f.L2.permutationOfRows(t.Er), t.r);
        MatrixS Dt[] = splitHorizontal(Ds[0].permutationOfRows(t.Er), t.r);
        MatrixS Dst[] = splitVertical(Dfs[1].permutationOfColumns(t.Ec), t.r);
        MatrixS V1[] = splitVertical(s.U2.permutationOfColumns(t.Ec), t.r);
        d = new Element[f.d.length + s.d.length + t.d.length];
        System.arraycopy(f.d, 0, d, 0, f.d.length);
        System.arraycopy(s.d, 0, d, f.d.length, s.d.length);
        System.arraycopy(t.d, 0, d, s.d.length + f.d.length, t.d.length);
        MatrixS Mkfs = s.M.multiplyRecursive(zero, ring).multiplyRecursive(f.D, ring).multiplyDivRecursive(f.M, a.multiply(ring.numberMINUS_ONE, ring), ring);
        MatrixS Wkfs = f.W.multiplyRecursive(f.D, ring).multiplyRecursive(Dfs[0], ring).multiplyDivRecursive(s.W, a.multiply(ring.numberMINUS_ONE, ring), ring);
        MatrixS Mfs = join(f.M, zero, Mkfs, s.M);
        MatrixS Wfs = join(f.W, Wkfs, zero, s.W);
        D = getD(d, a, f.r + s.r);
        int cols[] = new int[] {f.r, s.r};
//        Mkfs = (D.size==0)?zero:t.M.multiplyRecursive(join(new MatrixS[][]{{N0[0],Dt[0]}}, cols), ring).multiplyRecursive(D, ring).multiplyDivRecursive(Mfs, a.multiply(ring.numberMINUS_ONE, ring), ring);
        Wkfs = Wfs.multiplyRecursive(D, ring).multiplyRecursive(joinHorizontal(Dst[0], V1[0]), ring).multiplyDivRecursive(t.W, a.multiply(ring.numberMINUS_ONE, ring), ring);
        M = join(Mfs, zero, Mkfs, t.M);
        W = join(Wfs, Wkfs, zero, t.W);
        cols = new int[] {
            f.r, s.r, t.r
        };
//        L1 = join(new MatrixS[][] {
//            {f.L1, zero, zero},
//            {zero, s.L1, zero},
//            {N0[0], Dt[0], t.L1}
//        }, cols);
//        U1 = join(new MatrixS[][] {
//            {f.U1, Dfs[0], Dst[0]},
//            {zero, s.U1, V1[0]},
//            {zero, zero, t.U1}
//        }, cols);
//        L2 = join(new MatrixS[][] {
//            {N0[1], Dt[1], t.L2},
//            {zero, s.L2, zero}}, cols);
        cols = new int[] {k - s.r - t.r, n - f.r};
//        U2 = join(new MatrixS[][] {
//            {Dst[1], f.U2},
//            {V1[1],zero},
//            {t.U2, zero}}, cols);
        r = f.r + s.r + t.r;
        D = getD(d, a, r);
//        cols = getE(s.Er, f.r);
        int[] sec = getE(t.Er, f.r + s.r);
        Er = MatrixS.multPermutations(Er, sec, n + k);
//        cols = getE(s.Ec, f.r);
        sec = getE(t.Ec, f.r + s.r);
        Ec = MatrixS.multPermutations(Ec, sec, n + k);
        a_s = t.a_s;

    }

    public void mult(Element a0, Element a1) {
        L1 = L1.divideMultiply(a0, a1, ring);
        L2 = L2.divideMultiply(a0, a1, ring);
        U1 = U1.divideMultiply(a0, a1, ring);
        U2 = U2.divideMultiply(a0, a1, ring);
        M = M.divideMultiply(a0, a1, ring);
        W = W.divideMultiply(a0, a1, ring);
        D = D.divideMultiply(a1, a0, ring);
        for (int i = 0; i < d.length; i++) {
            d[i] = d[i].multiply(a1, ring).divide(a0, ring);
        }
        a_s = d[d.length - 1];
    }

    public void getLduV(int row, int col) {
        int div = row / 2;
        if (div < col) {
            div = col;
        }
        MatrixS blocks[] = splitHorizontal(A, div);
        MatrixS a0 = blocks[0];
        MatrixS a1 = blocks[1];
        if (a0.isZero(ring)) {
            Er = blocktoEnd(0, row - div, row);
            div = row - div;
            a0 = blocks[1];
            a1 = blocks[0];
        }
        LDUP1 f = new LDUP1(a0, a);
        f.getLdu(div, col);
        if (!a1.isZero(ring)) {
            MatrixS[][] S;
            MatrixS bblocks[] = splitVertical(a1.permutationOfColumns(f.Ec), f.r);
            MatrixS C0 = bblocks[0].multiplyDivRecursive(f.W, f.a, ring);
            MatrixS C1 = bblocks[1];
            C1 = C1.subtract(C0.multiplyRecursive(f.D, ring).multiplyRecursive(f.U2, ring), ring).multiplyDivide(f.a_s, a, ring);
            if (C1.isZero(ring)) {
                U2 = f.U2;
                L1 = f.L1;
                U1 = f.U1;
                M = f.M;
                W = f.W;
                d = f.d;
                D = f.D;
                a_s = f.a_s;
                Er = MatrixS.multPermutations(Er, f.Er, row);
                Ec = f.Ec;
                r = f.r;
                S = new MatrixS[][] {
                    {f.L2},
                    {C0},};
                L2 = join(S, new int[] {div - f.r, row - div}, new int[] {f.r});
            } else {
                LDUP1 s = new LDUP1(C1, f.a_s);
                s.getLdu(row - div, col - f.r);
                MatrixS[] C = splitHorizontal(C0.permutationOfRows(s.Er), s.r);
                MatrixS[] V = splitVertical(f.U2.permutationOfColumns(s.Ec), s.r);
                MatrixS Mksn = s.M.multiplyRecursive(C[0], ring).multiplyRecursive(f.D, ring).multiplyDivMulRecursive(f.M, a, ring.numberMINUS_ONE, ring);
                MatrixS Wksn = f.W.multiplyRecursive(f.D, ring).multiplyRecursive(V[0], ring).multiplyDivMulRecursive(s.W, a, ring.numberMINUS_ONE, ring);
                int[] ucols = new int[] {col - f.r - s.r};
                int[] lrows = new int[] {row - div - s.r, div - f.r};
                int[] cols = new int[] {f.r, s.r};
                S = new MatrixS[][] {
                    {f.M, zero},
                    {Mksn, s.M}
                };
                M = join(S, cols, cols);
                S = new MatrixS[][] {
                    {f.W, Wksn},
                    {zero, s.W}
                };
                W = join(S, cols, cols);
                S = new MatrixS[][] {
                    {f.L1, zero},
                    {C[0], s.L1}
                };
                L1 = join(S, cols, cols);
                S = new MatrixS[][] {
                    {f.U1, V[0]},
                    {zero, s.U1}
                };
                U1 = join(S, cols, cols);
                S = new MatrixS[][] {
                    {C[1], s.L2},
                    {f.L2, zero}
                };
                L2 = join(S, lrows, cols);
                S = new MatrixS[][] {
                    {V[1]},
                    {s.U2}
                };
                U2 = join(S, cols, ucols);
                r = f.r + s.r;
                d = new Element[r];
                System.arraycopy(f.d, 0, d, 0, f.r);
                System.arraycopy(s.d, 0, d, f.r, s.r);
                a_s = s.a_s;
                D = getD(d, a, r);
                Er = MatrixS.multPermutations(MatrixS.multPermutations(Er, f.Er, row), MatrixS.multPermutations(blocktoEnd(f.r, row - div, row), getE(s.Er, f.r), row), row);
                Ec = MatrixS.multPermutations(Ec, MatrixS.multPermutations(f.Ec, getE(s.Ec, f.r), col), col);
            }
        } else {
            U2 = f.U2;
            L1 = f.L1;
            U1 = f.U1;
            M = f.M;
            W = f.W;
            d = f.d;
            D = f.D;
            a_s = f.a_s;
            Er = MatrixS.multPermutations(Er, f.Er, row);
            Ec = f.Ec;
            MatrixS[][] S = new MatrixS[][] {
                {f.L2},
                {a1}
            };
            L2 = join(S, new int[] {div - f.r, row - div}, new int[] {f.r});
            r = f.r;
        }
    }

    public void getLduH(int row, int col) {
        int div = col / 2;
        if (div < row) {
            div = row;
        }
        MatrixS blocks[] = splitVertical(A, div);
        MatrixS a0 = blocks[0];
        MatrixS a1 = blocks[1];
        if (a0.isZero(ring)) {
            Ec = blocktoEnd(0, col - div, col);
            div = col - div;
            a0 = blocks[1];
            a1 = blocks[0];
        }
        LDUP1 f = new LDUP1(a0, a);
        f.getLdu(row, div);
        if (a1.isZero(ring)) {
            L1 = f.L1;
            L2 = f.L2;
            U1 = f.U1;
            U2 = f.U2;
            M = f.M;
            W = f.W;
            D = f.D;
            Ec = MatrixS.multPermutations(Ec, f.Ec, col);
            Er = MatrixS.multPermutations(Er, f.Er, row);
            U2.colNumb = col - f.r;
            a_s = f.a_s;
            r = f.r;
            d = f.d;
        } else {
            MatrixS[] B = splitHorizontal(a1.permutationOfRows(f.Er), f.r);
            B[0] = f.M.multiplyDivRecursive(B[0], f.a, ring);
            B[1] = B[1].subtract(f.L2.multiplyRecursive(f.D, ring).multiplyRecursive(B[0], ring), ring).multiplyDivide(f.a_s, f.a, ring);
            if (B[1].isZero(ring)) {
                L1 = f.L1;
                U1 = f.U1;
                M = f.M;
                W = f.W;
                d = f.d;
                D = f.D;
                a_s = f.a_s;
                Er = MatrixS.multPermutations(Er, f.Er, row);
                Ec = f.Ec;
                r = f.r;
                MatrixS[][] S = new MatrixS[][] {
                    {f.U2, B[0]}
                };
                U2 = join(S, new int[] {f.r}, new int[] {div - f.r, col - div});
                return;
            }
            LDUP1 s = new LDUP1(B[1], f.a_s);
            s.getLdu(row - f.r, col - div);
            Ec = MatrixS.multPermutations(MatrixS.multPermutations(Ec, f.Ec, col), MatrixS.multPermutations(blocktoEnd(f.r, col - div, col), getE(s.Ec, f.r), col), col);
            Er = MatrixS.multPermutations(Er, MatrixS.multPermutations(f.Er, getE(s.Er, f.r), row), row);
            MatrixS[] Bs = splitVertical(B[0].permutationOfColumns(s.Ec), s.r);
            MatrixS[] N0 = splitHorizontal(f.L2.permutationOfRows(s.Er), s.r);
            MatrixS Mk = s.M.multiplyDivMulRecursive(N0[0].multiplyRecursive(f.D, ring).multiplyRecursive(f.W, ring), f.a, ring.numberMINUS_ONE, ring);
            MatrixS Wk = f.W.multiplyDivMulRecursive(f.D.multiplyRecursive(Bs[0], ring).multiplyRecursive(s.W, ring), f.a, ring.numberMINUS_ONE, ring);
            int[] ucols = new int[] {col - div - s.r, div - f.r};
            int[] lrows = new int[] {row - s.r};
            int[] cols = new int[] {f.r, s.r};
            MatrixS[][] S = new MatrixS[][] {
                {f.L1, zero},
                {N0[0], s.L1}
            };
            L1 = join(S, cols, cols);
            S = new MatrixS[][] {
                {f.U1, Bs[0]},
                {zero, s.U1}
            };
            U1 = join(S, cols, cols);
            S = new MatrixS[][] {
                {f.W, Wk},
                {zero, s.W}
            };
            W = join(S, cols, cols);
            S = new MatrixS[][] {
                {f.M, zero},
                {Mk, s.M}
            };
            M = join(S, cols, cols);
            S = new MatrixS[][] {
                {N0[1], s.L2},};
            L2 = join(S, lrows, cols);
            S = new MatrixS[][] {
                {Bs[1], f.U2},
                {s.U2, zero},};
            U2 = join(S, cols, ucols);
            r = f.r + s.r;
            d = new Element[r];
            System.arraycopy(f.d, 0, d, 0, f.r);
            System.arraycopy(s.d, 0, d, f.r, s.r);
            D = getD(d, a, r);
            a_s = s.a_s;
        }
    }

    public void getLdu(int row, int col) {
        if (row == 0 || col == 0) {
            int rrr = 10;
        }
        if (A.isZero(ring)) {
            a_s = a;
            return;
        }
        if (row > col) {
            getLduV(row, col);
            return;
        }
        if (col > row) {
            getLduH(row, col);
            return;
        }
        if (row == col) {
            getLduQ(row);
        }
    }

    public static MatrixS getD(Element[] d, Element ak, int size) {
        if (size == 0) {
            return zero;
        }
        Element[][] m = new Element[size][];
        int[][] c = new int[size][];
        c[0] = new int[] {0};
        m[0] = new Element[] {new Fraction(ak, ak.multiply(d[0], ring)).cancel(ring)};
        for (int i = 1; i < c.length; i++) {
            if (i < d.length) {
                c[i] = new int[] {i};
                m[i] = new Element[] {new Fraction(ak, d[i].multiply(d[i - 1], ring)).cancel(ring)};
            } else {
                c[i] = new int[] {};
                m[i] = new Element[] {};
            }
        }
        return new MatrixS(size, size, m, c).cancel(ring);
    }

    public static MatrixS[] LDU(MatrixS S) {
        LDUP1 a = new LDUP1(S, ring.numberONE);
        int r0 = Math.max(S.size, S.colNumb);
        a.getLdu(S.size, S.colNumb);
        int[] ErT = MatrixS.transposePermutation(a.Er);
        int[] EcT = MatrixS.transposePermutation(a.Ec);
        MatrixS L, D, U;
        if (a.r != r0) {
            MatrixS E = MatrixS.scalarMatrix(S.size - a.r, ring.numberONE, ring);
            L = join(a.L1, MatrixS.zeroMatrix(), a.L2, MatrixS.scalarMatrix(S.size - a.r, ring.numberONE, ring));
            U = join(a.U1, a.U2, MatrixS.zeroMatrix(), MatrixS.scalarMatrix(S.colNumb - a.r, ring.numberONE, ring));
            D = join(a.D, MatrixS.zeroMatrix(a.U2.size), MatrixS.zeroMatrix(a.L2.size), MatrixS.zeroMatrix());
        } else {
            L = a.L1;
            D = a.D;
            U = a.U1;
        }
        return new MatrixS[] {L.permutationOfRows(ErT).permutationOfColumns(ErT),
            D.permutationOfRows(ErT).permutationOfColumns(EcT),
            U.permutationOfRows(EcT).permutationOfColumns(EcT)
        };
    }

    public static MatrixS[] splitHorizontal(MatrixS S, int len) {
        if (len == 0) {
            return new MatrixS[] {zero, S};
        }
        if (len < S.size) {
            Element[][] M1 = new Element[len][0];
            Element[][] M2 = new Element[S.size - len][0];
            int[][] col1 = new int[len][0];
            int[][] col2 = new int[S.size - len][0];
            for (int i = 0; i < M1.length; i++) {
                col1[i] = S.col[i];
                M1[i] = S.M[i];
            }
            for (int i = 0; i < M2.length; i++) {
                col2[i] = S.col[i + len];
                M2[i] = S.M[i + len];
            }
            return new MatrixS[] {new MatrixS(len, S.colNumb, M1, col1), new MatrixS(S.size - len, S.colNumb, M2, col2)};
        } else {
            return new MatrixS[] {S, zero};
        }
    }

    public static MatrixS[] splitVertical(MatrixS S, int len) {
        MatrixS[] res = new MatrixS[2];
        if (S.colNumb <= len) {
            return new MatrixS[] {S, zero};
        }
        if (len == 0) {
            return new MatrixS[] {zero, S};
        }
        int colNumb0 = Math.min(len, S.colNumb);
        int colNumb1 = Math.max(S.colNumb - len, 0);
        int[][] c0 = new int[S.size][0], c1 = new int[S.size][0];
        Element[][] r0 = new Element[S.size][0], r1 = new Element[S.size][0];
        for (int i = 0; i < S.size; i++) {
            if ((S.M[i] != null) && (S.M[i].length != 0)) {
                Element[][] R2 = new Element[2][0];
                int[][] C2 = new int[2][0];
                toHalveRow(S.M[i], R2, S.col[i], C2, len);
                c0[i] = C2[0];
                c1[i] = C2[1];
                r0[i] = R2[0];
                r1[i] = R2[1];
            }
        }
        return new MatrixS[] {
            new MatrixS(S.size, colNumb0, r0, c0),
            new MatrixS(S.size, colNumb1, r1, c1)};
    }

    int[] changeBlocks(int sizeLastBlock, int sizeFull) {
        int sizeFirstBlock = sizeFull - sizeLastBlock;
        one *= (sizeFirstBlock * sizeLastBlock % 2 == 1) ? -1 : 1;
        int[] res = new int[2 * sizeFull];
        int t = sizeFull - sizeLastBlock;
        for (int i = 0; i < sizeFull; i++) {
            res[i] = i;
            if (i < (t)) {
                res[i + sizeFull] = i + sizeLastBlock;
            } else {
                res[i + sizeFull] = i - t;
            }
        }
        return res;
    }

    public static int[] blocktoEnd(int startPos, int sizeLastBlock, int sizeFull) {
        int sizeFirstBlock = sizeFull - sizeLastBlock - startPos;
        int dif1 = sizeFull - sizeLastBlock - startPos;
        int t0 = (sizeFull - startPos);
        int[] res = new int[2 * t0];
        for (int i = 0; i < t0; i++) {
            res[i] = i + startPos;
            if (i < (sizeFirstBlock)) {
                res[i + t0] = i + startPos + sizeLastBlock;
            } else {
                res[i + t0] = i + startPos - dif1;
            }
        }
        return res;
    }

    static MatrixS join(MatrixS A, MatrixS B, MatrixS C, MatrixS D) {
        return join(new MatrixS[] {A, B, C, D});
    }

    static MatrixS joinHorizontal(MatrixS A, MatrixS B) {
        return join(new MatrixS[] {
            A, MatrixS.zeroMatrix(),
            B, MatrixS.zeroMatrix()
        });
    }

    static MatrixS joinVertical(MatrixS A, MatrixS B) {
        return join(new MatrixS[] {
            A, B,
            MatrixS.zeroMatrix(), MatrixS.zeroMatrix()
        });
    }

    int[] getE(int[] E, int k) {
        int[] t = new int[E.length];
        for (int i = 0; i < E.length; i++) {
            t[i] = E[i] + k;
        }
        return t;
    }

    public static MatrixS genM(int k, int eM, int proc) {
        MatrixS res = null;
        while (res == null) {
            Random rnd = new Random();
            int[][] tmp = new int[k][k];
            for (int i = 0; i < tmp.length; i++) {
                for (int j = 0; j < tmp.length; j++) {
                    int ttt = rnd.nextInt(100);
                    tmp[i][j] = (ttt < proc) ? 0 : (rnd.nextInt(eM) - eM / 2);
                }
            }
            res = new MatrixS(tmp, ring);
        }
        return res;
    }

    public static MatrixS[] split(MatrixS T, int len) {
        MatrixS[] res = new MatrixS[4];
        if (len == 0 || T.size == len) {
            if (len == 0) {
                res[0] = T;
                res[3] = MatrixS.zeroMatrix();
            }
            if (len == T.size) {
                res[3] = T;
                res[0] = MatrixS.zeroMatrix();
            }
            res[1] = res[2] = MatrixS.zeroMatrix();
            return res;
        }
        int len1 = Math.min(len, T.col.length); // rows for upper blocks
        int len2 = Math.max(T.col.length - len, 0); // rows for bound blocks
        int colNumb0 = Math.min(len, T.colNumb);
        int colNumb1 = Math.max(T.colNumb - len, 0);
        int[][] c0 = new int[len1][0], c1 = new int[len1][0],
                c3 = new int[len2][0],
                c2 = new int[len2][0];
        Element[][] r0 = new Element[len1][0], r1 = new Element[len1][0],
                r2 = new Element[len2][0],
                r3 = new Element[len2][0];
        for (int i = 0; i < len1; i++) {
            if ((T.M[i] != null) && (T.M[i].length != 0)) {
                Element[][] R2 = new Element[2][0];
                int[][] C2 = new int[2][0];
                toHalveRow(T.M[i], R2, T.col[i], C2, len);
                c0[i] = C2[0];
                c1[i] = C2[1];
                r0[i] = R2[0];
                r1[i] = R2[1];
            }
        }
        for (int i = 0; i < len2; i++) {
            int i_len = len + i;
            if (T.M[i_len].length != 0) {
                Element[][] R2 = new Element[2][0];
                int[][] C2 = new int[2][0];
                toHalveRow(T.M[i_len], R2, T.col[i_len], C2, len);
                c2[i] = C2[0];
                c3[i] = C2[1];
                r2[i] = R2[0];
                r3[i] = R2[1];
            }
        }
        res[0] = new MatrixS(len1, colNumb0, r0, c0);
        res[1] = new MatrixS(len1, colNumb1, r1, c1);
        res[2] = new MatrixS(len2, colNumb0, r2, c2);
        res[3] = new MatrixS(len2, colNumb1, r3, c3);
        return res;
    }

    static void toHalveRow(Element[] r, Element[][] r2, int[] c, int[][] c2, int len) {
        int m = c.length;
        int[] C0 = new int[m];
        int[] C1 = new int[m];
        Element[] R0 = new Element[m];
        Element[] R1 = new Element[m];
        int p0 = 0;
        int p1 = 0;
        for (int j = 0; j < m; j++) {
            int cj = c[j];
            if (cj < len) {
                C0[p0] = cj;
                R0[p0++] = r[j];
            } else {
                C1[p1] = (cj -= len);
                R1[p1++] = r[j];
            }
        }
        if (p0 == m) {
            c2[0] = C0;
            r2[0] = R0;
        } else if (p0 != 0) {
            int[] CC = new int[p0];
            Element[] RR = new Element[p0];
            System.arraycopy(C0, 0, CC, 0, p0);
            System.arraycopy(R0, 0, RR, 0, p0);
            c2[0] = CC;
            r2[0] = RR;
        }
        if (p1 == m) {
            c2[1] = C1;
            r2[1] = R1;
        } else if (p1 != 0) {
            int[] CC = new int[p1];
            Element[] RR = new Element[p1];
            System.arraycopy(C1, 0, CC, 0, p1);
            System.arraycopy(R1, 0, RR, 0, p1);
            c2[1] = CC;
            r2[1] = RR;
        }
    }

    public static MatrixS join(MatrixS[] b) {
        int len2 = Math.max(b[2].M.length, b[3].M.length);
        int len1 = Math.max(b[0].M.length, b[1].M.length);
        int n = (len2 == 0) ? len1 : len1 + len2;
        int col2 = Math.max(b[1].colNumb, b[3].colNumb);
        int col1 = Math.max(b[0].colNumb, b[2].colNumb);
        int colNumb = (col2 == 0) ? col1 : col1 + col2;
        Element[][] r = new Element[n][0];
        int[][] c = new int[n][0];
        Element[] R0 = null;
        Element[] R1 = null;
        int[] C0 = null;
        int[] C1 = null;
        for (int i = 0; i < len1; i++) {
            int m = 0;
            int k = 0;
            if (b[0].M.length > i) {
                C0 = b[0].col[i];
                R0 = b[0].M[i];
                m = C0.length;
            }
            if (b[1].M.length > i) {
                C1 = b[1].col[i];
                R1 = b[1].M[i];
                k = C1.length;
            }
            int mk = m + k;
            Element[] r0 = new Element[mk];
            int[] c0 = new int[mk];
            if (m > 0) {
                System.arraycopy(C0, 0, c0, 0, m);
                System.arraycopy(R0, 0, r0, 0, m);
            }
            if (k > 0) {
                System.arraycopy(C1, 0, c0, m, k);
                System.arraycopy(R1, 0, r0, m, k);
                for (int s = m; s < mk; s++) {
                    c0[s] += col1;
                }
            }
            r[i] = r0;
            c[i] = c0;
        }
        int ii = len1;
        for (int i = 0; i < len2; i++) {
            int m = 0;
            int k = 0;
            if (b[2].M.length > i) {
                C0 = b[2].col[i];
                R0 = b[2].M[i];
                m = C0.length;
            }
            if (b[3].M.length > i) {
                C1 = b[3].col[i];
                R1 = b[3].M[i];
                k = C1.length;
            }
            int mk = m + k;
            Element[] r0 = new Element[mk];
            int[] c0 = new int[mk];
            if (m > 0) {
                System.arraycopy(C0, 0, c0, 0, m);
                System.arraycopy(R0, 0, r0, 0, m);
            }
            if (k > 0) {
                System.arraycopy(C1, 0, c0, m, k);
                System.arraycopy(R1, 0, r0, m, k);
                for (int s = m; s < mk; s++) {
                    c0[s] += len1;
                }
            }
            r[ii] = r0;
            c[ii++] = c0;
        }
        MatrixS res = new MatrixS(n, colNumb, r, c);
        return res;
    }

    public static Element getDet(MatrixS T) {
        LDUP1 a = new LDUP1(T, ring.numberONE);
        Ring r = Ring.ringZxyz;
        a.getLdu(T.size, T.colNumb);
        if (a.d.length < T.size) {
            return r.numberZERO;
        }
        return a.d[a.d.length - 1];
    }

    public static MatrixS join(MatrixS[][] S, int[] rows, int[] cols) {
        int n = 0;
        for (int i : rows) {
            n += i;
        }
        Element[][] r = new Element[n][0];
        int[][] c = new int[n][0];
        int[] colss = new int[cols.length];
        for (int i = 1; i < colss.length; i++) {
            colss[i] += cols[i - 1] + colss[i - 1];
        }
        //перебираем ряды матриц
        int numrow = 0;
        for (int k = 0; k < S.length; k++) {
            //перебираем строки матрицы
            for (int j = 0; j < rows[k]; j++) {
                //перебираем матрицы
                int numbs = 0;
                int[] num = new int[cols.length];
                for (int i = 0; i < S[k].length; i++) {
                    try {
                        int tmps = S[k][i].M[j].length;
                        num[i] = tmps;
                        numbs += tmps;
                    } catch (Exception e) {
                    }
                }

                r[numrow] = new Element[numbs];
                c[numrow] = new int[numbs];

                int t = 0;
                for (int i = 0; i < num.length; i++) {
                    for (int l = 0; l < num[i]; l++) {
                        r[numrow][t] = S[k][i].M[j][l];
                        c[numrow][t] = S[k][i].col[j][l] + colss[i];
                        t++;
                    }
                }
                numrow++;
            }
        }
        MatrixS res = new MatrixS(n, colss[colss.length - 1] + cols[colss.length - 1], r, c);
        return res;


    }

    public static void main(String[] args) {
        MatrixS a = new MatrixS(new int[][] {
            {0, 1, 2, -5, 2, 1},
            {4, 0, 2, 2, -1, -1},
            {4, 4, 4, -3, -4, 4},
            {-2, 0, -5, -4, 4, 1},
            {-5, -5, -2, 1, 2, 4},
            {4, 3, 1, -2, 0, 1}
        }, ring);
        a = new MatrixS(new int[][] {
            { 2, 0, 0, 1},
            {4, 3, 7, 1},
            {1, 0, 0, 5},
            {2, 3, 0, 6},            
        }, ring);
        MatrixS[] s0 = LDU(a);
        MatrixS LD = s0[0].multiply(s0[1], ring);
        MatrixS LDU = LD.multiply(s0[2], ring);
        System.out.println(LDU.subtract(a, ring).isZero(ring));
    }
}
