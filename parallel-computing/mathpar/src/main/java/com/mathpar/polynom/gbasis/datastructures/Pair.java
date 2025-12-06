package com.mathpar.polynom.gbasis.datastructures;

import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.Term;

/**
 * Critical pair for F4 algorithm.
 *
 * Calculates and stores left and right parts of S-polynomial.
 */
public final class Pair implements Comparable<Pair> {
    private final Term lcm;
    private final Polynom firstPol;
    private final Polynom secondPol;
    private final Polynom leftPart;
    private final Polynom rightPart;
    private final Ring ring;

    public Pair(Ring ring, Polynom p1, Polynom p2) {
        this.ring = ring;
        this.firstPol = p1;
        this.secondPol = p2;
        Term htermP1 = p1.hTerm();
        Term htermP2 = p2.hTerm();

        lcm = htermP1.lcm(htermP2);
        leftPart = lcm.div(htermP1).multiply(p1);
        rightPart = lcm.div(htermP2).multiply(p2);
    }

    public Polynom firstPol() {
        return firstPol;
    }

    public Polynom secondPol() {
        return secondPol;
    }

    public Polynom leftPart() {
        return leftPart;
    }

    public Polynom rightPart() {
        return rightPart;
    }

    public int deg() {
        return lcm.deg();
    }

    @Override
    public int compareTo(Pair o) {
        int cmpFirst = firstPol.compareTo(o.firstPol(), ring);
        int cmpSecond = secondPol.compareTo(o.secondPol(), ring);
        if (cmpFirst > 0) {
            if (cmpSecond > 0) {
                return 1;
            } else if (cmpSecond < 0) {
                return 1;
            } else {
                return 1;
            }
        } else if (cmpFirst < 0) {
            if (cmpSecond > 0) {
                return -1;
            } else if (cmpSecond < 0) {
                return -1;
            } else {
                return -1;
            }
        } else {
            if (cmpSecond > 0) {
                return 1;
            } else if (cmpSecond < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }

        Pair p = (Pair) o;
        return firstPol.equals(p.firstPol()) && secondPol.equals(p.secondPol());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.firstPol != null ? this.firstPol.hashCode() : 0);
        hash = 47 * hash + (this.secondPol != null ? this.secondPol.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "(" + firstPol.toString(ring) + ", " + secondPol.toString(ring) + ")";
    }
}
