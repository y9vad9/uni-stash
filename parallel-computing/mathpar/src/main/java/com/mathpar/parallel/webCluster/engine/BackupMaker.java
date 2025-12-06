/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.webCluster.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author r1d1
 */
public class BackupMaker extends Thread {

    public BackupMaker() {
        setPriority(MIN_PRIORITY);
        start();
    }

    public static void restoreFromBackup(){
        String backupName=AlgorithmsConfig.CNF_DATA_PATH+File.separator+"backup.data";
        String tmpName=AlgorithmsConfig.CNF_DATA_PATH+File.separator+"tmp.data";
        File tmpBackup=new File(tmpName);
        if (tmpBackup.exists()){
            try{
            ObjectInputStream ios=new ObjectInputStream(new FileInputStream(tmpBackup));
            Server.taskCont.restoreFromBachup(ios);
            ios.close();
            }catch (Exception e){
                Server.writeLog(e.toString());
            }
            return;
        }
        File backup=new File(backupName);
        if (backup.exists()){
            try{
            ObjectInputStream ios=new ObjectInputStream(new FileInputStream(backup));
            Server.taskCont.restoreFromBachup(ios);
            ios.close();
            }catch (Exception e){
                Server.writeLog(e.toString());
            }
            return;
        }
        Server.writeLog("WARNING!! BACKUP NOT FOUND. IF IT NOT FIRST LAUNCH, THIS IS BAD...");
    }

    public void run(){
        while (true){
            try{
                sleep(AlgorithmsConfig.CNF_BACKUP_MAKING_PERIOD);
            }catch (Exception e){
                Server.writeLog(e.toString());
            }
            String backupName=AlgorithmsConfig.CNF_DATA_PATH+File.separator+"backup.data";
            String tmpName=AlgorithmsConfig.CNF_DATA_PATH+File.separator+"tmp.data";
            File oldBackup=new File(backupName);
            if (oldBackup.exists()){
                File tmpF=new File(tmpName);
                oldBackup.renameTo(tmpF);
            }
            File newBackup=new File(backupName);
            try{
                ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(newBackup));
                Server.taskCont.makeBackup(oos);
                oos.flush();
                oos.close();
            }catch (Exception e){
                Server.writeLog(e.toString());
            }
            File oldB=new File(tmpName);
            if (oldB.exists()){
                oldB.delete();
            }
        }
    }

}
