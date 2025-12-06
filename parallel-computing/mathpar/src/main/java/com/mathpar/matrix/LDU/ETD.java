/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU;

import com.mathpar.matrix.LDU.TO.ETDpTO;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import com.mathpar.parallel.webCluster.engine.QueryResult;
import com.mathpar.parallel.webCluster.engine.Tools;
import java.io.IOException;
import mpi.MPI;
import mpi.MPIException;

import static com.mathpar.matrix.LDU.ETDmpi.ETDParralel;
import static com.mathpar.matrix.LDU.ETDmpi.time;
import static com.mathpar.matrix.LDU.ETDmpi.timeExchAndRecoveryAndBack;

/**
 *
 * @author ridkeim
 */
public class ETD {
    
    //<editor-fold defaultstate="collapsed" desc="Triangular Decomposition modular functions">
    public static MatrixS[] ETDmodLDU(MatrixS T){
        try {
            return ETDp.ETDmod(T, ETDpTO.RESULT_LDU);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static MatrixS[] ETDmodWDK(MatrixS T){
        try {
            return ETDp.ETDmod(T, ETDpTO.RESULT_WDK);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static MatrixS[] ETDmodPLDUQWDK(MatrixS T){
        try {
            return ETDp.ETDmod(T, ETDpTO.RESULT_PLDUQWDK);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static MatrixS[] ETDmodLDUWDK(MatrixS T){
        try {
            return ETDp.ETDmod(T, ETDpTO.RESULT_LDUWDK);
        } catch (Exception ex) {
            return null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Tiangular Decomosition linear functions">

    public static MatrixS[] ETDLDU(MatrixS T){
        try {
            return ETDp.ETD(T, ETDpTO.RESULT_LDU);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static MatrixS[] ETDWDK(MatrixS T){
        try {
            return ETDp.ETD(T, ETDpTO.RESULT_WDK);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static MatrixS[] ETDPLDUQWDK(MatrixS T){
        try {
            return ETDp.ETD(T, ETDpTO.RESULT_PLDUQWDK);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static MatrixS[] ETDLDUWDK(MatrixS T){
        try {
            return ETDp.ETD(T, ETDpTO.RESULT_LDUWDK);
        } catch (Exception ex) {
            return null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Triangular Decomposition parrallel function (NOT TESTED!!)">

    private static void ETDmodLDUpar(String[] args) throws MPIException, IOException, ClassNotFoundException{
        MPI.Init(args);
        QueryResult queryRes = Tools.getDataFromClusterRootNode(args);
        MatrixS a= null;  
        int root = 0;
        if (MPI.COMM_WORLD.getRank() == 0) {            
            a=(MatrixS)queryRes.getData()[0];
            root = (int) queryRes.getData()[1];
        }
        a= ETDmpiUtils.bcastObject(a, root);
        MatrixS[] etd = ETDmpi.ETDParralel(a, ETDpTO.RESULT_LDU);
        
//        VectorS[] resTmp = tropParallel.BellmanEquationPar(a, ring, args, com);
        if (MPI.COMM_WORLD.getRank() == 0){          
            Object[] result = {etd};
            int userID= Integer.valueOf(args[0]);
            int taskID= Integer.valueOf(args[1]);
            QueryCreator qc= new QueryCreator(null,null);
            qc.saveCalcResultOnRootNode(userID, taskID, result);
        }        
        MPI.Finalize();
    }
    
    private static MatrixS[] ETDmodWDKpar(MatrixS T){
        try {
            return ETDmpi.ETDParralel(T, ETDpTO.RESULT_WDK);
        } catch (Exception ex) {
            return null;
        }
    }
    
    private static MatrixS[] ETDmodPLDUQWDKpar(MatrixS T){
        try {
            return ETDmpi.ETDParralel(T, ETDpTO.RESULT_PLDUQWDK);
        } catch (Exception ex) {
            return null;
        }
    }
    
    private static MatrixS[] ETDmodLDUWDKpar(MatrixS T){
        try {
            return ETDmpi.ETDParralel(T, ETDpTO.RESULT_LDUWDK);
        } catch (Exception ex) {
            return null;
        }
    }    
    //</editor-fold>
    
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        MPI.Init(args);
        QueryResult queryRes=Tools.getDataFromClusterRootNode(args);
        Object []ar=queryRes.getData();   
        ETDmpiUtils.resetTime();
        int matrix_size = 123,zeroP = 66,resId = ETDpTO.RESULT_LDU;
        int value_size = Integer.MAX_VALUE;
        int loggin = 0;
        if(ar != null && ar.length==4){
            if(ar[0] instanceof Element){
                matrix_size = ((Element)ar[0]).intValue();
            }else{
                matrix_size = (int) ar[0];
            }
            if(ar[1] instanceof Element){
                zeroP = ((Element)ar[1]).intValue();
            }else{
                zeroP = (int) ar[1];
            }
            if(ar[2] instanceof Element){
                resId = ((Element)ar[2]).intValue();
            }else{
                resId = (int) ar[2];
            }
            if(ar[3] instanceof Element){
                loggin = ((Element)ar[3]).intValue();
            }else{
                loggin = (int) ar[3];
            }
        }
        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        if(resId>ETDpTO.RESULT_PLDUQWDK || resId<ETDpTO.RESULT_LDU){
            resId = ETDpTO.RESULT_LDU;
        }
        MatrixS T = null;
        int tlo[] = new int[]{loggin};
        MPI.COMM_WORLD.bcast(tlo, tlo.length, MPI.INT, 0);
        loggin = tlo[0];
        if(loggin == 1){
           ETDmpiUtils.setLogging(true);
        }
        if(myrank == 0){
            T =  ETDUtils.randomMatrixS(matrix_size, value_size, zeroP, Ring.ringZxyz);
            System.out.print("Matrix size = "+matrix_size+"\n");
            System.out.print("Zero elements = "+zeroP+"%"+"\n");
        }
        T = (MatrixS) ETDmpiUtils.bcastObject(T, 0);
        long timeZero = System.currentTimeMillis();
        MatrixS[] LDUParralel = ETDParralel(T,ETDpTO.RESULT_LDU);
        long ldutime = System.currentTimeMillis()-timeZero;
        System.out.print("ldu time on proc "+myrank+" is "+ldutime+"\n");
        System.out.print("dets check time on proc "+myrank+" is "+time+"\n");
        System.out.print("recovery time on proc "+myrank+" is "+timeExchAndRecoveryAndBack+"\n"); 
        System.out.print("s/r time on proc "+myrank+" is "+ETDmpiUtils.getTime()+"\n"); 
        MPI.Finalize();
    }
}
