/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.kryuchkov.integrate;

import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.matrix.*;

/**
 *
 * @author alexey
 */
public class splineSquare {

    /**
     * программа строит полином второй степени  по трем точкам
     * @param point - массив из трех точек
     * @param ring - кольцо
     * @return коэффициеты полинома [a,b,c] в ax^2+bx+c
     */
    public static Element[] coeffsSquarePolynom(Element[] point, Ring ring) {

        Element[][] m = new Element[][]{{ring.numberZERO, ring.numberZERO, ring.numberONE},
                                          {ring.numberONE, ring.numberONE, ring.numberONE},
                                       {ring.posConst[4], ring.posConst[2], ring.numberONE}};
        MatrixS A = new MatrixS(m, ring);

        MatrixS inv = A.inverse(ring);

        Element[][] b = new Element[][]{{point[0]}, {point[1]}, {point[2]}};
        MatrixS B = new MatrixS(b, ring);

        B = inv.multiply(B, ring);
        MatrixD D = new MatrixD(B);

        Element[] res = new Element[3];
        for (int i = 0; i < 3; i++) {
            res[i] = (Element) (D.M[i][0]);
        }
        return res;
    }

    /**
     * программа строит полином второй тепени по двум точкам и производной в точке
     * @param point-массив из трех точек
     * @param k-значение производной в точке
     * @param l-номер элемента сплайна
     * @param ring-кольцо
     * @return коэффициенты полинома [a,b,c] в ax^2+bx+c
     */
    public static Element[] coeffsSquarePolynom(Element[] point, Element k, int l, Ring ring) {

        Element[][] m = new Element[][]{{ring.posConst[l].pow(2, ring),              ring.posConst[l],         ring.numberONE},
                                        {ring.posConst[l + 1].pow(2, ring),          ring.posConst[l + 1],     ring.numberONE},
                                        {ring.posConst[2].multiply(ring.posConst[l], ring), ring.numberONE,    ring.numberZERO}};
        MatrixS A = new MatrixS(m, ring);
        MatrixS inv = A.inverse(ring);

        Element[][] b = new Element[][]{{point[0]}, {point[1]}, {k}};
        MatrixS B = new MatrixS(b, ring);

        B = inv.multiply(B, ring);
        MatrixD D = new MatrixD(B);


        Element[] res = new Element[3];
        for (int i = 0; i < 3; i++) {
            res[i] = (Element) (D.M[i][0]);

        }
        return res;
    }

    /**
     * Процедура-сборщик полиномов по массиву элементов
     * @param coeff-массив коэффициентов полинома
     * @param ring- кольцо
     * @return -полном с коэффициетами из массива coeff
     */
    public static Polynom collectorPolinom(Element[] coeff, Ring ring) {
        Element res = coeff[0].multiply(new Polynom("x^2", ring), ring);
        res = res.add(coeff[1].multiply(new Polynom("x", ring), ring), ring);
        res = res.add(coeff[2], ring);

        Polynom result = (Polynom) res;
        return result;
    }

    /**
     * процедура создания одномерных квадратичных сплайнов по массиву елементов
     *
     * на первом шаге процедура вычисляет полином по трем точкам
     * на последующих по двум точкам и касательной
     *
     * @param m - массив точек для создания сплайнов
     * @param ring - кольцо
     * @return массив полиномов(сплайн)
     */
    public static Polynom[] constructionSquareSplines(Element[] m, Ring ring) {
        Polynom[] pol = new Polynom[m.length - 1];
        Element[] el = new Element[3];
        el[0] = m[0];
        el[1] = m[1];
        el[2] = m[2];
        pol[0] = collectorPolinom(coeffsSquarePolynom(el, ring), ring);

        for (int i = 1; i < m.length - 1; i++) {
            el[0] = m[i];
            el[1] = m[i + 1];
            Polynom p = (Polynom) pol[i - 1].D(ring);
            Element k = p.valueOf(m[i], ring);
            pol[i] = collectorPolinom(coeffsSquarePolynom(el, k, i, ring), ring);
        }
        return pol;
    }




    public static void main(String[] args) {

        Ring ring = new Ring("R64[x,y]");

        Element[] m = new Element[]{ring.numberZERO,ring.numberONE, ring.posConst[2], ring.numberZERO, ring.numberONE};
         Element[] n = new Element[]{ring.numberZERO, ring.posConst[2], ring.numberZERO,ring.numberONE, ring.numberONE};
        Polynom[] pol1 = constructionSquareSplines(m, ring);
        Polynom[] pol2 = constructionSquareSplines(n, ring);
        Polynom rho = new Polynom("x^2+x",ring);
        for (int i = 0; i < m.length - 1; i++) {
           System.out.print(pol1[i]+" ");
       }
        System.out.println(" ");
        for(int i=0;i<pol2.length;i++){
            System.out.print(pol2[i]+"");
        }
        System.out.println("  ");
        Element z = integratePamentricPolynomFunc.parametricIntegrateSquareSplines(pol1, pol2, rho, ring);
        System.out.println("I="+z);
        System.out.println("");
        System.out.println(" УРА РАБОТАЕТ!!!!!!!!!!!!!!!!!!!!");
    }
}
