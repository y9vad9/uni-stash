/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.yakovleva;

import java.util.Random;

import static com.mathpar.students.OLD.stud2014.yakovleva.test_spline.doc_test_real;
import static com.mathpar.students.OLD.stud2014.yakovleva.test_spline.doc_test_real_vector;
import static com.mathpar.students.OLD.stud2014.yakovleva.test_spline.spline1d_d_linear;
import static com.mathpar.students.OLD.stud2014.yakovleva.test_spline.spoil_vector_by_value;
import static com.mathpar.students.OLD.stud2014.yakovleva.test_spline.value_spline;
import static com.mathpar.students.OLD.stud2014.yakovleva.test_spline.value_spline_cub;

/**
 *
 * @author yakovleva
 */
public class test_spline {

    public static void spoil_vector_by_value(double[] x, double val) {
        if (x.length != 0) {
            int i = new Random().nextInt(x.length);
            x[i] = val;
        }
    }

    /**
     * метод вычисления линейного сплайна в точке
     * @param s -объект, содержащий значения коэффициентов
     * @return
     */
    public static double[] value_spline(spline1dinterpolant s) {
        double[] res = new double[s.x.length - 1];
        double[] x = s.x;
        int l = 0;
        int k = 0;
        for (int i = 0; i < res.length; i++) {
            res[i] = s.c[k + 1] * x[l] + s.c[k];
            l++;
            k = k + 4;


        }
        return res;
    }

    public static double[] value_spline_cub(spline1dinterpolant s) {
        double[] res = new double[s.x.length - 1];
        double[] x = s.x;
        for (int i = 0; i < res.length; i++) {
            res[i] = spline1d.spline1dcalc(s, s.x[i]);
        }
        return res;
    }

    public static double[] spoil_vector_by_adding_element(double[] x) {
        double[] y = new double[x.length + 1];
        System.arraycopy(x, 0, y, 0, x.length);
        y[x.length] = 1.5;
        return y;
    }

    public static double[] spoil_vector_by_deleting_element(double[] x) {
        double[] y = new double[x.length - 1];
        for (int i = 0; i < x.length - 1; i++) {
            y[i] = x[i];
        }
        return y;
    }
    //
    // TEST spline1d_d_linear
    //      Piecewise linear spline interpolation
    //

