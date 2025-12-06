/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.algorithms.tropic;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.*;
import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.parallel.webCluster.algorithms.tropic.multiplyMatrix.FactoryMultiplyMatrix;
import com.mathpar.parallel.webCluster.algorithms.tropic.multiplyMatrix.TaskMultiplyMatrix;
import mpi.MPI;
/**
 *
 * @author serega
 */

// mpirun -np 2 java -cp /home/serega/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.ddp.tropic.exampl
public class exampl {
    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        Ring ring= new Ring("R64MaxPlus[x]");
        int myRank=MPI.COMM_WORLD.getRank();
        long[][] aa ;
        long[][] aa2;
        MatrixS a=null;
        MatrixS b=null;
        if (myRank==0){
            int n=4; 
            aa= new long[n][n];
            aa2= new long[n][n];
            for(int i=0; i<n; i++){
                for(int j=0; j<n; j++){
                    //double a1=Math.random();
                    aa[i][j]=2;//Math.round(-100*a1);
                    //double a2=Math.random();
                    aa2[i][j]=3;//Math.round(100*a2);
                }
                //aa[i][i]=0;
                
            }
            a = new MatrixS(aa,ring);
            b = new MatrixS(aa2,ring);
        }        
        Object[]params=new Object[3];
        if (myRank==0){
            params[0]=new Integer(4);
            params[1]=a;
            params[2]=b;
        }
        FactoryMultiplyMatrix f = new FactoryMultiplyMatrix();
        DispThread disp = new DispThread(0, f, 2, 10, args,params);
        if (myRank==0){
            TaskMultiplyMatrix ab = (TaskMultiplyMatrix)disp.GetStartTask();
            System.out.println(ab.c.toString(ring));
            
            MatrixS c =a.multiply(b, ring);
            System.out.println(""+c.toString(ring));
        }
        
        
        MPI.Finalize();
    }
}
