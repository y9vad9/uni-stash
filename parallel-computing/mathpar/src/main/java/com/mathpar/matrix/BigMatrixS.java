package com.mathpar.matrix;

import com.mathpar.func.Fname;
import com.mathpar.number.*;
import com.mathpar.polynom.FactorPol;
import com.mathpar.polynom.Polynom;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

 
/**
 * Class BigMatrixS consists of 2-dimensional array of MatrixS. Each of
 * elementary MatrixS -- is a square matrix of size Bn
 *
 * @author Marina
 */
public class BigMatrixS extends Element implements Serializable {
//    public static final long serialVersionUID = 1L;
// public int size = 0;
 //   /** число столбцов в матрице, т.е. номер последнего ненулевого столбца */
//    public int colNumb = 0;
//     public int[][] col;   ==== ЭТО ТОЛЬКО ДЛЯ МАТРИЦ ТИПА MatrixS. ТУТ НЕДОПУСТИМО!
//    /** Установка граничного порядка матрицы, для параллельного умножения
//     * @param s  значение граничного порядка матриц для инициализации.
//     */
    public MatrixS[][] BM;//matrix of MatrixS

    int Bn;               // each elementary matrix has size Bn*Bn

    public BigMatrixS(MatrixS[][] BM, int Bn) {
        this.BM = BM;
        this.Bn = Bn;
    }

    /**
     * Нулевая матрица размера 0x0
     *
     * @return - нулевая матрица размера 1.
     */
    public BigMatrixS() {
    }

    public static BigMatrixS zeroMatrix = new BigMatrixS(new MatrixS[0][0], 0);

    public static BigMatrixS zeroMatrix() {
        return zeroMatrix;
    }

    public BigMatrixS(MatrixS M) {
        BM = new MatrixS[][] {{M}};
        Bn = Math.max(M.size, M.colNumb);
    }

    public BigMatrixS(MatrixD D) {
        BM = new MatrixS[][] {{new MatrixS(D)}};
        Bn = Math.max(D.M.length, D.M[0].length);
    }
 
    public BigMatrixS(int[][] a, Ring ring) {
        BM = new MatrixS[][] {{new MatrixS(a, ring)}};
        Bn = a.length;
        if (Bn > 0) {
            Bn = Math.max(Bn, a[0].length);
        }
    }

    public BigMatrixS(MatrixD[][] D, Ring ring) {int n=D.length; int m=D[0].length;
            BM = new MatrixS[n][m]; Bn=0;
            for (int i = 0; i < n; i++) {  for (int j = 0; j < m; j++) {
                    BM[i][j]=new MatrixS(D[i][j], ring);
                    int ll=Math.max(D[i][j].M.length, D[i][j].M[0].length);
                    Bn=Math.max(ll,Bn);                
            }}          
     }
    
//    private BigMatrixS(int len, int colNumb0, Element[][] r0, int[][] c0) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    /**
* isZero?
* @param r Ring 
* @return true if it is zero matrix.
*/
@Override
public Boolean isZero( Ring r) {
MatrixS[][] S = new MatrixS[BM.length][BM[0].length];
for (int i = 0; i < S.length; i++) {
for (int j = 0; j < S[0].length; j++) {
if (!BM[i][j].isZero(r)) return false;
}
}
return true;
}

    /**
     * Addition of two BigMatrixS.
     *
     * @param A -- BigMatrixS, which has the same block size and block structure
     * as this
     * @param ring -- Ring
     *
     * @return -- sum of two BigMatrixS: A and this
     */
    public BigMatrixS add(BigMatrixS A, Ring ring) {
        if (Bn != A.Bn) {
            ring.exception.append("Error: attempt to add BigMatrixS with different block size: " + Bn + " and " + A.Bn + ". ");
            return null;
        }
        int n = BM.length;
        int m = BM[0].length;
        MatrixS[][] z = new MatrixS[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                try {
                    z[i][j]
                            = (BM[i][j].size == 0) ? A.BM[i][j]
                                    : (A.BM[i][j].size == 0) ? BM[i][j]
                                            : BM[i][j].add(A.BM[i][j], ring);
                } catch (Exception e) {
                    ring.exception.append(
                            "Error: attempt to add BigMatrixS with different block structure: row=" + i + " column= " + j + ". ");
                    return null;
                }
            }
        }
        return new BigMatrixS(z, Bn);
    }
    /**
     * Subtraction of two BigMatrixS.
     *
     * @param A -- BigMatrixS, which has the same block size and block structure
     * as this
     * @param ring -- Ring
     *
     * @return -- difference of two BigMatrixS: A and this
     */
    //   Я исправил тут строку(нехватало negate()):                        : (A.BM[i][j].size == 0) ? BM[i][j].negate(ring)
    public BigMatrixS subtract(BigMatrixS A, Ring ring) {
        if (Bn != A.Bn) {
            ring.exception.append("Error: attempt to substraction BigMatrixS with different block size: " + Bn + " and " + A.Bn + ". ");
            return null;
        }
        int n = BM.length;
        int m = BM[0].length;
        MatrixS[][] z = new MatrixS[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                try {
                    z[i][j]
                            = (BM[i][j].size == 0) ? A.BM[i][j]
                                    : (A.BM[i][j].size == 0) ? BM[i][j].negate(ring)
                                            : BM[i][j].subtract(A.BM[i][j], ring);
                } catch (Exception e) {
                    ring.exception.append(
                            "Error: attempt to substract of two BigMatrixS with different block structure: row=" + i + " column= " + j + ". ");
                    return null;
                }
            }
        }
        return new BigMatrixS(z, Bn);
    }
/**
 * Умножение на число -хорошая процедура (я ее исправил полностью)
 * @param e
 * @param r
 * @return 
 */
    public BigMatrixS multiplyByNumber(Element e, Ring r) {
        MatrixS[][] S = new MatrixS[BM.length][BM[0].length];
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < S[0].length; j++) {
                S[i][j] = BM[i][j].multiplyByNumber(e, r);
            }
        }
        return new BigMatrixS(S, Bn);
    }
    /**
 * toMatrixD преобразование к типу MatrixD  
 * @param ring Ring
 * @return 
 */
    public MatrixD toMatrixD(Ring ring) {
        MatrixD[][] S = new MatrixD[BM.length][BM[0].length];
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < S[0].length; j++) {
                if(BM[i][j].size==Bn)
                  S[i][j] = new MatrixD(BM[i][j].toSquaredArray(Bn, ring));
            }
        }
        Element[][] res=new Element[BM.length*Bn][BM[0].length*Bn];
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < S[0].length; j++) {
                for (int k = 0; k < Bn; k++)  
                   System.arraycopy(S[i][j].M[k], 0, res[i*Bn+k], j*Bn, Bn);
            }
        }
        return new  MatrixD(res);
    }
        
 /**
 * Преобразование к типу String 
 * @param ring Ring
 * @return 
 */
    public String toString(Ring ring) {return toMatrixD(ring).toString(ring);}
     public String toString() { return toMatrixD(Ring.ringR64xyzt).toString(Ring.ringR64xyzt);}   
       public boolean equals(BigMatrixS x, Ring ring) {
        int n = BM.length;
        int m = BM[0].length;
        Element[][] z = new Element[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                 if(!BM[i][j].equals(x.BM[i][j], ring)) return false;
            }
        }
        return true;
    }
          
//     /** Процедура разбиения матрицы  на 4 равных квадратных блока.
//     *  [0]=(00), [1]=(01), [2]=(10), [3]=(11);
//     *  @return массив который содержит четыре блока исходной матрицы
//     */
    public BigMatrixS[] split(BigMatrixS m,Ring r) {
        BigMatrixS[] res = new BigMatrixS[4];
        Element [][] M = m.BM;
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
        res[0] = new BigMatrixS(new MatrixD(bl));
        bl = new Element[len1][len22];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[i][len2+j];
        res[1] = new BigMatrixS();
        bl = new Element[len12][len2];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[len1+i][j];
        res[2] = new BigMatrixS(new MatrixD(bl));
        bl = new Element[len12][len22];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[len1+i][len2+j];
        res[3] = new BigMatrixS(new MatrixD(bl));

        return res;
    }
