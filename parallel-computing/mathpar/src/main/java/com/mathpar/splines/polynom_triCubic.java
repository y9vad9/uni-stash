package com.mathpar.splines;

/**
 *
 * @author yuri
 */
import com.mathpar.number.NumberR64;
import com.mathpar.polynom.Polynom;

public class polynom_triCubic {

    private double[] k;

    public polynom_triCubic(double m[][], double f[]) {
        k = new double[64];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                k[i] += m[i][j] * f[j];
            }
        }
    }

    public double calc(double x, double y, double z) {
        return /*   */ k[0] + x * (k[1] + x * (k[2] + x * k[3]))
                + y * (k[4] + x * (k[5] + x * (k[6] + x * k[7]))
                + y * (k[8] + x * (k[9] + x * (k[10] + x * k[11]))
                + y * (k[12] + x * (k[13] + x * (k[14] + x * k[15])))))
                + z * (k[16] + x * (k[17] + x * (k[18] + x * k[19]))
                + y * (k[20] + x * (k[21] + x * (k[22] + x * k[23]))
                + y * (k[24] + x * (k[25] + x * (k[26] + x * k[27]))
                + y * (k[28] + x * (k[29] + x * (k[30] + x * k[31])))))
                + z * (k[32] + x * (k[33] + x * (k[34] + x * k[35]))
                + y * (k[36] + x * (k[37] + x * (k[38] + x * k[39]))
                + y * (k[40] + x * (k[41] + x * (k[42] + x * k[43]))
                + y * (k[44] + x * (k[45] + x * (k[46] + x * k[47])))))
                + z * (k[48] + x * (k[49] + x * (k[50] + x * k[51]))
                + y * (k[52] + x * (k[53] + x * (k[54] + x * k[55]))
                + y * (k[56] + x * (k[57] + x * (k[58] + x * k[59]))
                + y * (k[60] + x * (k[61] + x * (k[62] + x * k[63]))))))));
    }
    private static int[] pows = new int[]{
        0, 0, 0,
        1, 0, 0,
        2, 0, 0,
        3, 0, 0,
        0, 1, 0,
        1, 1, 0,
        2, 1, 0,
        3, 1, 0,
        0, 2, 0,
        1, 2, 0,
        2, 2, 0,
        3, 2, 0,
        0, 3, 0,
        1, 3, 0,
        2, 3, 0,
        3, 3, 0,
        0, 0, 1,
        1, 0, 1,
        2, 0, 1,
        3, 0, 1,
        0, 1, 1,
        1, 1, 1,
        2, 1, 1,
        3, 1, 1,
        0, 2, 1,
        1, 2, 1,
        2, 2, 1,
        3, 2, 1,
        0, 3, 1,
        1, 3, 1,
        2, 3, 1,
        3, 3, 1,
        0, 0, 2,
        1, 0, 2,
        2, 0, 2,
        3, 0, 2,
        0, 1, 2,
        1, 1, 2,
        2, 1, 2,
        3, 1, 2,
        0, 2, 2,
        1, 2, 2,
        2, 2, 2,
        3, 2, 2,
        0, 3, 2,
        1, 3, 2,
        2, 3, 2,
        3, 3, 2,
        0, 0, 3,
        1, 0, 3,
        2, 0, 3,
        3, 0, 3,
        0, 1, 3,
        1, 1, 3,
        2, 1, 3,
        3, 1, 3,
        0, 2, 3,
        1, 2, 3,
        2, 2, 3,
        3, 2, 3,
        0, 3, 3,
        1, 3, 3,
        2, 3, 3,
        3, 3, 3,};

    public Polynom getPolynom() {
        NumberR64[] coeffs = new NumberR64[64];
        for (int i = 0; i < 64; i++) {
            coeffs[i] = new NumberR64(k[i]);
        }

        return new Polynom(pows, coeffs);
    }
}
