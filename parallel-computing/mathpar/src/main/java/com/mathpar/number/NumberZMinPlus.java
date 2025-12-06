package com.mathpar.number;

/**
 * Class NumberZMaxP provides troplcal operations over NumberZ number:
 *
 * @author gennadi
 * @version 4.1 04/11/11
 * @since ParCA 4.1
 */
public class NumberZMinPlus extends NumberZ {
    public static NumberZMinPlus ONE = new NumberZMinPlus(NumberZ.ZERO);
    public static Element ZERO = POSITIVE_INFINITY;
    public static NumberZMinPlus MINUS_ONE = new NumberZMinPlus(NumberZ.MINUS_ONE);

    public NumberZMinPlus() {
    }

    @Override
    public NumberZMinPlus one(Ring ring) {
        return NumberZMinPlus.ONE;
    }

    @Override
    public NumberZMinPlus minus_one(Ring ring) {
        return new NumberZMinPlus(NumberZ.MINUS_ONE);
    }

    @Override
    public int numbElementType() {
        return Ring.ZMinPlus;
    }

    @Override
    public Element zero(Ring ring) {
        return NumberZMinPlus.ZERO;
    }

    @Override
    public NumberZMinPlus myOne(Ring ring) {
        return NumberZMinPlus.ONE;
    }

    @Override
    public Element myZero(Ring ring) {
        return NumberZMinPlus.ZERO;
    }

    public NumberZMinPlus(NumberZ p) {
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZMinPlus(NumberZ p, Ring ring) {
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZMinPlus(String s, Ring ring) {
        NumberZ p = new NumberZ(s);
        this.signum = p.signum;
        this.mag = p.mag;
    }

    @Override
    public Element add(Element val, Ring ring) {
        if ((val == NAN) || (val == NEGATIVE_INFINITY) || (val == POSITIVE_INFINITY)) {
            return (val == POSITIVE_INFINITY) ? this
                    : (val == NEGATIVE_INFINITY) ? val : NAN;
        }
        return (compareTo(val) < 0 ? this : val);
    }

    @Override
    public Element multiply(Element val, Ring ring) { //NumberZMinPlus vval=(NumberZMinPlus)val;
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
        return new NumberZMinPlus(((NumberZ) this).add((NumberZ) val));
    }

    @Override
    public Element divide(Element val, Ring ring) {
        if (val==POSITIVE_INFINITY) return NEGATIVE_INFINITY;
        return new NumberZMinPlus(((NumberZ) this).subtract((NumberZ) val));
    }

    @Override
    public Element negate(Ring ring) {
        return new NumberZMinPlus(new NumberZ(this.mag, -this.signum));
    }

    @Override
    public Element inverse(Ring ring) {
        return (this == NAN) ? NAN
                : (this == ZERO) ? NEGATIVE_INFINITY
                : (this == NEGATIVE_INFINITY) ? ZERO
                : new NumberZMinPlus((NumberZ) ((NumberZ) this).negate(ring));
    }

    /**
     * Вычисляет число 1 + x + x^2 + ...
     * @param ring
     * @return
     */
    @Override
    public Element closure(Ring ring){
        if(this.compareTo(ring.numberONE, LESS_OR_EQUAL, ring)) return ONE;
        else return NEGATIVE_INFINITY;
    }

    @Override
    public int compareTo(Element val, Ring ring) {
        return -((NumberZ) this).compareTo(val);
    }

     @Override
    public Boolean isZero(Ring ring) {
        return this == ring.numberZERO;
    }

    @Override
    public Element valOf(double x, Ring ring) {
        Element pp = NumberR.valueOf(x);
        if (pp instanceof NumberR) {
            return new NumberZMinPlus(((NumberR) NumberR.valueOf(x)).NumberRtoNumberZ(), ring);
        } else {
            return pp;
        }
    }

    @Override
    public NumberZMinPlus valOf(int x, Ring ring) {
        return new NumberZMinPlus((NumberZ.valueOf(x)), ring);
    }

    @Override
    public NumberZMinPlus valOf(long x, Ring ring) {
        return new NumberZMinPlus((NumberZ.valueOf(x)), ring);
    }

    @Override
    public NumberZMinPlus valOf(String s, Ring ring) {
        return (new NumberZMinPlus(s, ring));
    }

    @Override
    public Element D(Ring r) {
        return ZERO;
    }

    @Override
    public Element D(int num, Ring r) {
        return ZERO;
    }
    
    /**
     * Transform this number of NumberZMinPlus type to the new type fixed by
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
                return this;
            case Ring.ZMaxMult:
                return new NumberZMaxMult(new NumberZ(longValue()));
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
