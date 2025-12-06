/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.kireev;
import java.util.ArrayList;
import com.mathpar.matrix.*;
import com.mathpar.number.*;

/**
 *Задачи тропической математики
 *
 * @author kireev
 */
public class TropicalProblems {
    /**
     * Поиск наименьших расстояний
     * @param mr матрица расстояний (т.е. x_ij - расстояние между смежными вершинами, x_ii=0,
     * а если нет ребра, соединяющего вершины i и j, то x_ij=\infty)
     * @param r внешнее кольцо
     * @return матрицу кратчайших расстояний(т.е. наименьшее расстояние от i до j
     * - на пересечении соответствующих строки i(или j) и столбца j(или i))
     */
    public static MatrixD searchLeastDistances(MatrixD mr, Ring r) {
        Ring ring = new Ring("R64MinPlus[x]");
        for (int k1 = 0; k1 < mr.M.length; k1++) {
            for (int l = 0; l < mr.M.length; l++) {
                mr.M[k1][l]=mr.M[k1][l].toNumber(r.R64MinPlus, r);
            }
        }
        return equation.closureBlok(mr,ring);
    }

    /**
     * Поиск кратчайших путей
     * @param mr матрица расстояний
     * @param i вершина, от которой нужно найти кратчайший путь
     * @param j вершина, до которой нужно найти кратчайший путь
     * @param r внешнее кольцо
     * @return вектор, каждый элемент которого является кратчайшим путем
     */
    public static VectorS findTheShortestPath(MatrixD mr, int i, int j, Ring r) {
        Ring ring = new Ring("R64MinPlus[x]");
        MatrixD mkr = searchLeastDistances(mr, r);
        ArrayList<NumberZ64> ar = new ArrayList<NumberZ64>();
        ArrayList<NumberZ64[]> A = new ArrayList<NumberZ64[]>();
        ar.add(new NumberZ64(i));
        A.add(new NumberZ64[ar.size()]);
        for (int k = 0; k < A.size(); k++) {
            while ((ar.get(ar.size() - 1)).getIntvalue() != j) {
                Element min = ring.numberZERO;
                int nomer = 0;
                for (int q = 0; q < mr.M.length; q++) {
                    boolean flag = true;
                    for (int q1 = 0; q1 < ar.size(); q1++) {
                        if (q == ar.get(q1).getIntvalue()) {
                            flag = false;
                            break;
                        }
                    }
                    if ((flag) && (!mr.M[i][q].equals(new NumberR64MinPlus(NumberR64.ZERO), ring)) && (!mr.M[i][q].compareTo(ring.numberZERO, 0, ring))) {
                        Element a1 = mr.M[i][q].multiply(mkr.M[q][j], ring);
                        if (a1.compareTo(min, 2, ring)) {
                            nomer = q;
                        }
                        min = min.add(a1, ring);
                    }
                }
                for (int q2 = (nomer + 1); q2 < mr.M.length; q2++) {
                    if (q2 != i) {
                        Element a1 = mr.M[i][q2].multiply(mkr.M[q2][j], ring);
                        if (a1.compareTo(min, 0, ring)) {
                            NumberZ64[] rez2 = new NumberZ64[ar.size() + 1];
                            ar.toArray(rez2);
                            rez2[rez2.length - 1] = new NumberZ64(q2);
                            A.add(rez2);
                        }
                    }
                }
                ar.add(new NumberZ64(nomer));
                i = nomer;
            }
            NumberZ64[] rez3 = new NumberZ64[ar.size()];
            A.set(k, ar.toArray(rez3));
            if ((k + 1) != A.size()) {
                ar = new ArrayList<NumberZ64>();
                for (int q4 = 0; q4 < A.get(k + 1).length; q4++) {
                    ar.add((A.get(k + 1))[q4]);
                }
                i = ar.get(ar.size() - 1).getIntvalue();
            }
        }
        VectorS[] Parths = new VectorS[A.size()];
        for (int ii = 0; ii < A.size(); ii++) {
            Parths[ii]=new VectorS(A.get(ii));
        }
        VectorS VV=new VectorS(Parths);
        return VV;
    }

