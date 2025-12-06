package com.y9vad9.uni.openmpi.lab8;

import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.parallel.utils.MPITransport;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;
import java.util.Random;

public class MultiplyVectorToScalar {
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        Ring ring = new Ring("Z[]");
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        //розмiр вектора
        int ord = 8;
        //число
        Element s = NumberZ.valueOf(5);
        //кiлькiсть елементiв для процесорiв з номером > 0
        int k = ord / size;
        //кiлькiсть елементiв вектора
        //для процесора з номером 0

        int n = ord - k * (size - 1);
        if (rank == 0) {
            int den = 10000;
            Random rnd = new Random();
            VectorS B = new VectorS(ord, den,
                new int[]{5}, rnd, ring);
            System.out.println("Vector B = " + B);
            //створення масиву на процесорi з номером 0
            Element[] res0 = new Element[n];
            for (int i = 0; i < n; i++)
                res0[i] = B.V[i].multiply(s, ring);
            //вiдправлення елементiв вектора
            for (int j = 1; j < size; j++) {
                Element[] v = new Element[k];
                System.arraycopy(B.V, n + (j - 1) * k, v, 0, k);
                MPITransport.sendObject(v, j, 100 + j);
            }
            //масив, що має результат
            Element[] result = new Element[ord];
            System.arraycopy(res0, 0, result, 0, n);
            //отримуємо результати вiд кожного процесора
            for (int t = 1; t < size; t++) {
                Element[] resRank = (Element[])
                    MPITransport.recvObject(t, 100 + t);
                System.arraycopy(resRank, 0, result, n +
                    (t - 1) * k, resRank.length);

            }
            System.out.println("B * S = " +
                new VectorS(result).toString(ring));
        } else {
            //програма виконується на процесорi х
            //з номером rank
            System.out.println("I’m processor " + rank);
            //отримуємо частину вектора B вiд процесора
            //з номером 0
            Element[] B = (Element[])
                MPITransport.recvObject(0, 100 + rank);
            System.out.println("rank = " + rank + " B = " + Array.toString(B));
            // створення масиву для результату множення
            // вектора на скаляр
            Element[] result = new Element[k];
            for (int j = 0; j < B.length; j++)
                result[j] = B[j].multiply(s, ring);
            // вiдправлення результату процесору з номером 0
            MPITransport.sendObject(result, 0, 100 + rank);
            System.out.println("send result");
        }
        MPI.Finalize();
    }
}