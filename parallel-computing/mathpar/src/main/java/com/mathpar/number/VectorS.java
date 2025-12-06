package com.mathpar.number;
import com.mathpar.func.F;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import java.util.ArrayList;
import java.util.Random;

/**
 * Класс векторов типа Element.
 *   Cодержит методы вектороной арифметики.
 * <p>Title: Class VectorS of number package</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ParCA Tambov </p>
 * @author ParCA Tambov
 * @version 3.0
 */
public class VectorS extends Element  {
    public static final long serialVersionUID = 1L;

    /** массив коэффициентов вектора */
    public Element V[];
     /** 
     * Признак виртуального вектора полиномов или столбца:
     * fl =0 простая строка, 
     * fl>0 виртуальная строка
     * fl=-1 простой столбец
     * fl<-1 виртуальный столбец
     */
    public int fl = 0;
    public VectorS() {
    }

    public VectorS(int n) { V = new Element[n]; }
    public VectorS(VectorS VV, int ffl) { V =VV.V; fl=ffl; }
    public VectorS(F el) {V = el.X.clone();}
    public VectorS(ArrayList<Element> a, int ffl) {fl=ffl;
        Element[] nn = new Element[a.size()];
        a.toArray(nn);
        V = nn;
    }
    public void setFlag(int flag){fl=flag;};
    /** Создание нулевого вектора, содержащего n компонент.
     * @param n типа int, длина вектора <br>
     * <b> Пример использования </b> <br>
     * <CODE> import scalar.VectorS; <br>
     * class Example{ <br>
     * <ul> public static void main(String args[]){
     * <ul>  VectorS vect = new VectorS(5, NumberZ.ZERO); </ul>
     * } </ul> } <br>  </CODE>
     * В этом примере инициализируется 5-компонентный нулевой вектор <tt>vect</tt> c
     * элементами NumberZ.ZERO.
     */
    //______________________________________________________________________________
    public VectorS(int n, Element zero) {
        V = new Element[n];
        for (int i = 0; i < n; i++) {
            V[i] = zero;
        }
    }

    public VectorS(int n, int fll, Ring ring) {
        V = new Element[n]; fl=fll;
    }
    
    public VectorS(int n, int ringType) {
        V = new Element[n];
        Element zero = Ring.oneOfType(ringType);
        for (int i = 0; i < n; i++) {
            V[i] = zero;
        }
    }

    public VectorS(int n, int density, int[] randomType, Random ran, Ring ring) {
        this(MatrixS.randomScalarArr2d(1, n, density, randomType, ran, ring)[0]);
    }

    /**
     * Length of the vector
     * @return vector length
     */
    public int length() {
        return V.length;
    }

    /**
     * Constructor from Element array
     * @param arr - vector elements
     */
    public VectorS(Element arr[], int fll) { V = arr; fl=fll;}
    public VectorS(Element arr[]) { V = arr;  }

        /**
     * Constructor from Element array
     * @param arr - vector elements
     */
    public VectorS(int arr[]) {
        Element[] ee=new Element[arr.length];
        for (int i = 0; i < arr.length; i++) {ee[i]=NumberZ.valueOf((long)arr[i]);} 
        V = ee;  }
    /**
     * Constructor from one Element el
     * @param el - vector elements
     */
    public VectorS(Element el) { V = new Element[]{el};  }
     
    /**
     * Constructor from two Elements  
     * @param el -   element first
     * @param el2 -   element second
     */    
    public VectorS(Element el, Element el2) { V = new Element[]{el,el2};  }
    /**
     * Constructor from long array
     * @param one - one of needed Element type
     * @param arr - long array
     */
    public VectorS(long arr[], Ring ring) {
        Element one = ring.numberONE();
        Element[] sa = new Element[arr.length];
        for (int i = 0; i < arr.length; i++) {
            sa[i] = one.valOf(arr[i], ring);
        }
        V = sa;
    }

