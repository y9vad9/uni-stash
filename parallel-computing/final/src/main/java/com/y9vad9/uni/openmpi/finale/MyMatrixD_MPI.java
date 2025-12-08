package com.y9vad9.uni.openmpi.finale;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import mpi.Intracomm;
import mpi.MPIException;
import java.io.Serializable;

public class MyMatrixD_MPI extends MyMatrixD {

    public MyMatrixD_MPI(Element[][] blk, int i) {
        super(blk, i);
    }

    @Override
    public MyMatrixD_MPI[] divideToBlocks(int blRows, int blCols, Ring ring) {
        int n = M.length;
        int m = M[0].length;

        int blockH = (n + blRows - 1) / blRows;
        int blockW = (m + blCols - 1) / blCols;

        MyMatrixD_MPI[] res = new MyMatrixD_MPI[blRows * blCols];
        Element zero = ring.numberZERO;

        int idx = 0;
        for (int br = 0; br < blRows; br++) {
            for (int bc = 0; bc < blCols; bc++) {
                Element[][] blk = new Element[blockH][blockW];

                for (int i = 0; i < blockH; i++) {
                    int srcI = br * blockH + i;
                    for (int j = 0; j < blockW; j++) {
                        int srcJ = bc * blockW + j;
                        blk[i][j] = (srcI < n && srcJ < m) ? M[srcI][srcJ] : zero;
                    }
                }
                res[idx++] = new MyMatrixD_MPI(blk, 0); // <-- створюємо саме MyMatrixD_MPI
            }
        }
        return res;
    }

    /**
     * Блочний визначник з відстеженням ведучих елементів track.
     * 16 процесів, блокова розбивка, без перестановок.
     */
    public Element detAndTrackBlockedMPI(int[][] track, Intracomm com, Ring ring) throws MPIException {
        int n = M.length;
        int rank = com.getRank();
        int size = com.getSize(); // має бути 16
        int k = (int) Math.round(Math.sqrt(size));
        if (k*k != size) return null;

        // --- Розбиваємо матрицю на блоки ---
        MyMatrixD_MPI[] blocks = new MyMatrixD_MPI[size];
        MyMatrixD_MPI[] localBlock = this.divideToBlocks(k, k, ring);
        blocks[rank] = localBlock[rank];

        // --- Ведучі елементи ---
        int[] track_r = new int[n];
        int[] track_c = new int[n];
        track_r[0] = 0; track_c[0] = 0;

        Element den = ring.numberONE;

        for (int step = 0; step < n-1; step++) {
            // Кожен процес отримує повну матрицю на основі блоків
            MatrixD fullMatrix = MyMatrixD.matrixFromBlocks(localBlock, k, k);

            Element diag = fullMatrix.M[step][step];
            if (diag.isZero(ring)) return null; // невизначений випадок

            // --- Оновлення блоку після кроку ---
            for (int i = step+1; i < n; i++) {
                for (int j = step+1; j < n; j++) {
                    Element p = fullMatrix.M[step][step].multiply(fullMatrix.M[i][j], ring)
                                 .subtract(fullMatrix.M[i][step].multiply(fullMatrix.M[step][j], ring), ring)
                                 .divide(den, ring);
                    fullMatrix.M[i][j] = p;
                }
            }
            den = fullMatrix.M[step][step];

            track_r[step+1] = step+1;
            track_c[step+1] = step+1;

            // --- Розподіляємо оновлену матрицю на блоки ---
            MyMatrixD_MPI[] updatedBlocks = new MyMatrixD_MPI[k*k];
            for (int b = 0; b < k*k; b++) updatedBlocks[b] = new MyMatrixD_MPI(new Element[fullMatrix.M.length/k][fullMatrix.M[0].length/k],0);

            // розбивка повної матриці
            Element[][] fullM = fullMatrix.M;
            int blockH = (n+k-1)/k;
            int blockW = (n+k-1)/k;

            int idx = 0;
            for (int bi=0; bi<k; bi++) {
                for (int bj=0; bj<k; bj++) {
                    Element[][] blk = new Element[blockH][blockW];
                    for (int i=0;i<blockH;i++) {
                        for (int j=0;j<blockW;j++) {
                            int srcI = bi*blockH+i;
                            int srcJ = bj*blockW+j;
                            blk[i][j] = (srcI<n && srcJ<n)? fullM[srcI][srcJ] : ring.numberZERO;
                        }
                    }
                    updatedBlocks[idx++] = new MyMatrixD_MPI(blk,0);
                }
            }

            // --- AllGather: всі процеси отримують оновлені блоки ---
            Object[] sendObj = new Object[]{updatedBlocks[rank]};
            Object[] recvObj = new Object[size];
            TransportObjs.AllGatherObjs(sendObj, recvObj, com);

            for (int b=0;b<size;b++) localBlock[b] = (MyMatrixD_MPI) recvObj[b];
        }

        track[0] = track_r;
        track[1] = track_c;

        // останній елемент діагоналі
        MatrixD finalFull = MyMatrixD.matrixFromBlocks(localBlock,k,k);
        return finalFull.M[n-1][n-1];
    }


}