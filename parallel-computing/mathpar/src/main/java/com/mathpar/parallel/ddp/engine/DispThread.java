package com.mathpar.parallel.ddp.engine;
import java.lang.*;

import mpi.*;
import java.util.TreeSet;

import java.util.ArrayList;

public class DispThread {
    AbstractFactoryOfObjects factory;
    AbstractTask task;
    TaskQueue qTask;
    int maxDaughter;
    long sleepTime;
    long executeTime;
    AbstractTask startTask;
    //конструктор

    public AbstractTask GetStartTask(){
        return startTask;
    }
    
    /**
     * 
     * @param startType номер типа стартовой задачи
     * @param f фабрика объектов (наследник класса AbstractFactoryOfObjects
     * @param maxD максимальное количество дочерних узлов, которое будет создаваться в процессе работы алгоритма
     * @param sTime время, на прерывается диспетчерский поток
     * @param args аргументы командной строки
     * @param data произвольные данные, которые будут переданы в метод AbstractTask.setStartTask
     */
    public DispThread(int startType,AbstractFactoryOfObjects f,int maxD,long sTime,String []args, Object[] data){
        factory=f;
        maxDaughter=maxD;
        sleepTime=sTime;
        executeTime=0;
        try{
            int all=MPI.COMM_WORLD.getSize();
            int []nodes=new int[all];
            for (int i=0; i<all; i++){
                nodes[i]=i;
            }
            execute(startType,args,data,nodes);
        }catch(Exception e){}

    }
    
    /**
     * 
     * @param startType номер типа стартовой задачи
     * @param f фабрика объектов (наследник класса AbstractFactoryOfObjects
     * @param maxD максимальное количество дочерних узлов, которое будет создаваться в процессе работы алгоритма
     * @param sTime время, на прерывается диспетчерский поток
     * @param args аргументы командной строки
     * @param data произвольные данные, которые будут переданы в метод AbstractTask.setStartTask
     * @param nodes массив номеров процессоров, которые должны использоваться в процессе работы алгоритма. Если 
     * этот параметр не указан, будут использованы все доступные процессоры
     */
    public DispThread(int startType,AbstractFactoryOfObjects f,int maxD,long sTime,String []args, Object[] data, int []nodes){
        factory=f;
        maxDaughter=maxD;
        sleepTime=sTime;
        executeTime=0;
        try{
            TreeSet<Integer> numbs=new TreeSet<Integer>();
            if (nodes.length==0){                
                System.out.println("Wrong nodes numbs.");
                return;
            }
            for (int i=0; i<nodes.length; i++){
                if (nodes[i]<0 || nodes[i]>=MPI.COMM_WORLD.getSize() || numbs.contains(nodes[i])){
                    System.out.println("Wrong nodes numbs.");
                    return;
                }
                numbs.add(nodes[i]);
            }
            execute(startType,args,data,nodes);
        }catch(Exception e){}

    }

    public long GetExecuteTime(){
        return executeTime;
    }
    //начать выполнение алгоритма
    /**
     * 
     * @param startType номер типа стартовой задачи 
     * @param args аргументы
     * @param data данные
     * @throws mpi.MPIException
     * @throws InterruptedException 
     */
    
    
    
