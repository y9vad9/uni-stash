/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.ssam;

import com.mathpar.number.*;
import com.mathpar.func.*;
/**
 *
 * @author student
 */
public class SystemNLE {
    F[] functions;
    Ring r;

     public static void main(String[] args) {
        Ring r = new Ring("R64[x,y]");
        r.setMachineEpsilonR(30);
        r.setAccuracy(40);
        r.FLOATPOS = 35;
        SystemNLE q = new SystemNLE();
        F f1 = new F("x^2+ \\cos(x+\\sin(y))", r);
        F f2 = new F("x*\\sin(y)", r);
        q.functions = new F [2];
        q.functions [0]= f1;
        q.functions [1]= f2;
    }
}
