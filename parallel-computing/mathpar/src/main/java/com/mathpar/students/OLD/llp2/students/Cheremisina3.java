package com.mathpar.students.OLD.llp2.students;

import com.mathpar.matrix.MatrixS;
import mpi.*;
import com.mathpar.number.Ring;
import com.mathpar.matrix.*;

import com.mathpar.number.Element;

/**
 *
 * @author student
 */
public class Cheremisina3 {

	static int tag = 0;
	static int mod = 13;

	/**
	 * Вычисление обобщенной обратной матрицы методом Эрмита
	 *
	 * @return обобщенная обратная матрица А+
	 */
	public MatrixS GenInversPar(Ring ring, MatrixS aaa) throws MPIException {
////////		AdjointExample ness = new AdjointExample();
		Cheremisina2 f = new Cheremisina2();
		//LLP llp = new LLP();
		MPI mpi = new MPI();

		MatrixS Atr = aaa.transpose();
		// System.out.println("before");
		MatrixS AAtr = f.mult_par(mpi, aaa, Atr, ring);
		// System.out.println("after");
		// MatrixS AAtr = aaa.multiply(Atr, ring);
		MatrixS M = f.mult_par(mpi, AAtr, AAtr, ring);
                AdjMatrixS E=null;
		// MatrixS M = AAtr.multiply(AAtr, ring);
//		AdjMatrixS E = ness.Adj_Parall(llp, mpi, M, ring);
		// MatrixS E = M.adjoint(ring).cancel(ring);
		MatrixS P = f.mult_par(mpi, E.A, M, ring);
		// MatrixS P = E.A.multiply(M, ring);
		MatrixS Ptr = P.transpose();
                AdjMatrixS F=null;
//		 = ness.Adj_Parall(llp, mpi, Ptr, ring);
		// MatrixS F = Ptr.adjoint(ring).cancel(ring);
		MatrixS Ftr = F.A.transpose();
		MatrixS R = f.mult_par(mpi, F.A, Ptr, ring);
		// MatrixS R = F.A.multiply(Ptr, ring);
		Element coef = R.M[0][0].multiply(R.M[0][0], ring);

		MatrixS MR = f
				.mult_par(mpi, Ftr, (f.mult_par(mpi, E.A, R, ring)), ring);
		MatrixS AtMR = f.mult_par(mpi, MR, Atr, ring);
		// MatrixS AtMR = Atr.multiply(MR, ring);
		MatrixS Moor = f.mult_par(mpi, AtMR, AAtr, ring);
		// MatrixS Moor = AtMR.multiply(AAtr, ring);
		MatrixS MoorPen = Moor.divideByNumbertoFraction(coef, ring);
		return MoorPen;
	}

	public static void main(String[] args) throws MPIException {
		//!!!! MPI.Init(new String[0]); // старт MPI

		// получение номера узла
		int rank =  MPI.COMM_WORLD.getRank();
		// проверка mmultiply
		// Random rnd = new Random();
		// Ring ring = new Ring("Q[x]");
		// MatrixS A = new MatrixS(8, 8, 1000, new int[]{5, 5}, rnd,
		// ring.numberONE(), ring);
		// MatrixS B = new MatrixS(8, 8, 1000, new int[]{5, 5}, rnd,
		// ring.numberONE(), ring);
		// Cheremisina2 f = new Cheremisina2();
		// MatrixS ooo = f.mult_par(mpi, A, B, ring);
		// if(rank==0){System.out.println("ooo="+ooo.toString(ring));}

	//!!!!	Ring ring = new Ring("Q[x]");
	//!!!!	Random rnd = new Random();
	//!!!!	MatrixS A = new MatrixS(8, 8, 1000, new int[] { 5, 5 }, rnd,
	//!!!!			ring.numberONE(), ring);
		//!!!!Cheremisina3 ch = new Cheremisina3();
	//!!!!	MatrixS rrr = ch.GenInversPar(ring, A);
	//!!!!	if (rank == 0) {// System.out.println("rrr="+rrr.toString(ring));
		//!!!!	ring = new Ring("R64[x,y]");
		//!!!!	System.out.println("rrr="
		//!!!!			+ rrr.toNewRing(Ring.R64, ring).toString(ring));

		//!!!!}

		// System.out.println("aaa = " + ch.GenInversPar(ring, A));
		// //программа выполняется на нулевом процессоре
		// //NumberZp32.setMod(mod);
		// } else {
		// System.out.println("   ");

		// }
		//!!!! MPI.Finalize();

	}
}
