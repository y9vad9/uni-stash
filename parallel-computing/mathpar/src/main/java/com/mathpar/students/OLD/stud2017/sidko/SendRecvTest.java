package com.mathpar.students.OLD.stud2017.sidko;
import java.util.Random;
import mpi.*;
public class SendRecvTest {


        public static void main(String[] args)
                throws MPIException {

            MPI.Init(args);

            int myrank = MPI.COMM_WORLD.getRank();

            int np = MPI.COMM_WORLD.getSize();

            int n = Integer.parseInt(args[0]);
            double[] a = new double[n];

            //MPI.COMM_WORLD.barrier();

            if (myrank == 0) {
                for (int i = 0; i < n; i++) {
                    a[i] = (new Random()).nextDouble();
                    System.out.println("a[" + i + "]= " + a[i]);
                }

                for (int i = 1; i < np; i++) {
                    MPI.COMM_WORLD.send(a, n, MPI.DOUBLE, i, 3000);
                }
                System.out.println("Proc num " + myrank +
                        " array is sent" + "\n");
            } else {

                MPI.COMM_WORLD.recv(a, n, MPI.DOUBLE, 0, 3000);
                for (int i = 0; i < n; i++) {
                    System.out.println("a[" + i + "]= " + a[i]);
                }
                System.out.println("Proc num " + myrank +
                        " array is received" + "\n");
            }

            MPI.Finalize();
        }
    }