    public static MatrixD LDM1(MatrixD A, Ring ring) {
        for(int j=0; j<A.M.length-1; j++){
            Element v = A.M[j][j].closure(ring);
            MatrixD a2 = new MatrixD(new Element[A.M.length-j-1][1]);
            MatrixD a3 = new MatrixD(new Element[1][A.M.length-j-1]);
            int f2 =0;
            for(int i=j+1; i<A.M.length; i++){
                A.M[i][j]=A.M[i][j].multiply(v, ring);
            }
            for(int k=j+1; k<A.M.length; k++){
               a2.M[f2][0] = A.M[k][j];
               a3.M[0][f2] = A.M[j][k];
               f2++;
            }
            MatrixD a1 = a2.multCU(a3, ring);
            int f=0;
            for(int i1=j+1; i1<A.M.length; i1++){
                int f1=0;
                for(int i2=j+1; i2<A.M.length; i2++){
                    A.M[i1][i2]=A.M[i1][i2].add(a1.M[f][f1], ring);
                    f1++;
                }
                f++;
            }
            for (int i = j+1; i < A.M.length; i++) {
                A.M[j][i]=v.multiply(A.M[j][i], ring);
            }
        }
        return A;
    }

    public static MatrixD LDM2(MatrixD A, Ring ring) {
        for(int j=0; j<A.M.length; j++){
            VectorS v = new VectorS(new Element[j+1]);
            for (int i = 0; i <= j; i++) {
                v.V[i]=A.M[i][j];
            }
            for(int k=0; k<=j-1; k++){
                for(int i = k+1; i<=j; i++){
                    v.V[i]=v.V[i].add(A.M[i][k].multiply(v.V[k], ring), ring);
                }
            }
            for (int i = 0; i <= j-1; i++) {
                A.M[i][j]=(A.M[i][i].closure(ring)).multiply(v.V[i], ring);
            }
            A.M[j][j]=v.V[j];
            for(int k=0; k<=j-1; k++){
                for(int q=j+1; q<A.M.length; q++){
                    A.M[q][j]=A.M[q][j].add(A.M[q][k].multiply(v.V[k], ring), ring);
                }
            }
            Element d = v.V[j].closure(ring);
            for(int l=j+1; l<A.M.length; l++){
                A.M[l][j]=A.M[l][j].multiply(d, ring);
            }
        }
        return A;
    }

    public static MatrixD LDMcheck(MatrixD A, Ring ring) {
        MatrixD L = MatrixD.ZERO(A.M.length, ring);
        MatrixD D = MatrixD.ZERO(A.M.length, ring);
        MatrixD M = MatrixD.ZERO(A.M.length, ring);
        for(int i=0; i<A.M.length; i++){
            for(int j=0; j<A.M.length; j++){
                if(i>j){
                    L.M[i][j] = A.M[i][j];
                }
                if(i==j){
                    D.M[i][j] = A.M[i][j];
                    L.M[i][j] = ring.numberONE;
                    M.M[i][j] = ring.numberONE;
                }
                if(i<j){
                    M.M[i][j] = A.M[i][j];
                }
            }
        }
        MatrixD B = L.multCU(D.multCU(M, ring), ring);
        return B;
    }

