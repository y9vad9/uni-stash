
package com.mathpar.parallel.ddp.engine;

import java.util.ArrayList;



abstract public class AbstractGraphOfTask {
    private ArrayList<ArrayList<Integer> > arcs;
    private int totalVertex;
    private ArrayList<Integer> typesOfVertex;
    private AbstractFactoryOfObjects factory;

    public void SetTypesOfVertex(ArrayList<Integer> v){
        typesOfVertex=v;
    }

    public void SetArcs(ArrayList<ArrayList<Integer> > a){
        arcs=a;
    }
    public void SetFactory(AbstractFactoryOfObjects  f){
        factory=f;
    }
    public AbstractFactoryOfObjects GetFactory(){
        return factory;
    }
    public void SetTotalVertex(int cnt){
        totalVertex=cnt;
    }

    public int GetTotalVertex(){
        return totalVertex;
    }

    public int GetTypeOfVertex(int numb){
        return typesOfVertex.get(numb);
    }
    public boolean IsVertexAvail(int numb,AbstractTask []allVertex){
        int need=arcs.get(numb).size();
        int s=0;
        for (int i=0; i<need; i++){
            int curV=arcs.get(numb).get(i);
            if (allVertex[curV]!=null && allVertex[curV].IsTaskCompleted())
                s++;
        }
        return (s==need);
    }

    abstract public void InitVertex(int numb, AbstractTask currentTask,AbstractTask []allVertex);

    abstract public void FinalizeVertex(int numb, AbstractTask currentTask,AbstractTask []allVertex);

    abstract public void FinalizeGraph(AbstractTask currentTask,AbstractTask []allVertex);
}
