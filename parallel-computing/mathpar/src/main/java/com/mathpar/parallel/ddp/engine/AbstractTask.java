
package com.mathpar.parallel.ddp.engine;

public abstract class AbstractTask{
    private int taskType;
    private boolean isCompletedFl;
    private boolean isTaskSendetFl;
    private int identInQueue;

    public int GetType(){
        return taskType;
    }
    public void SetType(int type){
        taskType=type;
    }

    public int GetIdent(){
        return identInQueue;
    }
    public void SetIdent(int id){
        identInQueue=id;
    }
    public synchronized void SetTaskCompleted(){
        isCompletedFl=true;
    }
    public synchronized boolean  IsTaskCompleted(){
        return isCompletedFl;
    }
    public synchronized boolean IsTaskSendet(){
        return isTaskSendetFl;
    }
    public synchronized void SetTaskSendet(){
        isTaskSendetFl=true;
    }

    public abstract void SetStartTask(String []args, Object[] data);

    public abstract boolean IsLittleTask();

    public abstract void ProcLittleTask();

    public abstract void SendTaskToNode(int node);

    public abstract void RecvTaskFromNode(int node);

    public abstract void SendResultToNode(int node);

    public abstract void GetResultFromNode(int node);

}

