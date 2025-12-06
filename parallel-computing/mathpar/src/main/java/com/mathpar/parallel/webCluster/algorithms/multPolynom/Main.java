package com.mathpar.parallel.webCluster.algorithms.multPolynom;


import com.mathpar.number.Ring;
import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import mpi.*;
import com.mathpar.parallel.webCluster.engine.Tools;
import com.mathpar.polynom.Polynom;

// mpirun -np 2 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.webCluster.algorithms.multPolynom.Main

/*

A=x^2+3y;
B=x^2+3y+3z;
\polMultPar(A, B);

*/
public class Main {
    public static void main(String[] args) throws MPIException, InterruptedException {
        MPI.Init(args);
        FactoryMultiplyPolynom f = new FactoryMultiplyPolynom();
        DispThread disp = new DispThread(0, f, 2, 10, args,Tools.getDataFromClusterRootNode(args).getData());
        if (MPI.COMM_WORLD.getRank() == 0) {
            TaskMultiplyPolynom startT = (TaskMultiplyPolynom) disp.GetStartTask();
            Object[] result = {startT.c};
            int userID= Integer.valueOf(args[0]);
            int taskID= Integer.valueOf(args[1]);
            QueryCreator qc= new QueryCreator(null,null);
            qc.saveCalcResultOnRootNode(userID, taskID, result);
        }
       
        MPI.Finalize();
    }
}
