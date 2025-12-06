package com.mathpar.number;

import com.mathpar.func.F;
import com.mathpar.func.FUtils;
import com.mathpar.func.FvalOf;
import com.mathpar.func.Page;
import com.mathpar.func.parser.Parser;
import java.util.ArrayList;
import java.util.Arrays;

/** Subsets of real numbers R, which obtained by finite number of intervals.
 * This Subsets are closed for operation of union and intersection.
 *  С подмножествами SubsetR выполняются  операции пересечения, объединения,
 * сравнения.
 * Каждое подмножество задается упорядоченным  массивом интервалов вида {a,b},
 * где a - начало интервала, b - конец интервала. Если a=b, то это изолированное число.
 * Пример: set= {NEGATIVE_INFINITY,  -3, 1,10, 12,12, 34,POSITIVE_INFINITY}
 * border= {t,t,f,f,f,f,f,t} ОБОЗНАЧАЕТ: {(-oo - -3),(1-10],[12,12],[34-oo)}
 *  f=false= close interval, t=true=open interval.
 *@author Саша, Кривенцев Илья, Дима Шляпин, me ....
 */
public class SubsetR extends Element{

    ArrayList<Element> set = new ArrayList<Element>();
    ArrayList<Boolean> border = new ArrayList<Boolean>();

    public static final SubsetR empty=new SubsetR();
    /**
     * This is the \emptyset
     */
    public SubsetR() {
        set = new ArrayList<Element>();
        border = new ArrayList<Boolean>();
    }

    public SubsetR(ArrayList<Element> set, ArrayList<Boolean> border) {
        this.set = set;
        this.border = border;
    }

    public SubsetR(NumberR[] setR, Boolean[] borderI) {
        set = new ArrayList(setR.length);
        set.addAll(Arrays.asList(setR));
        border = new ArrayList(borderI.length);
        border.addAll(Arrays.asList(borderI));

    }

    public SubsetR(Element[] setE, Boolean[] borderI) {
        set = new ArrayList(setE.length);
        set.addAll(Arrays.asList(setE));
        border = new ArrayList(borderI.length);
        border.addAll(Arrays.asList(borderI));
    }

    public SubsetR(String str, Ring ring) {
        str = str.replaceAll("\\\\cup", ",");
        str = str.replaceAll(" ", "");
        String[] ll = FUtils.cutByCommasSets(str, ring);
        SubsetR res = new SubsetR();
        for(int i = 0; i < ll.length; i++){
        Element el1; Element el2;
        Boolean b1 = false; Boolean b2 = false;
        if(ll[i].charAt(0) == '('){b1 = true;}
        else if(ll[i].charAt(0) == '['){b1 = false;}
        else if(ll[i].charAt(0) == '{' | ll[i].charAt(ll[i].length() - 1) == '}'){
            b1 = false; b2 = b1;
            el1 = Parser.getF(ll[i].substring(1, ll[i].indexOf('}')), ring); el2 = el1;
            SubsetR setR = new SubsetR(el1,el2,b1,b2);
            res = res.union(setR, ring);
            continue;
        }
        if(ll[i].charAt(ll[i].length() - 1) == ')'){b2 = true;}
        else if(ll[i].charAt(ll[i].length() - 1) == ']'){b2 = false;}
        String st1 = ll[i].substring(1, ll[i].indexOf(','));
        if (st1.equals("-\\infty")) {el1 = Element.NEGATIVE_INFINITY;}
        else if(st1.equals("\\infty")) {el1 = Element.POSITIVE_INFINITY;}
        else {el1 = Parser.getF(st1, ring);}
        String st2 = ll[i].substring(ll[i].indexOf(',') + 1, ll[i].length() - 1);
        if (st2.equals("-\\infty")) {el2 = Element.NEGATIVE_INFINITY;}
        else if(st2.equals("\\infty")) {el2 = Element.POSITIVE_INFINITY;}
        else {el2 = Parser.getF(st2, ring);}
  //      Element f1 = (el1.isItNumber()) ? el1 : new F(((F)el1).name, ((F)el1).X);
   //     f1 = NumbersToR64(f1, ring);
   //     f1 = new FvalOf(ring).valOf(f1, new Element[0]);
   //     Element f2 = (el2.isItNumber()) ? el2 : new F(((F)el2).name, ((F)el2).X);
   //     f2 = NumbersToR64(f2, ring);
 //       f2 = new FvalOf(ring).valOf(f2, new Element[0]);
        if(  //f1.compareTo(f2, ring)  
                CompareOf(el1,el2,ring) == 1){
          try{
            throw new Exception(ring.exception.append("Неправильный порядок границ подмножества ").append(ll[i]).toString());
          }catch(Exception e){
            System.out.println(e);
          }
        }
        SubsetR setR = new SubsetR(el1,el2,b1,b2);
        res = res.union(setR, ring);
        }
        set = res.set;
        border = res.border;
    }

