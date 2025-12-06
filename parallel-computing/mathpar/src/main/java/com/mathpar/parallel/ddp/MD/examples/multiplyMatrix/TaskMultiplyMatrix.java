/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.MD.examples.multiplyMatrix;

import com.mathpar.parallel.ddp.engine.AbstractTask;
import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;

public class TaskMultiplyMatrix extends AbstractTask {

    public MatrixS a, b, c;
    public MatrixS[] ab;
    public MatrixS[] bb;

    public TaskMultiplyMatrix() {
        a = new MatrixS();
        b = new MatrixS();
        c = new MatrixS();
    }
    public MatrixS init(int n) {
        NumberR[][] mat = new NumberR[n][n];
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = new NumberR(r.nextInt(10));
            }
        }
        MatrixS matS = new MatrixS(mat,Ring.ringZxyz);
        return matS;
    }

    @Override
    public void SetStartTask(String[] args,Object[] data) {
        int n = Integer.parseInt(args[0]);
        a = init(n);
        b = init(n);
    }

    @Override
    public boolean IsLittleTask() {
        return (a.size <= 1);
    }

    @Override
    public void ProcLittleTask() {
        c = a.multiply(b, Ring.ringZxyz);
    }

    @Override
    public void SendTaskToNode(int node) {
        try {
           Object[] o = new Object[]{a,b};
            MPITransport.sendObjectArray(o,0,o.length,node,12);
//!!!!           MPI.COMM_WORLD.Send(o, 0, o.length, MPI.OBJECT, node, 12);
        }
        catch (Exception e) {
        }
    }

    @Override
    public void RecvTaskFromNode(int node) {
        try {
            Object[] o = new Object[2];
            MPITransport.recvObjectArray(o,0,2,node,12);
//!!!!            MPI.COMM_WORLD.Recv(o, 0,2,MPI.OBJECT, node,12);
            a=(MatrixS)o[0];
            b=(MatrixS)o[1];
        }catch (Exception e){}
    }

    @Override
    public void SendResultToNode(int node) {
        try{
            Object[] o = new Object[]{c};
            MPITransport.sendObjectArray(o,0,o.length,node,11);
//!!!!            MPI.COMM_WORLD.Send(o,0,o.length, MPI.OBJECT,node, 11);
        }catch (Exception e){}
    }

    @Override
    public void GetResultFromNode(int node) {
        try {
            Object[] o=new Object[1];
            MPITransport.recvObjectArray(o,0,1,node,11);
//!!!!            MPI.COMM_WORLD.Recv(o, 0,1,MPI.OBJECT, node,11);
            c=(MatrixS)o[0];
        }catch (Exception e){}
    }
}
