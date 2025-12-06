/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.yakovleva;

import java.util.Arrays;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author yakovleva 35
 * @year 2013
 */
public class Spline3D {
    private Element x[];
    private Element y[];
    private Polynom z;
    private int pow[] = new int[] {2, 1, 0};
    private int n, p;
    public Polynom[] masPol;
    static Polynom[] kasat;

    public Spline3D(Element x[], Element y[]) {
        this.x = x;
        this.y = y;
        n = x.length - 1;
        p = x.length;
        masPol = new Polynom[n];
        kasat = new Polynom[8];
    }

    /**
     * Вычисление производной в средней точке для параболы, построенной по трём
     * точкам
     *
     * @param t  - точка, в которой нужно вычислить производную
     * @param x0 - значение х в первой точке
     * @param f0 - значение функции в первой точке
     * @param x1 - значение х во второй точке
     * @param f1 - значение функции во второй точке
     * @param x2 - значение х в третьей точке
     * @param f2 - значение функции в третьей точке
     *
     * @return - возвращает значение производной
     */
    private static double diffthreepoint(double t, double x0, double f0,
            double x1, double f1, double x2, double f2) {
        double result = 0;
        double a = 0;
        double b = 0;
        t = t - x0;
        x1 = x1 - x0;
        x2 = x2 - x0;
        a = (f2 - f0 - x2 / x1 * (f1 - f0)) / (x2 * x2 - x1 * x2);
        b = f1 - f0 - a * (x1 * x1) / x1;
        result = 2 * a * t + b;
        return result;
    }

    /**
     * Вычисление производных в средних точках для кубики, построенной по
     * четырём точкам
     *
     * @param t - точка, в которой нужно вычислить производную
     * @param x0 - значение х в первой точке
     * @param f0 - значение функции в первой точке
     * @param x1 - значение х во второй точке
     * @param f1 - значение функции во второй точке
     * @param x2 - значение х в третьей точке
     * @param f2 - значение функции в третьей точке
     *
     * @return - возвращает значение производной
     */
    private static double diffcubic(double t, double x0, double f0,
            double x1, double f1, double x2, double f2, double x3, double f3) {
        double result = 0;
//         t =t - x0;
//        x1 = x1 - x0;
//        x2 = x2 - x0;
//        x3 = x3-x0;

        double znum1 = (x1 * x2 * x3 - x0 * x2 * x3) - x0 * x1 * x3 + x0 * x0 * x3 - x0 * x1 * x2 + x0 * x0 * x2 + x0 * x0 * x1 - x0 * x0 * x0;
        double znum2 = x1 * x2 * x3 - x0 * x2 * x3 - x1 * x1 * x3 + x0 * x1 * x3 - x1 * x1 * x2 + x0 * x1 * x2 + x1 * x1 * x1 - x0 * x1 * x1;
        double znum3 = x2 * x2 * x3 - x1 * x2 * x3 - x0 * x2 * x3 + x0 * x1 * x3 - x2 * x2 * x2 + x1 * x2 * x2 + x0 * x2 * x2 - x0 * x1 * x2;
        double znum4 = x3 * x3 * x3 - x2 * x3 * x3 - x1 * x3 * x3 - x0 * x3 * x3 + x1 * x2 * x3 + x0 * x2 * x3 + x0 * x1 * x3 - x0 * x1 * x2;
        double a = f0 * (-1 / znum1) + f1 * (1 / znum2) + f2 * (-1 / znum3) + f3 * (1 / znum4);


        double b = f0 * ((x3 + x2 + x1) / znum1) + f1 * ((-x3 - x2 - x0) / znum2) + f2 * ((x3 + x1 + x0) / znum3) + f3 * ((-x2 - x1 - x0) / znum4);


        double c = f0 * ((-x2 * x3 - x1 * x3 - x1 * x2) / znum1) + f1 * ((x2 * x3 + x0 * x3 + x0 * x2) / znum2) + f2 * ((-x1 * x3 - x0 * x3 - x0 * x1) / znum3) + f3 * ((-x0 * x1 * x2) / znum4);

        result = 2 * a * t * t + b * t + c;
        return result;
    }

