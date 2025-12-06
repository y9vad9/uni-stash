package com.mathpar.parallel.webCluster.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR;
import com.mathpar.number.Ring;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.TreeSet;

import static com.mathpar.parallel.webCluster.engine.Server.writeLog;

/**
 *
 * @author r1d1
 */
public class Launcher {
    private Method  runMPIMethod;
    /**
     * Этот метод вызывается экземляром класса Server и конечной целью его работы
     * служит запуск новой задачи PBS системой. Метод создает 2 файла в папке
     * ../userID/taskID : файл с данными для счета (data.ser) и файл с настройками
     * для PBS системы. Поскольку эти файлы сохраняются на управляющем узле кластера,
     * файл data.ser будет передан по сокетному соединению на корневой узел запущенной
     * задачи.
     * <br>
     * Всего существует 2 типа задач:<br>
     * - готовые алгоритмы, лежащие в пакете package parallel.webCluster.algorithms;<br>     
     * - скомпилированные java-классы, которые были загружены пользователем
     * matrpar через web-интерфейс для запуска на кластере в виде zip-архива.
     * <br>
     * Тип задачи хранится в параметре conf.
     * Если тип задачи не AN_RUN_UPLOADED_CLASS, то путь
     * до запускаемого класса берется из констант класса AlgorithmsConfig.     
     * Если тип задачи AN_RUN_UPLOADED_CLASS, то в первом элементе массива data
     * хранится имя zip-архива, а во втором имя запускаемого класса.
     * Оставшиеся элементы массива data - это данные для счета.
     *
     *
     * @param userID ID пользователя
     * @param taskID ID задачи
     * @param data данные для счета (массив произвольных объектов)
     * @param conf настройки задачи
     * @return результат запуска (константа класса AlgorithmsConfig)
     *
     *
     *
     *
     */
    
    public Launcher(){
        try{
            Class<?> c = Launcher.class;                    
            runMPIMethod= c.getDeclaredMethod(AlgorithmsConfig.CNF_RUN_MPI_METHOD_NAME,new Class[] {
                String.class, String.class, int.class, int.class, TaskConfig.class, boolean.class, String.class});                
        }
        catch (Exception e){
            writeLog("exception in launcher constructor: "+e.toString());
        }
    }
    
