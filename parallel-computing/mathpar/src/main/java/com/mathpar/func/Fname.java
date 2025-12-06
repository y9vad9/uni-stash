package com.mathpar.func;

import java.util.Arrays;
import com.mathpar.number.*;

/**
 *
 * @author Все все все)
 */
public class Fname extends Element {

    static final long serialVersionUID = 10000000000001L;

    public String name;
    /**
     * Поле аргуметов. X[0] - аргумент (возможно значение null -- это означает,
     * что значение еще не присвоено)
     */
    public Element[] X = null;
    /**
     * null, если индексы не используются
     *
     * [1] - нижние индексы (характерно для обозначения элементов матриц
     * векторов,тензоров а так же переменных с нижними индексами)
     *
     * [2] - верхние индексы (характерно для тензоров)
     *
     * [3] - индексы переменных из кольца (характерно для обозначения функций)
     */
    public Element[][] indices = new Element[3][];

    public Fname(String n) {
        name = n;
    }

    public Fname(String n, Element x) {
        name = n;
        X = new Element[] {x};
    }

    public Fname(String n, Element[] args) {
        this.name = n;
        this.X = new Element[args.length + 1];
        System.arraycopy(args, 0, this.X, 1, args.length);
    }

    public Fname(String n, Element[] args, int a) {
        this.name = n;
        this.X = args;
    }

    public void setX(Element x) {
        this.X = new Element[] {x};
    }

    public void setName(String n) {
        name = n;
    }

    @Override
    public String toString(Ring r) {
        if (X != null && X.length != 0 && X[0] != null) {
            return X[0].toString(r);
        } // то как было , только вкартце
        StringBuilder res = new StringBuilder(name);
        // Цепляем все индексы
        Element[] lowInd = lowerIndices();
        if (lowInd != null) {
            res.append("_{");
            for (int i = 0; i < lowInd.length; i++) {
                res.append(lowInd[i] == null ? "" : lowInd[i].toString(r)).append(", ");
            }
            res.delete(res.length() - 2, res.length()).append("}");
        }
        Element[] upInd = upperIndices();
        if (upInd != null) {
            res.append("^{");
            for (int i = 0; i < upInd.length; i++) {
                res.append(upInd[i] == null ? "" : upInd[i].toString(r)).append(", ");
            }
            res.delete(res.length() - 2, res.length()).append("}");
        }
        Element[] vars = varsIndices();
        if (vars != null) {
            res.append("(");
            for (int i = 0; i < vars.length; i++) {
                res.append(vars[i] == null ? "" : r.varNames[vars[i].intValue()]).append(", ");
            }
            res.delete(res.length() - 2, res.length()).append(")");
        }
        return res.toString();
    }

    @Override
    public String toString() {
        Ring r = Ring.ringR64xyzt;
        r.setDefaulRing();
        return toString(r);
    }

    @Override
    public Boolean isZero(Ring r) {
        return false;
    }

   /** isEmpty
    * returns "false" if this object points out to another object,
    * otherwise returns "true"
    * @param r
    * @return
    * Example of using in canonicForms:
    * Element el= (fn.isEmpty(RING))? substituteFname(fn):fn.X[0];
    */
    public Boolean isEmpty(Ring r) {
       if (X==null) return true;
       if (X[0]==null) return true;
        return false;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Fname) {
            return name.equals(((Fname) obj).name) && Arrays.equals(X, ((Fname) obj).X);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + Arrays.deepHashCode(this.X);
        return hash;
    }

