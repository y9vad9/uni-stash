/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2017.Pomin;

import mpi.*;
import java.io.*;

/**
 *
 * @author roman
 */
public class Chapt4_5 {

    public static void bcastObjectArray(Object[] o,
            int count, int root)
            throws IOException, MPIException, ClassNotFoundException {
        byte[] tmp = null;
        int[] size = new int[1];
        int rank = MPI.COMM_WORLD.getRank();
        if (rank == root) {
            ByteArrayOutputStream bos
                    = new ByteArrayOutputStream();
            ObjectOutputStream oos
                    = new ObjectOutputStream(bos);
            for (int i = 0; i < count; i++) {
                oos.writeObject(o[i]);
            }
            tmp = bos.toByteArray();
            size[0] = tmp.length;
        }
        MPI.COMM_WORLD.bcast(size, 1, MPI.INT, root);
        if (rank != root) {
            tmp = new byte[size[0]];
        }
        MPI.COMM_WORLD.bcast(tmp, tmp.length, MPI.BYTE, root);

        MPI.COMM_WORLD.bcast(tmp, tmp.length, MPI.BYTE, root);
        if (rank != root) {
            ByteArrayInputStream bis
                    = new ByteArrayInputStream(tmp);
            ObjectInputStream ois
                    = new ObjectInputStream(bis);
            for (int i = 0; i < count; i++) {
                o[i] = ois.readObject();
            }
        }
    }
    
    //mpirun -np 5 java -cp /home/roman/stemedu/target/clsses com/mathpar/students/ukma17m1/Pomin/Chapt4_5
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        MPI.Init(args);
        
        Integer num = 674;
        Boolean bool_val = true;
        
        Object[] obj_arr = {num, "MPI", bool_val, "Hello", "jigreogj", false};
        
        Chapt4_5.bcastObjectArray(obj_arr, 3, 0);
        
        Object[] obj_arr_recv = null;
        
        //Receiving part of array by every processor
        for(int i=0; i<2; i++) {
            obj_arr_recv = Chapt4_2.recvObjects(2, i, 11);
            
            for(int j=0; j<obj_arr_recv.length; j++) {
                System.out.println("arr[i] = "+obj_arr_recv[j]);
            }
        }
        
        MPI.Finalize();
    }
    
    /**
     * Program has no output
     * Maybe, root number of the processor was invalid
     */
    

}
