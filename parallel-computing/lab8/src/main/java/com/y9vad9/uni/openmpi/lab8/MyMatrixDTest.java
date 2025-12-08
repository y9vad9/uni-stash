package com.y9vad9.uni.openmpi.lab8;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import mpi.MPI;
import mpi.MPIException;
import mpi.Intracomm;

public class MyMatrixDTest {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        Intracomm com = MPI.COMM_WORLD;
        int rank = com.getRank();
        int size = com.getSize();

        if (size != 16) {
            if (rank == 0) System.out.println("Запустіть з 16 процесами.");
            MPI.Finalize();
            return;
        }

        Ring ring = new Ring("R64[]");

        int n = 8; // розмір матриці 8x8
        Element[][] elemsA = new Element[n][n];
        Element[][] elemsB = new Element[n][n];

        // Заповнюємо матриці одиницями
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                elemsA[i][j] = ring.numberONE;
                elemsB[i][j] = ring.numberONE;
            }
        }

        MyMatrixD A = new MyMatrixD(elemsA, 0);
        MyMatrixD B = new MyMatrixD(elemsB, 0);

        // --- Розбиваємо на 4x4 блоки (16 блоків для 16 процесорів) ---
        int k = 4; // розмір сітки блоків
        MyMatrixD[] Ablocks = A.divideToBlocks(k, k, ring);
        MyMatrixD[] Bblocks = B.divideToBlocks(k, k, ring);

        MyMatrixD Ablock = Ablocks[rank];
        MyMatrixD Bblock = Bblocks[rank];

        // --- Блочне множення на процесорі ---
        MatrixD resBlock = Ablock.squareComm_AA_AB_BA_BB(
            Bblock,
            ring.numberONE,
            ring.numberONE,
            ring.numberONE,
            ring.numberONE,
            com,
            ring
        );

        // --- Збираємо блоки на root ---
        MatrixD[] gathered = TransportObjs.gatherMatrix(resBlock, 0, com);

        if (rank == 0) {
            // Збираємо фінальну матрицю
            MatrixD finalRes = MyMatrixD.matrixFromBlocks(gathered, k, k);
            System.out.println("Final result 8x8:");
            System.out.println(finalRes.toString());
        }

        MPI.Finalize();
    }
}
