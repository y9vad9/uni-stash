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
public class math {
    public static Random rndobject = new Random();
    public double machineepsilon = 5E-16;
    public double maxrealnumber = 1E300;
    public double minrealnumber = 1E-300;

    public static boolean isfinite(double d) {
        return !Double.isNaN(d) && !Double.isInfinite(d);
    }

    public static double randomreal() {
        double r = 0;
        r = Math.random();
        return r;
    }

    public static int randominteger(int N) {
        int r = (int) (Math.random() * N);
        return r;
    }

    public static double sqr(double X) {
        return X * X;
    }
}
