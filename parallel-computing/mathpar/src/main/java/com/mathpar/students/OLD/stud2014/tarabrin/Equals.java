package com.mathpar.students.OLD.stud2014.tarabrin;

import com.mathpar.polynom.*;
import com.mathpar.number.*;

/**
 *
 * @author Тарабрин Владимир 25 группа ТГУ'11
 * решение алгебраических уравнений до 3 степени с действительными коэффициентам
 * для случая действительных корней
 *
 * главный метод класса - solve(Polynom p, Ring ring)
 *
 * solve1(Polynom p) -приводит полином p к виду ax+b
 * solve2(Polynom p) -приводит полином p к виду ax^2+bx+c
 * solve3(Polynom p) -приводит полином p к виду ax^3+bx^2+cx+d
 *
 * solveEquals1(Polynom p, Ring ring) -вычисляет корни уравнения 1 степени
 * solveEquals2(Polynom p, Ring ring) -вычисляет корни уравнения 2 степени
 * solveEquals3(Polynom p, Ring ring) -вычисляет корни уравнения 3 степени
 *
 */
public class Equals {

    public Equals() {}

    /**
     * solve опрделяет степень уравнения и передаёт соответствующему методу
     * @param  (Polynom) p- уравнение до 3 степени
     * @return  (Element[]) res- массив корней уравнения p
     *
     */
    public Element[] solve(Polynom p, Ring ring) {
        ring.setDefaulRing();

        if (p.powers.length == 0) {
            Element[] x = {};
            return x;

        }
        int powS = p.powers[0];
        Element[] res = new Element[]{};
        switch (powS) {
            case (3):
                res = Equals.solveEquals3(p, ring); // решение уравнения 3-й степени
                break;
            case (2):
                res = Equals.solveEquals2(p, ring); // решение уравнения 2-ой степени
                break;
            case (1):
                res = Equals.solveEquals1(p, ring);  // решение уравнения 1-ой степени
                break;
        }
        return res;
    }

    /**
     * solve1 дополняте неполное уравнение 1-ой степени до полного
     * @param (Polynom) p- степени = 1
     * @return (Polynom) p- вида ax+c
     */
    public Polynom solve1(Polynom p) {
        Element[] coef = new Element[2];
        int[] pow = new int[2];
        if (p.powers.length == 2) {
            return p;
        } else {
            pow[0] = 0;
            pow[1] = 0;
            coef[0] = p.coeffs[0];
            coef[1] = new NumberR64(0);

        }
        return new Polynom(pow, coef);
    }

    /**
     * solve2 дополняет неполное квадратное уравнение до полного
     * @param (Polynom) p- степени = 2
     * @return (Polynom) p- вида ax^2+bx+c
     *
     */
    public Polynom solve2(Polynom p) {
        Element[] coef = new Element[3];
        int[] pow = new int[3];
        Polynom res = new Polynom();
        int k = p.powers.length;
        switch (k) {
            case (3):
                return p;
            case (2): {
                if (p.powers[1] == 1) {
                    pow[0] = 2;
                    pow[1] = 1;
                    pow[2] = 0;
                    coef[0] = p.coeffs[0];
                    coef[1] = p.coeffs[1];
                    coef[2] = new NumberR64(0);
                    res = new Polynom(pow, coef);
                    break;
                } else {
                    pow[0] = 2;
                    pow[1] = 0;
                    pow[2] = 0;
                    coef[0] = p.coeffs[0];
                    coef[1] = new NumberR64(0);
                    coef[2] = p.coeffs[1];
                    res = new Polynom(pow, coef);
                    break;

                }

            }
            case (1): {
                pow[0] = 2;
                pow[1] = 0;
                pow[2] = 0;
                coef[0] = p.coeffs[0];
                coef[1] = new NumberR64(0);
                coef[2] = new NumberR64(0);
                res = new Polynom(pow, coef);
                break;
            }
        }
        return res;

    }

