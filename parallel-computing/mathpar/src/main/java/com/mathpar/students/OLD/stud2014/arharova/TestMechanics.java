package com.mathpar.students.OLD.stud2014.arharova;
import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 *
 * @author arharova
 */
public class TestMechanics {
    public static void main(String[] args) {
       Ring ring = new Ring("R64[x,y,z]");
       ring.setDefaulRing();
       Polynom p8 = new Polynom("1", ring);
        System.out.println("Полином = " + p8);
        double[][] temp1 = new double[][]{{0, 1, 2},
                                          {0, 1, 2},
                                          {0, 1, 2}
        };
        Element b1 = new NumberR64(10); //сдвиги
        Element b2 = new NumberR64(20);
        Element x = new NumberR64(10);
        Element y = new NumberR64(10);
        Polynom[][] pol2 = new SurfaceMechanics().splines2D(b1, b2, x, y, temp1, ring); //создает кусочно-заданную функцию
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
        Element m = new SurfaceMechanics().generalMass(p8, c, pol2,pol, num8, ring);
        System.out.println("Общая масса= " + m);
        System.out.println("");
        Element[] gen = new SurfaceMechanics().generalCentreObjectMass(p8, m, c, pol2,pol, num8, ring);
        System.out.println("Центр"
                + " " + Array.toString(gen, ring));
        System.out.println("");
    }
}
