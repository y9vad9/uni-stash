// discrepency// © Разработчик Смирнов Роман Антонович (White_Raven), ТГУ'10
package com.mathpar.func;
import com.mathpar.func.parser.Parser;
import java.io.File;
import java.io.FileInputStream;
import com.mathpar.students.OLD.stud2014.lukashin.TryGetSumProgress;
import com.mathpar.students.OLD.stud2014.lukashin.Sum;
import com.mathpar.parallel.webCluster.engine.AlgorithmsConfig;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import com.mathpar.parallel.webCluster.engine.QueryResult;
import com.mathpar.parallel.webCluster.engine.TaskConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
//import com.mathpar.laplaceTransform.*;
//import com.mathpar.lcTransform.*;
import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.polynom.*;
//import com.mathpar.diffEq.*;
//import com.mathpar.probability.RandomQuantity;
import com.mathpar.students.OLD.stud2014.kireev.TropicalProblems;
import com.mathpar.students.OLD.stud2014.kireev.equation;
import com.mathpar.students.OLD.stud2014.titov.SolveLEX;
import com.mathpar.web.exceptions.MathparException;
import java.util.regex.Matcher;
//import com.mathpar.funcSpec.*;
import com.mathpar.matrix.LDU.ETD;
import com.mathpar.matrix.LDU.TO.ETDpTO;
import java.io.Serializable; 

/**
 * 1. Арифметический уровень - обьект W, типа F с одним из имен : ADD, MULTIPLY,
 * DIVIDE , SUBTRACT, intPOW. Границей арифметического уровня являеться обьект
 * типа F с отличным от перечисленных имен именем либо любой другой наследник
 * класса Element , за исключением класса Fraction , равноценному имени DIVIDE.
 * Продолжением арифметического уровня являеться обькты подобные W. Таким
 * образом арифметический уровень это композиция обьектов W.
 *
 * Основные процедуры
 * 
 * ElementConvertToPolynom --- Сложная функция на верхнем уровне обращается в полином
 * ElementToPolynom --- обращается в полином или отношение полиномов
 *UnconvertAllLevels() -- восстановление функции из виртуального полинома (по всем уровням)
 * ElementToF -- восстановление функции из виртуального полинома, только по верхнему уровню
 */
public class CanonicForms {
   /** входное кольцо */
    public Ring RING;
    /** новое сформированное кольцо добавленные переменные которого обернутые функции */
    public Ring newRing; 
    /** являеться ли текущее кольцо комплексным */
    public boolean isComplexRing = false; 
    /** хранилище для узлов функций, которые заменяются переменными */
    public ArrayList<Element> List_of_Change = new ArrayList<Element>(); 
    /** индекс последней переменной в текущем кольце */
    public int index_in_Ring; 
    /** индекс последней переменной в текущем кольце после замен в интегрировании */
    public int index_var_for_Integration; 
    /** хранилище для полиномов (теперь полиномы равны, если их ссылки совпадают)*/
    public ArrayList<Element> List_of_Polynom = new ArrayList<Element>(); 
    /** Требуется ли приводить приводим ли к одному типу кольца */
    public boolean toOneRing = false; 
    /** Требуется ли автоматически раскрывать все скобки */
    public boolean expand_flag = false;  
    /** Указатель на основную страница */
    private Page page;  
    private int index_in_G_Ring = -1; // индекс для некоммутативных элементов
    private ArrayList<Element> G_ListChange = new ArrayList<Element>();// Список для замены элементов алгебры
    private int indexNameVariables = 0; // следим за именем индексированной переменной
    // поля используемые для удаления совпадающих объектов и фрагментов из функции F
    public Vector<com.mathpar.func.F>[] vectF;
    public Vector<Element>[] vectEl; // Vector<Element>[]
    public boolean changeDone=false;// Флаг свершения восстановления функции из полинома
                                    // Если после обхода он остался false, то изменения не было и восстановление закончилось
    /**
     * Имена всех встретившихся некоммутативных объектов. Используются для термов в алгебре
     * и искусственные имена для них
     */
    public ArrayList<String> G_List_Names = new ArrayList<String>();
    
    /**
     * Один шаг восстановления функции из виртуального полинома в исходный вид
     *
     * @param e  - Элемент любого типа, в котором могут присутствовать замененные на переменные подвыражения
     * @param f  - current CanonicForms
     * @return -  restored in one step Element
     */
    public Element UnconvertAllLevels(Element e ) {
        switch (e.numbElementType()) {
            case Ring.F:
                Element[] newArg = new Element[((F) e).X.length];
                for (int i = 0; i < ((F) e).X.length; i++) {
                    newArg[i] = UnconvertAllLevels(((F) e).X[i]);
                }
                return new F(((F) e).name, newArg);
            case Ring.Fname:
                if((((Fname) e).X==null)||(((Fname) e).X[0]==null)) return e;
                return new Fname(((Fname) e).name, UnconvertAllLevels(((Fname) e).X[0]));
            case Ring.Polynom:
                return  ElementToF(e);
            case Ring.FactorPol:
                Element[] newAr=new Element[((FactorPol)e).multin.length];
                for(int j=0;j<newAr.length;j++){
                newAr[j]=(((FactorPol)e).powers[j]!=1) ?
                    new F(F.intPOW, ElementToF(((FactorPol)e).multin[j]),RING.numberONE.valOf(((FactorPol)e).powers[j], RING)):
                      ElementToF(((FactorPol)e).multin[j]);
                }
             return newAr.length==1 ? newAr[0] : new F(F.MULTIPLY,newAr);
            case Ring.Rational:
            case Ring.Q:
  //            return new Fraction(UnconvertAllLevels(((Fraction) e).num), UnconvertAllLevels(((Fraction) e).denom));
                return new F(F.DIVIDE,  UnconvertAllLevels(((Fraction) e).num), UnconvertAllLevels(((Fraction) e).denom));
            default:
                return e;
        }
    }

    /**
     * Полное восстановление функции из виртуального полинома
     *
     * @param e  - Элемент любого типа, в котором могут присутствовать замененные на переменные подвыражения
     * @param f  - current CanonicForms
     * @return -  restored Element
     */
    public static Element UnconvertAllLevelsFunction_(Element e, CanonicForms f) {
        f.changeDone = false;
        Element e1 = f.UnconvertAllLevels(e);
        while (f.changeDone) { e1 = f.UnconvertAllLevels(e1);}
        return e1;
    }
   
    //-------------------------------------------------------------------------------
   // поля для генерации греческих букв
    public  String gr_words[] = {"mu","nu","psi","eta","alpha", "beta", "gamma", "delta", "epsilon", "zeta",
            "theta", "iota", "kappa", "lambda",  "xi", "omicron", "rho", "sigma", "tau",
            "upsilon", "phi", "chi",  "omega"};
    List<String> gr_vec_words=Arrays.asList(gr_words);
    public int indexs_free_gr_words[]=new int[gr_words.length];
    int index_of_last_gr_word=0;
    //--------------------------------------------------------------------------------

    public CanonicForms() {}

//    /**
//     * Конструктор от кольца
//     * @param r
//     */
//    public CanonicForms(Ring ring) {
//        RING = ring; ring.CForm=this;
//        newRing = RING;
//        index_in_Ring = RING.varNames.length + 1;
//        int typeEl=ring.numberONE.numbElementType();
//        isComplexRing = ((typeEl&Ring.Complex) == Ring.Complex) ;
//        vectEl=this.vectEl;
//        vectF=this.vectF;
//    }
     /**
     * Специальный конструктор для конвертации
     * @param ring -- входное кольцо
     * @param flag -- флаг для создания векторов
     *  Используется исключительно при создании кольца пользователем 
     * Предполагается, что векторы F и El будут одни и теже пока пользователь не обновит кольцо
     * При запуске клона или потоков на кольцо отрезать CForm нужно ручками.
     * Всегда CForm  берем из ring
     */
    public CanonicForms(Ring ring, boolean flagMakeVector) {
        RING = ring;  
        newRing =  Ring.cloneWithoutCFormPage(ring);
        index_in_Ring = RING.varNames.length + 1;
        index_var_for_Integration = RING.varNames.length; 
        int typeEl=ring.numberONE.numbElementType();
        isComplexRing = ((typeEl&Ring.Complex) == Ring.Complex) ;
        if (flagMakeVector) { makeNewVectFandVectEL();
        }else{
            vectEl=this.vectEl; vectF=this.vectF;}
        RING.CForm=this; 
    }

//================================================================================================
//============  Вспомогательные процедуры ========================================================
    /**
     * Возвращаем начальные пустые значения  следующим полям:
     * 1.index_in_Ring;
     * 2.index_in_G_Ring;
     * 3.newRing;
     * 4.List_of_Change;
     * 5.List_of_Polynom.
     */
    public void returnFirstStatsCForm() {
        index_in_Ring = RING.varNames.length + 1;
        index_in_G_Ring = -1;
        int RvNl=RING.varNames.length;
        if(newRing.varNames.length>RvNl)
        {  Polynom[] PP=new Polynom[RvNl];
            String[] SS=new String[RvNl];
            int[] vLI =new int[RING.varLastIndices.length];
            System.arraycopy(RING.varNames, 0, SS, 0, RvNl);
            System.arraycopy(RING.varPolynom, 0, PP, 0, RvNl);
            System.arraycopy(RING.varLastIndices, 0, vLI, 0, RING.varLastIndices.length);
            newRing.varNames=SS; newRing.varPolynom=PP; newRing.varLastIndices=vLI;
        }
       cleanLists();
    }
    /**
     * Создаем новое кольцо такого же рода как и входное но 
     * с колличесвом переменных равным значению "index_in_Ring-RING.varNames.length-1"
     * Только для одного типа алгебр.
     */
    public Ring makeWorkRing(int colNewVar){
        int nRVl=newRing.varNames.length;
        int numNew=Math.max(colNewVar, index_in_Ring-RING.varNames.length-1); if (numNew<=0) return newRing;
        int k=0;
        for (int i = 0; i < nRVl; i++) {
            String str=newRing.varNames[i]; int ind=str.indexOf("c$_"); if(ind==-1) continue;
            str=str.substring(ind+3).trim(); k=new Integer(str);
        } // к -- самый большой номер среди "c$_" или 0  (у них счет с с$_1, а с$_0 не используется)
        numNew-=k; if (numNew<=0) return newRing;
        int len=nRVl+numNew;
        String[] VN=new String[len]; Polynom[] VP= new Polynom[len];
        System.arraycopy(newRing.varNames, 0,VN,0, nRVl);
        System.arraycopy(newRing.varPolynom, 0,VP,0, nRVl);
        for (int i = nRVl; i < len; i++) {VN[i]="c$_"+(++k);
            int[] pow = new int[i + 1];
            pow[i] = 1;
            VP[i] = new Polynom(pow, new Element[] {RING.numberONE});
        }  
        newRing.varNames=VN; newRing.varPolynom=VP; 
        newRing.varLastIndices[0]=len-1;
        return newRing;
     }


    /**
     * Создаем одно кольцо из многих, путем добовления пременных в первое,
     * которое является по умолчанию самым тяжелым. F.e. на входе получаем -
     * R[x,y]R64[z,v]Q[r]Z[] после преобразования - R[x,y,z,v,r]
     * @param inputRing - входное кольцо
     * @return
     */
    private Ring createUnicRing(Ring inputRing) {
        StringBuilder r = new StringBuilder(inputRing.toString());
        StringBuilder out = new StringBuilder();
        out.append(r.substring(0, r.indexOf("]")));
        r.delete(0, r.indexOf("]") + 1);
        int index_Br_OP = r.indexOf("[");
        int index_Br_CL = 0;
        while (index_Br_OP != -1) {
            out.append(",");
            index_Br_CL = r.indexOf("]");
            out.append(r.substring(index_Br_OP + 1, index_Br_CL));
            r.delete(0, index_Br_CL + 1);
            index_Br_OP = r.indexOf("[");
        }
        return new Ring(out.toString() + "]");
    }

    /**
     * Переводим листы дерева функции func в тип алгебры Algebra из списка колец
     * в классе Ring.
     *
     * @param Algebra
     * @param func
     *
     * @return
     */
    public F AllElementsToOneRing(int Algebra, F func) {
        Element[] newArguments = new Element[func.X.length];
        for (int i = 0; i < func.X.length; i++) {
            newArguments[i] = (func.X[i] instanceof F)
                    ? AllElementsToOneRing(Algebra, (F) func.X[i])
                    : func.X[i].toNewRing(Algebra, RING);
        }
        return new F(func.name, newArguments);
    }

    /**
     * Вспомогательная процедура для образования дерева функции.
     *
     * @param vec - вектор элементов
     * @param name - имя корня
     *
     * @return
     */
    private Element convertVecToEl(Vector<Element> vec, int name) {
        if (vec.size() == 1) {return vec.firstElement();}
        Element[] arg = new Element[vec.size()];
        vec.copyInto(arg);
        return new F(name, arg);
    }

    /**
     * Метод конвертирующий элемент типа FactorPol в F
     *
     * @param factorpol - входной FactorPol или Factor
     *
     * @return
     */
    private Element convert_FactorPol_to_El(Element factorpol) {
        if(factorpol instanceof FactorPol){
        Vector<Element> newX = new Vector<Element>();
        for (int i = 0; i < ((FactorPol)factorpol).multin.length; i++) {
            Element temp = UnconvertAllLevels(((FactorPol)factorpol).multin[i]);
            if (((FactorPol)factorpol).powers[i] == 1) {
                newX.add(temp);
            } else {
                newX.add(new F(F.intPOW, temp,
                        RING.numberONE.valOf(((FactorPol)factorpol).powers[i], RING)));
            }
        } 
        return  convertVecToEl(newX, F.MULTIPLY);
        }else{
        Vector<Element> newX = new Vector<>();
        for (int i = 0; i < ((Factor)factorpol).multin.size(); i++) {
            Element temp = Unconvert_Polynom_to_Element(((Factor)factorpol).multin.get(i));
            if (((Factor)factorpol).powers.get(i).isOne(RING)) {
                newX.add(temp);
            } else {
                newX.add(new F(F.intPOW, temp,
                        RING.numberONE.valOf(((Factor)factorpol).powers.get(i).intValue(),RING)));
            }
        }
        return  convertVecToEl(newX, F.MULTIPLY);
        }
     }

    /**
     * Метод изменяющий последнее значение в массиве степеней полинома p на 1
     *
     * @param p - входной полином
     *
     * @return p^1
     */
    private Element polynomWithOnePow(Polynom p) {
        int[] np = new int[p.powers.length];//           p.powers.cloneWithoutCFormPage();
        np[np.length - 1] = 1;
        return new Polynom(np, p.coeffs);
    }

    /**
     * Создаем полином , имя берем из текущего кольца , тип коэф-тов NumberZ
     *
     * @param index_inRing - место полинома в кольце
     * @param pow - степень будущего полинома
     *
     * @return полином в степени pow
     */
    private Polynom makePolynom(int index_inRing, int pow) {
        int[] power = new int[index_inRing];
        power[index_inRing - 1] = pow;
        Element[] coef = new Element[] {RING.numberONE};
        return new Polynom(power, coef);
    }

    /**
     * Метод очищающий поля List_of_Change и List_of_Polynom
     */
    public void cleanLists() {
        List_of_Change.clear();
        List_of_Polynom.clear();
    }

    /**
     * Метод инициализирующий поля vectF и vectEl
     */
    public void makeNewVectFandVectEL() {
        vectEl = new Vector[Ring.NumberOfElementTypes];
        for (int i = 0; i < Ring.NumberOfElementTypes; i++) {
            vectEl[i] = new Vector<Element>();
        }
        vectF = new Vector[F.FUNC_ARR_LEN];
        for (int i = 0; i < F.FUNC_ARR_LEN; i++) {
            vectF[i] = new Vector<F>();
        }
    }
//==================================================================================================
//==================================================================================================
//========================= Конвертация из полинома в функцию =======================================

    /**
     * Процедурка разьединяющая один полином на много
     *
     * @param pol - входной полином
     *
     * @author White_Raven
     * @return массив мономов pol
     */
    public Polynom[] dividePolynomToMonoms(Polynom pol) {
        if (pol.isItNumber()) {
            return new Polynom[] {pol};
        }
        Vector<Polynom> Res = new Vector<Polynom>();
        int kol = pol.powers.length / pol.coeffs.length;
        int pos = 0;
        for (int i = 0; i < pol.coeffs.length; i++) {
            if (!pol.coeffs[i].isZero(RING)) {
                int[] newPow = new int[kol];
                System.arraycopy(pol.powers, pos, newPow, 0, kol);
                Res.add(new Polynom(newPow, new Element[] {pol.coeffs[i]}).normalNumbVar(RING));
            }
            pos += kol;
        }
        Polynom[] Result = new Polynom[Res.size()];
        Res.copyInto(Result);
        return Result;
    }

    /**
     * Процедура конвертирующая полином в F с учетом List_of_Change и
     * List_of_Polynom
     *
     * @param p - входной полином
     *
     * @return
     */
    private Element monom_to_F(Polynom p) {
 
        int numRealPolynoms = RING.varNames.length; // кол-во реальных полиномов
        p = p.normalNumbVar(RING);
        if (p.powers.length < numRealPolynoms) return p;
        Vector<Element> Xarr = new Vector<Element>(); // массив аргументов будущего произведения
        // формируем коэффициет и если он не равен единицы добавляем в Xarr
        int polMonLen=-1; 
        for (int i = numRealPolynoms-1; i >-1; i--) {; if (p.powers[i]!=0){polMonLen= i; break;}}
        int[] powerr = new int[polMonLen+1];
        System.arraycopy(p.powers, 0, powerr, 0, polMonLen+1);
        Element coef=(polMonLen==-1)? p.coeffs[0]: new Polynom(powerr, new Element[] {p.coeffs[0]});
        if (!coef.isOne(RING))  Xarr.add(coef);
        // конвертируем мнимые полиномы обратно в функции
        int st=Math.max(p.powers.length- List_of_Change.size(),RING.varNames.length);
        for (int i = st; i < p.powers.length; i++) {
            if (p.powers[i] != 0) {
                if (p.powers[i] == 1) {Xarr.add(List_of_Change.get(i - numRealPolynoms));
                } else { Xarr.add(new F(F.intPOW, List_of_Change.get(i
                                - numRealPolynoms), new NumberZ(p.powers[i])));
                }
            }
        }
        if (Xarr.isEmpty())  return coef; // если аргументов нет , то возвращаем коэффициент
        return convertVecToEl(Xarr, F.MULTIPLY); // возвращаем результат
    }

//    /**
//     * Процедура конвертирующая полином в F с учетом List_of_Cange и
//     * List_of_Polynom
//     *
//     * @param p - входной полином
//     *
//     * @return
//     */
//    public Element convert_Polynom_to_F!!!!!!!!!(Element el) {
//        if (el instanceof Polynom) {
//            Polynom p = (Polynom) el;
//            if (p.isItNumber()) {
//                return (p.isZero(RING)) ? RING.numberONE().zero(RING)
//                        : p.coeffs[0];
//            }
//            int numRealPolynoms = RING.varNames.length;// кол-во реальных полиномов
//            if (p.powers.length / p.coeffs.length <= numRealPolynoms) {
//                return p;
//            }
//            Polynom[] masPol = dividePolynomToMonoms(p); // разбиваем полином на мономы
//            Vector<Element> vec = new Vector<Element>();
//            Polynom Add = new Polynom(new int[] {}, new Element[] {});// сдесь будет накапливаться сумма обычных
//            for (Polynom pol : masPol) { // пробегаем по мономам и конвертируем их
//                if (pol.powers.length < numRealPolynoms) {
//                    Add = Add.add(pol, RING);
//                } else {
//                    vec.add(monom_to_F(pol));
//                }
//            }
//            if (!Add.isZero(RING)) {
//                if (Add.isItNumber()) {
//                    vec.add(0, Add.coeffs[0]);
//                } else {
//                    vec.add(0, Add);
//                }
//            }
//            return convertVecToEl(vec, F.ADD); // возвращаем результат
//        } else {
//            if (el instanceof Fraction) {
//                return new Fraction(ElementToF(((Fraction) el).num), ElementToF(((Fraction) el).denom));
//            }
//
//        }
//        return el;
//    }  ГИМ  13-12-13 !!!!!!!!!!!!!!111

    /**
     * Метод перегонки из полинов в функции объектов типа MatrixSF
     *
     * @param el
     *
     * @return
     */
    private MatrixD unconvertMSF(MatrixD el) {
        Element[][] newM = new Element[el.M.length][];
        int indexConvrert = 0;
        int lastType = Ring.Polynom;
        Element matrixEl;
        for (int i = 0; i < newM.length; i++) {
            newM[i] = new Element[el.M[i].length];
            for (int j = 0; j < newM[i].length; j++) {
                matrixEl = UnconvertToElement(el.M[i][j]);
                if (matrixEl.numbElementType() > lastType) {
                    indexConvrert++;
                }
                newM[i][j] = matrixEl;
            }
        }
        return new MatrixD(new MatrixD(newM), (indexConvrert>0)?1:0);
    }

    /**
     * Метод перегонки из полинов в функции объектов типа VectorS
     *
     * @param el
     *
     * @return
     */
    private VectorS unconvertVSF(VectorS el) {
        Element[] newV = new Element[el.V.length];
        int indexConvrert = 0;
        int lastType = Ring.Polynom;
        Element matrixEl;
        for (int i = 0; i < newV.length; i++) {
            matrixEl = UnconvertToElement(el.V[i]);
            if (matrixEl.numbElementType() > lastType) {
                indexConvrert++;
            }
            newV[i] = matrixEl;
        }
        int Resfl=(indexConvrert>0)? (el.fl<0)?-2:1: (el.fl<0)?-1:0;
        return new VectorS(newV, Resfl);
    }

    /**
     * Универсальные метод реконвертации (для одного уровня)
     * из виртуального полинома или дроби обратно в композицию функций
     * Для тензоров выполняется поэлементно.
     *
     * @param El - входной элемент
     *
     * @return
     */
    public Element ElementToF(Element El) {
        if (El == null) { return null;}
        switch (El.numbElementType()) {
           case Ring.G:  // это будем удалять и я не менял тут ничего----
               return (((Algebra) El).coeff.isEmpty()) ? RING.numberZERO() : Algebra2Element((Algebra) El, true, true);
            case Ring.Polynom:
                return Unconvert_Polynom_to_Element(El);
            case Ring.Q:  case  Ring.CQ: case Ring.Z:  case  Ring.CZ:
            case Ring.Rational: {Fraction Res;
                if(El instanceof Fraction)Res = (Fraction) El;
                else Res = new Fraction(El,NumberZ.ONE);
                if (Res.num.numbElementType() == Ring.Polynom && Res.denom.numbElementType() == Ring.Polynom) {
                    if (((Polynom) Res.num).isItNumber() & ((Polynom) Res.denom).isItNumber()) {
                        return (RING.algebra[0] == Ring.Z) ? Res.cancel(RING) : ((Polynom) Res.num).divideExact((Polynom) Res.denom, RING);
                    }
                }
                if (Res.num.numbElementType() < Ring.Polynom && Res.denom.numbElementType() < Ring.Polynom) {
                    return   // (RING.algebra[0] == Ring.Z) ? 
                            Res.cancel(RING);// : (Res.num).divide(Res.denom, RING);
                }
                if (Res.denom.isOne(RING))  return ElementToF(Res.num);
                return new F(F.DIVIDE, ElementToF(Res.num), ElementToF(Res.denom));
            }
            case Ring.MatrixD:
                return (((MatrixD) El).fl == 0) ? El : unconvertMSF((MatrixD) El);
            case Ring.VectorS:
                return (((VectorS) El).fl == 0) ? El : unconvertVSF((VectorS) El);
            case Ring.MatrixS:
                return matrixP2F(El);
            case Ring.FactorPol:
                Element[] newMultin =new Element[((FactorPol)El).multin.length];
                for(int i=0; i < newMultin.length;i++){
                  newMultin[i]=ElementToF(((FactorPol)El).multin[i]);
                }
                return new Factor(newMultin, ((FactorPol)El).powers);
             case Ring.VectorSet:
                VectorS VS= new VectorS(((VectorSet)El).V);
                Element[]  VVV = new Element[VS.V.length];
                for (int i = 0; i < VS.V.length; i++) VVV[i]= vectorP2F((VectorS)((VectorS)VS.V[i]));
                return new  VectorSet(VVV);
             case Ring.F:  F fT=(F)El;  int nn=fT.name;  
                   if(F.isInfixName(nn)) { Element[] ResE=new Element[fT.X.length] ;
                       for (int i = 0; i < fT.X.length; i++) {ResE[i]= ElementToF(fT.X[i]);}                     
                       return new F(nn, ResE); 
                   }
            default:
                return El;
        }
    }

    /**
     * Конвертируем элементы вектора из полиномов в функию
     *
     * @param el
     *
     * @return
     */
    public VectorS vectorP2F(VectorS el) {
        Element[] newV = new Element[el.V.length];
        for (int i = 0; i < newV.length; i++) {
            newV[i] = ElementToF(el.V[i]);
        }
        return new VectorS(newV, (el.fl<0)?-1:0);
    }

//============================================================================================
//============================================================================================
//============================= Входная форма ================================================
//============================================================================================
    /**
     * Выполняем оператор GCD
     *
     * @param ARGUMENTS
     *
     * @return
     */
    private Element runOperatorGCD(Element[] ARGUMENTS) {
    //    try 
            if (ARGUMENTS.length==1)
               if (ARGUMENTS[0] instanceof VectorS) return Element.arrayGCD(((VectorS)ARGUMENTS[0]).V, RING);
               else return ARGUMENTS[0];
            return  simplify_init(ARGUMENTS[0]).GCD( simplify_init(ARGUMENTS[1]), RING);
//        } catch (Exception ex) {
//            RING.exception.append("\n Catched in CanonicForms when try GCD in runOperatorGCD");
//            return null;
//        }
    }

    /**
     * Выполняем оператор LCM
     *
     * @param ARGUMENTS
     *
     * @return
     */
    private Element runOperatorLCM(Element[] ARGUMENTS) {
        try {if(ARGUMENTS.length==1)
               if (ARGUMENTS[0] instanceof VectorS) return Element.arrayLCM(((VectorS)ARGUMENTS[0]).V, RING);
               else return ARGUMENTS[0];
            return simplify_init(ARGUMENTS[0]).LCM( simplify_init(ARGUMENTS[1]), RING);
        } catch (Exception ex) {
            RING.exception.append("\n Catched in CanonicFormF when try GCD in runOperatorLCM");
            return null;
        }
    }

    /**
     * Запускаем оператор D, т.е. вычисляем производную ARGUMENTS[0] по
     * переменным ARGUMENTS[1],ARGUMENTS[2],.....,ARGUMENTS[ARGUMENTS.length-1].
     * Если длина массива ARGUMENTS == 1 то вычисляем по первой переменной в
     * текущем кольце.
     *
     * @return производная ARGUMENTS[0].
     */
    private Element runOperatorD(Element[] ARGUMENTS) {
        if (ARGUMENTS.length == 1) {
            switch (ARGUMENTS[0].numbElementType()) {
                case Ring.Fname:
                case Ring.VectorS:
                case Ring.MatrixD:
                case Ring.Polynom:
                    return ARGUMENTS[0].D(0, RING);
                case Ring.F:
                    return ARGUMENTS[0].D(0, RING);
                default:
                    return ARGUMENTS[0].zero(RING);
            }
        }
         if(!(ARGUMENTS[1] instanceof Polynom)) ARGUMENTS[1]=expandFnameOrId(ARGUMENTS[1]);
        if(ARGUMENTS.length==2 && (ARGUMENTS[1] instanceof Polynom)) return ARGUMENTS[0].mixedDerivativ((Polynom)ARGUMENTS[1], RING);
        int ind = 0;
        int[] mixedDerivative, colMixDerivative;
        Element[] DerivativeVariables;
        Element DV = ARGUMENTS[1];
        if (DV instanceof VectorS) {
            DerivativeVariables = ((VectorS) DV).V;
            mixedDerivative = new int[DerivativeVariables.length];
            colMixDerivative = new int[DerivativeVariables.length];
        } else {
            DerivativeVariables = new Element[] {DV};
            mixedDerivative = new int[1];
            colMixDerivative = new int[1];
        }
        int j = 0;
        for (int i = 0; i < DerivativeVariables.length; i++) {
            if (DerivativeVariables[i] instanceof Polynom) {
                j = ((Polynom) DerivativeVariables[i]).powers.length - 1;
                mixedDerivative[i] = j;
                colMixDerivative[i] = ((Polynom) DerivativeVariables[i]).powers[j];
                ind++;
            } else {
                throw new RuntimeException("Derivative variable is not polynomial: " + DerivativeVariables[i]);
            }
        }
        if (ind != mixedDerivative.length) {
            RING.exception.append("\n"
                    + "Not specify a variable in the derivative in ring (CanonicForms.runOperatorD) ").append(RING);
            return new F(F.D, ARGUMENTS);
        }
        Element FuncDerivative = ARGUMENTS[0];
        switch (FuncDerivative.numbElementType()) {
            case Ring.VectorS:
            case Ring.MatrixD:
                return mixedDerivativeNCElements(FuncDerivative, mixedDerivative, colMixDerivative);
            case Ring.Fname:
            case Ring.F:
            case Ring.Polynom: {
                if (ARGUMENTS.length == 3) {//NEW!!!!!!!!!!!!!!!!!! для дифф функций ARGUMENTS[2] раз
                    for (int i = 0; i < ARGUMENTS[2].intValue(); i++) {
                        FuncDerivative = mixedDerivative(FuncDerivative, mixedDerivative, colMixDerivative);
                    }
                    return FuncDerivative;
                } else {
                    return mixedDerivative(FuncDerivative, mixedDerivative, colMixDerivative);
                }
            }
            default:
                return RING.numberZERO;// пришло число
        }
    }


    private Element getElWihOutID(Element el){
    return (!(el instanceof F))?el : ((F)el).name==F.ID ? getElWihOutID(((F)el).X[0]) :el;
    }


    /**
     * *
     * Вычисление смешанных производных поэлементно у матриц и векторов
     *
     * @param el
     * @param mix
     * @param col
     *
     * @return
     */
    private Element mixedDerivativeNCElements(Element el, int[] mix, int[] col) {
        if (el instanceof VectorS) {
            for (int i = 0; i < ((VectorS) el).V.length; i++) {
                ((VectorS) el).V[i] =getElWihOutID(mixedDerivative(((VectorS) el).V[i], mix, col).expand(RING));
            }
            return el;
        }
        Element[][] newM = new Element[((MatrixD) el).M.length][];
        for (int i = 0; i < ((MatrixD) el).M.length; i++) {
            newM[i] = new Element[((MatrixD) el).M[i].length];
        }
        for (int i = 0; i < newM.length; i++) {
            for (int j = 0; j < newM[i].length; j++) {
                newM[i][j] =getElWihOutID(mixedDerivative(((MatrixD) el).M[i][j], mix, col).expand(RING));
            }
        }
        return new MatrixD(newM, ((MatrixD) el).fl);
    }

    /**
     * Метод вычисляющий смешанную производную в соответствии с массивом mix.
     *
     * @param el - элемент который необходимо продифферениировать
     * @param mix - массив индексов переменных текущего кольца.
     *
     * @return
     */
    private Element mixedDerivative(Element el, int[] mix, int[] col) {
        int index = 0;
        for (int l : mix) {
            for (int i = 0; i < col[index]; i++) {
                if (el.isZero(RING)) {
                    return el;
                } else {
                    el = el.D(l, RING);
                }
            }
            index++;
        }
        return el;
    }

    /**
     * Метод производящий факторизацию аргумента узла с именем FACTOR
     *
     * @param expf - аргумент
     *
     * @return факторизованное по возможности выражение
     */
    private Element runOperatorFactor(Element expf) {
       switch (expf.numbElementType()) {
            case Ring.VectorS:
            case Ring.MatrixS:
            case Ring.MatrixD:
                return expf.factor(RING);
            case Ring.Fname:
                expf = expf.ExpandFnameOrId();
                if (expf.numbElementType() == Ring.Polynom) {
                    return (((Polynom) expf).factorOfPol_inQ(false, newRing));
                } else {
                    RING.exception.append("\nFname-type expresion: Error factorization in CanonicForms.runOperatorFactor ").append(expf.toString(RING));
                    return new F(F.FACTOR, expf);
                }
            case Ring.Polynom:
                return (((Polynom) expf).factorOfPol_inQ(false, newRing));
            case Ring.F:  Element  newP= El2Pol(expf); Element den=null;
                int numbVars=index_in_Ring-RING.varNames.length-1; makeWorkRing(numbVars);
                if(newP instanceof Fraction){den=((Fraction)newP).denom; newP= ((Fraction)newP).num;}
                FactorPol ffp=((Polynom)newP).factorOfPol_inQ(false,newRing);
                if(den!=null) ffp=ffp.divide(((Polynom)den).factorOfPol_inQ(false,newRing), RING);
                return  ElementToF(ffp);
            default:
                try{
                  if (expf.isItNumber()){
                     NumberZ nz= (NumberZ)expf.toNumber(Ring.Z, RING);
                     if(!(nz.abs().compareTo(new NumberZ(0x7FFFFFFFFFFFFFFFL), Element.GREATER, RING)))
                         return NFunctionZ32.factoringLong(nz.longValue());
                           }
                  RING.exception.append("\n"
                        + " This element can not factorizate. It's value is greater then 2^63-1. ").append(expf.toString(RING));
                  return expf;}
                catch(Exception e) {RING.exception.append("\n"
                        + " This element may be not less then 2^63. It can not be factorized in current version: ")
                          .append(expf.toString(RING)).append(".  "+e);
                  return expf;}
        }
    }
   // private boolean radianFlag; // флаг отвечающий в какой мере исчесляются углы

    /**
     * Метод переводящий узел VALUE(X[0],X[1]) в посчитанное значение
     *
     * @param ARGUMENTS - аргументы функции VALUE
     *
     * @return значение X[0] в точке X[1]
     */
    private Element runOperatorValue(Element[] ARGUMENTS) { 
        Element expV = ARGUMENTS[0]; // функция которую надо посчитать
        //если длинна point равна нулю
        if (ARGUMENTS.length == 1) {
            int var=RING.varNames.length; int le= page.expr.size();
            Element[] ValuesOfVars=new Element[var];
            for (int i = 0; i < var; i++) { String s=RING.varNames[i];
               find:{for (int j = le-1; j >= 0; j--) { Element g=page.expr.get(j);
                if(g instanceof Fname){Fname fn=(Fname)g;
                    if((fn.name.equals(s))&&fn.X!=null){ValuesOfVars[i]=fn.X[0]; break find;}}
                } ValuesOfVars[i]=RING.varPolynom[i];  }
            } 
            switch (ARGUMENTS[0].numbElementType()) {
                case Ring.Fname:
                    page.replaceOfSymbols(ARGUMENTS[0], page.expr);
                    return new FvalOf(RING).valOf(ARGUMENTS[0], ValuesOfVars); //new Element[0]);
                case Ring.F:
                    page.replaceOfSymbols(ARGUMENTS[0], page.expr);
                    return new FvalOf(RING).valOf(ARGUMENTS[0], ValuesOfVars); //new Element[0]);
                case Ring.Polynom:
                    return new FvalOf(RING).valOfToPolynom((Polynom) ARGUMENTS[0], ValuesOfVars); //new Element[0]);
                 case Ring.VectorS:
                    FvalOf fv1=new FvalOf(RING);
                    VectorS uncovVec=(VectorS)ElementToF(ARGUMENTS[0]);
                    Element [] resV1=new Element[uncovVec.V.length];
                    for(int i=0;i<resV1.length;i++){
                     page.replaceOfSymbols(uncovVec.V[i], page.expr);
                     resV1[i]=(uncovVec.V[i] instanceof Polynom) ? fv1.valOfToPolynom((Polynom)uncovVec.V[i], ValuesOfVars): //new Element[0]):
                             fv1.valOf(uncovVec.V[i], ValuesOfVars); //new Element[0]);
                    } int Fl=((VectorS)ARGUMENTS[0]).fl;
                    return (( Fl>0)||( Fl<-1)) ? new VectorS(VecElements2Polynom(new VectorS(resV1)),(Fl<0)?-2:1) : new VectorS( resV1,Fl);
                 case Ring.MatrixD:
                  FvalOf fvm=new FvalOf(RING);
                  MatrixD uncovMat=((MatrixD)ElementToF(ARGUMENTS[0]));
                  Element[][] resValm=uncovMat.M.clone();
                  for(int i=0;i<uncovMat.M.length;i++){
                   for(int ind=0; ind<uncovMat.M[i].length;ind++){
                   page.replaceOfSymbols(uncovMat.M[i][ind], page.expr);
                   resValm[i][ind]=(uncovMat.M[i][ind] instanceof Polynom) ? fvm.valOfToPolynom((Polynom)uncovMat.M[i][ind], ValuesOfVars): //new Element[0]):
                             fvm.valOf(uncovMat.M[i][ind], ValuesOfVars); //new Element[0]);
                   }
                  }
                  return (((MatrixD)ARGUMENTS[0]).fl!=0)? new MatrixD(MatrixElements2Pol(new MatrixD(resValm)), 1):
                            new MatrixD(resValm, 0);
                 default:
                    return ARGUMENTS[0];
            }
        }
        // заполняем point
        Element[] point;
        if (ARGUMENTS[1] instanceof VectorS) {
            point = ((((VectorS) ARGUMENTS[1])).fl == 0) ? 
                    (((VectorS) ARGUMENTS[1])).V : 
                    vectorP2F((((VectorS) ARGUMENTS[1]))).V;
        } else {
            int len = ARGUMENTS.length - 1;
            point = new Element[len];
            for (int i = 0; i < point.length; i++) {
                point[i] = ARGUMENTS[i + 1];
            }
        }    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        switch (expV.numbElementType()) {
            case Ring.F:
                Element Vf = new FvalOf(RING).valOf((F) expV, point);
                if (Vf == null) {  // тут было ind-in-r=4 и   w=t (один в списке...)и подставленная взад w
                    RING.exception.append("\n"
                            + " Calculation error in FvalOf : ").append((new F(F.VALUE, ARGUMENTS)).toString(RING));
                    return new F(F.VALUE, ARGUMENTS);
                } else {
                    return Vf;
                }
            case Ring.FactorPol: expV=((FactorPol)expV).toPolynomOrFraction(RING);
                 if(expV instanceof Fraction) return runOperatorValue(new Element[] {new F(F.DIVIDE,
                     ((Fraction)expV).num,((Fraction)expV).denom),ARGUMENTS[1] });
            case Ring.Polynom:
                Element Vp = new FvalOf(RING).valOfToPolynom((Polynom) expV, point);
                if (Vp == null) {
                    RING.exception.append("\n"
                            + " Calculation error in valOfToPolynom :").append((new F(F.VALUE, ARGUMENTS)).toString(RING));
                    return new F(F.VALUE, ARGUMENTS);
                } else {return Vp;}
            case Ring.Fname: return ARGUMENTS[0];
            case Ring.VectorS:
            case Ring.MatrixD:
                return ARGUMENTS[0].value(point, RING);
//                RING.exception.append("\n" + " Calculation error for default ").append((new F(F.VALUE, ARGUMENTS)).toString(RING));
//                return new F(F.VALUE, ARGUMENTS); 11.11.2016
            default:
                return expV;
        }
    }

