/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import mpi.*;


public class MultonMatrixTask extends AbstractTask{

    public DoubleMatrix A,B,C;

    public MultonMatrixTask() {
        A=new DoubleMatrix();
        B=new DoubleMatrix();
        C=new DoubleMatrix();
    }


    public boolean IsLittleTask() {
        return (A.n<=16);
    }

    public void ProcLittleTask() {
        C=A.Multon(B);
    }

    public void SetStartTask(String []args,Object []data){
        int size=Integer.parseInt(args[0]);
        A.InitRandom(size);
        B.InitRandom(size);
    }
    public void SendTaskToNode(int node){
        try{
            double []tmp=A.ToDoubleArray();
            MPI.COMM_WORLD.send(tmp,tmp.length,MPI.DOUBLE,node,11);
            tmp=B.ToDoubleArray();
            MPI.COMM_WORLD.send(tmp,tmp.length,MPI.DOUBLE,node,12);
        }catch (MPIException e){};
    }
    public void RecvTaskFromNode(int node){
        try {
            int cnt;
            Status st=MPI.COMM_WORLD.probe(node,11);
            cnt=st.getCount(MPI.DOUBLE);
            double []tmp=new double[cnt];
            MPI.COMM_WORLD.recv(tmp,cnt,MPI.DOUBLE,node,11);
            A.FillFromDoubleArray(tmp);
            MPI.COMM_WORLD.recv(tmp,cnt,MPI.DOUBLE,node,12);
            B.FillFromDoubleArray(tmp);
        }catch (MPIException e){};
    }
    public void SendResultToNode(int node){
        try{
            double []tmp=C.ToDoubleArray();
            MPI.COMM_WORLD.send(tmp,tmp.length,MPI.DOUBLE,node,11);
        }catch (MPIException e){};
    }

    public void GetResultFromNode(int node){
        try {
            int cnt;
            Status st=MPI.COMM_WORLD.probe(node,11);
            cnt=st.getCount(MPI.DOUBLE);
            double []tmp=new double[cnt];
            MPI.COMM_WORLD.recv(tmp,cnt,MPI.DOUBLE,node,11);
            C.FillFromDoubleArray(tmp);
        }catch (MPIException e){};
    }
}
