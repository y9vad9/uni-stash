/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.MD;

import com.mathpar.func.F;
import mpi.MPI;
import mpi.MPIException;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author shmeleva
 *
 */
public class VectorFunc {
     public static Element[] VectorFuncD(Element[] vectorFunc, Ring ring ){
        Element[] result = new Element[vectorFunc.length];
        for(int i = 0; i<vectorFunc.length; i++){
           result[i] = vectorFunc[i].D(ring);


        }
       return result;
    }
 public static Element[] VectorFuncIntegrate(Element[] vectorFunc, Ring ring ){
         Element[] result = new Element[vectorFunc.length];
         for(int i = 0; i<vectorFunc.length; i++){
             result[i] = vectorFunc[i].integrate(ring);
         }
         return result;
    }

/*    public static void sendObjects(Object[] a, int proc, int tag) throws MPIException {
        byte[] temp = MPI.COMM_WORLD.Object_Serialize(a, 0, a.length, MPI.OBJECT);
        MPI.COMM_WORLD.Isend(temp, 0, temp.length, MPI.BYTE, proc, tag);
    }

    public static Object[] recvObjects(int m, int proc, int tag) throws MPIException {
        Status s = MPI.COMM_WORLD.Probe(proc, tag);
        int n = s.Get_count(MPI.BYTE);
        byte[] arr = new byte[n];
        MPI.COMM_WORLD.Recv(arr, 0, n, MPI.BYTE, proc, tag);
        Object[] res = new Object[m];
        MPI.COMM_WORLD.Object_Deserialize(res, arr, 0, m, MPI.OBJECT);
        return res;
    }*/

    /**
     *
     * @param n - количество координат вектор-функции
     * @param partNumb - количество процессоров
     * @param myrank - номер текущего процессора
     *
     * @return интервал, с какой и по какую задачу следует брать текущему процессору
     */
    public static int[] getMyInterval(int n, int partNumb, int myrank) {
        int[] interval = new int[2];
        interval[0] = (myrank < (partNumb - n % partNumb)) ? (myrank * n / partNumb) : myrank * n / partNumb + (myrank - (partNumb - n % partNumb));
        interval[1] = (myrank < (partNumb - n % partNumb)) ? ((myrank * n / partNumb) + n / partNumb) : myrank * n / partNumb + (myrank - (partNumb - n % partNumb)) + (n / partNumb + 1);
        return interval;
    }

    /**
     * параллельное дифференцирование вектора-функции
     * @param vecFunc - вектор-функция
     * @param ring - кольцо
     * @return продифференцированные координаты вектор-функции
     * @throws MPIException
     */
    public static Element[] parallelVectorVuncD(Element[] vecFunc, Ring ring) throws Exception {
        int rank = MPI.COMM_WORLD.getRank();//номер текущего процессора
        int size = MPI.COMM_WORLD.getSize();//количество процессоров
        int num = vecFunc.length / size;
        int ostatok = vecFunc.length % size;
        int[] interval = getMyInterval(vecFunc.length, size, rank);
        Element[] result = new Element[vecFunc.length];//результат дифференцирования вектора-функции
        int num_func = (rank < (size - ostatok)) ? num : num + 1;//количество функциий для одного процессора
        Element[] temp = new Element[num_func];//результат дифференцирования части вектора-функции(каждого процессора, кроме 0)

        for (int i = 0; i < num_func; i++) {
            if (rank == 0) {//если номер процессора равен 0

                result[interval[0] + i] = vecFunc[interval[0] + i].D(ring);
               // System.out.println("temp1 = " + result[rank * num + i] + " " + i);

            } else {
               // System.out.println("Длина массива = " + temp.length + "proc = " + rank);
                temp[i] = vecFunc[interval[0] + i].D(ring);
                //System.out.println("temp2 = " + temp[i] + " " + i);
                System.out.println("Temp2" + Array.toString(temp));
            }

        }

        if (rank == 0) {
            for (int i = 1; i < size; i++) {
                int n = (i < (size - ostatok)) ? num : num + 1;
                int[] k = getMyInterval(vecFunc.length, size, i);
//!!!!                Object[] noname = recvObjects(n, i, 1);
                Object []noname=new Object[n];
                MPITransport.recvObjectArray(noname,0,noname.length,i,1);
                Element[] sborka = new Element[noname.length];
                for (int p = 0; p < sborka.length; p++) {
                    sborka[p] = (Element) noname[p];

                }
                System.arraycopy(sborka, 0, result, k[0], n);
            }
        } else {
//!!!!            sendObjects(temp, 0, 1);
            MPITransport.sendObjectArray(temp,0,temp.length,0,1);
        }
        return result;
    }

    public static Element[] parallelVectorFuncIntegrate(Element[] vecFunc, Ring ring) throws Exception {
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        int numb_mod = vecFunc.length / size;
        int ostatok = vecFunc.length % size;
        int[] interval = getMyInterval(vecFunc.length, size, rank);
        Element[] temp1 = new Element[vecFunc.length];;
        int end = (rank < (size - ostatok)) ? numb_mod : numb_mod + 1;
        Element[] temp2 = new Element[end];

        for (int i = 0; i < end; i++) {
            if (rank == 0) {

                temp1[interval[0] + i] = vecFunc[interval[0] + i].D(ring);


            } else {

                temp2[i] = vecFunc[interval[0] + i].integrate(ring);

            }

        }

        if (rank == 0) {
            for (int i = 1; i < size; i++) {
                int n = (i < (size - ostatok)) ? numb_mod : numb_mod + 1;
                int start = (i < (size - ostatok)) ? i * numb_mod : i * numb_mod + (i - (size - ostatok));
//!!!!                Object[] noname = recvObjects(n, i, 1);
                Object[] noname=new Object[n];
                MPITransport.recvObjectArray(noname,0,noname.length,i,1);
                Element[] sborka = new Element[noname.length];
                for (int p = 0; p < sborka.length; p++) {
                    sborka[p] = (Element) noname[p];

                }
                System.arraycopy(sborka, 0, temp1, start, n);
            }
        } else {
//!!!!            sendObjects(temp2, 0, 1);
            MPITransport.sendObjectArray(temp2,0,temp2.length,0,1);
        }
        return temp1;
    }
// mpirun -np 8 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes parallel.matrix.VectorFunc
    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        Ring ring = new Ring("R64[x]");
        Element[] vecFunc = new Element[] {new F("\\ctg(3x)", ring), new Polynom("(x^7+x^6)/3x", ring), new Polynom("x^4+x^2+1", ring), new F("\\cos(x^2)", ring)};
        Element[] res = parallelVectorVuncD(vecFunc, ring);
        if (rank == 0) {
            System.out.println("главный ответ" + Array.toString(res));
        }
        MPI.Finalize();
    }
}
