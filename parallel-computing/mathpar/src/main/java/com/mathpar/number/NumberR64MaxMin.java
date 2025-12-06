package com.mathpar.number;

import java.util.Random;

/**
 * Class NumberR64MaxMin provides troplcal operations over NumberR number:
 *
 * @author gennadi
 * @version 4.1 04/11/11
 * @since ParCA 4.1
 */
public class NumberR64MaxMin extends NumberR64 {
    public static Element ONE = POSITIVE_INFINITY;
    public static Element ZERO = NEGATIVE_INFINITY;
    public static NumberR64MaxMin MINUS_ONE = new NumberR64MaxMin(NumberR64.MINUS_ONE);

    public NumberR64MaxMin() {
    }

    @Override
    public Element one(Ring ring) {
        return NumberR64MaxMin.ONE;
    }

    @Override
    public NumberR64MaxMin minus_one(Ring ring) {
        return new NumberR64MaxMin(NumberR64.MINUS_ONE);
    }

    @Override
    public int numbElementType() {
        return Ring.R64MaxMin;
    }

    @Override
    public Element zero(Ring ring) {
        return NumberR64MaxMin.ZERO;
    }

    @Override
    public Element myOne(Ring ring) {
        return NumberR64MaxMin.ONE;
    }

    @Override
    public Element myZero(Ring ring) {
        return NumberR64MaxMin.ZERO;
    }

    public NumberR64MaxMin(NumberR64 p) {
        this.value = p.value;
        this.local_En = p.local_En;
    }

    public NumberR64MaxMin(NumberR64 p, Ring ring) {
        this.value = p.value;
        this.local_En = p.local_En;
    }

    public NumberR64MaxMin(String s, Ring ring) {
        NumberR64 p = new NumberR64(s);
        this.value = p.value;
        this.local_En = p.local_En;
    }

     @Override
    public Element add(Element val, Ring ring) {
        return (val == NAN) ? NAN
                : (this == POSITIVE_INFINITY || val == POSITIVE_INFINITY) ? POSITIVE_INFINITY
                : (this == NEGATIVE_INFINITY) ? val
                : (val == NEGATIVE_INFINITY) ? this : (compareTo(val, ring) > 0 ? this : val);
    }

    @Override
    public Element multiply(Element val, Ring ring) {
        return (val == NAN) ? NAN
                : (this == NEGATIVE_INFINITY || val == NEGATIVE_INFINITY) ? NEGATIVE_INFINITY
                : (this == POSITIVE_INFINITY) ? val
                : (val == POSITIVE_INFINITY) ? this : (compareTo(val, ring) < 0 ? this : val);
    }

    @Override
    public NumberR64MaxMin divide(Element val, Ring ring) {
        return this;
    }

    @Override
    public NumberR64MaxMin negate(Ring ring) {
        return new NumberR64MaxMin(new NumberR64(-value));
    }

    /**
     * Вычисляет число 1 + x + x^2 + ...
     * @param ring
     * @return
     */
    @Override
    public Element closure(Ring ring){
        return ring.numberONE;
    }

    @Override
    public NumberR64MaxMin valOf(double x, Ring ring) {
        return this;
    }

    @Override
    public NumberR64MaxMin valOf(int x, Ring ring) {
        return this;
    }

    @Override
    public NumberR64MaxMin valOf(long x, Ring ring) {
        return new NumberR64MaxMin((NumberR64.valueOf(x)), ring);
    }

    @Override
    public NumberR64MaxMin valOf(String s, Ring ring) {
        return (new NumberR64MaxMin(s, ring));
    }

    @Override
    public Element D(Ring r) {
        return ZERO;
    }

    @Override
    public Element D(int num, Ring r) {
        return ZERO;
    }

    @Override
    public NumberR64MaxMin random(int[] randomType, Random rnd, Ring ring) {
        return new NumberR64MaxMin(NumberR64.ONE.random(randomType, rnd, ring));
    }
    
    /**
     * Transforms this number of NumberR64MaxMin type to given type defined in Ring.
     *
     * @param numberType new type
     * @param ring
     *
     * @return this transormed to the new type
     */
    @Override
    public Element toNumber(int numberType, Ring ring) {
        if (numberType < Ring.Complex) {
            switch (numberType) {
                case Ring.Z:
                    return (new NumberR(value)).NumberRtoNumberZ();
                case Ring.Z64:
                    return new NumberZ64(longValue());
                case Ring.Zp32:
                    return new NumberZp32(longValue());
                case Ring.Zp:
                    return new NumberZp((new NumberR(value)).NumberRtoNumberZ(), ring);
                case Ring.R:
                    return new NumberR(value);
                case Ring.R64:
                    return new NumberR64(value);
                case Ring.R128:
                    return new NumberR128(value);
                case Ring.C64:
                    return new Complex(value);
                case Ring.Q:
                    double denom = Math.pow(10, ring.FLOATPOS);
                    double dd = value * denom;
                    NumberZ n = (new NumberR(dd)).NumberRtoNumberZ();
                    NumberZ d = (new NumberR(denom)).NumberRtoNumberZ();
                    return new Fraction(n, d).cancel(ring);
                case Ring.R64MaxPlus:
                    return new NumberR64MaxPlus(new NumberR64(value));
                case Ring.R64MinPlus:
                    return new NumberR64MinPlus(new NumberR64(value));
                case Ring.R64MaxMult:
                    return new NumberR64MaxMult(new NumberR64(value));
                case Ring.R64MinMult:
                    return new NumberR64MinMult(new NumberR64(value));
                case Ring.R64MaxMin:
                    return this;
                case Ring.R64MinMax:
                    return new NumberR64MinMax(new NumberR64(value));
                case Ring.RMaxPlus:
                    return new NumberRMaxPlus(new NumberR(value));
                case Ring.RMinPlus:
                    return new NumberRMinPlus(new NumberR(value));
                case Ring.RMaxMult:
                    return new NumberRMaxMult(new NumberR(value));
                case Ring.RMinMult:
                    return new NumberRMinMult(new NumberR(value));
                case Ring.RMaxMin:
                    return new NumberRMaxMin(new NumberR(value));
                case Ring.RMinMax:
                    return new NumberRMinMax(new NumberR(value));
                case Ring.ZMaxPlus:
                    return new NumberZMaxPlus(new NumberZ(longValue()));
                case Ring.ZMinPlus:
                    return new NumberZMinPlus(new NumberZ(longValue()));
                case Ring.ZMaxMult:
                    return new NumberZMaxMult(new NumberZ(longValue()));
                case Ring.ZMinMult:
                    return new NumberZMinMult(new NumberZ(longValue()));
                case Ring.ZMaxMin:
                    return new NumberZMaxMin(new NumberZ(longValue()));
                case Ring.ZMinMax:
                    return new NumberZMinMax(new NumberZ(longValue()));
            }
        } else if (numberType < Ring.Polynom) {
            Element re = toNumber(numberType - Ring.Complex, ring);
            return new Complex(re, re.zero(ring));
        }
        return null;
    }
    
    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toNumber(Algebra, r);
    }
}
