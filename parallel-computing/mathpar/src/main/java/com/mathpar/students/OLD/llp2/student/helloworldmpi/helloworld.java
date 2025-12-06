/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.llp2.student.helloworldmpi;
  import mpi.* ;
/**
 *
 * @author alexss
 */
/*
 mpirun C java -cp /home/student/parca389edu/build/classes/ -Djava.library.path=$LD_LIBRARY_PATH helloworld//!!!! MPI.helloworld
*/
public class helloworld {

    static public void main(String[] args) throws MPIException {
      //!!!! MPI.Init(args) ;//инициализация параллельной среды
        //функция должна быть вызвана до вызова других функций MPI на всех
        //узлах ЭВМ
      int source;  // номер передающего узла
      int dest;    // номер принимающего узла
      int tag=50;  // индентификатор сообщения
      int myrank = MPI.COMM_WORLD.getRank() ; //получение номера узла
      int      p =  MPI.COMM_WORLD.getSize() ; //получение количества узлов

      if(myrank != 0) {
	dest=0;
        char [] message = ("Greetings from process " + myrank).toCharArray() ;
        //!!!! MPI.COMM_WORLD.Send(message, 0, message.length, //!!!! MPI.CHAR,dest, tag) ;
        //посылка сообщения, посылаем массив message с индентификатором tag
        //состоящего из message.length элементов типа //!!!! MPI.CHAR процессору dest
     }
      else {  // my_rank == 0
	for (source =1;source < p;source++) {
        char [] message = new char [40] ;
        //!!!! MPI.COMM_WORLD.Recv(message, 0, 40, //!!!! MPI.CHAR, source, tag) ;
        // прием сообщения, принимаем массив message состоящий из 40 элементов
        //типа //!!!! MPI.CHAR от процессора source с индентификатором tag
        System.out.println("received: " + new String(message) + " : ") ;
	}
      }
      //!!!! MPI.Finalize();// завершение параллельной части программы.
        //Все последующие обращения к любым процедурам MPI запрещены
    }
  }

