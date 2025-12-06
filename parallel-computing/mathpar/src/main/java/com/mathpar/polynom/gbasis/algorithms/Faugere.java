package com.mathpar.polynom.gbasis.algorithms;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.Term;
import com.mathpar.polynom.gbasis.Gbasis;
import com.mathpar.polynom.gbasis.datastructures.Pair;
import com.mathpar.polynom.gbasis.util.Utils;
import org.apache.logging.log4j.Logger;

/**
 * Implements F4 algorithm for computing Groebner basis.
 *
 * Firstly described in "A new efficient algorithm for computing Gröbner bases
 * (F4)" paper (http://www-salsa.lip6.fr/~jcf/Papers/F99a.pdf) by Jean-Charles
 * Faugere (http://www-salsa.lip6.fr/~jcf/).
 */
public final class Faugere implements Gbasis {
    private static final Logger LOG = getLogger(Faugere.class);
    /**
     * working ring
     */
    private final Ring ring;
    /**
     * input ideal
     */
    private final List<Polynom> ideal;
    /**
     * will contain constructed basis
     */
    private List<Polynom> basis;
    /**
     * are we in Q ring?
     */
    private boolean inQ = false;

    /**
     * Default constructor.
     */
    protected Faugere() {
        ideal = null;
        ring = null;
    }

    /**
     * Main constructor
     *
     * @param r ring
     * @param id ideal
     */
    public Faugere(Ring r, List<Polynom> id) {
        ideal = new ArrayList<Polynom>(id);
        // convert Q numbers to Z
        inQ = r.algebra[0] == Ring.Q;
        if (r.algebra[0] != Ring.Z) {
            this.ring = new Ring(r, Ring.Z);
            Utils.polsToZ(r, ideal);
        } else {
            this.ring = r;
        }
    }

    /**
     * @param r ring
     * @param str strings with polynomials
     */
    public Faugere(Ring r, String... str) {
        this(r, Utils.polList(r, str));
    }

    @Override
    public String toString() {
        return basis != null ? Element.colToStr(ring, basis) : "";
    }

    /**
     * @param arr list of polynomials
     *
     * @return list of all Terms of polynomials in <code>arr</code> (without
     * repeating)
     */
    private ArrayList<Term> getAllHighestTerms(List<Polynom> arr) {
        ArrayList<Term> res = new ArrayList<Term>();
        for (Polynom pol : arr) {
            if (!res.contains(pol.hTerm())) {
                res.add(pol.hTerm());
            }
        }
        return res;
    }

    /**
     * Initial filling of array with critical pairs
     *
     * @param arr ideal
     *
     * @return array of critical pairs at <code>arr</code>
     */
    private ArrayList<Pair> initPairs(List<Polynom> arr) {
        ArrayList<Pair> res = new ArrayList<Pair>();
        int arrSize = arr.size();
        for (int i = 0; i < arrSize && !Thread.currentThread().isInterrupted(); i++) {
            for (int j = i + 1; j < arrSize && !Thread.currentThread().isInterrupted(); j++) {
                if (i != j) {
                    res.add(new Pair(ring, arr.get(i), arr.get(j)));
                }
            }
        }
        return res;
    }

    /**
     * Converts set of polynomials to corresponding matrix of coefficients
     *
     * @param pols set of polynomials
     *
     * @return matrix with coefficients w.r.t. all termsPP from <code>arr</code>
     */
    private MatrixS matrixOfCoeffs(List<Polynom> pols, List<Term> terms) {
        int[][] col = new int[pols.size()][];
        Element[][] coeffsMatrix = new Element[pols.size()][];
        for (int i = 0; i < pols.size(); i++) {
            Polynom currPol = pols.get(i);
            coeffsMatrix[i] = new Element[currPol.coeffs.length];
            col[i] = new int[currPol.coeffs.length];
            for (int j = 0; j < currPol.coeffs.length; j++) {
                col[i][j] = Collections.binarySearch(terms, currPol.term(j), Collections.reverseOrder());
                coeffsMatrix[i][j] = currPol.coeffs[j];
            }
        }
        return new MatrixS(coeffsMatrix, col);
    }

