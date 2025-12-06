/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix;

import com.mathpar.func.F;
import com.mathpar.func.FvalOf;
import com.mathpar.func.Page;
import com.mathpar.number.Array;
import com.mathpar.number.Complex;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NumberR64;
import com.mathpar.number.NumberZ;
import com.mathpar.number.NumberZ64;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.polynom.FactorPol;
import com.mathpar.polynom.Polynom;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//Задание 1 такое: 
//
//Создать аналог пустой матрицы.
//
//[3172 стр. в классе CanonicForms создается нулевая матрицы MatrixD размера (indexI, indexJ)
//и записывается в переменную "substituteArg[i]"
//команда, которая ее создает A=\O_{i,j};  здесь большая буква O.
//(в 3163 отлавливается "\" в 3164 отлавливаются буквы "O_{"  ) и т.д.]
//
//
//У нас будет: A={}_{e,f}^{g,h}\O_{a,b}^{c,d}.
//Приходит этот текст в это же место. 
//И мы должны создать и отправить нулевой тензор в "substituteArg[i]"
//
//======================================================================
//
//Объект.  У МаtrixD объект -- это Element[][] M;
//
//
//У TensorD объект -- это Element[][]..[][] T; число и порядок аргументов берется из вида
//String s= "{}_{e,f}^{g,h}\O_{a,b}^{c,d}";
//
//Порядок всегда сохраняет такой обход:  ПрНиж-- ПрВерх -- ЛвНиж -- ЛвВерх.
//Тотальное количество индексов может быть любое от 0 до 8.
//Варианты записи:  "{}_{e,f}\O_{a,b}^{c,d}"; "{}_{e,f}^{g,h}\O_{a,b}"; "{}_{e,f}\O_{a,b}";
//"{}_{}^{g,h}\O_{a,b}^{c,d}"; "{}_{e,f}^{g,h}\O_{}^{c,d}"; "{}_{}^{g,h}\O_{}^{c,d}";
// "{}_{}\O_{} " --- это константа  = самая короткая запись 
//----
//Другими словами: первые 2 пары фигурных скобок и первая после символа -- Вечные. Отсальное 2 пары верхние можно и не писать.
//если они пустые.
//если есть ПрВерх  но нет ПрНиж, то на месте ПрНиж должны быть пустые фигурные скобки.
//
//Пусть например пришло "{}_{e,f}^{g,h}\O_{}^{c,d}"  --- что мы делаем? создаем
//TensorD с полем  Element[с][d][e][f][g][h] T;   полем int dimension=6; полем  int[] type= {0,2,2,2};
//
//И заполняем поля T нулями --- точно все так как у МаtrixD заполняется нулями Element[][] M.
//имей ввиду, что общее число элементов a*b*c*d*e*f*g*h;
//=======================================================

/**
 *
 * @author gennadi
 */
public class TensorD extends Element implements Serializable {

    public static Element[][] M;//matrix of Element  - это осталось от MatrixD (потом можно убрать)
    public int fl=0; // флаг отвечающий за наличие в матрице элементов типа функций и символов (0-false)
   // Element[с][d][e][f][g][h] T;   полем 
    public int dimension; // размерность(сумма всех чисел(подразмерностей) в type) 
                         // оно свпадает с числом квадратных скобок T=new Element[][][]..[]
    public int[] type;    // тип, например = {1,1,2,0};
    public Object T;// T= new Element[a][b][c][d] for tensor T_{a,b}^{c,d}
                      //T= new Element[a][c][b][d] for tensor T_{a,0,b}^{c,0,d} which is writen {}_{b}^{d}T_{a}^{c}
    public TensorD(int[] type,  Ring ring){
         this.type=type; dimension=0; 
         for (int i = 0; i < type.length; i++) {dimension=dimension+type[i];}
         switch (dimension) {
            case 0: T=new Element(); break;  
             case 1: T=new Element[]{}; break;  
              case 2: T=new Element[][]{}; break;  
               case 3: T=new Element[][][]{}; break;  
                case 4: T=new Element[][][][]{}; break;  
           case 5: T=new Element[][][][][]{}; break;  
              case 6: T=new Element[][][][][][]{}; break;  
               case 7: T=new Element[][][][][][][]{}; break;  
                case 8: T=new Element[][][][][][][][]{}; break;      
            default:T= null;}
    }

