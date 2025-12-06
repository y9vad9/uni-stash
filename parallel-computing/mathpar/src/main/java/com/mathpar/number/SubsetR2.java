package com.mathpar.number;

import com.mathpar.func.F;

import java.util.ArrayList;

import com.mathpar.polynom.Polynom;

/** Subsets of R^2, which obtained by finite number of compacts.
 * This Subsets are closed for operation of union and intersection.
 *  С подмножествами SubsetR2 мы будем стараться выполняють  операции пересечения, объединения,
 * сравнения.
 * Каждое подмножество задается упорядоченным  массивом компактов. Компакты замкнутые.
 * Упорядочиватся по левой границе проекции компакта на ось абцисс,
 * а при совпадении - по нежней границе проекции на ось ординат.
 *
 * Компакты могут быть нескольких типов:
 * 1) Если первый и второй аргумент - числа (isItNumber()?),
 * то это криволинейная трапеция:
 * [e0 > e1] - проекции граничных точек на ось абсцисс,
 * [e2 > e3] - верхняя и нижняя ограничивающая функция;
 * 2) иначе, это криволинейная трапеция:
 * [e2 > e3] - проекции граничных точек на ось ординат,
 * [e0 > e1] - верхняя и нижняя ограничивающая функция;
 * По умолчанию: абсцисса - первая переменная в кольце, ордината - вторая
 * переменная в кольце.
 *
 * Пример: set= {NEGATIVE_INFINITY,  -3, 1,10, 12,12, 34,POSITIVE_INFINITY}
 * border= {f,f,t,t,t,t,t,f} ОБОЗНАЧАЕТ: {(-oo - -3),(1-10],[12,12],[34-oo)}
 *@author   gennadi
 */
public class SubsetR2 extends Element{

    ArrayList<VectorS> set = new ArrayList<VectorS>();
  //  ArrayList<Boolean> border = new ArrayList<Boolean>();

    public SubsetR2() {
        set = new ArrayList<VectorS>();
      //  border = new ArrayList<Boolean>();
    }

    public SubsetR2(ArrayList<VectorS> set ) {
        this.set = set;
      //  this.border = border;
    }
/**
 * каждые четыре элемента образуют один компакт и записываются
 * в подмножество
 * @param elem
 */
    public SubsetR2(Element[] elem ) {
        set = new ArrayList(elem.length/4);
        for (int i = 0; i < elem.length-3 ; i+=4) {
            Element[] vect=new Element[4];
            System.arraycopy(elem, i, vect, 0, 4);
            VectorS v=new VectorS(vect);
            set.add(v);
        }
    }

    public SubsetR2(Element e1,Element e2,Element e3,Element e4) {
            set = new ArrayList(1);
            set.add(new VectorS(new Element[]{e1,e2,e3,e4}));
    }
    /** Сумма всех проекций на ось абсцисс.
     * @param ring Ring
     * @return s - sum of all projects on the axes X
     */
    public Element sumOfAllXProjs(Ring ring) {
        Element[] F=new Element[4]; Element s = ring.numberZERO;
        for (int i = 0; i < set.size(); i ++) {
            F=set.get(i).V; s=F[1].subtract(F[0], ring); }
        return s;
    }
    /** Сумма всех проекций на ось ординат.
     * @param ring Ring
     * @return s - sum of all projects on the axes X
     */
    public Element sumOfAllYProjs(Ring ring) {
        Element[] F=new Element[4]; Element s = ring.numberZERO;
        for (int i = 0; i < set.size(); i ++) {
            F=set.get(i).V; s=F[3].subtract(F[2], ring); }
        return s;
    }

//    /**
//     * Сравнение множеств.
//     * Результатом является TRUE при совпадениии и FALSE при несовпадении
//     * @param s множество-параметр
//     * @return TRUE при совпадении и FALSE при несовпадении
//     */
//    public boolean equals(number.SubsetR2 s) {
//        int size = this.set.size();
//        if (size != s.set.size()) {
//            return false;
//        }
//        for (int i = 0; i < size; i++) {
//            if (!this.set.get(i).equals(s.set.get(i))) {
//                return false;
//            }
//            if (!this.border.get(i).equals(s.border.get(i))) {
//                return false;
//            }
//        }
//        return true;
//    }

