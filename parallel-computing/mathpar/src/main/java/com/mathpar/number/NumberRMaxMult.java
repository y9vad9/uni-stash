package com.mathpar.number;

import java.util.Random;

/**
 * Class NumberRMaxMult provides troplcal operations over NumberR number:
 *
 * @author gennadi
 * @version 4.1 04/11/11
 * @since ParCA 4.1
 */
public class NumberRMaxMult extends NumberR {
    public static NumberRMaxMult ONE = new NumberRMaxMult(NumberR.ONE);
    public static NumberRMaxMult ZERO = new NumberRMaxMult(NumberR.ZERO);
    public static Element NEGATIVE_INFINITY = NAN;
    public static Element MINUS_ONE = NAN;

    public NumberRMaxMult() {
    }

    @Override
    public NumberRMaxMult one(Ring ring) {
        return NumberRMaxMult.ONE;
    }

    @Override
    public NumberRMaxMult minus_one(Ring ring) {
        return new NumberRMaxMult(NumberR.MINUS_ONE);
    }

    @Override
    public int numbElementType() {
        return Ring.RMaxMult;
    }

    @Override
    public NumberRMaxMult zero(Ring ring) {
        return NumberRMaxMult.ZERO;
    }

    @Override
    public NumberRMaxMult myOne(Ring ring) {
        return NumberRMaxMult.ONE;
    }

    @Override
    public NumberRMaxMult myZero(Ring ring) {
        return NumberRMaxMult.ZERO;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return this == ring.numberZERO;
    }

    public NumberRMaxMult(NumberR p) {
        this.intVal = p.intVal;
        this.scale = p.scale;
    }

    public NumberRMaxMult(NumberR p, Ring ring) {
        this.intVal = p.intVal;
        this.scale = p.scale;
    }

    public NumberRMaxMult(String s, Ring ring) {
        NumberR p = new NumberR(s);
        this.intVal = p.intVal;
        this.scale = p.scale;
    }

    @Override
    public Element add(Element val, Ring ring) {
        return (val == NAN) ? NAN
                : (this == POSITIVE_INFINITY || val == POSITIVE_INFINITY) ? POSITIVE_INFINITY
                : (compareTo(val, ring) > 0 ? this : val);
    }

    @Override
    public Element multiply(Element val, Ring ring) {
        if ((val == NAN) || (val == POSITIVE_INFINITY)) {
            return val;
        }
        return new NumberRMaxMult((NumberR) ((NumberR) this).multiply((NumberR) val));
    }

    @Override
    public Element divide(Element val, Ring ring) {
        if(val==ZERO) return POSITIVE_INFINITY;
        return new NumberRMaxMult((NumberR) ((NumberR) this).divide((NumberR) val,ring));
    }

    @Override
    public Element inverse(Ring ring) {
        return (this == NAN) ? NAN
                : (this == ZERO) ? POSITIVE_INFINITY
                : (this == POSITIVE_INFINITY) ? ZERO
                : new NumberRMaxMult((NumberR) (NumberR.ONE).divide((NumberR) this, ring.MC));
    }

    /**
     * Вычисляет число 1 + x + x^2 + ...
     * @param ring
     * @return
     */
    @Override
    public Element closure(Ring ring){
        if(this.compareTo(ring.numberONE, -1, ring)) return ONE;
        else return POSITIVE_INFINITY;
    }

    @Override
    public NumberRMaxMult valOf(double x, Ring ring) {
        return this;
    }

    @Override
    public NumberRMaxMult valOf(int x, Ring ring) {
        return this;
    }

    @Override
    public NumberRMaxMult valOf(long x, Ring ring) {
        return new NumberRMaxMult((NumberR.valueOf(x)), ring);
    }

    @Override
    public NumberRMaxMult valOf(String s, Ring ring) {
        return (new NumberRMaxMult(s, ring));
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
    public NumberRMaxMult random(int[] randomType, Random rnd, Ring ring) {
        return new NumberRMaxMult(NumberR.ONE.random(randomType, rnd, ring));
    }
    
    /**
     * Transform this number of NumberRMaxMult type to the new type fixed by
     * "numberType" defined by Ring.type
     *
     * @param numberType - new type
     * @param ring
     *
     * @return this transormed to the new type
     */
    @Override
    public Element toNumber(int numberType, Ring ring) {
        if (numberType < Ring.Polynom) {
            switch (numberType) {
                case Ring.Z:
                    return NumberRtoNumberZ();
                case Ring.Q:
                    return NumberRtoNumberQ().cancel(ring);
                case Ring.Z64:
                    return new NumberZ64(longValue());
                case Ring.Zp32:
                    return new NumberZp32(longValue(), ring);
                case Ring.Zp:
                    return new NumberZp(NumberRtoNumberZ(), ring);
                case Ring.R:
                    return new NumberR(doubleValue());
                case Ring.R64:
                    return new NumberR64(doubleValue());
                case Ring.R128:
                    return new NumberR128(intVal, scale);
                case Ring.RMaxPlus:
                    return new NumberRMaxPlus(new NumberR(doubleValue()));
                case Ring.RMinPlus:
                    return new NumberRMinPlus(new NumberR(doubleValue()));
                case Ring.RMaxMult:
                    return this;
                case Ring.RMinMult:
                    return new NumberRMinMult(new NumberR(doubleValue()));
                case Ring.RMaxMin:
                    return new NumberRMaxMin(new NumberR(doubleValue()));
                case Ring.RMinMax:
                    return new NumberRMinMax(new NumberR(doubleValue()));
                case Ring.R64MaxPlus:
                    return new NumberR64MaxPlus(new NumberR64(doubleValue()));
                case Ring.R64MinPlus:
                    return new NumberR64MinPlus(new NumberR64(doubleValue()));
                case Ring.R64MaxMult:
                    return new NumberR64MaxMult(new NumberR64(doubleValue()));
                case Ring.R64MinMult:
                    return new NumberR64MinMult(new NumberR64(doubleValue()));
                case Ring.R64MaxMin:
                    return new NumberR64MaxMin(new NumberR64(doubleValue()));
                case Ring.R64MinMax:
                    return new NumberR64MinMax(new NumberR64(doubleValue()));
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
                default:
                    return new Complex(this, this.zero(ring));
            }
        } else if (numberType < Ring.Polynom) {
            Element re = toNumber(numberType - Ring.Complex, ring);
            return new Complex(this, this.zero(ring));
        }

        return null;
    }
    
    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toNumber(Algebra, r);
    }
}
