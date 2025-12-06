package com.mathpar.func;

import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.polynom.*;

public class FvalOf {
    private Ring ring;

    public FvalOf() {
    }

    public FvalOf(Ring r) {
        ring = r;
    }

    public FvalOf(Ring r, boolean radianFlag) {
        ring = r;
        ring.RADIAN = new NumberZ64(radianFlag);
    }

    /**
     * Метод, возводящий переменную из кольца с индексом p в степень pow.
     *
     * @param p - номер переменной в кольце
     * @param pow - степень в которую возводится полином
     *
     * @return ring.varPolynom[p]^pow
     */
    private Polynom powPol(int p, int pow) {
        int[] newpowers = new int[p + 1];
        newpowers[newpowers.length - 1] = pow;
        return new Polynom(newpowers, new Element[] {ring.numberONE()});
    }

    /**
     * Метод подставляющий значения массива point в полином pol
     *
     * @param pol
     * @param point
     *
     * @return
     */
    public Element valOfToPolynom(Polynom pol, Element[] point) {
        pol = pol.normalNumbVar(ring); // заглушка
        if (pol.isItNumber()) {
            return (pol.isZero(ring)) ? ring.numberZERO : pol.coeffs[0];
        }
        //pol=pol.toPolynom(ring.algebra[0], ring); // приводим к типу кольца (первой алгебры)
        int type = 0;
        for (int i = 0; i < point.length; i++) {
            type = point[i].numbElementType();
            if (type >= Ring.Polynom) {
                break;
            }
        }
        if (type < Ring.Polynom) {
            return pol.value(point, ring);
        }

        if (pol.isItNumber()) {
            return (pol.isZero(ring)) ? ring.numberONE().zero(ring) : pol.coeffs[0]; // если чило , то сразу возвращаем
        }
        if (point.length == 0) {
            return pol;
        }
        int coefLen = pol.coeffs.length;
        int len = pol.powers.length / coefLen;
        int varlen = point.length;
        int coefIndex = 0; // индекс  монома в массиве степеней
        int coefpos = 0;// индекс монома в массиве коэффмциентов
        Element[] futureSum = new Element[coefLen];// будущая сумма мономов
        int varpow; // промежуточное значение степени
        Element Res;
        while (coefIndex < pol.powers.length) {
            Res = pol.coeffs[coefpos];
            for (int i = 0; i < len; i++) {
                varpow = pol.powers[coefIndex + i];
                if (varpow > 0) {
                    if (varpow == 1) {
                        if (i < varlen) {
                            Res = Res.multiply(point[i], ring);
                        } else {
                            Res = Res.multiply(ring.varPolynom[i], ring);
                        }
                    } else {
                        if (i < varlen) {
                            Res = Res.multiply(point[i].pow(varpow, ring), ring);
                        } else {
                            Res = Res.multiply(powPol(i, varpow), ring);
                        }
                    }
                }
            }
            futureSum[coefpos] = Res;
            coefpos++;
            coefIndex += len;
        }
        if (coefLen == 1) {
            Element res = futureSum[0];
            if (res instanceof F) {
                res = ((F) res).expand(ring);
            } else {
                return res;
            }
            return (((F) res).name == F.ID) ? ((F) res).X[0] : res;
        }
        F Lres = (new F(F.ADD, futureSum)).expand(ring);
        return (Lres.name == F.ID) ? Lres.X[0] : Lres;
    }

    /**
     * Процедура являющаяся некой надстройкой, которая перед вычислением функции
     * f в точке point, переводит все элементы этой точки в тип главного кольца.
     *
     * @param f - функция
     * @param point - точка
     *
     * @return значение f в точке point
     */
    public Element valOf(F f, Element[] point) {//see at == ------!---------=========================00000000000000
        Element[] newpoint = new Element[point.length]; // готовим новую точку
        for (int i = 0; i < point.length; i++) { // бежим по аргумента входной
            switch (point[i].numbElementType()) { // смотрим на тип
                case Ring.Fname:
                    if (((Fname) point[i]).name.equals("\\e")) {
                        newpoint[i] = ring.numberONE().e(ring);
                        break;
                    }
                    if (((Fname) point[i]).name.equals("\\pi")) {
                        newpoint[i] = ring.numberONE().pi(ring);
                        break;
                    }
                case Ring.F:
                    newpoint[i] = point[i];
                    break;
                case Ring.Polynom:
                    newpoint[i] = ((Polynom) point[i]).toPolynom(ring.algebra[0], ring);
                    break;
                default:
                    newpoint[i] = point[i].toNumber(ring.algebra[0], ring);
            }
        }
        return valOf_(f, newpoint);
    }

