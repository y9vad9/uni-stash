
package com.mathpar.parallel.ddp.engine;

import java.util.ArrayList;

abstract public class AbstractFactoryOfObjects {
    private ArrayList<AbstractGraphOfTask> graphsArray;
    private ArrayList<Integer> typeNumbs;

    public void AddGraphOfTask(int type, AbstractGraphOfTask g){
        if (graphsArray==null){
            graphsArray=new ArrayList<AbstractGraphOfTask>();
        }
        if (typeNumbs==null){
            typeNumbs=new ArrayList<Integer>();
        }
        graphsArray.add(g);
        typeNumbs.add(type);
        int curInd=graphsArray.size()-1;
        while (curInd>0 && typeNumbs.get(curInd)<typeNumbs.get(curInd-1)){
            AbstractGraphOfTask tmp=graphsArray.get(curInd-1);
            graphsArray.set(curInd-1, graphsArray.get(curInd));
            graphsArray.set(curInd,tmp);
            int tmp2=typeNumbs.get(curInd-1);
            typeNumbs.set(curInd-1,typeNumbs.get(curInd));
            typeNumbs.set(curInd,tmp2);
        }
        g.SetFactory(this);
    }

    public AbstractGraphOfTask GetGraphOfTask(int type){
        return graphsArray.get(type);
    }

    //методы, которые необходимо реализовать:
    public abstract AbstractTask CreateTask(int type);

    public abstract void InitGraphs();
}
