package com.mathpar.number;

import java.util.*;

import com.mathpar.number.math.*;
import com.mathpar.parallel.webCluster.engine.AlgorithmsConfig;
import com.mathpar.polynom.*;
import com.mathpar.func.CanonicForms;
import com.mathpar.func.FuncNumberR;
import com.mathpar.func.Page;

/**
 * <p>
 * Title: Класс Ring</p>
 *
 * <p>
 * Description: since ParCA1.1</p> Ring это класс, определяющий алгебраическое
 * пространство текущих переменных. Он определяет: <br> 1) Количество различных
 * числовых множества, в которых определены <br> -- текущие переменные (int
 * <B>algebraNum</B>). <br> 2) Массив типов числовых множеств:
 * <B>algebra</B> = new int[algebraNumb]; <br> 3) Количеств переменных в каждом
 * числовом множестве: <B>varLastIndices</B> = new int[algebraNumb]; <br> 4)
 * Имена переменных: <B>varNames</B> = new String[(SUM for all j of
 * varLastIndices[j])];
 * <br> 5) Массив полиномов, каждый из которых представляет соответствующую
 * текущую <br> -- переменную, как однородный полином первой степени: varPolynom
 * = new Polynom[0]. <br> 6) Поле StringBuffer в котором сохраняются все
 * сообщения о замеченных ошибках <B>exception</B> <br> 7) ACCURACY - число
 * десятичных позиций, которые определяют точность вычислений в чиловом классе
 * NumberR (for default=int 20) <br> 8) MachineEpsilonR - машинный ноль для
 * NumberR. <br> 9) MachineEpsilonR64 - машинный ноль для NumberR64. <br> 10)
 * FLOATPOS -- число десятичных позиций, которые должны появиться при выводе
 * чисел типа NumberR64 или NumberR (for defoult=int 2). <br> 11) MOD64 -
 * простое число типа long, являющееся модулем в классе NumberZp64. <br> 12) MOD
 * - простое число типа NumberZ, являющееся модулем в классе NumberZp. <br> 13)
 * numberONE - единица основной алгебры algebra[0]. <br> 14) numberMINUS_ONE -
 * минус единица основной алгебры algebra[0]. <br> 15) numberZERO - единица
 * основной алгебры algebra[0].
 */
public final class Ring implements java.io.Serializable {
    static final long serialVersionUID = 10000020151030L;
    /**
     * Flags field for multiplication and summation of matrices
     */
    public int[] flags = new int[0];
    public Page page = null;
    /**
     * Количество различных числовых множества для текущих переменных
     */
    public int algebraNumb = 1;
    /**
     * Числовые множества, в которых определены текущие переменные(см. первые 16
     * типов колец). Число элементов массива algebra -- это количество различных
     * числовых множеств Первое из них считается главным (наиболее широким) и к
     * нему приводятся все остальные (при необходимости)
     */
    public int[] algebra = new int[algebraNumb];
    /**
     * Индексы последних переменных в каждом числовом множестве
     */
    public int[] varLastIndices = new int[algebraNumb];
    /**
     * Имена текущих переменных
     */
    public String[] varNames = new String[0];
    /**
     * current  CanonicForms object
     * Each new operator of CanonicForm must put it here
     * And you must do it free if you not need CanonicForm
     */
    public CanonicForms CForm = null;
    /**
     * Число десятичных позиций, задающих точность вычислений в NumberR
     */
    public int ACCURACY = 34;
    /**
     * MathContext класс, определящий точность в NumberR, вычисляется по
     * ACCURACY
     */
    public MathContext MC = MathContext.DECIMAL128;
    /**
     * Объект для хранения шагов трассировки
     */
    public ArrayList<Element> traceSteps = new ArrayList<Element>();
    public static double MachineEpsilonFixed = 2.22044604925031300e-16;
        // least possible value for R64

    static {MachineEpsilonR64Initial = new NumberR64((new NumberZ64(36)).get2inPowMinThis());}

    public static MathContext MCinitial = MathContext.DECIMAL128; // 34 decimal digits
    public static NumberR64 MachineEpsilonR64Initial;
    /**
     * машинный ноль
     */
    public NumberR MachineEpsilonR = new NumberR("1.0e-29"); // 34-5

    /**
     * машинный ноль
     */
    public void cleanRing() {
        page = null;
        CForm = null;
    }

    public int MachineEpsilonR64bits = 36; // for 2^{-36}
    public NumberR64 MachineEpsilonR64 = MachineEpsilonR64Initial;

    public void setMachineEpsilonR64(int n) {
        MachineEpsilonR64bits = n;
        MachineEpsilonR64 = new NumberR64((new NumberZ64(n)).get2inPowMinThis());
    }

    /**
     * Число десятичных позиций, которые должны появится при выводе чисел типа
     * NumberR64 или NumberR (for default=int 2) Это число используется для
     * указания точных десятичных цифр после запятой, которые учитываются при
     * факторизации полинома, остальные цифры зануляются.
     */
    public int FLOATPOS = 2;
    /**
     * Простое число, являющееся модулем в классе NumberZp
     */
    public NumberZ MOD = NumberZ.PRIME268435399;
    /**
     * делитель, который использовался в последней операции деления
     */
    public long divizor32 = 1L;
    /**
     * Число обратное к числу divizor по модулю MOD32
     * <p>
     * Эти поля приготовлены для того, чтобы хранить последнее обратное
     * и использовать в операции деления по модулю. Обычно одно и тоже
     * число многократно подряд используется в качастве делимого.
     */
    public long invDivisor32 = 1L;
    /**
     * Простое число, являющееся модулем в классе NumberZp32
     */
    public long MOD32 = 268435399L;
    /**
     * Признак измерения углов в радианах
     */
    public NumberZ64 RADIAN = Element.TRUE;
    /**
     * Признак пошагового вывода результатов вычислений
     */
    public NumberZ64 STEPBYSTEP = Element.FALSE;

    /**
     * Признак подстановки внесения множителя под знак логарифма выше
     */
    public NumberZ64 ENTERING_FACTOR_IN_LOG = Element.FALSE;

