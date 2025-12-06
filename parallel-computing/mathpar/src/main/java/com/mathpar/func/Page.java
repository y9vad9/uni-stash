package com.mathpar.func;
//import com.mathpar.Graphic2D.Element2D.Planimetria;
import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.parser.Parser;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.matrix.Table;
import com.mathpar.number.*;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import com.mathpar.polynom.Polynom;
//import com.mathpar.probability.RandomQuantity;
//import com.mathpar.showgraph.Plots;
import com.mathpar.web.db.entity.CheckResult;
import com.mathpar.web.db.entity.User;
import com.mathpar.web.exceptions.MathparException;
import org.apache.commons.io.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;

public class Page {
    private static final Logger LOG = getLogger(Page.class);
    private static final int OP_JUMP = -1;
    private static final int OP_COND_JUMP = -2;
    private static final int OP_RETURN = -3;
    private static final String KW_PROC = "\\procedure";
    private static final String KW_RETURN = "\\return";
    /**
     * маска для векторов и матриц
     */
    public static final Pattern VECT_MATRIX =
            Pattern.compile(" *\\(*[a-zA-Z][a-zA-Z]*_\\{(.*\\,)*.*\\}\\)* *");
    /**
     * Root directory for storing files.
     */
    public static final File MATHPAR_DIR = new File(System.getProperty(
            "java.io.tmpdir", "/tmp") + File.separator + "mathpar");
    /**
     * Default charset for various IO operations.
     */
    public static final Charset CHARSET_DEFAULT = Charset.forName("UTF-8");
    //---%<---
    /**
     * Cписок выражений на этой странице - основной обьект/
     * Но у каждой процедуры есть свой expr!  Поэтому его всегда нужно передавать в аргументах!
     */
    public List<Element> expr;
    /**
     * Имена, которые содержат символ "подчеркивание" и являются элементми
     * вектора или матрицы хранятся в виде объектов "new Fname(fn.name, new
     * NumberZ64(pos_Matrix_in_page.expr)"
     */
    public List<Fname> nonCommutWithIndices;
    /**
     * Данные этой станицы, которые хранятся в отдельном обьекте.
     */
    public final PageData data;
    /**
     * Ring which is active now
     */
    public Ring ring;
    /**
     * указатель на позицию в expr, куда записан последний оператор(для печати
     * по умолчанию)
     */
    private int posLast;
    /**
     * The number of section which is executed now.
     */
    private int currentSectionNumber = 0;
    /**
     * TODO: don't store images in memory!!! Images plotted on this page. Key is
     * the section number.
     */
    private final Map<Integer, BufferedImage[]> images = new HashMap<>();
    /**
     * Stores plot functions that can be replotted. It's assumed that there is
     * only one such function in each section.
     */
    private final Map<Integer, F> plots = new HashMap<>();
    private final Map<Integer, double[][]> matrix3d = new HashMap<>();
    private final Map<Integer, double[][]> xyzCube = new HashMap<>();
    /**
     * HTTP session ID which this Page belongs to.
     */
    private String sessionId;
    /**
     * Personal user directory for uploading files, generating PDF etc.
     */
    private File userDir;
    /**
     * Directory for uploaded files in {@literal userDir}.
     */
    private File userUploadDir;
    /**
     * User info populated after successful authentication.
     */
    private User user;
    /**
     * Allows to communicate with cluster.
     */
    private QueryCreator clusterQueryCreator;
    ////////////////Настройки для 2D///////////////////////
    public double xMin = 0;
    public double xMax = 0;
    public double yMin = 0;
    public double yMax = 0;
    public String nameOX = "x";
    public String nameOY = "y";
    public String title = "";
    public String option = "";
    public double[] optionsPlot = null;
    //////////////////////////////////////////////////////

    /**
     * Page в которой точно не используется CanonicForm и числа типа R или C
     */
    public Page() {
        this(Ring.ringR64xyzt);
    }

    /**
     * Page в которой точно не используется CanonicForm и числа типа R или C
     *
     * @param r
     */
    public Page(Ring r) {
        expr = new ArrayList<>(16);
        data = new PageData();
        nonCommutWithIndices = new ArrayList<>();
        ring = r;

    }

    /**
     * Initialization of CanonicForms and ConstR in ring CREATION STANDARD PAGE
     * in MATHPAR for NEW USER !!!
     *
     * @param r simple ring
     * @param init -- true (initialization) -- false (without initialization )
     */
    public Page(Ring r, boolean init) {
        this(r);
        if (init) {
            ring.CForm = new CanonicForms(ring, true);
            new NumberR();
            ring.constR = FuncNumberR.setConstRInit();
        }
    }

    /**
     * THE MAIN METHOD OF CLASS PAGE Метод получающий строку с заданием и
     * возвращающий ответ
     *
     * @param str исходная строка с заданием
     * @param sectNumb номер секции
     *
     * @return строка с ответом
     */
    public String execution(String str, int sectNumb) {
        currentSectionNumber = sectNumb;
        ring.exception = new StringBuffer();// очищаем поле для вывода прехваченных ошибок
        // экземпляр класса для упрощения выражений и исполнения операторов,
        // является одним из звеньев связи между интерфейсом и ядром
        //   nonCommutWithIndices = new ArrayList<>();
        data.init(str);// initialization of all fields in page.data and set str to section.
        IntList proc = new IntList();
        //    CanonicForms.returnFirstStatsCForm();
        String str1 = deleteComments(str);
        readOperators(str1, proc); //input of all operators of section to the program Proc
        data.transProg.add(proc);      // and to the field: funcs, transProg, procNames, argsOfProc
        // и исполняются все операции со служебными словами, но поле page.expr остается чистым!
        if (data.section[0].toString().trim().length() > 0) {
            //Исполняется программа и вводятся выражения в поле expr
            go(data.transProg.size() - 1, expr);//2015 , ring.CForm);
        }
        //Сеанс закончен, вывод результата
        return FUtils.deleteDoubledBrackets(data.section[1].toString());
    }

    public String execution(String s) {
        return execution(s, 0);
    }

    public CheckResult check(List<String> userSolutionSections, List<String> dbSolutionSections) {
        LOG.info("User solutions: {}, \nSolutions from DB: {}", userSolutionSections, dbSolutionSections);
        String us, db, usIn = userSolutionSections.get(userSolutionSections.size() - 1);
        int pos_sem = usIn.indexOf(';');
        int pos_eq = usIn.indexOf('=');
        us = (pos_sem > 0) ? usIn.substring(pos_eq + 1, pos_sem) : usIn.substring(pos_eq + 1);
        db = dbSolutionSections.get(dbSolutionSections.size() - 1);
        pos_sem = db.indexOf(';');
        pos_eq = db.indexOf('=');
        db = (pos_sem > 0) ? db.substring(pos_eq + 1, pos_sem) : db.substring(pos_eq + 1);
        String res = db + "-(" + us + ")";
        CanonicForms cf = new CanonicForms(ring, true);
        F f = Parser.getF(res, this.ring, Collections.<String>emptyList(), this.nonCommutWithIndices);
        ArrayList<Element> expression = new ArrayList<>();
        replaceOfSymbols(f, expression);
        Element f_out = cf.InputForm(f, this, expression);
        return f_out.isZero(ring) ? CheckResult.OK : CheckResult.WRONG;
    }

// ///////////////////////////////////////////////////////////////////////
    /** NOT MATRIX, NOT VECTOR!
     * The string obj may have value abc_{x,y,f(x),..,Z} For the case like this:
     * abc_{(x,y,f(x),..,Z)} - returns obj without changes.
     *
     * for abc_{}  and abc_{}^{} we do all changes in indeces
     * @param obj
     * @param expression
     *
     * @return
     */
    public String replaceOfSymbolsInIndeces(String obj, List<Element> expression) {
        int b = obj.indexOf('{');
        int bCirc = obj.indexOf('(', b);
        if ((b != -1) && (bCirc != -1) && (obj.substring(b + 1, bCirc).trim().equals(""))) { return obj;}
        int e = obj.lastIndexOf('}');
        int eF = obj.indexOf('}');
        String strV,strV2=null;
        if(e!=eF){ strV = obj.substring(b + 1, eF);
                   strV2 = obj.substring(obj.lastIndexOf('{')+1, e);
        }else   strV = obj.substring(b + 1, e);
        if (strV.length() > 0) {   
            int fps = ring.FLOATPOS;
            ring.setFLOATPOS(0);
            F ff2=null; 
            F ff = Parser.getF("[" + strV + "]", ring);
            replaceOfSymbols(ff, expression);
            String ffS = ff.toString(ring);
            if (strV2!=null) {          
                ff2 = Parser.getF("[" + strV2 + "]", ring);
                replaceOfSymbols(ff2, expression);       
                 String ffS2 = ff2.toString(ring);
                 ffS=obj.substring(0,b+1)+ffS.substring(1,ffS.length()-1)+"}^{"+ffS2.substring(1,ffS2.length()-1)+obj.substring(e);}
            else ffS = obj.substring(0, b + 1) + ffS.substring(1, ffS.length() - 1) + obj.substring(e);
            ring.setFLOATPOS(fps);
            return ffS;
        }
        return obj;
    }

    /**
     * Changing the symbolic names in the function. The list of expressions will
     * be examined started from the end. If the symbolic name will be found then
     * the pointer into this name in the function will be changed and set into
     * the expression.
     */
    public void replaceOfSymbols(Element obj, List<Element> expression) {
        if (expression.isEmpty()) {
            return;
        }
        int len = 0;
        Element[] X = null;
        if (obj instanceof F) {
            if (((F) obj).name == F.ELEMENTOF) {
                replaceOfSymbols(((F) obj).X[0], expression);
            }
            X = ((F) obj).X;
            len = X.length;
        } else if (obj instanceof Fname) {
            Fname objFname = (Fname) obj;
            len = 1;
            X = ((Fname) obj).X;
            String nn = ((Fname) obj).name;
            if (((Fname) obj).indices != null) // заменяются символы только в первой строке indeces -- остальные для рисунков
            {
                if (objFname.indices.length > 0) {
                    Element[] Rind = objFname.indices[0];
                    if (Rind != null) {
                        for (int j = 0; j < Rind.length; j++) {
                            if (Rind[j] != null) {
                                replaceOfSymbols(Rind[j], expression);
                            }
                        }
                    }
                }
            }
            for (int j = expression.size() - 1; j >= 0; j--) {
                Element expfunc = expression.get(j);
                if (expfunc instanceof Fname) {
                    String nameExprJ = ((Fname) expfunc).name;
                    if (FUtils.equalsIgnoreBlanks(nn, nameExprJ)) {
                        ((Fname) obj).X = ((Fname) expfunc).X;
                        if (((Fname) obj).X == null)  return;
                        if (((Fname) obj).X.length == 1) {
                            replaceOfSymbols(((Fname) obj).X[0], expression);
                        } else {
                            replaceOfSymbols(obj, expression);
                        }
                        return;
                    }
                }
            }
            return;
        }
        for (int i = 0; i < len; i++) {
            replaceOfSymbols(X[i], expression);
        }
    }

    /**
     * Get the position or -1: Search Position (not position - but THE Element) in the list nonCommutWithIndices
     * for the name "name"
     *
     * @param name имя с индексом, которое обозначает компоненту матрицы или
     * вектора
     *
     * @return ее номер в списке или -1, если в списке нет 
     */
    public Element searchInMatricesElements(String name) {
        return searchInMatricesElements(name, nonCommutWithIndices);
    }
    public static Element searchInMatricesElements(String name, List<Fname> nonCommutWithInd) {
        for (int i = 0; i < nonCommutWithInd.size(); i++) {
             Fname t=nonCommutWithInd.get(i);
            if (name.equals(t.name)) {
                if(t.X==null)return null;
                Element pos = nonCommutWithInd.get(i).X[0] ;
                return pos;
            }
        }
        return null;
    }
       /**
     * Нечто для работы с именами с подчеркиванием?? но я бы возвращал -1 или
     * найденную позицию, чтобы дважды не искать....(ГМ)
     *
     * @param n
     *
     * @return
     */
//    public boolean contentNameInNCIE(String n) {
//        for (Fname nonCommutWithIndice : nonCommutWithIndices) {
//            if (n.equals(nonCommutWithIndice.name)) {
//                return true;
//            }
//        }
//        return false;
//    }
    public boolean contentNameInNCIE(String n) {Element pos=searchInMatricesElements(n);
      return (pos==null)? false: true;
    }
     public static boolean contentNameInNCIE(String n, List<Fname> nonCommutWithInd) {
         Element pos=searchInMatricesElements(n,nonCommutWithInd);
      return (pos==null)? false: true;
    }
    
    public Object valueOf(Page page, int objPosition, Element[] values, Ring ring) {
        Object result = null;
        Element obj = page.expr.get(objPosition);
        if (obj == null) {
            return null;
        }
        if (obj instanceof F) {
            Element X1;
            if (((F) obj).X.length == 1) {
                X1 = obj;
            } else {
                X1 = ((F) obj).X[1];
            }
            if (X1 instanceof F) {
                return ((F) X1).valueOf(values, ring);
            }
            return ((Polynom) X1).value(values, ring);
        }
        return result;
    }

    public String strToTexStr(String str) {
        return strToTexStr(str, false).replace("$", "").replace('\n', ' ');
    }

