package com.mathpar.func.parser;

import com.mathpar.func.F;
import com.mathpar.func.Fname;
import com.mathpar.func.Page;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ64;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mathpar.func.parser.ParserStatic.*;

/**
 * Utility class creating F objects from given string.
 * <p/>
 * <p>
 * How it works:</p>
 * <p/>
 * <ol>
 * <p/>
 * <li>Split input string at tokens using regular expressions. There are 2
 * arrays: {@code patterns} and {@code tokens} to construct the map:
 * {@code Pattern => Token}. Static patters are defined in {@code ParserStatic}
 * class. Some patterns are consructed at runtime and initially they are null,
 * that's why we don't use collection framework). Each pattern starts with
 * string {@code "\\G"} which is used to begin search from the end of previous
 * match. If there were no matches with predefined patterns, search goes forward
 * by one character, until {@code Matcher} meets predefined pattern again.
 * Everything between predefined patters becomes to {@code SYMBOLIC} type token.
 * For user-defined functions with pure {@code SYMBOLIC} arguments, names of
 * those arguments will be prefixed with {@code "$2"} string for inner
 * {@code Page} magic. E.g.: {@code "\\MySuperFunction(a, a + 1)"} becomes to
 * {@code "\\MySuperFunction($2a, a + 1)"} All tokens go to {@code tokens}
 * list.</li>
 * <p/>
 * <li>Preprocess tokens list for non-standard notation:
 * <p/>
 * <ol>
 * <p/>
 * <li>For matrix indexes:
 * <code>SYMBOLIC_{i, j} to SYMBOLIC{[i, j]};
 * SYMBOLIC_{i, j}_{k, l} to SYMBOLIC{[i, j], [k, l]}</code></li>
 * <p/>
 * <li>Limits:
 * <code>\lim_{x \\to 0} sin(x) to \lim(sin(x), x, 0)</code></li>
 * <p/>
 * <li>Integrals (both indefinite and definite):
 * <code>\int sin(x) d x to \int(sin(x), x);
 * \int_{0}^{1} sin(x) dx to \int(sin(x), x, 0, 1)</code></li>
 * <p/>
 * <li>Series/sums:
 * <code>\sum_{i = 0}^{n} (\cos(ix)) to \sum(cos(ix), i, 0, n);
 * \sum_{i = 0}^{\infty} (\sin(ix)) to \series(\sin(ix), i, 0)</code></li>
 * <p/>
 * <li>Sequences:</li>
 * <p/>
 * <li>Vectors:
 * <code>[a, b, c] to \\VectorS(a, b, c)</code></li>
 * <p/>
 * <li>Logarithms:
 * <code>log_{a} (\sin(x)) to \log(sin(x), a)</code></li>
 * <p/>
 * <li>Geninverse:
 * <code>^{+} to ^{\plus}</code></li>
 * <p/>
 * <li>Convert implicit multiplication to explicit.</li>
 * <p/>
 * <li>Convert unary minus to multiplication by -1.</li>
 * <p/>
 * </ol>
 * <p/>
 * </li>
 * <p/>
 * <li>Construct Reversed Polish Notation (RPN) - postfix form of input
 * expression.</li>
 * <p/>
 * <li>Preprocess RPN for monomials ({@code <variable_name>^<integer_number>}),
 * polynomials (production, sum of monomials etc.) and special power-like
 * function for matrices:
 * <p/>
 * <ol>
 * <p/>
 * <li>Transpose
 * <code>A^{T}</code></li>
 * <p/>
 * <li>conjugate
 * <code>A^{\\ast}</code></li>
 * <p/>
 * <li>adjoint
 * <code>A^\\star</code></li>
 * <p/>
 * <li>genInverse
 * <code>A^{+}</code></li>
 * <p/>
 * <li>closure
 * <code>A^\\times</code></li>
 * <p/>
 * </ol>
 * <p/>
 * <strong>Note:</strong> inverse (
 * <code>A^{-1}</code>) stays as {@code POW} for further analyze.</li>
 * <p/>
 * <li>Build an F-tree from RPN.</li>
 * <p/>
 * </ol>
 * <p/>
 * <
 * p/>
 * <b>Note about numbers at different rings:</b> Real numbers (contains "."
 * (dot) character) go to first algebra of ring.
 * <p/>
 * Integer numbers go to first <i>integer</i> algebra of ring (if it exists). If
 * there are no integer algebras in ring then integer numbers go to first
 * algebra explicitly.
 *
 * @see Parser
 * @see ParserStatic
 * @see Token
 * @see TokenType
 */
