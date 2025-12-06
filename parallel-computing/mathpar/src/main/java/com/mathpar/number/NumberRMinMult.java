package com.mathpar.number;

import java.util.Random;

/**
 * Class NumberRMaxPlus provides troplcal operations over NumberR number:
 *
 * @author gennadi
 * @version 4.1 04/11/11
 * @since ParCA 4.1
 */
public class NumberRMinMult extends NumberR {
    public static NumberRMinMult ONE = new NumberRMinMult(NumberR.ONE);
    public static Element ZERO = POSITIVE_INFINITY;
    public static Element NEGATIVE_INFINITY = NAN;
    public static Element MINUS_ONE = NAN;

    public NumberRMinMult() {
    }

    @Override
    public NumberRMinMult one(Ring ring) {
        return NumberRMinMult.ONE;
    }

    @Override
    public NumberRMinMult minus_one(Ring ring) {
        return new NumberRMinMult(NumberR.MINUS_ONE);
    }

    @Override
    public int numbElementType() {
        return Ring.RMinMult;
    }

    @Override
    public Element zero(Ring ring) {
        return NumberRMinMult.ZERO;
    }

    @Override
    public NumberRMinMult myOne(Ring ring) {
        return NumberRMinMult.ONE;
    }

    @Override
    public Element myZero(Ring ring) {
        return NumberRMinMult.ZERO;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return this == ring.numberZERO;
    }

    public NumberRMinMult(NumberR p) {
        this.intVal = p.intVal;
        this.scale = p.scale;
    }

    public NumberRMinMult(NumberR p, Ring ring) {
        this.intVal = p.intVal;
        this.scale = p.scale;
    }

    public NumberRMinMult(String s, Ring ring) {
        NumberR p = new NumberR(s);
        this.intVal = p.intVal;
        this.scale = p.scale;
    }

    @Override
    public Element add(Element val, Ring ring) {
        return (val == NAN) ? NAN
                : (this == POSITIVE_INFINITY) ? val
                : (val == POSITIVE_INFINITY) ? this : (compareTo(val, ring) > 0 ? this : val);
    }

    @Override
    public Element multiply(Element val, Ring ring) {
        if ((val == NAN) || (val == POSITIVE_INFINITY)) {
            return val;
        }
        return new NumberRMinMult((NumberR) ((NumberR) this).multiply((NumberR) val));
    }

    @Override
    public Element divide(Element val, Ring ring) {
        if(val==POSITIVE_INFINITY) return new NumberR64MinMult(NumberR64.ZERO);
        return new NumberRMinMult((NumberR) ((NumberR) this).divide((NumberR) val,ring));
    }

    @Override
    public Element inverse(Ring ring) {
        return (this == NAN) ? NAN
                : (this == ZERO) ? (new NumberRMinMult(NumberR.ZERO))
                : (this == POSITIVE_INFINITY) ? (new NumberRMinMult(NumberR.ZERO))
                : (this == (new NumberRMinMult(NumberR.ZERO))) ? POSITIVE_INFINITY
                : new NumberRMinMult((NumberR) (NumberR.ONE).divide((NumberR) this, ring.MC));
    }

    /**
     * Вычисляет число 1 + x + x^2 + ...
     * @param ring
     * @return
     */
    @Override
    public Element closure(Ring ring){
        if(this.compareTo(ring.numberONE, -1, ring)) return ONE;
        else return new NumberR64MinMult(NumberR64.ZERO);
    }

   @Override
    public int compareTo(Element val, Ring ring) {
        if ((val == NAN) || (this == NAN)) {
            return Integer.MAX_VALUE;
        }
        if (val == POSITIVE_INFINITY) {
            return (this == POSITIVE_INFINITY) ? 0 : 1;
        }
        if (this == POSITIVE_INFINITY) {
            return -1;
        }
        if (val == NEGATIVE_INFINITY) {
            return (this == NEGATIVE_INFINITY) ? 0 : -1;
        }
        if (this == NEGATIVE_INFINITY) {
            return 1;
        }
        int v = val.numbElementType();
        int m1 = Ring.R;
        int m2 = Ring.RMinMax;
        if (m1 > v) {      return -1;
        } else if (m2 < v) {return 1;
        } else {NumberR x = (NumberR) subtract((NumberR) val, ring);
                NumberR y = x.abs();
                if (y.compareTo(ring.MachineEpsilonR) < 0) {return 0; }
                return -x.signum();
        }
    }

    @Override
    public NumberRMinMult valOf(double x, Ring ring) {
        return this;
    }

    @Override
    public NumberRMinMult valOf(int x, Ring ring) {
        return this;
    }

    @Override
    public NumberRMinMult valOf(long x, Ring ring) {
        return new NumberRMinMult((NumberR.valueOf(x)), ring);
    }

    @Override
    public NumberRMinMult valOf(String s, Ring ring) {
        return (new NumberRMinMult(s, ring));
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
    public NumberRMinMult random(int[] randomType, Random rnd, Ring ring) {
        return new NumberRMinMult(NumberR.ONE.random(randomType, rnd, ring));
    }
    
    /**
     * Transform this number of NumberRMinMult type to the new type fixed by
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
                    return new NumberRMaxMult(new NumberR(doubleValue()));
                case Ring.RMinMult:
                    return this;
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
