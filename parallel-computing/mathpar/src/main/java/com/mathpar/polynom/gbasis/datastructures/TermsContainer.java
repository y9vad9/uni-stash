package com.mathpar.polynom.gbasis.datastructures;

import java.util.List;
import com.mathpar.polynom.Term;

/**
 * Container with sorted list of terms.
 *
 * @author Ivan Borisov
 */
public interface TermsContainer {

    /**
     * @return list with terms in container.
     */
    List<Term> terms();

    /**
     * @return size of container.
     */
    int size();

    /**
     * Adds given term to container.
     *
     * @param termToAdd term to add.
     * @return two-dimensional int array with 2 elements: first is the map for
     * new indexes, second is the inserted term index (-1 if term presents in
     * container).
     */
    int[][] add(Term termToAdd);

    /**
     * Adds given list with terms to container.
     *
     * @param termsToAdd list with terms to add.
     * @return two-dimensional int array with 2 elements: first is the map for
     * new indexes (e. g. (1, 3, 5) means that there is the map: 0->1, 1->3,
     * 2->5), second is the indexes of inserted terms.
     */
    int[][] addAll(List<Term> termsToAdd);

    /**
     * Adds given list with terms to container sorting it if needed.
     *
     * @param termsToAdd list with terms to add.
     * @param doSort perform sort of
     * <code>termsToAdd</code> if
     * <code>true</code>
     * @return 2d int array with 2 elements: first is the map for new indexes
     * (e. g. (1, 3, 5) means that there is the map: 0->1, 1->3, 2->5), second
     * is the indexes of inserted terms.
     */
    int[][] addAll(List<Term> termsToAdd, boolean doSort);

    /**
     * @param term term to find an index.
     * @return index of given term
     */
    int indexOf(Term term);

    /**
     * @param index index of term.
     * @return term with given index.
     */
    Term get(int index);
}
