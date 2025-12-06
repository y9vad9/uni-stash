package com.mathpar.students.OLD.llp2.students;

import java.util.Random;
import com.mathpar.matrix.MatrixS;

import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import mpi.*;
import com.mathpar.parallel.utils.parallel_debugger.ParallelDebug;

//mpirun C java -cp /home/student/45/parca/build/web/WEB-INF/classes -Djava.library.path=$LD_LIBRARY_PATH llp2.student.Parakhina
/**
 *
 * @author nasty
 */
public class Parakhina {

    static int tag = 0;

    public static void main(String[] args) throws MPIException {
        MPI.Init(new String[0]);// инициализация
        int rank = MPI.COMM_WORLD.getRank();
        int r = MPI.COMM_WORLD.getSize();
        ParallelDebug bp = new ParallelDebug(MPI.COMM_WORLD);
        bp.paddEvent("go", "");
        Ring ring = new Ring("Z[x]Z[c]");// задаем кольцо
        Random ran = new Random();
        Polynom one = new Polynom(NumberZ.ONE);
        int[] randomType = new int[] {3, 0, 100, 3};
        MatrixS a = new MatrixS(11, 10, 100, randomType, ran, one, ring);// генерируем
        // матрицу
		/*
         * Начитается программа 0 процессора запись матрицы в массив Element
         * разбиение массива на части и рассылка процессорам сборка вывод
         * продифференцированной матрицы
         */

        if (rank == 0) {
            Element[][] war = new Element[r][];
            int b = 0;
            // считаем количество элементов исходной матрицы
            for (int i = 0; i < a.M.length; i++) {
                int sum1 = a.M[i].length;
                b = sum1 + b;
            }
            int c1 = b % r;// столько процессоров получат b / r+1элементов
            int c2 = b / r;
            int c3 = r - c1;// столько процессоров получат b / r элементов
            int c4 = c2 + 1;
            int c5 = (c1 - 1);
            // отправляем параметры остальным процессорам
            Integer[] param = {c1, c2, c3, c4, c5, b};
            for (int i = 1; i < r; i++) {
                //!!!! MPI.COMM_WORLD.Send(param, 0, 5, //!!!! MPI.OBJECT, i, 1);
            }
            System.out.println("Matrix A =  " + a);
            System.out.println("Количество элементов матрицы = " + b);
            System.out.println("Количество процессоров = " + r);
            System.out.println("Процессоры с 0 по " + c5 + " получат по " + c4
                    + " элемента. Остальные " + c3 + " получат по " + c2);
            Element[] ar = new Element[b];

            int t = 0;
            // копирование элементов исходной матрицы в массив Element ar
            for (int i = 0; i < a.M.length; i++) {
                System.arraycopy(a.M[i], 0, ar, 0 + t, a.M[i].length);
                t += a.M[i].length;
            }
            System.out.println("ARRAY = " + Array.toString(ar));
            // для процессоров rank,которых меньше с1,начинаем рассылку
            for (int i = 0; i < c1; i++) {
                war[i] = new Element[c4];
                System.arraycopy(ar, c4 * i, war[i], 0, c4);
                System.out.println("WAR  = " + Array.toString(war[i]));
                bp.paddEvent("one", "");
                //!!!! MPI.COMM_WORLD.Send(war[i], 0, c4, //!!!! MPI.OBJECT, i % r, 111);
            }
            // 0 процессор дифференцирует свою часть
            for (int j = 0; j < c4; j++) {
                bp.paddEvent("2", "");
                war[0][j] = war[0][j].D(ring);
            }
            System.out.println("RAW [" + MPI.COMM_WORLD.getRank() + "]"
                    + Array.toString(war[0]));
            // рассылка для процессоров rank, которых больше с1
            for (int i = c1; i < r; i++) {
                war[i] = new Element[c2];
                System.arraycopy(ar, c2 * i, war[i], 0, c2);
                System.out.println("WAR = " + Array.toString(war[i]));
                bp.paddEvent("two", "");
                //!!!! MPI.COMM_WORLD.Send(war[i], 0, c2, //!!!! MPI.OBJECT, i % r, 2);
            }
			// прием продифференцированных частей
            // сначала для процессоров rank,которых меньше с1
            // далее для всех остальных
            for (int i = 1; i < c1; i++) {
                //!!!! MPI.COMM_WORLD.Recv(war[i], 0, c4, //!!!! MPI.OBJECT, i % r, 3);
                System.out.println("QWERTY 1" + Array.toString(war[i]));
            }
            for (int i = c1; i < r; i++) {
                //!!!! MPI.COMM_WORLD.Recv(war[i], 0, c2, //!!!! MPI.OBJECT, i % r, 4);
                System.out.println("QWERTY 2" + Array.toString(war[i]));
            }
            int summ0 = 0;
            int summ = 0;
            int summ1 = 0;
            int summ2 = 0;
            for (int i = 1; i < c1; i++) {
                summ0 = war[i].length;
                summ = summ + summ0;
            }
            for (int i = c1; i < r; i++) {
                summ1 = war[i].length;
                summ2 = summ2 + summ1;
            }
            int S = summ + summ2 + war[0].length;
            Element[] arr = new Element[S];
            Element[] det0 = new Element[summ];
            Element[] det1 = new Element[summ2];
            Element[] det = new Element[summ + summ2];
            int k = 0;
            for (int i = 1; i < c1; i++) {
                for (int j = 0; j < c4; j++) {
                    det0[k] = war[i][j];
                    k = k + 1;
                }
            }
            int p = 0;
            for (int i = c1; i < r; i++) {
                for (int j = 0; j < c2; j++) {
                    det1[p] = war[i][j];
                    p = p + 1;
                }
            }
            for (int i = 0; i < det0.length; i++) {
                det[i] = det0[i];
                int y = det0.length;
                for (int j = 0; j < det1.length; j++) {
                    det[j + y] = det1[j];
                }

            }
            // запись полученных частей в один массив
            for (int i = 0; i < war[0].length; i++) {
                arr[i] = war[0][i];
                int q = war[0].length;
                for (int j = 0; j < det.length; j++) {
                    arr[j + q] = det[j];
                }
            }

            System.out.println(" NEW ARRAY = " + Array.toString(arr));
			// копирование элементов массива в новую матрицу исходной
            // размерности
            Element[][] Matrix = new Element[a.M.length][a.colNumb];
            int d = 0;
            for (int i = 0; i < a.M.length; i++) {
                for (int j = 0; j < a.colNumb; j++) {
                    Matrix[i][j] = arr[j + d];
                    if (j == arr.length - 1) {
                        continue;
                    }
                }
                d = d + a.colNumb;
            }
            System.out.println("MATRIX = " + Array.toString(Matrix, ring));
            // программа для остальных процессоров
        } else {
            // прием параметров от 0 процессора
            Integer[] p = new Integer[6];
            //!!!! MPI.COMM_WORLD.Recv(p, 0, 5, //!!!! MPI.OBJECT, 0, 1);
            int c11 = p[0];
            int c12 = p[1];
            int c14 = p[3];
            Element[][] raw = new Element[r][];
			// в соответсвии со своим Rank процессоры принимают части от
            // нулевого процессора и дифференцируют их
            if (MPI.COMM_WORLD.getRank() <= c11 - 1) {
                for (int i = 1; i < c11; i++) {
                    raw[i] = new Element[c14];
                    bp.paddEvent("1", "");
                    //!!!! MPI.COMM_WORLD.Recv(raw[i], 0, c14, //!!!! MPI.OBJECT, 0, 111);
                    for (int j = 0; j < c14; j++) {
                        bp.paddEvent("2", "");
                        raw[i][j] = raw[i][j].D(ring);
                    }
                    System.out.println("RAW [" + MPI.COMM_WORLD.getRank() + "] = "
                            + Array.toString(raw[i]));
                    //!!!! MPI.COMM_WORLD.Send(raw[i], 0, c14, //!!!! MPI.OBJECT, 0, 3);
                }
            } else {
                for (int i = c11; i < r; i++) {
                    raw[i] = new Element[c12];
                    bp.paddEvent("to", "");
                    //!!!! MPI.COMM_WORLD.Recv(raw[i], 0, c12, //!!!! MPI.OBJECT, 0, 2);
                    for (int j = 0; j < c12; j++) {
                        bp.paddEvent("2", "");
                        raw[i][j] = raw[i][j].D(ring);
                    }
                    System.out.println("RAW [" + MPI.COMM_WORLD.getRank() + "] = "
                            + Array.toString(raw[i]));
                    //!!!! MPI.COMM_WORLD.Send(raw[i], 0, c12, //!!!! MPI.OBJECT, 0, 4);
                }
            }

        }
        bp.generateDebugLog();
        //!!!! MPI.Finalize();
    }
}