    public Element matrixP2F(Element matr) {
        if (matr instanceof MatrixS) {
            Element[][] dm = ((MatrixS) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < newM.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    newM[i][j] = Unconvert_Polynom_to_Element(dm[i][j]);
                }
            }
            return new MatrixS(newM, ((MatrixS) matr).col);
        } else {
            if (matr instanceof Polynom) {
                Element res = Unconvert_Polynom_to_Element(matr);
                cleanLists();
                index_in_Ring = 0;
                return res;
            }
            Element[][] dm = ((MatrixD) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < newM.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    newM[i][j] = Unconvert_Polynom_to_Element(dm[i][j]);
                }
            }
            return new MatrixD(newM,0);
        }
    }

    public Element vectorF2P(VectorS vec) {
        index_in_Ring = (index_in_Ring > (RING.varNames.length + 1))
                ? index_in_Ring : (RING.varNames.length + 1);
        Element[] newV = new Element[vec.V.length];
        for (int j = 0; j < vec.V.length; j++) {
            Element el = CleanElement(vec.V[j]);
            newV[j] = toMatrixConvertWTD(el);
        } makeWorkRing(index_in_Ring - RING.varNames.length-1);
      //  newRing = addNewVariables(index_in_Ring - RING.varNames.length);
        return new VectorS(newV, vec.fl);
    }

    /**
     *
     * @param matr
     *
     * @return
     */
    public Element matrixF2P(Element matr) {
        //Перед вызовом необходимо создать вектора для F.cleanOfRepeating
        if(index_in_Ring > (RING.varNames.length + 1))
                 index_in_Ring = (RING.varNames.length + 1);
        if (matr instanceof MatrixS) {
            Element[][] dm = ((MatrixS) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < dm.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    Element el = CleanElement(dm[i][j]);
                    newM[i][j] = toMatrixConvertWTD(el);
                }
            }makeWorkRing(index_in_Ring - RING.varNames.length-1);
          //  newRing = addNewVariables(index_in_Ring - RING.varNames.length);
            return new MatrixS(newM, ((MatrixS) matr).col);
        } else {if(((MatrixD) matr).fl>0)return matr;
            Element[][] dm = ((MatrixD) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < dm.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    Element el = CleanElement(dm[i][j]);
                    newM[i][j] = toMatrixConvertWTD(el);
                }
            }
            makeWorkRing(index_in_Ring - RING.varNames.length-1);
           // newRing = addNewVariables(index_in_Ring - RING.varNames.length);
            return new MatrixD(newM, ((MatrixD) matr).fl);
        }
    }

    private Element toMatrixConvertWTD(Element el) {
        switch (el.numbElementType()) {
            case Ring.F: {
                switch (((F) el).name) {
                    case F.ADD: {
                        Element res = toMatrixConvertWTD(((F) el).X[0]);
                        for (int i = 1; i < ((F) el).X.length; i++) {
                            res = res.add(toMatrixConvertWTD(((F) el).X[i]), RING);
                        }
                        return res;
                    }
                    case F.MULTIPLY: {
                        Element res = toMatrixConvertWTD(((F) el).X[0]);
                        for (int i = 1; i < ((F) el).X.length; i++) {
                            res = res.multiply(toMatrixConvertWTD(((F) el).X[i]), RING);
                        }
                        return res;
                    }
                    case F.SUBTRACT:
                        return toMatrixConvertWTD(((F) el).X[0]).subtract(toMatrixConvertWTD(((F) el).X[1]), RING);
                    case F.DIVIDE:
                        return new Fraction(toMatrixConvertWTD(((F) el).X[0]), toMatrixConvertWTD(((F) el).X[1]));


                    default:
                        return workToDefault(el, 1);
                }
            }
            case Ring.Rational:
            case Ring.Q:
                return new Fraction(toMatrixConvertWTD(((Fraction) el).num), toMatrixConvertWTD(((Fraction) el).denom));

            case Ring.Fname:
                return workToDefault(el, 1);


            default:
                return el;
        }
    }

    /**
     * Процедура разложения функции func в окрестности точки x в ряд Тейлора с
     * колличеством членов numb
     *
     * @param func
     * @param x
     * @param numb
     *
     * @return
     */
    private Element truncatedTeilor(Element func, Element x, int numb) {
        ArrayList<Element> arg = new ArrayList<Element>();
        arg.add(new FvalOf(RING).valOf(func, new Element[] {x}));
        Element Dfunc = func;
        for (int j = 1; j <= numb; j++) {
            Dfunc = Dfunc.D(RING);
            Element f = new FvalOf(RING).valOf(Dfunc, new Element[] {x});
            Element mnog = (RING.varPolynom[0].subtract(x, RING)).pow(j, RING);
            Element factorial = new F(F.FACTORIAL, RING.numberONE.valOf(j, RING));
            F div = new F(F.DIVIDE, new Element[] {f, factorial});
            Element tempres = getElWihOutID(new F(F.MULTIPLY, new Element[] {div, mnog}).expand(RING));
            if (!tempres.isZero(RING)) {
                arg.add(tempres);
            }
        }
        return new F(F.ADD, arg.toArray(new Element[arg.size()]));
    }

    /**
     * Процедура разложения функции func в окрестности точки x в ряд Тейлора
     *
     * @param func
     * @param x
     * @param numb
     * @return результат-объект типа F с именем SERIES
     */
    private Element runTeilor(Element func, Element x) {
        int type = x.numbElementType();
        if (!(type <= Ring.Polynom | type == Ring.Fname)) {
            try {
                throw new Exception(RING.exception.append("Wrong point in the Taylor series ").append(x).toString());
            } catch (Exception ex) {
                Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Fname index = new Fname("i");
        Element newFunc = new F(F.VALUE, new F(F.D, new Element[] {func, new F(F.intPOW, new Element[] {RING.varPolynom[0], index})}), x);
        F div = new F(F.DIVIDE, newFunc, new F(F.FACTORIAL, index));
        F mnog = new F(F.intPOW, new F(F.SUBTRACT, new Element[] {RING.varPolynom[0], x}), index);
        F res = new F(F.MULTIPLY, div, mnog);
        return new F(F.SERIES, res, index, RING.numberZERO, new Fname("\\infty"));
    }

    /**
     * Смотрим наличие в проекте пакета llp2/
     * @param className
     * @param mass
     * @return
     */
    private Element[] createMatrixsParAndPolynomPar(String className, Element[] mass) {
        Element[] w = new Element[mass.length];
        try {
            Class<?> y = Class.forName(className);
            try {
                Constructor<?> hh = y.getConstructor(mass[0].getClass());
                try {
                    for (int i = 0; i < w.length; i++) {
                        w[i] = (Element) hh.newInstance(mass[i]);
                    }
                    return w;
                } catch (InstantiationException ex) {
                    return null;
                } catch (IllegalAccessException ex) {
                    return null;
                } catch (IllegalArgumentException ex) {
                    return null;
                } catch (InvocationTargetException ex) {
                    return null;
                }
            } catch (NoSuchMethodException ex) {
                return null;
            } catch (SecurityException ex) {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            RING.exception.append("Нет пакета llp2/");
            System.err.println("Нет пакета llp2/");
            return null;
        }
    }

    /**
     * Смотрим наличие в проекте пакета llp2/
     * @param className
     * @param mass
     * @return
     */
    private Element createMatrixsPar(MatrixS el) {
        try {
            Class<?> y = Class.forName("llp2.MatrixSPar");
            try {
                Constructor<?> hh = y.getConstructor(MatrixS.class);
                try {
                    return (Element) hh.newInstance(el);
                } catch (InstantiationException ex) {
                    return null;
                } catch (IllegalAccessException ex) {
                    return null;
                } catch (IllegalArgumentException ex) {
                    return null;
                } catch (InvocationTargetException ex) {
                    return null;
                }
            } catch (NoSuchMethodException ex) {
                return null;
            } catch (SecurityException ex) {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            RING.exception.append("Нет пакета llp2/");
            System.err.println("Нет пакета llp2/");
            return null;
        }
    }

    /**
     * Решение обыкновенных уравнений степени больше чем 4 численным методом
     * @param p
     * @return
     */
    private VectorS returnRootsOfPolynom(Polynom p) {
        return new VectorS(p.rootsOfPol_inC(new int[1][0], RING), 0);
    }


    private Element[] generate_names_inSysSolve(int len) {
        Element[] res = new Element[len];
        //пока не рассматриваем ситуации когда у нас закончились все имена
        int indexRes = 0;
        for (int i = index_of_last_gr_word; i < gr_words.length; i++) {
            if (indexs_free_gr_words[i] == 0) {
                res[indexRes] = new Fname(gr_words[i]);
                indexRes++;
                if (indexRes == len) {
                    break;
                }
            }
        }
        return res;
    }

/**
 * Формируем коэффициенты искомых переменных
 * @param pol
 * @param indexs
 * @return
 */
    private Element[] returnRow(Element pol, int[] indexs) {
        if(pol instanceof Polynom){
        Polynom pol1=(Polynom)pol;
        Element[] res = new Element[indexs.length + 1];
        for (int j = 0; j < res.length; j++) {
            res[j] = RING.numberZERO;
        }
        int len = pol1.powers.length / pol1.coeffs.length;
        int coefPos = -1;
        int[] tempPow = new int[len];
        for (int i = 0; i < pol1.powers.length; i += len) {
            coefPos++;
            int l = 0;
            for (; l < indexs.length; l++) {
                if (indexs[l] < len) {
                    if (pol1.powers[indexs[l] + i] != 0) {
                        System.arraycopy(pol1.powers, i, tempPow, 0, len);
                        tempPow[indexs[l]] = 0;
                        res[l] = res[l].add(new Polynom(tempPow, new Element[] {pol1.coeffs[coefPos]}).normalNumbVar(RING), RING);
                        break;
                    }
                }
            }
            if (l >= indexs.length) {
                System.arraycopy(pol1.powers, i, tempPow, 0, len);
                res[indexs.length] = res[indexs.length].add(new Polynom(tempPow, new Element[] {pol1.coeffs[coefPos]}).normalNumbVar(RING), RING);
            }
        }
        return res;
        }else{// значит пришла дробь
        Element[] res = new Element[indexs.length + 1];
        for (int j = 0; j < res.length; j++) {
            res[j] = RING.numberZERO;
        }
        Element Denom=((Fraction)pol).denom;
        Polynom pol1=(Polynom)((Fraction)pol).num;
        int len = pol1.powers.length / pol1.coeffs.length;
        int coefPos = -1;
        int[] tempPow = new int[len];
        for (int i = 0; i < pol1.powers.length; i += len) {
            coefPos++;
            int l = 0;
            for (; l < indexs.length; l++) {
                if (indexs[l] < len) {
                    if (pol1.powers[indexs[l] + i] != 0) {
                        System.arraycopy(pol1.powers, i, tempPow, 0, len);
                        tempPow[indexs[l]] = 0;
                        res[l] = res[l].add(myOpDiv(new Polynom(tempPow, new Element[] {pol1.coeffs[coefPos]}).normalNumbVar(RING),Denom), RING);
                        break;
                    }
                }
            }
            if (l >= indexs.length) {
                System.arraycopy(pol1.powers, i, tempPow, 0, len);
                res[indexs.length] = res[indexs.length].add(myOpDiv(new Polynom(tempPow, new Element[] {pol1.coeffs[coefPos]}).normalNumbVar(RING),Denom), RING);
            }
        }
        return res;
        }
    }

    private VectorS solveAlgMatrixs(VectorS vec, int[] indexs, boolean flag) {
        Element[][] mat = new Element[indexs.length][indexs.length];
        Element[] stolb = new Element[indexs.length];
        Element[] temp;
        if (flag) {// случай SolveEQ
            for (int i = 0; i < vec.V.length; i += 2) {
                temp = returnRow(vec.V[i].subtract(vec.V[i + 1], RING), indexs);
                System.arraycopy(temp, 0, mat[i / 2], 0, mat[i / 2].length);
                stolb[i / 2] = temp[temp.length - 1].negate(RING);
            }
            MatrixS resMat = new MatrixS(mat, RING);
            resMat = resMat.inverseInFractions(RING);
            return new VectorS((VectorS)(resMat.multiplyByColumn(new VectorS(stolb), RING)).expand(RING),vec.fl);
        } else { // обычный Solve
            for (int i = 0; i < vec.V.length; i++) {
                temp = returnRow((Polynom) vec.V[i], indexs);
                System.arraycopy(temp, 0, mat[i], 0, mat[i].length);
                stolb[i] = temp[temp.length - 1].negate(RING);
            }
            MatrixS resMat = new MatrixS(mat, RING);
            resMat = resMat.inverseInFractions(RING);
            return new VectorS((VectorS)resMat.multiplyByColumn(new VectorS(stolb), RING).expand(RING),vec.fl);
        }
    }

    /**
     * Черт знает что !!!
     *
     * @param pol
     * @param indexVar
     *
     * @return
     */
    private VectorS returnRootsEq(Polynom pol, int indexVar) {
        int[] varPos = new int[pol.powers.length / pol.coeffs.length];
        Polynom resP = pol.denseVariables(varPos);
        if (resP.powers.length / resP.coeffs.length == 1) {
            return returnRootsOfPolynom(pol);
        }
        FactorPol factorRes = pol.FactorPol_SquareFree(RING);
        ArrayList<Element> res = new ArrayList<Element>();
        Element tempRes = null;
        for (int i = 0; i < factorRes.multin.length; i++) {
            if (variables(factorRes.multin[i], indexVar + 1)[indexVar] == 0) {
                continue; // если коэффициент числовой
            }
            tempRes = new SolveEq(this.RING).solvePolynomEq(factorRes.multin[i], indexVar, RING);
            if (tempRes == null)   return null; // мы сдаемся ((
//            for (int j = 0; j < factorRes.powers.length; j++) { // дублируем кратные
                if (tempRes instanceof VectorS) {
                    res.addAll(Arrays.asList(((VectorS) tempRes).V));
                } else {
                    res.add(tempRes);
                }
//            }
        }
        return  new VectorS(res.toArray(new Element[res.size()]), 1);
    }


    public Element unconvert2FforVectorSinLists(Element f) {
        switch (f.numbElementType()) {
            case Ring.F:
                Element[] newX = new Element[((F) f).X.length];
                for (int i = 0; i < newX.length; i++) {
                    newX[i] = unconvert2FforVectorSinLists(((F) f).X[i]);
                }
                return new F(((F) f).name, newX);
            case Ring.VectorS:
            case Ring.MatrixD:
                return ElementToF(f);
            default:
                return f;
        }
    }
   /**
    * Конвертируем два типа обьектов (Factor & FactorPol) в F во всем дереве функции
    * @param el
    * @return
    */
    private Element convertFactorInF(Element el){
     switch(el.numbElementType()){
         case Ring.F:{
          Element [] newArg= new Element[((F)el).X.length];
          for(int i=0; i < newArg.length; i++){
          newArg[i]=convertFactorInF(((F)el).X[i]);
          }
          return new F(((F)el).name,newArg);
         }
         case Ring.Factor:
         case Ring.FactorPol:
            return convert_FactorPol_to_El(el);
      default: return el;
      }
    }
   /**
    * Конвертируем в полином только те элементы дерева-функции , которые указаны в списке #var#
    * @param func - входная функция
    * @param var - список переменных для конвертации (обычно объекты ти па Fname)
    * @return
    */
    private Element covertListElements(Element func,Element[] var){
     for(int j=0;j<var.length;j++){
     if(func.compareTo(var[j], RING)==0){
            return workToDefault(var[j], 1);
       }
     }
     switch(func.numbElementType()){
         case Ring.F:
         Element[] newX= new Element[((F)func).X.length];
         for(int i=0;i<newX.length;i++) {
             newX[i]=covertListElements(((F)func).X[i], var);
         }
         return new F(((F)func).name,newX);
         case Ring.MatrixD:
         case Ring.VectorS:
         case Ring.MatrixS:
         case Ring.Q:
         case Ring.Rational:
         return new F(F.DIVIDE,new Element[]{covertListElements(((Fraction)func).num,var),covertListElements(((Fraction)func).denom,var)});
      default: return func;
     }
    }
//==============================================================================
//=================             GATHER           ===============================
//==============================================================================

//  private Element workWithDenom_GF(Element el){
//   switch
//
//  }
//
//
//
//  private Element add_GF(Element a, Element b){
//    if(a instanceof Fraction)
//    return null;
//   }



    public Element gatherFraction(Element el){
      switch(el.numbElementType()){
          case Ring.F: // работаем с функцией в зависимости от ее имени
           switch(((F)el).name){
               case F.ADD:
               case F.SUBTRACT:
               case F.MULTIPLY:
               case F.DIVIDE:
               case F.intPOW:
               default:
               Element[] newX=new Element[((F)el).X.length];
               for(int i=0;i<newX.length;i++){
                newX[i]=gatherFraction(((F)el).X[i]);
               }
               return workToDefault(CleanElement(new F(((F)el).name,newX)), 1);
           }
          case Ring.Fname:
          return workToDefault(el,1);
          case Ring.Q:// Fraction у которой в числителе и знаменателе чила
          return el;
          case Ring.Rational: // Fraction с интересными элементами
          return new Fraction(gatherFraction(((Fraction)el).num),gatherFraction(((Fraction)el).denom));
          default:  // число или полином - без изменений
          return el;
      }
    }

//==============================================================================


    /**
     * 
     * Вычисление в зависимости от корректности задания узлов: \GCD, \LCM, \D,
     * \VALUE, \FACTOR, \EXPAND & etc.. Создание при необходимости узла
     * MULTIPLY_NC.
     * 
     * @param func   may be F, may be sup_programm
     * @param page - only main page
     * @param express -- может быть от процедуры, а может быть от главной программы!
     * @return 
     */
    private Element runOperators(F func, Page page, List<Element> express) {
        if (func.name == F.IC) { return func; }
        if (func.name >= F.MAX_F_NUMB) {Element tempProc=null;
           try { 
               tempProc = page.runProcedure(func, express); //2015 this 
                return (tempProc == null) ? null : runOperatorValue(new Element[] {tempProc});
           } catch (Exception ex) { RING.exception.append("Error in procedure:"+func+"  "+tempProc+"  : "+ex.toString());
               System.out.println("Error in procedure:"+func+"  "+tempProc+"  : "+ex.toString()); 
           throw new MathparException("Error in procedure", ex); }
         } 
        Element[] CalcArg = new Element[func.X.length]; 
        Element[] forResults=null; // this for the case of several Elements in result
            for (int i = 0; i < func.X.length; i++) {
                Element res0 =func.X[i];
                if(func.X[i] instanceof F) res0= runOperators((F) func.X[i], page, express);
                CalcArg[i] = (res0==null)? func.X[i]: res0;
            }
            switch (func.name) {
                 case F.SOLVELEX:
                    SolveLEX solv = new SolveLEX();
                    F argSolveLex=(CalcArg[0] instanceof F) ? (F)CalcArg[0] : new F(F.ID,CalcArg[0]);
                    return solv.solve(argSolveLex, CalcArg[1], CalcArg[2], RING);
                 case F.MAX:
                     if (CalcArg[0] instanceof VectorS) {
                         Element tempM = ((VectorS) CalcArg[0]).V[0];
                         for (int i = 1; i < ((VectorS) CalcArg[0]).V.length; i++) {
                             if (tempM.compareTo(((VectorS) CalcArg[0]).V[i], RING) < 0) {
                                 tempM = ((VectorS) CalcArg[0]).V[i];
                             }
                         }
                         return tempM;
                     }
                     if (CalcArg[0] instanceof VectorS) {
                         Element tempM = ((VectorS) CalcArg[0]).V[0];
                         for (int i = 1; i < ((VectorS) CalcArg[0]).V.length; i++) {
                             if (tempM.compareTo(((VectorS) CalcArg[0]).V[i], RING) < 0) {
                                 tempM = ((VectorS) CalcArg[0]).V[i];
                             }
                         }
                         return tempM;
                     }
                     if (CalcArg[0].isItNumber() && CalcArg[1].isItNumber()) {
                         return CalcArg[0].compareTo(CalcArg[1], RING) >= 0 ? CalcArg[0] : CalcArg[1];
                     }
                     return Element.NAN;
                 case F.MIN:
                     if (CalcArg[0] instanceof VectorS) {
                         Element tempM = ((VectorS) CalcArg[0]).V[0];
                         for (int i = 1; i < ((VectorS) CalcArg[0]).V.length; i++) {
                             if (tempM.compareTo(((VectorS) CalcArg[0]).V[i], RING) > 0) {
                                 tempM = ((VectorS) CalcArg[0]).V[i];
                             }
                         }
                         return tempM;
                     }
                     if (CalcArg[0] instanceof VectorS) {
                         Element tempM = ((VectorS) CalcArg[0]).V[0];
                         for (int i = 1; i < ((VectorS) CalcArg[0]).V.length; i++) {
                             if (tempM.compareTo(((VectorS) CalcArg[0]).V[i], RING) > 0) {
                                 tempM = ((VectorS) CalcArg[0]).V[i];
                             }
                         }
                         return tempM;
                     }
                     if (CalcArg[0].isItNumber() && CalcArg[1].isItNumber()) {
                         return CalcArg[0].compareTo(CalcArg[1], RING) >= 0 ? CalcArg[1] : CalcArg[0];
                     }
                     return Element.NAN;
                 case F.TABULATION:
                     Element[] fV;
                  //   if(CalcArg[0] instanceof VectorS) fV= ((VectorS)CalcArg[0]).V; else 
                         if(CalcArg[0] instanceof VectorS) 
                            {fV= ((VectorS)CalcArg[0]).V;
                             for (int i = 0; i < fV.length; i++) fV[i]=ElementToF(fV[i]);}
                          else fV= new Element[]{ElementToF(CalcArg[0])};
                     
                     if (CalcArg.length==4)
                         return new Table( fV ,CalcArg[1],CalcArg[2],CalcArg[3], RING); 
                     else return Element.NAN;
                 case F.DIV:
                 case F.REM:
                 case F.DIVREM:
                    Element[] resDivAndRem = CalcArg[0].divideAndRemainder(CalcArg[1], RING);
                    return func.name == F.DIVREM ? new VectorS(resDivAndRem, 1) : func.name == F.DIV ? resDivAndRem[0] : resDivAndRem[1];
                 case F.RESULTANT:
                    if(CalcArg[0] instanceof Polynom){
                     return CalcArg.length==3 ? new VectorS(((Polynom)CalcArg[0]).resultant2Pol(((Polynom)CalcArg[1]), ((Polynom)CalcArg[2]).powers.length-1, RING)):
                           ((Polynom)CalcArg[0]).resultant(((Polynom)CalcArg[1]), RING);
                            }
                 case F.SIZE:
                     switch(CalcArg[0].numbElementType()){
                         case Ring.MatrixS:
                              return new VectorS(new Element[]{RING.numberONE.valOf(((MatrixS)CalcArg[0]).size , RING), RING.numberONE.valOf(((MatrixS)CalcArg[0]).colNumb, RING)});
                         case Ring.MatrixD:
                           return new VectorS(new Element[]{RING.numberONE.valOf(((MatrixD)CalcArg[0]).M.length, RING),RING.numberONE.valOf(((MatrixD)CalcArg[0]).M[0].length, RING)});
                         case Ring.VectorS:
                          return RING.numberONE.valOf(((VectorS)CalcArg[0]).V.length, RING);
                         default:
                         return RING.numberONE;
                     }
                 case F.COLNUMB:
                     switch (CalcArg[0].numbElementType()) {
                        case Ring.MatrixS:
                            return RING.numberONE.valOf(((MatrixS) CalcArg[0]).colNumb, RING);
                        case Ring.MatrixD:
                            return RING.numberONE.valOf(((MatrixD) CalcArg[0]).M[0].length, RING);
                        case Ring.VectorS:
                      return (((VectorS) CalcArg[0]).fl<0)?  RING.numberONE: RING.numberONE.valOf(((VectorS) CalcArg[0]).V.length, RING);
                        default:
                            return RING.numberONE;
                    }
                 case F.ROWNUMB:
                      switch (CalcArg[0].numbElementType()) {
                        case Ring.MatrixS:
                            return RING.numberONE.valOf(((MatrixS) CalcArg[0]).size, RING);
                        case Ring.MatrixD:
                            return RING.numberONE.valOf(((MatrixD) CalcArg[0]).M.length, RING);
                        case Ring.VectorS:
                            return (((VectorS) CalcArg[0]).fl<0)? RING.numberONE.valOf(((VectorS) CalcArg[0]).V.length, RING): RING.numberONE;
                        default:
                            return RING.numberONE;
                    }
                 case F.LENGTH:
                     Element[] argVec = (CalcArg[0] instanceof VectorS) ? ((VectorS) CalcArg[0]).V : ((VectorS) CalcArg[0]).V;
                     Element lvec = argVec[0].pow(2, RING);
                     for (int i = 1; i < argVec.length; i++) {
                         lvec = lvec.add(argVec[i].pow(2, RING), RING);
                     }
                     return lvec.sqrt(RING);
                 case F.DEGREE:
                     
                     Element forDegr=page.expandFnameOrId(CalcArg[0]);
                     if (forDegr.isItNumber()) return NumberZ.ZERO;
                     if(forDegr instanceof Polynom){ Polynom pdeg= (Polynom)forDegr;int var =pdeg.powers.length/pdeg.coeffs.length;
                     return (CalcArg.length==2) ? new NumberZ(((Polynom)forDegr).degree(((Polynom)CalcArg[1]).powers.length-1)) :
                             new NumberZ(((Polynom)forDegr).degree(var-1));
                     }   
                     return NumberZ.MINUS_ONE;
                 case F.DEGREES:
                     if(CalcArg[0] instanceof Polynom){
                     return new VectorS( ((Polynom)CalcArg[0]).degrees());         
                     }
                     return new VectorS(NumberZ.ZERO);
                case F.NINT:
//                    FvalOf cal = new FvalOf(RING);
//                  switch(CalcArg.length) {
//                        case 3:
//                            return NInt.integrate(cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), CalcArg[0], RING);
//                        case 4:
//                            if(CalcArg[3] instanceof VectorS) {
//                                return (cal.valOf(CalcArg[1],new Element[0]).isInfinite()||cal.valOf(CalcArg[2],new Element[0]).isInfinite())?
//                                        NInt.integrate(CalcArg[0], cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, RING):
//                                        NInt.integrate(cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, CalcArg[0], RING);
//                            } else {
//                                return NInt.integrate(cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), CalcArg[0], CalcArg[3].intValue(), RING);
//                            }
//                        case 5:
//                            if(CalcArg[3] instanceof VectorS) {
//                                if(CalcArg[4] instanceof VectorS) {
//                                    return NInt.integrate(CalcArg[0], cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, ((VectorS)CalcArg[4]).V, RING);
//                                } else {
//                                    return (cal.valOf(CalcArg[1],new Element[0]).isInfinite()||cal.valOf(CalcArg[2],new Element[0]).isInfinite())?
//                                            NInt.integrate(CalcArg[0], cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, CalcArg[4].intValue(),  RING):
//                                            NInt.integrate(cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, CalcArg[0], CalcArg[4].intValue(), RING);
//                                }
//                            } else {
//                            return  NInt.integrate(cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), CalcArg[0], CalcArg[3].intValue(), CalcArg[4].intValue(), RING);
//                            }
//                        case 6:
//                            if( (CalcArg[3] instanceof VectorS)&&(CalcArg[4] instanceof VectorS) ) {
//                                return NInt.integrate(CalcArg[0], cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, ((VectorS)CalcArg[4]).V, CalcArg[5].intValue(), RING );
//                            } else {
//                                return (cal.valOf(CalcArg[1],new Element[0]).isInfinite()||cal.valOf(CalcArg[2],new Element[0]).isInfinite())?
//                                        NInt.integrate(CalcArg[0], cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, CalcArg[4].intValue(), CalcArg[5].intValue(), RING):
//                                        NInt.integrate(cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, CalcArg[0], CalcArg[4].intValue(), CalcArg[5].intValue(), RING);
//                            }
//                        case 7:
//                            return NInt.integrate(CalcArg[0], cal.valOf(CalcArg[1],new Element[0]), cal.valOf(CalcArg[2],new Element[0]), ((VectorS)CalcArg[3]).V, ((VectorS)CalcArg[4]).V, CalcArg[5].intValue(), CalcArg[6].intValue(), RING);
//                    }
               case F.ISZERO:
                  return (CalcArg[0].isZero(RING))? Element.TRUE:Element.FALSE;
               case F.ISNEGATIVE:
                  return (CalcArg[0].isNegative())? Element.TRUE:Element.FALSE;
               case F.ISONE:
                  return (CalcArg[0].isOne(RING))? Element.TRUE:Element.FALSE;
                case F.ISEVEN:
                  return (CalcArg[0].isEven())? Element.TRUE:Element.FALSE; 
                case F.ISNAN:
                  return (CalcArg[0].isNaN())? Element.TRUE:Element.FALSE;  
                case F.ISINFINITE:
                  return (CalcArg[0].isInfinite())? Element.TRUE:Element.FALSE;  
                case F.GAMMA:
                  return CalcArg[0].gamma(RING);
                case F.BETA:
                   return CalcArg[0].beta(CalcArg[1], RING);
//                case F.BESSELJ:
//                     if (CalcArg.length<2) return Element.NAN;
//                   return com.mathpar.funcSpec.Bessel.valueJ(CalcArg[0],CalcArg[1], RING);          
//                case F.BESSELY:
//                      if (CalcArg.length<2) return Element.NAN;
//                   return com.mathpar.funcSpec.Bessel.valueY(CalcArg[0],CalcArg[1], RING); 
//                case F.LEGENDREP:
//                    switch(CalcArg.length) {
//                        case 2:
//                            return new PolLezh().nodes2( CalcArg[0] , CalcArg[1], RING);
//                        case 3:
//                            return new PolLezh().prisLeg(CalcArg[0], CalcArg[1], CalcArg[2], RING);
//                    }
//                case F.SPHERICALHARMONIC:
//                    return new PolLezh().Y(CalcArg[0], CalcArg[1], CalcArg[2], CalcArg[3], RING);
//                    
//                case F.SPHERICALHARMONICR:
//                    return new PolLezh().U(CalcArg[0], CalcArg[1], CalcArg[2], CalcArg[3], CalcArg[4], RING);
//                case F.SPHERICALHARMONICCART:
//                     return new PolLezh().YDec(CalcArg[0], CalcArg[1], CalcArg[2], CalcArg[3], CalcArg[4], RING);
//                
//                case F.SPHERICALHARMONICRCART:
//                    return new PolLezh().UDec(CalcArg[0], CalcArg[1], CalcArg[2], CalcArg[3], CalcArg[4], RING);
//             
 // #############################################################################                   
                case F.SIMPLEXMAX: 
                    switch(CalcArg.length){                         
                       case 3: return new VectorS(Simplex.simplex_max(((MatrixD)CalcArg[0]), ((VectorS)CalcArg[1]), ((VectorS)CalcArg[2]), RING),-1);
                       case 4: return new VectorS(Simplex.simplex_max(((MatrixD)CalcArg[0]), ((VectorS)CalcArg[1]).toIntArray(), ((VectorS)CalcArg[2]), ((VectorS)CalcArg[3]), RING),-1);
                       case 5: return new VectorS(Simplex.simplex_max(((MatrixD)CalcArg[0]),((MatrixD)CalcArg[1]), ((VectorS)CalcArg[2]), ((VectorS)CalcArg[3]),((VectorS)CalcArg[4]), RING),-1);
                       default:
                      return new VectorS(Simplex.simplex_max(((MatrixD)CalcArg[0]),((MatrixD)CalcArg[1]),((MatrixD)CalcArg[2]), ((VectorS)CalcArg[3]),((VectorS)CalcArg[4]),((VectorS)CalcArg[5]), ((VectorS)CalcArg[6]), RING),-1);
                   }
                case F.SIMPLEXMIN:
                    switch(CalcArg.length){
                       case 3: return new VectorS(Simplex.simplex_min(((MatrixD)CalcArg[0]), ((VectorS)CalcArg[1]), ((VectorS)CalcArg[2]), RING),-1);
                       case 4: return new VectorS(Simplex.simplex_min(((MatrixD)CalcArg[0]), ((VectorS)CalcArg[1]).toIntArray(), ((VectorS)CalcArg[2]), ((VectorS)CalcArg[3]), RING),-1);
                       case 5: return new VectorS(Simplex.simplex_min(((MatrixD)CalcArg[0]),((MatrixD)CalcArg[1]), ((VectorS)CalcArg[2]), ((VectorS)CalcArg[3]),((VectorS)CalcArg[4]), RING),-1);
                       default:
                      return new VectorS(Simplex.simplex_min(((MatrixD)CalcArg[0]),((MatrixD)CalcArg[1]),((MatrixD)CalcArg[2]), ((VectorS)CalcArg[3]),((VectorS)CalcArg[4]),((VectorS)CalcArg[5]), ((VectorS)CalcArg[6]), RING),-1);
                   }
                case F.SOLVELAETROPIC:
                   return equation.solveLAETropic(((MatrixD)CalcArg[0]), ((VectorS)CalcArg[1]), RING);
                case F.SOLVELAITROPIC:
                   return equation.solveLAITropic(((MatrixD)CalcArg[0]), ((VectorS)CalcArg[1]), RING);
                case F.BELLMANEQUATION:
                    Element[] resBel=(CalcArg.length==2) ? equation.BellmanEquation(((MatrixD)CalcArg[0]) , ((MatrixD)CalcArg[1]), RING):
                            equation.BellmanEquation(((MatrixD)CalcArg[0]), RING);
                    return new VectorS(resBel,-1);
                case F.SEARCHLEASTDISTANCES:
                   return TropicalProblems.searchLeastDistances(((MatrixD)CalcArg[0]), RING);
                case F.FINDTHESHORTESTPATH: MatrixD df=(MatrixD)((CalcArg[0] instanceof Fname)?((Fname)CalcArg[0]).X[0]: CalcArg[0]);
                   return TropicalProblems.findTheShortestPath(df,CalcArg[1].intValue(),CalcArg[2].intValue(), RING);
                case F.CUP:
                   return ( CalcArg[0]).union( CalcArg[1], RING);
                case F.CAP:
                   return ( CalcArg[0]).intersection( CalcArg[1], RING);
                case F.SYMMETRIC_DIFFERENCE:
                   return ( CalcArg[0]).symmetricDifferece( CalcArg[1], RING);
                case F.SET_MINUS:
                   return ( CalcArg[0]).setTheoreticDifference( CalcArg[1], RING);
                case F.COMPLEMENT:
                   return ( CalcArg[0]).complement(RING);
                case F.APPROX:{
                  if(CalcArg.length==3){
                  Element[] Xarr=(CalcArg[0] instanceof VectorS) ? ((VectorS)CalcArg[0]).V : ((VectorS)CalcArg[0]).V;
                  Element[] Yarr=(CalcArg[1] instanceof VectorS) ? ((VectorS)CalcArg[1]).V : ((VectorS)CalcArg[1]).V;
                  return  PolynomOneVar.getApproxPolynom(Xarr, Yarr, CalcArg[2].intValue(), RING); }
                  else {
                     Element[][] Xarr=(CalcArg[0] instanceof Table) ? ((Table)CalcArg[0]).M.M :
                             ((CalcArg[0] instanceof F)&& ((((F)CalcArg[0]).X[0]) instanceof Table))? ((Table)(((F)CalcArg[0]).X[0])).M.M : null; // ((VectorS)CalcArg[0]).V;
                     return  PolynomOneVar.getApproxPolynom(Xarr[0], Xarr[1], CalcArg[1].intValue(), RING); }
                      }
                case F.NUM:
                    if (CalcArg[0] instanceof F) {
                        F f = (F) CalcArg[0];
                        if (f.name == F.DIVIDE) {
                            return f.X[0];
                        } else {
                            return f;
                        }
                    }
                    return (CalcArg[0]).num(RING);
                case F.DENOM:
                    if (CalcArg[0] instanceof F) {
                        F f = (F) CalcArg[0];
                        if (f.name == F.DIVIDE) { return f.X[1];} 
                        else { return RING.numberONE; }
                    }
                    return (CalcArg[0]).denom(RING);
                case F.CANCEL:
                    if (CalcArg[0] instanceof F) {
                        F f = (F) CalcArg[0];
                        if (f.name == F.DIVIDE) { return (new Fraction(f.X[0],f.X[1])).cancel(RING) ;} }
                    if (CalcArg[0] instanceof Fraction) { return ((Fraction)CalcArg[0]).cancel(RING) ;}
                    return CalcArg[0];
                case F.PROPERFORM:
                    Fraction fract1;
                   if (CalcArg[0] instanceof F) { F f = (F) CalcArg[0];
                        if (f.name != F.DIVIDE) { return CalcArg[0];} 
                        else { fract1 =new Fraction(f.X[0],f.X[1]); }}
                   else if (CalcArg[0] instanceof Fraction) {fract1=(Fraction)CalcArg[0];}
                   else return CalcArg[0];
                   return new VectorS(fract1.quotientAndProperFraction(RING));
                case F.QUOTIENT:
                case F.REMAINDER:
                case F.QUOTIENTANDREMAINDER:
                    Element[] ee ;
                    int argNumb=CalcArg.length;
                    Element ARG=CalcArg[0]; Element var=NumberZ.ZERO;
                    if (ARG instanceof F) { F f = (F) ARG;
                        if (f.name == F.DIVIDE) { ARG= new Fraction(El2Pol(f.X[0]), El2Pol(f.X[1]));} 
                        else { return Element.NAN; }}
                    if (ARG instanceof Fraction){
                      if(argNumb==1) ee=((Fraction)ARG).quotientAndRemainder(RING);
                      else ee=(ARG.quotientAndRemainder(CalcArg[1], RING));
                    }else 
                       ee= (argNumb==2)? ARG.quotientAndRemainder(CalcArg[1],RING)
                          :(argNumb==3)? ARG.quotientAndRemainder(CalcArg[1],CalcArg[2],RING)    
                          : new Element[]{Element.NAN, Element.NAN};
                    if(func.name==F.QUOTIENT) return ee[0]; 
                    else if (func.name==F.REMAINDER) return ee[1]; else return new VectorS(ee);                
                case F.DETPAR:
                    return createMatrixsPar(new MatrixS(((MatrixD) CalcArg[0]),newRing)).detPar(newRing);
                case F.ECHELONFORMPAR:
                    return createMatrixsPar(new MatrixS(((MatrixD) CalcArg[0]),newRing)).echelonFormPar(newRing);
                case F.KERNELPAR:
                    return createMatrixsPar(new MatrixS(((MatrixD) CalcArg[0]),newRing)).kernelPar(newRing);
                case F.GETSTATUS:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }

                    int userId = (int)page.getUserId();
                    int taskId = CalcArg[0].intValue();
                    QueryResult qRes = page.getClusterQueryCreator().getStatusForTask(userId, taskId);
                    if (qRes.getState() == AlgorithmsConfig.RES_SUCCESS){
                        int v=(Integer)qRes.getData()[0];
                        return new Fname(AlgorithmsConfig.stateNames[v]);
                    }
                    return new Fname(AlgorithmsConfig.respNames[qRes.getState()]);
                }
                case F.UPLOADANDCOMPILE:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    String fileName = page.getUserUploadDir() + File.separator + CalcArg[0].toString(RING);
                    byte[] tmp = null;
                    try {
                        FileInputStream fis = new FileInputStream(fileName);
                        int fileSize = fis.available();
                        tmp = new byte[fileSize];
                        fis.read(tmp);
                        fis.close();
                    } catch (Exception e) {
                        return new Fname("file  reading  error");
                    }
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.uploadFileToClusterAndCompile((int) page.getUserId(), CalcArg[0].toString(), tmp);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.GET_CALC_RESULT:
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    int userId = (int)page.getUserId();
                    int taskId = CalcArg[0].intValue();
                    QueryResult qRes = page.getClusterQueryCreator().recvResultForTaskFromWeb(userId, taskId);
                    if (qRes.getState() == AlgorithmsConfig.RES_SUCCESS) {                   
                        return (Element)qRes.getData()[0];
                    }
                    return new Fname(AlgorithmsConfig.respNames[qRes.getState()]);
                case F.MATMULTPAR1X8:
                {
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,AlgorithmsConfig.AN_MULT_MATRIX_1x8,RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }
                    MatrixS AA,BB;
                    AA=(CalcArg[0] instanceof MatrixS)? ((MatrixS)CalcArg[0]):
                       new MatrixS(((MatrixD) CalcArg[0]), RING);
                    BB=(CalcArg[1] instanceof MatrixS)? ((MatrixS)CalcArg[1]):
                       new MatrixS(((MatrixD) CalcArg[1]), RING);                    
                    Object[] data = {AA,BB,newRing};                          
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.ADJOINTDETPAR:
                {
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,AlgorithmsConfig.AN_ADJOINT_DET,RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }                    
                    Object[] data = {
                        new MatrixS(((MatrixD) CalcArg[0]), RING),                        
                        newRing
                    };                          
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                 case F.BELLMAN_EQUATION_PAR:
                {
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    int algoNumb=AlgorithmsConfig.AN_BELLMAN_EQUATION1;
                    if (CalcArg.length==2){
                        algoNumb=AlgorithmsConfig.AN_BELLMAN_EQUATION2;
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,algoNumb, RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }
                    Object[] data = null;                    
                    if (CalcArg.length==1){
                        data=new Object[]{ 
                            new MatrixS(((MatrixD) CalcArg[0]), RING),
                            newRing};
                    }
                    else{
                        data=new Object[]{ 
                            new MatrixS(((MatrixD) CalcArg[0]), RING),
                            (VectorS)(CalcArg[1]),
                            newRing};
                    }    
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.BELLMAN_INEQUALITY_PAR:
                {
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    int algoNumb=AlgorithmsConfig.AN_BELLMAN_INEQUALITY1;
                    if (CalcArg.length==2){
                        algoNumb=AlgorithmsConfig.AN_BELLMAN_INEQUALITY2;
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,algoNumb, RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }
                    Object[] data = null;                    
                    if (CalcArg.length==1){
                        data=new Object[]{ 
                            new MatrixS(((MatrixD) CalcArg[0]), RING),
                            newRing};
                    }
                    else{
                        data=new Object[]{ 
                            new MatrixS(((MatrixD) CalcArg[0]), RING),
                            (VectorS)(CalcArg[1]),
                            newRing};
                    }    
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.ETD_TEST_PAR:
                {
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    int type = ETDpTO.RESULT_LDU;
                    int matrixSize = 64;
                    int zeroP = 50;
                    if(CalcArg.length==3){
                        matrixSize = CalcArg[0].intValue();
                        zeroP = CalcArg[1].intValue();
                        type = CalcArg[2].intValue();                        
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,AlgorithmsConfig.AN_ETD, RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }
                    Object[] data = new Object[]{matrixSize,zeroP,type};
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.POLMULTPAR:
                {
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,AlgorithmsConfig.AN_MULT_POLYNOM,RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }                    
                    Object[] data = {
                        CalcArg[0],CalcArg[1], newRing                       
                    };                                               
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.POLFACTORPAR:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,AlgorithmsConfig.AN_FACTOR_POL,RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }
                    Element arg = CalcArg[0];
                    if (arg instanceof F) {
                        arg = simplify_init( arg );
                    }
                    Object[] pol = {arg};                    
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), pol, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.CHARPOLPAR:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,AlgorithmsConfig.AN_CHAR_POL,RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }
                    Object[] data = {((MatrixD)CalcArg[0])};                    
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }                
                case F.GETERR:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    QueryCreator qc = page.getClusterQueryCreator();
                    int taskID=CalcArg[0].intValue();
                    QueryResult res = qc.getFileContent((int)page.getUserId(),taskID, "err");
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){                        
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        String errContent=AlgorithmsConfig.RAW_TEXT+(String)res.getData()[0]+AlgorithmsConfig.RAW_TEXT;
                        return new Fname(errContent);
                    }
                }
                case F.GETOUT:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    QueryCreator qc = page.getClusterQueryCreator();
                    int taskID=CalcArg[0].intValue();
                    QueryResult res = qc.getFileContent((int)page.getUserId(),taskID, "out");
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        String outContent=AlgorithmsConfig.RAW_TEXT+(String)res.getData()[0]+AlgorithmsConfig.RAW_TEXT;
                        return new Fname(outContent);
                    }
                }
                case F.RUNUPLOADEDCLASS:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }                      
                    TaskConfig conf= new TaskConfig(RING.TOTALNODES,RING.PROCPERNODE,RING.CLUSTERTIME,AlgorithmsConfig.AN_RUN_UPLOADED_CLASS,RING.MAXCLUSTERMEMORY);
                    int confCheckRes=conf.check();
                    if (confCheckRes!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[confCheckRes]);
                    }                    
                    QueryCreator qc = page.getClusterQueryCreator();
                    Object[]data= CalcArg;
                    QueryResult res = qc.addTask((int) page.getUserId(), data, conf);
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname(String.valueOf("Task  ID  is  " + res.getData()[0]));
                    }
                }
                case F.SHOWTASKLIST:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                   
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res=qc.getStatesList((int)page.getUserId());
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname((String)res.getData()[0]);
                    }
                }

                case F.SHOWFILELIST:{
                    if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                    }
                    QueryCreator qc = page.getClusterQueryCreator();
                    QueryResult res=qc.getFileList((int)page.getUserId());
                    if (res.getState()!=AlgorithmsConfig.RES_SUCCESS){
                        return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                    }
                    else {
                        return new Fname((String)res.getData()[0]);
                    }
                }
                case F.UPLOADTOCLUSTER:{
                     if (!page.isUserLoggedIn()) {
                        return new Fname("You  must  log  in  to  execute  parallel  functions!");
                     }
                     String fileName=page.getUserUploadDir()+File.separator+CalcArg[0].toString();
                     byte []tmp=null;
                     try {
                        FileInputStream fis = new FileInputStream(fileName);
                        int fileSize=fis.available();
                        tmp=new byte[fileSize];
                        fis.read(tmp);
                        fis.close();
                     } catch (Exception e) {
                        return new Fname("file  reading  error");
                     }
                     QueryCreator qc=page.getClusterQueryCreator();
                     QueryResult res=qc.uploadFileToCluster((int)page.getUserId(), CalcArg[0].toString(), tmp);
                     if (res.getState()==AlgorithmsConfig.RES_SUCCESS)
                         return new Fname("ok");
                     return new Fname(AlgorithmsConfig.respNames[res.getState()]);
                }
                case F.GBASISPAR:
                    Element[] mass = createMatrixsParAndPolynomPar("llp2.PolynomPar", (((VectorS) CalcArg[0])).V);
                    return mass[0].gbasisPar(mass, RING);
                case F.TONEWRING:
                    return CalcArg[0].toNewRing(RING.algebra[0], RING);
                case F.BRUHATDECOMPOSITION:
                    Element[] bldu = new MatrixS(((MatrixD) CalcArg[0]),newRing).BruhatDecomposition(newRing);
                    Element[] resbl = new Element[bldu.length];
                    for (int l = 0; l < resbl.length; l++) {
                        resbl[l] =  new MatrixD(((MatrixS) bldu[l]), true, RING,  ((MatrixD) CalcArg[0]).fl);
                    }
                    return new VectorS(resbl , ((MatrixD) CalcArg[0]).fl);
