/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.utils;

import java.io.IOException;
import java.io.Serializable;
import mpi.MPI;
import mpi.MPIException;
import com.mathpar.parallel.utils.*;

/**
 *
 * @author r1d1
 */

// mpirun -np 4 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.utils.TransportTest

class My implements Serializable{
    int var;
}

public class TransportTest {
     public static void main(String[] args) throws MPIException,IOException,ClassNotFoundException{
        MPI.Init(args);
        int myRank=MPI.COMM_WORLD.getRank();
        Integer tmp=null;
        if (myRank==0){
            tmp=new Integer(1234);           
        }
        tmp=(Integer)MPITransport.bcastObject(tmp, 0);
        System.out.println("rank="+myRank+" "+tmp.intValue());
        MPI.Finalize();

    }
}
