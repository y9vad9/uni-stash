package com.mathpar.number;

import com.mathpar.func.F;
import java.util.Random;

/**
 * <p>Title: ParCA</p> <p>Description: ParCA - parallel computer algebra
 * system</p> <p>Copyright: Copyright (c) ParCA Tambov, 2005-2007</p>
 * <p>Company: ParCA Tambov</p>
 */
public class NumberR128 extends Element {

//val
    public double val;
    public int exp;
    //11...1  00...0
    //  11  )(  52
    final static long MASK_POR = getMask1(62, 11);
    //1
    final static long SP = 0x3FF0000000000000L;
    //-1
    final static long SM = 0xBFF0000000000000L;
//    public static final NumberR128 POSITIVE_INFINITY = new NumberR128(1.0 / 0.0);
//    public static final NumberR128 NEGATIVE_INFINITY = new NumberR128(-1.0 / 0.0);
    public static final NumberR128 POSITIVE_ZERO = new NumberR128(1.0 / Double.POSITIVE_INFINITY);
    public static final NumberR128 NEGATIVE_ZERO = new NumberR128(1.0 / Double.NEGATIVE_INFINITY);
    //public static final NumberR128 NaN = new NumberR128(0.0d / 0.0);
    public static final NumberR128 eps = new NumberR128(0.001);
    public static final NumberR128 ONE = new NumberR128(1.0);
    public static final NumberR128 ZERO = new NumberR128(0.0);

    public NumberR128() {
        this.val = 0.0;
        this.exp = 0;
    }

    public NumberR128(double val, int exp) {
        if (val == 0 || val == -0 || val == Double.NEGATIVE_INFINITY || val == Double.POSITIVE_INFINITY || val == Double.NaN) {
            this.val = val;
            this.exp = 0;
        } else {
            this.val = val;
            this.exp = exp;
        }
    }

    public NumberR128(double val) {
        this.val = val;
        this.exp = 0;
    }

    public NumberR128(NumberR a) {
        NumberR128 b = new NumberR128(a.intVal, a.scale);
        exp = b.exp;
        val = b.val;
    }

    public NumberR128(NumberZ a) {
        NumberR128 b = new NumberR128(a, 0);
        exp = b.exp;
        val = b.val;
    }

    public NumberR128(NumberZ c, int n) {
        if (c.compareTo(NumberZ.ZERO) == 0) {
            val = 0;
            exp = 0;
        } else {
            int[] bits = c.mag;
            //i-позиция после 1-й 1
            int i = c.posOfHighestBit() - 2;
            long a = 0;
            //мантисса
            a = copyBits(bits, i, 52, a, 51);
            //знак //порядок
            a = (c.signum() < 0) ? a | SM : a | SP;
            val = Double.longBitsToDouble(a);
            exp = i + 1 + n;
        }
    }

    @Override
    public int numbElementType() {
        return Ring.R128;
    }

