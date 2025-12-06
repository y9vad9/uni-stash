package com.mathpar.number;

import java.util.Random;
import com.mathpar.polynom.Polynom;

/**
 * <p>Title: ParCA - Parallel Computer Algebra</p> <p>Description: Класс Complex
 * -- это класс комплексных чисел. Действительная и мнимая части являются
 * числами типа Element. <p>Copyright: Copyright (c) 2005-2009</p> <p>Company:
 * ParCA Tambov</p>
 *
 * Для комплексных чисел введен следующих порядок (см. метод complareTo()):
 * числа сравниваются сначала по модулю, а потом на одной окружности (от -pi до
 * +pi). Поэтому числа, например, (-1 + \sigma i) и (-1 - \sigma i) обязаны
 * различаться при любом сколь угодно малом \sigma, так как между ними лежит
 * целый круг. С другой стороны по отношению к machinEpsilon они совпадают. И об
 * этом факте свидетельствует метод equals().
 *
 * @version 3.0
 */
public class Complex extends Element {
    public static final long serialVersionUID = 1L;
    /**
     * Действительная часть комплексного числа типа BigDecimal
     */
    public Element re;
    /**
     * Мнимая часть комплексного числа типа BigDecimal
     */
    public Element im;
    public static final Complex C64_ONE = new Complex(1.0);
    public static final Complex C64_ZERO = new Complex(0.0);
    public static final Complex C_ONE = new Complex(NumberR.ONE, NumberR.ZERO);
    public static final Complex C_ZERO = new Complex(NumberR.ZERO, NumberR.ZERO);
    public static final Complex CZ_ONE = new Complex(NumberZ.ONE, NumberZ.ZERO);
    public static final Complex CZ_ZERO = new Complex(NumberZ.ZERO, NumberZ.ZERO);
    public static final Complex C_I = new Complex(NumberR.ZERO, NumberR.ONE);
    public static final Complex C_MINUS_I = new Complex(NumberR.ZERO, NumberR.MINUS_ONE);
    public static final Complex CZ_I = new Complex(NumberZ.ZERO, NumberZ.ONE);
    public static final Complex CZ_MINUS_I = new Complex(NumberZ.ZERO, NumberZ.MINUS_ONE);

    public Complex() {
    }

    /**
     * Конструктор, комплексифицирует два элемента Re и Im
     *
     * @param Re -- real part
     * @param Im -- imagin part
     */
    public Complex(Element Re, Element Im) {
        re = Re;
        im = Im;
    }

    public Complex(Element Re, Element Im, Ring ring) {
        if ((Re.numbElementType() & Ring.Complex) == Ring.Complex &
                (Im.numbElementType() & Ring.Complex) == Ring.Complex) {
            if (((Complex) Re).im.isZero(ring) && ((Complex) Im).im.isZero(ring)) {
                re = ((Complex) Re).re;
                im = ((Complex) Im).re;
                return;
            }
        }
        if (((Re.numbElementType() & Ring.Complex) == Ring.Complex)
                | (Im.numbElementType() & Ring.Complex) == Ring.Complex) {
            Element TempThis = Re.add(Im.multiply(ring.numberI, ring), ring);
            if ((TempThis.numbElementType() & Ring.Complex) == Ring.Complex) {
                re = ((Complex) TempThis).re;
                im = ((Complex) TempThis).im;
                return;
            }
        }
        re = Re;
        im = Im;
    }

    /**
     * Создает комплексное число число с действительной частью Re.
     * Или разбрасывает полином на 2 полинома: P--> [Re(P), Im(P)]
     */
    public Complex(Element Re, Ring ring) {
        if (Re instanceof Polynom) {
            re = ReImParts(Re, ring)[0];
            im = ReImParts(Re, ring)[1];
        } else {
            re = Re;
            im = Re.myZero(ring);
        }
    }

    /**
     * Комплексифицирует два элемента Re и Im если они одного типа и
     * checkArgument=true. Если разные типы, то вернет {null, null}.
     *
     * @param Re -- real part
     * @param Im -- imagin part
     * @param checkArgument -- (if false then do not check)
     */
    public Complex(Element Re, Element Im, boolean checkArgument) {
        if ((!checkArgument) || (Re.numbElementType() == Im.numbElementType())) {
            re = Re;
            im = Im;
        }
    }

    /**
     * Создает комплексное число над NumberZ.
     */
    public Complex(long r, long i) {
        re = NumberZ.valueOf(r);
        im = NumberZ.valueOf(i);
    }

    /**
     * Создает комплексное число над NumberZ.
     */
    public Complex(int r, int i) {
        re = NumberZ.valueOf(r);
        im = NumberZ.valueOf(i);
    }

    /**
     * Создает комплексное число над NumberZ
     */
    public Complex(int r) {
        re = NumberZ.valueOf(r);
        im = NumberZ.ZERO;
    }

    /**
     * Создает комплексное число над NumberZ
     */
    public Complex(double r) {
        re = new NumberR64(r);
        im = NumberR64.ZERO;
    }

    /**
     * Создает комплексное число над NumberZ
     */
    public Complex(long r) {
        re = NumberZ.valueOf(r);
        im = NumberZ.ZERO;
    }

    /**
     * Создает комплексное число над NumberR64
     */
    public Complex(double r, double i) {
        re = new NumberR64(r);
        im = new NumberR64(i);
    }

    /**
     * Конструктор, комплексифицирует два полинома из строковых выражений и
     * checkArgument=true
     *
     * @param r -- real part
     * @param i -- imagin part
     */
    public Complex(String r, String i, Ring ring) {
        re = new Polynom(r, ring);
        im = new Polynom(i, ring);
    }

    @Override
    public Complex myZero(Ring ring) {
        return new Complex(re.myZero(ring), re.myZero(ring));
    }

