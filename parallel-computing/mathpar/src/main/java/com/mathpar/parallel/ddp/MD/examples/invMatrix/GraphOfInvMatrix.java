/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.MD.examples.invMatrix;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import com.mathpar.parallel.ddp.engine.AbstractGraphOfTask;
import com.mathpar.parallel.ddp.engine.Tools;
import com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix.DoubleMatrix;
import com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix.MultonMatrixTask;

public class GraphOfInvMatrix extends AbstractGraphOfTask{

    public GraphOfInvMatrix(){
        SetTotalVertex(8);
        SetTypesOfVertex(Tools.ArrayListCreator(new int []{1,0,0,0,1,0,0,0}));
        SetArcs(Tools.ArrayListCreator(new int [][]{{},{0},{0},{2},{3},{4},{1,4},{1,5}}));
    }

    @Override
    public void InitVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
        InvMatrixTask t=(InvMatrixTask)currentTask;
        switch (numb){
            case 0:
                InvMatrixTask v0=(InvMatrixTask)allVertex[numb];
                v0.A=t.A.Split(0);
                break;
            case 1:
                MultonMatrixTask v1=(MultonMatrixTask)allVertex[numb];
                v1.A=((InvMatrixTask)allVertex[0]).Result;
                v1.B=t.A.Split(2);
                break;
            case 2:
                MultonMatrixTask v2=(MultonMatrixTask)allVertex[numb];
                v2.A=t.A.Split(1);
                v2.B=((InvMatrixTask)allVertex[0]).Result;
                break;
            case 3:
                MultonMatrixTask v3=(MultonMatrixTask)allVertex[numb];
                v3.A=((MultonMatrixTask)allVertex[2]).C;
                v3.B=t.A.Split(2);
                break;
            case 4:
                InvMatrixTask v4=(InvMatrixTask)allVertex[numb];
                v4.A=t.A.Split(3).Add(((MultonMatrixTask)allVertex[3]).C);
                break;
            case 5:
                MultonMatrixTask v5=(MultonMatrixTask)allVertex[numb];
                v5.A=((InvMatrixTask)allVertex[4]).Result;
                v5.B=((MultonMatrixTask)allVertex[2]).C;;
                break;
            case 6:
                MultonMatrixTask v6=(MultonMatrixTask)allVertex[numb];
                v6.A=((MultonMatrixTask)allVertex[1]).C;
                v6.B=((InvMatrixTask)allVertex[4]).Result;
                break;
            case 7:
                MultonMatrixTask v7=(MultonMatrixTask)allVertex[numb];
                v7.A=((MultonMatrixTask)allVertex[1]).C;
                v7.B=((MultonMatrixTask)allVertex[5]).C;
                break;

        }
    }

    @Override
    public void FinalizeVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
        switch (numb){
            case 0:
                InvMatrixTask curV=(InvMatrixTask)allVertex[numb];
                curV.Result.SetMinus();
                break;
        }
    }

    @Override
    public void FinalizeGraph(AbstractTask currentTask, AbstractTask[] allVertex) {
        InvMatrixTask t=(InvMatrixTask)currentTask;
        DoubleMatrix A=((InvMatrixTask)allVertex[0]).Result;
        A.SetMinus();
        DoubleMatrix DQ=((InvMatrixTask)allVertex[4]).Result;
        DoubleMatrix W=((MultonMatrixTask)allVertex[5]).C;
        DoubleMatrix XZ=((MultonMatrixTask)allVertex[6]).C;
        DoubleMatrix XW=((MultonMatrixTask)allVertex[7]).C;
        t.Result.FillPart(0,XW.Add(A));
        t.Result.FillPart(1, W);
        t.Result.FillPart(2, XZ);
        t.Result.FillPart(3, DQ);
    }

}
