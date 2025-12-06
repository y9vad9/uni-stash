/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.algorithms.tropic;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;
import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.parallel.webCluster.algorithms.tropic.multiplyMatrix.FactoryMultiplyMatrix;
import com.mathpar.parallel.webCluster.algorithms.tropic.multiplyMatrix.TaskMultiplyMatrix;
import com.mathpar.parallel.utils.MPITransport;
import mpi.MPI;
import mpi.Intracomm;
/**
 *
 * @author serega
 */
public class tropParallel {
    /**
     * Вычисление замыкания матрицы блочно-рекурсивным методом
     *
     * @param A    квадратная матрица
     * @param ring полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return замыкание матрицы
     * @throws java.lang.Exception
     */
    public static MatrixD closureBlokPar(MatrixD A, Ring ring, String args[], Intracomm com) throws Exception {
        int myrank = com.getRank();
        int n = com.getSize();
        int len;
        if (myrank == 0) {
            len = A.M.length;
            for (int i = 1; i < n; i++) {
                MPITransport.sendObject(len, i, 32);
            }
        } else {
            len = (int) MPITransport.recvObject(0, 32);
        }
        if (len <= 256) {
            MatrixD aa = null;
            if (myrank == 0) {
                aa = closureBlok(A, ring);
            }
            return aa;
        } else {
            int am = len / 2;
            int bm = len - am;
            MatrixD a = new MatrixD(new Element[am][am]);
            MatrixD b = new MatrixD(new Element[am][bm]);
            MatrixD c = new MatrixD(new Element[bm][am]);
            MatrixD d = new MatrixD(new Element[bm][bm]);
            if (myrank == 0) {
                for (int i = 0; i < am; i++) {
                    for (int j = 0; j < am; j++) {
                        a.M[i][j] = A.M[i][j];
                    }
                    for (int k = am; k < len; k++) {
                        b.M[i][k - am] = A.M[i][k];
                    }
                }
                for (int l = am; l < len; l++) {
                    for (int j = 0; j < am; j++) {
                        c.M[l - am][j] = A.M[l][j];
                    }
                    for (int k = am; k < len; k++) {
                        d.M[l - am][k - am] = A.M[l][k];
                    }
                }
            }
            MatrixD az = closureBlokPar(a, ring, args, com);
            MatrixS azS = null;
            MatrixS bS = null;
            MatrixS cS = null;
            if (myrank == 0) {
                azS = new MatrixS(az, ring);
                bS = new MatrixS(b, ring);
                cS = new MatrixS(c, ring);
            }
            com.barrier();
            MatrixS e = multTropPar(args, cS, azS, ring, com);
            MatrixS e1 = multTropPar(args, e, bS, ring, com);
            MatrixD D = null;
            if (myrank == 0) {
                D = d.add(new MatrixD(e1, ring), ring);
            }
            MatrixD Dz = closureBlokPar(D, ring, args, com);
            MatrixS DzS = null;
            if (myrank == 0) {
                DzS = new MatrixS(Dz, ring);
            }
            com.barrier();
            MatrixS r3 = multTropPar(args, DzS, e, ring, com);
            MatrixS f = multTropPar(args, azS, bS, ring, com);
            MatrixS r2 = multTropPar(args, f, DzS, ring, com);
            MatrixS fr3 = multTropPar(args, f, r3, ring, com);
            MatrixD r = new MatrixD(new Element[len][len]);
            if (myrank == 0) {
                MatrixD r1 = az.add(new MatrixD(fr3, ring), ring);
                for (int i = 0; i < am; i++) {
                    for (int j = 0; j < am; j++) {
                        r.M[i][j] = r1.M[i][j];
                    }
                    for (int k = am; k < len; k++) {
                        r.M[i][k] = r2.M[i][k - am];
                    }
                }
                for (int l = am; l < len; l++) {
                    for (int j = 0; j < am; j++) {
                        r.M[l][j] = r3.M[l - am][j];
                    }
                    for (int k = am; k < len; k++) {
                        r.M[l][k] = Dz.M[l - am][k - am];
                    }
                }
            }
            return r;//результат на нулевом процессоре
        }
    }
    
