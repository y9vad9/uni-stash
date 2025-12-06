package com.mathpar.students.OLD.stud2017.sidko;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.mathpar.number.SubsetZ;
import mpi.Intracomm;
import  mpi.MPI;
import mpi.MPIException;

public class GroupTest {
        public static void main(String[] args) throws MPIException {
            MPI.Init(args);
            ArrayList s = new ArrayList();
            s.add(0);s.add(1);s.add(2);

            ArrayList s1 = new ArrayList();
            s1.add(3);s1.add(4);

            mpi.Group g = MPI.COMM_WORLD.getGroup().incl(new int[]{0,1,2});
            mpi.Group g2 = MPI.COMM_WORLD.getGroup().incl(new int[]{3,4});
            Intracomm COMM_NEW = MPI.COMM_WORLD.create(g);
            Intracomm COMM_NEW_1 = MPI.COMM_WORLD.create(g2);
            int myrank = MPI.COMM_WORLD.getRank();

           // System.out.println("g2.size = " + g2.getSize() + ": g2rank = " +g2.getRank());

            int n = Integer.parseInt(args[0]);
            double[] a = new double[n];
            if (myrank == 0||myrank == 3){
                for (int i = 0; i < n; i++) {
                    a[i] = new Random().nextDouble();
                }
                System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
            }


           // COMM_NEW.barrier();
            if(s.contains(myrank))
                COMM_NEW.bcast(a, a.length, MPI.DOUBLE, 0);

           // COMM_NEW_1.barrier();
            if(s1.contains(myrank))
             COMM_NEW_1.bcast(a, a.length, MPI.DOUBLE, 0);


            if (myrank != 0 && myrank != 3)
                System.out.println("myrank = " + myrank + ": a = " + Arrays.toString(a));
            MPI.Finalize();
        }
}