    public boolean isEqual(VectorS b, Ring ring) {
        int len = length();
        if (len != b.length()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (V[i].compareTo(b.V[i], ring) != 0) {
                return false;
            }
        }
        return true;
    }

    /** Является ли вектор нулевым?
     *  @return <tt> (this == 0)? </tt>
     */
    public boolean isZERO(Ring ring) {
        for (int i = 0; i < V.length; i++) {
            if (!V[i].isZero(ring)) {
                return false;
            }
        }
        return true;
    }

    /** Случайный вектор, c заданным числом компонент. Случайные числа лежат в диапазоне <tt>(0; p-1)</tt>
     * @param len типа int,  длина вектора
     * @param p типа Element, простой модуль
     * @param r1 типа java.util.Random, экземпляр класса Random<br>
     * <b> Пример использования </b> <br>
     * <CODE> import matrix.VectorS; <br>
     * class Example{ <br>
     * <ul> public static void main(String args[]){
     * <ul> int len = 10; <br>
     * Element p = 17; <br>
     *  VectorS vect = new VectorS(len, 43); </ul>
     * } </ul> } <br>  </CODE>
     * В этом примере инициализируется 10-компонентный случайный вектор <tt>vect</tt> по модулю <tt>17</tt>.
     *
    //______________________________________________________________________________

    public  VectorS(int len,Element p, java.util.Random r1){
    V=new Element[len];
    for(int i=0;i<len;i++){
    V[i] =r1.nextLong()%p;
    if(V[i] < 0) V[i] += p;}
    }
     */
    /**
     * String form of VectorS
     * @return -- String form of VectorS
     */
    @Override
    public String toString() {
        if(V.length == 0) return new String("[]");
      //  if(V.length == 1) return V[0].toString(r);
        String S = "[";
        for (int i = 0; i < V.length - 1; i++)  S+= V[i].toString() + ", ";
        S+=V[V.length - 1].toString() + "]";
        if(fl<0)S+="^{T}";
        return S;
    }

    @Override
    public String toString(Ring r) {
        if(V.length == 0) return  "[]";
      //  if(V.length == 1) return V[0].toString(r);
        String S = "[";
        for (int i = 0; i < V.length - 1; i++)
            S+= ((V[i]==null)? "null": V[i].toString(r)) + ", ";
        S+=((V[V.length - 1]==null)? "null": V[V.length - 1].toString(r))  + "]";
       return (fl<0)?  S+"^T ": S;
    }

    /** Вектор преобразуется к текстовому формату в виде столбца.
     *  В выходном объекте String содержатся элементы вектора и символы разрыва строки.
     */
    public String toStringColumn() {
        String s = V[0] + "\n";
        for (int i = 1; i < V.length; i++) {
            s += V[i] + "\n";
        }
        return s;
    }
 
    /**
     * Convert each element to other type of Element
     * @param ring -- ring of New  Type
     * @return  -- new VectorS each element of which has new scalar type
     */
    public VectorS toVectorS(int NewType, Ring ring) {
        VectorS s = new VectorS(V.length, NewType);
        for (int i = 0; i < V.length; i++) {
            s.V[i] = (V[i]).toNumber(NewType, ring);
        }
        return s;
    }
     /**
     * Transformation of VectorS  to array of integers
     * @return array of integers
     */
    public int[] toIntArray( ) {
        int k= V.length;
        int[] res=new int[k];
        for (int i = 0; i < k; i++)  res[i] =  V[i].intValue();
        return res;
    }

    @Override
    public Element add(Element x, Ring ring) {
        if (x instanceof VectorS) {
            return add((VectorS) x, ring);
        }
        return new F(F.ADD, new Element[]{this, x});
    }

    /** Сумма векторов
     * @param x типа VectorS, слагаемое
     * @return <tt> this + x </tt> <br>
     */
    public VectorS add(VectorS x, Ring ring) {
        int newLen=(x.length()>V.length)? V.length : x.length();
        VectorS z = new VectorS(newLen);
        Element[] ZZ = z.V;
        Element[] XX = x.V;
        for (int i = 0; i < newLen; i++) {
            ZZ[i] = V[i].add(XX[i], ring);
        }
        return z;
    }

