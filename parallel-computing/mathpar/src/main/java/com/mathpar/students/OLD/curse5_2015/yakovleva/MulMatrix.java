/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package students.student.yakovleva;
package com.mathpar.students.OLD.curse5_2015.yakovleva;

import java.util.Random;
import com.mathpar.matrix.MatrixD;
import mpi.*;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;


/**
 *$HOME/openmpi/bin/mpirun -np 4 java -cp ${HOME}/mathpar/target/classes students.student.yakovleva.MulMatrix
 * @author katya
 */
public class MulMatrix {
    
    static int tag = 0;
    static Ring ring = new Ring("Z[x,y,z]");

    
    /**
     * Вычисление детей от данного процессора путем битового сдвига
     *  дети - восемь комбинаций перестановки трех первых битов от
     *  номера процеса родителя rank
     *  процессор-ребенок состоит из 8 комбинаций l - битов
     *  и самих битов rank
     * @param rank - процессор родитель
     * @param shift - сдвиг
     * @return массив детей процессора 
     */
    
    public static int[] numChildProc(int rank, int shift) {
        int[] ranks = new int[8];
        for (int i = 0; i < 8; i++) {
            ranks[i] = (i << shift) | rank;
        }
        return ranks;
    }
   /**
     *  Вычисление номера процессора-родителя по потомку и сдвигу\\ 
     *  Номер прочессора rank состоит из комбинации первых  \textbf{\textit{l}} битов\\
     *  и номера родительского процессора \\
     *  Удаляются первые \textbf{\textit{l}} битов\\
     *  тем самым находим номер процессора-родителя\\ 
     * @param rank - потомок, родителя которого надо найти
     * @param l - сдвиг битов
     * @return индекс процессора родителя
     */
    public static int numbParentProc(int rank, int l) {
        int temp = (rank >> l);
        int num_parent = rank - (temp << l);
        return num_parent;

    }
    /**
     *  Нахождение сдвига родителя процессора
     *  c помощью массива с заранее заполненными 
     *  максимальными индексами процессоров в каждом уровне
     *  определяем соответственно уровень, в котором находится процессор
     *  после этого уровень i*3 - сдвиг процессора родителя 
     *  для получения данного rank
     * @param rank -  процессор сдвиг родителя которого нужно найти
     * @return сдвиг родителя для получения этого процессора
     */
    public static int shiftProc(int rank) {

        int procs[] = new int[10];
        for (int i = 0; i < 10; i++) {
            procs[i] = (int) Math.pow(8.0, i + 1) - 1;
        }
        
        int res = 0;
        for (int i = 1; i < procs.length; i++) {
            if (rank > procs[i - 1] && rank <= procs[i]) {
                res = i * 3;
            }
        }
        return res;
    }
    /** 
     * Вычисление произведения матриц на параллельной машине
     * с числом процессоров кратным степени 8
     * Рекурсиная процедура вычисления произведения матриц
     * основана на древовидной рассылке по узлам блоков матриц
     * и вычислений произведения на каждом узле с последующим сбором
     * на головном процессоре блоков
     * @param A -  матрица для вычислений
     * @param B -  матрица для вычислений
     * @param N_crit - критическое число выхода
     * @param rank - индекс процессора вошедшего в процедуру
     * @param shift- сдвиг используемый для порождения детей-процессоров
     * @return произведение матриц A*B
     * @throws MPIException 
     */
    
