/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.pochtarkov;

import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 *
 * @author pochtarkov
 */


public class Newton {
    static Ring R=new Ring("R64[x]");
    static NumberR64[] ROOTS=new NumberR64[]{};
    public static NumberR64 Root (NumberR64 a,NumberR64 b,Polynom p) {
        NumberR64 root=new NumberR64();
        NumberR64 dva=new NumberR64 (2);
        NumberR64 x=(a.add(b)).divide(dva);
        NumberR64 E=new NumberR64(0.01);
        NumberR64 x1=x.add(E.multiply(dva));
        int i=0;
        while (((x.subtract(x1)).abs(R)).compareTo(E)==1) {
        Polynom p1=p.D(Ring.ringR64xyzt);
            System.out.println("p1==="+p1);
        Element[] pet1 = new Element[]{x};
            System.out.println("pet1==="+pet1);
        NumberR64 p2 = (NumberR64) p.valueOf(x,R);
            System.out.println("p2==="+p2);
        Element[] pet2 = new Element[]{x};
            System.out.println("pet2==="+pet2);
        NumberR64 p3 =(NumberR64) p1.valueOf(x,R);
            System.out.println("p3==="+p3);
        x1=x.subtract(p2.divide(p3));
            System.out.println("x1==="+x1);
        i++;
        NumberR64 x2=x;
            System.out.println("x2==="+x2);
        x=x1;
            System.out.println("x=="+x);
            System.out.println("x1=="+x1);
        x1=x2;}
        root=x;
        System.out.println("root=="+root);
        return root;
    }

public static void main(String[] args){
    Polynom p=new Polynom("(x-10)(x+2)(x-2)(x+5)(x-100)(x+15)(x-5000)",R);
    R.setDefaulRing();
    NumberR64 a= new NumberR64(2000.75);
    NumberR64 b= new NumberR64(6000.75);
    Root(a, b, p);
}

}
