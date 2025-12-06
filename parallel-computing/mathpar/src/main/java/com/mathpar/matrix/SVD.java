package com.mathpar.matrix;
import com.mathpar.number.*;

import java.util.ArrayList;
import java.util.Random;

public class SVD {
    private final ArrayList<Integer> _leadingColumns;
    private final MatrixD _initialMatrix;
    private final Ring _ring;

    public SVD(MatrixD matrix, Ring ring) {
        _ring = ring;
        _initialMatrix = matrix;
        _leadingColumns = new ArrayList<>();
    }

    public static void main(String[] args) {
        Ring ring = new Ring("R64[]");
        for (int n=4; n<=64; n=n*2){
            double[][] m = generateMatrix(n);
            MatrixD A = new MatrixD(m, ring);
            System.out.print("n="+n+"   ");
            long t1= System.currentTimeMillis();
            MatrixD[] result = new SVD(A, ring).compute();
            long t2= System.currentTimeMillis()-t1;
            System.out.println(" time="+t2);
            MatrixD U = result[0];
            MatrixD D = result[1];
            MatrixD W = result[2];
            MatrixD B = U.multCU(D, ring).multCU(W, ring);
            MatrixD Check = B.subtract(A, ring);
            System.out.println("Check final SVD: maxAbs absolute value = " + Check.max(ring).value);
        }}

    public static double[][] generateMatrix(int N) {
        Random rand = new Random();
        double[][] res = new double[N][N];

        for(int i =0; i < N ; i++) {
            for (int j = 0; j < N; j++) {
                res[i][j] = rand.nextDouble() * 10.0;
            }
        }

        return res;
    }

    public static double[][] getPerfectMatrix() {
        return new double[][] {{1,2,1,1}, {1,1,2,1}, {1,1,1,2}, {1,2,1,1}};
    }

    public static double[][] getPerfectBDMatrix() {
        return new double[][] {{-2, 4.33, -0, -0}, {0, 0.5, 0, 0}, {0, -0, 1, -0}, {0, -0, -0, -1}};
    }

    public MatrixD[] compute() {
        if (_initialMatrix.rowNum() != _initialMatrix.colNum())
            return null;
        MatrixD[] result = biDiagonalize(_initialMatrix, _ring);
        //checkBidiagonalization(_initialMatrix, result, _ring, true);
        MatrixD d=result[1]; int n= d.M.length;
        Element[][] DD=new Element[2][n];
        for(int k=0; k<n-1;k++){DD[1][k]=d.M[k][k]; DD[0][k]=d.M[k][k+1];}
        DD[1][n-1]=d.M[n-1][n-1]; DD[0][n-1]=_ring.numberZERO;
        MatrixD dd=new MatrixD(DD);
        MatrixD[] diagonal = diagonalize(result[1], _ring);
        result[0] = result[0].transpose(_ring).multCU(diagonal[0].transpose(_ring), _ring);
        result[1] = diagonal[1];
        result[2] = diagonal[2].transpose(_ring).multCU(result[2].transpose(_ring), _ring);

        return result;
    }

