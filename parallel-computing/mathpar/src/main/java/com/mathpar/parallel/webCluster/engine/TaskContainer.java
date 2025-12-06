/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author r1d1
 */


class TaskDescriptor implements Serializable{
    long lastUseTime;
    int taskState;
    String fileName;
}

public class TaskContainer {

    TreeMap<Integer, TreeMap<Integer,TaskDescriptor> > tasks;
    TreeMap<Integer, TreeMap<String, Long> > files;

    public TaskContainer() {
        tasks =new TreeMap<Integer, TreeMap<Integer,TaskDescriptor> >();
        files=new TreeMap<Integer, TreeMap<String, Long> >();
    }

    public synchronized void makeBackup(ObjectOutputStream oos){
        try {
            oos.writeObject(tasks);
            oos.writeObject(files);
        }
        catch(Exception e){
            Server.writeLog(e.toString());
        }
    }

    public synchronized void restoreFromBachup(ObjectInputStream ois){
        try{
            tasks=(TreeMap<Integer, TreeMap<Integer,TaskDescriptor> >)ois.readObject();
            files=(TreeMap<Integer, TreeMap<String, Long> >)ois.readObject();
        }catch (Exception e){
            Server.writeLog(e.toString());
        }
    }

    public synchronized boolean isUserFoldEmpty(int userID){
        TreeMap<String,Long> mp=files.get(userID);
        TreeMap<Integer,TaskDescriptor> mp2=tasks.get(userID);
        if (mp==null && mp2==null) return true;
        return false;
    }

    public synchronized ArrayList<ArrayList<Integer> > getAllTaskList(){
        ArrayList<ArrayList<Integer> > res=new ArrayList<ArrayList<Integer> >();
        for (Map.Entry e : tasks.entrySet()){
            Integer userID=new Integer((Integer)e.getKey());
            TreeMap<Integer,TaskDescriptor> curTasks=(TreeMap<Integer,TaskDescriptor>)e.getValue();
            for (Map.Entry t: curTasks.entrySet()){
                Integer taskID=new Integer((Integer)t.getKey());
                ArrayList<Integer> d=new ArrayList<Integer>();
                d.add(userID);
                d.add(taskID);
                res.add(d);
            }
        }
        return res;
    }

    public synchronized ArrayList<ArrayList<Object> > getAllFileList(){
        ArrayList<ArrayList<Object> > res=new ArrayList<ArrayList<Object> >();
        for (Map.Entry e : files.entrySet()){
            Integer userID=new Integer((Integer)e.getKey());
            TreeMap<String,Long> curFiles=(TreeMap<String,Long>)e.getValue();
            for (Map.Entry t: curFiles.entrySet()){
                String fileName=new String((String)t.getKey());
                ArrayList<Object> d=new ArrayList<Object>();
                d.add(userID);
                d.add(fileName);
                res.add(d);
            }
        }
        return res;
    }
    public synchronized void addFile(int userID, String fileName){
        TreeMap<String,Long> mp=files.get(userID);
        if (mp == null){
            mp=new TreeMap<String,Long>();
            files.put(userID,mp);
        }
        mp.put(fileName,System.currentTimeMillis());
    }

    public synchronized boolean isFileExist(int userID, String fileName){
        TreeMap<String,Long> mp=files.get(userID);
        if (mp!=null && mp.get(fileName)!=null){
            return true;
        }
        return false;
    }

    private synchronized void touchFile(int userID, String fileName){
        TreeMap<String,Long> mp=files.get(userID);
        if (mp!=null && mp.get(fileName)!=null){
            mp.put(fileName,System.currentTimeMillis());
        }
    }

    public synchronized int addTask(int userID, String fileName){
        TreeMap<Integer,TaskDescriptor> mp=tasks.get(userID);
        if (mp == null){
            mp=new TreeMap<Integer,TaskDescriptor>();
            tasks.put(userID,mp);
        }
        int taskNumb = 0;
        while (mp.get(taskNumb) != null) {
            taskNumb++;
        }
        TaskDescriptor tDesc=new TaskDescriptor();
        tDesc.lastUseTime=System.currentTimeMillis();
        tDesc.fileName=fileName;
        tDesc.taskState=AlgorithmsConfig.ST_IN_QUEUE;
        mp.put(taskNumb, tDesc);
        if (fileName!=null){
            touchFile(userID, fileName);
        }
        return taskNumb;
    }

