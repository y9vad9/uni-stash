/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU.TO;

import com.mathpar.matrix.LDU.Track;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.io.Serializable;
import java.util.List;



/**
 *
 * @author ridkeim
 */
public abstract class ETDpTO implements Serializable{
    
    protected Element[] d;
    protected NumberZ m;
    protected int part_number = -1;
    
    public static final int RESULT_LDU = 0;
    public static final int RESULT_WDK = 1;
    public static final int RESULT_LDUWDK = 2;
    public static final int RESULT_PLDUQWDK = 3;
    
    public abstract void restore(List<ETDpTO> etdpto, int myrank);
    public abstract void join(ETDpTO[] restored_parts);
    public abstract ETDpTO[] split_into_parts(int part_count);
    public abstract MatrixS[] generateResult(int size,Track track);
    
    public static ETDpTO getInstance(int result_id){
        switch(result_id){
            case RESULT_WDK:
                return new ETDpKDWTO();
            case RESULT_LDU:
                return new ETDpLDUTO();
            case RESULT_LDUWDK:
                return new ETDpLDUKDWTO();
            case RESULT_PLDUQWDK:
                return new ETDpPQTO();
            default:
                return null;
        }
    }
    
    public int getPartNumber(){
        return part_number;
    }
    protected static Element[] recoveryArray(Element[][] d, NumberZ[] mods,NumberZ[] arr){
        int size = 0;
        if(d !=null && d.length>0){
            size = d[0].length;
        }
        Element[] result = new NumberZ[size];
        for (int i = 0; i < result.length; i++) {
            Element[] tmp = new NumberZ[d.length];
            for (int j = 0; j < d.length; j++) {
                tmp[j] = d[j][i];
            }
            result[i] = Newton.recoveryNewtonWithoutArr(mods, (NumberZ[]) tmp, arr);
        }
        return result;
    }    
    protected static MatrixS joinMatricesByRows(MatrixS[] matrices){
        int all_size = 0;
        for (MatrixS matrix : matrices) {
            all_size+=matrix.size;
        }
        Element[][] Mm = new Element[all_size][];
        int[][] Cc = new int[all_size][];
        int row=0;
        int colNumb = 0;
        for (int i = 0; i < matrices.length; i++) {
            for (int j = 0; j < matrices[i].size; j++) {
                Mm[row] = matrices[i].M[j];
                Cc[row] = matrices[i].col[j];
                row++;
            }
            colNumb = Math.max(colNumb, matrices[i].colNumb);
        }
        return new MatrixS(all_size, colNumb, Mm, Cc);
    }
    protected static Element[] joinArrays(Element[][] array){
        int all_size = 0;
        for (Element[] array1 : array) {
            all_size+=array1.length;
        }
        Element[] result = new Element[all_size];
        int position = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                result[position] = array[i][j];
                position++;
            }
        }
        return result;
    }    
    protected static MatrixS[] split_into_parts(MatrixS mS, int part_count){
        MatrixS[] result = new MatrixS[part_count];
        int[] count = new int[part_count];
        int[] start_positions = new int[part_count];
        int count_per_proc = mS.size/part_count;        
        int div = mS.size-part_count*count_per_proc;
        for (int i = 0; i < count.length; i++) {
            count[i]=count_per_proc;
            if(div>0){
                count[i]++;
                div--;
            }
        }
        int start_position= 0;
        for (int i = 0; i < start_positions.length; i++) {
            start_positions[i] += start_position;
            start_position+=count[i];
        }
        for (int i = 0; i < result.length; i++) {
            Element[][] Mm = new Element[count[i]][];
            int[][] Cc = new int[count[i]][];
            int collen = 0;
            for (int j = 0; j < count[i]; j++) {
                Mm[j] = mS.M[j+start_positions[i]];
                Cc[j] = mS.col[j+start_positions[i]];
                collen = Math.max(collen, Cc[j].length);
            }
            if(collen!=0){
                result[i] = new MatrixS(Mm, Cc);
            }else{
                result[i] = new MatrixS(Cc.length,collen,Mm, Cc);
            }
        }
        return result;
    }
    protected static Element[][] split_Array(Element[] el, int part_count){
        Element[][] result = new Element[part_count][];
        int[] count = new int[part_count];
        int[] start_positions = new int[part_count];
        int count_per_proc = el.length/part_count;        
        int div = el.length-part_count*count_per_proc;
        for (int i = 0; i < count.length; i++) {
            count[i]=count_per_proc;
            if(div>0){
                count[i]++;
                div--;
            }
        }
        int start_position= 0;
        for (int i = 0; i < start_positions.length; i++) {
            start_positions[i] += start_position;
            start_position+=count[i];
            Element[] tmp = new Element[count[i]];
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = el[j+start_positions[i]];
            }
            result[i] = tmp;
        }
        return result;
    }
    protected NumberZ getMaxD(){
        return (NumberZ) d[d.length-1];
    }
    protected MatrixS generateD(int size) {
        Ring ring = Ring.ringZxyz;        
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        if (d.length > 0) {
            M[0] = new Element[] {new Fraction(ring.numberONE(), d[0]).cancel(ring)};
            col[0] = new int[] {0};
        }
        for (int i = 1; i < d.length; i++) {
            M[i] = new Element[]{new Fraction(ring.numberONE(), d[i - 1].multiply(d[i], ring)).cancel(ring)};
            col[i] = new int[]{i};
        }
        for (int i = d.length; i < size; i++) {
            M[i] = new Element[0];
            col[i] = new int[0];
        }
        return new MatrixS(size, d.length, M, col);
    }
    protected MatrixS generateD(int size, Element mult){
        Ring ring = Ring.ringZxyz;        
        System.out.println("multiplier is "+mult);
        Element[][] M = new Element[size][];
        int[][] col = new int[size][];
        if (d.length > 0) {
            M[0] = new Element[] {new Fraction(mult, d[0]).cancel(ring)};
            col[0] = new int[] {0};
        }
        for (int i = 1; i < d.length; i++) {
            M[i] = new Element[]{new Fraction(mult, d[i - 1].multiply(d[i], ring)).cancel(ring)};
            col[i] = new int[]{i};
        }
        for (int i = d.length; i < size; i++) {
            M[i] = new Element[0];
            col[i] = new int[0];
        }
        return new MatrixS(size, d.length, M, col);
    }
    

}
