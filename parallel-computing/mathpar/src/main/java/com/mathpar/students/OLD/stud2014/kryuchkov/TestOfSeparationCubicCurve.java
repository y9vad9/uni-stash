/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.kryuchkov;
/**
 *
 * @author
 * Алексей Крючков 3 курс
 */
import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 *
 * @author alexey
 */
public class TestOfSeparationCubicCurve {

    public static void main(String[] args) {
        Ring ring = new Ring("R64[x,y]");
        ring.setDefaulRing();
        ring.FLOATPOS = 5;
//         Ring ring1 = new Ring("R64[y,x]");
//        ring1.setDefaulRing();
//        ring1.FLOATPOS = 5;
        //Polynom  p1 = new Polynom().random(new int[] {3,3,10,10}, new Random(), ring);
      // Polynom p1 = new Polynom("x-y", ring);
       // System.out.println("rjkgo;fgil;fjkgl;");
        Polynom p1 = new Polynom(" x^2-y+1", ring);
        //Polynom p1 = new Polynom("x-y^3", ring);
        //Polynom p1 = new Polynom("y-2", ring);
        //Polynom p1 = new Polynom("(x-3)^2", ring);
        //Polynom p1 = new Polynom("x^2+y^2-1", ring);
        // Polynom p1 = new Polynom("x^2-1", ring);
        //Polynom p1 = new Polynom("(x-1)^2+(y-1)^2-1", ring);
        // Polynom p1 = new Polynom("(x-1)^2+(y+1)^2-1", ring);
        //Polynom p1 = (Polynom)p1.valueOf(new Element[]{new Polynom("x", ring),new NumberR64(-0.99)}, ring);

        //System.out.println(" koz   "+ring.posConst[2].toString(ring));
        // Polynom p1 = new Polynom("(x-2)(x-3)", ring);
         //Polynom p1 = new Polynom("x^2-200x+100-y", ring);
        //Polynom p1 = new Polynom("-(x-1)(2x-1)(x+2)", ring);
        // Element m = new NumberR64(-4);//точка отсчета
       // System.out.println("kjlgfudlgulfigfjlk");
        Element h = new NumberR64(1);//шаг проверки
       // int n = 10;// количество шагов проверки
        Element[] limits = new Element[4];//массив границ
        /**
         * пределы по x
         */
        limits[0] = new NumberR64(0);
        limits[1] = new NumberR64(3);
        /**
         * пределы по y
         */
        limits[2] = new NumberR64(0);
        limits[3] = new NumberR64(3);
        /**
         * n- количество шагов проверки
         */

        Element[][][] masObj = new SeparationOfTheCubicCurve().regionInSegments(limits, p1, h, ring);
        System.out.println("Область №1 =======================================");
        Element[][] qq = masObj[0];

        for (int i = 0; i < qq.length; i++) {
            if (qq[i] != null) {
                int nn = qq[i].length;
                for (int j = 0; j < nn; j++) {
                    if (qq[i] != null) {
                        System.out.print(" | " + qq[i][j].toString(ring));
                    } else {
                        System.out.print("NAN  ");
                    }
                }
                System.out.println(" | ");
            }
        }
        System.out.println("Область №2 =======================================");
        qq = masObj[1];




        for (int i = 0; i < qq.length; i++) {
            if (qq[i] != null) {
                int nn = qq[i].length;
                for (int j = 0; j < nn; j++) {
                    if (qq[i] != null) {
                        System.out.print(" | " + qq[i][j].toString(ring));
                    } else {
                        System.out.print("NAN  ");
                    }
                }
                System.out.println(" | ");
            }
      }

        System.out.println("------------------------- ");
//     Element c =new NumberR64(0);
//     Element h1 = new NumberR64(10);
//
//      int n = new SeparationOfTheCubicCurve().findFunctionOnTheInterval( p1, c, limits[2],  h1,ring1);
  //      System.out.println("    "+ n);
//        Polynom  pol1 = new SeparationOfTheCubicCurve().deleteNotVariables(p1,ring );
//        System.out.println("pol = "+pol1.toString(ring));
    }






}

