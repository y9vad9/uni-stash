package com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix;

import mpi.*;
import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.parallel.ddp.MD.examples.invMatrix.Factory;
/*
 mpirun -np 4 java -cp $HOME/openmpi/lib/mpi.jar:/home/r1d1/NetBeansProjects/mathpar/target/classes DDPExamples.MultonMatrix.Main 64
 mpjrun.sh -np 4 -cp /home/r1d1/NetBeansProjects/mathpar/target/classes DDPExamples.MultonMatrix.Main 64
 */

// mpirun -np 8 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.ddp.examples.multiplyDoubleMatrix.Main 64
public class Main {
    public static void main(String[] args) throws MPIException,InterruptedException{
        /*
         * args[0]- size of matrix
         */
        MPI.Init(args);
        Factory factory=new Factory();        
        int myRank=MPI.COMM_WORLD.getRank();
        
        
        DispThread disp=new DispThread(0, factory,2, 5, args,null);

        if (myRank==0){
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
        
        MPI.Finalize();
    }

}