    @Override
    public double doubleValue() {
        if (val == 0) {
            return 0;
        }
        long a = Double.doubleToRawLongBits(val);
        long b = ((a & MASK_POR) >> 52) + exp;
        //2^(-500)->0, а не бесконечность!!!
        //если b<1, то 0
        if (b > 2044 || b < 1) {
            if (val > 0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }
        b <<= 52;
        a &= ~MASK_POR;
        a |= b;
        return Double.longBitsToDouble(a);
    }

    /**NumberR128 negative constant
     * Initialize   constant array when class is loaded.
     * @return NumberR64 negative constant 
     */
    @Override
    public  Element[] negConst() {
        NumberR128 negConst[] = new NumberR128[MAX_CONSTANT + 1];
        for (int i = 1; i <= Element.MAX_CONSTANT; i++) {negConst[i] = new NumberR128(i*(-1.0)); }
        return negConst;
    }    
    /**Number128 positive constant
     * Initialize   constant array when class is loaded.
     * @return NumberR64 positive constant 
     */
    @Override
    public  Element[] posConst() {
        NumberR128 posConst[] = new NumberR128[MAX_CONSTANT + 1];
        for (int i = 0; i <= Element.MAX_CONSTANT; i++) {posConst[i] = new NumberR128(i*1.0); }
        return posConst;
    }
    
    /**
     * 0...01...10....0 --n-- i
     *
     * @param i int i>=n
     * @param n int n>0
     * @return long
     */
    public static long getMask1(int i, int n) {
        long a = (1 << n) - 1;
        a <<= i - (n - 1);
        return a;
    }

    //копирует n бит из массива bits, начиная с i-го
    //возможно частный случай следующей процедуры, когда в массиве один элемент
    public static long copyBits(int b, int i, int n, long a, int j) {
        long c = b & getMask1(i, n);
        if (i > j) {
            c >>= (i - j);
        } else if (i < j) {
            c <<= (j - i);
        }
        a |= c;
        return a;
    }

    /**
     * копирует n бит из массива b, начиная с i-го
     *
     * @param b int[]
     * @param i int
     * @param n int
     * @param a long
     * @param j int
     * @return long
     */
    public static long copyBits(int[] b, int i, int n, long a, int j) {
        int s = b.length;
        int i1 = s - 1 - i / 32;
        int i2 = i % 32;
        int l = n;
        //1) b[i1],i2,i2+1 ---> a,j
        a = copyBits(b[i1], i2, i2 + 1, a, j);
        i1++;
        j += (i2 + 1);
        l -= (i2 + 1);
        if (i + 1 > 51) {
            //2) b[i1],31,32 ---> a,j
            if (l >= 32) {
                a = copyBits(b[i1], 31, 32, a, j);
                i1++;
                j += 32;
                l -= 32;
            }

            //l<32
            //3) b[i1],31,l ---> a,j
            a = copyBits(b[i1], 31, l, a, j);
        }
        return a;
    }

    public NumberR128 myOne() {
        return ONE;
    }

    @Override
    public NumberR128 myOne(Ring ring) {
        return ONE;
    }

    @Override
    public NumberR128 myMinus_one() {
        return new NumberR128(-1.0, 0);
    }

    @Override
    public NumberR128 myZero() {
        return ZERO;
    }

    @Override
    public NumberR128 myZero(Ring r) {
        return ZERO;
    }

    public NumberR128 minus_one() {
        return new NumberR128(-1.0, 0);
    }

    public NumberR128 one() {
        return ONE;
    }

    public NumberR128 zero() {
        return ZERO;
    }

    /*
     *
     */
    @Override
    public NumberR128 rootOf(int pow, Ring r) {
        // if (this.val<0)return null;
        int newExp = this.exp / pow;
        double newVal = Math.pow(Math.abs(this.val), 1.0 / ((double) pow)) * Math.pow(2.0, ((double) (this.exp % pow) / ((double) pow)));
        return new NumberR128(newVal, newExp);
    }

    @Override
    public Element GCD(Element x, Ring ring) {
        return ONE;
    }

    public NumberR128 random(int[] randomType, Random rnd, Element itsCoeffOne) {
        int nbits = randomType[randomType.length - 2];
        return new NumberR128((long) rnd.nextInt(1 << nbits));
    }

    public NumberR RValue() {
        return (NumberR)(new NumberR(val)).multiply((new NumberR(2)).powExact(exp));
    }

    public NumberZ ZValue() {
        return ((RValue()).NumberRtoNumberZ());
    }

    // public number divide(number x, int i, RoundingMode r){return new NumberL(((Double)c).divide((Double)x.c));}
    /*public NumberR128 pow(int exp){return new NumberR128(0L);}
     public NumberR128 negate(){return new NumberR128(-val);}
     public NumberR128 abs(){return new NumberR128 (Math.abs((val)));}

     public NumberR128 add(Element x){return  ZERO;}
     public NumberR128 subtract(Element x){return ZERO;}
     public NumberR128 multiply(Element x){return  ZERO;}
     public NumberR128 multiply(Element x, int i){return multiply(x);}
     public NumberR128 divide(Element x){return  ZERO;}*/
    public NumberR128 Normalize() {
        //  Double y = 1.0;
        //  long z = Double.doubleToLongBits(y);
        //  String s = ConvToHex(z);
        long d1 = Double.doubleToRawLongBits(val);
        if ((this.val == 0)) {
            val = 0;
            exp = 0;
        } else {
            long d2 = (d1 >>> 52) & 0x7ff;
            exp += (int) (d2 - 0x3ffL);
            d1 = (d1 & 0x800fffffffffffffL);
            d1 = (d1 | (0x3ff0000000000000L));
            val = Double.longBitsToDouble(d1);
        }
        return new NumberR128(val, exp);
    }

    public NumberR128 abs() {
        return new NumberR128(Math.abs(this.val), exp);
    }

    public NumberR128 neg() {
        return new NumberR128(-this.val, exp);
    }

    public NumberR128 divide(NumberR128 a) {
        return new NumberR128(this.val / a.val, this.exp - a.exp);
    }

    public NumberR128 divide(NumberR128 a, Ring R) {
        return new NumberR128(this.val / a.val, this.exp - a.exp);
    }

    public NumberR128 multiply(NumberR128 a) {
        return new NumberR128(this.val * a.val, this.exp + a.exp);
    }

    public NumberR128 multiply(NumberR128 a, Ring R) {
        return new NumberR128(this.val * a.val, this.exp + a.exp);
    }

    public NumberR128 pow(int a) {
        return new NumberR128(Math.pow(this.val, a), this.exp * a);
    }

    public int CompareTo(NumberR128 a) {
        if (this.subtract(a).val == 0) {
            return 0;
        } else if (this.subtract(a).val > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NumberR128) {
            return equals((NumberR128) obj);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.val) ^ (Double.doubleToLongBits(this.val) >>> 32));
        hash = 67 * hash + this.exp;
        return hash;
    }

