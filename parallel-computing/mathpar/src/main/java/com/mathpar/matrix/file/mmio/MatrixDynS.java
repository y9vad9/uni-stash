
package com.mathpar.matrix.file.mmio;

import java.util.*;
import com.mathpar.number.*;
import com.mathpar.matrix.*;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author Yuri Valeev
 * @version 1.0
 */
public class MatrixDynS {
    private IntList[] col;
    private ArrayList<Element>[] M;


    public MatrixDynS(int rows){
        col=new IntList[rows];
        M=new ArrayList[rows];
    }


    /**
     * Добавить строчку в MatrixDynS.
     * i=line.i-1, j=line.j-1, т.к. в файле нумерация с 1, а в Java -- с 0.
     * @param line CoordLine
     */
    public void addLine(CoordLine line){
        int i=line.i-1;
        int j=line.j-1;
        Element el=line.el;
        //j-->col[i], el-->M[i]
        if (col[i]==null) {
            //если нет строки, то создать ее
            col[i]=new IntList();
            M[i]=new ArrayList<Element>();
        }
        col[i].add(j);
        M[i].add(el);
    }

    public MatrixS toMatrixS(){
        int m=col.length;
        //col-->col2
        int[] emptyIarr=new int[0];
        int[][] col2=new int[m][];
        for (int i = 0; i < m; i++) {
            if (col[i]!=null) {
                col2[i]=col[i].toArray();
            } else {
                col2[i]=emptyIarr;
            }
        }
        //M-->M2
        Element[] emptySarr=new Element[0];
        Element[][] M2=new Element[m][];
        for (int i = 0; i < m; i++) {
            if (M[i] != null) {
                M2[i]=M[i].toArray(new Element[0]);
            } else {
                M2[i]=emptySarr;
            }
        }
        return new MatrixS(M2,col2);
    }
}