    /**
     * Признак группировки отрицательных слагаемых в сумме
     */
    public NumberZ64 GROUP_NEGATIVE_TERMS = Element.TRUE;
    /**
     * Признак подстановки во входных выражениях значений переменных заданных
     * выше
     */
    public NumberZ64 SUBSTITUTION = Element.TRUE;
    /**
     * Признак раскрытия скобок во входных выражениях
     */
    public NumberZ64 EXPAND = Element.TRUE;
    /**
     * Timeout in seconds.
     */
    public int TIMEOUT = 15;
    /**
     * Maximal timeout in seconds.
     */
    public static final int TIMEOUT_MAX = 6000;
    /**
     * Maximal cluster time for current task.
     */
    public int CLUSTERTIME = 1;
    /**
     * Total number of processors for current task
     */
    public int TOTALNODES = 1;
    /**
     * Number of kernels per node for current task
     */

    public int PROCPERNODE = 2;
    /**
     * Maximal memory on cluster node
     * CNF_MAX_TOTAL_NODES = Integer.parseInt(CONFIG.getString("s.npMax"));
     */
    public int MAXCLUSTERMEMORY = AlgorithmsConfig.CNF_MAX_MEMORY;

    /**
     * Массив полиномов, каждый из которых представляет соответствующую текущую
     * переменную, как однородный полином первой степени с коеффициентом 1.
     */
    public Polynom[] varPolynom = new Polynom[0];

    /**
     * constant of main algebra
     */

    public Polynom polynomONE = com.mathpar.polynom.Polynom.polynom_one(NumberZ.ONE);
        // Type of algebra[0]

    /**
     * constant of main algebra
     */
    public Element numberONE = NumberZ.ONE;       // Type of algebra[0]
    /**
     * constant of main algebra
     */
    public Element numberZERO = NumberZ.ZERO;      // Type of algebra[0]

    /**
     * constant90 of main algebra
     */
    public Element number90 = NumberZ.Z_90;      // Type of algebra[0]
    /**
     * constant 180 of main algebra
     */
    public Element number180 = NumberZ.Z_180;     // Type of algebra[0]
    /**
     * constant of main algebra
     */
    public Element numberMINUS_ONE = com.mathpar.number.NumberZ.MINUS_ONE; // Type of algebra[0]
    /**
     * constant of main algebra
     */
    public Element numberI = com.mathpar.number.Complex.CZ_I;
        // Type of algebra[0] or Complex(algebra[0])
    /**
     * constant of main algebra
     */
    public Element numberMINUS_I = com.mathpar.number.Complex.CZ_MINUS_I;
    ;   // Type of algebra[0] or Complex(algebra[0])

    /**
     * Граничный порядок матрицы (MatrrixS), после которого, (когда строго меньше)
     * умножение вычисляется не рекурсивной процедурой (не параллелится),
     * а вычисляется последовательной процедурой.
     */
    public int SMALLESTBLOCK = 64;

    /**
     * Младшие натуральные числа в основной алгебре. Количество их -- константа
     * в классе Element
     */
    public Element[] posConst = NumberZ.POSCONST;
    public Element[] negConst = NumberZ.negConst;
    /**
     * Массив констант посчитанный при определенной ACCURACY: 1- e ; 2 - pi ; 3
     * - pi/180, 4 - 180/pi.
     */
    public NumberR[] constR;

    /**
     * Строка, которая хранит все текущие сообщения об ошибках
     */
    public StringBuffer exception = new StringBuffer();

    public Ring() {}

    public Ring(int[] algebra, int[] varLastIndices, String[] varNames) {
        this.algebra = algebra;
        this.varLastIndices = varLastIndices;
        this.varNames = varNames;
        this.algebraNumb = algebra.length;
        createVarPolynoms();
    }

    public final void setDefaulRing() {
        FLOATPOS = 2;
        ACCURACY = 34;
        MachineEpsilonR = new NumberR("10E-29");
        MachineEpsilonR64 = MachineEpsilonR64Initial;
        MC = MCinitial;
        MOD = NumberZ.PRIME268435399;
        MOD32 = 268435399L;
        RADIAN = Element.TRUE;
        STEPBYSTEP = Element.FALSE;
        SUBSTITUTION = Element.TRUE;
        EXPAND = Element.TRUE;
        SMALLESTBLOCK = 64;
        createVarPolynoms();
    }

    /**
     * Заполняется динамическое поле varPolynom класса Ring: создается массив
     * полиномов, varPolynom каждый из которых хранит одну переменную в первой
     * степени. Заполняет поля numberONE, numberZERO, numberMINUS_ONE
     */
    public void createVarPolynoms() {
        int i = varNames.length;
        varPolynom = new Polynom[i];
        int alg = algebra[0];
        Element one = oneOfType(algebra[0]);
        for (int j = 0; j < i; j++) {
            int[] pow = new int[j + 1];
            pow[j] = 1;
            varPolynom[j] = new Polynom(pow, new Element[]{one});
        }
        numberONE = one;
        boolean flag = (alg & Complex) == Complex;
        Element zero = zeroOfType(algebra[0]);
        numberZERO = ((flag) && ((zero.numbElementType() & Complex) != Complex)) ?
            new Complex(zero, zero) :
            zero;
        numberMINUS_ONE = (flag) ?
            new Complex(((Complex) numberONE).re.myMinus_one(), ((Complex) numberONE).re.myZero()) :
            one.myMinus_one();
        polynomONE = new Polynom(new int[0], new Element[]{one});
        if (flag) {
            Element reOne = oneOfType(algebra[0] - Complex);
            Element reZero = reOne.myZero();
            numberI = new Complex(reZero, reOne);
            numberMINUS_I = new Complex(reZero, reOne.myMinus_one());
        } else {
            numberMINUS_I = new Complex(numberZERO, numberMINUS_ONE);
            numberI = new Complex(numberZERO, numberONE);
        }

        posConst = one.posConst();
        negConst = one.negConst();
        //   for (int k = 1; k <= Element.MAX_CONSTANT; k++)
        number90 = (algebra[0] == Ring.R64) ? new NumberR64(90.0) : new NumberZ(90);
        number180 = (algebra[0] == Ring.R64) ? new NumberR64(180.0) : new NumberZ(180);
    }