    public Element valOf(Element func, Element[] point) {
        switch (func.numbElementType()) {
            case Ring.F:
                return valOf((F) func, point);
            case Ring.Polynom:
                return valOfToPolynom((Polynom) func, point);
            case Ring.Q:
            case Ring.Rational:
                return valOf(((Fraction) func).num, point).divide(valOf(((Fraction) func).denom, point), ring);
            case Ring.Fname:
                if (((Fname) func).name.equals("\\e")) {
                    return ring.numberONE().e(ring);

                }
                if (((Fname) func).name.equals("\\pi")) {
                    return ring.numberONE().pi(ring);

                }
                if (((Fname) func).name.equals("\\i")) {
                    return new Complex(ring.numberZERO, ring.numberONE);

                }

            //здесь можно вписать посчет для матриц , векторов и т.д.
            default:
                return func;
        }
    }

    /**
     * Процедура, подставляющая элементы массива point, на листах функции f , в
     * порядке соответствующим порядку переменных в текущем кольце. Если длинна
     * point меньше чем количество переменных в кольце (не всем хватило новых
     * значений), то те "на кого не хватило" остаются без изменения.
     *
     * @param f - функция для подсчета
     * @param point - список для замены
     *
     * @return значение функции в точке point
     */
    public Element valOf_(F f, Element[] point) {
        Element[] CalcArg = new Element[f.X.length];
        for (int i = 0; i < f.X.length; i++) {
            switch (f.X[i].numbElementType()) {
                case Ring.Rational:
                    F NUM = (((Fraction) f.X[i]).num instanceof F) ? (F) ((Fraction) f.X[i]).num : new F(((Fraction) f.X[i]).num);
                    F DENOM = (((Fraction) f.X[i]).denom instanceof F) ? (F) ((Fraction) f.X[i]).denom : new F(((Fraction) f.X[i]).denom);
                    CalcArg[i] = valOf_(NUM, point).divide(valOf_(DENOM, point), ring);
                    break;
                case Ring.Q:
                    CalcArg[i] = ((Fraction) f.X[i]).num.divide(((Fraction) f.X[i]).denom, ring);
                    break;
                case Ring.F:
                    CalcArg[i] = valOf_((F) f.X[i], point);
                    break;
                case Ring.Complex: {//Если действительная часть - функция, а не число
                    if (((Complex) f.X[i]).im.isZero(ring)) {
                        CalcArg[i] = (((Complex) f.X[i]).re instanceof F) ? valOf_((F) ((Complex) f.X[i]).re, point)
                                : valOf_(new F(((Complex) f.X[i]).re), point);
                        break;
                    }
                    CalcArg[i] = f.X[i];
                    break;
                }
                case Ring.Polynom:
                    CalcArg[i] = valOfToPolynom((Polynom) f.X[i], point);
                    break;
                case Ring.Fname:
                    if (((Fname) f.X[i]).name.equals("\\e")) {
                        CalcArg[i] = ring.numberONE().e(ring);
                        break;
                    }
                    if (((Fname) f.X[i]).name.equals("\\pi")) {
                        CalcArg[i] = ring.numberONE().pi(ring);
                        break;
                    }
                    if (((Fname) f.X[i]).name.equals("\\i")) {
                        CalcArg[i] = new Complex(ring.numberZERO, ring.numberONE);
                        break;
                    }
                    CalcArg[i] = f.X[i].value(point, ring);
                    break;
                case Ring.VectorS:
                    Element[] res = new Element[((VectorS) f.X[i]).V.length];
                    for (int n = 0; n < res.length; n++) {
                        res[n] = valOf(new F(f.name, ((VectorS) f.X[i]).V[n]), point);
                    }
                    CalcArg[i] = new VectorS(res);
                    break;
                case Ring.VectorSF:
                    Element[] resf = new Element[((VectorS) f.X[i]).V.length];
                    for (int n = 0; n < resf.length; n++) {
                        resf[n] = valOf(new F(f.name, ((VectorS) f.X[i]).V[n]), point);
                    }
                    CalcArg[i] = new VectorS(new VectorS(resf), ((VectorS)f.X[i]).fl);
                    break;
                case Ring.MatrixS:
                    Element[][] dm = ((MatrixS) f.X[i]).M;
                    Element[][] newM = new Element[dm.length][];
                    for (int i_ = 0; i_ < newM.length; i_++) {
                        newM[i] = new Element[dm[i].length];
                        for (int j = 0; j < newM[i].length; j++) {
                            newM[i][j] = valOf(new F(f.name, dm[i_][j]), point);
                        }
                    }
                    return new MatrixS(newM, ((MatrixS) f.X[i]).col);
                case Ring.MatrixD:
                    Element[][] dmf = ((MatrixD) f.X[i]).M;
                    Element[][] newMf = new Element[dmf.length][];
                    for (int i_ = 0; i_ < newMf.length; i_++) {
                        newMf[i] = new Element[dmf[i].length];
                        for (int j = 0; j < newMf[i].length; j++) {
                            newMf[i][j] = valOf(new F(f.name, dmf[i_][j]), point);
                        }
                    }
                    return new MatrixD(new MatrixD(newMf), ((MatrixD) f.X[i]).fl);
                default:
                    CalcArg[i] = (f.X[i]).value(point, ring);
            }
        }
        Element res = CalcArg[0];
        int numResType = res.numbElementType();
        if (numResType == Ring.VectorS | numResType == Ring.VectorSF | numResType == Ring.MatrixDF | numResType == Ring.MatrixS) {
            return res;
        }
        switch (f.name) {
            case F.VECTORS:
                return new F(f.name, CalcArg);
            case F.GAMMA:
                return CalcArg[0].gamma(ring);
            case F.BETA:
                return CalcArg[0].beta(CalcArg[1], ring);
            case F.UNITBOX:
                Element el = CalcArg[0].multiply(CalcArg[1], ring).subtract(CalcArg[0].multiply(CalcArg[0], ring), ring);
                if (!el.isItNumber()) {
                    return new F(f.name, CalcArg);
                }
                return el.isNegative() ? CalcArg[0].zero(ring) : CalcArg[0].one(ring);
            case F.UNITSTEP:
                if (!CalcArg[0].isItNumber()) {
                    return new F(f.name, CalcArg);
                }
                return CalcArg[0].isNegative() ? CalcArg[0].zero(ring) : CalcArg[0].one(ring);
            case F.ROOTOF:
                return CalcArg[0].rootOf(CalcArg[1].intValue(), ring);
            case F.B_OR:
                return ((NumberZ64) CalcArg[0]).B_OR(((NumberZ64) CalcArg[1]));
            case F.B_AND:
                return ((NumberZ64) CalcArg[0]).B_AND(((NumberZ64) CalcArg[1]));
            case F.ID:
                return (CalcArg[0] == null) ? new F(f.name, CalcArg) : CalcArg[0];
            case F.ABS:
                res = CalcArg[0].abs(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.EXP:
                res = CalcArg[0].exp(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.SQRT:
                res = CalcArg[0].sqrt(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.COS:
                res = CalcArg[0].cos(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.SIN:
                res = CalcArg[0].sin(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.TG:
                res = CalcArg[0].tan(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.CTG:
                res = CalcArg[0].ctg(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.SH:
                res = CalcArg[0].sh(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.CH:
                res = CalcArg[0].ch(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.TH:
                res = CalcArg[0].th(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.CTH:
                res = CalcArg[0].cth(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.LOG:
                res = CalcArg[1].log(CalcArg[0], ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.LN:
                res = CalcArg[0].ln(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.LG:
                res = CalcArg[0].lg(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCSIN:
                res = CalcArg[0].arcsn(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCCOS:
                res = CalcArg[0].arccs(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCTG:
                res = CalcArg[0].arctn(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCCTG:
                res = CalcArg[0].arcctn(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCSH:
                res = CalcArg[0].arsh(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCCH:
                res = CalcArg[0].arch(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCTGH:
                res = CalcArg[0].arth(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.BINOMIAL:
                return ((CalcArg[0].numbElementType() < Ring.Polynom) && (CalcArg[1].numbElementType() < Ring.Polynom))
                        ? Element.binomial(ring, CalcArg[0].intValue(), CalcArg[1].intValue())
                        : new F(F.BINOMIAL, new Element[] {CalcArg[0], CalcArg[1]});
            case F.intPOW:
                res = CalcArg[0].pow(CalcArg[1].intValue(), ring);
                return (res == null) ? new F(F.intPOW, new Element[] {CalcArg[0], CalcArg[1]}) : res;
            case F.POW:
                res = CalcArg[0].pow(CalcArg[1], ring);
                return (res == null) ? new F(F.POW, new Element[] {CalcArg[0], CalcArg[1]}) : res;
            case F.ARCCTGH:
                return new F(f.name, CalcArg);
            case F.ADD:
                res = CalcArg[0];
                for (int i = 1; i < CalcArg.length; i++) {
                    res = res.add(CalcArg[i], ring);
                }
                return res;
            case F.DIVIDE:
                return CalcArg[0].divide(CalcArg[1], ring);
            case F.SUBTRACT:
                return CalcArg[0].subtract(CalcArg[1], ring);
            case F.MULTIPLY:
                res = CalcArg[0];
                for (int i = 1; i < CalcArg.length; i++) {
                    res = res.multiply(CalcArg[i], ring);
                }
                return res;
        }
        return f;
    }

    public Element getFunctionValue(int FunctionNumber, Element arg) {
        Element answer = getFunctionValueR64(FunctionNumber, arg);
        if (answer == null) {
            answer = getFunctionValueR64(FunctionNumber, arg);
        }
        return answer;
    }

    /**
     *
     * @param FunctionNumber int
     * @param arg Element
     * @param ring
     *
     * @return Element
     */
    public Element getFunctionValueR(int FunctionNumber, Element arg, Ring ring) {
        FuncNumberR funcNumberR = new FuncNumberR(ring);
        Element Argument = arg;
        if (arg instanceof Polynom) {
            if (!((Polynom) arg).isItNumber()) {
                return new F(FunctionNumber, arg);
            } else {
                Argument = ((Polynom) arg).coeffs[0];
            }
        }
        if (Argument instanceof NumberR) {
            switch (FunctionNumber) {
                case F.ID:
                    return Argument;
                case F.ABS:
                    return (Argument.signum() == -1) ? Argument.negate(ring) : Argument;
                case F.LN:
                    return funcNumberR.ln((NumberR) Argument);
                case F.LG:
                    return ((NumberR) funcNumberR.ln((NumberR) Argument)).divide((NumberR) funcNumberR.ln(new NumberR(10)), ring.MC);
                case F.EXP:
                    return funcNumberR.exp((NumberR) Argument);
                case F.SQRT:
                    return funcNumberR.sqrt((NumberR) Argument);
                case F.SIN:
                    return funcNumberR.sin((NumberR) Argument);
                case F.COS:
                    return funcNumberR.cos((NumberR) Argument);
                case F.TG:
                    return funcNumberR.tan((NumberR) Argument);
                case F.CTG:
                    return funcNumberR.ctg((NumberR) Argument);
                case F.ARCSIN:
                    return funcNumberR.arcsn((NumberR) Argument);
                case F.ARCCOS:
                    return funcNumberR.arccs((NumberR) Argument);
                case F.ARCTG:
                    return funcNumberR.arctn((NumberR) Argument);
                case F.ARCCTG:
                    return funcNumberR.arcctn((NumberR) Argument);
                case F.SH:
                    return funcNumberR.sh((NumberR) Argument);
                case F.CH:
                    return funcNumberR.ch((NumberR) Argument);
                case F.TH:
                    return funcNumberR.th((NumberR) Argument);
                case F.CTH:
                    return funcNumberR.cth((NumberR) Argument);
                case F.ARCSH:
                    return funcNumberR.arsh((NumberR) Argument);
                case F.ARCCH:
                    return funcNumberR.arch((NumberR) Argument);
                case F.ARCTGH:
                    return funcNumberR.arth((NumberR) Argument);
                case F.ARCCTGH:
                    return funcNumberR.arcth((NumberR) Argument);
                case F.UNITSTEP:
                    if (Argument.doubleValue() > 0) {
                        return NumberR.ONE;
                    }
                    if (Argument.doubleValue() == 0) {
                        return (NumberR.ONE).divide(new NumberR(2), ring.MC); //1/2
                    } else {
                        return NumberR.ZERO;
                    }
                default:
                    return Argument;
            }
        }
        return null;
    }

    /**
     *
     * @param FunctionNumber int
     * @param arg Element
     *
     * @return Element
     */
    public Element getFunctionValueR64(int FunctionNumber, Element arg) {
        Element Argument = arg;
        double Result = 0;
        if (arg instanceof Polynom) {
            if (!((Polynom) arg).isItNumber()) {
                return new F(FunctionNumber, arg);
            } else if (arg.isZero(ring)) {
                Argument = NumberR64.ZERO;
            } else {
                Argument = ((Polynom) arg).coeffs[0];
            }
        }

        // Приводим результат в общим виде как double и переводим в нужный нам числовой тип
        switch (FunctionNumber) {
            case F.ID:
                return Argument;
            case F.ABS:
                Result = Math.abs(Argument.doubleValue());
                break;
            case F.LN:
                Result = Math.log(Argument.doubleValue());
                break;
            case F.LG:
                Result = Math.log10(Argument.doubleValue());
                break;
            case F.EXP:
                Result = Math.exp(Argument.doubleValue());
                break;
            case F.SQRT:
                Result = Math.sqrt(Argument.doubleValue());
                break;
            case F.SIN:
                Result = Math.sin(Argument.doubleValue());
                break;
            case F.COS:
                Result = Math.cos(Argument.doubleValue());
                break;
            case F.TG:
                Result = Math.tan(Argument.doubleValue());
                break;
            case F.CTG:
                Result = 1 / Math.tan(Argument.doubleValue());
                break;
            case F.ARCSIN:
                Result = Math.asin(Argument.doubleValue());
                break;
            case F.ARCCOS:
                Result = Math.acos(Argument.doubleValue());
                break;
            case F.ARCTG:
                Result = Math.atan(Argument.doubleValue());
                break;
            case F.ARCCTG:
                Result = (Math.acos(Argument.doubleValue()
                        / Math.sqrt(1 + (Argument.multiply(Argument, ring)).doubleValue()))); //arcctg(Argument);
                break;
            case F.SH:
                Result = (Math.sinh(Argument.doubleValue()));
                break;
            case F.CH:
                Result = (Math.cosh(Argument.doubleValue()));
                break;
            case F.TH:
                Result = (Math.tanh(Argument.doubleValue()));
                break;
            case F.CTH:
                Result = 1 / Math.tanh(Argument.doubleValue());
                break;
            case F.ARCSH:
                Result = (Math.log(Argument.doubleValue()
                        + Math.sqrt(Argument.doubleValue()
                                * Argument.doubleValue() - 1)));
                break;
            case F.ARCCH:
                Result = (Math.log(Argument.doubleValue()
                        + Math.sqrt(Argument.doubleValue()
                                * Argument.doubleValue() + 1)));
                break;
            case F.ARCTGH:
                Result = (Math.log((1 + Argument.doubleValue())
                        / (1 - Argument.doubleValue())));
                break;
            case F.ARCCTGH:
                Result = (Math.log((1 + Argument.doubleValue())
                        / (Argument.doubleValue() - 1)));
                break;
            case F.UNITSTEP:
                if (Argument.doubleValue() > 0) {
                    Result = 1;
                } else {
                    if (Argument.doubleValue() == 0) {
                        Result = 0.5; //1/2
                    } else {
                        Result = 0;
                    }
                }
                break;
            default:
                Result = Argument.doubleValue();
        }
        return new NumberR64(Result);
    }

    /**
     *
     * @param FunctionNumber int
     * @param ArgumentA Element
     * @param ArgumentB Element
     *
     * @return Element
     */
    public Element getFunctionValue(int FunctionNumber, Element ArgumentA,
            Element ArgumentB) {
        Element Result = NumberR64.ZERO;
        switch (FunctionNumber) {
            case F.LOG:
                Result = NumberR64.ONE.valOf(Math.log10(ArgumentA.doubleValue())
                        / Math.log10(ArgumentB.doubleValue()), ring);
                break;
            case F.intPOW:
            case F.POW:
                Result = NumberR64.ONE.valOf(Math.pow(ArgumentA.doubleValue(),
                        ArgumentB.doubleValue()), ring);
                break;
            case F.UNITSTEP:
                if (ArgumentA.compareTo(ArgumentB, ring) == -1) {
                    Result = ArgumentA.zero(ring); //1
                } else {
                    Result = ArgumentA.one(ring); //0
                }
                break;
        }
        return Result;
    }

    /**
     * для функций 3-х аргументов
     *
     * @param FunctionNumber int
     * @param ArgumentA Element
     * @param ArgumentB Element
     * @param ArgumentC Element
     *
     * @return Element
     */
    public Element getFunctionValue(int FunctionNumber, Element ArgumentA,
            Element ArgumentB, Element x) {
        Element Result = NumberR64.ZERO;
        switch (FunctionNumber) {
            case 61:
                if ((ArgumentA.compareTo(x, ring) == -1) && (ArgumentB.compareTo(x, ring) == 1)) {
                    Result = x.one(ring); //0
                } else {
                    Result = x.zero(ring); //1
                }
        }
        return Result;
    }

    /**
     * Метод позволяющий считать значения функций в точке, полиномов, объектов
     * типа VectorS и MatrixS с той точностью, которая интересует пользователя.
     *
     * @param Element f - объект типа Element; Element[] point - точки, в
     * которых веедтся подсчет; int acc - точность;
     *
     * @return f - какого типа пришел объект, такой же и на выходе.
     */
    public Element valOfwithACC(Element f, Element[] point, int acc) {
        ring.setAccuracy(acc);//принимаемая точность
        ring.setFLOATPOS(acc);//количество выводимых знаков после запятой
        Element rez;
        Element EPS = NumberR.TEN.pow(-ring.getAccuracy(), ring);//epsilon=10^-точность

        //входной параметр функция?
        if (f instanceof F) {
            do {//в цикле считаем значения пока результат не будет точен вплоть до точности заданной пользователем
                Element a = (NumberR) new FvalOf(ring).valOf(f, point);//cчитаем значение с входной точностью
                ring.setAccuracy(ring.getAccuracy() + 10);//увеличиваем точность на 10
                Element b = (NumberR) new FvalOf(ring).valOf(f, point);//cчитаем значение с увеличенной точностью
                rez = b.subtract(a, ring);// определяем разницу между получившимися данными с входной точностью и увеличенной
                rez = rez.abs(ring);
            } while (rez.compareTo(EPS, ring) == 1);//разница меньше епсилон?
            Element a = (NumberR) new FvalOf(ring).valOf(f, point);
        }

        //входной параметр полином?
        if (f instanceof Polynom) {
            Polynom z = new Polynom();
            do {
                Element a = (Element) new FvalOf(ring).valOf(f, point);
                ring.setAccuracy(ring.getAccuracy() + 100);

                Element b = (Element) new FvalOf(ring).valOf(f, point);
                rez = b.subtract(a, ring);
            } while (rez.compareTo(EPS) == 1);
            Element a = (Element) new FvalOf(ring).valOf(f, point);
        }

        //входной параметр вектор?
        if (f instanceof VectorS) {
            Element[] v = ((VectorS) f).V;
            int len = v.length;
            Element[] resArr = new Element[len];
            Element[] resArr2 = new Element[len];
            Element[] res = new Element[len];
            for (int i = 0; i < len; i++) {
                do {
                    resArr[i] = (Element) new FvalOf(ring).valOf(v[i], point);
                    ring.setAccuracy(ring.getAccuracy() + 10);
                    resArr2[i] = (Element) new FvalOf(ring).valOf(v[i], point);
                    res[i] = resArr2[i].subtract(resArr[i], ring);
                } while (res[i].compareTo(EPS) == 1);
            }
        }

        //входной параметр матрица?
        if (f instanceof MatrixS) {
            Element[][] v = ((MatrixS) f).toScalarArray(ring);

            int size = v.length;
            int colNumb = v.length;
            Element[][] resArr = new Element[size][colNumb];
            Element[][] resArr2 = new Element[size][colNumb];
            Element[][] res = new Element[size][colNumb];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < colNumb; j++) {
                    do {
                        resArr[i][j] = (Element) new FvalOf(ring).valOf(v[i][j], point);
                        ring.setAccuracy(ring.getAccuracy() + 10);
                        resArr2[i][j] = (Element) new FvalOf(ring).valOf(v[i][j], point);
                        res[i][j] = resArr2[i][j].subtract(resArr[i][j], ring);
                    } while (res[i][j].compareTo(EPS) == 1);
                }
            }
        }

        return f;
    }
    //процедура считающая точность на лепестках

    public Element valOfwithACC_list(F f, Element[] point) {
        NumberR rez;
        NumberR EPS = NumberR.TEN.pow(-ring.getAccuracy() + 10, ring);

        Element[] CalcArg = new Element[f.X.length];

        for (int i = 0; i < f.X.length; i++) {
            switch (f.X[i].numbElementType()) {
                case Ring.F:
                    CalcArg[i] = valOf_((F) f.X[i], point);
                    break;
                case Ring.Polynom:
                    CalcArg[i] = valOfToPolynom((Polynom) f.X[i], point);
                    break;
                case Ring.Fname:
                    if (((Fname) f.X[i]).name.equals("\\e")) {
                        CalcArg[i] = ring.numberONE().e(ring);
                        break;
                    }
                    if (((Fname) f.X[i]).name.equals("\\pi")) {
                        CalcArg[i] = ring.numberONE().pi(ring);
                        break;
                    }
                    CalcArg[i] = f.X[i];
                    break;
                default:
                    CalcArg[i] = f.X[i];
            }
        }
        Element res = null;
        Element aa;
        Element bb;
        Element cc;
        switch (f.name) {
            case F.ABS:
                do {
                    aa = CalcArg[0].abs(ring);
                    ring.setAccuracy(ring.getAccuracy() + 10);
                    bb = CalcArg[0].abs(ring);
                    res = bb.subtract(aa, ring);
                } while (res.compareTo(EPS) == 1);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.EXP:
                do {
                    aa = CalcArg[0].exp(ring);
                    ring.setAccuracy(ring.getAccuracy() + 10);
                    bb = CalcArg[0].exp(ring);
                    res = bb.subtract(aa, ring);
                } while (res.compareTo(EPS) == 1);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.SQRT:
                do {
                    aa = CalcArg[0].sqrt(ring);
                    ring.setAccuracy(ring.getAccuracy() + 10);
                    bb = CalcArg[0].sqrt(ring);
                    res = bb.subtract(aa, ring);
                } while (res.compareTo(EPS) == 1);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.COS:
                do {
                    aa = CalcArg[0].cos(ring);
                    ring.setAccuracy(ring.getAccuracy() + 10);
                    bb = CalcArg[0].cos(ring);
                    res = bb.subtract(aa, ring);
                } while (res.compareTo(EPS) == 1);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.SIN:
                do {
                    aa = CalcArg[0].sin(ring);
                    res = aa;
                    ring.setAccuracy(ring.getAccuracy() + 10);
                    bb = CalcArg[0].sin(ring);
                    cc = bb.subtract(aa, ring);
                } while (cc.compareTo(EPS) == 1);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.TG:
                res = CalcArg[0].tan(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.CTG:
                res = CalcArg[0].ctg(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.SH:
                res = CalcArg[0].sh(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.CH:
                res = CalcArg[0].ch(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.TH:
                res = CalcArg[0].th(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.CTH:
                res = CalcArg[0].cth(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.LOG:
                res = CalcArg[1].log(CalcArg[0], ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.LN:
                res = CalcArg[0].ln(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.LG:
                res = CalcArg[0].lg(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCSIN:
                res = CalcArg[0].arcsn(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCCOS:
                res = CalcArg[0].arccs(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCTG:
                res = CalcArg[0].arctn(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCCTG:
                res = CalcArg[0].arcctn(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCSH:
                res = CalcArg[0].arsh(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCCH:
                res = CalcArg[0].arch(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.ARCTGH:
                res = CalcArg[0].arth(ring);
                return (res == null) ? new F(f.name, CalcArg) : res;
            case F.BINOMIAL:
                return ((CalcArg[0].numbElementType() < Ring.Polynom) && (CalcArg[1].numbElementType() < Ring.Polynom))
                        ? Element.binomial(ring, CalcArg[0].intValue(), CalcArg[1].intValue())
                        : new F(F.BINOMIAL, new Element[] {CalcArg[0], CalcArg[1]});
            case F.intPOW:
                return CalcArg[0].pow(CalcArg[1].intValue(), ring);
            case F.POW:
                return CalcArg[0].pow(CalcArg[1], ring);
            case F.ARCCTGH:
                return new F(f.name, CalcArg);
            case F.ADD:
                res = CalcArg[0];
                for (int i = 1; i < CalcArg.length; i++) {
                    res = res.add(CalcArg[i], ring);
                }
                return res;
            case F.DIVIDE:
                return CalcArg[0].divide(CalcArg[1], ring);
            case F.SUBTRACT:
                return CalcArg[0].subtract(CalcArg[1], ring);
            case F.MULTIPLY:
                res = CalcArg[0];
                for (int i = 1; i < CalcArg.length; i++) {
                    res = res.multiply(CalcArg[i], ring);
                }
                return res;
        }
        return f;
    }

    /**
     * Процедура поднимающая последний элемент во вложенных Fname или F с
     * именами ID, либо их комбинациях.
     *
     * @param g - любой элемент
     *
     * @return
     */
    public Element ExpandFnameOrId(Element g) {
        if (g instanceof F) {
            return (((F) g).name == F.ID) ? ExpandFnameOrId(((F) g).X[0]) : g;
        }
        if (g instanceof Fname) {
            return (((Fname) g).X == null) ? g : ExpandFnameOrId(((Fname) g).X[0]);
        }
        return g;
    }

    public static void main(String[] args) {
            //Запуск функции
            Ring ring = new Ring("R[x,y,z]");
            FvalOf z = new FvalOf();
            z.ring = ring;
            F f1 = new F("\\sqrt(\\cos(\\sin(x)*\\cos(\\sin(x)))*\\cos(\\sin(x)*\\cos(\\sin(x)))^\\cos(\\sin(x)*\\cos(\\sin(x))))", ring);
            Element[] point2 = new Element[] {ring.posConst[3].pow(-10, ring), ring.posConst[4], ring.posConst[4]};
            z.valOfwithACC(f1, point2, 150);
            //0.999999999713202800539203374718
            //5.12345678910111246295855380594730377197265625
            //0.999999999713202801078406749282743843088503930546879
            //0.999999999713202800539203374718
            //0.9999999998566014002593201081552061404276816840019190496215035355308259933209716286300203531481648057131634769928043425399755965087423448236609302463324884063797936501664980526219387697786041765020891200670318343690050795672615772084420536496370486296
            //0.999999999856601400259320108155206140427681684001919049621503535530825993320971628630020353148164805713163476992804342539975596508742344823660930246332
            ////Запуск полинома

            // Ring ring=new Ring("R[x,y,z]");
            // FvalOf z=new FvalOf();
            // z.ring=ring;
            // Polynom df=new Polynom("x^2+y^3", ring);
            // Element[] point2=new Element[]{ring.posConst[2].pow(-10, ring), ring.posConst[5]};
            // z.valOfwithACC(df, point2, 50);
            //
            ////Запуск Вектора
            //Ring ring=new Ring("R[x,y,z]");
            //FvalOf z=new FvalOf();
            //z.ring=ring;
            //Polynom df=new Polynom("x^2+y^3", ring);
            //Polynom dff=new Polynom("x+y", ring);
            //Polynom ddff=new Polynom("x", ring);
            //Element[] vec=new Element[]{df, dff, ddff};
            //Element[] point2=new Element[]{ring.posConst[2].pow(-10, ring), ring.posConst[5]};
            //VectorS dd=new VectorS(vec);
            //z.valOfwithACC(dd, point2, 50);
            //
            ////Запуск матрицы
            //Ring ring=new Ring("R[x,y,z]");
            //FvalOf z=new FvalOf();
            //z.ring=ring;
            //Polynom df=new Polynom("x^2+y^3", ring);
            //Polynom dff=new Polynom("x+y", ring);
            //Polynom ddff=new Polynom("x", ring);
            //Element[][] mat=new Element[][]{{df, dff}, {dff, ddff}};
            //Element[] point2=new Element[]{ring.posConst[2].pow(-10, ring), ring.posConst[5]};
            //MatrixS ff=new MatrixS(mat, ring);
            //z.valOfwithACC(ff, point2, 50);
            // int gg=0;
            // do{
            //     gg=gg+1;
            //     System.out.println("gg= "+gg);
            // }while(gg<7);
    }
}
