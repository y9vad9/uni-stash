package com.y9vad9.uni.openmpi.lab8;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;
import com.mathpar.parallel.utils.MPITransport;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;

public class MatrixMul8 {
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        Ring ring = new Ring("Z[]");
        // iнiцiалiзацiя MPI
        MPI.Init(args);
        // отримання номера процесора
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == 0) {
            // програма виконується на нульовому процесорi
            int ord = 8;
            // ord = розмiр матрицi
            MatrixD A = new MatrixD(ord, ord, 10, ring);
            System.out.println("A = " + A);
            MatrixD B = new MatrixD(ord, ord, 10, ring);
            System.out.println("B = " + B);
            MatrixD D = null;
            // розбиваємо матрицю A на 4 блоки
            MatrixD[] AA = A.splitTo4();
            // розбиваємо матрицю B на 4 частини
            MatrixD[] BB = B.splitTo4();
            // вiдправлення вiд нульового процесора масиву Object
            // процесору rank з iдентифiкатором tag
            MPITransport.sendObjectArray
                (new Object[]{AA[1], BB[2]}, 0, 2, 1, 0);
            MPITransport.sendObjectArray
                (new Object[]{AA[0], BB[1]}, 0, 2, 2, 0);
            MPITransport.sendObjectArray(new Object[]{AA[1], BB[3]}, 0, 2, 3, 0);
            MPITransport.sendObjectArray(new Object[]{AA[2], BB[0]}, 0, 2, 4, 0);
            MPITransport.sendObjectArray(new Object[]{AA[3], BB[2]}, 0, 2, 5, 0);
            MPITransport.sendObjectArray(new Object[]{AA[2], BB[1]}, 0, 2, 6, 0);
            MPITransport.sendObjectArray(new Object[]{AA[3], BB[3]}, 0, 2, 7, 0);
            MatrixD[] DD = new MatrixD[4];
            // залишаємо один блок
            //нульовому процесору для оброблення
            DD[0] = (AA[0].multCU(BB[0], ring)).
                add((MatrixD) MPITransport.recvObject(1, 3),
                    ring);
            DD[1] = (MatrixD) MPITransport.recvObject(2, 3);
            DD[2] = (MatrixD) MPITransport.recvObject(4, 3);
            DD[3] = (MatrixD) MPITransport.recvObject(6, 3);
            D = MatrixD.join(DD);
            System.out.println("RES = " + D);
        } else {
            // програма виконується на процесорi
            // з номером rank
            System.out.println("I’m processor " + rank);
            Object[] b = new Object[2];
            MPITransport.recvObjectArray(b, 0, 2, 0, 0);
            MatrixD[] a = new MatrixD[b.length];
            for (int i = 0; i < b.length; i++)
                a[i] = (MatrixD) b[i];
            MatrixD res = a[0].multCU(a[1], ring);
            if (rank % 2 == 0) {
                MatrixD p = res.add((MatrixD) MPITransport.
                    recvObject(rank + 1, 3), ring);

                MPITransport.sendObject(p, 0, 3);
            } else {
                MPITransport.sendObject(res, rank - 1, 3);
            }
        }

        MPI.Finalize();
    }
}
