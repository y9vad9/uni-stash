package com.mathpar.parallel.ddp.MD.examples.adjoint;

import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
/**
 *
 * @author r1d1
 */
public class test {
    public static void main(String[] args) {
        int []a=new int[]{12};
        
    /*    MatrixS m1=new MatrixS(2, 2, 10000, new int[]{2}, new Random(), new Element(1),ring);
       // MatrixS m2=new MatrixS(2, 2, 10000, new int[]{2}, new Random(), new Element(1),ring);
      //  System.out.println(m1.toString(ring));
      //  System.out.println(m2.toString(ring));
      //  System.out.println(m1.multiply(m2, ring).toString(ring));
        
        MatrixS A=new MatrixS(new int[][]{{1,2,3,4},{1,6,7,2},{3,2,1,-3},{4,-3,2,1}}, ring);
        MatrixS B=new MatrixS(new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}}, ring);
        
        MatrixS []M=B.split();
        for (int i=0; i<4; i++){
            System.out.println("block "+i+M[i].toString(ring)+"\n");            
        }
        System.out.println("source matrix B: "+B.toString(ring));
        NumberZ numb=new NumberZ(1);
        
        System.out.println("Adjoint(B): "+B.adjoint(ring).toString(ring));*/
        int N=4;
        Ring ring=new Ring("Z[]");
        MatrixS m1=new MatrixS(N, N, 1000, new int[]{3}, new Random(), new Element(1),ring);
        System.out.println(m1.toString(ring));
        NumberZ numb=new NumberZ(1);
        long beg=System.currentTimeMillis();
        AdjMatrixS adj=new AdjMatrixS(m1, numb, ring);
        System.out.println(System.currentTimeMillis()-beg);
        /*System.out.println(adj.A.toString(ring));
        for (int i=0; i<adj.Ei.length; i++){
            System.out.print(adj.Ei[i]+" ");
        }
        System.out.println("");
        for (int i=0; i<adj.Ej.length; i++){
            System.out.print(adj.Ej[i]+" ");
        }
        System.out.println(S.adjoint(ring).toString(ring));*/
    }
}
