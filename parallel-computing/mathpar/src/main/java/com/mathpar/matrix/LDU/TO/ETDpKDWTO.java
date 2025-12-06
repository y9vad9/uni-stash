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
public class ETDpKDWTO extends ETDpTO{
    protected MatrixS L,U;
    
    public ETDpKDWTO(){
    }

    public ETDpKDWTO(MatrixS L,MatrixS U,Element[] d, NumberZ m, int part_number) {
        this.L = L;
        this.U = U;
        this.d = d;
        this.part_number = part_number;
        this.m = m;
    }
    
    @Override
    public void restore(List<ETDpTO> etdpto, int myrank) {
        MatrixS[] Lp = new MatrixS[etdpto.size()];
        MatrixS[] Up = new MatrixS[etdpto.size()];
        Element[][] dp = new Element[etdpto.size()][];
        NumberZ[] mods = new NumberZ[etdpto.size()];
        int t = 0;
        for (ETDpTO etdTO : etdpto) {
            ETDpKDWTO etd  = (ETDpKDWTO) etdTO;
            Lp[t] = etd.L;
            Up[t] = etd.U;
            dp[t] = etd.d;
            mods[t] = etd.m;
            t++;
        }
        NumberZ[] arr = Newton.arrayOfNumbersForNewton(mods);
        L = Newton.recoveryNewtonMatrixSWithoutArr(Lp, mods, arr);
        U = Newton.recoveryNewtonMatrixSWithoutArr(Up, mods, arr);
        d = recoveryArray(dp, mods, arr);
        part_number = myrank;
    }

    @Override
    public void join(ETDpTO[] restored_parts) {
        MatrixS[] Lp = new MatrixS[restored_parts.length];
        MatrixS[] Up = new MatrixS[restored_parts.length];
        Element[][] dp = new Element[restored_parts.length][];
        for (ETDpTO part : restored_parts) {
            int part_numb = part.part_number;
            ETDpKDWTO part1 = (ETDpKDWTO) part; 
            Lp[part_numb] = part1.L;
            Up[part_numb] = part1.U;
            dp[part_numb] = part1.d;
        }
        L = joinMatricesByRows(Lp);
        U = joinMatricesByRows(Up);
        d = joinArrays(dp);
        part_number = -1;
    }

    @Override
    public ETDpTO[] split_into_parts(int part_count) {
        MatrixS[] L_parts = split_into_parts(L, part_count);
        MatrixS[] U_parts = split_into_parts(U, part_count);
        Element[][] d_parts = split_Array(d, part_count);
        ETDpTO[] result = new ETDpKDWTO[part_count];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ETDpKDWTO(L_parts[i],U_parts[i],d_parts[i],m,i);
        }
        return result;
    }

    @Override
    public MatrixS[] generateResult(int size, Track track) {
        int rank = L.size;
        MatrixS Wd, Dd, Kd;
        int el = size - rank;
        MatrixS E = MatrixS.scalarMatrix(el, Ring.ringZxyz.numberONE, Ring.ringZxyz);
        MatrixS zero = MatrixS.zeroMatrix();
        Kd = (size==rank)?L:ETDUtils.join(
                new MatrixS[][] {{L, zero}, {zero, zero}},
                new int[] {rank, el},
                new int[] {rank, el});
        Wd = (size==rank)?U:ETDUtils.join(
                new MatrixS[][] {{U, zero}, {zero, zero}},
                new int[] {rank, el},
                new int[] {rank, el});        
        Dd = generateD(rank);
        int[] er = track.getRowPermutation();
        int[] ec = track.getColumnPermutation();
        Wd = Wd.permutationOfRows(ec).permutationOfColumns(ec);
        Dd = Dd.permutationOfRows(ec).permutationOfColumns(er);
        Kd = Kd.permutationOfRows(er).permutationOfColumns(er);
        return new MatrixS[]{Wd,Dd,Kd};
    }
    
    
}