    public static MatrixD procEightProc(MatrixD[] A, MatrixD[] B, int N_crit, int rank, int shift) throws Exception {

        MatrixD[] C = new MatrixD[4];
        MatrixD Result;
        if (N_crit < A[0].M.length) {

            int[] ranks = numChildProc(rank, shift);

            MatrixD[][] AA = new MatrixD[A.length][4];
            MatrixD[][] BB = new MatrixD[B.length][4];
            for(int i=0; i<A.length; i++) {
                AA[i] = split(A[i]);
                BB[i] = split(B[i]);
            }
            
            MatrixD[][] AAA = new MatrixD[4][A.length];
            MatrixD[][] BBB = new MatrixD[4][B.length];
            
            for(int i=0; i<A.length; i++) {
                for(int j=0; j<4; j++) {
                    AAA[j][i] = AA[i][j];
                    BBB[j][i] = BB[i][j];
                }
            }
            MPITransport.sendObject(new Object[] {AAA[1], BBB[2]}, ranks[1], tag);
            MPITransport.sendObject(new Object[] {AAA[0], BBB[1]}, ranks[2], tag);
            MPITransport.sendObject(new Object[] {AAA[1], BBB[3]}, ranks[3], tag);
            MPITransport.sendObject(new Object[] {AAA[2], BBB[0]}, ranks[4], tag);
            MPITransport.sendObject(new Object[] {AAA[3], BBB[2]}, ranks[5], tag);
            MPITransport.sendObject(new Object[] {AAA[2], BBB[1]}, ranks[6], tag);
            MPITransport.sendObject(new Object[] {AAA[3], BBB[3]}, ranks[7], tag);

            Status st = null;
            MatrixD[] tmp = new MatrixD[8];
            tmp[0] = procEightProc(AAA[0], BBB[0], N_crit, rank, shift + 3);

            for (int i = 1; i < 8; i++) {
                while (st == null) {
                    st = MPI.COMM_WORLD.probe(ranks[i], tag);
                }

                tmp[i] = (MatrixD) MPITransport.recvObject(ranks[i], tag);

            }

            C[0] = (MatrixD) tmp[0].add(tmp[1], ring);
            C[1] = (MatrixD) tmp[2].add(tmp[3], ring);
            C[2] = (MatrixD) tmp[4].add(tmp[5], ring);
            C[3] = (MatrixD) tmp[6].add(tmp[7], ring);

            Result = MatrixD.join(C);
            
        } else {
            Result = (MatrixD) A[0].multiply(B[0], ring);
            for(int i=1; i<A.length; i++) {
                Result = (MatrixD) Result.add(A[i].multiply(B[i], ring), ring);
            }
        }

        return Result;
    }
    /**
     * Запуск вычисления умножения матриц с использованием\\
     * древовидного алгоритма пересылки блоков матриц.\\
     * Запуск осуществляется на головном процессоре\\
     * в это время остальные ждут получения блока матрицы\\
     * после плучения запускается процесс вычисления на узлах\\
     * @param A - матрица для вычислений
     * @param B - матрица для вычислений
     * @param N_crit - критическое число выхода
     * @return - произведение матриц A*B 
     * @throws MPIException 
     */
    public static MatrixD multiplyMatrix(MatrixD[] A, MatrixD[] B, int N_crit) throws Exception {
        int myrank = MPI.COMM_WORLD.getRank();
        MatrixD result = null;
        if (myrank == 0) {
            result = procEightProc(A, B, N_crit, myrank, 0);
        } else {
            Status st = null;

            while (st == null) {
                st = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
            }


            int shift = shiftProc(myrank);
            int parent_proc = numbParentProc(myrank, shift);
            Object[] n = (Object[])MPITransport.recvObject( parent_proc, tag);
            MatrixD[] a = (MatrixD[]) n[0];
            MatrixD[] b = (MatrixD[]) n[1];

            MatrixD temp = procEightProc(a, b, N_crit, myrank, shift + 3);
            MPITransport.sendObject(temp, parent_proc, tag);

        }
        return result;
    }


