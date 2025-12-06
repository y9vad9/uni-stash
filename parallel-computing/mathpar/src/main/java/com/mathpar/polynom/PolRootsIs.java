/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.polynom;

import java.util.ArrayList;

import com.mathpar.number.*;
import com.mathpar.func.*;
import java.io.*;
//import polynom.Polynom;

/**
 *
 * @author alexss
 */
public class PolRootsIs {

    static ArrayList<NumberR> ArL = new ArrayList<NumberR>();
    static ArrayList<NumberR> ROOTS = new ArrayList<NumberR>();
    static Ring R = new Ring("R[x,u,v]");

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("(");
        for (int i = 0; i < ArL.size(); i++) {
            if (i % 2 != 0) {
                if (i == ArL.size() - 1) {
                    res.append(ArL.get(i) + ")");
                } else {
                    res.append(ArL.get(i) + ");(");
                }
            } else {
                res.append(ArL.get(i) + ",");
            }
        }
        return res.toString();
    }

    /**
     * Метод изменяет полином р, заменяя в нем переменную х на (u*x+v)
     * и домножая его на полином (х+1)^max, где max-максимальная степень полинома р.
     * При этом получаем полином от трех переменных.
     * @param p-первоначальный полином
     * @return измененный полином р3
     */
    public static Polynom polTransform(Polynom p) {

        int l = p.coeffs.length;
        int l_pow = p.powers.length;
        int varNumb = l_pow / l;
        int[] pow1 = new int[3 * l];
        int j = 0;
        int max = p.powers[0];
        for (int i = 0; i < l_pow; i += varNumb) {
            pow1[j] = 0;
            pow1[j + 1] = p.powers[i];
            pow1[j + 2] = max - p.powers[i];
            j += 3;
        }

        Polynom p1 = new Polynom(pow1, p.coeffs);
        //Изменим полином

        Element[] pet1 = new Element[]{
            new Polynom("x", R),
            new Polynom("u*x+v", R),
            new Polynom("x+1", R)};
        p1 = p1.ordering(R);
//             new FvalOf(R).valOfToPolynom(p1, pet1);
        Element p2 = new FvalOf(R).valOfToPolynom(p1, pet1);//(Polynom) p1.valueOf(pet1, R);
        return (Polynom) p2;
        //Подставим значения up и down
    }

    /**
     * Метод подставляющий значения up и down в измененный полином для постороения
     * нового полинома от одной переменной с новыми переменными.
     * @param p-данный полином
     * @param up-верхняя граница
     * @param down-нижняя граница
     * @return полином p3.
     * @throws MPIException
     */
    public static Polynom Pr1(Polynom p, NumberR up, NumberR down) {
        Element[] pet2 = new Element[]{new Polynom("x", R), down, up};
        Polynom p3 = (Polynom) (new FvalOf(R).valOfToPolynom(p, pet2));
        return p3;
    }

    /**
     * Метод находит колличество переменн знаков в полиноме р.
     * @param p-данный нам полином
     * @return число переменн знаков k
     */
    public static int Pr2(Polynom p) {


        int k = 0;
        for (int i = 0; i < p.coeffs.length - 1; i++) {
            if (p.coeffs[i].signum() != p.coeffs[i + 1].signum()) {
                k++;
            }
        }

        return k;
    }

    public static void Postr(ArrayList<Element[]> M) {
        for (int i = 0; i < M.size(); i++) {
            for (int j = 0; j < M.get(i).length; j++) {
                System.out.print("[" + i + "](" + j + ")==" + M.get(i)[j] + "   ");
            }
            System.out.println();
        }
    }

    /**
     * Метод построения нового массива границ.В зависимости от данных массива
     * корней rootsAmount,либо разбивает на колличество корней на данном промежутке
     * либо добавляет в ответ границы с 1 корнем либо ничего не делает с этим промежутком.
     * @param p-первоначальный полином
     * @param ar-массив границ
     * @param rootsAmount-массив корней
     * @return массив границ
     * @throws MPIException
     */
    public static ArrayList<Element> splitIntervals(Polynom p, ArrayList<Element> ar, int[] rootsAmount) {
        if (ar.size() != 2 * rootsAmount.length) {
            System.err.println("Bad array length");
            return null;
        }
        ArrayList<Element> res = new ArrayList<Element>();
        for (int i = 0, k = 0; i < ar.size(); i += 2, k++) {
            Element[] kkk=new Element[]{ar.get(i + 1)};
            if (((NumberR)(new FvalOf(R).valOfToPolynom(p,kkk))).compareTo(NumberR.ZERO)==0) {
                ArL.add((NumberR) ar.get(i + 1));
                ArL.add((NumberR) ar.get(i + 1));
            }
            if (rootsAmount[k] > 0) {
                NumberR left = (NumberR) ar.get(i),
                        right = (NumberR) ar.get(i + 1);
                if (rootsAmount[k] > 1) {
                    res = splitInterval(left, right, rootsAmount[k], res);
                } else {
                    if (rootsAmount[k] == 1) {
                        ArL.add(left);
                        ArL.add(right);
                    }
                }
            }
        }

        return res;
    }

    /**
     * Разбивает интервал (left, right) на rootsAmount частей и добавляет результат в ar.
     * @param left-нижняя граница
     * @param right-верхняя граница
     * @param rootsAmount-количество частей
     * @param ar-массив границ
     * @return
     */
    public static ArrayList<Element> splitInterval(NumberR left, NumberR right,
            int rootsAmount, ArrayList<Element> ar) {
        NumberR h = (NumberR)((NumberR)right.subtract(left)).divide(new NumberR(rootsAmount), 3, 3);
        for (int i = 0; i < rootsAmount; i++) {
            ar.add(left);
            left = (NumberR)left.add(h);
            ar.add(left);
        }
        return ar;
    }

    public static void solve(Polynom pol) {
        Polynom ptrans = polTransform(pol);
        NumberR b = pol.U_BOUND(R),
                a = NumberR.ZERO;
        Polynom pt = Pr1(ptrans, a, b);
        int k = Pr2(pt);
        ArrayList<Element> intervals = new ArrayList<Element>();
        ArrayList<Element> intervals1 = new ArrayList<Element>();
        intervals = splitInterval(a, b, k, intervals);
        int len = intervals.size();
        while (intervals.size() > 0) {
            for (int i = 0; i < intervals.size() / 2; i++) {
                NumberR ro1=(NumberR)(intervals.get(2*i));
                NumberR ro2=(NumberR)(intervals.get(2*i+1));
                int kil=Pr2(Pr1(ptrans, ro1, ro2));
                if (kil==1){ArL.add(ro1);ArL.add(ro2);}
                if(kil>1){NumberR ro3=(NumberR)((NumberR)ro1.add(ro2)).divide(new NumberR(2),R.MC);
                if( pol.valueOf(ro3, R).compareTo(NumberR.ZERO,R)==0 ){ArL.add(ro3);ArL.add(ro3);}
                    intervals1.add(ro1);
                    intervals1.add(ro3);
                    intervals1.add(ro3);
                    intervals1.add(ro2);
                    }
            }
            intervals.clear();
            for (int ss=0;ss<intervals1.size();ss++){
            intervals.add(intervals1.get(ss));
            }
            intervals1.clear();
    }
    }

     public static void main(String[] args)  throws FileNotFoundException, IOException, Exception {

            Polynom p= new Polynom("(x-3)(x-2)(x-1)",R);
            solve(p);
            // единственный вывод, toString() свой
            System.out.println(ArL.toString());


//            for(int i=0;i<(ArL.size()/2);i++){
//            NumberR64 a=NumberR64.valueOf(ArL.get(2*i));
//                System.out.println("a===="+a);
//            NumberR64 b=NumberR64.valueOf(ArL.get(2*i+1));
//                System.out.println("b===="+b);
//            NumberR64 root=Newton.Root(a, b, p);
//
//                System.out.println("root==="+root);
//            NumberR roots=root.BigDecimalValue();
//            ROOTS.add(roots);
                }


            //long t2 = System.currentTimeMillis();


            //String s1 = "\n"+"Procs=" + size+"\n"+"Deg=" + deg+"\n"+"Bits=" + bits+"\n"+"Time=" + (t2 - t1)+"\n";
            //String s11=size+"\n"+deg+"\n"+bits+"\n"+(t2-t1)+"\n";
            //System.out.println("s11111===="+s11);
            //RandomAccessFile raf =  new RandomAccessFile("/home/student/aaa.txt", "rw");
            //raf.seek(raf.length());
            //raf.writeUTF(s1);
            //RandomAccessFile raf1 =  new RandomAccessFile("/home/student/aaa1.txt", "rw");
            //raf1.seek(raf1.length());
            //raf1.writeUTF(s11);

}
//}
