/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mpi.MPI;
import mpi.MPIException;


/**
 *
 * @author ridkeim
 */
public class ETDmpiUtils {
    private static long sending_time =0;
    private static long tmp_time =0;
    private static boolean logging = false;
    private static void initTime(){
        tmp_time = System.currentTimeMillis();
    };
    private static void addTime(){
        long dif = System.currentTimeMillis()-tmp_time;
        sending_time += (dif>0)?dif:0;
    };
    public static void resetTime(){
        sending_time =0;
    };
    public static long getTime(){
        return sending_time;
    };
    public static void setLogging(boolean log){
        logging = log;
    }
    public static boolean isLogging(){
        return logging;
    }
    
    
    @SuppressWarnings("unchecked")
    public static <T> T bcastObject(T o, int root) throws MPIException{
        initTime();
        byte []tmp=null;
        int []size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        if (rank==root){
            try(ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos)){
                oos.writeObject(o);            
                tmp=bos.toByteArray();
                size[0]=tmp.length;
                if(logging)System.out.print("sending object from "+root+" "+o+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.bcast(size,1,MPI.INT,root);
        if (rank!=root){
            tmp=new byte[size[0]];
        }
        addTime();
        if(tmp != null && tmp.length > 0){
            initTime();
            MPI.COMM_WORLD.barrier();
            MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);         
            if (rank!=root){
                T res = null;
                try(ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
                ObjectInputStream ois=new ObjectInputStream(bis)){
                    res = (T) ois.readObject();
                    if(logging)System.out.print("recieved object on "+rank+" "+res+"\n");
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
                addTime();
                return res;                    
            }else{
                addTime();
                return o;
            }
        }else{
            return null;
        }
    }
    
    public static <T> void bcastArray(T[] o, int root) throws MPIException{
        initTime();
        byte[] tmp=null;
        int[] size=new int[1];
        int rank=MPI.COMM_WORLD.getRank();
        if (rank==root){
            try (ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos)){
                for (int i=0; i<o.length; i++){
                oos.writeObject(o[i]);
            }
            tmp=bos.toByteArray();
            size[0]=tmp.length;
            if(logging)System.out.print("sending object from "+root+" "+Arrays.toString(o)+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.bcast(size,size.length,MPI.INT,root);
        if (rank!=root){
            tmp=new byte[size[0]];
        }
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.bcast(tmp,tmp.length,MPI.BYTE,root);
        if (rank!=root){
            try(ByteArrayInputStream bis=new ByteArrayInputStream(tmp);
                ObjectInputStream ois=new ObjectInputStream(bis)){
                for (int i=0; i<o.length; i++){
                    o[i]= (T) ois.readObject();
                    if(logging)System.out.print("recieved object on "+rank+" "+Arrays.toString(o)+"\n");
                }
            }catch (IOException e){
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, e);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }        
    }
    
   /**
    * Пересылка данных по процессорам, используя метод AlltoAllv.
    * @param <T>
    * @param map карта рассылки данных по процессорам, ключ Integer означает какому процессору будут отправлен данных в виде List, не обязательно наличие всех процессоров.
    * @return возвращает данные полученные со всех процессоров в виде List.
    * @throws MPIException
    */
    @SuppressWarnings("unchecked")
    public static <T> List<T> AlltoAllv(Map<Integer,List<T>> map) throws MPIException{
        initTime();
        int size = MPI.COMM_WORLD.getSize();
        int rank = MPI.COMM_WORLD.getRank();
        List<T> recvList = null;
        int[] sendcount = new int[size];
        int[] sdispls = new int[size]; 
        int[] recvcount = new int[size];
        int[] rdispls = new int[size];
        byte[][] sendbuffer = new byte[size][0];
        int pointer = 0;
        for (Map.Entry<Integer, List<T>> entrySet : map.entrySet()) {
            Integer key = entrySet.getKey();
            List<T> value = entrySet.getValue();
            if(key<0 || key>=size){
                String msg = "Trying send data from processor number "+rank+" to processor number "+key+". There is no such processor. Ignoring data.";
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.WARNING, msg);
                continue;
            }
            if(recvList == null){
                try {
                    recvList = value.getClass().newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    recvList = null;
                }
            }
            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);){
                oos.writeObject(value);
                sendbuffer[key] = bos.toByteArray();
                pointer+= sendbuffer[key].length;
//                if(logging)System.out.print("sending object from "+rank+" to "+key+" "+value+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        byte[] sendbuf = new byte[pointer];
        pointer = 0;
        for (int i = 0; i < sendbuffer.length; i++) {
            int sendcounttmp = sendbuffer[i].length;
            sdispls[i] = pointer;
            sendcount[i] = sendcounttmp;
            pointer += sendcounttmp;
            if(logging){
                System.out.print("\nstart copying array on "+rank+"\n");
            }
            System.arraycopy(sendbuffer[i], 0, sendbuf, sdispls[i], sendcounttmp);            
            sendbuffer[i] = null;
        }
        sendbuffer = null;
        if(logging){
            String s = "\nsendcount on "+rank+" "+sendcount.length+""+Arrays.toString(sendcount)+"\n"
                    + "recievecount on "+rank+" "+recvcount.length+""+Arrays.toString(recvcount)+"\n"
                    + "sdispls on "+rank+" "+Arrays.toString(sdispls)+"\n"
                    + "start sending s/r counts on "+rank+"\n ";
            System.out.print(s);
        }
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.allToAll(sendcount, 1, MPI.INT, recvcount, 1, MPI.INT);
        if(logging){
            String s = "\nsending s/r counts done on "+rank+"\n"
                    + "af sendcount on "+rank+" "+sendcount.length+""+Arrays.toString(sendcount)+"\n"
                    + "af recievecount on "+rank+" "+recvcount.length+""+Arrays.toString(recvcount)+"\n";
            System.out.print(s);
        }
        int receivesize = 0;
        for (int i = 0; i < recvcount.length; i++) {
            rdispls[i]= receivesize;
            receivesize = receivesize+recvcount[i];
        }
        if(logging){
            String s= "\nrecivesize on "+ rank+" "+receivesize+"\n"
                    + "rdispls on "+rank+" "+Arrays.toString(rdispls)+"\n";
            System.out.print(s);
        }
        byte[] recivedata = new byte[receivesize];
        if(logging)System.out.print("\nAlltoAllv main data send begin on "+rank+"\n");
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.allToAllv(sendbuf, sendcount, sdispls, MPI.BYTE, recivedata, recvcount, rdispls, MPI.BYTE);
        if(logging){
            String s = "\naf recivecount on proc "+ rank+" "+ Arrays.toString(recvcount)+"\n"
                    + "af rdispls on proc "+ rank+" "+ Arrays.toString(rdispls)+"\n"
                    + "af recivesize on proc "+rank +" "+ receivesize+"\n"
                    + "AlltoAllv main data send done on "+rank+"\n";
            System.out.print(s);
        }
        MPI.COMM_WORLD.barrier();
        recvList = (recvList == null) ? new ArrayList<T>() : recvList;
        if(logging)System.out.println("\n start parsering data on "+rank+"\n");
        for (int i = 0; i < recvcount.length; i++) {
            if(recvcount[i]>0){
                try(ByteArrayInputStream bin = new ByteArrayInputStream(recivedata,rdispls[i], recvcount[i]);
                        ObjectInputStream oin = new ObjectInputStream(bin)) {
                    Object recvObject = oin.readObject();
                    if(recvObject instanceof List<?>){
                       List<T> tmpObj = (List<T>) recvObject;
                       recvList.addAll(tmpObj);
                    }
                } catch (IOException e){
                    String msg = "Something bad while reading data on proc "+ rank+" rd="+rdispls[i]+" recvcount="+recvcount[i];
                    Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, msg, e);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        addTime();
        return recvList;
    }
    
    public static <T> List<T> gatherv(List<T> list,int root) throws MPIException{
        initTime();
        int size = MPI.COMM_WORLD.getSize();
        int rank = MPI.COMM_WORLD.getRank();
        int[] recvcount = new int[size];
        int[] rdispls = new int[size];
        byte[] sendbuffer = new byte[0];
        List<T> recvList = null;
        try {
            recvList = (list!=null)?list.getClass().newInstance():new ArrayList<T>();
        } catch (InstantiationException | IllegalAccessException ex) {
            recvList = new ArrayList<>();
        }
        if(list != null){
            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);){
                oos.writeObject(list);
                sendbuffer = bos.toByteArray();
            } catch (IOException ex) {
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        recvcount[0] = sendbuffer.length;
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.gather(recvcount, 1, MPI.INT, recvcount, 1, MPI.INT,root);
        int receivesize = 0;
        if(rank == root){
            for (int i = 0; i < recvcount.length; i++) {
                rdispls[i]= receivesize;
                receivesize = receivesize+recvcount[i];
            }
        }
        byte[] recivedata = new byte[receivesize];
        MPI.COMM_WORLD.barrier();
        MPI.COMM_WORLD.gatherv(sendbuffer, sendbuffer.length, MPI.BYTE, recivedata, recvcount, rdispls, MPI.BYTE,root);
        if(rank == root){
            for (int i = 0; i < recvcount.length; i++) {
                if(recvcount[i]>0){
                    try(ByteArrayInputStream bin = new ByteArrayInputStream(recivedata,rdispls[i], recvcount[i]);
                            ObjectInputStream oin = new ObjectInputStream(bin)) {
                        Object recvObject = oin.readObject();
                        if(recvObject instanceof List<?>){
                           List<T> tmpObj = (List<T>) recvObject;
                           recvList.addAll(tmpObj);
                        }
                    } catch (IOException e){
                        String msg = "Something bad while reading data on proc "+ rank+" rd="+rdispls[i]+" recvcount="+recvcount[i];
                        Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, msg, e);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        addTime();
        return recvList;
    }
    
    public static <T> List<T> gathervSeparated(List<T> list,int root) throws MPIException{
        initTime();
        int size = MPI.COMM_WORLD.getSize();
        int rank = MPI.COMM_WORLD.getRank();
        int[] recvcount = new int[size];
        int[] rdispls = new int[size];
        byte[] sendbuffer = new byte[0];
        List<T> recvList = null;
        try {
            recvList = (list!=null)? list.getClass().newInstance() : new ArrayList<T>();
        } catch (InstantiationException | IllegalAccessException ex) {
            recvList = new ArrayList<>();
        }
        if(list != null && rank != root){
            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);){
                oos.writeObject(list);
                sendbuffer = bos.toByteArray();
                if(logging)System.out.print("sending object from "+rank+" to "+root+" "+list+"\n");
            } catch (IOException ex) {
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MPI.COMM_WORLD.barrier();
        if(root == rank){
            MPI.COMM_WORLD.gather(recvcount, 1, MPI.INT, root);
        }else{
            MPI.COMM_WORLD.gather(new int[]{sendbuffer.length}, 1, MPI.INT, root);
        }
//        if(rank == root){
//            System.out.println(Arrays.toString(recvcount));
//        }
        int receivesize = 0;
        if(rank == root){
            for (int i = 0; i < recvcount.length; i++) {
                rdispls[i]= receivesize;
                receivesize = receivesize+recvcount[i];
            }
        }
        byte[] recivedata = new byte[receivesize];
        MPI.COMM_WORLD.barrier();
        if(rank == root){
            MPI.COMM_WORLD.gatherv(recivedata, recvcount, rdispls, MPI.BYTE,root);
        }else{
            MPI.COMM_WORLD.gatherv(sendbuffer, sendbuffer.length, MPI.BYTE,root);
        }        
        if(rank == root){
            for (int i = 0; i < recvcount.length; i++) {
                if(i == root){
                    if(list != null) recvList.addAll(list);
                }else{
                    if(recvcount[i]>0){
                        try(ByteArrayInputStream bin = new ByteArrayInputStream(recivedata,rdispls[i], recvcount[i]);
                                ObjectInputStream oin = new ObjectInputStream(bin)) {
        //                    System.out.println("rank="+rank+" recv="+ Arrays.toString(Arrays.copyOfRange(recivedata, rdispls[i], rdispls[i]+recvcount[i])));
                            Object recvObject = oin.readObject();
                            if(recvObject instanceof List<?>){
                               List<T> tmpObj = (List<T>) recvObject;
                               recvList.addAll(tmpObj);
                            }
                        } catch (IOException e){
                            String msg = "Something bad while reading data on proc "+ rank+" rd="+rdispls[i]+" recvcount="+recvcount[i];
                            Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, msg, e);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        addTime();
//        System.out.println("recived data on proc "+rank +" "+ recvList.toString());
        return recvList;
    }
    
    public static <T> List<T> gathervSeparated(T obj,int root) throws MPIException{
        initTime();
        int size = MPI.COMM_WORLD.getSize();
        int rank = MPI.COMM_WORLD.getRank();        
//        System.out.println("initial obj on "+rank+" "+obj);
        int[] recvcount = new int[size];
        int[] rdispls = new int[size];
        byte[] sendbuffer = new byte[0];
        List<T> list = new ArrayList<>(size);
        if(obj != null && rank != root){
//            System.out.println("data to send on proc "+rank +" "+ list.toString());
            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);){
                oos.writeObject(obj);
                sendbuffer = bos.toByteArray();
            } catch (IOException ex) {
                Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MPI.COMM_WORLD.barrier();
        if(root == rank){
            MPI.COMM_WORLD.gather(recvcount, 1, MPI.INT, root);
        }else{
            MPI.COMM_WORLD.gather(new int[]{sendbuffer.length}, 1, MPI.INT, root);
        }
        if(rank == root){
//            System.out.println("recive counts = "+Arrays.toString(recvcount));
        }
        int receivesize = 0;
        if(rank == root){
            for (int i = 0; i < recvcount.length; i++) {
                rdispls[i]= receivesize;
                receivesize = receivesize+recvcount[i];
            }
        }
        byte[] recivedata = new byte[receivesize];
        MPI.COMM_WORLD.barrier();
        if(rank == root){
            MPI.COMM_WORLD.gatherv(recivedata, recvcount, rdispls, MPI.BYTE,root);
        }else{
            MPI.COMM_WORLD.gatherv(sendbuffer, sendbuffer.length, MPI.BYTE,root);
        }        
        if(rank == root){
            for (int i = 0; i < recvcount.length; i++) {
                if(i == root){
                    if(obj != null) list.add(obj);
//                    System.out.println("object on root"+obj);
                }else{
                    if(recvcount[i]>0){
                        try(ByteArrayInputStream bin = new ByteArrayInputStream(recivedata,rdispls[i], recvcount[i]);
                                ObjectInputStream oin = new ObjectInputStream(bin)) {
                            Object recvObject = oin.readObject();
                            list.add((T) recvObject);
                            if(recvObject instanceof List<?>){
                               List<T> tmpObj = (List<T>) recvObject;
                               list.addAll(tmpObj);
                               if(logging)System.out.print("recieved object from "+i+" on "+root+" "+tmpObj+"\n");
                            }
                        } catch (IOException e){
                            String msg = "Something bad while reading data on proc "+ rank+" rd="+rdispls[i]+" recvcount="+recvcount[i];
                            Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, msg, e);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(ETDmpiUtils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        addTime();
        return list;
    }
        
    public static void main(String[] args) {
//        int[] mod = new int[]{2,3,5};  //43
//        int[] rem = new int[]{43 % 2, 43 % 3,43 % 5};
//        int[] mod1 = new int[]{7};
//        int[] rem1 = new int[]{43 % 7};
//        int[] mod2 = new int[]{11};
//        int[] rem2 = new int[]{43 % 11};
//        int[] mod3 = new int[]{2,3,5,7,11};
//        int[] rem3 = new int[]{43 % 2, 43 % 3,43 % 5,43 % 7, 43 % 11};
//        int[] mod01 = new int[]{2,3,5,7};
//        NumberZ[] numbNewton = arrayOfNumbersForNewton(mod);
//        System.out.println("numbN"+Arrays.toString(numbNewton));
//        NumberZ[] recov = recoveryNewtonWithoutArr(mod, rem, numbNewton);
//        System.out.println("recov"+Arrays.toString(recov));
//        
//        NumberZ[] numbNewton1 = arrayOfNumbersForNewtonProceed(mod01, numbNewton, mod.length);
//        System.out.println("numbN1"+Arrays.toString(numbNewton1));
//        NumberZ recov1 =  recoveryNewtonWithoutArrProceed(mod1, rem1, numbNewton1, recov);
//        System.out.println(recov1);
//        NumberZ[] numbNewton2 = arrayOfNumbersForNewtonProceed(mod3, numbNewton1, mod01.length);
//        System.out.println("numbN2"+Arrays.toString(numbNewton2));
//        NumberZ recov2 =  recoveryNewtonWithoutArrProceed(mod2, rem2, numbNewton2,new NumberZ[]{recov1});
//        System.out.println(recov2);
//        
//        NumberZ[] numbNewton3 = arrayOfNumbersForNewton(mod3);
//        System.out.println("numbN3"+Arrays.toString(numbNewton3));
//        NumberZ[] recov3 = recoveryNewtonWithoutArr(mod3, rem3, numbNewton3);
//        System.out.println(Arrays.toString(recov3));
    }
}