class ParserImpl {
    private List<Fname> nonCommutWithIndices;
    /**
     * Expression to parse.
     */
    private final String expr;
    /**
     * Current ring.
     */
    private final Ring ring;
    /**
     * List of user defined functions.
     */
    private List<String> userFuncStr;
    /**
     * Pattern for commutative variables. Depends on ring.
     */
    private Pattern var;
    /**
     * Pattern for user-defined functions.
     */
    private Pattern userFunc;
    /**
     * End of last known pattern. Everything located in input string between
     * {@code lastKnownEnd} and {@code currKnownStart} is treated as
     * non-predefined expression and produces SYMBOLIC token.
     */
    private int lastKnownEnd;
    /**
     * Beginning of current known pattern.
     */
    private int currKnownStart;
    /**
     * List of patterns with order of checking.
     */
    private List<Pattern> patterns;
    /**
     * List with token types which correspond to {@code Pattern} list.
     */
    private List<TokenType> types;
    /**
     * SYMBOLIC can start with one of these patterns.
     */
    private List<Pattern> patternsSymb;
    /**
     * Tokens list.
     */
    private List<Token> tokens;
    /**
     * RPN tokens list.
     */
    private List<Token> tokensRpn;

    /**
     * Main constructor.
     * <p/>
     * <p> Additional actions:</p>
     * <ul>
     * <li>Prepare regex for variables (commutative and non-commutative) and
     * compile {@code Pattern} for it.</li>
     * <li>Copy predefined static patterns from {@code ParserStatic}.</li>
     * <li>Add generated patterns to {@code patterns} list.</li>
     * </ul>
     *
     * @param expr expression to parse
     * @param ring ring
     */
    ParserImpl(final String expr, final Ring ring) {
        this.expr = expr;
        this.ring = ring;

        patterns = new ArrayList<Pattern>(PATTERNS_STATIC);
        patternsSymb = new ArrayList<Pattern>(PATTERNS_SYMB_STATIC);
        types = new ArrayList<TokenType>(TYPES_STATIC);

        prepareAndCompileReVars();

        // Insert constructed patterns at appropriate positions.
        patterns.add(VAR_INSERT_INDEX, var);
    }

    /**
     * Constructor with user-defined functions. Calls
     * {@link ParserImpl(String, Ring)} and also prepares pattern for
     * user-defined functions based on given list with their names.
     *
     * @param expr expression to parse
     * @param ring ring
     * @param userFuncStr {@code List} with user defined functions names
     */
    ParserImpl(final String expr, final Ring ring, final List<String> userFuncStr) {
        this(expr, ring);
        this.userFuncStr = userFuncStr;

        userFunc = prepareAndCompileReUserFunc();

        // Insert user defined pattern before predefined functions pattern.
        int userDefinedFunctionInsertionIndex = patterns.indexOf(OP_FUNC) + 1;
        patterns.add(userDefinedFunctionInsertionIndex, userFunc);
        types.add(userDefinedFunctionInsertionIndex, TokenType.OP_FUNC);
    }

    /**
     * @param expr expression to parse
     * @param ring ring
     * @param userFuncStr {@code List} with user defined functions names
     * @param nonCommutWithIndices {@code List} with non-commutative
     * {@code Fname}. Only {@code SYMBOLIC} tokens from this list are specially
     * processed for lower/upper LaTeX-style indices (e.g.: A_{1, 2}; B_{1}^{2})
     * to extract those indices and put them to {@code Fname} fields.
     */
    ParserImpl(final String expr, final Ring ring, final List<String> userFuncStr,
            final List<Fname> nonCommutWithIndices) {
        this(expr, ring, userFuncStr);
        this.nonCommutWithIndices = nonCommutWithIndices;
    }

