package com.mathpar.students.OLD.stud2014.lukashin;
import com.mathpar.func.F;
import java.util.Arrays;
import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 * --класс TryGetSumProgress(запускать метод TryGetSumProgress) ищет в полиноме прогрессии и
 * преобразует их по формуле суммы(S=b1*(q^n-1)/q-1),
 *      где     S-сумма первых n членов
 *              b1-первый член геом. прогрессии
 *              q-знаменатель прогрессии
 * --выбирает из полинома все имеющиеся там прогрессии
 * --на выходе сумма из преобразованных по формуле геом. прогрессий и
 * оставшегося полинома
 *  *******имеется 2 одинаковых метода(TryGetProgress), НО:
 *  *******старый выбирает первую попавшуюся прогрессию(норм. работает)
 *  *******новый выбирает наибольшую прогрессию(проблема с выводом)
 * Пример:
 *      Polynom a = new Polynom ("x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+x^11+x^12+x^13",r1);
 *      F b = TryGetSumProgress(a, r1);
 *      System.out.println("b="+b);
 * Выводит:
 *      b=(0+(0+(x^14-x^3)/(x-1)))
 * @author Лукашин Владислав
 */
public class TryGetSumProgress {

    public TryGetSumProgress() {
    }


    public static void main(String arg[]){
         Ring r1 = new Ring("Z[x,y,z]");
        TryGetSumProgress gg=new TryGetSumProgress();
         Polynom a100 = new Polynom ("x+x^5+x^9+x^13+xyz+7x^2y^2z^2+7x^3y^3z^3+100xy+x+x^2+x^3+x^4",r1);
        Polynom a110 = new Polynom ("x+y+z",r1);
        Polynom a120 = new Polynom ("x+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+x^11+x^12+x^13",r1);
        F bbb = gg.TryGetSumProgress(a100, r1);
        F bbb1 = gg.TryGetSumProgress(a110, r1);
        F bbb2 = gg.TryGetSumProgress(a120, r1);
        System.out.println("bbb="+bbb.toString(r1));
        System.out.println("bbb1="+bbb1.toString(r1));
        System.out.println("bbb2="+bbb2.toString(r1));
    }
/**
 * главный метод
 * @param f1 - полином, который проверяется на присутствие в нем геом. прогрессии
 * @param r -  кольцо
 * @return элемент класса F, который записан в виде сумм геом. прогрессий и оставшегося полинома
 */
    public  F TryGetSumProgress(Polynom f1, Ring r) {
        Polynom f = (Polynom) f1.clone();
        F res = new F(Polynom.polynomZero);
        int over = f.coeffs.length/3;
        for (int i = 0;i< over;i++){
            if (f.coeffs.length==0)break;
            Polynom alpha=TryGetProgress(f,r);
            if (!(Polynom.polynomZero.equals(alpha,r))){
           //if ((new Polynom("0", r).compareTo(TryGetProgress, r))!=0){
            res= new F(F.ADD,new Element[]{res,PolynomToSumGeometricSequence(alpha,r)});
             // System.out.println("res.alp"+res);
          //  System.out.println("gam.alph="+PolynomToSumGeometricSequence(TryGetProgress,r));
            f=f.subtract(alpha, r);
            }
        }
        //System.out.println("f.alp="+f);
        res= new F(F.ADD,new Element[]{f,res});
        return res ;
    }
/***********новый метод******/
    /**
     * ищет геом. прогрессию с 3 и более членами, используя GetProgress
     * (выбирает наибольшую прогрессию)
     * @param f1 - полином
     * @param r - кольцо
     * @return найденую прогрессию
     */
   private  Polynom TryGetProgress(Polynom f1, Ring r) {//TryGetProgres
        Polynom f = (Polynom) f1.clone();
        Polynom res = Polynom.polynomZero;
        Polynom[] monoms = f.getmonoms(Polynom.polynomFromNumber(r.numberONE, r), r);
        int len = monoms.length;
        for (int i = len - 1; i >= 0; i--){
            for (int j = i - 1; j >= 0; j--){
                Polynom res1 = GetProgress(f, monoms[i], monoms[j], r);
                if (res1.coeffs.length>2){
                    if (res.coeffs.length<res1.coeffs.length){
                        res = (Polynom) res1.clone();
                    }
                }
            }
        }
        return res;
    }
 /*   ******старый метод*******/
    /**
     * ищет геом. прогрессию с 3 и более членами, используя GetProgress
     * (выбирает первую попавшуюся прогрессию)
     * @param f1 - полином
     * @param r - кольцо
     * @return найденую прогрессию
     */
  /*public static Polynom TryGetProgress(Polynom f1, Ring r) {
        Polynom f = (Polynom) f1.cloneWithoutCFormPage();
        Polynom[] monoms = f.getmonoms(Polynom.polynomZone, r);
        int len = monoms.length;
        for (int i = len - 1; i >= 0; i--){
            for (int j = i - 1; j >= 0; j--){
                Polynom res = GetProgress(f, monoms[i], monoms[j], r);
                if (res.coeffs.length>2){
                    return res;
                }
            }
        }
        return Polynom.polynomZero;
    }*/


