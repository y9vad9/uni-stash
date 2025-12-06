package com.mathpar.parallel.stat.MS;

import java.io.IOException;

import com.mathpar.polynom.*;
import java.util.Random;
import com.mathpar.students.OLD.llp2.student.Shcherbinin.Matrix_multiply;
 import mpi.MPI;
 import mpi.MPIException;
import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;

/**
 * Рекурсивный алгоритм Adj-Echelon с перестановками в верхний левый угол +
 * разложение Брюа. Январь 2012
 *
 * @author gennadi
 */
public class AELDU {

    /**
     * Adjoint matrix
     */
    public MatrixS A;
    /**
     * Echelon form of the initial matrix. It head elements placed in the
     * positions {(I,J),(I+1,J+1),..,(I+rank-1, J+rank-1)}
     */
    public MatrixS S;
    /**
     * Determinant of the head minor of rank "rank" of initial matrix
     */
    public Element Det;
    /**
     * Rank of the initial matrix.
     */
    public int rank;
    /**
     * Номер первой ненулевой строки у эшелонной матрицы и ее матрицы
     * перестановок
     */
    public int I;
    /**
     * Номер первого ненулевой столбца у эшелонной матрицы и ее матрицы
     * перестановок
     */
    public int J;
    /**
     * Перестановка строк матрицы S. {i1,.... in,j1...jn} Отсортирована по
     * возрастанию первая половина {i1,.... in}, - это строки отправки.
     * Тождественная перестановка -- это пустой массив.
     */
    public int[] Er;
    /**
     * Перестановка столбцов матрицы S. {i1,.... in,j1...jn} Тождественная
     * перестановка -- это пустой массив.
     */
    public int[] Ec;
    /**
     * LLL matrix
     */
    public MatrixS L;
    /**
     * U matrix
     */
    public MatrixS U;
    /**
     * diagonal matrix
     */
    public Element[] D;
    private static final int[] IdPerm = new int[0];

    public AELDU(MatrixS A, int[] Er, int[] Ec, MatrixS S, Element Det, int I, int J, int rank, MatrixS L, MatrixS U, Element[] D) {
        this.A = A;
        this.S = S;
        this.Er = Er;
        this.Ec = Ec;
        this.I = I;
        this.J = J;
        this.rank = rank;
        this.Det = Det;
        this.L = L;
        this.D = D;
        this.U = U;
    }

    /**
     * Main recursive constructor of AdjEchelon VERSION 26.11.2011
     *
     * @param m -- входная матрица
     * @param d0 -- determinant of the last upper block. For first step: d0=1. A
     * -- adjoin matrix for matrix m: Am=S -- echelon form of m. S -- echelon
     * form for matrix m Det -- determinant
     * @param shiftR -- the number of the firs row in matrix m. For first step:
     * =0.
     * @param shiftC -- the number of the firs row in matrix m. For first step:
     * =0.
     * @param ring Ring
     */
    public AELDU(MatrixS m, Element d0, int shiftR, int shiftC, Ring ring) {
        I = shiftR;
        J = shiftC;
        int N = m.size; //N is a number of rows in the matrix m
        if (m.isZero(ring)) {
            A = MatrixS.scalarMatrix(m.size, d0, ring);
            Er = IdPerm;
            Ec = IdPerm;
            S = MatrixS.zeroMatrix(N);
            Det = d0;
            rank = 0;
            L = MatrixS.scalarMatrix(N, ring.numberZERO, ring);
            U = MatrixS.scalarMatrix(N, ring.numberZERO, ring);
            D = new Element[0];
        } else {
            if (N == 1) {
                A = new MatrixS(1, 1, new Element[][]{{d0}}, new int[][]{{0}});
                Er = IdPerm;
                Ec = IdPerm;
                S = m;
                Det = m.M[0][0];
                rank = 1;
                L = new MatrixS(Det);
                U = new MatrixS(Det);
                D = new Element[]{Det};
            } else {
                int N2 = N;
                N = N >>> 1;
                int shiftRR = (shiftR > N) ? (shiftR - N) : 0;
                int shiftCC = (shiftC > N) ? (shiftC - N) : 0;
                MatrixS[] M = m.split();
                AELDU m11 = new AELDU(M[0], d0, shiftR, shiftC, ring);                // 1 STEP //
                M[1] = M[1].permutationOfRows(m11.Er);
                MatrixS MM_2 = (MatrixS) M[2].clone();
                M[2] = M[2].permutationOfColumns(m11.Ec);
                Element d11 = m11.Det;
                Element d11_2 = d11.multiply(d11, ring);
                MatrixS y11 = m11.S.ES_min_dI(d11, m11, ring);
                MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1], d0, ring);
                MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);
                int l_11 = m11.rank;
                int shiftR_11 = shiftR + l_11;
                int shiftC_11 = shiftC + l_11;
                AELDU m21 = new AELDU(M21_1, d11, shiftRR, shiftC_11, ring); //2 STEP  //
                AELDU m12 = new AELDU(M12_1.barImulA(m11), d11, shiftR_11, shiftCC, ring); //3 STEP  //
                int l_12 = m12.rank;
                int l_21 = m21.rank;
                Element d12 = m12.Det;
                Element d21 = m21.Det;
                M[3] = M[3].permutationOfRows(m21.Er).permutationOfColumns(m12.Ec);
                M[2] = M[2].permutationOfRows(m21.Er);




