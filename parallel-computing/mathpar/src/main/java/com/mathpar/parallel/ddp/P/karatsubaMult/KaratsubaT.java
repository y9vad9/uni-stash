/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.P.karatsubaMult;

import com.mathpar.parallel.ddp.MD.examples.multiplyMatrix.*;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.polynom.Polynom;
import mpi.MPI;

public class KaratsubaT extends AbstractTask {

    public Polynom a,b,c;
    boolean isIndexesFinded;
    public Polynom a0,a1,b0,b1;
    int reduceValue;
    Ring ring;
    
    public KaratsubaT() {
        a = new Polynom();
        b = new Polynom();
        c = new Polynom();
        isIndexesFinded=false;
        ring =null;
    }
    
    @Override
    public void SetStartTask(String[] args,Object[] data) {        
        int n1 = Integer.parseInt(args[0]);
        int n2 = Integer.parseInt(args[1]);
        ring=new Ring("Z[x,y,z]");
        a=new Polynom(new int[]{n1}, 100, 3, new Random(), ring.numberONE, ring);        
        b=new Polynom(new int[]{n2}, 100, 3, new Random(), ring.numberONE, ring);                
    }

    @Override
    public boolean IsLittleTask() {
        return (Math.min(a.coeffs.length, b.coeffs.length)<=1 || !a.checkMainVars(b));
    }

    @Override
    public void ProcLittleTask() {
        c= a.multiply(b, ring);
    }

    @Override
    public void SendTaskToNode(int node) {
        try {
            Object[] o = new Object[]{a,b,ring};
            MPITransport.sendObjectArray(o,0,o.length,node,12);
            int myRank=MPI.COMM_WORLD.getRank();
            System.out.println("task sendet from "+myRank+" to node "+node);
        }
        catch (Exception e) {
        }
    }

    @Override
    public void RecvTaskFromNode(int node) {
        try {
            Object[] o = new Object[3];
            MPITransport.recvObjectArray(o,0,3,node,12);
            a=(Polynom)o[0];
            b=(Polynom)o[1];
            ring =(Ring)o[2];
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
