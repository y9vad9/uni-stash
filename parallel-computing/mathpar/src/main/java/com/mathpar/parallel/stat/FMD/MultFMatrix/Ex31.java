/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.FMD.MultFMatrix;

import com.mathpar.matrix.file.dense.FileMatrixL;
import com.mathpar.matrix.file.dm.MatrixL;
import com.mathpar.matrix.file.utils.BaseMatrixDir;
import mpi.MPI;

import java.util.Random;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.matrix.file.dense.FileMatrix;
import com.mathpar.matrix.file.sparse.SFileMatrix;
import com.mathpar.matrix.file.sparse.SFileMatrixL;
import com.mathpar.matrix.file.sparse.SFileMatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

/**
 * @author vladimir
 */
public class Ex31 {

    // public static Logger logger;
    public static void main(String[] args) {
        try {
            MPI.Init(args);
//            logger = new Logger(System.getProperty("user.home")+"/"+"logs/"+MPI.COMM_WORLD.Rank()+".log");
//            logger.setDisabled();
//            logger.debug("Привет");
            //  BaseMatrixDir.setMatrixDirectory("/gpfs/NETHOME/tamgu1/ribakovm/tarabrin/matrs/rank" + MPI.COMM_WORLD.Rank());
//BaseMatrixDir.setMatrixDirectory(System.getProperty("user.home") +"/matrs/matr"+ MPI.COMM_WORLD.Rank());

            BaseMatrixDir.setMatrixDirectory("/scratch/ribakovm/matrs/matr" + MPI.COMM_WORLD.getRank());
          //  System.out.println(System.getProperty("user.home") + "/matrs/matr" + MPI.COMM_WORLD.Rank());

            int rank = MPI.COMM_WORLD.getRank();
            System.out.println("SIZE " + MPI.COMM_WORLD.getSize());
            SFileMatrixL A1 = null;
            SFileMatrixL B1 = null;
            Element el = null;
            int size = 1000;
            int depth = 1;
            while (size <= 2000) {
                try {
                    BaseMatrixDir.clearMatrixDir();
                    size += 1000;
                    if (rank == 0) {

                        long t1 = System.currentTimeMillis();
                        int mdens = 50;
                        String r_str = "Z[x,y,z]";
                        String rp_str = "Zp32[x,y,z]";
                        int pdens = 100;
                        int vars = 1;
                        int cbits = 7;
                        int deg = 1;
                        int deg1[] = {1};
                        int rnd = 2;
                        Ring r = new Ring(r_str);
                        int[] polArr = new int[vars + 2];
                        for (int i = 0; i < vars; i++) {
                            polArr[i] = deg1[i];
                        }
                        polArr[polArr.length - 2] = pdens;
                        polArr[polArr.length - 1] = cbits;

                        //входная матрица
//                MatrixS a1 = new MatrixS(size, size, mdens, polArr, new Random(10), r.numberONE(), r);
//                MatrixS a2 = new MatrixS(size, size, mdens, polArr, new Random(3), r.numberONE(), r);
//                MatrixD a = new MatrixD(a1, r);
//                MatrixL l = new MatrixL(new long[][]{
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
//                    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16}
//                });
                        MatrixL l = new MatrixL(new long[][]{{2}
                        });
                        // A1 = new SFileMatrixS(1, BaseMatrixDir.getRandomDir(), a1, r.numberZERO, r);
                        A1 = new SFileMatrixL(depth, size, size, 100, new Random(10000), (long) 8000);
//                logger.debug("Исходная матрциа ");
//                logger.line();
                        //  logger.printMatrix(A1);
//                logger.debug("-----------------------");
                        // B1 = new SFileMatrixS(1, BaseMatrixDir.getRandomDir(), a2, r.numberZERO, r);
                        B1 = new SFileMatrixL(depth, size, size, 100, new Random(10000), (long) 8000);
                        el = new Polynom(new NumberR64(2));
                    }
                    MPI.COMM_WORLD.barrier();

                    Multiplay<SFileMatrixL> multiplay3 = new Multiplay<SFileMatrixL>(SFileMatrixL.class);

                    SFileMatrixL C5 = multiplay3.mult(A1, B1, 0, Ring.ringR64xyzt);
                    long t5 = System.currentTimeMillis();
                    SFileMatrixL res = multiplay3.assemblage(C5, depth, 0);
                    long t52 = System.currentTimeMillis();

                    System.out.println(String.format("I`m %s node: assemb matrs %s x %s on %s nodes : %s ms", rank, size, size, depth, (t52 - t5)));
//            if (rank == 0) {
//                System.out.println("A");
//                for(Element[] arr: A1.toMatrixS().M){
//                    System.out.println(Array.toString(arr, Ring.ringR64xyzt));
//                }
//
//                System.out.println("B");
//                for(Element[] arr: B1.toMatrixS().M){
//                    System.out.println(Array.toString(arr, Ring.ringR64xyzt));
//                }
////
//                System.out.println("C");
//                for(Element[] arr:   res.toMatrixS().M){
//                    System.out.println(Array.toString(arr, Ring.ringR64xyzt));
//                }
//
//            }
                    MPI.COMM_WORLD.barrier();
                    if (rank == 0) {
                        System.out.println("=====================================================================================");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            MPI.Finalize();
        } catch (Exception e) {
            //logger.debug("Чтото пошло не так.....");
//            logger.close();
//            loggetarabrinr.error("Чтото пошло не так.....", e);
            e.printStackTrace();
        }
    }
}