    @Override
    public Complex myMinus_one(Ring ring) {
        return new Complex(re.myMinus_one(ring), re.myZero(ring));
    }

    @Override
    public Complex myOne(Ring ring) {
        return new Complex(re.myOne(ring), re.myZero(ring));
    }

    @Override
    public Element one(Ring ring) {
        return re.one(ring);
    }

    @Override
    public Element zero(Ring ring) {
        return re.zero(ring);
    }

    @Override
    public Element minus_one(Ring ring) {
        return re.minus_one(ring);
    }

    @Override
    public Element Re(Ring ring){
        return re;
    }

    @Override
    public Element Im(Ring ring){
        return im;
    }
    /**
     * Создает комплексное число над NumberR.
     */
    public Complex ComplexR(double r, double i) {
        return new Complex(new NumberR(r), new NumberR(i));
    }

    public static Element[] ReImParts(Element e, Ring ring) {
        if (e instanceof Polynom) {
            return ReImPartsOfPolynom((Polynom) e, ring);
        }
        return null;
    }

    public static Element[] ReImPartsOfPolynom(Polynom p, Ring ring){
        int l=p.coeffs.length;
    Element[] re = new Element[l];
    Element[] im = new Element[l];
        for(int i = 0; i < p.coeffs.length; i++){
                re[i] = (p.coeffs[i] instanceof Complex)?((Complex)p.coeffs[i]).re:p.coeffs[i];
                im[i] = (p.coeffs[i] instanceof Complex)?((Complex)p.coeffs[i]).im:ring.numberZERO;
        }
    Polynom Re =new Polynom(p.powers,re).deleteZeroCoeff(ring);
    Polynom Im =new Polynom(p.powers,im).deleteZeroCoeff(ring);
        return new Element[]{Re,Im};
    }

    /**
     * Возвращает случайное комплексное число над с параметрами randomType
     *
     * @param randomType - параметры
     * @param rnd - представитьль класса Random
     * @param one - Element one того же типа, что числитель и знаменатель
     *
     * @return - случайное комплексное число
     */
    @Override
    public Complex random(int[] randomType, Random rnd, Ring ring) {
        if (ring.algebra[0] >= Ring.Complex) {
            System.out.println("Ring type in the function \"random\" must be number-type (0,1,..,7).");
            return null;
        }
        return new Complex(re.random(randomType, rnd, ring), re.random(randomType, rnd, ring));
    }

    @Override
    public int intValue() {
        return re.intValue();
    }

    @Override
    public double doubleValue() {
        return re.doubleValue();
    }

    /**
     * Creating the Complex number from String s with the same type as "this".
     *
     * @param s -- String with complex value like "a+ib" or "-Ia-b" etc.
     *
     * @return Complex number
     */
    @Override
    public Complex valOf(String s, Ring ring) {
        String ss = s.trim();
        int i = ss.indexOf('+');
        if (i < 2) {
            i = ss.indexOf('-', i);
        }
        Element rea, imm;
        String n1 = ss.substring(i);
        String nn2, n2;
        if (i >= 2) {
            n2 = ss.substring(0, i);
            nn2 = n2.replace('i', ' ').replace('I', ' ');
            if (!nn2.equals(n2)) {
                rea = re.valOf(n1, ring);
                imm = re.valOf(nn2, ring);
            } else {
                imm = re.valOf(n1.replace('i', ' ').replace('I', ' '), ring);
                rea = re.valOf(n2, ring);
            }
        } else {
            nn2 = ss.replace('i', ' ').replace('I', ' ');
            if (!nn2.equals(ss)) {
                rea = re.myZero(ring);
                imm = re.valOf(nn2, ring);
            } else {
                imm = re.myZero(ring);
                rea = re.valOf(nn2, ring);
            }
        }
        return new Complex(rea, imm);
    }

    /**
     * Is this equals I?
     *
     * @param ring -- ring has all needed constants
     *
     * @return boolean = true, if this=I; false, if this != I.
     */
    @Override
    public Boolean isI(Ring ring) {
        return (re.isZero(ring) && im.isOne(ring));
    }

    /**
     * Is this equals (-I)?
     *
     * @return boolean = true, if this=-I; false, if this != -I.
     */
    @Override
    public Boolean isMinusI(Ring ring) {
        return (re.isZero(ring) && im.isMinusOne(ring));
    }

    /**
     * Is this equals 0?
     *
     * @return boolean = true, if this=0; false, if this != 0.
     */
    public Boolean isZERO(Ring ring) {
        return (re.isZero(ring) && im.isZero(ring));
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        return (re.isMinusOne(ring) && im.isZero(ring));
    }

    /**
     * Is this equals 0?
     *
     * @return boolean = true, if this=0; false, if this != 0.
     */
    @Override
    public Boolean isOne(Ring ring) {
        return (re.isOne(ring) && im.isZero(ring));
    }

    /**
     * Проверка на равенство двух комплексных чисел
     *
     * @param c - комплексное число сравниваемое с числом this
     *
     * @return boolean - результат сравнения
     */
    public Boolean equals(Complex c, Ring ring) {
        return ((re.compareTo(c.re, ring) == 0) && (im.compareTo(c.im, ring) == 0));
    }

    /**
     * Sign change
     *
     * @param ring
     *
     * @return (-1)*(this)
     */
    @Override
    public Complex negate(Ring ring) {
        return new Complex(re.negate(ring), im.negate(ring));
    }
    
    /**
     * is Even&
     *
     * @return boolean true is it even
     */
    @Override
    public boolean isEven() {return (re.isEven() && im.isEven());}
 
