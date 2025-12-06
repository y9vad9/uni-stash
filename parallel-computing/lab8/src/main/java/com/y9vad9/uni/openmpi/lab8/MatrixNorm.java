package com.y9vad9.uni.openmpi.lab8;

import mpi.MPI;
import mpi.MPIException;
import java.util.Random;

public class MatrixNorm {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        int n = 12; // розмір квадратної матриці
        double[][] matrix = null;

        if (rank == 0) {
            Random rnd = new Random();
            matrix = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matrix[i][j] = rnd.nextDouble() * 10;
                }
            }
            System.out.println("Matrix generated on rank 0:");
            for (double[] row : matrix) {
                for (double val : row) {
                    System.out.printf("%6.2f ", val);
                }
                System.out.println();
            }
        }

        // Кількість рядків на процесор
        int rowsPerProc = n / size;
        int remainder = n % size;

        int start = rank * rowsPerProc + Math.min(rank, remainder);
        int end = start + rowsPerProc + (rank < remainder ? 1 : 0);

        // Локальна сума по рядках
        double localMaxRowSum = 0;
        double[][] localMatrix = new double[end - start][n];

        if (rank == 0) {
            // Розсилка рядків
            for (int p = 0; p < size; p++) {
                int s = p * rowsPerProc + Math.min(p, remainder);
                int e = s + rowsPerProc + (p < remainder ? 1 : 0);

                if (p == 0) {
                    for (int i = s; i < e; i++) {
                        System.arraycopy(matrix[i], 0, localMatrix[i - s], 0, n);
                    }
                } else {
                    double[] sendBuffer = new double[(e - s) * n];
                    int idx = 0;
                    for (int i = s; i < e; i++) {
                        for (int j = 0; j < n; j++) {
                            sendBuffer[idx++] = matrix[i][j];
                        }
                    }
                    MPI.COMM_WORLD.send(sendBuffer, sendBuffer.length, MPI.DOUBLE, p, 0);
                }
            }
        } else {
            double[] recvBuffer = new double[(end - start) * n];
            MPI.COMM_WORLD.recv(recvBuffer, recvBuffer.length, MPI.DOUBLE, 0, 0);
            for (int i = 0; i < end - start; i++) {
                System.arraycopy(recvBuffer, i * n, localMatrix[i], 0, n);
            }
        }

        // Обчислюємо локальну норму (максимум суми рядка)
        for (double[] row : localMatrix) {
            double sum = 0;
            for (double val : row) {
                sum += Math.abs(val);
            }
            if (sum > localMaxRowSum) localMaxRowSum = sum;
        }

        // Збираємо глобальну норму
        double[] globalMax = new double[1];
        MPI.COMM_WORLD.reduce(new double[]{localMaxRowSum}, globalMax, 1, MPI.DOUBLE, MPI.MAX, 0);

        if (rank == 0) {
            System.out.println("Matrix norm (max row sum) = " + globalMax[0]);
        }

        MPI.Finalize();
    }
}
