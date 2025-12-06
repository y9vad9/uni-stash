package  com.mathpar.parallel.stat.FMD;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import  com.mathpar.matrix.MatrixD;
import  com.mathpar.matrix.file.dense.FileMatrixD;

import mpi.MPI;
import mpi.MPIException;
import  com.mathpar.number.NumberZp32;
import  com.mathpar.number.Ring;
import com.mathpar.parallel.stat.FMD.MultFMatrix.Multiplay;
import com.mathpar.parallel.stat.FMD.MultFMatrix.SendReciveFileMatrixL;
import com.mathpar.matrix.file.sparse.SFileMatrix;
import com.mathpar.matrix.file.sparse.SFileMatrixS;
import  com.mathpar.parallel.utils.MPITransport;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import mpi.Status;

/**
 *
 * @author galina
 */ // mpirun
// mpirun -np 4 java -cp /home/galina/NetBeansProjects/mathpar/mathpar/target/classes com.mathpar.CalcMath.FileMatrixMul8 4
public class FileMatrixMul8 implements Serializable {
    public static void Mul142(FileMatrixD fmA, FileMatrixD fmB,File path, Ring ring)
            throws MPIException, IOException, ClassNotFoundException, Exception {
        Transport<FileMatrixD> t = new Transport<FileMatrixD>(FileMatrixD.class);
        
       // File f = new File("/home/galina/matrixes/tmpA");
        FileMatrixD[] DD = new FileMatrixD[4];        
        
        int rank = MPI.COMM_WORLD.getRank();
        int tag1 = 0;
        int tag2 = 1;
        if (rank == 0) {            
            FileMatrixD D = null;
            FileMatrixD[] fmAA = fmA.split();
            FileMatrixD[] fmBB = fmB.split();
            t.send(fmAA[1], 1, tag1);
            t.send(fmBB[2], 1, tag2);
            t.send(fmAA[0], 2, tag1);
            t.send(fmBB[1], 2, tag2);
            t.send(fmAA[1], 3, tag1);
            t.send(fmBB[3], 3, tag2);
            t.send(fmAA[2], 4, tag1);
            t.send(fmBB[0], 4, tag2);
            t.send(fmAA[3], 5, tag1);
            t.send(fmBB[2], 5, tag2);
            t.send(fmAA[2], 6, tag1);
            t.send(fmBB[1], 6, tag2);
            t.send(fmAA[3], 7, tag1);
            t.send(fmBB[3], 7, tag2);
            DD[0] = fmAA[0].multCU(fmBB[0], 1000).add(t.recv(1, 3), 1000);
            DD[1] = (FileMatrixD) t.recv(2, 3);
            DD[2] = (FileMatrixD) t.recv(4, 3);
            DD[3] = (FileMatrixD) t.recv(6, 3);
            D = FileMatrixD.joinCopy(DD, false, path, ring);
            
        } else {

            FileMatrixD a1 = t.recv(0, tag1);
            FileMatrixD a2 = t.recv(0, tag2);
            FileMatrixD res = a1.multCU(a2, 1000);
            if (rank % 2 == 0) {
                FileMatrixD p = res.add((FileMatrixD) t.recv(rank + 1, 3), 1000);
                t.send(p, 0, 3);
            } else {
                t.send(res, rank - 1, 3);
            }
        }
        
    }
    public static void main(String[] args) throws MPIException,IOException, ClassNotFoundException, Exception {
        String[] chMod = {"rm", "-R","/home/galina/matrixes/"};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", "/home/galina/matrixes/"};
        Process chmod1 = Runtime.getRuntime().exec(chMod);
        chmod1.waitFor();
        MPI.Init(new String[0]);        
        File f1 = new File("/home/galina/matrixes/tmp2");
        File f2 = new File("/home/galina/matrixes/tmp3");
        File f3 = new File("/home/galina/matrixes/tmpR");       
        Ring ring = new Ring("Z[x,y,z]");
        int m = 8, n = 8;
        int rank = MPI.COMM_WORLD.getRank();
        FileMatrixD fmA=null,fmB=null;
        if (rank==0) {
            fmA = new FileMatrixD(f1, 2, m, n, 10000, new Random(), 5, ring);
            System.out.println("A = " + fmA.toMatrixD().toString(ring));
            fmB = new FileMatrixD(f2, 2, n, m, 10000, new Random(), 5, ring);
            System.out.println("B = " + fmB.toMatrixD().toString(ring));
        }
        Mul142(fmA,fmB, f3, ring);
        if (rank==0) {
            FileMatrixD fmR = new FileMatrixD (f3);
            System.out.println("R = " + fmR.toMatrixD().toString(ring));
        }
        MPI.Finalize();
    }
}

class Transport<T extends FileMatrixD> {
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
        return SendReciveFileMatrixL.INSTANCE.RecvD(rank, tag, construct);
    }
}