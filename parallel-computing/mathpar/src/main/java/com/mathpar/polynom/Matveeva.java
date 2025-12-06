package com.mathpar.polynom;

import com.mathpar.func.*;
import com.mathpar.number.*;

/**
 *
 * @author matveeva_yulia
 */
public class Matveeva {

    public static Complex resForFrac(Polynom p1, Polynom p2, Complex a, int n) {
        System.out.println("a " + a);
        Ring ring = new Ring("C64[x]");
        Polynom p1Con = p1.conjugate(ring);
        //---массив коэффициентов для создания полинома x-a
        Complex[] coeffs = {Complex.C64_ONE, a.negate(ring)};
        //---массив коэффициентов для создания полинома
        int[] powers = {1, 0};
        Polynom p = new Polynom(powers, coeffs, ring);
        //---возводим полином p в степень n
        System.out.println("^^^^^^^^^^^^^^ring.ZER0_R64 "+ring.MachineEpsilonR64);
        Polynom pDeg = (Polynom) p.powBinS(n, ring);
        System.out.println("^^^^^^^^^^^^^^^^^^^6ring.ZER0_R64 "+ring.MachineEpsilonR64);
        //---сопряженный к полиному pDeg
        Polynom pDegCon = pDeg.conjugate(ring);
        Polynom pol = p1.multiply(p1Con, ring).multiply(pDeg, ring).multiply(pDegCon, ring);
        System.out.println("pol "+pol);
//        Polynom pol2 = p1Con.multiply(pDegCon, ring).multiply(p1, ring);
//        FactorPol fact = (new FactorPol(new int[]{1, 1, -1}, new Polynom[]{pol2, pDeg, p2}));
        FactorPol fac1 = pol.factorOfPol_inC(ring);
        System.out.println("fac1 "+fac1);
        FactorPol fac2 = p2.factorOfPol_inC(ring);
                System.out.println("fac2 "+fac2);
        FactorPol d = fac1.divide(fac2, ring);
        System.out.println("d "+d);
       // System.out.println("fact "+fact);
        System.out.println("pol  " + pol.toString(ring));
        System.out.println("p2  " + p2.toString(ring));
        Fraction f = new Fraction(pol, p2);
        Element ef = f.cancel(ring);
        //   System.out.println("f.num "+f.num+" f.denom "+f.denom);
       Element p_num,p_den;
        if(ef instanceof Fraction){
              p_num =   ((Fraction)ef).num; p_den =   ((Fraction)ef).denom;}
        else{p_num=ef;p_den=ring.polynomONE;}
        p_num = p_num.divide(p1Con, ring);
        System.out.println("p_num " + p_num);
        FactorPol fp = (new FactorPol(new int[]{1, -1, -1}, new Polynom[]{(Polynom)p_num, (Polynom)p_den, pDegCon}));
        System.out.println("fp " + fp.toString(ring));
        //странно, у меня не работает
        // fp.normalFormWithFraction(ring);
        //       Element ee=fp.value(a, ring);
        Fraction r = new Fraction(p_num, p_den.multiply(pDegCon, ring));
        Element Dr = r;
        for (int i = 0; i < n - 1; i++) {
            Dr = Dr.D(ring);
        }
        System.out.println("Dr= " + Dr.toString(ring) + "  a= " + a);
        Dr = ((Fraction) Dr).value(new Element[]{a}, ring);
        System.out.println("Dr00= " + Dr.toString(ring));
        int k = 1;
        for (int i = 1; i <= n - 1; i++) {
            k *= i;
        }
        return (Complex) Dr.divide(new NumberZ(k), ring);
    }

