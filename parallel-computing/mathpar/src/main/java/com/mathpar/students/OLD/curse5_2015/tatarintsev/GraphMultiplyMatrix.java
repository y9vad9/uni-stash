package com.mathpar.students.OLD.curse5_2015.tatarintsev;

import com.mathpar.parallel.ddp.engine.AbstractGraphOfTask;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.ArrayList;
import com.mathpar.matrix.*;
import com.mathpar.number.*;


class Blocks {//misc info for block multon

    static int[][] blocks = {{0, 0},
        {1, 2},
        {0, 1},
        {1, 3},
        {2, 0},
        {3, 2},
        {2, 1},
        {3, 3}};
}

public class GraphMultiplyMatrix extends AbstractGraphOfTask {

    public GraphMultiplyMatrix() {
        ArrayList<Integer> types = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> arc = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 8; i++) {
            types.add(0);
            arc.add(new ArrayList<Integer>());
        }
        SetTotalVertex(8);
        SetTypesOfVertex(types);
        SetArcs(arc);
    }

    public void InitVertex(int numb, AbstractTask parentTask, AbstractTask[] allVertex) {
        TaskMultiplyMatrix parent = (TaskMultiplyMatrix) parentTask;
        TaskMultiplyMatrix curT = (TaskMultiplyMatrix) allVertex[numb];
        if(parent.ab==null){
            parent.ab = split(parent.a);
            parent.bb = split(parent.b);
        }
        curT.a = parent.ab[Blocks.blocks[numb][0]];
        curT.b = parent.bb[Blocks.blocks[numb][1]];
    }

    @Override
    public void FinalizeVertex(int numb, AbstractTask parentTask, AbstractTask[] allVertex) {
    }

    @Override
       public void FinalizeGraph(AbstractTask parentTask, AbstractTask[] allVertex) {
        TaskMultiplyMatrix parent=(TaskMultiplyMatrix)parentTask;
        MatrixD[] m = new MatrixD[4];
        for (int i=0; i<8; i+=2){
            TaskMultiplyMatrix curT1=((TaskMultiplyMatrix)allVertex[i]);
            TaskMultiplyMatrix curT2=(TaskMultiplyMatrix)allVertex[i+1];
            m[i/2]=curT1.c.add(curT2.c, Main.ring);

       }
        parent.c= MatrixD.join(m);
        //System.out.println("m = "+Array.toString(m));
    }


    static MatrixD[] split(MatrixD m) {
        MatrixD[] result = new MatrixD[4];
        int size = m.M.length/2;
        
        for(int i=0; i<result.length; i++) {
            result[i] = new MatrixD(new Element[size][size]);
        }
        
        for(int i=0; i<m.M.length; i++) {
            for(int j=0; j<m.M.length; j++) {
                if( (i<size )&&(j<size) ) {
                    result[0].M[i][j] = m.M[i][j];
                } else if( (i<size )&&(j>=size) ) {
                    result[1].M[i][j - size] = m.M[i][j];
                } else if( (i>=size )&&(j<size) ) {
                    result[2].M[i - size][j] = m.M[i][j];
                } else {
                    result[3].M[i - size][j - size] = m.M[i][j];
                }
            }
        }
        
        
        return result;
    }



}
