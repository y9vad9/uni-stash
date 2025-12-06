package com.mathpar.matrix.LDU;

import java.io.FileNotFoundException;
import java.util.Random;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;

/**
 *
 * @author ridkeim
 */
public class LDUP {
    boolean end;
    int one = 1;
    int n;
    int startNumber;
    int endNumber;
    int div;
    int zeroBlockSize;
    public Element[] d = new Element[0];
    public MatrixS L;
    public MatrixS D;
    public MatrixS U;
    public MatrixS M;
    public MatrixS W;
    public MatrixS A;
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

    public LDUP(MatrixS T) {
        this(T, 0, T.size, 0, true);
    }
    
    public LDUP(MatrixS T, int start, int end, int zeroSize, boolean fin) {
        A = T;
        this.end = fin;
        startNumber = start;
        endNumber = end;
        zeroBlockSize = zeroSize;
        n = A.size;
        L = new MatrixS();
        D = new MatrixS();
        U = new MatrixS();
        M = new MatrixS();
        W = new MatrixS();
    };
    
    public void getLDU(Element a) {
        if (!A.isZero(ring)) {
            switch (n) {
                case 1:
                    Element a_n = A.getElement(0, 0, ring);
                    L = new MatrixS(a_n);
                    D = new MatrixS(new Fraction(a, a_n.multiply(a, ring))).cancel(ring);
                    U = new MatrixS(a_n);
                    M = new MatrixS(a);
                    W = new MatrixS(a);
                    d = new Element[] {a_n};
                    break;
                default:
                    int l = n - (n / 2);
                    int new_zero = 0;
                    if (zeroBlockSize >= l) {
                        l = zeroBlockSize;
                    }
                    MatrixS[] Block = split(A, l);
                    if (Block[0].isZero(ring)) {
                        l = n - l;
                        new_zero = Block[0].size - l;
                        if (new_zero < 0) {
                            new_zero = 0;
                        }
                        zeroBlockSize = 0;
                        if (Block[2].isZero(ring) && Block[1].isZero(ring)) {
                            Er = changeBlocks(l,n);
                            Ec = changeBlocks(l,n);
                        } else {
                            if (!Block[2].isZero(ring)) {
                                Er = changeBlocks(l,n);
                                if (checkBlockC(Block[2])) {
                                    Ec = changeBlocks(Block[0].size - l, Block[0].size);
                                }
                            } else {
                                Ec = changeBlocks(l,n);
                                if (checkBlockB(Block[1],l)) {
                                    Er = changeBlocks(Block[0].size - l, Block[0].size);
                                }
                            }
                        }
                    }
                    if ((Ec.length != 0) || (Er.length != 0)) {
                        A = A.permutationOfColumns(Ec);
                        A = A.permutationOfRows(Er);
                        Block = split(A, l);             
                    }


                    LDUP first = new LDUP(Block[0], startNumber, startNumber + Block[0].size, zeroBlockSize, false);                    
                    first.getLDU(a);
                    one *= first.one;
                    if (first.zeroBlockSize != 0) {
                        new_zero = 0;
                    }

                    int second_start = first.L.size;
                    
                    A = A.permutationOfColumns(first.Ec).permutationOfRows(first.Er);
                    if(first.zeroBlockSize!=0){
                        Block = split(A, second_start);
                    }else{
                        Block[1] = Block[1].permutationOfRows(first.Er);
                        Block[2] = Block[2].permutationOfColumns(first.Ec);
                    }
                    MatrixS Ukssn = first.M.multiplyDivRecursive(Block[1],a, ring);
                            
                    MatrixS Lkssn = Block[2].multiplyDivRecursive(first.W,a, ring);
                            

                    Element as = first.d[first.d.length - 1];

                    MatrixS DU = first.D.multiplyRecursive(Ukssn, ring);
                    MatrixS Ldu = Lkssn.multiply(DU, ring).multiplyByNumber(as, ring);
//                    if(Ldu.size == 4){
//                        System.out.println("LDU4="+Lkssn.multiply(DU, ring));                        
//                    }
                    Block[3] = Block[3].multiplyByNumber(as, ring);
                    Block[3] = Block[3].subtract(Ldu, ring)
                            .divideByNumber(a, ring);

                    if (Block[3].isZero(ring)) {
                        zeroBlockSize = Block[3].size;
                        L = first.L;
                        D = first.D;
                        U = first.U;
                        M = first.M;
                        W = first.W;
                        d = first.d;
                        Ec = MatrixS.multPermutations(Ec, first.Ec, n);
                        Er = MatrixS.multPermutations(Er, first.Er, n);
                        endNumber = first.endNumber;


                    } else {                        
                        LDUP second = new LDUP(Block[3], first.endNumber, first.endNumber + Block[3].size, new_zero, false);
                        second.getLDU(as);
                        one*=second.one;
                        int tmp_size = second.L.size;
                        Lkssn = Lkssn.permutationOfRows(second.Er);
                        if ((!Lkssn.isZero(ring))&&(Lkssn.size != tmp_size)) {
                            int lastNumb = Lkssn.colNumb - 1;
                            Lkssn = Lkssn.getSubMatrix(0, tmp_size - 1, 0, (lastNumb < 0) ? 0 : lastNumb);
                        }
                        Ukssn = Ukssn.permutationOfColumns(second.Ec);
                        DU = DU.permutationOfColumns(second.Ec);
                        if ((!Ukssn.isZero(ring))&&(Ukssn.colNumb >= tmp_size)) {
                            Ukssn = Ukssn.getSubMatrix(0, Ukssn.size - 1, 0, tmp_size - 1);
                            DU = DU.getSubMatrix(0, DU.size - 1, 0, tmp_size - 1);
                        }



                        d = new Element[first.d.length + second.d.length];
                        System.arraycopy(first.d, 0, d, 0, first.d.length);
                        System.arraycopy(second.d, 0, d, first.d.length, second.d.length);
                        MatrixS Mkssn_0 = second.M.multiplyRecursive(Lkssn, ring);
                        MatrixS Wkssn_0 = first.W.multiplyRecursive(DU, ring);
                        MatrixS DM = first.D.multiplyRecursive(first.M, ring);
                        MatrixS Mkssn = Mkssn_0.multiplyDivRecursive(DM,a.multiply(ring.numberMINUS_ONE, ring), ring);
                        MatrixS Wkssn = Wkssn_0.multiplyDivRecursive(second.W,a.multiply(ring.numberMINUS_ONE, ring), ring);

                        //result
                        int[] secEr = getE(second.Er, second_start);
                        int[] secEc = getE(second.Ec, second_start);
                        A = A.permutationOfRows(secEr).permutationOfColumns(secEc);
                        int[] ssR2 = MatrixS.multPermutations(first.Er, secEr, n);
                        int[] ssC2 = MatrixS.multPermutations(first.Ec, secEc, n);
                        Er = MatrixS.multPermutations(Er, ssR2, n);
                        Ec = MatrixS.multPermutations(Ec, ssC2, n);
                        D = getD(d, a, d.length);
                        L = join(
                                first.L, MatrixS.zeroMatrix(first.L.size),
                                Lkssn, second.L);
                        U = join(
                                first.U, Ukssn,
                                MatrixS.zeroMatrix(second.U.size), second.U);
                        M = join(
                                first.M, MatrixS.zeroMatrix(first.M.size),
                                Mkssn, second.M);

                        W = join(
                                first.W, Wkssn,
                                MatrixS.zeroMatrix(second.W.size), second.W);

                        zeroBlockSize = second.zeroBlockSize;
                        endNumber -= zeroBlockSize;
                    }

            }
            if (end) {
                if ((endNumber != n)) {    
                    System.out.println("with End");
                    MatrixS B = A.getSubMatrix(0, endNumber - 1, endNumber, A.colNumb - 1);
                    MatrixS C = A.getSubMatrix(endNumber, n - 1, 0, endNumber - 1);
                    MatrixS Ut = M.multiplyRecursive(B, ring);
                    MatrixS Lt = C.multiplyRecursive(W, ring);
                    U = appendLeftRight(U,Ut,n);
                    L = appendUpDown(L,Lt,n);
                    W = appendLeftRight(W, MatrixS.zeroMatrix(W.size),n);
                    M = appendUpDown(M, MatrixS.zeroMatrix(),n);
                    D = getD(d, a, n);
                }
            }
        } else {
            endNumber = startNumber;
            if (end) {
                L = MatrixS.zeroMatrix(n);
                D = MatrixS.zeroMatrix(n);
                U = MatrixS.zeroMatrix(n);
                M = MatrixS.zeroMatrix(n);
                W = MatrixS.zeroMatrix(n);
            }
        }
    }