//                case F.PRS_STURM_OLD: PolynomOneVar pvv=new PolynomOneVar((Polynom)CalcArg[0]);
//                    Polynom[] pp= pvv.prs_Zx_Sturm((Polynom)CalcArg[1], RING);
//                    return new VectorS(pp);
                case F.PRS: Polynom pvv=(Polynom)CalcArg[0];
                //    Polynom[] pp= pvv.prs((Polynom)CalcArg[1], RING);
                    return new VectorS(pvv.prs((Polynom)CalcArg[1], RING));
                case F.PRS_SUBRES: PolynomOneVar pvv1=new PolynomOneVar((Polynom)CalcArg[0]);
                    Polynom[] pp1= pvv1.prs_Zx((Polynom)CalcArg[1], RING);
                    return new VectorS(pp1);
                case F.PRS_GENERAL: PolynomOneVar pvv2=new PolynomOneVar((Polynom)CalcArg[0]);
                    Polynom[] pp2= pvv2.prs_Zx_General((Polynom)CalcArg[1], 0 ,RING);
                    return new VectorS(pp2);
                case F.GCDNUMPOLCOEFFS:  
                    return ((Polynom)CalcArg[0]).GCDNumPolCoeffs(RING);               
                case F.GCDHPOLCOEFFS:  
                    return  (CalcArg.length==1)? ((Polynom)CalcArg[0]).GCDHPolCoeffs(RING):
                                                 ((Polynom)CalcArg[0]).GCDHPolCoeffs(CalcArg[1].intValue(),RING);               
                case F.LDU:
                    forResults = new MatrixS(((MatrixD) CalcArg[0]),newRing).LDU(newRing); 
                    return makeVectorSfromMatrixS(forResults, ((MatrixD) CalcArg[0]).fl );
                case F.LDU_M:
                    forResults = ETD.ETDmodLDU(new MatrixS(((MatrixD) CalcArg[0]),newRing));  
                    return makeVectorSfromMatrixS(forResults, ((MatrixD) CalcArg[0]).fl );
                case F.WDK:
                    forResults = ETD.ETDmodWDK(new MatrixS(((MatrixD) CalcArg[0]),newRing));
                    return makeVectorSfromMatrixS(forResults, ((MatrixD) CalcArg[0]).fl );
                case F.PLDUQWDK:
                    forResults = ETD.ETDmodPLDUQWDK(new MatrixS(((MatrixD) CalcArg[0]),newRing));
                    return makeVectorSfromMatrixS(forResults, ((MatrixD) CalcArg[0]).fl );
                case F.LDUWDK:
                    forResults = ETD.ETDmodLDUWDK(new MatrixS(((MatrixD) CalcArg[0]),newRing));
                    return makeVectorSfromMatrixS(forResults, ((MatrixD) CalcArg[0]).fl );
 
                case F.SUBMATRIX:
                    int r1=CalcArg[1].intValue()-1;
                    int rN=CalcArg[2].intValue();
                    int c1=CalcArg[3].intValue()-1;
                    int cN=CalcArg[4].intValue();
                    return new MatrixD( (((MatrixD) CalcArg[0])).submatrix(r1, rN, c1, cN, RING),
                            ((MatrixD) CalcArg[0]).fl);
                case F.SYLVESTER: int flag=(CalcArg[2].isZero(RING))?0:1;
                   MatrixD sylv= new MatrixD( MatrixD.Sylvester((Polynom)CalcArg[0],(Polynom)CalcArg[1],flag,RING),0);
                   sylv.fl=1; 
                   return sylv; // matrixP2F(sylv);
                 case F.AKRITAS:  
                   MatrixD ArkDF= new MatrixD( MatrixD.Akritas(((VectorS)CalcArg[0]).V,
                           ((VectorS)CalcArg[1]).V,
                           ((MatrixD)CalcArg[2]).toIntMatrix(), newRing),0); ArkDF.fl=1;
                   return  ArkDF;   //  matrixP2F(ArkDF);
                case F.CONJUGATE:
                   return CalcArg[0].conjugate(RING);
                case F.GENINVERSE:
                    return new MatrixD( new MatrixD(new MatrixS(((MatrixD) CalcArg[0]),newRing).GenInvers(newRing),RING), ((MatrixD) CalcArg[0]).fl);
//                case F.SOLVEPDE: {
//                    solvePDE s = new solvePDE(RING);
//                    return s.calculatePDE(func);
//                }
                case F.TEILOR: {
                    if (CalcArg.length == 2) {
                        return runTeilor(CalcArg[0], CalcArg[1]);
                    }
                    if (CalcArg.length == 3) {
                        return truncatedTeilor(CalcArg[0], CalcArg[1], CalcArg[2].intValue());
                    }
                    return null;
                }
//                case F.MATHEXPECTATION: {
//                    if (CalcArg.length == 1) {
//                        RandomQuantity M = new RandomQuantity(((MatrixD) CalcArg[0]).M[0], ((MatrixD) CalcArg[0]).M[1]);
//                        return M.mathExpectation(RING);
//                    } else {
//                        if (CalcArg.length == 2) {
//                            RING.exception.append(new Fname("У команды mathExpectation не может быть двух аргументов!"));
//                            return null;
//                        } else {
//                            RandomQuantity M = new RandomQuantity(CalcArg[0], CalcArg[1], CalcArg[2]);
//                            return M.mathExpectation(RING);
//                        }
//                    }
//                }
//                case F.DISPERSION: {
//                    if (CalcArg.length == 1) {
//                        RandomQuantity M = new RandomQuantity(((MatrixD) CalcArg[0]).M[0], ((MatrixD) CalcArg[0]).M[1]);
//                        return M.dispersion(RING);
//                    } else {
//                        if (CalcArg.length == 2) {
//                            RING.exception.append(new Fname("У команды dispersion не может быть двух аргументов!"));
//                            return null;
//                        } else {
//                            RandomQuantity M = new RandomQuantity(CalcArg[0], CalcArg[1], CalcArg[2]);
//                            return M.dispersion(RING);
//                        }
//                    }
//                }
//                case F.MEANSQUAREDEVIATION: {
//                    if (CalcArg.length == 1) {
//                        RandomQuantity M = new RandomQuantity(((MatrixD) CalcArg[0]).M[0], ((MatrixD) CalcArg[0]).M[1]);
//                        return M.meanSquareDeviation(RING);
//                    } else {
//                        if (CalcArg.length == 2) {
//                            RING.exception.append(new Fname("У команды meanSquareDeviation не может быть двух аргументов!"));
//                            return null;
//                        } else {
//                            RandomQuantity M = new RandomQuantity(CalcArg[0], CalcArg[1], CalcArg[2]);
//                            return M.meanSquareDeviation(RING);
//                        }
//                    }
//                }
//                case F.SIMPLIFYQUANTITY: {
//                    Element[][] QW = ((MatrixD) CalcArg[0]).M;
//                    RandomQuantity M = new RandomQuantity(QW[0], QW[1]);
//                    RandomQuantity A = M.simplify(RING);
//                    Element[][] A1 = new Element[2][A.P.length];
//                    A1[0] = A.X;
//                    A1[1] = A.P;
//                    return new MatrixD(new MatrixD(A1), 0);
//                }
//                case F.ADDRANDOMQUANTITY: {
//                    Element[][] Q = ((MatrixD) CalcArg[0]).M;
//                    RandomQuantity M = new RandomQuantity(Q[0], Q[1]);
//                    Element[][] Q1 = ((MatrixD) CalcArg[1]).M;
//                    RandomQuantity M1 = new RandomQuantity(Q1[0], Q1[1]);
//                    RandomQuantity A = M.add(M1, RING);
//                    Element[][] A1 = new Element[2][A.P.length];
//                    A1[0] = A.X;
//                    A1[1] = A.P;
//                    return new MatrixD(new MatrixD(A1), 0);
//                }
//                case F.MULTIPLYRANDOMQUANTITY: {
//                    Element[][] Q = ((MatrixD) CalcArg[0]).M;
//                    RandomQuantity M = new RandomQuantity(Q[0], Q[1]);
//                    Element[][] Q1 = ((MatrixD) CalcArg[1]).M;
//                    RandomQuantity M1 = new RandomQuantity(Q1[0], Q1[1]);
//                    RandomQuantity A = M.multiply(M1, RING);
//                    Element[][] A1 = new Element[2][A.P.length];
//                    A1[0] = A.X;
//                    A1[1] = A.P;
//                    return new MatrixD(new MatrixD(A1), 0);
//                }
//                case F.COVARIANCE: {
//                    Element[][] Q = ((MatrixD) CalcArg[0]).M;
//                    RandomQuantity M = new RandomQuantity(Q[0], Q[1]);
//                    Element[][] Q1 = ((MatrixD) CalcArg[1]).M;
//                    RandomQuantity M1 = new RandomQuantity(Q1[0], Q1[1]);
//                    return M.covariance(M1, RING);
//                }
//                case F.CORRELATION: {
//                    Element[][] Q = ((MatrixD) CalcArg[0]).M;
//                    RandomQuantity M = new RandomQuantity(Q[0], Q[1]);
//                    Element[][] Q1 = ((MatrixD) CalcArg[1]).M;
//                    RandomQuantity M1 = new RandomQuantity(Q1[0], Q1[1]);
//                    return M.correlation(M1, RING);
//                }
//                case F.SAMPLEMEAN: {
//                    Element[] M = new Element[((VectorS) CalcArg[0]).V.length];
//                    for (int index = 0; index < ((VectorS) CalcArg[0]).V.length; index++) {
//                        M[index] = ((VectorS) CalcArg[0]).V[index];
//                    }
//                    return RandomQuantity.sampleMean(M, RING);
//                }
//                case F.SAMPLEDISPERSION: {
//                    Element[] M = new Element[((VectorS) CalcArg[0]).V.length];
//                    for (int index = 0; index < ((VectorS) CalcArg[0]).V.length; index++) {
//                        M[index] = ((VectorS) CalcArg[0]).V[index];
//                    }
//                    return RandomQuantity.sampleDispersion(M, RING);
//                }
//                case F.COVARIANCECOEFFICIENT: {
//                    Element[] M = new Element[((VectorS) CalcArg[0]).V.length];
//                    for (int index = 0; index < ((VectorS) CalcArg[0]).V.length; index++) {
//                        M[index] =  ((VectorS) CalcArg[0]).V[index];
//                    }
//                    Element[] M1 = new Element[((VectorS) CalcArg[1]).V.length];
//                    for (int index = 0; index < ((VectorS) CalcArg[1]).V.length; index++) {
//                        M1[index] = ((VectorS) CalcArg[1]).V[index];
//                    }
//                    return RandomQuantity.covarianceCoefficient(M, M1, RING);
//                }
//                case F.CORRELATIONCOEFFICIENT: {
//                    Element[] M = new Element[((VectorS) CalcArg[0]).V.length];
//                    for (int index = 0; index < ((VectorS) CalcArg[0]).V.length; index++) {
//                        M[index] = ((VectorS) CalcArg[0]).V[index];
//                    }
//                    Element[] M1 = new Element[((VectorS) CalcArg[1]).V.length];
//                    for (int index = 0; index < ((VectorS) CalcArg[1]).V.length; index++) {
//                        M1[index] = ((VectorS) CalcArg[1]).V[index];
//                    }
//                    return RandomQuantity.correlationCoefficient(M, M1, RING);
//                }
//                case F.MULTIPLY_SERIES:
//                    return Series.multiply(CalcArg[0], CalcArg[1], RING);
//                case F.ADD_SERIES:
//                    return Series.addSeries((F) CalcArg[0], (F)  CalcArg[1], RING);
//                case F.SUBTRACT_SERIES:
//                    return Series.subtractSeries((F) CalcArg[0], (F) CalcArg[1], RING);
                case F.ABS:
                    return CalcArg[0].abs(RING);
               case F.ARG:
                    return CalcArg[0].arg(RING);
                case F.SIGN:
                    switch(CalcArg[0].numbElementType()){
                        case Ring.MatrixD:
                          return ((MatrixD)CalcArg[0]).signum(RING);
                        case Ring.VectorS:
                          return ((VectorS)CalcArg[0]).signum(RING);
                        default:
                          int sign = CalcArg[0].signum();
                          return (sign == -1) ? RING.numberMINUS_ONE : (sign == 1) ? RING.numberONE : RING.numberZERO;
                    }
               case F.POW:
                    if (CalcArg[0] instanceof F) {
                        if (((F) CalcArg[0]).name == F.EXP) {
                            return new F(F.EXP, new Element[] {((F) CalcArg[0]).X[0].multiply(CalcArg[1], RING)});
                        }
                    }
                    return new F(func.name, CalcArg);
                case F.intPOW:
                    if (((CalcArg[0] instanceof MatrixD) || (CalcArg[0] instanceof MatrixD)) & (CalcArg[1].isMinusOne(RING))) {
                        return new MatrixD(new MatrixD(new MatrixS(((MatrixD) CalcArg[0]),newRing).inverse(newRing),RING), ((MatrixD) CalcArg[0]).fl);
                    }
                    if (CalcArg[0] instanceof F) {
                        if (((F) CalcArg[0]).name == F.EXP) {
                            return new F(F.EXP, new Element[] {((F) CalcArg[0]).X[0].multiply(CalcArg[1], RING)});
                        }
                    }
                    return new F(func.name, CalcArg);
                case F.SUMGEOMPROGRESS: {
                    Element tr=delete_unnecessary_wraps(new TryGetSumProgress().TryGetSumProgress((Polynom) expandFnameOrId(CalcArg[0]), RING));
                    return new Factor(tr,RING);
                }
                case F.SUMOFPOL: {
                    int j = 0;
                    int k = 0;
                    int beg[] = new int[((VectorS) CalcArg[2]).V.length / 2];
                    int end[] = new int[((VectorS) CalcArg[2]).V.length / 2];
                    Polynom f = (Polynom) CalcArg[0];
                    Polynom v = Polynom.polynomFromNumber(RING.numberONE, RING);
                    for (int i = 0; i < ((VectorS) CalcArg[1]).V.length; i++) {
                        v = ((Polynom) ((VectorS) CalcArg[1]).V[i]).multiply(v, RING);
                    }
                    for (int i = 0; i < ((VectorS) CalcArg[2]).V.length; i++) {
                        if (i % 2 == 0) {
                            beg[j] = ((VectorS) CalcArg[2]).V[i].intValue();
                            j++;
                        } else {
                            end[k] = ((VectorS) CalcArg[2]).V[i].intValue();
                            k++;
                        }
                    }
                    return new Sum().SummingOfPolynomial(f, v, beg, end, RING);
                }
