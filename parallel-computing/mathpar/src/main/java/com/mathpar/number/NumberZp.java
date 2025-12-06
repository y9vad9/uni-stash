package com.mathpar.number;

//import func.F;

/**
 * Class NumberZp provides an modular operations over NumberZ number:
 *
 * add mod p; subtract mod p; multiply mod p; divide mod p (where p is a prime
 * number)
 *
 * @author gennadi
 * @version 3.0 09/09/09
 * @since ParCA 2.0
 */
public class NumberZp extends NumberZ {

    public NumberZp() {
    }
    public static NumberZp ONE = new NumberZp(NumberZ.ONE);
    public static NumberZp ZERO = new NumberZp(NumberZ.ZERO);
    public static NumberZp MINUS_ONE = new NumberZp(NumberZ.MINUS_ONE);

    @Override
    public NumberZp one(Ring ring) {
        return NumberZp.ONE;
    }

    @Override
    public NumberZp minus_one(Ring ring) {
        return NumberZp.MINUS_ONE;
    }

    @Override
    public int numbElementType() {
        return Ring.Zp;
    }

    @Override
    public NumberZp zero(Ring ring) {
        return NumberZp.ZERO;
    }

    @Override
    public NumberZp myOne(Ring ring) {
        return NumberZp.ONE;
    }

    @Override
    public NumberZp myMinus_one(Ring ring) {
        return NumberZp.MINUS_ONE;
    }

    @Override
    public NumberZp myZero(Ring ring) {
        return NumberZp.ZERO;
    }

    public NumberZp(NumberZ p) {
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZp(NumberZ p, Ring ring) {
        p = p.remainder(ring.MOD);
        this.signum = p.signum;
        this.mag = p.mag;
    }

    public NumberZp(String s, Ring ring) {
        NumberZ p = new NumberZ(s);
        p = p.remainder(ring.MOD);
        this.signum = p.signum;
        this.mag = p.mag;
    }

    @Override
    public NumberZp negate(Ring ring) {
        return new NumberZp(((NumberZ) this).negate(), ring);
    }

    @Override
    public Element add(Element x, Ring ring) {
        int Xtype=x.numbElementType();
        if (Xtype == Ring.Zp) return add((NumberZp)x, ring);
        if(Xtype==0){ if (x == NAN) {return NAN;}
              if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY))  return x;}
        if (Xtype > Ring.Zp32) return (this.toNewRing(Xtype, ring)).add(x, ring);
        return add(((NumberZp)x.toNewRing(Ring.Zp, ring)), ring);
    }