    /**
     * Замена алгебры на новый тип (все беретсмя из старой алгебры)
     *
     * @param ring       - Текущее кольцо
     * @param newAlgebra - новый тип главной алгебры
     */
    public Ring(Ring ring, int newAlgebra) {
        this.CForm = ring.CForm;
        this.varNames = new String[ring.varNames.length];
        System.arraycopy(ring.varNames, 0, this.varNames, 0, ring.varNames.length);
        int algebra1[] = new int[ring.algebra.length];
        this.algebraNumb = ring.algebraNumb;
        System.arraycopy(ring.algebra, 1, algebra1, 1, ring.algebra.length - 1);
        algebra1[0] = newAlgebra;
        this.varLastIndices = new int[ring.varLastIndices.length];
        System.arraycopy(
            ring.varLastIndices,
            0,
            this.varLastIndices,
            0,
            ring.varLastIndices.length
        );
        this.algebra = algebra1;
        this.varLastIndices = ring.varLastIndices;
        this.ACCURACY = ring.ACCURACY;
        this.FLOATPOS = ring.FLOATPOS;
        this.MachineEpsilonR64 = ring.MachineEpsilonR64;
        this.MachineEpsilonR = ring.MachineEpsilonR;
        this.MC = ring.MC;
        this.MOD = ring.MOD;
        this.MOD32 = ring.MOD32;
        this.RADIAN = ring.RADIAN;
        this.STEPBYSTEP = ring.STEPBYSTEP;
        this.SUBSTITUTION = ring.SUBSTITUTION;
        this.EXPAND = ring.EXPAND;
        this.SMALLESTBLOCK = ring.SMALLESTBLOCK;
        this.exception = ring.exception;
        createVarPolynoms();
    }

    /**
     * @param algebra числовой тип алгебры
     * @param varNumb число переменных в кольце
     */
    public Ring(int algebra, int varNumb) {
        varPolynom = new Polynom[varNumb];
        varNames = new String[varNumb];
        String[] varNamesTmp = new String[varNumb];
        for (int i = 0; i < varNumb; i++) {
            int[] pow = new int[i + 1];
            pow[i] = 1;
            varPolynom[i] = new Polynom(pow, new Element[]{numberONE});
            switch (i) {
                case 0:
                    varNames[i] = "x";
                    break;
                case 1:
                    varNames[i] = "y";
                    break;
                case 2:
                    varNames[i] = "z";
                    break;
                default:
                    varNames[i] = "z_" + (i - 2);
            }
        }
    }

    /**
     * We make this ring like Argument Ring.
     * But our CForm has pointer to the Argument(not to the new ring)
     *
     * @param ring -
     */
    public Ring(Ring ring) {
        CForm = ring.CForm;
        page = ring.page;
        this.varNames = new String[ring.varNames.length];
        System.arraycopy(ring.varNames, 0, this.varNames, 0, ring.varNames.length);
        this.algebra = new int[ring.algebra.length];
        this.algebraNumb = ring.algebraNumb;
        System.arraycopy(ring.algebra, 0, this.algebra, 0, ring.algebra.length);
        this.varLastIndices = new int[ring.varLastIndices.length];
        System.arraycopy(
            ring.varLastIndices,
            0,
            this.varLastIndices,
            0,
            ring.varLastIndices.length
        );
        this.ACCURACY = ring.ACCURACY;
        this.FLOATPOS = ring.FLOATPOS;
        this.MachineEpsilonR64 = ring.MachineEpsilonR64;
        this.MachineEpsilonR = ring.MachineEpsilonR;
        this.MC = ring.MC;
        this.MOD = ring.MOD;
        this.MOD32 = ring.MOD32;
        this.RADIAN = ring.RADIAN;
        this.STEPBYSTEP = ring.STEPBYSTEP;
        this.SUBSTITUTION = ring.SUBSTITUTION;
        this.TOTALNODES = ring.TOTALNODES;
        this.PROCPERNODE = ring.PROCPERNODE;
        this.MAXCLUSTERMEMORY = ring.MAXCLUSTERMEMORY;
        this.CLUSTERTIME = ring.CLUSTERTIME;
        this.TIMEOUT = ring.TIMEOUT;
        this.ENTERING_FACTOR_IN_LOG = ring.ENTERING_FACTOR_IN_LOG;
        this.GROUP_NEGATIVE_TERMS = ring.GROUP_NEGATIVE_TERMS;
        this.EXPAND = ring.EXPAND;
        this.SMALLESTBLOCK = ring.SMALLESTBLOCK;
        this.varPolynom = new Polynom[ring.varPolynom.length];
        System.arraycopy(ring.varPolynom, 0, this.varPolynom, 0, ring.varPolynom.length);
        this.numberONE = ring.numberONE;
        this.numberZERO = ring.numberZERO;
        this.numberMINUS_ONE = ring.numberMINUS_ONE;
        this.polynomONE = ring.polynomONE;
        this.numberI = ring.numberI;
        this.numberMINUS_I = ring.numberMINUS_I;
        this.number90 = ring.number90;
        this.number180 = ring.number180;
        this.constR = ring.constR;
        this.exception = ring.exception;
    }

    /**
     * Создание нового кольца по старому и вспомогательному массиву новых
     * переменных. Типы всех алгебр сохраняются.
     *
     * @param R      - текущее кольцо
     * @param newVar -- вспомогательному массиву новых переменных в первых позициях он
     *               хранит номера переменных, которые надо оставить, а затем стоят (-1)
     */
    public Ring(Ring R, int[] newVar) {
        CForm = R.CForm;
        page = R.page;
        int numbNewVar = 0; // столько переменных останется
        for (int i = 0; i < newVar.length; i++) {
            if (newVar[numbNewVar] != -1) {numbNewVar++; }
        }// подсчитали сколько их
        String vars[] = new String[numbNewVar];
        for (int j = 0; j < numbNewVar; j++) {vars[j] = R.varNames[newVar[j]]; } // перенесли имена
        int numbOfAlg = R.varLastIndices.length;
        int varNum[] = new int[numbOfAlg];
        int s = 0;
        int i = 0;
        for (; s < numbOfAlg; s++) {
            while ((i < numbNewVar) && (R.varLastIndices[s] > newVar[i])) {i++;}
            varNum[s] = i;
            if (i >= numbNewVar) { break; }
        }
        int newNumbOfAlg = 0;
        for (int j = 0; j < numbOfAlg; j++) {newNumbOfAlg++;}
        int varNumbs1[] = new int[newNumbOfAlg];
        int algebra1[] = new int[newNumbOfAlg];
        varNumbs1[0] = varNum[0];
        algebra1[0] = R.algebra[0];
        int k = 0;
        for (int j = 1; j < numbOfAlg; j++) {
            if (varNumbs1[k] != varNum[j]) {
                varNumbs1[k] = varNum[j];
                algebra1[k] = R.algebra[j];
                k++;
            }
        }
        this.algebra = algebra1;
        this.varLastIndices = varNumbs1;
        this.varNames = vars;
        this.ACCURACY = R.ACCURACY;
        this.FLOATPOS = R.FLOATPOS;
        this.MachineEpsilonR64 = R.MachineEpsilonR64;
        this.MachineEpsilonR = R.MachineEpsilonR;
        this.MC = R.MC;
        this.MOD = R.MOD;
        this.MOD32 = R.MOD32;
        this.RADIAN = R.RADIAN;
        this.STEPBYSTEP = R.STEPBYSTEP;
        this.SUBSTITUTION = R.SUBSTITUTION;
        this.EXPAND = R.EXPAND;
        this.SMALLESTBLOCK = R.SMALLESTBLOCK;
        this.varPolynom = new Polynom[numbNewVar];
        Element one = oneOfType(algebra[0]);
        for (int j = 0; j < numbNewVar; j++) {
            int[] pow = new int[j + 1];
            pow[j] = 1;
            varPolynom[j] = new Polynom(pow, new Element[]{one});
        }
    }