    /**
     * Вычисление полиномов от одной переменной второй степени, которые интерполируют функцию.
     * Интерполирующие функции имеют точку минимума, поэтому стыковки между ними нет.
     *
     *
     *
     */
    public void polinom() {
        NumberR64 a;
        NumberR64 b;
        NumberR64 c, delta, c2, c3, c4, c5, c6, f, f1, f3, f4;

        Ring ring = new Ring("R64[x]");
        ring.setFLOATPOS(5);



        //Вычисляем значения производных

        NumberR64[] d = new NumberR64[p];
        d[0] = new NumberR64(diffthreepoint(x[0].doubleValue(), x[0].doubleValue(), y[0].doubleValue(), x[1].doubleValue(), y[1].doubleValue(), x[2].doubleValue(), y[2].doubleValue()));

        for (int i = 1; i <= p - 2; i++) {

            d[i] = new NumberR64(diffthreepoint(x[i].doubleValue(), x[i].doubleValue(), y[i].doubleValue(), x[i + 1].doubleValue(), y[i + 1].doubleValue(), x[i - 1].doubleValue(), y[i - 1].doubleValue()));
        }


        d[p - 1] = new NumberR64(diffthreepoint(x[p - 1].doubleValue(), x[p - 3].doubleValue(), y[p - 3].doubleValue(), x[p - 2].doubleValue(), y[p - 2].doubleValue(), x[p - 1].doubleValue(), y[p - 1].doubleValue()));

        for (int i = 0; i < n; i++) {

            delta = (NumberR64) (x[i].pow(2, ring).multiply(x[i + 1], ring).subtract(x[i].multiply(x[i + 1].pow(2, ring), ring), ring));// РѕР±С‰Р°СЏ СЃРєРѕР±РєР°, РєРѕС‚РѕСЂР°СЏ РµСЃС‚СЊ РІ Р°, b Рё СЃ
            c2 = (NumberR64) (new NumberR64(2).multiply((x[i].subtract(x[i + 1], ring)).pow(2, ring), ring));// Р•РЅР°РјРµРЅР°С‚РµР»СЊ СЃ
            c = (NumberR64) ((new NumberR64(2).multiply(y[i], ring).multiply(x[i + 1].pow(2, ring).subtract(x[i].subtract(x[i + 1], ring), ring), ring).add(new NumberR64(2).multiply(y[i + 1], ring).multiply(x[i].pow(2, ring).subtract(x[i].subtract(x[i + 1], ring), ring), ring), ring).add((d[i].add(d[i + 1], ring)).multiply(delta, ring), ring))).divide(c2, ring);


            if (x[i].equals(NumberR64.ZERO, ring)) {
                a = new NumberR64(1);
                b = (NumberR64) ((y[i + 1].subtract(y[i], ring)).subtract(x[i + 1].pow(2, ring), ring)).divide(x[i + 1], ring);
                c = (NumberR64) y[i];
            } else {
                if (x[i + 1].equals(NumberR64.ZERO, ring)) {
                    a = new NumberR64(1);
                    b = (NumberR64) ((y[i].subtract(y[i + 1], ring)).subtract(x[i].pow(2, ring), ring)).divide(x[i], ring);
                    c = (NumberR64) y[i + 1];

                } else {
                    a = (NumberR64) ((x[i + 1].multiply(y[i].subtract(c, ring), ring)).subtract(x[i].multiply(y[i + 1].subtract(c, ring), ring), ring)).divide(delta, ring);
                    b = (NumberR64) ((x[i].pow(2, ring).multiply(y[i + 1].subtract(c, ring), ring)).subtract(x[i + 1].pow(2, ring).multiply(y[i].subtract(c, ring), ring), ring)).divide(delta, ring);
                }

            }



            NumberR64[] coeffs = new NumberR64[] {a, b, c};
            Polynom pol = new Polynom(pow, coeffs);
            masPol[i] = pol;


            System.out.println("" + pol.toString(ring));
        }

        System.out.println(Arrays.toString(masPol));
    }