//                case F.TRANSFORM:
//                    return new Sum_Simplify().Transform((F) CalcArg[0], RING);
//                case F.LIM:
//                    if(CalcArg.length==3){
//                      if(CalcArg[1] instanceof Fname){
//                         CleanElement(CalcArg[1]);
//                         CleanElement(CalcArg[0]);
//                         Element convFunc=covertListElements(CalcArg[0], new Element[]{CalcArg[1]});
//                         Polynom varLim=(Polynom)workToDefault(CalcArg[1],1);
//                         Element[] pointLim=new Element[varLim.powers.length];
//                         for(int lm=0;lm<pointLim.length-1;lm++) pointLim[lm]=RING.numberZERO;
//                         pointLim[pointLim.length-1]=CalcArg[2];
//                         makeWorkRing(0);
//                         return new LimitOf(newRing,varLim.powers.length-1).Limit(convFunc, pointLim);
//                      }
//                      if(CalcArg[1].equals(RING.varPolynom[0], RING)) {
//                            return new LimitOf(RING).Limit(CalcArg[0], CalcArg[2]);
//                      }else{
//                            Element[] pointLim=new Element[RING.varPolynom.length];
//                            for(int lm=0;lm<pointLim.length;lm++) pointLim[lm]=RING.numberZERO;
//                            pointLim[((Polynom)CalcArg[1]).powers.length-1]=CalcArg[2];
//                            return new LimitOf(RING,((Polynom)CalcArg[1]).powers.length-1).Limit(CalcArg[0], pointLim);
//                      }
//                    }
//                    return new LimitOf(RING).Limit(CalcArg[0], CalcArg[1]);
//  //  (str.1727)
//                case F.INT:
//                    Element resInt = new Integrate().integration(CalcArg[0], CalcArg[1], RING);
//                    return   resInt;
                case F.RANDOMPOLYNOM: {
                    int[] randomtype = new int[CalcArg.length];
                    for (int i = 0; i < CalcArg.length; i++) {
                        randomtype[i] = CalcArg[i].intValue();
                    }
                    return new F(new Polynom(randomtype, new Random(), RING));
                }
                case F.RANDOMMATRIX: {
                    int[] randomtype = new int[CalcArg.length - 3];
                    for (int i = 3; i < CalcArg.length; i++) {
                        randomtype[i - 3] = CalcArg[i].intValue();
                    }
                    return (randomtype.length == 1) ? new MatrixD(new MatrixD(
                            CalcArg[0].intValue(), //строки
                            CalcArg[1].intValue(), //столбцы
                            CalcArg[2].intValue(), //плотность
                            randomtype, new Random(), RING), 0)
                            : new MatrixD(new MatrixD(
                            CalcArg[0].intValue(), //строки
                            CalcArg[1].intValue(), //столбцы
                            CalcArg[2].intValue(), //плотность
                            randomtype, new Random(), RING), 0);
                }
                case F.RANDOMNUMBER:
                    int[] randomtype = new int[CalcArg.length];
                    for (int i = 0; i < CalcArg.length; i++) {
                        randomtype[i] = CalcArg[i].intValue();
                    }
                    return RING.numberONE.random(randomtype, new Random(), RING);
               case F.FACTORINC:
                    return  ( CalcArg[0] instanceof Polynom)?  ((Polynom) CalcArg[0]).factorOfPol_inC(RING): CalcArg[0] ;
   
                case F.LEADINGCOEFF:
                    return  ( CalcArg[0] instanceof Polynom)?  ((Polynom) CalcArg[0]).highCoeff(): CalcArg[0] ;
                case F.EXTENDEDGCD:
                    try {
                        VectorS v= CalcArg[0].extendedGCD(CalcArg[1], RING);
                        if(v.V.length==6){ Element[] NV=new Element[3], VV=v.V; NV[0]=VV[0];
                           Element den=VV[5].multiply(VV[3], RING); 
                           NV[1]=(den.isOne(RING))? VV[1]: new Fraction(VV[1],den).cancel(RING);
                           Element den2=VV[5].multiply(VV[4], RING); 
                           NV[2]=(den2.isOne(RING))? VV[2]: new Fraction(VV[2],den2).cancel(RING);          
                        return new VectorS(NV);
                        }else return v;
                    } catch (Exception ex) {
                        RING.exception.append("Exaption in extendedGCD in inputExpression");
                        return new F(F.EXTENDEDGCD, CalcArg);
                    }
                case F.GROEBNERB:
                    return new VectorS(
                            com.mathpar.polynom.gbasis.util.CommandsRunner.runGbasisB(RING, CalcArg), 0);
                case F.GROEBNER:
                    return new VectorS(
                            com.mathpar.polynom.gbasis.util.CommandsRunner.runGbasis(RING, CalcArg), 0);
                case F.SOLVENAE:
                    return new VectorS(
                            com.mathpar.polynom.gbasis.util.CommandsRunner.runSolveNAE(RING, CalcArg), -1);
                case F.REDUCEBYGB:
                    return com.mathpar.polynom.gbasis.util.CommandsRunner.runReduceByGb(RING, CalcArg);
                case F.FLOOR:
                    return CalcArg[0].floor(RING);
                case F.MOD:
                    return CalcArg[0].mod(CalcArg[1], RING);
                case F.MODCENTERED:
                    return CalcArg[0].Mod(CalcArg[1], RING);
                case F.ROUND:
                    return CalcArg[0].round(RING);
                case F.CEIL:
                    return CalcArg[0].ceil(RING);
                case F.SOLVETRIG:
                    // trig(f,var,n)=3
                    // trig(f,var)=2
                    // trig(f,n)=2
                    if (CalcArg.length == 3) {
                        return new SolveEq(RING).solveTrigEqu(CalcArg[0], CalcArg[1], CalcArg[2], RING);
                    }
                    if (CalcArg.length == 1) {
                        return new SolveEq(RING).solveTrigEqu(CalcArg[0], RING.varPolynom[0], null, RING);
                    }

                    return (CalcArg[1] instanceof Polynom) ? new SolveEq(RING).solveTrigEqu(CalcArg[0], CalcArg[1], null, RING)
                            : new SolveEq(RING).solveTrigEqu(CalcArg[0], RING.varPolynom[0], CalcArg[1], RING);

                case F.SOLVEEQ:
                    // варианты :
                    // 1. solve( P(x)func1= Const | func )
                    // 2. solve( P(x,y,z,....)=Const | func, [y])
                    // 3. solve([P(x,y,z,....)func=Const | func2,P1(x,y,z,....)func1=Const | func3,...])
                    // 4. solve([P(x,y,z,....)func=Const | func2,P1(x,y,z,....)func1=Const | func3,...],[x,y,...])
                    Element ArgEQ = CalcArg[0];
                    if (ArgEQ instanceof VectorS) {// варианты 3\4
                        //---------------------------------------------------------------------------------
                        if (((VectorS) ArgEQ).V.length == 2) {//частный случай вариант 1\2
                            Element trArg = El2Pol(CleanElement(((VectorS) ArgEQ).V[0]));
                            Element trArg1 = El2Pol(CleanElement(((VectorS) ArgEQ).V[1]));
                            Element convEq = trArg.subtract(trArg1, RING);
                            Element res = (CalcArg.length != 2) ? cleanResultOfCalculateRoots(new SolveEq(RING).solve(convEq, RING.varPolynom[0], RING))
                                    : cleanResultOfCalculateRoots(new SolveEq(RING).solve(convEq, (Polynom) El2Pol(((VectorS) CalcArg[1]).V[0]), RING));
                            if (res == null) {
                                int VarPos = (CalcArg.length == 2) ? 0 : ((Polynom) El2Pol(((VectorS) CalcArg[1]).V[0])).powers.length - 1;
                                return cleanResultOfCalculateRoots(returnRootsEq((Polynom) convEq, VarPos));
                            }
                            return (res instanceof VectorS) ? new VectorS((VectorS) res, 1) : res;
                        }
                        //---------------------------------------------------------------------------------

                        int[] indexVar = new int[((VectorS) ArgEQ).V.length / 2];
                        if (CalcArg.length == 1) {
                            for (int h = 0; h < indexVar.length; h++) {
                                indexVar[h] = h;
                            }
                            return solveAlgMatrixs((VectorS) ArgEQ, indexVar, true);
                        } else {
                            for (int h = 0; h < indexVar.length; h++) {
                                indexVar[h] = ((Polynom) ((VectorS) CalcArg[1]).V[h]).powers.length - 1;
                            }
                            return solveAlgMatrixs((VectorS) ArgEQ, indexVar, true);
                        }
                    } else {// вариант 1\2: не верно, что  (ArgEQ instanceof VectorS)
                        Element trArg = El2Pol(CleanElement(ArgEQ));
                        Element trArg1 = El2Pol(CleanElement(CalcArg[1]));
 
                        Element convEq = El2Pol(trArg).subtract( trArg1, RING);
                        Element res = (CalcArg.length == 2) ? cleanResultOfCalculateRoots(new SolveEq(RING).solve(convEq, RING.varPolynom[0], RING))
                                : cleanResultOfCalculateRoots(new SolveEq(RING).solve(convEq, (Polynom) El2Pol(((VectorS) CalcArg[2]).V[0]), RING));
                        if ((res == null)||(res==Element.NAN)) {
                            int VarPos = (CalcArg.length == 2) ? 0 : ((Polynom) El2Pol(((VectorS) CalcArg[2]).V[0])).powers.length - 1;
                            return cleanResultOfCalculateRoots(returnRootsEq((Polynom) convEq, VarPos));
                        }
                        return (res instanceof VectorS) ? new VectorS((VectorS) res, 1) : res;
                    }

                case F.SOLVE:
                    // варианты : 0. A,b ---> решаем систему, возможны параметры
                    // 1. solve( P(x)func )
                    // 2. solve( P(x,y,z,....)func, [y])
                    // 3. solve([P(x,y,z,....)func,P1(x,y,z,....)func,....])
                    // 4. solve([P(x,y,z,....)func,P1(x,y,z,....)func,...],[x,y,...])
                    Element Arg = CalcArg[0];
                    if(CalcArg.length==2 && Arg instanceof MatrixD ){
                      Element [][] newM=new Element[((MatrixD)Arg).M.length][];
                      for(int h=0;h<newM.length;h++){
                      newM[h]=new Element[((MatrixD)Arg).M[h].length+1];
                      System.arraycopy(((MatrixD)Arg).M[h], 0, newM[h], 0, ((MatrixD)Arg).M[h].length);
                      newM[h][newM[h].length-1]=((VectorS)CalcArg[1]).V[h];
                      }
                     VectorS [] pereopMat=new MatrixS(newM,newRing).solve(newM[0].length, RING); // РЕШАЕМ
                     Element[] chars=generate_names_inSysSolve(newM[0].length-1);
                     Element[] res=new Element[pereopMat.length];
                     VectorS RES=pereopMat[pereopMat.length-1];
                     for(int in_p=0;in_p<res.length-1;in_p++)
                         RES=RES.add((VectorS)pereopMat[in_p].multiply(chars[in_p], RING),RING);
                     int flRes=((VectorS)CalcArg[1]).fl;  flRes=(((MatrixD)Arg).fl>0)||(flRes>0)||(flRes<-1)?-3:-1;
                     return new VectorS(RES,flRes);
                    }
                    if(Arg instanceof MatrixD){
                     MatrixD eqM=((MatrixD)Arg);
                     VectorS [] pereopMat=new MatrixS(eqM,newRing).solve(eqM.colNum(), RING);
                     Element[] chars=generate_names_inSysSolve(eqM.colNum()-1);
                     Element[] res=new Element[pereopMat.length];
                     VectorS RES=pereopMat[pereopMat.length-1];
                     for(int in_p=0;in_p<res.length-1;in_p++)
                         RES=RES.add((VectorS)pereopMat[in_p].multiply(chars[in_p], RING),RING);
                     return new VectorS(RES,-((MatrixD)Arg).fl-1);
                    }
                //    }
                    if (Arg instanceof VectorS) {
                        //------------------------------------------------------------------------------------------
                        if (((VectorS) Arg).V.length == 1) {// частный случай вариант 1\2
                            Element trArg = El2Pol(CleanElement(((VectorS) Arg).V[0]));
                            Element res = (CalcArg.length == 1) ? cleanResultOfCalculateRoots(new SolveEq(RING).solve(trArg, RING.varPolynom[0], RING))
                                    : cleanResultOfCalculateRoots(new SolveEq(RING).solve(trArg, (Polynom) El2Pol(((VectorS) CalcArg[1]).V[0]), RING));
                            if (res == null) {
                                int VarPos = (CalcArg.length == 1) ? 0 : ((Polynom) El2Pol(((VectorS) CalcArg[1]).V[0])).powers.length - 1;
                                return cleanResultOfCalculateRoots(returnRootsEq((Polynom) trArg, VarPos));
                            }
                            return (res instanceof VectorS) ? new VectorS((VectorS) res, 1) : res;
                        }
//-------------------------------------------------------------------------------------------
                        VectorS Vec2F=(VectorS)unconvert2FforVectorSinLists(Arg);
                        if((Vec2F.V[0] instanceof F) && (
                                ((F)Vec2F.V[0]).name==F.B_LESS |
                                ((F)Vec2F.V[0]).name==F.B_LE |
                                ((F)Vec2F.V[0]).name==F.B_GT |
                                ((F)Vec2F.V[0]).name==F.B_GE |
                                ((F)Vec2F.V[0]).name==F.B_NE
                                )){
                        int [] znak= new int[Vec2F.length()];
                        Element[] eq=new Element[znak.length];
                        for(int index_=0; index_< znak.length; index_++){
                         switch(((F)Vec2F.V[index_]).name){
                             case F.B_LESS:
                             znak[index_]=Element.LESS; break;
                             case F.B_LE :
                             znak[index_]=Element.LESS_OR_EQUAL;break;
                             case F.B_GT:
                             znak[index_]=Element.GREATER; break;
                             case F.B_GE:
                             znak[index_]=Element.GREATER_OR_EQUAL;break;
                             default:
                             znak[index_]= Element.NOT_EQUAL;
                         }
                         eq[index_]=((F)Vec2F.V[index_]).X[0].subtract(((F)Vec2F.V[index_]).X[1], RING);
                        }
                        return new solveInequality().solveI(eq, znak, RING);
                        }
                        int[] indexVar = new int[((VectorS) Arg).V.length];
                        if (CalcArg.length == 1) {
                            for (int h = 0; h < indexVar.length; h++) {
                                indexVar[h] = h;
                            }
                            return solveAlgMatrixs((VectorS) Arg, indexVar, false);
                        } else {
                            for (int h = 0; h < indexVar.length; h++) {
                                indexVar[h] = ((Polynom) ((VectorS) CalcArg[1]).V[h]).powers.length - 1;
                            }
                            return solveAlgMatrixs((VectorS) Arg, indexVar, false);
                        }
                    } else {
                        if (Arg instanceof F) { Element typeOfEneq=null; //сюда присвоить тип и только одну команду туц ыщдму....
                             switch(((F)Arg).name){
                                 case F.B_LESS:
                                  return new solveInequality().solveI(((F)Arg).X[0].subtract(((F)Arg).X[1], RING),Element.LESS,RING);
                                 case F.B_LE:
                                  return new solveInequality().solveI(((F)Arg).X[0].subtract(((F)Arg).X[1], RING),Element.LESS_OR_EQUAL,RING);
                                 case F.B_GT:
                                  return new solveInequality().solveI(((F)Arg).X[0].subtract(((F)Arg).X[1], RING),Element.GREATER,RING);
                                 case F.B_GE:
                                  return new solveInequality().solveI(((F)Arg).X[0].subtract(((F)Arg).X[1], RING),Element.GREATER_OR_EQUAL,RING);
                                 case F.B_NE:
                                  return new solveInequality().solveI(((F)Arg).X[0].subtract(((F)Arg).X[1], RING),Element.NOT_EQUAL,RING);
                             }
                            Element trArg = El2Pol(CleanElement(Arg));
                            Element res = (CalcArg.length == 1) ? cleanResultOfCalculateRoots(new SolveEq(RING).solve(trArg, RING.varPolynom[0], RING))
                                    : cleanResultOfCalculateRoots(new SolveEq(RING).solve(trArg, (Polynom) El2Pol(((VectorS) CalcArg[1]).V[0]), RING));
                            if (res == null) {
                                int VarPos = (CalcArg.length == 1) ? 0 : ((Polynom) El2Pol(((VectorS) CalcArg[1]).V[0])).powers.length - 1;
                                return cleanResultOfCalculateRoots(returnRootsEq((Polynom) trArg, VarPos));
                            }
                            return (res instanceof VectorS) ? new VectorS((VectorS) res, 1) : res;
                        } else { // случай 1 считаем по первой переменной из кольца
                            int VarPos = (CalcArg.length == 1) ? 0 : ((Polynom) El2Pol(((VectorS) CalcArg[1]).V[0])).powers.length - 1;
                            makeWorkRing(index_in_Ring - RING.varNames.length-1); // 2015-11-27
                            Element res = new SolveEq(RING).solve(Arg, newRing.varPolynom[VarPos], RING);
                            if (res == null) {
                               res=cleanResultOfCalculateRoots(returnRootsEq((Polynom) Arg, VarPos));
                            }else{
                               res=cleanResultOfCalculateRoots(res);
                            }
                            return (res instanceof VectorS) ? new VectorS((VectorS) res, 1) : res;
                       }
                    }
                case F.TOMATRIX:
                    return new MatrixD(new MatrixD(new MatrixS(((VectorS) CalcArg[0]).V, newRing),RING), ((VectorS) CalcArg[0]).fl);
                case F.INVERSE:return ((MatrixD) CalcArg[0]).inverse(newRing); 
                 //   return new MatrixD(new MatrixD(new MatrixS(((MatrixD) CalcArg[0]), newRing).inverse(newRing),RING), ((MatrixD) CalcArg[0]).fl);
                case F.ADJOINT: return ((MatrixD) CalcArg[0]).adjoint(newRing);
                //  return new MatrixD(new MatrixD(new MatrixS(((MatrixD) CalcArg[0]), newRing).adjoint(newRing),RING), ((MatrixD) CalcArg[0]).fl);
              case F.SOLVELAE: {
                    MatrixS mat = new MatrixS(((MatrixD) CalcArg[0]),newRing);
                    VectorS nat = ((VectorS) CalcArg[1]);
                    MatrixS a = mat.append(nat);
                    MatrixS b = a.toEchelonForm(newRing);
                    if (b.isSysSolvable()) {int flRes=((VectorS)CalcArg[1]).fl;  flRes=(((MatrixD)CalcArg[0]).fl>0)||(flRes>0)||(flRes<-1)?-3:-1;
                        return new VectorS(b.oneSysSolvForFraction(newRing), flRes);
                    } else {
                        return new VectorS( new Element[] {}, -1);
                    }
                }
              case F.TAKEROW: {return ((MatrixD) CalcArg[0]).takeRow(CalcArg[1].intValue());}
              case F.TAKECOLUMN: {return ((MatrixD) CalcArg[0]).takeColumn(CalcArg[1].intValue());}
              case F.RANK: {return ((MatrixD) CalcArg[0]).rank(newRing);
                 //   return new MatrixD(new MatrixD(new MatrixS(((MatrixD) CalcArg[0]),newRing).kernel(newRing),RING), ((MatrixD) CalcArg[0]).fl);
                }                                           
                case F.KERNEL: {return ((MatrixD) CalcArg[0]).kernel(newRing);
                 //   return new MatrixD(new MatrixD(new MatrixS(((MatrixD) CalcArg[0]),newRing).kernel(newRing),RING), ((MatrixD) CalcArg[0]).fl);
                }
                case F.TRANSPOSE: {
                    if (CalcArg[0] instanceof VectorS) {
                        int ffl=((VectorS) CalcArg[0]).fl;
                        return new VectorS(((VectorS) CalcArg[0]), -ffl-1);
                    }
                    return new MatrixD(((MatrixD) CalcArg[0]).transpose(RING), ((MatrixD) CalcArg[0]).fl);
                }
                case F.TOECHELONFORM: {return ((MatrixD) CalcArg[0]).toEchelonForm(newRing);
                  //  return new MatrixD(new MatrixD(new MatrixS(((MatrixD) CalcArg[0]),newRing).toEchelonForm(newRing),RING), ((MatrixD) CalcArg[0]).fl);
                }
                case F.CHARPOLYNOM: {
                    MatrixD mat =((MatrixD) CalcArg[0]);
                    Polynom temp_res = mat.characteristicPolynomP(newRing);
                    Element[] newcoef = new Element[temp_res.coeffs.length];
                    for (int h = 0; h < newcoef.length; h++) {
                        newcoef[h] = ElementToF(temp_res.coeffs[h]);
                    }
                    return new Polynom(temp_res.powers, newcoef);
                }
                case F.DET:
                    if (CalcArg[0] instanceof MatrixD) {
                        return ((MatrixD) CalcArg[0]).det(newRing);
                    } else if (CalcArg[0] instanceof MatrixS) {
                        return ((MatrixS) CalcArg[0]).det(newRing);
                    }
                case F.VALUE:
                    CalcArg[0]=unconvert2FforVectorSinLists(CalcArg[0]);// на случай с векторами
                    Element res_val = runOperatorValue(CalcArg);
                    return (res_val == null) ? new F(F.VALUE, CalcArg) : res_val;
                case F.FACTOR: {
                    expand_flag = false;
                    Element res = runOperatorFactor(CalcArg[0]);
                    expand_flag = true;
                    return res;
                }
                case F.B_NE:        
                case F.B_EQ:
                case F.B_GE:
                case F.B_GT:
                case F.B_LE:
                case F.B_LESS:    
                   CalcArg=helpToBooleanFunctions(CalcArg);
                   return(CalcArg[0].isItNumber() && CalcArg[1].isItNumber()) ? new F(func.name, CalcArg).booleanFunction(RING) : new F(func.name, CalcArg);
                case F.B_AND:     
                case F.B_OR:
                    CalcArg=helpToBooleanFunctions(CalcArg); 
                   if((CalcArg[0] instanceof MatrixD)||(CalcArg[0] instanceof MatrixD)||(CalcArg[0] instanceof VectorS)||(CalcArg[0] instanceof VectorS)) 
                       return (F.B_OR==func.name)? CalcArg[0].B_OR(CalcArg[1], RING): CalcArg[0].B_AND(CalcArg[1], RING);
                   return(CalcArg[0].isItNumber() && CalcArg[1].isItNumber()) ?
                       new F(func.name, CalcArg).booleanFunction(RING): new F(func.name, CalcArg);
                case F.B_NOT:
                    CalcArg[0]=simplify_init(CalcArg[0]);
                    if((CalcArg[0] instanceof MatrixD)||(CalcArg[0] instanceof MatrixD)||(CalcArg[0] instanceof VectorS)||(CalcArg[0] instanceof VectorS)) 
                       return   CalcArg[0].B_NOT(RING);  
                    return(CalcArg[0].isItNumber()) ? new F(func.name, new Element[]{expandFnameOrId(CalcArg[0])}).booleanFunction(RING) :  new F(func.name, CalcArg);
                case F.HPOLCOEFFICIENT: 
                     CalcArg[0]=simplify_init(CalcArg[0]);      
                     return (CalcArg[0] instanceof Polynom)? ((Polynom)CalcArg[0]).highCoeff(): RING.numberONE();
                case F.HPOLTERM: 
                     CalcArg[0]=simplify_init(CalcArg[0]);
                     return (CalcArg[0] instanceof Polynom)? ((Polynom)CalcArg[0]).hTerm(): RING.numberONE();
                 case F.RE: return   CalcArg[0].Re(RING);         
                 case F.IM: return   CalcArg[0].Im(RING);
                 case F.TOCOMPLEX: return   CalcArg[0].toComplex(RING);
                 case F.FACTORIAL:
                     Element res = CalcArg[0].factorial(RING);
                    return (res == null) ? new F(F.FACTORIAL, CalcArg[0]) : res;
//                case F.SOLVETRANSFERFUNCTION:
//                    return dynamicProperties.solveTransferFunction((F)CalcArg[0], (F)CalcArg[1], RING);
//                case F.SOLVETIMERESPONSE:
//                    return dynamicProperties.solveTimeResponse((F)CalcArg[0], (F)CalcArg[1], RING);
//                case F.SOLVEFREQUENCERESPONSE:
//                    return dynamicProperties.solveFrequenceResponse((F)CalcArg[0], (F)CalcArg[1], RING);
//                case F.SOLVEDE:{ 
//                    //F A0=((F)CalcArg[0]);
//                    // Element leftPartDifEq =  (CalcArg.length==1)?
//                    //         ExpandForYourChoise( A0 , 1, 1, 1, -1, 1):
//                    //         ExpandForYourChoise((F) A0.subtract(CalcArg[1] ,RING), 1, 1, 1, -1, 1);
//                    // return new  SolveDiffEq().solve(leftPartDifEq,  RING);          
//                    return new SolveDiffEq().solve2(CalcArg[0], CalcArg[1]);
//                } 
//              case F.SOLVEHDE:{
//                    SolveHomogeneousDiffEq shde = new SolveHomogeneousDiffEq();
//                    return shde.solve(CalcArg[0], CalcArg[1]);
//              }
//                case F.SOLVELDE:
//                    try {
//                        if (CalcArg.length == 3) {
//                            SystemLDE slde = new SystemLDE();
//                            VectorS resSystemLDE = slde.solveSystemLDE_accuracy((F) CalcArg[0], (F) CalcArg[1], (NumberR)CalcArg[2], RING);
//                            page.setRing(slde.newRing);
//                            return resSystemLDE;
//                        } else{
//                            return (((F) CalcArg[0]).X.length == 2)
//                                ? new LDE().solveDifEquation((F) CalcArg[0], (F) CalcArg[1], RING)
//                                : new SystemLDE().solveSystemLDE((F) CalcArg[0], (F) CalcArg[1], RING);
//                        }
//                    } catch (Exception ex) {
//                        RING.exception.append('\n').append(ex.getMessage());
//                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
//                        return null;
//                    }
//                case F.LAPLACETRANSFORM: {
//                    return new LaplaceTransform().transform(simplify_init(CalcArg[0]), RING);
//                }
                case F.CLOSURE:
                    return CalcArg[0].closure(RING);
                case F.FULLEXPAND: { Element Expand1=CalcArg[0].Expand(RING);
                   return (Expand1 instanceof F)?((F)Expand1).valueOf(RING):Expand1;
               // return (CalcArg[0].Expand(RING)).value(RING);
                }
                case F.SIMPLIFY: {
                    return CalcArg[0].simplify(RING);}
                case F.FULLFACTOR: {
                    return CalcArg[0].Factor(true,RING);
                }
                case F.FFULLFACTOR:
                    return CalcArg[0].FACTOR(RING);
//               case F.INVERSELAPLACETRANSFORM: {
//                    return new InverseLaplaceTransform().inverseLaplaceTransform(simplify_init(CalcArg[0]), RING);
//                }
                case F.TIME:
                    return new NumberZ64(System.currentTimeMillis());
                case F.GCD:
                    return runOperatorGCD(CalcArg);
                case F.EXPAND:
                   return convertFactorInF(CalcArg[0]).expand(RING); // убираем все  Factor & FactorPol
                    // return (CalcArg[0] instanceof F) oo
                  //          ? Univers_Expand((F) CalcArg[0], false) : CalcArg[0];
                case F.LCM:
                    return runOperatorLCM(CalcArg);
                case F.D:
                    return runOperatorD(CalcArg);
                case F.DETL:
                    if (CalcArg.length==2) {
                        return MatrixD.detL( CalcArg[0].intValue(), (VectorS)CalcArg[1],  newRing);
                    } else if (CalcArg.length==3) {
                        return MatrixD.detL( CalcArg[0].intValue(), (VectorS)CalcArg[1],  (VectorS)CalcArg[2], newRing);
                    }
                case F.TOVECTORDENCE: 
                    if(CalcArg[0] instanceof Polynom) {int w=((Polynom)CalcArg[0]).varNumb();
                        if (w!=0)w--; int vsize= (CalcArg.length>1)? CalcArg[1].intValue():1;
                        return( (Polynom)CalcArg[0]).toVectorDence(vsize, w, RING);}
                    else return  new VectorS(CalcArg[0]);
                case F.TOVECTORSPARCE:  
                     if(CalcArg[0] instanceof Polynom) {int w=((Polynom)CalcArg[0]).varNumb();
                        if (w!=0)w--;
                        return( (Polynom)CalcArg[0]).toVectorSparce(w, RING);}
                    else return  new VectorS(CalcArg[0], NumberZ.ZERO);
                case F.VECTORTOPOLYNOM:  
                   return (CalcArg[0] instanceof VectorS)? Polynom.vectorToPolynom((VectorS) CalcArg[0], RING): Element.NAN;  
                 case F.SHIFTLEFT:   
                  if (CalcArg[0] instanceof VectorS){return ((VectorS)CalcArg[0]).siftLeft(CalcArg[1].intValue());}
                  else if  (CalcArg[0] instanceof MatrixD){return ((MatrixD)CalcArg[0]).siftLeft(CalcArg[1].intValue());}
                   else return Element.NAN;  
                case F.SHIFTRIGHT:  
                  if (CalcArg[0] instanceof VectorS){return ((VectorS)CalcArg[0]).siftRight(CalcArg[1].intValue());}
                  else if  (CalcArg[0] instanceof MatrixD){return((MatrixD)CalcArg[0]).siftRight(CalcArg[1].intValue());}
                  return Element.NAN;             
                default:
                    return (func.name == F.ID)? CalcArg[0]:new F(func.name, CalcArg);
            }
        
    }
/**
 * Results VectorS which elements are MatrixS converted to MatrixD
 * @param forResults  MatrixS[] as the Vector elements
 * @param fl  the flag of CanonicForm for the transc-Polynomial transformation
 * @return VectorS (which is the VectorS with Flag fl)
 */
    private VectorS makeVectorSfromMatrixS(Element[]  forResults, int fl ){
               Element[] resl = new Element[forResults.length];
               for (int l = 0; l < resl.length; l++) {
                   resl[l] =   new MatrixD((MatrixS) forResults[l], true, RING , fl );
               }
               return new VectorS(resl,fl );
    }
    
    private Element[] helpToBooleanFunctions(Element[] X){
    return new Element[]{expandFnameOrId(X[0]),expandFnameOrId(X[1])};
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
            return (((F) g).name == F.ID)  ? expandFnameOrId(((F) g).X[0]) : g;
        }
        if (g instanceof Fname) {
            return (((Fname) g).X == null) ? g : (((Fname) g).X[0]==null)? g
                    : expandFnameOrId(((Fname) g).X[0]);
        }
        return g;
    }
  public Element expandFnameOrIdOrElOf(Element g) {
        if (g instanceof F) {
            return ((((F) g).name == F.ID)||(((F) g).name == F.ELEMENTOF)) ? expandFnameOrIdOrElOf(((F) g).X[0]) : g;
        }
        if (g instanceof Fname) {
            return (((Fname) g).X == null) ? g : (((Fname) g).X[0]==null)? g
                    : expandFnameOrIdOrElOf(((Fname) g).X[0]);
        }
        return g;
    }

//    /**
//     * Процедура поднимающая последний обьект Fname, из g которая представляет
//     * комбинацию вложенных обьектов Fname или F c именами ID.
//     *
//     * @param g
//     *
//     * @return
//     */
//    private Element substituteFname(Fname g) {
//        if (g.X == null) {
//            return g;
//        } else {
//            if (g.X[0] instanceof F) {
//                if (((F) g.X[0]).name == F.ID
//                        && ((F) g.X[0]).X[0] instanceof Fname) {
//                    return substituteFname((Fname) ((F) g.X[0]).X[0]);
//                }
//            }
//        }
//        return g;
//    }

    /**
     * Метод вытаскивающий первый коэффициент из полинома если он число.
     *
     * @param el
     *
     * @return
     */
    private Element correctPolynomAndNumbers(Element el) {
     //   if (RING.algebra[0] == Ring.Q) { }
        if (el instanceof Polynom) {
            if (el.isZero(RING)) {
                return RING.numberZERO;
            }
            return (((Polynom) el).isItNumber()) ? ((Polynom) el).coeffs[0] : el;
        } else {
            return el;
        }
    }

    /**
     * Метод формирующий обьекты MatrixS | VectorS в зависимости от rows[0].
     *
     * @param rows - входной массив элементов
     *
     * @return MatrixS | VectorS
     */
    private Element returnMatrixOrVector(Element[] rows) {
        makeNewVectFandVectEL();//исправлено Рыбаковым М.А. 21.10.2011.
        if (rows[0] instanceof VectorS) {
            Element[][] M = new Element[rows.length][];
            for (int i = 0; i < rows.length; i++) {
                M[i] = new Element[((VectorS) rows[i]).V.length];
                M[i] = ((VectorS) rows[i]).V.clone();
            }
            return new MatrixS(M, RING);
        }
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] instanceof F) {
                rows[i] = Univers_Expand((F) rows[i], false);
            }
        }
        return new VectorS(rows);
    }

     /**
     * Обычное избавление от ассоциативных скобок в узлах MULTIPLY | ADD
     *
     * @param ARGUMENTS - аргументы узла
     * @param NAME - MULTIPLY | ADD
     *
     * @return
     */
    private Element[] workWithMultiply(Element[] ARGUMENTS, int NAME) {
        Vector<Element> newX = new Vector<Element>();
        newX.add(RING.numberONE);
        for (Element el : ARGUMENTS) {
            if (el instanceof F) {
                if (((F) el).name == NAME) {
                    int oldLeng=newX.size();
                    newX.addAll(Arrays.asList(workWithAddAndMultiply(((F) el).X, NAME)));
                    if(newX.get(newX.size()-oldLeng).isItNumber() || newX.get(newX.size()-oldLeng) instanceof Polynom){
                    Element SumEl=newX.get(0).multiply(newX.get(newX.size()-oldLeng), RING);
                    newX.set(0, SumEl);
                    newX.remove(newX.size()-oldLeng);
                    }
                } else {
                    newX.add(el);
                }
            } else {
                if(el.isItNumber() || el instanceof Polynom){
                Element SumEl=newX.get(0).multiply(el, RING);
                newX.set(0, SumEl);
                }else{
                newX.add(el);
                }
             }
        }

        if(newX.get(0).isOne(RING)){
         newX.remove(0); newX.toArray(new Element[newX.size()]);
        }
        return newX.toArray(new Element[newX.size()]);
    }


    /**
     * Обычное избавление от ассоциативных скобок в узлах MULTIPLY | ADD
     *
     * @param ARGUMENTS - аргументы узла
     * @param NAME - MULTIPLY | ADD
     *
     * @return
     */
    private Element[] workWithAddAndMultiply(Element[] ARGUMENTS, int NAME) {
        Vector<Element> newX = new Vector<Element>();
        newX.add(RING.numberZERO);
        for (Element el : ARGUMENTS) {
            if (el instanceof F) {
                if (((F) el).name == NAME) {
                    int oldLeng=newX.size();
                    newX.addAll(Arrays.asList(workWithAddAndMultiply(((F) el).X, NAME)));
                    if(newX.get(newX.size()-oldLeng).isItNumber() || newX.get(newX.size()-oldLeng) instanceof Polynom){
                    Element SumEl=newX.get(0).add(newX.get(newX.size()-oldLeng), RING);
                    newX.set(0, SumEl);
                    newX.remove(newX.size()-oldLeng);
                    }
                } else {
                    newX.add(el);
                }
            } else {
                if(el.isItNumber() || el instanceof Polynom){
                Element SumEl=newX.get(0).add(el, RING);
                newX.set(0, SumEl);
                }else{
                newX.add(el);
                }
             }
        }

        if(newX.get(0).isZero(RING)){
         newX.remove(0); newX.toArray(new Element[newX.size()]);
        }
        return newX.toArray(new Element[newX.size()]);
    }

    /**
     * Вспомогательная процедура при создании векторов и матриц
     *
     * @param rows
     *
     * @return
     */
    private Element SVTFACPtoMatrixsAndVectors(Element[] rows) {
        if(rows.length == 0) return new VectorS(new Element[]{}, 0);
        if (rows[0] instanceof VectorS) { // если аргументы вектора , то все уже обернуто и возвоащаем матрицу
            Element[][] M = new Element[rows.length][];
            int t = 0;
            for (int i = 0; i < rows.length; i++) {
                M[i] = new Element[((VectorS) rows[i]).V.length];
                M[i] = ((VectorS) rows[i]).V.clone();
                t += ((VectorS) rows[i]).fl;
            }
            return (t > 0) ? new MatrixD(new MatrixD(M), 1) : new MatrixD(new MatrixD(M), 0);
        }
        // иначе имеем дело c вектором и производим обертку
        Element[] newVec = new Element[rows.length];
        int t = 0;
        for (int i = 0; i < rows.length; i++) {
            switch (rows[i].numbElementType()) {
                case Ring.F:
                    Element el = CleanElement(rows[i]);
                    newVec[i] = (((F)el).name==F.VECTORS)?
                            SVTFACPtoMatrixsAndVectors(((F)el).X): El2Pol((F) el);
                    t++;
                    break;
                case Ring.Fname:
                    Element elF = CleanElement(rows[i]);
                    newVec[i] = workToDefault(elF, 1);
                    t++;
                    break;
                default:
                    newVec[i] = correctPolynomAndNumbers(rows[i]);
            }
        }
        return (t > 0) ? new VectorS(newVec, 1 ) : new VectorS( newVec, 0 );
    }

    /**
     * Оборачиваем элементы вектора являющиеся функциями
     *
     * @param vec
     *
     * @return
     */
    private VectorS VecElements2Polynom(VectorS vec) {
        Element[] newVec = new Element[vec.V.length];
        Element el;
        for (int i = 0; i < vec.V.length; i++) {
            el = CleanElement(vec.V[i]);
            newVec[i] = El2Pol(el);
        }
        return new VectorS(newVec);
    }
 
    /**
     * Оборачиваем элементы матрицы являющиеся функциями
     *
     * @param MM
     *
     * @return
     */
    private MatrixD MatrixElements2Pol(MatrixD MM) {
        Element[][] newM = new Element[MM.M.length][];
        Element el;
        for (int i = 0; i < MM.M.length; i++) {
            newM[i] = new Element[MM.M[i].length];
            for (int j = 0; j < MM.M[i].length; j++) {
                el = CleanElement(MM.M[i][j]);
                newM[i][j] = El2Pol(el);
            }
        }
        return new MatrixD(newM,1);
    }

    /** Element f может быть функцией F или именем Fname:
     * Производится : 1. Подстановка значений в Fname;
     * 2. Приводим числа и полиномы к R64 , если тип R; 3. Формируем VectorS &
     * MatrixS; 4. Формируем узел intPow; 5. Поднимаем узлы ADD и MULTIPLY; 6.
     * Меняем Fname c именем (\\i) на Complex.I или возвращаем Null если кольцо
     * не Комплексное
     *
     * @param f -input function
     * @return
     */
    public Element substituteValueToFnameAndCorrectPolynoms(Element f, List<Element> expr) {
        int nameF=-1; Element[] XX=null; 
        if (f instanceof F) {nameF=((F) f).name; XX=((F) f).X;} 
        else if (f instanceof Fraction){Fraction vec= (Fraction)f; 
                Element numm = El2Pol(CleanElement(vec.num));
                Element demm = El2Pol(CleanElement(vec.denom));  
   //             int col_new_var = index_in_Ring - RING.varNames.length - 1;
                makeWorkRing(index_in_Ring - RING.varNames.length-1);//2015-11-27              
//                newRing=(col_new_var != 0)?  newRing //addNewVariables(col_new_var)
//                        : RING;
                return new Fraction(numm,demm).cancel(RING);}
        else if (f instanceof Fname) {Fname fn= (Fname)f; 
                 if(fn.isEmpty(RING)) {XX= new Element[]{f}; nameF=F.ID;} 
                 else  XX=fn.X;
        }
        if ( nameF == F.IC |  nameF == F.TIME) return f; // на случай когда приходит \time

        Element[] substituteArg = new Element[XX.length]; // new vector for X
        // this loop too long  ---------------------------------2747-3118 (365 rows!)
        for (int i = 0; i < XX.length; i++) {
            switch (XX[i].numbElementType()) {
                case Ring.F:
                    substituteArg[i] = // substituteValueToFnameAndCorrectPolynoms((F) XX[i], expr);///
                    (((F)XX[i]).name==F.VECTORS)?
                            SVTFACPtoMatrixsAndVectors(((F)XX[i]).X): 
                            substituteValueToFnameAndCorrectPolynoms((F) XX[i], expr );
                    break;
                case Ring.MatrixD:
                    substituteArg[i] = (((MatrixD) XX[i]).fl > 0) ? new MatrixD(MatrixElements2Pol(((MatrixD) XX[i])), 1) : XX[i];
                    break;
                case Ring.VectorS: int ffl=((VectorS) XX[i]).fl;
                    substituteArg[i] = new VectorS(((VectorS)( (ffl > 0)? VecElements2Polynom((VectorS) XX[i]) : (ffl < -1) ? VecElements2Polynom((VectorS) XX[i]) : XX[i])),ffl);
                    break;
                case Ring.Fname:
                    Fname fname = (Fname) XX[i];
                    String name = fname.name;
                    substituteArg[i]=new Fname(name); // дальше мы его заменим...
                    if (name.equals("\\i")) {
                        if (isComplexRing) {
                            substituteArg[i] = RING.complexI();
                        } else {
                            RING.exception.append("Ring is not complex but we are using \\i");
                        }
                        break;
                    }
                    if (name.equals("\\e")) {
                        substituteArg[i] = new F(F.EXP, new Element[] {RING.numberONE});
                        break;
                    }
                    if (name.matches("-\\s*\\\\infty")) { // Между - и \infty могут быть пробелы
                        substituteArg[i] = Element.NEGATIVE_INFINITY;
                        break;
                    }
                    if (name.equals("\\infty")) {
                        substituteArg[i] = Element.POSITIVE_INFINITY;
                        break;
                    }
                   //случай единичной или нулевой матрицы а так же тензоры \O или \I
                  substituteArg[i]=  makeZeroTensor(name, expr);
                  if(substituteArg[i]!=null) break;
//                    if(name.charAt(0)=='\\'){ char second = name.charAt(1);
//                     if(((second=='I') || (second=='O'))&& name.charAt(2)=='_' && name.charAt(3)=='{') {
//                         int last = name.indexOf('}');
//                       if(last>3){ String indS=name.substring(4, last);
//                        F ff=Parser.getF("["+indS+"]", RING);
//                        VectorS vI= (VectorS) getIntegerValue(new VectorS(ff.X));
//                        tensor:if (vI.V.length>0){
//                                    int[] intInd=new int[vI.V.length];
//                                    for (int j = 0; j <vI.V.length; j++) {
//                                        intInd[j]=vI.V[j].intValue(); 
//                                        if(intInd[j]<1) break tensor;
//                                    }
//                                    int indexI=vI.V[0].intValue();
//                                   if(vI.V.length==2){ int indexJ=vI.V[1].intValue();
//                                    substituteArg[i] = (second=='I')?
//                                       new MatrixD(MatrixD.oneMatrixD(indexI, indexJ, RING),0):
//                                       new MatrixD(MatrixD.zeroMatrixD(indexI, indexJ, RING), 0);
//                                   }else if  (vI.V.length==1) {
//                                      VectorS res=new VectorS(indexI,RING.numberZERO);
//                                      if(second=='I') res.V[0]= RING.numberONE;
//                                      substituteArg[i] = new VectorS(res, 0);
//                                  }
//                                }
//                       }
//                       break;
//                     }
//                    }
                    // procedure Element makeZeroTensor(String name );
                   //=0===========================================================start
                    if (((Fname) XX[i]).X != null) {
                        //случай с матрицами A_{i,j} и векторами A_{i}
                        Element[] lowIndexs=((Fname) XX[i]).lowerIndices();
                        if (lowIndexs!=null) {   
                            Element el;// = expandFnameOrId(((Fname) XX[i]).X[0 
                            Element poss = page.searchInMatricesElements(((Fname) XX[i]).name);
                            if (poss != null) {
                                el = expandFnameOrId(((Fname)poss).X[0]);
                                //================================================================================================ 
                                if (el instanceof MatrixD) {// ЕСЛИ ЭТО МАТРИЦА, берем из нее эдемент или стр или стлб
                                    if (lowIndexs[0] != null && lowIndexs[1] != null) {
                                        Element tempJ = getIntegerValue(lowIndexs[1],expr);//, el);
                                        int indexJ;
                                        if (tempJ == null) return null; else {indexJ = tempJ.intValue() - 1;}
                                        Element tempI = getIntegerValue(lowIndexs[0],expr);//, el);
                                        int indexI;
                                        if (tempI == null) return null; else {indexI = tempI.intValue() - 1; }
                                        if (indexJ < 0 | indexI < 0) {
                                            try {throw new Exception(RING.exception.append("Negative or zero index in ").append(XX[i]).toString());
                                            } catch (Exception ex) { Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex); }
                                        }
                                        if (indexI >= ((MatrixD) el).rowNum() | indexJ >= ((MatrixD) el).colNum()) {// т.к. квадратная она все время
                                            try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(XX[i]).toString());
                                            } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);}
                                        }
                                        substituteArg[i] = ((MatrixD) el).getElement(indexI, indexJ);
                                        break;
                                    }

                                    if (lowIndexs[0] != null) {// БЕРЕМ ОДНУ СТРОКУ
                                        Element tempJ = getIntegerValue(lowIndexs[0],expr);//, el);
                                        int indexJ;
                                        if (tempJ == null) { return null;} else {indexJ = tempJ.intValue() - 1;}
                                        if (indexJ < 0) {
                                            try {throw new Exception(RING.exception.append("Negative or zero index in ").append(XX[i]).toString());
                                            } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);}
                                        }
                                        if (indexJ >= ((MatrixD) el).M.length) {
                                            try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(XX[i]).toString());
                                            } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex); }
                                        }
                                        Element[] returnVec = new Element[((MatrixD) el).M[indexJ].length];
                                        System.arraycopy(((MatrixD) el).M[indexJ], 0, returnVec, 0, returnVec.length);
                                        if (((MatrixD) el).fl == 0) {substituteArg[i] = new VectorS(returnVec, 0); break; }
                                        VectorS rv = (VectorS) SVTFACPtoMatrixsAndVectors(returnVec);
                                        substituteArg[i] = rv;
                                        break;
                                    }
                                    if (lowIndexs[1] != null) { // БЕРЕМ ОДИН СТОЛБЕЦ
                                        Element tempI = getIntegerValue(lowIndexs[1],expr);//, el);
                                        int indexI;
                                        if (tempI == null) {return null;} else {indexI = tempI.intValue() - 1;}
                                        if (indexI < 0) {
                                            try {throw new Exception(RING.exception.append("Negative or zero index in ").append(XX[i]).toString());
                                            } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);}
                                        }
                                        if (indexI >= ((MatrixD) el).M[0].length) {
                                            try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(XX[i]).toString());
                                            } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);}
                                        }
                                        Element[] returnM = new Element[((MatrixD) el).M.length] ;
                                        Element tempEl;
                                        int xx = 0;
                                        for (int ind = 0; ind < ((MatrixD) el).M.length; ind++) {
                                            tempEl = ((MatrixD) el).M[ind][indexI];
                                            if (tempEl.numbElementType() <= Ring.Polynom) {returnM[ind] = tempEl;} 
                                            else {tempEl = CleanElement(tempEl);
                                                xx++; returnM[ind] = El2Pol(tempEl);}
                                        }
                                        substituteArg[i] = new VectorS(returnM, (xx>0)?-2:-1);
                                        break;
                                    }
                                } // конец: ЕСЛИ ЭТО МАТРИЦА, берем из нее эдемент или стр или стлб
                                if (el instanceof VectorS){      // ЕСЛИ ЭТО ВЕКТОР
                                    Element tempI = getIntegerValue(lowIndexs[0], expr);//, el);
                                    int indexI;
                                    if (tempI == null) return null; 
                                      else indexI = tempI.intValue() - 1;                                         
                                    if (indexI < 0) {
                                        try {throw new Exception(RING.exception.append("Negative or zero index in ").append(XX[i]).toString());
                                        } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex); }
                                    }
                                    Element[] VV = ((VectorS) el).V ;
                                    if (indexI >= VV.length) {
                                        try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(XX[i]).toString());
                                        } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);}
                                    }
                                    substituteArg[i] = VV[indexI];
                                    break;
                                }
                                if (el instanceof Table) {  // ЕСЛИ ЭТО ТАБЛИЦА
                                    Element tempI = getIntegerValue(lowIndexs[0],expr);//, el);
                                    int indexI;
                                    if (tempI == null) {return null;} else {indexI = tempI.intValue() - 1;}
                                    if (indexI < 0) {
                                        try {throw new Exception(RING.exception.append("Negative or zero index in ").append(XX[i]).toString());
                                        } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);}
                                    }
                                    if (indexI >= ((Table) el).tableSize()) {
                                        try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(XX[i]).toString());
                                        } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);}
                                    }
                                 substituteArg[i] = ((Table) el).getFunction(indexI);// БЕРЕМ ВСЮ СТРОКУ ТАБЛИЦЫ
                                 break;
                                }
                            } else { // ЭТО НЕ ТЕНЗОР, Матр, ВЕКТ.
                                if(((Fname) XX[i]).X!=null&&((Fname) XX[i]).X.length>0&&((Fname) XX[i]).X[0]!=null){
                                 substituteArg[i] = ((Fname) XX[i]).X[0];
                                 break;
                                }
                            }
                         }
                    }
                    
                    // -----------------------------------------------------
                   Element el= expandFnameOrId(XX[i]);
                   switch (el.numbElementType()) {
                        case Ring.F:
                            substituteArg[i] = substituteValueToFnameAndCorrectPolynoms( el, expr);
                            break;
                        case Ring.Polynom:
                            substituteArg[i] = correctPolynomAndNumbers(el);
                            break;
                        case Ring.MatrixD:
                            substituteArg[i] = (((MatrixD) el).fl > 0) ? new MatrixD(MatrixElements2Pol(((MatrixD) el)), 1) : el;
                            break;
                        case Ring.VectorS:
                            substituteArg[i] = ((((VectorS) el).fl > 0)||(((VectorS) el).fl<-1)) ? new VectorS(VecElements2Polynom(((VectorS) el)), ((VectorS) el).fl) : el;
                            break;
                        case Ring.Fname:
                            String elStr=((Fname)el).name;
                            chek_name_in_gr_works(elStr);
                           if(page!=null){
                            Matcher m = Page.VECT_MATRIX.matcher(elStr);
                            if (m.matches()) {
                             if (!page.contentNameInNCIE(elStr)) {
                               elStr= page.replaceOfSymbolsInIndeces(elStr, expr);
                               el = new Fname(elStr);} // Simple NAME elStr 
                            }
                            page.replaceOfSymbols(el,expr);}
                        default:
                            substituteArg[i] = el;
                    }
                    break;
                default:
                    // очиcтка обычных чисел от полиномиальной "обертки"
                    substituteArg[i] = correctPolynomAndNumbers(XX[i]);
            }
        }
        // this loop too long  ---------------------------------2747-3118 (365 rows!)
        switch (nameF) {
            case F.VECTOR_SET:
                return new VectorSet(substituteArg);
            case F.VECTORS:
                return SVTFACPtoMatrixsAndVectors(substituteArg);
            case F.TIME:
                return new NumberZ64(System.currentTimeMillis()); // для того что бы потом не мучиться с пустым массивом аргументов
            // формируем при необходимости некоммутативное умножение для дальнейшей корректной работы
            case F.MULTIPLY:
               boolean fll=search_NC_elements(substituteArg); // флаг отвечающий за наличие некоммутативных элементов в произведении
              return (fll)? new F(F.MULTIPLY_NC,substituteArg)
                     : conectPolynomsMUL(substituteArg);
            // формируем деление
            case F.DIVIDE:
                return divideFSP(substituteArg);
            //формирование intPow и его поднятие
            case F.POW:
                return expand_pow(powFSP(substituteArg, false, true));
            case F.intPOW:
                return expand_pow(new F(F.intPOW, substituteArg));
            case F.ADD:
                return conectPolynomsADD(substituteArg);
            case F.SUBTRACT:
                return(substituteArg[0].numbElementType()<=Ring.Polynom & substituteArg[1].numbElementType()<=Ring.Polynom)? substituteArg[0].subtract(substituteArg[1], RING):
                    new F(F.SUBTRACT,substituteArg);
 //            case F.LIM:
 //                return expand_pow(powFSP(substituteArg, false, true));
            default:
                // избавление от ID , создание векторов и матриц,
                return ((nameF == F.ID)||(nameF==-1)) ? substituteArg[0]
                        : new F(nameF, substituteArg);
        }
    }

            /**
             *     Создание  единичной или нулевой матрицы, вектора, тензора \O или \I
             * @param name
             * @return tensor or null
             */
                 Element makeZeroTensor(String name, List<Element> expr){
                    if(name.charAt(0)=='\\'){ char second = name.charAt(1);
                     if(((second=='I') || (second=='O'))&& name.charAt(2)=='_' && name.charAt(3)=='{') {
                         int last = name.indexOf('}');
                       if(last>3){ String indS=name.substring(4, last);
                        F ff=Parser.getF("["+indS+"]", RING);
                        VectorS vI= (VectorS) getIntegerValue(new VectorS(ff.X),expr);
                        tensor:if (vI.V.length>0){
                                    int[] intInd=new int[vI.V.length];
                                    for (int j = 0; j <vI.V.length; j++) { //if( vI.V[j].isZero()) return null;
                                        intInd[j]=vI.V[j].intValue(); 
                                        if(intInd[j]<1) break tensor;
                                    }
                                    int indexI=vI.V[0].intValue();
                                   if(vI.V.length==2){ int indexJ=vI.V[1].intValue();
                                    return (second=='I')?
                                       new MatrixD(MatrixD.oneMatrixD(indexI, indexJ, RING),0):
                                       new MatrixD(MatrixD.zeroMatrixD(indexI, indexJ, RING), 0);
                                   }else if  (vI.V.length==1) {
                                      VectorS res=new VectorS(indexI,RING.numberZERO);
                                      if(second=='I') res.V[0]= RING.numberONE;
                                      return new VectorS(res, 0);
                                  }
                                }
                       }
                     }
                    }return null;
                 }
    
    
    
    private Element conectPolynomsMUL(Element[] Args) {
        Element res = RING.numberONE;
        ArrayList<Element> Fargs = new ArrayList<Element>();
        for (int i = 0; i < Args.length; i++) {
            if (Args[i].numbElementType() <= Ring.Polynom) {
                res = res.multiply(Args[i], RING);
            } else {
                Fargs.add(Args[i]);
            }
        }
        if (!res.isOne(RING)) {
            Fargs.add(0, res);
        }
        return Fargs.isEmpty() ? RING.numberONE : Fargs.size() == 1 ? Fargs.get(0) : new F(F.MULTIPLY, Fargs.toArray(new Element[Fargs.size()]));
    }

    private Element conectPolynomsADD(Element[] Args) {
        Element res = null;
        ArrayList<Element> Fargs = new ArrayList<Element>();
        for (int i = 0; i < Args.length; i++) {
            if (Args[i].numbElementType() <= Ring.Polynom) { 
                res =(res==null)?Args[i]:Args[i].add(res, RING);} 
            else                 Fargs.add(Args[i]);        
        }
        if (!(res==null)) {Fargs.add(res);}
        return Fargs.isEmpty() ? RING.numberZERO : Fargs.size() == 1 
                               ? Fargs.get(0) : new F(F.ADD, Fargs.toArray(new Element[Fargs.size()]));
    }






   /**
    * Вспомогательная процедура говорящая о наличии в умножении некоммутативных элементов
    * @param Arg - массив элементов
    * @return true | false
    */

    /**
     * Вспомогательная процедура говорящая о наличии в умножении некоммутативных элементов
     * @param Arg - массив элементов
     * @return true | false
     */
    private boolean search_NC_elements(Element[] Arg) {
        for (int i = 0; i < Arg.length; i++) {
            switch (Arg[i].numbElementType()) {
                case Ring.MatrixD:
                case Ring.MatrixS:
                case Ring.VectorS:
                    return true;
                case Ring.F:
                    if (((F) Arg[i]).name == F.MULTIPLY) {
                        if (search_NC_elements(((F) Arg[i]).X)) {
                            return true;
                        } else {
                            continue;
                        }
                    }
                    if (((F) Arg[i]).name == F.MULTIPLY_NC) {
                        return true;
                    }
                    continue;
                case Ring.Fname:
                    if (((Fname) Arg[i]).name.indexOf("\\") == 0 && Character.isUpperCase(((Fname) Arg[i]).name.charAt(1))) {
                        return true;
                    }
                default:
            }
        }
        return false;
    }


    /**
     * Поднимаем вложенные целочисленные степени типа (f(x)^6)^8;
     *
     * @param p
     *
     * @return
     */
    private Element expand_pow(F p) {
        if (p.name == F.intPOW) {
            if ((p.X[0] instanceof F) && (((F) p.X[0]).name == F.intPOW)) {
                return new F(F.intPOW, ((F) p.X[0]).X[0], ((F) p.X[0]).X[1].multiply(p.X[1], RING));
            }
        }
        return p;
    }
    /**
     * General simplification of pow, intPow and exp in some power
     * @param ARGS =[ Base=Основание, Power=Степень]
     * @param simplify (if true - do general symplification; if false do easy as powFSP)
     * @return simplified function
     */
    public F powFSP(Element[] ARGS, boolean sqrtInTheTower, boolean simplify){
         if  (ARGS[1].isZero(RING)) {return new F(F.ID,  NumberZ.ONE); }
                if(simplify){
                    
                    // System.out.println("PPPPPPPOOOO WWWWWWW=simplifyARGS[1]="  +ARGS[1]+" "+ ARGS[0] );
          if (ARGS[0] instanceof F){ F f=(F)ARGS[0];Element powN=RING.numberONE;
            if ((f.name==F.POW)||(f.name==F.intPOW)||(f.name==F.SQRT)||(f.name==F.SQRT) ) {
                if  (f.name==F.intPOW){ powN=ARGS[1].multiply(f.X[1], RING);}
                else if (f.name==F.POW) { powN=ARGS[1].multiply(f.X[1], RING); 
                   Element ee1=ARGS[1].multiply(RING.posConst[2],RING);
                   Element ee2=ee1.multiply(RING.posConst[2],RING);  
                   if ((!ee1.isEven())&&(ee2.isEven()))sqrtInTheTower=true;}
                else if (f.name==F.SQRT)  {powN=ARGS[1].divide(NumberZ.TWO,RING);sqrtInTheTower=true;}
                else if (f.name==F.CUBRT)   powN=ARGS[1].divide(RING.posConst[3],RING);   
                return powFSP(new Element[]{ f.X[0], powN}, sqrtInTheTower, simplify);
            }else if (f.name==F.ABS)  {return powFSP(new Element[]{new F(F.ABS, workSWG(f.X[0])),ARGS[1]} ); }
             else if (f.name==F.EXP)  {powN=ARGS[1].multiply(f.X[0], RING);
              if (powN.isZero(RING)) return new F(F.ID,  NumberZ.ONE); 
              if (powN.isOne(RING))  return new F(F.ID,  new Fname("\\e")); 
              return new F(F.EXP, powN );
             }
            // if (f.name==F.ABS) return powFSP(new Element[]{ f.X[0], powN}, true, simplify);
          }else   // end of F
              if ((ARGS[0] instanceof Fname)&&(((Fname)ARGS[0]).name.equals("\\e"))){
              return new F(F.EXP, ARGS[1]);
          }
        } // if(sqrtInTheTower&&(!ARGS[1].isEven())&&(!RING.isComplex())) 
         //   System.out.println("Tower2 "+ARGS[0] +"  "+ARGS[1] ); 
     //   else  // System.out.println("Tower1 " +ARGS[0] +"  "+ARGS[1] );
   //    System.out.println("CanonicForms.ListOfChanges="+ Array.toString( List_of_Change.toArray()));            
         return (sqrtInTheTower&&(!ARGS[1].isEven())&&(!RING.isComplex()))? 
          powFSP(new Element[]{new F(F.ABS,ARGS[0]),ARGS[1]})
                 : powFSP(ARGS);
    }
    /**
     * Обрабатываем узел с именем POW , меняем на intPOW если степнь целая
     *
     * @param el
     *
     * @return
     */
    public F powFSP(Element[] ARGS) {  
         if (ARGS[0].isZero(RING)) {return new F(F.ID,  RING.numberZERO);}
         if (ARGS[1].isZero(RING)) {return new F(F.ID,  RING.numberONE); }
         if (ARGS[1].isOne(RING)) {return (ARGS[0] instanceof F)? (F)ARGS[0] :  new F(F.ID,  ARGS[0]);}
         if ((ARGS[1]).isItNumber()) {
            switch (ARGS[1].numbElementType()) {
                case Ring.Z: case Ring.Zp: case Ring.Z64: case Ring.Zp32:
                    return new F(F.intPOW, new Element[] {ARGS[0], ARGS[1]});
                case Ring.R:
                    return (((NumberR) ARGS[1]).scale() == 0)
                            ? new F(F.intPOW, new Element[] {ARGS[0], ARGS[1]})
                            : new F(F.POW, ARGS);
                case Ring.R64:
                    double nR64 = ARGS[1].doubleValue();
                    return ((nR64 - Math.round(nR64)) == 0) ? new F(F.intPOW, new Element[] {ARGS[0], ARGS[1]}) : new F(F.POW, ARGS);
                case Ring.CQ: case Ring.C:  case Ring.C64:
                case Ring.C128: case Ring.CZ: case Ring.CZ64:
                case Ring.CZp:  case Ring.CZp32:
                      Element ImPart=ARGS[1].Im(RING);
                      if(ImPart.isZero(RING)){
                        double nC64 =ARGS[1].Re(RING).doubleValue();
                        return ((nC64 - Math.round(nC64)) == 0) ? new F(F.intPOW, new Element[] {
                                    ARGS[0], ARGS[1]}) : new F(F.POW, ARGS);
                      }
                      case Ring.F:  case Ring.Fname: 
                      Element fr = new Fraction(ARGS[1], RING);
                   return new F(F.POW, ARGS[0], fr);
                          
            }
        }
        return new F(F.POW, ARGS);
    }

    /**
     * Обрабатываем узел с именем DIVIDE, а именно : 1)проверяется возможность
     * деления 2)возвращается числитель если знаменатель единица
     *
     * @param el
     *
     * @return
     */
    private Element divideFSP(Element[] ARGS) {
        int type1 = ARGS[1].numbElementType();
        if (((type1 <= Ring.R64) && (type1 >= Ring.R)) || ((type1 <= Ring.C64) && (type1
                >= Ring.C))) {
            Element zn = RING.numberONE().divide(ARGS[1], RING);
            return ARGS[0].multiply(zn, RING);
        }
        return new F(F.DIVIDE, ARGS);
    }

    /**
     * Метод конвертирующий элемент f в полином в степени Pow с учетом (F->P)
     * поля List_of_Change.
     *
     * @param El - елемент который надо конвертировать в полином
     * @param Pow - его будущая степень
     *
     * @return f в виде полинома в степени Pow
     */
    public Element workToDefault(Element El, int Pow) {Polynom temp_=null;
        int pos = List_of_Change.lastIndexOf(El); // ищем такой же
        if (pos >= 0) { //если нашли ,то берем с тем же индексом из List_of_Polynom            
            if(index_in_Ring<RING.varNames.length+pos+2)index_in_Ring=RING.varNames.length+pos+2;
            return ((Polynom)List_of_Polynom.get(pos).clone()).pow(Pow, RING);
        } else {  // Если Fname указывал на полином, то вернем этот полином GM 15.10.2014
            if(El instanceof Fname){
            Element[] argEl=((Fname)El).X;
               if((argEl!=null)&&(argEl[0]!=null)){
                   if (argEl[0].numbElementType()==Ring.Polynom)
                     temp_=(Polynom)argEl[0];
                  else if (argEl[0].numbElementType()<Ring.Polynom)
                     temp_= new Polynom(argEl[0]); // чисел у нас нет???  - только полиномы или фунrции??
               }}
            if(temp_==null){ // Если Fname не указывал на полином, то заводим новую переменную
                // если нет создаем новый
                List_of_Change.add(El);  
                temp_ = makePolynom(index_in_Ring, Pow);
                List_of_Polynom.add(polynomWithOnePow(temp_));
                index_in_Ring=List_of_Change.size()+RING.varNames.length+1;
            }
        }return temp_;
    }
