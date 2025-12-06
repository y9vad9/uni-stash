// © Разработчик Смирнов Роман Антонович (White_Raven), ТГУ'10.

package com.mathpar.func;
import com.mathpar.number.Ring;
import java.util.Vector;
import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 *
 * @author White_Raven
 */
public class AnalisFunction extends F {

    public F func = null;//входная функция область определения которой нужно найти

    public Element[] not_a_Zero = new Element[]{};//элементы функции значение которых не должно быть равно нулю. (представлены в виде суммы)

    public Element[] equal_or_Greater_Zero = new Element[]{};//элементы функции значение которых должно быть больше либо равно нулю. (представлены в виде суммы)

    public Element[] greater_Zero = new Element[]{};//элементы функции значение которых должнобыть строго больше нуля. (представлены в виде суммы)


    public AnalisFunction() {
    }

    public AnalisFunction(F f) { // коструктор от элемента типа F ( от функции)
        this.name = f.name;
        this.X = f.X;
    }

    public AnalisFunction(Element f) { // коструктор от любого элемента
        if (f instanceof F) {
            this.name = ((F) f).name;
            this.X = ((F) f).X;
        } else {
            this.name = ID;
            this.X = new Element[]{f};
        }
    }
//=========================================================================================================================
//=================================                               ==========================================================
//=================================  Область определения функции  =========================================================
//=================================                               ==========================================================
//==========================================================================================================================

    /**
     * Процедура возвращающая область определенмя функции в виде F :
     * name - DomainOfFunc;
     * X[0] - входная функция область определения которой нужно найти.
     * X[1] - элементы функции значение которых не должно быть равно нулю. (представлены в виде суммы)
     * X[2] - элементы функции значение которых должно быть больше либо равно нулю. (представлены в виде суммы)
     * X[3] - элементы функции значение которых должнобыть строго больше нуля. (представлены в виде суммы)
     * @return Область определения функции
     */
    public F DomainOfFunction(Ring ring) {
        func = this;
        Vector<Element> Not_a_Zero = new Vector<Element>();
        Vector<Element> Equal_or_Greater_Zero = new Vector<Element>();
        Vector<Element> Greater_Zero = new Vector<Element>();
        FillDomain(this,Not_a_Zero, Equal_or_Greater_Zero, Greater_Zero,ring); // заполняем область определения фунции
        ChekingDomainOfFunction(Not_a_Zero, Equal_or_Greater_Zero, Greater_Zero, ring); // удаляем повторяющиеся элементы в векторах
        not_a_Zero = new Element[Not_a_Zero.size()];
        equal_or_Greater_Zero = new Element[Equal_or_Greater_Zero.size()];
        greater_Zero = new Element[Greater_Zero.size()];
        Equal_or_Greater_Zero.copyInto(equal_or_Greater_Zero);
        Not_a_Zero.copyInto(not_a_Zero);
        Greater_Zero.copyInto(greater_Zero);
        return new F(DOMAIN_OF_FUNC, new Element[]{this, new F(ADD, not_a_Zero), new F(ADD, equal_or_Greater_Zero), new F(ADD, greater_Zero)});
    }

    private void FillDomain(F f, Vector<Element> Not_a_Zero, Vector<Element> Equal_or_Greater_Zero, Vector<Element> Greater_Zero, Ring ring) {
        for (Element el : f.X) {
            if (el instanceof F) {
                FillDomain((F) el, Not_a_Zero, Equal_or_Greater_Zero, Greater_Zero,ring);
            } else {
                if (el instanceof Fraction) {
                    Not_a_Zero.add(((Fraction) el).denom);
                }
                if (el instanceof Fraction) {
                    Not_a_Zero.add(((Fraction) el).denom);
                }
            }
        }
        switch(f.name){
           case DIVIDE:  Not_a_Zero.add(f.X[1]); break;
           case TG: Not_a_Zero.add(new F(COS,f.X[0])); break;
           case CTG: Not_a_Zero.add(new F(SIN,f.X[0])); break;
           case SQRT: Equal_or_Greater_Zero.add(f.X[0]); break;
           case LN: Greater_Zero.add(f.X[0]); break;
           case LG: Greater_Zero.add(f.X[0]); break;
           case LOG: Greater_Zero.add(f.X[1]);
                     Not_a_Zero.add(f.X[0].add(f.X[0].minus_one(ring),ring)); break;
        }
        return;
     }

    void RemoveRepeating(Vector<Element> Nz,Vector<Element> GeZ,Vector<Element>Gz, Ring ring){
    RemoveRepeatingElements(GeZ, ring);
    RemoveRepeatingElements(Nz, ring);
    RemoveRepeatingElements(Gz, ring);
    }

    void RemoveRepeatingElements(Vector<Element> vec, Ring ring){
    for(int i=0 ; i < vec.size()-1 ; i++){
    for(int j=i+1 ; j < vec.size() ; j++){
     if (vec.elementAt(i).compareTo(vec.elementAt(j),ring)==0) vec.remove(j);
       }
      }
    }

    void ChekingDomainOfFunction(Vector<Element> Nz , Vector<Element> GeZ , Vector<Element> Gz, Ring ring){
        for (int i = 0; i < Nz.size(); i++) {
            for (int j = i + 1; j < Gz.size(); j++) {
                if (Nz.elementAt(i).compareTo(Gz.elementAt(j), ring) == 0) {
                    Nz.remove(i);
                }
            }
        }
         for (int i = 0; i < GeZ.size(); i++) {
            for (int j = i + 1; j < Gz.size(); j++) {
                if (GeZ.elementAt(i).compareTo(Gz.elementAt(j),ring) == 0) {
                    GeZ.remove(i);
                }
            }
        }
         for (int i = 0; i < Nz.size(); i++) {
            for (int j = i + 1; j < GeZ.size(); j++) {
                if (Nz.elementAt(i).compareTo(GeZ.elementAt(j),ring) == 0) {
                     Gz.add(Nz.elementAt(i));
                     Nz.remove(i);
                     GeZ.remove(j);
                     i--;
                }
            }
        }
        RemoveRepeating(Nz, GeZ, Gz,ring);
    }



