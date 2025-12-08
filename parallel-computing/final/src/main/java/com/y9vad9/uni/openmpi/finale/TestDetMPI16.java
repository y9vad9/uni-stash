package com.y9vad9.uni.openmpi.finale;

import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

public class TestDetMPI16 {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        if (size != 16) {
            if (rank == 0) {
                System.out.println("Запуск повинен бути на 16 процесорах!");
            }
            MPI.Finalize();
            return;
        }

        Ring ring = new Ring("Z[]");

        // --- приклад матриці 4x4 ---
        Element[][] matData = new Element[][]{
            {ring.numberONE.multiply(new NumberZ(5), ring), ring.numberONE.multiply(new NumberZ(3), ring), ring.numberZERO, ring.numberONE},
            {ring.numberONE.multiply(new NumberZ(4), ring), ring.numberONE, ring.numberZERO, ring.numberONE},
            {ring.numberONE, ring.numberONE, ring.numberONE, ring.numberONE},
            {ring.numberONE, ring.numberONE, ring.numberONE, ring.numberONE.multiply(new NumberZ(2), ring)}
        };

        MyMatrixD_MPI A = new MyMatrixD_MPI(matData, 0);

        int n = matData.length;
        int[][] track = new int[2][n];

        // --- обчислюємо детермінант з блоками і track ---
        Element det = A.detAndTrackBlockedMPI(track, MPI.COMM_WORLD, ring);

        if (rank == 0) {
            System.out.println("det = " + det);
            System.out.println("track = " + Arrays.deepToString(track));
        }

        MPI.Finalize();
    }
}
