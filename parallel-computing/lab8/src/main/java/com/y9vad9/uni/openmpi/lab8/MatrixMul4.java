package com.y9vad9.uni.openmpi.lab8;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;
import com.mathpar.parallel.utils.MPITransport;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;

public class MatrixMul4 {
    public static MatrixD mmultiply(MatrixD a, MatrixD b,
                                    MatrixD c, MatrixD d, Ring ring) {
        // помножимо a на b, с на d та додамо результати
        return (a.multCU(b, ring)).add(c.multCU(d, ring), ring);
    }

    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        Ring ring = new Ring("Z[]");
        //iнiцiалiзацiя MPI
        MPI.Init(args);
        // отримання номера процесора
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == 0) {
            // програма виконується на нульовому процесорi
            // розмiр матриць
            int ord = 4;
            MatrixD A = new MatrixD(ord, ord, 5, ring);
            MatrixD B = new MatrixD(ord, ord, 5, ring);
            MatrixD[] DD = new MatrixD[4];
            MatrixD CC = null;
            // розбиваємо матрицю A на 4 частини
            MatrixD[] AA = A.splitTo4();
            // розбиваємо матрицю B на 4 частини
            MatrixD[] BB = B.splitTo4();
            // вiдправлення вiд нульового процесора масиву Object
            // процесору 1 з iдентифiкатором tag = 1

            MPITransport.sendObjectArray(
                new Object[]{AA[0], BB[1], AA[1], BB[3]},
                0,
                4,
                1,
                1
            );
            // вiдправлення вiд нульового процесора масиву Object
            // процесору 2 з iдентифiкатором tag = 2
            MPITransport.sendObjectArray(new Object[]{
                AA[2], BB[0], AA[3], BB[2]}, 0, 4, 2, 2);
            // вiдправлення вiд нульового процесора масиву Object
            // процесору 3 з iдентифiкатором tag = 3
            MPITransport.sendObjectArray(new Object[]{
                AA[2], BB[1], AA[3], BB[3]}, 0, 4, 3, 3);
            // залишаємо один блок нульовому процесору для
            // оброблення
            DD[0] = (AA[0].multCU(BB[0], ring)).
                add(AA[1].multCU(BB[2], ring), ring);
            // отримуємо результат вiд першого процесора
            DD[1] = (MatrixD) MPITransport.recvObject(1, 1);
            System.out.println("recv 1 to 0");
            // отримуємо результат вiд другого процесора
            DD[2] = (MatrixD) MPITransport.recvObject(2, 2);
            System.out.println("recv 2 to 0");
            // отримуємо результат вiд третього процесора
            DD[3] = (MatrixD) MPITransport.recvObject(3, 3);
            System.out.println("recv 3 to 0");
            //процедура збору матрицi з блокiв DD[i]
            //(i=0,...,3)
            CC = MatrixD.join(DD);
            System.out.println("RES= " + CC);
        } else {
            // програма виконується на процесорi
            // з номером rank
            System.out.println("I’m processor " + rank);
            // отримуємо масив Object з блоками матриць
            // вiд нульового процесора
            Object[] n = new Object[4];
            MPITransport.recvObjectArray(n, 0, 4, 0, rank);
            MatrixD a = (MatrixD) n[0];
            MatrixD b = (MatrixD) n[1];
            MatrixD c = (MatrixD) n[2];

            MatrixD d = (MatrixD) n[3];
            // перемножуємо та складаємо блоки матриць
            MatrixD res = mmultiply(a, b, c, d, ring);
            // надсилаємо результат обчислень вiд
            // процесора rank нульовому процесору
            System.out.println("res = " + res);
            MPITransport.sendObject(res, 0, rank);
            // повiдомлення на консоль про те, що
            // результат буде надiслано
            System.out.println("send result");
        }
        MPI.Finalize();
    }
}