    private  boolean domainFuncisR(){
    return  ((F)X[1]).X.length==0 && ((F)X[2]).X.length==0 && ((F)X[3]).X.length==0;
    }
    /**
     * Проверяет является ли областью определения все числовое множество
     * @return true | false
     */
    public boolean domainFuncIsR(Ring ring){
    F DF=new AnalisFunction(this).DomainOfFunction(ring);
    return new AnalisFunction(DF).domainFuncisR();
    }
    /**
     * Процедура проверяющая равенство всех элементов Xarray  в точке point условию \u2260 0
     * @param Xarray - массив элементов , значение в точке которых надо проверить
     * @param point - точка (точки если полином от нескольких переменных) которая проверяется
     * @return true | false
     */

    private boolean NotZero(Element [] Xarray ,Element [] point, Ring ring){
    if(Xarray.length==0) return true;
    for(Element el : Xarray){
     if(el instanceof F){ if(((F)el).valueOf(point,ring).isZero(ring)) return false;
     }
    if(el instanceof Polynom){ if (((Polynom)el).value(point, ring  ).isZero(ring)) return false;
    }
     if (el.isZero(ring)) return false;
    }
    return true;
    }

    /**
     * Процедура проверяющая равенство всех элементов Xarray  в точке point условию >0
     * @param Xarray - массив элементов , значение в точке которых надо проверить
     * @param point - точка (точки если полином от нескольких переменных) которая проверяется
     * @return true | false
     */
    private boolean GreaterZero(Element [] Xarray ,Element [] point, Ring ring){
    if(Xarray.length==0) return true;
      return GreaterOrEqual(Xarray, point,ring) && NotZero(Xarray, point,ring);
     }

    /**
     * Процедура проверяющая равенство всех элементов Xarray  в точке point условию >=0
     * @param Xarray - массив элементов , значение в точке которых надо проверить
     * @param point - точка (точки если полином от нескольких переменных) которая проверяется
     * @return true | false
     */
     private boolean GreaterOrEqual(Element[] Xarray, Element[] point, Ring ring) {
        if (Xarray.length == 0) return true;
        for (Element el : Xarray) {
            if (el instanceof F) { if (((F) el).valueOf(point,ring).isNegative()) return false;
            }
            if (el instanceof Polynom) { if (((Polynom) el).value(point,ring).isNegative()) return false;
            }
            if (el.isNegative())  return false;
        }
        return true;
    }

/**
 * Процедура проверяющая лежит ли точка в области определения функции
 * @param DF - область определения функции
 * @param point - точка (точки если полином от нескольких переменных) которая проверяется
 * @return true | false;
 */
    public boolean PointInDomainFunction(F DF ,Element [] point, Ring ring){
    return NotZero(((F)DF.X[1]).X, point,ring) && GreaterOrEqual(((F)DF.X[2]).X, point,ring) && GreaterZero(((F)DF.X[1]).X,point,ring);
   }

 /**
 * Процедура проверяющая лежит ли точка в области определения функции
 * @param point - точка (точки если полином от нескольких переменных) которая проверяется
 * @return true | false
 */
    public boolean PointInDomainFunction(Element [] point, Ring ring){
    F DF =new AnalisFunction(this).DomainOfFunction(ring);
    return NotZero(((F)DF.X[1]).X, point,ring) && GreaterOrEqual(((F)DF.X[2]).X, point,ring) && GreaterZero(((F)DF.X[1]).X,point,ring);
     }

//===============================================================================================================================
//===============================================================================================================================
//===============================================================================================================================
   /**
    * Процедура проверяющая убвает или возрастает фунция в точке
    * @param point - точка (точки если полином от нескольких переменных) которая проверяется
    * @return  1 если ф-ия возрастает, -1 - убвает ,  0 не определено
    */
    public int RiseorDownFunctionInPoint(Element [] point, Ring ring){
     if( new AnalisFunction(this).PointInDomainFunction(point,ring)){
     F DiffFunc=(F)this.D(ring);
     if( new AnalisFunction(DiffFunc).PointInDomainFunction(point,ring)){
     Element res=DiffFunc.valueOf(point,ring);
      return (res.isNegative()) ? -1 : (res.isZero(ring)) ? 0 : 1;
     }else return 0;
          }else return 0;
    }




    /**
     *
     * @param point - NumberLimit
     * @return
     */
    public Element ReseachFunctionRoD(Element [] point, int r_ol_l, Ring ring){
    if (this.RiseorDownFunctionInPoint(point,ring)==0){
      return (r_ol_l==1) ? NumberLimit.POSITIVE_INFINITY : NumberLimit.NEGATIVE_INFINITY;
        }else{
     Element res =this.valueOf(point,ring);
     return new NumberLimit(res, r_ol_l);
       }
    }

//    public SubsetR DomainOfFunction(Element func, Ring ring){
//         F f = new AnalisFunction(func).DomainOfFunction(ring);
//
//    }


    public static void main(String[] args) {
        Ring ring = new Ring("R64[x]");
        ring.setDefaulRing();
        F f = new AnalisFunction(new F("\\tg(x)", ring)).DomainOfFunction(ring);
        System.out.println("f = " + f.toString(ring));
    }
}
