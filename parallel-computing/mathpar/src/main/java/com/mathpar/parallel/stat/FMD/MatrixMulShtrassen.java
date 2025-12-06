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

import  com.mathpar.parallel.utils.MPITransport;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import mpi.Status;

/**
 *
 * @author Agapov Andrei
 */ // mpirun
// mpirun -np 7 java -cp /home/galina/NetBeansProjects/mathpar/mathpar/target/classes com.mathpar.students.student.agapov.MatrixMulShtrassen
public class MatrixMulShtrassen implements Serializable{
    public static void MulShtrassen(FileMatrixD fmA, FileMatrixD fmB, File f3, Ring ring)
            throws MPIException, IOException, ClassNotFoundException, Exception {
        Transport<FileMatrixD> t = new Transport<FileMatrixD>(FileMatrixD.class);
        FileMatrixD[] C = new FileMatrixD[4];
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == 0) { 
            FileMatrixD D = null;
            FileMatrixD[] fmAA=fmA.split(); 
            FileMatrixD[] fmBB=fmB.split();
            
            t.send(fmAA[2], 1, 1);
            t.send(fmAA[3], 1, 2);
            t.send(fmBB[0], 1, 3);
            t.send(fmAA[0], 2, 1);
            t.send(fmBB[1], 2, 2);
            t.send(fmBB[3], 2, 3);
            t.send(fmAA[3], 3, 1);
            t.send(fmBB[2], 3, 2);
            t.send(fmBB[0], 3, 3);
            t.send(fmAA[0], 4, 1);
            t.send(fmAA[1], 4, 2);
            t.send(fmBB[3], 4, 3);
            t.send(fmAA[2], 5, 1);
            t.send(fmAA[0], 5, 2);
            t.send(fmBB[0], 5, 3);
            t.send(fmBB[1], 5, 4);
            t.send(fmAA[1], 6, 1);
            t.send(fmAA[3], 6, 2);
            t.send(fmBB[2], 6, 3);
            t.send(fmBB[3], 6, 4);
            
            FileMatrixD M1 = fmAA[0].add(fmAA[3], 1000);
            FileMatrixD M2 = fmBB[0].add(fmBB[3], 1000);
            
            FileMatrixD P = M1.multCU(M2, 1000);
            t.send(P, 5, 55);
            
            FileMatrixD P3 = t.recv(3, 10);
            FileMatrixD P4 = t.recv(4, 20);
            FileMatrixD P6 = t.recv(6, 30);
            
            C[0]=P.add(P3, 1000).subtract(P4, 1000).add(P6, 1000);
            C[1]= (FileMatrixD) t.recv(2, 101);
            C[2]= (FileMatrixD) t.recv(3, 102);
            C[3]= (FileMatrixD) t.recv(5, 103);
            D = FileMatrixD.joinCopy(C, false, f3, ring);
        }
        if (rank==1) {
            FileMatrixD a3 =  t.recv(0, 1);
            FileMatrixD a4 =  t.recv(0, 2);
            FileMatrixD b1 =  t.recv(0, 3);
            FileMatrixD P = (a3.add(a4, 1000)).multCU(b1, 1000);
            t.send(P, 3, 13);
            t.send(P, 5, 15);
        }
        if (rank==2) {
            FileMatrixD a0 =  t.recv(0, 1);
            FileMatrixD b1 =  t.recv(0, 2);
            FileMatrixD b3 =  t.recv(0, 3);
            FileMatrixD P = a0.multCU((b1.subtract(b3, 1000)), 1000);
            t.send(P, 5, 25);
            FileMatrixD res = P.add(t.recv(4, 22), 1000);
            t.send(res, 0, 101);
        }
        if (rank==3) {
            FileMatrixD a3 =  t.recv(0, 1);
            FileMatrixD b2 =  t.recv(0, 2);
            FileMatrixD b0 =  t.recv(0, 3);
            FileMatrixD P = a3.multCU((b2.subtract(b0, 1000)), 1000);
            t.send(P, 0, 10);
            FileMatrixD res = P.add(t.recv(1, 13), 1000);
            t.send(res, 0, 102);
        }
        if (rank==4) {
            FileMatrixD a0 =  t.recv(0, 1);
            FileMatrixD a1 =  t.recv(0, 2);
            FileMatrixD b3 =  t.recv(0, 3);
            FileMatrixD P = (a0.add(a1, 1000)).multCU(b3, 1000);
            t.send(P, 0, 20);
            t.send(P, 2, 22);
        }
        if (rank==5) {
            FileMatrixD a2 =  t.recv(0, 1);
            FileMatrixD a0 =  t.recv(0, 2);
            FileMatrixD b0 =  t.recv(0, 3);
            FileMatrixD b1 =  t.recv(0, 4);
            FileMatrixD P = (a2.subtract(a0, 1000)).multCU(b0.add(b1, 1000), 1000);
            FileMatrixD res = P.add(t.recv(0, 55).add(t.recv(2, 25).subtract(t.recv(1, 15), 1000), 1000), 1000);
            t.send(res, 0, 103);
        }
        if (rank==6) {
            FileMatrixD a1 =  t.recv(0, 1);
            FileMatrixD a3 =  t.recv(0, 2);
            FileMatrixD b2 =  t.recv(0, 3);
            FileMatrixD b3 =  t.recv(0, 4);
            FileMatrixD P = (a1.subtract(a3, 1000)).multCU(b2.add(b3, 1000), 1000);
            t.send(P, 0, 30);
        }

    }
    public static void main(String[] args) throws Exception {
        String[] chMod = {"rm", "-R","/home/galina/mA/"};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", "/home/galina/mA/"};
        Process chmod1 = Runtime.getRuntime().exec(chMod);
        chmod1.waitFor();
        MPI.Init(new String[0]);        
        File f1 = new File("/home/galina/mA/tmp2");
        File f2 = new File("/home/galina/mA/tmp3");
        File f3 = new File("/home/galina/mA/tmpR");       
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
        MulShtrassen(fmA,fmB, f3, ring);
        if (rank==0) {
            FileMatrixD fmR = new FileMatrixD (f3);
            System.out.println("R = " + fmR.toMatrixD().toString(ring));
        }
        MPI.Finalize();
    }
}

