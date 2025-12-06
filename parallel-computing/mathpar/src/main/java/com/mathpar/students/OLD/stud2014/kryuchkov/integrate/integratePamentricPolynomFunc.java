/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.kryuchkov.integrate;
import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.func.*;

/**
 *Данный класс интегрирует функции заданные параметрически x=x(t) y=y(t)
 * в которых каждое уранение задано полиномом второй степени
 * т.е. по длине дуги кривой
 * @author Alexey Kryuchkov
 */
public class integratePamentricPolynomFunc {


    /**
     * извлечение интеграла \int{ (ax^2+bx+c)^1/2 dx }
     * @param pol - полином стоящий под радикалом
     * @param ring - кольцо
     * @return результат интегрирования радикала
     */
   public static Element integral1(Element f,Ring ring){
       Polynom pol;
       if(f instanceof Polynom){pol=(Polynom)f;}
       else  if(f.isItNumber()){pol=Polynom.polynomFromNumber(f,ring);}
       else return null;


       Element[] X = addZeroCoeffs(pol,ring);
       Element a = X[0] ;
       Element b = X[1]; //коэффициенты полинома
       Element c = X[2];

       Element B = ring.posConst[8].multiply((a.sqrt(ring)).pow(3, ring), ring);

       Element K = (Element) ring.posConst[2].multiply(a.sqrt(ring), ring);

       Element D= (Element) b.pow(2, ring).subtract(ring.posConst[4].multiply(a, ring).multiply(c, ring), ring);
       Element M = ring.posConst[2].multiply(new Polynom("x",ring),ring).multiply(a, ring).add(b, ring);
       Element T = new F(F.SQRT,pol);

        F ff= new F(F.LN, (K.multiply(T, ring).add(M, ring)));


       Element res = (K.multiply(T, ring).multiply(M, ring)).divide(B, ring);
        res = res.subtract(((D.multiply(ff, ring)).divide(B, ring)),ring);

       return res;
   }
   /**
    * процедура извлекает интеграл от функции вида x(ax^2+bx+c)^1/2
    * @param pol - полином стоящий под радикалом
    * @param -кольцо
    * @return первообразная
    */
   public static Element integral2(Polynom pol,Ring ring){
       Element[] X = addZeroCoeffs(pol,ring);
       Element a = X[0] ;
       Element b = X[1]; //коэффициенты полинома
       Element c = X[2];
       Element mon = new Polynom("x",ring);

       Element D = ring.posConst[8].multiply(ring.posConst[6].multiply(a.sqrt(ring).pow(5, ring), ring), ring);
       Element L = ring.posConst[3].multiply(b.pow(3, ring).subtract(ring.posConst[4].multiply(a, ring).multiply(b, ring).multiply(c, ring), ring), ring);
       Element T = new F(F.SQRT,pol);
       Element K = (Element) ring.posConst[2].multiply(a.sqrt(ring), ring);
       Element M = ring.posConst[2].multiply(mon,ring).multiply(a, ring).add(b, ring);
       Element ABx= ring.posConst[2].multiply(a, ring).multiply(b, ring).multiply(mon, ring);
       Element A2X2C = ring.posConst[8].multiply(a.pow(2, ring), ring).multiply(new Polynom("x^2",ring), ring).add(ring.posConst[8].multiply(a, ring).multiply(c, ring), ring);
       Element B2= ring.numberMINUS_ONE.multiply(ring.posConst[3], ring).multiply(b.pow(2, ring), ring);


       F ff= new F(F.LN, (K.multiply(T, ring).add(M, ring)));

       Element res = (L.multiply(ff, ring)).divide(D, ring);
        res = res.add(K.multiply(T, ring).multiply(ABx.add(A2X2C, ring).subtract(B2, ring), ring), ring).divide(D, ring);

      return res;
   }
   /**
    *  процедура извлекает интеграл от функции вида (ax^2+bx+c)^1/2
    * @param pol-полином стоящий под радикалом
    * @param ring - кольцо
    * @return результат интегрирования функциии
    */
   public static Element integral3(Polynom pol,Ring ring){
        Element[] X = addZeroCoeffs(pol,ring);
       Element a = X[0] ;
       Element b = X[1]; //коэффициенты полинома
       Element c = X[2];
       Element mon = new Polynom("x^2",ring);

       Element D = ring.posConst[8].pow(2, ring).multiply(ring.posConst[6].multiply(a.sqrt(ring).pow(7, ring), ring), ring);
       Element L = ring.posConst[3].multiply(b.pow(3, ring).subtract(ring.posConst[4].multiply(a, ring).multiply(b, ring).multiply(c, ring), ring), ring);
       Element T = new F(F.SQRT,pol);
       Element K = (Element) ring.posConst[2].multiply(a.sqrt(ring), ring);
       Element M = ring.posConst[2].multiply(new Polynom("x",ring),ring).multiply(a, ring).add(b, ring);
       Element EZ = ring.posConst[3].multiply(ring.posConst[8], ring).multiply(a.pow(2, ring), ring).multiply(new Polynom("x",ring), ring);
       Element E = ring.posConst[2].multiply(a, ring).multiply(mon, ring).add(c, ring);
       Element FF= ring.posConst[4].multiply(a, ring).multiply(b, ring);
        FF = FF.multiply(ring.posConst[2].multiply(a, ring).multiply(mon, ring).subtract(ring.posConst[13].multiply(c, ring), ring), ring);
       Element Z = ring.posConst[10].multiply(a, ring).multiply(b.pow(2, ring), ring).multiply(new Polynom("x",ring), ring);
       Element N = ring.posConst[2].pow(4, ring).multiply(a.pow(2, ring).multiply(c.pow(2, ring), ring), ring);
        N= (N.subtract(ring.posConst[3].multiply(ring.posConst[3], ring).multiply(a, ring).multiply(b.pow(2, ring), ring).multiply(c, ring).add(ring.posConst[5].multiply(b.pow(4, ring), ring), ring), ring));
        N= ring.posConst[3].multiply(N, ring);

        F ff= new F(F.LN, (K.multiply(T, ring).add(M, ring)));

       Element res = K.multiply(T, ring).multiply(EZ.multiply(E, ring).subtract(Z, ring).subtract(FF, ring), ring);
       res = (res.subtract(N.multiply(ff, ring), ring)).divide(D, ring);
       return res;
   }
   /**
    * процедура делает из параметрически заданной функции  элемент длины дуги кривой
    * @param x-первая функция x=x(t);
    * @param y-вторая функция y=y(t);
    * @param ring
    * @return
    */
   public static Polynom difFunc(Polynom x,Polynom y, Ring ring){
       if(x.isItNumber()&&y.isItNumber()){
          return new Polynom("0",ring) ;
       }
       if(x.degree(0)==1||y.degree(0)==1){
         return (Polynom)x.D(ring).pow(2, ring).add(y.D(ring).pow(2, ring),ring);
       }

     Element[] X = addZeroCoeffs(x,ring);
     Element[] Y = addZeroCoeffs(y,ring);
     Element a1 = X[0];
     Element b1 = X[1];
     Element c1 = X[2];
     Element a2 = Y[0];
     Element b2 = Y[1];
     Element c2 = Y[2];

     Element A= ring.posConst[4].multiply(a1.pow(2, ring).add(a2.pow(2, ring), ring), ring);
     Element B= ring.posConst[4].multiply(a1.multiply(b1, ring).add(a2.multiply(b2, ring), ring), ring);
     Element C = b1.pow(2, ring).add(b2.pow(2, ring), ring);

     Polynom res =(Polynom) A.multiply(new Polynom("x^2",ring), ring).add(B.multiply(new Polynom("x",ring), ring), ring).add(C, ring);
    return res;
   }

