package com.mathpar.matrix.LDU;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberR64;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.students.OLD.llp2.student.lyanok.mpimyrpoject;
import com.mathpar.students.OLD.stud2014.yakovleva.math;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import mpi.MPI;
import mpi.MPIException;

/**
 *
 * @author ridkeim
 */
public class TestLDU {
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, MPIException{
//        MPI.Init(args);
        test1(args);
//        MPI.Finalize();
    }
    public static void test1(String[] args) throws IOException {
//        Element r = new  NumberR64("32");
        Ring ring = new Ring("Q[]");
        long[][] ML = new long[][]{{0,3,1,2},{0,0,3,2},{1,1,1,2},{1,0,1,2}};
        MatrixS M = new MatrixS(ML, ring);

        LDU ldu0 = new LDU(M);
        ldu0.getLDU(M, ring.numberONE);
        

        
        int n;
//        public Element[] d;
//        public MatrixS L;
//        public MatrixS D;
//        public MatrixS U;
//        public MatrixS M;
//        public MatrixS W;
//        static Ring ring = Ring.ringZxyz;
        
        System.out.println("L = "+ldu0.L+"; D = "+ldu0.D+"; U = "+ldu0.U+
                          " M = "+ldu0.M+" W = "+ldu0.W+"d[] = "+Array.toString(ldu0.d));
        
    }
    
    public static void test(String[] args) throws MPIException, IOException{
        int size = MPI.COMM_WORLD.getSize();
        int rank = MPI.COMM_WORLD.getRank();
        int size1 = 100;
        if(args.length == 1){
                size1= Integer.valueOf(args[0]);
        }
        Random m = new Random();
        
        Integer[] data1 = new Integer[size1];
        
        int  count = size1;//m.nextInt(1000000);
        for (int j = 0; j< count; j++) {
            data1[j] = m.nextInt();
        }
        
//        MPI.COMM_WORLD.barrier();
        long time =  System.currentTimeMillis();
//        List<Integer> rp = ETDmpiUtils.gatherv(data1, 0);
//        time = System.currentTimeMillis()-time;
//        System.out.println("normal on proc "+rank+"= "+time);
//        rp = null;
//        System.gc();
        MPI.COMM_WORLD.barrier();
        time =  System.currentTimeMillis();
        List<Integer[]> gathervSeparated = ETDmpiUtils.gathervSeparated(data1, 0);
        time = System.currentTimeMillis()-time;
        System.out.println("separated on proc "+rank+"= "+time);
    }

    
}