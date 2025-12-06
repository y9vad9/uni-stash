package com.mathpar.number;

import com.mathpar.func.*;
import java.util.Random;
import com.mathpar.polynom.*;

/**
 *
 * @author gennadi
 */
public class Fraction extends Element {
    private static final long serialVersionUID = 4755723849851872110L;
    //Числитель дроби
    public Element num;
    //Знаменатель дроби
    public Element denom;
    public static Fraction Z_ONE = new Fraction(NumberZ.ONE, NumberZ.ONE);
    public static Fraction Z_ZERO = new Fraction(NumberZ.ZERO, NumberZ.ONE);

    public Fraction() {
    }

    /**
     * Конструктор, создает дробь из числителя и знаменателя типа Element
     *
     * @param numerator числитель
     * @param denominator знаменатель
     */
    public Fraction(Element numerator, Element denominator) {
        num = numerator;
        denom = denominator;
    }

    public Fraction(F f, Ring ring) {
        F ff = f;
        num = (Polynom) ff.X[0];
        denom = (Polynom) ff.X[1];
    }

    public Fraction(F n, F d) {
        num = n;
        denom = d;
    }

    public Fraction(F f, Boolean b, Ring ring) {
        if ((f.X[0] instanceof NumberZ) && (f.X[1] instanceof NumberZ)) {
            num = Polynom.polynomFromNumber(f.X[0], ring);
            denom = Polynom.polynomFromNumber(f.X[1], ring);
        } else {
            num = (Polynom) ((F) f.X[0]).expand(ring).X[0];
            denom = (Polynom) ((F) f.X[1]).expand(ring).X[0];
        }
    }

//    /**
//     * Конструктор из функции дроби над типом элементов, который передан своей
//     * единицей
//     *
//     * @param f -- функция состоящая из дроби
//     * @param one -- тип элементов области над которой строится дробь
//     */
//    public Fraction(F f, Element one) {
//        int type = one.numbElementType();
//        if ((f.X[0].numbElementType() == type) && (f.X[1].numbElementType() == type)) {
//            num = (f.X[0]);
//            denom = (f.X[1]);
//        } else {
//            num = null;
//            denom = null;
//        }
//    }
    /**
     * Конструктор, создает дробь из числителя и знаменателя типа Element
     */
    public Fraction(Element numerator, Element denominator, boolean checkArgument) {
        int numT = numerator.numbElementType();
        if (!checkArgument
                || (numT == denominator.numbElementType()
                && (numT == Ring.Z || numT == Ring.Polynom))) {
            num = numerator;
            denom = denominator;
        }
    }

    public Fraction(String numerator, String denominator, Ring ring) {
        num = new Polynom(numerator, ring);
        denom = new Polynom(denominator, ring);
    }

    public Fraction(long numerator, long denominator) {
        num = NumberZ.valueOf(numerator);
        denom = NumberZ.valueOf(denominator);
    }

    /**
     * Конструктор, создает дробь из числителя и знаменателя типа long.
     */
    public Fraction(int numerator, int denominator) {
        num = NumberZ.valueOf(numerator);
        denom = NumberZ.valueOf(denominator);
    }

    /**
     * Конструктор, создает дробь из числителя и знаменателя типа long.
     */
    public Fraction(int numerator) {
        num = NumberZ.valueOf(numerator);
        denom = NumberZ.ONE;
    }

    public Fraction(long numerator) {
        num = NumberZ.valueOf(numerator);
        denom = NumberZ.ONE;
    }

    /**
     * Конструктор, создает дробь из числителя типа Element.
     */
    public Fraction(Element numerator, Ring ring) {
        if (numerator.signum() == 0) {
            num = NumberZ.ZERO;
            denom = NumberZ.ONE;
        } else {
            num = numerator;
            if (numerator instanceof Fname) {
                denom = ring.numberONE;
            } else {
                denom = numerator.one(ring);
            }
        }
    }

    @Override
    public Fraction myZero() {
        return new Fraction(denom.myZero(), Ring.oneOfType(denom.numbElementType()));
    }

    @Override
    public Fraction myZero(Ring ring) {
        return new Fraction(denom.myZero(ring), denom.myOne(ring));
    }

    @Override
    public Fraction myMinus_one(Ring ring) {
        return new Fraction(denom.myMinus_one(), denom.myOne(ring));
    }

    @Override
    public Fraction myMinus_one() {
        return new Fraction(denom.myMinus_one(), Ring.oneOfType(denom.numbElementType()));
    }

    @Override
    public Fraction myOne(Ring ring) {
        return new Fraction(denom.myOne(ring), denom.myOne(ring));
    }

    @Override
    public Element one(Ring ring) {
        return denom.one(ring);
    }

    @Override
    public Element zero(Ring ring) {
        return denom.zero(ring);
    }

    @Override
    public Element minus_one(Ring ring) {
        return denom.minus_one(ring);
    }

    public static Fraction Rational(Element num, Element denom) {
        if ((num instanceof Polynom) && (denom instanceof Polynom)) {
            return new Fraction(num, denom);
        }
        if (num instanceof F) {
            if (((F) num).name != F.ID) {
                return null;
            } else {
                if (denom instanceof F) {
                    if (((F) denom).name != F.ID) {
                        return null;
                    }
                    return new Fraction(((F) num).X[0], ((F) denom).X[0]);
                }
                return (denom instanceof Polynom) ? new Fraction(((F) num).X[0], denom) : null;
            }
        }
        if (num instanceof Polynom) {
            if (denom instanceof F) {
                if (((F) denom).name != F.ID) {
                    return null;
                }
                return new Fraction(num, ((F) denom).X[0]);
            }
        }
        return null;
    }

    public static Fraction Rational(Element num, Ring ring) {
        if (num instanceof Polynom) {
            return new Fraction(num, ring);
        }
        if (num instanceof F) {
            if (((F) num).name != F.ID) {
                return null;
            } else {
                return new Fraction(((F) num).X[0], ring);
            }
        }
        return null;
    }

    /**
     * Возвращает случайную дробь с параметрами randomType
     *
     * @param randomType - параметры
     * @param rnd - представитьль класса Random
     * @param one - Element one того же типа, что числитель и знаменатель
     *
     * @return - случайная дробь
     */
    @Override
    public Fraction random(int[] randomType, Random rnd, Ring ring) {
        Element one = ring.numberONE();
        Element el;
        while (one instanceof Fraction) {
            el = ((Fraction) one).denom;
            one = el;
        }
        Element num1 = one.random(randomType, rnd, ring);
        Element denom1;
        do {
            denom1 = one.random(randomType, rnd, ring);
        } while (denom1.isZero(ring));
        return new Fraction(num1, denom1);
    }

    @Override
    public Element valOf(int x, Ring ring) {
        return new NumberZ(x);
    }

    @Override
    public Element valOf(long x, Ring ring) {
        return new NumberZ(x);
    }

