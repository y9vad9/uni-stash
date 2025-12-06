// © Разработчик Смирнов Роман Антонович (White_Raven), ТГУ'10.
package com.mathpar.number;

import com.mathpar.func.F;
import com.mathpar.polynom.Polynom;

import java.util.Arrays;
import java.util.Vector;

/**
 * @author White_Raven
 */
public class Factor extends Element {
    private static final long serialVersionUID = -7102341128606142587L;
    public Vector<Element> multin = new Vector<Element>();
    public Vector<Element> powers = new Vector<Element>();
    //  private static final NumberZ powOne= NumberZ.ONE; // степень равная единице
    private static final Polynom MinusOne = new Polynom(new int[]{}, new Element[]{NumberZ.MINUS_ONE});
    private static final Polynom powOne = new Polynom(new int[]{}, new Element[]{NumberZ.ONE}); // степень равная единице

    /**
     * Пустой конструктор
     */
    public Factor() {
    }

    public Factor(Vector<Element> m, Vector<Element> p) {
        this.multin = m;
        this.powers = p;
    }

    /**
     * Конструктор Factora из массива Elements и их масива их степеней -- pow
     *
     * @param el
     * @param pow
     */
    public Factor(Element[] el, int[] pow) {
        this.multin = new Vector<>(Arrays.asList(el));
        for (int i = 0; i < pow.length; i++) {
            this.powers.add(i, new NumberZ(pow[i]));
        }
    }

    /**
     * Конструктор от любого элемента
     *
     * @param f - входной элемет
     */
    public Factor(Element f, Ring ring) {
        fill_FactorElement(f);
        normalForm(ring);
    }

    void fill_F(F f) {
        multin.add(f);
        powers.add(powOne);
    }

    void fill_F_pow(F f) {
        multin.add(f.X[0]);
        powers.add(f.X[1]);
    }

    void fill_El(Element t) {
        multin.add(t);
        powers.add(powOne);
    }

    void fill_FactorElement(Element f) {
        if (f instanceof F) {
            switch (((F) f).name) {
                case F.intPOW:
                    fill_F_pow((F) f);
                    break;
                case F.MULTIPLY:
                    for (Element el : ((F) f).X) {
                        fill_FactorElement(el);
                    }
                    break;
                default:
                    fill_F((F) f);
            }
        } else {
            fill_El(f);
        }
    }

    public Element toF(Ring ring) {
        if (multin.size() == 0) {
            return NumberZ.ZERO;
        }

        if (multin.size() == 1) {
            return (powers.firstElement().isOne(ring)) ? multin.firstElement() : new F(F.POW, new Element[]{multin.firstElement(), powers.firstElement()});
        }
        Element[] X = new Element[multin.size()];
        for (int el = 0; el < powers.size(); el++) {
            if (powers.elementAt(el).isOne(ring)) {
                X[el] = multin.elementAt(el);
            } else {
                X[el] = new F(F.POW, multin.elementAt(el), powers.elementAt(el));
            }
        }
        return new F(F.MULTIPLY, X);
    }

    void clearAllArgs() {
        multin.removeAllElements();
        powers.removeAllElements();
    }

    Element[] CopyVecToArray(Vector<Element> vec) {
        Element[] X = new Element[vec.size()];
        vec.copyInto(X);
        return X;
    }

    void sortByArrayInt(int[] index, Vector<Element> vecm, Vector<Element> vecp) {
        Vector<Element> mulltin = new Vector<Element>();
        Vector<Element> power = new Vector<Element>();
        for (int i = 0; i < index.length; i++) {
            if (index[i] >= 0) {
                mulltin.add(vecm.elementAt(index[i]));
                power.add(vecp.elementAt(index[i]));
            }
        }
        vecm.removeAllElements();
        vecp.removeAllElements();
        vecm.addAll(mulltin);
        vecp.addAll(power);
    }