//    @Override
//    public Element subtract(Element x, Ring ring) {
//        int xT=x.numbElementType();
//        if (xT == Ring.Zp) {
//            return new NumberZp(this.subtract((NumberZ) x), ring);
//        } else if (xT > Ring.Zp32) {
//            return  (this.toNewRing(xT, ring)).subtract(x,ring);
//        } else {
//            return new NumberZp((NumberZ) subtract((NumberZ) x.toNewRing(Ring.Z, ring)), ring);
//        }
//    }
    @Override
    public Element subtract(Element x, Ring ring) {
        int Xtype=x.numbElementType();
        if (Xtype == Ring.Zp) return subtract((NumberZp)x, ring);
        if(Xtype==0){ if (x == NAN) {return NAN;}
              if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY))  return x;}
        if (Xtype > Ring.Zp32) return (this.toNewRing(Xtype, ring)).subtract(x, ring);
        return subtract(((NumberZp)x.toNewRing(Ring.Zp, ring)), ring);
    }
    
    @Override
    public Element multiply(Element x, Ring ring) {
        int Xtype=x.numbElementType();
        if (Xtype == Ring.Zp) return multiply((NumberZp)x, ring);
        if(Xtype==0){ if (x == NAN) {return NAN;}
              if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY))  return  (this.isNegative())? x.negate(ring):x;}
        if (Xtype > Ring.Zp32) return (this.toNewRing(Xtype, ring)).multiply(x, ring);
        return multiply(((NumberZp)x.toNewRing(Ring.Zp, ring)), ring);
    }
    @Override
    public Element divideExact(Element el, Ring ring) {
        return divide(el, ring);
    }
    
    public Element add(NumberZp x, Ring ring) {
        return new NumberZp(this.add(((NumberZ) x)).mod(ring.MOD));
    }
    public Element subtract(NumberZp x, Ring ring) {
        return new NumberZp(this.subtract(((NumberZ) x)).mod(ring.MOD));
    }
    public Element multiply(NumberZp x, Ring ring) {
        return new NumberZp(this.multiply(((NumberZ) x)).mod(ring.MOD));
    }

    public Element divide(NumberZp x, Ring ring) {
        return new NumberZp(this.multiply(((NumberZ) x).modInverse(ring.MOD)).mod(ring.MOD), ring);
    }

    @Override
    public Element divide(Element x, Ring ring) {
        int type = x.numbElementType();
        if (type >= Ring.Polynom)  return new Fraction(this, x);
        if (x instanceof Fraction) {Fraction fr = (Fraction) x;
            return this.multiply(fr.num, ring).divide(fr.denom, ring);
        }
        if ((type != Ring.Zp)&&(type > Ring.Zp32)) 
            return this.toNewRing(type, ring).divide(x, ring);
        Element xx=x;
        if  (type <= Ring.Zp32) {xx=x.toNewRing(Ring.Z, ring);}
        return new NumberZp(this.multiply(((NumberZ) xx).modInverse(ring.MOD)), ring);
    }
  
    @Override
    public NumberZp inverse(Ring ring) {
        return new NumberZp(modInverse(ring.MOD));
    }

    /**
     * Сдвигает остаток от деления на модуль в в интервал [0..MOD-1]
     *
     * @return (value < 0)? new NumberZp32(value-MOD) : this;
     */
    @Override
    public NumberZp mod(Ring ring) {
        return (this.signum() < 0) ? new NumberZp(add(ring.MOD), ring) : this;
    }

    /**
     * Сдвигает остаток от деления на нечетный модуль в в интервал
     * [-MOD/2..MOD/2]
     *
     * @return (value > MOD/2)? new NumberZp32(value-MOD): this
     */
    @Override
    public NumberZp Mod(Ring ring) {
        NumberZ m1 = ring.MOD.shiftRight(1);
        return (compareTo(m1.negate()) == -1) ? new NumberZp(add(ring.MOD), ring)
                : (compareTo(m1) == 1 ? (new NumberZp(subtract(ring.MOD), ring)) : this);
    }

    @Override
    public Element valOf(double x, Ring ring) {
        Element pp=NumberR.valueOf(x);
        if(pp instanceof NumberR)
        return new NumberZp(((NumberR)NumberR.valueOf(x)).NumberRtoNumberZ(),ring);
        else return pp;
        }

    @Override
    public NumberZp valOf(int x, Ring ring) {
        return new NumberZp(NumberZ.valueOf(x), ring);
    }

    @Override
    public NumberZp valOf(long x, Ring ring) {
        return new NumberZp(NumberZ.valueOf(x), ring);
    }

    @Override
    public NumberZp valOf(String s, Ring ring) {
        return new NumberZp(s, ring);
    }
 
    /**
     * to New Ring which has type = Algebra
     * @param Algebra
     * @param r
     * @return this transformed to new ring
     */
    @Override
     public Element toNewRing(int Algebra, Ring r) {
       if (Algebra==Ring.Zp) return this;
       return new NumberZ(signum, mag).toNewRing(  Algebra, r);
    }
    @Override
    public Element toNumber(int Algebra, Ring r) {return toNewRing(Algebra, r);
    }   
    @Override
    public Element D(Ring r) {return ZERO;}

    @Override
    public Element D(int num, Ring r) {
        return ZERO;
    }

    @Override
    public NumberZp GCD(Element w, Ring r) {
        return ONE;
    }
}