    @Override
    public Fraction valOf(String s, Ring ring) {
        String ss = s.trim();
        int i = ss.indexOf('/');
        Element n, d;
        if (i == -1) {
            n = denom.valOf(ss, ring);
            d = denom.one(ring);
        } else {
            n = denom.valOf(ss.substring(0, i), ring);
            d = denom.valOf(ss.substring(i + 1), ring);
        }
        return new Fraction(n, d);
    }

    /**
     * Возвращает true, если дробь = 0.
     */
    @Override
    public Boolean isZero(Ring ring) {
        return num.isZero(ring);
    }

    @Override
    public Boolean isOne(Ring ring) {
        return num.isOne(ring) && denom.isOne(ring);
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        return num.isMinusOne(ring) && denom.isOne(ring);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Fraction) {
            Fraction fr = (Fraction) obj;
            return num.equals(fr.num) && denom.equals(fr.denom);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.num != null ? this.num.hashCode() : 0);
        hash = 79 * hash + (this.denom != null ? this.denom.hashCode() : 0);
        return hash;
    }

    /*
     * Возвращает true, если дробь this = дроби f
     */
    public boolean equals(Fraction f, Ring ring) {
        return num.equals(f.num, ring) && denom.equals(f.denom, ring);
    }

    @Override
    public boolean equals(Element f, Ring ring) {
        return (f instanceof Fraction) && equals((Fraction) f, ring);
    }

    /**
     * Абсолютная величина дроби
     */
    @Override
    public Fraction abs(Ring ring) {
        return new Fraction(num.abs(ring), denom.abs(ring));
    }

    /**
     * Складывает дроби this и f, затем сокращает числитель и знаменатель
     * результата на их НОД и возвращает результат.
     *
     * Параметры: this, f - дроби типа Fraction
     */
    public Element add(Fraction f, Ring ring) {
        boolean numS=num.isZero(ring), numfS=f.num.isZero(ring); 
        if (numS && numfS) return myZero(ring);
        else if (numS) {return f;} else if (numfS) {return this;}
        Element resnum;
        Element resdenom;
        if (denom.subtract(f.denom, ring).isZero(ring)) {
            resnum = num.add(f.num, ring);
            resdenom = denom;
        } else {
            resnum = num.multiply(f.denom, ring).add(f.num.multiply(denom, ring), ring);
            resdenom = denom.multiply(f.denom, ring);
        }
        if (resnum.isZero(ring))  return myZero(ring);
        if (!(resnum instanceof F) && !(resdenom instanceof F)
                && ((ring.algebra[0] == Ring.Z) || (ring.algebra[0] == Ring.Z64))) {
            return new Fraction(resnum, resdenom).cancel(ring);
        } else {
            return new Fraction(resnum, resdenom).cancel(ring);
        }
    }

    /*
     * Находит разность дробей this и f, затем сокращает числитель и знаменатель
     * результата на их НОД и возвращает результат.
     *
     * Параметры:
     * this, f   -  дроби типа Fraction
     */

    public Element subtract(Fraction f, Ring ring) {
        if (f.signum() == 0) {
            return this;
        }
        if (signum() == 0) {
            return f.negate(ring);
        }
        if (denom.compareTo(f.denom, ring) == 0) {
            return new Fraction(num.subtract(f.num, ring), denom).cancel(ring);
        }

        Element resnum = num.multiply(f.denom, ring).subtract(f.num.multiply(denom, ring), ring);
        if (resnum.signum() == 0) {
            return myZero(ring);
        }
        Element resdenom = denom.multiply(f.denom, ring);
        if (!(resnum instanceof F) && !(resdenom instanceof F) && ((ring.algebra[0] == Ring.Z) || (ring.algebra[0] == Ring.Z64))) {
            return new Fraction(resnum, resdenom).cancel(ring);
        } else {
            return new Fraction(resnum, resdenom).cancel(ring);
        }
    }

    /**
     * Умножает дроби this и f. Не сокращает числитель и знаменатель результата
     * на их НОД.
     *
     * @param f
     */
    public Element multiply(Fraction f, Ring ring) {
        if (num.signum() == 0 || f.num.signum() == 0) {
            return myZero(ring);
        }
        Element ee1 = new Fraction(num, f.denom).cancel(ring);
        Element ee2 = (new Fraction(f.num, denom).cancel(ring));
        Element num1;
        Element den1;
        if (ee1 instanceof Fraction) {
            num1 = ((Fraction) ee1).num;
            den1 = ((Fraction) ee1).denom;
            if (ee2 instanceof Fraction) {
                num1 = num1.multiply(((Fraction) ee2).num, ring);
                den1 = den1.multiply(((Fraction) ee2).denom, ring);
            } else {
                num1 = num1.multiply(ee2, ring);
            }
        } else if (ee2 instanceof Fraction) {
            num1 = ee1.multiply(((Fraction) ee2).num, ring);
            den1 = ((Fraction) ee2).denom;
        } else {
            return ee1.multiply(ee2, ring);
        }
        return new Fraction(num1, den1).cancel(ring);
    }

    /**
     * Процедура умножения дроби на число. Не сокращает на НОД(числ., знам.).
     *
     * @param b типа Element, число-сомножитель
     *
     * @return <tt>this*b</tt> типа Fraction
     */
    public Element multiplyByNumber(Element b, Ring ring) {if(b.isOne(ring))return this;
        Element fr = new Fraction(b, denom).cancel(ring);
        if (fr instanceof Fraction) {
            return new Fraction(num.multiply(((Fraction) fr).num, ring), ((Fraction) fr).denom);
        } else {
            return num.multiply(fr, ring);
        }
    }

    /**
     * Делит дробь this на дробь f, затем сокращает числитель и знаменатель
     * результата на их НОД и возвращает результат.
     *
     * Параметры: this, f - дроби типа Fraction
     */
    public Element divide(Fraction f, Ring ring) {
        if (num.signum() == 0 || f.denom.signum() == 0) {
            return myZero(ring);
        }
        Element ee1 = new Fraction(num, f.num).cancel(ring);
        ee1 = (new Fraction(f.denom, denom).cancel(ring)).multiply(ee1, ring);
        if (ee1 instanceof Fraction) {
            ee1 = ((Fraction) ee1).cancel(ring);
        }
        return ee1;
    }

    @Override
    public Fraction inverse(Ring ring) {
        if (num.signum() == 0) {
            throw new ArithmeticException("division by zero");
        }
        return (num.signum() < 0) ? new Fraction(denom.negate(ring), num.negate(ring)) : new Fraction(denom, num);
    }

    @Override
    public boolean isItNumber() {
        return num.isItNumber() && denom.isItNumber();
    }
    @Override
    public boolean isItNumber(Ring ring) {
        return num.isItNumber(ring) && denom.isItNumber(ring);
    }
    
    public Element cancel(Ring ring) {
        Element numM = num;
        Element denomM = denom; int ra=ring.algebra[0];

               if((ra==Ring.R64)||(ra==Ring.R)||(ra==Ring.Zp32)||(ra==Ring.Zp)||(ra==Ring.Complex)) return num.divide(denom, ring);
        if (numM.numbElementType() == Ring.INFTYorNAN
                | denomM.numbElementType() == Ring.INFTYorNAN) 
            return numM.divide(denomM, ring);
        if (denomM.isMinusOne(ring))  return numM.negate(ring);
        if (denomM.isOne(ring)) return numM;
        if (numM.isZero(ring)) return (denomM.isZero(ring)) ? Element.NAN : ring.numberZERO;
        if (denomM.isZero(ring)) {
            return (!numM.isItNumber()) ? this : (isNegative()) ? Element.NEGATIVE_INFINITY : Element.POSITIVE_INFINITY;
        }
        if (denomM.isNegative()){numM=numM.negate(ring); denomM=denomM.negate(ring);}       
        Polynom polNum = null;
        Polynom polDenom = null;

        if (numM instanceof Polynom) { polNum = (Polynom) numM;
            if (numM.isItNumber())  numM = ((Polynom) numM).coeffs[0];
        }
        if (denomM instanceof Polynom) {polDenom = (Polynom) denomM;
            if (denomM.isItNumber()) {denomM = ((Polynom) denomM).coeffs[0];}
        }
        if (denomM.isComplex(ring)) {
            Element cc =  denomM;
            if (!cc.Im(ring).isZero(ring)) {
                denomM = cc.absSquare(ring);
                numM = cc.conjugate(ring).multiply(numM, ring);
                if (numM instanceof Polynom) polNum = (Polynom) numM;
                if (denomM instanceof Polynom) polDenom = (Polynom) denomM;
            }
        }
        int typeRing = ring.algebra[0] % Ring.Complex;
        if (typeRing != Ring.Z && typeRing != Ring.Q && typeRing != Ring.Z64
                && denomM.isItNumber()) {
            return (denomM instanceof Polynom)
                    ? numM.divide(((Polynom) denomM).coeffs[0], ring)
                    : numM.divide(denomM, ring);
        } // Разделили полином над полем на число в знаменателе
        if (numM.isOne(ring)){ 
            if(denomM instanceof Fraction){Element nnuM=((Fraction)denomM).denom; Element ddenM=((Fraction)denomM).num;
               if (ddenM.isNegative()){ nnuM=nnuM.negate(ring); ddenM=ddenM.negate(ring);}
               return (ddenM.isOne(ring))? nnuM: new Fraction(nnuM,ddenM);
            }return new Fraction(numM,denomM);}
        if (numM.isMinusOne(ring)){ 
            if(denomM instanceof Fraction){Element nnuM=((Fraction)denomM).denom; Element ddenM=((Fraction)denomM).num;
               if (ddenM.isNegative()){ ddenM=ddenM.negate(ring);}else { nnuM=nnuM.negate(ring);}
               return (ddenM.isOne(ring))? nnuM: new Fraction(nnuM,ddenM);
            }return new Fraction(numM,denomM);}           
        if (numM instanceof Polynom && denomM instanceof Polynom
                && (typeRing == Ring.R64 | typeRing == Ring.R)
                && polNum.coeffs.length == 1 && polDenom.coeffs.length == 1) {
            Element coef = polNum.coeffs[0].divide(polDenom.coeffs[0], ring);
            int[] nn = new int[polNum.powers.length];
            int[] dd = new int[polDenom.powers.length];
            System.arraycopy(polNum.powers, 0, nn, 0, polNum.powers.length);
            System.arraycopy(polDenom.powers, 0, dd, 0, polDenom.powers.length);
            int w = Math.min(nn.length, dd.length);
            for (int i = 0; i < w; i++) {
                if (nn[i] != 0 && dd[i] != 0) {
                    int u = Math.min(dd[i], nn[i]);
                    dd[i] -= u;
                    nn[i] -= u;
                }
            }
            return new Fraction(new Polynom(nn, new Element[] {coef}).truncate(),
                    new Polynom(dd, new Element[] {ring.numberONE}).truncate()
            );
//             return new Polynom(polNum.powers,new Element[]{coef})
//                .divideToFraction(new Polynom(polDenom.powers,new Element[]{ring.numberONE}),ring) ;
        } // случай отношения двух мономов

        if (numM instanceof Polynom) {
            if (typeRing == Ring.Q && denomM.isItNumber()) {
                Element[] newCoef = new Element[polNum.coeffs.length];
                for (int i = 0; i < newCoef.length; i++) {
                    newCoef[i] = (polNum.coeffs[i] instanceof Fraction)
                            ? polNum.coeffs[i].divide(denomM, ring):
                            (denomM instanceof Fraction)?
                            new Fraction(polNum.coeffs[i], ((Fraction)denomM).num).cancel(ring).multiply(((Fraction)denomM).denom, ring)
                            : new Fraction(polNum.coeffs[i], denomM).cancel(ring);
                }
                return new Polynom(polNum.powers, newCoef);
            }// деление полинома на число в кольце Q
            if (typeRing == Ring.Z && denomM.isItNumber()) {
                if (denomM instanceof Polynom) {
                    denomM = polDenom.coeffs[0];
                }
                Element gcdcoef = polNum.GCDNumPolCoeffs(ring);
                gcdcoef = gcdcoef.GCD(denomM, ring);
                if (gcdcoef.isOne(ring)) {
                    return this;
                }
                return new Fraction(polNum.divideByNumber(gcdcoef, ring), denomM.divide(gcdcoef, ring));
            }// деление полинома на число в кольце Z
        }
        if ((denomM instanceof Polynom) && (numM.isItNumber())
                && ((typeRing == Ring.Q) || (typeRing == Ring.Z))) {
            if (numM instanceof Polynom) {
                numM = polNum.coeffs[0];
            }
            polDenom = (Polynom) denomM;
            Element gcdcoef = polDenom.GCDNumPolCoeffs(ring);
            if (gcdcoef.isOne(ring)) {
                if (this.denom.isNegative()) {//Если знаменатель <0, то исправим числитель и знаменатель
                  this.num = this.num.negate(ring); this.denom = this.denom.negate(ring); }            
                return this;
            }
            gcdcoef = gcdcoef.GCD((numM instanceof Fraction)
                    ? ((Fraction) numM).num : numM, ring);
            if (gcdcoef.isOne(ring)) {
                return this;
            }
            Fraction frr = new Fraction(numM.divide(gcdcoef, ring), polDenom.divideByNumber(gcdcoef, ring));
            return (frr.denom.isOne(ring)) ? frr.num : frr;
        }// деление числа на полинома   в кольце Z, Q

        if (numM instanceof Fraction) {
            return numM.divide(denomM, ring);
        } else if(numM instanceof Complex) {
            Complex c = (Complex)numM;
            if(c.re instanceof Fraction || c.im instanceof Fraction) {
                return numM.divide(denomM, ring);
            }
        }
        //Найдем НОД числителя и знаменателя и сократим их на НОД.
         boolean flagFn=false, flagFd=false;
        if (numM instanceof F){
          numM=
                  ring.CForm.ElementConvertToPolynom(numM); 
          flagFn=true;}   
        if (denomM instanceof F){
            denomM=ring.CForm.ElementConvertToPolynom(denomM);flagFd=true;} 
        Element gcd = numM.GCD(denomM, ring);
        if (!gcd.isOne(ring)) {
            numM = numM.divideExact(gcd, ring);
            denomM = denomM.divideExact(gcd, ring);
        }
        //           if(numM instanceof F){num=num.expand(ring);}
        //           if(denomM instanceof F){denomM=denomM.expand(ring);}
        if(flagFn || flagFd){
        if (flagFn){numM=ring.CForm.UnconvertAllLevels(numM);}
        if (flagFd){denomM=ring.CForm.UnconvertAllLevels(denomM);}    } 
        if (denomM.isNegative()) {//Если знаменатель <0, то исправим числитель и знаменатель
            numM = numM.negate(ring); denomM = denomM.negate(ring); }
        if (denomM.isOne(ring)) { return numM;}
        return new Fraction(numM, denomM);
    }

    /**
     * Возвращает дробь (-this) Параметры: this - дробь типа Fraction
     */
    @Override
    public Fraction negate(Ring ring) {
        return new Fraction(num.negate(ring), denom);
    }

    @Override
    public Element pow(int n, Ring ring) {
        Element Res = this.cancel(ring);
        if (Res instanceof Fraction) {
            return new Fraction(((Fraction) Res).num.pow(n, ring), ((Fraction) Res).denom.pow(n, ring));
        } else {
            return Res.pow(n, ring);
        }
    }


    /*
     * Возводит дробь this в степень power
     * Параметры:
     * this  -  дробь типа Fraction
     * power -  степень
     */
    @Override
    public Element[] pow(int n, int m, Ring ring) {
        Element f = cancel(ring);
        if (!(f instanceof Fraction)) {
            return f.pow(n, m, ring);
        }
        Fraction[] ff = new Fraction[m];
        Element[] nn = num.pow(n, m, ring);
        Element[] dd = denom.pow(n, m, ring);
        for (int k = 0; k < m; k++) {
            ff[k] = new Fraction(nn[k], dd[0]);
        }
        return ff;
    }

    /**
     * Вычисление первого значения дробной степрени num/den
     *
     * @param num числитель показателя степени
     * @param den знаменатель показателя степени
     * @param ring --ring
     *
     * @return Element -- den значений дробной степени
     */
    @Override
    public Element powTheFirst(int n, int d, Ring ring) {
        Element nn = num.powTheFirst(n, d, ring);
        Element dd = denom.powTheFirst(n, d, ring);
        return new Fraction(nn, dd);
    }

    /*
     * Возвращает число double равное этой дроби
     */
    @Override
    public double doubleValue() {
        if (denom.signum() == 0) {
            throw new ArithmeticException("division by zero");
        }
        return num.doubleValue() / denom.doubleValue();
    }

    /**
     * Возвращает знак дроби this
     */
    @Override
    public int signum() {
        return num.signum() * denom.signum();
    }

    /**
     * Возвращает только такие типы: Ring.Q, Ring.CQ, Ring.Rational
     *
     * @return numbElementType()
     */
    @Override
    public int numbElementType() {
        int max = Math.max(denom.numbElementType(), num.numbElementType());
        return (max == Ring.Z || max == Ring.Q) ? Ring.Q : (max == Ring.CZ || max == Ring.CQ)
                ? Ring.CQ : Ring.Rational;
    }

    /**
     * The square of module of this complex number OR square for all the other
     * numbers
     *
     * @return re^2 + im^2 OR this^2
     */
    @Override
    public Element absSquare(Ring ring) {
        return num.absSquare(ring).divide(denom.absSquare(ring), ring);
    }

    @Override
    public Element id(Ring r) {
        return this;
    }

    /**
     * Сравнение с дробью f. Возвращает: -1 при this<f 0 при this=f 1 при this>f
     *
     * Параметры: f - дробь Fraction
     */
    public int compareTo(Fraction f, Ring ring) {
        boolean flagCompl = (f.denom instanceof Complex) || (denom instanceof Complex)
                || (f.num instanceof Complex) || (num instanceof Complex);
        if (!flagCompl) {
            int sign_this = signum();
            int sign_f = f.signum();
            if (sign_this < sign_f) {
                return -1;
            }
            if (sign_this > sign_f) {
                return 1;
            }
            return (subtract(f, ring)).signum();
        }
        // Есть комплексная часть у кого-то
        Element as1 = absSquare(ring);
        Element as2 = f.absSquare(ring);
        int comp = as1.compareTo(as2, ring);
        if (comp != 0) {
            return comp;
        }
        Complex fd = (f.denom instanceof Complex) ? (Complex) f.denom : new Complex(f.denom, ring);
        Complex d = (denom instanceof Complex) ? (Complex) denom : new Complex(denom, ring);
        Complex fn = (f.num instanceof Complex) ? (Complex) f.num : new Complex(f.num, ring);
        Complex n = (num instanceof Complex) ? (Complex) num : new Complex(num, ring);
        Element ff = fn.divide(fd, ring);
        Element tt = n.divide(d, ring);
        Complex ffC = (ff instanceof Complex) ? (Complex) ff : new Complex(ff, ring);
        Complex ttC = (tt instanceof Complex) ? (Complex) tt : new Complex(tt, ring);
        return ttC.compareToOnCircle(ffC, ring);
    }

    @Override
    public int compareTo(Element x) {
        return compareTo(x, Ring.ringR64xyzt);
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        Fraction frac = (x instanceof Fraction) ? (Fraction) x : new Fraction(x, ring);
        return compareTo(frac, ring);
    }

    // TODO: переписать на общий случай коэффициентов.
    /**
     * Вычисление предела на плюс бесконечности для f/g (f,g in Z[x])
     *
     * @return the value of limit on plus infinity
     */
    public Element limitOfRationalOnPlusInfinity(Ring ring) {
        Polynom numerat = (Polynom) num;
        Polynom denomen = (Polynom) denom;
        NumberZ nCoeff0 = null;
        NumberZ dCoeff0 = null;
        if (!(numerat.isZero(ring))) {
            nCoeff0 = (NumberZ) (numerat.coeffs[0]);
        }
        if (!denomen.isZero(ring)) {
            dCoeff0 = (NumberZ) (denomen.coeffs[0]);
        }
        if ((!numerat.isItNumber()) && (!denomen.isItNumber())) {
            int sub = numerat.powers[0] - denomen.powers[0];
            if (sub > 0) {
                Element zer = ring.numberONE().myZero(ring);
                if (((nCoeff0.compareTo(zer, ring) == -1)
                        && (dCoeff0.compareTo(zer, ring) != -1))
                        || ((nCoeff0.compareTo(zer, ring) != -1)
                        && (dCoeff0.compareTo(zer, ring) == -1))) {
                    return NumberR.NEGATIVE_INFINITY;
                } else {
                    return NumberR.POSITIVE_INFINITY;
                }
            }
            if (sub < 0) {
                return NumberR.ZERO;
            }
            if (dCoeff0.signum == -1) {
                return new Fraction(nCoeff0.negate(), dCoeff0.negate());
            }
            return new Fraction(nCoeff0, dCoeff0);
        } else if ((!numerat.isItNumber()) && (denomen.isItNumber())) {
            Element zer = ring.numberONE().myZero(ring);
            if (((nCoeff0.compareTo(zer, ring)) == -1)
                    && (dCoeff0.compareTo(zer, ring) != -1)
                    || ((nCoeff0.compareTo(zer, ring) != -1)
                    && (dCoeff0.compareTo(zer, ring) == -1))) {
                return NumberR.NEGATIVE_INFINITY;
            } else {
                return NumberR.POSITIVE_INFINITY;
            }
        } else if ((numerat.isItNumber()) && (!denomen.isItNumber())) {
            return NumberR.ZERO;
        } else {
            if (nCoeff0 == null) {
                return NumberZ.ZERO;
            }
            if (dCoeff0 == null) {
                return NumberR.NAN;
            }
            if (dCoeff0.signum == -1) {
                return new Fraction(nCoeff0.negate(), dCoeff0.negate());
            }
            Fraction q = new Fraction(nCoeff0, dCoeff0);
            return q.cancel(ring);
        }
    }

    @Override
    public String toString() {
        Ring ring = Ring.ringR64xyzt;
        return toString(ring);
    }

    /**
     * Преобразовывает дробь в строку String
     */
    @Override
    public String toString(Ring ring) {String res; 
        if (denom.isOne(ring)) return num.toString(ring); else if(denom.isMinusOne(ring))num.negate(ring).toString(ring);
        int typeD = denom.numbElementType();
        int typeN = num.numbElementType();
        res= ((num instanceof Fraction)||(typeN>=Ring.Complex))? "(" + num.toString(ring) + ")": num.toString(ring);
        if (!denom.isOne(ring)) {res= (typeD>=Ring.Complex)
                ? res+"/(" + denom.toString(ring) + ")"
                : res+ "/"+ denom.toString(ring); 
        }return "("+res+")";
    }
