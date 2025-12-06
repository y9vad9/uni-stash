package com.mathpar.polynom;

import java.util.Arrays;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

/**
 *
 * @author gennady
 */
public class Gterm extends Element {
    private static final long serialVersionUID = -5987889368611904309L;
    /**
     * The term of group algebra, // where term[0] is a total degree of term, //
     * term[1], term[3],term[5].... are the names of generators // term[2],
     * term[4],term[6].... their degrees // Example. Let we have two generators
     * with names: X -- 1; Y -- 2. // Then X^3Y^{-1}X^2Y^4 is coded by the array
     * of int[8;1,3;2,-1;1,2;2,4] // where term[0]=3-1+2+4=8 is a total degree.
     */
    public int[] T;

    public Gterm(int[] term) {
        T = term;
    }
    public static  Gterm One= new Gterm(new int[]{0});
    
    /**
     * Comparison of two terms. We put the term1 is grater then term2: 1) if
     * they have total degree_1 > total degree_2; else 2) if number of
     * generators_1 > number of generators_2 3) if the nubmers at position 1..k
     * are equal and this.T[k+1]>t.T[k+1]
     *
     * @param t -- the second Gterm
     *
     * @return if this=t return 0, if this>t return (+int), else return (-int),
     */
    public int compareTo(Gterm t) {
        int[] term1 = this.T;
        int[] term2 = t.T;
        if (term1.length == 0 | term2.length == 0) {
            int res = term1.length - term2.length;
            return (res > 0) ? 1 : (res < 0) ? -1 : 0;
        }
        int s = term1[0] - term2[0];
        if (s != 0) {
            return s;
        }
        int l = term1.length;
        int m = l - term2.length;
        if (m != 0) {
            return m;
        }
        for (int i = 0; i < l; i++) {
            int tt = (term1[i] - term2[i]);
            if (tt != 0) {
                return tt;
            }
        }
        return 0;
    }

    public boolean isEqual(Gterm term2) {
        return (compareTo(term2) == 0) ? true : false;
    }

//    /**
//     * String form of Gterm.
//     *
//     * @param names -- the names of variables
//     *
//     * @return string form of Gterm
//     */
//    public String toString(Ring ring) {
//        StringBuilder res = new StringBuilder();
//        for (int i = 1; i < T.length; i += 2) {
//            int ii = T[i + 1];
//            if (ii != 0) {
//                res.append(ring.G_List_Names.get(T[i]));
//                if (ii != 1) {
//                    res.append("^");
//                    res.append(((ii > 1) ? ii : ("{" + ii + "}")));
//                }
//            }
//        }
//        return res.toString();
//    }
        public String toString() {
            String[] namesV=new String[]{"\\X","\\Y","\\Z","\\Z1","\\Z2","\\Z3","\\Z4","\\Z5","\\Z6","\\Z7"};
          return toString(namesV);
        }
   /**
     * String form of Gterm.
     *
     * @param names -- the names of variables
     *
     * @return string form of Gterm
     */
    public String toString(String[] namesV) {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i < T.length; i += 2) {
            int ii = T[i + 1];
            if (ii != 0) {
                res.append(namesV[T[i]]);
                if (ii != 1) {
                    res.append("^");
                    res.append(((ii > 1) ? ii : ("{" + ii + "}")));
                }
            }
        }
        return res.toString();
    }
    public Gterm multiply(Gterm t) {
        int[] term1 = T;
        int[] term2 = t.T;
        int l1 = term1.length;
        int l2 = term2.length;
        if (l1 < 2) { return (new Gterm(term2));
        }
        if (l2 < 2) { return (new Gterm(term1));
        }
        int i1 = l1 - 2;
        int i2 = 1;
        int pow = term1[i1 + 1] + term2[i2 + 1];
        boolean degFlag = false; // степени не суммируются
        while ((i1 > 0) && (i2 < l2) && (term1[i1] == term2[i2])) {
            pow = term1[i1 + 1] + term2[i2 + 1];
            if (pow != 0) {
                degFlag = true;
                break;
            }
            i1 -= 2;
            i2 += 2;
        }
        int li = l1 - i2;
        int ll = li + l2 - i2 + 1;
        int f1 = li;
        int f2 = l2 - i2;
        int s2 = i2;
        if (degFlag) {
            ll -= 2;
            f2 -= 2;
            s2 += 2;
        }
        int[] rez = new int[ll];
        rez[0] = term1[0] + term2[0];
        System.arraycopy(term1, 1, rez, 1, f1);
        if (degFlag) {
            rez[f1] = pow;
        }
        System.arraycopy(term2, s2, rez, f1 + 1, f2);
        return new Gterm(rez);
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        return compareTo((Gterm) x);
    }

    @Override
    public int compareTo(Element o) {
        if (o instanceof Gterm) {
            return compareTo((Gterm) o);
        }
        return -1;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return (T.length < 2);
    }

    @Override
    public Boolean isOne(Ring ring) {
        return (T.length < 2);
    }

    @Override
    public boolean equals(Element x, Ring r) {
        if (x instanceof Gterm) {
            return Arrays.equals(((Gterm) x).T, T);
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Gterm) {
            return equals((Gterm) obj, null);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Arrays.hashCode(this.T);
        return hash;
    }
    
    @Override
    public Gterm  pow(int m, Ring r){
      int n=T.length;
      if ((n<2)||(m==1)) return this;
      if (m==0) return One; 
      int[] W=new int[n]; int a=Math.abs(m); Gterm res=new Gterm(W);
      if (n==3) {   W[0]=T[0]*m;  W[1]=T[1]; W[2]=T[2]*m;  return res;}
      if (m<0){int s=n-2; W[0]=-T[0];  
                  for (int i = 1; i < n; i+=2) {W[i]=T[s];W[i+1]=-T[s+1]; s-=2;}}
      else{ res=this;}
      Gterm RES=res;
      for (int i = 1; i < a; i++) {RES=RES.multiply(res);}
      return RES;      
    }
}           
