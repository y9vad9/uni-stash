package com.mathpar.polynom;

import com.mathpar.func.F;
import com.mathpar.func.parser.Parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import com.mathpar.number.*;

/**
 * Free algebra with finite number of generators over ring. <br> <br>
 * Noncommutative generators of the group should begin with capital letters
 * (upper case). Symbol G denotes the group algebra. After it is a list of
 * generators, and before him - space in which a group. <br> Examples of free
 * group algebras: <br> <br> RING = Z [x, y] G [U, V]; (generators U, V) <br>
 * RING = R64 [u, v] G [A, B]; (generators A, B) <br> RING = C [] G [X, Y, Z,
 * T]; (generators X, Y, Z, T) <br> <br> Each element of algebra is a sum of
 * terms with coefficients that are functions. <br> For example,
 * R64[x,y]G[X,Y,Z] - this is the free group algebra with three * generators of
 * the noncommutative X, Y, Z on the functions in R64 [x, y]. <br> A = (t ^ 2
 * +1) X + tY + 3X ^ 2y ^ 3 + (t ^ 2 +1) XY ^ 3X ^ 2Y ^ {-2} x ^ 2, <br> B = \
 * sin (t ^ 2 +1) X + \ cos (t) Y + 3X ^ 2y ^ 3 + (t ^ 2 +1) XY ^ 3X ^ 2Y ^ {-2}
 * x ^ 2 - elements of such algebra. <br>
 *
 * @author gennady
 */
public class Algebra extends Element {
   static final long serialVersionUID = 10000020151030L;
    /**
     * coefficients
     */
    public ArrayList<Element> coeff;
    /**
     * terms: each element of Algebra is a sum of terms with coefficients
     */
    public ArrayList<Gterm> term; //
    // Заплатка , надоело что по умолчанию не понятно что пишет )))) Роман
    StringBuilder names = new StringBuilder("NonCommut:");

    @Override
    public Element one(Ring r) {
        ArrayList<Element> coef = new ArrayList<Element>();
        coef.add(r.numberONE());
        ArrayList<Gterm> term_ = new ArrayList<Gterm>();
        term_.add(new Gterm(new int[0]));
        return new Algebra(coef, term_);
    }

    @Override
    public Element minus_one(Ring r) {
        ArrayList<Element> coef = new ArrayList<Element>();
        coef.add(r.numberMINUS_ONE());
        ArrayList<Gterm> term_ = new ArrayList<Gterm>();
        term_.add(new Gterm(new int[0]));
        return new Algebra(coef, term_);
    }

    @Override
    public Element zero(Ring r) {
        ArrayList<Element> coef = new ArrayList<Element>();
        coef.add(r.numberZERO());
        ArrayList<Gterm> term_ = new ArrayList<Gterm>();
        term_.add(new Gterm(new int[0]));
        return new Algebra(coef, term_);
    }

    @Override
    public Element myOne(Ring r) {
        return one(r);
    }

    @Override
    public Element myZero(Ring r) {
        return zero(r);
    }

    public Algebra() {
    }

    public Algebra(Element coef,ArrayList<Gterm> term) {
        ArrayList<Element> c=new ArrayList<Element>();
        c.add(coef);
        this.coeff=c;
        this.term=term;
    }
    public Algebra(Element coef, Gterm Term) {
        ArrayList<Gterm> t =new ArrayList();
        ArrayList<Element> c=new ArrayList ();
        c.add(coef); t.add(Term);
        this.coeff=c;
        this.term=t;
    }


    // where term[0] is a total degree of term,
    // term[1], term[3],term[5].... are the names of generators
    // term[2], term[4],term[6].... their degrees
    // Example. Let we have two generators with names: X -- 1; Y -- 2.
    // Then X^3Y^{-1}X^2Y^4 is coded by the array of int[8;1,3;2,-1;1,2;2,4]
    // where term[0]=3-1+2+4=8 is a total degree.
    public Algebra(ArrayList<Element> coeff, ArrayList<Gterm> term) {
        this.coeff = coeff;
        this.term = term;
    }

    /**
     * Принцип рабты - забрасываем el в массив коэфициентов, термы оставляем
     * пустыми.
     *
     * @param el
     */
    public Algebra(Element el) {
        ArrayList<Gterm> n_term = new ArrayList<Gterm>();
        n_term.add(new Gterm(new int[0]));
        this.term = n_term;
        ArrayList<Element> n_coef = new ArrayList<Element>();
        n_coef.add(el);
        this.coeff = n_coef;
    }

    public Algebra(String s, Ring ring) {
        F f = Parser.getF(s, ring);
        if (f.X.length > 1) {
            throw new IllegalArgumentException(
                    "Can't make Algebra from String (root has more than 1 argument): "
                    + s);
        } else {
            Element x = f.X[0];
            if (x instanceof Algebra) {
                Algebra res = (Algebra) x;
                this.coeff = new ArrayList<Element>(res.coeff);
                this.term = new ArrayList<Gterm>(res.term);
            } else {
                this.term = new ArrayList<Gterm>(1);
                this.term.add(new Gterm(new int[] {0}));
                ArrayList<Element> coeffs = new ArrayList<Element>(1);
                if (x instanceof Polynom) {
                    coeffs.add((Polynom) x);
                    this.coeff = coeffs;
                } else {
                    if (x instanceof F) {
                        coeffs.add((F) x);
                        this.coeff = coeffs;
                    } else {
                        throw new IllegalArgumentException(
                                "Can't make Algebra from String (bad type of root): "
                                + s);
                    }
                } // Отметим, что числа уже обернуты в полиномы после parsera
            }
        }
    }

