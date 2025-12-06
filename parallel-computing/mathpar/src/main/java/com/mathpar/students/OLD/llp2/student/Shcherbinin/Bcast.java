/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import java.util.Arrays;
import mpi.*;

//import sun.nio.cs.ArrayDecoder;

/**
 *
 * @author ridkeim
 */
public class Bcast {
    public static void main(String[] args) throws MPIException{
        //mpirun C java -cp /home/ridkeim/NetBeansProjects/mpi/build/classes:$CLASSPATH mpitest.Bcast
         MPI.Init(args);
        int np = MPI.COMM_WORLD.getRank();
        int[] k = new int[]{1,2};
        if (np==0){
            k[0] = (int) (Math.random()*100);
            k[1] = (int) (Math.random()*100);
        }
        System.out.println("k0 = "+Arrays.toString(k)+" proc ="+np);
        //!!!! MPI.COMM_WORLD.barrier();
        //!!!! MPI.COMM_WORLD.bcast(k,1,//!!!! MPI.DOUBLE,0);
//!!!!        //!!!! MPI.COMM_WORLD.Bcast(k, 0, 1, //!!!! MPI.DOUBLE, 0);
        //!!!! MPI.COMM_WORLD.barrier();
        System.out.println("k1 = "+Arrays.toString(k)+" proc ="+np);
        //!!!! MPI.Finalize();
    }
}