    @Override
    public boolean equals(Element x, Ring r) {
        if (x instanceof NumberR128) {
            NumberR128 D1 = this.Normalize();
            NumberR128 D2 = ((NumberR128) x).Normalize();
            if (D1.exp != D2.exp) {
                return false;
            }
            return new NumberR64(D1.val).equals(new NumberR64(D2.val), r);
        }
        return false;
    }

    public boolean equals(NumberR128 a) {
        NumberR128 D1 = this.Normalize();
        NumberR128 D2 = a.Normalize();
        if (D1.exp != D2.exp) {
            return false;
        }
        return NumberR64.doubleToLongBits(D1.val) == NumberR64.doubleToLongBits(D2.val);

    }

    public boolean isZero(NumberR128 eps) {
        return (this.abs().CompareTo(eps) == -1);
    }

    public boolean isZero(double eps) {
        NumberR128 DEExp = new NumberR128(eps);
        return (this.abs().CompareTo(DEExp) == -1);
    }

    public boolean isZero() {
        return (this.abs().CompareTo(eps) == -1);
    }

    public NumberR128 expchange(int e) {
        this.exp -= e;
        long d1 = Double.doubleToRawLongBits(val);
        long d2 = (d1 >>> 52) & 0x7ff;
        d2 += e;
        d2 = d2 << 52;
        d1 = (d1 & 0x800fffffffffffffL);
        d1 = (d1 | d2);
        val = Double.longBitsToDouble(d1);
        return this;
    }

    public int fullexp() {
        return (int) (Double.doubleToRawLongBits(this.val) >>> 52 & 0x7ff) - 0x3ff + this.exp;
    }