    public static Complex resForFunc(F f, Polynom p, Complex a, int n) {
        System.out.println("a " + a);
        Ring ring = new Ring("C64[x]");
        //массив коэффициентов для создания полинома x-a
        Complex[] coeffs = {Complex.C64_ONE, a.negate(ring)};
        // массив коэффициентов для создания полинома
        int[] powers = {1, 0};
        // создаем полином x-a
        Polynom pol = new Polynom(powers, coeffs, ring);
        // возводим полином pol1 в степень n
        Polynom pDeg = (Polynom) pol.powBinS(n, ring);
        Fraction frac = new Fraction(pDeg, p);
        Element Efrac = frac.cancel(ring);
        if(Efrac instanceof Fraction)frac=(Fraction)Efrac;else frac=new Fraction(Efrac,ring.polynomONE);
        System.out.println("f "+f.toString(ring));
//        Polynom r_num = ((FactorPol) frac.num).toPolynom(ring);
//        Polynom r_denom = ((FactorPol) frac.denom).toPolynom(ring);
        F f_denom = new F(frac.denom);

        for (int i = 0; i < n - 1; i++) {
            F first = (F)f.D(ring);
            /// System.out.println("first " + first);
           // Polynom pol = r_denom.D(ring);
            F second = (F)f_denom.D(ring);
            // System.out.println("second " + second);
            F d = (F) (first.multiply(f_denom, ring)).subtract(second.multiply(first, ring), ring);
            ///  System.out.println("d " + d);
            F f1 = (F) second.multiply(second, ring);
           // Polynom d2 = (Polynom) r_denom.multiply(r_denom, ring);
          //  F ff = new F(d2);
            f = d;
            f_denom = f1;
        }
        Element num = ((F)f).valueOf(new Element[]{a}, ring);
        Element denom = (f_denom).valueOf(new Element[]{a}, ring);
        int k = 1;
        for (int i = 1; i <= n - 1; i++) {
            k *= i;
        }



        //   System.out.println("f.num "+f.num+" f.denom "+f.denom);
       // FactorPol fac1 = (FactorPol) frac.num;
       // FactorPol fac2 = (FactorPol) frac.denom;
      //  System.out.println("frac.num " + frac.num + " frac.denom " + frac.denom);
        System.out.println("num " + num + " denom " + denom);

        ///ПРОБЛЕМА! УМНОЖИТЬ ФУНКЦИЮ НА ПОЛИНОМ.
////////////        Polynom r_num = ((FactorPol) fac1).toPolynom(ring);
////////////        Polynom r_denom = ((FactorPol) fac2).toPolynom(ring);
////////////        System.out.println("e1num " + r_num);
////////////        System.out.println("e1denom " + r_denom);
////////////        for (int i = 0; i < n - 1; i++) {
////////////            Polynom first = r_num.D(ring);
////////////            /// System.out.println("first " + first);
////////////            Polynom second = r_denom.D(ring);
////////////            // System.out.println("second " + second);
////////////            Polynom d = (Polynom) (first.multiply(r_denom.toPolynom(ring), ring)).subtract(second.multiply(r_num, ring), ring);
////////////            ///  System.out.println("d " + d);
////////////            Polynom d2 = (Polynom) r_denom.multiply(r_denom, ring);
////////////            r_num = d;
////////////            r_denom = d2;
////////////        }
//////////////        //   System.out.println("2 e1.num " + e1num + " 2 e1.denom " + e1denom);
////////////        System.out.println("r " + r.num.toString(ring) + "  " + r.denom.toString(ring));
////////////        Complex res = MyClass.limit(r, a);
////////////        // System.out.println("e1 = "+e1);
////////////        // System.out.println("res = "+res); //верно
////////////        int k = 1;
////////////        for (int i = 1; i <= n - 1; i++) {
////////////            k *= i;
////////////        }
////////////        //Fraction rat = new Fraction(NumberR64.ONE, new NumberR64(k));
////////////        // System.out.println("rat "+rat);
////////////        Complex result = res.divide(new Complex(k), ring);
////////////        // System.out.println("result "+result);
////////////        return result;
////////////        //  return Complex.C64_ONE;



//        Complex result = res.divide(new Complex(k), ring);
//        // System.out.println("result "+result);
//        return result;
        ///.divide(new Complex(k), ring)
        Complex den = (Complex)denom.multiply(new Complex(k), ring);
        return new Complex(num.divide(den, ring),ring);
    }

