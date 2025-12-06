/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.polynom;
import com.mathpar.number.*;
import com.mathpar.func.*;
import java.util.ArrayList;
/**
 *
 * @author dmitry
 */

public class solveInequality {
    public solveInequality(){
    }

    public Element solveI(Element e, int znak, Ring ring){
        switch (e.numbElementType()) {
            case Ring.Polynom:
              Element x = ((Polynom)e).coeffs[0];
              return (x.isNegative()) ? solvePolynom((Polynom)e, ring, -znak) : solvePolynom((Polynom)e, ring, znak);
            case Ring.F:
              CanonicForms cf = ring.CForm;//new CanonicForms(ring,true);//2015
              Element p = cf.ElementToPolynom(e, true);
              if(p instanceof Fraction){
                Fraction fr = (Fraction)p;
                Element e1 = fr.num.multiply(fr.denom, cf.newRing);
                Element e2 = fr.denom;
                return solveI(new Element[]{e1,e2}, new int[]{znak,Element.NOT_EQUAL},cf.newRing);
              }
              Polynom poly = (Polynom)p;
              return solveI(poly, znak, cf.newRing);
            case Ring.FactorPol:
              Element y = ((FactorPol)e).multin[0];
              return (y.isNegative()) ? solveFactorPol((FactorPol)e, ring, -znak) : solveFactorPol((FactorPol)e, ring, znak);
            default:
              return null;
        }
    }

    public Element solveI(Element[] e, int[] znak, Ring ring){
        if(e.length != znak.length) return null;
        Element r = solveI(e[0], znak[0], ring);
        for(int i = 1; i < e.length; i++){
          r = ((SubsetR)r).intersection((SubsetR)solveI(e[i], znak[i], ring), ring);
        }
        return r;
    }

    public Element solvePolynom(Polynom p, Ring ring, int znak){
        FactorPol fpol = p.factorOfPol_inQ(false, ring);
        return solveFactorPol(fpol, ring, znak);
    }