    public int launch(int userID, int taskID, Object[] data, TaskConfig conf) {
        //проверка корректности данных
        String algoPath = null;
        boolean isUserClass=false;
        String zipFileName=null;
        switch (conf.getAlgoNumb()) {
            case AlgorithmsConfig.AN_MULT_MATRIX_1x8:
                algoPath = AlgorithmsConfig.MULT_MATRIX_1x8_PATH;                
                if (data.length != 3) {
                    return AlgorithmsConfig.RES_WRONG_DATA_ERROR;
                }
                break;
            case AlgorithmsConfig.AN_MULT_MATRIX_2x4:
                break;
            case AlgorithmsConfig.AN_FACTOR_POL:{
                 algoPath=AlgorithmsConfig.FACTOR_POL_PATH;                 
                 break;
            }
            case AlgorithmsConfig.AN_BELLMAN_EQUATION1:{
                 algoPath=AlgorithmsConfig.BELLMAN_EQUATION1_PATH;                 
                 break;
            }
            case AlgorithmsConfig.AN_BELLMAN_EQUATION2:{
                 algoPath=AlgorithmsConfig.BELLMAN_EQUATION2_PATH;                 
                 break;
            }
            case AlgorithmsConfig.AN_BELLMAN_INEQUALITY1:{
                 algoPath=AlgorithmsConfig.BELLMAN_INEQUALITY1_PATH;                 
                 break;
            }
            case AlgorithmsConfig.AN_BELLMAN_INEQUALITY2:{
                 algoPath=AlgorithmsConfig.BELLMAN_INEQUALITY2_PATH;                 
                 break;
            }
            case AlgorithmsConfig.AN_ETD:{
                 algoPath=AlgorithmsConfig.ETD_PATH;                 
                 break;
            }
            case AlgorithmsConfig.AN_MULT_POLYNOM:{
                 algoPath=AlgorithmsConfig.MULT_POLYNOM_PATH;                 
                 break;
            }
            case AlgorithmsConfig.AN_ADJOINT_DET:{
                algoPath=AlgorithmsConfig.ADJOINT_DET_PATH; 
                break;
            }
            case AlgorithmsConfig.AN_RUN_UPLOADED_CLASS:{
                if (data.length<2){
                    return AlgorithmsConfig.RES_WRONG_ALGO_CONFIG_ERROR;
                }
                isUserClass=true;
                zipFileName=((Element)data[0]).toString();
                algoPath=((Element)data[1]).toString();
                Object []tmpData=new Object[data.length-2];
                for (int i=2; i<data.length; i++)
                    tmpData[i-2]=data[i];
                data=tmpData;
                break;
            }
            case AlgorithmsConfig.AN_CHAR_POL:{
                algoPath=AlgorithmsConfig.CHAR_POL_PATH;
            }
        }
        String folderPath=AlgorithmsConfig.getPathForUserFold(userID, taskID);
        String dataPath=folderPath + "/data.ser";


        File dataF = new File(dataPath);
        dataF.getParentFile().mkdirs();
        try {
            //запись данных для счета в файл на управляющем узле в папку /userX/taskY
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataF));
            oos.writeObject(data);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            return AlgorithmsConfig.RES_DATA_WRITING_ERROR;
        }
        if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG){
            writeLog("calling run mpi code...");
        }
        //int ret =(int) method2.invoke(tmp, new Object[] {12});
        boolean runResult=false;
        try{
            runResult=(boolean) runMPIMethod.invoke(this, new Object[] {algoPath, folderPath, userID, taskID, conf,isUserClass,zipFileName});        
            
        }catch (Exception e){
            writeLog("invoke runMPIMethod exception: "+e.toString());
        }
        if (runResult) {
            return AlgorithmsConfig.RES_SUCCESS;
        } else {
            return AlgorithmsConfig.RES_CREATE_PBS_FILE_ERROR;
        }
    }

    boolean CreateAndRunPBSfileUnihub(String algoPath, String folderPath, int userID, int taskID, TaskConfig conf,boolean isUserClass, String zipFileName) {        
        FileWriter runFile;
        String run_file_path = folderPath + "/run";
        String rootUserFold=AlgorithmsConfig.getPathForUserFold(userID);
        String zipPath=null;
        if (isUserClass){
            zipPath=rootUserFold+File.separatorChar+zipFileName+File.pathSeparatorChar;
        }
        String javaMem=AlgorithmsConfig.CNF_JAVA_OPTIONS+String.valueOf(conf.getMem()/conf.GetNProc())+"M";
        try {            
            File dir = new File(run_file_path);
            runFile = new FileWriter(dir);
            runFile.append("#!/bin/sh\n\n"
                    + "#PBS -l walltime=" + conf.GetWallTime() + ",nodes=" + conf.GetNodesS() + ":ppn=" + conf.GetNprocS() + "\n"
                    + "#PBS -N test\n"
                    + "#PBS -o " + folderPath + "/out\n"
                    + "#PBS -e " + folderPath + "/err\n"
                    + "cd " + folderPath + "\n"
                    + AlgorithmsConfig.CNF_MPIEXEC_PATH
                    + " " + AlgorithmsConfig.CNF_JAVA_PATH + " " + javaMem + " -cp "
                    + (isUserClass?zipPath:"")
                    + AlgorithmsConfig.CNF_MATHPAR_CLASSES
                    + " " + algoPath + " " + userID + " " + taskID);
            runFile.flush();
            runFile.close();
        } catch (IOException e) {
            return false;
        }
        Process qsub;
        try {
            String[] chMod = {"chmod", "777", run_file_path};
            Process chmod = Runtime.getRuntime().exec(chMod);
            chmod.waitFor();
        } catch (Exception e) {
            return false;
        }
        try {
            String[] command = {"/usr/local/bin/qsub", run_file_path};
            qsub = Runtime.getRuntime().exec(command);
            qsub.waitFor();
        } catch (Exception e) {
            Server.writeLog("qsub error" + e);
            return false;
        }
        return true;
    }
    
    boolean runCompileProgramForJSCC(int userID, int taskID, String archName){
        FileWriter runFile;
        String folderPath=AlgorithmsConfig.getPathForUserFold(userID, taskID);
        String run_file_path = folderPath + File.separator+"run";                       
        String javaMem=AlgorithmsConfig.CNF_JAVA_OPTIONS+String.valueOf(AlgorithmsConfig.CNF_MAX_MEMORY/2)+"M";        
        File taskDir=new File(folderPath);
        taskDir.mkdirs();
        try {            
            File dir = new File(run_file_path);
            runFile = new FileWriter(dir);
            runFile.append("#!/bin/sh\n\n"
                    + "#PBS -l walltime=10,nodes=1:ppn=" + AlgorithmsConfig.CNF_MAX_PROCESSES_ON_NODE + "\n"
                    + "#PBS -N test\n"
                    + "#PBS -o " + folderPath + "/out\n"
                    + "#PBS -e " + folderPath + "/err\n"
                    + "cd " + folderPath + "\n"
                    + AlgorithmsConfig.CNF_MPIEXEC_PATH + " -np 1 -npernode 1 --machinefile  $PBS_NODEFILE "   
                    + " " + AlgorithmsConfig.CNF_JAVA_PATH + " " + javaMem + " -cp "                    
                    + AlgorithmsConfig.CNF_MATHPAR_CLASSES
                    + " com.mathpar.parallel.webCluster.engine.CompileZipPackage " + userID + " " + taskID+" "+archName);
            runFile.flush();
            runFile.close();
        } catch (IOException e) {
            return false;
        }
        Process qsub;
        if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG) {
            writeLog("calling chmod...");            
        }
        try {
            String[] chMod = {"chmod", "777", run_file_path};
            Process chmod = Runtime.getRuntime().exec(chMod);
            chmod.waitFor();
        } catch (Exception e) {
            return false;
        }
        if (AlgorithmsConfig.WRITE_SERVER_EXTENTED_LOG) {
            writeLog("calling chmod...");            
        }
        try {
            String[] command = {"/usr/local/bin/qsub", run_file_path};
            qsub = Runtime.getRuntime().exec(command);
            qsub.waitFor();
        } catch (Exception e) {
            Server.writeLog("qsub error" + e);
            return false;
        }
        return true;
    }
    
    boolean CreateAndRunPBSfileUnihubForJSCC(String algoPath, String folderPath, int userID, int taskID, TaskConfig conf,boolean isUserClass, String zipFileName) {        
        FileWriter runFile;
        String run_file_path = folderPath + "/run";
        String rootUserFold=AlgorithmsConfig.getPathForUserFold(userID);
        String zipPath=null;
        if (isUserClass){
            zipPath=rootUserFold+File.separatorChar+zipFileName+File.pathSeparatorChar;
        }
        String javaMem=AlgorithmsConfig.CNF_JAVA_OPTIONS+String.valueOf(conf.getMem()/conf.GetNProc())+"M";
        try {            
            File dir = new File(run_file_path);
            runFile = new FileWriter(dir);
            runFile.append("#!/bin/sh\n\n"
                    + "#PBS -l walltime=" + conf.GetWallTime() + ",nodes=" + conf.GetNodesS() + ":ppn=" + AlgorithmsConfig.CNF_MAX_PROCESSES_ON_NODE + "\n"
                    + "#PBS -N test\n"
                    + "#PBS -o " + folderPath + "/out\n"
                    + "#PBS -e " + folderPath + "/err\n"
                    + "cd " + folderPath + "\n"
                    + AlgorithmsConfig.CNF_MPIEXEC_PATH + " -np "+String.valueOf(conf.GetNProc()*conf.GetNodes())+" -npernode "+conf.GetNprocS()+" --machinefile  $PBS_NODEFILE "   
                    + " " + AlgorithmsConfig.CNF_JAVA_PATH + " " + javaMem + " -cp "
                    + (isUserClass?zipPath:"")
                    + AlgorithmsConfig.CNF_MATHPAR_CLASSES
                    + " " + algoPath + " " + userID + " " + taskID);
            runFile.flush();
            runFile.close();
        } catch (IOException e) {
            return false;
        }
        Process qsub;
        try {
            String[] chMod = {"chmod", "777", run_file_path};
            Process chmod = Runtime.getRuntime().exec(chMod);
            chmod.waitFor();
        } catch (Exception e) {
            return false;
        }
        try {
            String[] command = {"/usr/local/bin/qsub", run_file_path};
            qsub = Runtime.getRuntime().exec(command);
            qsub.waitFor();
        } catch (Exception e) {
            Server.writeLog("qsub error" + e);
            return false;
        }
        return true;
    }
    //метод исправляет файл $PBS_NODEFILE, созданный PBS
    //для openmpi требуется, чтобы количество запускаемых процессов на узле
    //равнялось количеству строк с именем определенного хоста в этом файле
    //поскольку в этом файле имя каждого хоста повторяется 12 раз, этот
    //метод выкидывает лишние повторения из этого файла
    public static void main(String[] args) throws Exception{
        String fName=args[0];
        int ppn=Integer.parseInt(args[1]);       
        BufferedReader br = new BufferedReader(new FileReader(fName));
        TreeSet<String> hostNames=new TreeSet<String>();
        try {            
            String line = br.readLine();
            while (line != null) {
                hostNames.add(line);
                line = br.readLine();
            }
            
        } finally {
            br.close();
        }
        BufferedWriter bw=new BufferedWriter(new FileWriter(fName));
        Iterator<String> it=hostNames.iterator();
        while (it.hasNext()){
            String curName=it.next();
            for (int i=0; i<ppn; i++){
                bw.write(curName+"\n");
            }
        }
        bw.flush();
        bw.close();
    }
    
    boolean CreateAndRunPBSfileUnihubForBadPBS(String algoPath, String folderPath, int userID, int taskID, TaskConfig conf,boolean isUserClass, String zipFileName) {        
        FileWriter runFile;
        String run_file_path = folderPath + "/run";
        String rootUserFold=AlgorithmsConfig.getPathForUserFold(userID);
        String zipPath=null;
        if (isUserClass){
            zipPath=rootUserFold+File.separatorChar+zipFileName+File.pathSeparatorChar;
        }
        String javaMem=AlgorithmsConfig.CNF_JAVA_OPTIONS+String.valueOf(conf.getMem()/conf.GetNProc())+"M";
        //String pbsNodeFileName="/tmp/"+String.valueOf(System.currentTimeMillis())+String.valueOf(userID)+".txt";
        try {            
            File dir = new File(run_file_path);
            runFile = new FileWriter(dir);
            runFile.append("#!/bin/sh\n\n"
                    + "#PBS -l walltime=" + conf.GetWallTime() + ",nodes=" + conf.GetNodesS() + ":ppn=12\n"
                    + "#PBS -q bl220\n"
                    + "#PBS -N test\n"
                    + "#PBS -o " + folderPath + "/out\n"
                    + "#PBS -e " + folderPath + "/err\n"
                   // +"cp $PBS_NODEFILE "+pbsNodeFileName+"\n"
                   // +"java -cp /unicluster/home/i.borisov/soft/mathpar_classes com.mathpar.parallel.webCluster.engine.Launcher "+pbsNodeFileName+" "+conf.GetNprocS()+"\n"
                    + "cd " + folderPath + "\n"
                    + "LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/unicluster/home/i.borisov/soft/openmpi10/lib:/usr/local/lib "
                    + AlgorithmsConfig.CNF_MPIEXEC_PATH
                    +" -np "+String.valueOf(conf.GetNProc()*conf.GetNodes())+" -npernode "+conf.GetNprocS()+" -oversubscribe --machinefile  $PBS_NODEFILE "                    
                    + AlgorithmsConfig.CNF_JAVA_PATH + " " + javaMem + " -cp "
                    + (isUserClass?zipPath:"")
                    + AlgorithmsConfig.CNF_MATHPAR_CLASSES
                    + " " + algoPath + " " + userID + " " + taskID);
            runFile.flush();
            runFile.close();
        } catch (IOException e) {
            return false;
        }
        Process qsub;
        try {
            String[] chMod = {"chmod", "777", run_file_path};
            Process chmod = Runtime.getRuntime().exec(chMod);
            chmod.waitFor();
        } catch (Exception e) {
            return false;
        }
        try {
            String[] command = {"/usr/bin/qsub", run_file_path};
            qsub = Runtime.getRuntime().exec(command);
            qsub.waitFor();
        } catch (Exception e) {
            Server.writeLog("qsub error" + e);
            return false;
        }
        return true;
    }


    boolean runMPIDebug(String algoPath, String folderPath, int userID, int taskID, TaskConfig conf,boolean isUserClass, String zipFileName){
        Server.writeLog("calling running mpi in debug mode");        
        String rootUserFold=AlgorithmsConfig.getPathForUserFold(userID);
        String zipPath=null;
        if (isUserClass){
            zipPath=rootUserFold+File.separator+zipFileName+File.pathSeparator;
        }
        StringBuffer buf=new StringBuffer();
        buf.append("mpirun -np ");
        buf.append(String.valueOf(conf.GetNProc()*conf.GetNodes())+" ");
        String memory="-Xmx"+String.valueOf(conf.getMem())+"M";
        buf.append("java "+memory+" -cp "+AlgorithmsConfig.CNF_MPIJAR_PATH+":");
        buf.append(isUserClass?zipPath:"");
        buf.append(AlgorithmsConfig.CNF_MATHPAR_CLASSES);
        buf.append(" " + algoPath + " " + userID + " " + taskID + " 1> "+AlgorithmsConfig.getPathForUserFold(userID, taskID)+"/out");
        buf.append(" 2> "+AlgorithmsConfig.getPathForUserFold(userID, taskID)+"/err");
        Server.writeLog(buf.toString());
        String []com={"bash" , "-c", buf.toString()};
        Process qsub;
        try {
            qsub = Runtime.getRuntime().exec(com);
            qsub.waitFor();
        } catch (Exception e) {
            Server.writeLog("mpirun error" + e);
            return false;
        }
        return true;
    }


}