    public static F IntegralResFrac(Polynom p1, Polynom p2, Complex c, NumberR64 numb) {
        Ring ring = new Ring("C64[x]");
        Polynom pp = new Polynom("1.0x", ring);
        FactorPol a = p2.factorOfPol_inC(ring); //раскладываем знаменатель на множители и находим корни
        Polynom[] pols = new Polynom[a.multin.length]; //записываем массив полиномов из разложения знаменателя
        for (int i = 0; i < pols.length; i++) {
            pols[i] = a.multin[i];
//            System.out.println("pols[i]  " + pols[i]);
        }
        Complex[] mas = new Complex[pols.length]; // записываем массив для вычетов, пока что он пуст
        Element[] mod = new Element[pols.length]; // создаем массив для модулей вычетов
        Complex[] tt = new Complex[pols.length]; //промежуточный массив Complex
        Complex[] comp = new Complex[pols.length];
        //  Element[] sub = new Element[pols.length];
        int k = 0;
        for (int i = 0; i < a.multin.length; i++) {
            // sub[i] = pols[i].subtract(pp, ring);
            /// mas[i] = new Complex(sub[i].negate(ring), ring);//заполняем массив с полюсами для Fraction f
            mas[i] = (Complex) pols[i].coeffs[1].negate(ring);//заполняем массив с полюсами для Fraction f
            System.out.println("mas[i] " + mas[i]);
            tt[i] = (Complex)mas[i].subtract(c, ring);
            mod[i] = ((Complex) tt[i]).abs(ring);// считаем модулю для каждого полюса
            System.out.println(" mod[i] " + mod[i]);
//            if (mod[i].compareTo(numb, -2, ring)) {
//                k++; // если полюс входит в окружность, то считаем их
//            }
            if (mod[i].compareTo(numb, -2, ring)) {
                k++;
                comp[i] = resForFrac(p1, p2, mas[i], a.powers[i]);// считаем вычет для каждого полюса, для которого выполнилось условие
                System.out.println("comp[i] " + comp[i]);

            } else {
                comp[i] = Complex.C64_ZERO;
            }
            // else return new F(NumberR.NAN);
            // System.out.println("k "+k);
        }
        if (k == 0) {
            return new F(NumberR64.NAN);
        }

//        for (int i = 0; i < k; i++) {
//           // System.out.println("f " + f);
//            // System.out.println("mas[i] "+mas[i]);
//            comp[i] = MyClass.resForFrac(p1,p2, mas[i], 1);// считаем вычет для каждого полюса, для которого выполнилось условие
//            System.out.println("comp[i] " + comp[i]);
//        }
        Complex sum = Complex.C64_ZERO;
        for (int i = 0; i < a.multin.length; i++) {
            sum = (Complex)sum.add(comp[i], ring);
        }
        sum = (Complex)sum.multiply(new Complex(0, 1), ring).multiply(new Complex(2, 0), ring);
        System.out.println("sum " + sum);
        F f1 = new F("\\pi", ring);
        F result = (F) f1.multiply(sum, ring);
        System.out.println("разложение   " + a);
        return result;
    }

