package com.mathpar.matrix;

import java.io.Serializable;
import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.func.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class MatrixD extends Element implements Serializable {
    public static final long serialVersionUID = 1L;

    public Element[][] M;//matrix of Element
    public int fl=0; // флаг отвечающий за наличие в матрице элементов типа функций и символов (0-false)

    public MatrixD() {
    }

    public MatrixD(Ring ring) {
        this.M = new Element[0][0];
    }

    /**
     * Коструктор от двух мерного массива элементов
     *
     * @param M
     */
   public MatrixD(Element[][] M, int ffl) {this.M = M; this.fl=ffl;}
   public MatrixD(Element[][] M) {this.M = M;}
   public MatrixD(Element[] v, boolean transpose) {
        int n=v.length;
        if(transpose){  M = new Element[n][1];
            for (int i = 0; i < n; i++) 
                M[i][0]=v[i];  
        }else {M = new Element[1][n]; 
               System.arraycopy(v, 0, M[0], 0, n); 
                } 
    }
    public MatrixD(VectorS V, boolean tr, int ffl) {fl=ffl;
        boolean transpose=(tr && (V.fl>=0))||(!tr && (V.fl<0));
        Element[] v=V.V; int n=v.length;
        if(transpose){  M = new Element[n][1];
            for (int i = 0; i < n; i++) 
                M[i][0]=v[i];  
        }else {M = new Element[1][n]; 
               System.arraycopy(v, 0, M[0], 0, n); 
        }fl=((V.fl==-1)||(V.fl==0))?0:1;
    }
    /**
     * Конструктор MatrixD от функции F, у которой n аргументов, каждый из
     * которых является функцией от m аргументов. Например:
     * \VectorS(\VectorS(x,y),\VectorS(2x,3y))
     *
     * @param f
     */
    public MatrixD(F f) {
        if (f.name != F.VECTORS) {
            return;
        }
        int n = f.X.length;
        int m = ((F) f.X[0]).X.length;
        M = new Element[n][];
        for (int i = 0; i < n; i++) {
            if (((F) (f.X[i])).name != F.VECTORS) {
                M = null;
                return;
            }
            M[i] = ((F) (f.X[i])).X;
        }
    }
        public MatrixD(VectorS f) {Element[] v=f.V;
            M=new Element[v.length][0];
            for (int i = 0; i < v.length; i++)  
                if(v[i] instanceof VectorS) M[i]=((VectorS)v[i]).V;  
        }
    
    /** Создает рандомную матрицу размерности m на n
     * @param m кол-во строк
     * @param n кол-во столбцов
     * @param mod макс. элемент по модулю
     * @param ring кольцо
     */
    public MatrixD(int m, int n, int mod, Ring ring){
        int[][] a = new int[m][n];
        mod++;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
               // a[i][j] = (int)(mod*(2*Math.random() - 1));
                a[i][j] = (int)(mod*(Math.random()));
        M = new MatrixD(a, ring).M;
    }
    public MatrixD(MatrixS A, Ring ring) {M = A.toScalarArray(ring);}
    public MatrixD(MatrixS A, Ring ring, int ffl) {fl=ffl; M = A.toScalarArray(ring);}
    public MatrixD(MatrixS A, boolean toSquaredArray, Ring ring) { 
        M = (toSquaredArray) ? A.toSquaredArray(ring) : A.toScalarArray(ring);
    }
    public MatrixD(MatrixS A, boolean toSquaredArray, Ring ring, int ffl) {fl=ffl;
        M = (toSquaredArray) ? A.toSquaredArray(ring) : A.toScalarArray(ring);
    }
    public MatrixD(MatrixS A){M = A.toScalarArray(Ring.ringR64xyzt);}
    public MatrixD(MatrixS A, int ffl) {fl=ffl;M = A.toScalarArray(Ring.ringR64xyzt);}
   public MatrixD(MatrixD A, int ffl) {M = A.M; fl=ffl; }

    public MatrixD(MatrixS A, boolean toSquaredArray, int  fll) {fl=fll;
        M = (toSquaredArray) ? A.toSquaredArray(Ring.ringR64xyzt) : A.toScalarArray(Ring.ringR64xyzt);
    }

    /**
     * Конструктор матрицы из двумерного плотного массива типа long.
     *
     * @param A входная матрица в ввиде плотного двумерного массива
     */
    public MatrixD(long[][] A, Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        int n = A.length, m = A[0].length;
        M = new Element[n][];
        Element[] MM = new Element[m];
        for (int i = 0; i < n; i++) {
            long[] AA = A[i];
            for (int j = 0; j < A[i].length; j++) {
                MM[j] = one.valOf(AA[j], ring);
            }
            M[i] = new Element[MM.length];
            System.arraycopy(MM, 0, M[i], 0, MM.length);
        }
    }
    
    
     public MatrixD divideByNumber(Element s, Ring ring) {
       if(s.isOne(ring))return this;
       if(s.isMinusOne(ring) )return negate(ring); 
        int n = M.length;
        int m = 0;
        Element[][] r = new Element[n][0];
        for (int i = 0; i < n; i++) {
            Element[] Mi = M[i];
            m = Mi.length;
            Element[] ri = new Element[m];
         //   r[i] = ri;
            for (int j = 0; j < m; j++) {
           //     System.out.println("sssss = "+Mi[j]+"    " + s.toString(ring));
                ri[j] = Mi[j].divideExact(s, ring); 
            }
             r[i] = ri;
        }
        return new MatrixD(r,fl);
    }

    public static MatrixD zeroMatrix() {
        return new MatrixD(MatrixS.zeroMatrix(),0);
    }
    public static MatrixD zeroMatrix(int n) {
        return new MatrixD(MatrixS.zeroMatrix(n),0);
    }
     
     public static MatrixD scalarMatrix(int n, Element a, Ring ring) {
        MatrixD ss=zeroMatrix(n);
        if (a.isZero(ring)) return ss;
        Element[][] MM=ss.M;
        for (int i = 0; i < n; i++)  MM[i][1] = a;
        return ss;
    }
//      public MatrixD[] split() {
//          int n=0,m=0;
//          MatrixD[] res = new MatrixD[4];
//          Element[][] A;
//          int b = M.length;
//          int a = M[0].length;
//          if (a<b) {
//            //Добавляем нулевые столбцы
//              m=b;
//              n=b;
//          }
//          if (a>b) {
//              //Добаляем нулевые строки
//              m=a;
//              n=a;
//          }
//          if (a==b && a%2==1) {
//              //Добавляем нулевую строку и столбец
//              m=a+1;
//              n=a+1;
//          }
//          if (a==b && a%2==0) {
//             m=a;
//             n=b;
//          }
//          //
//          A = new Element[m][n];
//          for (int i = 0; i < m; i++) {
//                  for (int j = 0; j < m; j++) {
//                      A[i][j]=NumberR64.ZERO;
//                  }
//          }
//          for (int i = 0; i < b; i++) {
//              System.arraycopy(M[i], 0, A[i], 0, a);
//          }
//          int k=0,l;
//          Element[][] R1 = new Element[m/2][m/2], R2 = new Element[m/2][m/2];
//          Element[][] R3 = new Element[m/2][m/2], R4 = new Element[m/2][m/2];
//          for (int i=0; i<m/2; i++){
//              l=0;
//              for (int j=0;j<m/2;j++) {
//                  R1[k][l]=A[i][j];
//                  l++;
//              }
//              k++;
//          }
//          k=0;
//          for (int i=m/2;i<m;i++) {
//              l=0;
//              for (int j = 0; j < m/2; j++) {
//                  R3[k][l]=A[i][j];
//                  l++;
//              }
//              k++;
//          }
//          k=0;
//          for (int i=0;i<m/2;i++) {
//              l=0;
//              for (int j = m/2; j < m; j++) {
//                  R2[k][l]=A[i][j];
//                  l++;
//              }
//              k++;
//          }
//          k=0;
//          for (int i=m/2;i<m;i++) {
//              l=0;
//              for (int j = m/2; j < m; j++) {
//                  R4[k][l]=A[i][j];
//                  l++;
//              }
//              k++;
//          }
//          res[0] = new MatrixD(R1);
//          res[1] = new MatrixD(R2);
//          res[2] = new MatrixD(R3);
//          res[3] = new MatrixD(R4);
//          return res;
//    } 
     public MatrixD[] split(MatrixD m) {
     MatrixD[] res = new MatrixD[4];
        Element [][] M = m.M;
        int n1 = M.length;
        int n2 = M[0].length;

        int len1 = n1 / 2;
        int len2 = n2 / 2;
        int len12 = n1 - len1;
        int len22 = n2 - len2;

        Element[][] bl = new Element[len1][len2];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[i][j];
        res[0] = new MatrixD(bl,fl);
        bl = new Element[len1][len22];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[i][len2+j];
        res[1] = new MatrixD(bl,fl);
        bl = new Element[len12][len2];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[len1+i][j];
        res[2] = new MatrixD(bl,fl);
        bl = new Element[len12][len22];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[len1+i][len2+j];
        res[3] = new MatrixD(bl,fl);

        return res;
}
/** Split the matrix into 4 equal blocks
 * 
 * @return 4 equal blocks: [11,12,21,22]
 */
    public MatrixD[] splitTo4() {
        int n = M.length >> 1;
        Element[][] a11 = new Element[n][n];
        Element[][] a12 = new Element[n][n];
        Element[][] a21 = new Element[n][n];
        Element[][] a22 = new Element[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(M[i], 0, a11[i], 0, n);
            System.arraycopy(M[i], n, a12[i], 0, n);
            System.arraycopy(M[i + n], 0, a21[i], 0, n);
            System.arraycopy(M[i + n], n, a22[i], 0, n);
        }
        return new MatrixD[]{new MatrixD(a11, fl), new MatrixD(a12, fl),
                new MatrixD(a21, fl), new MatrixD(a22, fl)
        };
    }

    public MatrixD(int[][] a, Ring ring) {
        MatrixS A = new MatrixS(a, ring);
        M = A.toScalarArray(ring);
    }
    public MatrixD(double[][] A, Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        int n = A.length, m = A[0].length;
        M = new Element[n][];
        Element[] MM = new Element[m];
        for (int i = 0; i < n; i++) {
            double[] AA = A[i];
            for (int j = 0; j < A[i].length; j++) {
                MM[j] = one.valOf(AA[j], ring);
            }
            M[i] = new Element[MM.length];
            System.arraycopy(MM, 0, M[i], 0, MM.length);
        }
    }
    /**
     * Tabulation the function f.
     * @param f -- function of one variable for tabulation
     * @param x0 -- first value of variable
     * @param x1-- last value of variable
     * @param n  -- number of steps
     * @param ring -- Ring
     */ 
    public  MatrixD(Element[] f, Element x0,  Element x1, Element n, Ring ring) {  
       FvalOf VO=new FvalOf(ring);
       Element X0=  ( x0.isItNumber())? x0: VO.valOf(x0, new Element[0]);
       Element X1=  ( x1.isItNumber())? x1: VO.valOf(x1, new Element[0]);           
       Element N1=  (  n.isItNumber())? n: VO.valOf(n, new Element[0]);     
        int N=N1.intValue();
        Element del= (N==0)? NumberR64.ONE: X1.subtract(X0, ring).divide(N1, ring);
        int k=f.length;
        M = new Element[k+1][N+1];
        for (int i = 0; i <= N; i++) {
            M[0][i]=X0;
            for (int j = 0; j < k; j++)  
                M[j+1][i]= VO.valOf(f[j], new Element[]{X0});   
            X0=X0.add(del, ring);
        }
    }

    /**
     * Constructor of random matrixS of polynomials or numbers
     *
     * @param r -- row numbers
     * @param c -- column numbers
     * @param density -- is an integer of range 0,1...10000.
     * @param randomType -- array of: [maxPowers_1_var,.., maxPowers-last_var,
     * type of coeffs, density of polynomials, nbits] The density is an integer
     * of range 0,1...100.
     * @param ran -- Random issue
     * @param one -- one of the matrix elements ring
     *
     * @return array2d of Elements
     */
    public MatrixD(int r, int c, int density, int[] randomType, Random ran, Ring ring) {
        this(MatrixS.randomScalarArr2d(r, c, density, randomType, ran, ring));
    }

    public MatrixD(long[][] a, Ring ring, Element zero) {
        MatrixS A = new MatrixS(a, ring);
        M = A.toScalarArray(ring, zero);
    }

    public MatrixD(MatrixS A, Ring ring, Element zero, int ffl) {fl=ffl;
        M = A.toScalarArray(ring, zero);
    }

    @Override
    public String toString() {
        return toString(Ring.ringR64xyzt);
    }

    /**
     * Преобразование матрицы к текстовому формату, пригодному для вставки в
     * исходный код, систему Mathematica.
     *
     * @return String(<tt>this</tt>)
     */
    @Override
    public String toString(Ring ring) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i <= M.length - 1; i++) {
            sb.append("[");
            for (int j = 0; j <= M[i].length - 1; j++) {
                sb.append(M[i][j].toString(ring));
                if (j < M[i].length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            if (i < M.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Транспонирование
     *
     * @param ring - входное кольцо
     *
     * @return транспонированная матрица
     */
    public MatrixD transpose(Ring ring) {
        Element[][] newM = new Element[M[0].length][M.length];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                newM[j][i] = M[i][j];
            }
        }
        return new MatrixD(newM,fl);
    }

    /**
     * Преобразование матрицы long[][] к текстовому формату, пригодному для
     * вставки в исходный код, систему Mathematica.
     *
     * @return String(<tt>this</tt>)
     */
    public String toString(long[][] M) {
        StringBuilder sb = new StringBuilder("\n [");
        for (int i = 0; i <= M.length - 1; i++) {
            sb.append("[");
            for (int j = 0; j <= M[i].length - 1; j++) {
                sb.append(M[i][j]);
                if (j < M[i].length - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            if (i < M[i].length - 1) {
                sb.append(",\n");
            }
        }
        sb.append("] \n");
        return sb.toString();
    }

    /**
     * Преобразование матрицы int[][] к текстовому формату, пригодному для
     * вставки в исходный код, систему Mathematica.
     *
     * @return String(<tt>this</tt>)
     */
    public String toString(int[][] M) {
        StringBuilder sb = new StringBuilder("\n [");
        for (int i = 0; i <= M.length - 1; i++) {
            sb.append("[");
            for (int j = 0; j <= M[i].length - 1; j++) {
                sb.append(M[i][j]);
                if (j < M[i].length - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            if (i < M[i].length - 1) {
                sb.append(",\n");
            }
        }
        sb.append("] \n");
        return sb.toString();
    }

    /**
     * Расширяет данную квадратную матрицу любого размера до наименьшей матрицы
     * размера <tt> 2 x 2</tt>. Метод дописывает матрицу единичной матрицей и
     * двумя прямоугольными нулевыми матрицами снизу и справа, а исходная
     * матрица остается сверху и слева.
     *
     * @return <tt> this -> (2<sup>n</sup> x 2<sup>n</sup>) </tt> по типу <br>
     * <TABLE BORDER="1" CELLSPACING="0" CELLPADDING="5">
     * <TR>
     * <TD align = "center">this</TD><TD align = "center">0</TD>
     * </TR>
     * <TR>
     * <TD align = "center">0</TD><TD align = "center">E</TD>
     * </TR>
     * </TABLE>
     * <b> Пример использования </b> <br>
     * <CODE>  import matrix.MatrixD;                 <br>
     * import java.util.Random; <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * MatrixD matr = new MatrixD(32, 5000, 32, rnd); <br>
     * MatrixD pm = matr.proceedToPow2_1UL(); </ul>
     * } </ul>
     * } </CODE>
     */
    public MatrixD proceedToPow2_1UL(Ring ring) {
        //int len = Math.max(matr.M.length, matr.M[0].length);
        int len = M.length;
        int iter = 2;
        while (iter < len) {
            iter *= 2;
        }
        if (M.length == iter) {
            return this;
        }
        Element[][] res = new Element[iter][iter];
        for (int i = 0; i < len; i++) {
            System.arraycopy(M[i], 0, res[i], 0, len);
        }
        for (int i = 0; i < iter; i++) {
            for (int j = len; j < iter; j++) {
                res[i][j] = M[i][j].zero(ring);
            }
        }
        for (int i = len; i < iter; i++) {
            for (int j = 0; j < len; j++) {
                res[i][j] = M[i][j].zero(ring);
            }
            res[i][i] = M[i][i].one(ring);
        }
        return new MatrixD(res,fl);
    }
   public boolean equals(MatrixD x, Ring ring) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                 if(!M[i][j].equals(x.M[i][j], ring)) return false;
            }
        }
        return true;
    }
   
   public boolean equals(MatrixD x ) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                 if(!M[i][j].equals(x.M[i][j])) return false;
            }
        }
        return true;
    }
   
    public MatrixD add(MatrixD x, Ring ring) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].add(x.M[i][j], ring);
            }
        }
        return new MatrixD(z, fl|x.fl);
    }
    
    public MatrixD add(MatrixD x, Element mod, Ring ring) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = (M[i][j].add(x.M[i][j], ring)).mod(mod, ring);
            }
        }
        return new MatrixD(z, fl|x.fl);
    }
    
    

    public MatrixD add(MatrixD x, int br1, int ar1, int bc1, int ac1, int br2,
            int bc2, Ring ring) {
        Element[][] z = new Element[ar1][ac1];
        for (int i = 0; i < ar1; i++) {
            for (int j = 0; j < ac1; j++) {
                z[i][j] = M[i + br1][j + bc1].add(x.M[i + br2][j + bc2], ring);
            }
        }
        return new MatrixD(z, fl|x.fl);
    }

    public MatrixD subtract(MatrixD x, int br1, int ar1, int bc1, int ac1,
            int br2, int bc2, Ring ring) {
        Element[][] z = new Element[ar1][ac1];
        for (int i = 0; i < ar1; i++) {
            for (int j = 0; j < ac1; j++) {
                z[i][j] = M[i + br1][j + bc1].subtract(x.M[i + br2][j + bc2], ring);
            }
        }
        return new MatrixD(z, fl|x.fl);
    }

    /**
     * Вычисление разности матриц.
     *
     * @param x типа MatrixD, вычитаемое
     *
     * @return <tt> this - x </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE>  import matrix.MatrixD;                          <br>
     * import java.util.Random(); <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
     * MatrixD msum = matr1.subtract(matr2); </ul>
     * } </ul>
     * } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> вычитаются и
     * записываются в <tt>msum</tt>.
     */
    public MatrixD subtract(MatrixD x, Ring ring) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].subtract(x.M[i][j], ring);
            }
        }
        return new MatrixD(z, fl|x.fl);
    }

    /**
     * Процедура вычисления произведения матриц. Используется стандартная
     * нерекурсивная схема умножения.
     *
     * @param x типа MatrixD, сомножитель
     *
     * @return <tt> this * x </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE>  import matrix.MatrixD;                          <br>
     * import java.util.Random; <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
     * MatrixD msum = matr1.multCU(matr2); </ul>
     * } </ul>
     * } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
     * записываются в <tt>msum</tt>.
     */
    public MatrixD multCU(MatrixD x, Ring ring) {
        Element zero = ring.numberZERO;
        Element[][] z = new Element[M.length][x.M[0].length];
        Element res;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < x.M[0].length; j++) {
                res = zero;
                for (int k = 0; k < x.M.length; k++) {
                    res = res.add(M[i][k].multiply(x.M[k][j], ring), ring);
                }
                z[i][j] = res;
            }
        }
        return new MatrixD(z, fl|x.fl);
    }
    
    @Override
    public MatrixD Mod(Element x, Ring r){
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].Mod(x, r);
            }
        }
        return new MatrixD(z,fl);
    }
    
    @Override
    public MatrixD mod(Element x, Ring r){
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].mod(x, r);
            }
        }
        return new MatrixD(z,fl);
    }
    
    public MatrixD multCU(MatrixD x, Element mod, Ring ring) {
        Element zero = ring.numberZERO;
        Element[][] z = new Element[M.length][x.M[0].length];
        Element res;
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < x.M[0].length; j++) {
                res = zero;
                for (int k = 0; k < M[0].length; k++) {
                    res = res.add(M[i][k].multiply(x.M[k][j], ring), ring);
                }
                z[i][j] = res.mod(mod, ring);
            }
        }
        return new MatrixD(z, fl|x.fl);
    }

    /**
     * Блочный нерекурсивный алгоритм стандартного умножения матриц. Эффективно
     * использует кэш (как может)
     *
     * @param x типа MatrixD, правый сомножитель
     * @param qsize типа int, размер кванта умножения
     *
     * @return MatrixD
     */
    public MatrixD multCU(MatrixD x, int qsize, Ring ring) {
        Element[][] z = new Element[M.length][x.M[0].length];
        for (int i = 0; i < M.length; i += qsize) {
            for (int j = 0; j < x.M[0].length; j += qsize) {
                // инициализация данного блока результата
                //(в  станд. алгоритме она не нужна)
                for (int l = 0; l < qsize; l++) {
                    for (int m = 0; m < qsize; m++) {
                        z[i + l][j + m] = z[0][0].zero(ring);
                    }
                }
                for (int k = 0; k < M[0].length; k += qsize) {
                    for (int l = 0; l < qsize; l++) {
                        for (int m = 0; m < qsize; m++) {
                            for (int n = 0; n < qsize; n++) {
                                z[i + l][j + m] = z[i + l][j + m]
                                        .add(M[i + l][k + n].multiply(x.M[k + n][j + m], ring), ring);
                            }
                        }
                    }
                }
            }
        }
        return new MatrixD(z, fl|x.fl);
    }

    public static MatrixD oneMatrixD(int i, int j, Ring ring) {
        Element[][] fM = new Element[i][j];
        for (int k = 0; k < i; k++) {
            for (int t = 0; t < j; t++) {
                if (k == t) {
                    fM[k][t] = ring.numberONE;
                } else {
                    fM[k][t] = ring.numberZERO;
                }
            }
        }
        return new MatrixD(fM,0);
    }
    /**
     * Matrix with unit second diagonal
     * @param i number of rows
     * @param j number of columns
     * @param ring Ring
     * @return [[001],[010],[100]] -- 'xample for i=j=3.
     */
    public static MatrixD oneInvMatrixD(int i, int j, Ring ring) {
        Element[][] fM = new Element[i][j];
        int min=Math.min(j, i)-1;
        for (int k = 0; k < i; k++) {
            for (int t = 0; t < j; t++) {
                if (k ==min- t ) {
                    fM[k][t] = ring.numberONE;
                } else {
                    fM[k][t] = ring.numberZERO;
                }
            }
        }
        return new MatrixD(fM,0);
    }
    public static MatrixD zeroMatrixD(int i, int j, Ring ring) {
        Element[][] fM = new Element[i][j];
        for (int k = 0; k < i; k++) {
            for (int t = 0; t < j; t++) {
                fM[k][t] = ring.numberZERO;
            }
        }
        return new MatrixD(fM,0);
    }

    /**
     * Процедура, возвращающая единичную матрицу заданного размера k и имеющую
     * тип Element one.
     *
     * @param n типа int, число строк и столбцов матрицы
     */
    public static MatrixD ONE(int k, Ring ring) {
        Element one = ring.numberONE;
        MatrixD M = ZERO(k, k, ring);
        for (int i = 0; i < k; i++) {
            M.M[i][i] = one;
        }
        return M;
    }

    /**
     * Процедура, возвращающая нулевую матрицу заданного размера и имеющую тип
     * MainRing.
     *
     * @param k типа int, число строк и столбцов матрицы
     */
    public static MatrixD ZERO(int k, Ring ring) {
        return ZERO(k, k, ring);
    }

    /**
     * Процедура, возвращающая нулевую матрицу заданного размера и имеющую тип
     * Element one.
     *
     * @param n типа int, число строк матрицы
     * @param m типа int, число столбцов матрицы
     */
    public static MatrixD ZERO(int n, int m, Ring ring) {
        Element zero = ring.numberONE().myZero(ring);
        Element[][] mas = new Element[n][m];
        Element[] tt = mas[0];
        for (int i = 0; i < m; i++) {
            tt[i] = zero;
        }
        for (int i = 1; i < n; i++) {
            System.arraycopy(tt, 0, mas[i], 0, m);
        }
        return new MatrixD(mas, 0);
    }

    /**
     * Процедура умножения матриц с использованием алгоритма Штрассена.
     *
     * @param b типа MatrixD, сомножитель
     *
     * @return <tt> this * b </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE> import matrix.MatrixD;                          <br>
     * import java.util.Random(); <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
     * MatrixD msum = matr1.multS(matr2); </ul>
     * } </ul>
     * } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
     * записываются в <tt>msum</tt>.
     */
    // /////////////////////////////////////////////////////////////

    public MatrixD multS(MatrixD b, Ring ring) {
        return multS(b, 0, 0, 0, 0, M.length, ring);
    }

    public MatrixD multSC(MatrixD b, int qsize, Ring ring) {
        if (qsize == 2) {
            return multS(b, 0, 0, 0, 0, M.length, ring);
        }
        return multSC(b, 0, 0, 0, 0, M.length, qsize, ring);
    }

    public MatrixD multSCR(MatrixD b, int qsize, int cqsize, Ring ring) {
        if (qsize == 2) {
            return multS(b, 0, 0, 0, 0, M.length, ring);
        }
        if (cqsize == 1) {
            return multSC(b, 0, 0, 0, 0, M.length, qsize, ring);
        }
        return multSCR(b, 0, 0, 0, 0, M.length, qsize, cqsize, ring);
    }

    public MatrixD multS(MatrixD b, int br1, int bc1, int br2, int bc2,
            int ord_now, Ring ring) {

        Element[][] res = new Element[ord_now][ord_now];
        if (ord_now > 2) {
            int ordN = ord_now >>> 1;
            MatrixD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
                    multS(
                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring), ring); // 03*03
            MatrixD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
                    multS(
                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), ring); // 20*01
            MatrixD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
                    bc1 + ordN, ring).multS(
                            b.add(b, br2 + ordN, ordN, bc2, ordN,
                                    br2 + ordN, bc2 + ordN, ring), ring); //13*23

            MatrixD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
                    bc1 + ordN, ring).multS(
                            b, 0, 0, br2, bc2, ordN, ring); // 23*0
            MatrixD t3 = multS(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
                    br2 + ordN, bc2 + ordN, ring),
                    br1, bc1, 0, 0, ordN, ring); // 0*13
            MatrixD t4 = multS(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
                    bc2, ring),
                    br1 + ordN, bc1 + ordN, 0, 0, ordN, ring); // 3*20
            MatrixD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).multS(
                    b, 0, 0, br2 + ordN, bc2 + ordN, ordN, ring); // 01*3


            /*   acm.print();
             acm.print(t1);
             acm.print(t2);
             acm.print(t3);
             acm.print(t4);
             acm.print(t5);
             acm.print(t6);
             acm.print(t7);
             acm.print();*/
            for (int i = 0; i < ordN; i++) {
                for (int j = 0; j < ordN; j++) {
                    res[i][j] = t1.M[i][j].add(t4.M[i][j].add(t7.M[i][j].
                            subtract(t5.M[i][
                                 j], ring), ring), ring);
                    res[i][j + ordN] = t3.M[i][j].add(t5.M[i][j], ring);
                    res[i + ordN][j] = t2.M[i][j].add(t4.M[i][j], ring);
                    res[i + ordN][j
                            + ordN] = t1.M[i][j].add(t3.M[i][j].add(t6.M[i][j].
                                            subtract(t2.M[i][
                                 j], ring), ring), ring);
                }
            }
        } else {
            Element s1 = (M[br1][bc1].add(M[br1 + 1][bc1 + 1], ring)).multiply(b.M[
                br2][bc2].add(b.M[br2 + 1][bc2 + 1], ring), ring);
            Element s2 = (M[br1
                    + 1][bc1].add(M[br1 + 1][bc1 + 1], ring)).multiply(b.M[br2][bc2], ring);
            Element s3 = M[br1][bc1].multiply(b.M[br2][bc2
                    + 1].subtract(b.M[br2 + 1][bc2
                            + 1], ring), ring);
            Element s4 = M[br1 + 1][bc1
                    + 1].multiply(b.M[br2 + 1][bc2].subtract(b.M[br2][bc2], ring), ring);
            Element s5 = (M[br1][bc1].add(M[br1][bc1 + 1], ring)).multiply(b.M[br2
                    + 1][bc2 + 1], ring);
            Element s6 = (M[br1
                    + 1][bc1].subtract(M[br1][bc1], ring)).multiply(b.M[br2][bc2].add(b.M[br2][
                                bc2 + 1], ring), ring);
            Element s7 = (M[br1][bc1 + 1].subtract(M[br1 + 1][bc1 + 1], ring)).
                    multiply(b.M[br2 + 1][bc2].add(b.M[br2 + 1][
                                               bc2 + 1], ring), ring);

            res[0][0] = s1.add(s4.add(s7.subtract(s5, ring), ring), ring);
            res[0][1] = s3.add(s5, ring);
            res[1][0] = s2.add(s4, ring);
            res[1][1] = s1.add(s3.add(s6.subtract(s2, ring), ring), ring);
        }
        return new MatrixD(res, fl|b.fl);
    }

    public MatrixD multSC(MatrixD b, int br1, int bc1, int br2, int bc2,
            int ord_now, int qsize, Ring ring) {

        Element[][] res = new Element[ord_now][ord_now];
        if (ord_now > qsize) {
            int ordN = ord_now >>> 1;
            MatrixD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring),
                            qsize, ring); // 03*03
            MatrixD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), qsize, ring); // 20*01
            MatrixD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b.add(b, br2 + ordN, ordN, bc2, ordN,
                                    br2 + ordN, bc2 + ordN, ring), qsize, ring); //13*23

            MatrixD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b, 0, 0, br2, bc2, ordN, qsize, ring); // 23*0
            MatrixD t3 = multSC(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
                    br2 + ordN, bc2 + ordN, ring),
                    br1, bc1, 0, 0, ordN, qsize, ring); // 0*13
            MatrixD t4 = multSC(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
                    bc2, ring),
                    br1 + ordN, bc1 + ordN, 0, 0, ordN, qsize, ring); // 3*20
            MatrixD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).
                    multSC(
                            b, 0, 0, br2 + ordN, bc2 + ordN, ordN, qsize, ring); // 01*3

            for (int i = 0; i < ordN; i++) {
                for (int j = 0; j < ordN; j++) {
                    res[i][j] = t1.M[i][j].add(t4.M[i][j].add(t7.M[i][j].
                            subtract(t5.M[i][
                                 j], ring), ring), ring);
                    res[i][j + ordN] = t3.M[i][j].add(t5.M[i][j], ring);
                    res[i + ordN][j] = t2.M[i][j].add(t4.M[i][j], ring);
                    res[i + ordN][j
                            + ordN] = t1.M[i][j].add(t3.M[i][j].add(t6.M[i][j].
                                            subtract(t2.M[i][
                                 j], ring), ring), ring);
                }
            }
        } else {
            return multCU(b, br1, bc1 + ord_now, bc1, bc1 + ord_now, br2, bc2,
                    bc2 + ord_now, ring);
        }
        return new MatrixD(res, fl|b.fl);
    }

    public MatrixD multCU(MatrixD m, int br1, int er1, int bc1, int ec1,
            int br2, int bc2, int ec2, Ring ring) {
        Element[][] res = new Element[er1 - br1][ec2 - bc2];
        Element tmp;
        for (int i = br1; i < er1; i++) {
            for (int j = bc2; j < ec2; j++) {
                tmp = NumberZ.ZERO;
                for (int k = 0; k < ec1 - bc1; k++) {
                    tmp = tmp.add(M[i][k + bc1].multiply(m.M[k + br2][j], ring), ring);
                }
                res[i - br1][j - bc2] = tmp;
            }
        }
        return new MatrixD(res, fl|m.fl);
    }

    public MatrixD multSCR(MatrixD b, int br1, int bc1, int br2, int bc2,
            int ord_now, int qsize, int cqsize, Ring ring) {

        Element[][] res = new Element[ord_now][ord_now];
        if (ord_now > qsize) {
            int ordN = ord_now >>> 1;
            MatrixD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring),
                            qsize, ring); // 03*03
            MatrixD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), qsize, ring); // 20*01
            MatrixD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b.add(b, br2 + ordN, ordN, bc2, ordN,
                                    br2 + ordN, bc2 + ordN, ring), qsize, ring); //13*23

            MatrixD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b, 0, 0, br2, bc2, ordN, qsize, ring); // 23*0
            MatrixD t3 = multSC(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
                    br2 + ordN, bc2 + ordN, ring),
                    br1, bc1, 0, 0, ordN, qsize, ring); // 0*13
            MatrixD t4 = multSC(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
                    bc2, ring),
                    br1 + ordN, bc1 + ordN, 0, 0, ordN, qsize, ring); // 3*20
            MatrixD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).
                    multSC(
                            b, 0, 0, br2 + ordN, bc2 + ordN, ordN, qsize, ring); // 01*3

            for (int i = 0; i < ordN; i++) {
                for (int j = 0; j < ordN; j++) {
                    res[i][j] = t1.M[i][j].add(t4.M[i][j].add(t7.M[i][j].
                            subtract(t5.M[i][
                                 j], ring), ring), ring);
                    res[i][j + ordN] = t3.M[i][j].add(t5.M[i][j], ring);
                    res[i + ordN][j] = t2.M[i][j].add(t4.M[i][j], ring);
                    res[i + ordN][j
                            + ordN] = t1.M[i][j].add(t3.M[i][j].add(t6.M[i][j].
                                            subtract(t2.M[i][
                                 j], ring), ring), ring);
                }
            }
        } else {
            return multCUR(b, cqsize, br1, bc1, br2, bc2, ord_now, ring);
        }
        return new MatrixD(res,fl|b.fl);
    }

    public MatrixD multCUR(MatrixD m, int qsize, int br, int bc, int br2,
            int bc2, int ord_now, Ring ring) {
        if (ord_now <= qsize) {
            return multCU(m, br, br + ord_now, bc, bc + ord_now, br2, bc2,
                    bc2 + ord_now, ring);
        }
        int ordN = ord_now >>> 1;
        return join(new MatrixD[] {
            multCUR(m, qsize, br, bc, br2, bc2,
            ordN, ring).add(multCUR(m, qsize, br, bc + ordN, br2 + ordN, bc2,
            ordN, ring), ring),
            multCUR(m, qsize, br, bc, br2, bc2 + ordN,
            ordN, ring).add(
            multCUR(m, qsize, br, bc + ordN, br2 + ordN, bc2 + ordN,
            ordN, ring), ring),
            multCUR(m, qsize, br + ordN, bc, br2, bc2,
            ordN, ring).add(
            multCUR(m, qsize, br + ordN, bc + ordN, br2 + ordN, bc2,
            ordN, ring), ring),
            multCUR(m, qsize, br + ordN, bc, br2, bc2 + ordN,
            ordN, ring).add(
            multCUR(m, qsize, br + ordN, bc + ordN, br2 + ordN,
            bc2 + ordN, ordN, ring), ring)
        });

    }

    public static MatrixD join(MatrixD[] matrs) {
        int r1 = matrs[0].M.length;
        int r2 = matrs[2].M.length;
        int c1 = matrs[0].M[0].length;
        int c2 = matrs[1].M[0].length;
        int rows = r1 + r2;
        int cols = c1 + c2;
        Element[][] res = new Element[rows][cols];
        for (int i = 0; i < r1; i++) {
            for (int j = 0; j < c1; j++) {
                res[i][j] = matrs[0].M[i][j];
            }
        }
        for (int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                res[i][j + c1] = matrs[1].M[i][j];
            }
        }
        for (int i = 0; i < r2; i++) {
            for (int j = 0; j < c1; j++) {
                res[i + r1][j] = matrs[2].M[i][j];
            }
        }
        for (int i = 0; i < r2; i++) {
            for (int j = 0; j < c2; j++) {
                res[i + r1][j + c1] = matrs[3].M[i][j];
            }
        }
        return new MatrixD(res, matrs[0].fl|matrs[1].fl|matrs[2].fl|matrs[3].fl);
    }
