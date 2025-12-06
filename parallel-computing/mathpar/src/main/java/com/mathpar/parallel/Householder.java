package com.mathpar.parallel;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;

import java.util.ArrayList;

public class Householder {
    public static void main(String[] args) {
        Ring ring = new Ring("R64");
        int [][]m = new int[][] {{1,1,1,1},{1,1,2,1},{1,1,1,2},{1,2,1,1}};
        MatrixD A = new MatrixD(m, ring);
        MatrixD I = new MatrixD(MatrixS.scalarMatrix(A.M.length, ring.numberONE, ring));
        int operationsAmount = A.M.length - 1;
        int i = 0;
        MatrixD Ai = A;

        ArrayList<MatrixD> As = new ArrayList<>(2 * A.M.length - 3);
        ArrayList<MatrixD> Ps = new ArrayList<>(A.M.length - 1);
        ArrayList<MatrixD> Qs = new ArrayList<>(A.M.length - 2);

        while (i < operationsAmount) {
            VectorS x = Ai.takeColumn(i + 1);
            for (int k = 0; k < i; k++){
                x.V[k] = ring.numberZERO;
            }

            if (getNorm2(x, i + 1, ring).isZero(ring)){
                Ps.add(new MatrixD(I.M.clone()));
            } else {
                Element norm_x = x.multiply(x.transpose(ring), ring).sqrt(ring);
                VectorS u = new VectorS(x.V.clone());

                if (u.V[i].value < 0)
                    u.V[i] = u.V[i].subtract(norm_x, ring);
                else
                    u.V[i] = u.V[i].add(norm_x, ring);

                Element e = x.V[i + 1];
                if (x.V[i].value < 0)
                    e = x.V[i].multiply(new NumberR64(-1), ring);

                Element norm_Squ = norm_x.multiply(norm_x.add(e, ring), ring).multiply(new NumberR64(2),ring);
                VectorS ut = (VectorS) u.transpose(ring);
                NumberR64 norm_div = NumberR64.valueOf(2/norm_Squ.value);
                MatrixD uut = (MatrixD) ut.multiply(u, ring);
                MatrixD nd_uut = (MatrixD) norm_div.multiply(uut, ring);
                MatrixD Pi = I.subtract(nd_uut, ring);
                Ps.add(Pi);

                Element dT = u.multiply(Ai, ring);
                Element nd_u = norm_div.multiply(ut, ring);
                Element nd_u_dT = nd_u.multiply(dT, ring);
                Ai = (MatrixD) Ai.subtract(nd_u_dT, ring);
                As.add(new MatrixD(Ai.M.clone()));
                System.out.println("A" + (2*i+1) + ":");
                System.out.println(Ai.toString(ring));
            }

            if (i == (A.M.length - 2)) {
                i++;
                continue;
            }

            VectorS x_r = new VectorS(Ai.takeRow(i + 1).V.clone());

            for (int k = 0; k < i + 1; k++){
                x_r.V[k] = ring.numberZERO;
            }

            if (getNorm2(x_r, i + 2, ring).isZero(ring)){
                Qs.add(new MatrixD(I.M.clone()));
            } else {
                Element norm_x_r = x_r.multiply(x_r.transpose(ring),ring).sqrt(ring);
                VectorS u_r = new VectorS(x_r.V.clone());

                if (u_r.V[i + 1].value < 0)
                    u_r.V[i + 1] = u_r.V[i + 1].subtract(norm_x_r, ring);
                else
                    u_r.V[i + 1] = u_r.V[i + 1].add(norm_x_r, ring);

                Element e_r = x_r.V[i + 1];
                if (x_r.V[i + 1].value < 0)
                    e_r = x_r.V[i + 1].multiply(new NumberR64(-1), ring);
                Element norm_Squ_r = norm_x_r.multiply(norm_x_r.add(e_r, ring), ring).multiply(new NumberR64(2),ring);
                VectorS ut_r = (VectorS) u_r.transpose(ring);
                Element norm_div_r = NumberR64.valueOf(2/norm_Squ_r.value);
                MatrixD utu_r = (MatrixD) ut_r.multiply(u_r, ring);
                MatrixD nd_utu_r = (MatrixD) norm_div_r.multiply(utu_r, ring);
                MatrixD Qi = I.subtract(nd_utu_r, ring);
                Qs.add(Qi);

                VectorS d_r = (VectorS) Ai.multiply(ut_r, ring);
                VectorS nd_d_r = (VectorS) norm_div_r.multiply(d_r, ring);
                MatrixD nd_d_u_r = (MatrixD) nd_d_r.transpose(ring).multiply(u_r, ring);
                Ai = Ai.subtract(nd_d_u_r, ring);
                System.out.println("A" + (2 * i + 2) + ":");
                System.out.println(Ai.toString(ring));
                As.add(new MatrixD(Ai.M.clone()));
            }

            i++;
        }

        check(A, As, Ps, Qs, ring);
    }

    private static Element getNorm2(VectorS v, int fromIndex, Ring ring) {
        Element norm2 = ring.numberZERO();

        for (int i = fromIndex; i < v.V.length; i++) {
            norm2 = norm2.add(v.V[i].multiply(v.V[i], ring), ring);
        }

        return norm2;
    }

    private static void check(MatrixD A, ArrayList<MatrixD> As, ArrayList<MatrixD> Ps, ArrayList<MatrixD> Qs, Ring ring){
        MatrixD P = Ps.get(Ps.size() - 1);

        for (int j = Ps.size() - 2; j >= 0; j--){
            P = (MatrixD) P.multiply(Ps.get(j), ring);
        }

        MatrixD U = P.transpose(ring);
        System.out.println("U:");
        System.out.println(U.toString());

        MatrixD W = Qs.get(0);

        for (int j = 1; j < Qs.size() - 1; j++){
            W = (MatrixD) W.multiply(Qs.get(j), ring);
        }

        System.out.println("W:");
        System.out.println(W.toString());
        MatrixD A_final = As.get(As.size() - 1);
        MatrixD B = (MatrixD) U.multiply(A_final, ring).multiply(W, ring);
        System.out.println("B:");
        System.out.println(B.toString());

        MatrixD Check = B.subtract(A, ring);
        System.out.println("Check:");
        System.out.println(Check.toString());

        MatrixD Chu = (MatrixD) U.inverse(ring).subtract(P, ring);
        System.out.println("Chu:");
        System.out.println(Chu.toString());

        MatrixD Chw = (MatrixD) W.inverse(ring).subtract(W.transpose(ring), ring);
        System.out.println("Chw:");
        System.out.println(Chw.toString());
    }
}
