package com.mathpar.parallel.utils.parallel_debugger;

/**
 * mpirun -np 4 java -cp $HOME/openmpi/lib///!!!! MPI.jar:/home/r1d1/NetBeansProjects/mathpar/target/classes clusterTools.algorithms.multMatrix1x8.Main 64
 mpirun -np 4 java -cp $HOME/openmpi/lib///!!!! MPI.jar:/home/r1d1/NetBeansProjects/mathpar/target/classes llp2.message.AlltoallDebug
 * @author student
 */
 
//mpirun C java -cp /home/gennadi/mathpar/target/classes/  -Djava.librth=$LD_LIBRARY_PATH llp2.student.message.AlltoallDebugary.path=$LD_LIBRARY_PATH llp2.student.message.AlltoallDebug

import mpi.*;

/**
 * 
 * @author student
 */
public class AlltoallDebug {
	public static void main(String[] args) throws MPIException {
		MPI.Init(args);
		// krivencev.init( MPI.COMM_WORLD);
		int myrank =  MPI.COMM_WORLD.getRank(); // определеине номера процессора
		int np =  MPI.COMM_WORLD.getSize();// определение числа процессоров в группе
		// int n = Integer.parseInt(args[0]);//входной размер -- размер массива

		Object[] a = new Object[3 * np];
		for (int i = 0; i < 3 * np; i++) {
			a[i] = new Integer(3 * myrank);// заполнение массива а
		}
		for (int i = 0; i < 3 * np; i += 2) {
			a[i] = new Integer(i);// заполнение массива а
		}
		Object[] q = new Object[3 * np];// создается массив q типа Object
		int[] qq = new int[3 * np];// создается массив qq типа int

		ParallelDebug pd=new ParallelDebug(MPI.COMM_WORLD);   // ЗАПУСКАЕМ ОТЛАДЧИК с именем pd
		try {
			int n = 3;
			//!!!! MPI.COMM_WORLD.Alltoall(a, 0, n, //!!!! MPI.OBJECT, q, 0, n, //!!!! MPI.OBJECT);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		for (int i = 0; i < q.length; i++) {
			System.out.println("  myrank = " + myrank + " send= " + a[i]
					+ "recv = " + q[i]);
		}

		for (int j = 0; j < 3 * np; j++) {
			qq[j] = ((Integer) q[j]).intValue();
		}
                
		 pd.paddEvent("после выполнения команды Alltoall", "near the end");   // Выбрасываем сообщение в протокол
     
         //!!!! MPI.COMM_WORLD.Barrier();
         pd.generateDebugLog();  // Формируем и смотрим html-fail со всеми сообщениями, отсортированными по времени
        
		//!!!! MPI.Finalize();
	}
}