//        public String toString(Ring ring) {String res; 
//        if (denom.isOne(ring)) return num.toString(ring); else if(denom.isMinusOne(ring))num.negate(ring).toString(ring);
//        int typeD = denom.numbElementType();
//        int typeN = num.numbElementType();
//        res= ((num instanceof Fraction)||(typeN>=Ring.Complex))? "(" + num.toString(ring) + ")": num.toString(ring);
//        if (!denom.isOne(ring)) 
//          if (ring.FRACTION==0){res= (typeD>=Ring.Complex)
//                ? res+"/(" + denom.toString(ring) + ")"
//                : res+ "/"+ denom.toString(ring); return "("+res+")";
//        }else res="\frak{"+res+"}{"+denom.toString(ring)+"}"; 
//        return  res ;
//    }
    
    @Override
    public long longValue() {
        return (ZValue()).longValue();
    }

    @Override
    public int intValue() {
        return (int) longValue();
    }

    public NumberR RValue() {
        Ring ring = new Ring("R[]");
        return (NumberR) (num.toNumber(Ring.R, ring)).divide(denom.toNumber(Ring.R, ring), ring);
    }

    public NumberZ ZValue() {
        return RValue().NumberRtoNumberZ();
    }

    @Override
    public VectorS extendedGCD(Element x, Ring ring) {
        return null;
    }

    @Override
    public Element GCD(Element x, Ring ring) {
        if (x instanceof Fraction) {
            Element numX = num.GCD(((Fraction) x).num, ring);
            Element denX = denom.GCD(((Fraction) x).denom, ring);
            return (denX.isOne(ring)) ? numX : new Fraction(numX, denX);
        }
        return num.GCD(x, ring);
    }

    public Fraction abs(Element x) {
        return abs((Fraction) (x));
    }

    @Override
    public Element subtract(Element x, Ring ring) {
        if (isZero(ring)) {
            return x.negate(ring);
        }
        if (x.isZero(ring)) {
            return this;
        }
        if (x.isInfinite()) {
            return (isItNumber()) ? x.negate(ring) : new F(F.SUBTRACT, new Element[] {this, x});
        }
        int this_type = denom.numbElementType();
        if (x instanceof Fraction) {
            Fraction xx = (Fraction) x;
            int x_type = xx.denom.numbElementType();
            if (x_type == this_type) {
                return subtract(xx, ring);
            }
            Element x_d = xx.denom.toNumber(this_type, ring);
            Element x_n = xx.num.toNumber(this_type, ring);
            return subtract(new Fraction(x_n, x_d), ring);
        }
        if (x instanceof F) {
            return new F(F.SUBTRACT, new Element[] {this, x});
        }
        return new Fraction(num.subtract(denom.multiply(x, ring), ring), denom);
    }

    @Override
    public Element add(Element x, Ring ring) {
        if (x.isZero(ring)) {
            return this;
        }
        if (isZero(ring)) {
            return x;
        }
        if (x.isInfinite()) {
            return (isItNumber()) ? x : new F(F.ADD, new Element[] {this, x});
        }
        int this_type = denom.numbElementType();
        if (ring.algebra[0] == Ring.Q && x instanceof Polynom && isItNumber()) {
            return new Polynom(this).add(x, ring);
        }
        if (x instanceof Fraction) {
            Fraction xx = (Fraction) x;
            int x_type = xx.denom.numbElementType();
            if (x_type == this_type) {
                return add(xx, ring);
            }
            return add(new Fraction(xx.num, xx.denom), ring);
        }
        if (x instanceof F) {
            return new F(F.ADD, new Element[] {this, x});
        }
        return new Fraction(num.add(denom.multiply(x, ring), ring), denom);
    }

    @Override
    public Element multiply(Element x, Ring ring) {
        if (x.isInfinite()) {
            if (isItNumber()) {
                return (isZero(ring)) ? Element.NAN : (signum() < 0) ? x.negate(ring) : x;
            }
            return new F(F.MULTIPLY, new Element[] {this, x});
        }
        if (x instanceof Fraction) {
            return multiply(((Fraction) x), ring);
        }
        if (x.numbElementType() > Ring.Polynom) {
            return x.multiply(this, ring);
        }
        return multiplyByNumber(x, ring); // x=number or x=Polynom
    }

    @Override
    public Element divide(Element x, Ring ring) {
        if (x.isInfinite()) {
            return (isItNumber()) ? ring.numberZERO : new Fraction(num, denom.multiply(x, ring));
        }
        int this_type = denom.numbElementType();
        if (x instanceof Fraction) {
            Fraction xx = (Fraction) x;
            int x_type = xx.denom.numbElementType();
            if (x_type == this_type) {
                return divide(xx, ring);
            }
            Element nn = xx.denom.toNumber(this_type, ring);
            Element x_d = (nn==null)? xx.denom: nn;
            nn = xx.num.toNumber(this_type, ring);
            Element x_n = (nn == null)? xx.num : nn;
            return divide(new Fraction(x_n, x_d), ring);
        }
        if (x.numbElementType() <= Ring.Polynom) {
            Element ff = (new Fraction(num, x)).cancel(ring);
            if (ff instanceof Fraction) {
                return new Fraction(((Fraction) ff).num, ((Fraction) ff).denom.multiply(denom, ring)).cancel(ring);
            } else if (denom.isOne(ring)) {
                return ff;
            } else {
                return new Fraction(ff, denom).cancel(ring);
            }
        }
        return new Fraction(num, denom.multiply(x, ring)).cancel(ring);
    }

    @Override
    public Element multiply(Element x, int i, Ring ring) {
        return multiply(x, ring);
    }

    public Fraction valOf(Fraction x) {
        return x;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        if ((num.numbElementType() < Ring.Polynom) && (denom.numbElementType() < Ring.Polynom)) {
            return toNumber(Algebra, r);
        }
        Element nu, de;
        nu = num.toNewRing(Algebra, r);
        de = denom.toNewRing(Algebra, r);
        return new Fraction(nu, de);
    }

    @Override
    public Element toNumber(int newType, Ring ring) {
        Element nu = num.toNumber(newType, ring);
        Element de = denom.toNumber(newType, ring);
        Element res;
        switch (newType) {
            case Ring.Z64:
                nu = new NumberZ(nu.longValue());
                de = new NumberZ(de.longValue());
            // fallthrough
            case Ring.Z:
                res = nu.divideToFraction(de, ring);
                break;
            default:
                res = nu.divide(de, ring);
        }
        return res;
    }

    @Override
    public Element value(Element[] var, Ring ring) {
        Element num_, denom_;
        if (num.numbElementType() < Ring.NumberOfSimpleTypes) {
            num_ = num;
        } else {
            num_ = num.value(var, ring);
        }
        if (denom.numbElementType() < Ring.NumberOfSimpleTypes) {
            denom_ = denom;
        } else {
            denom_ = denom.value(var, ring);
        }
        return num_.divide(denom_, ring);
    }

    /**
     * Вычисление производной дроби
     *
     * @param n - номер переменной в кольце
     * @param ring
     *
     * @return
     */
    @Override
    public Element D(int n, Ring ring) {
        Element f1 = num;
        Element f2 = denom;
        if (num.isItNumber() & denom.isItNumber()) {
            return ring.numberZERO;
        }
        Element first = f1.D(n, ring);
        Element second = f2.D(n, ring);
        f1 = (first.multiply(f2, ring)).subtract(second.multiply(f1, ring), ring);
        f2 = f2.multiply(f2, ring);
        return new Fraction(f1, f2);
    }

    // функция для создания Rational на основе функции деления с двумя аргументами -
    // X[0] -числитель, X[1] - знаменатель. Если аргументы целые числа, то предварительно
    // образуется соответствующий им полиномы. Результат - Fraction от двух полиномов.
    public static Fraction Rational(F f, Ring ring) {
        Element num;
        Element denom;
        if ((f.X[0] instanceof NumberZ)
                && (f.X[1] instanceof NumberZ)) {
            num = Polynom.polynomFromNumber(f.X[0], ring);
            denom = Polynom.polynomFromNumber(f.X[1], ring);
        } else if ((f.X[0] instanceof NumberZ) && (f.X[1] instanceof Polynom)) {
            num = Polynom.polynomFromNumber(f.X[0], ring);
            denom = (Polynom) f.X[1];
        } else if ((f.X[0] instanceof Polynom) && (f.X[1] instanceof NumberZ)) {
            num = (Polynom) f.X[0];
            denom = Polynom.polynomFromNumber(f.X[1], ring);
        } else {
            num = (Polynom) f.X[0];
            denom = (Polynom) f.X[1];
        }
        return new Fraction(num, denom);
    }

    public Element limitOfDivFrac(Ring ring) {
        Polynom numerat = (Polynom) num;
        Polynom denomen = (Polynom) denom;
        NumberZ nCoeff0 = null;
        NumberZ dCoeff0 = null;
        Element one = ring.numberONE().myZero(ring);
        if (!numerat.isZero(ring)) {
            nCoeff0 = (NumberZ) (numerat.coeffs[0]);
        }
        if (!denomen.isZero(ring)) {
            dCoeff0 = (NumberZ) (denomen.coeffs[0]);
        }
        // если числитель и знаменатель - полиномы
        if ((!numerat.isItNumber()) && (!denomen.isItNumber())) {
            int sub = numerat.powers[0] - denomen.powers[0];
            // если степень числителя больше степени знаменателя
            if (sub > 0) {
                if (((nCoeff0.compareTo(one, ring) == -1) && (dCoeff0.compareTo(one, ring) != -1))
                        || ((nCoeff0.compareTo(one, ring) != -1) && (dCoeff0.compareTo(one, ring) == -1))) {
                    return NumberR.NEGATIVE_INFINITY;
                } else {
                    return NumberR.POSITIVE_INFINITY;
                }
            }
            // если степень знаменателя больше степени числителя
            if (sub < 0) {
                return NumberR.ZERO;
            }
            if (dCoeff0.signum == -1) {
                Fraction q = new Fraction(nCoeff0.negate(), dCoeff0.negate());
                return q.cancel(ring);
            }
            // если степени равны
            Fraction q = new Fraction(nCoeff0, dCoeff0);
            return q.cancel(ring);
            // если числитель - полином, а знаменатель - число
        } else if ((!numerat.isItNumber()) && (denomen.isItNumber())) {
            if (dCoeff0 == null) {
                return NumberR.NAN;
            }
            if (((nCoeff0.compareTo(one, ring) == -1) && (dCoeff0.compareTo(one, ring) != -1))
                    || ((nCoeff0.compareTo(one, ring) != -1) && (dCoeff0.compareTo(one, ring) == -1))) {
                return NumberR.NEGATIVE_INFINITY;
            } else {
                return NumberR.POSITIVE_INFINITY;
            }
        } // если числитель - число, а знаменатель - полином
        else if ((numerat.isItNumber()) && (!denomen.isItNumber())) {
            return NumberR.ZERO;
        } else {
            if (nCoeff0 == null) {
                return NumberZ.ZERO;
            }
            if (dCoeff0 == null) {
                return NumberR.NAN;
            }
            if (dCoeff0.signum == -1) {
                Fraction q = new Fraction(nCoeff0.negate(), dCoeff0.negate());
                return q.cancel(ring);
            }
            Fraction q = new Fraction(nCoeff0, dCoeff0);
            return q.cancel(ring);
        }
    }

    @Override
    public Boolean isNegative() {
        return (signum() == -1);
    }

    /**
     * Returns <code>true</code> if this <code>Double</code> value is infinitely
     * large in magnitude, <code>false</code> otherwise.
     *
     * @return  <code>true</code> if the value represented by this object is
     * positive infinity or negative infinity; <code>false</code> otherwise.
     */
    @Override
    public boolean isPositiveInfinity() {
        return (value == POSITIVE_INFINITY_DOUBLE);
    }

    @Override
    public boolean isNegativeInfinity() {
        return (value == NEGATIVE_INFINITY_DOUBLE);
    }

    @Override
    public Element sin(Ring ring) {
        return new FuncNumberQZ(ring).trigFunc(F.SIN, this);
    }

    @Override
    public Element cos(Ring ring) {
        return new FuncNumberQZ(ring).trigFunc(F.COS, this);
    }

    @Override
    public Element tan(Ring ring) {
        return new FuncNumberQZ(ring).trigFunc(F.TG, this);
    }

    @Override
    public Element ctg(Ring ring) {
        return new FuncNumberQZ(ring).trigFunc(F.CTG, this);
    }

    @Override
    public Element sqrt(Ring ring) {
        return new FuncNumberQZ(ring).sqrt(this);
    }

    @Override
    public Element arcsn(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.SIN, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(this, F.SIN, ring);
        }
    }

    @Override
    public Element arccs(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.COS, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(this, F.COS, ring);
        }
    }

    @Override
    public Element arctn(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.TG, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(this, F.TG, ring);
        }
    }

    @Override
    public Element arcctn(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.CTG, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(this, F.CTG, ring);
        }
    }

    @Override
    public Element exp(Ring ring) {
        return new F(F.EXP, new Element[] {this});
    }

    @Override
    public Element ln(Ring ring) {
        return new F(F.LN, new Element[] {this});
    }

    /**
     * Integer part of quotient and fraction, which is remainder divided by
     * denominator.
     *
     * @param ring -- ring
     * @param var -- the number or Name of the main variable (i.e. 0,1,2 or
     * x,y,z)
     *
     * @return [0] - integer part of quotient, [1] - remainder
     *
     */
    public Element[] quotientAndProperFraction(Element var, Ring ring) {
        Element[] hh=quotientAndRemainder(var,   ring);
        if (hh[1].isZero(ring)) return hh; 
        Fraction fr= (denom.isNegative())
                    ? new Fraction(hh[1].negate(ring),denom.negate(ring))
                    : new Fraction(hh[1],denom);
        return new Element[] {hh[0], fr};
    }
    public Element[] quotientAndRemainder(Ring ring){
                return num.quotientAndRemainder(denom,ring);}
    @Override
        public Element[] quotientAndRemainder(Element var, Ring ring) {
        Element ee=this; 
        if ((num instanceof Polynom) && (denom instanceof Polynom)) {
              return num.quotientAndRemainder(denom, var, ring);
        }else{int type=num.numbElementType()&(~Ring.Complex);
        if ((type==Ring.Z) || (type==Ring.Z64))return num.divideAndRemainder(denom, ring);
        else{
          if ((num.isItNumber()) && (denom.isItNumber())) {
            ee= num.toNumber(ring.algebra[0], ring).divide(denom.toNumber(ring.algebra[0], ring), ring);
            if(ee instanceof Fraction) return ((Fraction)ee).quotientAndRemainder(ring);
          }
          if ((num instanceof Polynom) && (denom.isItNumber())) {
              Element den = denom.toNumber(ring.algebra[0], ring);
              if ((ring.algebra[0]==Ring.Z)||(ring.algebra[0]==Ring.Z64))
                 return ((Polynom)num).divideAndRemainderFromNumber(den,ring);
              ee = ((Polynom) num).divideByNumber(den, ring); 
          }
            return new Element[] {ee, ring.numberZERO};
        } }
    }

    /**
     * Transform a fraction to proper fraction plus integer part
     *
     * @param ring Ring
     *
     * @return Integer part plus proper fraction
     */
    public Element[] quotientAndProperFraction(Ring ring) {
        return quotientAndProperFraction(NumberZ.ZERO, ring);
    }