    static MatrixD[] split(MatrixD m) {
        MatrixD[] result = new MatrixD[4];
        int size = m.M.length/2;
        
        for(int i=0; i<result.length; i++) {
            result[i] = new MatrixD(new Element[size][size]);
        }
        
        for(int i=0; i<m.M.length; i++) {
            for(int j=0; j<m.M.length; j++) {
                if( (i<size )&&(j<size) ) {
                    result[0].M[i][j] = m.M[i][j];
                } else if( (i<size )&&(j>=size) ) {
                    result[1].M[i][j - size] = m.M[i][j];
                } else if( (i>=size )&&(j<size) ) {
                    result[2].M[i - size][j] = m.M[i][j];
                } else {
                    result[3].M[i - size][j - size] = m.M[i][j];
                }
            }
        }
        
        
        return result;
    }


    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        int N_crit;
        int size = MPI.COMM_WORLD.getSize();
        int myrank = MPI.COMM_WORLD.getRank();
        int n = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);
        int length = Integer.parseInt(args[2]);
        int temp = n;
        for(int i=0; size>1; i++) {
            size = size/8;
            temp = temp/2;
        }
        N_crit = temp;
        if(myrank == 0)System.out.println("N_crit = "+N_crit);
        MatrixD[] A = new MatrixD[m];
        MatrixD[] B = new MatrixD[m];
        int[] randomType = new int[] {length};
        Random ran = new Random();
        
        if (myrank == 0) {
            for(int i=0; i<A.length; i++) {
                A[i] = new MatrixD(n, n, 100, randomType, ran, ring);
                B[i] = new MatrixD(n, n, 100, randomType, ran, ring);
            }
        }
        
        if (myrank == 0) {
            long time = System.currentTimeMillis();
            Ring r = new Ring("Zp32[]");
            int block = 1;
            int count = 1;
            Object[] array_of_all_primes = new Object[100];
            int[] primes = NFunctionZ32.readBlockOfPrimesFromBack(1, Ring.ringZxyz);
            int number_of_primes = primes.length;
            array_of_all_primes[0] = primes;
            NumberZ mod = new NumberZ( primes[0]);
            NumberZ max = (NumberZ)new NumberZ(2).pow(length*(m+1)+n);//(int)(Math.log(n)/Math.log(2))+1, ring);
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
            MPI.COMM_WORLD.bcast(new int[]{count}, 1, MPI.INT, 0);
            primes = new int[number_of_primes];
            int tmp = 0;
            for(int i=0; array_of_all_primes[i] != null; i++) {
                System.arraycopy(array_of_all_primes[i], 0, primes, tmp, ((int[])array_of_all_primes[i]).length);
                tmp+=((int[])array_of_all_primes[i]).length;
            }
            
            MatrixD[] aa = new MatrixD[A.length];
            MatrixD[] bb = new MatrixD[B.length];
            MatrixD[] cc = new MatrixD[count];
            Ring r2 = new Ring("Z[]");
            for(int i=0; i < count; i++) {
                r.setMOD32(primes[i]);
                for(int j=0; j<aa.length; j++) {
                    aa[j] = (MatrixD)A[j].toNewRing(Ring.Zp32, r);
                    bb[j] = (MatrixD)B[j].toNewRing(Ring.Zp32, r);
                }
                ring = r;
                MPITransport.bcastObjectArray(new Object[]{r}, 1, 0);
                cc[i] = (MatrixD)multiplyMatrix(aa, bb, N_crit);
                cc[i] = (MatrixD)cc[i].toNewRing(Ring.Z, r2);
            }
            ring = r2;
            MatrixD C;
            MatrixD C_ = new MatrixD(new Element[cc[0].M.length][cc[0].M.length]);
            MatrixD C2;
            C = cc[0];
            for(int i=1; i<count; i++) {
                for(int z=0; z<C.M.length; z++) {
                    for(int j=0; j<C.M.length; j++) {
                        C_.M[z][j] = C.M[z][j].mod(new NumberZ(primes[i]), ring);
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

            System.out.println("time1 = "+(System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
            MatrixD res = (MatrixD) A[0].multiply(B[0], ring);
            for(int i=1; i<A.length; i++) {
                res = (MatrixD) res.add(A[i].multiply(B[i], ring), ring);
            }
            System.out.println("time2 = "+(System.currentTimeMillis() - time));
            if(C.subtract(res, ring).isZero(ring)) {
                System.out.println("ok!!!");
            } else {
                System.out.println("wrong answer!!!");
            }
        } else {
            int[] array = new int[1];
            Object[] obj = new Object[1];
            MPI.COMM_WORLD.bcast(array, 1, MPI.INT, 0);
            for(int i=0; i<array[0]; i++) {
                MPITransport.bcastObjectArray(obj, 1, 0);
                ring = (Ring) obj[0];
                multiplyMatrix(null, null, N_crit);
            }
        }
        
        MPI.Finalize();
    }
}

