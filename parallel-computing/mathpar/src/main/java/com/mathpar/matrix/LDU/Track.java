/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author ridkeim
 */
public class Track implements Serializable,Comparable<Track>{
    private int[] er;
    private int[] ec;
    private int signum = 1;
    public Track(){
        er = new int[]{};
        ec = new int[]{};
    }
    public void setRowPermutation(int[] Er){
        er = Er;
    }
    public int getSignum(){
        return signum;
    }
    public int[] getRowPermutation(){
        return er;
    }
    public int[] getTransposedRowPermutation(){
        return MatrixS.transposePermutation(er);
    }
    public void setColumnPermutatuion(int[] Ec){
        ec = Ec;
    }
    public int[] getColumnPermutation(){
        return ec;
    }
    public int[] getTransposedColumnPermutation(){
        return MatrixS.transposePermutation(ec);
    }
    private void appendRowPermutation(int[] Er, int shift,int maxrow) {
        int[] tEr = (0 == shift) ? Er : increasePermutation(Er, shift);
        er = MatrixS.multPermutations(tEr, er, maxrow);
    }
    private void appendColPermutation(int[] Ec, int shift, int maxcol) {
        int[] tEc = (0 == shift) ? Ec : increasePermutation(Ec, shift);
        ec = MatrixS.multPermutations(tEc, ec, maxcol);
    }    
    public void appendPermutation(Track t, int shift, int maxrow,int maxcol){
        appendColPermutation(t.getColumnPermutation(), shift, maxcol);
        appendRowPermutation(t.getRowPermutation(), shift, maxrow);
        signum *= t.signum;
    }
    public void appendPermutations(Track f, Track s, int fr, int sr,int divrow,int maxrow,int divcol,int maxcol) {
        int[] sEr1, sEc1, sEr2, tEr1;
        int[] sEr1T, sEc1T, sEr2T, tEr1T;

        sEr1 = getBlockToEndPermutation(0, divrow, maxrow);
        
        sEr1T = MatrixS.transposePermutation(sEr1);
        sEr2 = getBlockToEndPermutation(fr, maxrow - divrow - fr, maxrow);
        sEr2T = MatrixS.transposePermutation(sEr2);
        sEc1 = getBlockToEndPermutation(fr, divcol - fr, maxcol);
        sEc1T = MatrixS.transposePermutation(sEc1);

//        appendRowPermutation(sEr1T, 0,maxrow);
//        appendRowPermutation(f.getRowPermutation(), 0,maxrow);        
//        appendRowPermutation(sEr2T, 0,maxrow);
//
//        appendColPermutation(f.getColumnPermutation(), 0,maxcol);
//        appendColPermutation(sEc1T, 0,maxcol);
        
        appendRowPermutation(sEr1T, 0,maxrow);
        appendPermutation(f, 0, maxrow, maxcol);        
        appendRowPermutation(sEr2T, 0,maxrow);
        appendColPermutation(sEc1T, 0,maxcol);

        tEr1 = getBlockToEndPermutation(fr + sr, divrow - sr, maxrow);
        tEr1T = MatrixS.transposePermutation(tEr1);

//        appendRowPermutation(s.getRowPermutation(), fr,maxrow);
//        appendRowPermutation(tEr1T, 0,maxrow);
//
//        appendColPermutation(s.getColumnPermutation(), fr,maxcol);

        appendPermutation(s, fr, maxrow, maxcol);
        appendRowPermutation(tEr1T, 0,maxrow);
        
    }    
    
    public int[] getBlockToEndPermutation(int startpos, int sizeOfBlock, int sizeMatrix) {
        if ((sizeOfBlock == 0) || (startpos + sizeOfBlock - sizeMatrix >= 0)) {
            return new int[0];
        }
        int dd = sizeMatrix - startpos;
        int[] res = new int[dd * 2];
        int last = startpos + sizeOfBlock;
        int sizeLastBlock = sizeMatrix - last;
        for (int i = 0; i < dd; i++) {
            int row = i + startpos;
            res[i] = row;
            res[i + dd] = (row < last) ? row + sizeLastBlock : row - sizeOfBlock;
        }
        signum *= (((sizeOfBlock*sizeLastBlock)%2)==1)?-1:1;
        return res;
    }
    
