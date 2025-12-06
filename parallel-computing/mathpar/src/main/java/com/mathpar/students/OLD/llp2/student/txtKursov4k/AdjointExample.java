package com.mathpar.students.OLD.llp2.student.txtKursov4k;

/**
 *
 * @author andy
 */
public class AdjointExample {
	// данный пример показывает запуск параллельного вычисления присоединенной
	// матрицы для случайной матрицы

	// ВНИМАНИЕ параллельная программа запускается не из netbeans а из консоли

/*	public AdjMatrixS Adj_Parall(LLP llp, MPI mpi, MatrixS aLs, Ring ring) {
		// Ring r = new Ring("Z[]");
		try {
			// инициализация MPI и LLP
			// //!!!! MPI.Init(new String[0]);
			LLP.startLLP();
			// Ring r = new Ring("Z[]");

			if (LLP.myrank == 0) {
				// здесь вычисляется все, что должно делаться однопроцессорно,
				// включая запуск параллельной задачи
				// т.е. все тело программы должно быть здесь

				// данные для случайной матрицы
				// int ord =8; //размерность
				// int den = 10000; // плотность 10000 это 100%

				// Кольцо
				Element d1 = ring.numberONE; // единичка кольца

				// MatrixS aLs = new MatrixS(ord, ord, den,new int[]{2, 2}, rnd,
				// r.numberONE(), r);
				aLs = aLs.expandToPow2with0(); // обязательно, чтобы матрица
												// была квадратной размера
												// степени 2

				// теперь необходимые параметры для работы LLP
				LLP.setDelay(100); // пауза для диспетчера
				int boundLev = aLs.size / //!!!! MPI.COMM_WORLD.Size(); // граничный
																	// уровень
				S_MATRIX_INV.BOUND_LEV = boundLev;

				System.out.println("start Matrix 1=" + aLs.toString(ring));
				System.out.println("Calculating 1...");
				AdjMatrixS res1 = LLPMatrix.adjDet_S_LLP(aLs, d1, ring); // запуск
																			// параллельного
																			// метода
				ring = new Ring("R64[x,y]");
				System.out.println("res="
						+ res1.A.toNewRing(Ring.R64, ring).toString(ring));
				return res1;

				// если требуется сделать второй запуск на другую матрицу, то
				// просто еще раз на нулевом процессоре запускаем метод
				// MatrixS bLs = new MatrixS(ord, ord, den,new int[]{2, 2}, rnd,
				// r.numberONE(), r);
				// bLs=bLs.expandToPow2with0(); // обязательно, чтобы матрица
				// была квадратной размера степени 2
				// System.out.println("start Matrix 2="+bLs.toString(r));
				// System.out.println("Calculating 2...");
				// AdjMatrixS res2 = LLPMatrix.adjDet_S_LLP(bLs, d1, r);
				// //запуск параллельного метода
				// System.out.println("res="+res2.A.toString(r));

			} else {
				// остальные процессоры ждут поступления заданий, для этого
				// запускается на них след метод
				LLP.mainLoopSlave();

			}

			// финализация LLP и MPI
			LLP.exit();
			// //!!!! MPI.Finalize();
			Element i = NumberZ.ONE;
			return new AdjMatrixS(aLs, i, ring);

		} catch (Exception e) {
			System.out.println("error " + e.toString());
			Element i = NumberZ.ONE;
			return new AdjMatrixS(aLs, i, ring);
		}
	}
}*/
}
