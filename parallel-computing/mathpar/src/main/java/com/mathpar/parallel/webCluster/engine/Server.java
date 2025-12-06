package com.mathpar.parallel.webCluster.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.mathpar.number.Element;


/*
 
java -cp /home/r1d1/install/openmpi/lib/mpi.jar:/home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.webCluster.engine.Server &

 *
 * */

/**
 *
 * @author r1d1
 * <br><br>
 * Класс, реализующий работу сокетного сервера. При каждом новом соединении
 * создается новый экземпляр этого класса. В поле socket хранится сокет для
 * текущего соединения. Поля logFile и taskCont общие для всех экземпляров класса.
 * Обработка всех запросов осуществляется в методе run при помощи конструкции switch.
 * Первым параметром любого запроса является Integer - тип запроса. В зависимости от
 * его значения принимаются решения о дальнейшем считывании данных по сокетному
 * соединению.
 */

public class Server extends Thread {
    Socket socket;

    static FileWriter logFile;

    static TaskContainer taskCont;

    static String getTime() {
        long t = System.currentTimeMillis();
        String res = "";
        t /= 1000;
        t += 4 * 60 * 60;
        long h = t % (24 * 60 * 60) / (60 * 60);
        long m = t % (60 * 60) / 60;
        long s = t % 60;
        if (h < 10) {
            res += "0";
        }
        res += h + ":";
        if (m < 10) {
            res += "0";
        }
        res += m + ":";
        if (s < 10) {
            res += "0";
        }
        res += s;
        return res;
    }

    static boolean isNight(){
        long t = System.currentTimeMillis();
        t /= 1000;
        t += 4 * 60 * 60;
        long h = t % (24 * 60 * 60) / (60 * 60);
        if (h>=2 && h<5) return true;
        return false;
    }

    static synchronized void writeLog(String s) {//печать лога в файл
        try {
            logFile.append(s + " " + getTime() + "\n");
            logFile.flush();
        } catch (IOException e) {
        }
    }


    Server(Socket s) {
        this.socket = s;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
    }

    public static void main(String[] args) {
        try {
            System.in.close();
            System.out.close();            
            
            File fold = new File(AlgorithmsConfig.CNF_DATA_PATH);
            fold.mkdirs();
            logFile = new FileWriter(AlgorithmsConfig.CNF_DATA_PATH + File.separatorChar+"log.txt", false);
            
            ServerSocket server = new ServerSocket(AlgorithmsConfig.CNF_SERV_PORT);
            taskCont=new TaskContainer();
            BackupMaker.restoreFromBackup();
            TrashRemover TR=new TrashRemover();
            BackupMaker BM=new BackupMaker();
            writeLog("Server successfully started");            
            writeLog("File life time is "+String.valueOf(AlgorithmsConfig.CNF_FILE_LIFETIME+" ms,"));
            writeLog("Task life time is "+String.valueOf(AlgorithmsConfig.CNF_TASK_LIFETIME+" ms,"));
            writeLog("Trash removing period is "+String.valueOf(AlgorithmsConfig.CNF_TRASH_REMOVING_PERIOD+" ms,"));
            writeLog("Backup making period is "+String.valueOf(AlgorithmsConfig.CNF_BACKUP_MAKING_PERIOD+" ms,"));
            writeLog("max total proc: "+String.valueOf(AlgorithmsConfig.CNF_MAX_TOTAL_NODES));
            writeLog("max cores per proc: "+String.valueOf(AlgorithmsConfig.CNF_MAX_PROCESSES_ON_NODE));
            writeLog("Data folder is "+AlgorithmsConfig.CNF_DATA_PATH);
            //####################################
            while (true) {
                new Server(server.accept()).start();
            }
        }catch(Exception e){
            e.printStackTrace();
            writeLog(e+"Create server error");
        }
    }
    /*
     Запросы:
     * первым параметром всегда идет тип запроса
     *
     * ADD_TASK=0; - добавить задачу
     * параметры: Integer userID, Object[] data, Task_config conf;
     * ответ: Integer launchResult, Integer taskNumb (результат запуска и присвоенный номер задачи)

     * GET_STATE=1; - узнать состояние задачи
     * параметры: Integer userID, Integer taskID;
     * ответ: Integer taskState;

     * QS_GET_DATA_FOR_CALC=5; - получить данные для счета
     * (этот запрос идет от произвольного узла кластера к управляющему)
     * параметры: Integer userID, Integer taskID;
     * ответ: Integer error, Object []data;
     *
     * QS_RECV_RESULT_FOR_TASK=6; - послать результат вычислений на сервер
     * (этот запрос идет от произвольного узла кластера к управляющему)
     * параметры: Integer userID, Integer taskID, Object []data;
     * ответ: --
     *
     * QS_RECV_RESULT_FOR_TASK_WEB=7; - получить результат для задачи
     * (этот запрос идет от web mathpar к серверу)
     * параметры: Integer userID, Integer taskID;
     * ответ: Integer error, Object []data
     *
     * QS_UPLOAD_FILE_TO_CLUSTER=8 - отправить файл на кластер
     * (этот запрос идет от web mathpar к серверу)
     * параметры: Integer userID, String fileName, Object data (массив байтов с файлом)
     * ответ: Integer saveFileRes
     *
     * CHANGE_STATE=4; - изменить состояние задачи
     * (этот запрос идет от произвольного узла кластера к управляющему)
     * параметры: Integer userID, Integer taskID,Integer newState;
     * ответ: --
     */