    /**
     * conjugate
     *
     * @param ring
     *
     * @return (a+ib) ---> (a-ib)
     */
//    public Complex conj(Ring ring) {
//        return new Complex(re, im.negate(ring));
//    }

    /**
     * conjugate
     *
     * @param ring
     *
     * @return (a+ib) ---> (a-ib)
     */
    @Override
    public Element conjugate(Ring ring) {
        return new Complex(re, im.negate(ring));
    }

    /**
     * Возвращает знак комплексного числа как знак действительной части если
     * re.signum()==0 то возвращаем im.signum() Тем самым делит плоскость на две
     * симметричные части и изолированную точку 0. 0 -- только для комплексного
     * ноля: (0,0)-->0 ; (+1) -- для строго правой полуплоскости вместе с
     * положительной мнимой полуосью: (>0,x)-->1,(0,>0)-->1;
     *
     * (-1) -- для строго левой полуплоскости вместе c отрицательной мнимой
     * полуосью: (<0,x)-->-1,(0,<0)-->-1.
     */
    @Override
    public int signum() {
        if (re.signum() == 0)  return im.signum();
        return re.signum();
    }

    /**
     * Module of complex number
     *
     * @return sqrt(re*re+im*im)
     */
    @Override
    public Element abs(Ring ring) {
        return re.multiply(re, ring).add(im.multiply(im, ring), ring).sqrt(ring);
    }

    /**
     * The square of module of this complex number
     *
     * @return re^2 + im^2
     */
    
    @Override
    public Element absSquare(Ring ring) {
        return re.multiply(re, ring).add(im.multiply(im, ring), ring);
    }

    /**
     * Add Complex elements
     *
     * @param c
     * @param ring
     *
     * @return this+c
     */
    public Element add(Complex c, Ring ring) {
        Element Im = im.add(c.im, ring);
        Element Re = re.add(c.re, ring);
        return (Im.isZero(ring)) ? Re : new Complex(Re, Im);
    }

    /**
     * Subtract Complex elements
     *
     * @param c
     * @param ring
     *
     * @return this-c
     */
    public Element subtract(Complex c, Ring ring) {
        Element Re = re.subtract(c.re, ring);
        Element Im = im.subtract(c.im, ring);
        return (Im.isZero(ring)) ? Re : new Complex(Re, Im);
    }

    /**
     * Умножение на комплексное число
     *
     * @param c Complex - комплексное число
     *
     * @return Complex - произведение чисел
     */
    public Element multiply(Complex c, Ring ring) {
        Element Re = re.multiply(c.re, ring).subtract(im.multiply(c.im, ring), ring);
        Element Im = im.multiply(c.re, ring).add(re.multiply(c.im, ring), ring);
        return (Im.isZero(ring)) ? Re : new Complex(Re,Im);
    }

    /**
     * Деление на комплексное число
     *
     * @param c Complex - комплексное число
     *
     * @return Complex - частное чисел
     */
    public Element divide(Complex c, Ring ring) {
        Element d = c.re.multiply(c.re, ring).add(c.im.multiply(c.im, ring), ring);
        Element Re = re.multiply(c.re, ring).add(im.multiply(c.im, ring), ring).divide(d, ring);
        Element Im = im.multiply(c.re, ring).subtract(re.multiply(c.im, ring), ring).divide(d, ring);
        return (Im.isZero(ring)) ? Re : new Complex(Re,Im);
    }

      /**
     * Точное деление на комплексное число
     * @param c Complex - комплексное число
     * @return Complex - частное чисел
     */
    public Element divideExact(Complex c, Ring ring) {
        Element d = c.re.multiply(c.re, ring).add(c.im.multiply(c.im, ring), ring);
        Element Re = re.multiply(c.re, ring).add(im.multiply(c.im, ring), ring).divideExact(d, ring);
        Element Im = im.multiply(c.re, ring).subtract(re.multiply(c.im, ring), ring).divideExact(d, ring);
        return (Im.isZero(ring)) ? Re : new Complex(Re,Im);
    }          
    
    /**
     * Сложение с комплексным числом
     *
     * @param x
     * @param ring
     * @param c Complex - комплексное число
     *
     * @return Complex - сумма чисел
     */
    @Override
    public Element add(Element x, Ring ring) {
        int X_Type = x.numbElementType();
        if(x instanceof Complex){return add((Complex)x,ring);}
        if (X_Type < Ring.Complex)
            return (im.isZero(ring)) ? re.add(x, ring) : new Complex(re.add(x, ring), im);
        if (x instanceof Polynom)
            return (Polynom.polynomFromNumber(this, ring)).add((Polynom) x, ring);
        if (x instanceof Fraction){
           return x.add(this, ring);
        }
        return new com.mathpar.func.F(com.mathpar.func.F.ADD, new Element[] {this, x});
    }

    /**
     * Вычитание комплексного числа
     *
     * @param c вычитаемое
     *
     * @return Complex - разность чисел
     */
    @Override
    public Element subtract(Element x, Ring ring) {
        int X_Type = x.numbElementType();
        if(x instanceof Complex){
            return subtract((Complex)x,ring);
        }
        if (X_Type < Ring.Complex) {
            return (im.isZero(ring)) ? re.subtract(x, ring) : new Complex(re.subtract(x, ring), im);
        }
        if (x instanceof Polynom) {
            return (Polynom.polynomFromNumber(this, ring)).subtract((Polynom) x, ring);
        }
        if (x instanceof Fraction){
           return x.subtract(this, ring).negate(ring);
        }
        return new com.mathpar.func.F(com.mathpar.func.F.SUBTRACT, new Element[] {this, x});
    }

