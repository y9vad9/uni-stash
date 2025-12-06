/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.students;

import com.mathpar.matrix.MatrixS;
import mpi.*;
import com.mathpar.number.Ring;

/**
 *
 * @author student
 */
public class Cheremisina2 {

    static int tag = 0;
    static int mod = 13;

    public Cheremisina2(){
    }

    public static MatrixS mmultiply(MatrixS a, MatrixS b, MatrixS c, MatrixS d) {
        Ring ring = new Ring("Z[x,y]");
        return (a.multiply(b, ring)).add(c.multiply(d, ring), ring);//умножим a на b, с на d и сложим результаты
    }

    public MatrixS mult_par(MPI mpi,MatrixS A, MatrixS B,Ring ring) throws MPIException {
         MPI.Init(new String[0]);      //старт MPI
//получение номера узла

        int rank =  MPI.COMM_WORLD.getRank();
        int p =  MPI.COMM_WORLD.getSize();
        if (rank == 0) {
            //программа выполняется на нулевом процессоре
            //NumberZp32.setMod(mod);
//            long tt = 0;
//            int ord = 4;
//            int den = 10000;

//представитель класса случайного генератора
//            Random rnd = new Random();
      //      Ring ring = new Ring("Q[x]");

            //ord = размер матрицы, den = плотность
            //MatrixS A = new MatrixS(ord, ord, den, new int[]{5, 5}, rnd, ring.numberONE(), ring);
            //MatrixS B = new MatrixS(ord, ord, den, new int[]{5, 5}, rnd, ring.numberONE(), ring);
            //MatrixS[] DD = new MatrixS[4];
            //MatrixS CC = null;
            MatrixS[] AA = A.split();  //разбиваем матрицу A на 4 части
            MatrixS[] BB = B.split();  // разбиваем матрицу B на 4 части



// посылка от нулевого процессора массива Object процессору 1 с идентификатором

            Object[] send1 = new Object[]{AA[0], BB[1], AA[1], BB[3]};
//            System.out.println("AA[0]"+send1[0].toString());
//            System.out.println("BB[]"+BB[1].toString(ring));
//            System.out.println("AA[1]"+AA[1].toString(ring));
//            System.out.println("BB[3]"+BB[3].toString(ring));

            //!!!! MPI.COMM_WORLD.Send(send1, 0, 4, //!!!! MPI.OBJECT, 1, 000);
            //System.out.println("send1="+AA[0].toString(ring)+" "+BB[1].toString(ring)+" "+AA[1].toString(ring)+" "+ BB[3].toString(ring));
             //Transport.sendArrayOfObjects(new Object[]{AA[0], BB[1], AA[1], BB[3]}, 1, tag);
            Object[] send2 = new Object[]{AA[2], BB[0], AA[3], BB[2]};
            //!!!! MPI.COMM_WORLD.Send(send2, 0, 4, //!!!! MPI.OBJECT, 2, 000);
           // System.out.println("send1="+AA[2].toString(ring)+" "+BB[0].toString(ring)+" "+AA[3].toString(ring)+" "+ BB[2].toString(ring));
// посылка от нулевого процессора массива Object процессору 2 с идентификатором tag

            //Transport.sendArrayOfObjects(new Object[]{AA[2], BB[0], AA[3], BB[2]}, 2, tag);
            //System.out.println("q2");

// посылка от нулевого процессора массива Object процессору 3 с идентификатором tag
            Object[] send3 = new Object[]{AA[2], BB[1], AA[3], BB[3]};
            //!!!! MPI.COMM_WORLD.Send(send1, 0, 4, //!!!! MPI.OBJECT, 3, 000);
            //System.out.println("send1="+AA[2].toString(ring)+" "+BB[1].toString(ring)+" "+AA[3].toString(ring)+" "+ BB[3].toString(ring));
            //Transport.sendArrayOfObjects(new Object[]{AA[2], BB[1], AA[3], BB[3]}, 3, tag);
            //System.out.println("q3");
            //оставляем один блок нулевому процессору для обработки
            MatrixS A1 = (AA[0].multiply(BB[0], ring)).add(AA[1].multiply(BB[2], ring), ring);
            //.out.println("DD[0]="+A1.toString(ring));

            Object[] ppp = new Object[1];
            MatrixS[] result = new MatrixS[4];
            result[0]=A1;
            for(int i=1;i<p;i++){
                //!!!! MPI.COMM_WORLD.Recv(ppp, 0, 1, //!!!! MPI.OBJECT, i, 111);
                result[i]=(MatrixS) ppp[0];
                //System.out.println("res=" + result[i].toString(ring)+ " i="+i);
            }


//
//// принимаем результат от первого процессора
//            DD[1] = (MatrixS) Transport.recvObject(1, 1);
//            System.out.println("q5");
//
//// принимаем результат от второго процессора
//            DD[2] = (MatrixS) Transport.recvObject(2, 2);
//            System.out.println("q6");
//
//// принимаем результат от третьего процессора
//            DD[3] = (MatrixS) Transport.recvObject(3, 3);
//            System.out.println("q7");

//процедура сборки матрицы из блоков  DD[i] (i=0,...,3)
            MatrixS CC = MatrixS.join(result);
            //System.out.println("CC="+CC.toString(ring));

            //System.out.println("RES= " + CC.toString());
            return CC;
        } else {

            Object[] recv = new Object[4];
            //!!!! MPI.COMM_WORLD.Recv(recv, 0, 4, //!!!! MPI.OBJECT, 0, 000);
            MatrixS a = (MatrixS) recv[0];
            MatrixS b = (MatrixS) recv[1];
            MatrixS c = (MatrixS) recv[2];
            MatrixS d = (MatrixS) recv[3];
//            System.out.println("a="+a.toString(ring) + "  my="+rank);
//            System.out.println("b="+b.toString(ring) + "  my="+rank);
//            System.out.println("c="+c.toString(ring) + "  my="+rank);
//            System.out.println("d="+d.toString(ring) + "  my="+rank);
            //программа выполняется на процессоре с рангом rank
            //System.out.println("I'm processor " + rank);
            //NumberZp32.setMod(mod);
            // получаем массив Object с блоками матриц от нулевого процессора
            //Object[] n = Transport.recvArrayOfObjects(0, tag);
//            MatrixS a = (MatrixS) n[0];
//            MatrixS b = (MatrixS) n[1];
//            MatrixS c = (MatrixS) n[2];
//            MatrixS d = (MatrixS) n[3];

            //перемножаем и складываем блоки матриц
            MatrixS res = mmultiply(a, b, c, d);
            //System.out.println("res="+res.toString(ring)+" my="+rank);

            Object[] send5 = new Object[]{res};
            //!!!! MPI.COMM_WORLD.Send(send5, 0, 1, //!!!! MPI.OBJECT, 0, 111);
            //System.out.println("send result="+send5[0].toString()+" my="+rank);
            //посылаем результат вычислений от процессора rank нулевому процессору
            //Transport.sendObject(res, 0, rank);
            //System.out.println("w2");

            // сообщение на консоль о том, что результат будут послан



        }
        return A;
    }
}
/*

public static void main(String args[]) throws MPIException {
//!!!! MPI.Init(args);
int myrank = //!!!! MPI.COMM_WORLD.getRank();
int np = //!!!! MPI.COMM_WORLD.Size();
Ring ring = new Ring("Q[x]");
Random rnd = new Random();
Fraction one =  new Fraction(one, ring);
int[] randomType = new int[]{3};
MatrixS a = new MatrixS(2, 2, 100, randomType, rnd, ring.numberONE(), ring);
MatrixS b = new MatrixS(3, 4, 90, randomType, rnd, one, ring);
System.out.println("A =" + a +  a.M[0][0].numbElementType());
MatrixS c = a.GenInvers(ring);
System.out.println("C=" + c.multiply(a, ring).cancel(ring));
//!!!! MPI.Finalize();

}*/
