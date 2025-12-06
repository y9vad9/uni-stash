package com.mathpar.polynom.gbasis.util;

import java.util.ArrayList;
import java.util.List;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.Term;
import com.mathpar.polynom.gbasis.datastructures.TermsContainer;
import com.mathpar.polynom.gbasis.datastructures.TermsList;

/**
 * Utility class for support of gbasis package.
 */
public final class Utils {
    private Utils() {
    }

    /**
     * Constructs list of polynomials from array of Strings w.r.t. r.
     *
     * @param ring ring.
     * @param pols array of Strings containig polynomials.
     *
     * @return list of polynomials constructed from given strings.
     */
    public static List<Polynom> polList(Ring ring, String... pols) {
        List<Polynom> res = new ArrayList<Polynom>(pols.length);
        for (String s : pols) {
            res.add(new Polynom(s, ring));
        }
        return res;
    }

    /**
     * Modifies coefficients of polynomials in given list from ring Q (Fraction
     * Z over Z) to Z.
     *
     * @param ring ring.
     * @param arr list of polynomials to modify.
     */
    public static void polsToZ(Ring ring, List<Polynom> arr) {
        int mainAlgebra = ring.algebra[0];
        Polynom currPol;
        NumberZ newCoefficient = null, lcm;
        for (int i = 0; i < arr.size(); i++) {
            currPol = arr.get(i);
            if (mainAlgebra == Ring.Q) {
                Element[] multipliers = new Element[currPol.coeffs.length];
                for (int j = 0; j < currPol.coeffs.length; j++) {
                    Element currCoeff = currPol.coeffs[j];
                    if (currCoeff instanceof Fraction) {
                        multipliers[j] = ((Fraction) currCoeff).denom;
                    } else if (currCoeff instanceof NumberZ) {
                        multipliers[j] = NumberZ.ONE;
                    } else {
                        throw new IllegalArgumentException("Can't convert to Z: " + currCoeff.toString(ring));
                    }
                }
                lcm = (NumberZ) Element.arrayLCM(multipliers, ring);
                for (int j = 0; j < currPol.coeffs.length; j++) {
                    Element currCoeff = currPol.coeffs[j];
                    if (currCoeff instanceof Fraction) {
                        newCoefficient = lcm.multiply((NumberZ) ((Fraction) currCoeff).num)
                                .divide((NumberZ) ((Fraction) currCoeff).denom);
                    } else if (currCoeff instanceof NumberZ) {
                        newCoefficient = lcm.multiply((NumberZ) currCoeff);
                    }
                    currPol.coeffs[j] = newCoefficient;
                }
            } else if (mainAlgebra >= Ring.R && mainAlgebra <= Ring.R64MinMax) {
                arr.set(i, (Polynom) currPol.toNewRing(Ring.Z, ring));
            }
        }
    }

    /**
     * Converts polynomials with coefficients from Z to Q.
     *
     * @param ring ring.
     * @param arr list of polynomials.
     */
    // TODO: rewrite this to support PolyList.
    public static void polsOverZtoQ(Ring ring, List<Polynom> arr) {
        for (int i = 0; i < arr.size(); i++) {
            arr.set(i, arr.get(i).toPolynom(ring));
        }
    }

    /**
     * @param p polynomial.
     *
     * @return list of all Terms of given polynomial.
     */
    public static List<Term> getAllTerms(Polynom p) {
        List<Term> res = new ArrayList<Term>();
        for (int i = 0; i < p.coeffs.length; i++) {
            if (!res.contains(p.term(i))) {
                res.add(p.term(i));
            }
        }
        return res;
    }

    /**
     * @param l list of polynomials.
     *
     * @return list of all Terms of polynomials in l (without repeating).
     */
    public static List<Term> getAllTerms(List<Polynom> l) {
        List<Term> res = new ArrayList<Term>();
        for (Polynom pol : l) {
            for (int i = 0; i < pol.coeffs.length; i++) {
                if (!res.contains(pol.term(i))) {
                    res.add(pol.term(i));
                }
            }
        }
        return res;
    }

    /**
     * @param l list of polynomials.
     *
     * @return all Terms of polynomials in l in form of TermsContainer.
     */
    public static TermsContainer getAllTermsS(List<Polynom> l) {
        TermsContainer res = new TermsList(null);
        for (Polynom pol : l) {
            for (int i = 0; i < pol.coeffs.length; i++) {
                res.add(pol.term(i));
            }
        }
        return res;
    }

    /**
     * Normalizes given list of polynomials: first coefficients should become 1.
     *
     * @param ring ring.
     * @param pols list of polynomials.
     * @param inQ true if polynomials coefficients are rational, false
     * otherwise.
     */
    public static void normalize(Ring ring, List<Polynom> pols, boolean inQ) {
        for (Polynom pol : pols) {
            if (inQ && !pol.coeffs[0].isOne(ring)) {
                boolean onlyZ = true; // could be fractions but luckly only integers.
                for (int k = 0; k < pol.coeffs.length; k++) {
                    // For fractions: multiply on (1 / HC)
                    if (pol.coeffs[k] instanceof Fraction) {
                        pol.coeffs[k] = pol.coeffs[k].divide(pol.coeffs[0], ring);
                        ((Fraction) pol.coeffs[k]).cancel(ring);
                        onlyZ = false;
                    }
                }
                if (onlyZ) {
                    Element.arrayCancel(pol.coeffs, ring);
                }
            } else {
                // If we are NOT in Rational ring,
                // just cancel coefficients of each polynomial.
                Element.arrayCancel(pol.coeffs, ring);
            }
            if (pol.coeffs[0].isNegative()) {
                pol.negateThis(ring);
            }
        }
    }

    /**
     * @param pols list of polynomials.
     *
     * @return index of new highest variable to add to homogenize polynomial of
     * given polynomial list.
     */
    public static int getIndexOfNewHighestVarForHomogenization(List<Polynom> pols) {
        int maxVarCount = -1;
        for (int i = 0, polsSize = pols.size(); i < polsSize; i++) {
            Polynom currPol = pols.get(i);
            maxVarCount = Math.max(maxVarCount, currPol.getVarsNum());
        }
        return maxVarCount;
    }

    /**
     * @param pols list of polynomials.
     * @param newVarIdx index of new variable for homogenization.
     * @param doDehomogenization perform dehomogenization of early homogenized
     * polynomials, if true.
     *
     * @return list with homogenized (or dehomogenized) polynomials.
     */
    private static List<Polynom> homogenization(List<Polynom> pols,
            int newVarIdx, boolean doDehomogenization) {
        int polsSize = pols.size();
        List<Polynom> result = new ArrayList<Polynom>(polsSize);
        for (int i = 0; i < polsSize; i++) {
            if (doDehomogenization) {
                result.add(pols.get(i).dehomogenize(newVarIdx));
            } else {
                result.add(pols.get(i).homogenize(newVarIdx));
            }
        }
        return result;
    }

    public static List<Polynom> homogenize(List<Polynom> pols, int newVarIdx) {
        return homogenization(pols, newVarIdx, false);
    }

    public static List<Polynom> dehomogenize(List<Polynom> pols, int newVarIdx) {
        return homogenization(pols, newVarIdx, true);
    }
}