    /**
     * Коструктор от двух мерного массива элементов
     *
     * @param M
     */
   public TensorD(Element[][] M, int ffl) {this.M = M; this.fl=ffl;}
   public TensorD(Element[][] M) {this.M = M;}
   public TensorD(Element[] v, boolean transpose) {
        int n=v.length;
        if(transpose){  M = new Element[n][1];
            for (int i = 0; i < n; i++) 
                M[i][0]=v[i];  
        }else {M = new Element[1][n]; 
               System.arraycopy(v, 0, M[0], 0, n); 
                } 
    }
    public TensorD(VectorS V, boolean tr, int ffl) {fl=ffl;
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
     * Конструктор TensorD от функции F, у которой n аргументов, каждый из
     * которых является функцией от m аргументов. Например:
     * \VectorS(\VectorS(x,y),\VectorS(2x,3y))
     *
     * @param f
     */
    public TensorD(F f) {
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
    public TensorD(MatrixS A, Ring ring) {M = A.toScalarArray(ring);}
    public TensorD(MatrixS A, Ring ring, int ffl) {fl=ffl; M = A.toScalarArray(ring);}
    public TensorD(MatrixS A, boolean toSquaredArray, Ring ring) { 
        M = (toSquaredArray) ? A.toSquaredArray(ring) : A.toScalarArray(ring);
    }
    public TensorD(MatrixS A, boolean toSquaredArray, Ring ring, int ffl) {fl=ffl;
        M = (toSquaredArray) ? A.toSquaredArray(ring) : A.toScalarArray(ring);
    }
    public TensorD(MatrixS A){M = A.toScalarArray(Ring.ringR64xyzt);}
    public TensorD(MatrixS A, int ffl) {fl=ffl;M = A.toScalarArray(Ring.ringR64xyzt);}
   public TensorD(TensorD A, int ffl) {M = A.M; fl=ffl; }

    public TensorD(MatrixS A, boolean toSquaredArray, int  fll) {fl=fll;
        M = (toSquaredArray) ? A.toSquaredArray(Ring.ringR64xyzt) : A.toScalarArray(Ring.ringR64xyzt);
    }

    /**
     * Конструктор матрицы из двумерного плотного массива типа long.
     *
     * @param A входная матрица в ввиде плотного двумерного массива
     */
    public TensorD(long[][] A, Ring ring) {
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
    
    
     public TensorD divideByNumber(Element s, Ring ring) {
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
        return new TensorD(r,fl);
    }
     
     public static TensorD zeroMatrix() {
        return new TensorD(MatrixS.zeroMatrix(),0);
    }
     public static TensorD zeroMatrix(int n) {
        return new TensorD(MatrixS.zeroMatrix(),0);
    }
     
     public static TensorD scalarMatrix(int n, Element a, Ring ring) {
        TensorD ss=zeroMatrix(n);
        if (a.isZero(ring)) return ss;
        Element[][] MM=ss.M;
        for (int i = 0; i < n; i++)  MM[i][1] = a;
        return ss;
    }
//      public TensorD[] split() {
//          int n=0,m=0;
//          TensorD[] res = new TensorD[4];
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
//          res[0] = new TensorD(R1);
//          res[1] = new TensorD(R2);
//          res[2] = new TensorD(R3);
//          res[3] = new TensorD(R4);
//          return res;
//    } 
     public TensorD[] split(TensorD m) {
     TensorD[] res = new TensorD[4];
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
        res[0] = new TensorD(bl,fl);
        bl = new Element[len1][len22];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[i][len2+j];
        res[1] = new TensorD(bl,fl);
        bl = new Element[len12][len2];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[len1+i][j];
        res[2] = new TensorD(bl,fl);
        bl = new Element[len12][len22];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[len1+i][len2+j];
        res[3] = new TensorD(bl,fl);

        return res;
}
    public TensorD(int[][] a, Ring ring) {
        MatrixS A = new MatrixS(a, ring);
        M = A.toScalarArray(ring);
    }
    public TensorD(double[][] A, Ring ring) {
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
    public  TensorD(Element[] f, Element x0,  Element x1, Element n, Ring ring) {  
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
    public TensorD(int r, int c, int density, int[] randomType, Random ran, Ring ring) {
        this(MatrixS.randomScalarArr2d(r, c, density, randomType, ran, ring));
    }

    public TensorD(long[][] a, Ring ring, Element zero) {
        MatrixS A = new MatrixS(a, ring);
        M = A.toScalarArray(ring, zero);
    }

    public TensorD(MatrixS A, Ring ring, Element zero, int ffl) {fl=ffl;
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
    public TensorD transpose(Ring ring) {
        Element[][] newM = new Element[M[0].length][M.length];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                newM[j][i] = M[i][j];
            }
        }
        return new TensorD(newM,fl);
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
     * <CODE>  import matrix.TensorD;                 <br>
     * import java.util.Random; <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * TensorD matr = new TensorD(32, 5000, 32, rnd); <br>
     * TensorD pm = matr.proceedToPow2_1UL(); </ul>
     * } </ul>
     * } </CODE>
     */
    public TensorD proceedToPow2_1UL(Ring ring) {
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
        return new TensorD(res,fl);
    }
   public boolean equals(TensorD x, Ring ring) {
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
   
   public boolean equals(TensorD x ) {
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
   
    public TensorD add(TensorD x, Ring ring) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].add(x.M[i][j], ring);
            }
        }
        return new TensorD(z, fl|x.fl);
    }
    
    public TensorD add(TensorD x, Element mod, Ring ring) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = (M[i][j].add(x.M[i][j], ring)).mod(mod, ring);
            }
        }
        return new TensorD(z, fl|x.fl);
    }
    
    

    public TensorD add(TensorD x, int br1, int ar1, int bc1, int ac1, int br2,
            int bc2, Ring ring) {
        Element[][] z = new Element[ar1][ac1];
        for (int i = 0; i < ar1; i++) {
            for (int j = 0; j < ac1; j++) {
                z[i][j] = M[i + br1][j + bc1].add(x.M[i + br2][j + bc2], ring);
            }
        }
        return new TensorD(z, fl|x.fl);
    }

    public TensorD subtract(TensorD x, int br1, int ar1, int bc1, int ac1,
            int br2, int bc2, Ring ring) {
        Element[][] z = new Element[ar1][ac1];
        for (int i = 0; i < ar1; i++) {
            for (int j = 0; j < ac1; j++) {
                z[i][j] = M[i + br1][j + bc1].subtract(x.M[i + br2][j + bc2], ring);
            }
        }
        return new TensorD(z, fl|x.fl);
    }

    /**
     * Вычисление разности матриц.
     *
     * @param x типа TensorD, вычитаемое
     *
     * @return <tt> this - x </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE>  import matrix.TensorD;                          <br>
     * import java.util.Random(); <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * TensorD matr1 = new TensorD(32, 5000, 32, rnd); <br>
     * TensorD matr2 = new TensorD(32, 4000, 16, rnd); <br>
     * TensorD msum = matr1.subtract(matr2); </ul>
     * } </ul>
     * } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> вычитаются и
     * записываются в <tt>msum</tt>.
     */
    public TensorD subtract(TensorD x, Ring ring) {
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].subtract(x.M[i][j], ring);
            }
        }
        return new TensorD(z, fl|x.fl);
    }

    /**
     * Процедура вычисления произведения матриц. Используется стандартная
     * нерекурсивная схема умножения.
     *
     * @param x типа TensorD, сомножитель
     *
     * @return <tt> this * x </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE>  import matrix.TensorD;                          <br>
     * import java.util.Random; <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * TensorD matr1 = new TensorD(32, 5000, 32, rnd); <br>
     * TensorD matr2 = new TensorD(32, 4000, 16, rnd); <br>
     * TensorD msum = matr1.multCU(matr2); </ul>
     * } </ul>
     * } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
     * записываются в <tt>msum</tt>.
     */
    public TensorD multCU(TensorD x, Ring ring) {
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
        return new TensorD(z, fl|x.fl);
    }
    
    @Override
    public TensorD Mod(Element x, Ring r){
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].Mod(x, r);
            }
        }
        return new TensorD(z,fl);
    }
    
    @Override
    public TensorD mod(Element x, Ring r){
        int n = M.length;
        int m = M[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                z[i][j] = M[i][j].mod(x, r);
            }
        }
        return new TensorD(z,fl);
    }
    
    public TensorD multCU(TensorD x, Element mod, Ring ring) {
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
        return new TensorD(z, fl|x.fl);
    }

    /**
     * Блочный нерекурсивный алгоритм стандартного умножения матриц. Эффективно
     * использует кэш (как может)
     *
     * @param x типа TensorD, правый сомножитель
     * @param qsize типа int, размер кванта умножения
     *
     * @return TensorD
     */
    public TensorD multCU(TensorD x, int qsize, Ring ring) {
        Element[][] z = new Element[M.length][x.M[0].length];
        for (int i = 0; i < M.length; i += qsize) {
            for (int j = 0; j < x.M[0].length; j += qsize) {
                // инициализация данного блока результата
                //(в  станд. алгоритме она нах не нужна)
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
        return new TensorD(z, fl|x.fl);
    }

    public static TensorD oneTensorD(int i, int j, Ring ring) {
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
        return new TensorD(fM,0);
    }

    public static TensorD zeroTensorD(int i, int j, Ring ring) {
        Element[][] fM = new Element[i][j];
        for (int k = 0; k < i; k++) {
            for (int t = 0; t < j; t++) {
                fM[k][t] = ring.numberZERO;
            }
        }
        return new TensorD(fM,0);
    }

    /**
     * Процедура, возвращающая единичную матрицу заданного размера k и имеющую
     * тип Element one.
     *
     * @param n типа int, число строк и столбцов матрицы
     */
    public static TensorD ONE(int k, Ring ring) {
        Element one = ring.numberONE;
        TensorD M = ZERO(k, k, ring);
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
    public static TensorD ZERO(int k, Ring ring) {
        return ZERO(k, k, ring);
    }

    /**
     * Процедура, возвращающая нулевую матрицу заданного размера и имеющую тип
     * Element one.
     *
     * @param n типа int, число строк матрицы
     * @param m типа int, число столбцов матрицы
     */
    public static TensorD ZERO(int n, int m, Ring ring) {
        Element zero = ring.numberONE().myZero(ring);
        Element[][] mas = new Element[n][m];
        Element[] tt = mas[0];
        for (int i = 0; i < m; i++) {
            tt[i] = zero;
        }
        for (int i = 1; i < n; i++) {
            System.arraycopy(tt, 0, mas[i], 0, m);
        }
        return new TensorD(mas, 0);
    }

    /**
     * Процедура умножения матриц с использованием алгоритма Штрассена.
     *
     * @param b типа TensorD, сомножитель
     *
     * @return <tt> this * b </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE> import matrix.TensorD;                          <br>
     * import java.util.Random(); <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * TensorD matr1 = new TensorD(32, 5000, 32, rnd); <br>
     * TensorD matr2 = new TensorD(32, 4000, 16, rnd); <br>
     * TensorD msum = matr1.multS(matr2); </ul>
     * } </ul>
     * } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
     * записываются в <tt>msum</tt>.
     */
    // /////////////////////////////////////////////////////////////

    public TensorD multS(TensorD b, Ring ring) {
        return multS(b, 0, 0, 0, 0, M.length, ring);
    }

    public TensorD multSC(TensorD b, int qsize, Ring ring) {
        if (qsize == 2) {
            return multS(b, 0, 0, 0, 0, M.length, ring);
        }
        return multSC(b, 0, 0, 0, 0, M.length, qsize, ring);
    }

    public TensorD multSCR(TensorD b, int qsize, int cqsize, Ring ring) {
        if (qsize == 2) {
            return multS(b, 0, 0, 0, 0, M.length, ring);
        }
        if (cqsize == 1) {
            return multSC(b, 0, 0, 0, 0, M.length, qsize, ring);
        }
        return multSCR(b, 0, 0, 0, 0, M.length, qsize, cqsize, ring);
    }

    public TensorD multS(TensorD b, int br1, int bc1, int br2, int bc2,
            int ord_now, Ring ring) {

        Element[][] res = new Element[ord_now][ord_now];
        if (ord_now > 2) {
            int ordN = ord_now >>> 1;
            TensorD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
                    multS(
                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring), ring); // 03*03
            TensorD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
                    multS(
                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), ring); // 20*01
            TensorD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
                    bc1 + ordN, ring).multS(
                            b.add(b, br2 + ordN, ordN, bc2, ordN,
                                    br2 + ordN, bc2 + ordN, ring), ring); //13*23

            TensorD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
                    bc1 + ordN, ring).multS(
                            b, 0, 0, br2, bc2, ordN, ring); // 23*0
            TensorD t3 = multS(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
                    br2 + ordN, bc2 + ordN, ring),
                    br1, bc1, 0, 0, ordN, ring); // 0*13
            TensorD t4 = multS(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
                    bc2, ring),
                    br1 + ordN, bc1 + ordN, 0, 0, ordN, ring); // 3*20
            TensorD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).multS(
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
        return new TensorD(res, fl|b.fl);
    }

    public TensorD multSC(TensorD b, int br1, int bc1, int br2, int bc2,
            int ord_now, int qsize, Ring ring) {

        Element[][] res = new Element[ord_now][ord_now];
        if (ord_now > qsize) {
            int ordN = ord_now >>> 1;
            TensorD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring),
                            qsize, ring); // 03*03
            TensorD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), qsize, ring); // 20*01
            TensorD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b.add(b, br2 + ordN, ordN, bc2, ordN,
                                    br2 + ordN, bc2 + ordN, ring), qsize, ring); //13*23

            TensorD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b, 0, 0, br2, bc2, ordN, qsize, ring); // 23*0
            TensorD t3 = multSC(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
                    br2 + ordN, bc2 + ordN, ring),
                    br1, bc1, 0, 0, ordN, qsize, ring); // 0*13
            TensorD t4 = multSC(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
                    bc2, ring),
                    br1 + ordN, bc1 + ordN, 0, 0, ordN, qsize, ring); // 3*20
            TensorD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).
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
        return new TensorD(res, fl|b.fl);
    }

    public TensorD multCU(TensorD m, int br1, int er1, int bc1, int ec1,
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
        return new TensorD(res, fl|m.fl);
    }

    public TensorD multSCR(TensorD b, int br1, int bc1, int br2, int bc2,
            int ord_now, int qsize, int cqsize, Ring ring) {

        Element[][] res = new Element[ord_now][ord_now];
        if (ord_now > qsize) {
            int ordN = ord_now >>> 1;
            TensorD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring),
                            qsize, ring); // 03*03
            TensorD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
                    multSC(
                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), qsize, ring); // 20*01
            TensorD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b.add(b, br2 + ordN, ordN, bc2, ordN,
                                    br2 + ordN, bc2 + ordN, ring), qsize, ring); //13*23

            TensorD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
                    bc1 + ordN, ring).multSC(
                            b, 0, 0, br2, bc2, ordN, qsize, ring); // 23*0
            TensorD t3 = multSC(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
                    br2 + ordN, bc2 + ordN, ring),
                    br1, bc1, 0, 0, ordN, qsize, ring); // 0*13
            TensorD t4 = multSC(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
                    bc2, ring),
                    br1 + ordN, bc1 + ordN, 0, 0, ordN, qsize, ring); // 3*20
            TensorD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).
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
        return new TensorD(res,fl|b.fl);
    }

    public TensorD multCUR(TensorD m, int qsize, int br, int bc, int br2,
            int bc2, int ord_now, Ring ring) {
        if (ord_now <= qsize) {
            return multCU(m, br, br + ord_now, bc, bc + ord_now, br2, bc2,
                    bc2 + ord_now, ring);
        }
        int ordN = ord_now >>> 1;
        return join(new TensorD[] {
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

    public static TensorD join(TensorD[] matrs) {
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
        return new TensorD(res, matrs[0].fl|matrs[1].fl|matrs[2].fl|matrs[3].fl);
    }

    /**
     * Процедура умножения матриц с использованием стандартной рекурсивной схемы
     * умножения.
     *
     * @param b типа TensorD, сомножитель
     *
     * @return <tt> this * b </tt> <br>
     * <b> Пример использования </b> <br>
     * <CODE>  import matrix.TensorD;                          <br>
     * import java.util.Random(); <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Random rnd = new Random(); <br>
     * TensorD matr1 = new TensorD(32, 5000, 32, rnd); <br>
     * TensorD matr2 = new TensorD(32, 4000, 16, rnd); <br>
     * TensorD msum = matr1.multCUR(matr2); </ul>
     * } </ul> } </CODE> <br>
     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
     * записываются в <tt>msum</tt>.
     */
    public TensorD multCUR(TensorD b, Ring ring) { // классическое умножение
        int deg = M.length;
        Element[][] res = new Element[deg][deg];
        if (deg > 2) {
            deg = deg >>> 1;

            TensorD t1 = new TensorD(new Element[deg][deg]);
            TensorD t2 = new TensorD(new Element[deg][deg]);
            TensorD t11 = new TensorD(new Element[deg][deg]);
            TensorD t21 = new TensorD(new Element[deg][deg]);
            TensorD t12 = new TensorD(new Element[deg][deg]);
            TensorD t22 = new TensorD(new Element[deg][deg]);

            TensorD n11 = new TensorD(new Element[deg][deg]);
            TensorD n12 = new TensorD(new Element[deg][deg]);
            TensorD n21 = new TensorD(new Element[deg][deg]);
            TensorD n22 = new TensorD(new Element[deg][deg]);

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
        return new TensorD(res, b.fl|fl);

    }

 

 



    /**
     * Обращение знака матрицы. Создается новая матрица.
     *
     * @return матрица, полученная обращением знака у каждого элемента
     */
    @Override
    public TensorD negate(Ring ring) {
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
        return new TensorD(negM,fl);
    }

    /**
     * Копирование матрицы (создание дубликата)
     *
     * @return - дубликат матрицы
     */
    public TensorD copy() {
        int n = M.length;
        int k = M[0].length;
        Element[][] copyM = new Element[n][k];
        for (int i = 0; i < n; i++) {
            Element[] strM = M[i];
            Element[] str_copyM = new Element[k];
            System.arraycopy(strM, 0, str_copyM, 0, k);
            copyM[i] = str_copyM;
        }
        return new TensorD(copyM,fl);
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
        return new TensorD(newM,fl);
    }

    @Override
    public Element value(Element[] point, Ring ring) {
      Element[][] U=new Element[M.length][M[0].length];
       for(int i=0;i<M.length;i++){
            for(int j=0;j<M[0].length;j++){
           U[i][j]=M[i][j].value(point, ring);
       }}
       return new TensorD(U);
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
        if (x.numbElementType() != Ring.TensorD) {
            return (x.numbElementType() > Ring.TensorD) ? -1 : 1;
        }
        int sizeThis = M.length;
        int sizeX = ((TensorD) x).M.length;
        if (sizeThis != sizeX) {
            return (sizeX > sizeThis) ? -1 : 1;
        }
        int colNum = M[0].length;
        int colNumX = ((TensorD) x).M[0].length;
        if (colNum != colNumX) {
            return (colNumX > colNum) ? -1 : 1;
        }
        int flag;
        for (int i = 0; i < sizeThis; i++) {
            for (int j = 0; j < M[i].length; j++) {
                flag = M[i][j].compareTo(((TensorD) x).M[i][j], ring);
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
        TensorD matS = (TensorD) x;
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
        return new TensorD(tempM);
    }

    @Override
    public TensorD expand(Ring ring) {
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
    public TensorD submatrix(int row1, int rowsNumb, int col1, int colNumber, Ring ring) {

       Element[][] MatrM = new Element[rowsNumb][colNumber];
        for (int i = row1; i < row1+rowsNumb; i++) {
            System.arraycopy(M[i], col1, MatrM[i - row1], 0, colNumber);
        }
        return  new TensorD(MatrM ,fl);
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
    public TensorD Minor(int i, int j, Ring ring) {
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
        return new TensorD(S,fl);
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
        TensorD A1 = this.copy();
        for (int m = 2; m <= M.length; m++) {
            A1 = this.multCU(A1, ring);
            Element tr1 = A1.track(ring);
            rez = rez.add(tr1, ring);
        }
        return rez;
    }

    public TensorD multiplyByScalar(Element e, Ring r) {
        Element[][] S = new Element[M.length][M.length];
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < S[i].length; j++) {
                S[i][j] = M[i][j].multiply(e, r);
            }
        }
        return new TensorD(S,fl);
    }

    public VectorS multiplyByColumn(VectorS e, Ring r) {
        return new VectorS(multiplyByColumn(e.V,r),fl);
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
            case Ring.TensorD:
                return add((TensorD) e, r);
            case Ring.MatrixS:
         //       return new MatrixS(this, r).add(e, r); // пока только так(
            default:
                return null;
        }
    }

    @Override
    public Element subtract(Element e, Ring r) {
        switch (e.numbElementType()) {
            case Ring.TensorD:
                return subtract((TensorD) e, r);
            case Ring.MatrixS:
           //     return new MatrixS(this, r).subtract(e, r); // пока только так(
            default:
                return null;
        }
    }

    @Override
    public Element multiply(Element e, Ring r) {
        switch (e.numbElementType()) {
            case Ring.TensorD:
                return multCU((TensorD) e, r);
            case Ring.VectorS:
                return multiplyByColumn((VectorS) e, r);
            case Ring.MatrixS:
            //    return new MatrixS(this, r).multiply(e, r); // пока только так(
            default:
                return multiplyByScalar(e, r);
        }
    }

    public Element putElement(Element e, int i, int j) {
        Element[][] newM = M.clone();
        newM[i][j] = e;
        return new TensorD(newM);
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
        return Ring.TensorD;
    }

    /**
     * Возвращаем матрицу знаков входящих в нее элементов( F.e.
     * A=[[-3,5],[0,-x]]; ---> [[-1,1],[0,-1]] )
     *
     * @param ring кольцо
     *
     * @return
     */
    public TensorD signum(Ring ring) {
        Element[][] signumM = new Element[M.length][];
        int signumEl;
        for (int i = 0; i < M.length; i++) {
            signumM[i] = new Element[M[i].length];
            for (int j = 0; j < signumM[i].length; j++) {
                signumEl = M[i][j].signum();
                signumM[i][j] = (signumEl == 1) ? ring.numberONE : (signumEl == 0) ? ring.numberZERO : ring.numberMINUS_ONE;
            }
        }
        return new TensorD(signumM,0);
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
        return new TensorD(absM,fl);
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
        return new TensorD(absM,fl);
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
        return new TensorD(absM,fl);
    }

    @Override
    public Element closure(Ring ring) {
        return new TensorD(new MatrixS(M, ring).closure( ring), ring, fl);
       // return oneTensorD(M.length, M[0].length, ring).subtract(inverse(ring), ring).inverse(ring);
    }

    @Override
    public Element inverse(Ring ring) {
        return new TensorD(new MatrixS(M, ring).inverse(ring.numberONE, ring), ring, fl);
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
            return new TensorD(newM, fl);
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
 
   public TensorD siftLeft( int s){TensorD W=copy();
        Array.siftLeft(W.M, s); return W;}
   
    public TensorD siftRight( int s){TensorD W=copy();
        Array.siftRight(W.M, s); return W;}
      /**
    * Building of Akritas matrix for polynomial p1 and p2
    * @param p1 - first dense polynomial from highest coeffs
    * @param p2 - second dense polynomial from highest coeffs
    * @param int[][] type - {{t1,t2,..,tk},{ m1,m2,..,mk}} -- type(number of pol) and value of moving for each row
    * @param ring - Ring
    * @return  - Akritas matrix of k \times 2max(n1,n2)
    */
    public static TensorD Akritas(Element[] p1, Element[] p2, int[][] type, Ring ring){
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
        return new TensorD(e);
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
    public static TensorD Sylvester(Polynom p1, Polynom p2, int type, Ring ring){
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
        return new TensorD(e);
   }
    
    /**
     * Сложение матриц в Булевой алгебре (если пользователь указал
     * в матрицах числа не принадлежащие множеству {0,1}, то будем считать их =1)
     * @param b матрица
     * @param ring
     * @return
     */
    @Override
    public TensorD B_OR(Element B, Ring ring){  
        if (!(B instanceof TensorD)) return null; 
        TensorD b =(TensorD) B;
        TensorD rez = new TensorD(new Element[b.M.length][b.M.length], b.fl|fl);
        for (int i = 0; i < b.M.length; i++) {
            for (int j = 0; j < b.M.length; j++) 
               rez.M[i][j]=  ((this.M[i][j].isZero(ring)) && (b.M[i][j].isZero(ring)))?
                              NumberZ64.ZERO: NumberZ64.ONE;
        }
        return rez;
    }
    @Override
        public TensorD B_NOT(Ring ring){     
        TensorD rez = new TensorD(new Element[ M.length][M[0].length], fl);
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
    public TensorD B_AND(Element B, Ring ring){TensorD b=null;
        if (B instanceof VectorS) b=new TensorD(((VectorS)B), true, ((VectorS)B).fl );
        else if (B instanceof TensorD) b=(TensorD)B;
        int n=M.length; int k=M[0].length; int m=b.M[0].length;
        if(k!=b.M.length){return null;}
        TensorD rez = new TensorD(new Element[n][m], fl|b.fl);
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < m; j++) 
            for (int s = 0; s < k; s++){rez.M[i][j]= NumberZ64.ZERO;
                if((!M[i][s].isZero(ring)) && (!b.M[s][j].isZero(ring))){
                    rez.M[i][j]= NumberZ64.ONE; break;}
            }
        }
        return rez;
    }
    
    public TensorD[] snfAndMatrs(Element[] s) {Ring ring=Ring.ringZxyz; 
        TensorD x=this;
        TensorD[] Matrs=new TensorD[s.length];
        for (int i = 0; i < s.length; i++) {s[i]=NumberZ.ZERO;}
        int n= M.length, m=M[0].length; int k=m; NumberZ mm=new NumberZ(m);
        int numBits=0; 
        while (k!=0){k=k>>1; numBits++;}k++;
        Element gcd0; NumberZ gcd;
        TensorD VU1;
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
            TensorD UU=(new TensorD(rowU, false).multCU(x, ring)).divideByNumber(gcd, ring);
            System.out.println("UU="+ UU);
           if(!gcd.equals(NumberZ.ONE)){for (int i = 0; i < XmV.length; i++) XmV[i]=XmV[i].divideExact(gcd, ring);}
            VU1= (new TensorD(XmV , true)). multCU(UU,ring);
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
//    public TensorD  adjont(TensorD A,  Ring ring){ TensorD res=null;    
//    int n=A.M.length; int m=A.M[0].length; int N=Math.max(n,m); 
//    MatrixS mat= (new MatrixS(A, ring)).adjoint(ring); 
//    res= new TensorD(mat, true, ring, A.fl);
//    //  new TensorD(new TensorD(new MatrixS(((TensorD) CalcArg[0]), newRing).adjoint(newRing),RING), ((TensorD) CalcArg[0]).fl);
//    return res;
//    }
    /**
     * Вычисление присоединенной матрицы для this, используя алгоритм
     * Сейфуллина, не содержит делений и сокращений
     *
     * @return TensorD - присоединенная матрица = this
     *
     *
     * @author Pereslavtseva
     */
    public TensorD adjoint(Ring ring) {int size=M[0].length;
        MatrixS nn= new MatrixS(M,ring); nn=nn.adjoint(ring);
        Element[][] el=nn.toSquaredArray(size,  ring);
        return new TensorD(el);
//        charPolynomTensorD q = new charPolynomTensorD(this);
//        TensorD rr=q.adjointSeifullin(ring);
//        return new TensorD(q.adjointSeifullin(ring),fl);
        
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
        return new TensorD(mat, false, ring, fl); }
    public Element toEchelonForm(Ring ring) {
        MatrixS mat=new MatrixS(M,ring); int m=M[0].length; 
        mat=mat.toEchelonForm(ring);  
        return new TensorD(mat, false, ring, fl); }
    public Element inverse(TensorD A, Ring ring) {return A.inverse(ring); }
    public Element toEchelonForm(TensorD A, Ring ring) {return A.toEchelonForm(ring); }
    public Element adjoint(TensorD A, Ring ring) {return A.adjoint(ring); }
    public Element kernel(TensorD A, Ring ring) { return A.kernel(ring);}
    public Element runk(TensorD A, Ring ring) { return A.rank(ring);}
    public VectorS takeRow(int m ) { 
       return((m<=M.length)&&(m>0))?   new VectorS( M[m-1], fl):new VectorS(0) ;}
    public VectorS takeRow(TensorD A,int n ){return A.takeRow(n);}
    public VectorS takeColumn(int m ) { 
          if  (!((m<=M.length)&&(m>0)))return new VectorS(0);
         VectorS v=  new VectorS(M.length); 
         for (int i = 0; i < M.length; i++) {v.V[i]=M[i][m-1];}
         v.setFlag(fl);
         return v;  
        }
    public VectorS takeColumn(TensorD A,int n ){return A.takeColumn(n);}
    public void setFlag(int flag){fl=flag;};
    public static void main(String[] args) {
//       Ring ring=new Ring("Zp32[x,y]"); ring.MOD32=11;
//       ring.page=new Page(ring);
//       Polynom f= new Polynom("y+y x", ring);  
//       Polynom g= new Polynom("y^2+ x^2+2", ring);
      Ring ring = new Ring("R64[x, y, t]");
       F func = new F("\\exp(x)", ring);
       ring.CForm.ElementConvertToPolynom(func);
       Element ee=ring.CForm.newRing.varPolynom[3].Factor(false, ring);
       System.out.println("factor = "+ee);
       
      //  System.out.println("ee="+ee);
}
}

