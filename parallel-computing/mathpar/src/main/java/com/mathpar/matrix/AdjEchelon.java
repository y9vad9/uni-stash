package com.mathpar.matrix;
import com.mathpar.number.*;
/**  // Второй рекурсивный алгоритм с перестановками в верхний левый угол. Ноябрь 2011
 * @author gennadi
 */
public class AdjEchelon {

  /** Adjoint matrix */
  public MatrixS A;
  /** Echelon form of the initial matrix.
   *  It head elements placed in the positions {(I,J),(I+1,J+1),..,(I+rank-1, J+rank-1)}*/
  public MatrixS S;
  /** Determinant of the head minor of rank "rank" of initial matrix */
  public Element Det;
  /** Rank of the initial matrix.*/
  public int rank;
  /** Номер первой ненулевой строки у эшелонной матрицы и ее матрицы перестановок*/
  public int I;
  /** Номер первого ненулевой столбца у эшелонной матрицы и ее матрицы перестановок*/
  public int J;
  /** Перестановка строк матрицы S. {i1,.... in,j1...jn}
   * Отсортирована по возрастанию первая половина {i1,.... in}, - это строки отправки.
   *  Тождественная перестановка -- это пустой массив.  */
  public int[] Er;
  /** Перестановка столбцов матрицы S.  {i1,.... in,j1...jn}
   *  Тождественная перестановка -- это пустой массив.  */
  public int[] Ec;
  private static final int[] IdPerm = new int[0];
  public AdjEchelon(MatrixS A, int[] Er, int[] Ec, MatrixS S, Element Det, int I, int J, int rank) {
    this.A = A; this.S = S; this.Er = Er; this.Ec = Ec;
    this.I = I;  this.J = J; this.rank = rank;  this.Det = Det;
  }
  /** Main recursive constructor of AdjEchelon
   *                                       VERSION 26.11.2011
   * @param m --  входная матрица
   * @param d0 -- determinant of the last upper block. For first step: d0=1.
   *    A -- adjoin matrix for matrix m: Am=S -- echelon form of m.
   *    S -- echelon form for matrix m
   *    Det -- determinant
   * @param shiftR  -- the number of the firs row in matrix m.  For first step: =0.
   * @param shiftC  -- the number of the firs row in matrix m.  For first step: =0.
   * @param ring  Ring
   */
  public AdjEchelon(MatrixS m, Element d0, int shiftR, int shiftC, Ring ring) {
    // long t1=System.currentTimeMillis(), t2=0;
    I = shiftR; J = shiftC; int N = m.size; //N is a number of rows in the matrix m
    if (m.isZero(ring)) {
      A = MatrixS.scalarMatrix(m.size, d0, ring); Er = IdPerm;   Ec = IdPerm;
      S = MatrixS.zeroMatrix(N); Det = d0; rank = 0;
    } else {
      if (N == 1) {
        A = new MatrixS(1, 1, new Element[][]{{d0}}, new int[][]{{0}});
        Er = IdPerm; Ec = IdPerm;  S = m; Det = m.M[0][0];   rank = 1;
      } else {
        N = N >>> 1;
        int shiftRR = (shiftR > N) ? (shiftR - N) : 0;
        int shiftCC = (shiftC > N) ? (shiftC - N) : 0;
        MatrixS[] M = m.split();
        AdjEchelon m11 = new AdjEchelon(M[0], d0, shiftR, shiftC, ring);              // 1 STEP //
        M[1] = M[1].permutationOfRows(m11.Er);
        M[2] = M[2].permutationOfColumns(m11.Ec);
        Element d11 = m11.Det;
        Element d11_2 = d11.multiply(d11, ring);
        MatrixS y11 = m11.S.ES_min_dI(d11, m11, ring);
        MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1], d0, ring);
        MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);
        AdjEchelon m21 = new AdjEchelon(M21_1, d11, shiftRR, shiftC + m11.rank, ring);             // 2 STEP  //
        AdjEchelon m12 = new AdjEchelon(M12_1.barImulA(m11), d11, shiftR + m11.rank, shiftCC, ring);//3 STEP  //
        Element d12 = m12.Det;         Element d21 = m21.Det;
        M[3] = M[3].permutationOfRows(m21.Er).permutationOfColumns(m12.Ec);
        M[2] = M[2].permutationOfRows(m21.Er);
        m11.A = m11.A.permutationOfRows(m12.Er).permutationOfColumns(m12.Er);
        m11.S = m11.S.permutationOfColumns(m21.Ec);
        M12_1 = M12_1.permutationOfRows(m12.Er).permutationOfColumns(m12.Ec);
        MatrixS M22_1 = ((M[3].multiplyByNumber(d11, ring)).subtract(M[2].multiplyRecursive(M12_1.ETmulA(m11), ring), ring)).divideByNumber(d0, ring);
        MatrixS y21 =  m21.S.ES_min_dI(d21, m21, ring);
        MatrixS y12A = m12.S.ES_min_dI(d12, m12, ring);
        MatrixS M22_2 = (m21.A.multiplyRecursive(M22_1, ring)).multiplyDivRecursive(y12A, d11_2.negate(ring), ring);
        Element ds = d12.multiply(d21, ring).divide(d11, ring);
        AdjEchelon m22 = new AdjEchelon(M22_2.barImulA(m21)                 //  4-STEP //
            , ds, shiftRR + m21.rank, shiftCC + m12.rank, ring);
        Det = m22.Det;
        M22_1 = M22_1.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
        M22_2 = M22_2.permutationOfRows(m22.Er).permutationOfColumns(m22.Ec);
        M12_1 = M12_1.permutationOfColumns(m22.Ec);
        m12.S = m12.S.permutationOfColumns(m22.Ec);
        M[2] = M[2].permutationOfRows(m22.Er);
        m21.A = m21.A.permutationOfRows(m22.Er).permutationOfColumns(m22.Er);
        MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring), ring);
        MatrixS M12_1_I = M12_1.ImulA(m11);
        MatrixS M22_2_I = M22_2.ImulA(m21);
        MatrixS y12B = m12.S.ES_min_dI(d12, m12, ring);
        MatrixS M12_2 = ((((m11.S.multiplyDivRecursive(m21.A.ETmulA(m21), d11, ring).multiplyRecursive(M22_1, ring)).subtract((M12_1_I.multiplyByNumber(d21, ring)), ring)).divideByNumber(d11, ring).multiplyRecursive(y12B, ring)).add((m12.S).multiplyByNumber(d21, ring), ring)).divideByNumber(d11, ring);
        MatrixS y22 = m22.S.ES_min_dI(Det, m22, ring);
        MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring), ring);
        MatrixS M22_3 = (M22_2_I.multiplyDivRecursive(y22, ds.negate(ring), ring)).add(m22.S, ring);
        MatrixS A1 = m12.A.multiplyRecursive(m11.A, ring);
        MatrixS A2 = m22.A.multiplyRecursive(m21.A, ring);
        MatrixS A1_E12 = A1.ETmulA(m12);
        MatrixS L = (A1.subtract(M12_1_I.multiplyDivRecursive(A1_E12, d11, ring), ring)).divideMultiply(d11, Det, ring);
        MatrixS Q = (A2.subtract(M22_2_I.multiplyDivRecursive(A2.ETmulA(m22), ds, ring), ring)).divideByNumber(d21, ring);
        MatrixS F = (m11.S.multiplyDivMulRecursive(m21.A.ETmulA(m21) //  .multiplyLeftE(m21.Ej,m21.Ei)
            , d11, Det, ring).add(M12_2.multiplyDivRecursive(A2.ETmulA(m22), ds, ring), ring)).divideByNumber(d21.negate(ring), ring);
        MatrixS G = (M[2].multiplyDivMulRecursive(m11.A.ETmulA(m11), d0, d12, ring).add(M22_1.multiplyDivRecursive(A1_E12, d11, ring), ring)).divideByNumber(d11.negate(ring), ring);
        MatrixS[] AA = new MatrixS[4];
        AA[0] = (L.add(F.multiplyRecursive(G, ring), ring)).divideByNumber(d12, ring);
        AA[1] = F;
        AA[2] = Q.multiplyDivRecursive(G, d12, ring);
        AA[3] = Q;
        MatrixS M11_3 = M11_2.multiplyDivide(Det, d21, ring);
        MatrixS M21_3 = m21.S.multiplyDivide(Det, d21, ring);
        A = MatrixS.join(AA);
        S = MatrixS.join(new MatrixS[]{M11_3, M12_3, M21_3, M22_3});
        int l_11 = m11.rank; int l_12 = m12.rank; int l_21 = m21.rank; int l_22 = m22.rank;
        rank = l_11 + l_21 + l_12 + l_22;
        int from1 = N;
        int tothe1 = l_11 + shiftR;
        // строим матрицу перестановок строк
        if (from1 <= tothe1) Er = new int[0];
        else {
          int from2 = tothe1;    int tothe2 = tothe1 + l_21;
          int from3 = Math.max(N, shiftR) + l_21; int tothe3 = tothe1+l_21+l_12;
          int from4 = tothe1 + l_12;   int tothe4 = tothe1+l_12+l_21+l_22;
          int l_back = (l_21+l_22 == 0)? 0 : N-tothe1-l_12;
          int Erlen = (tothe1 == N)? l_back : l_21+l_back;
          if (l_21 != 0)  Erlen += l_12;
          if (from3 != tothe3) Erlen += l_22;
          Er = new int[2 * (Erlen)];
          int jj = Erlen;
          if (Erlen != 0) {int ii = 0;
            for (int i = 0; i < l_21; i++) {Er[ii++]=from1++;Er[jj++]=tothe1++;}
            if(l_21!=0)for(int i=0;i<l_12;i++){Er[ii++]=from2++;Er[jj++]=tothe2++;}
            if(from3!=tothe3)for(int i=0;i<l_22;i++){Er[ii++]=from3++;Er[jj++]=tothe3++;}
            if(l_21+l_22>0)for(int i=0;i<l_back;i++){Er[ii++]=from4++;Er[jj++]=tothe4++;}
          }
        }
        // строим матрицу перестановок столбцов
        int from2 = l_11 + l_21 + shiftC; int Ec_len = N - from2; int lll = l_22 + l_12;
        if ((Ec_len <= 0) || (lll == 0)) { Ec = new int[0];}
        else {
          Ec_len += lll;   Ec = new int[2 * Ec_len];
          int jj = Ec_len; from1 = N;  tothe1 = from2;
          for (int i = 0; i < lll; i++) {Ec[i] = from1++;Ec[jj++] = tothe1++;}
          int tothe2 = from2 + lll;
          for (int i = lll; i < Ec_len; i++) {Ec[i]=from2++; Ec[jj++]=tothe2++;}
        }
        int[] ErTr = new int[Er.length];
        int nTr = Er.length >> 1;  int mTr = Ec.length >> 1;
        System.arraycopy(Er, 0, ErTr, nTr, nTr); System.arraycopy(Er, nTr, ErTr, 0, nTr);
        int[] EcTr = new int[Ec.length];
        System.arraycopy(Ec, 0, EcTr, mTr, mTr); System.arraycopy(Ec, mTr, EcTr, 0, mTr);
        A = A.permutationOfRows(Er).permutationOfColumns(Er);
        S = S.permutationOfRows(Er).permutationOfColumns(Ec);
        Ec = MatrixS.permutationsOfOnestep(Ec, m11.Ec, m12.Ec, m21.Ec, m22.Ec, N);
        Er = MatrixS.permutationsOfOnestep(Er, m11.Er, m21.Er, m12.Er, m22.Er, N);
        //          System.out.println("N="+N+" Ec====================="+Array.toString(Ec));
        //          System.out.println("Er=================="+Array.toString(Er));
        //          System.out.println("l_11+l_21+l_12+l_22=================="+l_11+l_21+l_12+l_22);
        //        Array.p("Adj++++ ============="+A);
        //        Array.p("S+++++ ============="+S);
        //        Array.p("Ei"+Array.toString(Ei));Array.p("Ej"+Array.toString(Ej));
        //  t2=System.currentTimeMillis();
        // System.out.println("Time=A  S  =="+(t2-t1)+" millisec. N="+N+"     "+" A.M.length="+ A.M.length);
      }
    }
  }

  public static void main(String[] args) {
    Ring ring = new Ring("Z[x]");
    long t11=0, t22=0, n11=0, n22=0;
    long[][] m2 = {
      {0, 0, 0, 0},
      {0, 0, 0, 0},
      {0, 6, 0, 5},
      {0, 0, 0, 7}};
    xxx:

    for (int ttt = 0; ttt < 1; ttt++) {
        MatrixS A = new MatrixS(m2, ring);// A=A.multiply(Matrix, ring)
   //   MatrixS A = new MatrixS(8, 8, 30, new int[]{3}, new Random(), NumberZ.ONE, new Ring("Z[x]"));
      //   A.putElement(new NumberZ(2), 1, 4);//   A.putElement(new NumberZ(4), 1, 7);
      //   A.putElement(new NumberZ(5), 0, 0);//   A.putElement(new NumberZ(3), 0, 4);
      //   A.putElement(new NumberZ(5), 4, 4);//  A.putElement(new NumberZ(6), 5, 3);
      //     A.putElement(new NumberZ(7), 7, 6);
//      t11=System.currentTimeMillis();
//      AdjMatrixS xx = new AdjMatrixS(A, ring.numberONE, ring);
//      t22=System.currentTimeMillis();
//      n11=System.currentTimeMillis();
      AdjEchelon x = new AdjEchelon(A, ring.numberONE, 0, 0, ring);
 //    n22=System.currentTimeMillis();
      System.out.println("i=" + ttt + " #############################################################################");
      System.out.println("A=" + A + "\n x.Ec=" + Array.toString(x.Ec) + "Er=" + Array.toString(x.Er));
      MatrixS Anew = A.permutationOfRows(x.Er).permutationOfColumns(x.Ec);
      System.out.println("Anew=" + Anew);
      MatrixS Aj_A = x.A.multiply(Anew, ring);
      System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{A, x.S.subtract(Aj_A, ring)}, "A", ring));
      System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{x.S, x.A, Anew}, "=", ring));
      if (x.S.subtract(Aj_A, ring).isZero(ring))  System.out.println("GOOD A!! N=" + ttt);
      else {System.out.println("errorrrrrr A.? N=" + ttt); break xxx; }
   // System.out.println("time=" + (t22-t11)+"mks;"+ "  timeN=" + (n22-n11)+"mks");
  }
 }
}
