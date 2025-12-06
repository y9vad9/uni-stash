package com.mathpar.students.OLD.stud2014.arharova;

import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.splines.*;

/**
 *
 * @author arharova
 */
public class SurfacePhysics {
    /**
     * процедура, вычисляющая первообразную по перменной k, которая является
     * полиномом
     *
     * @param p - полином
     * @param k - номер переменной
     * @param ring - кольцо от переменных
     *
     * @return первообразная по переменной k
     */
    public Polynom threeIntegral(Polynom p, int k, Ring ring) {
        Element one = ring.numberONE;//единица

        int n = p.powers.length / p.coeffs.length;//количество переменных
        if (p.isItNumber()) {
            return p.multiply(ring.varPolynom[k], ring);
        }
        if (p.isZero(ring)) {
            return p;
        }
        if (k >= p.powers.length) {
            return p.multiply(ring.varPolynom[k], ring);

        }
        int type = p.coeffs[0].numbElementType();

        int[] pow = new int[p.powers.length];//новый массив степеней
        System.arraycopy(p.powers, 0, pow, 0, p.powers.length);
        Element[] coef = new Element[p.coeffs.length];//новый массив коэффициентов
        int h = 0;
        if ((type == ring.Z) || (type == Ring.Z64) || (type == Ring.Q)
                || (type == ring.CZ) || (type == Ring.CZ64) || (type == Ring.CQ)) {
            while (k < p.powers.length) {
                pow[k] = p.powers[k] + 1;
                Fraction M = new Fraction(NumberZ.ONE, new NumberZ(pow[k]));
                coef[h] = (p.coeffs[h]).multiply(M, ring);
                k += n;
                h++;
            }
        } else {
            while (k < p.powers.length) {
                pow[k] = p.powers[k] + 1;
                coef[h] = (p.coeffs[h]).multiply(one.divide(new NumberR64(pow[k]), ring), ring);
                k += n;
                h++;
            }
        }

        return new Polynom(pow, coef);

    }

    /**
     * процедура подстановки пределов интегрирования вместо первой переменной
     *
     * @param p - полином от трех переменных
     * @param num - номер переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     *
     * @return результат подстановки
     */
    public Element integralFirstVar(Polynom p, int num, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
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

        Element res1 = p.value(new Element[] {ring.varPolynom[0], ring.varPolynom[1], elem2}, ring);
        Element res2 = p.value(new Element[] {ring.varPolynom[0], ring.varPolynom[1], elem1}, ring);
        return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
    }

