package com.mathpar.number;

import java.util.Random;
import com.mathpar.polynom.PascalTriangle;

/**
 * Class NumberZp32 provides an modular operations over Long number: add mod p;
 * subtract mod p; multiply mod p; divide mod p (where p is a prime number) A
 * characteristic p (long) of Zp finite field. It is a prime number which less
 * that 2^31.
 *
 * @author gennadi
 * @version 2.0 10/10/08-- 01/01/11
 * @since ParCA 2.0 --3.9.7
 */
public class NumberZp32 extends NumberZ64 {
    public static final NumberZp32 ZERO = new NumberZp32(0L);
    public static final NumberZp32 ONE = new NumberZp32(1L);
    public static final NumberZp32 MINUS_ONE = new NumberZp32(-1L);
    private static final long serialVersionUID = -3648961564945418050L;

    public NumberZp32(long val) {
        super(val);
    }

    public NumberZp32(long val, Ring ring) {
        super(val % ring.MOD32);
    }

    public NumberZp32(NumberZ64 val, Ring ring) {
        super(val.value % ring.MOD32);
    }

    public void initCacheforBinomial(Ring ring) {
        PascalTriangle.initCacheLp32(ring.MOD32);
    }

    /**
     * Constructs a newly allocated
     * <code>NumberZp32</code> object that represents the NumberZ value.
     *
     * @param x the <code>NumberZ</code> to be converted to a
     * <code>NumberZp32</code>.
     */
    public NumberZp32(NumberZ x, Ring ring) {
        super(x.longValue() % ring.MOD32);
    }

    /**
     * Constructs a newly allocated
     * <code>NumberZp32</code> object that represents the integer value
     * indicated by the
     * <code>String</code> parameter. The string is converted to a
     * <code>Long</code> value in exactly the manner used by the
     * <code>parseLong</code> method for radix 10.
     *
     * @param s the <code>String</code> to be converted to a <code>Long</code>.
     * @exception NumberFormatException if the <code>String</code> does not
     * contain a parsable <code>long</code>.
     * @see java.lang.Long#parseLong(java.lang.String, int)
     */
    public NumberZp32(String s, Ring r) throws NumberFormatException {
        super(NumberZ64.parseLong(s, 10) % r.MOD32);
    }

    /**
     * Returns the value of this
     * <code>Long</code> as an
     * <code>int</code>.
     */
    @Override
    public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value of this
     * <code>Long</code> as a
     * <code>long</code> value.
     */
    @Override
    public long longValue() {
        return value;
    }

