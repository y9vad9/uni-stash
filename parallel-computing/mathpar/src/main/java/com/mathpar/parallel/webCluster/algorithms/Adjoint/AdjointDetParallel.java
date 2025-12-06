package com.mathpar.parallel.webCluster.algorithms.Adjoint;

import java.io.IOException;
import java.util.Random;
import com.mathpar.matrix.*;
import mpi.*;
import com.mathpar.number.*;
import com.mathpar.number.NFunctionZ32;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.parallel.webCluster.algorithms.multMatrix1x8.TaskMultiplyMatrix;
import com.mathpar.parallel.webCluster.engine.QueryCreator;
import com.mathpar.parallel.webCluster.engine.Tools;

/**
 *
 * @author khvorov
 */
public class AdjointDetParallel {
    public static int[] primesForMatrix(MatrixS A, Ring ring) throws IOException, MPIException {
        System.out.println("i rank = "+MPI.COMM_WORLD.getRank() + " in primesForMatrix");
        MatrixS tranA = A.transpose();
        //NumberZ adamar = tranA.adamarNumber(ring).multiply(new NumberZ(2));//проверка на неравенство Адамара
        int adamar = tranA.adamarBitCount(ring);
        System.out.println("adamarNumber = "+adamar);
        System.out.println("get adamar = "+MPI.COMM_WORLD.getRank());
        NFunctionZ32.doStaticPrimesProdAndInd();// создаем список модулей
        System.out.println("create list = "+MPI.COMM_WORLD.getRank());
        int line = 1;
        NumberZ p = new NumberZ(1);
        int k = 0;
        //for (int i = 0; (adamar.subtract(line)).compareTo(ring.numberZERO) == 1; i++) {
          for(int i = 0; line < adamar; i++){
            p = p.multiply(new NumberZ(NFunctionZ32.Primes[i]));
            line = p.bitCount();
            k++;
        }
        int[] primes = new int[k];
        System.arraycopy(NFunctionZ32.Primes, 0, primes, 0, k);
        System.out.println("i rank = "+MPI.COMM_WORLD.getRank() + " out primesForMatrix");
        return primes;
    }

    /**
     *
     * @param n - количество задач
     * @param partNumb - количество частей, на которые нужно поделить задачи
     * @param myrank - номер текущего процессора
     *
     * @return интервал, с какой и по какую задачу следует брать текущему
     * процессору
     */
    public static int[] getMyInterval(int n, int partNumb, int myrank) {
        int[] interval = new int[2];
        int numb_proc = n / partNumb;
        int ost = n % partNumb;
        interval[0] = (myrank < (partNumb - ost)) ? (myrank * numb_proc) : myrank * numb_proc + (myrank - (partNumb - ost));
        interval[1] = (myrank < (partNumb - ost)) ? ((myrank * numb_proc) + numb_proc) : myrank * numb_proc + (myrank - (partNumb - ost)) + (numb_proc + 1);
        return interval;
    }


   /**
     * транспонирование целочисленной матрицы
     *
     * @param A - целочисленная матрица
     *
     * @return транспонированная матрица
     */
    public static int[][] intTransposition(int[][] A) {
        int[][] transp = new int[A[0].length][A.length];
        for (int i = 0; i < A[0].length; i++) {
            for (int j = 0; j < A.length; j++) {
                transp[i][j] = A[j][i];
            }
        }
        return transp;
    }

