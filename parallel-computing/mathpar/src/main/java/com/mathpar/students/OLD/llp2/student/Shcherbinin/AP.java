/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import java.io.IOException;
import com.mathpar.matrix.MatrixS;
import mpi.MPI;
import mpi.MPIException;
import com.mathpar.number.Ring;
import com.mathpar.parallel.utils.MPITransport;

/**
 *
 * @author ridkeim
 */
/*
 * mpirun C java -cp /home/ridkeim/NetBeansProjects/mathpar/target/classes
 * llp2.student.Shcherbinin.AP
 */
public class AP {

    public static void main(String[] args) throws MPIException,IOException,ClassNotFoundException {
         MPI.Init(args);
        int minSize = MPI.COMM_WORLD.getSize();
        int l = 32;
        try {
            l = Integer.parseInt(args[0]);
            l= getSize(l);
        } catch (Exception e) {
        }

        boolean root = ( MPI.COMM_WORLD.getRank() == 0) ? true : false;
        Ring ring = new Ring("Z[x,y]");

        int size =  MPI.COMM_WORLD.getSize();
//        System.out.println("size="+size);
        int size3 = getSize(size);
        int size2 = l/size3;
        if(size2<2){
            size2 = 2;
        }
        MatrixS A = null;
        if (root) {
            System.out.println("MatrixSize="+l);
            System.out.println("ProcSize="+size3);
            A = Matrix_multiply.getRandomMatrix(l, l);
        }
        A = sendMatrix(A);
//        System.out.println("senddone on "+//!!!! MPI.COMM_WORLD.getRank());
//        if ((!root) && (A != null)) {
//            System.out.println("A not null");
//        }
//        MatrixS A = new MatrixS(
//                new int[][]{
//                    new int[]{14, 15, 7,  1, 3},
//                    new int[]{1,  5,  8,  1, 4},
//                    new int[]{12, 4,  7,  8, 1},
//                    new int[]{55, 32, 14, 3, 23},
//                }, new Ring("Z[x]"));
        long time = System.currentTimeMillis();
        MatrixS[] lx = new MatrixS[3];
        if (size==1) {
            lx = A.LDU(ring);
        }else{
            lx = A.LDUP(size2, ring);
        }
        time = System.currentTimeMillis() - time;
        if (root) {
            System.out.println("time=" + time);
        }
//        MatrixS L = lx[0];
//        MatrixS U = lx[2];
//        MatrixS D = lx[1];
//        MatrixS ldu = L.multiply(D.multiply(U, ring), ring);

//        ldu = ldu.cancel(ring);
//        MatrixS tt = Matrix_multiply.multiply(L, Matrix_multiply.multiply(D, U, 2, ring), 2, ring);
//        if (root) {
//            System.out.println(ldu);
//            System.out.println(L);
//            System.out.println(D);
//            System.out.println(U);
//                System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{ldu, L, D, U}, "=", ring));
//                System.out.println("T= "+tt);
//        }
//        A = new MatrixS(
//                new int[][]{
//                    new int[]{74, 15, 7,  1, 5},
//                    new int[]{2,  5,  0,  1, 41},
//                    new int[]{0, 2,  1,  3, 1},
//                    new int[]{65, 2, 1,43, 13},
//                }, new Ring("Z[x]"));
//        if (root) {
//            time = System.currentTimeMillis();
//            MatrixS[] lxs = A.LSU(ring);
//            time=System.currentTimeMillis()-time;
//            System.out.println("time without MPI="+time);
//        }
//        time = System.currentTimeMillis();
//        lx = A.LDUP(2,ring);
//        time=System.currentTimeMillis()-time;
//       if(root){System.out.println("time="+time);}
//
//
//            L = lx[0];
//            U = lx[2];
//            D = lx[1];
//            ldu = L.multiply(D.multiply(U, ring), ring);
//
//            ldu = ldu.cancel(ring);
//
////            if(root){
////                 System.out.println(ldu);
////                System.out.println(L);
////                System.out.println(D);
////                System.out.println(U);
//////                System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{ldu, L, D, U}, "=", ring));
////
////            }
//        A = new MatrixS(
//                new int[][]{
//                    new int[]{1, 15, 7,  1, 5},
//                    new int[]{2,  0, 50,  21, 41},
//                    new int[]{0, 2,  0,  3, 1},
//                    new int[]{35, 0, 1,4, 11},
//                }, new Ring("Z[x]"));
//        if (root) {
//            time = System.currentTimeMillis();
//            MatrixS[] lxs = A.LSU(ring);
//            time=System.currentTimeMillis()-time;
//            System.out.println("time without MPI="+time);
//        }
//        time = System.currentTimeMillis();
//        lx = A.LDUP(2,ring);
//        time=System.currentTimeMillis()-time;
//        if(root){System.out.println("time="+time);}
//
//            L = lx[0];
//            U = lx[2];
//            D = lx[1];
//            ldu = L.multiply(D.multiply(U, ring), ring);
//
//            ldu = ldu.cancel(ring);
//
//            if(root){
//                System.out.println(ldu);
//                System.out.println(L);
//                System.out.println(D);
//                System.out.println(U);
////                System.out.println(MatrixS.toStringMatrixArray(new MatrixS[]{ldu, L, D, U}, "=", ring));
//
//            }
        //!!!! MPI.Finalize();
    }

    public static MatrixS sendMatrix(MatrixS A) throws MPIException,ClassNotFoundException,IOException {
        boolean root = ( MPI.COMM_WORLD.getRank() == 0) ? true : false;
        MatrixS[] AS = new MatrixS[]{A};
        MPITransport.bcastObjectArrayOld(AS,0,1,0);
//!!!!        //!!!! MPI.COMM_WORLD.Bcast(AS, 0, 1, //!!!! MPI.OBJECT, 0);
        if (root) {
            return A;
        } else {
            return AS[0];
        }
    }
    private static int getSize(int size) {
        int n = 1;
        while (size > 1) {
            size /= 2;
            n *= 2;
        }
        return n;
    }
    /* Время вычисления LSU разложения для MatrixS над Z.
     	32	64	128	256
1
2
4	4580	6720	11940	81930
8	5360	8860	14450	78060

строки - количестов процессоров
столбцы - размер квадратной матрицы


     */
}