    public Algebra sortDown(Ring ring) {
        int n = coeff.size();
        Element[] coeffAr = new Element[n];
        Element[] coeffArS = new Element[n];
        coeff.toArray(coeffAr);
        Gterm[] termAr = new Gterm[n];
        Gterm[] termArS = new Gterm[n];
        term.toArray(termAr);
        int[] p = Array.sortPosDown(termAr, ring);
        for (int i = 0; i < n; i++) {
            coeffArS[i] = coeffAr[p[i]];
        }
        for (int i = 0; i < n; i++) {
            termArS[i] = termAr[p[i]];
        }
        return new Algebra(new ArrayList<Element>(Arrays.asList(coeffArS)),
                new ArrayList<Gterm>(Arrays.asList(termArS)));
    }

    public Algebra normalForm(Ring ring) {
        Gterm b1 = null, b2 = null;
        Element c1 = null, c2 = null;
        int start = 0;
        if (coeff.size() > 0) {
            b1 = term.get(0);
            c1 = coeff.get(0);
        }
        for (int i = 1; i < coeff.size();) {
            c2 = coeff.get(i);
            if (c2.isZero(ring)) {
                coeff.remove(i);
                term.remove(i);
            } else {
                b2 = term.get(i);
                if (b1.equals(b2, ring)) {
                    coeff.set(start, c1.add(c2, ring));
                    coeff.remove(i);
                    term.remove(i);
                } else {
                    b1 = b2;
                    c1 = c2;
                    start++;
                    i++;
                }
            }
        }
        if (coeff.get(0).isZero(ring)) {
            coeff.remove(0);
            term.remove(0);
        }
        return this;
    }

    /**
     * Работает только с полиномами и числовыми типами
     *
     * @param x
     * @param ring
     *
     * @return
     */
    @Override
    public Element add(Element x, Ring ring) {
        if (x.numbElementType() < Ring.Polynom) {
            return add(new Algebra(x), ring);
        }
        if (x.numbElementType() == Ring.G) {
            return add((Algebra) x, ring);
        }
        return null;
    }

    public Algebra add(Algebra x, Ring ring) {
        ArrayList<Element> res_coeffs = new ArrayList<Element>();
        ArrayList<Gterm> res_terms = new ArrayList<Gterm>();
        res_coeffs.addAll(coeff);
        res_coeffs.addAll(x.coeff);
        res_terms.addAll(term);
        res_terms.addAll(x.term);
        Algebra res = new Algebra(res_coeffs, res_terms);
        return res.sortDown(ring).normalForm(ring);
    }

    @Override
    public Element subtract(Element x, Ring ring) {
        if (x.numbElementType() < Ring.Polynom) {
            return subtract(new Algebra(x), ring);
        }
        if (x.numbElementType() == Ring.G) {
            return subtract((Algebra) x, ring);
        }

        return null;
    }

    public Algebra subtract(Algebra x, Ring ring) {
        ArrayList<Element> res_coeffs = new ArrayList<Element>();
        ArrayList<Gterm> res_terms = new ArrayList<Gterm>();
        res_coeffs.addAll(coeff);
        for (Element el : x.coeff) {
            res_coeffs.add(el.negate(ring));
        }
        res_terms.addAll(term);
        res_terms.addAll(x.term);
        Algebra res = new Algebra(res_coeffs, res_terms);
        return res.sortDown(ring).normalForm(ring);
    }

    @Override
    public Element multiply(Element el, Ring r) {
        if(el.isItNumber()){
        ArrayList<Element> newCoeff=new ArrayList<Element>();
        for(int i=0;i<coeff.size();i++){
        newCoeff.add(coeff.get(i).multiply(el, r));
        }
        return new Algebra(newCoeff, term);
        }
        if (el.numbElementType() <= Ring.Polynom) {
            return multiply(new Algebra(el), r);
        }
        return multiply((Algebra) el, r);
    }