    public Vector<Element> get_multin() {
        return multin;
    }

    public Vector<Element> get_powers() {
        return powers;
    }

    public Factor getPozitivePart() {
        if (isNegative()) {
            Vector<Element> newMultin = new Vector<Element>();
            Vector<Element> newPower = new Vector<Element>();
            for (int i = 1; i < multin.size(); i++) {
                newMultin.add(multin.elementAt(i));
                newPower.add(powers.elementAt(i));
            }
            return new Factor(newMultin, newPower);
        }
        return this;
    }

    public int compareTo(Factor f, Ring ring) {
        int len = multin.size() - 1;
        int len2 = f.multin.size() - 1;
        while (len >= 0 && len2 >= 0) {
            int compare = multin.elementAt(len).compareTo(f.multin.elementAt(len2), ring);
            if (compare != 0) {
                return compare;
            } else {
                int compPow = powers.elementAt(len).compareTo(f.powers.elementAt(len2), ring);
                if (compPow != 0) {
                    return compPow;
                }
            }
            len--;
            len2--;
        }
        if (len == len2) {
            return 0;
        } else if (len >= 0) {
            return multin.elementAt(len).compareTo(f.multin.firstElement(), ring);
        } else {
            return -1;
        }
    }

    @Override
    public Boolean isNegative() {
        return multin.firstElement().isNegative();
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        return (multin.size() == 1 && multin.firstElement().isMinusOne(ring));
    }

    @Override
    public Boolean isOne(Ring ring) {
        return (multin.size() == 1 && multin.firstElement().isOne(ring));
    }

    @Override
    public int numbElementType() {
        return com.mathpar.number.Ring.Factor;
    }

    /**
     * Приведение к номальной форме (самый общий случай):
     * Все сомножители отсортированы по возростанию,
     * нет повторяющихся оснований и нет сомножителя равного 1.
     * На первом месте может стоять сомножитель (-1)
     * Старший коэффициент у всех сомножителей положительный.
     * Например:
     */
    public void normalForm(Ring ring) {
        int len = powers.size();
        if (len == 0) {
            return;// пустой список возвращаем
        }
        int sign = 1; // sign of the product
        // определяем знак и приводим старшие коеффициенты к положительному знаку.
        Vector<Element> NewMultil = new Vector<Element>();
        Vector<Element> NewPowers = new Vector<Element>();
        NewPowers.addAll(powers);
        for (int pos = 0; pos < len; pos++) {
            if (multin.elementAt(pos).isNegative()) {
                sign *= -1;
                NewMultil.add(multin.elementAt(pos).negate(ring));
            } else {
                NewMultil.add(multin.elementAt(pos));
            }
        }
        Element[] X = CopyVecToArray(NewMultil); // заполняем наш массив для сортировки
        int[] nP = com.mathpar.number.Array.sortPosUp(X, ring); // сортируем  по возрастанию
        // Складываем степени у соседних одинаковых ,
        // а позиции исчезающих  помечаем (-1)
        int j = 0;
        int k = 0;
        if (len > 1) {
            while ((j < len)) {
                k = j + 1;
                int pos = nP[j];
                while ((k < len) && (NewMultil.elementAt(pos).compareTo(NewMultil.elementAt(nP[k]), ring) == 0)) {
                    Element pow = NewPowers.elementAt(pos).add(NewPowers.elementAt(nP[k]), ring);
                    NewPowers.remove(pos);
                    NewPowers.add(pos, pow);
                    nP[k] = -1;
                    k++;
                }
                j = k;
            }
        }
        for (j = 0; j < len; j++) {
            int pos = nP[j];
            if (pos == -1) {
                continue;
            }
            if (NewMultil.elementAt(pos).isOne(ring) | NewPowers.elementAt(pos).isZero(ring)) {
                nP[j] = -1;
            }

        }
        if (nP.length == 1 && nP[0] == -1) {
            nP[0] = 0;
        }
        sortByArrayInt(nP, NewMultil, NewPowers); // изменяем в том же порядке наши аргументы
        if (sign < 0) {
            NewMultil.add(0, NumberZ.MINUS_ONE);
            NewPowers.add(0, powOne);
        }
        clearAllArgs(); // очищаем наши поля
        multin.addAll(NewMultil);
        powers.addAll(NewPowers);
    }

