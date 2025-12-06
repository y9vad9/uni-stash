package com.mathpar.students.OLD.stud2014.kireev;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;

/**
 * Уравнения и неравенства в тропической математике
 *
 * @author kireev
 */
public class equation {

    /**
     * Проверяет является ли x решением уравнения Ax=b
     *
     * @param A    квадратная матрица
     * @param x    вектор(матрица, состоящая из одного столбца)
     * @param b    вектор
     * @param ring тропическое полукольцо
     * @return true, если x является решением, и false в противном случае
     */
    public static boolean delta(MatrixD A, MatrixD x, VectorS b, Ring ring) {
        if (x == null) {
            return false;
        }
        boolean flag = true;
        MatrixD Ax = A.multCU(x, ring);
        for (int i = 0; i < A.M.length; i++) {
            if (!((Ax.M[i][0]).equals(b.V[i], ring))) {
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
     * @param b    вектор
     * @param ring тропическое полукольцо
     * @return x - частное решение, если оно существует; иначе null
     */
    public static MatrixS solveLAETropic(MatrixD A, VectorS b, Ring ring) {
        VectorS b1 = b.psevdoInverse(ring);
        VectorS b2 = b1.multiply(A.M, ring);
//        Element[][] x1 = new Element[b2.V.length][1];
//        x1 = b2.psevdoInverse(ring);
        MatrixD x = new MatrixD(b2.psevdoInverse(ring),false,A.fl);
        if (!delta(A, x, b, ring)) {
            return null;
        }
        return new MatrixS(x);
    }

    /**
     * Решение векторного неравенства Ax<=b
     *
     * @param A    квадратная матрица
     * @param b    вектор
     * @param ring тропическое полукольцо
     * @return общее решение неравенства Ax<=b
     */
    public static VectorS solveLAITropic(MatrixD A, VectorS b, Ring ring) {
        VectorS b1 = b.psevdoInverse(ring);
        VectorS b2 = b1.multiply(A.M, ring);
        Element[][] x1;
        x1 =new MatrixD(b2.psevdoInverse(ring), false, A.fl).M;
        Element[] set = new Element[b2.V.length];
        switch (ring.algebra[0]) {
            case Ring.R64MaxPlus:
            case Ring.RMaxPlus:
            case Ring.ZMaxPlus:
            case Ring.R64MaxMult:
            case Ring.RMaxMult:
                for (int i = 0; i < b2.V.length; i++) {
                    set[i] = new SubsetR(ring.numberZERO, x1[i][0], false, false);
                }
                break;
            case Ring.R64MinPlus:
            case Ring.RMinPlus:
            case Ring.ZMinPlus:
            case Ring.R64MinMult:
            case Ring.RMinMult:
                for (int i = 0; i < b2.V.length; i++) {
                    set[i] = new SubsetR(x1[i][0], ring.numberZERO, false, false);
                }
                break;
            default:
                ring.exception.append("Error tropic algebra type:" + ring.toString());
                return null;
        }
        return new VectorS(set);
    }

    /**
     * Вычисляет матрицу M = A*(I + A + A^2 + ... +A^(n-1))
     *
     * @param A    квадратная матрица n*n
     * @param ring тропическое полукольцо
     * @return матрицу M = A*(I + A + A^2 + ... +A^(n-1)) = A + A^2 + ... + A^n
     */
    public static MatrixD Amult(MatrixD A, Ring ring) {
        MatrixD B = new MatrixD((new MatrixS(A, ring)).closure(ring));
        MatrixD rez = A.multCU(B, ring);
        return rez;
    }

    /**
     * Вычисляет матрицу A*, в этой матрице столбцы нулевые, если
     * соответствующие столбцы матриц Aplus и Amult не равны; в противном случае
     * столбцы матрицы A* совпадают со столбцами матрицы Aplus
     *
     * @param A    квадратная матрица n*n
     * @param T сумма треков матрицы A
     * @param ring тропическое полукольцо
     * @return матрицу A*
     */
    public static MatrixD Astar(MatrixD A, Element T, Ring ring) {
        MatrixD rez = MatrixD.ZERO(A.M.length, ring);
        if (T.equals(ring.numberONE, ring)) {
            MatrixD Ap = new MatrixD((new MatrixS(A, ring)).closure(ring));
            MatrixD Am = A.multCU(Ap, ring);//Amult(A,ring)
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
        return rez;
    }
    
    /**
     * Вычисляет матрицу A*, в этой матрице столбцы нулевые, если
     * соответствующие столбцы матриц Aplus и Amult не равны; в противном случае
     * столбцы матрицы A* совпадают со столбцами матрицы Aplus
     *
     * @param A    квадратная матрица n*n
     * @param Ap замыкание матрицы A
     * @param T сумма треков матрицы A
     * @param ring тропическое полукольцо
     * @return матрицу A*
     */
    public static MatrixD Astar1(MatrixD A, MatrixD Ap, Element T, Ring ring) {
        MatrixD rez = MatrixD.ZERO(A.M.length, ring);
        if (T.equals(ring.numberONE, ring)) {
            MatrixD Am = A.multCU(Ap, ring);//Amult(A,ring)
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
     * @return либо тривиальное решение x=0, либо набор векторов v_1,...,v_n
     */
    public static VectorS[] BellmanEquation(MatrixD A, Ring ring) {
        Element[] rez = new Element[A.M.length];
        Element tr = A.Tr(ring);
        MatrixD AsT = new MatrixD(ring);
        if (tr.equals(ring.numberONE, ring)) {
            MatrixD As = Astar(A, tr, ring);
            AsT = As.transpose(ring);
            int sNS = AsT.M.length + 1;
            VectorS[] vv = new VectorS[sNS];
            for (int j = 0; j < sNS - 1; j++) {
                vv[j] = new VectorS(AsT.M[j]);
            }
            vv[sNS - 1] = new VectorS(ring.numberZERO);
            return vv;
        } else {
            for (int k = 0; k < A.M.length; k++) {
                rez[k] = ring.numberZERO;
            }
        }
        return new VectorS[]{new VectorS(rez)};
    }

    /**
     * Решение неоднородного уравнения Беллмана Ax+b=x; в случае когда решение
     * не единственно, x=a_1 * v1 + ... + a_n * v_n + v_(n+1),
     * где a_1,...,a_n - любые числа, (операции из ring)
     *
     * @param A    квадратная матрица
     * @param b    матрица, состоящая из одного столбца
     * @param ring тропическое полукольцо
     * @return null, если решение не существует; вектор, если решение
     * единственно; набор векторов v_1,...,v_n, v_(n+1), если решение не единственно
     */
    public static VectorS[] BellmanEquation(MatrixD A, MatrixD b, Ring ring) {
        MatrixD x = new MatrixD(new Element[A.M.length][1]);
        MatrixD AsT = new MatrixD(ring);
        Element tr = A.Tr(ring);
        if (tr.compareTo(ring.numberONE, -2, ring)) {
            MatrixD Ap = closureBlok(A, ring);
            x = Ap.multCU(b, ring);
        } else {
            if (tr.compareTo(ring.numberONE, 0, ring)) {
                MatrixD Ap = new MatrixD((new MatrixS(A, ring)).closure(ring));
                x = Ap.multCU(b, ring);
                MatrixD As = Astar1(A, Ap, tr, ring);
                AsT = As.transpose(ring);
            } else {
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
        int sNS = AsT.M.length + 1;
        VectorS[] vv = new VectorS[sNS];
        for (int j = 0; j < sNS - 1; j++) {
            vv[j] = new VectorS(AsT.M[j]);
        }
        vv[sNS - 1] = new VectorS(x.transpose(ring).M[0]);
        return vv;
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
     * @return либо тривиальное решение x=0, либо матрицу B, в этом случае x = B
     * v, где v-любой вектор
     */
    public static MatrixS BellmanInequality(MatrixS A, Ring ring) {
        Element inf = infinityTrop(ring);
        MatrixD B = closureBlok(new MatrixD(A, ring), ring);
        if (!((B.M[A.M.length-1][A.M.length-1].compareTo(inf, 0, ring))|(B.M[A.M.length-1][A.M.length-1].compareTo(Element.NAN, 0, ring)))) {
            return new MatrixS(B, ring);
        } else {
            MatrixD rez = new MatrixD(new Element[A.M.length][1]); 
            for (int k = 0; k < A.M.length; k++) {
                rez.M[k][0] = ring.numberZERO;
            }
            MatrixS rez2 = new MatrixS(rez, ring);
            return rez2;
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
     * @param As    квадратная матрица
     * @param b    матрица, состоящая из одного столбца
     * @param ring тропическое полукольцо
     * @return null, если решение не существует; вектор-столбец, если решение
     * тривиально; матрицу, если решение не единственно
     */
    public static MatrixS BellmanInequality(MatrixS As, MatrixD b, Ring ring) {
        Element inf = infinityTrop(ring);
        MatrixD A = new MatrixD(As, ring);
        MatrixD x = new MatrixD(new Element[A.M.length][1]);
        MatrixD Ap = closureBlok(A, ring);
        if (!((Ap.M[Ap.M.length-1][Ap.M.length-1].compareTo(inf, 0, ring))|(Ap.M[Ap.M.length-1][Ap.M.length-1].compareTo(Element.NAN, 0, ring)))) { 
            MatrixD x1 = Ap.multCU(b, ring);
            MatrixD x2 = new MatrixD(new Element[A.M.length][A.M.length + 1]);
            for (int k = 0; k < A.M.length; k++) {
                x2.M[k][0] = x1.M[k][0];
                for (int l = 1; l < (A.M.length + 1); l++) {
                    x2.M[k][l] = Ap.M[k][l - 1];
                }
            }
            return new MatrixS(x2, ring);
        } else {
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
            } else {
                return null;
            }
        }
        return new MatrixS(x, ring);
    }

    /**
     * Вычисление замыкания матрицы эскалаторным методом
     *
     * @param B    квадратная матрица
     * @param ring полукольцо
     * @return замыкание матрицы
     */
    public static MatrixD closureEsc(MatrixD B, Ring ring) {
        MatrixD A = B.copy();
        A.M[0][0] = A.M[0][0].closure(ring);
        for (int i = 0; i < A.M.length - 1; i++) {
            MatrixD Ag1 = new MatrixD(new Element[i + 1][1]);
            MatrixD hA1 = new MatrixD(new Element[1][i + 1]);
            MatrixD A1 = new MatrixD(new Element[i + 1][i + 1]);
            for (int j = 0; j <= i; j++) {
                Ag1.M[j][0] = A.M[j][i + 1];
                hA1.M[0][j] = A.M[i + 1][j];
                for (int k = 0; k <= i; k++) {
                    A1.M[j][k] = A.M[j][k];
                }
            }
            MatrixD Ag = (MatrixD) A1.multiply(Ag1, ring);
            MatrixD hA = (MatrixD) hA1.multiply(A1, ring);
            A.M[i + 1][i + 1] = A.M[i + 1][i + 1].add(hA1.multCU(Ag, ring).M[0][0], ring);
            A.M[i + 1][i + 1] = A.M[i + 1][i + 1].closure(ring);
            MatrixD hA2 = new MatrixD(new Element[1][i + 1]);
            for (int j = 0; j <= i; j++) {
                A.M[j][i + 1] = A.M[i + 1][i + 1].multiply(Ag.M[j][0], ring);
                A.M[i + 1][j] = A.M[i + 1][i + 1].multiply(hA.M[0][j], ring);
                hA2.M[0][j] = A.M[i + 1][i + 1].multiply(hA.M[0][j], ring);
            }
            MatrixD A2 = Ag.multCU(hA2, ring);
            for (int j = 0; j <= i; j++) {
                for (int k = 0; k <= i; k++) {
                    A.M[j][k] = A.M[j][k].add(A2.M[j][k], ring);
                }
            }
        }
        return A;
    }

    /**
     * Вычисление замыкания матрицы методом исключения
     *
     * @param B    квадратная матрица
     * @param ring полукольцо
     * @return замыкание матрицы
     */
    public static MatrixD closureIsc(MatrixD B, Ring ring) {
        MatrixD A = B.copy();
        for (int i = 0; i < A.M.length; i++) {
            A.M[i][i] = A.M[i][i].closure(ring);
            for (int k = 0; k < A.M.length; k++) {
                if (k != i) {
                    A.M[k][i] = A.M[k][i].multiply(A.M[i][i], ring);
                }
            }
            for (int k = 0; k < A.M.length; k++) {
                for (int j = 0; j < A.M.length; j++) {
                    if ((k != i) & (j != i)) {
                        A.M[k][j] = A.M[k][j].add(A.M[k][i].multiply(A.M[i][j], ring), ring);
                    }
                }
            }
            for (int j = 0; j < A.M.length; j++) {
                if (j != i) {
                    A.M[i][j] = A.M[i][i].multiply(A.M[i][j], ring);
                }
            }
        }
        return A;
    }
    
    /**
     * Вычисление замыкания матрицы блочно-рекурсивным методом
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

    public static void main(String args[]) {
//        Ring ring = new Ring("R64MinMax[x]");
//        NumberR64MinMax a = new NumberR64MinMax(new NumberR64(2));
//        NumberR64MinMax b = new NumberR64MinMax(new NumberR64(3));
//        Element c = a.multiply(b, ring);
//        System.out.println("c="+c);
//        Ring ring = new Ring("ZMinMax[x]");
//        NumberZMinMax a = new NumberZMinMax(new NumberZ(2));
//        NumberZMinMax b = new NumberZMinMax(new NumberZ(3));
//        Element c = a.multiply(b, ring);
//        System.out.println("c=" + c);
//         Ring ring = new Ring("ZMinPlus[x]");
//         Element[][] aa1 = new Element[][]{{new NumberZMinPlus(NumberZ.ZERO), new NumberZMinPlus(new NumberZ(-2)), ring.numberZERO, ring.numberZERO},
//            {ring.numberZERO, new NumberZMinPlus(NumberZ.ZERO), new NumberZMinPlus(new NumberZ(3)), new NumberZMinPlus(new NumberZ(-1))},
//            {new NumberZMinPlus(new NumberZ(-1)), ring.numberZERO, new NumberZMinPlus(NumberZ.ZERO), new NumberZMinPlus(new NumberZ(-4))},
//            {new NumberZMinPlus(new NumberZ(2)), ring.numberZERO, ring.numberZERO, new NumberZMinPlus(NumberZ.ZERO)}};
//         MatrixD a = new MatrixD(aa1);
//        //MatrixD a = new MatrixD(aa,ring);
//////        VectorS b = new VectorS(bb,ring);
//        System.out.println(""+a.toString(ring));
//        MatrixD b= Az(a,ring);
//        System.out.println("a*="+b.toString(ring));
//        Ring ring = new Ring("ZMinPlus[x]");
//        //Element a111=ring.numberONE;
//        long[][] aa = new long[][]{{1,1,0},{2,0,3}, {3,4,2}};
//        long[] bb = new long[]{8,7,11};
//        //Element zero = new NumberR64MaxPlus(NumberR64.ZERO);
//        MatrixD a = new MatrixD(aa,ring);
//        VectorS b = new VectorS(bb,ring);
//        System.out.println(""+a.toString(ring));
//        System.out.println("b="+b.toString(ring));
//
//        MatrixS x = solveLAETropic(a,b,ring);
//        System.out.println("x=" + x);
//        VectorS x1 = solveLAITropic(a, b, ring);
//        System.out.println("set="+x1.toString(ring));
//
//        System.out.println("--------------------");
//        MatrixD x2 = solveLASTropicGeneral(a, b, ring);
//        System.out.println(""+x2.toString(ring));
//
//        System.out.println(""+delta(a,x,b,ring));
//        MatrixD Am = matrixAuxiliary(a, x, b, ring);
//        System.out.println(""+Am.toString(ring));

//        long[][] aa = new long[][]{{8,10,0,0},{0,5,4,8}, {6,12,11,7},{0,0,0,12}};
//        long[] bb = new long[]{14,11,16,15};
//        //Element zero = new NumberR64MaxPlus(NumberR64.ZERO);
//        MatrixD a = new MatrixD(aa,ring);
//        VectorS b = new VectorS(bb,ring);
//        System.out.println(""+a.toString(ring));
//        System.out.println(""+b.toString(ring));
//
//        MatrixD x = solveLASTropic(a,b,ring);
//        System.out.println(""+x.toString(ring));

//        Element[][] aa = new Element[][]{{new NumberR64MaxPlus(new NumberR64(1)),new NumberR64MaxPlus(new NumberR64(1)),new NumberR64MaxPlus(new NumberR64(0))},
//            {new NumberR64MaxPlus(new NumberR64(2)),new NumberR64MaxPlus(new NumberR64(0)),new NumberR64MaxPlus(new NumberR64(3))},
//            {new NumberR64MaxPlus(new NumberR64(3)),new NumberR64MaxPlus(new NumberR64(4)),new NumberR64MaxPlus(new NumberR64(2))}};
//        Element[] bb = new Element[]{new NumberR64MaxPlus(new NumberR64(8)),new NumberR64MaxPlus(new NumberR64(7)),new NumberR64MaxPlus(new NumberR64(11))};
//        MatrixD a = new MatrixD(aa);
//        VectorS b = new VectorS(bb);
//        System.out.println(""+a.toString(ring));
//        System.out.println(""+b.toString(ring));
//
//        MatrixD x = solveLASTropic(a,b,ring);
//        System.out.println(""+x.toString(ring));

        //RMaxMult Ax=b
//        Ring ring = new Ring("RMaxMult[x]");
//        //Element a111=ring.numberONE;
//        long[][] aa = new long[][]{{1, 1, 0}, {2, 0, 3}, {3, 4, 2}};
//        long[] bb = new long[]{7, 12, 28};
//        //Element zero = new NumberR64MaxPlus(NumberR64.ZERO);
//        MatrixD a = new MatrixD(aa, ring);
//        VectorS b = new VectorS(bb, ring);
//        System.out.println("" + a.toString(ring));
//        System.out.println("b=" + b.toString(ring));
//
//        MatrixS x = solveLAETropic(a, b, ring);
//        System.out.println("x=" + x);
//        VectorS x1 = solveLAITropic(a, b, ring);
//        System.out.println("x<="+x1);
//        System.out.println("--------------------");
//        MatrixD x2 = solveLASTropicGeneral(a, b, ring);
//        System.out.println(""+x2.toString(ring));

        //RMinMult Ax=b
//        Ring ring = new Ring("R64MinMult[x]");
//        //Element a111=ring.numberONE;
//        long[][] aa = new long[][]{{1,1,3},{2,1,3}, {3,4,2}};
//        long[] bb = new long[]{5,7,8};
//        Element zero = new NumberRMinMult(NumberR.ZERO);
//        MatrixD a = new MatrixD(aa,ring,zero);
//        VectorS b = new VectorS(bb,ring);
//        System.out.println(""+a.toString(ring));
//        System.out.println("b="+b.toString(ring));
//
//        MatrixS x = solveLAETropic(a,b,ring);
//        System.out.println("x=" + x);
//        VectorS x1 = solveLAITropic(a, b, ring);
//        System.out.println("x<="+x1);
//        System.out.println("--------------------");
//        MatrixD x2 = solveLASTropicGeneral(a, b, ring);
//        System.out.println(""+x2.toString(ring));

//        Ring ring = new Ring("R64MaxPlus[x]");
//        //Element a111=ring.numberONE;
//        long[][] aa = new long[][]{{1,1,-1},{1,1,-2}, {1,1,-3}};
//        long[] bb = new long[]{3,3,3};
//        Element zero = new NumberR64MaxPlus(NumberR64.ZERO);
//        MatrixD a = new MatrixD(aa,ring,zero);
//        VectorS b = new VectorS(bb,ring);
//        System.out.println(""+a.toString(ring));
//        System.out.println("b="+b.toString(ring));
//
//        MatrixD x = solveLASTropic(a,b,ring);
//        System.out.println("x=" + x);
//        MatrixD x1 = inequality(a, b, ring);
//        System.out.println("x<="+x1);
//        System.out.println("--------------------");
//        MatrixD x2 = solveLASTropicGeneral(a, b, ring);
//        System.out.println(""+x2.toString(ring));

//        //Tr
//        Ring ring = new Ring("R64MinMult[x]");
//         Element[][] aa11 = new Element[][]{{ring.numberZERO,new NumberR64MinMult(new NumberR64(2)),new NumberR64MinMult(new NumberR64(1)),new NumberR64MinMult(new NumberR64(0.8))},
//            {new NumberR64MinMult(new NumberR64(0.5)),ring.numberZERO,ring.numberZERO,new NumberR64MinMult(new NumberR64(0.4))},
//            {ring.numberZERO,new NumberR64MinMult(new NumberR64(3.0)),ring.numberZERO,new NumberR64MinMult(new NumberR64(1))},
//            {new NumberR64MinMult(new NumberR64(2)),ring.numberZERO,new NumberR64MinMult(new NumberR64(1)),ring.numberZERO}};
//         MatrixD m1 = new MatrixD(aa11);
//         System.out.println(""+m1);
//         System.out.println("tr A="+m1.track(ring).toString(ring));
//         System.out.println("Tr A="+m1.Tr(ring).toString(ring));

//        A+,Amult,A*
        //Ring ring = new Ring("R64MaxPlus[x]");
//        NumberR64MaxPlus z = new NumberR64MaxPlus(new NumberR64(1));
//        NumberR64MaxPlus z1 = new NumberR64MaxPlus(new NumberR64(9));
//        System.out.println("a+b="+z.add(z1, ring).toString(ring));
//        System.out.println("a*b="+z.multiply(z1, ring).toString(ring));
//        NumberR64MaxPlus z2 = new NumberR64MaxPlus(new NumberR64(9));
////        NumberR64MaxPlus z2 = (NumberR64MaxPlus) NumberR64MaxPlus.ZERO;
//        Element z3 = Element.NEGATIVE_INFINITY;
//        System.out.println("a+b="+z2.add(z3, ring).toString(ring));
//        System.out.println("a*b="+z2.multiply(z3, ring).toString(ring));
//        Element z13 = Element.POSITIVE_INFINITY;
//        NumberR64MaxPlus z12 = new NumberR64MaxPlus(new NumberR64(9));
//        System.out.println("a+b="+z13.add(z12, ring).toString(ring));
//        System.out.println("a*b="+z13.multiply(z12, ring).toString(ring));
//        System.out.println(""+z3.add(z13, ring).toString(ring));
//        System.out.println(""+z3.multiply(z13, ring).toString(ring));
//        System.out.println(""+z3.subtract(z13, ring).toString(ring));
//        System.out.println(""+z3.divide(z13, ring).toString(ring));
//        ring = new Ring("RMaxPlus[x]");
//        NumberRMaxPlus z4 = new NumberRMaxPlus(new NumberR(1));
//        NumberRMaxPlus z5 = new NumberRMaxPlus(new NumberR(9));
//        System.out.println("a+b="+z4.add(z5, ring));
//        System.out.println("a*b="+z4.multiply(z5, ring));
//        Element z6 = Element.NEGATIVE_INFINITY;
//        NumberRMaxPlus z7 = new NumberRMaxPlus(new NumberR(9));
//        System.out.println("a+b="+z6.add(z7, ring));
//        System.out.println("a*b="+z6.multiply(z7, ring));
//        Element z14 = Element.POSITIVE_INFINITY;
//        NumberRMaxPlus z15 = new NumberRMaxPlus(new NumberR(9));
//        System.out.println("a+b="+z14.add(z15, ring).toString(ring));
//        System.out.println("a*b="+z14.multiply(z15, ring).toString(ring));
//        ring = new Ring("ZMaxPlus[x]");
//        NumberZMaxPlus z8 = new NumberZMaxPlus(new NumberZ(1));
//        NumberZMaxPlus z9 = new NumberZMaxPlus(new NumberZ(9));
//        System.out.println("a+b="+z8.add(z9, ring));
//        System.out.println("a*b="+z8.multiply(z9, ring));
//        Element z10 = Element.NEGATIVE_INFINITY;
//        NumberZMaxPlus z11 = new NumberZMaxPlus(new NumberZ(9));
//        System.out.println("a+b="+z10.add(z11, ring).toString(ring));
//        System.out.println("a*b="+z10.multiply(z11, ring).toString(ring));
//        Element z16 = Element.POSITIVE_INFINITY;
//        NumberZMaxPlus z17 = new NumberZMaxPlus(new NumberZ(9));
//        System.out.println("a+b="+z16.add(z17, ring).toString(ring));
//        System.out.println("a*b="+z16.multiply(z17, ring).toString(ring));
//        System.out.println(""+z17.add(Element.NAN, ring).toString(ring));

        //Element a111=ring.numberONE;
        // long[][] aa = new long[][]{{1, 2}, {3, 4}};
//        Ring ring = new Ring("ZMinPlus[x]");
//        Element[] inter1 = new Element[]{Element.NEGATIVE_INFINITY,new NumberR(2)};
//        Boolean[] border1 = new Boolean[]{true,false};
//        SubsetR in1 = new SubsetR(inter1,border1);
//        System.out.println(""+in1.toString(ring));
//        Element[][] ee = new Element[][]{{in1},{new NumberR(1)}};
//        MatrixD d = new MatrixD(ee);
//        System.out.println(""+d.toString(ring));
//        Interval1 in = new Interval1(Element.NEGATIVE_INFINITY, new Element(-1));
//        System.out.println("in="+in.toString(ring));
//        Element[][] ee = new Element[][]{{in},{new NumberR64MaxPlus(new NumberR64(1))}};
//        MatrixD aaa = new MatrixD(ee);
//        System.out.println(""+aaa.toString(ring));

//        Ring ring = new Ring("R64[x]");
//        Element[][] aa1 = new Element[][]{{new NumberR64(1), new NumberR64(2), ring.numberZERO},
//            {ring.numberZERO, new NumberR64(1), new NumberR64(3)},
//            {ring.numberZERO, ring.numberZERO, new NumberR64(1)}};
//        MatrixD a = new MatrixD(aa1);
//        System.out.println("" + a.toString(ring));
//        System.out.println("track="+ a.track(ring));
//        System.out.println("Tr(A)=" + a.Tr(ring));
//        System.out.println("Aplus:");
////        MatrixD a1 = new MatrixD((new MatrixS(a)).inverse(ring));
//        //MatrixD a1 = a.closure(ring);
////        System.out.println("" + a1.toString(ring));

        //MinPlus---------------------------------------------------------------
//        Ring ring = new Ring("ZMinPlus[x]");
//        Element[][] aa1 = new Element[][]{{new NumberZMinPlus(NumberZ.ZERO), new NumberZMinPlus(new NumberZ(-2)), ring.numberZERO, ring.numberZERO},
//            {ring.numberZERO, new NumberZMinPlus(NumberZ.ZERO), new NumberZMinPlus(new NumberZ(3)), new NumberZMinPlus(new NumberZ(-1))},
//            {new NumberZMinPlus(new NumberZ(-1)), ring.numberZERO, new NumberZMinPlus(NumberZ.ZERO), new NumberZMinPlus(new NumberZ(-4))},
//            {new NumberZMinPlus(new NumberZ(2)), ring.numberZERO, ring.numberZERO, new NumberZMinPlus(NumberZ.ZERO)}};
////        Element[][] aa1 = new Element[][]{{new NumberR64MinMult(NumberR64.ZERO), new NumberR64MinMult(new NumberR64(2)), ring.numberZERO, ring.numberZERO},
////            {ring.numberZERO, new NumberR64MinMult(NumberR64.ZERO), new NumberR64MinMult(new NumberR64(3)), new NumberR64MinMult(new NumberR64(1))},
////            {new NumberR64MinMult(new NumberR64(1)), ring.numberZERO, new NumberR64MinMult(NumberR64.ZERO), new NumberR64MinMult(new NumberR64(4))},
////            {new NumberR64MinMult(new NumberR64(2)), ring.numberZERO, ring.numberZERO, new NumberR64MinMult(NumberR64.ZERO)}};
////        Element zero = ring.numberZERO;
////        System.out.println(""+zero.toString(ring));
////        MatrixD a = new MatrixD(aa1,ring,zero);
//        MatrixD a = new MatrixD(aa1);
//        MatrixD b1 = new MatrixD(aa1);
////        NumberR64MinPlus a1=new NumberR64MinPlus(new NumberR64(1));
////        NumberR64MinPlus a2=new NumberR64MinPlus(new NumberR64(2));
////        System.out.println(""+a1.compareTo(a2,2, ring));
//        System.out.println("" + a.toString(ring));
//        System.out.println("track="+ a.track(ring));
//        System.out.println("Tr(A)=" + a.Tr(ring));
//        System.out.println("Aplus:");
//        MatrixS a1 = new MatrixS(aa1,ring).closure(ring);
//        System.out.println("" + a1.toString(ring));
//        System.out.println("--------------------");
//        MatrixD a2 = Az1(a,ring);
//        System.out.println(""+a2.toString(ring));
//        System.out.println("--------------------");
//        MatrixD a3 = Az(b1,ring);
//        System.out.println(""+a3.toString(ring));
//        System.out.println("Amult:");
//        MatrixD a2 = Amult(a, ring);
//        System.out.println("" + a2.toString(ring));
//        System.out.println("A*:");
//        MatrixD a3 = Astar(a, ring);
//        System.out.println("" + a3.toString(ring));
//        //Ax=x
//        VectorS[] a4 = BellmanEquation(a, ring);
//        System.out.println("Ax=x");
//        for(int k=0; k<a4.length; k++){
//            System.out.println(""+a4[k].toString(ring));
//        }
//////          Ax+b=x
//         Element[][] bb1 = new Element[][]{{ring.numberZERO},{ring.numberZERO},{ring.numberZERO},{ring.numberZERO}};
////         //Element[][] bb1 = new Element[][]{{new NumberR64MinMult(new NumberR64(1))},{ring.numberZERO},{ring.numberZERO},{ring.numberZERO}};
//         MatrixD b = new MatrixD(bb1);
//         VectorS[] a5 = BellmanEquation(a, b, ring);
//         System.out.println("Ax+b=x");
//         for(int k=0; k<a5.length; k++){
//            System.out.println(""+a5[k].toString(ring));
//        }
//         MatrixS a6 = BellmanInequality(new MatrixS(a,ring), ring);
//         System.out.println("Ax<=x");
//         System.out.println(""+a6.toString(ring));
//         MatrixS a7 = BellmanInequality(new MatrixS(a,ring), b, ring);
//         System.out.println("Ax+b<=x");
//         System.out.println(""+a7);
        //MaxPlus-------------------------------------------------------------------
         Ring ring = new Ring("R64MaxPlus[x]");
         NumberR64MaxPlus a1 = new NumberR64MaxPlus(new NumberR64(3));
         Element a2 = a1.toNumber(ring.RMinPlus, ring);
//         a1.toNewRing(ring.R64MinPlus, ring);
         System.out.println("a2="+a2.toString(ring));
//         Element[][] aa1 = new Element[][]{{new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(-2)), ring.numberZERO, ring.numberZERO},
//            {ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(3)), new NumberR64MaxPlus(new NumberR64(-1))},
//            {new NumberR64MaxPlus(new NumberR64(-1)), ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(-4))},
//            {new NumberR64MaxPlus(new NumberR64(2)), ring.numberZERO, ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO)}};
//         MatrixD A = new MatrixD(aa1);
//         System.out.println("A="+A.toString(ring));
//         System.out.println("");
//         Element[][] bb1 = new Element[][]{{ring.numberZERO},{ring.numberZERO},{ring.numberZERO},{ring.numberZERO}};
//         MatrixD b=new MatrixD(bb1);
        
 
//            int n=4;
//        long[][] aa = new long[n][n];
//        long[][] aa2 = new long[n][1];
//     
//        MatrixD a;
//        MatrixD b;
//        
//            for(int i=0; i<n; i++){
//                for(int j=0; j<n; j++){
//                    double a1=Math.random();
//                    aa[i][j]=Math.round(-100*a1);
//                }
//                aa[i][i]=0;
//                double a2=Math.random();
//                aa2[i][0]=Math.round(100*a2);
//            }
//            a = new MatrixD(aa,ring);
//            b = new MatrixD(aa2,ring);
////            for (int i = 0; i < b.M.length; i++) {
////            b.M[i][0] = ring.numberZERO;
////        }
//        long t1 = System.currentTimeMillis();
//        MatrixS c = (new MatrixS(a, ring)).closure(ring);
//        long t2 = System.currentTimeMillis();
//        System.out.println("obichniy  " + c.toString(ring));
//        System.out.println("ob t=" + (t2 - t1));
////            Element c5 = a.Tr(ring);
////            System.out.println("Tr(a)="+c5.toString(ring));
////         System.out.println("--------------");
////         long t3 =System.currentTimeMillis();
////         MatrixD c2 = closureEsc(a,ring);
////         long t4 = System.currentTimeMillis();
//////         System.out.println("eskalator  "+c2.toString(ring));
////            System.out.println("esc t="+(t4-t3));
//        System.out.println("--------------");
////         long t5 =System.currentTimeMillis();
////         MatrixD c3 = closureBlok(a,ring);
////         long t6 = System.currentTimeMillis();
////         System.out.println("blok  "+c3.toString(ring));
////            System.out.println("blok t="+(t6-t5));
//            System.out.println("--------------");
////         long t7 =System.currentTimeMillis();
////         MatrixD c4 = closureIsc(a,ring);
////         long t8 = System.currentTimeMillis();
//////         System.out.println("iskl  "+c4.toString(ring));
////            System.out.println("iskl t="+(t8-t7));
//            
////        System.out.println("" + a.toString(ring));
////        System.out.println("track="+ a.track(ring));
//        System.out.println("Tr(A)=" + a.Tr(ring));
//        System.out.println("nerav:");
//        long t9 =System.currentTimeMillis();
//        MatrixS a1 = BellmanInequality(new MatrixS(a,ring), b,ring);
//        long t10 =System.currentTimeMillis();
//        System.out.println("" + a1.toString(ring));
//        System.out.println("nerav t="+(t10-t9));
//        System.out.println("--------------------");
//        MatrixD a2 = Az1(a,ring);
//        System.out.println(""+a2.toString(ring));
//        System.out.println("--------------------");
//        MatrixD a3 = Az(b,ring);
//        System.out.println(""+a3.toString(ring));
//        System.out.println("Amult:");
//        MatrixD a2 = Amult(a, ring);
//        System.out.println("" + a2.toString(ring));
//        System.out.println("A*:");
//        MatrixD a3 = Astar(a, ring);
//        System.out.println("" + a3.toString(ring));

        //Ax=x
//        VectorS[] a4 = BellmanEquation(a, ring);
//        System.out.println("Ax=x");
//        for(int k=0; k<a4.length; k++){
//            System.out.println(""+a4[k].toString(ring));
//        }
        //Ax+b=x
//         Element[][] bb1 = new Element[][]{{new NumberR64MaxPlus(new NumberR64(1))},{new NumberR64MaxPlus(new NumberR64(1))},{new NumberR64MaxPlus(new NumberR64(2))},{new NumberR64MaxPlus(new NumberR64(1))}};
//         MatrixD b = new MatrixD(bb1);
//         VectorS[] a5 = BellmanEquation(a, b, ring);
//         System.out.println("Ax+b=x");
//         for(int k=0; k<a5.length; k++){
//            System.out.println(""+a5[k].toString(ring));
//         }
//         MatrixS a6 = BellmanInequality(new MatrixS(a,ring), ring);
//         System.out.println("Ax<=x");
//         System.out.println(""+a6);
//         MatrixS a7 = BellmanInequality(new MatrixS(a,ring), b, ring);
//         System.out.println("Ax+b<=x");
//         System.out.println(""+a7);

        //MaxMult--------------------------------------------------------------
//         Ring ring = new Ring("R64MaxMult[x]");
//        Element[][] aa1 = new Element[][]{{ring.numberZERO, new NumberR64MaxMult(new NumberR64(1)), new NumberR64MaxMult(new NumberR64(1.8)), new NumberR64MaxMult(new NumberR64(3.2))},
//            {new NumberR64MaxMult(new NumberR64(0.8)), ring.numberZERO, new NumberR64MaxMult(new NumberR64(2)), new NumberR64MaxMult(new NumberR64(3))},
//            {new NumberR64MaxMult(new NumberR64(0.5)), new NumberR64MaxMult(new NumberR64(0.4)), ring.numberZERO, new NumberR64MaxMult(new NumberR64(1.5))},
//            {new NumberR64MaxMult(new NumberR64(0.2)), new NumberR64MaxMult(new NumberR64(0.3)), new NumberR64MaxMult(new NumberR64(0.5)), ring.numberZERO}};
//        MatrixD a = new MatrixD(aa1);
//        MatrixD b = new MatrixD(aa1);
//        System.out.println("" + a.toString(ring));
//        System.out.println("track="+ a.track(ring));
//        System.out.println("Tr(A)=" + a.Tr(ring));
//        System.out.println("Aplus:");
//        MatrixS a1 = (new MatrixS(aa1,ring)).closure(ring);
//        System.out.println("" + a1.toString(ring));
//        System.out.println("--------------------");
//        MatrixD a2 = Az1(a,ring);
//        System.out.println(""+a2.toString(ring));
//        System.out.println("--------------------");
//        MatrixD a3 = Az(b,ring);
//        System.out.println(""+a3.toString(ring));
//        System.out.println("Amult:");
//        MatrixD a2 = Amult(a, ring);
//        System.out.println("" + a2.toString(ring));
//        System.out.println("A*:");
//        MatrixD a3 = Astar(a, ring);
//        System.out.println("" + a3.toString(ring));
//        //Ax=x
//        VectorS[]a4 = BellmanEquation(a, ring);
//        System.out.println("Ax=x");
//        for(int k=0; k<a4.length; k++){
//            System.out.println(""+a4[k].toString(ring));
//        }
//        MatrixS a6 = BellmanInequality(new MatrixS(a,ring), ring);
//         System.out.println("Ax<=x");
//         System.out.println(""+a6.toString(ring));

        //MinMult--------------------------------------------------------------
//         Ring ring = new Ring("R64MinMult[x]");
//        Element[][] aa1 = new Element[][]{{ring.numberZERO, new NumberR64MinMult(new NumberR64(2)), new NumberR64MinMult(new NumberR64(1)), new NumberR64MinMult(new NumberR64(0.8))},
//            {new NumberR64MinMult(new NumberR64(0.5)), ring.numberZERO, ring.numberZERO, new NumberR64MinMult(new NumberR64(0.4))},
//            {ring.numberZERO, new NumberR64MinMult(new NumberR64(3)), ring.numberZERO, new NumberR64MinMult(new NumberR64(1))},
//            {new NumberR64MinMult(new NumberR64(2)), ring.numberZERO, new NumberR64MinMult(new NumberR64(1)), ring.numberZERO}};
//        MatrixD a = new MatrixD(aa1);
//        MatrixD b = new MatrixD(aa1);
//        System.out.println("" + a.toString(ring));
//        System.out.println("track="+ a.track(ring).toString(ring));
//        System.out.println("Tr(A)=" + a.Tr(ring));
//        System.out.println("Aplus:");
//        MatrixS a1 = (new MatrixS(aa1,ring)).closure(ring);
//        System.out.println("" + a1.toString(ring));
//        System.out.println("--------------------");
//        MatrixD a2 = Az1(a,ring);
//        System.out.println(""+a2.toString(ring));
//        System.out.println("--------------------");
//        MatrixD a3 = Az(b,ring);
//        System.out.println(""+a3.toString(ring));
//        System.out.println("Amult:");
//        MatrixD a2 = Amult(a, ring);
//        System.out.println("" + a2.toString(ring));
//        System.out.println("A*:");
//        MatrixD a3 = Astar(a, ring);
//        System.out.println("" + a3.toString(ring));
//        //Ax=x
//        VectorS[]a4 = BellmanEquation(a, ring);
//        System.out.println("Ax=x");
//        for(int k=0; k<a4.length; k++){
//            System.out.println(""+a4[k].toString(ring));
//        }
//        MatrixS a6 = BellmanInequality(new MatrixS(a,ring), ring);
//         System.out.println("Ax<=x");
//         System.out.println(""+a6);

        //кратч путь---------------------------------------------------------------
//        Ring ring = new Ring("R64MinPlus[x]");
//        Element[][] aa1 = new Element[][]{{new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(7)),new NumberR64MinPlus(new NumberR64(9)), ring.numberZERO, ring.numberZERO,new NumberR64MinPlus(new NumberR64(14))},
//            {new NumberR64MinPlus(new NumberR64(7)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(10)), new NumberR64MinPlus(new NumberR64(15)), ring.numberZERO, ring.numberZERO},
//            {new NumberR64MinPlus(new NumberR64(9)), new NumberR64MinPlus(new NumberR64(10)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(11)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(2))},
//            {ring.numberZERO, new NumberR64MinPlus(new NumberR64(15)), new NumberR64MinPlus(new NumberR64(11)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(6)), ring.numberZERO},
//            {ring.numberZERO, ring.numberZERO, ring.numberZERO, new NumberR64MinPlus(new NumberR64(6)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(9))},
//            {new NumberR64MinPlus(new NumberR64(14)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(2)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(9)), new NumberR64MinPlus(NumberR64.ZERO)}};
//        MatrixD a = new MatrixD(aa1);
//        System.out.println("" + a.toString(ring));
//        System.out.println("track="+ a.track(ring));
//        System.out.println("Tr(A)=" + a.Tr(ring));
//        System.out.println("Aplus:");
//        MatrixD a1 = a.closure(ring);
//        System.out.println("" + a1.toString(ring));
//        System.out.println("Amult:");
//        MatrixD a2 = Amult(a, ring);
//        System.out.println("" + a2.toString(ring));
//        System.out.println("A*:");
//        MatrixD a3 = Astar(a, ring);
//        System.out.println("" + a3.toString(ring));
//        //Ax=x
//        MatrixD a4 = BellmanEquation(a, ring);
//        System.out.println("Ax=x");
//        System.out.println(""+a4.toString());
//
//        MatrixD a5 = new MatrixD(new Element[][]{{NumberR64MinPlus.ONE},{ring.numberZERO},{ring.numberZERO},{ring.numberZERO},{ring.numberZERO},{ring.numberZERO}});
//        System.out.println("v="+a5.toString(ring));
//        System.out.println("--------------");
//        System.out.println(""+a4.multCU(a5, ring).toString(ring));
    }
}