    //--------------------------------------------------------------------------
//    /**
//     * Объеденение множеств
//     * @param a множество-параметр
//     * @return объединение множеств
//     */
//    public number.SubsetR2 union(number.SubsetR2 a, Ring ring) {
//        Element[] a1 = new Element[set.size()];
//        set.toArray(a1);
//        Element[] a2 = new Element[a.set.size()];
//        a.set.toArray(a2);
//        Boolean[] b1 = new Boolean[border.size()];
//        border.toArray(b1);
//        Boolean[] b2 = new Boolean[a.border.size()];
//        a.border.toArray(b2);
//        ArrayList<Element> result = new ArrayList<Element>();
//        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
//        //переводим все в R
//        Element[] R1 = fromQtoNumberR(a1, Ring.ringZxyz);
//        Element[] R2 = fromQtoNumberR(a2, Ring.ringZxyz);
//        int i = 0;
//        int j = 0;
////цикл по всем элементам вектора set 1 множества (i) , и 2 множества (j)
//        while (i < a1.length && j < a2.length){
//            Element N1 = R1[i];
//            Element N2 = R2[j];
//            if (N1.compareTo(N2, ring) == -1) {
//
//                if (j % 2 == 0) {
//                    result.add(a1[i]);
//                    if(b1[i] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                }
//                i++;
//            }
//            if (N1.compareTo(N2, ring) == 1) {
//                if (i % 2 == 0) {
//                    result.add(a2[j]);
//                    if(b2[j] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                }
//                j++;
//            }
//            if (N1.compareTo(N2, ring) == 0) {
//                if (i % 2 == j % 2) {
//                    result.add(a1[i]);
//                    if(b1[i] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                }
//                if(b1[i] == true & b1[i] == b2[j]){
//                  result.add(a1[i]);
//                  bolresult.add(true);
//                  result.add(a1[i]);
//                  bolresult.add(true);
//                }
//                i++;
//                j++;
//            }
//        }
//
//        if (i == a1.length) {
//          for (int k = j; k < a2.length; k++) {
//            result.add(a2[k]);
//            bolresult.add(b2[k]);
//          }
//        }
//
//        if (j == a2.length) {
//          for (int k = i; k < a1.length; k++) {
//            result.add(a1[k]);
//            bolresult.add(b1[k]);
//          }
//        }
//        return new number.SubsetR2(result, bolresult);
//    }

    //--------------------------------------------------------------------------
//    /**
//     * Пересечение  множеств
//     * @param a множество-параметр
//     * @return Пересечение множеств
//     */
//    public number.SubsetR2 intersection(number.SubsetR2 a, Ring ring) {
//        Element[] a1 = new Element[set.size()];
//        set.toArray(a1);
//        Element[] a2 = new Element[a.set.size()];
//        a.set.toArray(a2);
//        Boolean[] b1 = new Boolean[border.size()];
//        border.toArray(b1);
//        Boolean[] b2 = new Boolean[a.border.size()];
//        a.border.toArray(b2);
//        ArrayList<Element> result = new ArrayList<Element>();
//        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
//        //переводим все в R
//        Element[] R1 = fromQtoNumberR(a1, Ring.ringZxyz);
//        Element[] R2 = fromQtoNumberR(a2, Ring.ringZxyz);
//
//        int i = 0;
//        int j = 0;
//        while (i < a1.length && j < a2.length){
//            Element N1 = R1[i];
//            Element N2 = R2[j];
//            if (N1.compareTo(N2, ring) == -1) {
//                if (j % 2 == 1) {
//                    result.add(a1[i]);
//                    if(b1[i] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                }
//                i++;
//            }
//            if (N1.compareTo(N2, ring) == 1) {
//                if (i % 2 == 1) {
//                    result.add(a2[j]);
//                    if(b2[j] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                }
//                j++;
//            }
//            if (N1.compareTo(N2, ring) == 0) {
//                if (i % 2 == j % 2) {
//                    result.add(a1[i]);
//                    if(b1[i] == false & b2[j] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                } else {
//                    if(b1[i] == false && b2[j] == false){
//                    result.add(a1[i]);
//                    if(b1[i] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                    result.add(a2[j]);
//                    if(b2[j] == false) bolresult.add(false);
//                    else bolresult.add(true);
//                    }
//                }
//                i++;
//                j++;
//            }
//        }
//        return new number.SubsetR2(result, bolresult);
//    }

