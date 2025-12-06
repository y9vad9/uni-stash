package com.mathpar.polynom.gbasis.datastructures;

import java.util.*;

import static com.mathpar.number.Array.copyOf;

import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Term;

import static com.mathpar.number.Array.copyOf;
import static com.mathpar.number.Array.copyOf;
import static com.mathpar.number.Array.copyOf;
import static com.mathpar.number.Array.copyOf;
import static com.mathpar.number.Array.copyOf;
import static com.mathpar.number.Array.copyOf;
import static com.mathpar.number.Array.copyOf;

/**
 * Stores sorted list of polynomial terms.
 *
 * @author ivan
 */
public class TermsList implements TermsContainer {
    private final Ring ring;
    private List<Term> terms = new ArrayList<Term>();
    private static final Comparator<Term> CMP = Collections.reverseOrder();

    /**
     * Constructs TermStore with empty terms list.
     *
     * @param ring ring.
     */
    public TermsList(Ring ring) {
        this.ring = ring;
    }

    /**
     * Constructs TermStore with given list of terms and sorts it if necessary.
     *
     * @param ring ring.
     * @param terms terms list.
     * @param doSort flag to perform.
     */
    public TermsList(Ring ring, List<Term> terms, boolean doSort) {
        this(ring);
        this.terms = new ArrayList<Term>(terms);
        if (doSort) {
            Collections.sort(this.terms, CMP);
        }
    }

    /**
     * Constructs TermStore with given sorted (desc) list of terms.
     *
     * @param ring ring.
     * @param terms terms list.
     */
    public TermsList(Ring ring, List<Term> terms) {
        this(ring, terms, false);
    }

    @Override
    public List<Term> terms() {
        return Collections.unmodifiableList(terms);
    }

    @Override
    public int size() {
        return terms.size();
    }

    /**
     * Adds given term to container.
     *
     * @param termToAdd term to add.
     *
     * @return two-dimensional int array with 2 elements: first is the map for
     * new indexes, second is the inserted term index (-1 if term presents in
     * container).
     */
    @Override
    public int[][] add(Term termToAdd) {
        // Get old indexes.
        int[] res = range();
        // Insertion
        int searchResult = Collections.binarySearch(terms, termToAdd, CMP);
        int insertIndex;
        if (searchResult < 0) {
            // Term not found -- insert it.
            insertIndex = -searchResult - 1;
            terms.add(insertIndex, termToAdd);
            // Increment old indexes from insertion index to the end of old
            // indexes.
            for (int i = insertIndex; i < res.length; i++) {
                res[i]++;
            }

            return new int[][] {res, new int[] {insertIndex}};
        } else {
            // Term found -- don't need to insert.
            return new int[][] {res, new int[] {-1}};
        }
    }

    /**
     * @param termsToAdd list of terms to add to this TermsList.
     *
     * @return two-dimensional array: first element is array with the map of
     * changed indexes, second is array with indexes of inserted terms ({-1} if
     * no terms were added).
     */
    @Override
    public int[][] addAll(List<Term> termsToAdd) {
        if (termsToAdd.isEmpty()) {
            return new int[][] {range(), new int[] {-1}};
        }

        Collections.sort(termsToAdd, Collections.reverseOrder());

        int[] map = range();
        // Reserve place for all indexes with -1. Cut by first -1 after
        // insertion of all terms.
        int[] tmpIndexes = rangeMinusOne(termsToAdd.size());

        int insertedCnt = 0;
        for (int i = 0; i < termsToAdd.size(); i++) {
            int[][] addRes = add(termsToAdd.get(i));
            int insertedIndex = addRes[1][0];
            if (insertedIndex >= 0) {
                tmpIndexes[insertedCnt] = insertedIndex;
// change the map if current term was inserted not to the end of TermsContainer.
                if (insertedIndex < map.length + insertedCnt) {
                    for (int j = Arrays.binarySearch(map, insertedIndex);
                            j < map.length; j++) {
                        try {
                            map[j]++;
                        } catch (RuntimeException t) {
                            System.out.println("t");
                            throw t;
                        }
                    }
                }
                insertedCnt++;
            }
        }
        if (insertedCnt == 0) {
            return new int[][] {map, new int[] {-1}};
        }
        // Remove all -1 elements from tail of tmpIndexes.
        return new int[][] {map, copyOf(tmpIndexes, insertedCnt)};
    }

    @Override
    public int[][] addAll(List<Term> termsToAdd, boolean doSortTermsToAdd) {
        if (doSortTermsToAdd) {
            List<Term> sortedTermsToAdd = new ArrayList<Term>(termsToAdd);
            Collections.sort(sortedTermsToAdd, CMP);
            return addAll(sortedTermsToAdd);
        } else {
            return addAll(termsToAdd);
        }
    }

    @Override
    public int indexOf(Term term) {
        return Collections.binarySearch(terms, term, CMP);
    }

    @Override
    public Term get(int index) {
        return terms.get(index);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!(other instanceof TermsList)) {
            return false;
        }

        TermsList ts = (TermsList) other;
        return ts.terms().equals(terms);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.terms != null ? this.terms.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return Element.colToStr(ring, terms);
    }

    private int[] range() {
        int[] res = new int[terms.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = i;
        }
        return res;
    }

    private int[] rangeMinusOne(int len) {
        int[] res = new int[len];
        for (int i = 0; i < len; i++) {
            res[i] = -1;
        }
        return res;
    }
}