    /**
     * Вычисление полинома от двух переменных второй степени по каждой
     * переменной, который апрксимирует функцию, заданную 12 точками z(0,0),
     * z(0,1),z(1,0), z(1,1), z(-1,0), z(-1,1),z(0,2),z(1,2), z(2,1),z(2,0),
     * z(1,-1), z(0,-1). внутри квадрата (0,0), (0,1), (1,0), (1,1). Производные
     * по направлениям х и y в вершинах этого квадрата вычисляются как
     * касательные к соответствующим кубикам, построенным по 4 точкам. Искомый
     * полином должен проходить по точкам, стоящим в вершинах квадрата, а его
     * производные по направлениям в этих точках должны иметь наименьшее
     * среднеквадратичное отклонение от точек, полученных по кубикам.
     *
     * @param masZ - массив заданных точек
     *
     * @return - возвращает функцию z от переменных x и y
     */
    public Polynom surface(NumberR64[] masZ) {

        Ring ring = new Ring("R64[x,y]");
        ring.setFLOATPOS(4);

        NumberR64[] dmasZ = new NumberR64[8];
        NumberR64 z0 = NumberR64.ZERO;
        NumberR64 z1 = NumberR64.ONE;
        dmasZ[0] = new NumberR64(diffthreepoint(z0.doubleValue(), z0.doubleValue() - 1, masZ[4].doubleValue(), z0.doubleValue(), masZ[0].doubleValue(), z0.doubleValue() + 1, masZ[2].doubleValue()));
        dmasZ[1] = new NumberR64(diffthreepoint(z0.doubleValue(), z0.doubleValue() - 1, masZ[5].doubleValue(), z0.doubleValue(), masZ[1].doubleValue(), z0.doubleValue() + 1, masZ[3].doubleValue()));
        dmasZ[2] = new NumberR64(diffthreepoint(z1.doubleValue(), z1.doubleValue() - 1, masZ[0].doubleValue(), z1.doubleValue(),masZ[2].doubleValue(), z1.doubleValue() + 1, masZ[9].doubleValue()));
        dmasZ[3] = new NumberR64(diffthreepoint(z1.doubleValue(), z1.doubleValue() - 1, masZ[1].doubleValue(), z1.doubleValue(), masZ[3].doubleValue(), z1.doubleValue() + 1, masZ[8].doubleValue()));


        dmasZ[4] = new NumberR64(diffthreepoint(z0.doubleValue(), z0.doubleValue()-1, masZ[11].doubleValue(), z0.doubleValue(), masZ[0].doubleValue(), z0.doubleValue()+1, masZ[1].doubleValue()));
        dmasZ[5] = new NumberR64(diffthreepoint(z0.doubleValue(), z1.doubleValue()-1, masZ[0].doubleValue(), z1.doubleValue(), masZ[1].doubleValue(), z1.doubleValue()+1, masZ[6].doubleValue()));
        dmasZ[6] = new NumberR64(diffthreepoint(z1.doubleValue(), z0.doubleValue()-1, masZ[10].doubleValue(), z0.doubleValue(),masZ[2].doubleValue(), z0.doubleValue()+1, masZ[3].doubleValue()));
        dmasZ[7] = new NumberR64(diffthreepoint(z1.doubleValue(), z1.doubleValue()-1, masZ[2].doubleValue(), z1.doubleValue(), masZ[3].doubleValue(), z1.doubleValue()+1, masZ[7].doubleValue()));

//        dmasZ[0] = new NumberR64(diffcubic(z0.doubleValue(), z0.doubleValue() - 1, masZ[4].doubleValue(), z0.doubleValue(), masZ[0].doubleValue(), z0.doubleValue() + 1, masZ[2].doubleValue(), z1.doubleValue() + 1, masZ[9].doubleValue()));// производная по x в точке z(0,0)
//        dmasZ[1] = new NumberR64(diffcubic(z0.doubleValue(), z0.doubleValue() - 1, masZ[5].doubleValue(), z0.doubleValue(), masZ[1].doubleValue(), z0.doubleValue() + 1, masZ[3].doubleValue(), z1.doubleValue() + 1, masZ[8].doubleValue()));//производная по x в точке z(0,1)
//        dmasZ[2] = new NumberR64(diffcubic(z1.doubleValue(), z0.doubleValue() - 1, masZ[4].doubleValue(), z0.doubleValue(), masZ[0].doubleValue(), z0.doubleValue() + 1, masZ[2].doubleValue(), z1.doubleValue() + 1, masZ[9].doubleValue()));//производная по x в точке z(1,0)
//        dmasZ[3] = new NumberR64(diffcubic(z1.doubleValue(), z0.doubleValue() - 1, masZ[5].doubleValue(), z0.doubleValue(), masZ[1].doubleValue(), z0.doubleValue() + 1, masZ[3].doubleValue(), z1.doubleValue() + 1, masZ[8].doubleValue()));//производная по x в точке z(1,1)
//
//
//        dmasZ[4] = new NumberR64(diffcubic(z0.doubleValue(), z0.doubleValue() - 1, masZ[11].doubleValue(), z0.doubleValue(), masZ[0].doubleValue(), z0.doubleValue() + 1, masZ[1].doubleValue(), z1.doubleValue() + 1, masZ[6].doubleValue())); // производная по y в точке z(0,0)
//        dmasZ[5] = new NumberR64(diffcubic(z1.doubleValue(), z0.doubleValue() - 1, masZ[10].doubleValue(), z0.doubleValue(), masZ[2].doubleValue(), z0.doubleValue() + 1, masZ[3].doubleValue(), z1.doubleValue() + 1, masZ[7].doubleValue()));// производная по y в точке z(0,1)
//        dmasZ[6] = new NumberR64(diffcubic(z0.doubleValue(), z0.doubleValue() - 1, masZ[11].doubleValue(), z0.doubleValue(), masZ[0].doubleValue(), z0.doubleValue() + 1, masZ[1].doubleValue(), z1.doubleValue() + 1, masZ[6].doubleValue()));// производная по y в точке z(1,0)
//        dmasZ[7] = new NumberR64(diffcubic(z1.doubleValue(), z0.doubleValue() - 1, masZ[10].doubleValue(), z0.doubleValue(), masZ[2].doubleValue(), z0.doubleValue() + 1, masZ[3].doubleValue(), z1.doubleValue() + 1, masZ[7].doubleValue()));// производная по y в точке z(1,1)
        int[] powersZ = new int[] {2, 2,
            1, 2,
            0, 2,
            2, 1,
            1, 1,
            0, 1,
            2, 0,
            1, 0,
            0, 0};
        Element[] coeffsZ = new Element[9];

        coeffsZ[8] = masZ[0]; //свободный коэффициент

        coeffsZ[7] = dmasZ[0];//коэффициент при х
        coeffsZ[5] = dmasZ[4];//коэффициент при y
        coeffsZ[6] = dmasZ[2].subtract(dmasZ[0], ring).divide(new NumberR64(2), ring);//коэффициент при х^2
        coeffsZ[2] = dmasZ[5].subtract(dmasZ[4], ring).divide(new NumberR64(2), ring);//коэффициент при y^2

        coeffsZ[1] = dmasZ[7].divide(new NumberR64(3), ring).subtract(dmasZ[6].divide(new NumberR64(3), ring), ring).subtract(
                dmasZ[3].divide(new NumberR64(6), ring), ring).add(coeffsZ[6].divide(new NumberR64(3), ring), ring).subtract(
                coeffsZ[2].multiply(new NumberR64(2).divide(new NumberR64(3), ring), ring), ring).subtract(
                dmasZ[4].divide(new NumberR64(3), ring), ring).add(dmasZ[1].divide(new NumberR64(6), ring), ring);//коэффициент при хy^2

        coeffsZ[3] = dmasZ[7].divide(new NumberR64(6).negate(ring), ring).add(dmasZ[6].divide(new NumberR64(6), ring), ring).add(
                dmasZ[3].divide(new NumberR64(3), ring), ring).subtract(coeffsZ[6].multiply(new NumberR64(2).divide(new NumberR64(3), ring), ring), ring).add(
                coeffsZ[2].divide(new NumberR64(3), ring), ring).add(
                dmasZ[4].divide(new NumberR64(6), ring), ring).subtract(dmasZ[1].divide(new NumberR64(3), ring), ring);//коэффициент при х^2y

        coeffsZ[0] = dmasZ[7].divide(new NumberR64(6), ring).subtract(dmasZ[6].divide(new NumberR64(6), ring), ring).add(
                dmasZ[3].divide(new NumberR64(6), ring), ring).subtract(coeffsZ[6].divide(new NumberR64(3), ring), ring).subtract(
                coeffsZ[2].divide(new NumberR64(3), ring), ring).subtract(
                dmasZ[4].divide(new NumberR64(6), ring), ring).subtract(dmasZ[1].divide(new NumberR64(6), ring), ring);//коэффициент при х^y^2

        coeffsZ[4] = dmasZ[0].negate(ring).subtract(
                dmasZ[4].multiply(new NumberR64(2).divide(new NumberR64(3), ring), ring), ring).subtract(
                coeffsZ[8], ring).subtract(
                coeffsZ[6].divide(new NumberR64(3), ring), ring).subtract(
                coeffsZ[2].divide(new NumberR64(3), ring), ring).add(
                dmasZ[1].divide(new NumberR64(3), ring), ring).add(
                masZ[3], ring).subtract(
                dmasZ[3].divide(new NumberR64(3), ring), ring).add(
                dmasZ[3].divide(new NumberR64(3), ring), ring).subtract(
                dmasZ[7].divide(new NumberR64(3), ring), ring);//коэффициент при хy



//        NumberR64[] coeffsX0 = new NumberR64[2];
//        coeffsX0[0] = dmasZ[4];
//        coeffsX0[1] = (NumberR64) dmasZ[4].multiply(z0, ring);
//        coeffsX0[1].negate(ring);
//        coeffsX0[1] = (NumberR64) coeffsX0[1].add(masZ[0], ring);
//
//
//        kasat[0] = new Polynom(new int[] {1, 0}, coeffsX0);// касательная точке z(0,0) к линии у
//
//        NumberR64[] coeffsX2 = new NumberR64[2];
//        coeffsX2[0] = dmasZ[5];
//        coeffsX2[1] = (NumberR64) dmasZ[5].multiply(z0, ring);
//        coeffsX2[1].negate(ring);
//        coeffsX2[1] = (NumberR64) coeffsX2[1].add(masZ[1], ring);
//        kasat[2] = new Polynom(new int[] {1, 0}, coeffsX2); // касательная точке z(0,1) к линии у
//
//        NumberR64[] coeffsX1 = new NumberR64[2];
//        coeffsX1[0] = dmasZ[6];
//        coeffsX1[1] = (NumberR64) dmasZ[6].multiply(z1, ring);
//        coeffsX1[1].negate(ring);
//        coeffsX1[1] = (NumberR64) coeffsX1[1].add(masZ[2], ring);
//        kasat[1] = new Polynom(new int[] {1, 0}, coeffsX1); // касательная точке z(1,0) к линии у
//
//        NumberR64[] coeffsX3 = new NumberR64[2];
//        coeffsX3[0] = dmasZ[7];
//        coeffsX3[1] = (NumberR64) dmasZ[7].multiply(z1, ring);
//        coeffsX3[1].negate(ring);
//        coeffsX3[1] = (NumberR64) coeffsX3[1].add(masZ[3], ring);
//        kasat[3] = new Polynom(new int[] {1, 0}, coeffsX3); // касательная точке z(1,1) к линии у
//
//
//        NumberR64[] coeffsX4 = new NumberR64[2];
//        coeffsX4[0] = dmasZ[0];
//        coeffsX4[1] = (NumberR64) dmasZ[0].multiply(z0, ring);
//        coeffsX4[1].negate(ring);
//        coeffsX4[1] = (NumberR64) coeffsX4[1].add(masZ[0], ring);
//        kasat[4] = new Polynom(new int[] {1, 0}, coeffsX4);// касательная точке z(0,0) к линии x
//
//
//        NumberR64[] coeffsX5 = new NumberR64[2];
//        coeffsX5[0] = dmasZ[1];
//        coeffsX5[1] = (NumberR64) dmasZ[1].multiply(z0, ring);
//        coeffsX5[1].negate(ring);
//        coeffsX5[1] = (NumberR64) coeffsX5[1].add(masZ[1], ring);
//        kasat[5] = new Polynom(new int[] {1, 0}, coeffsX5);// касательная точке z(0,1) к линии x
//
//
//        NumberR64[] coeffsX6 = new NumberR64[2];
//        coeffsX6[0] = dmasZ[2];
//        coeffsX6[1] = (NumberR64) dmasZ[2].multiply(z1, ring);
//        coeffsX6[1].negate(ring);
//        coeffsX6[1] = (NumberR64) coeffsX6[1].add(masZ[2], ring);
//        kasat[6] = new Polynom(new int[] {1, 0}, coeffsX6);// касательная точке z(1,0) к линии x
//
//
//        NumberR64[] coeffsX7 = new NumberR64[2];
//        coeffsX7[0] = dmasZ[3];
//        coeffsX7[1] = (NumberR64) dmasZ[3].multiply(z1, ring);
//        coeffsX7[1].negate(ring);
//        coeffsX7[1] = (NumberR64) coeffsX7[1].add(masZ[3], ring);
//        kasat[7] = new Polynom(new int[] {1, 0}, coeffsX7);// касательная точке z(1,1) к линии x
//        NumberR64[] tt = new NumberR64[16];
//        tt[0] = coeffsX0[0];
//        tt[1] = coeffsX0[1];
//
//        tt[2] = coeffsX1[0];
//        tt[3] = coeffsX1[1];
//        tt[4] = coeffsX2[0];
//        tt[5] = coeffsX2[1];
//        tt[6] = coeffsX3[0];
//        tt[7] = coeffsX3[1];
//        tt[8] = coeffsX4[0];
//        tt[9] = coeffsX4[1];
//        tt[10] = coeffsX5[0];
//        tt[11] = coeffsX5[1];
//        tt[12] = coeffsX6[0];
//        tt[13] = coeffsX6[1];
//        tt[14] = coeffsX7[0];
//        tt[15] = coeffsX7[1];

//        System.out.println(Arrays.toString(kasat));

        z = new Polynom(powersZ, coeffsZ);
        return new Polynom(powersZ, coeffsZ).deleteZeroCoeff(ring);
    }