    /**
     * Последовательное вычисление замыкания матрицы блочно-рекурсивным методом
     *
     * @param A    квадратная матрица
     * @param ring полукольцо
     * @return замыкание матрицы
     */
    public static MatrixD closureBlok(MatrixD A, Ring ring) {
        if(A.M.length == 1){
            A.M[0][0] = A.M[0][0].closure(ring);
            return A;
        }else{
            int am = A.M.length/2;
            int bm = A.M.length - am;
            MatrixD a = new MatrixD(new Element[am][am]);
            MatrixD b = new MatrixD(new Element[am][bm]);
            MatrixD c = new MatrixD(new Element[bm][am]);
            MatrixD d = new MatrixD(new Element[bm][bm]);
            for (int i = 0; i < am; i++) {
                for (int j = 0; j < am; j++) {
                    a.M[i][j]=A.M[i][j];
                }
                for (int k = am; k < A.M.length; k++) {
                    b.M[i][k-am]=A.M[i][k];
                }
            }
            for (int l = am; l < A.M.length; l++) {
                for (int j = 0; j < am; j++) {
                    c.M[l-am][j]=A.M[l][j];
                }
                for (int k = am; k < A.M.length; k++) {
                    d.M[l-am][k-am]=A.M[l][k];
                }
            }
            MatrixD az = closureBlok(a, ring);
            MatrixD e = c.multCU(az, ring);
            MatrixD D = d.add(e.multCU(b, ring), ring);
            MatrixD Dz = closureBlok(D, ring);
            MatrixD r3 = Dz.multCU(e, ring);
            MatrixD f = az.multCU(b, ring);
            MatrixD r2 = f.multCU(Dz, ring);
            MatrixD r1 = az.add(f.multCU(r3, ring), ring);
            MatrixD r = new MatrixD(new Element[A.M.length][A.M.length]);
            for (int i = 0; i < am; i++) {
                for (int j = 0; j < am; j++) {
                    r.M[i][j]=r1.M[i][j];
                }
                for (int k = am; k < A.M.length; k++) {
                    r.M[i][k]=r2.M[i][k-am];
                }
            }
            for (int l = am; l < A.M.length; l++) {
                for (int j = 0; j < am; j++) {
                    r.M[l][j]=r3.M[l-am][j];
                }
                for (int k = am; k < A.M.length; k++) {
                    r.M[l][k]=Dz.M[l-am][k-am];
                }
            }
            return r;
        }
    }
    
     /**
     * Вычисляет матрицу M = I + A + A^2 + ...A^n-1
     * @param A квадратная матрица
     * @param ring тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return M
     * @throws java.lang.Exception
     */
    public static MatrixD AplusPar(MatrixD A, Ring ring, String args[],Intracomm com) throws Exception {
        int myrank = com.getRank();
        int n = com.getSize();
        MatrixD rez = null;
        MatrixD A1;
        MatrixS B = null;
        MatrixS B1 = null;
        int len;
        if (myrank == 0) {
            len = A.M.length;
            for (int i = 1; i < n; i++) {
                MPITransport.sendObject(len, i, 10);
            }
        } else {
            len = (int) MPITransport.recvObject(0, 10);
        }
        if (myrank == 0) {
            rez = (MatrixD.ONE(len, ring)).add(A, ring);
            A1 = A.copy();
            B = new MatrixS(A, ring);
            B1 = new MatrixS(A1, ring);
        }
        MatrixS C;
        for (int i = 2; i < len; i++) {
            com.barrier();
            C = multTropPar(args, B, B1, ring, com);
            if (myrank == 0) {
                A1 = new MatrixD(C, ring);
                rez = rez.add(A1, ring);
                B1 = new MatrixS(A1, ring);
            }
        }
        return rez;
    }
    
