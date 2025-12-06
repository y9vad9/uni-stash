/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.shmeleva.diplom;
import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.func.*;
/**
 *
 * @author shmeleva
 */
public class VectorFunc {
 public static Element[] VectorFuncD(Element[] vectorFunc, Ring ring ){
        Element[] result = new Element[vectorFunc.length];
        for(int i = 0; i<vectorFunc.length; i++){
           result[i] = vectorFunc[i].D(ring);


        }
       return result;
    }
 public static Element[] VectorFuncIntegrate(Element[] vectorFunc, Ring ring ){
         Element[] result = new Element[vectorFunc.length];
         for(int i = 0; i<vectorFunc.length; i++){
             result[i] = vectorFunc[i].integrate(ring);
         }
         return result;
    }



 public static void main(String[] args) {
       Ring ring = new Ring("R64[x]");
       Element[] vec = new Element[]{new Polynom("x^3+x^2-1",ring), new F("\\cos(x)",ring)};
       Element[] result = VectorFuncD(vec, ring);
        System.out.println("Производная = "+Array.toString(result));
       System.out.println(" ="+Array.toString(result));
      Element [] res2 = VectorFuncIntegrate(vec, ring);
      System.out.println("Интегрирование = "+Array.toString(res2));

    }

}
