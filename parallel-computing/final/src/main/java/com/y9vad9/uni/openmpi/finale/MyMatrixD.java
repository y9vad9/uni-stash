package com.y9vad9.uni.openmpi.finale;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import mpi.Intracomm;
import mpi.MPIException;

/**
 * Розширена матриця MatrixD з підтримкою блокової множини
 * та MPI-паралельних операцій.
 */
public class MyMatrixD extends MatrixD {

    // --- Конструктор ---
    public MyMatrixD(Element[][] blk, int i) {
        super(blk, i); // виклик батьківського конструктора MatrixD
    }

    // --- Збірка матриці з блоків ---
    public static MatrixD matrixFromBlocks(MatrixD[] blocks, int blRows, int blCols) {
        // blocks = масив блоків, blRows/blCols = кількість блоків по рядках/стовпцях
        if (blocks == null || blocks.length != blRows * blCols) return null;

        int br = blocks[0].M.length; // висота блоку
        int bc = blocks[0].M[0].length; // ширина блоку

        Element[][] res = new Element[br * blRows][bc * blCols];

        int idx = 0;
        for (int i = 0; i < blRows; i++) {
            for (int j = 0; j < blCols; j++) {
                MatrixD b = blocks[idx++]; // поточний блок
                for (int r = 0; r < br; r++) {
                    // копіюємо рядки блоку в результат (матрицю)
                    System.arraycopy(
                        b.M[r], 0,
                        res[i * br + r], j * bc,
                        bc
                    );
                }
            }
        }
        return new MatrixD(res, 0);
    }

    // --- Розбиття матриці на блоки ---
    public MyMatrixD[] divideToBlocks(int blRows, int blCols, Ring ring) {
        int n = M.length; // кількість рядків матриці
        int m = M[0].length;  // кількість стовпців матриці

        // розмір блоку (округлення вгору, щоб покрити всю матрицю)
        int blockH = (n + blRows - 1) / blRows;
        int blockW = (m + blCols - 1) / blCols;

        MyMatrixD[] res = new MyMatrixD[blRows * blCols];
        Element zero = ring.numberZERO;

        int idx = 0;
        for (int br = 0; br < blRows; br++) {
            for (int bc = 0; bc < blCols; bc++) {
                Element[][] blk = new Element[blockH][blockW];

                for (int i = 0; i < blockH; i++) {
                    int srcI = br * blockH + i;
                    for (int j = 0; j < blockW; j++) {
                        int srcJ = bc * blockW + j;
                        // якщо виходимо за межі, заповнюємо нулем
                        blk[i][j] = (srcI < n && srcJ < m) ? M[srcI][srcJ] : zero;
                    }
                }
                res[idx++] = new MyMatrixD(blk, 0);
            }
        }
        return res;
    }

    // --- Множення блочної матриці через MPI ---
    public MatrixD MultiplyBlockedMatr(MatrixD Bblock,
                                       Intracomm com,
                                       Ring ring) throws MPIException {
        int p = com.getSize(); // кількість процесів
        int k = (int) Math.round(Math.sqrt(p)); // розмір сітки блоків
        if (k * k != p) return null;

        int rank = com.getRank();
        int bi = rank / k; // номер рядка блока
        int bj = rank % k; // номер стовпця блока

        // --- Збираємо всі блоки A і B з усіх процесів ---
        Object[] sendA = new Object[]{this};  // локальний блок A
        Object[] sendB = new Object[]{Bblock}; // локальний блок B

        Object[] recvA = new Object[p]; // всі блоки A
        Object[] recvB = new Object[p]; // всі блоки B

        TransportObjs.AllGatherObjs(sendA, recvA, com);
        TransportObjs.AllGatherObjs(sendB, recvB, com);

        // --- Результуюча матриця (заповнена нулями) ---
        int n = this.M.length;
        MatrixD acc = new MatrixD(new Element[n][n], 0);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                acc.M[i][j] = ring.numberZERO;

        // --- Блочне множення: acc += A_block_row_s * B_block_s_col ---
        for (int s = 0; s < k; s++) {
            MatrixD A = (MatrixD) recvA[bi * k + s]; // блок з тієї ж рядкової групи
            MatrixD B = (MatrixD) recvB[s * k + bj]; // блок з тієї ж стовпцевої групи
            MatrixD C = A.multCU(B, ring); // локальне множення
            acc = acc.add(C, ring); // накопичення результату
        }

        return acc;
    }

    // --- Блочне комбіноване множення AA+AB+BA+BB ---
    public MatrixD squareComm_AA_AB_BA_BB(MyMatrixD Bblock,
                                          Element naa, Element nab,
                                          Element nba, Element nbb,
                                          Intracomm com, Ring ring)
        throws MPIException {

        int p = com.getSize();
        int k = (int) Math.round(Math.sqrt(p));
        if (k * k != p) return null;

        MatrixD res = null;

        // AA = A*A
        if (!naa.isZero(ring)) {
            MatrixD AA = this.MultiplyBlockedMatr(this, com, ring).multiplyByScalar(naa, ring);
            res = AA;
        }

        // AB = A*B
        if (!nab.isZero(ring)) {
            MatrixD AB = this.MultiplyBlockedMatr(Bblock, com, ring).multiplyByScalar(nab, ring);
            res = (res == null) ? AB : res.add(AB, ring);
        }

        // BA = B*A
        if (!nba.isZero(ring)) {
            MatrixD BA = Bblock.MultiplyBlockedMatr(this, com, ring).multiplyByScalar(nba, ring);
            res = (res == null) ? BA : res.add(BA, ring);
        }

        // BB = B*B
        if (!nbb.isZero(ring)) {
            MatrixD BB = Bblock.MultiplyBlockedMatr(Bblock, com, ring).multiplyByScalar(nbb, ring);
            res = (res == null) ? BB : res.add(BB, ring);
        }

        return res;
    }
}
