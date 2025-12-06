/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.algorithms.tropic;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import com.mathpar.parallel.webCluster.engine.QueryResult;
import com.mathpar.parallel.webCluster.engine.Tools;
import mpi.MPI;
import mpi.Intracomm;
/**
 *\BellmanInequalityPar(A); Ax<=x
 * @author serega
 */

/*

SPACE = R64MaxPlus[x];
A=[[0,-2,0,0],[0,0,3,-1],[-1,0,0,-4],[2,0,0,0]];
\BellmanInequalityPar(A);

*/
public class bellmanInequality1 {
    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        QueryResult queryRes = Tools.getDataFromClusterRootNode(args);
        Intracomm com = MPI.COMM_WORLD;
        /*Ring ring = new Ring("R64MaxPlus[x]");        
        Element[][] aa1 = new Element[][] {{new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(-2)), ring.numberZERO, ring.numberZERO},
        {ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(3)), new NumberR64MaxPlus(new NumberR64(-1))},
        {new NumberR64MaxPlus(new NumberR64(-1)), ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO), new NumberR64MaxPlus(new NumberR64(-4))},
        {new NumberR64MaxPlus(new NumberR64(2)), ring.numberZERO, ring.numberZERO, new NumberR64MaxPlus(NumberR64.ZERO)}};
        MatrixD a = new MatrixD(aa1);*/
        MatrixS a=null;         
        Ring ring=null;
        if (MPI.COMM_WORLD.getRank() == 0) {            
            a=(MatrixS)queryRes.getData()[0];
            ring=(Ring)queryRes.getData()[1];            
        }
        ring=(Ring)MPITransport.bcastObject(ring, 0);
        a=(MatrixS)MPITransport.bcastObject(a, 0);
        MatrixS res=tropParallel.BellmanInequalityPar(a, ring, args, com);
        if (MPI.COMM_WORLD.getRank() == 0){           
            System.out.println("BellmanInequality1");
            Object[] result = {res};
            int userID= Integer.valueOf(args[0]);
            int taskID= Integer.valueOf(args[1]);
            QueryCreator qc= new QueryCreator(null,null);
            qc.saveCalcResultOnRootNode(userID, taskID, result);
        }        
        MPI.Finalize();
    }
}
