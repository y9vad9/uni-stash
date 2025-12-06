/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import com.mathpar.parallel.ddp.engine.AbstractGraphOfTask;
import com.mathpar.parallel.ddp.engine.Tools;

class IFBM{//misc info for block multon
    static int [][]blocks={{0,0},
                           {2,1},
                           {1,0},
                           {3,1},
                           {0,2},
                           {2,3},
                           {1,2},
                           {3,3}};
}


public class GraphOfMultonTask extends AbstractGraphOfTask{

    public GraphOfMultonTask(){
        SetTotalVertex(8);
        SetTypesOfVertex(Tools.ArrayListCreator(new int[]{0,0,0,0,0,0,0,0}));
        SetArcs(Tools.ArrayListCreator(new int[][]{{},{},{},{},{},{},{},{}}));
    }
    @Override
    public void InitVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
        MultonMatrixTask parent=(MultonMatrixTask)currentTask;
        MultonMatrixTask curT=(MultonMatrixTask)allVertex[numb];
        curT.A=parent.A.Split(IFBM.blocks[numb][0]);
        curT.B=parent.B.Split(IFBM.blocks[numb][1]);
    }

    @Override
    public void FinalizeVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {

    }

    @Override
    public void FinalizeGraph(AbstractTask currentTask, AbstractTask[] allVertex) {
        MultonMatrixTask parent=(MultonMatrixTask)currentTask;
        for (int i=0; i<8; i+=2){
            MultonMatrixTask curT1=(MultonMatrixTask)allVertex[i];
            MultonMatrixTask curT2=(MultonMatrixTask)allVertex[i+1];
            parent.C.FillPart(i/2,curT1.C.Add(curT2.C));
        }
    }




}