   /**
    * данная процедура раскладывает один интеграл на сумму несольких интегралов
    * @param rho - плотность кривой
    * @param x - х=х(t)
    * @param y - y=y(t) - параметрическое задание функций
    * @param ring
    * @return результат интегрирования функции(получение массы кривой)
    */
   public static Element IntegralSum(Polynom rho,Polynom x,Polynom y, Ring ring){
        Element[] RHO = addZeroCoeffs(rho,ring);
       Element A = RHO[0];
       Element B = RHO[1];
       Element C = RHO[2];
       Element res;
       Polynom rad = (Polynom) difFunc(x,y,ring);
       if(rad.isItNumber()){
             res = integratePolynom(rho, rad, ring);
       }else{
       Element I1 =  integral3(rad, ring);
       Element I2 =  integral2(rad,ring);
       Element I3 = integral1(rad,ring);
       res = A.multiply(I3, ring).add(B.multiply(I2, ring), ring);
       res = res.add(C.multiply(I1, ring), ring);
       }
       return res;
   }
   /**
    * дописывает нули в массив коэффициентов полинома
    * @param pol-полином
    * @param ring-кольцо
    * @return дописанный массив коэффициентов
    */
   public static Element[] addZeroCoeffs(Polynom pol,Ring ring){
       Element [] res = new Element[]{ring.numberZERO, ring.numberZERO, ring.numberZERO};
       for(int i=0;i<pol.coeffs.length;i++){
         res[i]=pol.coeffs[i];
       }

       return res;
   }
/**
    * извлечение интеграла если элемент длины дуги кривой - число
    * @param pol
    * @param ring
    * @return
    */
   public static Element integratePolynom(Polynom pol,Element num,Ring ring){
       if(num.isZero(ring)){
           return ring.numberZERO;
       }
       num = num.sqrt(ring);
       Element res = pol.integrate(ring).multiply(num, ring);
       return res;
   }
  /**
    * Процедура предназначена для интегрирования сплайн-функций при постоянной плотности
    * т.е. функция плотности одинакова на всех участках сплайна
    ****************************************************************************
    * обязательное условие x.length = y.length
    *
    * @param x - сплайн-функция x=x(t)
    * @param y - сплайн-функция y=y(t)
    * @param rho - плотность кривой заданной параметрически
    * @param ring
    * @return интегрирование на участках и суммирование результатов
    */