    /**
     * Процедура предназначена для подготовки входного текста для компилятора
     * latex-2e. Реализуется три прогона текста: 1) Слова, начинающиеся с '\'
     * кроме стандартных ТеХ-слов, используемых в текущей версии ParCA,
     * окружаются \mathbf{}. 2) Обозначение конечных колец _ and ^
     * конвертируется в Z_{p} (p - целое число). 3) Строки, кроме начинающихся с
     * '%',окружаются матмодой и \par\noindent, матрицы записываются в ТеХ-фоме:
     * \left(\begin{array}{c..c} a1 & a2 & a3 .. // a4 & a5 & a6 ..
     * \end{array}\right)
     *
     * @param str -- readSingleOperator String
     * @param addLatexPreamble -- true if you want to get "head" &
     * "\end{document}
     *
     * @return -- output String
     */
    public String strToTexStr(String str, boolean addLatexPreamble) {
        // Standard TeX words used wich will not be surrounded with \mathbf{}.
        String words[] = {"times", "star", "ast", "sum", "prod", "int", "in",
            "over", "infty", "hbox", "sqrt",
            "le", "ge", "ne", "&", "lor", "neg",
            "alpha", "beta", "gamma", "Gamma", "delta", "Delta", "varepsilon", "epsilon", "zeta", "eta",
            "theta", "iota", "varkappa", "kappa", "lambda", "mu", "nu", "xi", "omicron", "pi", "rho", "sigma", "tau",
            "upsilon", "phi", "varphi", "chi", "psi", "omega", "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa",
            "Lambda", "Mu", "Nu", "Xi", "Omicron", "Pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi",
            "Psi", "Omega", "partial", "circ", "emptyset", "cup", "cap", "setminus", "to", "bf", "it", "nabla",
            "approx", "angle", "parallel", "in", "notin", "owns", "equiv", "sim", "smile",
            "neg", "triangle", "square", "exists", "nexists", "forall", "subset", "subseteq", "supset", "supseteq",
            "vee", "wedge", "oplus", "otimes", "perp", "blacksquare", "hbar", "overbrace", "underbrace", "frac", "bar", "hat", "tilde", "vec", "dot", "ddot", "overline", "overrightarrow", "widehat", "widetilde"
        };
        if (str.length() == 0) {
            return str;
        }
        String ST = str;
        StringBuilder NT = new StringBuilder();

        /* 1 ----------------------------------------------------------------------------*/
        int pbs = ST.indexOf('\\'); // Pointer to backslash in ST//
        int pb = 0;
        while (pbs != -1) {
            int pw = pbs + 1;   // pw - Pointer to next letter in ST//
            b:
            {
                while (pw < ST.length()) {
                    char w = ST.charAt(pw);
                    if ((w < 'a' || w > 'z') && (w < 'A' || w > 'Z') && (w != '!') && (w != '&')) {
                        break b;
                    }
                    pw++;
                }
            }
            String word = ST.substring(pbs + 1, pw); // служебное слово из букв и восклицательного знака
            a:
            {
                for (int j = 0; j < words.length; j++) {
                    if (word.equals(words[j])) {
                        break a;
                    }
                }
                if (word.equals("binom")) {
                    NT.append(ST.substring(pb, pbs));
                    int pbs1 = ST.indexOf('(', pbs + 1);
                    int para = FUtils.posOfPairBracket(ST, pbs1, '(', ')');
                    String dd = ST.substring(pbs1 + 1, para);
                    dd = dd.replace(",", "}{");
                    NT.append("\\binom{").append(dd).append("}");
                    pb = para + 1;
                    pbs = para;
                    break a;
                }
                if (word.equals("log")) {
                    int pbsU = ST.indexOf("_{", pbs + 1);
                    if ((pbsU == -1) || (pbsU - pbs - 1 > 5)) {
                        NT.append(ST.substring(pb, pbs));
                        int pbs1 = ST.indexOf('(', pbs + 1);
                        int para = FUtils.posOfPairBracket(ST, pbs1, '(', ')');
                        String dd = ST.substring(pbs1 + 1, para);
                        dd = dd.replace(",", "}(");
                        NT.append("\\log_{").append(dd).append(")");
                        pb = para + 1;
                        pbs = para;
                        break a;
                    }
                }

                if (word.equals("rootOf")) {
                    NT.append(ST.substring(pb, pbs));
                    int pbs1 = ST.indexOf('(', pbs + 1);
                    int para = FUtils.posOfPairBracket(ST, pbs1, '(', ')');
                    String dd = ST.substring(pbs1 + 1, para);
                    int iind = dd.indexOf(',');
                    int pointP = dd.indexOf('.', iind);
                    String ppow = (pointP == -1) ? dd.substring(iind + 1) : dd.substring(iind + 1, pointP - 1);
                    NT.append("\\sqrt[").append(ppow)
                            .append("]{").append(dd.substring(0, iind)).append("}");
                    pb = para + 1;
                    pbs = para;
                    break a;
                }
                if (word.equals("degreeC")) {
                    NT.append(ST.substring(pb, pbs));
                    NT.append("\\unicode{xB0}C");
                    pbs += 7;
                    pb = pbs + 1;
                    break a;
                }
                if (word.equals("degreeK")) {
                    NT.append(ST.substring(pb, pbs));
                    NT.append("\\unicode{xB0}K");
                    pbs += 7;
                    pb = pbs + 1;
                    break a;
                }
                if (word.equals("Beta")) {
                    NT.append(ST.substring(pb, pbs));
                    NT.append("\\unicode{x0392}");
                    pbs += 4;
                    pb = pbs + 1;
                    break a;
                }
                if ((word.equals("d")) || (word.equals("D"))) {
                    NT.append(ST.substring(pb, pbs));  
                    int tempPbs=ST.indexOf('(', pbs + 1); 
                    if(tempPbs==-1){ pb=pbs+2; NT.append(ST.substring( pbs,pb ));pbs++; break a;}
                    pbs = tempPbs; 
                    int posUnder = ST.lastIndexOf('_', pbs); if (posUnder<pb)posUnder=-1;
                    String ind= (posUnder==-1)?"": ST.substring(posUnder, pbs);
                    int para = FUtils.posOfPairBracket(ST, pbs, '(', ')');
                    String dd = ST.substring(pbs + 1, para);
                    NT.append((word.equals("D")) ? FUtils.toStringD_new(dd, ind,  ring)
                            : FUtils.toStringD(dd, ind, ring));
                    pb = para + 1;
                    pbs = para;
                    break a;
                }
                if (word.equals("ic")) {
                    NT.append(ST.substring(pb, pbs));
                    pbs = ST.indexOf('(', pbs + 1);
                    int para = FUtils.posOfPairBracket(ST, pbs, '(', ')');
                    String dd = ST.substring(pbs + 1, para);
                    NT.append(FUtils.toInitCond(dd,ring));
                    pb = para + 1;
                    pbs = para;
                    break a;
                }
                if (word.equals("elementOf")) {
                    String str1 = ST.substring(pb, pbs);
                    int elem = Math.max(str1.lastIndexOf(';'), str1.lastIndexOf('\n'));
                    String elemName;
                    if (elem == -1) {
                        elemName = str1;
                    } else {
                        elemName = str1.substring(elem + 1);
                        NT.append(str1.substring(0, elem + 1));
                    }
                    elemName = elemName.replace('=', ' ').trim();
                    int pbs1 = ST.indexOf('(', pbs + 1);
                    int para = FUtils.posOfPairBracket(ST, pbs1, '(', ')');
                    String dd = ST.substring(pbs1 + 1, para).trim();
                    Element MMATR=getElementByNameFromExpr(dd);
                    boolean Matrix = !(MMATR instanceof VectorS); //признак того, что имеем элементы матрицы
//                    int j = expr.size() - 1;
//                    for (; j >= 0; j--) {
//                        Element obj = expr.get(j);
//                        if ((obj instanceof Fname) && ((Fname) obj).name.equals(dd)) {
//                            Fname tmp = (Fname) obj;
//                            Matrix = (tmp.X != null) && ((tmp.X[0] instanceof MatrixS));
//                            break;
//                        }
//                    }// теперь Matrix==true, если матрица нашлась
                    String matInd = (Matrix) ? "_{i,j})" : "_{i})";
                    NT.append(dd).append("=(").append(elemName).append(matInd);
                    pb = para + 1;
                    pbs = para;
                    break a;
                }
                if (word.equals("fact") || word.equals("!")) {
                    NT.append(ST.substring(pb, pbs));
                    int pbs1 = ST.indexOf('(', pbs + 1);
                    int para = FUtils.posOfPairBracket(ST, pbs1, '(', ')');
                    String dd = ST.substring(pbs1 + 1, para);
                    dd = strToTexStr(dd);
                    NT.append(dd).append("!");
                    pb = para + 1;
                    pbs = para;
                    break a;
                }
                if (word.equals("exp")) {
                    NT.append(ST.substring(pb, pbs));
                    int pbs1 = ST.indexOf('(', pbs + 1);
                    int para = FUtils.posOfPairBracket(ST, pbs1, '(', ')');
                    String dd = ST.substring(pbs1 + 1, para);
                    dd = strToTexStr(dd);
                    NT.append("e^{").append(dd).append("}");
                    pb = para + 1;
                    pbs = para;
                    break a;
                }
                if (word.equals("cubrt")) {
                    NT.append(ST.substring(pb, pbs)).append("\\sqrt[3]");
                    pb = pw;
                    break a;
                }
                if (word.equals("series")) {
                    NT.append(ST.substring(pb, pbs)).append("\\sum_");
                    pb = pw;
                    break a;
                }
                if (word.equals("systLDE") || (word.equals("initCond")) || (word.equals("system")) ||(word.equals("equation"))|| (word.equals("systemOR"))) {
                    NT.append(ST.substring(pb, pbs)); // + "\\sum_"; pb = pw;break a;
                    pbs = ST.indexOf('(', pbs + 1);
                    int para = FUtils.posOfPairBracket(ST, pbs, '(', ')');
                    String dd = ST.substring(pbs + 1, para);
                    dd = strToTexStr(dd);
                    String[] SS = FUtils.cutByCommas(dd, ring);
                    String SSnew= (SS.length==1)? "\\begin{array}{rcl}":
                       (word.equals("systemOR"))? "\\left[\\begin{array}{rcl}":
                         "\\left\\{ \\begin{array}{rcl}";
                    for (int j = 0; j < SS.length; j++) {
                        String[] compSig = new String[] {"=", "<", ">", "\\ne", "\\ge", "\\le"};
                        int equ = -1; int i = 0; int shiftS = 1;
                        for (; i < 6; i++) {
                            equ = SS[j].lastIndexOf(compSig[i]);
                            if (equ != -1) {if (i > 2)shiftS = 3; break;}// для \ne,\le,\ge
                        }
                        if (equ != -1) {SSnew = SSnew + SS[j].substring(0, equ) + compSig[i]
                                    + SS[j].substring(equ + shiftS, SS[j].length());
                            if (j<SS.length-1) SSnew=SSnew+" \\\\ ";
                        }
                    }
                    NT.append(SSnew).append((SS.length==1)? " \\end{array}":" \\end{array}\\right.");
                    pb = para + 1;
                    pbs = pb;
                    break a;
                }
                NT.append(ST.substring(pb, pbs));
                char cccc = ST.charAt(pbs + 1); //слэш сос словом окружаем mathbf{}
                //если слово пустое, то проверяем на конструкцию \{ и сохраняем её
                if (word.equalsIgnoreCase("") && (cccc == '{' || cccc == '}' || cccc == ' ')) {
                    NT.append("\\").append(cccc);
                    pb = pw + 1;
                } else {
                    NT.append("\\mathbf{").append(word).append("}");
                    pb = pw;
                }
            }
            pbs = ST.indexOf('\\', pbs + 1);
        }
        NT.append(ST.substring(pb, ST.length())); // Have changed all words with backslash


        /* 2 -------------------------------------------------------------------------------*/
        StringBuilder T = new StringBuilder();    // Making curly brackets around _  index
        pbs = NT.indexOf("_");
        pb = 0;
        while (pbs != -1) {
            int pw = pbs + 1;
            c:
            {
                while (pw < NT.length()) {
                    char w = NT.charAt(pw);
                    if ((w < '0') || (w > '9')) {
                        break c;
                    }
                    pw++;
                }
            }
            if (pbs + 1 != pw) {
                String word = NT.substring(pbs + 1, pw);
                T.append(NT.substring(pb, pbs + 1)).append("{").append(word).append("}");
                pb = pw;
            }
            pbs = NT.indexOf("_", pbs + 1);
        }
        T.append(NT.substring(pb, NT.length()));
        NT = T;
        T = new StringBuilder();  // Making curly brackets around  ^ index
        pbs = NT.indexOf("^");
        pb = 0;
        while (pbs != -1) {
            int pw = pbs + 1;
            c:
            {
                while (pw < NT.length()) {
                    char w = NT.charAt(pw);
                    if ((w < '0') || (w > '9')) {
                        break c;
                    }
                    pw++;
                }
            }
            if (pbs + 1 != pw) {
                String word = NT.substring(pbs + 1, pw);
                T.append(NT.substring(pb, pbs + 1));
                if (pw - pbs == 2) {
                    T.append(word);
                } else {
                    T.append("{").append(word).append("}");
                }
                pb = pw;
            }
            pbs = NT.indexOf("^", pbs + 1);
        }
        T.append(NT.substring(pb, NT.length()));

        /* 3 -----------------------------------------------------------------------*/
        int i = 0, j = 0, l, m, k, k2, kk2 = 0, z, prev = 0, mm, mb;
        String N = "", M = "", MM, C;
        a:
        while (j != -1) {                                // Пересчет матрицы
            int iTmp = -1, jTmp = 0;
            while (jTmp != -1) {
                iTmp = T.indexOf("[", kk2);                  // первая открывающая скобка
                jTmp = (iTmp == -1) ? -1 : T.indexOf("[", iTmp + 1);    // вторая открывающая скобка

                if (jTmp != -1) {
                    kk2 = jTmp;
                    if (blankTeXString(T, iTmp, jTmp)) {
                        i = iTmp;
                        j = jTmp;
                    } else if (j > 0 && iTmp == j) {
                        break; // матрицы еще могут быть
                    }
                } else {
                    i = -1;
                    j = -1;
                    break a; // матриц нет
                }
            }
            kk2 = j;
            k = T.indexOf("]", j + 1);                    // первая закрывающая скобка
            if (k == -1) {
                break;                          // матриц нет
            }
            k2 = -1;                                      // вторая закрывающая скобка
            int kk1 = k;
            kk2 = k;
            while (kk2 != -1) {
                kk2 = T.indexOf("]", kk1 + 1);
                if (blankTeXString(T, kk1, kk2)) {
                    k2 = kk2;
                    break;
                } else {
                    kk1 = kk2;
                }
            } // нашли вторую закрывающую = k2, если к2 не равно -1
            if (k2 == -1) {
                if (kk2 == -1) {
                    kk2 = kk1;
                }
                continue;
            } // стартуем  с последней живой скобки
            String Matr = T.substring(i, k2 + 1); // забрали матрицу
            N += T.substring(prev, i);
            prev = k2 + 1; // переписали предыдущую часть
            Matr = Matr.replaceAll("\n", "");
            Matr = Matr.replaceAll("\t", "");  // очистили тело матрицы
            MM = " \\left(\\begin{array}{";
            m = Matr.indexOf("[", 1) + 1;  // позиция второй открывающей
            do {
                C = "c";        // C -- Number of columns in matrix   //
                k = Matr.indexOf("]", m);
                z = Matr.indexOf(",", m);
                int fOp = Matr.indexOf("{", m);
                int fCl = -1;
                if (fOp != -1) {
                    fCl = FUtils.posOfPairBracket(Matr, fOp, '{', '}');
                }
                while ((z < k) && (z != -1)) { // Position of ',' is left from ']'. //
                    if (!FUtils.isIntBetweenOf(z, fOp, fCl)) {
                        M += Matr.substring(m, z) + " & ";
                        m = z + 1;
                        z = Matr.indexOf(",", m);
                        C += "c";
                        if ((fCl != -1) && (fCl < z)) {
                            fOp = Matr.lastIndexOf("{", z);
                            if (fOp != -1) {
                                fCl = FUtils.posOfPairBracket(Matr, fOp, '{', '}');
                            }
                        }
                    } else {
                        M += Matr.substring(m, z) + ",";
                        m = z + 1;
                        z = Matr.indexOf(",", m);
                    }
                }
                M += Matr.substring(m, k);
                l = Matr.indexOf("]", k + 1);
                m = Matr.indexOf("[", k + 1) + 1;
                if ((l > m) && (m != 0)) {
                    M += " \\\\ ";
                }
            } while (m != 0); //  l=]; m=[+1;  конец одной строки
            MM += C + "}" + M + " \\end{array}\\right) ";
            M = "";
            //конец матрицы -- новая в MM
            N += MM;
        }  // все матрицы заменены
        N += T.substring(prev, T.length());
        // В N находятся все матрицы и все, что было   ------------------  ставим концы строк и прочее
        StringBuilder MMM = new StringBuilder();
        int kaw0 = 0;   // позиция кавычек младшая
        int kaw = N.indexOf('"'); // позиция кавычек старшая: (kaw>kaw0)
        boolean inText = (kaw == 0);   // признак того, что мы находимся в тексте (иначе мы в операторах)
        if (kaw == 0) {
            kaw0 = kaw + 1;
            kaw = N.indexOf('"', 1);
        }
        while (true) {
            String sss = (kaw == -1) ? N.substring(kaw0, N.length()) : N.substring(kaw0, kaw);
            if (inText) {
                MMM.append(sss.replace("\n", "\n\n "));
                if ((N.length()>kaw+1)&&(N.charAt(kaw + 1) == '\n')) {
                    MMM.append("\n\n");
                    kaw++;
                }
            } else {
                MMM.append("$ ").append(insertSlashsForAllOperator(
                        new StringBuilder(sss.replace("\n", " $\n\n$ "))));
                if (kaw > 0 && N.charAt(kaw - 1) == '\n') {
                    MMM.deleteCharAt(MMM.length() - 2);
                } else {
                    MMM.append(" $");
                }
            }
            if (kaw == -1) {
                break;
            }
            kaw0 = kaw + 1;
            kaw = N.indexOf('"', kaw0);
            inText = !inText;
        }
        /*  начнем с начала ---------------------------------------------------------------*/
        int ll = MMM.indexOf("^");
        while (ll != -1) {
            if (MMM.charAt(++ll) == '(') {
                MMM.setCharAt(ll, '{');
                int clouse = FUtils.posOfPareBracket(MMM, ll, '(', ')');
                MMM.setCharAt(clouse, '}');
            }
            ll = MMM.indexOf("^", ll + 1);
        }
        ll = MMM.indexOf("_");
        while (ll != -1) {
            if (MMM.charAt(++ll) == '(') {
                MMM.setCharAt(ll, '{');
                int clouse = FUtils.posOfPareBracket(MMM, ll, '(', ')');
                MMM.setCharAt(clouse, '}');
            }
            ll = MMM.indexOf("_", ll + 1);
        }

        return MMM.toString();
    }

    /**
     * Insert two slashes for all operator: {..} --> \{..\} and changing symbol:
     * * --> \cdot and saving the formate of string : changing k blank symbols
     * to k-1 backslash+blank, k>1.
     *
     * @param NN StringBuffer where we do the changes
     */
    private static StringBuilder insertSlashsForAllOperator(StringBuilder NN) {
        insertSlashsForOperator("for", NN);
        insertSlashsForOperator("if", NN);
        insertSlashsForOperator("else", NN);
        insertSlashsForOperator("while", NN);
        insertSlashsForOperator("procedure", NN);
        insertSlashsForOperator("return", NN);
        int ll = NN.indexOf("*");
        while (ll != -1) {
            NN.deleteCharAt(ll).insert(ll, "\\cdot ");
            ll = NN.indexOf("*", ll);
        }
        NN = addTexBlanks(NN);
        return NN;
    }

