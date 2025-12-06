package com.mathpar.students.OLD.savchenko;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.students.OLD.savchenko.exception.WrongDimensionsException;

public class Utils {

    public static MatrixD getGivensRotationMatrix(int n, int i, int j, Element a, Element b, Ring ring) {
        MatrixD G = MatrixD.ONE(n, ring);
        if (b.isZero(ring)) {
            G.M[i][i] = ring.numberONE;
            G.M[i][j] = ring.numberZERO;
            G.M[j][i] = ring.numberZERO;
            G.M[j][j] = ring.numberONE;
        } else {
            Element r = a.pow(2, ring).add(b.pow(2, ring), ring).sqrt(ring);     // Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
            Element c = a.divide(r, ring);                                            // a/r;
            Element s = b.negate(ring).divide(r, ring);                               // (-b)/r;
            if (c.isInfinite() || s.isInfinite()) {
                G.M[i][i] = ring.numberONE;
                G.M[i][j] = ring.numberZERO;
                G.M[j][i] = ring.numberZERO;
                G.M[j][j] = ring.numberONE;
            } else {
                G.M[i][i] = c;
                G.M[i][j] = s;
                G.M[j][i] = s.negate(ring);
                G.M[j][j] = c;
            }
        }
        return G;
    }

    public static boolean checkSecondDiagonalValues(MatrixD temp, int n, Ring ring) {
        for (int i = 0; i < (n-1); i++) {
            if (!temp.getElement(i, i+1).isZero(ring) || !temp.getElement(i+1, i).isZero(ring))
                return false;
        }
        return true;
    }


    public static MatrixD getTriangleMatrixNumber64(int n, int mod, Ring ring) {
        MatrixD L = new MatrixD(n, n, mod, ring);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (!(j<=i))
                    L.M[i][j] = ring.numberZERO;
        return L;
    }

    public static MatrixD[] getTwoGivensRotationMatrices(double a, double b, double d, int n, int i, int j, Ring ring) {
        MatrixD left = MatrixD.ONE(n, ring);
        MatrixD right = MatrixD.ONE(n, ring);
        double c = 0d;
        double s = 0d;
        double C = 0d;
        double S = 0d;
        double t = 0d;
        double T = 0d;

        if (a != 0d && d != 0d) {
//            System.out.println("a ≠ 0, d ≠ 0 \n");
            t = ((-1d*(b*b+d*d-a*a)) + Math.sqrt(Math.pow((b*b+d*d-a*a), 2) + 4d*a*a*b*b)) / (2d*a*b);
            T = (-1d/d)*(a*t + b);
        } else if (a != 0d && d == 0d) {
//            System.out.println("a ≠ 0, d = 0 \n");
            T = 0d;
            t = (-b)/a;
        } else if (a == 0d && d != 0d) {
//            System.out.println("a = 0, d ≠ 0 \n");
            t = 0d;
            T = (-b)/d;
        } else {                                    // a = 0 & d = 0
//            System.out.println("a = 0, d = 0 \n");
            c = 1d;
            s = 0d;
            C = 0d;
            S = 1d;
        }

        if (!(a == 0d && d == 0d)) {
            c = Math.sqrt(1d/(1d+t*t));                                 // c = Math.cos(Math.atan(t));
            s = t*c;                                                    // s = Math.sin(Math.atan(t));
            C = Math.sqrt(1d/(1d+T*T));                                 // C = Math.cos(Math.atan(T));
            S = T*C;                                                    // S = Math.sin(Math.atan(T));
        }

        left.M[i][i] = new NumberR64(c);
        left.M[i][j] = new NumberR64(-s);
        left.M[j][i] = new NumberR64(s);
        left.M[j][j] = new NumberR64(c);

        right.M[i][i] = new NumberR64(C);
        right.M[i][j] = new NumberR64(-S);
        right.M[j][i] = new NumberR64(S);
        right.M[j][j] = new NumberR64(C);

        return new MatrixD[]{left, right};
//            long [][] arr = {{1, 0}, {5, 2}};
//            MatrixD test = new MatrixD(arr, ring);
//            MatrixD[] lr = getTwoGivensRotationMatrices(test.getElement(0,0).doubleValue(), test.getElement(1, 0).doubleValue(),
//                    test.getElement(1,1).doubleValue(), 2, 0, 1, ring);
//            System.out.println("L * A * R = \n");
//            System.out.println(lr[0].multiplyMatr(test, ring).multiplyMatr(lr[1], ring).toString());
    }

