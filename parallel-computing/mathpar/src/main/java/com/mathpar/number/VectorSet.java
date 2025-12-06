package com.mathpar.number;

import com.mathpar.func.F;
import java.util.ArrayList;
import com.mathpar.matrix.MatrixS;

/**
 * Object VectorSet is а compact in n-dimensional space.  
 * Example:   0<x<3, x^2+1<y< x^3+2, 0<z< x^2+y^2
 *
 */
public class VectorSet extends VectorS  {
    public VectorSet() {
    }

    public VectorSet (Element[] a) {
        V = a;
    }

    public VectorSet (ArrayList<Element> a) {
        Element[] nn = new Element[a.size()];
        a.toArray(nn);
        V = nn;
    }

    public VectorSet (int n) {
        V = new Element[n];
    }

    /**
     * Constructor from Element array
     *
     * @param arr - vector elements
     */
    /**
     * Constructor of VectorC from F (VectorS), which has an array of Elements
     * in the first variable X[0]
     *
     * @param el F VectorS.
     */
    public VectorSet (F el) {
        V = el.X.clone();
    }

  
    /**
     * Constructor from Element array
     *
     * @param arr - vector elements
     */
    public VectorSet (Element el) {
        if (el instanceof VectorS){V = ((VectorS) (el)).V;
        } else {V = new Element[]{el};}
    }

    /**
     * Constructor from long array
     *
     * @param one - one of needed Element type
     * @param arr - long array
     */
    public VectorSet (long arr[], Ring ring) {
        Element one = ring.numberONE();
        Element[] sa = new Element[arr.length];
        for (int i = 0; i < arr.length; i++) {
            sa[i] = one.valOf(arr[i], ring);
        }
        V = sa;
    }

    public boolean isEqual(VectorSet  b, Ring ring) {
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

    /**
     * String form of VectorS
     *
     * @return -- String form of VectorS
     */
    @Override
    public String toString() {
        Ring r = Ring.ringR64xyzt;
        return toString(r);
    }

    /**
     *   NOT_EQUAL = -3; LESS            = -2; LESS_OR_EQUAL = -1;
     *   EQUAL      = 0; GREATER_OR_EQUAL = 1; GREATER        = 2;
  
     * Вектор преобразуется к текстовому формату в виде столбца. В выходном
     * объекте String содержатся элементы вектора и символы разрыва строки.
     */
    @Override
    public String toString(Ring ring) {
    String[] symb=new String[]{"\\ne ","< ","\\le ","= ","\\ge ","> "};
    int len=V.length;
    if (len == 0) {return "";}
    StringBuilder tmp = new StringBuilder("\\{" ); 
    int lenV=0;
    for (int i = 0; i < len; i++) {
      Element[] vv=null;
      if(V[i] instanceof VectorS)vv=((VectorS)V[i]).V; 
      else if (V[i] instanceof VectorS) vv=((VectorS)V[i]).V;
            else {tmp.append(" under construction ");  break;}
      if((i==1)&&(lenV==1))tmp.append(": " ); else if (i!=0) tmp.append(", " );
      lenV=vv.length; 
      if (lenV%2 == 1) for (int j = 0; j < lenV; j++) { 
          if(j%2==0)tmp.append(vv[j].toString(ring)) ;
          else tmp.append(symb[vv[j].intValue()+3])  ;
      }else {tmp.append(" under construction "); break;}  
    }
    tmp.append("\\}" );
    return tmp.toString();
    }

    /**
     * Convert each element to other type of Element
     *
     * @param oneOfNewType -- one Of New Element Type
     * @return -- new VectorS each element of which has new scalar type
     */
    public VectorSet toVectorSetNewType(int NewType, Ring ring) {
        VectorSet s = new VectorSet(V.length );
        for (int i = 0; i < V.length; i++) {
            s.V[i] = (V[i]).toNewRing(NewType, ring);
        }
        return s;
    }
 
  

 
    /**
     * Процедура создания матрицы-MatrixS из данного вектора-столбцв
     *   ???????   ??????
     * @return Матрица в виде двумерного массива скаляров
     */
    public MatrixS toMatrixS() {
        Element[][] M = toColMatrix();
        int[][] col = new int[V.length][1];
        for (int i = 0; i < V.length; i++) {
            col[i][0] = 0;
        }
        return new MatrixS(V.length, 1, M, col);
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

    /**
     *    * New VectorC has new type of element
     *
     * @param NewType (int) type of elements
     * @return VectorC with new type of elements
     */
    public VectorSet toVectorSet(int NewType, Ring ring) {
        Element[] res = new Element[V.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = V[i].toNumber(NewType, ring);
        }
        return new VectorSet(res);
    }

    @Override
    public int numbElementType() {
        return Ring.VectorSet;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        VectorSet s = new VectorSet(V.length );
        for (int i = 0; i < V.length; i++) {
            s.V[i] = (V[i]).toNewRing(Algebra, r);
        }
        return s;
    }

    public int compareTo(Element x) {
        int xNum = x.numbElementType();
        if (xNum > Ring.VectorSet) {
            return 1;
        }
        if (xNum < Ring.VectorSet) {
            return -1;
        }
        VectorSet X = (VectorSet) x;
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
        if (xNum > Ring.VectorSet) { return 1; }
        if (xNum < Ring.VectorSet) {return -1;   }
        VectorSet X = (VectorSet) x;
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
    public Object clone() {
        VectorSet temp = new VectorSet();
        temp.V = new Element[this.V.length];
        for (int i = 0; i < temp.V.length; i++) {
            temp.V[i] = (Element) this.V[i].clone();
        }
        return temp;
    }
}