    /**
     * Reconstructs list of polynomials from matrix with terms positions.
     *
     * @param m matrix with coefficients
     * @param terms list of terms
     *
     * @return list of polynomials
     */
    private ArrayList<Polynom> polsFromMatrix(MatrixS m, List<Term> terms) {
        ArrayList<Polynom> res = new ArrayList<Polynom>();
        ArrayList<Term> currPolTerms = new ArrayList<Term>();
        ArrayList<Element> currPolCoeffs = new ArrayList<Element>();

        MatrixD tempMatrix = new MatrixD(m);
        for (int i = 0, rowCount = tempMatrix.M.length; i < rowCount; i++) {
            for (int j = 0, colCount = tempMatrix.M[i].length; j < colCount; j++) {
                if (!tempMatrix.M[i][j].isZero(ring)) {
                    currPolTerms.add(terms.get(j));
                    currPolCoeffs.add(tempMatrix.M[i][j]);
                }
            }
            if (!currPolTerms.isEmpty()) {
                res.add(polFromTerms(ring, currPolTerms, currPolCoeffs));
            }
            currPolTerms.clear();
            currPolCoeffs.clear();
        }
        return res;
    }

    /**
     * Constructs polynom from set of not null termsPP
     *
     * @param ring
     * @param termsPP
     * @param coeffs
     *
     * @return
     */
    private Polynom polFromTerms(Ring ring, ArrayList<Term> terms,
            ArrayList<Element> coeffs) {
        // Calculate maximal powers length
        int powLen = 0;
        for (Term t : terms) {
            powLen = Math.max(powLen, t.size());
        }

        // Fill powers array
        int[] pows = new int[powLen * terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            System.arraycopy(terms.get(i).powers(), 0, pows,
                    i * powLen, terms.get(i).size());
        }
        // Fill coefficients array
        Element[] c = new Element[coeffs.size()];
        c = coeffs.toArray(c);

        return new Polynom(pows, c, ring);
    }

    /**
     * Select and removes some pairs from given array of critical pairs
     *
     * @param pairs array of critical pairs
     *
     * @return array of selected pairs. <i>Note</i>: selected pairs are removing
     * from given array.
     */
    private ArrayList<Pair> selectPairs(List<Pair> pairs) {
        ArrayList<Pair> res = new ArrayList<Pair>();
        res.add(pairs.remove(0));
        return res;
    }

    /**
     * Returns first reductor of {@code Term t} if highest term of some
     * polynomial {@code f} from input array is reducing given term, this method
     * returns {@code f}.
     *
     * @param t term
     * @param arr set of polynomials
     *
     * @return reductor of {@code t} from {@code arr} if any, otherwise null.
     */
    private Polynom getTopReductorOfTerm(Term t, List<Polynom> arr) {
        for (Polynom pol : arr) {
            if (t.isReducibleBy(pol)) {
                return pol;
            }
        }
        return null;
    }

