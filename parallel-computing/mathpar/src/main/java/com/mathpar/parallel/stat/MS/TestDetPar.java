/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.MS;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.webCluster.engine.QueryResult;
import com.mathpar.parallel.webCluster.engine.Tools;
import mpi.MPI;

import static com.mathpar.parallel.webCluster.algorithms.Adjoint.AdjointDetParallel.detParallel;
import static com.mathpar.parallel.webCluster.algorithms.Adjoint.AdjointDetParallel.randomMatrixS;

/**
 *
 * @author derbist
 */
public class TestDetPar {
   public static void main(String[] args) throws Exception {
        MPI.Init(args);
        QueryResult queryRes = Tools.getDataFromClusterRootNode(args);
        Ring ring = new Ring("Z[x]");
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == 0) {
            MatrixS rnd = randomMatrixS(8, 100, ring);
            Element det = detParallel(rnd, ring);
            System.out.println("DET = " + det);
            
        } else {
            MatrixS rnd = new MatrixS();
            Element det = detParallel(rnd, ring);
        }
        Tools.sendFinishMessage(args);
        MPI.Finalize();
    } 
}
