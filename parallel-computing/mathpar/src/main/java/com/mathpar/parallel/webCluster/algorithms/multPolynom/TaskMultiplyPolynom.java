package com.mathpar.parallel.webCluster.algorithms.multPolynom;

import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.polynom.Polynom;



public class TaskMultiplyPolynom extends AbstractTask {

    public Polynom a, b, c;
    public Polynom[] ab;
    public Polynom[] bb;
    public Ring ring;
    
    public TaskMultiplyPolynom() {
        a = new Polynom();
        b = new Polynom();
        c = new Polynom();
    }
   
    @Override
    public void SetStartTask(String[] args,Object []data) {
        a=(Polynom)data[0];
        b=(Polynom)data[1];
        ring =(Ring)data[2];
        System.out.println("used ring: "+ring.toString());     
    }

    @Override
    public boolean IsLittleTask() {
        return (Math.min(a.coeffs.length, b.coeffs.length) <= 30);
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
            a=(Polynom)o[0];
            b=(Polynom)o[1];
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
            c=(Polynom)o[0];
        }catch (Exception e){}
    }
}