//========================================================================================
    //   Блок для конвертации любого элемента в полином и обратно
//========================================================================================

 

    public Element myOpDiv(Element a, Element b) {
        if (a instanceof Fraction) {
            if (b instanceof Fraction) {
                return a.divide(b, RING);
            } else {
                return new Fraction(((Fraction) a).num, ((Fraction) a).denom.multiply(b, RING)).cancel(RING);
            }
        } else {
            if (b instanceof Fraction) {
                return new Fraction(((Fraction) b).denom.multiply(a, RING), ((Fraction) b).num).cancel(RING);
            } else {
                return new Fraction(a, b).cancel(RING);
            }
        }
    }
    // умножаем два любых элемента

    public Element myOpMul(Element a, Element b) {
        if (a instanceof Fraction) {
            if (b instanceof Fraction) {
                return a.multiply(b, RING);
            } else {
                return new Fraction(((Fraction) a).num.multiply(b, RING), ((Fraction) a).denom).cancel(RING);
            }
        } else {
            if (b instanceof Fraction) {
                return new Fraction(((Fraction) b).num.multiply(a, RING), ((Fraction) b).denom).cancel(RING);
            } else {
                return a.multiply(b, RING);
            }
        }
    }
    // вычитаем два любых элемента

    public Element myOpSub(Element a, Element b) {
        if (a instanceof Fraction) {
            if (b instanceof Fraction) {
                return a.subtract(b, RING);
            } else {
                return new Fraction(((Fraction) a).num.subtract(((Fraction) a).denom.multiply(b, RING), RING), ((Fraction) a).denom).cancel(RING);
            }
        } else {
            if (b instanceof Fraction) {
                return new Fraction(((Fraction) b).denom.multiply(a, RING).subtract(((Fraction) b).num, RING), ((Fraction) b).denom).cancel(RING);
            } else {
                return a.subtract(b, RING);
            }
        }
    }
    // складываем два любых элемента

    public Element myOpAdd(Element a, Element b) {
        if (a instanceof Fraction) {
            if (b instanceof Fraction) {
                return a.add(b, RING);
            } else {
                return new Fraction(((Fraction) a).num.add(((Fraction) a).denom.multiply(b, RING), RING), ((Fraction) a).denom).cancel(RING);
            }
        } else {
            if (b instanceof Fraction) {
                return new Fraction(((Fraction) b).num.add(((Fraction) b).denom.multiply(a, RING), RING), ((Fraction) b).denom).cancel(RING);
            } else {
                return a.add(b, RING);
            }
        }
    }

    private Element intpow_e2pW(Element[] ARGS) {
        int Pow = ARGS[1].intValue();
        return oneArg_e2pW(ARGS[0]).pow(Pow, RING);
    }

    private Element subtract_e2pW(Element[] ARGS) {
        return myOpSub(oneArg_e2pW(ARGS[0]), oneArg_e2pW(ARGS[1]));
    }

    private Element divide_e2pW(Element[] ARGS) {
        return myOpDiv(oneArg_e2pW(ARGS[0]), oneArg_e2pW(ARGS[1]));
    }

    private Element add_e2pW(Element[] ARGS) {
        Element Res = oneArg_e2pW(ARGS[0]);
        for (int i = 1; i < ARGS.length; i++) {
            Res = myOpAdd(Res, oneArg_e2pW(ARGS[i]));
        }
        return Res;
    }

    private Element multiply_e2pW(Element[] ARGS) {
        Element Res = oneArg_e2pW(ARGS[0]);
        for (int i = 1; i < ARGS.length; i++) {
            Res = myOpMul(Res, oneArg_e2pW(ARGS[i]));
        }
        return Res;
    }

    private Element oneArg_e2pW(Element El) {
        if (El instanceof F) {
            switch (((F) El).name) {
                case F.ID:
                    return oneArg_e2pW(((F) El).X[0]);
                case F.ADD:
                    return add_e2pW(((F) El).X);
                case F.MULTIPLY:
                    return multiply_e2pW(((F) El).X);
                case F.SUBTRACT:
                    return subtract_e2pW(((F) El).X);
                case F.DIVIDE:
                    return divide_e2pW(((F) El).X);
                case F.intPOW:
                    return intpow_e2pW(((F) El).X);
                default:
                    return workToDefault(El, 1);
            }
        } else {
            if (El instanceof Fraction) {
                return divide_e2pW(new Element[] {((Fraction) El).num,
                            ((Fraction) El).denom});
            }
            switch (El.numbElementType()) {
                case Ring.Fname:
                    return workToDefault(El, 1);
                case Ring.Polynom:
                    return El;
                case Ring.VectorS:
                    return Vec2PolW((VectorS) El);
                case Ring.MatrixS:
                case Ring.MatrixD:
                    return matrixF2PW(El);
                default:
                    return El;
            }
        }
    }

    public Element matrixF2PW(Element matr) {
        if (matr instanceof MatrixS) {
            Element[][] dm = ((MatrixS) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < dm.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    Element el = CleanElement(dm[i][j]);
                    newM[i][j] = oneArg_e2pW(el);
                }
            }
            return new MatrixS(newM, ((MatrixS) matr).col);
        } else {
            Element[][] dm = ((MatrixD) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < dm.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    Element el = CleanElement(dm[i][j]);
                    newM[i][j] = oneArg_e2pW(el);
                }
            }
            return new MatrixD(newM,1); // REALLY WE DO NOT KNOW IS IT CHANGES OR NOT !!!! need more cleaver
        }
    }

    private Element Vec2PolW(VectorS vec) {
        Element[] newVec = new Element[vec.V.length];
        Element el;
        for (int i = 0; i < vec.V.length; i++) {
            el = CleanElement(vec.V[i]);
            newVec[i] = oneArg_e2pW(el);
        }
        return new VectorS(newVec);
    }


    /**
     * Метод конвертирующий любой элемент в полином (если на входе матрица или
     * вектор конвертируются их элементы)
     *
     * @param el
     *
     * @return
     */
    public Element ElementConvertToPolynom(Element el) {
        int numOldVar = newRing.varNames.length;//  index_in_Ring;
        Element cleanEl = CleanElement(el);
        switch (el.numbElementType()) {
            case Ring.F:
                Element r;
                switch (((F) cleanEl).name) {
                    case F.ID:
                        r = oneArg_e2pW(((F) cleanEl).X[0]);
                        break;
                    case F.ADD:
                        r = add_e2pW(((F) cleanEl).X);
                        break;
                    case F.MULTIPLY:
                        r = multiply_e2pW(((F) cleanEl).X);
                        break;
                    case F.SUBTRACT:
                        r = subtract_e2pW(((F) cleanEl).X);
                        break;
                    case F.DIVIDE:
                        r = divide_e2pW(((F) cleanEl).X);
                        break;
                    case F.intPOW:
                        r = intpow_e2pW(((F) cleanEl).X);
                        break;
                    default:
                        r = workToDefault(cleanEl, 1);
                }
                    makeWorkRing(index_in_Ring - numOldVar-1);                  
                  //  makeNewVariablesInNewRing(colNewVar);             
                return r;
            case Ring.MatrixD:
            case Ring.MatrixS:
                Element resM = matrixF2PW(cleanEl);
                {makeWorkRing(index_in_Ring - numOldVar-1);
                   // makeNewVariablesInNewRing(colNewVarM);
                }
                return resM;

            case Ring.VectorS:
                Element resV = Vec2PolW((VectorS) cleanEl);
                {makeWorkRing(index_in_Ring - numOldVar-1);
                   // makeNewVariablesInNewRing(colNewVarV);
                }
                return resV;

            case Ring.Fname:
                Element fnm2Pol = workToDefault(cleanEl, 1);
                 {makeWorkRing(index_in_Ring - numOldVar-1);
                }
                return fnm2Pol;

            case Ring.Rational:
            case Ring.Q:
                Element resRQ = divide_e2pW(new Element[] {((Fraction) cleanEl).num,
                            ((Fraction) cleanEl).denom});
                    {makeWorkRing(index_in_Ring - numOldVar-1);
                   // makeNewVariablesInNewRing(colNewVarRQ);
                }
                return resRQ;

            default:
                return cleanEl;
        }
    }

    /**
     * Процедура конвертирующая полином в F с учетом List_of_Cange и
     * List_of_Polynom
     *
     * @param p - входной полином
     *
     * @return
     */
    public Element Unconvert_Polynom_to_Element(Element el) {
        if (el instanceof Polynom) {
            Polynom p = (Polynom) el;
            if (p.isItNumber()) {
                      return (p.isZero(RING)) ? RING.numberZERO : p.coeffs[0]; }
            int numRealPolynoms = index_var_for_Integration;// кол-во реальных полиномов
            if (p.powers.length / p.coeffs.length <= numRealPolynoms) {return p;}
            Polynom[] masPol = dividePolynomToMonoms(p); // разбиваем полином на мономы
            Vector<Element> vec = new Vector<Element>();
            Polynom Add = new Polynom(new int[] {}, new Element[] {});// сдесь будет накапливаться сумма обычных
            for (Polynom pol : masPol) { // пробегаем по мономам и конвертируем их
                if (pol.powers.length < numRealPolynoms) Add = Add.add(pol, RING);
                else {vec.add(monom_to_F(pol)); changeDone=true; }    // !!!!!!  GIM 13-12-13  changeDone
            }
            if (!Add.isZero(RING)) {
                if (Add.isItNumber()) { vec.add(0, Add.coeffs[0]);
                } else {vec.add(0, Add);  }
            }
            return  makeSUBTRACT(vec.toArray(new Element[vec.size()]) );// возвращаем результат
        } else {
            if (el instanceof Fraction) {
                return new Fraction(Unconvert_Polynom_to_Element(((Fraction) el).num), Unconvert_Polynom_to_Element(((Fraction) el).denom));
            }
        }return el;
    }

    public Element matP2F(Element matr) {
        if (matr instanceof MatrixS) {
            Element[][] dm = ((MatrixS) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < newM.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    newM[i][j] = Unconvert_Polynom_to_Element(dm[i][j]);
                }
            }
            return new MatrixS(newM, ((MatrixS) matr).col);
        } else {
            Element[][] dm = ((MatrixD) matr).M;
            Element[][] newM = new Element[dm.length][];
            for (int i = 0; i < newM.length; i++) {
                newM[i] = new Element[dm[i].length];
                for (int j = 0; j < newM[i].length; j++) {
                    newM[i][j] = Unconvert_Polynom_to_Element(dm[i][j]);
                }
            }
            return new MatrixD(newM,0);
        }
    }

    public Element vecF2P(VectorS vec) {
        Element[] newV = new Element[vec.V.length];
        for (int j = 0; j < vec.V.length; j++) {
            newV[j] = Unconvert_Polynom_to_Element(vec.V[j]);
        } int ffl=(vec.fl==-2)?-1:(vec.fl==1)?0:vec.fl;
        return new VectorS(newV,ffl );
    }

    private Element hardStructUnconvert(F f) {
        Element[] newArg = new Element[f.X.length];
        for (int i = 0; i < f.X.length; i++) {
            newArg[i] = (f.X[i] instanceof F) ? hardStructUnconvert((F) f.X[i]) : UnconvertToElement(f.X[i]);
        }
        return new F(f.name, newArg);
    }

    /**
     * Универсальный метод обратной перегонки обращенных в полиномы элементов
     * Существует возможность перегонки полинов находящихся внутри дерева
     * функции.
     *
     * @param el
     *
     * @return
     */
    public Element UnconvertToElement(Element el) {
        switch (el.numbElementType()) {
            case Ring.F:
                return hardStructUnconvert((F) el);
            case Ring.MatrixD:
                return (((MatrixD) el).fl == 0) ? el : unconvertMSF((MatrixD) el);
            case Ring.VectorS:
                return (((VectorS) el).fl == 0) ? el : unconvertVSF((VectorS) el);

            case Ring.MatrixS:
//            case Ring.MatrixD:
//                return matP2F(el);
//
//            case Ring.VectorS:
//                return vecF2P((VectorS) el);

            case Ring.Polynom:
                return Unconvert_Polynom_to_Element(el);

            case Ring.Rational:
            case Ring.Q:
                Fraction uEl = (Fraction) el;
                if (uEl.denom.isOne(RING)) {
                    return UnconvertToElement(uEl.num);
                }
                return new Fraction(UnconvertToElement(uEl.num), UnconvertToElement(uEl.denom));

            default:
                return el;
        }
    }

//==============================================================================================
//========================== Перевод в полином =====================================================
//==============================================================================================
    /**
     * Метод переводящий любой элемент в полином или полиномиальную
     * дробь(Fraction) Если на входе матрица или вектор, то будут преобразованы
     * каждые из элементов. Таблица замен в полях List_of_Change,
     * List_of_Polynom представителя класса CanonicForms, где каждому полиному
     * из списка List_of_Polynom ставиться в том же порядке элемент из
     * List_of_Change, новое кольцо в newRing;
     *
     * @param el - сам элемент
     * @param makeVec - делать ли вектора для cleanOfRepeating ?
     *
     * @return
     */
    public Element ElementToPolynom(Element el, boolean makeVec) {
        if (makeVec) {
           // index_in_Ring = RING.varNames.length + 1;
            makeNewVectFandVectEL();
        }
        switch (el.numbElementType()) {
            case Ring.F:
                return F2Pol((F) el);
            case Ring.Polynom:
                return el;
            case Ring.VectorS:
                return VEC2Pol((VectorS) el);
            case Ring.Fname: makeWorkRing(1); //2015
                return workToDefault(el, 1);
            case Ring.MatrixS:
            case Ring.MatrixD:
                return MATRIX2Pol(el);
            // считаем что далее к нам может прийти только число
            default:
                return el;
        }
    }
//---------------------------------- обертка фукции в полином ------------------------------

    private Element intpow_e2pWork(Element[] ARGS) {
        int Pow = ARGS[1].intValue();
        if (Pow>0)return oneArg_e2pWork(ARGS[0]).pow(Pow, RING);
        else if(Pow<0)return new Fraction(NumberZ.ONE, oneArg_e2pWork(ARGS[0]).pow(-Pow, RING));
        return NumberZ.ONE;
    }

    private Element subtract_e2pWork(Element[] ARGS) {
        return myOpSub(oneArg_e2pWork(ARGS[0]), oneArg_e2pWork(ARGS[1]));
    }

    private Element divide_e2pWork(Element[] ARGS) {
        return myOpDiv(oneArg_e2pWork(ARGS[0]), oneArg_e2pWork(ARGS[1]));
    }

    private Element add_e2pWork(Element[] ARGS) {
        Element Res = oneArg_e2pWork(ARGS[0]);
        for (int i = 1; i < ARGS.length; i++) {
            Res = myOpAdd(Res, oneArg_e2pWork(ARGS[i]));
        }
        return Res;
    }

    private Element multiply_e2pWork(Element[] ARGS) {
        Element Res = oneArg_e2pWork(ARGS[0]);
        for (int i = 1; i < ARGS.length; i++) {
            Res = myOpMul(Res, oneArg_e2pWork(ARGS[i]));
        }
        return Res;
    }

    private Element oneArg_e2pWork(Element El) {
        if (El instanceof F) {F Fel=(F) El;
            switch (((F) El).name) {
                case F.ID:
                    return oneArg_e2pWork(Fel.X[0]);
                case F.ADD:
                    return add_e2pWork(Fel.X);
                case F.MULTIPLY:
                    return multiply_e2pWork(Fel.X);
                case F.SUBTRACT:
                    return subtract_e2pWork(Fel.X);
                case F.DIVIDE:
                    return divide_e2pWork(Fel.X);
                case F.intPOW:
                    return intpow_e2pWork(Fel.X);
                default:
                    return workToDefault(El, 1);
             }
         } else {           
            if (El instanceof Fraction) {
                return divide_e2pWork(new Element[] {((Fraction) El).num,
                            ((Fraction) El).denom});
            }
            switch (El.numbElementType()) {
                case Ring.Fname:
                    return workToDefault(El, 1);
                case Ring.VectorS:
                    return workToDefault(VEC2Pol((VectorS) El), 1);
                case Ring.MatrixS:
                case Ring.MatrixD:
                    return workToDefault(MATRIX2Pol(El), 1);
                default:
                    return El;
            }
        }
    }
    /**
     * Метод конвертирующий элемент el в полином в степени Pow с учетом (F->P)
     * поля List_of_Change.
     * Здесь же происходит увеличение кольца newRing, если это требуется,
     * т.е. когда элемент el -- новый.
     * @param el  -- любой элемент
     * @param pow -- целая положительная степень
     * @return полином со степенью pow, в который обратился элемент el
     */
    public Element addNewElement(Element el, int pow) {
        int oldIndex = index_in_Ring;
        Element res = workToDefault(CleanElement(el), pow);
        makeWorkRing(index_in_Ring - oldIndex); //
        return res;
    }

    private Element El2Pol(Element el) {
        switch (el.numbElementType()) {
            case Ring.F: {
                switch (((F) el).name) {
                    case F.ID:
                        return El2Pol(((F) el).X[0]);
                    case F.ADD:
                        return add_e2pWork(((F) el).X);
                    case F.MULTIPLY:
                        return multiply_e2pWork(((F) el).X);
                    case F.SUBTRACT:
                        return subtract_e2pWork(((F) el).X);
                    case F.DIVIDE:
                        return divide_e2pWork(((F) el).X);
                    case F.intPOW:
                        return intpow_e2pWork(((F) el).X);
                    default:
                        return workToDefault(el, 1);
                }
            }
            case Ring.Fname:
                return workToDefault(el, 1);
            case Ring.MatrixS:
            case Ring.MatrixD:
                return MATRIX2Pol(el);
            case Ring.VectorS:
                return VEC2Pol((VectorS) el);
            default:
                return el;
        } 
    }

    private Element F2Pol(F f) {
        Element newf = El2Pol(CleanElement(f));
              makeWorkRing(index_in_Ring - RING.varNames.length-1);
        return newf;
    }

    private Element VEC2Pol(VectorS vec) {
        Element[] newVec = new Element[vec.V.length];
        Element el;
        for (int i = 0; i < vec.V.length; i++) {
            el = CleanElement(vec.V[i]);
            newVec[i] = El2Pol(el);
        }
        makeWorkRing(index_in_Ring - RING.varNames.length-1);//2015
        return new VectorS(newVec, (vec.fl<0)?-2:1 ); // NOT VERY CLEAVER !!!
    }

    private Element MATRIX2Pol(Element MM) {
        boolean fl = true;
        Element[][] M = null;
        if (MM instanceof MatrixD) {
            fl = false;
            M = ((MatrixD) MM).M;
        } else if (MM instanceof MatrixS) {
            M = ((MatrixS) MM).M;
        }
        Element[][] newM = new Element[M.length][];
        Element el;
        for (int i = 0; i < M.length; i++) {
            newM[i] = new Element[M[i].length];
            for (int j = 0; j < M[i].length; j++) {
                el = CleanElement(M[i][j]);
                newM[i][j] = El2Pol(el);
            }
        }
        makeWorkRing(index_in_Ring - RING.varNames.length-1);//2015
        return (fl) ? new MatrixS(newM, ((MatrixS) MM).col) : new MatrixD(newM,1); // !!! the same
    }

    public Element convert_Element(Element el) {
        switch (el.numbElementType()) {
            case Ring.Fname:
                if (Character.isUpperCase(((Fname) el).name.charAt(0))) {
                    G_ListChange.add(el);
                    index_in_G_Ring++;
                    G_List_Names.add(((Fname) el).name);
                    return el;
                  // ?????????????????????? ГМ  это что ??
                } else {
                    return workToDefault(el, 1);
                }
            case Ring.VectorS:
            case Ring.MatrixS:
            case Ring.MatrixD:
                G_ListChange.add(el);
                index_in_G_Ring++;
                G_List_Names.add("V$_" + index_in_G_Ring);
                return new Fname("V$_" + index_in_G_Ring);
            default:
                return el;
        }
    }

    private void makeG_Ring() {
        if (!G_List_Names.isEmpty()) {
            StringBuilder gRing = new StringBuilder("G[");
            for (int i = 0; i < G_List_Names.size() - 1; i++) {
                gRing.append(G_List_Names.get(i)).append(",");
            }
            gRing.append(G_List_Names.get(G_List_Names.size() - 1)).append("]");
            newRing =
                    new Ring(new StringBuilder(newRing.toString()).append(gRing).toString());
        }
    }

    public Algebra AlgebraConvertor(F func) {
        index_in_Ring = RING.varNames.length + 1;
        makeNewVectFandVectEL();
        Algebra last_res = el_to_Algebra(Fname_Convert(func));
        makeWorkRing(index_in_Ring - RING.varNames.length-1);//2015
        makeG_Ring();
        return last_res;
    }

    public Element Fname_Convert(F func) {
        Element[] convertArg = new Element[func.X.length];
        for (int i = 0; i < func.X.length; i++) {
            switch (func.X[i].numbElementType()) {
                // пробегаем по дереву в глубь
                case Ring.F:
                    convertArg[i] = Fname_Convert((F) func.X[i]);
                    break;
                // сначало оборачиваем каждый элемент матрицы или вектора, а затем переводим его в Fname
                case Ring.VectorS:
                    convertArg[i] =
                            convert_Element(VEC2Pol((VectorS) func.X[i]));
                    break;
                case Ring.MatrixS:
                case Ring.MatrixD:
                    convertArg[i] =
                            convert_Element(MATRIX2Pol(func.X[i]));
                    break;
                // смотрим какой Fname пришел
                case Ring.Fname:
                    convertArg[i] = convert_Element(func.X[i]);
                    break;
                default:
                    convertArg[i] = func.X[i];
            }
        }
        switch (func.name) {
            case F.ADD:
            case F.SUBTRACT:
            case F.DIVIDE:
            case F.MULTIPLY:
            case F.MULTIPLY_NC:
                return new F(func.name, convertArg);
            case F.intPOW:
                return (convertArg[0] instanceof Polynom)
                        ? ((Polynom) convertArg[0]).pow(func.X[1].intValue(), RING)
                        : new F(F.intPOW, convertArg);
            default:
                Element temp_f =
                        CleanElement(new F(func.name, convertArg));
                return workToDefault(temp_f, 1);
        }
    }

