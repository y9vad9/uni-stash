/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU.TO;

import com.mathpar.matrix.LDU.ETDUtils;
import com.mathpar.matrix.LDU.Track;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.util.List;

import static com.mathpar.matrix.LDU.TO.ETDpTO.recoveryArray;

/**
 *
 * @author ridkeim
 */
public class ETDpLDUKDWTO extends ETDpLDUTO{
    MatrixS K,W;

    protected ETDpLDUKDWTO() {
        super();
    }

    public ETDpLDUKDWTO(MatrixS L, MatrixS U, MatrixS V, MatrixS M,MatrixS K, MatrixS W, Element[] d, NumberZ m, int part_number) {
        super(L, U, V, M, d, m, part_number);
        this.K = K;
        this.W = W;
    }
    
    @Override
    public void restore(List<ETDpTO> etdpto, int myrank){
        MatrixS[] Lp = new MatrixS[etdpto.size()];
        MatrixS[] Up = new MatrixS[etdpto.size()];
        MatrixS[] Vp = new MatrixS[etdpto.size()];
        MatrixS[] Mp = new MatrixS[etdpto.size()];
        MatrixS[] Kp = new MatrixS[etdpto.size()];
        MatrixS[] Wp = new MatrixS[etdpto.size()];
        Element[][] dp = new Element[etdpto.size()][];
        NumberZ[] mods = new NumberZ[etdpto.size()];
        int t = 0;
        for (ETDpTO etd : etdpto) {
            ETDpLDUKDWTO etdkw = (ETDpLDUKDWTO) etd;
            Kp[t] = etdkw.K;
            Wp[t] = etdkw.W;
            Lp[t] = etdkw.L;
            Up[t] = etdkw.U;
            Vp[t] = etdkw.V;
            Mp[t] = etdkw.M;
            dp[t] = etdkw.d;
            mods[t] = etdkw.m;
            t++;
        }
        NumberZ[] arr = Newton.arrayOfNumbersForNewton(mods);
        L = Newton.recoveryNewtonMatrixSWithoutArr(Lp, mods, arr);
        U = Newton.recoveryNewtonMatrixSWithoutArr(Up, mods, arr);
        V = Newton.recoveryNewtonMatrixSWithoutArr(Vp, mods, arr);
        M = Newton.recoveryNewtonMatrixSWithoutArr(Mp, mods, arr);
        K = Newton.recoveryNewtonMatrixSWithoutArr(Kp, mods, arr);
        W = Newton.recoveryNewtonMatrixSWithoutArr(Wp, mods, arr);
        d = recoveryArray(dp, mods, arr);
        part_number = myrank;
    }
    @Override
    public void join(ETDpTO[] restored_parts){
        MatrixS[] Lp = new MatrixS[restored_parts.length];
        MatrixS[] Up = new MatrixS[restored_parts.length];
        MatrixS[] Vp = new MatrixS[restored_parts.length];
        MatrixS[] Mp = new MatrixS[restored_parts.length];
        MatrixS[] Kp = new MatrixS[restored_parts.length];
        MatrixS[] Wp = new MatrixS[restored_parts.length];
        Element[][] dp = new Element[restored_parts.length][];
        for (ETDpTO part : restored_parts) {
            int part_numb = part.part_number;
            ETDpLDUKDWTO etdkw = (ETDpLDUKDWTO) part;
            Kp[part_numb] = etdkw.K;
            Wp[part_numb] = etdkw.W;
            Lp[part_numb] = etdkw.L;
            Up[part_numb] = etdkw.U;
            Vp[part_numb] = etdkw.V;
            Mp[part_numb] = etdkw.M;
            dp[part_numb] = etdkw.d;
        }
        L = joinMatricesByRows(Lp);
        U = joinMatricesByRows(Up);
        V = joinMatricesByRows(Vp);
        M = joinMatricesByRows(Mp);
        W = joinMatricesByRows(Wp);
        K = joinMatricesByRows(Kp);
        d = joinArrays(dp);
        part_number = -1;
    }

    @Override
    public MatrixS[] generateResult(int size, Track track) {
        int rank = L.size;
        MatrixS Ld, Dd, Ud,Wd,Kd,Dn;
        int el = size - rank;
        MatrixS E = MatrixS.scalarMatrix(el, Ring.ringZxyz.numberONE, Ring.ringZxyz);
        MatrixS zero = MatrixS.zeroMatrix();
        Ld = (size==rank)?L:ETDUtils.join(
                new MatrixS[][] {{L, zero}, {M, E}},
                new int[] {rank, el},
                new int[] {rank, el});
        Ud = (size==rank)?U:ETDUtils.join(
                new MatrixS[][] {{U, V}, {zero, E}},
                new int[] {rank, el},
                new int[] {rank, el});
        Kd = (size==rank)?K:ETDUtils.join(
                new MatrixS[][] {{K, zero}, {zero, E}},
                new int[] {rank, el},
                new int[] {rank, el});
        Wd = (size==rank)?W:ETDUtils.join(
                new MatrixS[][] {{W, zero}, {zero, E}},
                new int[] {rank, el},
                new int[] {rank, el});
        Dd = generateD(size);        
        int[] er = track.getRowPermutation();
        int[] ec = track.getColumnPermutation();
        
        Dn = Dd.permutationOfRows(ec).permutationOfColumns(er);
        Wd = Wd.permutationOfRows(ec).permutationOfColumns(ec);        
        Kd = Kd.permutationOfRows(er).permutationOfColumns(er);

        Ld = Ld.permutationOfRows(er).permutationOfColumns(er);
        Dd = Dd.permutationOfRows(er).permutationOfColumns(ec);
        Ud = Ud.permutationOfRows(ec).permutationOfColumns(ec);
        
        return new MatrixS[]{Ld,Dd,Ud,Wd,Dn,Kd};
    }

    @Override
    public ETDpTO[] split_into_parts(int part_count) {
        MatrixS[] L_parts = split_into_parts(L, part_count);
        MatrixS[] U_parts = split_into_parts(U, part_count);
        MatrixS[] V_parts = split_into_parts(V, part_count);
        MatrixS[] M_parts = split_into_parts(M, part_count);
        MatrixS[] K_parts = split_into_parts(K, part_count);
        MatrixS[] W_parts = split_into_parts(W, part_count);
        Element[][] d_parts = split_Array(d, part_count);
        ETDpTO[] result = new ETDpLDUKDWTO[part_count];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ETDpLDUKDWTO(L_parts[i],U_parts[i],V_parts[i],M_parts[i],K_parts[i],W_parts[i],d_parts[i],m,i);
        }
        return result;
    }
    
    
    
    
    
    
    
    
}
