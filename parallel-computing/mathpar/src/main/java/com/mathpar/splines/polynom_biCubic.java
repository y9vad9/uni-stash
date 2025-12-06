package com.mathpar.splines;

import com.mathpar.number.NumberR64;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author yuri
 */
public class polynom_biCubic {

    private double[] k;

    public polynom_biCubic(double m[][], double f[]) {
        k = new double[16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                k[i] += m[i][j] * f[j];
            }
        }
    }

    public double calc(double x, double y) {
        return k[0] + x * (k[1] + x * (k[2] + x * k[3]))
                + y * (k[4] + x * (k[5] + x * (k[6] + x * k[7]))
                + y * (k[8] + x * (k[9] + x * (k[10] + x * k[11]))
                + y * (k[12] + x * (k[13] + x * (k[14] + x * k[15])))));
    }
    private static int[] pows = new int[]{
        0, 0,
        1, 0,
        2, 0, // x^2
        3, 0,
        0, 1, // y
        1, 1,
        2, 1,
        3, 1,
        0, 2,
        1, 2, // xy^2
        2, 2,
        3, 2,
        0, 3,
        1, 3,
        2, 3,
        3, 3
    };

    public Polynom getPolynom() {
        NumberR64[] coeffs = new NumberR64[16];
        for (int i = 0; i < 16; i++) {
            coeffs[i] = new NumberR64(k[i]);
        }

        return new Polynom(pows, coeffs);
    }
}