// сюда будет приходить куски дерева после всех преобразований
// если пришел Fname то это бывший не коммутативный элемент
    private Algebra el_to_Algebra(Element el) {
        ArrayList<Element> coeff = new ArrayList<Element>();
        ArrayList<Gterm> term = new ArrayList<Gterm>();
        switch (el.numbElementType()) {
            case Ring.F: { // может только прийти -,+,*,^
                switch (((F) el).name) {
                    case F.ADD:
                        Algebra res = el_to_Algebra(((F) el).X[0]);
                        for (int i = 1; i < ((F) el).X.length; i++) {
                            res = res.add(el_to_Algebra(((F) el).X[i]), RING);
                        }
                        return res;
                    case F.SUBTRACT:
                        return el_to_Algebra(((F) el).X[0]).subtract(el_to_Algebra(((F) el).X[1]), RING);
                    //---------------------------------------------------------------------------------------------
                    // расчитано исключительно на X^6  но не на (X+Y)^6
                    //---------------------------------------------------------------------------------------------
                    case F.intPOW:// надо наверное степень перехватывать выше и все менять на F !!!!!!
                        Algebra pow_alg = el_to_Algebra(((F) el).X[0]);
//                        Gterm newGterm = pow_alg.term.get(0);
//                        newGterm.T[0] = ((F) el).X[1].intValue();
//                        newGterm.T[newGterm.T.length - 1] = ((F) el).X[1].intValue();
//                        term.add(newGterm);
//                        return new Algebra(pow_alg.coeff, term);
                        return (Algebra) pow_alg.pow(((F) el).X[1].intValue(), RING);
                    //-----------------------------------------------------------------------------------------------
                    case F.MULTIPLY_NC:
                    case F.MULTIPLY:
                        Algebra res_m = el_to_Algebra(((F) el).X[0]);
                        for (int i = 1; i < ((F) el).X.length; i++) {
                            res_m =
                                    res_m.multiply(el_to_Algebra(((F) el).X[i]), RING);
                        }
                        return res_m;
                    default:
                        RING.exception.append(el.toString(RING)).append(" can not be converted !!!!");
                        return null;
                }
            }
            case Ring.Fname: // это не коммутативный элемент
                coeff.add(RING.numberONE());
                term.add(new Gterm(new int[] {1,
                            G_List_Names.indexOf(((Fname) el).name),
                            1}));
                return new Algebra(coeff, term);
            case Ring.G: // если то что пришло уже есть Algebra
                return (Algebra) el;
            default: // здесь полиномы и другая числовая нечисть )))
                coeff.add(el);
                term.add(new Gterm(new int[0]));
                return new Algebra(coeff, term);
        }
    }
    //----------------------  Обратное отображение  ---------------------------

    private Element fullConvertNCEl(Element el) {
        if(el instanceof F){ // немного уличной магии )
         return new F(((F)el).name,new Element[]{fullConvertNCEl(((F)el).X[0]),((F)el).X[1]});
        }
        if (el instanceof VectorS) {
            return (((VectorS) el).fl != 0) ? unconvertVSF(((VectorS) el)) : el;
//
//            if(((VectorS) el).fl==0) return el;
//            Element[] newVec = new Element[((VectorS) el).V.length];
//            for (int i = 0; i < ((VectorS) el).V.length; i++) {
//                newVec[i] = Convert_Polynom_to_F(((VectorS) el).V[i]);
//            }
//            return new VectorS(new VectorS(newVec),1);
        } else {
            if (el instanceof Fname) {return el; // !!! ой ли?
            } else {
                if (el instanceof MatrixD) {
                    return (((MatrixD) el).fl != 0) ? unconvertMSF(((MatrixD) el)) : el;
//                  if(((MatrixSF) el).fl==0) return el;
//                    Element[][] newM = new Element[((MatrixSF) el).M.length][];
//                    for (int i = 0; i < ((MatrixSF) el).M.length; i++) {
//                        newM[i] = new Element[((MatrixSF) el).M[i].length];
//                        for (int j = 0; j < ((MatrixSF) el).M[i].length; j++) {
//                            newM[i][j] =
//                                    Convert_Polynom_to_F(((MatrixSF) el).M[i][j]);
//                        }
//                    }
//                    return new MatrixSF(new MatrixS(newM, ((MatrixSF) el).col),1);
                } 
//                else {
//                    Element[][] newM = new Element[((MatrixD) el).M.length][];
//                    for (int i = 0; i < ((MatrixD) el).M.length; i++) {
//                        newM[i] = new Element[((MatrixD) el).M[i].length];
//                        for (int j = 0; j < ((MatrixD) el).M[i].length; j++) {
//                            newM[i][j] =Unconvert_Polynom_to_Element(((MatrixD) el).M[i][j]);
//                        }
//                    }
//                    return new MatrixD(newM);
//                }
            }
        } return el;
    }

    private Element makeNCElement(int var, int pow) {
//        if (reConvertNCElement) {
//            return (pow == 1) ? fullConvertNCEl(G_ListChange.get(var))
//                    : new F(F.intPOW, new Element[] {
//                        fullConvertNCEl(G_ListChange.get(var)), new NumberZ(pow)});
//        } else {
            return (pow == 1) ? G_ListChange.get(var) : new F(F.intPOW, new Element[] {
                        G_ListChange.get(var), new NumberZ(pow)});
     //   }
    }

    private boolean isMatrix_or_Vector(Element el) {
        int type = el.numbElementType();
        return type == Ring.MatrixD | type == Ring.VectorS | type == Ring.VectorS | type == Ring.MatrixS | type == Ring.MatrixD;
    }


    private Element MakeNCMultiply(Element coef, Gterm gt, boolean fullReConvert) {
       if(fullReConvert){
        int len = gt.T.length;
        if (len == 0) {
            return null;
        }
        if (len == 3) {// то есть у нас один элемент
            Element temp=makeNCElement(gt.T[1], gt.T[2]);
            return(isMatrix_or_Vector(temp))? fullConvertNCEl(temp.multiply(coef, RING)) : new F(F.MULTIPLY_NC, new Element[]{ElementToF(coef),temp});
        }
        Element[] resArg = new Element[(len - 1) / 2];
        int index = 0;
        boolean fl=(coef.isOne(RING))? false : true;
        Element temp;
        for (int i = 1; i < len; i += 2) {
            if(fl){
            temp=makeNCElement(gt.T[i], gt.T[i + 1]);
             if(isMatrix_or_Vector(temp)){
             resArg[index]=fullConvertNCEl(temp.multiply(coef, RING));
             index++;fl=false;
             }else{
             resArg[index] = fullConvertNCEl(makeNCElement(gt.T[i], gt.T[i + 1]));
             index++;
             }
            }else{
            resArg[index] = fullConvertNCEl(makeNCElement(gt.T[i], gt.T[i + 1]));
            index++;
            }
        }
        if(fl){
        Element[] newResArg=new Element[resArg.length+1];
        newResArg[0]=ElementToF(coef);
        System.arraycopy(resArg, 0, newResArg, 1, resArg.length);
        return new F(F.MULTIPLY_NC, newResArg);
        }
        return new F(F.MULTIPLY_NC, resArg);
       }else{
       int len = gt.T.length;
        if (len == 0) {
            return null;
        }
        if (len == 3) {// то есть у нас один элемент
            return(coef.isOne(RING)) ? makeNCElement(gt.T[1], gt.T[2]) : new F(F.MULTIPLY_NC, new Element[]{ElementToF(coef),makeNCElement(gt.T[1], gt.T[2])});
        }
        Element[] resArg = new Element[(len - 1) / 2];
        int index = 0;
        for (int i = 1; i < len; i += 2) {
            resArg[index] = makeNCElement(gt.T[i], gt.T[i + 1]);
            index++;
        }
         if(!coef.isOne(RING)){
        Element[] newResArg=new Element[resArg.length+1];
        newResArg[0]=ElementToF(coef);
        System.arraycopy(resArg, 0, newResArg, 1, resArg.length);
        return new F(F.MULTIPLY_NC, newResArg);
        }
        return new F(F.MULTIPLY_NC, resArg);
        }
    }

    public Element oneTermToEl(Element coef, Gterm gt, boolean fullReConvert,
            boolean reConvertPolynom) {
        Element res = MakeNCMultiply(coef,gt, fullReConvert);
        if (reConvertPolynom) {
            return (res == null) ? ElementToF(coef) : res;
        } else {
            return (res == null) ? coef : (coef.isOne(newRing)) ? res : new F(F.MULTIPLY, new Element[] {
                        coef, res});
        }
    }

    /**
     * Метод конвертирующий алгебру в элемент с учетом полей :
     * G_ListChange,G_ListNames,List_of_Change, List_of_Polynom.
     *
     * @param alg - алгебра которую надо конвертировать
     * @param reConvertNCElement - заменять ли элементы матрицы или вектора на
     * их истинные значения
     * @param reConvertPolynom - заменять ли полиномы и элементы Алгебры на их
     * истинные значения
     *
     * @return
     */
    public Element Algebra2Element(Algebra alg, boolean ReConvertNCElement,
            boolean reConvertPolynom) {
        int coffLen = alg.coeff.size();
        if (coffLen == 1) {
            return oneTermToEl(alg.coeff.get(0), alg.term.get(0), ReConvertNCElement, reConvertPolynom);
        }
        Element[] resArg = new Element[coffLen];
        for (int i = 0; i < coffLen; i++) {
            resArg[i] = oneTermToEl(alg.coeff.get(i), alg.term.get(i), ReConvertNCElement, reConvertPolynom);
        }
        return new F(F.ADD, resArg);
    }

    /**
     * Конвертируем функцию типа F в полином если это возможно!!!!
     *
     * @param f
     *
     * @return
     */
    public Polynom PolynomialConvert(F f) {
        try {
            return (Polynom) Algebra2Element(AlgebraConvertor(f), false, false);
        } catch (Exception e) {
            RING.exception.append("\n Function ").append(f.toString()).append(" can not converte into polynom !");
            return null;
        }
    }

//====================================================================================================
    /**
     * Факторизаций выражения, как обычного полинома, после замены всех
     * трансцендентных компонент в новые переменные и применение процедур
     * факторизации для полинома.
     * Состоит из 2х частей. Первая -- это factor = head, данная вторая часть factorBody
     * @param fE -исходная функция факторизация которой производится
     * @return возвращается: [0]функция после конвертации в полином, факторизации и
     *         де-конвертации из полинома; [1] виртуальный фактор-полином как объкт FactorPol
     */
    public Element[] factorBody(Element fE) {  
        if (! ( fE instanceof F ) ){return new Element[] {fE,null};}
        F f=(F)fE;
        if (f.name == F.ID) return factorBody(f.X[0]);
        if (f.name == F.ABS) {Element[] fb= factorBody(f.X[0]); 
             FactorPol fp=(FactorPol)fb[1]; if(fp==null) return new Element[]{ fE,null};
             Element[] ab =new Element[fp.powers.length];
             for (int i = 0; i < ab.length; i++) {
                  ab[i]= new F(F.ABS, UnconvertAllLevels(fp.multin[i]));
                  fp.multin[i]=(Polynom)ElementToPolynom(ab[i],false); 
                  if (fp.powers[i]!=1)ab[i]=new F(F.intPOW,ab[i]);     }
             return new Element[]{new F(F.MULTIPLY,ab), fp};
        }
        Element temp_res = ElementToPolynom(fE, false); //  
        if (temp_res instanceof Polynom) { 
                makeWorkRing(0);
                FactorPol fp=(FactorPol)((Polynom) temp_res).factor(newRing);
                // выносим общтй числовой множитель  cfTP  из всех полиномов:
                Element cfTP=RING.numberONE;
                for (int i = 0; i < fp.multin.length; i++) 
                   if ((!fp.multin[i].isItNumber(RING))&&(!fp.multin[i].coeffs[0].isOne(RING))){
                       Element cfp=fp.multin[i].coeffs[0];
                       cfTP=cfTP.multiply(cfp.pow(fp.powers[i], RING), RING);
                       fp.multin[i]=fp.multin[i ].divideByNumber(cfp, RING);
                   }
                 // Запмшем его в фактор-полином:  
                    if (!cfTP.isOne(RING)) {
                        if(fp.multin[0].isItNumber()){
                            Element tempP =fp.multin[0].pow(fp.powers[0], RING).multiply(cfTP, RING);
                            fp.multin[0]=(tempP instanceof Polynom)? (Polynom)tempP: Polynom.polynomFromNot0Number(tempP);
                            fp.powers[0]=1;
                        }else {Polynom[] pp=new Polynom[fp.multin.length+1]; int[] ii=new int[fp.multin.length+1];
                          System.arraycopy(fp.multin, 0, pp, 1, fp.multin.length);
                          System.arraycopy(fp.powers, 0, ii, 1, fp.multin.length);
                          ii[0]=1; pp[0]=  Polynom.polynomFromNot0Number(cfTP);
                          fp=new FactorPol(ii,pp);
                        }     
            }
                return new Element[] {convert_FactorPol_to_El(fp), fp};
        }else
         if (temp_res instanceof Fraction) {Fraction fr=(Fraction)temp_res;
                makeWorkRing(0);
                Element nn= ((fr.num) instanceof Polynom)
                            ? ((Polynom) fr.num).factor(newRing): new FactorPol(new Polynom(fr.num));
                Element dd=  ((fr.denom) instanceof Polynom)
                            ? ((Polynom) fr.denom).factor(newRing): new FactorPol(new Polynom(fr.denom));
                        dd=nn.divide(dd, newRing);
                        if (!(dd instanceof FactorPol))return  new Element[] {dd, null};
                        ((FactorPol)dd).normalForm(newRing);
                        return new Element[] { convert_FactorPol_to_El(dd), dd};
         }else{return new Element[] {new F(F.ID, ElementToF(El2Pol(temp_res))), null} ;}   
    }
//=======================================================================================================

    /**
     * Процедура возвращающая превращающая ADD в SUBTRACT по возможности
     * (выносится -1 если складываются отрицательные или разбираются слогаемые
     * на + и - )
     *
     * @return измененный F
     */
    private F makeSUBTRACT(Element[] ARGS ) {
        boolean groupNegative = !RING.GROUP_NEGATIVE_TERMS.isZero(RING);
        if (ARGS.length==1)return (ARGS[0] instanceof F)? (F)ARGS[0]:new F (F.ID,ARGS[0] );
        if (!groupNegative) return new F(F.ADD, ARGS);
        ARGS = ExpandToADDorMULTYPLY(ARGS, F.ADD).X;
        Vector<Element> Pozitive = new Vector<Element>(); // заводим хранилища для положительных и отрицательных слагаемых
        Vector<Element> Negative = new Vector<Element>();
        for (Element el : ARGS) {
            if (el.isNegative()) { // заполняем из расчета отрицательности
                Negative.add(el.negate(RING)); // меняем знак перед заполнением
            } else {
                Pozitive.add(el); // или просто записваем в положительные
            }
        }
        if (Negative.isEmpty()) { // если все положительные то возвращаем без изменений
            return new F(F.ADD, ARGS);
        }
        if ((Pozitive.size() > 1) && (Negative.size() > 1)) {// если положительных и отрицательных больше ,чем один
            Element[] TempN = new Element[Negative.size()];
            Element[] TempP = new Element[Pozitive.size()];
            Negative.copyInto(TempN);
            Pozitive.copyInto(TempP);
            return new F(F.SUBTRACT, new Element[] {new F(F.ADD, TempP),
                        new F(F.ADD, TempN)});
        }
        if (Pozitive.isEmpty()) { // если все отрицательные то выносим -1 за скобки
            Element[] TempN = new Element[Negative.size()];
            Negative.copyInto(TempN);
            return new F(F.MULTIPLY, new Element[] {NumberZ.MINUS_ONE,
                        new F(F.ADD, TempN)});
        }
        // дальше по обстоятельствам
        if ((Negative.size() == 1) && (Pozitive.size() == 1)) { // дальше по обстоятельствам
            return new F(F.SUBTRACT, new Element[] {Pozitive.elementAt(0),
                        Negative.elementAt(0)});
        }
        if ((Negative.size() == 1) && (Pozitive.size() > 1)) {
            Element[] TempP = new Element[Pozitive.size()];
            Pozitive.copyInto(TempP);
            return new F(F.SUBTRACT, new Element[] {new F(F.ADD, TempP),
                        Negative.elementAt(0)});
        }
        if (Pozitive.size() == 1) {
            Element[] TempN = new Element[Negative.size()];
            Negative.copyInto(TempN);
            return new F(F.SUBTRACT, new Element[] {Pozitive.elementAt(0),
                        new F(F.ADD, TempN)});
        }
        return new F(F.ADD, ARGS);
    }

    /**
     * Процедура заменяющая произведение с множителями в отрицательной степени
     * на дробь
     *
     * @return
     */
    private F makeDIVIDE(Element[] ARGS) {
        Vector<Element> Num = new Vector<Element>(); // заводим хранилища для положительных и отрицательных слагаемых
        Vector<Element> Denum = new Vector<Element>();
        for (Element el : ARGS) { //сортируем в места хранения
            if (el instanceof F) {
                if ((((F) el).name == F.POW) | (((F) el).name == F.intPOW)) {
                    if ((((F) el).X[1]).isNegative()) { //если степень < 0
                        Denum.add(new F(((F) el).name, new Element[] {
                                    ((F) el).X[0], ((F) el).X[1].negate(RING)}));
                    } else {
                        Num.add(el);
                    }
                } else {
                    Num.add(el); // если не в степени
                }
            } else { // если не F , то в масси числителя
                Num.add(el);
            }
        }
        if (Num.size() == 1 && Denum.size() == 1) { // дальше по обстоятельствам
            return new F(F.DIVIDE, new Element[] {Num.elementAt(0),
                        Denum.elementAt(0)});
        }
        if (Num.size() == 1 && Denum.size() > 1) {
            Element[] denumm = new Element[Denum.size()];
            Denum.copyInto(denumm);
            return new F(F.DIVIDE, new Element[] {Num.elementAt(0),
                        new F(F.MULTIPLY, denumm)});
        }
        if (Denum.size() == 1 && Num.size() > 1) {
            Element[] numm = new Element[Num.size()];
            Num.copyInto(numm);
            return new F(F.DIVIDE, new Element[] {new F(F.MULTIPLY, numm),
                        Denum.elementAt(0)});
        }
        if (Num.size() > 1 && Denum.size() > 1) {
            Element[] numm = new Element[Num.size()];
            Num.copyInto(numm);
            Element[] denumm = new Element[Denum.size()];
            Denum.copyInto(denumm);
            return new F(F.DIVIDE, new Element[] {new F(F.MULTIPLY, numm),
                        new F(F.MULTIPLY, denumm)});
        }
        return new F(F.MULTIPLY, ARGS);
    }

    /**
     * Выходная форма полученная путем замены умножения на минус единицу
     * вычитанием и отрицательных степеней – делением.
     */
    public F MakeDivideAndSubtract(F f, int m) {
        Element[] TempARg = new Element[f.X.length];
        for (int i = 0; i < f.X.length; i++) {
            if (f.X[i] instanceof F) {
                if (((F) f.X[i]).name == F.ID) {
                    TempARg[i] = ((F) f.X[i]).X[0];
                } else {
                    TempARg[i] = MakeDivideAndSubtract((F) f.X[i], m);
                }
            } else {
                TempARg[i] = f.X[i];
            }
        }
        switch (f.name) {
            case F.ADD:
                return makeSUBTRACT(TempARg);// формируем разность
            case F.MULTIPLY:
                return (m != 0) ? makeDIVIDE(TempARg)
                        : ExpandToADDorMULTYPLY(TempARg, F.MULTIPLY);// формируем разность
            default: {
                return new F(f.name, TempARg);
            }
        }
    }
//========================================================================================================

    /**
     * Процедура осуществляющая удаление ассоциативных скобок внутри операции
     * сложения или удаление ассоциативных скобок внутри операции умножения.
     *
     * @param name_ - имя входной вершины (ADD | MULTYPLY)
     *
     * @return
     */
    private F ExpandToADDorMULTYPLY(Element[] ARGS, int name_) {
        Vector<Element> newX = new Vector<Element>();
        for (Element arg : ARGS) {
            if (arg instanceof F) {
                if (((F) arg).name == name_) {
                    newX.addAll(Arrays.asList(((F) arg).X));
                } else {newX.add(arg);}
            } else {newX.add(arg);}
        }
        Element[] NewX = new Element[newX.size()];
        newX.copyInto(NewX);
        return new F(name_, NewX);
    }

    /**
     * Процедура поднимает узлы с вложенными одноименными узлами-элементами
     *
     * @return
     */
    private F ExpandPow(Element[] ARGS, int name_) {
        if (ARGS[0] instanceof F) {
            if (((F) ARGS[0]).name == F.POW | ((F) ARGS[0]).name == F.intPOW) {
                return new F(F.POW, new Element[] {((F) ARGS[0]).X[0],
                            (ARGS[1]).add(((F) ARGS[0]).X[1], RING)});
            }
            return new F(name_, ARGS);
        } else {
            return new F(name_, ARGS);
        }
    }

    /**
     * Процедура поднимает узлы с вложенными одноименными узлами-элементами
     *
     * @param name_ - имя узла (DIVIDE | SUBTRUCT)
     *
     * @return
     */
    private F ExpandToDIVIDEorSUBTRACT(Element[] ARGS, int name_) {
        if (ARGS[0] instanceof F && ARGS[1] instanceof F) {
            if (((F) ARGS[0]).name == name_ && ((F) ARGS[1]).name == name_) {
                Element num = CanonMultiply(((F) ARGS[0]).X[0], ((F) ARGS[1]).X[1], name_
                        - 2);
                Element denom = CanonMultiply(((F) ARGS[0]).X[1], ((F) ARGS[1]).X[0], name_
                        - 2);
                return new F(name_, new Element[] {num, denom});
            }
            if (((F) ARGS[0]).name == name_) {
                Element denom = CanonMultiply(((F) ARGS[0]).X[1], ARGS[1], name_
                        - 2);
                return new F(name_, new Element[] {ARGS[0], denom});
            }
            if (((F) ARGS[1]).name == name_) {
                Element num = CanonMultiply(ARGS[0], ((F) ARGS[1]).X[1], name_
                        - 2);
                return new F(name_, new Element[] {num, ((F) ARGS[0]).X[0]});
            }
            return new F(name_, ARGS);
        }
        return new F(name_, ARGS);
    }

    /**
     * Замена разности суммой, в которой все элементы X[1] , стоят с
     * противоположным знаком
     *
     * @return
     */
    private F RemoveSubtract(Element[] ARGS) {
        int col_pozitive = argsLength(ARGS[0], F.ADD);
        int col_negatve = argsLength(ARGS[1], F.ADD);
        Element[] newX = new Element[col_pozitive + col_negatve];
        int index = 0;
        if (col_pozitive == 1) {
            newX[index] = ARGS[0];
            index++;
        } else {
            System.arraycopy(((F) ARGS[0]).X, 0, newX, index, col_pozitive);
            index += col_pozitive;
        }
        if (col_negatve == 1) {
            newX[index] = ARGS[1].negate(RING);
        }// меняем знак на противоположный
        else {
            F Nn = (F) ARGS[1].negate(RING); // меняем знак на противоположный
            System.arraycopy(Nn.X, 0, newX, index, Nn.X.length);
        }
        return new F(F.ADD, newX);
    }

    /**
     * Процедура превращающая DIVIDE в MULTIPLY
     *
     * @return F, в котором вместо DIVIDE лежат MULTIPLY
     */
    private F RemoveDIVIDE(Element[] ARGS) {
        int col_pozitiv = argsLength(ARGS[0], F.MULTIPLY);
        int col_negativ = argsLength(ARGS[1], F.MULTIPLY);
        Element[] newArg = new Element[col_negativ + col_pozitiv]; // заводим массив нужной нам длинны для будущго произведения
        int index = 0;
        if (col_pozitiv == 1) { // переписываем элементы
            newArg[index] = ARGS[0];
            index++;
        } else {
            System.arraycopy(((F) ARGS[0]).X, 0, newArg, index, col_pozitiv);
            index += col_pozitiv;
        }
        if (col_negativ == 1) {
            newArg[index] = multiplcative_Inversion(ARGS[1]);
        } else {
            Element[] Nn = ((F) ARGS[1]).X.clone();
            for (Element el : Nn) {
                newArg[index] = multiplcative_Inversion(el);
                index++;
            }
        }
        return new F(F.MULTIPLY, newArg);
    }

    /**
     * Полное поднятие дерева фун-ии
     *
     * @return
     */
    public F Expand(F f) {
        return ExpandForYourChoise(f, 1, 1, 1, 1, 1);
    }

    /**
     * Поднятие узлов дерева на выбор пользователя Лишние ID cчитаются
     * убранными.
     *
     * @param a - если > 0 , поднимаем сумму ,иначе нет.
     * @param m - если > 0 , поднимаем произведение ,иначе нет.
     * @param d - если > 0 , поднимаем деление , d=0 оставляем без изменений, d
     * меньше 0 меняем на произведение.
     * @param s - если > 0 , поднимаем разность , s=0 оставляем без изменений ,
     * s меньше 0 меняем на сумму.
     * @param p - если > 0 , поднимаем степень ,иначе нет.
     *
     * @return
     */
    public F ExpandForYourChoise(F f, int a, int m, int d, int s, int p) {
        Element[] newArg = new Element[f.X.length];
        for (int i = 0; i < f.X.length; i++) {
            Element fi = f.X[i];
            if (fi instanceof F) {
                newArg[i] = ExpandForYourChoise((F) fi, a, m, d, s, p);
            } else {
                newArg[i] = fi;
            }
        }
        switch (f.name) {
            case F.ADD:
                return (a > 0) ? ExpandToADDorMULTYPLY(newArg, f.name)
                        : new F(F.ADD, newArg);
            case F.MULTIPLY:
                return (m > 0) ? ExpandToADDorMULTYPLY(newArg, f.name)
                        : new F(F.MULTIPLY, newArg);
            case F.POW:
            case F.intPOW:
                return (p > 0) ? ExpandPow(newArg, f.name)
                        : new F(f.name, newArg);
            case F.DIVIDE:
                return (d > 0) ? ExpandToDIVIDEorSUBTRACT(newArg, f.name) : (d
                        == 0) ? new F(f.name, newArg) : RemoveDIVIDE(newArg);
            case F.SUBTRACT:
                return (s > 0) ? ExpandToDIVIDEorSUBTRACT(newArg, f.name) : (s
                        == 0) ? new F(f.name, newArg) : RemoveSubtract(newArg);
            default:
                return new F(f.name, newArg);
        }
    }

    /**
     * Метод складывающий или умножающий элементы(один из которых типа F)
     * ,который не допускает образования ассоциативных скобок
     *
     * @param a - элемент операции
     * @param b - элемент операции
     * @param name_ - имя операции (ADD | MULTIPLY)
     *
     * @return
     */
    public F CanonMultiply(Element a, Element b, int name_) {
        int numA = argsLength(a, name_);
        int numB = argsLength(b, name_);
        Element[] NewX = new Element[numA + numB]; // заводим массив новых аргументов
        if (numA == 1) { //заполняем массив по обстоятельствам
            NewX[0] = a;
            if (numB == 1) {
                NewX[1] = b;
            } else {
                System.arraycopy(((F) b).X, 0, NewX, 1, numB);
            }
        } else {
            System.arraycopy(((F) a).X, 0, NewX, 0, numA);
            if (numB == 1) {
                NewX[numA] = b;
            } else {
                System.arraycopy(((F) b).X, 0, NewX, numA, numB);
            }
        }
        return new F(name_, NewX);
    }

    /**
     * Процедура определяющая длинну массива аргументов X
     *
     * @param a - F
     * @param name - нужное нам имя
     *
     * @return длинна
     */
    private int argsLength(Element a, int name) {
        if (a instanceof F) {
            return (((F) a).name != name) ? 1 : ((F) a).X.length;
        } else {
            return 1;
        }
    }

    /**
     * Возведение в степнь -1 , либо изменение знака у степени (используется в
     * процедуре "уничтожения" дробей)
     *
     * @param a
     *
     * @return
     */
    public F multiplcative_Inversion(Element a) {
        if (a instanceof F) {
            if ((((F) a).name == F.intPOW) | (((F) a).name == F.POW)) {
                return new F(((F) a).name, new Element[] {((F) a).X[0],
                            ((F) a).X[1].negate(RING)});
            } else {
                if (((F) a).name == F.MULTIPLY) {
                    Element[] newARg = new Element[((F) a).X.length];
                    for (int i = 0; i < ((F) a).X.length; i++) {
                        newARg[i] = multiplcative_Inversion(((F) a).X[i]);
                    }
                    return new F(F.MULTIPLY, newARg);
                } else {
                    return new F(F.intPOW, new Element[] {a, new NumberZ(-1)});
                }
            }
        }
        return new F(F.ID, new Element[] {a.inverse(RING)});//new F(F.intPOW, new Element[]{a, new NumberZ(-1)});
    }
