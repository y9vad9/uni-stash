package com.mathpar.students.OLD.stud2014.kalugin;

import com.mathpar.func.F;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;

/**
 *
 * @author Калугин Илья
 */
public class trapeziumMethod {
     public static void main(String[] args) {
    	Ring ring = new Ring("R[x]");
    	ring.setMachineEpsilonR(10);
    	System.out.println(integrate(new NumberR("0"), new NumberR("1"), new F("\\exp(x)", ring), ring));
    }
    static Element integrate(Element a, Element b, F f, Ring r) {
    	Element n = new NumberZ("10");
    	Element h = b.subtract(a, r).divide(n, r);
    	Element Result = new NumberZ("0");
    	Element Result2 = new NumberZ("0");

    	do {
    		Result2 = Result;
    		Result = r.numberZERO;
    		Result = f.valueOf(new Element[]{a}, r).add(f.valueOf(new Element[]{b}, r), r).divide(new NumberZ("2"), r);
    		for(NumberZ i = new NumberZ("1"); i.compareTo(n, r) == -1; i = (NumberZ)i.add(new NumberZ("1"), r)) {
    			Result = Result.add(f.valueOf(new Element[]{a.add(h.multiply(i, r), r)}, r), r);

    		}
    		Result = Result.multiply(h, r);
    		n = n.multiply(new NumberZ("2"), r);
    		h = b.subtract(a, r).divide(n, r);


    	} while(!Result.abs(r).subtract(Result2.abs(r), r).isZero(r));
    	return Result;
    }
}