    public synchronized void touchTask(int userID, int taskID){
        TreeMap<Integer,TaskDescriptor> mp=tasks.get(userID);
        if (mp!=null){
            TaskDescriptor tDesc=mp.get(taskID);
            if (tDesc!=null){
                tDesc.lastUseTime=System.currentTimeMillis();
                if (tDesc.fileName!=null){
                    touchFile(userID, tDesc.fileName);
                }
            }
        }
    }

    public synchronized int getStateForTask(int userID, int taskID){
        int state=AlgorithmsConfig.ST_NOT_EXIST;
        TreeMap<Integer,TaskDescriptor> mp=tasks.get(userID);
        if (mp!=null){
            TaskDescriptor desc=mp.get(taskID);
            if (desc!=null){
                state=desc.taskState;
            }
        }
        return state;
    }

    public synchronized void changeStateForTask(int userID, int taskID, int newState){
        TreeMap<Integer,TaskDescriptor> mp=tasks.get(userID);
        if (mp!=null){
            TaskDescriptor desc=mp.get(taskID);
            if (desc!=null){
                desc.taskState=newState;
            }
        }
    }

    public boolean CheckTaskForCrash(int userID, int taskID) {
        int state=getStateForTask(userID, taskID);
        if (state == AlgorithmsConfig.ST_RUNNING || state==AlgorithmsConfig.ST_IN_QUEUE) {
            String fileName = AlgorithmsConfig.getPathForUserFold(userID, taskID);
            fileName += File.separator + "err";
            try {
                FileInputStream fis = new FileInputStream(fileName);
                int fileSize = fis.available();
                if (fileSize > 0) {
                    changeStateForTask(userID, taskID, AlgorithmsConfig.ST_CRASH);
                    return true;
                }
            } catch (Exception e) {}
        }
        return false;
    }

    public synchronized String getTasksListForUser(int userID){
        String res="id:     state  description:\n";
        TreeMap<Integer,TaskDescriptor> tmp=tasks.get(userID);
        if (tmp == null) {
            res="No  tasks";
        }
        else {
            boolean flIsFirst=true;
            for (Map.Entry e: tmp.entrySet()){
                if (!flIsFirst){
                    res+="\n";
                }
                flIsFirst=false;
                Integer taskID=(Integer)e.getKey();
                Integer state=((TaskDescriptor)e.getValue()).taskState;
                if (state==AlgorithmsConfig.ST_RUNNING || state==AlgorithmsConfig.ST_IN_QUEUE)
                    CheckTaskForCrash(userID, taskID);
                state=((TaskDescriptor)e.getValue()).taskState;
                res+=String.valueOf(taskID)+":      "+AlgorithmsConfig.stateNames[state];
            }
        }
        return res;
    }

    public synchronized String getFileListForUser(int userID){
        String res="";
        TreeMap<String,Long> tmp=files.get(userID);
        if (tmp == null) {
            res="No  files";
        }
        else {
            boolean flIsFirst=true;
            for (Map.Entry e: tmp.entrySet()){
                if (!flIsFirst){
                    res+="\n";
                }
                flIsFirst=false;
                res+=e.getKey();
            }
        }
        return res;
    }

    //только удаление записей из таблицы. Удалять файлы должен вызывающий метод
    public synchronized boolean checkAndDeleteOldFile(int userID, String fileName){
        TreeMap<String,Long> mp=files.get(userID);
        if (mp==null){
            return false;
        }
        Long fTime=mp.get(fileName);
        if (fTime==null){
            return false;
        }
        long cur=System.currentTimeMillis();
        if (cur-fTime>AlgorithmsConfig.CNF_FILE_LIFETIME){
            mp.remove(fileName);
            if (mp.size()==0){
                files.remove(userID);
            }
            return true;
        }
        else return false;
    }

    //только удаление записей из таблицы. Удалять файлы должен вызывающий метод
     public synchronized boolean checkAndDeleteOldTask(int userID, int taskID){
        TreeMap<Integer,TaskDescriptor> mp=tasks.get(userID);
        if (mp==null){
            return false;
        }
        TaskDescriptor tDesc=mp.get(taskID);
        if (tDesc==null){
            return false;
        }
        long cur=System.currentTimeMillis();
        if (cur-tDesc.lastUseTime>AlgorithmsConfig.CNF_TASK_LIFETIME){
            mp.remove(taskID);
            if (mp.size()==0){
                tasks.remove(userID);
            }
            return true;
        }
        else return false;
    }
}
