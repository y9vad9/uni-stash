package com.mathpar.students.OLD.curse5_2015.korabelnikov_kurdymova;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.logging.Level;
import com.mathpar.matrix.MatrixS;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.sparse.*;
import mpi.MPI;
import mpi.MPIException;
import com.mathpar.number.*;
import com.mathpar.parallel.ddp.engine.*;
import com.mathpar.parallel.stat.FMD.MultFMatrix.*;
import com.mathpar.parallel.utils.MPITransport;


public class TaskMult extends AbstractTask{

    static int[] primes = null;
    static Ring ring = null;
    static NumberZ mod = null;
    static int size_of_little_matrix;
    static int counter = 0;



    SFileMatrixS A = null;
    SFileMatrixS B = null;
    SFileMatrixS AB = null;

    @Override
    public void SetStartTask(String[] args, Object[] data) {
        A = (SFileMatrixS)data[0];
        B = (SFileMatrixS)data[1];
        try {
           // System.out.println("A = "+A.toMatrixS()+"B = "+B.toMatrixS());
        } catch(Exception e) {}
    }

    @Override
    public boolean IsLittleTask() {
        try {
            int[] arr = A.getFullSize();
            if(arr[0] <= size_of_little_matrix ) {
                return true;
            }
        } catch(Exception e) {System.out.println("errlittle   \n "+e);System.exit(123);}
        return false;
    }

    @Override
    public void ProcLittleTask() {
        try{
            MatrixD AA = new MatrixD(A.toMatrixS(), ring);
            MatrixD BB = new MatrixD(B.toMatrixS(), ring);


            MatrixD aa;
            MatrixD bb;
            MatrixD[] cc = new MatrixD[primes[primes.length - 1]];
            Ring r = new Ring("Zp32[]");
            for(int i=0; i < primes[primes.length - 1]; i++) {
                r.setMOD32(primes[i]);
                aa = (MatrixD)AA.toNewRing(Ring.Zp32, r);
                bb = (MatrixD)BB.toNewRing(Ring.Zp32, r);
                cc[i] = (MatrixD) aa.multiply(bb, r);
                cc[i] = (MatrixD) cc[i].toNewRing(Ring.Z, ring);
            }
            MatrixD C;
            MatrixD C_ = new MatrixD(new Element[cc[0].M.length][cc[0].M.length]);
            MatrixD C2;
            C = cc[0];
            for(int i=1; i<primes[primes.length - 1]; i++) {
                for(int z=0; z<C.M.length; z++) {
                    for(int w=0; w<C.M.length; w++) {
                        C_.M[z][w] = C.M[z][w].mod(new NumberZ(primes[i]), ring);
                    }
                }
                C2 = C;
                C = cc[i].subtract(C_, ring);
                for(int j=0; j<i; j++) {
                    C = (MatrixD)C.multiply(new NumberZ(primes[j]).multiply(new NumberZ(primes[j]).modInverse(new NumberZ(primes[i])), ring), ring);
                }
                C = C.add(C2, ring);
            }
            for(int i=0; i<C.M.length; i++) {
                for(int j=0; j<C.M.length; j++) {
                    C.M[i][j] = C.M[i][j].Mod(mod, ring);
                }
            }

            MatrixS CC = new MatrixS(C, ring);
            AB = new SFileMatrixS(0, new File("/home/user/a/c"+MPI.COMM_WORLD.getRank()+"-"+counter), CC, new NumberZ(), ring);
            counter++;

        } catch(Exception e) {System.out.println("errorproc!!!  "+e);System.exit(123);}
    }

    @Override
    public void SendTaskToNode(int node) {
        try {
            Transport<SFileMatrixS> t = new Transport<SFileMatrixS>(SFileMatrixS.class);
            t.send(A, node, 0);
            t.send(B, node, 1);
        } catch(Exception e) {System.out.println("errsendtask");System.exit(123);}
    }

    @Override
    public void RecvTaskFromNode(int node) {
        try {
            Transport<SFileMatrixS> t = new Transport<SFileMatrixS>(SFileMatrixS.class);
            A = t.recv(node, 0);
            B = t.recv(node, 1);
        } catch(Exception e) {System.out.println("errorrecvtask");System.exit(123);}
    }

    @Override
    public void SendResultToNode(int node) {
        try {
            Transport<SFileMatrixS> t = new Transport<SFileMatrixS>(SFileMatrixS.class);
            t.send(AB, node, 2);
        } catch(Exception e) {System.out.println("errsendres");System.exit(123);}
    }