    //--------------------------------------------------------------------------
//    /**
//     * Разность множеств
//     * @param множество-параметр
//     * @return Разность множеств
//     */
//    public number.SubsetR2 subtraction(number.SubsetR2 a, Ring ring) {
//        Element[] a1 = new Element[set.size()];
//        set.toArray(a1);
//        Element[] a2 = new Element[a.set.size()];
//        a.set.toArray(a2);
//        Boolean[] b1 = new Boolean[border.size()];
//        border.toArray(b1);
//        Boolean[] b2 = new Boolean[a.border.size()];
//        a.border.toArray(b2);
//        ArrayList<Element> result = new ArrayList<Element>();
//        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
//        //переводим все в R
//        Element[] R1 = fromQtoNumberR(a1, Ring.ringZxyz);
//        Element[] R2 = fromQtoNumberR(a2, Ring.ringZxyz);
//        int i = 0;
//        int j = 0;
////цикл по всем элементам вектора set 1 множества (i) , и 2 множества (j)
//        while (i < a1.length && j < a2.length) {
//            Element N1 = R1[i];
//            Element N2 = R2[j];
//            if (N1.compareTo(N2, ring) == -1) {
//              if(j % 2 == 0) {
//                result.add(a1[i]);
//                if(b1[i] == false) bolresult.add(false);
//                else bolresult.add(true);
//              }
//              if (i < a1.length - 1) i++;
//              else break;
//            }
//            if (N1.compareTo(N2, ring) == 1) {
//                 if (i % 2 == 1) {
//                     result.add(a2[j]);
//                   if(b2[j] == false) bolresult.add(true);
//                   else bolresult.add(false);
//                   }
//            if (j < a2.length - 1) j++;
//            else {
//                result.add(a1[i]);
//                if(b1[i] == true) bolresult.add(true);
//                else bolresult.add(false);
//                break;
//            }
//
//            }
//            if (N1.compareTo(N2, ring) == 0) {
//                if (i % 2 == j % 2) {
//                    result.add(a1[i]);
//                      if (b1[j] == b2[j]) bolresult.add(true);
//                      else bolresult.add(true);
//                      j++;
//                  } else {
//                      if (i % 2 == 1){
//                        result.add(a1[i]);
//                        if (b1[j] == b2[j]) bolresult.add(true);
//                        else bolresult.add(true);
//                      }
//                  }
//                  if (i < a1.length - 1) i++;
//                  else {
//                      break;
//                  }
//              }
//            }
//
//        if (R1[i].compareTo(R2[j], ring) == 1) {
//          for (int k = i+1; k < a1.length; k++) {
//            result.add(a1[k]);
//            bolresult.add(b1[k]);
//          }
//        }
//        return new number.SubsetR2(result, bolresult);
//    }

//     public number.SubsetR2 symmetricSubtraction(number.SubsetR2 a, Ring ring) {
//       return (this.union(a, ring)).subtraction(this.intersection(a, ring), ring);
//   }

