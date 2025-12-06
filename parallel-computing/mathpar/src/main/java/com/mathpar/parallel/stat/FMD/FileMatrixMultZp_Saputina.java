/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.FMD;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.dense.FileMatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.parallel.stat.FMD.MultFMatrix.Multiplay;
import com.mathpar.parallel.stat.FMD.MultFMatrix.SendReciveFileMatrixL;
import com.mathpar.parallel.utils.MPITransport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import mpi.MPI;


/**
 *
 * @author r1d1
 */
//args: mSize, nbits
// mpirun -np 8 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.stat.FMD.FileMatrixMultZp_Saputina 4 30
public class FileMatrixMultZp_Saputina implements Serializable {
    static String logName="/home/r1d1/FMlogs";
    static String matrixFolder="/tmp/mA";
    static boolean PRINT_LOGS=false;
    static boolean PRINT_MODS_LOGS=false;
    static Ring ring = new Ring("Z[x,y,z]");
    
    public static void printLog(int rank, String s) throws Exception{                       
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logName+File.separatorChar+String.valueOf(rank)+".txt", true)));
        out.println(s);
        out.close();        
    }
    
    public static FileMatrixD[] saveAndMultMatrixesByModules(FileMatrixD mA, FileMatrixD mB, ArrayList<NumberZ> mods, int range[], int myRank) throws Exception{
        FileMatrixD[] res=new FileMatrixD[range[1]-range[0]+1];
        for (int i=range[0],k=0; i<=range[1]; i++,k++){
            File f1 = new File(matrixFolder+File.separatorChar+"AByMod"+mods.get(i).toString(ring));              
            File f2 = new File(matrixFolder+File.separatorChar+"BByMod"+mods.get(i).toString(ring));
            File f3 = new File(matrixFolder+File.separatorChar+"CByMod"+mods.get(i).toString(ring));
            FileMatrixD tmpA=mA.copyByMod(f1, mods.get(i), ring);
            FileMatrixD tmpB=mB.copyByMod(f2, mods.get(i), ring);
            res[k]=tmpA.multCU(tmpB, f3,mods.get(i).longValue());
            if (PRINT_MODS_LOGS){
                printLog(myRank, "matrixes by mod="+mods.get(i).toString(ring));
                printLog(myRank, "mA: \n"+tmpA.toMatrixD().toString(ring));
                printLog(myRank, "mB: \n"+tmpB.toMatrixD().toString(ring));
                printLog(myRank, "mC: \n"+res[k].toMatrixD().toString(ring));
            }
        }
        return res;
    }
    
    public static void main(String[] args) throws Exception {               
        String[] chMod = {"rm", "-R", matrixFolder};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();      

        MPI.Init(args);
        int myRank=MPI.COMM_WORLD.getRank();
        int wSize=MPI.COMM_WORLD.getSize();
        long begTime=System.currentTimeMillis();
        if (PRINT_LOGS){
            if (myRank==0){
                String[] chModLog = {"rm", "-R", logName};
                Process chmodLog = Runtime.getRuntime().exec(chModLog);
                chmodLog.waitFor();
                String[] chMod1Log = {"mkdir", logName};
                Process chmod1Log = Runtime.getRuntime().exec(chMod1Log);
                chmod1Log.waitFor();                
            }
            MPI.COMM_WORLD.barrier();
        }
        
        
        File f1 = new File(matrixFolder+"/AonRank"+String.valueOf(myRank));              
        File f2 = new File(matrixFolder+"/BonRank"+String.valueOf(myRank));
        
        Transport2<FileMatrixD> t = new Transport2<FileMatrixD>(FileMatrixD.class);
        int mSize=Integer.parseInt(args[0]);
        int nBits=Integer.parseInt(args[1]);        
        int depth=1,blocksCnt=1;
        for (; ; blocksCnt*=4,depth++){
            if (blocksCnt>=wSize){
                break;
            }
        }        
        if (myRank==0){
            System.out.println("depth="+depth+", blocksCnt="+blocksCnt);
        }
        if (depth>=mSize){
            if (myRank==0){
                System.out.println("depth for matrixes can't be great than mSize, reduce np param");
            }
            MPI.Finalize();
            return;
        }
        ArrayList<NumberZ> mods=null;        
        FileMatrixD A=null,B=null;
        if (myRank==0){
            //исходные матрицы - генерация и рассылка
            //################################
            A=new FileMatrixD(f1, depth, mSize, mSize, nBits);
            B=new FileMatrixD(f2, depth, mSize, mSize, nBits);
            for (int i=1; i<MPI.COMM_WORLD.getSize(); i++){
                t.send(A,i, 0);
                t.send(B,i, 1);
            }     
            if (PRINT_LOGS){
                printLog(myRank,"A=\n"+ A.toMatrixD().toString(ring));
                printLog(myRank,"B=\n"+ B.toMatrixD().toString(ring));
            }
            //################################
            
            //модули - нахождение необходимого количества и генерация
            //################################
            mods = new ArrayList<NumberZ>();            
            NumberZ lenComparator = (new NumberZ(nBits, new Random())).multiply(new NumberZ(nBits, new Random())).multiply(new NumberZ(100*mSize));
            NumberZ product = new NumberZ(1);
            for (long i = 1000000000; product.compareTo(lenComparator, ring) == -1; i++) {
                NumberZ cur = new NumberZ(i);
                if (cur.isProbablePrime(1)) {
                    mods.add(cur);
                    product = product.multiply(cur);
                }
            }          
            System.out.println("cnt mods="+mods.size());
            //################################
        }
        else{
            //принимаем матрицы на других узлах
            A=t.recv(0,0);
            B=t.recv(0,1);
            if (PRINT_LOGS){
                printLog(myRank,"A=\n"+ A.toMatrixD().toString(ring));
                printLog(myRank,"B=\n"+ B.toMatrixD().toString(ring));
            }            
        }        
        
        //отправим и примем модули
        Object []tmpAr=new Object[1];
        if (myRank==0){
            tmpAr[0]=mods;
        }       
        MPITransport.bcastObjectArray(tmpAr, 1, 0);
        if (myRank!=0){
            mods=(ArrayList<NumberZ>)tmpAr[0];
        }
        if (PRINT_LOGS) {
            printLog(myRank, "received mods:");
            for (int i = 0; i < mods.size(); i++) {
                printLog(myRank, mods.get(i).toString(ring));
            }
        }
        //#############################
        
        //найдем диапазоны модулей и блоков матриц для каждого узла
        int [][]modRanges=new int[wSize][2];
        int [][]blockRanges=new int[wSize][2];
        int []blockNumbToProcRank=new int[blocksCnt];
        int modsOnProc=mods.size()/wSize, remMods=mods.size()%wSize;
        int blocksOnProc=blocksCnt/wSize, remBlocks=blocksCnt%wSize;
        int curModNumb=0,curBlocknumb=0,tmpCurBlockNumb=0;
        for (int i=0; i<wSize; i++){
            modRanges[i][0]=curModNumb;
            modRanges[i][1]=curModNumb+modsOnProc-1;
            curModNumb+=modsOnProc;
            if (remMods>0){
                modRanges[i][1]++;
                curModNumb++;
                remMods--;
            }
            blockRanges[i][0]=curBlocknumb;
            blockRanges[i][1]=curBlocknumb+blocksOnProc-1;
            curBlocknumb+=blocksOnProc;
            if (remBlocks>0){
                blockRanges[i][1]++;
                curBlocknumb++;
                remBlocks--;
            }
            for (int j=blockRanges[i][0]; j<=blockRanges[i][1]; j++,tmpCurBlockNumb++){
                blockNumbToProcRank[tmpCurBlockNumb]=i;
            }
        }
        int []modNumbToProcRank=new int[mods.size()];
        for (int i=0; i<modRanges.length; i++){
            for (int j=modRanges[i][0]; j<=modRanges[i][1]; j++){
                modNumbToProcRank[j]=i;
            }
        }
        //#############################        
        if (PRINT_LOGS){
            printLog(myRank, "mod ranges: "+modRanges[myRank][0]+" "+modRanges[myRank][1]);
            printLog(myRank, "block ranges: "+blockRanges[myRank][0]+" "+blockRanges[myRank][1]);
            String blockOut="Blocks to proc array: ";
            for (int i=0; i<blocksCnt; i++){
                blockOut+=String.valueOf(blockNumbToProcRank[i])+" ";
            }
            printLog(myRank,blockOut);           
            String modOut="Mods numbs to proc array: ";
            for (int i=0; i<mods.size(); i++){
                modOut+=String.valueOf(modNumbToProcRank[i])+" ";
            }
            printLog(myRank, modOut);           
        }
        FileMatrixD []wholeMatrixRems=null;
        if (modRanges[myRank][1]>=modRanges[myRank][0]){
            wholeMatrixRems=saveAndMultMatrixesByModules(A, B, mods,modRanges[myRank],myRank);
        }
        
        FileMatrixD [][]blockMatrixRems=new FileMatrixD [blockRanges[myRank][1]-blockRanges[myRank][0]+1][mods.size()];
        int nameCounter=0;
        if (PRINT_LOGS){
            printLog(myRank, "Cmods send/recv start...");               
        }
        for (int curBNumb=0; curBNumb<blocksCnt; curBNumb++){
            for (int curModNumbI=0; curModNumbI<mods.size(); curModNumbI++){
                int whoIsCreated=modNumbToProcRank[curModNumbI];
                int whoIsRestore=blockNumbToProcRank[curBNumb];
                int curTag=curBNumb*(mods.size()+10)+curModNumbI;               
                if (whoIsCreated==myRank || whoIsRestore==myRank){
                    if (whoIsCreated==whoIsRestore){
                        File tmpF=new File(matrixFolder+File.separatorChar+String.valueOf(myRank)+"_"+String.valueOf(nameCounter));
                        nameCounter++;                            
                        blockMatrixRems[curBNumb-blockRanges[myRank][0]][curModNumbI]=wholeMatrixRems[curModNumbI-modRanges[myRank][0]].copyBlockTo(curBNumb, tmpF);
                    }
                    else{
                        if (whoIsCreated==myRank){
                            t.SendBlockFM(wholeMatrixRems[curModNumbI-modRanges[myRank][0]], curBNumb, whoIsRestore, curTag);
                        }
                        else{
                            File tmpF=new File(matrixFolder+File.separatorChar+String.valueOf(myRank)+"_"+String.valueOf(nameCounter));
                            nameCounter++;
                            blockMatrixRems[curBNumb-blockRanges[myRank][0]][curModNumbI]=t.RecvBlockFMHowNewMatrix(tmpF, whoIsCreated, curTag);
                        }
                    }
                }
            }
        }
        if (PRINT_LOGS){
            printLog(myRank, "Cmods send/recv finished");       
        }
        if (PRINT_MODS_LOGS){
            if (modRanges[myRank][1]>=modRanges[myRank][0]){                
                printLog(myRank,"rems: ");
                for (int i=0,k=blockRanges[myRank][0]; i<blockMatrixRems.length; i++,k++){
                    printLog(myRank,"rems for block with numb="+String.valueOf(k));
                    for (int j=0; j<mods.size(); j++){
                        printLog(myRank,"rems for mod="+mods.get(j).toString(ring));
                        printLog(myRank,blockMatrixRems[i][j].toMatrixD().toString(ring));
                    }
                }
            }
        }      
        if (PRINT_LOGS){
            printLog(myRank, "restoration by Garner start...");
        }
        Newton.initRArray(mods);
        FileMatrixD []restoredRems=new FileMatrixD[blockMatrixRems.length];
        for (int i=0,bNumb=blockRanges[myRank][0]; i<blockMatrixRems.length; i++,bNumb++){
            File curRemF=new File(matrixFolder+File.separatorChar+String.valueOf(myRank)+"_restoredM"+String.valueOf(i));
            restoredRems[i]=new FileMatrixD(curRemF,1);
            restoredRems[i].restoreByGarner(blockMatrixRems[i], mods);
            if (myRank!=0){
                if (PRINT_LOGS){
                    printLog(myRank, "sending restored block with numb="+String.valueOf(bNumb));
                }
                t.SendFMWithSingleDepthHowMatrixPart(restoredRems[i], 0, bNumb+1500000000);
            }
        }
        if (PRINT_LOGS){
            printLog(myRank, "restoration by Garner finished.");
        }
        if (myRank==0){
            File resF=new File(matrixFolder+File.separatorChar+"RES");
            FileMatrixD res=new FileMatrixD(resF, depth);
            for (int i=0,bNumb=blockRanges[myRank][0]; i<restoredRems.length; i++,bNumb++){
                restoredRems[i].copyThisMatrixHowPartBigMatrix(bNumb,depth,resF);
            }
            for (int i=blockRanges[0][1]+1; i<blocksCnt; i++){
                if (PRINT_LOGS){
                    printLog(myRank, "recieving restored block with numb="+String.valueOf(i)+" from proc with rank="+String.valueOf(blockNumbToProcRank[i]));
                }
                t.RecvBlockFMHowPartOfMatrix(resF, i, depth, blockNumbToProcRank[i], i+1500000000);
            }
            System.out.println("runTime="+(System.currentTimeMillis()-begTime));
            
            if (PRINT_LOGS){
                MatrixD trueRes=A.toMatrixD().multCU(B.toMatrixD(), ring);
                printLog(myRank,"bruteForseRes=\n"+trueRes.toString(ring));
                printLog(myRank,"modRes=\n"+res.toMatrixD().toString(ring));                
                if (res.toMatrixD().subtract(trueRes, ring).isZero(ring)){
                   System.out.println("TRUE answer");
                }
                else{
                    System.out.println("WRONG answer");
                }
            }                        
        }
        MPI.Finalize();
    }
}

