/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2017.sidko;

import com.mathpar.students.OLD.llp2.student.helloworldmpi.Transport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

/**
 *
 * @author alla
 */
public class PracticeObjectModule2 {
    
    /*
    openmpi/bin/mpirun --hostfile hostfile -np 10 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/PracticeObjectModule2
    
    
    Result :
    Send Object to processor 3
    Receive Object from 5
    Fast
    
    */
    public static void ExampleSendReceive(String args[]) throws MPIException, IOException, ClassNotFoundException
    {
        
         MPI.Init(args);
          int myRank=MPI.COMM_WORLD.getRank();
         if(myRank == 5)
         {
                Bird bird = new Bird("Fast");
                sendObject(bird, 3, 1);
         }
         if(myRank == 3)
         {
            Bird bird = (Bird)recvObject(5, 1);
            System.out.println(bird.getFly());
         }
         MPI.Finalize();
    }
    public static void sendObject(Object a, int proc, int tag)throws MPIException, IOException 
    {
       
        ByteArrayOutputStream bos =new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(a);
        byte[] tmp = bos.toByteArray();
        MPI.COMM_WORLD.send(tmp, tmp.length, MPI.BYTE,proc, tag);
        System.out.println("Send Object to processor "+ proc );
       
    }
    
    public static Object recvObject(int proc, int tag) throws MPIException, IOException,ClassNotFoundException 
    {
        Status st = MPI.COMM_WORLD.probe(proc, tag);
        int size = st.getCount(MPI.BYTE);
        byte[] tmp = new byte[size];
        MPI.COMM_WORLD.recv(tmp, size, MPI.BYTE,proc, tag);
        Object res = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
        ObjectInputStream ois = new ObjectInputStream(bis);
        res = ois.readObject();
        System.out.println("Receive Object from "+proc);
        return res;
    }
    
/*
    openmpi/bin/mpirun --hostfile hostfile -np 10 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/PracticeObjectModule2


    Result :

    Send Object to processor 8
    Send Object to processor 8
    Send Object to processor 8
    Send Object to processor 8
    Receive Object from 2
    Receive Object from 2
    Receive Object from 2
    Receive Object from 2

    Fast
    Slow
    Not flying
    Really fast!!


*/
    
    private static Bird[] ConvertToArrayBird(Object[] objects){

        Bird[] dogs = new Bird[objects.length];
        for (int i=0; i<objects.length; ++i){

            dogs[i] = (Bird)objects[i];

        }
        return dogs;
    }
    
    public static void ExampleSendReceiveArrayOfObjects(String args[]) throws MPIException, IOException, ClassNotFoundException
    {
        
         MPI.Init(args);
         int myRank=MPI.COMM_WORLD.getRank();
         if(myRank == 2)
         {
                Bird []birds = {new Bird("Fast"),new Bird("Slow"),new Bird("Not flying"),new Bird("Really fast!!")};
                sendArrayOfObjects(birds, 8, 1);
         }
         if(myRank == 8)
         {
            Bird []birds =  ConvertToArrayBird(recvArrayOfObjects(2, 1));
            for(int i = 0; i<birds.length; i++)
                System.out.println(birds[i].getFly());
         }
         MPI.Finalize();
    }
    
    public static void sendArrayOfObjects(Object[] a, int proc, int tag) throws MPIException, IOException 
    {
        for (int i = 0; i < a.length; i++)
            sendObject(a[i], proc, tag + i);
    }
    
     public static Object[] recvArrayOfObjects(int proc,int tag) throws MPIException, IOException, ClassNotFoundException 
    {
        Object[] o = new Object[4];
        for (int i = 0; i < 4; i++)
            o[i] = recvObject(proc, tag + i);
        return o;
    }
    
   /*
    openmpi/bin/mpirun --hostfile hostfile -np 10 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/PracticeObjectModule2
    
    
    Result :
    Send Objects to processor 4
    Receive Objects from 3
     
    Nov 21, 2017 9:41:21 PM com.mathpar.students.ukma17i41.sidko.PracticeObjectModule2 recvObjects
    SEVERE: null
    java.io.EOFException
	
    Fast
    Slow
    Not flying
    Really fast!!
    
    */
     
