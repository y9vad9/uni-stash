/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.students;

import com.mathpar.func.*;
import mpi.*;
import java.io.*;

import com.mathpar.matrix.*;
import com.mathpar.number.*;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author klochneva
 */
public class MatrixSPov {
    /** Convert F to MatrixS
    *  If F f has the form of VectorS of Vectors then f will be converted to
    *  MatrixS.
    * @param f -- the function which probably has the form of VectorS of Vectors
    * @return MatrixS if possible to convert and null if impossible to convert.
    */
   private static MatrixS toMatrixS(F f, Ring ring){
       if (f.name!=F.VECTORS) return null;
       int n=f.X.length;
       for (int j=0;j<n;j++){
         if (!(( f.X[j] instanceof F)&&(((F)f.X[j]).name==F.VECTORS)))return null;}
       Element[][] M=new Element[n][];
       for (int i=0;i<n;i++){   M[i]= ((F)(f.X[i])).X;}
       System.out.println("MMMMMMMMMMMM="+Array.toString(M));
       return new MatrixS(M, ring);
    }

    public static void main(String[] args)
            throws MPIException, FileNotFoundException, IOException {
      //  //!!!! MPI.Init(args);//инициализация MPI
        System.out.println("start");
        String ss = null;
      int myrank = 0; ////!!!! MPI.COMM_WORLD.getRank(); //определеине номера процессора
       // int n0 = Integer.parseInt(args[0]); //степень
     //   int np = //!!!! MPI.COMM_WORLD.Size();
        String n1 = "/home/student/f";  // new String(args[0]);//считывание названия файла "полный путь"
        System.out.println("n1= "+n1);
       // String n2 = new String(args[2]);
        Ring ring = new Ring("R64[x]");
        //Ring r = n2;
        InputStream f1 = new FileInputStream(n1);
        int size = f1.available();
        byte[] b = new byte[size-1 ];
        //for (int i = 0; i < size ; i++) {



            f1.read(b);
            ss = new String(b);
       // }
        System.out.println("ss= "+ss);
        f1.close();
        //System.out.println("00000000000000000000000");
        F f = new F(ss, ring);
        System.out.println("22222222"+f.X[0].toString(ring));
        System.out.println("11111111"+f.X[1].toString(ring));

      MatrixS M=  toMatrixS((F)f.X[0],ring);
        //F f11 = ((F)f.X[0]);
         // F f12 = ((F)f11.X[0]);
        // Polynom P = (Polynom) f12.X[0];

       // MatrixS M = (MatrixS) ff.X[0];
 System.out.println("len"+M.M.length);
        System.out.println("MMM "+M.toString(ring));
        int n = ((Polynom) f.X[1]).coeffs[0].intValue();
        if (n == 0) {
            System.out.println("   " + ring.numberONE);
            return;
        }
        if (n == 1) {
            System.out.println("   " + M);
            return;
        }
        Element one = ring.numberONE();
        System.out.println("ring = " + ring.toString());
       // System.out.println("one = " + one.toString(ring));
        Element min_one = ring.numberMINUS_ONE();
        //проверка числа на равенство 1 или -1
        if (M.isZero(ring) || M.isOne(ring)) {
          //  System.out.println("    " + M.toString(ring));

            return;
        }
        Element res = one;
        Element temp = M;
       // System.out.println("MMM ="+M.toString(ring));
        //System.out.println("MMM2 ="+M);
        n >>>= 1;
        if (myrank == 0) {

            while (n != 0) {

                temp = (temp.multiply(temp, ring));
                char[] message = new char[40];


                if ((n & 1) == 1) {
                    //!!!! MPI.COMM_WORLD.Send(message, 0, 40, //!!!! MPI.CHAR, 1, 3);
 // !!!!!!!!!!!!!!!!                   Transport.sendObject(temp, 1, 2);
                } else {
                    //!!!! MPI.COMM_WORLD.Send(message, 0, 40, //!!!! MPI.CHAR, 1, 1);
                }
                n >>>= 1;
            }
        } else {
            // if ((n0 & 1) == 1) { res = temp; }

            char[] message = new char[40];


            while (n != 0) {
                n >>>= 1;
                if ((n & 1) == 1) {
                    //!!!! MPI.COMM_WORLD.Recv(message, 0, 40, //!!!! MPI.CHAR, 1, 3);
  // !!!!!!!!!!!!!!!!                    Transport.recvObject(0, 2);
                } else {
                    //!!!! MPI.COMM_WORLD.Recv(message, 0, 40, //!!!! MPI.CHAR, 1, 1);
                }



                if ((n & 1) == 1) {
                    res = (res.multiply(temp, ring));
                   // System.out.println("otvet ="+res.toString(ring));
                }
                n >>>= 1;
            }

        }
//System.out.println(" Result =" + Array.toString(res));
        System.out.println(" Result =" + res.toString(ring));
        //!!!! MPI.Finalize();
    }
}
