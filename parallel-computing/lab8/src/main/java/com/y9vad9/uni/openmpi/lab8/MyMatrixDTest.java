package com.y9vad9.uni.openmpi.lab8;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;

public class MyMatrixDTest {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        Intracomm com = MPI.COMM_WORLD;
        int rank = com.getRank();
        int size = com.getSize();

        if (size != 4) {
            if (rank == 0) System.out.println("Запустіть з 4 процесами.");
            MPI.Finalize();
            return;
        }

        Ring ring = new Ring("R64[]");

        // --- Створюємо матриці A та B 4x4 ---
        // A і B — це повні матриці, всі елементи = 1
        Element[][] elemsA = new Element[4][4];
        Element[][] elemsB = new Element[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                elemsA[i][j] = ring.numberONE;
                elemsB[i][j] = ring.numberONE;
            }
        }
        MyMatrixD A = new MyMatrixD(elemsA, 0); // повна матриця A
        MyMatrixD B = new MyMatrixD(elemsB, 0); // повна матриця B

        // --- Ділимо матриці на 2x2 блоки ---
        // Ablocks[0] = верхній лівий блок A (A00)
        // Ablocks[1] = верхній правий блок A (A01)
        // Ablocks[2] = нижній лівий блок A (A10)
        // Ablocks[3] = нижній правий блок A (A11)
        MyMatrixD[] Ablocks = A.divideToBlocks(2, 2, ring);
        MyMatrixD[] Bblocks = B.divideToBlocks(2, 2, ring);

        // Кожен процес працює зі своїм блоком
        // rank = 0 -> блокує A00 і B00
        // rank = 1 -> блокує A01 і B01
        // rank = 2 -> блокує A10 і B10
        // rank = 3 -> блокує A11 і B11
        MyMatrixD Ablock = Ablocks[rank];
        MyMatrixD Bblock = Bblocks[rank];

        // --- SquareCombinations на рівні блоків ---
        // Формула на блоковому рівні:
        //
        // resBlock = AA + AB + BA + BB
        //
        // де:
        // AA = Ablock * Ablock
        // AB = Ablock * Bblock
        // BA = Bblock * Ablock
        // BB = Bblock * Bblock
        //
        // Все множення блочне (MultiplyBlockedMatr) та синхронізоване через MPI
        MatrixD resBlock = Ablock.squareComm_AA_AB_BA_BB(
            Bblock,
            ring.numberONE, // коефіцієнт для AA
            ring.numberONE, // коефіцієнт для AB
            ring.numberONE, // коефіцієнт для BA
            ring.numberONE, // коефіцієнт для BB
            com,
            ring
        );

        // --- Збираємо блоки на root ---
        MatrixD[] gathered = TransportObjs.gatherMatrix(resBlock, 0, com);

        if (rank == 0) {
            // root збирає блоки у фінальну матрицю
            // В нашому випадку 4 блоки 2x2 → фінальна 4x4
            MatrixD finalRes = MyMatrixD.matrixFromBlocks(gathered, 2, 2);

            System.out.println("Final result:");
            System.out.println(finalRes.toString());
        }

        MPI.Finalize();
    }
}
