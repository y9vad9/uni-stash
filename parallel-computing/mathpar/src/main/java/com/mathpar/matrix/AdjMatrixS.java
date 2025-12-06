
package com.mathpar.matrix;
import com.mathpar.number.*;
import com.mathpar.number.Ring;
/**
 * /////
 * @author gennadi
 * Первый, базовый вариант для вычисления Adjoint+Echelon
 */
public class AdjMatrixS extends Element{

    private static final long serialVersionUID = 10000000000111L;
   /** adjoint matrix */
   public MatrixS A;
   /** список строк матрицы Е */
   public int[] Ei;
   /** список столбцов матрицы Е, которые соответствуют
    * ее списку строк:  (E[Ei[i]][Ej[i]] = 1) */
   public int[] Ej;
   /** echelon form for the initial matrix  */
   public MatrixS S;
   /** determinant of the initial matrix */
   public Element Det;



   public AdjMatrixS(MatrixS A, int[] Ei, int[] Ej, MatrixS S, Element Det){
           this.A=A;
           this.Ei=Ei;
           this.Ej=Ej;
           this.S=S;
           this.Det=Det;
    }

 /** Конструктор класса AdjMatrixS
  *                                        FIRST VERSION
  * @param m --  входная матрица
  * @param d0 -- determinant of the last upper block. For first step: d0=1.
  *    A -- adjoin matrix for matrix m: Am=S -- echelon form of m.
  *    S -- echelon form for matrix m
  *    Det -- determinant
  *    Ei,Ej -- obtained E-matrix
  * @param ring  Ring
  */
 public  AdjMatrixS(MatrixS m, Element d0, Ring  ring ) {
 //    Array.p(""+m+"  do====="+d0);Array.p(""+m.print()+"  do="+d0);
   // long t1=System.currentTimeMillis(), t2=0;
   int N =m.size;         // The number of rows in the matrix m
   if (m.isZero(ring)) {//Array.p(" ZERO_Matr  ");
   A= MatrixS.scalarMatrix(m.size,d0,ring);
        Ei=new int[0]; Ej= new int[0]; S=  MatrixS.zeroMatrix(N); Det=d0;}
   else{
     if (N == 1){ A= new MatrixS(1,1,new Element[][]{{d0}},new int[][]{{0}});
                     Ei= new int[]{0}; Ej= new int[]{0}; S=m; Det=m.M[0][0];}
     else{
       N = N>>>1;
       MatrixS[] M = m.split();
       AdjMatrixS m11 = new AdjMatrixS(M[0], d0, ring);              // 1 STEP //
       Element d11=m11.Det;
       Element d11_2=d11.multiply(d11, ring);
       MatrixS y11 = m11.S.ES_min_dI(d11, m11.Ei, m11.Ej, ring);
       MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1],d0, ring);
       MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);
       MatrixS M22_1 = ((M[3].multiplyByNumber(d11, ring)).subtract(M[2]
                       .multiplyRecursive(M12_1.multiplyLeftE(m11.Ej,m11.Ei), ring)  , ring))
                       .divideByNumber(d0, ring);
       AdjMatrixS m21 = new AdjMatrixS(M21_1, d11, ring);          // 2 STEP  //
       Element d21=m21.Det;
       AdjMatrixS m12 = new AdjMatrixS(M12_1.multiplyLeftI(
               Array.involution(m11.Ei,N)), d11, ring);       //  3 STEP  //
       Element d12=m12.Det;
       MatrixS y12 = m12.S.ES_min_dI(d12, m12.Ei, m12.Ej, ring);

       MatrixS M22_2 = (m21.A.multiplyRecursive(M22_1, ring))
                       .multiplyDivRecursive(y12,  d11_2.negate(ring), ring);
       Element ds=d12.multiply(d21, ring).divide(d11, ring);
       AdjMatrixS m22 = new AdjMatrixS(M22_2.multiplyLeftI(
                      Array.involution(m21.Ei,N)), ds, ring);  //  4-STEP //
       Det=m22.Det;
       MatrixS y21 = m21.S.ES_min_dI(d21, m21.Ei, m21.Ej, ring);
        //Array.p("m21.S=="+m21.S); Array.p("y21==="+y21);

       MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring), ring);
       // M12_2={{( I(m11.Ei)*M12_1*d21 - (m11.S* (E^T(m21.Ej,m21.Ei) * m21.A) / d11) *M22_1 )/(-d11)}Y12 + m12.S*d21}/d11
  //  13/06/2011 В этой формуле заменили (-d11) на (d11) и поменяли местами уменьшаемое и вычитаемое
       MatrixS M12_2 =(((    (m11.S.multiplyDivRecursive((m21.A
          .multiplyLeftE(m21.Ej,m21.Ei)),d11, ring).multiplyRecursive(M22_1, ring) )
          .subtract( (M12_1.multiplyLeftI(m11.Ei).multiplyByNumber(d21, ring)), ring)
                         ).divideByNumber(d11, ring).multiplyRecursive(y12, ring)
                        ).add((m12.S).multiplyByNumber(d21, ring), ring)
                       ).divideByNumber(d11, ring);
       MatrixS y22 = m22.S.ES_min_dI(Det, m22.Ei, m22.Ej, ring);
   //Array.p("m22.S=="+m22.S); Array.p("y22==="+y22);
       MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring), ring);
   //  Array.p("M12_3=="+M12_3);
       MatrixS M22_3 = ((M22_2.multiplyLeftI(m21.Ei))
                          .multiplyDivRecursive(y22, ds.negate(ring)  , ring)).add(m22.S, ring);
       MatrixS A1=m12.A.multiplyRecursive(m11.A, ring);
       MatrixS A2=m22.A.multiplyRecursive(m21.A, ring);
       MatrixS L = (A1.subtract((M12_1.multiplyLeftI(m11.Ei)).
                     multiplyDivRecursive(A1.multiplyLeftE(m12.Ej,m12.Ei), d11, ring), ring)
                    ).divideMultiply(d11, Det, ring);
       MatrixS P = (A2.subtract((M22_2.multiplyLeftI(m21.Ei)).
                      multiplyDivRecursive(A2.multiplyLeftE(m22.Ej,m22.Ei), ds, ring), ring)
                    ).divideByNumber(d21, ring);
       MatrixS F = (m11.S.multiplyDivMulRecursive(m21.A
                           .multiplyLeftE(m21.Ej,m21.Ei), d11, Det, ring)
                           .add(M12_2.multiplyDivRecursive(A2
                           .multiplyLeftE(m22.Ej,m22.Ei), ds, ring), ring)
                    ).divideByNumber(d21.negate(ring), ring);
       MatrixS G = (M[2].multiplyDivMulRecursive(m11.A
                     .multiplyLeftE(m11.Ej,m11.Ei), d0, d12, ring)
                     .add(M22_1.multiplyDivRecursive(A1
                     .multiplyLeftE(m12.Ej,m12.Ei), d11, ring), ring)).divideByNumber(d11.negate(ring), ring);
       MatrixS[] RR = new MatrixS[4];
       RR[0] =(L.add( F.multiplyRecursive(G, ring) , ring) ).divideByNumber(d12, ring); RR[1] =  F;
       RR[2] = P.multiplyDivRecursive(G, d12, ring);                     RR[3] =  P;
       MatrixS M11_3 = M11_2.multiplyDivide(Det, d21, ring);
       MatrixS M21_3 = m21.S.multiplyDivide(Det, d21, ring);
       Ei = new int[m11.Ei.length+m12.Ei.length+m21.Ei.length+m22.Ei.length];
       Ej = new int[Ei.length];
       int j=0;
       for (int i = 0; i < m11.Ei.length;) {Ei[j] = m11.Ei[i];
                                           Ej[j++] = m11.Ej[i++];  }
       for (int i = 0; i < m12.Ei.length;) {Ei[j] = m12.Ei[i];
                                           Ej[j++] = m12.Ej[i++]+N;}
       for (int i = 0; i < m21.Ei.length;) {Ei[j] = m21.Ei[i]  +N;
                                           Ej[j++] = m21.Ej[i++];  }
       for (int i = 0; i < m22.Ei.length;) {Ei[j] = m22.Ei[i]  +N;
                                           Ej[j++] = m22.Ej[i++]+N;}
       A= MatrixS.join(RR);
       S=MatrixS.join(new MatrixS[] {M11_3, M12_3, M21_3, M22_3});
     //   Array.p("A="+A);
    //    Array.p("S="+S);
    //    Array.p("Ei"+Array.toString(Ei));Array.p("Ej"+Array.toString(Ej));
      //  t2=System.currentTimeMillis();
      // System.out.println("Time=A  S  =="+(t2-t1)+" millisec. N="+N+"     "+" A.M.length="+ A.M.length);

     }
   }
 }

