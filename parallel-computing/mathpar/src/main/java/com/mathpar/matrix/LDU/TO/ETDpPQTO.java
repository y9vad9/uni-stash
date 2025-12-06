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
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;

/**
 *
 * @author ridkeim
 */
public class ETDpPQTO extends ETDpLDUKDWTO{

    protected ETDpPQTO() {
        super();
    }
    public ETDpPQTO(MatrixS L, MatrixS U, MatrixS V, MatrixS M,MatrixS K, MatrixS W, Element[] d, NumberZ m, int part_number) {
        super(L, U, V, M,K, W, d, m, part_number);
    }
    @Override
    public MatrixS[] generateResult(int size, Track track) {
        int rank = L.size;
        MatrixS Ld,Dlu,Dd,Ud,Wd,Kd,Pd,Qd;
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
        Dlu = generateD(size);
        Pd = track.getRowPermutationAsMatrixS(size, Ring.ringZxyz);
        Qd = track.getColumnPermutationAsMatrixS(size, Ring.ringZxyz);
        
        return new MatrixS[]{Pd,Ld,Dlu,Ud,Qd,Wd/*,Dd*/,Kd};
    }
    
}
