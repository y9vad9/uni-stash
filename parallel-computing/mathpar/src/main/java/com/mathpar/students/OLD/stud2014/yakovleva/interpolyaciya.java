/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.yakovleva;

import java.util.*;

/**
 *
 * @author yakovleva
 */
public class interpolyaciya {
    public static void spline1dbuildlinear(double x[], double y[], int n, spline1dinterpolant c) {
        c = new spline1dinterpolant();
        spline1d.spline1dbuildlinear(x, y, n, c);
        return;
    }

    public class alglibexception {
        public String msg;

        public alglibexception(String s) {
            msg = s;
        }
    }

    public static void spline1dbuildlinear(double x[], double y[], spline1dinterpolant c) {
        int n;
        if ((ap.len(x) != ap.len(y))) {
            //  throw new alglibexception("Error while calling 'spline1dbuildlinear': looks like one of arguments has wrong size");
        }
        n = ap.len(x);
        spline1d.spline1dbuildlinear(x, y, n, c);
        return;
    }

    public static void main(String[] args) {
        double x[] = new double[10];
        double y[] = new double[10];
        for (int i = 0;
                i < 10; i++) {
            x[i] = new Random().nextDouble();
            y[i] = new Random().nextDouble();
        }
        spline1dinterpolant c = new spline1dinterpolant();
        interpolyaciya.spline1dbuildlinear(x, y, c);
        for (int j = 0; j < c.c.length; j++) {
            System.out.println(j + " =  " + c.c[j]);
        }
    }
}
