package com.mathpar.polynom.gbasis.algorithms;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Fname;

import java.util.ArrayList;
import java.util.List;

import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.gbasis.Gbasis;
import com.mathpar.polynom.gbasis.util.Utils;
import org.apache.logging.log4j.Logger;

/**
 * Implements simple classical Buchberger algorithm of Groebner basis
 * computation for educational purpose.
 */
public final class Buchberger implements Gbasis {
    /**
     * Logger for debug.
     */
    private static final Logger LOG = getLogger(Buchberger.class);
    /**
     * Ring.
     */
    private Ring ring;
    /**
     * Given polynomial ideal.
     */
    private final List<Polynom> ideal;
    /**
     * Stores resulting Groebner basis.
     */
    private List<Polynom> basis;
    /**
     * Verbose output for user.
     */
    private boolean isStepbystep;

    /**
     * @param r  ring
     * @param id ideal of <code>r</code>
     */
    public Buchberger(final Ring r, final List<Polynom> id) {
        ring = r;
        isStepbystep = ring.isStepbystep();
        ideal = new ArrayList<Polynom>(id);
    }

    /**
     * @param r  ring
     * @param id ideal containing polynomials as strings
     */
    public Buchberger(final Ring r, final String... id) {
        this(r, Utils.polList(r, id));
    }

