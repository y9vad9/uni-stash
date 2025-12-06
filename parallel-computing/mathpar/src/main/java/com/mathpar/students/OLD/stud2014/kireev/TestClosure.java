/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.kireev;

import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.*;

/**
 *
 * @author kireev
 */
public class TestClosure {
    public static void main(String args[]) {
//        Ring ring = new Ring("R64MaxPlus[x]");
////        Element[][] aa1 = new Element[][]{{new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(-2)), ring.numberZERO, ring.numberZERO},
////            {ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(3)), new NumberR64MaxPlus(new NumberR64(-1))},
////            {new NumberR64MaxPlus(new NumberR64(-1)), ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(-4))},
////            {new NumberR64MaxPlus(new NumberR64(2)), ring.numberZERO, ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO)}};
////        Element[][] aa2= new Element[][]{{new NumberR64MaxPlus(new NumberR64(0.00001)),new NumberR64MaxPlus(new NumberR64(0.2))},
////                                         {new NumberR64MaxPlus(new NumberR64(0.3)),new NumberR64MaxPlus(new NumberR64(0.00001))}};
////        MatrixS aa1 = new MatrixS(aa2,ring);
////        MatrixS a = new MatrixS(aa1,ring);
////        MatrixD b = new MatrixD(aa1);
////        MatrixD c = new MatrixD(aa1);
//
//        int f1 = 0;
//        int f2 = 0;
//        int f3 = 0;
//        Element inf=Element.POSITIVE_INFINITY;
////        Element[][] aa2 = new Element[][] {{inf,inf},
////                                           {inf,inf}};
//        Element[][] aa2 = new Element[][] {{inf,inf,inf,inf},
//                                           {inf,inf,inf,inf},
//                                           {inf,inf,inf,inf},
//                                           {inf,inf,inf,inf}};
//        MatrixD m = new MatrixD(aa2);
//        for (int k = 0; k < 1; k++) {
//            MatrixS aa1 = new MatrixS( 4, 4, 100, new int[] {5}, new Random(), ring.numberONE, new Ring("R64[]"));
//
//            aa1 = aa1.multiplyByScalar(new NumberR64(-10), new Ring("R64[x]"));
//            aa1=(MatrixS) aa1.toNewRing(Ring.R64MaxPlus, ring);
//
//            for (int i = 0; i < aa1.M.length; i++) {
//                aa1.M[i][i] = ring.numberONE;
//            }
//
//
//            Element z = new MatrixD(aa1, ring).Tr(ring);
////            if (z.equals(ring.numberONE, ring)) {
////                f1++;
////            }
//            System.out.println("Tr="+z);
//
//            MatrixD a = new MatrixD(aa1, ring);
//            MatrixD b = new MatrixD(aa1, ring);
//
//            System.out.println("input1="+a.toString(ring));
//            long t1 = System.currentTimeMillis();
//            MatrixS a1 = aa1.closure(ring);
//            long t2 = System.currentTimeMillis();
//        System.out.println(""+a1.toString(ring));
//        System.out.println("time="+(t2-t1));
//        System.out.println("----------------------");
//            long t3 = System.currentTimeMillis();
//            MatrixD a2 = equation.closureEsc(b, ring);
//            long t4 = System.currentTimeMillis();
////             if (!a2.compareTo(m, 0, ring)) {
////                f2++;
////            }
//
//        System.out.println(""+a2.toString(ring));
//        System.out.println("time2="+(t4-t3));
//        System.out.println("----------------------");
//            long t5 = System.currentTimeMillis();
//            MatrixD a3 = equation.closureIsc(a, ring);
//            long t6 = System.currentTimeMillis();
////            if (!a3.compareTo(m, 0, ring)) {
////                f3++;
////            }
//            MatrixD a4 = MatrixD.ONE(4, ring).add(a3.multCU(new MatrixD(aa1,ring), ring), ring);
//        System.out.println(""+a3.toString(ring));
//            System.out.println("a4="+a4.toString(ring));
//        System.out.println("time3="+(t6-t5));
//        }
//        System.out.println("f1="+f1+";   f2="+f2+";   f3="+f3);
        Ring ring = new Ring("Z64[x]");
        Element[][] a1 = new Element[][]{{new NumberZ64(0), new NumberZ64(1), new NumberZ64(0)},
            {new NumberZ64(1), new NumberZ64(0),  new NumberZ64(1)}};
        Element[][] b1 = new Element[][]{{new NumberZ64(0), new NumberZ64(0)},
            {new NumberZ64(1), new NumberZ64(1)},
        {new NumberZ64(1), new NumberZ64(0)}};
        MatrixD a = new MatrixD(a1);
        MatrixD b = new MatrixD(b1);
        System.out.println("a="+a.toString(ring));
        System.out.println("b="+b.toString(ring));
        System.out.println("-------------------");
//        MatrixD c = a.or(b);
//        System.out.println("a+b="+c.toString(ring));
        System.out.println("--------------------");
        MatrixD d = a.B_AND(b,ring);
        System.out.println("a & b="+d.toString(ring));
    } 
}
