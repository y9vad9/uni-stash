package com.mathpar.number;

import com.mathpar.func.F;
import com.mathpar.func.Fname;
import com.mathpar.func.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.mathpar.matrix.*;
//import com.mathpar.lcTransform.*;
import com.mathpar.polynom.Polynom;

import static com.mathpar.polynom.PascalTriangle.*;

/**
 * Abstract class Element for all mathematical objects since ParCA3 from
 * 22_01_2010
 *
 * @author gennadi
 */
public class Element implements Serializable, Comparable<Element>, Cloneable {
    /**
     * A constant holding the positive infinity of type <code>double</code>. It
     * is equal to the value returned by
     * <code>Double.longBitsToDouble(0x7ff0000000000000L)</code>.
     */
    public static final double POSITIVE_INFINITY_DOUBLE = 1.0 / 0.0;
    /**
     * A constant holding the negative infinity of type <code>double</code>. It
     * is equal to the value returned by
     * <code>Double.longBitsToDouble(0xfff0000000000000L)</code>.
     */
    public static final double NEGATIVE_INFINITY_DOUBLE = -1.0 / 0.0;
    public static final double NaN_DOUBLE = 0.0d / 0.0;
    public static final NumberZ64 TRUE = NumberZ64.ONE;
    public static final NumberZ64 FALSE = NumberZ64.ZERO;
    public static final int NOT_EQUAL = -3;
    public static final int LESS = -2;
    public static final int LESS_OR_EQUAL = -1;
    public static final int EQUAL = 0;
    public static final int GREATER = 2;
    public static final int GREATER_OR_EQUAL = 1;
    /**
     * The length of the list of natural integers "POSCONST" in the Ring
     */
    public static final int MAX_CONSTANT = 16;
    public static Element[] POSCONST = new NumberZ[MAX_CONSTANT + 1];
    public static Element negConst[] = new NumberZ[MAX_CONSTANT + 1];
    private static final long serialVersionUID = -6928010327650772612L;
    public static Element POSITIVE_INFINITY = new Element(POSITIVE_INFINITY_DOUBLE);
    public static Element NEGATIVE_INFINITY = new Element(NEGATIVE_INFINITY_DOUBLE);
    public static final Element NAN = new Element(NaN_DOUBLE);
    public static final Fname CONSTANT_E = new Fname("\\e");
    public static final Fname CONSTANT_PI = new Fname("\\pi");
    public double value;
    // for the base int numbers
    public static int[] positiveNumbers;  // track of integers: 0,1,2,..,MaxPositiveNumber-1
    public static int MaxPositiveNumber = 256; //

    static {
        positiveNumbers = new int[MaxPositiveNumber];
        for (int j = 0; j < MaxPositiveNumber; j++) {
            positiveNumbers[j] = j;
        }
    }

    public Element() {
    }

    public Element(double value) {
        this.value = value;
    }

    public boolean isItNumber() {
        return true;
    }

    /**
     * Applicable to the F and Fname.
     *
     * @param ring
     * @return true if the sentence without variables
     */
    public boolean isItNumber(Ring ring) {
        return true;
    }

    /**
     * Метод возвращающий номер кольца переменной в списке колец
     *
     * @return номер кольца переменной в списке колец
     */
    public int numbElementType() {
        return Ring.INFTYorNAN;
    }

    /**
     * This method can do more long static track of integers "positiveNumbers"
     *
     * @param newMaxPositiveNumber -- new size of integer track
     */
    public static void newMaxPositiveNumbers(int newMaxPositiveNumber) {
        int[] temp = positiveNumbers;
        positiveNumbers = new int[newMaxPositiveNumber];
        int min = Math.min(newMaxPositiveNumber, MaxPositiveNumber);
        System.arraycopy(temp, 0, positiveNumbers, 0, min);
        for (int j = min; j < newMaxPositiveNumber; j++) {
            positiveNumbers[j] = j;
        }
        MaxPositiveNumber = newMaxPositiveNumber;
    }

    public Element one(Ring ring) {
        return null;
    }

    public Element[] posConst() {
        return null;
    }

    public Element[] negConst() {
        return null;
    }

    public Element zero(Ring ring) {
        return null;
    }

    public Element minus_one(Ring ring) {
        return null;
    }
    // for polynomials, fractions, functions...

    public Element myOne(Ring ring) {
        return null;
    }

    public Element myZero(Ring ring) {
        return null;
    }

    public Element myZero() {
        return null;
    }

    public Element conjugate(Ring ring) {
        return Re(ring).add(Im(ring).multiply(ring.numberMINUS_I, ring), ring);
    }

    public Element myMinus_one(Ring ring) {
        return null;
    }

    public Element myMinus_one() {
        return null;
    }

    public Element Re(Ring ring) {
        return this;
    }

    public Element Im(Ring ring) {
        return ring.numberZERO;
    }

    public Element add(Element x, Ring ring) {
        if (this == NAN || x == NAN) {
            return NAN;
        }
        if (x.getClass() != this.getClass()) {
            return x.add(this, ring);
        } else {
            int type = ring.algebra[0];
            switch (type) {
                case Ring.R64MaxPlus:
                case Ring.RMaxPlus:
                case Ring.ZMaxPlus:
                case Ring.R64MaxMult:
                case Ring.RMaxMult:
                case Ring.ZMaxMult:
                    return (this == POSITIVE_INFINITY || x == POSITIVE_INFINITY) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
                case Ring.R64MinPlus:
                case Ring.RMinPlus:
                case Ring.ZMinPlus:
                case Ring.R64MinMult:
                case Ring.RMinMult:
                case Ring.ZMinMult:
                    return (this == NEGATIVE_INFINITY || x == NEGATIVE_INFINITY) ? NEGATIVE_INFINITY : POSITIVE_INFINITY;
                case Ring.R64MaxMin:
                case Ring.RMaxMin:
                case Ring.ZMaxMin:
                    return (this == POSITIVE_INFINITY || x == POSITIVE_INFINITY) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
                case Ring.R64MinMax:
                case Ring.RMinMax:
                case Ring.ZMinMax:
                    return (this == NEGATIVE_INFINITY || x == NEGATIVE_INFINITY) ? NEGATIVE_INFINITY : POSITIVE_INFINITY;
                default:
                    return (this == NEGATIVE_INFINITY) ? ((x == POSITIVE_INFINITY) ? NAN : NEGATIVE_INFINITY)
                            : (x == POSITIVE_INFINITY) ? POSITIVE_INFINITY : NAN;
            }
        }
    }