    public MatrixD[] biDiagonalize(MatrixD A, Ring ring) {
        int nn=A.M.length; int nn1 = nn - 1;
        MatrixD Ai =A;
        MatrixD UU=MatrixD.ONE(nn, ring);
        MatrixD WW=MatrixD.ONE(nn, ring);
        int i = 0;
        while (true) {
            VectorS x = new VectorS( Ai.takeColumn(i + 1).V.clone());
            for (int k = 0; k < i; k++) {x.V[k] = ring.numberZERO;}
            Element norm_x = x.norm2(i, ring).sqrt(ring);
            if (! norm_x.isZero(ring)) {
                VectorS u = new VectorS(x.V.clone());
                u.V[i] = u.V[i].subtract(norm_x, ring);
                VectorS Ut = u.transpose(ring);
                Element XU = x.multiply(Ut, ring);
                Element mult = ring.numberONE.divide(XU, ring);
                VectorS Vec=(VectorS) u.multiply(Ai, ring);
                VectorS VecU=(VectorS) u.multiply(UU, ring);
                MatrixD UtUAi = (MatrixD) Ut.multiply( Vec,ring).multiply(mult, ring);
                Ai = Ai.subtract(UtUAi, ring);
                MatrixD UtU_U = (MatrixD) Ut.multiply( VecU,ring).multiply(mult, ring);
                UU = UU.subtract(UtU_U, ring);
            }
            if (i == nn1-1) {break;}
            VectorS x_r =new VectorS( Ai.takeRow(i + 1).V.clone());
            for (int k = 0; k < i + 1; k++) x_r.V[k] = ring.numberZERO;
            Element norm_x_r = x_r.norm2(i + 1, ring).sqrt(ring);
            if (! norm_x_r.isZero(ring)) {
                VectorS u_r = new VectorS(x_r.V.clone());
                u_r.V[i + 1] = u_r.V[i + 1].subtract(norm_x_r, ring);
                VectorS Ut_r = u_r.transpose(ring);
                Element XU_r = x_r.multiply(Ut_r, ring);
                Element mult_r = ring.numberONE.divide(XU_r, ring);
                VectorS VecT=(VectorS) Ai.multiply(Ut_r, ring);
                VectorS VecW = (VectorS) WW.multiply(Ut_r, ring);
                MatrixD AiUtU = (MatrixD)VecT.multiply( u_r,ring).multiply(mult_r, ring);
                Ai = Ai.subtract(AiUtU, ring);
                MatrixD W_UtU = (MatrixD) VecW.multiply(u_r,ring).multiply(mult_r, ring);
                WW = WW.subtract(W_UtU, ring);
            }
            i++;
        }
        return new MatrixD[]{UU, Ai, WW};
    }

    public MatrixD[] biDiagonalizeWithLeadingElements(MatrixD A, Ring ring) {
        int nn=A.M.length; int nn1 = nn - 1;
        MatrixD Ai =A;
        MatrixD UU=MatrixD.ONE(nn, ring);
        MatrixD WW=MatrixD.ONE(nn, ring);
        int i = 0;
        ArrayList<Element> possibleColsNorms = new ArrayList<>();
        Element min = new NumberR64(-1);

        while (true) {
            VectorS colsNorms = Ai.colsNorm0(ring);
            possibleColsNorms = new ArrayList<>();

            for (int j = 0; j < colsNorms.length(); ++j) {
                if (_leadingColumns.contains(j)) {
                    possibleColsNorms.add(min);
                    continue;
                }

                possibleColsNorms.add(colsNorms.V[j]);
            }

            int leadingColumn = Array.maxPos(possibleColsNorms, ring);
            _leadingColumns.add(leadingColumn);

            VectorS x = new VectorS( Ai.takeColumn(i + 1).V.clone());
            for (int k = 0; k < i; k++) {x.V[k] = ring.numberZERO;}
            Element norm_x = x.norm2(i, ring).sqrt(ring);
            if (! norm_x.isZero(ring)) {
                VectorS u = new VectorS(x.V.clone());
                u.V[i] = u.V[i].subtract(norm_x, ring);
                VectorS Ut = u.transpose(ring);
                Element XU = x.multiply(Ut, ring);
                Element mult = ring.numberONE.divide(XU, ring);
                VectorS Vec=(VectorS) u.multiply(Ai, ring);
                VectorS VecU=(VectorS) u.multiply(UU, ring);
                MatrixD UtUAi = (MatrixD) Ut.multiply( Vec,ring).multiply(mult, ring);
                Ai = Ai.subtract(UtUAi, ring);
                MatrixD UtU_U = (MatrixD) Ut.multiply( VecU,ring).multiply(mult, ring);
                UU = UU.subtract(UtU_U, ring);
            }
            if (i == nn1-1) {break;}
            VectorS x_r =new VectorS( Ai.takeRow(i + 1).V.clone());
            for (int k = 0; k < i + 1; k++) x_r.V[k] = ring.numberZERO;
            Element norm_x_r = x_r.norm2(i + 1, ring).sqrt(ring);
            if (! norm_x_r.isZero(ring)) {
                VectorS u_r = new VectorS(x_r.V.clone());
                u_r.V[i + 1] = u_r.V[i + 1].subtract(norm_x_r, ring);
                VectorS Ut_r = u_r.transpose(ring);
                Element XU_r = x_r.multiply(Ut_r, ring);
                Element mult_r = ring.numberONE.divide(XU_r, ring);
                VectorS VecT=(VectorS) Ai.multiply(Ut_r, ring);
                VectorS VecW = (VectorS) WW.multiply(Ut_r, ring);
                MatrixD AiUtU = (MatrixD)VecT.multiply( u_r,ring).multiply(mult_r, ring);
                Ai = Ai.subtract(AiUtU, ring);
                MatrixD W_UtU = (MatrixD) VecW.multiply(u_r,ring).multiply(mult_r, ring);
                WW = WW.subtract(W_UtU, ring);
            }
            i++;
        }
        return new MatrixD[]{UU, Ai, WW};
    }