    public static int[] getBlockPermutation(int sizeOfFirstBlock, int sizeMatrix) {
        if (sizeOfFirstBlock == 0) {
            return new int[0];
        }
        int[] res = new int[sizeMatrix * 2];
        int last = sizeMatrix - sizeOfFirstBlock;
        for (int i = 0; i < sizeMatrix; i++) {
            res[i] = i;
            res[i + sizeMatrix] = (i < last) ? i + sizeOfFirstBlock : i - last;
        }
        return res;
    }
    
    public static int[] increasePermutation(int[] E, int k) {
        int[] t = new int[E.length];
        for (int i = 0; i < E.length; i++) {
            t[i] = E[i] + k;
        }
        return t;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if(!(obj instanceof Track)){
//            return false;
//        }
//        Track t = (Track) obj;
//        return (signum == t.signum) && Arrays.equals(er, t.er) && Arrays.equals(ec, t.ec);
//    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Arrays.hashCode(this.er);
        hash = 59 * hash + Arrays.hashCode(this.ec);
        hash = 59 * hash + this.signum;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Track other = (Track) obj;
        if (this.signum != other.signum) {
            return false;
        }
        if (!Arrays.equals(this.er, other.er)) {
            return false;
        }
        if (!Arrays.equals(this.ec, other.ec)) {
            return false;
        }        
        return true;
    }  

    @Override
    public int compareTo(Track o) {
        int sign = signum + o.signum;
        switch(sign){
            case 2:
            case -2:
                return comparePermutations(o);
            default:
                return (signum>o.signum)?1:-1;
        }
    }
       
    private int comparePermutations(Track o){
        if(!Arrays.equals(er, o.er)){
            return 1;
        }else{
            if(!Arrays.equals(ec, o.ec)){
                return -1;
            }
            else{
                return 0;
            }
        }
    }
    
    public MatrixS getRowPermutationAsMatrixS(int size,Ring r){
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        for (int i = 0; i < size; i++) {
            M[i] = new Element[]{r.numberONE};
            col[i] = new int[]{i};
        }
        for (int i = 0; i < er.length/2; i++) {
            int to = er[i+er.length/2];
            int from = er[i]; 
            col[to][0] = from;
        }
        return new MatrixS(M, col);
    }
    public MatrixS getColumnPermutationAsMatrixS(int size,Ring r){
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        for (int i = 0; i < size; i++) {
            M[i] = new Element[]{r.numberONE};
            col[i] = new int[]{i};
        }
        for (int i = 0; i < ec.length/2; i++) {
            int to = ec[i+ec.length/2];
            int from = ec[i]; 
            col[from][0] = to;
        }
        return new MatrixS(M, col);
    }    
    
    public MatrixS getRowPermutationAsMatrixS(int[] p,int size,Ring r){
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        for (int i = 0; i < size; i++) {
            M[i] = new Element[]{r.numberONE};
            col[i] = new int[]{i};
        }
        for (int i = 0; i < p.length/2; i++) {
            int to = p[i+p.length/2];
            int from = p[i]; 
            col[to][0] = from;
        }
        return new MatrixS(M, col);
    }
    
    public static MatrixS getColumnPermutationAsMatrixS(int[] q,int size,Ring r){
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        for (int i = 0; i < size; i++) {
            M[i] = new Element[]{r.numberONE};
            col[i] = new int[]{i};
        }
        for (int i = 0; i < q.length/2; i++) {
            int to = q[i+q.length/2];
            int from = q[i]; 
            col[from][0] = to;
        }
        return new MatrixS(M, col);    
    }   
}
