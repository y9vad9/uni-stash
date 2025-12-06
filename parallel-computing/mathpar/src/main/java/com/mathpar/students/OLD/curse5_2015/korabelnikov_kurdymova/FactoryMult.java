package com.mathpar.students.OLD.curse5_2015.korabelnikov_kurdymova;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.logging.Level;
import com.mathpar.matrix.MatrixS;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.sparse.*;
import mpi.MPI;
import mpi.MPIException;
import com.mathpar.number.*;
import com.mathpar.parallel.ddp.engine.*;
import com.mathpar.parallel.stat.FMD.MultFMatrix.*;
import com.mathpar.parallel.utils.MPITransport;


public class FactoryMult extends AbstractFactoryOfObjects {

    @Override
    public AbstractTask CreateTask(int type) {
        return new TaskMult();
    }

    @Override
    public void InitGraphs() {
        GraphMult g = new GraphMult();
        AddGraphOfTask(0, g);
    }
    
}
