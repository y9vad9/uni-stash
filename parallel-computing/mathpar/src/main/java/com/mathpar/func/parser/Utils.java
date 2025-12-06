package com.mathpar.func.parser;

import com.mathpar.func.F;
import com.mathpar.number.*;
import com.mathpar.polynom.Polynom;

import java.util.*;

import static com.mathpar.func.F.VECTORS;
import static com.mathpar.func.F.getNameFromStr;

/**
 * Some static methods for Parser support.
 */
final class Utils {
    /**
     * Don't need to construct this.
     */
    private Utils() {
    }

    /**
     * Returns priority of given operator.
     * <p/>
     * Operators precedence table (from higher to lower): 8) ! (postfix,
     * factorial) 7) ^ (infix, power) 6) * / (infix multiplicative) 5) + -
     * (infix additive) 4) {@literal <}, {@literal >}, \le (less or equal), \ge
     * (greater or equal) (infix relational) 3) ==, \ne (not equal) (infix
     * equality) 2) \& (infix logical AND) 1) \lor (infix logical OR)
     *
     * @param token token of operator.
     * @return operator's priority.
     */
    static int getPriority(final Token token) {
        int result = -1;
        String str = token.str();
        if (str.equals("\\lor")) {
            result = 10;
        } else if (str.equals("\\&")) {
            result = 20;
        } else if (str.equals("==") || str.equals("\\ne")) {
            result = 30;
        } else if (str.equals("<") || str.equals("\\le")
                || str.equals(">") || str.equals("\\ge")) {
            result = 40;
        } else if (str.equals("+") || str.equals("-") || str.equals("\\cup")) {
            return 50;
        } else if (str.equals("*") || str.equals("/") || str.equals("\\cap")) {
            return 60;
        } else if (str.equals("^")) {
            return 70;
        } else if (str.equals("!")) {
            return 80;
        }
        return result;
    }

    /**
     * Return index of algebra where given variable is located.
     *
     * @param ring ring
     * @param var  string with variable name
     * @return index of algebra from which given var is
     */
    static int getAlgebraIndex(final Ring ring, final String var) {
        int res = -1;
        int varIndex = getVarIndex(ring, var);
        int lowInd = 0;
        int highInd;

        for (int i = 0; i < ring.algebraNumb; i++) {
            highInd = ring.varLastIndices[i];
            if (varIndex >= lowInd && varIndex <= highInd) {
                return i;
            }
            lowInd = highInd;
        }
        return res;
    }

    /**
     * Return algebra type of given var.
     *
     * @param ring ring
     * @param var  string with variable name
     * @return type of algebra from which {@code var} is
     */
    static int getAlgebraType(final Ring ring, final String var) {
        int algebraIndex = getAlgebraIndex(ring, var);
        if (algebraIndex == -1) {
            throw new IllegalArgumentException(String.format(
                    "Can't find var %s among variables of ring %s", var, ring));
        }
        return ring.algebra[algebraIndex];
    }

    /**
     * Returns index of first integer algebra in given ring.
     *
     * @param ring ring
     * @return index of first integer type in {@code ring}
     */
    static int getFirstIntegerType(final Ring ring) {
        for (int i = 0; i < ring.algebra.length; i++) {
            int currAlgebra = ring.algebra[i];
            if (currAlgebra >= Ring.Z64 && currAlgebra <= Ring.Zp) {
                return currAlgebra;
            }
        }

        return -1;
    }

    /**
     * Returns index of given variable in ring.
     *
     * @param ring ring
     * @param var  string with variable
     * @return index of variable
     */
    static int getVarIndex(final Ring ring, final String var) {
        int res = -1;
        for (int i = 0; i < ring.varNames.length; i++) {
            if (var.equals(ring.varNames[i])) {
                res = i;
                break;
            }
        }
        return res;
    }

    /**
     * Converts number from given string to polynomial.
     *
     * @param ring ring
     * @param type number type
     * @param s    string with number
     * @return polynomial from number
     */
    static Polynom getPolynomFromNumber(final Ring ring, final int type,
                                        final String s) {
        Element el;
        switch (type) {
            // Zp32 and Zp are initially Z64 and Z for polynomials powers.
            case Ring.Z64:
            case Ring.Zp32:
                el = new NumberZ64(s);
                break;
            case Ring.Z:
            case Ring.Zp:
                el = new NumberZ(s);
                break;
            case Ring.Q:
                el = new NumberZ(s);
                break;
            case Ring.R:
                el = new NumberR(s);
                break;
            case Ring.R128:
                el = new NumberR128(new NumberR(s));
                break;
            case Ring.R64:
                el = new NumberR64(s);
                break;
            case Ring.CZ64:
                el = new Complex(new NumberZ64(s), ring);
                break;
            case Ring.CZp32:
                el = new Complex(new NumberZp32(s, ring), ring);
                break;
            case Ring.CZ:
                el = new Complex(new NumberZ(s), ring);
                break;
            case Ring.CZp:
                el = new Complex(new NumberZp(s, ring), ring);
                break;
            case Ring.CQ:
                el = new Complex(new Fraction(new NumberZ(s), NumberZ.ONE), ring);
                break;
            case Ring.C:
                el = new Complex(new NumberR(s), ring);
                break;
            case Ring.C128:
                el = new Complex(new NumberR128(new NumberR(s)), ring);
                break;
            case Ring.C64:
                el = new Complex(new NumberR64(s), ring);
                break;
            case Ring.ZMaxPlus:
                el = new NumberZMaxPlus(s, ring);
                break;
            case Ring.ZMinPlus:
                el = new NumberZMinPlus(s, ring);
                break;
            case Ring.ZMaxMin:
                el = new NumberZMaxMin(s, ring);
                break;
            case Ring.ZMinMax:
                el = new NumberZMinMax(s, ring);
                break;
            case Ring.ZMaxMult:
                el = new NumberZMaxMult(s, ring);
                break;
            case Ring.ZMinMult:
                el = new NumberZMinMult(s, ring);
                break;
            case Ring.RMaxPlus:
                el = new NumberRMaxPlus(s, ring);
                break;
            case Ring.RMinPlus:
                el = new NumberRMinPlus(s, ring);
                break;
            case Ring.RMaxMin:
                el = new NumberRMaxMin(s, ring);
                break;
            case Ring.RMinMax:
                el = new NumberRMinMax(s, ring);
                break;
            case Ring.RMaxMult:
                el = new NumberRMaxMult(s, ring);
                break;
            case Ring.RMinMult:
                el = new NumberRMinMult(s, ring);
                break;
            case Ring.R64MaxPlus:
                el = new NumberR64MaxPlus(s, ring);
                break;
            case Ring.R64MinPlus:
                el = new NumberR64MinPlus(s, ring);
                break;
            case Ring.R64MaxMin:
                el = new NumberR64MaxMin(s, ring);
                break;
            case Ring.R64MinMax:
                el = new NumberR64MinMax(s, ring);
                break;
            case Ring.R64MaxMult:
                el = new NumberR64MaxMult(s, ring);
                break;
            case Ring.R64MinMult:
                el = new NumberR64MinMult(s, ring);
                break;
            default:
                throw new IllegalArgumentException("Can't create number in given ring: " + ring);
        }

        if (el.isZero(ring)) {
            return Polynom.polynomZero;
        } else {
            return new Polynom(el);
        }
    }