    @Override
    public Element subtract(Element x, Ring ring) {
        if (x instanceof VectorS) {
            return subtract((VectorS) x, ring);
        }
        return new F(F.SUBTRACT, new Element[]{this, x});
    }

    /** Разность векторов VectorS .
     * @param x типа VectorS, вычитаемое
     * @return <tt> this - x </tt> <br>
     */
    public VectorS subtract(VectorS x, Ring ring) {
        int n = V.length > x.length() ? x.length() : V.length;
        VectorS z = new VectorS(n);
        Element[] ZZ = z.V;
        Element[] XX = x.V;
        for (int i = 0; i < n; i++) {
            ZZ[i] = V[i].subtract(XX[i], ring);
        }
        return z;
        }

    @Override
    public Element multiply(Element x, Ring ring) {int flX=0; int resFL=0;
        if (x instanceof MatrixD){MatrixD md=((MatrixD)x); flX=md.fl;
            resFL= (flX!=0)? 1: (fl==-1)? 0:1;
            return (fl>=0)?   new VectorS (multiply(md.M, ring), fl+flX):
                   (md.M.length==1)? new MatrixD(multiplyOuter(md.M[0], ring),resFL): null;}
        if (x instanceof VectorS){VectorS S=((VectorS)x); flX=S.fl;
            resFL= (flX!=0)? 1: (fl==-1)? 0:1;
            return ((flX>=0)&&(fl<0))? new MatrixD(multiplyOuter(S.V, ring), resFL):
                   ((flX<0)&&(fl>=0))?  multiply(S.V, ring): null;}
        int n = V.length;
        VectorS res = new VectorS(n,fl, ring); Element[] ZZ = res.V;
        for (int i = 0; i < n; i++)  ZZ[i] = V[i].multiply(x, ring);
        return res;
    }  
    
    /**
     * Процедура умножения вектора-строки на матрицу, заданную двумерным массивом
     * @param M двумерный массив скаляров -- правый матричный сомножитель вектора-строки
     * @return <tt>this*M</tt> типа VectorS
     */
    public VectorS multiply(Element[][] M, Ring ring) {
        int n = M[0].length;
        Element zero = ring.numberZERO;
        VectorS res = new VectorS(n, zero);
        Element[] ZZ = res.V;
        for (int i = 0; i < M.length; i++) {
            Element[] MM = M[i];
            Element temp = V[i];
            for (int j = 0; j < n; j++) 
                ZZ[j] = ZZ[j].add(MM[j].multiply(temp, ring), ring);
        }
        return res;
    }
  
       /**
     * Процедура умножения вектора-строки на вектор-столбец, как одномерный массив
     * @param C - Element[] вектор-сомножитель в скалярном произведении
     * @return <tt>this*M</tt> скаляр
     */
    public Element multiply(Element[] C, Ring ring) {
        int n = C.length;
        if (V.length!=n){ring.exception.append("Vectors with different lengthes."); return null;}
        Element zero = ring.numberZERO;
        for (int i = 0; i < n; i++) {
                zero = zero.add(V[i].multiply(C[i], ring), ring);
        }
        return zero;
    }
    
    /**
     * Процедура умножения матрицы перестановок типа <tt>E</tt> на вектор-столбец.
     * Матрица перестановок находится слева от вектор-столбеца, поэтому в названии - "EL".
     * @param Ei типа int[], список номеров строк матрицы, на которых стоят единичные элементы
     * @param Ej типа int[], список соответствующих столбцов.
     * @return <tt>E*this</tt> типа VectorS
     */
    public VectorS multEL(int[] Ei, int[] Ej, Ring ring) {
        int n = V.length;
        VectorS res = new VectorS(n, V[0].zero(ring));
        Element[] ZZ = res.V;
        for (int i = 0; i < Ei.length; i++) {
            ZZ[Ei[i]] = V[Ej[i]];
        }
        return res;
    }