//=======================================================================================================
///=======================================================================
//=============   Universal Expand ====================================

    private Polynom UE_gcd_term_coef(Algebra alg) {
        if (alg.coeff.size() == 1) {
            return (Polynom) alg.coeff.get(0);
        }
        Polynom res = (Polynom) alg.coeff.get(0);
        Polynom gcd = res.myOne(RING);
        for (int i = 1; i < alg.coeff.size(); i++) {
            gcd = res.gcd((Polynom) alg.coeff.get(i), RING);
            if (gcd.isOne(RING)) {
                return gcd; // если выпала единица нет смысла искать дальше ))
            }
            res = gcd;
        }
        return gcd;
    }

    private Algebra UE_reduce_algebra_coef(Algebra alg, Polynom pol) {
        ArrayList<Element> new_coef = new ArrayList<Element>();
        for (int i = 0; i < alg.coeff.size(); i++) {
            new_coef.add(((Polynom) alg.coeff.get(i)).divide(pol, RING));
        }
        return new Algebra(new_coef, alg.term);
    }

    private Element UE_cancel(Fraction rat) {
        int num_type = rat.num.numbElementType();
        int denom_type = rat.denom.numbElementType();
        if (num_type == Ring.Polynom) {
            if (denom_type == Ring.Polynom) {
                return rat.cancel(RING);
            } else {
                Polynom sum_coeff_denom = UE_gcd_term_coef((Algebra) rat.denom);
                Polynom gcd = sum_coeff_denom.gcd((Polynom) rat.num, RING);
                return (gcd.isOne(RING)) ? rat
                        : new Fraction(((Polynom) rat.num).divide(gcd, RING), UE_reduce_algebra_coef((Algebra) rat.denom, gcd));
            }
        } else {
            if (denom_type == Ring.Polynom) {
                Polynom sum_coeff_num = UE_gcd_term_coef((Algebra) rat.num);
                Polynom gcd = sum_coeff_num.gcd((Polynom) rat.denom, RING);
                return (gcd.isOne(RING)) ? rat
                        : new Fraction(UE_reduce_algebra_coef((Algebra) rat.num, gcd), ((Polynom) rat.denom).divide(gcd, RING));
            } else {
                Polynom sum_coeff_num = UE_gcd_term_coef((Algebra) rat.num);
                Polynom sum_coeff_denom = UE_gcd_term_coef((Algebra) rat.denom);
                Polynom gcd = sum_coeff_num.gcd(sum_coeff_denom, RING);
                return (gcd.isOne(RING)) ? rat
                        : new Fraction(UE_reduce_algebra_coef((Algebra) rat.num, gcd), UE_reduce_algebra_coef((Algebra) rat.denom, gcd));
            }
        }
    }

    private Element UE_add(Element[] ARGS) {
        Element Res = convert_one_arg_UE(ARGS[0]);
        for (int i = 1; i < ARGS.length; i++) {
            Res = UE_proc_add(Res, convert_one_arg_UE(ARGS[i]));
        }
        return Res;
    }

    private Element UE_proc_add(Element a, Element b) {
        int a_type = a.numbElementType();
        int b_type = b.numbElementType();
        switch (a_type) {
            case Ring.Polynom:
            case Ring.G:
                switch (b_type) {
                    case Ring.G:
                    case Ring.Polynom:
                        return a.add(b, RING);
                    case Ring.Q:
                    case Ring.Rational:
                        Element ee = new Fraction(a, RING).add((Fraction) b, RING);
                        if (ee instanceof Fraction) {
                            return UE_cancel((Fraction) ee);
                        }
                        return ee;
                }
            case Ring.Q:
            case Ring.Rational:
                switch (b_type) {
                    case Ring.G:
                    case Ring.Polynom:
                        // return UE_cancel(((Fraction) a).add(new Fraction(b, RING), RING));
                        Element ee = ((Fraction) a).add(new Fraction(b, RING), RING);
                        if (ee instanceof Fraction) {
                            return UE_cancel((Fraction) ee);
                        }
                        return ee;
                    case Ring.Q:
                    case Ring.Rational:
                        return UE_cancel((Fraction) a.add(b, RING));
                }
        }
        return UE_cancel((Fraction) a.add(b, RING));
    }

    private Element UE_multiply(Element[] ARGS) {
        Element Res = convert_one_arg_UE(ARGS[0]);
        for (int i = 1; i < ARGS.length; i++) {
            Res = UE_proc_multiply(Res, convert_one_arg_UE(ARGS[i]));
        }
        return Res;
    }

    private Element UE_proc_multiply(Element a, Element b) {
        int a_type = a.numbElementType();
        int b_type = b.numbElementType();
        switch (a_type) {
            case Ring.Polynom:
            case Ring.G:
                switch (b_type) {
                    case Ring.G:
                    case Ring.Polynom:
                        return a.multiply(b, RING);
                    case Ring.Q:
                    case Ring.Rational:
                        Element tempRes = new Fraction(a, RING).multiply((Fraction) b, RING);
                        if (tempRes instanceof Fraction) {
                            return UE_cancel((Fraction) tempRes);
                        } else {
                            return tempRes;
                        }
                }
            case Ring.Q:
            case Ring.Rational:
                switch (b_type) {
                    case Ring.G:
                    case Ring.Polynom:
                        Element tempRes = ((Fraction) a).multiply(new Fraction(b, RING), RING);
                        if (tempRes instanceof Fraction) {
                            return UE_cancel((Fraction) tempRes);
                        } else {
                            return tempRes;
                        }
                    case Ring.Q:
                    case Ring.Rational:
                        return UE_cancel((Fraction) a.multiply(b, RING));
                }
        }
        return UE_cancel((Fraction) a.multiply(b, RING));
    }

    private Element UE_subtract(Element[] ARGS) {
        return UE_proc_subtract(convert_one_arg_UE(ARGS[0]), convert_one_arg_UE(ARGS[1]));
    }

    private Element UE_proc_subtract(Element a, Element b) {
        int a_type = a.numbElementType();
        int b_type = b.numbElementType();
        switch (a_type) {
            case Ring.Polynom:
            case Ring.G:
                switch (b_type) {
                    case Ring.G:
                    case Ring.Polynom:
                        return a.subtract(b, RING);
                    case Ring.Q:
                    case Ring.Rational:
                        Element tempRes = (new Fraction(a, RING).subtract((Fraction) b, RING));
                        if (tempRes instanceof Fraction) {
                            return UE_cancel((Fraction) tempRes);
                        } else {
                            return tempRes;
                        }
                }
            case Ring.Q:
            case Ring.Rational:
                switch (b_type) {
                    case Ring.G:
                    case Ring.Polynom:
                        Element tempRes = ((Fraction) a).subtract(new Fraction(b, RING), RING);
                        if (tempRes instanceof Fraction) {
                            return UE_cancel((Fraction) tempRes);
                        } else {
                            return tempRes;
                        }
                    case Ring.Q:
                    case Ring.Rational:
                        return UE_cancel((Fraction) a.subtract(b, RING));
                }
        }
        return UE_cancel((Fraction) a.subtract(b, RING));
    }

    private Element UE_divide(Element[] ARGS) {
        return UE_proc_divide(convert_one_arg_UE(ARGS[0]), convert_one_arg_UE(ARGS[1]));
    }

    private Element UE_proc_divide(Element a, Element b) {
        int a_type = a.numbElementType();
        int b_type = b.numbElementType();
        switch (a_type) {
            case Ring.Polynom:
            case Ring.G:
                switch (b_type) {
                    case Ring.G:
                        return UE_cancel(new Fraction(a, b));
                    case Ring.Polynom:
                        return UE_cancel(new Fraction(a, b)); // пробуем вдобавок сократить
                    case Ring.Q:
                    case Ring.Rational:
                        return UE_cancel(new Fraction(a.multiply(((Fraction) b).denom, RING), ((Fraction) b).num));
                }
            case Ring.Q:
            case Ring.Rational:
                switch (b_type) {
                    case Ring.G:
                    case Ring.Polynom:
                        return UE_cancel(new Fraction(((Fraction) a).num, ((Fraction) a).denom.multiply(b, RING)));
                    case Ring.Q:
                    case Ring.Rational:
                        return UE_cancel(((Fraction) a.divide(b, RING)));
                }
        }
        return UE_cancel(new Fraction(a, b));
    }

    private Element UE_intpow(Element[] ARGS) {
        int Pow = ARGS[1].intValue();
        return convert_one_arg_UE(ARGS[0]).pow(Pow, RING);
    }

    public Element Universal_Convert_With_Expand(F f, boolean makeVectors) {
        if (makeVectors) {
            makeNewVectFandVectEL();
        }
        index_in_Ring = RING.varNames.length + 1;
        Element last_res =
                convert_one_arg_UE(CleanElement(f));
        makeWorkRing(index_in_Ring - RING.varNames.length-1);//2015
        makeG_Ring();
        return last_res;
    }

    /**
     * Заполняем вектора замен содержащие некомутативные элементы, так формируем
     * имена нового кольца G[]
     * 
     * Сохраняем либо имена (backslash+capital) либо сами элементы (матрицы), но и имена их будут такие V$_n, где n - index_in_G_Ring
     * @param El
     */
    private void fill_G_Vectors(Element El) {
        if (El instanceof Fname) {
            if (G_List_Names.indexOf(((Fname) El).name) == -1) { // этого элемента не было -- добавляем, иначе ничего не делаем
                G_List_Names.add(((Fname) El).name);
                index_in_G_Ring++;
                G_ListChange.add(El);
            }
        } else {
            if (G_ListChange.indexOf(El) == -1) { // этот элемент уже  был
                G_ListChange.add(El);
                index_in_G_Ring++;
                G_List_Names.add("V$_" + index_in_G_Ring);
            }
        }
    }

    private Element convert_one_arg_UE(Element El) {
        if (El instanceof F) {
            switch (((F) El).name) {
                case F.ID:
                    return convert_one_arg_UE(((F) El).X[0]);
                case F.ADD:
                    return UE_add(((F) El).X);
                case F.MULTIPLY:
                    return UE_multiply(((F) El).X);
                case F.SUBTRACT:
                    return UE_subtract(((F) El).X);
                case F.DIVIDE:
                    return UE_divide(((F) El).X);
                case F.intPOW: // ????????????????????????????
                    return UE_intpow(((F) El).X);
                default:
                    return workToDefault(El, 1);
            }
        } else {
            if (El instanceof Fraction) {
                return UE_divide(new Element[] {
                            convert_one_arg_UE(((Fraction) El).num),
                            convert_one_arg_UE(((Fraction) El).denom)});
            }
            switch (El.numbElementType()) {
                case Ring.Polynom:
                    return El;
                case Ring.Fname: {
                    if (Character.isUpperCase(((Fname) El).name.charAt(0))) {
                        fill_G_Vectors(El);
                        ArrayList<Element> coeff = new ArrayList<Element>();
                        ArrayList<Gterm> term = new ArrayList<Gterm>();
                        coeff.add(RING.numberONE());
                        term.add(new Gterm(new int[] {1, G_ListChange.indexOf(El),
                                    1}));
                        return new Algebra(coeff, term);
                    } else {
                        return workToDefault(El, 1);
                    }
                }
                case Ring.VectorS:
                case Ring.MatrixS:
                case Ring.MatrixD:
                    fill_G_Vectors(El);
                    ArrayList<Element> coeff = new ArrayList<Element>();
                    ArrayList<Gterm> term = new ArrayList<Gterm>();
                    coeff.add(RING.numberONE());
                    term.add(new Gterm(new int[] {1, index_in_G_Ring, 1}));
                    return new Algebra(coeff, term);
                default:
                    return new Polynom(El);
            }
        }
    }

    public Element Univers_unconvert(Element t) {
        if (t instanceof Fraction) {
            return new F(F.DIVIDE, new Element[] {
                        UE_unconvert(((Fraction) t).num),
                        UE_unconvert(((Fraction) t).denom)});
        }
        return UE_unconvert(t);
    }

    private Element UE_unconvert(Element el) {
        return (el instanceof Polynom) ? Unconvert_Polynom_to_Element(el)
                : Algebra2Element((Algebra) el, true, true);
    }
    // возможны флаги !!!! но нужно ли?????

    public Element Univers_Expand(F func, boolean makeVectors) {
        Element convert_func = Universal_Convert_With_Expand(func, makeVectors);
        Element res = (convert_func instanceof Fraction)
                ? new F(F.DIVIDE, new Element[] {
                    UE_unconvert(((Fraction) convert_func).num),
                    UE_unconvert(((Fraction) convert_func).denom)})
                : UE_unconvert(convert_func);
        cleanLists();
        return res;
    }

   private F icWork(F ic, List<Element> expr) {
        Element[] newArg = ic.X;
        newArg[0] = expandFnameOrId(ic.X[0]);
        for (int i = 1; i < ic.X.length; i++) {
            if (newArg[i] != null) {
                newArg[i] = (newArg[i] instanceof F) ? substituteValueToFnameAndCorrectPolynoms((F) newArg[i],expr)
                        : substituteValueToFnameAndCorrectPolynoms(new F(F.ID, newArg[i]), expr);
            }
        }
        return new F(ic.name, newArg);
    }

    /**
     * Входная форма. Используется при вводе функции со строки. Осуществляет :
     * 1. Подстановка значений в Fname (в зависимости от substituteflag); 2.
     * Приводим числа и полиномы к R64 , если тип R , а текущее кольцо R64; 3.
     * Формируем VectorS & MatrixS , а так же узeл MULTIPLY_NC; 4. Формируем
     * узел intPow; 5. Поднимаем узлы ADD и MULTIPLY , убираем ID ; 6. Меняем
     * Fname c именем (\\i) на Complex.I или возвращаем Null если кольцо не
     * Комплексное 7. В зависимости от expandFlag : true - полное раскрытие на
     * каждом арифметическом уровне(результат умножение или одна дробь) false
     * -выполнение простых арифметических операций (-,*,+) на каждом из
     * арифметическом уровне. 8. Вычисление в зависимости от корректности
     * задания узлов: \TIME, \GCD, \LCM, \D, \VALUE, \FACTOR, \EXPAND & etc..
     *
     * В начале процедуры формируем матрицы,вектора, e, I ,O ,\i ,\Pi ,\infty & etc...;
     *   редактируем +,*,-,^;
     *   создаем матрицы и вектора из F как объекты MatrixD и VectorS
     *   работа с поэлементными обращениями матриц и векторов;
     * @param func -входная функция типа F.
     */
    public Element InputForm(F func, Page page, List<Element> express) {
        //перезаписываем поле так как там могли возникнуть изменения
       this.page = page;  // Надо понять зачем это тут -- но без него не работает.....
       Element res;
      try {
            res = substituteValueToFnameAndCorrectPolynoms(func, express);
        } catch (Exception ex) {
            throw new RuntimeException("\n CanonicForms(InputForm): POSSIBLE THAT SYNTAX ERROR", ex);
       }
        // если в результате не F то возвращаем резульат сразу
        if (!(res instanceof F)) {
            res=(index_in_Ring - 1 == RING.varNames.length) ? res : ElementToF(res);
            //возвращаем начальные значения индексам для предотвращения роста длинны массива степеней в будущих полиномах при замене
             returnFirstStatsCForm();
            return res;
        }
        // выполняем действия(f.e.: вычисления производной, определителя матрицы и т.д..)LIM тут
        res = runOperators((F) res, page, express); // вычисляем операторы
        // если в результате не F то возвращаем резульат сразу
        if (!(res instanceof F)) {
//            if(res instanceof Fraction){res=new F(F.DIVIDE, ((Fraction)res).num, ((Fraction)res).denom );}
//            else{
            res=(index_in_Ring - 1 == RING.varNames.length) ? res : ElementToF(res);
            //возвращаем начальные значения индексам для предотвращения роста длинны массива степеней в будущих полиномах при замене
             returnFirstStatsCForm();    
            return res;
     //   }
        }
        // блок арифметических вычислений
        try {  
            //   !!!!!!!!!!!!!!!!! ГЛАВНАЯ Ф-ция
            // ловим ошибки на случай операций с некорректными слaгаемыми , множителями и тд
            res = simplify_init(res); // (b+(1+w))   w=t и in-in-=4 
   // НЕЛЬЗЯ !! ВСКРЫВАТЬ КОММ !!! res=(index_in_Ring - 1 == RING.varNames.length) ? res : ElementToF(res);           
//возвращаем начальные значения индексам для предотвращения роста длинны массива степеней в будущих полиномах при замене
            returnFirstStatsCForm();
            // на выходе формируем разность
            return (res instanceof F) ? MakeDivideAndSubtract((F) res, 0) : res;
        } catch (Exception ex) {
            RING.exception.append("\n EXCEPTION in CanonicForms(InputForm): Arithmetic exception");
            return null;
        }
    }
/**
 * Комплексные корни полиноиа нужно привести в соответствие с пространством SPACE
 *   SPACE действительное, поэтому надо отбросить комплексные корни.
 * @param el -  Element or VectorS or VectorS
 * @return Element or VectorS or VectorS без комплексных корней
 */
    private Element cleanResultOfCalculateRoots(Element el){
    if(el instanceof VectorS){
     if(isComplexRing) return el;
     Vector<Element> res= new Vector<Element>();
     int typeK;
     for(Element k : ((VectorS)el).V){
      typeK=k.numbElementType();
      if(!k.isNaN()) {
         if(!(k instanceof Complex)){
         res.add(k);
         }else{
          if(((Complex)k).im.isZero(RING)) {
                 res.add(((Complex)k).re);
             }
         }
      }
     }
     return res.isEmpty()? new VectorS(new Element[]{Element.NAN}) : new VectorS(res.toArray(new Element[res.size()]));
    }
    if(el instanceof VectorS){
     return new VectorS((VectorS)cleanResultOfCalculateRoots((VectorS)el),((VectorS)el).fl);
    }
    if((el==null)||(el==Element.NAN)) return  Element.NAN ;
    if((el instanceof Complex)&&(((Complex)el).im.isZero(RING)))return ((Complex)el).re;
    return el;
    }
 

    public Element Simplify(F f) {
        if(vectF==null) {makeNewVectFandVectEL();}
        index_in_Ring = RING.varNames.length + 1;
        Element res = CleanElement(f);
        res = ElementToF(workSWG(res));
        return (res instanceof F)? MakeDivideAndSubtract((F) res, 0) : res;
    }
 /**
  * Вспомогательная процедура для Input_Form. Является заключительным этапом обработки функции, на котором
  * происходит вычисление простейших арифметических операций.
  * @param f - входная функция типа F
  * @return
  */
    public Element simplify_init(Element f) {
        if (f instanceof F) {F[] fA= new F[]{(F)f};
            F.cleanOfRepeatingWithNewVectors(fA, RING);
           // Element el = CleanElement(f);// очищаем
           Element el = funcExpand(fA[0]);
   //         if (el instanceof Polynom) el=LN_EXP_POW_LOG_LG.factorExpFunction((Polynom)el, 0, RING);
          // совершаем простейшие арифметическме операции с конвертацией в полином по арифметическим уровням
       
            el = ElementToF(el);// обратная конвертация полученного результата
            return el; // возвращаем результат вычислений
        }
        return f;
    }

/**
 * Поднимаем следующий арифметический уровень
 * @param f
 * @return
 */
    private Element expand_next_level(Element f) {
        if (f instanceof F) {
            if (((F) f).name == F.d | ((F) f).name == F.IC) {
                return CleanElement(f);
            }
            Element el = funcExpand(f);
            el = ElementToF(el);
            return el;
        }
        return f;
    }

/**
 * Вспомогательная процедура для funcExpand. Обрабатываем узел умножения , где в параметрах:
 * @param el - множитель из коммутативных элементов
 * @param vec - список некоммутативных элементов
 * @return
 */
    private Element work_multiply(Element el, ArrayList<Element> vec) {
        if (vec.isEmpty()) {
            return el;
        }
        // вносим множитель если это возможно и схлопываем матрицы
        if(!el.isOne(RING)){
        boolean flagMul=true;
        for(int i=0;i<vec.size();i++){
         if(flagMul && !(vec.get(i) instanceof Algebra)){
            Element temp = vec.get(i).multiply(el, RING);
            if(el instanceof Polynom && ((Polynom)el).powers.length>=RING.varNames.length){
             if(temp instanceof MatrixD){
               ((MatrixD)temp).fl=1;
               }
             if(temp instanceof VectorS){
             ((VectorS)temp).fl=1;
              }
               }
            vec.set(i, temp);
            i--;
            flagMul=false;
            el=RING.numberONE;
            continue;
         }else{
         if(!(vec.get(i) instanceof Algebra)&&(i+1<vec.size())&&(!(vec.get(i+1) instanceof Algebra))){
          Element temp=vec.get(i).multiply(vec.get(i+1), RING);
          vec.remove(i+1);
          vec.set(i, temp);
          continue;
         }
//         if((vec.get(i) instanceof Algebra)&&(i+1<vec.size())&&((vec.get(i+1) instanceof Algebra))){
//          Element temp=vec.get(i).multiply(vec.get(i+1), RING);
//          vec.remove(i+1);
//          vec.set(i, temp); 
//          continue;
//           }
          }
         }
        }else{
        for(int i=0;i<vec.size()-1;i++)// перемножаем или 2 матрицы или 2 алгебры
          if( !((vec.get(i) instanceof  com.mathpar.polynom.Algebra)^((vec.get(i+1) instanceof com.mathpar.polynom.Algebra)))){
             Element temp=vec.get(i).multiply(vec.get(i+1), RING);
             vec.remove(i+1);
             vec.set(i, temp);i--;
          } 
        }
        if(vec.size()==1){
         return (vec.get(0) instanceof Algebra)? (el.isOne(RING))? vec.get(0): vec.get(0).multiply(el, RING)
                 : vec.get(0);
        }
       Algebra res = new Algebra(el);
        for (int i = 0; i < vec.size(); i++) {
            res = (vec.get(i) instanceof Algebra) ? res.multiply((Algebra) vec.get(i), RING) : res.multiply(makeAlgebraFromNCElement(vec.get(i), RING.numberONE), RING);
        }
        return  res;
    }

    private Algebra makeAlgebraFromNCElement(Element el, Element coef) {
        fill_G_Vectors(el);
        ArrayList<Element> coeff = new ArrayList<Element>();
        ArrayList<Gterm> term = new ArrayList<Gterm>();
        coeff.add(RING.numberONE());
        term.add(new Gterm(new int[] {1, G_ListChange.indexOf(el), 1}));
        return new Algebra(coeff, term);
    }

    private Element myOpSubFE(Element a, Element b){
      switch(a.numbElementType()){
          //алгебры
          case Ring.G:
             switch(b.numbElementType()){
                 case Ring.MatrixD:
                 case Ring.MatrixS:
                 case Ring.VectorS:
                    fill_G_Vectors(b);
                    ArrayList<Element> coeff = new ArrayList<Element>();
                    ArrayList<Gterm> term = new ArrayList<Gterm>();
                    coeff.add(RING.numberONE());
                    term.add(new Gterm(new int[] {1, G_ListChange.indexOf(b), 1}));
                    return a.subtract(new Algebra(coeff, term),RING);
                 case Ring.G:
                    return a.subtract(b, RING);
                 default:
                    return a.subtract(new Algebra(b), RING);
             }
          //другие некоммутативные элементы
          case Ring.MatrixD:
          case Ring.MatrixS:
          case Ring.VectorS:
          if(!(b instanceof Algebra)) return a.subtract(b, RING);
          fill_G_Vectors(a);
          ArrayList<Element> coeff = new ArrayList<Element>();
          ArrayList<Gterm> term = new ArrayList<Gterm>();
          coeff.add(RING.numberONE);
          term.add(new Gterm(new int[] {1, G_ListChange.indexOf(a), 1}));
          return new Algebra(coeff, term).subtract(b, RING);
          //все остальное
          default:
          return  // (!(b instanceof com.mathpar.polynom.Algebra)) ?
          
                  a.subtract(b, RING); //):
        //   new Algebra(a).subtract(b, RING);
      }
    }
    private Element myOpAddFE(Element a, Element b){
      switch(a.numbElementType()){
          //алгебры
          case Ring.G:
             switch(b.numbElementType()){
                 case Ring.MatrixD:
                 case Ring.MatrixS:
                 case Ring.VectorS:
                    fill_G_Vectors(b);
                    ArrayList<Element> coeff = new ArrayList<Element>();
                    ArrayList<Gterm> term = new ArrayList<Gterm>();
                    coeff.add(RING.numberONE());
                    term.add(new Gterm(new int[] {1, G_ListChange.indexOf(b), 1}));
                    return a.add(new Algebra(coeff, term),RING);
                 case Ring.G:
                    return a.add(b, RING);
                 default:
                    return a.add(new Algebra(b), RING);
             }
          //другие некоммутативные элементы
          case Ring.MatrixD:
          case Ring.MatrixS:
          case Ring.VectorS:
          if(!(b instanceof Algebra)) return a.add(b, RING);
          fill_G_Vectors(a);
          ArrayList<Element> coeff = new ArrayList<Element>();
          ArrayList<Gterm> term = new ArrayList<Gterm>();
          coeff.add(RING.numberONE);
          term.add(new Gterm(new int[] {1, G_ListChange.indexOf(a), 1}));
          return new Algebra(coeff, term).add(b, RING);
          //все остальное
          default:
          return (!(b instanceof Algebra)) ?
           a.add(b, RING)
          : new Algebra(a).add(b, RING);
      }
    }
    
/**
 *  Возвращает соответствующих виртуальный полином
 * @param el - Элемент любого типа подлежащий упрощению и конвертации
 * @return - виртуальный полином
 */
    private Element funcExpand(Element el) {
        switch (el.numbElementType()) {
            case Ring.F: {
                F Fel = (F) el;
                switch ((Fel).name) {
                    case F.ID:
                        return funcExpand(Fel.X[0]);
                    case F.ADD: {
                        Element res = funcExpand((Fel).X[0]);
                        for (int i = 1; i < (Fel).X.length; i++) {
                            res = myOpAddFE(res,funcExpand((Fel).X[i]));
                        }
                            return res;
                    }
                    case F.SUBTRACT: {
                        return myOpSubFE(funcExpand((Fel).X[0]),funcExpand((Fel).X[1]));
                    }
                    case F.MULTIPLY_NC:
                    case F.MULTIPLY: {
                        ArrayList<Element> vec = new ArrayList<Element>();
                        Element mnogitel = RING.numberONE;
                        for (int i = 0; i < (Fel).X.length; i++) {
                            Element b = funcExpand((Fel).X[i]);
                            switch (b.numbElementType()) {
                                case Ring.MatrixS:
                                case Ring.VectorS:
                                case Ring.MatrixD: {vec.add(b); break; }
                                case Ring.G:
                                    if(((Algebra)b).coeff.size()==1){
                                     mnogitel=mnogitel.multiply(((Algebra)b).coeff.get(0), RING);
                                     ((Algebra)b).coeff.set(0, RING.numberONE);
                                    }
                                    vec.add(b);
                                    break;
                                default:
                                    mnogitel = mnogitel.multiply(b, RING);
                            }
                        }
                        return work_multiply(mnogitel, vec);
                    }
                    case F.intPOW: {
                            return funcExpand(Fel.X[0]).pow(Fel.X[1].intValue(), RING);
                    }
                    case F.DIVIDE: {
                            Element a111 = funcExpand((Fel).X[0]);
                            Element b111 = funcExpand((Fel).X[1]);
                            return myOpDiv(a111, b111);
                    }
                    //разбиение на арифметические уровни
                    case F.ROOTOF:
                    Element[] nArg_= new Element[2];
                    for (int j = 0; j < 2; j++) {
                            nArg_[j] = expand_next_level((Fel).X[j]);
                        }
                    Element resRof=(nArg_[0].isItNumber() && nArg_[1].isItNumber()) ? nArg_[0].rootOf(nArg_[1].intValue(), RING) : new F(F.ROOTOF,nArg_);
                    return ((resRof instanceof F)||(resRof instanceof Fname))?  workToDefault(CleanElement(resRof), 1):  resRof ;
                    case F.KORNYAK_MATRIX:
                        Ring[] ringOut = new Ring[] {page.ring};
                        Element[][] permsTmp = ((MatrixD) ((F) el).X[0]).M;
                        int[][] perms = new int[permsTmp.length][];
                        for (int i = 0, rowCnt = permsTmp.length; i < rowCnt; i++) {
                            int rowLen = permsTmp[i].length;
                            perms[i] = new int[rowLen];
                            for (int j = 0; j < rowLen; j++) {
                                perms[i][j] = permsTmp[i][j].intValue();
                            }
                        }
                        int matrixSize = ((F) el).X[1].intValue();
                        Element res = MatrixS.kornyakMatrix(perms, matrixSize, ringOut);
                        RING = ringOut[0];
                        page.setRing(ringOut[0]);
                        return res;
                    case F.LOG:
                    Element[] nArg= new Element[2];
                    for (int j = 0; j < 2; j++) {
                            nArg[j] = expand_next_level((Fel).X[j]);
                        }
                    Element resLog=(nArg[0].isItNumber() && nArg[1].isItNumber()) ? nArg[1].log(nArg[0], RING) : new F(F.LOG,nArg);
                    return (resLog.isItNumber())? resLog :   workToDefault(CleanElement(resLog), 1);
                    case F.POW:
                    case F.SYSTLDE:
                    case F.SYSTEM:
                    case F.EQUATION:
                    case F.SYSTEMOR:
                    case F.INITCOND:
                    case F.BINOMIAL:
                    case F.SUM:
                    case F.UNITBOX:
                    case F.UNITSTEP:
                    case F.VECTOR_SET:
                    {
                        Element[] newArg = new Element[((F) el).X.length];
                        for (int j = 0; j < ((F) el).X.length; j++) {
                            newArg[j] = expand_next_level(Fel.X[j]);
                        }
                        Element equ1=workToDefault(CleanElement(new F(((F) el).name, newArg)), 1);
                        return equ1;
                    }
                    case F.SERIES:
                    case F.INT:
                    case F.IC:
                    case F.d:
                        return workToDefault(CleanElement(el), 1);
                    default:
                         if( F.isInfixName(Fel.name))
                           return new F(Fel.name, funcExpand((Fel).X[0]), funcExpand((Fel).X[1]));
                        
                        switch(((F) el).X[0].numbElementType()){
                            case Ring.F:
                            Element valF=valueOFToDefault(((F) el).name, expand_next_level(((F) el).X[0]));
                            return (valF.isItNumber())? valF: workToDefault(valF, 1);
                            case Ring.VectorS:
                            case Ring.MatrixD:
                               Element tempNC=ElementToF(((F) el).X[0]);
                               tempNC = new F(((F) el).name, tempNC);
                               tempNC = CleanElement(tempNC);
                               return workToDefault(tempNC, 1);
                           default:
                             Element valF_=valueOFToDefault(((F) el).name, ((F) el).X[0]);
                             return (valF_.isItNumber())? valF_: workToDefault(valF_, 1);
                        }
                }
            }
            case Ring.MatrixD:
            case Ring.MatrixS: // оборачиваем элементы матриц и векторов в полиномы
                return matrixF2P(el);
            case Ring.VectorS:
                return vectorF2P((VectorS) el);
            case Ring.Polynom: //работаем с полиномами если  длинна мономов больше 1 и expand_Flag=false
                    return el;
            case Ring.Q: // оборачиваем дроби из чисел  и полиномов, с ними должен работать был \gether
            case Ring.Rational:
                  Element a111 = funcExpand(((Fraction)el).num);
                  Element b111 = funcExpand(((Fraction)el).denom);
                  return myOpDiv(a111, b111);
                 // return workToDefault(el, 1);
            case Ring.Fname: {
                if (((Fname) el).name.indexOf("\\") == 0 && Character.isUpperCase(((Fname) el).name.charAt(1))) {
                    fill_G_Vectors(el);
                    ArrayList<Element> coeff = new ArrayList<Element>();
                    ArrayList<Gterm> term = new ArrayList<Gterm>();
                    coeff.add(RING.numberONE());
                    term.add(new Gterm(new int[] {1, G_ListChange.indexOf(el), 1}));
                    return new Algebra(coeff, term);
                }
                return workToDefault(el, 1);
            }
            case Ring.FactorPol:
            case Ring.Factor:
              return workToDefault(el, 1);
            default:
                return el; // имеем дело с числом
        }
    }
/** Value of function in the point "arg"
 *
 * @param name (int) the name of function
 * @param arg argument of function
 * @return value of the function "name" in the point arg
 */
    private Element valueOFToDefault(int name, Element arg) {
         if (!arg.isItNumber()) {if (!arg.isNegative())return new F(name, arg);
            switch (name) {
            case F.COS: case F.CH:case F.ARCCH:
                return new F(name, arg.negate(RING));        
            case F.SIN: case F.SH: case F.TG:case F.TH:case F.CTH:
            case F.CTG:case F.ARCTG:case F.ARCCTG:
            case F.ARCCTGH: case F.ARCSH: case F.ARCTGH:
                 return (new F(name, arg.negate(RING)).negate(RING)); 
            default:  return new F(name, arg);
        }}
        if (( RING.algebra[0] < Ring.R)||(( RING.algebra[0] < Ring.C)&&( RING.algebra[0] > Ring.Complex))) {return new F(name, arg);}
        Element res=null;
        switch (name) {
            case F.EXP:
                res = arg.exp(RING); break;
           case F.SQRT:
                res = arg.sqrt(RING); break;
            case F.COS:
                res = arg.cos(RING);break;
            case F.SIN:
                res = arg.sin(RING);break;
            case F.TG:
                res = arg.tan(RING);break;
            case F.CTG:
                res = arg.ctg(RING);break;
            case F.SH:
                res = arg.sh(RING);break;
            case F.CH:
                res = arg.ch(RING);break;
            case F.TH:
                res = arg.th(RING);break;
            case F.CTH:
                res = arg.cth(RING);break;
            case F.LN:
                res = arg.ln(RING);break;
            case F.LG:
                res = arg.lg(RING);break;
            case F.ARCSIN:
                res = arg.arcsn(RING);break;
            case F.ARCCOS:
                res = arg.arccs(RING);break;
            case F.ARCTG:
                res = arg.arctn(RING);break;
             case F.ARCCTG:
                res = arg.arcctn(RING);break;
            case F.ARCSH:
                res = arg.arsh(RING);break;
            case F.ARCCH:
                res = arg.arch(RING);break;
             case F.ARCTGH:
                res = arg.arth(RING); break;

        }
        return (res==null || !res.isItNumber()) ?  CleanElement(new F(name,arg)): res;
    }





    private Element work_with_times_UE(Element[] ARGS) {
        ArrayList<Element> newArg = new ArrayList<Element>();
        Element mul_res = null;
        for (int i = 0; i < ARGS.length; i++) {
            if (ARGS[i].numbElementType() == Ring.Fname) {
                if (mul_res != null) {
                    mul_res = null;
                }
                newArg.add(ARGS[i]);
            } else {
                if (mul_res == null) {
                    newArg.add(ARGS[i]);
                    mul_res = ARGS[i];
                } else {
                    mul_res = mul_res.multiply(ARGS[i], RING);
                    newArg.remove(newArg.size() - 1);
                    newArg.add(mul_res);
                }
            }
        }
        return (newArg.size() == 1) ? newArg.get(0)
                : new F(F.MULTIPLY_NC, newArg.toArray(new Element[newArg.size()]));
    }

    /**
     * Считаeм что в умножение одно не коммутативное умножение
     *
     * @param func
     *
     * @return
     */
    private Element multiply_last_result_expand(F func) {
        ArrayList<Element> new_mul = new ArrayList<Element>();
        Element result_mul_nc_element = null;
        for (int i = 0; i < func.X.length; i++) { // формируем множитель и перемножаем сомножители в \times
            switch (func.X[i].numbElementType()) {
                case Ring.F:
                    if (((F) func.X[i]).name == F.MULTIPLY_NC) {
                        result_mul_nc_element =
                                work_with_times_UE(((F) func.X[i]).X);
                    } else {
                        new_mul.add(func.X[i]);
                    }
                    break;
                case Ring.Fname:
                    if (Character.isUpperCase(((Fname) func.X[i]).name.charAt(0))) {
                        result_mul_nc_element = func.X[i];
                    } else {
                        new_mul.add(func.X[i]);
                    }
                    break;
                case Ring.MatrixS:
                case Ring.MatrixD:
                case Ring.VectorS:
                    result_mul_nc_element = func.X[i];
                    break;
                default:
                    new_mul.add(func.X[i]);
            }
        }
        if (result_mul_nc_element == null) { // значит это обычное умножение без некомутативных элементов
            return func;
        }
        if (result_mul_nc_element instanceof F) {
            new_mul.add(result_mul_nc_element);
            return new F(F.MULTIPLY, new_mul.toArray(new Element[new_mul.size()]));
        } else {
            return (new_mul.size() == 1)
                    ? result_mul_nc_element.multiply(new_mul.get(0), RING)
                    : result_mul_nc_element.multiply(new F(F.MULTIPLY, new_mul.toArray(new Element[new_mul.size()])), RING);
        }
    }

    private Element work_last_result_expand(Element el) {
        if (el instanceof F) {
            switch (((F) el).name) {
                case F.MULTIPLY:
                    return multiply_last_result_expand((F) el);
                case F.SUBTRACT:
                    return work_last_result_expand(((F) el).X[0]).subtract(work_last_result_expand(((F) el).X[1]), RING);
                case F.DIVIDE:
                    return new F(F.DIVIDE, new Element[] {
                                work_last_result_expand(((F) el).X[0]),
                                work_last_result_expand(((F) el).X[1])});
                case F.ADD:
                    return add_last_resul_expand((F) el);
                default:
                    return el;
            }
        }
        return el;
    }

    /**
     * Cчитаем что сумма чистая
     *
     * @param func
     *
     * @return
     */
    private Element add_last_resul_expand(F func) {
        Element vec_add = null;
        Element matrix_add = null;
        ArrayList<Element> comut_arg = new ArrayList<Element>();
        for (int i = 0; i < func.X.length; i++) {
            switch (func.X[i].numbElementType()) {
                case Ring.F:
                    Element mul_res = work_last_result_expand(func.X[i]);
                    int nt = mul_res.numbElementType();
                    if (nt == Ring.VectorS) {
                        vec_add = (vec_add == null) ? vec_add = mul_res
                                : vec_add.add(mul_res, RING);
                        break;
                    }
                    if (nt == Ring.MatrixS | nt == Ring.MatrixD) {
                        matrix_add = (matrix_add == null) ? matrix_add = mul_res
                                : matrix_add.add(mul_res, RING);
                        break;
                    }
                    comut_arg.add(mul_res);
                    break;
                case Ring.MatrixD:
                case Ring.MatrixS:
                    matrix_add = (matrix_add == null) ? matrix_add = func.X[i]
                            : matrix_add.add(func.X[i], RING);
                    break;
                case Ring.VectorS:
                    vec_add = (vec_add == null) ? vec_add = func.X[i]
                            : vec_add.add(func.X[i], RING);
                    break;
                default:
                    comut_arg.add(func.X[i]);
            }
        }
        if (comut_arg.isEmpty()) { // т.е. имеем либо вектор либо матрицу либо их сумму
            return (matrix_add == null) ? vec_add : (vec_add == null)
                    ? matrix_add : new F(F.ADD, new Element[] {
                        vec_add, matrix_add});
        }
        if (vec_add != null) {
            comut_arg.add(vec_add);
        }
        if (matrix_add != null) {
            comut_arg.add(matrix_add);
        }
        return new F(F.ADD, comut_arg.toArray(new Element[comut_arg.size()])); // по дефолту )
    }

    private Algebra SA_work_to_default(Element El, int pow) {
        switch (El.numbElementType()) {
            case Ring.F: {
                switch (((F) El).name) {
                    case F.ID:
                        return SA_work_to_default(((F) El).X[0], pow);
                    case F.ADD:
                        return SA_proc_ADD(((F) El).X);
                    case F.MULTIPLY:
                        return SA_proc_MULTIPLY(((F) El).X);
                    case F.SUBTRACT:
                        return SA_proc_SUBTRUCT(((F) El).X);
                    case F.intPOW:
                        return new Algebra(workToDefault(((F) El).X[0], ((F) El).X[1].intValue()).pow(pow, RING));
                    // case F.POW:  in the future )))))))))))))))))
                    default:
                        return new Algebra(workToDefault(El, pow));
                }
            }
            case Ring.Fname:
                if (Character.isUpperCase(((Fname) El).name.charAt(0))) {
                    fill_G_Vectors(El);
                    ArrayList<Element> coeff = new ArrayList<Element>();
                    ArrayList<Gterm> term = new ArrayList<Gterm>();
                    coeff.add(RING.numberONE());
                    term.add(new Gterm(new int[] {1, index_in_G_Ring, 1}));
                    return new Algebra(coeff, term);
                } else {
                    return new Algebra(workToDefault(El, pow));
                }
            case Ring.VectorS:
            case Ring.MatrixS:
            case Ring.MatrixD:
                fill_G_Vectors(El);
                ArrayList<Element> coeff = new ArrayList<Element>();
                ArrayList<Gterm> term = new ArrayList<Gterm>();
                coeff.add(RING.numberONE());
                term.add(new Gterm(new int[] {1, index_in_G_Ring, 1}));
                return new Algebra(coeff, term);
            default: // пришло чило
                return new Algebra(El);
        }
    }

    /**
     * Процедура переводящая уровень узла суммы в полином (F->P)
     *
     * @param ARGUMENT - аргументы узла суммы
     *
     * @return Polynom | Fraction
     */
    private Algebra SA_proc_ADD(Element[] ARGUMENT) {
        Algebra Res = SA_work_to_default(ARGUMENT[0], 1);
        for (int i = 1; i < ARGUMENT.length; i++) {
            Res = Res.add(SA_work_to_default(ARGUMENT[i], 1), RING);
        }
        return Res;
    }

    /**
     * Процедура переводящая уровень узла разность в полином (F->P)
     *
     * @param ARGUMENT - аргументы узла разность
     *
     * @return Polynom | Fraction
     */
    private Algebra SA_proc_SUBTRUCT(Element[] ARGUMENT) {
        return SA_work_to_default(ARGUMENT[0], 1).subtract(SA_work_to_default(ARGUMENT[1], 1), RING);
    }

    /**
     * Процедура переводящая уровень узла произведения в полином (F->P)
     *
     * @param ARGUMENT - аргументы узла произведения
     *
     * @return Polynom | Fraction
     */
    private Algebra SA_proc_MULTIPLY(Element[] ARGUMENT) {
        Algebra Res = (Algebra) new Algebra().one(RING);
        for (int i = 0; i < ARGUMENT.length; i++) {
            if (ARGUMENT[i] instanceof F) {
                switch (((F) ARGUMENT[i]).name) {
                    case F.ID:
                        Res = Res.multiply(SA_work_to_default(((F) ARGUMENT[i]), 1), RING);
                        break;
                    case F.MULTIPLY:
                        Res = Res.multiply(SA_proc_MULTIPLY(((F) ARGUMENT[i]).X), RING);
                        break;
                    case F.intPOW:
                        Res = Res.multiply(SA_work_to_default(((F) ARGUMENT[i]), ((F) ARGUMENT[i]).X[1].intValue()), RING);
                        break;
                    default:
                        Res = Res.multiply(SA_work_to_default(((F) ARGUMENT[i]), 1), RING);
                }
            } else {
                if (ARGUMENT[i] instanceof Polynom) {
                    if (((Polynom) ARGUMENT[i]).coeffs.length > 1) {
                        Res = Res.multiply(new Algebra(workToDefault(ARGUMENT[i], 1)), RING);
                    } else {
                        Res = Res.multiply(SA_work_to_default(((Polynom) ARGUMENT[i]), 1), RING);
                    }
                } else {
                    Res = Res.multiply(SA_work_to_default(ARGUMENT[i], 1), RING);
                }
            }
        }
        return Res; // выводим готовый результат
    }

    /**
     * Метод упрощающий верхний арифметический уровень , не содержащий
     * некоммутативных элементов. Все числовые типы находящиеся на верхнем
     * арифметическом уровне приводяться к более тяжелому.
     *
     * @param func
     * @param makeSUBTRACT - делать ли вычитание ( узел SUBTRACT) на выходе
     *
     * @return упрощенное выражение (не происходит не каких вычислений с
     * дробями, кроме работы в числители и знаменатели)
     */
    public Element SimplifyWithOutGether(Element func, boolean makeSUBTRACT) {
        if (func instanceof F) {
            makeNewVectFandVectEL();
            index_in_Ring = RING.varNames.length + 1;
            Element res = CleanElement(func);
            res = ElementToF(workSWOG(res));
            return (res instanceof F & makeSUBTRACT) ? MakeDivideAndSubtract((F) res, 0) : res;
        }
        return func;
    }

    private Element workSWOG(Element el) {
        if (el instanceof Fraction) {
            return workToDefault(el, 1);
        }
        switch (el.numbElementType()) {
            case Ring.F: {
                switch (((F) el).name) {
                    case F.ADD: {
                        Element res = workSWOG(((F) el).X[0]);
                        for (int i = 1; i < ((F) el).X.length; i++) {
                            res = res.add(workSWOG(((F) el).X[i]), RING);
                        }
                        return res;
                    }
                    case F.DIVIDE:
                        Element tempRes = myOpDiv(workSWG(((F) el).X[0]), workSWG(((F) el).X[1]));
                        tempRes = ElementToF(tempRes);
                        //  Element tempRes = new F(F.DIVIDE, new Element[]{workSWOG(((F) el).X[0]), workSWOG(((F) el).X[1])});
                        tempRes = CleanElement(tempRes);
                        return workToDefault(tempRes, 1);
                    case F.SUBTRACT:
                        return workSWOG(((F) el).X[0]).subtract(workSWOG(((F) el).X[1]), RING);
                    case F.MULTIPLY: {
                        Element res = workSWOG(((F) el).X[0]);
                        for (int i = 1; i < ((F) el).X.length; i++) {
                            res = res.multiply(workSWOG(((F) el).X[i]), RING);
                        }
                        return res;
                    }
                    case F.intPOW:
                        return workSWOG(((F) el).X[0]).pow(((F) el).X[1].intValue(), RING);
                    case F.EXP:
                        if (((F) el).X[0].isOne(RING)) {
                            Element e = CleanElement(new Fname("\\e"));
                            return workToDefault(e, 1);
                        }
                        return (((F) el).X[0].isZero(RING)) ? new Polynom(RING.numberONE()) : workToDefault(el, 1);
                    case F.SQRT:
                    case F.CUBRT:
                    case F.ROOTOF:
                        int nameOp = (((F) el).name == F.ROOTOF) ? ((F) el).X[1].intValue() : (((F) el).name == F.SQRT) ? 2 : 3;
                        if (((F) el).X[0].numbElementType() < Ring.Polynom) {
                            Element rootRes = ((F) el).X[0].rootOf(nameOp, RING);
                            return (rootRes == null) ? workToDefault(el, 1) : new Polynom(rootRes);
                        }
                        if (isComplexRing) {
                            return workToDefault(el, 1);
                        }
                        boolean polynomFlag = (((F) el).X[0] instanceof Polynom);
                        Polynom factor = (polynomFlag) ? (Polynom) ((F) el).X[0] : PolynomialConvert((F) ((F) el).X[0]);
                        FactorPol resFactor = FactorizationManyVar.Factor(factor, RING);
                        if (resFactor.powers.length == 1 & resFactor.powers[0] == 1) {
                            return workToDefault(el, 1);
                        }
                        Element tempRes_ = workWithPows(resFactor, nameOp, polynomFlag);
                        return (tempRes_ == null) ? workToDefault(el, 1) : tempRes_;
                    default:
                        return workToDefault(el, 1);
                }
            }
            case Ring.Fname:
                return workToDefault(el, 1);
            case Ring.Polynom:
                return el;
            default:
                return new Polynom(el);
        }
    }

    /**
     * Метод упрощающий верхний арифметический уровень , не содержащий
     * некоммутативных элементов. Все числовые типы находящиеся на верхнем
     * арифметическом уровне приводяться к более тяжелому.
     *
     * @param func
     * @param makeSUBTRACT - делать ли вычитание ( узел SUBTRACT) на выходе
     *
     * @return упрощенное выражение (результат одна дробь , или одно
     * произведение)
     */
    public Element SimplifyWithGether(Element func, boolean makeSUBTRACT) {
        if (func instanceof F) {
     //       System.out.println("workSWG========== SimplifyWithGether==="+func);
            Element eee=workSWG(func);
            Element res = ElementToF(eee);
            return (res instanceof F & makeSUBTRACT) ? MakeDivideAndSubtract((F) res, 0) : res;
        }
        return func;
    }

    Element workSWG(Element el) {
        if (el instanceof Fraction) {
            return myOpDiv(workSWG(((Fraction) el).num), workSWG(((Fraction) el).denom));
        }
        switch (el.numbElementType()) {
            case Ring.F: {
                switch (((F) el).name) {
                    case F.ID:
                        return workSWG(((F) el).X[0]);
                    case F.ADD: {
                        Element res = workSWG(((F) el).X[0]);
                        for (int i = 1; i < ((F) el).X.length; i++) {
                            res = myOpAdd(res, workSWG(((F) el).X[i]));
                        }
                        return res;
                    }
                    case F.DIVIDE:
                        return myOpDiv(workSWG(((F) el).X[0]), workSWG(((F) el).X[1]));
                    case F.SUBTRACT:
                        return myOpSub(workSWG(((F) el).X[0]), workSWG(((F) el).X[1]));
                    case F.MULTIPLY: {
                        Element res = workSWG(((F) el).X[0]); 
       //      System.out.println(0+ "  workSWG===mmmmm==res"+((F) el).X[0]+"  "+ res);
        //     System.out.println("CanonicForms.ListOfChanges="+ Array.toString( List_of_Change.toArray())); 
                        for (int i = 1; i < ((F) el).X.length; i++) {
       //      System.out.println(i+ "  workSWG===mmmmm=="+((F) el).X[i]);
        //     System.out.println("CanonicForms.ListOfChanges="+ Array.toString( List_of_Change.toArray())); 
                            res = myOpMul(res, workSWG(((F) el).X[i]));
   //     System.out.println(i+ "  workSWG===mmmmm==res="+res);
    //    System.out.println("CanonicForms.ListOfChanges="+ Array.toString( List_of_Change.toArray())); 
                        }
                        return res;
                    }
                    case F.POW: case F.intPOW:
//                        if (((F) el).X[1].isZero(RING)) {
//                            return new Polynom(RING.numberONE());
//                        } else if (((F) el).X[1].isOne(RING)) {
//                            return workToDefault(((F) el).X[0], 1);
//                            //return workToDefault(ElementToF(workSWG(((F) el).X[0])), 1);
//                        } else {
//                          //  Element temp=new F(F.POW, new Element[]{ElementToF(workSWG(((F) el).X[0])),ElementToF(workSWG(((F) el).X[1]))});
//                          //  temp=F.cleanOfRepeating(temp, vectF, vectEl, RING);
//                          //  return workToDefault(temp, 1);
                       F ff=powFSP(((F) el).X,false,true);  
//  System.out.println("CanonicForms.ListOfChanges="+ Array.toString( List_of_Change.toArray())); 
                       return  (ff.name==F.ID)? workToDefault(CleanElement(ff.X[0]), 1):
                               workToDefault(CleanElement(ff), 1);
                       case F.SQRT:case F.ABS:case F.CUBRT:
                              return workToDefault(((F) el),1); 
                       //                      } 
//                    case F.intPOW:
//                        if (((F) el).X[1].isZero(RING)) {
//                            return new Polynom(RING.numberONE());
//                        }
//                        return workSWG(((F) el).X[0]).pow(((F) el).X[1].intValue(), RING);
                    case F.EXP:
                        if (((F) el).X[0].isOne(RING)) {
                            Element e = CleanElement(new Fname("\\e"));
                            return workToDefault(e, 1);
                        }
                        return (((F) el).X[0].isZero(RING)) ? new Polynom(RING.numberONE()) : workToDefault(el, 1);
           //         case F.SQRT:
          //          case F.CUBRT:
                    case F.ROOTOF:
                        if (isComplexRing) {
                            return workToDefault(el, 1);
                        }
           //             if(Ring.CQ==RING.algebra[0] | Ring.CZ==RING.algebra[0] ){return workToDefault(el,1);}
                        int nameOp = (((F) el).name == F.ROOTOF) ? ((F) el).X[1].intValue() : (((F) el).name == F.SQRT) ? 2 : 3;
                        if (((F) el).X[0].numbElementType() < Ring.Polynom) {
                            Element rootRes = ((F) el).X[0].rootOf(nameOp, RING);
                            return (rootRes == null || (rootRes instanceof F)) ? workToDefault(el, 1) : new Polynom(rootRes);
                        }
                        boolean polynomFlag = (((F) el).X[0] instanceof Polynom);
                        Polynom factor = (polynomFlag) ? (Polynom) ((F) el).X[0]:
                                (((F) el).X[0] instanceof FactorPol) ? (Polynom)((FactorPol)((F) el).X[0]).toPolynomOrFraction(RING)
                                : PolynomialConvert((F) ((F) el).X[0]);
                        FactorPol resFactor = FactorizationManyVar.Factor(factor, newRing);
                        if (resFactor.powers.length == 1 & resFactor.powers[0] == 1) {
                            return workToDefault(el, 1);
                        }

                        Element tempRes = workWithPows(resFactor, nameOp, polynomFlag);
                        return (tempRes == null) ? workToDefault(el, 1) : tempRes;
                    default:
                        return workToDefault(el, 1);
                }
            }
            case Ring.FactorPol:
            case Ring.Fname:
                return  workToDefault(el, 1);
                //  return((((Fname)el).X!=null && ((Fname)el).X.length>0 && ((Fname)el).X[0]!=null))? workSWG(((Fname)el).X[0])
             //           :workToDefault(el, 1);
    //        case Ring.Polynom:
    //            return el;
            default:  return el;
      //          return Polynom.polynomFromNumber(el, RING);
        }
    }
