package com.mathpar.func.parser;

import com.mathpar.func.F;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains all predefined static objects for {@code ParserImpl} support.
 *
 * @author ivan
 */
final class ParserStatic {
    // all predefined regular exressions
    // =========================================================================
    /**
     * Real numbers regex.
     */
    public static final String RE_NUMBER = "\\G\\d+(\\.\\d+)?";
    /**
     * keywords regex
     */
    public static final String RE_KEYWORDS
            = "\\G(-\\s*\\\\infty|\\\\infty|\\\\times|\\\\star|\\\\ast|\\\\to|\\\\i"
            + "|\\\\[Aa]lpha|\\\\[Bb]eta|\\\\[Gg]amma|\\\\[Dd]elta|\\\\[Ee]psilon"
            + "|\\\\varepsilon|\\\\[Zz]eta|\\\\[Ee]ta|\\\\[Tt]heta|\\\\vartheta"
            + "|\\\\[Ii]ota|\\\\[Kk]appa|\\\\[Ll]ambda|\\\\[Mm]u|\\\\[Nn]u"
            + "|\\\\[Xx]i|\\\\[Oo]micron|\\\\[Pp]i|\\\\varpi|\\\\[Rr]ho"
            + "|\\\\varrho|\\\\[Ss]igma|\\\\varsigma|\\\\[Tt]au|\\\\[Uu]psilon"
            + "|\\\\[Pp]hi|\\\\varphi|\\\\[Cc]hi|\\\\[Pp]si|\\\\[Oo]mega"
            + "|\\\\degreeC|\\\\textdegreeC)";
    /**
     * index separator (for somethins like "A_{i, j, k}")
     */
    public static final String RE_SEP_INDICIES = "\\G[_]";
    /**
     * operations + - *
     */
    public static final String RE_OP_LA = "\\G([-+*/]|\\\\\\\\|\\\\cup|\\\\cap|\\\\triangle|\\\\setminus)";
    // operation ^
    public static final String RE_OP_RA = "\\G\\^";
    // operation !
    public static final String RE_OP_POSTFIX = "\\G[!']";
    public static final String RE_OP_BOOL
            = "\\G(\\\\lor|\\\\neg|==|\\\\le|\\\\ge|\\\\ne|\\\\&|<|>)";
    // opening brace for vector
    public static final String RE_BRACE_VECTOR_OPEN = "\\G\\[";
    // closing brace for vector
    public static final String RE_BRACE_VECTOR_CLOSE = "\\G\\]";
    // opening brace for set
    public static final String RE_BRACE_SET_OPEN = "\\G\\\\[\\[(]";
    // closing brace for set
    public static final String RE_BRACE_SET_CLOSE = "\\G\\\\[\\])]";
    // opening brace for VectorSet
    public static final String RE_BRACE_VECTOR_SET_OPEN = "\\G\\\\[{]";
    // closing brace for VectorSet
    public static final String RE_BRACE_VECTOR_SET_CLOSE = "\\G\\\\[}]";
    // opening braces
    public static final String RE_BRACE_OPEN = "\\G[({]";
    // closing braces
    public static final String RE_BRACE_CLOSE = "\\G[)}]";
    // function arguments separator
    public static final String RE_SEP_ARGS = "\\G[,=:]";
    // end of line
    public static final String RE_EOL = "\\G\\z";
    // spaces
    public static final String RE_SPACE = "\\G\\s+";
    //==========================================================================
    // PATTERNS_STATIC corresponding to regexpes
    static final Pattern NUMBER = Pattern.compile(RE_NUMBER);
    static final Pattern KEYWORD = Pattern.compile(RE_KEYWORDS);
    static final Pattern SEP_INDICES = Pattern.compile(RE_SEP_INDICIES);
    static final Pattern OP_LA = Pattern.compile(RE_OP_LA);
    static final Pattern OP_RA = Pattern.compile(RE_OP_RA);
    static final Pattern OP_POSTFIX = Pattern.compile(RE_OP_POSTFIX);
    static final Pattern OP_BOOL = Pattern.compile(RE_OP_BOOL);
    static final Pattern OP_FUNC = prepareAndComplieReFunc();
    static final Pattern BRACE_VECTOR_OPEN = Pattern.compile(RE_BRACE_VECTOR_OPEN);
    static final Pattern BRACE_VECTOR_CLOSE = Pattern.compile(RE_BRACE_VECTOR_CLOSE);
    static final Pattern BRACE_SET_OPEN = Pattern.compile(RE_BRACE_SET_OPEN);
    static final Pattern BRACE_SET_CLOSE = Pattern.compile(RE_BRACE_SET_CLOSE);
    static final Pattern BRACE_VECTOR_SET_OPEN = Pattern.compile(RE_BRACE_VECTOR_SET_OPEN);
    static final Pattern BRACE_VECTOR_SET_CLOSE = Pattern.compile(RE_BRACE_VECTOR_SET_CLOSE);
    static final Pattern BRACE_OPEN = Pattern.compile(RE_BRACE_OPEN);
    static final Pattern BRACE_CLOSE = Pattern.compile(RE_BRACE_CLOSE);
    static final Pattern SEP_ARGS = Pattern.compile(RE_SEP_ARGS);
    static final Pattern EOL = Pattern.compile(RE_EOL);
    static final Pattern SPACES = Pattern.compile(RE_SPACE);
    //==========================================================================
    // PATTERNS_STATIC and corresponding token TYPES_STATIC
   public static final List<Pattern> PATTERNS_STATIC;
   public static final List<TokenType> TYPES_STATIC;
    /**
     * SYMBOLIC tokens end with these patterns
     */
  public  static final List<Pattern> PATTERNS_SYMB_STATIC;
   public static final int VAR_INSERT_INDEX = 4;