    /**
     * Returns the value of this
     * <code>Long</code> as a
     * <code>double</code>.
     */
    @Override
    public double doubleValue() {
        return (double) value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Returns a hash code for this
     * <code>Long</code>. The result is the exclusive OR of the two halves of
     * the primitive
     * <code>long</code> value held by this
     * <code>Long</code> object. That is, the hashcode is the value of the
     * expression: <blockquote><pre>
     * (int)(this.longValue()^(this.longValue()&gt;&gt;&gt;32))
     * </pre></blockquote>
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public boolean equals(Element obj, Ring ring) {
        return (obj instanceof NumberZp32)
                ? (value - ((NumberZp32) obj).value) % ring.MOD32 == 0 : false;
    }

    @Override
    public int numbElementType() {
        return Ring.Zp32;
    }

    /**
     * Random number
     *
     * @param rT
     * @param rnd (Java Random class object)
     */
    public NumberZp32(int[] rT, Random rnd, Ring ring) {
        super(com.mathpar.number.NumberZ64.ONE.random(rT, rnd, NumberZ.ONE).longValue() % ring.MOD32);
    }

    @Override
    public Element myMinus_one() {
        return MINUS_ONE;
    }

    @Override
    public Element myOne(Ring ring) {
        return ONE;
    }

    @Override
    public Element myZero(Ring ring) {
        return ZERO;
    }

    @Override
    public Element minus_one() {
        return MINUS_ONE;
    }

    @Override
    public Element one(Ring ring) {
        return ONE;
    }

    @Override
    public Element zero(Ring ring) {
        return ZERO;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return ((value % ring.MOD32) == 0L);
    }

    @Override
    public Boolean isOne(Ring ring) {
        return ((mod(ring).value) == 1L);
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        return ((((NumberZp32) Mod(ring)).value) == -1L);
    }

    @Override
    public NumberZp32 abs(Ring ring) {
        return new NumberZp32(Math.abs(value));
    }

    @Override
    public NumberZp32 negate(Ring ring) {
        return new NumberZp32(-value);
    }

// /////////////////////////////////////////////////////////////////////////////
// вычисляет обратное к b по простому модулю m (случай b=1 не проверяется)
    //   xy[0] содержит b^{-1} mod p. При обращении q1, xy[0] и xy[1] нужно занулить
////////////////////////////////////////////////////////////////////////////////
    public static void m_Inverse(int m, int b, int q1, int[] xy) {
        int r, q, temp;
        r = m % b;
        q = (m - r) / b;
        if (r == -1) {
            xy[0] = -1;
            xy[1] = q;
        } else if (r == 1) {
            xy[0] = 1;
            xy[1] = -q;
        } else {
            m_Inverse(b, r, q, xy);
        }
        temp = xy[0];
        xy[0] = xy[1];
        xy[1] = temp - q1 * xy[1];
    }

    /**
     * вычисляет обратное к a по простому модулю p=getMod() (общий случай)
     */
    public static int inverse(int a, int MOD32) {
        if (a == 1) {
            return 1;
        } else if (a == -1) {
            return -1;
        } else {
            int xy[] = new int[]{0, 1};
            int qq = 0;
            m_Inverse(MOD32, a, qq, xy);
            return (xy[0]);
        }
    }

    // Not bad code, but the cases n=0 and n=1 may be done better!!
    /**
     * Возвращает число типа long возведённое в целую степень по модулю MOD32
     *
     * @param a число, которое возводится в целую степень
     * @param pow целая степень (может быть нулем и отрицательным числом)
     * @param MOD32 простой модуль поля вычетов
     * @return
     */
    public static long pow(long a, int pow, long MOD32) {
        int n = (pow < 0) ? -pow : pow; //возведем в положительную степень и обратим
        //проверка числа на равенство 1 или -1
        if ((a == -1) || (a == 1)) {
            return ((n & 1) == 1) ? ((pow < 0) ? inverse((int) a, (int) MOD32) : a) : 1;
        }
        //обрезание степени по теореме Ferma
        if (n > MOD32) {
            n %= MOD32 - 1;
        }
        long res = 1, temp = a;
        if ((n & 1) == 1) {
            res = a;
        }
        n >>>= 1;
        while (n != 0) {
            temp = (temp * temp) % MOD32;
            if ((n & 1) == 1) {
                res = (res * temp) % MOD32;
            }
            n >>>= 1;
        }
        return (pow < 0) ? inverse((int) res, (int) MOD32) : res;
    }

    public static NumberZp32 valueOf(long l, Ring ring) {
        return new NumberZp32(l % ring.MOD32);
    }

    public static NumberZp32 valueOf(int l, Ring ring) {
        return new NumberZp32(((long) l) % ring.MOD32);
    }

    public static NumberZp32 valueOf(double l, Ring ring) {
        return new NumberZp32(NumberZ64.valueOf(l).value % ring.MOD32);
    }

    public static NumberZp32 valueOf(NumberZ64 l, Ring ring) {
        return new NumberZp32(l.value % ring.MOD32);
    }

    public static NumberZp32 valueOf(NumberZp32 l) {
        return l;
    }

    @Override
    public Element value(Element[] ValuesOfVars, Ring ring) {
        return this;
    }

    @Override
    public NumberZp32 random(int[] randomType, Random rnd, Ring ring) {
        int nbits = randomType[randomType.length - 1];
        return new NumberZp32((long) rnd.nextInt(1 << nbits) % ring.MOD32);
    }

    @Override
    public NumberZp32 GCD(Element x, Ring ring) {
        return NumberZp32.ONE;
    }

    public NumberZp32 GCD(Element x) {
        return NumberZp32.ONE;
    }

    @Override
    public NumberZp32 inverse(Ring ring) {
        return new NumberZp32(inverse((int) this.value, (int) ring.MOD32));
    }

    @Override
    public NumberZp32 pow(int n, Ring ring) {
        return new NumberZp32(pow(value, n, ring.MOD32));
    }

//    @Override
//    public Element add(Element x, Ring ring) {
//        return x.numbElementType() <= Ring.Zp32
//                ? new NumberZp32(value + ((NumberZ64) x).value, ring)
//                : x.add(this, ring);
//    }

    public NumberZp32 add(NumberZp32 x, Ring ring) {
        return new NumberZp32(x.value + value, ring);
    }

//    @Override
//    public NumberZp32 subtract(Element x, Ring ring) {
//        return new NumberZp32(value - ((NumberZ64) x).value, ring);
//    }
    /**
     * Subtraction of two elements. The first is NumberZp32.
     * @param x
     * @param ring Rinf
     * @return
     */
    @Override
    public Element subtract(Element x, Ring ring) {
            return x.negate(ring).add(this, ring);
    }

    /**
     * Addition of two elements. The first is NumberZp32.
     * @param x
     * @param ring Rinf
     * @return
     */
    @Override
    public Element add(Element x, Ring ring) {
        int Xtype=x.numbElementType();
        if (Xtype == Ring.Zp32) return add ((NumberZp32)x, ring);
        if(Xtype==0){ if (x == NAN) {return NAN;}
          if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY))  return x;}
    //    if  ((Xtype != Ring.Z64)&&(Xtype != Ring.Z)) 
            return x.add(this.toNewRing(Xtype, ring), ring);
    //    return add((NumberZp32) (x.toNumber(Ring.Zp32, ring)));
    }
    
    public NumberZp32 subtract(NumberZp32 x, Ring ring) {
        return new NumberZp32(value - x.value, ring);
    }

    @Override
    public Element multiply(Element x, Ring ring) {
        int Xtype=x.numbElementType();
        if (Xtype == Ring.Zp32) return multiply ((NumberZp32)x, ring);
        if(Xtype==0){ if (x == NAN) {return NAN;}
           if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) {
              return (this.isZero(ring)) ? NAN : this.isNegative() ? x.inverse(ring) : x;}}
      //  if ((Xtype != Ring.Z64)&&(Xtype != Ring.Z))
            return x.multiply(this.toNewRing(Xtype, ring), ring);
     //   return multiply((NumberZp32) (x.toNumber(Ring.Zp32, ring)));
    }
    
    
    
    
    public NumberZp32 multiply(NumberZp32 x, Ring ring) {
        return new NumberZp32(value * x.value, ring);
    }

    public NumberZp32 divide(NumberZp32 x, Ring ring) {
        long xVal=x.value;
        if (xVal!=ring.divizor32 )
           {int inv = inverse((int) xVal, (int) ring.MOD32);
        ring.divizor32=  xVal; ring.invDivisor32=  inv;}
        return new NumberZp32(value * ring.invDivisor32 % ring.MOD32);
    }

    @Override
    public Element divide(Element x, Ring ring) {
        int type = x.numbElementType();
        if (type == Ring.Zp32) return divide ((NumberZp32)x, ring);
        if (x instanceof Fraction) {Fraction fr = (Fraction) x;
            return this.multiply(fr.num, ring).divide(fr.denom, ring);}
        if (type >= Ring.Polynom)  return new Fraction(this, x);
        if (x == NAN) return NAN;
        if (x == NEGATIVE_INFINITY || x == POSITIVE_INFINITY) return ZERO;  
     //   if ((type != Ring.Z64)&&(type != Ring.Z)) 
            return x.divide(this.toNewRing(type, ring), ring);
     //   return divide((NumberZp32) (x.toNumber(Ring.Zp32, ring)));
    }
    @Override
    public Element divideExact(Element el, Ring ring) {
        return divide(el, ring);
    }

    /**
     * Сдвигает остаток от деления на модуль в в интервал [0..MOD-1]
     *
     * @return (value < 0)? new NumberZp3232(value-MOD) : this;
     */
    @Override
    public NumberZp32 mod(Ring ring) {
        return (value < 0) ? new NumberZp32(value + ring.MOD32, ring) : this;
    }

    /**
     * Сдвигает остаток от деления на нечетный модуль в в интервал
     * [-MOD/2..MOD/2]
     *
     * @return (value > MOD/2)? new NumberZp32(value-MOD): ....
     */
    @Override
    public Element Mod(Ring ring) {
        long m1 = (ring.MOD32 >> 1);
        return (value < -m1 ? new NumberZp32(value + ring.MOD32, ring)
                : (value > m1 ? (new NumberZp32(value - ring.MOD32, ring)) : this));
    }

    @Override
    public NumberZp32 valOf(double x, Ring ring) {
        return new NumberZp32((long) x, ring);
    }

    @Override
    public NumberZp32 valOf(int x, Ring ring) {
        return new NumberZp32((long) x, ring);
    }

    @Override
    public NumberZp32 valOf(long x, Ring ring) {
        return new NumberZp32(x, ring);
    }

    @Override
    public NumberZp32 valOf(String s, Ring ring) {
        return (new NumberZp32(s, ring));
    }

    /**
     * Transform this number of NumberR64 type to the new type fixed by
     * "numberType" defined by Ring.type
     *
     * @param numberType - new type
     * @return this transormed to the new type
     */  
    @Override
    public Element toNewRing(int Algebra, Ring r) {return toNumber(Algebra, r); }
    /**
     * Transform this number of NumberZ64 type to the new type fixed by
     * "numberType" defined by Ring.type
     *
     * @param numberType - new type
     *
     * @return this transormed to the new type
     */
    public Element toNumber(int numberType, Ring ring) {
        switch (numberType) {
            case Ring.Z:
                return new NumberZ(value);
            case Ring.Z64:
                return new NumberZ64(value);
            case Ring.Zp:
                return new NumberZp(new NumberZ(value), ring);
            case Ring.R:
                return new NumberR(value);
            case Ring.R64:
                return new NumberR64(value);
            case Ring.R128:
                return new NumberR128(value);
        }
        return this;
    }    
    
    @Override
    public NumberZp32 D(Ring r) {
        return ZERO;
    }

    @Override
    public NumberZp32 D(int num, Ring r) {
        return ZERO;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NumberZp32 other = (NumberZp32) obj;
        return true;
    }

    public int compareTo(NumberZp32 val, Ring ring) {
        long thisVal = this.mod(ring).value;
        long anotherVal = val.mod(ring).value;
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    @Override
    public int compareTo(Element val, Ring ring) {
        if (val.numbElementType() > com.mathpar.number.Ring.Zp32) {
            return 1;
        }
        if (val.numbElementType() < com.mathpar.number.Ring.Zp32) {
            return -1;
        }
        return compareTo((NumberZp32) val, ring);
    }

}