    @Override
    public boolean equals(Element x, Ring r) {
        if (x instanceof Fname) {
            return name.equals(((Fname) x).name) && Array.equals(X, ((Fname) x).X, r);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Element o) {
        return compareTo(o, null);
    }

    @Override
    public int compareTo(Element x, Ring r) {
        if (x instanceof Fname) {
            int comp = name.compareTo(((Fname) x).name);
            if (X == null || ((Fname) x).X == null) {
                return comp;
            }
            if (X.length >= 2 && ((Fname) x).X.length >= 2) {
                return X[1].compareTo(((Fname) x).X[1], r);
            }
            return comp;
        } else {
            return -1000;
        }
    }

    @Override
    public int numbElementType() {
        return Ring.Fname;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return this;
    }

    @Override
    public Boolean isNegative() {
        return false;
    }

    @Override
    public F negate(Ring ring) {
        return new F(F.MULTIPLY, new Element[] {ring.numberMINUS_ONE, this});
    }

    @Override
    public Element toNumber(int numberType, Ring ring) {
        if (X == null) {
            return null;
        } else {
            return X[0].toNumber(numberType, ring);
        }
    }

    @Override
    public Boolean isOne(Ring ring) {
        return false;
    }

    @Override
    public Element add(Element f, Ring ring) {
        return (f.isZero(ring)) ? this : new F(F.ADD, new Element[] {this, f});
    }

    @Override
    public Element subtract(Element f, Ring ring) {
        return (f.isZero(ring)) ? this : new F(F.SUBTRACT, new Element[] {this, f});
    }

    @Override
    public Element multiply(Element f, Ring ring) {
        if (f.isZero(ring)) {
            return ring.numberZERO;
        }
        return (f.isOne(ring)) ? this : new F(F.MULTIPLY, new Element[] {this, f});
    }

    @Override
    public Element sin(Ring ring) {
        return (name.equals("\\pi")) ? Fraction.Z_ONE.sin(ring) : null;
    }

    @Override
    public Element cos(Ring ring) {
        return (name.equals("\\pi")) ? Fraction.Z_ONE.cos(ring) : null;
    }

    @Override
    public Element tan(Ring ring) {
        return (name.equals("\\pi")) ? Fraction.Z_ONE.tan(ring) : null;
    }

    @Override
    public Element ctg(Ring ring) {
        return (name.equals("\\pi")) ? Fraction.Z_ONE.ctg(ring) : null;
    }

    @Override
    public Object clone() {
        return new Fname(this.name);
    }

    @Override
    public Element divide(Element f, Ring ring) {
        if (f.isOne(ring)) {
            return this;
        }
        return new F(F.DIVIDE, new Element[] {this, f});
    }

    @Override
    public double doubleValue() {
        if ("\\pi".equals(name)) {
            return Math.PI;
        }
        if ("\\e".equals(name)) {
            return Math.E;
        }
        return Double.NaN;
    }

    @Override
    public Element D(Ring ring) {
        return D(0, ring);
    }

    @Override
    public Element D(int num, Ring ring) {
        Element[] vars = varsIndices();
        if (vars != null) {
            for (int i = 0; i < vars.length; i++) {
                if (vars[i].intValue() == num) {
                    return new F(F.D, new Element[] {this, new VectorS(new Element[] {ring.varPolynom[num]})});
                }
            }
        }
        return (X != null && X.length == 1 && X[0] != null) ? X[0].D(num, ring) : ring.numberZERO;
    }

    @Override
    public Element num(Ring ring) {
        if ((X != null) && (X[0] != null) && ((X[0] instanceof Fraction) || (X[0] instanceof F))) {
            return X[0].num(ring);
        }
        return this;
    }

    @Override
    public Element denom(Ring ring) {
        if ((X != null) && (X[0] != null) && ((X[0] instanceof Fraction) || (X[0] instanceof F))) {
            return X[0].denom(ring);
        }
        return ring.numberONE;
    }

    @Override
    public boolean isItNumber(Ring ring) {
        if ((name.equals("\\pi"))||(name.equals("\\e"))||(!name.equals("\\i"))) return true;
        if  (((X==null)||(X[0]==null)))return false;
        return X[0].isItNumber(ring);
    }
    
    @Override
    public boolean isItNumber() {
        if ((X==null)||(X[0]==null))return false;
        if (!X[0].isItNumber())return false;           
        return true;
    }
    @Override
    public Element pow(int n, Ring r) {
        return new F(F.intPOW, new Element[] {this, r.numberONE.valOf(n, r)});
    }

    /**
     * @return нижние индексы (характерно для обозначения элементов матриц
     * векторов,тензоров а так же переменных с нижними индексами), {@code null},
     * если отсутсвуют.
     */
    public Element[] lowerIndices() {
        return (indices != null && indices.length > 0)? indices[0] : null;
    }

    /**
     * @return верхние индексы (характерно для тензоров), {@code null}, если
     * отсутсвуют.
     */
    public Element[] upperIndices() {
        return (indices != null && indices.length > 1)? indices[1] : null;
    }

    /**
     * @return индексы переменных из кольца (характерно для обозначения
     * функций), {@code null}, если отсутсвуют.
     */
    public Element[] varsIndices() {
        return (indices != null && indices.length >= 3 && indices[2] != null)
                ? indices[2] : null;
    }

    @Override
    public Element value(Element[] var, Ring r) {
        if (X != null && X.length > 0 && X[0] != null) {
            return new Fname(name, X[0].value(var, r));
        }
        return this;
    }
                /**
    *  Замена порядка переменных во всех полиномах этого элемента
    * @param varsMap - правило замены порядка переменных
    * @param flag   - куда/откуда
    * @param ring    - Ring
    * @return - результат замены порядка переменныхво всех полиномах
    */
    @Override
    public Element changeOrderOfVars(int[] varsMap, boolean flag, Ring ring){
      if((X!=null)&&(X[0]!=null))
               X[0]=  X[0].changeOrderOfVars(varsMap, flag, ring);
      return this;
    }

    @Override
    public Element simplify(Ring ring){
    return(X!=null && X.length>0 && X[0]!=null)? X[0].simplify(ring) : this;
    }
}