    public static MatrixD[] biDiagonalizeWithSpecialFirstRow(MatrixD A, Ring ring) {
        int nn=A.M.length; int nn1 = nn - 1;
        MatrixD I = MatrixD.ONE(nn, ring);
        MatrixD Ai =A;
        MatrixD UU=MatrixD.ONE(nn, ring);
        MatrixD WW=MatrixD.ONE(nn, ring);
        int i = 0;

        VectorS x_r_special = new VectorS(Ai.takeRow(i+1).V.clone());
        Element norm_x_special = x_r_special.norm2(i, ring).sqrt(ring);
        x_r_special.V[i] = x_r_special.V[i].subtract(norm_x_special, ring);

        for (int k = 1; k < x_r_special.length(); k++) {
            x_r_special.V[k] = ring.numberZERO;
        }

        while (true) {
            VectorS x = new VectorS( Ai.takeColumn(i + 1).V.clone());
            for (int k = 0; k < i; k++) {x.V[k] = ring.numberZERO;}
            Element norm_x = x.norm2(i, ring).sqrt(ring);
            if (! norm_x.isZero(ring)) {
                VectorS u = new VectorS(x.V.clone());
                u.V[i] = u.V[i].subtract(norm_x, ring);
                VectorS Ut = u.transpose(ring);
                Element XU = x.multiply(Ut, ring);
                Element mult = ring.numberONE.divide(XU, ring);
                VectorS Vec=(VectorS) u.multiply(Ai, ring);
                VectorS VecU=(VectorS) u.multiply(UU, ring);
                MatrixD UtUAi = (MatrixD) Ut.multiply( Vec,ring).multiply(mult, ring);
                Ai = Ai.subtract(UtUAi, ring);
                MatrixD UtU_U = (MatrixD) Ut.multiply( VecU,ring).multiply(mult, ring);
                UU = UU.subtract(UtU_U, ring);
            }
            if (i == nn1-1) {break;}
            VectorS x_r =new VectorS( Ai.takeRow(i + 1).V.clone());
            for (int k = 0; k < i + 1; k++) x_r.V[k] = ring.numberZERO;
            Element norm_x_r = x_r.norm2(i + 1, ring).sqrt(ring);
            if (! norm_x_r.isZero(ring)) {
                VectorS u_r = new VectorS(x_r.V.clone());
                u_r.V[i + 1] = u_r.V[i + 1].subtract(norm_x_r, ring);
                VectorS Ut_r = u_r.transpose(ring);
                Element XU_r = x_r.multiply(Ut_r, ring);
                Element mult_r = ring.numberONE.divide(XU_r, ring);
                VectorS VecT=(VectorS) Ai.multiply(Ut_r, ring);
                VectorS VecW = (VectorS) WW.multiply(Ut_r, ring);
                MatrixD AiUtU = (MatrixD)VecT.multiply( u_r,ring).multiply(mult_r, ring);
                Ai = Ai.subtract(AiUtU, ring);
                MatrixD W_UtU = (MatrixD) VecW.multiply(u_r,ring).multiply(mult_r, ring);
                WW = WW.subtract(W_UtU, ring);
            }
            i++;
        }
        return new MatrixD[]{UU, Ai, WW};
    }