  /** Внешнее произведение -- произведение вектора-столбца на вектор-строку.
     * @param b -- правый сомножитель-строка
     * @return <tt>this*b </tt> - матрица в виде двумерного массива
     */
    public Element[][] multiplyOuter(Element[] BB, Ring ring) {
        Element[][] res = new Element[V.length][BB.length];
        for (int i = 0; i < V.length; i++) {
            Element[] RR = res[i];
            Element temp = V[i];
            for (int j = 0; j < BB.length; j++) {
                RR[j] = temp.multiply(BB[j], ring);
            }
        }
        return res;
    }
    /** Внешнее произведение -- произведение вектора-столбца на вектор-строку.
     * @param b -- правый сомножитель-строка
     * @return <tt>this*b </tt> - матрица в виде двумерного массива
     */
    public Element[][] multiplyOuter(VectorS a, Ring ring) { 
     return   multiplyOuter(a.V,ring);
    }

    /** Деление вектора на скаляр
     * @param x типа Element, делитель
     * @return <tt> this / x </tt> <br>
     */
    @Override
    public VectorS divide(Element x, Ring ring) {
        int n = V.length;
        VectorS z = new VectorS(n);
        Element[] ZZ = z.V;
        for (int i = 0; i < n; i++) {
            ZZ[i] = V[i].divide(x, ring);
        }        return z;
    }

       @Override
    public VectorS divideToFraction(Element x, Ring ring) {
        int n = V.length;
        VectorS z = new VectorS(n);
        Element[] ZZ = z.V;
        for (int i = 0; i < n; i++) {
            ZZ[i] = V[i].divideToFraction(x, ring);
        }        return z;
    }

    /** Скалярный квадрат вектора.
     * @return <tt> this * this </tt> <br>
     */
    public Element innerSquare(Ring ring) {
        Element rez = V[0].multiply(V[0], ring);
        for (int i = 1; i < V.length; i++) {
            rez = rez.add(V[i].multiply(V[i], ring), ring);
        }
        return rez;
    }

    /** Приведение вектора по модулю <tt> p </tt> к интервалу <tt> [-(р-1)/2,+(р-1)/2] </tt>
     * @param p типа Element, модуль
     * @return <tt> this % p </tt> <br>
     */
    //______________________________________________________________________________
    @Override
    public VectorS Mod(Ring ring) {
        int n = V.length;
        VectorS z = new VectorS(n);
        Element[] ZZ = z.V;
        for (int i = 0; i < n; i++) {
            ZZ[i] = V[i].Mod(ring);
        }
        return z;
    }

    @Override
    public VectorS mod(Ring ring) {
        int n = V.length;
        VectorS z = new VectorS(n);
        Element[] ZZ = z.V;
        for (int i = 0; i < n; i++) {
            ZZ[i] = V[i].mod(ring);
        }
        return z;
    }

    /**
     * Процедура изменения знака компонент вектора
     * @return <tt>-this</tt> типа VectorS
     */
    @Override
    public VectorS negate(Ring ring) {
        Element[] res = new Element[V.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = V[i].negate(ring);
        }
        return new VectorS(res);
    }

    /**
     * Процедура создания матрицы-строки из данного вектора
     * @return  Матрица в виде двумерного массива скаляров
     */
    public Element[][] toRowMatrix() {
        Element[][] M = new Element[1][];
        M[0] = V;
        return M;
    }