    static {
        PATTERNS_STATIC = Arrays.asList(
                NUMBER, // 00
                OP_BOOL, // 01
                OP_FUNC, // 02
                KEYWORD, // 03
                // 04 Will add variables pattern here
                SEP_ARGS, // 05
                OP_LA, // 06
                OP_RA, // 07
                OP_POSTFIX, // 08
                SEP_INDICES, // 09
                BRACE_VECTOR_OPEN, // 10
                BRACE_VECTOR_CLOSE, // 11
                BRACE_SET_OPEN, // 12
                BRACE_SET_CLOSE, // 13
                BRACE_VECTOR_SET_OPEN, // 14
                BRACE_VECTOR_SET_CLOSE, // 15
                BRACE_OPEN, // 16
                BRACE_CLOSE, // 17
                EOL // 18
        );
        TYPES_STATIC = Arrays.asList(
                TokenType.NUM, // 00
                TokenType.OP_BOOL, // 01
                TokenType.OP_FUNC, // 02
                TokenType.SYMBOLIC, // 03
                TokenType.VAR, // 04
                TokenType.ARG_SEP, // 05
                TokenType.OP_LA, // 06
                TokenType.OP_RA, // 07
                TokenType.OP_POSTFIX, // 08
                TokenType.UNDERSCORE, // 09
                TokenType.VEC_BR_OP, // 10
                TokenType.VEC_BR_CL, // 11
                TokenType.SET_BR_OP, // 12
                TokenType.SET_BR_CL, // 13
                TokenType.VECTOR_SET_BR_OP, // 14
                TokenType.VECTOR_SET_BR_CL, // 15
                TokenType.BR_OP, // 16
                TokenType.BR_CL, //17
                TokenType.EOL //18
        );
        PATTERNS_SYMB_STATIC = Arrays.asList(
                SPACES,
                BRACE_VECTOR_SET_OPEN,
                BRACE_VECTOR_SET_CLOSE,
                OP_BOOL,
                OP_FUNC,
                KEYWORD,
                SEP_INDICES,
                OP_LA,
                OP_RA,
                OP_POSTFIX,
                BRACE_VECTOR_OPEN,
                BRACE_VECTOR_CLOSE,
                BRACE_SET_OPEN,
                BRACE_SET_CLOSE,
                BRACE_OPEN,
                BRACE_CLOSE,
                SEP_ARGS,
                EOL
        );
    }

    /**
     * Don't need to construct this.
     */
    private ParserStatic() {
    }

    /**
     * Generates regexp for string function names and creates corresponding
     * pattern.
     */
    private static Pattern prepareAndComplieReFunc() {
        /**
         * Compares string function names by length.
         */
        class FuncNameSorted implements Comparator<String> {
            @Override
            public int compare(final String name1, final String name2) {
                int l1 = name1.length();
                int l2 = name2.length();
                return (l1 == l2) ? 0 : (l1 > l2) ? 1 : -1;
            }
        }
        // future regexp for function names
        StringBuilder sbFunc = new StringBuilder("\\G\\\\(");
        int len = F.FUNC_NAMES.length;
        // добавляем в динамический массив имена функций != null
        ArrayList<String> notNullFuncNames = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            if (F.FUNC_NAMES[i] != null) {
                notNullFuncNames.add(Matcher.quoteReplacement(F.FUNC_NAMES[i]));
            }
        }
        // сортируем динамический массив с именами функций в обратном порядке
        // (первыми идут имена с большей длиной)
        Collections.sort(notNullFuncNames, Collections.reverseOrder(new FuncNameSorted()));
        len = notNullFuncNames.size();
        String[] sortedFuncNames = new String[len];
        notNullFuncNames.toArray(sortedFuncNames);

        // Перебор всех имен функций из класса F
        for (int i = 0; i < sortedFuncNames.length; i++) {
            if (!sortedFuncNames[i].matches(RE_OP_LA) && sortedFuncNames[i].length() != 0) {
                sbFunc.append(sortedFuncNames[i]).append("|");
            }
        }
        // удаляем последний символ "|" и добавляем ")"
        sbFunc.deleteCharAt(sbFunc.length() - 1).append(")");
        // компилируем полученное рег. выр-е в паттерн
        return Pattern.compile(sbFunc.toString());
    }
}
