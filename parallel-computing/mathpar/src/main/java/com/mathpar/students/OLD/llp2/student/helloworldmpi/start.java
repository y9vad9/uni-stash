package com.mathpar.students.OLD.llp2.student.helloworldmpi;
import com.mathpar.number.Ring;
import com.mathpar.number.NumberZp32;
import java.util.Random;

import com.mathpar.matrix.MatrixS;
import mpi.*;
/**
 *
 * @author andy
 *  mpirun C java -cp /home/student/parca389edu/build/classes/ -Djava.library.path=$LD_LIBRARY_PATH helloworld//!!!! MPI.start
 */
public class start {
 static int tag = 0;
 static int mod = 13;
    public static void main(String[] args) throws MPIException {
        Ring ring=Ring.ringR64xyzt;
         MPI.Init(new String[0]);//старт MPI
        int rank =  MPI.COMM_WORLD.getRank();//получение номера узла
        if (rank == 0) {
            // программа выполняется на нулевом процессоре
            ring.setMOD32(mod);
            long tt = 0;
            int ord = 4;
            int den = 10000;
            Random rnd = new Random(); //представитель класса случайного генератора
            MatrixS A = new MatrixS(ord, ord, den, new int[]{5, 5}, rnd, NumberZp32.ONE,ring); //ord = размер матрицы, den = плотность
            MatrixS B = new MatrixS(ord, ord, den, new int[]{5, 5}, rnd, NumberZp32.ONE,ring);
            System.out.println("res one proc="+A.multiply(B,ring).toString());
            MatrixS[] DD = new MatrixS[4];
            MatrixS CC = null;
            MatrixS[] AA = A.split();        //разбиваем матрицу A на 4 части
            MatrixS[] BB = B.split();        //разбиваем матрицу B на 4 части

            Transport.sendArrayOfObjects(new Object[]{AA[0], BB[1], AA[1], BB[3]}, 1, tag);
            //посылка от нулевого процессора массива Object процессору 1 с идентификатором tag
            Transport.sendArrayOfObjects(new Object[]{AA[2], BB[0], AA[3], BB[2]}, 2, tag);
            //посылка от нулевого процессора массива Object процессору 2 с идентификатором tag
            Transport.sendArrayOfObjects(new Object[]{AA[2], BB[1], AA[3], BB[3]}, 3, tag);
            //посылка от нулевого процессора массива Object процессору 3 с идентификатором tag

            DD[0] = (AA[0].multiply(BB[0],ring)).add(AA[1].multiply(BB[2],ring),ring); // оставляем один блок нулевому процессору для обработки
            DD[1] = (MatrixS) Transport.recvObject(1, 1);  // принимаем результат от первого процессора
            DD[2] = (MatrixS) Transport.recvObject(2, 2);  // принимаем результат от второго процессора
            DD[3] = (MatrixS) Transport.recvObject(3, 3);  // принимаем результат от третьего процессора
            CC = MatrixS.join(DD);                         //процедура сборки матрицы из блоков DD[i] (i=0,...,3)
            System.out.println("RES= "+ CC.toString());

        } else {
             // программа выполняется на процессоре с рангом rank
            System.out.println("I'm processor " + rank);
            ring.setMOD32(mod);
            Object[] n =Transport.recvArrayOfObjects(0, tag);// получаем массив Object с блоками матриц от нулевого процессора
            MatrixS a=(MatrixS)n[0];
            MatrixS b=(MatrixS)n[1];
            MatrixS c=(MatrixS)n[2];
            MatrixS d=(MatrixS)n[3];
            MatrixS res =Transport.mmultiply(a, b, c, d, ring);    //перемножаем и складываем блоки матриц
            Transport.sendObject(res, 0, rank);              //посылаем результат вычислений от процессора rank нулевому процессору
            System.out.println("send result");               //сообщение на консоль о том что результат бедет послан
            }
    }
}