     public static void ExampleSendReceiveObjects(String args[]) throws MPIException, IOException, ClassNotFoundException
    {
        
         MPI.Init(args);
         int myRank=MPI.COMM_WORLD.getRank();
         if(myRank == 3)
         {
                Bird []birds = {new Bird("Fast"),new Bird("Slow"),new Bird("Not flying"),new Bird("Really fast!!")};
                sendObjects(birds, 4, 1);
         }
         if(myRank == 4)
         {
            Bird []birds = ConvertToArrayBird(recvObjects(4, 3, 1));
            for(int i = 0; i<birds.length; i++)
                System.out.println(birds[i].getFly());
         }
         MPI.Finalize();
    }
    
    
    public static void sendObjects(Object[] a, int proc, int tag)throws MPIException 
    {
        ByteArrayOutputStream bos = null;
        try 
        {
            bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            
            for (int i = 0; i < a.length; i++)
                oos.writeObject(a[i]);
            
            bos.toByteArray();
        } 
        catch (Exception ex) {
            Logger.getLogger(Transport.class.getName()).
            log(Level.SEVERE, null, ex);
        }
        byte[] temp = bos.toByteArray();
        ByteBuffer buf = MPI.newByteBuffer(temp.length);
        buf.put(temp);
        MPI.COMM_WORLD.iSend(buf, temp.length, MPI.BYTE, proc, tag);
        System.out.println("Send Objects to processor "+ proc );
    }
    
   
    public static Object[] recvObjects(int m, int proc, int tag) throws MPIException 
    {
        Status s = MPI.COMM_WORLD.probe(proc, tag);
        int n = s.getCount(MPI.BYTE);
        byte[] arr = new byte[n];
        MPI.COMM_WORLD.recv(arr, n, MPI.BYTE, proc, tag);
        System.out.println("Receive Objects from "+proc);
        Object[] res = new Object[m];
        try 
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ObjectInputStream ois = new ObjectInputStream(bis);
            for (int i = 0; i < arr.length; i++)
                res[i] = (Object) ois.readObject();
        } 
        catch (Exception ex) {
            Logger.getLogger(Transport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    
    /*
    openmpi/bin/mpirun --hostfile hostfile -np 10 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/PracticeObjectModule2
    
    Result :
    
    Bcast object processor number 5
    Fast
    Bcast object processor number 2
    Fast
    Bcast object processor number 6
    Fast
    Bcast object processor number 3
    Fast
    Bcast object processor number 0
    Fast
    Bcast object processor number 1
    Fast
    Bcast object processor number 8
    Fast
    Bcast object processor number 7
    Fast
    Bcast object processor number 9
    Fast
    Bcast object processor number 4
    Fast
    
    */
    
    public static void ExampleBcastObject(String args[]) throws MPIException, IOException, ClassNotFoundException
    {
        
         MPI.Init(args);
            Bird bird = new Bird("Fast");
            Bird resbird = (Bird)bcastObject(bird, 5);
            System.out.println("Bcast object processor number " + MPI.COMM_WORLD.getRank());  
            System.out.println(resbird.getFly());
         MPI.Finalize();
    }
    public static Object bcastObject(Object o, int root)throws IOException,MPIException,ClassNotFoundException
    {
        byte []tmp=null;
        int []size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        if (rank==root)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            tmp=bos.toByteArray();
            size[0]=tmp.length;
        }
        MPI.COMM_WORLD.bcast(size,1,MPI.INT,root);
        if (rank!=root) tmp=new byte[size[0]];
        MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);
        if (rank!=root)
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        }
        return o;
    }
    
    
    /*
    
 openmpi/bin/mpirun --hostfile hostfile -np 10 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/PracticeObjectModule2
    
    Result :    
   
    Bcast objects, processor number = 3
    Bcast objects, processor number = 5
    Bcast objects, processor number = 2
    Bcast objects, processor number = 7
    Bcast objects, processor number = 0
    Bcast objects, processor number = 6
    Bcast objects, processor number = 4
    Bcast objects, processor number = 8
    Bcast objects, processor number = 1
    Bcast objects, processor number = 9

    */
    
     public static void ExampleBcastObjects(String args[]) throws MPIException, IOException, ClassNotFoundException
    {
         MPI.Init(args);
            Bird []birds = {new Bird("Fast"),new Bird("Slow"),new Bird("Not flying"),new Bird("Really fast!!")};
            bcastObjectArray(birds,4, 3);
            System.out.println("Bcast objects, processor number = " + MPI.COMM_WORLD.getRank());  
         MPI.Finalize();
    }
    public static void bcastObjectArray(Object []o, int count, int root)throws IOException, MPIException,ClassNotFoundException
    {
        byte []tmp=null;
        int []size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        if (rank==root)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            for (int i=0; i<count; i++) oos.writeObject(o[i]);
            tmp=bos.toByteArray();
            size[0]=tmp.length;
        }
        MPI.COMM_WORLD.bcast(size,1,MPI.INT,root);
        if (rank!=root) tmp=new byte[size[0]];
        MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);
        if (rank!=root)
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
            ObjectInputStream ois = new ObjectInputStream(bis);
            for (int i=0; i<count; i++) o[i]=ois.readObject();
        }
    }   
     public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
      //ExampleSendReceive(args);
      //ExampleSendReceiveArrayOfObjects(args);
      //ExampleSendReceiveObjects(args);
      //ExampleBcastObject(args);
      ExampleBcastObjects(args);
       
    }
}
     
     class Bird implements Serializable
     {
         String _fly;
         
         Bird(String fly)
         {
             _fly = fly;
         }
         
         String getFly()
         {
             return _fly;
         }
         
     
     }
  




