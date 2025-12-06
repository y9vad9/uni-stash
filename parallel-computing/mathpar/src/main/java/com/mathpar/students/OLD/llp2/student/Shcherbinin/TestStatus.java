/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import mpi.*;

/**
 *
 * @author ridkeim
 */
/*
 mpirun C java -cp /home/ridkeim/NetBeansProjects/mathpar/target/classes llp2.student.Shcherbinin.TestStatus
 */
public class TestStatus {
    public static void main(String[] args) throws MPIException {
         MPI.Init(args);
        int myrank= MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        System.out.println("myrank="+myrank);
        if(myrank==0){
            for (int i = 1; i < size; i++) {
                //!!!! MPI.COMM_WORLD.Send(new int[1], 0, 1,  MPI.INT, i, i);
            }
            int k =0;
            for (int i = 0; i < 1000000; i++) {
                k++;
            }
             for (int i = 1; i < size; i++) {
                //!!!! MPI.COMM_WORLD.Send(new int[1], 0, 1, MPI.INT, i, 0);
            }
        } else {
            Status st1 =  MPI.COMM_WORLD.probe(0, MPI.ANY_TAG);
            boolean flag = true;
            if(st1.getTag() == 0){
                flag = false;
            }

            System.out.println("st1="+st1);
            int[] a = new int[1];
            while(flag){
                //!!!! MPI.COMM_WORLD.Recv(a, 0, 1, MPI.INT, 0, myrank);
                System.out.println("s");
                st1 = MPI.COMM_WORLD.probe(0,MPI.ANY_TAG);
                if(st1.getTag() == 0){
                    flag = false;
                    System.out.println("MESSAGE Stop");
                }

            }
            //!!!! MPI.COMM_WORLD.Recv(a, 0, 1, //!!!! MPI.INT, 0, 0);
        }
        //!!!! MPI.Finalize();
    }
}