    /**
     * Clone of ring without CForm.
     * CForm need not for arithmetic operations.
     * If you need CForm you have to create it by hand.
     *
     * @param ring - Ring
     * @return cloneWithoutCFormPage of input ring
     */
    public static Ring cloneWithoutCFormPage(Ring ring) {
        Ring ringNew = new Ring(ring);
        ringNew.CForm = null;
        ringNew.page = null;
        return ringNew;
    }

    /**
     * Ring Z[x,y,z] is the default-Z-ring
     */
    public static final Ring ringZxyz
        = new Ring(new int[]{Ring.Z}, new int[]{2}, new String[]{"x", "y", "z"});
    /**
     * Ring R64[x,y,z,t] is the default-R64-ring
     */
    public static final Ring ringR64xyzt
        = new Ring(new int[]{Ring.R64}, new int[]{3}, new String[]{"x", "y", "z", "t"});
    public static final Ring ringZpX
        = new Ring(new int[]{Ring.Zp}, new int[]{0}, new String[]{"x"});
    public static final Ring ringZp32X
        = new Ring(new int[]{Ring.Zp32}, new int[]{0}, new String[]{"x"});

    public static final Ring getRingXYZTofType(int ringType) {
        return
            new Ring(new int[]{ringType}, new int[]{3}, new String[]{"x", "y", "z", "t"});
    }

    public static final Ring getRingXYofType(int ringType) {
        return
            new Ring(new int[]{ringType}, new int[]{1}, new String[]{"x", "y"});
    }

    /**
     * Ring Z[x,y,z]Z[u,v,w] is the default-Z-Z-ring
     */
    public static final Ring ringZxyzZuvw
        = new Ring(new int[]{Ring.Z, Ring.Z},
                   new int[]{2, 5},
                   new String[]{"x", "y", "z", "u", "v", "w"}
    );

    public Ring(String ringStr, Ring ring) {
        this(ringStr);
        CForm = ring.CForm;
        page = ring.page;
        this.ACCURACY = ring.ACCURACY;
        this.MC = ring.MC;
        this.FLOATPOS = ring.FLOATPOS;
        this.SMALLESTBLOCK = ring.SMALLESTBLOCK;
        this.MOD = ring.MOD;
        this.MOD32 = ring.MOD32;
        this.MachineEpsilonR = ring.MachineEpsilonR;
        this.MachineEpsilonR64 = ring.MachineEpsilonR64;
        this.RADIAN = ring.RADIAN;
        this.STEPBYSTEP = ring.STEPBYSTEP;
        this.SUBSTITUTION = ring.SUBSTITUTION;
        this.EXPAND = ring.EXPAND;
        this.TOTALNODES = ring.TOTALNODES;
        this.PROCPERNODE = ring.PROCPERNODE;
        this.MAXCLUSTERMEMORY = ring.MAXCLUSTERMEMORY;
        this.CLUSTERTIME = ring.CLUSTERTIME;
        this.TIMEOUT = ring.TIMEOUT;
        this.ENTERING_FACTOR_IN_LOG = ring.ENTERING_FACTOR_IN_LOG;
        this.GROUP_NEGATIVE_TERMS = ring.GROUP_NEGATIVE_TERMS;
        this.constR = ring.constR;
        this.exception = ring.exception;
    }

    /**
     * Создание кольца от строки
     *
     * @param ringStr -- текстовый вид кольца
     */
    public Ring(String ringStr) {
        try {
            StringBuffer str = new StringBuffer(ringStr);
            int pos1, pos2;
            StringBuffer str1;
            ArrayList<String> varN = new ArrayList<String>();
            IntList varCount = new IntList();
            IntList nameA = new IntList();
            int k = 0;
            for (int i = 0; i < str.length(); i++) {
                pos1 = str.indexOf("[");
                pos2 = str.indexOf("]");
                String nameOfRing = str.substring(0, pos1);
                str1 = new StringBuffer(str.substring(pos1 + 1, pos2));
                String[] vars = getVarsNames(str1);
                for (int j = 0; j < vars.length; j++) {
                    varN.add(j + k, vars[j]);
                }
                k = k + vars.length;
                varCount.add(k - 1);
                nameA.add(ringNumbByName.get(nameOfRing));
                str = new StringBuffer(str.substring(pos2 + 1, str.length()));
            }
            varNames = new String[varN.size()];
            varLastIndices = new int[varCount.size];
            algebra = new int[nameA.size];
            for (int i = 0; i < varN.size(); i++) {varNames[i] = varN.get(i);}
            System.arraycopy(varCount.arr, 0, varLastIndices, 0, varCount.size);
            System.arraycopy(nameA.arr, 0, algebra, 0, nameA.size);
            algebraNumb = algebra.length;
            createVarPolynoms();
            CForm = new CanonicForms(this, true);
        } catch (Exception ee) {
            exception.append("Wrong SPACE declaration:").append(ee);
        }
    }

