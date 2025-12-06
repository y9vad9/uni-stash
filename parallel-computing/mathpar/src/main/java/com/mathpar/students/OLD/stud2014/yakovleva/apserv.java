/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.yakovleva;

import java.util.Random;

/**
 *
 * @author yakovlev
 */
public class apserv {
    public class apbuffers {
        public int[] ia0;
        public int[] ia1;
        public int[] ia2;
        public int[] ia3;
        public double[] ra0;
        public double[] ra1;
        public double[] ra2;
        public double[] ra3;

        public apbuffers() {
            ia0 = new int[0];
            ia1 = new int[0];
            ia2 = new int[0];
            ia3 = new int[0];
            ra0 = new double[0];
            ra1 = new double[0];
            ra2 = new double[0];
            ra3 = new double[0];
        }
    };

    public static double randomreal() {
        Random m = new Random();
        return m.nextDouble();
    }

    public static void taskgenint1d(double a,
            double b,
            int n,
            double[] x,
            double[] y) {
        int i = 0;
        double h = 0;
        x = new double[0];
        y = new double[0];
        ap.assertJ(n >= 1, "TaskGenInterpolationEqdist1D: N<1!");
        x = new double[n];
        y = new double[n];
        if (n > 1) {
            x[0] = a;
            y[0] = 2 * randomreal() - 1;
            h = (b - a) / (n - 1);
            for (i = 1; i <= n - 1; i++) {
                if (i != n - 1) {
                    x[i] = a + (i + 0.2 * (2 * randomreal() - 1)) * h;
                } else {
                    x[i] = b;
                }
                y[i] = y[i - 1] + (2 * randomreal() - 1) * (x[i] - x[i - 1]);
            }
        } else {
            x[0] = 0.5 * (a + b);
            y[0] = 2 * randomreal() - 1;
        }
    }

    public static boolean isfinite(double d) {
        return !Double.isNaN(d) && !Double.isInfinite(d);
    }

    public static boolean isfinitevector(double[] x,
            int n) {
        boolean result = false;
        int i = 0;
        ap.assertJ(n >= 0, "APSERVIsFiniteVector: internal error (N<0)");
        for (i = 0; i <= n - 1; i++) {
            if (x.length != 0) {
                if (!math.isfinite(x[i])) {
                    result = false;
                    return result;
                }
            }
        }
        result = true;
        return result;
    }

    public static boolean aredistinct(double[] x,
            int n) {
        boolean result = false;
        double a = 0;
        double b = 0;
        int i = 0;
        boolean nonsorted = false;
        ap.assertJ(n >= 1, "APSERVAreDistinct: internal error (N<1)");
        if (n == 1) {
            //
            // everything is alright, it is up to caller to decide whether it
            // can interpolate something with just one point
            //
            result = true;
            return result;
        }
        a = x[0];
        b = x[0];
        nonsorted = false;
        for (i = 1; i <= n - 1; i++) {
            a = Math.min(a, x[i]);
            b = Math.max(b, x[i]);
            nonsorted = nonsorted | (double) (x[i - 1]) >= (double) (x[i]);
        }
        ap.assertJ(!nonsorted, "APSERVAreDistinct: internal error (not sorted)");
        for (i = 1; i <= n - 1; i++) {
            if ((double) ((x[i] - a) / (b - a) + 1) == (double) ((x[i - 1] - a) / (b - a) + 1)) {
                result = false;
                return result;
            }
        }
        result = true;
        return result;
    }

    public static void apperiodicmap(double x,
            double a,
            double b,
            double k) {
        k = 0;
        ap.assertJ((double) (a) < (double) (b), "APPeriodicMap: internal error!");
        k = (int) Math.floor((x - a) / (b - a));
        x = x - k * (b - a);
        while ((double) (x) < (double) (a)) {
            x = x + (b - a);
            k = k - 1;
        }
        while ((double) (x) > (double) (b)) {
            x = x - (b - a);
            k = k + 1;
        }
        x = Math.max(x, a);
        x = Math.min(x, b);
    }

    /**
     * ***********************************************************************
     * If Length(X)<N, resizes X
     *
     * -- ALGLIB -- Copyright 20.03.2009 by Bochkanov Sergey
        ************************************************************************
     */
    public static void ivectorsetlengthatleast(int[] x,
            int n) {
        if (ap.len(x) < n) {
            x = new int[n];
        }
    }

    /**
     * ***********************************************************************
     * If Length(X)<N, resizes X
     *
     * -- ALGLIB -- Copyright 20.03.2009 by Bochkanov Sergey
        ************************************************************************
     */
    public static void rvectorsetlengthatleast(double[] x,
            int n) {
        if (ap.len(x) < n) {
            x = new double[n];
        }
    }
}