    public static Polynom[] tangent(Polynom[] p, NumberR64[] masZ, Ring ring) {
        Polynom[] result = new Polynom[p.length * 2];
        int j = 0;
        for (int i = 0; i < p.length - 2; i++) {
            Element a = ring.numberZERO;
            Element q1 = p[i].value(new Element[] {ring.numberZERO, a}, new Ring("R64[y]"));
            Polynom derev = p[i].D(1, new Ring("R64[y]"));
            Element q2 = derev.value(new Element[] {ring.numberZERO, a}, new Ring("R64[y]"));
            result[j] = (Polynom) q1.add(q2.multiply(new Polynom("y", new Ring("R64[y]")).subtract(a, new Ring("R64[y]")), new Ring("R64[y]")), new Ring("R64[y]"));
            j++;
            Element b = ring.numberONE;
            Element q3 = p[i].value(new Element[] {ring.numberZERO, b}, new Ring("R64[y]"));
            Polynom derev1 = p[i].D(1, new Ring("R64[y]"));
            Element q4 = derev1.value(new Element[] {ring.numberZERO, b}, new Ring("R64[y]"));
            result[j] = (Polynom) q3.add(q4.multiply(new Polynom("y", new Ring("R64[y]")).subtract(b, new Ring("R64[y]")), new Ring("R64[y]")), new Ring("R64[y]"));
            j++;
        }
        for (int i = p.length - 2; i < p.length; i++) {
            Element a = ring.numberZERO;
            Element q1 = p[i].value(new Element[] {a, ring.numberZERO}, new Ring("R64[y]"));
            Polynom derev = p[i].D(0, new Ring("R64[x]"));
            Element q2 = derev.value(new Element[] {a, ring.numberZERO}, new Ring("R64[x]"));
            result[j] = (Polynom) q1.add(q2.multiply(new Polynom("x", new Ring("R64[x]")).subtract(a, new Ring("R64[x]")), new Ring("R64[x]")), new Ring("R64[x]"));
            j++;
            Element b = ring.numberONE;
            Element q3 = p[i].value(new Element[] {b, ring.numberZERO}, new Ring("R64[y]"));
            Polynom derev1 = p[i].D(0, new Ring("R64[x]"));
            Element q4 = derev1.value(new Element[] {b, ring.numberZERO}, new Ring("R64[x]"));
            result[j] = (Polynom) q3.add(q4.multiply(new Polynom("x", new Ring("R64[x]")).subtract(b, new Ring("R64[x]")), new Ring("R64[x]")), new Ring("R64[x]"));
            j++;
        }
        return result;
    }

