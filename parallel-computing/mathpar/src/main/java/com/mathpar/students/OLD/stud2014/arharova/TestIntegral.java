package com.mathpar.students.OLD.stud2014.arharova;
import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.func.*;
/**
 *
 * @author Arkharova E.V.
 */
public class TestIntegral {
    public static void main(String[] args) {
        Ring ring = new Ring("R64[x,y,z]");
        ring.setDefaulRing();
        Polynom p1 = new Polynom("x^2-y^2-1", ring); // Пусть S - гладкая поверхность, заданная уравнением
        //Polynom p2 = new Polynom("x-y",ring);
        F p2 = new F("\\sqrt(x) -2-4y",ring); //f(x,y,z) - некоторая ограниченная функция, определенная на поверхности S
        boolean k = false;//компонент вектора-нормали если к == true, то он положителен
                                                     //если к == false, то он отрицателен
        Polynom[] F = new Polynom[3]; // векторное поле
        F[0] = new Polynom("y", ring);
        F[1] = new Polynom("x", ring);
        F[2] = new Polynom("z", ring);
        p1 = p1.integrate(0,ring);
        Element [] c = new Element[4];
        c[0] =new NumberR64(0);
        c[1] = new NumberR64(1);
        c[2] = new NumberR64(0);
        c[3] = new NumberR64(1);
        int[] w = new int[2];
        w[0] =0;
        w[1] = 1;
          SurfaceIntegral box = new SurfaceIntegral(p1, p2, c, w);
        Element otvet_1 = box.surfaceIntegralFirstType(p1, p2, c, w, ring);
        //Element otvet_1 = new SurfaceIntegral().surfaceIntegralFirstType(p1,p2,c,w,ring);
       // Element otvet_2 = box.surfaceIntegralSecondType(F, p2, c, w, k, ring);
         Element otvet_2 = new SurfaceIntegral().surfaceIntegralSecondType(F,p1,c,w,k,ring);
         System.out.println(" Ответ для интеграла первого рода " + " " + otvet_1 );
         System.out.println(" Ответ для интеграла второго рода " + " " + otvet_2 );
//                  Element zero = ring.numberZERO;
//                  Element m = new NumberR64(5);
//            Polynom r1 = new Polynom("x-2",ring);
//        r1.toVector(m, ring);
//        System.out.println(" vect = " + r1.toString());
    }

}