    /**
     * Вычисляет величину Tr A = tr A + tr A^2 + ... + tr A^n
     *
     * @param A квадратная матрица
     * @param ring кольцо или тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return Tr A
     * @throws java.lang.Exception
     */
    public static Element TrPar(MatrixD A, Ring ring, String args[],Intracomm com) throws Exception {
        int myrank = com.getRank();
        int n = com.getSize();
        Element rez = null;
        MatrixD A1 = null;
        MatrixS As = null;
        if (myrank == 0) {
            rez = A.track(ring);
            A1 = A.copy();
            As = new MatrixS(A, ring);
        }
        MatrixS A1s = null;
        MatrixS C;
        int len;
        if (myrank == 0) {
            len = A.M.length;
            for (int i = 1; i < n; i++) {
                MPITransport.sendObject(len, i, 12);
            }
        } else {
            len = (int) MPITransport.recvObject(0, 12);
        }
        for (int m = 2; m <= len; m++) {
            if (myrank == 0) {
                A1s = new MatrixS(A1, ring);
            }
            com.barrier();
            C = multTropPar(args, As, A1s, ring, com);
            if (myrank == 0) {
                A1 = new MatrixD(C, ring);
                Element tr1 = A1.track(ring);
                rez = rez.add(tr1, ring);
            }
        }
        if (myrank == 0) {
            for (int i = 1; i < n; i++) {
                MPITransport.sendObject(rez, i, 11);
            }
        } else {
            rez = (Element) MPITransport.recvObject(0, 11);
        }
        return rez;//необходим на всех процессорах
    }

    /**
     * Вычисляет матрицу A*, в этой матрице столбцы нулевые, если
     * соответствующие столбцы матриц Aplus и Amult не равны; в противном случае
     * столбцы матрицы A* совпадают со столбцами матрицы Aplus
     *
     * @param A    квадратная матрица n*n
     * @param T сумма треков
     * @param ring тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return матрицу A*
     * @throws java.lang.Exception
     */
    public static MatrixD AstarPar(MatrixD A, Element T, Ring ring, String args[],Intracomm com) throws Exception {
        int myrank = com.getRank();
        MatrixD rez = null;
        if (myrank == 0) {
            rez = MatrixD.ZERO(A.M.length, ring);
        }
        if (T.equals(ring.numberONE, ring)) {
            MatrixD Ap = AplusPar(A, ring, args, com);
            MatrixS Ap1 = null;
            MatrixS As = null;
            if (myrank == 0) {
                Ap1 = new MatrixS(Ap, ring);
                As = new MatrixS(A, ring);
            }
            com.barrier();
            MatrixS Am1 = multTropPar(args, As, Ap1, ring, com);
            if (myrank == 0) {
                MatrixD Am = new MatrixD(Am1, ring);
                boolean flag;
                for (int j = 0; j < A.M.length; j++) {
                    flag = true;
                    for (int i = 0; i < A.M.length; i++) {
                        if (!((Ap.M[i][j]).equals(Am.M[i][j], ring))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int k = 0; k < A.M.length; k++) {
                            rez.M[k][j] = Ap.M[k][j];
                        }
                    }
                }
            }
        }
        return rez;
    }
    
    /**
     * Вычисляет матрицу A*, в этой матрице столбцы нулевые, если
     * соответствующие столбцы матриц Aplus и Amult не равны; в противном случае
     * столбцы матрицы A* совпадают со столбцами матрицы Aplus
     *
     * @param A    квадратная матрица n*n
     * @param Ap замыкание матрицы A
     * @param T сумма треков
     * @param ring тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return матрицу A*
     * @throws java.lang.Exception
     */
    public static MatrixD AstarPar1(MatrixD A, MatrixD Ap, Element T, Ring ring, String args[], Intracomm com) throws Exception {
        int myrank = com.getRank();
        MatrixD rez = null;
        if (myrank == 0) {
            rez = MatrixD.ZERO(A.M.length, ring);
        }
        if (T.equals(ring.numberONE, ring)) {
            MatrixS Ap1 = null;
            MatrixS As = null;
            if (myrank == 0) {
                Ap1 = new MatrixS(Ap, ring);
                As = new MatrixS(A, ring);
            }
            com.barrier();
            MatrixS Am1 = multTropPar(args, As, Ap1, ring, com);
            if (myrank == 0) {
                MatrixD Am = new MatrixD(Am1, ring);
                boolean flag;
                for (int j = 0; j < A.M.length; j++) {
                    flag = true;
                    for (int i = 0; i < A.M.length; i++) {
                        if (!((Ap.M[i][j]).equals(Am.M[i][j], ring))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int k = 0; k < A.M.length; k++) {
                            rez.M[k][j] = Ap.M[k][j];
                        }
                    }
                }
            }
        }
        return rez;
    }

