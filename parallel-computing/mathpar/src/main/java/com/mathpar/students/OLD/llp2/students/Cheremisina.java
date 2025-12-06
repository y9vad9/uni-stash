/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.students;

import com.mathpar.matrix.MatrixS;
import com.mathpar.func.*;
import mpi.*;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.number.*;

/**
 *
 * @author cheremisina
 * mpirun C java -cp /home/student/parca/build/web/WEB-INF/classes/ -Djava.library.path=$LD_LIBRARY_PATH llp2.student Cheremisina
 */
public class Cheremisina {

    public static void main(String args[]) throws MPIException {
      /*  //        //!!!! MPI.Init(args);
        //       int myrank = //!!!! MPI.COMM_WORLD.getRank();
        //       int np = //!!!! MPI.COMM_WORLD.Size();
        Ring ring = new Ring("Q[x]");
        Random rnd = new Random();
        Fraction one =  new Fraction(NumberZ.ONE, ring);
        int[] randomType = new int[]{3};
        MatrixS a = new MatrixS(2, 2, 100, randomType, rnd, one, ring);
        //MatrixS b = new MatrixS(3, 4, 90, randomType, rnd, one, ring);
        System.out.println("A =" + a+  a.M[0][0].numbElementType());
        MatrixS c = a.GenInvers(ring);
        System.out.println("C=" + c.multiply(a, ring).cancel(ring));
        //        //!!!! MPI.Finalize();*/


Ring ring = new Ring ("Z[x,y]");
//Ring ring = Ring.ringR64xyzt;
        int[][] A1 = new int[][]{{0, 0, 1},{4, 2, 1}, {16, 4, 1}, {100, 10, 1}};
        long[]  B1 = new long[] {1, 0, 1, 2};
        //Polynom[] abc = new Polynom[]{new Polynom("a",ring), new Polynom("b",ring), new Polynom("c",ring)};
        MatrixS A = new MatrixS (A1, ring).GenInvers(ring);
        VectorS B = new VectorS (B1,ring);
        //System.out.println(A.toString(ring));
        System.out.println("a=" + A );
        VectorS x=A.multiplyByColumn(B, ring);
        System.out.println("koord=" + x.toString(ring));
        ring= new Ring("R64[x,y]");
        System.out.println("koord=" + x.toNewRing(Ring.R64, ring).toString(ring));



//F f = new F("a(x^2)+bx+c", r);

/* Ring ring = new Ring ("Z[x,y]");
//Ring ring = Ring.ringR64xyzt;
 int[][] A1 = new int[][]{{0, 0, 1},{4, 2, 1}, {16, 4, 1}, {100, 10, 1}};
 long[]  B1 = new long[] {1, 0, 1, 2};
 //Polynom[] abc = new Polynom[]{new Polynom("a",ring), new Polynom("b",ring), new Polynom("c",ring)};
 MatrixS A = new MatrixS (A1, r).GenInvers(r);
 VectorS B = new VectorS (B1,ring);
 //System.out.println(A.toString(ring));
 System.out.println("A=" + A );
  //VectorS x=null;
  //A.multiplyByColumn(B, r);
  //System.out.println("M="+A.getSubMatrix(0, 0, 0, 0).toString(r));
 VectorS a=(A.getSubMatrix(0, 0, 0, A.colNumb)).multiplyByColumn(B, r);
 int qq = a.V.length; int[] Aq = new int[qq];
 for ( int i =0; i< qq ; i++ ) Aq[i]=qq-i-1;
 Polynom ap = new Polynom (Aq,a.V);
 VectorS b=(A.getSubMatrix(1, 1, 0, A.colNumb)).multiplyByColumn(B, r);
 VectorS c=(A.getSubMatrix(2, 2, 0, A.colNumb)).multiplyByColumn(B, r);
 //System.out.println("a=" + a.toString(r));
 // System.out.println("b2=" + x[1].toString(r));
 //System.out.println("b3=" + x[2].toString(r));
 ring= new Ring("R64[x,y]");
 System.out.println("a=" + a.toNewRing(Ring.R64, ring).toString(r));
 System.out.println("b=" + b.toNewRing(Ring.R64, ring).toString(r));
 System.out.println("c=" + c.toNewRing(Ring.R64, ring).toString(r));
 //  F f = new F(x.toNewRing(Ring.R64, r).toString(r), r);
 Element[] cc = new Element [A1[0].length];*/



        /* Polynom pp=new Polynom(new int[]{2,1,0}, x.V);
         F ff=new F(F.ID,new Element[]{pp});
         ff.showGraf();
         F f = new F("\\Plot(pp,\\VectorS(-10,10,-10,10,-10,10))",ring);
         System.out.println("name="+((F)f.X[0]).name);
           //f.showGraf(true, 1, ""+1, "x", "y", "");*/



//        Polynom xyz = new Polynom("x+y+z",ring);
//        int[] a = new int[]{0, 4, 16, 49};
//        int[] b = new int[]{0, 2, 4, 7};
//        int[] c = new int[]{1, 1, 1, 1};
//        VectorS C = new VectorS();

//    System.out.println("abc=" + abc[0]);
//       MatrixS AA = A(ring);
//       MatrixS AAB = AA.multiply(B, ring);
//       VectorS C = AAB.oneSysSolvForFraction(ring);
//       System.out.println("u="+ u);


    }
}