    /**
     *
     * @param A - исходная матрица типа matrixS
     * @param rank - номер текущего процессора
     * @param size - количество процессоров
     * @param primes - список модулей
     * @param flag - если flag = true, то метод вычисляет присоединенные матрицы
     * по модулям из списка primes, если flag = false, то определители по
     * модулям.
     * @param ring - кольцо Z
     *
     * @return Массив присоединенных матриц и определителей, посчитанных на
     * одном процессоре в кольце Zp, где p - просто число из списка primes.
     * Каждой присоединенной матрицы соответствует свой простой модуль.
     *
     * @throws IOException
     */
    public static MatrixS[] adjointDetZp(MatrixS A, int rank, int size, int[] primes, boolean flag, Ring ring) throws IOException {
       // System.out.println("i rank = "+rank + " in adjointDetZp");
        Ring ringZp = new Ring("Zp32[x]");
        MatrixS Azp;
        int primesLen = primes.length;
        int[] interval = getMyInterval(primesLen, size, rank);//интервал из списка primes
        int numPrimesforProc = (rank < (size - primesLen % size)) ? primesLen / size : primesLen / size + 1;//число модулей на процессор
        MatrixS[] matrZp = new MatrixS[numPrimesforProc];//присоединенные матрицы по модулям
        if (flag == true) {
            int k = 0;
            for (int i = interval[0]; i < interval[1]; i++) {
                ringZp.setMOD32(primes[i]);
                Azp = (MatrixS) A.toNewRing(Ring.Zp32, ringZp);//переводим матрицу А к кольцу по модулю primes[i] 
                //System.out.println("Azp = " + Azp.toString(ring) + " rank= " + rank);
                Element[] c = new Element[] {ringZp.numberONE};
                matrZp[k] = Azp.adjointDet(c, ringZp);//записываем присоединенную матрицу по модулю primes[i] 
               // System.out.println("adjZp = " + matrZp[k] + " rank = " + rank+ " prime= "+primes[i]);
                k++;
                System.out.println("i rank = "+rank + " out adjointDetZp");
            }
        } else {
            int k = 0;
            for (int i = interval[0]; i < interval[1]; i++) {
                ringZp.setMOD32(primes[i]);
                Azp = (MatrixS) A.toNewRing(Ring.Zp32, ringZp);//переводим матрицу А к кольцу по модулю primes[i]
               // System.out.println("Azp = " + Azp.toString(ring) + " rank= " + rank + " num=" + k + " prime=" + primes[i] + " ");
                if (A.rank(ring) != Azp.rank(ringZp)) {//проверяем совпадают ли ранги исходной матрицы и новой
                    matrZp[k] = new MatrixS(ringZp.numberZERO);
                } else {
                    Element[] c = new Element[] {ringZp.numberONE};
                    MatrixS mtr = Azp.adjointDet(c, ringZp);
                    //System.out.println("Adjoint = "+mtr+" rank="+rank);
                    matrZp[k] = new MatrixS(c[0]);// записываем определитель по модулю primes[i]
                    
                }
                k++;
            }
        }
        return matrZp;
    }

    /**
     * Рассылка строк присоединенных матриц в кольце Zp между процессорами для
     * последующего их восстановления
     *
     * @param matrZp - список присоединенных матриц
     * @param rank - номер текущего процессора
     * @param size - количество процессоров
     * @param primes - массив простых модулей
     *
     * @return массив строк
     *
     * @throws MPIException
     */
    public static MatrixS[] sendRecvRowBetweenProc(MatrixS[] matrZp, int rank, int size, int[] primes) throws Exception {
       // System.out.println("i rank = "+rank + " in sendRecv");
        int matr_len = matrZp[0].size;
        int primes_len = primes.length;
        int numbRowforProc = (rank < (size - primes_len % size)) ? primes_len / size : (primes_len / size) + 1;
        System.out.println("first i rank = "+rank);
        MatrixS[] recSubMatr = new MatrixS[primes_len];
        MatrixS[] subM = new MatrixS[matrZp.length];
        for (int i = 0; i < size; i++) {
            int[] interval_i = getMyInterval(matr_len, size, i);
            
            if (i != rank) {
                for (int j = 0; j < matrZp.length; j++) {
                    subM[j] = matrZp[j].getSubMatrix(interval_i[0], interval_i[1] - 1, 0, matrZp[j].colNumb);
                }
                MPITransport.sendObjectArray(subM, 0, subM.length, i, 1);

            } else {
                for (int j = 0; j < matrZp.length; j++) {
                    subM[j] = matrZp[j].getSubMatrix(interval_i[0], interval_i[1] - 1, 0, matrZp[j].colNumb);
                }

            }
            int[] interval = getMyInterval(primes_len, size, i);
            System.arraycopy(subM, 0, recSubMatr, interval[0], numbRowforProc);
        }
        System.out.println("send victory = "+ rank);
        for (int i = 0; i < size; i++) {
            int[] interval = getMyInterval(primes_len, size, i);
            if (i != rank) {
                int numbRowforProcI = (i < (size - primes_len % size)) ? primes_len / size : (primes_len / size) + 1;
                Object[] obj = new Object[numbRowforProcI];
                MPITransport.recvObjectArray(obj, 0, obj.length, i, 1);
                MatrixS[] temp = new MatrixS[obj.length];
                for (int j = 0; j < obj.length; j++) {
                    temp[j] = (MatrixS) obj[j];
                }
                System.out.println("second i rank = "+rank);
                System.arraycopy(temp, 0, recSubMatr, interval[0], numbRowforProcI);
                
            }
            System.out.println("third i rank = "+rank);
        }
       // System.out.println("i rank = "+rank + " out sendRecv");
        return recSubMatr;
    }

