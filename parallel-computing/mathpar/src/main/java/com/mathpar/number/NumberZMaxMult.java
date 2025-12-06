package com.mathpar.number;

import java.util.Random;

/**
 *
 * Class NumberZMaxMult provides troplcal operations over NumberZ number:
 *
 * @author pashin
 * @version 4.1 04/11/11
 * @since ParCA 4.1
 *
 */
public class NumberZMaxMult extends NumberZ {
    public static NumberZMaxMult ONE = new NumberZMaxMult(NumberZ.ONE);
    public static final Element ZERO = NEGATIVE_INFINITY;
    public static NumberZMaxMult MINUS_ONE = new NumberZMaxMult(NumberZ.MINUS_ONE);

    public NumberZMaxMult() {
    }

    @Override
    public NumberZMaxMult one(Ring ring) {
        return NumberZMaxMult.ONE;
    }

    @Override
    public NumberZMaxMult minus_one(Ring ring) {
        return new NumberZMaxMult(NumberZ.MINUS_ONE);
    }

    @Override
    public int numbElementType() {
        return Ring.ZMaxMult;
    }

    @Override
    public NumberZMaxMult zero(Ring ring) {
        return (NumberZMaxMult) ZERO;
    }

    @Override
    public NumberZMaxMult myOne(Ring ring) {
        return NumberZMaxMult.ONE;
    }
    //  public NumberZMaxPlus myMinus_one(Ring ring) {return NumberZMaxPlus.MINUS_ONE; }

    @Override
    public NumberZMaxMult myZero(Ring ring) {
        return (NumberZMaxMult) ZERO;
    }

