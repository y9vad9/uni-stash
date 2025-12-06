package com.mathpar.parallel.webCluster.engine;

import java.io.Serializable;

/**
 *
 * @author r1d1
 * <br><br>
 * Класс, который инкапсулирует настройки запуска задачи на кластере.

 */
public class TaskConfig implements Serializable{
    private int nodes;
    private int procPerNode;
    private int wall_time;
    private int algo_numb;
    private int maxMemory;
    
    public TaskConfig(){
        nodes=AlgorithmsConfig.CNF_MAX_TOTAL_NODES/2;
        procPerNode=1;
        wall_time=2;
    }
    /**
     *
     * @param nd количество запрашиваемых компьютеров
     * @param coresPerN количество ядер на каждом узле
     * @param wallTime верхнее ограничение времени счета задачи в минутах
     * @param algoN тип алгоритма
     */
    public TaskConfig(int nd, int procPerN,int wallTime, int algoN, int mem){
        algo_numb=algoN;
        nodes=nd;
        procPerNode=procPerN;
        wall_time=wallTime;
        maxMemory=mem;
    }
    public String GetWallTime(){
        String h=String.valueOf(wall_time/60);
        if (h.length()==1)
            h="0"+h;
        String m=String.valueOf(wall_time%60);
        if (m.length()==1)
            m="0"+m;
        return h+":"+m+":00";
    }
    public int GetNodes(){
        return nodes;
    }
    public String GetNodesS(){
        return String.valueOf(nodes);
    }
    public int GetNProc(){
        return procPerNode;
    }
    
    public int getMem(){
        return maxMemory;
    }
    public int getAlgoNumb(){
        return algo_numb;
    }
    public String GetNprocS(){
        return String.valueOf(procPerNode);
    }
    public int check(){
        if (nodes<1 || nodes>AlgorithmsConfig.CNF_MAX_TOTAL_NODES)
            return AlgorithmsConfig.RES_INCORRECT_NUMBER_OF_NODES;
        if (procPerNode<1 || procPerNode>AlgorithmsConfig.CNF_MAX_PROCESSES_ON_NODE)
            return AlgorithmsConfig.RES_INCORRECT_NUMBER_PROC_PER_NODE;
        if (wall_time<1 || wall_time>AlgorithmsConfig.CNF_MAX_WALL_TIME)
            return AlgorithmsConfig.RES_INCORRECT_WALL_TIME;
        if (maxMemory<1 || maxMemory>AlgorithmsConfig.CNF_MAX_MEMORY)
            return AlgorithmsConfig.RES_INCORRECT_MEMORY_SETTINGS;
        return AlgorithmsConfig.RES_SUCCESS;
    }
  
    
}
