package com.mathpar.students.OLD.stud2017.sidko;
import java.nio.IntBuffer;
import  java.util.Random;
import mpi.*;

public class IsendIrecvTest {
        public static void main(String[] args) throws MPIException {

            MPI.Init(args);

            int myrank = MPI.COMM_WORLD.getRank();

            int np = MPI.COMM_WORLD.getSize();

            int n = Integer.parseInt(args[0]);
            IntBuffer b = MPI.newIntBuffer(n);
            MPI.COMM_WORLD.barrier();
            if (myrank == 0) {
                for (int i = 0; i < n; i++){
                    b.put(new Random().nextInt(10));
                }
                for (int i = 1; i < np; i++) {
                    MPI.COMM_WORLD.iSend(b, b.capacity(), MPI.INT, i, 3000);
                }
                System.out.println("proc num = " + myrank + " array is sent");
            } else {
                MPI.COMM_WORLD.recv(b, b.capacity(), MPI.INT, 0, 3000);
                System.out.println("proc num = " + myrank + " array is received");
            }

            MPI.Finalize();
        }


    }
