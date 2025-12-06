/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.yakovleva;

import static com.mathpar.students.OLD.stud2014.yakovleva.spline1d.spline1dbuildhermite;

/**
 *
 * этот класс строит сплайн
 *
 * @author yakovleva&osipova
 * @years 2011
 * @version 1.0
 */
public class spline1d {
    /**
     * dd - производная по промежутку
     *
     */
    public static double[] dd = new double[] {};
    /**
     * pp - производная
     */
    public static int[] pp = new int[] {};//variables for griddiff
    /**
     * dd1 - производная
     */
    public static double[] dd1 = new double[] {};//variables for griddiff
    /**
     * dd2 - производная
     */
    public static double[] dd2 = new double[] {};//variables for griddiff
    /**
     * pp2 - производная
     */
    public static int[] pp2 = new int[] {};//variables for convdiff
    /**
     * yy2 - производная
     */
    public static double[] yy2 = new double[] {};//variables for convdiff

    public spline1d() {
    }

    /**
     * строит линейный сплайн
     */
    public static void spline1dbuildlinear(double[] x,
            double[] y,
            int n,
            spline1dinterpolant c) {
        int i = 0;
        x = (double[]) x.clone();
        y = (double[]) y.clone();
        // выбирает и сортирует точки
        ap.assertJ(n > 1, "Spline1DBuildLinear: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DBuildLinear: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DBuildLinear: Length(Y)<N!");
        // строит линейный сплайн
        c.periodic = false;
        c.n = n;
        c.k = 3;
        c.x = new double[n];
        c.c = new double[4 * (n - 1)];
        for (i = 0; i <= n - 1; i++) {
            c.x[i] = x[i];
        }
        for (i = 0; i <= n - 2; i++) {
            c.c[4 * i + 1] = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
            c.c[4 * i + 0] = y[i] - c.c[4 * i + 1] * x[i];
            c.c[4 * i + 2] = 0;
            c.c[4 * i + 3] = 0;
        }
    }

    private static void heapsortpoints(double[] x,
            double[] y,
            int n) {
        double[] bufx = new double[0];
        double[] bufy = new double[0];
        tsort.tagsortfastr(x, y, bufx, bufy, n);
    }

    public static void spline1dbuildcubic(double[] x, double[] y, int n, int boundltype, double boundl, int boundrtype, double boundr, spline1dinterpolant c) {
        spline1d.spline1dbuildcubic1(x, y, n, boundltype, boundl, boundrtype, boundr, c);
        return;
    }

    public static void spline1dbuildcubic(double[] x, double[] y, spline1dinterpolant c) {
        int n;
        int boundltype;
        double boundl;
        int boundrtype;
        double boundr;
        if ((ap.len(x) != ap.len(y))) {
            c = new spline1dinterpolant();
        }
        n = ap.len(x);
        boundltype = 0;
        boundl = 0;
        boundrtype = 0;
        boundr = 0;
        spline1d.spline1dbuildcubic(x, y, n, boundltype, boundl, boundrtype, boundr, c);
        return;
    }
    // строит кубический сплайн

    public static void spline1dbuildcubic1(double[] x,
            double[] y,
            int n,
            int boundltype,
            double boundl,
            int boundrtype,
            double boundr,
            spline1dinterpolant c) {
        double[] a1 = new double[0];
        double[] a2 = new double[0];
        double[] a3 = new double[0];
        double[] b = new double[0];
        double[] dt = new double[0];
        double[] d = new double[0];
        int[] p = new int[0];
        int ylen = 0;
        x = (double[]) x.clone();
        y = (double[]) y.clone();
        //
        // выбираем граничные условия
        //
        ap.assertJ(((boundltype == -1 | boundltype == 0) | boundltype == 1) | boundltype == 2, "Spline1DBuildCubic: incorrect BoundLType!");
        ap.assertJ(((boundrtype == -1 | boundrtype == 0) | boundrtype == 1) | boundrtype == 2, "Spline1DBuildCubic: incorrect BoundRType!");
        ap.assertJ((boundrtype == -1 & boundltype == -1) | (boundrtype != -1 & boundltype != -1), "Spline1DBuildCubic: incorrect BoundLType/BoundRType!");
        if (boundltype == 1 | boundltype == 2) {
            ap.assertJ(math.isfinite(boundl), "Spline1DBuildCubic: BoundL is infinite or NAN!");
        }
        if (boundrtype == 1 | boundrtype == 2) {
            ap.assertJ(math.isfinite(boundr), "Spline1DBuildCubic: BoundR is infinite or NAN!");
        }

        //
        // выбираем и сортируем точки
        //
        ap.assertJ(n >= 2, "Spline1DBuildCubic: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DBuildCubic: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DBuildCubic: Length(Y)<N!");

        //
        // выбираем и сортируем точки
        //
        ylen = n;
        if (boundltype == -1) {
            ylen = n - 1;
        }
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DBuildCubic: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, ylen), "Spline1DBuildCubic: Y contains infinite or NAN values!");
        heapsortppoints(x, y, p, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DBuildCubic: at least two consequent points are too close!");


        spline1dgriddiffcubicinternal(x, y, n, boundltype, boundl, boundrtype, boundr, d, a1, a2, a3, b, dt);

        spline1dbuildhermite(x, y, dd, n, c);
        c.periodic = boundltype == -1 | boundrtype == -1;
    }

    public static void spline1dbuildhermite(double[] x, double[] y, double[] d, int n, spline1dinterpolant c) {

        spline1d.spline1dbuildhermite1(x, y, d, n, c);
        return;
    }

    public static void spline1dbuildhermite(double[] x, double[] y, double[] d, spline1dinterpolant c) {
        int n;
        if ((ap.len(x) != ap.len(y)) || (ap.len(x) != ap.len(d))) //   throw new alglibexception("Error while calling 'spline1dbuildhermite': looks like one of arguments has wrong size");
        {
            c = new spline1dinterpolant();
        }
        n = ap.len(x);
        spline1d.spline1dbuildhermite(x, y, d, n, c);

        return;
    }
    //строит сплайн Эрмита

    public static void spline1dbuildhermite1(double[] x, //массив у
            double[] y,//массив у
            double[] d,//массив производны
            int n,
            spline1dinterpolant c) {
        int i = 0;
        double delta = 0;
        double delta2 = 0;
        double delta3 = 0;

        x = (double[]) x.clone();
        y = (double[]) y.clone();
        d = (double[]) d.clone();

        ap.assertJ(n >= 2, "Spline1DBuildHermite: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DBuildHermite: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DBuildHermite: Length(Y)<N!");
        ap.assertJ(ap.len(d) >= n, "Spline1DBuildHermite: Length(D)<N!");

        //
        // выбираем и сортируем точки
        //
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DBuildHermite: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, n), "Spline1DBuildHermite: Y contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(d, n), "Spline1DBuildHermite: D contains infinite or NAN values!");
        heapsortdpoints(x, y, d, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DBuildHermite: at least two consequent points are too close!");

        //строит сплайн Эрмита


        c.x = new double[n];
        c.c = new double[4 * (n - 1)];
        c.periodic = false;
        c.k = 3;
        c.n = n;
        for (i = 0; i <= n - 1; i++) {
            c.x[i] = x[i];
        }
        for (i = 0; i <= n - 2; i++) {
            delta = x[i + 1] - x[i];
            delta2 = math.sqr(delta);
            delta3 = delta * delta2;
            c.c[4 * i + 0] = y[i];
            c.c[4 * i + 1] = d[i];//
            c.c[4 * i + 2] = (3 * (y[i + 1] - y[i]) - 2 * d[i] * delta - d[i + 1] * delta) / delta2;
            c.c[4 * i + 3] = (2 * (y[i] - y[i + 1]) + d[i] * delta + d[i + 1] * delta) / delta3;
        }
    }

    /**
     * ***********************************************************************
     * Internal subroutine. Heap sort.
     ************************************************************************
     */
    public static void heapsortdpoints(double[] x,
            double[] y,
            double[] d,
            int n) {
        double[] rbuf = new double[0];
        int[] ibuf = new int[0];
        double[] rbuf2 = new double[0];
        int[] ibuf2 = new int[0];
        int i = 0;
        int i_ = 0;

        ibuf = new int[n];
        rbuf = new double[n];
        for (i = 0; i <= n - 1; i++) {
            ibuf[i] = i;
        }
        tsort.tagsortfasti(x, ibuf, rbuf2, ibuf2, n);
        for (i = 0; i <= n - 1; i++) {
            rbuf[i] = y[ibuf[i]];
        }
        for (i_ = 0; i_ <= n - 1; i_++) {
            y[i_] = rbuf[i_];
        }
        for (i = 0; i <= n - 1; i++) {
            rbuf[i] = d[ibuf[i]];
        }
        for (i_ = 0; i_ <= n - 1; i_++) {
            d[i_] = rbuf[i_];
        }
    }

    public static void spline1dbuildakima(double[] x, double[] y, int n, spline1dinterpolant c) {
        c = new spline1dinterpolant();
        spline1d.spline1dbuildakima1(x, y, n, c);
        return;
    }
