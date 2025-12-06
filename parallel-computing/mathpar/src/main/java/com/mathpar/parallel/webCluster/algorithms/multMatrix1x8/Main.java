package com.mathpar.parallel.webCluster.algorithms.multMatrix1x8;


import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import mpi.*;
import com.mathpar.parallel.webCluster.engine.Tools;
import java.util.Random;

// mpirun -np 2 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.webCluster.algorithms.multMatrix1x8.Main

/*

\TOTALNODES = 3;
\PROCPERNODE = 4;
A=[[0,1],[2,3]];
B=[[0,1],[2,3]];
\matMultPar1x8(A, B);

*/
public class Main {
    public static void main(String[] args) throws MPIException, InterruptedException {
        MPI.Init(args);
        FactoryMultiplyMatrix f = new FactoryMultiplyMatrix();
        DispThread disp = new DispThread(0, f, 2, 10, args,Tools.getDataFromClusterRootNode(args).getData());
        if (MPI.COMM_WORLD.getRank() == 0) {
            TaskMultiplyMatrix startT = (TaskMultiplyMatrix) disp.GetStartTask();
            Object[] result = {startT.c};
            int userID= Integer.valueOf(args[0]);
            int taskID= Integer.valueOf(args[1]);
            QueryCreator qc= new QueryCreator(null,null);
            qc.saveCalcResultOnRootNode(userID, taskID, result);
        }        
        MPI.Finalize();
    }
}