//    public BigMatrixS multCUR(BigMatrixS b, Ring ring) { // классическое умножение
//        int deg = BM.length;
//        Element[][] res = new Element[deg][deg];
//        if (deg > 2) {
//            deg = deg >>> 1;
//
//            MatrixD t1 = new MatrixD(new Element[deg][deg]);
//            MatrixD t2 = new MatrixD(new Element[deg][deg]);
//            MatrixD t11 = new MatrixD(new Element[deg][deg]);
//            MatrixD t21 = new MatrixD(new Element[deg][deg]);
//            MatrixD t12 = new MatrixD(new Element[deg][deg]);
//            MatrixD t22 = new MatrixD(new Element[deg][deg]);
//
//            MatrixD n11 = new MatrixD(new Element[deg][deg]);
//            MatrixD n12 = new MatrixD(new Element[deg][deg]);
//            MatrixD n21 = new MatrixD(new Element[deg][deg]);
//            MatrixD n22 = new MatrixD(new Element[deg][deg]);
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = BM[i][j];
//                    t12.M[i][j] = b.BM[i][j];
//                    t21.M[i][j] = BM[i][j + deg];
//                    t22.M[i][j] = b.BM[i + deg][j];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n11.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = BM[i][j];
//                    t12.M[i][j] = b.BM[i][j + deg];
//                    t21.M[i][j] = BM[i][j + deg];
//                    t22.M[i][j] = b.BM[i + deg][j + deg];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n12.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = BM[i + deg][j];
//                    t12.M[i][j] = b.BM[i][j];
//                    t21.M[i][j] = BM[i + deg][j + deg];
//                    t22.M[i][j] = b.BM[i + deg][j];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n21.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = BM[i + deg][j];
//                    t12.M[i][j] = b.BM[i][j + deg];
//                    t21.M[i][j] = BM[i + deg][j + deg];
//                    t22.M[i][j] = b.BM[i + deg][j + deg];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n22.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    res[i][j] = n11.M[i][j];
//                    res[i][j + deg] = n12.M[i][j];
//                    res[i + deg][j] = n21.M[i][j];
//                    res[i + deg][j + deg] = n22.M[i][j];
//                }
//            }
//        } else {
//            res[0][0] = (BM[0][0].multiply(b.BM[0][0], ring))
//                    .add(BM[0][1].multiply(b.BM[1][0], ring), ring);
//            res[0][1] = (BM[0][0].multiply(b.BM[0][1], ring))
//                    .add(BM[0][1].multiply(b.BM[1][1], ring), ring);
//            res[1][0] = (BM[1][0].multiply(b.BM[0][0], ring))
//                    .add(BM[1][1].multiply(b.BM[1][0], ring), ring);
//            res[1][1] = (BM[1][0].multiply(b.BM[0][1], ring))
//                    .add(BM[1][1].multiply(b.BM[1][1], ring), ring);
//        }
//        return new BigMatrixS(new MatrixD(res));
//
//    }
      
//    --------------------- я закомментировал Ваш неправильный код -----начало------------------
//    @Override
//    public Element multiply(Element e, Ring r) {
//        switch (e.numbElementType()) {
//            case Ring.MatrixD:
//                return multCU((MatrixD) e, r);
//            case Ring.VectorS:
//                return multiplyByColumn((VectorS) e, r);
//            default:
//                return multiplyByNumber(e, r);
//        }
//    }
    
//    --------------------- я закомментировал Ваш неправильный код -----конец------------------
 
      
    //-------------------------- тут живет копия старого MatrixD --------
    //  ------------------------   это Вам поможет создать  Ваш класс -----