    public static MatrixD[] biDiagonalizeWithSpecialEachRow(MatrixD A, Ring ring) {
        int nn=A.M.length; int nn1 = nn - 1;
        MatrixD I = MatrixD.ONE(nn, ring);
        MatrixD Ai =A;
        MatrixD UU=MatrixD.ONE(nn, ring);
        MatrixD WW=MatrixD.ONE(nn, ring);
        int i = 0;

        while (true) {
            VectorS x_r_special = new VectorS(Ai.takeRow(i+1).V.clone());
            Element norm_x_special = x_r_special.norm2(i, ring).sqrt(ring);
            x_r_special.V[i] = x_r_special.V[i].add(norm_x_special, ring);

            for (int k = i+1; k < x_r_special.length(); k++) {
                x_r_special.V[k] = ring.numberZERO;
            }

            VectorS x = new VectorS( Ai.takeColumn(i + 1).V.clone());
            for (int k = 0; k < i; k++) {x.V[k] = ring.numberZERO;}
            Element norm_x = x.norm2(i, ring).sqrt(ring);
            if (! norm_x.isZero(ring)) {
                VectorS u = new VectorS(x.V.clone());
                u.V[i] = u.V[i].subtract(norm_x, ring);
                VectorS Ut = u.transpose(ring);
                Element XU = x.multiply(Ut, ring);
                Element mult = ring.numberONE.divide(XU, ring);
                VectorS Vec=(VectorS) u.multiply(Ai, ring);
                VectorS VecU=(VectorS) u.multiply(UU, ring);
                MatrixD UtUAi = (MatrixD) Ut.multiply( Vec,ring).multiply(mult, ring);
                Ai = Ai.subtract(UtUAi, ring);
                MatrixD UtU_U = (MatrixD) Ut.multiply( VecU,ring).multiply(mult, ring);
                UU = UU.subtract(UtU_U, ring);
            }
            if (i == nn1-1) {break;}
            VectorS x_r =new VectorS( Ai.takeRow(i + 1).V.clone());
            for (int k = 0; k < i + 1; k++) x_r.V[k] = ring.numberZERO;
            Element norm_x_r = x_r.norm2(i + 1, ring).sqrt(ring);
            if (! norm_x_r.isZero(ring)) {
                VectorS u_r = new VectorS(x_r.V.clone());
                u_r.V[i + 1] = u_r.V[i + 1].subtract(norm_x_r, ring);
                VectorS Ut_r = u_r.transpose(ring);
                Element XU_r = x_r.multiply(Ut_r, ring);
                Element mult_r = ring.numberONE.divide(XU_r, ring);
                VectorS VecT=(VectorS) Ai.multiply(Ut_r, ring);
                VectorS VecW = (VectorS) WW.multiply(Ut_r, ring);
                MatrixD AiUtU = (MatrixD)VecT.multiply( u_r,ring).multiply(mult_r, ring);
                Ai = Ai.subtract(AiUtU, ring);
                MatrixD W_UtU = (MatrixD) VecW.multiply(u_r,ring).multiply(mult_r, ring);
                WW = WW.subtract(W_UtU, ring);
            }
            i++;
        }
        return new MatrixD[]{UU, Ai, WW};
    }

