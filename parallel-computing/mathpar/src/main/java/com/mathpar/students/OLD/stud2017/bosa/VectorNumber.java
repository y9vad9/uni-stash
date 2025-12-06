/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2017.bosa;

import java.io.IOException;
import java.util.Random;
import com.mathpar.matrix.MatrixS;
import mpi.*;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.parallel.utils.MPITransport;

/**
 *
 * @author sasha
 */

//openmpi/bin/mpirun --hostfile hostfile -np 4 java -cp /home/sasha/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/bosa/MatrixVector 4 4
public class VectorNumber {
    
    public static void MultiplyVectorToNumber(String[] args) throws MPIException, IOException, ClassNotFoundException{
        
        Ring ring = new Ring("Z[x]");
       
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        
        int ord = Integer.parseInt(args[0]);

        Element s = NumberR64.valueOf(
        Integer.parseInt(args[1]));

        int k = ord / size;
        int n = ord - k * (size - 1);
        
        if (rank == 0) {
            
            int den = 10000;
            Random rnd = new Random();
            VectorS B = new VectorS(ord, den,
            new int[] {5, 5}, rnd, ring);
            
            System.out.println("Vector B = " + B);
           
            Element[] res0 = new Element[n];
            
            for (int i = 0; i < n; i++)
                res0[i] = B.V[i].multiply(s, ring);
            
            for (int j = 1; j < size; j++) {
                
                Element[] v = new Element[k];
                System.arraycopy(B.V, n + (j - 1) * k, v, 0, k);
                MPITransport.sendObject(v, j, 100 + j);   
            }

            Element[] result = new Element[ord];
            System.arraycopy(res0, 0, result, 0, n);

            for (int t = 1; t < size; t++) {

                Element[] resRank = (Element[])
                MPITransport.recvObject(t, 100 + t);
                System.arraycopy(resRank, 0, result, n +(t - 1) * k, resRank.length);
    }
            
            System.out.println("B * S = " +
            new VectorS(result).toString(ring));
            
        } else {
        
            System.out.println("I'm processor " + rank);

            Element[] B = (Element[])
            MPITransport.recvObject(0, 100 + rank);
            System.out.println("rank = " + rank +
            " B = " + Array.toString(B));
 
            Element[] result = new Element[k];
            
            for (int j = 0; j < B.length; j++)
                result[j] = B[j].multiply(s, ring);

            MPITransport.sendObject(result, 0, 100 + rank);
            System.out.println("send result");
        }
    }
    
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException{
         MPI.Init(args);
         MultiplyVectorToNumber(args);
         MPI.Finalize();
    
    }
    
}

/*
********************result**************************

I'm processor 1
I'm processor 2
I'm processor 3
Matrix A = 
[[5,  26, 4,  3 ]
 [3,  6,  12, 0 ]
 [5,  12, 17, 29]
 [31, 27, 26, 25]]
Vector B = [27, 29, 9, 13]
rank = 0 row = [5, 26, 4, 3]
rank = 2row = [5, 12, 17, 29]
rank = 2 B = [27, 29, 9, 13]
rank = 1row = [3, 6, 12]
rank = 1 B = [27, 29, 9, 13]
rank = 3row = [31, 27, 26, 25]
rank = 3 B = [27, 29, 9, 13]
A * B = [[[135, 145, 45, 65],
[702, 754, 234, 338],
[108, 116, 36, 52],
[81, 87, 27, 39]], [[81, 87, 27, 39],
[162, 174, 54, 78],
[324, 348, 108, 156]], [[135, 145, 45, 65],
[324, 348, 108, 156],
[459, 493, 153, 221],
[783, 841, 261, 377]], [[837, 899, 279, 403],
[729, 783, 243, 351],
[702, 754, 234, 338],
[675, 725, 225, 325]]]


*/