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
 * @author Andrei Agapov
 */
// /home/esther/OMPI/bin/mpirun -np 7 /home/esther/jdk1.8.0_40/bin/java -cp /home/esther/NetBeansProjects/mathpar1/target/classes CalcMath.FileMatrixMulShtrassen_Agapov

// mpirun -np 7 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.stat.FMD.FileMatrixMulShtrassen_Agapov 4 1 10
public class FileMatrixMulShtrassen_Agapov implements Serializable {

    static String logName = "/home/r1d1/FMlogs";
    static String matrixFold = "/tmp/mA";
    static boolean PRINT_LOGS = false;

    public static void printLog(int rank, String s) throws Exception {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logName + File.separatorChar + String.valueOf(rank) + ".txt", true)));
        out.println(s);
        out.close();
    }

    static int nameCounter = 0;

    public static File getNextUnusedFileName(int rank) {
        return new File(matrixFold + File.separatorChar + String.valueOf(rank) + "_" + String.valueOf(nameCounter++));
    }

    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException, Exception {
        //********************Cоздание директорий**************************************  
        MPI.Init(args);        
        String[] chMod = {"rm", "-R", matrixFold};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", matrixFold};
        Process chmod1 = Runtime.getRuntime().exec(chMod);
        chmod1.waitFor();

        int rank = MPI.COMM_WORLD.getRank();

        File f1 = new File(matrixFold + File.separatorChar + "A" + String.valueOf(rank));
        File f2 = new File(matrixFold + File.separatorChar + "B" + String.valueOf(rank));
        Ring ring = new Ring("Z[x,y,z]");

        int myRank = MPI.COMM_WORLD.getRank();
        if (PRINT_LOGS) {
            if (rank == 0) {
                String[] chModLog = {"rm", "-R", logName};
                Process chmodLog = Runtime.getRuntime().exec(chModLog);
                chmodLog.waitFor();
                String[] chMod1Log = {"mkdir", logName};
                Process chmod1Log = Runtime.getRuntime().exec(chMod1Log);
                chmod1Log.waitFor();
            }
            MPI.COMM_WORLD.barrier();
        }

        FileMatrixD fmA = null, fmB = null;
        Transport2<FileMatrixD> t = new Transport2<FileMatrixD>(FileMatrixD.class);
        FileMatrixD res[] = new FileMatrixD[7];
        int minSize = Integer.parseInt(args[1]);
//***** I этап *************Распределение блоков по процессорам*************************
        if (rank == 0) {
            long begTime = System.currentTimeMillis();
            int m = Integer.parseInt(args[0]);
            int nBits = Integer.parseInt(args[2]);
            int n = m;   //размеры матрицы
            System.out.println("start matrix size=" + n + " nBits=" + nBits);
            fmA = new FileMatrixD(f1, 2, m, n, nBits);
            fmB = new FileMatrixD(f2, 2, m, n, nBits);
            if (PRINT_LOGS){
                printLog(rank, "A = \n" + fmA.toMatrixD().toString(ring));
                printLog(rank, "B = \n" + fmB.toMatrixD().toString(ring));
            }  
            FileMatrixD sA = fmA;
            FileMatrixD sB = fmB;
            int levl = (int) (Math.log(n) / Math.log(2) - minSize);
            int procsize = (int) Math.pow(7, levl);
            for (int i = 0; i < levl; i++) {
                FileMatrixD[] fmAA = fmA.split();
                FileMatrixD[] fmBB = fmB.split();
                procsize = procsize / 7;
                fmA = fmAA[0].add(fmAA[3], getNextUnusedFileName(rank));
                fmB = fmBB[0].add(fmBB[3], getNextUnusedFileName(rank));

                t.send(fmAA[2], 1 * procsize, 200000 + i);
                t.send(fmAA[3], 1 * procsize, 200000 + i);
                t.send(fmBB[0], 1 * procsize, 200000 + i);

                t.send(fmAA[0], 2 * procsize, 300000 + i);
                t.send(fmBB[1], 2 * procsize, 300000 + i);
                t.send(fmBB[3], 2 * procsize, 300000 + i);

                t.send(fmAA[3], 3 * procsize, 400000 + i);
                t.send(fmBB[2], 3 * procsize, 400000 + i);
                t.send(fmBB[0], 3 * procsize, 400000 + i);

                t.send(fmAA[0], 4 * procsize, 500000 + i);
                t.send(fmAA[1], 4 * procsize, 500000 + i);
                t.send(fmBB[3], 4 * procsize, 500000 + i);

                t.send(fmAA[2], 5 * procsize, 600000 + i);
                t.send(fmAA[0], 5 * procsize, 600000 + i);
                t.send(fmBB[0], 5 * procsize, 600000 + i);
                t.send(fmBB[1], 5 * procsize, 600000 + i);

                t.send(fmAA[1], 6 * procsize, 700000 + i);
                t.send(fmAA[3], 6 * procsize, 700000 + i);
                t.send(fmBB[2], 6 * procsize, 700000 + i);
                t.send(fmBB[3], 6 * procsize, 700000 + i);
            }
            //** II этап********************Умножение собственных блоков******************************
            res[0] = fmA.multCU(fmB, getNextUnusedFileName(rank));
//*****III этап *****************Сборка окончательного результата**************************            
            FileMatrixD sum[] = new FileMatrixD[4];
            for (int i = levl - 1; i >= 0; i--) {
//**********приемка блоков от дочерних процессоров*********************** 
                if (PRINT_LOGS){
                    printLog(rank, "start recv, rank="+(1*procsize));
                }
                res[1] = t.recv(1 * procsize, i);
                if (PRINT_LOGS){
                    printLog(rank, "start recv, rank="+(2*procsize));
                }
                res[2] = t.recv(2 * procsize, i);
                if (PRINT_LOGS){
                    printLog(rank, "start recv, rank="+(3*procsize));
                }
                res[3] = t.recv(3 * procsize, i);
                if (PRINT_LOGS){
                    printLog(rank, "start recv, rank="+(4*procsize));
                }
                res[4] = t.recv(4 * procsize, i);
                if (PRINT_LOGS){
                    printLog(rank, "start recv, rank="+(5*procsize));
                }
                res[5] = t.recv(5 * procsize, i);
                if (PRINT_LOGS){
                    printLog(rank, "start recv, rank="+(6*procsize));
                }
                res[6] = t.recv(6 * procsize, i);
                if (PRINT_LOGS){
                    printLog(rank, "finish recv");
                }
                procsize = procsize * 7;                
//***********формирование промежуточных матриц****************************   
                sum[0] = res[0].add(res[3], getNextUnusedFileName(rank)).subtract(res[4], getNextUnusedFileName(rank)).add(res[6], getNextUnusedFileName(rank));
                sum[1] = res[2].add(res[4], getNextUnusedFileName(rank));
                sum[2] = res[1].add(res[3], getNextUnusedFileName(rank));
                sum[3] = res[0].add(res[2], getNextUnusedFileName(rank)).subtract(res[1], getNextUnusedFileName(rank)).add(res[5], getNextUnusedFileName(rank));
                File f3 = new File(matrixFold + File.separatorChar + "C" + String.valueOf(rank) + String.valueOf(i));
                res[0] = FileMatrixD.joinCopy(sum, false, f3, ring);
            }
            if (PRINT_LOGS) {
                printLog(rank, "Parall C = \n" + res[0].toMatrixD().toString(ring));
                MatrixD trueRes = sA.toMatrixD().multCU(sB.toMatrixD(), ring);
                printLog(rank, "BruteForce C = \n" + res[0].toMatrixD().toString(ring));
            }
            System.out.println("runTime=" + (System.currentTimeMillis() - begTime));
// Результат умножения матриц A и B записан в  res[0],т.е. res[0]=C=A*B            
        } else {
//**********Принимаем блоки матриц А и В и продолжаем разбиение***********************             
            Status s = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);            
            int main_proc = s.getSource();
            int tag = s.getTag();

            int tagL = tag % 100000;
            int tagP = tag / 100000;
            if (PRINT_LOGS){
                printLog(rank, "start recv blocks...");
            }
            switch (tagP) {
                case 2: {
                    fmA = t.recv(main_proc, tag).add(t.recv(main_proc, tag), tag);
                    fmB = t.recv(main_proc, tag);
                    break;
                }
                case 3: {
                    fmA = t.recv(main_proc, tag);
                    fmB = t.recv(main_proc, tag).subtract(t.recv(main_proc, tag), tag);
                    break;
                }
                case 4: {
                    fmA = t.recv(main_proc, tag);
                    fmB = t.recv(main_proc, tag).subtract(t.recv(main_proc, tag), tag);
                    break;
                }
                case 5: {
                    fmA = t.recv(main_proc, tag).add(t.recv(main_proc, tag), tag);
                    fmB = t.recv(main_proc, tag);
                    break;
                }
                case 6: {
                    fmA = t.recv(main_proc, tag).subtract(t.recv(main_proc, tag), tag);
                    fmB = t.recv(main_proc, tag).add(t.recv(main_proc, tag), tag);
                    break;
                }
                case 7: {
                    fmA = t.recv(main_proc, tag).subtract(t.recv(main_proc, tag), tag);
                    fmB = t.recv(main_proc, tag).add(t.recv(main_proc, tag), tag);
                    break;
                }
            }
            if (PRINT_LOGS){
                printLog(rank, "recv blocks finished");
                printLog(rank, "A= \n"+fmA.toMatrixD().toString(ring)+"\nB= \n"+fmB.toMatrixD().toString(ring));
            }
            int levl = (int) (Math.log(fmA.toMatrixD().M[0].length) / Math.log(2) - minSize);
            if (levl != 0) {
                int procsize = (int) Math.pow(7, levl);
                for (int i = 0; i < levl; i++) {
                    FileMatrixD[] fmAA = fmA.split();
                    FileMatrixD[] fmBB = fmB.split();
                    procsize = procsize / 7;

                    fmA = fmAA[0].add(fmAA[3], getNextUnusedFileName(rank));
                    fmB = fmBB[0].add(fmBB[3], getNextUnusedFileName(rank));

                    t.send(fmAA[2], MPI.COMM_WORLD.getRank() + 1 * procsize, 200000 + i);
                    t.send(fmAA[3], MPI.COMM_WORLD.getRank() + 1 * procsize, 200000 + i);
                    t.send(fmBB[0], MPI.COMM_WORLD.getRank() + 1 * procsize, 200000 + i);

                    t.send(fmAA[0], MPI.COMM_WORLD.getRank() + 2 * procsize, 300000 + i);
                    t.send(fmBB[1], MPI.COMM_WORLD.getRank() + 2 * procsize, 300000 + i);
                    t.send(fmBB[3], MPI.COMM_WORLD.getRank() + 2 * procsize, 300000 + i);

                    t.send(fmAA[3], MPI.COMM_WORLD.getRank() + 3 * procsize, 400000 + i);
                    t.send(fmBB[2], MPI.COMM_WORLD.getRank() + 3 * procsize, 400000 + i);
                    t.send(fmBB[0], MPI.COMM_WORLD.getRank() + 3 * procsize, 400000 + i);

                    t.send(fmAA[0], MPI.COMM_WORLD.getRank() + 4 * procsize, 500000 + i);
                    t.send(fmAA[1], MPI.COMM_WORLD.getRank() + 4 * procsize, 500000 + i);
                    t.send(fmBB[3], MPI.COMM_WORLD.getRank() + 4 * procsize, 500000 + i);

                    t.send(fmAA[2], MPI.COMM_WORLD.getRank() + 5 * procsize, 600000 + i);
                    t.send(fmAA[0], MPI.COMM_WORLD.getRank() + 5 * procsize, 600000 + i);
                    t.send(fmBB[0], MPI.COMM_WORLD.getRank() + 5 * procsize, 600000 + i);
                    t.send(fmBB[1], MPI.COMM_WORLD.getRank() + 5 * procsize, 600000 + i);

                    t.send(fmAA[1], MPI.COMM_WORLD.getRank() + 6 * procsize, 700000 + i);
                    t.send(fmAA[3], MPI.COMM_WORLD.getRank() + 6 * procsize, 700000 + i);
                    t.send(fmBB[2], MPI.COMM_WORLD.getRank() + 6 * procsize, 700000 + i);
                    t.send(fmBB[3], MPI.COMM_WORLD.getRank() + 6 * procsize, 700000 + i);
                }
                //** II этап********************Умножение собственных блоков******************************
                res[0] = fmA.multCU(fmB, getNextUnusedFileName(rank));
//*****III этап *****************Сборка окончательного результата**************************            
                FileMatrixD sum[] = new FileMatrixD[4];
                for (int i = 0; i < levl; i++) {
//**********приемка блоков от дочерних процессоров*********************** 
                    if (PRINT_LOGS){
                        printLog(rank, "recv dBlocks start... startRank="+(MPI.COMM_WORLD.getRank() + 1 * procsize)
                        +" end rank="+(MPI.COMM_WORLD.getRank() + 6 * procsize + myRank));
                    }
                    res[1] = t.recv(1 * procsize + myRank, i);
                    res[2] = t.recv(2 * procsize + myRank, i);
                    res[3] = t.recv(3 * procsize + myRank, i);
                    res[4] = t.recv(4 * procsize + myRank, i);
                    res[5] = t.recv(5 * procsize + myRank, i);
                    res[6] = t.recv(6 * procsize + myRank, i);
                    procsize = procsize * 7;
//***********формирование промежуточных матриц****************************   
                    sum[0] = res[0].add(res[3], getNextUnusedFileName(rank)).subtract(res[4], getNextUnusedFileName(rank)).add(res[6], getNextUnusedFileName(rank));
                    sum[1] = res[2].add(res[4], getNextUnusedFileName(rank));
                    sum[2] = res[1].add(res[3], getNextUnusedFileName(rank));
                    sum[3] = res[0].add(res[2], getNextUnusedFileName(rank)).subtract(res[1], getNextUnusedFileName(rank)).add(res[5], 1000);
                    res[0] = FileMatrixD.joinCopy(sum, false, f1, ring);
                }
                t.send(res[0], main_proc, tagL);
            } else {
                t.send(fmA.multCU(fmB, getNextUnusedFileName(rank)), main_proc, tagL);
            }

        }
        MPI.Finalize();
    }
}
