/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.khvorov;

import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 *
 * @author khvorov
 */
public class TestMass {

    public static void main(String[] args) {
       Ring ring = new Ring("R64[x,y,z]");
       ring.setDefaulRing();
//       Polynom primer1 = new Polynom("x^2+y^2+z^2", ring);
//       System.out.println("Полином1 = "+primer1);
//       Element[] predel1 = new Element[]{new NumberR64(-1), new NumberR64(1), new NumberR64(-1), new NumberR64(1), new NumberR64(-1), new NumberR64(1)};
//       System.out.println("Пределы интегрирования1 ="+Array.toString(predel1));
//       int[] num1 = new int[]{2,1,0};
//       Element mass1 = new ObjectMassAndCentreObjectMass().objectMass(primer1,num1, predel1, ring);
//       System.out.println("Масса1 = " + mass1);
//       Element[] centr1 = new ObjectMassAndCentreObjectMass().centreObjectMass(primer1, mass1,num1, predel1, ring);
//       System.out.println("Координаты центра массы1  = " + Array.toString(centr1));
//       System.out.println("");
       ////////////////////////////////////////////////////////////////////////
//       Polynom primer2 = new Polynom("x^2+y^2",ring);
//       System.out.println("Полином2 = "+primer2);
//       Element k = new NumberR64(1);
//       Element b1 = new NumberR64(1);
//       Element b2 = new NumberR64(1);
//
//       Element[] predel2 = new Element[]{new NumberR64(1),new NumberR64(2),new NumberR64(1),new NumberR64(3),new NumberR64(1),new NumberR64(3)};
//       System.out.println("Пределы интегрирования2 ="+Array.toString(predel2));
//       int[] num2 = new int[]{0,1,2};
//       Element mass2 = new ObjectMassAndCentreObjectMass().objectMass(primer2,num2,predel2,ring);
//       System.out.println("Масса2 ="+mass2);
//       Element[] centr2 = new ObjectMassAndCentreObjectMass().centreObjectMass(primer2, mass2,num2, predel2, ring);
//       System.out.println("Координаты центра массы2 = " + Array.toString(centr2));
//       System.out.println("");
       /////////////////////////////////////////////////////////////////////////
        Polynom p8 = new Polynom("1", ring);
        System.out.println("Полином = " + p8);
        double[][] temp1 = new double[][]{{0, 1, 2},
                                          {0, 1, 2},
                                          {0, 1, 2}
        };
        Element b1 = new NumberR64(10);
        Element b2 = new NumberR64(20);
        Element x = new NumberR64(10);
        Element y = new NumberR64(10);
        Polynom[][] pol2 = new ObjectMassAndCentreObjectMass().splines2D(b1, b2, x, y, temp1, ring);
        System.out.println(Array.toString(pol2,ring));
        Polynom[][] pol = new Polynom[][]{{new Polynom("0",ring),new Polynom("0",ring)},{new Polynom("0",ring),new Polynom("0",ring)}};
        int[] num8 = new int[]{2, 1, 0};
        Element[] c1 = new Element[]{new NumberR64(0), new NumberR64(1), new NumberR64(0), new NumberR64(0.5), new NumberR64(0), new NumberR64(0.5)};
        Element[] c2 = new Element[]{new NumberR64(0), new NumberR64(1), new NumberR64(0), new NumberR64(0.5), new NumberR64(0.5), new NumberR64(1)};
        Element[] c3 = new Element[]{new NumberR64(0), new NumberR64(1), new NumberR64(0.5), new NumberR64(1), new NumberR64(0), new NumberR64(0.5)};
        Element[] c4 = new Element[]{new NumberR64(0), new NumberR64(1), new NumberR64(0.5), new NumberR64(1), new NumberR64(0.5), new NumberR64(1)};
        Element[][] c = new Element[][]{c1, c2, c3, c4};
        System.out.println("Пределы интегрирования= " + Array.toString(c, ring));
        System.out.println("");
        Element m = new ObjectMassAndCentreObjectMass().generalMass(p8, c, pol2,pol, num8, ring);
        System.out.println("Общая масса= " + m);
        System.out.println("");
        Element[] gen = new ObjectMassAndCentreObjectMass().generalCentreObjectMass(p8, m, c, pol2,pol, num8, ring);
        System.out.println("Центр"
                + " " + Array.toString(gen, ring));
        System.out.println("");
//        double f[][][] = {
//            {
//                {-00, -00, -00, 00, -7, -10, -1},
//                {-00, 0.3, -00, 00, -7, -1.8, -1},
//                {-00, -00, -00, 00, -4, -1, -1},
//                {-00, -00, -00, 00, -7, -1, -1},
//                {-00, -0.1, 0.5, 1, -0.2, -0.5, -1},
//                {-00, -1, -1.5, 1, -7, -1, -1}
//            },
//            {
//                {-00, -00, -00, 00, -7, -10, -1},
//                {-00, 0.3, -00, 00, -7, -1.8, -1},
//                {-00, -00, -00, 00, -4, -1, -1},
//                {-00, -00, -00, 00, -7, -1, -1},
//                {-00, -0.1, 0.5, 1, -0.2, -0.5, -1},
//                {-00, -1, -1.5, 1, -7, -1, -1}
//            },
//            {
//                {-00, -00, -00, 00, -7, -10, -1},
//                {-00, 0.3, -00, 00, -7, -1.8, -1},
//                {-00, -00, -00, 00, -4, -1, -1},
//                {-00, -00, -00, 00, -7, -1, -1},
//                {-00, -0.1, 0.5, 1, -0.2, -0.5, -1},
//                {-00, -1, -1.5, 1, -7, -1, -1}
//            },
//            {
//                {-00, -00, -00, 00, -7, -10, -1},
//                {-00, 0.3, -00, 00, -7, -1.8, -1},
//                {-00, -00, -00, 00, -4, -1, -1},
//                {-00, -00, -00, 00, -7, -1, -1},
//                {-00, -0.1, 0.5, 1, -0.2, -0.5, -1},
//                {-00, -1, -1.5, 1, -7, -1, -1}
//            }
//        };
//        Polynom test = new ObjectMassAndCentreObjectMass().splines3D(b1, b1, b1, x, x, x, f, pol2, ring);
//        System.out.println("Полином = " +test);
//        System.out.println("");
        /////////////////////////////////////////////////////////////////////////////////
//        Polynom p = new Polynom("x+y+z",ring);
//        Polynom p1 = new Polynom("1-x/4 -y/2",ring);
//        Element[] gen = new SurfaceIntegral().derivative(p1, ring);
//        p = new SurfaceIntegral().title(p, p1, ring);
//        int[] b = new int[]{0,1};
//        Element[] c = new Element[]{new NumberR64(0),new Polynom("4 - 2y",ring),  new NumberR64(0), new NumberR64(2)};
//        double[][] temp1 = new double[][]{{0, 1, 2},
//                                          {0, 1, 2},
//                                          {0, 1, 2}
//        };
//        Element b1 = new NumberR64(0);
//        Element b2 = new NumberR64(0);
//        Element x = new NumberR64(2);
//        Element y = new NumberR64(2);
//        Polynom[][] pol2 = new ObjectMassAndCentreObjectMass().splines2D(b1, b2, x, y, temp1, ring);
//        System.out.println(Array.toString(pol2,ring));
//        Element temp = new SurfaceIntegral().surfaceIntegralFirstType(p, pol2, c, b, ring);
//        System.out.println("Ответ ="+ temp);
//        System.out.println(" ");


    }
}
