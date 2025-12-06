
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
 * вариант основанный на блок-схеме
 */
public class AdjMatrixSThreadsMolodtsov {
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
            synchronized (AdjMatrixSThreadsMolodtsov.class) {
                if(service == null) {
                    service = Executors.newFixedThreadPool(4);
                }
            }
        }
    }

    public AdjMatrixSThreadsMolodtsov(MatrixS A, int[] Ei, int[] Ej, MatrixS S, Element Det) {
        this.A = A;
        this.Ei = Ei;
        this.Ej = Ej;
        this.S = S;
        this.Det = Det;
    }

    /**
     * Конструктор класса AdjMatrixS
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
    public AdjMatrixSThreadsMolodtsov(MatrixS m, Element d0, Ring ring) {
        // long t1=System.currentTimeMillis(), t2=0;
        int N = m.size;         // The number of rows in the matrix m
        if (m.isZero(ring)) {  //Array.p(" ZERO_Matr  ");
            A = MatrixS.scalarMatrix(m.size, d0, ring);
            Ei = new int[0];
            Ej = new int[0];
            S = MatrixS.zeroMatrix(N);
            Det = d0;
        } else {
            if (N == 1) {
                A = new MatrixS(1, 1, new Element[][]{{d0}}, new int[][]{{0}});
                Ei = new int[]{0};
                Ej = new int[]{0};
                S = m;
                Det = m.M[0][0];
            } else {
                initService();
                N = N >>> 1;
                MatrixS[] M = m.split();

                // step 1 //
                AdjMatrixSThreadsMolodtsov m11 = new AdjMatrixSThreadsMolodtsov(M[0], d0, ring);

                // step 2 //
                Element d11 = m11.Det;
                MatrixS y11 = m11.S.ES_min_dI(d11, m11.Ei, m11.Ej, ring);
                MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);

                // step 3 //
                AtomicReference<MatrixS> M12_1 = new AtomicReference<>();
                AtomicReference<MatrixS> M12_2 = new AtomicReference<>();
                int finalN = N;
                Future<?> step3 = service.submit(() -> {
                    M12_1.set(m11.A.multiplyDivRecursive(M[1], d0, ring));
                    M12_2.set(M12_1.get().multiplyLeftI(Array.involution(m11.Ei, finalN)));
                });

                // step 4 //
                AtomicReference<AdjMatrixSThreadsMolodtsov> m21 = new AtomicReference<>();
                AtomicReference<Element> d21 = new AtomicReference<>();
                Future<?> step4 = service.submit(() -> {
                    m21.set(new AdjMatrixSThreadsMolodtsov(M21_1, d11, ring));
                    d21.set(m21.get().Det);
                });

                // step 5 // pre 3
                AtomicReference<MatrixS> M22_1 = new AtomicReference<>();
                Future<?> step5 = service.submit(() -> {
                    while(!step3.isDone()) {}

                    M22_1.set(((M[3].multiplyByNumber(d11, ring))
                            .subtract(M[2].multiplyRecursive(M12_1.get().multiplyLeftE(m11.Ej, m11.Ei), ring), ring))
                            .divideByNumber(d0, ring));
                });

                // step 6 //
                AtomicReference<AdjMatrixSThreadsMolodtsov> m12 = new AtomicReference<>();
                AtomicReference<Element> d12 = new AtomicReference<>();
                AtomicReference<MatrixS> y12 = new AtomicReference<>();
                Future<?> step6 = service.submit(() -> {
                    while (!step3.isDone()) {}

                    m12.set(new AdjMatrixSThreadsMolodtsov(M12_2.get(), d11, ring));
                    d12.set(m12.get().Det);
                    y12.set(m12.get().S.ES_min_dI(d12.get(), m12.get().Ei, m12.get().Ej, ring));
                });

                // step 7 //
                AtomicReference<MatrixS> y21 = new AtomicReference<>();
                AtomicReference<MatrixS> M11_2 =  new AtomicReference<>();
                Future<?> step7 = service.submit(() -> {
                    while (!step4.isDone()) {}

                    y21.set(m21.get().S.ES_min_dI(d21.get(), m21.get().Ei, m21.get().Ej, ring));
                    M11_2.set(m11.S.multiplyDivRecursive(y21.get(), d11.negate(ring), ring));
                });


                // step 8 //
                AtomicReference<MatrixS> A1 = new AtomicReference<>();
                Future<?> step8 = service.submit(() -> {
                    while (!step6.isDone()) {}

                    A1.set(m12.get().A.multiplyRecursive(m11.A, ring));
                });


                // step 9 //
                AtomicReference<MatrixS> B = new AtomicReference<>();
                Future<?> step9 = service.submit(() -> {
                    while( !(step4.isDone() && step5.isDone()) ) {}

                    B.set(m21.get().A.multiplyRecursive(M22_1.get(), ring));
                });



                // step 10 //
                AtomicReference<MatrixS> Q = new AtomicReference<>();
                Future<?> step10 = service.submit(() -> {
                    while (!step4.isDone()) {}
                    Q.set(m11.S.multiplyDivRecursive(m21.get().A.multiplyLeftE(m21.get().Ej, m21.get().Ei), d11, ring));
                });


                // step 11 // pre 8
                AtomicReference<MatrixS> K2 = new AtomicReference<>();
                Future<?> step11 = service.submit(() -> {
                    while( !(step5.isDone() && step6.isDone() && step8.isDone()) ) {}

                    K2.set(M22_1.get().multiplyDivRecursive(A1.get().multiplyLeftE(m12.get().Ej, m12.get().Ei), d11, ring));
                });


                // step 12 //
                AtomicReference<Element> d11_2 = new AtomicReference<>();
                AtomicReference<MatrixS> M22_2 = new AtomicReference<>();
                AtomicReference<Element> ds = new AtomicReference<>();
                AtomicReference<MatrixS> M22_3 = new AtomicReference<>();

                Future<?> step12 = service.submit(() -> {
                    while( !(step4.isDone() && step6.isDone() && step9.isDone()) ) {}

                    d11_2.set(d11.multiply(d11, ring));
                    M22_2.set(B.get().multiplyDivRecursive(y12.get(), d11_2.get().negate(ring), ring));
                    ds.set(d12.get().multiply(d21.get(), ring).divide(d11, ring));
                    M22_3.set(M22_2.get().multiplyLeftI(Array.involution(m21.get().Ei, finalN)));
                });


                // step 13
                AtomicReference<MatrixS> Q1 = new AtomicReference<>();
                Future<?> step13 = service.submit(() -> {
                    while( !(step5.isDone() && step10.isDone()) ) {}

                    Q1.set(Q.get().multiplyRecursive(M22_1.get(), ring));
                });


                // step 14
                AtomicReference<MatrixS> G = new AtomicReference<>();
                Future<?> step14 = service.submit(() -> {
                    while( !(step6.isDone() && step11.isDone()) ) {}

                    G.set((
                            M[2].multiplyDivMulRecursive(m11.A.multiplyLeftE(m11.Ej, m11.Ei), d0, d12.get(), ring).add(K2.get(), ring)
                    ).divideByNumber(d11.negate(ring), ring));
                });


                // step 15 //
                AtomicReference<AdjMatrixSThreadsMolodtsov> m22 = new AtomicReference<>();
                AtomicReference<MatrixS> y22 = new AtomicReference<>();
                Future<?> step15 = service.submit(() -> {
                    while( !(step12.isDone()) ) {}

                    m22.set(new AdjMatrixSThreadsMolodtsov(M22_3.get(), ds.get(), ring));
                    Det = m22.get().Det;
                    y22.set(m22.get().S.ES_min_dI(Det, m22.get().Ei, m22.get().Ej, ring));
                });


                // step 16 //
                AtomicReference<MatrixS> M12_2_new = new AtomicReference<>();
                Future<?> step16 = service.submit(() -> {
                    while( !(step4.isDone() && step6.isDone() && step13.isDone()) ) {}

                    M12_2_new.set((
                            (
                                    (
                                            Q1.get().subtract((M12_1.get().multiplyLeftI(m11.Ei).multiplyByNumber(d21.get(), ring)), ring)
                                    )
                                            .divideByNumber(d11, ring).multiplyRecursive(y12.get(), ring)
                            )
                                    .add((m12.get().S).multiplyByNumber(d21.get(), ring), ring)
                    )
                            .divideByNumber(d11, ring));
                });


                // step 17 //
                AtomicReference<MatrixS> L = new AtomicReference<>();
                Future<?> step17 = service.submit(() -> {
                    while( !(step8.isDone() && step15.isDone() && step16.isDone()) ) {}

                    L.set((A1.get().subtract((M12_1.get().multiplyLeftI(m11.Ei)).
                            multiplyDivRecursive(A1.get().multiplyLeftE(m12.get().Ej, m12.get().Ei), d11, ring), ring)
                    ).divideMultiply(d11, Det, ring));
                });



                // step 18 //
                AtomicReference<MatrixS> M12_3 = new AtomicReference<>();
                Future<?> step18 = service.submit(() -> {
                    while( !(step15.isDone() && step16.isDone()) ) {}

                    M12_3.set(M12_2_new.get().multiplyDivRecursive(y22.get(), ds.get().negate(ring), ring));
                });


                // step 19 //
                AtomicReference<MatrixS> M22_3_new = new AtomicReference<>();
                Future<?> step19 = service.submit(() -> {
                    while( !(step15.isDone()) ) {}

                    M22_3_new.set(((M22_2.get().multiplyLeftI(m21.get().Ei))
                            .multiplyDivRecursive(y22.get(), ds.get().negate(ring), ring)).add(m22.get().S, ring));
                });


                // step 20 //
                AtomicReference<MatrixS> A2 = new AtomicReference<>();
                Future<?> step20 = service.submit(() -> {
                    while( !(step15.isDone()) ) {}

                    A2.set(m22.get().A.multiplyRecursive(m21.get().A, ring));
                });

                // step 21 //
                AtomicReference<MatrixS> K1 = new AtomicReference<>();
                Future<?> step21 = service.submit(() -> {
                    while( !(step16.isDone() && step20.isDone()) ) {}

                    K1.set(M12_2_new.get().multiplyDivRecursive(A2.get().multiplyLeftE(m22.get().Ej, m22.get().Ei), ds.get(), ring));
                });



                // step 22 //
                AtomicReference<MatrixS> P = new AtomicReference<>();
                Future<?> step22 = service.submit(() -> {
                    while( !(step20.isDone()) ) {}

                    P.set((A2.get().subtract((M22_2.get().multiplyLeftI(m21.get().Ei)).
                            multiplyDivRecursive(A2.get().multiplyLeftE(m22.get().Ej, m22.get().Ei), ds.get(), ring), ring)
                    ).divideByNumber(d21.get(), ring));
                });


                // step 23 //
                AtomicReference<MatrixS> F = new AtomicReference<>();
                Future<?> step23 = service.submit(() -> {
                    while( !(step21.isDone()) ) {}

                    F.set((m11.S.multiplyDivMulRecursive(m21.get().A.multiplyLeftE(m21.get().Ej, m21.get().Ei), d11, Det, ring).add(K1.get(), ring))
                            .divideByNumber(d21.get().negate(ring), ring));
                });


                //step 24//
                AtomicReference<MatrixS> P1 = new AtomicReference<>();
                Future<?> step24 = service.submit(() -> {
                    while( !(step14.isDone() && step22.isDone()) ) {}

                    P1.set(P.get().multiplyDivRecursive(G.get(), d12.get(), ring));
                });


                //step 25//
                AtomicReference<MatrixS> F1  = new AtomicReference<>();
                Future<?> step25 = service.submit(() -> {
                    while( !(step14.isDone() && step17.isDone() && step23.isDone()) ) {}

                    F1.set((L.get().add(F.get().multiplyRecursive(G.get(), ring), ring)).divideByNumber(d12.get(), ring));
                });



                // joining S // pre 18, 19
                Future<?> joiningS = service.submit(() -> {
                    while( !(step18.isDone() && step19.isDone()) ) {}

                    MatrixS M11_3 = M11_2.get().multiplyDivide(Det, d21.get(), ring);
                    MatrixS M21_3 = m21.get().S.multiplyDivide(Det, d21.get(), ring);
                    S = MatrixS.join(new MatrixS[]{M11_3, M12_3.get(), M21_3, M22_3_new.get()});
                });

                // joining Ei Ej // pre  15
                Future<?> joiningEiEj = service.submit(() -> {
                    while( !(step15.isDone()) ) {}

                    Ei = new int[m11.Ei.length + m12.get().Ei.length + m21.get().Ei.length + m22.get().Ei.length];
                    Ej = new int[Ei.length];
                    int j = 0;
                    for (int i = 0; i < m11.Ei.length; ) {
                        Ei[j] = m11.Ei[i];
                        Ej[j++] = m11.Ej[i++];
                    }
                    for (int i = 0; i < m12.get().Ei.length; ) {
                        Ei[j] = m12.get().Ei[i];
                        Ej[j++] = m12.get().Ej[i++] + finalN;
                    }
                    for (int i = 0; i < m21.get().Ei.length; ) {
                        Ei[j] = m21.get().Ei[i] + finalN;
                        Ej[j++] = m21.get().Ej[i++];
                    }
                    for (int i = 0; i < m22.get().Ei.length; ) {
                        Ei[j] = m22.get().Ei[i] + finalN;
                        Ej[j++] = m22.get().Ej[i++] + finalN;
                    }
                });


                while( !(step22.isDone() && step23.isDone() && step24.isDone() && step25.isDone()) ) {}
                // joining A // pre 22 - 25
                MatrixS[] RR = new MatrixS[4];
                RR[0] = F1.get();
                RR[1] = F.get();
                RR[2] = P1.get();
                RR[3] = P.get();
                A = MatrixS.join(RR);

                while( !(joiningS.isDone() && joiningEiEj.isDone()) ) {}
            }
        }
    }
}