    //--------------------------------------------------------------------------
//    /**
//     * Дополнение множества
//     * @param a параметр-множество
//     * @return дополнение множеств
//     */
//    public number.SubsetR2 complement() {
//        ArrayList<Element> a =  set;
//        ArrayList<Boolean> b =  border;
//        ArrayList<Element> result = new ArrayList<Element>();
//        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
//        int i = 0;
//        int j = 0;
//        if ((a.size() == 2) && (b.size() == 2)) {
//            result.add(Element.NEGATIVE_INFINITY);
//            bolresult.add(Boolean.TRUE);
//            result.add(a.get(0));
//            if(b.get(0)) bolresult.add(false);
//            else bolresult.add(true);
//            result.add(a.get(1));
//            if(b.get(1)) bolresult.add(false);
//            else bolresult.add(true);
//            result.add(Element.POSITIVE_INFINITY);
//            bolresult.add(Boolean.TRUE);
//        } else {
//            result.add(Element.NEGATIVE_INFINITY);
//            bolresult.add(Boolean.TRUE);
//            while (j < a.size()) {
//                result.add(a.get(j));
//                if (b.get(j) == Boolean.TRUE) {
//                    bolresult.add(Boolean.FALSE);
//                } else {
//                    bolresult.add(Boolean.TRUE);
//
//                }
//                j++;
//            }
//            result.add(Element.POSITIVE_INFINITY);
//            bolresult.add(Boolean.TRUE);
//        }
//
//        return new number.SubsetR2(result, bolresult);
//    }

    public com.mathpar.number.SubsetR2 add(com.mathpar.number.SubsetR2 x, Ring ring){
        return   null; //this.union(x, ring);
    }

    public com.mathpar.number.SubsetR2 multiply(com.mathpar.number.SubsetR2 x, Ring ring){
        return  null; //this.intersection(x, ring);
    }

    //--------------------------------------------------------------------------
//    private Element[] fromQtoNumberR(Element[] r, Ring ring){
//      Element[] rr = new Element[r.length];//массив корней над R
// // int granica = 0;
//    for(int i = 0; i < r.length; i++){
//          if(r[i] instanceof Fraction){
//            if(((Fraction)r[i]).num instanceof F){
//              Fraction f = new Fraction(((Fraction)r[i]).num, ((Fraction)r[i]).denom);
//              f.num = NumbersToR64(f.num, ring);
//              f.num = new FvalOf(ring).valOf(f.num, new Element[0]);
//              rr[i] = new NumberR64(f.doubleValue());
//            }else
//            if(((Fraction)r[i]).denom instanceof F){
//              Fraction f = new Fraction(((Fraction)r[i]).num, ((Fraction)r[i]).denom);
//              f.denom = NumbersToR64(f.denom, ring);
//              f.denom = new FvalOf(ring).valOf(f.denom, new Element[0]);
//              rr[i] = new NumberR64(f.doubleValue());
//            }else
//              rr[i] = new NumberR64(((Fraction)r[i]).doubleValue());
//          }else
//          if(r[i] instanceof F){
//              Element f = new F(((F)r[i]).name, ((F)r[i]).X);
//              f = NumbersToR64(f, ring);
//              f = new FvalOf(ring).valOf(f, new Element[0]);
//              rr[i] = f;
//            }
//            else
//            rr[i] = r[i].toNumber(Ring.R64, ring);
//    }
//    return rr;
//    }

    //--------------------------------------------------------------------------
//    public Element NumbersToR64(Element num, Ring ring){
//      if (num instanceof F){
//        F f = (F)num;
//        for(int i = 0; i < f.X.length; i++){
//          f.X[i] = (f.X[i] instanceof F) ? NumbersToR64(f.X[i], ring) : f.X[i].toNumber(Ring.R64, ring);
//        }
//      }
//      return num;
//    }

