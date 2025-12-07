package com.y9vad9.uni.openmpi.lab8;

import java.io.IOException;
import java.util.Random;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.utils.MPITransport;
import mpi.MPI;
import mpi.MPIException;

/**
 * <h2>Завдання</h2>
 *
 * Напишiть паралельну програму алгоритму множення за
 * Штрассеном на 7 процесорах.
 */
public class StrassenMul7 {
    private static final int TAG_SEND_MATRIX_A = 10;
    private static final int TAG_SEND_MATRIX_B = 11;
    private static final int TAG_RECEIVE_M = 12;

    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();

        if (size != 7) {
            if (rank == 0) System.out.println("Потрібна кількість процесорів: 7. Знайдено: " + size);
            MPI.Finalize();
            return;
        }

        Ring ring = new Ring("Z[]");

        if (rank == 0) {
            // --- Головний процесор ---
            int ord = 4;
            Random rnd = new Random();
            int den = 10000;

            MatrixS A = new MatrixS(ord, ord, den, new int[]{5}, rnd, ring.numberONE(), ring);
            MatrixS B = new MatrixS(ord, ord, den, new int[]{5}, rnd, ring.numberONE(), ring);

            System.out.println("A = " + A.toString(ring));
            System.out.println("B = " + B.toString(ring));

            MatrixS[] AA = A.split();
            MatrixS[] BB = B.split();
            MatrixS A11 = AA[0], A12 = AA[1], A21 = AA[2], A22 = AA[3];
            MatrixS B11 = BB[0], B12 = BB[1], B21 = BB[2], B22 = BB[3];

            // M1..M6 для розподілу
            Element[][] M_operands = new Element[][]{
                {A11.add(A22, ring), B11.add(B22, ring)}, // M1
                {A21.add(A22, ring), B11},                // M2
                {A11, B12.subtract(B22, ring)},           // M3
                {A22, B21.subtract(B11, ring)},           // M4
                {A11.add(A12, ring), B22},                // M5
                {A21.subtract(A11, ring), B11.add(B12, ring)} // M6
            };

            // --- Відправка M1..M6 rank 1..6 ---
            for (int i = 0; i < 6; i++) {
                int dest = i + 1; // rank 1..6
                MPITransport.sendObject(M_operands[i][0], dest, TAG_SEND_MATRIX_A);
                MPITransport.sendObject(M_operands[i][1], dest, TAG_SEND_MATRIX_B);
            }

            // --- Обчислення M7 локально ---
            MatrixS M7 = A12.subtract(A22, ring).multiply(B21.add(B22, ring), ring);

            // --- Прийом результатів M1..M6 ---
            MatrixS[] M = new MatrixS[7];
            for (int i = 0; i < 6; i++) {
                M[i] = (MatrixS) MPITransport.recvObject(i + 1, TAG_RECEIVE_M);
            }
            M[6] = M7;

            // --- Формування C ---
            MatrixS C11 = M[0].add(M[3], ring).subtract(M[4], ring).add(M[6], ring);
            MatrixS C12 = M[2].add(M[4], ring);
            MatrixS C21 = M[1].add(M[3], ring);
            MatrixS C22 = M[0].subtract(M[1], ring).add(M[2], ring).add(M[5], ring);

            MatrixS[] DD = {C11, C12, C21, C22};
            MatrixS D = MatrixS.join(DD);

            System.out.println("RESULT C = A * B = " + D.toString(ring));

        } else if (rank >= 1 && rank <= 6) {
            // --- Робітники rank 1..6 ---
            MatrixS X = (MatrixS) MPITransport.recvObject(0, TAG_SEND_MATRIX_A);
            MatrixS Y = (MatrixS) MPITransport.recvObject(0, TAG_SEND_MATRIX_B);

            MatrixS M_res = X.multiply(Y, ring);

            MPITransport.sendObject(M_res, 0, TAG_RECEIVE_M);
        }

        MPI.Finalize();
    }
}
