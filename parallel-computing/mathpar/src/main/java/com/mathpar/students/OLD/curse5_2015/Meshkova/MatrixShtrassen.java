/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.curse5_2015.Meshkova;

import java.util.Random;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;

/**
 *
 *@author tatyana
 *
 */
public class MatrixShtrassen {
    static int tag = 0;
    static Ring ring = new Ring("R64[x,y,z]");

    /**
     * Вычисление массива детей от данного процессора
     *
     * @param parent - процессор родитель
     * @param level - уровень. вычисляется в функции shiftProc
     *
     * @return массив детей процессора
     */
    public static int[] numChildProc(int parent, int level) {
        int[] array_of_child = new int[7];
        for (int i = 0; i < array_of_child.length; i++) {
            array_of_child[i] = (int) (i * Math.pow(7, level)) + parent;
        }
        return array_of_child;
    }

    /**
     * Вычисление номера процессора-родителя по потомку и уровню\\ тем самым
     * находим номер процессора-родителя\\
     *
     * @param child - потомок-процессор, родителя которого надо найти
     * @param num_of_level - номер уровня на котором находится текущий процессор
     *
     * @return индекс процессора родителя
     *
     * Каждый уровень исчисляется 7^n процессоров, где n - уровень нахождение
     * искомого эл-та определяется
     *
     */
    public static int numbParentProc(int child, int num_of_level) {
        return child % (int) Math.pow(7, num_of_level);

    }

    /**
     * Нахождение уровня родителя процессора c помощью массива с заранее
     * заполненными максимальными индексами процессоров в каждом уровне
     * определяем соответственно уровень, в котором находится процессор
     * num_of_proc
     *
     * @param num_of_proc - номер процессора, сдвиг родителя которого нужно
     * найти
     *
     * @return сдвиг родителя для получения этого процессора 7^1 - 1 7^2 - 1 ...
     */
    public static int shiftProc(int num_of_proc) {

        int[] array_of_levels = new int[10];
        for (int i = 0; i < 10; i++) {
            array_of_levels[i] = (int) Math.pow(7.0, i + 1) - 1;
        }
        /*
         уровни " 0 нулевой" =  0 - 6
         "1 первый " =  7 - 48 
         "2 второй " = 49 - 342 
         ....  
         определение принадлежности процессора уровню => номер уровня
         */
        int level = 0;
        for (int i = 1; i < array_of_levels.length; i++) {
            if (num_of_proc > array_of_levels[i - 1] && num_of_proc <= array_of_levels[i]) {
                level = i;
            }
        }
        return level;  // номер уровня, передается в метод numChildProc
    }