    public NumberZMaxMult(NumberZ p) {
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZMaxMult(NumberR64 p) {
        this.signum = 0;
        this.mag = null;
    }

    public NumberZMaxMult(NumberZ p, Ring ring) {
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZMaxMult(String s, Ring ring) {
        NumberZ p = new NumberZ(s);
        this.signum = p.signum;
        this.mag = p.mag;
    }
    //  public NumberZMaxPlus negate(Ring  ring) {  return new NumberZMaxPlus( ((NumberZ)this).negate(),ring); }
    //  public NumberZMaxPlus add(NumberZMaxPlus x, Ring ring) { return new NumberZMaxPlus(add(x), ring); }

    @Override
    public Element add(Element val, Ring ring) {
        //NumberZMaxMult vval = (NumberZMaxMult) val;
        if ((val == NAN) || (val == NEGATIVE_INFINITY) || (val == POSITIVE_INFINITY)) {
            return (val == POSITIVE_INFINITY) ? val
                    : (val == NEGATIVE_INFINITY) ? this : NAN;
        }
        return (compareTo(val) > 0 ? this : val);
    }

    @Override
    public Element multiply(Element val, Ring ring) { //NumberZMaxMult vval=(NumberZMaxMult)val;
        if ((val == NAN) || (val == NEGATIVE_INFINITY) || (val == POSITIVE_INFINITY)) {
            return ((this == NAN) || (val == NAN))
                    ? NAN : (this == POSITIVE_INFINITY)
                    ? (val == NEGATIVE_INFINITY) ? NAN : this
                    : (this == NEGATIVE_INFINITY)
                    ? (val == POSITIVE_INFINITY) ? NAN : this
                    : (val == POSITIVE_INFINITY)
                    ? val
                    : (val == NEGATIVE_INFINITY) ? val : NAN;
        }
        return new NumberZMaxMult(((NumberZ) this).multiply((NumberZ) val));
    }

//    @Override
//     public NumberZMaxMult subtract(Element val, Ring ring) {
//        NumberZMaxMult vval=(NumberZMaxMult)val;
//       return vval;
//    }
    public NumberZMaxMult divide(Element val, Ring ring) {
        NumberZMaxMult vval = (NumberZMaxMult) val;
        return vval;
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
    public Element valOf(double x, Ring ring) {
        Element pp = NumberR.valueOf(x);
        if (pp instanceof NumberR) {
            return new NumberZMaxMult(((NumberR) NumberR.valueOf(x)).NumberRtoNumberZ(), ring);
        } else {
            return pp;
        }
    }

    @Override
    public NumberZMaxMult valOf(int x, Ring ring) {
        return new NumberZMaxMult((NumberZ.valueOf(x)), ring);
    }

    @Override
    public NumberZMaxMult valOf(long x, Ring ring) {
        return new NumberZMaxMult((NumberZ.valueOf(x)), ring);
    }

    @Override
    public NumberZMaxMult valOf(String s, Ring ring) {
        return (new NumberZMaxMult(s, ring));
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
    public NumberZMaxMult random(int[] randomType, Random rnd, Ring ring) {
        return new NumberZMaxMult(NumberZ.ONE.random(randomType, rnd, ring));
    }
    //  @Override
    //   public NumberZMaxPlus GCD(Element w,Ring r) {return ONE; }
    
    /**
     * Transform this number of NumberZMaxMult type to the new type fixed by
     * "numberType" defined by Ring.type
     *
     * @param numberType - new type
     * @param ring
     *
     * @return this transormed to the new type
     */
    @Override
    public Element toNumber(int numberType, Ring ring) {
        switch (numberType) {
            case Ring.Z:
                return new NumberZ(longValue());
            case Ring.Z64:
                return new NumberZ64(longValue());
            case Ring.Zp32:
                NumberZ x = (new NumberZ(longValue())).remainder(new NumberZ(ring.MOD32));
                return new NumberZp32(x, ring);
            case Ring.Zp: {
                NumberZ zz = (new NumberZ(longValue())).remainder(ring.MOD);
                return new NumberZp(zz);
            }
            case Ring.R:
                return new NumberR(new NumberZ(longValue()));
            case Ring.R64:
                return new NumberR64(doubleValue());
            case Ring.R128:
                return new NumberR128(new NumberZ(longValue()));
            case Ring.Q: case Ring.CQ:
                return (new Fraction(new NumberZ(longValue()), NumberZ.ONE));
            case Ring.CZ:
                return new Complex(new NumberZ(longValue()), NumberZ.ZERO);
            case Ring.CZp32:
                return new Complex(new NumberZp32(longValue(), ring),
                        NumberZp32.ZERO);
            case Ring.CZp:
                return new Complex(new NumberZp(new NumberZ(longValue()), ring), NumberZp.ZERO);
            case Ring.C:
                return new Complex(new NumberR(new NumberZ(longValue())), NumberR.ZERO);
            case Ring.C64:
                return new Complex(new NumberR64(doubleValue()), NumberR64.ZERO);
            case Ring.C128:
                return new Complex(new NumberR128(new NumberZ(longValue())), NumberR128.ZERO);
            case Ring.ZMaxPlus:
                return new NumberZMaxPlus(new NumberZ(longValue()));
            case Ring.ZMinPlus:
                return new NumberZMinPlus(new NumberZ(longValue()));
            case Ring.ZMaxMult:
                return this;
            case Ring.ZMinMult:
                return new NumberZMinMult(new NumberZ(longValue()));
            case Ring.ZMaxMin:
                return new NumberZMaxMin(new NumberZ(longValue()));
            case Ring.ZMinMax:
                return new NumberZMinMax(new NumberZ(longValue()));
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
            case Ring.RMaxPlus:
                return new NumberRMaxPlus(new NumberR(this));
            case Ring.RMinPlus:
                return new NumberRMinPlus(new NumberR(this));
            case Ring.RMaxMult:
                return new NumberRMaxMult(new NumberR(this));
            case Ring.RMinMult:
                return new NumberRMinMult(new NumberR(this));
            case Ring.RMaxMin:
                return new NumberRMaxMin(new NumberR(this));
            case Ring.RMinMax:
                return new NumberRMinMax(new NumberR(this));
        }
        return null;
    }
    
    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toNumber(Algebra, r);
    }
}