    public static void checkBidiagonalization(MatrixD initialMatrix, MatrixD[] result, Ring ring, boolean printDetails) {
        if (printDetails) {
            System.out.println("----------------U---------------");
            System.out.println(result[0]);
            System.out.println("----------------A---------------");
            System.out.println(result[1]);
            System.out.println("----------------W---------------");
            System.out.println(result[2]);
        }

        MatrixD UA = result[0].transpose(ring).multCU(result[1], ring);

        if (printDetails) {
            System.out.println("----------------UA---------------");
            System.out.println(UA);
        }

        MatrixD UAW = UA.multCU(result[2].transpose(ring), ring);

        if (printDetails) {
            System.out.println("----------------UAW---------------");
            System.out.println(UAW);
        }

        MatrixD Check = UAW.subtract(initialMatrix, ring);
        System.out.println("Check BD: maxAbs absolute value = " + Check.max(ring).value);
    }

    public static MatrixD[] diagonalize(MatrixD A, Ring r) {
        int n = A.rowNum();
        MatrixD tmp = A.copy();
        MatrixD L = MatrixD.ONE(n, r);
        MatrixD R = MatrixD.ONE(n, r);
        Element[][] givens = new Element[2][2];
        givens[0][0] = r.numberONE;
        givens[0][1] = r.numberZERO;
        givens[1][0] = r.numberZERO;
        givens[1][1] = r.numberONE;

        boolean side = true;

        while (!checkSecondDiagonalValues(tmp, n, r)) {
            if (side) {
                for (int i = 0; i < n - 1; ++i) {
                    if (!tmp.getElement(i, i + 1).isZero(r)) {
                        getRightGivensRotationMatrix(givens, tmp.getElement(i, i), tmp.getElement(i, i + 1), r);
                        multLinesRight(R, givens, i, r);
                        multSquareRight(tmp, givens, i, r);
                    }
                }
            } else {
                for (int j = 0; j < n - 1; ++j) {
                    if (!tmp.getElement(j + 1, j).isZero(r)) {
                        getLeftGivensRotationMatrix(givens, tmp.getElement(j, j), tmp.getElement(j + 1, j), r);
                        multLinesLeft(givens, L, j, r);
                        multSquareLeft(givens, tmp, j, r);
                    }
                }
            }
            side = !side;
        }

        return  new MatrixD[] {L, tmp, R};
    }

    public static Element[][] getRightGivensRotationMatrix(Element[][] givens, Element a, Element b, Ring ring) {
        givens[0][0] = ring.numberONE;
        givens[0][1] = ring.numberZERO;
        givens[1][0] = ring.numberZERO;
        givens[1][1] = ring.numberONE;
        Element re = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);

        if (!b.isZero(ring) && !(re.value <= 0)) {
            Element c = a.divide(re, ring);
            Element s = b.divide(re, ring);
            if (!c.isInfinite() && !s.isInfinite()) {
                givens[0][0] = c;
                givens[0][1] = s.negate(ring);
                givens[1][0] = s;
                givens[1][1] = c;
            }
        }

