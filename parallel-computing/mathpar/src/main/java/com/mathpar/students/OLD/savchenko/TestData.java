package com.mathpar.students.OLD.savchenko;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;

import java.util.Random;

public class TestData {

    public static MatrixD getTestMatrix(int n, Ring ring) {
        return getTestMatrix(n, n, ring);
    }

    public static MatrixD getTestMatrix(int r, int c, Ring ring) {
        Random random = new Random(1);

        int[][] matrix = new int[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }

        MatrixD matrixD = new MatrixD(matrix, ring);

        return matrixD;
    }

}
