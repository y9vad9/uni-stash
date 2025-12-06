package com.mathpar.polynom;

import com.mathpar.func.CanonicForms;
import com.mathpar.func.F;
import com.mathpar.func.Fname;
import com.mathpar.func.Page;
import com.mathpar.func.parser.Parser;
import java.io.*;
import java.util.*;
import com.mathpar.number.*;
import com.mathpar.number.math.MathContext;
import com.mathpar.number.math.RoundingMode;
import com.mathpar.polynom.file.FPolynom;
import com.mathpar.polynom.file.util.FileUtils;

/**
 * Класс Polynom -- это класс полиномов многих переменных над коммутативным
 * кольцом скаляров: Polynom=Element[x,y,z,...]. Коэффициенты полиномов хранятся
 * в объектах типа Element.
 * <p>
 * Все операции делятся на несколько основных групп: сложение, умножение,
 * вычитание, деление (точное и с остатком), возведение в степень, нахождение
 * НОД, преобразование в полиномы над другими кольцами и др.
 *
 * <p>
 * Copyright: Copyright (c) ParCA Tambov, 2005,2008 </p>
 * <p>
 * Company: MSofT Tambov </p>
 */
public class Polynom extends Element implements Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 6046993957224292837L;
    /**
     * Максимальное число мономов для последовательного умножения. При
     * превышении -- умножение рекурсивное
     */
    public static final int DEFAULT_QUANT_SIZE = 20000;
    static int CHECK_MULKS = 4;
    //   public static final Polynom polynomZone = new Polynom(new int[0], new Element[]{NumberZ.ONE});
    public static final Polynom polynomZero = new Polynom(new int[0], new Element[0]);
    /**
     * Массив степеней полинома, в котором записаны все степени ненулевых
     * мономов полинома. Степени мономов записываются в порядке записи мономов,
     * т.е. в обратном лексикографическом порядке
     * ({@link #ordering(int, int, int, Ring) см. метод ordering}. Это означает,
     * что по адресу 0 записаны степени старшего монома. <br> Степени монома
     * записываются так: сначала младшие, затем старшие.
     * <p>
     * Например: <br>
     * Пусть дано кольцо: {@code Z[x,y,z], x < y < z}. <br> Пусть дан полином:
     * {@code f = 5zy^2x^3 - 7y^5x^2z^5 + 3y^3z^3x^5 - 8y^3x^4z + 9x^7y^3z^3.}
     * <br> После упорядочивания:
     *
     * @{code f = - 7x^2y^5z^5 + 9x^7y^3z^3 + 3x^5y^3z^3 - 8x^4y^3z + 5x^3y^2z}.
     * <p>
     * Тогда массив степеней равен:
     * <p>
     * <code> powers = {2,5,5, 7,3,3, 5,3,3, 4,3,1, 3,2,1}.</code>
     *
     * В формате полинома можно хранить и числа. Для этого нужно записать:<br>
     * {@code powers=new int[0];}
     */
    public int[] powers;
    /**
     * Массив коэффициентов полинома. coeffs[i] = i-й коэффициент полинома.<br>
     * Все коэффициенты <b>должны</b> быть ненулевыми. Например:<br> Пусть дано
     * кольцо: {@code Z[x,y,z], x < y < z.}<br> Пусть дан полином:
     * {@code f = 5zy^2x^3 - 7y^5x^2z^5 + 3y^3z^3x^5 - 8y^3x^4z + 9x^7y^3z^3.}
     * <br> После упорядочивания:
     * {@code f = - 7x^2y^5z^5 + 9x^7y^3z^3 + 3x^5y^3z^3 - 8x^4y^3z + 5x^3y^2z.}
     * <p>
     * Тогда массив коэффициентов равен:<br>
     * <code>coeffs = {-7, 9, 3, -8, 5}.</code>
     * <p>
     * Если полином равен ненулевому числу, то массив coeffs содержит 1 число.
     * Если полином равен 0, то длина массива coeffs равна 0.
     */
    public Element[] coeffs;

    public Polynom() {
    }

    /**
     * Нулевой полином.
     *
     * @param ring кольцо
     */
    public Polynom(Ring ring) {
        powers = new int[0];
        coeffs = Ring.emptyArrayOf(ring.numberONE());
    }

    /**
     * Нулевой полином.
     *
     * @param el единица в кольце коеффициентов
     */
    public static Polynom myZero(Element el) {
        int[] pow = new int[0];
        Element[] coef = Ring.emptyArrayOf(el);
        return new Polynom(pow, coef);
    }

    /**
     * Polynomial which has a constant term.
     *
     * @param number coefficient of constant term
     * @param ring Ring
     */
    public static Polynom polynomFromNumber(Element number, Ring ring) {
        if (number.isZero(ring)) {
            return new Polynom(new int[0], new Element[0]);
        }
        return new Polynom(new int[0], new Element[] {number});
    }

    /**
     * Polynomial which has a NOT ZERO constant term.
     *
     * @param number coefficient of constant term NOT ZERO !
     * @param ring ring
     */
    public static Polynom polynomFromNot0Number(Element number) {
        return new Polynom(new int[0], new Element[] {number});
    }

    /**
     * Main constructor of polynomial.
     *
     * @param pows powers
     * @param coe coeffs
     */
    public Polynom(int[] pow, Element[] coe) {
        powers = pow;
        coeffs = coe;
    }

    /**
     * Polynomial which has a NOT ZERO constant term.
     *
     * @param el coefficient of constant term NOT ZERO !
     */
    public Polynom(Element el) {
        powers = new int[0];
        coeffs = new Element[] {el};
    }

    public Polynom(F f) {
        coeffs = ((Polynom) f.X[0]).coeffs;
        powers = ((Polynom) f.X[0]).powers;
    }

    /**
     * Polynomial one with coefficients of the type Element s Полином является
     * единицей, при p.coeffs.length=1, coeffs[0]=1
     *
     * @param s with type Element
     *
     * @return One polynomial
     */
    public static Polynom polynom_one(Element one) {
        return new Polynom(new int[0], new Element[] {one});
    }

    public static Polynom polynom_zero(Element one) {
        return new Polynom(new int[0], Ring.emptyArrayOf(one));
    }

    /**
     * @param strPol
     * @param r
     */
    public Polynom(String strPol, Ring r) {
        Element f1 = Parser.getPol(strPol, r); Polynom Res=null;
        Element elem =r.CForm.InputForm((F) f1, null, null);
        if (elem instanceof Polynom) { Res=(Polynom)elem; coeffs = Res.coeffs;  powers = Res.powers; return;}
        powers = new int[0];
        if (elem.numbElementType() < Ring.Polynom) {coeffs = new Element[] {elem}; return;}
        coeffs = new Element[0];
        if (elem.isZero(r)) return;
        if(elem instanceof Fraction){Fraction fr=(Fraction)elem; 
          if (fr.num.numbElementType()<=Ring.Polynom) {Element  elem1=fr.num; 
            if (elem1 instanceof Polynom) {Res=((Polynom)elem1).divideByNumberToFraction(fr.denom, r);
                coeffs = Res.coeffs;  powers = Res.powers;  
            } else  coeffs = new Element[] {elem1}; 
            return; 
         }}
         r.exception. append("See in:  Polynom new Polynom(String, ring) "
                                + "\n  This is not a polynomial, so you get 0.");
    }

    /**
     * Нормализует полином: если старшие переменные кольца не используются, то
     * удаляются из полинома модифицирует массив степеней у полинома.
     *
     *  
     */
    public Polynom normalNumbVar(Ring ring) {
        int nC = coeffs.length;
        if (nC == 0) {
            return myZero(ring);
        }
        int nP = powers.length;
        int varN = nP / nC;
        int i = varN - 1;
        a:
        for (; i >= 0; i--) {
            for (int j = 0; j < nC; j++) {
                if (powers[i + j * varN] != 0) {
                    break a;
                }
            }
        }
        i++; // сейчас i -- это число переменных
        // если i=0, то это не полином, а число!
        int[] pow = new int[nC * i];
        for (int k = 0; k < i; k++) {
            int i1 = k;
            int i2 = k;
            for (int j = 0; j < nC; j++) {
                pow[i1] = powers[i2];
                i1 += i;
                i2 += varN;
            }
        }
        return new Polynom(pow, this.coeffs);
    }

    /**
     * Polynomial one
     *
     * @return Polynomial one
     */
    @Override
    public Polynom myOne(Ring ring) {
        return new Polynom(new int[0], new Element[] {one(ring)});
    }

    /**
     * Polynomial zero
     *
     * @return Polynomial zero
     */
    @Override
    public Polynom myZero(Ring ring) {
        return polynomZero;
    }

    /**
     * Polynomial one
     *
     * @return Polynomial one
     */
    @Override
    public Polynom myMinus_one(Ring ring) {
        return new Polynom(new int[0], new Element[] {minus_one(ring)});
    }

    @Override
    public Element one(Ring ring) {
        return Ring.oneOfType(ring.algebra[0]);
    }

    @Override
    public Element zero(Ring ring) {
        return one(ring).myZero(ring);
    }

    @Override
    public Element minus_one(Ring ring) {
        return one(ring).negate(ring);
    }

    /**
     * Конструктор создает объект Polynom из массива степеней и коэффициентов
     * без всяких проверок.
     *
     * @param powers массив степеней
     * @param coeffs массив коэффициентов
     */
    public Polynom(int[] powers, Element[] coeffs, Ring ring) {
        this.powers = powers;
        this.coeffs = coeffs;
    }

        /**
     * The position of the highest variable of this polynomial
     * in the ring.  
     * @return powers.length/coeffs.length  or 0
     */
    public int varNumb() {
        return (coeffs.length==0)? 0: powers.length/coeffs.length;
    }
    
    
    /**
     * Равен ли полином нулю?
     *
     * @return true for ZERO-polynom
     */
    @Override
    public Boolean isZero(Ring ring) {
        if (coeffs == null) {
            return true;
        }
        for (int i = 0; i < coeffs.length; i++) {
            if (!coeffs[i].isZero(ring)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Является ли полином числом, т.е. его степень равна нулю?
     *
     * @return true if polynom degree equals zero
     */
    @Override
    public boolean isItNumber() {
        for (int i = 0; i < powers.length; i++) if (powers[i]!=0) return false;         
        return true;
    }
    @Override
    public boolean isItNumber(Ring ring) {return  isItNumber();}
    /**
     * Создает полином типа R или Z из строки. Для этого сначала находит имена
     * всех переменных-однобуквенных слов, затем создает кольцо, в торором есть
     * все нужные переменные, затем обращается к конструктору полинома от строки
     * и нового кольца. Предполагая, что все переменные - однобуквенные Порядок
     * переменных предполагается алфавитным.
     *
     * @param s - Полином в виде строки с однобуквенными именами переменных
     *
     * @return - Полином типа R или Z (если нет десятичных точек - то Z, иначе
     * R)
     */
    public Polynom valovWithoutRing(String s) {
        StringBuilder ss = new StringBuilder(); // ="";
        if (s.indexOf('.') == -1) {
            ss.append("Z[");
        } else {
            ss.append("R[");
        }
        int i = (int) 'a';
        for (; i < (int) 'z'; i++) {
            if (s.indexOf((char) i) != -1) {
                ss.append((char) i);
            }
            break;
        }
        for (; i < (int) 'z'; i++) {
            if (s.indexOf((char) i) != -1) {
                ss.append(',').append((char) i);
            }
        }
        try {
            Ring ring1 = new Ring(ss.append(']').toString());
            return new Polynom(s, ring1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
    
    
    
    public int findLastMonomWithMainVar(){
        int varsCnt = powers.length/coeffs.length;
        int leftInd=0,rightInd=coeffs.length-1;
        while (leftInd<=rightInd){
            int midInd=(leftInd+rightInd)/2;
            if (powers[midInd*varsCnt+varsCnt-1]!=0 && (midInd*varsCnt+2*varsCnt-1>=powers.length || powers[midInd*varsCnt+2*varsCnt-1]==0)){
                return midInd;
            }
            if (powers[midInd*varsCnt+varsCnt-1]==0){
                rightInd=midInd-1;
            }
            else{
                leftInd=midInd+1;
            }
        }
        return leftInd;
    }
    
    public void reduceMainVarPowers(int reduceValue){
        int varsCnt = powers.length/coeffs.length;
        for (int i=0; i<coeffs.length; i++){
            powers[i*varsCnt+varsCnt-1]-=reduceValue;
        }
    }
      /**
     * Конструктор создает объект Polynom из массива степеней и коэффициентов
     * без всяких проверок.
     *
     * @param powers массив степеней
     * @param coeffs массив коэффициентов
     */
    public Polynom(Integer[] pows, Element[] coeffs) {
        this.powers = new int[pows.length];
        for (int i = 0; i < pows.length; i++) {
            powers[i] = pows[i].intValue();
        }
        this.coeffs = coeffs;
    }

        /**
     * Конструктор создает объект Polynom из плотного массива  коэффициентов
     * без всяких проверок, но уточняет, что это числа и переводит их в тип 
     * ring.algebra[0]
     * @param coeffs массив коэффициентов
     * @param ring Ring
     */
    public Polynom(Element[] coeffs, Ring ring) {
        int n=coeffs.length;
        this.powers = new int[n];
        this.coeffs = new Element[n];
        for (int i = 0; i < n; i++)  powers[i] = n-1-i;
        for (int i = 0; i < n; i++)  
          this.coeffs[i]=coeffs[i].toNumber(ring.algebra[0], ring);
        deleteZeros(ring); 
    } 
 
    public boolean checkMainVars(Polynom p){
        int varCntThis=powers.length/coeffs.length;
        int varCntOther=p.powers.length/p.coeffs.length;
        if (varCntThis!=varCntOther){
            return false;
        }
        if (powers[varCntThis-1]==0) return false;
        if (p.powers[varCntOther-1]==0) return false;
        return true;
    }
    
    /**
     * VectorS transform to Polynomial
     * The number of variables is equal  to the highest degree of elements plus one
     * @param vec
     * @param ring
     * @return 
     */
    public  static Polynom vectorToPolynom(VectorS vec, Ring ring) {
            Element[] coeffs = vec.V; Polynom res=new Polynom();
        int n=coeffs.length;
        int[] powersNew = new int[n];
        res.coeffs = new Element[n];
        int newDeg=0;
        for (int i = 0; i < n; i++)  {powersNew[i] = n-1-i; 
          int s=(coeffs[i] instanceof Polynom)? ((Polynom)coeffs[i]).varNumb() : 0;
          newDeg=Math.max(newDeg, s);
        }
        if (newDeg==0){res.coeffs = new Element[n];
          for (int i = 0; i < n; i++) res.coeffs[i]=coeffs[i].toNumber(ring.algebra[0], ring);
          res.powers= powersNew;
          res.deleteZeros(ring); 
        }else{
            if(n==1)return (Polynom)coeffs[0];
            if(ring.varNames.length<newDeg+1 ){ring.exception.append("Not enough variables in SPACE. Please, add new variables."); return Polynom.polynomZero;}
            Polynom temp=   ring.varPolynom[newDeg];
            int j=n-1;
            res= (coeffs[j] instanceof Polynom)? (Polynom)coeffs[j]: Polynom.polynomFromNumber(coeffs[j], ring);
            for (int i = 1; i < n; i++) { j--;
                Polynom mult=(coeffs[j] instanceof Polynom)? (Polynom)temp.multiply(coeffs[j], ring): temp.multiplyByNumber(coeffs[j], ring);
                res= res.add(mult, ring);
                temp=temp.multiply(ring.varPolynom[newDeg], ring); }
        }    
      return res;
    }           
            
    /*
     * Конструктор, который генерирует случайный полином maxpowers -
     * максимальные степени при переменных, т.е. степень при i-й переменной
     * меняется от 0 до maxpowers[i]. density - плотность полинома. Кол-во
     * мономов в полиноме плотность k% равно: M =
     * (maxpowers[0]+1)*...*(maxpowers[vars-1]+1)*k/100, где vars - кол-во
     * переменных полинома. nbits - кол-во бит в коэффициентах полинома rnd -
     * генератор случайных чисел. Для того, чтобы генерировать псевдослучайные
     * полиномы нужно создать объект Random с заданным числом seed и вызвать
     * этот конструктор.
     */
    public Polynom(int[] maxPowers, int density, int nbits, Random rnd,
            Element itsCoeffOne, Ring ring) {
        int powNumber = maxPowers.length;
        int[] randomType = new int[powNumber + 2];
        System.arraycopy(maxPowers, 0, randomType, 0, powNumber);
        randomType[powNumber] = density;
        randomType[++powNumber] = nbits;
        Polynom Pol = random(randomType, rnd, ring).deleteZeroCoeff(ring);
        coeffs = Pol.coeffs;
        powers = Pol.powers;
    }

    /**
     * Constructor of random polynomial. The
     *
     * @param randomType -- array of: [maxPowers_1_var,.., maxPowers-last_var,
     * density, nbits]
     * @param rnd -- Random class issue
     * @param itsCoeffOne -- one of the coefficient ring
     */
    public Polynom(int[] randomType, Random rnd, Ring r) {
        Polynom Pol = random(randomType, rnd, r).deleteZeroCoeff(r);
        coeffs = Pol.coeffs;
        powers = Pol.powers;
    }

    /**
     * Method for constructin of random polynomials.
     *
     * @param randomType -- array of: [maxPowers_1_var,.., maxPowers-last_var,
     * density, nbits] The density is an integer of range 0,1...100. (-15
     * denotes the value 15^(-1) )
     * @param rnd -- Random class issue
     * @param itsCoeffOne -- one of the coefficient ring
     */
    @Override
    public Polynom random(int[] randomType, Random rnd, Ring ring) {
        Element itsCoeffOne = Ring.oneOfType(ring.algebra[0]);
        int vars = randomType.length;
        int nbits = randomType[--vars];
        int density = randomType[--vars];
        int[] maxPowers = new int[vars];
        System.arraycopy(randomType, 0, maxPowers, 0, vars);
        int[] randomTnew = {nbits};
        if (vars == 0) {
            return (new Polynom(new int[0], new Element[] {itsCoeffOne.random(
                randomTnew, rnd, ring)}));
        }
        int maxmonoms = 1;
        for (int i = 0; i < vars; i++) {
            maxmonoms *= maxPowers[i] + 1;
        }
        int nlongs = (maxmonoms + 63) / 64;
        int monoms = (maxmonoms * density) / 100;
        if (monoms == 0) {
            return myZero(ring);
        }
        long[] longs = new long[nlongs];
        int nfree = maxmonoms;
        long hMask = 1L << 63;
        for (int i = 0; i < monoms; i++) {
            long mask = hMask;
            int l = 0;
            int nSteps = rnd.nextInt(nfree);
            do {
                while ((longs[l] & mask) != 0) {
                    mask >>>= 1;
                    if (mask == 0) {
                        l++;
                        mask = hMask;
                    }
                }
                if (nSteps == 0) {
                    break;
                } else {
                    nSteps--;
                    mask >>>= 1;
                    if (mask == 0) {
                        l++;
                        mask = hMask;
                    }
                }
            } while (true);
            longs[l] |= mask;
            nfree--;
        }
        powers = new int[monoms * vars];
        coeffs = new Element[monoms];
        int[] pows = maxPowers.clone();
        int powersAdr = 0;
        long mask = hMask;
        int l = 0;
        for (int i = 0; i < monoms; i++) {
            while ((longs[l] & mask) == 0) {
                mask >>>= 1;
                if (mask == 0) {
                    l++;
                    mask = hMask;
                }
                int k = 0;
                while (pows[k] == 0) {
                    pows[k] = maxPowers[k];
                    k++;
                }
                pows[k]--;
            }
            for (int j = 0; j < vars; j++) {
                powers[powersAdr++] = pows[j];
            }
            coeffs[i] = itsCoeffOne.random(randomType, rnd, ring);
            if (i < monoms - 1) {
                mask >>>= 1;
                if (mask == 0) {
                    l++;
                    mask = hMask;
                }
                int k = 0;
                while (pows[k] == 0) {
                    pows[k] = maxPowers[k];
                    k++;
                }
                pows[k]--;
            }
        }
        return (new Polynom(powers, coeffs)).deleteZeroCoeff(ring).truncate();
    }

    @Override
    public String toString() {
        if ((coeffs == null) || (coeffs.length == 0)) {return "0";}
        Element cf = coeffs[0];
        int type = cf.numbElementType();
        int var = powers.length / coeffs.length;
        return toString(new Ring(type, var));
    }

    /**
     * String form of polynomials with different types of coefficients.
     *
     * @param j case of 0 return toString(r); 1 return function-polynomial
     * without Ring 2 return function-polynomial with Ring; default -
     * toString(). Function-polynomial--is a polynomial which has
     * functions-coefficients
     * @param r Ring for coefficients
     *
     * @return
     */
    @Override
    public String toString(int j, Ring r) {
        switch (j) {
            case 0:
                return toString(r);
            case 1: {
                int l = powers.length;
                StringBuilder s = new StringBuilder(3 * l);
                for (int i : powers) {
                    String cc = coeffs[i].toString();
                    s.append("+(").append(cc).append(")X^").append(powers[i]);
                }
                return s.toString();
            }
            case 2: {
                int l = powers.length;
                int c = coeffs.length;
                int v = l / c;
                int v0 = r.varLastIndices[0] + 1;
                int v1 = r.varLastIndices[1];
                StringBuilder s = new StringBuilder(3 * l);
                String first = "(";
                int q = 0;
                int v2 = v0 + v;
                for (int i = 0; i < c; i++) {
                    s.append(first).append(coeffs[i].toString(r)).append(")");
                    first = "+(";
                    for (int k = v0; k < v2; k++) {
                        if (powers[q] != 0) {
                            s.append(r.varNames[k]).append("^").
                                    append(powers[q]);
                        }
                        q++;
                    }
                }
                return s.toString();
            }
        }
        return toString();
    }

    public static String toString(Polynom p, Ring r) {
        int l = p.powers.length;
        int c = p.coeffs.length;
        int v = l / c;
        int v0 = r.varLastIndices[0] + 1;
        int v1 = r.varLastIndices[1];
        StringBuilder s = new StringBuilder(3 * l);
        String first = "(";
        int q = 0;
        int v2 = v0 + v;
        for (int i = 0; i < c; i++) {
            s.append(first).append(p.coeffs[i].toString(r)).append(")");
            first = "+(";
            for (int k = v0; k < v2; k++) {
                if (p.powers[q] != 0) {
                    s.append(r.varNames[k]).append("^").append(p.powers[q]);
                }
                q++;
            }
        }
        return s.toString();
    }

    public String toStringF(Ring r) {
        if (r.algebraNumb > 1) {
            return toString(r, r.varLastIndices[0] + 1, true);
        } else {
            return toString(r, 0, false);
        }
    }

    @Override
    public String toString(Ring r) {
        Polynom pp = this;
        boolean polCoef = false;
        for (int i = 0; i < coeffs.length; i++) {
            if (pp.coeffs[i].numbElementType() >= Ring.Polynom) {
                polCoef = true;
                break;
            }
        }
        return (polCoef) ? pp.toStringF(r) : pp.toString(r, 0, false);
    }

    /**
     *
     * Преобразует полином из данного объекта в строку Параметры: ring - текущее
     * кольцо(отсюда берем названия переменных)
     */
    /**
     * Преобразует полином в строку Могут быть и "дикие" полиномы у которых
     * коэффициенты - это функции (тогда FPolynomType=true) или коэффициенты -
     * это другие полиномы (из второй алгебры, при этом vars_0 > 0 )
     *
     * @param ring -- ring
     * @param vars_0 -- the number of variable in the coefficients. It is equal
     * 0 for the case of number-coefficients
     * @param FPolynomType -- the type of coefficients (true="any functions",
     * false="any numbers")
     *
     * @return
     */
    public String toString(Ring ring, int vars_0, boolean FPolynomType) {
        Polynom p = this;
        if (p.isZero(ring)) {
            return "0";
        }
        int monoms = coeffs.length;
        int vars = powers.length / monoms;
        boolean flag_N1; // For the case of only one monom we'll put "true"
        StringBuffer monom; // буфер для результата одного монома
        StringBuilder s = new StringBuilder(); // буфер для результата
        for (int j = 0; j < monoms; j++) { // j-номер монома=0..monoms-1
            flag_N1 = false;
            monom = new StringBuffer();
            Element X = coeffs[j];
            if (FPolynomType) {
                monom.append("(").append(coeffs[j].toString(ring)).append(")");
            } else {
                if (X.isMinusOne(ring)) {
                    monom.append("-");
                } else {
                    if (!X.isOne(ring)) {
                        monom.append(X.toString(ring));
                        flag_N1 = true;
                    }
                }
            }
            for (int i = vars - 1; i >= 0; i--) {
                if (powers[j * vars + i] > 0) {
                    flag_N1 = true;                                  
                    String vN=(ring.varNames.length>(i + vars_0))?
                            ring.varNames[i + vars_0]:
                            (ring.CForm.newRing.varNames.length>i + vars_0)?
                            ring.CForm.newRing.varNames[i + vars_0]: "#";
                    monom.append(vN);  // ЗАПЛАТКА ot 12 03 2016 для ловли ошибок
                } // если степень переменной>0,то добавить переменную в s
                if (powers[j * vars + i] > 1) {
                    // если степень переменной>1, то добавим "^степень
                    monom.append('^').append(powers[j * vars + i]);
                }
            }
            if ((j != 0)
                    && ((((monom.length() > 0) && (monom.charAt(0) != '-')) || (monom.
                    length() == 0)))) {
                s.append("+");
            }
            s.append(monom);
            if (!flag_N1) {
                s.append("1");
            }
        }
        return s.toString(); // преобразуем буфер в строку и возвратим ее
    }

    public Polynom toPolynom(Ring ring) {
        int oneType = ring.algebra[0];
        return toPolynom(oneType, ring);
    }

    @Override
    public Element toPolynomial(int newType, Ring ring) {
        Element oneOfNewPCoeff = Ring.oneOfType(newType);
        return toPolynomial(oneOfNewPCoeff, ring);
    }

//    private int helpToSearchscalePolynomToR64(NumberR64 coef, int scale, Ring r) {
//        boolean fl = true;
//        int chFl = scale - 1;
//        double d, dd;
//        Element t;
//        while (fl) {
//            chFl++;
//            d = Math.pow(10.0, chFl);
//            dd = coef.doubleValue() * d;
//            t = NumberZ.ONE.valOf(Math.rint(dd), r);
//            fl = (d * r.MachineEpsilonR64.doubleValue() <= 1) & (t.isZero(r));
//        }
//        return chFl;
//    }

//    /**
//     * Transform Polynom over R64 to Polynom over Z and comput scale.
//     *
//     * @param powOfMultiplyer
//     *
//     * @return
//     */
//    public Polynom toPolynomZfromR64(NumberZ64 powOfMultiplyer, Ring r) {
//        int maxScale = r.FLOATPOS;
//        int ii;
//        for (Element c : coeffs) {
//            ii = helpToSearchscalePolynomToR64((NumberR64) c, maxScale, r);
//            maxScale = Math.max(ii, maxScale);
//        }
//        if (maxScale > r.FLOATPOS) {
//            r.FLOATPOS = maxScale;
//            r.exception.append("Value of a variable FLOATPOS was changed to ").append(maxScale).append("\n");
//        }
//
//        Element[] cc = new Element[coeffs.length];
//        int i = 0;
//        double d = Math.pow(10.0, maxScale);
//        boolean fl = false;
//        for (Element c : coeffs) {
//            cc[i++] = NumberZ.ONE.valOf(Math.rint((((NumberR64) c).doubleValue()) * d), r);
//            if (cc[i - 1].isZero(r)) {
//                fl = true;
//            }
//        }
//        powOfMultiplyer.value = maxScale;
//        return (fl) ? new Polynom(powers, cc).deleteZeroCoeff(r) : new Polynom(powers, cc);
//    }

    /**
     * Transform Polynom over C64 or R64 to Polynom over CZ or Z and comput scale.
     *
     * @param powOfMultiplyer
     *
     * @return
     */
    public Polynom toPolynom_Zfrom_64(NumberZ64 powOfMultiplyer, Ring r) {
 //       int maxScale = r.FLOATPOS;
//        int ii;
//        for (Element c : coeffs) {
//            Element cc=(c instanceof Complex)? ((Complex) c).re: c;
//            NumberR64 r64=(NumberR64)((cc instanceof NumberR64)?cc: cc.toNumber(Ring.R64, r));
//      //      ii = helpToSearchscalePolynomToR64(r64, maxScale, r);
//       //     maxScale = Math.max(ii, maxScale);
//
//            if(c instanceof Complex){cc= ((Complex) c).im;
//              r64=(NumberR64)((cc instanceof NumberR64)?cc: cc.toNumber(Ring.R64, r));
//   //           ii = helpToSearchscalePolynomToR64( r64, maxScale, r);
//     //         maxScale = Math.max(ii, maxScale);  
//            }
//        }
//        if (maxScale > r.FLOATPOS) {
//            r.FLOATPOS = maxScale;
//            r.exception.append("Value of a variable FLOATPOS was changed to ").append(maxScale).append("\n");
//        }
        Element[] cc = new Element[coeffs.length];
        int i = 0;
        double d = Math.rint( 1.0/r.MachineEpsilonR64.value);  //Math.pow(10.0, maxScale);
        Element im, re;
        boolean fl = false;
        for (Element c : coeffs) {Element r64,i64;
            if (c instanceof Complex){r64=((Complex) c).re;i64=((Complex) c).im;
              re = NumberZ.ONE.valOf(Math.rint(r64.doubleValue() * d), r);
              im = NumberZ.ONE.valOf(Math.rint(i64.doubleValue() * d), r);
              cc[i] =  new Complex(re, im);}
            else {cc[i] = NumberZ.ONE.valOf(Math.rint(c.doubleValue() * d), r);}
            if (cc[i].isZero(r)) fl = true; i++;
        }
 //       powOfMultiplyer.value = maxScale;
        return (fl) ? new Polynom(powers, cc).deleteZeroCoeff(r) : new Polynom(powers, cc);
    }

//    /**
//     * Transform Polynom over R to Polynom over Z and comput maxScale. THe
//     * parameter powOfMultiplyer will the number of maxScale (the biggest scale
//     * of NumberR among all coefficients)
//     *
//     * @param powOfMultiplyer
//     *
//     * @return
//     */
//    public Polynom toPolynomZfromR(NumberZ64 powOfMultiplyer) {
//        int maxScale = 0, ii=0;
//        for (Element c : coeffs) {
//            if (c instanceof Complex){ Complex cc=(Complex)c;
//              ii = ((NumberR) cc.re).scale(); if (ii > maxScale)   maxScale = ii;
//              ii = ((NumberR) cc.im).scale(); if (ii > maxScale)   maxScale = ii;}
//            else {  ii = ((NumberR) c).scale(); if (ii > maxScale)   maxScale = ii;}          
//        }
//        Element[] newC = new Element[coeffs.length];
//        int i = 0;
//        for (Element c : coeffs) {
//             if (c instanceof Complex){ Complex cc=(Complex)c;
//               ii = maxScale - ((NumberR) cc.re).scale();
//               Element RR=((NumberR) cc.re).toBigInteger().multiply(NumberZ.tenInPowerOf(ii));
//               ii = maxScale - ((NumberR) cc.im).scale();
//               Element II=((NumberR) cc.im).toBigInteger().multiply(NumberZ.tenInPowerOf(ii));
//               newC[i++] = new Complex(RR,II);}
//             else{ ii = maxScale - ((NumberR) c).scale();
//                   newC[i++] = ((NumberR) c).toBigInteger().multiply(NumberZ.tenInPowerOf(ii));}
//        }
//        powOfMultiplyer.value = maxScale;
//        return new Polynom(powers, newC);
//    }

    /**
     * Transform Polynom over C(or R) to Polynom over CZ (Z) and comput maxScale. The
     * parameter powOfMultiplyer will be the number of maxScale (the biggest scale
     * of NumberR among all coefficients) 
     * @param NumberZ64 powOfMultiplyer  -- здесь возвращается экспонента Домножителя=10^{maxScale}: 
     *           числа на которое были умножены все коэффициенты этого полинома
     * @return 
     */
    public Polynom toPolynomCZfromC(NumberZ64 powOfMultiplyer) {
        int maxScale = 0;
        Element[] cc = new Element[coeffs.length];
        int i = 0;
        int ii=0;
        Element re, im;
        for (Element c : coeffs) { // находим максимальный scale у всех коэфф-тов
            if (c instanceof Complex)
                 maxScale = Math.max(maxScale, Math.max(((NumberR) ((Complex) c).re).scale(), 
                                                        ((NumberR) ((Complex) c).im).scale()));
            else maxScale = Math.max(maxScale,  ((NumberR)c).scale() );
        }
        for (Element c : coeffs) {   
          if (c instanceof Complex){re = ( ((Complex) c).re)
                    .multiply(NumberZ.tenInPowerOf(maxScale), Ring.ringZxyz);
            re=re.toNumber(Ring.Z,Ring.ringZxyz);
            im = (((Complex) c).im).multiply(NumberZ.tenInPowerOf(maxScale), Ring.ringZxyz);
            im=im.toNumber(Ring.Z,Ring.ringZxyz);
            cc[i++] = (im.isZero(Ring.ringZpX))? re : new Complex(re, im);
          }else cc[i++] =  c.multiply(NumberZ.tenInPowerOf(maxScale), Ring.ringZxyz).toNumber(Ring.Z,Ring.ringZxyz);
        }
        powOfMultiplyer.value = maxScale;
        return new Polynom(powers, cc);
    }

    /**
     * Transform Polynom over CQ to Polynom over CZ and comput lcm of denoms
     * among all coefficients.
     */
    public Polynom toPolynomCZfromCQ(Element[] one, Ring ring) {
        if (coeffs.length == 0)  return this;
        Element[] dens = new Element[2 * coeffs.length];
        Element re;
        Element im;
        int indexDens = 0;
        for (int i = 0; i < coeffs.length; i++) { Element ci=coeffs[i];
            if (ci instanceof Complex) {
               dens[indexDens] = (((Complex) ci).re instanceof Fraction) ? ((Fraction) ((Complex) ci).re).denom : NumberZ.ONE;
               indexDens++;
               dens[indexDens] = (((Complex) ci).im instanceof Fraction) ? ((Fraction) ((Complex) ci).im).denom : NumberZ.ONE;
            }else{
               dens[indexDens] = (ci instanceof Fraction) ? ((Fraction) ci).denom : new NumberZ(1); indexDens++; dens[indexDens]=NumberZ.ONE;}         
            indexDens++;
        }
        Element densLCM = arrayLCM(dens, ring);
        Element[] cc = new Element[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {Element ci=coeffs[i];
           if (ci instanceof Complex) {
               re
                    = (((Complex) ci).re instanceof Fraction)
                    ? densLCM.multiply(((Fraction) ((Complex) ci).re).num, ring).divide(((Fraction) ((Complex) ci).re).denom, ring)
                    : densLCM.multiply(((Complex) ci).re, ring);
              im
                    = (((Complex) ci).im instanceof Fraction)
                    ? densLCM.multiply(((Fraction) ((Complex) ci).im).num, ring).divide(((Fraction) ((Complex) ci).im).denom, ring)
                    : densLCM.multiply(((Complex) ci).im, ring);
              cc[i] = new Complex(re, im);
           }else{
            cc[i]  = (  ci instanceof Fraction)
                    ? densLCM.multiply(((Fraction) ci).num, ring).divide(((Fraction) ci).denom, ring)
                    : densLCM.multiply(ci, ring);         
           }        
        }
        one[0] = densLCM;
        return new Polynom(powers, cc);
    }

    public Polynom toPolynomZfromQ(Element[] one, Ring ring) {
        if (coeffs.length == 0) {
            return Polynom.polynomZero;
        }
        Element[] dens = new Element[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            dens[i] = (coeffs[i] instanceof Fraction) ? ((Fraction) coeffs[i]).denom : new NumberZ(1);
        }
        Element densLCM = arrayLCM(dens, ring);
        Element[] cc = new Element[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            coeffs[i] = (coeffs[i] instanceof Fraction)
                    ? densLCM.multiply(((Fraction) coeffs[i]).num, ring).divide(((Fraction) coeffs[i]).denom, ring)
                    : densLCM.multiply(coeffs[i], ring);
            cc[i] = coeffs[i];
        }
        one[0] = densLCM;
        return new Polynom(powers, cc);
    }

    /** 
     * Создание из данного полинома нового полинома над числовым кольцом того же
     * нового числового типа. Каждый числовой коэффициент преобразуется к новыму
     * числовыму типу. Если возникают нулевые коэффициенты, то они удаляются из
     * записи полинома.
     *
     * @param oneType -- тот числовой типа, к которому приводятся все
     * коэффициенты
     *
     * @return -- полином в новом чиловом кольце
     */
    public Polynom toPolynom(int oneType, Ring ring) {
        if (coeffs.length == 0) {
            return Polynom.polynom_zero(Ring.oneOfType(oneType));
        }
        int typeCoeff = coeffs[0].numbElementType();
        //Если типы совпадают
        if ((typeCoeff == oneType)
                || (((oneType & (~Ring.MASK_C)) == Ring.Q)
                && (((typeCoeff & (~Ring.MASK_C)) == Ring.Z)
                || ((typeCoeff & (~Ring.MASK_C)) == Ring.Q)))) {
            return this;
        }
        int v = powers.length / coeffs.length;
        Element[] cc = new Element[coeffs.length];
        int[] pow = new int[powers.length];
        int j = 0, s = 0, t = 0;
        for (int i = 0; i < coeffs.length; i++) {
            cc[s] = coeffs[i].toNumber(oneType, ring);
            if (cc[s] == null) {
                System.out.println("cc[s]==" + s + "  " + cc.length + "  " + coeffs[i] + "  " + ring.algebra[0] + " " + ring.MOD32);
            }
            if (!cc[s].isZero(ring)) {
                s++;
                for (int k = 0; k < v; k++) {
                    pow[j++] = powers[t++];
                }
            } else {
                t += v;
            }
        }
        Polynom res;
        if (s == coeffs.length) {
            res = new Polynom(pow, cc);
        } else {
            Element[] cc1 = new Element[s];
            int[] pow1 = new int[j];
            System.arraycopy(cc, 0, cc1, 0, s);
            System.arraycopy(pow, 0, pow1, 0, j);
            res = new Polynom(pow1, cc1);
        }
        return res;
    }

    /**
     * Создание из данного полинома нового полинома над числовым кольцом того же
     * типа, что и числовой скалярный аргумент one. Каждый числовой коэффициент
     * преобразуется к новыму числовыму типу. Если возникают нулевые
     * коэффициенты, то они удаляются из записи полинома.
     *
     * @param one -- единица того числового типа, к которому приводятся все
     * коэффициенты
     *
     * @return -- полином в новом чиловом кольце
     */
    public Polynom toPolynomMod(int oneType, Ring ring) {
        if (coeffs.length == 0) {
            return Polynom.polynom_zero(Ring.oneOfType(oneType));
        }
        Element mod = (oneType == Ring.Zp32) ? new NumberZp32(ring.MOD32)
                : new NumberZp32(ring.MOD, ring);
        int v = powers.length / coeffs.length;
        Element[] cc = new Element[coeffs.length];
        int[] pow = new int[powers.length];
        int j = 0, s = 0, t = 0;
        for (int i = 0; i < coeffs.length; i++) {

            cc[s] = coeffs[i].toNumber(oneType, ring);

            cc[s] = cc[s].Mod(ring);

            if (!cc[s].isZero(ring)) {
                s++;
                for (int k = 0; k < v; k++) {
                    pow[j++] = powers[t++];
                }
            } else {
                t += v;
            }
        }
        Polynom res;
        if (s == coeffs.length) {
            res = new Polynom(pow, cc);
        } else {
            Element[] cc1 = new Element[s];
            int[] pow1 = new int[j];
            System.arraycopy(cc, 0, cc1, 0, s);
            System.arraycopy(pow, 0, pow1, 0, j);
            res = new Polynom(pow1, cc1);
        }
        return res;
    }

    public Polynom changeOrderOfVars(int[] varsMap, Ring ring) {
        return changeOrderOfVars(varsMap, true, ring);
    }

    /**
     * меняем переменные местами в зависимости от их положения в массиве int[]
     * varsMap
     *
     * @param varsMap
     * @param bol if true then [2,3,1] lead to x-->2, y-->3, z-->1 if false then
     * [2,3,1] lead to x-->3, y-->1, z-->2
     * @param ring
     *
     * @return
     */
    public Polynom changeOrderOfVars(int[] varsMap, boolean bol, Ring ring) {

        // this=0
        if (coeffs.length == 0)  return new Polynom(new int[0], new Element[0]);
        
        // this=числу
        if (powers.length == 0)  return this;
        // this=полиному и changePowers=true
        int monoms1 = coeffs.length;
        int vars1 = powers.length / coeffs.length;
        int[] powers2;
        if (bol) {
            powers2 = convertPowers_1(varsMap, powers, monoms1, vars1);
        } else {
            powers2 = convertPowers_2(varsMap, powers, monoms1, vars1);
        }
        return makePolynom(powers2, coeffs, getReorder(varsMap, vars1), ring);
    }

    /**
     *
     * @param powers
     * @param coeffs
     * @param reorder
     *
     * @return
     */
    public static Polynom makePolynom(int[] powers, Element[] coeffs,
            boolean reorder, Ring ring) {
        if (reorder) {
            return new Polynom(powers, coeffs).ordering(0, 0, coeffs.length,
                    ring);
        } else {
            return new Polynom(powers, coeffs);
        }
    }

    /**
     * Определяет нужно ли переупорядочивать после преобразования полинома с
     * реальным кол-вом переменных realVars.
     *
     * @param varsMap отображение переменных из старого кольца в новое
     * @param realVars реальное кол-во переменных
     *
     * @return boolean true, если нужно переупорядочивать
     */
    public static boolean getReorder(int[] varsMap, int realVars) {
        for (int i = 1; i < realVars; i++) {
            if (varsMap[i] < varsMap[i - 1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получить кол-во переменных в полиноме, после преобразования
     *
     * @param varsMap int[] отображение переменных
     * @param vars int число переменных в исходном полиноме
     *
     * @return int -- кол-во переменных в полиноме, после преобразования. Это
     * будет максимальное число в varsMap +1.
     */
    public static int getVarsAfterConvert(int[] varsMap, int vars) {
        int max = varsMap[0];
        for (int i = 1; i < vars; i++) {
            if (varsMap[i] > max) {
                max = varsMap[i];
            }
        }
        return max + 1;
    }

    /**
     * изменяем массив степеней, перемещая степень из позиции i в позицию
     * varsMap[i]
     *
     * @param varsMap
     * @param powers1
     * @param monoms1
     * @param vars1
     *
     * @return
     */
    public static int[] convertPowers_1(int[] varsMap, int[] powers1,
            int monoms1, int vars1) {
        int vars2 = getVarsAfterConvert(varsMap, vars1);
        int[] powers2 = new int[monoms1 * vars2];
        int adr1 = 0, adr2 = 0;
        for (int i = 0; i < monoms1; i++) {
            for (int j = 0; j < vars1; j++) {
                powers2[adr2 + varsMap[j]] = powers1[adr1++];
            }
            adr2 += vars2;
        }
        return powers2;
    }

    /**
     * изменяем массив степеней, перемещая степень из varsMap[i] в i
     *
     * @param varsMap
     * @param powers1
     * @param monoms1
     * @param vars1
     *
     * @return
     */
    public static int[] convertPowers_2(int[] varsMap, int[] powers1,
            int monoms1, int vars1) {
        int vars2 = getVarsAfterConvert(varsMap, vars1);
        int[] powers2 = new int[monoms1 * vars2];
        int adr1 = 0, adr2 = 0;
        for (int i = 0; i < monoms1; i++) {
            for (int j = 0; j < vars2; j++) {
                powers2[adr2++] = (varsMap[j]>=vars1)?0:powers1[adr1 + varsMap[j]];
            }
            adr1 += vars1;
        }
        return powers2;
    }

    /*
     * Метод add возвращает сумму полиномов this и p, хранящихся в формате с
     * разным кол-вом переменных. Параметры: p - полином типа Polynom
     *
     * Если this=0 и p=0, то возвращаем сумму ZERO-нулевой полином. (Полином
     * считается нулевым, если кол-во мономов=0) Если this=0, то возвращаем p.
     * Если p=0, то возвращаем this.
     */
    public Polynom add(Polynom p, Ring ring) {
        if (coeffs.length == 0 && p.coeffs.length == 0) {
            return myZero(ring);
        } else if (coeffs.length == 0) {
            return p;
        } else if (p.coeffs.length == 0) {
            return this;
        }
        Polynom pp = add(powers, coeffs, 0, powers.length, 0, coeffs.length,
                p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                false, ring);
        return pp;
    }

    @Override
    public Element add(Element pp, Ring ring) {
        if (isZero(ring)) {
            return pp;
        }
        switch (pp.numbElementType()) {
            case Ring.Polynom:
                return add((Polynom) pp, ring);
            case Ring.F:
                return pp.add(this, ring);
            case Ring.Fname:
                return new F(F.ADD, new Element[] {this, pp});
            case Ring.Q:
            case Ring.Rational:
                return ((Fraction) pp).add(this, ring);
            default:
                if (pp.isZero(ring)) {
                    return this;
                }

                // остался случай pp --это число
                return add(powers, coeffs, 0, powers.length, 0, coeffs.length,
                        new int[] {}, new Element[] {pp}, 0, 0, 0, 1, false, ring);
        }
    }

    /**
     * Метод add отличается от обычного add тем, что если указать параметр
     * copy=true, то он будет копировать полиномы в случаях: 1) 0+p = p, 2) p+0
     * = p, результат будет копией p. в остальных случаях он ведет себя как
     * обычный add.
     *
     * @param p Polynom вычитаемый полином
     * @param copy boolean если =true, то копировать массивы степеней в случаях:
     * 0+p, p+0, если =false, то результат в случаях: 0+p, p+0 -- это ссылка на
     * входной p (как в обычном add)
     *
     * @return Polynom this+p
     */
    public Polynom add(Polynom p, boolean copy, Ring ring) {
        if (copy) {
            return add(powers, coeffs, 0, powers.length, 0, coeffs.length,
                    p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                    false, ring);
        } else {
            if (coeffs.length == 0 && p.coeffs.length == 0) { // Если this=0 и
                // p=0,
                return myZero(ring); // то возвращаем ZERO-нулевой полином.
            } else if (coeffs.length == 0) { // Если this=0, то
                return p; // возвращаем p
            } else if (p.coeffs.length == 0) { // Если p=0, то
                return this; // возвращаем this
            }
            return add(powers, coeffs, 0, powers.length, 0, coeffs.length,
                    p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                    false, ring);
        }
    }

    /*
     * Метод add возвращает сумму частей полиномов this от beg1 до end1 и p от
     * beg2 до end2. Параметры: p - полином типа Polynom beg1, end1 - номера
     * начального и конечного монома в this beg2, end2 - номера начального и
     * конечного монома в p
     *
     * Если this=0 и p=0, то возвращаем сумму ZERO-нулевой полином. (Полином
     * считается нулевым, если кол-во мономов=0) Если this=0, то возвращаем
     * копию p (=p.cloneWithoutCFormPage()). Если p=0, то возвращаем копию this (=cloneWithoutCFormPage()).
     */
    public Polynom add(Polynom p, int beg1, int end1, int beg2, int end2,
            Ring ring) {
        int fvars1 = powers.length / coeffs.length;
        int fvars2 = p.powers.length / p.coeffs.length;
        return add(powers, coeffs, beg1 * fvars1, end1 * fvars1, beg1, end1,
                p.powers, p.coeffs, beg2 * fvars2, end2 * fvars2, beg2, end2,
                true, ring);
    }

    /*
     * Сложение полиномов. powers1, coeffs1 - массивы, где находятся степени и
     * коэффициенты 1-го полинома pb1, pe1 - адрес начала и конца степеней 1-го
     * полинома в powers1 cb1, ce1 - адрес начала и конца коэффициентов 1-го
     * полинома в coeffs1 powers2, coeffs2 - массивы, где находятся степени и
     * коэффициенты 2-го полинома pb2, pe2 - адрес начала и конца степеней 2-го
     * полинома в powers2 cb2, ce2 - адрес начала и конца коэффициентов 2-го
     * полинома в coeffs2
     */
    public static Polynom add(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring ring) {
        if (ce1 == cb1 && ce2 == cb2) { // Если this=0 и p=0,
            return polynomFromNumber(coeffs1[0], ring);
        } else if (ce1 == cb1) { // Если this=0, то возвращаем копию p
            int[] pows = new int[pe2 - pb2];
            Element[] cfs = new Element[ce2 - cb2];
            System.arraycopy(powers2, pb2, pows, 0, pows.length);
            System.arraycopy(coeffs2, cb2, cfs, 0, cfs.length);
            return new Polynom(pows, cfs);
        } else if (ce2 == cb2) { // Если p=0, то возвращаем копию this
            int[] pows = new int[pe1 - pb1];
            Element[] cfs = new Element[ce1 - cb1];
            System.arraycopy(powers1, pb1, pows, 0, pows.length);
            System.arraycopy(coeffs1, cb1, cfs, 0, cfs.length);
            return new Polynom(pows, cfs);
        }

        // Кол-во переменных в массивах полиномов
        int fvars1 = (pe1 - pb1) / (ce1 - cb1);
        int fvars2 = (pe2 - pb2) / (ce2 - cb2);

        // Если getVars==true, то нужно определить реальное кол-во переменных
        int v1, v2;
        if (getRealVars) {
            // вычисляем реальное кол-во переменных по 1-м мономам
            v1 = getRealVars(powers1, pb1, fvars1);
            v2 = getRealVars(powers2, pb2, fvars2);
        } else {
            v1 = fvars1;
            v2 = fvars2;
        }

        if (v1 < v2) {
            int[] tempArr = powers1;
            powers1 = powers2;
            powers2 = tempArr;

            Element[] tempBI = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = tempBI;

            int temp = v1;
            v1 = v2;
            v2 = temp;

            temp = fvars1;
            fvars1 = fvars2;
            fvars2 = temp;

            temp = pb1;
            pb1 = pb2;
            pb2 = temp;

            temp = pe1;
            pe1 = pe2;
            pe2 = temp;

            temp = cb1;
            cb1 = cb2;
            cb2 = temp;

            temp = ce1;
            ce1 = ce2;
            ce2 = temp;
        }

        // максимально возможное число мономов в сумме
        int monNumb = ce1 - cb1 + ce2 - cb2;

        // Создадим массивы степеней и коэффициентов для суммы.
        // В сумме будет максимум v1 переменная.
        int[] pows = new int[monNumb * v1];
        Element[] cfs = new Element[monNumb];

        int j1 = ce1 - 1; // j1 - номер монома в p1
        int adr1 = pe1 - fvars1; // adr1 - адрес j1-го монома
        int j2 = ce2 - 1;
        int adr2 = pe2 - fvars2;
        int j = monNumb - 1; // j-номер монома суммы
        int adr = j * v1; // adr=адрес j-го монома суммы=j*v1

        int i1; // i1 и i2 - переменные для хранения adr1 и adr2
        int i2;
        int i;
        int resAdr, a, shift1, shift2;

        while (j1 >= cb1 && j2 >= cb2) {
            i1 = adr1 + v1 - 1; // сравним j1-й j2-й мономы:
            i2 = adr2 + v2 - 1; // запишем в i1 и i2 адреса последних переменных
            i = v1 - 1;

            for (int k = 0; k < v1 - v2; k++) {
                if (powers1[i1] != 0) {
                    break;
                }
                i1--;
                i--;
            }
            if (i > v2 - 1) {

                // построить результат:
                // остаток p1, остаток p2, уже готовая часть суммы
                int[] respowers;
                Element[] rescoeffs;
                if (ce1 - j1 + ce2 - j2 - 1 == cfs.length - j) {
                    // если не было подобных слагаемых, то в качестве
                    // результата можно использовать pows и cfs. Причем в
                    // конце pows и cfs уже записана часть суммы!
                    respowers = pows;
                    rescoeffs = cfs;
                } else {
                    // иначе придется создать новые массивы, т.к. pows и cfs
                    // будут больше по размеру, чем нужно (на кол-во подобных)
                    rescoeffs = new Element[j1 - cb1 + j2 - cb2 + monNumb - j
                            + 1];
                    respowers = new int[rescoeffs.length * v1];
                    // Скопировать в конец массивов результата часть суммы
                    System.arraycopy(pows, adr + v1, respowers, (j1 - cb1 + j2
                            - cb2 + 2)
                            * v1, (monNumb - j - 1) * v1);
                    System.arraycopy(cfs, j + 1, rescoeffs, j1 - cb1 + j2 - cb2
                            + 2, monNumb - j - 1);
                }
                // Сопировать остаток p1
                if (fvars1 == v1) {
                    // если p1 хранится в формате v1, то использовать
                    // метод быстрого копирования
                    System.arraycopy(powers1, pb1, respowers, 0, adr1 - pb1
                            + v1);
                } else {
                    // fvars1>v1
                    resAdr = 0;
                    a = pb1;
                    shift1 = fvars1 - v1;
                    for (int k = 0; k < j1 - cb1 + 1; k++) {
                        for (int v = 0; v < v1; v++) {
                            respowers[resAdr++] = powers1[a++];
                        }
                        a += shift1;
                    }
                }
                System.arraycopy(coeffs1, cb1, rescoeffs, 0, j1 - cb1 + 1);

                // Скопировать остаток p2.
                if (fvars2 == v1) {
                    // если p2 хранится в формате v1, то использовать
                    // метод быстрого копирования
                    System.arraycopy(powers2, pb2, respowers, (j1 - cb1 + 1)
                            * v1, adr2 - pb2 + v1);
                } else {
                    // v1>fvars2>v2 или fvars2>v1>v2
                    resAdr = (j1 - cb1 + 1) * v1;
                    a = pb2;
                    shift1 = v1 - v2;
                    shift2 = fvars2 - v2;
                    for (int k = 0; k < j2 - cb2 + 1; k++) {
                        for (int v = 0; v < v2; v++) {
                            respowers[resAdr++] = powers2[a++];
                        }
                        resAdr += shift1;
                        a += shift2;
                    }
                }
                System.arraycopy(coeffs2, cb2, rescoeffs, j1 - cb1 + 1, j2
                        - cb2 + 1);

                return new Polynom(respowers, rescoeffs);
            }

            while (i >= 0 && powers1[i1] == powers2[i2]) {
                i--;
                i1--;
                i2--;
            }
            if (i == -1) { // если степени мономов равны, то
                cfs[j] = coeffs1[j1].add(coeffs2[j2], ring); // сложить их
                // коэффициенты
                if (cfs[j].isZero(ring)) {
                    // если их сумма = 0, то увеличить номер монома суммы(в
                    // конце он уменьшится),
                    // т.е. он не изменится
                    j++;
                    adr += v1;
                } else {
                    // иначе если сумма коэффициентов не равна 0, то
                    // копировать степени мономов в сумму по адресу adr
                    System.arraycopy(powers1, adr1, pows, adr, v2);
                }
                j1--;
                adr1 -= fvars1;
                j2--;
                adr2 -= fvars2;

            } else if (powers1[i1] < powers2[i2]) {

                cfs[j] = coeffs1[j1];
                System.arraycopy(powers1, adr1, pows, adr, v2);
                j1--;
                adr1 -= fvars1;

            } else {
                cfs[j] = coeffs2[j2];
                System.arraycopy(powers2, adr2, pows, adr, v2);
                j2--;
                adr2 -= fvars2;
            }
            j--; // перейти к следующему моному в сумме
            adr -= v1;
        }

        if (j1 >= cb1) {
            // Построить результат из остатка p1 и суммы.
            // Т.к. в начале результата будет 1-й моном p1, в котором v1
            // переменная, то в результате будет v1 переменная!
            int[] respowers;
            Element[] rescoeffs;
            // Если при сложении не было подобных, то можно использовать
            // в качестве результата pows и cfs!
            if (ce2 - j2 + ce1 - j1 - 1 == monNumb - j) {
                respowers = pows;
                rescoeffs = cfs;
            } else {
                // иначе придется создать новые массивы и копировать в конец
                // сумму.
                respowers = new int[(j1 - cb1 + monNumb - j) * v1];
                rescoeffs = new Element[j1 - cb1 + monNumb - j];
                System.arraycopy(pows, adr + v1, respowers,
                        (j1 - cb1 + 1) * v1, (monNumb - j - 1) * v1);
                System.arraycopy(cfs, j + 1, rescoeffs, j1 - cb1 + 1, monNumb
                        - j - 1);
            }
            // Скопировать остаток p1
            if (fvars1 == v1) {
                // если p1 хранится в формате v1, то использовать
                // метод быстрого копирования
                System.arraycopy(powers1, pb1, respowers, 0, adr1 - pb1 + v1);
            } else {
                // fvars1>v1
                resAdr = 0;
                a = pb1;
                shift1 = fvars1 - v1;
                for (int k = 0; k < j1 - cb1 + 1; k++) {
                    for (int v = 0; v < v1; v++) {
                        respowers[resAdr++] = powers1[a++];
                    }
                    a += shift1;
                }
            }
            System.arraycopy(coeffs1, cb1, rescoeffs, 0, j1 - cb1 + 1);
            return new Polynom(respowers, rescoeffs); // вернуть сумму
        }

        if (j2 >= cb2) {
            // Построить результат из остатка p2 и суммы. Т.к. этот
            // случай возник, то v1==v2 и 1-м мономом результата будет p2,
            // то в результате будет v1 переменная.
            int[] respowers;
            Element[] rescoeffs;
            // Если при сложении не было подобных, то можно использовать
            // в качестве результата pows и cfs!
            if (ce2 - j2 + ce1 - j1 - 1 == monNumb - j) {
                respowers = pows;
                rescoeffs = cfs;
            } else {
                respowers = new int[(monNumb - j + j2 - cb2) * v1];
                rescoeffs = new Element[monNumb - j + j2 - cb2];
                System.arraycopy(pows, adr + v1, respowers,
                        (j2 - cb2 + 1) * v1, (monNumb - j - 1) * v1);
                System.arraycopy(cfs, j + 1, rescoeffs, j2 - cb2 + 1, monNumb
                        - j - 1);
            }
            if (fvars2 == v1) {
                // если p2 хранится в формате v1, то использовать
                // метод быстрого копирования
                System.arraycopy(powers2, pb2, respowers, 0, adr2 - pb2 + v1);
            } else {
                // fvars2>v1(=v2)
                resAdr = 0;
                a = pb2;
                shift2 = fvars2 - v2;
                for (int k = 0; k < j2 - cb2 + 1; k++) {
                    for (int v = 0; v < v2; v++) {
                        respowers[resAdr++] = powers2[a++];
                    }
                    a += shift2;
                }
            }
            System.arraycopy(coeffs2, cb2, rescoeffs, 0, j2 - cb2 + 1);
            return new Polynom(respowers, rescoeffs); // вернуть сумму
        }

        // j1=cb1-1 и j2=cb2-1, то проверим равна ли сумма 0
        // При этом обязательно должны быть подобные мономы, иначе
        // было бы j1>=cb1 или j2>=cb2!
        if (j == monNumb - 1) {
            // если указатель j остался равным monNumb-1, то все суммы
            // коэффициентов были равны 0, то сумма полиномов равна 0
            return new Polynom(new int[0], new Element[0]);

        } else {
            // иначе были подобные, т.к. j1=cb1-1 и j2=cb2-1.
            // Скопировать массивы pows и cfs в новые
            // Получить реальное кол-во переменных в результате.
            // resVars<=v1
            int resVars = getRealVars(pows, adr + v1, v1);
            if (resVars == 0) {
                // результат=числу
                return new Polynom(new int[0], new Element[] {cfs[j + 1]});
            } else {
                // resVars>0
                int[] respowers = new int[(monNumb - j - 1) * resVars];
                if (resVars == v1) {
                    System.arraycopy(pows, adr + v1, respowers, 0,
                            respowers.length);
                } else {
                    // resVars<v1
                    resAdr = 0;
                    int powsAdr = adr + v1;
                    int shift = v1 - resVars;
                    for (i = 0; i < monNumb - j - 1; i++) {
                        for (int v = 0; v < resVars; v++) {
                            respowers[resAdr++] = pows[powsAdr++];
                        }
                        powsAdr += shift;
                    }
                }
                Element[] rescoeffs = new Element[monNumb - j - 1];
                System.arraycopy(cfs, j + 1, rescoeffs, 0, rescoeffs.length);
                return new Polynom(respowers, rescoeffs); // вернуть сумму
            }
        }
    }

    /**
     * Возвращает реальное кол-во переменных в мономе, который расположен в
     * массиве powers, начиная с beg и хранится в формате vars переменных.
     *
     * @param powers int[] массив степеней
     * @param beg int начало монома
     * @param vars int кол-во переменных в формате, vars>=реального кол-ва
     * переменных, vars>=0.
     *
     * @return int реальное кол-во переменных в мономе
     */
    public static int getRealVars(int[] powers, int beg, int vars) {
        int adr = beg + vars - 1;
        while (vars > 0 && powers[adr] == 0) {
            vars--;
            adr--;
        }
        return vars;
    }

    /**
     * Разность полиномов this-p. Если this=0 и p=0, то возвращаем ZERO -
     * нулевой полином. (Полином считается нулевым, если кол-во мономов=0) Если
     * this=0, то возвращаем -p. ВН�\uFFFDМАН�\uFFFDЕ: У -p и p общие массивы
     * powers. Чтобы -p был полной копией p нужно вызывать subtarct(p, true)
     * Если p=0, то возвращаем this.
     *
     * @param p Polynom вычитаемый полином
     *
     * @return Polynom this-p
     */
    public Polynom subtract(Polynom pp, Ring ring) {
        Polynom p = pp;
        int monoms1 = coeffs.length;
        int monoms2 = p.coeffs.length;

        if (monoms1 == 0 && monoms2 == 0) { // Если this=0 и p=0,
            return new Polynom(new int[0], new Element[0]); // то возвращаем
            // ZERO-нулевой
            // полином.
        } else if (monoms1 == 0) { // Если this=0, то
            Element[] resultCoeffs = new Element[monoms2];
            for (int i = 0; i < monoms2; i++) {
                resultCoeffs[i] = p.coeffs[i].negate(ring);
            }
            return new Polynom(p.powers, resultCoeffs); // вернуть -p
        } else if (monoms2 == 0) { // Если p=0, то
            return this; // возвращаем this
        }
        Polynom res = subtract(powers, coeffs, 0, powers.length, 0,
                coeffs.length, p.powers, p.coeffs, 0, p.powers.length, 0,
                p.coeffs.length, false, ring);
        return res;
    }

    @Override
    public Element subtract(Element pp, Ring ring) {
        switch (pp.numbElementType()) {
            case Ring.F:
                return (((F) pp).negate(ring)).add(this, ring);
            case Ring.Fname:
                return new F(F.SUBTRACT, new Element[] {this, pp});
            case Ring.Polynom:
                return subtract((Polynom) pp, ring);
            case Ring.Q:
            case Ring.Rational:
                return ((Fraction) pp).negate(ring).add(this, ring);
            default:
                if (pp.isZero(ring)) {
                    return this;
                }
                // остался случай pp --это число
                Polynom res = subtract(powers, coeffs, 0, powers.length, 0,
                        coeffs.length, new int[] {}, new Element[] {pp}, 0, 0, 0,
                        1, false, ring);
                return res;
        }
    }

    /**
     * Метод subtract отличается от обычного subtract тем, что если указать
     * параметр copy=true, то он будет копировать полиномы в случаях: 1) 0-p =
     * -p, -p будет иметь копию p.powers и коэффициенты, умноженные на -1, 2)
     * p-0 = p, результат будет копией p. в остальных случаях он ведет себя как
     * обычный subtract.
     *
     * @param p Polynom вычитаемый полином
     * @param copy boolean если =true, то копировать массивы степеней в случаях:
     * 0-p, p-0, если =false, то массивы степеней в случаях: 0-p, p-0 у
     * результата и p общие (как в обычном subtract)
     *
     * @return Polynom this-p
     */
    public Polynom subtract(Polynom p, boolean copy, Ring ring) {
        if (copy) {
            return subtract(powers, coeffs, 0, powers.length, 0, coeffs.length,
                    p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                    false, ring);
        } else {
            int monoms1 = coeffs.length;
            int monoms2 = p.coeffs.length;

            if (monoms1 == 0 && monoms2 == 0) { // Если this=0 и p=0,
                return new Polynom(new int[0], new Element[0]); // то возвращаем
                // ZERO-нулевой
                // полином.
            } else if (monoms1 == 0) { // Если this=0, то
                Element[] resultCoeffs = new Element[monoms2];
                for (int i = 0; i < monoms2; i++) {
                    resultCoeffs[i] = p.coeffs[i].negate(ring);
                }
                return new Polynom(p.powers, resultCoeffs); // вернуть -p
            } else if (monoms2 == 0) { // Если p=0, то
                return this; // возвращаем this
            }
            return subtract(powers, coeffs, 0, powers.length, 0, coeffs.length,
                    p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                    false, ring);
        }
    }

    /**
     * Индексная разность полиномов.
     *
     * @param p Polynom
     * @param beg1 int
     * @param end1 int
     * @param beg2 int
     * @param end2 int
     *
     * @return Polynom
     */
    public Polynom subtract(Polynom p, int beg1, int end1, int beg2, int end2,
            Ring r) {
        int fvars1 = powers.length / coeffs.length;
        int fvars2 = p.powers.length / p.coeffs.length;
        return subtract(powers, coeffs, beg1 * fvars1, end1 * fvars1, beg1,
                end1, p.powers, p.coeffs, beg2 * fvars2, end2 * fvars2, beg2,
                end2, true, r);
    }

    /*
     * Разность полиномов. powers1, coeffs1 - массивы, где находятся степени и
     * коэффициенты 1-го полинома pb1, pe1 - адрес начала и конца степеней 1-го
     * полинома в powers1 cb1, ce1 - адрес начала и конца коэффициентов 1-го
     * полинома в coeffs1 powers2, coeffs2 - массивы, где находятся степени и
     * коэффициенты 2-го полинома pb2, pe2 - адрес начала и конца степеней 2-го
     * полинома в powers2 cb2, ce2 - адрес начала и конца коэффициентов 2-го
     * полинома в coeffs2
     */
    public static Polynom subtract(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring ring) {
        if (ce1 == cb1 && ce2 == cb2) { // Если this=0 и p=0,
            return new Polynom(new int[0], new Element[0]); // то возвращаем
            // ZERO-нулевой
            // полином.
        } else if (ce1 == cb1) { // Если this=0, то возвращаем -p
            int[] pows = new int[pe2 - pb2];
            Element[] cfs = new Element[ce2 - cb2];
            System.arraycopy(powers2, pb2, pows, 0, pows.length);
            for (int i = cb2; i < ce2; i++) {
                cfs[i - cb2] = coeffs2[i].negate(ring);
            }
            return new Polynom(pows, cfs);
        } else if (ce2 == cb2) { // Если p=0, то возвращаем копию this
            int[] pows = new int[pe1 - pb1];
            Element[] cfs = new Element[ce1 - cb1];
            System.arraycopy(powers1, pb1, pows, 0, pows.length);
            System.arraycopy(coeffs1, cb1, cfs, 0, cfs.length);
            return new Polynom(pows, cfs);
        }

        // Кол-во переменных в массивах полиномов
        int fvars1 = (pe1 - pb1) / (ce1 - cb1);
        int fvars2 = (pe2 - pb2) / (ce2 - cb2);

        // Если getVars==true, то нужно определить реальное кол-во переменных
        int v1, v2;
        if (getRealVars) {
            // вычисляем реальное кол-во переменных по 1-м мономам
            v1 = getRealVars(powers1, pb1, fvars1);
            v2 = getRealVars(powers2, pb2, fvars2);
        } else {
            v1 = fvars1;
            v2 = fvars2;
        }

        boolean p1_minus_p2 = true;

        if (v1 < v2) {
            int[] tempArr = powers1;
            powers1 = powers2;
            powers2 = tempArr;

            Element[] tempBI = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = tempBI;

            int temp = v1;
            v1 = v2;
            v2 = temp;

            temp = fvars1;
            fvars1 = fvars2;
            fvars2 = temp;

            temp = pb1;
            pb1 = pb2;
            pb2 = temp;

            temp = pe1;
            pe1 = pe2;
            pe2 = temp;

            temp = cb1;
            cb1 = cb2;
            cb2 = temp;

            temp = ce1;
            ce1 = ce2;
            ce2 = temp;

            p1_minus_p2 = false;
        }

        // максимально возможное число мономов в разности
        int monNumb = ce1 - cb1 + ce2 - cb2;

        // Создадим массивы степеней и коэффициентов для разности.
        // В разности будет максимум v1 переменная.
        int[] pows = new int[monNumb * v1];
        Element[] cfs = new Element[monNumb];

        int j1 = ce1 - 1; // j1 - номер монома в p1
        int adr1 = pe1 - fvars1; // adr1 - адрес j1-го монома
        int j2 = ce2 - 1;
        int adr2 = pe2 - fvars2;
        int j = monNumb - 1; // j-номер монома разности
        int adr = j * v1; // adr=адрес j-го монома разности=j*v1

        int i1; // i1 и i2 - переменные для хранения adr1 и adr2
        int i2;
        int i;
        int resAdr, a, shift1, shift2;

        while (j1 >= cb1 && j2 >= cb2) {
            i1 = adr1 + v1 - 1; // сравним j1-й j2-й мономы:
            i2 = adr2 + v2 - 1; // запишем в i1 и i2 адреса последних переменных
            i = v1 - 1;

            for (int k = 0; k < v1 - v2; k++) {
                if (powers1[i1] != 0) {
                    break;
                }
                i1--;
                i--;
            }
            if (i > v2 - 1) {

                // построить результат:
                // остаток p1, остаток p2, уже готовая часть разности
                int[] respowers;
                Element[] rescoeffs;
                if (ce1 - j1 + ce2 - j2 - 1 == cfs.length - j) {
                    // если не было подобных слагаемых, то в качестве
                    // результата можно использовать pows и cfs. Причем в
                    // конце pows и cfs уже записана часть разности!
                    respowers = pows;
                    rescoeffs = cfs;
                } else {
                    // иначе придется создать новые массивы, т.к. pows и cfs
                    // будут больше по размеру, чем нужно (на кол-во подобных)
                    rescoeffs = new Element[j1 - cb1 + j2 - cb2 + monNumb - j
                            + 1];
                    respowers = new int[rescoeffs.length * v1];
                    // Скопировать в конец массивов результата часть разности
                    System.arraycopy(pows, adr + v1, respowers, (j1 - cb1 + j2
                            - cb2 + 2)
                            * v1, (monNumb - j - 1) * v1);
                    System.arraycopy(cfs, j + 1, rescoeffs, j1 - cb1 + j2 - cb2
                            + 2, monNumb - j - 1);
                }
                // Сопировать остаток p1
                if (fvars1 == v1) {
                    // если p1 хранится в формате v1, то использовать
                    // метод быстрого копирования
                    System.arraycopy(powers1, pb1, respowers, 0, adr1 - pb1
                            + v1);
                } else {
                    // fvars1>v1
                    resAdr = 0;
                    a = pb1;
                    shift1 = fvars1 - v1;
                    for (int k = 0; k < j1 - cb1 + 1; k++) {
                        for (int v = 0; v < v1; v++) {
                            respowers[resAdr++] = powers1[a++];
                        }
                        a += shift1;
                    }
                }
                if (p1_minus_p2) {
                    // Если не было перестановки, то нужно взять коэффициенты p1
                    // как есть
                    System.arraycopy(coeffs1, cb1, rescoeffs, 0, j1 - cb1 + 1);
                } else {
                    // иначе нужно взять коэффициенты p1 с минусом
                    for (int k = cb1; k <= j1; k++) {
                        rescoeffs[k - cb1] = coeffs1[k].negate(ring);
                    }
                }
                // Скопировать остаток p2.
                if (fvars2 == v1) {
                    // если p2 хранится в формате v1, то использовать
                    // метод быстрого копирования
                    System.arraycopy(powers2, pb2, respowers, (j1 - cb1 + 1)
                            * v1, adr2 - pb2 + v1);
                } else {
                    // v1>fvars2>v2 или fvars2>v1>v2
                    resAdr = (j1 - cb1 + 1) * v1;
                    a = pb2;
                    shift1 = v1 - v2;
                    shift2 = fvars2 - v2;
                    for (int k = 0; k < j2 - cb2 + 1; k++) {
                        for (int v = 0; v < v2; v++) {
                            respowers[resAdr++] = powers2[a++];
                        }
                        resAdr += shift1;
                        a += shift2;
                    }
                }
                if (p1_minus_p2) {
                    // Если не было перестановки, то нужно взять коэффициенты p2
                    // с минусом
                    int shift = j1 - cb1 + 1 - cb2;
                    for (int k = cb2; k <= j2; k++) {
                        rescoeffs[k + shift] = coeffs2[k].negate(ring);
                    }
                } else {
                    // иначе нужно взять коэффициенты p2 как есть
                    System.arraycopy(coeffs2, cb2, rescoeffs, j1 - cb1 + 1, j2
                            - cb2 + 1);
                }
                return new Polynom(respowers, rescoeffs);
            }

            while (i >= 0 && powers1[i1] == powers2[i2]) {
                i--;
                i1--;
                i2--;
            }
            if (i == -1) { // если степени мономов равны, то
                // вычесть их коэффициенты
                if (p1_minus_p2) {
                    cfs[j] = coeffs1[j1].subtract(coeffs2[j2], ring);
                } else {
                    cfs[j] = coeffs2[j2].subtract(coeffs1[j1], ring);
                }
                if (cfs[j].isZero(ring)) {
                    // если их сумма = 0, то увеличить номер монома разности(в
                    // конце он уменьшится),
                    // т.е. он не изменится
                    j++;
                    adr += v1;

                } else {
                    // иначе если сумма коэффициентов не равна 0, то
                    // копировать степени мономов в разность по адресу adr
                    System.arraycopy(powers1, adr1, pows, adr, v2);
                }
                j1--;
                adr1 -= fvars1;
                j2--;
                adr2 -= fvars2;

            } else if (powers1[i1] < powers2[i2]) {
                if (p1_minus_p2) {
                    cfs[j] = coeffs1[j1];
                } else {
                    cfs[j] = coeffs1[j1].negate(ring);
                }
                System.arraycopy(powers1, adr1, pows, adr, v2);
                j1--;
                adr1 -= fvars1;

            } else {
                if (p1_minus_p2) {
                    cfs[j] = coeffs2[j2].negate(ring);
                } else {
                    cfs[j] = coeffs2[j2];
                }
                System.arraycopy(powers2, adr2, pows, adr, v2);
                j2--;
                adr2 -= fvars2;
            }
            j--; // перейти к следующему моному в разности
            adr -= v1;

        }

        if (j1 >= cb1) {
            // Построить результат из остатка p1 и разности.
            // Т.к. в начале результата будет 1-й моном p1, в котором v1
            // переменная, то в результате будет v1 переменная!
            int[] respowers;
            Element[] rescoeffs;
            // Если при вычитании не было подобных, то можно использовать
            // в качестве результата pows и cfs!
            if (ce2 - j2 + ce1 - j1 - 1 == monNumb - j) {
                respowers = pows;
                rescoeffs = cfs;
            } else {
                // иначе придется создать новые массивы и копировать в конец
                // разность.
                respowers = new int[(j1 - cb1 + monNumb - j) * v1];
                rescoeffs = new Element[j1 - cb1 + monNumb - j];
                System.arraycopy(pows, adr + v1, respowers,
                        (j1 - cb1 + 1) * v1, (monNumb - j - 1) * v1);
                System.arraycopy(cfs, j + 1, rescoeffs, j1 - cb1 + 1, monNumb
                        - j - 1);
            }
            // Сопировать остаток p1
            if (fvars1 == v1) {
                // если p1 хранится в формате v1, то использовать
                // метод быстрого копирования
                System.arraycopy(powers1, pb1, respowers, 0, adr1 - pb1 + v1);
            } else {
                // fvars1>v1
                resAdr = 0;
                a = pb1;
                shift1 = fvars1 - v1;
                for (int k = 0; k < j1 - cb1 + 1; k++) {
                    for (int v = 0; v < v1; v++) {
                        respowers[resAdr++] = powers1[a++];
                    }
                    a += shift1;
                }
            }
            if (p1_minus_p2) {
                // копировать коэффициенты p1
                System.arraycopy(coeffs1, cb1, rescoeffs, 0, j1 - cb1 + 1);
            } else {
                // иначе нужно взять коэффициенты p1 с минусом
                for (int k = cb1; k <= j1; k++) {
                    rescoeffs[k - cb1] = coeffs1[k].negate(ring);
                }
            }
            return new Polynom(respowers, rescoeffs); // вернуть разность
        }

        if (j2 >= cb2) {
            // Построить результат из остатка p2 и разности. Т.к. этот
            // случай возник, то v1==v2 и 1-м мономом результата будет p2,
            // то в результате будет v1 переменная.
            int[] respowers;
            Element[] rescoeffs;
            // Если при вычитании не было подобных, то можно использовать
            // в качестве результата pows и cfs!
            if (ce2 - j2 + ce1 - j1 - 1 == monNumb - j) {
                respowers = pows;
                rescoeffs = cfs;
            } else {
                respowers = new int[(monNumb - j + j2 - cb2) * v1];
                rescoeffs = new Element[monNumb - j + j2 - cb2];
                System.arraycopy(pows, adr + v1, respowers,
                        (j2 - cb2 + 1) * v1, (monNumb - j - 1) * v1);
                System.arraycopy(cfs, j + 1, rescoeffs, j2 - cb2 + 1, monNumb
                        - j - 1);
            }
            if (fvars2 == v1) {
                // если p2 хранится в формате v1, то использовать
                // метод быстрого копирования
                System.arraycopy(powers2, pb2, respowers, 0, adr2 - pb2 + v1);
            } else {
                // fvars2>v1(=v2)
                resAdr = 0;
                a = pb2;
                shift2 = fvars2 - v2;
                for (int k = 0; k < j2 - cb2 + 1; k++) {
                    for (int v = 0; v < v2; v++) {
                        respowers[resAdr++] = powers2[a++];
                    }
                    a += shift2;
                }
            }
            if (p1_minus_p2) {
                // взять коэффициенты p2 с минусом
                for (int k = cb2; k <= j2; k++) {
                    rescoeffs[k - cb2] = coeffs2[k].negate(ring);
                }
            } else {
                System.arraycopy(coeffs2, cb2, rescoeffs, 0, j2 - cb2 + 1);
            }
            return new Polynom(respowers, rescoeffs); // вернуть разность
        }

        // j1=cb1-1 и j2=cb2-1, то проверим равна ли сумма 0
        // При этом обязательно должны быть подобные мономы, иначе
        // было бы j1>=cb1 или j2>=cb2!
        if (j == monNumb - 1) {
            // если указатель j остался равным monNumb-1, то все разности
            // коэффициентов были равны 0, то сумма полиномов равна 0
            return new Polynom(new int[0], new Element[0]);

        } else {
            // иначе были подобные, т.к. j1=cb1-1 и j2=cb2-1.
            // Скопировать массивы pows и cfs в новые
            // Получить реальное кол-во переменных в результате.
            // resVars<=v1
            int resVars = getRealVars(pows, adr + v1, v1);
            if (resVars == 0) {
                // результат=числу
                return new Polynom(new int[0], new Element[] {cfs[j + 1]});
            } else {
                // resVars>0
                int[] respowers = new int[(monNumb - j - 1) * resVars];
                if (resVars == v1) {
                    System.arraycopy(pows, adr + v1, respowers, 0,
                            respowers.length);
                } else {
                    // resVars<v1
                    resAdr = 0;
                    int powsAdr = adr + v1;
                    int shift = v1 - resVars;
                    for (i = 0; i < monNumb - j - 1; i++) {
                        for (int v = 0; v < resVars; v++) {
                            respowers[resAdr++] = pows[powsAdr++];
                        }
                        powsAdr += shift;
                    }
                }
                Element[] rescoeffs = new Element[monNumb - j - 1];
                System.arraycopy(cfs, j + 1, rescoeffs, 0, rescoeffs.length);
                return new Polynom(respowers, rescoeffs); // вернуть разность
            }
        }
    }

    /*
     * Метод mulSS стандартно и последовательно(на 1 процессоре) умножает 2
     * части полиномов this от beg1 до end1 и p от beg2 до end2.
     *
     * Параметры: p - полином типа Polynom beg1, end1 - номера начального и
     * конечного монома в this beg2, end2 - номера начального и конечного монома
     * в p v1 - число переменных в полиноме this v2 - число переменных в
     * полиноме p Если один из полиномов равен 0 (кол-во мономов=0), то
     * произведение равно 0(ZERO). Классический алгоритм умножения. Исходные
     * сомножители -- упорядоченные полиномы. Для задания порядка в
     * полиноме-произведении вводится массив index[]: index[k] -- номер
     * следующего монома после монома с номерном k. Три части. Первая часть =
     * старший моном this умножается на полином p. Вторая часть(цикл) = каждый
     * из мономов полинома this, кроме старшего монома, умножается на полином p,
     * при этом мономы-произведения располагаются в нужном порядке в
     * полиноме-произведении: Если моном соответствующей степени уже существует,
     * то происходит суммирование, если нет, то в произведение дописывается
     * новый моном в конец списка мономов, при этом массив index[]
     * корректируется так, чтобы этот моном был в нужном порядке. Третья часть =
     * мономы произведения переставляются в естественном порядке и удаляются
     * мономы с нулевыми коеффициентами.
     */
    public Polynom mulSS(Polynom p, int quant, Ring r) {

        if (coeffs.length == 0) {
            return this;
        }
        if (p.coeffs.length == 0) {
            return p;
        }
        return mulSS(powers, coeffs, 0, powers.length, 0, coeffs.length,
                p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                false, quant, r);
    }

    public Polynom multiply(Polynom p, int ind, Ring ring) {
        if (ind == 100) {
            Element thisOne = this.one(ring);
            Element one = p.one(ring);
            int this_o = this.numbElementType();
            int o = one.numbElementType();
            if (o > this_o) {
                return this.toPolynom(o, ring).mulSS(p, DEFAULT_QUANT_SIZE,
                        ring);
            }
            if (o < this_o) {
                return this.mulSS(p.toPolynom(this_o, ring),
                        DEFAULT_QUANT_SIZE, ring);
            }
        }
        return mulSS(p, DEFAULT_QUANT_SIZE, ring);
    }

    public Polynom multiply(Polynom p, Ring ring) {
        return mulSS(p, ring);
    }

    public Polynom mulSS(Polynom p, Ring ring) {
        Polynom pp = mulSS(p, DEFAULT_QUANT_SIZE, ring);
        return pp;
    }

    @Override
    public Element multiply(Element p, int ind, Ring ring) {
        if (p instanceof Polynom) {
            return multiply((Polynom) p, ind, ring);
        }
        return multiply(p, ring);
    }

    @Override
    public Element multiply(Element p, Ring ring) {
        if ((p.isZero(ring )||(isZero(ring))))return ring.numberZERO;
        if (isOne(ring)) {return p; }
        if (isMinusOne(ring)) {return p.negate(ring); }
        if(p.isItNumber(ring)){
               Element ee=(p instanceof Polynom)? ((Polynom)p).coeffs[0]:p;  
               return multiplyByNumber(ee, ring);}
        int numbElementType = p.numbElementType();
        if ((numbElementType >= Ring.Polynom)
                && (numbElementType < Ring.Rational)) {
            return mulSS((Polynom) p, DEFAULT_QUANT_SIZE, ring);
        }
        switch (numbElementType) {
            case Ring.Fname:
                return (new F(F.MULTIPLY, new Element[] {new F(this), p}));
            case Ring.F:
                return (new F(F.MULTIPLY, new Element[] {new F(this), p}));
            default:  
                return p.multiply(this, ring);
        }
    }

    // считаем аргумент числом
    // return multiplyByNumber(p);
    public Polynom multiplyDih(Element p, Ring ring) {
        return mulSS((Polynom) p, DEFAULT_QUANT_SIZE, ring);
    }

    /*
     * Метод mulSS стандартно и последовательно(на 1 процессоре) умножает 2
     * части полиномов this от beg1 до end1 и p от beg2 до end2.
     *
     * Параметры: p - полином типа Polynom beg1, end1 - номера начального и
     * конечного монома в this beg2, end2 - номера начального и конечного монома
     * в p
     */
    public Polynom mulSS(Polynom p, int beg1, int end1, int beg2, int end2,
            int quant, Ring r) {
        int fvars1 = powers.length / coeffs.length;
        int fvars2 = p.powers.length / p.coeffs.length;
        return mulSS(powers, coeffs, beg1 * fvars1, end1 * fvars1, beg1, end1,
                p.powers, p.coeffs, beg2 * fvars2, end2 * fvars2, beg2, end2,
                true, quant, r);
    }

    public Polynom mulSS(Polynom p, int beg1, int end1, int beg2, int end2,
            Ring r) {
        return mulSS(p, beg1, end1, beg2, end2, DEFAULT_QUANT_SIZE, r);

    }

    public static Polynom mulSS(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring r) {
        return mulSS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2, coeffs2,
                pb2, pe2, cb2, ce2, getRealVars, DEFAULT_QUANT_SIZE, r);
    }

    public static Polynom mulSS(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, int quant,
            Ring r) {

        if ((ce1 - cb1) * (ce2 - cb2) > quant) {
            // делить пополам каждый полином и умножить их рекурсивно
            // с помощью rmulSS
            int m1 = (ce1 - cb1) / 2;
            int m2 = (ce2 - cb2) / 2;
            int fvars1 = (pe1 - pb1) / (ce1 - cb1);
            int fvars2 = (pe2 - pb2) / (ce2 - cb2);
            Polynom p1 = mulSS(powers1, coeffs1, pb1, pb1 + m1 * fvars1, cb1,
                    cb1 + m1, powers2, coeffs2, pb2, pb2 + m2 * fvars2, cb2,
                    cb2 + m2, true, quant, r);
            Polynom p2 = mulSS(powers1, coeffs1, pb1, pb1 + m1 * fvars1, cb1,
                    cb1 + m1, powers2, coeffs2, pb2 + m2 * fvars2, pe2, cb2
                    + m2, ce2, true, quant, r);
            Polynom p3 = mulSS(powers1, coeffs1, pb1 + m1 * fvars1, pe1, cb1
                    + m1, ce1, powers2, coeffs2, pb2, pb2 + m2 * fvars2, cb2,
                    cb2 + m2, true, quant, r);
            Polynom p4 = mulSS(powers1, coeffs1, pb1 + m1 * fvars1, pe1, cb1
                    + m1, ce1, powers2, coeffs2, pb2 + m2 * fvars2, pe2, cb2
                    + m2, ce2, true, quant, r);
            Polynom res = p1.add(p2, r).add(p3, r).add(p4, r);
            return res;
        } else {
            // размеры полиномов достаточно малы, то умножить с помощью qmulSS
            return qmulSS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                    coeffs2, pb2, pe2, cb2, ce2, getRealVars, r);
        }
    }

    /*
     * Стандартно и последовательно умножает 2 полинома: 1-й записан в массивы
     * powers1 от pb1 до pe1 и coeffs1 от cb1 до ce1, 2-й записан в массивы
     * powers2 от pb2 до pe2 и coeffs2 от cb2 до ce2.
     *
     * powers1, coeffs1 - массивы, где находятся степени и коэффициенты 1-го
     * полинома pb1, pe1 - адрес начала и конца степеней 1-го полинома в powers1
     * cb1, ce1 - адрес начала и конца коэффициентов 1-го полинома в coeffs1
     * powers2, coeffs2 - массивы, где находятся степени и коэффициенты 2-го
     * полинома pb2, pe2 - адрес начала и конца степеней 2-го полинома в powers2
     * cb2, ce2 - адрес начала и конца коэффициентов 2-го полинома в coeffs2
     */
    public static Polynom qmulSS(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring ring) {
        if (ce1 == cb1 || ce2 == cb2) { // если один из полиномов равен 0, то
            return new Polynom(new int[0], new Element[0]); // возвращаем 0
        }

        int fvars1 = (pe1 - pb1) / (ce1 - cb1);
        int fvars2 = (pe2 - pb2) / (ce2 - cb2);

        // Если getVars==true, то нужно определить реальное кол-во переменных
        int v1, v2;
        if (getRealVars) {
            // вычисляем реальное кол-во переменных по 1-м мономам
            v1 = getRealVars(powers1, pb1, fvars1);
            v2 = getRealVars(powers2, pb2, fvars2);
        } else {
            v1 = fvars1;
            v2 = fvars2;
        }

        if (v1 < v2) {
            int[] t1 = powers1;
            powers1 = powers2;
            powers2 = t1;

            Element[] t2 = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = t2;

            int t3 = pb1;
            pb1 = pb2;
            pb2 = t3;

            t3 = pe1;
            pe1 = pe2;
            pe2 = t3;

            t3 = cb1;
            cb1 = cb2;
            cb2 = t3;

            t3 = ce1;
            ce1 = ce2;
            ce2 = t3;

            t3 = v1;
            v1 = v2;
            v2 = t3;

            t3 = fvars1;
            fvars1 = fvars2;
            fvars2 = t3;
        }

        if (ce1 - cb1 == 1) {
            // если число мономов в p1 =1, то
            // умножить cb1-й моном p1 на все мономы p2
            // p1 - 1 моном (v1>0), 1 моном C (v1=0)
            // p2 - полином (v2>0, v2<v1), моном C (v2=0)
            int[] pows = new int[(ce2 - cb2) * v1];
            Element[] cfs = new Element[ce2 - cb2];
            int adr = 0;
            int adr2 = pb2;
            int shift2 = fvars2 - v2;
            for (int i = cb2; i < ce2; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers2[adr2++] + powers1[pb1 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[pb1 + v];
                }
                adr2 += shift2;
                cfs[i - cb2] = coeffs2[i].multiply(coeffs1[cb1], ring);
            }
            return new Polynom(pows, cfs);

        } else if (ce2 - cb2 == 1) {
            // если число мономов в p2 =1, то
            // умножить моном p2 на все мономы p1
            // p1 - полином, v1>0, v1>v2
            int[] pows = new int[(ce1 - cb1) * v1];
            Element[] cfs = new Element[ce1 - cb1];
            int adr = 0;
            int adr1 = pb1;
            int shift1 = fvars1 - v1;
            for (int i = cb1; i < ce1; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers1[adr1++] + powers2[pb2 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[adr1++];
                }
                adr1 += shift1;
                cfs[i - cb1] = (coeffs1[i]).multiply(coeffs2[cb2], ring);
            }
            return new Polynom(pows, cfs);

        }

        // monNumb=максимальное количество мономов в произведении
        int monNumb = (ce1 - cb1) * (ce2 - cb2);
        // Массив степеней и массив коэффициентов для произведения
        int[] resultPowers = new int[monNumb * v1];
        Element[] resultCoeffs = new Element[monNumb];
        // массив для хранения степеней одного монома
        int[] pow = new int[v1];
        // коэффициент этого монома
        Element c;

        int[] index = new int[monNumb]; // массив индексов
        int resAdr = 0; // адрес степени монома в произведении
        int adr1 = pb1;
        int adr2 = pb2;
        int shift2 = fvars2 - v2;
        // умножаем 0-й моном p1 на все мономы p2
        for (int i = 0; i < ce2 - cb2; i++) {
            for (int v = 0; v < v2; v++) {
                resultPowers[resAdr++] = powers1[adr1 + v] + powers2[adr2++];
            }
            for (int v = v2; v < v1; v++) {
                resultPowers[resAdr++] = powers1[adr1 + v];
            }
            adr2 += shift2;
            resultCoeffs[i] = coeffs1[cb1].multiply(coeffs2[i + cb2], ring);
            index[i] = i + 1; // указатель на следующий моном
        }
        index[ce2 - cb2 - 1] = 0; // индекс последнего монома=0
        int first = 0; // индекс, с которого начинается поиск места для
        // произведения
        // следующего монома p1 на все мономы p2
        int free = ce2 - cb2; // индекс свободного монома в resultPowers
        adr1 += fvars1; // адрес (i1-cb1)-го монома в p1

        for (int i1 = cb1 + 1; i1 < ce1; i1++) { // (i1-cb1)-номер монома в p1
            int wLast = first; // индекс, с которого начинается поиск места для
            // произведения (i1-cb1)-го монома p1 на (i2-cb2)-й моном p2
            adr2 = pb2; // адрес (i2-cb2)-го монома в p2
            for (int i2 = cb2; i2 < ce2; i2++) {

                // умножаем (i1-cb1)-й моном p1 на
                // (i2-cb2)-й моном p2 и запишем произведение в pows и c
                for (int v = 0; v < v2; v++) {
                    pow[v] = powers1[adr1 + v] + powers2[adr2++];
                }
                for (int v = v2; v < v1; v++) {
                    pow[v] = powers1[adr1 + v];
                }
                c = coeffs1[i1].multiply(coeffs2[i2], ring);
                adr2 += shift2;

                // найдем место для вставки произведения мономов
                // начиная с индекса после wLast
                int w = index[wLast];
                while (w > 0) {
                    // повторять пока не конец списка и пока не
                    // нашли место для pows
                    int powersIndx = w * v1 + v1 - 1;
                    int i = v1 - 1;
                    while (i >= 0 && pow[i] == resultPowers[powersIndx]) {
                        i--;
                        powersIndx--;
                    }

                    if (i == -1) { // если степени pows равны степеням powers[w],
                        // то
                        resultCoeffs[w] = resultCoeffs[w].add(c, ring); // сложить
                        // коэффициенты
                        wLast = w; // с этого индекса будет следующий поиск
                        break; // и выход из цикла
                    } else if (pow[i] < resultPowers[powersIndx]) { // если
                        // степени
                        // pows < resultPowers,то
                        wLast = w; // сохраним этот индекс в wLast
                        w = index[w]; // w=индекс следующего монома
                    } else { // иначе если степени pows > степеней resultPowers,
                        // то
                        resultCoeffs[free] = c; // записать моном pows в конец
                        // resultPowers
                        for (int v = 0; v < v1; v++) {
                            resultPowers[resAdr++] = pow[v];
                        }
                        index[free] = w; // вставить индекс этого монома free
                        // между wLast и w
                        index[wLast] = free;
                        wLast = free; // с этого индекса free будет следующий
                        // поиск
                        free++;
                        break; // и выход из цикла
                    }
                }
                if (w == 0) { // если дошли до конца и степени pows < всех
                    // степеней
                    // resultPowers, то вставить pows в конец списка
                    resultCoeffs[free] = c;
                    for (int v = 0; v < v1; v++) {
                        resultPowers[resAdr++] = pow[v];
                    }
                    index[free] = 0;
                    index[wLast] = free;
                    wLast = free;
                    free++;
                }
                if (i2 == cb2) { // индекс для произведения мономов
                    // (i1+1-cb1)-го и 0-го
                    first = wLast; // будем искать после индекса произведения
                    // (i1-cb1)-го и 0-го
                }
            }
            adr1 += fvars1; // перейти к следующему моному в p1
        }

        int[] powers0 = new int[resAdr]; // Проидем по массиву index и запишем
        Element[] coeffs0 = new Element[free]; // все ненулевые мономы в массивы
        // powers0 и coeffs0
        int w = 0; // w=индекс в массиве index
        int j0 = 0; // номер места для ненулевого коэффициента в coeffs0
        int adr0 = 0; // адрес места для ненулевого моннома в powers0; adr0=j*v1

        for (int i = 0; i < free; i++) {
            if (resultCoeffs[w].signum() != 0) {
                // если коэффициент !=0, то запишем коэффициент по адресу j
                coeffs0[j0++] = resultCoeffs[w];
                resAdr = w * v1; // адрес монома в resultPowers
                for (int v = 0; v < v1; v++) { // а моном по адресу adr0
                    powers0[adr0++] = resultPowers[resAdr++];
                }
            }
            w = index[w]; // w=следующий индекс
        }
        if (j0 == free) {
            return new Polynom(powers0, coeffs0);
        } else {
            int[] newpowers = new int[adr0]; // скопируем powers0 и coeffs0 в
            System.arraycopy(powers0, 0, newpowers, 0, adr0); // новые массивы
            Element[] newcoeffs = new Element[j0];
            System.arraycopy(coeffs0, 0, newcoeffs, 0, j0);
            return new Polynom(newpowers, newcoeffs);
        }
    }

    /*
     * Метод mulSS стандартно и последовательно(на 1 процессоре) умножает 2
     * части полиномов this от beg1 до end1 и p от beg2 до end2 с использованием
     * умножения чисел по алгоритму Карацубы.
     *
     * Параметры: p - полином типа Polynom beg1, end1 - номера начального и
     * конечного монома в this beg2, end2 - номера начального и конечного монома
     * в p v1 - число переменных в полиноме this v2 - число переменных в
     * полиноме p Если один из полиномов равен 0 (кол-во мономов=0), то
     * произведение равно 0(ZERO). Классический алгоритм умножения. исходные
     * сомножители -- упорядоченные полиномы. Для задания порядка в
     * полиноме-произведении вводится массив index[]: index[k] -- номер
     * следующего монома после монома с номерном k. Три части. Первая часть =
     * старший моном this умножается на полином p. Вторая часть(цикл) = каждый
     * из мономов полинома this, кроме старшего монома, умножается на полином p,
     * при этом мономы-произведения располагаются в нужном порядке в
     * полиноме-произведении: Если моном соответствующей степени уже существует,
     * то происходит суммирование, если нет, то в произведение дописывается
     * новый моном в конец списка мономов, при этом массив index[]
     * корректируется так, чтобы этот моном был в нужном порядке. Третья часть =
     * мономы произведения переставляются в естественном порядке и удаляются
     * мономы с нулевыми коеффициентами.
     */
    public Polynom mulSSK(Polynom p, int quant, Ring ring) {

        if (coeffs.length == 0 || p.coeffs.length == 0) {
            return new Polynom(new int[0], new Element[0]);
        }

        return mulSSK(powers, coeffs, 0, powers.length, 0, coeffs.length,
                p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                false, quant, ring);

    }

    public Polynom mulSSK(Polynom p, Ring ring) {
        return mulSSK(p, DEFAULT_QUANT_SIZE, ring);

    }

    /*
     * Метод mulSS стандартно и последовательно(на 1 процессоре) умножает 2
     * части полиномов this от beg1 до end1 и p от beg2 до end2 с использованием
     * умножения чисел по алгоритму Карацубы.
     *
     * Параметры: p - полином типа Polynom beg1, end1 - номера начального и
     * конечного монома в this beg2, end2 - номера начального и конечного монома
     * в p
     */
    public Polynom mulSSK(Polynom p, int beg1, int end1, int beg2, int end2,
            int quant, Ring ring) {
        int fvars1 = powers.length / coeffs.length;
        int fvars2 = p.powers.length / p.coeffs.length;
        return mulSSK(powers, coeffs, beg1 * fvars1, end1 * fvars1, beg1, end1,
                p.powers, p.coeffs, beg2 * fvars2, end2 * fvars2, beg2, end2,
                true, quant, ring);
    }

    public Polynom mulSSK(Polynom p, int beg1, int end1, int beg2, int end2,
            Ring ring) {
        return mulSSK(p, beg1, end1, beg2, end2, DEFAULT_QUANT_SIZE, ring);

    }

    public static Polynom mulSSK(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring ring) {
        return mulSSK(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2, coeffs2,
                pb2, pe2, cb2, ce2, getRealVars, DEFAULT_QUANT_SIZE, ring);
    }

    public static Polynom mulSSK(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, int quant,
            Ring r) {

        if ((ce1 - cb1) * (ce2 - cb2) > quant) {
            // делить пополам каждый полином и умножить их рекурсивно
            // с помощью rmulSS
            int m1 = (ce1 - cb1) / 2;
            int m2 = (ce2 - cb2) / 2;
            int fvars1 = (pe1 - pb1) / (ce1 - cb1);
            int fvars2 = (pe2 - pb2) / (ce2 - cb2);
            Polynom p1 = mulSSK(powers1, coeffs1, pb1, pb1 + m1 * fvars1, cb1,
                    cb1 + m1, powers2, coeffs2, pb2, pb2 + m2 * fvars2, cb2,
                    cb2 + m2, true, quant, r);
            Polynom p2 = mulSSK(powers1, coeffs1, pb1, pb1 + m1 * fvars1, cb1,
                    cb1 + m1, powers2, coeffs2, pb2 + m2 * fvars2, pe2, cb2
                    + m2, ce2, true, quant, r);
            Polynom p3 = mulSSK(powers1, coeffs1, pb1 + m1 * fvars1, pe1, cb1
                    + m1, ce1, powers2, coeffs2, pb2, pb2 + m2 * fvars2, cb2,
                    cb2 + m2, true, quant, r);
            Polynom p4 = mulSSK(powers1, coeffs1, pb1 + m1 * fvars1, pe1, cb1
                    + m1, ce1, powers2, coeffs2, pb2 + m2 * fvars2, pe2, cb2
                    + m2, ce2, true, quant, r);
            Polynom res = p1.add(p2, r).add(p3, r).add(p4, r);
            return res;
        } else {
            // размеры полиномов достаточно малы, то умножить с помощью qmulSSK
            return qmulSSK(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                    coeffs2, pb2, pe2, cb2, ce2, getRealVars, r);
        }
    }

    /*
     * Стандартно и последовательно умножает 2 полинома с использованием
     * умножения чисел по алгоритму Карацубы: 1-й записан в массивы powers1 от
     * pb1 до pe1 и coeffs1 от cb1 до ce1, 2-й записан в массивы powers2 от pb2
     * до pe2 и coeffs2 от cb2 до ce2.
     *
     * powers1, coeffs1 - массивы, где находятся степени и коэффициенты 1-го
     * полинома pb1, pe1 - адрес начала и конца степеней 1-го полинома в powers1
     * cb1, ce1 - адрес начала и конца коэффициентов 1-го полинома в coeffs1
     * powers2, coeffs2 - массивы, где находятся степени и коэффициенты 2-го
     * полинома pb2, pe2 - адрес начала и конца степеней 2-го полинома в powers2
     * cb2, ce2 - адрес начала и конца коэффициентов 2-го полинома в coeffs2
     */
    public static Polynom qmulSSK(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring ring) {
        if (ce1 == cb1 || ce2 == cb2) { // если один из полиномов равен 0, то
            return new Polynom(new int[0], new Element[0]); // возвращаем 0
        }

        int fvars1 = (pe1 - pb1) / (ce1 - cb1);
        int fvars2 = (pe2 - pb2) / (ce2 - cb2);

        // Если getVars==true, то нужно определить реальное кол-во переменных
        int v1, v2;
        if (getRealVars) {
            // вычисляем реальное кол-во переменных по 1-м мономам
            v1 = getRealVars(powers1, pb1, fvars1);
            v2 = getRealVars(powers2, pb2, fvars2);
        } else {
            v1 = fvars1;
            v2 = fvars2;
        }

        if (v1 < v2) {
            int[] t1 = powers1;
            powers1 = powers2;
            powers2 = t1;

            Element[] t2 = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = t2;

            int t3 = pb1;
            pb1 = pb2;
            pb2 = t3;

            t3 = pe1;
            pe1 = pe2;
            pe2 = t3;

            t3 = cb1;
            cb1 = cb2;
            cb2 = t3;

            t3 = ce1;
            ce1 = ce2;
            ce2 = t3;

            t3 = v1;
            v1 = v2;
            v2 = t3;

            t3 = fvars1;
            fvars1 = fvars2;
            fvars2 = t3;
        }

        if (ce1 - cb1 == 1) {
            // если число мономов в p1 =1, то
            // умножить cb1-й моном p1 на все мономы p2
            // p1 - 1 моном (v1>0), 1 моном C (v1=0)
            // p2 - полином (v2>0, v2<v1), моном C (v2=0)
            int[] pows = new int[(ce2 - cb2) * v1];
            Element[] cfs = new Element[ce2 - cb2];
            int adr = 0;
            int adr2 = pb2;
            int shift2 = fvars2 - v2;
            for (int i = cb2; i < ce2; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers2[adr2++] + powers1[pb1 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[pb1 + v];
                }
                adr2 += shift2;
                cfs[i - cb2] = coeffs2[i].multiply(coeffs1[cb1], 1, ring);
            }
            return new Polynom(pows, cfs);

        } else if (ce2 - cb2 == 1) {
            // если число мономов в p2 =1, то
            // умножить моном p2 на все мономы p1
            // p1 - полином, v1>0, v1>v2
            int[] pows = new int[(ce1 - cb1) * v1];
            Element[] cfs = new Element[ce1 - cb1];
            int adr = 0;
            int adr1 = pb1;
            int shift1 = fvars1 - v1;
            for (int i = cb1; i < ce1; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers1[adr1++] + powers2[pb2 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[adr1++];
                }
                adr1 += shift1;
                cfs[i - cb1] = coeffs1[i].multiply(coeffs2[cb2], 1, ring);
            }
            return new Polynom(pows, cfs);

        }

        // monNumb=максимальное количество мономов в произведении
        int monNumb = (ce1 - cb1) * (ce2 - cb2);
        // Массив степеней и массив коэффициентов для произведения
        int[] resultPowers = new int[monNumb * v1];
        Element[] resultCoeffs = new Element[monNumb];
        // массив для хранения степеней одного монома
        int[] pow = new int[v1];
        // коэффициент этого монома
        Element c;

        int[] index = new int[monNumb]; // массив индексов
        int resAdr = 0; // адрес степени монома в произведении
        int adr1 = pb1;
        int adr2 = pb2;
        int shift2 = fvars2 - v2;
        // умножаем 0-й моном p1 на все мономы p2
        for (int i = 0; i < ce2 - cb2; i++) {
            for (int v = 0; v < v2; v++) {
                resultPowers[resAdr++] = powers1[adr1 + v] + powers2[adr2++];
            }
            for (int v = v2; v < v1; v++) {
                resultPowers[resAdr++] = powers1[adr1 + v];
            }
            adr2 += shift2;
            resultCoeffs[i] = coeffs1[cb1].multiply(coeffs2[i + cb2], 1, ring);
            index[i] = i + 1; // указатель на следующий моном
        }
        index[ce2 - cb2 - 1] = 0; // индекс последнего монома=0
        int first = 0; // индекс, с которого начинается поиск места для
        // произведения
        // следующего монома p1 на все мономы p2
        int free = ce2 - cb2; // индекс свободного монома в resultPowers
        adr1 += fvars1; // адрес (i1-cb1)-го монома в p1

        for (int i1 = cb1 + 1; i1 < ce1; i1++) { // (i1-cb1)-номер монома в p1
            int wLast = first; // индекс, с которого начинается поиск места для
            // произведения (i1-cb1)-го монома p1 на (i2-cb2)-й моном p2
            adr2 = pb2; // адрес (i2-cb2)-го монома в p2
            for (int i2 = cb2; i2 < ce2; i2++) {

                // умножаем (i1-cb1)-й моном p1 на
                // (i2-cb2)-й моном p2 и запишем произведение в pows и c
                for (int v = 0; v < v2; v++) {
                    pow[v] = powers1[adr1 + v] + powers2[adr2++];
                }
                for (int v = v2; v < v1; v++) {
                    pow[v] = powers1[adr1 + v];
                }
                c = coeffs1[i1].multiply(coeffs2[i2], 1, ring);
                adr2 += shift2;

                // найдем место для вставки произведения мономов
                // начиная с индекса после wLast
                int w = index[wLast];
                while (w > 0) {
                    // повторять пока не конец списка и пока не
                    // нашли место для pows
                    int powersIndx = w * v1 + v1 - 1;
                    int i = v1 - 1;
                    while (i >= 0 && pow[i] == resultPowers[powersIndx]) {
                        i--;
                        powersIndx--;
                    }

                    if (i == -1) { // если степени pows равны степеням powers[w],
                        // то
                        resultCoeffs[w] = resultCoeffs[w].add(c, ring); // сложить
                        // коэффициенты
                        wLast = w; // с этого индекса будет следующий поиск
                        break; // и выход из цикла
                    } else if (pow[i] < resultPowers[powersIndx]) { // если
                        // степени
                        // pows < resultPowers,то
                        wLast = w; // сохраним этот индекс в wLast
                        w = index[w]; // w=индекс следующего монома
                    } else { // иначе если степени pows > степеней resultPowers,
                        // то
                        resultCoeffs[free] = c; // записать моном pows в конец
                        // resultPowers
                        for (int v = 0; v < v1; v++) {
                            resultPowers[resAdr++] = pow[v];
                        }
                        index[free] = w; // вставить индекс этого монома free
                        // между wLast и w
                        index[wLast] = free;
                        wLast = free; // с этого индекса free будет следующий
                        // поиск
                        free++;
                        break; // и выход из цикла
                    }
                }
                if (w == 0) { // если дошли до конца и степени pows < всех
                    // степеней
                    // resultPowers, то вставить pows в конец списка
                    resultCoeffs[free] = c;
                    for (int v = 0; v < v1; v++) {
                        resultPowers[resAdr++] = pow[v];
                    }
                    index[free] = 0;
                    index[wLast] = free;
                    wLast = free;
                    free++;
                }
                if (i2 == cb2) { // индекс для произведения мономов
                    // (i1+1-cb1)-го и 0-го
                    first = wLast; // будем искать после индекса произведения
                    // (i1-cb1)-го и 0-го
                }
            }
            adr1 += fvars1; // перейти к следующему моному в p1
        }

        int[] powers0 = new int[resAdr]; // Проидем по массиву index и запишем
        Element[] coeffs0 = new Element[free]; // все ненулевые мономы в массивы
        // powers0 и coeffs0
        int w = 0; // w=индекс в массиве index
        int j0 = 0; // номер места для ненулевого коэффициента в coeffs0
        int adr0 = 0; // адрес места для ненулевого моннома в powers0; adr0=j*v1
        resAdr = 0; // adr-адрес монома в resultPowers; resAdr=w*v1
        for (int i = 0; i < free; i++) {
            if (resultCoeffs[w].signum() != 0) { // если коэффициент !=0, то
                coeffs0[j0++] = resultCoeffs[w]; // запишем коэффициент по
                // адресу j
                resAdr = w * v1;
                for (int v = 0; v < v1; v++) { // а моном по адресу adr0
                    powers0[adr0++] = resultPowers[resAdr++];
                }
            }
            w = index[w]; // w=следующий индекс
        }
        if (j0 == free) {
            return new Polynom(powers0, coeffs0);
        } else {
            int[] newpowers = new int[adr0]; // скопируем powers0 и coeffs0 в
            System.arraycopy(powers0, 0, newpowers, 0, adr0); // новые массивы
            Element[] newcoeffs = new Element[j0];
            System.arraycopy(coeffs0, 0, newcoeffs, 0, j0);
            return new Polynom(newpowers, newcoeffs);
        }
    }

    /*
     * Метод mulSS стандартно и последовательно(на 1 процессоре) умножает 2
     * части полиномов this с кол-вом переменных v1 от beg1 до end1 и p с
     * кол-вом переменных v2 от beg2 до end2. Кол-во переменных в форматах, в
     * которых хранятся p1 и p2 должно быть одинаковым и оно может быть больше
     * v1 и v2. Обязательно должно быть v1>v2.
     *
     * Параметры: p - полином типа Polynom beg1, end1 - номера начального и
     * конечного монома в this beg2, end2 - номера начального и конечного монома
     * в p v1 - число переменных в полиноме this (v1>v2) v2 - число переменных в
     * полиноме p (v1>v2)
     */
    private Polynom mulSS(Polynom p, int beg1, int end1, int beg2, int end2,
            int v1, int v2, Ring r) {
        int monNumb = (end1 - beg1) * (end2 - beg2); // monNumb=максимальное
        // количество мономов
        // в произведении
        if (monNumb == 0) { // если один из полиномов равен 0, то
            return new Polynom(new int[0], new Element[0]); // возвращаем 0
        }
        int vv = powers.length / coeffs.length;

        if (end1 - beg1 == 1) { // если число мономов в this =1, то
            // умножить моном this на все мономы p
            int[] pows = new int[(end2 - beg2) * vv];
            Element[] cfs = new Element[end2 - beg2];
            int adr = 0;
            int adr1 = beg1 * vv;
            int adr2 = beg2 * vv;
            for (int i = beg2; i < end2; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = p.powers[adr2++] + powers[adr1 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers[adr1 + v];
                }
                adr2 += vv - v2;
                adr += vv - v1;
                cfs[i - beg2] = p.coeffs[i].multiply(coeffs[beg1], r);
            }
            return new Polynom(pows, cfs);

        } else if (end2 - beg2 == 1) { // если число мономов в p =1, то
            // умножить моном p на все мономы this
            int[] pows = new int[(end1 - beg1) * vv];
            Element[] cfs = new Element[end1 - beg1];
            int adr = 0;
            int adr1 = beg1 * vv;
            int adr2 = beg2 * vv;
            for (int i = beg1; i < end1; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers[adr1++] + p.powers[adr2 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers[adr1++];
                }
                adr1 += vv - v1;
                adr += vv - v1;
                cfs[i - beg1] = coeffs[i].multiply(p.coeffs[beg2], r);
            }
            return new Polynom(pows, cfs);
        }

        int[] resultPowers = new int[monNumb * vv]; // создадим массив степеней
        Element[] resultCoeffs = new Element[monNumb]; // и массив коэффициентов
        // для произведения

        int[] pow = new int[vv]; // массив для хранения степеней одного монома
        Element c; // коэффициент этого монома
        int[] index = new int[monNumb]; // массив индексов
        int resAdr = 0; // адрес степени монома в произведении
        int adr1 = beg1 * vv;
        int adr2 = beg2 * vv;
        for (int i = 0; i < end2 - beg2; i++) { // умножаем 0-й моном this на
            // все мономы p
            for (int v = 0; v < v2; v++) {
                resultPowers[resAdr++] = powers[adr1 + v] + p.powers[adr2++];
            }
            for (int v = v2; v < v1; v++) {
                resultPowers[resAdr++] = powers[adr1 + v];
            }
            resAdr += vv - v1;
            adr2 += vv - v2;
            resultCoeffs[i] = coeffs[beg1].multiply(p.coeffs[i + beg2], r);
            index[i] = i + 1; // указатель на следующий моном
        }
        index[end2 - beg2 - 1] = 0; // индекс последнего монома=0
        int first = 0; // индекс, с которого начинается поиск места для
        // произведения
        // следующего монома this на все мономы p
        int free = end2 - beg2; // индекс свободного монома в resultPowers
        adr1 = (beg1 + 1) * vv; // adr1=адрес i1-го монома в this; adr1=i1*vars
        for (int i1 = beg1 + 1; i1 < end1; i1++) { // i1-номер монома в this
            int wLast = first; // индекс с которого начинается поиск места для
            // произведения i1-го монома this на i2-й моном p
            adr2 = beg2 * vv; // adr2=адрес i2-го монома в p; adr2=i2*vars
            for (int i2 = beg2; i2 < end2; i2++) { // умножаем i1-й моном this
                // на
                // i2-й моном p и запишем произведение
                // в pows и c
                for (int v = 0; v < v2; v++) {
                    pow[v] = powers[adr1 + v] + p.powers[adr2++];
                }
                for (int v = v2; v < v1; v++) {
                    pow[v] = powers[adr1 + v];
                }
                adr2 += vv - v2;
                c = coeffs[i1].multiply(p.coeffs[i2], r);
                int w = index[wLast]; // найдем место для вставки произведения
                // мономов
                // начиная с индекса после wLast
                while (w > 0) { // повторять пока не конец списка и пока не
                    // нашли место для pows
                    // сравним степени pows с resultPowers[w]
                    int powersIndx = w * vv + v1 - 1; // powIndx и powersIndx -
                    // адреса степеней
                    int i = v1 - 1;
                    while (i >= 0 && pow[i] == resultPowers[powersIndx]) {
                        i--;
                        powersIndx--;
                    }
                    if (i == -1) { // если степени pows равны степеням powers[w],
                        // то
                        resultCoeffs[w] = resultCoeffs[w].add(c, r); // сложить
                        // коэффициенты
                        wLast = w; // с этого индекса будет следующий поиск
                        break; // и выход из цикла
                    } else if (pow[i] < resultPowers[powersIndx]) { // если
                        // степени
                        // pows < resultPowers,то
                        wLast = w; // сохраним этот индекс в wLast
                        w = index[w]; // w=индекс следующего монома
                    } else { // иначе если степени pows > степеней resultPowers,
                        // то
                        resultCoeffs[free] = c; // записать моном pows в конец
                        // resultPowers
                        for (int v = 0; v < v1; v++) {
                            resultPowers[resAdr++] = pow[v];
                        }
                        resAdr += vv - v1;
                        index[free] = w; // вставить индекс этого монома free
                        // между wLast и w
                        index[wLast] = free;
                        wLast = free; // с этого индекса free будет следующий
                        // поиск
                        free++;
                        break; // и выход из цикла
                    }
                }
                if (w == 0) { // если дошли до конца и степени pows < всех
                    // степеней
                    // resultPowers, то вставить pows в конец списка
                    resultCoeffs[free] = c;
                    for (int v = 0; v < v1; v++) {
                        resultPowers[resAdr++] = pow[v];
                    }
                    resAdr += vv - v1;
                    index[free] = 0;
                    index[wLast] = free;
                    wLast = free;
                    free++;
                }
                if (i2 == beg2) { // индекс для произведения мономов (i1+1)-го и
                    // beg2-го
                    first = wLast; // будем искать после индекса произведения
                    // i1-го и beg2-го
                }
            }
            adr1 += vv; // перейти к следующему моному в p1
        }
        int[] powers0 = new int[resAdr]; // Проидем по массиву index и запишем
        Element[] coeffs0 = new Element[free]; // все ненулевые мономы в массивы
        // powers0 и coeffs0
        int w = 0; // w=индекс в массиве index
        int j = 0; // номер места для ненулевого коэффициента в coeffs0
        int adr0 = 0; // адрес места для ненулевого моннома в powers0;
        // adr0=j*vars
        resAdr = 0; // adr-адрес монома в powers; resAdr=w*vv
        for (int i = 0; i < free; i++) {
            if (resultCoeffs[w].signum() != 0) { // если коэффициент !=0, то
                coeffs0[j++] = resultCoeffs[w]; // запишем коэффициент по адресу
                // j
                resAdr = w * vv;
                for (int v = 0; v < vv; v++) { // а моном по адресу adr0
                    powers0[adr0++] = resultPowers[resAdr++];
                }
            }
            w = index[w]; // w=следующий индекс
        }
        if (j == free) {
            return new Polynom(powers0, coeffs0);
        } else {
            int[] newpowers = new int[adr0]; // скопируем powers0 и coeffs0 в
            System.arraycopy(powers0, 0, newpowers, 0, adr0); // новые массивы
            Element[] newcoeffs = new Element[j];
            System.arraycopy(coeffs0, 0, newcoeffs, 0, j);
            return new Polynom(newpowers, newcoeffs);
        }
    }

    public Polynom mulKS(Polynom p, Ring ring) {
        if (coeffs.length == 0 || p.coeffs.length == 0) { // Если p=0 или
            // this=0, то
            return new Polynom(new int[0], new Element[0]); // результат=0
        }
        int v1 = powers.length / coeffs.length;
        int v2 = p.powers.length / p.coeffs.length;
        if (v1 == 0 && v2 == 0) {
            return new Polynom(powers, new Element[] {coeffs[0].multiply(
                p.coeffs[0], ring)});
        }
        return mulKS(powers, coeffs, 0, powers.length, 0, coeffs.length,
                p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                false, ring);
    }

    /*
     * Последовательно умножает 2 полинома по методу Карацубы. Умножает полиномы
     * this и p по методу Карацубы с помощью индексов, что экономит память и
     * увеличивает скорость, т.к. не делаются лишние копирования. 1-й записан в
     * массивы powers1 от pb1 до pe1 и coeffs1 от cb1 до ce1, 2-й записан в
     * массивы powers2 от pb2 до pe2 и coeffs2 от cb2 до ce2.
     *
     * powers1, coeffs1 - массивы, где находятся степени и коэффициенты 1-го
     * полинома pb1, pe1 - адрес начала и конца степеней 1-го полинома в powers1
     * cb1, ce1 - адрес начала и конца коэффициентов 1-го полинома в coeffs1
     * powers2, coeffs2 - массивы, где находятся степени и коэффициенты 2-го
     * полинома pb2, pe2 - адрес начала и конца степеней 2-го полинома в powers2
     * cb2, ce2 - адрес начала и конца коэффициентов 2-го полинома в coeffs2
     * Алгоритм: p1=this, p2=p 1) Если p1=0 или p2=0, то p1*p2 = 0 2) Если
     * количество мономов в p1 (или в p2) =1, то умножить моном p1 (p2) на все
     * мономы p2 (p1). 3) Если количество мономов в p1 и p2 >1, то найдем номер
     * самой старшей переменной из ring, входящей в p1 (пусть это будет v1) и
     * номер самой старшей переменной из ring, входящей в p2 (v2). 4) Если v1>v2
     * (если v1<v2, то поменять p1 и p2 местами), то разделить p1 на части:
     * p1=f1*v1^k1 + f2*v1^k2 + ... + fn*v1^kn так, что fi не содержат v1,
     * тогда: результат = p1*p2 = (f1*p2)*v1^k1 + ... + (fn*p2)*v1^kn 5) Если
     * v1=v2=v, то определим такое число вида 2^k, что 2^k > максимальной
     * степени при v в p1 и p2 и найдем p=2^{k-1} 6) Представим p1 и p2 в виде:
     * p1=p11*v^p + p12 p2=p21*v^p + p22, причем среди полиномов p11 и p12 (p21
     * и p22) не больше одного нулевого и p11 и p21 не могут одновременно быть
     * равными 0. 7) Пусть p11<>0, p12<>0, p21<>0, p22<>0, то результат = p1*p2
     * = (p11*p21)*v^2p + ((p11+p12)(p21+p22) - p11*p21 - - p12*p22)*v^p +
     * p12*p22 8) Пусть p11<>0, p12=0, p21<>0, p22<>0, то результат = p1*p2 =
     * (p11*p21)*v^2p + (p11*p22)*v^p Аналогично рассматриваются другие случаи.
     * **
     * ************************************************************************
     */
    public static Polynom mulKS(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring ring) {

        if (ce1 == cb1 || ce2 == cb2) { // если один из полиномов равен 0, то
            return new Polynom(new int[0], new Element[0]); // возвращаем 0
        }

        int fvars1 = (pe1 - pb1) / (ce1 - cb1);
        int fvars2 = (pe2 - pb2) / (ce2 - cb2);

        // Если getRealVars==true, то нужно определить реальное кол-во
        // переменных
        int v1, v2;
        if (getRealVars) {
            // вычисляем реальное кол-во переменных по 1-м мономам
            v1 = getRealVars(powers1, pb1, fvars1);
            v2 = getRealVars(powers2, pb2, fvars2);
        } else {
            v1 = fvars1;
            v2 = fvars2;
        }

        if (v1 < v2) {
            int[] t1 = powers1;
            powers1 = powers2;
            powers2 = t1;

            Element[] t2 = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = t2;

            int t3 = pb1;
            pb1 = pb2;
            pb2 = t3;

            t3 = pe1;
            pe1 = pe2;
            pe2 = t3;

            t3 = cb1;
            cb1 = cb2;
            cb2 = t3;

            t3 = ce1;
            ce1 = ce2;
            ce2 = t3;

            t3 = v1;
            v1 = v2;
            v2 = t3;

            t3 = fvars1;
            fvars1 = fvars2;
            fvars2 = t3;
        }

        // v1>=v2
        if (ce1 - cb1 == 1) {
            // если число мономов в p1 =1, то
            // умножить cb1-й моном p1 на все мономы p2
            // p1 - 1 моном (v1>0), 1 моном C (v1=0)
            // p2 - полином (v2>0, v2<=v1), моном C (v2=0)
            int[] pows = new int[(ce2 - cb2) * v1];
            Element[] cfs = new Element[ce2 - cb2];
            int adr = 0;
            int adr2 = pb2;
            int shift2 = fvars2 - v2;
            for (int i = cb2; i < ce2; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers2[adr2++] + powers1[pb1 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[pb1 + v];
                }
                adr2 += shift2;
                cfs[i - cb2] = coeffs2[i].multiply(coeffs1[cb1], ring);
            }

            if (CHECK_MULKS == 1) {
                try {
                    check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                            coeffs2, pb2, pe2, cb2, ce2, getRealVars,
                            new Polynom(pows, cfs), ring);
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            }

            return new Polynom(pows, cfs);

        } else if (ce2 - cb2 == 1) {
            // если число мономов в p2 =1, v2>=0, то
            // умножить моном p2 на все мономы p1
            // p1 - полином, т.к. ce1-cb1!=1 => ce1-cb1>1 => v1>0, v1>=v2
            int[] pows = new int[(ce1 - cb1) * v1];
            Element[] cfs = new Element[ce1 - cb1];
            int adr = 0;
            int adr1 = pb1;
            int shift1 = fvars1 - v1;
            for (int i = cb1; i < ce1; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers1[adr1++] + powers2[pb2 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[adr1++];
                }
                adr1 += shift1;
                cfs[i - cb1] = coeffs1[i].multiply(coeffs2[cb2], ring);
            }

            if (CHECK_MULKS == 1) {
                try {
                    check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                            coeffs2, pb2, pe2, cb2, ce2, getRealVars,
                            new Polynom(pows, cfs), ring);
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            }
            return new Polynom(pows, cfs);
        }
        if (v1 > v2) {
            return mulKSv1grv2(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                    coeffs2, pb2, pe2, cb2, ce2, fvars1, fvars2, v1, v2, ring);
        }
        int v = v1;
        Polynom result;
        Polynom p11p21, p12p22, p11_p12, p21_p22, p11_p12p21_p22, pmiddle; // здесь
        // будет  записан  результат
        int adr1 = pb1 + v - 1;
        int adr2 = pb2 + v - 1;
        // Определим максимальное число pows=2^k такое что max(pow1,pow2)>=pows
        int pow = getMiddlePow(Math.max(powers1[adr1], powers2[adr2]));
        // p1=p11*v^pows + p12
        // indx1 = индекс конца p11 и начала p12 в p1, 0<=indx<=m1
        int indx1 = findMiddle(powers1, pb1, pe1, fvars1, v - 1, pow);
        // p=p21*v^pows + p22
        // indx2 = индекс конца p21 и начала p22 в p2, 0<=indx2<=m2
        int indx2 = findMiddle(powers2, pb2, pe2, fvars2, v - 1, pow);

        // Т.к. p11 и p21 не могут одновременно равняться 0, тогда
        // если p11=0, то переставим this и p и тогда p11<>0
        if (indx1 == 0) {
            int[] t1 = powers1;
            powers1 = powers2;
            powers2 = t1;

            Element[] t2 = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = t2;

            int t3 = pb1;
            pb1 = pb2;
            pb2 = t3;

            t3 = pe1;
            pe1 = pe2;
            pe2 = t3;

            t3 = cb1;
            cb1 = cb2;
            cb2 = t3;

            t3 = ce1;
            ce1 = ce2;
            ce2 = t3;

            t3 = fvars1;
            fvars1 = fvars2;
            fvars2 = t3;

            t3 = indx1;
            indx1 = indx2;
            indx2 = t3;

            t3 = adr1;
            adr1 = adr2;
            adr2 = t3;
        }
        // Теперь p11<>0

        // p11=p1/v^pows (p1 изменен, поэтому
        // его нужно потом восстановить)
        divPol_vPow(powers1, pb1, pb1 + indx1 * fvars1, fvars1, v - 1, pow);

        int end1 = ce1 - cb1;
        int end2 = ce2 - cb2;
        if (indx1 == end1) { // если p11<>0, p12=0, то

            if (indx2 == 0) { // если p21=0, p22<>0, то

                result = mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, true, ring); // result=p11*p2
                // в result vars=v, т.к. в p2 vars=v.
                result.mulPol_vPow(v - 1, pow); // result=p11*p2*v^pows

            } else if (indx2 == end2) { // если p21<>0, p22=0, то

                // p21 = p2/v^pows (потом мы должны восстановить p2)
                divPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

                result = mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, true, ring); // result=p11*p21
                // в result vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                result = result.raise_mul_vPow(v, pow << 1); // result=(p11*p21)*v^2pow

                // Восстановим первоначальный p2 (умножим на v^pows)
                mulPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

            } else { // иначе p21<>0, p22<>0, то
                // Разделим p2 на 2 части: p2 = p21*v^pows + p22
                // p21=1-я часть p2, деленная на v^pows (в конце мы восстановим
                // p2)
                divPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);

                result = mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pb2 + indx2 * fvars2, cb2, cb2 + indx2,
                        true, ring); // result=p11*p21
                // в result vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                result = result.raise_mul_vPow(v, pow << 1); // result =
                // (p11*p21)*v^2pow

                // p22=2-й части p2
                Polynom part = mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1,
                        powers2, coeffs2, pb2 + indx2 * fvars2, pe2, cb2
                        + indx2, ce2, true, ring); // part=p11*p22
                // в part vars может быть любым => поднять до v (если нужно) и
                // умножить на v^pows
                part = part.raise_mul_vPow(v, pow); // part=(p11*p22)*v^pows

                result = result.add(part, ring); // result=(p11*p21)*v^2pow +
                // (p11*p22)*v^pows
                // Восстановим 1-ю часть p2
                mulPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);
            }
        } else { // иначе p11<>0, p12<>0, то
            // разделим p1 на 2 части: p1 = p11*v^pows + p12
            if (indx2 == 0) { // если p21=0, p22<>0, то

                result = mulKS(powers1, coeffs1, pb1, pb1 + indx1 * fvars1,
                        cb1, cb1 + indx1, powers2, coeffs2, pb2, pe2, cb2, ce2,
                        true, ring); // result=p11*p2=p11*p22
                // в result vars=v, т.к. в p2 vars=v.
                result.mulPol_vPow(v - 1, pow); // result=(p11*p22)*v^pows

                Polynom part = mulKS(powers1, coeffs1, pb1 + indx1 * fvars1,
                        pe1, cb1 + indx1, ce1, powers2, coeffs2, pb2, pe2, cb2,
                        ce2, true, ring); // part=p12*p22
                result = result.add(part, ring); // result=(p11*p22)*v^pows +
                // (p12*p22)

            } else if (indx2 == end2) { // если p21<>0, p22=0, то
                // p21=p2/v^pows (p2 изменили, потом нужно будет восстановить)
                divPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

                result = mulKS(powers1, coeffs1, pb1, pb1 + indx1 * fvars1,
                        cb1, cb1 + indx1, powers2, coeffs2, pb2, pe2, cb2, ce2,
                        true, ring); // result=p11*p21
                // в result vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                result = result.raise_mul_vPow(v, pow << 1); // result =
                // (p11*p21)*v^2pow
                Polynom part = mulKS(powers1, coeffs1, pb1 + indx1 * fvars1,
                        pe1, cb1 + indx1, ce1, powers2, coeffs2, pb2, pe2, cb2,
                        ce2, true, ring); // part=p12*p21
                // в part vars может быть любым => поднять до v (если нужно) и
                // умножить на v^pows
                part = part.raise_mul_vPow(v, pow); // part=(p12*p21)*v^pows
                result = result.add(part, ring); // result=(p11*p21)*v^2pow +
                // (p12*p21)*v^pows

                // Восстановим первоначальный p2 (умножим на v^pows)
                mulPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

            } else { // иначе если p21<>0, p22<>0, то
                // Разделим p2 на 2 части: p2 = p21*v^pows + p22
                // p21=1-я часть p2, деленная на v^pows (в конце мы должны
                // восстановить
                // первоначальное значение p2)
                divPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);

                // p11p21 = p11*p21
                p11p21 = mulKS(powers1, coeffs1, pb1, pb1 + indx1 * fvars1,
                        cb1, cb1 + indx1, powers2, coeffs2, pb2, pb2 + indx2
                        * fvars2, cb2, cb2 + indx2, true, ring);
                // p12p22 = p12*p22
                p12p22 = mulKS(powers1, coeffs1, pb1 + indx1 * fvars1, pe1, cb1
                        + indx1, ce1, powers2, coeffs2, pb2 + indx2 * fvars2,
                        pe2, cb2 + indx2, ce2, true, ring);
                // p11_p12 = p11+p12
                p11_p12 = add(powers1, coeffs1, pb1, pb1 + indx1 * fvars1, cb1,
                        cb1 + indx1, powers1, coeffs1, pb1 + indx1 * fvars1,
                        pe1, cb1 + indx1, ce1, true, ring);
                // p21_p22 = p21+p22
                p21_p22 = add(powers2, coeffs2, pb2, pb2 + indx2 * fvars2, cb2,
                        cb2 + indx2, powers2, coeffs2, pb2 + indx2 * fvars2,
                        pe2, cb2 + indx2, ce2, true, ring);
                // p11_p12p21_p22 = (p11+p12)*(p21+p22)
                p11_p12p21_p22 = p11_p12.mulKS(p21_p22, ring);

                // pmiddle = (p11+p12)*(p21+p22) - p11p21 - p12p22
                // Здесь нужно вызвать subtract с копированием, т.к. в
                // случае 0, pmiddle будет иметь общий powers с одним из
                // этих полиномов.
                pmiddle = p11_p12p21_p22.subtract(p11p21, true, ring).subtract(
                        p12p22, true, ring);

                // в p11p21 vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                p11p21 = p11p21.raise_mul_vPow(v, pow << 1); // p11p21 =
                // (p11*p21)*v^2pow

                // pmiddle = ((p11+p12)*(p21+p22) - p11p21 - p12p22)*v^pows
                // в pmiddle vars может быть любым => поднять до v (если нужно)
                // и
                // умножить на v^pows
                pmiddle = pmiddle.raise_mul_vPow(v, pow);

                // result = (p11*p21)*v^2pow + ((p11+p12)*(p21+p22) - p11p21 -
                // - p12p22)*v^pows + p12*p22
                // Здесь вызван обычный add без копирования, т.к. result
                // может иметь общий powers с другими полиномами, т.к. эти
                // полиномы временные и не будут использоваться в дальнейшем.
                result = p11p21.add(pmiddle, ring).add(p12p22, ring);

                // Восстановим p2
                mulPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);
            }
        }
        // Восстановим первоначальный p1 (умножим на v^pows)
        mulPol_vPow(powers1, pb1, pb1 + indx1 * fvars1, fvars1, v - 1, pow);

        if (CHECK_MULKS == 1) {
            try {
                check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, getRealVars, result, ring);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return result;

    }

    private static void check_mulKS(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars,
            Polynom result, Ring ring) {
        Polynom actualResult = mulSS(powers1, coeffs1, pb1, pe1, cb1, ce1,
                powers2, coeffs2, pb2, pe2, cb2, ce2, getRealVars, ring);
        if (!result.equals(actualResult)) {
            throw new RuntimeException("Check not passed!!!");
        }
    }

    private static Polynom mulKSv1grv2(int[] powers1, Element[] coeffs1,
            int pb1, int pe1, int cb1, int ce1, int[] powers2,
            Element[] coeffs2, int pb2, int pe2, int cb2, int ce2, int fv1,
            int fv2, int v1, int v2, Ring ring) {
        // если кол-во переменных v1>v2, то
        // v2>0 и v1>v2 => v1>0 и v2>0
        //
        // Разобьем p1 на части, имеющие мономы с равными степенями при v1,
        // тогда
        // p1 * p2 = (q1*v1^pow1 + q2*v1^pow2 + ...) * p2 =
        // = q1*p2*v1^pow1 + q2*p2*v1^pow2 + ...
        //
        // Полиномы q1*p2, q2*p2, ... вычисляются с помощью mulKS.
        // При сложении частей q1*p2*v1^pow1 , q2*p2*v1^pow2, ... можно не
        // пользоваться add, т.к. эти части не пересекаются.
        //
        ArrayList<Polynom> parts = new ArrayList<Polynom>();
        int begPows = pb1;
        int begCfs = cb1;
        int begAdr = pb1;
        int adr1 = pb1 + v1 - 1;
        int i = cb1;
        int pow1; // степень при переменной v1, которая одинакова
        // во всех мономах части p1
        do {
            pow1 = powers1[adr1]; // степень текущей части p1
            i++;
            adr1 += fv1;
            begAdr += fv1;
            // найдем адрес конца части p1, в которой все мономы имеют
            // одинаковую
            // степень pows при переменной v1
            while (i < ce1 && powers1[adr1] == pow1) {
                i++;
                adr1 += fv1;
                begAdr += fv1;
            }

            // Разделить часть на v1^pow1 только если pow1!=0
            if (pow1 != 0) {
                divPol_vPow(powers1, begPows, begAdr, fv1, v1 - 1, pow1);
            }
            // Умножим часть p1 от begPows до begAdr с переменными от
            // 0 до v1-1 (т.е. из этой части мы вынесли общий множитель v1^pow1)
            // на p2.
            // У part кол-во переменных < v1
            Polynom part = mulKS(powers1, coeffs1, begPows, begAdr, begCfs, i,
                    powers2, coeffs2, pb2, pe2, cb2, ce2, true, ring);
            // Поднять кол-во форматных переменных до v1. Было <v1, станет =v1.
            part = part.raiseToVar(v1);
            // Если выносили v1^pow1 и pow1>0, то умножить на v1^pow1
            if (pow1 != 0) {
                part = part.mulPol_vPow(v1 - 1, pow1);
                // Восстановить powers1 от begPows до begAddr
                mulPol_vPow(powers1, begPows, begAdr, fv1, v1 - 1, pow1);
            }
            parts.add(part);

            // индекс конца этой части будет индексом
            // начала следующей части
            begPows = begAdr;
            begCfs = i;
        } while (i < ce1); // повторять пока есть мономы в p1

        // Подсчитаем кол-во мономов во всех частях
        int monoms = 0;
        for (i = 0; i < parts.size(); i++) {
            monoms += parts.get(i).coeffs.length;
        }

        // Все части в формате v1, не пересекаются (т.к. v1 разные), то
        // объединить все части.
        int[] respowers = new int[monoms * v1];
        Element[] rescoeffs = new Element[monoms];

        Polynom part;
        int resAdr = 0;
        int k = 0;
        for (i = 0; i < parts.size(); i++) {
            part = parts.get(i);
            System.arraycopy(part.powers, 0, respowers, resAdr,
                    part.powers.length);
            resAdr += part.powers.length;
            System.arraycopy(part.coeffs, 0, rescoeffs, k, part.coeffs.length);
            k += part.coeffs.length;
        }

        if (CHECK_MULKS == 1) {
            try {
                check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, true, new Polynom(
                                respowers, rescoeffs), ring);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return new Polynom(respowers, rescoeffs);
    }

    public Polynom mulKSK(Polynom p, Ring ring) {
        if (coeffs.length == 0 || p.coeffs.length == 0) { // Если p=0 или
            // this=0, то
            return new Polynom(new int[0], new Element[0]); // результат=0
        }
        int v1 = powers.length / coeffs.length;
        int v2 = p.powers.length / p.coeffs.length;
        if (v1 == 0 && v2 == 0) {
            return new Polynom(powers, new Element[] {coeffs[0].multiply(
                p.coeffs[0], 1, ring)});
        }
        return mulKSK(powers, coeffs, 0, powers.length, 0, coeffs.length,
                p.powers, p.coeffs, 0, p.powers.length, 0, p.coeffs.length,
                false, ring);
    }

    /*
     * Последовательно умножает 2 полинома по методу Карацубы с использованием
     * умножения чисел по алгоритму Карацубы. Умножает полиномы this и p по
     * методу Карацубы с помощью индексов, что экономит память и увеличивает
     * скорость, т.к. не делаются лишние копирования. 1-й записан в массивы
     * powers1 от pb1 до pe1 и coeffs1 от cb1 до ce1, 2-й записан в массивы
     * powers2 от pb2 до pe2 и coeffs2 от cb2 до ce2.
     *
     * powers1, coeffs1 - массивы, где находятся степени и коэффициенты 1-го
     * полинома pb1, pe1 - адрес начала и конца степеней 1-го полинома в powers1
     * cb1, ce1 - адрес начала и конца коэффициентов 1-го полинома в coeffs1
     * powers2, coeffs2 - массивы, где находятся степени и коэффициенты 2-го
     * полинома pb2, pe2 - адрес начала и конца степеней 2-го полинома в powers2
     * cb2, ce2 - адрес начала и конца коэффициентов 2-го полинома в coeffs2
     * Алгоритм: p1=this, p2=p 1) Если p1=0 или p2=0, то p1*p2 = 0 2) Если
     * количество мономов в p1 (или в p2) =1, то умножить моном p1 (p2) на все
     * мономы p2 (p1). 3) Если количество мономов в p1 и p2 >1, то найдем номер
     * самой старшей переменной из ring, входящей в p1 (пусть это будет v1) и
     * номер самой старшей переменной из ring, входящей в p2 (v2). 4) Если v1>v2
     * (если v1<v2, то поменять p1 и p2 местами), то разделить p1 на части:
     * p1=f1*v1^k1 + f2*v1^k2 + ... + fn*v1^kn так, что fi не содержат v1,
     * тогда: результат = p1*p2 = (f1*p2)*v1^k1 + ... + (fn*p2)*v1^kn 5) Если
     * v1=v2=v, то определим такое число вида 2^k, что 2^k > максимальной
     * степени при v в p1 и p2 и найдем p=2^{k-1} 6) Представим p1 и p2 в виде:
     * p1=p11*v^p + p12 p2=p21*v^p + p22, причем среди полиномов p11 и p12 (p21
     * и p22) не больше одного нулевого и p11 и p21 не могут одновременно быть
     * равными 0. 7) Пусть p11<>0, p12<>0, p21<>0, p22<>0, то результат = p1*p2
     * = (p11*p21)*v^2p + ((p11+p12)(p21+p22) - p11*p21 - - p12*p22)*v^p +
     * p12*p22 8) Пусть p11<>0, p12=0, p21<>0, p22<>0, то результат = p1*p2 =
     * (p11*p21)*v^2p + (p11*p22)*v^p Аналогично рассматриваются другие случаи.
     * **
     * ************************************************************************
     */
    public static Polynom mulKSK(int[] powers1, Element[] coeffs1, int pb1,
            int pe1, int cb1, int ce1, int[] powers2, Element[] coeffs2,
            int pb2, int pe2, int cb2, int ce2, boolean getRealVars, Ring ring) {

        if (ce1 == cb1 || ce2 == cb2) { // если один из полиномов равен 0, то
            return new Polynom(new int[0], new Element[0]); // возвращаем 0
        }

        int fvars1 = (pe1 - pb1) / (ce1 - cb1);
        int fvars2 = (pe2 - pb2) / (ce2 - cb2);

        // Если getRealVars==true, то нужно определить реальное кол-во
        // переменных
        int v1, v2;
        if (getRealVars) {
            // вычисляем реальное кол-во переменных по 1-м мономам
            v1 = getRealVars(powers1, pb1, fvars1);
            v2 = getRealVars(powers2, pb2, fvars2);
        } else {
            v1 = fvars1;
            v2 = fvars2;
        }

        if (v1 < v2) {
            int[] t1 = powers1;
            powers1 = powers2;
            powers2 = t1;

            Element[] t2 = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = t2;

            int t3 = pb1;
            pb1 = pb2;
            pb2 = t3;

            t3 = pe1;
            pe1 = pe2;
            pe2 = t3;

            t3 = cb1;
            cb1 = cb2;
            cb2 = t3;

            t3 = ce1;
            ce1 = ce2;
            ce2 = t3;

            t3 = v1;
            v1 = v2;
            v2 = t3;

            t3 = fvars1;
            fvars1 = fvars2;
            fvars2 = t3;
        }

        // v1>=v2
        if (ce1 - cb1 == 1) {
            // если число мономов в p1 =1, то
            // умножить cb1-й моном p1 на все мономы p2
            // p1 - 1 моном (v1>0), 1 моном C (v1=0)
            // p2 - полином (v2>0, v2<=v1), моном C (v2=0)
            int[] pows = new int[(ce2 - cb2) * v1];
            Element[] cfs = new Element[ce2 - cb2];
            int adr = 0;
            int adr2 = pb2;
            int shift2 = fvars2 - v2;
            for (int i = cb2; i < ce2; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers2[adr2++] + powers1[pb1 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[pb1 + v];
                }
                adr2 += shift2;
                cfs[i - cb2] = coeffs2[i].multiply(coeffs1[cb1], 1, ring);
            }

            if (CHECK_MULKS == 1) {
                try {
                    check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                            coeffs2, pb2, pe2, cb2, ce2, getRealVars,
                            new Polynom(pows, cfs), ring);
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            }

            return new Polynom(pows, cfs);

        } else if (ce2 - cb2 == 1) {
            // если число мономов в p2 =1, v2>=0, то
            // умножить моном p2 на все мономы p1
            // p1 - полином, т.к. ce1-cb1!=1 => ce1-cb1>1 => v1>0, v1>=v2
            int[] pows = new int[(ce1 - cb1) * v1];
            Element[] cfs = new Element[ce1 - cb1];
            int adr = 0;
            int adr1 = pb1;
            int shift1 = fvars1 - v1;
            for (int i = cb1; i < ce1; i++) {
                for (int v = 0; v < v2; v++) {
                    pows[adr++] = powers1[adr1++] + powers2[pb2 + v];
                }
                for (int v = v2; v < v1; v++) {
                    pows[adr++] = powers1[adr1++];
                }
                adr1 += shift1;
                cfs[i - cb1] = coeffs1[i].multiply(coeffs2[cb2], 1, ring);
            }

            if (CHECK_MULKS == 1) {
                try {
                    check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                            coeffs2, pb2, pe2, cb2, ce2, getRealVars,
                            new Polynom(pows, cfs), ring);
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            }

            return new Polynom(pows, cfs);

        }

        if (v1 > v2) {
            return mulKSv1grv2K(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                    coeffs2, pb2, pe2, cb2, ce2, fvars1, fvars2, v1, v2, ring);
        }

        // Иначе v1=v2=v
        int v = v1;
        Polynom result;
        // здесь будет записан результат
        Polynom p11p21, p12p22, p11_p12, p21_p22, p11_p12p21_p22, pmiddle;

        int adr1 = pb1 + v - 1;
        int adr2 = pb2 + v - 1;
        // Определим максимальное число pows=2^k такое что max(pow1,pow2)>=pows
        int pow = getMiddlePow(Math.max(powers1[adr1], powers2[adr2]));

        // p1=p11*v^pows + p12
        // indx1 = индекс конца p11 и начала p12 в p1, 0<=indx<=m1
        int indx1 = findMiddle(powers1, pb1, pe1, fvars1, v - 1, pow);

        // p=p21*v^pows + p22
        // indx2 = индекс конца p21 и начала p22 в p2, 0<=indx2<=m2
        int indx2 = findMiddle(powers2, pb2, pe2, fvars2, v - 1, pow);

        // Т.к. p11 и p21 не могут одновременно равняться 0, тогда
        // если p11=0, то переставим this и p и тогда p11<>0
        if (indx1 == 0) {
            int[] t1 = powers1;
            powers1 = powers2;
            powers2 = t1;

            Element[] t2 = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = t2;

            int t3 = pb1;
            pb1 = pb2;
            pb2 = t3;

            t3 = pe1;
            pe1 = pe2;
            pe2 = t3;

            t3 = cb1;
            cb1 = cb2;
            cb2 = t3;

            t3 = ce1;
            ce1 = ce2;
            ce2 = t3;

            t3 = fvars1;
            fvars1 = fvars2;
            fvars2 = t3;

            t3 = indx1;
            indx1 = indx2;
            indx2 = t3;

            t3 = adr1;
            adr1 = adr2;
            adr2 = t3;
        }
        // Теперь p11<>0

        // p11=p1/v^pows (p1 изменен, поэтому
        // его нужно потом восстановить)
        divPol_vPow(powers1, pb1, pb1 + indx1 * fvars1, fvars1, v - 1, pow);

        int end1 = ce1 - cb1;
        int end2 = ce2 - cb2;
        if (indx1 == end1) { // если p11<>0, p12=0, то

            if (indx2 == 0) { // если p21=0, p22<>0, то

                result = mulKSK(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, true, ring); // result=p11*p2
                // в result vars=v, т.к. в p2 vars=v.
                result.mulPol_vPow(v - 1, pow); // result=p11*p2*v^pows

            } else if (indx2 == end2) { // если p21<>0, p22=0, то

                // p21 = p2/v^pows (потом мы должны восстановить p2)
                divPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

                result = mulKSK(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, true, ring); // result=p11*p21
                // в result vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                result = result.raise_mul_vPow(v, pow << 1); // result=(p11*p21)*v^2pow

                // Восстановим первоначальный p2 (умножим на v^pows)
                mulPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

            } else { // иначе p21<>0, p22<>0, то
                // Разделим p2 на 2 части: p2 = p21*v^pows + p22
                // p21=1-я часть p2, деленная на v^pows (в конце мы восстановим
                // p2)
                divPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);

                result = mulKSK(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pb2 + indx2 * fvars2, cb2, cb2 + indx2,
                        true, ring); // result=p11*p21
                // в result vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                result = result.raise_mul_vPow(v, pow << 1); // result =
                // (p11*p21)*v^2pow

                // p22=2-й части p2
                Polynom part = mulKSK(powers1, coeffs1, pb1, pe1, cb1, ce1,
                        powers2, coeffs2, pb2 + indx2 * fvars2, pe2, cb2
                        + indx2, ce2, true, ring); // part=p11*p22
                // в part vars может быть любым => поднять до v (если нужно) и
                // умножить на v^pows
                part = part.raise_mul_vPow(v, pow); // part=(p11*p22)*v^pows

                result = result.add(part, ring); // result=(p11*p21)*v^2pow +
                // (p11*p22)*v^pows
                // Восстановим 1-ю часть p2
                mulPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);
            }
        } else { // иначе p11<>0, p12<>0, то
            // разделим p1 на 2 части: p1 = p11*v^pows + p12
            if (indx2 == 0) { // если p21=0, p22<>0, то

                result = mulKSK(powers1, coeffs1, pb1, pb1 + indx1 * fvars1,
                        cb1, cb1 + indx1, powers2, coeffs2, pb2, pe2, cb2, ce2,
                        true, ring); // result=p11*p2=p11*p22
                // в result vars=v, т.к. в p2 vars=v.
                result.mulPol_vPow(v - 1, pow); // result=(p11*p22)*v^pows

                Polynom part = mulKSK(powers1, coeffs1, pb1 + indx1 * fvars1,
                        pe1, cb1 + indx1, ce1, powers2, coeffs2, pb2, pe2, cb2,
                        ce2, true, ring); // part=p12*p22
                result = result.add(part, ring); // result=(p11*p22)*v^pows +
                // (p12*p22)

            } else if (indx2 == end2) { // если p21<>0, p22=0, то
                // p21=p2/v^pows (p2 изменили, потом нужно будет восстановить)
                divPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

                result = mulKSK(powers1, coeffs1, pb1, pb1 + indx1 * fvars1,
                        cb1, cb1 + indx1, powers2, coeffs2, pb2, pe2, cb2, ce2,
                        true, ring); // result=p11*p21
                // в result vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                result = result.raise_mul_vPow(v, pow << 1); // result =
                // (p11*p21)*v^2pow
                Polynom part = mulKSK(powers1, coeffs1, pb1 + indx1 * fvars1,
                        pe1, cb1 + indx1, ce1, powers2, coeffs2, pb2, pe2, cb2,
                        ce2, true, ring); // part=p12*p21
                // в part vars может быть любым => поднять до v (если нужно) и
                // умножить на v^pows
                part = part.raise_mul_vPow(v, pow); // part=(p12*p21)*v^pows
                result = result.add(part, ring); // result=(p11*p21)*v^2pow +
                // (p12*p21)*v^pows

                // Восстановим первоначальный p2 (умножим на v^pows)
                mulPol_vPow(powers2, pb2, pe2, fvars2, v - 1, pow);

            } else { // иначе если p21<>0, p22<>0, то
                // Разделим p2 на 2 части: p2 = p21*v^pows + p22
                // p21=1-я часть p2, деленная на v^pows (в конце мы должны
                // восстановить
                // первоначальное значение p2)
                divPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);

                // p11p21 = p11*p21
                p11p21 = mulKSK(powers1, coeffs1, pb1, pb1 + indx1 * fvars1,
                        cb1, cb1 + indx1, powers2, coeffs2, pb2, pb2 + indx2
                        * fvars2, cb2, cb2 + indx2, true, ring);
                // p12p22 = p12*p22
                p12p22 = mulKSK(powers1, coeffs1, pb1 + indx1 * fvars1, pe1,
                        cb1 + indx1, ce1, powers2, coeffs2, pb2 + indx2
                        * fvars2, pe2, cb2 + indx2, ce2, true, ring);
                // p11_p12 = p11+p12
                p11_p12 = add(powers1, coeffs1, pb1, pb1 + indx1 * fvars1, cb1,
                        cb1 + indx1, powers1, coeffs1, pb1 + indx1 * fvars1,
                        pe1, cb1 + indx1, ce1, true, ring);
                // p21_p22 = p21+p22
                p21_p22 = add(powers2, coeffs2, pb2, pb2 + indx2 * fvars2, cb2,
                        cb2 + indx2, powers2, coeffs2, pb2 + indx2 * fvars2,
                        pe2, cb2 + indx2, ce2, true, ring);
                // p11_p12p21_p22 = (p11+p12)*(p21+p22)
                p11_p12p21_p22 = p11_p12.mulKSK(p21_p22, ring);

                // pmiddle = (p11+p12)*(p21+p22) - p11p21 - p12p22
                // Здесь нужно вызвать subtract с копированием, т.к. в
                // случае 0, pmiddle будет иметь общий powers с одним из
                // этих полиномов.
                pmiddle = p11_p12p21_p22.subtract(p11p21, true, ring).subtract(
                        p12p22, true, ring);

                // в p11p21 vars может быть любым => поднять до v (если нужно) и
                // умножить на v^2pow
                p11p21 = p11p21.raise_mul_vPow(v, pow << 1); // p11p21 =
                // (p11*p21)*v^2pow

                // pmiddle = ((p11+p12)*(p21+p22) - p11p21 - p12p22)*v^pows
                // в pmiddle vars может быть любым => поднять до v (если нужно)
                // и
                // умножить на v^pows
                pmiddle = pmiddle.raise_mul_vPow(v, pow);

                // result = (p11*p21)*v^2pow + ((p11+p12)*(p21+p22) - p11p21 -
                // - p12p22)*v^pows + p12*p22
                // Здесь вызван обычный add без копирования, т.к. result
                // может иметь общий powers с другими полиномами, т.к. эти
                // полиномы временные и не будут использоваться в дальнейшем.
                result = p11p21.add(pmiddle, ring).add(p12p22, ring);

                // Восстановим p2
                mulPol_vPow(powers2, pb2, pb2 + indx2 * fvars2, fvars2, v - 1,
                        pow);
            }
        }
        // Восстановим первоначальный p1 (умножим на v^pows)
        mulPol_vPow(powers1, pb1, pb1 + indx1 * fvars1, fvars1, v - 1, pow);

        if (CHECK_MULKS == 1) {
            try {
                check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, getRealVars, result, ring);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return result;

    }

    private static Polynom mulKSv1grv2K(int[] powers1, Element[] coeffs1,
            int pb1, int pe1, int cb1, int ce1, int[] powers2,
            Element[] coeffs2, int pb2, int pe2, int cb2, int ce2, int fv1,
            int fv2, int v1, int v2, Ring ring) {
        // если кол-во переменных v1>v2, то
        // v2>0 и v1>v2 => v1>0 и v2>0
        //
        // Разобьем p1 на части, имеющие мономы с равными степенями при v1,
        // тогда
        // p1 * p2 = (q1*v1^pow1 + q2*v1^pow2 + ...) * p2 =
        // = q1*p2*v1^pow1 + q2*p2*v1^pow2 + ...
        //
        // Полиномы q1*p2, q2*p2, ... вычисляются с помощью mulKS.
        // При сложении частей q1*p2*v1^pow1 , q2*p2*v1^pow2, ... можно не
        // пользоваться add, т.к. эти части не пересекаются.
        //
        ArrayList<Polynom> parts = new ArrayList<Polynom>();
        int begPows = pb1;
        int begCfs = cb1;
        int begAdr = pb1;
        int adr1 = pb1 + v1 - 1;
        int i = cb1;
        int pow1; // степень при переменной v1, которая одинакова
        // во всех мономах части p1
        do {
            pow1 = powers1[adr1]; // степень текущей части p1
            i++;
            adr1 += fv1;
            begAdr += fv1;
            // найдем адрес конца части p1, в которой все мономы имеют
            // одинаковую
            // степень pows при переменной v1
            while (i < ce1 && powers1[adr1] == pow1) {
                i++;
                adr1 += fv1;
                begAdr += fv1;
            }

            // Разделить часть на v1^pow1 только если pow1!=0
            if (pow1 != 0) {
                divPol_vPow(powers1, begPows, begAdr, fv1, v1 - 1, pow1);
            }
            // Умножим часть p1 от begPows до begAdr с переменными от
            // 0 до v1-1 (т.е. из этой части мы вынесли общий множитель v1^pow1)
            // на p2.
            // У part кол-во переменных < v1
            Polynom part = mulKSK(powers1, coeffs1, begPows, begAdr, begCfs, i,
                    powers2, coeffs2, pb2, pe2, cb2, ce2, true, ring);
            // Поднять кол-во форматных переменных до v1. Было <v1, станет =v1.
            part = part.raiseToVar(v1);
            // Если выносили v1^pow1 и pow1>0, то умножить на v1^pow1
            if (pow1 != 0) {
                part = part.mulPol_vPow(v1 - 1, pow1);
                // Восстановить powers1 от begPows до begAddr
                mulPol_vPow(powers1, begPows, begAdr, fv1, v1 - 1, pow1);
            }
            parts.add(part);

            // индекс конца этой части будет индексом
            // начала следующей части
            begPows = begAdr;
            begCfs = i;
        } while (i < ce1); // повторять пока есть мономы в p1

        // Подсчитаем кол-во мономов во всех частях
        int monoms = 0;
        for (i = 0; i < parts.size(); i++) {
            monoms += parts.get(i).coeffs.length;
        }

        // Все части в формате v1, не пересекаются (т.к. v1 разные), то
        // объединить все части.
        int[] respowers = new int[monoms * v1];
        Element[] rescoeffs = new Element[monoms];

        Polynom part;
        int resAdr = 0;
        int k = 0;
        for (i = 0; i < parts.size(); i++) {
            part = parts.get(i);
            System.arraycopy(part.powers, 0, respowers, resAdr,
                    part.powers.length);
            resAdr += part.powers.length;
            System.arraycopy(part.coeffs, 0, rescoeffs, k, part.coeffs.length);
            k += part.coeffs.length;
        }

        if (CHECK_MULKS == 1) {
            try {
                check_mulKS(powers1, coeffs1, pb1, pe1, cb1, ce1, powers2,
                        coeffs2, pb2, pe2, cb2, ce2, true,
                        new Polynom(respowers, rescoeffs), ring);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return new Polynom(respowers, rescoeffs);
    }

    /**
     * Увеличивает кол-во форматных переменных до v и умножает на v^pows. Если
     * this=0, то вернуть 0.
     *
     * @param v int v - кол-во переменных
     * @param pows int
     *
     * @return Polynom
     */
    private Polynom raise_mul_vPow(int v, int pow) {
        if (coeffs.length == 0) {
            return new Polynom(new int[0], new Element[0]);
        }

        return raiseToVar(v).mulPol_vPow(v - 1, pow);
    }

    /*
     * Увеличивает кол-во переменных в формате полинома до v, записывая в
     * степени недостающих переменных нули. Если this=0, то вернуть 0. v>=0,
     * v>=fv. Если v=fv, то ничего не делать.
     */
    private Polynom raiseToVar(int v) {
        if (coeffs.length == 0) {
            return new Polynom(new int[0], new Element[0]);
        }
        // fv>=0
        int fv = powers.length / coeffs.length;
        if (fv == v) {
            return this;
        }

        int[] respowers = new int[coeffs.length * v];
        int resAdr = 0;
        int adr = 0;
        for (int i = 0; i < coeffs.length; i++) {
            for (int j = 0; j < fv; j++) {
                respowers[resAdr++] = powers[adr++];
            }
            resAdr += v - fv;
        }
        return new Polynom(respowers, coeffs);
    }

    /**
     * Умножает полином this на v^pows. fv >= v+1.
     *
     * @param v int номер переменной
     * @param pows int
     *
     * @return Polynom
     */
    private Polynom mulPol_vPow(int v, int pow) {
        if (coeffs.length == 0) {
            return new Polynom(new int[0], new Element[0]);
        }
        int fvars = powers.length / coeffs.length;
        int adr = v;
        for (int i = 0; i < coeffs.length; i++) {
            powers[adr] += pow;
            adr += fvars;
        }
        return this;
    }

    /**
     * Умножает мономы на v^pows
     *
     * @param powers int[]
     * @param pbeg int
     * @param pend int
     * @param fv int fv>0
     * @param v int номер переменной v>=0, v<fv
     * @param pows int
     *
     * @return Polynom
     */
    private static void mulPol_vPow(int[] powers, int pbeg, int pend, int fv,
            int v, int pow) {
        int adr = pbeg + v;
        while (adr < pend) {
            powers[adr] += pow;
            adr += fv;
        }
    }

    /**
     * Делит мономы на v^pows
     *
     * @param powers int[]
     * @param pbeg int
     * @param pend int
     * @param fv int fv>0
     * @param v int номер переменной v>=0
     * @param pows int
     *
     * @return Polynom
     */
    private static void divPol_vPow(int[] powers, int pbeg, int pend, int fv,
            int v, int pow) {
        int adr = pbeg + v;
        while (adr < pend) {
            powers[adr] -= pow;
            adr += fv;
        }
    }

    /**
     * Возвращает 2^K: <code>2^k &lt;= pows &lt; 2^{k+1}</code>
     *
     * @param pows int
     *
     * @return int
     */
    private static int getMiddlePow(int pow) {
        int mask = 1 << 31;
        while ((pow & mask) == 0) {
            mask >>>= 1;
        }
        return mask;
    }

    private static int findMiddle(int[] powers, int pbeg, int pend, int fv,
            int v, int pow) {
        // p=p1*v^pows + p2
        int indx = 0; // indx = индекс 1-го монома в p2 или end
        int adr = pbeg + v;
        while (adr < pend && powers[adr] >= pow) {
            indx++;
            adr += fv;
        }
        return indx;
    }

    /**
     * Divide polynom by number of Z or Z64-type.
     *
     * @param p - number of type Z, CZ, Z64, CZ64 type.
     * @param ring
     *
     * @return polynom with fraction coefficients
     */
    public Polynom divideByNumberToFraction(Element p, Ring ring) {
        int cLength = coeffs.length;
        Element[] cc = new Element[cLength];
            for (int i = 0; i < cLength; i++) {Element ccc=coeffs[i];
              cc[i]=  ((( ccc.numbElementType() & Ring.Z)==Ring.Z)||
                      ((( ccc.numbElementType() & Ring.Z64)) ==Ring.Z64))?
                     (new Fraction(ccc, p)).cancel(ring): ccc.divide(p, ring);
            }
            return new Polynom(powers, cc);
    }

    /**
     * Divide polynom by number. The operation of division of two number are
     * used for each coefficient. The cense of division operation is the same as
     * the standart division for this type of numbers. For Z-numbers you obtain
     * the integer part of quotient.
     *
     * @param p
     * @param ring
     *
     * @return
     */
    public Polynom divideByNumber(Element p, Ring ring) { if(p.isOne(ring))return this;
        Element[] cc = new Element[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            cc[i] = coeffs[i].divide(p, ring);
        }
        return new Polynom(powers, cc);
    }

    /**
     * Divide polynom by polynom or number to fraction
     *
     * @param elem
     * @param ring
     *
     * @return
     */
    @Override
    public Element divideToFraction(Element elem, Ring ring) {
        if (elem.isOne(ring)) {return this; }
        if (elem instanceof Polynom) {
            Polynom val = (Polynom) elem;
            if (val.isItNumber()) {
                return divideByNumberToFraction(val.coeffs[0], ring);
            }
            Polynom gcd = (Polynom) GCD(val, ring);

            Polynom numer = divideExact(gcd, ring);
            Polynom denom = val.divideExact(gcd, ring);
            if (denom.isOne(ring)) {return numer;}
            return new Fraction(numer, denom).cancel(ring);

        }

        if(elem.isItNumber())
        return divideByNumberToFraction(elem, ring);
        else return new Fraction(this, elem);

    }

    @Override
    public Element divide(Element p, Ring ring) {
        if ((p == NAN)||(p.isZero(ring))) {return NAN;}
        if ((p == NEGATIVE_INFINITY) || (p == POSITIVE_INFINITY)) {
                                            return ring.numberZERO;        }
        if (p.isItNumber()) {
            if (p instanceof Polynom) {
                p = ((Polynom) p).coeffs[0];
            }else if (p instanceof FactorPol) {
                p = ((FactorPol) p).toNumber(ring.algebra[0], ring);
            }
            return divideByNumber(p, ring);
        } else {
            return divideToFraction(p, ring);
            //return new Fraction(this, p);
        }
    }

    public Polynom[] divideExt(Element p, Ring ring) {
        return divideExt((Polynom) p, ring);
    }

    @Override
    public Element divideExact(Element el, Ring ring) {
        if (el instanceof Polynom) {
            return divideExact((Polynom) el, ring);
        }
        return super.divideExact(el, ring);
    }

    /**
     * Стандартное точное деление полиномов. Если полином this не делится на p,
     * то метод зависнет (попадет в бесконечный цикл).
     *
     * @param p 2-й полином (делитель)
     * @param ring
     *
     * @return частное от деления this на p
     */
    public Polynom divideExact(Polynom p, Ring ring) {
        if (coeffs.length == 0) {
            return new Polynom(new int[0], new Element[0]);
        }
        int v1 = powers.length / coeffs.length;
        int v2 = p.powers.length / p.coeffs.length;

        // случай когда this и p - константы
        if (v1 == 0 && v2 == 0) {
            return new Polynom(powers, new Element[] {coeffs[0].divide(
                p.coeffs[0], ring)});
        }

        // случай когда this делится на 1 моном
        if (p.coeffs.length == 1) {
            int[] respowers = new int[powers.length];
            Element[] rescoeffs = new Element[coeffs.length];
            int adr1 = 0;
            for (int i = 0; i < coeffs.length; i++) {
                for (int v = 0; v < v2; v++) {
                    respowers[adr1] = powers[adr1] - p.powers[v];
                    adr1++;
                }
                for (int v = v2; v < v1; v++) {
                    respowers[adr1] = powers[adr1];
                    adr1++;
                }
                rescoeffs[i] = coeffs[i].divide(p.coeffs[0], ring);
            }
            return new Polynom(respowers, rescoeffs).truncate();
        }
        Polynom f = this;
        Polynom p1 = null;

        ArrayList resPows = new ArrayList();
        ArrayList resCfs = new ArrayList();

        do {

            // Получить следующий моном результата h_i
            int[] pows = new int[v1];
          
            for (int v = 0; v < v2; v++) {
                
                pows[v] = f.powers[v] - p.powers[v];
            }
            System.arraycopy(f.powers, v2, pows, v2, v1 - v2);

            Element c = f.coeffs[0].divide(p.coeffs[0], ring);

            // p1=p*h_i
			/*
             * if (pows[v1-1]==0){ System.err.println("pows has not v1
             * vars..."); }
             */
            /*
             * Если в pows оказалось меньше, чем v1 переменных, то в p ровно v1
             * переменных и ошибки при перемножении не возникнет.
             */
            p1 = new Polynom(pows, new Element[] {c}).mulSS(p, ring);

            f = f.subtract(p1, ring);

            if (!f.isZero(ring)) {
                v1 = f.powers.length / f.coeffs.length;
            }

            // Добавить в список результата pows и c
            resPows.add(pows);
            resCfs.add(c);

        } while (!f.isZero(ring));

        // Сделать из списков результат
        // По 0-му моному результата определим кол-во переменных в результате
        int[] pows = (int[]) resPows.get(0);
        int v = pows.length - 1;
        Element c;
        while (v >= 0 && pows[v] == 0) {
            v--;
        }
        if (v == -1) {
            return new Polynom(new int[0],
                    new Element[] {(Element) resCfs.get(0)});
        }
        int[] resPowers = new int[resPows.size() * (v + 1)];
        Element[] resCoeffs = (Element[]) resCfs.toArray(new Element[0]);

        int adr = 0;
        for (int i = 0; i < resCoeffs.length; i++) {
            pows = (int[]) resPows.get(i);
            int vmin = v + 1 <= pows.length ? v + 1 : pows.length; // Math.min(v+1,pows.length);
            for (int j = 0; j < vmin; j++) {
                resPowers[adr++] = pows[j];
            }
            adr += v + 1 - vmin;
        }
        return new Polynom(resPowers, resCoeffs);
    }

    /**
     * Стандартное точное деление полиномов. Этот метод содержит 2 проверки: 1.
     * Если полином this не делится на p, то будет выброшено RuntimeException со
     * значениями полиномов. 2. В конце метода результат умножается на p и
     * сравнивается с this. Если значения не совпадают, то результат деления
     * неверен и будет выброшено RuntimeException со значениями полиномов.
     *
     * @param p 2-й полином (делитель)
     *
     * @return частное от деления this на p
     */
    public Polynom divideExactWithCheck(Polynom p, Ring ring) throws Exception {
        if (coeffs.length == 0) {
            return new Polynom(new int[0], new Element[0]);
        }
        int v1 = powers.length / coeffs.length;
        int v2 = p.powers.length / p.coeffs.length;

        // случай когда this и p - константы
        if (v1 == 0 && v2 == 0) {
            return new Polynom(powers, new Element[] {coeffs[0].divide(
                p.coeffs[0], ring)});
        }

        // случай когда this делится на 1 моном
        if (p.coeffs.length == 1) {
            int[] respowers = new int[powers.length];
            Element[] rescoeffs = new Element[coeffs.length];
            int adr1 = 0;
            for (int i = 0; i < coeffs.length; i++) {
                for (int v = 0; v < v2; v++) {
                    respowers[adr1] = powers[adr1] - p.powers[v];
                    if (respowers[adr1] < 0) {
                        Ring r = createRing(v1);
                        throw new RuntimeException(
                                "Polynoms cannot be divided exactly:\n" + "p1="
                                + toString(r) + "\n" + "p2="
                                + p.toString(r) + "\n");
                    }
                    adr1++;
                }
                for (int v = v2; v < v1; v++) {
                    respowers[adr1] = powers[adr1];
                    adr1++;
                }
                rescoeffs[i] = coeffs[i].divide(p.coeffs[0], ring);
            }
            return new Polynom(respowers, rescoeffs).truncate();
        }
        Polynom f = this;
        Polynom p1 = null;

        ArrayList resPows = new ArrayList();
        ArrayList resCfs = new ArrayList();

        int[] prevHighPows = new int[v1];
        System.arraycopy(f.powers, 0, prevHighPows, 0, v1);

        do {
            // Получить следующий моном результата h_i
            int[] pows = new int[v1];
            for (int v = 0; v < v2; v++) {
                pows[v] = f.powers[v] - p.powers[v];
            }
            System.arraycopy(f.powers, v2, pows, v2, v1 - v2);

            Element c = f.coeffs[0].divide(p.coeffs[0], ring);

            // p1=p*h_i
            p1 = new Polynom(pows, new Element[] {c}).mulSS(p, ring);

            // f=f-p1
            f = f.subtract(p1, ring);
            if (f.coeffs.length != 0) {
                v1 = f.powers.length / f.coeffs.length;
            }

            // Добавить в список результата pows и c
            resPows.add(pows);
            resCfs.add(c);

            if (f.coeffs.length != 0) {
                boolean passed = true;
                if (f.isZero(ring)) {
                    passed = false;
                } else if (!pows1GrPows2(prevHighPows, f.powers, v1)) {
                    passed = false;
                }

                if (!passed) {
                    Ring r = createRing(v1);

                    throw new RuntimeException(
                            "Polynoms cannot be divided exactly:\n" + "p1="
                            + toString(r) + "\n" + "p2="
                            + p.toString(r) + "\n");
                }

                // Записать в prevHighPows степени 0-го монома f
                prevHighPows = new int[v1];
                System.arraycopy(f.powers, 0, prevHighPows, 0, v1);
            }

        } while (f.coeffs.length != 0);

        // Сделать из списков результат
        // По 0-му моному результата определим кол-во переменных в результате
        int[] pows = (int[]) resPows.get(0);
        int v = pows.length - 1;
        Element c;
        while (v >= 0 && pows[v] == 0) {
            v--;
        }
        if (v == -1) {
            return new Polynom(new int[0],
                    new Element[] {(Element) resCfs.get(0)});
        }
        int[] resPowers = new int[resPows.size() * (v + 1)];
        Element[] resCoeffs = (Element[]) resCfs.toArray(new Element[0]);

        int adr = 0;
        for (int i = 0; i < resCoeffs.length; i++) {
            pows = (int[]) resPows.get(i);

            int vmin = Math.min(v + 1, pows.length);
            for (int j = 0; j < vmin; j++) {
                resPowers[adr++] = pows[j];
            }
            adr += v + 1 - vmin;
        }

        if (new Polynom(resPowers, resCoeffs).mulSS(p, ring).subtract(this,
                ring).coeffs.length != 0) {
            Ring r = createRing(powers.length / coeffs.length);
            throw new RuntimeException("Polynoms cannot be divided exactly:\n"
                    + "p1=" + toString(r) + "\n" + "p2=" + p.toString(r) + "\n");
        }
        return new Polynom(resPowers, resCoeffs);
    }

    /*
     * Возвращает true, если 0-й моном в powers1 > 0-го монома в powers2.
     */
    private boolean pows1GrPows2(int[] powers1, int[] powers2, int vars2) {
        if (powers1.length > vars2) {
            return true;
        } else if (powers1.length < vars2) {
            return false;
        }
        vars2--;
        while (vars2 >= 0 && powers1[vars2] == powers2[vars2]) {
            vars2--;
        }
        return vars2 >= 0 && powers1[vars2] > powers2[vars2];

    }

    private Ring createRing(int vars) {
        String ringStr = "x,y,z,s,t,u,v,w";
        String s = "Z[" + ringStr.substring(0, 2 * vars + 1) + "]";
        Ring r = null;
        try {
            r = new Ring(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return r;
    }

    /*
     * �\uFFFDщет в полиноме this самый младший моном, степени которого >=
     * степеням монома pows. Предполагается, что степени 0-го монома this >
     * степеней монома pows.
     */
    private int findLastGEmonom(int[] pows) {
        int v = pows.length;

        // Сравним последний моном this с мономом pows
        int adr = powers.length - 1;
        int i = v - 1;
        while (i >= 0 && powers[adr] == pows[i]) {
            adr--;
            i--;
        }
        if (i == -1 || powers[adr] > pows[i]) {
            return coeffs.length - 1;
        }

        // �\uFFFDнвариант: в конце a -">", в конце b - "<"
        int a = 0;
        int b = coeffs.length - 1;
        int m;
        while (b - a > 1) {
            m = (a + b) >>> 1;

            // Сравним m-й моном this с мономом pows
            adr = m * v + v - 1;
            i = v - 1;
            while (i >= 0 && powers[adr] == pows[i]) {
                adr--;
                i--;
            }
            if (i == -1) {
                // степени m-го монома == степеням pows
                return m;
            } else if (powers[adr] > pows[i]) {
                a = m;
            } else {
                b = m;
            }
        }

        // b-a==1
        return a;
    }

    /*
     * Возвращает часть полинома (подполином) от beg включительно до end, не
     * включая
     *
     */
    public Polynom subPolynom(int beg, int end) {
        if (beg == end) {
            return new Polynom(new int[0], new Element[0]);
        }
        int vars = powers.length / coeffs.length;
        // определение реального кол-ва переменных
        int v = getRealVars(powers, beg * vars, vars);
        int[] respowers = new int[(end - beg) * v];
        Element[] rescoeffs = new Element[end - beg];

        if (vars == v) {
            System.arraycopy(powers, beg * vars, respowers, 0, respowers.length);
        } else {
            // v<vars
            int adr1 = beg * vars;
            int adr = 0;
            int shift = vars - v;
            for (int i = beg; i < end; i++) {
                for (int j = 0; j < v; j++) {
                    respowers[adr++] = powers[adr1++];
                }
                adr1 += shift;
            }
        }
        System.arraycopy(coeffs, beg, rescoeffs, 0, rescoeffs.length);
        return new Polynom(respowers, rescoeffs);
    }

    //peresl
    public Polynom subPolynom1(int beg, int end) {
        if (beg == end) {
            return new Polynom(new int[0], new Element[0]);
        }
        int len = end - beg;//кол-во мономов
        int nvars = powers.length / coeffs.length;
        Element coeffsRes[] = new Element[len];
        int powersRes[] = new int[len * nvars];
        System.arraycopy(coeffs, beg, coeffsRes, 0, len);
        System.arraycopy(powers, beg * nvars, powersRes, 0, len * nvars);
        Polynom res = new Polynom(powersRes, coeffsRes);
        return res;

    }
    
    
    /*
     * Вычитание полиномов. powers1, coeffs1 - массивы, где находятся степени и
     * коэффициенты 1-го полинома pb1, pe1 - адрес начала и конца степеней 1-го
     * полинома в powers1 cb1, ce1 - адрес начала и конца коэффициентов 1-го
     * полинома в coeffs1 powers2, coeffs2 - массивы, где находятся степени и
     * коэффициенты 2-го полинома pb2, pe2 - адрес начала и конца степеней 2-го
     * полинома в powers2 cb2, ce2 - адрес начала и конца коэффициентов 2-го
     * полинома в coeffs2
     */

    public static Polynom subtractV1grV2p1p2(int[] powers1, Element[] coeffs1,
            int pb1, int pe1, int cb1, int ce1, int[] powers2,
            Element[] coeffs2, int pb2, int pe2, int cb2, int ce2, Ring ring) {
        if (ce1 - cb1 == 0 && ce2 - cb2 == 0) { // Если this=0 и p=0,
            return new Polynom(new int[0], new Element[0]); // то возвращаем
            // ZERO-нулевой
            // полином.
        } else if (ce1 - cb1 == 0) { // Если this=0, то возвращаем -p
            int[] pows = new int[pe2 - pb2];
            Element[] cfs = new Element[ce2 - cb2];
            System.arraycopy(powers2, pb2, pows, 0, pows.length);
            for (int i = 0; i < cfs.length; i++) {
                cfs[i] = coeffs2[i + cb2].negate(ring);
            }
            return new Polynom(pows, cfs);
        } else if (ce2 - cb2 == 0) { // Если p=0, то возвращаем копию this
            int[] pows = new int[pe1 - pb1];
            Element[] cfs = new Element[ce1 - cb1];
            System.arraycopy(powers1, pb1, pows, 0, pows.length);
            System.arraycopy(coeffs1, cb1, cfs, 0, cfs.length);
            return new Polynom(pows, cfs);
        }

        int vars1 = (pe1 - pb1) / (ce1 - cb1);
        int vars2 = (pe2 - pb2) / (ce2 - cb2);

        if (vars1 < vars2) {
            int[] tempArr = powers1;
            powers1 = powers2;
            powers2 = tempArr;

            Element[] tempBI = coeffs1;
            coeffs1 = coeffs2;
            coeffs2 = tempBI;

            int temp = vars1;
            vars1 = vars2;
            vars2 = temp;

            temp = pb1;
            pb1 = pb2;
            pb2 = temp;

            temp = pe1;
            pe1 = pe2;
            pe2 = temp;

            temp = cb1;
            cb1 = cb2;
            cb2 = temp;

            temp = ce1;
            ce1 = ce2;
            ce2 = temp;
        }

        // максимально возможное число мономов в разности
        int monNumb = ce1 - cb1 + ce2 - cb2;

        // создадим массивы степеней и коэффициентов для разности
        int[] pows = new int[monNumb * vars1];
        Element[] cfs = new Element[monNumb];

        int j1 = ce1 - 1; // j1 - номер монома в this(j1=0..monoms-1)
        int adr1 = pe1 - vars1; // adr1 - адрес j1-го монома
        int j2 = ce2 - 1;
        int adr2 = pe2 - vars2;
        int j = monNumb - 1; // j-номер монома разности
        int adr = j * vars1; // adr=адрес j-го монома разности=j*vars

        int i1; // i1 и i2 - переменные для хранения adr1 и adr2
        int i2;
        int i;

        while (j1 >= cb1 && j2 >= cb2) {
            i1 = adr1 + vars1 - 1; // сравним j1-й j2-й мономы:
            i2 = adr2 + vars2 - 1; // запишем в i1 и i2 адреса последних
            // переменных
            i = vars1 - 1;

            for (int k = 0; k < vars1 - vars2; k++) {
                if (powers1[i1] != 0) {
                    break;
                }
                i1--;
                i--;
            }
            if (i > vars2 - 1) {

                // построить результат:
                // остаток p1, остаток p2, уже готовая часть разности
                int[] respowers = new int[adr1 - pb1
                        + (j2 - cb2 + monNumb - j + 1) * vars1];
                System.arraycopy(powers1, pb1, respowers, 0, adr1 - pb1 + vars1);

                int resAdr = adr1 - pb1 + vars1;
                int a2 = pb2;
                for (int k = 0; k < j2 - cb2 + 1; k++) {
                    for (int v = 0; v < vars2; v++) {
                        respowers[resAdr++] = powers2[a2++];
                    }
                    resAdr += vars1 - vars2;
                }
                System.arraycopy(pows, adr + vars1, respowers, adr1 - pb1
                        + (j2 - cb2 + 2) * vars1, (monNumb - j - 1) * vars1);

                Element[] rescoeffs = new Element[j1 - cb1 + j2 - cb2 + monNumb
                        - j + 1];
                System.arraycopy(coeffs1, cb1, rescoeffs, 0, j1 - cb1 + 1);
                for (int k = 0; k < j2 - cb2 + 1; k++) {
                    rescoeffs[j1 - cb1 + 1 + k] = coeffs2[cb2 + k].negate(ring);
                }
                System.arraycopy(cfs, j + 1, rescoeffs,
                        j1 - cb1 + j2 - cb2 + 2, monNumb - j - 1);

                return new Polynom(respowers, rescoeffs);
            }

            while (i >= 0 && powers1[i1] == powers2[i2]) {
                i--;
                i1--;
                i2--;
            }
            if (i == -1) { // если степени мономов равны, то
                cfs[j] = coeffs1[j1].subtract(coeffs2[j2], ring); // вычесть их
                // коэффициенты
                if (cfs[j].isZero(ring)) {
                    // если их разность = 0, то увеличить номер монома
                    // разности(в конце он уменьшится),
                    // т.е. он не изменится
                    j++;
                    adr += vars1;

                } else {
                    // иначе если сумма коэффициентов не равна 0, то
                    // копировать степени мономов в разность по адресу adr
                    System.arraycopy(powers1, adr1, pows, adr, vars2);
                }
                j1--;
                adr1 -= vars1;
                j2--;
                adr2 -= vars2;

            } else if (powers1[i1] < powers2[i2]) {

                cfs[j] = coeffs1[j1];
                System.arraycopy(powers1, adr1, pows, adr, vars2);
                j1--;
                adr1 -= vars1;

            } else {
                cfs[j] = coeffs2[j2].negate(ring);
                System.arraycopy(powers2, adr2, pows, adr, vars2);
                j2--;
                adr2 -= vars2;
            }
            j--; // перейти к следующему моному в разности
            adr -= vars1;

        }

        if (j1 >= cb1) {
            // построим результат из остатка p1 и разности
            int[] respowers = new int[adr1 - pb1 + (monNumb - j) * vars1];
            System.arraycopy(powers1, pb1, respowers, 0, adr1 - pb1 + vars1);
            System.arraycopy(pows, adr + vars1, respowers, adr1 - pb1 + vars1,
                    (monNumb - j - 1) * vars1);

            Element[] rescoeffs = new Element[j1 - cb1 + monNumb - j];
            System.arraycopy(coeffs1, cb1, rescoeffs, 0, j1 - cb1 + 1);
            System.arraycopy(cfs, j + 1, rescoeffs, j1 - cb1 + 1, monNumb - j
                    - 1);
            return new Polynom(respowers, rescoeffs); // вернуть разность
        }

        if (j2 >= cb2) {
            // построим результат из остатка p2 и разности
            int[] respowers = new int[(monNumb - j + j2 - cb2) * vars1];
            int resAdr = 0;
            int a2 = pb2;
            for (int k = 0; k < j2 - cb2 + 1; k++) {
                for (int v = 0; v < vars2; v++) {
                    respowers[resAdr++] = powers2[a2++];
                }
                resAdr += vars1 - vars2;
            }
            System.arraycopy(pows, adr + vars1, respowers, (j2 - cb2 + 1)
                    * vars1, (monNumb - j - 1) * vars1);

            Element[] rescoeffs = new Element[monNumb - j + j2 - cb2];
            for (int k = 0; k < j2 - cb2 + 1; k++) {
                rescoeffs[k] = coeffs2[k + cb2].negate(ring);
            }
            System.arraycopy(cfs, j + 1, rescoeffs, j2 - cb2 + 1, monNumb - j
                    - 1);
            return new Polynom(respowers, rescoeffs).truncate(); // вернуть
            // сумму
        }

        // j1=cb1-1 и j2=cb2-1, то проверим равна ли разность 0
        if (j == monNumb - 1) {
            // если указатель j остался равным monNumb-1, то все разности
            // коэффициентов были равны 0, то разность полиномов равна 0
            return new Polynom(new int[0], new Element[0]);

        } else { // иначе скопировать массивы pows и cfs в новые

            int[] respowers = new int[(monNumb - j - 1) * vars1];
            System.arraycopy(pows, adr + vars1, respowers, 0, respowers.length);
            Element[] rescoeffs = new Element[monNumb - j - 1];
            System.arraycopy(cfs, j + 1, rescoeffs, 0, rescoeffs.length);
            return new Polynom(respowers, rescoeffs).truncate(); // вернуть
            // сумму
        }
    }

    /*
     * powBinS - возводит полином типа Polynom в числовую степень типа int и
     * возвращает полином типа Polynom Степень вычисляется по бинарному
     * алгоритму. Параметры: power - степень, в которую возводим полином this.
     */
    public Polynom powBinS(int power, Ring ring) {
        Element one = one(ring);
        Polynom y = Polynom.polynomFromNot0Number(one);
        if (power == 0) return y;
        if (coeffs.length == 0) return Polynom.polynomZero;
        if (powers.length == 0) 
            return new Polynom(powers, new Element[] {coeffs[0].pow(power,ring)});
        if (power == 1) return this;
        if (coeffs.length == 1) {
            int vars = powers.length / coeffs.length;
            int[] resPowers = new int[vars];
            for (int v = 0; v < vars; v++)  resPowers[v] = powers[v] * power;
            return new Polynom(resPowers, new Element[] {coeffs[0].pow(power, ring)});
        }
        Polynom z = this;
        while (power > 0) {
            if ((power & 1) == 1) { y = y.mulSS(z, ring);
                power--;
            } else { z = z.mulSS(z, ring);
                power >>>= 1;
            }
        }
        return y;
    }

 
    /** Recursive procedure that raises this polynomial in the integer degree.
     * Возводит полином типа Polynom в числовую степень типа int и
     * Параметры: power - степень, в которую возводится полином  
     * @param power  - степень, в которую возводится полином
     * @param ring    -- Ring
     * @return    -- Polynom
     */
    public Polynom powRecS(int power, Ring ring) {
        Polynom v = polynom_one(NumberZ.ONE);
        // for(int i=0; i<power; i++){v=v.mulSS(this, ring);}
        // return v;
        Element one = one(ring);
        if (power == 0) {
            return new Polynom(new int[0],
                    new Element[] {one});
        }
        if (coeffs.length <= 3) {
            return powNewS(power, ring);
        } else {
            if (power == 1) {
                return this;
            }
            int vars = powers.length / coeffs.length;
            // Делим полином this на 2 части: p1 и p2
            int p1Len = coeffs.length >>> 1;
            Polynom p1 = new Polynom(new int[p1Len * vars], new Element[p1Len]);
            System.arraycopy(powers, 0, p1.powers, 0, p1Len * vars);
            System.arraycopy(coeffs, 0, p1.coeffs, 0, p1Len);
            int p2Len = coeffs.length - p1Len;
            Polynom p2 = new Polynom(new int[p2Len * vars], new Element[p2Len]);
            System.arraycopy(powers, p1.powers.length, p2.powers, 0, p2Len
                    * vars);
            System.arraycopy(coeffs, p1Len, p2.coeffs, 0, p2Len);
            // Возведем p1 в степень power, это и будет начальным значением
            // полинома
            // результата result
            Polynom result = p1.powRecS(power, ring);
            // Добавим к result полином p2 в степени power
            result = result.add(p2.powRecS(power, ring), ring);
            // Теперь добавим к result остальные (power-1) слагаемых вида:
            // soch(power,k)*p1^k*p2^{power-k}
            Polynom item;
            Element sochPowerK;
            int typeCoeff = coeffs[0].numbElementType();
            for (int k = 1; k < power; k++) {
                item = p1.powRecS(k, ring).mulSS(p2.powRecS(power - k, ring),
                        ring);
                sochPowerK = PascalTriangle.binomialZ(power, k).toNewRing(typeCoeff, ring);
                // Бином Ньютона !!
                for (int i = 0; i < item.coeffs.length; i++) {
                    item.coeffs[i] = item.coeffs[i].multiply(sochPowerK, ring);
                }
                result = result.add(item, ring);
            }
            return result;
        }
    }

    /*
     * powRecX - возводит полином типа Polynom в числовую степень типа int и
     * возвращает полином типа Polynom Параметры: power - степень, в которую
     * возводим полином this.
     */
    public Polynom powRecSX(int power, Ring ring) throws Exception {
        Element one = one(ring);
        if (power == 0) {
            if (coeffs.length == 0) {
                // 0^0
                return new Polynom(powers, new Element[] {one});
            } else {
                // c^0, p^0
                return new Polynom(new int[powers.length / coeffs.length],
                        new Element[] {one});
            }
        }
        if (coeffs.length <= 3) {
            return powNewS(power, ring);
        } else {
            if (power == 1) {
                return this;
            }
            int vars = powers.length / coeffs.length;
            // Делим полином this на 2 части: p1 и p2
            int p1Len = coeffs.length >>> 1;
            Polynom p1 = new Polynom(new int[p1Len * vars], new Element[p1Len]);
            System.arraycopy(powers, 0, p1.powers, 0, p1Len * vars);
            System.arraycopy(coeffs, 0, p1.coeffs, 0, p1Len);
            int p2Len = coeffs.length - p1Len;
            Polynom p2 = new Polynom(new int[p2Len * vars], new Element[p2Len]);
            System.arraycopy(powers, p1.powers.length, p2.powers, 0, p2Len
                    * vars);
            System.arraycopy(coeffs, p1Len, p2.coeffs, 0, p2Len);
            // Начальное значение полинома p1pow=p1^power, полинома p2pow=1,
            // а полинома результата result=p1pow
            Polynom p1pow = p1.powRecSX(power, ring);
            Polynom p2pow = new Polynom(new int[vars], new Element[1]);
            p2pow.coeffs[0] = one;
            Polynom result = p1pow;
            // Теперь добавим к result power слагаемых вида:
            // soch(power,k)*p1^k*p2^{power-k}, k=1,...,power
            Polynom item;
            Element sochPowerK;
            for (int k = 1; k <= power; k++) {
                p1pow = p1pow.divideExactWithCheck(p1, ring);
                p2pow = p2pow.mulSS(p2, ring);
                item = p1pow.mulSS(p2pow, ring);
                sochPowerK = coeffs[0].toNumber(
                        PascalTriangle.binomialZ(power, k).numbElementType(),
                        ring);
                for (int i = 0; i < item.coeffs.length; i++) {
                    item.coeffs[i] = item.coeffs[i].multiply(sochPowerK, ring);
                }
                result = result.add(item, ring);
            }
            return result;
        }
    }

    /*
     * powNewS - возводит полином типа Polynom в числовую степень типа int и
     * возвращает полином типа Polynom Параметры: power - степень, в которую
     * возводим полином this.
     */
    public Polynom powNewS(int power, Ring ring) {
        Element one = one(ring);
        if (power == 0) {
            return new Polynom(new int[0],
                    new Element[] {one});
        }
        int monoms = coeffs.length;
        if (monoms == 0) {
            return new Polynom(new int[0], new Element[0]);
        }
        int vars = powers.length / monoms;
        if (vars == 0) {
            return new Polynom(powers, new Element[] {coeffs[0].pow(power, ring)});
        }
        if (power == 1) {
            return this;
        }
        if (monoms == 1) {
            int[] resPowers = new int[vars];
            for (int v = 0; v < vars; v++) {
                resPowers[v] = powers[v] * power;
            }
            return new Polynom(resPowers, new Element[] {coeffs[0].pow(power, ring)});
        }
        PascalTriangle.growPasTrgZ(power); // вычислим все невычисленные
        // строки кэша
        // до номера power включительно
        int[] k = new int[monoms + 1]; // массив индексов суммирования
        // k[0]=k(var)=power,k[1]=k(var-1), ..., k[var-1]=k(1)
        k[0] = power; // оставим постоянными k[0]=k(var)=power, k[var]=0
        // а остальные k(i) будем менять от 0 до power
        int j;
        int resultMonoms = 0; // число мономов в результирующем полиноме
        // Переберем все k(i) от 0 до k так, чтобы
        // k(i+1)-k(i) было от 0 до k для любого i=0,...,var и
        // (k-k(var-1))+...(k(2)-k(1))+k(1)=k
        do {
            resultMonoms++; // посчитаем число наборов k(i), это и будет
            // число мономов в результате
            // перейдем к следующему набору k(i)
            j = monoms - 1;
            while (j > 1 && k[j] == k[j - 1]) { // для этого будем двигаться
                // справа налево начиная
                // с k[var-1]=k(1) пока разности k[j]-k[j-1]=0
                j--;
            }
            // остановимся на ненулевой разности k[j]-k[j-1] или на j=1(т.е. на
            // k(var-1))
            if (k[j] > 0) { // если остановились на ненулевом k[j], то
                for (int i = j + 1; i < monoms; i++) { // обнулим все k[i] от
                    // j+1 до var-1
                    k[i] = 0;
                }
            }
            k[j]++; // и увеличим k[j]
        } while (k[1] <= power); // повторять пока k[1]<=power
        k[1] = 0; // k[0]=power и все k[i]=0 i=1,...,var
        // создаем массивы степеней и коэффициентов результирующего полинома,
        // содержащего resultMonoms мономов
        int[] resultPowers = new int[resultMonoms * vars];
        Element[] resultCoeffs = new Element[resultMonoms];
        int pow; // временная переменная для хранения степени k(i)-k(i-1)
        int resultMonNumbAdr = 0; // адрес монома resultMonNumb в массиве
        // степеней
        // результирующего полинома
        for (int resultMonNumb = 0; resultMonNumb < resultMonoms; resultMonNumb++) {
            // вычислим все мономы результата от 0 до resultMonoms-1
            // начальное значение коэффициента монома = 1
            resultCoeffs[resultMonNumb] = one;
            int thisMonAdr = 0; // адрес i-го монома this полинома
            // моном результирующего полинома = произведение всех i-х мономов в
            // степени k[i]-k[i+1]
            for (int i = 0; i < monoms; i++) {
                pow = k[i] - k[i + 1];
                // коэффициент монома результирующего полинома = произведение
                // всех
                // коэффициентов i-х мономов в степени k[i]-k[i+1]
                resultCoeffs[resultMonNumb] = resultCoeffs[resultMonNumb].
                        multiply(coeffs[i].pow(pow, ring), ring);
                // и умножить коэффициент монома результирующего полинома на
                // soch(k[i],k[i+1]) при i от 0 до monoms-1
                resultCoeffs[resultMonNumb] = resultCoeffs[resultMonNumb].
                        multiply(PascalTriangle.binomialZ(k[i], k[i + 1]), ring);
//                        resultCoeffs[resultMonNumb].multiply(coeffs[0].toNumber(
//                        PascalTriangle.binomialZ(k[i], k[i + 1]).numbElementType(), ring), ring);
                // степень v-й переменной монома результирующего полинома =
                // сумме
                // (степеней v-х переменных i-х мономов)*(k[i]-k[i+1])
                for (int v = 0; v < vars; v++) {
                    resultPowers[resultMonNumbAdr + v] += powers[thisMonAdr++]
                            * pow;
                }
            }
            // переходим к следующему моному результирующегpowNewSо полинома
            resultMonNumbAdr += vars;
            // и переходим к следующему набору индексов k(i)
            j = monoms - 1;
            while (j > 1 && k[j] == k[j - 1]) {
                j--;
            }
            if (k[j] > 0) {
                for (int i = j + 1; i < monoms; i++) {
                    k[i] = 0;
                }
            }
            k[j]++;
        }
        // создадим и упорядочим результирующий полином
        Polynom resultPolynom = new Polynom(resultPowers, resultCoeffs);
        resultPolynom.ordering(0, 0, resultPolynom.coeffs.length, ring);
        // возвращаем результирующий полином
        return resultPolynom;
    }

    /**
     * Отсортировать по убыванию весь полином (сортировка слиянием) и записать
     * отсортиртированную часть полинома this вместо полинома this, т.е.
     * записать вместо powers и coeffs отсортированные части powers и coeffs.
     */
    public Polynom ordering(Ring ring) {
        int len = coeffs.length;
        int sortMethod = 0;
        //  Если sortMethod =0, то вызывается сортировка слиянием (по умолчанию),
        // Если sortMethod = 1, то вызывается быстрая сортировка.
        return (ordering(sortMethod, 0, len, ring));
    }

    /**
     * Отсортировать по убыванию часть полинома this от монома beg до монома end
     * (не включая end) и записать отсортиртированную часть полинома this вместо
     * полинома this, т.е. записать вместо powers и coeffs отсортированные части
     * powers и coeffs.
     *
     * Параметры: this - полином, который нужно отсортировать Если sortMethod =
     * 0, то вызывается сортировка слиянием (по умолчанию), Если sortMethod = 1,
     * то вызывается быстрая сортировка. beg - начальный моном end - конечный
     * моном+1
     */
    public Polynom ordering(int sortMethod, int beg, int end, Ring ring) {
        if (end - beg > 1) { // если p содержит более 1 монома, то
            if (sortMethod == 0) {
                mergeSort(beg, end, ring);
            } else {
                quickSort(beg, end, ring);
            }
        } else {
            // иначе если end-beg==1, то записать в this моном номер beg
            // если beg=0, end=кол-во коэффициентов, то ничего не делать
            if (beg != 0 || end != coeffs.length) {
                int vars = powers.length / coeffs.length;
                int[] pows = new int[vars];
                System.arraycopy(powers, beg * vars, pows, 0, vars);
                powers = pows;
                coeffs = new Element[] {coeffs[beg]};
            }
        }
        return this;
    }

    /**
     * quickSort упорядочивает полином по убыванию методом быстрой сортировки
     * После сортировки проходим по всему списку и складываем мономы одинакового
     * веса.
     *
     * Параметры: this - полином, который нужно отсортировать beg - начальный
     * моном end - конечный моном+1
     */
    private void quickSort(int beg, int end, Ring ring) {
        int fvars = powers.length / coeffs.length;
        int lastvar = fvars - 1; // lastvar=адрес последней переменной в мономе
        int[] xpow = new int[fvars]; // xpow - массив для хранения монома в
        // центре
        // полинома
        Element xcoeff; // xcoeff - его коэффициент
        int j1; // индекс монома слева от xpow
        int adr1; // adr1=j1*vars
        int j2; // индекс монома справа от xpow
        int adr2; // adr2=j2*vars
        int k1; // k1,k2 - адреса для сравнения мономов
        int k2;
        int i; // i - номер сравниваемой переменной
        int l = beg; // l - левая граница сортировки
        int r = end - 1; // r - правая граница сортировки (включительно!)
        int[] stack = new int[(end - beg) * 2]; // стек для запоминания 2-х
        // границ для
        // следующей сортировки
        int s = 0; // запишем в 0-й элемент стека границы beg и end
        stack[0] = l;
        stack[1] = r;
        do {
            l = stack[s]; // извлечь из стека следующие границы области
            // сортировки
            r = stack[s + 1];
            s -= 2;
            do {
                j1 = l; // установим j1=l - левая граница
                adr1 = j1 * fvars;
                j2 = r; // j2=r - правая граница
                adr2 = j2 * fvars;
                int m = (l + r) / 2; // m - середина области сортировки
                int adrm = m * fvars;
                for (int v = 0; v < fvars; v++) { // скопировать моном m в x
                    // (xpow и xcoeff)
                    xpow[v] = powers[adrm + v];
                }
                xcoeff = coeffs[m];
                do {
                    i = lastvar; // сравним степени мономов j1 и x
                    k1 = adr1 + lastvar;

                    while (i >= 0 && powers[k1] == xpow[i]) {
                        i--;
                        k1--;
                    }

                    while (i != -1 && powers[k1] > xpow[i]) { // while (степени
                        // j1>степеней
                        // x){
                        j1++; // j1++;}
                        adr1 += fvars;
                        i = lastvar; // сравним степени мономов j1 и x
                        k1 = adr1 + lastvar;
                        while (i >= 0 && powers[k1] == xpow[i]) {
                            i--;
                            k1--;
                        }
                    }
                    i = lastvar; // сравним степени мономов j2 и x
                    k1 = adr2 + lastvar;
                    while (i >= 0 && powers[k1] == xpow[i]) {
                        i--;
                        k1--;
                    }

                    while (i != -1 && powers[k1] < xpow[i]) { // while (степени
                        // j2<степней
                        // x){
                        j2--; // j2--;}
                        adr2 -= fvars;
                        i = lastvar; // сравним степени мономов j2 и x
                        k1 = adr2 + lastvar;
                        while (i >= 0 && powers[k1] == xpow[i]) {
                            i--;
                            k1--;
                        }
                    }
                    if (j1 <= j2) { // если j1<=j2, то
                        for (int v = 0; v < fvars; v++) { // поменять местами
                            int w = powers[adr1 + v]; // мономы j1 и j2
                            powers[adr1 + v] = powers[adr2 + v];
                            powers[adr2 + v] = w;
                        }
                        Element wcoeff = coeffs[j1];
                        coeffs[j1] = coeffs[j2];
                        coeffs[j2] = wcoeff;
                        j1++;
                        adr1 += fvars;
                        j2--;
                        adr2 -= fvars;
                    }
                } while (j1 <= j2); // выйти из цикла, если j1>j2
                // теперь слева от монома x: мономы степеней < степенни x,
                // справа от монома x: мономы степеней > степени x.
                // Осталось отсортировать мономы в левой части и в правой части
                if (j1 < r) { // если правая часть не пустая, то
                    s += 2; // записать ее границы в стек (правую часть мы
                    stack[s] = j1; // отсортируем после того как отсортируем
                    // левую часть)
                    stack[s + 1] = r;
                }
                r = j2; // и начинаем сортировку левой части
            } while (l < r); // если l<r (левая часть не пустая), то
            // сортируем левую часть;
            // иначе если l>r (левая часть пустая), то
        } while (s != -2); // если s!=-2 (стек не пуст), то
        // переходим на начало и извлекаем из стека правую
        // часть и отсортируем ее
        // иначе если s=-2 (стек пуст), то конец сортировки

        // Сложим мономы с одинаковыми степенями
        j1 = beg;
        adr1 = j1 * fvars;
        j2 = beg + 1;
        adr2 = j2 * fvars;
        while (j2 < end - beg) { // повторять пока не обработаем весь массив
            // pows
            i = lastvar; // сравним степени мономов j1 и j2
            k1 = adr1 + lastvar;
            k2 = adr2 + lastvar;
            while (i >= 0 && powers[k1] == powers[k2]) {
                i--;
                k1--;
                k2--;
            }
            if (i == -1) { // если степени монома j2 = степеням j1, то
                coeffs[j1] = coeffs[j1].add(coeffs[j2], ring); // добавить j2 к
                // j1
            } else { // иначе если степени монома j2 != степеням j1, то
                if (coeffs[j1].signum() != 0) { // если в j1
                    j1++; // накопился не 0, то запишем
                    adr1 += fvars;
                } // моном j2 в j1+1, иначе запишем
                coeffs[j1] = coeffs[j2]; // моном j2 поверх монома j1
                for (int v = 0; v < fvars; v++) {
                    powers[adr1 + v] = powers[adr2 + v];
                }
            }
            j2++; // j2=j2+1
            adr2 += fvars;
        }
        int monoms = j1; // monoms = число ненулевых мономов
        if (coeffs[j1].signum() != 0) {
            monoms++;
        }
        if (monoms == 0) { // если число ненулевых мономов = 0, то
            powers = new int[0]; // возвращаем ZERO
            coeffs = new Element[0];
        } else {
            // иначе создаем массив степеней из массива a и
            // копируем cfs в массив коэффициентов
            // По получившемуся старшему моному определим реальное кол-во
            // переменных.
            int rvars = getRealVars(powers, beg * fvars, fvars);
            int[] newpowers = new int[monoms * rvars]; // размера monoms
            int indxAdr = 0; // адрес в newpowers
            int adr = beg * fvars;
            for (int k = 0; k < monoms; k++) {
                for (int v = 0; v < rvars; v++) { // копируем из powers в
                    // newpowers
                    newpowers[indxAdr++] = powers[adr++];
                }
                adr += fvars - rvars;
            }
            powers = newpowers;
            Element[] newcoeffs = new Element[monoms]; // копируем monoms
            // коэффициентов
            System.arraycopy(coeffs, beg, newcoeffs, 0, monoms); // из coeffs в
            // newcoeffs
            coeffs = newcoeffs;
        }
    }

    /*
     * mergeSort - сортировка слиянием части полинома от beg до end по убыванию.
     * После сортировки проходим по всему списку и складываем мономы одинакового
     * веса.
     *
     * Параметры: this - полином, который нужно отсортировать beg - начальный
     * моном end - конечный моном+1
     */
    private void mergeSort(int beg, int end, Ring ring) {
        int fvars = powers.length / coeffs.length;
        int lastvar = fvars - 1; // lastvar=адрес последней переменной в мономе
        int j1 = 0; // адрес 1-го элемента 1-й части
        int adr1 = 0; // adr1=j1*vars
        int j2 = 0; // адрес 1-го элемента 2-й части
        int adr2 = 0; // adr2=j2*vars
        int k1; // k1,k2 - адреса для сравнения мономов
        int k2;
        int i; // i - номер сравниваемой переменной
        int[] a = new int[end - beg]; // создадим входной и выходной массивы,
        int[] b = new int[end - beg]; // содержащие адреса мономов
        int[] temp; // промежуточная переменная для обмена
        // значений a и b
        int adr = beg * fvars;
        for (int k = 0; k < end - beg; k++) { // запишем в a адреса всех мономов
            // 0,vars,2*vars,...
            a[k] = adr;
            adr += fvars;
        }
        int partSize = 1; // размер частей для слияния 1,2,4,8,16,...
        int monomsLeft; // количество мономов, которые осталось слить
        int part1Size; // размер 1-й части
        int part2Size; // размер 2-й части
        int outAdr; // индекс в выходном массиве, куда записывается адрес
        // большего монома при слияний частей
        do {
            monomsLeft = end - beg;
            j1 = 0;
            outAdr = 0;
            do {
                if (monomsLeft <= partSize) { // если мономов осталось < размера
                    // частей,
                    part1Size = monomsLeft; // то размер 1-й части=числу
                    // оставшихся
                    // мономов
                } else {
                    part1Size = partSize; // иначе размер 1-й части=размеру
                    // частей
                }
                monomsLeft -= part1Size; // уменьшим число оставшихся мономов
                j2 = j1 + part1Size; // j2 - начало 2-й части=адрес после 1-й
                // части
                if (monomsLeft <= partSize) { // если мономов осталось < размера
                    // частей,
                    part2Size = monomsLeft; // то размер 2-й части=числу
                    // оставшихся
                    // мономов
                } else {
                    part2Size = partSize; // иначе размер 2-й части=размеру
                    // частей
                }
                monomsLeft -= part2Size; // уменьшим число оставшихся мономов
                while (part1Size != 0 && part2Size != 0) { // повторять пока
                    // есть элементы
                    // в обеих частях
                    k1 = a[j1] + lastvar; // сравним степени мономов по адресу
                    // a[j1]
                    k2 = a[j2] + lastvar; // и a[j2]
                    i = lastvar;
                    while (i >= 0 && powers[k1] == powers[k2]) {
                        i--;
                        k1--;
                        k2--;
                    }
                    if (i == -1) { // если степени мономов a[j1] и a[j2] равны,
                        // то
                        b[outAdr++] = a[j1++]; // запишем их адреса в выходной
                        // массив b
                        part1Size--; // уменьшим размеры 1-й и 2-й части и
                        b[outAdr++] = a[j2++]; // перейдем к мономам j1+1 и j2+1
                        part2Size--;
                    } else {
                        if (powers[k1] > powers[k2]) { // наче если степень
                            // монома a[j1]>
                            // степени монома a[j2], то
                            b[outAdr++] = a[j1++]; // записать адрес a[j1] в b
                            part1Size--; // и перейти к j1+1
                        } else { // наче если степень монома a[j1] <
                            // степени монома a[j2], то
                            b[outAdr++] = a[j2++]; // записать адрес a[j2] в b
                            part2Size--; // и перейти к j2+1
                        }
                    }
                }

                while (part1Size != 0) { // если остались адреса в 1-й части, то
                    b[outAdr++] = a[j1++]; // скопировать их в b
                    part1Size--;
                }

                while (part2Size != 0) { // если остались адреса в 2-й части, то
                    b[outAdr++] = a[j2++]; // скопировать их в b
                    part2Size--;
                }
                j1 = j2; // начало следующей 1-й части=адрес после
                // 2-й части
            } while (monomsLeft > 0); // повторять сляние частей размером по
            // partSize
            partSize *= 2; // увеличим размер сливаемых частей в 2 раза
            temp = a;
            a = b;
            b = temp;
        } while (partSize < end - beg); // повторять пока этот размер<числа
        // мономов
        // Преобразуем массив адресов в массив коэффициентов
        // результат находится в a
        Element[] cfs = new Element[end - beg];
        for (int k = 0; k < end - beg; k++) { // преобразуем каждый адрес a[k] в
            // коэффициент
            cfs[k] = coeffs[a[k] / fvars]; // копируем коэффициент из coeffs в
            // cfs
        }
        // Конец сортировки
        // Сложим мономы с одинаковыми степенями
        j1 = 0;
        j2 = 1;
        while (j2 < end - beg) { // повторять пока не обработаем весь массив
            // pows
            i = lastvar; // сравним степени мономов j1 и j2
            k1 = a[j1] + lastvar;
            k2 = a[j2] + lastvar;
            while (i >= 0 && powers[k1] == powers[k2]) {
                i--;
                k1--;
                k2--;
            }
            if (i == -1) { // если степени монома j2 = степеням j1, то
                cfs[j1] = cfs[j1].add(cfs[j2], ring); // добавить j2 к j1
            } else { // иначе если степени монома j2 != степеням j1, то
                if (cfs[j1].signum() != 0) { // если в j1
                    j1++; // накопился не 0, то запишем
                } // моном j2 в j1+1, иначе запишем
                cfs[j1] = cfs[j2]; // моном j2 поверх монома j1
                a[j1] = a[j2];
            }
            j2++; // j2=j2+1
        }
        int monoms = j1; // monoms = число ненулевых мономов
        if (cfs[j1].signum() != 0) {
            monoms++;
        }
        if (monoms == 0) { // если число ненулевых мономов = 0, то
            powers = new int[0]; // возвращаем ZERO
            coeffs = new Element[0];
        } else {
            // иначе создаем массив степеней из массива a и
            // копируем cfs в массив коэффициентов
            // По получившемуся старшему моному определим реальное кол-во
            // переменных.
            int rvars = getRealVars(powers, a[0], fvars);
            int[] newpowers = new int[monoms * rvars]; // размера monoms
            int indxAdr = 0; // адрес в newpowers
            for (int k = 0; k < monoms; k++) { // преобразуем каждый адрес a[k]
                // в моном
                adr = a[k]; // adr=адрес монома в powers
                for (int v = 0; v < rvars; v++) { // копируем его из powers в
                    // newpowers
                    newpowers[indxAdr++] = powers[adr++];
                }
            }
            powers = newpowers;
            coeffs = new Element[monoms]; // копируем monoms коэффициентов
            System.arraycopy(cfs, 0, coeffs, 0, monoms); // из cfs в coeffs
        }
    }

    /*
     * quickSort упорядочивает полином по убыванию методом быстрой сортировки
     * После сортировки проходим по всему списку и складываем мономы одинакового
     * веса.
     *
     * Параметры: this - полином, который нужно отсортировать beg - начальный
     * моном end - конечный моном+1 mod - модуль кольца Z_p
     */
    private void quickSort(int beg, int end, Element mod, Ring ring) {
        int fvars = powers.length / coeffs.length;
        int lastvar = fvars - 1; // lastvar=адрес последней переменной в мономе
        int[] xpow = new int[fvars]; // xpow - массив для хранения монома в
        // центре
        // полинома
        Element xcoeff; // xcoeff - его коэффициент
        int j1; // индекс монома слева от xpow
        int adr1; // adr1=j1*vars
        int j2; // индекс монома справа от xpow
        int adr2; // adr2=j2*vars
        int k1; // k1,k2 - адреса для сравнения мономов
        int k2;
        int i; // i - номер сравниваемой переменной
        int l = beg; // l - левая граница сортировки
        int r = end - 1; // r - правая граница сортировки (включительно!)
        int[] stack = new int[(end - beg) * 2]; // стек для запоминания 2-х
        // границ для
        // следующей сортировки
        int s = 0; // запишем в 0-й элемент стека границы beg и end
        stack[0] = l;
        stack[1] = r;
        do {
            l = stack[s]; // извлечь из стека следующие границы области
            // сортировки
            r = stack[s + 1];
            s -= 2;
            do {
                j1 = l; // установим j1=l - левая граница
                adr1 = j1 * fvars;
                j2 = r; // j2=r - правая граница
                adr2 = j2 * fvars;
                int m = (l + r) / 2; // m - середина области сортировки
                int adrm = m * fvars;
                for (int v = 0; v < fvars; v++) { // скопировать моном m в x
                    // (xpow и xcoeff)
                    xpow[v] = powers[adrm + v];
                }
                xcoeff = coeffs[m];
                do {
                    i = lastvar; // сравним степени мономов j1 и x
                    k1 = adr1 + lastvar;

                    while (i >= 0 && powers[k1] == xpow[i]) {
                        i--;
                        k1--;
                    }

                    while (i != -1 && powers[k1] > xpow[i]) { // while (степени
                        // j1>степеней
                        // x){
                        j1++; // j1++;}
                        adr1 += fvars;
                        i = lastvar; // сравним степени мономов j1 и x
                        k1 = adr1 + lastvar;
                        while (i >= 0 && powers[k1] == xpow[i]) {
                            i--;
                            k1--;
                        }
                    }
                    i = lastvar; // сравним степени мономов j2 и x
                    k1 = adr2 + lastvar;
                    while (i >= 0 && powers[k1] == xpow[i]) {
                        i--;
                        k1--;
                    }

                    while (i != -1 && powers[k1] < xpow[i]) { // while (степени
                        // j2<степней
                        // x){
                        j2--; // j2--;}
                        adr2 -= fvars;
                        i = lastvar; // сравним степени мономов j2 и x
                        k1 = adr2 + lastvar;
                        while (i >= 0 && powers[k1] == xpow[i]) {
                            i--;
                            k1--;
                        }
                    }
                    if (j1 <= j2) { // если j1<=j2, то
                        for (int v = 0; v < fvars; v++) { // поменять местами
                            int w = powers[adr1 + v]; // мономы j1 и j2
                            powers[adr1 + v] = powers[adr2 + v];
                            powers[adr2 + v] = w;
                        }
                        Element wcoeff = coeffs[j1];
                        coeffs[j1] = coeffs[j2];
                        coeffs[j2] = wcoeff;
                        j1++;
                        adr1 += fvars;
                        j2--;
                        adr2 -= fvars;
                    }
                } while (j1 <= j2); // выйти из цикла, если j1>j2
                // теперь слева от монома x: мономы степеней < степенни x,
                // справа от монома x: мономы степеней > степени x.
                // Осталось отсортировать мономы в левой части и в правой части
                if (j1 < r) { // если правая часть не пустая, то
                    s += 2; // записать ее границы в стек (правую часть мы
                    stack[s] = j1; // отсортируем после того как отсортируем
                    // левую часть)
                    stack[s + 1] = r;
                }
                r = j2; // и начинаем сортировку левой части
            } while (l < r); // если l<r (левая часть не пустая), то
            // сортируем левую часть;
            // иначе если l>r (левая часть пустая), то
        } while (s != -2); // если s!=-2 (стек не пуст), то
        // переходим на начало и извлекаем из стека правую
        // часть и отсортируем ее
        // иначе если s=-2 (стек пуст), то конец сортировки

        // Сложим мономы с одинаковыми степенями
        j1 = beg;
        adr1 = j1 * fvars;
        j2 = beg + 1;
        adr2 = j2 * fvars;
        coeffs[j1] = coeffs[j1].mod(mod, ring);
        while (j2 < end - beg) { // повторять пока не обработаем весь массив
            // pows
            i = lastvar; // сравним степени мономов j1 и j2
            k1 = adr1 + lastvar;
            k2 = adr2 + lastvar;
            while (i >= 0 && powers[k1] == powers[k2]) {
                i--;
                k1--;
                k2--;
            }
            if (i == -1) { // если степени монома j2 = степеням j1, то
                coeffs[j1] = (coeffs[j1].add(coeffs[j2], ring)).mod(mod, ring); // добавить
                // j2
                // к
                // j1
            } else { // иначе если степени монома j2 != степеням j1, то
                if (!coeffs[j1].isZero(ring)) { // если в j1
                    j1++; // накопился не 0, то запишем
                    adr1 += fvars;
                } // моном j2 в j1+1, иначе запишем
                coeffs[j1] = coeffs[j2].mod(mod, ring); // моном j2 поверх
                // монома j1
                for (int v = 0; v < fvars; v++) {
                    powers[adr1 + v] = powers[adr2 + v];
                }
            }
            j2++; // j2=j2+1
            adr2 += fvars;
        }
        int monoms = j1; // monoms = число ненулевых мономов
        if (!coeffs[j1].isZero(ring)) {
            monoms++;
        }
        if (monoms == 0) { // если число ненулевых мономов = 0, то
            powers = new int[0]; // возвращаем ZERO
            coeffs = new Element[0];
        } else {
            // иначе создаем массив степеней из массива a и
            // копируем cfs в массив коэффициентов
            // По получившемуся старшему моному определим реальное кол-во
            // переменных.
            int rvars = getRealVars(powers, beg * fvars, fvars);
            int[] newpowers = new int[monoms * rvars]; // размера monoms
            int indxAdr = 0; // адрес в newpowers
            int adr = beg * fvars;
            for (int k = 0; k < monoms; k++) {
                for (int v = 0; v < rvars; v++) { // копируем из powers в
                    // newpowers
                    newpowers[indxAdr++] = powers[adr++];
                }
                adr += fvars - rvars;
            }
            powers = newpowers;
            Element[] newcoeffs = new Element[monoms]; // копируем monoms
            // коэффициентов
            System.arraycopy(coeffs, beg, newcoeffs, 0, monoms); // из coeffs в
            // newcoeffs
            coeffs = newcoeffs;
        }
    }

    /*
     * mergeSort - сортировка слиянием части полинома от beg до end по убыванию.
     * После сортировки проходим по всему списку и складываем мономы одинакового
     * веса.
     *
     * Параметры: this - полином, который нужно отсортировать beg - начальный
     * моном end - конечный моном+1 mod - модуль кольца Z_p
     */
    private void mergeSort(int beg, int end, Element mod, Ring ring) {
        int fvars = powers.length / coeffs.length;
        int lastvar = fvars - 1; // lastvar=адрес последней переменной в мономе
        int j1; // адрес 1-го элемента 1-й части
        int j2; // адрес 1-го элемента 2-й части
        int k1; // k1,k2 - адреса для сравнения мономов
        int k2;
        int i; // i - номер сравниваемой переменной
        int[] a = new int[end - beg]; // создадим входной и выходной массивы,
        int[] b = new int[end - beg]; // содержащие адреса мономов
        int[] temp; // промежуточная переменная для обмена значений a и b
        int adr = beg * fvars;
        for (int k = 0; k < end - beg; k++) {
            // запишем в a адреса всех мономов 0,vars,2*vars,...
            a[k] = adr;
            adr += fvars;
        }
        int partSize = 1; // размер частей для слияния 1,2,4,8,16,...
        int monomsLeft; // количество мономов, которые осталось слить
        int part1Size; // размер 1-й части
        int part2Size; // размер 2-й части
        int outAdr; // индекс в выходном массиве, куда записывается адрес
        // большего монома при слияний частей
        do {
            monomsLeft = end - beg;
            j1 = 0;
            outAdr = 0;
            do {
                if (monomsLeft <= partSize) {
                    // если мономов осталось < размера частей, то размер
                    // 1-й части=числу оставшихся мономов
                    part1Size = monomsLeft;
                } else {
                    // иначе размер 1-й части=размеру частей
                    part1Size = partSize;
                }
                monomsLeft -= part1Size; // уменьшим число оставшихся мономов
                // j2 - начало 2-й части=адрес после 1-й части
                j2 = j1 + part1Size;
                if (monomsLeft <= partSize) {
                    // если мономов осталось < размера частей, то размер
                    // 2-й части=числу оставшихся мономов.
                    part2Size = monomsLeft;
                } else {
                    // иначе размер 2-й части=размеру частей
                    part2Size = partSize;
                }
                monomsLeft -= part2Size; // уменьшим число оставшихся мономов
                // Повторять пока есть элементы в обеих частях.
                while (part1Size != 0 && part2Size != 0) {
                    // сравним степени мономов по адресу a[j1]...
                    k1 = a[j1] + lastvar;
                    k2 = a[j2] + lastvar; // ... и a[j2]
                    i = lastvar;
                    while (i >= 0 && powers[k1] == powers[k2]) {
                        i--;
                        k1--;
                        k2--;
                    }
                    if (i == -1) {
                        // если степени мономов a[j1] и a[j2] равны, то
                        // запишем их адреса в выходной массив b
                        b[outAdr++] = a[j1++];
                        part1Size--; // уменьшим размеры 1-й и 2-й части и
                        b[outAdr++] = a[j2++]; // перейдем к мономам j1+1 и j2+1
                        part2Size--;
                    } else {
                        if (powers[k1] > powers[k2]) {
                            // наче если степень
                            // монома a[j1]> степени монома a[j2], то
                            b[outAdr++] = a[j1++]; // записать адрес a[j1] в b
                            part1Size--; // и перейти к j1+1
                        } else {
                            // наче если степень монома a[j1] < степени монома a[j2], то
                            b[outAdr++] = a[j2++]; // записать адрес a[j2] в b
                            part2Size--; // и перейти к j2+1
                        }
                    }
                }

                while (part1Size != 0) { // если остались адреса в 1-й части, то
                    b[outAdr++] = a[j1++]; // скопировать их в b
                    part1Size--;
                }

                while (part2Size != 0) { // если остались адреса в 2-й части, то
                    b[outAdr++] = a[j2++]; // скопировать их в b
                    part2Size--;
                }
                j1 = j2; // начало следующей 1-й части=адрес после 2-й части
                // повторять сляние частей размером по partSize
            } while (monomsLeft > 0);
            partSize *= 2; // увеличим размер сливаемых частей в 2 раза
            temp = a;
            a = b;
            b = temp;
            // повторять пока этот размер меньше числа мономов.
        } while (partSize < end - beg);
        // Преобразуем массив адресов в массив коэффициентов
        // результат находится в a
        Element[] cfs = new Element[end - beg];
        for (int k = 0; k < end - beg; k++) {
            // преобразуем каждый адрес a[k] в коэффициент
            // копируем коэффициент из coeffs в cfs
            cfs[k] = coeffs[a[k] / fvars];
        }
        // Конец сортировки
        // Сложим мономы с одинаковыми степенями
        j1 = 0;
        j2 = 1;
        cfs[0] = cfs[0].mod(mod, ring);
        while (j2 < end - beg) { // повторять пока не обработаем весь массив
            // pows
            i = lastvar; // сравним степени мономов j1 и j2
            k1 = a[j1] + lastvar;
            k2 = a[j2] + lastvar;
            while (i >= 0 && powers[k1] == powers[k2]) {
                i--;
                k1--;
                k2--;
            }
            if (i == -1) {
                // если степени монома j2 = степеням j1, то добавить j2 к j1
                cfs[j1] = (cfs[j1].add(cfs[j2], ring).mod(mod, ring));
            } else { // иначе если степени монома j2 != степеням j1, то
                if (!cfs[j1].isZero(ring)) { // если в j1
                    j1++; // накопился не 0, то запишем
                } // моном j2 в j1+1, иначе запишем
                cfs[j1] = cfs[j2].mod(mod, ring); // моном j2 поверх монома j1
                a[j1] = a[j2];
            }
            j2++;
        }
        int monoms = j1; // monoms = число ненулевых мономов
        if (!cfs[j1].isZero(ring)) {
            monoms++;
        }
        if (monoms == 0) { // если число ненулевых мономов = 0, то
            powers = new int[0]; // возвращаем ZERO
            coeffs = new Element[0];
        } else {
            // иначе создаем массив степеней из массива a и
            // копируем cfs в массив коэффициентов
            // По получившемуся старшему моному определим реальное кол-во
            // переменных.
            int rvars = getRealVars(powers, a[0], fvars);
            int[] newpowers = new int[monoms * rvars]; // размера monoms
            int indxAdr = 0; // адрес в newpowers
            for (int k = 0; k < monoms; k++) {
                // преобразуем каждый адрес a[k] в моном
                adr = a[k]; // adr=адрес монома в powers
                for (int v = 0; v < rvars; v++) {
                    // копируем его из powers в newpowers
                    newpowers[indxAdr++] = powers[adr++];
                }
            }
            powers = newpowers;
            coeffs = new Element[monoms]; // копируем monoms коэффициентов
            System.arraycopy(cfs, 0, coeffs, 0, monoms); // из cfs в coeffs
        }
    }

    /*
     * Метод cloneWithoutCFormPage() - создает новый экземпляр Polynom с теми же значениями всех
     * входящих в него переменных, т.е. копирует его и возвращает ссылку на
     * копию.
     */
    @Override
    public Polynom clone() {
        if (coeffs.length == 0) return Polynom.polynomZero ;
        int[] clonePowers =new int[powers.length];
        Element[] cloneCoeffs=new Element[coeffs.length];
        System.arraycopy(powers, 0,  clonePowers, 0,powers.length);
        System.arraycopy(coeffs, 0,  cloneCoeffs, 0, coeffs.length);
        return new Polynom(clonePowers, cloneCoeffs); // возвращаем копию
    }
         /*
     * Создает новый экземпляр Polynom с теми же значениями всех
     * входящих в него переменных, но с пустым массивом коэффициентов
     */
    public Polynom cloneWithoutCoeffs() {
        if (coeffs.length == 0) return Polynom.polynomZero ;
        int[] clonePowers =new int[powers.length];
        Element[] cloneCoeffs=new Element[coeffs.length];
        System.arraycopy(powers, 0,  clonePowers, 0,powers.length);
        return new Polynom(clonePowers, cloneCoeffs); // возвращаем копию
    }
    
    /**
     * Клонировать только var старших мономов
     *
     * @param var
     *
     * @return
     */
    public Polynom clone(int n) {
        int cl = coeffs.length;
        if (cl == 0) {
            return polynomZero;
        }
        if (cl < n) {
            n = cl;
        }
        int var = powers.length / cl;
        int[] clonePowers = new int[n * var];
        System.arraycopy(powers, 0, clonePowers, 0, n * var);
        Element[] cloneCoeffs = new Element[n];
        System.arraycopy(coeffs, 0, cloneCoeffs, 0, n); // создаем копию coeffs
        return new Polynom(clonePowers, cloneCoeffs); // возвращаем копию
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Polynom) {
            Polynom p = (Polynom) obj;
            if (powers.length != p.powers.length
                    || coeffs.length != p.coeffs.length) {
                // при сравнении 0 с с,p или c с p не совпадают длины powers и
                // coeffs
                return false;
            }

            // 0 и 0, c и c, p и p
            if (coeffs.length == 0 && p.coeffs.length == 0) {
                // this=p=0
                return true;
            }
            if (powers.length == 0 && p.powers.length == 0) {
                // сравниваем c и c
                return coeffs[0].equals(p.coeffs[0]);
            } else {
                // сравниваем p и p
                for (int i = 0; i < powers.length; i++) {
                    if (powers[i] != p.powers[i]) {
                        return false;
                    }
                }
                for (int i = 0; i < coeffs.length; i++) {
                    if (!coeffs[i].equals(p.coeffs[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.powers);
        hash = 97 * hash + Arrays.deepHashCode(this.coeffs);
        return hash;
    }

    /*
     * Сравнивает 2 полинома. Возвращает true, если в полиномах совпадают все
     * степени и все коэффициенты
     */
    @Override
    public boolean equals(Element x, Ring ring) {
        return x instanceof Polynom ? equals((Polynom) x, ring) : false;
    }

    public boolean equals(Polynom p, Ring ring) {
        if (powers.length != p.powers.length
                || coeffs.length != p.coeffs.length) {
            // при сравнении 0 с с,p или c с p не совпадают длины powers и
            // coeffs
            return false;
        }

        // 0 и 0, c и c, p и p
        if (coeffs.length == 0 && p.coeffs.length == 0) {
            // this=p=0
            return true;
        }
        if (powers.length == 0 && p.powers.length == 0) {
            // сравниваем c и c
            return coeffs[0].equals(p.coeffs[0], ring);
        } else {
            // сравниваем p и p
            for (int i = 0; i < powers.length; i++) {
                if (powers[i] != p.powers[i]) {
                    return false;
                }
            }
            for (int i = 0; i < coeffs.length; i++) {
                if (!coeffs[i].equals(p.coeffs[i], ring)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Удаление из массива степеней лишних строк, когда нет старшей переменной
     *
     * @return
     */
    public Polynom truncate() {
        if (coeffs.length == 0) {
            return this;
        }
        int v = powers.length / coeffs.length;
        int v1 = v - 1;
        while (v1 >= 0 && powers[v1] == 0) {
            v1--;
        }
        if (v1 == v - 1) {
            // если в полиноме есть все v переменных, то ничего менять не надо
            return this;
        } else if (v1 == -1) {
            // если полином this -- это число, то вернуть полином с числом
            // степеней 0
            if (powers.length == 0) {
                return this;
            } else {
                return new Polynom(new int[0], coeffs);
            }
        } else {
            v1++;
            int[] respowers = new int[coeffs.length * v1];
            int adr = 0;
            int resAdr = 0;
            for (int i = 0; i < coeffs.length; i++) {
                System.arraycopy(powers, adr, respowers, resAdr, v1);
                adr += v;
                resAdr += v1;
            }
            return new Polynom(respowers, coeffs);
        }
    }

    /**
     * добавляет нули к массиву степеней v - количество нулей, которое требуется
     * добавить каждому моному
     *
     * @param v - количество нулей, которое требуется
     *
     * @return КРИВОЙ полином! (для Диминых алгоритмов)
     */
    public Polynom unTruncate(int v) {
        int kol_vars = this.powers.length / this.coeffs.length;
        int new_kol_vars = kol_vars + v;
        int[] newpowers = new int[powers.length + v * coeffs.length];
        int adr = 0;
        int newadr = 0;
        for (int i = 0; i < coeffs.length; i++) {
            System.arraycopy(powers, adr, newpowers, newadr, kol_vars);
            adr += kol_vars;
            newadr += new_kol_vars;
        }
        return new Polynom(newpowers, coeffs);
    }

    /**
     * Процедура нахождения НОД числовых или дробных коэффициентов полинома
     * this. Для дробного коэффициента считается, что он имеет сокращенный вид и
     * используется только его числитель.
     *
     * @return k НОД коэффициентов - число
     */
    public Element GCDNumPolCoeffs(Ring ring) {
        if (coeffs.length == 1) {
            return (isZero(ring)) ? ring.numberONE
                    : (coeffs[0].isNegative()) ? coeffs[0].negate(ring) : coeffs[0];
        }
        Element k = coeffs[0]; Element den=null;
        if (k instanceof Fraction) {  den= ((Fraction)k).denom; k = ((Fraction) k).num; }
        if (k.isNegative()) {k = k.negate(ring);}
        Element one = k.one(ring); 
        if ((k.isOne(ring))&&(den==null)) {return one;}
        boolean k_Not_One=true;
        for (int i = 1; i < coeffs.length; i++) {
            Element ki = coeffs[i];
            if (ki instanceof Fraction) {
                if (den!=null){ den=den.GCD(((Fraction)ki).denom,ring); if(den.isOne(ring))den=null;}
                ki = ((Fraction) ki).num;}
           if(k_Not_One){ if (ki.isNegative()) {ki = ki.negate(ring);} k = k.GCD(ki, ring); if (k.isOne(ring)) k_Not_One=false;}
           if (!k_Not_One){ if(den==null)return one;}
        } if((den!=null)&&(den.isNegative()))den = den.negate(ring);
        return ((k_Not_One)&(den!=null))? new Fraction(k,den):(den!=null)? new Fraction(one,den):k;
    }

    /**
     * Процедура выделения "старшего коэффициента" при старшей переменной.
     * Кольцо полиномов не изменяется. Степень старшей переменной в каждом
     * мономе результата равна нулю.
     *
     * @return "старший коэффициент" при старшей переменной (полином от младших
     * переменных)
     */
    private Polynom highCoeffVars() {
        int k = 1, vars = powers.length / coeffs.length;
        Polynom b = new Polynom();
        while (k < coeffs.length
                && powers[k * vars + vars - 1] == powers[vars - 1]) {
            k++;
        }
        b.coeffs = new Element[k];
        b.powers = new int[vars * k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < vars - 1; j++) {
                b.powers[vars * i + j] = powers[vars * i + j];
            }
            b.coeffs[i] = coeffs[i];
        }
        return b;
    }

    /**
     * Процедура выделения "старшего коэффициента" при старшей переменной.
     * Кольцо полиномов изменяется.
     *
     * @return "старший коэффициент" при старшей переменной (полином от младших
     * переменных)
     */
    public Polynom highCoeff() {
        int k = 1, vars = powers.length / coeffs.length;
        if (vars==0) return this;
        Polynom b = new Polynom();
        int newVars = vars - 1;
        // определение длинны коэффициента
        while (k < coeffs.length
                && powers[k * vars + newVars] == powers[newVars]) {k++;}
        // определение количества переменных в коэффициенте(с учётом того, что
        // полином отсортирован)
        while (newVars != 0 && powers[newVars - 1] == 0) {newVars--;}
        // заполнение значений
        b.coeffs = new Element[k];
        b.powers = new int[newVars * k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < newVars; j++) {
                b.powers[newVars * i + j] = powers[vars * i + j];
            }
            b.coeffs[i] = coeffs[i];
        }
        return b;
    }
    /**
     * Процедура выделения "старшего коэффициента" при переменной с номером n_var.
     * Кольцо полиномов изменяется.
     *
     * @param n_var --the number of variabe in Ring. (0,1,2,....)=(x,y,z...)
     * @return "старший коэффициент" при переменной n_var 
     *  Это полином от остальных переменных.
     */
    public Polynom highCoeff(int n_var) {
        int vars = powers.length / coeffs.length;
        if (vars - 1 == n_var) return highCoeff();
        if ((vars <= n_var)||(n_var<0)) { return this;}
        int n = n_var, j = 0; 
        int deg = this.degree(n_var);
        // j the number of coeffs in result
        for (int i = 0; i < coeffs.length; i++) {
            if (powers[n] == deg)  j++;
            n += vars;
        }
        int[] pow = new int[j*vars];
        Element[] cf = new Element[j];
        n = n_var; j=0; int jp = 0, start = 0;  
        for (int i = 0; i < coeffs.length; i++) {
            if (powers[n] == deg) {
                cf[j] = coeffs[i];
                System.arraycopy(powers, start, pow, jp, vars);
                jp += vars;
                j++;
            }
            start += vars;
            n += vars;
        }
        Polynom res = new Polynom(pow, cf);
        for (int i = n_var; i < res.powers.length; i += vars) {
            res.powers[i] = 0;
        }
        return res.truncate();
    }
    /**
     * Процедура изменения знака полинома(исходный полином не меняется)
     *
     * @return входной полином с противоположным знаком
     */
    @Override
    public Element negate(Ring ring) {
        Polynom p = this.clone();
        for (int i = 0; i < coeffs.length; i++) {
            p.coeffs[i] = p.coeffs[i].negate(ring);
        }
        return p;
    }

    /**
     * Процедура изменения знака полинома (меняет this: this:=-this)
     *
     * @return -this
     */
    public Polynom negateThis(Ring ring) {
        for (int i = 0; i < coeffs.length; i++) {
            coeffs[i] = coeffs[i].negate(ring);
        }
        return this;
    }

    /**
     * Процедура выделения "коэффициента" при некоторой степени S старшей
     * переменной.
     *
     * @param var номер первого монома,
     * @param k номер последнего монома, у которых старшая переменная имеет
     * степень S.
     *
     * @return полином-коэффициент при степени S по старшей переменной,
     */
    public Polynom Coeff(int n, int k) {
        Polynom r = new Polynom();
        int vars = powers.length / coeffs.length;
        if (vars == 0) {
            return this;
        }
        int newVars = vars - 1;
        // определение количества переменных в коэффициенте(с учётом того, что
        // полином отсортирован)
        while ((newVars > 0) && (powers[n * vars + newVars - 1] == 0)) {
            newVars--;
        }
        // заполнение значений
        if (newVars == 0) {
            return new Polynom(new int[] {}, new Element[] {coeffs[n]});
        }
        r.coeffs = new Element[k - n];
        r.powers = new int[newVars * (k - n)];
        for (int i = 0; i < k - n; i++) {
            r.coeffs[i] = coeffs[n + i];
            for (int j = 0; j < newVars; j++) {
                r.powers[newVars * i + j] = powers[(i + n) * vars + j];
            }
        }
        return r;
    }

    /**
     * Процедура вычисляющая последующий промежуточный множитель в алгоритме
     * Брауна, имея в качестве входного параметра предыдущее значение множителя
     * (this)
     *
     * @param vi разность степеней делимого и делителя
     * @param Ci1 старший коэффициент в делителе
     *
     * @return возвращает последующий промежуточный множитель в алгоритме Брауна
     */
    private Polynom braunDi(int vi, Polynom Ci1, Ring ring) {
        Polynom di;
        Ci1 = Ci1.powRecS(vi, ring);
        if (vi != 0) {
            if (vi == 1) {
                return Ci1;
            } else {
                di = powRecS(vi - 1, ring);
                di = Ci1.divideExact(di, ring);
                return di;
            }
        } else {
            return myOne(ring);
        }
    }

    /**
     * Процедура упрощения коэффициентов в остатке
     *
     * @param di промежуточный множитель
     * @param Ci старший коэффициент в делимом
     * @param vi разность старших степеней полиномов
     *
     * @return this полином, который упрощается
     */
    Polynom braunUpr(Polynom di, int vi, Polynom Ci, Ring ring) {
        return divideExact((((Polynom) di.negate(ring)).powRecS(vi, ring)).mulSS(
                (Polynom) Ci.negate(ring), ring), ring);
    }

    /**
     * Процедура деления полинома на число типа Element
     *
     * @param r число, на которое делится полином
     *
     * @return полином, коэффициенты которого делятся нацело на число r типа
     * Element
     */
    public Polynom polDivNumb(Element r, Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        Polynom Pone = Polynom.polynomFromNumber(one, ring);
        if (r.equals(Pone)) {
            return this;
        }

        Polynom res = new Polynom();
        res.powers = powers;
        res.coeffs = new Element[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            res.coeffs[i] = coeffs[i].divide(r, ring);
        }
        return res;
    }

    /**
     * Процедура нахождения остатка и домножителя при делении двух полиномов
     * this и q
     *
     * @param q делитель
     *
     * @return массив полиномов: на нулевом месте стоит остаток деления this на
     * q; на первом - домножитель (для степени n-m+1): остаток от деления с минимальным домножителем, минимальный домножитль
     */
    public Polynom[] remainder(Polynom q, Ring ring) {
        int vars = powers.length / coeffs.length;
        int v2 = q.powers.length / q.coeffs.length;
        if (vars > v2) {
            return new Polynom[] {new Polynom(new int[0], new Element[0]), q};
        }
        if (vars < v2 || powers[vars - 1] < q.powers[v2 - 1]) {
            return new Polynom[] {this, myOne(ring)};
        }
        if (vars == 1) {
            return PolynomOneVar.remainderZx(this, q, ring);
        }
        Polynom chastnoe;
        Polynom firstmonQ = q.highCoeff();
        Polynom p = this;
        int pow = p.powers[vars - 1] - q.powers[vars - 1] + 1, t = 0;
        // проверка коэффициента при старшей степени z на равенство 1
        boolean oneB = false;
        if (!(firstmonQ.coeffs[0].isOne(ring) && firstmonQ.isZero(ring))) {
            oneB = true;
        }
        // деление полиномов
        while (p.coeffs.length != 0
                && p.powers.length / p.coeffs.length == vars
                && q.powers[vars - 1] <= p.powers[vars - 1]) {
            // выделение элемента частного
            if (q.powers[vars - 1] != p.powers[vars - 1]) {
                chastnoe = p.highCoeffVars();
                for (int i = 0; i < chastnoe.coeffs.length; i++) {
                    chastnoe.powers[vars * i + vars - 1] = p.powers[vars - 1]
                            - q.powers[vars - 1];
                }
            } else {
                chastnoe = p.highCoeff();
            }
            // домножение полинома p на коэффициент при старшей степени z
            // полинома q
            if (oneB) {
                p = p.mulSS(firstmonQ, chastnoe.coeffs.length, p.coeffs.length,
                        0, firstmonQ.coeffs.length, ring);
            } else {
                p = p.subPolynom(chastnoe.coeffs.length, p.coeffs.length);
            }
            p = p.subtract(q.mulSS(chastnoe, firstmonQ.coeffs.length,
                    q.coeffs.length, 0, chastnoe.coeffs.length, ring), ring);
            t++;
        }
        // Домножение остатка
        Polynom x= p;
        if (oneB && t != pow) {  
            x = p.mulSS(firstmonQ.powRecS(pow - t, ring), ring);
        }
        return new Polynom[] {x, firstmonQ.powRecS(pow, ring), p, firstmonQ.powRecS(t, ring) };
    }

    /**
     * Метод возвращающий оценку по неравенства адамара для матрицы Сильвестра
     * двух полиномов над кольцом Z[x]
     *
     * @param q Polynom второй полином
     *
     * @return double оценка Адамара матрицы Сильвестра
     */
    public double hadamarSilv(Polynom q, Ring ring) {
        // вычисление оценки Адамара матрицы Сильвестра
        int vars = powers.length / coeffs.length;
        double addCoefF = 0, addCoefG = 0;
        for (int i = 0; i < coeffs.length; i++) {
            addCoefF += Math.ceil(Math.pow(coeffs[i].doubleValue(), 2));
        }
        for (int i = 0; i < q.coeffs.length; i++) {
            addCoefG += Math.ceil(Math.pow(q.coeffs[i].doubleValue(), 2));
        }
        return Math.ceil(Math.sqrt(Math.ceil(Math.pow(addCoefF,
                q.powers[vars - 1]))
                * Math.ceil(Math.pow(addCoefG, powers[vars - 1]))));
    }

    /**
     * Метод возвращающий квадрат оценки по неравенства адамара для матрицы
     * Сильвестра двух полиномов над кольцом Z[x]
     *
     * @param q Polynom второй полином
     *
     * @return Element оценку Адамара матрицы Сильвестра
     */
    public Element hadamarSilvBI(Polynom q, Ring ring) {
        Element zero = one(ring);
        // вычисление квадрата оценки Адамара матрицы Сильвестра
        int vars = powers.length / coeffs.length;
        Element addCoefF = zero, addCoefG = zero;
        for (int i = 0; i < coeffs.length; i++) {
            addCoefF = addCoefF.add(coeffs[i].pow(2, ring), ring);
        }
        for (int i = 0; i < q.coeffs.length; i++) {
            addCoefG = addCoefG.add(q.coeffs[i].pow(2, ring), ring);
        }
        return addCoefF.pow(q.powers[vars - 1], ring).multiply(
                addCoefG.pow(powers[vars - 1], ring), ring);
    }

    public Element landauBI_Zx(int m, Ring ring) {
        Element zero = one(ring);
        Element addCoefF = zero;
        for (int i = 0; i < coeffs.length; i++) {
            addCoefF = addCoefF.add(coeffs[i].pow(2, ring), ring);
        }
        Element res = addCoefF.multiply(addCoefF.toNumber(PascalTriangle.
                binomialZ(2 * m, m).numbElementType(), ring), ring);
        return res;
    }

    /**
     * Метод нахождения полинома по числовому модулю, оставляющий его в кольце
     * Z[x,y,..]
     *
     * @param m Element модуль
     *
     * @return Polynom преобразованный полином
     */
    @Override
    public Polynom mod(Element m, Ring ring) {
        Element[] newCoeffs = new Element[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            newCoeffs[i] = coeffs[i].mod(m, ring);
        }
        return (new Polynom(powers, newCoeffs)).deleteZeroCoeff(ring);
    }

    /**
     * Процедура нахождения остатка при делении двух полиномов this и q
     *
     * @param q делитель
     *
     * @return остаток деления this на q;
     */
    private Polynom rem(Polynom q, Ring ring) {
        Element one = one(ring);
        int vars = powers.length / coeffs.length;
        if (vars == 1) return PolynomOneVar.remZx(this, q, ring);
        Polynom chastnoe;
        Polynom firstmonQ = q.highCoeff();
        Polynom p = this;
        int pow = p.powers[vars - 1] - q.powers[vars - 1] + 1, t = 0;
        // проверка коэффициента при старшей степени z на равенство 1
        boolean oneB = false;
        if (!firstmonQ.coeffs[0].isOne(ring) || !firstmonQ.isZero(ring)) {
            oneB = true;
        }
        // деление полиномов
        while (p.coeffs.length != 0
                && p.powers.length / p.coeffs.length == vars
                && q.powers[vars - 1] <= p.powers[vars - 1]) {
            // выделение элемента частного
            if (q.powers[vars - 1] != p.powers[vars - 1]) {
                chastnoe = p.highCoeffVars();
                for (int i = 0; i < chastnoe.coeffs.length; i++) {
                    chastnoe.powers[vars * i + vars - 1] = p.powers[vars - 1]
                            - q.powers[vars - 1];
                }
            } else {
                chastnoe = p.highCoeff();
            }
            // домножение полинома p на коэффициент при старшей степени z
            // полинома q
            if (oneB) {
                p = p.mulSS(firstmonQ, chastnoe.coeffs.length, p.coeffs.length,
                        0, firstmonQ.coeffs.length, ring);
            } else {
                p = p.subPolynom(chastnoe.coeffs.length, p.coeffs.length);
            }
            p = p.subtract(q.mulSS(chastnoe, firstmonQ.coeffs.length,
                    q.coeffs.length, 0, chastnoe.coeffs.length, ring), ring);
            t++;
        }

        // Домножение остатка
        if (oneB && t != pow) {
            p = p.mulSS(firstmonQ.powRecS(pow - t, ring), ring);
        }

        return p;
    }

    /**
     * Процедура нахождения частного и домножителя при делении двух полиномов
     * this и q
     *
     * @param q делитель
     * @param ring
     *
     * @return массив полиномов: на нулевом месте стоит частное деления this на
     * q; на первом - домножитель
     */
    public Polynom[] divideExt(Polynom q, Ring ring) {
        Element one = one(ring);
        int vars = powers.length / coeffs.length;
        int v2 = q.powers.length / q.coeffs.length;
        Polynom p = this;
        if (vars > v2) {
            return new Polynom[] {p.mulSS(q, ring), q};
        }
        if (vars < v2 || powers[vars - 1] < q.powers[v2 - 1]) {
            return new Polynom[] {myZero(ring), myOne(ring)};
        }
        if (vars == 1) {
            PolynomOneVar t = new PolynomOneVar(this);
            return t.divideExt(q, ring);
        }
        Polynom firstmonQ = q.highCoeff();
        int pow = p.powers[vars - 1] - q.powers[vars - 1] + 1, t = 0;
        Polynom chastnoe[] = new Polynom[pow];
        // поиск всех мономов с одинаковой старшей степенью при z в полиноме q
        // проверка коэффициента при старшей степени z в полиноме q на равенство
        // 1
        boolean oneB = false;
        if (!firstmonQ.coeffs[0].isOne(ring) || !firstmonQ.isZero(ring)) {
            oneB = true;
        }
        // деление полиномов
        while (p.coeffs.length != 0
                && p.powers.length / p.coeffs.length == vars
                && q.powers[vars - 1] <= p.powers[vars - 1]) {
            // выделение элемента частного
            if (q.powers[vars - 1] != p.powers[vars - 1]) {
                chastnoe[t] = p.highCoeffVars();
                for (int i = 0; i < chastnoe[t].coeffs.length; i++) {
                    chastnoe[t].powers[vars * i + vars - 1] = p.powers[vars - 1]
                            - q.powers[vars - 1];
                }
            } else {
                chastnoe[t] = p.highCoeff();
            }
            // домножение мн-на p на коэффициент при старшей степени z
            if (oneB) {
                p = p.mulSS(firstmonQ, chastnoe[t].coeffs.length,
                        p.coeffs.length, 0, firstmonQ.coeffs.length, ring);
            } else {
                p = p.subPolynom(chastnoe[t].coeffs.length, p.coeffs.length);
            }
            p = p.subtract(q.mulSS(chastnoe[t], firstmonQ.coeffs.length,
                    q.coeffs.length, 0, chastnoe[t].coeffs.length, ring), ring);
            t++;
        }
        // домножение частного
        if (oneB) {
            for (int i = 0; i < t; i++) {
                chastnoe[i] = chastnoe[i].mulSS(
                        firstmonQ.powRecS(pow - i - 1, ring), ring);
            }
        }
        // Собирание результата частного
        vars = chastnoe[0].powers.length / chastnoe[0].coeffs.length;
        Polynom result = new Polynom();
        int k = chastnoe[0].coeffs.length;
        for (int i = 1; i < t; i++) {
            k += chastnoe[i].coeffs.length;
        }
        result.coeffs = new Element[k];
        result.powers = new int[vars * k];
        int varsChastnoe;
        int l = 0;
        for (int i = 0; i < t; i++) {
            for (int j = 0; j < chastnoe[i].coeffs.length; j++) {
                result.coeffs[j + l] = chastnoe[i].coeffs[j];
                varsChastnoe = chastnoe[i].powers.length
                        / chastnoe[i].coeffs.length;
                for (int s = 0; s < varsChastnoe; s++) {
                    result.powers[vars * (j + l) + s] = chastnoe[i].powers[varsChastnoe
                            * j + s];
                }
            }
            l += chastnoe[i].coeffs.length;
        }
        return new Polynom[] {result, firstmonQ.powRecS(pow, ring)};
    }

    /**
     * Процедура нахождения частного, остатка при делении двух полиномов this и
     * q1, когда главная переменная это самая старшая переменная. Деление
     * происходит в поле рацианальных функций от всех маладших переменных.
     *
     * @param q1 делител
     *
     * @return два полинома: частное деления this на q и остаток от деления;
     * (оба являются полиномами по главной переменной, но имеют дробную часть --
     * полином, который не содержит главную переменную)
     */
    public Element[] divAndRemToRational(Element pol, Ring ring) {
        if (pol.isItNumber())return divideAndRemainderFromNumber( pol, ring);
        if (!(pol instanceof Polynom)) return new Element[]{NAN,NAN};
        Polynom q =(Polynom) pol;        
        Element[] pp = divAndRem(q, ring);
        if (pp[2].isOne(ring))return new Element[]{pp[0],pp[1]};
        Element p1 = (new Fraction(pp[0], pp[2])).cancel(ring);
        Element p2 = (new Fraction(pp[1], pp[2])).cancel(ring);
        return new Element[] {p1, p2};
    }
    

    /**
     ** Вычисление частного и остатка при делении двух полиномов this и q1, когда//////////////////
     * главная переменная varNumb может иметь любой номер (0,1,..). Деление
     * происходит в поле рацианальных функций от всех маладших переменных.
     *
     * @param q1 делитель
     *
     * @pV  главная переменная в кольце или ее номер
     * @return два полинома: частное от деления this на q и остаток от деления;
     * (оба являются полиномами по главной переменной, но имеют дробную часть --
     * полином, который не содержит главную переменную)
     */
    @Override
    public Element[] quotientAndRemainder(Element q1, Element pV, Ring ring) {
        int cNumb=0;
        if  (pV instanceof Polynom) cNumb=((Polynom)pV).powers.length-1;
        else cNumb=pV.intValue();
        if ((q1 instanceof Polynom)&&(cNumb>=0)) {
            return quotientAndRemainder( (Polynom)q1, cNumb,  ring);
        } else { return new Element[]{NAN,NAN}; }  
    }
    @Override
    public Element[] quotientAndRemainder(Element q1, Ring ring){Polynom qq;
        if(!(q1 instanceof Polynom)) if(q1.isItNumber()) qq=new Polynom(q1); else return new Element[]{NAN,NAN};
        else qq=(Polynom)q1;
            return quotientAndRemainder( qq, 0,  ring);
    }
//    @Override
//    public Element[] quotientAndProperFraction(Element denom,  Ring ring) {
//            return quotientAndProperFraction( denom, 1,ring); }
//    
//    public Element[] quotientAndProperFraction(Element denom, int var, Ring ring) {
//        boolean thisNumb=this.isItNumber(), denNumb=denom.isItNumber() ;
//      if (thisNumb && denNumb) {
//            return coeffs[0].quotientAndProperFraction(denom.toNumber(ring.algebra[0], ring), ring);
//       }
//        if  (denNumb) {
//            Element den = denom.toNumber(ring.algebra[0], ring);
//            Element ee =  divideByNumber(den, ring);//  divide  AndRemainderFromNumber(den , ring);
//            return new Element[] {ee, ring.numberZERO};
//        }
//        if  (denom instanceof Polynom) {
//            Element[] hh =  quotientAndRemainder((Polynom)denom, var, ring);
//            return (hh[1].isZero(ring)) ? hh: new Element[] {hh[0], new Fraction(hh[1],denom)};
//        }
//        return new Element[] {this, ring.numberZERO}; 
//    }
    public Element[] quotientAndRemainder(Polynom q1,   Ring ring) {
        return quotientAndRemainder(q1, 1, ring);
    }
    public Element[] quotientAndRemainder(Polynom q1, int varNumb, Ring ring) {
        Polynom p = this, q = q1;
        
        int pV = p.powers.length / p.coeffs.length;
        int qV = q.powers.length / q.coeffs.length;
        int max = Math.max(pV, qV) - 1;
        if (varNumb >= max)  return divAndRemToRational(q1, ring);
        //отсортировали и заменили этот массив так как нам надо
        int ll = max+1;//ring.varNames.length;
        int[] mas = new int[ll];//массив сортировки
        for (int i = 0; i < ll; i++) {
            mas[i] = i;
        }
        mas[max] = varNumb;
        mas[varNumb] = max;
        p = p.changeOrderOfVars(mas, true, ring);
        q = q.changeOrderOfVars(mas, true, ring);
        Element[] ress = p.divAndRemToRational(q, ring);
        ress[0] = ress[0].changeOrderOfVars(mas, true, ring);
        ress[1] = ress[1].changeOrderOfVars(mas, true, ring);
        return ress;
    }

    /**
     * Процедура нахождения частного, остатка и домножителя при делении двух
     * полиномов this и q
     *
     * @param q делитель
     *
     * @return массив полиномов: на нулевом месте стоит частное деления this на
     * q; на первом - остаток; на втором - домножитель
     */
    public Polynom[] divAndRem(Polynom q, Ring ring) {
        Element one = ring.numberONE;
        Polynom ONE = Polynom.polynomFromNot0Number(one);
        if (this.isZero(ring)) {
            return new Polynom[] {polynomZero, polynomZero, ONE}; }
        int vars = powers.length / coeffs.length;
        int v2 = q.powers.length / q.coeffs.length;
        Polynom p = this;
        if ((vars > v2)||((vars == 0) && (v2 == 0))) return new Polynom[] {this, Polynom.polynomZero, q};
        if (vars < v2 || powers[vars - 1] < q.powers[v2 - 1]) {
            return new Polynom[] {Polynom.polynomZero, p, ONE};
        }
        if (vars == 1) {
            if (ring.isFeild()) {Element[] rF= divAndRemRQZpx(q, ring); return new Polynom[]{(Polynom)rF[0],(Polynom)rF[1],Polynom.polynomFromNot0Number(ring.numberONE)};  }      
            PolynomOneVar t = new PolynomOneVar(this);
            return t.divAndRem_Zx(q, ring);  }
        Polynom firstmonQ = q.highCoeff();
        int pow = p.powers[vars - 1] - q.powers[vars - 1] + 1, t = 0;
        Polynom chastnoe[] = new Polynom[pow];
        // проверка коэффициента при старшей степени z в полиноме q на равенство 1
        boolean oneB = false;
        if (!firstmonQ.coeffs[0].isOne(ring) || !firstmonQ.isZero(ring)) {
            oneB = true;
        }
        // деление полиномов
        while (p.coeffs.length != 0
                && p.powers.length / p.coeffs.length == vars
                && q.powers[vars - 1] <= p.powers[vars - 1]) {
            // выделение элемента частного
            if (q.powers[vars - 1] != p.powers[vars - 1]) {
                chastnoe[t] = p.highCoeffVars();
                for (int i = 0; i < chastnoe[t].coeffs.length; i++) {
                    chastnoe[t].powers[vars * i + vars - 1] = p.powers[vars - 1]
                            - q.powers[vars - 1];
                }
            } else {
                chastnoe[t] = p.highCoeff();
            }
            // домножение мн-на p на коэффициент при старшей степени z
            if (oneB) {
                p = p.mulSS(firstmonQ, chastnoe[t].coeffs.length,
                        p.coeffs.length, 0, firstmonQ.coeffs.length, ring);
            } else {
                p = p.subPolynom(chastnoe[t].coeffs.length, p.coeffs.length);
            }
            p = p.subtract(q.mulSS(chastnoe[t], firstmonQ.coeffs.length,
                    q.coeffs.length, 0, chastnoe[t].coeffs.length, ring), ring);
            t++;
        }
        if (oneB) {
            // Домножение частного
            for (int i = 0; i < t; i++) {
                chastnoe[i] = chastnoe[i].mulSS(
                        firstmonQ.powRecS(pow - i - 1, ring), ring);
            }
            // Домножение остатка
            if (t != pow) {
                p = p.mulSS(firstmonQ.powRecS(pow - t, ring), ring);
            }
        }
        // Собирание частного
        vars = chastnoe[0].powers.length / chastnoe[0].coeffs.length;
        Polynom result = new Polynom();
        int k = chastnoe[0].coeffs.length;
        for (int i = 1; i < t; i++) {
            k += chastnoe[i].coeffs.length;
        }
        result.coeffs = new Element[k];
        result.powers = new int[vars * k];
        int varsChastnoe;
        int l = 0;
        for (int i = 0; i < t; i++) {
            for (int j = 0; j < chastnoe[i].coeffs.length; j++) {
                result.coeffs[j + l] = chastnoe[i].coeffs[j];
                varsChastnoe = chastnoe[i].powers.length
                        / chastnoe[i].coeffs.length;
                for (int s = 0; s < varsChastnoe; s++) {
                    result.powers[vars * (j + l) + s] = chastnoe[i].powers[varsChastnoe
                            * j + s];
                }
            }
            l += chastnoe[i].coeffs.length;
        }
        return new Polynom[] {result, p, firstmonQ.powRecS(pow, ring)};
    }

    /**
     * Процедура нахождения частного и остатка при делении двух полиномов this и
     * q === Вспомогательная для ExtendedGCD - надо бы пересмотреть....
     *
     * @param q делитель
     *
     * @return массив полиномов: на нулевом месте стоит частное деления this на
     * q; на первом - остаток
     */
    private Polynom[] divRem(Polynom q, Ring ring) {
        if (isZero(ring)) {
            return new Polynom[] {Polynom.polynomZero, Polynom.polynomZero};
        }
        int vars = powers.length / coeffs.length;
        if (vars == 1) {
            PolynomOneVar t = new PolynomOneVar(this);
            return t.divRemZx(q, ring);
        }
        Polynom p = this;
        Polynom firstmonQ = q.highCoeff();
        int pow = p.powers[vars - 1] - q.powers[vars - 1] + 1, t = 0;
        Polynom chastnoe[] = new Polynom[pow];
        // проверка коэффициента при старшей степени z в полиноме q на равенство
        // 1
        boolean oneB = false;
        if (!firstmonQ.coeffs[0].isOne(ring) || !firstmonQ.isZero(ring)) {
            oneB = true;
        }

        // деление полиномов
        while (p.coeffs.length != 0
                && p.powers.length / p.coeffs.length == vars
                && q.powers[vars - 1] <= p.powers[vars - 1]) {
            // выделение элемента частного
            if (q.powers[vars - 1] != p.powers[vars - 1]) {
                chastnoe[t] = p.highCoeffVars();
                for (int i = 0; i < chastnoe[t].coeffs.length; i++) {
                    chastnoe[t].powers[vars * i + vars - 1] = p.powers[vars - 1]
                            - q.powers[vars - 1];
                }
            } else {
                chastnoe[t] = p.highCoeff();
            }
            // домножение мн-на p на коэффициент при старшей степени z
            if (oneB) {
                p = p.mulSS(firstmonQ, chastnoe[t].coeffs.length,
                        p.coeffs.length, 0, firstmonQ.coeffs.length, ring);
            } else {
                p = p.subPolynom(chastnoe[t].coeffs.length, p.coeffs.length);
            }
            p = p.subtract(q.mulSS(chastnoe[t], firstmonQ.coeffs.length,
                    q.coeffs.length, 0, chastnoe[t].coeffs.length, ring), ring);
            t++;
        }
        if (oneB) {
            // Домножение частного
            for (int i = 0; i < t; i++) {
                chastnoe[i] = chastnoe[i].mulSS(
                        firstmonQ.powRecS(pow - i - 1, ring), ring);
            }
            // Домножение остатка
            if (t != pow) {
                p = p.mulSS(firstmonQ.powRecS(pow - t, ring), ring);
            }
        }
        // Собирание частного
        vars = chastnoe[0].powers.length / chastnoe[0].coeffs.length;
        Polynom result = new Polynom();
        int k = chastnoe[0].coeffs.length;
        for (int i = 1; i < t; i++) {
            k += chastnoe[i].coeffs.length;
        }
        result.coeffs = new Element[k];
        result.powers = new int[vars * k];
        int varsChastnoe;
        int l = 0;
        for (int i = 0; i < t; i++) {
            for (int j = 0; j < chastnoe[i].coeffs.length; j++) {
                result.coeffs[j + l] = chastnoe[i].coeffs[j];
                varsChastnoe = chastnoe[i].powers.length
                        / chastnoe[i].coeffs.length;
                for (int s = 0; s < varsChastnoe; s++) {
                    result.powers[vars * (j + l) + s] = chastnoe[i].powers[varsChastnoe
                            * j + s];
                }
            }
            l += chastnoe[i].coeffs.length;
        }
        return new Polynom[] {result, p};
    }

    /**
     * Процедура нахождения НОД полиномиальных коэффициентов при старшей
     * переменной, когда старшей переменной считается переменная с заданным
     * номером "numbOfHvar". Алгоритм: переорганизовываем полином так, чтобы
 данная переменная стала старшей, затем вызываем GCDHPolCoeffs(ring).
     *
     * @param numbOfHvar -- номер переменной которая считается старшей (0,1,..).
     * @param ring -- Ring
     *
     * @return полином от нескольких переменных, число его переменных меньше чем
     * в исходном полиноме, и который является НОДом всех
     * полиномов-коэффициентов при переменной "numbOfHvar"
     */
    public Polynom GCDHPolCoeffs(int numbOfHvar, Ring ring) {
        int vars = powers.length / coeffs.length;
        if (numbOfHvar == vars - 1) {
            return Polynom.this.GCDHPolCoeffs(ring);
        }
        int[] varsMap = new int[vars + 1];
        for (int i = 0; i < vars; i++) {
            varsMap[i] = i;
        }
        varsMap[numbOfHvar] = vars;    // несуществующая переменная
        varsMap[vars] = numbOfHvar;
        Polynom changed = changeOrderOfVars(varsMap, ring);
        return changed.GCDHPolCoeffs(ring);
    }

    /**
     * Процедура нахождения НОД полиномиальных коэффициентов при старшей
     * переменной
     *
     * @param ring --Ring
     *
     * @return полином от нескольких переменных, число его переменных меньше чем
     * в исходном полиноме, и который является НОДом всех
     * полиномов-коэффициентов при старшей переменной
     */
    public Polynom GCDHPolCoeffs(Ring ring) {
        int vars = powers.length / coeffs.length;
        if (vars == 1) {
            return new Polynom(new int[] {}, new Element[] {GCDNumPolCoeffs(ring)});
        }
        int i = 1;
        int j;
        Polynom GCDcoeffHV;
        while (i < coeffs.length) {
            if (powers[vars - 1] == powers[vars * (i + 1) - 1]) {
                i++;
            } else {
                break;
            }
        }
        GCDcoeffHV = Coeff(0, i);
        if (i != coeffs.length) {
            // старшая переменная встречается в мономах с разными степенями
            j = i + 1;
            while (j < coeffs.length) {
                if (powers[vars * j - 1] == powers[vars * (j + 1) - 1]) {
                    j++;
                } else {
                    GCDcoeffHV = GCDcoeffHV.gcd(Coeff(i, j), ring);
                    i = j;
                    j++;
                    if (GCDcoeffHV.isOne(ring)) {
                        return GCDcoeffHV;
                    }
                }
            }
            GCDcoeffHV = GCDcoeffHV.gcd(Coeff(i, j), ring);
        }
        return GCDcoeffHV;
    }

    /**
     * Метод нахождения НОД полиномов, если один из них содержит только один
     * моном (алгоритм Евклида не используется)
     *
     * @param r Polynom
     *
     * @return Polynom
     */
    protected Polynom monomGCD(Polynom r, Ring ring) {
        Element one = one(ring);
        Polynom p;
        Polynom q;
        Element coeff;
        // в полиноме p всегда один моном
        if (coeffs.length == 1) {
            p = this;
            q = r;
        } else {
            p = r;
            q = this;
        }
        int v1 = p.powers.length / p.coeffs.length, 
            v2 = q.powers.length / q.coeffs.length, vars;
        // определение максимального размера переменных в будущем результате
        if (v1 > v2) {
            vars = v2;
        } else {
            vars = v1;
        }

        coeff = (p.coeffs[0]).GCD(q.coeffs[0], ring);
        int t = 1;
        while (!coeff.isOne(ring) && t < q.coeffs.length) {
            coeff = coeff.GCD(q.coeffs[t], ring);
            t++;
        }
        // проверка наличия в коэффициенте свободного члена
        boolean usl = true;
        for (int i = 0; i < v2; i++) {
            if (q.powers[v2 * (q.coeffs.length - 1) + i] != 0) {
                usl = false;
            }
        }
        if (vars == 0 || usl) {
            return new Polynom(new int[] {}, new Element[] {coeff});
        }
        int[] tempPower = new int[vars];
        System.arraycopy(p.powers, 0, tempPower, 0, vars);
        for (int i = 0; i < q.coeffs.length; i++) {
            for (int j = 0; j < vars; j++) {
                if (tempPower[j] > q.powers[i * v2 + j]) {
                    tempPower[j] = q.powers[i * v2 + j];
                }
            }
        }
        // обработка полученного массива степеней
        int var = vars;
        for (int i = vars - 1; i > -1; i--) {
            if (tempPower[i] == 0) {
                var--;
            } else {
                break;
            }
        }
        if (var != vars) {
            int[] power = new int[var];
            System.arraycopy(tempPower, 0, power, 0, var);
            return new Polynom(power, new Element[] {coeff});
        }
        return new Polynom(tempPower, new Element[] {coeff});
    }

    /**
     * Процедура нахождения НОК полиномов this и q
     *
     * @param q один из полиномов, чей НОК ищется
     *
     * @return НОК полиномов
     */
    public Polynom LCM(Polynom q, Ring ring) {
        Polynom p = gcd(q, ring);
        return multiply(q.divideExact(p, ring), ring);
    }

    @Override
    public Element LCM(Element q, Ring ring) {
        if (q instanceof Polynom) {
            Polynom p = gcd((Polynom) q, ring);
            return multiply(((Polynom) q).divide(p, ring), ring);
        } else {
            return null;
        }
    }

    @Override
    public VectorS extendedGCD(Element x, Ring ring) {
        if (x instanceof Polynom) {
            return extendedGCD((Polynom) x, ring);
        } else {
            return null;
        }
    }

    @Override
    public Element GCD(Element p, Ring ring) {
        if (isZero(ring)) {return p;}
        if (p.isZero(ring)) {return this;}
        if (p instanceof Polynom) {
                if (((Polynom) p).isItNumber()) {
                    if (ring.algebra[0] == Ring.Z) {
                        NumberZ h = (NumberZ) this.GCDNumPolCoeffs(ring);
                        return h.gcd((NumberZ) ((Polynom) p).coeffs[0]);
                    }
                }
                return gcd((Polynom) p, ring);
            } else if (p.numbElementType() == Ring.Z && coeffs[0].numbElementType() == Ring.Z) {
                Element a = GCDNumPolCoeffs(ring);
                return a.GCD(p, ring);
            }        
        return ring.numberONE;
    }

    /**
     * Процедура нахождения НОД полиномов this и q
     *
     * @param q один из двух полиномов, для которых ищется НОД
     *
     * @return НОД полиномов
     */
    public Polynom gcd(Polynom q, Ring ring) {
        Polynom g = gcd_Domain(q, ring);
        Element hh = g.coeffs[0];
        int typeR = ring.algebra[0] % Ring.Complex;
        if ((((typeR == Ring.Z) || (typeR == Ring.Q) || (typeR == Ring.Z64))) ||(hh.isOne(ring)))
            return g;
       g=g.divideByNumber(hh, ring);
        return (typeR==Ring.Zp32)||(typeR==Ring.Zp32)? g.Mod(ring): g;
    }

    public Polynom gcd_Domain(Polynom q, Ring ring) {
        Polynom p = this;
        int pN = coeffs.length;
        int qN = q.coeffs.length;
        if (pN == 0) {
            return q;
        }
        if (qN == 0) {
            return p;
        }
        if (coeffs.length == 1 || q.coeffs.length == 1) {
            return monomGCD(q, ring);
        }
        int usl = p.powers.length / pN - q.powers.length / qN;
        while (usl != 0) {
            if (usl > 0) {
                if (q.powers.length == 0) {
                    return polynomFromNumber((q.coeffs[0]).
                            GCD(p.GCDNumPolCoeffs(ring), ring), ring);
                }
                p = p.GCDHPolCoeffs(ring);
            } else {
                if (usl < 0) {
                    if (p.powers.length == 0) {
                        return polynomFromNumber((p.coeffs[0]).GCD(q.GCDNumPolCoeffs(ring), ring), ring);
                    }
                    q = q.GCDHPolCoeffs(ring);
                }
            }
            usl = p.powers.length / p.coeffs.length
                    - q.powers.length / q.coeffs.length;
        }
        int v, vars = p.powers.length / p.coeffs.length;
        if (vars == 0) {
            return polynomFromNumber((p.coeffs[0]).GCD(q.coeffs[0], ring), ring);
        }
        if (vars == 1) {
            PolynomOneVar t = new PolynomOneVar(p);
            return t.gcdZx(q, ring);
        }
        Polynom C;
        Polynom C1, d, p1, q1, gcdcoeff = null;
        //нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        //полиномов на соответствующий ему НОД коэффициентов
        p1 = p.GCDHPolCoeffs(ring);
        if (!p1.isOne(ring)) {
            p = p.divideExact(p1, ring);
        }
        q1 = q.GCDHPolCoeffs(ring);
        if (!q1.isOne(ring)) {
            q = q.divideExact(q1, ring);
        }
        gcdcoeff = p1.gcd(q1, ring); // найдем рекурсивно НОД коеффициентов
        d = myOne(ring);
        //Первое деление полиномов без упрощения остатка по методу Брауна, но
        //с нахождением следующего di

        if (p.powers[vars - 1] >= q.powers[vars - 1]) {
            C1 = q.highCoeff();
            v = p.powers[vars - 1] - q.powers[vars - 1];
            d = d.braunDi(v, C1, ring);
            p = p.rem(q, ring);
        } else {
            C1 = p.highCoeff();
            v = q.powers[vars - 1] - p.powers[vars - 1];
            d = d.braunDi(v, C1, ring);
            q = q.rem(p, ring);

        }

        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.coeffs.length > 0 && q.coeffs.length > 0
                && p.powers.length / p.coeffs.length
                == q.powers.length / q.coeffs.length) {
            if (p.powers[vars - 1] > q.powers[vars - 1]) {
                C = p.highCoeff();
                C1 = q.highCoeff();
                v = p.powers[vars - 1] - q.powers[vars - 1];
                p = p.rem(q, ring);
                if (p.coeffs.length == 0) {
                    break;
                }
                p = p.braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
            } else {
                C = q.highCoeff();
                C1 = p.highCoeff();
                v = q.powers[vars - 1] - p.powers[vars - 1];
                q = q.rem(p, ring);
                if (q.coeffs.length == 0) {
                    break;
                }
                q = q.braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
            }
        }

        //Вывод ответа в случае, когда он лежит в том же кольце что и многочлены p и q
        if (p.coeffs.length == 0) {
            q1 = q.GCDHPolCoeffs(ring);
            if (!q1.isOne(ring)) {
                q = q.divideExact(q1, ring);
            }
            if (!gcdcoeff.isOne(ring)) {
                q = q.mulSS(gcdcoeff, ring);
                return q;
            }
            return q;
        }
        if (q.coeffs.length == 0) {
            p1 = p.GCDHPolCoeffs(ring);
            if (!p1.isOne(ring)) {
                p = p.divideExact(p1, ring);
            }
            if (!gcdcoeff.isOne(ring)) {
                p = p.mulSS(gcdcoeff, ring);
                return p;
            }
            return p;
        }

        //Вывод ответа в случае, когда он не лежит в кольце многочленов от меньшего
        // количества переменных, чем vars
        if (!((vars == 1) || (gcdcoeff.isOne(ring)))) {
            return gcdcoeff;
        }
        //Вывод ответа в случае, когда НОД(f, g) = 1
        return myOne(ring);
    }

    /**
     * Процедура возвращающая последний остаток при нахождении НОД двух
     * полиномов this и q по алгоритму Евклида
     *
     * @param q один из исходных полиномов
     *
     * @return полином - остаток при нахождении НОД двух полиномов по алгоритму
     * Евклида
     */
    public Polynom resultant(Polynom q, Ring ring) {
        return resultant2Pol(q, ring)[0];
    }

    /**
     * Процедура возвращающая последний остаток при нахождении НОД двух
     * полиномов this и q по алгоритму Евклида и предпоследний остаток. Главная
     * переменная varNumb может иметь любой номер (0,1,..). Это полезно, если
     * дальше нужно искать корни последнего остатка и подставлять в
     * предпослкдний для нахождения НОД
     *
     * @param q один из исходных полиномов
     * @param varNumb the number of variable, which is MAIN VARIABLE
     *
     * @return массив полиномов - остатков при нахождении НОД двух полиномов по
     * алгоритму Евклида
     */
    public Polynom[] resultant2Pol(Polynom q1, int varNumb, Ring ring) {  
        Polynom p = this, q = q1;
       // int ll = ring.varNames.length;
        int pV = p.powers.length / p.coeffs.length;
        int qV = q.powers.length / q.coeffs.length;
        int max = Math.max(pV, qV) - 1;
        if (varNumb >= max) {
            return resultant2Pol(q, ring);
        }   
        //отсортировали и заменили этот массив так как нам надо
        int[] mas = new int[max+1];//массив сортировки
        for (int i = 0; i < mas.length; i++)  mas[i] = i;
        mas[max] = varNumb;
        mas[varNumb] = max;
        p = p.changeOrderOfVars(mas, true, ring);
        q = q.changeOrderOfVars(mas, true, ring);
        Polynom[] ress = p.resultant2Pol(q, ring);
        for (int i = 0; i < ress.length; i++)
             ress[i] = ress[i].changeOrderOfVars(mas, true, ring);
        return ress;
    }

    /**
     * Процедура возвращающая последние остатки при нахождении НОД двух
     * полиномов this и q по алгоритму Евклида.  Это
     * полезно, если дальше нужно искать корни последнего остатка и подставлять
     * в предпослкдний для нахождения НОД
     *
     * @param q один из исходных полиномов
     *
     * @return массив полиномов - остатков при нахождении НОД двух полиномов по
     * алгоритму Евклида
     */
    public Polynom[] resultant2Pol(Polynom q, Ring ring) {    
        int vars = powers.length / coeffs.length, alfa, beta,varsQ=q.powers.length / q.coeffs.length;
        if (vars!=varsQ)return new Polynom[0];
        int degr= this.degree(vars-1)+1;
        Polynom[] results= new Polynom[degr]; // для всех остатков
        int indRes=2; // index for results
        if (vars == 1) {
            PolynomOneVar t = new PolynomOneVar(this);
            return new Polynom[] {t.resultant_Zx(q, ring), Polynom.polynomZero};
        }
        Polynom p = this;
        int v;
        Polynom C;
        Polynom C1, d, p1, q1;
        Polynom result = null, other = null;
        // нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        // полиномов на соответствующий ему НОД коэффициентов
        p1 = p.GCDHPolCoeffs(ring);
        if (!p1.equals(myOne(ring))) {
            p = p.divideExact(p1, ring);
        }
        q1 = q.GCDHPolCoeffs(ring);
        if (!q1.isOne(ring)) {
            q = q.divideExact(q1, ring);
        }
        d = Polynom.polynom_one(ring.numberONE);
        // Первое деление полиномов без упрощения остатка по методу Брауна, но
        // с нахождением следующего di
        if (p.powers[vars - 1] >= q.powers[vars - 1]) {
            v = p.powers[vars - 1] - q.powers[vars - 1];
            if (p.powers[vars - 1] == 1) {
                alfa = 1;
                beta = 1;
            } else {
                beta = v + 2;
                alfa = 0;
            }
            C1 = q.highCoeff();
            d = d.braunDi(v, C1, ring);
            p = p.rem(q, ring);
            result = p; 
            results[0]=q.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring);
            results[1]=p.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring); //  add 2015 ++++++++++++++
            other = q;
        } else {
            v = q.powers[vars - 1] - p.powers[vars - 1];
            beta = 0;
            alfa = v + 2;
            C1 = p.highCoeff();
            d = d.braunDi(v, C1, ring);
            q = q.rem(p, ring);
            result = q; 
            results[0]=p.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring);  
            results[1]=q.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring); //  add 2015 ++++++++++++++
            other = p;
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.coeffs.length > 0
                && q.coeffs.length > 0
                && p.powers.length / p.coeffs.length == q.powers.length
                / q.coeffs.length) {
            if (p.powers[vars - 1] > q.powers[vars - 1]) {
                C = p.highCoeff();
                C1 = q.highCoeff();
                v = p.powers[vars - 1] - q.powers[vars - 1];
                beta += v + 1;
                p = p.rem(q, ring);
                if (p.coeffs.length == 0) {
                    break;
                }
                p = p.braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
                result = p; results[indRes++]=p.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring);  //  add 2015 ++++++++++++++]=p;   
                other = q;
            } else {
                C = q.highCoeff();
                C1 = p.highCoeff();
                v = q.powers[vars - 1] - p.powers[vars - 1];
                alfa += v + 1;
                q = q.rem(p, ring);
                if (q.coeffs.length == 0) {
                    break;
                }
                q = q.braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
                result = q; 
                results[indRes++]=q.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring);  //  add 2015 ++++++++++++++]=p;   
                other = p;
            }
        }
        // вывод ответа с домножением на собранный коэффициент
        if (p.coeffs.length == 0 || q.coeffs.length == 0) {
            return new Polynom[] {myZero(ring), myZero(ring)};
        }
        Polynom res = result.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring);
       // results[--indRes]=res;                  //  add 2015 ++++++++++++++
//        System.out.println("res other="+res+"       "+other);
        Polynom[] inverceRes=new Polynom[indRes]; //  add 2015 ++++++++++++++
        int j=indRes-2; //  add 2015 ++++++++++++++
        for (int i = 1; i < indRes; i++) { //  add 2015 ++++++++++++++
            inverceRes[i]=results[j];j--; //  add 2015 ++++++++++++++
        }inverceRes[0]=res; //  add 2015 ++++++++++++++
        return   inverceRes;   //new Polynom[] {res, other}; //  add 2015 ++++++++++++++
    }
    
       public Polynom[][] resultant2Pols(Polynom q1, int varNumb, Ring ring) {
        Polynom p = this, q = q1;
        int ll = ring.varNames.length;
        int pV = p.powers.length / p.coeffs.length;
        int qV = q.powers.length / q.coeffs.length;
        int max = Math.max(pV, qV) - 1;
        if (varNumb >= max) {
            return resultant2Pols(q, ring);
        }
        //отсортировали и заменили этот массив так как нам надо
        int[] mas = new int[ll];//массив сортировки
        for (int i = 0; i < ll; i++) {
            mas[i] = i;
        }
        mas[max] = varNumb;
        mas[varNumb] = max;
        p = p.changeOrderOfVars(mas, true, ring);
        q = q.changeOrderOfVars(mas, true, ring);
        Polynom[][] ress = p.resultant2Pols(q, ring);
        for (int i = 0; i < ress.length; i++) for (int j = 0; j < 2; j++)
             ress[i][j] = ress[i][j].changeOrderOfVars(mas, true, ring);
        return ress;
    } 
    
    public Polynom[][] resultant2Pols(Polynom q, Ring ring) {    
        int vars = powers.length / coeffs.length, alfa, beta,varsQ=q.powers.length / q.coeffs.length;
        if ((vars!=varsQ)||(vars == 1))return new Polynom[0][0];
        int degr= this.degree(vars-1)+1;
        Polynom[][] results= new Polynom[degr][2]; // для всех остатков[0] и их домножителей[1]
        int indRes=2; // index for results
        Polynom p = this;
        int v;
        Polynom C;
        Polynom C1, d, p1, q1;
        Polynom result = null, other = null;
        // нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        // полиномов на соответствующий ему НОД коэффициентов
        p1 = p.GCDHPolCoeffs(ring);
        if (!p1.equals(myOne(ring))) {
            p = p.divideExact(p1, ring);
        }
        q1 = q.GCDHPolCoeffs(ring);
        if (!q1.isOne(ring)) {
            q = q.divideExact(q1, ring);
        }
        d = Polynom.polynom_one(ring.numberONE);
        // Первое деление полиномов без упрощения остатка по методу Брауна, но
        // с нахождением следующего di
        if (p.powers[vars - 1] >= q.powers[vars - 1]) {
            v = p.powers[vars - 1] - q.powers[vars - 1];
            if (p.powers[vars - 1] == 1) {
                alfa = 1;
                beta = 1;
            } else {
                beta = v + 2;
                alfa = 0;
            }
            C1 = q.highCoeff();
            d = d.braunDi(v, C1, ring);
            p = p.rem(q, ring);
            result = p; 
            results[0][0]=q;  results[0][1]= p1.powRecS(alfa, ring).mulSS(q1.powRecS(beta, ring), ring);
            results[1][0]=p;  results[1][1]= p1.powRecS(alfa, ring).mulSS(q1.powRecS(beta, ring), ring);
                //  add 2015 ++++++++++++++
            other = q;
        } else {
            v = q.powers[vars - 1] - p.powers[vars - 1];
            beta = 0;
            alfa = v + 2;
            C1 = p.highCoeff();
            d = d.braunDi(v, C1, ring);
            q = q.rem(p, ring);
            result = q; 
            results[0][0]=p; results[0][1]= p1.powRecS(alfa, ring).mulSS(q1.powRecS(beta, ring), ring);  
            results[1][0]=q; results[0][1]= p1.powRecS(alfa, ring).mulSS(q1.powRecS(beta, ring), ring);
                 //  add 2015 ++++++++++++++
            other = p;
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.coeffs.length > 0
                && q.coeffs.length > 0
                && p.powers.length / p.coeffs.length == q.powers.length
                / q.coeffs.length) {
            if (p.powers[vars - 1] > q.powers[vars - 1]) {
                C = p.highCoeff();
                C1 = q.highCoeff();
                v = p.powers[vars - 1] - q.powers[vars - 1];
                beta += v + 1;
                p = p.rem(q, ring);
                if (p.coeffs.length == 0) {
                    break;
                }
                p = p.braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
                result = p; results[indRes++][0]=p; results[indRes][1]= p1.powRecS(alfa, ring).mulSS(q1.powRecS(beta, ring), ring);  
                 //  add 2015 ++++++++++++++]=p;   
                other = q;
            } else {
                C = q.highCoeff();
                C1 = p.highCoeff();
                v = q.powers[vars - 1] - p.powers[vars - 1];
                alfa += v + 1;
                q = q.rem(p, ring);
                if (q.coeffs.length == 0) {
                    break;
                }
                q = q.braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
                result = q; 
                results[indRes++][0]=q; results[indRes][1]= p1.powRecS(alfa, ring).mulSS(q1.powRecS(beta, ring), ring);   //  add 2015 ++++++++++++++]=p;   
                other = p;
            }
        }
        // вывод ответа с домножением на собранный коэффициент
        if (p.coeffs.length == 0 || q.coeffs.length == 0) {
            return new Polynom[][] {{myZero(ring), myZero(ring)}};
        }
        Polynom res = result.mulSS(p1.powRecS(alfa, ring), ring).mulSS(
                q1.powRecS(beta, ring), ring);
       // results[--indRes]=res;                  //  add 2015 ++++++++++++++
//        System.out.println("res other="+res+"       "+other);
        Polynom[][] inverceRes=new Polynom[indRes][2]; //  add 2015 ++++++++++++++
        int j=indRes-2; //  add 2015 ++++++++++++++
        for (int i = 1; i < indRes; i++) { //  add 2015 ++++++++++++++
            inverceRes[i]=results[j];j--; //  add 2015 ++++++++++++++
        }inverceRes[0][0]=result; inverceRes[0][1]=p1.powRecS(alfa, ring).mulSS(
                q1.powRecS(beta, ring), ring);
//  add 2015 ++++++++++++++
        return   inverceRes;   //new Polynom[] {res, other}; //  add 2015 ++++++++++++++
    }

    /**
     * Процедура возвращающая последовательность остатков при нахождении НОД
     * двух полиномов this и q по алгоритму Евклида
     *
     * @param q один из исходных полиномов
     *
     * @return массив полиномов - остатков при нахождении НОД двух полиномов по
     * алгоритму Евклида
     */
    public Polynom[] prs(Polynom q, Ring ring) {
        int vars = powers.length / coeffs.length, varsQ=q.powers.length / q.coeffs.length;
        if (vars!=varsQ)return new Polynom[0];    // так как старшая переменная должна быть одинаковая!        
        if (vars == 1) {PolynomOneVar t = new PolynomOneVar(this);return t.prs_Zx(q, ring);} // Для одной переменной отдельно
        Polynom  C1, d, p1, q1, p2, q2, gcdcoeff, p = this, C0=Polynom.polynomZero;
        int v, t = 1, dl = q.powers[vars - 1];
        Polynom[] result = new Polynom[dl];
        // нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        // полиномов: делением  на НОД коэффициентов
        p1 = p.GCDHPolCoeffs(ring);
        if (!p1.isOne(ring)) {p = p.divideExact(p1, ring);}
        q1 = q.GCDHPolCoeffs(ring);
        if (!q1.isOne(ring)) {q = q.divideExact(q1, ring);}
        d = myOne(ring);
        gcdcoeff = p1.gcd(q1, ring); p2=p1; q2=q1;
        if(!gcdcoeff.isOne(ring)){p2 = p2.divideExact(gcdcoeff, ring);q2 = q2.divideExact(gcdcoeff, ring);}
        boolean needsMult= !(p1.isOne(ring))&& (q1.isOne(ring))&&(!gcdcoeff.isOne(ring));
        Polynom mult=Polynom.polynomZero;  // 'это паразитный множитель (для случая needsMult=true)
        // Первое деление полиномов без упрощения остатка по методу Брауна, но
        // с нахождением следующего di
        if (p.powers[vars - 1] >= q.powers[vars - 1]) {
            C0 = q.highCoeff();
            v = p.powers[vars - 1] - q.powers[vars - 1];
            d = d.braunDi(v, C0, ring);
            p = p.rem(q, ring);
            result[0] = p;
           if(needsMult) mult= q2.powRecS(v+2, ring).mulSS(gcdcoeff,ring);
        } else {
            C1 = p.highCoeff();
            v = q.powers[vars - 1] - p.powers[vars - 1];
            d = d.braunDi(v, C1, ring);
            q = q.rem(p, ring);
            result[0] = q;
            if(needsMult) mult= p2.powRecS(v+2, ring).mulSS(gcdcoeff, ring);
        }
        if(needsMult) result[0]=result[0].mulSS(mult, ring);
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (p.coeffs.length > 0
                && q.coeffs.length > 0
                && p.powers.length/p.coeffs.length == q.powers.length/q.coeffs.length) {
            if (p.powers[vars - 1] > q.powers[vars - 1]) {
                C0 = p.highCoeff();
                C1 = q.highCoeff();
                v = p.powers[vars - 1] - q.powers[vars - 1];
                p = p.rem(q, ring);
                if (p.coeffs.length == 0) {break;}
               // p = p.braunUpr(d, v, C, ring);
                p=p.divideExact((d.powRecS(v, ring).mulSS(C0, ring)),ring) ;               
                d = d.braunDi(v, C1, ring);// d=C1^v/d^{v-1}
                result[t] = p;
                if(needsMult) mult=mult.mulSS(q2.powRecS((v+1), ring),ring);
            } else {
                C0 = q.highCoeff();
                C1 = p.highCoeff();
                v = q.powers[vars - 1] - p.powers[vars - 1];
                q = q.rem(p, ring);
                if (q.coeffs.length == 0) {break; }
                q=q.divideExact((d.powRecS(v, ring).mulSS(C0, ring)),ring) ;               
                d = d.braunDi(v, C1, ring);// d=C1^v/d^{v-1}
                result[t] = q;    
                if(needsMult) mult=mult.mulSS(p2.powRecS((v+1), ring),ring);
            } t++; 
             if(needsMult)result[t]=result[t].mulSS(mult, ring);
        }  // end of while   
        if (t < dl) {Polynom[] otv = new Polynom[t];
            System.arraycopy(result, 0, otv, 0, t);
            return otv;
        }
        return result;
    }

    /**
     * Процедура, возвращающая полиномы в представлении НОД двух полиномов через
     * их самих. Это алгоритм PRS Брауна с добавкой вычисления сомножителей
     *
     * @param r один из исходных полиномов
     *
     * @return result[0] НОД полиномов
     *
     * @return result[1], result[2], result[3], result[4], result[5] элементы
     * множителей a и b при f и g, с помощью которых можно получить НОД(f, g)по
     * следующей формуле НОД(f, g) =
     * ((f/result[3])*result[1]+(g/result[4])*result[2])/result[5]
     */
    public VectorS extendedGCD(Polynom r, Ring ring) {
        Polynom p = this;
        Polynom q = r;
        Polynom[] result = new Polynom[6];
        result[3] = myOne(ring);
        result[4] = myOne(ring);
        int usl = p.powers.length / p.coeffs.length - q.powers.length
                / q.coeffs.length;
        while (usl != 0) {
            if (usl > 0) {
                if (q.isZero(ring)) {
                    Element temp = p.GCDNumPolCoeffs(ring);
                    result[3] = p.polDivNumb(temp, ring);
                    VectorS VI = temp.extendedGCD(q.coeffs[0], ring);
                    Element[] BI = VI.V;
                    return new VectorS(new Polynom[] {
                        polynomFromNumber(BI[0], ring),
                        polynomFromNumber(BI[1], ring),
                        polynomFromNumber(BI[2], ring), result[3],
                        myOne(ring), myOne(ring)});
                }
                Polynom tempPol = p.GCDHPolCoeffs(ring);
                result[3] = result[3].mulSS(p.divideExact(tempPol, ring), ring);
                p = tempPol;
            } else {
                if (usl < 0) {
                    if (p.isZero(ring)) {
                        Element temp = q.GCDNumPolCoeffs(ring);
                        result[4] = q.polDivNumb(temp, ring);
                        VectorS VI = (p.coeffs[0]).extendedGCD(temp, ring);
                        Element[] BI = VI.V;
                        return new VectorS(new Polynom[] {
                            polynomFromNumber(BI[0], ring),
                            polynomFromNumber(BI[1], ring),
                            polynomFromNumber(BI[2], ring), myOne(ring),
                            result[4], myOne(ring), myOne(ring)});
                    }
                    Polynom tempPol = q.GCDHPolCoeffs(ring);
                    result[4] = result[4].mulSS(q.divideExact(tempPol, ring), ring);
                    q = tempPol;
                }
            }
            usl = p.powers.length / p.coeffs.length - q.powers.length
                    / q.coeffs.length;
        }
        int v, vars = p.powers.length / p.coeffs.length;
        if (vars == 0) {
            VectorS VI = (p.coeffs[0]).extendedGCD(q.coeffs[0], ring);
            Element[] BI = VI.V;
            return new VectorS(new Polynom[] {polynomFromNumber(BI[0], ring),
                polynomFromNumber(BI[1], ring),
                polynomFromNumber(BI[2], ring), result[3], result[4],
                myOne(ring)});
        }
        if (vars == 1) {Polynom[] pol;
            if(ring.isFeild()) return new VectorS(extendedGCDinEDx(q, ring));
            PolynomOneVar t = new PolynomOneVar(p);
            pol = t.ExtendedGCD(q, ring);
            return new VectorS(new Polynom[] {pol[0], pol[1], pol[2],
                pol[3].mulSS(result[3], ring),
                pol[4].mulSS(result[4], ring), pol[5]});
        }
        Polynom C;
        Polynom C1, d, p1, q1, ofF, ofG, gcdcoeff;
        Polynom[] term1 = new Polynom[2];
        Polynom[] term2 = new Polynom[2];
        Polynom[] term = new Polynom[2];
        term1[1] = p;
        term2[1] = q;
        // нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        // полиномов на соответствующий ему НОД коэффициентов
        p1 = term1[1].GCDHPolCoeffs(ring);
        result[3] = result[3].mulSS(p1, ring);
        if (!p1.isOne(ring)) {
            term1[1] = term1[1].divideExact(p1, ring);
        }
        q1 = term2[1].GCDHPolCoeffs(ring);
        result[4] = result[4].mulSS(q1, ring);
        if (!q1.isOne(ring)) {
            term2[1] = term2[1].divideExact(q1, ring);
        }
        gcdcoeff = p1.gcd(q1, ring);
        d = myOne(ring);
        // Первое деление полиномов без упрощения остатка по методу Брауна, но
        // с нахождением следующего di
        if (term1[1].powers[vars - 1] >= term2[1].powers[vars - 1]) {
            term[0] = myZero(ring);
            term[1] = myOne(ring);
            C1 = term2[1].highCoeff();
            v = term1[1].powers[vars - 1] - term2[1].powers[vars - 1];
            term1 = term1[1].divRem(term2[1], ring);
            if (term1[1].coeffs.length != 0) {
                d = d.braunDi(v, C1, ring);
                result[1] = C1.powRecS(v + 1, ring);
                result[2] = (Polynom) term1[0].negate(ring);
            } else {
                result[1] = term[0];
                result[2] = term[1];
            }
        } else {
            term[0] = myOne(ring);
            term[1] = myZero(ring);
            C1 = term1[1].highCoeff();
            v = term2[1].powers[vars - 1] - term1[1].powers[vars - 1];
            term2 = term2[1].divRem(term1[1], ring);
            if (term2[1].coeffs.length != 0) {
                d = d.braunDi(v, C1, ring);
                result[2] = C1.powRecS(v + 1, ring);
                result[1] = (Polynom) term2[0].negate(ring);
            } else {
                result[1] = term[0];
                result[2] = term[1];
            }
        }
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while (term1[1].coeffs.length > 0
                && term2[1].coeffs.length > 0
                && term1[1].powers.length / term1[1].coeffs.length == term2[1].powers.length
                / term2[1].coeffs.length) {
            if (term1[1].powers[vars - 1] > term2[1].powers[vars - 1]) {
                C = term1[1].highCoeff();
                C1 = term2[1].highCoeff();
                v = term1[1].powers[vars - 1] - term2[1].powers[vars - 1];
                term1 = term1[1].divRem(term2[1], ring);
                if (term1[1].coeffs.length == 0) {
                    break;
                }
                // нахождение линейного множителя стоящего при f
                ofF = term[0].mulSS(C1.powRecS(v + 1, ring), ring);
                term[0] = result[1];
                result[1] = ofF.subtract(term1[0].mulSS(result[1], ring), ring);
                // нахождение линейного множителя стоящего при g
                ofG = term[1].mulSS(C1.powRecS(v + 1, ring), ring);
                term[1] = result[2];
                result[2] = ofG.subtract(term1[0].mulSS(result[2], ring), ring);
                // упрощения остатка и сомножителей
                term1[1] = term1[1].braunUpr(d, v, C, ring);
                result[1] = result[1].braunUpr(d, v, C, ring);
                result[2] = result[2].braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
            } else {
                C = term2[1].highCoeff();
                C1 = term1[1].highCoeff();
                v = term2[1].powers[vars - 1] - term1[1].powers[vars - 1];
                term2 = term2[1].divRem(term1[1], ring);
                if (term2[1].coeffs.length == 0) {
                    break;
                }
                // нахождение линейного множителя стоящего при f
                ofF = term[0].mulSS(C1.powRecS(v + 1, ring), ring);
                term[0] = result[1];
                result[1] = ofF.subtract(term2[0].mulSS(result[1], ring), ring);
                // нахождение линейного множителя стоящего при g
                ofG = term[1].mulSS(C1.powRecS(v + 1, ring), ring);
                term[1] = result[2];
                result[2] = ofG.subtract(term2[0].mulSS(result[2], ring), ring);
                // упрощение остатка и сомножителей
                term2[1] = term2[1].braunUpr(d, v, C, ring);
                result[1] = result[1].braunUpr(d, v, C, ring);
                result[2] = result[2].braunUpr(d, v, C, ring);
                d = d.braunDi(v, C1, ring);
            }
        }

        // Вывод НОД в случае, когда он лежит в том же кольце,
        // что и многочлены p и q
        if (term1[1].coeffs.length == 0) {
            q1 = term2[1].GCDHPolCoeffs(ring);
            result[5] = q1;
            if (!q1.equals(myOne(ring))) {
                term2[1] = term2[1].divideExact(q1, ring);
            }
            if (!gcdcoeff.equals(myOne(ring))) {
                term2[1] = term2[1].mulSS(gcdcoeff, ring);
            }
            result[0] = term2[1];
        } else {
            if (term2[1].coeffs.length == 0) {
                p1 = term1[1].GCDHPolCoeffs(ring);
                result[5] = p1;
                if (!p1.equals(myOne(ring))) {
                    term1[1] = term1[1].divideExact(p1, ring);
                }
                if (!gcdcoeff.equals(myOne(ring))) {
                    term1[1] = term1[1].mulSS(gcdcoeff, ring);
                }
                result[0] = term1[1];
            } else {
                // Вывод НОД в случае, когда он лежит в кольце многочленов от
                // меньшего
                // количества переменных, чем vars
                result[0] = gcdcoeff;
                if (term1[1].powers.length / term1[1].coeffs.length < term2[1].powers.length
                        / term2[1].coeffs.length) {
                    result[5] = term1[1];
                } else {
                    result[5] = term2[1];
                }
            }
        }
        result[1] = result[1].mulSS(gcdcoeff, ring);
        result[2] = result[2].mulSS(gcdcoeff, ring);
        return new VectorS(result);
    }

    /**
     * Процедура сравнивает два полинома
     *
     * @param p один из полиномов
     *
     * @return возвращает true, если полином this старше полинома p, и false -
     * если младше или равен
     */
    public boolean greater(Polynom p, Ring ring) {
        if (coeffs.length == 0) {
            return false;
        }
        if (p.coeffs.length == 0) {
            return true;
        }
        if (powers.length / coeffs.length > p.powers.length / p.coeffs.length) {
            return true;
        }
        if (powers.length / coeffs.length < p.powers.length / p.coeffs.length) {
            return false;
        }
        int leng = Math.min(coeffs.length, p.coeffs.length), vars = p.powers.length
                / p.coeffs.length;
        for (int i = 0; i < leng; i++) {
            for (int j = vars - 1; j >= 0; j--) {
                if (powers[i * vars + j] > p.powers[i * vars + j]) {
                    return true;
                }
                if (powers[i * vars + j] < p.powers[i * vars + j]) {
                    return false;
                }
            }
            int comp = coeffs[i].compareTo(p.coeffs[i], ring);
            if (comp > 0) {
                return true;
            }
            if (comp < 0) {
                return false;
            }
        }
        if (coeffs.length > p.coeffs.length) {
            return true;
        }
        return false;
    }

    /**
     * divideAndRemainderFromNumber this by the number p. Element p may be
     * number of constant polynomial. 
     * But p is not complex number.
     *
     * @param p - number  (not complex)
     *
     * @return Polynom[] quotient and remainder
     */
    public Polynom[] divideAndRemainderFromNumber(Element p, Ring ring) {
        Element P=(p instanceof Polynom) ? ((Polynom) p).coeffs[0] : p; 
        int ty=P.numbElementType();
        if((ty!=Ring.Z)&&(ty!=Ring.Z64))
            return new Polynom[]{divideByNumber(P,ring), Polynom.polynomZero};
        NumberZ den = (NumberZ) P.toNumber(Ring.Z, ring);
        int[] pow = new int[powers.length]; // степени
        System.arraycopy(powers, 0, pow, 0, powers.length);
        Element[] mas1 = new Element[coeffs.length];
        Element[] mas2 = new Element[coeffs.length];
        Element[] cf = new Element[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
                cf[i] = (coeffs[0] instanceof NumberZ)? coeffs[i]: coeffs[i].toNumber(Ring.Z, ring);
            }
        for (int i = 0; i < cf.length; i++) {
            Element[] mas = ((NumberZ) cf[i]).divideAndRemainder(den);
            mas1[i] = mas[0];
            mas2[i] = mas[1];
        }
        Polynom pol1 = new Polynom(pow, mas1);
        Polynom pol2 = new Polynom(pow, mas2);
        pol1 = pol1.deleteZeroCoeff(ring);
        pol2 = pol2.deleteZeroCoeff(ring);
        return new Polynom[] {pol1, pol2};
    }

    /**
     * Процеура сокращения полиномов на их НОД
     *
     * @param r - один из полиномов
     *
     * @return полиномы сокращённые на их НОД
     */
    public Polynom[] cancel(Polynom r, Ring ring) {
        Polynom t = gcd(r, ring);
        return new Polynom[] {divideExact(t, ring), r.divideExact(t, ring)};
    }

    /**
     * Процеура сокращения коэффициентов полинома
     *
     *
     * @return полином после сокращения всех коэффициентов
     */
    public Polynom cancelCoeffs(Ring ring) {
        for (int i = 0; i < coeffs.length; i++) {
            Element fi = coeffs[i];
            if (fi instanceof Fraction) {
                coeffs[i]
                        = ((Fraction) fi).cancel(ring);
            }
        }
        return this;
    }

    /**
     * Процедура сравнивает два полинома
     *
     * @param p один из полиномов
     *
     * @return возвращает 1, если полином this старше полинома p, -1 - если
     * младше и 0 - равен
     */
    public static int compareTo(Element p, Element pp, Ring ring) {
        return ((Polynom) p).compareTo((Polynom) pp, ring);
    }

    @Override
    public int numbElementType() {
        return Ring.Polynom;
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        if (isItNumber()) {if (coeffs.length==0) return false;
            return coeffs[0].isMinusOne(ring);
        }
        return false;
    }

    @Override
    public Boolean isNegative() {
        return (signum() < 0) ? true : false;
    }

    @Override
    public Boolean isOne(Ring ring) {
        if (coeffs.length == 0) {
            return false;
        }
        if (powers.length == 0) {
            return coeffs[0].isOne(ring);
        }
        if (powers.length == 1 && powers[0] == 0) {
            return coeffs[0].isOne(ring);
        }
        return false;
    }
      /**
     * Процедура сравнивает  полином и Элемент.
     *
     * @param p один из полиномов
     * @param ring
     *
     * @return возвращает 1, если   this старше   p, -1 - если
     * младше и 0 - равен 
     * (все числа считаются младше всех полиномов, а полиномы --  младше всего остального)
     */
    @Override
    public int compareTo(Element p, Ring ring) { boolean pPolFl=(p instanceof Polynom);
           if (pPolFl) return compareTo((Polynom)p,  ring);
          boolean numbThis=this.isItNumber(), numbP=p.isItNumber();
          if (numbThis&&numbP) return coeffs[0].compareTo(p,  ring);
          return (numbP)? 1:-1;
}
    /**
     * Процедура сравнивает два полинома
     *
     * @param p один из полиномов
     * @param ring
     *
     * @return возвращает 1, если полином this старше полинома p, -1 - если
     * младше и 0 - равен
     */
    public int compareTo(Polynom p, Ring ring) {
        int length_p = p.coeffs.length;
        int length_this = coeffs.length;
        if ((length_p == 0) && (length_this == 0)) {
            return 0;
        }
        if (length_p == 0) {
            return signum();
        }
        if (length_this == 0) {
            return -p.signum();
        }
        int v = p.coeffs[0].numbElementType();
        int m = coeffs[0].numbElementType();
        if (m > v) {
            return 1;
        } else if (m < v) {
            return -1;
        } else {
            if (powers.length / coeffs.length > p.powers.length
                    / p.coeffs.length) {
                return 1;
            }
            if (powers.length / coeffs.length < p.powers.length
                    / p.coeffs.length) {
                return -1;
            }
            int leng = Math.min(coeffs.length, p.coeffs.length), vars = p.powers.length
                    / p.coeffs.length;
            for (int i = 0; i < leng; i++) {
                for (int j = vars - 1; j >= 0; j--) {
                    if (powers[i * vars + j] > p.powers[i * vars + j]) {
                        return 1;
                    }
                    if (powers[i * vars + j] < p.powers[i * vars + j]) {
                        return -1;
                    }
                }
                int comp = coeffs[i].compareTo(p.coeffs[i], ring);
                if (comp > 0) {
                    return 1;
                }
                if (comp < 0) {
                    return -1;
                }
            }
            if (coeffs.length > p.coeffs.length) {
                return 1;
            } else {
                if (coeffs.length < p.coeffs.length) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public Element abs(Ring ring) {
        if (isZero(ring)) {return ring.numberZERO;}
        return (isItNumber()) ? coeffs[0].abs(ring) : new F(F.ABS, this);
    }

//    @Override
//    public Element[] divideAndRemainder(Element p, Ring ring) {
//        if (p instanceof Polynom) {
//            return this.divAndRem((Polynom) p, ring);
//        }
//        return new Element[] {ring.numberZERO, this};
//    }

    public Polynom remainder(Element p, Ring ring) {
        return myZero(ring);
    }

    @Override
    public int signum() {
        return coeffs.length == 0 ? 0 : (coeffs[0]).signum();
    }

    public Polynom modInverse(Element m, Ring ring) {
        return myZero(ring);
    }

    public Polynom modPow(Element exp, Element m, Ring ring) {
        return myZero(ring);
    }

    /**
     * Возведение числа a в степень с при помощи массива.
     *
     * @param VarNum количество переменных
     * @param c
     * @param power матрица степеней (для каждой переменной массив)
     *
     * @return
     */
    private Element degree(int VarNum, int c, Element[][] power, Ring ring) {
        Element p = power[VarNum][c];
        Element one = power[0][0].myOne(ring);
        if (p.isZero(ring)) {
            int d = c, j = 1;
            p = one;
            while (d > 0) {
                if ((d & 1) == 1) {
                    p = p.multiply(power[VarNum][j], ring);
                } // выбираем из массива нужную степень и накапливаем в
                // произведеение p
                d >>= 1;
                j <<= 1; // счетчик
            }
            power[VarNum][c] = p;
        }
        return p;
    }

    /**
     * создается массив степеней значений неизвестных. Он содержит var строк
     * (число переменных). MaxPowerDif[i]+1 - число элементов в i-строке. в нем
     * заполняются элементы 1, 2, 4, 8, .....
     *
     * @param ValuesOfVars
     * @param MaxPowerDif
     *
     * @return
     */
    private Element[][] initPowers(Element[] ValuesOfVars, int[] MaxPowerDif,
            Ring ring) {
        Element zero = ValuesOfVars[0].myZero(ring);
        int n = MaxPowerDif.length;
        Element m[][] = new Element[n][];
        for (int i = 0; i < n; i++) {
            m[i] = new Element[MaxPowerDif[i] + 1];
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m[i].length; j++) {
                m[i][j] = zero;
            }
        }

        for (int i = 0; i < n; i++) {
            if (MaxPowerDif[i] != 0) {
                int j = 2;
                Element a = ValuesOfVars[i];
                m[i][1] = a;
                while (j <= MaxPowerDif[i]) {
                    a = a.multiply(a, ring);
                    m[i][j] = a;
                    j <<= 1;
                }
            }
        }
        return m;
    }
//     /**
//     * Maximal degree for each variable k (k=0,..,numberOfVar-1)
//     * @param ring
//     * @return VectorS with NumberZ array with maximal degree of each variable  
//     */
//    @Override
//        public Element degrees(Ring ring) {
//           int[] degr= degrees();
//           Element[] dd=new NumberZ[degr.length];
//            for (int i = 0; i < degr.length; i++) dd[i]=new NumberZ(degr[i]);
//            return new VectorS(dd);    
//        }
    /**
     * Maximal degree for each variable k (k=0,..,numberOfVar-1)
     *
     * @return integer array with maximal degree of each variable  
     */
    public int[] degrees() {
        int l = this.coeffs.length; // number of monomials
        int n = this.powers.length / l; // number of variables
        int prev[] = new int[n];
        System.arraycopy(powers, 0, prev, 0, n);
        int j = n;
        for (int s = 1; s < l; s++) {
            for (int k = 0; k < n - 1; k++) {
                if (powers[j] > prev[k]) {
                    prev[k] = powers[j];
                }
                j++;
            }
            j++;
        }
        return prev;
    }
/**
     * Total degree of the first monomial of this polynomial:
     * the sum of all powers in the leading monomial in this polynomial
     *
     * @return integer which is the total degree of the first monomial of this polynomial 
     */
    public int degreeOfFirstMonomial() { if(coeffs.length==0)return 0;
        int var=this.powers.length/this.coeffs.length;
        int total=0;
        for (int s = 0; s < var; s++)  total+=powers[s];
        return total;
    }
    /**
     * Total degree of  this polynomial:
     * the sum of all powers in the some maximal monomial in this polynomial
     *
     * @return integer which is the total degree of this polynomial 
     */
    public int degreeTotal() { if(coeffs.length==0)return 0;
        int var= powers.length/coeffs.length;
        int total=0, s=0, lock=0;
        for (int i = 0; i < powers.length; i++) {
            if(s==var){total=Math.max(total, lock); lock=0;s=0;}
            lock+=powers[s];s++;
        }   total=Math.max(total, lock);
        return total;
    }
  
    
    private int[] maxPowerDif() {
        int l = this.coeffs.length; // number of monomials
        int n = this.powers.length / l; // number of variables
        int pow[] = new int[n];
        int prev[] = new int[n];
        // в prev[] записываются степени первого монома, pows[] = 0
        System.arraycopy(powers, 0, prev, 0, n);
        for (int s = 1; s < l; s++) { // цикл по всем мономам
            int i = n - 1;
            while (i > 0 && prev[i] == powers[i + s * n]) {
                i--; // пропускаем переменные в двух соседних мономах,
                // у которых степени совпадают
            }
            // i - ведущая переменная в схеме Горнера, для нее вычисляем
            // разность степеней в двух соседних мономах
            pow[i] = Math.max(pow[i], prev[i] - powers[i + s * n]);
            prev[i] = powers[i + s * n];
            // для всех переменных младше i устанавливаетсая максимальное
            // значение
            for (int j = 0; j < i; j++) {
                pow[j] = Math.max(pow[j], prev[j]);
                prev[j] = powers[j + s * n];
            }
        }
        for (int i = 0; i < n; i++) {
            pow[i] = Math.max(pow[i], prev[i]);
        }
        return pow;
    }

    /**
     * подстановка в полином вместо переменных полиномов с удалением возникающих
     * нулевых кооэффециентов
     */
    /**
     * Горнер для var переменных
     *
     * @param ValuesOfVars аргументы
     *
     * @return значение
     *
     * тут нет заготовок степеней, как для double -> нужно улучшить
     *
     * public Element value(Element[] ValuesOfVars) { Element zero =
     * ValuesOfVars[0].myZero(ring); int l = coeffs.length; int var =
     * powers.length / l;
     *
     * if (var == 0) { if (l == 0) { return zero; } else { return
     * coeffs[0];//случай когда на входе функция от числа } } Element
     * PowArrays[][] = initPowers(ValuesOfVars, maxPowerDif()); int i_; int k;
     * Element c = zero; Element s[] = new Element[var]; // частичные cсуммы по
     * каждой переменной for (int i = 0; i < var; i++) { s[i] = zero; } //
     * Основной цикл for (int i = 1; i < l; i++) { i_ = i - 1; k = var - 1;
     * while (powers[i * var + k] == powers[i_ * var + k]) { k--; } if (k == 0)
     * { s[0] = s[0].add(coeffs[i_]).multiply(degry(0, powers[i_ * var] -
     * powers[i * var], PowArrays)); } else { c = (Element) coeffs[i_]; for (int
     * j = 0; j < k; j++) { c = s[j].add(c).multiply(degry(j, powers[i_ * var +
     * j], PowArrays)); s[j] = zero; } s[k] = s[k].add(c).multiply(degry(k,
     * powers[i_ * var + k] - powers[i * var + k], PowArrays)); } } //
     * Завершение k = var - 1; while (powers[(l - 1) * var + k] == 0 && k > 0) {
     * k--; } if (k == 0) { s[0] = (Element) s[0].add( coeffs[l-
     * 1]).multiply(degry(0, powers[(l - 1) * var], PowArrays)); } else { c =
     * (Element) coeffs[l - 1]; for (int j = 0; j < k; j++) { c =
     * s[j].add(c).multiply(degry(j, powers[(l - 1) * var + j], PowArrays));
     * s[j] = zero; } int re = (l - 1) * var + k; Element se = degry(k,
     * powers[re], PowArrays); s[k] = s[k].add(c).multiply(se);
     *
     * } for (int i = 0; i < var - 1; i++) { s[var - 1] = s[var - 1].add(s[i]);
     * } return s[var - 1]; }
     */
 /**
  * Вычисление значения полинома в точке. Значения переменных берутся из page.expr
  * @param page указатель на  page
  * @param ring Ring
  * @return значение полинома в точке
  */
        @Override
    public Element value(Page page, Ring ring) {
        int l = coeffs.length;
        if (l == 0) { return ring.numberZERO;}
        int var = powers.length / l;
        if (var == 0) { return coeffs[0];}
        Element[] ValuesOfVars=new Element[var];
        int le= page.expr.size();
        for (int i = 0; i < var; i++) { String s=ring.varNames[i];
        find:{for (int j = le-1; j >= 0; j--) { Element g=page.expr.get(j);
                if(g instanceof Fname){Fname fn=(Fname)g;
                    if((fn.name.equals(s))&&fn.X!=null){ValuesOfVars[i]=fn.X[0]; break find;}}
            } ValuesOfVars[i]=ring.varPolynom[i];  }
        }
        return value(ValuesOfVars, ring);
    }

    @Override
    public Element value(Element[] ValuesOfVars, Ring ring) {
        int len = ValuesOfVars.length;
        if (len == 0) { return this;
        }
        int l = coeffs.length;
        if (l == 0) { return ring.numberZERO;}
        Element zero = coeffs[0].myZero(ring);
        int var = powers.length / l;
        if (var == 0) {return coeffs[0];}
        Element PowArrays[][] = initPowers(ValuesOfVars, maxPowerDif(), ring);
        int i_;
        int k;
        Element c;
        Element s[] = new Element[var]; // частичные cсуммы по каждой переменной
        for (int i = 0; i < var; i++) {s[i] = zero;}
        // Основной цикл по коэффициентам
        for (int i = 1; i < l; i++) {
            i_ = i - 1;/*предыд коэфф. */ k = var - 1;// старшая. переменн.
            while (powers[i * var + k] == powers[i_ * var + k]) {k--;}
            if (k == 0) {s[0] = s[0].add(coeffs[i_], ring).multiply(
                        Polynom.this.degree(0, powers[i_ * var] - powers[i * var], PowArrays, ring), ring);
            } else {
                c = coeffs[i_];
                for (int j = 0; j < k; j++) {
                    c = s[j].add(c, ring).multiply(
                            Polynom.this.degree(j, powers[i_ * var + j], PowArrays, ring), ring);
                    s[j] = zero;
                }
                s[k] = s[k].add(c, ring).multiply(
                        Polynom.this.degree(k, powers[i_ * var + k] - powers[i * var + k], PowArrays, ring), ring);
            }
        }

        // Завершение
        k = var - 1;
        while (powers[(l - 1) * var + k] == 0 && k > 0) {
            k--;
        }
        if (k == 0) {
            s[0] = (s[0].add(coeffs[l - 1], ring)).multiply(
                    Polynom.this.degree(0, powers[(l - 1) * var], PowArrays, ring), ring);
        } else {
            c = coeffs[l - 1];
            for (int j = 0; j < k; j++) {
                c = s[j].add(c, ring).multiply(
                        Polynom.this.degree(j, powers[(l - 1) * var + j], PowArrays, ring), ring);
                s[j] = zero;
            }
            int re = (l - 1) * var + k;
            Element se = Polynom.this.degree(k, powers[re], PowArrays, ring);
            s[k] = s[k].add(c, ring).multiply(se, ring);

        }
        for (int i = 0; i < var - 1; i++) {
            s[var - 1] = s[var - 1].add(s[i], ring);
        }
        Element f = s[var - 1];
        int t = f.numbElementType();
        if ((t & Ring.Polynom) == Ring.Polynom) {
            return (((Polynom) f).deleteZeroCoeff(ring));
        }
        return f;
    }

    /**
     * ****************************************************************************
     * Вычисление значения полинома одной переменной в точке Вычисление
     * происходит по схеме Горнера. Предварительно вычисляется степень 2. Затем
     * происходит подстановка.
     *
     * @param var аргумент полинома
     *
     * @return значение полинома
     */
    public Element valueOf(Element var, Ring ring) {
        Element[] x = new Element[1];
        x[0] = var;
        return value(x, ring);
    }

    /**
     * Метод вычисляющий промежуточный остаток при делении двух полиномов
     * столбиком, при этом старшие мономы в действиях не участвуют
     *
     * @param divisor исходный делитель
     * @param quot элемент частного
     *
     * @return промежуточный остаток при делении двух полиномов столбиком
     */
    private Polynom interimRem(Polynom divisor, Polynom quot, Ring ring) {
        Polynom q = divisor.clone();
        int lengRem = coeffs.length, lengDiv = q.coeffs.length;
        // нахожднние произведения делителя и элемента частного
        for (int i = 1; i < lengDiv; i++) {
            q.coeffs[i] = q.coeffs[i].multiply(quot.coeffs[0], ring);
        }
        if (quot.powers.length != 0) {
            for (int i = 1; i < lengDiv; i++) {
                q.powers[i] += quot.powers[0];
            }
        }
        Element[] newCoef = new Element[lengRem + lengDiv - 2];
        int[] newPow = new int[lengRem + lengDiv - 2];
        int k = 1, n = 1, t = 0;
        // нахождения промежуточного остатка(вычитание двух полиномов 1-ой
        // степени без первых мономов)
        while (k < lengRem && n < lengDiv) {
            if (powers[k] == q.powers[n]) {
                Element temp;
                temp = coeffs[k].subtract(q.coeffs[n], ring);
                if (temp.signum() != 0) {
                    newCoef[t] = temp;
                    newPow[t] = q.powers[n];
                    t++;
                }
                k++;
                n++;
            } else {
                if (powers[k] > q.powers[n]) {
                    newCoef[t] = coeffs[k];
                    newPow[t] = powers[k];
                    k++;
                    t++;

                } else {
                    newCoef[t] = q.coeffs[n].negate(ring);
                    newPow[t] = q.powers[n];
                    n++;
                    t++;
                }
            }
        }
        if (k < lengRem) {
            for (int i = k; i < lengRem; i++) {
                newCoef[t] = coeffs[i];
                newPow[t] = powers[i];
                t++;
            }
        } else {
            for (int i = n; i < lengDiv; i++) {
                newCoef[t] = q.coeffs[i].negate(ring);
                newPow[t] = q.powers[i];
                t++;
            }
        }
        // вывод ответа
        if (t == 1 && newPow[0] == 0) {
            return new Polynom(new int[] {}, new Element[] {newCoef[0]});
        }
        if (t < lengRem + lengDiv - 2) {
            Element[] Coef = new Element[t];
            int[] Pow = new int[t];
            System.arraycopy(newPow, 0, Pow, 0, t);
            System.arraycopy(newCoef, 0, Coef, 0, t);
            return new Polynom(Pow, Coef);
        }
        return new Polynom(newPow, newCoef);
    }

    /**
     * Процедура нахождения остатка при делении двух полиномов 1-ой степени
     *
     * @param q один из полиномов
     *
     * @return остаток от деления this на q
     */
    public Polynom remainderRx(Polynom q, Ring ring) {
        if (q.isZero(ring)) {
            return q.myZero(ring);
        }
        if (powers.length == 0 || powers[0] < q.powers[0]) {
            return this;
        }
        Polynom p = this;
        Polynom chastnoe = new Polynom();
        chastnoe.coeffs = new NumberR64[1];
        // деление полиномов
        while (!p.isZero(ring) && q.powers[0] <= p.powers[0]) {
            // выделение элемента частного
            chastnoe.coeffs[0] = p.coeffs[0].divide(q.coeffs[0], ring);
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {p.powers[0] - q.powers[0]};
            }
            p = p.interimRem(q, chastnoe, ring);
        }
        return p;
    }

    /**
     * Процедура умножения полинома на число Element
     *
     * @param r число, на которое умножается полином
     *
     * @return полином, коэффициенты которого умножаются на число r  
     */
    public Polynom multiplyByNumber(Element nubm, Ring ring) {
        if (nubm.isOne(ring)) {return this; }
        Polynom res = new Polynom();
        res.powers = powers;
        res.coeffs = new Element[coeffs.length];
        Element Numb=(nubm instanceof Polynom)?((Polynom)nubm).coeffs[0]:nubm;
        for (int i = 0; i < coeffs.length; i++) {
            res.coeffs[i] = coeffs[i].multiply(nubm, ring);
        }
            res = res.deleteZeroCoeff(ring);
        return res;
    }

    /**
     * Процедура нахождения частного  при делении двух полиномов
     * this и q  одной переменной над полем R,Q,Zp....
     *
     * @param q делитель
     * @return полином - частное от деления this на q;
     */
//    public Polynom divideRx(Polynom q, Ring ring) {
//        Polynom p = this;
//        if (q.isZero(ring)) {
//            return p.multiplyByNumber(q.coeffs[0], ring);
//        }
//        if (powers.length == 0 || powers[0] < q.powers[0]) {
//            return q.myZero(ring);
//        }
//        int t = 0;
//        Polynom[] chast = new Polynom[p.powers[0] - q.powers[0] + 1];
//        // деление полиномов
//        while (!p.isZero(ring) && q.powers[0] <= p.powers[0]) {
//            // выделение элемента частного
//            Polynom chastnoe = new Polynom();
//            chastnoe.coeffs = new Element[1];
//            chastnoe.coeffs[0] = p.coeffs[0].divide(q.coeffs[0], ring);
//            if (p.powers[0] == q.powers[0]) {
//                chastnoe.powers = new int[] {};
//            } else {
//                chastnoe.powers = new int[] {p.powers[0] - q.powers[0]};
//            }
//            chast[t] = chastnoe;
//            p = p.interimRem(q, chast[t], ring);
//            t++;
//        }
//        // Собирание результата частного
//        if (t == 1) {
//            return chast[0];
//        }
//        Polynom result = new Polynom();
//        result.coeffs = new Element[t];
//        result.powers = new int[t];
//        for (int i = 0; i < t - 1; i++) {
//            result.coeffs[i] = chast[i].coeffs[0];
//            result.powers[i] = chast[i].powers[0];
//        }
//        if (chast[t - 1].isZero(ring)) {
//            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
//            result.powers[t - 1] = 0;
//        } else {
//            result.coeffs[t - 1] = chast[t - 1].coeffs[0];
//            result.powers[t - 1] = chast[t - 1].powers[0];
//        }
//        return result;
//    }
//
//    ================
//
//    /**
//     * Процедура нахождения частного
//     *
//     * @param q делитель
//     * @param mod модуль кольца
//     *
//     * @return полином - частное от деления this на q;
//     */
    public Polynom divideRQZpx(Polynom q, Ring ring) {
        Polynom p = this;
        Element one = q.one(ring);
        Element zero = q.zero(ring);
        if (q.isZero(ring)) {
            return p.multiplyByNumber(q.coeffs[0], ring);
        }
        if (powers.length == 0 || powers[0] < q.powers[0]) {
            return Polynom.polynomFromNumber(zero, ring);
        }
        Element inversFirstMonQ = one.divide(q.coeffs[0], ring);
        int t = 0;
        Polynom[] chast = new Polynom[p.powers[0] - q.powers[0] + 1];
        // деление полиномов
        while (!p.isZero(ring) && q.powers[0] <= p.powers[0]) {
            // выделение элемента частного
            Polynom chastnoe = new Polynom();
            chastnoe.coeffs = new Element[1];
            chastnoe.coeffs[0] = (p.coeffs[0].multiply(inversFirstMonQ, ring));
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {p.powers[0] - q.powers[0]};
            }
            chast[t] = chastnoe;
            p = p.interimRem(q, chast[t], ring);
            t++;
        }
        // Собирание результата частного
        if (t == 1) {
            return chast[0];
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
        return result;
    }
    
    @Override
    public Polynom D(int num, Ring ring) {
        if (isItNumber()) {
            return myZero(ring);
        }

        int Kol_perem = powers.length / coeffs.length;

        Element zero = coeffs[0].zero(ring);
        if (Kol_perem <= num) {
            return myZero(ring);
        }

        Element[] Coeffs = coeffs.clone();
        int[] Powers = powers.clone();
        int n = num;
        for (int i = 0; i < coeffs.length; i++) {
            if (Powers[n] == 0) {
                Coeffs[i] = zero;
            } else {
                Coeffs[i] = Coeffs[i].multiply(
                        Coeffs[i].valOf(Powers[n], ring), ring);
                Powers[n]--;
            }
            n += Kol_perem;
        }
        Polynom d = new Polynom(Powers, Coeffs);
        d=d.deleteZeroCoeff(ring);
        d = d.ordering(0, 0, d.coeffs.length, ring);
        return d;
    }

    @Override
    public Polynom D(Ring ring) {
        return D(0, ring);
    }

    /**
     * Интеграл по первой переменной
     */
    @Override
    public Polynom integrate(Ring ring) {
        return integrate(0, ring);
    }

    /**
     * Интеграл по переменной num
     *
     * @param num -- the number of variable in ring
     * @param ring -- Ring
     *
     * @return -- symbolic integral of polynomial
     */
    @Override
    public Polynom integrate(int num, Ring ring) {
        if (isZero(ring)) {
            return this;
        }
        int Kol_perem = powers.length / coeffs.length;
        int ringType = coeffs[0].numbElementType();
        Element[] Coeffs;
        int[] Powers;
        if ((ringType == Ring.Z) || (ringType == Ring.Z64)) {
            Coeffs = new Element[coeffs.length];
            for (int i = 0; i < coeffs.length; i++) {
                Coeffs[i] = coeffs[i].toNumber(Ring.Q, ring);
            }
        } else {
            Coeffs = coeffs.clone();
        }
        if (Kol_perem <= num) {
            Powers = new int[coeffs.length * num];
            int j = 0;
            if (powers.length == 0) {
                Powers[num] = 1;
            }
            for (int i = 0; i < powers.length; i += Kol_perem) {
                System.arraycopy(powers, i, Powers, j, Kol_perem);
                j += num;
                Powers[j - 1] = 1;
            }
        } else {
            Powers = powers.clone();
            int n = num;
            for (int i = 0; i < coeffs.length; i++) {
                Powers[n]++;
                Coeffs[i] = Coeffs[i].
                        divide(Coeffs[i].valOf(Powers[n], ring), ring);
                n += Kol_perem;
            }
        }
        return new Polynom(Powers, Coeffs);
    }

    /**
     * Процедура упрощения коэффициентов в остатке
     *
     * @param di промежуточный множитель
     * @param Ci старший коэффициент в делимом
     * @param vi разность старших степеней полиномов
     *
     * @return this полином, который упрощается
     */
    private Polynom braunUprZx(Element di, int vi, Element Ci, Ring ring) {
        return polDivNumb(((di.negate(ring)).pow(vi, ring)).multiply(
                Ci.negate(ring), ring), ring);
    }

    /**
     * Процедура нахождения остатка при делении двух полиномов от 1-ой
     * переменной
     *
     * @param q один из полиномов
     *
     * @return остаток от деления
     */
    private Polynom remZx(Polynom q, Ring ring) {
        Polynom p = this;
        Element firstmonQ = q.one(ring);
        int t = 0, pw = p.powers[0] - q.powers[0] + 1;
        Polynom chastnoe = new Polynom();
        chastnoe.coeffs = new Element[1];
        boolean not_one_hc = true;
        // проверка коэффициента при старшей степени z в полиноме q на равенство
        // 1
        if ((q.coeffs[0]).equals(q.one(ring))) {
            not_one_hc = false;
        } else {
            firstmonQ = q.coeffs[0];
        }
        // деление полиномов
        while ((p.powers.length != 0) && (q.powers[0] <= p.powers[0])) {
            // выделение элемента частного
            chastnoe.coeffs[0] = p.coeffs[0];
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {p.powers[0] - q.powers[0]};
            }
            // домножение мн-на p на коэффициент при старшей степени z
            if (not_one_hc) {
                p = p.multiplyByNumber(firstmonQ, ring);
            }
            p = p.interimRem(q, chastnoe, ring);
            t++;
        }
        // домножение остатка
        if (not_one_hc && t != pw) {
            p = p.multiplyByNumber(firstmonQ.pow(pw - t, ring), ring);
        }
        if (pw % 2 == 1 && firstmonQ.signum() == -1) {
            return (Polynom) p.negate(ring);
        }
        return p;
    }

    /**
     * Процедура вычисляющая последующий промежуточный множитель в алгоритме
     * Брауна, имея в качестве входного параметра предыдущее значение множителя
     * (this)
     *
     * @param d предыдущее значение параметра di
     * @param vi разность степеней делимого и делителя
     * @param Ci1 старший коэффициент в делителе
     *
     * @return возвращает последующий промежуточный множитель в алгоритме Брауна
     */
    private static Element braunDiZx(Element d, int vi, Element Ci1, Ring ring) {
        Element di;

        Ci1 = Ci1.pow(vi, ring);
        if (vi != 0) {
            if (vi == 1) {
                return Ci1;
            } else {
                //  Ci1 = Ci1.pows(vi, ring);
                di = d.pow((vi - 1), ring);
                di = Ci1.divide(di, ring);
                return di;
            }
        } else {
            return d.myOne(ring);
        }

    }

    /**
     * Процедура нахождения НОД полиномов this и q от 1-ой переменной
     *
     * @param q один из полиномов, чей НОД ищется
     *
     * @return НОД полиномов
     */
    public Polynom gcdZx(Polynom q, Ring ring) {
        Element one = q.one(ring);
        if (coeffs.length == 1 || q.coeffs.length == 1) {
            return monomGCD(q, ring);
        }
        Polynom p = this;
        if (!(ring.algebra[0] == Ring.Z)) {
            Element c1 = p.coeffs[0];
            Element c2 = q.coeffs[0];
            if (!c1.isOne(ring)) {
                p = p.polDivNumb(c1, ring);
            }
            if (!c2.isOne(ring)) {
                q = q.polDivNumb(c2, ring);
            }
        }

        int v;
        Element p2, q2, C, C1, gcdBI;
        // нахождение НОД коэффициентов полиномов p и q с последующем упрощением
        // полиномов на соответствующий ему НОД коэффициентов
        p2 = p.GCDNumPolCoeffs(ring);
        if (!p2.isOne(ring)) {
            p = p.polDivNumb(p2, ring);
        }
        q2 = q.GCDNumPolCoeffs(ring);
        if (!q2.isOne(ring)) {
            q = q.polDivNumb(q2, ring);
        }
        gcdBI = p2.GCD(q2, ring);
        Element d = q.one(ring);
        // Первое деление полиномов без упрощения остатка по методу Брауна, но
        // с нахождением следующего di

        if (p.powers[0] >= q.powers[0]) {
            C1 = q.coeffs[0];
            v = p.powers[0] - q.powers[0];
            d = braunDiZx(d, v, C1, ring);
            p = p.remZx(q, ring);
        } else {
            C1 = p.coeffs[0];
            v = q.powers[0] - p.powers[0];
            d = braunDiZx(d, v, C1, ring);
            q = q.remZx(p, ring);
        }

        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // Последующие деления двух полиномов по алгоритму Евклида с применением
        // упрощения по методу Брауна
        while ((p.powers.length > 0) && (q.powers.length > 0)) {
            if (p.powers[0] > q.powers[0]) {
                C = p.coeffs[0];
                C1 = q.coeffs[0];
                v = p.powers[0] - q.powers[0];
                p = p.remZx(q, ring);
                if (p.isZero(ring)) {
                    break;
                }
                p = p.polDivNumb(((d.negate(ring)).pow(v, ring)).multiply(
                        C.negate(ring), ring), ring);
                d = braunDiZx(d, v, C1, ring);
            } else {
                C = q.coeffs[0];
                C1 = p.coeffs[0];
                v = q.powers[0] - p.powers[0];
                q = q.remZx(p, ring);
                if (q.isZero(ring)) {
                    break;
                }
                q = q.polDivNumb(((d.negate(ring)).pow(v, ring)).multiply(
                        C.negate(ring), ring), ring);
                d = braunDiZx(d, v, C1, ring);
            }
        }

        if (!(ring.algebra[0] == Ring.Z)) {
            if (p.isZero(ring)) {
                if (q.isZero(ring)) {
                    return Polynom.polynom_one(one);
                } else {
                    return q.polDivNumb(q.coeffs[0], ring);
                }
            }
            if (q.isZero(ring)) {
                return p.polDivNumb(p.coeffs[0], ring);
            }
        }

        // Вывод ответа в случае, когда он лежит в том же кольце Z[x]
        if (p.isZero(ring)) {
            q2 = q.GCDNumPolCoeffs(ring);
            if (!q2.isOne(ring)) {
                q = q.polDivNumb(q2, ring);
            }
            if (!gcdBI.isOne(ring)) {
                q = q.multiplyByNumber(gcdBI, ring);
            }
            return (q.coeffs[0].isNegative()) ? (Polynom) q.negate(ring) : q;
        }

        if (q.isZero(ring)) {
            p2 = p.GCDNumPolCoeffs(ring);
            if (!p2.isOne(ring)) {
                p = p.polDivNumb(p2, ring);
            }
            if (!gcdBI.isOne(ring)) {
                p = p.multiplyByNumber(gcdBI, ring);
            }
            return (p.coeffs[0].isNegative()) ? (Polynom) p.negate(ring) : p;
        }
        // Вывод ответа в случае, когда он не лежит в кольце Zp
        if (!gcdBI.isOne(ring)) {
            return polynomFromNumber(gcdBI, ring);
        }
        // Вывод ответа в случае, когда НОД(f, g) = 1
        return Polynom.polynom_one(one);
    }

    /**
     * Возведение числа "a" в степень с при помощи массива.
     *
     * @param VarNum количество переменных
     * @param c
     * @param power матрица степеней(для каждой переменной свой массив)
     *
     * @return
     */
    private static Polynom degry(int VarNum, int c, Polynom[][] power, Ring ring) {
        Polynom p = power[VarNum][c];
        if (p == null) {
            int d = c, j = 1;
            p = Polynom.polynom_one(NumberZ.ONE);
            while (d > 0) {
                if ((d & 1) == 1) {
                    p = p.mulSS(power[VarNum][j], ring);
                    // выбираем из массива нужную степень и накапливаем в
                    // произведеение p
                    d--;
                } else {
                    d >>= 1;
                    j <<= 1; // счетчик
                }
            }
            power[VarNum][c] = p;
        }
        return p;
    }

    /**
     * создается массив степеней значений неизвестных. Он содержит var строк
     * (число переменных). MaxPowerDif[i]+1 - число элементов в i-строке. в нем
     * заполняются элементы 1, 2, 4, 8, .....
     *
     * @param ValuesOfVars
     * @param MaxPowerDif
     *
     * @return матрица степеней
     */
    public static Polynom[][] InitPowers(Polynom[] ValuesOfVars,
            int[] MaxPowerDif, Ring ring) {
        int n = MaxPowerDif.length;
        Polynom m[][] = new Polynom[n][];
        for (int i = 0; i < n; i++) {
            m[i] = new Polynom[MaxPowerDif[i] + 1];
        }
        for (int i = 0; i < n; i++) {
            if (MaxPowerDif[i] != 0) {
                int j = 2;
                Polynom a = ValuesOfVars[i];
                m[i][1] = a;
                while (j <= MaxPowerDif[i]) {
                    a = a.mulSS(a, ring);
                    m[i][j] = a;
                    j <<= 1;
                }
            }
        }
        return m;
    }

    public static Polynom gornerX(Polynom pol, Polynom[] ValuesOfVars, Ring ring) {
        Polynom GX = new Polynom(new int[0], new Element[] {}); // pol.myZero(ring);
        Polynom PowArrays[][] = InitPowers(ValuesOfVars, pol.maxPowerDif(),
                ring);
        int len = pol.coeffs.length - 1;
        for (int i = 0; i < len; i++) {
            GX = (GX.add(Polynom.polynomFromNumber(pol.coeffs[i], ring), ring)).
                    mulSS(degry(0, pol.powers[i] - pol.powers[i + 1],
                                    PowArrays, ring), ring);
        }
        GX = (GX.add(Polynom.polynomFromNumber(pol.coeffs[len], ring), ring)).
                mulSS(degry(0, pol.powers[len], PowArrays, ring), ring);
        return GX;
    }

    /**
     * Для полиномов одной переменной разложение полинома над Z в произведение
     * свободных от квадратов множителей.
     *
     * Результатом является вектор, состоящий из полиномов сомножителей, и
     * вектора целых чисел - их степеней
     *
     * @return полином в формате FactorPolZ
     */
    public FactorPol FactorPol_SquareFreeOneVar(Ring ring) {
        int pos = 0;
        if (powers[powers.length - 1] > 0) {
            pos = powers[powers.length - 1];//младшая степень
        }
        int m = 0; //m=1  - числовой множитель отличный от 1
        //m=2  - имеется общий множитель  x^pl
        //m=4  - имеется и числовой множитель и множитель  x^pl
        int coeffsLength = coeffs.length;
        if (coeffsLength == 0) {
            return FactorPol.ZERO;
        }
        Element a = GCDNumPolCoeffs(ring);
        Element one = a.one(ring);
        int pl; // степень при младшем члене
        if (coeffsLength == 1) {
            if (powers.length != 0) {
                pl = powers[0];
                Polynom x = new Polynom(new int[] {1}, new Element[] {one});
                if (!a.equals(one)) {
                    return new FactorPol(a, x, pl, ring);
                } else {
                    return new FactorPol(x, pl);
                }
            } else {
                return new FactorPol(a, ring);
            }
        }
        if (!a.equals(one)) {
            m = 1;
            for (int i = 0; i < coeffsLength; i++) {
                coeffs[i] = coeffs[i].divide(a, ring);
            }
        }

        //pl - показатель степени сомножителя x^pl
        pl = powers[coeffsLength - 1];
        if (pl != 0) {
            for (int i = 0; i < coeffsLength; i++) {
                powers[i] -= pl;
            }
            if (m == 1) {
                m = 2;
            } else {
                m = 4;
            }
        }
        Polynom[] F = new Polynom[powers[0]];
        int k = 0;
        F[0] = this;
        int n = 0;

        while (F[k].powers.length != 0) {
            Polynom pol_s = F[k].D(0, ring);
            if (pol_s.powers.length == 0 || pol_s.powers[0] == 0) {
                k++;
                break;
            }
            F[k + 1] = F[k].gcdZx(pol_s, ring);
            k++;
        }

        Polynom[] M = new Polynom[k];
        M[0] = F[k - 1];
        Polynom Q = M[0];
        int s = 0;
        for (int i = k - 2; i > -1; i--) {
            Q = Q.mulSS(M[k - 1 - i - 1], ring);
            if (s != 0) {
                for (int j = 0; j < k - 1 - i; j++) {
                    Q = Q.mulSS(M[j], ring);
                }
            }
            s++;
            M[k - 1 - i] = F[i].divideExact(Q, ring);
            if (M[k - 1 - i].powers.length != 0) {
                n += 1;
            }
        }
        n += 1 + (m % 3);

        Polynom[] multinF = new Polynom[n];

        int[] powersF = new int[n];
        int ii = 0;
        Boolean flag = true;
        if ((m == 1) || (m == 2)) {
            multinF[0] = Polynom.polynomFromNumber(a, ring);
            powersF[0] = 1;
            ii++;
        }

        if (pos > 0) {
            multinF[multinF.length - 1] = new Polynom(new int[] {1}, new Element[] {one});
            powersF[multinF.length - 1] = pl;
        }

        for (int i = 0; i < M.length; i++) {
            if (M[i].powers.length != 0) {
                multinF[ii] = M[i];
                powersF[ii] = k - i;
                ii++;
            }
        }
        return new FactorPol(powersF, multinF);
    }

    /**
     * Re реальная часть полинома. Она образована реальными частями
     * коэффициентов при мономах
     *
     * @param ring Ring
     *
     * @return re of this polynomial
     */
    @Override
    public Polynom Re(Ring ring) {
         if (coeffs.length == 0) return this;
        Polynom p = cloneWithoutCoeffs();
        for (int i=0; i < coeffs.length; i++) 
               p.coeffs[i] = coeffs[i].Re(ring);
        return p.deleteZeroCoeff( ring) ;
    }

    /**
     * Мнимая часть полинома. Она образована мнимыми частями коэффициентов при
     * мономах.
     *
     * @param ring Ring
     *
     * @return im of this polynomial
     */
    @Override
    public Polynom Im(Ring ring) {
        if (coeffs.length == 0) return this;
        Polynom p = cloneWithoutCoeffs();
        for (int i=0; i < coeffs.length; i++) 
               p.coeffs[i] = coeffs[i].Im(ring);
        return p.deleteZeroCoeff( ring) ;
    }

    /** Complex conjugate polynomial
     * Комплексно сопряженный полином.
     *
     * @param ring Ring
     *
     * @return conjugated polynomial
     */
    @Override
    public Polynom conjugate(Ring ring) {
        if (coeffs.length == 0) return this;
        Polynom p = cloneWithoutCoeffs();
        for (int i=0; i < coeffs.length; i++) 
               p.coeffs[i] = coeffs[i].conjugate(ring);
        return p.deleteZeroCoeff( ring) ;
    }

    
    /**
     * Для полинома с одной неизвестной z делаем замену z=(x+yt). Раскрываем
     * скобки и собирам множитель при t=\i. В ответе получается 2 полинома от x
     * и y -- Re и Im.
     *
     * @param ring
     *
     * @return
     */
    public Polynom[] Re_Im_Of(Ring ring) {
        Element one = this.one(ring);
        Polynom pz = new Polynom(new int[] {0, 1, 1, 1, 0, 0}, new Element[] {
            one, one});
        Polynom p = Polynom.gornerX(this, new Polynom[] {pz}, ring);
        int im_number = 0;
        int re_number = 0;
        for (int i = 2; i < p.powers.length; i += 3) {
            if (p.powers[i] % 4 == 1 || p.powers[i] % 4 == 3) {
                im_number++;
            }
            if (p.powers[i] % 4 == 0 || p.powers[i] % 4 == 2) {
                re_number++;
            }
        }
        Element[] re_coeffs = new Element[re_number];
        int[] re_powers = new int[2 * re_number];
        Element[] im_coeffs = new Element[im_number];
        int[] im_powers = new int[2 * im_number];
        int index_re = 0;
        int index_im = 0;
        int index = 2;
        for (int i = 0; i < p.coeffs.length; i++) {
            if (p.powers[index] % 4 == 1) {
                im_coeffs[index_im] = p.coeffs[i];
                im_powers[2 * index_im] = p.powers[index - 2];
                im_powers[2 * index_im + 1] = p.powers[index - 1];
                index_im++;
            }
            if (p.powers[index] % 4 == 3) {
                im_coeffs[index_im] = p.coeffs[i].negate(ring);
                im_powers[2 * index_im] = p.powers[index - 2];
                im_powers[2 * index_im + 1] = p.powers[index - 1];
                index_im++;
            }

            if (p.powers[index] % 4 == 0) {
                re_coeffs[index_re] = p.coeffs[i];
                re_powers[2 * index_re] = p.powers[index - 2];
                re_powers[2 * index_re + 1] = p.powers[index - 1];
                index_re++;
            }

            if (p.powers[index] % 4 == 2) {
                re_coeffs[index_re] = p.coeffs[i].negate(ring);
                re_powers[2 * index_re] = p.powers[index - 2];
                re_powers[2 * index_re + 1] = p.powers[index - 1];
                index_re++;
            }
            index += 3;
        }

        Polynom Re_P = new Polynom(re_powers, re_coeffs);
        Polynom Im_P = new Polynom(im_powers, im_coeffs);

        Re_P = Re_P.ordering(0, 0, Re_P.coeffs.length, ring);
        Im_P = Im_P.ordering(0, 0, Im_P.coeffs.length, ring);
        return new Polynom[] {Re_P, Im_P};
    }

    /**
     * Workaround for solving polynomials with complex coefficients. Use instead
     * of Re_Im_Of().
     *
     * @param ring ring.
     *
     * @return array of polynomials with two elements: first is real part of
     * original polynomials; second is imaginary part.
     */
    public Polynom[] reImForComplexCoeffs(Ring ring) {
        // Get Re, Im of initial polynomial.
        Polynom[] reIm_ = Re_Im_Of(ring);
        Polynom re = reIm_[0];
        Polynom im = reIm_[1];

        // Split again for complex coefficients.
        Polynom reRe = re;
        Polynom reIm = re.clone();
        // Modify coefficients: reRe contains only real parts of re's coefficient;
        // reIm -- only imaginary parts.
        for (int i = 0; i < reRe.coeffs.length; i++) {
            if (re.coeffs[i] instanceof Complex) {
                reRe.coeffs[i] = ((Complex) reRe.coeffs[i]).re;
                reIm.coeffs[i] = ((Complex) reIm.coeffs[i]).im;
            } else {
                reIm.coeffs[i] = ring.numberZERO();
            }
        }

        Polynom imRe = im;
        Polynom imIm = im.clone();
        for (int i = 0; i < imRe.coeffs.length; i++) {
            if (im.coeffs[i] instanceof Complex) {
                imRe.coeffs[i] = ((Complex) imRe.coeffs[i]).re;
                imIm.coeffs[i] = ((Complex) imIm.coeffs[i]).im;
            } else {
                imIm.coeffs[i] = ring.numberZERO();
            }
        }

        Polynom[] res = new Polynom[] {
            // re + I * im.
            reRe.subtract(imIm, ring).deleteZeroCoeff(ring),
            reIm.add(imRe, ring).deleteZeroCoeff(ring)
        };
        return res;
    }

    public Element maxCoeff(Ring ring) {
        Element coef = this.coeffs[0];
        for (int i = 0; i < this.coeffs.length; i++) {
            if (coef.abs(ring).compareTo(this.coeffs[i].abs(ring), ring) == -1) {
                coef = this.coeffs[i];
            }
        }
        return coef.abs(ring);
    }

    /**
     * Возведение числа a в степень с при помощи массива.
     *
     * @param VarNum количество переменных
     * @param c
     * @param power матрица степеней (для каждой переменной массив)
     *
     * @return
     */
    private static double degry(int VarNum, int c, double power[][]) {
        double p = power[VarNum][c];
        if (p == 0) {
            int d = c, j = 1;
            p = 1;
            while (d > 0) {
                if ((d & 1) == 1) {
                    p *= power[VarNum][j];
                } // выбираем из массива нужную степень и накапливаем в
                // произведеение p
                d >>= 1;
                j <<= 1; // счетчик
            }
            power[VarNum][c] = p;
        }
        return p;
    }

    /**
     * создается массив степеней значений неизвестных. Он содержит var строк
     * (число переменных). MaxPowerDif[i]+1 - число элементов в i-строке. в нем
     * заполняются элементы 1, 2, 4, 8, .....
     *
     * @param ValuesOfVars
     * @param MaxPowerDif
     *
     * @return
     */
    private static double[][] initPowers(double[] ValuesOfVars,
            int[] MaxPowerDif) {
        int n = MaxPowerDif.length;
        double m[][] = new double[n][];
        for (int i = 0; i < n; i++) {
            m[i] = new double[MaxPowerDif[i] + 1];
        }
        for (int i = 0; i < n; i++) {
            if (MaxPowerDif[i] != 0) {
                int j = 2;
                double a = ValuesOfVars[i];
                m[i][1] = a;
                while (j <= MaxPowerDif[i]) {
                    a *= a;
                    m[i][j] = a;
                    j <<= 1;
                }
            }
        }
        return m;
    }

    /**
     * Горнер для var переменных
     *
     * @param ValuesOfVars аргументы
     *
     * @return значение
     */
    public double valueOf(double[] ValuesOfVars, Ring ring) {
        int l = coeffs.length;
        int n = powers.length / l;
        double PowArrays[][] = initPowers(ValuesOfVars, maxPowerDif());
        int i_;
        int k;
        double c;
        double s[] = new double[n]; // частичные cсуммы по каждой переменной
        for (int i = 0; i < n; i++) {
            s[i] = 0;
        }
        // Основной цикл
        for (int i = 1; i < l; i++) {
            i_ = i - 1;
            k = n - 1;
            while (powers[i * n + k] == powers[i_ * n + k]) {
                k--;
            }
            if (k == 0) {
                s[0] = (s[0] + coeffs[i_].doubleValue())
                        * degry(0, powers[i_ * n] - powers[i * n], PowArrays);
            } else {
                c = coeffs[i_].doubleValue();
                for (int j = 0; j < k; j++) {
                    c = (s[j] + c) * degry(j, powers[i_ * n + j], PowArrays);
                    s[j] = 0;
                }
                s[k] = (s[k] + c)
                        * degry(k, powers[i_ * n + k] - powers[i * n + k],
                                PowArrays);
            }
        }
        // Завершение
        k = n - 1;
        while (powers[(l - 1) * n + k] == 0 && k > 0) {
            k--;
        }
        if (k == 0) {
            s[0] = (coeffs[l - 1].doubleValue() + s[0])
                    * degry(0, powers[(l - 1) * n], PowArrays);
        } else {
            c = coeffs[l - 1].doubleValue();
            for (int j = 0; j < k; j++) {
                c = (s[j] + c) * degry(j, powers[(l - 1) * n + j], PowArrays);
                s[j] = 0;
            }
            s[k] = (s[k] + c) * degry(k, powers[(l - 1) * n + k], PowArrays);

        }
        for (int i = 0; i < n - 1; i++) {
            s[n - 1] += s[i];
        }
        return s[n - 1];
    }

    /**
     * Нахождение var комплексных корней полинома степени var. /* (у входного
     * полинома не должно быть кратных корней!!) /* Поиск на комплексной
     * плоскости путем спуска по Ньютону из случайных точек /
     *
     * @param PolynomZ p /
     * @param double eps - точность (по значению полинома в корне) /
     *
     * @return Complex[] rootsOf массив корней
     */
    public Element[] rootOfE(Ring ring) {

        Element eps;
        if ((ring.algebra[0] >= Ring.R64) && (ring.algebra[0] <= Ring.R64 + 6)) {
            eps = ring.MachineEpsilonR64;
        } else {
            eps = ring.MachineEpsilonR;
        }

        // double epsDoub = eps.doubleValue();
        long Time = System.currentTimeMillis();
        if (powers.length == 0) {
            return (new Complex[] {null});
        }
        if ((powers[0] == 1) && (coeffs.length == 2)) {
            Element x = (coeffs[1].divide(coeffs[0], ring)).negate(ring);
            return new Complex[] {new Complex(x, ring.numberZERO)};
        }
        if ((powers[0] == 1) && (coeffs.length == 1)) {
            return new Complex[] {new Complex(ring.numberZERO, ring.numberZERO)};
        } else {

            Element R = this.maxCoeff(ring);
            R = R.add(R, ring); //2*maxCoeff

            Polynom P = this;

            Polynom dP = P.D(ring);
            //Polynom[] P_re_im = P.Re_Im_Of(ring);
            //Polynom[] dP_re_im = dP.Re_Im_Of(ring);
            Polynom[] P_re_im = reImForComplexCoeffs(ring);
            Polynom[] dP_re_im = dP.reImForComplexCoeffs(ring);
            Polynom Re = P_re_im[0].toPolynom(Ring.R, ring);
            Polynom Im = P_re_im[1].toPolynom(Ring.R, ring);
            Polynom dRe = dP_re_im[0].toPolynom(Ring.R, ring);
            Polynom dIm = dP_re_im[1].toPolynom(Ring.R, ring);

            Element root[] = new Complex[P.powers[0]];
            root[0] = new Complex(ring.numberONE, ring.numberONE);
            int position = 1;  // --------------------------------------------------
            Element point[] = new Element[2];
            Element _Xpoint[] = new Element[2];
            Element Xpoint_[] = new Element[2];
            Element _Ypoint[] = new Element[2];
            Element Ypoint_[] = new Element[2];
            Element x_fz = ring.numberONE;
            Element y_fz = ring.numberONE;
            Element x_dfz;
            Element y_dfz;
            Element z_ch;
            Element z_zn;
            Element z_frac;
            int check = 0;
            int step = 0;
            int time = 0;
            Element temp_x = ring.numberZERO;
            Element temp_y = ring.numberZERO;
            int num;

            int p = 0;
            Random rnd;
            Element x;
            Element y;
            Element z0;
            while (position < P.powers[0]) {

                rnd = new Random();
                x = new NumberR(2 * rnd.nextDouble()).multiply(R, ring).subtract(R, ring);
                y = new NumberR(rnd.nextDouble()).multiply(R, ring);
                x = x.toNewRing(ring.algebra[0], ring);
                y = y.toNewRing(ring.algebra[0], ring);
                z0 = new Complex(x, y);
                while ((((x_fz.abs(ring)).add((y_fz.abs(ring)), ring)).compareTo(eps, 2, ring)) && (time == 0)) {
                    point[0] = (z0 instanceof Complex) ? ((Complex) z0).re : z0;
                    point[1] = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;

                    x_fz = Re.value(point, ring);
                    y_fz = Im.value(point, ring);

                    if (p == 1) {
                        if (((x_fz.abs(ring)).compareTo((temp_x.abs(ring)), 1, ring))
                                || ((y_fz.abs(ring)).compareTo((temp_y.abs(ring)), 1, ring))) {
                            break;
                        }
                    }
                    p = 1;

                    if (((point[0].abs(ring)).compareTo(R, 2, ring))
                            || ((point[1].abs(ring)).compareTo(R, 2, ring))) {
                        break;
                    }

                    temp_x = x_fz;
                    temp_y = y_fz;

                    z_ch = new Complex(x_fz, y_fz);

                    x_dfz = dRe.value(point, ring);
                    y_dfz = dIm.value(point, ring);

                    z_zn = new Complex(x_dfz, y_dfz);

                    z_frac = z_ch.divide(z_zn, ring);
                    z0 = z0.subtract(z_frac, ring);

                }
                p = 0;
                time = 0;
                num = 10;
                NumberR hh = new NumberR(100);
                Element hhE = hh.toNewRing(ring.algebra[0], ring);
                Element E = eps.multiply(hhE, ring);

                _Xpoint[0] = (z0 instanceof Complex) ? ((Complex) z0).re.add(E.negate(ring), ring) : z0.add(E.negate(ring), ring);
                Xpoint_[0] = (z0 instanceof Complex) ? ((Complex) z0).re.add(E, ring) : z0.add(E, ring);
                _Xpoint[1] = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;
                Xpoint_[1] = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;

                _Ypoint[0] = (z0 instanceof Complex) ? ((Complex) z0).re : z0;
                Ypoint_[0] = (z0 instanceof Complex) ? ((Complex) z0).re : z0;
                _Ypoint[1] = (z0 instanceof Complex) ? ((Complex) z0).im.add(E.negate(ring), ring) : ring.numberZERO.add(E.negate(ring), ring);
                Ypoint_[1] = (z0 instanceof Complex) ? ((Complex) z0).im.add(E, ring) : ring.numberZERO.add(E, ring);

                while ((((((Re.value(_Xpoint, ring)).multiply(Re.value(Xpoint_, ring), ring)).compareTo(ring.numberZERO, ring) == 1)
                        && (((Im.value(_Xpoint, ring)).multiply(Im.value(Xpoint_, ring), ring)).compareTo(ring.numberZERO, ring) == 1)
                        || (((Re.value(_Ypoint, ring))).multiply(Re.value(Ypoint_, ring), ring)).compareTo(ring.numberZERO, ring) == 1)
                        && ((Im.value(_Ypoint, ring)).multiply(Im.value(Ypoint_, ring), ring)).compareTo(ring.numberZERO, ring) == 1)
                        && num > 0) {
                    num--;
                    NumberR four = new NumberR(4);
                    Element fourE = four.toNewRing(ring.algebra[0], ring);
                    if (num == 2) {
                        E = E.divide(fourE, ring);
                    } else {
                        E = E.multiply(fourE, ring);
                    }

                    _Xpoint[0] = (z0 instanceof Complex) ? ((Complex) z0).re.add(E.negate(ring), ring) : z0.add(E.negate(ring), ring);
                    Xpoint_[0] = (z0 instanceof Complex) ? ((Complex) z0).re.add(E, ring) : z0.add(E, ring);
                    _Xpoint[1] = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;
                    Xpoint_[1] = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;

                    _Ypoint[0] = (z0 instanceof Complex) ? ((Complex) z0).re : z0;
                    Ypoint_[0] = (z0 instanceof Complex) ? ((Complex) z0).re : z0;
                    _Ypoint[1] = (z0 instanceof Complex) ? ((Complex) z0).im.add(E.negate(ring), ring) : ring.numberZERO.add(E.negate(ring), ring);
                    Ypoint_[1] = (z0 instanceof Complex) ? ((Complex) z0).im.add(E, ring) : ring.numberZERO.add(E, ring);

                }
                if (num > 0) {

                    if (check == 0) {
                        root[0] = (z0 instanceof Complex) ? (Complex) z0 : new Complex(z0, ring);
                        NumberR th = new NumberR(1000);
                        Element thE = th.toNewRing(ring.algebra[0], ring);
                        Element z0im = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;
                        if ((z0im.abs(ring)).compareTo((eps.multiply(thE, ring)), GREATER, ring)) {
                            root[1] = (z0 instanceof Complex) 
                                      ? ((Complex) z0).conjugate(ring)
                                      : new Complex(z0, ring);
                            position++;
                        }
                        check = 1;
                    } else {
                        int i = 0;
                        Element z0re = (z0 instanceof Complex) ? ((Complex) z0).re : z0;
                        Element z0im = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;
                        while ((i < position)
                                && ((((root[i].Re(ring).add(z0re.negate(ring), ring)).abs(ring)).compareTo(eps, 2, ring)) || (((root[i].Im(ring).add(z0im.negate(ring), ring)).abs(ring)).compareTo(eps, 2, ring)))) {
                            i++;
                        }
                        if (i == position) {
                            root[position] = (z0 instanceof Complex) ? (Complex) z0 : new Complex(z0, ring);

                            step = 1;
                            Element z0im_ = (z0 instanceof Complex) ? ((Complex) z0).im : ring.numberZERO;
                            if ((z0im_.abs(ring)).compareTo(eps, 2, ring)) {
                                root[position + 1] = root[position].conjugate(ring);
                                step = 2;
                            }
                        }
                    }
                }

                position += step;
                step = 0;
                x_fz = ring.numberONE;
                y_fz = ring.numberONE;
                long Time2 = System.currentTimeMillis() - Time;

                //System.out.println("root#= "+position+"   time="+Time2);
            }
            return root;
        }
    }

    public Element[] returnSearchPolRoots(Ring r) {
        FactorPol fp = FactorPol_SquareFree(r);
        ArrayList<Element> res = new ArrayList<Element>();
        for (int i = 0; i < fp.multin.length; i++) {
            res.addAll(Arrays.asList(fp.multin[i].rootOfEC(r)));
        }
        return res.toArray(new Element[res.size()]);
    }

    /**
     * Нахождение var комплексных корней полинома степени var. /* (у входного
     * полинома не должно быть кратных корней!!) /* Поиск на комплексной
     * плоскости путем спуска по Ньютону из случайных точек /
     *
     * @param PolynomZ p /
     * @param double eps - точность (по значению полинома в корне) /
     *
     * @return Complex[] rootsOf массив корней
     */
    public Complex[] rootOfEC(Ring ring) {
        Element eps = ring.MachineEpsilonR.toNewRing(ring.algebra[0], ring);
        if (powers.length == 0) {
            return (new Complex[] {null});
        }
        if ((powers[0] == 1) && (coeffs.length == 2)) {
            Element x = (coeffs[1].divide(coeffs[0], ring)).negate(ring);
            return new Complex[] {(x instanceof Complex) ? (Complex) x : new Complex(x, ring.numberZERO)};
        }
        if ((powers[0] == 1) && (coeffs.length == 1)) {
            return new Complex[] {new Complex(ring.numberZERO, ring.numberZERO)};
        } else {
            Element R = this.maxCoeff(ring);
            R = R.add(R, ring); //2*maxCoeff
            Polynom dP = D(ring);
            Polynom[] P_re_im = reImForComplexCoeffs(ring);
            Polynom[] dP_re_im = dP.reImForComplexCoeffs(ring);
            Polynom Re = P_re_im[0];
            Polynom Im = P_re_im[1];
            Polynom dRe = dP_re_im[0];
            Polynom dIm = dP_re_im[1];

            Complex root[] = new Complex[powers[0]];
            root[0] = new Complex(ring.numberONE, ring.numberONE);
            int position = 1;
            Element point[] = new Element[2];
            Element _Xpoint[] = new Element[2];
            Element Xpoint_[] = new Element[2];
            Element _Ypoint[] = new Element[2];
            Element Ypoint_[] = new Element[2];

            Element x_fz = ring.numberONE;
            Element y_fz = ring.numberONE;
            Element x_dfz;
            Element y_dfz;
            Complex z_ch;
            Complex z_zn;
            Element z_frac;
            int check = 0;
            int step = 0;
            int time = 0;
            Element temp_x = ring.numberZERO;
            Element temp_y = ring.numberZERO;
            int num;

            int p = 0;
            Random rnd;
            Element x;
            Element y;
            Element z0;
            while (position < powers[0]) {
                rnd = new Random();
                x = new NumberR(2 * rnd.nextDouble()).multiply(R, ring).
                        subtract(R, ring);
                y = new NumberR(2 * rnd.nextDouble()).multiply(R, ring).
                        subtract(R, ring);
                x = x.toNewRing(ring.algebra[0], ring);
                y = y.toNewRing(ring.algebra[0], ring);

                z0 = new Complex(x, y);
                while ((((x_fz.abs(ring)).add((y_fz.abs(ring)), ring)).
                        compareTo(eps, 2, ring)) && (time == 0)) {
                    point[0] = z0.Re(ring);
                    point[1] = z0.Im(ring);

                    x_fz = Re.value(point, ring);
                    y_fz = Im.value(point, ring);

                    if (p == 1) {
                        if (x_fz.abs(ring).compareTo(temp_x.abs(ring), 1, ring)
                                || y_fz.abs(ring).compareTo(temp_y.abs(ring), 1, ring)) {
                            break;
                        }
                    }
                    p = 1;
                    if (((point[0].abs(ring)).compareTo(R, 2, ring))
                            || ((point[1].abs(ring)).compareTo(R, 2, ring))) {
                        break;
                    }
                    temp_x = x_fz;
                    temp_y = y_fz;

                    z_ch = new Complex(x_fz, y_fz);

                    x_dfz = dRe.value(point, ring);
                    y_dfz = dIm.value(point, ring);

                    z_zn = new Complex(x_dfz, y_dfz);

                    z_frac = z_ch.divide(z_zn, ring);
                    z0 = z0.subtract(z_frac, ring);
                }
                p = 0;
                time = 0;
                num = 10;
                NumberR hh = new NumberR(100);
                Element hhE = hh.toNewRing(ring.algebra[0], ring);
                Element E = eps.multiply(hhE, ring);

                _Xpoint[0] = z0.Re(ring).add(E.negate(ring), ring);
                Xpoint_[0] = z0.Re(ring).add(E, ring);
                _Xpoint[1] = z0.Im(ring);
                Xpoint_[1] = z0.Im(ring);

                _Ypoint[0] = z0.Re(ring);
                Ypoint_[0] = z0.Re(ring);
                _Ypoint[1] = z0.Im(ring).add(E.negate(ring), ring);
                Ypoint_[1] = z0.Im(ring).add(E, ring);
                NumberR four = new NumberR(4);
                Element fourE = four.toNewRing(ring.algebra[0], ring);
                while ((((((Re.value(_Xpoint, ring)).multiply(Re.
                        value(Xpoint_, ring), ring)).
                        compareTo(ring.numberZERO, ring) == 1)
                        && (((Im.value(_Xpoint, ring)).multiply(Im.
                                value(Xpoint_, ring), ring)).
                        compareTo(ring.numberZERO, ring) == 1)
                        || (((Re.value(_Ypoint, ring))).multiply(Re.
                                value(Ypoint_, ring), ring)).
                        compareTo(ring.numberZERO, ring) == 1)
                        && ((Im.value(_Ypoint, ring)).multiply(Im.
                                value(Ypoint_, ring), ring)).
                        compareTo(ring.numberZERO, ring) == 1)
                        && num > 0) {
                    num--;
                    if (num == 2) E = E.divide(fourE, ring);
                    else  E = E.multiply(fourE, ring);


                    _Xpoint[0] = z0.Re(ring).add(E.negate(ring), ring);
                    Xpoint_[0] = z0.Re(ring).add(E, ring);
                    _Xpoint[1] = z0.Im(ring);
                    Xpoint_[1] = z0.Im(ring);

                    _Ypoint[0] = z0.Re(ring);
                    Ypoint_[0] = z0.Re(ring);
                    _Ypoint[1] = z0.Im(ring).add(E.negate(ring), ring);
                    Ypoint_[1] = z0.Im(ring).add(E, ring);

                }
                if (num > 0) { // i.e. all right!
                    if (check == 0) { // входим сюда первый раз
                        root[0] = (z0 instanceof Complex) ? (Complex) z0
                                : new Complex(z0, ring);
                        check = 1;
                    } else {
                        int i = 0;
                        if (z0 instanceof Complex) {
                            while ((i < position)
                                    && ((((root[i].re.add(z0.Re(ring).negate(ring), ring)).
                                    abs(ring)).compareTo(eps, 2, ring)) || (((root[i].im.
                                    add(z0.Im(ring).negate(ring), ring)).abs(ring)).
                                    compareTo(eps, 2, ring))))  i++;
                            }
                        else {
                            while ((i < position)
                                    && ((((root[i].re.add(z0.negate(ring), ring)).
                                    abs(ring)).compareTo(eps, 2, ring)) || ((root[i].im.abs(ring)).
                                    compareTo(eps, 2, ring)))) {
                                i++;
                            }
                        }
                        if (i == position) {
                            root[position] = (z0 instanceof Complex) ? (Complex) z0 : new Complex(z0, ring);
                            step = 1;
                        }
                    }
                }
                position += step;
                step = 0;
                x_fz = ring.numberONE;
                y_fz = ring.numberONE;
            }
            return root;
        }
    }

    /**
     * Swaps variables at given positions.
     *
     * @param varInd1 index of first var.
     * @param varInd2 index of second var.
     */
    public Polynom swapVars(int varInd1, int varInd2) {
        if (varInd1 == varInd2) {
            return this;
        }
        int varsNum = getVarsNum();
        int monomsCount = coeffs.length;
        for (int i = 0; i < monomsCount; i++) {
            int base = i * varsNum;
            int tmp = powers[base + varInd1];
            powers[base + varInd1] = powers[base + varInd2];
            powers[base + varInd2] = tmp;
        } 
        return truncate();
    }

    @Override
    public Element factor(Ring ring) {
        return (isItNumber()) ? new FactorPol(this, ring) : factorOfPol_inQ(false, ring);
    }
    @Override
    public Element Factor(boolean doNewVector, Ring ring) {  //return factorWithI(ring)[0];        
        if(isItNumber())return new FactorPol(this, ring);
        FactorPol p= factorOfPol_inQ(false, ring);
        //*******************************// Ищем еще и комплексные корни... ***********
        if (ring.isComplex()){ 
           int pN=p.powers.length; int newMultNumb=0; // число добавочных сомножителей
           FactorPol[] newFact=new FactorPol[pN]; // Если каждый сомножитель разложится.. //SolveEq SEclass=new SolveEq(ring);
           for (int i = 0; i < pN; i++) { Polynom pol=p.multin[i];
                int tot=pol.degreeOfFirstMonomial();
                if((pol.powers.length==pol.coeffs.length)&&(tot>1)&&(tot<5)){
                   //    Element   tempRes = SEclass.solvePolynomEq(pol,0, ring); // по Кордано .. но не будем..
                   newFact[i]= pol.factorOfPol_inC(ring); // корни ищем вероятностным методом    
                newMultNumb+=newFact[i].powers.length-1;}
                else  newFact[i]=null;              
            }
            if(newMultNumb!=0){ Polynom[] newMult= new Polynom[newMultNumb+pN];
              int[] newpow= new int[newMultNumb+pN];
              int j=0;
              for (int i = 0; i < pN; i++) {
                if(newFact[i]==null) {newMult[j]=p.multin[i];newpow[j++]=p.powers[i]; }
                else {System.arraycopy(newFact[i].multin, 0, newMult, j, newFact[i].powers.length);
                      for (int k = 0; k < newFact[i].powers.length; k++) 
                           newpow[j++]=p.powers[i]*newFact[i].powers[k];                  
                    //  j+=newFact[i].powers.length;
                }
              }  return new FactorPol(newpow, newMult);
            } 
        } return p;
    }
    /**
     * Разложение на рациональные сомножители  
     * полиномов от 8 числовых типов (Z,Q,R,R64, CZ,CQ,CR,C64)
     *
     * @param squareFree -- true - разложение только на свободные от квадратов,
     * false - полное разложение на рациональные сомножители (долго)
     *
     * @return -- факторполином.
     */
 public FactorPol factorOfPol_inQ(boolean squareFree, Ring ring) {
        if(degreeTotal()<2){Element gcdC=this.GCDNumPolCoeffs(ring);
            return (gcdC.isOne(ring))? new FactorPol(this):
                    new FactorPol(new int[]{1,1}, new Polynom[]{Polynom.polynomFromNot0Number(gcdC),this.divideByNumber(gcdC, ring)});
        }
        Ring currentRing = ring;
        if (isZero(ring)) {return FactorPol.ZERO;}
        if(coeffs.length==1){  Polynom[] pp; int[] pow; int pos=0;
             int numb=0;
             for (int i = 0; i < powers.length; i++) if( powers[i]!=0)numb++;
             if( coeffs[0].isOne(ring)){pp=new Polynom[numb]; pow=new int[numb];}
             else{numb++; pp=new Polynom[numb]; pow=new int[numb]; pow[0]=1; 
                  pp[0]=new Polynom(coeffs[0]); pos++;}
             for (int i = 0; i < powers.length; i++) { 
                 if( powers[i]!=0){  // pp[pos]=ring.varPolynom[i];
                     int[] poww=new int[i+1];  poww[i]=1;   pp[pos]= new Polynom(poww, new Element[] {ring.numberONE});
                 pow[pos]=powers[i]; pos++;}}
             return new FactorPol(pow, pp);
         } 
        mm: if((coeffs.length==2)&&(powers[0]==1)) {
            int ll = powers.length/2;
        //    boolean b = false;
            for(int i=0; i<ll; i++) {
                if(powers[i] + powers[i+ll] > 1) {
                  //  b = true;
                    break mm;
                } 
            }
           // if(b == false) 
                return new FactorPol(this);
        }
        int rType = 0; // ring-type of the coeffs
        int ii=0; // index for the biggest type coeff.
        boolean isComplex= false;
        for (int i = 0; i < coeffs.length; i++) {
            int tmp=coeffs[i].numbElementType(); if(tmp>rType) {rType=tmp; ii=i;};
            isComplex=isComplex || coeffs[i].isComplex(ring); }
        Element coeffsBiggest=coeffs[ii];
        Polynom p = this;
        int deg=0;
        if (isComplex) {//  сначало разбираемся со всеми комплексными типами
          int type = (coeffsBiggest instanceof Fraction)? Ring.Q:
               ((Complex) coeffsBiggest).re.isZero(ring)?
                    ((Complex) coeffsBiggest).im.numbElementType():
                    ((Complex) coeffsBiggest).re.numbElementType(); // base of complex numbers      
            if (type < Ring.R) {// все целые типы C... CQ
                Element[] one = new Element[] {NumberZ.ONE};
            //    if (type != Ring.Z) // видимо в one вернется знаменатель дроби
                    p = ((type == Ring.Q)||(type == Ring.Z)) ? toPolynomCZfromCQ(one, ring) : toPolynom(Ring.CZ, ring);
                FactorPol fpp = (squareFree) ? p.FactorPol_SquareFree(ring)
                        : FactorizationManyVar.Factor(p, ring).deleteOne(ring);
                if (one[0].isOne(ring))  // знаменателя нет
                    return ((type == Ring.Q) || (type == Ring.Z)) ? fpp : fpp.toNewRing(currentRing.algebra[0], ring);
                else {
                    Polynom[] mul = new Polynom[fpp.multin.length + 1];
                    int[] pow = new int[fpp.powers.length + 1];
                    System.arraycopy(fpp.multin, 0, mul, 1, fpp.multin.length);
                    System.arraycopy(fpp.powers, 0, pow, 1, fpp.powers.length);
                    mul[0] = new Polynom(new Fraction(new Complex(NumberZ.ONE, NumberZ.ZERO), one[0]));
                    pow[0] = 1;
                    return new FactorPol(pow, mul);
                }// конец целых типов
            } else {
                if (type == Ring.R64) {  //  ..... C64  ТИП
                    NumberZ64 powOfmultiplyer = new NumberZ64(0L);
                    p = p.toPolynom_Zfrom_64(powOfmultiplyer, ring);
                double d=Math.rint(1.0/ring.MachineEpsilonR64.value); 
                } else { //  ..... осталось 2 типа и ... C128 приводим к C ..........
                    p = this;
                    if (type == Ring.R128)  p = this.toPolynom(Ring.C, ring);
                    NumberZ64 powOfmultiplyer = new NumberZ64(0L);
                    p = p.toPolynomCZfromC(powOfmultiplyer);
                    deg = -(int) powOfmultiplyer.value;
                }
                Ring ring2 = new Ring(ring, Ring.CZ);
                Element factCoeffs = p.GCDNumPolCoeffs(ring2); // общий множитель коэффициентов сохраним
                if (!factCoeffs.isOne(ring2)) p = p.divideByNumber(factCoeffs, ring2);
                FactorPol fp = (squareFree) ? p.FactorPol_SquareFree(ring2)
                        : FactorizationManyVar.Factor(p, ring2);
                if(fp.multin.length==1){return new FactorPol(this);}
                fp =fp.toNewRing(ring.algebra[0], ring); 
                Element mult;
            if((ring.algebra[0]& (~Ring.MASK_C))==Ring.R64) mult=factCoeffs.multiply(ring.MachineEpsilonR64, ring);
            else{  Element im=(NumberZ)factCoeffs.Im(ring);
                mult=   (im.isZero(ring))? 
                        new NumberR((NumberZ)factCoeffs.Re(ring), -deg, ring):
                        new Complex(new NumberR((NumberZ)(factCoeffs.Re(ring)), -deg,ring),
                                    new NumberR((NumberZ)im, -deg));}
                if (!mult.isOne(ring)) {
                    Polynom numbMultipl = Polynom.polynomFromNumber(mult, ring);
                    int ss = fp.multin.length + 1;
                    Polynom[] pols = new Polynom[ss];
                    int[] pows = new int[ss];
                    System.arraycopy(fp.multin, 0, pols, 1, ss - 1);
                    pols[0] = numbMultipl;
                    System.arraycopy(fp.powers, 0, pows, 1, ss - 1);
                    pows[0] = 1;
                    fp = new FactorPol(pows, pols);
                }
                return fp.deleteOne(ring);
            }
        }
        rType=rType&(~Ring.Complex);
        if (rType  < Ring.R) {  //   ВСЕ ЦЕЛЫЕ ТИПЫ
            Element[] one = new Element[] {NumberZ.ONE};
            if (rType != Ring.Z) {
                p = (rType == Ring.Q) ? toPolynomZfromQ(one, ring) : toPolynom(Ring.Z, ring);
            }
            ring = new Ring(ring, Ring.Z);
            FactorPol fpp = (squareFree) ? p.FactorPol_SquareFree(ring)
                    : FactorizationManyVar.Factor(p, ring).deleteOne(ring);
            if (one[0].isOne(ring)) {
                return ((rType == Ring.Q) || (rType == Ring.Z)) ? fpp : fpp.toNewRing(currentRing.algebra[0], ring);
            } else {
                Polynom[] mul = new Polynom[fpp.multin.length + 1];
                int[] pow = new int[fpp.powers.length + 1];
                System.arraycopy(fpp.multin, 0, mul, 1, fpp.multin.length);
                System.arraycopy(fpp.powers, 0, pow, 1, fpp.powers.length);
                mul[0] = new Polynom(new Fraction(NumberZ.ONE, one[0]));
                pow[0] = 1;
                return new FactorPol(pow, mul);
            }
        } else {
            if (rType == Ring.R64) {  //  ..... R64  ТИП
                NumberZ64 powOfmultiplyer = new NumberZ64(0L);
                p = p.toPolynom_Zfrom_64(powOfmultiplyer, ring);
                deg = -(int) powOfmultiplyer.value;
            } else { //  ..... осталось 2 типа и ... R128 приводим к R ..........
                p = this;
                if (rType == Ring.R128) {
                    p = this.toPolynom(Ring.R, ring);
                }
                NumberZ64 powOfmultiplyer = new NumberZ64(0L);
                p = p.toPolynomCZfromC(powOfmultiplyer);
                deg = -(int) powOfmultiplyer.value;
            }
            Ring ring2 = new Ring(ring, Ring.Z);
            Element factCoeffs = p.GCDNumPolCoeffs(ring2); // общий множитель коэффициентов сохраним
            if (!factCoeffs.isOne(ring2)) {
                p = p.divideByNumber(factCoeffs, ring2);
            }
            FactorPol fp = (squareFree) ? p.FactorPol_SquareFree(ring2)
                    : FactorizationManyVar.Factor(p, ring2);
            
            
            fp = fp.toNewRing(ring.algebra[0], ring);
            Element mult;
            if((ring.algebra[0]& (~Ring.MASK_C))==Ring.R64) mult=factCoeffs.multiply(ring.MachineEpsilonR64, ring);
            else {mult= new NumberR((NumberZ) factCoeffs, -deg);
                 if((ring.algebra[0]& (~Ring.MASK_C))!=Ring.R) mult=mult.toNewRing((ring.algebra[0]& (~Ring.MASK_C)), ring);
            }
             if (!mult.isOne(ring)) {
                    Polynom numbMultipl = Polynom.polynomFromNumber(mult, ring);
                    int ss = fp.multin.length + 1;
                    Polynom[] pols = new Polynom[ss];
                    int[] pows = new int[ss];
                    System.arraycopy(fp.multin, 0, pols, 1, ss - 1);
                    pols[0] = numbMultipl;
                    System.arraycopy(fp.powers, 0, pows, 1, ss - 1);
                    pows[0] = 1;
                    fp = new FactorPol(pows, pols);
                }
                return fp.deleteOne(ring);
        }
    }
//    /**
//     * Факторизуем полином, свободный от квадратов, в CZ
//     *  Приводим к виду f(z)^2 +i^2g(z)^2.
//     *  полагаем, что lc(p) \n f(z). тогда (f(z)-lc(p))/(2\sgrt(lc(p))) 
//     * частном будет корень из хвоста, а в остатке -- хвост + чужие.
//     * @param ring
//     * @return 
//     */
//    public Polynom[] factorWithI(Ring ring) {
//        int var=powers.length/coeffs.length;
//        int[] firstPow=new int[var];
//        for (int i = 0; i < var; i++){ if (powers[i]%2!=0) return null; else firstPow[i]= powers[i]>>1;}  
//        Element firstC=coeffs[0]; Element sqrtC=firstC.sqrt(ring);
//        if(!( sqrtC instanceof NumberZ))return null; Polynom H=new Polynom(firstPow, new Element[]{sqrtC});
//        Polynom[] QR=(subtract(H,ring)).divRem(H, ring); Polynom R=QR[1]; 
//      //  int[]= new int
 //       return null;
//        int totalDeg=degreeTotal();
//        if (totalDeg<2) return null; boolean flag=true;
//        for (int i = 1; i < coeffs.length; i++) {
//            if (!coeffs[i].isNegative()){flag=false; break;}
//        }if (flag)  return null;
//        int[]ppow=new int[coeffs.length+powers.length];
//        Element[]cff=new Element[coeffs.length];
//        int s=0,j=0,cf=0;
//        for (int i = 0; i <= powers.length; i++) {
//            if(s==coeffs.length){
//                if ((coeffs[cf].isNegative())||(cf==0)){ ppow[j++]= 0; cff[cf]= coeffs[cf];}
//                else {cff[cf]=coeffs[cf].negate(ring); ppow[j++]= 2;}cf++;s=0;}
//            if(i < powers.length)ppow[j++]=powers[i];
//        }   
//        Polynom P=new Polynom(ppow,cff);
//        Polynom [] res= FactorizationManyVar.PoliFactorPol(P, ring);
//        if (res.length<2) return null;
//        for (int i = 0; i < res.length; i++) {
//            
//        }
//    }
 
    // Разложение на линейные сомножител. (работает через нахождение корней
    // случайным методом)
    public FactorPol factorOfPol_inR(Ring ring) {
        FactorPol fp = this.factorOfPol_inC(ring);
        List<Element> lpol = new ArrayList<Element>();//для линейных сомножителей
        List<Element> conjpol = new ArrayList<Element>();//для сомножителей с кратными корнями
        IntList p1 = new IntList();
        IntList p2 = new IntList();
        for (int i = 0; i < fp.multin.length; i++) {
            if (fp.multin[i].coeffs.length>1){
            Element ii = fp.multin[i].coeffs[1];
            if (ii instanceof Complex) {
                Complex ci = (Complex) ii;
                if (!ci.re.isZero(ring) && ci.im.isZero(ring)) {
                    lpol.add(fp.multin[i]);
                    p1.add(fp.powers[i]);
                } else {
                    conjpol.add(fp.multin[i]);
                    p2.add(fp.powers[i]);
                }
            } }
            else {
                lpol.add(fp.multin[i]);
                p1.add(fp.powers[i]);
            }
        }
        List<Element> mconjpol = new ArrayList<Element>();//после умножения
        IntList p3 = new IntList();
        int j = 0;
        while (j < conjpol.size()) {
            Polynom p = (Polynom) conjpol.get(j).multiply(conjpol.get(j + 1), ring);
            p = p.deleteZeroCoeff(ring);
            mconjpol.add(p);
            p3.add(p2.arr[j]);
            j += 2;
        }
        Element[] pol1 = new Element[lpol.size()];
        lpol.toArray(pol1);
        Element[] pol2 = new Element[mconjpol.size()];
        mconjpol.toArray(pol2);

        int[] pow1 = new int[p1.size];
        System.arraycopy(p1.arr, 0, pow1, 0, p1.size);
        int[] pow2 = new int[p3.size];
        System.arraycopy(p3.arr, 0, pow2, 0, p3.size);

        Polynom[] polres = new Polynom[pol1.length + pol2.length];
        System.arraycopy(pol1, 0, polres, 0, pol1.length);
        System.arraycopy(pol2, 0, polres, pol1.length, pol2.length);

        int[] powres = new int[pow1.length + pow2.length];
        System.arraycopy(pow1, 0, powres, 0, pow1.length);
        System.arraycopy(pow2, 0, powres, pow1.length, pow2.length);
        return new FactorPol(powres, polres).toNewRing(Ring.R, new Ring("R[x]"));
    }

    public int[] listOfVariables() {
        int b = powers.length;
        int c = coeffs.length;
        int d = (c == 0) ? 0 : b / c;
        int[] varInd = new int[d];
        int k = 0; // index in per;
        if (d != 0) {
            for (int i = 0; i < d; i++) {
                for (int r = i; r < b; r += d) {
                    if (powers[r] > 0) {
                        varInd[k++] = i;
                        break;
                    }
                }
            }
        }
        if (k == d) {
            return varInd;
        }
        int[] res = new int[k];
        System.arraycopy(varInd, 0, res, 0, k);
        return res;
    }

    /**
     * 
     * Разложение на линейные сомножители. (работает через нахождение корней
     * случайным методом) Все типы, кроме R и C будут приведены автоматически к
     * R64, C64. Если требуется иное, то конвертните к R или C заранее. ВАЖНО!
     * Кратные сомножители выделяются точно заранее. Для этого все коэффициенты
     * входного полинома умножаются на число 10^(FLOATPOS) и округляются до
     * целой части. Полученные свободные от квадратов полиномы считаются не
     * имеющими кратных корней. ОДНАКО! Их корни могут оказаться очень близко
     * расположенными и это потребует настройки параметров ACCURACY
     * MachineEpsilonR. В случае С64 может оказаться невозможным разделить эти
     * близкие корни.
     * 
     * @param onlyRoots --  if (onlyRoots)return new VectorS(newRoots) (in the multiplicity we write the degrees of factors)
     *                      else return  new FactorPol(newPow, newMultin);
     * @param multiplicity -- array of int[][]:  multiplicity int[0][] -- the degrees of linear multipliers
     * @param ring -- Ring
     * @return if(!onlyRoots): Тип FactorPol. Разложение в произведение полиномов
     * первой степени над C или С64. При каждом полиноме указана кратность данного корня.
     * Массив multiplicity не используется.
     *         if(onlyRoots): Тип VectorS. Содержит все корни полинома. 
     * Кратности корней записаны в первой строке массива multiplicity.
     */
    public Element factorOfPol_inC(boolean onlyRoots, int[][] multiplicity, Ring ring) {
        int numbers = 0; // для числа всех сомножителей
        // нашли  сомножители  без  кратных  корней
        FactorPol factPolRational = this.factorOfPol_inQ(true, ring);// Рациональная факторизация !!!!!!!!!
        int prevAlgebra0 = -1; // при смене алгебры на R64|C64 запомним тут текущую алгебру0, и в конце вернем ее
        int ringType = ring.algebra[0] % Ring.Complex;   // Все числа кроме R64,C64,R,C отправляем в R64 или C64
        if ((ringType != Ring.R64) && (ringType != Ring.R)) { // Если же имелось кольцо Z или Q и хочется их бросить
            // в R или C, то это делайте "ручками" заранее, то есть до обращения к этой процедуре перегони
            //все данные в NumberR и смени кольцо на R.
            prevAlgebra0 = ring.algebra[0];
            ring.algebra[0] = (ring.algebra[0] < Ring.Complex) ? Ring.R64 : Ring.C64;
            factPolRational = factPolRational.toNewRing(ring.algebra[0], ring);
        }
        factPolRational.normalForm(ring);
        int n = factPolRational.multin.length; // Число сомножителей после рациональной факторизации
        Element[] firstCoeffs = new Element[n]; // тут сохраним старшие коэффициенты (или null)
        int[][] pows_i_pol = new int[n][0];    // для создания полиномов первой степени
        // тут будут готовые наборы для массива pows
        Element[][] newroot = new Element[n][0];
        int numFactManyVar = 0; //число неизменяемых сомножителей: больше 1 переменной или просто один моном
        for (int i = 0; i < n; i++) { // -------------------------------------------------------
            Polynom pp = factPolRational.multin[i];
            int ppCoefNumb = pp.coeffs.length;
            boolean isOneCoeffs0 = pp.coeffs[0].isOne(ring); //если коэффициент[0] равен 1 запомним это
            if (!isOneCoeffs0) {
                firstCoeffs[i] = pp.coeffs[0]; // старший коэффициент в полиноме
            }
            if (ppCoefNumb == 1) {
                if (!isOneCoeffs0) {
                    pp.coeffs[0] = ring.numberONE;
                }
                if (pp.powers.length != 0)  numFactManyVar++;
                continue;
            }
            // один моном не факторизуем (pows_i_pol[i].length==0)
            int[] listVars = pp.listOfVariables();         //список всех переменных в полиноме
            if (listVars.length != 1) {
                if (!isOneCoeffs0) {
                    factPolRational.multin[i] = pp.
                            divideByNumber(firstCoeffs[i], ring);
                }
                numFactManyVar++;
                continue;
            }            // если больше одной переменной, то не факторизуем, (pows_i_pol[i].length==0)
            int varN = pp.powers.length / ppCoefNumb; // найдем, как далеко расположена переменная
            int realVarN = listVars[0];               //  номер этой переменной
            // -------------  переставляем единственную переменную на первое место,
            if (varN != 1) {   // We have to create the polynomial with one first variable.
                int[] newPow = new int[ppCoefNumb];
                int ii = realVarN;
                for (int j = 0; j < ppCoefNumb; j++) {
                    newPow[j] = pp.powers[ii];
                    ii += varN;
                }
                pp = new Polynom(newPow, pp.coeffs);
            }
//            System.out.println("pp = " + pp.toString(ring));
//            for(int c = 0; c < pp.coeffs.length; c++){
//            System.out.println("ii = " + pp.coeffs[c].);
//            }
            if (pp.powers[0] > 1) {
                //Ring rr=new Ring("R64");
                //pp=(Polynom)pp.toNewRing(rr.algebra[0], rr);
                newroot[i] = pp.rootOfE(ring); // НАШЛИ все не кратные корни и обратили знаки !!!!
                for (int j = 0; j < newroot[i].length; j++) {
                    newroot[i][j] = newroot[i][j].negate(ring);
                }
            } else {
                newroot[i] = new Element[] {
                    (ppCoefNumb == 2) ? (pp.coeffs[1]).
                    divide(pp.coeffs[0], ring) : ring.numberZERO};
            }
            numbers += newroot[i].length; // подсчитаем полное количество корней
            // ----- pows array for linear polynomials in the result  -- так запомним позицию переменной
            pows_i_pol[i] = new int[2 * realVarN + 2];
            pows_i_pol[i][realVarN] = 1;
        } // -------------------------------------------------------------------------
        // сначала получим числовой коеффициент
        Element numMultiplier = ring.numberONE;
        for (int i = 0; i < n; i++) {
            if (firstCoeffs[i] != null) {
                int powM = factPolRational.powers[i];
                numMultiplier = numMultiplier.multiply(
                        (powM == 1) ? firstCoeffs[i] : firstCoeffs[i].pow(powM, ring), ring);
            }
        }
        //
        Polynom[] newMultin=null; int[] newPow=null; Element[] newRoots=null;
        int newInd = 0; // index of newMultin
        int maxMultinNumb = numFactManyVar + numbers;
        boolean numMultiplier_isOne = numMultiplier.isOne(ring);
        if ((!numMultiplier_isOne)&&(!onlyRoots)) {maxMultinNumb++;
            newPow = new int[maxMultinNumb];
            newMultin = new Polynom[maxMultinNumb];
            newMultin[0] = Polynom.polynomFromNumber(numMultiplier, ring);
            newPow[0] = 1;  newInd++;  }
        else if (onlyRoots){newRoots=new Element[maxMultinNumb];
                         multiplicity[0]=new int[maxMultinNumb];}
             else {newPow = new int[maxMultinNumb]; newMultin = new Polynom[maxMultinNumb];}
        Element compOne = new Complex(ring.numberONE, ring);
        for (int i = 0; i < n; i++) {
            int[] pow0 = pows_i_pol[i];
            if (pow0.length > 0) {
           // из корней сформируем линейные полиномы (onlyRoots=false) или просто список чисел (onlyRoots=true)
                Element[] cc = newroot[i];
                int pow1 = factPolRational.powers[i];
                if (onlyRoots){for (int j = 0; j < cc.length; j++){
                      newRoots[newInd]=cc[j];
                      multiplicity[0][newInd++]=pow1;}}
                else{
                   for (int j = 0; j < cc.length; j++) {
                     newPow[newInd] = pow1;
                     newMultin[newInd++] = new Polynom(pow0, new Element[] {compOne, cc[j]});}
                }
            } else if (factPolRational.multin[i].powers.length != 0) { //перепишем сомножитель
                newPow[newInd] = factPolRational.powers[i];
                newMultin[newInd++] = factPolRational.multin[i];
            }
        }
        if (prevAlgebra0 != -1) {
            ring.algebra[0] = prevAlgebra0; // вернули обратно настоящий тип для algebra[0], если он менялся.
        }
        return (onlyRoots)? new VectorS(newRoots): new FactorPol(newPow, newMultin);
    }

    /**
     * Get all complex roots of this polynomial.
     * The multiplicity of each roots will set in the first row of multiplicity.
     * @param multiplicity - must be set as int[1][0];
     *         You obtain here the multiplicity of roots in the first row.
     * @param ring
     * @return Element[] - array with all complex roots
     */
    public Element[] rootsOfPol_inC(int[][] multiplicity, Ring ring) {
            return ((VectorS)factorOfPol_inC(true,  multiplicity, ring)).V;
        }
    /**
     * Get factorization of this polynomial in the field of complex numbers. (C64 or C)
     * The multiplicity of each roots unknown.
     * @param ring
     * @return FactorPol: powers of which is the multiplicities,
     *           multins of which   consists of the all linear multiplyers.
     *  But the first multins may be the number which equals the highest coefficient of polynomial,
     * if it is not equal one.
     */
    public FactorPol factorOfPol_inC( Ring ring) {
            return (FactorPol)factorOfPol_inC(false, new int[1][0], ring);
    }

//    удалить с заменой на
//    factorOfPol_inC !!!!!!!!!!!!!!!!!!!!!!!!!!!!#################################!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // Разложение на линейные сомножител. (работает через нахождение корней
    // случайным методом)
    public IndexedList roots_inC(Ring ring) {
        FactorPol fp = factorOfPol_inQ(true, ring);
        int numb = 0;
        Element[][] roots = new Complex[fp.multin.length][0];
        for (int i = 0; i < fp.multin.length; i++) {
            roots[i] = fp.multin[i].rootOfE(ring);
            numb += roots[i].length;
        }

        Element[] el = new Element[numb];
        int[] pow = new int[numb];
        int pos = 0;
        int len;
        for (int i = 0; i < fp.multin.length; i++) {
            len = roots[i].length;
            System.arraycopy(roots[i], 0, el, pos, len);
            for (int j = pos; j < pos + len; j++) {
                pow[j] = fp.powers[i];
            }
            pos += len;
        }
        return new IndexedList(el, pow);
    }

 

    /**
     * возвращает результат сравнения 2-ух полиномов, true - равны, false - не
     * равны
     *
     * @param p Polynom
     * @param eps double
     *
     * @return boolean
     *
     * @author Ribakov Mixail
     */
    public boolean aproxEqual(Polynom p, Ring ring) {
        return subtract(p, ring).isZero(ring);
    }

    /**
     * возвращает результат перемножения multin из FactorPol
     *
     * @param factInput FactorPol
     *
     * @return Polynom
     *
     * @author Ribakov Mixail
     */
    public Polynom mulsFactMultin(Factor factInput, Ring ring) {
        Polynom polOutput = (Polynom) factInput.multin.get(0);
        for (int i = 1; i < factInput.multin.size(); i++) {
            polOutput = polOutput.multiply((Polynom) factInput.multin.get(i),
                    ring);
        }
        return polOutput;
    }
    /**
     * Delete zero coefficients from this polynomial.
     * If there no zero coefficients return this polynomial.
     * @param ring
     * @return 
     */
    public void  deleteZeros(Ring ring) {
        int cl = coeffs.length;
        int pl = powers.length;
        if (cl == 0)  return  ;
        int vars = pl / cl;
        int[] zeros = new int[cl];
        int k = 0;
        for (int i = 0; i < cl; i++) {
            if (coeffs[i].isZero(ring)) {zeros[k++] = i; }
        }
        if (k == 0) return  ; 
        Element[] coeff = new Element[cl - k];
        int[] power = new int[pl - k * vars];
        k = 0;
        int j = 0;
        int jp = 0;
        int ip = 0;
        for (int i = 0; i < cl; i++) {
            if (i == zeros[k]) {
                k++;
                ip += vars;
                continue;
            }
            coeff[j++] = coeffs[i];
            for (int s = 0; s < vars; s++) {
                power[jp++] = powers[ip++];
            }
        }; this.coeffs=coeff; this.powers=power;
    }
    /**
     * Delete zero coefficients from this polynomial.
     * If there no zero coefficients return this polynomial.
     * @param ring
     * @return 
     */
    public Polynom deleteZeroCoeff(Ring ring) {
        deleteZeros( ring);
//        int cl = coeffs.length;
//        int pl = powers.length;
//        if (cl == 0)  return this;
//        int vars = pl / cl;
//        int[] zeros = new int[cl];
//        int k = 0;
//        for (int i = 0; i < cl; i++) {
//            if (coeffs[i].isZero(ring)) {zeros[k++] = i; }
//        }
//        if (k == 0) { return this; }
//        Element[] coeff = new Element[cl - k];
//        int[] power = new int[pl - k * vars];
//        k = 0;
//        int j = 0;
//        int jp = 0;
//        int ip = 0;
//        for (int i = 0; i < cl; i++) {
//            if (i == zeros[k]) {
//                k++;
//                ip += vars;
//                continue;
//            }
//            coeff[j++] = coeffs[i];
//            for (int s = 0; s < vars; s++) {
//                power[jp++] = powers[ip++];
//            }
//        }
        return new Polynom(powers, coeffs);
    }
//
//    /**
//     * Процедура, возвращающая полиномы в представлении НОД двух полиномов this
//     * и r 1-ой степени через их самих
//     *
//     * @param r один из исходных полиномов
//     * @param mod модуль кольца
//     *
//     * @return result[0] НОД полиномов result[1], result[2] множители a и b при
//     * f и g, с помощью которых можно получить НОД(f, g) по следующей формуле
//     * НОД(f, g) = f*result[1]+g*result[12
//     */
//    public Polynom[] ExtendedGCD_Zx(Polynom r, long mod, Ring ring) {
//        Polynom ofF, ofG;
//        Element one = r.one(ring);
//        Element zero = r.zero(ring);
//        Polynom[] term1 = new Polynom[2];
//        Polynom[] term2 = new Polynom[2];
//        Polynom[] result = new Polynom[3];
//        Polynom[] term = new Polynom[2];
//        term1[1] = this.clone();
//        term2[1] = r.clone();
//        // Первое деление полиномов
//        if (term1[1].powers[0] >= term2[1].powers[0]) {
//            term[0] = Polynom.polynomFromNumber(zero, ring);
//            term[1] = Polynom.polynom_one(one);
//
//            Polynom term11 = term1[1].toPolynomMod(Ring.Zp32, ring);
//            Polynom term22 = term2[1].toPolynomMod(Ring.Zp32, ring);
//            term1 = term11.divAndRemRQZpx(term22, ring);
//
//            if (term1[1].coeffs.length != 0) {
//                result[1] = Polynom.polynom_one(one);
//                result[2] = (Polynom) term1[0].negate(ring);
//            } else {
//                result[1] = term[0];
//                result[2] = term[1];
//            }
//        } else {
//            term[1] = Polynom.polynomZero;
//            term[0] = Polynom.polynom_one(one);
//            Polynom term11 = term1[1].toPolynomMod(Ring.Zp32, ring);
//            Polynom term22 = term2[1].toPolynomMod(Ring.Zp32, ring);
//            term2 = term22.divAndRemRQZpx(term11, ring);
//
//            if (term2[1].coeffs.length != 0) {
//                result[2] = Polynom.polynom_one(one);
//                result[1] = (Polynom) term2[0].negate(ring);
//            } else {
//                result[2] = term[1];
//                result[1] = term[0];
//            }
//        }
//
//        // Деления двух полиномов по алгоритму Евклида
//        while (term1[1].powers.length > 0 && term2[1].powers.length > 0) {
//            if (term1[1].powers[0] > term2[1].powers[0]) {
//
//                Polynom term11 = term1[1].toPolynomMod(Ring.Zp32, ring);
//                Polynom term22 = term2[1].toPolynomMod(Ring.Zp32, ring);
//                term1 = term11.divAndRemRQZpx(term22, ring);
//
//                if (term1[1].coeffs.length == 0) {
//                    break;
//                }
//                // нахождение линейного множителя стоящего при f
//                ofF = term[0];
//                term[0] = result[1];
//                result[1] = ofF.subtract(term1[0].mulSS(result[1], ring), ring);
//                // нахождение линейного множителя стоящего при g
//                ofG = term[1];
//                term[1] = result[2];
//                result[2] = ofG.subtract(term1[0].mulSS(result[2], ring), ring);
//                // сокращение коэффициентов a=result[0] и b=result[1] в случае,
//                // когда
//                // остаток от деления равен числу, т. е. НОД(f',g')=1
//                if (term1[1].isZero(ring)) {
//                    result[1].polDivNumb(term1[1].coeffs[0], ring);
//                    result[2].polDivNumb(term1[1].coeffs[0], ring);
//                }
//            } else {
//                Polynom term11 = term1[1].toPolynomMod(Ring.Zp32, ring);
//                Polynom term22 = term2[1].toPolynomMod(Ring.Zp32, ring);
//                term2 = term22.divAndRemRQZpx(term11, ring);
//
//                if (term2[1].coeffs.length == 0) {
//                    break;
//                }
//                // нахождение линейного множителя стоящего при f
//                ofF = term[0];
//                term[0] = result[1];
//                result[1] = ofF.subtract(term2[0].mulSS(result[1], ring), ring);
//                // нахождение линейного множителя стоящего при g
//                ofG = term[1];
//                term[1] = result[2];
//                result[2] = ofG.subtract(term2[0].mulSS(result[2], ring), ring);
//                // сокращение коэффициентов a=result[0] и b=result[1] в случае,
//                // когда
//                // остаток от деления равен числу, т. е. НОД(f',g')=1
//                if (term2[1].isZero(ring)) {
//                    result[1].polDivNumb(term2[1].coeffs[0], ring);
//                    result[2].polDivNumb(term2[1].coeffs[0], ring);
//                }
//            }
//        }
//        // Вывод НОД
//        if (term1[1].coeffs.length == 0) {
//            result[0] = term2[1];
//        } else {
//            if (term2[1].coeffs.length == 0) {
//                result[0] = term1[1];
//            } else {
//                result[0] = Polynom.polynom_one(one);
//                if (term1[1].isZero(ring)) {
//                    result[1] = result[1].polDivNumb(term1[1].coeffs[0], ring);
//                    result[2] = result[2].polDivNumb(term1[1].coeffs[0], ring);
//                } else {
//                    result[1] = result[1].polDivNumb(term2[1].coeffs[0], ring);
//                    result[2] = result[2].polDivNumb(term2[1].coeffs[0], ring);
//                }
//            }
//        }
//        return result;
//    }

    /**
     * Процедура нахождения частного и остатка при делении двух полиномов this и
     * q. Это полиномы над полем. (R64, Zp, Q)
     *
     * @param q делитель
     * @param ring Ring
     *
     * @return массив полиномов: на первом месте стоит частное от деления this
     * на q, на втором остаток;
     */
    public Polynom[] divAndRemRQZpx(Polynom q, Ring ring) {
        Element zero = q.zero(ring);
        Element one = q.one(ring);
        Polynom p = this;
        if (q.powers.length == 0) {
            return new Polynom[] {p.divideByNumber(q.coeffs[0], ring),
                Polynom.polynomFromNumber(zero, ring)};
        }
        if (powers.length == 0 || powers[0] < q.powers[0]) {
            return new Polynom[] {Polynom.polynomFromNumber(zero, ring), p};
        }
        Element inversFirstMonQ = one.divide(q.coeffs[0], ring);
        int t = 0;
        Polynom[] chast = new Polynom[p.powers[0] - q.powers[0] + 1];
        // деление полиномов
        while (p.powers.length != 0 && q.powers[0] <= p.powers[0]) {
            // выделение элемента частного
            Polynom chastnoe = new Polynom();
            chastnoe.coeffs = new Element[1];
            chastnoe.coeffs[0] = (p.coeffs[0].multiply(inversFirstMonQ, ring));
            if (p.powers[0] == q.powers[0]) {
                chastnoe.powers = new int[] {};
            } else {
                chastnoe.powers = new int[] {p.powers[0] - q.powers[0]};
            }
            chast[t] = chastnoe;
            p = p.interimRem(q, chast[t], ring);
            t++;
        }
        // Собирание результата частного
        if (t == 1) {
            return new Polynom[] {chast[0], p};
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
        return new Polynom[] {result, p};
    }

    /**
     * Vozvrashaet iz deap {0..p} v {-p/2..p/2}
     */
    public void ModZp32(Ring ring) {
        for (int i = 0; i < this.coeffs.length; i++) { // нормализация полинома
            this.coeffs[i] = ((NumberZp32) this.coeffs[i]).Mod(ring);
        }
    }

    /**
     * Vozvrashaet iz deap {0..p} v {-p/2..p/2}
     */
    public void ModZp(Ring ring) {
        for (int i = 0; i < this.coeffs.length; i++) { // нормализация полинома
            this.coeffs[i] = ((NumberZp) this.coeffs[i]).Mod(ring);
        }
    }

    /**
     * Vozvrashaet iz deap {0..p} v {-p/2..p/2}
     */
    @Override
    public Polynom Mod(Element p, Ring ring) {
        if (p instanceof NumberZ) {
            ring.setMOD((NumberZ) p);
        }
        if (p instanceof NumberZ64) {
            ring.setMOD32(p.longValue());
        }
        int n = coeffs.length;
        Element[] c = new Element[n];
        for (int i = 0; i < n; i++) {
            c[i] = coeffs[i].Mod(ring);
        }
        return (new Polynom(powers, c)).deleteZeroCoeff(ring);
    }
    @Override
    public Polynom Mod(  Ring ring) {
        int n = coeffs.length;
        Element[] c = new Element[n];
        for (int i = 0; i < n; i++) {
            c[i] = coeffs[i].Mod(ring);
        }
        return (new Polynom(powers, c)).deleteZeroCoeff(ring);
    }


    /**
     * *********************** Pozdnikin Alexey
     * **********************************
     */
    public Polynom[] getmonoms(Element itsCoeffOne, Ring ring) {
        Polynom monoms[] = new Polynom[this.coeffs.length];
        int vars = this.powers.length / this.coeffs.length;
        int pow[] = new int[vars];
        Element cfs[] = new Element[1];
        int k = 0;
        for (int i = 0; i < this.coeffs.length; i++) {
            System.arraycopy(this.powers, i + k, pow, 0, vars);
            System.arraycopy(this.coeffs, i, cfs, 0, 1);
            k = k + vars - 1;
            monoms[i] = new Polynom(pow, cfs).mulSS(
                    polynom_one(itsCoeffOne.one(ring)), ring);
        }
        return monoms;
    }

    /**
     * Convert file to FPolynom
     *
     * @param filename
     * @param itsCoeffOne
     *
     * @return
     */
    public FPolynom toFPolynom(File filename, Element itsCoeffOne) {
        try {
            File fvar = new File(filename.getParentFile(), filename.getName()
                    + "_var");
            ObjectOutputStream rw_var = new ObjectOutputStream(
                    new FileOutputStream(fvar));
            ObjectOutputStream rw_pol = new ObjectOutputStream(
                    new FileOutputStream(filename));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            int vars = powers.length / coeffs.length;
            int index = 0;
            for (int i = 0; i < coeffs.length; i++) {

                byte[] b = FileUtils.toByteArray(coeffs[i]);
                dos.writeInt(b.length); // пишем в файл количество байт, которое
                // понадобилось для коэффициента
                rw_pol.write(b);// пишем коэффициент в поток
                // rw_pol.flush();

                for (int j = 0; j < vars; j++) {
                    rw_pol.writeInt(powers[j + index]);
                }
                index += vars;
            }
            rw_var.writeLong(coeffs.length);
            rw_var.writeInt(vars);
            rw_var.writeObject(itsCoeffOne);
            baos.writeTo(rw_var);

            baos.close();
            dos.close();
            rw_pol.close();
            rw_var.close();
        } catch (Exception e) {
        }
        return new FPolynom(filename);
    }

    public Term term(int num) {
        // length of each term
        int n = this.powers.length / this.coeffs.length;
        // count of ending zeroes in term
        int k = 0;
        for (int i = n - 1; i >= 0; i--) {
            // if i-th power of (var*num)-th term == 0
            if (this.powers[n * num + i] == 0) {
                k++;
            } else {
                break;
            }
        }

        // create array for term without ending zeroes
        int res[] = new int[n - k];
        for (int i = 0; i < n - k; i++) {
            res[i] = this.powers[n * num + i];
        }
        Term term = new Term(res);
        return term;
    }

    public boolean verifyReduction(Polynom a) {
        return this.hTerm().isReducibleBy(a);
    }
/**
 * Full number of variables in this polynomial
 * This number depends on the order of variables in the ring.
 * This procedure return the greatest order  which have
 * variables of this polynomial among the ring variables.
 * @return (powers.length / coeffs.length)
 */
    public int getVarsNum() {
        if (coeffs.length == 0) {return 0;}
        return powers.length / coeffs.length;
    }

    public Term hTerm() {
        return term(0);
    }

    public NumberR U_BOUND(Ring ring) {
        NumberR ub = NumberR.ZERO;
        NumberR tempub;
        NumberZ temp;
        int lamda = 0;
        int n = coeffs.length;
        Element[] cl = new Element[coeffs.length];
        MathContext mc = new MathContext(200, RoundingMode.HALF_UP);

        if (coeffs[0].signum() < 0) {
            for (int i = coeffs.length - 1; i > -1; i--) {
                cl[i] = coeffs[i].negate(ring);
                if (cl[i].signum() < 0) {
                    lamda += 1;
                }
            }
        } else {
            for (int i = coeffs.length - 1; i > -1; i--) {
                cl[i] = coeffs[i];
                if (cl[i].signum() < 0) {
                    lamda += 1;
                }
            }
        }

        if (n <= 1 || lamda == 0) {
            return ub;
        }

        for (int i = n - 1; i > -1; i--) {
            if (cl[i].signum() < 0) {
                tempub = (NumberR) ((NumberR) ((NumberR) cl[i].negate(ring))).divide((NumberR) cl[0], mc);
                if ((tempub.subtract(NumberR.ONE)).signum() > 0) {
                    temp = (NumberZ) tempub.toNumber(Ring.Z, Ring.ringR64xyzt);
                    temp = NFunctionZ.lessThenDoubledRoot(temp, powers[0] - powers[i]);
                    tempub = new NumberR(temp);
                } else {
                    tempub = NumberR.ONE;
                }
                tempub = (NumberR) tempub.multiply(new NumberR(2));
                if ((tempub.subtract(ub)).signum() > 0) {
                    ub = tempub;
                }
            }
        }

        return ub;
    }

//    /**
//     * Degree of the highest variable of this polynomial
//     * @return
//     */
//    @Override
//    public NumberZ degree() {return new NumberZ(degree(0));}
//    
    /**
     * Maximal degree which has some variable k (k=0,..,numberOfVar-1)
     *
     * @param k order number of given variable (k=0,..,numberOfVar-1)
     *
     * @return Maximal degree which has variable k (return -1, if k is greater
     * than number of variables, or k is negative)
     */
    public int degree(int k) {
        int max = -1;
        int nV = getVarsNum();
        if ((k < nV) && (k >= 0)) {
            for (int r = k; r < powers.length; r += nV) {
                if (powers[r] > max) {
                    max = powers[r];
                }
            }
        }
        return max;
    }

    /**
     * Group polynomial of variable k (k=0,..,numberOfVar-1)
     */
    public FactorPol groupPolynomialOfVar(int k, Ring ring) {
        FactorPol res = new FactorPol();
        if (k == ring.varNames.length - 1) {
            return coefOfHightVar(ring);
        }
        int n = degree(k) + 1;
        if (k == -1) {
            return null;
        }
        int nV = getVarsNum();
        int[] lengthOfCoeffs = new int[n];
        int[] pointer = new int[n];
        for (int r = k; r < powers.length; r += nV) {
            lengthOfCoeffs[powers[r]]++;
        }
        Element[][] cc = new Element[n][];
        int[][] pp = new int[n][];
        for (int i = 0; i < n; i++) {
            cc[i] = new Element[lengthOfCoeffs[i]];
            pp[i] = new int[nV * lengthOfCoeffs[i]];
        }
        int index_coef = 0;
        for (int r = k; r < powers.length; r += nV) {
            int pow = powers[r];
            int cc_point = pointer[pow]++;
            cc[pow][cc_point] = coeffs[index_coef];
            int start = cc_point * nV - k;
            index_coef++;
            for (int i = 0; i < nV; i++) {
                pp[pow][start + i] = powers[r - k + i];
            }
            pp[pow][start + k] = 0;
        }

        res.multin = new Polynom[n];
        res.powers = new int[n];
        for (int i = 0; i < n; i++) {
            res.multin[i] = new Polynom(pp[i], cc[i]).ordering(ring);
            res.powers[i] = i;
        }
        return res;
    }

    /**
     * Расщепить полином на сумму мономов по старшей переменной. Упаковать
     * результат в FactorPol --> как список полиномов и список степеней старшей
     * переменной.
     *
     * @param ring Ring
     *
     * @return FactorPol.multin=мономы, FactorPol.pow=степени старшей пер, так,
     * что this=SUM multin[i]* Z^pow[i].
     */
    public FactorPol coefOfHightVar(Ring ring) {
        int vars = this.powers.length / this.coeffs.length;
        int vars1 = vars - 1;
        Polynom[] pol = new Polynom[this.coeffs.length];
        int k = 0;
        int j = 0, jpow = 0, jpoww = 0, jw;
        int[] poww = new int[this.powers.length];
        Element[] coeffw = new Element[this.coeffs.length];
        int[] powHight = new int[this.coeffs.length];

        while ((jpow) < this.powers.length) {
            coeffw[0] = this.coeffs[j];
            jw = 1;
            System.arraycopy(this.powers, jpow, poww, 0, vars1);
            powHight[k] = this.powers[jpow + vars1];
            int deg = powHight[j];
            j++;
            jpow += vars;
            jpoww += vars1;
            while (((jpow) < this.powers.length)
                    && (deg == this.powers[jpow + vars1])) {
                coeffw[jw++] = this.coeffs[j++];
                System.arraycopy(this.powers, jpow, poww, jpoww, vars1);
                jpow += vars;
                jpoww += vars1;
            }
            Element[] coeffw2 = new Element[jw];
            int[] poww2 = new int[jw * vars1];
            System.arraycopy(poww, 0, poww2, 0, jw * vars1);
            System.arraycopy(coeffw, 0, coeffw2, 0, jw);
            pol[k] = new Polynom(poww2, coeffw2);
            k++;
            jpoww = 0;
        }
        Polynom[] res = new Polynom[k];
        System.arraycopy(pol, 0, res, 0, k);
        int[] resHight = new int[k];
        System.arraycopy(powHight, 0, resHight, 0, k);
        for (int b = 0; b < res.length; b++) {
            res[b] = res[b].normalNumbVar(ring);
        }
        return new FactorPol(resHight, res);

    }

    public String groupPolToString(FactorPol groupPol, int k, Ring ring) {
        String vv = ring.varNames[k];
        StringBuilder sb = new StringBuilder();
        int m = groupPol.powers.length;
        boolean first = true;
        for (int i = m - 1; i >= 0; i--) {
            int powi = groupPol.powers[i];
            if (groupPol.multin[i].powers.length != 0) {
                if (first) {
                    first = false;
                } else {
                    sb.append('+');
                }
                sb.append('(');
                sb.append(groupPol.multin[i].toString(ring));
                sb.append(')');
                if (powi != 0) {
                    sb.append(vv);
                    if (powi != 1) {
                        sb.append('^');
                        sb.append(powi);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Actual number Of Variables in this polynomial.
     * This number may be less then powers.length/coeffs.length
     * @return actual number of variables
     */
    public int numberOfVariables() {
        int[] vv = variables();
        int res = 0;
        for (int i = 0; i < vv.length; i++) res += vv[i];
        return res;
    }

    /**
     * Vector of 0/1 integers, which shows absent variables
     * @return int[] array with 1 in the position of exist variables
     *                          0 in the position of absent variables
     */
     public int[] variables( ) {
        int d =   powers.length /  coeffs.length;
        int[] u=new int[d];
        if (d == 0) return u;
        for (int i = 0; i < d; i++)
           for (int r = i; r < powers.length; r += d)
              if ( powers[r] > 0) {u[i]=1; break;}
        return u;
    }


    @Override
    public int compareTo(Element o) {
        int v = o.numbElementType();
        int m = Ring.Polynom;
        if (m > v) {
            return 1;
        } else if (m < v) {
            return -1;
        } else {
            Polynom p = (Polynom) o;
            int length_p = p.coeffs.length;
            int length_this = coeffs.length;
            if ((length_p == 0) && (length_this == 0)) {
                return 0;
            }
            if (length_p == 0) {
                return signum();
            }
            if (length_this == 0) {
                return -p.signum();
            }
            int pCoeffType = p.coeffs[0].numbElementType();
            int coeffType = coeffs[0].numbElementType();
            if (coeffType > pCoeffType) {
                return 1;
            } else if (coeffType < pCoeffType) {
                return -1;
            } else {
                if (powers.length / coeffs.length > p.powers.length
                        / p.coeffs.length) {
                    return 1;
                }
                if (powers.length / coeffs.length < p.powers.length
                        / p.coeffs.length) {
                    return -1;
                }
                int leng = Math.min(coeffs.length, p.coeffs.length), vars = p.powers.length
                        / p.coeffs.length;
                for (int i = 0; i < leng; i++) {
                    for (int j = vars - 1; j >= 0; j--) {
                        if (powers[i * vars + j] > p.powers[i * vars + j]) {
                            return 1;
                        }
                        if (powers[i * vars + j] < p.powers[i * vars + j]) {
                            return -1;
                        }
                    }
                    int comp = coeffs[i].compareTo(p.coeffs[i]);
                    if (comp > 0) {
                        return 1;
                    }
                    if (comp < 0) {
                        return -1;
                    }
                }
                if (coeffs.length > p.coeffs.length) {
                    return 1;
                } else {
                    if (coeffs.length < p.coeffs.length) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }


    /**
     * 
     * Удаляет отсутствующие младшие переменные в полиноме, уменьшая общую
     * длинну массива степеней. Аргумент newVar изначально передается вызывающей
     * программой заполненный -1,а после завершения работы процедуры
     * присутствующие в полиноме переменные будут отмечены положительным числом
     * равным их номеру в позиции их номера. входной newVar (-1,-1,-1,-1,-1),
     * полином (x-q) принадлежищий (Z[x,y,z,a,q]) на выходе полином (x-q)
     * принадлежищий (Z[x,q]) и newVar (0,4,-1,-1,-1)
     *  ==.Придумано Димой Ивашовым для уплотнения перед факторизацией, 
     *  == После факторизации делается разрежение.
     * @param newVar - flags, which demonstrated the picture of existing variables:
     * -- the numbers of existing variables are in the firsts positions. All lasts positions have numbers -1.
     * @return dense polynomial 
     */
    public Polynom denseVariables(int newVar[]) {
        int powerlen = powers.length / coeffs.length;
        int varNumb = 0;
        for (int i = 0; i < powerlen; i++) {
            boolean flag = false;
            for (int j = i; j < powers.length; j += powerlen) {
                if (powers[j] != 0) {flag = true; break;}            }
            if (flag) {newVar[varNumb++] = i;}
        }
        if (powerlen == varNumb) {return this;} else for(int j=varNumb;j<powerlen;j++) newVar[j] = -1;
        int length = varNumb * coeffs.length;
        int[] powN = new int[length];
        int shift = 0;
        int shiftN = 0;
        for (int i = 0; i < coeffs.length; i++) {
            for (int j = 0; j < varNumb; j++) {
                powN[j + shiftN] = powers[newVar[j] + shift];
            }
            shiftN += varNumb;
            shift += powerlen;
        }
        return new Polynom(powN, coeffs);
    }

    /**
     * Undancing of polynomial
     *
     * @param newVar which was obtained in danceVariable
     *
     * @return sparce written polynomial
     */
    public Polynom danceVariableBack(int[] oldVar) {
        int varNumb = oldVar.length;
        boolean flagMinus = false;
        int numbDV = 0;
        int row;
        int len = varNumb * coeffs.length;
        while (numbDV < varNumb) {
            row = oldVar[numbDV];
            if (row < 0) {flagMinus = true; break;}
            numbDV++;
        }
        if (!flagMinus) {return this;}
        int[] powN = new int[len];
        int shift = 0; int shiftN = 0;
        for (int i = 0; i < coeffs.length; i++) {
            for (int j = 0; j < numbDV; j++) {powN[oldVar[j] + shiftN] = powers[j + shift];}
            shiftN += varNumb;
            shift += numbDV;
        }
        return new Polynom(powN, coeffs);
    }
 
  

    /**
     * Процедура выделения кратных сомножителей полинома многих переменных
     * Числовой общий множитель коэффициентоввключается в произведение !
     *
     * @param Ring ring
     */
    public FactorPol FactorPol_SquareFreeWithCoeff(Ring ring) {
        Element coeff = this.GCDNumPolCoeffs(ring);
        Polynom pp = (coeff.isOne(ring)) ? this
                : this.divideByNumber(coeff, ring);
        FactorPol fp = pp.FactorPol_SquareFree(ring);
        if (!coeff.isOne(ring)) {
            int k = fp.powers.length;
            int[] pow = new int[k + 1];
            Polynom[] pols = new Polynom[k + 1];
            System.arraycopy(fp.multin, 0, pols, 1, k);
            System.arraycopy(fp.powers, 0, pow, 1, k);
            pow[0] = 1;
            pols[0] = Polynom.polynomFromNot0Number(coeff);
            return new FactorPol(pow, pols);
        } else {
            return fp;
        }
    }

    /** Factorization polynomials of many variables up to the
     *  product of Square-Free-Polynomials
     *  New version 27/05/17
     * 
     * @param ring Ring
     * @return FactorPol object, which contains of factors and their degrees
     */
    public FactorPol FactorPol_SquareFree(Ring ring) {
       Polynom f = this;  int nVar =f.varNumb(); 
       Polynom[] diffV = new Polynom[nVar+1]; Polynom vf=f;
       int ii=0;
       while(!vf.isItNumber()){Polynom gC=vf.GCDHPolCoeffs(ring); 
             if(!gC.isOne(ring)){vf=vf.divideExact(gC, ring);} 
             diffV[ii]=vf; vf=gC; ii++;
       }diffV[ii]=vf;
       FactorPol[] fpi = new FactorPol[ii];
       //  main loop ------------------- main loop ------------------------
        for (int j = 0; j < ii; j++) {
            f=diffV[j]; nVar=f.varNumb(); int n =f.degrees()[nVar-1];
            Polynom[] k = new Polynom[n+1];
            k[0] = f.gcd(f.D(nVar-1,ring), ring); 
            int m = 0;
            while  (k[m].varNumb()== nVar) {
                k[m + 1] =  k[m].gcd(k[m].D(nVar-1,ring), ring); m++;} 
            m++;
            Polynom[] r = new Polynom[m];  
            r[0] = f.divideExact(k[0], ring);
            if (m > 1) {Polynom[] s = new Polynom[m]; 
                int  counter = 0;
                for (int i = 1; i < m-1; i = i + 1) {
                      r[i] =   k[i-1].divideExact(k[i], ring);
                      s[i-1] =  r[i-1].divideExact(r[i], ring);
                      if (s[i-1].isOne(ring)) {counter++; }
                  }
                 r[m-1]  = k[m-2].divideExact(k[m-1], ring);
                 s[m - 2]= r[m-2].divideExact(r[m-1], ring);
                 s[m-1] = r[m-1];
                 if (s[m-2].isOne(ring)) {counter++; }
                 if (s[m-1].isOne(ring)) {counter++; }
                 int b = m - counter;
                 Polynom[] a = new Polynom[b]; int[] a_pow= new int[b];
                 int jj = 0;
                 for (int i = 0; i < m; i++) {
                    if (!s[i].isOne(ring)) { a[jj] = s[i]; a_pow[jj] = i+1; jj++;} 
                 } 
                 fpi[j]= new FactorPol(a_pow, a);
            } else {fpi[j]= new FactorPol(r[0], 1);}
       } 
        int i = (diffV[ii].isOne(ring))? 1:0; 
         FactorPol res= (i==1)? fpi[0]:  new FactorPol(diffV[ii],1);
        for (; i < ii; i++) { 
            res=res.multiply(fpi[i], ring);}
        return res;
    }


    /**
     * Пытается вычислить корень var-ой степени данного полинома. Перед началом
     * работы полином раскладывается на множители
     * (FP=this.FactorPol_SquareFree). Возвращает: 1) FactorPol, если FP вышел
     * из-под корня. 2) F.ROOTOF(FactorPol), если FP вообще не выносится из-под
     * корня. 3) FactorPol*F.ROOTOF(FactorPol) в остальных случаях.
     *
     * @param var степень корня.
     * @param ring ring.
     *
     * @return sqrt[var](this)
     */
    @Override
    public Element rootOf(int n, Ring ring) {
        FactorPol fp = FactorPol_SquareFree(ring);
        List<Integer> newMultinIndices = new ArrayList<Integer>();
        List<Integer> newMultinRootOfIndices = new ArrayList<Integer>();
        List<Integer> newPowers = new ArrayList<Integer>();
        List<Integer> newPowersRootOf = new ArrayList<Integer>();
        for (int i = 0, multinCnt = fp.multin.length; i < multinCnt; i++) {
            int currPower = fp.powers[i];
            int newPowerQuot = currPower / n;
            int newPowerRem = currPower % n;
            if (newPowerQuot != 0) {
                newMultinIndices.add(i);
                newPowers.add(newPowerQuot);
            }
            if (newPowerRem != 0) {
                newMultinRootOfIndices.add(i);
                newPowersRootOf.add(newPowerRem);
            }
        }
        Element beforeRootOf = buildFactorPolOrPolynom(fp, newMultinIndices, newPowers, ring);
        Element rootOf = buildFactorPolOrPolynom(fp, newMultinRootOfIndices, newPowersRootOf, ring);
        rootOf = (rootOf != null) ? new F(F.ROOTOF, new Element[] {
            rootOf, ring.numberONE().multiply(new NumberZ(n), ring)
        }) : null;
        Element result;
        if (beforeRootOf != null && rootOf != null) {
            result = new F(F.MULTIPLY, new Element[] {beforeRootOf, rootOf});
        } else if (beforeRootOf != null) {
            result = beforeRootOf;
        } else {
            result = rootOf;
        }
        return result;
    }

    /**
     * Из данного {@code fp} строит новый {@code FactorPol}, извлекая множители
     * из {@code fp} по индексам {@code multinIndices}, со степенями
     * {@code newPowers}.
     *
     * @param fp {@code FactorPol}, из которого берутся множители.
     * @param multinIndices индексы множителей из {@code fp}.
     * @param newPowers степени в построенном {@code FactorPol}.
     * @param ring ring.
     *
     * @return
     */
    private Element buildFactorPolOrPolynom(FactorPol fp, List<Integer> multinIndices,
            List<Integer> newPowers, Ring ring) {
        Element result = null;
        if (!multinIndices.isEmpty()) {
            int multinCnt = multinIndices.size();
            int[] rootOfPowers = new int[multinCnt];
            Polynom[] rootOfMultin = new Polynom[multinCnt];
            for (int i = 0; i < multinCnt; i++) {
                rootOfPowers[i] = newPowers.get(i);
                rootOfMultin[i] = fp.multin[multinIndices.get(i)];
            }
            result = new FactorPol(rootOfPowers, rootOfMultin);
        }
        return result;
    }

    /**
     * Процедура выделения "коэффициента" при некоторой степени S старшей
     * переменной.
     *
     * @param numbV -- порядковый номер переменной в кольце
     * @return полином-коэффициент при степени S по старшей переменной,
     * записанный в кольце полиномов, у которого на одну переменную меньше
     */
    public FactorPol toCoeffsHighestVar(int numbV) {
        int vars = 0;
        if (coeffs.length != 0) {
            vars = powers.length / coeffs.length;
        }
        if ((vars < numbV) || (vars == 0)) {
            return new FactorPol(new int[] {0}, new Polynom[] {this});
        }
        int nVars = vars - 1;
        int s = powers[nVars];
        int j = 0;
        int d = nVars;
        int shift = 1;
        int w = d + vars;
        for (int h = 1; h < coeffs.length; h++) {
            if (powers[w] != s) {
                shift++;
                s = powers[w];
            }
            w += vars;
        } // насчитали сколько будет таких Мономов
        Polynom[] r = new Polynom[shift];
        int[] degry = new int[shift];
        int i = 0;
        int start = 0;
        for (; i < shift; i++) {
            int k = powers[d];
            int size = 0;
            IntList pow = new IntList();
            ArrayList<Element> coef = new ArrayList<Element>();
            while ((d < powers.length) && (k == powers[d])) {
                System.arraycopy(powers, start, pow.arr, size, nVars);
                size += nVars;
                start += vars;
                d += vars;
                coef.add(coeffs[j]);
                j++;
            }
            int[] p1 = new int[size];
            System.arraycopy(pow.arr, 0, p1, 0, size);
            Element[] p2 = new Element[coef.size()];
            coef.toArray(p2);
            r[i] = new Polynom(p1, p2);
            degry[i] = k;
        }
        return new FactorPol(degry, r);
    }

    /**
     * Процедура выделяет "коэффициенты" при разложении по степеням
     * переменной numbV.
     *
     * @param numbV - порядковый номер переменной в кольце.(0,1,2..)
     * @return вектор, у которого первая половина -- коэффициенты при переменной 
     * numbV, а вторая половина -- соответствующие степени этой переменной. 
     * Коэффициенты имеют на одну переменную меньше.
     */
    public VectorS toVectorForHighestVar(int numbV) {
      FactorPol fp= toCoeffsHighestVar(numbV);
      int n= fp.powers.length;
      int m=2*n;
      Element[] VV=new Element[m];
      System.arraycopy(fp.multin, 0, VV, 0, n);
      for (int i = 0; i < n; i++) {
            VV[n+i]= new NumberZ64(fp.powers[i]);
        }
      return new VectorS(VV);
    }
    /**
     * Процедура выделяет "коэффициенты" при разложении по степеням переменной numbV.
     * 
     * @param vectSize размер вектора будет не менее этого числа, 
     *                 но может быть больше его, если степень полинома плюс 1 превышает vectSize.
     * @param numbV  - индекс переменной, которая считается старшей
     * @param ring Ring
     * @return VectorS -- в котором записаны все коэффициенты при переменной numbV в порядке убывания, включая нулевые
     */
    public VectorS toVectorForHighestVarWithZeros(int vectSize, int numbV, Ring ring) {
        VectorS v=toVectorForHighestVar( numbV);
        Element[] V=v.V;
        int n= V.length;
        int m=n/2;
        int l= V[m].intValue();
        int L= Math.max(vectSize, l+1);
        int z=L-(l+1);
        Element[] W=new Element[L];
        if(L==m){System.arraycopy(V, 0, W, 0, m);} 
        else{ for (int s = 0; s < z; s++){ W[s]=ring.numberZERO();}
              W[z]=V[0]; int  i=1;
              for (int s = z+1; s < L; s++){         l--;     
                W[s]= ((m+i<n)&&(V[m+i].intValue()==l))?  V[i++] :ring.numberZERO();}
        }
      return new VectorS(W);
    }
    /**
     * Процедура выделяет "коэффициенты" при разложении по степеням
     * переменной numbV.
     *
     * @param numbV - порядковый номер переменной в кольце.(0,1,2..)
     * @param ring - Ring
     * @return вектор, у которого первая половина -- коэффициенты при переменной 
     * numbV, а вторая половина -- соответствующие степени этой переменной. 
     * Коэффициенты имеют на одну переменную меньше.
     */ 
    public VectorS toVectorSparce(int numbV, Ring ring){
            return   toVectorForHighestVar(  numbV);}   
   /**
     * Процедура выделяет "коэффициенты" при разложении по степеням
     * переменной numbV.
     *
     * @param vectSize -- the minimal size of vector 
     *    (if it is too little ерут  it will be increased )
     * @param  numbV - порядковый номер переменной в кольце.(0,1,2..)
     * @param ring -Ring
     * @return вектор, где стоят коэффициенты при переменной 
     * numbV в порядке убывания. Нулевые коэффициенты присутствуют.
     */
    public VectorS toVectorDence(int vectSize, int numbV,  Ring ring){
            return   toVectorForHighestVarWithZeros(vectSize, numbV, ring);}
    /**
     * *********************** Pozdnikin Alexey (for FPolynom)
     * **********************************
     */
 
    
    public Polynom[] getmonoms() {
        Polynom monoms[] = new Polynom[this.coeffs.length];
        int vars = this.powers.length / this.coeffs.length;
        int pow[] = new int[vars];
        Element cfs[] = new Element[1];
        int k = 0;
        for (int i = 0; i < this.coeffs.length; i++) {
            System.arraycopy(this.powers, i + k, pow, 0, vars);
            System.arraycopy(this.coeffs, i, cfs, 0, 1);
            k = k + vars - 1;
            monoms[i] = new Polynom(pow, cfs);
          //.mulSS(Polynom.polynom_one(itsCoeffOne),new Ring(itsCoeffOne.numbElementType(), 1));
        }
        return monoms;
    }

    // тип Polynom конвертируется в тип файлового полинома FPolynom
    public FPolynom toFPolynom(File filename, Element itsCoeffOne, long quantum) {
        try {
            DataOutputStream rw_pol = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(filename)));

            File fvar = new File(filename.getParentFile(), filename.getName()
                    + "_var");
            DataOutputStream rw_var = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(fvar)));

            int vars = this.getVarsNum();

            rw_var.writeInt(FPolynom.toInt(itsCoeffOne)); // записываем в файл
            // тип коэффициентов
            rw_var.writeInt(vars); // записываем в файл количество переменных
            rw_var.writeLong(coeffs.length); // записываем в файл количество
            // мономов

            Polynom[] arr = this.toPolynoms(quantum);

            int sumbytes = 0;

            for (int i = 0; i < arr.length; i++) {
                byte[] b = FileUtils.toByteArray(arr[i]);
                rw_pol.write(b);
                sumbytes += b.length;
                rw_var.writeLong(sumbytes);
            }
            rw_pol.close();
            rw_var.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FPolynom(filename);
    }

    /**
     * Нарезает полином на мономы
     *
     * @param ring Ring
     *
     * @return массив полиномов, каждый из которых - один моном исходного
     * полинома
     */
    public Polynom[] toMonomials(Ring ring) {
        if (isZero(ring))  return new Polynom[0];
        int n = coeffs.length;
        int var = powers.length / n;
        Polynom[] m = new Polynom[n];
        for (int i = 0; i < n; i++) {
            int[] p = new int[var];
            System.arraycopy(powers, i * var, p, 0, var);
            m[i] = new Polynom(p, new Element[] {coeffs[i]});
        }
        return m;
    }

    // процедура разбивает исходный полином на массив полиномов
    /**
     * Разрезать полином по мономам так чтобы получить массив полиномов, каждый
     * из которых требеут для сохранения примерно quantum байт
     *
     * @param quantum число байт в объекте, который соответствует каждому из
     * полиномиальных фрагментов, примерно равно quantum и превосходит не более,
     * чем на число байт в одном мономе.
     *
     * @return массив полиномов, сумма которых равна исходному полиному
     *
     * @throws Exception
     */
    public Polynom[] toPolynoms(long quantum) throws Exception {
        ArrayList<Polynom> polynoms = new ArrayList<Polynom>();
        ArrayList<Integer> pows = new ArrayList<Integer>();
        ArrayList<Element> cfs = new ArrayList<Element>();
        int vars = this.getVarsNum();
        int numbytes;
        int i = 0, index = 0;
        Polynom pol = Polynom.polynomZero;
        do {
            do {
                cfs.add(this.coeffs[i]);
                for (int j = 0; j < vars; j++) {
                    pows.add(powers[index + j]);
                }
                pol = new Polynom(pows.toArray(new Integer[pows.size()]),
                        cfs.toArray(new Element[cfs.size()]));
                numbytes = FileUtils.ObjectSize(pol);
                i++;
                index += vars;
            } while ((numbytes < quantum) && (i < coeffs.length - 1));
            polynoms.add(pol);
            cfs.clear();
            pows.clear();
        } while (i < coeffs.length);
        return polynoms.toArray(new Polynom[polynoms.size()]);
    }

    //@Override
    public Element ln(Ring ring) {
        return (!isItNumber()) ? new F(F.LN, new Element[] {this})
                : (isZero(ring)) ? ring.numberZERO.ln(ring)
                : coeffs[0].ln(ring);
    }

    @Override
    public Element lg(Ring r) {
        return (!isItNumber()) ? new F(F.LG, new Element[] {this})
                : (isZero(r)) ? r.numberZERO.lg(r)
                : coeffs[0].lg(r);
    }

    @Override
    public Element exp(Ring r) {
        return (!isItNumber()) ? new F(F.EXP, new Element[] {this})
                : (isZero(r)) ? r.numberONE
                : coeffs[0].exp(r);
    }

    @Override
    public Element sqrt(Ring r) {
        return (!isItNumber()) ? new F(F.SQRT, new Element[] {this})
                : (isZero(r)) ? r.numberZERO
                : coeffs[0].sqrt(r);
    }
  
    /** The first sgrt, i.e. with the sign PLUS for exact square expression. */
    public Element sqrtTheFirst(Ring r) {
      if (isItNumber()) 
          {return (isZero(r)) ? r.numberZERO: coeffs[0].sqrt(r);}      
      if (coeffs.length==1) {  int i=0;   int[] npow=new int[powers.length];
        for ( ; i < powers.length; i++) {
           if( powers[i]%2==1) break; else npow[i]=powers[i]/2;}
         if (i==powers.length){Element s=coeffs[0].sqrt(r); 
         return (s.isNaN())? s:  new Polynom(npow,new Element[]{s});  }        
      }
      // еще бы тут надо попробовать this факторизовать.......
      return  new F(F.SQRT, new Element[] {this});
    }

    @Override
    public Element sin(Ring r) {
        return (!isItNumber()) ? new F(F.SIN, new Element[] {this})
                : (isZero(r)) ? r.numberZERO
                : coeffs[0].sin(r);
    }

    @Override
    public Element cos(Ring r) {
        return (!isItNumber()) ? new F(F.COS, new Element[] {this})
                : (isZero(r)) ? r.numberONE.pi(r).divide(r.posConst[2], r)
                : coeffs[0].cos(r);
    }

    @Override
    public Element tan(Ring r) {
        return (!isItNumber()) ? new F(F.TG, new Element[] {this})
                : (isZero(r)) ? r.numberZERO
                : coeffs[0].tan(r);
    }

    @Override
    public Element ctg(Ring r) {
        return (!isItNumber()) ? new F(F.CTG, new Element[] {this})
                : (isZero(r)) ? r.numberZERO.ctg(r)
                : coeffs[0].ctg(r);
    }

    @Override
    public Element arcsn(Ring r) {
        return (!isItNumber()) ? new F(F.ARCSIN, new Element[] {this})
                : (isZero(r)) ? r.numberZERO.arcsn(r) : coeffs[0].arcsn(r);
    }

    @Override
    public Element arccs(Ring r) {
        return (!isItNumber()) ? new F(F.ARCCOS, new Element[] {this})
                : (isZero(r)) ? r.numberZERO.arccs(r) : coeffs[0].arccs(r);
    }

    @Override
    public Element arctn(Ring r) {
        return (!isItNumber()) ? new F(F.ARCTG, new Element[] {this})
                : (isZero(r)) ? r.numberZERO.arctn(r)
                : coeffs[0].arctn(r);
    }

    @Override
    public Element arcctn(Ring r) {
        return (!isItNumber()) ? new F(F.ARCCTG, new Element[] {this})
                : (isZero(r)) ? r.numberZERO.arcctn(r)
                : coeffs[0].arcctn(r);
    }

    @Override
    public Element sh(Ring r) {
        return (!isItNumber()) ? new F(F.SH, new Element[] {this})
                : (isZero(r)) ? r.numberZERO
                : coeffs[0].sh(r);
    }

    @Override
    public Element ch(Ring r) {
        return (!isItNumber()) ? new F(F.CH, new Element[] {this})
                : (isZero(r)) ? r.numberZERO.ch(r)
                : coeffs[0].ch(r);
    }

    @Override
    public Element th(Ring r) {
        return (!isItNumber()) ? new F(F.TH, new Element[] {this})
                : (isZero(r)) ? r.numberZERO
                : coeffs[0].th(r);
    }

    @Override
    public Element cth(Ring r) {
        return (!isItNumber()) ? new F(F.CTH, new Element[] {this})
                : (isZero(r)) ? r.numberONE().zero(r).
                cth(r) : coeffs[0].
                cth(r);
    }

    @Override
    public Element arsh(Ring r) {
        return (!isItNumber()) ? new F(F.ARCSH, new Element[] {this})
                : (isZero(r)) ? r.numberZERO : coeffs[0].arsh(r);
    }

    @Override
    public Element arch(Ring r) {
        return (!isItNumber()) ? new F(F.ARCCH, new Element[] {this})
                : (isZero(r)) ? r.numberONE.pi(r).divide(r.posConst[2], r)
                : coeffs[0].arch(r);
    }

    @Override
    public Element arth(Ring r) {
        return (!isItNumber()) ? new F(F.ARCTGH, new Element[] {this})
                : (isZero(r)) ? r.numberZERO
                : coeffs[0].arth(r);
    }

    @Override
    public Element arcth(Ring r) {
        return (!isItNumber()) ? new F(F.ARCCTGH, new Element[] {this})
                : (isZero(r)) ? r.numberONE.pi(r).divide(r.posConst[2], r) : coeffs[0].arcth(r);
    }

    public Element unitstep() {
        return null;
    }

    public Element unitstep(Element a) {
        return null;
    }


    @Override
    public double doubleValue() {
        return (coeffs.length == 0) ? 0 : constantTerm(new Ring("Z64[]")).doubleValue();
    }

    @Override
    public long longValue() {
        return (coeffs.length == 0) ? 0 : constantTerm(new Ring("R64[]")).longValue();
    }

    /**
     * Get the constant term of this polynomial
     *
     * @param ring ring
     *
     * @return Constant term of this polynomial
     */
    public Element constantTerm(Ring ring) {
        int i = powers.length;
        int j = coeffs.length;
        if (j == 0) {
            return ring.numberZERO();
        }
        int var = i / j;
        for (int k = i - var; k < i; k++) {
            if (powers[k] != 0) {
                return ring.numberZERO();
            }
        }
        return coeffs[j - 1];
    }
    
    /** Get Last Monomial of this polynomial
     * 
     * @param ring
     * @return last monomial, as new polynomial
     */
    public Polynom getLastMonomial() {
        int i = powers.length;
        int j = coeffs.length;
        int var=i/j;
        if (j == 0) return Polynom.polynomZero;
        int k = i - 1;
        for (; k > 0; k--) { if (powers[k] != 0) {break;}}
        if (k<i-var)return Polynom.polynomFromNot0Number(coeffs[j-1]);
        else{int ll=k-i+var+1;int[] mm=new int[ll];System.arraycopy(powers,k, mm, 0, ll);
        return new Polynom (mm, new Element[]{coeffs[j-1]});  }
    }

    /**
     * Get the constant term of this polynomial or NumberR64.ZERO if zero
     * constant term
     *
     * @param ring ring
     *
     * @return Constant term of this polynomial
     */
    public Element constantTerm() {
        return constantTerm(Ring.ringR64xyzt);
    }
//    public boolean isEven() {
//        return constantTerm(Ring.ringR64xyzt).isEven();
//    }
    /**
     * The number is equals the constant term of polynomial, which type is
     * changed to "newType". For ZERO polynomial the number is equal
     * NumberZ.ZERO.
     *
     * @return constant term of polynomial
     */
    @Override
    public Element toNumber(int newType, Ring ring) {
        return constantTerm(ring).toNumber(newType, ring);
    }

//    @Override
//    public Element toNumber(Ring ring) {
//        return constantTerm(ring);
//    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toPolynom(Algebra, r);
    }

    @Override
    public Element toNewRing(Ring r) {
        return toPolynom(r.algebra[0], r);
    }
    /**
     * метод, который делит полином на var подполиномов, минимально отличающихся
     * по длине
     *
     * @param p1 - количество полученных подполиномов
     *
     * @return - массив, содержащий все q подполиномов
     */
    public Polynom[] Sub_n_polynoms(int n) {
        Polynom[] p1 = new Polynom[n];

        int k = coeffs.length / n;//длина подполинома
        int r = coeffs.length - n * k;// r первых подполиномов будут на 1 моном длинее, чем остальные подполиномы

        int i = 0, j = 0;
        while (i < coeffs.length) {
            p1[j] = j < r ? this.subPolynom1(i, i + k + 1) : this.subPolynom1(i, i + k);
            i = j < r ? i + k + 1 : i + k;
            j++;
        }
        return p1;
    }

    /**
     * Averina метод, который делит полином на q подполиномов, минимально
     * отличающихся по длинне не равномерно делит!!!
     *
     * @param q - количество полученных подполиномов
     *
     * @return - массив, содержащий все q подполиномов
     */
    public Polynom[] Sub_n_polynoms1(int n) {
        Polynom[] p1 = new Polynom[n];

        SubsetZ sub = new SubsetZ(new int[] {0, this.coeffs.length});
        SubsetZ[] numS1 = sub.divideOnParts(n);

        for (int i = 0; i < numS1.length; i++) {
            int y;
            if (i == numS1.length - 1) {
                y = numS1[i].toArray()[1];
            } else {
                y = numS1[i].toArray()[1] + 1;
            }
            p1[i] = this.subPolynom(numS1[i].toArray()[0], y);
        }
        return p1;
    }

    /**
     * Averina метод сравнивающий исходный полином и p2 согласно
     * лексикографическому упорядочению
     *
     * @param p2 - полином, с которым сравниваем
     *
     * @return - 0 если равны,1 если р1>р2,-1 если p1<p2
     */
    public int compare_lex_polynoms(Polynom p2, Ring ring) {
        int more = 1;
        int less = -1;
        int ret = 0;
        int[] pow_p1 = this.powers;
        int[] pow_p2 = p2.powers;
        int c;
        if (pow_p1.length > pow_p2.length) {
            ret = 1;
        }
        if (pow_p1.length < pow_p2.length) {
            c = pow_p1.length;
            ret = -1;
        } else {
            c = pow_p2.length;
        }
        for (int i = 0; i < c; i++) {
            if (pow_p1[i] > pow_p2[i]) {
                return more;
            }
            if (pow_p1[i] < pow_p2[i]) {
                return less;
            }
        }
        if (ret == 0) {
            Element[] coef_p1 = this.coeffs;
            Element[] coef_p2 = p2.coeffs;
            for (int i = 0; i < c; i++) {
                if (coef_p1[i].compareTo(coef_p2[i], 2, ring)) {
                    return more;
                }
                if (!coef_p1[i].compareTo(coef_p2[i], 1, ring)) {
                    return less;
                }
            }
        }
        return ret;
    }

    /**
     * Averina метод возвращающий последние мономы при делении исходного
     * полинома на var частей
     *
     * @param var - на сколько частей мы разбиваем полином
     *
     * @return массив последних мономов при разбиении
     */
    public Polynom[] last_sub_polynoms(int n) {
        Polynom[] n1 = this.Sub_n_polynoms(n);
        for (int i = 0; i < n1.length; i++) {
            n1[i] = n1[i].
                    subPolynom1(n1[i].coeffs.length - 1, n1[i].coeffs.length);
        }
        return n1;
    }

    /**
     * метод, делящий на части полином, относительно разбиения другого полинома
     * mons - массив мономов,которые определяют границу разбиения полинома
     * return - массив состоящий из частей исходного полинома, согласно нужному
     * разделению
     */
    public Polynom[] sub_monom_pol(Polynom[] mons, Ring ring) {
        Polynom[] res = new Polynom[mons.length];
        Polynom[] p1 = this.Sub_n_polynoms(this.coeffs.length);//здесь содержатся отдельные мономы полинома this
        int i = 0, j = 0, beg = j, end;
        //beg - начало текущей части this-полинома, end - конец
        Polynom temp = mons[i];
        //младший терм текущего полинома из  mons
        Term lastmon = new Term(temp.subPolynom(temp.coeffs.length - 1, temp.coeffs.length).powers);

        for (i = 0; i < mons.length - 1; i++) {
            //берем моном из this-полинома
            Term thisterm = new Term(subPolynom1(j, j + 1).powers);

            while (j < coeffs.length - 1 && thisterm.compareTo(lastmon) != -1) {
                j++;
                thisterm = new Term(subPolynom1(j, j + 1).powers);
            }
            end = j;
            //если не оказалось ни одного монома в части
            if (beg == end) {
                res[i] = polynomZero;
            } //иначе копируем часть полинома от [beg до end)
            else {
                res[i] = subPolynom(beg, end);
                beg = end;
            }
            temp = mons[i + 1];
            lastmon = new Term(temp.subPolynom(temp.coeffs.length - 1, temp.coeffs.length).powers);
        }
        // копируем последнюю часть
        res[i] = j < coeffs.length ? subPolynom(beg, coeffs.length) : polynomZero;
        return res;
    }

    /**
     * Процедура восстановления (Ньютон) полинома многих переменных по его
     * остаткам rem - числам или полиномам многих переменных без той переменной,
     * по которой идет восстановление, и по линейным модулям x-b, x-(b+1),
     * x-(b+2),...,x-(b+rem.length+1). (всего модулей rem.length)
     *
     * @param rem остатки,
     * @param b первый модуль x-b,
     * @param s номер переменной по которой восстанавливаем,
     * @param p 28-ми битный числовой модуль, т.к.ю все вычисления проходят в
     * Zp32 по модулю p,
     * @param ring кольцо. Должно быть Zp32[x1,x2,...,xs,...].
     *
     * @return Polynom[] 0: полином f(x1,x2,...,xs): f mod x-(b+i) = rem[i]; 1:
     * произведение модулей
     * m(xs)=(x-b)(x-(b+1))(x-(b+2))***(x-(b+rem.length+1)).
     *
     */
    public static Polynom[] recoveryOfLin(Element[] rem, int b, int s, long p, Ring ring) {
        ring.MOD32 = p;
        if (rem.length == 0) {
            return null;
        }
        if (rem.length == 1) {
            Element[] coeff = new Element[] {ring.numberONE, new NumberZp32(-b)};
            int pow[] = new int[(s << 1) + 2];
            pow[s] = 1;
            return new Polynom[] {new Polynom(rem[0]), new Polynom(pow, coeff)};
        }
        Polynom c = rem[0] instanceof Polynom ? (Polynom) rem[0] : new Polynom(rem[0]);
        Polynom cm = rem[0] instanceof Polynom ? (Polynom) rem[0] : new Polynom(rem[0]);
        Polynom pm = new Polynom(ring.numberONE);//произведение модулей m(xs)
        //m_0
        Element[] coeff = new Element[] {ring.numberONE, new NumberZp32(-b)};
        int pow[] = new int[(s << 1) + 2];
        pow[s] = 1;
        Polynom mi = new Polynom(pow, coeff);//m_0
        Element[] val = new Polynom[s + 1];
        NumberZp32[] coe = new NumberZp32[1];
        coe[0] = NumberZp32.ONE;

        for (int i = 0; i < s; i++) {
            int[] po = new int[s];
            po[i] = 1;
            val[i] = new Polynom(po, coe);
        }
        for (int i = 1; i < rem.length; i++) {
            //m_0*m_1*...*m_(i-1)
            pm = pm.multiply(mi, ring);

            //n_0i*n_1i***n_(i-1)i
            NumberZp32 prnij = NumberZp32.ONE;
            NumberZp32 nij;
            for (int j = 0; j < i; j++) {
                nij = (new NumberZp32(i - j)).inverse(ring);
                prnij = prnij.multiply(nij, ring);
            }

            Polynom r = rem[i] instanceof Polynom ? (Polynom) rem[i] : new Polynom(rem[i]);
            Polynom sub = r.subtract(cm, ring);
            c = c.
                    add((sub.multiply(pm, ring).
                            multiply(new Polynom(prnij), ring)), ring);

            //m_i
            coeff = new Element[] {ring.numberONE, new NumberZp32(-b - i)};
            mi = new Polynom(pow, coeff);

            //cm = c mod (m_(i+1));
            if (c.powers.length == 0 || c.powers.length / c.coeffs.length < s) {
                cm = c;
            } else {
                val[s] = new Polynom(new NumberZp32(b + i + 1));
                cm = (Polynom) c.value(val, ring);
            }

        }

        pm = pm.multiply(mi, ring);
        if (c.powers.length / c.coeffs.length > s) {
            c = c.rem(pm, ring);
        }
        Polynom[] res = new Polynom[] {c, pm};
        return res;
    }

    /**
     * возвращает свободный член полинома одной переменной
     */
    public Element constantTermOneVar(Ring ring) {
        Element res;
        if (powers.length == 0) {
            if (coeffs.length == 0) {
                res = ring.numberZERO;
            } else {
                res = coeffs[ coeffs.length - 1];
            }
        } else {
            if (this.powers[this.powers.length - 1] == 0) {
                res = coeffs[ coeffs.length - 1];
            } else {
                res = ring.numberZERO;
            }
        }
        return res;
    }

    /**
     * Возвращает вектор-полином, т.е. полином, у которого каждый коэффициент -
     * это вектор из size компонент. Коэффициенты исходного полинома
     * записываются в var-той компоненте вектора, остальные коэффициенты -
     * нулевые
     *
     * @param ring - Ring
     * @param var - номер компоненты вектора, в которой записан числовой
     * коеффициент входного полинома для этого же монома
     * @param size - число компонент у векторов-коэффициентов нового полинома
     *
     * @return Polynom с коэффициентами типа VectorS размера size, все
     * коэффициенты нулевые, кроме var-ой координаты
     */
    public Polynom toVectorPolynom(Ring ring, int n, int size) {
        VectorS vv[] = new VectorS[coeffs.length];
        for (int i = 0; i < vv.length; i++) {
            vv[i] = new VectorS(size, ring.numberZERO);//NumberZp32.ZERO
            vv[i].V[n] = coeffs[i];
        }
        return new Polynom(powers, vv);
    }

    /**
     * массив полиномов записывается в виде одного векто-полинома, у которого
     * коэффициентами являются векторы VectorS с числом компонент равным числу
     * полиномов в массиве. Коэффициенты var-го полинома записываются в var-той
     * компоненте каждого вектора.
     *
     * @param ring Ring
     * @param pols - array of Polynomials
     *
     * @return one VectorPolynom
     */
    public Polynom toVectorPolynoms(Ring ring, Polynom[] pols) {
        int size = pols.length;
        Polynom S = pols[0].toVectorPolynom(ring, 0, size);
        for (int i = 1; i < size; i++) {
            S = S.add(pols[i].toVectorPolynom(ring, i, size), ring);
        }
        return S;
    }

    /**
     * Expands each monomial of this polynomial by adding new variable to make
     * homogeneous polynomial.
     *
     * @param varIdxToAdd index of variable to add.
     *
     * @return homogenized version of this polynomial.
     */
    public Polynom homogenize(int varIdxToAdd) {
        int termsCnt = coeffs.length;
        int newTermLen = varIdxToAdd + 1;
        int[] newPowers = new int[termsCnt * newTermLen];
        int termLen = getVarsNum();
        boolean isHomogeneous = true;
        int maxTermDegree = term(0).deg();
        int[] termsDegrees = new int[termsCnt];
        termsDegrees[0] = maxTermDegree;
        for (int i = 1; i < termsCnt; i++) {
            int currTermDegree = 0;
            for (int j = 0; j < termLen; j++) {
                currTermDegree += powers[i * termLen + j];
            }
            termsDegrees[i] = currTermDegree;
            if (isHomogeneous && maxTermDegree != currTermDegree) {
                isHomogeneous = false;
            }
            maxTermDegree = Math.max(maxTermDegree, currTermDegree);
        }
        for (int i = 0; i < termsCnt; i++) {
            // Copy tail from existing powers.
            System.arraycopy(powers, i * termLen,
                    newPowers, i * newTermLen + 1, termLen);
            // Add new variable to the first power of current monomial.
            newPowers[i * newTermLen] = maxTermDegree - termsDegrees[i];
        }
        return new Polynom(newPowers, coeffs);
    }

    /**
     * Dehomogenization of this polynomial by deleted the varIdxToRemove
     * variable. This function is inverse to homogenize function.
     *
     * @param varIdxToRemove index variable to remove.
     *
     * @return dehomogenized version of this polynomial.
     */
    public Polynom dehomogenize(int varIdxToRemove) {
        int termsCnt = coeffs.length;
        int termLen = getVarsNum();
        int[] newPowers = new int[termsCnt * (termLen - 1)];
        for (int i = 0; i < termsCnt; i++) {
            System.arraycopy(powers, i * termLen + 1,
                    newPowers, i * (termLen - 1), termLen - 1);
        }
        return new Polynom(newPowers, coeffs);
    }

    /**
     * Is this polynomial even?
     *
     * @return true if all coefficient is even and return false in other case.
     *
     */
    @Override
    public boolean isEven() {
        if (isItNumber()) {
            return (coeffs.length == 0) ? true : coeffs[0].isEven();
        }
        for (int i = 0; i < coeffs.length; i++) {
            if (!coeffs[i].isEven()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Coefficient of highest variable, when highest variable has number
     * "n_var". It is a number for the case of one variable polynomial and for
     * the other cases it is a polynomial with other variables then "n_var".
     *
     * @param n_var - the number of highest variable
     * @param ring Ring
     *
     * @return
     */
    public Polynom coefOfHightVar(int n_var, Ring ring) {
        int jj = powers.length / coeffs.length; // число переменных
        if (n_var >= jj || degree(n_var) <= 0) {
            return this;
        }
        // при некорректном запросе вернули весь полином
        int[][] el = new int[coeffs.length][jj];// матрица под все степени
        int k = 0;
        for (int i = 0; i < coeffs.length; i++) {
            System.arraycopy(powers, k, el[i], 0, jj);
            k += jj;
        }  // перебросили в эту матрицу весь массив степеней входного полинома
        int[] ind = new int[coeffs.length];
        int p = el[0][n_var];
        k = 1;
        ind[0] = 0;
        for (int i = 1; i < coeffs.length; i++) {
            if (p == el[i][n_var]) {
                ind[k] = i;
                k++;
            } else if (p < el[i][n_var]) {
                ind[0] = i;
                k = 1;
                p = el[i][n_var];
            }
        }// теперь известно, что старщее значение степени -- это p и оно встречается k раз
        // индекcы всех "встречь" находятся в ind[]
        int[] new_p = new int[k * jj]; // для массива степеней искомого полинома
        Element[] new_c = new Element[k];// для массива коэффициентов искомого полинома
        int j = 0;
        for (int i = 0; i < k; i++) {
            System.arraycopy(el[ind[i]], 0, new_p, j, jj);
            new_p[n_var] = 0;
            new_c[i] = coeffs[ind[i]];
            j += jj;
            n_var += jj;
        }
        Polynom res = new Polynom(new_p, new_c).ordering(ring).truncate();
        return res;
    }
    /**
     * Maximal power of each variable in this polynomial
     *
     * @param R Ring
     *
     * @return int-array which has the maximal degree of variable i at the
     * position i: for example: x~int[0], y~int[1], z~int[2].
     */
    public int[] maxPowOfVars__(Ring R) {
        int len = powers.length / coeffs.length;
        //Находим максимальные степени переменных полинома р, запишем их в массив maxPowersPol
        int maxPowersPol[] = new int[len];
        for (int i = 0; i < len - 1; i++) {
            for (int j = i; j < powers.length; j += len) {
                if (maxPowersPol[i] < powers[j]) {
                    maxPowersPol[i] = powers[j];
                }
            }
        }
        if (len!=0) maxPowersPol[len - 1] = powers[len - 1];
        return maxPowersPol;
    }

    /**
     * List of monomials which have such variables vars
     * We have to check does vars numbers greater than number of variables in this polynomial
     * @param R Ring
     *
     * @return int[]-  array-list of monomials which have such variables vars.
     */
    public int[] monomsWithVars(int[] vars) {if(coeffs.length==0)return new int[0];
        int len = powers.length / coeffs.length;
        int Nlen=0;
        for (int i = 0; i < vars.length; i++) {if(vars[i]>=len) Nlen++;}
        Nlen=vars.length-Nlen;
        if(Nlen==0)return new int[0];      
        int[] res= new int[coeffs.length];
        int k=0;
        for (int i = 0; i < coeffs.length; i++) {
            res[i]=0; // if will be not 0 then we take this monomial
            for (int j = 0; j < Nlen; j ++) {res[i]+=powers[k+vars[j]]; } 
            k+=len;
        }
        int numb=0;
        for (int i = 0; i < coeffs.length; i++) {if(res[i]!=0)numb++;}
        int[] monList= new int[numb]; k=0;
        for (int i = 0; i < coeffs.length; i++) {if(res[i]!=0)monList[k++]=i;}
        return monList ;
    }

    /**
     * Checking all the coefficients for number values.
     *
     * @return true - all coeffs are numbers false - if some coeff is not a
     * number
     */
    public boolean isNumberCoeffs() {
        for (int i = 0; i < coeffs.length; i++) {
            if (!coeffs[i].isItNumber()) {
                return false;
            }
        }
        return true;
    }

    public F convertToF(Ring ring) {
        if (isNumberCoeffs()) {return new F(F.ID, this);}
        Polynom[] mp = this.toMonomials(ring);
        int j = 0;
        Element[] mm = new Element[mp.length];
        Polynom pp = Polynom.polynomZero;
        for (int i = 0; i < mm.length; i++) {
            Element cf = mp[i].coeffs[0];
            if (!cf.isItNumber()) {
                cf = cf.Factor(false, ring).multiply(
                        new Polynom(mp[i].powers,
                                new Element[] {ring.numberONE}), ring);
                mm[j] = cf;
                j++;
            } else {
                pp.add(mp[i], ring);
            }
        }
        Element[] ef = (pp.isZero(ring)) ? new Element[j] : new Element[j + 1];
        System.arraycopy(mm, 0, ef, 0, j);
        if (!pp.isZero(ring)) {
            ef[j] = pp;
        }
        return new F(F.ADD, ef);
    }

    @Override
    public boolean isComplex(Ring ring) {
        for (Element coeff : coeffs) {
            if (coeff.isComplex(ring)) return true;
        }
        return false;
    }

    /** is Homogeneous?
     *  It returns true  if polynomial has the same total degree of its monomials.
     * @param ring
     * @return 
     */
    public boolean isHomogeneous() {if (coeffs.length==0) return true;
        int var = powers.length / coeffs.length; if (var<2) return true;
        int totalMonDegr=0;
        for (int i = 0; i < var; i++) totalMonDegr+=powers[i]; int add=var;
        for (int i = 1; i < coeffs.length; i++) { int temp=0;
             for (int j = 0; j < var; j++) 
                 temp+=powers[j+add];
             if (temp!=totalMonDegr) return false; add+=var;
        }
        return true;
    }   
    
    
    
        /**
     * List Of Monomials With Defined Variables.
     *   Result is empty list if there no such variables in this polynomial
     *  @varNums int[] Variables which we want to check and we want return
     *      the list of monomials with these variables
     * @param R Ring
     *
     * @return int-list which has these monomials  
     *  : for example, for pol= z^3+xy+x and varNums=[1], i.t."y":
     *   we get  result= [1].i.e. the monomial number 1 (in the middle)
     */
    public int[] listOfMonomWithVariabls(int[] varNums, Ring R) {
        int cfl=coeffs.length;
        int len = powers.length / cfl; int vNN=varNums.length;
        int[] res = new int[cfl]; int posRes=0,cfNumb=0;
        for (int pPow = 0; pPow < powers.length; pPow += len) {
                for (int j = 0; j < vNN; j++) {                
                  if (powers[pPow+varNums[j]]!=0) {res[posRes]=cfNumb; posRes++;break;}
                } cfNumb++;
            }
       
        if (posRes==cfl) return res;
        int[] ress= new int[posRes];
        System.arraycopy(res, 0, ress, 0, posRes); return ress;
    }

     /**
     * Разложение двучлена на множители. Каждая переменная, которая
     * присутствует в обоих членах образует новый полином-сомножитель состоящий
     * из этой переменной в максимальной степени и коэффициентом 1.
     * Результат - это произведение полиномов, соответствующее входному
     * двучлену - имеет тип factorPol
     *
     * @param ff input polynomial with 2 monomials
     * @param ring -- Ring
     *
     * @return polynomials which are written in FactorPol and
     *          equal input polynomial
     * Example: 3x^5y^6z+7x^3y^7u --> (x^3)(y^6)(3x^2z+ 7yu)
     */
    public FactorPol factorizationOfTwoMonoms(Ring ring) {
        int[] ppow = new int[this.powers.length];
        System.arraycopy(this.powers, 0, ppow, 0, this.powers.length);
        ArrayList<Polynom> mas = new ArrayList<Polynom>();
        int vars = this.powers.length / this.coeffs.length;
        for (int i = 0; i < vars; i++) {
            int[] pow = new int[vars];
            if (ppow[i] != 0 & ppow[i + vars] != 0) {
                if (ppow[i] < ppow[i + vars]) {
                    pow[i] = ppow[i];
                    ppow[i + vars] -= ppow[i];
                    ppow[i] = 0;
                } else {
                    pow[i] = ppow[i + vars];
                    ppow[i] -= ppow[i + vars];
                    ppow[i + vars] = 0;
                }
                mas.add(new Polynom(pow, new Element[] {NumberZ.ONE}));
            }
        }
        mas.add(new Polynom(ppow, this.coeffs));
        int[] power = new int[mas.size()];
        for (int i = 0; i < mas.size(); i++) {
            power[i] = 1;
        }
        Polynom[] mul = new Polynom[mas.size()];
        mul = mas.toArray(mul);
        return new FactorPol(power, mul);
    }
    
    /**
     * Процедура, возвращающая   НОД двух polynomials a=this и b:
     * и порождающие идеала (x и y), такие, что НОД(a,b) = a x + b y; 
     * @param b
     * @param ring -- полиномы над полем, например Zp32  
     * @return {НОД(a,b), x,  y}
     */
    public Polynom[] extendedGCDinEDx(Polynom bb, Ring Fiield_QorZp) {Polynom b=bb;
    Element one=Fiield_QorZp.numberONE;    Polynom a=this;  
    int invFlag=bb.compareTo(a,Fiield_QorZp); // if >=0 then do nothing
   if(invFlag<0){a=bb; b=this;}
   Polynom x=Polynom.polynomZero, y=Polynom.polynomFromNot0Number(one),
   u=Polynom.polynomFromNot0Number(one), v = Polynom.polynomZero;
    while (!a.isZero(Fiield_QorZp)){
       // q, r = b//a, b%a
       // m, n = x-u*q, y-v*q
       // b,a, x,y, u,v = a,r, u,v, m,n}
        Polynom[] qr=b.divAndRemRQZpx(a, Fiield_QorZp) ;
        Polynom m=x.subtract(u.multiply(qr[0], Fiield_QorZp), Fiield_QorZp);
        Polynom n=y.subtract(v.multiply(qr[0], Fiield_QorZp), Fiield_QorZp);
        b=a;         a=qr[1];
        x=u; y=v;        u=m;        v=n;
    }
         Polynom[] res= (b.isNegative())?  
            new Polynom[]{(Polynom)b.negate(Fiield_QorZp),(Polynom) x.negate(Fiield_QorZp),
                (Polynom) y.negate(Fiield_QorZp)}: new Polynom[]{b, x, y};
         if(res[0].isItNumber()){
           Element bbb=res[0].coeffs[0];
           if(!bbb.subtract(one,Fiield_QorZp).isZero(Fiield_QorZp)){
            Element bi=bbb.inverse(Fiield_QorZp);
            res[0]=Polynom.polynomFromNot0Number(one);// (Polynom)res[0].multiplyByNumber(bi, Fiield_QorZp).Mod(Fiield_QorZp); 
            res[1]=(Polynom)res[1].multiplyByNumber(bi, Fiield_QorZp);//.Mod(Fiield_QorZp); 
            res[2]=(Polynom)res[2].multiplyByNumber(bi, Fiield_QorZp);//.Mod(Fiield_QorZp);         
         }}
      if (invFlag<0) {Polynom tmp=res[1]; res[1]=res[2]; res[2]=tmp;}
      if  ((Ring.Zp32 == Fiield_QorZp.algebra[0])||(Ring.Zp== Fiield_QorZp.algebra[0])) 
        {res[0]=res[0].Mod(Fiield_QorZp);res[1]=res[1].Mod(Fiield_QorZp);res[2]=res[2].Mod(Fiield_QorZp);}
      return (res[0].isNegative())?      
        new Polynom[]{(Polynom)res[0].negateThis(Fiield_QorZp),(Polynom)res[1].negateThis(Fiield_QorZp),(Polynom)res[2].negateThis(Fiield_QorZp)}
        : res; 
    
    }
    
   /**
     * При вычислении аргумента используем следующие правила
     *            _
     *           | arctg(im/re),     re>0;
     *           | arctg(im/re)+\pi, re<0, im>0;
     *           | arctg(im/re)-\pi, re<0 , im<0;
     * arg (Z) =<  \pi/2 ,           re=0 , im>0;
     *           | -\pi/2,           re=0 , im<0;
     *           | 0,                re>0, im=0;
     *           | \pi,              re<0, im=0;
     *           | NaN               re=0, im=0;
     *            -
     *
     * @param ring -   кольцо
     * @return аргумент комплексного числа Z=re+\i*im
     */
    @Override
    public Element arg(Ring ring) {
        if (isZero(ring)) return Element.NAN;
      Polynom Re=Re(ring); Polynom Im=Im(ring);
        if (Re.isZero(ring)) {
            Element piDiv2 = Im.coeffs[0].pi(ring).divide(ring.posConst[2], ring);
            return (Im.isNegative()) ?  piDiv2.negate(ring): piDiv2 ;
        }
        if (Re.isNegative()) {
            return (Im.isZero(ring)) ? Re.coeffs[0].pi(ring) : (Im.isNegative())
                  ? Im.divide(Re, ring).arctn(ring).subtract(Re.coeffs[0].pi(ring), ring):
                    Im.divide(Re, ring).arctn(ring).add(Re.coeffs[0].pi(ring), ring) ;
        } 
        return (Im.isZero(ring)) ?  ring.numberZERO  : Im.divide(Re, ring).arctn(ring);
    }
    
    public static void main(String[] args){// throws Exception {
  //      Ring ring = new Ring("Q[x, u, v, w]");ring.se
//        F f1 = new F("\\sin(x)", ring);
//        F f2 = new F("\\sin(x+1)", ring);
//        System.out.println("ring = "+ring.CForm.newRing);
        
              Ring ring = new Ring("Z[x,y,z,t]");  //,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22,x23,x24,x25,x26,x27,x28,x29,x30]");
              ring.setFLOATPOS(8);
   //           ring.MOD32=29;          
   //    Polynom pol2 = new Polynom("49x^2+14x+1", ring);
   //     Polynom pol1 = new Polynom(" 7x+1", ring);  
       //  Polynom[] rr=  pol1.divAndRemRQZpx(pol2,    ring);      
     //  Polynom[]   isolated 
            //   Element rr=  pol1.GCD(pol2, ring);  //extendedGCDinEDx(pol2,    ring);
            
        int[] powers = new int[ 6];
        Element[] coeffs = new Element[ 6];
        for (int i = 0; i <  6; i++) {coeffs[i]=NumberZ.ONE; powers[i]=i+5;}
            
//        }
//        {new Fraction(ring.numberONE, new NumberZ("2")), new Fraction(ring.numberONE, new NumberZ("2"))};
//       // Element[] coeffs = new Element[]{ring.numberONE, ring.numberONE};
//        powers[7] = 1;
//        powers[22] = 1;
//        powers[29] = 1;
//        powers[45] = 1;
        
        Polynom p11 = new Polynom(powers, coeffs);
   //  FactorPol ffpp=   p.factor( ring);
        Polynom p = new Polynom("xy+y", ring); // Polynom p1=  new Polynom("3y^2x+3z^2x", ring); 
    //    p=p.multiply(p1, ring).multiply(p1, ring);
        System.out.println(p.toString(ring));        
               Element fp= p.FactorPol_SquareFree(ring);
      System.out.println(
              "   fp ="+fp.toString(ring));
 
}
}