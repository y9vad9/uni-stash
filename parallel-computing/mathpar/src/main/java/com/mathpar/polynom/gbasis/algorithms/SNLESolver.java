package com.mathpar.polynom.gbasis.algorithms;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.gbasis.util.Utils;

import java.util.*;
import org.apache.logging.log4j.Logger;

/**
 * Solves Systems of NonLinear Equations (polynomial) with Groebner basis (if
 * possible).
 */
public class SNLESolver {
    private static final Logger LOG = getLogger(SNLESolver.class);
    /**
     * System of nonlinear (polynomial) equations.
     */
    private List<Polynom> system;
    /**
     * Ring.
     */
    private Ring ring;

    /**
     * @param ring   ring.
     * @param system system of polynomial equations.
     */
    public SNLESolver(Ring ring, List<Polynom> system) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Solving system: {}", Element.colToStr(ring, system));
        }
        this.system = system;
        this.ring = ring;
    }

    /**
     * @param ring   ring.
     * @param system system of polynomials equations.
     */
    public SNLESolver(Ring ring, Polynom... system) {
        this(ring, Arrays.asList(system));
    }

    /**
     * @param ring          ring.
     * @param systemStrings system of polynomials equations in String form.
     */
    public SNLESolver(Ring ring, String... systemStrings) {
        this(ring, Utils.polList(ring, systemStrings));
    }

    /**
     * Solves polynomial (non-linear) system of equations.
     *
     * @return list of maps with roots (eg.:
     * {@code [{x: x1, y: y1}, {x: x1, y: y2}]} , where {@code x, y} are unknown
     * variables (Polynom objects) and {@code x1, y1, y2} are roots
     * ({@code Complex} objects)).
     */
    public List<SortedMap<Polynom, Element>> solve() {
        List<SortedMap<Polynom, Element>> res;
        List<Polynom> gbasis = new Faugere(ring, system).gbasis();
        if (LOG.isTraceEnabled()) {
            LOG.trace("basis: {}", Element.colToStr(ring, gbasis));
        }
        res = solveStep(gbasis);
        return res;
    }

    /**
     * Recursively solve one-var equatioins and substitute corresponding vars
     * with roots.
     *
     * @param system list of polynomials representing system.
     * @return list of maps with roots (eg.:
     * {@code [{x: x1, y: y1}, {x: x1, y: y2}]}, where {@code x, y} are unknown
     * variables (Polynom objects) and {@code x1, y1, y2} are roots
     * ({@code Complex} objects)).
     * @throws IllegalArgumentException if {@code system} doesn't have any
     *                                  one-var equation.
     * @throws ArithmeticException      if error in roots computation has occurred.
     */
    private List<SortedMap<Polynom, Element>> solveStep(List<Polynom> system) {
        List<SortedMap<Polynom, Element>> res = new ArrayList<>();
        List<Polynom> oneVarPols = getOneVarPolys(system);
        if (oneVarPols.isEmpty() && !system.isEmpty()) {
            // TODO: 0 = 0; stop the whole process, not single step.
            // Return the res of the system mapped to ring.polynomONE.
            SortedMap<Polynom, Element> systemMap = new TreeMap<>();
            systemMap.put(ring.polynomONE, new VectorS(system.toArray(new Element[system.size()])));
            res.add(systemMap);
            return res;
        }
        // Is it recursion base case? -- current system contains only one-var polynomials.
        // Return computed roots from leaves if true.
        boolean onlyFindingRoots = system.size() == oneVarPols.size();
        // Remove one-var polynomials from system first.
        for (Polynom oneVarPol : oneVarPols) {
            system.remove(indexOf(ring, system, oneVarPol));
        }
        SortedMap<Polynom, Element[]> varPolsToRoots = getRootsOfOneVarPols(oneVarPols);
        List<SortedMap<Polynom, Element>> oneVarPolsSolutions = new ArrayList<>();
        combinations(varPolsToRoots, oneVarPolsSolutions);


        List<SortedMap<Polynom, Element>> resTmp = new ArrayList<>();
        for (SortedMap<Polynom, Element> oneVarPolsSolution : oneVarPolsSolutions) {
            if (onlyFindingRoots) {
                resTmp.add(oneVarPolsSolution);
            } else {
                List<Polynom> systemWithSubstitutedPols = substituteSystemWithRoots(system, oneVarPolsSolution);
                // Join roots from current level with already computed.
                for (SortedMap<Polynom, Element> m : solveStep(systemWithSubstitutedPols)) {
                    m.putAll(oneVarPolsSolution);
                    res.add(m);
                }
            }
        }
        if (onlyFindingRoots) {
            res = resTmp;
        }
        return res;
    }

    private List<Polynom> substituteSystemWithRoots(List<Polynom> system, Map<Polynom, Element> oneVarPolsSolution) {
        // Make a copy of system then substitute each polynomial for current set of roots.
        List<Polynom> systemWithSubstitutedPols = new ArrayList<>(system);
        for (int j = 0, systemSize = system.size(); j < systemSize; j++) {
            // Remove current polynomial from system.
            Polynom toSubs = systemWithSubstitutedPols.remove(0);
            List<Polynom> varPols = getVarPols(ring, toSubs);
            Element[] varValues = varPols.toArray(new Element[varPols.size()]);
            for (Map.Entry<Polynom, Element> varToRoot : oneVarPolsSolution.entrySet()) {
                Polynom currVarPol = varToRoot.getKey();
                Element root = varToRoot.getValue();
                // Substitute var of polynomial with all roots and add
                // all resulting polynomials to current system.
                int varIndex = indexOf(ring, varPols, currVarPol);
                if (varIndex >= 0) {
                    varValues[varIndex] = root;
                }
            }
            Element substituted = toSubs.value(varValues, ring);
            if (!(substituted instanceof Polynom)) {
                substituted = new Polynom(substituted);
            }
            systemWithSubstitutedPols.add((Polynom) substituted);
        }
        return systemWithSubstitutedPols;
    }

    /**
     * @param oneVarPols list of polynomials with single variable.
     * @return map of {varPolynomial -> array of roots} for each polynomial from input list.
     */
    private SortedMap<Polynom, Element[]> getRootsOfOneVarPols(List<Polynom> oneVarPols) {
        SortedMap<Polynom, Element[]> varToRoots = new TreeMap<>();
        for (Polynom oneVarPol : oneVarPols) {
            Polynom currVarPol = getVarOfOneVarPolynom(ring, oneVarPol);
            // Hack for computing roots -- move variable to the first position.
            int varInd = indexOf(ring, getVarPols(ring, oneVarPol), currVarPol);
            Polynom oneVarPolSwapped = oneVarPol.swapVars(0, varInd);
            Element[] roots = null;
            try {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Trying to solve: {} (swapped to {})",
                            oneVarPol.toString(ring), oneVarPolSwapped.toString(ring));
                }
                roots = oneVarPolSwapped.rootOfEC(ring);
                varToRoots.put(currVarPol, roots);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("roots: {}", Arrays.toString(roots));
                }
            } catch (Exception e) {
                LOG.error(String.format("Error computing roots of %s", oneVarPol.toString(ring)), e);
                throw (ArithmeticException) new ArithmeticException(
                        String.format("Error computing roots of polynomial %s (swapped to %s).",
                                oneVarPol.toString(ring), oneVarPolSwapped.toString(ring)))
                        .initCause(e);
            }
        }
        return varToRoots;
    }

    /**
     * @param map  map {varPolynomial -> array of roots}.
     * @param list list of maps with combinations {varPolynomial -> root}.
     * @param <K>  key type
     * @param <V>  value type
     */
    public static <K, V> void combinations(SortedMap<K, V[]> map, List<SortedMap<K, V>> list) {
        recurse(map, new LinkedList<K>(map.keySet()).listIterator(), new TreeMap<K, V>(), list);
    }

    // helper method to do the recursion
    private static <K, V> void recurse(SortedMap<K, V[]> map, ListIterator<K> iter,
                                       SortedMap<K, V> cur, List<SortedMap<K, V>> list) {
        // we're at a leaf node in the recursion tree, add solution to list
        if (!iter.hasNext()) {
            SortedMap<K, V> entry = new TreeMap<>();
            for (K key : cur.keySet()) {
                entry.put(key, cur.get(key));
            }
            list.add(entry);
        } else {
            K key = iter.next();
            V[] set = map.get(key);
            for (V value : set) {
                cur.put(key, value);
                recurse(map, iter, cur, list);
                cur.remove(key);
            }
            iter.previous();
        }
    }

    /**
     * @param p polynomial.
     * @return (actual) number of variables of given polynomial.
     */
    private static int getVarsNumber(Polynom p) {
        // numberOfVariables() in Polynom do it more quiсkly !! G.I. ###
        int monomialsCnt = p.coeffs.length;
        int maxVarsCnt = p.powers.length / monomialsCnt;
        int[] varAccumulator = new int[maxVarsCnt];
        int[] powers = p.powers;
        for (int i = 0; i < monomialsCnt; i++) {
            for (int j = 0; j < maxVarsCnt; j++) {
                varAccumulator[j] += powers[i * maxVarsCnt + j];
            }
        }
        int nonZeroVarsCnt = 0;
        for (int i = 0; i < maxVarsCnt; i++) {
            if (varAccumulator[i] > 0) {
                nonZeroVarsCnt++;
            }
        }
        return nonZeroVarsCnt;
    }

    /**
     * @param ring ring.
     * @param p    polynomial.
     * @return list with one-var polynomials for each var of given polynomial.
     */
    private static List<Polynom> getVarPols(Ring ring, Polynom p) {
        List<Polynom> res = new ArrayList<Polynom>();
        int varsCount = p.getVarsNum();
        for (int i = 0; i < varsCount; i++) {
            int[] polPows = new int[varsCount];
            // Set only i-th var to 1.
            polPows[i] = 1;
            res.add(new Polynom(polPows, new Element[]{ring.numberONE}).
                    truncate());
        }
        return res;
    }

    /**
     * @param ring ring.
     * @param l    list of polynomials.
     * @param p    polynomial.
     * @return index of given polynomial in given list of polynomials. -1 if p
     * is not in l.
     */
    private static int indexOf(Ring ring, List<Polynom> l, Polynom p) {
        int size = l.size();
        for (int i = 0; i < size; i++) {
            if (l.get(i).equals(p, ring)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param ring ring.
     * @param p    one-var Polynom.
     * @return Polynom representing var of given one-var Polynom.
     */
    private static Polynom getVarOfOneVarPolynom(Ring ring, Polynom p) {
        int[] pows = p.hTerm().powers();
        for (int i = 0; i < pows.length; i++) {
            if (pows[i] != 0) {
                pows[i] = 1;
            }
        }
        return new Polynom(pows, new Element[]{ring.numberONE});
    }

    /**
     * Searches index of polynomial with only one var in list of polynomials.
     *
     * @param gbasis list of polynomials containing Groebner basis.
     * @return index of polynomial with only one variable in basis.
     */
    private static List<Polynom> getOneVarPolys(List<? extends Element> gbasis) {
        List<Polynom> res = new ArrayList<Polynom>();
        for (int i = 0; i < gbasis.size(); i++) {
            if ((gbasis.get(i) instanceof Polynom)) {
                Polynom currPol = (Polynom) gbasis.get(i);
                int varNum = getVarsNumber(currPol);
                if (varNum == 1) {
                    res.add(currPol);
                }
            }
        }
        return res;
    }
}
