
/**
 *
 * @author gennady
 */

package com.mathpar.matrix;
import com.mathpar.number.*;
import com.mathpar.number.Ring;

public class adjLDU {
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
   /** L matrix */
   public MatrixS L;
   /** U matrix */
   public MatrixS U;
      /** diagonal matrix */
   public Element[] D;
      /** список строк матрицы Е полного ранга */
   public int[] Eif;
   /** список столбцов матрицы Е полного ранга */
   public int[] Ejf;

   public adjLDU(MatrixS A, int[] Ei, int[] Ej, MatrixS S,  Element Det, MatrixS L, MatrixS U, Element[] D, int[] Eif, int[] Ejf){
           this.A=A;
           this.Ei=Ei;
           this.Ej=Ej;
           this.S=S;
           this.Det=Det;
           this.L=L;
           this.U=U;
           this.D=D;
           this.Eif=Eif;
           this.Ejf=Ejf;
    }
/**
 * Конструкторы полей для нулевой матрицы
 * @param m_size - размер
 * @param d0 - внешний угловой минор
 * @param ring
 */
     private void ldu_for_ZERO(int m_size, Element d0, Ring  ring){
     A= MatrixS.scalarMatrix(m_size,d0,ring);
     L=  MatrixS.zeroMatrix(m_size); U= L; D= new Element[0];
     Ei=new int[0]; Ej= new int[0]; S= L; Det=d0; Eif=new int[m_size]; Ejf= new int[m_size];
     for (int i=0; i<m_size; i++){Eif[i]=i; Ejf[i]=i;}
   }