    /**
     * Создание кольца добавлением новых переменных
     *
     * @param ringStr -- текстовый вид кольца
     */
    public Ring addVariables(String[] newNames) {
        Ring my = new Ring();
        int newNumb = newNames.length;
        int newVarNubm = varNames.length + newNumb;
        try {
            my.varNames = new String[newVarNubm];
            my.varLastIndices = new int[varLastIndices.length];
            System.arraycopy(varLastIndices, 0, my.varLastIndices, 0, varLastIndices.length);
            System.arraycopy(varNames, 0, my.varNames, 0, varNames.length);
            System.arraycopy(newNames, 0, my.varNames, varNames.length, newNames.length);
            my.varLastIndices[varLastIndices.length - 1] += newNumb;
            my.algebra = algebra;
            my.algebraNumb = algebraNumb;
            my.varPolynom = new Polynom[newVarNubm];
            for (int j = 0; j < newVarNubm; j++) {
                int[] pow = new int[j + 1];
                pow[j] = 1;
                my.varPolynom[j] = new Polynom(pow, new Element[]{numberONE});
            }
            my.CForm = CForm;
            my.page = page;
            my.ACCURACY = ACCURACY;
            my.MC = MC;
            my.FLOATPOS = FLOATPOS;
            my.SMALLESTBLOCK = SMALLESTBLOCK;
            my.MOD = MOD;
            my.MOD32 = MOD32;
            my.MachineEpsilonR = MachineEpsilonR;
            my.MachineEpsilonR64 = MachineEpsilonR64;
            my.RADIAN = RADIAN;
            my.STEPBYSTEP = STEPBYSTEP;
            my.SUBSTITUTION = SUBSTITUTION;
            my.EXPAND = EXPAND;
            my.TOTALNODES = TOTALNODES;
            my.PROCPERNODE = PROCPERNODE;
            my.MAXCLUSTERMEMORY = MAXCLUSTERMEMORY;
            my.CLUSTERTIME = CLUSTERTIME;
            my.TIMEOUT = TIMEOUT;
            my.ENTERING_FACTOR_IN_LOG = ENTERING_FACTOR_IN_LOG;
            my.GROUP_NEGATIVE_TERMS = GROUP_NEGATIVE_TERMS;
            my.constR = constR;
            my.exception = exception;
            my.numberONE = numberONE;
            my.numberZERO = numberONE;
            my.numberMINUS_ONE = numberMINUS_ONE;
            my.polynomONE = polynomONE;
            my.numberI = numberI;
            my.numberMINUS_I = numberMINUS_I;
            my.posConst = posConst;
            my.negConst = negConst;
            my.number90 = number90;
            my.number180 = number180;
            my.numberZERO = numberZERO;
            my.numberMINUS_ONE = numberMINUS_ONE;
            my.polynomONE = polynomONE;
        } catch (Exception ee) {
            exception.append("Wrong SPACE addition with new vars:").append(ee);
        }
        return my;
    }

    /**
     * Number of Tropical algebras for one Numerical ordered set
     */
    public final static int TropNumber = 6;
    //собственный тип для +\- бесконечности и неопределенностей  Element
    public final static int INFTYorNAN = 0;
    //Числовые множества:
    public final static int Z64 = 1;
    public final static int Zp32 = 2;  // это тоже конечное поле GCD==1 для всех чисел
    public final static int Z = 3;
    public final static int ZMaxPlus = Z + 1;
    public final static int ZMinPlus = Z + 2;
    public final static int ZMaxMult = Z + 3;
    public final static int ZMinMult = Z + 4;
    public final static int ZMaxMin = Z + 5;
    public final static int ZMinMax = Z + 6;
    public final static int Q = 10;//    ЭТО ГРАНИЦА ДЛЯ GCD функции !!
    // -----------------------------------------------------------------------
    public final static int Zp = 11; // ТУТ начинаются поля с приведением полиномов
    // к старшему коэффициенту 1 при поиске GCD
    public final static int R = 12;
    public final static int RMaxPlus = R + 1;
    public final static int RMinPlus = R + 2;
    public final static int RMaxMult = R + 3;
    public final static int RMinMult = R + 4;
    public final static int RMaxMin = R + 5;
    public final static int RMinMax = R + 6;
    public final static int R128 = 19;
    public final static int R64 = 20;
    public final static int R64MaxPlus = R64 + 1;
    public final static int R64MinPlus = R64 + 2;
    public final static int R64MaxMult = R64 + 3;
    public final static int R64MinMult = R64 + 4;
    public final static int R64MaxMin = R64 + 5;
    public final static int R64MinMax = R64 + 6;
    public final static int NumberOfSimpleTypes = 26;
    public final static int MASK_C = 32;
    public final static int Complex = MASK_C;
    public final static int CZ64 = MASK_C + Z64;
    public final static int CZp32 = MASK_C + Zp32;
    public final static int CZ = MASK_C + Z;
    public final static int CZp = MASK_C + Zp;
    public final static int CQ = MASK_C + Q;
    public final static int C = MASK_C + R;
    public final static int C128 = MASK_C + R128;
    public final static int C64 = MASK_C + R64;
    public final static int MASK_H = 64;
    public final static int Polynom = MASK_H - 1;
    public final static int Rational = MASK_H;
    public final static int VectorS = MASK_H + 1;
    public final static int VectorSet = MASK_H + 2;
    public final static int MatrixS = MASK_H + 3;
    public final static int MatrixD = MASK_H + 4;
    public final static int Fname = MASK_H + 5;
    public final static int G = MASK_H + 6; // Group (free, noncommutative)!
    public final static int F = MASK_H + 7;
    public final static int FactorPol = MASK_H + 8;
    public final static int Factor = MASK_H + 9;
    public final static int FactorPolSum = MASK_H + 10;
    public final static int FactorSum = MASK_H + 11;
    public final static int Product = MASK_H + 12;
    public final static int SumOfProduct = MASK_H + 13;
    public final static int TensorD = MASK_H + 14;
    //два типа созданные исключительно для увеличения скорости
    //работ с матрицами и векторами в классе CanonicForms
    public final static int MatrixDF = MASK_H + 15;
    public final static int VectorSF = MASK_H + 16;
    public final static int VectorSetF = MASK_H + 17;
    public final static int TensorDF = MASK_H + 18;
    public final static int NumberOfElementTypes = MASK_H + 19;
    /**
     * Dictionary which corresponds the ring Number by ring string Name.
     * Example: ((Integer)ringNumbByName.get("F")).intValue() is equals to
     * Ring.F.
     */
    public static final Map<String, Integer> ringNumbByName = new HashMap<String, Integer>(64);

