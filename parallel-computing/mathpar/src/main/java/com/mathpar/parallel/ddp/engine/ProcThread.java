/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.ddp.engine;
import java.util.ArrayList;
import mpi.*;
/**
 *
 * @author r1d1
 */
public class ProcThread implements Runnable{
    Thread t;
    AbstractTask task;
    AbstractFactoryOfObjects factory;
    TaskQueue queue;
    volatile boolean flToExit;
    volatile boolean isRecvTask;

    public ProcThread(TaskQueue q, AbstractFactoryOfObjects f) {
        t=new Thread(this,"ProcThread");
        t.setPriority(1);
        flToExit=false;
        isRecvTask=false;
        queue=q;
        factory=f;
        t.start();
    }
    public void DoneThread(){
        flToExit=true;
    }
    public synchronized  void SetTask(AbstractTask tmpTask){
        task=tmpTask;
        isRecvTask=true;
    }
    private void ProcFunc(AbstractTask curTask){
        if (curTask.IsLittleTask()){
            curTask.ProcLittleTask();
            return;
        }
        AbstractGraphOfTask graph=factory.GetGraphOfTask(curTask.GetType());
        int totalVert=graph.GetTotalVertex();
        AbstractTask []daughtTasks=new AbstractTask[totalVert];
        int procVertex=0;
        boolean []isTaskInQueue=new boolean[totalVert];
        boolean []taskIsFinalized=new boolean[totalVert];
        while (procVertex<totalVert){
            procVertex=0;
            for (int i=0; i<totalVert; i++){
                if (isTaskInQueue[i] && !taskIsFinalized[i] && daughtTasks[i].IsTaskCompleted()){
                    graph.FinalizeVertex(i, curTask, daughtTasks);
                    taskIsFinalized[i]=true;
                }
            }
            ArrayList<Integer> availVertexInd=new ArrayList<Integer>();
            for (int i=0; i<totalVert; i++){
                if (daughtTasks[i]==null && graph.IsVertexAvail(i, daughtTasks))
                    availVertexInd.add(i);
            }
            if (availVertexInd.size()>0){
                for (int i=0; i<availVertexInd.size(); i++){
                    int curV=availVertexInd.get(i);
                    daughtTasks[curV]=factory.CreateTask(graph.GetTypeOfVertex(curV));
                    daughtTasks[curV].SetType(graph.GetTypeOfVertex(curV));
                    graph.InitVertex(curV, curTask, daughtTasks);
                }
                if (availVertexInd.size()>1){
                    for (int i=1; i<availVertexInd.size();i++){
                        int curV=availVertexInd.get(i);
                        queue.PushTaskInQueue(daughtTasks[curV]);
                        isTaskInQueue[curV]=true;
                    }
                }
                ProcFunc(daughtTasks[availVertexInd.get(0)]);
                graph.FinalizeVertex(availVertexInd.get(0), curTask, daughtTasks);
                daughtTasks[availVertexInd.get(0)].SetTaskCompleted();
            }
            else {
                for (int i=0; i<totalVert; i++){
                    if (daughtTasks[i]!=null && !daughtTasks[i].IsTaskCompleted() && isTaskInQueue[i]){
                        if (queue.TryReturnTaskFromQueue(daughtTasks[i])){
                            isTaskInQueue[i]=false;
                            ProcFunc(daughtTasks[i]);
                            graph.FinalizeVertex(i, curTask, daughtTasks);
                            daughtTasks[i].SetTaskCompleted();
                            break;
                        }
                    }
                }
            }
            for (int i=0; i<totalVert; i++){
                if (daughtTasks[i]!=null && daughtTasks[i].IsTaskCompleted())
                    procVertex++;
            }
        }
        graph.FinalizeGraph(curTask, daughtTasks);
    }
    public void run(){
        int myRank=0;
        try{
            myRank=MPI.COMM_WORLD.getRank();
        }catch (MPIException e){};
        while (!flToExit){
            if (!isRecvTask)
                continue;
            if (!task.IsTaskCompleted()){
                ProcFunc(task);
                task.SetTaskCompleted();
                isRecvTask=false;
            }
        }
    }
}
