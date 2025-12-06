package com.mathpar.students.OLD.stud2017.sidko;

import com.mathpar.parallel.utils.MPITransport;
import mpi.MPI;
import mpi.MPIException;

import java.io.IOException;

public class TestTransport {
    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
        //ExampleSendReceive(args);
        //ExampleSendReceiveArrayOfObjects(args);
        ExampleSendReceiveObjects(args);
        //ExampleBcastObject(args);


    }
    private static Bird[] ConvertToArrayBird(Object[] objects){

        Bird[] dogs = new Bird[objects.length];
        for (int i=0; i<objects.length; ++i){

            dogs[i] = (Bird)objects[i];

        }
        return dogs;
    }

    public static void ExampleSendReceiveObjects(String args[]) throws MPIException, IOException, ClassNotFoundException
    {

        MPI.Init(args);
        int myRank=MPI.COMM_WORLD.getRank();
        if(myRank == 3)
        {
            Bird []birds = {new Bird("Fast"),new Bird("Slow"),new Bird("Not flying"),new Bird("Really fast!!")};
            Transport.sendObjects(birds, 4, 1);
        }
        if(myRank == 4)
        {

            Bird []birds =new Bird[4];
            MPITransport.recvObjectArray(birds, 0,4, 3, 1);
            for(int i = 0; i<birds.length; i++)
                System.out.println(birds[i].getFly());
        }
        MPI.Finalize();
    }
}