    public NumberR128[] AddOper(NumberR128 b) {
        if (Math.abs(this.fullexp() - b.fullexp()) <= 0x3ff) {
            if (this.exp != b.exp) {
                int diff = Math.abs(this.exp - b.exp);
                if (this.exp > b.exp) {
                    int texp = (int) ((Double.doubleToRawLongBits(b.val) >>> 52) & 0x7ff) - 0x3ff;
                    if (diff > texp) {
                        b.expchange(-texp);
                        this.expchange(diff - texp);
                    } else {
                        b.expchange(-diff);
                    }
                } else {
                    int texp = (int) ((Double.doubleToRawLongBits(this.val) >>> 52) & 0x7ff) - 0x3ff;
                    if (diff > texp) {
                        this.expchange(-texp);
                        b.expchange(diff - texp);
                    } else {
                        this.expchange(-diff);
                    }
                }
            }
        } else {
            if (this.fullexp() > b.fullexp()) {
                b.val = 0;
            } else {
                this.val = 0;
                this.exp = b.exp;
            }
        }
        NumberR128 ab[] = new NumberR128[2];
        ab[0] = this;
        ab[1] = b;
        return ab;
    }

    @Override
    public Element add(Element x, Ring ring) {
        if(x==NAN) return NAN;
        if ((x==NEGATIVE_INFINITY)||(x==POSITIVE_INFINITY)) return x;
        if (x.numbElementType() > com.mathpar.number.Ring.R128) {
            return x.add(this, ring);
        }
        if (x.numbElementType() == com.mathpar.number.Ring.R128) {
            return add((NumberR128) x);
        }
        return add((NumberR128) x.toNumber(Ring.R128, ring));
    }

    @Override
    public Element multiply(Element x, Ring ring) {
        if(x==NAN) return NAN;
        if ((x==NEGATIVE_INFINITY)||(x==POSITIVE_INFINITY)) return (this.isZero(ring))? NAN: this.isNegative()? x.inverse(ring):x;
        if (x.numbElementType() > com.mathpar.number.Ring.R128) {
            return x.multiply(this, ring);
        }
        if (x.numbElementType() == com.mathpar.number.Ring.R128) {
            return multiply((NumberR128) x);
        }
        return multiply((NumberR128) x.toNumber(Ring.R128, ring));
    }

    @Override
    public Element divide(Element x, Ring ring) {
        if(x==NAN) return NAN;
        if ((x==NEGATIVE_INFINITY)||(x==POSITIVE_INFINITY)) return  ZERO;
        if (x.isOne(ring)) {
            return this;
        }
        if (x.isMinusOne(ring)) {
            return negate(ring);
        }
        if (x instanceof NumberR128) {
            return divide((NumberR128) x);
        }
        return (x.numbElementType() < com.mathpar.number.Ring.NumberOfSimpleTypes) ? divide((NumberR128) x.toNumber(
                Ring.R128, ring)) : new F(F.DIVIDE, new Element[]{this, x});
    }

    @Override
    public Element subtract(Element x, Ring ring) {
        if(x==NAN) return NAN;
        if ((x==NEGATIVE_INFINITY)||(x==POSITIVE_INFINITY)) return x.negate(ring);
        switch (x.numbElementType()) {
            case com.mathpar.number.Ring.Polynom:
                return add(x.negate(ring), ring);
            case com.mathpar.number.Ring.F:
                return new F(com.mathpar.func.F.ADD, new Element[]{this, x.negate(ring)});
            default:
                if (x.numbElementType() > com.mathpar.number.Ring.R128) {
                    return x.subtract(this, ring).negate(ring);
                }
                if (x.numbElementType() == com.mathpar.number.Ring.R128) {
                    return subtract((NumberR128) x);
                }
                return subtract((NumberR128) x.toNumber(Ring.R128, ring));

        }
    }

