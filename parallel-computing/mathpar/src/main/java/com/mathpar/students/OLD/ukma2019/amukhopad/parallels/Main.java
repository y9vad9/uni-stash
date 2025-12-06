package com.mathpar.students.OLD.ukma2019.amukhopad.parallels;

import mpi.MPI;
import mpi.MPIException;

public class Main {

  public static void main(String[] args) throws MPIException {
    MPI.Init(args);

    try {
      Class.forName(args[0])
          .getMethod("main", String[].class)
          .invoke(null, (Object) args);
    } catch (ReflectiveOperationException e) {
      e.printStackTrace();
    }

    MPI.Finalize();
  }
}
