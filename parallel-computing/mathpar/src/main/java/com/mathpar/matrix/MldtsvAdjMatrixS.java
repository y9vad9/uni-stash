
package com.mathpar.matrix;

import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * /////
 *
 * @author mldtsv
 * Паралельний варіант AdjMatrixS
 */
public class MldtsvAdjMatrixS {
    /**
     * adjoint matrix
     */
    public MatrixS A;
    /**
     * список строк матрицы Е
     */
    public int[] Ei;
    /**
     * список столбцов матрицы Е, которые соответствуют
     * ее списку строк:  (E[Ei[i]][Ej[i]] = 1)
     */
    public int[] Ej;
    /**
     * echelon form for the initial matrix
     */
    public MatrixS S;
    /**
     * determinant of the initial matrix
     */
    public Element Det;

    private static ExecutorService service;

    private static void initService() {
        if(service == null) {
            synchronized (MldtsvAdjMatrixS.class) {
                if(service == null) {
                    service = Executors.newFixedThreadPool(4);
                }
            }
        }
    }


    public MldtsvAdjMatrixS(MatrixS A, int[] Ei, int[] Ej, MatrixS S, Element Det) {
        this.A = A;
        this.Ei = Ei;
        this.Ej = Ej;
        this.S = S;
        this.Det = Det;
    }

    /**
     * Конструктор класса MldtsvAdjMatrixS
     * FIRST VERSION
     *
     * @param m    --  входная матрица
     * @param d0   -- determinant of the last upper block. For first step: d0=1.
     *             A -- adjoin matrix for matrix m: Am=S -- echelon form of m.
     *             S -- echelon form for matrix m
     *             Det -- determinant
     *             Ei,Ej -- obtained E-matrix
     * @param ring Ring
     */
    public MldtsvAdjMatrixS(MatrixS m, Element d0, Ring ring) {
        // long t1=System.currentTimeMillis(), t2=0;
        int nonFinalN = m.size;         // The number of rows in the matrix m
        if (m.isZero(ring)) {  //Array.p(" ZERO_Matr  ");
            A = MatrixS.scalarMatrix(m.size, d0, ring);
            Ei = new int[0];
            Ej = new int[0];
            S = MatrixS.zeroMatrix(nonFinalN);
            Det = d0;
        } else {
            if (nonFinalN == 1) {
                A = new MatrixS(1, 1, new Element[][]{{d0}}, new int[][]{{0}});
                Ei = new int[]{0};
                Ej = new int[]{0};
                S = m;
                Det = m.M[0][0];
            } else {
                initService();
                final int N = nonFinalN >>> 1;
                MatrixS[] M = m.split();
                // prerequisites for all results
                MldtsvAdjMatrixS m11 = new MldtsvAdjMatrixS(M[0], d0, ring);              // 1 STEP //
                Element d11 = m11.Det;
                MatrixS y11 = m11.S.ES_min_dI(d11, m11.Ei, m11.Ej, ring);
                MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);
                MldtsvAdjMatrixS m21 = new MldtsvAdjMatrixS(M21_1, d11, ring);          // 2 STEP  //
                MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1], d0, ring);
                MldtsvAdjMatrixS m12 = new MldtsvAdjMatrixS(
                        M12_1.multiplyLeftI(Array.involution(m11.Ei, N))
                        , d11
                        , ring);       //  3 STEP  //
                Element d12 = m12.Det;
                MatrixS y12 = m12.S.ES_min_dI(d12, m12.Ei, m12.Ej, ring);
                MatrixS M22_1 = (
                        (M[3].multiplyByNumber(d11, ring))
                                .subtract(
                                        M[2].multiplyRecursive(M12_1.multiplyLeftE(m11.Ej, m11.Ei), ring)
                                        , ring)
                )
                        .divideByNumber(d0, ring);
                Element d11_2 = d11.multiply(d11, ring);
                MatrixS M22_2 = (m21.A.multiplyRecursive(M22_1, ring))
                        .multiplyDivRecursive(y12, d11_2.negate(ring), ring);
                Element d21 = m21.Det;
                Element ds = d12.multiply(d21, ring).divide(d11, ring);
                MldtsvAdjMatrixS m22 = new MldtsvAdjMatrixS(
                        M22_2.multiplyLeftI(Array.involution(m21.Ei, N))
                        , ds
                        , ring);  //  4-STEP //


                // filling Ei Ej (parallel)
                Future<?> fillingEiEjFull = service.submit(() -> {
                    Ei = new int[m11.Ei.length + m12.Ei.length + m21.Ei.length + m22.Ei.length];
                    Ej = new int[Ei.length];
                    Future<?> fillingEiEj_1 = service.submit(() -> {
                        int j = 0;
                        for (int i = 0; i < m11.Ei.length; ) {
                            Ei[j] = m11.Ei[i];
                            Ej[j++] = m11.Ej[i++];
                        }
                    });


                    Future<?> fillingEiEj_2 = service.submit(() -> {
                        int j = m11.Ei.length;
                        for (int i = 0; i < m12.Ei.length; ) {
                            Ei[j] = m12.Ei[i];
                            Ej[j++] = m12.Ej[i++] + N;
                        }
                    });

                    Future<?> fillingEiEj_3 = service.submit(() -> {
                        int j = m11.Ei.length + m12.Ei.length;
                        for (int i = 0; i < m21.Ei.length; ) {
                            Ei[j] = m21.Ei[i] + N;
                            Ej[j++] = m21.Ej[i++];
                        }
                    });

                    Future<?> fillingEiEj_4 = service.submit(() -> {
                        int j = m11.Ei.length + m12.Ei.length + m21.Ei.length;
                        for (int i = 0; i < m22.Ei.length; ) {
                            Ei[j] = m22.Ei[i] + N;
                            Ej[j++] = m22.Ej[i++] + N;
                        }
                    });
                    while(! (fillingEiEj_1.isDone() && fillingEiEj_2.isDone()
                        && fillingEiEj_3.isDone() && fillingEiEj_4.isDone())) {}
                });