    /**
     * subtraction
     *
     * @param x    -- вычитаемое
     * @param ring -- ring
     * @return
     */
    public Element subtract(Element x, Ring ring) {
        if (this == NAN || x == NAN) {
            return NAN;
        }
        if (x.getClass() != this.getClass()) {
            return this;
        } else {
            return (this == NEGATIVE_INFINITY) ? ((x == POSITIVE_INFINITY) ? NEGATIVE_INFINITY : NAN)
                    : (x == POSITIVE_INFINITY) ? NAN : POSITIVE_INFINITY;
        }
    }

    /**
     * multiplication
     *
     * @param x    -- правый сомножитель
     * @param ring -- ring
     * @return
     */
    public Element multiply(Element x, Ring ring) {
        if (x.isOne(ring)) return this;
        if (this == NAN || x == NAN) {
            return NAN;
        }
        if (x.getClass() != this.getClass()) {
            return x.multiply(this, ring);
        } else {
            int type = ring.algebra[0];
            switch (type) {
                case Ring.R64MaxPlus:
                case Ring.RMaxPlus:
                case Ring.ZMaxPlus:
                case Ring.R64MinPlus:
                case Ring.RMinPlus:
                case Ring.ZMinPlus:
                    return (this == NEGATIVE_INFINITY) ? ((x == POSITIVE_INFINITY) ? NAN : NEGATIVE_INFINITY)
                            : (x == POSITIVE_INFINITY) ? POSITIVE_INFINITY : NAN;
//            case Ring.R64MaxMult:case Ring.RMaxMult: case Ring.ZMaxMult: case Ring.R64MinMult:case Ring.RMinMult: case Ring.ZMinMult:
//                return (this== NEGATIVE_INFINITY || x== NEGATIVE_INFINITY)? NEGATIVE_INFINITY: POSITIVE_INFINITY;
                case Ring.R64MaxMin:
                case Ring.RMaxMin:
                case Ring.ZMaxMin:
                    return (this == NEGATIVE_INFINITY || x == NEGATIVE_INFINITY) ? NEGATIVE_INFINITY : POSITIVE_INFINITY;
                case Ring.R64MinMax:
                case Ring.RMinMax:
                case Ring.ZMinMax:
                    return (this == POSITIVE_INFINITY || x == POSITIVE_INFINITY) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
                default:
                    return (((this == POSITIVE_INFINITY) && (x == POSITIVE_INFINITY)) || ((this == NEGATIVE_INFINITY) && (x == NEGATIVE_INFINITY))) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
            }
        }
    }

    /**
     * division
     *
     * @param x    --делитель
     * @param ring -- ring
     * @return
     */
    public Element divide(Element x, Ring ring) {
        if (x.isOne(ring)) return this;
        if (x.isMinusOne(ring)) return this.negate(ring);
        if (this == NAN || x == NAN || x.isZero(ring)) {
            return NAN;
        }
        //    return new Fraction (this, x);
        if (x.getClass() != this.getClass()) {
            if (x.compareTo(x.valOf(0, ring), LESS, ring)) {
                return this.negate(ring);
            } else {
                return this;
            }
        } else {
            return NAN;
        }
    }

    public Element divideToFraction(Element x, Ring ring) { int ra= ring.algebra[0];

                if((ra==Ring.R64)||(ra==Ring.R)||(ra==Ring.Zp32)||(ra==Ring.Zp)||(ra==Ring.Complex)) return divide(x, ring);

                    if(x.isOne(ring))return this;
        if (x.isMinusOne(ring)) return this.negate(ring);
        if (this == NAN || x == NAN || x.isZero(ring)) {
            return NAN;
        }
        return new Fraction(this, x);
    }

    public Element maxAbs(Element val, Ring ring) {
        if (!(this.isItNumber() && val.isItNumber())){
            ring.exception.append("Not exist maxAbs for not number arguments" );
            return null;}
        Element ww= val.abs(ring);
        Element th= this.abs(ring);
        return (th.compareTo(ww) >= 0 ? th : ww);
    }

    public Element maxAbs(Ring ring) {
        ring.exception.append("Not exist maxAbs for this Element. " );
        return null;}


    public Element max(Element val, Ring ring) {
        return max(this,val, ring);
    }

//    /**
//     * Transform a fraction to proper fraction plus integer part
//     * @param ring Ring
//     * @return Integer part plus proper fraction
//     */ 
//    public Element  properForm(Ring ring) {return this;}

    public Element inverse(Ring ring) {
        // return new Element(1 / value);
        return new Fraction(ring.numberONE(), this);
    }

    /**
     * Special variants of multiplications.
     *
     * @param x       -- правый сомножитель
     * @param variant case of variant:
     *                <p>
     *                for Numbers 0: Standart method for Z (default) 1: Karatsuba's method for
     *                Z
     *                <p>
     *                for Polynomials 0: Standart for polynomials and standart for numbers Z
     *                (default) 1: Standart for polynomials and Karacuba's for numbers Z 2:
     *                Karacuba's for polynomials and standart for numbers Z 3: Karacuba's for
     *                polynomials and Karacuba's for numbers Z 4: Discreet Fourier
     *                Transform'stoString_obj
     * @param ring    --ring
     * @return
     */
    public Element multiply(Element x, int variant, Ring ring) {
        return null;
    }

    /**
     * Quotient and remainder of two polynomials or integers.
     * this Element - numerator of fraction
     *
     * @param x    Element -denominator of fraction
     * @param var  -- is a variable in polynomial
     * @param ring -- ring
     * @return [0]-quotient,[1] -remainder
     */
    public Element[] quotientAndRemainder(Element x, Element var, Ring ring) {
        Element th = this.ExpandFnameOrId();
        if (th instanceof Polynom) return ((Polynom) th).quotientAndRemainder(x, var, ring);
        if (this.isItNumber() && x.isItNumber()) return divideAndRemainder(x, ring);
        return new Element[]{ring.numberZERO, this};
    }