                m11.A = m11.A.permutationOfRows(m12.Er).permutationOfColumns(m12.Er);
                m11.S = m11.S.permutationOfColumns(m21.Ec);
                m11.U = m11.U.permutationOfColumns(m21.Ec);
                M12_1 = M12_1.permutationOfRows(m12.Er).permutationOfColumns(m12.Ec);
                MatrixS M22_1 = ((M[3].multiplyByNumber(d11, ring)).subtract(M[2].multiplyRecursive(M12_1.ETmulA(m11), ring), ring)).divideByNumber(d0, ring);
                MatrixS y21 = m21.S.ES_min_dI(d21, m21, ring);
                MatrixS y12A = m12.S.ES_min_dI(d12, m12, ring);
                MatrixS A21M22 = m21.A.multiplyRecursive(M22_1, ring);
                MatrixS M22_low = A21M22.barImulA(m21);   // for L  43 63
                MatrixS M22_hight = A21M22.ImulA(m21);
                MatrixS M22_2_low = M22_low.multiplyDivRecursive(y12A, d11_2.negate(ring), ring);
                MatrixS M22yA = M22_hight.multiplyDivRecursive(y12A, d11_2.negate(ring), ring);
                Element ds = d12.multiply(d21, ring).divide(d11, ring);
                AELDU m22 = new AELDU(M22_2_low, ds, shiftRR + m21.rank, shiftCC + m12.rank, ring); //  4-STEP //
                int l_22 = m22.rank;
                rank = l_11 + l_21 + l_12 + l_22;
                Det = m22.Det;
                M22_1 = M22_1.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
                M22yA = M22yA.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
                M12_1 = M12_1.permutationOfColumns(m22.Ec);
                m12.S = m12.S.permutationOfColumns(m22.Ec);
                M[2] = M[2].permutationOfRows(m22.Er);
                M[3] = M[3].permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
                M[1] = M[1].permutationOfColumns(m12.Ec).permutationOfColumns(m22.Ec);
                m21.A = m21.A.permutationOfRows(m22.Er).permutationOfColumns(m22.Er);
                MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring), ring);
                MatrixS M12_1_I = M12_1.ImulA(m11);
                MatrixS y12B = m12.S.ES_min_dI(d12, m12, ring);
                MatrixS M12_2 = ((((m11.S.multiplyDivRecursive(m21.A.ETmulA(m21), d11, ring).multiplyRecursive(M22_1, ring)).subtract((M12_1_I.multiplyByNumber(d21, ring)), ring)).divideByNumber(d11, ring).multiplyRecursive(y12B, ring)).add((m12.S).multiplyByNumber(d21, ring), ring)).divideByNumber(d11, ring);
                MatrixS y22 = m22.S.ES_min_dI(Det, m22, ring);
                MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring), ring);
                MatrixS M22_3 = (M22yA.multiplyDivRecursive(y22, ds.negate(ring), ring)).add(m22.S, ring);
                MatrixS A1 = m12.A.multiplyRecursive(m11.A, ring);
                MatrixS A2 = m22.A.multiplyRecursive(m21.A, ring);
                MatrixS A1_E12 = A1.ETmulA(m12);
                MatrixS P = (A1.subtract(M12_1_I.multiplyDivRecursive(A1_E12, d11, ring), ring)).divideMultiply(d11, Det, ring);
                MatrixS Q = (A2.subtract(M22yA.multiplyDivRecursive(A2.ETmulA(m22), ds, ring), ring)).divideByNumber(d21, ring);
                MatrixS F = (m11.S.multiplyDivMulRecursive(m21.A.ETmulA(m21) //  .multiplyLeftE(m21.Ej,m21.Ei)
                        , d11, Det, ring).add(M12_2.multiplyDivRecursive(A2.ETmulA(m22), ds, ring), ring)).divideByNumber(d21.negate(ring), ring);
                MatrixS G = (M[2].multiplyDivMulRecursive(m11.A.ETmulA(m11), d0, d12, ring).add(M22_1.multiplyDivRecursive(A1_E12, d11, ring), ring)).divideByNumber(d11.negate(ring), ring);
                MatrixS[] AA = new MatrixS[4];
                AA[0] = (P.add(F.multiplyRecursive(G, ring), ring)).divideByNumber(d12, ring);
                AA[1] = F;
                AA[2] = Q.multiplyDivRecursive(G, d12, ring);
                AA[3] = Q;
                MatrixS M11_3 = M11_2.multiplyDivide(Det, d21, ring);
                MatrixS M21_3 = m21.S.multiplyDivide(Det, d21, ring);
                A = MatrixS.join(AA);
                S = MatrixS.join(new MatrixS[]{M11_3, M12_3, M21_3, M22_3});

                int from1 = N;
                int tothe1 = shiftR_11;
                // строим матрицу перестановок строк
                if (from1 <= shiftR_11) {
                    Er = new int[0];
                } else {
                    int from2 = shiftR_11;
                    int tothe2 = shiftR_11 + l_21;
                    int from3 = Math.max(N, shiftR) + l_21;
                    int tothe3 = shiftR_11 + l_21 + l_12;
                    int from4 = shiftR_11 + l_12;
                    int tothe4 = tothe3 + l_22;
                    int l_back = (l_21 + l_22 == 0) ? 0 : N - shiftR_11 - l_12;
                    int Erlen = (shiftR_11 == N) ? l_back : l_21 + l_back;
                    if (l_21 != 0) {
                        Erlen += l_12;
                    }
                    if (from3 != tothe3) {
                        Erlen += l_22;
                    }
                    Er = new int[2 * Erlen];
                    int jj = Erlen;
                    if (Erlen != 0) {
                        int ii = 0;
                        for (int i = 0; i < l_21; i++) {
                            Er[ii++] = from1++;
                            Er[jj++] = tothe1++;
                        }
                        if (l_21 != 0) {
                            for (int i = 0; i < l_12; i++) {
                                Er[ii++] = from2++;
                                Er[jj++] = tothe2++;
                            }
                        }
                        if (from3 != tothe3) {
                            for (int i = 0; i < l_22; i++) {
                                Er[ii++] = from3++;
                                Er[jj++] = tothe3++;
                            }
                        }
                        if (l_21 + l_22 > 0) {
                            for (int i = 0; i < l_back; i++) {
                                Er[ii++] = from4++;
                                Er[jj++] = tothe4++;
                            }
                        }
                    }
                }
                // строим матрицу перестановок столбцов
                int from2 = l_21 + shiftC_11;
                int Ec_len = N - from2;
                int end12_22R = l_12 + l_22;
                if ((Ec_len <= 0) || (end12_22R == 0)) {
                    Ec = new int[0];
                } else {
                    Ec_len += end12_22R;
                    Ec = new int[2 * Ec_len];
                    int jj = Ec_len;
                    from1 = N;
                    tothe1 = from2;
                    for (int i = 0; i < end12_22R; i++) {
                        Ec[i] = from1++;
                        Ec[jj++] = tothe1++;
                    }
                    int tothe2 = from2 + end12_22R;
                    for (int i = end12_22R; i < Ec_len; i++) {
                        Ec[i] = from2++;
                        Ec[jj++] = tothe2++;
                    }
                }
                A = A.permutationOfRows(Er).permutationOfColumns(Er);
                S = S.permutationOfRows(Er).permutationOfColumns(Ec);
                Ec = MatrixS.permutationsOfOnestep(Ec, m11.Ec, m12.Ec, m21.Ec, m22.Ec, N);
                Er = MatrixS.permutationsOfOnestep(Er, m11.Er, m21.Er, m12.Er, m22.Er, N);

                int end11_21R = l_11 + l_21;
                int end11_21_12R = end11_21R + l_12;
                int end11_12R = l_11 + l_12;
                int end21_22R = l_21 + l_22;
                int Nend21_22R = N - end21_22R;
                int Nend11_12R = N - end11_12R;
                int Nend11_21R = N - end11_21R;
                // ============================= строим матрицы U , L ==================================
                MatrixS u13_14_16, LL21A, A11M1 = null, u23_24_26 = null;
                if (l_11 == 0) {
                    u13_14_16 = MatrixS.zeroMatrix(N);
                    LL21A = m21.L;
                } else {
                    MatrixS A11_i = m11.A.ImulA(m11).moveRows(shiftR, 0, N - shiftR).moveColumns(shiftR, 0, N - shiftR);
                    A11M1 = M12_1_I.moveRows(shiftR, 0, l_11);
                    u13_14_16 = m11.U.multiplyDivRecursive(A11M1, m11.Det, ring);  // 13 14 16   U
                    MatrixS M2 = MM_2.permutationOfColumns(m11.Ec).permutationOfRows(m21.Er).moveColumns(shiftC, 0, l_11);
                    LL21A = M2.multiply(A11_i, ring).multiplyDivRecursive(m11.L, d0.multiply(m11.Det, ring), ring).
                            add(m21.L.moveColumns(0, l_11, l_21), ring); //21 22, 41 42, 61 62   L
                }
                LL21A = LL21A.permutationOfRows(m22.Er);
                LL21A = LL21A.moveRows(l_21 + shiftRR, l_21, l_22);
                if (l_21 == 0) {
                    u23_24_26 = MatrixS.zeroMatrix(N);
                } else {
                    if (l_11 == 0) {
                        u23_24_26 = m21.U.multiplyDivRecursive((m21.A.ImulA(m21).moveRows(m21.I, 0, l_21).multiply(M[3], ring)), m21.Det.multiply(d0, ring), ring);
                    } else {
                        u23_24_26 = m21.U.multiplyDivRecursive((m21.A.ImulA(m21).multiply(M22_1, ring)), m21.Det.multiply(d11, ring), ring); //23 24 26  U
                    }
                }
                M22_low = (MatrixS) M22_low.clone();   //   for L :  43 44         I whant to know why I must do cloneWithoutCFormPage()?
                MatrixS S0 = (l_11 == 0) ? m21.U : (l_21 == 0) ? m11.U : m21.U.moveColumns(0, l_11, l_21).insertRowsIn(m11.U.moveColumns(l_11 + shiftC, l_11, l_21), 0, l_11, l_21); // ready
                // сдиг block12  из-за шифта
                MatrixS u33_34_36 = m12.U.permutationOfColumns(m22.Ec).multiplyDivide(m21.Det, m11.Det, ring);
                u33_34_36 = u33_34_36.moveColumns(l_12 + shiftCC, l_12, l_22); // сдиг block34  из-за шифта
                MatrixS u33_34_36_44_46 = (l_12 == 0) ? m22.U : (l_22 == 0) ? u33_34_36
                        : m22.U.moveColumns(0, l_12, l_22).insertRowsIn(u33_34_36, 0, l_12, l_22);
                MatrixS u1_2_346 = (l_11 == 0) ? u23_24_26 : (l_21 == 0) ? u13_14_16 : u23_24_26.insertRowsIn(u13_14_16, 0, l_11, l_21);
                MatrixS S1_2 = u1_2_346.insertRowsIn(MatrixS.zeroMatrix(N + N), 0, 0, end11_21R);
                U = S0.insertRowsIn(MatrixS.zeroMatrix(N + N), 0, 0, end11_21R).moveColumns(end11_21R, rank, N - end11_21R);// move 5
                MatrixS S3_4 = u33_34_36_44_46.insertRowsIn(S1_2, 0, end11_21R, end12_22R);
                S3_4 = S3_4.moveColumns(end12_22R, N + end12_22R, N - end12_22R).moveColumns(0, end11_21R, end12_22R);// move 6   then  33,44
                U = U.add(S3_4, ring);
                // ============================= строим матрицу L  ==================================
                MatrixS T1_2 = MatrixS.zeroMatrix(Nend11_21R + N), T3_4 = MatrixS.zeroMatrix(N2), T0;
                MatrixS m11_L = m11.L.permutationOfRows(m12.Er);
                T1_2 = m11_L.insertRowsIn(T1_2, shiftR_11, 0, l_12);  // 31b
                T1_2 = m11_L.insertRowsIn(T1_2, end11_12R, end12_22R, Nend11_12R);  //51b
                T0 = LL21A.insertRowsIn(m11.L, 0, l_11, l_21); //b21 22
                T1_2 = LL21A.insertRowsIn(T1_2, l_21, l_12, l_22);  // b41 42
                T1_2 = LL21A.insertRowsIn(T1_2, end21_22R, l_22 + N - l_11, Nend21_22R);  //61 62
                MatrixS m12A = m12.A.moveRows(shiftR_11, 0, N - shiftR_11).moveColumns(shiftR + l_11, 0, N - shiftR - l_11).EmulA(0, 0, l_12);
                MatrixS AL12 = m12A.multiply(m12.L, ring);
                M22_low = M22_low.moveColumns(m12.J, 0, l_12);  // 43 63;   редкий случай, когда у 12 есть сдвиг по столбцам...
                MatrixS LL21_2moved = M22_low.multiplyDivRecursive(AL12, d12.multiply(d11, ring).multiply(d11, ring), ring);
                LL21_2moved = LL21_2moved.permutationOfRows(m22.Er);
                MatrixS m22Ln = m22.L.moveColumns(0, l_12, l_22).moveRows(0, l_21, l_22); // поставили на место 44
                MatrixS L43_44_63_64 = (l_12 == 0) ? m22Ln : LL21_2moved.add(m22Ln, ring);  // 43 44, 63 64
                MatrixS m12_L = m12.L.multiplyDivide(m21.Det, m11.Det, ring);
                T3_4 = m12_L.insertRowsIn(T3_4, 0, end11_21R, l_12);  // 33
                T3_4 = m12_L.insertRowsIn(T3_4, end11_12R, rank, Nend11_12R);  // 53
                T3_4 = L43_44_63_64.insertRowsIn(T3_4, l_21, end11_21_12R, l_22);  // 43 44
                T3_4 = L43_44_63_64.insertRowsIn(T3_4, end21_22R, N + end21_22R, Nend21_22R);  // 63 64
                L = T0.insertRowsIn(MatrixS.zeroMatrix(N2), 0, 0, end11_21R);  // L11
                L = T1_2.insertRowsIn(L, 0, end11_21R, N + Nend11_21R);  // L21
                L = L.add(T3_4.moveColumns(0, end11_21R, N), ring);
                // ============================= строим матрицу D  ==================================
                D = new Element[rank];
                System.arraycopy(m11.D, 0, D, 0, l_11);
                System.arraycopy(m21.D, 0, D, l_11, l_21);
                for (int i = 0; i < m12.D.length; i++) {
                    m12.D[i] = m12.D[i].multiply(m21.Det, ring).divide(m11.Det, ring);
                }
                System.arraycopy(m12.D, 0, D, end11_21R, l_12);
                System.arraycopy(m22.D, 0, D, end11_21_12R, l_22);
            }
        }
    }

    public AELDU(MatrixS m, Element d0, int shiftR, int shiftC, int minSizez, Ring ring) throws MPIException {
        int minSize = (minSizez>4)?minSizez/2:minSizez;
        if(MPI.COMM_WORLD.getRank()==0){
//            System.out.println("minSizez="+minSizez+"minSize="+minSize);
        }
        I = shiftR;
        J = shiftC;
        int N = m.size; //N is a number of rows in the matrix m
        if (m.isZero(ring)) {
            A = MatrixS.scalarMatrix(m.size, d0, ring);
            Er = IdPerm;
            Ec = IdPerm;
            S = MatrixS.zeroMatrix(N);
            Det = d0;
            rank = 0;
            L = MatrixS.scalarMatrix(N, ring.numberZERO, ring);
            U = MatrixS.scalarMatrix(N, ring.numberZERO, ring);
            D = new Element[0];
        } else {
            if (N == 1) {
                A = new MatrixS(1, 1, new Element[][]{{d0}}, new int[][]{{0}});
                Er = IdPerm;
                Ec = IdPerm;
                S = m;
                Det = m.M[0][0];
                rank = 1;
                L = new MatrixS(Det);
                U = new MatrixS(Det);
                D = new Element[]{Det};
            } else {
                int N2 = N;
                N = N >>> 1;
                int shiftRR = (shiftR > N) ? (shiftR - N) : 0;
                int shiftCC = (shiftC > N) ? (shiftC - N) : 0;
                MatrixS[] M = m.split();
                AELDU m11 = new AELDU(M[0], d0, shiftR, shiftC, minSize, ring);                // 1 STEP //
                M[1] = M[1].permutationOfRows(m11.Er);
                MatrixS MM_2 = (MatrixS) M[2].clone();
                M[2] = M[2].permutationOfColumns(m11.Ec);
                Element d11 = m11.Det;
                Element d11_2 = d11.multiply(d11, ring);
                MatrixS y11 = m11.S.ES_min_dI(d11, m11, ring);
                MatrixS M12_1 = Matrix_multiply.multiplyDiv(m11.A, M[1], d0, minSize, ring);
//                MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1], d0, ring);
                MatrixS M21_1 = Matrix_multiply.multiplyDiv(M[2], y11, d0.negate(ring), minSize, ring);
//                MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);
                int l_11 = m11.rank;
                int shiftR_11 = shiftR + l_11;
                int shiftC_11 = shiftC + l_11;
                AELDU m21 = new AELDU(M21_1, d11, shiftRR, shiftC_11, minSize, ring); //2 STEP  //
                AELDU m12 = new AELDU(M12_1.barImulA(m11), d11, shiftR_11, shiftCC, minSize, ring); //3 STEP  //
                int l_12 = m12.rank;
                int l_21 = m21.rank;
                Element d12 = m12.Det;
                Element d21 = m21.Det;
                M[3] = M[3].permutationOfRows(m21.Er).permutationOfColumns(m12.Ec);
                M[2] = M[2].permutationOfRows(m21.Er);
                m11.A = m11.A.permutationOfRows(m12.Er).permutationOfColumns(m12.Er);
                m11.S = m11.S.permutationOfColumns(m21.Ec);
                m11.U = m11.U.permutationOfColumns(m21.Ec);
                M12_1 = M12_1.permutationOfRows(m12.Er).permutationOfColumns(m12.Ec);
                MatrixS M22_1 = ((M[3].multiplyByNumber(d11, ring)).subtract(
                        Matrix_multiply.multiply(M[2], M12_1.ETmulA(m11), minSize, ring),
                        //                        M[2].multiplyRecursive(M12_1.ETmulA(m11), ring),
                        ring)).divideByNumber(d0, ring);
                MatrixS y21 = m21.S.ES_min_dI(d21, m21, ring);
                MatrixS y12A = m12.S.ES_min_dI(d12, m12, ring);
                MatrixS A21M22 = Matrix_multiply.multiply(m21.A, M22_1, minSize, ring);
//                MatrixS A21M22 = m21.A.multiplyRecursive(M22_1, ring);
                MatrixS M22_low = A21M22.barImulA(m21);   // for L  43 63
                MatrixS M22_hight = A21M22.ImulA(m21);
                MatrixS M22_2_low = Matrix_multiply.multiplyDiv(M22_low, y12A, d11_2.negate(ring), minSize, ring);
//                MatrixS M22_2_low = M22_low.multiplyDivRecursive(y12A, d11_2.negate(ring), ring);
                MatrixS M22yA = Matrix_multiply.multiplyDiv(M22_hight, y12A, d11_2.negate(ring), minSize, ring);
//                MatrixS M22yA = M22_hight.multiplyDivRecursive(y12A, d11_2.negate(ring), ring);
                Element ds = d12.multiply(d21, ring).divide(d11, ring);
                AELDU m22 = new AELDU(M22_2_low, ds, shiftRR + m21.rank, shiftCC + m12.rank, ring); //  4-STEP //
                int l_22 = m22.rank;
                rank = l_11 + l_21 + l_12 + l_22;
                Det = m22.Det;
                M22_1 = M22_1.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
                M22yA = M22yA.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
                M12_1 = M12_1.permutationOfColumns(m22.Ec);
                m12.S = m12.S.permutationOfColumns(m22.Ec);
                M[2] = M[2].permutationOfRows(m22.Er);
                M[3] = M[3].permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
                M[1] = M[1].permutationOfColumns(m12.Ec).permutationOfColumns(m22.Ec);
                m21.A = m21.A.permutationOfRows(m22.Er).permutationOfColumns(m22.Er);
                MatrixS M11_2 = Matrix_multiply.multiplyDiv(m11.S, y21, d11.negate(ring), minSize, ring);
//                MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring), ring);
                MatrixS M12_1_I = M12_1.ImulA(m11);
                MatrixS y12B = m12.S.ES_min_dI(d12, m12, ring);
                ;
                MatrixS M12_2 =
                        //                        (m11.S.multiplyDivRecursive(m21.A.ETmulA(m21), d11, ring).multiplyRecursive(M22_1, ring). multiplyRecursive(y12B, ring)).add((m12.S).multiplyByNumber(d21, ring), ring)).divideByNumber(d11, ring).multiplyRecursive(y12B, ring));
                        Matrix_multiply.multiply(
                        (Matrix_multiply.multiply(
                        Matrix_multiply.multiplyDiv(m11.S, m21.A.ETmulA(m21), d11, minSize, ring),
                        M22_1, minSize, ring)).subtract(M12_1_I.multiplyByNumber(d21, ring), ring).divideByNumber(d11, ring),
                        y12B,
                        minSize,
                        ring).add((m12.S).multiplyByNumber(d21, ring), ring).divideByNumber(d11, ring);
                MatrixS y22 = m22.S.ES_min_dI(Det, m22, ring);
                MatrixS M12_3 = Matrix_multiply.multiplyDiv(M12_2, y22, ds.negate(ring), minSize, ring);
//                MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring), ring);
                MatrixS M22_3 = (Matrix_multiply.multiplyDiv(M22yA, y22, ds.negate(ring), minSize, ring) //                        M22yA.multiplyDivRecursive(y22, ds.negate(ring), ring)
                        ).add(m22.S, ring);
                MatrixS A1 = Matrix_multiply.multiply(m12.A, m11.A, minSize, ring);
//                MatrixS A1 = m12.A.multiplyRecursive(m11.A, ring);
                MatrixS A2 = Matrix_multiply.multiply(m22.A, m21.A, minSize, ring);
//                MatrixS A2 = m22.A.multiplyRecursive(m21.A, ring);
                MatrixS A1_E12 = A1.ETmulA(m12);




                MatrixS P = (A1.subtract(
                        Matrix_multiply.multiplyDiv(M12_1_I, A1_E12, d11, minSize, ring) //                        M12_1_I.multiplyDivRecursive(A1_E12, d11, ring)
                        , ring)).divideMultiply(d11, Det, ring);
                MatrixS Q = (A2.subtract(
                        Matrix_multiply.multiplyDiv(M22yA, A2.ETmulA(m22), ds, minSize, ring) //                        M22yA.multiplyDivRecursive(A2.ETmulA(m22), ds, ring)
                        , ring)).divideByNumber(d21, ring);
                MatrixS F = (Matrix_multiply.multiplyDiv(m11.S, m21.A.ETmulA(m21), d11, minSize, ring).multiplyByNumber(Det, ring) //                        m11.S.multiplyDivMulRecursive(m21.A.ETmulA(m21) //  .multiplyLeftE(m21.Ej,m21.Ei)
                        //                        , d11, Det, ring)
                        .add(
                        Matrix_multiply.multiplyDiv(M12_2, A2.ETmulA(m22), ds, minSize, ring) //                        M12_2.multiplyDivRecursive(A2.ETmulA(m22), ds, ring)
                        , ring)).divideByNumber(d21.negate(ring), ring);
                MatrixS G = (Matrix_multiply.multiplyDiv(M[2], m11.A.ETmulA(m11), d0, minSize, ring).multiplyByNumber(d12, ring) //                        M[2].multiplyDivMulRecursive(m11.A.ETmulA(m11), d0, d12, ring)
                        .add(
                        Matrix_multiply.multiplyDiv(M22_1, A1_E12, d11, minSize, ring) //                        M22_1.multiplyDivRecursive(A1_E12, d11, ring)
                        , ring)).divideByNumber(d11.negate(ring), ring);
                MatrixS[] AA = new MatrixS[4];
                AA[0] = (P.add(
                        Matrix_multiply.multiply(F, G, minSize, ring) //                        F.multiplyRecursive(G, ring)
                        , ring)).divideByNumber(d12, ring);
                AA[1] = F;
                AA[2] = Matrix_multiply.multiplyDiv(Q, G, d12, minSize, ring);
//                        Q.multiplyDivRecursive(G, d12, ring);
                AA[3] = Q;
                MatrixS M11_3 = M11_2.multiplyDivide(Det, d21, ring);
                MatrixS M21_3 = m21.S.multiplyDivide(Det, d21, ring);

                A = MatrixS.join(AA);
                S = MatrixS.join(new MatrixS[]{M11_3, M12_3, M21_3, M22_3});

                int from1 = N;
                int tothe1 = shiftR_11;
                // construct matrix of rows permutations

                if (from1 <= shiftR_11) {
                    Er = new int[0];
                } else {
                    int from2 = shiftR_11;
                    int tothe2 = shiftR_11 + l_21;
                    int from3 = Math.max(N, shiftR) + l_21;
                    int tothe3 = shiftR_11 + l_21 + l_12;
                    int from4 = shiftR_11 + l_12;
                    int tothe4 = tothe3 + l_22;
                    int l_back = (l_21 + l_22 == 0) ? 0 : N - shiftR_11 - l_12;
                    int Erlen = (shiftR_11 == N) ? l_back : l_21 + l_back;
                    if (l_21 != 0) {
                        Erlen += l_12;
                    }
                    if (from3 != tothe3) {
                        Erlen += l_22;
                    }
                    Er = new int[2 * Erlen];
                    int jj = Erlen;
                    if (Erlen != 0) {
                        int ii = 0;
                        for (int i = 0; i < l_21; i++) {
                            Er[ii++] = from1++;
                            Er[jj++] = tothe1++;
                        }
                        if (l_21 != 0) {
                            for (int i = 0; i < l_12; i++) {
                                Er[ii++] = from2++;
                                Er[jj++] = tothe2++;
                            }
                        }
                        if (from3 != tothe3) {
                            for (int i = 0; i < l_22; i++) {
                                Er[ii++] = from3++;
                                Er[jj++] = tothe3++;
                            }
                        }
                        if (l_21 + l_22 > 0) {
                            for (int i = 0; i < l_back; i++) {
                                Er[ii++] = from4++;
                                Er[jj++] = tothe4++;
                            }
                        }
                    }
                }
                // construct matrix of columns permutations
                int from2 = l_21 + shiftC_11;
                int Ec_len = N - from2;
                int end12_22R = l_12 + l_22;
                if ((Ec_len <= 0) || (end12_22R == 0)) {
                    Ec = new int[0];
                } else {
                    Ec_len += end12_22R;
                    Ec = new int[2 * Ec_len];
                    int jj = Ec_len;
                    from1 = N;
                    tothe1 = from2;
                    for (int i = 0; i < end12_22R; i++) {
                        Ec[i] = from1++;
                        Ec[jj++] = tothe1++;
                    }
                    int tothe2 = from2 + end12_22R;
                    for (int i = end12_22R; i < Ec_len; i++) {
                        Ec[i] = from2++;
                        Ec[jj++] = tothe2++;
                    }
                }
                A = A.permutationOfRows(Er).permutationOfColumns(Er);
                S = S.permutationOfRows(Er).permutationOfColumns(Ec);
                Ec = MatrixS.permutationsOfOnestep(Ec, m11.Ec, m12.Ec, m21.Ec, m22.Ec, N);
                Er = MatrixS.permutationsOfOnestep(Er, m11.Er, m21.Er, m12.Er, m22.Er, N);

                int end11_21R = l_11 + l_21;
                int end11_21_12R = end11_21R + l_12;
                int end11_12R = l_11 + l_12;
                int end21_22R = l_21 + l_22;
                int Nend21_22R = N - end21_22R;
                int Nend11_12R = N - end11_12R;
                int Nend11_21R = N - end11_21R;
                // ============================= construct matrixes U , L ==================================
                MatrixS u13_14_16, LL21A, A11M1 = null, u23_24_26 = null;
                if (l_11 == 0) {
                    u13_14_16 = MatrixS.zeroMatrix(N);
                    LL21A = m21.L;
                } else {
                    MatrixS A11_i = m11.A.ImulA(m11).moveRows(shiftR, 0, N - shiftR).moveColumns(shiftR, 0, N - shiftR);
                    A11M1 = M12_1_I.moveRows(shiftR, 0, l_11);
                    u13_14_16 = Matrix_multiply.multiplyDiv(m11.U, A11M1, m11.Det, minSize, ring);
//                            m11.U.multiplyDivRecursive(A11M1, m11.Det, ring);  // 13 14 16   U
                    MatrixS M2 = MM_2.permutationOfColumns(m11.Ec).permutationOfRows(m21.Er).moveColumns(shiftC, 0, l_11);
                    LL21A = Matrix_multiply.multiplyDiv(
                            Matrix_multiply.multiply(M2, A11_i, minSize, ring), m11.L, d0.multiply(m11.Det, ring), minSize, ring).
                            //                            M2.multiply(A11_i, ring).multiplyDivRecursive(m11.L, d0.multiply(m11.Det, ring), ring).
                            add(m21.L.moveColumns(0, l_11, l_21), ring); //21 22, 41 42, 61 62   L
                }
                LL21A = LL21A.permutationOfRows(m22.Er);
                LL21A = LL21A.moveRows(l_21 + shiftRR, l_21, l_22);
                if (l_21 == 0) {
                    u23_24_26 = MatrixS.zeroMatrix(N);
                } else {
                    if (l_11 == 0) {
                        u23_24_26 = Matrix_multiply.multiplyDiv(m21.U,
                                Matrix_multiply.multiply(m21.A.ImulA(m21).moveRows(m21.I, 0, l_21), M[3], minSize, ring), m21.Det.multiply(d0, ring), minSize, ring);
//                                m21.U.multiplyDivRecursive((m21.A.ImulA(m21).moveRows(m21.I, 0, l_21).multiply(M[3], ring)), m21.Det.multiply(d0, ring), ring);
                    } else {
                        MatrixS t0 =
                                u23_24_26 = Matrix_multiply.multiplyDiv(
                                m21.U,
                                Matrix_multiply.multiply(m21.A.ImulA(m21), M22_1, minSize, ring),
                                m21.Det.multiply(d11, ring), minSize, ring);
//                                m21.U.multiplyDivRecursive((m21.A.ImulA(m21).multiply(M22_1, ring)), m21.Det.multiply(d11, ring), ring); //23 24 26  U
                    }
                }
                M22_low = (MatrixS) M22_low.clone();   //   for L :  43 44         I whant to know why I must do cloneWithoutCFormPage()?
                MatrixS S0 = (l_11 == 0) ? m21.U : (l_21 == 0) ? m11.U : m21.U.moveColumns(0, l_11, l_21).insertRowsIn(m11.U.moveColumns(l_11 + shiftC, l_11, l_21), 0, l_11, l_21); // ready
                // сдиг block12  из-за шифта
                MatrixS u33_34_36 = m12.U.permutationOfColumns(m22.Ec).multiplyDivide(m21.Det, m11.Det, ring);
                u33_34_36 = u33_34_36.moveColumns(l_12 + shiftCC, l_12, l_22); // shift block34
                MatrixS u33_34_36_44_46 = (l_12 == 0) ? m22.U : (l_22 == 0) ? u33_34_36
                        : m22.U.moveColumns(0, l_12, l_22).insertRowsIn(u33_34_36, 0, l_12, l_22);
                MatrixS u1_2_346 = (l_11 == 0) ? u23_24_26 : (l_21 == 0) ? u13_14_16 : u23_24_26.insertRowsIn(u13_14_16, 0, l_11, l_21);
                MatrixS S1_2 = u1_2_346.insertRowsIn(MatrixS.zeroMatrix(N + N), 0, 0, end11_21R);
                U = S0.insertRowsIn(MatrixS.zeroMatrix(N + N), 0, 0, end11_21R).moveColumns(end11_21R, rank, N - end11_21R);// move 5
                MatrixS S3_4 = u33_34_36_44_46.insertRowsIn(S1_2, 0, end11_21R, end12_22R);
                S3_4 = S3_4.moveColumns(end12_22R, N + end12_22R, N - end12_22R).moveColumns(0, end11_21R, end12_22R);// move 6   then  33,44
                U = U.add(S3_4, ring);
                // ============================= construct matrix L  ==================================
                MatrixS T1_2 = MatrixS.zeroMatrix(Nend11_21R + N), T3_4 = MatrixS.zeroMatrix(N2), T0;
                MatrixS m11_L = m11.L.permutationOfRows(m12.Er);
                T1_2 = m11_L.insertRowsIn(T1_2, shiftR_11, 0, l_12);  // 31b
                T1_2 = m11_L.insertRowsIn(T1_2, end11_12R, end12_22R, Nend11_12R);  //51b
                T0 = LL21A.insertRowsIn(m11.L, 0, l_11, l_21); //b21 22
                T1_2 = LL21A.insertRowsIn(T1_2, l_21, l_12, l_22);  // b41 42
                T1_2 = LL21A.insertRowsIn(T1_2, end21_22R, l_22 + N - l_11, Nend21_22R);  //61 62
                MatrixS m12A = m12.A.moveRows(shiftR_11, 0, N - shiftR_11).moveColumns(shiftR + l_11, 0, N - shiftR - l_11).EmulA(0, 0, l_12);
                MatrixS AL12 = Matrix_multiply.multiply(m12A, m12.L, minSize, ring);
//                        m12A.multiply(m12.L, ring);
                M22_low = M22_low.moveColumns(m12.J, 0, l_12);  // 43 63;  case where 12 have a shift in the columns ...
                MatrixS LL21_2moved = Matrix_multiply.multiplyDiv(M22_low, AL12, d12.multiply(d11, ring).multiply(d11, ring), minSize, ring);
//                        M22_low.multiplyDivRecursive(AL12, d12.multiply(d11, ring).multiply(d11, ring), ring);
                LL21_2moved = LL21_2moved.permutationOfRows(m22.Er);
                MatrixS m22Ln = m22.L.moveColumns(0, l_12, l_22).moveRows(0, l_21, l_22); //  44
                MatrixS L43_44_63_64 = (l_12 == 0) ? m22Ln : LL21_2moved.add(m22Ln, ring);  // 43 44, 63 64
                MatrixS m12_L = m12.L.multiplyDivide(m21.Det, m11.Det, ring);
                T3_4 = m12_L.insertRowsIn(T3_4, 0, end11_21R, l_12);  // 33
                T3_4 = m12_L.insertRowsIn(T3_4, end11_12R, rank, Nend11_12R);  // 53
                T3_4 = L43_44_63_64.insertRowsIn(T3_4, l_21, end11_21_12R, l_22);  // 43 44
                T3_4 = L43_44_63_64.insertRowsIn(T3_4, end21_22R, N + end21_22R, Nend21_22R);  // 63 64
                L = T0.insertRowsIn(MatrixS.zeroMatrix(N2), 0, 0, end11_21R);  // L11
                L = T1_2.insertRowsIn(L, 0, end11_21R, N + Nend11_21R);  // L21
                L = L.add(T3_4.moveColumns(0, end11_21R, N), ring);
                // ============================= construct matrix D  ==================================
                D = new Element[rank];
                System.arraycopy(m11.D, 0, D, 0, l_11);
                System.arraycopy(m21.D, 0, D, l_11, l_21);
                for (int i = 0; i < m12.D.length; i++) {
                    m12.D[i] = m12.D[i].multiply(m21.Det, ring).divide(m11.Det, ring);
                }
                System.arraycopy(m12.D, 0, D, end11_21R, l_12);
                System.arraycopy(m22.D, 0, D, end11_21_12R, l_22);
            }
        }
    }
