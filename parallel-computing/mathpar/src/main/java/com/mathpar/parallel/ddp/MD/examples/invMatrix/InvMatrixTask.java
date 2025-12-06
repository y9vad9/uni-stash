/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.MD.examples.invMatrix;
import com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix.DoubleMatrix;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import mpi.*;

/**
 *
 * @author yuri
 */
public class InvMatrixTask extends AbstractTask{
    DoubleMatrix A,Result;

    public InvMatrixTask(){
        A=new DoubleMatrix();
        Result=new DoubleMatrix();
    }

    public void SetStartTask(String[] args,Object[] data) {
        A.InitRandom(Integer.parseInt(args[0]));
    }

    @Override
    public boolean IsLittleTask() {
        return (A.GetSize()<=1);
    }

    @Override
    public void ProcLittleTask() {
        Result=A.GetRev();
    }

    @Override
    public void SendTaskToNode(int node) {
       try{
            double []tmp=A.ToDoubleArray();
            MPI.COMM_WORLD.send(tmp,tmp.length,MPI.DOUBLE,node,11);
//!!!!            MPI.COMM_WORLD.Send(tmp,0,tmp.length, MPI.DOUBLE,node, 11);
        }catch (MPIException e){};
    }

    @Override
    public void RecvTaskFromNode(int node) {
         try {
            int cnt;
            Status st=MPI.COMM_WORLD.probe(node,11);
//!!!!            Status st=MPI.COMM_WORLD.Probe(node, 11);
            cnt=st.getCount(MPI.DOUBLE);
//!!!!            cnt=st.Get_count(MPI.DOUBLE);
            double []tmp=new double[cnt];
            MPI.COMM_WORLD.recv(tmp,cnt,MPI.DOUBLE,node,11);
//!!!!            MPI.COMM_WORLD.Recv(tmp, 0,cnt,MPI.DOUBLE, node,11);
            A.FillFromDoubleArray(tmp);
        }catch (MPIException e){};
    }

    @Override
    public void SendResultToNode(int node) {
        try{
            double []tmp=Result.ToDoubleArray();
            MPI.COMM_WORLD.send(tmp,tmp.length,MPI.DOUBLE,node,11);
//!!!!            MPI.COMM_WORLD.Send(tmp,0,tmp.length, MPI.DOUBLE,node, 11);
        }catch (MPIException e){};
    }

    @Override
    public void GetResultFromNode(int node) {
         try {
            int cnt;
            Status st=MPI.COMM_WORLD.probe(node,11);
//!!!!            Status st=MPI.COMM_WORLD.Probe(node, 11);
            cnt=st.getCount(MPI.DOUBLE);
//!!!!            cnt=st.Get_count(MPI.DOUBLE);
            double []tmp=new double[cnt];
            MPI.COMM_WORLD.recv(tmp,cnt,MPI.DOUBLE,node,11);
//!!!!            MPI.COMM_WORLD.Recv(tmp, 0,cnt,MPI.DOUBLE, node,11);
            Result.FillFromDoubleArray(tmp);
        }catch (MPIException e){};
    }

}