// строит сплайн Акимы

    public static void spline1dbuildakima(double[] x, double[] y, spline1dinterpolant c) {
        int n;
        if ((ap.len(x) != ap.len(y))) {
            c = new spline1dinterpolant();
        }
        n = ap.len(x);
        spline1d.spline1dbuildakima1(x, y, n, c);

        return;
    }

    /**
     * Эта подпрограмма строит сплайн Акимы интерполянт Подпрограмма
     * автоматически сортирует пункты, таким образом, абонент может передать
     * несортированный массив.
     *
     * @param x - сплайн узлы, массив [0 .. N-1]
     * @param y - значения функции, массив [0 .. N-1]
     * @param n - количество пунктов (необязательно): N> = 5 Если дано, только
     * первые очки N используются для построения сплайна Если не указаны,
     * автоматически определяются из X / Y Размеры (LEN (X) должна быть равна
     * длина (Y))
     * @param c - сплайн интерполянт
     */
    public static void spline1dbuildakima1(double[] x,
            double[] y,
            int n,
            spline1dinterpolant c) {
        int i = 0;
        double[] d = new double[0];
        double[] w = new double[0];
        double[] diff = new double[0];

        x = (double[]) x.clone();
        y = (double[]) y.clone();

        ap.assertJ(n >= 5, "Spline1DBuildAkima: N<5!");
        ap.assertJ(ap.len(x) >= n, "Spline1DBuildAkima: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DBuildAkima: Length(Y)<N!");

        //
        // check and sort points
        //
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DBuildAkima: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, n), "Spline1DBuildAkima: Y contains infinite or NAN values!");
        heapsortpoints(x, y, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DBuildAkima: at least two consequent points are too close!");

        //
        // Prepare W (weights), Diff (divided differences)
        //
        w = new double[n - 1];
        diff = new double[n - 1];
        for (i = 0; i <= n - 2; i++) {
            diff[i] = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
        }
        for (i = 1; i <= n - 2; i++) {
            w[i] = Math.abs(diff[i] - diff[i - 1]);
        }

        //
        // Prepare Hermite interpolation scheme
        //
        d = new double[n];
        for (i = 2; i <= n - 3; i++) {
            if ((double) (Math.abs(w[i - 1]) + Math.abs(w[i + 1])) != (double) (0)) {
                d[i] = (w[i + 1] * diff[i - 1] + w[i - 1] * diff[i]) / (w[i + 1] + w[i - 1]);
            } else {
                d[i] = ((x[i + 1] - x[i]) * diff[i - 1] + (x[i] - x[i - 1]) * diff[i]) / (x[i + 1] - x[i - 1]);
            }
        }
        d[0] = diffthreepoint(x[0], x[0], y[0], x[1], y[1], x[2], y[2]);
        d[1] = diffthreepoint(x[1], x[0], y[0], x[1], y[1], x[2], y[2]);
        d[n - 2] = diffthreepoint(x[n - 2], x[n - 3], y[n - 3], x[n - 2], y[n - 2], x[n - 1], y[n - 1]);
        d[n - 1] = diffthreepoint(x[n - 1], x[n - 3], y[n - 3], x[n - 2], y[n - 2], x[n - 1], y[n - 1]);

        //
        // Build Akima spline using Hermite interpolation scheme
        //
        spline1dbuildhermite(x, y, d, n, c);
    }

    /**
     * Внутренние подпрограммы. Три точки дифференциации
     *
     * @param t
     * @param x0
     * @param f0
     * @param x1
     * @param f1
     * @param x2
     * @param f2
     *
     * @return
     */
    private static double diffthreepoint(double t,
            double x0,
            double f0,
            double x1,
            double f1,
            double x2,
            double f2) {
        double result = 0;
        double a = 0;
        double b = 0;

        t = t - x0;
        x1 = x1 - x0;
        x2 = x2 - x0;
        a = (f2 - f0 - x2 / x1 * (f1 - f0)) / (math.sqr(x2) - x1 * x2);
        b = (f1 - f0 - a * math.sqr(x1)) / x1;
        result = 2 * a * t + b;
        return result;
    }

    /**
     * ***********************************************************************
     * This subroutine calculates the value of the spline at the given point X.
     *
     * INPUT PARAMETERS: C - spline interpolant X - point
     *
     * Result: S(x)
     *
     * -- ALGLIB PROJECT -- Copyright 23.06.2007 by Bochkanov Sergey
     ************************************************************************
     */
    public static double spline1dcalc(spline1dinterpolant c,
            double x) {
        double result = 0;
        int l = 0;
        int r = 0;
        int m = 0;
        double t = 0;

        ap.assertJ(c.k == 3, "Spline1DCalc: internal error");
        ap.assertJ(!Double.isInfinite(x), "Spline1DCalc: infinite X!");

        //
        // special case: NaN
        //
        if (Double.isNaN(x)) {
            result = Double.NaN;
            return result;
        }

        //
        // correct if periodic
        //
        if (c.periodic) {
            apserv.apperiodicmap(x, c.x[0], c.x[c.n - 1], t);
        }

        //
        // Binary search in the [ x[0], ..., x[n-2] ] (x[n-1] is not included)
        //
        l = 0;
        r = c.n - 2 + 1;
        while (l != r - 1) {
            m = (l + r) / 2;
            if (c.x[m] >= x) {
                r = m;
            } else {
                l = m;
            }
        }

        //
        // Interpolation
        //
        x = x - c.x[l];
        m = 4 * l;
        result = c.c[m] + x * (c.c[m + 1] + x * (c.c[m + 2] + x * c.c[m + 3]));
        return result;
    }

    public static void spline1ddiff(spline1dinterpolant c, double x, double s, double ds, double d2s) {
        s = 0;
        ds = 0;
        d2s = 0;
        spline1d.spline1ddiff(c, x, s, ds, d2s);
        return;
    }

    public static double spline1dintegrate(spline1dinterpolant c,
            double x) {
        double result = 0;
        int n = 0;
        int i = 0;
        int j = 0;
        int l = 0;
        int r = 0;
        int m = 0;
        double w = 0;
        double v = 0;
        double t = 0;
        double intab = 0;
        double additionalterm = 0;

        n = c.n;

        //
        // Periodic splines require special treatment. We make
        // following transformation:
        //
        //     integral(S(t)dt,A,X) = integral(S(t)dt,A,Z)+AdditionalTerm
        //
        // here X may lie outside of [A,B], Z lies strictly in [A,B],
        // AdditionalTerm is equals to integral(S(t)dt,A,B) times some
        // integer number (may be zero).
        //
        if (c.periodic & ((double) (x) < (double) (c.x[0]) | (double) (x) > (double) (c.x[c.n - 1]))) {

            //
            // compute integral(S(x)dx,A,B)
            //
            intab = 0;
            for (i = 0; i <= c.n - 2; i++) {
                w = c.x[i + 1] - c.x[i];
                m = (c.k + 1) * i;
                intab = intab + c.c[m] * w;
                v = w;
                for (j = 1; j <= c.k; j++) {
                    v = v * w;
                    intab = intab + c.c[m + j] * v / (j + 1);
                }
            }

            //
            // map X into [A,B]
            //
            apserv.apperiodicmap(x, c.x[0], c.x[c.n - 1], t);
            additionalterm = t * intab;
        } else {
            additionalterm = 0;
        }

        //
        // Binary search in the [ x[0], ..., x[n-2] ] (x[n-1] is not included)
        //
        l = 0;
        r = n - 2 + 1;
        while (l != r - 1) {
            m = (l + r) / 2;
            if ((double) (c.x[m]) >= (double) (x)) {
                r = m;
            } else {
                l = m;
            }
        }

        //
        // интегрирует
        //
        result = 0;
        for (i = 0; i <= l - 1; i++) {
            w = c.x[i + 1] - c.x[i];
            m = (c.k + 1) * i;
            result = result + c.c[m] * w;
            v = w;
            for (j = 1; j <= c.k; j++) {
                v = v * w;
                result = result + c.c[m + j] * v / (j + 1);
            }
        }
        w = x - c.x[l];
        m = (c.k + 1) * l;
        v = w;
        result = result + c.c[m] * w;
        for (j = 1; j <= c.k; j++) {
            v = v * w;
            result = result + c.c[m + j] * v / (j + 1);
        }
        result = result + additionalterm;
        return result;
    }

    public static void spline1dlintransx(spline1dinterpolant c,
            double a,
            double b) {
        int i = 0;
        int j = 0;
        int n = 0;
        double v = 0;
        double dv = 0;
        double d2v = 0;
        double[] x = new double[0];
        double[] y = new double[0];
        double[] d = new double[0];

        n = c.n;

        //
        // Special case: A=0
        //
        if ((double) (a) == (double) (0)) {
            v = spline1dcalc(c, b);
            for (i = 0; i <= n - 2; i++) {
                c.c[(c.k + 1) * i] = v;
                for (j = 1; j <= c.k; j++) {
                    c.c[(c.k + 1) * i + j] = 0;
                }
            }
            return;
        }

        //
        // General case: A<>0.
        // Unpack, X, Y, dY/dX.
        // Scale and pack again.
        //
        ap.assertJ(c.k == 3, "Spline1DLinTransX: internal error");
        x = new double[n - 1 + 1];
        y = new double[n - 1 + 1];
        d = new double[n - 1 + 1];
        for (i = 0; i <= n - 1; i++) {
            x[i] = c.x[i];
            spline1ddiff(c, x[i], v, dv, d2v);
            x[i] = (x[i] - b) / a;
            y[i] = v;
            d[i] = a * dv;
        }
        spline1dbuildhermite(x, y, d, n, c);
    }

    public static void spline1dlintransy(spline1dinterpolant c,
            double a,
            double b) {
        int i = 0;
        int j = 0;
        int n = 0;

        n = c.n;
        for (i = 0; i <= n - 2; i++) {
            c.c[(c.k + 1) * i] = a * c.c[(c.k + 1) * i] + b;
            for (j = 1; j <= c.k; j++) {
                c.c[(c.k + 1) * i + j] = a * c.c[(c.k + 1) * i + j];
            }
        }
    }

    public static void spline1dunpack(spline1dinterpolant c, int n, double[][] tbl) {
        n = 0;
        tbl = new double[0][0];
        spline1d.spline1dunpack(c, n, tbl);
        return;
    }

    /**
     *
     * @param x
     * @param y
     * @param n
     * @param boundltype
     * @param boundl
     * @param boundrtype
     * @param boundr
     * @param x2
     * @param n2
     * @param y2
     */
    public static void spline1dconvcubic(double[] x,
            double[] y,
            int n,
            int boundltype,
            double boundl,
            int boundrtype,
            double boundr,
            double[] x2,
            int n2,
            double[] y2) {
        double[] a1 = new double[0];
        double[] a2 = new double[0];
        double[] a3 = new double[0];
        double[] b = new double[0];
        double[] d = new double[0];
        double[] dt = new double[0];
        double[] d1 = new double[0];
        double[] d2 = new double[0];
        int[] p = new int[0];
        int[] p2 = new int[0];
        int i = 0;
        int ylen = 0;
        double t = 0;
        double t2 = 0;
        int i_ = 0;

        x = (double[]) x.clone();
        y = (double[]) y.clone();
        x2 = (double[]) x2.clone();
        y2 = new double[0];


        //
        // выбирает граничный условия
        //
        ap.assertJ(((boundltype == -1 | boundltype == 0) | boundltype == 1) | boundltype == 2, "Spline1DConvCubic: incorrect BoundLType!");
        ap.assertJ(((boundrtype == -1 | boundrtype == 0) | boundrtype == 1) | boundrtype == 2, "Spline1DConvCubic: incorrect BoundRType!");
        ap.assertJ((boundrtype == -1 & boundltype == -1) | (boundrtype != -1 & boundltype != -1), "Spline1DConvCubic: incorrect BoundLType/BoundRType!");
        if (boundltype == 1 | boundltype == 2) {
            ap.assertJ(math.isfinite(boundl), "Spline1DConvCubic: BoundL is infinite or NAN!");
        }
        if (boundrtype == 1 | boundrtype == 2) {
            ap.assertJ(math.isfinite(boundr), "Spline1DConvCubic: BoundR is infinite or NAN!");
        }

        //
        // выбирает длину аргумента
        //
        ap.assertJ(n >= 2, "Spline1DConvCubic: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DConvCubic: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DConvCubic: Length(Y)<N!");
        ap.assertJ(n2 >= 2, "Spline1DConvCubic: N2<2!");
        ap.assertJ(ap.len(x2) >= n2, "Spline1DConvCubic: Length(X2)<N2!");

        //
        // check and sort X/Y
        //
        ylen = n;
        if (boundltype == -1) {
            ylen = n - 1;
        }
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DConvCubic: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, ylen), "Spline1DConvCubic: Y contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(x2, n2), "Spline1DConvCubic: X2 contains infinite or NAN values!");
        heapsortppoints(x, y, p, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DConvCubic: at least two consequent points are too close!");

        //
        // set up DT (we will need it below)
        //
        dt = new double[Math.max(n, n2)];

        //
        // sort X2:
        // * use fake array DT because HeapSortPPoints() needs both integer AND real arrays
        // * if we have periodic problem, wrap points
        // * sort them, store permutation at P2
        //
        if (boundrtype == -1 & boundltype == -1) {
            for (i = 0; i <= n2 - 1; i++) {
                t = x2[i];
                apserv.apperiodicmap(t, x[0], x[n - 1], t2);
                x2[i] = t;
            }
        }
        heapsortppoints(x2, dt, p2, n2);


        //
        // Now we've checked and preordered everything, so we:
        // * call internal GridDiff() function to get Hermite form of spline
        // * convert using internal Conv() function
        // * convert Y2 back to original order
        //
        spline1dgriddiffcubicinternal(x, y, n, boundltype, boundl, boundrtype, boundr, d, a1, a2, a3, b, dt);
        spline1dconvdiffinternal(x, y, d, n, x2, n2, y2, true, d1, false, d2, false);
        ap.assertJ(ap.len(dt) >= n2, "Spline1DConvCubic: internal error!");
        for (i = 0; i <= n2 - 1; i++) {
            dt[pp2[i]] = yy2[i];
        }
        for (i_ = 0; i_ <= n2 - 1; i_++) {
            yy2[i_] = dt[i_];
        }
    }

    public static void spline1dconvcubic(double[] x, double[] y, double[] x2, double[] y2) {
        int n;
        int boundltype;
        double boundl;
        int boundrtype;
        double boundr;
        int n2;
        if ((ap.len(x) != ap.len(y))) //throw new alglibexception("Error while calling 'spline1dconvcubic': looks like one of arguments has wrong size");
        {
            y2 = new double[0];
        }
        n = ap.len(x);
        boundltype = 0;
        boundl = 0;
        boundrtype = 0;
        boundr = 0;
        n2 = ap.len(x2);
        spline1d.spline1dconvcubic(x, y, n, boundltype, boundl, boundrtype, boundr, x2, n2, y2);

        return;
    }

    /**
     * ***********************************************************************
     * Internal version of Spline1DGridDiffCubic.
     *
     * Accepts pre-ordered X/Y, temporary arrays (which may be preallocated, if
     * you want to save time, or not) and output array (which may be
     * preallocated too).
     *
     * Y is passed as var-parameter because we may need to force last element to
     * be equal to the first one (if periodic boundary conditions are
     * specified).
     *
     * -- ALGLIB PROJECT -- Copyright 03.09.2010 by Bochkanov Sergey
     ************************************************************************
     */
    /**
     * ***********************************************************************
     * Internal version of Spline1DConvDiff
     *
     * Converts from Hermite spline given by grid XOld to new grid X2
     *
     * INPUT PARAMETERS: XOld - old grid YOld - values at old grid DOld - first
     * derivative at old grid N - grid size X2 - new grid N2 - new grid size Y -
     * possibly preallocated output array (reallocate if too small) NeedY - do
     * we need Y? D1 - possibly preallocated output array (reallocate if too
     * small) NeedD1 - do we need D1? D2 - possibly preallocated output array
     * (reallocate if too small) NeedD2 - do we need D1?
     *
     * OUTPUT ARRAYS: Y - values, if needed D1 - first derivative, if needed D2
     * - second derivative, if needed
     *
     * -- ALGLIB PROJECT -- Copyright 03.09.2010 by Bochkanov Sergey
     ************************************************************************
     */
    public static void spline1dconvdiffinternal(double[] xold,
            double[] yold,
            double[] dold,
            int n,
            double[] x2,
            int n2,
            double[] y,
            boolean needy,
            double[] d1,
            boolean needd1,
            double[] d2,
            boolean needd2) {
        int intervalindex = 0;
        int pointindex = 0;
        boolean havetoadvance = false;
        double c0 = 0;
        double c1 = 0;
        double c2 = 0;
        double c3 = 0;
        double a = 0;
        double b = 0;
        double w = 0;
        double w2 = 0;
        double w3 = 0;
        double fa = 0;
        double fb = 0;
        double da = 0;
        double db = 0;
        double t = 0;


        //
        // Prepare space
        //
        if (needy & ap.len(y) < n2) {
            y = new double[n2];
        }
        if (needd1 & ap.len(d1) < n2) {
            d1 = new double[n2];
        }
        if (needd2 & ap.len(d2) < n2) {
            d2 = new double[n2];
        }
        dd1 = new double[n2];
        dd2 = new double[n2];
        //
        // These assignments aren't actually needed
        // (variables are initialized in the loop below),
        // but without them compiler will complain about uninitialized locals
        //
        c0 = 0;
        c1 = 0;
        c2 = 0;
        c3 = 0;
        a = 0;
        b = 0;

        //
        // Cycle
        //
        intervalindex = -1;
        pointindex = 0;
        while (true) {

            //
            // are we ready to exit?
            //
            if (pointindex >= n2) {
                break;
            }
            t = x2[pointindex];

            //
            // do we need to advance interval?
            //
            havetoadvance = false;
            if (intervalindex == -1) {
                havetoadvance = true;
            } else {
                if (intervalindex < n - 2) {
                    havetoadvance = (double) (t) >= (double) (b);
                }
            }
            if (havetoadvance) {
                intervalindex = intervalindex + 1;
                a = xold[intervalindex];
                b = xold[intervalindex + 1];
                w = b - a;
                w2 = w * w;
                w3 = w * w2;
                fa = yold[intervalindex];
                fb = yold[intervalindex + 1];
                da = dd[intervalindex];
                db = dd[intervalindex + 1];
                c0 = fa;
                c1 = da;
                c2 = (3 * (fb - fa) - 2 * da * w - db * w) / w2;
                c3 = (2 * (fa - fb) + da * w + db * w) / w3;
                continue;
            }

            //
            // Calculate spline and its derivatives using power basis
            //
            t = t - a;
            if (needy) {
                y[pointindex] = c0 + t * (c1 + t * (c2 + t * c3));
            }
            if (needd1) {
                dd1[pointindex] = c1 + 2 * t * c2 + 3 * t * t * c3;
            }
            if (needd2) {
                dd2[pointindex] = 2 * c2 + 6 * t * c3;
            }
            pointindex = pointindex + 1;
        }
        yy2 = y;
    }

    private static void spline1dgriddiffcubicinternal(double[] x,
            double[] y,
            int n,
            int boundltype,
            double boundl,
            int boundrtype,
            double boundr,
            double[] d,
            double[] a1,
            double[] a2,
            double[] a3,
            double[] b,
            double[] dt) {
        int i = 0;
        int i_ = 0;


        //
        // allocate arrays
        //
        if (ap.len(d) < n) {
            d = new double[n];
        }
        if (ap.len(a1) < n) {
            a1 = new double[n];
        }
        if (ap.len(a2) < n) {
            a2 = new double[n];
        }
        if (ap.len(a3) < n) {
            a3 = new double[n];
        }
        if (ap.len(b) < n) {
            b = new double[n];
        }
        if (ap.len(dt) < n) {
            dt = new double[n];
        }

        //
        // Special cases:
        // * N=2, parabolic terminated boundary condition on both ends
        // * N=2, periodic boundary condition
        //
        if ((n == 2 & boundltype == 0) & boundrtype == 0) {
            d[0] = (y[1] - y[0]) / (x[1] - x[0]);
            d[1] = d[0];
            dd1 = d;
            return;
        }
        if ((n == 2 & boundltype == -1) & boundrtype == -1) {
            d[0] = 0;
            d[1] = 0;
            dd1 = d;
            return;
        }

        //
        // Periodic and non-periodic boundary conditions are
        // two separate classes
        //
        if (boundrtype == -1 & boundltype == -1) {

            //
            // Periodic boundary conditions
            //
            y[n - 1] = y[0];

            //
            // Boundary conditions at N-1 points
            // (one point less because last point is the same as first point).
            //
            a1[0] = x[1] - x[0];
            a2[0] = 2 * (x[1] - x[0] + x[n - 1] - x[n - 2]);
            a3[0] = x[n - 1] - x[n - 2];
            b[0] = 3 * (y[n - 1] - y[n - 2]) / (x[n - 1] - x[n - 2]) * (x[1] - x[0]) + 3 * (y[1] - y[0]) / (x[1] - x[0]) * (x[n - 1] - x[n - 2]);
            for (i = 1; i <= n - 2; i++) {

                //
                // Altough last point is [N-2], we use X[N-1] and Y[N-1]
                // (because of periodicity)
                //
                a1[i] = x[i + 1] - x[i];
                a2[i] = 2 * (x[i + 1] - x[i - 1]);
                a3[i] = x[i] - x[i - 1];
                b[i] = 3 * (y[i] - y[i - 1]) / (x[i] - x[i - 1]) * (x[i + 1] - x[i]) + 3 * (y[i + 1] - y[i]) / (x[i + 1] - x[i]) * (x[i] - x[i - 1]);
            }

            //
            // Solve, add last point (with index N-1)
            //
            solvecyclictridiagonal(a1, a2, a3, b, n - 1, dt);
            for (i_ = 0; i_ <= n - 2; i_++) {
                d[i_] = dt[i_];
            }
            d[n - 1] = d[0];
            dd1 = d;
        } else {

            //
            // Non-periodic boundary condition.
            // Left boundary conditions.
            //
            if (boundltype == 0) {
                a1[0] = 0;
                a2[0] = 1;
                a3[0] = 1;
                b[0] = 2 * (y[1] - y[0]) / (x[1] - x[0]);
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
                b[0] = 3 * (y[1] - y[0]) / (x[1] - x[0]) - 0.5 * boundl * (x[1] - x[0]);
            }

            //
            // Central conditions
            //
            for (i = 1; i <= n - 2; i++) {
                a1[i] = x[i + 1] - x[i];
                a2[i] = 2 * (x[i + 1] - x[i - 1]);
                a3[i] = x[i] - x[i - 1];
                b[i] = 3 * (y[i] - y[i - 1]) / (x[i] - x[i - 1]) * (x[i + 1] - x[i]) + 3 * (y[i + 1] - y[i]) / (x[i + 1] - x[i]) * (x[i] - x[i - 1]);
            }

            //
            // Right boundary conditions
            //
            if (boundrtype == 0) {
                a1[n - 1] = 1;
                a2[n - 1] = 1;
                a3[n - 1] = 0;
                b[n - 1] = 2 * (y[n - 1] - y[n - 2]) / (x[n - 1] - x[n - 2]);
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
                b[n - 1] = 3 * (y[n - 1] - y[n - 2]) / (x[n - 1] - x[n - 2]) + 0.5 * boundr * (x[n - 1] - x[n - 2]);
            }

            //
            // Solve
            //!!!!!!!!!!!!!!!!!!!!!!!!!!

            solvetridiagonal(a1, a2, a3, b, n, d);
            dd = d;
            dd1 = d;
        }
    }

    /**
     * ***********************************************************************
     * Internal subroutine. Tridiagonal solver. Solves
     *
     * ( B[0] C[0] ) ( A[1] B[1] C[1] ) ( A[2] B[2] C[2] ) ( .......... ) * X =
     * D ( .......... ) ( A[N-2] B[N-2] C[N-2] ) ( A[N-1] B[N-1] )
     *
     ************************************************************************
     */
    private static void solvetridiagonal(double[] a,
            double[] b,
            double[] c,
            double[] d,
            int n,
            double[] x) {
        int k = 0;
        double t = 0;

        b = (double[]) b.clone();
        d = (double[]) d.clone();

        if (ap.len(x) < n) {
            x = new double[n];
        }
        for (k = 1; k <= n - 1; k++) {
            t = a[k] / b[k - 1];
            b[k] = b[k] - t * c[k - 1];
            d[k] = d[k] - t * d[k - 1];
        }
        x[n - 1] = d[n - 1] / b[n - 1];
        for (k = n - 2; k >= 0; k--) {
            x[k] = (d[k] - c[k] * x[k + 1]) / b[k];
        }
    }

    /**
     * ***********************************************************************
     * Internal subroutine. Heap sort.
     *
     * Accepts: X, Y - points P - empty or preallocated array
     *
     * Returns: X, Y - sorted by X P - array of permutations; I-th position of
     * output arrays X/Y contains (X[P[I]],Y[P[I]])
     ************************************************************************
     */
    private static void solvecyclictridiagonal(double[] a,
            double[] b,
            double[] c,
            double[] d,
            int n,
            double[] x) {
        int k = 0;
        double alpha = 0;
        double beta = 0;
        double gamma = 0;
        double[] y = new double[0];
        double[] z = new double[0];
        double[] u = new double[0];

        b = (double[]) b.clone();

        if (ap.len(x) < n) {
            x = new double[n];
        }
        beta = a[0];
        alpha = c[n - 1];
        gamma = -b[0];
        b[0] = 2 * b[0];
        b[n - 1] = b[n - 1] - alpha * beta / gamma;
        u = new double[n];
        for (k = 0; k <= n - 1; k++) {
            u[k] = 0;
        }
        u[0] = gamma;
        u[n - 1] = alpha;
        solvetridiagonal(a, b, c, d, n, y);
        solvetridiagonal(a, b, c, u, n, z);
        for (k = 0; k <= n - 1; k++) {
            x[k] = y[k] - (y[0] + beta / gamma * y[n - 1]) / (1 + z[0] + beta / gamma * z[n - 1]) * z[k];
        }
    }

    private static void heapsortppoints(double[] x,
            double[] y,
            int[] p,
            int n) {
        double[] rbuf = new double[0];
        int[] ibuf = new int[0];
        int i = 0;
        int i_ = 0;

        if (ap.len(p) < n) {
            p = new int[n];
        }
        rbuf = new double[n];
        for (i = 0; i <= n - 1; i++) {
            p[i] = i;
        }
        pp = p;
        pp2 = p;
        tsort.tagsortfasti(x, p, rbuf, ibuf, n);
        for (i = 0; i <= n - 1; i++) {
            rbuf[i] = y[p[i]];
        }
        for (i_ = 0; i_ <= n - 1; i_++) {
            y[i_] = rbuf[i_];
        }
    }

    public static void spline1dconvdiffcubic(double[] x, double[] y, int n, int boundltype, double boundl, int boundrtype, double boundr, double[] x2, int n2, double[] y2, double[] d2) {
        y2 = new double[0];
        d2 = new double[0];
        spline1d.spline1dconvdiffcubic2(x, y, n, boundltype, boundl, boundrtype, boundr, x2, n2, y2, d2);
        return;
    }

    public static void spline1dconvdiffcubic2(double[] x,
            double[] y,
            int n,
            int boundltype,
            double boundl,
            int boundrtype,
            double boundr,
            double[] x2,
            int n2,
            double[] y2,
            double[] d2) {
        double[] a1 = new double[0];
        double[] a2 = new double[0];
        double[] a3 = new double[0];
        double[] b = new double[0];
        double[] d = new double[0];
        double[] dt = new double[0];
        double[] rt1 = new double[0];
        int[] p = new int[0];
        int[] p2 = new int[0];
        int i = 0;
        int ylen = 0;
        double t = 0;
        double t2 = 0;
        int i_ = 0;

        x = (double[]) x.clone();
        y = (double[]) y.clone();
        x2 = (double[]) x2.clone();
        y2 = new double[0];
        d2 = new double[0];


        //
        // check correctness of boundary conditions
        //
        ap.assertJ(((boundltype == -1 | boundltype == 0) | boundltype == 1) | boundltype == 2, "Spline1DConvDiffCubic: incorrect BoundLType!");
        ap.assertJ(((boundrtype == -1 | boundrtype == 0) | boundrtype == 1) | boundrtype == 2, "Spline1DConvDiffCubic: incorrect BoundRType!");
        ap.assertJ((boundrtype == -1 & boundltype == -1) | (boundrtype != -1 & boundltype != -1), "Spline1DConvDiffCubic: incorrect BoundLType/BoundRType!");
        if (boundltype == 1 | boundltype == 2) {
            ap.assertJ(math.isfinite(boundl), "Spline1DConvDiffCubic: BoundL is infinite or NAN!");
        }
        if (boundrtype == 1 | boundrtype == 2) {
            ap.assertJ(math.isfinite(boundr), "Spline1DConvDiffCubic: BoundR is infinite or NAN!");
        }

        //
        // check lengths of arguments
        //
        ap.assertJ(n >= 2, "Spline1DConvDiffCubic: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DConvDiffCubic: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DConvDiffCubic: Length(Y)<N!");
        ap.assertJ(n2 >= 2, "Spline1DConvDiffCubic: N2<2!");
        ap.assertJ(ap.len(x2) >= n2, "Spline1DConvDiffCubic: Length(X2)<N2!");

        //
        // check and sort X/Y
        //
        ylen = n;
        if (boundltype == -1) {
            ylen = n - 1;
        }
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DConvDiffCubic: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, ylen), "Spline1DConvDiffCubic: Y contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(x2, n2), "Spline1DConvDiffCubic: X2 contains infinite or NAN values!");
        heapsortppoints(x, y, p, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DConvDiffCubic: at least two consequent points are too close!");

        //
        // set up DT (we will need it below)
        //
        dt = new double[Math.max(n, n2)];

        //
        // sort X2:
        // * use fake array DT because HeapSortPPoints() needs both integer AND real arrays
        // * if we have periodic problem, wrap points
        // * sort them, store permutation at P2
        //
        if (boundrtype == -1 & boundltype == -1) {
            for (i = 0; i <= n2 - 1; i++) {
                t = x2[i];
                apserv.apperiodicmap(t, x[0], x[n - 1], t2);
                x2[i] = t;
            }
        }
        heapsortppoints(x2, dt, p2, n2);

        //
        // Now we've checked and preordered everything, so we:
        // * call internal GridDiff() function to get Hermite form of spline
        // * convert using internal Conv() function
        // * convert Y2 back to original order
        //
        spline1dgriddiffcubicinternal(x, y, n, boundltype, boundl, boundrtype, boundr, d, a1, a2, a3, b, dt);
        spline1dconvdiffinternal(x, y, d, n, x2, n2, y2, true, d2, true, rt1, false);
        ap.assertJ(ap.len(dt) >= n2, "Spline1DConvDiffCubic: internal error!");
        for (i = 0; i <= n2 - 1; i++) {
            dt[pp2[i]] = yy2[i];
        }
        for (i_ = 0; i_ <= n2 - 1; i_++) {
            yy2[i_] = dt[i_];
        }
        for (i = 0; i <= n2 - 1; i++) {
            dt[pp2[i]] = dd2[i];
        }
        for (i_ = 0; i_ <= n2 - 1; i_++) {
            dd2[i_] = dt[i_];
        }
    }

    public static void spline1dconvdiffcubic(double[] x, double[] y, double[] x2, double[] y2, double[] d2) {
        int n;
        int boundltype;
        double boundl;
        int boundrtype;
        double boundr;
        int n2;
        if ((ap.len(x) != ap.len(y))) //  throw new alglibexception("Error while calling 'spline1dconvdiffcubic': looks like one of arguments has wrong size");
        {
            y2 = new double[0];
        }
        d2 = new double[0];
        n = ap.len(x);
        boundltype = 0;
        boundl = 0;
        boundrtype = 0;
        boundr = 0;
        n2 = ap.len(x2);
        spline1d.spline1dconvdiffcubic(x, y, n, boundltype, boundl, boundrtype, boundr, x2, n2, y2, d2);

        return;
    }

    public static void spline1dconvdiff2cubic(double[] x, double[] y, int n, int boundltype, double boundl, int boundrtype, double boundr, double[] x2, int n2, double[] y2, double[] d2, double[] dd2) {
        y2 = new double[0];
        d2 = new double[0];
        dd2 = new double[0];
        spline1d.spline1dconvdiff2cubic3(x, y, n, boundltype, boundl, boundrtype, boundr, x2, n2, y2, d2, dd2);
        return;
    }

    /**
     * ***********************************************************************
     * This function solves following problem: given table y[] of function
     * values at old nodes x[] and new nodes x2[], it calculates and returns
     * table of function values y2[], first and second derivatives d2[] and
     * dd2[] (calculated at x2[]).
     *
     * This function yields same result as Spline1DBuildCubic() call followed by
     * sequence of Spline1DDiff() calls, but it can be several times faster when
     * called for ordered X[] and X2[].
     *
     * INPUT PARAMETERS: X - old spline nodes Y - function values X2 - new
     * spline nodes
     *
     * OPTIONAL PARAMETERS: N - points count: N>=2 if given, only first N points
     * from X/Y are used if not given, automatically detected from X/Y sizes
     * (len(X) must be equal to len(Y)) BoundLType - boundary condition type for
     * the left boundary BoundL - left boundary condition (first or second
     * derivative, depending on the BoundLType) BoundRType - boundary condition
     * type for the right boundary BoundR - right boundary condition (first or
     * second derivative, depending on the BoundRType) N2 - new points count:
     * N2>=2 if given, only first N2 points from X2 are used if not given,
     * automatically detected from X2 size
     *
     * OUTPUT PARAMETERS: F2 - function values at X2[] D2 - first derivatives at
     * X2[] DD2 - second derivatives at X2[]
     *
     * ORDER OF POINTS
     *
     * Subroutine automatically sorts points, so caller may pass unsorted array.
     * Function values are correctly reordered on return, so F2[I] is always
     * equal to S(X2[I]) independently of points order.
     *
     * SETTING BOUNDARY VALUES:
     *
     * The BoundLType/BoundRType parameters can have the following values: -1,
     * which corresonds to the periodic (cyclic) boundary conditions. In this
     * case: both BoundLType and BoundRType must be equal to -1. BoundL/BoundR
     * are ignored Y[last] is ignored (it is assumed to be equal to Y[first]).
     * 0, which corresponds to the parabolically terminated spline (BoundL
     * and/or BoundR are ignored). 1, which corresponds to the first derivative
     * boundary condition 2, which corresponds to the second derivative boundary
     * condition by default, BoundType=0 is used
     *
     * PROBLEMS WITH PERIODIC BOUNDARY CONDITIONS:
     *
     * Problems with periodic boundary conditions have
     * Y[first_point]=Y[last_point]. However, this subroutine doesn't require
     * you to specify equal values for the first and last points - it
     * automatically forces them to be equal by copying Y[first_point]
     * (corresponds to the leftmost, minimal X[]) to Y[last_point]. However it
     * is recommended to pass consistent values of Y[], i.e. to make
     * Y[first_point]=Y[last_point].
     *
     * -- ALGLIB PROJECT -- Copyright 03.09.2010 by Bochkanov Sergey
     ************************************************************************
     */
    public static void spline1dconvdiff2cubic3(double[] x,
            double[] y,
            int n,
            int boundltype,
            double boundl,
            int boundrtype,
            double boundr,
            double[] x2,
            int n2,
            double[] y21,
            double[] d21,
            double[] dd21) {
        double[] a1 = new double[0];
        double[] a2 = new double[0];
        double[] a3 = new double[0];
        double[] b = new double[0];
        double[] d = new double[0];
        double[] dt = new double[0];
        int[] p = new int[0];
        int[] p2 = new int[0];
        int i = 0;
        int ylen = 0;
        double t = 0;
        double t2 = 0;
        int i_ = 0;

        x = (double[]) x.clone();
        y = (double[]) y.clone();
        x2 = (double[]) x2.clone();
        y21 = new double[0];
        d21 = new double[0];
        dd21 = new double[0];


        //
        // check correctness of boundary conditions
        //
        ap.assertJ(((boundltype == -1 | boundltype == 0) | boundltype == 1) | boundltype == 2, "Spline1DConvDiff2Cubic: incorrect BoundLType!");
        ap.assertJ(((boundrtype == -1 | boundrtype == 0) | boundrtype == 1) | boundrtype == 2, "Spline1DConvDiff2Cubic: incorrect BoundRType!");
        ap.assertJ((boundrtype == -1 & boundltype == -1) | (boundrtype != -1 & boundltype != -1), "Spline1DConvDiff2Cubic: incorrect BoundLType/BoundRType!");
        if (boundltype == 1 | boundltype == 2) {
            ap.assertJ(math.isfinite(boundl), "Spline1DConvDiff2Cubic: BoundL is infinite or NAN!");
        }
        if (boundrtype == 1 | boundrtype == 2) {
            ap.assertJ(math.isfinite(boundr), "Spline1DConvDiff2Cubic: BoundR is infinite or NAN!");
        }

        //
        // check lengths of arguments
        //
        ap.assertJ(n >= 2, "Spline1DConvDiff2Cubic: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DConvDiff2Cubic: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DConvDiff2Cubic: Length(Y)<N!");
        ap.assertJ(n2 >= 2, "Spline1DConvDiff2Cubic: N2<2!");
        ap.assertJ(ap.len(x2) >= n2, "Spline1DConvDiff2Cubic: Length(X2)<N2!");

        //
        // check and sort X/Y
        //
        ylen = n;
        if (boundltype == -1) {
            ylen = n - 1;
        }
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DConvDiff2Cubic: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, ylen), "Spline1DConvDiff2Cubic: Y contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(x2, n2), "Spline1DConvDiff2Cubic: X2 contains infinite or NAN values!");
        heapsortppoints(x, y, p, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DConvDiff2Cubic: at least two consequent points are too close!");

        //
        // set up DT (we will need it below)
        //
        dt = new double[Math.max(n, n2)];

        //
        // sort X2:
        // * use fake array DT because HeapSortPPoints() needs both integer AND real arrays
        // * if we have periodic problem, wrap points
        // * sort them, store permutation at P2
        //
        if (boundrtype == -1 & boundltype == -1) {
            for (i = 0; i <= n2 - 1; i++) {
                t = x2[i];
                apserv.apperiodicmap(t, x[0], x[n - 1], t2);
                x2[i] = t;
            }
        }
        heapsortppoints(x2, dt, p2, n2);

        //
        // Now we've checked and preordered everything, so we:
        // * call internal GridDiff() function to get Hermite form of spline
        // * convert using internal Conv() function
        // * convert Y2 back to original order
        //
        spline1dgriddiffcubicinternal(x, y, n, boundltype, boundl, boundrtype, boundr, d, a1, a2, a3, b, dt);
        spline1dconvdiffinternal(x, y, d, n, x2, n2, y21, true, d21, true, dd21, true);
        ap.assertJ(ap.len(dt) >= n2, "Spline1DConvDiff2Cubic: internal error!");
        for (i = 0; i <= n2 - 1; i++) {
            dt[pp2[i]] = yy2[i];
        }
        for (i_ = 0; i_ <= n2 - 1; i_++) {
            yy2[i_] = dt[i_];
        }
        for (i = 0; i <= n2 - 1; i++) {
            dt[pp2[i]] = dd2[i];
        }
        for (i_ = 0; i_ <= n2 - 1; i_++) {
            dd2[i_] = dt[i_];
        }
        for (i = 0; i <= n2 - 1; i++) {
            dt[pp2[i]] = dd2[i];
        }
        for (i_ = 0; i_ <= n2 - 1; i_++) {
            dd2[i_] = dt[i_];
        }
    }

    public static void spline1dconvdiff2cubic(double[] x, double[] y, double[] x2, double[] y2, double[] d2, double[] dd2) {
        int n;
        int boundltype;
        double boundl;
        int boundrtype;
        double boundr;
        int n2;
        if ((ap.len(x) != ap.len(y))) //throw new alglibexception("Error while calling 'spline1dconvdiff2cubic': looks like one of arguments has wrong size");
        {
            y2 = new double[0];
        }
        d2 = new double[0];
        dd2 = new double[0];
        n = ap.len(x);
        boundltype = 0;
        boundl = 0;
        boundrtype = 0;
        boundr = 0;
        n2 = ap.len(x2);
        spline1d.spline1dconvdiff2cubic(x, y, n, boundltype, boundl, boundrtype, boundr, x2, n2, y2, d2, dd2);

        return;
    }

    public static void spline1dgriddiffcubic(double[] x, double[] y, double[] d) {
        int n;
        int boundltype;
        double boundl;
        int boundrtype;
        double boundr;
        if ((ap.len(x) != ap.len(y))) // throw new alglibexception("Error while calling 'spline1dgriddiffcubic': looks like one of arguments has wrong size");
        {
            d = new double[0];
        }
        n = ap.len(x);
        boundltype = 0;
        boundl = 0;
        boundrtype = 0;
        boundr = 0;
        spline1d.spline1dgriddiffcubic(x, y, n, boundltype, boundl, boundrtype, boundr, d);

        return;
    }

    public static void spline1dgriddiff2cubic(double[] x, double[] y, double[] d1, double[] d2) {
        int n;
        int boundltype;
        double boundl;
        int boundrtype;
        double boundr;
        if ((ap.len(x) != ap.len(y))) //   throw new alglibexception("Error while calling 'spline1dgriddiff2cubic': looks like one of arguments has wrong size");
        {
            d1 = new double[0];
        }
        d2 = new double[0];
        n = ap.len(x);
        boundltype = 0;
        boundl = 0;
        boundrtype = 0;
        boundr = 0;
        spline1d.spline1dgriddiff2cubic(x, y, n, boundltype, boundl, boundrtype, boundr, d1, d2);

        return;
    }

    public static void spline1dgriddiffcubic(double[] x,
            double[] y,
            int n,
            int boundltype,
            double boundl,
            int boundrtype,
            double boundr,
            double[] d) {
        double[] a1 = new double[0];
        double[] a2 = new double[0];
        double[] a3 = new double[0];
        double[] b = new double[0];
        double[] dt = new double[0];
        int[] p = new int[0];
        int i = 0;
        int ylen = 0;
        int i_ = 0;

        x = (double[]) x.clone();
        y = (double[]) y.clone();
        d = new double[0];


        //
        // check correctness of boundary conditions
        //
        ap.assertJ(((boundltype == -1 | boundltype == 0) | boundltype == 1) | boundltype == 2, "Spline1DGridDiffCubic: incorrect BoundLType!");
        ap.assertJ(((boundrtype == -1 | boundrtype == 0) | boundrtype == 1) | boundrtype == 2, "Spline1DGridDiffCubic: incorrect BoundRType!");
        ap.assertJ((boundrtype == -1 & boundltype == -1) | (boundrtype != -1 & boundltype != -1), "Spline1DGridDiffCubic: incorrect BoundLType/BoundRType!");
        if (boundltype == 1 | boundltype == 2) {
            ap.assertJ(math.isfinite(boundl), "Spline1DGridDiffCubic: BoundL is infinite or NAN!");
        }
        if (boundrtype == 1 | boundrtype == 2) {
            ap.assertJ(math.isfinite(boundr), "Spline1DGridDiffCubic: BoundR is infinite or NAN!");
        }

        //
        // check lengths of arguments
        //
        ap.assertJ(n >= 2, "Spline1DGridDiffCubic: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DGridDiffCubic: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DGridDiffCubic: Length(Y)<N!");

        //
        // check and sort points
        //
        ylen = n;
        if (boundltype == -1) {
            ylen = n - 1;
        }
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DGridDiffCubic: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, ylen), "Spline1DGridDiffCubic: Y contains infinite or NAN values!");
        heapsortppoints(x, y, p, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DGridDiffCubic: at least two consequent points are too close!");

        //
        // Now we've checked and preordered everything,
        // so we can call internal function.
        //
        spline1dgriddiffcubicinternal(x, y, n, boundltype, boundl, boundrtype, boundr, d, a1, a2, a3, b, dt);

        //
        // Remember that HeapSortPPoints() call?
        // Now we have to reorder them back.
        //
        if (ap.len(dt) < n) {
            dt = new double[n];
        }
        for (i = 0; i <= n - 1; i++) {
            dt[pp[i]] = dd[i];
        }
        for (i_ = 0; i_ <= n - 1; i_++) {
            dd[i_] = dt[i_];
        }
    }

    public static void spline1dgriddiff2cubic(double[] x,
            double[] y,
            int n,
            int boundltype,
            double boundl,
            int boundrtype,
            double boundr,
            double[] d1,
            double[] d2) {
        double[] a1 = new double[0];
        double[] a2 = new double[0];
        double[] a3 = new double[0];
        double[] b = new double[0];
        double[] dt = new double[0];
        int[] p = new int[0];
        int i = 0;
        int ylen = 0;
        double delta = 0;
        double delta2 = 0;
        double delta3 = 0;
        double s0 = 0;
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;
        int i_ = 0;

        x = (double[]) x.clone();
        y = (double[]) y.clone();
        d1 = new double[0];
        d2 = new double[0];


        //
        // check correctness of boundary conditions
        //
        ap.assertJ(((boundltype == -1 | boundltype == 0) | boundltype == 1) | boundltype == 2, "Spline1DGridDiff2Cubic: incorrect BoundLType!");
        ap.assertJ(((boundrtype == -1 | boundrtype == 0) | boundrtype == 1) | boundrtype == 2, "Spline1DGridDiff2Cubic: incorrect BoundRType!");
        ap.assertJ((boundrtype == -1 & boundltype == -1) | (boundrtype != -1 & boundltype != -1), "Spline1DGridDiff2Cubic: incorrect BoundLType/BoundRType!");
        if (boundltype == 1 | boundltype == 2) {
            ap.assertJ(math.isfinite(boundl), "Spline1DGridDiff2Cubic: BoundL is infinite or NAN!");
        }
        if (boundrtype == 1 | boundrtype == 2) {
            ap.assertJ(math.isfinite(boundr), "Spline1DGridDiff2Cubic: BoundR is infinite or NAN!");
        }

        //
        // check lengths of arguments
        //
        ap.assertJ(n >= 2, "Spline1DGridDiff2Cubic: N<2!");
        ap.assertJ(ap.len(x) >= n, "Spline1DGridDiff2Cubic: Length(X)<N!");
        ap.assertJ(ap.len(y) >= n, "Spline1DGridDiff2Cubic: Length(Y)<N!");

        //
        // check and sort points
        //
        ylen = n;
        if (boundltype == -1) {
            ylen = n - 1;
        }
        ap.assertJ(apserv.isfinitevector(x, n), "Spline1DGridDiff2Cubic: X contains infinite or NAN values!");
        ap.assertJ(apserv.isfinitevector(y, ylen), "Spline1DGridDiff2Cubic: Y contains infinite or NAN values!");
        heapsortppoints(x, y, p, n);
        ap.assertJ(apserv.aredistinct(x, n), "Spline1DGridDiff2Cubic: at least two consequent points are too close!");

        //
        // Now we've checked and preordered everything,
        // so we can call internal function.
        //
        // After this call we will calculate second derivatives
        // (manually, by converting to the power basis)
        //
        spline1dgriddiffcubicinternal(x, y, n, boundltype, boundl, boundrtype, boundr, d1, a1, a2, a3, b, dt);
        d2 = new double[n];
        delta = 0;
        s2 = 0;
        s3 = 0;
        for (i = 0; i <= n - 2; i++) {

            //
            // We convert from Hermite basis to the power basis.
            // Si is coefficient before x^i.
            //
            // Inside this cycle we need just S2,
            // because we calculate S'' exactly at spline node,
            // (only x^2 matters at x=0), but after iterations
            // will be over, we will need other coefficients
            // to calculate spline value at the last node.
            //
            delta = x[i + 1] - x[i];
            delta2 = math.sqr(delta);
            delta3 = delta * delta2;
            s0 = y[i];
            s1 = dd1[i];
            s2 = (3 * (y[i + 1] - y[i]) - 2 * dd1[i] * delta - dd1[i + 1] * delta) / delta2;
            s3 = (2 * (y[i] - y[i + 1]) + dd1[i] * delta + dd1[i + 1] * delta) / delta3;
            d2[i] = 2 * s2;
        }
        d2[n - 1] = 2 * s2 + 6 * s3 * delta;

        //
        // Remember that HeapSortPPoints() call?
        // Now we have to reorder them back.
        //
        if (ap.len(dt) < n) {
            dt = new double[n];
        }
        for (i = 0; i <= n - 1; i++) {
            dt[pp[i]] = dd1[i];
        }
        for (i_ = 0; i_ <= n - 1; i_++) {
            dd1[i_] = dt[i_];
        }
        for (i = 0; i <= n - 1; i++) {
            dt[pp[i]] = d2[i];
        }
        for (i_ = 0; i_ <= n - 1; i_++) {
            d2[i_] = dt[i_];
        }
        dd2 = d2;
    }
}
