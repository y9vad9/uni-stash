/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.algorithms.multPolynom;

import com.mathpar.parallel.ddp.engine.AbstractGraphOfTask;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.ArrayList;
import com.mathpar.matrix.*;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;


class Blocks {//misc info for block multon

    static int[][] blocks = {{0, 0},
                             {0, 1},
                             {1, 0},
                             {1, 1}};        
}

public class GraphMultiplyPolynom extends AbstractGraphOfTask {

    public GraphMultiplyPolynom() {
        ArrayList<Integer> types = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> arc = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 4; i++) {
            types.add(0);
            arc.add(new ArrayList<Integer>());
        }
        SetTotalVertex(4);
        SetTypesOfVertex(types);
        SetArcs(arc);
    }

    public void InitVertex(int numb, AbstractTask parentTask, AbstractTask[] allVertex) {
        TaskMultiplyPolynom parent = (TaskMultiplyPolynom) parentTask;
        TaskMultiplyPolynom curT = (TaskMultiplyPolynom) allVertex[numb];       
        if(parent.ab==null){
            parent.ab=new Polynom[]{parent.a.subPolynom(0,parent.a.coeffs.length/2),parent.a.subPolynom(parent.a.coeffs.length/2,parent.a.coeffs.length)};
            parent.bb=new Polynom[]{parent.b.subPolynom(0,parent.b.coeffs.length/2),parent.b.subPolynom(parent.b.coeffs.length/2,parent.b.coeffs.length)};               
        }
        curT.a = parent.ab[Blocks.blocks[numb][0]];
        curT.b = parent.bb[Blocks.blocks[numb][1]];
    }

    @Override
    public void FinalizeVertex(int numb, AbstractTask parentTask, AbstractTask[] allVertex) {
    }

    @Override
       public void FinalizeGraph(AbstractTask parentTask, AbstractTask[] allVertex) {
        TaskMultiplyPolynom parent=(TaskMultiplyPolynom)parentTask;               
        parent.c=((TaskMultiplyPolynom) allVertex[0]).c;
        for (int i=1; i<4; i++){
            parent.c=parent.c.add(((TaskMultiplyPolynom) allVertex[i]).c,Ring.ringZxyz);
        }
        
    }
}