    private void execute(int startType,String []args,Object[] data,int []nodes) throws mpi.MPIException,InterruptedException{        
        int myRank=MPI.COMM_WORLD.getRank();
        int cntProc=nodes.length;

        factory.InitGraphs();
        qTask=new TaskQueue();

        Thread disp=Thread.currentThread();
        disp.setPriority(10);
        disp.setName("disp");
        ProcThread counter=new ProcThread(qTask,factory);
        int mode=0;
        //0 - ожидание задачи, 1 - выполнение задачи, -1 завершение всей программы
        //2 - ожидание завершения текущей задачи
        TreeSet<Integer> freeProcs=new TreeSet<Integer>();
        TreeSet<Integer> dNodes=new TreeSet<Integer>();
        int maxProcNumb=-1;
        for( int i=0; i<cntProc; i++){
            maxProcNumb=Math.max(maxProcNumb, nodes[i]);
        }
        AbstractTask []dTasks=new AbstractTask[maxProcNumb+1];
        //массив содержит номер последней отправленной вершины
        //-2 - не было отправлено ни одной вершины
        
        int []lastFreeNode=new int [maxProcNumb+1];
        int Parent=0;
        //в этом массиве true у процессов, от которых был получен запрос о
        //завершении. Им не будем отсылать свободные узлы.
        boolean []requestToDone=new boolean[maxProcNumb+1];
        //флаг подтверждения о завершении.
        int flAgree=-1;
        int rootNumb=nodes[0];
        if (myRank==rootNumb){
            Parent=-1;
            for (int i=1; i<nodes.length; i++)
                freeProcs.add(nodes[i]);
            task=factory.CreateTask(startType);
            task.SetType(startType);            
            task.SetStartTask(args,data);            
            startTask=task;
            mode=1;
            counter.SetTask(task);
        }
        executeTime=System.currentTimeMillis();
        if (myRank==rootNumb){
            System.out.println("DDP start with total nodes="+cntProc);
            System.out.println("maxD = "+maxDaughter);
            System.out.println("sleep time = "+sleepTime);
        }



        while (mode!=-1){
            // теги:
             //     0: сообщение содержит задачу
             //     1: сообщение содержит свободные узлы
             //     2: сообщение содержит состояние задачи
             //     3: сообщение содержит запрос на завершение
             //     4: сообщение содержит одну из последних отосланных вершин
             //     5: сообщение содержит команду на завершение (вся задача посчитана)
            if (mode==0){
                //режим ожидания задачи
                Status taskInfo=MPI.COMM_WORLD.probe(MPI.ANY_SOURCE,MPI.ANY_TAG);
                int tag=taskInfo.getTag();
                Parent=taskInfo.getSource();
                int []tmpAr=new int[1];
                MPI.COMM_WORLD.recv(tmpAr,1,MPI.INT,Parent,tag);
                if (tag==5){
                    mode=-1;
                    counter.DoneThread();
                }
                else {
                 //   System.out.println("proc "+myRank+" get task with type "+tmpAr[0]);
                    task=factory.CreateTask(tmpAr[0]);
                    task.SetType(tmpAr[0]);
                    task.RecvTaskFromNode(Parent);
                    counter.SetTask(task);
                    mode=1;
                }
            }
            if (mode==2){               
                //режим завершения работы узла (текущего)
                //если пришло сообщение со своб узлами (от предка),
                //добавим их в множ. своб:
                Status st=MPI.COMM_WORLD.iProbe(Parent,1);
                if (st!=null){
                    int cnt=st.getCount(MPI.INT);
                    int []freeProcsAr=new int[cnt];
                    MPI.COMM_WORLD.recv(freeProcsAr,cnt,MPI.INT,Parent,1);
                    for (int i=0; i<cnt; i++){
                        freeProcs.add(freeProcsAr[i]);
                    }
                }
                //если был получен номер последней своб. вершины:
                if (flAgree!=-1){
                    //если в своб вершинах присутствует последняя отравленная предком:
                    //либо не отправлялись сообщения со своб. вершинами
                    if (freeProcs.contains(flAgree)||flAgree==-2){
                        //отошлем родителю все свободные вершины, перейдем в сост. ожидания задачи
                        //к отсылаемым добавим свой номер
                        Object []tmpFree=freeProcs.toArray();
                        int []tmpFreeAr=new int [tmpFree.length+1];
                        for (int i=0; i<tmpFree.length; i++)
                            tmpFreeAr[i]=(Integer)tmpFree[i];
                        tmpFreeAr[tmpFree.length]=myRank;
                        MPI.COMM_WORLD.send(tmpFreeAr,tmpFreeAr.length, MPI.INT, Parent, 1);
                        task.SendResultToNode(Parent);
                        //System.out.println("result sendet from "+myRank);
                        mode=0;
                        flAgree=-1;
                        freeProcs.clear();
                        dNodes.clear();
                    }
                }
                else {
                    //иначе ждем номер последней отосл вершины
                    Status existFree=MPI.COMM_WORLD.iProbe(Parent,4);
                    if (existFree!=null){
                        int []tmpLastFree=new int [1];
                        MPI.COMM_WORLD.recv(tmpLastFree,1,MPI.INT,Parent,4);
                        flAgree=tmpLastFree[0];
                    }
                }
            }
            if (mode==1){
                if (myRank!=rootNumb){
                     //получение свободных процессов от род. узла:
                    Status st=MPI.COMM_WORLD.iProbe(Parent,1);
                    if (st!=null){
                       //получено сообщение от род. узла со своб. процессами
                       int cnt=st.getCount(MPI.INT);
                       int []freeProcsAr=new int[cnt];
                       MPI.COMM_WORLD.recv(freeProcsAr,cnt,MPI.INT,Parent,1);
                       for (int i=0; i<cnt; i++){
                            freeProcs.add(freeProcsAr[i]);
                       }
                    }
                }
                //все дочерние узлы
                Object []daughtersAll=dNodes.toArray();
                //опрос дочерних узлов о завершенности работы:
                for (int i=0; i<daughtersAll.length; i++){
                    if (requestToDone[(Integer)daughtersAll[i]])
                        continue;
                    int []tmpReq=new int[1];
                    Status stReq=MPI.COMM_WORLD.iProbe((Integer)daughtersAll[i],3);
                    if (stReq!=null){
                        MPI.COMM_WORLD.recv(tmpReq,1,MPI.INT,(Integer)daughtersAll[i],3);
                        tmpReq[0]=lastFreeNode[(Integer)daughtersAll[i]];
                        requestToDone[(Integer)daughtersAll[i]]=true;
                        //если был запрос на завершение, отправим номер последней
                        //отправленной вершины:
                        MPI.COMM_WORLD.send(tmpReq,1,MPI.INT,(Integer)daughtersAll[i],4);
                    }
                }
                //в этом векторе будем хранить дочерние узлы, от которых не получен
                //запрос на завершение
                ArrayList<Integer> daughtersWork=new ArrayList<Integer>();
                //а в этом храним дочерние узлы, от которых получен запрос на
                //завершение и от которых мы ждем массив со свободными вершинами
                ArrayList<Integer> daughtersFree=new ArrayList<Integer>();
                for (int i=0; i<daughtersAll.length; i++){
                    if (!requestToDone[(Integer)daughtersAll[i]])
                        daughtersWork.add((Integer)daughtersAll[i]);
                    else daughtersFree.add((Integer)daughtersAll[i]);
                }
                //проверим пришло ли сообщение со своб.верш. от доч. узлов
                //проверять будем только ожидающих завершение
                //те, от которых пришло, переместим из дочерних в свободные
                //также в свободные пойдут вершины из полученного сообщения
                for (int i=0; i<daughtersFree.size(); i++){
                    int curD=daughtersFree.get(i);
                    Status stReq=MPI.COMM_WORLD.iProbe(curD,1);
                    if (stReq!=null){
                        int cnt=stReq.getCount(MPI.INT);
                        int []freeProcsAr=new int[cnt];
                        MPI.COMM_WORLD.recv(freeProcsAr,cnt,MPI.INT,curD,1);
                        for (int j=0; j<cnt; j++){
                            freeProcs.add(freeProcsAr[j]);                     
                        }
                        dNodes.remove(curD);
                        requestToDone[curD]=false;
                        dTasks[curD].GetResultFromNode(curD);
                        dTasks[curD].SetTaskCompleted();
                    }
                }

                //если число дочерних узлов меньше maxDaughter, создадим новые
                //отправив им задачи из очереди:
                if (dNodes.size()<maxDaughter){
                    int needT=java.lang.Math.min(freeProcs.size(),maxDaughter-dNodes.size());
                    ArrayList<AbstractTask> qTasks=qTask.RemoveTasksForSending(needT);
                    Object []freeNodesAr=freeProcs.toArray();
                    for (int i=0; i<qTasks.size(); i++){
                        Integer curNode=(Integer)freeNodesAr[i];
                        dTasks[curNode]=qTasks.get(i);
                        lastFreeNode[curNode]=-2;
                        freeProcs.remove(curNode);
                        dNodes.add(curNode);
                        int []tmpS={qTasks.get(i).GetType()};
                        MPI.COMM_WORLD.send(tmpS,1,MPI.INT,curNode,0);
                        qTasks.get(i).SendTaskToNode(curNode);
                    }
                }


                //отправим дочерним узлам все имеющиеся своб. процессы
                if (daughtersWork.size()>0 && freeProcs.size()>0){
                    Object []freeNodesAr=freeProcs.toArray();
                    int cntFreeOnD=(freeNodesAr.length+daughtersWork.size()-1)/daughtersWork.size();
                    int curDInd=0;
                    for (int i=0; i<freeNodesAr.length; i+=cntFreeOnD){
                        int []tmpFreeToSend=new int[cntFreeOnD];
                        int cntFreeToSend=0;
                        for (int j=i; j<i+cntFreeOnD && j<freeNodesAr.length; j++)
                            tmpFreeToSend[cntFreeToSend++]=(Integer)freeNodesAr[j];
                        MPI.COMM_WORLD.send(tmpFreeToSend,cntFreeToSend,MPI.INT,daughtersWork.get(curDInd),1);
                        lastFreeNode[daughtersWork.get(curDInd)]=tmpFreeToSend[cntFreeToSend-1];
                        curDInd++;
                    }
                    freeProcs.clear();
                }




                //задача посчитана - перейти в режим 2
                if (task.IsTaskCompleted()){
                    //System.out.println("proc "+myRank+" completed");
                    if (myRank==rootNumb){
                        //задача посчитана в корневом узле - завершение работы
                        int []tmpAr={24};
                        for (int i=1; i<cntProc; i++){
                            MPI.COMM_WORLD.send(tmpAr,1,MPI.INT,nodes[i],5);
                        }
                        mode=-1;
                        counter.DoneThread();
                    }
                    else {
                        //иначе отправим родителю запрос на завершение
                        int []tmpInt=new int[1];
                        MPI.COMM_WORLD.send(tmpInt,1,MPI.INT,Parent,3);
                        mode=2;
                    }
                }

            }
            //периодичность диспетчера
            disp.sleep(sleepTime);
        }
        counter.t.join();
        if (myRank==rootNumb)
            System.out.println("DDP done.");
        executeTime=System.currentTimeMillis()-executeTime;
    }

}

