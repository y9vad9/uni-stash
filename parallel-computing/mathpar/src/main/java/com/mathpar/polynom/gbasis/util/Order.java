package com.mathpar.polynom.gbasis.util;

import com.mathpar.polynom.Term;

/**
 * Contains terms orders in form of {@code Comparator}.
 *
 * @author ivan
 */
public enum Order implements java.util.Comparator<Term> {
    /**
     * Lexicographical.
     */
    LEX() {
                @Override
                public int compare(Term t1, Term t2) {
                    return t2.compareTo(t1);
                }
            },
    /**
     * Reversed lexicographical is the default order.
     */
    REVLEX() {
                @Override
                public int compare(Term t1, Term t2) {
                    return t1.compareTo(t2);
                }
            },
    /**
     * Degree reversed lexicographical.
     */
    DEGREVLEX() {
                @Override
                public int compare(Term t1, Term t2) {
                    int t1Deg = t1.deg(), t2Deg = t2.deg();
                    if (t1Deg > t2Deg) {
                        return 1;
                    } else if (t1Deg < t2Deg) {
                        return -1;
                    }
                    return t1.compareTo(t2);
                }
            };
}
