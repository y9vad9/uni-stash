/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2017.Pomin;

import mpi.*;
import java.io.*;
import java.util.logging.*;
//import javax.mail.Transport;
import java.nio.ByteBuffer;

/**
 *
 * @author roman
 */
public class Chapt4_2 {
    
    public static void sendArrayOfObjects(Object[] a,
        int proc, int tag) throws MPIException,
        IOException {

        for (int i = 0; i < a.length; i++)
            Chapt4_1.sendObject(a[i], proc, tag + i);
    }
    
    //Chapter 4_2
    public static void sendObjects(Object[] a, int proc,
        int tag)
        throws MPIException {
        
        ByteArrayOutputStream bos = null;
        try {
               bos = new ByteArrayOutputStream();
               ObjectOutputStream oos = new ObjectOutputStream(bos);
               
                for (int i = 0; i < a.length; i++)
                oos.writeObject(a[i]);
                bos.toByteArray();
        } catch (Exception ex) {
            //Logger.getLogger(Transport.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger("feswf").log(Level.SEVERE, null, ex);
        }
        byte[] temp = bos.toByteArray();
        ByteBuffer buf = MPI.newByteBuffer(temp.length);
        buf.put(temp);
        MPI.COMM_WORLD.iSend(buf, temp.length,
        MPI.BYTE, proc, tag);
    }
    
    //Chapter 4.3
    public static Object[] recvObjects(int m, int proc, int tag)
        throws MPIException {
        
        Status s = MPI.COMM_WORLD.probe(proc, tag);
        int n = s.getCount(MPI.BYTE);
        byte[] arr = new byte[n];
        MPI.COMM_WORLD.recv(arr, n, MPI.BYTE, proc, tag);
        Object[] res = new Object[m];
        try {ByteArrayInputStream bis =
        new ByteArrayInputStream(arr);
        ObjectInputStream ois =
        new ObjectInputStream(bis);
        for (int i = 0; i < arr.length; i++)
        res[i] = (Object) ois.readObject();
        } catch (Exception ex) {
        //Logger.getLogger(Transport.class.getName()).
        Logger.getLogger("feswf").
        log(Level.SEVERE, null, ex);}
        
        return res;
    }
    
    //mpirun -np 5 java -cp /home/roman/stemedu/target/classes com/mathpar/students/ukma17m1/Pomin/Chapt4_2
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        
        MPI.Init(args);
        
        Integer num = 674;
        Boolean bool_val = true;
        
        Object[] obj_arr = {num, "MPI", bool_val, "Hello", "jigreogj", false};
        
        Chapt4_2.sendObjects(obj_arr, 3, 11);
        
        Object[] obj_arr_recv = Chapt4_2.recvObjects(2, 3, 11);
        
        for(int i=0; i<obj_arr_recv.length; i++) {
            System.out.println("arr[i] = "+obj_arr_recv[i]);    
        }
        
        MPI.Finalize();
    }
    
    /**
     * First time program should take 4 el-s from array
     * arr[i] = 674
       arr[i] = MPI
       arr[i] = true
       arr[i] = Hello

    * 
    * Second time program should take 2 el-s from array
     * arr[i] = 674
       arr[i] = MPI
       * 
       * 
       * But also I see java.lang.ArrayIndexOutOfBoundsException: 2
       * I wonder, why?
     */
    
}