    /**
     * Generates regex and creates pattern for commutative and non-commutative
     * variables from {@code ring}.
     */
    private void prepareAndCompileReVars() {
        StringBuilder sbVars = new StringBuilder("\\G(");
        StringBuilder sbAlgVars = new StringBuilder("\\G(");

        for (String varStr : ring.varNames) {
            String varStrEscaped = Pattern.quote(varStr);
            // Generator variables should not be appended to usual variables
            if (Utils.getAlgebraType(ring, varStr) != Ring.G) {
                sbVars.append(varStrEscaped).append('|');
            } else {
                sbAlgVars.append(varStrEscaped).append('|');
            }
        }
        // After loop there is an extra '|' char. Remove it and add closing ')'
        sbVars.deleteCharAt(sbVars.length() - 1).append(')');
        sbAlgVars.deleteCharAt(sbAlgVars.length() - 1).append(')');
        if (sbVars.toString().equals("\\G)")) {
            var = Pattern.compile("\\G^$");
        } else {
            var = Pattern.compile(sbVars.toString());
        }
    }

    /**
     * Generates regex and creates pattern for user-defined functions.
     *
     * @return {@code Pattern} for user-defined functions
     */
    private Pattern prepareAndCompileReUserFunc() {
        /**
         * Compares strings with functions names by length.
         */
        class FuncNameSorted implements Comparator<String> {
            @Override
            public int compare(final String name1, final String name2) {
                return Integer.valueOf(name1.length()).
                        compareTo(name2.length());
            }
        }

        if (userFuncStr.isEmpty()) {
            return Pattern.compile("\\G^$");
        }

        // Future regexp for user-defined function names.
        StringBuilder sbUserFunc = new StringBuilder("\\G\\\\(");
        // Reverse sort of list with strings of user-defined functions.
        // (begin with functions with bigger lenght).
        ArrayList<String> sortedUserFuncStr = new ArrayList<String>(userFuncStr);
        Collections.sort(sortedUserFuncStr, Collections.reverseOrder(new FuncNameSorted()));
        // Add all function names from sorted arrays.
        for (String s : sortedUserFuncStr) {
            sbUserFunc.append(Pattern.quote(s)).append("|");
        }
        // Remove last "|" and add ")".
        sbUserFunc.deleteCharAt(sbUserFunc.length() - 1).append(")");
        // Compile generated regex to Pattern and return it.
        return Pattern.compile(sbUserFunc.toString());
    }

    /**
     * Breaks expression to token list.
     */
    private void tokenize() {
        tokens = new ArrayList<Token>();
        // Current token.
        Token token;
        Matcher mat = EOL.matcher(expr);
        // Add tokens to list until End Of Line.
        do {
            token = nextToken(mat);
            tokens.add(token);
        } while (token.type() != TokenType.EOL && expr.length() > 0);
    }