//    @Override
//    public Element  properForm(Ring ring) {
//        Element[] qr= quotientAndProperFraction( ring);
//        if (qr[1].isZero(ring))return qr[0];
//        return  new VectorS( qr[0], new  Fraction (qr[1], denom).cancel(ring));
//    }
    @Override
    public Element round(Ring ring) {
        Element th = cancel(ring);
        if (th instanceof Fraction) {
            Fraction fr = (Fraction) th;
            Element[] qr = fr.num.divideAndRemainder(fr.denom, ring);
            Element delta = qr[1].isNegative() ? ring.numberMINUS_ONE : ring.numberONE;
            return (qr[1].add(qr[1], ring).abs(ring).compareTo(fr.denom, ring) >= 0)
                    ? qr[0].add(delta, ring) : qr[0];
        }
        return th;
    }

    @Override
    public Element ceil(Ring ring) {
        Element[] dr = num.divideAndRemainder(denom, ring);
        if (dr[1].isZero(ring)) {
            return dr[0];
        }
        return (dr[1].isNegative()) ? dr[0] : dr[0].add(ring.numberONE, ring);
    }

    @Override
    public Element floor(Ring ring) {
        Element[] dr = num.divideAndRemainder(denom, ring);
        if (dr[1].isZero(ring)) {
            return dr[0];
        }
        return (dr[1].isNegative()) ? dr[0].subtract(ring.numberONE, ring) : dr[0];
    }

    @Override
    public Element mod(Element mod, Ring ring) {
        Element dr = divide(mod, ring).floor(ring);
        return subtract(mod.multiply(dr, ring), ring);
    }

    @Override
    public Element num(Ring ring) {
        return num;
    }

    @Override
    public Element denom(Ring ring) {
        return denom;
    }

    @Override
    public boolean isEven() {
        return num.isEven();
    }

    @Override
    public Element rootTheFirst(int n, Ring ring) {
        return new Fraction(num.rootTheFirst(n, ring), denom.rootTheFirst(n, ring)).cancel(ring);
    }

    @Override
    public Element expand(Ring ring) {
        if (num instanceof F) {
            if (((F) num).name == F.ID) {
                num = ((F) num).X[0];
            }
        }
        if (denom instanceof F) {
            if (((F) denom).name == F.ID) {
                denom = ((F) denom).X[0];
            }
        }
        if (num.numbElementType() <= Ring.Polynom & denom.numbElementType() <= Ring.Polynom) {
            return cancel(ring);
        }
        return ring.CForm.UnconvertToElement(ring.CForm.ElementConvertToPolynom(this));
    }

    @Override
    public Element[] posConst() {
        return NumberZ.POSCONST;
    }
    @Override
    public Element[] negConst() {
        return NumberZ.NEGCONST;
    }
    @Override
    public Element Factor(boolean doNewVector, Ring ring) {
        return num.Factor(doNewVector, ring).divide(denom.Factor(doNewVector, ring), ring);
    }

    /**
     * Замена порядка переменных во всех полиномах этого элемента
     *
     * @param varsMap - правило замены порядка переменных
     * @param flag - куда/откуда
     * @param ring - Ring
     *
     * @return - результат замены порядка переменныхво всех полиномах
     */
    @Override
    public Element changeOrderOfVars(int[] varsMap, boolean flag, Ring ring) {
        return new Fraction(
                num.changeOrderOfVars(varsMap, flag, ring),
                denom.changeOrderOfVars(varsMap, flag, ring));
    }

    @Override
    public Element simplify(Ring ring) {
        Element res = ring.CForm.simplify_init(new F(F.ID, this));
        return res;
    }

    @Override
    public boolean isComplex(Ring ring) {
        return num.isComplex(ring) || denom.isComplex(ring);
    }

    @Override
    public Element Re(Ring ring) {
        if (!isComplex(ring)) {
            return this;
        }
        Element Res = NumberZ.ZERO;
        if (!denom.isComplex(ring)) {
            return new Fraction(num.Re(ring), denom).cancel(ring);
        }
        Element conDen = denom.conjugate(ring);
        return new Fraction(conDen.multiply(num, ring).Re(ring), denom.absSquare(ring));
    }

    @Override
    public Element Im(Ring ring) {
        Element Res = NumberZ.ZERO;
        if (!isComplex(ring)) {
            return Res;
        }
        if (!denom.isComplex(ring)) {
            return new Fraction(num.Im(ring), denom).cancel(ring);
        }
        Element conDen = denom.conjugate(ring);
        return new Fraction(conDen.multiply(num, ring).Im(ring), denom.absSquare(ring));
    }
    public static Fraction elementToFraction(Element el,Ring ring){Element res=el;
       if(el instanceof F){F f=(F)el; 
                       if (f.name==F.DIVIDE) return new Fraction(f.X[0], f.X[1]); }
       if(el instanceof FactorPol) {FactorPol fp=(FactorPol)(el); 
                                    res= fp.toPolynomOrFraction( ring);}
       if (res instanceof Fraction) return (Fraction)res; else return new Fraction(res, NumberZ.ONE);
    
    } 
