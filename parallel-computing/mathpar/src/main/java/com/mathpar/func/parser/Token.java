package com.mathpar.func.parser;

import com.mathpar.func.Fname;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.number.SubsetR;
import com.mathpar.polynom.Polynom;

/**
 * Class representing token for {@code ParserImpl}.
 *
 * @author ivan
 * @see TokenType
 */
class Token {
    /**
     * Token type.
     */
    private TokenType type;
    /**
     * String representation of token.
     */
    private String str;
    /**
     * List of notnull arguments (for {@code OP_FUNC} and {@code SYMBOLIC}).
     * В нормальном случае 3-х аргумент, тут будет = (0,1,2), 
     * но если пришел null во втором аргументе, то будет тут (0,2)
     * Полное число аргументов всегда есть в поле "argsNum"
     */
    private List<Integer> indNotNull;
    /**
     * Number of arguments (for {@code OP_FUNC} and {@code SYMBOLIC}).
     */
    private int argsNum = -1;
    /**
     * Was there spaces before token.
     */
    private boolean wasSpace = false;
    /**
     * for numbers &mdash; value (numeric object); for variables &mdash; index
     * in varNames array of ring; for monomials &mdash; polynom object; for
     * symbols &mdash; null.
     */
    private Element value;

    /**
     * Main constructor.
     *
     * @param type token type
     * @param str string form of token
     * @param ring ring
     */
    protected Token(TokenType type, String str, Ring ring) {
        int powers[];
        int powerLen;

        this.str = str;
        this.type = type;
        value = null;

        switch (type) {
            case SET:
                value = new SubsetR(this.str, ring);
                break;
            case SYMBOLIC:
                if (this.str.equals("\\i")) {
                    this.type = TokenType.POL_Z;
                    value = new Polynom(ring.numberI);
                } else {
                    this.type = type;
                    // XXX: hack for printing new lines with "\print" function
                    value = new Fname(this.str.replaceAll("\\\\n", "\n"));
                }
                break;
            // Index separator contains string as Fname object like SYMBOLIC.
            case UNDERSCORE:
                value = new Fname(this.str);
                break;
            case NUM:
                if (str.contains(".")) {
                    // Floating point number.
                    this.type = TokenType.POL_R;
                    int numType = ring.algebra[0];
                    // Truncate after dot if main algbera is over integer
                    // numbers or .
                    if (numType >= Ring.Z64 && numType <= Ring.Zp
                            || numType == Ring.Q) {
                        int dotInd = this.str.indexOf('.');
                        this.str = str.substring(0, dotInd);
                    }
                    value = Utils.getPolynomFromNumber(ring, numType, this.str);
                } else {
                    // Integer number.
                    int intType = Utils.getFirstIntegerType(ring);
                    if (intType >= 0) {
                        this.type = TokenType.POL_Z;
         /* ??? */      value = Utils.getPolynomFromNumber(ring, intType, this.str);
                    } else {
                        this.type = TokenType.POL_R;
                        value = Utils.getPolynomFromNumber(ring, ring.algebra[0], this.str);
                    }
                }
                break;
            case VAR:
                powerLen = Utils.getVarIndex(ring, this.str) + 1;
                powers = new int[powerLen];
                powers[powerLen - 1] = 1;
                this.type = TokenType.POL_Z;
                // XXX: hack for NumberOne in Q.
                Element one = ring.algebra[0] == Ring.Q ? NumberZ.ONE : ring.numberONE;
                value = new Polynom(powers, new Element[] {one});
                break;
            case OP_BOOL:
                // XXX: hack for boolean NOT.
                if (str.equals("\\neg")) {
                    this.type = TokenType.OP_FUNC;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return str;
        // Uncomment from time to time for debugging
        //return (str + "|" + type);// + "," + value + ")");
    }

    public TokenType type() {
        return type;
    }

    public String str() {
        return str;
    }

    public Element value() {
        return value;
    }

    public void str(String str) {
        this.str = str;
    }

    public void type(TokenType type) {
        this.type = type;
    }

    public Token value(Element value) {
        this.value = value;
        return this;
    }

    public int argsNum() {
        return argsNum;
    }

    public List<Integer> indNotNull() {
        return Collections.unmodifiableList(indNotNull);
    }

    public Token indNotNull(List<Integer> indNotNull) {
        this.indNotNull = (indNotNull == null) ? new ArrayList<Integer>()
                : new ArrayList<Integer>(indNotNull);
        return this;
    }

    public Token argsNum(int argsNum) {
        this.argsNum = argsNum;
        return this;
    }

    public boolean hasSpaceBefore() {
        return wasSpace;
    }

    public void hasSpaceBefore(boolean wasSpace) {
        this.wasSpace = wasSpace;
    }

    public boolean isPolynomialNumber() {
        if (!(value instanceof Polynom)) {
            return false;
        }

        Polynom pol = (Polynom) value;
        return pol.isItNumber();
    }

    public boolean isPolynomialVar() {
        if (!(value instanceof Polynom)) {
            return false;
        }

        Polynom pol = (Polynom) value;
        int m = 0;
        for (int i : pol.powers) {
            if (i > 0) {
                m++;
            }
        }

        return m == 1;
    }

    public boolean hasFuncType() {
        return type == TokenType.OP_FUNC;
    }

    public boolean hasSymbolicType() {
        return type == TokenType.SYMBOLIC;
    }

    public boolean hasPolZType() {
        return type == TokenType.POL_Z;
    }

    public boolean hasVarType() {
        return type == TokenType.POL_Z
                || type == TokenType.POL_R
                && !isPolynomialNumber();
    }
}
