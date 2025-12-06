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
public class ETDpLDUTO extends ETDpKDWTO{   
    MatrixS V,M;
    
    public ETDpLDUTO(MatrixS L, MatrixS U, MatrixS V, MatrixS M, Element[] d, NumberZ m,int part_number){
        super(L, U, d, m, part_number);
        this.V = V;
        this.M = M;
    }
    
    protected ETDpLDUTO(){
    }
    
    @Override
    public void restore(List<ETDpTO> etdpto, int myrank) {
        MatrixS[] Lp = new MatrixS[etdpto.size()];
        MatrixS[] Up = new MatrixS[etdpto.size()];
        MatrixS[] Vp = new MatrixS[etdpto.size()];
        MatrixS[] Mp = new MatrixS[etdpto.size()];
        Element[][] dp = new Element[etdpto.size()][];
        NumberZ[] mods = new NumberZ[etdpto.size()];
        int t = 0;
        for (ETDpTO etdTO : etdpto) {
            ETDpLDUTO etd  = (ETDpLDUTO) etdTO;
            Lp[t] = etd.L;
            Up[t] = etd.U;
            Vp[t] = etd.V;
            Mp[t] = etd.M;
            dp[t] = etd.d;
            mods[t] = etd.m;
            t++;
        }
        NumberZ[] arr = Newton.arrayOfNumbersForNewton(mods);
        L = Newton.recoveryNewtonMatrixSWithoutArr(Lp, mods, arr);
        U = Newton.recoveryNewtonMatrixSWithoutArr(Up, mods, arr);
        V = Newton.recoveryNewtonMatrixSWithoutArr(Vp, mods, arr);
        M = Newton.recoveryNewtonMatrixSWithoutArr(Mp, mods, arr);
        d = recoveryArray(dp, mods, arr);
        part_number = myrank;
    }
    @Override
    public void join(ETDpTO[] restored_parts) {
        MatrixS[] Lp = new MatrixS[restored_parts.length];
        MatrixS[] Up = new MatrixS[restored_parts.length];
        MatrixS[] Vp = new MatrixS[restored_parts.length];
        MatrixS[] Mp = new MatrixS[restored_parts.length];
        Element[][] dp = new Element[restored_parts.length][];
        for (ETDpTO part : restored_parts) {
            int part_numb = part.part_number;
            ETDpLDUTO part1 = (ETDpLDUTO) part; 
            Lp[part_numb] = part1.L;
            Up[part_numb] = part1.U;
            Vp[part_numb] = part1.V;
            Mp[part_numb] = part1.M;
            dp[part_numb] = part1.d;
        }
        L = joinMatricesByRows(Lp);
        U = joinMatricesByRows(Up);
        V = joinMatricesByRows(Vp);
        M = joinMatricesByRows(Mp);
        d = joinArrays(dp);
        part_number = -1;
    }
    @Override
    public ETDpTO[] split_into_parts(int part_count){
        MatrixS[] L_parts = split_into_parts(L, part_count);
        MatrixS[] U_parts = split_into_parts(U, part_count);
        MatrixS[] V_parts = split_into_parts(V, part_count);
        MatrixS[] M_parts = split_into_parts(M, part_count);
        Element[][] d_parts = split_Array(d, part_count);
        ETDpTO[] result = new ETDpLDUTO[part_count];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ETDpLDUTO(L_parts[i],U_parts[i],V_parts[i],M_parts[i],d_parts[i],m,i);
        }
        return result;
    }
    @Override
    public MatrixS[] generateResult(int size,Track track){
        int rank = L.size;
        MatrixS Ld, Dd, Ud;
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
        Dd = generateD(size);
        int[] er = track.getRowPermutation();
        int[] ec = track.getColumnPermutation();
        Ld = Ld.permutationOfRows(er).permutationOfColumns(er);
        Dd = Dd.permutationOfRows(er).permutationOfColumns(ec);
        Ud = Ud.permutationOfRows(ec).permutationOfColumns(ec);
        return new MatrixS[]{Ld,Dd,Ud};
    };
   
    
}