        return givens;
    }

    public static Element[][] getLeftGivensRotationMatrix(Element[][] givens, Element a, Element b, Ring ring) {
        givens[0][0] = ring.numberONE;
        givens[0][1] = ring.numberZERO;
        givens[1][0] = ring.numberZERO;
        givens[1][1] = ring.numberONE;
        Element re = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);

        if (!b.isZero(ring) && !(re.value <= 0)) {
            Element c = a.divide(re, ring);
            Element s = b.divide(re, ring);
            if (!c.isInfinite() && !s.isInfinite()) {
                givens[0][0] = c;
                givens[0][1] = s;
                givens[1][0] = s.negate(ring);
                givens[1][1] = c;
            }
        }

        return givens;
    }

    public static boolean checkSecondDiagonalValues(MatrixD temp, int n, Ring ring) {
        for (int i = 0; i < (n-1); i++) {
            if (!temp.getElement(i, i+1).isZero(ring) || !temp.getElement(i+1, i).isZero(ring))
                return false;
        }
        return true;
    }

    public static boolean isPowerOfTwo(int number) {
        return number > 0 && ((number & (number - 1)) == 0);
    }

    public static MatrixD getSubMatrix(MatrixD matrix, int start_i, int end_i, int start_j, int end_j) {
        matrix = matrix.copy();
        int rowNum = end_i - start_i + 1;
        int colNum = end_j - start_j + 1;

        Element[][] e = new Element[rowNum][colNum];
        for (int i = start_i; i <= end_i; i++) {
            for (int j = start_j; j <= end_j; j++) {
                e[i-start_i][j-start_j] = matrix.getElement(i, j);
            }
        }

        return new MatrixD(e, 0);
    }

    public static MatrixD multSquareRight(MatrixD m, Element[][] r, int pos, Ring ring) {
        Element c11 = m.M[pos][pos].multiply(r[0][0], ring);
        c11 = c11.add(m.M[pos][pos + 1].multiply(r[1][0], ring), ring);

        Element c12 = m.M[pos][pos].multiply(r[0][1], ring);
        c12 = c12.add(m.M[pos][pos + 1].multiply(r[1][1], ring), ring);

        Element c21 = m.M[pos + 1][pos].multiply(r[0][0], ring);
        c21 = c21.add(m.M[pos + 1][pos + 1].multiply(r[1][0], ring), ring);

        Element c22 = m.M[pos + 1][pos].multiply(r[0][1], ring);
        c22 = c22.add(m.M[pos + 1][pos + 1].multiply(r[1][1], ring), ring);

        m.M[pos][pos] =  c11;
        m.M[pos][pos + 1] =  c12;
        m.M[pos + 1][pos] =  c21;
        m.M[pos + 1][pos + 1] =  c22;

        return m;
    }

    public static MatrixD multSquareLeft(Element[][] r, MatrixD m, int pos, Ring ring) {
        Element c11 = r[0][0].multiply(m.M[pos][pos], ring);
        c11 = c11.add(r[0][1].multiply(m.M[pos + 1][pos], ring), ring);

        Element c12 = r[0][0].multiply(m.M[pos][pos + 1], ring);
        c12 = c12.add(r[0][1].multiply(m.M[pos + 1][pos + 1], ring), ring);

        Element c21 = r[1][0].multiply(m.M[pos][pos], ring);
        c21 = c21.add(r[1][1].multiply(m.M[pos + 1][pos], ring), ring);

        Element c22 = r[1][0].multiply(m.M[pos][pos + 1], ring);
        c22 = c22.add(r[1][1].multiply(m.M[pos + 1][pos + 1], ring), ring);

        m.M[pos][pos] = c11;
        m.M[pos][pos + 1] = c12;
        m.M[pos + 1][pos] = c21;
        m.M[pos + 1][pos + 1] = c22;

        return m;
    }

    public static MatrixD multLinesRight(MatrixD right, Element[][] r, int line, Ring ring) {
        int size = right.rowNum();
        Element[][] tmp = new Element[size][2];

        for (int i = 0; i < 2; ++i) {
            if (line + i < size) {
                for (int j = 0; j < size; ++j) {
                    tmp[j][i] = right.M[j][line].multiply(r[0][i], ring).add(right.M[j][line + 1].multiply(r[1][i], ring), ring);
                }
            }
        }

        for (int i = 0; i < 2; ++i) {
            if (line + i < size) {
                for (int j = 0; j < size; ++j) {
                    right.M[j][i + line] = tmp[j][i];
                }
            }
        }

        return right;
    }

    public static MatrixD multLinesLeft(Element[][] lg, MatrixD left, int line, Ring ring) {
        int size = left.rowNum();
        Element[][] tmp = new Element[2][size];

        for (int i = 0; i < 2; ++i) {
            if (line + i < size) {
                for (int j = 0; j < size; ++j) {
                    tmp[i][j] = lg[i][0].multiply(left.M[line][j], ring).add(lg[i][1].multiply(left.M[line + 1][j], ring), ring);
                }
            }
        }

        for (int i = 0; i < 2; ++i) {
            if (line + i < size) {
                System.arraycopy(tmp[i], 0, left.M[i + line], 0, size);
            }
        }

        return left;
    }
}
