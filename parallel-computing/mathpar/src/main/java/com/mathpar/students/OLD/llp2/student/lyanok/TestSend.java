package com.mathpar.students.OLD.llp2.student.lyanok;

import java.util.Arrays;
import java.util.Random;
import mpi.*;

/**
 *
 * @author lyanochka
 */
public class TestSend {
public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int n = MPI.COMM_WORLD.getSize();
        int m = 8;
        int a[][] = new int[4][m];
        int b[][] = new int[4][m];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                a[j][i] = new Random().nextInt(100);
            }
        }
        //!!!! MPI.COMM_WORLD.Barrier();
        if (myrank == 0) {
            for (int j = 0; j < 4; j++) {
                for (int i = 1; i < n; i++) {

                    //!!!! MPI.COMM_WORLD.Send(a[j], i * 2, 2, //!!!! MPI.INT, i, 3000);
                }
                System.out.println("Proc num " + myrank + " Массив отправлен");
                for (int k = 0; k < a.length; k++) {
                    System.out.println("Proc num= " + myrank+ "  :  "+ Arrays.toString(a[k]));
                }
            }
        }
        //!!!! MPI.COMM_WORLD.Barrier();
        for (int i = 0; i < 4; i++) {
            //!!!! MPI.COMM_WORLD.Recv(b[i], myrank * 2, 2, //!!!! MPI.INT, 0, 3000);
            System.out.println("Proc num= " + myrank + ": Массив принят");
            for (int k = 0; k < b.length; k++) {
                System.out.println("Proc num= " + myrank+ "  :  "+ Arrays.toString(b[k]));
            }
        }
        //!!!! MPI.Finalize();
//        for(int i=0;i<a.length;i++){
//            for(int j=0;j<a[0].length;j++){
//                a[i][j]=a[i][j]-b[i][j];
//            }
//        }
//
//        for(int i=0;i<a.length;i++){
//            for(int j=0;j<a[0].length;j++){
//                System.out.println("Proverka:   "+ myrank+"   "+ a[i][j]);
            //}
       // }
    }
}
