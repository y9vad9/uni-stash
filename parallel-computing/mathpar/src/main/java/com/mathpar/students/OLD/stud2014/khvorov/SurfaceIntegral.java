/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.khvorov;

import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.matrix.*;

/**Данный класс реализует алгоритмы для вычисления поверхностных интегралов
 * от функций, заданных сплайнами
 * @author khvorov
 */

public class SurfaceIntegral {

    /**
     * процедура нахождения производной Z_x, Z_y
     *
     * @param p - полином
     * @param t : если t = true, то дифференцирование по (x,y)
     * если t = false, то дифференцирование по (u.v)
     * @param ring - кольцо
     * @return массив производных
     */
    public Element[] derivative(Polynom p, boolean t, Ring ring) {
        Element[] c = new Element[2];
        if (t == true) {
            Element c1 = p.D(0, ring);//производная по х
            Element c2 = p.D(1, ring);//производная по у
            c = new Element[]{c1, c2};
        } else {
            Element c1 = p.D(3, ring);//производная по u
            Element c2 = p.D(4, ring);//производная по v
            c = new Element[]{c1, c2};
        }

        return c;
    }

    /**
     * процедура, заменяющая: переменную z на полином от (x,y)
     * переменную у на полином от (x,z)
     * переменную x на полином от (y,z)
     * @param p - полином
     * @param p1 - полином от (x,y)
     * @param l - номер заменяемой переменной
     * @param ring - кольцо
     * @return полином
     */
    public Polynom substitute(Polynom p, Polynom p1, int l, Ring ring) {
        if (l == 2) {
            Element x = ring.varPolynom[0];
            Element y = ring.varPolynom[1];
            Element z = (Element) p1;
            p = (Polynom) p.value(new Element[]{x, y, z}, ring);
        }
        if (l == 1) {
            Element x = ring.varPolynom[0];
            Element y = (Element) p1;
            Element z = ring.varPolynom[2];
            p = (Polynom) p.value(new Element[]{x, y, z}, ring);
        }
        if (l == 0) {
            Element x = (Element) p1;
            Element y = ring.varPolynom[1];
            Element z = ring.varPolynom[2];
            p = (Polynom) p.value(new Element[]{x, y, z}, ring);
        }
        return p;
    }

    /**
     * процедура вычисления двойного интеграла от полинома
     *
     * @param p - полином
     * @param c - пределы интегрирования
     * @param num - номер замещенной переменной
     * @param b - номера переменных
     * @param ring - кольцо
     * @return результат вычисления
     */
    public Element doubleIntegral(Polynom p, Element[] c, int num, int[] b, Ring ring) {
        Element result = ring.numberZERO;
        if (num == 2) {
            //Polynom p1 = (Polynom) new ObjectMassAndCentreObjectMass().threeIntegral(p, b[0], ring);
            Polynom p1 = p.integrate(b[0], ring);
            Polynom I_one = (Polynom) new ObjectMassAndCentreObjectMass().integralFirstVar(p1, b[0], c[0], c[1], ring);
            //Polynom res = new ObjectMassAndCentreObjectMass().threeIntegral(I_one, b[1], ring);
            Polynom res = I_one.integrate(b[1], ring);
            Element I_two = new ObjectMassAndCentreObjectMass().integralThirdVar(res, b[1], c[2], c[3], ring);
            result = I_two;
        }
        if (num == 1) {
            //Polynom p1 = (Polynom) new ObjectMassAndCentreObjectMass().threeIntegral(p, b[0], ring);
            Polynom p1 = p.integrate(b[0], ring);
            Polynom I_one = (Polynom) new ObjectMassAndCentreObjectMass().integralFirstVar(p1, b[0], c[0], c[1], ring);
            //Polynom res = new ObjectMassAndCentreObjectMass().threeIntegral(I_one, b[2], ring);
            Polynom res = I_one.integrate(b[2], ring);
            Element I_two = new ObjectMassAndCentreObjectMass().integralThirdVar(res, b[2], c[2], c[3], ring);
            result = I_two;
        }
        if (num == 0) {
            //Polynom p1 = (Polynom) new ObjectMassAndCentreObjectMass().threeIntegral(p, b[1], ring);
            Polynom p1 = p.integrate(b[1], ring);
            Polynom I_one = (Polynom) new ObjectMassAndCentreObjectMass().integralFirstVar(p1, b[1], c[0], c[1], ring);
            //Polynom res = new ObjectMassAndCentreObjectMass().threeIntegral(I_one, b[2], ring);
            Polynom res = I_one.integrate(b[2], ring);
            Element I_two = new ObjectMassAndCentreObjectMass().integralThirdVar(res, b[2], c[2], c[3], ring);
            result = I_two;
        }

        return result;
    }

