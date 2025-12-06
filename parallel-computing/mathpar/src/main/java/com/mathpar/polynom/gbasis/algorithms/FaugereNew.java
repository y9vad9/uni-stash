package com.mathpar.polynom.gbasis.algorithms;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.Term;
import com.mathpar.polynom.gbasis.Gbasis;
import com.mathpar.polynom.gbasis.datastructures.PolyListDefault;
import com.mathpar.polynom.gbasis.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Logger;

/**
 * Optimized version without unneeded conversions of polynomials.
 * <p/>
 * Implements F4 algorithm for computing Groebner basis. Firstly described in "A
 * new efficient algorithm for computing Gröbner bases (F4)" paper
 * (http://www-salsa.lip6.fr/~jcf/Papers/F99a.pdf) by Jean-Charles Faugere
 * (http://www-salsa.lip6.fr/~jcf/).
 */
public final class FaugereNew implements Gbasis {
    /**
     * Logger for debug.
     */
    private static final Logger LOG = getLogger(FaugereNew.class);
    /**
     * Working ring.
     */
    private Ring ring;
    /**
     * Input ideal.
     */
    private final List<Polynom> ideal;
    /**
     * Will contain constructed basis.
     */
    private PolyListDefault basis;
    /**
     * True if given ring is Q.
     */
    private boolean inQ = false;

    protected FaugereNew() {
        ideal = null;
    }

    /**
     * Main constructor. Converts polynomial from Q to Z if needed.
     *
     * @param r            ring.
     * @param doHomogenize homogenize input ideal if {@code true}.
     * @param ideal        polynomial ideal.
     */
    public FaugereNew(Ring r, boolean doHomogenize, List<Polynom> ideal) {
        this.ideal = new ArrayList<Polynom>(ideal);
        ring = r;
        // convert Q numbers to Z
        if (ring.algebra[0] == Ring.Q) {
            Utils.polsToZ(r, this.ideal);
            inQ = true;
        }
    }

    public FaugereNew(Ring r, List<Polynom> ideal) {
        this(r, false, ideal);
    }

    /**
     * Constructor with input ideal given in form of String array.
     *
     * @param r     ring.
     * @param ideal strings with polynomial ideal.
     */
    public FaugereNew(Ring r, String... ideal) {
        this(r, false, Utils.polList(r, ideal));
    }

    public FaugereNew(Ring r, boolean doHomogenize, String... ideal) {
        this(r, doHomogenize, Utils.polList(r, ideal));
    }

