package com.mathpar.polynom;

import java.util.Arrays;
import com.mathpar.number.*;
import java.util.ArrayList;

/**
 */
public class PrepareFactorZ {
 
/** We try to obtain GCD of coefficients several times
 * 
 * @param x input polynomial
 * @param R - Ring
 * @return array of polynomials which obtained as GCD of coefficients.
 *      Their product is equal to x. 
 */
    public static Polynom[] PrepareFactor(Polynom x, Ring R) {   
       ArrayList rezalt  = new ArrayList<Polynom>();      
       while(true){ int h = x.powers.length / x.coeffs.length;
           Polynom  gcdH=x.GCDHPolCoeffs(h, R);
           if (!gcdH.isOne(R)) {rezalt.add(x.divideExact(gcdH, R));  x=gcdH;}
           else {rezalt.add(x); break;}
       }
        int size=rezalt.size(); 
        Polynom[] otvet = new Polynom[size];
        return (Polynom[]) rezalt.toArray(otvet);
    }
//        int h = x.powers.length / x.coeffs.length;
 
    /**
     *
     * @param x
     * @param number - номер переменной
     * @param ring
     *
     * @return
     */
    public static Polynom Polynomial_GCD(Polynom x, int number, Ring ring) {
        int len = x.powers.length;
        int length = x.coeffs.length;
        if (number == (ring.varNames.length - 1)) {
            return x.GCDHPolCoeffs(ring);
        }
        if (number > len / length) {
            return Polynom.polynom_one(NumberZ.ONE);
        }
        int h = len / length;
        int powers[] = new int[h];
        int pow_x[] = new int[x.powers.length];
        Element coef_x[] = new Element[x.coeffs.length];
        Arrays.fill(coef_x, NumberZ.ZERO);

        boolean flag = true;
        int k = number;
        Polynom first = Polynom.polynom_one(NumberZ.ONE);
        Polynom second = Polynom.polynom_one(NumberZ.ONE);
        int pol = 0;
        //основной цикл
        while (flag) {
            Polynom sum = Polynom.polynom_zero(NumberZ.ZERO);//может нулевой полином создавать?
            int t = 0;
            int col = 0;           //степень по которой будем состовлять полином
            while (x.powers[k] == -1) {
                k += h;
            }
            col = x.powers[k];
            int tt = 0;
            int ttt = 0;
            for (int i = number; i < len; i += h) {
                if (x.powers[i] != -1) {
                    if (x.powers[i] == col) {
                        x.powers[i] = 0;
                        Element coeffs[] = new Element[] {x.coeffs[i / h]};
                        for (int j = 0; j < h; j++) {
                            powers[j] = x.powers[i - i % h + j];
                        }
                        coef_x[ttt] = coeffs[0];
                        System.arraycopy(powers, 0, pow_x, (ttt) * h, h);
                        x.powers[i] = -1;
                        t++;
                        tt++;
                    }
                } else {
                    t++;
                }
                ttt++;
            }
            tt = 0;
            sum = new Polynom(pow_x, coef_x);
            sum = sum.ordering(ring);
            sum = sum.deleteZeroCoeff(ring);
            sum = sum.normalNumbVar(ring);
            pol++;
            if (pol % 2 != 0) {
                first = sum;
            } else {
                second = sum;
            }
            if (pol >= 2) {
                second = second.gcd(first, ring);
                if (second.isOne(ring)) {
                    return Polynom.polynom_one(NumberZ.ONE);
                }
            }
            if (t == length) {
                flag = false;
            }
        }
        Element gcd = second.GCDNumPolCoeffs(ring);
        if (!gcd.isOne(ring)) {
            for (int i = 0; i < second.coeffs.length; i++) {
                second.coeffs[i] = second.coeffs[i].divide(gcd, ring);
            }
        }
        if (second.coeffs[0].isMinusOne(ring)) {
            second = (Polynom) second.negate(ring);
        }
        return second;
    }
}
