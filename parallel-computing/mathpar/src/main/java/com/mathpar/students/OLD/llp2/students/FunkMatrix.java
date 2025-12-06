/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.students;
import java.util.Random;
import com.mathpar.matrix.*;
import com.mathpar.number.*;

/**
 *
 * @author vitalik
 */

public class FunkMatrix {
    public static void main(String[] args){
        Ring ring = new Ring("Z[x]");
        int size = 6;
        Random rnd = new Random();
        MatrixS A  = new MatrixS(size, size, 10, new int[]{3, 3}, rnd, NumberZ.ONE, ring);
        MatrixS B = new MatrixS(size, size, 10, new int[]{3, 3}, rnd, NumberZ.ONE, ring);
        System.out.println("  A ="+A);
        Element [] R  = Koeff_Exp_Matrix(ring);
        Element [] m = new Element[6];
        for(int j =0;j<=5;j++){
            m[j]= new NumberZ(j+1);
        }
        MatrixS P = PartSum(A,B,m);
        int S[] = Proc_gr(size);
       // System.out.println("   exp (A)   = "+ R[0]);
        //System.out.println(" Proc_gr "+Proc_gr(size));
       // System.out.println(" PartSum   = "+P);
    }

    public static MatrixS PartSum(MatrixS A,MatrixS B, Element []s){
        MatrixS S = null;
        S = A.multiplyByNumber(s[0], Ring.ringR64xyzt);
        for(int i =1;i<s.length;i++){
            A=A.multiply(B, Ring.ringR64xyzt);
            S = S.add((MatrixS)(A.multiply(s[i], Ring.ringR64xyzt)), Ring.ringR64xyzt);
        }
        return S;
    }


   public static NumberR64 [] Koeff_Exp_Matrix(Ring ring){
       int e = 25 ;
       NumberR64 d ;
       NumberR64 g;
       NumberR64 one_z = NumberR64.ONE;
       NumberR64 [] f= new NumberR64 [e];
       NumberR64 z = new NumberR64(2);
       f[0]=one_z;
       System.out.println("f[0] = "+f[0].toString(ring));
       for(int j = 1;j<=e-1;j++){
          g = new NumberR64 (j) ;
          d =(NumberR64)g.factorial(ring);
          f[j]=one_z.divide(d);
          ring.setFLOATPOS(25);
           //System.out.println("f["+j+"]"+" = "+f[j].toString(ring));
       }
        return f;
        }


   public static int [] Proc_gr(int proc){
       int p =0;
       int m;
       NumberZ t = new NumberZ (2);
       int one = 1;
       NumberZ k = null;
       int r = 0;
       int[] G = new int[]{};
       for(int i = 1;i<= proc/2;i++){
          t = t.pow(i);
          System.out.println(" t = "+t);
          if(((t.compareTo(new NumberZ(proc)))==1)&((t.compareTo(new NumberZ(proc)))==0)){
              k = t.divide(new NumberZ(i));
               r = k.intValue();
               System.out.println("1111");
              break;
                          }
       }
       //int r = k.intValue();
       for(int j = 0;j<=r-1;j++){
           G = new int [r];
           System.out.println("G["+j+"] = "+G[j]);
       }
       return G;
   }



       public static MatrixS Sin_Matrix(MatrixS S,Ring ring){
          int e = 10 ;
          MatrixS M = null;
          Element r;
          MatrixS Res = null;

          for(int i =1;i<=e;i++){
          M= (MatrixS)S.pow(i, ring);
          NumberZ g = new NumberZ(i);
          g.factorial(ring);
          r = M.divide((Element)(g.factorial(ring)), ring);
          Res = Res.add((MatrixS)r, ring);

       }
        return null;
       }

   }

