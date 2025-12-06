/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.MD.examples.invMatrix;

import com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix.MultonMatrixTask;
import com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix.GraphOfMultonTask;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import com.mathpar.parallel.ddp.engine.AbstractFactoryOfObjects;


/**
 *
 * @author r1d1
 */
public class Factory extends AbstractFactoryOfObjects{

    @Override
    public AbstractTask CreateTask(int type) {
        switch (type){
            case 0:
                return new MultonMatrixTask();
            case 1:
                return new InvMatrixTask();
            default:
                return null;
        }
    }

    @Override
    public void InitGraphs() {
        GraphOfMultonTask g=new GraphOfMultonTask();
        AddGraphOfTask(0, g);
        GraphOfInvMatrix invG=new GraphOfInvMatrix();
        AddGraphOfTask(1, invG);
    }

}
