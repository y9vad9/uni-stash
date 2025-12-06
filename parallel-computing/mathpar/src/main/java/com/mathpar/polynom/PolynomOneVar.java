
package com.mathpar.polynom;
import com.mathpar.number.Ring;
import java.util.*;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;

/**
 * September 2008
 * @author gennadi
 */

public class PolynomOneVar extends Polynom {


    //public static PolynomOneVar polynomOne = new PolynomOneVar(new int[0],new Element[]{NumberZ.ONE});
    public static PolynomOneVar polynomZero = new PolynomOneVar(new int[0], new Element[0]);

    public PolynomOneVar(int[] powers,Element[] coeffs){
        this.coeffs=coeffs;
        this.powers=powers;}

    public PolynomOneVar(){};
    public PolynomOneVar(Polynom p){coeffs=p.coeffs; powers=p.powers; }
    public PolynomOneVar(Polynom p, Ring r){coeffs=p.coeffs; powers=p.powers; }
    public PolynomOneVar(int[] randomType, Random rnd, Ring r) {
        Polynom Pol=random(randomType, rnd ,r);
        this.coeffs=Pol.coeffs;  this.powers=Pol.powers;     }

     /**
     * Метод вычисляющий промежуточный остаток при делении двух полиномов столбиком, при
     * этом старшие мономы в действиях не участвуют
     * @param divisor  исходный делитель
     * @param quot элемент частного
     * @param p -- надо думать, что p -- это остаток частного
     * @return промежуточный остаток при делении двух полиномов столбиком
     */
    // protected
     static Polynom interimRem(Polynom p, Polynom divisor, Polynom quot,Ring ring) {
        Polynom q = (Polynom) divisor.clone( );
        int lengRem = p.coeffs.length, lengDiv = q.coeffs.length;
        //нахожднние произведения делителя и элемента частного
        for (int i = 1; i < lengDiv; i++)
            q.coeffs[i] = q.coeffs[i].multiply(quot.coeffs[0], ring);
        if (quot.powers.length != 0)
            for (int i = 1; i < lengDiv; i++)q.powers[i] += quot.powers[0];
        Element[] newCoef = new Element[lengRem + lengDiv - 2];
        int[] newPow = new int[lengRem + lengDiv - 2];
        int k = 1, n = 1, t = 0;
        //нахождения промежуточного остатка(вычитание двух полиномов 1-ой степени без первых мономов)
        while (k < lengRem && n < lengDiv) {
            if (p.powers[k] == q.powers[n]) {
                Element temp = p.coeffs[k].subtract(q.coeffs[n], ring);
                if(!temp.isZero(ring)){newCoef[t]=temp; newPow[t]=q.powers[n];t++;}
                k++; n++;}
            else{if (p.powers[k] > q.powers[n]) {
                       newCoef[t] = p.coeffs[k]; newPow[t] = p.powers[k];k++;t++;}
                  else{newCoef[t] = q.coeffs[n].negate(ring);newPow[t] = q.powers[n]; n++;t++;}}
        }
        if (k < lengRem) {
            for (int i = k; i < lengRem; i++) {
                newCoef[t] = p.coeffs[i];newPow[t] = p.powers[i];t++; }}
        else {
            for (int i = n; i < lengDiv; i++) {
                newCoef[t] = q.coeffs[i].negate(ring); newPow[t] = q.powers[i]; t++;}
        }
        //вывод ответа
        if (t==1 && newPow[0]==0)
            return new Polynom(new int[0], new Element[]{newCoef[0]});
        if (t < lengRem + lengDiv - 2) {
            Element[] Coef = new Element[t]; int[] Pow = new int[t];
            System.arraycopy(newPow,0,Pow,0,t); System.arraycopy(newCoef,0,Coef,0,t);
            return new Polynom(Pow, Coef);      }
        return new Polynom(newPow, newCoef);
    }




    public static Element mod_x(Polynom p,Ring ring){
        if(p.isZero(ring)){return p;}
         int i=p.powers.length;
         if(i==0){return p.coeffs[i];}
         if(p.powers[i-1]!=0){return ring.numberONE;}
         return  p.coeffs[i-1];}