    /**
     * Returns the next token of input expression.
     *
     * @param mat initial {@code Matcher} object.
     *
     * @return next token of input expression.
     */
    private Token nextToken(final Matcher mat) {
        String str;
        TokenType type;

        // All in \hbox{...} goes to single SYMBOLIC token.
        if (tokens.size() >= 2
                && tokens.get(tokens.size() - 1).type() == TokenType.BR_OP
                && tokens.get(tokens.size() - 2).type() == TokenType.OP_FUNC
                && F.getNameFromStr(tokens.get(tokens.size() - 2).str()) == F.HBOX) {
            lastKnownEnd = Utils.gotoPairChar(expr, '{', '}', currKnownStart - 1);
            return new Token(TokenType.SYMBOLIC,
                    expr.substring(currKnownStart, lastKnownEnd), ring);
        }

        // Space was found before current token.
        boolean currTokenHasSpaceBefore = false;
        // Skip spaces.
        if (mat.usePattern(SPACES).find(lastKnownEnd)) {
            lastKnownEnd = mat.end();
            currTokenHasSpaceBefore = true;
        }

        // String literals (everything between single quotes).
        final char quoteChar = '\'';
        if (lastKnownEnd < expr.length() && expr.charAt(lastKnownEnd) == quoteChar) {
            currKnownStart = expr.indexOf(quoteChar, lastKnownEnd + 1);
            if ((currKnownStart != -1)&&(currKnownStart != lastKnownEnd+1)) {
                String s = expr.substring(lastKnownEnd + 1, currKnownStart);
                currKnownStart++;
                lastKnownEnd = currKnownStart;
                Token t = new Token(TokenType.SYMBOLIC, s, ring);
                t.hasSpaceBefore(currTokenHasSpaceBefore);
                return t;
            } else {
                currKnownStart = lastKnownEnd;
            }
        }

        // Find matches for predefined patterns from current position.
        for (int i = 0; i < patterns.size(); i++) {
            Pattern currPattern = patterns.get(i);
            if (mat.usePattern(currPattern).find(lastKnownEnd)) {
                // Current token type.
                type = types.get(i);
                // Current matching string.
                str = mat.group();
                // Matching string was found.
                lastKnownEnd = mat.end();
                currKnownStart = lastKnownEnd;

                // XXX: hack. If ring variable was recognized as keyword, change it to var.
                // E.g.: SPACE = R[\alpha, y]; p = \alpha^2 y^3 + 5\alpha
                if (type == TokenType.SYMBOLIC) {
                    int strLen = str.length();
                    if (mat.usePattern(var).find(lastKnownEnd - strLen)) {
                        str = mat.group();
                        type = TokenType.VAR;
                        lastKnownEnd = mat.end();
                        currKnownStart = lastKnownEnd;
                    }
                }

                // XXX: hack. Solve boolean operators-functions conflicts (e.g. \ge - \getStatus)
                if (type == TokenType.OP_BOOL
                        && mat.usePattern(OP_FUNC).find(lastKnownEnd - str.length())) {
                    String tmp = mat.group();
                    if (tmp.length() > str.length()) {
                        type = TokenType.OP_FUNC;
                        str = tmp;
                        lastKnownEnd = mat.end();
                        currKnownStart = lastKnownEnd;
                    }
                }

                // Set special syntax: \[, \(, \), \]
                if (type == TokenType.SET_BR_OP) {
                    type = TokenType.SET;
                    int openParen = lastKnownEnd;
                    int closeParen = Utils.gotoClosingSetBrace(expr,
                            lastKnownEnd, "\\)", "\\]");
                    if (closeParen == -1) {
                        throw new ParserException("Can't find closing brace for set.");
                    }
                    str = str + expr.substring(openParen, closeParen);
                    str = str.replaceAll("\\\\([\\[(])", "$1")
                            .replaceAll("\\\\([\\])])", "$1");
                    lastKnownEnd = closeParen;
                    currKnownStart = lastKnownEnd;
                }

                // XXX: dirty hack. Solve functions-keywords conflicts (e.g. \time - \times, \d - \delta)
                if (type == TokenType.OP_FUNC) {
                    // Try to look forward for keyword after function's match.
                    int funcLen = str.length();
                    if (mat.usePattern(KEYWORD).find(lastKnownEnd - funcLen)) {
                        String tmp = mat.group();
                        // Current keyword is larger then found function.
                        if (tmp.length() > funcLen) {
                            type = TokenType.SYMBOLIC;
                            str = tmp;
                            lastKnownEnd = mat.end();
                            currKnownStart = lastKnownEnd;
                        }
                    }

                    // XXX: hack inside hack. Hell yeah!
                    // Everything inside \free memory() function goes to SET token.
                    if (str.equals("\\set")) {
                        type = TokenType.SET;
                        int openParen = expr.indexOf('(', lastKnownEnd);
                        int closeParen = Utils.gotoPairChar(expr,
                                new char[] {'(', '{', '['},
                                new char[] {')', '}', ']'},
                                openParen);
                        str = expr.substring(openParen + 1, closeParen);
                        lastKnownEnd = closeParen + 1;
                        currKnownStart = lastKnownEnd;
                    }

                    // XXX: hack. \cup and \cap should be infix.
                    if (str.matches(RE_OP_LA)) {
                        type = TokenType.OP_LA;
                    }
                }

                Token t = new Token(type, str, ring);
                t.hasSpaceBefore(currTokenHasSpaceBefore);
                return t;
            }
        }

        /*
         * There was no any match with predefined patters. Start constructing
         * SYMBOLIC tokens. Begin with the end of previous search. New SYMBOLIC
         * token contains all characters between the end of previous search and
         * first matching with any pattern from patternsSymb list.
         */
        currKnownStart = lastKnownEnd;
        while (true) {
            // Trying to find matching with predefined token which can 'cancel'
            // SYMBOLIC from current position.
            for (int j = 0; j < patternsSymb.size(); j++) {
                if (mat.usePattern(patternsSymb.get(j)).find(currKnownStart)) {
                    type = TokenType.SYMBOLIC;
                    str = expr.substring(lastKnownEnd, currKnownStart);
                    lastKnownEnd = currKnownStart;
                    Token t = new Token(type, str.trim(), ring);
                    t.hasSpaceBefore(currTokenHasSpaceBefore);
                    return t;
                }
            }
            // There was no matching -- move pointer of current position forward.
            currKnownStart++;
        }
    }

