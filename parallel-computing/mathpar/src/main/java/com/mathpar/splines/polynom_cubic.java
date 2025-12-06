package com.mathpar.splines;

/**
 *
 * @author yuri
 */
import com.mathpar.number.NumberR64;
import com.mathpar.polynom.Polynom;

public class polynom_cubic {

    private double[] k;

    public polynom_cubic(double m[][], double f[]) {
        k = new double[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                k[i] += m[i][j] * f[j];
            }
        }
    }

    public double calc(double x) {
        return k[0] + x * (k[1] + x * (k[2] + x * k[3]));
    }

    public double calc_dF_dx(double x) {
        return k[1] + 2 * x * k[2] + 3 * x * x * k[3];
    }
    private static int[] pows = new int[]{
        0,
        1,
        2, // x^2
        3
    };

    public Polynom getPolynom() {
        NumberR64[] coeffs = new NumberR64[4];
        for (int i = 0; i < 4; i++) {
            coeffs[i] = new NumberR64(k[i]);
        }

        return new Polynom(pows, coeffs);
    }
}