    /**
     * Preprocess input array of polynomials to eliminate not only highest
     * termsPP. (symPP in original)
     *
     * @param pairs polynomials from left and right pairs (each t*f from pairs).
     * @param basis current basis.
     *
     * @return set of polynomials with non-highest termsPP ready for reduction.
     */
    private List<Polynom> preprocessing(List<Polynom> pairs, List<Polynom> basis) {
        List<Polynom> currBasis = new ArrayList<Polynom>(basis);
        List<Term> highestTerms = getAllHighestTerms(pairs);
        Collections.sort(highestTerms, Collections.reverseOrder());

        List<Term> terms = Utils.getAllTerms(pairs);
        Collections.sort(terms, Collections.reverseOrder());
        List<Term> nonHT = new ArrayList<Term>(terms);
        nonHT.removeAll(highestTerms);

        List<Polynom> res = new ArrayList<Polynom>();
        res.addAll(pairs);
        while (!nonHT.isEmpty() && !Thread.currentThread().isInterrupted()) {
            Term reducibleTerm = nonHT.remove(0);
            Polynom reductor = getTopReductorOfTerm(reducibleTerm, currBasis);
            if (null != reductor) {
                Term multiplierForReduction = reducibleTerm.
                        div(reductor.hTerm());
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Reductor of m = {} is f = {} (m' = {})",
                            new Object[] {
                                reducibleTerm.toString(ring),
                                reductor.toString(ring),
                                multiplierForReduction.toString(ring)
                            });
                }
                Polynom polynomToAdd = multiplierForReduction.multiply(reductor);
                res.add(polynomToAdd);
                // Add terms of reductor-polynomial except of HT and
                // already added.
                for (int i = 1; i < polynomToAdd.coeffs.length; i++) {
                    Term currTerm = polynomToAdd.term(i);
                    if (!nonHT.contains(currTerm)) {
                        nonHT.add(currTerm);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Reduction using matrix row echelon form.
     *
     * @param reducible list of reducible polynomials
     * @param reductors list of reductors
     *
     * @return result of reduction
     */
    private List<Polynom> reduction(List<Polynom> reducible, List<Polynom> reductors) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Reducible: {}", Element.colToStr(ring, reducible));
            LOG.trace("Reductors: {}", Element.colToStr(ring, reductors));
        }
        // Preprocess reducible for matrix constructing.
        List<Polynom> preprocessed = preprocessing(reducible, reductors);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Reducible after symPP: {}",
                    Element.colToStr(ring, preprocessed));
        }
        List<Term> termsPP = Utils.getAllTerms(preprocessed);
        Collections.sort(termsPP, Collections.reverseOrder());

        MatrixS matrix = matrixOfCoeffs(preprocessed, termsPP);
        MatrixS matrixEch = matrix.toGroebnerEchelonForm(ring);
        List<Polynom> polsAfterEchelon = polsFromMatrix(matrixEch, termsPP);
        List<Term> preprocessedHt = getAllHighestTerms(preprocessed);
        List<Polynom> res = new ArrayList<Polynom>();
        Utils.normalize(ring, polsAfterEchelon, inQ);

        // Add only reduced polynomials in result of reduction.
        for (Polynom p : polsAfterEchelon) {
            if (!preprocessedHt.contains(p.hTerm()) && !res.contains(p)) {
                res.add(p);
            }
        }
        return res;
    }

    /**
     * Special version of preprocessing for interreducing (consumes also HT)
     *
     * @param pairs polynomials from left and right pairs (each t*f from pairs)
     * @param arr set of polynomials
     *
     * @return set of polynomials with non-highest termsPP ready to elimination
     */
    private List<Polynom> symPpWithHt(List<Polynom> pairs, List<Polynom> reductors) {
        List<Polynom> res = new ArrayList<Polynom>(pairs);

        List<Term> done = new ArrayList<Term>();
        List<Term> tf = Utils.getAllTerms(res);
        Collections.sort(tf, Collections.reverseOrder());

        while (done.size() != Utils.getAllTerms(res).size()) {
            Term reducibleTerm = tf.remove(0);
            done.add(reducibleTerm);
            Polynom reductor = getTopReductorOfTerm(reducibleTerm, reductors);
            if (null != reductor) {
                Term multiplierForReduction = reducibleTerm.
                        div(reductor.hTerm());
                Polynom added = multiplierForReduction.multiply(reductor);
                res.add(added);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Reductor of m = {} is f = {} (m' = {})",
                            new Object[] {
                                reducibleTerm.toString(ring),
                                reductor.toString(ring),
                                multiplierForReduction.toString(ring)
                            });
                }
                for (int i = 0; i < added.coeffs.length; i++) {
                    if (!(tf.contains(added.term(i))
                            || done.contains(added.term(i)))) {
                        tf.add(added.term(i));
                    }
                }
            }
        }