    public static F IntegralResFrac2(Polynom p1, Polynom p2) {
        Ring ring = new Ring("C64[x]");
        FactorPol a = p2.factorOfPol_inC(ring); //раскладываем знаменатель на множители и находим корни
        for (int i = 0; i < a.multin.length; i++) {
            System.out.println("aa  " + (a.powers[i]));
        }
        Polynom[] pols = new Polynom[a.multin.length]; //записываем массив полиномов из разложения знаменателя
        for (int i = 0; i < pols.length; i++) {
            pols[i] = a.multin[i];
            System.out.println("pols[i]  " + pols[i]);
        }
        Complex[] mas = new Complex[pols.length]; // записываем массив для вычетов, пока что он пуст
        Complex[] tt = new Complex[pols.length]; //промежуточный массив Complex
        Complex[] comp = new Complex[pols.length];
        int k = 0;
        Complex num;
        Complex ress[] = new Complex[a.multin.length];
        for (int i = 0; i < a.multin.length; i++) {
            ress[i] = Complex.C64_ZERO;
        }
        //Complex aa[] = {mas[]};
        Complex one1;
        for (int i = 0; i < a.multin.length; i++) {
            mas[i] = (Complex) pols[i].coeffs[1].negate(ring);//заполняем массив с полюсами для Fraction f
            System.out.println("mas[" + i + "] = " + mas[i]);
            num = Complex.C64_ONE;
            //one1 =(Complex)p1.value(new Complex[]{mas[i]}, ring) ;
            one1 = new Complex(p1.value(new Complex[]{mas[i]}, ring), ring);
            ress[i] = one1;
            System.out.println("ress[i] " + ress[i]);
            num = (Complex)num.subtract(num, ring).add(Complex.C64_ONE, ring);
        }
        for (int i = 0; i < a.multin.length; i++) {
            System.out.println("mas[i] " + mas[i]);
            System.out.println("!(mas[i].im).isNegative() " + !(mas[i].im).isNegative());
            if ((!(mas[i].im).isNegative()) && (ress[i].compareTo(Complex.C64_ZERO, -3, ring))) {
                System.out.println("выполнено");
                k++;
                comp[i] = resForFrac(p1, p2, mas[i], a.powers[i]);// считаем вычет для каждого полюса, для которого выполнилось условие
                System.out.println("comp[i] " + comp[i]);

            } else {
                comp[i] = Complex.C64_ZERO;
            }
            // else return new F(NumberR.NAN);
            // System.out.println("k "+k);
        }
        if (k == 0) {
            return new F(NumberR64.NAN);
        }

//        for (int i = 0; i < k; i++) {
//           // System.out.println("f " + f);
//            // System.out.println("mas[i] "+mas[i]);
//            comp[i] = MyClass.resForFrac(p1,p2, mas[i], 1);// считаем вычет для каждого полюса, для которого выполнилось условие
//            System.out.println("comp[i] " + comp[i]);
//        }
        Complex sum = Complex.C64_ZERO;
        for (int i = 0; i < a.multin.length; i++) {
            sum = (Complex)sum.add(comp[i], ring);
        }
        sum = (Complex)sum.multiply(new Complex(0, 1), ring).multiply(new Complex(2, 0), ring);
        System.out.println("sum " + sum);
        F f1 = new F("\\pi", ring);
        F result = (F) f1.multiply(sum, ring);
        System.out.println("разложение   " + a);
        return result;
    }

    public static F IntegralResTrigon(Fraction f) {
        Ring ring = new Ring("C64[x]");
        F cos = new F("(x^2+1)/(2x)", ring);
        F sin = new F("(x^2-1)/(2x\\i)", ring);
        System.out.println("cos " + cos.toString(ring));
        System.out.println("sin " + sin.toString(ring));
        return new F("result", ring);
    }
    /* Вычисление определенного интеграла вида (функция F)/полином в круге с центром
     * в точке Complex c и радиусом NumberR64 numb.
     */

    public static F IntegralFuncFrac(F f, Polynom p, Complex c, NumberR64 numb) {
        Ring ring = new Ring("C64[x]");
        //раскладываем знаменатель на множители и находим корни
        FactorPol a = p.factorOfPol_inC(ring);
        //записываем массив полиномов из разложения знаменателя
        Polynom[] pols = new Polynom[a.multin.length];
        for (int i = 0; i < pols.length; i++) {
            pols[i] = a.multin[i];
            System.out.println("pols[i]  " + pols[i]);
        }
        // записываем массив для вычетов, пока что он пуст
        Complex[] mas_res = new Complex[pols.length];
        // создаем массив для модулей вычетов
        Element[] mod = new Element[pols.length];
        //промежуточный массив Complex
        Complex[] tt = new Complex[pols.length];
        Complex[] comp = new Complex[pols.length];
        int k = 0;
        for (int i = 0; i < a.multin.length; i++) {
            mas_res[i] = (Complex) pols[i].coeffs[1].negate(ring);//заполняем массив с полюсами для Fraction f
            System.out.println("mas[i] " + mas_res[i]);
            tt[i] = (Complex)mas_res[i].subtract(c, ring);
            mod[i] = ((Complex) tt[i]).abs(ring);// считаем модули для каждого полюса
            System.out.println(" mod[i] " + mod[i]);
//            if (mod[i].compareTo(numb, -2, ring)) {
//                k++; // если полюс входит в окружность, то считаем их
//            }
            if (mod[i].compareTo(numb, -2, ring)) {
                k++;
                comp[i] = resForFunc(f, p, mas_res[i], a.powers[i]);// считаем вычет для каждого полюса, для которого выполнилось условие
                System.out.println("comp[i] " + comp[i]);

            } else {
                comp[i] = Complex.C64_ZERO;
            }
            // else return new F(NumberR.NAN);
            // System.out.println("k "+k);
        }
        return new F(Element.TRUE);
    }

