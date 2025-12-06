
package com.mathpar.matrix.file.sparse;



import java.io.*;
import com.mathpar.matrix.*;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;


/**
 *
 * @author gennadi
 */
public class AdjSFMS {
    /** adjoint matrix */
    public SFileMatrixS A;

    /** список строк матрицы Е */
    public int[] Ei;

    /** список столбцов матрицы Е, которые соответствуют
     * ее списку строк:  (E[Ei[i]][Ej[i]] = 1) */
    public int[] Ej;

    /** echelon form for the initial matrix  */
    public SFileMatrixS S;

    /** determinant of the initial matrix */
    public Element Det;

    public AdjSFMS(SFileMatrixS A, int[] Ei, int[] Ej, SFileMatrixS S, Element Det){
        this.A=A;
        this.Ei=Ei;
        this.Ej=Ej;
        this.S=S;
        this.Det=Det;

    }
    /** Конструктор класса AdjSFMS
     *
     * @param m --  входная матрица
     * @param d0 -- determinant of the last upper block. For first step: d0=1.
     *    A -- adjoin matrix for matrix m: Am=S -- echelon form of m.
     *    S -- echelon form for matrix m
     *    Det -- determinant
     *    Ei,Ej -- obtained E-matrix
     */
    public AdjSFMS(SFileMatrixS m, Element d0, int N, int depth,Ring ring) throws IOException {
        if (m.isZERO()) {
            //m==0
            A = SFileMatrixS.ONEmultD(depth, d0, N, ring.numberONE(),ring);
            Ei = new int[0];
            Ej = new int[0];
            S = SFileMatrixS.ZERO;
            Det = d0;
        } else {
            //m!=0
            if (depth==0) {
                //дошли до листа
                AdjMatrixS adjms=new AdjMatrixS(m.toMatrixS(), d0,ring);
                //adjms.A-->A
                //adjms.S-->S
                //adjms.Ei-->Ei
                //adjms.Ej-->Ej
                //adjms.Det-->Det
                A=new SFileMatrixS(depth,adjms.A,d0,ring);
                S=new SFileMatrixS(depth,adjms.S,d0,ring);
                Ei = adjms.Ei;
                Ej = adjms.Ej;
                Det = adjms.Det;
            } else {
                N = N >>> 1;
                depth--;
                SFileMatrixS[] M = m.splitNoNulls();

                //0----------------------------------------------------
                AdjSFMS m11 = new AdjSFMS(M[0], d0, N, depth,ring); // 1 STEP //
                //----------------------------------------------------

                Element d11 = m11.Det;
                Element d11_2 = d11.multiply(d11,ring);
                SFileMatrixS y11 = m11.S.ES_min_dI(d11, m11.Ei, m11.Ej,depth,N,ring);


                //1-----------------------------------------------------
                SFileMatrixS M12_1 = m11.A.multiplyDivRecursive(M[1], d0,ring);
                SFileMatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring),ring);
                //-----------------------------------------------------
                //case2
                SFileMatrixS x_1=(M[3].multiplyByNumber(d11,ring));
                SFileMatrixS x_2=M12_1.multiplyLeftE(m11.Ej, m11.Ei,ring);
                SFileMatrixS x_3=M12_1.multiplyLeftI(Array.involution(m11.Ei, N),ring);


                //2--------------------------------------------------------
                AdjSFMS m21 = new AdjSFMS(M21_1, d11, N, depth,ring); // 2 STEP  //
                AdjSFMS m12 = new AdjSFMS(x_3, d11, N, depth,ring); //  3 STEP  //
                //--------------------------------------------------------

                //case3
                Element d21 = m21.Det;
                SFileMatrixS y21 = m21.S.ES_min_dI(d21, m21.Ei, m21.Ej,depth,N,ring);

                //3--------------------------------------------------------
                SFileMatrixS y_1=M[2].multiplyRecursive(x_2,ring);
                SFileMatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring),ring);
                //--------------------------------------------------------
                //case4
                SFileMatrixS M22_1 = ( x_1.subtract(y_1,ring)).divideByNumber(d0,ring);
                Element d12 = m12.Det;
                SFileMatrixS y12 = m12.S.ES_min_dI(d12, m12.Ei, m12.Ej,depth,N,ring);
                Element ds = d12.multiply(d21,ring).divide(d11,ring);
                SFileMatrixS x_5=M12_1.multiplyLeftI(m11.Ei,ring).multiplyByNumber(d21,ring);
                SFileMatrixS x_6=(m21.A.multiplyLeftE(m21.Ej, m21.Ei,ring));

                //4-------------------------------------------------------
                SFileMatrixS y_2=m21.A.multiplyRecursive(M22_1,ring);
                SFileMatrixS y_3=m11.S.multiplyDivRecursive( x_6, d11,ring);
                //-------------------------------------------------------

                //case5
                //5-------------------------------------------------------
                SFileMatrixS M22_2 = (y_2).multiplyDivRecursive(y12, d11_2.negate(ring),ring);
                SFileMatrixS y_4=y_3.multiplyRecursive(M22_1,ring);
                //------------------------------------------------------
                //case6
                SFileMatrixS x_4=M22_2.multiplyLeftI(Array.involution(m21.Ei, N),ring);
                SFileMatrixS x_7=( (x_5).subtract(y_4,ring)).divideByNumber(d11.negate(ring),ring);

                //6------------------------------------------------------
                AdjSFMS m22 = new AdjSFMS(x_4, ds,  N, depth,ring); //  4-STEP //
                SFileMatrixS y_5=x_7.multiplyRecursive(y12,ring);
                //------------------------------------------------------
                //case7
                Det = m22.Det;
                SFileMatrixS M12_2 = ((y_5).add( (m12.S).multiplyByNumber(d21,ring),ring)).divideByNumber(d11,ring);
                SFileMatrixS y22 = m22.S.ES_min_dI(Det, m22.Ei, m22.Ej,depth,N,ring);
                SFileMatrixS x_8=(M22_2.multiplyLeftI(m21.Ei,ring));


                //7-----------------------------------------------------
                SFileMatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring),ring);
                SFileMatrixS y_6=( x_8.multiplyDivRecursive(y22, ds.negate(ring),ring));
                //------------------------------------------------------
                //case8
                SFileMatrixS M22_3 = y_6. add(m22.S,ring);

                //8------------------------------------------------------
                SFileMatrixS A1 = m12.A.multiplyRecursive(m11.A,ring);
                SFileMatrixS A2 = m22.A.multiplyRecursive(m21.A,ring);
                //------------------------------------------------------

                //case9
                SFileMatrixS x_9=(M12_1.multiplyLeftI(m11.Ei,ring));
                SFileMatrixS x_10=A1.multiplyLeftE(m12.Ej, m12.Ei,ring);
                SFileMatrixS x_11=A2.multiplyLeftE(m22.Ej, m22.Ei,ring);
                SFileMatrixS x_12=(M22_2.multiplyLeftI(m21.Ei,ring));

                //9------------------------------------------------------
                SFileMatrixS y_7=x_9.multiplyDivRecursive(x_10, d11,ring);
                SFileMatrixS y_8=x_12.multiplyDivRecursive(x_11, ds,ring);
                //-----------------------------------------------------
                //case10
                SFileMatrixS x_13=A1.subtract( y_7,ring);
                SFileMatrixS L = (x_13).divideMultiply(d11, Det,ring);
                SFileMatrixS x_14=m21.A.multiplyLeftE(m21.Ej, m21.Ei,ring);
                SFileMatrixS x_15=A2.multiplyLeftE(m22.Ej, m22.Ei,ring);
                SFileMatrixS P = (A2.subtract( y_8,ring)).divideByNumber(d21,ring);


                //10------------------------------------------------------------
                SFileMatrixS y_9=m11.S.multiplyDivMulRecursive(x_14, d11, Det,ring);
                SFileMatrixS y_10=M12_2.multiplyDivRecursive(x_15, ds,ring);
                //------------------------------------------------------------

                //case11
                SFileMatrixS F = (y_9.add(y_10,ring)).divideByNumber(d21.negate(ring),ring);
                SFileMatrixS x_16=m11.A.multiplyLeftE(m11.Ej, m11.Ei,ring);
                SFileMatrixS x_17=A1.multiplyLeftE(m12.Ej, m12.Ei,ring);

                //11-------------------------------------------------------------
                SFileMatrixS y_11=M[2].multiplyDivMulRecursive(x_16, d0, d12,ring);
                SFileMatrixS y_12=M22_1.multiplyDivRecursive(x_17, d11,ring);
                //-------------------------------------------------------------

                //case12
                SFileMatrixS G = (y_11.add(y_12,ring)).divideByNumber(d11.negate(ring),ring);
                SFileMatrixS[] RR = new SFileMatrixS[4];


                //12-------------------------------------------------------------
                SFileMatrixS y_13=F.multiplyRecursive(G,ring);
                RR[2] = P.multiplyDivRecursive(G, d12,ring);
                //-------------------------------------------------------------

                RR[0] = (L.add(y_13,ring)).divideByNumber(d12,ring);
                RR[1] = F;
                RR[3] = P;
                SFileMatrixS M11_3 = M11_2.multiplyDivide(Det, d21,ring);
                SFileMatrixS M21_3 = m21.S.multiplyDivide(Det, d21,ring);
                Ei = new int[m11.Ei.length + m12.Ei.length + m21.Ei.length +
                    m22.Ei.length];
                Ej = new int[Ei.length];
                int j = 0;
                for (int i = 0; i < m11.Ei.length; ) {
                    Ei[j] = m11.Ei[i];
                    Ej[j++] = m11.Ej[i++];
                }
                for (int i = 0; i < m12.Ei.length; ) {
                    Ei[j] = m12.Ei[i];
                    Ej[j++] = m12.Ej[i++] + N;
                }
                for (int i = 0; i < m21.Ei.length; ) {
                    Ei[j] = m21.Ei[i] + N;
                    Ej[j++] = m21.Ej[i++];
                }
                for (int i = 0; i < m22.Ei.length; ) {
                    Ei[j] = m22.Ei[i] + N;
                    Ej[j++] = m22.Ej[i++] + N;
                }
                A = new SFileMatrixS(d0,ring).joinCopy(RR);
                S = new SFileMatrixS(d0,ring).joinCopy(new SFileMatrixS[] {M11_3, M12_3, M21_3, M22_3});
                //сохранить M, иначе после удаления M будут удалены поддеревья m
                SFileMatrix.keep(M);



                /*checkAdjMatrixS(m.toMatrixS(),d0,
                                new String[]{"m11","d11","d11_2","y11","M12_1","M21_1","M22_1",
                                "m21","d21","m12","d12","y12","M22_2","ds","m22","Det","y21","M11_2","M12_2","y22",
                                "M12_3","M22_3","A1","A2","L","P","F","G","RR[0]","RR[1]","RR[2]","RR[3]",
                                "Ei","Ej","A","S"},
                                new Object[]{m11,d11,d11_2,y11,M12_1,M21_1,M22_1,
                                m21,d21,m12,d12,y12,M22_2,ds,m22,Det,y21,M11_2,M12_2,y22,
                                M12_3,M22_3,A1,A2,L,P,F,G,RR[0],RR[1],RR[2],RR[3],
                                Ei,Ej,A,S});*/
            }
        }
    }



    public boolean equalsToAdjMS(AdjMatrixS adjms,Ring ring) throws IOException{
        //A-->AMS
        //S-->SMS
        MatrixS AMS=A.toMatrixS();
        MatrixS SMS=S.toMatrixS();
        return msEquals(AMS,adjms.A,ring) && msEquals(SMS,adjms.S,ring) &&
            Det.equals(adjms.Det) &&
            arrEquals(Ei,adjms.Ei) && arrEquals(Ej,adjms.Ej);
    }


    //m1==null или m1!=null
    public static boolean msEquals(MatrixS m1, MatrixS m2,Ring ring){
        if (m1==null) {
            if (m2.isZero(ring)) {
                return true;
            } else {
                return false;
            }
        } else {
            //m1!=null
            return m1.isEqual(m2,ring);
        }
    }


    public static boolean arrEquals(int[] arr1, int[] arr2){
        if (arr1.length!=arr2.length){
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i]!=arr2[i]){
                return false;
            }
        }
        return true;
    }




    public static void checkAdjMatrixS(MatrixS m, Element d0,
                                       String[] fnames, Object[] fvals,Ring ring) {
        MatrixS A,S;
        int[] Ei,Ej;
        Element Det;
        int N = m.size; // The number of rows in the matrix m
        if (m.isZero(ring)) { //Array.p(" ZERO_Matr  ");
            A = MatrixS.scalarMatrix(m.size, d0,ring);
            Ei = new int[0];
            Ej = new int[0];
            S = MatrixS.zeroMatrix(N);
            Det = d0;
        } else {
            if (N == 1) {
                A = new MatrixS(1, 1, new Element[][] { {d0}
                }, new int[][] { {0}
                });
                Ei = new int[] {
                    0};
                Ej = new int[] {
                    0};
                S = m;
                Det = m.M[0][0];
            } else {
                N = N >>> 1;
                MatrixS[] M = m.split();
                AdjMatrixS m11 = new AdjMatrixS(M[0], d0,ring); // 1 STEP //
                Element d11 = m11.Det;
                Element d11_2 = d11.multiply(d11,ring);
                MatrixS y11 = m11.S.ES_min_dI(d11, m11.Ei, m11.Ej,ring);
                MatrixS M12_1 = m11.A.multiplyDivRecursive(M[1], d0,ring);
                MatrixS M21_1 = M[2].multiplyDivRecursive(y11, d0.negate(ring),ring);
                MatrixS M22_1 = ( (M[3].multiplyByNumber(d11,ring)).subtract(M[2]
                    .multiplyRecursive(M12_1.multiplyLeftE(m11.Ej, m11.Ei),ring),ring))
                    .divideByNumber(d0,ring);
                AdjMatrixS m21 = new AdjMatrixS(M21_1, d11,ring); // 2 STEP  //
                Element d21 = m21.Det;
                AdjMatrixS m12 = new AdjMatrixS(M12_1.multiplyLeftI(
                    Array.involution(m11.Ei, N)), d11,ring); //  3 STEP  //
                Element d12 = m12.Det;
                MatrixS y12 = m12.S.ES_min_dI(d12, m12.Ei, m12.Ej,ring);

                MatrixS M22_2 = (m21.A.multiplyRecursive(M22_1,ring))
                    .multiplyDivRecursive(y12, d11_2.negate(ring),ring);
                Element ds = d12.multiply(d21,ring).divide(d11,ring);

                AdjMatrixS m22 = new AdjMatrixS(M22_2.multiplyLeftI(
                    Array.involution(m21.Ei, N)), ds,ring); //  4-STEP //
                Det = m22.Det;
                MatrixS y21 = m21.S.ES_min_dI(d21, m21.Ei, m21.Ej,ring);
                MatrixS M11_2 = m11.S.multiplyDivRecursive(y21, d11.negate(ring),ring);

                MatrixS M12_2 = ( ( ( (M12_1.multiplyLeftI(m11.Ei).
                                       multiplyByNumber(d21,ring))
                                     .subtract(m11.S.multiplyDivRecursive( (m21.
                    A
                    .multiplyLeftE(m21.Ej, m21.Ei)),
                                               d11,ring).multiplyRecursive(M22_1,ring),ring)
                                   ).divideByNumber(d11.negate(ring),ring).
                                   multiplyRecursive(y12,ring)
                                 ).add( (m12.S).multiplyByNumber(d21,ring),ring)
                    ).divideByNumber(d11,ring);
                MatrixS y22 = m22.S.ES_min_dI(Det, m22.Ei, m22.Ej,ring);
                MatrixS M12_3 = M12_2.multiplyDivRecursive(y22, ds.negate(ring),ring);
                MatrixS M22_3 = ( (M22_2.multiplyLeftI(m21.Ei))
                                 .multiplyDivRecursive(y22, ds.negate(ring),ring)).add(
                    m22.S,ring);
                MatrixS A1 = m12.A.multiplyRecursive(m11.A,ring);
                MatrixS A2 = m22.A.multiplyRecursive(m21.A,ring);
                MatrixS L = (A1.subtract( (M12_1.multiplyLeftI(m11.Ei)).
                                         multiplyDivRecursive(A1.multiplyLeftE(
                    m12.Ej, m12.Ei), d11,ring),ring)
                    ).divideMultiply(d11, Det,ring);
                MatrixS P = (A2.subtract( (M22_2.multiplyLeftI(m21.Ei)).
                                         multiplyDivRecursive(A2.multiplyLeftE(
                    m22.Ej, m22.Ei), ds,ring),ring)
                    ).divideByNumber(d21,ring);
                MatrixS F = (m11.S.multiplyDivMulRecursive(m21.A
                    .multiplyLeftE(m21.Ej, m21.Ei), d11, Det,ring)
                             .add(M12_2.multiplyDivRecursive(A2
                    .multiplyLeftE(m22.Ej, m22.Ei), ds,ring),ring)
                    ).divideByNumber(d21.negate(ring),ring);

                MatrixS G = (M[2].multiplyDivMulRecursive(m11.A
                    .multiplyLeftE(m11.Ej, m11.Ei), d0, d12,ring)
                             .add(M22_1.multiplyDivRecursive(A1
                    .multiplyLeftE(m12.Ej, m12.Ei), d11,ring),ring)).divideByNumber(d11.
                    negate(ring),ring);
                MatrixS[] RR = new MatrixS[4];
                RR[0] = (L.add(F.multiplyRecursive(G,ring),ring)).divideByNumber(d12,ring);
                RR[1] = F;
                RR[2] = P.multiplyDivRecursive(G, d12,ring);
                RR[3] = P;
                MatrixS M11_3 = M11_2.multiplyDivide(Det, d21,ring);
                MatrixS M21_3 = m21.S.multiplyDivide(Det, d21,ring);
                Ei = new int[m11.Ei.length + m12.Ei.length + m21.Ei.length +
                    m22.Ei.length];
                Ej = new int[Ei.length];
                int j = 0;
                for (int i = 0; i < m11.Ei.length; ) {
                    Ei[j] = m11.Ei[i];
                    Ej[j++] = m11.Ej[i++];
                }
                for (int i = 0; i < m12.Ei.length; ) {
                    Ei[j] = m12.Ei[i];
                    Ej[j++] = m12.Ej[i++] + N;
                }
                for (int i = 0; i < m21.Ei.length; ) {
                    Ei[j] = m21.Ei[i] + N;
                    Ej[j++] = m21.Ej[i++];
                }
                for (int i = 0; i < m22.Ei.length; ) {
                    Ei[j] = m22.Ei[i] + N;
                    Ej[j++] = m22.Ej[i++] + N;
                }
                A = MatrixS.join(RR);
                S = MatrixS.join(new MatrixS[] {M11_3, M12_3, M21_3, M22_3});
                compareRes(N,fnames,
                           new Object[]{m11,d11,d11_2,y11,M12_1,M21_1,M22_1,
                           m21,d21,m12,d12,y12,M22_2,ds,m22,Det,y21,M11_2,M12_2,y22,
                           M12_3,M22_3,A1,A2,L,P,F,G,RR[0],RR[1],RR[2],RR[3],
                           Ei,Ej,A,S},
                           fvals,ring);

            }
        }
    }




    private static void compareRes(int N,String[] fnames, Object[] mvals, Object[] fvals,Ring ring){
        for (int i = 0; i < mvals.length; i++) {
            compareResi(N,fnames[i],mvals[i],fvals[i],ring);
        }
    }


    private static void compareResi(int N,String name, Object mem, Object inFile,Ring ring){
        try {
            boolean res;
            if (mem instanceof MatrixS) {
                MatrixS msf = ( (SFileMatrixS) inFile).toMatrixS();
                res = msEquals(msf, (MatrixS) mem,ring);
                if (!res) {
                    System.out.println("Mem: "+mem);
                    System.out.println("File: "+msf);
                }
            } else if (mem instanceof Element) {
                res = ( (Element) mem).equals((Element)inFile);
            } else if (mem instanceof int[]) {
                res =  arrEquals((int[]) mem, (int[])inFile);
            } else if (mem instanceof AdjMatrixS) {
                res = ( (AdjSFMS) inFile).equalsToAdjMS( (AdjMatrixS) mem,ring);
            } else {
                throw new RuntimeException("Unknown type: " + mem.getClass());
            }
            if (!res) {
                System.out.println(N+": "+name + " are not equals");
                //throw new RuntimeException(N+": "+name + " are not equals");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


} // end of class AdjSFMS