    /**
     * Creates Reverse Polish Notation (RPN) of tokens list.
     */
    private void makeRPN() {
        int TenzorType=-1; // для поиска канструкций _{..}^
        tokensRpn = new ArrayList<Token>();
        Stack<Token> st = new Stack<Token>();
        Token ct; // Current token.
        TokenType tt; // Type of current token.
        Token tmp; // Temporary token.
        int solveIdx = -1; // for solve with "=" sign.
        for (int i = 0; i < tokens.size(); i++) {
            ct = tokens.get(i);
            tt = ct.type();
            switch (tt) {
                //  case UNDERSCORE: st.push(ct); TenzorType=1; break;
                // Polynom, generator variable -- add it directly to the output.
                case POL_Z:
                case POL_R:
                case SET:
                    tokensRpn.add(ct);
                    break;
                // Undefined symbol.
                case SYMBOLIC:
                    if (ct.argsNum() > 0) {// Has arguments -- push to stack (as a function).
                        st.push(ct);
                    } else {// Doesn't have arguments -- add it directly to the output.
                        tokensRpn.add(ct);
                    }
                    break;
                // Opening brace -- push it to the stack.
                case BR_OP:
                    // If there is function before, set it's arguments number
                    // to positive integer (magic number 100).
                    if (i > 0 && (tokens.get(i - 1).type() == TokenType.OP_FUNC
                            || tokens.get(i - 1).type() == TokenType.SYMBOLIC)) {
                        ct.argsNum(100);
                    }
                    st.push(ct);
                    break;
                // Function -- push it to the stack.
                case OP_FUNC:
                    // XXX: hack for solve with "=" sign
                    if (ct.str().equals("\\solve")) {
                        solveIdx = st.size();
                    }
                    st.push(ct);
                    break;
                // Function argumets separator -- pop tokens from the stack to
                // the output until closing brace appears on the top.
                case ARG_SEP:
                    // XXX: hack for solve with "=" sign -> solveLEQ.
                    if (solveIdx != -1 && ct.str().equals("=")) {
                        st.get(solveIdx).str("\\solveLEQ");
                        solveIdx = -1;
                    }
                    while ((tmp = st.peek()).type() != TokenType.BR_OP) {
                        st.pop();
                        tokensRpn.add(tmp);
                    }
                    break;
                // Infix operation -- pop tokens from stack to the output until
                // priority of current operation is less or equal of priority
                // of operation on the top of the stack (if any).
                case OP_LA:
                case OP_RA:
                case OP_POSTFIX:
                case OP_BOOL:
                    boolean arbitraryArityMultAdd;
                    if (arbitraryArityMultAdd = tryMakeArbitraryArityMultiplyAdd(ct, st)) {
                        break;
                    }
                    while (st.size() > 0
                            && Utils.getPriority(ct) <= Utils.getPriority(st.peek())) {
                        tmp = st.pop();
                        tokensRpn.add(tmp);
                        // Check again after popping from stack.
                        // E. g. when current token is '*' and stack is [*, ^, ^]
                        // we should pop '^' two times and after that modify the '*' at the stack.
                        if (arbitraryArityMultAdd = tryMakeArbitraryArityMultiplyAdd(ct, st)) {
                            break;
                        }
                    }
                    if (!arbitraryArityMultAdd) {
                        st.push(ct);
                    }
                    break;
                // Closing brace -- pop tokens from the stack to the output until
                // opening brace appears on the top of the stack. Opening brace
                // is just removed from the stack (doesn't go to the output).
                case BR_CL:
                    if (st.size() > 0) {
                        while ((tmp = st.peek()).type() != TokenType.BR_OP) {
                            st.pop();
                            tokensRpn.add(tmp);
                        }
                    }
                    // Now on top of the stack should be opening brace.
                    // We pop it.
                    tmp = st.pop();
                    // If opening brace corresponing to current closing brace
                    // stood after a function token, there is a function token
                    // on the top after pop opening brace from the stack.
                    // We pop this funtion token to the output.
                    if (tmp.argsNum() >= 0) {
                        tokensRpn.add(st.pop());
                    }
                    break;
                default:
                    break;
            }
        }
        // Pop remaining tokens from the stack to the output.
        while (st.size() > 0) {
            tokensRpn.add(st.pop());
        }
    }

