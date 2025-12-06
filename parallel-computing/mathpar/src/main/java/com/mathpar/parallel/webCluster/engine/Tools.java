/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.webCluster.engine;

import mpi.MPI;
import mpi.MPIException;

/**
 *
 * @author r1d1
 */
public class Tools {
    public static QueryResult getDataFromClusterRootNode(String []args) throws MPIException{
        int myRank = MPI.COMM_WORLD.getRank();
        QueryResult getDataRes=new QueryResult();
        if (myRank==0){
            int userID= Integer.valueOf(args[0]);
            int taskID= Integer.valueOf(args[1]);
            QueryCreator qc= new QueryCreator(null,null);
            getDataRes= qc.getTaskDataForUser(userID, taskID);
            if (getDataRes.resultState!=AlgorithmsConfig.RES_SUCCESS){
                System.err.println("Could not to obtain task data from server.");
                System.err.println(AlgorithmsConfig.respNames[getDataRes.resultState]);
            }
        }
        return getDataRes;
    }

    public static void sendFinishMessage(String []args) throws MPIException{
        int myRank = MPI.COMM_WORLD.getRank();
        if (myRank==0){
            int userID= Integer.valueOf(args[0]);
            int taskID= Integer.valueOf(args[1]);
            QueryCreator qc= new QueryCreator(null,null);            
            qc.sendMessageAboutFinish(userID, taskID);
        }
    }
}