    /**
     * solve3 дополняте неполное кубическое уравнение до полного
     * @param (Polynom) степени = 3
     * @return (Polynom) p вида ax^3+bx^2+cx+d
     */
    public Polynom solve3(Polynom p) {
        Polynom res = new Polynom();
        Element[] coef = new Element[4];
        int[] pow = new int[4];
        int k = p.powers.length;
        switch (k) {
            case (4): {
                res = p;
                break;
            }
            case (3): {
                int kk = p.powers[1];
                switch (kk) {
                    case (2): {
                        if (p.powers[2] == 1) {

                            pow[0] = 3;
                            pow[1] = 2;
                            pow[2] = 1;
                            pow[3] = 0;

                            coef[0] = p.coeffs[0];
                            coef[1] = p.coeffs[1];
                            coef[2] = p.coeffs[2];
                            coef[3] = new NumberR64(0);
                            res = new Polynom(pow, coef);
                        } else {
                            pow[0] = 3;
                            pow[1] = 2;
                            pow[2] = 1;
                            pow[3] = 0;

                            coef[0] = p.coeffs[0];
                            coef[1] = p.coeffs[1];
                            coef[2] = new NumberR64(0);
                            coef[3] = p.coeffs[2];
                            res = new Polynom(pow, coef);

                        }
                        break;

                    }
                    case (1): {
                        pow[0] = 3;
                        pow[1] = 2;
                        pow[2] = 1;
                        pow[3] = 0;
                        coef[0] = p.coeffs[0];
                        coef[1] = new NumberR64(0);
                        coef[2] = p.coeffs[1];
                        coef[3] = p.coeffs[2];
                        res = new Polynom(pow, coef);
                        break;

                    }
                }
                break;

            }
            case (2): {
                switch (p.powers[1]) {
                    case (2): {
                        pow[0] = 3;
                        pow[1] = 2;
                        pow[2] = 1;
                        pow[3] = 0;
                        coef[0] = p.coeffs[0];
                        coef[1] = p.coeffs[1];
                        coef[2] = new NumberR64(0);
                        coef[3] = new NumberR64(0);
                        res = new Polynom(pow, coef);
                        break;

                    }
                    case (1): {
                        pow[0] = 3;
                        pow[1] = 2;
                        pow[2] = 1;
                        pow[3] = 0;
                        coef[0] = p.coeffs[0];
                        coef[1] = new NumberR64(0);
                        coef[2] = p.coeffs[1];
                        coef[3] = new NumberR64(0);
                        res = new Polynom(pow, coef);
                        break;

                    }
                    case (0): {
                        pow[0] = 3;
                        pow[1] = 2;
                        pow[2] = 1;
                        pow[3] = 0;
                        coef[0] = p.coeffs[0];
                        coef[1] = new NumberR64(0);
                        coef[2] = new NumberR64(0);
                        coef[3] = p.coeffs[1];
                        res = new Polynom(pow, coef);
                        break;

                    }

                }
                break;


            }
            case (1): {
                pow[0] = 3;
                pow[1] = 2;
                pow[2] = 1;
                pow[3] = 0;
                coef[0] = p.coeffs[0];
                coef[1] = new NumberR64(0);
                coef[2] = new NumberR64(0);
                coef[3] = new NumberR64(0);
                res = new Polynom(pow, coef);
                break;


            }
        }
        return res;
    }

    /**
     * solveEquals1 вычисляет значения уравнения 1-ой степени вида ax+b=0
     * @param (Polynom) p- полином степени = 1
     * @return Element[] -массив корней уравнения
     */
    public static Element[] solveEquals1(Polynom p, Ring ring) {
        ring.setDefaulRing();
        p = p.toPolynom(ring);
        p = new Equals().solve1(p);
        Element a = p.coeffs[0];
        Element b = p.coeffs[1];
        Element[] res = new Element[1];
        res[0] = (b.negate(ring)).divide(a, ring);
        return res;

    }

