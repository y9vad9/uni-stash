
package com.mathpar.parallel.ddp.P.karatsubaMult;

import com.mathpar.parallel.ddp.engine.AbstractFactoryOfObjects;
import com.mathpar.parallel.ddp.engine.AbstractTask;


public class KaratsubaFactory extends AbstractFactoryOfObjects{

    @Override
    public AbstractTask CreateTask(int type) {
        switch (type){
            default: return new KaratsubaT();
        }
    }

    @Override
    public void InitGraphs() {
        KaratsubaGraph g0=new KaratsubaGraph();
        AddGraphOfTask(0, g0);
    }


}