    static {
        ringNumbByName.put("Zp32", Zp32);
        ringNumbByName.put("Z", Z);
        ringNumbByName.put("Z64", Z64);
        ringNumbByName.put("Zp", Zp);
        ringNumbByName.put("R", R);
        ringNumbByName.put("R64", R64);
        ringNumbByName.put("R128", R128);
        ringNumbByName.put("Q", Q);
        ringNumbByName.put("CQ", CQ);
        ringNumbByName.put("CZp32", Zp32);
        ringNumbByName.put("CZ", CZ);
        ringNumbByName.put("CZ64", Z64);
        ringNumbByName.put("CZp ", CZp);
        ringNumbByName.put("C", C);
        ringNumbByName.put("C64", C64);
        ringNumbByName.put("C128", C128);
        ringNumbByName.put("Polynom", Polynom);
        ringNumbByName.put("Rational", Rational);
        ringNumbByName.put("VectorS", VectorS);
        ringNumbByName.put("VectorSet", VectorSet);
        ringNumbByName.put("MatrixS", MatrixS);
        ringNumbByName.put("MatrixD", MatrixD);
        ringNumbByName.put("Fname", Fname);
        ringNumbByName.put("F", F);
        ringNumbByName.put("Product", Product);
        ringNumbByName.put("SumOfProduct", SumOfProduct);
        ringNumbByName.put("FactorPol", FactorPol);
        ringNumbByName.put("Factor", Factor);
        ringNumbByName.put("FactorPolSum", FactorPolSum);
        ringNumbByName.put("FactorSum", FactorSum);
        ringNumbByName.put("G", G);
        ringNumbByName.put("ZMaxPlus", ZMaxPlus);
        ringNumbByName.put("ZMinPlus", ZMinPlus);
        ringNumbByName.put("ZMaxMin", ZMaxMin);
        ringNumbByName.put("ZMinMax", ZMinMax);
        ringNumbByName.put("ZMaxMult", ZMaxMult);
        ringNumbByName.put("ZMinMult", ZMinMult);
        ringNumbByName.put("RMaxPlus", RMaxPlus);
        ringNumbByName.put("RMinPlus", RMinPlus);
        ringNumbByName.put("RMaxMin", RMaxMin);
        ringNumbByName.put("RMinMax", RMinMax);
        ringNumbByName.put("RMaxMult", RMaxMult);
        ringNumbByName.put("RMinMult", RMinMult);
        ringNumbByName.put("R64MaxPlus", R64MaxPlus);
        ringNumbByName.put("R64MinPlus", R64MinPlus);
        ringNumbByName.put("R64MaxMin", R64MaxMin);
        ringNumbByName.put("R64MinMax", R64MinMax);
        ringNumbByName.put("R64MaxMult", R64MaxMult);
        ringNumbByName.put("R64MinMult", R64MinMult);
    }

    /**
     * Названия числовых множеств, соответствующие номерам числовых множеств
     */
    public static String nameOfRing(int i) {
        switch (i) {
            case Zp32:
                return "Zp32";
            case Z:
                return "Z";
            case Z64:
                return "Z64";
            case Zp:
                return "Zp";
            case R:
                return "R";
            case R64:
                return "R64";
            case R128:
                return "R128";
            case CZp32:
                return "CZp32";
            case CZ:
                return "CZ";
            case CZ64:
                return "CZ64";
            case CZp:
                return "CZp";
            case C:
                return "C";
            case C64:
                return "C64";
            case C128:
                return "C128";
            case Q:
                return "Q";
            case CQ:
                return "CQ";
            case Rational:
                return "Rational";
            case Polynom:
                return "Polynom";
            case VectorS:
                return "VectorS";
//            case VectorC:
//                return "VectorC";
            case MatrixS:
                return "MatrixS";
            case MatrixD:
                return "MatrixD";
            case Fname:
                return "Fname";
            case F:
                return "  F  ";
            case FactorPol:
                return "FactorPol";
            case Factor:
                return "Factor";
            case G:
                return "G";
            case Product:
                return "Product";
            case SumOfProduct:
                return "SumOfProduct";
            case ZMaxPlus:
                return "ZMaxPlus";
            case ZMinPlus:
                return "ZMinPlus";
            case RMaxPlus:
                return "RMaxPlus";
            case RMinPlus:
                return "RMinPlus";
            case R64MaxPlus:
                return "R64MaxPlus";
            case R64MinPlus:
                return "R64MinPlus";
            case ZMaxMin:
                return "ZMaxMin";
            case ZMinMax:
                return "ZMinMax";
            case RMaxMin:
                return "RMaxMin";
            case RMinMax:
                return "RMinMax";
            case R64MaxMin:
                return "R64MaxMin";
            case R64MinMax:
                return "R64MinMax";
            case ZMaxMult:
                return "ZMaxMult";
            case ZMinMult:
                return "ZMinMult";
            case RMaxMult:
                return "RMaxMult";
            case RMinMult:
                return "RMinMult";
            case R64MaxMult:
                return "R64MaxMult";
            case R64MinMult:
                return "R64MinMult";
            default:
                return " Index of ring name is Out Of Bounds ";
        }
    }

    /**
     * Set the number of exact decimal places in the number of NumberR type.
     * Each operation with NumberR will be perform with this accurecy
     *
     * @param numbOfDecFig -- number of exact decimal places
     */
    public void setAccuracy(int numbOfDecFigures) {
        ACCURACY = numbOfDecFigures;
        MC = new MathContext(ACCURACY, RoundingMode.HALF_EVEN);
        NumberR acc = new NumberR("1E-" + ACCURACY);
        if (!(MachineEpsilonR.compareTo(acc) > 0)) {
            this.exception.append("MachineEpsilonR can't be less then ACCURACY!");
        }
        setConstR();
    }

    /**
     * set constR[] of numberR:  {e, pi, pi/180, 180/pi, 2pi, pi/2}
     */
    public void setConstR() {
        FuncNumberR fnr = new FuncNumberR(this);
        constR = fnr.setConstR();
    }

    /**
     * Add the number of exact decimal places
     *
     * @param numbOfDecFig --extra number of exact decimal places
     */
    public void addAccuracy(int numbOfDecFigures) {
        ACCURACY += numbOfDecFigures;
        MC = new MathContext(ACCURACY, RoundingMode.HALF_EVEN);
    }

