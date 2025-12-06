/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2017.sidko;

import java.nio.IntBuffer;
import java.util.Random;
import mpi.MPI;
import mpi.MPIException;

/**
 *
 * @author alla
 */
public class Practice {
     private static void TaskSend(String[] args) throws MPIException {
         
          MPI.Init(args);
         int myRank=MPI.COMM_WORLD.getRank();
         int n=Integer.parseInt(args[0]);
         int []a=new int[n];
         
         if (myRank==0)
         {   
             Random rnd=new Random();
             for (int i=0; i<n; i++)
             {
                 a[i]=rnd.nextInt()%n;
             }
             
             MPI.COMM_WORLD.send(a, n, MPI.INT, 1, 0);
             System.out.println("RANK = 0");
             for (int i=0; i<n; i++)
             {
                 System.out.print(a[i]+" ");
             }
              System.out.println("");
         }
         
         if (myRank==1)
         {
            
             MPI.COMM_WORLD.recv(a, n, MPI.INT, 0, 0);
              System.out.println("RANK = 1");
               for (int i=0; i<n; i++)
             {
                 System.out.print(a[i]+" ");
             }
               System.out.println("");
         }
         
          MPI.Finalize();
       }
     
     
     
     private static void TaskISend(String[] args) throws MPIException {
         
          MPI.Init(args);
         int myRank=MPI.COMM_WORLD.getRank();
         int n=Integer.parseInt(args[0]);
         IntBuffer a =  MPI.newIntBuffer(n);
         //int []a=new int[n];
         
         if (myRank==0)
         {   
             for (int i=0; i<n; i++)
             {
                 a.put(new Random().nextInt()%n);
             }
             
             MPI.COMM_WORLD.iSend(a, n, MPI.INT, 1, 0);
             System.out.println("RANK = 0");
             for (int i=0; i<n; i++)
             {
                 System.out.print(a.get(i)+" ");
             }
              System.out.println();
         }
         
         if (myRank==1)
         {
            
             MPI.COMM_WORLD.recv(a, n, MPI.INT, 0, 0);
              System.out.println("RANK = 1");
               for (int i=0; i<n; i++)
             {
                 System.out.print(a.get(i)+" ");
             }
               System.out.println();
         }
         
          MPI.Finalize();
       }
     
     
        private static void Task(String[] args) throws MPIException {
         
          MPI.Init(args);
          
          int myRank=MPI.COMM_WORLD.getRank();
          int mySize = MPI.COMM_WORLD.getSize();
          int n=1;
          IntBuffer a = MPI.newIntBuffer(n);
          a.put(0);
         
         while(mySize-1 == a.get(0))
         {
             if (myRank==0)
             {   
                 a.put(0,a.get(0)+1);
             }
             
             MPI.COMM_WORLD.send(a, n, MPI.INT, 1, 0);
             System.out.println("RANK = "+myRank);
             System.out.println("Sent "+ a.get(0));
             
             MPI.COMM_WORLD.iRecv(a, n, MPI.INT, 0, 0);
             System.out.println("RANK = "+myRank);
             System.out.println("Received "+ a.get(0));    
         
         }
         
          MPI.Finalize();
       }
     
     private static void SendNext(String[] args) throws MPIException
     {
        MPI.Init(args);
        
        int myRank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        
        int n = Integer.parseInt(args[0]);
        
        IntBuffer a = MPI.newIntBuffer(1);
        a.put(n);
        
        int next = (myRank +1) % size;
        int prev = (myRank -1 + size) % size;
        
        while(n != 1)
        {
            MPI.COMM_WORLD.iRecv(a, 1, MPI.INT, prev, 0);
        
            n = a.get(0);

            System.out.println("It's me " + myRank + " received " + n + " from " + prev);

            if(n == 1)
            {
                System.out.println(myRank + " winner!");
            }
            else if(n == -1)
            {
                MPI.Finalize();
                MPI.COMM_WORLD.iSend(a.put(0, -1), 1, MPI.INT, next, 0);
            }
            else
            {
                System.out.println("It's me " + myRank + " trying to send " + --n + " to " + next);
                MPI.COMM_WORLD.iSend(a.put(0, n), 1, MPI.INT, next, 0);
            }
        }
        
        System.out.println("\n" + myRank + " finished execution");
        MPI.Finalize();

        /*if(myRank == 0)
        {
            a.put(size + 1);

            while(n == 1)
            {
                System.out.println(myRank + " trying to send " + a.get(0) + " to " + next);
                System.out.println("It's me - the " + myRank + " sent a number "  + a.get(0)+ " to " + next + " processor");

                MPI.COMM_WORLD.iSend(a, n, MPI.INT, next, 0);
                MPI.COMM_WORLD.recv(a, n, MPI.INT, prev, 0);

                int num = a.get(0);

                System.out.println(myRank + " received " + num + " from " + prev);

                if(num == 1)
                {
                    System.out.println("I was first "+ myRank +" i received " + num +". Finish program");
                    n=0;
                }
                else
                {
                    a = MPI.newIntBuffer(1);
                    a.put(num-1);
                }   
            }
        }
        else
        {
            while(n==1)
            {
                MPI.COMM_WORLD.recv(a, n, MPI.INT, prev, 0);

                int num = a.get(0);
                System.out.println(myRank + " received " + num + " from " + prev);

                if(num == 1)
                {
                    System.out.println("I was first  "+ myRank +" i received " + a.get(0)+". Finish program");
                    n = 0;
                    a = MPI.newIntBuffer(1);
                    a.put(0);
                }
                else
                {
                    a = MPI.newIntBuffer(1);
                    a.put(num-1);

                    System.out.println("It's me - the " + myRank + " sent a number " + a.get(0) + " to " + next + " processor");
                }

                System.out.println(myRank + " trying to send " + a.get(0) + " to " + next);
                MPI.COMM_WORLD.iSend(a, n, MPI.INT, next, 0);
            }
            
        }*/
        
    }
    
    
     public static void main(String[] args) throws MPIException {
        //TaskSend(args);
        //TaskISend(args);
        //Task(args);
        SendNext(args);
    }
    
}
