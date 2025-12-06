/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import mpi.Datatype;
import mpi.MPI;
import mpi.MPIException;
import mpi.Request;
import mpi.Status;

/**
 *
 * @author r1d1
 */
public class MPITransport {
    public static void sendObject(Object o, int dest, int tag) throws IOException,MPIException{
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(o);
        byte []tmp=bos.toByteArray();
        MPI.COMM_WORLD.send(tmp,tmp.length, MPI.BYTE, dest, tag);
    }

    public static Object recvObject(int source, int tag)throws IOException,MPIException,ClassNotFoundException{
        Status st=MPI.COMM_WORLD.probe(source, tag);
        int size=st.getCount(MPI.BYTE);
        byte[]tmp=new byte[size];
        MPI.COMM_WORLD.recv(tmp, size, MPI.BYTE, source, tag);
        Object res=null;
        ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
        ObjectInputStream ois=new ObjectInputStream(bis);
        res=ois.readObject();
        return res;
    }

    public static void iSendObject(Object o, int dest, int tag) throws IOException,MPIException{
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(o);
        byte []tmp=bos.toByteArray();
        ByteBuffer buf=MPI.newByteBuffer(tmp.length);
        buf.put(tmp);
        MPI.COMM_WORLD.iSend(buf,tmp.length, MPI.BYTE, dest, tag);
    }

     public static Object iRecvObject(int source, int tag)throws IOException,MPIException,ClassNotFoundException{
        //!!! too many coping...
        Status st=MPI.COMM_WORLD.probe(source, tag);
        int size=st.getCount(MPI.BYTE);
        ByteBuffer buf=MPI.newByteBuffer(size);
        MPI.COMM_WORLD.iRecv(buf, size, MPI.BYTE, source, tag);
        Object res=null;
        byte[]tmp = new byte[buf.remaining()];
        buf.get(tmp,0,size);
        ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
        ObjectInputStream ois=new ObjectInputStream(bis);
        res=ois.readObject();
        return res;
    }

    public static void sendObjectArray(Object []o, int offset, int count, int dest, int tag) throws IOException,MPIException{
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        for (int i=offset; i<offset+count; i++){
            oos.writeObject(o[i]);
        }
        byte []tmp=bos.toByteArray();
        MPI.COMM_WORLD.send(tmp,tmp.length, MPI.BYTE, dest, tag);
    }

    public static void recvObjectArray(Object []o, int offset, int count, int source, int tag)throws IOException,MPIException,ClassNotFoundException{
        Status st=MPI.COMM_WORLD.probe(source, tag);
        int size=st.getCount(MPI.BYTE);
        byte[]tmp=new byte[size];
        MPI.COMM_WORLD.recv(tmp, size, MPI.BYTE, source, tag);
        Object res=null;
        ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
        ObjectInputStream ois=new ObjectInputStream(bis);
        for (int i=offset; i<offset+count; i++){
            o[i]=ois.readObject();
        }
    }

    public static void iSendObjectArray(Object []o, int offset, int count, int dest, int tag) throws IOException,MPIException{
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        for (int i=offset; i<offset+count; i++){
            oos.writeObject(o[i]);
        }
        byte []tmp=bos.toByteArray();
        ByteBuffer buf=MPI.newByteBuffer(tmp.length);
        buf.put(tmp);
        MPI.COMM_WORLD.iSend(buf,tmp.length, MPI.BYTE, dest, tag);
    }

    public static void iRecvObjectArray(Object []o, int offset, int count, int source, int tag)throws IOException,MPIException,ClassNotFoundException{
        //!!! too many coping...
        Status st=MPI.COMM_WORLD.probe(source, tag);
        int size=st.getCount(MPI.BYTE);
        ByteBuffer buf=MPI.newByteBuffer(size);
        MPI.COMM_WORLD.iRecv(buf, size, MPI.BYTE, source, tag);
        Object res=null;
        byte[]tmp = new byte[buf.remaining()];
        buf.get(tmp,0,size);
        ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
        ObjectInputStream ois=new ObjectInputStream(bis);
        for (int i=offset; i<offset+count; i++){
            o[i]=ois.readObject();
        }
    }