 /** Конструктор класса adjLDU
  *
  * @param m --  входная матрица
  * @param d0 -- determinant of the last upper block. For first step: d0=1.
  *    A -- adjoin matrix for matrix m: Am=S -- echelon form of m.
  *    S -- echelon form for matrix m
  *    Det -- determinant
  *    Ei,Ej -- obtained E-matrix
  *    step -- = 0,1,2,3 - the number of block which is computed
  * @param ring  -- ring
  */
 public  adjLDU(MatrixS m, Element d0, int step, Ring  ring ) {

   int N =m.size;         // The number of rows in the matrix m
   if (m.isZero(ring)) { ldu_for_ZERO(N, d0,  ring); return;}
   if (N == 1){ A= new MatrixS(1,1,new Element[][]{{d0}},new int[][]{{0}});
                 Ei= new int[]{0}; Ej= new int[]{0}; S=m; Det=m.M[0][0];
                 L= new MatrixS(Det);U= new MatrixS(Det); D=new Element[]{Det}; Eif= new int[]{0}; Ejf= new int[]{0};
                // System.out.println("UUUUUU000UUUUUUUUUUUUUu="+U);
                 return; }
    N = N>>>1;
    MatrixS[] M = m.split();
//    int flag_of_block_case=0; // 0-standart, 1 - columns inverse, 2 - rows inverse
    adjLDU m11 = new adjLDU(M[0], d0, 0, ring);              // 1 STEP //
       Element d11=m11.Det;
       Element d11_2=d11.multiply(d11, ring);
       MatrixS y11 = m11.S.ES_min_dI(d11, m11.Ei, m11.Ej, ring);
       MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1],d0, ring);
       MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring), ring);

       MatrixS M22_1 = ((M[3].multiplyByNumber(d11, ring)).subtract(M[2]
                       .multiplyRecursive(M12_1.multiplyLeftE(m11.Ej,m11.Ei), ring)  , ring))
                       .divideByNumber(d0, ring);
    //   System.out.println("hhhhhhhhhhhhh="+d11+M21_1);
       adjLDU m21 = new adjLDU(M21_1, d11, 2 ,ring);          // 2 STEP  //
       Element d21=m21.Det;
       adjLDU m12 = new adjLDU(M12_1.multiplyLeftI(
               Array.involution(m11.Ei,N)), d11, 1, ring);       //  3 STEP  //
       Element d12=m12.Det;
       MatrixS y12 = m12.S.ES_min_dI(d12, m12.Ei, m12.Ej, ring);

       MatrixS M22_2 = (m21.A.multiplyRecursive(M22_1, ring))
                       .multiplyDivRecursive(y12,  d11_2.negate(ring), ring);
       Element ds=d12.multiply(d21, ring).divide(d11, ring);
       adjLDU m22 = new adjLDU(M22_2.multiplyLeftI(
                      Array.involution(m21.Ei,N)), ds, 3, ring);  //  4-STEP //
       Det=m22.Det;
       MatrixS y21 = m21.S.ES_min_dI(d21, m21.Ei, m21.Ej, ring);
       MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring), ring);
       MatrixS M12_2 =((((M12_1.multiplyLeftI(m11.Ei).multiplyByNumber(d21, ring))
          .subtract(m11.S.multiplyDivRecursive((m21.A
          .multiplyLeftE(m21.Ej,m21.Ei)),d11, ring).multiplyRecursive(M22_1, ring), ring)
                         ).divideByNumber(  d11.negate(ring), ring).multiplyRecursive(y12, ring)
                        ).add((m12.S).multiplyByNumber(d21, ring), ring)
                       ).divideByNumber(d11, ring);
       MatrixS y22 = m22.S.ES_min_dI(Det, m22.Ei, m22.Ej, ring);
       MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring), ring);
       MatrixS M22_3 = ((M22_2.multiplyLeftI(m21.Ei))
                          .multiplyDivRecursive(y22, ds.negate(ring)  , ring)).add(m22.S, ring);
       MatrixS A1=m12.A.multiplyRecursive(m11.A, ring);
       MatrixS A2=m22.A.multiplyRecursive(m21.A, ring);
       MatrixS Q = (A1.subtract((M12_1.multiplyLeftI(m11.Ei)).
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
       RR[0] =(Q.add( F.multiplyRecursive(G, ring) , ring) ).divideByNumber(d12, ring); RR[1] =  F;
       RR[2] = P.multiplyDivRecursive(G, d12, ring); RR[3] =  P;
       MatrixS M11_3 = M11_2.multiplyDivide(Det, d21, ring);
       MatrixS M21_3 = m21.S.multiplyDivide(Det, d21, ring);



       int len1=m11.Ei.length+m12.Ei.length;      int len=len1+ m21.Ei.length+m22.Ei.length;
       Ei=new int [len];  Ej=new int [len];  D=new Element[len]; Element start=ring.numberONE;
       joinSortedArraysWithoutDoubling(m11.Ei,m12.Ei, Ei, m11.Ej,m12.Ej,  Ej, m11.D, m12.D, D, 0,    0, N,  start, ring);

      int clm_1=0;       int clm_2=0;
       if((m11.Ej.length)!=0){clm_1=(( m12.Ei.length==0  )|| (m11.Ei[m11.Ei.length-1]>m12.Ei[m12.Ei.length-1]))?
                                                              m11.Ej[m11.Ej.length-1] :  m12.Ej[m12.Ej.length-1];}
      else {if((m12.Ej.length)!=0) clm_1=Integer.MAX_VALUE;}
       if((m21.Ej.length)!=0){clm_2=(( m22.Ei.length==0  )|| (m21.Ei[m21.Ei.length-1]>m22.Ei[m22.Ei.length-1]))?
                                                              m21.Ej[m21.Ej.length-1] :  m22.Ej[m22.Ej.length-1];}
       if ((clm_1>clm_2)&&(len1>0)) start=D[len1-1];

       System.out.println(clm_1+"   "+clm_2+"  "+start+"= start, ffff1="+Array.toString(D));

       joinSortedArraysWithoutDoubling(m22.Ei,m21.Ei, Ei, m22.Ej, m21.Ej, Ej, m22.D, m21.D, D, len1, N, N, start, ring);

       if(len<m.size){int N2=m.size; int[]EEi=new int[N2]; int[]EEj=new int[N2];
           for(int k=0;k<len;k++){EEj[Ej[k]]=-1; EEi[Ei[k]]=-1;}
           int pos=0; for(;pos<N2;pos++)if(EEj[pos]!=-1)break;System.out.println(N2+"=N2 pss000osss="+pos);
           for(int k=0; k<N2; k++){System.out.println("pssosss="+pos+"  k="+k);  if(EEi [k] !=-1){EEi[ k ]=k; EEj[ k ]=pos++;  for(;pos<N2;pos++){if(EEj[pos]!=-1)break;}System.out.println("posss="+pos);  }  }
           for(int k=0; k<len; k++){ EEi[Ei[k]]=Ei[k]; EEj[Ei[k]]=Ej[k]; } // Готова полная перестановка

           int n0=0;for(int k=0;k<N;k++){if(EEj[k]<N) n0++;} int n2=0;for(int k=N;k<N2;k++){if(EEj[k]<N) n2++;}
           m11.Ei=new int[n0]; m11.Ej=new int[n0]; m12.Ei=new int[N-n0]; m12.Ej=new int[N-n0];
           int p0=0, p1=0; for(int k=0;k<N;k++){if(EEj[k]<N) {m11.Ei[p0]=k; m11.Ej[p0++]=EEj[k];}
                                                else {m12.Ei[p1]=k; m12.Ej[p1++]=EEj[k]-N;}}
           m21.Ei=new int[n2]; m21.Ej=new int[n2]; m22.Ei=new int[N-n2]; m22.Ej=new int[N-n2];
           p0=0; p1=0; for(int k=N;k<N2;k++){if(EEj[k]<N) {m21.Ei[p0]=k-N; m21.Ej[p0++]=EEj[k];}else {m22.Ei[p1]=k-N; m22.Ej[p1++]=EEj[k]-N;}}
      System.out.println("arrayE------------ ="+Array.toString( EEj));
       }

       System.out.println("arrayE e e e ="+Array.toString( m11.Ej)+Array.toString( m12.Ej)+Array.toString( m21.Ej)+Array.toString( m22.Ej) );
       int[][] T=MatrixS.joinEij( new int[][]{m11.Ei,m12.Ei,m21.Ei,m22.Ei},new int[][]{m11.Ej,m12.Ej, m21.Ej,m22.Ej}, N);
       Eif=T[0];Ejf=T[1];
           System.out.println(start+"= Eif="+Array.toString(Eif));    System.out.println(start+"= Ejf="+Array.toString(Ejf));
       System.out.println("Eijf="+ MatrixS.Eij2MatrixS_Z(T[0],T[1], 2*N));
       System.out.println(start+"= start, ffff2="+Array.toString(D));

       S = MatrixS.join(new MatrixS[]{M11_3, M12_3, M21_3, M22_3});
       A = MatrixS.join(RR);

       MatrixS A11new=m11.A.multiplyLeftI(m11.Ei).add(m12.A.multiplyLeftI(m12.Ei), ring);
       MatrixS U11=m11.U.add(m12.U, ring);     MatrixS U22=m21.U.multiplyByNumber(start, ring).add(m22.U, ring);
       MatrixS U11_A11new=U11.multiplyRecursive(A11new, ring);
       MatrixS Ua=U11_A11new.multiplyDivRecursive(M[0].multiplyRightE(m21.Ej, m21.Ei), d0.multiply(m11.Det, ring), ring);
       MatrixS Ub=U11_A11new.multiplyDivRecursive(M[1].multiplyRightE(m22.Ej, m22.Ei), d0.multiply(m12.Det, ring), ring);
       MatrixS U12=Ua.add(Ub, ring);
 System.out.println( "Ua,Ub,A11new= "+Ua+"   "+M[0].multiplyRightE(m21.Ej, m21.Ei)+ A11new);
       MatrixS L11=m11.L.add(m12.L, ring);    MatrixS L22=m21.L.multiplyByNumber(start, ring).add(m22.L, ring);
       MatrixS A11newL11=A11new.multiplyRecursive(L11, ring);
       MatrixS La=M[2].multiplyRightE(m11.Ej, m11.Ei).multiplyDivRecursive(A11newL11, d0.multiply(m11.Det, ring), ring);
       MatrixS Lb=M[3].multiplyRightE(m12.Ej, m12.Ei).multiplyDivRecursive(A11newL11, d0.multiply(m12.Det, ring), ring);
       MatrixS L21=La.add(Lb, ring);
       System.out.println("La,Lb="+La+Lb+A11newL11+A11new);
       L=MatrixS.join(new MatrixS[] {L11, MatrixS.zeroMatrix(),  L21,  L22 });//  m12.L.multiplyLeft_barI(m11.Ei),
       U=MatrixS.join(new MatrixS[] {U11, U12, MatrixS.zeroMatrix(), U22});
        System.out.println( "U21========="+m21.U); System.out.println( "U22========="+m22.U);
          System.out.println( "U11========="+m11.U); System.out.println( "U12========="+m12.U);
  // WAS     m21.L.multiplyRight_barI(m11.Ej).add(L21_2, ring), m22.L.add(L22_1, ring)});

  }

 /**
  * The array of fractions that is a diagonal in Gauss decomposition
  * @param diag -- diagonal elements of a matrix
  * @param ring -- Ring
  * @return  -- a diagonal in Gauss decomposition
  */
 private static Element[] GaussDiag(Element[] diag, int[] EjBar, int N, Ring ring){
       int k=diag.length;
       Element[] DD=new Element[N];
       for(int i=1;i<k;i++)DD[i]=new Fraction(ring.numberONE, diag[i-1].multiply(diag[i], ring));
       if(k>0) DD[0]=new Fraction(ring.numberONE,diag[0]);
       if (k!=N){ Fraction one=new Fraction(ring.numberONE, ring);
           int j=EjBar.length-1; // номера последних елементов в EjBar, которые должны в DD отмечаться единицами
           int i=k-1; // указатель на элементы в DD, которые переносим
           int c=N-1;  // указатели на позиции для записи в DD
           while(j>=0){ if(EjBar[j]>i){DD[c--]=one; j--; System.out.println("ONE=i,j,c="+i+j+c);}
               else{DD[c--]=DD[i--];System.out.println("MOVE=i,j,c="+i+j+c);}                 }
       }return DD;
    }

 /**
  * The array of fractions that is a diagonal in Gauss decomposition
  * @param diag -- diagonal elements of a matrix
  * @param ring -- Ring
  * @return  -- a diagonal in Gauss decomposition
  */
 private static Element[] GaussDiag(Element[] diag,    Ring ring){
       int k=diag.length;
       Element[] DD=new Element[k];
       for(int i=1;i<k;i++){DD[i]=new Fraction(ring.numberONE, diag[i-1].multiply(diag[i], ring));}
       if(k>0) DD[0]=new Fraction(ring.numberONE,diag[0]);
       return DD;
    }


 /**
  * Сливаем два целочисленных массива  a и b с несовпадающими элементами в массив с с одновременной сортировкой
  * Одновременно сливаются сопутствующие массивы aJ, bJ  и aD, bD.
  *
  * @param a
  * @param b
  * @param c  -- result
  * @param aJ
  * @param bJ
  * @param cJ  -- result
  * @param aD
  * @param bD
  * @param cD  -- result
  * @param offset -- offset in destination array
  * @param half  -- constant for second part  half
  * @param halfColm =N
  * @param start = Element on Det or One
  * @param ring
  */
 public static void joinSortedArraysWithoutDoubling(int[] a, int b[], int[] c, int[] aJ, int bJ[], int cJ[],
       Element[] aD, Element bD[], Element cD[], int offset, int half, int halfColm, Element start, Ring ring){
     System.out.println("-a------"+Array.toString(a)+Array.toString(aJ));
     System.out.println("-b------"+Array.toString(b)+Array.toString(bJ));
     int[][] res=new int[2][];
     System.out.println("here----------------------------");
    int w1=a.length; int w2=b.length; int halfColmA=0; if (half!=0){halfColmA=halfColm;halfColm=0;}
    int ab =0; Element lasta=start; Element lastb=lasta;
    int l=w1+w2; // c.length
    int u1=0; int u2=0; // current pointers in the first and second arrays
    int k=offset;            // current pointer in c vector
       while ((u2<w2)&&(u1<w1)){    // Start join
          if (a[u1]<b[u2]){ cD[k]=aD[u1].multiply(lastb,ring); lasta=aD[u2]; c[k]=a[u1]+half; cJ[k++]=aJ[u1++]+halfColmA; ab=1;}
          else            { cD[k]=bD[u2].multiply(lasta,ring); lastb=bD[u2]; c[k]=b[u2]+half; cJ[k++]=bJ[u2++]+halfColm; ab=-1;}
       }
        if (u2<w2)for(; u2<w2; u2++) {cD[k]=bD[u2].multiply(lasta,ring); lastb=bD[u2]; c[k]=b[u2]+half; cJ[k++]=bJ[u2]+halfColm;ab=-1; }
        if (u1<w1)for(; u1<w1; u1++) {cD[k]=aD[u1].multiply(lastb,ring); lasta=aD[u1]; c[k]=a[u1]+half; cJ[k++]=aJ[u1]+halfColmA;ab=1; }
     System.out.println("-------"+Array.toString(c)+Array.toString(cJ));
 }



 public static void main(String[] args) {

     long[][] m5 = new long[][]
                         {{10, 0, 10, 1},
                          {1, 7, 10, 5},
                          {1, 2, 3,  11},
                          {2, 9, 5,  17} };
//                        {{ 2,  0, 1, 1},
//                          {1,  0, 7, 5},
//                          {1, 3, 2,  11},
//                          {2, 5, 9,  17} };
//                        {{ 2,  0, 0,  0},
//                          {0,  0, 7,  0},
//                          {0,  1, 0,  0},
//                          {0,  0, 0,  1} };
long[][]m4 = new long[][]{{5,  0, 0,  0},
                          {0,  0, 0,  0},
                          {0,  3, 0,  8},
                          {10,  0, 1,  4} };
        Ring ring=Ring.ringZxyz;

      MatrixS A4=new MatrixS (m4,ring);
      MatrixS A5=new MatrixS (m5,ring);
   //   MatrixS A1=MatrixS.join(new MatrixS[]{A5,A4,A4,A5});
         // вектор b - столбец свободных членов

        //MatrixS A1=new MatrixS (m4,ring);
        long[][] m2 =  {{0, 0 } ,
                        {10, 6 } };
 //     MatrixS A2=new MatrixS(m2,ring);
    MatrixS A1=new MatrixS (m5,ring);// A=A.multiply(Matrix, ring)
             System.out.println( " A        ======  "+A1);
             MatrixS AF=A1;
     adjLDU x = new adjLDU(A1, ring.numberONE, 0, ring);  // y as is
             MatrixS EE= MatrixS.Eij2MatrixS_Z(x.Ei,x.Ej,AF.size);
//             System.out.println( " A*A,   S  initial ************     ======  "+y.A.multiply(A, ring)+ y.S);
              System.out.println( "eeee=="+EE);
//    MatrixD t=new MatrixD(A); int[][] E_=MatrixS.AddBarE(y.Ei,y.Ej, 4)
//    MatrixS A2=A.multiplyRightE(x.Ej, x.Ei);
//     MatrixS EE= MatrixS.Eij2MatrixS_Z(x.Ei,x.Ej,A1.size);
//       System.out.println("E_____="+EE);
//       System.out.println("E_1____="+EE.multiplyRightE(x.Ei, x.Ej));
//       System.out.println( " A  new  here     ======  "+A2);
//     adjLDU y=new adjLDU(A1.multiplyRightE(x.Ej, x.Ei),  ring.numberONE, 0, ring);    // y - normal
 //              MatrixS EE11= MatrixS.Eij2MatrixS_Z(y.Ei,y.Ej,4);
             //  System.out.println("E new="+EE11);
             //  System.out.println( "  A*A, S  second =  "+ y.A.multiply(A2, ring)+y.S);
    ////          // System.out.println( "  A*A, S  second =  "+ x.A.multiply(A, ring)+x.S);
   //           System.out.println( "  D new  =  "+ Array.toString(y.D));
               //   System.out.println("a0aaa="+Array.toString(E_[2]));  //    System.out.println("aa1aa="+Array.toString(E_[1]));
     Element[]         DD= GaussDiag(x.D, ring);
                    //      System.out.println( "D=  "+Array.toString(x.D));
                  //    System.out.println( " Gauss new x DD=  "+Array.toString(DD));
    //  System.out.println("adjLDU *E  normal=="+ y.L.multiply(y.U.multiplyLeftDE(x.Ei, x.Ej, DD, false, ring),ring).cancel(ring)  );
          //           MatrixS UU1=x.U.multiplyRightE(x.Ei, x.Ej).multiplyLeftE(x.Ej, x.Ei);
              //     System.out.println("new  y.U,   EUE=" +x.U+UU1);
              //       System.out.println("adjLDU norm1 y =="+ y.L.multiply(y.U.multiplyLeftDE(y.Ej, y.Ei, DD,false, ring),ring)
              //           .multiplyRightE(x.Ei, x.Ej).cancel(ring)  );
//     MatrixS adjLDU=y.L.multiply(y.U.multiplyLeftDE(y.Ej, y.Ei, DD, false, ring),ring)
//          .multiplyRightE(x.Ei, x.Ej).cancel(ring);
//           MatrixS   EE_T= MatrixS.Eij2MatrixS_Z(x.Ej,x.Ei,4);
// asymptote.sourseforge.net   (ce.fo...)
System.out.println("L =="+x.L);
System.out.println("D =="+Array.toString(x.D));
System.out.println("DD =="+MatrixS.scalarMatrix(DD.length, NumberZ.ONE, ring).multiplyRightD(DD, ring));
 System.out.println("E =="+MatrixS.scalarMatrix(AF.size, NumberZ.ONE, ring).multiplyRightE(x.Ei, x.Ej));
 MatrixS UU=     x.U.multiplyLeftE(x.Ejf, x.Eif).multiplyRightE(x.Eif, x.Ejf) ;
System.out.println(" xU ==" +x.U+Array.toString(x.U.M[0])+Array.toString(x.U.col[0]));
System.out.println(" UU ==" +UU+Array.toString(UU.M[1])+Array.toString(UU.col[1]));
 MatrixS ldeu1=x.L.multiplyRightDE(x.Ei,x.Ej, DD,ring).cancel(ring);
 System.out.println(" LDEU1  =" +ldeu1);
MatrixS ldeu=x.L.multiplyRightDE(x.Ei,x.Ej, DD,ring).multiply(UU,ring).cancel(ring);
System.out.println(" LDEU  =" +ldeu); // + Array.toString(ldeu.M[0])+Array.toString(ldeu.col[0]));
System.out.println(" A  =" +AF);
 //            System.out.println("E_____="+EE);
//               System.out.println("E_1____="+EE.multiplyRightE(x.Ei, x.Ej));
System.out.println( AF.subtract(ldeu, ring));
 if (AF.subtract(ldeu, ring).isZero(ring)) System.out.println("GOOD !!!!");
    }

}// end of class adjLDU