    /**
     * Процедура создания матрицы-столбца из данного вектора.
     * @return Матрица в виде двумерного массива скаляров
     */
    public Element[][] toColMatrix() {
        Element[][] M = new Element[V.length][1];
        for (int i = 0; i < V.length; i++) {
            M[i][0] = V[i];
        }
        return M;
    }
//        public Element[][] psevdoInverse(Ring ring) {
//        Element[][] M = new Element[V.length][1];
//        for (int i = 0; i < V.length; i++) {
//            if(V[i].isZero(ring)) M[i][0]=ring.numberZERO;
//            else M[i][0] = V[i].inverse(ring);
//        }
//        return M;
//    }
//
//        public VectorS inverse1(Ring ring) {
//        VectorS w = new VectorS(this.V.length);
//        for (int i = 0; i < V.length; i++) {
//            if(V[i].isZero(ring)){
//                w.V[i]=ring.numberZERO;
//            }
//            else w.V[i] = V[i].inverse(ring);
//        }
//        return w;
//    }
    /**
     * Процедура создания матрицы-столбца из данного вектора.
     * @return VectorS
     */
    public VectorS transpose(Ring ring) {return new VectorS(V, (fl==0)?-1:(fl==-1)?0:(fl>0)?-2:1);}

//    public MatrixS psevdoInverse() {
//        Element[][] M = new Element[V.length][];int[][] c = new int[V.length][];
//        for (int i = 0; i < V.length; i++) {
//            if(V[i].isZero(Ring.defaultRing)){ M[i]=new Element[0];  c[i]=new int[0];}
//            else { M[i] = new Element[]{V[i].inverse(Ring.defaultRing)};  c[i]=new int[]{0}; }
//        }
//        return new MatrixS(V.length,1,M,c);
//    }
    public VectorS psevdoInverse(Ring ring) {
        Element[]  M = new Element[V.length];
        for (int i = 0; i < V.length; i++) {
            if(V[i].isZero(ring)) M[i]=V[i]; else M[i]=V[i].inverse(ring); 
        }
        return new VectorS(M,(fl==0)?-1:(fl==-1)?0:(fl>0)?-2:1 );
    }
    @Override
    public Boolean isZero(Ring ring) {
        int t = this.length();
        for (int i = 0; i < t; i++) {
            if (!(V[i].isZero(ring))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Element x, Ring ring) {
        if (Ring.VectorS != x.numbElementType()) {
            return false;
        }
        VectorS vv = (VectorS) x;
        int t = this.length();
        if (t != vv.length()) {
            return false;
        }
        for (int i = 0; i < t; i++) {
            if (!(V[i].equals(vv.V[i]))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean isNegative() {
        return true;
    }

    @Override
    public int numbElementType() {
        return Ring.VectorS;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        VectorS s = new VectorS(V.length, Algebra);
        for (int i = 0; i < V.length; i++) {
            s.V[i] = (V[i]).toNewRing(Algebra, r);
        }
        return s;
    }


    public int compareTo(Element x) {
        int xNum = x.numbElementType();
        if (xNum > Ring.VectorS) {
            return 1;
        }
        if (xNum < Ring.VectorS) {
            return -1;
        }
        VectorS X = (VectorS) x;
        if (V.length != X.V.length) {
            return (int) Math.signum(V.length - X.V.length);
        }
        for (int j = 0; j < V.length; j++) {
            int res = V[j].compareTo(X.V[j]);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        int xNum = x.numbElementType();
        if (xNum > Ring.VectorS) {
            return 1;
        }
        if (xNum < Ring.VectorS) {
            return -1;
        }
        VectorS X = (VectorS) x;
        if (V.length != X.V.length) {
            return (int) Math.signum(V.length - X.V.length);
        }
        for (int j = 0; j < V.length; j++) {
            int res = V[j].compareTo(X.V[j], ring);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

    @Override
    public Boolean isOne(Ring ring) {

        for (int i = 1; i < V.length; i++)
            if(!V[i].isZero(ring))return false;
        if(V[0].isOne(ring)) return true;
        return false;
    }
     @Override
    public Boolean isMinusOne(Ring ring) {

        for (int i = 1; i < V.length; i++)
            if(!V[i].isZero(ring))return false;
        if(V[0].isMinusOne(ring)) return true;
        return false;
    }

    @Override
    public Object clone(){
        VectorS temp = new VectorS();
        temp.V = new Element[this.V.length];
        for (int i =0; i< temp.V.length; i++){
            temp.V[i] = (Element) this.V[i].clone();
        }
        return temp;
    }

    @Override
    public Element expand(Ring ring) {
        for (int i = 0; i < V.length; i++) {
                V[i]= V[i].expand(ring);
            }
        return this;
    }

    @Override
    public Element integrate(Ring ring) {
        for (int i = 0; i < V.length; i++) {
                V[i]= V[i].integrate(ring);
            }
        return this;
    }

     @Override
    public Element integrate(int num, Ring ring) {
        for (int i = 0; i < V.length; i++) {
                V[i]= V[i].integrate(num,ring);
            }
        return this;
    }

    @Override
    public Element D(int num, Ring ring) {
      for (int i = 0; i < V.length; i++) {
                V[i]= V[i].D(ring).expand(ring);
            }
        return this;
    }
    @Override
    public Element factorLnExp(Ring ring) {
      for (int i = 0; i < V.length; i++) {
                V[i]= V[i].factorLnExp(ring);
            }
        return this;
    }

    @Override
    public Element expandLn(Ring ring) {
      for (int i = 0; i < V.length; i++) {
                V[i]= V[i].expandLn(ring);
            }
        return this;
    }

    @Override
    public Element factor(Ring ring) {
      for (int i = 0; i < V.length; i++) {
                V[i]= V[i].factor(ring);
            }
        return this;
    }

     @Override
    public Element ln(Ring r) {
        VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].ln(r);
        }
        return z;
    }

    @Override
    public Element lg(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].lg(r);
        }
        return z;
    }

    @Override
    public Element exp(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].exp(r);
        }
        return z;
    }

    @Override
    public Element sqrt(Ring r) {
        VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].sqrt(r);
        }
        return z;
    }