    /**
     * Set FLOATPOS ring variable.
     *
     * @param i -- number of digits in the fraction part of decimal number for
     *          converting to String. Example: for print a number 1.234 the FLOATPOS must
     *          be equals 3 )
     */
    public void setFLOATPOS(int i) {
        FLOATPOS = i;
    }

    public void setMachineEpsilonR(int i) {
        MachineEpsilonR = new NumberR("1E" + ((i < 0) ? ("+" + i) : (-i)));
    }

    public void setMachineEpsilonR(NumberR eps) {
        MachineEpsilonR = eps;
    }

    public void setMachineEpsilonR(String eps) {
        MachineEpsilonR = new NumberR(eps);
    }

//    public void setMachineEpsilonR64(int i) {
//        MachineEpsilonR64 = new NumberR64("1E" + ((i < 0) ? ("+" + (-i)) : (-i)));
//    }

    public void setMachineEpsilonR64(double eps) {
        MachineEpsilonR64 = new NumberR64(eps);
    }

    public void setMachineEpsilonR64(NumberR64 eps) {
        MachineEpsilonR64 = eps;
    }

    public void setMachineEpsilonR64(String eps) {
        MachineEpsilonR64 = new NumberR64(eps);
    }

    public void setMOD32(long mod32) {
        MOD32 = mod32;
    }

    public void setRADIAN(Element booleanFlag) {
        // true--> radian, false-->degree
        RADIAN = (booleanFlag.isZero(this)) ? Element.FALSE : Element.TRUE;
    }

    public void setSTEPBYSTEP(Element booleanFlag) {
        boolean bb = booleanFlag.isZero(this);
        STEPBYSTEP = (bb) ? Element.FALSE : Element.TRUE;
    }

    public void setSUBSTITUTION(Element booleanFlag) {
        boolean bb = booleanFlag.isZero(this);
        SUBSTITUTION = (bb) ? Element.FALSE : Element.TRUE;
    } // true--> SUBSTITUTION,
    //false-->NO SUBSTITUTION (не подставлять раннее введенные значения для
    //                            переменных во входных выражениях)

    public void setENTERING_FACTOR_IN_LOG(Element booleanFlag) {
        boolean bb = booleanFlag.isZero(this);
        ENTERING_FACTOR_IN_LOG = (bb) ? Element.FALSE : Element.TRUE;
    }

    public void setGROUP_NEGATIVE_TERMS(Element booleanFlag) {
        boolean bb = booleanFlag.isZero(this);
        GROUP_NEGATIVE_TERMS = (bb) ? Element.FALSE : Element.TRUE;
    }

    /**
     * Expand expresions? i.e.
     *
     * @param booleanFlag
     */

    public void setEXPAND(Element booleanFlag) {
        boolean bb = booleanFlag.isZero(this);
        EXPAND = (bb) ? Element.FALSE : Element.TRUE;
    } // true--> EXPAND, false-->NO EXPAND (не раскрывать скобки во входных выражениях)

    public void setMOD(NumberZ mod) {
        MOD = mod;
    }

    public void setTOTALNODES(int i) {
        TOTALNODES = i;
    }

    public void setPROCPERNODE(int i) {
        PROCPERNODE = i;
    }

    public void setMAXCLUSTERMEMORY(int i) {
        MAXCLUSTERMEMORY = i;
    }

    public void setCLUSTERTIME(int i) {
        CLUSTERTIME = i;
    }

    public void setSmallestBlock(int i) {
        SMALLESTBLOCK = i;
    }

    /**
     * По элементу возвращает пустой массив данного типа
     *
     * @param el
     * @return
     */
    public static Element[] emptyArrayOf(Element el) {
        return (Element[]) java.lang.reflect.Array.newInstance(el.getClass(), 0);
    }