    public static void removeNonDiagonalValues(MatrixD d, Ring ring) {
        for (int i = 0; i < d.M.length; i++) {
            for (int j = 0; j < d.M[0].length; j++) {
                if (i != j) {
                    d.M[i][j] = ring.numberZERO;
                }
            }
        }
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

    public static MatrixD insertMatrixToMatrix(MatrixD matrix, MatrixD block, int i_start, int j_start){
        block = block.copy();
        MatrixD result = matrix.copy();

        for (int i = 0; i < block.rowNum(); i++) {
            for (int j = 0; j < block.colNum(); j++) {
                result.M[i+i_start][j+j_start] = block.getElement(i, j);
            }
        }

        return result;
    }

    public static void readBlock(MatrixD matrix, int iOffset, int jOffset, Element[][] elements, int h) {
        for (int i = iOffset; i < (h+iOffset); i++) {
            for (int j = jOffset; j < (h+jOffset); j++) {
                elements[i-iOffset][j-jOffset] = matrix.getElement(i, j);
            }
        }
    }

    public static MatrixD getBlock(MatrixD input, int block) {
        int rowNum = input.rowNum();
        int colNum = input.colNum();

        int i_start = 0, i_end = 0, j_start = 0, j_end = 0;

        if (block == 1) {
            i_start = rowNum / 4;
            i_end = rowNum - (rowNum / 4) - 1;
            j_start = 0;
            j_end = (colNum / 2) - 1;
        } else if (block == 2) {
            i_start = 0;
            i_end = (rowNum / 2) - 1;
            j_start = 0;
            j_end = (colNum / 2) - 1;
        } else if (block == 3) {
            i_start = rowNum / 2;
            i_end = rowNum - 1;
            j_start = colNum / 2;
            j_end = colNum - 1;
        } else if (block == 4) {
            i_start = rowNum / 4;
            i_end = rowNum - (rowNum / 4) - 1;
            j_start = colNum / 2;
            j_end = colNum - 1;
        }

        return Utils.getSubMatrix(input, i_start, i_end, j_start, j_end);
    }

    public static MatrixD block4(MatrixD input, char b) throws WrongDimensionsException {
        if ((input.rowNum() != input.colNum()) || !Utils.isPowerOfTwo(input.rowNum()))
            throw new WrongDimensionsException();

        MatrixD matrix = input.copy();
        int n = matrix.rowNum();
        int h = n/2;

        if (n == 1) {
            return matrix;
        } else {
            switch (b) {
                case 'A': return Utils.getSubMatrix(matrix, 0, h-1, 0, h-1);
                case 'B': return Utils.getSubMatrix(matrix, 0, h-1, h, n-1);
                case 'C': return Utils.getSubMatrix(matrix, h, n-1, 0, h-1);
                case 'D': return Utils.getSubMatrix(matrix, h, n-1, h, n-1);
                default: return matrix;
            }
        }
    }

    // В результате у матрицы B поменяется только ряд x и ряд y
    public static MatrixD leftMultiplyGivensToMatrix(MatrixD A, MatrixD B, int x, int y, Ring ring) {
        MatrixD result = B.copy();
        Element zero = ring.numberZERO;
        Element res;

        int k = B.colNum();
        int n = B.rowNum();

        VectorS newXrow = new VectorS(k);
        VectorS newYrow = new VectorS(k);
        VectorS xRowFromA = A.takeRow(x+1);
        VectorS yRowFromA = A.takeRow(y+1);

        for (int i = 0; i < k; i++) {
            VectorS iColFromB = B.takeColumn(i+1);
            // замена рядка x
            res = zero;
            for (int j = 0; j < n; j++) {
                res = res.add((xRowFromA.V[j]).multiply(iColFromB.V[j], ring), ring);
            }
            newXrow.V[i] = res;
            // замена рядка y
            res = zero;
            for (int j = 0; j < n; j++) {
                res = res.add((yRowFromA.V[j]).multiply(iColFromB.V[j], ring), ring);
            }
            newYrow.V[i] = res;
        }

        for (int h = 0; h < k; h++) {
            result.M[x][h] = newXrow.V[h];
            result.M[y][h] = newYrow.V[h];
        }

        return result;
    }

    // В результате у матрицы A поменяется только столбец x и столбец y
    public static MatrixD rightMultiplyMatrixToGivens(MatrixD A, MatrixD B, int x, int y, Ring ring) {
        MatrixD result = A.copy();
        Element zero = ring.numberZERO;
        Element res;

        int n = A.rowNum();

        VectorS newXcol = new VectorS(n);
        VectorS newYcol = new VectorS(n);
        VectorS xColFromB = B.takeColumn(x+1);
        VectorS yColFromB = B.takeColumn(y+1);

        for (int i = 0; i < n; i++) {
            VectorS iRowFromA = A.takeRow(i+1);
            // замена столбца x
            res = zero;
            for (int j = 0; j < n; j++) {
                res = res.add(iRowFromA.V[j].multiply(xColFromB.V[j], ring), ring);
            }
            newXcol.V[i] = res;
            // замена столбца y
            res = zero;
            for (int j = 0; j < n; j++) {
                res = res.add(iRowFromA.V[j].multiply(yColFromB.V[j], ring), ring);
            }
            newYcol.V[i] = res;
        }

        for (int h = 0; h < n; h++) {
            result.M[h][x] = newXcol.V[h];
            result.M[h][y] = newYcol.V[h];
        }

        return result;
    }
}