/**  Join 4 equal-size p*p blocks in one matrix of size (2p)*(2p)
 * 
 * @param matrices the 4 blocks of matrix [(1,1),(1,2), (2,1) , (2,2)]
 * @return  one matrix of size (2p)*(2p)
 */
    public static MatrixD joinMatr(MatrixD[] matrices) {
        int n = matrices[0].M.length;
        Element[][] res = new Element[n << 1][n << 1];

        for (int i = 0; i < n; i++) {
            System.arraycopy(matrices[0].M[i], 0, res[i], 0, n);
            System.arraycopy(matrices[1].M[i], 0, res[i], n, n);
            System.arraycopy(matrices[2].M[i], 0, res[i + n], 0, n);
            System.arraycopy(matrices[3].M[i], 0, res[i + n], n, n);
        }

        return new MatrixD(res, matrices[0].fl|matrices[1].fl|matrices[2].fl|matrices[3].fl);
    }

    /**
     * Процедура умножения матриц с использованием стандартной рекурсивной схемы
     * умножения.
     *
     * @param b типа MatrixD, сомножитель
     *
     * @return <tt> this * b </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE>  import matrix.MatrixD;                          <br>
     * import java.util.Random(); <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
     * MatrixD msum = matr1.multCUR(matr2); </ul>
     * } </ul> } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
     * записываются в <tt>msum</tt>.
     */
    public MatrixD multCUR(MatrixD b, Ring ring) { // классическое умножение
        int deg = M.length;
        Element[][] res = new Element[deg][deg];
        if (deg > 2) {
            deg = deg >>> 1;

            MatrixD t1 = new MatrixD(new Element[deg][deg]);
            MatrixD t2 = new MatrixD(new Element[deg][deg]);
            MatrixD t11 = new MatrixD(new Element[deg][deg]);
            MatrixD t21 = new MatrixD(new Element[deg][deg]);
            MatrixD t12 = new MatrixD(new Element[deg][deg]);
            MatrixD t22 = new MatrixD(new Element[deg][deg]);

            MatrixD n11 = new MatrixD(new Element[deg][deg]);
            MatrixD n12 = new MatrixD(new Element[deg][deg]);
            MatrixD n21 = new MatrixD(new Element[deg][deg]);
            MatrixD n22 = new MatrixD(new Element[deg][deg]);

            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    t11.M[i][j] = M[i][j];
                    t12.M[i][j] = b.M[i][j];
                    t21.M[i][j] = M[i][j + deg];
                    t22.M[i][j] = b.M[i + deg][j];
                }
            }
            t1 = t11.multCUR(t12, ring);
            t2 = t21.multCUR(t22, ring);
            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    n11.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
                }
            }

            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    t11.M[i][j] = M[i][j];
                    t12.M[i][j] = b.M[i][j + deg];
                    t21.M[i][j] = M[i][j + deg];
                    t22.M[i][j] = b.M[i + deg][j + deg];
                }
            }
            t1 = t11.multCUR(t12, ring);
            t2 = t21.multCUR(t22, ring);
            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    n12.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
                }
            }

            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    t11.M[i][j] = M[i + deg][j];
                    t12.M[i][j] = b.M[i][j];
                    t21.M[i][j] = M[i + deg][j + deg];
                    t22.M[i][j] = b.M[i + deg][j];
                }
            }
            t1 = t11.multCUR(t12, ring);
            t2 = t21.multCUR(t22, ring);
            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    n21.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
                }
            }
            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    t11.M[i][j] = M[i + deg][j];
                    t12.M[i][j] = b.M[i][j + deg];
                    t21.M[i][j] = M[i + deg][j + deg];
                    t22.M[i][j] = b.M[i + deg][j + deg];
                }
            }
            t1 = t11.multCUR(t12, ring);
            t2 = t21.multCUR(t22, ring);
            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    n22.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
                }
            }

            for (int i = 0; i < deg; i++) {
                for (int j = 0; j < deg; j++) {
                    res[i][j] = n11.M[i][j];
                    res[i][j + deg] = n12.M[i][j];
                    res[i + deg][j] = n21.M[i][j];
                    res[i + deg][j + deg] = n22.M[i][j];
                }
            }
        } else {
            res[0][0] = (M[0][0].multiply(b.M[0][0], ring))
                    .add(M[0][1].multiply(b.M[1][0], ring), ring);
            res[0][1] = (M[0][0].multiply(b.M[0][1], ring))
                    .add(M[0][1].multiply(b.M[1][1], ring), ring);
            res[1][0] = (M[1][0].multiply(b.M[0][0], ring))
                    .add(M[1][1].multiply(b.M[1][0], ring), ring);
            res[1][1] = (M[1][0].multiply(b.M[0][1], ring))
                    .add(M[1][1].multiply(b.M[1][1], ring), ring);
        }
        return new MatrixD(res, b.fl|fl);

    }

    /**
     * Вычисление хар. полинома матрицы this, используя алгоритм Сейфуллина, не
     * содержит делений и сокращений
     *
     * @return Element[] - коэффициенты характеристического полинома, начиная с
     * коэффициента перед x^n и заканчивая свободным членом
     *
     * @author Pereslavtseva
     */
    public Element[] characteristicPolynom(Ring ring) {
        charPolynomMatrixD q = new charPolynomMatrixD(this);
        return q.charPolSeifullin(ring);
    }

    /**
     * Вычисление хар. полинома матрицы this, используя алгоритм Сейфуллина, не
     * содержит делений и сокращений
     *
     * @return Polynom - характеристического полином от одной переменной с
     * коэффициентами Element
     *
     * @author Pereslavtseva
     */
    public Polynom characteristicPolynomP(Ring ring) {
        charPolynomMatrixD q = new charPolynomMatrixD(this);
        return q.charPolSeifullinP(ring);
    }



    /**
     * Обращение знака матрицы. Создается новая матрица.
     *
     * @return матрица, полученная обращением знака у каждого элемента
     */
    @Override
    public MatrixD negate(Ring ring) {
        int n = M.length;
        int k = M[0].length;
        Element[][] negM = new Element[n][k];
        Element[] strM = new Element[k];
        for (int i = 0; i < n; i++) {
            Element[] str_negM = new Element[k];
            strM = M[i];
            for (int j = 0; j < k; j++) {
                str_negM[j] = strM[j].negate(ring);
            }
            negM[i] = str_negM;

        }
        return new MatrixD(negM,fl);
    }

    /**
     * Копирование матрицы (создание дубликата)
     *
     * @return - дубликат матрицы
     */
    public MatrixD copy() {
        int n = M.length;
        int k = M[0].length;
        Element[][] copyM = new Element[n][k];
        for (int i = 0; i < n; i++) {
            Element[] strM = M[i];
            Element[] str_copyM = new Element[k];
            System.arraycopy(strM, 0, str_copyM, 0, k);
            copyM[i] = str_copyM;
        }
        return new MatrixD(copyM,fl);
    }

    /**
     * Вычисление значения полиномиальной матрицы в точке point по модулю p
     *
     * @return long[][]
     */
    public long[][] valueOf(NumberZp32[] point, long p) {
        if (M[0][0] instanceof Polynom) {
            int n = M.length;
            int k = M[0].length;
            long[][] A = new long[n][k];
            Ring ring = Ring.ringR64xyzt; //new Ring ("Zp32");
            ring.MOD32 = p;
            for (int i = 0; i < n; i++) {
                Polynom[] str = (Polynom[]) M[i];
                long[] str1 = new long[k];
                for (int j = 0; j < k; j++) {
                    str1[j] = (str[j].value(point, ring)).longValue();
                }
                System.arraycopy(str1, 0, A[i], 0, k);
            }
            return A;
        } else {
            return null;
        }
    }
    /**
     * Make int-matrix from this matrix
     * @return int[][]
     */
    public int[][] toIntMatrix() {
        int[][] newM = new int[M.length][];
        for (int i = 0; i < newM.length; i++) {
            newM[i] = new int[M[i].length];
            for (int j = 0; j < newM[i].length; j++) {
                newM[i][j] = M[i][j].intValue() ;
            }
        }
        return  newM;
    }

    @Override
    public Element toNewRing(int numAlgebra, Ring ring) {
        Element[][] newM = new Element[M.length][];
        for (int i = 0; i < newM.length; i++) {
            newM[i] = new Element[M[i].length];
            for (int j = 0; j < newM[i].length; j++) {
                newM[i][j] = M[i][j].toNewRing(numAlgebra, ring);
            }
        }
        return new MatrixD(newM,fl);
    }

    @Override
    public Element value(Element[] point, Ring ring) {
      Element[][] U=new Element[M.length][M[0].length];
       for(int i=0;i<M.length;i++){
            for(int j=0;j<M[0].length;j++){
           U[i][j]=M[i][j].value(point, ring);
       }}
       return new MatrixD(U);
    }
    /**
     * Вычисление значения полиномиальной матрицы в точке point по модулю p
     *
     * @return long[][]
     */
    public long[][] valueOf(NumberZp32[] point, Ring r) {
        //System.out.println("ring = "+ r.toString());
        if ((M[0][0] instanceof Polynom)) // && (M[0][1] instanceof Polynom) &&                (M[1][0] instanceof Polynom) &&(M[1][1] instanceof Polynom ))
        {
            int n = M.length;
            int k = M[0].length;
            long[][] A = new long[n][k];
            //Ring ring = Ring.defaultR64Ring; //new Ring ("Zp32");
            long p = r.MOD32;
            for (int i = 0; i < n; i++) {
                //-Polynom []str = (Polynom[]) M[i];
                //-long [] str1= new long [k];
                for (int j = 0; j < k; j++) {
                    //System.out.println("M["+i+"]["+j+"] "+M[i][j]+",  point= {"+point[0]+"}");
                    A[i][j] = (M[i][j].value(point, r)).longValue();
//                    Element q = M[i][j];
//                    A[i][j] = (q instanceof Polynom)? (q.value(point, r)).longValue():
//                            ((NumberZp32) q).longValue();
                }
                //-System.arraycopy(str1, 0, A[i], 0, k);
            }
            return A;
        }
        if ((M[0][0] instanceof NumberZp32)) {
            int n = M.length;
            int k = M[0].length;
            long[][] A = new long[n][k];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k; j++) {
                    A[i][j] = M[i][j].longValue();
                }
            }
            return A;
        }
        if ((M[0][0] instanceof NumberZ)) {
            int n = M.length;
            int k = M[0].length;
            long[][] A = new long[n][k];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k; j++) {
                    A[i][j] = (M[i][j].Mod(new NumberZ(r.MOD32), r)).longValue();
                }
            }
            return A;
        }
        return null;

    }

    @Override
    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Считаем что матрицы прямоугольные
     *
     * @param x
     * @param ring
     *
     * @return
     */
    @Override
    public int compareTo(Element x, Ring ring) {
        if (x.numbElementType() != Ring.MatrixD) {
            return (x.numbElementType() > Ring.MatrixD) ? -1 : 1;
        }
        int sizeThis = M.length;
        int sizeX = ((MatrixD) x).M.length;
        if (sizeThis != sizeX) {
            return (sizeX > sizeThis) ? -1 : 1;
        }
        int colNum = M[0].length;
        int colNumX = ((MatrixD) x).M[0].length;
        if (colNum != colNumX) {
            return (colNumX > colNum) ? -1 : 1;
        }
        int flag;
        for (int i = 0; i < sizeThis; i++) {
            for (int j = 0; j < M[i].length; j++) {
                flag = M[i][j].compareTo(((MatrixD) x).M[i][j], ring);
                if (flag != 0) {
                    return flag;
                }
            }
        }
        return 0;
    }
    
    @Override
    public boolean isItNumber() {return false;}
    
    @Override
    public Boolean isZero(Ring ring) {
        Boolean f = true;
        for (int i = 0; i < this.M.length; i++) {
            for (int j = 0; j < this.M.length; j++) {
                if (!(this.M[i][j].equals(ring.numberZERO, ring))) {
                    f = false;
                    return f;
                }
            }
        }
        return f;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Element x, Ring r) {
        MatrixD matS = (MatrixD) x;
        return (subtract(matS, r)).isZero(r);

    }

    @Override
    public Boolean isOne(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object clone() {
     Element[][]   tempM = new Element[this.M.length][this.M[0].length];
        for (int i = 0; i < tempM.length; i++) {
            for (int j = 0; j < tempM[i].length; j++) {
                tempM[i][j] = (Element) this.M[i][j].clone();
            }
        }
        return new MatrixD(tempM);
    }

    @Override
    public MatrixD expand(Ring ring) {
        for (Element[] M1 : M) {
            for (int j = 0; j < M1.length; j++) {
                M1[j] = M1[j].expand(ring);
            }
        }
        return this;
    }

     /**
     * Интеграл по первой переменной
     * @param r Ring
     * @return 
     */
    @Override
    public Element integrate(Ring r) {
        return  integrate(0, r);
    }
     /**
     * Интеграл по  переменной num
     * @param num - variable of integration number in Ring
     * @param ring - Ring
     * @return matrix with integrals of all its elements
     */
    @Override
    public Element integrate(int num, Ring ring) {
        for (Element[] M1 : M) {
            for (int j = 0; j < M1.length; j++) {
                M1[j] = M1[j].integrate(num, ring);
            }
        }
        return this;
    }

    @Override
    public Element D(int num, Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].D(num, ring).expand(ring);
            }
        }
        return this;
    }
   
    @Override
    public Element factorLnExp(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].factorLnExp(ring);
            }
        }
        return this;
    }

    @Override
    public Element expandLn(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].expandLn(ring);
            }
        }
        return this;
    }

    @Override
    public Element factor(Ring ring) {
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                M[i][j] = M[i][j].ExpandFnameOrId().factor(ring);
            }
        }
        return this;
    }

    /**
     * Из матрицы копируем блок, начиная с строки begs(вклчительно), до строки
 ends(не включая), от столбца col1(включая) до столбца endc(не включая).
     *
     * @param row1 nmber of the first row (start from 0)
     * @param rowsNumb number of rows
     * @param col1 nmber of the first column (start from 0)
     * @param colNumber number of columns
     * @param ring кольцо
     * @return
     */
    public MatrixD submatrix(int row1, int rowsNumb, int col1, int colNumber, Ring ring) {

       Element[][] MatrM = new Element[rowsNumb][colNumber];
        for (int i = row1; i < row1+rowsNumb; i++) {
            System.arraycopy(M[i], col1, MatrM[i - row1], 0, colNumber);
        }
        return  new MatrixD(MatrM ,fl);
    }

    /**
     * Составляем матрицу из элементов матрицы this, вырожденность которой нужно
     * проверить для посторения матрицы К.
     *
     * @param k шаг
     * @param ring кольцо
     *
     * @return
     */
    /**
     * Вычисление определителя матрицы для this, используя алгоритм Сейфуллина,
     * не содержит делений и сокращений
     *
     * @return Element - determinant
     *
     * @author Pereslavtseva
     */
    public Element det(Ring ring) {
        charPolynomMatrixD q = new charPolynomMatrixD(this);
        return q.detSeifullin(ring);
    }

    public long[][] matrixPartForCharPolKrilov(int k, Ring ring) {
        long[][] C = new long[M.length / k][M.length / k];
        int ii = 0;
        for (int i = k - 1; i < M.length; i += k) {
            int jj = 0;
            for (int j = k - 1; j < M.length; j += k) {
                C[ii][jj] = Long.valueOf(M[i][j].toString(ring));
                jj++;
            }
            ii++;
        }
        return C;
    }

    /**
     * След матрицы
     *
     * @param ring кольцо или тропическое полукольцо
     *
     * @return след матрицы
     */
    public Element track(Ring ring) {
        Element tr = ring.numberZERO;
        for (int i = 0; i < M.length; i++) {
            tr = tr.add(M[i][i], ring);
        }
        return tr;
    }

    public void del(int[] k, Ring ring) {
        Element[][] S = new Element[M.length - k.length][M.length];
        int c = 0;
        int b = 0;
        for (int i = 0; i < M.length; i++) {
            if (c < k.length && i == k[c]) {
                c++;
                continue;
            }
            System.arraycopy(M[i], 0, S[b], 0, M[i].length);
            b++;
        }
        for (int i = 0; i < S.length; i++) {
            S[i] = Array.delColumnsForMatrixD(S[i], k, ring);
        }
        M = S;
    }

    /**
     * Удаление из матрицы i-ой строки и j-ого столбца Вычисление всевозможных
     * миноров матрицы
     *
     * @param i номер строк j номер столбца
     * @param ring кольцо
     *
     *
     * @return
     */
    public MatrixD Minor(int i, int j, Ring ring) {
        Element[][] S = new Element[M.length - 1][M.length - 1];
        int b = 0;
        for (int k = 0; k < M.length; k++) {
            if (k == i) {
                continue;
            }
            System.arraycopy(M[k], 0, S[b], 0, j);
            System.arraycopy(M[k], j + 1, S[b], j, M.length - j - 1);

            b++;
            //}
        }
        return new MatrixD(S,fl);
    }

    public static void toString(Element[][] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println("s" + Arrays.toString(a[i]));
        }

    }

    /**
     * Вычисляет величину Tr A = tr A + tr A^2 + ... + tr A^n
     *
     * @param ring кольцо или тропическое полукольцо
     *
     * @return Tr A
     */
    public Element Tr(Ring ring) {
        Element rez = this.track(ring);
        MatrixD A1 = this.copy();
        for (int m = 2; m <= M.length; m++) {
            A1 = this.multCU(A1, ring);
            Element tr1 = A1.track(ring);
            rez = rez.add(tr1, ring);
        }
        return rez;
    }

    public MatrixD multiplyByScalar(Element e, Ring r) {
        Element[][] S = new Element[M.length][M[0].length];
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < S[i].length; j++) {
                S[i][j] = M[i][j].multiply(e, r);
            }
        }
        return new MatrixD(S,fl);
    }

    public VectorS multiplyByColumn(VectorS e, Ring r) {
        int ffl=((fl>0)||(e.fl<-1))? -2:-1;
         VectorS vv= new VectorS(multiplyByColumn(e.V,r),ffl);
         return  vv;
    }
    
    public Element[] multiplyByColumn(Element[] V, Ring r) {
        Element[] res = new Element[M[0].length];
        for (int i = 0; i < res.length; i++) res[i] = r.numberZERO;
        for (int j = 0; j < M.length; j++) {
            for (int i = 0; i < M[j].length; i++) {
                res[j] = res[j].add(M[j][i].multiply( V[i], r), r);
            }
        }
        return  res;
    }

    @Override
    public Element add(Element e, Ring r) {
        switch (e.numbElementType()) {
            case Ring.MatrixD:
                return add((MatrixD) e, r);
            case Ring.MatrixS:
                return new MatrixS(this, r).add(e, r); // пока только так(
            default:
                return null;
        }
    }

    @Override
    public Element subtract(Element e, Ring r) {
        switch (e.numbElementType()) {
            case Ring.MatrixD:
                return subtract((MatrixD) e, r);
            case Ring.MatrixS:
                return new MatrixS(this, r).subtract(e, r); // пока только так(
            default:
                return null;
        }
    }

    public Element multiply(VectorS e, Ring r) { 
        if(e.fl<0) return multiplyByColumn((VectorS) e, r);
        else {r.exception.append(" Matrix-by-row-multiplication-forbidden "); return null;}
    }
    
    @Override
    public Element multiply(Element e, Ring r) {
        switch (e.numbElementType()) {
            case Ring.MatrixD:
                return multCU((MatrixD) e, r);
            case Ring.VectorS:
                if(((VectorS)e).fl<0) return multiplyByColumn(((VectorS) e), r);
                else {r.exception.append(" Matrix-by-row-multiplication-forbidden "); return null;}
            case Ring.MatrixS:
                return new MatrixS(this, r).multiply(e, r); // пока только так(
            default:
                return multiplyByScalar(e, r);
        }
    }

    public Element putElement(Element e, int i, int j) {
        Element[][] newM = M.clone();
        newM[i][j] = e;
        return new MatrixD(newM);
    }

    public Element getElement(int i, int j) {
        return M[i][j];
    }

    public int rowNum() {
        return M.length;
    }

    public int colNum() {
        return M[0].length;
    }

    @Override
    public int numbElementType() {
        return Ring.MatrixD;
    }

    /**
     * Возвращаем матрицу знаков входящих в нее элементов( F.e.
     * A=[[-3,5],[0,-x]]; ---> [[-1,1],[0,-1]] )
     *
     * @param ring кольцо
     *
     * @return
     */
    public MatrixD signum(Ring ring) {
        Element[][] signumM = new Element[M.length][];
        int signumEl;
        for (int i = 0; i < M.length; i++) {
            signumM[i] = new Element[M[i].length];
            for (int j = 0; j < signumM[i].length; j++) {
                signumEl = M[i][j].signum();
                signumM[i][j] = (signumEl == 1) ? ring.numberONE : (signumEl == 0) ? ring.numberZERO : ring.numberMINUS_ONE;
            }
        }
        return new MatrixD(signumM,0);
    }

    /**
     * Вычисляем модули входящих в матрицу элементов.... результат матрица.
     *
     * @param ring
     *
     * @return
     */
    @Override
    public Element abs(Ring ring) {
        Element[][] absM = new Element[M.length][];
        for (int i = 0; i < M.length; i++) {
            absM[i] = new Element[M[i].length];
            for (int j = 0; j < absM[i].length; j++) {
                absM[i][j] = M[i][j].abs(ring);
            }
        }
        return new MatrixD(absM,fl);
    }

    @Override
    public Element Factor(boolean doNewVector, Ring ring) {
        Element[][] absM = new Element[M.length][];
        for (int i = 0; i < M.length; i++) {
            absM[i] = new Element[M[i].length];
            for (int j = 0; j < absM[i].length; j++) {
                absM[i][j] = M[i][j].Factor(false, ring);
            }
        }
        return new MatrixD(absM,fl);
    }

    @Override
    public Element Expand(Ring ring) {
        Element[][] absM = new Element[M.length][];
        for (int i = 0; i < M.length; i++) {
            absM[i] = new Element[M[i].length];
            for (int j = 0; j < absM[i].length; j++) {
                absM[i][j] = M[i][j].Expand(ring);
            }
        }
        return new MatrixD(absM,fl);
    }

    @Override
    public Element closure(Ring ring) {
        return new MatrixD(new MatrixS(M, ring).closure( ring), ring, fl);
       // return oneMatrixD(M.length, M[0].length, ring).subtract(inverse(ring), ring).inverse(ring);
    }

    @Override
    public Element inverse(Ring ring) {
        return new MatrixD(new MatrixS(M, ring).inverse(ring.numberONE, ring), ring, fl);
    }

    @Override
    public Element conjugate(Ring ring) {
        if (ring.numberONE instanceof Complex) {
            Element[][] newM = new Element[M[0].length][M.length];
            for (int i = 0; i < M.length; i++) {
                for (int j = 0; j < M[i].length; j++) {
                    newM[j][i] = M[i][j].conjugate(ring);
                }
            }
            return new MatrixD(newM, fl);
        } else {
            return transpose(ring);
        }
    }

    public Element min(Ring r) {
        int i = 0, n = M.length;
        int ma = M[0].length;
        Element min = M[0][0].abs(r);
        Element m;
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < ma; k++) {
                m = M[j][k];
                if (m.abs(r).compareTo(min, r) == -1) {
                    min = m.abs(r);
                }
            }
        }
        return min;
    }

    public Element max(Ring r) {
        int i = 0, n = M.length;
        int ma = M[0].length;
        Element max = M[0][0];
        Element m;
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < ma; k++) {
                m = M[j][k];
                if (m.abs(r).compareTo(max, r) == 1) {
                    max = m.abs(r);
                }
            }
        }
        return max;
    }
    public static Element[] rotateLeft(Element[] row){
      Element[] newRow=new Element[row.length];
      newRow[row.length-1]=row[0];
      for (int i = 0; i < row.length-1; i++)
            newRow[i]=row[i+1];
      return newRow;
    }
   /**
    * Rotate right if numb>0, rotate left if numb<0
    * @param row
    * @param numb -- value of ticks for the right movings 
    *                (negativ number -- for the left movings )
    * @return 
    */
   public static Element[] rotate(Element[] row, int numb){
       Element[] newRow=row; int n=0;
       while(n<numb){newRow=rotateRight(newRow); n++;}
       while(n>numb){newRow=rotateLeft(newRow); n--;} 
       return newRow;
   }
   
   public static Element[] rotateRight(Element[] row){
      Element[] newRow=new Element[row.length];
      newRow[0]=row[row.length-1];
        for (int i = row.length-1; i >0; i--)
            newRow[i]=row[i-1];
      return newRow;
    }
 
   public MatrixD siftLeft( int s){MatrixD W=copy();
        Array.siftLeft(W.M, s); return W;}
   
    public MatrixD siftRight( int s){MatrixD W=copy();
        Array.siftRight(W.M, s); return W;}
      /**
    * Building of Akritas matrix for polynomial p1 and p2
    * @param p1 - first dense polynomial from highest coeffs
    * @param p2 - second dense polynomial from highest coeffs
    * @param int[][] type - {{t1,t2,..,tk},{ m1,m2,..,mk}} -- type(number of pol) and value of moving for each row
    * @param ring - Ring
    * @return  - Akritas matrix of k \times 2max(n1,n2)
    */
    public static MatrixD Akritas(Element[] p1, Element[] p2, int[][] type, Ring ring){
        int n1=p1.length;int n2=p2.length; int m=2*Math.max(n1, n2);
        int n=type[0].length;
        Element[][] e=new Element[n][m];
        Element[] row1=new Element[m];for (int i = 0; i < n1; i++) {row1[i]=p1[i];}
        Element[] row2=new Element[m];for (int i = 0; i < n2; i++) {row2[i]=p2[i];}
        for (int i = n1; i < m; i++) {row1[i]=ring.numberZERO;}
        for (int i = n2; i < m; i++) {row2[i]=ring.numberZERO;}
        if ((type.length&1)!=1)
        {for (int i = 0; i < n; i++) { 
            e[i]=(type[0][i]==1)?rotate(row1,type[1][i]):rotate(row2,type[1][i]);
        }}
        return new MatrixD(e);
    }
   /**
    * Building of Sylvester matrix for polynomial p1 and p2
    * @param p1 - first polynomial
    * @param p2 - second polynomial
    * @param type - 0 classical form of size n1+n2;
    *             - 1 "regular form" of size 2max(n1,n2)
    * @param ring - Ring
    * @return  - Sylvester matrix
    */
    public static MatrixD Sylvester(Polynom p1, Polynom p2, int type, Ring ring){
        if(p1.isZero(ring)||p2.isZero(ring)) return null;
        int var1=p1.powers.length/p1.coeffs.length;
        int var2=p2.powers.length/p2.coeffs.length;
        if(var1!=var2) return null;
      FactorPol fp1=p1.toCoeffsHighestVar(var1);
      FactorPol fp2=p2.toCoeffsHighestVar(var1);
      int n1=fp1.powers[0];
      int n2=fp2.powers[0];
      int n=(type==0)?n1+n2:2*Math.max(n1, n2);
      if(n1<n2){FactorPol fpTemp=fp1; fp1=fp2; fp2=fpTemp; int t=n1; n1=n2; n2=t;}

      Element[][] e=new Element[n][n];
      Element[] m1=new Element[n];
      Element[] m2=new Element[n];
      for (int i = 0; i < n; i++) m1[i]=m2[i]=ring.numberZERO;
      for (int i = 0; i < fp1.powers.length; i++)
            m1[n1-fp1.powers[i]]=fp1.multin[i];
      for (int i = 0; i < fp2.powers.length; i++)
            m2[n2-fp2.powers[i]]=fp2.multin[i];
      e[0]=m1;
      if(type==0){
        for (int i = 1; i < n2; i++) e[i]=rotateRight(e[i-1]);
        e[n2]=m2;
        for (int i = n2+1; i < n; i++) e[i]=rotateRight(e[i-1]);
      }else{
         for (int i = 2; i < n; i+=2) e[i]=rotateRight(e[i-2]);
         for (int i = 0; i < n1-n2; i++)m2= rotateRight(m2);
         e[1]=m2;
         for (int i = 3; i < n; i+=2) e[i]=rotateRight(e[i-2]);
      }
        return new MatrixD(e);
   }
    
    /**
     * Сложение матриц в Булевой алгебре (если пользователь указал
     * в матрицах числа не принадлежащие множеству {0,1}, то будем считать их =1)
     * @param b матрица
     * @param ring
     * @return
     */
    @Override
    public MatrixD B_OR(Element B, Ring ring){  
        if (!(B instanceof MatrixD)) return null; 
        MatrixD b =(MatrixD) B;
        MatrixD rez = new MatrixD(new Element[b.M.length][b.M.length], b.fl|fl);
        for (int i = 0; i < b.M.length; i++) {
            for (int j = 0; j < b.M.length; j++) 
               rez.M[i][j]=  ((this.M[i][j].isZero(ring)) && (b.M[i][j].isZero(ring)))?
                              NumberZ64.ZERO: NumberZ64.ONE;
        }
        return rez;
    }
    @Override
        public MatrixD B_NOT(Ring ring){     
        MatrixD rez = new MatrixD(new Element[ M.length][M[0].length], fl);
        for (int i = 0; i <  M.length; i++) {
            for (int j = 0; j <  M[0].length; j++) 
               rez.M[i][j]=(M[i][j].isZero(ring))?NumberZ64.ONE: NumberZ64.ZERO;
        }
        return rez;
    }
        

    public Element GCD(Ring ring){Element gcd=NumberZ.ZERO; 
        for (int i = 0; i <  M.length; i++) 
            gcd=NumberZ.arrayGCD(M[i], gcd, ring);
        return gcd;
    }
    /**
     * Умножение матриц в Булевой алгебре (если пользователь указал
     * в матрицах числа не принадлежащие множеству {0,1}, то будем считать их =1)
     * @param b матрица
     * @return
     */
    @Override
    public MatrixD B_AND(Element B, Ring ring){MatrixD b=null;
        if (B instanceof VectorS) b=new MatrixD(((VectorS)B), true, ((VectorS)B).fl );
        else if (B instanceof MatrixD) b=(MatrixD)B;
        int n=M.length; int k=M[0].length; int m=b.M[0].length;
        if(k!=b.M.length){return null;}
        MatrixD rez = new MatrixD(new Element[n][m], fl|b.fl);
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < m; j++) 
            for (int s = 0; s < k; s++){rez.M[i][j]= NumberZ64.ZERO;
                if((!M[i][s].isZero(ring)) && (!b.M[s][j].isZero(ring))){
                    rez.M[i][j]= NumberZ64.ONE; break;}
            }
        }
        return rez;
    }
    
    public MatrixD[] snfAndMatrs(Element[] s) {Ring ring=Ring.ringZxyz; 
        MatrixD x=this;
        MatrixD[] Matrs=new MatrixD[s.length];
        for (int i = 0; i < s.length; i++) {s[i]=NumberZ.ZERO;}
        int n= M.length, m=M[0].length; int k=m; NumberZ mm=new NumberZ(m);
        int numBits=0; 
        while (k!=0){k=k>>1; numBits++;}k++;
        Element gcd0; NumberZ gcd;
        MatrixD VU1;
        int step=0;
        while(true){ 
          if(step!=s.length-1){
            NumberZ[] rendV = new NumberZ[m];
            Element[] rowU=new NumberZ[m];
            Element[] XmV=null;
            Element[] bestU=null, bestV=null;
            int count=0;
            gcd=NumberZ.ZERO;   
            boolean loop=true; int iter=0;
            while(loop){           System.out.println("step and iter==" +step+" "+ iter);  iter++;
                for (int i = 0; i < m; i++)rendV[i]=new NumberZ(numBits, new Random()).subtract(mm);
                XmV=x.multiplyByColumn(rendV, ring);
               System.out.println("XmV="+Array.toString(XmV)+ "rendV="+Array.toString(rendV));
                gcd=(NumberZ)NumberZ.arrayExtendedGCD(XmV, rowU, ring);
               System.out.println(gcd+"   XmV="+Array.toString(XmV)+ "rowU="+Array.toString(rowU));
                if(gcd.signum==0) continue;
                loop=false;
                if (!gcd.equals(NumberZ.ONE)){
                check: for (int i = 0; i < n; i++) {for (int j = 0; j < m; j++) {
                           if (((NumberZ)x.M[i][j]).remainder(gcd).signum!=0){
                               System.out.println(""+i+" "+j+x.M[i][j]+"   "+gcd+"   "+((NumberZ)x.M[i][j]).remainder(gcd)+"  "+Array.toString(rendV)+"===========");
                               loop=true; break check;
               }}}}
            }
            System.out.println("==========="+ (gcd)+"  "+Array.toString(rendV)+ Array.toString(rowU)+ "===========");
            if (gcd.signum==0) return Matrs;   
            MatrixD UU=(new MatrixD(rowU, false).multCU(x, ring)).divideByNumber(gcd, ring);
            System.out.println("UU="+ UU);
           if(!gcd.equals(NumberZ.ONE)){for (int i = 0; i < XmV.length; i++) XmV[i]=XmV[i].divideExact(gcd, ring);}
            VU1= (new MatrixD(XmV , true)). multCU(UU,ring);
            System.out.println("VU1="+VU1);
            x=x.subtract(VU1.multiplyByScalar(gcd, ring), ring);
            System.out.println("xNew="+x);  
        }else{ 
              gcd0=NumberZ.ZERO; 
                            System.out.println("--00-----gcd0=="+gcd0+x);
              gcd0=x.GCD(ring); 
              System.out.println("-11------gcd0=="+gcd0+x);
              VU1= (gcd0.equals(NumberZ.ONE, ring))? x: x.divideByNumber(gcd0, ring);
              gcd=(NumberZ)gcd0;
        }
        Matrs[step]=VU1; s[step]=gcd; step++; System.out.println(step+"=---------------------");
        if (step==s.length) return Matrs;
      }
    }
    
    /** Lagutinski determinant, which was introduced be Mikhail Malih in 2016
     *  The basis of differential ring is: B= [1,y,x,,^2,yx,x^2,y^3,.....] and so on
     * @param n  the order
     * @param D operator of differentiation  f dx+g dy, which represented by VectorS=[f,x,g,y]
     * @param ring - Ring (it must be Q[x,y,....], no less then 2 variables)
     * @return the determinant of the following matrix M:
     *  M[0]= first n element of basis B
     *  M[i][j]=D(M[i-1][j]) for all i>1, j>0
     */
     public static Element detL(int n, VectorS D,  Ring ring){
       int s=0; int m=1;  Element one= ring.numberONE; 
       if (one instanceof Fraction) one= ((Fraction)one).num;
       for (; m <= n; m++) {  s=m*(m+1)/2; if (s>=n)break;}
       Element[] P  =new Element[s];  
       P[0]=one;
       if (ring.varPolynom.length<2){ring.exception.append("Ring has less then 2 variables! "); return null;}
      int j=0;
      all:for(int k= 1; k <= m; k=k+1){ 
           for(int i=0; i <= k; i=i+1) {
             j=j+1; if(j==s)break all;
             P[j]= (k-i==0)? 
                     new Polynom(new int[]{i}, new Element[]{one}):
                 new Polynom(new int[]{i,k-i}, new Element[]{one});
                }
      };
     return detL( n , D, new VectorS (P), ring);
     }
    /** Lagutinski determinant, which was introduced be Mikhail Malih in 2016.
     * @param B The standard basis of differential ring is: B= [1,y,x,,^2,yx,x^2,y^3,.....] and so on.
     *  But the user can put any list of n functions B= [ \phi_1, \phi_2,.. ].
     * @param n  the order of the matrix
     * @param D operator of differentiation  f dx+g dy, which represented by VectorS=[f,x,g,y]
     * @param ring - Ring (it must be Q[x,y,....], no less then 2 variables)
     * @return the determinant of the following matrix M:
     *  M[0]= first n element of basis B
     *  M[i][j]=D(M[i-1][j]) for all i>1, j>0
     */
        public static Element detL(int n, VectorS D, VectorS B,  Ring ring){ Element res=null;
            Element f=D.V[0], g=D.V[2]; int xx=((Polynom)D.V[1]).powers.length-1, yy=((Polynom)D.V[3]).powers.length-1;
            if (n>B.V.length)return null;
            Element[][] A=new Element[n][n];
            for (int i = 0; i < n; i++) {for (int j = 0; j < n; j++) A[i][j]=NumberZ.ZERO;}
            System.arraycopy(B.V, 0, A[0], 0, n);        
            for (int j = 0; j < n; j++) {
                for (int i = 1; i < n; i++) {
                    if(A[i-1][j].isItNumber())break;
                    A[i][j]=A[i-1][j].D(xx, ring).multiply(f, ring).add(
                            A[i-1][j].D(yy, ring).multiply(g, ring), ring);
            }}       
            MatrixD b=new MatrixD(A);
        return b.det(ring);
    }
       /** Take left part of the matrix
         *  M[i][j]=0 for all i>n-j
         * @return  left upper part of this matrix
         */
        public MatrixD  takeLeft(){ int n=M.length;
            int m=Math.min(n , M[0].length );
            Element[][] A=new Element[m][m];
            Element[] B=new Element[m] ; 
            for (int i = 0; i < m; i++) B[i]=NumberZ.ZERO;
            for (int i = 0; i < m; i++) {int w=m-1-i; 
                System.arraycopy(B, w, A[i], w,  1+i ); 
                System.arraycopy(M[i], 0, A[i], 0, w);        
            }
        return new MatrixD(A, this.fl);
    }
    /** Take right part of the matrix
         *  M[i][j]=0 for all j>n-i
         * @return  right low part of this matrix
         */
        public MatrixD  takeRight(){ int n=M[0].length;
            int m=Math.min(n , M.length );
            Element[][] A=new Element[m][m];
            Element[] B=new Element[m] ; 
            for (int i = 0; i < m; i++) B[i]=NumberZ.ZERO;
            for (int i = n-m; i < n; i++) {int w=n-i; 
                System.arraycopy(M[i] , w, A[i], w,   i ); 
                System.arraycopy(B , 0, A[i], 0, w);        
            }
        return new MatrixD(A, this.fl);
    }
    /**
     * Вычисление присоединенной матрицы для this, используя алгоритм
     * Сейфуллина, не содержит делений и сокращений
     *
     * @return MatrixD - присоединенная матрица = this
     *
     *
     * @author Pereslavtseva
     */
    public MatrixD adjoint(Ring ring) {int size=M[0].length;
        MatrixS nn= new MatrixS(M,ring); nn=nn.adjoint(ring);
        Element[][] el=nn.toSquaredArray(size,  ring);
        return new MatrixD(el);
//        charPolynomMatrixD q = new charPolynomMatrixD(this);
//        MatrixD rr=q.adjointSeifullin(ring);
//        return new MatrixD(q.adjointSeifullin(ring),fl);
        
    }
     public Element rank(Ring ring) {
        MatrixS mat=new MatrixS(M,ring); int m=M[0].length; 
        Integer[] sizeOfNullSpase=new Integer[]{0};
        mat=mat.kernel(m, sizeOfNullSpase,   ring); 
        return new NumberZ64(m-sizeOfNullSpase[0]);}
    public Element kernel(Ring ring) {
        MatrixS mat=new MatrixS(M,ring); int m=M[0].length; 
        mat=mat.kernel(M[0].length, ring);  
        System.out.println( "  == KERNEL=="+mat);
        return new MatrixD(mat, false, ring, fl); }
    public Element toEchelonForm(Ring ring) {
        MatrixS mat=new MatrixS(M,ring); int m=M[0].length; 
        mat=mat.toEchelonForm(ring);  
        return new MatrixD(mat, false, ring, fl); }
    public Element inverse(MatrixD A, Ring ring) {return A.inverse(ring); }
    public Element toEchelonForm(MatrixD A, Ring ring) {return A.toEchelonForm(ring); }
    public Element adjoint(MatrixD A, Ring ring) {return A.adjoint(ring); }
    public Element kernel(MatrixD A, Ring ring) { return A.kernel(ring);}
    public Element runk(MatrixD A, Ring ring) { return A.rank(ring);}
    public VectorS takeRow(int m ) { 
       return((m<=M.length)&&(m>0))?   new VectorS( M[m-1], fl):new VectorS(0) ;}
    public VectorS takeRow(MatrixD A,int n ){return A.takeRow(n);}
    public VectorS takeColumn(int m ) { 
          if  (!((m<=M.length)&&(m>0)))return new VectorS(0);
         VectorS v=  new VectorS(M.length); 
         for (int i = 0; i < M.length; i++) {v.V[i]=M[i][m-1];}
         v.setFlag(fl);
         return v;  
        }
    public VectorS takeColumn(MatrixD A,int n ){return A.takeColumn(n);}
    public void setFlag(int flag){fl=flag;};
    
    /** LU-разложение квадратной матрицы.
     * @param ring кольцо
     * @return L и U такие, что this = L*U;
     * @author Сергей Глазков
     */
    public MatrixD[] toLU(Ring ring){
        if(this.M[0].length != this.M.length) throw new NullPointerException("\nУказанная матрица не является квадратной.");
        Element[][] a = this.M;//для удобства обращения к элементам
        int n = a.length;//узнаем размер
        Element[][] u = new Element[n][n], l = new Element[n][n];//инициализируем массивы L и U
        for (int i = 0; i < n; i++)//заполняем их нулями
            for (int j = 0; j < n; j++) {
                u[i][j] = ring.numberZERO;
                l[i][j] = ring.numberZERO;
                if(i == j) l[i][j] = ring.numberONE;
            }
        for (int j = 0; j < n; j++) {
            u[0][j] = a[0][j];
            if(j == 0) continue; 
            l[j][0] = a[j][0].divide(u[0][0], ring);
        }
        for (int i = 1; i < n; i++)
            for (int j = i; j < n; j++) {
                Element res = ring.numberZERO;
                for (int k = 0; k < i; k++)
                    res = res.add(l[i][k].multiply(u[k][j], ring), ring);
                u[i][j] = a[i][j].subtract(res, ring);
                
                if(j == i) continue;
                res = ring.numberZERO;
                for (int k = 0; k < i; k++)
                    res = res.add(l[j][k].multiply(u[k][i], ring), ring);
                l[j][i] = a[j][i].subtract(res, ring).divide(u[i][i], ring);
            }
        MatrixD L = new MatrixD(l);
        MatrixD U = new MatrixD(u);
        return new MatrixD[]{L, U};
    }
    
    /**
     * Тестирование скорости умножения матриц разными алгоритмами
     * @param m кол-во строк
     * @param n кол-во столбцов
     * @param st - кол-во проходов
     * @param mod - макс. число по модулю
     * @return матрица с элементами, показывающими время выполнения умножения
     * Каждая строка - один проход, последняя строка - среднее время
     * 1 столбец - обычное умножение, 2 столбец - алг-м Штрассена,
     * 3 столбец - алг-м Штрассена 2, 4 - алг-м Винограда-Штрассена
     * @author Глазков Сергей
     */
    public static void Test(int m, int n, int st, int mod, Ring ring){
        long tick; 
        double time, time1 = 0, time2 = 0, time3 = 0, time4 = 0, time5 = 0;
        System.out.println("\nРазмерность: "+m+"x"+n+".");
        System.out.println("\tCU\tPCU\tSTM\tST\tVS");
        MatrixD A;
        MatrixD B;
        for (int j = 0; j < st; j++) {
            A = new MatrixD(m, n, mod, ring);
            B = new MatrixD(m, n, mod, ring);

            System.out.print("П-д "+(j+1)+":\t");

            tick = System.currentTimeMillis();
            A.multCU(B, ring);
            time = System.currentTimeMillis() - tick;
            time1 += time/1000.0;
            System.out.print(String.format("%.1f\t", time/1000.0));
            
            tick = System.currentTimeMillis();
            A.multiplyMatr(B, ring);
            time = System.currentTimeMillis() - tick;
            time2 += time/1000.0;
            System.out.print(String.format("%.1f\t", time/1000.0));
            
            try{
                tick = System.currentTimeMillis();
                //A.multS(B, ring);
                time = System.currentTimeMillis() - tick;
                time3 += time/1000.0;
                System.out.print(String.format("%.1f\t", time/1000.0));
            }
            catch(Exception e){System.out.print("NaN\t");}
            
            tick = System.currentTimeMillis();
            //A.multSR(B, ring);
            time = System.currentTimeMillis() - tick;
            time4 += time/1000.0;
            System.out.print(String.format("%.1f\t", time/1000.0));
            
            tick = System.currentTimeMillis();
            //A.multVSR(B, ring);
            time = System.currentTimeMillis() - tick;
            time5 += time/1000.0;
            System.out.print(String.format("%.1f\t", time/1000.0));
            
            Date date = new Date(System.currentTimeMillis());
            System.out.println(String.format("Проход завершен в %02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds()));
        }
        if(time2 == 0) time2 = -st;
        time1 /= st; time2 /= st; time3 /= st; time4 /= st; time5 /= st;
        System.out.println(String.format("Средн.:\t%.1f\t%.1f\t%.1f\t%.1f\t%.1f\t", 
                                            time1, time2, time3, time4, time5));
    }
    
    
    /** Выполняет умножение методом Штрассена для прямоугольных матриц
     * @param b на что умножаем
     * @param ring кольцо
     * @return this * b
     * @author Сергей Глазков
     */
    public MatrixD multSS(MatrixD b, Ring ring){
        if(this.M[0].length != b.M.length) throw new NullPointerException("\nУмножение указанных матриц невыполнимо из-за несоответствия размеров.");
        
        int m_size = this.M.length, n_size = b.M[0].length;
        int size = Math.max(Math.max(this.M.length, this.M[0].length), Math.max(b.M.length, b.M[0].length));
        for (int i = 0; i < size; i++)//находим ближайшую степень двойки
            if(Math.pow(2, i) >= size){
                size = (int)Math.pow(2, i); 
                break;
            }
        Element[][] n1 = new Element[size][size], n2 = new Element[size][size];
        for (int i = 0; i < size; i++)//доводим обе матрицы до нового размера (степень двойки)
            for (int j = 0; j < size; j++) {
                if(i < this.M.length && j < this.M[i].length) n1[i][j] = this.M[i][j];
                else n1[i][j] = ring.numberZERO;//новые элементы заполняем нулями
                
                if(i < b.M.length && j < b.M[i].length) n2[i][j] = b.M[i][j];
                else n2[i][j] = ring.numberZERO;
            }
        MatrixD a = new MatrixD(n1);
        b = new MatrixD(n2);//объявляем новые матрицы
        a = a.multSR(b, ring);//вызываем рекурсивное умножение алгоритмом Винограда-Штрассена
        Element[][] n = new Element[m_size][n_size];
        for (int i = 0; i < m_size; i++)//сжимаем матрицу до нужного размера
            System.arraycopy(a.M[i], 0, n[i], 0, n_size);
        return new MatrixD(n);
    }
    /** Умножение квадратных матриц с размером 2^n методом Штрассена
     * @param b на что умножаем
     * @param ring кольцо
     * @return this * b
     * @author Сергей Глазков
     */
    public MatrixD multSR(MatrixD b, Ring ring){
        if(this.M.length <= 64) return this.multCU(b, ring);
        
        MatrixD A11 = split(this)[0];
        MatrixD A12 = split(this)[1];
        MatrixD A21 = split(this)[2];
        MatrixD A22 = split(this)[3];
        
        MatrixD B11 = split(b)[0];
        MatrixD B12 = split(b)[1];
        MatrixD B21 = split(b)[2];
        MatrixD B22 = split(b)[3];
        
        MatrixD P1 = (A11.add(A22, ring)).multSR(B11.add(B22, ring), ring);
        MatrixD P2 = A21.add(A22, ring).multSR(B11, ring);
        MatrixD P3 = A11.multSR(B12.subtract(B22, ring), ring);
        MatrixD P4 = A22.multSR(B21.subtract(B11, ring), ring);
        MatrixD P5 = A11.add(A12, ring).multSR(B22, ring);
        MatrixD P6 = (A21.subtract(A11, ring)).multSR(B11.add(B12, ring), ring);
        MatrixD P7 = (A12.subtract(A22, ring)).multSR(B21.add(B22, ring), ring);
        
        MatrixD C11 = P1.add(P4, ring).subtract(P5, ring).add(P7, ring);
        MatrixD C12 = P3.add(P5, ring);
        MatrixD C21 = P2.add(P4, ring);
        MatrixD C22 = P1.subtract(P2, ring).add(P3, ring).add(P6, ring);
        
        int size = C11.M.length;
        Element[][] n = new Element[2*size][2*size];
        for (int i = 0; i < 2*size; i++)//собираем четыре матрицы в одну
            for (int j = 0; j < 2*size; j++) {
                if(i < size && j < size) {n[i][j] = C11.M[i][j]; continue;}
                if(i < size && j >= size){n[i][j] = C12.M[i][j-size]; continue;}
                if(i >= size && j < size){n[i][j] = C21.M[i-size][j]; continue;}
                if(i >= size && j >= size){n[i][j] = C22.M[i-size][j-size]; continue;}
            }
        return new MatrixD(n);
    }
    
    
    /** Умножение матриц методом Винограда-Штрассена
     * @param b множитель
     * @param ring кольцо
     * @return this * b
     * @author Сергей Глазков
     */
    public MatrixD multVS(MatrixD b, Ring ring){
        if(this.M[0].length != b.M.length) throw new NullPointerException("\nУмножение указанных матриц невыполнимо из-за несоответствия размеров.");
        
        int m_size = this.M.length, n_size = b.M[0].length;
        int size = Math.max(Math.max(this.M.length, this.M[0].length), Math.max(b.M.length, b.M[0].length));
        for (int i = 0; i < size; i++)//находим ближайшую степень двойки
            if(Math.pow(2, i) >= size){
                size = (int)Math.pow(2, i); 
                break;
            }
        Element[][] n1 = new Element[size][size], n2 = new Element[size][size];
        for (int i = 0; i < size; i++)//доводим обе матрицы до нового размера (степень двойки)
            for (int j = 0; j < size; j++) {
                if(i < this.M.length && j < this.M[i].length) n1[i][j] = this.M[i][j];
                else n1[i][j] = ring.numberZERO;//новые элементы заполняем нулями
                
                if(i < b.M.length && j < b.M[i].length) n2[i][j] = b.M[i][j];
                else n2[i][j] = ring.numberZERO;
            }
        MatrixD a = new MatrixD(n1);
        b = new MatrixD(n2);//объявляем новые матрицы
        a = a.multVSR(b, ring);//вызываем рекурсивное умножение алгоритмом Винограда-Штрассена
        Element[][] n = new Element[m_size][n_size];
        for (int i = 0; i < m_size; i++)//сжимаем матрицу до нужного размера
            System.arraycopy(a.M[i], 0, n[i], 0, n_size);
        return new MatrixD(n);
    }
    /** Умножение квадратных матриц размера 2^n методом Винограда-Штрассена
     * @param b на что умножаем
     * @param ring кольцо
     * @return this * b
     * @author Сергей Глазков
     */
    public MatrixD multVSR(MatrixD b, Ring ring){
        if(this.M.length <= 64) return this.multCU(b, ring);
        
        MatrixD A11 = split(this)[0];
        MatrixD A12 = split(this)[1];
        MatrixD A21 = split(this)[2];
        MatrixD A22 = split(this)[3];
        
        MatrixD B11 = split(b)[0];
        MatrixD B12 = split(b)[1];
        MatrixD B21 = split(b)[2];
        MatrixD B22 = split(b)[3];
        
        MatrixD P1 = A21.add(A22, ring).subtract(A11, ring).multVSR(B22.subtract(B12.subtract(B11, ring), ring), ring);
        MatrixD P2 = A11.multVSR(B11, ring);
        MatrixD P3 = A12.multVSR(B21, ring);
        MatrixD P4 = A11.subtract(A21, ring).multVSR(B22.subtract(B12, ring), ring);
        MatrixD P5 = A21.add(A22, ring).multVSR(B12.subtract(B11, ring), ring);
        MatrixD P6 = A12.subtract(A21.add(A22, ring).subtract(A11, ring), ring).multVSR(B22, ring);
        MatrixD P7 = A22.multVSR(B22.subtract(B12.subtract(B11, ring), ring).subtract(B21, ring), ring);
        
        MatrixD C11 = P2.add(P3, ring);
        MatrixD C12 = P1.add(P2, ring).add(P5, ring).add(P6, ring);
        MatrixD C21 = P1.add(P2, ring).add(P4, ring).subtract(P7, ring);
        MatrixD C22 = P1.add(P2, ring).add(P4, ring).add(P5, ring);
        
        
        int size = C11.M.length;
        Element[][] n = new Element[2*size][2*size];
        for (int i = 0; i < 2*size; i++)//собираем четыре матрицы в одну
            for (int j = 0; j < 2*size; j++) {
                if(i < size && j < size) {n[i][j] = C11.M[i][j]; continue;}
                if(i < size && j >= size){n[i][j] = C12.M[i][j-size]; continue;}
                if(i >= size && j < size){n[i][j] = C21.M[i-size][j]; continue;}
                if(i >= size && j >= size){n[i][j] = C22.M[i-size][j-size]; continue;}
            }
        return new MatrixD(n);
    }

    /** Прямое умножение матриц без пробега по столбцам
     * @param b на что умножаем
     * @param ring кольцо
     * @return произведение матриц
     * @author Глазков Сергей
     */
    public MatrixD multiplyMatr(MatrixD b, Ring ring){
        if(this.M[0].length != b.M.length) throw new NullPointerException("\nУмножение указанных матриц невыполнимо из-за несоответствия размеров.");
        int m = this.M.length, n = b.M[0].length;
        Element[][] res = new Element[m][n];//узнаем размер матрицы-результата и инициализируем её массив элементов
        b = b.transpose(ring);//транспонируем матрицу b, чтобы пробегать не столбцы, а строки.
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                Element cur = ring.numberZERO;//элемент i cтроки j столбца результирующей матрицы
                for (int k = 0; k < b.M[0].length; k++)
                    cur = cur.add(M[i][k].multiply(b.M[j][k], ring), ring);
                res[i][j] = cur;
            }
        return new MatrixD(res);
    }

    public   MatrixD inverseLowTriangle(  Ring ring) {
        if (M.length == 2){
            Element a= M[0][0].inverse(ring);
            Element c= M[1][1].inverse(ring);

            Element b=c.multiply(M[1][0].multiply(a, ring), ring).negate(ring);

            Element[][] Arr={{a,ring.numberZERO},{b,c}};

            return new MatrixD(Arr, 0);

        }// end of size 2
        else{
            MatrixD[] blocks = this.splitTo4();
            MatrixD inv0= blocks[0].inverseLowTriangle( ring);
            MatrixD inv4= blocks[3].inverseLowTriangle( ring);
            MatrixD inv3 = inv4.multiplyMatr(blocks[2], ring)
                    .multiplyMatr(inv0, ring).negate(ring);
            return MatrixD.join(new MatrixD[]{inv0, blocks[1],inv3,inv4}) ;
        }
    }


    public   MatrixD inverseLowTriangle4(  Ring ring) {
        if (M.length == 2){
            Element a= M[0][0].inverse(ring);
            Element c= M[1][1].inverse(ring);

            Element b=c.multiply(M[1][0].multiply(a, ring), ring).negate(ring);

            Element[][] Arr={{a,ring.numberZERO},{b,c}};

            return new MatrixD(Arr, 0);

        }// end of size 2
        else{
            MatrixD[] blocks = this.splitTo4();
            MatrixD inv0= blocks[0].inverseLowTriangle4( ring);
            MatrixD inv4= blocks[3].inverseLowTriangle4( ring);
            MatrixD inv3 = inv4.multiply4(blocks[2], ring).
                    multiply4(inv0, ring).negate(ring);
            return MatrixD.join(new MatrixD[]{inv0, blocks[1],inv3,inv4}) ;
        }
    }

    public   MatrixD inverseLowTriangle8(  Ring ring) {
        if (M.length == 2){
            Element a= M[0][0].inverse(ring);
            Element c= M[1][1].inverse(ring);

            Element b=c.multiply(M[1][0].multiply(a, ring), ring).negate(ring);

            Element[][] Arr={{a,ring.numberZERO},{b,c}};

            return new MatrixD(Arr, 0);

        }// end of size 2
        else{
            MatrixD[] blocks = this.splitTo4();
            MatrixD inv0= blocks[0].inverseLowTriangle8( ring);
            MatrixD inv4= blocks[3].inverseLowTriangle8( ring);
            MatrixD inv3 =inv4.multiply8(blocks[2], ring).
                    multiply8(inv0, ring).negate(ring);
            return MatrixD.join(new MatrixD[]{inv0, blocks[1],inv3,inv4}) ;
        }
    }

    public   MatrixD inverseLowTriangleWin(  Ring ring) {
        if (M.length == 2){
            Element a= M[0][0].inverse(ring);
            Element c= M[1][1].inverse(ring);

            Element b=c.multiply(M[1][0].multiply(a, ring), ring).negate(ring);

            Element[][] Arr={{a,ring.numberZERO},{b,c}};

            return new MatrixD(Arr, 0);

        }// end of size 2
        else{
            MatrixD[] blocks = this.splitTo4();
            MatrixD inv0= blocks[0].inverseLowTriangleWin( ring);
            MatrixD inv4= blocks[3].inverseLowTriangleWin( ring);
            MatrixD inv3 =inv4.multiplyStrassWin(blocks[2], ring).
                    multiplyStrassWin(inv0, ring).negate(ring);
            return MatrixD.join(new MatrixD[]{inv0, blocks[1],inv3,inv4}) ;
        }
    }


    public MatrixD[] choleskyFactorize(Ring ring) {

        if (M.length == 1) {
            Element value = this.getElement(0, 0);
            if(value.isNegative()||value.isZero(ring))
            {return new MatrixD[]
                    {MatrixD.zeroMatrix(1),   MatrixD.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixD[]{ new MatrixD(new MatrixS(sqrtVal), ring),
                        new MatrixD(new MatrixS(ring.numberONE.divide(sqrtVal,ring)), ring)};}
        } else {
            MatrixD[] inputMatrixBlocks = this.splitTo4(); //(alpha, beta, beta , gamma)
            MatrixD[] decomposedA = inputMatrixBlocks[0].choleskyFactorize(ring);
            MatrixD bT = decomposedA[1].multiplyMatr(inputMatrixBlocks[1], ring);
            MatrixD b = bT.transpose(ring);
            MatrixD beta = inputMatrixBlocks[3].subtract(b.multiplyMatr(bT, ring), ring);

            MatrixD[] decomposedC = beta.choleskyFactorize(ring);
            MatrixD z = decomposedC[1].multiplyMatr(b, ring)
                    .multiplyMatr(decomposedA[1], ring).negate(ring);
            MatrixD[] LBlocks = new MatrixD[4];
            MatrixD[] LinvBlocks = new MatrixD[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] = zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

            MatrixD L = MatrixD.join(LBlocks);
            MatrixD Linv = MatrixD.join(LinvBlocks);
            return new MatrixD[]{L, Linv};
        }
    }

    public MatrixD[] choleskyFactorize4(Ring ring) {

        if (M.length == 1) {
            Element value = this.getElement(0, 0);
            if(value.isNegative()||value.isZero(ring))
            {return new MatrixD[]
                    {MatrixD.zeroMatrix(1),   MatrixD.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixD[]{ new MatrixD(new MatrixS(sqrtVal), ring),
                        new MatrixD(new MatrixS(ring.numberONE.divide(sqrtVal,ring)), ring)};}
        } else {
            MatrixD[] inputMatrixBlocks = this.splitTo4(); //(alpha, beta, beta , gamma)
            MatrixD[] decomposedA = inputMatrixBlocks[0].choleskyFactorize4(ring);
            MatrixD bT = decomposedA[1].multiply4(inputMatrixBlocks[1], ring);
            MatrixD b = bT.transpose(ring);
            MatrixD beta = inputMatrixBlocks[3].subtract(b.multiply4(bT, ring), ring);

            MatrixD[] decomposedC = beta.choleskyFactorize4(ring);
            MatrixD z = decomposedC[1].multiply4(b, ring).
                    multiply4(decomposedA[1], ring).negate(ring);
            MatrixD[] LBlocks = new MatrixD[4];
            MatrixD[] LinvBlocks = new MatrixD[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] = zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

            MatrixD L = MatrixD.join(LBlocks);
            MatrixD Linv = MatrixD.join(LinvBlocks);
            return new MatrixD[]{L, Linv};
        }
    }

    public MatrixD[] choleskyFactorize8(Ring ring) {

        if (M.length == 1) {
            Element value = this.getElement(0, 0);
            if(value.isNegative()||value.isZero(ring))
            {return new MatrixD[]
                    {MatrixD.zeroMatrix(1),   MatrixD.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixD[]{ new MatrixD(new MatrixS(sqrtVal), ring),
                        new MatrixD(new MatrixS(ring.numberONE.divide(sqrtVal,ring)), ring)};}
        } else {
            MatrixD[] inputMatrixBlocks = this.splitTo4(); //(alpha, beta, beta , gamma)
            MatrixD[] decomposedA = inputMatrixBlocks[0].choleskyFactorize8(ring);
            MatrixD bT = decomposedA[1].multiply8(inputMatrixBlocks[1], ring);
            MatrixD b = bT.transpose(ring);
            MatrixD beta = inputMatrixBlocks[3].subtract(b.multiply8(bT, ring), ring);

            MatrixD[] decomposedC = beta.choleskyFactorize8(ring);
            MatrixD z = decomposedC[1].multiply8(b, ring).multiply8(decomposedA[1], ring).negate(ring);
            MatrixD[] LBlocks = new MatrixD[4];
            MatrixD[] LinvBlocks = new MatrixD[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] = zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

            MatrixD L = MatrixD.join(LBlocks);
            MatrixD Linv = MatrixD.join(LinvBlocks);
            return new MatrixD[]{L, Linv};
        }
    }

    public MatrixD[] choleskyFactorizeWin(Ring ring) {

        if (M.length == 1) {
            Element value = this.getElement(0, 0);
            if(value.isNegative()||value.isZero(ring))
            {return new MatrixD[]
                    {MatrixD.zeroMatrix(1),   MatrixD.zeroMatrix(1)};
            } else { Element sqrtVal=value.sqrt(ring);
                return new MatrixD[]{ new MatrixD(new MatrixS(sqrtVal), ring),
                        new MatrixD(new MatrixS(ring.numberONE.divide(sqrtVal,ring)), ring)};}
        } else {
            MatrixD[] inputMatrixBlocks = this.splitTo4(); //(alpha, beta, beta , gamma)
            MatrixD[] decomposedA = inputMatrixBlocks[0].choleskyFactorizeWin(ring);

            MatrixD bT = decomposedA[1].multiplyStrassWin(inputMatrixBlocks[1], ring);
            MatrixD b = bT.transpose(ring);
            MatrixD beta = inputMatrixBlocks[3].subtract(b.multiplyStrassWin(bT, ring), ring);

            MatrixD[] decomposedC = beta.choleskyFactorizeWin(ring);
            MatrixD z = decomposedC[1].multiplyStrassWin(b, ring).
                    multiplyStrassWin(decomposedA[1], ring).negate(ring);
            MatrixD[] LBlocks = new MatrixD[4];
            MatrixD[] LinvBlocks = new MatrixD[4];
            LBlocks[0] = decomposedA[0];
            LBlocks[1] = zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LBlocks[2] = b;
            LBlocks[3] = decomposedC[0];

            LinvBlocks[0] = decomposedA[1];
            LinvBlocks[1] =zeroMatrixD(decomposedA[0].M.length, decomposedA[0].M.length, ring);
            LinvBlocks[2] = z;
            LinvBlocks[3] = decomposedC[1];

            MatrixD L = MatrixD.joinMatr(LBlocks);
            MatrixD Linv = MatrixD.joinMatr(LinvBlocks);
            return new MatrixD[]{L, Linv};
        }
    }


    public MatrixD multiply4(MatrixD B, Ring ring) {
        if(M.length == 1) return this.multiplyMatr(B, ring);

        MatrixD[] DD = new MatrixD[4];
        MatrixD[] AA = this.splitTo4();
        MatrixD[] BB = B.splitTo4();
        DD[0] = multiplyScalar(AA[0], BB[0], AA[1], BB[2], ring);
        DD[1] = multiplyScalar(AA[0], BB[1], AA[1], BB[3], ring);
        DD[2] = multiplyScalar(AA[2], BB[0], AA[3], BB[2], ring);
        DD[3] = multiplyScalar(AA[2], BB[1], AA[3], BB[3], ring);

        MatrixD CC = MatrixD.join(DD);

        return CC;
    }

    public MatrixD multiplyScalar(MatrixD A, MatrixD B, MatrixD C, MatrixD D, Ring ring) {
        if(A.M.length == 1) return (A.multiplyMatr(B, ring)).add(C.multiplyMatr(D, ring), ring);

        return A.multiply4(B, ring).add(C.multiply4(D, ring), ring);
    }

    public MatrixD multiply8(MatrixD B, Ring ring){
        if(M.length == 1) return this.multiplyMatr(B, ring);

        MatrixD[] AA = this.splitTo4();
        MatrixD[] BB = B.splitTo4();

        MatrixD[] DD = new MatrixD[4];
        DD[0] = AA[0].multiply8(BB[0], ring).add(AA[1].multiply8(BB[2], ring), ring);
        DD[1] = AA[0].multiply8(BB[1], ring).add(AA[1].multiply8(BB[3], ring), ring);
        DD[2] = AA[2].multiply8(BB[0], ring).add(AA[3].multiply8(BB[2], ring), ring);
        DD[3] = AA[2].multiply8(BB[1], ring).add(AA[3].multiply8(BB[3], ring), ring);

        return MatrixD.join(DD);
    }

/**   matrix multiplication according Strassen-Winograd algorithm
 * 
 * @param this - first matrix
 * @param b - second matrix
 * @param r -Ring
 * @return  a * b
 */
    public MatrixD multiplyStrassWin(MatrixD b, Ring r) {
        if (M.length <=32) {return this.multCU(b,r);}
        MatrixD[] splitted_a = this.splitTo4();
        MatrixD[] splitted_b = b.splitTo4();
        MatrixD s1 = splitted_a[2].add(splitted_a[3], r); // a21 + a22
        MatrixD s2 = s1.subtract(splitted_a[0], r); // s1 - a11
        MatrixD s3 = splitted_a[0].subtract(splitted_a[2], r); // a11 - a21
        MatrixD s4 = splitted_a[1].subtract(s2, r); // a12 - s2

        MatrixD s5 = splitted_b[1].subtract(splitted_b[0], r); // b12 - b11
        MatrixD s6 = splitted_b[3].subtract(s5, r); // b22 - s5
        MatrixD s7 = splitted_b[3].subtract(splitted_b[1], r); // b22 - b12
        MatrixD s8 = s6.subtract(splitted_b[2], r); // s6 - b21

        MatrixD p1 = s2.multiplyStrassWin(s6, r); // s2 * s6
        MatrixD p2 = splitted_a[0].multiplyStrassWin(splitted_b[0], r); // a11 * b11
        MatrixD p3 = splitted_a[1].multiplyStrassWin(splitted_b[2], r); // a12 * b21
        MatrixD p4 = s3.multiplyStrassWin(s7, r); // s3 * s7
        MatrixD p5 = s1.multiplyStrassWin(s5, r); // s1 * s5
        MatrixD p6 = s1.multiplyStrassWin(splitted_b[3], r); // s4 * b22
        MatrixD p7 = splitted_a[3].multiplyStrassWin(s8, r); // a22 * s8

        MatrixD T1 = p1.add(p2, r);
        MatrixD T2 = T1.add(p4, r);

        MatrixD C11 = p2.add(p3, r);
        MatrixD C12 = T1.add(p5, r).add(p6, r);
        MatrixD C21 = T2.subtract(p7, r);
        MatrixD C22 = T2.add(p5, r);
        return join(new MatrixD[] {C11, C12, C21, C22});
    }

    /**
     * We compute norm-zero for each cols
     * @param ring ring
     * @return Vector (row) which consists of
     * this matrix norms of each col
     */
    public VectorS colsNorm0(Ring ring) {
        Element[] norm = new Element[M.length];
        for (int i = 0; i < M.length; i++) {
            Element s=M[i][0].abs(ring);
            for (int k = 1; k < M[i].length; k++)
                s=s.add(M[i][k].abs(ring), ring);
            norm[i] = s;
        }
        return new VectorS(norm,fl);
    }

    public static void main(String[] args) {
        //System.out.println("CU - обычное умножение, STM - Штрассен в MathPar, ST - алгоритм Штрассена, VS - алгоритм Винограда-Штрассена, время в секундах.\n");
        Ring ring=new Ring("Z[]"); int kk=4;
        int c = 64, n = 5, m = 1000;//начальный размер матриц и кол-во проходов
        MatrixD a = new MatrixD(kk , kk , 99 , ring);
        MatrixD g = a.takeRight();MatrixD f = a.takeLeft();
         System.out.println("L=="+f);
                  System.out.println("A=="+a);
         System.out.println("R=="+g);
        MatrixD w=  oneInvMatrixD(6, 3, ring);
         System.out.println("W=="+w);

 
    }
}