    /**
     * Умножение на элемент
     *
     * @param x сомножитель
     * @param ring
     *
     * @return
     */
    @Override
    public Element multiply(Element x, Ring ring) {
        if (x instanceof Complex) {return multiply((Complex) x, ring);}
        int X_Type = x.numbElementType();
        if (X_Type < Ring.Complex) {
            return (im.multiply(x, ring).isZero(ring)) ? re.multiply(x, ring) : new Complex(re.multiply(x, ring), im.multiply(x, ring));
        }
        if (x instanceof Polynom) {
            return Polynom.polynomFromNumber(this, ring).multiply(x, ring);
        }
        if (x instanceof Fraction) {Fraction fr=(Fraction)x;
            Element el= new Fraction(this, fr.denom).cancel(ring);
            return  el.multiply(fr.num, ring);
        }
        return new com.mathpar.func.F(com.mathpar.func.F.MULTIPLY, new Element[] {this, x});
    }

    @Override
    public Element divide(Element x, Ring ring) { if(x.isOne(ring))return this;
        if (x instanceof Complex) {return divide((Complex) x, ring);}
        int X_Type = x.numbElementType();
        if (X_Type < Ring.Complex) {
            return (im.isZero(ring)) ? re.divide(x, ring) : new Complex(re.divide(x, ring), im.divide(x, ring));
        }
        if (x instanceof Polynom) {
            return new Fraction(Polynom.polynomFromNumber(this, ring), x);
        }
        if (x instanceof Fraction) {Fraction fr=(Fraction)x;
            Element el= new Fraction(this, fr.num).cancel(ring);
            return  el.multiply(fr.denom, ring);
        }
        return new com.mathpar.func.F(com.mathpar.func.F.DIVIDE, new Element[] {this, x});
    }

    @Override
        public Element divideExact(Element x, Ring ring) {
        if (x instanceof Complex) {return divideExact((Complex) x, ring);}
        int X_Type = x.numbElementType();
        if (X_Type < Ring.Complex) {
            return (im.isZero(ring)) ? re.divide(x, ring) : new Complex(re.divide(x, ring), im.divide(x, ring));
        }
        if (x instanceof Polynom) {
            return  Polynom.polynomFromNumber(this, ring).divideExact(x, ring);
        }
        if (x instanceof Fraction) {Fraction fr=(Fraction)x; 
            Element el= new Fraction(this, fr.num).cancel(ring);
            return  el.multiply(fr.denom, ring);
        }
        return new com.mathpar.func.F(com.mathpar.func.F.DIVIDE, new Element[] {this, x});
    }
    
    @Override
    public Boolean isNegative() {
        return (re.isZero(Ring.ringZxyz))? im.isNegative(): re.isNegative();
    }

    @Override
    public String toString() {
        Ring ring = Ring.ringZxyz;
        return toString(ring);
    }

    /**
     * Преобразует комплексное число в строку
     *
     * @param ring -- Ring.
     *
     * @return String - строковое представление числа.
     */
    @Override
    public String toString(Ring ring) {
        boolean reNotZero = !re.isZero(ring);
        boolean imNotZero = !im.isZero(ring);
        if (!imNotZero) return re.toString(ring);

        StringBuffer S = new StringBuffer();
        if (reNotZero) S = S.append(re.toString(ring));
        if (imNotZero) {
            if ((im.signum() > 0) && (reNotZero)) {
                S = S.append("+");
            }
            String imm = im.toString(ring);
            if (!im.isItNumber() && (!((im instanceof Polynom) && (((Polynom) im).coeffs.length == 1)))) {
                imm = "(" + imm + ")";
            }
            S =(im.isOne(ring))? S.append("\\i ") : (im.isMinusOne(ring))? S.append("-\\i ") :S.append(imm).append("\\i ");
        }
        String s = S.toString();
        return (!reNotZero && !imNotZero) ? "0" : (reNotZero && imNotZero) ? "(" + s + ")" : s;
    }

    /**
     * Возведение комплексного числа в степень с целочисленным показателем
     *
     * @param n показатель степени - любое целое число
     *
     * @return
     */
    @Override
    public Element pow(int n, Ring ring) {
        if(isZero(ring)) return this;
        Complex one = myOne(ring);
        Complex min_one = myMinus_one(ring);
        //проверка числа на равенство 1 или -1
        if (equals(min_one) || equals(one)) {
            return ((n & 1) == 1) ? this : one;
        }
        Element res = one;
        Element temp = this;
        if ((n & 1) == 1) {
            res = temp;
        }
        n >>>= 1;
        int m = (n < 0) ? -n : n;
        while (m != 0) {
            temp = (temp.multiply(temp, ring));
            if ((m & 1) == 1) {
                res = res.multiply(temp, ring);
            }
            m >>>= 1;
        }
        return (n < 0) ? one.divide(res, ring) : res;
    }

    /**
     * Вычисление двух значений квадратного корня
     *
     * @return Complex[] два квадратных корня
     */
    public Complex[] sqrt2roots(Ring ring) {
        Element zero = re.myZero(ring);
        Complex ZERO = myZero(ring);
        Element ro = abs(ring);
        Complex[] c = new Complex[2];
        if (ro.compareTo(zero, ring) == 0) {
            c[0] = ZERO;
            c[1] = ZERO;
            return c;
        }
        Element fi = (re.divide(ro, ring)).arccs(ring).divide(re.valOf(2L, ring), ring);
        ro = ro.sqrt(ring);
        Element piPLUSfi = ro.pi(ring).add(fi, ring);
        c[0] = new Complex(fi.cos(ring).multiply(ro, ring), fi.sin(ring).
                multiply(ro, ring));
        c[1] = new Complex(piPLUSfi.cos(ring).multiply(ro, ring), piPLUSfi.sin(ring).
                multiply(ro, ring));
        return c;
    }

