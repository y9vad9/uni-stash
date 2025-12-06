package com.mathpar.parallel.webCluster.engine;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author r1d1
 */
public class TrashRemover extends Thread{

    public TrashRemover() {
        setPriority(MIN_PRIORITY);
        start();
    }

    public void run(){
        while (true){
            try{
                sleep(AlgorithmsConfig.CNF_TRASH_REMOVING_PERIOD);
            }catch (Exception e){
                Server.writeLog(e.toString());
            }
            //удаление файлов - загруженных zip архивов с классами
            ArrayList<ArrayList<Object> > allFiles=Server.taskCont.getAllFileList();
            for (int i=0; i<allFiles.size(); i++){
                Integer userID=(Integer)allFiles.get(i).get(0);
                String fileName=(String)allFiles.get(i).get(1);
                boolean delRes=Server.taskCont.checkAndDeleteOldFile(userID, fileName);
                if (delRes){
                    File f=new File(AlgorithmsConfig.getPathForUserFold(userID)+File.separator+fileName);
                    try {
                        f.delete();
                    }catch (Exception e){
                        Server.writeLog(e.toString());
                    }
                    if (Server.taskCont.isUserFoldEmpty(userID)){
                        File dir=new File(AlgorithmsConfig.getPathForUserFold(userID));
                        try{
                            dir.delete();
                        }catch (Exception e){
                            Server.writeLog(e.toString());
                        }
                    }
                }
            }

            //удаление файлов, созданных при выполнении задачи (err,out,data.ser,result.ser)

            ArrayList<ArrayList<Integer> > allTasks=Server.taskCont.getAllTaskList();
            for (int i=0; i<allTasks.size(); i++){
                Integer userID=(Integer)allTasks.get(i).get(0);
                Integer taskID=(Integer)allTasks.get(i).get(1);
                boolean delRes=Server.taskCont.checkAndDeleteOldTask(userID, taskID);
                if (delRes){
                    String []fileNames={"run","err","out","data.ser","result.ser"};                    
                    for (int j=0; j<fileNames.length; j++){
                        File f=new File(AlgorithmsConfig.getPathForUserFold(userID,taskID)+File.separator+fileNames[j]);
                        try {
                            f.delete();
                        }catch (Exception e){
                            Server.writeLog(e.toString());
                        }
                    }
                    File f=new File(AlgorithmsConfig.getPathForUserFold(userID,taskID));
                    try {
                        f.delete();
                    }catch (Exception e){
                        Server.writeLog(e.toString());
                    }

                    if (Server.taskCont.isUserFoldEmpty(userID)){
                        File dir=new File(AlgorithmsConfig.getPathForUserFold(userID));
                        try{
                            dir.delete();
                        }catch (Exception e){
                            Server.writeLog(e.toString());
                        }
                    }
                }
            }
        }
    }

}