    /**
     * Initial filling of list with critical pairs.
     *
     * @param ideal initial ideal.
     * @return list with critical pairs of polynomials.
     */
    private PolyListDefault initPairs(PolyListDefault ideal) {
        PolyListDefault pairs = new PolyListDefault(ring);
        int size = ideal.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                PolyListDefault.computeAndAddLRPair(ideal, pairs, i, j);
            }
        }
        return pairs;
    }

    /**
     * Select and removes some pairs from given array of critical pairs.
     *
     * @param pairs list with critical pairs.
     * @return list of selected pairs. Note: selected pairs are removed from
     * given list.
     */
    private PolyListDefault selectAndRemovePairs(PolyListDefault pairs) {
        PolyListDefault res = new PolyListDefault(ring);
        // TODO: add selection strategy.
        // TODO: add all pairs.
        // Get only first pair.
        pairs.copyToAndRemovePols(res, 0, 1);
        return res;
    }

    /**
     * Main method implementing F4 algorithm.
     */
    private void constructBasis() {
        // 1) Copy input ideal to current basis matrix.
        basis = new PolyListDefault(ring, ideal);
        LOG.trace("Ideal: {}", basis);
        // 2) Initialize critical pairs.
        PolyListDefault pairs = initPairs(basis);

        while (pairs.size() != 0 && !Thread.currentThread().isInterrupted()) {
            // 3) Get and remove pairs.
            PolyListDefault selectedPairs = selectAndRemovePairs(pairs);
            LOG.trace("Selected pairs: {}", selectedPairs);
            // 4) Add left and right parts of selected pairs to reduction matrix.
            //    and reduce them.
            int basisBeforeReductionSize = basis.size();
            // Reduce nonhighest terms of selectedPairs.
            reduction(selectedPairs, basis, false);
            // 5) Update LR-parts - get polynomials before reduction
            // and make pairs with polynomials after reduction.
            for (int i = basisBeforeReductionSize; i < basis.size(); i++) {
                for (int j = 0; j < basisBeforeReductionSize; j++) {
                    PolyListDefault.computeAndAddLRPair(basis, pairs, i, j);
                }
            }
        }
    }

    // TODO: write docs here.

    /**
     * @param reducible
     * @param reductors
     * @param doAutoreduce
     */
    private void reduction(PolyListDefault reducible, PolyListDefault reductors,
                           boolean doAutoreduce) {
        LOG.trace("Reducible: {}", reducible);
        LOG.trace("Reductors: {}", reductors);
        int reducibleSizeBeforeEchelon = reducible.size();
        symPP(reducible, reductors, doAutoreduce);
        if (doAutoreduce && reducible.size() == reducibleSizeBeforeEchelon) {
            // If size of reducible didn't changed after symPP, reducible
            // polynomial can't be reduced.
            LOG.trace("SymPP did't change reducible size - can't reduce");
            return;
        }
        LOG.trace("Reducible after symPP: {}", reducible);
        // Save HT of reducible terms after symPP()
        Set<Term> htAfterPP = reducible.ht();
        reducibleSizeBeforeEchelon = reducible.size();
        reducible.toEchelonForm(true);
        normalize(reducible);
        if (doAutoreduce) {
            if (reducible.size() < reducibleSizeBeforeEchelon) {
                // If after Echelon form size of matrix < size of reducible,
                // reducible had been reduced to zero, we should drop it.
                reducible.set(0, new Element[]{});
                LOG.trace("Rows count after echelon < preprocessed count. "
                        + "Reduced to zero - drop it.");
                return;
            }
            // If polynomial has been reduced,
            // replace reducible polynomial with reduced.
            return;
        }
        Set<Term> htReductors = reductors.ht();
        for (int i = 0; i < reducible.size(); i++) {
            if (reducible.rowSize(i) > 0) {
                Term afterEchHT = reducible.term(i, 0);
                if (!htAfterPP.contains(afterEchHT)
                        && !htReductors.contains(afterEchHT)) {
                    PolyListDefault.appendPols(reducible, reductors, i);
                }
            }
        }
        LOG.trace("Reductors after reduction: {}", reductors);
    }

    // TODO: write docs here.

    /**
     * @param reducible
     * @param reductors
     * @param doAutoreduce
     */
    private void symPP(PolyListDefault reducible, PolyListDefault reductors,
                       boolean doAutoreduce) {
        // If doing autoreduce, include highest terms.
        int beginningTermInd = doAutoreduce ? 0 : 1;

        List<Term> workingTerms = new ArrayList<Term>();
        int reducibleSize = reducible.size();
        int reductorsSize = reductors.size();

        // Get terms of reducible (including HT if doing autoreduce).
        for (int i = 0; i < reducibleSize; i++) {
            int rowLen = reducible.rowSize(i);
            for (int j = beginningTermInd; j < rowLen; j++) {
                Term t = reducible.term(i, j);
                if (!workingTerms.contains(t)) {
                    workingTerms.add(t);
                }
            }
        }

        // FIXME: slooow.
        Collections.sort(workingTerms, Collections.reverseOrder());

        while (!workingTerms.isEmpty()) {
            Term reducibleTerm = workingTerms.remove(0);

            for (int i = 0; i < reductorsSize; i++) {
                if (reducibleTerm.isReducibleBy(reductors.term(i, 0))) {
                    PolyListDefault.appendPols(reductors, reducible, i);
                    reducible.multiply(reducibleSize,
                            reducibleTerm.div(reductors.term(i, 0)));
                    int insertedLen = reducible.rowSize(reducibleSize);
                    // Add only non-highest terms of reductor to working terms.
                    for (int j = 1; j < insertedLen; j++) {
                        Term t = reducible.term(reducibleSize, j);
                        if (!workingTerms.contains(t)) {
                            workingTerms.add(t);
                        }
                    }
                    reducibleSize++;

//                     FIXME: HIGH PRIORITY! slooow.
                    Collections.sort(workingTerms, Collections.reverseOrder());

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Reductor of m = {} is f = {} (m' = {})",
                                reducibleTerm.toString(ring),
                                reductors.get(i).toString(ring),
                                reducibleTerm.div(reductors.term(i, 0)).
                                        toString(ring));
                    }
                    break;
                }
            }
        }
    }

    /**
     * Reduces constructed basis.
     */
    private List<Polynom> interreduce() {
        LOG.trace("======= Starting interreduce =======\n\n");
        int i;
        List<Polynom> interreducedBasis = new ArrayList<Polynom>(basis.size());
        for (i = 0; i < basis.size(); ) {
            PolyListDefault reducible = new PolyListDefault(ring);
            PolyListDefault reductors = new PolyListDefault(ring);
            // Reduce i-th polynomial by all other polynomials from basis.
            PolyListDefault.appendPols(basis, reducible, i);
            PolyListDefault.appendPols(basis, reductors, range(0, i));
            PolyListDefault.appendPols(basis, reductors, range(i + 1, basis.size()));
            reduction(reducible, reductors, true);
            Polynom p = reducible.get(0);
            if (!p.isZero(ring)) {
                interreducedBasis.add(p);
                i++;
            } else {
                // Drop zero polynomial from basis.
                basis.remove(i);
            }
        }
        return interreducedBasis;
    }

    /**
     * Generates integer array with given bounds.
     *
     * @param begin lower bound.
     * @param end   upper bound.
     * @return array with integers from begin to (end - 1).
     */
    public static int[] range(int start, int end) {
        int sz = end - start;
        int[] res = new int[sz];
        for (int i = 0; i < sz; i++) {
            res[i] = start + i;
        }
        return res;
    }

    /**
     * Normalize given PolyListDefault.
     * <p/>
     * First coefficients become to 1 if possible by cancelling and negating (if
     * first coefficient is negative) row of coefficients and negating.
     *
     * @param l
     */
    private void normalize(PolyListDefault l) {
        int size = l.size();
        for (int i = 0; i < size; i++) {
            l.cancelRow(i);
            // If first coefficient is negative, negate all coefficients.
            if (l.coeff(i, 0).isNegative()) {
                int rowLen = l.rowSize(i);
                Element[] coeffs = new Element[rowLen];
                for (int j = 0; j < rowLen; j++) {
                    coeffs[j] = l.coeff(i, j).negate(ring);
                }
                l.set(i, coeffs);
            }
        }
    }

    /**
     * Facade method constructing reduced Groebner basis with F4 algorithm.
     *
     * @return list with polynomials of reduced Groebner basis.
     */
    @Override
    public List<Polynom> gbasis() {
        constructBasis();
        normalize(basis);
        LOG.trace("Basis after normalize(): {}", basis);
        List<Polynom> gb = interreduce();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interreduced basis: {}", Element.colToStr(ring, gb));
        }
        if (inQ) {
            Utils.polsOverZtoQ(ring, gb);
        }
        Utils.normalize(ring, gb, inQ);
        return gb;
    }
}