    /**
     * Восстанавливает определитель
     *
     * @param B - квадратная матрица
     * @param ring - кольцо Z
     *
     * @return восстановленный определитель
     *
     * @throws MPIException
     */
    public static Element detParallel(MatrixS B, Ring ring) throws Exception {
        MatrixS A = sendInitialMatrix(B, ring);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        MatrixS tranA = A.transpose();
        int adamar = tranA.adamarBitCount(ring);
        //int[] primes = primesForMatrix(A, ring);
        int[] primes = Newton.primesForAdjoint(adamar, 1, ring);
        if(size > A.size || size > primes.length){
            System.out.println("Число процессоров превышает количество строк матрицы или число простых модулей. Уменьшите число процессоров!!!");
            return null;
        }else{
        MatrixS[] detParts = adjointDetZp(A, rank, size, primes, false, ring);
        // System.out.println("detParts = " + Array.toString(detParts) + " rank= " + rank);
        NumberZ determinant = new NumberZ();
        if (rank != 0) {
            MPITransport.sendObjectArray(detParts, 0, detParts.length, 0, 1);
        } else {
            int pr_len = primes.length;
            int[] inter_zero = getMyInterval(pr_len, size, rank);
            MatrixS[] temp = new MatrixS[pr_len];
            System.arraycopy(detParts, 0, temp, inter_zero[0], detParts.length);
            for (int i = 1; i < size; i++) {
                int n = (i < (size - pr_len % size)) ? pr_len / size : pr_len / size + 1;
                int[] interval = getMyInterval(pr_len, size, i);
                Object[] noname = new Object[n];
                MPITransport.recvObjectArray(noname, 0, noname.length, i, 1);
                MatrixS[] sborka = new MatrixS[noname.length];
                for (int p = 0; p < sborka.length; p++) {
                    sborka[p] = (MatrixS) noname[p];
                }
                System.arraycopy(sborka, 0, temp, interval[0], n);
            }
            int[] det = new int[pr_len];
            for (int i = 0; i < pr_len; i++) {
                det[i] = temp[i].getElement(0, 0, ring).intValue();
            }
            //System.out.println("det_i = " + Array.toString(det));
            determinant = Newton.recoveryNewton(primes, det);
        }
        return determinant;
        }
    }

    /**
     * Восстанавливает строки присоединенных матриц по модулям
     *
     * @param A - несколько строк присоединенных матриц по модулям из списка
     * primes
     * @param ring - кольцо Z
     *
     * @return присоединенная матрица в кольце Z
     *
     * @throws java.lang.Exception
     */
    public static MatrixS adjointParallel(MatrixS B, Ring ring) throws Exception {
        MatrixS A = sendInitialMatrix(B, ring);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        int matr_len = A.size;
        MatrixS tranA = A.transpose();
        int adamar = tranA.adamarBitCount(ring);
        //int[] primes = primesForMatrix(A, ring);
        int[] primes = Newton.primesForAdjoint(adamar, 1, ring);
         if (rank == 0) {
            System.out.println("Primes = " + Array.toString(primes));
        }
        if(size > A.size || size > primes.length){
            System.out.println("Число процессоров превышает количество строк матрицы или число простых модулей. Уменьшите число процессоров!!!");
            return null;
        }else{
        MatrixS[] adjZp = adjointDetZp(A, rank, size, primes, true, ring);
        MatrixS[] subMatr = sendRecvRowBetweenProc(adjZp, rank, size, primes);
        int[][][] revive = new int[subMatr[0].size][subMatr.length][matr_len];
        for (int i = 0; i < subMatr.length; i++) {
            for (int j = 0; j < subMatr[i].size; j++) {
                for (int k = 0; k < subMatr[i].colNumb - 1; k++) {
                    revive[j][i][k] = subMatr[i].getElement(j, k, ring).intValue();
                }
            }
        }
        for (int i = 0; i < revive.length; i++) {

            revive[i] = intTransposition(revive[i]);
        }
        Element[][] answer = new Element[revive.length][revive[0].length];
        for (int i = 0; i < revive.length; i++) {
            for (int j = 0; j < revive[i].length; j++) {
                answer[i][j] = com.mathpar.number.Newton.recoveryNewton(primes, revive[i][j]);
            }
        }
        if (rank != 0) {
            MPITransport.sendObjectArray(answer, 0, answer.length, 0, 2);
            return null;
        } else {
            Element[][] adj = new Element[matr_len][matr_len];
            int numbRowforProc = (rank < (size - matr_len % size)) ? matr_len / size : matr_len / size + 1;
            int[] inter_zero = getMyInterval(matr_len, size, rank);
            System.arraycopy(answer, 0, adj, inter_zero[0], numbRowforProc);
            for (int i = 1; i < size; i++) {
                int numbRowforProcI = (i < (size - matr_len % size)) ? matr_len / size : matr_len / size + 1;
                int[] interval = getMyInterval(matr_len, size, i);
                Element[][] temp = new Element[numbRowforProcI][];
                MPITransport.recvObjectArray(temp, 0, numbRowforProcI, i, 2);
                System.arraycopy(temp, 0, adj, interval[0], numbRowforProcI);
            }
            MatrixS result = new MatrixS(adj, ring);
           //  System.out.println("i rank = "+rank + " out adjointParallel");
            return result;
        }
        }
    }