//

    /**
     * The array of fractions that is a diagonal in Gauss decomposition
     *
     * @param diag -- diagonal elements of a matrix
     * @param ring -- Ring
     * @return -- a diagonal in Gauss decomposition
     */
    public static Element[] GaussDiag(Element[] diag, Ring ring) {
        int k = diag.length;
        Element[] DD = new Element[k];
        for (int i = 1; i < k; i++) {
            DD[i] = diag[i - 1].multiply(diag[i], ring);
        }
        if (k > 0) {
            DD[0] = diag[0];
        }
        for (int i = 0; i < k; i++) {
            if (!(DD[i].isOne(ring))) {
                DD[i] = (DD[i].isNegative())? new Fraction(ring.numberMINUS_ONE, DD[i].negate(ring)) : new Fraction(ring.numberONE, DD[i]);
            }
        }
        return DD;
    }

    public MatrixS[] splitMultSend(MatrixS[] buf) throws MPIException,IOException,ClassNotFoundException {
        int size = MPI.COMM_WORLD.getSize();
        MatrixS[] temp = Matrix_multiply.split(buf[0], true, Matrix_multiply.getNumb(A.size, size));
        for (int i = 1; i < size; i++) {
            MPITransport.sendObjectArray(temp,i,1,i,i);
//!!!!            MPI.COMM_WORLD.Send(temp, i, 1, MPI.OBJECT, i, i);// Рассылка первой матрицы остальным процессорам
            MPITransport.sendObjectArray(buf,1,1,i,i+size);
//!!!!            MPI.COMM_WORLD.Send(buf, 1, 1, MPI.OBJECT, i, i + size);//Отправка остальным процессорам второй матрицы
        }
        temp[0] = temp[0].multiply(buf[1], Ring.ringR64xyzt);
        return temp;
    }

    ;
  public static void resvMultSend() throws MPIException,IOException,ClassNotFoundException {
        int size = MPI.COMM_WORLD.getSize();
        int myrank = MPI.COMM_WORLD.getRank();
        MatrixS[] rbuff = new MatrixS[2];
        MPITransport.recvObjectArray(rbuff,0,1,0,myrank);
//!!!!        MPI.COMM_WORLD.Recv(rbuff, 0, 1, MPI.OBJECT, 0, myrank);
        MPITransport.recvObjectArray(rbuff,1,1,0,myrank+size);
//!!!!        MPI.COMM_WORLD.Recv(rbuff, 1, 1, MPI.OBJECT, 0, myrank + size);
        MatrixS res = rbuff[0].multiply(rbuff[1], Ring.ringR64xyzt);
        MPITransport.sendObjectArray(new MatrixS[]{res}, 0, 1, 0, myrank + 2 * size);
//!!!!        MPI.COMM_WORLD.Send(new MatrixS[]{res}, 0, 1, MPI.OBJECT, 0, myrank + 2 * size);
    }

    public MatrixS resvMultRes(MatrixS[] tmp) throws MPIException,IOException,ClassNotFoundException {
        int size = MPI.COMM_WORLD.getSize();
        int myrank = MPI.COMM_WORLD.getRank();
        for (int i = 1; i < tmp.length; i++) {
            MPITransport.recvObjectArray(tmp, i, 1, 0, myrank + 2 * size);
//!!!!            MPI.COMM_WORLD.Recv(tmp, i, 1, MPI.OBJECT, 0, myrank + 2 * size);
        }
        MatrixS res = Matrix_multiply.concatMatrixS(tmp, true);
        return res;
    }

    public static void sendDone(int n, int k) throws MPIException {
        int size = MPI.COMM_WORLD.getSize();
        int tag = n * size;
        int[] a = new int[1];
        for (int i = 1; i < size; i++) {
            System.out.println("SEND MESSAGE TO STOP to " + i + " type=" + k + " tag=" + tag);
            MPI.COMM_WORLD.send(a,1,MPI.INT,i,tag);
//!!!!            MPI.COMM_WORLD.Send(a, 0, 1, MPI.INT, i, tag);
        }



    }

    public MatrixS mult(MatrixS a, MatrixS b) throws MPIException,IOException,ClassNotFoundException{
        MatrixS[] buf = new MatrixS[]{a, b};
        System.out.println("MULT" + a + " * " + b);
        MatrixS[] t = splitMultSend(buf);
        return resvMultRes(t);
    }