    @Override
    public Element sin(Ring r) {
        VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].sin(r);
        }
        return z;
    }

    @Override
    public Element cos(Ring r) {
        VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].cos(r);
        }
        return z;
    }

    @Override
    public Element tan(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].tan(r);
        }
        return z;
    }

    @Override
    public Element ctg(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].ctg(r);
        }
        return z;
    }

    @Override
    public Element arcsn(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].arcsn(r);
        }
        return z;
    }

    @Override
    public Element arccs(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].arccs(r);
        }
        return z;
    }

    @Override
    public Element arctn(Ring r) {
      VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].arctn(r);
        }
        return z;
    }

    @Override
    public Element arcctn(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].arcctn(r);
        }
        return z;
    }

    @Override
    public Element sh(Ring r) {
        VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].sh(r);
        }
        return z;
    }

    @Override
    public Element ch(Ring r) {
       VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].ch(r);
        }
        return z;
    }

    @Override
    public Element th(Ring r) {
        VectorS z = new VectorS(length());
        Element[] ZZ = z.V;
        for (int i = 0; i < length(); i++) {
            ZZ[i] = V[i].th(r);
        }
        return z;
    }
    /** Gcd Of Vector Elements.
     *
     * @param ring
     * @return
     */
        public Element gcdOfElements(Ring ring){
        return Element.arrayGCD(V,ring);
    }