/* public  AdjMatrixS__(MatrixS m, Element d0) {
   // long t1=System.currentTimeMillis(), t2=0;
   int N =m.size;         // The number of rows in the matrix m
   if (m.isZero(ring)) { A= MatrixS.scalarMatrix(N,d0);
        Ei=new int[0]; Ej= new int[0]; S=  MatrixS.zeroMatrix(N); Det=d0;}
   else{
     if (N == 1){ A= new MatrixS(1,1,new Element[][]{{d0}},new int[][]{{0}});
                     Ei= new int[]{0}; Ej= new int[]{0}; S=m; Det=m.M[0][0];}
     else{
       N = N>>>1;
       MatrixS[] M = m.split();
       AdjMatrixS m11 = new AdjMatrixS(M[0], d0);              // 1 STEP //
       Element d11=m11.Det;
       Element d11_2=d11.multiply(d11);
 //1v     MatrixS y11 = m11.S.ES_min_dI(d11, m11.Ei, m11.Ej);
          MatrixS y11= m11.S.ES_eraseDi(m11.Ei, m11.Ej );              //////y11
 //1v      MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate());
           MatrixS M21_1 = (M[2].A_ES_min_dI(y11, m11.Ej, d11)).divideByNumber(d0.negate());       //////y11


       MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1],d0);
       MatrixS M22_1 = ((M[3].multiplyByNumber(d11)).subtract(M[2]
                       .multiplyRecursive(M12_1.multiplyLeftE(m11.Ej,m11.Ei))))
                       .divideByNumber(d0);
       AdjMatrixS m21 = new AdjMatrixS(M21_1, d11);          // 2 STEP  //
       Element d21=m21.Det;
       AdjMatrixS m12 = new AdjMatrixS(M12_1.multiplyLeftI(
               Array.involution(m11.Ei,N)), d11);       //  3 STEP  //
       Element d12=m12.Det;
 //v2  MatrixS y12 = m12.S.ES_min_dI(d12, m12.Ei, m12.Ej);
       MatrixS y12= m12.S.ES_eraseDi(m12.Ei, m12.Ej );
 //v2      MatrixS M22_2 = (m21.A.multiplyRecursive(M22_1)).multiplyDivRecursive(y12, d11_2.negate());
       MatrixS M22_2 = (m21.A.multiplyRecursive(M22_1)).A_ES_min_dI(y12, m12.Ej, d12).divideByNumber(d11_2.negate()); ////
       Element ds=d12.multiply(d21).divide(d11);
       AdjMatrixS m22 = new AdjMatrixS(M22_2.multiplyLeftI(
                      Array.involution(m21.Ei,N)), ds);  //  4-STEP //
       Det=m22.Det;
//v3   MatrixS y21 = m21.S.ES_min_dI(d21, m21.Ei, m21.Ej);
       MatrixS y21= m21.S.ES_eraseDi(m21.Ei, m21.Ej );                  ////
//v3   MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate());
       MatrixS M11_2 = (m11.S.A_ES_min_dI(y21, m21.Ej, d21)).divideByNumber(d11.negate());   ////
       MatrixS M12_2 =((((M12_1.multiplyLeftI(m11.Ei).multiplyByNumber(d21))
          .subtract(m11.S.multiplyDivRecursive((m21.A
          .multiplyLeftE(m21.Ej,m21.Ei)),d11).multiplyRecursive(M22_1))
                         ).divideByNumber(  d11.negate()).A_ES_min_dI(y12, m12.Ej, d12)    //.multiplyRecursive(y12)  //v2
                        ).add((m12.S).multiplyByNumber(d21))
                       ).divideByNumber(d11);
 //v4  MatrixS y22 = m22.S.ES_min_dI(Det, m22.Ei, m22.Ej);
       MatrixS y22= m22.S.ES_eraseDi(m22.Ei, m22.Ej );                  ////

//v4       MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate());
       MatrixS M12_3 = (M12_2.A_ES_min_dI(y22, m22.Ej, Det)).divideByNumber(ds.negate());    /////
       MatrixS M22_3 = ((M22_2.multiplyLeftI(m21.Ei))
                          .A_ES_min_dI(y22, m22.Ej, Det)).divideByNumber(ds.negate()   // .multiplyDivRecursive(y22, ds.negate())
                          ).add(m22.S);
       MatrixS A1=m12.A.multiplyRecursive(m11.A);
       MatrixS A2=m22.A.multiplyRecursive(m21.A);
       MatrixS L = (A1.subtract((M12_1.multiplyLeftI(m11.Ei)).
                     multiplyDivRecursive(A1.multiplyLeftE(m12.Ej,m12.Ei), d11))
                    ).divideMultiply(d11, Det);
       MatrixS P = (A2.subtract((M22_2.multiplyLeftI(m21.Ei)).
                      multiplyDivRecursive(A2.multiplyLeftE(m22.Ej,m22.Ei), ds))
                    ).divideByNumber(d21);
       MatrixS F = (m11.S.multiplyDivMulRecursive(m21.A
                           .multiplyLeftE(m21.Ej,m21.Ei), d11, Det)
                           .add(M12_2.multiplyDivRecursive(A2
                           .multiplyLeftE(m22.Ej,m22.Ei), ds))
                    ).divideByNumber(d21.negate());
       MatrixS G = (M[2].multiplyDivMulRecursive(m11.A
                     .multiplyLeftE(m11.Ej,m11.Ei), d0, d12)
                     .add(M22_1.multiplyDivRecursive(A1
                     .multiplyLeftE(m12.Ej,m12.Ei), d11))).divideByNumber(d11.negate());
       MatrixS[] RR = new MatrixS[4];
       RR[0] =(L.add(F.multiplyRecursive(G))).divideByNumber(d12); RR[1] =  F;
       RR[2] = P.multiplyDivRecursive(G, d12);                     RR[3] =  P;
       MatrixS M11_3 = M11_2.multiplyDivide(Det, d21);
       MatrixS M21_3 = m21.S.multiplyDivide(Det, d21);
       Ei = new int[m11.Ei.length+m12.Ei.length+m21.Ei.length+m22.Ei.length];
       Ej = new int[Ei.length];
       int j=0;
       for (int i = 0; i < m11.Ei.length;) {Ei[j] = m11.Ei[i];
                                           Ej[j++] = m11.Ej[i++];  }
       for (int i = 0; i < m12.Ei.length;) {Ei[j] = m12.Ei[i];
                                           Ej[j++] = m12.Ej[i++]+N;}
       for (int i = 0; i < m21.Ei.length;) {Ei[j] = m21.Ei[i]  +N;
                                           Ej[j++] = m21.Ej[i++];  }
       for (int i = 0; i < m22.Ei.length;) {Ei[j] = m22.Ei[i]  +N;
                                           Ej[j++] = m22.Ej[i++]+N;}
//            Array.p(Array.toString(Ei )); Array.p(Array.toString( Ej ));
       A= MatrixS.join(RR);
       S=MatrixS.join(new MatrixS[] {M11_3, M12_3, M21_3, M22_3});
     //  t2=System.currentTimeMillis();
      // System.out.println("Time=A  S  =="+(t2-t1)+" millisec. N="+N+"     "+" A.M.length="+ A.M.length);

     }  // y
   }
 }
*/




}// end of class AdjMatrixS
