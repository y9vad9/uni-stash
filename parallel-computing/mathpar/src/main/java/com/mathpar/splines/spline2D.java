package com.mathpar.splines;

import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author yuri
 */
public class spline2D {

    private int n;
    private int m;
    private polynom_biCubic[][] polynoms;
    private spline1D.approxType aType;

    public spline2D(double[][] f) {
        this.aType = spline1D.approxType.Akima;
        buildBiCubic(f);
    }

    public spline2D(double[][] f, spline1D.approxType aType) {
        this.aType = aType;
        switch (aType) {
            case Linear:
                buildBiLinear(f);
            case Cubic:
            case Akima:
            default:
                buildBiCubic(f);
        }
    }

    private void buildBiLinear(double[][] f) {
        n = f.length;
        m = f[0].length;
        polynoms = new polynom_biCubic[(n - 1)][(m - 1)];
        double[][] matrix = new double[][]{
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, -1, 1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < m - 1; j++) {
                double func[] = new double[]{
                    f[i][j],
                    f[i + 1][j],
                    f[i + 1][j + 1],
                    f[i][j + 1],
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
                };
                polynoms[i][j] = new polynom_biCubic(matrix, func);
            }
        }
    }

    private void buildBiCubic(double[][] f) {
        n = f.length;
        m = f[0].length;
        derivative2D df = new derivative2D(f);
        polynoms = new polynom_biCubic[(n - 1)][(m - 1)];

        double[][] matrix = new double[][]{
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-3, 3, 0, 0, -2, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, -2, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, -3, 3, 0, 0, -2, -1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 2, -2, 0, 0, 1, 1, 0, 0},
            {-3, 0, 0, 3, 0, 0, 0, 0, -2, 0, 0, -1, 0, 0, 0, 0},
            {0, 0, 0, 0, -3, 0, 0, 3, 0, 0, 0, 0, -2, 0, 0, -1},
            {9, -9, 9, -9, 6, 3, -3, -6, 6, -6, -3, 3, 4, 2, 1, 2},
            {-6, 6, -6, 6, -3, -3, 3, 3, -4, 4, 2, -2, -2, -2, -1, -1},
            {2, 0, 0, -2, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 2, 0, 0, -2, 0, 0, 0, 0, 1, 0, 0, 1},
            {-6, 6, -6, 6, -4, -2, 2, 4, -3, 3, 3, -3, -2, -1, -1, -2},
            {4, -4, 4, -4, 2, 2, -2, -2, 2, -2, -2, 2, 1, 1, 1, 1}
        };
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < m - 1; j++) {
                double func[] = new double[]{
                    f[i][j],
                    f[i + 1][j],
                    f[i + 1][j + 1],
                    f[i][j + 1],
                    df.dx[i][j],
                    df.dx[i + 1][j],
                    df.dx[i + 1][j + 1],
                    df.dx[i][j + 1],
                    df.dy[i][j],
                    df.dy[i + 1][j],
                    df.dy[i + 1][j + 1],
                    df.dy[i][j + 1],
                    df.dxy[i][j],
                    df.dxy[i + 1][j],
                    df.dxy[i + 1][j + 1],
                    df.dxy[i][j + 1]
                };
                polynoms[i][j] = new polynom_biCubic(matrix, func);
            }
        }
    }

    public double calc(double x, double y) {
        int ix = (int) x;
        if (ix >= n - 1) {
            ix = n - 2;
        }
        double xx = (x - ix);
        int iy = (int) y;
        if (iy >= m - 1) {
            iy = m - 2;
        }
        double yy = (y - iy);
        return polynoms[ix][iy].calc(xx, yy);
    }

    public Polynom getPolynom(int i, int j) {
        return polynoms[i][j].getPolynom();
    }

    public Polynom[][] getPolynoms() {
        Polynom[][] result = new Polynom[n - 1][m - 1];
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < m - 1; j++) {
                result[i][j] = polynoms[i][j].getPolynom().deleteZeroCoeff(Ring.ringR64xyzt);
                result[i][j] = result[i][j].ordering(Ring.ringR64xyzt);
            }
        }
        return result;
    }
}