/**
 *   Избавляемся от иррациональности в знаменателе типа \sqrt(a) и (\sqrt(a)+b)
 * @param ring
 * @return 
 */
    public Element disTransInDenom( Ring ring){Polynom mult=null; // future multiplier for denominator
      if(denom instanceof F){
        F res=(F)denom; int name=res.name; Element a=res.X[0]; Element b;
        if ((name==F.SQRT)||(name==F.CUBRT)||(name==F.ROOTOF)){
          if (name==F.SQRT) b= res; else
              if (name==F.CUBRT) b= new F(F.CUBRT, new F(F.intPOW, a, NumberZ.TWO)); else
                  b= new F(F.intPOW,  res, res.X[1].subtract(ring.numberONE, ring));
          return new Fraction( new F(F.MULTIPLY, b, num).Simplify(ring),a);
        } 
        // легкая жизнь кончилась........... пошли суммы и прочее ..............
        int nRVl=ring.CForm.newRing.varNames.length;
        Element polD= ring.CForm.ElementToPolynom(denom, false);
        Element polN= ring.CForm.ElementToPolynom(num, false);
        Ring ring1=ring.CForm.makeWorkRing(0);
        int  Vl=ring1.varNames.length; int n=Vl-nRVl;
        Element[] varNew= new Element[n];
        System.arraycopy(ring1.varPolynom, nRVl, varNew, 0, n);
        VectorS funcNew = (VectorS)ring.CForm.ElementToF(new VectorS(varNew));
        varNew=funcNew.V;
        int maxRootOf=0;          // max value of int root in the function rootOf
        int[] priznak= new int[4]; // counters for types of fumctions
        for (int i = 0; i < varNew.length; i++) {
              if(varNew[i] instanceof F){int name1=((F)varNew[i]).name; 
               switch(name1){
                   case F.SQRT: priznak[0]+=1; break;
                   case F.CUBRT: priznak[1]+=1; break;
                   case F.ROOTOF: priznak[2]+=1; maxRootOf=Math.max(maxRootOf, ((F)varNew[i]).X[1].intValue());  break;
                   default: priznak[4]+=1;
               }
              }else priznak[4]+=1;
        } int priz=0;
        for (int i = 0; i < 3; i++) priz+=priznak[i] ;  
        // дальше пока короткий анализ на двучлены
        if (polD instanceof Polynom){
            Polynom pD=(Polynom)polD;       
            if ((priz<3)&&(priz>0)){ 
               if ((maxRootOf<3)&&(priznak[1]==0)) { // дополняем до разности квадратов
                   // считаем, что старший -- это наш x, иначе потом надо добавить перестановку переменных             
                 Element[] cf=new Element[pD.coeffs.length];
                 System.arraycopy(pD.coeffs, 0, cf, 0, pD.coeffs.length);
                 cf[0]=cf[0].negate(ring);
                 mult=new Polynom(pD.powers,cf);  
            }}else 
            if ((maxRootOf==3)||(priznak[0]==0)) { // дополняем до разности кубов (x-1)(x^2+x+1))
                     // считаем, что старший -- это наш x, иначе потом надо добавить перестановку переменных               
                 int var=pD.powers.length/ pD.coeffs.length;
                 Element[] cf=new Element[pD.coeffs.length-1]; Element[] cf0=new Element[]{pD.coeffs[0]};
                 System.arraycopy(pD.coeffs, 1, cf, 0, cf.length);
                 int[] ps=new int[pD.powers.length-var]; int[] ps0=new int[var] ;
                 System.arraycopy(pD.powers, var, ps, 0, ps.length);
                 System.arraycopy(pD.coeffs, 0, ps0, 0, var);
                 for (int j = 0; j < cf.length; j++) {cf[j]=cf[j].negate(ring);
                 Polynom multX=new Polynom(ps0,cf0);   Polynom multA=new Polynom(ps,cf);
                 Polynom XpA=multX.add(multA, ring);
                 mult=XpA.multiply(XpA, ring).subtract(multX.multiply(multA, ring), ring);
                }
            } else  return this;    
            return ring.CForm.UnconvertAllLevels(new Fraction( num.multiply(mult, ring).Simplify(ring), 
                                 denom.multiply(mult,ring).Simplify(ring)));
        }
      }return this;         
    } 
  }