    /**
     * Вычисление квадратного корня
     *
     * @param root_numb int // номер корня по порядку - (0 или 1)
     *
     * @return требуемый корень
     */
    public Complex sqrt(int root_numb, Ring ring) {
        return sqrt2roots(ring)[root_numb];
    }

    /**
     * Вычисление квадратного корня из комплексного числа
     *
     * @return первый корень
     */
    @Override
    public Complex sqrt(Ring ring) {
        if(im.isZero(ring)){
         if(re.compareTo(re.myZero(ring), ring)==-1){
         return new Complex(re.negate(ring).sqrt(ring), re.myOne(ring));
         }
         return new Complex(re.sqrt(ring), re.myZero(ring));
        }
        return sqrt2roots(ring)[0];
    }

    @Override
    public Element rootTheFirst(int pow, Ring ring){
     return root(pow,ring)[0];
    }


    /**
     * Вычисление n значений корня n-ой степени
     *
     * @return Complex[] n значения корня n-ой степени
     */
    @Override
    public Complex[] root(int n, Ring ring) {
        return pow(1, n, ring);
    }

    /**
     * Вычисление n значений дробной степрени num/den
     *
     * @param num числитель показателя степени
     * @param den знаменатель показателя степени
     *
     * @return Complex[] n значения дробной степени
     */
    @Override
    public Complex[] pow(int num, int den, Ring ring) {
        Element nn = re.valOf(num, ring);
        Element nd = re.valOf(den, ring);
        Element two = re.valOf(2L, ring);
        Element zero = re.myZero(ring);
        Complex ZERO = myZero(ring);
        Element ro = abs(ring);
        Complex[] c = new Complex[den];
        if (ro.compareTo(zero, ring) == 0) {
            for (int k = 0; k < den; k++) {
                c[k] = ZERO;
            }
            return c;
        }
        Element fi = (re.divide(ro, ring)).arccs(ring).multiply(nn, ring).divide(nd, ring);
        ro = ro.powTheFirst(num, den, ring);
        Element pi2DIVnd = ro.pi(ring).multiply(two, ring).divide(nd, ring);
        c[0] = new Complex(fi.cos(ring).multiply(ro, ring), fi.sin(ring).
                multiply(ro, ring));
        for (int k = 1; k < den; k++) {
            fi = fi.add(pi2DIVnd, ring);
            c[k] = new Complex(fi.cos(ring).multiply(ro, ring), fi.sin(ring).
                    multiply(ro, ring));
        }
        return c;
    }

    public Element[] toTrigForm(Ring ring) {
        Element[] r = new Element[2];
        r[0] = abs(ring);
        r[1] = (re.divide(r[0], ring)).arccs(ring);
        return r;
    }

    /**
     * Конструктор комплексного числа из тригонометрической формы с точностью
     * eps
     *
     * @param r Element[]//r[0]-mod,r[1]-arg
     * @param eps int
     */
    public Complex(Element[] r, Ring ring) {
        re = r[0].multiply((r[1]).cos(ring), ring);
        im = r[0].multiply((r[1]).sin(ring), ring);
    }

    /**
     * Конструктор, создающий число Complex(NumberR) из строки
     *
     * @param S String строковое представление числа
     */
    public Complex complexOfNumberR(String S) {
        Ring ring = new Ring("R[]");
        NumberR re1 = null, im1 = null;
        int i = 0;
        boolean b = false;
        //инкрементирование минуса
        if (S.charAt(0) == '-') {
            i++;
            b = true;
        }
        String s = "";
        while (i < S.length() && S.charAt(i) != '+' && S.charAt(i) != '-'
                && S.charAt(i) != 'i') {
            s += S.charAt(i);
            i++;
        }
        // если строка не кончилась
        if (i < S.length()) {
            //и если за числом стоит i, то это мнимоя часть
            if (S.charAt(i) == 'i') {
                // дуйствительной нет
                re = new NumberR(0);
                // если мнимого нет то это 1
                if (s.equals("")) {
                    im = new NumberR(1);
                } else {
                    im = new NumberR(s);
                }
                // если сначала был минус, то смена знака
                if (b) {
                    im = im.negate(ring);
                }
            } else {
                // если нет, то действительная часть
                re = new NumberR(s);
                if (b) {
                    re = re.negate(ring);
                }
                // прверка, что стоит между двумя частями числа (действительной и мнимой)
                if (S.charAt(i) == '+') {
                    b = false;
                }
                i++;
                s = "";
                // опять такой-же цикл, теперь для мнимой части
                while (i < S.length() && S.charAt(i) != '+'
                        && S.charAt(i) != '-'
                        && S.charAt(i) != 'i') {
                    s += S.charAt(i);
                    i++;
                }
                // аналогично тому, что было выше
                if (s.equals("")) {
                    im1 = new NumberR(1);
                } else {
                    im1 = new NumberR(s);
                }
                if (b) {
                    im1 = im1.negate();
                }
            }
        } // если кончилось (была только одна часть, то она действительна)
        else {
            re1 = new NumberR(s);
            if (b) {
                re1 = re1.negate();
            }
            im1 = new NumberR(0);
        }
        return new Complex(re1, im1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return re.equals(c.re) && im.equals(c.im);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.re != null ? this.re.hashCode() : 0);
        hash = 97 * hash + (this.im != null ? this.im.hashCode() : 0);
        return hash;
    }