//    
//     public MatrixD divideByNumber(Element s, Ring ring) {
//       if(s.isOne(ring))return this;
//       if(s.isMinusOne(ring) )return negate(ring); 
//        int n = M.length;
//        int m = 0;
//        Element[][] r = new Element[n][0];
//        for (int i = 0; i < n; i++) {
//            Element[] Mi = M[i];
//            m = Mi.length;
//            Element[] ri = new Element[m];
//         //   r[i] = ri;
//            for (int j = 0; j < m; j++) {
//           //     System.out.println("sssss = "+Mi[j]+"    " + s.toString(ring));
//                ri[j] = Mi[j].divideExact(s, ring); 
//            }
//             r[i] = ri;
//        }
//        return new MatrixD(r);
//    }
//     
//    
//     public MatrixD[] split(MatrixD m) {
//     MatrixD[] res = new MatrixD[4];
//        Element [][] M = m.M;
//        int n1 = M.length;
//        int n2 = M[0].length;
// 
//        int len1 = n1 / 2;
//        int len2 = n2 / 2;
//        int len12 = n1 - len1;
//        int len22 = n2 - len2;
//
//        Element[][] bl = new Element[len1][len2];
//        for (int i = 0; i < len1; i++)
//            for (int j = 0; j < len2; j++)
//                bl[i][j] = M[i][j];
//        res[0] = new MatrixD(bl);
//        bl = new Element[len1][len22];
//        for (int i = 0; i < len1; i++)
//            for (int j = 0; j < len22; j++)
//                bl[i][j] = M[i][len2+j];
//        res[1] = new MatrixD(bl);
//        bl = new Element[len12][len2];
//        for (int i = 0; i < len12; i++)
//            for (int j = 0; j < len2; j++)
//                bl[i][j] = M[len1+i][j];
//        res[2] = new MatrixD(bl);
//        bl = new Element[len12][len22];
//        for (int i = 0; i < len12; i++)
//            for (int j = 0; j < len22; j++)
//                bl[i][j] = M[len1+i][len2+j];
//        res[3] = new MatrixD(bl);
//
//        return res;
//}
// 
// 
//    /**
//     * Constructor of random matrixS of polynomials or numbers
//     *
//     * @param r -- row numbers
//     * @param c -- column numbers
//     * @param density -- is an integer of range 0,1...10000.
//     * @param randomType -- array of: [maxPowers_1_var,.., maxPowers-last_var,
//     * type of coeffs, density of polynomials, nbits] The density is an integer
//     * of range 0,1...100.
//     * @param ran -- Random issue
//     * @param one -- one of the matrix elements ring
//     *
//     * @return array2d of Elements
//     */
//    public MatrixD(int r, int c, int density, int[] randomType, Random ran, Ring ring) {
//        this(MatrixS.randomScalarArr2d(r, c, density, randomType, ran, ring));
//    }
//
//    public MatrixD(long[][] a, Ring ring, Element zero) {
//        MatrixS A = new MatrixS(a, ring);
//        M = A.toScalarArray(ring, zero);
//    }
//
//    public MatrixD(MatrixS A, Ring ring, Element zero) {
//        M = A.toScalarArray(ring, zero);
//    }
//
//    @Override
//    public String toString() {
//        return toString(Ring.ringR64xyzt);
//    }
//
//    /**
//     * Преобразование матрицы к текстовому формату, пригодному для вставки в
//     * исходный код, систему Mathematica.
//     *
//     * @return String(<tt>this</tt>)
//     */
//    @Override
//    public String toString(Ring ring) {
//        StringBuilder sb = new StringBuilder("[");
//        for (int i = 0; i <= M.length - 1; i++) {
//            sb.append("[");
//            for (int j = 0; j <= M[i].length - 1; j++) {
//                sb.append(M[i][j].toString(ring));
//                if (j < M[i].length - 1) {
//                    sb.append(", ");
//                }
//            }
//            sb.append("]");
//            if (i < M.length - 1) {
//                sb.append(",\n");
//            }
//        }
//        sb.append("]");
//        return sb.toString();
//    }
//
//    /**
//     * Транспонирование
//     *
//     * @param ring - входное кольцо
//     *
//     * @return транспонированная матрица
//     */
//    public MatrixD transpose(Ring ring) {
//        Element[][] newM = new Element[M[0].length][M.length];
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < M[i].length; j++) {
//                newM[j][i] = M[i][j];
//            }
//        }
//        return new MatrixD(newM);
//    }
//
//    /**
//     * Преобразование матрицы long[][] к текстовому формату, пригодному для
//     * вставки в исходный код, систему Mathematica.
//     *
//     * @return String(<tt>this</tt>)
//     */
//    public String toString(long[][] M) {
//        StringBuilder sb = new StringBuilder("\n [");
//        for (int i = 0; i <= M.length - 1; i++) {
//            sb.append("[");
//            for (int j = 0; j <= M[i].length - 1; j++) {
//                sb.append(M[i][j]);
//                if (j < M[i].length - 1) {
//                    sb.append(",");
//                }
//            }
//            sb.append("]");
//            if (i < M[i].length - 1) {
//                sb.append(",\n");
//            }
//        }
//        sb.append("] \n");
//        return sb.toString();
//    }
//
//    /**
//     * Преобразование матрицы int[][] к текстовому формату, пригодному для
//     * вставки в исходный код, систему Mathematica.
//     *
//     * @return String(<tt>this</tt>)
//     */
//    public String toString(int[][] M) {
//        StringBuilder sb = new StringBuilder("\n [");
//        for (int i = 0; i <= M.length - 1; i++) {
//            sb.append("[");
//            for (int j = 0; j <= M[i].length - 1; j++) {
//                sb.append(M[i][j]);
//                if (j < M[i].length - 1) {
//                    sb.append(",");
//                }
//            }
//            sb.append("]");
//            if (i < M[i].length - 1) {
//                sb.append(",\n");
//            }
//        }
//        sb.append("] \n");
//        return sb.toString();
//    }
//
//    /**
//     * Расширяет данную квадратную матрицу любого размера до наименьшей матрицы
//     * размера <tt> 2 x 2</tt>. Метод дописывает матрицу единичной матрицей и
//     * двумя прямоугольными нулевыми матрицами снизу и справа, а исходная
//     * матрица остается сверху и слева.
//     *
//     * @return <tt> this -> (2<sup>n</sup> x 2<sup>n</sup>) </tt> по типу <br>
//     * <TABLE BORDER="1" CELLSPACING="0" CELLPADDING="5">
//     * <TR>
//     * <TD align = "center">this</TD><TD align = "center">0</TD>
//     * </TR>
//     * <TR>
//     * <TD align = "center">0</TD><TD align = "center">E</TD>
//     * </TR>
//     * </TABLE>
//     * <b> Пример использования </b> <br>
//     * <CODE>  import matrix.MatrixD;                 <br>
//     * import java.util.Random; <br>
//     * class Example{ <br>
//     * <ul> public static void main(String[] args){
//     * <ul> Random rnd = new Random(); <br>
//     * MatrixD matr = new MatrixD(32, 5000, 32, rnd); <br>
//     * MatrixD pm = matr.proceedToPow2_1UL(); </ul>
//     * } </ul>
//     * } </CODE>
//     */
//    public MatrixD proceedToPow2_1UL(Ring ring) {
//        //int len = Math.max(matr.M.length, matr.M[0].length);
//        int len = M.length;
//        int iter = 2;
//        while (iter < len) {
//            iter *= 2;
//        }
//        if (M.length == iter) {
//            return this;
//        }
//        Element[][] res = new Element[iter][iter];
//        for (int i = 0; i < len; i++) {
//            System.arraycopy(M[i], 0, res[i], 0, len);
//        }
//        for (int i = 0; i < iter; i++) {
//            for (int j = len; j < iter; j++) {
//                res[i][j] = M[i][j].zero(ring);
//            }
//        }
//        for (int i = len; i < iter; i++) {
//            for (int j = 0; j < len; j++) {
//                res[i][j] = M[i][j].zero(ring);
//            }
//            res[i][i] = M[i][i].one(ring);
//        }
//        return new MatrixD(res);
//    }
//   public boolean equals(MatrixD x, Ring ring) {
//        int n = M.length;
//        int m = M[0].length;
//        Element[][] z = new Element[n][m];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                 if(!M[i][j].equals(x.M[i][j], ring)) return false;
//            }
//        }
//        return true;
//    }
//
//    public MatrixD add(MatrixD x, Ring ring) {
//        int n = M.length;
//        int m = M[0].length;
//        Element[][] z = new Element[n][m];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                z[i][j] = M[i][j].add(x.M[i][j], ring);
//            }
//        }
//        return new MatrixD(z);
//    }
//    
//    public MatrixD add(MatrixD x, Element mod, Ring ring) {
//        int n = M.length;
//        int m = M[0].length;
//        Element[][] z = new Element[n][m];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                z[i][j] = (M[i][j].add(x.M[i][j], ring)).mod(mod, ring);
//            }
//        }
//        return new MatrixD(z);
//    }
//    
//    
//
//    public MatrixD add(MatrixD x, int br1, int ar1, int bc1, int ac1, int br2,
//            int bc2, Ring ring) {
//        Element[][] z = new Element[ar1][ac1];
//        for (int i = 0; i < ar1; i++) {
//            for (int j = 0; j < ac1; j++) {
//                z[i][j] = M[i + br1][j + bc1].add(x.M[i + br2][j + bc2], ring);
//            }
//        }
//        return new MatrixD(z);
//    }
//
//    public MatrixD subtract(MatrixD x, int br1, int ar1, int bc1, int ac1,
//            int br2, int bc2, Ring ring) {
//        Element[][] z = new Element[ar1][ac1];
//        for (int i = 0; i < ar1; i++) {
//            for (int j = 0; j < ac1; j++) {
//                z[i][j] = M[i + br1][j + bc1].subtract(x.M[i + br2][j + bc2], ring);
//            }
//        }
//        return new MatrixD(z);
//    }
//
//    /**
//     * Вычисление разности матриц.
//     *
//     * @param x типа MatrixD, вычитаемое
//     *
//     * @return <tt> this - x </tt> <br>
//     * <b> Пример использования </b> <br>
//     * <CODE>  import matrix.MatrixD;                          <br>
//     * import java.util.Random(); <br>
//     * class Example{ <br>
//     * <ul> public static void main(String[] args){
//     * <ul> Random rnd = new Random(); <br>
//     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
//     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
//     * MatrixD msum = matr1.subtract(matr2); </ul>
//     * } </ul>
//     * } </CODE> <br>
//     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> вычитаются и
//     * записываются в <tt>msum</tt>.
//     */
//    public MatrixD subtract(MatrixD x, Ring ring) {
//        int n = M.length;
//        int m = M[0].length;
//        Element[][] z = new Element[n][m];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                z[i][j] = M[i][j].subtract(x.M[i][j], ring);
//            }
//        }
//        return new MatrixD(z);
//    }
//
//    /**
//     * Процедура вычисления произведения матриц. Используется стандартная
//     * нерекурсивная схема умножения.
//     *
//     * @param x типа MatrixD, сомножитель
//     *
//     * @return <tt> this * x </tt> <br>
//     * <b> Пример использования </b> <br>
//     * <CODE>  import matrix.MatrixD;                          <br>
//     * import java.util.Random; <br>
//     * class Example{ <br>
//     * <ul> public static void main(String[] args){
//     * <ul> Random rnd = new Random(); <br>
//     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
//     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
//     * MatrixD msum = matr1.multCU(matr2); </ul>
//     * } </ul>
//     * } </CODE> <br>
//     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
//     * записываются в <tt>msum</tt>.
//     */
//    public MatrixD multCU(MatrixD x, Ring ring) {
//        Element zero = ring.numberZERO;
//        Element[][] z = new Element[M.length][x.M[0].length];
//        Element res;
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < x.M[0].length; j++) {
//                res = zero;
//                for (int k = 0; k < M[0].length; k++) {
//                    res = res.add(M[i][k].multiply(x.M[k][j], ring), ring);
//                }
//                z[i][j] = res;
//            }
//        }
//        return new MatrixD(z);
//    }
//    
//    public MatrixD mod(Element x, Ring r){
//        int n = M.length;
//        int m = M[0].length;
//        Element[][] z = new Element[n][m];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                z[i][j] = M[i][j].mod(x, r);
//            }
//        }
//        return new MatrixD(z);
//    }
//    
//    public MatrixD multCU(MatrixD x, Element mod, Ring ring) {
//        Element zero = ring.numberZERO;
//        Element[][] z = new Element[M.length][x.M[0].length];
//        Element res;
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < x.M[0].length; j++) {
//                res = zero;
//                for (int k = 0; k < M[0].length; k++) {
//                    res = res.add(M[i][k].multiply(x.M[k][j], ring), ring);
//                }
//                z[i][j] = res.mod(mod, ring);
//            }
//        }
//        return new MatrixD(z);
//    }
//
//    /**
//     * Блочный нерекурсивный алгоритм стандартного умножения матриц. Эффективно
//     * использует кэш (как может)
//     *
//     * @param x типа MatrixD, правый сомножитель
//     * @param qsize типа int, размер кванта умножения
//     *
//     * @return MatrixD
//     */
//    public MatrixD multCU(MatrixD x, int qsize, Ring ring) {
//        Element[][] z = new Element[M.length][x.M[0].length];
//        for (int i = 0; i < M.length; i += qsize) {
//            for (int j = 0; j < x.M[0].length; j += qsize) {
//                // инициализация данного блока результата
//                //(в  станд. алгоритме она нах не нужна)
//                for (int l = 0; l < qsize; l++) {
//                    for (int m = 0; m < qsize; m++) {
//                        z[i + l][j + m] = z[0][0].zero(ring);
//                    }
//                }
//                for (int k = 0; k < M[0].length; k += qsize) {
//                    for (int l = 0; l < qsize; l++) {
//                        for (int m = 0; m < qsize; m++) {
//                            for (int n = 0; n < qsize; n++) {
//                                z[i + l][j + m] = z[i + l][j + m]
//                                        .add(M[i + l][k + n].multiply(x.M[k + n][j + m], ring), ring);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return new MatrixD(z);
//    }
//
//    public static MatrixD oneMatrixD(int i, int j, Ring ring) {
//        Element[][] fM = new Element[i][j];
//        for (int k = 0; k < i; k++) {
//            for (int t = 0; t < j; t++) {
//                if (k == t) {
//                    fM[k][t] = ring.numberONE;
//                } else {
//                    fM[k][t] = ring.numberZERO;
//                }
//            }
//        }
//        return new MatrixD(fM);
//    }
//
//    public static MatrixD zeroMatrixD(int i, int j, Ring ring) {
//        Element[][] fM = new Element[i][j];
//        for (int k = 0; k < i; k++) {
//            for (int t = 0; t < j; t++) {
//                fM[k][t] = ring.numberZERO;
//            }
//        }
//        return new MatrixD(fM);
//    }
//
//    /**
//     * Процедура, возвращающая единичную матрицу заданного размера k и имеющую
//     * тип Element one.
//     *
//     * @param n типа int, число строк и столбцов матрицы
//     */
//    public static MatrixD ONE(int k, Ring ring) {
//        Element one = ring.numberONE;
//        MatrixD M = ZERO(k, k, ring);
//        for (int i = 0; i < k; i++) {
//            M.M[i][i] = one;
//        }
//        return M;
//    }
//
//    /**
//     * Процедура, возвращающая нулевую матрицу заданного размера и имеющую тип
//     * MainRing.
//     *
//     * @param k типа int, число строк и столбцов матрицы
//     */
//    public static MatrixD ZERO(int k, Ring ring) {
//        return ZERO(k, k, ring);
//    }
//
//    /**
//     * Процедура, возвращающая нулевую матрицу заданного размера и имеющую тип
//     * Element one.
//     *
//     * @param n типа int, число строк матрицы
//     * @param m типа int, число столбцов матрицы
//     */
//    public static MatrixD ZERO(int n, int m, Ring ring) {
//        Element zero = ring.numberONE().myZero(ring);
//        Element[][] mas = new Element[n][m];
//        Element[] tt = mas[0];
//        for (int i = 0; i < m; i++) {
//            tt[i] = zero;
//        }
//        for (int i = 1; i < n; i++) {
//            System.arraycopy(tt, 0, mas[i], 0, m);
//        }
//        return new MatrixD(mas);
//    }
//
//    /**
//     * Процедура умножения матриц с использованием алгоритма Штрассена.
//     *
//     * @param b типа MatrixD, сомножитель
//     *
//     * @return <tt> this * b </tt> <br>
//     * <b> Пример использования </b> <br>
//     * <CODE> import matrix.MatrixD;                          <br>
//     * import java.util.Random(); <br>
//     * class Example{ <br>
//     * <ul> public static void main(String[] args){
//     * <ul> Random rnd = new Random(); <br>
//     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
//     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
//     * MatrixD msum = matr1.multS(matr2); </ul>
//     * } </ul>
//     * } </CODE> <br>
//     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
//     * записываются в <tt>msum</tt>.
//     */
//    // /////////////////////////////////////////////////////////////
//
//    public MatrixD multS(MatrixD b, Ring ring) {
//        return multS(b, 0, 0, 0, 0, M.length, ring);
//    }
//
//    public MatrixD multSC(MatrixD b, int qsize, Ring ring) {
//        if (qsize == 2) {
//            return multS(b, 0, 0, 0, 0, M.length, ring);
//        }
//        return multSC(b, 0, 0, 0, 0, M.length, qsize, ring);
//    }
//
//    public MatrixD multSCR(MatrixD b, int qsize, int cqsize, Ring ring) {
//        if (qsize == 2) {
//            return multS(b, 0, 0, 0, 0, M.length, ring);
//        }
//        if (cqsize == 1) {
//            return multSC(b, 0, 0, 0, 0, M.length, qsize, ring);
//        }
//        return multSCR(b, 0, 0, 0, 0, M.length, qsize, cqsize, ring);
//    }
//
//    public MatrixD multS(MatrixD b, int br1, int bc1, int br2, int bc2,
//            int ord_now, Ring ring) {
//
//        Element[][] res = new Element[ord_now][ord_now];
//        if (ord_now > 2) {
//            int ordN = ord_now >>> 1;
//            MatrixD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
//                    multS(
//                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring), ring); // 03*03
//            MatrixD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
//                    multS(
//                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), ring); // 20*01
//            MatrixD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
//                    bc1 + ordN, ring).multS(
//                            b.add(b, br2 + ordN, ordN, bc2, ordN,
//                                    br2 + ordN, bc2 + ordN, ring), ring); //13*23
//
//            MatrixD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
//                    bc1 + ordN, ring).multS(
//                            b, 0, 0, br2, bc2, ordN, ring); // 23*0
//            MatrixD t3 = multS(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
//                    br2 + ordN, bc2 + ordN, ring),
//                    br1, bc1, 0, 0, ordN, ring); // 0*13
//            MatrixD t4 = multS(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
//                    bc2, ring),
//                    br1 + ordN, bc1 + ordN, 0, 0, ordN, ring); // 3*20
//            MatrixD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).multS(
//                    b, 0, 0, br2 + ordN, bc2 + ordN, ordN, ring); // 01*3
//
//
//            /*   acm.print();
//             acm.print(t1);
//             acm.print(t2);
//             acm.print(t3);
//             acm.print(t4);
//             acm.print(t5);
//             acm.print(t6);
//             acm.print(t7);
//             acm.print();*/
//            for (int i = 0; i < ordN; i++) {
//                for (int j = 0; j < ordN; j++) {
//                    res[i][j] = t1.M[i][j].add(t4.M[i][j].add(t7.M[i][j].
//                            subtract(t5.M[i][
//                                 j], ring), ring), ring);
//                    res[i][j + ordN] = t3.M[i][j].add(t5.M[i][j], ring);
//                    res[i + ordN][j] = t2.M[i][j].add(t4.M[i][j], ring);
//                    res[i + ordN][j
//                            + ordN] = t1.M[i][j].add(t3.M[i][j].add(t6.M[i][j].
//                                            subtract(t2.M[i][
//                                 j], ring), ring), ring);
//                }
//            }
//        } else {
//            Element s1 = (M[br1][bc1].add(M[br1 + 1][bc1 + 1], ring)).multiply(b.M[
//                br2][bc2].add(b.M[br2 + 1][bc2 + 1], ring), ring);
//            Element s2 = (M[br1
//                    + 1][bc1].add(M[br1 + 1][bc1 + 1], ring)).multiply(b.M[br2][bc2], ring);
//            Element s3 = M[br1][bc1].multiply(b.M[br2][bc2
//                    + 1].subtract(b.M[br2 + 1][bc2
//                            + 1], ring), ring);
//            Element s4 = M[br1 + 1][bc1
//                    + 1].multiply(b.M[br2 + 1][bc2].subtract(b.M[br2][bc2], ring), ring);
//            Element s5 = (M[br1][bc1].add(M[br1][bc1 + 1], ring)).multiply(b.M[br2
//                    + 1][bc2 + 1], ring);
//            Element s6 = (M[br1
//                    + 1][bc1].subtract(M[br1][bc1], ring)).multiply(b.M[br2][bc2].add(b.M[br2][
//                                bc2 + 1], ring), ring);
//            Element s7 = (M[br1][bc1 + 1].subtract(M[br1 + 1][bc1 + 1], ring)).
//                    multiply(b.M[br2 + 1][bc2].add(b.M[br2 + 1][
//                                               bc2 + 1], ring), ring);
//
//            res[0][0] = s1.add(s4.add(s7.subtract(s5, ring), ring), ring);
//            res[0][1] = s3.add(s5, ring);
//            res[1][0] = s2.add(s4, ring);
//            res[1][1] = s1.add(s3.add(s6.subtract(s2, ring), ring), ring);
//        }
//        return new MatrixD(res);
//    }
//
//    public MatrixD multSC(MatrixD b, int br1, int bc1, int br2, int bc2,
//            int ord_now, int qsize, Ring ring) {
//
//        Element[][] res = new Element[ord_now][ord_now];
//        if (ord_now > qsize) {
//            int ordN = ord_now >>> 1;
//            MatrixD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
//                    multSC(
//                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring),
//                            qsize, ring); // 03*03
//            MatrixD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
//                    multSC(
//                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), qsize, ring); // 20*01
//            MatrixD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
//                    bc1 + ordN, ring).multSC(
//                            b.add(b, br2 + ordN, ordN, bc2, ordN,
//                                    br2 + ordN, bc2 + ordN, ring), qsize, ring); //13*23
//
//            MatrixD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
//                    bc1 + ordN, ring).multSC(
//                            b, 0, 0, br2, bc2, ordN, qsize, ring); // 23*0
//            MatrixD t3 = multSC(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
//                    br2 + ordN, bc2 + ordN, ring),
//                    br1, bc1, 0, 0, ordN, qsize, ring); // 0*13
//            MatrixD t4 = multSC(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
//                    bc2, ring),
//                    br1 + ordN, bc1 + ordN, 0, 0, ordN, qsize, ring); // 3*20
//            MatrixD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).
//                    multSC(
//                            b, 0, 0, br2 + ordN, bc2 + ordN, ordN, qsize, ring); // 01*3
//
//            for (int i = 0; i < ordN; i++) {
//                for (int j = 0; j < ordN; j++) {
//                    res[i][j] = t1.M[i][j].add(t4.M[i][j].add(t7.M[i][j].
//                            subtract(t5.M[i][
//                                 j], ring), ring), ring);
//                    res[i][j + ordN] = t3.M[i][j].add(t5.M[i][j], ring);
//                    res[i + ordN][j] = t2.M[i][j].add(t4.M[i][j], ring);
//                    res[i + ordN][j
//                            + ordN] = t1.M[i][j].add(t3.M[i][j].add(t6.M[i][j].
//                                            subtract(t2.M[i][
//                                 j], ring), ring), ring);
//                }
//            }
//        } else {
//            return multCU(b, br1, bc1 + ord_now, bc1, bc1 + ord_now, br2, bc2,
//                    bc2 + ord_now, ring);
//        }
//        return new MatrixD(res);
//    }
//
//    public MatrixD multCU(MatrixD m, int br1, int er1, int bc1, int ec1,
//            int br2, int bc2, int ec2, Ring ring) {
//        Element[][] res = new Element[er1 - br1][ec2 - bc2];
//        Element tmp;
//        for (int i = br1; i < er1; i++) {
//            for (int j = bc2; j < ec2; j++) {
//                tmp = NumberZ.ZERO;
//                for (int k = 0; k < ec1 - bc1; k++) {
//                    tmp = tmp.add(M[i][k + bc1].multiply(m.M[k + br2][j], ring), ring);
//                }
//                res[i - br1][j - bc2] = tmp;
//            }
//        }
//        return new MatrixD(res);
//    }
//
//    public MatrixD multSCR(MatrixD b, int br1, int bc1, int br2, int bc2,
//            int ord_now, int qsize, int cqsize, Ring ring) {
//
//        Element[][] res = new Element[ord_now][ord_now];
//        if (ord_now > qsize) {
//            int ordN = ord_now >>> 1;
//            MatrixD t1 = add(this, br1, ordN, bc1, ordN, br1 + ordN, bc1 + ordN, ring).
//                    multSC(
//                            b.add(b, br2, ordN, bc2, ordN, br2 + ordN, bc2 + ordN, ring),
//                            qsize, ring); // 03*03
//            MatrixD t6 = subtract(this, br1 + ordN, ordN, bc1, ordN, br1, bc1, ring).
//                    multSC(
//                            b.add(b, br2, ordN, bc2, ordN, br2, bc2 + ordN, ring), qsize, ring); // 20*01
//            MatrixD t7 = subtract(this, br1, ordN, bc1 + ordN, ordN, br1 + ordN,
//                    bc1 + ordN, ring).multSC(
//                            b.add(b, br2 + ordN, ordN, bc2, ordN,
//                                    br2 + ordN, bc2 + ordN, ring), qsize, ring); //13*23
//
//            MatrixD t2 = add(this, br1 + ordN, ordN, bc1, ordN, br1 + ordN,
//                    bc1 + ordN, ring).multSC(
//                            b, 0, 0, br2, bc2, ordN, qsize, ring); // 23*0
//            MatrixD t3 = multSC(b.subtract(b, br2, ordN, bc2 + ordN, ordN,
//                    br2 + ordN, bc2 + ordN, ring),
//                    br1, bc1, 0, 0, ordN, qsize, ring); // 0*13
//            MatrixD t4 = multSC(b.subtract(b, br2 + ordN, ordN, bc2, ordN, br2,
//                    bc2, ring),
//                    br1 + ordN, bc1 + ordN, 0, 0, ordN, qsize, ring); // 3*20
//            MatrixD t5 = add(this, br1, ordN, bc1, ordN, br1, bc1 + ordN, ring).
//                    multSC(
//                            b, 0, 0, br2 + ordN, bc2 + ordN, ordN, qsize, ring); // 01*3
//
//            for (int i = 0; i < ordN; i++) {
//                for (int j = 0; j < ordN; j++) {
//                    res[i][j] = t1.M[i][j].add(t4.M[i][j].add(t7.M[i][j].
//                            subtract(t5.M[i][
//                                 j], ring), ring), ring);
//                    res[i][j + ordN] = t3.M[i][j].add(t5.M[i][j], ring);
//                    res[i + ordN][j] = t2.M[i][j].add(t4.M[i][j], ring);
//                    res[i + ordN][j
//                            + ordN] = t1.M[i][j].add(t3.M[i][j].add(t6.M[i][j].
//                                            subtract(t2.M[i][
//                                 j], ring), ring), ring);
//                }
//            }
//        } else {
//            return multCUR(b, cqsize, br1, bc1, br2, bc2, ord_now, ring);
//        }
//        return new MatrixD(res);
//    }
//
//    public MatrixD multCUR(MatrixD m, int qsize, int br, int bc, int br2,
//            int bc2, int ord_now, Ring ring) {
//        if (ord_now <= qsize) {
//            return multCU(m, br, br + ord_now, bc, bc + ord_now, br2, bc2,
//                    bc2 + ord_now, ring);
//        }
//        int ordN = ord_now >>> 1;
//        return join(new MatrixD[] {
//            multCUR(m, qsize, br, bc, br2, bc2,
//            ordN, ring).add(multCUR(m, qsize, br, bc + ordN, br2 + ordN, bc2,
//            ordN, ring), ring),
//            multCUR(m, qsize, br, bc, br2, bc2 + ordN,
//            ordN, ring).add(
//            multCUR(m, qsize, br, bc + ordN, br2 + ordN, bc2 + ordN,
//            ordN, ring), ring),
//            multCUR(m, qsize, br + ordN, bc, br2, bc2,
//            ordN, ring).add(
//            multCUR(m, qsize, br + ordN, bc + ordN, br2 + ordN, bc2,
//            ordN, ring), ring),
//            multCUR(m, qsize, br + ordN, bc, br2, bc2 + ordN,
//            ordN, ring).add(
//            multCUR(m, qsize, br + ordN, bc + ordN, br2 + ordN,
//            bc2 + ordN, ordN, ring), ring)
//        });
//
//    }
//
//    public static MatrixD join(MatrixD[] matrs) {
//        int r1 = matrs[0].M.length;
//        int r2 = matrs[2].M.length;
//        int c1 = matrs[0].M[0].length;
//        int c2 = matrs[1].M[0].length;
//        int rows = r1 + r2;
//        int cols = c1 + c2;
//        Element[][] res = new Element[rows][cols];
//        for (int i = 0; i < r1; i++) {
//            for (int j = 0; j < c1; j++) {
//                res[i][j] = matrs[0].M[i][j];
//            }
//        }
//        for (int i = 0; i < r1; i++) {
//            for (int j = 0; j < c2; j++) {
//                res[i][j + c1] = matrs[1].M[i][j];
//            }
//        }
//        for (int i = 0; i < r2; i++) {
//            for (int j = 0; j < c1; j++) {
//                res[i + r1][j] = matrs[2].M[i][j];
//            }
//        }
//        for (int i = 0; i < r2; i++) {
//            for (int j = 0; j < c2; j++) {
//                res[i + r1][j + c1] = matrs[3].M[i][j];
//            }
//        }
//        return new MatrixD(res);
//    }
//
//    /**
//     * Процедура умножения матриц с использованием стандартной рекурсивной схемы
//     * умножения.
//     *
//     * @param b типа MatrixD, сомножитель
//     *
//     * @return <tt> this * b </tt> <br>
//     * <b> Пример использования </b> <br>
//     * <CODE>  import matrix.MatrixD;                          <br>
//     * import java.util.Random(); <br>
//     * class Example{ <br>
//     * <ul> public static void main(String[] args){
//     * <ul> Random rnd = new Random(); <br>
//     * MatrixD matr1 = new MatrixD(32, 5000, 32, rnd); <br>
//     * MatrixD matr2 = new MatrixD(32, 4000, 16, rnd); <br>
//     * MatrixD msum = matr1.multCUR(matr2); </ul>
//     * } </ul> } </CODE> <br>
//     * В этом примере матрицы <tt>matr1</tt> и <tt>matr2</tt> умножаются и
//     * записываются в <tt>msum</tt>.
//     */
//    public MatrixD multCUR(MatrixD b, Ring ring) { // классическое умножение
//        int deg = M.length;
//        Element[][] res = new Element[deg][deg];
//        if (deg > 2) {
//            deg = deg >>> 1;
//
//            MatrixD t1 = new MatrixD(new Element[deg][deg]);
//            MatrixD t2 = new MatrixD(new Element[deg][deg]);
//            MatrixD t11 = new MatrixD(new Element[deg][deg]);
//            MatrixD t21 = new MatrixD(new Element[deg][deg]);
//            MatrixD t12 = new MatrixD(new Element[deg][deg]);
//            MatrixD t22 = new MatrixD(new Element[deg][deg]);
//
//            MatrixD n11 = new MatrixD(new Element[deg][deg]);
//            MatrixD n12 = new MatrixD(new Element[deg][deg]);
//            MatrixD n21 = new MatrixD(new Element[deg][deg]);
//            MatrixD n22 = new MatrixD(new Element[deg][deg]);
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = M[i][j];
//                    t12.M[i][j] = b.M[i][j];
//                    t21.M[i][j] = M[i][j + deg];
//                    t22.M[i][j] = b.M[i + deg][j];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n11.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = M[i][j];
//                    t12.M[i][j] = b.M[i][j + deg];
//                    t21.M[i][j] = M[i][j + deg];
//                    t22.M[i][j] = b.M[i + deg][j + deg];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n12.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = M[i + deg][j];
//                    t12.M[i][j] = b.M[i][j];
//                    t21.M[i][j] = M[i + deg][j + deg];
//                    t22.M[i][j] = b.M[i + deg][j];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n21.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    t11.M[i][j] = M[i + deg][j];
//                    t12.M[i][j] = b.M[i][j + deg];
//                    t21.M[i][j] = M[i + deg][j + deg];
//                    t22.M[i][j] = b.M[i + deg][j + deg];
//                }
//            }
//            t1 = t11.multCUR(t12, ring);
//            t2 = t21.multCUR(t22, ring);
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    n22.M[i][j] = t1.M[i][j].add(t2.M[i][j], ring);
//                }
//            }
//
//            for (int i = 0; i < deg; i++) {
//                for (int j = 0; j < deg; j++) {
//                    res[i][j] = n11.M[i][j];
//                    res[i][j + deg] = n12.M[i][j];
//                    res[i + deg][j] = n21.M[i][j];
//                    res[i + deg][j + deg] = n22.M[i][j];
//                }
//            }
//        } else {
//            res[0][0] = (M[0][0].multiply(b.M[0][0], ring))
//                    .add(M[0][1].multiply(b.M[1][0], ring), ring);
//            res[0][1] = (M[0][0].multiply(b.M[0][1], ring))
//                    .add(M[0][1].multiply(b.M[1][1], ring), ring);
//            res[1][0] = (M[1][0].multiply(b.M[0][0], ring))
//                    .add(M[1][1].multiply(b.M[1][0], ring), ring);
//            res[1][1] = (M[1][0].multiply(b.M[0][1], ring))
//                    .add(M[1][1].multiply(b.M[1][1], ring), ring);
//        }
//        return new MatrixD(res);
//
//    }
//
//    /**
//     * Вычисление хар. полинома матрицы this, используя алгоритм Сейфуллина, не
//     * содержит делений и сокращений
//     *
//     * @return Element[] - коэффициенты характеристического полинома, начиная с
//     * коэффициента перед x^n и заканчивая свободным членом
//     *
//     * @author Pereslavtseva
//     */
//    public Element[] characteristicPolynom(Ring ring) {
//        charPolynomMatrixD q = new charPolynomMatrixD(this);
//        return q.charPolSeifullin(ring);
//    }
//
//    /**
//     * Вычисление хар. полинома матрицы this, используя алгоритм Сейфуллина, не
//     * содержит делений и сокращений
//     *
//     * @return Polynom - характеристического полином от одной переменной с
//     * коэффициентами Element
//     *
//     * @author Pereslavtseva
//     */
//    public Polynom characteristicPolynomP(Ring ring) {
//        charPolynomMatrixD q = new charPolynomMatrixD(this);
//        return q.charPolSeifullinP(ring);
//    }
//
//    /**
//     * Вычисление присоединенной матрицы для this, используя алгоритм
//     * Сейфуллина, не содержит делений и сокращений
//     *
//     * @return MatrixD - присоединенная матрица = this
//     *
//     *
//     * @author Pereslavtseva
//     */
//    public MatrixD adjoint(Ring ring) {
//        charPolynomMatrixD q = new charPolynomMatrixD(this);
//        return q.adjointSeifullin(ring);
//    }
//
//    /**
//     * Обращение знака матрицы. Создается новая матрица.
//     *
//     * @return матрица, полученная обращением знака у каждого элемента
//     */
//    @Override
//    public MatrixD negate(Ring ring) {
//        int n = M.length;
//        int k = M[0].length;
//        Element[][] negM = new Element[n][k];
//        Element[] strM = new Element[k];
//        for (int i = 0; i < n; i++) {
//            Element[] str_negM = new Element[k];
//            strM = M[i];
//            for (int j = 0; j < k; j++) {
//                str_negM[j] = strM[j].negate(ring);
//            }
//            negM[i] = str_negM;
//
//        }
//        return new MatrixD(negM);
//    }
//
//    /**
//     * Копирование матрицы (создание дубликата)
//     *
//     * @return - дубликат матрицы
//     */
//    public MatrixD copy() {
//        int n = M.length;
//        int k = M[0].length;
//        Element[][] copyM = new Element[n][k];
//        for (int i = 0; i < n; i++) {
//            Element[] strM = M[i];
//            Element[] str_copyM = new Element[k];
//            System.arraycopy(strM, 0, str_copyM, 0, k);
//            copyM[i] = str_copyM;
//        }
//        return new MatrixD(copyM);
//    }
//
//    /**
//     * Вычисление значения полиномиальной матрицы в точке point по модулю p
//     *
//     * @return long[][]
//     */
//    public long[][] valueOf(NumberZp32[] point, long p) {
//        if (M[0][0] instanceof Polynom) {
//            int n = M.length;
//            int k = M[0].length;
//            long[][] A = new long[n][k];
//            Ring ring = Ring.ringR64xyzt; //new Ring ("Zp32");
//            ring.MOD32 = p;
//            for (int i = 0; i < n; i++) {
//                Polynom[] str = (Polynom[]) M[i];
//                long[] str1 = new long[k];
//                for (int j = 0; j < k; j++) {
//                    str1[j] = (str[j].value(point, ring)).longValue();
//                }
//                System.arraycopy(str1, 0, A[i], 0, k);
//            }
//            return A;
//        } else {
//            return null;
//        }
//    }
//    /**
//     * Make int-matrix from this matrix
//     * @return int[][]
//     */
//    public int[][] toIntMatrix() {
//        int[][] newM = new int[M.length][];
//        for (int i = 0; i < newM.length; i++) {
//            newM[i] = new int[M[i].length];
//            for (int j = 0; j < newM[i].length; j++) {
//                newM[i][j] = M[i][j].intValue() ;
//            }
//        }
//        return  newM;
//    }
//
//    @Override
//    public Element toNewRing(int numAlgebra, Ring ring) {
//        Element[][] newM = new Element[M.length][];
//        for (int i = 0; i < newM.length; i++) {
//            newM[i] = new Element[M[i].length];
//            for (int j = 0; j < newM[i].length; j++) {
//                newM[i][j] = M[i][j].toNewRing(numAlgebra, ring);
//            }
//        }
//        return new MatrixD(newM);
//    }
//
//    /**
//     * Вычисление значения полиномиальной матрицы в точке point по модулю p
//     *
//     * @return long[][]
//     */
//    public long[][] valueOf(NumberZp32[] point, Ring r) {
//        //System.out.println("ring = "+ r.toString());
//        if ((M[0][0] instanceof Polynom)) // && (M[0][1] instanceof Polynom) &&                (M[1][0] instanceof Polynom) &&(M[1][1] instanceof Polynom ))
//        {
//            int n = M.length;
//            int k = M[0].length;
//            long[][] A = new long[n][k];
//            //Ring ring = Ring.defaultR64Ring; //new Ring ("Zp32");
//            long p = r.MOD32;
//            for (int i = 0; i < n; i++) {
//                //-Polynom []str = (Polynom[]) M[i];
//                //-long [] str1= new long [k];
//                for (int j = 0; j < k; j++) {
//                    //System.out.println("M["+i+"]["+j+"] "+M[i][j]+",  point= {"+point[0]+"}");
//                    A[i][j] = (M[i][j].value(point, r)).longValue();
////                    Element q = M[i][j];
////                    A[i][j] = (q instanceof Polynom)? (q.value(point, r)).longValue():
////                            ((NumberZp32) q).longValue();
//                }
//                //-System.arraycopy(str1, 0, A[i], 0, k);
//            }
//            return A;
//        }
//        if ((M[0][0] instanceof NumberZp32)) {
//            int n = M.length;
//            int k = M[0].length;
//            long[][] A = new long[n][k];
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < k; j++) {
//                    A[i][j] = M[i][j].longValue();
//                }
//            }
//            return A;
//        }
//        if ((M[0][0] instanceof NumberZ)) {
//            int n = M.length;
//            int k = M[0].length;
//            long[][] A = new long[n][k];
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < k; j++) {
//                    A[i][j] = (M[i][j].Mod(new NumberZ(r.MOD32), r)).longValue();
//                }
//            }
//            return A;
//        }
//        return null;
//
//    }
//
//    @Override
//    public int compareTo(Element o) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    /**
//     * Считаем что матрицы прямоугольные
//     *
//     * @param x
//     * @param ring
//     *
//     * @return
//     */
//    @Override
//    public int compareTo(Element x, Ring ring) {
//        if (x.numbElementType() != Ring.MatrixD) {
//            return (x.numbElementType() > Ring.MatrixD) ? -1 : 1;
//        }
//        int sizeThis = M.length;
//        int sizeX = ((MatrixD) x).M.length;
//        if (sizeThis != sizeX) {
//            return (sizeX > sizeThis) ? -1 : 1;
//        }
//        int colNum = M[0].length;
//        int colNumX = ((MatrixD) x).M[0].length;
//        if (colNum != colNumX) {
//            return (colNumX > colNum) ? -1 : 1;
//        }
//        int flag;
//        for (int i = 0; i < sizeThis; i++) {
//            for (int j = 0; j < M[i].length; j++) {
//                flag = M[i][j].compareTo(((MatrixD) x).M[i][j], ring);
//                if (flag != 0) {
//                    return flag;
//                }
//            }
//        }
//        return 0;
//    }
//
//    @Override
//    public Boolean isZero(Ring ring) {
//        Boolean f = true;
//        for (int i = 0; i < this.M.length; i++) {
//            for (int j = 0; j < this.M.length; j++) {
//                if (!(this.M[i][j].equals(ring.numberZERO, ring))) {
//                    f = false;
//                    return f;
//                }
//            }
//        }
//        return f;
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public boolean equals(Element x, Ring r) {
//        MatrixD matS = (MatrixD) x;
//        return (subtract(matS, r)).isZero(r);
//
//    }
//
//    @Override
//    public Boolean isOne(Ring ring) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public Object clone() {
//        MatrixD temp = new MatrixD();
//        temp.M = new Element[this.M.length][this.M[0].length];
//        for (int i = 0; i < temp.M.length; i++) {
//            for (int j = 0; j < temp.M[i].length; j++) {
//                temp.M[i][j] = (Element) this.M[i][j].clone();
//            }
//        }
//        return temp;
//    }
//
//    @Override
//    public MatrixD expand(Ring ring) {
//        for (Element[] M1 : M) {
//            for (int j = 0; j < M1.length; j++) {
//                M1[j] = M1[j].expand(ring);
//            }
//        }
//        return this;
//    }
//
//     /**
//     * Интеграл по первой переменной
//     * @param r Ring
//     * @return 
//     */
//    @Override
//    public Element integrate(Ring r) {
//        return  integrate(0, r);
//    }
//     /**
//     * Интеграл по  переменной num
//     * @param num - variable of integration number in Ring
//     * @param ring - Ring
//     * @return matrix with integrals of all its elements
//     */
//    @Override
//    public Element integrate(int num, Ring ring) {
//        for (Element[] M1 : M) {
//            for (int j = 0; j < M1.length; j++) {
//                M1[j] = M1[j].integrate(num, ring);
//            }
//        }
//        return this;
//    }
//
//    @Override
//    public Element D(int num, Ring ring) {
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < M[i].length; j++) {
//                M[i][j] = M[i][j].D(num, ring).expand(ring);
//            }
//        }
//        return this;
//    }
//   
//    @Override
//    public Element factorLnExp(Ring ring) {
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < M[i].length; j++) {
//                M[i][j] = M[i][j].factorLnExp(ring);
//            }
//        }
//        return this;
//    }
//
//    @Override
//    public Element expandLn(Ring ring) {
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < M[i].length; j++) {
//                M[i][j] = M[i][j].expandLn(ring);
//            }
//        }
//        return this;
//    }
//
//    @Override
//    public Element factor(Ring ring) {
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < M[i].length; j++) {
//                M[i][j] = M[i][j].ExpandFnameOrId().factor(ring);
//            }
//        }
//        return this;
//    }
//
//    /**
//     * Из матрицы копируем блок, начиная с строки begs(вклчительно), до строки
// ends(не включая), от столбца col1(включая) до столбца endc(не включая).
//     *
//     * @param row1 nmber of the first row (start from 0)
//     * @param rowsNumb number of rows
//     * @param col1 nmber of the first column (start from 0)
//     * @param colNumber number of columns
//     * @param ring кольцо
//     * @return
//     */
//    public MatrixD submatrix(int row1, int rowsNumb, int col1, int colNumber, Ring ring) {
//        MatrixD Matr = new MatrixD();
//        Matr.M = new Element[rowsNumb][colNumber];
//        for (int i = row1; i < row1+rowsNumb; i++) {
//            System.arraycopy(M[i], col1, Matr.M[i - row1], 0, colNumber);
//        }
//        return Matr;
//    }
//
//    /**
//     * Составляем матрицу из элементов матрицы this, вырожденность которой нужно
//     * проверить для посторения матрицы К.
//     *
//     * @param k шаг
//     * @param ring кольцо
//     *
//     * @return
//     */
//    /**
//     * Вычисление определителя матрицы для this, используя алгоритм Сейфуллина,
//     * не содержит делений и сокращений
//     *
//     * @return Element - determinant
//     *
//     * @author Pereslavtseva
//     */
//    public Element det(Ring ring) {
//        charPolynomMatrixD q = new charPolynomMatrixD(this);
//        return q.detSeifullin(ring);
//    }
//
//    public long[][] matrixPartForCharPolKrilov(int k, Ring ring) {
//        long[][] C = new long[M.length / k][M.length / k];
//        int ii = 0;
//        for (int i = k - 1; i < M.length; i += k) {
//            int jj = 0;
//            for (int j = k - 1; j < M.length; j += k) {
//                C[ii][jj] = Long.valueOf(M[i][j].toString(ring));
//                jj++;
//            }
//            ii++;
//        }
//        return C;
//    }
//
//    /**
//     * След матрицы
//     *
//     * @param ring кольцо или тропическое полукольцо
//     *
//     * @return след матрицы
//     */
//    public Element track(Ring ring) {
//        Element tr = ring.numberZERO;
//        for (int i = 0; i < M.length; i++) {
//            tr = tr.add(M[i][i], ring);
//        }
//        return tr;
//    }
//
//    public void del(int[] k, Ring ring) {
//        Element[][] S = new Element[M.length - k.length][M.length];
//        int c = 0;
//        int b = 0;
//        for (int i = 0; i < M.length; i++) {
//            if (c < k.length && i == k[c]) {
//                c++;
//                continue;
//            }
//            System.arraycopy(M[i], 0, S[b], 0, M[i].length);
//            b++;
//        }
//        for (int i = 0; i < S.length; i++) {
//            S[i] = Array.delcolumns(S[i], k, ring);
//        }
//        M = S;
//    }
//
//    /**
//     * Удаление из матрицы i-ой строки и j-ого столбца Вычисление всевозможных
//     * миноров матрицы
//     *
//     * @param i номер строк j номер столбца
//     * @param ring кольцо
//     *
//     *
//     * @return
//     */
//    public MatrixD Minor(int i, int j, Ring ring) {
//        Element[][] S = new Element[M.length - 1][M.length - 1];
//        int b = 0;
//        for (int k = 0; k < M.length; k++) {
//            if (k == i) {
//                continue;
//            }
//            System.arraycopy(M[k], 0, S[b], 0, j);
//            System.arraycopy(M[k], j + 1, S[b], j, M.length - j - 1);
//
//            b++;
//            //}
//        }
//        MatrixD W = new MatrixD(S);
//        return W;
//    }
//
//    public static void toString(Element[][] a) {
//        for (int i = 0; i < a.length; i++) {
//            System.out.println("s" + Arrays.toString(a[i]));
//        }
//
//    }
//
//    /**
//     * Вычисляет величину Tr A = tr A + tr A^2 + ... + tr A^n
//     *
//     * @param ring кольцо или тропическое полукольцо
//     *
//     * @return Tr A
//     */
//    public Element Tr(Ring ring) {
//        Element rez = this.track(ring);
//        MatrixD A1 = this.copy();
//        for (int m = 2; m <= M.length; m++) {
//            A1 = this.multCU(A1, ring);
//            Element tr1 = A1.track(ring);
//            rez = rez.add(tr1, ring);
//        }
//        return rez;
//    }
//
//    public MatrixD multiplyByScalar(Element e, Ring r) {
//        Element[][] S = new Element[M.length][M.length];
//        for (int i = 0; i < S.length; i++) {
//            for (int j = 0; j < S[i].length; j++) {
//                S[i][j] = M[i][j].multiply(e, r);
//            }
//        }
//        return new MatrixD(S);
//    }
//
//    public VectorS multiplyByColumn(VectorS e, Ring r) {
//        Element[] res = new Element[M[0].length];
//        for (int i = 0; i < res.length; i++) {
//            res[i] = r.numberZERO;
//        }
//        for (int j = 0; j < M.length; j++) {
//            for (int i = 0; i < M[j].length; i++) {
//                res[j] = res[j].add(M[j][i].multiply(e.V[i], r), r);
//            }
//        }
//        return new VectorS(res);
//    }
//
//    @Override
//    public Element add(Element e, Ring r) {
//        switch (e.numbElementType()) {
//            case Ring.MatrixD:
//                return add((MatrixD) e, r);
//            case Ring.MatrixS:
//                return new MatrixS(this, r).add(e, r); // пока только так(
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public Element subtract(Element e, Ring r) {
//        switch (e.numbElementType()) {
//            case Ring.MatrixD:
//                return subtract((MatrixD) e, r);
//            case Ring.MatrixS:
//                return new MatrixS(this, r).subtract(e, r); // пока только так(
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public Element multiply(Element e, Ring r) {
//        switch (e.numbElementType()) {
//            case Ring.MatrixD:
//                return multCU((MatrixD) e, r);
//            case Ring.VectorS:
//                return multiplyByColumn((VectorS) e, r);
//            case Ring.MatrixS:
//                return new MatrixS(this, r).multiply(e, r); // пока только так(
//            default:
//                return multiplyByScalar(e, r);
//        }
//    }
//
//    public Element putElement(Element e, int i, int j) {
//        Element[][] newM = M.clone();
//        newM[i][j] = e;
//        return new MatrixD(newM);
//    }
//
//    public Element getElement(int i, int j) {
//        return M[i][j];
//    }
//
//    public int rowNum() {
//        return M.length;
//    }
//
//    public int colNum() {
//        return M[0].length;
//    }
//
//    @Override
//    public int numbElementType() {
//        return Ring.MatrixD;
//    }
//
//    /**
//     * Возвращаем матрицу знаков входящих в нее элементов( F.e.
//     * A=[[-3,5],[0,-x]]; ---> [[-1,1],[0,-1]] )
//     *
//     * @param ring кольцо
//     *
//     * @return
//     */
//    public MatrixD signum(Ring ring) {
//        Element[][] signumM = new Element[M.length][];
//        int signumEl;
//        for (int i = 0; i < M.length; i++) {
//            signumM[i] = new Element[M[i].length];
//            for (int j = 0; j < signumM[i].length; j++) {
//                signumEl = M[i][j].signum();
//                signumM[i][j] = (signumEl == 1) ? ring.numberONE : (signumEl == 0) ? ring.numberZERO : ring.numberMINUS_ONE;
//            }
//        }
//        return new MatrixD(signumM);
//    }
//
//    /**
//     * Вычисляем модули входящих в матрицу элементов.... результат матрица.
//     *
//     * @param ring
//     *
//     * @return
//     */
//    @Override
//    public Element abs(Ring ring) {
//        Element[][] absM = new Element[M.length][];
//        for (int i = 0; i < M.length; i++) {
//            absM[i] = new Element[M[i].length];
//            for (int j = 0; j < absM[i].length; j++) {
//                absM[i][j] = M[i][j].abs(ring);
//            }
//        }
//        return new MatrixD(absM);
//    }
//
//    @Override
//    public Element Factor(boolean doNewVector, Ring ring) {
//        Element[][] absM = new Element[M.length][];
//        for (int i = 0; i < M.length; i++) {
//            absM[i] = new Element[M[i].length];
//            for (int j = 0; j < absM[i].length; j++) {
//                absM[i][j] = M[i][j].Factor(false, ring);
//            }
//        }
//        return new MatrixD(absM);
//    }
//
//    @Override
//    public Element Expand(Ring ring) {
//        Element[][] absM = new Element[M.length][];
//        for (int i = 0; i < M.length; i++) {
//            absM[i] = new Element[M[i].length];
//            for (int j = 0; j < absM[i].length; j++) {
//                absM[i][j] = M[i][j].Expand(ring);
//            }
//        }
//        return new MatrixD(absM);
//    }
//
//    @Override
//    public Element closure(Ring ring) {
//        return new MatrixD(new MatrixS(M, ring).closure( ring), ring);
//       // return oneMatrixD(M.length, M[0].length, ring).subtract(inverse(ring), ring).inverse(ring);
//    }
//
//    @Override
//    public Element inverse(Ring ring) {
//        return new MatrixD(new MatrixS(M, ring).inverse(ring.numberONE, ring), ring);
//    }
//
//    @Override
//    public Element conjugate(Ring ring) {
//        if (ring.numberONE instanceof Complex) {
//            Element[][] newM = new Element[M[0].length][M.length];
//            for (int i = 0; i < M.length; i++) {
//                for (int j = 0; j < M[i].length; j++) {
//                    newM[j][i] = M[i][j].conjugate(ring);
//                }
//            }
//            return new MatrixD(newM);
//        } else {
//            return transpose(ring);
//        }
//    }
//
//    public Element min(Ring r) {
//        int i = 0, n = M.length;
//        int ma = M[0].length;
//        Element min = M[0][0].abs(r);
//        Element m;
//        for (int j = 0; j < n; j++) {
//            for (int k = 0; k < ma; k++) {
//                m = M[j][k];
//                if (m.abs(r).compareTo(min, r) == -1) {
//                    min = m.abs(r);
//                }
//            }
//        }
//        return min;
//    }
//
//    public Element max(Ring r) {
//        int i = 0, n = M.length;
//        int ma = M[0].length;
//        Element max = M[0][0];
//        Element m;
//        for (int j = 0; j < n; j++) {
//            for (int k = 0; k < ma; k++) {
//                m = M[j][k];
//                if (m.abs(r).compareTo(max, r) == 1) {
//                    max = m.abs(r);
//                }
//            }
//        }
//        return max;
//    }
//    public static Element[] rotateLeft(Element[] row){
//      Element[] newRow=new Element[row.length];
//      newRow[row.length-1]=row[0];
//      for (int i = 0; i < row.length-1; i++)
//            newRow[i]=row[i+1];
//      return newRow;
//    }
//   /**
//    * Rotate right if numb>0, rotate left if numb<0
//    * @param row
//    * @param numb -- value of ticks for the right movings 
//    *                (negativ number -- for the left movings )
//    * @return 
//    */
//   public static Element[] rotate(Element[] row, int numb){
//       Element[] newRow=row; int n=0;
//       while(n<numb){newRow=rotateRight(newRow); n++;}
//       while(n>numb){newRow=rotateLeft(newRow); n--;} 
//       return newRow;
//   }
//   
//   public static Element[] rotateRight(Element[] row){
//      Element[] newRow=new Element[row.length];
//      newRow[0]=row[row.length-1];
//        for (int i = row.length-1; i >0; i--)
//            newRow[i]=row[i-1];
//      return newRow;
//    }
//   
//      /**
//    * Building of Akritas matrix for polynomial p1 and p2
//    * @param p1 - first dense polynomial from highest coeffs
//    * @param p2 - second dense polynomial from highest coeffs
//    * @param int[][] type - {{t1,t2,..,tk},{ m1,m2,..,mk}} -- type(number of pol) and value of moving for each row
//    * @param ring - Ring
//    * @return  - Akritas matrix of k \times 2max(n1,n2)
//    */
//    public static MatrixD Akritas(Element[] p1, Element[] p2, int[][] type, Ring ring){
//        int n1=p1.length;int n2=p2.length; int m=2*Math.max(n1, n2);
//        int n=type[0].length;
//        Element[][] e=new Element[n][m];
//        Element[] row1=new Element[m];for (int i = 0; i < n1; i++) {row1[i]=p1[i];}
//        Element[] row2=new Element[m];for (int i = 0; i < n2; i++) {row2[i]=p2[i];}
//        for (int i = n1; i < m; i++) {row1[i]=ring.numberZERO;}
//        for (int i = n2; i < m; i++) {row2[i]=ring.numberZERO;}
//        if ((type.length&1)!=1)
//        {for (int i = 0; i < n; i++) { 
//            e[i]=(type[0][i]==1)?rotate(row1,type[1][i]):rotate(row2,type[1][i]);
//        }}
//        return new MatrixD(e);
//    }
//   /**
//    * Building of Sylvester matrix for polynomial p1 and p2
//    * @param p1 - first polynomial
//    * @param p2 - second polynomial
//    * @param type - 0 classical form of size n1+n2;
//    *             - 1 "regular form" of size 2max(n1,n2)
//    * @param ring - Ring
//    * @return  - Sylvester matrix
//    */
//    public static MatrixD Sylvester(Polynom p1, Polynom p2, int type, Ring ring){
//        if(p1.isZero(ring)||p2.isZero(ring)) return null;
//        int var1=p1.powers.length/p1.coeffs.length;
//        int var2=p2.powers.length/p2.coeffs.length;
//        if(var1!=var2) return null;
//      FactorPol fp1=p1.toCoeffsHighestVar(var1);
//      FactorPol fp2=p2.toCoeffsHighestVar(var1);
//      int n1=fp1.powers[0];
//      int n2=fp2.powers[0];
//      int n=(type==0)?n1+n2:2*Math.max(n1, n2);
//      if(n1<n2){FactorPol fpTemp=fp1; fp1=fp2; fp2=fpTemp; int t=n1; n1=n2; n2=t;}
//
//      Element[][] e=new Element[n][n];
//      Element[] m1=new Element[n];
//      Element[] m2=new Element[n];
//      for (int i = 0; i < n; i++) m1[i]=m2[i]=ring.numberZERO;
//      for (int i = 0; i < fp1.powers.length; i++)
//            m1[n1-fp1.powers[i]]=fp1.multin[i];
//      for (int i = 0; i < fp2.powers.length; i++)
//            m2[n2-fp2.powers[i]]=fp2.multin[i];
//      e[0]=m1;
//      if(type==0){
//        for (int i = 1; i < n2; i++) e[i]=rotateRight(e[i-1]);
//        e[n2]=m2;
//        for (int i = n2+1; i < n; i++) e[i]=rotateRight(e[i-1]);
//      }else{
//         for (int i = 2; i < n; i+=2) e[i]=rotateRight(e[i-2]);
//         for (int i = 0; i < n1-n2; i++)m2= rotateRight(m2);
//         e[1]=m2;
//         for (int i = 3; i < n; i+=2) e[i]=rotateRight(e[i-2]);
//      }
//        return new MatrixD(e);
//   }
//    
//    /**
//     * Сложение матриц в Булевой алгебре (если пользователь указал
//     * в матрицах числа не принадлежащие множеству {0,1}, то будем считать их =1)
//     * @param b матрица
//     * @return
//     */
//    public MatrixD or(MatrixD b){
//        MatrixD rez = new MatrixD(new Element[b.M.length][b.M.length]);
//        for (int i = 0; i < b.M.length; i++) {
//            for (int j = 0; j < b.M.length; j++) {
//                if((this.M[i][j].longValue()==0) && (b.M[i][j].longValue()==0)){
//                    rez.M[i][j]=new NumberZ64(0);
//                }else{
//                    rez.M[i][j]=new NumberZ64(1);
//                }
//            }
//        }
//        return rez;
//    }
//    
//    /**
//     * Умножение матриц в Булевой алгебре (если пользователь указал
//     * в матрицах числа не принадлежащие множеству {0,1}, то будем считать их =1)
//     * @param b матрица
//     * @return
//     */
//    public MatrixD and(MatrixD b){
//        MatrixD rez = new MatrixD(new Element[M.length][b.M[0].length]);
//        for (int i = 0; i < M.length; i++) {
//            for (int j = 0; j < b.M[0].length; j++) {
//                rez.M[i][j] = new NumberZ64(0);
//                for (int k = 0; k < M[0].length; k++) {
//                    if((this.M[i][k].longValue() != 0) && (b.M[k][j].longValue() != 0)){
//                        rez.M[i][j] = new NumberZ64(1);
//                        break;
//                    }
//                } 
//            }
//        }
//        return rez;
//    }
//    
    public static void main(String[] args) {
           Ring ring=new Ring("Z[x]");
           int[][] mat1={{1,3},{2,4}};
           MatrixD[][]MD2x2= new MatrixD[2][2];
           for (int i = 0; i < 2; i++) {for (int j = 0; j < 2; j++) MD2x2[i][j]=new MatrixD(mat1, ring);}
           BigMatrixS BMS= new  BigMatrixS(MD2x2, ring); 
//            BigMatrixS BMSa=BMS.add(BMS, ring);
//          BigMatrixS BMSb=BMS.subtract(BMS, ring);
          BigMatrixS BMSss[]=BMS.split(BMS, ring);
          for (int i = 0; i < BMSss.length; i++) {
              System.out.println(BMSss[i]);
        }
           System.out.println("BMS="+BMS.toString(ring));
//           System.out.println("BMSa="+BMSa.toString(ring));
//           System.out.println("BMSb="+BMSb.toString(ring));

           
        }
}
// мне бы хотелось видеть печать такую:
//      [[ 1,3,1,3],
//       [ 2,4,2,4]];
// то есть так -- как выглядела бы одна матрица в печати.
// Поэтому напишите программу склейки  в одну MatrixD. А потом просто печатайте ее программойй toString()