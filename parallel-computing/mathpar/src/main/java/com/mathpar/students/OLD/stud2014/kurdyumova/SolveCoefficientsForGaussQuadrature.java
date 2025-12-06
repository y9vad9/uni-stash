/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.kurdyumova;

import com.mathpar.func.F;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Complex;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.polynom.FactorPol;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author student
 */
public class SolveCoefficientsForGaussQuadrature {
    private NumberR64 a;
    private NumberR64 b;
    private int n;
    private F f;
    private F weight;

    public SolveCoefficientsForGaussQuadrature(NumberR64 a,
            NumberR64 b,
            int n,
            F f,
            F weight) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.f = f;
        this.weight = weight;

    }

    public NumberR64 solveInteg(int k, Ring ring) {
        return (NumberR64) (b.pow(k + 1, ring).divide(
                new NumberR64(k + 1), ring)).subtract(a.pow(k + 1, ring).divide(new NumberR64(k + 1), ring), ring);
    }

    public Element[] solveMatrix(Ring ring) {
        NumberR64[][] matrix = new NumberR64[n][n];
        Element[] v = new Element[n];
        int ind = matrix.length;
        for (int i = 0; i < matrix.length; i++) {
            ind = matrix.length + i;
            v[i] = solveInteg(ind, ring).negate(ring);
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = solveInteg(ind - 1, ring);
                ind--;
            }
        }
        MatrixS d1 = new MatrixS(matrix, ring);
        //System.out.println("M= " + d1.toString(ring));
        MatrixS d2 = d1.inverse(ring);
        //System.out.println("B= " + Array.toString(v,ring));
       // System.out.println("M inverse = " + d2.toString(ring));
        return d2.multiply(v, n, ring);
    }

    public NumberR64[] solveCoefficients(Element[] x, Ring ring) {
        NumberR64[] w = new NumberR64[n];
        Polynom[] mono = new Polynom[n - 1];
        int[] jk = new int[n - 1];
        for (int i = 0; i < n; i++) {
            for (int t = 0; t < n - 1; t++) {
                mono[t] = new Polynom(new int[] {1, 0}, new NumberR64[] {new NumberR64(1), (NumberR64) new NumberR64(0).subtract(x[(t + i + 1) % n].Re(ring), ring)});
                jk[t] = 1;
            }
            NumberR64 koefi = new NumberR64(1);
            NumberR64 t = new NumberR64(0);
            for (int j = 0; j < n - 1; j++) {
                koefi = (NumberR64) koefi.multiply((NumberR64) x[i].Re(ring).subtract((NumberR64) x[(j + i + 1) % n].Re(ring), ring));
            }
            koefi = (NumberR64) koefi.pow(-1, ring);
            FactorPol fa = new FactorPol(jk, mono);
            System.out.println(fa);
            Polynom lk = (Polynom)fa.toPolynomOrFraction(ring);
            System.out.println(lk);
            for (int j = 0; j < lk.coeffs.length; j++) {
                t = (NumberR64) t.add(koefi.multiply((NumberR64) lk.coeffs[j].multiply(solveInteg(lk.powers[j], ring), ring), ring), ring);
            }
            System.out.println("x = " + x[i].Re(ring));
            System.out.println("w = " + t);
            w[i] = (NumberR64) t;
        }
        return w;

    }

    public void start(Ring ring) {
        Element[] koef = solveMatrix(ring);
        Element[] kk = new Element[koef.length + 1];
        kk[0] = new NumberR64(1);
        System.arraycopy(koef, 0, kk, 1, koef.length);
        NumberR64[] x = new NumberR64[n];
        int[] pow = new int[n+1];
        for (int i = 0; i < pow.length; i++) {
            pow[i] = pow.length-i-1;
        }
        Polynom p = new Polynom(pow, kk);
        Polynom p1 = p.deleteZeroCoeff(ring);
        Element[] c = p1.rootOfE(ring);
        for (int i = 0; i < n; i++) {
            x[i] = (NumberR64) c[i].Re(ring);
        }
        System.out.println(Array.toString(c));
        NumberR64[] w = solveCoefficients(c, ring);
        NumberR64 s = NumberR64.ZERO;
        for(int j = 0; j < w.length; j++){
            s = (NumberR64)s.add(w[j], ring);
        }
        System.out.println("SUM = " + s);
        System.out.println(Array.toString(w));
    }

    public static void main(String[] args) {
        Ring ring = new Ring("R64[x]");
        F f = new F("5", ring);
        F w = new F("1", ring);
        NumberR64 a = NumberR64.MINUS_ONE;
        NumberR64 b = new NumberR64(1);
        int n = 5;
        SolveCoefficientsForGaussQuadrature s = new SolveCoefficientsForGaussQuadrature(a, b, n, f, w);
        s.start(ring);
    }
}
