/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.polynom;

import com.mathpar.func.F;
//import com.mathpar.func.Integrate;
import com.mathpar.func.Page;
import java.util.Arrays;
import com.mathpar.number.*;

/**
 *156051 4196
 * @author student
 */
public class NewClass {

    public static void main(String args[]) {
       // Ring r = new Ring("C64[x]");
    Ring ring = new Ring("Q[x]"); ring.page  =new Page(ring,true);
  F f= new F("1/3* \\ln(\\abs(1/2* \\cos(x^3+7)* \\exp(7\\i )))",ring);
  Element ee= f.ExpandLog(ring);
  //      Element resInt = new Integrate().integration(f, new Polynom("x", ring), ring);
   //     Element b = new F("((-3x) + (\\ln(\\abs(x+2) \\abs(x+1) \\abs(x+3)))+ x * \\ln(x^3+6x^2+11x+6))", ring);
    //   Element res = resInt.subtract(b, ring);
        System.out.println("RES=" +ee); // resInt);
    //    System.out.println("b="+  b );
    }
}
