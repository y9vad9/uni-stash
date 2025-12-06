package com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix;

import mpi.*;
import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.parallel.ddp.MD.examples.invMatrix.Factory;

// mpirun -np 8 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes parallel.ddp.examples.multiplyDoubleMatrix.MyNodesForDDPExample 64
public class MyNodesForDDPExample {
    public static void main(String[] args) throws MPIException,InterruptedException{
        /*
         * args[0]- size of matrix         
         */
        MPI.Init(args);
        Factory factory=new Factory();
        int []nodes={7,4,5,1};
        int myRank=MPI.COMM_WORLD.getRank();
        boolean fl=false;
        for(int i=0; i<nodes.length; i++){
            if (myRank==nodes[i]){
                fl=true;
            }
        }
        if (fl){
            DispThread disp=new DispThread(0, factory,2, 5, args,null,nodes);

            if (myRank==nodes[0]){
                MultonMatrixTask t=(MultonMatrixTask)disp.GetStartTask();
                DoubleMatrix D=t.A.Multon(t.B);
                System.out.println("DDP done at "+disp.GetExecuteTime());
                if (D.Compare(t.C)){
                    System.out.println("Result is correct");
                }
                else {
                    System.out.println("Result is wrong");
                }
            }
        }
        MPI.Finalize();
    }

}