    /**
     * Fills ideal with additional polynomials to construct Groebner basis. Also
     * performs normalization &mdash; all leading coefficients should be equal
     * to one in reduced basis.
     */
    private void constructBasis() {
        List<Polynom> res = new ArrayList<Polynom>(ideal);
        Polynom[] gbArr = new Polynom[0];
        gbArr = res.toArray(gbArr);
        List<Polynom> pairs = new ArrayList<Polynom>();

        if (LOG.isTraceEnabled()) {
            LOG.trace("Starting Groebner bases construction of ideal: {}",
                    Element.colToStr(ring, ideal));
        }

        Polynom s, sReduced;
        for (int i = 0; i < gbArr.length - 1; i++) {
            for (int j = i + 1; j < gbArr.length; j++) {
                if (i != j) {
                    pairs.add(gbArr[i]);
                    pairs.add(gbArr[j]);
                }
            }
        }

        Polynom f1;
        Polynom f2;
        while (!pairs.isEmpty() && !Thread.currentThread().isInterrupted()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Critical pairs: {}", Element.colToStr(ring, pairs));
            }
            f1 = pairs.get(0);
            f2 = pairs.get(1);
            pairs.remove(0);
            pairs.remove(0);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Get pair: [{}, {}].", f1.toString(ring), f2.toString(ring));
            }
            s = spol(ring, f1, f2);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Its S-polynomial: {}.", s.toString(ring));
            }
            if (!s.isZero(ring)) {
                sReduced = rem(ring, s, res);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Reduced S-polynomial: {}.", sReduced.toString(ring));
                }
                if (!sReduced.isZero(ring)) {
                    for (int k = 0; k < res.size(); k++) {
                        pairs.add(res.get(k));
                        pairs.add(sReduced);
                    }
                    res.add(sReduced);
                    LOG.trace("Adding it to basis.");
                }
            }

            if (isStepbystep) {
                ring.addTraceStep(new VectorS(new Element[]{
                        new Fname("Current  basis:  "),
                        new Fname(Element.colToStr(ring, res))
                }));
            }
        }

        if (isStepbystep) {
            ring.addTraceStep(new Fname("Constructed  Groebner  basis:  "
                    + Element.colToStr(ring, res)
                    + ".  Now  going  to  normalize  it"));
        }
        basis = res;
    }

    /**
     * Calculates S-polynomial of given polynomials.
     *
     * @param ring ring
     * @param f1   first polynomial
     * @param f2   second polynomial
     * @return S-polynomial of <code>f1</code> and <code>f2</code>
     */
    public static Polynom spol(final Ring ring,
                               final Polynom f1, final Polynom f2) {
        Polynom lcm = f1.hTerm().lcm(f2.hTerm()).toPolynom(ring);
        Polynom lm1 = lm(f1);
        Polynom lm2 = lm(f2);
        Polynom fst1 = lcm.divideExact(lm1, ring);
        Polynom snd1 = lcm.divideExact(lm2, ring);
        Polynom fst = fst1.multiply(f1, ring);
        Polynom snd = snd1.multiply(f2, ring);

        return fst.subtract(snd, ring);
    }

    /**
     * @param pol polynomial
     * @return Leading Monomial (Leading Coefficient * Leading Term) of given
     * polynomial {@code pol}
     */
    private static Polynom lm(Polynom pol) {
        return new Polynom(pol.hTerm().powers(), new Element[]{pol.coeffs[0]});
    }

    /**
     * @param ring ring
     * @param pol  polynomial
     * @param arr  set of polynomials
     * @return reminder of division <code>pol</code> by <code>arr</code>
     */
    public static Polynom rem(Ring ring, Polynom pol, List<Polynom> arr) {
        Polynom res = Polynom.polynomZero;
        int i;
        boolean divides;

        Polynom p = pol;
        Polynom f;

        if (LOG.isTraceEnabled()) {
            LOG.trace("Делим полином p = {} на множество F = {}",
                    pol.toString(ring), Element.colToStr(ring, arr));
        }

        while (!p.isZero(ring) && !Thread.currentThread().isInterrupted()) {
            i = 0;
            divides = false;
            Polynom lmP = lm(p);
            while (i < arr.size() && !divides) {
                f = arr.get(i);
                Polynom lmF = lm(f);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Делим полином p = {} на полином f = {} из множества F",
                            p.toString(ring), f.toString(ring));
                }
                if (!p.isZero(ring) && lmP.verifyReduction(lmF)) {
                    Polynom lmDivided = lmP.divideExact(lmF, ring);
                    // trying to cancel fraction coefficient
                    if (lmDivided.coeffs[0] instanceof Fraction) {
                        ((Fraction) lmDivided.coeffs[0]).cancel(ring);
                    }
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("p = {} - ({}) * ({}) = ...",
                                p.toString(ring),
                                lmDivided.toString(ring),
                                f.toString(ring));
                    }
                    p = p.subtract(lmDivided.multiply(f, ring), ring);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("... = {}", p.toString(ring));
                    }
                    divides = true;
                } else {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("p = {} не делится на {}",
                                p.toString(ring), f.toString(ring));
                    }
                    i++;
                }
            }

            if (!p.isZero(ring) && !divides) {
                res = res.add(lm(p), ring);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("p = {} не делится на множество F ...",
                            p.toString(ring));
                }
                p = p.subtract(lm(p), ring);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("... поэтому продолжаем деление полинома без старшего монома: p = {}",
                            p.toString(ring));
                }
            }
        }

        return res;
    }

    /**
     * Tries to interreduce constructed basis.
     */
    private void interreduceBasis() {
        List<Polynom> bas = new ArrayList<Polynom>(basis);
        List<Polynom> tmp = new ArrayList<Polynom>();

        Polynom rem;
        Polynom currpol;

        // Make minimal basis - throw out polynomials
        int i = 0;
        while (i < bas.size() && !Thread.currentThread().isInterrupted()) {
            tmp.addAll(bas.subList(0, i));
            tmp.addAll(bas.subList(i + 1, bas.size()));
            currpol = bas.get(i);
            rem = rem(ring, bas.get(i), tmp);
            if (rem.isZero(ring)) {
                bas.remove(currpol);
            } else {
                i++;
            }
            tmp.clear();
        }

        // Reduce each polynomial in "small" basis
        List<Polynom> res = new ArrayList<Polynom>();
        for (int k = 0; k < bas.size() && !Thread.currentThread().isInterrupted(); k++) {
            tmp.addAll(bas.subList(0, k));
            tmp.addAll(bas.subList(k + 1, bas.size()));
            rem = rem(ring, bas.get(k), tmp);
            res.add(rem);
            tmp.clear();
        }

        basis = res;
    }

    /**
     * Normalizes given list of polynomials. First coefficient should become 1
     * if possible.
     *
     * @param ring ring
     * @param pols list of polynomials to normalize
     */
    private static void normalize(final Ring ring, final List<Polynom> pols) {
        for (int i = 0, sz = pols.size(); i < sz && !Thread.currentThread().isInterrupted(); i++) {
            Polynom pol = pols.get(i);
            Element multiplier = ring.numberONE().divide(pol.coeffs[0], ring);
            for (int k = 0; k < pol.coeffs.length; k++) {
                pol.coeffs[k] = pol.coeffs[k].multiply(multiplier, ring);
                if (pol.coeffs[k] instanceof Fraction) {
                    ((Fraction) pol.coeffs[k]).cancel(ring);
                }
            }
        }
    }

    /**
     * Constructs reduced Groebner basis of current ideal.
     *
     * @return reduced Groebner basis
     */
    @Override
    public List<Polynom> gbasis() {
        constructBasis();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Constructed basis: {}. Now starting normalize it.",
                    Element.colToStr(ring, basis));
        }
        if (isStepbystep) {
            ring.addTraceStep(new Fname("Starting  interreduction  ..."));
        }
        normalize(ring, basis);
        interreduceBasis();
        normalize(ring, basis);
        if (isStepbystep) {
            ring.addTraceStep(new Fname("Minimal  Groebner  basis:  "
                    + Element.colToStr(ring, basis)));
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interreduced basis: {}.",
                    Element.colToStr(ring, basis));
        }
        return basis;
    }
}