    /**
     * Insert two slashes for operator: {..} --> \{..\}
     *
     * @param operName = if, for, while, else procedure
     * @param NN StringBuffer where we do the changes
     */
    private static void insertSlashsForOperator(String operName, StringBuilder NN) {
        int ll = NN.indexOf(operName);
        while (ll != -1) {
            if (checkOperatorName(operName, NN, ll)) {
                if (operName.equals("return")) {
                    NN.insert(ll + 7, "\\  ");
                } else {
                    if (operName.equals("procedure")) {
                        NN.insert(ll + 10, "\\  ");
                    }
                    if (!operName.equals("else")) { // не будем обходить круглые скобки
                        ll = NN.indexOf("(", ll + 1);
                        ll = FUtils.posOfPareBracket(NN, ll, '(', ')');
                    } else {
                        NN.insert(ll + 5, "\\ ");
                    }
                    ll = NN.indexOf("{", ll + 1);
                    NN.insert(ll, "\\");
                    int ll1 = FUtils.posOfPareBracket(NN, ll + 2, '{', '}');
                    NN.insert(ll1, "\\");
                }
                ll = NN.indexOf(operName, ll + 1);
            } else {
                ll = -1;
            }
        }
    }

    /**
     *
     * @param operName it may be: if, else, for, while, procedure,
     * @param NN StringBuilder TEXT
     * @param pos the position of this Name in TEXT
     *
     * @return true -- if Name of operator false -- if not Name of operator
     */
    private static boolean checkOperatorName(String operName, StringBuilder NN, int pos) {
        if (pos > 0) {
            char ch = NN.charAt(pos - 1);
            if ((ch <= 'z') && (ch >= 'a')) {
                return false;
            }
        }
        pos = operName.length() + pos;
        if (pos < NN.length()) {
            char ch = NN.charAt(pos);
            if ((ch <= 'z') && (ch >= 'a')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Метод возвращает индекс закрывающей скобки в строке
     *
     * @param text -- исходная строка
     * @param beg -- начальный индекс поиска
     * @param open -- символ открывающей скобки
     * @param close -- символ закрывающей скобки
     *
     * @return -- индекс закрыващей скобки в строке
     */
    public int endOfOperator(String text, int beg, char open, char close) {
        StringBuilder str = new StringBuilder(text);
        if (text.indexOf(open, beg) > text.indexOf(close, beg)) {
            return -1;
        } else {
            return FUtils.posOfPareBracket(str, text.indexOf(open, beg), open, close);
        }
    }

    /**
     * Add the symbol "\n" after each symbol ";", excluding string fragments
     * between double quotes (").
     *
     * @param str -- input String
     *
     * @return obtained text in the form of StringBuffer
     */
    public static StringBuffer addSlashN(String str) {
        StringBuffer ss = new StringBuffer(str);
        int i0;     // begin \"
        int i1 = -1;     // end of \"
        int i = ss.indexOf(";");
        while ((i != -1) && (i < ss.length() - 1)) {
            i0 = ss.indexOf("\"", i1 + 1);         // begin \"
            i1 = ss.indexOf("\"", i0 + 1);         // end of \"
            if (i0 == -1) {
                i0 = ss.length();
            }
            if (i1 == -1) {
                i1 = ss.length();
            }
            while ((i != -1) && (i < i0 - 1)) {
                if (ss.charAt(i + 1) != '\n') {
                    ss = ss.insert(++i, '\n');
                    i0++;
                    i1++;
                }
                i = ss.indexOf(";", i + 1);
            }
            if (i == i0 - 1) {
                ss = ss.insert(++i, '\n');
            }
            while ((i != -1) && (i < i1)) {
                i = ss.indexOf(";", i + 1);
            }
        }
        return (ss);
    }

    /**
     * @param s StringBuffer
     * @param beg - index before the first symbol
     * @param end- index after the last symbol
     *
     * @return true if the substring is blank, else - otherwise
     */
    public static boolean blankString(String s, int beg, int end) {
        int i = beg + 1;
        while (i < end) {
            char sh = s.charAt(i++);
            if (sh != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * @param s StringBuffer
     * @param beg - index before the first symbol
     * @param end- index after the last symbol
     *
     * @return true if the substring is blank, else - otherwise
     */
    public static boolean blankTeXString(StringBuilder s, int beg, int end) {
        int i = beg + 1;
        while (i < end) {
            char sh = s.charAt(i++);
            if ((sh != ' ') && (sh != '\t') && (sh != '\n')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Deleting from the Input String all the tracks, which marked by "..".
     * These tracks are comments in Mathpar. If the substring TASK is in the
     * first comment then we do CLEAN this page: clean expr and
     * nonCommutWithIndices. The SPACE may be corrected: if the symbol following
     * the word TASK is equal Q,R,D,C, or Z (like: TASK-Q, or TASK-C) the ring
     * may be changed to the new one. Also for q,r,d,c take the ring to
     * Q,R,R64,C and take the corner measure to degree (not to radian). The
     * number at the end give the new FLOATPOS: Example: TASK-r20 - gives ring
     * R+ degree + FLOATPOS=20
     *
     *
     * @param ss -- the whole text of one section(window)
     *
     * @return -- the string without comments
     */
    private String deleteComments(String ss) {
        int kaw0 = ss.indexOf('"');
        if (kaw0 == -1) {
            return ss;
        }
        String res = ss.substring(0, kaw0);
        int kaw = ss.indexOf('"', kaw0 + 1);
        int pTASK = ss.lastIndexOf("TASK", kaw);
        if (pTASK >= 0) {
            expr.clear();
            ring.CForm.vectEl[Ring.Fname]=new Vector<Element>();
            nonCommutWithIndices.clear();
            ring.CForm.vectF[0]=new Vector<F>();
            char minusSign = ss.charAt(pTASK + 4);
            if ((minusSign != '-') && (Ring.R64 != ring.algebra[0])) {
                setRing(Ring.getRingXYZTofType(Ring.R64));
                // ring = Ring.getRingXYZTofType(Ring.R64);
            } else // for Q,R,D,Z,- radian, for q,r,d,z - degree
            if (pTASK + 5 < ss.length()) {
                char r = ss.charAt(pTASK + 5);
                Element radian = Element.TRUE; // by defoult we set radian
                int ringType = Ring.R64;;
                switch (r) {
                    case 'Q': ringType = Ring.Q; break;
                    case 'R': ringType = Ring.R; break;
                    case 'Z': ringType = Ring.Z; break;
                    case 'q': ringType = Ring.Q; radian = Element.FALSE; break;
                    case 'r': ringType = Ring.R; radian = Element.FALSE; break;
                    case 'z': ringType = Ring.Z; radian = Element.FALSE; break;
                    case 'd': radian = Element.FALSE; 
                    case 'D': 
                     defoult: ringType = Ring.R64;
                }
                if (ringType != ring.algebra[0]) {
                    Ring ring1 = (ringType == Ring.R64)
                            ? Ring.getRingXYZTofType(ringType) : Ring.getRingXYofType(ringType);
                    setRing(ring1);
                }
                ring.setRADIAN(radian);
                // ------------------------------ new value for FLOATPOS
                if (pTASK + 6 < ss.length()) {
                    r = ss.charAt(pTASK + 6);
                    if (r != ' ') {
                        int posBlank = ss.indexOf(' ', pTASK);
                        if (posBlank == -1) {
                            posBlank = ss.indexOf('\"', pTASK);
                        }
                        if (posBlank == -1) {
                            posBlank = ss.indexOf('\n', pTASK);
                        }
                        if ((posBlank - pTASK - 6 < 9) && (posBlank > pTASK + 6)) {
                            String newDigits = ss.substring(pTASK + 6, posBlank);
                            int nd = 0;
                            try {
                                nd = Integer.valueOf(newDigits);
                            } catch (Exception ex) {
                                nd = -777;
                                ring.exception.append("\nError in TASK-SPACE declaration: value FLOATPOS not correct");
                            }
                            if ((nd > -777) && (nd <= 1000)) {
                                ring.setFLOATPOS(nd);
                            }
                        } // new value for FLOATPOS
                    }
                }
            }
        }//End of case: pTASK>0
        boolean inText = true; // flag: we in the comments
        while (kaw != -1) {
            res += (inText) ? ";" : ss.substring(kaw0, kaw);
            inText = !inText;
            kaw0 = kaw + 1;
            kaw = ss.indexOf('"', kaw0);
        }
        if (!inText) {
            res += ss.substring(kaw0, ss.length());
        }
        return res;
    }

    /**
     * Add blanks for texing: each k blanks (k>1) we change to k-1
     * backslash+blank
     *
     * @param nn
     *
     * @return
     */
    public static StringBuilder addTexBlanks(StringBuilder nn) {
        int ll = nn.indexOf("  ");
        if (ll == -1) {
            return nn;
        }
        while (ll != -1) {
            ll++;
            nn.insert(ll, '\\');
            ll = (ll < nn.length()) ? nn.indexOf("  ", ll + 1) : -1;
        }
        return nn;
    }

    public void showExpr() {
        LOG.debug("expr==== ");
        for (int i = 0; i < expr.size(); i++) {
            Element el = expr.get(i);
            if (el instanceof Fname) {
                LOG.debug("{} = {},  ", ((Fname) el).name, el.toString(ring));
            } else {
                LOG.debug("{},  ", el);
            }
        }
    }

    public void insertObj(String name, Element ob) {
        insertObj(name, ob, expr);
    }

    /**
     * Вставка вычисленного выражения в список выражений на текущей странице:
     * если имя уже встречалось, то его значение просто замещается. Если имя не
     * встречалось или имя отсутствует, то добавляется в конец списка. Если в
     * конце списка было выражение без имени, то оно удаляется.
     *
     * @param name String имя объекта
     * @param ob Element его значение
     * @param expression текущий список expr
     */
    public void insertObj(String name, Element ob, List<Element> expression) {
        posLast = expression.size() - 1;
        int j = posLast;
        if (!name.equals("")) {
            for (; j >= 0; j--) {
                Element obj = expression.get(j);
                if ((obj instanceof Fname) && ((Fname) obj).name.equals(name)) {
                    Fname fn = ((Fname) obj);
                    if (fn.X == null) {
                        fn.X = new Element[1];
                    }
                    fn.X[0] = ob;
                    posLast = j;
                    return;
                }
            }
        }
        Element toWrite = (name.equals("")) ? ob : (new Fname(name, ob));
        if ((posLast == -1) || (expression.get(posLast) instanceof Fname)) {
            expression.add(toWrite);
            posLast = expression.size() - 1;
        } else {
            expression.set(posLast, toWrite);
        }
    }

    /**
     * метод, сохраняющий все формулы и записывающий структуру программы ГОТОВИМ
     * ВСЕ ДЛЯ ПРОЦЕДУРЫ GO
     * <p/>
     * В data.funcs сохраняется список имен выражений у которых подвешены
     * вводимые выражения в порядке появления в языке mathpar.
     * <p/>
     * В программе "proc" сохраняется номер этого имени в списке data.funcs и
     * номер выражения в его аргументах. Всего на один оператор присваивания
     * записывается два целых числа в программу proc.
     * <p/>
     * 
     * (Кроме того формируем корректный список "nonCommutWithIndices" объектов,
     * которые являются элемантами матриц и векторов - НЕТ - перенесли это в RarselImpl)
     *
     * @param name имя, сохраняемого оператора
     * @param ob правая часть, сохраняемого оператора
     * @param proc текущая процедура
     */
    public void saveFuncs(String name, Element ob, IntList proc) {
        int pos = data.funcs.size() - 1;
        int j = pos;
        if (!name.equals("")) {
            for (; j >= 0; j--) {// ищем имя в data.funcs, которое совпадет с входным name
                // в его список X дописываем ob. В конец программы Proc дописываем номер этой записи
                // в data.funcs и номер по которому стоит ob в его Х.
                Element obj = data.funcs.get(j);
                if ((obj instanceof Fname) && ((Fname) obj).name.equals(name)) {
                    Element[] bf = new Element[((Fname) obj).X.length + 1];
                    System.arraycopy(((Fname) obj).X, 0, bf, 0, ((Fname) obj).X.length);
                    bf[bf.length - 1] = ob;
                    Fname Fn = new Fname(((Fname) obj).name, bf, 1);
                    data.funcs.set(j, Fn);
                    proc.add(j);
                    proc.add(bf.length - 1);
                    return;
                }
            }
           }
        boolean isNew = (pos == -1) || (data.funcs.get(pos) instanceof Fname);
        if (isNew) {
            pos = data.funcs.size();}
            if ((ob instanceof F) && (((F) ob).name == F.ELEMENTOF)) {
                Element posMatrEl = searchInMatricesElements(name);
                if (posMatrEl == null) {
                    nonCommutWithIndices.add(new Fname(name));
                }
            }
      //  }  // когда мы сделаем add, то pos
        //станет точно адресом записи, так как size увеличится на 1;
        if (name.equals("")) {
            if (isNew) {
                data.funcs.add(ob);             // сохраняем правую часть
            } else {
                data.funcs.set(pos, ob);
            }       // затираем неименнованное выражение в конце
        } else {
            if (isNew) {
                data.funcs.add(new Fname(name, ob)); // в Fname сохраняем имя и правую часть
            } else {
                data.funcs.set(pos, new Fname(name, ob));
            }  // затираем неименнованное выражение в конце
        }
        proc.add(pos);   // адрес пишем в программу.
        proc.add(0);     // 0 пишем в программу, потому что было добавлено новое выражение, его индекс, как аргумента =0.
    }         
//        }
//        boolean isNew = (pos == -1) || (data.funcs.get(pos) instanceof Fname);
//        Element toWrite=(name.equals(""))? ob: new Fname(name, ob);
//        if (isNew) {
//            pos = data.funcs.size();
//            if ((ob instanceof F) && (((F) ob).name == F.ELEMENTOF)) { //-> в ParserImpl
//                int posMatrEl = searchPosInMatricesElements(name);
//                if (posMatrEl == -1)  nonCommutWithIndices.add( (Fname)toWrite);
//                else nonCommutWithIndices.set(posMatrEl, (Fname)toWrite);
//            }
//        }  // когда мы сделаем add, то pos
//        //станет точно адресом записи, так как size увеличится на 1;
//            if (isNew) data.funcs.add(toWrite);             // сохраняем правую часть
//            else {data.funcs.set(pos, toWrite);}           // затираем неименнованное выражение в конце
//        proc.add(pos);   // адрес пишем в программу.
//        proc.add(0);     // 0 пишем в программу, потому что было добавлено новое выражение, его индекс, как аргумента =0.
//    }

    /**
     * Ввод одного оператора до точки с запятой. Отделение Имени по знаку равно
     * в конце вызывает saveFuncs
     *
     * @param expres String ОДИН ВХОДНОЙ ОПЕРАТОР ДО ТОЧКИ С ЗАПЯТОЙ
     * @param proc ПРОГРАММА
     *
     * @throws Exception
     */
    public void readSingleOperator(String expres, IntList proc) {
        String objName /* имя выражения (левая часть) */, rhsExpr /* выражение (правая часть) */;
        int eqPos = expres.indexOf('=');
        int eq2Pos = expres.indexOf("=="); // Исключаем знак == и \hbox
        while (eqPos != -1 && eqPos == eq2Pos) {
            eqPos = expres.indexOf('=', eq2Pos + 2);
            eq2Pos = expres.indexOf("==", eq2Pos + 2);
        }
        int hboxPos = expres.indexOf("\\hbox");
        while (hboxPos != -1 && hboxPos < eqPos) {
            int hbox1 = expres.indexOf('{', hboxPos);
            int hbox2 = FUtils.posOfPairBracket(expres, hbox1, '{', '}');
            if ((eqPos > hbox1) && (hbox2 > eqPos)) {
                eqPos = expres.indexOf('=', hbox2);
                eq2Pos = expres.indexOf("==", hbox2); // Исключаем знак == и \hbox
                while (eqPos != -1 && eqPos == eq2Pos) {
                    eqPos = expres.indexOf('=', eq2Pos + 2);
                    eq2Pos = expres.indexOf("==", eq2Pos + 2);
                }
                hboxPos = expres.indexOf("\\hbox", hbox2);
            }
        } //  position of the sign "=" is out of the hbox.
//        hboxPos = expres.indexOf("\\system");
//        if((hboxPos!=-1)&&(hboxPos<eqPos)){
//            int hbox1 = expres.indexOf('(', hboxPos);
//            int hbox2 = FUtils.posOfPairBracket(expres, hbox1, '(', ')');
//            eqPos = expres.indexOf('=', hbox2);
//        } // system was excluded
  
        if (eqPos == -1) {
            objName = "$NONAME$";
            rhsExpr = expres.trim();
        } else {
            objName = expres.substring(0, eqPos).trim();
            rhsExpr = expres.substring(eqPos + 1).trim();
            if (objName.matches("\\\\.*[({].*")) {// отлавливаем ситуации, когда нет имени, но есть "="
                objName = "$NONAME$";
                rhsExpr = expres.trim();
            }
        }
        // ------------------------- готово! - разрезали --
        // проверим имя на корректность, сначала ловим служебные слова ------
        Element fOut = null;  // Здесь будет объект после обработки парсером правой части
        if (objName.equals("SPACE")) {
            try {
                setRing(rhsExpr);
                fOut = new Fname(rhsExpr);
            } catch (Exception ex) {
                ring.exception.append("\nError in space declaration");
                throw ex;
            }
        } else if (objName.equals("FLOATPOS")) {
            try {
                fOut = new NumberZ64(rhsExpr);
                ring.setFLOATPOS(Integer.parseInt(rhsExpr));
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in FLOATPOS number");
                throw ex;
            }
        } else if (objName.equals("MOD")) {
            try {
                NumberZ newMOD = new NumberZ(rhsExpr);
                fOut = newMOD;
                ring.setMOD(newMOD);
                ring.CForm.newRing.MOD=ring.MOD;
            } catch (Exception ex) {
                ring.exception.append("\nError in MOD number");
                throw ex;
            }
        } else if (objName.equals("SMALLESTBLOCK")) {// SMALLEST MATRIX BLOCK for recursive matrix multiplication
            try {
                NumberZ64 blockSize = new NumberZ64(rhsExpr);
                fOut = blockSize;
                ring.setSmallestBlock((int) blockSize.value);
            } catch (Exception ex) {
                ring.exception.append("\nError in SMALLESTBLOCK size number");
                throw ex;
            }
        } else if (objName.equals("MOD32")) {
            try {  
                fOut = new NumberZ64(rhsExpr);
                ring.setMOD32(Long.parseLong(rhsExpr));
                ring.CForm.newRing.MOD32=ring.MOD32;
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in MOD32 number");
                throw ex;
            }
        } else if (objName.equals("MachineEpsilonR")) {
            try {
                int e, a;
                int divP = rhsExpr.indexOf('/');
                if (divP == -1) {
                    e = Integer.parseInt(rhsExpr);
                    a = e + 5;
                } else {
                    e = Integer.parseInt(rhsExpr.substring(0, divP));
                    a = Integer.parseInt(rhsExpr.substring(divP + 1));
                }
                if (!(a > e)) {
                    a = e + 1;
                    ring.exception.append("\nNotification: Accuracy can't be less then MachineEpsilonR. It is set = MachineEpsilonR+1.");
                }
                fOut = new Fraction(new NumberZ64(e), new NumberZ64(a));
                ring.setMachineEpsilonR(e);
                ring.setAccuracy(a);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in MachineEpsilonR can be <int> or <int>/<int>");
                throw ex;
            }
        } else if (objName.equals("MachineEpsilonR64")) {
            try {
                fOut = new NumberZ64(rhsExpr);
                int v = fOut.intValue();
                if ((v > 2013) || (v < 1)) {
                    v = 36;
                    ring.exception.append("\nNotification: We put 36; you can insert 0<value<1023; MachineEpsilonR64= 2^(-value).");
                }
                ring.setMachineEpsilonR64(v);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in MachineEpsilonR64");
                throw ex;
            }
        } else if (objName.equals("RADIAN")) {
            try {
                fOut = new NumberZ64(rhsExpr);
                ring.setRADIAN(fOut);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in RADIAN definition");
                throw ex;
            }
        } else if (objName.equals("STEPBYSTEP")) {
            try {
                fOut = new NumberZ64(rhsExpr);
                ring.setSTEPBYSTEP(fOut);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in STEPBYSTEP definition: 1 or 0");
                throw ex;
            }
        } else if (objName.equals("SUBSTITUTION")) {
            try {
                fOut = new NumberZ64(rhsExpr);
                ring.setSUBSTITUTION(fOut);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in SUBSTITUTION definition");
                throw ex;
            }
        } else if (objName.equals("EXPAND")) {
            try {
                fOut = new NumberZ64(rhsExpr);
                ring.setEXPAND(fOut);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in EXPAND definition");
                throw ex;
            }
        } else if (objName.equals("\\TOTALNODES")) {
            try {
                fOut = new NumberZ64(rhsExpr);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in TOTALNODES definition");
                throw ex;
            }
        } else if (objName.equals("\\PROCPERNODE")) {
            try {
                fOut = new NumberZ64(rhsExpr);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in PROCPERNODE definition");
                throw ex;
            }
        } else if (objName.equals("\\MAXCLUSTERMEMORY")) {
            try {
                fOut = new NumberZ64(rhsExpr);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in MAXCLUSTERMEMORY definition");
                throw ex;
            }
        } else if (objName.equals("\\CLUSTERTIME")) {
            try {
                fOut = new NumberZ64(rhsExpr);
            } catch (NumberFormatException ex) {
                ring.exception.append("\nError in CLUSTERTIME definition");
                throw ex;
            }
        } else if (objName.equals("TIMEOUT")) {
            try {
                fOut = new NumberZ64(rhsExpr);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("Wrong TIMEOUT format");
            }
        } else {
            rhsExpr = tryLoadFromFileInRhs(rhsExpr); // replace rhs expression with loaded from file (if any).
            int severEq = FUtils.posOfLastFreeEq(rhsExpr);
            if (severEq < -1) { ring.exception.append("Syntax error. Bracket (  " 
                    +((severEq==-2)?'{':(severEq==-3)?'(':'[')  + " not closed ");}
            if ((severEq > 0)&&(rhsExpr.charAt(severEq-1)!='=')) {
                rhsExpr = rhsExpr.substring(severEq + 1);// разрешаем иметь несколько знаков равно!
            }
            F left = Parser.getF(objName, ring);  
            if (left.name == F.ID && left.X[0] instanceof Fname) { 
                fOut = Parser.getF(rhsExpr, ring, data.procNames, this.nonCommutWithIndices );
            } else {
                pol_case: // разрешаем иметь "имена" в левой части, которые совпадают с переменными из кольца
                {
                    if (left.name == F.ID && left.X[0] instanceof Polynom) {
                        Polynom pp = (Polynom) left.X[0];
                        if ((pp.coeffs.length == 1) && (pp.coeffs[0].isOne(ring))) {
                            int ll = pp.powers.length;
                            if (ll > 0 && pp.powers[ll - 1] == 1) {
                                int mm = ll - 2;
                                while ((mm >= 0) && (pp.powers[mm] == 0)) {
                                    mm--;
                                }
                                if (mm == -1) {
                                    objName = ring.varNames[ll - 1];
                                    fOut = Parser.getF(rhsExpr, ring, data.procNames, this.nonCommutWithIndices);
                                    break pol_case;
                                }
                            }
                        }
                    }// it was case for VARIABLE in the left part
                    fOut = Parser.getF(rhsExpr, ring, data.procNames, this.nonCommutWithIndices);
                    objName = "$NONAME$";
                } // polcase
            }
            Matcher m = VECT_MATRIX.matcher(rhsExpr);
            if (m.matches()) {
                F fout = (F) fOut;
                if (fout.name == F.ID && fout.X[0] instanceof Fname) {
                    String nname_ = ((Fname) fout.X[0]).name;
                    if (!contentNameInNCIE(nname_)) {
                        rhsExpr = replaceOfSymbolsInIndeces(rhsExpr, this.expr);
                        // Выполняем операции в индексах типа: a_{k+1}
                         fOut = Parser.getF(rhsExpr, ring, data.procNames, this.nonCommutWithIndices);
                      //  fOut = new F(F.ID, new Fname(rhsExpr));
                    }
                }
            }
        }
         saveFuncs(objName, fOut, proc);
    } //новое: разрешены имена совпадающие с именами переменных!! 7.11.2014 ГМ
 
    /**
     * Getting of Index of Element in page.Expr by Name of this expr.
     *
     * @param name -- name of this expr.
     *
     * @return -1 if absent such name OR index of a named expression
     */
    public int getIndexElementByNameFromExpr(String name, List<Element> expr1) {
        for (int i = expr1.size() - 1; i >= 0; i--) {
            if (expr1.get(i) instanceof Fname) {
                if (((Fname) expr1.get(i)).name.equals(name)) {
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * Getting of   Element in page.Expr by Name of this expr.
     *
     * @param name -- name of this expr.
     * @return -- this Fname.X[0] if it exists,
     *         null -- if absent such name OR index of a named expression
     */
    public Element getElementByNameFromExpr(String name ) {
        for (int i = expr.size() - 1; i >= 0; i--) {
            if (expr.get(i) instanceof Fname) {Fname el=((Fname) expr.get(i));
                if (el.name.equals(name)) {if((el.X!=null)&&(el.X[0]!=null))
                    return el.X[0];else return null;
                }
            }
        }
        return null;
    }

    /**
     * метод, исполняющий процедуры и функции, ыключая и главную
     *
     * @param procNum исполняемая процедура
     * @param expression текущий список expr
     *
     * @return результат выполнения программы или функции
     *
     * @throws Exception
     */
    private Element go(int procNum, List<Element> expression) {
        CanonicForms cfPage = ring.CForm;
        boolean printLastExpr = true;
        int currTransProg[] = data.transProg.get(procNum).toArray();
        Element f_out;
        int i = 0;
        while (i < currTransProg.length) {
            if (currTransProg[i] >= 0) {
                Fname fn = (Fname) data.funcs.get(currTransProg[i]);
                if (!trySetConstants(cfPage, fn, currTransProg, i)) {
                    cfPage.chek_name_in_gr_works(fn.name);
                     Element ElE= (((Fname) data.funcs.get(currTransProg[i])).X[currTransProg[i + 1]]);
                     System.out.println("ElE=========="+ElE.toString(ring));
                     F f = (F) ElE;
                    int f_name = 1500;
                    int xLen = 0;
                    Element[] XX = null;
                    if (f != null) {
                        XX = f.X;
                        f_name = f.name;
                        xLen = XX.length;
                    }
                    switch (f_name) {
                     case F.SET2D: { 
                             if((f.X[0] instanceof F) && (((F) expandFnameOrId(f.X[0])).name == F.VECTORS)){
                                set2D(expandFnameOrId(f));
                                break;
                            }else{
                            int len = f.X.length;
                            String sName = "";
                            option = "";
                            boolean f1 = false;
                            boolean f2 = false;
                            if (expandFnameOrId(f.X[len - 1]) instanceof F) {
                                if (((F) expandFnameOrId(f.X[len - 1])).name == F.VECTORS) {
                                    f1 = true;
                                    F fOpt = ((F) expandFnameOrId(f.X[len - 1]));
                                    optionsPlot = new double[fOpt.X.length];
                                    if (((Polynom) fOpt.X[0]).isZero(ring)) {
                                        optionsPlot[0] = 0;
                                    } else {
                                        optionsPlot[0] = ((Polynom) fOpt.X[0]).coeffs[0].value;
                                    }
                                    if (((Polynom) fOpt.X[1]).isZero(ring)) {
                                        optionsPlot[1] = 0;
                                    } else {
                                        optionsPlot[1] = ((Polynom) fOpt.X[1]).coeffs[0].value;
                                    }
                                    if (((Polynom) fOpt.X[2]).isZero(ring)) {
                                        optionsPlot[2] = 0;
                                    } else {
                                        optionsPlot[2] = ((Polynom) fOpt.X[2]).coeffs[0].value;
                                    }
                                    if (((Polynom) fOpt.X[3]).isZero(ring)) {
                                        optionsPlot[3] = 0;
                                    } else {
                                        optionsPlot[3] = ((Polynom) fOpt.X[3]).coeffs[0].value;
                                    }
                                    if (((Polynom) fOpt.X[4]).isZero(ring)) {
                                        optionsPlot[4] = 0;
                                    } else {
                                        optionsPlot[4] = ((Polynom) fOpt.X[4]).coeffs[0].value;
                                    }
                                } else {
                                    if (((F) expandFnameOrId(f.X[len - 1])).name == F.HBOX) {
                                        f2 = true;
                                        sName = ((Fname) ((F) ((F) expandFnameOrId(f.X[len - 1])).X[0]).X[0]).name;
                                    } else {
                                        if ((((F) expandFnameOrId(f.X[len - 1])).name != F.HBOX) && (((F) expandFnameOrId(f.X[len - 1])).name != F.VECTORS)) {
                                            optionsPlot = null;
                                            if (f.X.length == 7) {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[5]), expandFnameOrId(f.X[6]));
                                            }
                                            if (f.X.length == 5) {
                                                if (expandFnameOrId(f.X[2]).isItNumber()) {
                                                    set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, expandFnameOrId(f.X[4]));
                                                } else {
                                                    set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[2]));
                                                }
                                            }
                                            if (f.X.length == 4) {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, null);
                                            }
                                            if (f.X.length == 3) {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, expandFnameOrId(f.X[2]));
                                            }
                                            if (f.X.length == 2) {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, null);
                                            }
                                            if (f.X.length == 0) {
                                                set2D_old(null, null, null, null, null, null, null);
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (expandFnameOrId(f.X[len - 2]) instanceof F) {
                                    if (((F) expandFnameOrId(f.X[len - 2])).name == F.HBOX) {
                                        f2 = true;
                                        sName = ((Fname) ((F) ((F) expandFnameOrId(f.X[len - 2])).X[0]).X[0]).name;
                                    }
                                }
                                if (sName.equals("ES") || sName.equals("BW") || sName.equals("ESBW") || sName.equals("BWES")) {
                                    if (f1 && f2) {
                                        option = sName;
                                        if (f.X.length == 9) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[5]), expandFnameOrId(f.X[6]));
                                        }
                                        if (f.X.length == 7) {
                                            if (expandFnameOrId(f.X[2]).isItNumber()) {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, expandFnameOrId(f.X[4]));
                                            } else {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[2]));
                                            }
                                        }
                                        if (f.X.length == 6) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, null);
                                        }
                                        if (f.X.length == 5) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, expandFnameOrId(f.X[2]));
                                        }
                                        if (f.X.length == 4) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, null);
                                        }
                                    } else {
                                        option = sName;
                                        if (f.X.length == 8) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[5]), expandFnameOrId(f.X[6]));
                                        }
                                        if (f.X.length == 7) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[5]), null);
                                        }
                                        if (f.X.length == 6) {
                                            if (expandFnameOrId(f.X[2]).isItNumber()) {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, expandFnameOrId(f.X[4]));
                                            } else {
                                                set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[2]));
                                            }
                                        }
                                        if (f.X.length == 5) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, null);
                                        }
                                        if (f.X.length == 4) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, expandFnameOrId(f.X[2]));
                                        }
                                        if (f.X.length == 3) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, null);
                                        }
                                        if (f.X.length == 1) {
                                            set2D_old(null, null, null, null, null, null, null);
                                        }
                                    }
                                } else {
                                    if (f.X.length == 8) {
                                        set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[5]), expandFnameOrId(f.X[6]));
                                    }
                                    if (f.X.length == 7) {
                                        set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[5]), expandFnameOrId(f.X[6]));//null!!!!!!!!!!
                                    }
                                    if (f.X.length == 6) {
                                        if (expandFnameOrId(f.X[2]).isItNumber()) {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, expandFnameOrId(f.X[4]));
                                        } else {
                                            set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[2]));
                                        }
                                    }
                                    if (f.X.length == 5) {
                                        set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, null);
                                    }
                                    if (f.X.length == 4) {
                                        set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, expandFnameOrId(f.X[2]));
                                    }
                                    if (f.X.length == 3) {
                                        set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, null);
                                    }
                                    if (f.X.length == 1) {
                                        set2D_old(null, null, null, null, null, null, null);
                                    }
                                }
                            } else {
                                optionsPlot = null;
                                if (f.X.length == 7) {
                                    set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[5]), expandFnameOrId(f.X[6]));
                                }
                                if (f.X.length == 5) {
                                    if (expandFnameOrId(f.X[2]).isItNumber()) {
                                        set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, expandFnameOrId(f.X[4]));
                                    } else {
                                        set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, expandFnameOrId(f.X[3]), expandFnameOrId(f.X[4]), expandFnameOrId(f.X[2]));
                                    }
                                }
                                if (f.X.length == 4) {
                                    set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), expandFnameOrId(f.X[2]), expandFnameOrId(f.X[3]), null, null, null);
                                }
                                if (f.X.length == 3) {
                                    set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, expandFnameOrId(f.X[2]));
                                }
                                if (f.X.length == 2) {
                                    set2D_old(expandFnameOrId(f.X[0]), expandFnameOrId(f.X[1]), null, null, null, null, null);
                                }
                                if (f.X.length == 0) {
                                    set2D_old(null, null, null, null, null, null, null);
                                }
                            }}
                            break;
                        }
 // new Set2D up to this point 04062016  
                        case F.TOFILE:
                            toFile(f);
                            break;
                        case F.LS:
                            insertObj(fn.name, getUploadedFilesListing());
                            break;
                        case F.TABLEFROMFILE:
                            insertObj(fn.name, Table.fromFname(userUploadDir, expandFnameOrId(f.X[0])));
                            break;
                        case F.ELEMENTOF: //привязываем к нашему объекту через ссылку
                            Element[] pare=new Element[]{null,f.X[0]};
                            pare= expandFnameOrId2(pare);
                            if(pare[0]==null)  {cfPage.RING.exception.append(" Not correct operator <<\\elementOf()>>."); return null;}
                            Fname tempEOf= ((Fname)pare[0]);  tempEOf.X= new Element[]{pare[1]};
                          //  Element tempEOf = expandFnameOrId(f.X[0]);
                            System.out.println("tempEOf=="+tempEOf);
                            replaceOfSymbols(tempEOf, expression);
                            int posInExpr = getIndexElementByNameFromExpr(((Fname) tempEOf).name, expression);
                            insertObj(fn.name, f); //для красоты вывода
                            if (posInExpr == -1) { // он уже встречался, добавим в список nonCommutWithIndices с найденным номером
                                // он не встречался, добавим в список nonCommutWithIndices его и в expr пустую матрицу
                                    posInExpr = expr.size() - 1;
                                insertObj(((Fname) tempEOf).name, null);
                            }
                            int jN = nonCommutWithIndices.size() - 1;
                            while ((jN > 0) && (!nonCommutWithIndices.get(jN).name.equals(fn.name))) {
                                jN--;
                            }
                            if (jN != -1) {// указатель на текущий индекс в спимке expr из списка nonCommutWithIndices
                                nonCommutWithIndices.get(jN).X =new Element[]{expression.get(posInExpr)};
                            } else {
                                nonCommutWithIndices.add(new Fname(fn.name, expression.get(posInExpr)));
                             //   cfPage.RING.exception.append("in GO Problems withnonCommutWithIndices");
                            }
                            break;
                        case F.CLEAN: {
                            if (xLen == 0) {
                                expression.clear();
                                nonCommutWithIndices=new ArrayList<>();
                                ring.CForm.vectEl[Ring.Fname]=new Vector<Element>();
                                ring.CForm.vectF[0]=new Vector<F>();
                            } else {
                                for (int t = 0; t < xLen; t++) {
                                    while (XX[t] instanceof F) {
                                        XX[t] = ((F) XX[t]).X[0];
                                    }
                                    String nn = ((Fname) XX[t]).name;
                                    int j = expression.size() - 1;
                                    for (; j >= 0; j--) {
                                        Element obj = expression.get(j);
                                        if (obj instanceof Fname && ((Fname) obj).name.equals(nn)) {
                                            expression.remove(j);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case F.PRINTS: {
                            StringBuffer sb = data.section[1];
                            String ts = (((Fname) data.funcs.get(currTransProg[i])).X[currTransProg[i + 1]]).toString(ring);
                            ts = ts.substring(ts.indexOf("(") + 1, ts.length() - 1);
                            String[] StringNames = FUtils.cutByCommas(ts, ring);
                            for (int t = 0; t < StringNames.length; t++) {
                                String nn = StringNames[t];
                                String nnObj = "";
                                Matcher m = VECT_MATRIX.matcher(nn);
                                if (m.matches()) {
                                    F leftPart_ = Parser.getF(nn, ring, data.procNames, this.nonCommutWithIndices);
                                    replaceOfSymbols(leftPart_, expression);
                                    String nname_ = ((Fname) leftPart_.X[0]).name;
                                    if (contentNameInNCIE(nname_)) {
                                        Element posEL = searchInMatricesElements(nname_);
                                        if (posEL != null) {
                                            replaceOfSymbols(XX[t], expression);
                                            F leftPart__ = (XX[t] instanceof F) ? (F) XX[t] : new F(XX[t]);
                                            nnObj = cfPage.InputForm(leftPart__, this, expression).toString(ring);
                                        } else {
                                            ring.exception.append("Address to a non-existent object ").append(nn).append(";");
                                        }
                                    }
                                } else {
                                    int j = expression.size() - 1;
                                    for (; j >= 0; j--) {
                                        Element obj = expression.get(j);
                                        if ((obj instanceof Fname) && ((Fname) obj).name.
                                                equals(nn.trim())) {
                                            nnObj = (((Fname) obj).X == null)
                                                    ? obj.toString(ring)
                                                    : (((Fname) obj).X[0]).toString(ring);
                                            break;
                                        }
                                    }
                                }
                                if (nnObj.length() == 0) {
                                    sb.append(nn);
                                } else {
                                    sb.append(nnObj) ;
                                }
                            }
                            printLastExpr = false;
                            break;
                        }
                        case F.PRINT: {
                            StringBuffer sb = data.section[1];
                            String ts = (((Fname) data.funcs.get(currTransProg[i])).X[currTransProg[i + 1]]).
                                    toString(ring);
                            ts = ts.substring(ts.indexOf("(") + 1, ts.lastIndexOf(")"));
                            String[] StringNames = FUtils.cutByCommas(ts, ring);
                            for (int t = 0; t < StringNames.length; t++) {
                                sb.append("\n");// начали писать с перевода корретки
                                String nn = StringNames[t];
                                String nnObj = "";
                                Matcher m = VECT_MATRIX.matcher(nn);
                                if (m.matches()) {
                                    F leftPart_ = Parser.getF(nn, ring, data.procNames, this.nonCommutWithIndices);
                                    String feftPartStr = FUtils.deleteExternalBrackets(
                                            leftPart_.toString(ring));
                                    replaceOfSymbols(leftPart_, expression);
                                    String nname_ = ((Fname) leftPart_.X[0]).name;
                                    if (contentNameInNCIE(nname_)) {
                                        Element posEL = searchInMatricesElements(nname_);
                                        if (posEL != null) {
                                            replaceOfSymbols(XX[t], expression);
                                            F leftPart__ = (XX[t] instanceof F) ? (F) XX[t] : new F(XX[t]);
                                            nnObj = cfPage.InputForm(leftPart__, this, expression).toString(ring);
                                        } else {
                                            ring.exception.append("Address to a non-existent object ").append(nn).append(";");
                                        }
                                        sb.append(nn);
                                        if (nnObj.length() == 0) {// это признак матричного элемента
                                            Element ElemNN = new Fname(nn);
                                            replaceOfSymbols(ElemNN, expression);
                                            String rStr = ElemNN.toString(ring);
                                            if (!rStr.equals(nn)) {
                                                sb.append(" = ").append(rStr);
                                            }
                                        } else {
                                            sb.append(" = ").append(nnObj);
                                        }
                                    } else {
                                        String ss = cfPage.InputForm(leftPart_, this, expression).toString(ring);
                                        sb.append(feftPartStr).append(" = ").append(ss);
                                    }
                                } else { //обычная печать имен из expr   ////////////////////////////
                                    int j = expression.size() - 1;
                                    ura:
                                    {
                                        for (; j >= 0; j--) {
                                            Element obj = expression.get(j);
                                            if (obj instanceof Fname
                                                    && ((Fname) obj).name.equals(FUtils.deleteDoubledBrackets(nn).trim())) {
                                                nnObj = ((((Fname) obj).X == null) || (((Fname) obj).X[0] == null))
                                                        ? (obj.toString(ring))
                                                        : (((Fname) obj).X[0]).toString(ring);
                                                // почему тут типы VectorSF и MatrixDF ?? почему все еще приставка F?                                      
                                                sb.append(nn).append(" = ").append(nnObj);
                                                break ura;
                                            }
                                        }
                                        sb.append(nn);
                                    }
                                }  // конец обычной печати a=x+1 или a. ///////////////
                            } // цикл по всем печатающимся элементам
                            printLastExpr = false;
                            break;
                        }
//                        case F.PLOTPOLYGONDISTRIBUTION: {
//                            replaceOfSymbols(f, expression);
//                            Element ff = expandFnameOrId(f.X[0]);
//                            Element[][] M;
////                            if (ff instanceof MatrixD) {
////                                M = ((MatrixD) ff).M;
////                            } else {
//                            M = ((MatrixD) cfPage.substituteValueToFnameAndCorrectPolynoms(ff, expression)).M;
//                            //               }
//                            RandomQuantity M_ = new RandomQuantity(M[0], M[1]);
//                            f = M_.plotPolygonDistribution();
//                            f.showGraphics(this, true, false, false, null);
//                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
//                            printLastExpr = false;
//                            break;
//                        }
//                        case F.PLOTDISTRIBUTIONFUNCTION: {
//                            replaceOfSymbols(f, expression);
//                            RandomQuantity M_;
//                            if (f.X.length == 1) {
//                                Element ff = expandFnameOrId(f.X[0]);
//                                Element[][] M;
////                                if (ff instanceof MatrixDF) {
////                                    M = ((MatrixDF) ff).M;
////                                } else {
//                                M = ((MatrixD) cfPage.substituteValueToFnameAndCorrectPolynoms(f, expression)).M;
//                                //                      }
//                                M_ = new RandomQuantity(M[0], M[1]);
//                            } else {
//                                Element a1 = expandFnameOrId(f.X[0]);
//                                Element a2 = expandFnameOrId(f.X[1]);
//                                Element f1 = expandFnameOrId(f.X[2]);
//                                M_ = new RandomQuantity(a1, a2, f1);
//                            }
//                            f = M_.plotDistributionFunction();
//                            ArrayList<String> namesFunc = new ArrayList<String>();
//                            for (int index = 0; index < ((F) f.X[0]).X.length; index++) {
//                                namesFunc.add("f" + index);
//                            }
//                            f.showGraphics(this, true, false, false, namesFunc.toArray(new String[namesFunc.size()]));
//                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
//                            printLastExpr = false;
//                            break;
//                        }
                        case F.PLOTGRAPH:
                            replaceOfSymbols(f, expression);
                            f.plotGraph(this, true);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        case F.POINTSPLOT:
                        case F.PARAMPLOT:
                        case F.PLOT:
                        case F.TABLEPLOT:
                        case F.TEXTPLOT:
                            replaceOfSymbols(f, expression);
                            plots.put(currentSectionNumber, f);
                            f.showGraphics(this, true, false, false, null);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        case F.SHOWPLOTS:
                            ArrayList<String> namesFunc = new ArrayList<>();
                            for (int index = 0; index < ((F) f.X[0]).X.length; index++) {
                                if (((F) f.X[0]).X[index] instanceof F) {
                                    if (((F) ((F) f.X[0]).X[index]).name == F.TABLEPLOT) {
                                        namesFunc.add(String.valueOf(index));
                                    } else {
                                        namesFunc.add(((F) f.X[0]).X[index].toString(ring));
                                    }
                                } else {
                                    namesFunc.add(((F) f.X[0]).X[index].toString(ring));
                                }
                            }
                            plots.put(currentSectionNumber, f);
                            replaceOfSymbols(f, expression);
                            f.showGraphics(this, true, false, false, namesFunc.toArray(new String[namesFunc.size()]));
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        case F.REPLOT:
                            optionsPlot = null;
                            handleReplot(f, expression, cfPage);
                            printLastExpr = false;
                            break;
                        case F.IMPLICIT_PLOT3D:
                            replaceOfSymbols(f, expression);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        case F.EXPLICIT_PLOT3D:
                            replaceOfSymbols(f, expression);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        case F.PARAMETRIC_PLOT3D:
                            replaceOfSymbols(f, expression);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        case F.SHOW_3D:
                            replaceOfSymbols(f, expression);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        case F.PLOT3D:
                        case F.PARAMPLOT3D:
                            replaceOfSymbols(f, expression);
                            Element[] vals = new Element[ring.varPolynom.length];
                            System.arraycopy(ring.varPolynom, 0, vals, 0, 2);
                            for (int j = 2; j < ring.varPolynom.length; j++) {
                                vals[j] = ring.numberONE;
                            }
                            Element[] newArgs = new Element[f.X.length];
                            System.arraycopy(f.X, 0, newArgs, 0, f.X.length);
                            //Первый аргумент - функция для построения
                            newArgs[0] = f.X[0].value(vals, ring);
                            // Tак, чтобы можно было строить функции не только от (x, y)
                            F newF = new F(f.name, newArgs);
                            plots.put(currentSectionNumber, f);
                            newF.showGraf3D(this, true, "x", "y", "");
                            printLastExpr = false;
                            break;
                        case F.TABLE:
                            replaceOfSymbols(f, expression);
                            f_out = new Table(f, expression);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f_out, expression);
                            break;
                        case F.PAINTELEMENT: {
                            plots.put(currentSectionNumber, f);
                            replaceOfSymbols(f, expression);
                            String[] s = new String[f.X.length];
                            for (int h = 0; h < f.X.length; h++) {
                                s[h] = ((Fname) ((F) ((F) f.X[h]).X[0]).X[0]).name;
                            }
//                            Planimetria pp = new Planimetria();
//                            pp.paint(this, true, s);
//                            f = pp.elementF;
 //                           this.title = pp.title;
                            ArrayList<String> names = new ArrayList<>();
                            for (int index = 0; index < ((F) f.X[0]).X.length; index++) {
                                if (((F) f.X[0]).X[index] instanceof F) {
                                    if (((F) ((F) f.X[0]).X[index]).name == F.TABLEPLOT) {
                                        names.add(String.valueOf(index));
                                    }
                                }
                            }
                            f.showGraphics(this, true, true, false, names.toArray(new String[names.size()]));
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f, expression);
                            printLastExpr = false;
                            break;
                        }
                        case F.SOLVEPDE:
                            replaceOfSymbols(f, expression);
                            f_out = cfPage.InputForm(f, this, expression);
                            StringBuffer sb = data.section[1];
                            F resPDE = (F) f_out;// vector
                            for (int ind = 0; ind < resPDE.X.length; ind++) {
                                insertObj(((Fname) resPDE.X[ind]).name, ((Fname) resPDE.X[ind]).X[0], expression);
                                sb.append("\n").
                                        append(((Fname) resPDE.X[ind]).name).
                                        append("=").
                                        append(((Fname) resPDE.X[ind]).X[0]).
                                        append(";");
                            }
                            printLastExpr = false;
                            break;
                        case F.SOLVELDE:
                        case F.VECTOR_SET:
                        case F.SYSTEM:
                        case F.SYSTEMOR:
                            replaceOfSymbols(f, expression);
                            f_out = cfPage.InputForm(f, this, expression);
                            insertObj(((Fname) data.funcs.get(currTransProg[i])).name, f_out, expression);
                            break;
                        default:
                            replaceOfSymbols(f, expression);
                            f_out = cfPage.InputForm(f, this, expression);
                            //     replaceOfSymbols(f_out, expression);
                            if (f_out != null) {
                                String nname = ((Fname) data.funcs.get(currTransProg[i])).name;
                                if (nname.equals("$NONAME$")) {
                                    nname = "";
                                    insertObj(nname, f_out, expression);
                                } else {
                                    Matcher m = VECT_MATRIX.matcher(nname);
                                    tensor1:
                                    //     Matcher m2 = VECT_MATRIX.matcher(nname);
                                    if (m.matches()) {
                                        Element leftPart_ = Parser.getF(nname, ring, data.procNames, this.nonCommutWithIndices);
                                        replaceOfSymbols(leftPart_, expression);
                                        String objName = getArgElementOf(leftPart_);
                                        Fname ffnn = (Fname) ((leftPart_ instanceof F) ? ((F) leftPart_).X[0] : ((Fname) leftPart_));
                                        String nname_ = ffnn.name;
                                        if (contentNameInNCIE(nname_)) {// это вектор или матрица
                                            Element posEL = searchInMatricesElements(nname_);// in nonCommutWithIndices
                                            if (posEL != null) {
                                                //               ((Fname) expr.get(posEL)).X[0] == null ? -2
                                             //   Fname fLmatr = (Fname) expression.get(posEL);
                                                Fname fLmatr = (Fname) posEL;//  expr.get(posEL);
                                                  if ((fLmatr.X != null) && (fLmatr.X[0] instanceof Fname)) {
                                                    Element newTensor= cfPage.makeZeroTensor(((Fname)fLmatr.X[0]).name, expression);
                                                    if(newTensor!=null) {ffnn.X=new Element[]{newTensor}; 
                                                            f_out = cfPage.workWithMatrixs(ffnn, f_out, expression); } 
            //                                        else{ insertObj(ffnn.name, fLmatr, expression); break tensor1;}
                                                  }
                                                //     Fname fLmatr=nonCommutWithIndices.get(posEL);
                                                //   ffnn.X=new Element[]{expr.get(fLmatr.X[0].intValue())};
                                                  else if ((fLmatr.X == null) || (fLmatr.X[0] == null)) {
                                                    ffnn.X = new Element[] {fLmatr};
                                                    f_out = null;
                                                } else {
                                                    ffnn.X = fLmatr.X;
                                                    f_out = cfPage.workWithMatrixs(ffnn, f_out, expression); // 
                                                    if(f_out==null){insertObj(
                                                            nname, f_out, expression);}
                                                }
                                                if (f_out != null) {
                                                   insertObj(objName, f_out, expression);
                                                }
                                            } else {
                                                ring.exception.append("Address to a non-existent object ").append(nname_).append(";\n");
                                            }
                                        } else {// обычное имя с индексами не матрица или вектор
                                            // Fname getFullName = ffnn;
                                            // getFullName.X = null;
                                            insertObj(nname, f_out, expression);
                                        }
                                    } else {
                                        insertObj(nname, f_out, expression);
                                    }
                                }
                            }
                    }
                } // if (!trySetConstants(cfPage, fn, currTransProg, i)){  --- это его конец !!(стр.1463)
                // сюда доходит все, что по умолчанию.... Идем по программе....
                // было обычное выражение из двух индексов в data.funcs, идем вперед на два элемента
                i += 2;
            } else {
                switch (currTransProg[i]) {
                    case OP_COND_JUMP: {
                        i += 1;
                        Element f = ((Fname) data.funcs.get(currTransProg[i])).X[currTransProg[i + 1]];
                        replaceOfSymbols(f, expression);
                        f_out = (f instanceof F) ? cfPage.InputForm((F) f, this, expression) : f;
                        f = f_out;
                        Element resultReq = (f instanceof F) ? (((F) f).X[0].ExpandFnameOrId()) : f;
                        if (resultReq.equals(NumberZ64.FALSE, ring)) {
                            i = currTransProg[i + 2];
                        } else if (resultReq.equals(NumberZ64.TRUE, ring)) {
                            i += 3;
                        } else {
                            throw new MathparException("Error evaluating condition: "
                                    + ((Fname) data.funcs.get(currTransProg[i])).X[currTransProg[i + 1]].toString(ring));
                        }
                        break;
                    }
                    case OP_JUMP: {
                        i = currTransProg[i + 1];
                        break;
                    }
                    case OP_RETURN: {
                        F f = new F((F) ((Fname) data.funcs.get(currTransProg[i + 1])).X[currTransProg[i + 2]]);
                        replaceOfSymbols(f, expression);
                        f_out = cfPage.InputForm(f, this, expression);
                        // это и есть ядро для юбщего случая
                        return f_out;
                    }
                }
            }
        }
        if (printLastExpr && procNum == data.transProg.size() - 1) {
            StringBuffer sb = data.section[1];
            if (expr.isEmpty()) {
                return null;
            }
            
            if(expr.size()<=posLast) posLast=expr.size()-1;
            Element obj = expr.get(posLast);
            // Печать последнего выражения ################################################
            if (!ring.STEPBYSTEP.isZero(ring) && !ring.traceSteps.isEmpty()) {
                VectorS vv = new VectorS(ring.traceSteps, -1);
                sb.append("\n").append(vv.toString(ring)).append(";");
            } else {
                if ((obj instanceof Fname)&&(((Fname)obj).X!=null)&&(((Fname)obj).X[0]!=null)
                        &&(((Fname)obj).X[0]instanceof F)&&(((F)((Fname)obj).X[0]).name==F.ELEMENTOF))
                    sb.append("\n").append(((Fname)obj).name+"=").append(obj.toString(ring));
                else sb.append("\n").append(obj.toString(ring)); //.append(";");       
            }
        }
        return null;
    }  // end of GO

    /**
     * метод, позволяющий выполнять  процедуры и функции, которые завел пользователь
     * Все аргументы этих процедур возвращаются в внешнюю процедуру в измененном виде
     * @param inputProcedure дерево процедуры
     *
     * @return значение функции
     */
    public Element runProcedure(F inputProcedure, List<Element> lastExpress) {
        CanonicForms cf = ring.CForm;
        int ProcNum = inputProcedure.name - F.MAX_F_NUMB;
        Element procArgs[] = inputProcedure.X;
        // создаем для данной процедуры свой список выражение (исполненных операторов)
        List<Element> express = new ArrayList<Element>(16);
        String args = data.argsOfProc.get(ProcNum);
        if (!(args.equals(""))) {
            int i = 0;
            int j = args.indexOf(",");
            int index = 0;
            F f;
            Element fOut;
            while (j <= args.length()) {
                if (j > 0) { // цикл для всех аргументов, кроме последнего
                    String arg = args.substring(i, j).trim();
                    fOut = inputProcedure.X[index];
                    if (fOut instanceof Fname) {
                        String fOutName = ((Fname) fOut).name;
                        // Remove "$2" from the beginning of argument's name.
                        fOut = new Fname(fOutName.substring(2, fOutName.length()));
                        replaceOfSymbols(fOut, lastExpress);
                    }
                    if (fOut instanceof F) {
                        f = (F) inputProcedure.X[index];
                        fOut = cf.InputForm(f, this, express);
                    }
                    // Записали переданное значение параметра в переменную процедуры
                    insertObj(arg, fOut.ExpandFnameOrId(), express);
                    index++;
                    i = j + 1;
                    j = args.indexOf(",", i);
                } else { 
                    j = args.length();
                    String arg = args.substring(i, j).trim();
                    fOut = inputProcedure.X[index];
                    if (fOut instanceof Fname) {
                        String fOutName = ((Fname) fOut).name;
                        // Remove "$2" from the beginning of argument's name.
                        fOut = new Fname(fOutName.substring(2, fOutName.length()));
                        replaceOfSymbols(fOut, lastExpress);
                    }
                    if (fOut instanceof F) {
                        f = (F) inputProcedure.X[index];
                        fOut = cf.InputForm(f, this, express);
                    }// запись значения последнего аргумента в процедуру
                    insertObj(arg, fOut.ExpandFnameOrId(), express);
                    j += 1;
                }
            }
        }
        Element result = go(ProcNum, express);
        for (int ii = 0; ii < procArgs.length; ii++) {
            if (procArgs[ii] instanceof Fname) {
                String procArgName = ((Fname) procArgs[ii]).name;
                String nname = procArgName.substring(2, procArgName.length());
                insertObj(nname, express.get(ii).ExpandFnameOrId(), lastExpress);
            }
        }
        return result;
    }

    /*=================Operatoru Ypravlenia [Dubovitskiy Evgeniy]=================*/
    /**
     * Обработчик оператора if-then-else
     *
     * @param str строка содержащая только оператор if-then-else и его тело
     * @param Proc текущая процедура
     *
     * @throws Exception
     */
    private int ifCaught(String str, IntList Proc) {
        String temp, iReq, iBranchTRUE, iBranchFALSE = "";
        int i = str.indexOf("(");
        int j = FUtils.posOfPairBracket(str, i, '(', ')');
        iReq = str.substring(i + 1, j).trim();

        i = str.indexOf("{", j);
        j = FUtils.posOfPairBracket(str, i, '{', '}');
        iBranchTRUE = str.substring(i + 1, j);

        temp = str.substring(j + 1).trim();
        if (temp.indexOf("else") == 0) {
            i = str.indexOf("{", j);
            j = FUtils.posOfPairBracket(str, i, '{', '}');
            iBranchFALSE = str.substring(i + 1, j);
        }
        int iLength = j;

        Proc.add(OP_COND_JUMP);
        readSingleOperator("$Req$ = " + iReq, Proc);
        int tmp = Proc.size;
        Proc.add(0);
        readOperators(iBranchTRUE, Proc);
        if (iBranchFALSE.length() > 0) {
            Proc.set(tmp, Proc.size + 2);
            Proc.add(OP_JUMP);
            tmp = Proc.size;
            Proc.add(0);
            readOperators(iBranchFALSE, Proc);
        }
        Proc.set(tmp, Proc.size);
        return iLength;
    }

    /**
     * Обработчик оператора while
     *
     * @param str строка содержащая только оператор while и его тело
     * @param Proc текущая процедура
     *
     * @throws Exception
     */
    private int whileCaught(String str, IntList Proc) {
        String wReq, wBody;
        int i = str.indexOf("(");
        int j = FUtils.posOfPairBracket(str, i, '(', ')');
        wReq = str.substring(i + 1, j).trim();

        i = str.indexOf("{", j);
        j = FUtils.posOfPairBracket(str, i, '{', '}');
        wBody = str.substring(i + 1, j).trim();

        int wLength = j;

        int jump = Proc.size;
        Proc.add(OP_COND_JUMP);
        readSingleOperator("$Req$ = " + wReq, Proc);
        int temp = Proc.size;
        Proc.add(0);
        readOperators(wBody, Proc);
        Proc.add(OP_JUMP);
        Proc.add(jump);
        Proc.set(temp, Proc.size);

        return wLength;
    }

    /**
     * Обработчик оператора for
     *
     * @param str строка содержащая только оператор for и его тело
     * @param Proc текущая процедура
     */
    private int forCaught(String str, IntList Proc) {
        String temp, forInit, forCond, forCounter, forBody;
        int i = str.indexOf("(");
        int j = FUtils.posOfPairBracket(str, i, '(', ')');
        temp = str.substring(i + 1, j).trim();
        int ii = 0;
        int jj = temp.indexOf(";");
       try {int eee=1/(1+jj);} catch (Exception ee) {
           ring.exception.append("\nFor(; ;) without semicolons. Wrong for-operator. ") ; throw ee;}
        forInit = temp.substring(ii, jj).trim();
        ii = jj + 1;
        jj = temp.indexOf(";", ii);
        forCond = temp.substring(ii, jj).trim();
        ii = jj + 1;
        forCounter = temp.substring(ii).trim();
       try {int eee=1/(ii);} catch (Exception ee) {
           ring.exception.append("\nFor(; ;) without semicolons. Wrong for-operator. ") ; throw ee;}
        i = str.indexOf("{", j);
        j = FUtils.posOfPairBracket(str, i, '{', '}');
        forBody = str.substring(i + 1, j).trim();

        int wLength = j;

        readSingleOperator(forInit, Proc);
        int jump = Proc.size;
        Proc.add(OP_COND_JUMP);
        readSingleOperator("$Req$ = " + forCond, Proc);
        int tmp = Proc.size;
        Proc.add(0);
        readOperators(forBody, Proc);
        readSingleOperator(forCounter, Proc);
        Proc.add(OP_JUMP);
        Proc.add(jump);
        Proc.set(tmp, Proc.size);
        return wLength;
    }

    private int procedureCaught(String str, IntList Proc) {
        int i = str.indexOf("(");
        data.procNames.add(str.substring(str.indexOf(KW_PROC) + KW_PROC.length(), i).trim());
        int j = FUtils.posOfPairBracket(str, i, '(', ')');
        data.argsOfProc.add(str.substring(i + 1, j).trim());
        i = str.indexOf("{", j);
        j = endOfOperator(str, i, '{', '}');
        String expr1 = str.substring(i + 1, j).trim();
        IntList Proc1 = new IntList();
        readOperators(expr1, Proc1);
        data.transProg.add(Proc1);
        return j;
    }

    /**
     * метод, разделяющий строку на операторы: - либо по символу ";" - либо по
     * операторам управления и отдающая полученную подстроку на обработку
     *
     * @param str исходная строка
     * @param proc текущая процедура
     */
    private void readOperators(String str, IntList proc) {
        int stringLiteralBegin, stringLiteralEnd, opEnd = -1, opBegin = 0;
        int operatorsCount = 0;
        boolean notLastOperator = true;
        while (notLastOperator) {
            while (true) {
                stringLiteralBegin = str.indexOf('\'', opEnd);
                stringLiteralEnd = str.indexOf('\'', stringLiteralBegin + 1);
                opEnd = str.indexOf(';', opEnd + 1);
                if (opEnd == -1 || opEnd > stringLiteralEnd || opEnd < stringLiteralBegin) {
                    break;
                }
                // найден конец оператора
                while (opEnd > stringLiteralBegin && opEnd < stringLiteralEnd) {
                    opEnd = str.indexOf(";", opEnd + 1);
                }
            }
            if (opEnd == -1){
                opEnd = str.length();
                notLastOperator = false;
            }
            String expr1 = str.substring(opBegin, opEnd).trim();
            operatorsCount++;
            if (!expr1.equals("")) {
                /*=============================================*/
                if (expr1.indexOf("if") == 0) {
                    opEnd = opBegin + ifCaught(str.substring(opBegin), proc);
                    /*=============================================*/
                } else if (expr1.indexOf("while") == 0) {
                    opEnd = opBegin + whileCaught(str.substring(opBegin), proc);
                    /*=============================================*/
                } else if (expr1.indexOf("for") == 0) {
                    opEnd = opBegin + forCaught(str.substring(opBegin), proc);
                    /*=============================================*/
                } else if (expr1.indexOf(KW_PROC) == 0) {
                    opEnd = opBegin + procedureCaught(str.substring(opBegin), proc);
                    /*=============================================*/
                } else if (expr1.indexOf(KW_RETURN) == 0) {
                    proc.add(OP_RETURN);
                    expr1 = "RETURN = " + expr1.substring(KW_RETURN.length()).trim();
                    readSingleOperator(expr1, proc);
                } else { 
                    expr1 = expr1.replaceAll("([^'A-Za-z0-9\\)\\]\\}])''", "$1\\\\hbox{}");
                    expr1 = expr1.replaceAll("'([^']+?)'", "\\\\hbox{$1}");
                    try {
                        readSingleOperator(expr1, proc);
                    } catch (Exception ee) {
                        ring.exception.append("\nException in the operator number ").append(operatorsCount);
                        throw ee;
                    }
                }
            }
            opBegin = opEnd + 1; //начало следующего оператора
            if (opBegin >= str.length()) {
                notLastOperator = false;
            }
        }
    }

    /**
     * Rezult may be "" or the last symbolic name If this object is polynomial
     * or other "notSymbolicName" we obtain ""
     *
     * @param e
     *
     * @return String which is symbolic name or ""
     */
    public String getArgElementOf(Element e) {
        if (e instanceof F) {
            if (((F) e).name == F.ELEMENTOF) {
                return getArgElementOf(((F) e).X[0]);
            }
            if (((F) e).name == F.ID) {
                return getArgElementOf(((F) e).X[0]);
            }
        }
        if (e instanceof Fname) {
            return (((Fname) e).X == null) ? ((Fname) e).name : getArgElementOf(((Fname) e).X[0]);
        }
        return "";
    }

    /**
     * Процедура поднимающая последний элемент во вложенных Fname или F с
     * именами ID, либо их комбинациях.
     *
     * @param g - любой элемент
     *
     * @return
     */
    public Element expandFnameOrId(Element g) {
        if (g instanceof F) {
            return (((F) g).name == F.ID) ? expandFnameOrId(((F) g).X[0]) : g;
        }
        if (g instanceof Fname) {
            return (((Fname) g).X == null) ? g : expandFnameOrId(((Fname) g).X[0]);
        }
        return g;
    }

        /**
     * Процедура поднимающая 2 последних элемента во вложенных Fname или F с
     * именами ID, либо их комбинациях. Вернется Fname+object.
     *
     * @param g[] - пара элементов, любой элемент, например [null, F]
     *
     * @return пара элементов [Fname, Element]
     */
    public Element[] expandFnameOrId2(Element[] g) {
        if (g[1] instanceof Fname) { g[0]= g[1] ; 
           if (((Fname)(g[1])).isEmpty(ring)) {g[1]=null;  return g;}
           else { Element W = ((Fname)(g[1])).X[0]; expandFnameOrId(W); g[1]=W;  return g;}}
         if (g[1] instanceof F) {
            if(((F) (g[1])).name == F.ID) { 
                g[1]=((F)(g[1])).X[0]; 
                return expandFnameOrId2(g);}}
       return g;
    }
    
    /**
     * Метод для настройки параметров перерисовки графиков
     *
     * @param f - функция для перерисовки графиков
     * @param expression -
     * @param cfPage -
     */
    private void handleReplot(F f, List<Element> expression, CanonicForms cfPage) {
        F func = plots.get(currentSectionNumber);//функция графика
        replaceOfSymbols(f, expression);
        F fInput = new F(func.name, func.X);
        fInput = (F) cfPage.UnconvertToElement(cfPage.substituteValueToFnameAndCorrectPolynoms(fInput, expression));
        Table t;
        boolean lineDraw = true;
        if (func.name == F.POINTSPLOT) {
            lineDraw = false;
        }
        if (func.name == F.TABLEPLOT) {
            if (fInput.X[0] instanceof MatrixD) {
                t = new Table(((MatrixD) fInput.X[0]));
                fInput.X[0] = t;
                plots.put(currentSectionNumber, fInput);
            } else if (fInput.X[0] instanceof MatrixS) {
                t = new Table((MatrixS) fInput.X[0]);
                fInput.X[0] = t;
                plots.put(currentSectionNumber, fInput);
            } else if (fInput.X[0] instanceof F) {
                t = new Table(((MatrixD) ((F) fInput.X[0]).X[0]));
                fInput.X[0] = t;
                plots.put(currentSectionNumber, fInput);
            } else {
                t = (Table) fInput.X[0];
            }
            //пересчет параметров и таблицы
            t.replot(f, expression);
            Element[] settings = new Element[((F) f.X[0]).X.length + 5];
            settings[((F) f.X[0]).X.length] = (((Fname) ((F) f.X[4]).X[0]).name.equals("false")) ? new NumberR64(0) : new NumberR64(1);
            settings[((F) f.X[0]).X.length + 1] = (((Fname) ((F) f.X[3]).X[0]).name.equals("false")) ? new NumberR64(0) : new NumberR64(1);
            settings[((F) f.X[0]).X.length + 2] = ((Polynom) f.X[5]).coeffs[0];
            settings[((F) f.X[0]).X.length + 3] = ((Polynom) f.X[6]).coeffs[0];
            settings[((F) f.X[0]).X.length + 4] = ((Polynom) f.X[7]).coeffs[0];
            String style = "";
            if (fInput.X.length == 2) {
                if (fInput.X[1] != null) {
                    style = ((Fname) ((F) fInput.X[1]).X[0]).name;
                }
            }
      //      Plots p = new Plots(this, true, t, new Element[] {}, 0, lineDraw, settings, style);
        } else {
            F paramsVector = (F) f.X[0];
            Element[] paramsAndSettings = new Element[paramsVector.X.length + 5];
            for (int i = 0; i < paramsAndSettings.length - 5; i++) {
                Polynom currParam = (Polynom) paramsVector.X[i];
                if (currParam.isZero(ring)) {
                    paramsAndSettings[i] = new NumberR64(0);
                } else {
                    paramsAndSettings[i] = currParam.coeffs[0];
                }
            }
            paramsAndSettings[paramsVector.X.length] = (((Fname) ((F) f.X[2]).X[0]).name.equals("false"))
                    ? new NumberR64(0) : new NumberR64(1);
            paramsAndSettings[paramsVector.X.length + 1] = (((Fname) ((F) f.X[1]).X[0]).name.equals("false"))
                    ? new NumberR64(0) : new NumberR64(1);
            paramsAndSettings[paramsVector.X.length + 2] = ((Polynom) f.X[3]).coeffs[0];
            paramsAndSettings[paramsVector.X.length + 3] = ((Polynom) f.X[4]).coeffs[0];
            paramsAndSettings[paramsVector.X.length + 4] = ((Polynom) f.X[5]).coeffs[0];
            if (func.name == F.POINTSPLOT || func.name == F.PLOT || func.name == F.PARAMPLOT || func.name == F.PLOT3D) {
                handleMultiframeReplot(paramsAndSettings, f, func);
            } else if (func.name == F.SHOWPLOTS) {
                List<String> namesFunc = new ArrayList<String>();
                for (int index = 0; index < ((F) func.X[0]).X.length; index++) {
                    namesFunc.add("f" + index);
                }
                func.showPlots(this, paramsAndSettings, true, namesFunc.toArray(new String[namesFunc.size()]));
            }
        }
    }

    /**
     * Produces image or a sequence of images with respect to the parameter
     * frames number of {@literal f} function (replot).
     *
     * @param arr
     * @param f
     * @param func
     */
    private void handleMultiframeReplot__(Element[] arr, F f, F func) {
        int framesNumber = 1;
        if (f.X[4] == null) {
            framesNumber = 1;
        } else {
            framesNumber = ((Polynom) f.X[4]).coeffs[0].intValue();
        }
        if (framesNumber > 1) {
            int parametersNumber = arr.length - 5;
            Element[] deltas = new Element[parametersNumber];
            NumberR64 framesCnt = new NumberR64(framesNumber);
            for (int j = 0; j < parametersNumber; j++) {
                // delta[j] = (1 - parameters[j]) / framesCnt;
                deltas[j] = NumberR64.ONE.subtract(arr[j], ring).divide(framesCnt, ring);
                // j-ый параметр стартует с 1 и уменьшается до заданного
                // пользователем значения с шагом deltas[j].
                arr[j] = NumberR64.ONE;
            }
            // Готовим пустое хранилище для кадров
            images.put(currentSectionNumber, new BufferedImage[framesNumber]);
            for (int j = 0; j < framesNumber; j++) {
                callplot(func, arr);
                for (int k = 0; k < parametersNumber; k++) {
                    arr[k] = arr[k].subtract(deltas[k], ring);
                }
            }
        } else {
            callplot(func, arr);
        }
    }

    /**
     * Produces image or a sequence of images with respect to the parameter
     * frames number of {@literal f} function (replot).
     *
     * @param arr
     * @param f
     * @param func
     */
    private void handleMultiframeReplot(Element[] arr, F f, F func) {
        int framesNumber = 1;
        if (f.X[6] == null) {
            framesNumber = 1;
        } else {
            framesNumber = ((Polynom) f.X[6]).coeffs[0].intValue();
        }
        if (framesNumber > 1) {
            int parametersNumber = arr.length - 5;
            Element[] deltas = new Element[parametersNumber];
            NumberR64 framesCnt = new NumberR64(framesNumber);
            for (int j = 0; j < parametersNumber; j++) {
                // delta[j] = (1 - parameters[j]) / framesCnt;
                deltas[j] = NumberR64.ONE.subtract(arr[j], ring).divide(framesCnt, ring);
                // j-ый параметр стартует с 1 и уменьшается до заданного
                // пользователем значения с шагом deltas[j].
                arr[j] = NumberR64.ONE;
            }
            // Готовим пустое хранилище для кадров
            images.put(currentSectionNumber, new BufferedImage[framesNumber]);
            for (int j = 0; j < framesNumber; j++) {
                callplot(func, arr);
                for (int k = 0; k < parametersNumber; k++) {
                    arr[k] = arr[k].subtract(deltas[k], ring);
                }
            }
        } else {
            callplot(func, arr);
        }
    }

    private void callplot(F func, Element[] arr) {
        switch (func.name) {
            case F.POINTSPLOT:
                func.pointsPlot(this, arr, true,
                        0, 0, 0, 0, new double[1][], new double[1][],
                        new Vector(), new Vector(), new Vector(), new Vector(), false, true);
                break;
            case F.PARAMPLOT:
                func.paramPlot(this, arr, true, 0, 0, 0, 0, 0, 0, new Vector(), new Vector(), true);
                break;
            case F.PLOT:
                func.plot(this, arr, true, new Vector(), true);
                break;
            case F.PLOT3D:
                Element[] vals = new Element[ring.varPolynom.length];
                System.arraycopy(ring.varPolynom, 0, vals, 0, 2);
                for (int j = 2; j < ring.varPolynom.length; j++) {
                    vals[j] = arr[j - 2];
                }
                Element[] newArgs = new Element[func.X.length];
                System.arraycopy(func.X, 0, newArgs, 0, func.X.length);
                newArgs[0] = func.X[0].value(vals, ring);
                F newF = new F(func.name, newArgs);
                newF.showGraf3D(this, true, "x", "y", "");
                break;
            default:
                throw new IllegalArgumentException("Can't plot this function: " + func.toString(ring));
        }
    }

    /**
     *
     * New method for \set2D([],[],[])
     *
     * @param f
     *
     */
    public void set2D(Element f) {
        xMin = 0;
        xMax = 0;
        yMin = 0;
        yMax = 0;
        nameOX = "x";
        nameOY = "y";
        title = "";
        option = "";
        if (f instanceof F) {
            int l = ((F) f).X.length;//количество аргументов
            if (l == 1) {//либо [xMin, xMax, yMin, yMax], либо [xMin, xMax]
                Element arg = expandFnameOrId(((F) f).X[0]);
                if (arg instanceof F) {
                    if (((F) arg).name == F.VECTORS) {
                        int k = ((F) arg).X.length;
                        FvalOf fvof = new FvalOf(ring);
                        if (((F) arg).X[0] instanceof Polynom) {
                            if (((Polynom) ((F) arg).X[0]).isZero(ring)) {
                                xMin = 0;
                            } else {
                                xMin = ((Polynom) ((F) arg).X[0]).coeffs[0].value;
                            }
                        } else {
                            xMin = fvof.valOf(((F) arg).X[0], new Element[0]).doubleValue();
                        }
                        if (((F) arg).X[1] instanceof Polynom) {
                            if (((Polynom) ((F) arg).X[1]).isZero(ring)) {
                                xMax = 0;
                            } else {
                                xMax = ((Polynom) ((F) arg).X[1]).coeffs[0].value;
                            }
                        } else {
                            xMax = fvof.valOf(((F) arg).X[1], new Element[0]).doubleValue();
                        }
                        if (k == 4) {
                            if (((F) arg).X[2] instanceof Polynom) {
                                if (((Polynom) ((F) arg).X[2]).isZero(ring)) {
                                    yMin = 0;
                                } else {
                                    yMin = ((Polynom) ((F) arg).X[2]).coeffs[0].value;
                                }
                            } else {
                                yMin = fvof.valOf(((F) arg).X[2], new Element[0]).doubleValue();
                            }
                            if (((F) arg).X[3] instanceof Polynom) {
                                if (((Polynom) ((F) arg).X[3]).isZero(ring)) {
                                    yMax = 0;
                                } else {
                                    yMax = ((Polynom) ((F) arg).X[3]).coeffs[0].value;
                                }
                            } else {
                                yMax = fvof.valOf(((F) arg).X[3], new Element[0]).doubleValue();
                            }
                        }
                    }
                }
            }
            if (l == 2) {//либо ([xMin, xMax, yMin, yMax],['x','y','title']), либо ([xMin, xMax, yMin, yMax],[0,1,18,3,3])
                Element arg = expandFnameOrId(((F) f).X[0]);
                if (arg instanceof F) {
                    if (((F) arg).name == F.VECTORS) {
                        int k = ((F) arg).X.length;
                        FvalOf fvof = new FvalOf(ring);
                        if (((F) arg).X[0] instanceof Polynom) {
                            if (((Polynom) ((F) arg).X[0]).isZero(ring)) {
                                xMin = 0;
                            } else {
                                xMin = ((Polynom) ((F) arg).X[0]).coeffs[0].value;
                            }
                        } else {
                            xMin = fvof.valOf(((F) arg).X[0], new Element[0]).doubleValue();
                        }
                        if (((F) arg).X[1] instanceof Polynom) {
                            if (((Polynom) ((F) arg).X[1]).isZero(ring)) {
                                xMax = 0;
                            } else {
                                xMax = ((Polynom) ((F) arg).X[1]).coeffs[0].value;
                            }
                        } else {
                            xMax = fvof.valOf(((F) arg).X[1], new Element[0]).doubleValue();
                        }
                        if (k == 4) {
                            if (((F) arg).X[2] instanceof Polynom) {
                                if (((Polynom) ((F) arg).X[2]).isZero(ring)) {
                                    yMin = 0;
                                } else {
                                    yMin = ((Polynom) ((F) arg).X[2]).coeffs[0].value;
                                }
                            } else {
                                yMin = fvof.valOf(((F) arg).X[2], new Element[0]).doubleValue();
                            }
                            if (((F) arg).X[3] instanceof Polynom) {
                                if (((Polynom) ((F) arg).X[3]).isZero(ring)) {
                                    yMax = 0;
                                } else {
                                    yMax = ((Polynom) ((F) arg).X[3]).coeffs[0].value;
                                }
                            } else {
                                yMax = fvof.valOf(((F) arg).X[3], new Element[0]).doubleValue();
                            }
                        }
                    }
                }
                Element arg1 = expandFnameOrId(((F) f).X[1]);
                if (arg1 instanceof F) {
                    if (((F) arg1).name == F.VECTORS) {
                        int k = ((F) arg1).X.length;
                        if (k == 1) {
                            if (((F) arg1).X[0] instanceof F) {
                                if (((F) ((F) arg1).X[0]).name == F.HBOX) {
                                    Element argT = expandFnameOrId(((F) ((F) arg1).X[0]).X[0]);
                                    String name = ((Fname) argT).name;
                                    if (name.equals("ES") || name.equals("BW") || name.equals("ESBW") || name.equals("BWES")) {
                                        option = name;
                                    } else {
                                        title = name;
                                    }
                                }
                            }
                        }
                        if (k == 2) {
                            if (((F) ((F) arg1).X[0]).name == F.HBOX && ((F) ((F) arg1).X[1]).name == F.HBOX) {
                                Element argX = expandFnameOrId(((F) ((F) arg1).X[0]).X[0]);
                                Element argY = expandFnameOrId(((F) ((F) arg1).X[1]).X[0]);
                                nameOX = ((Fname) argX).name;
                                nameOY = ((Fname) argY).name;
                            }
                        }
                        if (k == 3) {
                            if (((F) ((F) arg1).X[0]).name == F.HBOX && ((F) ((F) arg1).X[1]).name == F.HBOX && ((F) ((F) arg1).X[2]).name == F.HBOX) {
                                Element argX = expandFnameOrId(((F) ((F) arg1).X[0]).X[0]);
                                Element argY = expandFnameOrId(((F) ((F) arg1).X[1]).X[0]);
                                Element argT = expandFnameOrId(((F) ((F) arg1).X[2]).X[0]);
                                nameOX = ((Fname) argX).name;
                                nameOY = ((Fname) argY).name;
                                title = ((Fname) argT).name;
                            }
                        }
                        if (k == 5) {
                            F fOpt = ((F) arg1);
                            optionsPlot = new double[fOpt.X.length];
                            if (((Polynom) ((F) arg1).X[0]).isZero(ring)) {
                                optionsPlot[0] = 0;
                            } else {
                                optionsPlot[0] = ((Polynom) fOpt.X[0]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[1]).isZero(ring)) {
                                optionsPlot[1] = 0;
                            } else {
                                optionsPlot[1] = ((Polynom) fOpt.X[1]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[2]).isZero(ring)) {
                                optionsPlot[2] = 0;
                            } else {
                                optionsPlot[2] = ((Polynom) fOpt.X[2]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[3]).isZero(ring)) {
                                optionsPlot[3] = 0;
                            } else {
                                optionsPlot[3] = ((Polynom) fOpt.X[3]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[4]).isZero(ring)) {
                                optionsPlot[4] = 0;
                            } else {
                                optionsPlot[4] = ((Polynom) fOpt.X[4]).coeffs[0].value;
                            }
                        }
                    }
                }
            }
            if (l == 3) {//либо ([xMin, xMax, yMin, yMax],['x','y','title'],[0,1,18,3,3])
                Element arg0 = expandFnameOrId(((F) f).X[0]);
                if (arg0 instanceof F) {
                    if (((F) arg0).name == F.VECTORS) {
                        int k = ((F) arg0).X.length;
                        FvalOf fvof = new FvalOf(ring);
                        if (((F) arg0).X[0] instanceof Polynom) {
                            if (((Polynom) ((F) arg0).X[0]).isZero(ring)) {
                                xMin = 0;
                            } else {
                                xMin = ((Polynom) ((F) arg0).X[0]).coeffs[0].value;
                            }
                        } else {
                            xMin = fvof.valOf(((F) arg0).X[0], new Element[0]).doubleValue();
                        }
                        if (((F) arg0).X[1] instanceof Polynom) {
                            if (((Polynom) ((F) arg0).X[1]).isZero(ring)) {
                                xMax = 0;
                            } else {
                                xMax = ((Polynom) ((F) arg0).X[1]).coeffs[0].value;
                            }
                        } else {
                            xMax = fvof.valOf(((F) arg0).X[1], new Element[0]).doubleValue();
                        }
                        if (k == 4) {
                            if (((F) arg0).X[2] instanceof Polynom) {
                                if (((Polynom) ((F) arg0).X[2]).isZero(ring)) {
                                    yMin = 0;
                                } else {
                                    yMin = ((Polynom) ((F) arg0).X[2]).coeffs[0].value;
                                }
                            } else {
                                yMin = fvof.valOf(((F) arg0).X[2], new Element[0]).doubleValue();
                            }
                            if (((F) arg0).X[3] instanceof Polynom) {
                                if (((Polynom) ((F) arg0).X[3]).isZero(ring)) {
                                    yMax = 0;
                                } else {
                                    yMax = ((Polynom) ((F) arg0).X[3]).coeffs[0].value;
                                }
                            } else {
                                yMax = fvof.valOf(((F) arg0).X[3], new Element[0]).doubleValue();
                            }
                        }
                    }
                }
                Element arg = expandFnameOrId(((F) f).X[1]);
                if (arg instanceof F) {
                    if (((F) arg).name == F.VECTORS) {
                        int k = ((F) arg).X.length;
                        if (k == 1) {
                            if (((F) arg).X[0] instanceof F) {
                                if (((F) ((F) arg).X[0]).name == F.HBOX) {
                                    Element argT = expandFnameOrId(((F) ((F) arg).X[0]).X[0]);
                                    title = ((Fname) argT).name;
                                }
                            }
                        }
                        if (k == 2) {
                            if (((F) ((F) arg).X[0]).name == F.HBOX && ((F) ((F) arg).X[1]).name == F.HBOX) {
                                Element argX = expandFnameOrId(((F) ((F) arg).X[0]).X[0]);
                                Element argY = expandFnameOrId(((F) ((F) arg).X[1]).X[0]);
                                nameOX = ((Fname) argX).name;
                                nameOY = ((Fname) argY).name;
                            }
                        }
                        if (k == 3) {
                            if (((F) ((F) arg).X[0]).name == F.HBOX && ((F) ((F) arg).X[1]).name == F.HBOX && ((F) ((F) arg).X[2]).name == F.HBOX) {
                                Element argX = expandFnameOrId(((F) ((F) arg).X[0]).X[0]);
                                Element argY = expandFnameOrId(((F) ((F) arg).X[1]).X[0]);
                                Element argT = expandFnameOrId(((F) ((F) arg).X[2]).X[0]);
                                nameOX = ((Fname) argX).name;
                                nameOY = ((Fname) argY).name;
                                title = ((Fname) argT).name;
                            }
                        }
                    }
                }
                Element arg1 = expandFnameOrId(((F) f).X[2]);
                if (arg1 instanceof F) {
                    if (((F) arg1).name == F.VECTORS) {
                        int k = ((F) arg1).X.length;
                        if (k == 1) {
                            if (((F) arg1).X[0] instanceof F) {
                                if (((F) ((F) arg1).X[0]).name == F.HBOX) {
                                    Element argT = expandFnameOrId(((F) ((F) arg1).X[0]).X[0]);
                                    option = ((Fname) argT).name;
                                }
                            }
                        }
                        if (k == 5) {
                            F fOpt = ((F) arg1);
                            optionsPlot = new double[fOpt.X.length];
                            if (((Polynom) fOpt.X[0]).isZero(ring)) {
                                optionsPlot[0] = 0;
                            } else {
                                optionsPlot[0] = ((Polynom) fOpt.X[0]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[1]).isZero(ring)) {
                                optionsPlot[1] = 0;
                            } else {
                                optionsPlot[1] = ((Polynom) fOpt.X[1]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[2]).isZero(ring)) {
                                optionsPlot[2] = 0;
                            } else {
                                optionsPlot[2] = ((Polynom) fOpt.X[2]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[3]).isZero(ring)) {
                                optionsPlot[3] = 0;
                            } else {
                                optionsPlot[3] = ((Polynom) fOpt.X[3]).coeffs[0].value;
                            }
                            if (((Polynom) fOpt.X[4]).isZero(ring)) {
                                optionsPlot[4] = 0;
                            } else {
                                optionsPlot[4] = ((Polynom) fOpt.X[4]).coeffs[0].value;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Устанавливаем настрйоки для 2D
     *
     * @param x0 - min по X
     * @param x1 - max по X
     * @param y0 - min по Y
     * @param y1 - max по Y
     * @param x - подпись по оси X
     * @param y - подпись по оси Y
     * @param t - заголовок
     */
    public void set2D_old(Element x0, Element x1, Element y0, Element y1, Element x, Element y, Element t) {
        if (x0 == null && x1 == null && y0 == null && y1 == null && x == null && y == null && t == null) {
            xMin = 0;
            xMax = 0;
            yMin = 0;
            yMax = 0;
            nameOX = "x";
            nameOY = "y";
            title = "";
            option = "";
        } else {
            FvalOf fvof = new FvalOf(ring);
            if (x0 != null) {
                if (x0 instanceof Polynom) {
                    if (((Polynom) x0).isZero(ring)) {
                        xMin = 0;
                    } else {
                        xMin = ((Polynom) x0).coeffs[0].value;
                    }
                } else {
                    xMin = fvof.valOf(x0, new Element[0]).doubleValue();
                }
            }
            if (x1 != null) {
                if (x1 instanceof Polynom) {
                    if (((Polynom) x1).isZero(ring)) {
                        xMax = 0;
                    } else {
                        xMax = ((Polynom) x1).coeffs[0].value;
                    }
                } else {
                    xMax = fvof.valOf(x1, new Element[0]).doubleValue();
                }
            }
            if (y0 != null) {
                if (y0 instanceof Polynom) {
                    if (((Polynom) y0).isZero(ring)) {
                        yMin = 0;
                    } else {
                        yMin = ((Polynom) y0).coeffs[0].value;
                    }
                } else {
                    yMin = fvof.valOf(y0, new Element[0]).doubleValue();
                }
            }

            if (y1 != null) {
                if (y1 instanceof Polynom) {
                    if (((Polynom) y1).isZero(ring)) {
                        yMax = 0;
                    } else {
                        yMax = ((Polynom) y1).coeffs[0].value;
                    }
                } else {
                    yMax = fvof.valOf(y1, new Element[0]).doubleValue();
                }
            }

            if (x != null) {
                nameOX = ((Fname) ((F) ((F) x).X[0]).X[0]).name;
            }
            if (y != null) {
                nameOY = ((Fname) ((F) ((F) y).X[0]).X[0]).name;
            }
            if (t != null) {
                title = ((Fname) ((F) ((F) t).X[0]).X[0]).name;
            }
        }
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        userDir = new File(MATHPAR_DIR, sessionId);
        if (!userDir.exists()) {
            userDir.mkdir();
            userUploadDir = new File(userDir, "uploads");
            if (!userUploadDir.exists()) {
                userUploadDir.mkdir();
            }
        }
    }

    public File getUserDir() {
        return userDir;
    }

    public File getUserUploadDir() {
        return userUploadDir;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Ring ring() {
        return ring;
    }

    public void setClusterQueryCreator(QueryCreator queryCreator) {
        this.clusterQueryCreator = queryCreator;
    }

    public QueryCreator getClusterQueryCreator() {
        return clusterQueryCreator;
    }

    /**
     * Adds image at current active section.
     *
     * @param image image to add.
     */
    public void putImage(BufferedImage image) {
        BufferedImage[] frames = images.get(currentSectionNumber);
        if (frames == null) {
            images.put(currentSectionNumber, new BufferedImage[] {image});
        } else {
            // Find the first null frame and fill it with given image.
            int i = 0;
            for (; i < frames.length; i++) {
                if (frames[i] == null) {
                    break;
                }
            }
            if (i == frames.length) {
                images.put(currentSectionNumber, new BufferedImage[] {image});
            } else {
                frames[i] = image;
            }
        }
    }

    /**
     * @return the first image of current active section.
     */
    public BufferedImage getImage() {
        return getImage(0);
    }

    /**
     * @param frame frame number.
     *
     * @return
     */
    public BufferedImage getImage(int frame) {
        BufferedImage[] frames = images.get(currentSectionNumber);
        return frames != null ? frames[frame] : null;
    }

    public int getFramesNumber() {
        return getFramesNumber(currentSectionNumber);
    }

    public int getFramesNumber(int section) {
        BufferedImage[] frames = images.get(section);
        return frames != null ? frames.length : 0;
    }

    public void matrix3d(double[][] m) {
        matrix3d.put(currentSectionNumber, m);
    }

    public double[][] matrix3d() {
        return matrix3d.get(currentSectionNumber);
    }

    public void xyzCube(double[][] cube) {
        xyzCube.put(currentSectionNumber, cube);
    }

    public double[][] xyzCube() {
        double[][] cube = xyzCube.get(currentSectionNumber);
        if (cube == null) {
            cube = new double[8][3];
            xyzCube(cube);
            return xyzCube.get(currentSectionNumber);
        }
        return cube;
    }

    public int currentSectionNum() {
        return currentSectionNumber;
    }

    public void currentSectionNum(int sect) {
        currentSectionNumber = sect;
    }

    /**
     * @return Vector with files names.
     */
    public VectorS getUploadedFilesListing() {
        String[] listing = getUploadedFilesListingStr();
        if (listing == null) {
            return new VectorS(new Element[] {new Fname("User  directory  doesn't  exists.")});
        }
        int size = listing.length;
        Element[] tmp = new Element[size];
        for (int i = 0; i < size; i++) {
            tmp[i] = new Fname(listing[i]);
        }
        return new VectorS(tmp);
    }

    public String[] getUploadedFilesListingStr() {
        String[] list = userUploadDir.list();
        return list == null ? new String[] {} : list;
    }

    public boolean removeFile(final String filename) {
        File[] files = userUploadDir.listFiles(
                new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(filename);
            }
        });
        if (files != null && files.length == 1) {
            return files[0].isFile() && files[0].delete();
        }
        return false;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isUserLoggedIn() {
        return user != null;
    }

    /**
     * @return Logged in user ID or -1 if {@code user} is null.
     */
    public long getUserId() {
        return user != null ? user.getId() : -1;
    }

    /**
     * @param rhs rhs expression
     *
     * @return original rhs expression or rhs expression with \fromFile command
     * replaced with file content.
     */
    private String tryLoadFromFileInRhs(String rhs) { int fromFileIdx=-1;
     try{   fromFileIdx = rhs.indexOf(F.FUNC_NAMES[F.FROMFILE]);       }
     catch( NullPointerException ex){return rhs;}
        if (fromFileIdx == -1) {return rhs;}
//        int openQuoteIdx = rhs.indexOf('\'', fromFileIdx + 1);
//        int closeQuoteIdx = rhs.indexOf('\'', openQuoteIdx + 1);
        int openParenIdx = rhs.indexOf('(', fromFileIdx);
        int closeParenIdx = FUtils.posOfPairBracket(rhs, openParenIdx, '(', ')');
        if ( //openQuoteIdx == -1 || closeQuoteIdx <= openParenIdx
                // || 
                openParenIdx == -1 || closeParenIdx <= openParenIdx) {
            throw new RuntimeException("Syntax error at: " + rhs);
        }
        String fromFileCommand = rhs.substring(fromFileIdx - 1, closeParenIdx + 1);
        //  String filename = rhs.substring(openQuoteIdx + 1, closeQuoteIdx);
        String filename = rhs.substring(openParenIdx + 1, closeParenIdx).trim();
        return rhs.replace(fromFileCommand, rhsFromFile(filename));
    }

    /**
     * Loads rhs Mathpar expression from file.
     *
     * @param filename
     *
     * @return
     */
    private String rhsFromFile(String filename) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(
                    new File(userUploadDir, filename), CHARSET_DEFAULT);
        } catch (FileNotFoundException fnf) {
            throw new RuntimeException("File \"" + filename
                    + "\" not found. Check if the file was uploaded and you've typed correct name.", fnf);
        } catch (IOException ex) {
            LOG.error("Error loading object from file " + filename, ex);
            throw new RuntimeException("Error loading object from file " + filename, ex);
        }
    }

    private void toFile(F f) {
        String objNameOrig = f.X[0].toString(ring);
        Element objName = expandFnameOrId(f.X[0]);
        replaceOfSymbols(objName, expr);
        if (!(objName instanceof Fname)) {
            throw new RuntimeException("Can't get value from: " + objNameOrig);
        }
        Fname obj = (Fname) objName;
        String filename = ((Fname) expandFnameOrId(f.X[1])).name;
        try {
            FileUtils.write(new File(userUploadDir, filename), obj.X[0].toString(ring), CHARSET_DEFAULT);
        } catch (IOException ex) {
            throw new MathparException("Error writing to file '" + filename + "'", ex);
        }
    }

    /**
     * @param cfPage
     * @param fn
     * @param mass
     * @param i
     *
     * @return {@code true} if {@code fn} was an expression to set a constant
     * (like "FLOATPOS = ...;").
     */
    private boolean trySetConstants(CanonicForms cfPage, Fname fn, int[] mass, int i) {
        String fnName = fn.name;
        if (fnName.equals("SPACE")) {
            try {
                setRing(((Fname) fn.X[mass[i + 1]]).name);
            } catch (Exception ex) {
                ring.exception.append("\nError in SPACE declaration");
                throw ex;
            }
        } else if (fnName.equals("FLOATPOS")) {
            try {
                int tt = (int) ((NumberZ64) (fn.X[mass[i + 1]])).value;
                ring.setFLOATPOS(tt);
            } catch (Exception ex) {
                ring.exception.append("\nError in FLOATPOS number");
                throw ex;
            }
        } else if (fnName.equals("SMALLESTBLOCK")) {
            try {
                int tt = (int) ((NumberZ64) (fn.X[mass[i + 1]])).value;
                ring.setSmallestBlock(tt);
            } catch (Exception ex) {
                ring.exception.append("\nError in SMALLESTBLOCK number");
                throw ex;
            }

        } else if (fnName.equals("MOD")) {
            try {
                ring.setMOD((NumberZ) fn.X[mass[i + 1]]);
            } catch (Exception ex) {
                ring.exception.append("\nError in MOD number");
                throw ex;
            }
        } else if (fnName.equals("MOD32")) {
            try {
                ring.setMOD32((int) ((NumberZ64) fn.X[mass[i + 1]]).value);
            } catch (Exception ex) {
                ring.exception.append("\nError in MOD32 number");
                throw ex;
            }
        } else if (fnName.equals("MachineEpsilonR")) {
            try {
                ring.constR = null;// при смене точности меняются и константы в этом поле, поэтому мы его и обнуляем
                Fraction ff = (Fraction) fn.X[mass[i + 1]];
                ring.setMachineEpsilonR((int) ((NumberZ64) ff.num).value);
                ring.setAccuracy((int) ((NumberZ64) ff.denom).value);
            } catch (Exception ex) {
                ring.exception.append("\nError in MachineEpsilonR number");
                throw ex;
            }
        } else if (fnName.equals("MachineEpsilonR64")) {
            try {
                ring.setMachineEpsilonR64((int) ((NumberZ64) (fn.X[mass[i + 1]])).value);
            } catch (Exception ex) {
                ring.exception.append("\nError in MachineEpsilonR64 number");
                throw ex;
            }
        } else if (fnName.equals("RADIAN")) {
            try {
                ring.setRADIAN(fn.X[mass[i + 1]]);
            } catch (Exception ex) {
                ring.exception.append("\nError in RADIAN number");
                throw ex;
            }
        } else if (fnName.equals("STEPBYSTEP")) {
            try {
                ring.setSTEPBYSTEP(fn.X[mass[i + 1]]);
            } catch (Exception ex) {
                ring.exception.append("\nError in STEPBYSTEP number");
                throw ex;
            }
        } else if (fnName.equals("SUBSTITUTION")) {
            try {
                ring.setSUBSTITUTION(fn.X[mass[i + 1]]);
            } catch (Exception ex) {
                ring.exception.append("\nError in SUBSTITUTION number");
                throw ex;
            }
        } else if (fnName.equals("EXPAND")) {
            try {
                ring.setEXPAND(fn.X[mass[i + 1]]);
            } catch (Exception ex) {
                ring.exception.append("\nError in EXPAND number");
                throw ex;
            }
        } else if (fnName.equals("\\TOTALNODES")) {
            try {
                ring.setTOTALNODES(fn.X[mass[i + 1]].intValue());
            } catch (Exception ex) {
                ring.exception.append("\nError in TOTALNODES number");
                throw ex;
            }
        } else if (fnName.equals("\\PROCPERNODE")) {
            try {
                ring.setPROCPERNODE(fn.X[mass[i + 1]].intValue());
            } catch (Exception ex) {
                ring.exception.append("\nError in PROCPERNODE number");
                throw ex;
            }
        } else if (fnName.equals("\\MAXCLUSTERMEMORY")) {
            try {
                ring.setMAXCLUSTERMEMORY(fn.X[mass[i + 1]].intValue());
            } catch (Exception ex) {
                ring.exception.append("\nError in MAXCLUSTERMEMORY number");
                throw ex;
            }
        } else if (fnName.equals("\\CLUSTERTIME")) {
            try {
                ring.setCLUSTERTIME(fn.X[mass[i + 1]].intValue());
            } catch (Exception ex) {
                ring.exception.append("\nError in CLUSTERTIME number");
                throw ex;
            }
        } else if (fnName.equals("TIMEOUT")) {
            try {
                int newTimeout = fn.X[mass[i + 1]].intValue();
                if (newTimeout < 1 || newTimeout > 6000) {
                    throw new IllegalArgumentException();
                }
                ring.setTimeout(newTimeout);
            } catch (Exception ex) {
                throw new MathparException("TIMEOUT must be an integer at the range of 1 to "
                        + Ring.TIMEOUT_MAX + " seconds.", ex);
            }
        } else if (fnName.equals("ENTERING_FACTOR_IN_LOG")) {
            try {
                ring.setENTERING_FACTOR_IN_LOG(fn.X[mass[i + 1]]);
            } catch (Exception ex) {
                ring.exception.append("\nError in ENTERING_FACTOR_IN_LOG number");
                throw ex;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @param s
     */
    public void setRing(String s) {
        ring = new Ring(s, ring);
        ring.CForm = new CanonicForms(ring, true);
        ring.page = this;
    }

    /**
     *
     * @param r
     */
    public void setRing(Ring r) {
        NumberR[] consts = ring.constR;
        ring = r;
        ring.constR = consts;
        ring.CForm = new CanonicForms(ring, true);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRING= ").append(ring).append(";\n");
        StringBuffer[] section = data.section;
        if (section[0].length() != 0) {
            sb.append("\nin ").append(":\n").append(section[0]);
        }
        if (section[1].length() != 0) {
            sb.append("\nout:\n").append(section[1]);
        }
        return sb.toString();
    }
} // trySetConst можно дважды пользовать если добавить out !!!