    /**
     * Решение однородного уравнения Беллмана Ax=x; если существует
     * нетривиальное решение, то решение x=a_1 * v1 + ... + a_n * v_n,
     * где a_1,...,a_n - любые числа, (операции из ring); а вектор v_(n+1) для однородного
     * уравнения можно не учитывать
     *
     * @param A    квадратная матрица
     * @param ring тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return либо тривиальное решение x=0, либо набор векторов v_1,...,v_n
     * @throws java.lang.Exception
     */
    public static VectorS[] BellmanEquationPar(MatrixD A, Ring ring, String args[],Intracomm com) throws Exception  {
        int myrank = com.getRank();
        Element tr = TrPar(A, ring, args,com);
        if (tr.equals(ring.numberONE, ring)) {
            MatrixD As = AstarPar(A, tr, ring, args, com);
            if (myrank == 0) {
                MatrixD AsT = As.transpose(ring);
                int sNS = AsT.M.length + 1;
                VectorS[] vv = new VectorS[sNS];
                for (int j = 0; j < sNS - 1; j++) {
                    vv[j] = new VectorS(AsT.M[j]);
                }
                vv[sNS - 1] = new VectorS(ring.numberZERO);
                return vv;
            } else {
                return null;
            }
        } else {
            if (myrank == 0) {
                Element[] rez = new Element[A.M.length];
                for (int k = 0; k < A.M.length; k++) {
                    rez[k] = ring.numberZERO;
                }
                return new VectorS[] {new VectorS(rez)};
            } else {
                return null;
            }
        }
    }