    @Override
    public void run() {
        try {
            socket.setSoTimeout(30*60*1000);
            InputStream is = socket.getInputStream();
            ObjectInputStream inp = new ObjectInputStream(is);
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            Launcher launcher = new Launcher();
            if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG) {
                writeLog("connection with " + socket.toString() + " accepted");
            }
            while (true) {
                try {
                    Integer qType = (Integer) inp.readObject();
                    Integer userID;

                    switch (qType) {
                        case AlgorithmsConfig.ECHO_CONST:{
                            oos.writeObject(AlgorithmsConfig.ECHO_CONST);
                            break;
                        }
                        case AlgorithmsConfig.QS_GET_STATE_LIST:{
                            userID = (Integer) inp.readObject();
                            Object[] data={taskCont.getTasksListForUser(userID)};
                            oos.writeObject(data);
                            break;
                        }
                        case AlgorithmsConfig.QS_GET_FILE_LIST:{
                            userID = (Integer) inp.readObject();
                            Object[] data={taskCont.getFileListForUser(userID)};
                            oos.writeObject(data);
                            break;
                        }
                        case AlgorithmsConfig.QS_GET_FILE_CONTENT:{
                            userID = (Integer) inp.readObject();
                            Integer taskID = (Integer) inp.readObject();
                            taskCont.touchTask(userID,taskID);
                            taskCont.CheckTaskForCrash(userID,taskID);
                            String fileName=(String)inp.readObject();
                            byte[] tmp = null;
                            try {
                                fileName=AlgorithmsConfig.getPathForUserFold(userID, taskID)+File.separator+fileName;
                                FileInputStream fis = new FileInputStream(fileName);
                                int fileSize = fis.available();
                                tmp = new byte[fileSize];
                                fis.read(tmp);
                                fis.close();
                                String res;
                                if (fileSize==0){
                                    res="File  is  empty";
                                }
                                else {
                                    res="\""+new String(tmp)+"\"";
                                }
                                oos.writeObject(AlgorithmsConfig.RES_SUCCESS);
                                oos.writeObject(res);
                            } catch (Exception e) {
                                oos.writeObject(AlgorithmsConfig.RES_DATA_READING_ERROR);
                            }
                            break;
                        }
                        case AlgorithmsConfig.QS_ADD_TASK: {
                            if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG){
                                writeLog("server recieve add task request");
                            }
                            userID = (Integer) inp.readObject();
                            Object[] data;
                            try {
                                data = (Object[]) inp.readObject();
                            }catch(Exception e){
                                writeLog("error caused by opening file with task data: "+e.toString());
                                break;
                            }
                            TaskConfig taskConf = (TaskConfig) inp.readObject();                            
                            String fileName=null;
                            if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG){
                                writeLog("checking task type...");
                            }
                            if (taskConf.getAlgoNumb()==AlgorithmsConfig.AN_RUN_UPLOADED_CLASS){
                                fileName=((Element)data[0]).toString();
                                if (!taskCont.isFileExist(userID, fileName)){
                                    oos.writeObject(AlgorithmsConfig.RES_FILE_NOT_FOUND_ERROR);
                                    oos.writeObject(new Integer(0));
                                    break;
                                }
                            }
                            if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG){
                                writeLog("adding task in container...");
                            }
                            Integer taskNumb = taskCont.addTask(userID,fileName);
                            if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG){
                                writeLog("calling launcher...");
                            }
                            Integer launchResult = launcher.launch(userID, taskNumb, data, taskConf);
                            oos.writeObject(launchResult);
                            oos.writeObject(taskNumb);
                            break;
                        }
                        case AlgorithmsConfig.QS_UPLOAD_FILE_TO_CLUSTER: {
                            userID = (Integer) inp.readObject();
                            String fileName=(String)inp.readObject();
                            taskCont.addFile(userID, fileName);
                            byte [] data = (byte []) inp.readObject();
                            Integer saveFileRes=AlgorithmsConfig.RES_SUCCESS;
                            try {
                                File dataF = new File(AlgorithmsConfig.getPathForUserFold(userID));
                                dataF.mkdirs();
                                fileName=AlgorithmsConfig.getPathForUserFold(userID)+File.separator+fileName;
                                FileOutputStream fos=new FileOutputStream(fileName);
                                fos.write(data);
                                fos.flush();
                                fos.close();
                            }catch (Exception e){
                                saveFileRes=AlgorithmsConfig.RES_DATA_WRITING_ERROR;
                            }
                            oos.writeObject(saveFileRes);
                            break;
                        }
                        case AlgorithmsConfig.QS_UPLOAD_FILE_TO_CLUSTER_AND_COMPILE: {
                            userID = (Integer) inp.readObject();
                            String archName=(String)inp.readObject();
                            taskCont.addFile(userID, archName);
                            byte [] data = (byte []) inp.readObject();
                            Integer saveFileRes=AlgorithmsConfig.RES_SUCCESS;
                            String fileName=null;
                            try {
                                File dataF = new File(AlgorithmsConfig.getPathForUserFold(userID));
                                dataF.mkdirs();
                                fileName=AlgorithmsConfig.getPathForUserFold(userID)+File.separator+archName;
                                FileOutputStream fos=new FileOutputStream(fileName);
                                fos.write(data);
                                fos.flush();
                                fos.close();
                            }catch (Exception e){
                                saveFileRes=AlgorithmsConfig.RES_DATA_WRITING_ERROR;
                                oos.writeObject(saveFileRes);
                                break;
                            }                                                                                                                                         
                            if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG){
                                writeLog("adding task in container...");
                            }
                            Integer taskNumb = taskCont.addTask(userID,fileName);
                            if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG){
                                writeLog("calling launcher...");
                            }
                            boolean launchResultBool = launcher.runCompileProgramForJSCC(userID, taskNumb, archName);
                            Integer launchResult=AlgorithmsConfig.RES_SUCCESS;
                            if (!launchResultBool){
                                launchResult=AlgorithmsConfig.RES_CREATE_PBS_FILE_ERROR;
                            }
                            oos.writeObject(launchResult);
                            oos.writeObject(taskNumb);                            
                            break;
                        }
                        case AlgorithmsConfig.QS_GET_DATA_FOR_CALC: {
                            writeLog("case for data request processing start...");
                            userID = (Integer) inp.readObject();
                            Integer taskID = (Integer) inp.readObject();
                            taskCont.changeStateForTask(userID, taskID, AlgorithmsConfig.ST_RUNNING);
                            String dataPath = AlgorithmsConfig.getPathForUserFold(userID, taskID) + "/data.ser";
                            Integer res = AlgorithmsConfig.RES_SUCCESS;
                            Object[] data = null;
                            try {
                                ObjectInputStream fois = new ObjectInputStream(new FileInputStream(dataPath));
                                data = (Object[]) fois.readObject();
                                fois.close();
                            } catch (Exception e) {
                                writeLog("reading data for user and task " + userID + " " + taskID + " error " + e.toString());
                                res = AlgorithmsConfig.RES_DATA_READING_ERROR;
                            }
                            try {
                                oos.writeObject(res);
                                oos.writeObject(data);
                                oos.flush();
                            } catch (IOException e) {
                                writeLog("send data error for user and task " + userID + " " + taskID);
                            }
                            break;
                        }
                        case AlgorithmsConfig.QS_SAVE_CALC_RESULT: {
                            userID = (Integer) inp.readObject();
                            Integer taskID = (Integer) inp.readObject();
                            Object[] data = (Object[]) inp.readObject();
                            String resPath = AlgorithmsConfig.getPathForUserFold(userID, taskID) + "/result.ser";
                            ObjectOutputStream foos = new ObjectOutputStream(new FileOutputStream(resPath));
                            foos.writeObject(data);
                            foos.flush();
                            foos.close();
                            taskCont.changeStateForTask(userID, taskID, AlgorithmsConfig.ST_FINISHED);
                            break;
                        }
                        case AlgorithmsConfig.QS_SEND_MESSAGE_ABOUT_FINISH: {
                            userID = (Integer) inp.readObject();
                            Integer taskID = (Integer) inp.readObject();                            
                            taskCont.changeStateForTask(userID, taskID, AlgorithmsConfig.ST_FINISHED);
                            break;
                        }
                        case AlgorithmsConfig.QS_SEND_CALC_RESULT_TO_WEB: {
                            userID = (Integer) inp.readObject();
                            Integer taskID = (Integer) inp.readObject();
                            taskCont.touchTask(userID, taskID);
                            Integer err;
                            Object[] data=null;
                            if (taskCont.CheckTaskForCrash(userID,taskID)){
                                err=AlgorithmsConfig.RES_CRASH_TASK_ERROR;
                                oos.writeObject(err);
                                break;
                            }
                            int tState = taskCont.getStateForTask(userID, taskID);
                            if (tState != AlgorithmsConfig.ST_FINISHED) {
                                if (tState == AlgorithmsConfig.ST_NOT_EXIST) {
                                    err = AlgorithmsConfig.RES_NO_TASK_ERROR;
                                } else {
                                    err = AlgorithmsConfig.RES_NO_RESULT_ERROR;
                                }
                                oos.writeObject(err);
                                break;
                            }

                            String resPath = AlgorithmsConfig.getPathForUserFold(userID, taskID) + "/result.ser";
                            try {
                                ObjectInputStream fois = new ObjectInputStream(new FileInputStream(resPath));
                                data = (Object[]) fois.readObject();
                            } catch (Exception e) {
                                writeLog("can't read data result for user and task " + userID + " " + taskID);
                                err = AlgorithmsConfig.RES_READING_RESULT_FILE_ERROR;
                                oos.writeObject(err);
                                break;
                            }
                            err = AlgorithmsConfig.RES_SUCCESS;
                            oos.writeObject(err);
                            oos.writeObject(data);
                            break;
                        }
                        case AlgorithmsConfig.QS_GET_STATE: {
                            userID = (Integer) inp.readObject();
                            Integer taskID = (Integer) inp.readObject();
                            taskCont.touchTask(userID,taskID);
                            taskCont.CheckTaskForCrash(userID,taskID);
                            Object[] data = {taskCont.getStateForTask(userID,taskID)};
                            oos.writeObject(data);
                            break;
                        }
                        case AlgorithmsConfig.QS_CANCEL_TASK:
                            break;
                        default:
                            writeLog("wrong query type");
                    }
                } catch (Exception e) {
                    writeLog(e.toString());
                    writeLog("connection with " + socket.toString() + " terminated");
                    break;
                }
            }
            socket.close();
        } catch (IOException e) {
            writeLog(e.toString());
        }
    }
}
