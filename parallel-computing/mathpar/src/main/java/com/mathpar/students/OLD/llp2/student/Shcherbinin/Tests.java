/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import com.mathpar.matrix.MatrixS;
import mpi.*;
import com.mathpar.number.Ring;

/**
 *
 * @author ridkeim
 */
/*
 mpirun C java -cp /home/ridkeim/NetBeansProjects/mathpar/target/classes llp2.student.Shcherbinin.
 */

public class Tests {
    public static void main(String[] args) throws MPIException {
        //!!!! MPI.Init(args);

        MatrixS A = new MatrixS(new int[][]{
                new int[]{0,0},
                new int[]{0,0},

        }, Ring.ringR64xyzt);
        MatrixS B = new MatrixS(new int[][]{
                new int[]{1,2},
                new int[]{1,2},
        }, Ring.ringR64xyzt);
//        System.out.println(Matrix_multiply.getProc(512));
//        System.out.println(Matrix_multiply.getPow(-1));
        MatrixS C = Matrix_multiply.multiply(A, B,1,Ring.ringR64xyzt);
//        C = Matrix_multiply.mult_(A, B,4,1,1);
//        C = Matrix_multiply.mult_(A, B,4,2,1);
//        C = Matrix_multiply.mult_(A, B,4,3,1);
//        System.out.println("C="+C);
//        C = Matrix_multiply.mult_(A, B,2,1,1);
        int rank =  MPI.COMM_WORLD.getRank();
        System.out.println("C on "+rank+" = \n"+C);
       MPI.Finalize();
    }
}