        public int degree(){
        return (powers.length==0)?0:powers[0];
    }
        /**
     * Процедура нахождения частного остатка и домножителя при делении
     *  двух полиномов this и q от 1-ой переменной
     *  @param q  делитель
     *  @return массив полиномов: на нулевом месте стоит частное деления this на q;
     *  на первом - остаток, на втором - домножитель
     */
    public PolynomOneVar[] divAndRem(PolynomOneVar q,Ring ring) {Element one=one(ring);
        PolynomOneVar p = this;
        if (q.powers.length == 0) {
            return new PolynomOneVar[] {
                (PolynomOneVar)p.multiplyByNumber(q.coeffs[0], ring), polynomZero , q};
        }
        if (powers.length == 0 || powers[0] < q.powers[0]) {
            return new PolynomOneVar[] {
                polynomZero, p, new PolynomOneVar(ring.polynomONE)};
        }
        Element firstmonQ = one;
        int pw = p.powers[0] - q.powers[0] + 1, t = 0;
        PolynomOneVar[] chast = new PolynomOneVar[pw];
        //проверка коэффициента при старшей степени z в полиноме q на равенство 1
        boolean oneB = true;
        if ( (q.coeffs[0]).equals(one)) {
            oneB = false;
        } else {
            firstmonQ = q.coeffs[0];
        }
        //деление полиномов
        while (p.powers.length != 0 && q.powers[0] <= p.powers[0]) {
            //выделение элемента частного
            PolynomOneVar chastnoe = new PolynomOneVar();
            chastnoe.coeffs = new Element[1];
            chastnoe.coeffs[0] = p.coeffs[0];
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {
                    p.powers[0] - q.powers[0]};
            }
            chast[t] = chastnoe;
            //домножение мн-на p на коэффициент  при старшей степени z
            if (oneB) {
                ///System.out.println("p="+p.toString(ring) +"  "+p.multiplyByNumber(firstmonQ, ring).powers.length+" "+p.multiplyByNumber(firstmonQ, ring).coeffs.length);
                p = new PolynomOneVar(p.multiplyByNumber(firstmonQ, ring));
            }
            p =  new PolynomOneVar(interimRem(p,q, chast[t], ring));
            t++;
        }
        if (oneB) {
            //домножение частного
            for (int i = 0; i < t; i++) {
                chast[i] = new PolynomOneVar (chast[i].multiplyByNumber(firstmonQ.pow(pw - i - 1, ring), ring));
            }
            //домножение остатка
            if (t != pw) {
                p = new PolynomOneVar (p.multiplyByNumber(firstmonQ.pow(pw - t, ring), ring));
            }
        }
        //Собирание результата частного
        if (t == 1) {
            return new PolynomOneVar[] {
                chast[0], p,new PolynomOneVar (polynomFromNumber(firstmonQ.pow(pw, ring), ring))};
        }
        PolynomOneVar result = new PolynomOneVar();
        result.coeffs = new Element[t];
        result.powers = new int[t];
        for (int i = 0; i < t - 1; i++) {
            result.coeffs[i] = chast[i].coeffs[0];
            result.powers[i] = chast[i].powers[0];
        }
        if (chast[t - 1].powers.length == 0) {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = 0;
        } else {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = chast[t - 1].powers[0];
        }
        return new PolynomOneVar[] {
            result, p,new PolynomOneVar (polynomFromNumber(firstmonQ.pow(pw, ring), ring))};
    }

    /**
     * Процедура нахождения остатка при делении двух полиномов от 1-ой переменной
     * @param q один из полиномов
     * @return остаток от деления и домножитель
     */
    public  static Polynom[] remainderZx(Polynom p, Polynom q, Ring ring) {
        //Polynom p=this;
        Element one1=p.one(ring);
        if (q.powers.length == 0) {
            return new Polynom[] {
                new Polynom(new int[0], new Element[0]), q};
        }
        if (p.powers.length == 0 || p.powers[0] < q.powers[0]) {
            return new Polynom[] {
                p, p.myOne(ring)};
        }
        Element firstmonQ = one1;
        int t = 0, pw = p.powers[0] - q.powers[0] + 1;
        Polynom chastnoe = new Polynom();
        chastnoe.coeffs = new Element[1];
        boolean one = true;
        //проверка коэффициента при старшей степени z в полиноме q на равенство 1
        if ( (q.coeffs[0]).equals(one)) {
            one = false;
        } else {
            firstmonQ = q.coeffs[0];
        }
        //деление полиномов
        while (p.powers.length != 0 && q.powers[0] <= p.powers[0]) {
            //выделение элемента частного
            chastnoe.coeffs[0] = p.coeffs[0];
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {
                    p.powers[0] - q.powers[0]};
            }
            //домножение мн-на p на коэффициент  при старшей степени z
            if (one) {
                p = p.multiplyByNumber(firstmonQ, ring);
            }
            p = interimRem(p, q, chastnoe, ring);
            t++;
        }
        //домножение остатка
        Polynom x= p;
        if (one && t != pw) {  
            x = p.multiplyByNumber(firstmonQ.pow(pw - t, ring), ring);
        }
       return new Polynom[] {x, polynomFromNumber(firstmonQ.pow(pw, ring), ring), p, polynomFromNumber(firstmonQ.pow(t, ring), ring)};
    }




    /**
     * Процедура нахождения частного и остатка  при делении двух
     *  полиномов this и q от 1-ой переменной
     *  @param q  делитель
     *  @return массив полиномов: на нулевом месте стоит частное деления this на q;
     *  на первом - остаток
     */
     protected Polynom[] divRemZx( Polynom q, Ring ring) { Element one=one(ring);
        if (isZero(ring)) return new Polynom[]{Polynom.polynom_zero(one),this};

        Element firstmonQ = one;
        Polynom p=this;
        int pw = p.powers[0] - q.powers[0] + 1, t = 0;
        if (pw<0) return  new Polynom[]{Polynom.polynom_zero(one),this};
        Polynom[] chast = new Polynom[pw];
        //проверка коэффициента при старшей степени z в полиноме q на равенство 1
        boolean oneB = true;
        if ( (q.coeffs[0]).equals(one)) {  oneB = false;
        } else {                           firstmonQ = q.coeffs[0];
        }
        //деление полиномов
        while (p.powers.length != 0 && q.powers[0] <= p.powers[0]) {
            //выделение элемента частного
            Polynom chastnoe = new Polynom();
            chastnoe.coeffs = new Element[1];
            chastnoe.coeffs[0] = p.coeffs[0];
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {
                    p.powers[0] - q.powers[0]};
            }
            chast[t] = chastnoe;
            //домножение мн-на p на коэффициент  при старшей степени z
            if (oneB) {
                p = p.multiplyByNumber(firstmonQ, ring);
            }
            p = interimRem(p, q, chast[t], ring);
            t++;
        }
        if (oneB) {
            //домножение частного
            for (int i = 0; i < t; i++) {
                chast[i] = chast[i].multiplyByNumber(firstmonQ.pow(pw - i - 1, ring), ring);
            }
            //домножение остатка
            if (t != pw) {
                p = p.multiplyByNumber(firstmonQ.pow(pw - t, ring), ring);
            }
        }
        //Собирание результата частного
        if (t == 1) {
            return new Polynom[] {
                chast[0], p};
        }
        Polynom result = new Polynom();
        result.coeffs = new Element[t];
        result.powers = new int[t];
        for (int i = 0; i < t - 1; i++) {
            result.coeffs[i] = chast[i].coeffs[0];
            result.powers[i] = chast[i].powers[0];
        }
        if (chast[t - 1].powers.length == 0) {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = 0;
        } else {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = chast[t - 1].powers[0];
        }
        return new Polynom[] {
            result, p};
    }



    /**
     * Процедура, возвращающая полиномы в представлении НОД двух полиномов
     * от 1-ой переменной через их самих
     * @param r  один из исходных полиномов
     * @return result[0]  НОД полиномов
     * @return result[1], result[2], result[3], result[4], result[5]  элементы
     * множителей a и b при f и g, с помощью которых можно получить НОД(f, g)по
     * следующей формуле НОД(f, g) = ((f/result[3])*result[1]+(g/result[4])*result[2])/result[5]
     */
    public Polynom[] ExtendedGCD(Polynom r,Ring ring) {Element one= one(ring);
        Element p2, q2, d1, C, C1, d, gcdBI;
        Polynom ofF;
        Polynom ofG;
        Polynom[] term1 = new Polynom[2];
        Polynom[] term2 = new Polynom[2];
        Polynom[] result = new Polynom[6];
        Polynom[] term = new Polynom[2];
        term1[1] = (Polynom)this.clone( );
        term2[1] = (Polynom) r.clone( );
        int v;
        //нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        //полиномов на соответствующий ему НОД коэффициентов
        p2 = term1[1].GCDNumPolCoeffs( ring);
        result[3] = polynomFromNumber(p2, ring);
        if (!p2.equals(one)) {
            term1[1] = term1[1].polDivNumb(p2, ring);
        }
        q2 = term2[1].GCDNumPolCoeffs( ring);
        result[4] = polynomFromNumber(q2, ring);
        if (!q2.equals(one)) {
            term2[1] = term2[1].polDivNumb(q2, ring);
        }
        gcdBI = p2.GCD(q2, ring);
        d = one;
        //Первое деление полиномов без упрощения остатка по методу Брауна, но
        //с нахождением следующего di
        if (term1[1].powers[0] >= term2[1].powers[0]) {
            term[0] = new Polynom(new int[0], new Element[0]);
            term[1] = myOne(ring);
            C1 = term2[1].coeffs[0];
            v = term1[1].powers[0] - term2[1].powers[0];
            term1 = (new PolynomOneVar(term1[1])).divRemZx( term2[1], ring);
            if (term1[1].coeffs.length != 0) {
                d = braunDiZx(d, v, C1, ring);
                result[1] = polynomFromNumber(C1.pow(v + 1, ring), ring);
                result[2] = (Polynom)term1[0].negate(ring);
            } else {
                result[1] = term[0];
                result[2] = term[1];
            }
        } else {
            term[1] = new Polynom(new int[0], new Element[0]);
            term[0] = myOne(ring);
            C1 = term1[1].coeffs[0];
            v = term2[1].powers[0] - term1[1].powers[0];
            term2 =(new PolynomOneVar(term2[1], ring)).divRemZx(term1[1], ring);
            if (term2[1].coeffs.length != 0) {
                d = braunDiZx(d, v, C1, ring);
                result[2] = polynomFromNumber(C1.pow(v + 1, ring), ring);
                result[1] = (Polynom)term2[0].negate(ring);
            } else {
                result[2] = term[0];
                result[1] = term[1];
            }
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (term1[1].powers.length > 0 && term2[1].powers.length > 0) {
            if (term1[1].powers[0] > term2[1].powers[0]) {
                C = term1[1].coeffs[0];
                C1 = term2[1].coeffs[0];
                v = term1[1].powers[0] - term2[1].powers[0];
                term1 = (new PolynomOneVar(term1[1])).divRemZx(term2[1], ring);
                if (term1[1].coeffs.length == 0) {
                    break;
                }
                //нахождение линейного множителя стоящего при f
                ofF = term[0].multiplyByNumber(C1.pow(v + 1, ring), ring);
                term[0] = result[1];
                result[1] = ofF.subtract(term1[0].mulSS(result[1], ring), ring);
                //нахождение линейного множителя стоящего при g
                ofG = term[1].multiplyByNumber(C1.pow(v + 1, ring), ring);
                term[1] = result[2];
                result[2] = ofG.subtract(term1[0].mulSS(result[2], ring), ring);
                //упрощения остатка и сомножителей
                term1[1] = braunUprZx(term1[1],d, v, C, ring);
                result[1] = braunUprZx(result[1], d, v, C, ring);
                result[2] = braunUprZx(result[2], d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
            } else {
                C = term2[1].coeffs[0];
                C1 = term1[1].coeffs[0];
                v = term2[1].powers[0] - term1[1].powers[0];
                term2 = (new PolynomOneVar(term2[1])).divRemZx(term1[1], ring);
                if (term2[1].coeffs.length == 0) {
                    break;
                }
                //нахождение линейного множителя стоящего при f
                ofF = term[0].multiplyByNumber(C1.pow(v + 1, ring), ring);
                term[0] = result[1];
                result[1] = ofF.subtract(term2[0].mulSS(result[1], ring), ring);
                //нахождение линейного множителя стоящего при g
                ofG = term[1].multiplyByNumber(C1.pow(v + 1, ring), ring);
                term[1] = result[2];
                result[2] = ofG.subtract(term2[0].mulSS(result[2], ring), ring);
                //упрощение остатка и сомножителей
                term2[1] = braunUprZx(term2[1], d, v, C, ring);
                result[1] = braunUprZx(result[1], d, v, C, ring);
                result[2] = braunUprZx(result[2], d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
            }
        }

        //Вывод НОД в случае, когда он лежит в том же кольце что и многочлены p и q
        if (term1[1].coeffs.length == 0) {
            q2 = term2[1].GCDNumPolCoeffs(ring);
            result[5] = polynomFromNumber(q2, ring);
            if (!q2.equals(one)) {
                term2[1] = term2[1].polDivNumb(q2, ring);
            }
            if (!gcdBI.equals(one)) {
                term2[1] = term2[1].multiplyByNumber(gcdBI, ring);
            }
            result[0] = term2[1];
        } else {
            if (term2[1].coeffs.length == 0) {
                p2 = term1[1].GCDNumPolCoeffs( ring);
                result[5] = polynomFromNumber(p2, ring);
                if (!p2.equals(one)) {
                    term1[1] = term1[1].polDivNumb(p2, ring);
                }
                if (!gcdBI.equals(one)) {
                    term1[1] = term1[1].multiplyByNumber(gcdBI, ring);
                }
                result[0] = term1[1];
            } else {
                //Вывод НОД в случае, когда он  лежит в кольце многочленов от меньшего
                // количества переменных, чем vars
                result[0] = polynomFromNumber(gcdBI, ring);
                if (term2[1].powers.length == 0) {
                    result[5] = term2[1];
                } else {
                    result[5] = term1[1];
                }
            }
        }
        result[1] = result[1].multiplyByNumber(gcdBI, ring);
        result[2] = result[2].multiplyByNumber(gcdBI, ring);
        return result;
    }





    /**
     * Процедура вычисляющая последующий промежуточный множитель в алгоритме Брауна,
     * имея в качестве входного параметра предыдущее значение множителя (this)
     * @param d предыдущее значение параметра di
     * @param vi  разность степеней делимого и делителя
     * @param Ci1  старший коэффициент в делителе
     * @return возвращает последующий промежуточный множитель в алгоритме Брауна
     */
    private Element braunDiZx(Element d, int vi, Element Ci1, Ring ring) {
        if (vi == 0)  return one(ring);
        Ci1 = Ci1.pow(vi, ring);
        if (vi == 1)  return Ci1; 
        return Ci1.divide(d.pow(vi - 1, ring), ring);      
    }




    /**
     * Процедура упрощения коэффициентов в остатке
     * @param di  промежуточный множитель
     * @param  Ci  старший коэффициент в делимом
     * @param vi разность старших степеней полиномов
     * @return this  полином, который упрощается
     */ 
   static private Polynom braunUprZx(Polynom p, Element di, int vi, Element Ci,Ring ring) {
        return p.polDivNumb( 
                ( (di.negate(ring)).pow(vi, ring)) 
                        .multiply(Ci.negate(ring), ring), ring);
    }
    /**
     * Процедура упрощения коэффициентов в остатке для метода Штурма отделения корней
     * @param di  промежуточный множитель
     * @param  Ci  старший коэффициент в делимом
     * @param vi разность старших степеней полиномов
     * @return this  упрощенный полином
     */
    private Polynom braunUprZxSturm(Polynom p, Element di, int vi, Element Ci,Ring ring) {
        return p.polDivNumb( ( (di.abs( ring)).pow(vi, ring)).multiply(Ci.abs( ring), ring), ring);
    }




    /**
     * Процедура нахождения частного и домножителя при делении двух
     *  полиномов this и q от 1-ой переменной
     *  @param q  делитель
     *  @return массив полиномов: на нулевом месте стоит частное деления this на q;
     *  на первом - домножитель
     */
    public Polynom[] divideExt(Polynom q,Ring ring) {
        Element one=one(ring);
        Polynom p = this;
        if (q.powers.length == 0) {
            return new Polynom[] {
                p.multiplyByNumber(q.coeffs[0], ring), q};
        }
        if (powers.length == 0 || powers[0] < q.powers[0]) {
            return new Polynom[] {
                new Polynom(new int[0], new Element[0]),myOne(ring)};
        }
        Element firstmonQ = one;
        int pw = p.powers[0] - q.powers[0] + 1, t = 0;
        Polynom[] chast = new Polynom[pw];
        //проверка коэффициента при старшей степени z в полиноме q на равенство 1
        boolean oneB = true;
        if ( (q.coeffs[0]).equals(one)) {
            oneB = false;
        } else {
            firstmonQ = q.coeffs[0];
        }
        //деление полиномов
        while (p.powers.length != 0 && q.powers[0] <= p.powers[0]) {
            //выделение элемента частного
            Polynom chastnoe = new Polynom();
            chastnoe.coeffs = new Element[1];
            chastnoe.coeffs[0] = p.coeffs[0];
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {
                    p.powers[0] - q.powers[0]};
            }
            chast[t] = chastnoe;
            //домножение мн-на p на коэффициент  при старшей степени z
            if (oneB) {
                p = p.multiplyByNumber(firstmonQ, ring);
            }
            p = interimRem(p, q, chast[t], ring);
            t++;
        }
        //домножение частного
        if (oneB) {
            for (int i = 0; i < t; i++) {
                chast[i] = chast[i].multiplyByNumber(firstmonQ.pow(pw - i - 1, ring), ring);
            }
        }
        //Собирание результата частного
        if (t == 1) {
            return new Polynom[] {
                chast[0], polynomFromNumber(firstmonQ.pow(pw, ring), ring)};
        }
        Polynom result = new Polynom();
        result.coeffs = new Element[t];
        result.powers = new int[t];
        for (int i = 0; i < t - 1; i++) {
            result.coeffs[i] = chast[i].coeffs[0];
            result.powers[i] = chast[i].powers[0];
        }
        if (chast[t - 1].powers.length == 0) {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = 0;
        } else {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = chast[t - 1].powers[0];
        }
        return new Polynom[] {
            result, polynomFromNumber(firstmonQ.pow(pw, ring), ring)};
    }




    /**
     * Процедура нахождения НОД полиномов this и q от 1-ой переменной
     * @param  q  один из полиномов, чей НОД ищется
     * @return НОД полиномов
     */
    public Polynom gcdZx(Polynom q,Ring ring) {
      Element one=ring.numberONE;
        Polynom polOne=Polynom.polynomFromNot0Number(one);
    //    PolynomOneVar examplar_pov=new PolynomOneVar();
        if(coeffs.length == 1 || q.coeffs.length == 1){
            return monomGCD(q, ring);
        }
        Polynom p = this;
        int v;
        Element p2, q2, C, C1, gcdBI;
        //нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        //полиномов на соответствующий ему НОД коэффициентов
        p2 = p.GCDNumPolCoeffs( ring);
        if (!p2.isOne(ring)) {p = p.polDivNumb(p2, ring);}
        q2 = q.GCDNumPolCoeffs( ring);
        if (!q2.isOne(ring)) {
            q = q.polDivNumb(q2, ring);
        }
        gcdBI = (p2).GCD((q2), ring);
      //  System.out.println("gcdBI="+gcdBI);
        Element d = one;
        //Первое деление полиномов без упрощения остатка по методу Брауна, но
        //с нахождением следующего di
        if (p.powers[0] >= q.powers[0]) {
            C1 = q.coeffs[0];
            v = p.powers[0] - q.powers[0];
            d = braunDiZx(d, v, C1, ring);
            p = //examplar_pov.
                    remZx(p,q, ring);
        } else {
            C1 = p.coeffs[0];
            v = q.powers[0] - p.powers[0];
            d = braunDiZx(d, v, C1, ring);
            q = //examplar_pov.
                    remZx(q,p, ring);
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.powers.length > 0 && q.powers.length > 0) {
         //   System.out.println("p="+p+"   q="+q);
             if (p.powers[0] > q.powers[0]) {
                C = p.coeffs[0];
                C1 = q.coeffs[0];
                v = p.powers[0] - q.powers[0];
                p = //examplar_pov.
                        remZx(p,q, ring);
                if (p.powers.length == 0)
                    break;
                p = braunUprZx(p,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
            } else {
                C = q.coeffs[0];
                C1 = p.coeffs[0];
                v = q.powers[0] - p.powers[0];
                q = //examplar_pov.
                        remZx(q,p, ring);
                if (q.powers.length == 0)
                    break;
                q = braunUprZx(q,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
            }
        }
        //Вывод ответа в случае, когда он лежит в том же кольце Z[x]
//        System.out.println("p="+p.coeffs[0].numbElementType()+"   q="+q);
////        if(!(p.isZero(ring) | q.isZero(ring))){
  //        if(p.coeffs[0].numbElementType()!=2 ||q.coeffs[0].numbElementType()!=2){
//            System.out.println("p="+p);
  // GI may 2011    p=p.Mod(new NumberZ64(ring.MOD32),ring);
 //      System.out.println("p2="+p);
  //GI may 2011      q=q.Mod(new NumberZ64(ring.MOD32),ring);
//        }}
     //   System.out.println("p="+p+"   q="+q);
        if (p.isZero( ring)) {
            q2 = q.GCDNumPolCoeffs( ring);
            if (!q2.equals(one)) {q = q.polDivNumb(q2, ring); }
            if (!gcdBI.equals(one)) {q = q.multiplyByNumber(gcdBI, ring); }
            return (q.coeffs[0].isNegative())? (Polynom)q.negate(ring) : q;
        }
        if (q.coeffs.length == 0) {
            p2 = p.GCDNumPolCoeffs(ring);
            if (!p2.equals(one)) {p = p.polDivNumb(p2, ring); }
            if (!gcdBI.isOne(ring)) {p = p.multiplyByNumber(gcdBI, ring);}
             return (p.coeffs[0].isNegative())? (Polynom)p.negate(ring) : p;

        }
        //Вывод ответа в случае, когда он не лежит в кольце Zp
        if (!gcdBI.equals(one)) { return polynomFromNumber(gcdBI, ring); }
        //Вывод ответа в случае, когда НОД(f, g) = 1
        return polOne;
    }




    /**
     * Процедура нахождения частного остатка и домножителя при делении
     *  двух полиномов this и q от 1-ой переменной над Z
     *  @param q  делитель
     *  @return массив полиномов: на нулевом месте стоит частное от деления this на q;
     *  на первом - остаток, на втором - домножитель
     */
    public Polynom[] divAndRem_Zx(Polynom q,Ring ring) {Element one=one(ring);
        Polynom p = this;
        if (q.powers.length == 0) {
            return new Polynom[] {
                p.multiplyByNumber(q.coeffs[0], ring), new Polynom(new int[0], new Element[0]) , q};
        }
        if (powers.length == 0 || powers[0] < q.powers[0]) {
            return new Polynom[] {
                new Polynom(new int[0], new Element[0]), p, myOne(ring)};
        }
        Element firstmonQ = one;
        int pw = p.powers[0] - q.powers[0] + 1, t = 0;
        Polynom[] chast = new Polynom[pw];
        //проверка коэффициента при старшей степени z в полиноме q на равенство 1
        boolean oneB = true;
        if ( (q.coeffs[0]).equals(one)) {
            oneB = false;
        } else {
            firstmonQ = q.coeffs[0];
        }
        //деление полиномов
        while (p.powers.length != 0 && q.powers[0] <= p.powers[0]) {
            //выделение элемента частного
            Polynom chastnoe = new Polynom();
            chastnoe.coeffs = new Element[1];
            chastnoe.coeffs[0] = p.coeffs[0];
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {
                    p.powers[0] - q.powers[0]};
            }
            chast[t] = chastnoe;
            //домножение мн-на p на коэффициент  при старшей степени z
            if (oneB) {
                p = p.multiplyByNumber(firstmonQ, ring);
            }
            p = interimRem(p,q, chast[t], ring);
            t++;
        }
        if (oneB) {
            //домножение частного
            for (int i = 0; i < t; i++) {
                chast[i] = chast[i].multiplyByNumber(firstmonQ.pow(pw - i - 1, ring), ring);
            }
            //домножение остатка
            if (t != pw) {
                p = p.multiplyByNumber(firstmonQ.pow(pw - t, ring), ring);
            }
        }
        //Собирание результата частного
        if (t == 1) {
            return new Polynom[] {
                chast[0], p, polynomFromNumber(firstmonQ.pow(pw, ring), ring)};
        }
        Polynom result = new Polynom();
        result.coeffs = new Element[t];
        result.powers = new int[t];
        for (int i = 0; i < t - 1; i++) {
            result.coeffs[i] = chast[i].coeffs[0];
            result.powers[i] = chast[i].powers[0];
        }
        if (chast[t - 1].powers.length == 0) {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = 0;
        } else {
            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
            result.powers[t - 1] = chast[t - 1].powers[0];
        }
        return new Polynom[] {
            result, p, polynomFromNumber(firstmonQ.pow(pw, ring), ring)};
    }




    /**
     * Процедура нахождения остатка при делении двух полиномов от 1-ой переменной
     * @param q один из полиномов
     * @return остаток от деления
     */
   static protected Polynom remZx(Polynom p, Polynom q,Ring ring) {
        Element firstmonQ = ring.numberONE;
        int t = 0, pw = p.powers[0]-q.powers[0]+1;
        Polynom chastnoe = new Polynom();
        chastnoe.coeffs = new Element[1];
        boolean oneB = true;
        //проверка коэффициента при старшей степени z в полиноме q на равенство 1
        if ( (q.coeffs[0]).isOne(ring)) oneB = false;
        else firstmonQ = q.coeffs[0];
        //деление полиномов
        while ((p.powers.length != 0) && (q.powers[0] <= p.powers[0])) {
            //выделение элемента частного
            chastnoe.coeffs[0] = p.coeffs[0];
            if (p.powers[0] == q.powers[0]) {chastnoe.powers = new int[0];}
            else {chastnoe.powers = new int[] {p.powers[0]-q.powers[0]};}
            //домножение мн-на p на коэффициент  при старшей степени z
            if (oneB) p = p.multiplyByNumber(firstmonQ, ring);
            p = interimRem(p,q, chastnoe, ring);
            t++;
        }
        //домножение остатка
        if (oneB && (t!= pw))p = p.multiplyByNumber(firstmonQ.pow(pw-t, ring), ring);
        if(pw%2==1 && firstmonQ.isNegative()) return (Polynom) p.negate(ring);
        return p; 
    }






    /**
     * Процедура возвращающая последовательность остатков при нахождении НОД двух
     * полиномов this и q от 1-ой переменной по алгоритму Евклида
     * @param  q  один из исходных полиномов
     * @return массив полиномов - остатков при нахождении НОД двух
     * полиномов по алгоритму Евклида
     */
    public Polynom[] prs_Zx(Polynom q,Ring ring) {Element one=one(ring);
        Polynom p = this;
        int v, t = 1, dl = q.powers[0], alfa = 0, beta = 0;
        Element p1, q1, p2, q2, d, C, C1, gcdBI;
        Polynom[] result = new Polynom[dl];
        //нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        //полиномов на соответствующий ему НОД коэффициентов
        p1 = p.GCDNumPolCoeffs(ring);
        if (!p1.equals(one)) {p = p.polDivNumb(p1, ring);}
        q1 = q.GCDNumPolCoeffs(ring);
        if (!q1.equals(one)) {q = q.polDivNumb(q1, ring);}
        d = one;
//        gcdBI = p1.GCD(q1, ring);
//        p2 = p1.divide(gcdBI, ring);
//        q2 = q1.divide(gcdBI, ring);
        //Первое деление полиномов без упрощения остатка по методу Брауна, но
        //с нахождением следующего di
        if (p.powers[0] >= q.powers[0]) {
            C1 = q.coeffs[0];
            v = p.powers[0] - q.powers[0];
            beta = v + 2;
            d = braunDiZx(d, v, C1, ring);
            p = remZx(p,q, ring);
            result[0] = p;//.multiplyByNumber((q2.pow(beta, ring)).multiply(gcdBI, ring), ring);
        } else {
            C1 = p.coeffs[0];
            v = q.powers[0] - p.powers[0];
            alfa = v + 2;
            d = braunDiZx(d, v, C1, ring);
            // gennadi had changed rem -> remZx: 10/10/08
            q = remZx(q,p, ring);
            result[0] = q;//.multiplyByNumber((p2.pow(alfa, ring)).multiply(gcdBI, ring), ring);
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.powers.length > 0 && q.powers.length > 0) {
            if (p.powers[0] > q.powers[0]) {
                C = p.coeffs[0]; C1 = q.coeffs[0];
                v = p.powers[0] - q.powers[0]; //   beta += v + 1;
                p = remZx(p,q, ring);
                if (p.coeffs.length == 0) {break;}
                p = braunUprZx(p,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
                result[t] = p;//.multiplyByNumber(p2.pow(alfa, ring). multiply(q2.pow(beta, ring), ring).multiply(gcdBI, ring), ring);
            } else {
                C = q.coeffs[0]; C1 = p.coeffs[0];
                v = q.powers[0] - p.powers[0]; // alfa += v + 1;
                q = remZx(q,p, ring);
                if (q.coeffs.length == 0) {break;}
                q = braunUprZx(q,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
                result[t] = q; //.multiplyByNumber((p2.pow(alfa, ring)).multiply(q2.pow(beta, ring), ring).multiply(gcdBI, ring), ring);
            }t++;
        }
        //вывод ответа
        if (t < dl) {
            Polynom[] otv = new Polynom[t];
            for (int i = 0; i < t; i++) {
                otv[i] = result[i];
            }
            return otv;
        }
        return result;
    }

/**   To DO
 * 
     * Процедура возвращающая последовательность остатков при нахождении НОД двух
     * полиномов this и q от 1-ой переменной по алгоритму Евклида
     * @param  q  один из исходных полиномов
     * @param flagESU - flag of Euclid (0), Sturm (1), Unsigned (2)
     * @return массив полиномов - остатков при нахождении НОД двух
     * полиномов по алгоритму Евклида
     */
    public Polynom[] prs_Zx_General(Polynom q, int flagESU, Ring ring) {Element one=one(ring);
        Polynom p = this;
        int v, t = 1, dl = q.powers[0], Si=0, phi, psi, pSum=0;
        Element p1, q1, p2, q2, d, C, C1, gcdBI;
        Polynom[] result = new Polynom[dl];
        int[] PP=new int[dl];
        int[] Phi=new int[dl];
        int[] Psi=new int[dl];
        //нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        //полиномов на соответствующий ему НОД коэффициентов
        p1 = p.GCDNumPolCoeffs(ring);
        if (!p1.equals(one)) {p = p.polDivNumb(p1, ring);}
        q1 = q.GCDNumPolCoeffs(ring);
        if (!q1.equals(one)) {q = q.polDivNumb(q1, ring);}
        d = one;
//        gcdBI = p1.GCD(q1, ring);
//        p2 = p1.divide(gcdBI, ring);
//        q2 = q1.divide(gcdBI, ring);
        //Первое деление полиномов без упрощения остатка по методу Брауна, но
        //с нахождением следующего di
        if (p.powers[0] >= q.powers[0]) {
            C1 = q.coeffs[0];
            v = p.powers[0] - q.powers[0]; //  beta = v + 2;
            d = braunDiZx(d, v, C1, ring);
            p = remZx(p,q, ring);
            result[0] = p;//.multiplyByNumber((q2.pow(beta, ring)).multiply(gcdBI, ring), ring);
        } else {
            C1 = p.coeffs[0];
            v = q.powers[0] - p.powers[0]; //   alfa = v + 2;
            d = braunDiZx(d, v, C1, ring);
            // gennadi had changed rem -> remZx: 10/10/08
            q = remZx(q,p, ring);
            result[0] = q; //.multiplyByNumber((p2.pow(alfa, ring)).multiply(gcdBI, ring), ring);
        }
        int pp=0;  // NEW MY/// 13-08-2015
        Si=v%2; phi=(pp+1)/2;  Phi[0]=phi; Psi[0]=phi;
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.powers.length > 0 && q.powers.length > 0) {
            if (p.powers[0] > q.powers[0]) {
                C = p.coeffs[0]; C1 = q.coeffs[0];
                v = p.powers[0] - q.powers[0]; //beta += v + 
                p = remZx(p,q, ring);
                if (p.coeffs.length == 0) {break;}  // p = braunUprZx(p,d, v, C, ring);
                p= p.polDivNumb( ( (d.negate(ring)).pow(v, ring)).multiply(C.negate(ring), ring), ring);
                d = braunDiZx(d, v, C1, ring);
                result[t] = p;//.multiplyByNumber(p2.pow(alfa, ring).multiply(q2.pow(beta, ring), ring).multiply(gcdBI, ring), ring);
            } else {
                C = q.coeffs[0];C1 = p.coeffs[0];
                v = q.powers[0] - p.powers[0]; //  alfa += v + 1; 
                q = remZx(q,p, ring);
                if (q.coeffs.length == 0) {break;}   // q = braunUprZx(q,d, v, C, ring);
                q= q.polDivNumb( ( (d.negate(ring)).pow(v, ring)).multiply(C.negate(ring), ring), ring);
                d = braunDiZx(d, v, C1, ring);
                result[t] = q;//.multiplyByNumber((p2.pow(alfa, ring)).multiply(q2.pow(beta, ring), ring).multiply(gcdBI, ring), ring);
            }t++; Si+=v%2; phi=(pp+1)/2; 
             Phi[0]=phi; Psi[0]=phi;
        }
        //вывод ответа
        if (t < dl) {
            Polynom[] otv = new Polynom[t];
            for (int i = 0; i < t; i++) {
                otv[i] = result[i];
            }
            return otv;
        }
        return result;
    }






    /**
     * Процедура возвращающая последний остаток при нахождении НОД двух
     * полиномов this и q от 1-ой переменной по алгоритму Евклида
     * @param  q  один из исходных полиномов
     * @return полином? полученный при нахождении НОД двух
     * полиномов по алгоритму Евклида
     */
    public Polynom resultant_Zx(Polynom q,Ring ring) {Element one=one(ring);
        Polynom p = this;
        int v, alfa, beta;
        Element p2, q2, d, C, C1, gcdCoeff;
        Polynom result = null;
        //нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        //полиномов на соответствующий ему НОД коэффициентов
        p2 = p.GCDNumPolCoeffs(ring);
        if (!p2.equals(one)) {
            p = p.polDivNumb(p2, ring);
        }
        q2 = q.GCDNumPolCoeffs(ring);
        if (!q2.equals(one)) {
            q = q.polDivNumb(q2, ring);
        }
        d = one;
        gcdCoeff = p2.GCD(q2, ring);
        //Первое деление полиномов без упрощения остатка по методу Брауна, но
        //с нахождением следующего di
        if (p.powers[0] >= q.powers[0]) {
            alfa = 1;
            C1 = q.coeffs[0];
            v = p.powers[0] - q.powers[0];
            beta = v + 1;
            d = braunDiZx(d, v, C1, ring);
            p = remZx(p,q, ring);
            result = p;
        } else {
            beta = 1;
            C1 = p.coeffs[0];
            v = q.powers[0] - p.powers[0];
            alfa = v + 1;
            d = braunDiZx(d, v, C1, ring);
            //gennadi had change rem -> remZx: 10.10.08
            q = remZx(q,p, ring);
            result = q;
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.powers.length > 0 && q.powers.length > 0) {
            if (p.powers[0] > q.powers[0]) {
                C = p.coeffs[0];
                C1 = q.coeffs[0];
                v = p.powers[0] - q.powers[0];
                beta += v + 1;
                p = remZx(p,q, ring);
                if (p.coeffs.length == 0) {
                    break;
                }
                p = braunUprZx(p,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
                result = p;
            } else {
                C = q.coeffs[0];
                C1 = p.coeffs[0];
                v = q.powers[0] - p.powers[0];
                alfa += v + 1;
                q = remZx(q,p, ring);
                if (q.coeffs.length == 0) {
                    break;
                }
                q = braunUprZx(q,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
                result = q;
            }
        }
        //вывод ответа с домножением на собранный коэффициент
        if(p.coeffs.length == 0 || q.coeffs.length == 0){
            return new Polynom(new int[0], new Element[0]);
        }
        return result.multiplyByNumber(p2.pow(alfa, ring).multiply(q2.pow(beta, ring), ring), ring);
    }







    /**
     * Процедура возвращающая последовательность остатков при нахождении НОД двух
     * полиномов this и q от 1-ой переменной по алгоритму Евклида для нужд алгоритма Штурма
     * отделения корней многочлена
     * @param  q  один из исходных полиномов
     * @return массив полиномов - остатков при нахождении НОД двух
     * полиномов по алгоритму Евклида
     */
    public Polynom[] prs_Zx_Sturm(Polynom q,Ring ring) {Element one=one(ring);
        Polynom p = this;
        int v, t = 1, dl = q.powers[0], alfa = 0, beta = 0;
        Element p1, q1, p2, q2, d, C, C1, gcdBI;
        Polynom[] result = new Polynom[dl];
        //нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        //полиномов на соответствующий ему НОД коэффициентов
        p1 = p.GCDNumPolCoeffs(ring);
        if (!p1.equals(one)) {
            p = p.polDivNumb(p1, ring);
        }
        q1 = q.GCDNumPolCoeffs(ring);
        if (!q1.equals(one)) {
            q = q.polDivNumb(q1,ring);
        }
        d = one;
        gcdBI = p1.GCD(q1, ring);
        p2 = p1.divide(gcdBI, ring);
        q2 = q1.divide(gcdBI, ring);
        //Первое деление полиномов без упрощения остатка по методу Брауна, но
        //с нахождением следующего di
        if (p.powers[0] >= q.powers[0]) {
            C1 = q.coeffs[0];
            v = p.powers[0] - q.powers[0];
            beta = v + 2;
            d = braunDiZx(d, v, C1, ring);
            p = (Polynom)(remZx(p,q, ring)).negate(ring);
            result[0] = p.multiplyByNumber((q2.pow(beta, ring)).multiply(gcdBI, ring), ring);
        } else {
            C1 = p.coeffs[0];
            v = q.powers[0] - p.powers[0];
            alfa = v + 2;
            d = braunDiZx(d, v, C1, ring);
            // gennadi had changed rem -> remZx: 10.10.08
            q = (Polynom)(remZx(q,p, ring)).negate(ring);
            result[0] = q.multiplyByNumber((p2.pow(alfa, ring)).multiply(gcdBI, ring), ring);
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.powers.length > 0 && q.powers.length > 0) {
            if (p.powers[0] > q.powers[0]) {
                C = p.coeffs[0];
                C1 = q.coeffs[0];
                v = p.powers[0] - q.powers[0];
                beta += v + 1;
                p = (Polynom)(remZx(p,q, ring)).negate(ring);
                if (p.coeffs.length == 0) {
                    break;
                }
                p = braunUprZxSturm(p,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
                result[t] = p.multiplyByNumber((p2.pow(alfa, ring)).multiply(q2.pow(beta, ring), ring).multiply(gcdBI, ring), ring);
                t++;
            } else {
                C = q.coeffs[0];
                C1 = p.coeffs[0];
                v = q.powers[0] - p.powers[0];
                alfa += v + 1;
                q = (Polynom)(remZx(q,p, ring)).negate(ring);
                if (q.coeffs.length == 0) {
                    break;
                }
                q = braunUprZxSturm(q,d, v, C, ring);
                d = braunDiZx(d, v, C1, ring);
                result[t] = q.multiplyByNumber((p2.pow(alfa, ring)).multiply(q2.pow(beta, ring), ring).multiply(gcdBI, ring), ring);
                t++;
            }
        }
        //вывод ответа
        if (t < dl) {
            Polynom[] otv = new Polynom[t];
            for (int i = 0; i < t; i++) {
                otv[i] = result[i];
            }
            return otv;
        }
        return result;
    }

 /** Get the constant term of this polynomial
  *
  * @param ring -- ring
  * @return Constant term of this polynomial
  */
    @Override
 public  Element constantTerm(Ring ring){
         int i=powers.length;  if((i==0)||(powers[i-1]!=0))return ring.numberONE;
         return  coeffs[i-1];}

 /** Get the coefficient of term which power is equal power
  * @param power -- the power of term
  * @param ring -- Ring
  * @return    */
 Element coeffOfPow(int power,Ring ring){
         int i=powers.length;  if(i==0) return ring.numberZERO;
         for(int k=0;k<i;k++) if(powers[k]>=power)return (powers[k]==power)? coeffs[powers[k]]: ring.numberZERO();
         return ring.numberZERO();
 }

 public static Element getApproxPolynom(VectorS xx, VectorS yy, int powerApp, Ring ring){
    return getApproxPolynom(xx.V, yy.V, powerApp, ring);
 }

 public static Element getApproxPolynom(Element[] xx, Element[] yy, int powerApp, Ring ring){
       if (powerApp<1){ring.exception.append("The degree of approximating polynomial cannot be less then 1!"); return null;}
       int pointsN=xx.length; Element[] XX=null;  Element D=null;
       int rType = yy[0].numbElementType();
       if (rType != Ring.Z) {
          int mas =  ring.FLOATPOS ;
          double d = Math.pow(10.0, mas); D=yy[0].valOf(d, ring);
          XX = new Element[pointsN];
          if (rType == Ring.R64) {//  ..... R64  ТИП
              for (int j = 0; j < pointsN; j++) {
                    double dd = (((NumberR64) xx[j]).doubleValue()) * d;
                    XX[j] = NumberZ.ONE.valOf(Math.rint(dd), ring);   }}
          else if (rType == Ring.R) {//  ..... R64  ТИП
              for (int j = 0; j < pointsN; j++) {
                    double dd = (xx[j].isZero(ring))? 0.0: (((NumberR) xx[j]).doubleValue()) * d;
                    XX[j] = NumberZ.ONE.valOf(Math.rint(dd), ring);    }}
           else {ring.exception.append("TablePlot with unknown type of numbers"); return null;}
       }
       Ring ringZ= (rType!=Ring.Z)?  Ring.ringZxyz:ring;
       VectorS V=  (rType!=Ring.Z)? (new VectorS(XX)): (new VectorS(xx));
       Element gcd=V.gcdOfElements(ringZ);
       if (!gcd.isOne(ring)) V=V.divide(gcd, ring);
       XX=V.V;
       Element[][] mm=new Element[pointsN][powerApp+1];
       for (int j = 0; j < pointsN; j++) {
         mm[j][powerApp]=NumberZ.ONE; mm[j][powerApp-1]=XX[j];
         for(int i=powerApp-2;i>=0;i--) mm[j][i]=mm[j][i+1].multiply(XX[j], ring);
       }
       MatrixS A=new MatrixS(mm,ringZ);
       MatrixS BB,B=A.GenInvers(ringZ);    // Это центральное место всего алгоритма
       if(rType!=Ring.Z) BB=(MatrixS)B.toNewRing(rType, ring); else BB=B;       
       Element[] Coeffs=  (BB.M.length>0)? BB.multiply(yy, powerApp+1 , ring): null;
       if (Coeffs==null){ring.exception.append("DO LEES FLOATPOS or use SPACE=R[x] with new MachinEpsilonR."); return Polynom.polynomZero;}
       int[] pows=new int[powerApp+1];
       Element mul=D.divide(gcd, ring); Element t=mul.valOf(1, ring);
       for (int i = powerApp; i >=0; i--)
          {pows[i]=powerApp-i; Coeffs[i]=Coeffs[i].multiply(t, ring); t=t.multiply(mul, ring); }
       return ((new Polynom(pows,Coeffs)).deleteZeroCoeff(ring));
 }


    public static void main(String[] args) {
        Ring R=new Ring("Z[x,y,z]");

        Polynom p1=new Polynom("z^13y^7+z^7y^10+2z^7y^7+2z^6+2y^3+4",R);
        Polynom p2=new Polynom("13z^12y^7+7z^6y^10+14z^6y^7+12z^5",R);
        Polynom s=new Polynom(" 6y^6+5y^5+4y^4+3y^3+2y^2+y",R);
   // Polynom   c= p1.braunUpr(p1, 1, p2, R);
        Polynom ds=(Polynom)p1.GCD(p2, R);
        System.out.println("ds="+ds);

        p1=new Polynom("2x^4+5x^3+5x^2-2x+1",R);
        PolynomOneVar P=new PolynomOneVar(p1);
        p2=new Polynom("3x^3+3x^2+3x-4",R);
        Polynom[] k=P.prs_Zx_Sturm(p2,R); System.out.println("Shturm");
        for (int i = 0; i < k.length; i++) {
            System.out.println("k["+i+"]="+k[i]);
        }
        k=P.prs_Zx(p2,R);
        for (int i = 0; i < k.length; i++) {
            System.out.println("k["+i+"]="+k[i]);
        }
//        Polynom mmm= 0.mulSS(s, R);
//            System.out.println("mm="+ mmm);
//        System.out.println("ds="+ s.powRecS(2, R));
//          System.out.println("mm="+ mmm);
     //Polynom ddds=23117006778588463104y^184+462340135571769262080y^181+4161061220145923358720y^178+22192326507444924579840y^175+77673142776057236029440y^172+184936054228707704832000y^169+295897686765932327731200y^166+295897686765932327731200y^163+147948843382966163865600y^160+1479488433829661638656y^99+14794884338296616386560y^96+59179537353186465546240y^93+118359074706372931092480y^90+118359074706372931092480y^87+23671814941274586218496y^14;
}




}