    @Override
    public void GetResultFromNode(int node) {
        try {
            Transport<SFileMatrixS> t = new Transport<SFileMatrixS>(SFileMatrixS.class);
            AB = t.recv(node, 2);
        } catch(Exception e) {System.out.println("errrecvres");System.exit(123);}
    }
    
    public static void main(String[] args) throws Exception{
        MPI.Init(args);

        MatrixS af = new MatrixS(new int[][]{
                {2, 5, 7, 1},
                {1, 5, 8, 2},
                {3, 7, 5, 4},
                {1, 6, 8, 1}}, Ring.ringZxyz);
        MatrixS bf = new MatrixS(new int[][]{
                {1, 6, 8, 2},
                {4, 7, 1, 4},
                {3, 7, 9, 1},
                {5, 1, 2, 8}}, Ring.ringZxyz);
        FactoryMult f = new FactoryMult();
        ring = new Ring("Z[]");
        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        int length = Integer.parseInt(args[1]);
        int n = Integer.parseInt(args[0]);
        int depth = 1;
        while(n > Math.pow(2, depth)) {
            depth += 1;
        }
        size_of_little_matrix = n;
        for(int i=0; size > Math.pow(8, i); i++) {
            size_of_little_matrix /= 2;
        }
        SFileMatrixS A = null;
        SFileMatrixS B = null;
        if (myrank == 0) {
            System.out.println("size_of_little_matrix = "+size_of_little_matrix);
            A = new SFileMatrixS(depth, new File("/home/user/a/a"), n, n, 100, new Random(), new int[]{length}, new NumberZ(), ring);
            B = new SFileMatrixS(depth, new File("/home/user/a/b"), n, n, 100, new Random(), new int[]{length}, new NumberZ(), ring);
        }

        if (myrank == 0) {
            long time = System.currentTimeMillis();
            int block = 1;
            int count = 1;
            Object[] array_of_all_primes = new Object[100];
            primes = NFunctionZ32.readBlockOfPrimesFromBack(1, Ring.ringZxyz);
            int number_of_primes = primes.length;
            array_of_all_primes[0] = primes;
            mod = new NumberZ( primes[0]);
            NumberZ max = (NumberZ)new NumberZ(2).pow(length*(2)+n);//(int)(Math.log(n)/Math.log(2))+1, ring);
            for(int i=1; mod.compareTo(max, ring) < 0; i++) {
                if(i >= primes.length) {
                    i = 0;
                    block++;
                    primes = NFunctionZ32.readBlockOfPrimesFromBack(block, Ring.ringZxyz);
                    array_of_all_primes[block - 1] = primes;
                    number_of_primes += primes.length;
                }
                count++;
                mod = mod.multiply(new NumberZ(primes[i]));
            }
            System.out.println("n = "+count);
            primes = new int[number_of_primes + 1];
            int tmp = 0;
            for(int i=0; array_of_all_primes[i] != null; i++) {
                System.arraycopy(array_of_all_primes[i], 0, primes, tmp, ((int[])array_of_all_primes[i]).length);
                tmp+=((int[])array_of_all_primes[i]).length;
            }


            primes[primes.length - 1] = count;


            MPITransport.bcastObjectArray(new Object[]{primes, mod}, 2, 0);
        } else {
            Object[] obj = new Object[2];
            MPITransport.bcastObjectArray(obj, 2, 0);
            primes = (int[]) obj[0];
            mod = (NumberZ) obj[1];
        }

        long time = System.currentTimeMillis();
        DispThread disp = new DispThread(0, f, 8, 10, args,new Object[]{A, B});

        if (myrank==0){
            System.out.println("time1 = "+(System.currentTimeMillis() - time));

            TaskMult ab = (TaskMult)disp.GetStartTask();
            time = System.currentTimeMillis();
            MatrixS res = ab.A.toMatrixS().multiply(ab.B.toMatrixS(), ring);
            System.out.println("time2 = "+(System.currentTimeMillis() - time));
            MatrixS sub = ab.AB.toMatrixS().subtract(res, ring);
            
            if (sub.isZero(ring)) {
               System.out.println("ok");
            } else{
                System.out.println("error");
           }
        }
        MPI.Finalize();
    }
}
class Transport<T extends SFileMatrix> {
        private final Class<T> cls;

        private Constructor construct;
       public Transport(Class<T> cls) {
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
    
    
    public T recv(int rank, int tag) throws Exception {
        return SendReciveFileMatrixL.INSTANCE.Recv(rank, tag, construct);
    }
}