    //    public Element[] quotientAndRemainder(Ring ring) {
//        return new Element[] {this, ring.numberZERO};
//    }
    public Element[] quotientAndRemainder(Element x, Ring ring) {
        Element th = this.ExpandFnameOrId();
        //    if (th instanceof Polynom)return ((Polynom)th).quotientAndRemainder(x, ring);
        if (this.isItNumber() && x.isItNumber()) return th.divideAndRemainder(x, ring);
        return new Element[]{this.divide(x, ring), ring.numberZERO};
    }

    public Element[] divideAndRemainder(Element x, Ring ring) {
        Element th = this.ExpandFnameOrId();
        //   if (th instanceof Polynom)return ((Polynom)th).divideAndRemainder(x, ring);
        if (this.isItNumber() && x.isItNumber())
            return new Element[]{this.divide(x, ring), ring.numberZERO};
        return new Element[]{ring.numberZERO, this};
    }

    public Element[] divideAndRemainder(Element x, Element var, Ring ring) {
        //  Element th=this.ExpandFnameOrId();
        //  if (th instanceof Polynom)return ((Polynom)th).divideAndRemainder(x, var, ring);
        if (this.isItNumber() && x.isItNumber())
            return new Element[]{this.divide(x, ring), ring.numberZERO};
        return new Element[]{ring.numberZERO, this};
    }
//    public Element[] divAndRemToRational(Element x, Ring ring) {
//        if(this.isItNumber() && x.isItNumber()) return  new Element[]{this.divide(x, ring), ring.numberZERO};
//        return new Element[] { ring.numberZERO,this};
//    }
//    
//    public Element[] divAndRemToRational(Element x, Element var, Ring ring) {
//        if(this.isItNumber() && x.isItNumber()) return  new Element[]{this.divide(x, ring), ring.numberZERO};
//        return new Element[] { ring.numberZERO,this};
//    }   

    /**
     * GCD of two elements
     *
     * @param f    -- second argument of GCD (first element is this)
     * @param ring -- Ring
     * @return GCD(this, x) which has the form "NumberZ*Polynom*a1*a2*...*ak" in
     * the general case
     */
    public Element GCD(Element f, Ring ring) {
        int fl = numbElementType() - f.numbElementType();
        return (fl >= 0) ? f.GCD(this, ring) : GCD(f, ring);
    }

    public Element LCM(Element x, Ring ring) {
        return Ring.oneOfType(ring.algebra[0]);
    }

    /**
     * Calculates arrayLCM of given array with Elements.
     *
     * @param a    array with Elements
     * @param ring ring
     * @return arrayLCM of all elements from {@code a}; first element if
     * {@code a} contains only one element; {@code null}, if {@code a} is empty;
     */
    public static Element arrayLCM(Element[] a, Ring ring) {
        if (a.length == 0) {
            return null;
        }
        Element lcm = a[0];
        for (int i = 1; i < a.length; i++) {
            lcm = lcm.LCM(a[i], ring);
        }
        return lcm;
    }

