/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2017.sidko;

import java.util.Arrays;
import java.util.Random;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;


/**
 *
 * @author alla
 */


//Example HelloWorld

// COMMAND
/*
openmpi/bin/mpirun -np 2 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/com.mathpar.students.ukma.Zhyrkova.Module2Part1.ZhyrkovaHelloWorldParallel
*/

class HelloWorldParallel {
    public static void main(String[] args)
            throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();
        System.out.println("Proc num " + myrank + " Hello World");
        MPI.Finalize();
    }
}

//Result

/*
Proc num 1 Hello World
Proc num 0 Hello World

*/




//Example 1.1

// COMMAND
/*
openmpi/bin/mpirun -np 2 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/Task1 12
*/

public class Tasks {
     private static void Task1(String[] args) throws MPIException {
         
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
                 System.out.println(a[i]);
             }
         }
         
         if (myRank==1)
         {
            
             MPI.COMM_WORLD.recv(a, n, MPI.INT, 0, 0);
              System.out.println("RANK = 1");
              
         }
         
          MPI.Finalize();
       }
    
     public static void main(String[] args) throws MPIException {
        Task1(args);
    }
}

//RESULT
/*

RANK = 0
-7
8
0
-8
8
5
-10
-7
-4
-9
-7
11
RANK = 1

*/


//Example 1.2
//COMMAND
/*
openmpi/bin/mpirun -np 2 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/Task2 10
*/
class Task2 {
    private static void Task2(String[] args) throws MPIException {
          MPI.Init(args);
          
          int myRank=MPI.COMM_WORLD.getRank();
          int n=Integer.parseInt(args[0]);
          int []a=new int[n];
          if (myRank==0){
              Random rnd=new Random();
              for (int i=0; i<n; i++)
              {
                  a[i]=rnd.nextInt()%n;
              }
              System.out.println("RANK = 0\n");
             for (int i=0; i<n; i++)
             {
                 System.out.println(a[i]);
             }
              MPI.COMM_WORLD.send(a, n, MPI.INT, 1, 0);
          } 
              if (myRank==1)
              {
                   System.out.println("RANK = 1\n");
                  MPI.COMM_WORLD.recv(a, n, MPI.INT, 0, 0);
                  System.out.println("First runk OK\n");
             
              }
              MPI.Finalize();
       }
        
     public static void main(String[] args) throws MPIException {
        Task2(args);
    }
}

//RESULT
/*

RANK = 1

RANK = 0

4
-4
1
-1
6
3
8
-7
0
-7
First runk OK

*/



//Example 1.3
//COMMAND
/*
openmpi/bin/mpirun -np 2 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/Task3 4 5
*/

class Task3 {
    private static void Task3(String[] args) throws MPIException {
          MPI.Init(args);
          
         int myRank=MPI.COMM_WORLD.getRank();
         int n=Integer.parseInt(args[0]);
         int []a=new int[n];
         int []b=new int[n];
         if (myRank==0)
         {
             Random rnd=new Random();
             for (int i=0; i<n; i++)
             {
                 a[i]=rnd.nextInt()%n;
             }
            MPI.COMM_WORLD.send(a, n, MPI.INT, 1, 0);
            MPI.COMM_WORLD.recv(b, n, MPI.INT, 1, 1);
            System.out.println("RANK = 0\n");
             for (int i=0; i<n; i++)
              {
                  System.out.println(a[i]);
              }
              
                             
         }
         if (myRank==1)
         {
             Random rnd=new Random();
             for (int i=0; i<n; i++)
             {
                 b[i]=rnd.nextInt()%n;
             }
             MPI.COMM_WORLD.send(b, n, MPI.INT, 0, 1);
             MPI.COMM_WORLD.recv(a, n, MPI.INT, 0, 0);
             System.out.println("RANK = 1\n");
              for (int i=0; i<n; i++)
              {
                  System.out.println(b[i]);
              }
         }
          MPI.Finalize();
     }
        
     public static void main(String[] args) throws MPIException {
        Task3(args);
    }
}

//RESULT

/*
RANK = 0

RANK = 1
0
2
2
2

3
0
-2
2

*/



//Example 1.4
//COMMAND

/*
openmpi/bin/mpirun -np 2 java -cp /home/alla/Documents/stemedu/stemedu/target/classes  com/mathpar/students/ukma17i41/sidko/com.mathpar.students.ukma.Zhyrkova.Module2Part1.ZhyrkovaTestCreateIntracomm 4
*/


class TestCreateIntracomm {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        mpi.Group g = MPI.COMM_WORLD.getGroup().incl(new int[]{0, 1});
        Intracomm COMM_NEW = MPI.COMM_WORLD.create(g);
        int myrank = COMM_NEW.getRank();
        int n = Integer.parseInt(args[0]);
        double[] a = new double[n];
        if (myrank == 0)
        {
            for (int i = 0; i < n; i++) {
                a[i] = new Random().nextDouble();
            }
            System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        }
        
        COMM_NEW.barrier();
        COMM_NEW.bcast(a, a.length, MPI.DOUBLE, 0);
        if (myrank != 0)
            System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
        MPI.Finalize();
}
}

//RESULT

/*
myrank = 0: a = [0.3578584027137055, 0.10104800842299733, 0.37912482168987804, 0.3716670618262895]
myrank = 1: a = [0.3578584027137055, 0.10104800842299733, 0.37912482168987804, 0.3716670618262895]

*/

