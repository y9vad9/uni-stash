package com.mathpar.number;

/**
 * Class NumberZMaxPlus provides troplcal operations over NumberZ number:
 *
 * @author gennadi
 * @version 4.1 04/11/11
 * @since ParCA 4.1
 */
public class NumberZMaxPlus extends NumberZ {
    public static NumberZMaxPlus ONE = new NumberZMaxPlus(NumberZ.ZERO);
    public static Element ZERO = NEGATIVE_INFINITY;
    public static NumberZMaxPlus MINUS_ONE = new NumberZMaxPlus(NumberZ.MINUS_ONE);

    public NumberZMaxPlus() {
    }

    public NumberZMaxPlus(NumberR64 p) {
    }

    @Override
    public NumberZMaxPlus one(Ring ring) {
        return NumberZMaxPlus.ONE;
    }

    @Override
    public NumberZMaxPlus minus_one(Ring ring) {
        return new NumberZMaxPlus(NumberZ.MINUS_ONE);
    }

    @Override
    public int numbElementType() {
        return Ring.ZMaxPlus;
    }

    @Override
    public Element zero(Ring ring) {
        return NEGATIVE_INFINITY;
    }

    @Override
    public NumberZMaxPlus myOne(Ring ring) {
        return NumberZMaxPlus.ONE;
    }

    @Override
    public Element myZero(Ring ring) {
        return NEGATIVE_INFINITY;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return this == ring.numberZERO;
    }

    public NumberZMaxPlus(NumberZ p) {
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZMaxPlus(NumberZ p, Ring ring) {
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZMaxPlus(String s, Ring ring) {
        NumberZ p = new NumberZ(s);
        this.signum = p.signum;
        this.mag = p.mag;
    }

    @Override
    public Element add(Element val, Ring ring) {
        if (val.isNegativeInfinity()) {
            return this;
        }
        if (val.isPositiveInfinity()) {
            return POSITIVE_INFINITY;
        }
        if (val == NAN) {
            return NAN;
        }
        return (compareTo(val) > 0 ? this : val);
    }

    @Override
    public Element multiply(Element val, Ring ring) { //NumberZMaxPlus vval=(NumberZMaxPlus)val;
        if ((val.isNegativeInfinity()) || (val.isPositiveInfinity())) {
            return (isPositiveInfinity())
                    ? (val.isNegativeInfinity()) ? NAN : this
                    : (isNegativeInfinity())
                    ? (val.isPositiveInfinity()) ? NAN : this
                    : (val.isPositiveInfinity())
                    ? val
                    : (val.isNegativeInfinity()) ? val : NAN;
        }
        return (val.numbElementType() >= Ring.Polynom) ? val.multiply(this, ring) : new NumberZMaxPlus(((NumberZ) this).add((NumberZ) val));
    }

    @Override
    public Element divide(Element val, Ring ring) {
        if(val==NEGATIVE_INFINITY) return POSITIVE_INFINITY;
        return new NumberZMaxPlus(((NumberZ) this).subtract((NumberZ) val));
    }

    @Override
    public Element negate(Ring ring) {
        return new NumberZMaxPlus(new NumberZ(this.mag, -this.signum));
    }

    @Override
    public Element inverse(Ring ring) {
        return (this == NAN) ? NAN
                : (this == ZERO) ? POSITIVE_INFINITY
                : (this == POSITIVE_INFINITY) ? ZERO
                : new NumberZMaxPlus(((NumberZ) this).negate());
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
            return new NumberZMaxPlus(((NumberR) NumberR.valueOf(x)).NumberRtoNumberZ(), ring);
        } else {
            return pp;
        }
    }

    @Override
    public NumberZMaxPlus valOf(int x, Ring ring) {
        return new NumberZMaxPlus((NumberZ.valueOf(x)), ring);
    }

    @Override
    public NumberZMaxPlus valOf(long x, Ring ring) {
        return new NumberZMaxPlus((NumberZ.valueOf(x)), ring);
    }

    @Override
    public NumberZMaxPlus valOf(String s, Ring ring) {
        return (new NumberZMaxPlus(s, ring));
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
     * Transform this number of NumberZMaxPlus type to the new type fixed by
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
                return this;
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