    /**
     * Вычисление произведения матриц на параллельной машине с числом
     * процессоров кратным степени 7 Рекурсиная процедура вычисления
     * произведения матриц основана на древовидной рассылке по узлам блоков
     * матриц и вычислений произведения на каждом узле с последующим сбором на
     * головном процессоре блоков
     *
     * @param A - матрица для вычислений
     * @param B - матрица для вычислений
     * @param N_crit - наименьший размер матрицы, критическое число выхода к
     * подсчёту последовательного алгоритма
     * @param num_of_proc - индекс процессора вошедшего в процедуру
     * @param level- уровень, используемый для порождения детей-процессоров
     *
     * @return произведение матриц A*B
     *
     * @throws MPIException
     */
    public static MatrixS Slau_Seven_Proc(MatrixS A, MatrixS B, int N_crit, int num_of_proc, int level) throws MPIException {
        MatrixS[] C = new MatrixS[4];   // блоки матрицы-решения
        MatrixS[] block_of_Shtrassen_a = new MatrixS[7];   // подзадачи метода Штрассена 
        MatrixS Result;

        if (N_crit < A.size) {
            // вычисление номера детей на следующем уровне
            // где num_of_childs[0] совпадает с номером текущего процессора(им же и является)
            int[] num_of_childs = numChildProc(num_of_proc, level);
            //   System.out.println("CHILD = "+Array.toString(array_of_child)+" num_of_proc="+num_of_proc);
            /*
             A = [[0, 1],
             [2, 3]].
             B = [[0, 1],
             [2, 3]].
             */
            MatrixS[] blocks_A = A.split();
            MatrixS[] blocks_B = B.split();

            /*
             формула
             0 - с какого элемента начинается массив
             2 - количество передаваемых элементов в этом масссиве 
             array_of_child - номер процессора, которому передаю
             tag - метка
             */
            try {
                MPITransport.sendObjectArray(new Object[] {blocks_A[2].add(blocks_A[3], ring), blocks_B[0]}, 0, 2, num_of_childs[1], tag);
                MPITransport.sendObjectArray(new Object[] {blocks_A[0], blocks_B[1].subtract(blocks_B[3], ring)}, 0, 2, num_of_childs[2], tag);
                MPITransport.sendObjectArray(new Object[] {blocks_A[3], blocks_B[2].subtract(blocks_B[0], ring)}, 0, 2, num_of_childs[3], tag);
                MPITransport.sendObjectArray(new Object[] {blocks_A[0].add(blocks_A[1], ring), blocks_B[3]}, 0, 2, num_of_childs[4], tag);
                MPITransport.sendObjectArray(new Object[] {blocks_A[2].subtract(blocks_A[0], ring), blocks_B[1].add(blocks_B[0], ring)}, 0, 2, num_of_childs[5], tag);
                MPITransport.sendObjectArray(new Object[] {blocks_A[1].subtract(blocks_A[3], ring), blocks_B[2].add(blocks_B[3], ring)}, 0, 2, num_of_childs[6], tag);
            } catch (Exception e) {
            }

            Status st = null;
            block_of_Shtrassen_a[0] = Slau_Seven_Proc(blocks_A[0].add(blocks_A[3], ring), blocks_B[0].add(blocks_B[3], ring), N_crit, num_of_proc, level + 1);

// задержка программы для ожидания со всех процессоров, затем приемка со всех
            for (int i = 1; i < 7; i++) {
                while (st == null) {
                    st = MPI.COMM_WORLD.probe(num_of_childs[i], tag);
                }

                try {
                    // приемка задачи от заданного процессора
                    block_of_Shtrassen_a[i] = (MatrixS) MPITransport.recvObject(num_of_childs[i], tag);
                    // System.out.println("recvobj   "+block_of_Shtrassen_a[i].toString(ring)+" num_of_proc "+ array_of_child[i]);
                    //a[i]=(MatrixS)FOR_block_of_Shtrassen_a[0];
                } catch (Exception e) {
                }

            }
            /*
             финальные формулы м-да Штрассена 
             */
            //   System.out.println("куски = "+Array.toString(block_of_Shtrassen_a));
            C[0] = block_of_Shtrassen_a[0].add(block_of_Shtrassen_a[3], ring).subtract(block_of_Shtrassen_a[4], ring).add(block_of_Shtrassen_a[6], ring);
            //  System.out.println(" C[0] "+C[0].toString(ring));
            C[1] = block_of_Shtrassen_a[2].add(block_of_Shtrassen_a[4], ring);
            //   System.out.println(" C[1] "+C[1].toString(ring));
            C[2] = block_of_Shtrassen_a[1].add(block_of_Shtrassen_a[3], ring);
            //  System.out.println(" C[2] "+C[2].toString(ring));
            C[3] = block_of_Shtrassen_a[0].subtract(block_of_Shtrassen_a[1], ring).add(block_of_Shtrassen_a[2], ring).add(block_of_Shtrassen_a[5], ring);
            //System.out.println(" C[3] "+C[3].toString(ring));
            Result = MatrixS.join(C);
            // System.out.println("ответ");

        } else {
            // System.out.println("ELSE");
            Result = A.multiply(B, ring);
            //  System.out.println("EDNELSE = "+ Result.toString(ring)+"   num_of_proc = "+ num_of_proc);
        }

        return Result;
    }