    public static F equation(F f) {
        Ring ring = new Ring("C64[x]");
        Polynom p = (Polynom) f.X[0];
        System.out.println("p " + p);
        F f1 = new F("\\PI*k", ring);
        System.out.println("f1 " + f1);
        if (p.powers.length == 2) {
            F f2 =(F) f1.subtract(p.coeffs[1], ring);
            System.out.println("f2 " + f2);
            return (F) f2.divide(p.coeffs[0], ring);
        } else {
            if (p.powers.length == 1) {
                return (F) f1.divide(p.coeffs[0], ring);
            } else {
                return null;
            }
        }
    }

    public static void main(String args[]) {
        Ring ring = new Ring("C64[x]");
        ring.FLOATPOS=12;
        //Notebook nb = new Notebook(ring);
        //ring.setMachineEpsilonR64(new NumberR64(0.000000001));
        Polynom p1 = new Polynom("1.0x^2+1.0", ring);
//        FactorPol p = p1.factorOfPol_inC(ring);


////////// для IntegralResFrac НЕ РАБОТАЕТ!!!!!!!!!!!!!!!!!!! -PI*i/2 ??? откуда это вообще
//        Polynom pol1 = new Polynom("1.0", ring);
//        Polynom pol2 = new Polynom("(x-1)(x-1)(x^2+1)", ring);

////////// для IntegralResFrac не работает!!!!!!!!!!!!!!!!!!!
//        Polynom pol1 = new Polynom("1.0x+1.0", ring);
//        Polynom pol2 = new Polynom("1.0x^2+3.0", ring);


//////////// для IntegralResFrac ВЕРНО!!!!!!!!!!!!!!!!!!!
//        Polynom pol1 = new Polynom("1.0x+1.0", ring);
//        Polynom pol2 = new Polynom("1.0x^2+1.0", ring);

////// для IntegralResFrac НЕ РАБОТАЕТ!!!!
//        Polynom pol1 = new Polynom("1.0x+1.0", ring);
//        Polynom pol2 = new Polynom("1.0x^2-2.0x-1.0", ring);

///// для  IntegralResFrac2 ВЕРНО 0,08*PI
//        Polynom pol1 = new Polynom("1.0x+1.0", ring);
//        Polynom pol2 = new Polynom("(1.0x^2+1.0)(1.0x^2+9.0)", ring);

        ///// для  IntegralResFrac2 ВЕРНО 5*PI/12
        Polynom pol1 = new Polynom("1.0x^2-1.0x+2.0", ring);
        Polynom pol2 = new Polynom("1.0x^4+10.0x^2+9.0", ring);

         ///// для  IntegralResFrac2 ВЕРНО PI/4
//        Polynom pol1 = new Polynom("1.0x^2", ring);
//        Polynom pol2 = new Polynom("1.0x^4+6.0x^2+25.0", ring);

        ///// для  IntegralResFrac2 НЕ РАБОТАЕТ 3*PI/8
//        Polynom pol1 = new Polynom("1.0", ring);
//        Polynom pol2 = new Polynom("(1.0x^2+1.0)^3", ring);

        ///// для  IntegralResFrac2 НЕ РАБОТАЕТ 4*PI/3
//        Polynom pol1 = new Polynom("1.0x^4+1.0", ring);
//        Polynom pol2 = new Polynom("1.0x^6+1.0", ring);

        ///// для  IntegralResFrac2 НЕ РАБОТАЕТ PI/sqrt(2)
//        Polynom pol1 = new Polynom("1.0x^2+1.0", ring);
//        Polynom pol2 = new Polynom("1.0x^4+1.0", ring);

                ///// для  IntegralResFrac2 НЕ РАБОТАЕТ PI/sqrt(2)
//        Polynom pol1 = new Polynom("(1.0x+1.0)(1.0x-2.0)", ring);
//        Polynom pol2 = new Polynom("1.0x^4-16.0", ring);


        FactorPol f = new FactorPol(pol1);
        Fraction frac = new Fraction(pol1, pol2);
        Complex a = new Complex(0, 1.000);
        Complex a1 = new Complex(((new NumberR64(3)).sqrt(ring)).subtract(new NumberR64(2), ring), NumberR64.ZERO);


        int n = 1;
//         System.out.println("pol1 = "+pol1);
        //NumberZ z = new NumberZ(2);
        // System.out.println("frac "+frac.toString(ring));
        ////// System.out.println("frac = "+frac+" a = "+a+" n = "+n);
        Complex[] coeffs = {Complex.C64_ONE, a.negate(ring)};


        int[] powers = {1, 0};
        Polynom pp = new Polynom(powers, coeffs, ring);
        // System.out.println("pp "+pp);
        // System.out.println("pp.powBinS  "+pp.powBinS(n, ring));
        F f1 = new F("1/(\\cos(x)+2)", ring);
        // System.out.println("f1 "+f1);
        //   System.out.println("res " + MyClass.res(pol1, pol2, a, n));
        F ff = new F(frac);
        Fraction fffff = new Fraction(new NumberZ(2), new NumberZ(4));
        // System.out.println("fffff.cancel "+fffff.cancel(ring));
        F funnc1 = new F("5\\sqrt(3)+3\\sqrt(3)\\i", ring);
        F funnc2 = new F("10+6\\i", ring);
        //  System.out.println("funnc "+funnc1.divide(funnc2, ring));
        // Complex ss = new Complex("10+6\\i", ring);
//        Complex ss1 = new Complex("5\\sqrt(3)+3\\sqrt(3)\\i", ring);
        //System.out.println("   " + ss1.divide(ss, ring));


        Complex cent = Complex.C64_ZERO;
        // Complex cent = new Complex(0, -1);
        /// System.out.println("ololo  " + IntegralResFrac(pol1, pol2, cent, new NumberR64(2)));
      System.out.println("ololo  " + IntegralResFrac2(pol1, pol2));
        //System.out.println("eeeeeeeeeeee "+MyClass.IntegralResTrigon(fraccc));
//        Polynom pp1 = new Polynom("x+1", ring);
//        Polynom pp2 = new Polynom("x+\\i", ring);
//        System.out.println("ff "+pp1.compareTo(pp2, ring));
//        Polynom polynom =  new Polynom("x^3", ring);
//        FactorPol fact = polynom.factorOfPol_inC(ring);
//        System.out.println("fact "+fact.toString(ring));
//        for (int i = 0; i < fact.multin.length; i++) {
//            System.out.println("fact.multin "+fact.multin[i].toString(ring));
//        }
//        Polynom[] pols = new Polynom[fact.multin.length];
//        for (int i = 0; i < pols.length; i++) {
//            pols[i] = fact.multin[i];
//            System.out.println("pols[i]  " + pols[i]);
//        }
//        for (int i = 0; i < pols.length; i++) {
//            System.out.println("coeffs  " + Array.toString(pols[i].coeffs));
//        }
        F fff = new F("\\sin(2x)", ring);
        //System.out.println("rrrrrr " + ((Polynom) fff.X[0]).coeffs[0]);
        //      System.out.println("ee " +  equation(fff));

       F ffff = new F("\\exp(3x)",ring);
       // F ffff = new F("ln^{x+1}",ring);
     //   System.out.println("ffff "+ffff);
      //  Polynom pppp = new Polynom("x^2",ring);
         Polynom pppp = new Polynom("(x^2-1)(x^2-4)",ring);
        System.out.println("pppp "+pppp);
        Complex aa = new Complex(2.0,0.0);
      //  System.out.println("ololo "+resForFunc(ffff, pppp, aa, 2));

    }
}
