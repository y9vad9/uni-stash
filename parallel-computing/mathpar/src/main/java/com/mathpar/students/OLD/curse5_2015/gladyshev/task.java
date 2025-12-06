package com.mathpar.students.OLD.curse5_2015.gladyshev;

import com.mathpar.parallel.ddp.engine.*;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;




class task extends AbstractTask {
	MatrixD A;
	MatrixD B;
	MatrixD C;
	Ring ring;

	public void SetStartTask(String []args, Object[] data) {
		A = (MatrixD) data[0];
		B = (MatrixD) data[1];
		ring = (Ring) data[2];
	}

	public boolean IsLittleTask() {
		if(A.M.length < 2) {
			return true;
		}
		return false;
	}

	public void ProcLittleTask() {
		C = (MatrixD) A.multiply(B, ring);
	}

	public void SendTaskToNode(int node) {
		try {
			MPITransport.sendObject(new Object[]{A, B, ring}, node, 0);
		} catch(Exception e) {System.out.println("Error in send task: "+e);}
	}

	public void RecvTaskFromNode(int node) {
		try {
			Object obj = MPITransport.recvObject(node, 0);
			Object[] array = (Object[]) obj;
			A = (MatrixD) array[0];
			B = (MatrixD) array[1];
			ring = (Ring) array[2];
		} catch(Exception e) {System.out.println("Error in recv task: "+e);}
	}

	public void SendResultToNode(int node) {
		try {
			MPITransport.sendObject(C, node, 0);
		} catch(Exception e) {System.out.println("Error in send result: "+e);}
	}

	public void GetResultFromNode(int node) {
		try {
			C = (MatrixD) MPITransport.recvObject(node, 0);
		} catch(Exception e) {System.out.println("Error in recv result: "+e);}
	}

}