    /**
     * процедура подстановки пределов интегрирования вместо второй переменной
     *
     * @param p - полином от трех переменных
     * @param num1 - номер переменной
     * @param num2 - номер проинтегрированной переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     *
     * @return результат подстановки
     */
    public Element integralSecondVar(Polynom p, int num1, int num2, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
        if (num1 == 0) {//переменная, в которую ведется подстановка
            if (num2 == 1) {//замороженная переменная
                Element res1 = p.value(new Element[] {elem2, ring.numberZERO, ring.varPolynom[2]}, ring);
                Element res2 = p.value(new Element[] {elem1, ring.numberZERO, ring.varPolynom[2]}, ring);
                return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
            }

            Element res1 = p.value(new Element[] {elem2, ring.varPolynom[1], ring.numberZERO}, ring);
            Element res2 = p.value(new Element[] {elem1, ring.varPolynom[1], ring.numberZERO}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

        }
        if (num1 == 1) {//переменная, в которую ведется подстановка
            if (num2 == 0) {//замороженная переменная
                Element res1 = p.value(new Element[] {ring.numberZERO, elem2, ring.varPolynom[2]}, ring);
                Element res2 = p.value(new Element[] {ring.numberZERO, elem1, ring.varPolynom[2]}, ring);
                return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

            }

            Element res1 = p.value(new Element[] {ring.varPolynom[0], elem2, ring.numberZERO}, ring);
            Element res2 = p.value(new Element[] {ring.varPolynom[0], elem1, ring.numberZERO}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

        }
        if (num2 == 0) {//замороженная переменная
            Element res1 = p.value(new Element[] {ring.numberZERO, ring.varPolynom[1], elem2}, ring);
            Element res2 = p.value(new Element[] {ring.numberZERO, ring.varPolynom[1], elem1}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
        }
        Element res1 = p.value(new Element[] {ring.varPolynom[0], ring.numberZERO, elem2}, ring);
        Element res2 = p.value(new Element[] {ring.varPolynom[0], ring.numberZERO, elem1}, ring);
        return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

    }

    /**
     * процедура подстановки пределов интегрирования вместо третьей переменной
     *
     * @param p - полином от трех переменных
     * @param num - номер переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     *
     * @return результат подстановки
     */
    public Element integralThirdVar(Polynom p, int num, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
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
        Element res1 = p.value(new Element[] {ring.numberZERO, ring.numberZERO, elem2}, ring);
        Element res2 = p.value(new Element[] {ring.numberZERO, ring.numberZERO, elem1}, ring);
        return res1.subtract(res2, ring);

    }

    /**
     * процедура нахождения массы тела
     *
     * @param p - полином от трех переменных
     * @param b - номера переменных
     * @param c - пределы интегрирования, которые могут быть выражены: по z:
     * полином от двух переменных или от одной переменной или число по y:
     * полином от одной переменных или число по х: число
     * @param ring - кольцо от трех переменных
     *
     * @return масса тела
     */
    public Element objectMass(Polynom p, int[] b, Element[] c, Ring ring) {
        Polynom p1 = (Polynom) threeIntegral(p, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p1, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        Polynom res2 = threeIntegral(I_two, b[2], ring);//вычисление интеграла от полинома по третей переменной
        Element I_three = integralThirdVar(res2, b[2], c[4], c[5], ring);//подстановка пределов интегрирования вместо третей переменной
        return I_three;
    }

    /**
     * Процедура нахождения массы оболочки Пусть S представляет собой тонкую
     * гладкую оболочку. Распределение массы оболочки описывается функцией
     * плотности u(x,y,z) . Тогда полная масса оболочки выражается через
     * поверхностный интеграл первого рода по формуле: m = \int\int u(x,y,z) dS;
     * Поверхность S задана уравнением , где z (x,y) − дифференцируемая функция
     * в области D (x,y), поверхностный интеграл находится по формуле:
     * \int\intf(x,y,z) =\int\int f(x,y,z(x,y))*((1+Z'_x+Z'_y)^1/2);
     *
     * @param p - некоторая ограниченная функция
     * @param pol - уравнение поверхности, на которой ограничена p
     * @param c - пределы интегрирования
     * @param b - порядок интегрированния переменных
     * @param ring - кольцо
     *
     * @return результат вычисления
     */
    public Element massOfShell(Element p1, Element p2, Element[] c, int[] b, Ring ring) {
        Element m = ring.numberZERO;
        m = new SurfaceIntegral().surfaceIntegralFirstType(p1, p2, c, b, ring);

        return m;

    }

    /**
     * Процедура нахождения заряд поверхности
     *
     * @param p - некоторая ограниченная функция
     * @param pol - уравнение поверхности, на которой ограничена p
     * @param c - пределы интегрирования
     * @param b - порядок интегрированния переменных
     * @param ring - кольцо
     *
     * @return результат вычисления
     */
    public Element surfaceCharge(Element p1, Element p2, Element[] c, int[] b, Ring ring) {
        Polynom p1_1 = (Polynom) threeIntegral((Polynom) p1, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p1_1, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        return I_two;

    }

    /**
     * Пусть задана поверхность S, а в точке (x_0, y_0, z_0), не принадлежащей
     * поверхности, находится тело массой m. Сила притяжения между поверхностью
     * S и точечным телом m определяется выражением: F = G_m\int\intf(x,y,z)
     * r/r^3 dS где r = (x-x_0,y-y_0,z-z_0), G - гравитационная постоянная
     * 6,67384(80)*10 ⁻¹¹ , f(x.y.z)− функция плотности
     *
     * @param p1
     * @param p2
     * @param c
     * @param b
     * @param ring
     *
     * @return
     */
//    public Element attractiveForce(Element p1,Element[] a0, Element[] c, int[] b, Ring ring){
//        VectorS vect;
//        Element zero = ring.numberZERO;
//        vect = new VectorS(3,zero);
//        Polynom r1 = new Polynom("x-2",ring);
//        r1.toVector(zero, ring);
//
//
//    }
    /**
     * момент инерции
     */
    /**
     *
     * @param p1
     * @param n
     * @param c
     * @param b
     * @param ring
     *
     * @return
     */
    public Element momentofIn(Element p1, Polynom n, Element[] c, int[] b, Ring ring) {
        Polynom po = new Polynom("x^2+y^2", ring);
        Element res;
        Element p1_1 = p1.multiply(po, ring);
        Polynom p1_2 = (Polynom) threeIntegral((Polynom) p1_1, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p1_2, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        return I_two;
    }

    /**
     * Пусть величина является плотностью распределения заряда по поверхности.
     * Тогда полный заряд, распределенный по проводящей поверхности S выражается
     * формулой
     *
     * @param p1
     * @param n
     * @param c
     * @param b
     * @param ring
     *
     * @return
     */
    public Element surfaceCharge(Element p1, Polynom n, Element[] c, int[] b, Ring ring) {
        Polynom p1_1 = (Polynom) threeIntegral((Polynom) p1, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p1_1, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        return I_two;

    }

    /**
     *
     * @param p1
     * @param n
     * @param c
     * @param b
     * @param k
     * @param ring
     *
     * @return
     */
    public Element fluidFlow(Element p1, Polynom n, Element[] c, int[] b, boolean k, Ring ring) {
        Element o = new Element(-1);
        if (k == false) {
            p1.multiply(o, ring);
        }

        Polynom p1_1 = (Polynom) threeIntegral((Polynom) p1, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p1_1, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        return I_two;
    }

    /**
     * поверхность S задана вектором и находится под воздействием некоторой силы
     * давления (это может быть плотина, крыло самолета, стенка баллона со
     * сжатым газом и т.д.). Полная сила , созданная давлением , находится с
     * помощью поверхностного интеграла по формуле F = \int\int p(r)dS Давление,
     * по определению, действует в направлении вектора нормали к поверхности S в
     * каждой точке. Поэтому, мы можем записать: F = \int\int p(r)dS = \int\int
     * p n dS где n − единичный нормальный вектор к поверхности S.
     *
     * @param p1
     * @param a0
     * @param c
     * @param b
     * @param ring
     *
     * @return
     */
    public Element pressureForce(Polynom p1, Polynom n, Element[] a0, Element[] c, int[] b, Ring ring) {
        Polynom p = p1.multiply(n, ring);
        p = (Polynom) threeIntegral(p1, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p1, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        //Element res = I_two.multiply(n, ring);
        return I_two;
    }

    /**
     * процедура нахождения координат центра массы тела
     *
     * @param p - полином от трех переменных
     * @param m - масса тела
     * @param b - номера переменных
     * @param c - пределы интегрирования, которые могут быть выражены: по z:
     * полином от двух переменных или от одной переменной или число по y:
     * полином от одной переменных или число по х: число
     * @param ring - кольцо от трех переменных
     *
     * @return координаты центра массы
     */
    public Element[] centreObjectMass(Polynom p, Element m, int[] b, Element[] c, Ring ring) {
        Element one = ring.numberONE;//единица
        Element temp = one.divide(m, ring);
        Polynom x = p.multiply(ring.varPolynom[0], ring);
        Polynom y = p.multiply(ring.varPolynom[1], ring);
        Polynom z = p.multiply(ring.varPolynom[2], ring);
        Element x0 = temp.multiply((Element) objectMass(x, b, c, ring), ring);//первая координата центра массы тела
        Element y0 = temp.multiply((Element) objectMass(y, b, c, ring), ring);//вторая координата центра массы тела
        Element z0 = temp.multiply((Element) objectMass(z, b, c, ring), ring);//третья координата центра массы тела
        Element[] centr = new Element[] {x0, y0, z0};//координаты центра массы тела
        return centr;
    }

    /**
     * процедура нахождения общей массы
     *
     * @param p - полином от трех переменных
     * @param c - пределы интегрирования каждого сплайна
     * @param pol - массив сплайнов(полиномов)
     * @param b - номера переменных
     * @param ring - кольцо от трех переменных
     *
     * @return общая масса
     */
    public Element generalMass(Polynom p, Element[][] c, Polynom[][] pol1, Polynom[][] pol2, int[] b, Ring ring) {
        Element genMass = new NumberR64(0);
        for (int i = 0; i < pol1.length; i++) {
            for (int j = 0; j < pol1[i].length; j++) {
                c[i][1] = pol1[i][j];
                c[i][0] = pol2[i][j];
                Element mass = objectMass(p, b, c[i], ring);
                genMass = genMass.add(mass, ring);

            }
        }
        return genMass;
    }

    /**
     * процедура нахождения общего центра массы
     *
     * @param p - полином от трех переменных
     * @param genMass - общая масса тела
     * @param c - пределы интегрирования каждого сплайна
     * @param pol - массив сплайнов
     * @param b - номера переменных
     * @param ring - кольцо от трех переменных
     *
     * @return координаты центра
     */
    public Element[] generalCentreObjectMass(Polynom p, Element genMass, Element[][] c, Polynom[][] pol1, Polynom[][] pol2, int[] b, Ring ring) {
        Element[] temp = new Element[] {new NumberR64(0), new NumberR64(0), new NumberR64(0)};
        for (int i = 0; i < pol1.length; i++) {
            for (int j = 0; j < pol1[i].length; j++) {
                c[i][1] = (Element) pol1[i][j];
                c[i][0] = (Element) pol2[i][j];
                Element temp1 = objectMass(p, b, c[i], ring);//масса тела одной части
                Element[] temp2 = centreObjectMass(p, temp1, b, c[i], ring);//координаты центра массы одной части
                Element[] temp3 = new Element[] {temp1.multiply(temp2[0], ring), temp1.multiply(temp2[1], ring), temp1.multiply(temp2[2], ring)};
                temp = new Element[] {temp[0].add(temp3[0], ring), temp[1].add(temp3[1], ring), temp[2].add(temp3[2], ring)};
            }
        }
        Element[] generalcentre = new Element[] {temp[0].divide(genMass, ring), temp[1].divide(genMass, ring), temp[2].divide(genMass, ring)};
        return generalcentre;
    }

    public Element surfaceArea(Polynom p, Element[] c, int[] b, Ring ring) {
        Polynom empty = new Polynom("1", ring);
        p = (Polynom) threeIntegral(empty, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        return I_two;
    }
/**
 *
 * @param area
 * @param pol
 * @param c
 * @param b
 * @param k
 * @param ring
 * @return
 */
    public Element volumeOfB(Element[] area, Element pol, Element[] c, int[] b, boolean k, Ring ring) {
        Element V = new SurfaceIntegral().surfaceIntegralSecondType(area, pol, c, b, k, ring);
        Element q = new Element(0.3);
        V.multiply(q, ring);
        return V;
    }

    /**
     * процедура сжимающая и сдвигающая полином от двух переменных
     *
     * @param p - полином от двух переменных
     * @param k1 - коэффициент сжатия по х
     * @param k2 - коэффициент сжатия по у
     * @param b1 - перенос по х
     * @param b2 - перенос по у
     * @param ring - кольцо
     *
     * @return измененный полином
     */
    public Polynom transfer2D(Polynom p, Element k1, Element k2, Element b1, Element b2, Ring ring) {
        Element x = (ring.varPolynom[0].multiply(k1, ring)).add(b1, ring);//x = k1x + b1
        Element y = (ring.varPolynom[1].multiply(k2, ring)).add(b2, ring);//y = k2y + b2
        p = p.ordering(ring);
        Polynom p1 = (Polynom) p.value(new Element[] {x, y}, ring);
        return p1;
    }

    /**
     * процедура построения массива сплайнов по точкам, а также сжатия и сдвига
     * этих сплайнов
     *
     * @param B_x - сдвиг по х
     * @param B_y - сдвиг по у
     * @param Len_x - длина по х
     * @param Len_y - длина по у
     * @param f - массив точек
     * @param ring - кольцо
     *
     * @return массив сплайнов
     */
    public Polynom[][] splines2D(Element B_x, Element B_y, Element Len_x, Element Len_y, double[][] f, Ring ring) {
        spline2D temp;
        temp = new spline2D(f, spline1D.approxType.Linear);
        Polynom[][] p = temp.getPolynoms();
        System.out.println(Array.toString(p, ring));
        Polynom[][] pol = new Polynom[p.length][p.length];
        int n = p.length;
        Element r = new NumberR64(n);
        Element K_x = Len_x.divide(r, ring);
        Element K_y = Len_y.divide(r, ring);
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p[i].length; j++) {
                pol[i][j] = transfer2D(p[i][j], K_x, K_y, B_x, B_y, ring);
            }
        }
        return pol;
    }
}