    /**
     * Создает матрицу
     *
     * @param size - размер матрицы
     * @param numb - граница случайного целого числа
     * @param ring - кольцо
     *
     * @return матрица типа MatrixS
     */
    public static MatrixS randomMatrixS(int size, int numb, Ring ring) {
        Random rnd = new Random();
        NumberZ[][] a = new NumberZ[size][size];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = new NumberZ((numb+rnd.nextInt(numb)));
            }
        }
        MatrixS A = new MatrixS(a, ring);
        return A;
    }
    
    public static MatrixS sendInitialMatrix(MatrixS A, Ring ring) throws MPIException, IOException, ClassNotFoundException{
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        if(rank == 0){
            for (int i = 1; i < size; i++) {
                MPITransport.sendObject(A, i, 1);
            }
            return A;
        }else{
          MatrixS matr = (MatrixS) MPITransport.recvObject(0, 1);
          return matr;
        }
    }
    /*
    SPACE = Z[x];
    A=[[0,1],[2,3]];
    TOTALNODES = 1;
    PROCPERNODE = 1;
    \adjointDetPar(A);
    
    */

// mpirun -np 2 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.webCluster.algorithms.Adjoint.AdjointDetParallel
    public static void main(String[] args) throws Exception {
        MPI.Init(args);        
        long t1 = System.currentTimeMillis();
        Object []argsFromWeb=Tools.getDataFromClusterRootNode(args).getData();
        MatrixS rnd =new MatrixS();
        int rank = MPI.COMM_WORLD.getRank();
        if (rank==0){
            rnd=(MatrixS)argsFromWeb[0];
        }            
        Ring ring = (Ring)argsFromWeb[1];
        ring=(Ring)MPITransport.bcastObject(ring, 0);
        
        MatrixS adjoint=null;
        if (rank == 0) {            
            //System.out.println("Исходная матрица = " + rnd.toString(ring));
//            for (int i = 1; i < size; i++) {
//                MPITransport.sendObject(rnd, i, 1);
//            }
            adjoint = adjointParallel(rnd,  ring);
            System.out.println("Time = " + (System.currentTimeMillis() - t1));
            // System.out.println("Adjoint = " + adjoint.toString(ring));
            //Element det = detParallel(rnd, rank, size, ring);
//            System.out.println("DET = " + det);
            Element[] c = new Element[] {ring.numberONE};
            MatrixS mtr = rnd.adjointDet(c, ring);
            //Element detRight = c[0];
           // System.out.println("Правильный adjoint=" + mtr.toString(ring));
            //System.out.println("Правильный det=" + detRight);
        } else {            
            //MatrixS rnd = (MatrixS) MPITransport.recvObject(0, 1);
            adjoint = adjointParallel(rnd, ring);
            //Element det = detParallel(rnd, rank, size, ring);
        }
        if (rank==0){            
            Object[] result = {adjoint};
            int userID= Integer.valueOf(args[0]);
            int taskID= Integer.valueOf(args[1]);
            QueryCreator qc= new QueryCreator(null,null);
            qc.saveCalcResultOnRootNode(userID, taskID, result);
        }
        MPI.Finalize();
    }

}
