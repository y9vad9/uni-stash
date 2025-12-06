/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.splines;

/**
 *
 * @author yuri
 */
public class derivative3D {

    double[][][] dx;
    double[][][] dy;
    double[][][] dz;
    double[][][] dxy;
    double[][][] dxz;
    double[][][] dyz;
    double[][][] dxyz;

    public derivative3D(double[][][] f) {
        System.out.println("3d calc dx");
        dx = dFdx(f);
        System.out.println("3d calc dy");
        dy = dFdy(f);
        System.out.println("3d calc dz");
        dz = dFdz(f);
        System.out.println("3d calc dxy");
        dxy = dFdy(dx);
        System.out.println("3d calc dxz");
        dxz = dFdz(dx);
        System.out.println("3d calc dyz");
        dyz = dFdz(dy);
        System.out.println("3d calc dxyz");
        dxyz = dFdz(dxy);
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

    private double[][][] dFdz(double[][][] f) {
        int n = f.length;
        int m = f[0].length;
        double d[][][] = new double[n][m][];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                d[i][j] = buildSplineAndCalcDerivative(f[i][j]);
            }
        }
        return d;
    }

    private double[][][] dFdy(double[][][] f) {
        int n = f.length;
        int m = f[0].length;
        int l = f[0][0].length;
        double d[][][] = new double[n][m][l];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < l; k++) {
                double[] ft = new double[m];
                for (int j = 0; j < m; j++) {
                    ft[j] = f[i][j][k];
                }
                double[] dt = buildSplineAndCalcDerivative(ft);
                for (int j = 0; j < m; j++) {
                    d[i][j][k] = dt[j];
                }
            }
        }
        return d;
    }

    private double[][][] dFdx(double[][][] f) {
        int n = f.length;
        int m = f[0].length;
        int l = f[0][0].length;
        double d[][][] = new double[n][m][l];
        for (int j = 0; j < m; j++) {
            for (int k = 0; k < l; k++) {
                double[] ft = new double[m];
                for (int i = 0; i < n; i++) {
                    ft[i] = f[i][j][k];
                }
                double[] dt = buildSplineAndCalcDerivative(ft);
                for (int i = 0; i < n; i++) {
                    d[i][j][k] = dt[i];
                }
            }
        }
        return d;
    }
}