    public SubsetR(Element el1, Element el2, boolean b1, boolean b2){
        set.add(el1);
        set.add(el2);
        border.add(b1);
        border.add(b2);
    }
    /**
     * Subset which consists of one point 
     * @param el1 
     */
    public SubsetR(Element el1 ){
        set.add(el1);
        set.add(el1);
        border.add(false);
        border.add(false);
    }
    static SubsetR realNumbers=new SubsetR(Element.NEGATIVE_INFINITY, Element.POSITIVE_INFINITY,true,true);
    
//    /** Сумма всех отрезков состовляющих подмножество |a|=Ea[i],
//     * a[i]=Отрезки Е=Общая сумма (объединение)
//     *
//     * @param a
//     * @return с
//     */
//    public Element sumOfAllIntervals_() {
//        ArrayList<Element> a = set;
//        ArrayList<Element> b = set;
//        NumberR c = NumberR.ZERO;
//        for (int i = 0; i < a.size(); i += 2) {
//            Element el1 = (NumberR)(((NumberR)a.get(i + 1)).subtract(((NumberR)a.get(i))));
//            Element el2 = (NumberR)(((NumberR)el1).add(NumberR.ONE));
//            c = (NumberR)c.add((NumberR)el2);
//        }
//        return c;
//    }

    /**
     * Сравнение множества с множеством, передаваемом в параметре.
     * Результатом является TRUE при совпадениии и FALSE при несовпадении
     * @param s множество-параметр
     * @return TRUE при совпадении и FALSE при несовпадении
     */
    public boolean equals(SubsetR s) {
        int size = this.set.size();
        if (size != s.set.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.set.get(i).equals(s.set.get(i))) {
                return false;
            }
            if (!this.border.get(i).equals(s.border.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Объеденение множеств
     * @param a множество-параметр
     * @return объединение множеств
     */
    public SubsetR union(SubsetR a, Ring ring) {
        Element[] a1 = new Element[set.size()];
        set.toArray(a1);
        Element[] a2 = new Element[a.set.size()];
        a.set.toArray(a2);
        Boolean[] b1 = new Boolean[border.size()];
        border.toArray(b1);
        Boolean[] b2 = new Boolean[a.border.size()];
        a.border.toArray(b2);
        ArrayList<Element> result = new ArrayList<Element>();
        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
        //переводим все в R
        Element[] R1 =a1; //fromQtoNumberR(a1, Ring.ringZxyz);
        Element[] R2 =a2; //fromQtoNumberR(a2, Ring.ringZxyz);
        int i = 0;
        int j = 0;
//цикл по всем элементам вектора set 1 множества (i) , и 2 множества (j)
        while (i < a1.length && j < a2.length){
            Element N1 = R1[i];
            Element N2 = R2[j]; int comp=CompareOf(N1,N2,ring);
            if (comp == -1) {

                if (j % 2 == 0) {
                    result.add(a1[i]);
                    if(b1[i] == false) bolresult.add(false);
                    else bolresult.add(true);
                }
                i++;
            }
            if (comp == 1) {
                if (i % 2 == 0) {
                    result.add(a2[j]);
                    if(b2[j] == false) bolresult.add(false);
                    else bolresult.add(true);
                }
                j++;
            }
            if (comp == 0) {
                if (i % 2 == j % 2) {
                    result.add(a1[i]);
                    if(b1[i] == false) bolresult.add(false);
                    else bolresult.add(b2[j]);
                }else{
                if(b1[i] == true & b1[i] == b2[j]){
                  result.add(a1[i]);
                  bolresult.add(true);
                  result.add(a1[i]);
                  bolresult.add(true);
                }}
                i++;
                j++;
            }
        }

        if (i == a1.length) {
          for (int k = j; k < a2.length; k++) {
            result.add(a2[k]);
            bolresult.add(b2[k]);
          }
        }

        if (j == a2.length) {
          for (int k = i; k < a1.length; k++) {
            result.add(a1[k]);
            bolresult.add(b1[k]);
          }
        }
        return new SubsetR(result, bolresult);
    }
    @Override
    public Element intersection(Element a, Ring ring){
      if(a instanceof SubsetR)return intersection(((SubsetR) a),  ring);
      return new F(F.CAP, this, a);
    }
       @Override
    public Element union(Element a, Ring ring){
      if(a instanceof SubsetR)return union(((SubsetR) a),  ring);
      return new F(F.CUP, this, a);
    }
    /**
     * Пересечение  множеств
     * @param a множество-параметр
     * @return Пересечение множеств
     */
    public SubsetR intersection(SubsetR a, Ring ring) {
        Element[] a1 = new Element[set.size()];
        set.toArray(a1);
        Element[] a2 = new Element[a.set.size()];
        a.set.toArray(a2);
        Boolean[] b1 = new Boolean[border.size()];
        border.toArray(b1);
        Boolean[] b2 = new Boolean[a.border.size()];
        a.border.toArray(b2);
        ArrayList<Element> result = new ArrayList<Element>();
        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
        //переводим все в R
        Element[] R1 = a1;//fromQtoNumberR(a1, Ring.ringZxyz);
        Element[] R2 =a2; // fromQtoNumberR(a2, Ring.ringZxyz);

        int i = 0;
        int j = 0;
        while (i < a1.length && j < a2.length){
            Element N1 = R1[i];
            Element N2 = R2[j]; int comp=CompareOf(N1,N2,ring);
            if (comp == -1) {
                if (j % 2 == 1) {
                    result.add(a1[i]);
                    if(b1[i] == false) bolresult.add(false);
                    else bolresult.add(true);
                }
                i++;
            }
            if (comp == 1) {
                if (i % 2 == 1) {
                    result.add(a2[j]);
                    if(b2[j] == false) bolresult.add(false);
                    else bolresult.add(true);
                }
                j++;
            }
            if (comp == 0) {
                if (i % 2 == j % 2) {
                    result.add(a1[i]);
                    if(b1[i] == false & b2[j] == false) bolresult.add(false);
                    else bolresult.add(true);
                } else {
                    if(b1[i] == false && b2[j] == false){
                    result.add(a1[i]);
                    if(b1[i] == false) bolresult.add(false);
                    else bolresult.add(true);
                    result.add(a2[j]);
                    if(b2[j] == false) bolresult.add(false);
                    else bolresult.add(true);
                    }
                }
                i++;
                j++;
            }
        }
        return new SubsetR(result, bolresult);
    }

//    /** Set Theoretic Difference
//     * (Теоретико-числовая разность множеств)
//     * @param  second set (множество-параметр)
//     * @return Difference  (Разность множеств)
//     */
//    public SubsetR setTheoreticDifference1(SubsetR a, Ring ring) {
//        Element[] a1 = new Element[set.size()]; set.toArray(a1);
//        Element[] a2 = new Element[a.set.size()]; a.set.toArray(a2);
//        Boolean[] b1 = new Boolean[border.size()];  border.toArray(b1);
//        Boolean[] b2 = new Boolean[a.border.size()];  a.border.toArray(b2);
//        ArrayList<Element> result = new ArrayList<Element>();
//        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
//        //переводим все в R
//        Element[] R1 = fromQtoNumberR(a1, Ring.ringZxyz);   //  ???
//        Element[] R2 = fromQtoNumberR(a2, Ring.ringZxyz);   // && ???
//        int i = 0;
//        int j = 0;
////цикл по всем элементам вектора set 1 множества (i) , и 2 множества (j)
//        while (i < a1.length && j < a2.length) {
//            Element N1 = R1[i];  Element N2 = R2[j];
//            if (N1.compareTo(N2, ring) == -1) {
//               if(j % 2 == 0) {result.add(a1[i]); bolresult.add(b1[i]); //   //
//                  if(b1[i] == false) bolresult.add(false);
//                  else bolresult.add(true);             }
//               if (i < a1.length - 1) i++; else break;
//            }
//            else if (N1.compareTo(N2, ring) == 1) {
//                 if (i % 2 == 1) {result.add(a2[j]); bolresult.add(!b1[i]); //   //
//                     if(b2[j] == false) bolresult.add(true);
//                     else bolresult.add(false);             }
//                 if (j < a2.length - 1) j++;
//                 else {result.add(a1[i]); bolresult.add(b1[i]); //   //
//                     if(b1[i] == true) bolresult.add(true);else bolresult.add(false); 
//                     break;            }
//            }
//            else if (N1.compareTo(N2, ring) == 0) {
//                if (i % 2 == j % 2) {result.add(a1[i]);
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
//                  if (i < a1.length - 1) i++; else  break;
//              }
//            }
//
//        if (R1[i].compareTo(R2[j], ring) == 1) {
//          for (int k = i+1; k < a1.length; k++) {
//            result.add(a1[k]);
//            bolresult.add(b1[k]);
//          }
//        }
//        return new SubsetR(result, bolresult);
//    }
    
     @Override
   public Element setTheoreticDifference(Element a, Ring ring) {  
      if(a instanceof SubsetR)return setTheoreticDifference(((SubsetR) a),  ring);
      return new F(F.SET_MINUS, this, a);
    }   
    /** Set Theoretic Difference
     * (Теоретико-числовая разность множеств)
     * @param  second set (множество-параметр)
     * @return Difference  (Разность множеств)
     */
    public SubsetR setTheoreticDifference(SubsetR a, Ring ring) { 
        ArrayList<Element> aSet = a.set;
        ArrayList<Boolean> aBor = a.border;
        ArrayList<Element> result = new ArrayList<Element>();
        ArrayList<Boolean> bolresult = new ArrayList<Boolean>();
        int i = 0;
        int j = 0;      
        int k=0; // pos on result
        Boolean bi=true, bj, kFirst=true, iFirst=true, jFirst;
        Element ai=null;
        //цикл по всем элементам вектора set 1 множества (i) , и 2 множества (j)
        while (i < set.size() && j < aSet.size()) {
        ai=set.get(i); Element aj=aSet.get(j);
        bi=border.get(i); bj=aBor.get(j); kFirst=(k%2==0);
        int comp=ai.compareTo(aj, ring);  // 1,0,-1 = greater, eq, less     
        iFirst=(i%2==0); jFirst=(j%2==0); // first element of interval (false=last el. of interfal)
        if((iFirst)&&(jFirst))
              switch(comp){ 
                    case -1: result.add(ai); bolresult.add(bi);  i++; k++; break;
                    case  0: if (bj&&(! bi)){result.add(ai); result.add(ai); bolresult.add(false); 
                                bolresult.add(false); k+=2;}i++;j++; break;
                    case  1: if(!kFirst){result.add(aj); bolresult.add(!bj); j++;k++;}j++; break;
        }else if((iFirst)&&(!jFirst))
                switch(comp){
                    case 1: j++;break; //result.add(a1[i]); bolresult.add(b1[i]);  i++; k++; break;
                    case  0: if(!kFirst && bj){ result.add(ai); bolresult.add(bi); k++; i++; }  j++; break;
                    case  -1: i++; break; 
        }else if((!iFirst)&&(jFirst))
                switch(comp){
                    case  1: if(!kFirst){ result.add(aj); bolresult.add(!bj); k++;} j++; break;
                    case  0: if(!kFirst){ result.add(ai); bolresult.add(bj||!bi); k++;}  j++;i++; break;
                    case  -1: if(!kFirst){  result.add(ai); bolresult.add(bi); k++;}   i++; break;
         }else   if((!iFirst)&&(!jFirst))   
                  switch(comp){
                    case -1:    i++; break;
                    case  0: if ((bj&&!bi)&&kFirst){result.add(ai); result.add(ai); bolresult.add(false); 
                                bolresult.add(false);k+=2; }                    
                                i++; j++; break;
                    case  1:
                        if(kFirst){result.add(aj); bolresult.add(!bj);}
                             else{result.add(ai); bolresult.add(bi); i++;} k++;j++; break;
              }  
        }    
       if( i < set.size()){
               if(!kFirst){if(!iFirst){result.add(ai); bolresult.add(bi);i++; k++;} 
                       else {k++; i++; ring.exception.append("Set not correct"+ this.toString(ring)); }}
               result.addAll( set.subList(i, set.size()));
               bolresult.addAll(border.subList(i, border.size()));
       }  
        return new SubsetR(result, bolresult);
    } 
    
    @Override
   public Element symmetricDifferece(Element a, Ring ring) {  
      if(a instanceof SubsetR)return symmetricDifferece(((SubsetR) a),  ring);
      return new F(F.SYMMETRIC_DIFFERENCE, this, a);
    }
/**
 * Symmetric setTheoreticDifference of two sets: a and this
 * @param a
 * @param ring Ring
 * @return Symmetric setTheoreticDifference
 */
     public SubsetR symmetricDifferece(SubsetR a, Ring ring) {
       return (this.union(a, ring)).setTheoreticDifference(this.intersection(a, ring), ring);
   }
/**
 * This is the same as symmetric setTheoreticDifference.
 * @param a
 * @param ring Ring
 * @return symmetric setTheoreticDifference
 */
     
     public SubsetR subtract(SubsetR a, Ring ring) {return symmetricDifferece(a, ring);}
   
    @Override
     public SubsetR subtract(Element a, Ring ring) {SubsetR numb=this;
            if(a instanceof SubsetR)  numb= (SubsetR)a;   
            else if (a.isItNumber())   numb= new SubsetR(a); else return numb;
            return symmetricDifferece(numb, ring);
   }
    /**
     * Дополнение множества
     * @param a параметр-множество
     * @return дополнение множеств
     */
    @Override
    public Element complement(Ring ring) {
       SubsetR a=new SubsetR(set, border);
       return realNumbers.setTheoreticDifference(a, ring);
    }
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
//        return new SubsetR(result, bolresult);
//    }

    public SubsetR add(SubsetR x, Ring ring){
        return this.union(x, ring);
    }

    public SubsetR multiply(SubsetR x, Ring ring){
        return this.intersection(x, ring);
    }

    
 private int CompareOf(Element N1, Element N2, Ring ring){
 Element[] rr = new Element[]{N1,N2 };
 rr=fromElementtoNumberR64(rr,ring);
 return rr[0].compareTo(rr[1]);
}
    private Element[] fromElementtoNumberR64(Element[] r, Ring ring){
      Element[] rr = new Element[r.length];//массив корней над R
 // int granica = 0;
    for(int i = 0; i < r.length; i++){
          if(r[i] instanceof Fraction){
            if(((Fraction)r[i]).num instanceof F){
              Fraction f = new Fraction(((Fraction)r[i]).num, ((Fraction)r[i]).denom);
              f.num = NumbersToR64(f.num, ring);
              f.num = new FvalOf(ring).valOf(f.num, new Element[0]);
              rr[i] = new NumberR64(f.doubleValue());
            }else
            if(((Fraction)r[i]).denom instanceof F){
              Fraction f = new Fraction(((Fraction)r[i]).num, ((Fraction)r[i]).denom);
              f.denom = NumbersToR64(f.denom, ring);
              f.denom = new FvalOf(ring).valOf(f.denom, new Element[0]);
              rr[i] = new NumberR64(f.doubleValue());
            }else
              rr[i] = new NumberR64(((Fraction)r[i]).doubleValue());
          }else
          if(r[i] instanceof F){
              Element f = new F(((F)r[i]).name, ((F)r[i]).X);
              f = NumbersToR64(f, ring);
              f = new FvalOf(ring).valOf(f, new Element[0]);
              rr[i] = f;
            }
            else
            rr[i] = r[i].toNumber(Ring.R64, ring);
    }
    return rr;
    }
   //&&&&&&&&&&&&&&&&&&?????????????????????????????????????????????????????
   // Для подсчета значения рациональной функции, которая содержит
   // трансцендентные числа e, pi или алгебраические \sqrt(2) и пр.
   // НО где же вычисление функций типа корень?
    // ....... кажется для этого есть valOf....
    public Element NumbersToR64(Element num, Ring ring){
      if (num instanceof F){
        F f = (F)num;
        Element[] x=new Element[f.X.length];
        for(int i = 0; i < f.X.length; i++){
          x[i] = (f.X[i] instanceof F) ? NumbersToR64(f.X[i], ring) : f.X[i].toNumber(Ring.R64, ring);
        } f.X=x;
      }
      return num;
    }
    @Override
    public String toString() {return toString(Ring.ringR64xyzt);}
    @Override
    public String toString(Ring ring) {
        StringBuilder res = new StringBuilder("");
        if(this.set.isEmpty() && this.border.isEmpty()) return res.append("\\emptyset").toString();
        int k = this.set.size();
        if (k == 1) {
            String tempSt=this.set.get(0).toString(ring);
//            if(tempSt.contains("(")){
//            res.append("\\[" + tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")"))+"\\]");
//              }else{
   //         res.append("\\[" + tempSt+"\\]");
  //            }
           // res.append("[" + this.set.get(0).toString(ring) + "]");
            return "\\[" + tempSt+"\\]";   // res.toString();
        }
        if(this.set.get(0).compareTo(Element.NEGATIVE_INFINITY, ring) == 0
                && this.set.get(1).compareTo(Element.POSITIVE_INFINITY, ring) == 0) {
           res.append("\\( -"+"\\infty"+","+"\\infty");
           res.append("\\)");
           return res.toString();
        }
        int l = 0;
        int i;
        for (i = 0; i < k; i++) {
            l++;
            if (l % 2 == 0) {
                if(this.set.get(i).compareTo(Element.POSITIVE_INFINITY, ring) == 0)
                    res.append("\\infty");
                else{
                 StringBuffer tempSt=new StringBuffer(this.set.get(i).toString(ring));
//                    if(tempSt.indexOf("(")!=-1){
//                    res.append(tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")")));
//                    }else{
                    res.append(tempSt.toString());
    //                }
              //      res.append(this.set.get(i).toString(ring));
                }

                if (this.border.get(i).equals(true)) {
                    if (i != k - 1) {res.append("\\)"+"\\cup");} 
                    else { res.append("\\)");
                    }
                } else {
                    if (i != k - 1) {res.append("\\]"+"\\cup");} 
                    else {res.append("\\]");}
                }
                l = 0;
            } else {
                if ((this.set.get(i+1).equals(this.set.get(i), ring))){
                   if(this.border.get(i+1).equals(Boolean.FALSE) && this.border.get(i).equals(Boolean.FALSE)){
                    StringBuffer tempSt=new StringBuffer(this.set.get(i).toString(ring));
                    if(tempSt.indexOf("(")!=-1){
                    res.append("\\{" + tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")")));
                    }else{res.append("\\{" + tempSt.toString()); }
                    if(i == k-2) res.append("\\}");
                    else res.append("\\}" + "\\cup");
                    i++;
                    l = 0;
                    continue;
                }else{ res.append("please use \\[ \\]"); break;}
                }
                if (this.border.get(i).equals(Boolean.TRUE)) {res.append("\\(");} 
                else {res.append("\\["); }
                if(this.set.get(i).compareTo(Element.NEGATIVE_INFINITY,ring) == 0)
                    res.append("-" + "\\infty" + ",");
                else{
                 StringBuffer tempSt=new StringBuffer(this.set.get(i).toString(ring));
//                    if(tempSt.indexOf("(")!=-1){
//                       res.append(tempSt.substring(tempSt.indexOf("(")+1, tempSt.indexOf(")"))+ ",");
//                    }else{
                        res.append(tempSt.toString()+ ","); 
                //    }
               //     res.append(this.set.get(i).toString(ring) + ",");
                }
            }
        }
        return res.toString();
    }
         
    public Boolean isEmpty(Ring ring) {
        return ((set.isEmpty())&&(border.isEmpty()));   
    }
        @Override
    public Boolean isZero(Ring ring) {
        return ((set.isEmpty())&&(border.isEmpty()));   
    } 
     public static void main(String[] args) {
         Ring ring = new Ring("Q[x]");
         ring.page=new Page(ring,true);
         SubsetR nr1 = new SubsetR("(1/3,3 ),[5,16)",ring);
         SubsetR nr2 = new SubsetR("(2,4),[10,20) ",ring);
      //   System.out.println("ssd = " + nr2.setTheoreticDifference(nr1, ring).toString(ring));
          System.out.println("cup = " + nr1.union(nr2, ring).toString(ring));
    }
}