    public Element solveFactorPol(FactorPol fpol, Ring ring, int znak){
        CanonicForms cfr=ring.CForm; //new CanonicForms(ring); //2015
        int s = 0;//количество корней
        int[] degrees=fpol.multin[fpol.multin.length-1].degrees();
        int var=degrees.length-1; // номер переменной в кольце 
        
        ArrayList<Element> roots = new ArrayList();
        ArrayList<Boolean> border = new ArrayList<>();
        ArrayList<Boolean> powofroot = new ArrayList<>();
        for(int i = 0; i < fpol.multin.length; i++){
            int deg=fpol.multin[i].degree(var);
            if(fpol.multin[i].isItNumber() || fpol.multin[i].powers[var] > 4) continue;
            if(deg > 1 & deg < 5){
                Polynom p = fpol.multin[i];
                Element pp = new SolveEq(ring).solvePolynomEq(p, var, ring);
                Element[] e = (pp instanceof VectorS)?((VectorS)pp).V: new Element[]{pp}  ;//вектор корней
                if(fpol.powers[i] % 2 == 0){
                  for(int j = 0; j < e.length; j++){
                    if(e[j].isNaN() || e[j] instanceof Complex) continue;
                      roots.add(e[j]);//сначала добавили корень
                      //потом добавили скобку(круглую или квадратную)
                    if(znak == Element.GREATER || znak == Element.LESS) {
                      border.add(true);//круглая
                    }
                    else if(znak == Element.GREATER_OR_EQUAL || znak == Element.LESS_OR_EQUAL){
                      border.add(false);//квадратная
                    }
                  powofroot.add(true);//показали что корень четной степени
                  s++;//отметили что добавили корень
                  }
                }else{
                for(int j = 0; j < e.length; j++){
                    if(e[j].isNaN() || e[j] instanceof Complex) continue;
                  roots.add(e[j]);//добавили корень
                  //добавили знак
                  if(znak == Element.GREATER || znak == Element.LESS) border.add(true);
                  else if(znak == Element.GREATER_OR_EQUAL || znak == Element.LESS_OR_EQUAL) border.add(false);
                  powofroot.add(false);//показали что корень нечетной степени
                  s++;
                }
                }
                continue;
            }
              switch(znak){
                  case Element.EQUAL:
                  case Element.NOT_EQUAL:
                     if (fpol.multin[i].coeffs.length == 1){
                       roots.add(new NumberR(0));
                       roots.add(new NumberR(0));
                       border.add(false);
                       border.add(false);
                       powofroot.add(false);
                       powofroot.add(false);
                       s++;
                      }else{
                       roots.add(fpol.multin[i].coeffs[1].negate(ring).divide(fpol.multin[i].coeffs[0], ring));
                       roots.add(fpol.multin[i].coeffs[1].negate(ring).divide(fpol.multin[i].coeffs[0], ring));
                       border.add(false);
                       border.add(false);
                       powofroot.add(false);
                       powofroot.add(false);
                       s++;
                     }
                      break;
                  case Element.GREATER:
                  case Element.LESS:
                      roots.add((fpol.multin[i].coeffs.length == 1) ? new NumberR(0) :
                              fpol.multin[i].coeffs[1].negate(ring).divide(fpol.multin[i].coeffs[0], ring));
                      border.add(true);
                      powofroot.add((fpol.powers[i] % 2 == 0) ? true : false);
                      s++;
                      break;
                  case Element.GREATER_OR_EQUAL:
                  case Element.LESS_OR_EQUAL:
                      roots.add((fpol.multin[i].coeffs.length == 1) ? new NumberR(0) :
                              fpol.multin[i].coeffs[1].negate(ring).divide(fpol.multin[i].coeffs[0], ring));
                      border.add(false);
                      powofroot.add((fpol.powers[i] % 2 == 0) ? true : false);
                      s++;
                      break;
                  default:
                      break;
              }
          }
  Element[] r = new Element[roots.size()];//массив всех корней(иррациональных, дробных и так далее)
  roots.toArray(r);//массив корней, которые на вывод
  Boolean[] b = new Boolean[border.size()];
  border.toArray(b);//массив скобок ( ) или [ ]
  Boolean[] p = new Boolean[powofroot.size()];
  powofroot.toArray(p);//массив крневых степеней: если четная, то там true
  Element[] rr = fromQtoNumberR(r, ring);//массив корней над NumberR64
    /*после всех вышеперделанных процедур у нас получилось три массива:
     * массив r - массив корней на вывод
     * массив rr - массив корней, котороые пригодны для сортировки
     * массив b - булевый массив с границами
     */
  int[] st = new int[rr.length];
  st = Array.sortPosUp(rr, ring);//массив позиций
  ArrayList<Element> setE = new ArrayList();
  ArrayList<Boolean> bolresult = new ArrayList();
  ArrayList<Boolean> pows = new ArrayList();
  for(int i = 0; i < st.length; i++){
    setE.add(r[st[i]]);
    bolresult.add(b[st[i]]);
    pows.add(p[st[i]]);
  }//отсортировали, теперь надо либо добавить, либо убрать некоторые корни
  //теперь считаем, что на крайнем правом конце стоит знак плюс
  int flag = 2;//если четный то мы на плюсе
  for(int i = pows.size() - 1; i >= 0; i--){
      if(setE.get(i) == Element.POSITIVE_INFINITY || setE.get(0) == Element.POSITIVE_INFINITY)
          continue;
      if(pows.get(i) == false){flag++; continue;}
      if(flag % 2 == 0){
        if(znak == Element.GREATER_OR_EQUAL || znak == Element.LESS){
          setE.remove(setE.get(i));
          bolresult.remove(bolresult.get(i));
          s--;
        }else if(znak == Element.GREATER || znak == Element.LESS_OR_EQUAL){
          setE.add(i, setE.get(i));
          bolresult.add(i, bolresult.get(i));
          s++;
        }
      }else{
        if(znak == Element.GREATER || znak == Element.LESS_OR_EQUAL){
          setE.remove(setE.get(i));
          bolresult.remove(bolresult.get(i));
          s--;
        }else if(znak == Element.GREATER_OR_EQUAL || znak == Element.LESS){
          setE.add(i, setE.get(i));
          bolresult.add(i, bolresult.get(i));
          s++;
        }
      }
  }
  if(znak == Element.GREATER || znak == Element.GREATER_OR_EQUAL){
        setE.add(Element.POSITIVE_INFINITY);
        bolresult.add(true);
        pows.add(false);
          if(s % 2 == 0){
            setE.add(0, Element.NEGATIVE_INFINITY);
            bolresult.add(0,true);
            pows.add(0,false);
          }
      }else if(znak == Element.LESS || znak == Element.LESS_OR_EQUAL){
         if(s % 2 != 0){
           setE.add(0, Element.NEGATIVE_INFINITY);
           bolresult.add(0,true);
           pows.add(0,false);
         }
      }//получили окончательный вариант корней(отсортированный и о знаками бесконечности)
return (znak == Element.NOT_EQUAL) ? (new SubsetR(setE,bolresult)).complement(ring) : new SubsetR(setE, bolresult);
    }

    private Element[] fromQtoNumberR(Element[] r, Ring ring){
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

    public Element NumbersToR64(Element num, Ring ring){
      if (num instanceof F){
        F f = (F)num;
        for(int i = 0; i < f.X.length; i++){
          f.X[i] = (f.X[i] instanceof F) ? NumbersToR64(f.X[i], ring) : f.X[i].toNumber(Ring.R64, ring);
        }
      }
      return num;
    }

    public static void main(String[] args) {
         Ring ring = new Ring("Q[x,y]");
         F p1 = new F("(x+1)^2", ring);
         //Polynom p2 = new Polynom("x^2-2x-8", ring);
         //Polynom p3 = new Polynom("0.5-x", ring);

         int znak1 = Element.GREATER;
         //int znak2 = Element.LESS;
         //int znak3 = Element.GREATER;

         //Polynom[] p = new Polynom[]{p1};
         int[] znak = new int[]{znak1};
         //FactorPol p = new FactorPol("(x-1/5)(x^2-2)(x-110)(x)", ring);
         //int znak = Element.GREATER_OR_EQUAL;
         System.out.println("result = " + new solveInequality().solveI(p1,znak1,ring).toString(ring));
//         Polynom p = new Polynom("1/3x+2/7", ring);
//         FactorPol p1=p.factorOfPol_inQ(true, ring);
//         System.out.println("result = " + p1.toString(ring));
    }
}
