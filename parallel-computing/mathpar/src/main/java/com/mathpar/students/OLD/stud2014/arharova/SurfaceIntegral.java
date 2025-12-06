package com.mathpar.students.OLD.stud2014.arharova;

import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.students.OLD.stud2014.kryuchkov.integrate.integratePamentricPolynomFunc;

/**
 *
 * @author Arharova E.V.
 */
public class SurfaceIntegral {
    //поверхностный интеграл как фрагмент сплайн-поверхности
    Element func;  //функция rho=rho(x,y,z), скалярная функция заданная в точке пространства,
    // например плотность заданная в каждой точке поверхности rho=rho(x,y,f(x,y))
    Element pol;//поверхность (z = f(x,y), заданная явно)
    Element[] limits_xy; // (x1,x2,y1,y2) -отрезки по осям X, Y,
    //фиксирующие прямоугольник в котором определена функция f(x,y)
    int[] b = new int[] {}; //порядок переменных при вычислении интеграла

    public SurfaceIntegral(Polynom p1, Element p2, Element[] limitXY, int[] t) {
        func = p2;
        pol = p1;
        limits_xy = limitXY;
        b = t;
    }

    SurfaceIntegral() {
        Element func;  //функция rho=rho(x,y,z), скалярная функция заданная в точке пространства,
        // например плотность заданная в каждой точке поверхности rho=rho(x,y,f(x,y))
        Element pol;//поверхность (z = f(x,y), заданная явно)
        Element[] limits_xy; // (x1,x2,y1,y2) -отрезки по осям X, Y,
        //фиксирующие прямоугольник в котором определена функция f(x,y)

        int[] b = new int[] {}; //порядок переменных при вычислении интеграла;
    }

    /**
     * процедура нахождения производной Z_x, Z_y
     * @param p - полином
     * @param ring - кольцо
     * @return массив производных
     */
    public Element[] derivativeXY(Element p, Ring ring) {
        Element c1 = p.D(0, ring);//производная по х
        Element c2 = p.D(1, ring);//производная по у
        Element[] c = new Element[] {c1, c2};
        return c;
    }

    /**
     * процедура, заменяющая переменную z на полином от (x,y)
     * @param p - полином
     * @param p1 - полином от (x,y)
     * @param ring - кольцо
     * @return полином
     */
    public Element substitute(Element p, Element p1, Ring ring) {
        Element x = ring.varPolynom[0];
        Element y = ring.varPolynom[1];
        //Element z = (Element) p1;
        p = p.value(new Element[] {x, y, p1}, ring);
        return p;
    }