    /**
     *  Нормальная форма для полиномов над полем.
     *  Числовой множитель может быть только один - число со знаком и степенью 1.
     */
    /*     public void normalFormInField(){
    normalForm();// Приведем к общей нормальной форме
    int len=powers.numberOfVar(); if (len==0) return;//Пустой список возвращаем
    if (multin.firstElement() instanceof F) return;//Если нет числовых сомножителей -выходим
    int i=0;
    if (isNegative()) i++;
    Element mul= multin.firstElement();
    Element num=mul.myOne(), denom=mul.myOne();
    Element pow= powers.firstElement();
    Element pownum=pow.myZero(); Element powdenom=pow.myZero();
    // Соберем числитель и знаменатель числового дробного сомножителя
    for (;i<len;i++) {
    if (!(multin.elementAt(i)instanceof F) && (Ring.numbElementType(powers.elementAt(i)) <15))
    { if(powers.elementAt(i).isNegative()){
    denom=denom.multiply(multin.elementAt(i).pow(i));

    }
    else  num=num.multiply(multin[i].coeffs[0].pow(power));

    else break;
    } if (i==1) return;//Если только один числовой сомножитель -выходим
    Element number=num.divide(denom);
    int numbAdd=0; if (!number.isOne()) numbAdd=1;
    int newLen=len-i+numbAdd;
    Polynom[] newMultin=new Polynom[newLen];
    int[] newPowers=new int[newLen];
    int j=0; // pointer for new arrays
    if (numbAdd==1){ // The first will be number "number"
    newMultin[0]=Polynom.polynomFromNumber(number); newPowers[0]=1; j=1;}
    for (int m=i;m<len;m++) {newMultin[j]=multin[m]; newPowers[j++]=powers[m];}
    this.multin= newMultin; this.powers=newPowers;
    }

     */

    /**
     * Вывод в строку
     *
     * @return
     */
//    @Override
//    public String toString(Ring r) {
//      StringBuilder res = new StringBuilder();
//      for (int i = 0; i < multin.size(); i++) {
//            Element el=multin.elementAt(i);
//            Element pow=powers.elementAt(i);
//            if(!el.isOne(r)){res.append(el.toString(r));
//               if(!pow.isOne(r))res.append("^{").append(pow.toString(r)).append("} ");
//            }
//        }
//      String ss=res.toString(); if (ss.equals("")) ss="1";
//        return res.toString();
//    }
    @Override
    public String toString(Ring R) {
        String S = "";
        for (int k = 0; k < multin.size(); k++) {
            if (!powers.get(k).isZero(R)) {
                S = S + '(' + multin.get(k).toString(R) + ')';
                if (!powers.get(k).isOne(R)) {
                    S = S + '^' + powers.get(k);
                }
            }
        }
        if (S.equals("")) {
            return "1";
        } else {
            return S;
        }
    }

    @Override
    public Boolean isZero(Ring ring) {
        return multin.isEmpty();
    }

