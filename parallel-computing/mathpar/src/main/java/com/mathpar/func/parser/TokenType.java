package com.mathpar.func.parser;

/**
 * Contains token types for {@code Token} class.
 *
 * @see Token
 */
enum TokenType {
    /**
     * Real numbers.
     */
    NUM,
    /**
     * Polynomial variables.
     */
    VAR,
    /**
     * Number sets.
     */
    SET,
    /**
     * Polynomials with integer coefficients.
     */
    POL_Z,
    /**
     * Polynomials with real coefficients.
     */
    POL_R,
    /**
     * Operations "+ * - /".
     */
    OP_LA,
    /**
     * Operation "^".
     */
    OP_RA,
    /**
     * Postfix operation "!".
     */
    OP_POSTFIX,
    /**
     * Boolean operations.
     */
    OP_BOOL,
    /**
     * Functions.
     */
    OP_FUNC,
    /**
     * Vector opening brace "[".
     */
    VEC_BR_OP,
    /**
     * Vector closing brace "]".
     */
    VEC_BR_CL,
    /**
     * Set opening brace "\( \[".
     */
    SET_BR_OP,
    /**
     * Set closing brace "\) \]".
     */
    SET_BR_CL,
    /**
     * VectorSet opening brace "\{".
     */
    VECTOR_SET_BR_OP,
    /**
     * VectorSet closing brace "\}".
     */
    VECTOR_SET_BR_CL,
    /**
     * Opening braces "( {".
     */
    BR_OP,
    /**
     * Closing braces ") }".
     */
    BR_CL,
    /**
     * Function's arguments separator ",".
     */
    ARG_SEP,
    /**
     * Lower index separator "_" (e.g. A_{i, j}).
     */
    UNDERSCORE,
    /**
     * Other symbols.
     */
    SYMBOLIC,
    /**
     * End of the line.
     */
    EOL;
}