    public NumberR128 add(NumberR128 a, Ring R) {
        NumberR128 DExp1 = new NumberR128(this.val, this.exp);
        NumberR128 DExp2 = new NumberR128(a.val, a.exp);
        NumberR128 mas[] = new NumberR128[2];
        mas = DExp1.AddOper(DExp2);
        return new NumberR128(mas[0].val + mas[1].val, mas[0].exp);
    }

    public NumberR128 add(NumberR128 a) {
        NumberR128 DExp1 = new NumberR128(this.val, this.exp);
        NumberR128 DExp2 = new NumberR128(a.val, a.exp);
        NumberR128 mas[] = new NumberR128[2];
        mas = DExp1.AddOper(DExp2);
        return new NumberR128(mas[0].val + mas[1].val, mas[0].exp);
    }

    public NumberR128 subtract(NumberR128 a, Ring R) {
        NumberR128 DExp1 = new NumberR128(this.val, this.exp);
        NumberR128 DExp2 = new NumberR128(a.val, a.exp);
        NumberR128 mas[] = new NumberR128[2];
        mas = DExp1.AddOper(DExp2);
        return new NumberR128(mas[0].val - mas[1].val, mas[0].exp);
    }

    public NumberR128 subtract(NumberR128 a) {
        NumberR128 DExp1 = new NumberR128(this.val, this.exp);
        NumberR128 DExp2 = new NumberR128(a.val, a.exp);
        NumberR128 mas[] = new NumberR128[2];
        mas = DExp1.AddOper(DExp2);
        return new NumberR128(mas[0].val - mas[1].val, mas[0].exp);
    }

    public int signum() {
        if (val == 0) {
            return 0;
        }
        return (val > 0) ? 1 : -1;
    }

    @Override
    public String toString() {
        //return(""+val+"E"+exp);
        return (val + "*2^" + exp);
    }

    @Override
    public String toString(Ring r) {
        //return(""+val+"E"+exp);
        if (exp == 0) {
            return "" + val;
        }
        return ("(" + val + "*2^" + exp + ")");
    }

    public String toString(int i) {
        return (toString());
    }

    //public boolean isZero(){return (val==0D);}
    public static NumberR128 valOf(NumberZ x) {
        return new NumberR128(x, 0);
    }

    public NumberR128 valOf(int x) {
        return new NumberR128((double) x);
    }

    public NumberR128 valOf(long x) {
        return new NumberR128((double) x);
    }

    public NumberR128 valOf(double x) {
        return new NumberR128(x);
    }

    public NumberR128 valOf(NumberR x) {
        return new NumberR128(x);
    }

    public NumberR128 valOf(String s) {
        return valOf(s);
    }

    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    @Override
    public int intValue() {
        return (int) doubleValue();
    }

// an example -> public Double sin(){ return new Double(Math.sin(val));}
    @Override
    public Element D(Ring ring) {
        return ZERO;
    }

    @Override
    public Element D(int num, Ring ring) {
        return ZERO;
    }

    public int compareTo(Element x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isZero(Ring r) {
        double e = r.MachineEpsilonR64.value;
        double val1;
        if (exp != 0) {
            val1 = Normalize().val;
        } else {
            val1 = val;
        }
        return ((val1 < e) && (val1 > -e)) ? true : false;
    }

    @Override
    public Boolean isMinusOne(Ring r) {
        double val1;
        if (exp != 0) {
            val1 = Normalize().val;
        } else {
            val1 = val;
        }
        double v = val1 + 1.0;
        double e = r.MachineEpsilonR64.value;
        return ((v < e) && (v > -e)) ? true : false;
    }

    @Override
    public Boolean isOne(Ring r) {
        double val1;
        if (exp != 0) {
            val1 = Normalize().val;
        } else {
            val1 = val;
        }
        double v = val1 - 1.0;
        double e = r.MachineEpsilonR64.value;
        return ((v < e) && (v > -e)) ? true : false;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toNumber(Algebra, r);
    }
}