//public AELDU(MatrixS m, Element d0, int shiftR, int shiftC, int minSize, Ring ring) throws MPIException {
//        int myrank = MPI.COMM_WORLD.Rank();
//        int size =  MPI.COMM_WORLD.Size();
//        int N = 0;
//        int[] Nsize = new int[1];
//        if (myrank == 0) {
//            N = m.size; //N is a number of rows in the matrix m
//            Nsize[0] = N;
//        }
//        MPI.COMM_WORLD.Bcast(Nsize, 0, Nsize.length, MPI.INT, 0);
//        if (myrank != 0) {
//            N = Nsize[0]; //N is a number of rows in the matrix m
//        }
//        if (myrank == 0) {
//            System.out.println("N==" + N);
////            I = shiftR;
////            J = shiftC;
//
//            if (m.isZero(ring)) {
////                A = MatrixS.scalarMatrix(m.size, d0, ring);
////                Er = IdPerm;
////                Ec = IdPerm;
////                S = MatrixS.zeroMatrix(N);
////                Det = d0;
////                rank = 0;
////                L = MatrixS.scalarMatrix(N, ring.numberZERO, ring);
////                U = MatrixS.scalarMatrix(N, ring.numberZERO, ring);
////                D = new Element[0];
//                sendDone(0,type);
//            } else {
//                if (N == 1) {
////                    A = new MatrixS(1, 1, new Element[][]{{d0}}, new int[][]{{0}});
////                    Er = IdPerm;
////                    Ec = IdPerm;
////                    S = m;
////                    Det = m.M[0][0];
////                    rank = 1;
////                    L = new MatrixS(Det);
////                    U = new MatrixS(Det);
////                    D = new Element[]{Det};
//                    sendDone(N,type);
//                } else {
//                    int N2 = N;
//                    N = N >>> 1;
////                    int shiftRR = (shiftR > N) ? (shiftR - N) : 0;
////                    int shiftCC = (shiftC > N) ? (shiftC - N) : 0;
//                    MatrixS[] M = m.split();
//                    AELDU m11 = new AELDU(M[0], d0, shiftR, shiftC, 1, ring);                // 1 STEP //
////
////                    M[1] = M[1].permutationOfRows(m11.Er);
////                    MatrixS MM_2 = (MatrixS) M[2].cloneWithoutCFormPage();
////                    M[2] = M[2].permutationOfColumns(m11.Ec);
////                    Element d11 = m11.Det;
////                    Element d11_2 = d11.multiply(d11, ring);
////                    MatrixS y11 = m11.S.ES_min_dI(d11, m11, ring);
////
////
////                    if (N >= minSize) {
////                        System.out.println("OLOLOL");
////                        MatrixS M12_1_ = mult(m11.A, M[1]);
////                    }
////
////                    MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1], d0, ring);
////                    MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);
////
////
////
////
////                    int l_11 = m11.rank;
////                    int shiftR_11 = shiftR + l_11;
////                    int shiftC_11 = shiftC + l_11;
//                    AELDU m21 = new AELDU(M[1], d0, shiftR, shiftC, 2, ring); //2 STEP  //
//                    AELDU m12 = new AELDU(M[2], d0, shiftR, shiftC, 3, ring); //3 STEP  //
////                    AELDU m21 = new AELDU(M21_1, d11, shiftRR, shiftC_11, 0, ring); //2 STEP  //
////                    AELDU m12 = new AELDU(M12_1.barImulA(m11), d11, shiftR_11, shiftCC, 0, ring); //3 STEP  //
////                    int l_12 = m12.rank;
////                    int l_21 = m21.rank;
////                    Element d12 = m12.Det;
////                    Element d21 = m21.Det;
////                    M[3] = M[3].permutationOfRows(m21.Er).permutationOfColumns(m12.Ec);
////                    M[2] = M[2].permutationOfRows(m21.Er);
////
////
////
////
////                    m11.A = m11.A.permutationOfRows(m12.Er).permutationOfColumns(m12.Er);
////                    m11.S = m11.S.permutationOfColumns(m21.Ec);
////                    m11.U = m11.U.permutationOfColumns(m21.Ec);
////                    M12_1 = M12_1.permutationOfRows(m12.Er).permutationOfColumns(m12.Ec);
////                    MatrixS M22_1 = ((M[3].multiplyByNumber(d11, ring)).subtract(M[2].multiplyRecursive(M12_1.ETmulA(m11), ring), ring)).divideByNumber(d0, ring);
////                    MatrixS y21 = m21.S.ES_min_dI(d21, m21, ring);
////                    MatrixS y12A = m12.S.ES_min_dI(d12, m12, ring);
////                    MatrixS A21M22 = m21.A.multiplyRecursive(M22_1, ring);
////                    MatrixS M22_low = A21M22.barImulA(m21);   // for L  43 63
////                    MatrixS M22_hight = A21M22.ImulA(m21);
////                    MatrixS M22_2_low = M22_low.multiplyDivRecursive(y12A, d11_2.negate(ring), ring);
////                    MatrixS M22yA = M22_hight.multiplyDivRecursive(y12A, d11_2.negate(ring), ring);
////                    Element ds = d12.multiply(d21, ring).divide(d11, ring);
//                    AELDU m22 = new AELDU(M[3], d0, shiftR, shiftC, 4, ring);
////                    AELDU m22 = new AELDU(M22_2_low, ds, shiftRR + m21.rank, shiftCC + m12.rank, 0, ring); //  4-STEP //
////                    int l_22 = m22.rank;
////                    rank = l_11 + l_21 + l_12 + l_22;
////                    Det = m22.Det;
////                    M22_1 = M22_1.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
////                    M22yA = M22yA.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
////                    M12_1 = M12_1.permutationOfColumns(m22.Ec);
////                    m12.S = m12.S.permutationOfColumns(m22.Ec);
////                    M[2] = M[2].permutationOfRows(m22.Er);
////                    M[3] = M[3].permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
////                    M[1] = M[1].permutationOfColumns(m12.Ec).permutationOfColumns(m22.Ec);
////                    m21.A = m21.A.permutationOfRows(m22.Er).permutationOfColumns(m22.Er);
////                    MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring), ring);
////                    MatrixS M12_1_I = M12_1.ImulA(m11);
////                    MatrixS y12B = m12.S.ES_min_dI(d12, m12, ring);
////                    MatrixS M12_2 = ((((m11.S.multiplyDivRecursive(m21.A.ETmulA(m21), d11, ring).multiplyRecursive(M22_1, ring)).subtract((M12_1_I.multiplyByNumber(d21, ring)), ring)).divideByNumber(d11, ring).multiplyRecursive(y12B, ring)).add((m12.S).multiplyByNumber(d21, ring), ring)).divideByNumber(d11, ring);
////                    MatrixS y22 = m22.S.ES_min_dI(Det, m22, ring);
////                    MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring), ring);
////                    MatrixS M22_3 = (M22yA.multiplyDivRecursive(y22, ds.negate(ring), ring)).add(m22.S, ring);
////                    MatrixS A1 = m12.A.multiplyRecursive(m11.A, ring);
////                    MatrixS A2 = m22.A.multiplyRecursive(m21.A, ring);
////                    MatrixS A1_E12 = A1.ETmulA(m12);
////                    MatrixS P = (A1.subtract(M12_1_I.multiplyDivRecursive(A1_E12, d11, ring), ring)).divideMultiply(d11, Det, ring);
////                    MatrixS Q = (A2.subtract(M22yA.multiplyDivRecursive(A2.ETmulA(m22), ds, ring), ring)).divideByNumber(d21, ring);
////                    MatrixS F = (m11.S.multiplyDivMulRecursive(m21.A.ETmulA(m21) //  .multiplyLeftE(m21.Ej,m21.Ei)
////                            , d11, Det, ring).add(M12_2.multiplyDivRecursive(A2.ETmulA(m22), ds, ring), ring)).divideByNumber(d21.negate(ring), ring);
////                    MatrixS G = (M[2].multiplyDivMulRecursive(m11.A.ETmulA(m11), d0, d12, ring).add(M22_1.multiplyDivRecursive(A1_E12, d11, ring), ring)).divideByNumber(d11.negate(ring), ring);
////                    MatrixS[] AA = new MatrixS[4];
////                    AA[0] = (P.add(F.multiplyRecursive(G, ring), ring)).divideByNumber(d12, ring);
////                    AA[1] = F;
////                    AA[2] = Q.multiplyDivRecursive(G, d12, ring);
////                    AA[3] = Q;
////                    MatrixS M11_3 = M11_2.multiplyDivide(Det, d21, ring);
////                    MatrixS M21_3 = m21.S.multiplyDivide(Det, d21, ring);
////                    A = MatrixS.join(AA);
////                    S = MatrixS.join(new MatrixS[]{M11_3, M12_3, M21_3, M22_3});
////
////                    int from1 = N;
////                    int tothe1 = shiftR_11;
////                    // строим матрицу перестановок строк
////                    if (from1 <= shiftR_11) {
////                        Er = new int[0];
////                    } else {
////                        int from2 = shiftR_11;
////                        int tothe2 = shiftR_11 + l_21;
////                        int from3 = Math.max(N, shiftR) + l_21;
////                        int tothe3 = shiftR_11 + l_21 + l_12;
////                        int from4 = shiftR_11 + l_12;
////                        int tothe4 = tothe3 + l_22;
////                        int l_back = (l_21 + l_22 == 0) ? 0 : N - shiftR_11 - l_12;
////                        int Erlen = (shiftR_11 == N) ? l_back : l_21 + l_back;
////                        if (l_21 != 0) {
////                            Erlen += l_12;
////                        }
////                        if (from3 != tothe3) {
////                            Erlen += l_22;
////                        }
////                        Er = new int[2 * Erlen];
////                        int jj = Erlen;
////                        if (Erlen != 0) {
////                            int ii = 0;
////                            for (int i = 0; i < l_21; i++) {
////                                Er[ii++] = from1++;
////                                Er[jj++] = tothe1++;
////                            }
////                            if (l_21 != 0) {
////                                for (int i = 0; i < l_12; i++) {
////                                    Er[ii++] = from2++;
////                                    Er[jj++] = tothe2++;
////                                }
////                            }
////                            if (from3 != tothe3) {
////                                for (int i = 0; i < l_22; i++) {
////                                    Er[ii++] = from3++;
////                                    Er[jj++] = tothe3++;
////                                }
////                            }
////                            if (l_21 + l_22 > 0) {
////                                for (int i = 0; i < l_back; i++) {
////                                    Er[ii++] = from4++;
////                                    Er[jj++] = tothe4++;
////                                }
////                            }
////                        }
////                    }
////                    // строим матрицу перестановок столбцов
////                    int from2 = l_21 + shiftC_11;
////                    int Ec_len = N - from2;
////                    int end12_22R = l_12 + l_22;
////                    if ((Ec_len <= 0) || (end12_22R == 0)) {
////                        Ec = new int[0];
////                    } else {
////                        Ec_len += end12_22R;
////                        Ec = new int[2 * Ec_len];
////                        int jj = Ec_len;
////                        from1 = N;
////                        tothe1 = from2;
////                        for (int i = 0; i < end12_22R; i++) {
////                            Ec[i] = from1++;
////                            Ec[jj++] = tothe1++;
////                        }
////                        int tothe2 = from2 + end12_22R;
////                        for (int i = end12_22R; i < Ec_len; i++) {
////                            Ec[i] = from2++;
////                            Ec[jj++] = tothe2++;
////                        }
////                    }
////                    A = A.permutationOfRows(Er).permutationOfColumns(Er);
////                    S = S.permutationOfRows(Er).permutationOfColumns(Ec);
////                    Ec = MatrixS.permutationsOfOnestep(Ec, m11.Ec, m12.Ec, m21.Ec, m22.Ec, N);
////                    Er = MatrixS.permutationsOfOnestep(Er, m11.Er, m21.Er, m12.Er, m22.Er, N);
////
////                    int end11_21R = l_11 + l_21;
////                    int end11_21_12R = end11_21R + l_12;
////                    int end11_12R = l_11 + l_12;
////                    int end21_22R = l_21 + l_22;
////                    int Nend21_22R = N - end21_22R;
////                    int Nend11_12R = N - end11_12R;
////                    int Nend11_21R = N - end11_21R;
////                    // ============================= строим матрицы U , L ==================================
////                    MatrixS u13_14_16, LL21A, A11M1 = null, u23_24_26 = null;
////                    if (l_11 == 0) {
////                        u13_14_16 = MatrixS.zeroMatrix(N);
////                        LL21A = m21.L;
////                    } else {
////                        MatrixS A11_i = m11.A.ImulA(m11).moveRows(shiftR, 0, N - shiftR).moveColumns(shiftR, 0, N - shiftR);
////                        A11M1 = M12_1_I.moveRows(shiftR, 0, l_11);
////                        u13_14_16 = m11.U.multiplyDivRecursive(A11M1, m11.Det, ring);  // 13 14 16   U
////                        MatrixS M2 = MM_2.permutationOfColumns(m11.Ec).permutationOfRows(m21.Er).moveColumns(shiftC, 0, l_11);
////                        LL21A = M2.multiply(A11_i, ring).multiplyDivRecursive(m11.L, d0.multiply(m11.Det, ring), ring).
////                                add(m21.L.moveColumns(0, l_11, l_21), ring); //21 22, 41 42, 61 62   L
////                    }
////                    LL21A = LL21A.permutationOfRows(m22.Er);
////                    LL21A = LL21A.moveRows(l_21 + shiftRR, l_21, l_22);
////                    if (l_21 == 0) {
////                        u23_24_26 = MatrixS.zeroMatrix(N);
////                    } else {
////                        if (l_11 == 0) {
////                            u23_24_26 = m21.U.multiplyDivRecursive((m21.A.ImulA(m21).moveRows(m21.I, 0, l_21).multiply(M[3], ring)), m21.Det.multiply(d0, ring), ring);
////                        } else {
////                            u23_24_26 = m21.U.multiplyDivRecursive((m21.A.ImulA(m21).multiply(M22_1, ring)), m21.Det.multiply(d11, ring), ring); //23 24 26  U
////                        }
////                    }
////                    M22_low = (MatrixS) M22_low.cloneWithoutCFormPage();   //   for L :  43 44         I whant to know why I must do cloneWithoutCFormPage()?
////                    MatrixS S0 = (l_11 == 0) ? m21.U : (l_21 == 0) ? m11.U : m21.U.moveColumns(0, l_11, l_21).insertRowsIn(m11.U.moveColumns(l_11 + shiftC, l_11, l_21), 0, l_11, l_21); // ready
////                    // сдиг block12  из-за шифта
////                    MatrixS u33_34_36 = m12.U.permutationOfColumns(m22.Ec).multiplyDivide(m21.Det, m11.Det, ring);
////                    u33_34_36 = u33_34_36.moveColumns(l_12 + shiftCC, l_12, l_22); // сдиг block34  из-за шифта
////                    MatrixS u33_34_36_44_46 = (l_12 == 0) ? m22.U : (l_22 == 0) ? u33_34_36
////                            : m22.U.moveColumns(0, l_12, l_22).insertRowsIn(u33_34_36, 0, l_12, l_22);
////                    MatrixS u1_2_346 = (l_11 == 0) ? u23_24_26 : (l_21 == 0) ? u13_14_16 : u23_24_26.insertRowsIn(u13_14_16, 0, l_11, l_21);
////                    MatrixS S1_2 = u1_2_346.insertRowsIn(MatrixS.zeroMatrix(N + N), 0, 0, end11_21R);
////                    U = S0.insertRowsIn(MatrixS.zeroMatrix(N + N), 0, 0, end11_21R).moveColumns(end11_21R, rank, N - end11_21R);// move 5
////                    MatrixS S3_4 = u33_34_36_44_46.insertRowsIn(S1_2, 0, end11_21R, end12_22R);
////                    S3_4 = S3_4.moveColumns(end12_22R, N + end12_22R, N - end12_22R).moveColumns(0, end11_21R, end12_22R);// move 6   then  33,44
////                    U = U.add(S3_4, ring);
////                    // ============================= строим матрицу L  ==================================
////                    MatrixS T1_2 = MatrixS.zeroMatrix(Nend11_21R + N), T3_4 = MatrixS.zeroMatrix(N2), T0;
////                    MatrixS m11_L = m11.L.permutationOfRows(m12.Er);
////                    T1_2 = m11_L.insertRowsIn(T1_2, shiftR_11, 0, l_12);  // 31b
////                    T1_2 = m11_L.insertRowsIn(T1_2, end11_12R, end12_22R, Nend11_12R);  //51b
////                    T0 = LL21A.insertRowsIn(m11.L, 0, l_11, l_21); //b21 22
////                    T1_2 = LL21A.insertRowsIn(T1_2, l_21, l_12, l_22);  // b41 42
////                    T1_2 = LL21A.insertRowsIn(T1_2, end21_22R, l_22 + N - l_11, Nend21_22R);  //61 62
////                    MatrixS m12A = m12.A.moveRows(shiftR_11, 0, N - shiftR_11).moveColumns(shiftR + l_11, 0, N - shiftR - l_11).EmulA(0, 0, l_12);
////                    MatrixS AL12 = m12A.multiply(m12.L, ring);
////                    M22_low = M22_low.moveColumns(m12.J, 0, l_12);  // 43 63;   редкий случай, когда у 12 есть сдвиг по столбцам...
////                    MatrixS LL21_2moved = M22_low.multiplyDivRecursive(AL12, d12.multiply(d11, ring).multiply(d11, ring), ring);
////                    LL21_2moved = LL21_2moved.permutationOfRows(m22.Er);
////                    MatrixS m22Ln = m22.L.moveColumns(0, l_12, l_22).moveRows(0, l_21, l_22); // поставили на место 44
////                    MatrixS L43_44_63_64 = (l_12 == 0) ? m22Ln : LL21_2moved.add(m22Ln, ring);  // 43 44, 63 64
////                    MatrixS m12_L = m12.L.multiplyDivide(m21.Det, m11.Det, ring);
////                    T3_4 = m12_L.insertRowsIn(T3_4, 0, end11_21R, l_12);  // 33
////                    T3_4 = m12_L.insertRowsIn(T3_4, end11_12R, rank, Nend11_12R);  // 53
////                    T3_4 = L43_44_63_64.insertRowsIn(T3_4, l_21, end11_21_12R, l_22);  // 43 44
////                    T3_4 = L43_44_63_64.insertRowsIn(T3_4, end21_22R, N + end21_22R, Nend21_22R);  // 63 64
////                    L = T0.insertRowsIn(MatrixS.zeroMatrix(N2), 0, 0, end11_21R);  // L11
////                    L = T1_2.insertRowsIn(L, 0, end11_21R, N + Nend11_21R);  // L21
////                    L = L.add(T3_4.moveColumns(0, end11_21R, N), ring);
////                    // ============================= строим матрицу D  ==================================
////                    D = new Element[rank];
////                    System.arraycopy(m11.D, 0, D, 0, l_11);
////                    System.arraycopy(m21.D, 0, D, l_11, l_21);
////                    for (int i = 0; i < m12.D.length; i++) {
////                        m12.D[i] = m12.D[i].multiply(m21.Det, ring).divide(m11.Det, ring);
////                    }
////                    System.arraycopy(m12.D, 0, D, end11_21R, l_12);
////                    System.arraycopy(m22.D, 0, D, end11_21_12R, l_22);
//                    sendDone(N,type);
//                }
//
//            }
//
//        } else {
//            int N2 = N;
//            if(N!=1){
//                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA "+type);
//                N = N >>> 1;
//            } else {
//
//            }
//            int size = MPI.COMM_WORLD.Size();
//            int tag = N * size;
//            System.out.println("PROC " + myrank + " " + N);
//            Status st = MPI.COMM_WORLD.Probe(0, MPI.ANY_TAG);
//
//            if (N >= minSize) {
//                boolean flag = ((st.tag == 0) || (st.tag == tag)) ? false : true;
//                while (flag) {
//                    System.out.println("RECV MESS 111");
//                    resvMultSend();
//                    st = MPI.COMM_WORLD.Probe(0, MPI.ANY_TAG);
//                    flag = (st.tag == 0) ? false : true;
//                }
//            }
//            int[] a = new int[1];
//            System.out.println("RECV MESSAGE TO STOP type ="+type+" tag=" + st.tag);
//            MPI.COMM_WORLD.Recv(a, 0, 1, MPI.OBJECT, 0, st.tag);
//        }
//    }
    public static void main(String[] args) {// throws CloneNotSupportedException {
        System.out.println("hhhhhhhhhhh");
        Ring ring = new Ring("Z[x,y]");
        System.out.println("hhhhhhhhhhh");
        long t11 = 0, t22 = 0, n11 = 0, n22 = 0;
        Polynom px = new Polynom(new int[]{1, 100, 3}, new Random(), ring);
        int N = 4;
        MatrixS mx = new MatrixS(N, N, 100, new int[]{1, 100, 3}, new Random(), ring.polynomONE, ring);
        //   System.out.println("mx="+mx);
//        long[][] m1 = new long[][] { { -1,-2,0,0,0,0,0,0}, {2,-1,0, 0,0,0,0,0},{ 1,2,0,0,0,0,0,0}, { -1,-2,0,0,0,0,0,0},
//                                     { -1,2,0,0,0,0,0,0}, {2,1,0, 0,0,0,0,0}, { 0,2,0,0,0,0,0,0}, { 1,2,0,0,0,0,0,0} };
//     long[][] m2 = new long[][] {{1, 2, -4, 1, 2,1,1, 3}, {2,1,0, 3,-2,1,2, -1},{0,0,0, 0,0,0,0,0}, {0,0,0, 0,0,0,0,0},
//                                  {0,0,0, 0, 0,0,0,0},  {0,0,0, 0, 0,0,0,0}, {0,0,0, 0, 0,0,0,0}, {0,0,0, 0, 0,0,0,0} };
        long[][] m2 = new long[][]{{0, -4, 0}, {4, 5, 1}, {1, 1, 1}};

        xxx:
        for (int ttt = 0; ttt < 1; ttt++) {
            MatrixS A = new MatrixS(
           new int[][]{ {14, 15, 7, 1, 3},
                         {1, 5, 8, 1, 4},
                         {12, 4, 7, 8, 1},
                         {55, 32, 14, 3, 23}}
                    , new Ring("Z[x]"));
//     MatrixS A = new MatrixS(N, N, 100, new int[]{5}, new java.util.Random(), NumberZ.ONE, new Ring("Z[x]"));
            t11 = System.currentTimeMillis();                          //   System.out.println("NEW MATRIX===" + A);
            System.out.println("A=" + A);

            MatrixS[] lx = A.LDU(ring);
            MatrixS[] xx= A.BruhatDecomposition(  ring);
            t22 = System.currentTimeMillis();
      MatrixS L_ = xx[0];
      MatrixS U_ = xx[2];
      MatrixS D_ = xx[1];
            MatrixS L = lx[0];
            MatrixS U = lx[2];
            MatrixS D = lx[1];
       MatrixS bruhat = L_.multiply(D_.multiply(U_, ring), ring);
                 bruhat=bruhat.cancel(ring);
            MatrixS ldu = L.multiply(D.multiply(U, ring), ring);
            ldu = ldu.cancel(ring);
            System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{ldu, L, D, U}, "=", ring));
   System.out.println("_______________________________________________________________________________");
       System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{bruhat,  L_, D_, U_}, "=", ring));
                System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{A, A.subtract(ldu, ring)}, " ", ring));
       if (A.subtract(bruhat, ring).isZero(ring)) System.out.println("GOOD in Bruhat");
       if (A.subtract(ldu, ring).isZero(ring)) {System.out.println("GOOD in LdU ! n=" + ttt+"  time="+(t22-t11));}
      else {System.out.println("My  error in LdU !!!! n=" + ttt+A+A.subtract(ldu, ring));  break xxx;  }
      MatrixS Adj=L.adjoint(ring);
            System.out.println("Adj="+Adj);
      MatrixS invD=D.inverseInFractions(ring);
      System.out.println("invD="+invD);


      MatrixS[] ldu1 = A.LDU(ring);  MatrixS UU=ldu1[2];
      System.out.println("UU="+UU);
   }
 }
}