    public static void spline1d_d_linear() {
        boolean _TestResult = true;
        boolean _TotalResult = true;
        for (int i = -1; i < 12; i++) {
            try {
                //
                // We use piecewise linear spline to interpolate f(x)=x^2 sampled
                // at 5 equidistant nodes on [-1,+1].
                //
                double[] x = new double[]{-1.0, -0.5, 0.0, +0.5, +1.0};
                if (i == 0) {
                    spoil_vector_by_value(x, (double) Double.NaN);
                }
                if (i == 1) {
                    spoil_vector_by_value(x, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 2) {
                    spoil_vector_by_value(x, (double) Double.NEGATIVE_INFINITY);
                }
//                    if( i==3 )
//                        x = spoil_vector_by_adding_element(x);
//                    if( i==4 )
//                        x = spoil_vector_by_deleting_element(x);
                double[] y = new double[]{+1.0, 0.25, 0.0, 0.25, +1.0};
                if (i == 5) {
                    spoil_vector_by_value(y, (double) Double.NaN);
                }
                if (i == 6) {
                    spoil_vector_by_value(y, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 7) {
                    spoil_vector_by_value(y, (double) Double.NEGATIVE_INFINITY);
                }
//                    if( i==8 )
//                       y = spoil_vector_by_adding_element( y);
//                    if( i==9 )
//                        y = spoil_vector_by_deleting_element( y);
                double t = 0.25;
                if (i == 10) {
                    t = (double) Double.POSITIVE_INFINITY;
                }
                if (i == 11) {
                    t = (double) Double.NEGATIVE_INFINITY;
                }
                double v;
                spline1dinterpolant s = new spline1dinterpolant();

                // build spline
                interpolyaciya.spline1dbuildlinear(x, y, s);
                double[] yy = value_spline(s);
                for (int k = 0; k < yy.length; k++) {
                    System.out.println("yy[" + k + "] = " + yy[k]);
                }
                System.out.println("s.c.length " + s.c.length);
                for (int k = 0; k < s.c.length; k++) {
                    System.out.println("c[" + k + "] = " + s.c[k]);
                }
                System.out.println("s.x.length " + s.x.length);
                for (int k = 0; k < s.x.length; k++) {
                    System.out.println("x[" + k + "] = " + s.x[k]);
                }

                // calculate S(0.25) - it is quite different from 0.25^2=0.0625
                v = spline1d.spline1dcalc(s, t);
                System.out.println("vvvvvvv " + v);
                _TestResult = _TestResult && doc_test_real(v, 0.125, 0.00005);//0.125
                _TestResult = _TestResult && ( i == -1 );
            }
            catch (Exception e) {
                _TestResult = _TestResult && ( i != -1 );
            }

        }
        if (!_TestResult) {
            System.out.println("{0,-32} FAILED" + "spline1d_d_linear");
        }
        _TotalResult = _TotalResult && _TestResult;

    }

    public static boolean doc_test_bool(boolean val, boolean test_val) {
        return ( val && test_val ) || ( !val && !test_val );
    }

    public static boolean doc_test_int(int val, int test_val) {
        return val == test_val;
    }

    public static boolean doc_test_real(double val, double test_val, double _threshold) {
        double s = _threshold >= 0 ? 1.0 : Math.abs(test_val);
        double threshold = Math.abs(_threshold);
        return Math.abs(val - test_val) / s <= threshold;
    }

    /* public static bool doc_test_complex(alglib.complex val, alglib.complex test_val, double _threshold)
    {
    double s = _threshold>=0 ? 1.0 : alglib.math.abscomplex(test_val);
    double threshold = Math.Abs(_threshold);
    return alglib.math.abscomplex(val-test_val)/s<=threshold;
    }*/
    //
    // TEST spline1d_d_cubic
    //      Cubic spline interpolation
    //
    public static void spline1d_d_cubic() {
        boolean _TestResult = true;
        boolean _TotalResult = true;
        for (int i = -1; i < 10; i++) {
            try {
                //
                // We use cubic spline to interpolate f(x)=x^2 sampled
                // at 5 equidistant nodes on [-1,+1].
                //
                // First, we use default boundary conditions ("parabolically terminated
                // spline") because cubic spline built with such boundary conditions
                // will exactly reproduce any quadratic f(x).
                //
                // Then we try to use natural boundary conditions
                //     d2S(-1)/dx^2 = 0.0
                //     d2S(+1)/dx^2 = 0.0
                // and see that such spline interpolated f(x) with small error.
                //
                double[] x = new double[]{-1.0, -0.5, 0.0, +0.5, +1.0};
                if (i == 0) {
                    spoil_vector_by_value(x, (double) Double.NaN);
                }
                if (i == 1) {
                    spoil_vector_by_value(x, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 2) {
                    spoil_vector_by_value(x, (double) Double.NEGATIVE_INFINITY);
                }
                //  if( i==3 )
                //   spoil_vector_by_deleting_element( x);
                double[] y = new double[]{1.0, 0.25, 0.0, 0.25, 1.0};
                if (i == 4) {
                    spoil_vector_by_value(y, (double) Double.NaN);
                }
                if (i == 5) {
                    spoil_vector_by_value(y, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 6) {
                    spoil_vector_by_value(y, (double) Double.NEGATIVE_INFINITY);
                }
                // if( i==7 )
                //   spoil_vector_by_deleting_element( y);
                double t = 0.25;
                if (i == 8) {
                    t = (double) Double.POSITIVE_INFINITY;
                }
                if (i == 9) {
                    t = (double) Double.NEGATIVE_INFINITY;
                }
                double v;
                spline1dinterpolant s = new spline1dinterpolant();
                int natural_bound_type = 2;
                //
                // Test exact boundary conditions: build S(x), calculare S(0.25)
                // (almost same as original function)
                //
                spline1d.spline1dbuildcubic(x, y, s);
                double[] yy = value_spline_cub(s);
                for (int k = 0; k < yy.length; k++) {
                    System.out.println("yy[" + k + "] = " + yy[k]);
                }
                System.out.println("s.c.length " + s.c.length);
                for (int k = 0; k < s.c.length; k++) {
                    System.out.println("c[" + k + "] = " + s.c[k]);
                }
                System.out.println("s.x.length " + s.x.length);
                for (int k = 0; k < s.x.length; k++) {
                    System.out.println("x[" + k + "] = " + s.x[k]);
                }

                v = spline1d.spline1dcalc(s, t);//подстановка значения в сплайн
                _TestResult = _TestResult && doc_test_real(v, 0.0625, 0.00001);

                //
                // Test natural boundary conditions: build S(x), calculare S(0.25)
                // (small interpolation error)
                //
                spline1d.spline1dbuildcubic(x, y, 5, natural_bound_type, 0.0, natural_bound_type, 0.0, s);
                v = spline1d.spline1dcalc(s, t);
                _TestResult = _TestResult && doc_test_real(v, 0.0580, 0.0001);
                _TestResult = _TestResult && ( i == -1 );
            }
            catch (Exception e) {
                _TestResult = _TestResult && ( i != -1 );
            }

        }
        if (!_TestResult) {
            System.out.println("{0,-32} FAILED" + "spline1d_d_cubic");
        }
        _TotalResult = _TotalResult && _TestResult;
    }

    //
    // TEST spline1d_d_griddiff
    //      Differentiation on the grid using cubic splines
    //
    public static void spline1d_d_griddiff() {
        boolean _TestResult = true;
        boolean _TotalResult = true;
        for (int i = -1; i < 10; i++) {
            try {
                //
                // We use cubic spline to do grid differentiation, i.e. having
                // values of f(x)=x^2 sampled at 5 equidistant nodes on [-1,+1]
                // we calculate derivatives of cubic spline at nodes WITHOUT
                // CONSTRUCTION OF SPLINE OBJECT.
                //
                // There are efficient functions spline1dgriddiffcubic() and
                // spline1dgriddiff2cubic() for such calculations.
                //
                // We use default boundary conditions ("parabolically terminated
                // spline") because cubic spline built with such boundary conditions
                // will exactly reproduce any quadratic f(x).
                //
                // Actually, we could use natural conditions, but we feel that
                // spline which exactly reproduces f() will show us more
                // understandable results.
                //
                double[] x = new double[]{-1.0, -0.5, 0.0, +0.5, +1.0};
                if (i == 0) {
                    spoil_vector_by_value(x, (double) Double.NaN);
                }
                if (i == 1) {
                    spoil_vector_by_value(x, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 2) {
                    spoil_vector_by_value(x, (double) Double.NEGATIVE_INFINITY);
                }
                // if( i==3 )
                //   spoil_vector_by_adding_element( x);
                // if( i==4 )
                //  spoil_vector_by_deleting_element( x);
                double[] y = new double[]{+1.0, 0.25, 0.0, 0.25, +1.0};
                if (i == 5) {
                    spoil_vector_by_value(y, (double) Double.NaN);
                }
                if (i == 6) {
                    spoil_vector_by_value(y, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 7) {
                    spoil_vector_by_value(y, (double) Double.NEGATIVE_INFINITY);
                }
                //if( i==8 )
                // spoil_vector_by_adding_element( y);
                //if( i==9 )
                //spoil_vector_by_deleting_element( y);
                double[] d1 = new double[]{};
                double[] d2 = new double[]{};

                //
                // We calculate first derivatives: they must be equal to 2*x
                //
                spline1d.spline1dgriddiffcubic(x, y, d1);
                d1 = spline1d.dd1;
                _TestResult = _TestResult && doc_test_real_vector(d1, new double[]{-2.0, -1.0, 0.0, +1.0, +2.0}, 0.0001);

                //
                // Now test griddiff2, which returns first AND second derivatives.
                // First derivative is 2*x, second is equal to 2.0
                //
                spline1d.spline1dgriddiff2cubic(x, y, d1, d2);
                d1 = spline1d.dd1;
                d2 = spline1d.dd2;
                for (int k = 0; k < d1.length; k++) {
                    System.out.println("d1[" + k + "] = " + d1[k]);
                }
                //  System.out.println("s.x.length " + d2.length);
                for (int k = 0; k < d2.length; k++) {
                    System.out.println("d2[" + k + "] = " + d2[k]);
                }

                _TestResult = _TestResult && doc_test_real_vector(d1, new double[]{-2.0, -1.0, 0.0, +1.0, +2.0}, 0.0001);
                _TestResult = _TestResult && doc_test_real_vector(d2, new double[]{2.0, 2.0, 2.0, 2.0, 2.0}, 0.0001);
                _TestResult = _TestResult && ( i == -1 );
            }
            catch (Exception e) {
                _TestResult = _TestResult && ( i != -1 );
            }

        }
        if (!_TestResult) {
            System.out.println("{0,-32} FAILED" + "spline1d_d_griddiff");
        }
        _TotalResult = _TotalResult && _TestResult;

    }
    //
    // TEST spline1d_d_convdiff
    //      Resampling using cubic splines
    //

    public static void spline1d_d_convdiff() {
        boolean _TestResult = true;
        boolean _TotalResult = true;
        for (int i = -1; i < 11; i++) {
            try {
                //
                // We use cubic spline to do resampling, i.e. having
                // values of f(x)=x^2 sampled at 5 equidistant nodes on [-1,+1]
                // we calculate values/derivatives of cubic spline on
                // another grid (equidistant with 9 nodes on [-1,+1])
                // WITHOUT CONSTRUCTION OF SPLINE OBJECT.
                //
                // There are efficient functions spline1dconvcubic(),
                // spline1dconvdiffcubic() and spline1dconvdiff2cubic()
                // for such calculations.
                //
                // We use default boundary conditions ("parabolically terminated
                // spline") because cubic spline built with such boundary conditions
                // will exactly reproduce any quadratic f(x).
                //
                // Actually, we could use natural conditions, but we feel that
                // spline which exactly reproduces f() will show us more
                // understandable results.
                //
                double[] x_old = new double[]{-1.0, -0.5, 0.0, +0.5, +1.0};
                if (i == 0) {
                    spoil_vector_by_value(x_old, (double) Double.NaN);
                }
                if (i == 1) {
                    spoil_vector_by_value(x_old, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 2) {
                    spoil_vector_by_value(x_old, (double) Double.NEGATIVE_INFINITY);
                }
                // if( i==3 )
                //  spoil_vector_by_deleting_element(  x_old);
                double[] y_old = new double[]{+1.0, 0.25, 0.0, 0.25, +1.0};
                if (i == 4) {
                    spoil_vector_by_value(y_old, (double) Double.NaN);
                }
                if (i == 5) {
                    spoil_vector_by_value(y_old, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 6) {
                    spoil_vector_by_value(y_old, (double) Double.NEGATIVE_INFINITY);
                }
                //  if( i==7 )
                //  spoil_vector_by_deleting_element(  y_old);
                double[] x_new = new double[]{-1.00, -0.75, -0.50, -0.25, 0.00, +0.25, +0.50, +0.75, +1.00};
                if (i == 8) {
                    spoil_vector_by_value(x_new, (double) Double.NaN);
                }
                if (i == 9) {
                    spoil_vector_by_value(x_new, (double) Double.POSITIVE_INFINITY);
                }
                if (i == 10) {
                    spoil_vector_by_value(x_new, (double) Double.NEGATIVE_INFINITY);
                }
                double[] y_new = new double[]{};
                double[] d1_new = new double[]{};
                double[] d2_new = new double[]{};

                //
                // First, conversion without differentiation.
                //
                //
                spline1d.spline1dconvcubic(x_old, y_old, x_new, y_new);
                y_new = spline1d.yy2;
                _TestResult = _TestResult && doc_test_real_vector(y_new, new double[]{1.0000, 0.5625, 0.2500, 0.0625, 0.0000, 0.0625, 0.2500, 0.5625, 1.0000}, 0.0001);

                //
                // Then, conversion with differentiation (first derivatives only)
                //
                //
                spline1d.spline1dconvdiffcubic(x_old, y_old, x_new, y_new, d1_new);
                y_new = spline1d.yy2;
                d1_new = spline1d.dd2;
                _TestResult = _TestResult && doc_test_real_vector(y_new, new double[]{1.0000, 0.5625, 0.2500, 0.0625, 0.0000, 0.0625, 0.2500, 0.5625, 1.0000}, 0.0001);
                _TestResult = _TestResult && doc_test_real_vector(d1_new, new double[]{-2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0}, 0.0001);

                //
                // Finally, conversion with first and second derivatives
                //
                //
                spline1d.spline1dconvdiff2cubic(x_old, y_old, x_new, y_new, d1_new, d2_new);
                y_new = spline1d.yy2;
                d2_new = spline1d.dd1;
                d1_new = spline1d.dd2;
                for (int k = 0; k < y_new.length; k++) {
                    System.out.println("y_new[" + k + "] = " + y_new[k]);//y
                }
                for (int k = 0; k < d2_new.length; k++) {
                    System.out.println("d2_new[" + k + "] = " + d2_new[k]);//2*x
                }
                for (int k = 0; k < d1_new.length; k++) {
                    System.out.println("d1_new[" + k + "] = " + d1_new[k]);//koef 2
                }
                _TestResult = _TestResult && doc_test_real_vector(y_new, new double[]{1.0000, 0.5625, 0.2500, 0.0625, 0.0000, 0.0625, 0.2500, 0.5625, 1.0000}, 0.0001);
                _TestResult = _TestResult && doc_test_real_vector(d1_new, new double[]{-2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0}, 0.0001);
                _TestResult = _TestResult && doc_test_real_vector(d2_new, new double[]{2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0}, 0.0001);
                _TestResult = _TestResult && ( i == -1 );
            }
            catch (Exception e) {
                _TestResult = _TestResult && ( i != -1 );
            }

        }
        if (!_TestResult) {
            System.out.println("{0,-32} FAILED" + "spline1d_d_convdiff");
        }
        _TotalResult = _TotalResult && _TestResult;
    }

    public static void main(String[] args) {
       // spline1d_d_convdiff();
        spline1d_d_griddiff();
        //spline1d_d_cubic();
        //spline1d_d_linear();
    }

    public static boolean doc_test_real_vector(double[] val, double[] test_val, double _threshold) {
        int i;
        if (ap.len(val) != ap.len(test_val)) {
            return false;
        }
        for (i = 0; i < ap.len(val); i++) {
            double s = _threshold >= 0 ? 1.0 : Math.abs(test_val[i]);
            double threshold = Math.abs(_threshold);
            if (Math.abs(val[i] - test_val[i]) / s > threshold) {
                return false;
            }
        }
        return true;
    }
}