    public static void main(String[] args) {

        Ring ring = new Ring("R64[x]");
        NumberZ init = new NumberZ(1);

        long[][] matL = new long[][] {{-6, -2, -4, -1, 0, 2, 3}, {6, 5, 3, 2, 0, 1, 3}};
        MatrixD matT = new MatrixD(matL, ring);
        Element x[] = matT.M[0];
        Element y[] = matT.M[1];

        //     Element x[] = new Element[] {new NumberR64(-6), new NumberR64(-2), new NumberR64(-4), new NumberR64(-1), new NumberR64(0), new NumberR64(2), new NumberR64(3)};
        //    Element y[] = new Element[] {new NumberR64(6), new NumberR64(5), new NumberR64(3), new NumberR64(2), new NumberR64(0), new NumberR64(1), new NumberR64(3)};

        Spline3D spline = new Spline3D(x, y);

        spline.polinom();


        System.out.println(spline.masPol[0].valueOf(x[0], ring));

        System.out.println(spline.masPol[0].valueOf(x[1], ring));
        System.out.println(spline.masPol[1].valueOf(x[1], ring));


        System.out.println(spline.masPol[1].valueOf(x[2], ring));
        System.out.println(spline.masPol[2].valueOf(x[0], ring));

        System.out.println(spline.masPol[2].valueOf(x[3], ring));
        System.out.println(spline.masPol[3].valueOf(x[0], ring));

        System.out.println(spline.masPol[3].valueOf(x[4], ring));
        System.out.println(spline.masPol[4].valueOf(x[4], ring));

        System.out.println(spline.masPol[4].valueOf(x[5], ring));
        System.out.println(spline.masPol[5].valueOf(x[5], ring));

        System.out.println(spline.masPol[5].valueOf(x[6], ring));

        /////////////////////////////////////////////////////////////////////// Part 2 ////////////////////////////////////////


        long[] matZ = new long[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Element z[] = matT.M[0];

        NumberR64[] masZ = new NumberR64[] {new NumberR64(1), new NumberR64(2),
            new NumberR64(3), new NumberR64(4), new NumberR64(5),
            new NumberR64(6), new NumberR64(7),
            new NumberR64(8), new NumberR64(9),
            new NumberR64(10), new NumberR64(11),
            new NumberR64(12)};
        Polynom sdfg = spline.surface(masZ);
        Polynom pov = spline.surface(masZ);
        System.out.println("z = " + pov);
        Ring ring1 = new Ring("R64[x,y]");
        System.out.println("0,0 = " + pov.value(new Element[]{new NumberR64(0),new NumberR64(0)}, ring1));
        System.out.println("0,1 = " + pov.value(new Element[]{new NumberR64(0),new NumberR64(1)}, ring1));
        System.out.println("1,0 = " + pov.value(new Element[]{new NumberR64(1),new NumberR64(0)}, ring1));
        System.out.println("1,1 = " + pov.value(new Element[]{new NumberR64(1),new NumberR64(1)}, ring1));

        Polynom[] arraPar = new Polynom[4];

        for (int i = 0; i < 4; i++) {
            if (i <= 1) {
                arraPar[i] = (Polynom) pov.value(new Element[] {new NumberR64(i), new Polynom("y", ring1)}, ring1);
                System.out.println("Уравнение параболы в плоскости yoz: " + arraPar[i].toString(ring1));
            } else {
                arraPar[i] = (Polynom) pov.value(new Element[] {new Polynom("x", ring1), new NumberR64(i - 2)}, ring1);
                System.out.println("Уравнение параболы в плоскости xoz: " + arraPar[i].toString(ring1));
            }
        }
        Polynom[] kas = tangent(arraPar, masZ, ring);
        System.out.println("tangent : " + Array.toString(kas));
    }
}
