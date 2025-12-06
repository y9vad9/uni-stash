/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2017.bosa;

import java.io.IOException;
import java.util.Random;
import com.mathpar.matrix.MatrixS;
import mpi.MPI;
import mpi.MPIException;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import com.mathpar.parallel.utils.MPITransport;


/**
 *
 * @author sasha
 */

//openmpi/bin/mpirun --hostfile hostfile -np 10 java -cp /home/sasha/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/bosa/Matrix8S
public class Matrix8S {
    
    
    public static void multiplyMatrixes(Ring ring) throws MPIException, IOException, ClassNotFoundException 
    {
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == 0) {

        int ord = 4;
        int den = 10000;

        Random rnd = new Random();

        MatrixS A = new MatrixS(ord, ord, den,
        new int[] {5, 3}, rnd, NumberZp32.ONE, ring);
        System.out.println("A = " + A);
        MatrixS B = new MatrixS(ord, ord, den,
        new int[] {5, 3}, rnd, NumberZp32.ONE, ring);
        System.out.println("B = " + B);
        MatrixS D = null;

        MatrixS[] AA = A.split();

        MatrixS[] BB = B.split();

        int tag = 0;

        MPITransport.sendObjectArray
        (new Object[] {AA[1], BB[2]},0,2, 1, tag);
        MPITransport.sendObjectArray
        (new Object[] {AA[0], BB[1]},0,2, 2, tag);
        MPITransport.sendObjectArray
        (new Object[] {AA[1], BB[3]},0,2, 3, tag);
        MPITransport.sendObjectArray
        (new Object[] {AA[2], BB[0]},0,2, 4, tag);
        MPITransport.sendObjectArray
        (new Object[] {AA[3], BB[2]},0,2, 5, tag);
        MPITransport.sendObjectArray(new Object[] {AA[2], BB[1]},0,2, 6, tag);
        MPITransport.sendObjectArray
        (new Object[] {AA[3], BB[3]},0,2, 7, tag);
        MatrixS[] DD = new MatrixS[4];

        DD[0] = (AA[0].multiply(BB[0], ring)).
        add((MatrixS) MPITransport.recvObject(1, 3),
        ring);
        DD[1] = (MatrixS) MPITransport.recvObject(2, 3);
        DD[2] = (MatrixS) MPITransport.recvObject(4, 3);
        DD[3] = (MatrixS) MPITransport.recvObject(6, 3);
        D = MatrixS.join(DD);
        System.out.println("RES = " + D.toString());
        } else {

        System.out.println("I'm processor " + rank);
        Object[] b = new Object[2];
        MPITransport.recvObjectArray(b,0,2,0, 0);
        MatrixS[] a = new MatrixS[b.length];
        for (int i = 0; i < b.length; i++)
        a[i] = (MatrixS) b[i];
        MatrixS res = a[0].multiply(a[1], ring);
        if (rank % 2 == 0) {
        MatrixS p = res.add((MatrixS) MPITransport.
        recvObject(rank + 1, 3), ring);
        MPITransport.sendObject(p, 0, 3);
        } else {
        MPITransport.sendObject(res, rank - 1, 3);
                }
        }
    }
    
public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        Ring ring = new Ring("R64[x]");

        MPI.Init(new String[0]);
        multiplyMatrixes(ring);

        MPI.Finalize();
    }
}

/*
*********************result**********************

I'm processor 5
I'm processor 3
I'm processor 7
I'm processor 2
I'm processor 1
I'm processor 6
I'm processor 4
A = 
[[0.9,  0.66, 0.18, 0.16]
 [0.89, 0.52, 0.15, 0.28]
 [0.38, 0.28, 0.02, 0.87]
 [0.28, 0.7,  0.47, 0.71]]
B = 
[[0.97, 0.12, 0.57, 0.55]
 [0.63, 0.75, 0.06, 0.64]
 [0.97, 0.79, 0.68, 0.9 ]
 [0.19, 0.26, 0.61, 0.49]]
RES = 
[[1.49, 0.79, 0.77, 1.15]
 [1.39, 0.69, 0.81, 1.09]
 [0.72, 0.49, 0.78, 0.83]
 [1.3,  1.12, 0.96, 1.38]]

*/