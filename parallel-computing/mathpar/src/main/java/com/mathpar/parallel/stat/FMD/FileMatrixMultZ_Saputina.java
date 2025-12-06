package com.mathpar.parallel.stat.FMD;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.dense.FileMatrixD;
import com.mathpar.matrix.file.sparse.SFileMatrix;
import com.mathpar.matrix.file.sparse.SFileMatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;

import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import com.mathpar.parallel.stat.FMD.MultFMatrix.Multiplay;
import com.mathpar.parallel.stat.FMD.MultFMatrix.SendReciveFileMatrixL;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.parallel.webCluster.engine.QueryResult;
import com.mathpar.parallel.webCluster.engine.Tools;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;



/**
 *
 * @author TGU_410
 */ // mpirun
//args: mSize, minSize, nBits
// mpirun -np 8 java -cp /home/galina/NetBeansProjects/mathpar/mathpar/target/classes com.mathpar.CalcMath.FileMatrixMultZ_Saputina 4 1
// mpirun -np 8 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.stat.FMD.FileMatrixMultZ_Saputina 4 1 30
public class FileMatrixMultZ_Saputina implements Serializable {

    
    static String logName="/home/r1d1/FMlogs";
    static String matrixFold="/tmp/mA";
    static boolean PRINT_LOGS=false;
    
