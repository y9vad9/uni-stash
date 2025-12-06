package com.mathpar.func;

import com.mathpar.number.*;
import com.mathpar.number.math.MathContext;
import com.mathpar.number.math.RoundingMode;

/**
 * Вычисление трансцендентных функций (NumberR).
 */
public class FuncNumberR {

    MathContext mc=new MathContext(ACCURACYSTATIC, RoundingMode.HALF_EVEN);
    MathContext mc_old;
    int accuracy;
    Ring r;
 
    static final int ACCURACYSTATIC = 34;
    static NumberR OneBy_5 =new NumberR(NumberZ.TWO, 1);
    static NumberR OneBY_239init = (NumberR) NumberR.ONE.divide(new NumberR(new NumberZ(239), 0),
                                        new MathContext(ACCURACYSTATIC, RoundingMode.HALF_EVEN));
    static NumberR oneHalf = new NumberR( NumberZ.POSCONST[5], 1);
    static NumberR oneTens = new NumberR(NumberZ.ONE, 1);
    static NumberR Ten = NumberR.POSCONST[10];
    static NumberR Sixtin = NumberR.POSCONST[16];
    static NumberR Two =  NumberR.POSCONST[2]; 
    static NumberR Three = NumberR.POSCONST[3] ;
    static NumberR oneAndAHalf = new NumberR(NumberZ.POSCONST[15], 1);
    static NumberR number180 = new NumberR(new NumberZ(180), 0);
 //   static NumberR numberPI = new NumberR(new NumberZ(180), 0);
    
 
        
    public   FuncNumberR(int ACCURACYSTATIC) {
        this.accuracy = ACCURACYSTATIC;
        this.mc =  new MathContext(ACCURACYSTATIC, RoundingMode.HALF_EVEN);
    }
   
    public static NumberR[] setConstRInit() {NumberR[] constR=new NumberR[6];
        FuncNumberR fnr=new FuncNumberR(ACCURACYSTATIC);
        MathContext mc=new MathContext(ACCURACYSTATIC, RoundingMode.HALF_EVEN);
        constR[0] =  fnr.exp2();
        constR[1] =  fnr.pi1();
        constR[2] = (NumberR) constR[1].divide(NumberR.R_180, mc); // pi/180
        constR[3] = (NumberR) NumberR.R_180.divide(constR[1], mc); // 180/pi
        constR[4] = (NumberR) constR[1].multiply(NumberR.TWO,mc);   // 2 pi
        constR[5] = (NumberR) constR[1].divide(NumberR.TWO, mc);     // pi/2
        return constR;
    }
    
    public NumberR[] setConstR() {
        NumberR[] constR=new NumberR[6];
        constR[0] =  exp2();
        constR[1] =  pi();
        constR[2] = (NumberR) constR[1].divide(NumberR.R_180, mc); // pi/180
        constR[3] = (NumberR) NumberR.R_180.divide(constR[1], mc); // 180/pi
        constR[4] = (NumberR) constR[1].multiply(NumberR.TWO,mc);   // 2 pi
        constR[5] = (NumberR) constR[1].divide(NumberR.TWO, mc);     // pi/2
        return constR;
    } 
    public FuncNumberR(Ring r, MathContext mc) {
        this.r = r;
        this.accuracy = r.getAccuracy();
        this.mc = mc;
 
    }

    public FuncNumberR(Ring r) {
        this.r = r;
        accuracy = r.ACCURACY;//r.getAccuracy();
        mc = new MathContext(accuracy, RoundingMode.HALF_EVEN);
 
    }

//    /** Get the value of e, pi, pi/180 and 180/pi
//     * with current accuracy
//     *
//     * @param r -Ring
//     */
//    public final void e_pi_other(Ring r) {
//        this.e =exp2();
//        this.pi =pi(); 
//        this.degreeToRadian=(NumberR)pi.divide(number180,r);
//        this.radianToDegree=(NumberR)number180.divide(pi,r);
//    }
    
    /**
     * Add the number of exact decimal places
     *
     * @param numbOfDecFigures --extra number of exact decimal places
     */
    public void addAccuracy(int numbOfDecFigures) {
        accuracy += numbOfDecFigures;
        r.ACCURACY= accuracy;
        mc_old = mc;
        mc = new MathContext(accuracy, RoundingMode.HALF_EVEN);
    }

    /**
     * Substruct the number of exact decimal places
     *
     * @param numbOfDecFigures --extra number of exact decimal places
     */
    public void subAccuracy(int numbOfDecFigures) {
        accuracy -= numbOfDecFigures;
        r.ACCURACY= accuracy ;
        mc = mc_old;
    }

    /**
     * Set the number of exact decimal places in the number of NumberR type.
     * Each operation with NumberR will be perform with this accurecy
     *
     * @param numbOfDecFigures -- number of exact decimal places
     */
    public void setAccuracy(int numbOfDecFigures) {
        accuracy = numbOfDecFigures;
        r.ACCURACY= accuracy ;
        mc = new MathContext(numbOfDecFigures, RoundingMode.HALF_EVEN);
    }

//    /**
//     * Метод вычисляющий pi , 2*pi , pi/2
//     *
//     * @return массив обьектов NumberR с результатами данного метода в
//     * соответствующем порядке
//     */
//    public Element[] pi_2pi_pi_div2() {
//        NumberR pi0 = (r.constR == null) ? pi() : r.constR[1];       //вычисляем pi
//        Element pi2 = pi0.multiply(Two, mc);           //вычисляем 2*pi
//        Element pi_2 = pi0.divide(Two, mc);
//        return new Element[] {pi0, pi2, pi_2};
//    }

    /**
     * Процедура замены переменной x на t
     *
     * @param x (NumberR) аргумент
     *
     * @return t (NumberR)
     */
    public int pogresh(NumberR x) {
        if (x.compareTo(NumberR.ZERO) == 0) {
            return 0;
        }
        x = x.abs();
        Integer t = 0;
        NumberR c = NumberR.ONE;
        if (x.compareTo(c) == 1) {
            while (x.compareTo(c) == 1) {
                c = (NumberR) c.multiply(Ten, r);
                t++;
            }
        } else {
            if (x.compareTo(c) == 0) {
                t = 0;
            } else {
                while (x.compareTo(c) == -1) {
                    c = (NumberR) c.multiply(oneTens);
                    t--;
                }
            }
        }
        return t;//возвращаем полученный результат
    }