    public static boolean LDMclosureCheck(MatrixD A, MatrixD LDM, Ring ring) {
        MatrixD Aclosure = equation.closureBlok(A,ring);
        MatrixD L = MatrixD.ZERO(LDM.M.length, ring);
        MatrixD D = MatrixD.ZERO(LDM.M.length, ring);
        MatrixD M = MatrixD.ZERO(LDM.M.length, ring);
        for(int i=0; i<LDM.M.length; i++){
            for(int j=0; j<LDM.M.length; j++){
                if(i>j){
                    L.M[i][j] = LDM.M[i][j];
                }
                if(i==j){
                    D.M[i][j] = LDM.M[i][j];
                    L.M[i][j] = ring.numberONE;
                    M.M[i][j] = ring.numberONE;
                }
                if(i<j){
                    M.M[i][j] = LDM.M[i][j];
                }
            }
        }
        MatrixD Lclosure = equation.closureBlok(L,ring);
        MatrixD Dclosure = equation.closureBlok(D,ring);
        MatrixD Mclosure = equation.closureBlok(M,ring);
        MatrixD MDL = Mclosure.multCU(Dclosure.multCU(Lclosure, ring), ring);
        boolean b = Aclosure.compareTo(MDL, 0, ring);
        return b;
    }

     public static void main(String args[]) {
//        Ring ring = new Ring("R64MinPlus[x]");
//        Element[][] aa1 = new Element[][]{{new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(7)),new NumberR64MinPlus(new NumberR64(9)), ring.numberZERO, ring.numberZERO,new NumberR64MinPlus(new NumberR64(14))},
//            {new NumberR64MinPlus(new NumberR64(7)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(10)), new NumberR64MinPlus(new NumberR64(15)), ring.numberZERO, ring.numberZERO},
//            {new NumberR64MinPlus(new NumberR64(9)), new NumberR64MinPlus(new NumberR64(10)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(11)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(2))},
//            {ring.numberZERO, new NumberR64MinPlus(new NumberR64(15)), new NumberR64MinPlus(new NumberR64(11)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(6)), ring.numberZERO},
//            {ring.numberZERO, ring.numberZERO, ring.numberZERO, new NumberR64MinPlus(new NumberR64(6)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(9))},
//            {new NumberR64MinPlus(new NumberR64(14)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(2)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(9)), new NumberR64MinPlus(NumberR64.ZERO)}};
//        MatrixD mr = new MatrixD(aa1);
//        System.out.println("" + mr.toString(ring));
//        System.out.println("----------------------------");
//        MatrixD mkr = searchLeastDistances(mr);
//        System.out.println("" + mkr.toString(ring));
//        VectorS path = findTheShortestPath(mr, 0, 4);
//        System.out.println(""+path.toString(ring));
         Ring ring = new Ring("R[x]");
        Element[][] aa1 = new Element[][]{{NumberR.ZERO, new NumberR(1),new NumberR(1), Element.POSITIVE_INFINITY, new NumberR(1), Element.POSITIVE_INFINITY},
            {new NumberR(1), NumberR.ZERO, Element.POSITIVE_INFINITY, new NumberR(1), new NumberR(1), Element.POSITIVE_INFINITY},
            {new NumberR(1), Element.POSITIVE_INFINITY, NumberR.ZERO, Element.POSITIVE_INFINITY, new NumberR(1), new NumberR(1)},
            {Element.POSITIVE_INFINITY, new NumberR(1), Element.POSITIVE_INFINITY, NumberR.ZERO, Element.POSITIVE_INFINITY, Element.POSITIVE_INFINITY},
            {new NumberR(1), new NumberR(1), new NumberR(1), Element.POSITIVE_INFINITY, NumberR.ZERO, new NumberR(1)},
            {Element.POSITIVE_INFINITY, Element.POSITIVE_INFINITY, new NumberR(1), Element.POSITIVE_INFINITY, new NumberR(1), NumberR.ZERO}};
        MatrixD mr = new MatrixD(aa1);
        System.out.println("" + mr.toString(ring));
        System.out.println("----------------------------");
        MatrixD mkr = searchLeastDistances(mr, ring);
        System.out.println("" + mkr.toString(ring));
        VectorS path = findTheShortestPath(mr, 0, 5, ring);
        System.out.println(""+path.toString(ring));
//       NumberR64MinPlus a = new NumberR64MinPlus(NumberR64.ZERO);
//       System.out.println(""+a.isZero(ring));

//        Element[][] aa1 = new Element[][]{{new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(7)),new NumberR64MinPlus(new NumberR64(9)), ring.numberZERO, ring.numberZERO,new NumberR64MinPlus(new NumberR64(14))},
//            {new NumberR64MinPlus(new NumberR64(7)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(2)), new NumberR64MinPlus(new NumberR64(15)), ring.numberZERO, ring.numberZERO},
//            {new NumberR64MinPlus(new NumberR64(9)), new NumberR64MinPlus(new NumberR64(2)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(10)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(2))},
//            {ring.numberZERO, new NumberR64MinPlus(new NumberR64(15)), new NumberR64MinPlus(new NumberR64(10)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(1)), ring.numberZERO},
//            {ring.numberZERO, ring.numberZERO, ring.numberZERO, new NumberR64MinPlus(new NumberR64(1)), new NumberR64MinPlus(NumberR64.ZERO), new NumberR64MinPlus(new NumberR64(9))},
//            {new NumberR64MinPlus(new NumberR64(14)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(2)), ring.numberZERO, new NumberR64MinPlus(new NumberR64(9)), new NumberR64MinPlus(NumberR64.ZERO)}};
//        MatrixD mr = new MatrixD(aa1);
//        System.out.println("" + mr.toString(ring));
//        System.out.println("----------------------------");
//        MatrixD mkr = searchLeastDistances(mr);
//        System.out.println("" + mkr.toString(ring));
//        VectorS path = findTheShortestPath(mr, 0, 4);
//        System.out.println(""+path.toString(ring));

        //Ring ring = new Ring("R64[x]");
//         Ring ring = new Ring("R64MaxPlus[x]");
////         Element[][] a1 = new Element[][]{{new NumberR64(10),new NumberR64(10),new NumberR64(20)},
////                                          {new NumberR64(20),new NumberR64(25),new NumberR64(40)},
////                                          {new NumberR64(30),new NumberR64(50),new NumberR64(61)}};
//         Element[][] a1 = new Element[][]{{new NumberR64MaxPlus(new NumberR64(-1)),new NumberR64MaxPlus(new NumberR64(-1)),new NumberR64MaxPlus(new NumberR64(-2))},
//                                          {new NumberR64MaxPlus(new NumberR64(-2)),new NumberR64MaxPlus(new NumberR64(1)),new NumberR64MaxPlus(new NumberR64(-4))},
//                                          {new NumberR64MaxPlus(new NumberR64(-3)),new NumberR64MaxPlus(new NumberR64(-4)),new NumberR64MaxPlus(new NumberR64(1))}};
//         MatrixD a = new MatrixD(a1);
//         MatrixD b = new MatrixD(a1);
//         System.out.println(""+a.toString());
//         System.out.println("---------------");
//         MatrixD a2 = LDM1(a,ring);
//         MatrixD b2 = LDM2(b,ring);
//         System.out.println("LDM1");
//         System.out.println(""+a2.toString());
//          System.out.println("---------------");
//         System.out.println("Проверка");
//         MatrixD check = LDMcheck(a2,ring);
//
//         System.out.println(""+new MatrixD(a1).compareTo(check, 0, ring));
//         System.out.println("Проверка A*=M*D*L*:  "+LDMclosureCheck(new MatrixD(a1),a2,ring));
//         System.out.println("---------------");
//         System.out.println("LDM2");
//         System.out.println(""+b2.toString());
//         System.out.println("---------------");
//         System.out.println("Проверка");
//         MatrixD check1 = LDMcheck(b2,ring);
//         System.out.println(""+new MatrixD(a1).compareTo(check1, 0, ring));
//         System.out.println("Проверка A*=M*D*L*:  "+LDMclosureCheck(new MatrixD(a1),b2,ring));
    }
}
