/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.MD.examples.multiplyMatrix;

import com.mathpar.parallel.ddp.engine.AbstractGraphOfTask;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.ArrayList;
import com.mathpar.matrix.*;
import com.mathpar.number.Ring;


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
            parent.ab = parent.a.split();
            parent.bb = parent.b.split();
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
        MatrixS[] m = new MatrixS[4];
        for (int i=0; i<8; i+=2){
            TaskMultiplyMatrix curT1=((TaskMultiplyMatrix)allVertex[i]);
            TaskMultiplyMatrix curT2=(TaskMultiplyMatrix)allVertex[i+1];
            m[i/2]=curT1.c.add(curT2.c,Ring.ringZxyz);

       }
        parent.c= MatrixS.join(m);
        //System.out.println("m = "+Array.toString(m));
    }
}