    //--------------------------------------------------------------------------
//    @Override
//    public String toString(Ring ring) {
//        StringBuffer res = new StringBuffer("");
//        if(this.set.isEmpty() && this.border.isEmpty()) return res.append("\\emptyset").toString();
//        int k = this.set.size();
//        if (k == 1) {
//            StringBuffer tempSt=new StringBuffer(this.set.get(0).toString(ring));
//            if(tempSt.indexOf("(")!=-1){
//            res.append("[" + tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")"))+"]");
//              }else{
//            res.append("[" + tempSt.toString()+"]");
//              }
//           // res.append("[" + this.set.get(0).toString(ring) + "]");
//            return res.toString();
//        }
//        if(this.set.get(0).compareTo(Element.NEGATIVE_INFINITY, ring) == 0
//                && this.set.get(1).compareTo(Element.POSITIVE_INFINITY, ring) == 0) {
//           res.append("( -"+"\\infty"+","+"\\infty");
//           res.append(")");
//           return res.toString();
//        }
//        int l = 0;
//        int i;
//        for (i = 0; i < k; i++) {
//            l++;
//            if (l % 2 == 0) {
//                if(this.set.get(i).compareTo(Element.POSITIVE_INFINITY, ring) == 0)
//                    res.append("\\infty");
//                else{
//                 StringBuffer tempSt=new StringBuffer(this.set.get(i).toString(ring));
//                    if(tempSt.indexOf("(")!=-1){
//                    res.append(tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")")));
//                    }else{
//                    res.append(tempSt.toString());
//                    }
//              //      res.append(this.set.get(i).toString(ring));
//                }
//
//                if (this.border.get(i).equals(true)) {
//                    if (i != k - 1) {
//                        res.append(")"+"\\cup");
//                    } else {
//                        res.append(")");
//                    }
//                } else {
//                    if (i != k - 1) {
//                        res.append("]"+"\\cup");
//                    } else {
//                        res.append("]");
//                    }
//                }
//                l = 0;
//            } else {
//                if ((this.set.get(i+1).equals(this.set.get(i), ring))){
//                   if(this.border.get(i+1).equals(Boolean.FALSE) && this.border.get(i).equals(Boolean.FALSE)){
//                    StringBuffer tempSt=new StringBuffer(this.set.get(i).toString(ring));
//                    if(tempSt.indexOf("(")!=-1){
//                    res.append("\\{" + tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")")));
//                    }else{
//                    res.append("\\{" + tempSt.toString());
//                    }
//                    if(i == k-2) res.append("\\}");
//                    else res.append("\\}" + "\\cup");
//                    i++;
//                    l = 0;
//                    continue;
//                }else{
//                    res.append("please use []");
//                    break;
//                }
//                }
//                if (this.border.get(i).equals(Boolean.TRUE)) {
//                    res.append("(");
//                } else {
//                    res.append("[");
//                }
//                if(this.set.get(i).compareTo(Element.NEGATIVE_INFINITY,ring) == 0)
//                    res.append("-" + "\\infty" + ",");
//                else{
//                 StringBuffer tempSt=new StringBuffer(this.set.get(i).toString(ring));
//                    if(tempSt.indexOf("(")!=-1){
//                    res.append(tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")"))+ ",");
//                    }else{
//                    res.append(tempSt.toString()+ ",");
//                    }
//               //     res.append(this.set.get(i).toString(ring) + ",");
//                }
//            }
//        }
//        return res.toString();
//    }
//

    @Override
     public String toString(Ring ring) {
        Element[] e4=new Element[4];
        e4=set.get(0).V;
       return "SubsetR2("+e4[0].toString(ring)+", "+e4[1].toString(ring)+", "+e4[2].toString(ring)+", "+e4[3].toString(ring)+")";
    }
     public static void main(String[] args) {
         Ring ring = new Ring("R[x,y,z]");
          SubsetR2 nr1 = new  SubsetR2( ring.numberZERO, ring.numberONE, new F(F.COS,ring.varPolynom[0]), new Polynom("x^3/10", ring) );

         System.out.println("cup = " + nr1.toString(ring));
//         NumberR[] N1 = new NumberR[]{new NumberR(0),new NumberR(2),new NumberR(8),new NumberR(9)};
//         NumberR[] N2 = new NumberR[]{new NumberR(1), new NumberR(1)};
//
//         Boolean[] b1 = new Boolean[]{true,true,false,true};
//         Boolean[] b2 = new Boolean[]{false,false};
//
//         System.out.println("result = " + (new SubsetR2(N2,b2)).symmetricSubtraction(new SubsetR2(N1,b1)).toString(ring));
//         System.out.println("result = " + (new SubsetR2(N2,b2)).symmetricSubtraction(new SubsetR2(N1,b1)).sumOfAllIntervals().toString(ring));
    }
}
