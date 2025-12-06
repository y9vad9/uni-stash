/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.splines;

import com.mathpar.polynom.Polynom;

/**
 *
 * @author yuri
 */
public class spline1D {

    private int n;
    private polynom_cubic[] polynoms;

    public static enum approxType {

        Linear, Cubic, Akima
    };

    public spline1D(double[] y) {
        buildAkima(y);
    }

    public spline1D(double[] y, approxType aType) {
        switch (aType) {
            case Linear:
                buildLinear(y);
                break;
            case Cubic:
                buildCubic(y, -1, 0, -1, 0);
            case Akima:
            default:
                buildAkima(y);
        }
    }

    private void buildLinear(double[] y) {
        double[][] m = new double[][]{
            {0, 1, 0, 0},
            {1, -1, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        n = y.length;
        this.polynoms = new polynom_cubic[n - 1];
        for (int i = 0; i < n - 1; i++) {
            double[] f = new double[]{
                y[i + 1],
                y[i],
                0,
                0
            };
            this.polynoms[i] = new polynom_cubic(m, f);
        }
    }

    private void buildCubic(double[] y, int boundltype, double boundl, int boundrtype, double boundr) {
        n = y.length;
        double[] d = new double[n];

        gridDiffCubicInternal(y, boundltype, boundl, boundrtype, boundr, d);
        buildHermite(y, d);
    }

    private void gridDiffCubicInternal(double[] y, int boundltype, double boundl, int boundrtype, double boundr, double[] d) {
        //
        // Special cases:
        // * N=2, parabolic terminated boundary condition on both ends
        // * N=2, periodic boundary condition
        //
        n = y.length;
        if ((n == 2 & boundltype == 0) & boundrtype == 0) {
            d[0] = (y[1] - y[0]);
            d[1] = d[0];
            return;
        }
        if ((n == 2 & boundltype == -1) & boundrtype == -1) {
            d[0] = 0;
            d[1] = 0;
            return;
        }

        //
        // Periodic and non-periodic boundary conditions are
        // two separate classes
        //
        double[] a1 = new double[n];
        double[] a2 = new double[n];
        double[] a3 = new double[n];
        double[] b = new double[n];
        if (boundrtype == -1 & boundltype == -1) {

            //
            // Periodic boundary conditions
            //
            y[n - 1] = y[0];

            //
            // Boundary conditions at N-1 points
            // (one point less because last point is the same as first point).
            //
            a1[0] = 1;
            a2[0] = 4;
            a3[0] = 1;
            b[0] = 3 * (y[n - 1] - y[n - 2] + y[1] - y[0]);
            for (int i = 1; i <= n - 2; i++) {
                //
                // Altough last point is [N-2], we use X[N-1] and Y[N-1]
                // (because of periodicity)
                //
                a1[i] = 1;
                a2[i] = 4;
                a3[i] = 1;
                b[i] = 3 * (y[i - 1] + y[i + 1]);
            }

            //
            // Solve, add last point (with index N-1)
            //
            double[] dt = new double[n];
            solveCyclicTriDiagonal(a1, a2, a3, b, n - 1, dt);
            for (int i_ = 0; i_ <= n - 2; i_++) {
                d[i_] = dt[i_];
            }
            d[n - 1] = d[0];
        } else {

            //
            // Non-periodic boundary condition.
            // Left boundary conditions.
            //
            if (boundltype == 0) {
                a1[0] = 0;
                a2[0] = 1;
                a3[0] = 1;
                b[0] = 2 * (y[1] - y[0]);
            }
            if (boundltype == 1) {
                a1[0] = 0;
                a2[0] = 1;
                a3[0] = 0;
                b[0] = boundl;
            }
            if (boundltype == 2) {
                a1[0] = 0;
                a2[0] = 2;
                a3[0] = 1;
                b[0] = 3 * (y[1] - y[0]) - 0.5 * boundl;
            }

            //
            // Central conditions
            //
            for (int i = 1; i <= n - 2; i++) {
                a1[i] = 1;
                a2[i] = 4;
                a3[i] = 1;
                b[i] = 3 * (y[i - 1] + y[i + 1]);
            }

            //
            // Right boundary conditions
            //
            if (boundrtype == 0) {
                a1[n - 1] = 1;
                a2[n - 1] = 1;
                a3[n - 1] = 0;
                b[n - 1] = 2 * (y[n - 1] - y[n - 2]);
            }
            if (boundrtype == 1) {
                a1[n - 1] = 0;
                a2[n - 1] = 1;
                a3[n - 1] = 0;
                b[n - 1] = boundr;
            }
            if (boundrtype == 2) {
                a1[n - 1] = 1;
                a2[n - 1] = 2;
                a3[n - 1] = 0;
                b[n - 1] = 3 * (y[n - 1] - y[n - 2]) + 0.5 * boundr;
            }

            //
            // Solve
            //
            solveTriDiagonal(a1, a2, a3, b, n, d);
        }
    }

    /*************************************************************************
    Internal subroutine. Tridiagonal solver. Solves

    ( B[0] C[0]                      )
    ( A[1] B[1] C[1]                 )
    (      A[2] B[2] C[2]            )
    (            ..........          ) * X = D
    (            ..........          )
    (           A[N-2] B[N-2] C[N-2] )
    (                  A[N-1] B[N-1] )

     *************************************************************************/
    private void solveTriDiagonal(double[] a, double[] b, double[] c, double[] d,
            int n, double[] x) {
        for (int i = 1; i <= n - 1; i++) {
            double t = a[i] / b[i - 1];
            b[i] = b[i] - t * c[i - 1];
            d[i] = d[i] - t * d[i - 1];
        }
        x[n - 1] = d[n - 1] / b[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            x[i] = (d[i] - c[i] * x[i + 1]) / b[i];
        }
    }

    /*************************************************************************
    Internal subroutine. Cyclic tridiagonal solver. Solves

    ( B[0] C[0]                 A[0] )
    ( A[1] B[1] C[1]                 )
    (      A[2] B[2] C[2]            )
    (            ..........          ) * X = D
    (            ..........          )
    (           A[N-2] B[N-2] C[N-2] )
    ( C[N-1]           A[N-1] B[N-1] )
     *************************************************************************/
    private void solveCyclicTriDiagonal(double[] a, double[] b, double[] c, double[] d,
            int n, double[] x) {
        double alpha = c[n - 1];
        double beta = a[0];
        double gamma = -b[0];
        b[0] = 2 * b[0];
        b[n - 1] = b[n - 1] - alpha * beta / gamma;
        double[] u = new double[n];
        for (int i = 0; i <= n - 1; i++) {
            u[i] = 0;
        }
        u[0] = gamma;
        u[n - 1] = alpha;
        double[] y = new double[n];
        solveTriDiagonal(a, b, c, d, n, y);
        double[] z = new double[n];
        solveTriDiagonal(a, b, c, u, n, z);
        x = new double[n];
        for (int i = 0; i <= n - 1; i++) {
            x[i] = y[i] - (y[0] + beta / gamma * y[n - 1]) / (1 + z[0] + beta / gamma * z[n - 1]) * z[i];
        }
    }

    private void buildHermite(double[] y, double[] d) {
        double[][] m = new double[][]{
            {1, 0, 0, 0},
            {0, 0, 1, 0},
            {-3, 3, -2, -1},
            {2, -2, 1, 1}
        };
        n = y.length;
        this.polynoms = new polynom_cubic[n - 1];
        for (int i = 0; i <= n - 2; i++) {
            double[] f = new double[]{
                y[i],
                y[i + 1],
                d[i],
                d[i + 1]};
            this.polynoms[i] = new polynom_cubic(m, f);
        }
    }

    private void buildAkima(double[] y) {
        //
        // Prepare W (weights), Diff (divided differences)
        //
        n = y.length;
        double[] diff = new double[n - 1];
        for (int i = 0; i <= n - 2; i++) {
            diff[i] = y[i + 1] - y[i];
        }
        double[] w = new double[n - 1];
        for (int i = 1; i <= n - 2; i++) {
            w[i] = Math.abs(diff[i] - diff[i - 1]);
        }
        //
        // Prepare Hermite interpolation scheme
        //
        double[] d = new double[n];
        for (int i = 2; i <= n - 3; i++) {
            if ((double) (Math.abs(w[i - 1]) + Math.abs(w[i + 1])) != (double) (0)) {
                d[i] = (w[i + 1] * diff[i - 1] + w[i - 1] * diff[i]) / (w[i + 1] + w[i - 1]);
            } else {
                d[i] = (diff[i - 1] + diff[i]) / 2;
            }
        }
        d[0] = diffThreePoint(0, y[0], y[1], y[2]);
        d[1] = diffThreePoint(1, y[0], y[1], y[2]);
        d[n - 2] = diffThreePoint(1, y[n - 3], y[n - 2], y[n - 1]);
        d[n - 1] = diffThreePoint(2, y[n - 3], y[n - 2], y[n - 1]);

        //
        // Build Akima spline using Hermite interpolation scheme
        //
        buildHermite(y, d);
    }

    /*************************************************************************
    Internal subroutine. Three-point differentiation
     *************************************************************************/
    private double diffThreePoint(double t, double f0, double f1, double f2) {
        double a = (f2 - f0) / 2 - (f1 - f0);
        double b = f1 - f0 - a;
        double result = 2 * a * t + b;
        return result;
    }

    public double calc(double x) {
        int i = (int) x;
        if (i >= polynoms.length - 1) {
            i = polynoms.length - 1;
        }
        return polynoms[i].calc(x - i);
    }

    public double calc_dF_dx(double x) {
        int ix = (int) x;
        if (ix >= n - 1) {
            ix = n - 2;
        }
        return polynoms[ix].calc_dF_dx(x - ix);
    }

    public Polynom getPolynom(int i) {
        return polynoms[i].getPolynom();
    }

    public Polynom[] getPolynoms() {
        Polynom[] result = new Polynom[n - 1];
        for (int i = 0; i < n - 1; i++) {
            result[i] = polynoms[i].getPolynom();
        }
        return result;
    }
}
