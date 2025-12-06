/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.yakovleva;

/**
 *
 * @author yakovlev
 */
public class ap {
    public class alglibexception {
        public String msg;

        public alglibexception(String s) {
            msg = s;
        }
    }

    public static int len(double[] a) {
        return a.length;
    }

    public static int len(int[] a) {
        return a.length;
    }

    public static void assertJ(boolean cond, String s) {
        if (!cond) {
            System.out.println(s);
        }
    }
}