   public static Element parametricIntegrateSquareSplines(Polynom[] x,Polynom[] y,Polynom rho,Ring ring){

       Element res = ring.numberZERO;

       for(int i=0;i<x.length;i++){
       res= res.add(IntegralSum(rho,x[i],y[i], ring), ring);
       }
       return res;
   }

   /**
    * Процедура предназначена для интегрирования сплайн-функций
    *  функция плотности может быть различна  на всех участках сплайна
    ****************************************************************************
    * обязательное условие x.length = y.length
    *
    * @param x - сплайн-функция x=x(t)
    * @param y - сплайн-функция y=y(t)
    * @param rho - плотность кривой заданной параметрически
    * @param ring
    * @return интегрирование на участках и суммирование результатов
    */

   public Element parametricIntegrateSquareSplines(Polynom[] x,Polynom[] y,Polynom[] rho,Ring ring){

       Element res = ring.numberZERO;

       for(int i=0;i<x.length;i++){
       res= res.add(IntegralSum(rho[i],x[i],y[i], ring), ring);
       }
       return res;
   }


   public static void main(String[] args){
        Ring ring = new Ring("R64[x,y]");
       Polynom rho = new Polynom("4x^2+x+1",ring);
      Polynom pol1 = new Polynom("4x",ring);
      Polynom pol2 = new Polynom("3",ring);
       Element p = difFunc(pol1,pol2,ring);
       System.out.println("dx="+p.toString(ring));
      Element e =  IntegralSum(rho,pol2,pol1,ring);
      System.out.println(""+e);

   }
}
