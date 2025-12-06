package com.mathpar.students.OLD.llp2.student.lyanok;

/**
 *
 * @author lyanochka
 */
import java.util.Arrays;
import java.util.Random;
import mpi.*;

public class Massiv {
    //    /home/lyanochka/NetBeansProjects/mpi
    // mpirun C java -cp /home/lyanochka/NetBeansProjects/mpi/build/classes:$CLASSPATH mpimy.Massiv

    public static void main(String[] args) throws  MPIException {
        //!!!! MPI.//!!!! MPI.Init(args);
        int myrank =   MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();
        System.out.println("np=" + np + "         myrank=" + myrank);
        int[][] A = new int[4][8];
        int[][] B = new int[4][2];
        int[][] C = new int[4][8];
        if (myrank == 0) {
            for (int j = 0; j < np; j++) {
                for (int i = 0; i < 8; i++) {
                    A[j][i] = new Random().nextInt(100);
                }
                System.out.println("A = " + Arrays.toString(A[j]));
            }
        }
        if (myrank == 0) {
            for (int i = 0; i < np; i++) {
                for (int j = 0; j < 4; j++) {
                    //!!!! MPI.COMM_WORLD.Send(A[j], i * 2, 2, //!!!! MPI.//!!!! MPI.INT, i, j);
                }
                System.out.println("Массив  " + Arrays.toString(A[0]) + "  Proc num   " + myrank + "  Массив отправлен  ");
            }
            System.out.println("7777777777777");
        }

        for (int i = 0; i < np; i++) {
            for (int j = 0; j < 4; j++) {
//                System.out.println("i = " + i + "   j = " + j + "   myrank = " + myrank);
                //!!!! MPI.//!!!! MPI.COMM_WORLD.Recv(B[j], 2 * i, 2, //!!!! MPI.//!!!! MPI.INT, i, j);
//                    System.out.println("-----  i = "+i+"   j = "+j+"   myrank = "+myrank);
////                if ((i == 3) && (j == 3)) {
////                    System.out.println("jjjjjjj" + "   j = " + "   myrank = " + myrank);
////                }
//           }
//            System.out.println("privetr" + "   j = " + j+"   myrank = " + myrank);
                System.out.println("Массив " + Arrays.toString(B[j]) + "  Proc num  " + myrank + " Массив принят  j=" + j);
            }
        }
        if (myrank == 0) {
            for (int j = 0; j < 4; j++) {
                System.out.println("B = " + Arrays.toString(B[j]));
            }
        }
        //!!!! MPI.//!!!! MPI.Finalize();
    }
}
