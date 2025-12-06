/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.splines;

/**
 *
 * @author yuri
 */
public class derivative2D {

    double[][] dx;
    double[][] dy;
    double[][] dxy;

    public derivative2D(double[][] f) {
        dx = dFdx(f);
        dy = dFdy(f);
        dxy = dFdy(dx);
    }

    private double[] buildSplineAndCalcDerivative(double[] f) {
        spline1D s = new spline1D(f);

        int n = f.length;
        double[] d = new double[n];
        for (int j = 0; j < n; j++) {
            d[j] = s.calc_dF_dx(j);
        }
        return d;
    }

    private double[][] dFdx(double[][] f) {
        int n = f.length;
        int m = f[0].length;
        double d[][] = new double[n][m];
        for (int j = 0; j < n; j++) {
            double[] ft = new double[n];
            for (int i = 0; i < n; i++) {
                ft[i] = f[i][j];
            }
            double[] buf = buildSplineAndCalcDerivative(ft);
            for (int i = 0; i < n; i++) {
                d[i][j] = buf[i];
            }
        }
        return d;
    }

    private double[][] dFdy(double[][] f) {
        int n = f.length;
        double d[][] = new double[n][];
        for (int i = 0; i < n; i++) {
            d[i] = buildSplineAndCalcDerivative(f[i]);
        }
        return d;
    }
}