    /**
     * Finds index of closing brace in tokens list. Supports '(', '{', and '['
     * characters.
     *
     * @param tokens       list with tokens
     * @param openBrace    opening brace
     * @param openBraceIdx index of opening brace
     * @return index of closing brace corresponding to opening brace, -1 if not
     * found
     */
    static int findClosingBrace(final List<Token> tokens,
                                final String openBrace, final int openBraceIdx) {
        int nestingLevel = 1;
        String closingBrace;
        Token currTok;
        if (openBrace.equals("(")) {
            closingBrace = ")";
        } else if (openBrace.equals("{")) {
            closingBrace = "}";
        } else if (openBrace.equals("[")) {
            closingBrace = "]";
        } else if (openBrace.equals("\\{")) {
            closingBrace = "\\}";
        } else {
            return -1;
        }
        for (int i = openBraceIdx + 1; i < tokens.size(); i++) {
            currTok = tokens.get(i);
            if (currTok.str().equals(closingBrace)) {
                nestingLevel--;
            } else if (currTok.str().equals(openBrace)) {
                nestingLevel++;
            }
            if (nestingLevel < 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Translates matrix operations LaTeX-style to fuction calls: 1) A^{T}
     * \transpose(A) 2) A^{\ast} \conjugate(A) (NOT ONLY matrices) 3) A^{\star}
     * \adjoint(A) 4) A^{\plus} \genInverse(A) 5) A^{-1} \inverse(A) 6)
     * A^{\times} \closure(A) (NOT ONLY matrices). Also applicable to matrix
     * literals like [[1, 2], [3, 4]].
     *
     * @param tokensRPN list with tokens in RPN form
     * @param ring      ring
     */
    static void prepareTokensRpnForPowerLikeOperations(
            final List<Token> tokensRPN, final Ring ring) {
        // Current and previous tokens
        Token ct, pt, toInsert;
        // String->Function
        Map<String, Integer> m = new HashMap<String, Integer>() {
            {
                put("T", F.TRANSPOSE);
                put("\\ast", F.CONJUGATE);
                put("\\star", F.ADJOINT);
                put("\\plus", F.GENINVERSE);
                put("\\times", F.CLOSURE);
            }

            private static final long serialVersionUID = 1L;
        };

        for (int i = 2; i < tokensRPN.size(); i++) {
            ct = tokensRPN.get(i);
            pt = tokensRPN.get(i - 1);
            if (ct.type() == TokenType.OP_RA && ct.str().equals("^")
                    && m.containsKey(pt.str())) {
                toInsert = new Token(TokenType.OP_FUNC,
                        "\\" + F.FUNC_NAMES[m.get(pt.str())], ring);
                toInsert.argsNum(F.getArgsNum(m.get(pt.str())));
                ArrayList<Integer> indNotNull = new ArrayList<Integer>(toInsert.argsNum());
                for (int j = 0; j < toInsert.argsNum(); j++) {
                    indNotNull.add(j);
                }
                toInsert.indNotNull(indNotNull);
                tokensRPN.set(i, toInsert);
                // Remove "power"
                tokensRPN.remove(i - 1);
            }
        }
    }

    /**
     * Transoformations of SYMBOLIC tokens: SYMBOLIC_{i, j} => SYMBOLIC{[i, j]}
     * SYMBOLIC_{i, j}^{k, l} => SYMBOLIC{[i, j], [k, l]}.
     * <p/>
     * Also concatenates tokens to enlarge SYMBOLIC: SYMBOLIC_POL, POL_SYMBOLIC,
     * POL_POL. Also concatenates (if possible) SYMBOLIC tokens with other to
     * allow names like: ax where x is variable from SPACE.
     *
     * @param tokens list with tokens
     * @param ring   ring
     */
    static void prepareTokensForFname(List<Token> tokens, Set<String> nonCommut, Ring ring) {
        // current, previous and next tokens
        Token currTok, prevTok, nextTok;

        // concatenating SYMBOLIC and POLYNOMIALS without spaces,
        // SYMBOLIC and numbers without spaces.
        for (int i = tokens.size() - 1; i > 0; i--) {
            currTok = tokens.get(i);
            prevTok = tokens.get(i - 1);
            boolean doConcat = false;
            boolean prevHasSpaceBefore = prevTok.hasSpaceBefore();
            if (currTok.hasSpaceBefore()) { continue;      }
            switch (prevTok.type()) {
                case POL_Z:
                case POL_R:
                    doConcat = (currTok.hasSymbolicType()
                            || currTok.str().matches(ParserStatic.RE_NUMBER))
                            && !prevTok.str().matches(ParserStatic.RE_NUMBER);
                    break;
                case SYMBOLIC:
                    switch (currTok.type()) {
                        case POL_Z:
                        case POL_R:
                        case SYMBOLIC:
                            doConcat = true;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

            if (doConcat) {
                tokens.remove(i);
                Token toInsert = new Token(TokenType.SYMBOLIC,
                        prevTok.str().concat(currTok.str()), ring);
                toInsert.hasSpaceBefore(prevHasSpaceBefore);
                tokens.set(i - 1, toInsert);
                i++;
            }
        }

        for (int i = 0; i < tokens.size() - 1; i++) {
            boolean isDependentOnVars = true;
            currTok = tokens.get(i);
            nextTok = tokens.get(i + 1);
            if (!currTok.hasSymbolicType() || !(nextTok.type() == TokenType.BR_OP
                    && nextTok.str().equals("("))) {
                continue;
            }
            int closingBraceIdx = findClosingBrace(tokens, "(", i + 1);
            // Check that inside braces there are ONLY variables
            // and argument separators (comma)
            for (int j = i + 2; j < closingBraceIdx; j++) {
                Token tokTmp = tokens.get(j);
                if (!(tokTmp.isPolynomialVar() || tokTmp.type() == TokenType.ARG_SEP)) {
                    isDependentOnVars = false;
                    break;
                }
            }
            if (!isDependentOnVars) {
                i = closingBraceIdx;
                continue;
            }
            // Replace variables with their indices in ring and {,, []} tokens
            for (int j = i + 2; j < closingBraceIdx; j++) {
                Token tokTmp = tokens.get(j);
                if (tokTmp.isPolynomialVar()) {
                    int varIdx = ((Polynom) tokTmp.value()).powers.length - 1;
                    tokens.set(j, new Token(TokenType.NUM, String.valueOf(varIdx), ring));
                }
            }
            tokens.set(i + 1, new Token(TokenType.VEC_BR_OP, "[", ring));
            tokens.set(closingBraceIdx, new Token(TokenType.VEC_BR_CL, "]", ring));
            tokens.add(closingBraceIdx + 1, new Token(TokenType.BR_CL, "}", ring));
            tokens.add(i + 1, new Token(TokenType.ARG_SEP, ",", ring));
            tokens.add(i + 1, new Token(TokenType.ARG_SEP, ",", ring));
            tokens.add(i + 1, new Token(TokenType.BR_OP, "{", ring));
            i = closingBraceIdx + 4;
        }

        // "_" characters. See cases in comments below.
        for (int i = 1; i < tokens.size() - 2; i++) {
            boolean doConcat = false;
            currTok = tokens.get(i);
            if (currTok.type() == TokenType.UNDERSCORE) {
                prevTok = tokens.get(i - 1);
                nextTok = tokens.get(i + 1);
                switch (prevTok.type()) {
                    case SYMBOLIC:
                        switch (nextTok.type()) {
                            case BR_OP:
                                if (nextTok.str().equals("{")) {
                                    processSymbolicWithIndices(tokens, i, nonCommut, prevTok, nextTok, ring);
                                }
                                break;
                            case SYMBOLIC:
                            case POL_R:
                            case POL_Z:
                                doConcat = true;
                                break;
                            default:
                                break;
                        }
                        break;
                    case POL_R:
                    case POL_Z:
                        switch (nextTok.type()) {
                            case SYMBOLIC:
                            case POL_R:
                            case POL_Z:
                                doConcat = true;
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }

                // SYMBOLIC_POL
                // POL_SYMBOLIC
                // POL_POL -- concatenate.
                if (doConcat) {
                    StringBuilder s = new StringBuilder(
                            prevTok.str().length() + currTok.str().length() + nextTok.str().length());
                    s.append(prevTok.str()).append(currTok.str()).append(nextTok.str());
                    tokens.set(i - 1, new Token(TokenType.SYMBOLIC, s.toString(), ring));
                    tokens.remove(i);
                    tokens.remove(i);
                    i -= 1;
                }
            }
        }
    }

    /**
     * Translates logarithms tokens from TeX-like form to standard functions.
     * \log_{a}(x) => \log(a, x)
     */
    static void prepareTokensForLog(List<Token> tokens, Ring ring) {
        Token ct, nt, tmptok;
        int upLow[];
        int logInd; // index of logarithm token itself
        int argInd; // start index of arguments
        int opBrInd, clBrInd; // indexes of inserted function parentheses.

        for (int i = 0; i < tokens.size() - 1; i++) {
            ct = tokens.get(i);
            nt = tokens.get(i + 1);
            if (ct.type() == TokenType.OP_FUNC
                    && getNameFromStr(ct.str()) == F.LOG
                    && nt.type() == TokenType.UNDERSCORE) {
                logInd = i;
                opBrInd = i + 1;
                // find limits of base of logarithm
                upLow = findLowerUpper(tokens, logInd + 1, ring);
                if (upLow.length == 0) {
                    argInd = logInd + 1;
                } else {
                    argInd = upLow[3] + 2;
                }
                tmptok = tokens.get(argInd);
                // если после основания идет открывающая круглая скобка,
                // то конец аргумента -- индекс соответсвующей закрывающей скобки
                if (tmptok.type() == TokenType.BR_OP && tmptok.str().equals("(")) {
                    clBrInd = Utils.findClosingBrace(
                            tokens, tmptok.str(), argInd) + 1;
                } else {
                    // если после основания скобки нет, то аргументом будет
                    // только следующий токен. Индекс увеличивается на 2,
                    // потому что будет еще вставлена открывающая скобка
                    // сразу после знака логарифма
                    clBrInd = argInd + 2;
                }
                // окружаем все круглыми скобками
                tokens.add(opBrInd,
                        new Token(TokenType.BR_OP, "(", ring));
                tokens.add(clBrInd,
                        new Token(TokenType.BR_CL, ")", ring));
            }
        }
    }

    /**
     * Call with default separator "=".
     *
     * @param tokens list of tokens
     * @param start  beginnig index
     * @param ring   ring
     * @return
     */
    static int[] findLowerUpper(List<Token> tokens, int start, Ring ring) {
        return findLowerUpper(tokens, start, "=", ring);
    }

    /**
     * Finds bound of upper and lower limits (LaTeX style): "_" is lower and "^"
     * is upper. Return -1 if bound was not found.
     *
     * @param tokens         list of tokens
     * @param start          beginning index
     * @param lowerSeparator string with symbol treating as a separator in lower
     *                       limit
     * @param ring           ring
     * @return array of indexes:<br /> 0 -- beginning of lower limit<br /> 1 --
     * end of lower limit<br /> 2 -- beginning of upper limit<br /> 3 -- end of
     * upper limit<br /> 4 -- index of separator inside lower limit
     */
    static int[] findLowerUpper(List<Token> tokens, int start,
                                String lowerSeparator, Ring ring) {
        Token tmptok;
        tmptok = tokens.get(start);
        int lowerBeg,
                lowerEnd,
                higherBeg = -1,
                higherEnd,
                sepIdx = -1;

        // первый токен -- "_"
        if (tmptok.type() == TokenType.UNDERSCORE) {
            // удаляем "_"
            tokens.remove(start);
            // начальный индекс верхнего предела
            lowerBeg = start;
            // получаем конечный индекс верхнего предела
            lowerEnd = Utils.findClosingBrace(tokens, "{", lowerBeg);
            // ищем разделитель в нижнем пределе ("=")
            int nesting = 1;
            for (int i = lowerBeg + 1; i < lowerEnd; i++) {
                tmptok = tokens.get(i);
                if (tmptok.type() == TokenType.BR_OP) {
                    nesting++;
                } else if (tmptok.type() == TokenType.BR_CL) {
                    nesting--;
                } else if (nesting == 1 && tmptok.str().equals(lowerSeparator)) {
                    sepIdx = i;
                    tokens.set(i, new Token(TokenType.ARG_SEP, ",", ring));
                    tokens.remove(lowerBeg);
                    lowerEnd -= 1;
                    sepIdx -= 1;
                    tokens.remove(lowerEnd);
                    lowerEnd -= 1;
                    break;
                }
            }

            tmptok = tokens.get(lowerEnd + 1);
            // если присутствует верхний индекс
            if (tmptok.str().equals("^")) {
                // заменяем "^" на ","
                tmptok.str(",");
                tmptok.type(TokenType.ARG_SEP);
                tmptok.value(null);
                // начальный индекс верхнего предела
                higherBeg = lowerEnd + 2;
                // получаем конечный индекс верхнего предела
                higherEnd = Utils.findClosingBrace(tokens, "{", higherBeg);
            } else {
                // ! Если верхнего индекса нет, индекс конца верхнего предела
                //   такой же, как верхнего
                higherEnd = lowerEnd;
            }

            // заменяем "*" (появляется после insertMultiply()) на ","
            tmptok = tokens.get(higherEnd + 1);
            if (tmptok.str().equals("*")) {
                tmptok.str(",");
                tmptok.type(TokenType.ARG_SEP);
                tmptok.value(null);
            } else {
                tokens.add(higherEnd + 1, new Token(TokenType.ARG_SEP, ",", ring));
            }
            return new int[]{lowerBeg, lowerEnd, higherBeg, higherEnd, sepIdx};
        }
        return new int[0];
    }

    /**
     * Inserts explicit multiply operators.
     */
    static void insertMultiply(List<Token> tokens, Ring ring) {
        // Current pair of tokens.
        // Will try to insert multiply operator between them.
        Token tok1, tok2;
        boolean needToInsertMultiply;
        for (int i = 1; i < tokens.size(); i++) {
            tok1 = tokens.get(i - 1);
            tok2 = tokens.get(i);
            needToInsertMultiply = false;
            switch (tok1.type()) {
                case POL_Z:
                case POL_R:
                case SYMBOLIC:
                case OP_POSTFIX:
                    switch (tok2.type()) {
                        case POL_Z:
                        case POL_R:
                        case SYMBOLIC:
                        case BR_OP:
                        case OP_FUNC:
                            needToInsertMultiply = true;
                            break;
                        default:
                            break;
                    }
                    break;
                case BR_CL:
                    switch (tok2.type()) {
                        case POL_Z:
                        case POL_R:
                        case SYMBOLIC:
                        case BR_OP:
                        case OP_FUNC:
                            needToInsertMultiply = true;
                            break;
                        default:
                            break;
                    }
                    break;
            }

            if (needToInsertMultiply) {
                tokens.add(i, new Token(TokenType.OP_LA, "*", ring));
            }
        }
    }

    /**
     * Unary minus processing: multiply by -1 or negate number.
     *
     * @param tokens list with tokens
     * @param ring   ring
     */
    static void unaryMinus(final List<Token> tokens, final Ring ring) {
        // current, previous and next tokens
        Token tc, tp, tn;
        TokenType tpType;
        String tcStr;

        if (tokens.size() < 3) { // 3 because of EOL
            return;
        }
        // Check the first token.
        if (tokens.get(0).str().equals("-")) {
            tn = tokens.get(1);
            negateOrMultiplyOnMinusOne(ring, tokens, tn, 0);
        }
        // Find combinations like "(-", "--" and ",-"
        for (int i = 1; i < tokens.size() - 1; i++) {
            tp = tokens.get(i - 1);
            tc = tokens.get(i);
            tn = tokens.get(i + 1);
            tpType = tp.type();
            tcStr = tc.str();

            if (tcStr.equals("-")
                    && (tpType == TokenType.BR_OP
                    || tpType == TokenType.VEC_BR_OP
                    || tpType == TokenType.ARG_SEP
                    || tp.str().equals("-"))) {
                negateOrMultiplyOnMinusOne(ring, tokens, tn, i);
            }
        }
    }

    /**
     * Negates given token if it's a number or multiplies it on minus one.
     *
     * @param ring            ring
     * @param tokens          list with tokens
     * @param tokenAfterMinus token after minus sign token
     * @param minusIdx        index of minus sign token
     */
    private static void negateOrMultiplyOnMinusOne(
            final Ring ring, final List<Token> tokens,
            final Token tokenAfterMinus, final int minusIdx) {
        if (tokenAfterMinus.isPolynomialNumber()) {
            Element val = tokenAfterMinus.value().negate(ring);
            tokenAfterMinus.value(val);
            tokenAfterMinus.str("-" + tokenAfterMinus.str());
            tokens.remove(minusIdx);
        } else {
            tokens.remove(minusIdx);
            tokens.add(minusIdx, new Token(TokenType.POL_Z, "-1", ring));
            tokens.get(minusIdx).value(getPolynomFromNumber(ring, ring.algebra[0], "-1"));
            tokens.add(minusIdx + 1, new Token(TokenType.OP_LA, "*", ring));
        }
    }

    /**
     * Process limit in LaTeX style: \lim_{x \\to 0} \sin(x) becomes
     * \lim(\sin(x), x, 0). Wrap argument with parentheses if it isn't a single
     * function: \lim_{x \\to 0} (\sin(x) + \cos(x))
     *
     * @param tokens list with tokens
     * @param ring   ring
     */
    static void prepareTokensForLim(final List<Token> tokens, final Ring ring) {
        Token ct, tmpTok;
        int upLow[];
        int argInd;
        int limInd;
        int opBrInd, clBrInd;

        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            if (ct.type() == TokenType.OP_FUNC
                    && getNameFromStr(ct.str()) == F.LIM) {
                limInd = i;
                opBrInd = limInd + 1;

                upLow = findLowerUpper(tokens, limInd + 1, "\\to", ring);
                if (upLow.length == 0) {
                    return;
                } else {
                    argInd = upLow[1] + 2;
                }

                tmpTok = tokens.get(argInd);
                // Analyze argument
                if (tmpTok.type() == TokenType.BR_OP) {
                    // Expression with (...)
                    clBrInd = findClosingBrace(tokens, "(", argInd) + 1;
                } else if (tmpTok.type() == TokenType.OP_FUNC) {
                    // Just a function f(...)
                    clBrInd = findClosingBrace(tokens, "(", argInd + 1) + 1;
                } else {
                    // Some single token
                    clBrInd = argInd + 1;
                }

                // rearrange tokens to match arguments order
                List<Token> arr1 = new ArrayList<>(tokens.subList(limInd + 1, argInd - 1));
                List<Token> arr2 = new ArrayList<>(tokens.subList(argInd, clBrInd));
                tokens.removeAll(arr1);
                tokens.removeAll(arr2);
                tokens.addAll(limInd + 1, arr2);
                tokens.addAll(limInd + clBrInd - argInd + 2, arr1);

                // Surround resulting tokens with brackets
                tokens.add(opBrInd, new Token(TokenType.BR_OP, "(", ring));
                tokens.add(clBrInd + 1, new Token(TokenType.BR_CL, ")", ring));
            }
        }
    }

    /**
     * Process integrals in LaTeX style. Indefinite or definite: \int sin(x) d x
     * becomes \int(sin(x), x) and \int_{0}^{1} sin(x) d x becomes \int(sin(x),
     * x, 0, 1). If argument isn't wrapped with parentheses, looks for the first
     * "d" symbol. If after "d" there is some expression, like "dx", it assumes
     * that "x" is a variable.
     * <p/>
     * So it's produces a mess when: 1) there is a "dVAR" expression inside
     * argument, and argument isn't wrapped with parentheses. 2) there is a
     * "dSOMETHING", where SOMETHING isn't a var name.
     *
     * @param tokens list with tokens
     * @param ring   ring
     */
    static void prepareTokensForIntegral(final List<Token> tokens, final Ring ring) {
        boolean withLimits;
        Token ct, tmptok; // Current and temporary tokens
        int upLow[];
        int argInd;
        int intInd, dInd;
        int clBrInd, opBrInd;
        int grBeg, grEnd;

        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            if (ct.type() == TokenType.OP_FUNC && getNameFromStr(ct.str()) == F.INT) {
                withLimits = false;
                dInd = -1;
                intInd = i;
                opBrInd = intInd + 1;

                // Try to find indexes of all this _{...}^{...} parts.
                upLow = findLowerUpper(tokens, intInd + 1, ring);
                // In indexes wasn't found, argument goes right after integral.
                if (upLow.length == 0) {
                    argInd = intInd + 1;
                } else {
                    argInd = upLow[3] + 2;
                    withLimits = true;
                }

                // If next token is opening paren.
                tmptok = tokens.get(argInd);
                if (tmptok.type() == TokenType.BR_OP && tmptok.str().equals("(")) {
                    // Find index of "d" -- it should go right after closing brace.
                    dInd = Utils.findClosingBrace(tokens, tmptok.str(), argInd) + 1;
                    Token probablyD = tokens.get(dInd);
                    if (probablyD.type() == TokenType.SYMBOLIC
                            && probablyD.str().startsWith("d")) {
                        // If there is no space between "d" and variable,
                        // split "d<var>" to 2 tokens: "d" and "<var>".
                        if (probablyD.str().length() > 1) {
                            Token arg = new Token(TokenType.VAR, probablyD.str().substring(1), ring);
                            tokens.add(dInd + 1, arg);
                            probablyD.str("d");
                        }
                    }
                    tmptok = tokens.get(dInd + 1);
                    // If there is an opening brace.
                    if (tmptok.type() == TokenType.BR_OP && tmptok.str().equals("{")) {
                        grBeg = dInd + 1; // Start of group in braces {...}.
                        // Find end of group in braces {...}.
                        grEnd = Utils.findClosingBrace(tokens, tmptok.str(), grBeg);
                        clBrInd = grEnd + 2;
                    } else {
                        // Index of closing parenthesis keeping in mind future
                        // closing parenthesis and variable token after "d".
                        clBrInd = dInd + 3;
                    }
                    // There was no closing brace.
                } else {
                    // Find index of "d" starting from argument's index.
                    for (int ii = argInd; ii < tokens.size(); ii++) {
                        Token probablyD = tokens.get(ii);
                        if (probablyD.type() == TokenType.SYMBOLIC
                                && probablyD.str().startsWith("d")) {
                            // If there is no space between "d" and variable,
                            // split "d<var>" to 2 tokens: "d" and "<var>".
                            if (probablyD.str().length() > 1) {
                                Token arg = new Token(
                                        TokenType.VAR, probablyD.str().substring(1), ring);
                                tokens.add(ii + 1, arg);
                                probablyD.str("d");
                            }
                            dInd = ii;
                            break;
                        }
                    }

                    tmptok = tokens.get(dInd + 1);
                    if (tmptok.type() == TokenType.BR_OP && tmptok.str().equals("{")) {
                        // начало группы в фигурных скобках
                        grBeg = dInd + 1;
                        // ищем конец группы в фигурных скобках
                        grEnd = Utils.findClosingBrace(tokens, tmptok.str(), grBeg);
                        clBrInd = grEnd + 2;
                    } else {
                        // индекс закрывающей скобки, учитывая будущую открывающую
                        // и токен после "d"
                        clBrInd = dInd + 3;
                    }
                }

                // Replace "d" with function's arguments separator ","
                tmptok = tokens.get(dInd);
                if (tmptok.type() == TokenType.SYMBOLIC && tmptok.str().equals("d")) {
                    tmptok.type(TokenType.ARG_SEP);
                    tmptok.str(",");
                    tmptok.value(null); // Remove Fname value of "d".
                }

                if (withLimits) {
                    // Rearrange tokens to get proper arguments order.
                    List<Token> arr1 = new ArrayList<>(
                            tokens.subList(intInd + 1, argInd - 1));
                    List<Token> arr2 = new ArrayList<>(
                            tokens.subList(argInd, clBrInd - 1));
                    tokens.removeAll(arr1);
                    tokens.removeAll(arr2);
                    tokens.addAll(intInd + 1, arr2);
                    tokens.addAll(intInd + clBrInd - argInd + 1, arr1);
                }
                // Insert new parentheses around arguments list.
                tokens.add(opBrInd, new Token(TokenType.BR_OP, "(", ring));
                tokens.add(clBrInd, new Token(TokenType.BR_CL, ")", ring));
            }
        }
    }

    /**
     * Приводит суммы из ТеХ-подобной нотации к виду функций
     */
    static void prepareTokensForSum(List<Token> tokens, Ring ring) {
        // текущий и временный токены
        Token ct, tmptok;
        // массив для результатов обработки верхнего и нижнего пределов
        int upLow[];
        int argInd;  // индекс начала аргумента
        int sumInd;  // индекс суммы
        int higherBeg, higherEnd; // верхний предел (начало и конец)
        int clBrInd; // индекс будущей закрывающей скобки
        int opBrInd;  // индекс будущей открывающей скобки

        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);

            if (ct.type() == TokenType.OP_FUNC
                    && getNameFromStr(ct.str()) == F.SUM) {
                // встретился токен суммы
                sumInd = i;
                opBrInd = sumInd + 1;
                // определяем индексы в верхнем и нижнем пределах
                upLow = findLowerUpper(tokens, sumInd + 1, ring);
                argInd = upLow[3] + 2;
                higherBeg = upLow[2];
                higherEnd = upLow[3];
                // проверяем, можно ли заменить sum на series
                if (tokens.get(higherBeg + 1).str().equals("\\infty")) {
                    // захватываем еще следующую запятую
                    ArrayList<Token> arrToRemove = new ArrayList<>(
                            tokens.subList(higherBeg, higherEnd + 2));
                    tokens.removeAll(arrToRemove);
                    argInd -= higherEnd + 2 - higherBeg;
                }

                // если аргумент начинается с открывающей скобки
                tmptok = tokens.get(argInd);
                if (tmptok.type() == TokenType.BR_OP) {
                    clBrInd = Utils.findClosingBrace(tokens, tmptok.str(), argInd) + 2;
                } else {
                    // открывающей скобки не было (только 1 токен)
                    clBrInd = argInd + 3;
                }

                ArrayList<Token> arr1, arr2;
                // переставляем элементы, чтобы получилась требуемая форма ф-ции
                arr1 = new ArrayList<>(tokens.subList(sumInd + 1, argInd - 1));
                arr2 = new ArrayList<>(tokens.subList(argInd, clBrInd - 1));
                tokens.removeAll(arr1);
                tokens.removeAll(arr2);
                tokens.addAll(sumInd + 1, arr2);
                tokens.addAll(sumInd + clBrInd - argInd + 1, arr1);

                tokens.add(opBrInd,
                        new Token(TokenType.BR_OP, "(", ring));
                tokens.add(clBrInd,
                        new Token(TokenType.BR_CL, ")", ring));
            }
        }
    }

    /**
     * Приводит ряды из ТеХ-подобной нотации к виду функций
     */
    static void prepareTokensForSequence(List<Token> tokens, Ring ring) {
        // текущий и временный токены
        Token ct, tmptok;
        // массив для результатов обработки верхнего и нижнего пределов
        int upLow[];
        int sepInd;  // индекс разделителя
        int argInd;  // индекс начала аргумента
        int seqInd;  // индекс суммы
        int lowerEnd;  // конечный индекс нижнего предела
        int clBrInd; // индекс будущей закрывающей скобки
        int opBrInd;  // индекс будущей закрывающей скобки

        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            if (ct.type() == TokenType.OP_FUNC
                    && getNameFromStr(ct.str()) == F.SEQUENCE) {
                // встретился токен ряда
                seqInd = i;
                opBrInd = seqInd + 1;
                // определяем индексы в верхем и нижнем пределах
                upLow = findLowerUpper(tokens, seqInd + 1, ring);
                argInd = upLow[3] + 2;
                sepInd = upLow[4];
                lowerEnd = upLow[1];

                if (tokens.get(lowerEnd - 1).str().equals("..")
                        && tokens.get(lowerEnd).str().equals("\\infty")) {
                    ArrayList<Token> arrToRemove = new ArrayList<>(
                            tokens.subList(sepInd + 1, lowerEnd + 2));
                    tokens.removeAll(arrToRemove);
                    argInd -= lowerEnd + 2 - sepInd - 1;
                }

                // если аргумент начинается с открывающей скобки
                tmptok = tokens.get(argInd);
                if (tmptok.type() == TokenType.BR_OP) {
                    clBrInd = Utils.findClosingBrace(tokens, tmptok.str(),
                            argInd) + 2;
                } else {
                    // открывающей скобки не было (только 1 токен)
                    clBrInd = argInd + 3;
                }

                ArrayList<Token> arr1, arr2;
                // переставляем элементы, чтобы получилась требуемая форма ф-ции
                arr1 = new ArrayList<Token>(tokens.subList(seqInd + 1,
                        argInd - 1));
                arr2 = new ArrayList<Token>(tokens.subList(argInd, clBrInd - 1));
                tokens.removeAll(arr1);
                tokens.removeAll(arr2);
                tokens.addAll(seqInd + 1, arr2);
                // индекс, по которому надо вставлять:
                // (sumInd + 1 + clBrInd - 1 - argInd + 1)
                tokens.addAll(seqInd + clBrInd - argInd + 1, arr1);
                // Surround with braces.
                tokens.add(opBrInd,
                        new Token(TokenType.BR_OP, "(", ring));
                tokens.add(clBrInd,
                        new Token(TokenType.BR_CL, ")", ring));
            }
        }
    }

    /**   For  D and  d::
     * Converts \D_x(F) to \D(F, x); \D_{x^2}(F) to \D(F, x^2); \D_x to \D(, x);
     * \D_{x^2} to \D(, x^2).
     *
     * @param tokens tokens list
     * @param ring   ring
     */
    static void prepareTokensForD(List<Token> tokens, Ring ring) {
        Token ct, nt, nnt;
        for (int i = 0; i < tokens.size() - 2; i++) {
            ct = tokens.get(i); // D
            nt = tokens.get(i + 1); // _
            nnt = tokens.get(i + 2); // var or {
            if (ct.type() == TokenType.OP_FUNC
    //                && getNameFromStr(ct.str()) == F.D
                    && ((getNameFromStr(ct.str()) == F.D)||( getNameFromStr(ct.str()) == F.d))
                    && nt.type() == TokenType.UNDERSCORE) {
                if (nnt.isPolynomialVar()) {
                    // Single var after _
                    if (i + 3 < tokens.size() && tokens.get(i + 3).str().equals("(")) {
                        // With function: D_x (...)
                        int opBrace = i + 3;
                        int clBrace = findClosingBrace(tokens, "(", opBrace);
                        List<Token> varPart = new ArrayList<>(tokens.subList(i + 2, opBrace));
                        tokens.add(clBrace, new Token(TokenType.ARG_SEP, ",", ring));
                        tokens.remove(i + 1);
                        tokens.removeAll(varPart);
                        tokens.addAll(clBrace - varPart.size(), varPart);
                    } else {
                        // Only D_x
                        tokens.set(i + 1, new Token(TokenType.ARG_SEP, ",", ring));
                        tokens.add(i + 1, new Token(TokenType.BR_OP, "(", ring));
                        tokens.add(i + 3 + 1, new Token(TokenType.BR_CL, ")", ring));
                    }
                } else if (nnt.type() == TokenType.BR_OP && nnt.str().equals("{")) {
                    // {...} after _
                    int clBraceVar = findClosingBrace(tokens, "{", i + 2);
                    if (clBraceVar + 1 < tokens.size() && tokens.get(clBraceVar + 1).str().equals("(")) {
                        // With function: D_{...} (...)
                        int opBrace = clBraceVar + 1;
                        int clBrace = findClosingBrace(tokens, "(", opBrace);
                        List<Token> varPart = new ArrayList<Token>(tokens.subList(i + 2, opBrace));
                        tokens.add(clBrace, new Token(TokenType.ARG_SEP, ",", ring));
                        tokens.remove(i + 1);
                        tokens.removeAll(varPart);
                        tokens.addAll(clBrace - varPart.size(), varPart);
                    } else if (i + 3 < tokens.size()) {
                        // Only D_{...}
                        tokens.add(i + 3, new Token(TokenType.ARG_SEP, ",", ring));
                        tokens.remove(i + 1);
                    }
                } else {
                    throw new IllegalArgumentException("incorrect \\D format");
                }
            }
        }
    }

    /**
     * Converts vectors from [a, b, c] notation to \\Vector(a, b, c).
     */
    static void prepareTokensForVector(List<Token> tokens, Ring ring) {
        Token ct; // Current token.
        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            if (ct.type() == TokenType.VEC_BR_OP) {
                tokens.add(i, new Token(TokenType.OP_FUNC,
                        "\\" + F.FUNC_NAMES[F.VECTORS], ring));
                ct.type(TokenType.BR_OP);
                ct.str("(");
            }
            if (ct.type() == TokenType.VEC_BR_CL) {
                ct.type(TokenType.BR_CL);
                ct.str(")");
            }
        }
    }

    private static void putVectorSetArgumentsToVectors(
            List<Token> tokens, int opBraceIdx, int clBraceIdx, Ring ring) {
        int i = opBraceIdx + 1;
        Token ct;
        tokens.add(i, new Token(TokenType.BR_OP, "(", ring));
        tokens.add(i, new Token(TokenType.OP_FUNC, "\\" + F.FUNC_NAMES[VECTORS], ring));
        int currCloseBraceIdx = clBraceIdx + 2; // т.к. добавили 2 новых токена
        while (i < currCloseBraceIdx) {
            ct = tokens.get(i);
            if (ct.type() == TokenType.ARG_SEP) {
                tokens.add(i, new Token(TokenType.BR_CL, ")", ring));
                tokens.add(i + 2, new Token(TokenType.BR_OP, "(", ring));
                tokens.add(i + 2, new Token(TokenType.OP_FUNC, "\\" + F.FUNC_NAMES[VECTORS], ring));
                currCloseBraceIdx += 3; // т.к. добавили 3 новых токена: ) \Vector (
                i += 3; // чтобы перескочить запятую
            }
            i++;
        }
        tokens.add(currCloseBraceIdx, new Token(TokenType.BR_CL, ")", ring));
    }

    /**
     * Converts vectors from \{a < b, c < d < f \} notation to
     * \VectorSet(\Vector(a, Element.LESS, b), \Vector(c, Element.LESS, d, Element.LESS, f));
     */
    static void prepareTokensForVectorSet(List<Token> tokens, Ring ring) {
        Map<String, Integer> boolConst = new HashMap<>();
        boolConst.put("==", Element.EQUAL);
        boolConst.put(">", Element.GREATER);
        boolConst.put("\\ge", Element.GREATER_OR_EQUAL);
        boolConst.put("<", Element.LESS);
        boolConst.put("\\le", Element.LESS_OR_EQUAL);
        boolConst.put("\\ne", Element.NOT_EQUAL);
        Token ct; // Current token.
        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            if (ct.type() == TokenType.VECTOR_SET_BR_OP) {
                int closingIdx = Utils.findClosingBrace(tokens, "\\{", i);

                putVectorSetArgumentsToVectors(tokens, i, closingIdx, ring);
                // Ищем еще на случай, если после putVectorSetArgumentsToVectors добавились новые токены.
                closingIdx = Utils.findClosingBrace(tokens, "\\{", i);
                tokens.add(i, new Token(TokenType.OP_FUNC,
                        "\\" + F.FUNC_NAMES[F.VECTOR_SET], ring));
                ct.type(TokenType.BR_OP);
                ct.str("(");
                closingIdx += 1;
                for (int j = i; j < closingIdx; j++) {
                    if (tokens.get(j).type() == TokenType.OP_BOOL) {
                        tokens.add(j, new Token(TokenType.ARG_SEP, ",", ring));
                        tokens.set(j + 1, new Token(TokenType.NUM,
                                String.valueOf(boolConst.get(tokens.get(j + 1).str())), ring));
                        tokens.add(j + 2, new Token(TokenType.ARG_SEP, ",", ring));
                        closingIdx += 2;
                    }
                }
                tokens.get(closingIdx).type(TokenType.BR_CL);
                tokens.get(closingIdx).str(")");
            }
        }
    }

    static void prepareTokensForSolveTrig(List<Token> tokens, Ring ring) {
        Token ct;
        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            if (ct.type() == TokenType.OP_FUNC
                    && getNameFromStr(ct.str()) == F.SOLVETRIG) {
                // equal sign.
                for (int j = i + 1; j < tokens.size(); j++) {
                    if (tokens.get(j).type() == TokenType.ARG_SEP
                            && tokens.get(j).str().equals("=")) {
                        tokens.set(j, new Token(TokenType.OP_LA, "-", ring));
                    }
                }
            }
        }
    }

    /**
     * Converts ^{+} to ^{\plus}.
     *
     * @param tokens list of tokens.
     * @param ring   ring.
     */
    static void prepareTokensForGenInverse(List<Token> tokens, Ring ring) {
        for (int i = 0; i <= tokens.size() - 4; i++) {
            if (tokens.get(i).type() == TokenType.OP_RA
                    && tokens.get(i).str().equals("^")
                    && tokens.get(i + 1).type() == TokenType.BR_OP
                    && tokens.get(i + 1).str().equals("{")
                    && tokens.get(i + 2).type() == TokenType.OP_LA
                    && tokens.get(i + 2).str().equals("+")
                    && tokens.get(i + 3).type() == TokenType.BR_CL
                    && tokens.get(i + 3).str().equals("}")) {
                tokens.set(i + 2, new Token(TokenType.SYMBOLIC, "\\plus", ring));
            }
        }
    }

    static int gotoClosingSetBrace(String str, int startIdx, String... closingBraces) {
        int res = -1;
        int closingBraceLength = 0;
        for (String closingBrace : closingBraces) {
            int tmp = str.indexOf(closingBrace, startIdx);
            if (res >= 0 && tmp >= 0) {
                res = Math.min(res, tmp);
                closingBraceLength = closingBrace.length();
            } else if (tmp >= 0) {
                res = tmp;
                closingBraceLength = closingBrace.length();
            }
        }
        return res + closingBraceLength;
    }

    static int gotoPairChar(String string, char[] openChars, char[] closingChars,
                            int startIdx) {
        int nestingLevel = 1;
        int i;
        char currChar;

        if (openChars.length != closingChars.length) {
            throw new IllegalArgumentException(
                    "Opening and closing characters arrays must have the same length");
        }

        for (i = startIdx + 1; i < string.length(); i++) {
            if (nestingLevel < 1) {
                return i - 1;
            }
            currChar = string.charAt(i);
            for (int j = 0; j < openChars.length; j++) {
                if (currChar == openChars[j]) {
                    nestingLevel++;
                    break;
                } else if (currChar == closingChars[j]) {
                    nestingLevel--;
                    break;
                }
            }
        }

        return nestingLevel < 1 ? i - 1 : -1;
    }

    /**
     * Ищет индекс закрывающего символа в строке (закрывающие скобки, кавычки) с
     * учетом вложенности
     *
     * @param str       строка, в которой ведется поиск
     * @param openChar  первый символ в паре (открывающая скобка, кавычка)
     * @param openIndex индекс первого из пары символов
     * @return индекс второго из пары символов; -1, если закрывающий символ не
     * найден
     */
    static int gotoPairChar(String str, char openChar, char closingChar,
                            int openIndex) {
        return gotoPairChar(str, new char[]{openChar}, new char[]{closingChar},
                openIndex);
    }

    /**
     * Counts number of arguments for function with ambiguous arguments.
     *
     * @param tokens    list of tokens.
     * @param stInd     index of function token in tokens list.
     * @param funcToken function token to analyze.
     */
    static void countAndSetArgsNumber(List<Token> tokens, int stInd, Token funcToken) {
        int cntArgs = 1; // Number of arguments.
        int nesting = 0; // Nesting counter.
        Token ct; // Current token.
        TokenType tt; // Type of current token.
        ArrayList<Integer> indNotNull = new ArrayList<Integer>(5); // Indexes of non-null arguments.

        for (int i = stInd + 1; i < tokens.size() - 1; i++) {
            ct = tokens.get(i);
            tt = ct.type();
            if (tt == TokenType.BR_CL) {
                nesting--;
            } else if (tt == TokenType.BR_OP) {
                nesting++;
            } else if (tt == TokenType.ARG_SEP && nesting == 1) {
                boolean openingBraceAndQuestion = // Case (?,
                        i == stInd + 3 && tokens.get(i - 1).str().equals("?");
                boolean argsSeparatorAndQuestion = // Case ,?,
                        tokens.get(i - 2).type() == TokenType.ARG_SEP
                                && tokens.get(i - 1).str().equals("?");
                if (openingBraceAndQuestion || argsSeparatorAndQuestion) {
                    // Turn this cases to (, or ,,
                    tokens.remove(i - 1);
                    i--;
                }
                boolean rightAfterOpeningBrace = i == stInd + 2; // Case (,
                boolean doubleArgsSeparator = tokens.get(i - 1).type() == TokenType.ARG_SEP; // Case ,,
                if (!(rightAfterOpeningBrace || doubleArgsSeparator)) {
                    indNotNull.add(cntArgs - 1);
                }
                cntArgs++;
            }

            // Now we are right after the final closing brace.
            if (nesting == 0) {
                if (tokens.get(i - 1).type() == TokenType.BR_OP) {
                    // Case ()
                    indNotNull = null;
                    cntArgs = 0;
                } else if (tokens.get(i - 1).str().equals("?")
                        && tokens.get(i - 2).type() == TokenType.ARG_SEP) {
                    // Case ,?)
                    tokens.remove(i - 1);
                    i--;
                } else if (!(tokens.get(i - 1).type() == TokenType.ARG_SEP)) {
                    // Case ,)
                    indNotNull.add(cntArgs - 1);
                }
                break;
            }
        }

        // Set calculated values for current function token.
        funcToken.indNotNull(indNotNull);
        funcToken.argsNum(cntArgs);
    }

    /**
     * Определяет количество аргументов у всех функций и операций
     */
    static void getAllArgNumber(List<Token> tokens) {
        // Current token.
        Token ct;
        // Array of infix, prefix and postfix unary and binary operations. We assume that these operation
        // always have 2 non-null arguments. Note: ADD and MULTIPLY could have more than 2 args.
        Integer[] infixPostfixOperations = {F.ADD, F.SUBTRACT, F.DIVIDE, F.MULTIPLY,
                F.POW, F.intPOW, F.B_AND, F.B_EQ, F.B_GE,
                F.B_GT, F.B_LE, F.B_LESS,
                F.B_NE, F.B_NOT, F.B_OR,
            F.CUP, F.CAP, F.SYMMETRIC_DIFFERENCE, F.SET_MINUS,
            F.FACTORIAL, F.COMPLEMENT};
        Set<Integer> preInPostFix = new HashSet<>(Arrays.asList(infixPostfixOperations));

        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            // If current token is function or operation.
            if (ct.type() == TokenType.OP_FUNC
                    || ct.type() == TokenType.OP_LA
                    || ct.type() == TokenType.OP_RA
                    || ct.type() == TokenType.OP_POSTFIX
                    || ct.type() == TokenType.OP_BOOL) {
                int func_name = getNameFromStr(ct.str());
                // If function is not infix, prefix or postfix operation, count argument number.
                if (!preInPostFix.contains(func_name)) {
                    countAndSetArgsNumber(tokens, i, ct);
                } else {
                    // Get predefined number of arguments.
                    // иначе берем заданное количесвто аргументов
                    int argsNum = F.getArgsNum(func_name);
                    // Assume that infix, prefix and postfix operations can't have null args.
                    // So fill it with 0, 1, 2, 3, ...
                    List<Integer> indNotNull = new ArrayList<Integer>(argsNum);
                    for (int j = 0; j < argsNum; j++) {
                        indNotNull.add(j);
                    }
                    ct.indNotNull(indNotNull);
                    ct.argsNum(argsNum);
                }
            }
            // SYMBOLIC_{}
            if (i + 2 < tokens.size() && ct.hasSymbolicType()
                    && tokens.get(i + 1).str().equals("*")
                    && tokens.get(i + 2).str().equals("{")) {
                countAndSetArgsNumber(tokens, i + 1, ct);
                if (ct.argsNum() >= 0) {
                    tokens.remove(i + 1);
                }
            }
        }
    }

    /**
     * TODO: complete this javadoc.
     *
     * @param tokensRpn
     * @param ring
     */
    static void makeMonomialsRpn(List<Token> tokensRpn, Ring ring) {
        Token tc, tp, tpp; // current, previous and preprevious from RPN tokens.
        String str; // String of new token (for convinience).
        Element value = null; // Value of new token.
        TokenType type = null; // Type of new token.
        boolean made; // Was monomial made?

        for (int i = 2; i < tokensRpn.size(); i++) {
            made = false;
            tc = tokensRpn.get(i);
            tp = tokensRpn.get(i - 1);
            tpp = tokensRpn.get(i - 2);

            int algTmp = ring.algebra[0];
            boolean inZpRing = algTmp == Ring.CZp || algTmp == Ring.CZp32
                    || algTmp == Ring.Zp || algTmp == Ring.Zp32;

            if (inZpRing) {
                if (tpp.isPolynomialNumber()) {
                    tpp.value(tpp.value().toNewRing(algTmp, ring));
                }
                if (!tc.str().equals("^") && tp.isPolynomialNumber()) {
                    tp.value(tp.value().toNewRing(algTmp, ring));
                }
            }

            // int / int in ring Q
            if ((ring.algebra[0] == Ring.Q || ring.algebra[0] == Ring.CQ)
                    && tc.str().equals("/")
                    && tp.isPolynomialNumber() && tp.str().matches("-?\\d+")
                    && tpp.isPolynomialNumber() && tpp.str().matches("-?\\d+")) {
                type = TokenType.POL_Z;
                value = tpp.value().divide(tp.value(), ring);
                str = tpp.str() + tc.str() + tp.str();
                tokensRpn.set(i - 2, new Token(type, str, ring));
                tokensRpn.get(i - 2).value(value);
                tokensRpn.remove(i - 1);
                tokensRpn.remove(i - 1);
                i -= 2;
                if (i < 2) {
                    i = 2;
                }

                continue;
            }

            // variable ^ integer
            if (tc.str().equals("^")
                    && tp.isPolynomialNumber()
                    && tp.str().matches("-?\\d+")) {
                switch (tpp.type()) {
                    case POL_Z:
                        if (tp.str().matches("\\d+")) {
                            made = true;
                            type = TokenType.POL_Z;
                            value = powPolNumZ(tpp, tp, ring);
                        }
                        break;
                    case POL_R:
                        if (tp.str().matches("\\d+")) {
                            made = true;
                            type = TokenType.POL_R;
                            value = powPolNumZ(tpp, tp, ring);
                        }
                        break;
                    default:
                        break;
                }
            }

            // monomial was made
            if (made) {
                str = tpp.str() + tc.str() + tp.str();
                tokensRpn.set(i - 2, new Token(type, str, ring));
                tokensRpn.get(i - 2).value(value);
                tokensRpn.remove(i - 1);
                tokensRpn.remove(i - 1);
                i -= 2;
                if (i < 2) {
                    i = 1;
                }
            }
        }
    }

    /**
     * Собирает моном из двух частей
     *
     * @param t1 переменная
     * @param t2 степень
     * @return моном вида "t1^t2"
     */
    private static Element powPolNumZ(Token t1, Token t2, Ring ring) {
        Polynom p1 = (Polynom) t1.value();
        Polynom p2 = (Polynom) t2.value();
        if (p2.isZero(ring)) {
            return p1.one(ring);
        }
        int power = p2.coeffs[0].intValue();
        Element res = p1.pow(power, ring);
        if (res instanceof Complex) { // XXX: probably hackish, check this.
            return ((Complex) res).re;
        } else {
            return res;
        }
    }

    /**
     * Parse SYMBOLIC_{...} and SYMBOLIC_{...}^{...}.
     *------------------
     * New: TENSOR = {}_{...}^{...}SYMBOLIC_{...}^{...}
     * Main part is the same: "SYMBOLIC_{". All other symbol can be absent.
     * {}_{.}S_{.}^{.}; {}^{.}S_{.}^{.}; {}^{.}S_{.}; {}S_{.}^{.};
     * В символьном виде не меняется. А в виде компоненты тензора создаются индексные:
     * {}_{ef}^{gh}SYMBOLIC_{ab}^{cd}--> S{[ab0ef]}^{[cd0gh]}
     * @param tokens        list of tokens to mutate.
     * @param underscoreIdx index of underscore symbol ("_").
     * @param nonCommut     list of noncommutative variables from Page.
     * @param prevTok       SYMBOLIC token.
     * @param nextTok       opening curly brace.
     * @param ring          ring.
     */
    private static void processSymbolicWithIndices(
            List<Token> tokens, int underscoreIdx, Set<String> nonCommut,
            Token prevTok, Token nextTok, Ring ring) {
        // Если имя есть в списке некоммутативных объектов, добавляем информацию по "индексам".
        if (nonCommut.contains(prevTok.str())) {
            // SYMBOLIC_{ ... } -- preparing indices to store in vector.
            tokens.remove(underscoreIdx); // remove "_"
            int closingIdx = findClosingBrace(tokens, "{", underscoreIdx); // закрывающая к Symb_{
            tokens.add(closingIdx, new Token(TokenType.VEC_BR_CL, "]", ring));
            tokens.add(underscoreIdx + 1, new Token(TokenType.VEC_BR_OP, "[", ring));
            // SYMBOLIC_{...}^{...}
            if (tokens.get(closingIdx + 3).str().equals("^")
                    && tokens.get(closingIdx + 4).str().equals("{")) {
                tokens.remove(closingIdx + 2);
                tokens.set(closingIdx + 2, new Token(TokenType.ARG_SEP, ",", ring));
                int closingPowerIdx = findClosingBrace(tokens, "{", closingIdx + 3);
                tokens.set(closingIdx + 3, new Token(TokenType.VEC_BR_OP, "[", ring));
                tokens.add(closingPowerIdx, new Token(TokenType.VEC_BR_CL, "]", ring));
            }
            //{}_{...}^{...}SYMBOLIC_{...}^{...}
            
        } else {
            // Склеиваем все токены до закрывающей фигурной скобки в один (пробелы пропадают).
            int closingIdx = findClosingBrace(tokens, "{", underscoreIdx + 1);
            if (closingIdx < tokens.size() + 2
                    && tokens.get(closingIdx + 1).str().equals("^")
                    && tokens.get(closingIdx + 2).str().equals("{")) {
                closingIdx = findClosingBrace(tokens, "{", closingIdx + 2);
            } // это закрывающая  для второй ^
            int tensor=-1;            
            for (int j = 0; j < tokens.size()-3; j++) {
                if ((tokens.get(j).str().equals("{"))&&(tokens.get(j+1).str().equals("}"))
                    &&(tokens.get(j+2).str().equals("_"))&&(tokens.get(j+3).str().equals("{")))
                {tensor=j; break;}} // tensor= позиция начала тензора
            if (tensor==-1)tensor=underscoreIdx; else tensor++; 
            // tensor или в начале тензора или после символа, будем все переписывать, начиная от tensor
            StringBuilder s = new StringBuilder(tokens.get(tensor-1).str());
            for (int i = tensor; i <= closingIdx; i++) {
                Token removed = tokens.remove(tensor);
                s.append(removed.str());
            }
            tokens.set(tensor - 1, new Token(TokenType.SYMBOLIC, s.toString(), ring));
            // все, что переписали влили на место одного символьного токена
        }
    }
}
