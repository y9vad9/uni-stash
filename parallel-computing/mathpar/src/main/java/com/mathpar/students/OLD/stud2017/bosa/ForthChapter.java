/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2017.bosa;

import com.mathpar.students.OLD.llp2.student.helloworldmpi.Transport;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;


/**
 *
 * @author sasha
*/
//openmpi/bin/mpirun --hostfile hostfile -np 10 java -cp /home/sasha/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/bosa/ForthChapter
public class ForthChapter {
    
   
    
public static void sendObject(Object a, int proc, int tag) throws MPIException, IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(a);
        byte[] tmp = bos.toByteArray();
        MPI.COMM_WORLD.send(tmp, tmp.length, MPI.BYTE,proc, tag);

}
     
public static Object recvObject(int proc, int tag) throws MPIException, IOException,ClassNotFoundException {

        Status st = MPI.COMM_WORLD.probe(proc, tag);

        int size = st.getCount(MPI.BYTE);

        byte[] tmp = new byte[size];

        MPI.COMM_WORLD.recv(tmp, size, MPI.BYTE, proc, tag);
        Object res = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
        ObjectInputStream ois = new ObjectInputStream(bis);
        res = ois.readObject();

        return res;
}

    static void sendRecv(String[] args)throws MPIException, IOException, ClassNotFoundException{
        
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        
        if(myrank == 2)
        {
            MyObject obj = new MyObject(3);
            sendObject(obj,8,0);
            System.out.println("PROC 2 SENDING TO PROC 8: "+ obj);
            
        }
        if(myrank == 8)
        {
             MyObject obj = (MyObject)recvObject(2,0);
             System.out.println("PROC 8 RECIEVED FROM PROC 2: "+ obj);  
        }
           
        System.out.println("Nor 2d proc neither 8th");  
        MPI.Finalize();
         
    }
    
    /***********result for sendrecvObject
Not 2d proc neither 8th
Not 2d proc neither 8th
Not 2d proc neither 8thNot 2d proc niether 8th
Not 2d proc neither 8th

Not 2d proc neither 8th
Not 2d proc neither 8th
Not 2d proc neither 8th
PROC 2 SENDING TO PROC 8: com.mathpar.students.ukma17i41.bosa.MyObject@372f7a8d
Not 2d proc neither 8th
PROC 8 RECIEVED FROM PROC 2: com.mathpar.students.ukma17i41.bosa.MyObject@1e80bfe8
Not 2d proc neither 8th

*/
    
     private static MyObject[] convertToArray(Object[] objs) {
        
        MyObject[] myobjs = new MyObject[objs.length];
        for(int i=0; i<objs.length; ++i){
            myobjs[i]=(MyObject)objs[i];
        }
        
        return myobjs;
            
        }
    
    public static void sendArrayOfObjects(Object[] a, int proc, int tag) throws MPIException,IOException {

        for (int i = 0; i < a.length; i++)
         sendObject(a[i], proc, tag + i);
    }
    
    public static Object[] recvArrayOfObjects(int proc, int tag) throws MPIException, IOException, ClassNotFoundException {
        Object[] o = new Object[4];
        for (int i = 0; i < 4; i++)
            o[i] = recvObject(proc, tag + i);
        
        return o;
    }
    
    public static void sendRecvArray(String[] args) throws MPIException, IOException, ClassNotFoundException{
        
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();

        if(myrank == 4)
        { 
            MyObject[] Arrobj = {new MyObject(3),new MyObject(5),new MyObject(7),new MyObject(8)};
            sendArrayOfObjects(Arrobj,6,0);
            for(int i =0; i<Arrobj.length; ++i)
                System.out.println("PROC 4 SENDING TO PROC 6: "+ Arrobj[i].getId());
            
        }
        if(myrank == 6)
        {
            MyObject[] Arrobj = convertToArray(recvArrayOfObjects(4,0));
             for(int i=0; i<Arrobj.length; i++)
                System.out.println("PROC 6 RECIEVED FROM PROC 4: "+ Arrobj[i].getId());  
        }
           
        System.out.println("Nor 4th proc neither 6th");  
        MPI.Finalize();
        
    }
    
    /*******result for sendrecvArrayOfObjects
     Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
PROC 4 SENDING TO PROC 6: 3
PROC 4 SENDING TO PROC 6: 5
PROC 4 SENDING TO PROC 6: 7
PROC 4 SENDING TO PROC 6: 8
Nor 4th proc neither 6th
PROC 6 RECIEVED FROM PROC 4: 3
PROC 6 RECIEVED FROM PROC 4: 5
PROC 6 RECIEVED FROM PROC 4: 7
PROC 6 RECIEVED FROM PROC 4: 8
Nor 4th proc neither 6th



    */
    
    public static void sendObjects(Object[] a, int proc, int tag) throws MPIException {

           ByteArrayOutputStream bos = null;

           try {

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
           MPI.COMM_WORLD.iSend(buf, temp.length,
           MPI.BYTE, proc, tag);
     }   
    
    public static Object[] recvObjects(int m, int proc, int tag) throws MPIException {
        
        Status s = MPI.COMM_WORLD.probe(proc, tag);
        int n = s.getCount(MPI.BYTE);
        byte[] arr = new byte[n];
        
        MPI.COMM_WORLD.recv(arr, n, MPI.BYTE, proc, tag);
        Object[] res = new Object[m];
        
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ObjectInputStream ois = new ObjectInputStream(bis);
        
        for (int i = 0; i < arr.length; i++)
         res[i] = (Object) ois.readObject();
        } 
        catch (Exception ex) {
            
        Logger.getLogger(Transport.class.getName()).
        log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
  

    public static void sendRecvObjects(String[] args)throws MPIException, IOException, ClassNotFoundException{
        
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
       if(myrank == 4)
        { 
            MyObject[] Arrobj = {new MyObject(3),new MyObject(5),new MyObject(7),new MyObject(8)};
            sendArrayOfObjects(Arrobj,6,0);
            for(int i =0; i<Arrobj.length; ++i)
                System.out.println("PROC 4 SENDING TO PROC 6: "+ Arrobj[i].getId());
            
        }
        if(myrank == 6)
        {
            MyObject[] Arrobj = convertToArray(recvArrayOfObjects(4,0));
             for(int i=0; i<Arrobj.length; i++)
                System.out.println("PROC 6 RECIEVED FROM PROC 4: "+ Arrobj[i].getId());  
        }
           
           
        System.out.println("Nor 4th proc neither 6th");  
        MPI.Finalize();
    }
    
    /**************result for sendrecvObjects
    Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
Nor 4th proc neither 6th
PROC 4 SENDING TO PROC 6: 3
PROC 4 SENDING TO PROC 6: 5
PROC 4 SENDING TO PROC 6: 7
PROC 4 SENDING TO PROC 6: 8
Nor 4th proc neither 6th
PROC 6 RECIEVED FROM PROC 4: 3
PROC 6 RECIEVED FROM PROC 4: 5
PROC 6 RECIEVED FROM PROC 4: 7
PROC 6 RECIEVED FROM PROC 4: 8
Nor 4th proc neither 6th

    */
    
    
    public static void bcastObjectArray(Object []o, int count, int root) throws IOException, MPIException,ClassNotFoundException{
        
        byte []tmp=null;
        int []size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        
        if (rank==root){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        for (int i=0; i<count; i++)
            oos.writeObject(o[i]);
       
        tmp=bos.toByteArray();
        size[0]=tmp.length;
        }
        
        MPI.COMM_WORLD.bcast(size,1,MPI.INT,root);
        
        if (rank!=root) 
            tmp=new byte[size[0]];
        
        MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);
        
        if (rank!=root){
            
        ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        for (int i=0; i<count; i++) 
            o[i]=ois.readObject();
        }
    }
    
    public static void bcastCall(String[] args) throws IOException, MPIException,ClassNotFoundException{
        
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        
            MyObject[] Arrobj = new MyObject[4];
            for(int i=0; i<4; i++)
                Arrobj[i] = new MyObject(0);   
            
            //System.out.println("Starting broadcast");  
     
            bcastObjectArray(Arrobj,1,0);
            for(int i=0; i<Arrobj.length; i++)
                System.out.println("Proc = " + myrank+" Broadcasted successfully ");  
            
       
        MPI.Finalize();
    }
    
    /*************result for bcast**************
Proc = 0 Broadcasted successfully
Proc = 6 Broadcasted successfully
Proc = 4 Broadcasted successfully
Proc = 9 Broadcasted successfully
Proc = 3 Broadcasted successfully
Proc = 2 Broadcasted successfully
Proc = 5 Broadcasted successfully
Proc = 7 Broadcasted successfully
Proc = 8 Broadcasted successfully
Proc = 1 Broadcasted successfully
    */
    
     
     public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException{
         
        //sendRecv(args); 
       // sendRecvArray(args);
       // sendRecvObjects(args);
         bcastCall(args);
     }
     
     
};



 class MyObject implements Serializable{
    int id;
    
    int getId(){ 
    return id; }
    
    void setId(int nid){
        this.id=nid;
    }
    
    MyObject(int Id){
        this.id=Id;
    }
    
}


