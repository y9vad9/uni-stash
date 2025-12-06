package com.mathpar.polynom;

import com.mathpar.number.Ring;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.Vector;
import com.mathpar.number.*;

/**
 * Класс для хранения объектов вида: FactorPol[0] + FactorPol[1] + FactorPol[2] + ... + FactorPol[n],
 * например, суммы полиномиальных дробей.
 *
 * @version 1.0
 */
public class FactorPolSum extends Element {

    /**
     * вектор FactorPol, хранящийся в отсортированном виде.
     * Сортировка производится по возрастанию.
     */
    public Vector<FactorPol> arg;

    public FactorPolSum() {
    }

    /**
     *
     * @param s
     * Пример [(4)(x-3)^-2(x+8)^5]+[(2)(x+99)(x-3)]
     */
    public FactorPolSum(String s, Ring ring)  {
        Vector<FactorPol> a = new Vector<FactorPol>();
        int begin = 0;
        for (int i = begin; i < s.length(); i++) {
            begin = s.indexOf("[");
            int end = s.indexOf("]");
            FactorPol fp = new FactorPol(s.substring(begin + 1, end),ring);
            fp.normalForm(ring);
            a.add(fp);
            begin = end;
            if (end + 2 < s.length()) {
                s = s.substring(end + 2);
            } else {
                s = "";
            }
        }
        arg=a;
    }

    /**
     * конструктор от массива Element-ов типа FactorPol
     * @param a - FactorPol[]
     */
    public FactorPolSum(FactorPol[] a) {
        Vector<FactorPol> b = new Vector<FactorPol>();
        for (int i = 0; i < a.length; i++) {
            b.add(a[i]);
        }
        this.arg = b;
    }

    /**
     * конструктор от VectorS Element-ов типа FactorPol
     * @param a - VectorS
     */
    public FactorPolSum(Vector<FactorPol> a) {
        this.arg = a;
    }

    /**
     *  Конструктор создающий FactorPolSum из FactorPol
     * @param a - FactorPol
     */
    public FactorPolSum(FactorPol a) {
        Vector<FactorPol> Arg = new Vector<FactorPol>();
        Arg.add(a);
        this.arg = Arg;
    }
    /**
     *
     */
    public static FactorPolSum ZERO = new FactorPolSum(new Vector<FactorPol>());

    /**
     *  Конструктор создающий FactorPolSum из полинома
     * @param a - полином
     */
    public FactorPolSum(Polynom a, Ring ring) {
        Vector<FactorPol> Arg = new Vector<FactorPol>();
        Arg.add(a.FactorPol_SquareFreeOneVar(ring));
        this.arg = Arg;
    }

//    public FactorPolSum add(FactorPolSum b, Ring ring){ Add(b); normalForm(ring);
//           return this;
//    }
    /**
     *  добавление FactorPolSum без сортировки
     */
    public void Add(FactorPolSum f) {
        arg.addAll(arg.size(), f.arg);
    }

    /**
     *  добавление FactorPol без сортировки
     */
    public void AddFp(FactorPol Fp) {
        arg.add(Fp);
    }

    /**
     * обнуляем FactorPolSum
     */
    public void toZero() {
        arg.removeAllElements();
    }

    /**
     * На входе FactorPolSum в котором нет совпадающих FactorPol
     * Сортируем элементы в векторе FactorPolSum. В порядке возрастания.
     *
     */
    void sorting(Ring ring) {
        Element[] res = new Element[arg.size()];
        arg.copyInto(res);
        int[] nP = com.mathpar.number.Array.sortPosUp(res,ring); // сортируем полиномы по возрастанию
        arg.clear();
        for (int i = 0; i < res.length; i++) {
            arg.add((FactorPol) res[nP[i]]);
        }
    }

    /**
     * приводит FactorPolSum к нормальному виду
     *
     * 1: складывает равные между собой FactorPol
     * 2: сортирует в порядке возрастания
     */
    public void normalForm(Ring ring) {
        int len = arg.size();//размер FactorPolSum
        IntList mas = new IntList();
        for (int i = 0; i < arg.size(); i++) {
            int k = 0;
            for (int j = 0; j < arg.size(); j++) {
                if ((i != j) && (arg.get(i) != null) && (arg.get(j) != null)) {
                    if (arg.get(i).equals(arg.get(j))) {
                        k++;
                        k = k + arg.get(j).koeff_mas() + 1;
                        arg.remove(j);
                    } else {
                        if (arg.get(i).polynom_Mas().equals(arg.get(j).
                                polynom_Mas())) {
                            int k1 = arg.get(i).koeff_mas(); //1
                            int k2 = arg.get(j).koeff_mas(); //2
                            k = k1 + k2;
                            arg.remove(j);
                        }
                    }
                }
            }
            mas.arr[i] = k;
        }
        for (int i = 0; i < arg.size(); i++) {
            if (mas.arr[i] != 0) {
                int coeff = mas.arr[i];// + 1;
                Polynom[] pol = arg.get(i).pol();
                int[] power = arg.get(i).powers;
                Polynom p = new Polynom(String.valueOf(coeff),ring);
                arg.remove(i);
                Polynom[] pols = new Polynom[pol.length + 1];
                pols[0] = p;
                int[] power_final = new int[power.length + 1];
                power_final[0] = 1;
                System.arraycopy(power, 0, power_final, 1, power.length);
                System.arraycopy(pol, 0, pols, 1, pol.length);
                FactorPol fp = new FactorPol(power_final, pols);
                arg.add(i, fp);
            }
        }
        sorting(ring);
    }

    public int compareTo(Element x) {
        return compareTo((FactorPolSum) x);
    }

