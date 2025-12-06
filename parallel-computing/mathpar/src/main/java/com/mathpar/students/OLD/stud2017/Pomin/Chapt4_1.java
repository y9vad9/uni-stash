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
public class Chapt4_1 {
   //mpirun -np 5 java -cp /home/roman/stemedu/target/classes com/mathpar/students/ukma17m1/Pomin/Chapt4_1 
    public static void sendObject(Object a, int proc, int tag) throws MPIException, IOException {
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(a);
        byte[] tmp = bos.toByteArray();
        MPI.COMM_WORLD.send(tmp, tmp.length, MPI.BYTE, proc, tag);
        
    }
    
    public static Object recvObject(int proc, int tag)
        throws MPIException, IOException,
        ClassNotFoundException {

        Status st = MPI.COMM_WORLD.probe(proc, tag);
     
        int size = st.getCount(MPI.BYTE);
      
        byte[] tmp = new byte[size];

        MPI.COMM_WORLD.recv(tmp, size, MPI.BYTE,
        proc, tag);
        Object res = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
        ObjectInputStream ois = new ObjectInputStream(bis);
        res = ois.readObject();

        return res;
}
    
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        
        MPI.Init(args);
        
        int[] arr = {1,6,8,3,3,7};
        Object obj_arr = (Object) arr;
        
        Chapt4_1.sendObject(obj_arr, 2, 1);
        Object obj_reciv = Chapt4_1.recvObject(2, 1);
        
        int[] arr_rec = (int[]) obj_reciv;
        
        for(int i=0; i<arr_rec.length; i++) {
            System.out.println("arr[i] = "+arr_rec[i]);    
        }
        
        MPI.Finalize();
        
    }
    
    /**
     * Here we get array sent by sendObject()
     * and received by recvObject()
     * arr[i] = 1
       arr[i] = 6
       arr[i] = 8
       arr[i] = 3
       arr[i] = 3
       arr[i] = 7
     */
    
}