    /**
     * Проверяет равенство этого комплексного числа данному {@code x}:
     * комплексные числа считаются равными, если компоненты вектора,
     * соединяющего две точки на комплексной плоскости, не превосходят по
     * абсолютной величине machinEpsilon данного {@code ring}.
     *
     * @param x
     * @param ring
     *
     * @return {@code true}, если этот объект равен данному {@code x}, иначе
     * {@code false}
     */
    @Override
    public boolean equals(Element x, Ring ring) {
        int type = x.numbElementType();
        Complex c;
        if (type < Ring.NumberOfSimpleTypes) {
            c = new Complex(x, ring);
        } else if (type >= Ring.Polynom) {
            return false;
        } else if(x instanceof Fraction){Fraction fr=(Fraction)x;
              return fr.num.equals(multiply(fr.denom,ring), ring);
        } else if(x instanceof Complex) return equals((Complex)x, ring);
        return x.compareTo(this, ring) == 0;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return ((re.isZero(ring) && (im.isZero(ring)))) ? true : false;
    }

    @Override
    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        Element newx=x;
        int type = numbElementType();
        int x_type = x.numbElementType();
        if(x instanceof Fraction){
        return (new Fraction(this,ring)).compareTo(x,ring);
        }
        if (x_type < Ring.Complex) {
            newx = new Complex(x, ring);
            x_type = newx.numbElementType(); } 
        if (type > x_type) {return 1;}
        if (type < x_type) {return -1;}
        return compareTo((Complex) newx, ring);
    }

    /**
     * Вводит порядок для комплексных чисел: сначала по модулю, а потом на одной
     * окружности (от -pi до +pi). Поэтому числа, например, (-1 + \sigma i) и
     * (-1 - \sigma i) обязаны различаться при любом сколь угодно малом \sigma,
     * так как между ними лежит целый круг. С другой стороны по отношению к
     * machinEpsilon они совпадают. И об этом факте свидетельствует метод
     * equals().
     *
     * @param x
     * @param ring
     *
     * @return -1, 0, 1 если объект соответственно меньше, равен, больше данного
     * x
     */
    public int compareTo(Complex x, Ring ring) {
        Element res = absSquare(ring).subtract(x.absSquare(ring), ring);
        if (!res.isZero(ring)) return (res.isNegative()) ? -1 : 1;
        return compareToOnCircle( x,  ring);
    }
    
     /**
     * Вводит порядок для комплексных чисел, которые лежат на одной окружности:
     * (от -pi до +pi). 
     * Числа(-1 + \sigma i) и (-1 - \sigma i) обязаны различаться при любом сколь 
     * угодно малом \sigma. ( Применяется equals().)
     *
     * @param x
     * @param ring
     *
     * @return -1, 0, 1 если объект соответственно меньше, равен, больше данного
     * x
     */  
 public int compareToOnCircle(Complex x, Ring ring) {
            int a1 = re.signum(); int a2 = x.re.signum();
            int b1 = im.signum(); int b2 = x.im.signum();
            if (a1 >= 0 && a2 >= 0) {//im1>im2
                return (b2 > b1) ? -1 : (b2 < b1) ? 1 : 0; }
            if (a1 < 0 && a2 < 0) {//im2>im1
                if (b1 > 0 && b2 > 0) {return (b1 < b2) ? -1 : (b2 < b1) ? 1 : 0;
                } else {return (b1 > b2) ? 1 : (b2 > b1) ? -1 : 0;               }
            }
            if (b1 > 0 && b2 > 0) {//re2>re1
                return (a2 > a1) ? 1 : (a2 < a1) ? -1 : 0;  }
            if (b1 <= 0 && b2 <= 0) {//re1>re2
                return (a1 > a2) ? 1 : (a1 < a2) ? -1 : 0;  }
            if (a1 >= 0 && a2 <= 0) {//re1>re2
                return (b1 >= b2) ? 1 : -1;             }
            if (a1 <= 0 && a2 >= 0) {//re1>re2
                return (b2 >= b1) ? -1 : 1;             }
            return 0;
    }
    
    @Override
    public int numbElementType() {int imRe=Math.max(im.numbElementType(), re.numbElementType());
        return (imRe<Ring.Complex)? imRe + Ring.Complex: Ring.Complex;
    }

    @Override
    public Element D(Ring ring) {
        return myZero(ring);
    }

    @Override
    public Element D(int re, Ring ring) {
        return myZero(ring);
    }

    /**
     * Transform this number of Complex type to the new type fixed by
     * "numberType" defined by Ring.type
     *
     * @param numberType - new type
     * @param ring
     *
     * @return this transormed to the new type
     */
    @Override
    public Element toNumber(int numberType, Ring ring) {
        if (numberType == numbElementType()) { return this;}
        if (numberType >= Ring.Complex) { 
            numberType = numberType ^ Ring.Complex;}
            return new Complex(re.toNumber(numberType, ring), im.toNumber(numberType, ring));
    }

    public double[][] univers_pow(Complex comp, int n, Ring ring) {
        double[] q1 = new double[n];
        double[] q2 = new double[n];
        double res[][] = new double[2][];
        double t;
        double c;
        double a = comp.re.doubleValue();
        double b = comp.im.doubleValue();
        double m = NumberR64.ONE.divide(new NumberR64(n), ring).doubleValue();
        double pi = 3.14159265;
        double s = Math.pow(a * a + b * b, (m / 2));
        if (a == 0) {
            t = Math.signum(b) * pi / 2;
            t *= m;
            c = 2 * pi * m;
            for (int i = 0; i < n; i++) {
                q1[i] = s * Math.cos(t);
                q2[i] = s * Math.sin(t);
                t += c;
            }
            res[0] = q1;
            res[1] = q2;
            return res;
        }
        if (a > 0) {
            t = Math.atan(b / a);
            t *= m;
            c = 2 * pi * m;
            for (int i = 0; i < n; i++) {
                q1[i] = s * Math.cos(t);
                q2[i] = s * Math.sin(t);
                t += c;
            }
            res[0] = q1;
            res[1] = q2;
            return res;
        }
        t = pi + Math.atan(b / a);
        if (b < 0) {
            t -= 2 * pi;
        }
        t *= m;
        c = 2 * pi * m;
        for (int i = 0; i < n; i++) {
            q1[i] = s * Math.cos(t);
            q2[i] = s * Math.sin(t);
            t += c;
        }
        res[0] = q1;
        res[1] = q2;
        return res;
    }

    public Complex[] uni_pow(double[][] a) {
        double[] q1 = new double[a.length];
        double[] q2 = new double[a.length];
        System.arraycopy(a[0], 0, q1, 0, a[0].length);
        System.arraycopy(a[1], 0, q2, 0, a[1].length);
        Complex[] masC = new Complex[a.length];
        for (int i = 0; i < a.length; i++) {
            masC[i] = new Complex(q1[i], q2[i]);
        }
        return masC;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toNumber(Algebra, r);
    }

    public Element[] divAndRemZ(Complex q, Ring ring) {
        Complex p = this;
        NumberZ div = (NumberZ) q.absSquare(ring);
        NumberZ Re = (NumberZ) p.re.multiply(q.re, ring).add(p.im.multiply(q.im, ring), ring);
        NumberZ Im = (NumberZ) p.im.multiply(q.re, ring).subtract(p.re.multiply(q.im, ring), ring);
        NumberZ Res[] = Re.divideAndRemainder(div);
        NumberZ Ims[] = Im.divideAndRemainder(div);
        Complex d = new Complex(Res[1], Ims[1]);
        Element dd = d.divide((Complex) q.conjugate(ring), ring);
        Element[] res = {new Complex(Res[0], Ims[0]), dd};
        return res;
    }

    @Override
    public Element GCD(Element x, Ring ring) {int alg=ring.algebra[0] % Ring.Complex;
        if((alg != Ring.Z)&&(alg!=Ring.Q)) { return NumberZ.ONE;}
       Complex a = new Complex(this.re, this.im);
        if(!(x instanceof Complex)) return a.im.GCD(a.re.GCD(x, ring), ring);
       Complex b = new Complex( ((Complex) x).re, ((Complex)x).im);
       if((a.re.isZero(ring))&& (b.re.isZero(ring)))
           return new Complex(NumberZ.ZERO, b.im.GCD(a.im, ring));
         //  return b.im.GCD(b.re.GCD(a.re.GCD(a.im, ring), ring), ring);
       if(a.im.isZero(ring)) return b.im.GCD(a.re.GCD(b.re, ring), ring);
       if(b.im.isZero(ring)) return a.im.GCD(a.re.GCD(b.re, ring), ring);
       Element denom=NumberZ.ONE;  
       VectorS vsA=VectorS.toCommonDenom(new VectorS(a.re, a.im),ring);
       VectorS vsB=VectorS.toCommonDenom(new VectorS(b.re, b.im),ring);
       denom=vsA.V[2].multiply(vsB.V[2], ring);
       if(!denom.isOne(ring)){denom= vsA.V[2].LCM(vsB.V[2], ring); 
           Element dop=denom.divideExact(vsA.V[2], ring);
           a.im=vsA.V[1].multiply(dop, ring); a.re=vsA.V[0].multiply(dop, ring);
           dop=denom.divideExact(vsB.V[2], ring);
           b.im=vsB.V[1].multiply(dop, ring);  b.re=vsB.V[0].multiply(dop, ring);
           denom=vsB.V[2].multiply(vsA.V[2], ring).divideExact(denom, ring);
       }
        Element aAbs = a.absSquare(ring);
        Element bAbs = b.absSquare(ring);
        Element[] qANDr;
        while ((!a.isZero(ring)) && (!b.isZero(ring))) {
            switch (aAbs.compareTo(bAbs, ring)) {
                case 1:
                case 0:
                    qANDr = a.divAndRemZ(b, ring);
                    a = (qANDr[1] instanceof Complex) ? (Complex)qANDr[1] : new Complex(qANDr[1], ring);
                    aAbs = a.absSquare(ring);
                    if (qANDr[0].signum() == 0){
                          Element ee= (a.im.GCD(a.re, ring)).GCD((b.im.GCD(b.re, ring)), ring);
                         return (denom.isOne(ring))? ee: new Fraction(ee, denom);      }
                    break;
                case -1:
                    qANDr = b.divAndRemZ(a, ring);
                    b = (qANDr[1] instanceof Complex) ? (Complex)qANDr[1] : new Complex(qANDr[1], ring);
                    bAbs = b.absSquare(ring);
                    if (qANDr[0].signum() == 0){
                          Element ee= (a.im.GCD(a.re, ring)).GCD((b.im.GCD(b.re, ring)), ring);
                    return (denom.isOne(ring))? ee: new Fraction(ee, denom);      }
                    break;
            }
        }  
        Complex res = a.isZero(ring) ? b : a;
        if(!denom.isOne(ring)){res.re=new Fraction(res.re, denom); res.im=new Fraction(res.im, denom);}
        return (res.im.signum() == 0) ? res.re :(res.re.signum() == 1) ? res : res.negate(ring);
    }
    @Override
    public Element pi(Ring r) { return im.pi(r);}

    @Override
    public Element e(Ring r) {  return im.e(r); }

    @Override
    public Element valOf(double x, Ring ring) {
        return  im.valOf(x, ring);
    }

    @Override
    public Element valOf(int x, Ring ring) {
        return im.valOf(x, ring);
    }

    @Override
    public Element valOf(long x, Ring ring) {
        return im.valOf(x, ring);
    }

    @Override
    public Complex value(Element[] var, Ring ring) {
        Element ree = (re.numbElementType() < Ring.Polynom) ? re : re.value(var, ring);
        Element imm = (im.numbElementType() < Ring.Polynom) ? im : im.value(var, ring);
        return new Complex(ree, imm);
    }

    //==========================================================================
    // Вычисление тригонометрических функций
    // Некоторая временная вынужденная затычка !!!
    //@ autor Смирнов Роман
    //===========================================================================
    /**
     * Вычисляем значение экспоненты в степени Z
     * @param ring
     * @return
     */
    @Override
    public Element exp(Ring ring){
      return new Complex(re.exp(ring).multiply(im.cos(ring), ring), re.exp(ring).multiply(im.sin(ring), ring));
    }
    /**
     * Данная функция вообще многозначная, поэтому возвратим главное значение логарифма
     * @param ring
     * @return
     */
    @Override
    public Element ln(Ring ring){
        if(isZero(ring)) {
            return Element.NAN;
        }
        return new Complex(abs(ring).ln(ring),arg(ring));
    }

    /**
     * При вычислении аргумента используем следующие правила
     *            _
     *           | arctg(im/re),     re>0;
     *           | arctg(im/re)+\pi, re<0, im>0;
     *           | arctg(im/re)-\pi, re<0 , im<0;
     * arg (Z) =<  \pi/2 ,           re=0 , im>0;
     *           | -\pi/2,           re=0 , im<0;
     *           | 0,                re>0, im=0;
     *           | \pi,              re<0, im=0;
     *           | NaN               re=0, im=0;
     *            -
     *
     * @param ring - наше входное кольцо
     * @return аргумент комплексного числа Z=re+\i*im
     */
    @Override
    public Element arg(Ring ring) {
        if (isZero(ring)) return Element.NAN;
        if (re.isZero(ring)) {
            Element piDiv2 = re.pi(ring).divide(ring.posConst[2], ring);
            return (im.isNegative()) ?  piDiv2.negate(ring): piDiv2 ;
        }
        if (re.isNegative()) {
            return (im.isZero(ring)) ? re.pi(ring) : (im.compareTo(ring.numberZERO) == 1)
                    ? im.divide(re, ring).arctn(ring).add(re.pi(ring), ring) : im.divide(re, ring).arctn(ring).subtract(re.pi(ring), ring);
        } 
        return (im.isZero(ring)) ? re.myZero(ring) : im.divide(re, ring).arctn(ring);
    }

    @Override
    public Element sin(Ring ring) {
        if(im.isZero(ring)) {
            return re.sin(ring);
        }
        return multiply(ring.numberI, ring).exp(ring).subtract(multiply(ring.numberMINUS_I, ring).exp(ring), ring).divide(ring.numberONE.valOf(2, ring).multiply(ring.numberI, ring), ring);
    }

    @Override
    public Element cos(Ring ring) {
        if(im.isZero(ring)) {
            return re.cos(ring);
        }
        return multiply(ring.numberI, ring).exp(ring).add(multiply(ring.numberMINUS_I, ring).exp(ring), ring).divide(ring.numberONE.valOf(2, ring), ring);
    }

    @Override
    public Element tan(Ring ring) {
        if(im.isZero(ring)) {
            return re.tan(ring);
        }
        return sin(ring).divide(cos(ring), ring);
    }

    @Override
    public Element ctg(Ring ring) {
        if(im.isZero(ring)) {
            return re.ctg(ring);
        }
        return cos(ring).divide(sin(ring), ring);
    }

    @Override
    public Element sh(Ring ring) {
        if(im.isZero(ring)) {
            return re.sh(ring);
        }
        return exp(ring).subtract(negate(ring).exp(ring), ring).divide(ring.numberONE.valOf(2, ring), ring);
    }

    @Override
    public Element ch(Ring ring) {
        if(im.isZero(ring)) {
            return re.ch(ring);
        }
        return exp(ring).add(negate(ring).exp(ring), ring).divide(ring.numberONE.valOf(2, ring), ring);
    }

    @Override
    public Element th(Ring ring) {
        if(im.isZero(ring)) {
            return re.th(ring);
        }
        return sh(ring).divide(ch(ring), ring);
    }

    @Override
    public Element arctn(Ring ring){
    if(im.isZero(ring)) {
            return re.arctn(ring);
        }
    return null;
    }

    @Override
    public Element cth(Ring ring) {
        if(im.isZero(ring)) {
            return re.cth(ring);
        }
        return ch(ring).divide(sh(ring), ring);
    }

    @Override
    public boolean isItNumber() {
      return  (re.isItNumber() && im.isItNumber());
    }

        @Override
    public Element[] posConst() {
        return re.posConst() ;
    }
            @Override
    public Element[] negConst() {
        return re.negConst();
    }
    
    @Override
    public Element Factor(boolean doNewVector, Ring ring) {
         return  re.Factor(doNewVector, ring).add(im.Factor(doNewVector, ring).multiply(ring.numberI, ring), ring);
    }
    /**
    *  Замена порядка переменных во всех полиномах этого элемента
    * @param varsMap - правило замены порядка переменных
    * @param flag   - куда/откуда
    * @param ring    - Ring
    * @return - результат замены порядка переменныхво всех полиномах
    */
    @Override
    public Element changeOrderOfVars(int[] varsMap, boolean flag, Ring ring){
       return new Complex(
                 re.changeOrderOfVars(varsMap, flag, ring),
                 im.changeOrderOfVars(varsMap, flag, ring));
    }

    @Override
    public boolean isComplex(Ring ring){
    return im.isZero(ring) ? false : true;
    }



    public static void main(String[] args) {
       Ring ring = new Ring("Z[x,y,z]");
       Complex c1 = new Complex(new NumberZ(10), new NumberZ(4));
       Complex c2 = new Complex(new NumberZ(5), new NumberZ(2));
        System.out.println("GCD = " + c1.GCD(c2, ring).toString(ring));
    }
 //================================================================================
 //================================================================================
}