    /**
     * Вычисление pi по формуле \Pi=16*arctg(1/5)-4*arctg(1/239)
     * @return pi value
     */
    public NumberR pi() {
        addAccuracy(5);
        NumberR arctg1 = atanForPI(OneBy_5);
        NumberR arctg2 = atanForPI(OneBY_239init);
        NumberR y = (NumberR) ((NumberR) arctg1.multiply( NumberR.POSCONST[16], mc)).subtract((NumberR) arctg2.multiply( NumberR.POSCONST[4], mc), mc);
        subAccuracy(5);
        return y;
    }
    /**
     * pi for initialization
     * @return 
     */
    public NumberR pi1() {
        NumberR arctg1 = atanForPI(OneBy_5);
        NumberR arctg2 = atanForPI(OneBY_239init);
        NumberR y = (NumberR) ((NumberR) arctg1.multiply( NumberR.POSCONST[16], mc)).subtract((NumberR) arctg2.multiply( NumberR.POSCONST[4], mc), mc);
        return y;
    }
    /**
     * Метод вычисления квадратного корня. Вычисление происходит путем
     * построения полинома x^2-d, и нахождении корней методом хорд
     *
     * @param d NumberR - подкоренное число
     *
     * @return NumberR результат вычисления
     */
    public Element sqrt(NumberR d) {
       // System.out.println("in funcnumberr sqrt");
        int precis = mc.getPrecision();
        if (d.compareTo(NumberR.ZERO) == -1) {
            return Element.NAN;
        }//если d меньше нуля, то NAN
//если d равно нулю, то корень равен нулю
        if (d.compareTo(NumberR.ZERO) == 0) {
            return NumberR.ZERO;
        }
//если d равно единице, то корень равен единице
        if (d.compareTo(NumberR.ONE) == 0) {
            return NumberR.ONE;
        }
//объявляем переменные - начало и конец отрезка
        NumberR A, B;
// если подкоренное число > 1, то корень будет меньше d
        if (d.compareTo(NumberR.ONE) == 1) {
            A = NumberR.ONE;
            B = d;
        } // если подкоренное число < 1, то корень будет больше d
        else {
            A = d;
            B = NumberR.ONE;
        }
//подразумевается,что мы  рассматриваем многочлен  x^2-d=0
//вычисляем значение ф-ции в точке A
        //System.out.println("2in funcnumberr sqrt");
        NumberR Alpha = (NumberR) (((NumberR) A.multiply(A)).subtract(d));
//вычисляем значение ф-ции в точке B
        NumberR Betta = (NumberR) ((NumberR) B.multiply(B)).subtract(d);
//объявляем переменную - приближенный корень
        NumberR C = null;
//вычисляем значение ф-ции в точке, которая есть приближенный корень
        NumberR Gamma;
        //System.out.println("3in funcnumberr sqrt");
// выполняется до тех пор, пока погрешность не будет меньше допустимой

        do {
            C = (NumberR) ((NumberR) B.add(A)).multiply(oneHalf, mc);
            Gamma = (NumberR) ((NumberR) C.multiply(C)).subtract(d);
           // System.out.println("C = "+C.toString(r)+" Gamma = "+ Gamma.toString(r));
            if (Gamma.multiply(Alpha).compareTo(NumberR.ZERO) == 1) {
                A = C;
                Alpha = Gamma;
            } else {
                B = C;
                Betta = Gamma;
            }
//формула метода хорд
            C = ((NumberR) ((NumberR) Betta.multiply(A)).subtract((NumberR) Alpha.multiply(B))).divide((NumberR) Betta.subtract(
                    Alpha), precis * 2 + 2, 1);
//вычисляем значение ф-ции в точке приближенного корня
            Gamma = (NumberR) ((NumberR) C.multiply(C)).subtract(d);
//остановка метода
           // System.out.println("4in funcnumberr sqrt");
            if (Gamma.multiply(Alpha).compareTo(NumberR.ZERO) == 1) {
                A = C;
                Alpha = Gamma;
            } else {
                B = C;
                Betta = Gamma;
            }

           // System.out.println("2C = "+C.toString(r)+" Gamma = "+ Gamma.toString(r));
        } while (!Gamma.isZero(r));

        //System.out.println("before return");
        return C.setScale(precis, 1); //отсекание лишнего хвоста
    }

    /**
     * Метод вычисляющий значение x^{1/pow}
     *
     * @param x
     * @param pow
     *
     * @return
     */
    public Element rootOf(NumberR x, Element pow) {
        if (pow.isZero(r)) {
            return NumberR.ZERO;
        }
        if (x.isZero(r)) {
            return x;
        }
        if (x.compareTo(NumberR.ZERO, r) == 1) {
            return exp((NumberR) (NumberR.ONE.divide(pow.toNumber(Ring.R,r), r)).multiply(ln(x), r));
        }
        double new_pow = pow.doubleValue();
        if (new_pow % 2 == 0) {
            return (new_pow < 0) ? x.pow((NumberR) NumberR.valueOf(Math.abs(new_pow)), r) : Element.NAN;
        } else {
            return exp((NumberR) (NumberR.ONE.divide(new NumberR(new_pow), r)).multiply(ln(x.abs()), r)).negate(r);
        }
    }
    /* Вычисление тригонометрических функций sin(x), cos(x), tg(x), ctg(x)
     для  NumberR */

    /**
     * функция вычисления синуса на отрезке [0,2?] с помощью разложения в ряд
     * Тейлора Sin(x) = x-x^3/3!+ x^5/5!- x^7/7!+…+(-1)^n+1*x^2n+1/(2n+1)!
     *
     * @param x NumberR аргумент
     *
     * @return Sin(x) NumberR результат вычисления
     */
    public NumberR sin1(NumberR x) {
        NumberR n = NumberR.ONE;
        NumberR k, k1;
        NumberR y = NumberR.ZERO;
        NumberR f = NumberR.ZERO;
        NumberR sign = NumberR.ONE;
        NumberR step = NumberR.ZERO;
        NumberR fak = NumberR.ONE;
        NumberR e = oneTens;
        NumberR eps = e.powExact(accuracy);
        NumberR od = NumberR.ONE;
        NumberR dv = Two;
        step = (NumberR) step.add(x, mc);                           //задаем  первоначальное значение степени
        y = (NumberR) y.add(x, mc);                                  //задаем первоначальное значение члена
        NumberR m = y.abs(mc);                        //абсолютное значение члена
        while (m.compareTo(eps) == 1) {
            f = (NumberR) f.add(y, mc);//f=f+y;                  //суммируем
            sign = sign.negate(mc);                    //меняем знак
            k = (NumberR) n.add(od, mc);                         // считаем k=n+1
            fak = (NumberR) fak.multiply(k, mc);
            k1 = (NumberR) k.add(od, mc);                         //k1=k+1
            fak = (NumberR) fak.multiply(k1, mc);                 //считаем факториал по формуле fak=fak*(n+1)*(n+2);
            step = (NumberR) ((NumberR) step.multiply(x, mc)).multiply(x, mc);    //накапливаем степень
            y = (NumberR) ((NumberR) step.multiply(sign, mc)).divide(fak, mc);    //считаем член ряда по формуле y=step*sign/fak
            n = (NumberR) n.add(dv, mc);
            m = y.abs(mc);
        }
        return f;
    }

