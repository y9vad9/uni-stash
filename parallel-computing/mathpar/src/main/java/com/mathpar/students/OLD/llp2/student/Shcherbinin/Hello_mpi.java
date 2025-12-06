package com.mathpar.students.OLD.llp2.student.Shcherbinin;
import java.io.UnsupportedEncodingException;
import mpi.*;
//  cd $HOME/NetBeansProjects/mpi_Test/build/classes && mpirun C java mpi_test.Hello_mpi && cd $HOME
class Hello_mpi{
    public static void main(String[] args) throws MPIException, UnsupportedEncodingException{
        String a = " олололол";
        String newString = new String(a.getBytes("KOI8-R"), "cp1251");
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.getRank();

        System.out.println(newString+" "+myrank);
        MPI.Finalize();
    }
}
