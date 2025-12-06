/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.algorithms.tropic.multiplyMatrix;

import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;

public class TaskMultiplyMatrix extends AbstractTask {

    public MatrixS a, b, c;
    public MatrixS[] ab;
    public MatrixS[] bb;
    public Ring ring;
    
    public TaskMultiplyMatrix() {
        a = new MatrixS();
        b = new MatrixS();
        c = new MatrixS();
    }
  

    @Override
    public void SetStartTask(String[] args,Object[] data) {
        int n =(Integer) data[0];
        a = (MatrixS)data[1];
        b = (MatrixS)data[2];
        ring=(Ring)data[3];
    }

    @Override
    public boolean IsLittleTask() {
        return (a.size <= 64);
    }

    @Override
    public void ProcLittleTask() {
        c = a.multiply(b, ring);
    }

    @Override
    public void SendTaskToNode(int node) {
        try {
           Object[] o = new Object[]{a,b,ring};
           MPITransport.sendObjectArray(o,0,o.length,node,12);
        }
        catch (Exception e) {
        }
    }

    @Override
    public void RecvTaskFromNode(int node) {
        try {
            Object[] o = new Object[3];
            MPITransport.recvObjectArray(o,0,o.length,node,12);
            a=(MatrixS)o[0];
            b=(MatrixS)o[1];
            ring=(Ring)o[2];
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
            c=(MatrixS)o[0];
        }catch (Exception e){}
    }
}