    public static void printLog(int rank, String s) throws Exception{                       
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logName+File.separatorChar+String.valueOf(rank)+".txt", true)));
        out.println(s);
        out.close();        
    }
    
    static int nameCounter=0;
    public static File getNextUnusedFileName(int rank){
        return new File(matrixFold+File.separatorChar+String.valueOf(rank)+"_"+String.valueOf(nameCounter++));
    }
    
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException, Exception {
        //********************Cоздание директорий**************************************  
        MPI.Init(new String[0]);        
        String[] chMod = {"rm", "-R", matrixFold};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", matrixFold};
        Process chmod1 = Runtime.getRuntime().exec(chMod);
        chmod1.waitFor();
        
        int rank = MPI.COMM_WORLD.getRank();        
        if (PRINT_LOGS){            
            if (rank==0){                
                String[] chModLog = {"rm", "-R", logName};
                Process chmodLog = Runtime.getRuntime().exec(chModLog);
                chmodLog.waitFor();
                String[] chMod1Log = {"mkdir", logName};
                Process chmod1Log = Runtime.getRuntime().exec(chMod1Log);
                chmod1Log.waitFor();
            }
            MPI.COMM_WORLD.barrier();
        }
        

        File f1 = new File(matrixFold+File.separatorChar+"A"+String.valueOf(rank));
        File f2 = new File(matrixFold+File.separatorChar+"B"+String.valueOf(rank));        
        Ring ring = new Ring("Z[x,y,z]");                        
        FileMatrixD fmA = null, fmB = null;
        Transport2<FileMatrixD> t = new Transport2<FileMatrixD>(FileMatrixD.class);
        FileMatrixD res[] = new FileMatrixD[8];
        int minSize=Integer.parseInt(args[1]);
//***** I этап *************Распределение блоков по процессорам*************************
        if (rank == 0) {
            long begTime=System.currentTimeMillis();
            int m = Integer.parseInt(args[0]);            
            int nBits=Integer.parseInt(args[2]);        
            int n = m;   //размеры матрицы
            System.out.println("start matrix size="+n);            
            fmA=new FileMatrixD(f1, 2, m, n, nBits);            
            fmB=new FileMatrixD(f2, 2, m, n, nBits);          
            FileMatrixD sA=fmA;
            FileMatrixD sB=fmB;
            if (PRINT_LOGS){
                printLog(rank, "A = \n" + fmA.toMatrixD().toString(ring));
                printLog(rank, "B = \n" + fmB.toMatrixD().toString(ring));
            }           
            int levl = (int) (Math.log(n) / Math.log(2) - minSize);
            int procsize = (int) Math.pow(8, levl);          
            for (int i = 0; i < levl; i++) {
                FileMatrixD[] fmAA = fmA.split();
                FileMatrixD[] fmBB = fmB.split();
                procsize = procsize / 8;
                fmA = fmAA[0];
                fmB = fmBB[0];

                t.send(fmAA[1], 1 * procsize, i);
                t.send(fmBB[2], 1 * procsize, i);
                t.send(fmAA[0], 2 * procsize, i);
                t.send(fmBB[1], 2 * procsize, i);

                t.send(fmAA[1], 3 * procsize, i);
                t.send(fmBB[3], 3 * procsize, i);

                t.send(fmAA[2], 4 * procsize, i);
                t.send(fmBB[0], 4 * procsize, i);

                t.send(fmAA[3], 5 * procsize, i);
                t.send(fmBB[2], 5 * procsize, i);

                t.send(fmAA[2], 6 * procsize, i);
                t.send(fmBB[1], 6 * procsize, i);

                t.send(fmAA[3], 7 * procsize, i);
                t.send(fmBB[3], 7 * procsize, i);
            }
 //** II этап********************Умножение собственных блоков******************************
            res[0] = fmA.multCU(fmB, getNextUnusedFileName(rank));            
//*****III этап *****************Сборка окончательного результата**************************            
            FileMatrixD sum[] = new FileMatrixD[4];         
            for (int i = levl - 1; i >= 0; i--) {
//**********приемка блоков от дочерних процессоров***********************                             
                res[1] = t.recv(1 * procsize, i);            
                res[2] = t.recv(2 * procsize, i);             
                res[3] = t.recv(3 * procsize, i);            
                res[4] = t.recv(4 * procsize, i);            
                res[5] = t.recv(5 * procsize, i);            
                res[6] = t.recv(6 * procsize, i);             
                res[7] = t.recv(7 * procsize, i);
                procsize = procsize * 8;             
//***********формирование промежуточных матриц****************************                
                sum[0] = res[0].add(res[1], getNextUnusedFileName(rank));
                sum[1] = res[2].add(res[3], getNextUnusedFileName(rank));
                sum[2] = res[4].add(res[5], getNextUnusedFileName(rank));
                sum[3] = res[6].add(res[7], getNextUnusedFileName(rank));                
                File f3 = new File(matrixFold+File.separatorChar+"C" + String.valueOf(rank)+String.valueOf(i));
                res[0] = FileMatrixD.joinCopy(sum, false, f3, ring);                   
            }
            if (PRINT_LOGS){
                printLog(rank, "Parall C = \n" + res[0].toMatrixD().toString(ring));             
                MatrixD trueRes=sA.toMatrixD().multCU(sB.toMatrixD(), ring);
                printLog(rank, "BruteForce C = \n" + res[0].toMatrixD().toString(ring));             
            }
            System.out.println("runTime="+(System.currentTimeMillis()-begTime));     
// Результат умножения матриц A и B записан в  res[0],т.е. res[0]=C=A*B            
        } else {
//**********Принимаем блоки матриц А и В и продолжаем разбиение***********************
          //  printLog(rank,"start recv blocks...");
            fmA = t.recv(MPI.ANY_SOURCE, MPI.ANY_TAG);
            Status s = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
            int main_proc = s.getSource();
            int tag = s.getTag();
            fmB = t.recv(MPI.ANY_SOURCE, MPI.ANY_TAG);          
            int levl = (int) (Math.log(fmA.toMatrixD().M[0].length) / Math.log(2) - minSize);         
            if (levl != 0) {
                int procsize = (int) Math.pow(8, levl);
                for (int i = 0; i < levl; i++) {
                    FileMatrixD[] fmAA = fmA.split();
                    FileMatrixD[] fmBB = fmB.split();
                    procsize = procsize / 8;                    
                    fmA = fmAA[0];                 
                    fmB = fmBB[0];
                    t.send(fmAA[1], MPI.COMM_WORLD.getRank() + 1 * procsize, i);
                    t.send(fmBB[2], MPI.COMM_WORLD.getRank() + 1 * procsize, i);
                    t.send(fmAA[0], MPI.COMM_WORLD.getRank() + 2 * procsize, i);
                    t.send(fmBB[1], MPI.COMM_WORLD.getRank() + 2 * procsize, i);
                    t.send(fmAA[1], MPI.COMM_WORLD.getRank() + 3 * procsize, i);
                    t.send(fmBB[3], MPI.COMM_WORLD.getRank() + 3 * procsize, i);
                    t.send(fmAA[2], MPI.COMM_WORLD.getRank() + 4 * procsize, i);
                    t.send(fmBB[0], MPI.COMM_WORLD.getRank() + 4 * procsize, i);
                    t.send(fmAA[3], MPI.COMM_WORLD.getRank() + 5 * procsize, i);
                    t.send(fmBB[2], MPI.COMM_WORLD.getRank() + 5 * procsize, i);
                    t.send(fmAA[2], MPI.COMM_WORLD.getRank() + 6 * procsize, i);
                    t.send(fmBB[1], MPI.COMM_WORLD.getRank() + 6 * procsize, i);
                    t.send(fmAA[3], MPI.COMM_WORLD.getRank() + 7 * procsize, i);
                    t.send(fmBB[3], MPI.COMM_WORLD.getRank() + 7 * procsize, i);
                }
 //** II этап********************Умножение собственных блоков******************************
                res[0] = fmA.multCU(fmB, getNextUnusedFileName(rank));              
//*****III этап *****************Сборка окончательного результата**************************            
                FileMatrixD sum[] = new FileMatrixD[4];            
                for (int i = 0; i <levl ; i++) {
//**********приемка блоков от дочерних процессоров***********************            
                    res[1] = t.recv(MPI.COMM_WORLD.getRank() +1 * procsize, i);            
                    res[2] = t.recv(MPI.COMM_WORLD.getRank() +2 * procsize, i);            
                    res[3] = t.recv(MPI.COMM_WORLD.getRank() +3 * procsize, i);             
                    res[4] = t.recv(MPI.COMM_WORLD.getRank() +4 * procsize, i);              
                    res[5] = t.recv(MPI.COMM_WORLD.getRank() +5 * procsize, i);              
                    res[6] = t.recv(MPI.COMM_WORLD.getRank() +6 * procsize, i);               
                    res[7] = t.recv(MPI.COMM_WORLD.getRank() +7 * procsize, i);
                    procsize = procsize * 8;
//***********формирование промежуточных матриц****************************   
                    sum[0] = res[0].add(res[1], getNextUnusedFileName(rank));
                    sum[1] = res[2].add(res[3], getNextUnusedFileName(rank));
                    sum[2] = res[4].add(res[5], getNextUnusedFileName(rank));
                    sum[3] = res[6].add(res[7], getNextUnusedFileName(rank));
                    res[0] = FileMatrixD.joinCopy(sum, false, f1, ring);
                }               
                t.send(res[0], main_proc, tag);
            } else {
                t.send(fmA.multCU(fmB, getNextUnusedFileName(rank)), main_proc, tag);                
            }

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