    public static MatrixS getD(Element[] d, Element ak, int size) {
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

    private MatrixS appendUpDown(MatrixS Up,MatrixS Down, int size) {
        Element[][] MM = new Element[size][];
        int[][] Col = new int[size][];
        int l = Up.M.length;
        int new_size = l + Down.M.length;
        for (int i = 0; i < new_size; i++) {
            Element[] Ms;
            int[] Cs;
            if (i < Up.size) {
                Ms = Up.M[i];
                Cs = Up.col[i];
            } else {
                Ms = Down.M[i - l];
                Cs = Down.col[i - l];
            }
            MM[i] = new Element[Ms.length];
            Col[i] = new int[Cs.length];
            System.arraycopy(Ms, 0, MM[i], 0, Ms.length);
            System.arraycopy(Cs, 0, Col[i], 0, Cs.length);
        }
        for (int i = new_size; i < size; i++) {
            MM[i] = new Element[0];
            Col[i] = new int[0];
        }
        return new MatrixS(size, size, MM, Col);

    }

    private MatrixS appendLeftRight(MatrixS left, MatrixS right, int size) {
        Element[][] MM = new Element[size][];
        int[][] Col = new int[size][];
        for (int i = 0; i < left.M.length; i++) {
            MM[i] = new Element[left.M[i].length + right.M[i].length];
            System.arraycopy(left.M[i], 0, MM[i], 0, left.M[i].length);
            System.arraycopy(right.M[i], 0, MM[i], left.M[i].length, right.M[i].length);
            Col[i] = new int[left.col[i].length + right.col[i].length];
            System.arraycopy(left.col[i], 0, Col[i], 0, left.col[i].length);
            System.arraycopy(right.col[i], 0, Col[i], left.col[i].length, right.col[i].length);
            for (int j = left.col[i].length; j < Col[i].length; j++) {
                Col[i][j] += left.M.length;
            }
        }
        for (int i = left.M.length; i < size; i++) {
            MM[i] = new Element[0];
            Col[i] = new int[0];
        }
        return new MatrixS(size, left.M.length + right.colNumb, MM, Col);

    }

    public static MatrixS[] LDU(MatrixS S) {
        LDUP a = new LDUP(S, 0, S.size, 0, true);
        a.getLDU(ring.numberONE);
        int[] ErT = MatrixS.transposePermutation(a.Er);
        int[] EcT = MatrixS.transposePermutation(a.Ec);
//        System.out.println(a.L+""+a.D+""+a.U);
        return new MatrixS[] {a.L.permutationOfRows(ErT).permutationOfColumns(ErT),
                              a.D.permutationOfRows(ErT).permutationOfColumns(EcT),
                              a.U.permutationOfRows(EcT).permutationOfColumns(EcT)};
    }
    
    public static MatrixS adjoint(MatrixS S){
        long time = System.currentTimeMillis();
        if(S.isZero(ring)){
            return MatrixS.zeroMatrix(S.size);
        }
        LDUP a = new LDUP(S, 0, S.size, 0, true);
        a.getLDU(ring.numberONE);
        if(S.size!=a.d.length){
            System.out.println("Не полный ранг");
            return MatrixS.zeroMatrix(S.size);
        }
        int[] ErT = MatrixS.transposePermutation(a.Er);
        int[] EcT = MatrixS.transposePermutation(a.Ec);
        System.out.println("Er.len="+a.Er.length);
        System.out.println("Er.len="+a.Ec.length);
//        System.out.println("one="+a.one);
//        System.out.println("an="+a.d[a.d.length-1]);
        time=System.currentTimeMillis()-time;
        System.out.println("time.Adj="+time);
        Element a_n = 
//                a.d[a.d.length-1];
                (a.one==-1)?a.d[a.d.length-1].multiply(ring.numberMINUS_ONE, ring):a.d[a.d.length-1];
//        System.out.println("a_n="+a_n);
        MatrixS res = a.W.multiply(getD( a.d , ring.numberONE, S.size), ring)
                .multiply(a.M, ring).multiplyByNumber(a_n, ring);
        res = res.permutationOfRows(EcT).permutationOfColumns(ErT);
        return res;
    }

    
    int[] changeBlocks(int sizeLastBlock, int sizeFull) {
        int sizeFirstBlock = sizeFull-sizeLastBlock;
        one *= (sizeFirstBlock*sizeLastBlock%2==1)?-1:1;            
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

    MatrixS join(MatrixS A, MatrixS B, MatrixS C, MatrixS D) {
        return join(new MatrixS[]{A,B,C,D});
//        int new_size = A.size + D.size;
//        int size1 = A.size;
//        Element[][] MM = new Element[new_size][0];
//        int[][] col = new int[new_size][0];
//        int size11 = Math.min(A.M.length, B.M.length);
//        for (int i = 0; i < size11; i++) {
//            int t = A.M[i].length + B.M[i].length;
//            MM[i] = new Element[t];
//            col[i] = new int[t];
//            System.arraycopy(A.M[i], 0, MM[i], 0, A.M[i].length);
//            System.arraycopy(B.M[i], 0, MM[i], A.M[i].length, B.M[i].length);
//            System.arraycopy(A.col[i], 0, col[i], 0, A.col[i].length);
//            System.arraycopy(B.col[i], 0, col[i], A.col[i].length, B.col[i].length);
//            for (int j = A.col[i].length; j < col[i].length; j++) {
//                col[i][j] += size1;
//            }
//        }
//        for (int i = size11; i < A.size; i++) {
//            int t = A.M[i].length;
//            MM[i] = new Element[t];
//            col[i] = new int[t];
//            System.arraycopy(A.M[i], 0, MM[i], 0, A.M[i].length);
//            System.arraycopy(A.col[i], 0, col[i], 0, A.col[i].length);
//        }
//        int size22 = Math.min(C.M.length, D.M.length);
//
//        for (int i = 0; i < size22; i++) {
//            int t = C.M[i].length + D.M[i].length;
//            int sizes = i + size1;
//            MM[sizes] = new Element[t];
//            col[sizes] = new int[t];
//            System.arraycopy(C.M[i], 0, MM[sizes], 0, C.M[i].length);
//            System.arraycopy(D.M[i], 0, MM[sizes], C.M[i].length, D.M[i].length);
//            System.arraycopy(C.col[i], 0, col[sizes], 0, C.col[i].length);
//            System.arraycopy(D.col[i], 0, col[sizes], C.col[i].length, D.col[i].length);
//            for (int j = C.col[i].length; j < col[sizes].length; j++) {
//                col[sizes][j] += size1;
//            }
//        }
//        for (int i = size22; i < D.size; i++) {
//            int t = D.M[i].length;
//            int sizes = i + size1;
//            MM[sizes] = new Element[t];
//            col[sizes] = new int[t];
//            System.arraycopy(D.M[i], 0, MM[sizes], 0, D.M[i].length);
//            System.arraycopy(D.col[i], 0, col[sizes], 0, D.col[i].length);
//            for (int j = 0; j < col[sizes].length; j++) {
//                col[sizes][j] += size1;
//            }
//        }
//        return new MatrixS(new_size, new_size, MM, col);
    }

    int[] getE(int[] E, int k) {
        int[] t = new int[E.length];
        for (int i = 0; i < E.length; i++) {
            t[i] = E[i] + k;
        }
        return t;
    }

    int[] joinP(int[] P1, int[] P2, int k) {
        int[] ErN = new int[P1.length + P2.length];
        System.arraycopy(P1, 0, ErN, 0, P1.length / 2);
        System.arraycopy(P1, P1.length / 2, ErN, ErN.length / 2, P1.length / 2);
        System.arraycopy(P2, 0, ErN, P1.length / 2, P2.length / 2);
        System.arraycopy(P2, P2.length / 2, ErN, ErN.length / 2 + P1.length / 2, P2.length / 2);
        for (int i = P1.length / 2; i < ErN.length / 2; i++) {
            ErN[i] += k;
            ErN[i + ErN.length / 2] += k;
        }
        return ErN;
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

    public static String toS(MatrixS T) {
        String a = "MatrixS T = new MatrixS(new int[][]{";
        String b = "}, Ring.ringZxyz);";
        Element[][] sArr = T.toSquaredArray(Ring.ringZxyz);
        for (int i = 0; i < sArr.length; i++) {
            String t = "\n{";
            for (int j = 0; j < sArr[i].length; j++) {
                if (j != (sArr[i].length - 1)) {
                    t += sArr[i][j].toString() + ",";
                } else {
                    t += sArr[i][j].toString();
                }

            }
            t += "}";
            if (i != (sArr.length - 1)) {
                t += ",";
            }
            a += t;
        }
        return a + b;
    }

    boolean checkBlockC(MatrixS Block) {
        boolean res = true;
        for (int i = 0; i < Block.col.length; i++) {
            try {
                if (Block.size > Block.col[i][0]) {
                    res = false;
                    return res;
                }
            } catch (Exception e) {
            }
        }
        return res;
    }

    boolean checkBlockB(MatrixS Block,int l) {
        boolean res = true;
        for (int i = 0; i < Math.min(Block.col.length,l); i++) {
            try {
                if (Block.col[i].length > 0) {
                    res = false;
                    return res;
                }
            } catch (Exception e) {
            }
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
            if ((T.M[i]  != null)&&(T.M[i].length != 0)) {
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
        int len = b[0].size;  
        int len2 = Math.max(b[2].M.length, b[3].M.length);
        int len1 = Math.max(b[0].M.length, b[1].M.length);
        int n = (len2 == 0) ? len1 : len + len2;

        int col2 = Math.max(b[1].colNumb, b[3].colNumb);
        int col1 = Math.max(b[0].colNumb, b[2].colNumb);
        int colNumb = (col2 == 0) ? col1 : len + col2;     
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
                for (int s = m; s < mk; s++)  c0[s] += len;
            }
            r[i] = r0;  c[i] = c0;
        }
        int ii = len;
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
                for (int s = m; s < mk; s++) c0[s] += len;
            }
            r[ii] = r0;
            c[ii++] = c0;
        }
        MatrixS res = new MatrixS(len1+len2, colNumb, r, c);
        return res;
    } 
         
    public static Element getDet(MatrixS T){
        LDUP a = new LDUP(T);
        Ring r = Ring.ringZxyz;
        a.getLDU(r.numberONE);
        if(a.d.length<T.size){
            return r.numberZERO;
        }
        return a.d[a.d.length-1];
    }
    public static void main(String[] args) throws FileNotFoundException {
        MatrixS a = new MatrixS(new int[][] {
           {0,0,12},
            {10,-8,-20},
             {22,32,-12}
 
        }, ring);
        Element as = new NumberZ("4");
        LDUP1 aa = new LDUP1(a, as);
        aa.getLdu(3, 3);
    }
}