        return res;
    }

    /**
     * Reduction that allows picking Highest Terms in preprocessing.
     *
     * @param reducible list of reducible polynomimals
     * @param reductors list of reductors
     *
     * @return result of reduction
     */
    public List<Polynom> reductionWithHT(List<Polynom> reducible,
            List<Polynom> reductors) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Reducible: {}", Element.colToStr(ring, reducible));
            LOG.trace("Reductors: {}", Element.colToStr(ring, reductors));
        }
        List<Polynom> preprocessed = symPpWithHt(reducible, reductors);
        if (preprocessed.size() == reducible.size()) {
            // can't reduce
            LOG.trace("SymPP did nothing - can't reduce");
            return new ArrayList<Polynom>();
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Reducible after symPP: {}",
                    Element.colToStr(ring, preprocessed));
        }
        List<Term> termsPP = Utils.getAllTerms(preprocessed);
        Collections.sort(termsPP, Collections.reverseOrder());
        MatrixS matrix = matrixOfCoeffs(preprocessed, termsPP);
        MatrixS matrixEch = matrix.toEchelonForm(ring);
        if (matrixEch.M.length < preprocessed.size()) {
            // reduced to zero
            LOG.trace("Rows count after echelon < preprocessed count. Reduced to zero - drop it.");
            return new ArrayList<Polynom>(Arrays.asList(Polynom.polynomZero));
        }
        List<Polynom> polsAfterEchelon = polsFromMatrix(matrixEch, termsPP);
        if (LOG.isTraceEnabled()) {
            LOG.trace("After echelon form: {}",
                    Element.colToStr(ring, polsAfterEchelon));
        }
        List<Polynom> res = new ArrayList<Polynom>();
        res.add(polsAfterEchelon.get(0));

        return res;
    }

    /**
     * Main method implementing F4 algorithm.
     */
    private void constructBasis() {
        // 1) Copy input ideal co current basis.
        List<Polynom> currentBasis = new ArrayList<Polynom>(ideal);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Ideal: {}",
                    Element.colToStr(ring, currentBasis));
        }
        // 2) Initialize critical pairs.
        List<Pair> pairs = initPairs(currentBasis);

        while (!pairs.isEmpty() && !Thread.currentThread().isInterrupted()) {
            // 3) Get and remove pairs.
            List<Pair> selectedPairs = selectPairs(pairs);
            List<Polynom> pairsSelectedLR = new ArrayList<Polynom>();
            // get left and right pairs of selected pairs
            for (Pair pair : selectedPairs) {
                pairsSelectedLR.add(pair.leftPart());
                pairsSelectedLR.add(pair.rightPart());
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Selected pairs: {}",
                        Element.colToStr(ring, pairsSelectedLR));
            }
            // 4) Add left and right parts of selected pairs and reduce them.
            List<Polynom> reducedPolsToAdd = reduction(pairsSelectedLR, currentBasis);
            List<Term> currentBasisHT = getAllHighestTerms(currentBasis);
            // 5) Update LR-parts - get polynomials before reduction
            // and make pairs with polynomials after reduction.
            // And also update current basis.
            for (Polynom someOfReduced : reducedPolsToAdd) {
                if (!currentBasisHT.contains(someOfReduced.hTerm())) {
                    for (Polynom someOfCurrentBasis : currentBasis) {
                        pairs.add(new Pair(ring, someOfReduced, someOfCurrentBasis));
                    }
                    currentBasis.add(someOfReduced);
                }
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Reductors after reduction: {}",
                        Element.colToStr(ring, currentBasis));
            }
            pairsSelectedLR.clear();
        }
        basis = currentBasis;
    }

    /**
     * Reduces constructed basis.
     */
    private void interreduce() {
        LOG.trace("======= Starting interreduce =======\n\n");
        ListIterator<Polynom> it = basis.listIterator();
        for (int i = 0; it.hasNext(); i = it.nextIndex()) {
            Polynom p = it.next();
            ArrayList<Polynom> reducible = new ArrayList<Polynom>();
            reducible.add(p);
            ArrayList<Polynom> reductors
                    = new ArrayList<Polynom>(basis.subList(0, i));
            reductors.addAll(basis.subList(i + 1, basis.size()));
            List<Polynom> reduced = reductionWithHT(reducible, reductors);
            if (reduced.size() == 1 && reduced.get(0).isZero(ring)) {
                it.remove();
            } else if (reduced.size() == 1 && !basis.contains(reduced.get(0))) {
                it.set(reduced.get(0));
            }
        }
    }

    /**
     * Facade method constructing reduced Groebner basis using F4 algorithm.
     *
     * @return reduced Groebner basis.
     */
    @Override
    public List<Polynom> gbasis() {
        constructBasis();
        Utils.normalize(ring, basis, inQ);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Basis after normalize(): {}",
                    Element.colToStr(ring, basis));
        }
        interreduce();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interreduced basis: {}", Element.colToStr(ring, basis));
        }
        if (inQ) {
            Utils.polsOverZtoQ(ring, basis);
        }
        Utils.normalize(ring, basis, inQ);
        return new ArrayList<Polynom>(basis);
    }
}
