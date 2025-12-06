package com.mathpar.polynom;
import com.mathpar.number.Ring;
import com.mathpar.number.*;
import java.util.ArrayList;

public class PolynomialRootsIsolation {
    Ring rng;                                                   // Зачем оно здесь? О_о
    public PolynomialRootsIsolation(Ring rng) {this.rng=rng;
    }
    static Polynom p = new Polynom();
    static ArrayList<NumberR> ArL = new ArrayList<NumberR>();

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("(");
        for (int i = 0; i < ArL.size(); i++) {
            if (i % 2 != 0) {
                if (i == ArL.size() - 1) {
                    res.append(ArL.get(i) + ")");
                } else {
                    res.append(ArL.get(i) + "),(");
                }
            } else {
                res.append(ArL.get(i) + ",");
            }
        }
        // res.append(")");
        return res.toString();
    }

    public static  Polynom Pr1(Polynom p, NumberR up, NumberR down, Ring ring) {
        // увеличим массив powers
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
        //изменим полином
        Element[] pet1 = new Element[]{
            new Polynom("x",ring),
            new Polynom("u*x+v",ring),
            new Polynom("x+1",ring)};
        p1 = p1.ordering(ring);
        Polynom p2 = (Polynom) p1.value(pet1, ring);
        //подставим значения up и down
        Element[] pet2 = new Element[]{new Polynom("x", ring), down, up};
        Polynom p3 = (Polynom) p2.value(pet2, ring);
        return p3;
    }

    public static int Pr2(Polynom p) {
        int k = 0;
        for (int i = 0; i < p.coeffs.length - 1; i++) {
            if (p.coeffs[i].signum() != p.coeffs[i + 1].signum()) {
                k++;
            }
        }
        return k;
    }


    public static NumberR[] Pr4 (int t, NumberR up,NumberR down, Ring ring){
        //процесс разделения промежутка нахождения корней
         //при невыполнении условия
         NumberR[] l=new NumberR[0];
         if (t==0){System.out.println("Not find koren");}
         //корней нет
         else  {if (t==1){
                     ArL.add(down);
                     ArL.add(up);
                     l=new NumberR[ArL.size()];
                     ArL.toArray(l);}
          //промежуток найден
              else {NumberR m=(NumberR)((NumberR) up.add(down)).divide(new NumberR(2),ring.MC);
                    NumberR a1=(NumberR) down;
                    NumberR b1=(NumberR) m;
                    NumberR a2=(NumberR) m;
                    NumberR b2=(NumberR) up;
          //нахождение новых границ
                    Element[] pet=new Element[]{m};
                    Element p3= p.value(pet, ring);
                    if (p3.isZero(ring)){
                        ArL.add(m);
                        ArL.add(m);
                        l=new NumberR[ArL.size()];
                        ArL.toArray(l);}
                          Polynom p1= Pr1(p, b1, a1, ring);
                          int t1= Pr2(p1);
                          l= Pr4(t1,b1,a1,ring);
         //нахождение корней на стыке границ
                          Polynom p2= Pr1(p, b2, a2, ring);
                          int t2= Pr2(p2);
                          l= Pr4(t2,b2,a2, ring);
         //нахождение корней на 2ой границе
              }
        }
        return l;
          }


    public static ArrayList<NumberR> Otvet(NumberR a, NumberR b) {
        //зададим массив корней
        ArrayList<NumberR> r = new ArrayList<NumberR>();
        r.add(b);
        r.add(a);
        return r;
    }

    public static int[] MProc() {
        int[] r = new int[666];
        return r;
    }

    public static void Rass(Polynom p, Ring ring) {
        int[] NPr = MProc();
        int intNumbProc = NPr.length;
        System.out.println("intNumbProc=="+intNumbProc);
        ArrayList<Element[]> MassIn = new ArrayList<Element[]>(intNumbProc);//массив введенной 4ки данных о процессоре
        ArrayList<Element[]> MassOut = new ArrayList<Element[]>(intNumbProc);//массив выводимых данных о процессорах
        NumberR b = (NumberR) p.U_BOUND(ring);//изначальная верхняя граница
        NumberR a = NumberR.ZERO;//изначальная нижняя граница
        NumberR NumbProc = new NumberR(NPr.length);//кол-во процессоров(размер массива процессоров в формате NubmerR)
        NumberR h = (NumberR)((NumberR) (b.subtract(a))).divide(NumbProc,10,3);//первый шаг деления всего отрезка
        for (int i = 0; i < intNumbProc; i++) {
            b = (NumberR)a.add(h);
            Polynom p1 = Pr1(p, b, a, ring);
            Element[] pet = new Element[]{b};
            Element p3 = p.value(pet, ring);
            if (p3.isZero(ring)) {
                ArL.add(b);
                ArL.add(b);
            }
            int k = Pr2(p1);
            Element[] M4 = new Element[]{new NumberZ(i), new NumberZ(k), a, b};
            MassIn.add(M4);
            a = b;
        }
        NumberR sum = NumberR.ONE;
        while (!sum.isZero(ring)) {
            NumberR64 delta = NumberR64.ZERO;
            //заполнили массив MassIn
            sum = NumberR.ZERO;//сумма отрезков на которых больше 1 корня

            for (int j = 0; j < intNumbProc; j++) {
                if (MassIn.get(j)[1].isZero(ring)==false) {
                    if (MassIn.get(j)[1].isOne(ring)==false) {
                        NumberR a1 = (NumberR) (MassIn.get(j)[2]);
                        NumberR b1 = (NumberR) (MassIn.get(j)[3]);
                        sum = (NumberR)sum.add((NumberR)b1.subtract(a1));
                    } else {
                        ArL.add((NumberR) MassIn.get(j)[2]);
                        ArL.add((NumberR) MassIn.get(j)[3]);
                    }
                }
            }//найдем эту сумму ------------------------
            //---------------------------------

            NumberR h1 = (NumberR) sum.divide(new NumberR(intNumbProc),5,3);//шаг 2
            int N = 0;// номер процессора в который пишем
            System.out.println("sum===="+sum+"          "+h1);
            System.out.println("new NumberR(intNumbProc)==="+new NumberR(intNumbProc));
            NumberR64 K1 = NumberR64.ZERO;//
            for (int i = 0; i < intNumbProc; i++) {//  номер процессора из которого берем
                System.out.println("MassIn.size=="+MassIn.size());
                System.out.println("i=="+i);

                if (MassIn.get(i)[1].isZero(ring) || MassIn.get(i)[1].isOne(ring)) {
                    continue;
                }
                NumberR64 k1 = (NumberR64) (((NumberR) MassIn.get(i)[3]).toNumber(Ring.R64,ring).
                        subtract(((NumberR) MassIn.get(i)[2]).toNumber(Ring.R64,ring), ring)).divide(h1, ring);
                System.out.println("MassIn.get(i)[2]=="+MassIn.get(i)[2]);
                System.out.println("MassIn.get(i)[3]=="+MassIn.get(i)[3]);
                System.out.println("h1==="+h1);
                k1 = k1.add(delta);
                double k1d = k1.doubleValue();
                double k1_round=Math.round(k1d);

                delta = new NumberR64(k1d-k1_round);
                System.out.println("k1=="+k1+"    "+"delta====="+delta);
                int k1Z =  (int) k1_round;//
                // производить запись для текущего i-ого
                if (k1Z!=1){
                NumberR a2 = (NumberR) (MassIn.get(i)[2]);
                NumberR b2 = (NumberR) a2.add(h1);
                Polynom p0 = Pr1(p, b2, a2, ring);
                Element[] M40 = new Element[]{new NumberZ(i), new NumberZ(Pr2(p0)), a2, b2};
                a2 = b2;
                b2 = (NumberR)a2.add(h1);
                MassOut.add(M40);
                Element[] pet = new Element[]{b2};
                Element p3 = p.value(pet, ring);
                if (p3.isZero(ring)) {
                    ArL.add(b2);
                    ArL.add(b2);
                    }
                int tt = 0;
                while ((tt < k1Z - 1) && (N < intNumbProc)) {
                    System.out.println("tt=="+tt);
                    System.out.println("N=="+N);
                    System.out.println("k1Z="+k1Z);
                    if (MassIn.get(N)[1].isZero(ring) || MassIn.get(N)[1].isOne(ring)) {
                        Polynom p1 = Pr1(p, b2, a2,ring);
                        Element[] M41 = new Element[]{new NumberZ(N), new NumberZ(Pr2(p1)), a2, b2};
                        a2 = b2;
                        b2 = (NumberR)a2.add(h1);
                        Element[] pet2 = new Element[]{b2};
                        Element p32 = p.value(pet2, ring);
                        if (p32.isZero(ring)) {
                            ArL.add(b2);
                            ArL.add(b2);
                            }
                        MassOut.add(M41);
                        tt++;
                    }
                    N++;
                }
                //конец записи для текущего i-ого

              }
            else{ Pr4(MassIn.get(i)[1].intValue(),(NumberR) MassIn.get(i)[2],(NumberR) MassIn.get(i)[3],ring);
                  Element[] M42=new Element[]{(NumberZ) MassIn.get(i)[0],NumberZ.ZERO , (NumberR) MassIn.get(i)[2], (NumberR) MassIn.get(i)[3]};
                  MassOut.add(M42);}
//            MassIn = MassOut;
//            MassOut = new ArrayList<Element[]>(intNumbProc);
            }
            MassIn = MassOut;
            MassOut = new ArrayList<Element[]>(intNumbProc);
            WrAr(MassIn);
        }
    }


    public static void WrAr(ArrayList<Element[]> Mass){
     for (int i=0;i<Mass.size();i++){
         System.out.println("   ");
         for(int j=0;j<Mass.get(i).length;j++){
             System.out.println(Mass.get(i)[j]+" ");
         }
     }
    }


}



   /*public static int Pr2 ( Polynom p1, Polynom p2){
        //найдем количество изменений знаков
        Polynom p3=p1.normalNumbVar();
        int k=0;
        for (int i=0;i<p3.powers.length;i++) {
            for (int u=0;u<p2.powers.length;u++) {
               if (p3.powers[i]==p2.powers[u]) {
                    if (p3.coeffs[i].signum()!=p2.coeffs[u].signum()) {
                        k++;
                    }
                 }
            }
         }
         return k;
    }*/