    @Override
    public boolean equals(Element x, Ring ring) {
        return multin.containsAll(((Factor) x).multin) && powers.containsAll(((Factor) x).powers);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Factor) {
            Factor f = (Factor) obj;
            if (powers.size() != f.powers.size()) {
                return false;
            }
            return powers.equals(f.powers) && multin.equals(f.multin);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.multin != null ? this.multin.hashCode() : 0);
        hash = 37 * hash + (this.powers != null ? this.powers.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Element o) {
        Factor f = (Factor) o;
        int len = multin.size() - 1;
        int len2 = f.multin.size() - 1;
        while (len >= 0 && len2 >= 0) {
            int compare = multin.elementAt(len).compareTo(f.multin.elementAt(len2));
            if (compare != 0) {
                return compare;
            } else {
                int compPow = powers.elementAt(len).compareTo(f.powers.elementAt(len2));
                if (compPow != 0) {
                    return compPow;
                }
            }
            len--;
            len2--;
        }
        if (len == len2) {
            return 0;
        } else if (len >= 0) {
            return multin.elementAt(len).compareTo(f.multin.firstElement());
        } else {
            return -1;
        }
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        return compareTo(((Factor) x), ring);
    }
    
    
//     /**  НЕДОПИСАНА !!!!!!!!!!!!1
//     * Производная от FactorPol -- сумма (F.ADD) FactorPol.
//     *
//     * @param num
//     * @param r
//     *
//     * @return
//     */
//    @Override
//    public Element D(int num, Ring r) {
//        int nonZeroCnt = 0;
//        int multinCnt = multin.size();
//        // Assume that number is at the very beginning.
//        boolean hasNumberMultin = multin.get(0).isItNumber();
//        if (hasNumberMultin && multinCnt == 1) { // Only constant.
//            return r.numberZERO;
//        }
//        Element[] tmpSumArgs = new Element[multinCnt];
//        for (int i = hasNumberMultin ? 1 : 0; i < multinCnt; i++) {
//            Vector<Element>   tmpPowers = new Vector<Element>(multin);
//            Vector<Element>  tmpMultins = new Vector<Element>(powers);
//            Element deriv = multin.get(i).D(num, r);//.truncate();
//            if (deriv.isZero(r))  continue;
//            if (!(powers.get(i).isOne(r))){
//                if (hasNumberMultin) {Element pow=tmpMultins.get(i); E'
//               ''
//                   tmpMultins.setElementAt(pow.multiply(powers.get(i), r), i); 
//                   tmpPowers.setElementAt(pow.subtract(NumberZ.ONE, r), i); 
//                    tmpPowers[i]--;
//                    tmpMultins = multin.clone();
//                    tmpMultins[0] = multin[0].multiply(
//                            new Polynom(NumberZ.valueOf(powers[i])), r);
//                    tmpMultins[i] = deriv;
//                } else {
//                    tmpPowers = new int[multinCnt + 1];
//                    System.arraycopy(powers, 0, tmpPowers, 1, multinCnt);
//                    tmpPowers[0] = 1; 
//                    tmpPowers[i + 1]--;
//                    tmpMultins = new Polynom[multinCnt + 1];
//                    System.arraycopy(multin, 0, tmpMultins, 1, multinCnt);
//                    tmpMultins[0] =deriv.multiplyByNumber(NumberZ.valueOf(powers[i]),r);
//                  //  tmpMultins[i + 1] = deriv;
//                }
//            } else {
//                tmpPowers = powers.clone();
//                tmpMultins = multin.clone();
//                tmpMultins[i] = deriv;
//            }
//            FactorPol tmpFactorPol = new FactorPol(tmpPowers, tmpMultins);
//            tmpFactorPol.normalForm(r);
//            tmpSumArgs[nonZeroCnt] = tmpFactorPol;
//            nonZeroCnt++;
//        }
//        if (nonZeroCnt == 1) {
//            return tmpSumArgs[0];
//        }
//        Element[] sumArgs = new Element[nonZeroCnt];
//        System.arraycopy(tmpSumArgs, 0, sumArgs, 0, nonZeroCnt);
//        return new F(F.ADD, sumArgs);
//    }
//    /** is It Number?
// * 
// * @return true/false
// */
//    public boolean isItNumber() {
//            for (int i = 0; i < multin.size(); i++) {
//                if(!multin.get(i).isItNumber()) return false;
//            }
//        return true;
//    }
    
}