/**     This is function: root of "pow"
 * Иизвлечение корня степени pow из произведения, хранящегося в FactorPol pol
 * @param pol FactorPol, который хранит подрадикальное выражение
 * @param pow степень корня (3 means  1/3)
 * @param isPolynom true означает, что pol это произведение исходных полиномов,
 *                  false означает, что pol это произведение полиномов, кодирующих функции
 * @return 
 */
    private Element workWithPows(FactorPol pol, int pow, boolean isPolynom) {
        ArrayList<Element> withoutroot = new ArrayList<Element>();
        ArrayList<Element> from_root = new ArrayList<Element>();
        double numPow = pow; // степень для извлечения  --??  pow это степень корня?? =3 для 1/3 ?
      //  double temp_pow;// текущая степень множителя
        double res;
        for (int i = 0; i < pol.powers.length; i++) {
            double res_pow = ((double) pol.powers[i] % numPow);
            if (res_pow != pol.powers[i]) {
                res = ((double) pol.powers[i] - res_pow) / numPow;
                if (isPolynom) {
                    if (res == 1) {
                        withoutroot.add(pol.multin[i]);
                    } else {
                        withoutroot.add(new F(F.intPOW, new Element[] {pol.multin[i], new NumberZ((int) res)}));
                    }
                    if (res_pow != 0) {
                        if (res_pow == 1) {
                            from_root.add(pol.multin[i]);
                        } else {
                            from_root.add(new F(F.intPOW, new Element[] {pol.multin[i], new NumberZ((int) res_pow)}));
                        }
                    }
                } else {
                    if (res == 1) {
                        withoutroot.add(ElementToF(pol.multin[i]));
                    } else {
                        withoutroot.add(new F(F.intPOW, new Element[] {ElementToF(pol.multin[i]), new NumberZ((int) res)}));
                    }
                    if (res_pow != 0) {
                        if (res_pow == 1) {
                            from_root.add(ElementToF(pol.multin[i]));
                        } else {
                            from_root.add(new F(F.intPOW, new Element[] {ElementToF(pol.multin[i]), new NumberZ((int) res_pow)}));
                        }
                    }
                }
            } else {
                if (isPolynom) {
                    if (pol.powers[i] == 1) {
                        from_root.add(pol.multin[i]);
                    } else {
                        from_root.add(new F(F.intPOW, new Element[] {pol.multin[i], new NumberZ(pol.powers[i])}));
                    }
                } else {
                    if (pol.powers[i] == 1) {
                        from_root.add(ElementToF(pol.multin[i]));
                    } else {
                        from_root.add(new F(F.intPOW, new Element[] {ElementToF(pol.multin[i]), new NumberZ(pol.powers[i])}));
                    }
                }
            }
        }
        if (withoutroot.isEmpty()) {
            return null;
        }
        double chet = numPow % 2;
        Element root;
        if (from_root.isEmpty()) {
            if (chet == 0) {
                root = new F(F.ABS, new Element[] {new F(F.MULTIPLY, withoutroot.toArray(new Element[withoutroot.size()]))});
                return workToDefault(CleanElement(root), 1);
            } else {
                root = new F(F.MULTIPLY, withoutroot.toArray(new Element[withoutroot.size()]));
                return workToDefault(CleanElement(root), 1);
            }
        }
        if (chet == 0) {
            if (numPow == 2) { //т.е. \\sqrt
                Element wor = new F(F.ABS, new Element[] {new F(F.MULTIPLY, withoutroot.toArray(new Element[withoutroot.size()]))});
                Element fr = new F(F.SQRT, new Element[] {new F(F.MULTIPLY, from_root.toArray(new Element[from_root.size()]))});
                Element res_ = new F(F.MULTIPLY, new Element[] {wor, fr});
                return workToDefault(CleanElement(res_), 1);
            }
            Element wor = new F(F.ABS, new Element[] {new F(F.MULTIPLY, withoutroot.toArray(new Element[withoutroot.size()]))});
            Element fr = new F(F.ROOTOF, new Element[] {new F(F.MULTIPLY, from_root.toArray(new Element[from_root.size()])), new NumberZ((int) numPow)});
            Element res_ = new F(F.MULTIPLY, new Element[] {wor, fr});
            return workToDefault(CleanElement(res_), 1);
        } else {
            if (numPow == 3) { //т.е. \\cubrt
                Element wor = new F(F.MULTIPLY, withoutroot.toArray(new Element[withoutroot.size()]));
                Element fr = new F(F.CUBRT, new Element[] {new F(F.MULTIPLY, from_root.toArray(new Element[from_root.size()]))});
                Element res_ = new F(F.MULTIPLY, new Element[] {wor, fr});
                return workToDefault(CleanElement(res_), 1);
            }
            Element wor = new F(F.MULTIPLY, withoutroot.toArray(new Element[withoutroot.size()]));
            Element fr = new F(F.ROOTOF, new Element[] {new F(F.MULTIPLY, from_root.toArray(new Element[from_root.size()])), new NumberZ((int) numPow)});
            Element res_ = new F(F.MULTIPLY, new Element[] {wor, fr});
            return workToDefault(CleanElement(res_), 1);
        }
    }

    private F sort_proc(Element[] Args, boolean Up, int nameFunc) {
        int[] pos = (Up) ? Array.sortPosUp(Args, RING) : Array.sortPosDown(Args, RING);
        Element[] res = new Element[Args.length];
        for (int i = 0; i < pos.length; i++) {
            res[i] = Args[pos[i]];
        }
        return new F(nameFunc, res);
    }

    /**
     * Сортируем аргументы арифмитического уровня через compareTo.
     * @param func - арифметический уровень
     * @param Up - true ( от меньшего к большему) false ( от большего к меньшему)
     * @return
     */
    public F sortByName(F func, boolean Up) {
        switch (func.name) {
            case F.ADD:
            case F.MULTIPLY:
                Element[] res = new Element[func.X.length];
                for (int i = 0; i < func.X.length; i++) {
                    res[i] = (func.X[i] instanceof F) ? sortByName((F) func.X[i], Up) : func.X[i];
                }
                return sort_proc(res, Up, func.name);
            case F.LOG:
            case F.intPOW:
            case F.POW:
            case F.DIVIDE:
            case F.SUBTRACT: {
                Element oneArg = (func.X[0] instanceof F) ? sortByName((F) func.X[0], Up) : func.X[0];
                Element twoArg = (func.X[1] instanceof F) ? sortByName((F) func.X[1], Up) : func.X[1];
                return new F(func.name, new Element[] {oneArg, twoArg});
            }
        }
        return func;
    }

    /**
     * Считаем "завернутые индексы" вида - i+j+k+const - , где i,j,k - Fname,
     * Polynom | F.
     * @param func - индекс для подсчета
     * @return значение индекса
     */
    public Element indexValOF(Element func, List<Element> expr) {
        if (func==null) return Element.NAN;
        if(func instanceof VectorS) {VectorS v=(VectorS)func;
            VectorS res= new VectorS(v.V.length);
            for (int i = 0; i < v.V.length; i++) {
                res.V[i]=indexValOF(v.V[i],expr);
            } return res;
        }else{
        switch (func.numbElementType()) {
            case Ring.F: {
                Element res = indexValOF(((F) func).X[0],expr);
                switch (((F) func).name) {
                    case F.ADD:
                        for (int i = 1; i < ((F) func).X.length; i++) {
                            res = res.add(indexValOF(((F) func).X[i],expr), RING);
                        }
                        break;
                    case F.MULTIPLY:
                        for (int i = 1; i < ((F) func).X.length; i++) {
                            res = res.multiply(indexValOF(((F) func).X[i],expr), RING);
                        }
                        break;
                    case F.SUBTRACT:
                        res = res.subtract(indexValOF(((F) func).X[1],expr), RING);
                        break;
                    case F.DIVIDE:
                        res = res.divide(indexValOF(((F) func).X[1],expr), RING);
                        break;
                }
                return  indexValOF(res,expr);
            }
            case Ring.Fname:
                Fname fn=(Fname) func; Element nn=null;
                if(fn.isEmpty(RING))
                     {nn=  substituteValueToFnameAndCorrectPolynoms(fn,expr);
                      if(nn==fn) return fn;}
                else  nn= fn.X[0];
                if(nn==null) throw new MathparException("Unknown variable '" + fn + " in canonicF 6242");
                 
                return indexValOF(nn,expr);
            case Ring.Polynom:
                return (((Polynom) func).coeffs.length == 0) ? RING.numberZERO : ((Polynom) func).coeffs[0];
            default: // пришло число
                return func;
        }
      }
    }
    /**
     * Метод осуществляющий извлечение целых чисел (индексов),
     * из "сложных" объектов или векторов
     * @param el - сложный объкт или вектор
     * @return целое число или вектор из целых чисел
     */
    private Element getIntegerValue(Element el, List<Element> expr) {
        if (el == null) return Element.NAN;
        if (el instanceof VectorS) {
            VectorS v = (VectorS) el;
            VectorS res = new VectorS(v.V.length);
            for (int i = 0; i < v.V.length; i++) {
                res.V[i] = getIntegerValue(v.V[i],expr);
            }
            return res;
        } else {
            Element tempI = expandFnameOrId(el);
            switch (tempI.numbElementType()) {
                case Ring.Fname:  //if(((Fname)tempI).X==null)return Element.NAN;
                case Ring.Polynom:
                case Ring.F:      
                    tempI = indexValOF(tempI,expr);
            }
            return ((tempI.numbElementType() < Ring.Polynom)||
                     ((tempI.numbElementType()==Ring.Fname)&&(((Fname)tempI).isEmpty(RING)))) ?
                   tempI : getIntegerValue(tempI,expr);
        }
    }

    /**
     * Метод выполняющий работу с выражениями вида A_{i,...,j}=F(x,y)
     * приходящими из Page.
     * @param left - левая часть
     * @param rigth - правая часть
     */
    public Element workWithMatrixs(Element left, Element rigth, List<Element> expr) {
        Fname ffn=((Fname) left);
        Element ELleft = expandFnameOrIdOrElOf(ffn.X[0]);
        Element ElRigth = expandFnameOrIdOrElOf(rigth);
        if (ELleft == null)  return null;
        Element[] lowIndexs=ffn.lowerIndices();
        if (ELleft instanceof MatrixD) {// MATRIXD +++++++++++
            MatrixD mat = ((MatrixD) ELleft);
            if (lowIndexs[0] != null && lowIndexs[1] != null) {
                Element tempJ = getIntegerValue(lowIndexs[1],expr);//, left);
                int indexJ;
                if (tempJ == null) {return null;} else {indexJ = tempJ.intValue() - 1; }
                Element tempI = getIntegerValue(lowIndexs[0], expr);//, left);
                int indexI;
                if (tempI == null) {return null;} else {indexI = tempI.intValue() - 1; }
                if ((indexJ < 0) || (indexI < 0)) {  
                    try {throw new Exception(RING.exception.append("Negative or zero index in ").append(left).toString());
                    } catch (Exception ex) {
                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (indexI >= mat.M.length | indexJ >= mat.M[0].length) {
                    try {
                        throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(left).toString());
                    } catch (Exception ex) {
                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mat.putElement(ElRigth, indexI, indexJ); 
                return  ElRigth;// new MatrixD(mat, ((MatrixD) ELleft).fl); !!!!!!!!!!!!!!
            }
            // ----если есть оба индекса, то их выделили, проверили их значение и ЗАПИСАЛИ ЭЛЕМЕНТ В МАТРЦУ. --------------------
            if (lowIndexs[1] != null) { // только один из двух инднксов есть.
                Element tempJ = getIntegerValue(lowIndexs[1],expr);//, left);
                int indexJ;
                if (tempJ == null) {return null; } else {indexJ = tempJ.intValue() - 1; }
                if (indexJ < 0) {
                    try {throw new Exception(RING.exception.append("Negative or zero index in ").append(left).toString());
                    } catch (Exception ex) {
                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (indexJ >= mat.M.length) {
                    try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(left).toString());
                    } catch (Exception ex) {Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (ElRigth instanceof VectorS) {
                    int leng = (mat.M.length > ((VectorS) ElRigth).V.length) ? ((VectorS) ElRigth).V.length : mat.M.length;
                    for (int i = 0; i < leng; i++) {
                        mat.M[i][indexJ] = ((VectorS) ElRigth).V[i];
                        //  mat.putElement(((VectorS)ElRigth).V[i], i, indexJ);
                    }
                    if (mat.M.length - ((VectorS) ElRigth).V.length != 0) {
                        RING.exception.append("Diffrent lengths  ").append(((Fname) left).name).append("  and  ").append(ElRigth);
                    }
                    return new MatrixD(mat, ((MatrixD) ELleft).fl);
                } else { mat.putElement(ElRigth, 0, indexJ);
                    return new MatrixD(mat, ((MatrixD) ELleft).fl);
                } // заменили столбец на столбец, который был в vectorе-аргументе
            }
            if (lowIndexs[0] != null) {  // только один другой из двух инднксов есть.
                Element tempI = getIntegerValue(lowIndexs[0],expr);//, left);
                int indexI;
                if (tempI == null) { return null;} else {indexI = tempI.intValue() - 1; }
                if (indexI < 0) {try { throw new Exception(RING.exception.append("Negative or zero index in ").append(left).toString());
                    } catch (Exception ex) {
                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }}
                if (indexI >= mat.M[0].length) {
                    try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(left).toString());
                    } catch (Exception ex) {
                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }}
                if (ElRigth instanceof VectorS) {
                    int leng = (mat.M[indexI].length > ((VectorS) ElRigth).V.length) ? ((VectorS) ElRigth).V.length : mat.M[indexI].length;
                    System.arraycopy(((VectorS) ElRigth).V, 0, mat.M[indexI], 0, leng);
                    if (mat.M[indexI].length - ((VectorS) ElRigth).V.length != 0) {
                        RING.exception.append("Diffrent lengths  ").append(((Fname) left).name).append("  and  ").append(ElRigth);
                    }
                    return new MatrixD(mat, ((MatrixD) ELleft).fl);
                    // --- заменили в матрице строку c номером indexI на аргумент - vector
                } else {
                    mat.putElement(ElRigth, indexI, 0);
                    return new MatrixD(mat, ((MatrixD) ELleft).fl);
                }
            } //  конец случая с матрицей
        } else {
          if(ELleft instanceof Table){ // TABLE ++++++++++++++++++++++++++++++++++++++++++=
                Table  table= ((Table) ELleft);
                Element tempI = getIntegerValue(lowIndexs[0],expr);//, left);
                int indexI;
                if (tempI == null) {return null;} else {indexI = tempI.intValue() - 1;}
                if (indexI < 0) {try {
                        throw new Exception(RING.exception.append("Negative or zero index in ").append(left).toString());
                    } catch (Exception ex) {
                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }}
                if (indexI >= table.tableSize()) { try {
                        throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(left).toString());
                    } catch (Exception ex) {
                        Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                    }}
                table.setFunction(rigth, indexI);
                return table;
            }
            // иначе вектор +++++++++++++++++++++++++++++++++++++
           VectorS vec=null;
            if(ELleft instanceof VectorS){vec = ((VectorS) ELleft);}else
                System.out.println("  ##########=="+ ELleft);
            
            if (lowIndexs== null) return null;
            Element tempI =  getIntegerValue(lowIndexs[0],expr);//, left);
            int indexI;
            if (tempI == null) return null;  else   indexI = tempI.intValue() - 1;
            if (indexI < 0) {
                try { throw new Exception(RING.exception.append("Negative or zero index in ").append(left).toString());
                } catch (Exception ex) {
                    Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                }}
            if (indexI >= vec.V.length) {  
                try {throw new Exception(RING.exception.append("ArrayIndexOutOfBounds ").append(left).toString());
                } catch (Exception ex) {
                    Logger.getLogger(CanonicForms.class.getName()).log(Level.SEVERE, null, ex);
                }}
            vec.V[indexI] = ElRigth;
            return  ElRigth; 
        }
        return null;
    }

   /**
    * Корректируем выражения типа 0-Element, Element -0 , Element+0 & etc..
    * @param f любой наследник элемента но интерес представляют объекты типа F
    * @return
    */
    private Element delete_unnecessary_wraps(Element f){
     switch(f.numbElementType()){
         case Ring.F:
          switch(((F)f).name){
              case F.ID:
               return delete_unnecessary_wraps(((F)f).X[0]);
              case F.ADD:
              ArrayList<Element> newArg=new ArrayList<Element>();
              for(Element el : ((F)f).X ){
               if(!el.isZero(RING)) newArg.add(delete_unnecessary_wraps(el));
              }
              return (newArg.isEmpty())? RING.numberZERO : (newArg.size()==1) ? newArg.get(0) : new F(F.ADD, newArg.toArray(new Element[newArg.size()]));
              //case F.MULTIPLY:
              case F.SUBTRACT:
              return(((F)f).X[0].isZero(RING)) ? delete_unnecessary_wraps(((F)f).X[1]).negate(RING): (((F)f).X[1].isZero(RING)) ? delete_unnecessary_wraps(((F)f).X[0]) : f;
              default:
              return f;
          }
         case Ring.Fname:
             if(((Fname)f).X==null || ((F)f).X.length==0 || ((F)f).X[0]==null) return f;
             return delete_unnecessary_wraps(((Fname)f).X[0]);
         default:
         return f;
     }
    }



//    /**
//     * Метод добавляет новую переменную(или нет если она есть) написано Шляпиным
//     * Дмитрием. НЕ УДАЛЯТЬ!!!!!!!!
//     * @param num
//     * @return
//     */
//    public Ring addNewVariable1(int num) {
//        //Отсекаем последние два символа в строковом представлении кольца,
//        String s = RING.toString().substring(0, RING.toString().length() - 1);
//        if (num == 1) {
//            return new Ring(s + ",c$_0]"); // если нужна одна новая переменная
//        }        // для всего остального и необъятного
//        s += ",";
//        String A = "c$_";
//        for (int i = 0; i < num - RING.varNames.length - 2; i++) {
//            s += A + i + ",";
//        }
//        s += A + (num - RING.varNames.length - 2) + "]";
//        return new Ring(s);
//    }

/**
 * Проверяем какие переменные присутствуют в полиноме p.
 * @param p полином  для проверки
 * @param size предполагаемый размер массива степеней одного монома (колл-во переменных в полиноме)
 * @return массив типа int каждый элемент которого не равный 0 указывает на наличие такой переменной с тем же индексом (в соответствии с лексиграфическим порядком в кольце) в полиноме.
 */
    public int[] variables(Polynom p, int size) {
        int b = p.powers.length;
        int d = p.powers.length / p.coeffs.length;
        if (d > size) {
            size = d;
        }
        int[] u = new int[size];
        if (d != 0) {
            for (int i = 0; i < d; i++) {
                for (int r = i; r < b; r += d) {
                    if (p.powers[r] > 0) {
                        u[i] = 1;
                        break;
                    }
                }
            }
        }
        return u;
    }
   /**
    * Очищаем элемент в соответствии со списками vectF, vectEl . После очистки элемент будет либо добавлен 
    * при отсутствии такого же в списках
    * либо заменен частично( если это дерево функции) либо полностью, если есть такой же , из списков. 
    * Тем самым один и тот же объект будет указывать на одну облать в памяти  и их можно сравнивать 
    * чере опереатор ==.
    * @param el  элемент который необходимо очистить
    * @return очищенный элемент el.
    */
    public Element CleanElement(Element el){
      return F.cleanOfRepeating(el, RING);
    }
    /**
     * Проверяем использовалось ли имя name
     * @param name имя которое необходимо проверить в списке gr_vec_words. СПИСОК Greek names..
     */
    public void chek_name_in_gr_works(String name){
        int index=gr_vec_words.indexOf(name);
        if(index!=-1) {
            indexs_free_gr_words[index]=1;
        }
    }


 


    /**
     * Для вызова этой процедуры необходимо наличие в руках CanonicForms c заполнеными векторами
     * @param el - элемент для упрощения
     * @param deleteNamesToFname - уничожаем имена у Fname
     * @return
     */
    public Element simplifyWithPow(Element el, boolean deleteNamesToFname){
     el=CleanElement(el);
     return ElementToF(helpSimplifyWithPow(el, deleteNamesToFname));
    }

    private Element  helpSimplifyWithPow(Element el, boolean deleteNamesToFname){
      switch(el.numbElementType()){
          case Ring.F:
          switch(((F)el).name){
              case F.ID:
              return  helpSimplifyWithPow(((F)el).X[0], deleteNamesToFname);
              case F.ADD:
              Element res=helpSimplifyWithPow(((F)el).X[0], deleteNamesToFname);
              for(int i=1;i<((F)el).X.length;i++){
              res=res.add(helpSimplifyWithPow(((F)el).X[i], deleteNamesToFname), RING);
              }
              return res;
              case F.SUBTRACT:
              return helpSimplifyWithPow(((F)el).X[0], deleteNamesToFname).subtract(helpSimplifyWithPow(((F)el).X[1], deleteNamesToFname), RING);
              case F.MULTIPLY:
              Element resM=helpSimplifyWithPow(((F)el).X[0], deleteNamesToFname);
              for(int i=1;i<((F)el).X.length;i++){
              resM=resM.multiply(helpSimplifyWithPow(((F)el).X[i], deleteNamesToFname), RING);
              }
              return resM;
              case F.DIVIDE:
              return helpSimplifyWithPow(((F)el).X[0], deleteNamesToFname).divide(helpSimplifyWithPow(((F)el).X[1], deleteNamesToFname), RING);
              case F.SQRT:
              Element elSQ=new F(F.POW,new Element[]{((F)el).X[0],RING.numberONE.divide(RING.numberONE.valOf(2, RING), RING)});
              elSQ=CleanElement(elSQ);
              return workToDefault(elSQ, 1);
              case F.CUBRT:
              Element elCU=new F(F.POW,new Element[]{((F)el).X[0],RING.numberONE.divide(RING.numberONE.valOf(3, RING), RING)});
              elCU=CleanElement(elCU);
              return workToDefault(elCU, 1);
              case F.ROOTOF:
              Element elRO=new F(F.POW,new Element[]{((F)el).X[0],RING.numberONE.divide(RING.numberONE.valOf(((F)el).X[1].intValue(), RING), RING)});
              elRO=CleanElement(elRO);
              return workToDefault(elRO, 1);
              case F.intPOW:
              return workToDefault(helpSimplifyWithPow(((F)el).X[0], deleteNamesToFname), ((F)el).X[1].intValue());
              default: //для разделителей арифметических уровней
              Element [] simplifyArg=new Element[((F)el).X.length];
              for(int i=0; i<simplifyArg.length;i++){
              simplifyArg[i]=helpSimplifyWithPow(((F)el).X[i], deleteNamesToFname);
               }
              Element newArg=CleanElement(new F(((F)el).name,simplifyArg));
              return workToDefault(newArg, 1);
            }
          case Ring.Fname:
              if(((Fname)el).X==null | ((Fname)el).X[0]==null) return workToDefault(el, 1);
              if(deleteNamesToFname) return helpSimplifyWithPow(((Fname)el).X[0], deleteNamesToFname);
              Element tempF=CleanElement(new Fname(((Fname)el).name, ElementToF(helpSimplifyWithPow(((Fname)el).X[0],deleteNamesToFname))));
              return workToDefault(tempF, 1);
          case Ring.Rational:
              return  new Fraction(helpSimplifyWithPow(((Fraction)el).num,deleteNamesToFname),helpSimplifyWithPow(((Fraction)el).denom,deleteNamesToFname)).cancel(RING);
          case Ring.Polynom:
          default:
          return el;
      }
    }


    public Element elementToSimplifyWithPow(Element El) {
        if (El == null) { return null;}
        switch (El.numbElementType()) {
            case Ring.Polynom:
                return Unconvert_Polynom_to_Element(El);
            case Ring.Q:
            case Ring.Rational: {
                Fraction Res = (Fraction) El;
                if (Res.num.numbElementType() == Ring.Polynom && Res.denom.numbElementType() == Ring.Polynom) {
                    if (((Polynom) Res.num).isItNumber() & ((Polynom) Res.denom).isItNumber()) {
                        return (RING.algebra[0] == Ring.Z) ? Res.cancel(RING) : ((Polynom) Res.num).divide(((Polynom) Res.denom), RING);
                    }
                }
                if (Res.num.numbElementType() < Ring.Polynom && Res.denom.numbElementType() < Ring.Polynom) {
                    return (RING.algebra[0] == Ring.Z) ? Res.cancel(RING) : (Res.num).divide(Res.denom, RING);
                }
                if (Res.denom.isOne(RING)) {
                    return ElementToF(Res.num);
                }
                return new F(F.DIVIDE, new Element[] {
                            ElementToF(Res.num),
                            ElementToF(Res.denom)});
            }
              default:
                return El;
        }
    }

    /**
     * Get Positions in newRing of these functions in f_mas
     * @param f_mas - имена функций,  
     * @return список позиций переменных в newRing, которые имеются в f_mas
     */
   public int[] getPosInNewRingTheseFuncs(int[] f_mas) {
       int[] res=getPosInListOfChTheseFuncs(f_mas);
       for (int i = 0; i < res.length; i++) {res[i]+=RING.varNames.length;}
       return res;
   }
    /**
     * Get Positions in the ListOfChanges of these functions in f_mas
     * @param f_mas - имена функций,  
     * @return список функций в g.List_of_Change, которые имеются в f_mas
     *        (при добавлении g.RING.varName.length получим позиции в новом кольце)
     */
    public int[] getPosInListOfChTheseFuncs(int[] f_mas) {
        int sizeLofCh = this.List_of_Change.size();
        int[] list = new int[sizeLofCh];
        for (int i = 0; i < sizeLofCh; i++) {list[i] = 0; }
        int flagNumbs = 0;
        for (int i = 0; i < sizeLofCh; i++) {
            for (int j = 0; j < f_mas.length; j++) {
                Element getI = this.List_of_Change.get(i);
                if (!(getI instanceof F)) { continue; }
                if (((F) getI).name == f_mas[j]) {
                    list[i] = 1; flagNumbs++; break;
                }
            }
        }
        int[] res = new int[flagNumbs];
        if (flagNumbs == 0) { return res;  }
        int j = 0;
        for (int i = 0; i < sizeLofCh; i++) {
            if (list[i] == 1) { res[j++] = i; }
        }
        return res;
    }
    
        public static void main(String[] args) {
      
        Ring ring = new Ring("C64[x,y]"); ring.page=new Page(ring);
        F g= new F(F.intPOW, new Polynom("x^2+1",ring), new NumberZ64(2));
        Polynom pol= new Polynom("(x^2+1)^2",ring);
        F f = new F(F.ID, pol); 
        F h= new F("(x^2+1)^2",ring);
        Element ee =ring.CForm.simplify_init(g) ;
        System.out.println("with intPow:"+ee.toString(ring));
        ee =ring.CForm.simplify_init(f) ;
        System.out.println("with Polynom from Parser:"+ee.toString(ring));
        ee =ring.CForm.simplify_init(h) ;
        System.out.println("with F from Parser:"+ee.toString(ring));
    
        
        Element a=new F(" (\\exp(x)+\\exp(-x)",ring);
        a=a.factorLnExp(ring);
        System.out.println("" +a.toString(ring));



    //    System.out.println(" "+System.getProperty ("user.home")); (( (\\sqrt(a^2+b^2))^2))


        // System.get\\sqrt
    }         
}