    public Algebra multiply(Algebra x, Ring ring) {
        int n = coeff.size();
        int m = x.coeff.size();
        Element[] coeffAr = new Element[n];
        Element[] coeffArx = new Element[m];
        coeff.toArray(coeffAr);
        x.coeff.toArray(coeffArx);
        Gterm[] termAr = new Gterm[n];
        Gterm[] termArx = new Gterm[m];
        term.toArray(termAr);
        x.term.toArray(termArx);
        Gterm[] termN = new Gterm[m * n];
        Element[] coeffN = new Element[m * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int k = m * i + j;
                termN[k] = termAr[i].multiply(termArx[j]);
                coeffN[k] = coeffAr[i].multiply(coeffArx[j], ring);
            }
        }
        Algebra aN = new Algebra(new ArrayList<Element>(Arrays.asList(coeffN)),
                new ArrayList<Gterm>(Arrays.asList(termN)));
        return (aN.sortDown(ring)).normalForm(ring);
    }

    @Override
    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isZero(Ring ring) {
        return coeff.isEmpty()
                | (term.size() == 1 & term.get(0).T.length == 0
                & coeff.size() == 1 & coeff.get(0).isZero(ring));
    }

    @Override
    public Boolean isOne(Ring ring) {
        return term.size() == 1 & term.get(0).T.length == 0 & coeff.size() == 1
                & coeff.get(0).isOne(ring);
    }

    @Override
   public boolean isItNumber() {
        return term.size() == 1 & term.get(0).T.length == 0 & coeff.size() == 1
                & coeff.get(0).isItNumber();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Algebra) {
            Algebra a = (Algebra) obj;
            return coeff.equals(a.coeff) && term.equals(a.term);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.coeff != null ? this.coeff.hashCode() : 0);
        hash = 97 * hash + (this.term != null ? this.term.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Element x, Ring r) {
        if (x instanceof Algebra) {
            Algebra a = (Algebra) x;
            if (a == this) {
                return true;
            }

            ListIterator<Element> c1 = coeff.listIterator();
            ListIterator<Element> c2 = a.coeff.listIterator();
            while (c1.hasNext() && c2.hasNext()) {
                Element el1 = c1.next();
                Element el2 = c2.next();
                if (!(el1 == null ? el2 == null : el1.equals(el2, r))) {
                    return false;
                }
            }
            boolean coeffsAreEqual = !(c1.hasNext() || c2.hasNext());

            ListIterator<Gterm> t1 = term.listIterator();
            ListIterator<Gterm> t2 = a.term.listIterator();
            while (t1.hasNext() && t2.hasNext()) {
                Gterm term1 = t1.next();
                Gterm term2 = t2.next();
                if (!(term1 == null ? term2 == null : term1.equals(term2, r))) {
                    return false;
                }
            }
            boolean termsAreEqual = !(c1.hasNext() || c2.hasNext());

            return coeffsAreEqual && termsAreEqual;
        } else {
            return false;
        }

    }
    @Override
    public Element  pow(int n, Ring r){
        if (coeff.size()>1)  return new F(F.intPOW, this, new NumberZ64(n) );
        return new Algebra(coeff.get(0).pow(n,r), term.get(0).pow(n,r));    
    }
    
    
    
//    @Override
//    public String toString(Ring ring) { 
//       StringBuilder res = new StringBuilder();
//       int i =0;  Element first , First ;    
//        if (coeff.size() > i) {
//            while (i < coeff.size()) { if(i>0) res.append("+");
//            first=coeff.get(i);
//            if (!first.isOne(ring))
//                if (first.isItNumber()) res.append(first.toString(ring));
//                else res.append("(").append(first.toString(ring)).append(")");
//            First=term.get(i);
//            if (!First.isOne(ring))res.append(First.toString(ring));
//              else if (first.isOne(ring)) res.append("1");      
//            i++;
//        }} else  return "0";
//        return res.toString();
//    }
    @Override
    public String toString() { 
       StringBuilder res = new StringBuilder();
       int i =0;  Element first  ;    
        if (coeff.size() > 0) {
            while (i < coeff.size()) { if(i>0) res.append("+");
              first=coeff.get(i);
              if (first.isItNumber()) res.append(first.toString( ));
                else res.append("(").append(first.toString( )).append(")");
              res.append(term.get(i).toString()); i++;
           }
        } else  return "0";
        return res.toString();
    }
    
    @Override
    public int numbElementType() {
        return Ring.G;
    }

    private String elTermtoString(int el, int pow) {
        return (pow == 1) ? names.substring(el, el + 1).toString() : names
                .substring(el, el + 1).toString() + "^{" + pow + "}";
    }

   // @Override
    public String toString1111() {
        StringBuilder res = new StringBuilder();
        res.append(coeff.get(0));
        Gterm t_ = term.get(0);
        if (t_.T.length < 4) {
            res.append(elTermtoString(t_.T[1], t_.T[2]));
        } else {
            res.append("\\times(");
            int j = 1;
            while (j < t_.T.length) {
                res.append(elTermtoString(t_.T[j], t_.T[j + 1]));
                j += 2;
            }
        }
        for (int i = 1; i < coeff.size(); i++) {
            if (coeff.get(i).isNegative()) {
                res.append(coeff.get(i));
            } else {
                res.append("+").append(coeff.get(i));
            }
            Gterm t = term.get(i);
            if (t.T.length < 4) {
                res.append(elTermtoString(t.T[1], t.T[2]));
            } else {
                res.append("\\times(");
                int j = 1;
                while (j < t.T.length) {
                    res.append(elTermtoString(t.T[j], t.T[j + 1]));
                    j += 2;
                }
                res.append(")");
            }
        }

        return res.toString();
    }
}