    /**
     * ищет в полиноме f1 геометр. прогрессию по двум мономам a1 и b1
     * @param f1 - полином
     * @param a1 - первый моном
     * @param b1 - второй моном
     * @param r кольцо
     * @return найденую прогрессию(Polynom)
     */

    private  Polynom GetProgress(Polynom f1, Polynom a1, Polynom b1, Ring r) {
        Polynom f = (Polynom) f1.clone();
        Polynom a = (Polynom) a1.clone();
        Polynom b = (Polynom) b1.clone();
        //System.out.println("a="+a);
        //System.out.println("b="+b);
        int len = b.powers.length;
        int powers[] = new int[len];
        if (a.powers.length != len) {
            System.arraycopy(a.powers, 0, powers, 0, a.powers.length);
            a.powers = powers;
        }
        int[] sub = new int[len];
//        if (len!=a.powers.length){
//            System.out.println("ошибка!!! мономы имеют разную размерность");
//            return null;
//        }
        for (int i = 0; i < len; i++) {
            sub[i] = b.powers[i] - a.powers[i];
            //System.out.println("sub="+sub[i]);
        }
        Polynom[] mon = new Polynom[f.coeffs.length];
        mon = f.getmonoms(Polynom.polynomFromNumber(r.numberONE, r), r);
        Polynom res = Polynom.polynomZero;
        int[] prom = new int[len];
//========ищем в f геом. прогрессию со знаменателем sub======//
        h:for (int i = 0; i <= mon.length; i++) {
            for (int k = 0; k < sub.length; k++) {
                prom[k] = a.powers[k] + sub[k] * i;
                //System.out.println("prom="+prom[k]);
            }
            for (int j = mon.length-1; j >=0; j--) {
                if (Arrays.equals(mon[j].powers, prom)) {
                    res = res.add(mon[j], r);
                    break;
                }
                if (j==0)break h;
            }
        }
        if (res.coeffs.length!=0){
        Element min = res.coeffs[0];
        for (int i = 1; i < res.coeffs.length; i++) {
            if (res.coeffs[i].compareTo(min, r) < 0) {
                min = res.coeffs[i];
            }
        }
        for (int i = 0; i < res.coeffs.length; i++) {
            res.coeffs[i] = min;
        }
        }
        return res;

    }

    /**Проверяет можно ли привести полином к сумме геометрической прогрессии
     * @param Polynom f
     * @param r - кольцо
     * @return F (либо сумму геом. прогрессии(если она есть),либо исходный полином)
     */
    private  F PolynomToSumGeometricSequence(Polynom f1, Ring r) {
       // if (f1.coeffs.length==0)return null;
        //System.out.println("f111=="+f1);
           Polynom f = (Polynom) f1.clone();
        //===============проверка коеф-тов==================//

            if (f.coeffs.length == 0) {
                return new F(f);

            }
        Element first = f.coeffs[0];//
        // System.out.println("first="+first.toString(r));
        //System.out.println("f len="+f.coeffs.length);
        for (int i = 1; i < f.coeffs.length; i++) {//
            //  System.out.println("f["+i+"]="+f.coeffs[i].toString(r));
            if (!first.equals(f.coeffs[i], r)) {//             проверка коеф-тов
                return new F(f);//new Polynom[]{f, new Polynom("1", r)};//
            }
        }
        int kol_per = f.powers.length / f.coeffs.length;//кол-во переменных
        //========================проверка степеней==========================//
        int[] d = new int[kol_per];                                             //проверка степеней
        for (int i = 0; i < kol_per; i++) {//
            d[i] = f.powers[i] - f.powers[i + kol_per];//
        }//
        for (int i = 2 * kol_per; i < f.powers.length - kol_per; i += kol_per) {//
            for (int j = 0; j < kol_per; j++) {//
                if (f.powers[i + j - kol_per] != f.powers[i + j] + d[j]) {//
                    return new F(f);// new Polynom[]{f, new Polynom("1", r)};//
                }
            }
        }
        //===========================преобразуем f в сумму геометр. прогресии========================================//
        int[] del1 = new int[kol_per];
        Element[] del2 = new Element[]{first};
        for (int i = f.powers.length - kol_per; i < f.powers.length; i++) {
            del1[i - f.powers.length + kol_per] = f.powers[i];
        }
        Polynom del = new Polynom(del1, del2);////////del--общий множитель
        for (int i = 0; i < f.powers.length; i += kol_per) {
            for (int j = 0; j < kol_per; j++) {
                f.powers[i + j] = f.powers[i + j] - del1[j];//выносим общий множитель
            }
        }
        int[] r1 = new int[2 * kol_per];
        int[] r2 = new int[2 * kol_per];
        Element[] r12 = new NumberZ[]{new NumberZ(1), new NumberZ(-1)};
        for (int i = 0; i < kol_per; i++) {
            r1[i] = d[i] * f.coeffs.length;
            r1[kol_per + i] = 0;
            r2[kol_per + i] = 0;
            r2[i] = d[i];
        }
        Polynom res1 = new Polynom(r1, r12);
        Polynom res2 = new Polynom(r2, r12);
        F res = new F(F.DIVIDE, new Element[]{res1.multiply(del, r), res2});//
        return res;
    }
}