    /**
     * сравнение 2-ух FactorPolSum
     * @param x
     * @return
     */
    public int compareTo(FactorPolSum x, Ring ring) {
        int len = x.arg.size() - 1;
        int len2 = this.arg.size() - 1;
        //len-=len2;
        while (len >= 0 && len2 >= 0) {
            int compare = this.arg.elementAt(len).compareTo(x.arg.elementAt(len2), ring);
            if (compare != 0) {
                return compare;
            }
            len--;
            len2--;
        }
        return (len == len2) ? 0 : (len >= 0) ? this.arg.elementAt(len).compareTo(x.arg.firstElement(), ring) : -1;
    }

    /**
     * сложение 2-ух FactorPolSum
     */
    public FactorPolSum add(FactorPolSum a, Ring ring) {
        Element[] masPol1 = new Element[arg.size()];//массив для полиномиальной части FactorPol
        int[] numberPol1 = new int[arg.size()];//массив для числовой части FactorPol
        for (int i = 0; i < arg.size(); i++) {
            numberPol1[i] = arg.get(i).koeff_mas();
            masPol1[i] = arg.get(i).polynom_Mas();
        }
        Element[] masPol2 = new Element[a.arg.size()];//массив для полиномиальной части FactorPol
        int[] numberPol2 = new int[a.arg.size()];//массив для числовой части FactorPol
        for (int j = 0; j < a.arg.size(); j++) {
            numberPol2[j] = a.arg.get(j).koeff_mas();
            masPol2[j] = a.arg.get(j).polynom_Mas();
        }
        int[][] powers = new int[1][];
        Element[] res = com.mathpar.number.Array.jointUp(masPol1, masPol2, numberPol1, numberPol2, powers, ring);//слияние 2-ух FactorPolSum
        //сборка слитого массива
        ArrayList<FactorPol> result = new ArrayList<FactorPol>();//
        for (int i = 0; i < res.length; i++) {//
            if (powers[0][i] != 0) {
                Polynom p = new Polynom(new int[]{}, new Element[]{new NumberZ(powers[0][i])});
                FactorPol f = (FactorPol) res[i].multiply(p, ring);
                result.add(f);
            }
        }
        Vector<FactorPol> args = new Vector<FactorPol>();
        for (int i = 0; i < result.size(); i++) {
            args.add(result.get(i));
        }
        FactorPolSum fps = new FactorPolSum(args);
        fps.sorting(ring);//сортируем полученный FactorPolSum
        return fps;
    }

    /**
     * возвращает результат перемножения 2-ух FactorPolSum
     *
     * @param a
     * @return
     */
    public FactorPolSum multiply(FactorPolSum a, Ring ring) {
        //новый VectorS для добавления результата последовательного умножения 2-ух FactorPolSum
        Vector<FactorPol> args = new Vector<FactorPol>();
        for (int i = 0; i < arg.size(); i++) {
            for (int j = 0; j < a.arg.size(); j++) {
                args.add(arg.get(i).multiply(a.arg.get(j), ring));
            }
        }
        FactorPolSum fps = new FactorPolSum(args);
        System.out.println("----- " + fps);
        fps.normalForm(ring);//приводим к нормальному виду полученный FactorPolSum
        return fps;
    }

    /**
     * разность 2-ух FactorPolSum
     * @param a
     * @return
     */
    public FactorPolSum subtract(FactorPolSum a, Ring  ring){
        return add(a.negate(ring),ring);
    }

    public FactorPolSum D(int num) {
        return ZERO;
    }

    public FactorPolSum D() {
        return ZERO;
    }

    /**
     *
     * @return
     */
    public FactorPolSum negate(Ring ring) {
        FactorPol[] a = new FactorPol[arg.size()];
        arg.copyInto(a);
        Polynom minus_one = new Polynom(new int[0], new Element[]{Ring.oneOfType(ring.algebra[0]).myMinus_one(ring)});
        for (int i = 0; i < a.length; i++) {
            a[i] = (FactorPol) a[i].multiply(minus_one, ring);
        }
        return new FactorPolSum(a);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < arg.size() - 1; i++) {
            res.append(arg.elementAt(i).toString());
            res.append(" + ");
        }
        if(arg.size()!=0) res.append(arg.lastElement().toString());
        return res.toString();
    }

    @Override
    public Boolean isZero(Ring r) {
        return arg.size() == 0;
    }

    @Override
    public boolean equals(Element x, Ring r) {
        return this.compareTo((FactorPolSum) x) == 0;
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isOne(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    public static void main(String[] args) {
        FactorPol fp1 = new FactorPol("(2)(x-1)^-3(x-1)^-2(x+2)", new Ring("R64[x]"));
        FactorPol fp2 = new FactorPol("(2)(x-1)(x+2)", new Ring("R64[x]"));
        fp1.normalFormInField(new Ring("R64[x]"));
        fp2.normalFormInField(new Ring("R64[x]"));
        FactorPol fp3 = new FactorPol("(2)(x-1)^-3(x-1)^-2(x+2)", new Ring("R64[x]"));
        FactorPol fp4 = new FactorPol("(2)(x-1)(x+2)", new Ring("R64[x]"));
        fp3.normalFormInField(new Ring("R64[x]"));
        fp4.normalFormInField(new Ring("R64[x]"));
        FactorPolSum fps1 = new FactorPolSum(new FactorPol[]{fp1, fp2});
        FactorPolSum fps2 = new FactorPolSum(new FactorPol[]{fp3, fp4});


        FactorPolSum fps3 = fps1.add(fps2, new Ring("R64[x]"));
        System.out.println("fps3 = " + fps3);

       FactorPolSum fps4 = fps1.multiply(fps2, new Ring("R64[x]"));
       System.out.println("fps4 = " + fps4);
    }
}