//    @Override
//    public Element PI(Ring r) {return new NumberR64(Math.PI);}

    /**
     * Функция для вычисления функции cth(x) - гиперболического котангенса.
     * Используется представление данной функции через 1/th()
     *
     * @return ctg()
     */
    @Override
    public Element cth(Ring r) {
        return NumberR64.ONE.divide(th(r), r);
    }

    /**
     * Процедура вычисления Arsh(x) по формуле Arsh(x)=Ln(x+sqrt(x^2+1))
     */
    @Override
    public Element arsh(Ring r) {
        double x = this.value;
        return new NumberR64(Math.log(x + Math.sqrt(x * x + 1)));
    }

    /**
     * Процедура вычисления Arch(x) по формуле Ln(x+sqrt(x^2-1))
     */
    @Override
    public Element arch(Ring r) {
        double x = this.value;
        return new NumberR64(Math.log(x + Math.sqrt(x * x - 1)));
    }

    /**
     * Процедура вычисления Arth(x) по формуле Arth(x)=Ln((1+x)/(1-x))
     */
    @Override
    public Element arth(Ring r) {
        double x = this.value;
        return new NumberR64(Math.log((x + 1) / (1 - x)));
    }

    /**
     * Процедура вычисления Arcth(x) по формуле Arcth(x)=Ln((1+x)/(x-1))
     */
    @Override
    public Element arcth(Ring r) {
        double x = this.value;
        return new NumberR64(Math.log((x + 1) / (x - 1)));
    }


    public VectorS signum(Ring ring) {
       Element[] signumV=new Element[V.length];
       int signumEl;
       for(int i=0;i<V.length;i++){
           signumEl=V[i].signum();
           signumV[i]=(signumEl == 1) ? ring.numberONE : (signumEl==0) ? ring.numberZERO : ring.numberMINUS_ONE;
       }
       return new VectorS(signumV);
    }

    @Override
    public Element abs(Ring r) {
       Element[] absV=new Element[V.length];
       for(int i=0;i<V.length;i++){
           absV[i]=V[i].abs(r);
       }
       return new VectorS(absV);
    }
    @Override
    public boolean isComplex(Ring r) {
       for(int i=0;i<V.length;i++){
          if (V[i].isComplex(r)) return true;
       }
       return false;
    }
    @Override
    public boolean isItNumber(Ring r) {
       for(int i=0;i<V.length;i++){
          if (!V[i].isItNumber(r)) return false;
       }
       return true;
    }
        @Override
    public boolean isItNumber() {
       for(int i=0;i<V.length;i++){
          if (!V[i].isItNumber()) return false;
       }
       return true;
    }
    @Override
     public Element Factor(boolean doNewVector, Ring ring){
      Element[] absV=new Element[V.length];
       for(int i=0;i<V.length;i++){
           absV[i]=V[i].Factor(doNewVector, ring);
       }
       return new VectorS(absV);
    }

    @Override
    public Element Expand(Ring ring){
      Element[] absV=new Element[V.length];
       for(int i=0;i<V.length;i++){
           absV[i]=V[i].Expand(ring);
       }
       return new VectorS(absV);
    }

  
     @Override
    public Element value(Element[] point, Ring ring) {
      Element[] U=new Element[V.length];
       for(int i=0;i<V.length;i++){
           U[i]=V[i].value(point, ring);
       }
       return new VectorS(U);
    }
    
    @Override
    public Element closure(Ring ring){
    Element[] newV =V;
    newV[0]=ring.numberONE.subtract(V[0].inverse(ring), ring).inverse(ring);
    return new VectorS(newV);
    }

    public VectorS siftLeft( int s){
        Element[] vv =new Element[V.length];
        System.arraycopy(V, 0, vv, 0, V.length);
        Array.siftLeft(vv, s); return new VectorS(vv);}
    
    public VectorS siftRight( int s){
        Element[] vv =new Element[V.length];
        System.arraycopy(V, 0, vv, 0, V.length);
        Array.siftRight(vv, s); return new VectorS(vv);} 