    /**
     * solveEquals2 вычисляет действительные корни уравнения 2 -ой степени вида ax^2+bx+c=0
     * @param (Polynom) p- полином степени =2
     * @return Element[] -массив корней уравнения
     */
    public static Element[] solveEquals2(Polynom p, Ring ring) {
        ring.setDefaulRing();
        p = p.toPolynom(ring);
        p = new Equals().solve2(p);
        Element a = p.coeffs[0];
        Element b = p.coeffs[1];
        Element c = p.coeffs[2];
        Element ac4 = (new NumberR64(4)).multiply(a.multiply(c, ring), ring);
        Element bb = b.multiply(b, ring);
        Element D = bb.subtract(ac4, ring);
        Element[] res = new Element[]{};


        if (D.compareTo(NumberR64.ZERO, 2, ring)) {
            Element l1 = (b.negate(ring).add(D.sqrt(ring), ring)).divide(new NumberR64(2).multiply(a, ring), ring);
            Element l2 = (b.negate(ring).subtract(D.sqrt(ring), ring)).divide(new NumberR64(2).multiply(a, ring), ring);

            if (l1.compareTo(l2, 0, ring)) {

                res = new Element[]{l1};
            } else {
                res = new Element[]{l1, l2};
            }


        }
        if (D.compareTo(NumberR64.ZERO, 0, ring)) {
            Element l1 = (b.negate(ring).add(D.sqrt(ring), ring)).divide(new NumberR64(2).multiply(a, ring), ring);
            res = new Element[]{l1};
        }



        return Array.sortUp(res, ring);





    }

