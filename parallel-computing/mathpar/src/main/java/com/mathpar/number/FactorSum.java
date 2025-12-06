// © Разработчик Смирнов Роман Антонович (White_Raven), ТГУ'10.
package com.mathpar.number;

import com.mathpar.func.*;
import java.util.Vector;

/**
 * Класс для объектов вида: FactorElement[0] + FactorElement[1] +  ... + FactorElement[n]
 * @author White_Raven
 * @version 1.0
 */
public class FactorSum extends Element {

    Vector<Factor> args = new Vector<Factor>();

    public Vector<Factor> get_args() {
        return args;
    }
    public static final FactorSum ZERO = new FactorSum(new Vector<Factor>());

    public FactorSum() {
    }

    public FactorSum(Vector<Factor> arg) {
        this.args = arg;
    }

    public FactorSum(Element el, Ring ring) {
        FillFEA(el, ring);
        sortingWithOutAdd(ring);
    } //конструктор от любого элемента

    void AddWithoutSorting(Factor fe) {
        args.add(fe);
    }

    void FillFEA(Element el, Ring ring) {
        if (el instanceof F) {
            if (((F) el).name == F.ADD) {
                for (Element e : ((F) el).X) {
                    FillFEA(e, ring);
                }
            } else {
                args.add(new Factor(el, ring));
            }
        } else {
            if (el instanceof Factor) {
                args.add((Factor) el);
            } else {
                args.add(new Factor(el, ring));
            }
        }
    }

    Element[] CopyVecToArray(Vector<Factor> vec) {
        Element[] X = new Element[vec.size()];
        vec.copyInto(X);
        return X;
    }

    void sortingLikeArray(int[] a) {
        Vector<Factor> res = new Vector<Factor>();
        for (int el : a) {
            res.add(args.elementAt(el));
        }
        args.removeAllElements();
        args.addAll(res);
    }

    void sortingWithOutAdd(Ring ring) {
        if (args.size() < 2) {
            return;
        }
        Element[] X = CopyVecToArray(args); // заполняем наш массив для сортировки
        int[] nP = com.mathpar.number.Array.sortPosUp(X, ring); // сортируем  по возрастанию
        sortingLikeArray(nP);
    }

    public Element FactorElementAdd_to_F(Ring ring) {
        int len = args.size();
        if (len == 0) {
            return NumberZ.ZERO;
        }
        if (len == 1) {
            return args.firstElement().toF(ring);
        }
        Element[] X = new Element[len];
        for (int i = 0; i < len; i++) {
            X[i] = args.elementAt(i).toF(ring);
        }
        return new F(F.ADD, X);
    }

    /**
     *  Вывод в строку
     * @return
     */
    @Override
    public String toString() {
        if (args.size() == 0) {
            return "0";
        }
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < args.size() - 1; i++) {
            res.append(args.elementAt(i).toString());
            res.append(" + ");
        }
        res.append(args.lastElement().toString());
        return res.toString();
    }

    @Override
    public Boolean isZero(Ring r) {
        return args.size() == 0;
    }

    /**
     * сравнение 2-ух FactorSum
     * @param x
     * @return
     */
    public int compareTo(FactorSum x, Ring ring) {
        int len = x.args.size() - 1;
        int len2 = this.args.size() - 1;
        while (len >= 0 && len2 >= 0) {
            int compare = this.args.elementAt(len).compareTo(x.args.elementAt(len2), ring);
            if (compare != 0) {
                return compare;
            }
            len--;
            len2--;
        }
        return (len == len2) ? 0 : (len >= 0) ? this.args.elementAt(len).compareTo(x.args.firstElement(), ring) : -1;
    }


    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        return compareTo((FactorSum) x, ring);
    }

    @Override
    public boolean equals(Element x, Ring r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isOne(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