    public static void bcastObjectArray(Object []o, int count, int root) throws IOException,MPIException,ClassNotFoundException{
        byte []tmp=null;
        int []size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        if (rank==root){
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos);
            for (int i=0; i<count; i++){
                oos.writeObject(o[i]);
            }
            tmp=bos.toByteArray();
            size[0]=tmp.length;
        }
        MPI.COMM_WORLD.bcast(size,1,MPI.INT,root);
        if (rank!=root){
            tmp=new byte[size[0]];
        }
        MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);
        if (rank!=root){
            ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
            ObjectInputStream ois=new ObjectInputStream(bis);
            for (int i=0; i<count; i++){
                o[i]=ois.readObject();
            }
        }
    }
    
    public static Object bcastObject(Object o, int root) throws IOException,MPIException,ClassNotFoundException{
        byte []tmp=null;
        int []size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        if (rank==root){
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos);            
            oos.writeObject(o);            
            tmp=bos.toByteArray();
            size[0]=tmp.length;
        }
        MPI.COMM_WORLD.bcast(size,1,MPI.INT,root);
        if (rank!=root){
            tmp=new byte[size[0]];
        }
        MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);
        if (rank!=root){
            ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
            ObjectInputStream ois=new ObjectInputStream(bis);            
            return ois.readObject();            
        }
        return o;
    }
    

    public static void bcastObjectArrayOld(Object []o,int offset, int count, int root) throws IOException,MPIException,ClassNotFoundException{
        byte []tmp=null;
        int []size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        if (rank==root){
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos);
            for (int i=offset; i<offset+count; i++){
                oos.writeObject(o[i]);
            }
            tmp=bos.toByteArray();
            size[0]=tmp.length;
        }
        MPI.COMM_WORLD.bcast(size,1,MPI.INT,root);
        if (rank!=root){
            tmp=new byte[size[0]];
        }
        MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);
        if (rank!=root){
            ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
            ObjectInputStream ois=new ObjectInputStream(bis);
            for (int i=offset; i<offset+count; i++){
                o[i]=ois.readObject();
            }
        }
    }

     public static void sendOld(Object o,int offset, int count, Datatype type, int dest, int tag) throws MPIException{
        switch (type.getName()){
            //MPI.INT
            case "MPI_INT32_T":{
                int []s=(int[])o;
                int []tmp=new int[count];
                for (int i=0; i<count; i++){
                    tmp[i]=s[i+offset];
                }
                MPI.COMM_WORLD.send(tmp,count,type,dest,tag);
                break;
            }
            //MPI.DOUBLE
            case "MPI_DOUBLE":{
                double []s=(double[])o;
                double []tmp=new double[count];
                for (int i=0; i<count; i++){
                    tmp[i]=s[i+offset];
                }
                MPI.COMM_WORLD.send(tmp,count,type,dest,tag);
                break;
            }
            //MPI.LONG
            case "MPI_INT64_T":{
                long[]s=(long[])o;
                long []tmp=new long[count];
                for (int i=0; i<count; i++){
                    tmp[i]=s[i+offset];
                }
                MPI.COMM_WORLD.send(tmp,count,type,dest,tag);
                break;
            }
        }

    }


     public static void iSendOld(Object o,int offset, int count, Datatype type, int dest, int tag) throws MPIException{
        switch (type.getName()){
            //MPI.INT
            case "MPI_INT32_T":{
                int []s=(int[])o;
                IntBuffer tmp=MPI.newIntBuffer(count);
                for (int i=0; i<count; i++){
                    tmp.put(s[i+offset]);
                }
                MPI.COMM_WORLD.iSend(tmp,count,type,dest,tag);
                break;
            }
            //MPI.DOUBLE
            case "MPI_DOUBLE":{
                double []s=(double[])o;
                DoubleBuffer tmp=MPI.newDoubleBuffer(count);
                for (int i=0; i<count; i++){
                    tmp.put(s[i+offset]);
                }
                MPI.COMM_WORLD.iSend(tmp,count,type,dest,tag);
                break;
            }
            //MPI.LONG
            case "MPI_INT64_T":{
                long []s=(long[])o;
                LongBuffer tmp=MPI.newLongBuffer(count);
                for (int i=0; i<count; i++){
                    tmp.put(s[i+offset]);
                }
                MPI.COMM_WORLD.iSend(tmp,count,type,dest,tag);
                break;
            }
        }

    }

    public static void recvOld(Object o,int offset, int count, Datatype type, int source, int tag) throws MPIException{
        switch (type.getName()){
            //MPI.INT
            case "MPI_INT32_T":{
                int []tmp=new int[count];
                MPI.COMM_WORLD.recv(tmp,count,type,source,tag);
                int []d=(int[])o;
                for (int i=0; i<count; i++){
                    d[offset+i]=tmp[i];
                }
                break;
            }
            //MPI.DOUBLE
            case "MPI_DOUBLE":{
                double []tmp=new double[count];
                MPI.COMM_WORLD.recv(tmp,count,type,source,tag);
                double []d=(double[])o;
                for (int i=0; i<count; i++){
                    d[offset+i]=tmp[i];
                }
                break;
            }
            //MPI.LONG
            case "MPI_INT64_T":{
                long []tmp=new long[count];
                MPI.COMM_WORLD.recv(tmp,count,type,source,tag);
                long []d=(long[])o;
                for (int i=0; i<count; i++){
                    d[offset+i]=tmp[i];
                }
                break;
            }
        }

    }

    public static void iRecvOld(Object o,int offset, int count, Datatype type, int source, int tag) throws MPIException{
        switch (type.getName()){
            //MPI.INT
            case "MPI_INT32_T":{
                IntBuffer tmp=MPI.newIntBuffer(count);
                Request req=MPI.COMM_WORLD.iRecv(tmp,count,type,source,tag);
                while (!req.test());//!!! dirty huck
                int []s=(int[])o;
                for (int i=0; i<count; i++){
                    s[i+offset]=tmp.get(i);
                }
                break;
            }
            //MPI.DOUBLE
            case "MPI_DOUBLE":{
                DoubleBuffer tmp=MPI.newDoubleBuffer(count);
                Request req=MPI.COMM_WORLD.iRecv(tmp,count,type,source,tag);
                while (!req.test());//!!! dirty huck
                double []s=(double[])o;
                for (int i=0; i<count; i++){
                    s[i+offset]=tmp.get(i);
                }
                break;
            }
            //MPI.LONG
            case "MPI_INT64_T":{
                LongBuffer tmp=MPI.newLongBuffer(count);
                Request req=MPI.COMM_WORLD.iRecv(tmp,count,type,source,tag);
                while (!req.test());//!!! dirty huck
                long []s=(long[])o;
                for (int i=0; i<count; i++){
                    s[i+offset]=tmp.get(i);
                }
                break;
            }
        }

    }
}