    /**
     * Функция вычисления синуса на всей числовой оси
     *
     * @param x NumberR аргумент
     *
     * @return Sin(x) NumberR результат вычисления
     */
    public Element sin(NumberR x) {
        if (r.RADIAN.value == 0) x = (NumberR) x.multiply(r.constR[2], r);
        NumberR x1 = x.abs();
        int t = pogresh(x1);
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 0) { pog = 3 / 2 * t + 1; } //уравнение прямой
        else { pog = 0; }
        addAccuracy(pog);
        boolean flagS = false;
        NumberR s = NumberR.ONE;
        NumberR z = NumberR.ZERO;
        NumberR a0 = NumberR.ZERO;
        NumberR tr = Three;
        z = (NumberR) z.add(x, mc);
        if (x.compareTo(a0) == -1) {z = x.abs(mc);flagS = true;}
                                //избавляемся от отрицательных значений аргумента
        NumberR pi1 =  r.constR[1] ;     //задаем pi
        NumberR p =    r.constR[4] ;     //задаем pi*2
        NumberR p2 =   r.constR[5] ;      //задаем pi/2
        while ((z.compareTo(p) == 1) | (z.compareTo(p) == 0)) {
            z = (NumberR) z.subtract(p, mc);                                   //избавляемся от периода
        }
        int k1 = (z.divide(pi1, 1, 1)).intValue();  //определяем в какой четверти аргумент
        switch (k1) {
            case 0: {                          //первая четверть
                s = sin1(z);
                if (flagS)  s = s.negate(mc);  //если x<0
                break;
            }
            case 1: {                                //вторая четверть
                z = (NumberR) z.subtract(p2, mc);
                s = cos1(z);
                if (flagS)  s = s.negate(mc);  //если x<0
                break;
            }
            case 2: {                                //третья четверть
                z = (NumberR) z.subtract(pi1, mc);
                s = sin1(z);
                s = s.negate(mc);
                if (flagS)  s = s.negate(mc);  //если x<0
                break;
            }
            case 3: {                                //четвертая четверть
                p = (NumberR) p2.multiply(tr, mc);
                z = (NumberR) z.subtract(p, mc);
                s = cos1(z);
                s = s.negate(mc);
                if (flagS)  s = s.negate(mc);  //если x<0
                break;
            }
        }
        subAccuracy(pog);
        return s;
    }

    /**
     * Функция вычисления косинуса на отрезке [0,2?] с помощью разложения в ряд
     * Тейлора Cos(x) = 1-x2/2!+ x4/4!- x6/6!+…+(-1)n*x2n/(2n)!
     *
     * @param x NumberR аргумент
     *
     * @return Cos(x) NumberR результат вычисления
     */
    public NumberR cos1(NumberR x) {
        NumberR n = NumberR.ONE;
        NumberR zc = NumberR.ONE;//задаём первый член ряда
        NumberR f = NumberR.ZERO;
        NumberR sign = NumberR.MINUS_ONE;
        NumberR step = NumberR.ONE;
        NumberR fak = NumberR.ONE;
        NumberR e = oneTens;
        NumberR eps = e.powExact(accuracy);
        NumberR s;
        NumberR od = NumberR.ONE;
        NumberR dv = Two;
        NumberR m = zc.abs(mc);
        while (m.compareTo(eps) == 1) {
            f = (NumberR) f.add(zc, mc);
            step = (NumberR) step.multiply(x, mc);
            step = (NumberR) step.multiply(x, mc);
            s = (NumberR) n.add(od, mc);                                   //s=n+1
            fak = (NumberR) fak.multiply(s, mc);
            fak = (NumberR) fak.multiply(n, mc);
            zc = (NumberR) step.multiply(sign, mc);
            zc = (NumberR) zc.divide(fak, mc);                             //вычисляем член ряда по формуле
            sign = sign.negate(mc);
            n = (NumberR) n.add(dv, mc);
            m = zc.abs(mc);
        }
        return f;
    }

    /**
     * Функция вычисления косинуса на всей числовой оси
     *
     * @param x NumberR аргумент
     *
     * @return Cos(x) NumberR результат вычислений
     */
    public Element cos(NumberR x) {
        if (r.RADIAN.value == 0) {x = (NumberR) x.multiply(r.constR[2], r); }
        NumberR x1 = x.abs();
        int t = pogresh(x1);
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 0) {  pog = 3 / 2 * t + 1; } //уравнение прямой
        else { pog = 0;}
        addAccuracy(pog);
        NumberR c = NumberR.ONE;
        NumberR z = NumberR.ZERO;
        NumberR a0 = NumberR.ZERO;
        NumberR dv = Two;
        NumberR tr = Three;
        z = (NumberR) z.add(x, mc);
        if (x.compareTo(a0) == -1) {z = x.abs(mc);}
                                //избавляемся от отрицательных значений аргумента
        NumberR pi1 =  r.constR[1] ;     //задаем pi
        NumberR p =    r.constR[4] ;     //задаем pi*2
        NumberR p2 =   r.constR[5] ;      //задаем pi/2
        while ((z.compareTo(p) == 1) | (z.compareTo(p) == 0)) {
            z = (NumberR) z.subtract(p, mc);                                   //избавляемся от периода
        }
        int k1 = (z.divide(pi1, 1, 1)).intValue();  //определяем в какой четверти аргумент
        switch (k1) {
            case 0: {                                                //первая четверть
                c = cos1(z);
                break;
            }
            case 1: {                                                //вторая четверть
                z = (NumberR) z.subtract(p2, mc);
                c = sin1(z);
                c = c.negate(mc);
                break;
            }
            case 2: {                                                //третья четверть
                z = (NumberR) z.subtract(pi1, mc);
                c = cos1(z);
                c = c.negate(mc);
                break;
            }
            case 3: {                                                //четвертая четверть
                p = (NumberR) p2.multiply(tr, mc);
                z = (NumberR) z.subtract(p, mc);
                c = sin1(z);
                break;
            }
        }
        subAccuracy(pog);
        return c;
    }

    /**
     * Функция вычисления тангенса
     *
     * @param x NumberR аргумент
     *
     * @return tan(x) NumberR результат вычислений
     */
    public Element tan(NumberR x) {
        if (x.isNaN() | x.isInfinite()) {return Element.NAN;}
        if (r.RADIAN.value == 0) { x = (NumberR) x.multiply(r.constR[2], r);     
        }
        int t = pogresh(x);
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 2) {
            pog = -25 * t * t + 150 * t - 200;
        } //уравнение параболы
        else {
            pog = 1;
        }
        addAccuracy(pog);
        NumberR t1 = (NumberR) ((NumberR) sin(x)).divide((NumberR) cos(x), mc);
        subAccuracy(pog);
        return t1;
    }

    /**
     * функция вычисления котангенса
     *
     * @param x NumberR аргумент
     *
     * @return ctg(x) NumberR результат вычислений
     */
    public Element ctg(NumberR x) {
        if (x.isNaN()) {
            return Element.NAN;
        }
        int t = pogresh(x);
        if (r.RADIAN.value == 0) {
 
                x = (NumberR) x.multiply(r.constR[2], r);
           
        }
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 2) {
            pog = -25 * t * t + 150 * t - 200;
        } //уравнение параболы
        else {
            pog = -4 / 5 * t + 1;
        }
        addAccuracy(pog);
        NumberR ct = (NumberR) ((NumberR) cos(x)).divide((NumberR) sin(x), mc);
        subAccuracy(pog);
        return ct;
    }

    /**
     * ............................................................................
     * Вычисление обратных тригонометрических функций
     * ............................................................................
     *
     */
    /**
     * Вычисление арктангенса Метод вычисляет значение арктангенса с помощью
     * ряда Тейлора при -1<х<1 : x-x^3/3+x^5/5-x^7/7+.... при x>1 и x<-1 :
     * П/2-(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
     *
     * @param x NumberR аргумент
     *
     * @return ArcTan(x) NumberR результат вычислений
     */
    public NumberR arctn(NumberR x) { NumberR F= arctnForRadian(x);
        return (r.RADIAN.value == 0) ? (NumberR) F.multiply(r.constR[3], r) : F;
    }
    /**
     *  Arctg for the case when we use radian: (RADIAN=1)
     * @param x - фкпгьуте 
     * @return 
     */
    public NumberR arctnForRadian(NumberR x) {
        int t = pogresh(x);
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t > 1) {
            pog = 0;
        } else {
            pog = -t + 2;
        }
        addAccuracy(pog);
        NumberR F = NumberR.ZERO;
        NumberR k = Three;
        NumberR k1 = NumberR.ONE;
