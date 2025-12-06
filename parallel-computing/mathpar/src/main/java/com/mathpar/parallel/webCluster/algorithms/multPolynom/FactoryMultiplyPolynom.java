
package com.mathpar.parallel.webCluster.algorithms.multPolynom;

import com.mathpar.parallel.ddp.engine.AbstractFactoryOfObjects;
import com.mathpar.parallel.ddp.engine.AbstractTask;


public class FactoryMultiplyPolynom extends AbstractFactoryOfObjects{

    @Override
    public AbstractTask CreateTask(int type) {
        switch (type){
            default: return new TaskMultiplyPolynom();
        }
    }

    @Override
    public void InitGraphs() {
        GraphMultiplyPolynom g0=new GraphMultiplyPolynom();
        AddGraphOfTask(0, g0);
    }


}