    /**
     * В кольцах главных идеалов (полиномы одной переменной над полем или Z)
     * вычисляется GCD=НОД(this,x) и коеффициенты (a,b) в линейном разложении
     * НОД: GCD=a*this+b*x;
     *
     * @param x - второй исходный полином
     * @return result: GCD= result[0], a= result[1], b= result[2] <br> Для колец
     * полиномов K= R[x,y..][z], не являющимися кольцами главных идеалов
     * дополнительно вычисляются три элемента из кольца R[x,y..] коеффициентов
     * при старшей переменной z: A,B,C и <br> <br>
     * НОД(this,х)=((this/A)a+(x/B)b)/C в кольце R(x,y..)[z]. <br> При этом А -
     * НОД коэффициентов полинома this, B - НОД коэффициентов полинома x, С -
     * общий знаменатель всех коэффициентов в НОД(this,x) <br> <br>
     * <p>
     * return result: GCD= result[0], a= result[1], b= result[2], A= result[3],
     * B= result[4], C= result[5]
     */
    public VectorS extendedGCD(Element x, Ring ring) {
        ring.exception.append("ExtendedGCD has not correct arguments, or SPACE is not Z or Z64 or Q. ");
        return (this.isZero(ring)) ?
                (x.isZero(ring)) ? new VectorS(new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO}) :
                        new VectorS(new Element[]{ring.numberONE, ring.numberZERO(), x.inverse(ring)}) :
                new VectorS(new Element[]{ring.numberONE, this.inverse(ring), ring.numberZERO()});
    }

    public Element negate(Ring ring) {
        return new Element(-value);
    }

    public int signum() {
        return Integer.MAX_VALUE;
    }

    public String toString(Ring r) {
        if (this == null) return "null";
        String s = Double.toString(value);
        return s.replace("Infinity", "\\infty");
    }

    /**
     * Method "toString" для различных специальных печатей
     *
     * @param i
     * @param ring -- ring
     * @return
     */
    public String toString(int i, Ring ring) {
        return new String();
    }

    /**
     * Преобразовывает объект типа Element (число, полином, функцию, вектор или
     * матрицу) в строку.
     *
     * @param page -- страница тетради
     * @param i    -- дополнительный целый ключ
     * @return представление объекта типа Element в виде строки String
     */
    public String toString_obj(Page page, int i) {
        //Пустой объект имеет только имя (name!=null, все остальные = null), ringNum=-1.
        //Безымянный объект имеет name=null.
        F func = ((F) page.expr.get(i));
        StringBuilder sb = new StringBuilder();
        if (page.expr.get(i) instanceof F) {
            sb.append(((F) page.expr.get(i)).toString());
        }
        return sb.toString();
    }

    /**
     * Compare of two Elements acording with 6 compType.
     *
     * @param x
     * @param compType -3 --> (this != x)? <br> -2 --> (this < x)? <br> -1 -->
     *                 (this <= x)? <br> 0 --> (this = x)? <br> 1 --> (this >= x)? <br> 2 -->
     *                 (this > x)? <br>
     * @param ring     --ring
     * @return true -- if equality (inequality) is true; <br> false -- if
     * equality (inequality) is false; <br>
     */
    public boolean compareTo(Element x, int compType, Ring ring) {
        int i = compareTo(x, ring);
        switch (compType) {
            case NOT_EQUAL:
                return i != 0;
            case LESS:
                return i < 0;
            case LESS_OR_EQUAL:
                return i <= 0;
            case EQUAL:
                return i == 0;
            case GREATER_OR_EQUAL:
                return i >= 0;
            case GREATER:
                return i > 0;
        }
        return false;
    }

    public int compareTo(Element x, Ring ring) {
        if (isNegativeInfinity()) {
            return x.isNegativeInfinity() ? 0 : -1;
        }
        if (isPositiveInfinity()) {
            return x.isPositiveInfinity() ? 0 : 1;
        }
        return Integer.MAX_VALUE; // something like NAN
    }

    /**
     * Сравнение двух элементов - если оба равны(то есть они элементы) то
     * проверяем на плюс\минус бесконечность и NAN, возвращая -1, если первая
     * стоит минус бксконечность; 1, если на первом месте стоит плюс
     * бесконечность и 0, если они равны; также прооверям на NAN - если хоть
     * один из них NAN, возвращаем Integer.MAX_VALUE
     *
     * @param x
     * @return
     */
    @Override
    public int compareTo(Element x) {
        int n1 = this.numbElementType();
        int n2 = x.numbElementType();
        if (n1 == n2) {
            if ((x == NAN) || (this == NAN)) return Integer.MAX_VALUE;
            if (x == POSITIVE_INFINITY) return (this == POSITIVE_INFINITY) ? 0 : -1;
            if (this == POSITIVE_INFINITY) return 1;
            if (x == NEGATIVE_INFINITY) return (this == NEGATIVE_INFINITY) ? 0 : 1;
            if (this == NEGATIVE_INFINITY) return -1;
        }
        return -x.compareTo(this);
    }

    /**
     * Максимальный из двух элементов
     *
     * @param a - one element
     * @param b - other element
     * @return max(a, b) or NaN
     */
    public Element max(Element a, Element b, Ring ring) {
        if (a.isItNumber() && b.isItNumber())
            return a.compareTo(b, ring) >= 0 ? a : b;
        else return (new F(F.MAX, a, b)).valueOf(ring);
    }

    /**
     * Минимальный из двух элементов
     *
     * @param a - one element
     * @param b - other element
     * @return min(a, b) or NaN
     */
    public Element min(Element a, Element b, Ring ring) {
        if (a.isItNumber() && b.isItNumber())
            return a.compareTo(b, ring) >= 0 ? b : a;
        else return (new F(F.MIN, a, b)).valueOf(ring);
    }

    /**
     * Is it a zero Element? For rounded numbers we consider that (this==0) iff
     * abs(this)< eps.
     *
     * @param ring --ring
     * @return true if this==0, false if this!=0.
     */
    public Boolean isZero(Ring ring) {if(this==null)return true;
        int type = ring.algebra[0];
        switch (type) {
            case Ring.R64MaxPlus:
            case Ring.RMaxPlus:
            case Ring.ZMaxPlus:
            case Ring.R64MinPlus:
            case Ring.RMinPlus:
            case Ring.ZMinPlus:
            case Ring.R64MinMult:
            case Ring.RMinMult:
            case Ring.ZMinMult:
            case Ring.R64MaxMin:
            case Ring.RMaxMin:
            case Ring.ZMaxMin:
            case Ring.R64MinMax:
            case Ring.RMinMax:
            case Ring.ZMinMax:
                return this == ring.numberZERO;
            default:
                return false;
        }
    }

    public Boolean isMinusOne(Ring ring) {
        return false;
    }

    public Boolean isOne(Ring ring) {
        return false;
    }

    public Boolean isMinusI(Ring ring) {
        return false;
    }

    public Boolean isI(Ring ring) {
        return false;
    }

    public Boolean isNegative() {
        return value == NEGATIVE_INFINITY.value;
    }

    public boolean equals(Element x, Ring r) {
        return false;
    }

    /**
     * Производная по переменной num
     */
    public Element D(int num, Ring r) {
        return null;
    }

    /**
     * Производная по первой переменной
     */
    public Element D(Ring r) {
        return null;
    }

    public Element mixedDerivativ(Polynom num, Ring r) {
        Element res = this;
        for (int i = 0; i < num.powers.length; i++) {
            if (num.powers[i] != 0) {
                for (int j = 0; j < num.powers[i]; j++) {
                    res = (res instanceof Factor) ? (((Factor) res).toF(r)).D(i, r) : res.D(i, r);
                    if (res.isZero(r)) return r.numberZERO;
                }
            }
        }
        return res;
    }

    /**
     * Интеграл по переменной num
     */
    public Element integrate(int num, Ring r) {
        return null;
    }

    /**
     * Интеграл по первой переменной
     */
    public Element integrate(Ring r) {
        return null;
    }

    /**
     * Все числа, которые есть в коэффициентах elementa отображаются в интервал
     * -m/2.. +m/2/.
     *
     * @param m -- модуль, по которому берутся все числа
     * @param r -- ring
     * @return
     */
    public Element Mod(Element m, Ring r) {
        return null;
    } // move to -m/2.. +m/2

    /**
     * Все числа, которые есть в коэффициентах elementa отображаются в интервал
     * 0..m-1 Для числовых полей вычисляется число this - (m * floor(this/m))
     *
     * @param m -- модуль
     * @param r -- Ring
     * @return -- this mod ring.MOD
     */
    public Element mod(Element m, Ring r) {
        return null;
    } // move to 0..m-1

    public Element mod(Ring r) {
        return null;
    } // for NumberZp, NumberZp32

    public Element Mod(Ring r) {
        return null;
    } // for NumberZp, NumberZp32

    public Element random(int[] randomType, Random rnd, Ring ring) {
        return null;
    }

    public int intValue() {
        return (int) longValue();
    }

    public long longValue() {
        return (long) Double.NaN;
    }

    public double doubleValue() {
        return value;
    }

    /**
     * Transformation of array of Elements to array of integers
     *
     * @param arr array of Element
     * @return array of integers
     */
    public static int[] toIntArray(Element[] arr) {
        int k = arr.length;
        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            res[i] = arr[i].intValue();
        }
        return res;
    }

    public Element toNumber(int numberType, Ring ring) {
        return (isItNumber()) ? this : NAN;
        // return this;
    }

