package com.mathpar.polynom.gbasis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NumberR;
import com.mathpar.number.NumberZ;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.web.exceptions.MathparException;

public class Modular {
    public static Element rationalReconstruction(int a, int m) {
        return rationalReconstruction(NumberZ.valueOf(a), NumberZ.valueOf(m));
    }

    public static Element rationalReconstruction(Element el, NumberZ m) {
        if (el instanceof VectorS) {
            return rationalReconstruction((VectorS) el, m);
        } else if (el instanceof NumberZ) {
            return rationalReconstruction((NumberZ) el, m);
        }
        throw new IllegalArgumentException("Can't do rational reconstruction of " + el
                + " mod " + m);
    }

    public static Element rationalReconstruction(VectorS v, NumberZ m) {
        Element[] res = new Element[v.length()];
        for (int i = 0; i < res.length; i++) {
            Element vEl = v.V[i].toNewRing(Ring.Z, Ring.ringZxyz);
            if (vEl instanceof NumberZ) {
                res[i] = rationalReconstruction((NumberZ) vEl, m);
            } else {
                throw new MathparException("Can't do rational reconstruction of " + vEl
                        + " mod " + m);
            }
        }
        return new VectorS(res);
    }

    /**
     * Rational reconstruction by Wang algorithm.
     *
     * @param a integer number
     * @param m modulo
     *
     * @return Fraction r/s with rational reconstruction a (mod m):
     * a=r*s^{-1}(mod m)
     *
     * @throws MathparException if rational reconstruction a (mod m) doesn't
     * exist.
     */
    public static Fraction rationalReconstruction(NumberZ a, NumberZ m) {
        a = a.mod(m);
        if (a.isZero(Ring.ringZxyz) || m.isZero(Ring.ringZxyz)) {
            return new Fraction(0, 1);
        }
        if (m.isNegative()) {
            m = m.negate();
        }
        if (a.isNegative()) {
            a = m.subtract(a);
        }
        if (a.isOne(Ring.ringZxyz)) {
            return new Fraction(1, 1);
        }
        NumberZ u = m;
        NumberZ v = a;
        NumberZ bound = (NumberZ) new NumberR(m)
                .divide(new NumberR("2"), Ring.ringZxyz)
                .sqrt(Ring.ringZxyz)
                .floor(Ring.ringZxyz);
        NumberZ[] uVec = {new NumberZ(1), new NumberZ(0), u};
        NumberZ[] vVec = {new NumberZ(0), new NumberZ(1), v};
        while (vVec[2].abs().compareTo(bound) > 0) {
            NumberZ q = uVec[2].divide(vVec[2]); // auto floor.
            NumberZ[] tempVec = {
                uVec[0].subtract(q.multiply(vVec[0])),
                uVec[1].subtract(q.multiply(vVec[1])),
                uVec[2].subtract(q.multiply(vVec[2]))
            };
            uVec = vVec;
            vVec = tempVec;
        }
        NumberZ denom = vVec[1].abs();
        NumberZ num = vVec[2];
        if (vVec[1].isNegative()) {
            num = num.negate();
        }
        if (denom.compareTo(bound) <= 0 && num.gcd(denom).isOne(Ring.ringZxyz)) {
            return new Fraction(num, denom);
        }
        throw new MathparException("Rational reconstruction of " + a + " (mod "
                + m + ") doesn't exist.");
    }

    /**
     * Find exact rational solution x of a*x=b system of linear equations.
     *
     * @param pInt prime number to use in p-adic lifting
     * @param a integer matrix
     * @param b integer vector
     *
     * @return
     */
    public static VectorS solveLaePadic(MatrixS a, VectorS b, int pInt) {
        Ring ringZ = new Ring("Z[]");
        Ring ringZp = new Ring("Zp32[]");
        ringZp.setMOD32(pInt);
        Element p = new NumberZp32(pInt);
        Element pZ = new NumberZ(pInt);
        // p^k = 2*D^2. D is from Hadamard's inequality
        Element pkBound = new NumberZ(2).multiply(hadamardBoundSquared(ringZ, a), ringZ);
        List<Element> pPowers = new ArrayList<>();
        List<Element> x = new ArrayList<>();
        MatrixS aP = (MatrixS) a.toNewRing(Ring.Zp32, ringZp);
        MatrixS aPInv = aP.inverse(ringZp);
        if (aPInv == null) {
            throw new MathparException("Matrix of coefficients must be nonsingular (mod " + pInt + ")");
        }
        NumberZ pPower = new NumberZ(1);
        Element currB = b;
        Element currBp = currB.toNewRing(Ring.Zp32, ringZp);
        Element currX = aPInv.multiply(currBp, ringZp);
        Element xx = currX;
        while (true) {
            pPower = (NumberZ) pPower.multiply(p, ringZ);
            if (pPower.compareTo(pkBound, ringZ) >= 0) {
                break;
            }
            currB = currB.subtract(a.multiply(currX, ringZ), ringZ).divide(pZ, ringZ);
            //a.multiply(currX, ringZ).negate(ringZ).add(currB, ringZ).divide(pZ, ringZ).toNewRing(Ring.Zp32, ringZp);
            currBp = currB.toNewRing(Ring.Zp32, ringZp);
            currX = aPInv.multiply(currBp, ringZp);
            x.add(currX);
            pPowers.add(pPower);
        }
        for (int i = 0, sz = pPowers.size(); i < sz; i++) {
            xx = xx.add(x.get(i).multiply(pPowers.get(i), ringZ), ringZ);
        }
        Element xReconstructed = rationalReconstruction(xx, pPower);
        return (VectorS) xReconstructed;
    }

    /**
     * @param ring integer ring
     * @param a integer matrix
     *
     * @return square of Hadamard's bound: |det(a)| \leq D^2, where D is the
     * product of matrix rows Euclidean norms.
     */
    public static Element hadamardBoundSquared(Ring ring, MatrixS a) {
        Element boundSquare = new NumberZ(1);
        int colNum = a.columnsNumber();
        Element[] colSumOfSquares = new Element[colNum];
        for (int i = 0; i < colNum; i++) {
            colSumOfSquares[i] = new NumberZ(0);
            Element[] col = a.getCol(i, ring);
            for (int j = 0, colSz = col.length; j < colSz; j++) {
                colSumOfSquares[i] = colSumOfSquares[i].add(col[j].multiply(col[j], ring), ring);
            }
        }
        // At case when the number of columns is greater then the number of columns
        // get only columns with maximal sum of squares.
        Arrays.sort(colSumOfSquares);
        int lastIdx = colNum > a.size ? colNum - a.size : 0;
        for (int i = colSumOfSquares.length - 1; i >= lastIdx; i--) {
            boundSquare = boundSquare.multiply(colSumOfSquares[i], ring);
        }
        return boundSquare;
    }
}