//        NumberR eps = new NumberR("1.0E-100");             //точность
        NumberR ch = NumberR.ZERO;                    //член ряда
        NumberR odin = NumberR.ONE;
        NumberR odinv = NumberR.MINUS_ONE;
        NumberR dva = Two;
        NumberR r1 = oneHalf;
        NumberR rm = oneHalf.negate(r);
        NumberR q = oneAndAHalf;
        NumberR t1, t2, w, x2;
        NumberR y = NumberR.ZERO;
        if (x.abs(mc).compareTo(r1) == -1) { //если x < 0.5
            x2 = (NumberR) x.multiply(x, mc);
            ch = (NumberR) ch.add(x, mc);                                        //первый член ряда
            while (!ch.isZero(r)) { //пока ch>eps
                F = (NumberR) F.add(ch, mc);
                NumberR y1 = (NumberR) x2.multiply(odinv, mc);
                NumberR y2 = (NumberR) y1.multiply(k1, mc);
                ch = (NumberR) ch.multiply(y2, mc);
                ch = (NumberR) ch.divide(k, mc);                         //общая формула ряда arctg(x)  x-x^3/3+x^5/5-x^7/7+....
                k1 = (NumberR) k1.add(dva, mc);
                k = (NumberR) k.add(dva, mc);
            }
        } else if (((x.compareTo(r1) == 1) || (x.compareTo(r1) == 0)) & (x.compareTo(odin) == -1)) //  если 0,5 <= x < 1
        {
            y = (NumberR) y.add(x, mc);
            w = (NumberR) y.multiply(y, mc);
            w = (NumberR) odin.subtract(w, mc);
            x = (NumberR) ((NumberR) dva.multiply(y, mc)).divide(w, mc);
            x2 = (NumberR) x.multiply(x, mc);
            ch = (NumberR) odin.divide(x, mc);                        //первый член ряда
            while (!ch.isZero(r)) //пока ch>eps
            {
                F = (NumberR) F.add(ch, mc);
                NumberR y1 = (NumberR) ch.multiply(k1, mc);
                NumberR y2 = (NumberR) ((NumberR) x2.multiply(odinv, mc)).multiply(k, mc);
                ch = (NumberR) y1.divide(y2, mc);                         //общая формула ряда arctg(x)(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
                k1 = (NumberR) k1.add(dva, mc);
                k = (NumberR) k.add(dva, mc);
            }
            NumberR PI = r.constR[5] ;      //задаем pi/2
            F = (NumberR) PI.subtract(F, mc);     // П/2-(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
            F = (NumberR) F.divide(dva, mc);
        } else if (((x.compareTo(odin) == 1) || (x.compareTo(odin) == 0)) & (x.abs(mc).compareTo(q) == -1)) //если 1 < x < 1.5
        {
            y = (NumberR) y.add(x);
            t1 = (NumberR) y.multiply(y, mc);
            t2 = (NumberR) sqrt((NumberR) odin.add(t1));
            w = (NumberR) odin.negate().add(t2, mc);
            x = (NumberR) w.divide(y, mc);
            ch = (NumberR) ch.add(x);                                        //первый член ряда
            x2 = (NumberR) x.multiply(x, mc);
            while (!ch.isZero(r)) { //пока ch >eps
                F = (NumberR) F.add(ch, mc);
                NumberR y1 = (NumberR) ((NumberR) ((NumberR) ch.multiply(x2, mc)).multiply(k1, mc)).multiply(odin.negate());
                ch = (NumberR) y1.divide(k, mc);                         //общая формула ряда arctg(x)  x-x^3/3+x^5/5-x^7/7+....
                k1 = (NumberR) k1.add(dva, mc);
                k = (NumberR) k.add(dva, mc);
            }
            F = (NumberR) F.multiply(dva, mc);
        } else if ((x.compareTo(q) == 1) || (x.compareTo(q) == 0)) //если x >= 1.5
        {
            ch = (NumberR) odin.divide(x, mc);                        //первый член ряда
            x2 = (NumberR) x.multiply(x, mc);
            while (!ch.isZero(r)) //пока ch>eps
            {
                F = (NumberR) F.add(ch, mc);
                NumberR y1 = (NumberR) ch.multiply(k1, mc);
                NumberR y2 = (NumberR) x2.multiply((NumberR) (odin.negate()).multiply(k));
                ch = (NumberR) y1.divide(y2, mc);                         //общая формула ряда arctg(x)(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
                k1 = (NumberR) k1.add(dva, mc);
                k = (NumberR) k.add(dva, mc);
            }
            NumberR PI = r.constR[5] ;      //задаем pi/2
            F = (NumberR) (PI).subtract(F, mc);     // П/2-(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
        } else if (((x.compareTo(rm) == -1) || (x.compareTo(rm) == 0)) & (x.compareTo(odinv) == 1)) //если 0,5 <= x <= 1
        {
            x = x.negate();
            y = (NumberR) y.add(x, mc);
            w = (NumberR) y.multiply(y, mc);
            w = (NumberR) odin.subtract(w, mc);
            x = (NumberR) ((NumberR) dva.multiply(y, mc)).divide(w, mc);
            x2 = (NumberR) x.multiply(x, mc);
            ch = (NumberR) odin.divide(x, mc);                        //первый член ряда
            while (!ch.isZero(r)) //пока ch>eps
            {
                F = (NumberR) F.add(ch, mc);
                NumberR y1 = (NumberR) ch.multiply(k1, mc);
                NumberR y2 = (NumberR) ((NumberR) x2.multiply(odinv, mc)).multiply(k, mc);
                ch = (NumberR) y1.divide(y2, mc);                         //общая формула ряда arctg(x)(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
                k1 = (NumberR) k1.add(dva, mc);
                k = (NumberR) k.add(dva, mc);
            }
            NumberR PI = r.constR[5] ;      //задаем pi/2
            F = (NumberR) PI.subtract(F, mc);     // П/2-(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
            F = (NumberR) F.divide(dva, mc);
            F = F.negate();
        } else //если x < -1.5
        if (((x.compareTo(odinv) == 1) || (x.compareTo(odinv) == 0)) & (x.abs(mc).compareTo(q) == -1)) //пїЅпїЅпїЅпїЅ 1 < x < 1.5
        {
            x = x.negate();
            y = (NumberR) y.add(x);
            t1 = (NumberR) y.multiply(y, mc);
            t2 = (NumberR) sqrt((NumberR) odin.add(t1));
            w = (NumberR) ((NumberR) odin.negate()).add(t2, mc);
            x = (NumberR) w.divide(y, mc);
            ch = (NumberR) ch.add(x);                                        //gthdsq член ряда
            x2 = (NumberR) x.multiply(x, mc);
            while (!ch.isZero(r)) //пока ch>eps
            {
                F = (NumberR) F.add(ch, mc);
                NumberR y1 = (NumberR) ((NumberR) ((NumberR) ch.multiply(x2, mc)).multiply(k1, mc)).multiply(odin.negate());
                ch = (NumberR) y1.divide(k, mc);                         //общая формула ряда arctg(x)  x-x^3/3+x^5/5-x^7/7+....
                k1 = (NumberR) k1.add(dva, mc);
                k = (NumberR) k.add(dva, mc);
            }
            F = (NumberR) F.multiply(dva, mc);
            F = F.negate();
        } else {
            x = x.negate();                                 // если х отрицательный
            ch = (NumberR) odin.divide(x, mc);                      //первый член ряда
            x2 = (NumberR) x.multiply(x, mc);
            while (!ch.isZero(r)) //пока ch>eps
            {
                F = (NumberR) F.add(ch, mc);
                NumberR y1 = (NumberR) ch.multiply(k1, mc);
                NumberR y2 = (NumberR) ((NumberR) x2.multiply(odin.negate())).multiply(k);
                ch = (NumberR) y1.divide(y2, mc);                       //общая формула ряда arctg(x)(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
                k1 = (NumberR) k1.add(dva, mc);
                k = (NumberR) k.add(dva, mc);
            }
            NumberR PI = r.constR[5] ;      //задаем pi/2
            F = (NumberR) (PI).subtract(F, mc);                      // П/2-(1/x-1/3*x^3+1/5*x^5-1/7*x^7+.....)
            F = F.negate(mc);                               //функции приcваиваем противоположное значение
        }
        subAccuracy(pog);
        return F;
    }

    /**
     * Метод вычисляет arctg(x), где 0<=x<=1
     */
    public NumberR atanForPI(NumberR x) {
        NumberR y = NumberR.ONE;                //задаём первый член ряда
        NumberR f = NumberR.ZERO;                //задаём начальное значение суммы ряда
        double znak = 1;                                   //знак
        NumberR zn = NumberR.ONE;               //задаём начальное значение знаменатиля
        NumberR z = x;                                  //задаём начальное значение степени
        NumberR two = Two;                //формируем необходимую точность
        NumberR eps2 = //oneTens;
                new NumberR(NumberZ.ONE, accuracy + accuracy / 5);
//        NumberR eps2 = eps1.powExact(2);                 //разложение в ряд 1-x^3/3+x^5/5-...
        while (eps2.compareTo(y.abs(mc)) == -1) //пока y>eps2
        {
            y = (NumberR) z.divide(zn, mc);                            //вычисляем один член ряда
            if (znak > 0) {                                  //проверка знака члена ряда
                f = (NumberR) f.add(y, mc);
            } //накапливаем сумму
            else {
                f = (NumberR) f.subtract(y, mc);
            }
            znak = znak * (-1);                                //смена знака
            z = (NumberR) ((NumberR) z.multiply(x, mc)).multiply(x, mc);             //формируем степень
            zn = (NumberR) zn.add(two, mc);
        }                            //формируем знаменатель
        return f;  // in RADIAN
        
    }

    /**
     * Метод вычисляет arctg(x), где 0<=x<=1
     */
    public NumberR atan(NumberR x) {
        NumberR y = NumberR.ONE;                //задаём первый член ряда
        NumberR f = NumberR.ZERO;                //задаём начальное значение суммы ряда
        double znak = 1;                                   //знак
        NumberR zn = NumberR.ONE;               //задаём начальное значение знаменатиля
        NumberR z = x;                                  //задаём начальное значение степени
        NumberR two = Two;                //формируем необходимую точность
        NumberR eps1 = oneTens;
        eps1 = eps1.powExact(accuracy);
        NumberR eps2 = eps1.powExact(2);                 //разложение в ряд 1-x^3/3+x^5/5-...
        while (eps2.compareTo(y.abs(mc)) == -1) //пока y>eps2
        {   y = (NumberR) z.divide(zn, mc);     //вычисляем один член ряда
            if (znak > 0) { f = (NumberR) f.add(y, mc);  //проверка знака члена ряда          
            } //накапливаем сумму
            else  f = (NumberR) f.subtract(y, mc);
            znak = znak * (-1);                                //смена знака
            z = (NumberR) ((NumberR) z.multiply(x, mc)).multiply(x, mc);             //формируем степень
            zn = (NumberR) zn.add(two, mc);
        }                            //формируем знаменатель
        return (r.RADIAN.value==0) ? (NumberR) f.multiply(r.constR[3], r) : f;
    }

    /**
     * Вычисление арксинуса Метод вычисляет значение арксинуса с помощью
     * арктангенса arcsin(х)=arctn(x/(sqrt(1-x^2))
     *
     * @param x NumberR аргумент
     *
     * @return ArcSin(x) NumberR результат вычислений
     */
    public Element arcsn(NumberR x) {
        NumberR x1;
        NumberR arcsin = NumberR.ZERO;
        NumberR odin = NumberR.ONE;
        NumberR odinv = NumberR.MINUS_ONE;
        NumberR dva = Two;
        int t = pogresh(x);
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t > 1) {pog = 0;} else {pog = t - 1; }
        addAccuracy(pog);
        if ((x.compareTo(NumberR.ONE) == 1) | (x.compareTo(NumberR.MINUS_ONE) == -1)) {
            return Element.NAN;
        }
        if (x.compareTo(odin) == 0) {                         //если х=1
            NumberR PI = r.constR[5] ;      //задаем pi/2
            arcsin = (NumberR) arcsin.add(PI);
        } else if (x.compareTo(odinv) == 0) {                      //если х=-1
            NumberR PI = r.constR[5] ;      //задаем pi/2
            arcsin = (NumberR) arcsin.add(PI);
            arcsin = arcsin.negate();
        } else if ((x.abs(mc).compareTo(odin) == -1) || (x.abs(mc).compareTo(odin) == 0)) //если -1<x<1
        {
            x1 = (NumberR) x.divide((NumberR) sqrt((NumberR) (((NumberR) x.multiply(x, mc)).negate().add(odin, mc))), mc);       //аргумент арктангенса
            arcsin = arctnForRadian(x1);
        } else {
            r.exception.append("\n Massage from FuncNumberR.arsin! ");
        }
        subAccuracy(pog);
        return (r.RADIAN.value==0) ? (NumberR) arcsin.multiply(r.constR[3], r) : arcsin;
    }

    /**
     * Вычисление арккотангенса Метод вычисляет значение арккотангенса с помощью
     * арктангенса arcctn(х)=П/2-arctn(x)
     *
     * @param x NumberR аргумент
     *
     * @return ArcCtn(x) NumberR результат вычислений
     */
    public NumberR arcctn(NumberR x) {
        NumberR dva = Two;
        int t = pogresh(x);
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t > 1) {pog = 0;} else { pog = t + 1; }
        addAccuracy(pog);
        NumberR PI = r.constR[5] ;      //задаем pi/2
        
        NumberR F = (NumberR) PI.subtract(arctnForRadian(x), mc);
        subAccuracy(pog);
        return (r.RADIAN.value == 0) ? (NumberR) F.multiply(r.constR[3], r) : F;
    }

    /**
     * Вычисление арккосинуса Метод вычисляет значение арккосинуса с помощью
     * арккотангенса arccos(х)=PI-arctn(x/(sqrt(1-x^2))
     *
     * @param x NumberR аргумент
     *
     * @return ArcCos(x) NumberR результат вычислений
     */
    public Element arccs(NumberR x) {
        NumberR k =   r.constR[1]  ;  // pi
        NumberR x1;                 //аргумент арккотангенса
        Element arccos;
        NumberR odin = NumberR.ONE;
        int t = pogresh(x);
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t > 1) {pog = 0;} else { pog = -2 / 3 * t; }
        addAccuracy(pog);
        if (x.abs(mc).compareTo(odin) > 0 ) arccos=Element.NAN;      else
        if (x.compareTo(NumberR.ONE) == 0)  arccos= NumberR.ZERO;    else
        if (x.compareTo(NumberR.MINUS_ONE) == 0) 
            arccos= (r.RADIAN.value == 0) ? number180 : r.constR[1];  else
         // если -1<x<1
        { x1 = (NumberR) x.divide((NumberR) sqrt((NumberR) (((NumberR) x.multiply(x, mc)).negate().add(odin, mc))), mc);
            arccos = (NumberR)(r.constR[5]).subtract(arctnForRadian(x1),r);
            if(r.RADIAN.value == 0) arccos=arccos.multiply(r.constR[3], r);}
        subAccuracy(pog);
        return  arccos;
    }

    /**
     * ................................................................................
     * Вычисление значения гиперболических функций sh(x), ch(x), th(x), cth(x)
     * ................................................................................
     *
     */
    /**
     * Функция для вычисления e^x с помощью разложения в ряд: 1 + x/1! + x^2/2!
     * + x^3/3! + ...
     *
     * @param x NumberR - аргумент искомой функции
     *
     * @return f NumberR - результат вычисления
     */
    public NumberR exp1(NumberR x) {
        int n = 1;
        NumberR f = NumberR.ONE;//задаем начальное значение суммы ряда
        NumberR y = NumberR.ONE;//задаем начальное значение члена ряда
        //формируем необходимую точность
        NumberR eps1 = new NumberR(NumberZ.ONE, accuracy);

        //пока заданная погрешность меньше члена ряда разложения накапливаем сумму
        while (y.compareTo(eps1) == 1) {
            //образующая формула члена ряда: y(i+1) = y(i-1) * x/n
            NumberR y1 = (NumberR) y.multiply(x, mc);
            NumberR y2 = NumberR.valueOf(n);
            y = (NumberR) y1.divide(y2, mc);
            f = (NumberR) f.add(y, mc);//накапление суммы
            n++;
        }
        return f;
    }
    /**
     * Функция для вычисления e  с помощью разложения в ряд: 1 + 1/1! + 1/2!
     * + 1/3! + ...
    
     * @return  NumberR e =2.71....
     */
    public NumberR exp2() {
        int n = 1;
        NumberR f = NumberR.ONE;//задаем начальное значение суммы ряда
        NumberR y = NumberR.ONE;//задаем начальное значение члена ряда
        //формируем необходимую точность
        NumberR eps1 = new NumberR(NumberZ.ONE, accuracy);
        //пока заданная погрешность меньше члена ряда разложения накапливаем сумму
        while (y.compareTo(eps1) == 1) {
            //образующая формула члена ряда: y(i+1) = y(i-1) * x/n
            y = (NumberR) y.divide(NumberR.valueOf(n), mc);
            f = (NumberR) f.add(y, mc);//накапление суммы
            n++;
        }
        return f;
    }
    /**
     * Функция для вычисления e^x. Выбор алгоритма вычисления на определенном
     * интервале, которому принадлежит аргумент х.
     *
     * @param x NumberR - аргумент искомой функции
     *
     * @return f NumberR - результат вычисления
     */
    public NumberR exp(NumberR x) {
        NumberR dv = Two;
        NumberR g, f; //задаем начальное значение промежуточной суммы ряда
        if (NumberR.ZERO.compareTo(x) != 1) {
            if (NumberR.ONE.compareTo(x) != -1) {
//вычисления e^x на отрезке [0;1]
                f = exp1(x);
            } else {
//вычисления e^x при x > 1 - в этом случае сводим задачу к уже решенной на отрезке [0;1]
                //переводим x NumberR в b BigInteger
                NumberZ b = (NumberZ) x.toNumber(Ring.Z, r);
                //находим логарифм от х по основанию 2 через битовую длину b
                int m = b.bitLength();
                //преобразуем х в t, принадлежащее отрезку [0;1]
                NumberR t1 = dv.powExact(m);
                NumberR t = (NumberR) x.divide(t1, mc);
                g = exp1(t);//вызов функции exp1(x, eps)
                //получаем окончательное значение суммы
                for (int i = 0; i < m; i++) {
                    g = (NumberR) g.multiply(g, mc);
                }
                f = g;
            }
        } else {
//вычисления e^x при x < 0; в этом случае e^(-x) = 1/(e^x)
            g = exp1(x.abs());//вызов функции exp1(x, eps)
            f = (NumberR) NumberR.ONE.divide(g, mc);
        }
        return f;//возвращаем полученный результат
    }

    /**
     * Функция для вычисления функции sh(x) - гиперболического синуса.
     * Используется представление данной функции через e^x: (e^x - e^(-x))/2
     *
     * @param x NumberR - аргумент искомой функции
     *
     * @return y NumberR - результат вычисления
     */
    public NumberR sh(NumberR x) {
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 1) {
            pog = 2 * t + 2;
        } else {
            pog = 2;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR px = exp(x);//вызов функции exp(x,eps)
        NumberR ox = exp(x.negate());//вызов функции exp(x,eps)
        //вычисление сth(x) по формуле (e^x - e^(-x))/2
        NumberR y1 = (NumberR) px.subtract(ox, mc);
        NumberR y2 = Two;
        NumberR y = (NumberR) y1.divide(y2, mc);
        subAccuracy(pog);
        return y;//возвращаем полученный результат
    }

    /**
     * Функция для вычисления функции ch(x) - гиперболического косинуса.
     * Используется представление данной функции через e^x: (e^x + e^(-x))/2
     *
     * @param x NumberR - аргумент искомой функции
     * @param eps Integer - заданная погрешность
     *
     * @return y NumberR - результат вычисления
     */
    public NumberR ch(NumberR x) {
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= -1) {
            pog = t + 4;
        } else {
            pog = 0;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR px = exp(x);//вызов функции exp(x,eps)
        NumberR ox = exp(x.negate());//вызов функции exp(x,eps)
        //вычисление сth(x) по формуле (e^x + e^(-x))/2
        NumberR y1 = (NumberR) px.add(ox, mc);
        NumberR y2 = Two;
        NumberR y = (NumberR) y1.divide(y2, mc);
        subAccuracy(pog);
        return y;//возвращаем полученный результат
    }

    /**
     * Функция для вычисления функции th(x) - гиперболического тангенса.
     * Используется представление данной функции через e^x: (e^x - e^(-x))/(e^x
     * + e^(-x))
     *
     * @param x NumberR - аргумент искомой функции
     * @param eps Integer - заданная погрешность
     *
     * @return y NumberR - результат вычисления
     */
    public NumberR th(NumberR x) {
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t > 3) {
            pog = 0;
        } else {
            pog = 2;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR px = exp(x);//вызов функции exp(x,eps)
        NumberR ox = exp(x.negate());//вызов функции exp(x,eps)
        //вычисление сth(x) по формуле (e^x - e^(-x))/(e^x + e^(-x))
        NumberR y1 = (NumberR) px.add(ox, mc);
        NumberR y2 = (NumberR) px.subtract(ox, mc);
        NumberR y = (NumberR) y2.divide(y1, mc);
        subAccuracy(pog);
        return y;//возвращаем полученный результат
    }

    /**
     * Функция для вычисления функции cth(x) - гиперболического котангенса.
     * Используется представление данной функции через e^x: (e^x + e^(-x))/(e^x
     * - e^(-x))
     *
     * @param x NumberR - аргумент искомой функции
     * @param eps Integer - заданная погрешность
     *
     * @return y NumberR - результат вычисления
     */
    public Element cth(NumberR x) {
        NumberR a0 = NumberR.ZERO;
        if (x.compareTo(a0) == 0) {
            return Element.NAN;
        }
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t > 1) {
            pog = 0;
        } else {
            pog = -t + 2;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR px = exp(x);//вызов функции exp(x,eps)
        NumberR ox = exp(x.negate());//вызов функции exp(x,eps)
        //вычисление сth(x) по формуле (e^x + e^(-x))/(e^x - e^(-x))
        NumberR y1 = (NumberR) px.add(ox, mc);
        NumberR y2 = (NumberR) px.subtract(ox, mc);
        NumberR y = (NumberR) y1.divide(y2, mc);
        subAccuracy(pog);
        return y;//возвращаем полученный результат
    }

    /* Вычисление обратно гиперболических функций arsh1(x), arch1(x), arth1(x), arcth1(x) */

    /*  Процедура вычисления натурального логарифма на отрезке (0, 1.5)
     * Вычисление происходит путем разложения логарифма в точке х=1
     * в степенной ряд  (х-1) - (х-1)^2/2 + (x-1)^3/3 - ...
     * и почленного суммирования полученного ряда
     * @param x NumberR - аргумент функции
     * @param Notebook.SCALE int - точность вычисления
     * @return F NumberR результат вычисления
     */
    public NumberR ln2(NumberR x) {
        NumberR F = NumberR.ZERO;
        NumberR s = (NumberR) x.add(NumberR.MINUS_ONE, mc);
        NumberR ch = (NumberR) NumberR.ZERO.add(s, mc);                    //задаем начальное значение члена ряда
        s = (NumberR) s.negate(r);
        NumberR ch1 = ch;
        int kk = 2;
        NumberR k = new NumberR(kk);
        while (!ch.isZero(r)) {      //Пока член ряда больше заданной точности, накапливаем сумму
            F = (NumberR) F.add(ch1, mc);                 //накапливаем сумму
            ch = (NumberR) ch.multiply(s, mc);
            ch1 = (NumberR) ch.divide(k, mc);
            kk++;
            k = new NumberR((new NumberZ(1, new int[] {kk})));
        }
        return F;                                   //возвращаем полученный результат
    }

    /*  Процедура вычисления натурального логарифма на интервале (0, 3)
     * с помощью разложения в точке х=2 в ряд
     * Ln(2) + (х-2)/2 - ((х-2)^2)/(2*2^2) + ((х-2)^3)/(3*2^3) - ...
     * на интервале [1.5, 3]  и почленного суммирования полученного ряда
     * или с помощью процедуры ln(x) на интервале (0, 1.5)
     *
     * @param x NumberR - аргумент функции
     * @param Notebook.SCALE int - точность вычисления
     * @return F NumberR результат вычисления
     */
    public NumberR ln1(NumberR x) {
        NumberR a0 = NumberR.ZERO;    //инициализация новых переменных
        NumberR a1 = new NumberR(new NumberZ(16), 1);
        NumberR a2 = Three;
        NumberR ch;
        NumberR s = NumberR.ZERO;
        NumberR s1 = NumberR.ZERO;
        NumberR koff = Two;
        NumberR F = NumberR.ZERO;
        NumberR odi = NumberR.MINUS_ONE;
        NumberR od = NumberR.ONE;
        NumberR dv = Two;
        NumberR zn = NumberR.ONE;
        NumberR L = NumberR.ZERO;
        NumberR eps = new NumberR(NumberZ.ONE, accuracy);
        //при х из промежутка (0, 1.5) вычисляем Ln(x) с помощью процедуры ln2(x)
        if ((x.compareTo(a0) == 1) & ((x.compareTo(a1) == -1) | (x.compareTo(a1) == 0))) {
            F = (NumberR) F.add(ln2(x), mc);
        }
        //на интервале (1.5, 3) раскладываем Ln(x) в точке х=2 в ряд
        // Ln(2) + (х-2)/2 - ((х-2)^2)/(2*2^2) + ((х-2)^3)/(3*2^3) - ...
        if ((x.compareTo(a1) == 1) & ((x.compareTo(a2) == -1) | (x.compareTo(a2) == 0))) {
            NumberR k = Two;
            ch = (NumberR) x.subtract(dv, mc);       //задаем начальное значение члена ряда
            s = (NumberR) s.add(ch, mc);
            s1 = (NumberR) s1.add(s, mc);
            ch = (NumberR) ch.divide(dv, mc);
            int n = 1;
            int n1 = 2;              //задаем значение первого члена ряда
            while (ch.abs(mc).compareTo(eps) == 1) { //пока член ряда больше заданной точности, накапливаем сумму
                F = (zn.compareTo(a0) == 1) ? ((NumberR) F.add(ch, mc)) : ((NumberR) F.subtract(ch, mc));             //накапливаем сумму
                s = (NumberR) s.multiply(s1, mc);
                zn = (NumberR) zn.multiply(odi, mc);
                ch = (NumberR) s.divide(k, mc);   //вычисляем очередной член ряда
                koff = (NumberR) koff.multiply(dv, mc);
                ch = (NumberR) ch.divide(koff, mc);
                k = (NumberR) k.add(od, mc);
                n++;
                n1++;
            }
            L = (NumberR) L.add((NumberR) sqrt(dv), mc);  //вычисляем Ln(sqrt(2))
            a0 = (NumberR) a0.add(ln2(L), mc);
            a0 = (NumberR) a0.multiply(dv, mc);
            F = (NumberR) F.add(a0, mc);            //получаем окончательный результат
        }
        return F;                     //возвращаем полученный результат
    }

    /**
     * Процедура вычисления натурального логарифма на всей числовой оси
     *
     * @param x NumberR - аргумент функции
     *
     * @return F NumberR результат вычисления
     */
    public Element ln(NumberR x) {
        Element a0 = NumberR.ZERO;    //задаем начальное значение пременных
        if (x.compareTo(a0) == -1) {
            return Element.NAN;
        }
        if (x.compareTo(a0) == 0) {
            return Element.NEGATIVE_INFINITY;
        }
        NumberR a2 = Three;
        NumberR F = NumberR.ZERO;
        NumberR od = NumberR.ONE;
        NumberR y, y1, y2;
        if ((x.compareTo(a0) == 1) & ((x.compareTo(a2) == -1) | (x.compareTo(a2) == 0)))//при х из промежутка (0, 3] вычисляем Ln(x) с помощью процедуры ln(x)
        {
            F = (NumberR) F.add(ln1(x), mc);     //получаем окончательный результат
        }
        if (x.compareTo(a2) == 1) {       //при х>3 делаем замену  y=(x-1)/(x+1)
//            и вычисляем Ln(x) по формуле Ln(x)=ln1(y+1)-ln1(1-y)
            y1 = (NumberR) x.subtract(od, mc);
            y2 = (NumberR) x.add(od, mc);
            y = (NumberR) y1.divide(y2, mc);
            y1 = (NumberR) y.add(od, mc);
            y2 = (NumberR) od.subtract(y, mc);
            F = (NumberR) ln1(y1).subtract(ln1(y2), mc);
        }       //получаем окончательный результат
        return F;                                              //возвращаем полученный результат
    }

    /**
     * Процедура вычисления Arsh(x) с помощью процедуры вычисления Ln(x) по
     * формуле Arsh(x)=Ln(x+sqrt(x^2+1))
     *
     * @param x NumberR - аргумент функции
     * @param Notebook.SCALE int - точность вычисления
     */
    public NumberR arsh(NumberR x) {
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= -1) {
            pog = t + 4;
        } else {
            pog = 2;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR od = NumberR.ONE;        //задаем начальное значение пременных
        NumberR F = NumberR.ZERO;
        NumberR y;
        NumberR y1 = NumberR.ONE;
        NumberR y2 = NumberR.ZERO;
        NumberR a1 = NumberR.ZERO;
        y1 = (NumberR) y1.multiply(x, mc);          //вычисляем аргумент Ln(x) по формуле y=x+sqrt(x^2+1)
        y1 = (NumberR) y1.multiply(x, mc);
        y1 = (NumberR) y1.add(od, mc);
        y2 = (NumberR) y2.add((NumberR) sqrt(y1), mc);
        y = (NumberR) x.add(y2, mc);
        F = (NumberR) F.add((NumberR) ln(y));  //вычисляем значение Arsh(x) с помощью процедуры ln(x)
        if (x.compareTo(a1) == -1) {  //при отрицательном х меняем знак результата
            F = F.negate(mc);
        }
        subAccuracy(pog);
        return F;
    }

    /**
     * Процедура вычисления Arch(x) с помощью процедуры вычисления Ln(x) по
     * формуле Arch(x)=Ln(x+sqrt(x^2-1))
     *
     * @param x NumberR - аргумент функции
     */
    public Element arch(NumberR x) {
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 1) {
            pog = t + 4;
        } else {
            pog = 0;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR od = NumberR.ONE;       //задаем начальное значение пременных
        NumberR F = NumberR.ZERO;
        NumberR y;
        NumberR y1 = NumberR.ONE;
        NumberR y2 = NumberR.ZERO;
        NumberR a1 = NumberR.ONE;
        if (x.compareTo(a1) == -1) {
            return Element.NAN;
        } //при х<1 выводим сообщение о недопустимом значении аргумента Arch(x)
        else {                        // при х>=1 вычисляем значение Arsh(x) с помощью процедуры ln(x)
            y1 = (NumberR) y1.multiply(x, mc);     //вычисляем аргумент Ln(x) по формуле y=x+sqrt(x^2-1)
            y1 = (NumberR) y1.multiply(x, mc);
            y1 = (NumberR) y1.subtract(od, mc);
            y2 = (NumberR) y2.add((NumberR) sqrt(y1), mc);
            y = (NumberR) y2.add(x, mc);
            F = (NumberR) F.add((NumberR) ln(y), mc);
        }
        subAccuracy(pog);
        return F;
    }

    /**
     * Процедура вычисления Arth(x) с помощью процедуры вычисления Ln(x) по
     * формуле Arth(x)=Ln((1+x)/(1-x))
     *
     * @param x NumberR - аргумент функции
     */
    public Element arth(NumberR x) {
        if (x.isNaN() | x.isInfinite()) {
            return Element.NAN;
        }
        if ((x.compareTo(NumberR.MINUS_ONE) == -1) | x.isMinusOne(r)
                | (x.compareTo(NumberR.ONE) == 1) | x.isOne(r)) {
            return Element.NAN;
        }
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 1) {
            pog = 0;
        } else {
            pog = 3;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR od = NumberR.ONE;   //задаем начальное значение пременных
        NumberR F = NumberR.ZERO;
        NumberR dv = Two;
        NumberR y = (NumberR) od.add(x, mc);           //вычисляем аргумент Ln(x) по формуле y=(1+x)/(1-x)
        NumberR y1 = (NumberR) od.subtract(x, mc);
        y = (NumberR) y.divide(y1, mc);
        F = (NumberR) F.add((NumberR) ((NumberR) ln(y)).divide(dv, mc), mc);
        subAccuracy(pog);
        return F;
    }

    /**
     * Процедура вычисления Arcth(x) с помощью процедуры вычисления Ln(x) по
     * формуле Arcth(x)=Ln((1+x)/(x-1))
     *
     * @param x NumberR - аргумент функции
     */
    public Element arcth(NumberR x) {
        if (x.isNaN() | x.isInfinite()) {
            return Element.NAN;
        }
        if (((x.compareTo(NumberR.MINUS_ONE) == 1) | x.isMinusOne(r))
                & ((x.compareTo(NumberR.ONE) == -1) | x.isOne(r))) {
            return Element.NAN;
        }
        int t = pogresh(x);//вызов функции pogresh(x)
        int pog;
        //вычисление погрешности  с учетом количества символов несовпадения,
        //на которую необходимо увеличить исходную точность, чтобы количество
        //верных цифр в числе совпадало с исходной точностью.
        if (t >= 1) {
            pog = t + 4;
        } else {
            pog = 0;
        }
        //увеличиваем точность на величину погрешности
        addAccuracy(pog);
        NumberR od = NumberR.ONE;
        NumberR dv = Two;
        NumberR F = NumberR.ZERO;
        NumberR y = (NumberR) x.add(od, mc);               //вычисляем аргумент Ln(x) по формуле y=(1+x)/(x-1)
        NumberR y1 = (NumberR) x.subtract(od, mc);
        y = (NumberR) y.divide(y1, mc);
        F = (NumberR) F.add((NumberR) ((NumberR) ln(y)).divide(dv, mc), mc);
        subAccuracy(pog);
        return F;
    }

    public static void main(String[] args) {
        Ring ring = new Ring("R[]");
        ring.setMachineEpsilonR(100);
        ring.setAccuracy(105);
        ring.setFLOATPOS(50);
        NumberR t = new NumberR("0.001");
        long tt0 = System.currentTimeMillis();
        Element w = new FuncNumberR(ring).ln(t);
        long tt1 = System.currentTimeMillis();
        System.out.println((tt1 - tt0) + "  w=" + w.toString(ring));
    }
}