    /**
     * @param ct current token
     * @param st stack with operators
     *
     * @return {@code true} when it's possible to use arbitrary arity + or *
     * function.
     */
    private boolean tryMakeArbitraryArityMultiplyAdd(Token ct, Stack<Token> st) {
        if (st.isEmpty()) {
            return false;
        }
        String ctStr = ct.str(), stPeekStr = st.peek().str();
        boolean currAndStackTopAreMultiply = ctStr.equals("*") && stPeekStr.equals("*");
        boolean currAndStackTopAreAdd = ctStr.equals("+") && stPeekStr.equals("+");
        boolean doArbitraryArity = currAndStackTopAreAdd || currAndStackTopAreMultiply;
        if (doArbitraryArity) {
            int argsNum = st.peek().argsNum() + 1;
            List<Integer> indNotNull = new ArrayList<Integer>(argsNum);
            for (int j = 0; j < argsNum; j++) {
                indNotNull.add(j);
            }
            st.peek().argsNum(argsNum).indNotNull(indNotNull);
        }
        return doArbitraryArity;
    }

    /**
     * Makes {@code F} F object from of tokens in Reversed Polish Notation.
     *
     * @return {@code F} instance
     */
    private F makeF() {
        Stack<Element> st = new Stack<Element>();
        // Curent token type.
        TokenType tt;
        // Temporary F instance.
        Element tmpF = null;
        // Arguments number.
        int argsNum;
        // Function name.
        int funcName;

        for (Token ct : tokensRpn) {
            tt = ct.type();
            switch (tt) {
                case SYMBOLIC:
                    argsNum = ct.argsNum();
                    if (argsNum <= 0) {
                        tmpF = new F(ct.value());
                        break;
                    } else {
                        // Fill array of arguments considering not-null arguments
                        // and their positions.
                        List<Integer> indNotNull = ct.indNotNull();
                        Element[][] indices = new Element[3][];
                        Element[] args = new Element[argsNum];
                        for (int i = argsNum - 1; i >= 0; i--) {
                            if (indNotNull.contains(i)) {
                                args[i] = st.pop();
                                if (args[i] instanceof F) {
                                    F arg = (F) args[i];
                                    if (arg.name == F.VECTORS) {
                                        indices[i] = arg.X;
                                    }
                                }
                            }
                        }
                        // create Fname instance with arguments
                        tmpF = new Fname(ct.str());
                        ((Fname) tmpF).indices = indices;
                        break;
                    }
                case POL_Z:
                    tmpF = ct.value(); int alg=ring.algebra[0];
                    if((alg==Ring.Zp32)||(alg==Ring.Zp)){if (tmpF instanceof Polynom){
                        tmpF=((Polynom)tmpF).toNewRing(alg, ring); }}
                    break;
                case POL_R:
                case SET:

                  tmpF = ct.value();  break;
                case OP_LA:
                case OP_RA:
                case OP_POSTFIX:
                case OP_BOOL:
                case OP_FUNC:
                    // Get function name from it's string form.
                    funcName = F.getNameFromStr(ct.str());
                    boolean isUserFunc = false;
                    if (funcName == -1) {
                        // It was an user defined function. ПРОЦЕДУРА В ПРОГРАММЕ?
                        int indexOfFunc = userFuncStr.indexOf(ct.str().substring(1));
                        funcName = F.MAX_F_NUMB + indexOfFunc;
                        isUserFunc = true;
                    }
                    argsNum = ct.argsNum();
                    // Replace SUM with 3 arguments with SERIES.
                    if (funcName == F.SUM && argsNum == 3) {
                        funcName = F.SERIES;
                    }

                    // Fill array of arguments considering not-null arguments
                    // and their positions.
                    List<Integer> indNotNull = ct.indNotNull();
                    Element[] args = new Element[argsNum];
                    if (indNotNull != null) {
                        for (int i = argsNum - 1; i >= 0; i--) {
                            if (indNotNull.contains(i)) {
                                Element el = st.pop();
                                // For plain Fnames in arguments add to it's
                                // name field magic "$2" string
                                if (isUserFunc && el instanceof F) {
                                    F f = (F) el;
                                    if (f.name == F.ID && f.X[0] instanceof Fname) {
                                        f.X[0] = new Fname("$2" + ((Fname) f.X[0]).name);
                                    }
                                }
                                args[i] = el;
                            }
                        }
                    }
                    tmpF = new F(funcName, args);
                    break;
                default:
                    break;
            }
            // push to the stack created F instance.
            st.push(tmpF); 
        }

        F top;
        // Resulting F object is placed on the top of the stack.
        // Pop it and wrap to F if needed.
        if (st.peek() instanceof F) {
            top = (F) st.pop();
        } else {
            top = new F(st.pop());
        }
        return top;
    }