//    public Element toNumber(Ring ring) {
//        return this;
//    }

    public Element toPolynomial(int newType, Ring ring) {
        return this;
    }

    public Element toPolynomial(Element oneOfNewPCoeff, Ring ring) {
        return this;
    }

    public Element toNewRing(int Algebra, Ring r) {
        return this;
    }

    public Element toRational(Element oneOfNewPCoeff, Ring r) {
        return this;
    }

    public Element toFunction(Element oneOfNewPCoeff, Ring r) {
        return this;
    }

    public Element toMatrix(Element oneOfNewPCoeff, Ring r) {
        return this;
    }

    public Element toVector(Element oneOfNewPCoeff, Ring r) {
        return null;
    }

    public Element valOf(int x, Ring ring) {
        return valOf((long) x, ring);
    }

    public Element valOf(long x, Ring ring) {
        return null;
    }

    public Element valOf(double x, Ring ring) {
        return null;
    }

    public Element valOf(String x, Ring ring) {
        return null;
    }

    /**
     * Возведение в степень n по бинарному алгоритму
     *
     * @param n -- целая степень
     * @param r -- ring
     * @return элемент в степени n
     */
    public Element pow(int n, Ring r) {
        int m = (n < 0) ? -n : n;
        Element one = r.numberONE;
        if (one instanceof Fraction) one = ((Fraction) one).denom;
        Element res = one;
        if (n == 0) {
            return one;
        }
        if (this.isZero(r) || this.isOne(r)) return this;
        if ((this instanceof Polynom) && ((Polynom) this).coeffs.length == 1) {
            Polynom pp = ((Polynom) this);
            int[] npow = new int[pp.powers.length];
            Element[] cof = new Element[]{pp.coeffs[0].pow(n, r)};
            for (int i = 0; i < pp.powers.length; i++) npow[i] = m * pp.powers[i];
            res = new Polynom(npow, cof);
        } else {
            if (this.isMinusOne(r)) return (((n & 1) == 1) ? this : res);
            Element temp = this;
            if ((m & 1) == 1) res = temp;
            m >>>= 1;
            while (m != 0) {
                temp = (temp.multiply(temp, r));
                if ((m & 1) == 1) res = (res.multiply(temp, r));
                m >>>= 1;
            }
        }
        return (n < 0) ? new Fraction(one, res) : res;
    }

    /**
     * Вычисление d значений корня степени d
     *
     * @param d    -- степень корня (2 -квадратный, 3 - кубический и т.д.)
     * @param ring -- ring
     * @return Element[] -- d значений корня
     */
    public Element[] root(int d, Ring ring) {
        return null;
    }

    /**
     * Вычисление den значений дробной степрени num/den
     *
     * @param num  числитель показателя степени
     * @param den  знаменатель показателя степени
     * @param ring ring
     * @return Element[] -- den значений дробной степени
     */
    public Element[] pow(int num, int den, Ring ring) {
        return null;
    }

    /**
     * Вычисляет первое значение дробной степени num/den.
     *
     * @param num  числитель показателя степени
     * @param den  знаменатель показателя степени
     * @param ring ring
     * @return Element -- den значений дробной степени
     */
    public Element powTheFirst(int num, int den, Ring ring) {
        return null;
    }

    /**
     * This in the power n.  General case of two Elements: this and n.
     *
     * @return this^n.
     * @n - power
     */
    public Element pow(Element n, Ring ring) {
        int thisType = this.numbElementType();
        int nType = n.numbElementType();
        int t = thisType - nType;
        if (nType == Ring.Q) {
            return powFraction(n, ring);
        } else if (thisType < Ring.NumberOfSimpleTypes && nType < Ring.NumberOfSimpleTypes) {
            Double res = Math.pow(doubleValue(), n.doubleValue());
            return (t < 0) ? n.valOf(res, ring) : valOf(res, ring);
        }
        if ((nType <= Ring.Z) || (n.isItNumber() && n.round(ring).equals(n, ring)))
            return pow(n.intValue(), ring);
        return new F(F.POW, new Element[]{this, n});
    } // нужно доделать для Fraction   (1/5)^(3/4) и  Complex  (1+i)^(2+i) !!!

    /**
     * @param powerFraction Fraction
     * @param ring
     * @return this^power
     */
    private Element powFraction(Element powerFraction, Ring ring) {
        Fraction power = (Fraction) powerFraction;
        Element numPow = power.num;
        Element denomPow = power.denom;
        if (power.isZero(ring)) return ring.numberONE();
        else if (power.isOne(ring)) return this;
        Element result = numPow.isOne(ring) ? this : new F(F.POW, new Element[]{this, numPow.abs(ring)});
        result = denomPow.isOne(ring) ? result : new F(F.ROOTOF, new Element[]{result, denomPow.abs(ring)});
        if (result instanceof F) {
            result = ((F) result).valueOf(ring);
        }
        return power.isNegative() ? new Fraction(NumberZ.ONE, result) : result;
    }

    /**
     * Вычисляет 1-ое значение корня n-ой степени.
     *
     * @param n    степень корня (2 - квадратный, 3 - кубический)
     * @param ring --ring
     * @return 1-е значение корня n-ой степени
     */
    public Element rootTheFirst(int n, Ring ring) {
        return null;
    }

    /**
     * Calculates GCD of all the elements from given array with Elements.
     *
     * @param a    array with Elements.
     * @param ring ring
     * @return GCD of all the elements from {@code a}.
     */
    public static Element arrayGCD(Element[] a, Ring ring) {
        int i = 0;
        for (; i < a.length; i++) {
            if (!a[i].isZero(ring)) {
                break;
            }
        }
        if (i == a.length) return NumberZ.ZERO;
        Element one = a[i].myOne(ring);
        Element minus_one = a[i].minus_one(ring);
        if (a[i].equals(one) || (a[i].equals(minus_one))) {
            return one;
        }
        Element gcd = a[i];
        for (; i < a.length; i++) {
            if (!a[i].isZero(ring)) {
                gcd = gcd.GCD(a[i], ring);
                if (gcd.equals(one) || gcd.equals(minus_one)) {
                    return one;
                }
            }
        }
        return gcd;
    }

    /**
     * Cancels given array of Elements by an Element  which is the GCD of all elements in array.
     *
     * @param a cancelled array with Elements
     * @param r ring
     * @return {@code true} if array was canceled, {@code false} otherwise.
     */
    public static boolean arrayCancel(Element[] a, Ring r) {
        Element gcd = arrayGCD(a, r);
        Element one = a[0].myOne(r);
        if (gcd.equals(one)) return false;
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i].divide(gcd, r);
        }
        return true;
    }

    public boolean isNaN() {
        return (this == NAN);
    }

    public boolean isNegativeInfinity() {
        return value == NEGATIVE_INFINITY.value;
    }

    public boolean isPositiveInfinity() {
        return value == POSITIVE_INFINITY.value;
    }

    public boolean isInfinite() {
        return value == POSITIVE_INFINITY.value || value == NEGATIVE_INFINITY.value;
    }

    public boolean isFnameOrFType() {
        return ((this instanceof F) || (this instanceof Fname));
    }

    public Element e(Ring r) {
        return new Fname("\\e");
    }

    public Element pi(Ring r) {
        return new Fname("\\pi");
    }

    public Element arg(Ring r) {
        int s = signum();
        return (s > 0) ? r.numberZERO : (s < 0) ? new Fname("\\pi") : NAN;
    }

    public Element abs(Ring r) {
        return (value == POSITIVE_INFINITY_DOUBLE || value == NEGATIVE_INFINITY_DOUBLE) ?
                POSITIVE_INFINITY : (value == NaN_DOUBLE) ? NAN : new F(F.ABS, this);
    }

    /**
     * The square of module of this complex number
     * OR square for all the other numbers
     *
     * @return re^2 + im^2 OR this^2
     */
    public Element absSquare(Ring ring) {
        return Re(ring).multiply(Re(ring), ring)
                .add(Im(ring).multiply(Im(ring), ring), ring);
    }

    public Element id(Ring r) {
        return this;
    }

    public Element ln(Ring r) {

        if (this == POSITIVE_INFINITY) return this;
        if (this == NEGATIVE_INFINITY)
            return (r.isComplex()) ? new Complex(NEGATIVE_INFINITY, r.numberZERO) : NAN;
        return (this == NAN) ? NAN : new F(F.LN, this);
    }

    public Element log(Element base, Ring r) {
        Element ee = this.ln(r);
        Element dd = base.ln(r);
        if ((ee == null) || (ee == NAN)) {
            ee = new F(F.LN, new Element[]{this});
        }
        if ((dd == null) || (dd == NAN)) {
            dd = new F(F.LN, new Element[]{base});
        }
        return ee.divide(dd, r);
    }

    public Element lg(Ring r) {
        return log(r.posConst[10], r);

    }

    public Element exp(Ring r) {
        if (this == POSITIVE_INFINITY) return this;
        if (this == NEGATIVE_INFINITY) return r.numberZERO;
        return (this == NAN) ? NAN : new F(F.EXP, this);
    }

    public Element sqrt(Ring r) {
        if (this == POSITIVE_INFINITY) return this;
        if (this == NEGATIVE_INFINITY)
            return ((r.algebra[0] & Ring.Complex) == Ring.Complex) ? new Complex(r.numberZERO, POSITIVE_INFINITY) : NAN;
        return (this == NAN) ? NAN : new F(F.SQRT, this);
    }

    public Element sin(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.SIN, this);
    }

    public Element cos(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.COS, this);
    }

    public Element tan(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.TG, this);
    }

    public Element ctg(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.CTG, this);
    }

    public Element arcsn(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.ARCSIN, this);
    }

    public Element arccs(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.ARCCOS, this);
    }

    public Element arctn(Ring r) {
        if (this == NEGATIVE_INFINITY)
            return (r.RADIAN == FALSE) ? r.number90.negate(r) : pi(r).divide(r.posConst[2], r).negate(r);
        if (this == POSITIVE_INFINITY)
            return (r.RADIAN == FALSE) ? r.number90 : pi(r).divide(r.posConst[2], r);
        return (this == NAN) ? NAN : new F(F.ARCTG, this);
    }

    public Element arcctn(Ring r) {
        if (this == POSITIVE_INFINITY) return r.numberZERO;
        if (this == NEGATIVE_INFINITY) return (r.RADIAN == FALSE) ? r.number180 : pi(r);
        return (this == NAN) ? NAN : new F(F.ARCCTG, this);
    }

    public Element sh(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.SH, this);
    }

    public Element ch(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.CH, this);
    }

    public Element th(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.TH, this);
    }

    public Element cth(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.CTH, this);
    }

    public Element arsh(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.ARCSH, this);
    }

    public Element arch(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.ARCCH, this);
    }

    public Element arth(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.ARCTGH, this);
    }

    public Element arcth(Ring r) {
        if ((this == POSITIVE_INFINITY) || (this == NEGATIVE_INFINITY) || (this == NAN)) return NAN;
        return new F(F.ARCCTGH, this);
    }

    public Element unitstep(Ring r) {
        return null;
    }

    public Element unitstep(Element a, Ring r) {
        return null;
    }

    public Element value(Element[] var, Ring r) {
        return this;
    }

    public Element value(Page page, Ring ring) {
        return this;
    }

    public Element valueOf(Ring ring) {
        return this;
    }

    public static Element binomial(Ring ring, int n, int k) {
        switch (ring.algebra[0]) {
            case Ring.R64:
            case Ring.C64:
                growPasTrgR64(n);
                return new NumberR64(binomialR64(n, k));
            default:
                growPasTrgZ(n);
                return binomialZ(n, k);
        }
    }

    /**
     * Как бы его по-человечески написать Можно \binomial использовать
     *
     * @param ring
     * @param n
     * @return
     */
    public static Element factorial(Ring ring, int n) {
        switch (ring.algebra[0]) {
            case Ring.R64:
            case Ring.C64:
                double current = n;
                double res = 1.0;
                for (double i = 1; i <= current; i++) {
                    res *= i;
                }
                return new NumberR64(res);
            default:
                NumberZ resZ = NumberZ.ONE;
                for (int i = 1; i <= n; i++) {
                    resZ = resZ.multiply(new NumberZ(i));
                }
                return resZ;
        }
    }

    public Element factorial(Ring r) {
        return null;
    }

    public Element gamma(Ring r) {
        return null; // new NumberR64(new specFuncs().gamma(doubleValue()));
    }

    public Element beta(Element a, Ring r) {
        return null; // new NumberR64(new specFuncs().beta(doubleValue(), a.doubleValue()));
    }

    /**
     * Поднимает последний элемент во вложенных Fname или F с именами ID, либо
     * их комбинациях. Дополнительно возвращает в параметре NumberZ64
     * nonCommutate значение 1, если найдено имя Fname, которое начинается с
     * большой буквы.
     *
     * @param nonCommutate - Признак некоммутативности элемента. При обращении к
     *                     методу нужно установить "NumberZ64 nonCommutate=new NumberZ64(0)", нельзя
     *                     использовать NumberZ64.ZERO.
     *                     <p>
     *                     Если в цепочке обнаруживается Fname, который имеет имя начинающееся с
     *                     большой буквы, то будет возвращено значение "1". В противном случае
     *                     значение останется "0"
     * @return Element, without 1) root F.ID and 2) root Fname with X[0]!=null
     * 3) nonCommutate=1 iff nonCommutative case
     */
    public Element ExpandFnameOrId(NumberZ64 nonCommutate) {
        if (this instanceof F) {
            if (((F) this).name == F.ID) {
                return ((F) this).X[0].ExpandFnameOrId(nonCommutate);
            }
        } else {
            if (this instanceof Fname) {
                Fname g = (Fname) this;
                if (Character.isUpperCase(g.name.charAt(0))) {
                    nonCommutate.value = 1L;
                }
                return (g.X == null) ? g : g.X[0].ExpandFnameOrId(nonCommutate);
            } else {
                if (this instanceof MatrixS || this instanceof VectorS
                        || this instanceof MatrixD) {
                    //nonCommutate.value = 1L;
                }
            }
        }
        return this;
    }

    /**
     * Поднимает последний элемент во вложенных Fname или F с именами ID, либо
     * их комбинациях.
     *
     * @return Element, without 1) root F.ID and 2) root Fname with X[0] != null
     */
    public Element ExpandFnameOrId() {
        if (this instanceof F) {
            F thisF = (F) this;
            if (thisF.name == F.ID) {
                return ((F) this).X[0].ExpandFnameOrId();
            } else {
                for (int i = 0; i < thisF.X.length; i++) {
                    thisF.X[i] = thisF.X[i].ExpandFnameOrId();
                }
            }
        } else if (this instanceof Fname) {
            Fname g = (Fname) this;
            return (g.X != null && g.X.length > 0 && g.X[0] != null) ? g.X[0].ExpandFnameOrId() : g;
        }
        return this;
    }

    public Element ExpandId() {
        if (this instanceof F) {
            F thisF = (F) this;
            if (thisF.name == F.ID) {
                return ((F) this).X[0].ExpandId();
            } else {
                for (int i = 0; i < thisF.X.length; i++)
                    thisF.X[i] = thisF.X[i].ExpandId();
            }
        } else if (this instanceof Fname) {
            Fname g = (Fname) this;
            if (g.X != null && g.X.length > 0 && g.X[0] != null)
                g.X[0] = g.X[0].ExpandId();
        }
        return this;
    }

    @Override
    public Object clone() {
        return null;
    }

    public Element rootOf(int pow, Ring r) {
        return new F(F.ROOTOF, this, new NumberZ(pow));
    }

    public Element myPow(Element n, Ring ring) {
        int This = this.numbElementType();
        int X = n.numbElementType();
        int T = This - X;
        if (This < 15 && X < 15) {
            Double res = Math.pow(doubleValue(), n.doubleValue());
            return new NumberR64(res);
        }
        return pow(n.intValue(), ring);
    }

    public Element factorLnExp(Ring ring) {
        return this;
    }

    public Element expandLn(Ring ring) {
        return this;
    }

    public Element multiplyPar(Element el, Ring r) {
        return null;
    }

    public Element adjointPar(Ring r) {
        return null;
    }

    public Element echelonFormPar(Ring r) {
        return null;
    }

    public Element detPar(Ring r) {
        return null;
    }

    public Element kernelPar(Ring r) {
        return null;
    }

    public Element charPolPar(Ring r) {
        return null;
    }

    public Element adjointDetPar(Ring r) {
        return null;
    }

    public Element gbasisPar(Element[] v, Ring r) {
        return null;
    }

    /**
     * Checks if given Collections of Elements c1 and c2 are fully equal using
     * equals(Element, ring) method.
     *
     * @param ring ring.
     * @param c1   first collection of Elements.
     * @param c2   second collection of Elements.
     * @return true if {@code c1} equals {@code c2} w.r.t. {@code ring}.
     */
    public static boolean equals(Ring ring, Collection<? extends Element> c1,
                                 Collection<? extends Element> c2) {
        if (c1.size() != c2.size()) {
            return false;
        }
        for (Iterator<? extends Element> i1 = c1.iterator(), i2 = c2.iterator(); i1.hasNext(); ) {
            Element p1 = i1.next();
            Element p2 = i2.next();
            if (!p1.equals(p2, ring)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns string representation of given collection of Elements.
     * Element-specific toString(Ring) is used.
     *
     * @param ring ring.
     * @param c    collection of Elements.
     * @return string form of {@code c}.
     */
    public static String colToStr(Ring ring, Collection<? extends Element> c) {
        if (c.isEmpty()) {
            return "[]";
        }
        StringBuilder res = new StringBuilder("[ \n");
        for (Element el : c) {
            res.append("    ").append(el.toString(ring)).append(",\n");
        }
        // add closing ']'
        res.setCharAt(res.length() - 2, '\n');
        // remove trailing ',' character
        res.setCharAt(res.length() - 1, ']');

        return res.toString();
    }

    /**
     * Returns a string representation of given map with Elements.
     * Element-specific toString(Ring) is used.
     *
     * @param ring ring.
     * @param m    map with Elements.
     * @return string for of m.
     */
    public static String mapToStr(Ring ring, Map<?, ?> m) {
        Iterator<?> i = m.entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (; ; ) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) i.next();
            Object key = e.getKey();
            Object value = e.getValue();
            sb.append(key instanceof Element ? ((Element) key).toString(ring) : key);
            sb.append('=');
            sb.append(value instanceof Element ? ((Element) value).toString(ring) : value);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }

    public Element round(Ring ring) {
        return this;
    }

    public Element ceil(Ring ring) {
        return this;
    }

    public Element floor(Ring ring) {
        return this;
    }

    public Element num(Ring ring) {
        return this;
    }

    public Element intersection(Element arg, Ring ring) {
        return new F(F.CAP, this, arg);
    }

    public Element union(Element a, Ring ring) {
        return new F(F.CUP, this, a);
    }

    public Element symmetricDifferece(Element arg, Ring ring) {
        return new F(F.SYMMETRIC_DIFFERENCE, this, arg);
    }

    public Element setTheoreticDifference(Element a, Ring ring) {
        return new F(F.SET_MINUS, this, a);
    }

    public Element complement(Ring ring) {
        return new F(F.COMPLEMENT, this);
    }


    public Element denom(Ring ring) {
        return ring.numberONE;
    }

    public boolean isEven() {
        return false;
    }

    public Element factor(Ring ring) {
        return this;
    }

    public Element expand(Ring ring) {
        return this;
    }

    //    // for functions special case with key: flag of Making New Vects of Els and Funks
//    public Element expand(Ring ring, boolean flagMakeNewVect) {
//        return expand(ring );
//    }
    public Element simplify(Ring ring) {
        return this;
    }

    /**
     * Метод осуществляющий факторизацию транцендентных функций(экспоненциальные
     * , логорифмические и тригонометрические преобразования) только на 1 уровне дерева-функции
     *
     * @param ring - текущее кольцо
     * @return
     */
    public Element Factor(boolean doNewVector, Ring ring) {
        return this;
    }

    /**
     * Метод осуществляющий факторизацию транцендентных функций(экспоненциальные
     * , логорифмические и тригонометрические преобразования) по всем уровням дерева-функции
     *
     * @param ring - текущее кольцо
     * @return
     */
    public Element FACTOR(Ring ring) {
        return this;
    }


    /**
     * Метод осуществляющий раскрытие транцендентных функций(экспоненциальные ,
     * логорифмические и тригонометрические преобразования) на верхнем уровне
     * (ExpandLog  and then ExpandTrig)
     *
     * @param ring - текущее кольцо
     * @return
     */
    public Element Expand(Ring ring) {
        return this;
    }

    /**
     * Метод осуществляющий раскрытие транцендентных функций(экспоненциальные ,
     * логорифмические и тригонометрические преобразования) на верхнем уровне
     * (expand, then ExpandLog  and then ExpandTrig)
     * We get the shortest String result among all these combinations.
     *
     * @param ring - текущее кольцо
     * @return shortest functions in symbolic form
     */
    public Element Simplify(Ring ring) {
        return this;
    }

    public Element ExpandLog(Ring ring) {
        return this;
    }

    public Element ExpandTrig(Ring ring) {
        return this;
    }

    /**
     * Метод осуществляющий раскрытие транцендентных функций(экспоненциальные ,
     * логорифмические и тригонометрические преобразования) по всему дереву функции
     *
     * @param ring - текущее кольцо
     * @return
     */
    public Element EXPAND(Ring ring) {
        return this;
    }


//    /**
//     * Метод являющийся некой комбинацией поочередного применения методов Expand
//     * и Factor.
//     */
//    public Element Simplify(Ring ring) {
//        return this;
//    }

    /**
     * Return n points at the unit circle starting from -pi with equal distances.
     *
     * @param n - points number
     * @return
     */
    public static MatrixD circl(int n) {
        Element[] x = new Element[n];
        Element[] y = new Element[n];
        for (int i = 0; i < n; i++) {
            x[i] = new NumberR64(Math.cos((2 * Math.PI * i / n) - Math.PI));
            y[i] = new NumberR64(Math.sin((2 * Math.PI * i / n) - Math.PI));
        }
        Element[][] M = new Element[2][];
        M[0] = x;
        M[1] = y;
        return new MatrixD(M);
    }

    public boolean isComplex(Ring ring) {
        return false;
    }


    /**
     * Вычисляет число 1 + x + x^2 + ...
     *
     * @param ring
     * @return
     */
    public Element closure(Ring ring) {
        switch (ring.algebra[0]) {
            case Ring.ZMaxPlus:
            case Ring.RMaxPlus:
            case Ring.R64MaxPlus:
            case Ring.ZMaxMult:
            case Ring.RMaxMult:
            case Ring.R64MaxMult:
                return (this == NEGATIVE_INFINITY) ? ring.numberONE : this;
            case Ring.ZMinPlus:
            case Ring.RMinPlus:
            case Ring.R64MinPlus:
            case Ring.RMinMult:
            case Ring.R64MinMult:
                return (this == POSITIVE_INFINITY) ? ring.numberONE : this;
            case Ring.ZMaxMin:
            case Ring.RMaxMin:
            case Ring.R64MaxMin:
            case Ring.ZMinMax:
            case Ring.RMinMax:
            case Ring.R64MinMax:
                return (this == NAN) ? NAN : ring.numberONE;
            default:
                return this;
        }
    }

    public Element divideExact(Element el, Ring ring) {
        return divide(el, ring);
    }

    /**
     * Замена порядка переменных во всех полиномах этого элемента
     *
     * @param varsMap - правило замены порядка переменных
     * @param flag    - куда/откуда
     * @param ring    - Ring
     * @return - результат замены порядка переменныхво всех полиномах
     */
    public Element changeOrderOfVars(int[] varsMap, boolean flag, Ring ring) {
        return this;
    }

    /**
     * Transform to composition of ln and exp functions with complex arguments
     *
     * @param ring Ring
     * @return
     */
    public Element toComplex(Ring ring) {
        return this;
    }

    /**
     * Logical negate.
     *
     * @param ring
     * @return
     */
    public Element B_NOT(Ring ring) {
        return new F(F.B_NOT, this);
    }

    /**
     * Logical AND
     *
     * @param B
     * @param ring
     * @return
     */
    public Element B_AND(Element B, Ring ring) {
        return new F(F.B_AND, new Element[]{this, B});
    }

    /**
     * Logical OR
     *
     * @param B
     * @param ring
     * @return
     */
    public Element B_OR(Element B, Ring ring) {
        return new F(F.B_OR, new Element[]{this, B});
    }


    public Element toNewRing(Ring r) {
        return toNewRing(r.algebra[0], r);
    }

//    /** Degree of the highest variable of this polynomial
//     *
//     * @param r
//     * @return
//     */
//    public Element degree() {return NumberZ.ZERO; }
//
//    
//        /** degree of the highest variable of this polynomial
//     *
//     * @param r
//     * @return
//     */
//    public Element degrees(Ring r) {
//        return new VectorS(NumberZ.ZERO);
//    }
}