    /**
     * Запуск вычисления умножения матриц с использованием\\ древовидного
     * алгоритма пересылки блоков матриц.\\ Запуск осуществляется на нулевом
     * процессоре\\ в это время остальные ждут получения блока матрицы\\ после
     * получения запускается процесс вычисления на узлах (остальных
     * процессорах)\\
     *
     * @param A - матрица для вычислений
     * @param B - матрица для вычислений
     * @param N_crit - критическое число выхода
     *
     * @return - произведение матриц A*B
     *
     * @throws MPIException
     */
    public static MatrixS multiplyMatrix(MatrixS A, MatrixS B, int N_crit) throws Exception {
        int myrank = MPI.COMM_WORLD.getRank();

        MatrixS result = null;
        if (myrank == 0) {
            result = Slau_Seven_Proc(A, B, N_crit, myrank, 0);
        } else {
            Status st = null;
// задержка программы до завершения подсчётов
            while (st == null) {
                st = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
            }
            // вычисления уровня, на котором находится текущий процессор
            int shift = shiftProc(myrank);
            // номер родителя
            int parent_proc = numbParentProc(myrank, shift);

            Object[] FOR_block_of_Shtrassen_a = new Object[2];
            MPITransport.recvObjectArray(FOR_block_of_Shtrassen_a, 0, 2, parent_proc, tag);
            MatrixS a = (MatrixS) FOR_block_of_Shtrassen_a[0];
            //  System.out.println("block_of_Shtrassen_a = "+block_of_Shtrassen_a.toString(ring)+"  rank = "+myrank);
            MatrixS b = (MatrixS) FOR_block_of_Shtrassen_a[1];
            //  System.out.println("b = "+b.toString(ring)+"  rank = "+myrank);

            MatrixS Slau_blocks_of_Shtrassen = Slau_Seven_Proc(a, b, N_crit, myrank, shift + 1);
            //   System.out.println("result = "+Slau_blocks_of_Shtrassen+"  rank = "+myrank + "  parent_proc "+ parent_proc);
            MPITransport.sendObject(Slau_blocks_of_Shtrassen, parent_proc, tag);

        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        MatrixS A = null;
        MatrixS B = null;
        // int[] randomType = new int[] {1, 100, 3};

        /*
         № 1
         */
        int N_crit = 64;
        int[] randomType = new int[] {3};
        Random ran = new Random();
        Element one = new NumberZ();
        if (MPI.COMM_WORLD.getRank() == 0) {
            A = new MatrixS(128, 128, 100, randomType, ran, one, ring);
            B = new MatrixS(128, 128, 100, randomType, ran, one, ring);
         //   System.out.println("A= " + A.toString(ring));
            //   System.out.println("B= " + B.toString(ring));
        }
        // long t1 = System.currentTimeMillis();
        MatrixS result = multiplyMatrix(A, B, N_crit);
        if (MPI.COMM_WORLD.getRank()
                == 0) {
            //    System.out.println("C =" + result.toString(ring));
            MatrixD AAA = new MatrixD(A);
            MatrixD BBB = new MatrixD(B);

            BBB = (MatrixD) AAA.multiply(BBB, ring);
            // System.out.println("A = "+AAA+"   \n B  = "+BBB);
            if (new MatrixD(result).compareTo(BBB, ring) == 0) {
                System.out.println("TRUE, Matrix 1 slau");
            }
        }
        //System.out.println("C= "+result.toString(ring));
        // long t2 = System.currentTimeMillis();

        /*
         № 2
         */
        N_crit = 2;
        if (MPI.COMM_WORLD.getRank() == 0) {
            int[][] a = new int[][] {{6, 6, -1, 3},
            {-6, 0, -1, 1},
            {0, 1, 1, -5},
            {-1, 0, 6, 6}};
            int[][] b = new int[][] {{-1, -5, -1, 0},
            {0, -5, 0, 3},
            {3, 0, 0, 0},
            {0, -4, -3, 2}};

            A = new MatrixS(a, ring);
            B = new MatrixS(b, ring);
        }
        result = multiplyMatrix(A, B, N_crit);
        if (MPI.COMM_WORLD.getRank()
                == 0) {
            System.out.println("C =" + result.toString(ring));

            MatrixD AAA = new MatrixD(A);
            MatrixD BBB = new MatrixD(B);
            BBB = (MatrixD) AAA.multiply(BBB, ring);
            // System.out.println("A = "+AAA+"   \n B  = "+BBB);
            if (new MatrixD(result).compareTo(BBB, ring) == 0) {
                System.out.println("TRUE, Matrix 2 slau");
            }
        }

        /*
         № 3 
         */
        if (MPI.COMM_WORLD.getRank() == 0) {
            System.out.println("Test1     " + MPI.COMM_WORLD.getRank());
            // System.out.println("Result multiply=" + result.toString(ring));
            A = new MatrixS(32, 32, 100, randomType, ran, one, ring);
            B = new MatrixS(32, 32, 100, randomType, ran, one, ring);
            // System.out.println(A);
            //System.out.println(B);
        }
        N_crit = 16;
        //   t1 = System.currentTimeMillis();
        result = multiplyMatrix(A, B, N_crit);
        //  t2 = System.currentTimeMillis();
        if (MPI.COMM_WORLD.getRank()
                == 0) {
     //       System.out.println("C =" + result.toString(ring));

            MatrixD AAA = new MatrixD(A);
            MatrixD BBB = new MatrixD(B);
            BBB = (MatrixD) AAA.multiply(BBB, ring);
            // System.out.println("A = "+AAA+"   \n B  = "+BBB);
            if (new MatrixD(result).compareTo(BBB, ring) == 0) {
                System.out.println("TRUE, Matrix 3 slau");
            }

        }
//        if (MPI.COMM_WORLD.getRank() == 0) {
//            System.out.println("Test2" + MPI.COMM_WORLD.getRank() + " time= " + (t2 - t1));
//            // System.out.println("Result multiply=" + result.toString(ring));
//            A = new MatrixS(1024, 1024, 100, randomType, ran, one, ring);
//            B = new MatrixS(1024, 1024, 100, randomType, ran, one, ring);
//            //System.out.println(A);
//            //System.out.println(B);
//        }
//
//
//        N_crit = 1024;
//        t1 = System.currentTimeMillis();
//        result = multiplyMatrix(A, B, N_crit);
//        t2 = System.currentTimeMillis();
//        if (MPI.COMM_WORLD.getRank() == 0) {
//            System.out.println("Test3 time= " + (t2 - t1));
//            // System.out.println("Result multiply=" + result.toString(ring));
//        }
        MPI.Finalize();
    }
}