    /**
     * Parses given string with function written at ATeX notation.
     *
     * @return {@code F} instance acquired from {@code expression}.
     */
    public F getF() {
        // Break expression to tokens.
        tokenize();

        // Process "special" functions (non-standard notation).
        Utils.prepareTokensForSolveTrig(tokens, ring);
        if ((nonCommutWithIndices != null)&&(nonCommutWithIndices.size()>0)) {
            Set<String> nonCommut = new LinkedHashSet<>(nonCommutWithIndices.size());
            for (Fname fname : nonCommutWithIndices) {nonCommut.add(fname.name);}
            Utils.prepareTokensForFname(tokens, nonCommut, ring); 
        } else {
            Utils.prepareTokensForFname(tokens, Collections.<String>emptySet(), ring);
        }
        Utils.prepareTokensForLim(tokens, ring);
        Utils.prepareTokensForIntegral(tokens, ring);
        Utils.prepareTokensForD(tokens, ring);
        Utils.prepareTokensForSum(tokens, ring);
        Utils.prepareTokensForSequence(tokens, ring);
        Utils.prepareTokensForLog(tokens, ring);
        Utils.prepareTokensForGenInverse(tokens, ring);
        Utils.prepareTokensForVectorSet(tokens, ring);
        Utils.prepareTokensForVector(tokens, ring);
        // Insert explicit multiplication
        Utils.insertMultiply(tokens, ring);
        // Process unary minus.
        Utils.unaryMinus(tokens, ring);
        // Fill arguments number.
        Utils.getAllArgNumber(tokens);

        // Make RPN.
        makeRPN();
        Utils.makeMonomialsRpn(tokensRpn, ring);
        Utils.prepareTokensRpnForPowerLikeOperations(tokensRpn, ring);

        // Create F instance from RPN.
        F f = makeF();
        return f;
    }

    @Override
    public String toString() {
        return "Parser{" + "expr=" + expr + '}';
    }
}