    /**
     * процедура подстановки пределов интегрирования вместо первой переменной
     * @param p - полином от трех переменных
     * @param num - номер переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     * @return результат подстановки
     */
    public Element integralFirstVar(Element p, int num, Element elem1, Element elem2, Ring ring) {
        // p = p.ordering(ring);
        if (num == 0) {
            Element res1 = p.value(new Element[] {elem2, ring.varPolynom[1], ring.varPolynom[2]}, ring);
            Element res2 = p.value(new Element[] {elem1, ring.varPolynom[1], ring.varPolynom[2]}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

        }
        if (num == 1) {
            Element res1 = p.value(new Element[] {ring.varPolynom[0], elem2, ring.varPolynom[2]}, ring);
            Element res2 = p.value(new Element[] {ring.varPolynom[0], elem1, ring.varPolynom[2]}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
        }
        return null;
    }

    /**
     * процедура подстановки пределов интегрирования вместо второй переменной
     * @param p - полином от трех переменных
     * @param num - номер переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     * @return результат подстановки
     */
    public Element integralSecondVar(Element p, int num, Element elem1, Element elem2, Ring ring) {
        //p = p.ordering(ring);
        if (num == 0) {
            Element res1 = p.value(new Element[] {elem2, ring.numberZERO, ring.numberZERO}, ring);
            Element res2 = p.value(new Element[] {elem1, ring.numberZERO, ring.numberZERO}, ring);
            return res1.subtract(res2, ring);
        }
        if (num == 1) {
            Element res1 = p.value(new Element[] {ring.numberZERO, elem2, ring.numberZERO}, ring);
            Element res2 = p.value(new Element[] {ring.numberZERO, elem1, ring.numberZERO}, ring);
            return res1.subtract(res2, ring);

        }

        return null;
    }

    /**
     * процедура вычисления интеграла вида \int{ (ax^2+bx+c)^1/2 dx
     *
     */
    public Element radicalIntegral(Element p, Element[] c, int[] b, Ring ring) {
        Element result = ring.numberZERO;
        Polynom p1 = (Polynom) integratePamentricPolynomFunc.integral1(p, ring);
        Polynom I_one = (Polynom) integralFirstVar(p1, b[0], c[0], c[1], ring);
        Polynom res = I_one.integrate(b[1], ring);
        Element I_two = integralSecondVar(res, b[1], c[2], c[3], ring);
        return I_two;
    }

    /**
     * процедура вычисления двойного интеграла от полинома
     * @param p - интегрируемая функция
     * @param xxyy - пределы интегрирования [x1,x2,y1,y2]
     * @param b - номера переменных [0,1] bkb [1,0]
     * @param ring - кольцо
     * @return  двойной интеграл интеграл от p по прямоугольной области xxyy
     */
    public Element doubleIntegral(Element p, Element[] xxyy, int[] b, Ring ring) {

        Element p1 = p.integrate(b[0], ring); // интеграл по переменной B[0]
        Element I_one = integralFirstVar(p1, b[0], xxyy[0], xxyy[1], ring);
        Element res = I_one.integrate(b[1], ring);
        Element I_two = integralSecondVar(res, b[1], xxyy[2], xxyy[3], ring);
        return I_two;
    }

    /** Пусть S - гладкая поверхность, заданная уравненим z=z(x,y), x,y из D,
     * где D -замкнутая ограниченная область, а f(x,y,z) - некоторая ограниченная
     * функция, определенная на поверхности S. Тогда справедливо равенство
     *
     * \int\intf(x,y,z) =\int\int f(x,y,z(x,y))*((1+Z'_x+Z'_y)^1/2);
     *
     * @param p - некоторая ограниченная функция
     * @param pol - уравнение поверхности, на которой ограничена p
     * @param c - пределы интегрирования
     * @param b - порядок интегрированния переменных
     * @param ring - кольцо
     * @return результат вычисления
     */
    public Element surfaceIntegralFirstType(Element p, Element pol, Element[] c, int[] b, Ring ring) {
        Element result = ring.numberZERO;
        Element t = null;
        Element one = ring.numberONE;
        Element t1 = ring.numberZERO;
        Element[] der = derivativeXY(pol, ring);
        Element Z_x = der[0].multiply(der[0], ring);
        Element Z_y = der[1].multiply(der[1], ring);
        Element temp = (one.add(Z_x, ring).add(Z_y, ring)).sqrt(ring);
        t = substitute(p, pol, ring);
        t = t.multiply(temp, ring);
        // if((temp instanceof Polynom)){
//                 t1 = radicalIntegral(t, c, b, ring);
        //return result;
        // }else{
        Element res = doubleIntegral(t, c, b, ring);
        result = result.add(res, ring);

        return result;
        // }
    }

    /**
     * процедура вычисления поверхностных интегралов второго рода от функции,заданной полиномом
     * @param area - векторное поле
     * @param pol - полином
     * @param c - пределы интегрирования
     * @param b - номера переменных
     * @param k - компонент вектора-нормали:
     * если к == true, то он положителен
     * если к == false, то он отрицателен
     * @param ring - кольцо
     * @return результат вычисления
     */
    public Element surfaceIntegralSecondType(Element[] area, Element pol, Element[] c, int[] b, boolean k, Ring ring) {
        Element one = ring.numberONE;
        Element minus_one = ring.numberMINUS_ONE;
        Element result = ring.numberZERO;
        Element p;
        Element[] IJK = new Element[3];

        Element[] der = derivativeXY(pol, ring);
        if (k == true) {
            IJK[0] = minus_one.multiply(der[0], ring);
            IJK[1] = minus_one.multiply(der[1], ring);
            IJK[2] = one;
        } else {
            IJK[0] = one.multiply(der[0], ring);
            IJK[1] = one.multiply(der[1], ring);
            IJK[2] = minus_one;
        }
        Element temp1 = area[0].multiply(IJK[0], ring);
        Element temp2 = area[1].multiply(IJK[1], ring);
        Element temp3 = area[2].multiply(IJK[2], ring);
        p = (temp1.add(temp2, ring).add(temp3, ring));
        p = substitute(p, pol, ring);
        Element res = doubleIntegral(p, c, b, ring);
        result = result.add(res, ring);
        return result;
    }
}