    /**
     * Решение неоднородного уравнения Беллмана Ax+b=x; в случае когда решение
     * не единственно, x=a_1 * v1 + ... + a_n * v_n + v_(n+1),
     * где a_1,...,a_n - любые числа, (операции из ring)
     *
     * @param A    квадратная матрица
     * @param b    матрица, состоящая из одного столбца
     * @param ring тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return null, если решение не существует; вектор, если решение
     * единственно; набор векторов v_1,...,v_n, v_(n+1), если решение не единственно
     * @throws java.lang.Exception
     */
    public static VectorS[] BellmanEquationPar(MatrixD A, MatrixD b, Ring ring, String args[],Intracomm com) throws Exception  {
        int myrank = com.getRank();
        MatrixD x = null;
        if (myrank == 0) {
            x = new MatrixD(new Element[A.M.length][1]);
        }
        MatrixD AsT = new MatrixD(ring);
        Element tr = TrPar(A, ring, args, com);
        if (tr.compareTo(ring.numberONE, -2, ring)) {
            MatrixD Ap = closureBlokPar(A, ring, args, com);
            MatrixS ApS = null;
            MatrixS bs = null;
            if (myrank == 0) {
                ApS = new MatrixS(Ap, ring);
                bs = new MatrixS(b, ring);
            }
            MatrixS c = multTropPar(args, ApS, bs, ring, com);
            if (myrank == 0) {
                x = new MatrixD(c, ring);
            } 
        } else {
            if (tr.compareTo(ring.numberONE, 0, ring)) {
                MatrixD Ap = AplusPar(A, ring, args, com);
                MatrixS Aps = null;
                MatrixS bs = null;
                if (myrank == 0) {
                    Aps = new MatrixS(Ap, ring);
                    bs = new MatrixS(b, ring);
                }
                MatrixS c = multTropPar(args, Aps, bs, ring, com);
                MatrixD As = AstarPar1(A, Ap, tr, ring, args, com);
                if (myrank == 0) {
                    AsT = As.transpose(ring);
                    x = new MatrixD(c, ring);
                } 
            } else {
                if (myrank == 0) {
                    boolean flag = true;
                    for (int i = 0; i < A.M.length; i++) {
                        if (!(b.M[i][0]).equals(ring.numberZERO, ring)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int j = 0; j < A.M.length; j++) {
                            x.M[j][0] = ring.numberZERO;
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
        if (myrank == 0) {
            int sNS = AsT.M.length + 1;
            VectorS[] vv = new VectorS[sNS];
            for (int j = 0; j < sNS - 1; j++) {
                vv[j] = new VectorS(AsT.M[j]);
            }
            vv[sNS - 1] = new VectorS(x.transpose(ring).M[0]);
            return vv;
        } else {
            return null;
        }
    }

    /**
     * Тропическая бесконечность заданного тропического полукольца
     * @param ring тропическое полукольцо
     * @return тропическую бесконечность
     */
    public static Element infinityTrop(Ring ring) {
        Element inf;
        switch (ring.algebra[0]) {
            case Ring.R64MaxPlus:
            case Ring.RMaxPlus:
            case Ring.ZMaxPlus:
            case Ring.R64MaxMult:
            case Ring.RMaxMult:
            case Ring.ZMaxMult:
                inf = Element.POSITIVE_INFINITY;
                break;
            case Ring.R64MinPlus:
            case Ring.RMinPlus:
            case Ring.ZMinPlus:
                inf = Element.NEGATIVE_INFINITY;
                break;   
            case Ring.R64MinMult:
                inf = new NumberR64MinMult(new NumberR64(0), ring);
                break;
            case Ring.RMinMult:
                inf = new NumberRMinMult(new NumberR(0), ring);
                break;
            case Ring.ZMinMult:
                inf = new NumberZMinMult(new NumberZ(0), ring);
                break;
            default:
                ring.exception.append("Error tropic algebra type:" + ring.toString());
                return null;
        }
        return inf;
    }
    
    /**
     * Решение однородного неравенства Ax<=x; если существует нетривиальное
     * решение, то чтобы получить решение, нужно умножить матрицу B на любой
     * вектор-столбец v; т.е. x=B v
     *
     * @param A    квадратная матрица
     * @param ring тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return либо тривиальное решение x=0, либо матрицу B, в этом случае x = B
     * v, где v-любой вектор
     * @throws java.lang.Exception
     */
    public static MatrixS BellmanInequalityPar(MatrixS A, Ring ring, String args[],Intracomm com) throws Exception {
        int myrank = com.getRank();
        Element inf = null;
        MatrixD A1 = null;
        if (myrank == 0) {
            inf = infinityTrop(ring);
            A1 = new MatrixD(A, ring);
        }
        MatrixD B = closureBlokPar(A1, ring, args, com);
        if (myrank == 0) {
            MatrixD rez = new MatrixD(new Element[A.M.length][1]);
            if (!((B.M[A.M.length - 1][A.M.length - 1].compareTo(inf, 0, ring)) | (B.M[A.M.length - 1][A.M.length - 1].compareTo(Element.NAN, 0, ring)))) {
                return new MatrixS(B, ring);
            } else {
                for (int k = 0; k < A.M.length; k++) {
                    rez.M[k][0] = ring.numberZERO;
                }
            }
            return new MatrixS(rez, ring);
        } else {
            return null;
        }
    }

    /**
     * Решение неоднородного неравенства Ax+b<=x; в случае когда решение
     * cуществует и не единственно, из полученной матрицы берем первый столбец и
     * складываем с остальной частью матрицы, умноженной на любой вектор
     * (операции из данного тропического полукольца); т.е. пусть B=(B1|B2) -
     * полученная матрица, где В1 - первый столбец, B2 - остальная матрица,
     * тогда x=B1+B2*v, где v - любой вектор, сложение и умножение из данного
     * тропического полукольца
     *
     * @param As квадратная матрица
     * @param b    матрица, состоящая из одного столбца
     * @param ring тропическое полукольцо
     * @param args массив из main
     * @param com коммуникатор
     * @return null, если решение не существует; вектор-столбец, если решение
     * тривиально; матрицу, если решение не единственно
     * @throws java.lang.Exception
     */
    public static MatrixS BellmanInequalityPar(MatrixS As, MatrixD b, Ring ring, String args[],Intracomm com) throws Exception {
        int myrank = com.getRank();
        int n = com.getSize();
        MatrixD A = null;
        Element inf = infinityTrop(ring);
        if (myrank == 0) {
            A = new MatrixD(As, ring);
        }
        MatrixD Ap = closureBlokPar(A, ring, args, com);
        Element el;
        if (myrank == 0) {
            el = Ap.M[Ap.M.length-1][Ap.M.length-1];
            for (int i = 1; i < n; i++) {
                MPITransport.sendObject(el, i, 34);
            }
        } else {
            el = (Element) MPITransport.recvObject(0, 34);
        }
        if (!((el.compareTo(inf, 0, ring))|(el.compareTo(Element.NAN, 0, ring)))) {
            MatrixS Aps = null;
            MatrixS bs = null;
            if (myrank == 0) {
                Aps = new MatrixS(Ap, ring);
                bs = new MatrixS(b, ring);
            }
            MatrixS c = multTropPar(args, Aps, bs, ring, com);
            MatrixD x1;
            if (myrank == 0) {
                x1 = new MatrixD(c, ring);
                MatrixD x2 = new MatrixD(new Element[A.M.length][A.M.length + 1]);
                for (int k = 0; k < A.M.length; k++) {
                    x2.M[k][0] = x1.M[k][0];
                    for (int l = 1; l < (A.M.length + 1); l++) {
                        x2.M[k][l] = Ap.M[k][l - 1];
                    }
                }
                return new MatrixS(x2, ring);
            } else {
                return null;
            }
        } else {
            if (myrank == 0) {
                MatrixD x = new MatrixD(new Element[A.M.length][1]);
                boolean flag = true;
                for (int i = 0; i < A.M.length; i++) {
                    if (!(b.M[i][0]).compareTo(ring.numberZERO, 0, ring)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    for (int j = 0; j < A.M.length; j++) {
                        x.M[j][0] = ring.numberZERO;
                    }
                    return new MatrixS(x, ring);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
    
    /**
     * Параллельное умножение двух матриц
     * @param args массив из main
     * @param A матрица
     * @param B матрица
     * @param ring кольцо
     * @param com коммуникатор
     * @return A*B
     * @throws Exception 
     */
    public static MatrixS multTropPar(String args[], MatrixS A, MatrixS B, Ring ring, Intracomm com) throws Exception {
        int myRank=com.getRank();
        MatrixS multC = null;
        Object[]params=new Object[3];
        if (myRank==0){
            params[0]=new Integer(4);
            params[1]=A;
            params[2]=B;
        }
        FactoryMultiplyMatrix f = new FactoryMultiplyMatrix();
        DispThread disp = new DispThread(0, f, 2, 10, args,params);
        if (myRank==0){
            TaskMultiplyMatrix ab = (TaskMultiplyMatrix)disp.GetStartTask();
            multC=ab.c;
        }
        return multC;
    }
    
    /**
     * Псевдообращение матрицы A
     * @param A произвольная матрица
     * @param ring тропическое полукольцо
     * @param com коммуникатор
     * @return псевдообратную матрицу
     * @throws Exception 
     */
    public static MatrixD psevdoInversePar(MatrixD A, Ring ring, Intracomm com) throws Exception {
        int myrank = com.getRank();
        int p = com.getSize();
        Object in[] = new Object[4];//определение кусков матрицы для каждого процессора
        int n = A.M.length;
        int m = A.M[0].length;
        int q = n * m / p;
        if (myrank == 0) {
            in[2] = n - 1;
            in[3] = m - 1;
            int p1 = p - 1;
            for (int i = n - 1; i >= 0; i--) {
                for (int j = m - 1; j >= 0; j--) {
                    q--;
                    if (q == 0) {
                        in[0] = i;
                        in[1] = j;
                        MPITransport.sendObjectArray(in, 0, 4, p1, 10);
                        p1--;
                        if (j > 0) {
                            in[2] = i;
                            in[3] = j - 1;
                        } else {
                            in[2] = i - 1;
                            in[3] = m - 1;
                        }
                        q = n * m / p;
                        if (p1 == 0) {
                            in[0] = 0;
                            in[1] = 0;
                            break;
                        }
                    }
                }
                if (p1 == 0) {
                    break;
                }
            }
        } else {
            MPITransport.recvObjectArray(in, 0, 4, 0, 10);
        }
        //выполнение псевдообращения каждым процессором своей части
        MatrixD B = new MatrixD(new Element[m][n]);
        int i1 = (int) in[0];
        int i2 = (int) in[2];
        int j1 = (int) in[1];
        int j2 = (int) in[3];
        int m1 = j1;
        for (int k = i1; k <= i2; k++) {
            while (m1 < m) {
                if (A.M[k][m1].compareTo(ring.numberZERO, 0, ring)) {
                    B.M[m1][k] = ring.numberZERO;
                } else {
                    B.M[m1][k] = A.M[k][m1].inverse(ring);
                }
                if (k == i2 & m1 == j2) {
                    break;
                }
                m1++;
            }
            m1 = 0;
        }
        //сборка на нулевом процессоре
        if (myrank != 0) {
            MPITransport.sendObject(B, 0, 21);
        } else {
            for (int pr = 1; pr < p; pr++) {
                MatrixD C = (MatrixD) MPITransport.recvObject(pr, 21);
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        if (C.M[i][j] != null) {
                            B.M[i][j] = C.M[i][j];
                        }
                    }
                }
            }
        }
        //результат всем процессорам
        if(myrank == 0){
            for (int l = 1; l < p; l++) {
                MPITransport.sendObject(B, l, 22);
            }
        }else{
            B = (MatrixD) MPITransport.recvObject(0, 22);
        }
        return B;
    }
    
    /**
     * Проверяет является ли x решением уравнения Ax=b
     *
     * @param A    квадратная матрица
     * @param x    вектор(матрица, состоящая из одного столбца)
     * @param b    вектор
     * @param ring тропическое полукольцо
     * @param com коммуникатор
     * @param args массив из main
     * @return true, если x является решением, и false в противном случае
     * @throws java.lang.Exception
     */
    public static boolean deltaPar(MatrixD A, MatrixD x, MatrixD b, Ring ring, Intracomm com, String args[]) throws Exception {
        if (x == null) {
            return false;
        }
        boolean flag = true;
        com.barrier();
        MatrixS Ax1=multTropPar(args, new MatrixS(A,ring), new MatrixS(x,ring), ring, com);
        MatrixD Ax = new MatrixD(Ax1,ring);
        for (int i = 0; i < A.M.length; i++) {
            if (!((Ax.M[i][0]).equals(b.M[i][0], ring))) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * Решение векторного уравнения Ax=b
     *
     * @param A    квадратная матрица
     * @param b    вектор-столбец
     * @param ring тропическое полукольцо
     * @param com коммуникатор
     * @param args массив из main
     * @return x - частное решение, если оно существует; иначе null
     * @throws java.lang.Exception
     */
    public static MatrixS solveLAETropicPar(MatrixD A, MatrixD b, Ring ring, Intracomm com, String args[]) throws Exception {
        MatrixD b1 = psevdoInversePar(b, ring, com);
        com.barrier();
        MatrixS b3 = multTropPar(args, new MatrixS(b1,ring), new MatrixS(A,ring), ring, com);
        MatrixD b2 = new MatrixD(b3,ring);
        com.barrier();
        MatrixD x = psevdoInversePar(b2, ring, com);
        if (!deltaPar(A, x, b, ring, com, args)) {
            return null;
        }
        return new MatrixS(x);
    }

    /**
     * Решение векторного неравенства Ax<=b
     *
     * @param A    квадратная матрица
     * @param b    вектор-столбец
     * @param ring тропическое полукольцо
     * @param com коммуникатор
     * @param args массив из main
     * @return общее решение неравенства Ax<=b
     * @throws java.lang.Exception
     */
    public static VectorS solveLAITropicPar(MatrixD A, MatrixD b, Ring ring, Intracomm com, String args[]) throws Exception {
        MatrixD b1 = psevdoInversePar(b, ring, com);
        com.barrier();
        MatrixS b3 = multTropPar(args, new MatrixS(b1,ring), new MatrixS(A,ring), ring, com);
        MatrixD b2 = new MatrixD(b3,ring);
        com.barrier();
        MatrixD x1 = psevdoInversePar(b2, ring, com);
        Element[] set = new Element[x1.M.length];
        switch (ring.algebra[0]) {
            case Ring.R64MaxPlus:
            case Ring.RMaxPlus:
            case Ring.ZMaxPlus:
            case Ring.R64MaxMult:
            case Ring.RMaxMult:
                for (int i = 0; i < x1.M.length; i++) {
                    set[i] = new SubsetR(ring.numberZERO, x1.M[i][0], false, false);
                }
                break;
            case Ring.R64MinPlus:
            case Ring.RMinPlus:
            case Ring.ZMinPlus:
            case Ring.R64MinMult:
            case Ring.RMinMult:
                for (int i = 0; i < x1.M.length; i++) {
                    set[i] = new SubsetR(x1.M[i][0], ring.numberZERO, false, false);
                }
                break;
            default:
                ring.exception.append("Error tropic algebra type:" + ring.toString());
                return null;
        }
        return new VectorS(set);
    }
    
    // mpirun -np 2 java -cp /home/serega/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.webCluster.algorithms.tropic.tropParallel
    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        //QueryResult queryRes=com.mathpar.parallel.webCluster.engine.Tools.getDataFromClusterRootNode(args);
        Ring ring= new Ring("R64MaxPlus[x]");
        Intracomm com=MPI.COMM_WORLD;
        int myRank = com.getRank();
        int p = com.getSize();
        int n;
        if (args.length == 0) {
            n = 4;
        } else {
            String ar = args[0];
            n = new Integer(ar);
        }
        if (myRank == 0) {
            System.out.println("n=" + n + "    p=" + p);
        }
        
        long[][] aa = new long[n][n];
        long[][] aa2 = new long[n][1];
        MatrixD a;
        MatrixD b;
        if (myRank==0){
            for(int i=0; i<n; i++){
                for(int j=0; j<n; j++){
                    double a1=Math.random();
                    aa[i][j]=Math.round(-100*a1);
                }
                aa[i][i]=0;
                double a2=Math.random();
                aa2[i][0]=Math.round(100*a2);
            }
            a = new MatrixD(aa,ring);
            b = new MatrixD(aa2,ring);
            for(int k=1; k<MPI.COMM_WORLD.getSize(); k++){
               MPITransport.sendObject(a, k, 12);
               MPITransport.sendObject(b, k, 13);
            }
        }else{
            a=(MatrixD)MPITransport.recvObject(0, 12);
            b=(MatrixD)MPITransport.recvObject(0, 13);
        }
        long t1 = 0;
        long t2 = 0;
        long t3 = 0;
        long t4 = 0;
        long t5 = 0;
        long t6 = 0;
        long t7 = 0;
        long t8 = 0;
        long t9 = 0;
        long t10 = 0;
        int f;
        if (args.length == 0) {
            f = 1;
        } else {
            f = new Integer(args[1]);
        }
        if (f == 1) {
            if (myRank == 0) {
                t1 = System.currentTimeMillis();
            }
            BellmanEquationPar(a, ring, args,MPI.COMM_WORLD);
            if (myRank == 0) {
                t2 = System.currentTimeMillis();
                System.out.println("Equation od t=" + (t2 - t1));
            }
        } else {
            if (f == 2) {
                if (myRank == 0) {
                    t3 = System.currentTimeMillis();
                }
                BellmanEquationPar(a, b, ring, args,MPI.COMM_WORLD);
                if (myRank == 0) {
                    t4 = System.currentTimeMillis();
                    System.out.println("Equation neod t=" + (t4 - t3));
                }
            } else {
                MatrixS a22 = new MatrixS(a, ring);
                if (f == 3) {
                    if (myRank == 0) {
                        t5 = System.currentTimeMillis();
                    }
                    BellmanInequalityPar(a22, ring, args,MPI.COMM_WORLD);
                    if (myRank == 0) {
                        t6 = System.currentTimeMillis();
                        System.out.println("Inequality od t=" + (t6 - t5));
                    }
                } else {
                    if (f == 4) {
                        if (myRank == 0) {
                            t7 = System.currentTimeMillis();
                        }
                        BellmanInequalityPar(a22, b, ring, args, MPI.COMM_WORLD);
                        if (myRank == 0) {
                            t8 = System.currentTimeMillis();
                            System.out.println("Inequality neod t=" + (t8 - t7));
                        }
                    } else {
                        if (f == 5) {
                            MatrixS am = new MatrixS(a,ring);
                            MatrixS bm = new MatrixS(a,ring);
                            if (myRank == 0) {
                                t9 = System.currentTimeMillis();
                            }
                            multTropPar(args, am, bm, ring, com);
                            if (myRank == 0) {
                                t10 = System.currentTimeMillis();
                                System.out.println("mult t=" + (t10 - t9));
                            }
                        } else {
                            if (myRank == 0) {
                                System.out.println("2 parametr= ot 1 do 5");
                            }
                        }

                    }
                }
            }
        }
        //com.mathpar.parallel.webCluster.engine.Tools.sendFinishMessage(args);
        MPI.Finalize();
    }
}