    /**
     * процедура подстановки пределов интегрирования вместо первой переменной
     * после параметризации
     *
     * @param p - параметризованный полином
     * @param num - номер интегрируемой переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо
     * @return результат подстановки
     */
    public Element parametricFirstVar(Polynom p, int num, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
        if (num == 0) {
            Element res1 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, elem2, ring.varPolynom[4]}, ring);
            Element res2 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, elem1, ring.varPolynom[4]}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

        }

        Element res1 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, ring.varPolynom[3], elem2}, ring);
        Element res2 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, ring.varPolynom[3], elem1}, ring);
        return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
    }

    /**
     * процедура подстановки пределов интегрирования вместо второй переменной
     * после параметризации
     *
     * @param p - параметризованный полином
     * @param num - номер интегрируемой переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо
     * @return результат подстановки
     */
    public Element parametricSecondVar(Polynom p, int num, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
        if (num == 0) {
            Element res1 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, elem2, ring.numberZERO}, ring);
            Element res2 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, elem1, ring.numberZERO}, ring);
            return res1.subtract(res2, ring);

        }

        Element res1 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, ring.numberZERO, elem2}, ring);
        Element res2 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO, ring.numberZERO, elem1}, ring);
        return res1.subtract(res2, ring);
    }

    /**
     *
     * @param p - полином
     * @param c - массив пределов интегрирования
     * @param num - номер интегрируемой переменной
     * @param b - массив номеров переменных
     * @param ring - кольцо
     * @return результат вычисления
     */
    public Element doubleIntegralParametricForm(Polynom p, Element[] c, int num, int[] b, Ring ring) {
        if (num == 0) {// интегрирование сначала по u, затем по v
            Polynom p1 = p.integrate(3, ring);
            Polynom I_one = (Polynom) parametricFirstVar(p1, b[0], c[0], c[1], ring);
            Polynom res = I_one.integrate(4, ring);
            Element I_two = parametricSecondVar(res, b[1], c[2], c[3], ring);
            return I_two;
        }
        // интегрирование сначала по v, затем по u
        Polynom p1 = p.integrate(4, ring);
        Polynom I_one = (Polynom) parametricFirstVar(p1, b[1], c[2], c[3], ring);
        Polynom res = I_one.integrate(3, ring);
        Element I_two = parametricSecondVar(res, b[0], c[0], c[1], ring);
        return I_two;
    }

    /**
     * процедура вычисления поверхностных интегралов первого рода, от функций,
     * заданных сплайнами
     *
     * @param p - полином
     * @param pol - массив полиномов(сплайн)
     * @param c - пределы интегрирования
     * @param b - номера переменных
     * @param ring - кольцо
     * @return результат вычисления
     */
    public Element surfaceIntegralFirstType(Polynom p, Polynom[][] pol, Element[][] c, int[] b, Ring ring) {
        Element result = ring.numberZERO;
        Polynom t;
        Element one = ring.numberONE;
        for (int i = 0; i < pol.length; i++) {
            for (int j = 0; j < pol[i].length; j++) {
                Element[] der = derivative(pol[i][j], true, ring);
                Element Z_x = der[0].multiply(der[0], ring);
                Element Z_y = der[1].multiply(der[1], ring);
                Element temp = (one.add(Z_x, ring).add(Z_y, ring)).sqrt(ring);
                t = substitute(p, pol[i][j], b[2], ring);
                t = (Polynom) t.multiply(temp, ring);
                Element res = doubleIntegral(t, c[i], b[2], b, ring);
                result = result.add(res, ring);
            }
        }
        return result;
    }

    /**
     * процедура вычисления поверхностных интегралов второго рода, от функций,
     * заданных сплайнами
     *
     * @param area - векторное поле
     * @param pol - массив полиномов(сплайн)
     * @param c - пределы интегрирования
     * @param b - номера переменных
     * @param k - компонент вектора-нормали: если к == true, то он
     * положителен(поверхность ориентирована внешней нормалью) если к == false,
     * то он отрицателен(поверхность ориентирована внутренней нормалью)
     * @param ring - кольцо
     * @return результат вычисления
     */
    public Element surfaceIntegralSecondType(Element[] area, Polynom[][] pol, Element[][] c, int[] b, boolean k, Ring ring) {
        Element one = ring.numberONE;
        Element minus_one = ring.numberMINUS_ONE;
        Element result = ring.numberZERO;
        Polynom p;
        Element[] IJK = new Element[3];
        for (int i = 0; i < pol.length; i++) {
            for (int j = 0; j < pol[i].length; j++) {
                Element[] der = derivative(pol[i][j], true, ring);
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
                p = (Polynom) (temp1.add(temp2, ring).add(temp3, ring));
                p = substitute(p, pol[i][j], b[2], ring);
                Element res = doubleIntegral(p, c[i], 2, b, ring);
                result = result.add(res, ring);
            }

        }
        return result;
    }



    /**
     * параметризация полинома
     *
     * @param p - полином
     * @param uv - полиномы от u,v
     * @param ring - кольцо
     * @return параметризованный полином
     */
    public Polynom polynomParametricForm(Polynom p, Polynom[] uv, Ring ring) {
        p = (Polynom) p.value(new Element[]{uv[0], uv[1], uv[2], ring.varPolynom[3], ring.varPolynom[4]}, ring);
        return p;
    }

    /**
     * процедура вычисления поверхностного интеграла по параметризованной
     * поверхности
     *
     * @param area - векторное поле
     * @param p - массив (x(u,v), y(u,v), z(u,v))
     * @param c - пределы интегрирования
     * @param num - номер интегрируемой переменной
     * @param b - номера переменных
     * @param ring - кольцо
     * @return результат вычисления
     */
    public Element secondTypeParametricForm(Element[] area, Polynom[] p, Element[] c, int num, int[] b, Ring ring) {
        area[0] = polynomParametricForm((Polynom) area[0], p, ring);// замена P через (u,v)
        area[1] = polynomParametricForm((Polynom) area[1], p, ring);//замена Q через (u,v)
        area[2] = polynomParametricForm((Polynom) area[2], p, ring);//замена R через (u,v)
        Element[] der_x = derivative((Polynom) area[0], false, ring);//дифференцирование P по (u,v)
        Element[] der_y = derivative((Polynom) area[1], false, ring);//дифференцирование Q по (u,v)
        Element[] der_z = derivative((Polynom) area[2], false, ring);//дифференцирование R по (u,v)
        Element[][] temp = new Element[][]{{area[0], area[1], area[2]},
            {der_x[0], der_y[0], der_z[0]},
            {der_x[1], der_y[1], der_z[1]}};
        MatrixS mx = new MatrixS(temp, ring);
        Element pol = mx.det(ring);
        Element res = doubleIntegralParametricForm((Polynom) pol, c, num, b, ring);
        return res;
    }
}
