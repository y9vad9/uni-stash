package com.mathpar.students.OLD.curse5_2015.tatarintsev;

import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;

public class TaskMultiplyMatrix extends AbstractTask {

    public MatrixD a, b, c;
    public MatrixD[] ab;
    public MatrixD[] bb;

    public TaskMultiplyMatrix() {
        a = new MatrixD();
        b = new MatrixD();
        c = new MatrixD();
    }


    @Override
    public void SetStartTask(String[] args, Object[] data) {
        a = (MatrixD) data[0];
        b = (MatrixD) data[1];
    }

    @Override
    public boolean IsLittleTask() {
        return (a.M.length <= Main.size_of_little_matrix);
    }

    @Override
    public void ProcLittleTask() {
        if(a.M.length < 256) {
            c = (MatrixD) a.multiply(b, Main.ring);
        } else {
            c = (MatrixD) a.multS(b, Main.ring);
        }
    }

    @Override
    public void SendTaskToNode(int node) {
        try {
           Object[] o = new Object[]{a,b};
            MPITransport.sendObjectArray(o,0,o.length,node,12);
        }
        catch (Exception e) {
        }
    }

    @Override
    public void RecvTaskFromNode(int node) {
        try {
            Object[] o = new Object[2];
            MPITransport.recvObjectArray(o,0,2,node,12);
            a=(MatrixD)o[0];
            b=(MatrixD)o[1];
        }catch (Exception e){}
    }

    @Override
    public void SendResultToNode(int node) {
        try{
            Object[] o = new Object[]{c};
            MPITransport.sendObjectArray(o,0,o.length,node,11);
        }catch (Exception e){}
    }

    @Override
    public void GetResultFromNode(int node) {
        try {
            Object[] o=new Object[1];
            MPITransport.recvObjectArray(o,0,1,node,11);
            c=(MatrixD)o[0];
        }catch (Exception e){}
    }
}
