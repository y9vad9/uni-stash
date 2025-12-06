/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.matrix.file;

import com.mathpar.matrix.file.dense.FileMatrixD;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.parallel.stat.FMD.MultFMatrix.Multiplay;
import com.mathpar.parallel.stat.FMD.MultFMatrix.SendReciveFileMatrixL;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import mpi.*;
/**
 *
 * @author r1d1
 */

//mpirun -np 2 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.matrix.file.ZeroDepthTest
public class ZeroDepthTest {
    static String matrixFold="/tmp/mA";
    public static void main(String[] args) throws Exception{
        //NumberZ number = new NumberZ(5);
        //sendRecvTest(args);
        addTest();
    }
    
    public static void addTest() throws Exception{
        String[] chMod = {"rm", "-R", matrixFold};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", matrixFold};
        Process chmod1 = Runtime.getRuntime().exec(chMod1);
        chmod1.waitFor();
        File f1 = new File(matrixFold+File.separatorChar+"A");
        File f2 = new File(matrixFold+File.separatorChar+"B");
        File f3 = new File(matrixFold+File.separatorChar+"C");
        int m=2,nBits=10;
        int n=m;
        FileMatrixD fmA=new FileMatrixD(f1, 0, m, n, nBits);
        FileMatrixD fmB=new FileMatrixD(f2, 0, m, n, nBits);
        FileMatrixD fmC=fmA.add(fmB, f3);        
        System.out.println("A=\n"+fmA.toMatrixD().toString(Ring.ringZxyz));
        System.out.println("B=\n"+fmB.toMatrixD().toString(Ring.ringZxyz));
        System.out.println("A+B=\n"+fmC.toMatrixD().toString(Ring.ringZxyz));
    }
    public static void sendRecvTest(String[] args) throws Exception{
        String[] chMod = {"rm", "-R", matrixFold};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", matrixFold};
        Process chmod1 = Runtime.getRuntime().exec(chMod1);
        chmod1.waitFor();
        MPI.Init(args);
        int myRank=MPI.COMM_WORLD.getRank();
        File f = new File(matrixFold+File.separatorChar+"A"+String.valueOf(myRank));        
        int m=2,nBits=10;
        int n=m;        
        Transport2<FileMatrixD> t = new Transport2<FileMatrixD>(FileMatrixD.class);
        if (myRank==0){
            FileMatrixD fmA=new FileMatrixD(f, 0, m, n, nBits);        
            System.out.println("generated A=\n"+fmA.toMatrixD().toString(Ring.ringZxyz));
            t.send(fmA, 1, 0);            
        }
        if (myRank==1){            
            FileMatrixD fmA=t.recv(0, 0);            
            System.out.println("received A=\n"+fmA.toMatrixD().toString(Ring.ringZxyz));
        }
        MPI.Finalize();
    }
}

class Transport2<T extends FileMatrixD> {
    private final Class<T> cls;

    private Constructor construct;

    public Transport2(Class<T> cls) {
        this.cls = cls;
        try {
            construct = cls.getConstructor(java.io.File.class, int.class);
        } catch (Exception ex) {
            System.out.println("lf//////////////////////////////");
            java.util.logging.Logger.getLogger(Multiplay.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void send(T A, int rank, int tag) throws Exception {
        SendReciveFileMatrixL.INSTANCE.SendFM(A, rank, tag);
    }
    
    public void SendBlockFM(T A, int blockNumb, int rank, int tag) throws Exception {
        SendReciveFileMatrixL.INSTANCE.sendBlockFM(A, blockNumb, rank, tag);
    }

    public T recv(int rank, int tag) throws Exception {
        return SendReciveFileMatrixL.INSTANCE.RecvD(rank, tag, construct);
    }
    
    public  T RecvBlockFMHowNewMatrix(File f, final int proc, final int tag) throws Exception{
        return SendReciveFileMatrixL.INSTANCE.RecvBlockFMHowNewMatrix(f, proc, tag,construct);
    }
    
    public void SendFMWithSingleDepthHowMatrixPart(final T m, final int proc, final int tag) throws Exception{
        SendReciveFileMatrixL.INSTANCE.SendFMWithSingleDepthHowMatrixPart(m, proc, tag);
    }
    
    public void RecvBlockFMHowPartOfMatrix(File f, int blockNumb, int depth, final int proc, final int tag)throws Exception{
        SendReciveFileMatrixL.INSTANCE.RecvBlockFMHowPartOfMatrix(f, blockNumb, depth, proc, tag, construct);
    }
}