    /**
     * возвращает: true, если кольцо this совпадает с кольцом ring false, если
     * кольца не совпадают
     *
     * @param ring
     * @return
     */
    public boolean equals(Ring ring) {
        if (varNames.length != ring.varNames.length
            || varLastIndices.length != ring.varLastIndices.length ||
            algebra.length != ring.algebra.length) {
            return false;
        }
        if (varNames.length > 0) {
            for (int i = 0; i < varNames.length; i++) {
                if (!varNames[i].equals(ring.varNames[i])) {
                    return false;
                }
            }
        }
        if (algebra.length > 0) {
            for (int i = 0; i < algebra.length; i++) {
                if (!(algebra[i] == ring.algebra[i])) {
                    return false;
                }
            }
        }
        if (varLastIndices.length > 0) {
            for (int i = 0; i < varLastIndices.length; i++) {
                if (!(varLastIndices[i] == ring.varLastIndices[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return the array of string for variable String[] varNames of Ring.
     * It prints the error message if two names is equal.
     *
     * @param varStr -- StringBuffer that consists one string with all vars names separated by commas.
     * @return array of string for variable String[] varNames of Ring
     */
    private String[] getVarsNames(StringBuffer varStr) {
        int posEnd;
        //Подсчитаем кол-во переменных = кол-во запятых+1
        int varsCount = 1;
        if (varStr.toString().trim().length() == 0) {
            varsCount = 0;
        } else {
            for (int i = 0; i < varStr.length(); i++) {
                if (varStr.charAt(i) == ',') {
                    varsCount++;
                }
            }
        }
        //Создадим массив имен переменных длиной varsCount
        String[] vars = new String[varsCount];
        String oneVarStr; //имя i-й переменной
        for (int i = 0; i < varsCount; i++) {
            if (i == varsCount - 1) {
                oneVarStr = varStr.toString(); //если это последняя переменная, то
                //имя i-й переменной=всей строке
            } else {
                posEnd = varStr.indexOf(","); //иначе имя i-й переменной=
                oneVarStr = varStr.substring(0, posEnd); //строке varStr от 0 до 1-й запятой
                varStr.delete(0, posEnd + 1); //удалить из varStr имя i-й
                //переменной вместе с запятой
            }
            oneVarStr = oneVarStr.trim();
            for (int j = 0; j < i; j++) {
                if (oneVarStr.equals(vars[j])) {
                    System.err.append(
                        "\nstr2Ring: Error 5: Variable number " + (i + 1)
                        + " is equal to variable number " + (j + 1));
                }
            }
            vars[i] = oneVarStr; //Записываем имя i-й переменной
        }
        return vars;
    }

    /**
     * Вывод представителя Ring в строку
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        //Запишем в строку res имя числового множества
        int k = 0;
        for (int i = 0; i < algebra.length; i++) {
            int ring = algebra[i];
            res.append(nameOfRing(ring));//имя алгебры
            res.append("[");
            for (int j = 0 + k; j <= varLastIndices[i]; j++) {
                res.append(varNames[j]);
                if (j != varLastIndices[i]) { res.append(","); }
            }
            res.append("]");
            k = varLastIndices[i] + 1;
        }
        return res.toString();
    }

    public Element complexMINUS_I() {
        return numberMINUS_I;
    }

    public Element complexI() {
        return numberI;
    }

    public Element numberZERO() {
        return numberZERO;
    }

    public Element numberMINUS_ONE() {
        return numberMINUS_ONE;
    }

    public Element numberONE() {
        return numberONE;
    }

    public static Element oneOfType(int type) {
        if (type < Ring.Complex) {
            switch (type) {
                case Zp32:
                    return NumberZp32.ONE;
                case R128:
                    return NumberR128.ONE;
                case R64:
                    return NumberR64.ONE;
                case R:
                    return NumberR.ONE;
                case Z:
                    return NumberZ.ONE;

                case R64MaxMult:
                    return NumberR64MaxMult.ONE;
                case RMaxMult:
                    return NumberRMaxMult.ONE;
                case ZMaxMult:
                    return NumberZMaxMult.ONE;
                case R64MinMult:
                    return NumberR64MinMult.ONE;
                case RMinMult:
                    return NumberRMinMult.ONE;
                case ZMinMult:
                    return NumberZMinMult.ONE;

                case Zp:
                    return NumberZp.ONE;
                case Z64:
                    return NumberZ64.ONE;
                case Q:
                    return new Fraction(NumberZ.ONE, NumberZ.ONE);

                case ZMaxPlus:
                    return NumberZMaxPlus.ONE;
                case ZMaxMin:
                    return NumberZMaxMin.ONE;
                case ZMinMax:
                    return NumberZMinMax.ONE;
                case ZMinPlus:
                    return NumberZMinPlus.ONE;
                case RMaxPlus:
                    return NumberRMaxPlus.ONE;
                case RMaxMin:
                    return NumberR.POSITIVE_INFINITY;
                case RMinMax:
                    return NumberR.NEGATIVE_INFINITY;
                case RMinPlus:
                    return NumberRMinPlus.ONE;
                case R64MaxPlus:
                    return NumberR64MaxPlus.ONE;
                case R64MinPlus:
                    return NumberR64MinPlus.ONE;
                case R64MaxMin:
                    return NumberR64.POSITIVE_INFINITY;
                case R64MinMax:
                    return NumberR64.NEGATIVE_INFINITY;
            }
        } else if (type < Polynom) {
            return new Complex(oneOfType(type - Complex), zeroOfType(type - Complex));
        }
        return NumberZ.ONE;
    }

    public static Element zeroOfType(int type) {
        if (type < Ring.Complex) {
            switch (type) {
                case Zp32:
                    return NumberZp32.ZERO;
                case R128:
                    return NumberR128.ZERO;
                case Zp:
                    return NumberZp.ZERO;
                case Z64:
                    return NumberZ64.ZERO;
                case Q:
                    return new Fraction(NumberZ.ZERO, NumberZ.ONE);
                case Z:
                    return NumberZ.ZERO;
                case ZMaxPlus:
                    return NumberZMaxPlus.ZERO;
                case ZMinPlus:
                    return NumberZMinPlus.ZERO;
                case ZMaxMin:
                    return NumberZMaxMin.ZERO;
                case ZMinMax:
                    return NumberZMinMax.ZERO;
                case ZMaxMult:
                    return NumberZMaxMult.ZERO;

                case ZMinMult:
                    return NumberZMinMult.ZERO;
                case R:
                    return NumberR.ZERO;
                case RMaxPlus:
                    return NumberRMaxPlus.ZERO;
                case RMinPlus:
                    return NumberRMinPlus.ZERO;
                case RMaxMin:
                    return NumberRMaxMin.ZERO;
                case RMinMax:
                    return NumberRMinMax.ZERO;
                case RMaxMult:
                    return NumberRMaxMult.ZERO;
                case RMinMult:
                    return NumberRMinMult.ZERO;
                case R64:
                    return NumberR64.ZERO;
                case R64MaxPlus:
                    return NumberR64MaxPlus.ZERO;
                case R64MinPlus:
                    return NumberR64MinPlus.ZERO;
                case R64MaxMin:
                    return NumberR64MaxMin.ZERO;
                case R64MinMax:
                    return NumberR64MinMax.ZERO;
                case R64MaxMult:
                    return NumberR64MaxMult.ZERO;
                case R64MinMult:
                    return NumberR64MinMult.ZERO;
            }
        } else if (type < Polynom) {
            Element zero = zeroOfType(type - Complex);
            return new Complex(zero, zero);
        }
        return NumberZ.ZERO;
    }

    /*   точную нижнюю грань двух элементов,
     операцию замыкания Клини (звездочку), обращение элемента в
     полуполях и пополненных полуполях. Все полукольца разумно считать
     полными (включать не только минус, но и плюс-бесконечность).
     С каждым полукольцом разумно иметь его интервальную версию  */
    public int getAccuracy() {
        return ACCURACY;
    }

    /**
     * Метод добавления в объект хранения шагов трассировки одного элемента
     *
     * @param el - элемент
     */
    public void addTraceStep(Element el) {
        traceSteps.add(el);
    }

    public boolean isStepbystep() {return STEPBYSTEP == NumberZ64.ONE;}

    public boolean isFeild() {
        int a = (algebra[0] & (Complex - 1));
        if ((a == Ring.Zp32) || (a == Ring.R64) || (a == Ring.Q) || (a == Ring.R) ||
            (a == Ring.Zp) || (a == Ring.R128)) { return true; }
        return false;
    }

    public boolean isComplex() {return ((algebra[0] & Complex) != 0);}

    public boolean isExactRing() { return (algebra[0] & ~Complex) < R;}

    public void setTimeout(int newTimeout) {TIMEOUT = newTimeout; }

    public static void main(String[] args) {

        Ring ring = new Ring(2, 0);
        System.out.println("ring.Fl=" + ring.FLOATPOS);
    }
}
