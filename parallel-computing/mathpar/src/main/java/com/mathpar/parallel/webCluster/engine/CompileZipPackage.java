/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.engine;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import mpi.MPI;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author r1d1
 */



public class CompileZipPackage {
    public static boolean printErrStream(Process p, String actionName,String []args)throws Exception{        
        BufferedReader STDErrorInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));       
        String s = null;        
        boolean errFlag=false;
        while ((s = STDErrorInput.readLine()) != null) {
            errFlag=true;
            System.err.println(s);
        }
        if (errFlag){
            System.out.println(actionName+" fail.");
            Tools.sendFinishMessage(args);
            MPI.Finalize();
            return false;
        }
        System.out.println(actionName+" finished.");
        return true;
    }
    
    public static void directoryWalk(String path, ArrayList<String> result) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                directoryWalk(f.getAbsolutePath(),result);                
            } else {
                String curFileName=f.getName();
                if (curFileName.length()>5 && curFileName.substring(curFileName.length()-5,curFileName.length()).compareTo(".java")==0){
                    result.add(f.getAbsolutePath());                
                }
            }
        }
    }
    
    private static void compressDirectory(String directory, String additionalPrefix, ZipOutputStream out) throws Exception {        
        File fileToCompress = new File(directory);
        // list contents.
        String[] contents = fileToCompress.list();
        // iterate through directory and compress files.
        for (int i = 0; i < contents.length; i++) {
            File f = new File(directory, contents[i]);
            // testing type. directories and files have to be treated separately.
            if (f.isDirectory()) {
                // add empty directory
                out.putNextEntry(new ZipEntry(f.getName() + File.separator));
                // initiate recursive call
                compressDirectory(f.getPath(),additionalPrefix+f.getName()+File.separator, out);
                // continue the iteration
                continue;
            } else {
                // prepare stream to read file.
                FileInputStream in = new FileInputStream(f);
                // create ZipEntry and add to outputting stream.
                System.out.println("compressing "+additionalPrefix+f.getName()+"...");
                out.putNextEntry(new ZipEntry(additionalPrefix+f.getName()));
                // write the data.
                int len;
                byte data[]=new byte[4000000];
                while ((len = in.read(data)) > 0) {
                    out.write(data, 0, len);
                }
                out.flush();
                out.closeEntry();
                in.close();
            }
        }
    }
    
    //userID, taskID, zipName
    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        QueryResult queryRes=Tools.getDataFromClusterRootNode(args);
        
        String archFullName=AlgorithmsConfig.getPathForUserFold(Integer.parseInt(args[0]))+File.separator+args[2];        
        String mpiJavaCompiler=AlgorithmsConfig.CNF_MPICOMPILER_PATH;
        String mpiJarPath=AlgorithmsConfig.CNF_MPIJAR_PATH;
        String mathparClasses=AlgorithmsConfig.CNF_MATHPAR_CLASSES;
        String outputFolder=AlgorithmsConfig.getPathForUserFold(Integer.parseInt(args[0]));
        String archNameWithoutZip=args[2].substring(0, args[2].length()-4);
        String inputFolder=archFullName.substring(0, archFullName.length()-4);
             
        System.out.println("unzip start...");                            
        String[] unzipCommand = {"unzip", "-o", "-q", archFullName, "-d",outputFolder};
        Process unzip = Runtime.getRuntime().exec(unzipCommand);
        unzip.waitFor();
        if (!printErrStream(unzip, "unzip",args)){
            return;
        }
        
       
        
        String tmpDirName=outputFolder+File.separator+"__tmpFor_"+archNameWithoutZip;
        System.out.println("tmp dir making start...");                            
        String[] mkDirCommand = {"mkdir", "-p", tmpDirName};
        Process mkdir = Runtime.getRuntime().exec(mkDirCommand);
        mkdir.waitFor();
        if (!printErrStream(mkdir, "tmp directory making",args)){
            return;
        }
        
        
        System.out.println("compile start..."); 
        ArrayList<String> allSouceNames=new ArrayList<String>();
        directoryWalk(inputFolder,allSouceNames);
        for (int i=0; i<allSouceNames.size(); i++){
            String curFileName=allSouceNames.get(i);
            String[] compileCommand = {mpiJavaCompiler, "-d", tmpDirName, "-cp", mpiJarPath + ":" + mathparClasses, curFileName};
            Process compile = Runtime.getRuntime().exec(compileCommand);
            compile.waitFor();
            if (!printErrStream(compile, "compiling for "+new File(curFileName).getName(),args)) {
                return;
            }             
        }
        
        System.out.println("souce directory removing start...");                            
        String[] rmDirCommand = {"rm", "-R", inputFolder};
        Process rmdir = Runtime.getRuntime().exec(rmDirCommand);
        rmdir.waitFor();
        if (!printErrStream(rmdir, "source directory removing",args)){
            return;
        }
                
        System.out.println("removing archieve with sources start...");                            
        String[] rmArchDirCommand = {"rm", archFullName};
        Process rmArch = Runtime.getRuntime().exec(rmArchDirCommand);
        rmArch.waitFor();
        if (!printErrStream(rmArch, "removing archieve with sources removing",args)){
            return;
        }
              
        FileOutputStream zfos=new FileOutputStream(archFullName);
        ZipOutputStream zos=new ZipOutputStream(zfos);
        compressDirectory(tmpDirName,"", zos);
        zos.flush();
        zos.close();
        zfos.flush();
        zfos.close();
        
        System.out.println("removing tmp directory start...");                            
        String[] rmTmpDirCommand = {"rm", "-R", tmpDirName};
        Process rmtmpdir = Runtime.getRuntime().exec(rmTmpDirCommand);
        rmtmpdir.waitFor();
        if (!printErrStream(rmtmpdir, "removing tmp directory removing",args)){
            return;
        }
        System.out.println("sending finish message...");
        Tools.sendFinishMessage(args);
        
        MPI.Finalize();
    }
}