/**
 * Comput Common Denominator for the elements of VectorS
 * 
 * @param frs VectorS with Fractions end not Fraction elements
 * @param ring  Ring
 * @return VectorS with new Element [V.length] == common denominator
 * and all other elements changed to new numerators
 */
    public static VectorS toCommonDenom(VectorS frs, Ring ring) {
        Element[] V=frs.V; Element[] nV= new Element[V.length+1];
        Element ComDen = NumberZ.ONE;
        for (int j = 0; j < V.length; j++) {  
            if(V[j] instanceof Fraction){
               ComDen=ComDen.LCM(((Fraction)V[j]).denom, ring); nV[j]=((Fraction)V[j]).num;}
            else nV[j]=V[j];
        } 
        if (!ComDen.isOne(ring)){
        for (int i = 0; i < V.length; i++) {  
            if(V[i] instanceof Fraction){
               nV[i]=nV[i].multiply(ComDen.divideExact(((Fraction)V[i]).denom, ring), ring);
             } else nV[i]=nV[i].multiply(ComDen, ring);
        }}
        nV[V.length]=ComDen;
        return new VectorS(nV);
    }
        /** Calculate the squared Euclidean norm of the entire vector 
     * or only a part of the vector, 
     * which will include all components starting from fromIndex,
     * 
     * @param fromIndex - From this Index to the last index
     * @param ring Ring
     * @return - the squared Euclidean norm
     */
    public Element norm2( int fromIndex, Ring ring) {
        Element norm2 = ring.numberZERO;
        for (int i = fromIndex; i < V.length; i++) {
            norm2 = norm2.add(V[i].multiply(V[i], ring), ring);
        } return norm2;
    }
    /** Calculate the squared Euclidean norm of the  vector 
     * @param ring Ring
     * @return - the squared Euclidean norm of this  vector 
     */
    public Element norm2( Ring ring) {
        Element norm2 = ring.numberZERO;
        for (int i = 0; i < V.length; i++) {
            norm2 = norm2.add(V[i].multiply(V[i], ring), ring);
        } return norm2;
    }
        /** Calculate the Euclidean norm  
     * @param ring Ring
     * @return - Euclidean norm of this  vector 
     */
    public Element norm( Ring ring) {
        return norm2(ring).sqrt(ring);
    }
}
// ################################################################################
//  Пересылка-прием
// ###############################################################################
/** Процедура приема вектора VectorS от данного процессора - отправителя с данным тэгом.
 * @param node типа int, номер процессора - отправителя
 * @param tag типа int, тэг отправителя
 * @return vect типа VectorS -- принятый вектор
 *
public static VectorS recv(int node, int tag) throws MPIException {
int[] serve = new int[1];
MPI.COMM_WORLD.Recv(serve, 0, 1, MPI.INT, node, tag);
Element[] res = new Element[serve[0]];
MPI.COMM_WORLD.Recv(res, 0, serve[0], MPI.LONG, node, tag);
return new VectorS(res);
}


/** Процедура пересылки вектора VectorS целиком на данный узел с данным тэгом
 * @param node типа int, номер процессора - получателя
 * @param tag типа int, тэг, передаваемый получателю
 *
public  void send(int node, int tag) throws
MPIException {
int[] serve = new int[1];
serve[0] = V.length;
MPI.COMM_WORLD.Send(serve, 0, 1, MPI.INT, node, tag);
MPI.COMM_WORLD.Send(V, 0, serve[0], MPI.LONG, node, tag);
}



/** Процедура пересылки части вектора VectorS на данный узел с данным тэгом.
 * Пересылается определенное количество элементов вектора, начиная с данной позиции
 * @param node типа int, номер процессора - получателя
 * @param tag типа int, тэг, передаваемый получателю
 * @param pos типа int, позиция начала
 * @param len типа int, количество пересылаемых элементов
 *
public void send(int node, int tag, int pos, int len) throws
MPIException {
int[] serve = new int[1];
serve[0] = len;
MPI.COMM_WORLD.Send(serve, 0, 1, MPI.INT, node, tag);
MPI.COMM_WORLD.Send(V, pos, serve[0], MPI.LONG, node, tag);

}

public void write(String filename)throws IOException{
MatrixWriter mw = new MatrixWriter(filename);
mw.write(V);
}

public static VectorS read(String filename) throws IOException{
MatrixReader mr = new MatrixReader(filename);
return new VectorS(mr.readL1());
}
 */
