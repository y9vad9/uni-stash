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
public class TestSurface {

    public static void main(String[] args) {
        Ring ring = new Ring("R64[x,y,z]");
        ring.setDefaulRing();
        Polynom p = new Polynom("x+y+z", ring);

        int[] b = new int[]{0, 1, 2};
        Element[][] c = new Element[][]{{new NumberR64(0), new Polynom("y", ring), new NumberR64(0), new NumberR64(1)},
            {new NumberR64(0), new Polynom("y+1", ring), new NumberR64(1), new NumberR64(2)},
            {new NumberR64(0), new Polynom("y", ring), new NumberR64(0), new NumberR64(1)},
            {new NumberR64(0), new Polynom("y+1", ring), new NumberR64(1), new NumberR64(2)}};
        double[][] temp1 = new double[][]{{0, 1, 2},
                                          {0, 1, 2},
                                          {0, 1, 2}
        };
        Element b1 = new NumberR64(0);
        Element b2 = new NumberR64(0);
        Element x = new NumberR64(2);
        Element y = new NumberR64(2);
        Polynom[][] pol2 = new ObjectMassAndCentreObjectMass().splines2D(b1, b2, x, y, temp1, ring);

        Element temp = new SurfaceIntegral().surfaceIntegralFirstType(p, pol2, c, b, ring);
        System.out.println("Ответ =" + temp);
        System.out.println(" ");
        //////////////////////////////////////////////////////////////////////////////////////////
        Element[] t = new Element[]{new Polynom("x", ring), ring.numberMINUS_ONE, new Polynom("z", ring)};
        double[][] temp2 = new double[][]{{1, 0, 1},
                                          {1, 0, 1},
                                          {1, 0, 1}};
        Polynom[][] pol3 = new ObjectMassAndCentreObjectMass().splines2D(b1, b2, x, y, temp2, ring);
        System.out.println("");
        Element[][] c1 = new Element[][]{{new NumberR64(0), new NumberR64(3), new NumberR64(0), new NumberR64(1)},
                                         {new NumberR64(3), new NumberR64(7), new NumberR64(1), new NumberR64(2)},
                                         {new NumberR64(0), new NumberR64(3), new NumberR64(0), new NumberR64(1)},
                                         {new NumberR64(3), new NumberR64(7), new NumberR64(1), new NumberR64(2)}};

        Element result = new SurfaceIntegral().surfaceIntegralSecondType(t, pol3, c1, b, true, ring);
        System.out.println("Ответ =" + result);
        System.out.println("");
        //////////////////////////////////////////////////////////////////////////////////////////////////
        Ring ring2 = new Ring("R64[x,y,z,u,v]");
        ring2.setDefaulRing();
        Element[] t1 = new Element[]{new Polynom("x", ring2), new Polynom("y",ring2), new Polynom("z", ring2)};
        Polynom[] jkl = new Polynom[]{new Polynom("u+v",ring2), new Polynom("u^2",ring2), new Polynom("v^2",ring2)};
        Element[] predel = new Element[]{new NumberR64(0), new NumberR64(5), new NumberR64(0), new NumberR64(2)};
        System.out.println("Пределы интегрирования = "+Array.toString(predel));
        int[] num = new int[]{0,1};
        Element otvet = new SurfaceIntegral().secondTypeParametricForm(t1, jkl, predel, num[1], num, ring2);

        System.out.println("Ответ = "+otvet);

    }
}