    /**
     * solveEquals3 вычисляет действительные корни кубического уравнения вида ax^3+bx^2+cx+d=0
     * методом Виетта-Кордана
     * @param (Polynom) p- полином степени = 3
     * @return Element[] -массив корней уравнения отсоритированный по возрастанию
     */
    public static Element[] solveEquals3(Polynom p, Ring ring) {
        ring.setDefaulRing();
        p = p.toPolynom(ring);
        p = new Equals().solve3(p);
        Element[] res = null;
        Element x1 = null;
        Element x2 = null;
        Element x3 = null;
        Element two = new NumberR64(2);
        Element three = new NumberR64(3);
        Element a = p.coeffs[0];
        Element b = p.coeffs[1];
        Element c = p.coeffs[2];
        Element d = p.coeffs[3];
        /**
         * перешли к уравнению вида x^3+ax^2+bx+c=0
         */
        Element A = b.divide(a, ring);
        Element B = c.divide(a, ring);
        Element C = d.divide(a, ring);
        Element AA = A.pow(2, ring);
        Element q1 = AA.subtract((three).multiply(B, ring), ring);
        Element Q = q1.divide(new NumberR64(9), ring); //(a^2-3b)/9
        Element AAA2 = (A.pow(3, ring)).multiply(two, ring);
        Element ab9 = (new NumberR64(9)).multiply(A, ring).multiply(B, ring);
        Element c27 = (new NumberR64(27)).multiply(C, ring);
        Element R = (AAA2.subtract(ab9, ring).add(c27, ring)).divide(new NumberR64(54), ring);
        Element R2 = R.pow(2, ring);
        Element Q3 = Q.pow(3, ring);

        if (R2.compareTo(Q3, -2, ring)) { // если R^2 < Q^3  то.... в условии было -2

            /**
             * то три действительных корня, которые вычисляем по формулам Виетта
             * t=(arcos(R/sqt(Q^3)))/3
             * x1=-2*sqrt(Q)*cos(t)-A/3
             * x2=-2*sqrt(Q)*cos(t+(2pi)/3)-A/3
             * x3=-2*sqrt(Q)*cos(t-(2pi)/3)-A/3
             */
            Element sRQ = R.divide((Q3.sqrt(ring)), ring);
            Element t = (new NumberR64(Math.acos(sRQ.doubleValue()))).divide(three, ring);
            Element cost = new NumberR64(Math.cos(t.doubleValue()));
            Element pi = new NumberR64(Math.PI);
            Element pi3a = t.add(((two.multiply(pi, ring)).divide(three, ring)), ring);// t+2pi/3
            Element pi3s = t.subtract(((two.multiply(pi, ring)).divide(three, ring)), ring); // t-2pi/3
            Element cospia = new NumberR64(Math.cos(pi3a.doubleValue()));
            Element cospis = new NumberR64(Math.cos(pi3s.doubleValue()));
            Element sQ = (Q.abs(ring)).sqrt(ring);
            Element sQ2 = ((two.negate(ring)).multiply(sQ, ring));
            Element a3 = A.divide(three, ring);

            x1 = sQ2.multiply(cost, ring).subtract(a3, ring);
            x2 = sQ2.multiply(cospia, ring).subtract(a3, ring);
            x3 = sQ2.multiply(cospis, ring).subtract(a3, ring);

            res = new Element[3];
            res[0] = x1;
            res[1] = x2;
            res[2] = x3;

        }
        if (R2.compareTo(Q3, 1, ring)) { //в условии было 1

            /**
             * если  R^2>=Q^3, то действительных корней один (общий случай)
             * или два (вырожденные случаи).
             * Для их нахождения вычисляются (формула Кардано):
             * A1=-sign(R)[|R|+sqrt(R2-Q3)]1/3
             * B1=Q/A1 при A1!=0 или B1=0 при A1=0.
             * Действительный корень будет: x1=(A1+B1)-A/3.
             * Если A1=B1, то комплексно-сопряженные корни вырождаются в действительный:
             * x2=-A1-A/3.
             */
            Element A1 = null;
            Element B1 = null;
            Element sqRQ = ((R2.subtract(Q3, ring)).abs(ring)).sqrt(ring);
            Element ss = (R.abs(ring)).add(sqRQ, ring);
            Element cuRQ = new NumberR64(Math.cbrt(ss.doubleValue()));
            Element signR = (new NumberR64(R.signum())).negate(ring);
            A1 = signR.multiply(cuRQ, ring);

            if (A1.isZero(ring)) {
                B1 = NumberR64.ZERO;

            } else {
                B1 = Q.divide(A1, ring);
            }

            if (A1.compareTo(B1, 0, ring)) {
                x1 = (A1.add(B1, ring)).subtract(A.divide(three, ring), ring);
                x2 = A1.negate(ring).subtract(A.divide(three, ring), ring);

                res = new Element[2];
                res[0] = x1;
                res[1] = x2;

            } else {
                x1 = (A1.add(B1, ring)).subtract(A.divide(three, ring), ring);
                res = new Element[1];
                res[0] = x1;
            }
        }

        switch(res.length){
            case (1):{
                return res;

            }
            case (2):{
                if (res[0].compareTo(res[1], 0, ring)){
                    return  new Element[]{res[0]};
                }
                break;

            }
            case (3):{
                Boolean f1 = res[0].doubleValue() == res[1].doubleValue();
                Boolean f2 = res[0].doubleValue() == res[2].doubleValue();
                Boolean f3 = res[1].doubleValue() == res[2].doubleValue();
                if(f1&f2&f3){
                    return new Element[]{res[0]};
                }
                break;



            }
        }


        return Array.sortUp(res, ring);


    }
    public static void main(String[] args) {
        Ring ring = new Ring("R64[x]");
        ring.setDefaulRing();
        Polynom p = new Polynom("(x-5)(x+2)(x-1)", ring);
        Element[] res = new Equals().solve(p, ring);
        System.out.println("result = "+ Array.toString(res));
        System.out.println("f== "+p);
    }
}
