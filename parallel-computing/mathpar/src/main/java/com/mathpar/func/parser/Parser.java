package com.mathpar.func.parser;

import com.mathpar.func.F;
import com.mathpar.func.Fname;
import com.mathpar.number.Ring;
import java.util.ArrayList;

import java.util.List;

/**
 * Public interface for {@code ParserImpl} invocation.
 *
 * @author ivan
 * @see ParserImpl
 */
public final class Parser {

    /**
     * Don't need to construct this.
     */
    private Parser() {
    }

    /**
     * Makes {@code F} instance with {@code F.ID} name containing
     * {@code Polynom} object from given {@code expression}. You must extract
     * {@code Polynom} manually.
     *
     * @param expression expression to parse
     * @param r          ring
     * @return {@code F} instance with polynomial inside
     */
    public static F getPol(final String expression, final Ring r) {
        return getF(expression, r);
    }

    /**
     * Makes {@code F} instance from given {@code expression}.
     *
     * @param expression expression to parse
     * @param r          ring
     * @return {@code F} instance
     */
    public static F getF(final String expression, final Ring r) {
        try { 
            return new ParserImpl(expression, r).getF();
        } catch (Exception e) {
            throw new ParserException("Parser error for: " + expression, e);
        }
    }

    /**
     * Makes {@code F} instance from given {@code expression}.
     *
     * @param expression           expression to parse
     * @param userFunc             {@code List} with user-defined functions names
     * @param ring                 ring
     * @param nonCommutWithIndices {@code List} with non-commutative {@code Fname}.
     *                             Only {@code SYMBOLIC} tokens from this list are
     *                             specially processed for lower/upper LaTeX-style indices
     *                             (e.g.: A_{1, 2}; B_{1}^{2}) to extract those indices
     *                             and put them to {@code Fname} fields.
     * @return {@code F} instance
     */
    public static F getF(
            final String expression, final Ring ring,
            final List<String> userFunc, final List<Fname> nonCommutWithIndices) {
//        try {
            return new ParserImpl(expression, ring, userFunc, nonCommutWithIndices).getF();
 //       } catch (Exception e) {
 //           throw new ParserException("Parser error for: " + expression, e);
  //      }
    }
}
