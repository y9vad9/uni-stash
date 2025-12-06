/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.shmeleva;

import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.func.*;

/**
 * Программный класс предназначенный для решения несобственных интегралов
 *
 * @author Shmeleva A.A.
 * 2012 years
 */
public class improper_integral {

    public Element lower_limit = new NumberR();//нижний предел
    public Element upper_limit = new NumberR();//верхний предел
    public Element function = null;//подинтегральная функция
    public boolean integral_converges = false;//флаг, обозначающий сходится или расходится интеграл

    public improper_integral() {
    }

    /**
     *
     * @param a - нижний предел
     * @param b - верхний предел
     * @param f - подинтегральная функция
     */
    public improper_integral(Element a, Element b, Element f) {
        lower_limit = a;
        upper_limit = b;
        function = f;
    }

    /**
     * Вычисление первообразной
     * @param f - функция входа
     * @return первообразная для функции f
     */
    public Element antiderivative(Element f, Ring ring) {
        int n = f.numbElementType(); //определение типа функции
        switch (n) {
            case Ring.Polynom: {
                return Integral.pervB(((Polynom) f), 0, ring);
            }
            case Ring.F: {
                int m = ((F) f).name;
                switch (m) {
                    case F.COS: {
                        Element el = ((F) f).X[0];
                        return new F(F.SIN, el);//sin(x)
                    }
                    case F.SIN: {
                        Element el = ((F) f).X[0];
                        F f1 = new F(F.COS, el);
                        return new F(F.MULTIPLY, new Element[]{ring.numberMINUS_ONE, f1});//-cos(x)
                    }
                    case F.EXP: { //e^{kx}
                        Polynom el = (Polynom) ((F) f).X[0];
                        Element k = el.coeffs[0];
                        if(k.isOne(ring)) return f;
                            else
                        return new F(F.DIVIDE, new Element[]{f, k});//e^{kx}/k
                    }
                    case F.LN: {//ln(x)
                        Polynom p = new Polynom(new int[]{1}, new Element[]{ring.numberONE});//x
                        F f1 = new F(F.MULTIPLY, new Element[]{p, f});//xln(x)
                        return new F(F.SUBTRACT, new Element[]{f1, p});//xln(x)-x
                    }
                    case F.LOG: {//log_{b}(x)
                        Polynom p = new Polynom(new int[]{1}, new Element[]{ring.numberONE});//x
                        F f1 = new F(F.MULTIPLY, new Element[]{p, f});//xln(x)
                        F f2 = new F(F.SUBTRACT, new Element[]{f1, p});//xln(x)-x
                        F f3 = new F(F.LN, ((F) f).X[0]);//ln(b)
                        return new F(F.DIVIDE, new Element[]{f2, f3});//(xln(x)-x) / ln(b)
                    }
                    case F.POW: {//a^{x}
                        Element a = ((F) f).X[0];
                        F f1 = new F(F.LN, a);
                        return new F(F.DIVIDE, new Element[]{f, f1});//a^{x} / ln(a)
                    }
                    case F.TG: {//tg(x)
                        Element a = ring.numberMINUS_ONE;//-1
                        F f1 = new F(F.COS, ((F) f).X[0]);//cos(x)
                        return new F(F.MULTIPLY, new Element[]{a, new F(F.LN, f1)});//-ln(cos(x))
                    }
                    case F.CTG: {//ctg(x)
                        F f1 = new F(F.SIN, ((F) f).X[0]);//sin(x)
                        return new F(F.LN, f1);//ln(sin(x))
                    }
                    case F.SH: {//sh(x)
                        return new F(F.CH, ((F) f).X[0]);//ch(x)
                    }
                    case F.CH: {//ch(x)
                        return new F(F.SH, ((F) f).X[0]);//sh(x)
                    }
                    case F.TH: {//th(x)
                        F f1 = new F(F.CH, ((F) f).X[0]);//ch(x)
                        return new F(F.LN, f1);//ln(ch(x))
                    }
                    case F.CTH: {//cth(x)
                        F f1 = new F(F.SH, ((F) f).X[0]);//sh(x)
                        return new F(F.LN, f1);//ln(sh(x)x)
                    }
                    case F.DIVIDE: {
                        F ff = ((F) f);
                        if ((ff.X[0].isOne(ring)) && (ff.X[1] instanceof Polynom)) {
                            Polynom pp = (Polynom) ff.X[1];
                            if ((pp.powers[0] != 1) && (pp.coeffs.length == 1) && (pp.powers.length == 1)) {
                                //1/x^{n} n>1
                                    int n1 = -pp.powers[0] + 1;
                                    Element n2 = ring.numberONE.multiply(ring.numberONE.valOf(n1, ring), ring);//(-n+1)*1
                                    int n3 = Math.abs(n1);
                                    Polynom ppp = new Polynom(new int[]{n3}, new Element[]{pp.coeffs[0]});//x^{n+1}
                                    F multin = new F(F.MULTIPLY, new Element[]{n2, ppp});
                                    return new F(F.DIVIDE, new Element[]{ring.numberONE, multin});// x^{n+1}/(n+1)

                            } else {
                                if (pp.powers[0] == 1) {//1/x
                                    return new F(F.LN, pp);//ln(x)
                                } else
                                //1/(x^2+a^2)
                                if ((pp.coeffs.length == 2) && (pp.powers.length == 2)) {
                                    if (pp.powers[0] == 2) {
                                        if (pp.coeffs[1].signum() == 1) {
                                                Element div = ring.numberONE.divide(pp.coeffs[1].sqrt(ring), ring);//1/a
                                                Polynom ppp = new Polynom(new int[]{1}, new Element[]{ring.numberONE});//x
                                                Element pppp = ppp.divide(pp.coeffs[1].sqrt(ring), ring);//x/a
                                                if (div.isOne(ring)) {
                                                    return new F(F.ARCTG, pppp);//arctg(x/a)
                                                } else {
                                                    return new F(F.MULTIPLY, new Element[]{div, new F(F.ARCTG, pppp)});//(1/a)*arctg(x/a)
                                                }
                                        } else {//1/(x^2-a^2)
                                            Element a1 = pp.coeffs[1].sqrt(ring);//a
                                            Element a2 = ring.numberONE.valOf(2, ring).multiply(pp.coeffs[1].sqrt(ring), ring);//2*a
                                            Polynom p1 = new Polynom(new int[]{1, 0}, new Element[]{ring.numberONE, a1.negate(ring)});//x-a
                                            Polynom p2 = new Polynom(new int[]{1, 0}, new Element[]{ring.numberONE, a1});//x+a
                                            F f1 = new F(F.DIVIDE, new Element[]{p1, p2});//(x-a)/(x+a)
                                            F f2 = new F(F.LN, f1);//ln{(x-a)/(x+a)}
                                            return new F(F.DIVIDE, new Element[]{f2, a2});// ln{(x-a)/(x+a)} / 2a
                                        }
                                    }
                                }
                            }
                        } else {
                            if (ff.X[1] instanceof F) {// 1/e^{1/2}
                                if (((F) ff.X[1]).name == F.SQRT) {
                                    Element el = ring.numberONE.valOf(2, ring);
                                    return new F(F.MULTIPLY, new Element[]{el, ff.X[1]});//2*e^{1/2}
                                }
                            } else {
                                if (ff.X[1] instanceof F) {//1/(xln(x))
                                    if (((F) ff.X[1]).name == F.MULTIPLY) {
                                        if (((F) ff.X[1]).X.length == 2) {
                                            if ((((F) ff.X[1]).X[0] instanceof Polynom) && (((F) ff.X[1]).X[1] instanceof F)) {
                                                if ((((Polynom) ((F) ff.X[1]).X[0]).coeffs.length == 1) && (((Polynom) ((F) ff.X[1]).X[0]).powers[0] == 1)) {
                                                    if (((F) ((F) ff.X[1]).X[1]).name == F.LN) {
                                                        Element el = ((F) ff.X[1]).X[1];
                                                        return new F(F.LN, el);//ln(ln(x))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (ff.X[1] instanceof F) {//1/cos(x)^{2}
                                        if (((F) ff.X[1]).name == F.POW) {
                                            if ((((F) ff.X[1]).X[1].intValue() == 2) && (((F) ff.X[1]).X[0] instanceof F)) {
                                                if (((F) ((F) ff.X[1]).X[0]).name == F.COS) {
                                                    if (((F) ((F) ff.X[1]).X[0]).X[0] instanceof Polynom) {
                                                        Element el = ((F) ((F) ff.X[1]).X[0]).X[0];
                                                        return new F(F.TG, el);//tg(x)
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (ff.X[1] instanceof F) {//1/sin(x)^{2}
                                            if (((F) ff.X[1]).name == F.POW) {
                                                if ((((F) ff.X[1]).X[1].intValue() == 2) && (((F) ff.X[1]).X[0] instanceof F)) {
                                                    if (((F) ((F) ff.X[1]).X[0]).name == F.SIN) {
                                                        if (((F) ((F) ff.X[1]).X[0]).X[0] instanceof Polynom) {
                                                            Element el = ((F) ((F) ff.X[1]).X[0]).X[0];
                                                            F f1 = new F(F.CTG, el);//ctg(x)
                                                            Element a = ring.numberMINUS_ONE;//-1
                                                            return new F(F.MULTIPLY, new Element[]{a, f1});//-ctg(x)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            default: {
                Polynom p = new Polynom(new int[]{1}, new Element[]{f});//ax
                return p;
            }
        }
    }

    /**
     * Вычисление несобственного интеграла
     * @param ring
     * @return
     */
    public Element value(Ring ring) {
//        Element t = antiderivative(function, ring);
//        Element a = null;
//        Element b = null;
//        if ((lower_limit.compareTo(NumberR.NEGATIVE_INFINITY, ring) != 0) && (lower_limit.compareTo(NumberR.POSITIVE_INFINITY, ring) != 0)) {
//            a = t.value(new Element[]{lower_limit}, ring);//F(a)
//        } else {
//            if(t instanceof F){
//                if((((F)t).name == F.EXP) && (lower_limit.compareTo(NumberR.NEGATIVE_INFINITY, ring) == 0)){//обработка в случае когда первообразная есть Exp
//                    a = NumberR.ZERO;
//                }else  a = new LimitOf(ring).Limit(t, new Element[]{lower_limit});
//            }else
//            a = new LimitOf(ring).Limit(t, new Element[]{lower_limit});
//        }
//        if ((upper_limit.compareTo(NumberR.NEGATIVE_INFINITY, ring) != 0) && (upper_limit.compareTo(NumberR.POSITIVE_INFINITY, ring) != 0)) {
//            b = t.value(new Element[]{upper_limit}, ring);//F(b)
//        } else {
//            b = new LimitOf(ring).Limit(t, new Element[]{upper_limit});
//        }
//        Element c = b.subtract(a, ring);
//        if (c.isNaN() || c.isInfinite()) {
//            integral_converges = false;
//        } else {
//            integral_converges = true;
//        }
        return null; // c;//lim(F(a)+F(b))
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Ring ring = new Ring("R[x]");

        System.out.println("Example 1:");
        Element a = NumberR.ONE;
        Element b = NumberR.POSITIVE_INFINITY;
        Element f = new F("1/x^2", ring);
        improper_integral integral = new improper_integral(a, b, f);
        System.out.println("improper_integral = " + integral.value(ring) + " integral_converges: " + integral.integral_converges);//1 сходится

        System.out.println("Example 2:");
        Element a1 = NumberR.NEGATIVE_INFINITY;
        Element b1 = NumberR.ONE;
        Element f1 = new F("\\cos(x)", ring);
        improper_integral integral1 = new improper_integral(a1, b1, f1);
        System.out.println("improper_integral = " + integral1.value(ring) + " integral_converges: " + integral1.integral_converges);//NAN расходится

        System.out.println("Example 3:");
        Element a2 = NumberR.ONE;
        Element b2 = NumberR.POSITIVE_INFINITY;
        Element f2 = new F("1/x", ring);
        improper_integral integral2 = new improper_integral(a2, b2, f2);
        System.out.println("improper_integral = " + integral2.value(ring) + " integral_converges: " + integral2.integral_converges);//Infty расходится

        System.out.println("Example 4:");
        Element a3 = NumberR.ZERO;
        Element b3 = NumberR.ONE;
        Element f3 = new F("1/x^2", ring);
        improper_integral integral3 = new improper_integral(a3, b3, f3);
        System.out.println("improper_integral = " + integral3.value(ring) + " integral_converges: " + integral3.integral_converges);//Infty расходится

        System.out.println("Example 5:");
        Element a4 = NumberR.ZERO;
        Element b4 = NumberR.POSITIVE_INFINITY;
        Element f4 = new F("1/(x^2+16)", ring);
        improper_integral integral4 = new improper_integral(a4, b4, f4);
        System.out.println("improper_integral = " + integral4.value(ring) + " integral_converges: " + integral4.integral_converges);//Pi/8 сходится

        System.out.println("Example 6:");
        Element a5 = ring.numberONE.valOf(-2, ring);
        Element b5 = ring.numberONE.valOf(0, ring);
        Element f5 = new F("1/x^3", ring);
        improper_integral integral5 = new improper_integral(a5, b5, f5);
        System.out.println("improper_integral = " + integral5.value(ring) + " integral_converges: " + integral5.integral_converges);//Infty расходится

        System.out.println("Example 7:");
        Element a6 = NumberR.NEGATIVE_INFINITY;
        Element b6 = NumberR.ZERO;
        Element f6 = new F("\\exp(x)", ring);
        improper_integral integral6 = new improper_integral(a6, b6, f6);
        System.out.println("improper_integral = " + integral6.value(ring) + " integral_converges: " + integral6.integral_converges);//1 сходится

        System.out.println("Example 8:");
        Element a7 = NumberR.ZERO;
        Element b7 = NumberR.ONE;
        Element f7 = new F("\\ln(x)", ring);
        improper_integral integral7 = new improper_integral(a7, b7, f7);
        System.out.println("improper_integral = " + integral7.value(ring) + " integral_converges: " + integral7.integral_converges);//-1 сходится

        System.out.println("Example 9:");
        Element a8 = NumberR.NEGATIVE_INFINITY;
        Element b8 = NumberR.MINUS_ONE;
        Element f8 = new F("1/x^2", ring);
        improper_integral integral8 = new improper_integral(a8, b8, f8);
        System.out.println("improper_integral = " + integral8.value(ring) + " integral_converges: " + integral8.integral_converges);//1 сходится

        System.out.println("Example 10:");
        Element a9 = NumberR.ZERO;
        Element b9 = NumberR.POSITIVE_INFINITY;
        Element f9 = new F("1/(x^2+1)", ring);
        improper_integral integral9 = new improper_integral(a9, b9, f9);
        System.out.println("improper_integral = " + integral9.value(ring) + " integral_converges: " + integral9.integral_converges);//Pi/2 сходится

        System.out.println("Example 11:");
        Element a10 = NumberR.ZERO;
        Element b10 = NumberR.POSITIVE_INFINITY;
        Element f10 = new F("\\sin(x)", ring);
        improper_integral integral10 = new improper_integral(a10, b10, f10);
        System.out.println("improper_integral = " + integral10.value(ring) + " integral_converges: " + integral10.integral_converges);//NAN расходится

        System.out.println("Example 12:");
        Element a11 = ring.numberONE.valOf(5, ring);
        Element b11 = NumberR.POSITIVE_INFINITY;
        Element f11 = new F("1/x^3", ring);
        improper_integral integral11 = new improper_integral(a11, b11, f11);
        System.out.println("improper_integral = " + integral11.value(ring) + " integral_converges: " + integral11.integral_converges);//0.02 сходится

        System.out.println("Example 13:");
        Element a12 = NumberR.NEGATIVE_INFINITY;
        Element b12 = NumberR.POSITIVE_INFINITY;
        Element f12 = new F("1/(x^2+1)", ring);
        improper_integral integral12 = new improper_integral(a12, b12, f12);
        System.out.println("improper_integral = " + integral12.value(ring) + " integral_converges: " + integral12.integral_converges);//Pi сходится
    }
}