                // prerequisites for joining A and S
                Det = m22.Det;
                MatrixS M12_2 =
                        (
                            (
                                (
                                    (
                                        m11.S.multiplyDivRecursive
                                            (
                                                m21.A.multiplyLeftE(m21.Ej, m21.Ei)
                                                , d11
                                                , ring
                                            )
                                        .multiplyRecursive(M22_1, ring)
                                    )
                                    .subtract(
                                            (M12_1.multiplyLeftI(m11.Ei).multiplyByNumber(d21, ring))
                                            , ring
                                    )
                                )
                                .divideByNumber(d11, ring).multiplyRecursive(y12, ring)
                            )
                            .add((m12.S).multiplyByNumber(d21, ring), ring)
                        )
                        .divideByNumber(d11, ring);

                // Joining A (parallel)
                Future<?> joiningA = service.submit(() -> {
                    AtomicReference<MatrixS> A1 = new AtomicReference<>();
                    AtomicReference<MatrixS> L = new AtomicReference<>();
                    AtomicReference<MatrixS> G = new AtomicReference<>();

                    Future<?> buildingA1Dependency = service.submit(() -> {
                        A1.set(m12.A.multiplyRecursive(m11.A, ring));
                        L.set((A1.get().subtract((M12_1.multiplyLeftI(m11.Ei)).
                                multiplyDivRecursive(A1.get().multiplyLeftE(m12.Ej, m12.Ei), d11, ring), ring)
                        ).divideMultiply(d11, Det, ring));
                        G.set((
                                M[2].multiplyDivMulRecursive
                                                (
                                                        m11.A.multiplyLeftE(m11.Ej, m11.Ei)
                                                        , d0
                                                        , d12
                                                        , ring
                                                )
                                        .add
                                                (
                                                        M22_1.multiplyDivRecursive(A1.get().multiplyLeftE(m12.Ej, m12.Ei), d11, ring)
                                                        , ring
                                                )
                        ).divideByNumber(d11.negate(ring), ring));
                    });

                    //////////
                    AtomicReference<MatrixS> A2 = new AtomicReference<>();
                    AtomicReference<MatrixS> P = new AtomicReference<>();
                    AtomicReference<MatrixS> F = new AtomicReference<>();
                    Future<?> buildingA2Dependency = service.submit(() -> {
                        A2.set(m22.A.multiplyRecursive(m21.A, ring));
                        P.set((A2.get().subtract((M22_2.multiplyLeftI(m21.Ei)).
                                multiplyDivRecursive(A2.get().multiplyLeftE(m22.Ej, m22.Ei), ds, ring), ring)
                        ).divideByNumber(d21, ring));
                        F.set((
                                m11.S.multiplyDivMulRecursive
                                                (
                                                        m21.A.multiplyLeftE(m21.Ej, m21.Ei)
                                                        , d11
                                                        , Det
                                                        , ring
                                                )
                                        .add
                                                (
                                                        M12_2.multiplyDivRecursive(A2.get().multiplyLeftE(m22.Ej, m22.Ei), ds, ring)
                                                        , ring
                                                )
                        ).divideByNumber(d21.negate(ring), ring));
                    });

                    while(! (buildingA1Dependency.isDone() && buildingA2Dependency.isDone())) {}
                    MatrixS[] RR = new MatrixS[4];
                    RR[0] = (L.get().add(F.get().multiplyRecursive(G.get(), ring), ring)).divideByNumber(d12, ring);
                    RR[1] = F.get();
                    RR[2] = P.get().multiplyDivRecursive(G.get(), d12, ring);
                    RR[3] = P.get();
                    A = MatrixS.join(RR);
                });

                // Calculating S
                MatrixS y21 = m21.S.ES_min_dI(d21, m21.Ei, m21.Ej, ring);
                MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring), ring);
                MatrixS y22 = m22.S.ES_min_dI(Det, m22.Ei, m22.Ej, ring);
                MatrixS M11_3 = M11_2.multiplyDivide(Det, d21, ring);
                MatrixS M12_3;
                M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring), ring);
                MatrixS M21_3 = m21.S.multiplyDivide(Det, d21, ring);
                MatrixS M22_3 = ((M22_2.multiplyLeftI(m21.Ei))
                        .multiplyDivRecursive(y22, ds.negate(ring), ring)).add(m22.S, ring);
                S = MatrixS.join(new MatrixS[]{M11_3, M12_3, M21_3, M22_3});

                // joining threads
                while(! (fillingEiEjFull.isDone() && joiningA.isDone())) {}
            }
        }
    }
}// end of class AdjMatrixS
