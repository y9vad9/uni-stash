
package com.mathpar.students.OLD.llp2.student.helloworldmpi;

import com.mathpar.matrix.*;
import mpi.*;
import com.mathpar.number.Ring;

public class Transport {

    public static MatrixS mmultiply(MatrixS a,MatrixS b,MatrixS c,MatrixS d, Ring ring){
        return (a.multiply(b,ring)).add(c.multiply(d,ring),ring);

    }

//    public static void send4Matrices(Object a,Object b,Object c,Object d, int proc, int tag) throws MPIException{
//       sendObject(a, proc, tag+1);
//       sendObject(b, proc, tag+2);
//       sendObject(c, proc, tag3);
//       sendObject(d, proc, 4);
//       System.out.println("send 4 matrices to proc N"+ proc);
//
//    }
        public static void sendArrayOfObjects(Object[] a , int proc, int tag) throws MPIException{
            for(int i=0;i<4;i++) sendObject(a[i], proc, tag+i);
            System.out.println("New send 4 matrices to proc N"+ proc);
    }
     public static Object[] recvArrayOfObjects(int proc, int tag) throws MPIException{
         Object[] o=new Object[4];
            for(int i=0;i<4;i++) o[i]=recvObject(proc, tag+i);
            System.out.println("New send 4 matrices to proc N"+ proc);
            return o;
    }
    public static void sendObject(Object a, int proc, int tag) throws MPIException{
                    /**MPIException нужен т.к. мы пользуемся в этом методе обращением к МPI
                      *Object a = посылаемый объект, имеет интерфейс serilizeable, т.е. существует механизм превращающий его в массив байтов и обратно
                      *int proc = номер процессора которому посылаем объект
                      *int tag = таг который пошлем с этим объектом
                      */

 //       byte[] temp=MPI.COMM_WORLD.Object_Serialize(new Object[]{a},0,1, MPI.OBJECT); //метод Object_Serialize() превращает массив объектов в массив байтов
        //!!!! MPI.COMM_WORLD.Isend(temp, 0, temp.length, //!!!! MPI.BYTE, proc, tag);  //, //!!!! MPI.BYTE= тип передаваемых данных
                    /**Isend = неблокирующая пересылка
                      *temp = массив который посылаем
                      *0 = номер элемента массива с которого начинам
                      *temp.length = число посылаемых элементов массива
                      * MPI.BYTE = тип элементов в массиве
                      *proc = номер процессора которыму посылаем сообщение
                      *tag = таг сообщение
                      */
    }

    public static Object recvObject(int proc, int tag) throws MPIException{
                    /**proc = номер процессора от которого получаем объект
                      *tag = таг который пришел с объектом
                      */
        Status s= MPI.COMM_WORLD.probe(proc, tag);  //команда считывает статус буфера для приема сообщения от процессора proc с тагом tag
        int n=s.getCount( MPI.BYTE); //динамический метод класса статус который подсчитывает количество элементов в буфере (в данном случае //!!!! MPI.BYTE)
        byte[] arr=new byte[n];// заводится байт массив с нужным числом элементов
        //!!!! MPI.COMM_WORLD.Recv(arr, 0, n,//!!!! MPI.BYTE, proc, tag);//Recv = блокирующий прием массива из буффера ввода в массив arr
        Object[] res=new Object[1]; // заводится массив объектов длинной 1
        //!!!! MPI.COMM_WORLD.Object_Deserialize(res, arr, 0, 1, //!!!! MPI.OBJECT);//процедура обратная для Serialize
        return res[0]; // передаем на выход процедуры полученный объект

    }

}
