/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.P.karatsubaMult;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.parallel.ddp.MD.examples.multiplyMatrix.FactoryMultiplyMatrix;
import com.mathpar.parallel.ddp.MD.examples.multiplyMatrix.TaskMultiplyMatrix;
import com.mathpar.parallel.ddp.engine.DispThread;
import com.mathpar.polynom.Polynom;
import java.util.Random;
import mpi.MPI;

/**
 *
 * @author r1d1
 */


        
public class Main {
    //           {      bracketPol        }   
    // h(x)=a0b0+[(a0+a1)(b0+b1)-a0b0-a1b1]x+a1b1x^2
    //            {   sumPol   }
    static Polynom karatsubaMult(Polynom a, Polynom b, Ring ring){
        if (Math.min(a.coeffs.length, b.coeffs.length)<=1 || !a.checkMainVars(b)){
            return a.multiply(b, ring);
        }        
        System.out.println("KAR INPUT: a="+a.toString(ring));
        System.out.println("KAR INPUT: b="+b.toString(ring));
        int a0BorderInd=Math.min(a.findLastMonomWithMainVar()+1,a.coeffs.length/2);
        int b0BorderInd=Math.min(b.findLastMonomWithMainVar()+1,b.coeffs.length/2);
        Polynom a1=a.subPolynom1(0, a0BorderInd);
        Polynom a0=a.subPolynom1(a0BorderInd, a.coeffs.length);        
        Polynom b1=b.subPolynom1(0, b0BorderInd);
        Polynom b0=b.subPolynom1(b0BorderInd, b.coeffs.length);
        System.out.println("a0="+a0.toString(ring));
        System.out.println("a1="+a1.toString(ring));
        System.out.println("b0="+b0.toString(ring));
        System.out.println("b1="+b1.toString(ring));
        int reduceValue=Math.min(a1.powers[a1.powers.length-1],b1.powers[b1.powers.length-1]);
        System.out.println("aBorderInd="+a0BorderInd+" bBorderInd="+b0BorderInd+" reduceValue="+reduceValue);
        a1.reduceMainVarPowers(reduceValue);
        b1.reduceMainVarPowers(reduceValue);
        System.out.println("a1 after reduce="+a1.toString(ring));
        System.out.println("b1 after reduce="+b1.toString(ring));
        Polynom a0b0=karatsubaMult(a0, b0, ring);
        System.out.println("a0b0="+a0b0.toString(ring));
        Polynom a1b1=karatsubaMult(a1, b1, ring);
        System.out.println("a1b1="+a1b1.toString(ring));
        Polynom sumPol=karatsubaMult(a0.add(a1, ring), b0.add(b1, ring), ring);
        System.out.println("sumPol="+sumPol.toString(ring));
        Polynom bracketPol=sumPol.subtract(a0b0, ring).subtract(a1b1, ring);
        System.out.println("bracketPol="+bracketPol.toString(ring));
        int []xMultPowers=new int[a.powers.length/a.coeffs.length];
        xMultPowers[xMultPowers.length-1]=reduceValue;
        Element []xCoeff=new Element[1];
        xCoeff[0]=ring.numberONE;
        int []x2MultPowers=new int[a.powers.length/a.coeffs.length];
        x2MultPowers[x2MultPowers.length-1]=reduceValue*2;        
        Polynom x=new Polynom(xMultPowers,xCoeff);
        Polynom x2=new Polynom(x2MultPowers,xCoeff);
        bracketPol=bracketPol.multiply(x, ring); //*x
        System.out.println("bracketPol after multon by x="+bracketPol.toString(ring));
        a1b1=a1b1.multiply(x2, ring); //*x^2
        System.out.println("a1b1 after multon by x^2="+a1b1.toString(ring));
        Polynom res=a0b0.add(bracketPol, ring).add(a1b1, ring);
        System.out.println("KAR OUTPUT: res="+res.toString(ring));
        return res;
    }
    
    public static void karatsubaTest(){
        Ring ring=new Ring("Z[x,y,z]");
        //Polynom p1=new Polynom(new int[]{30}, 100, 3, new Random(), ring.numberONE, ring);        
        //Polynom p2=new Polynom(new int[]{23}, 100, 3, new Random(), ring.numberONE, ring);       
        Polynom p1=new Polynom("-z^5+3z^2+4xyz+y^2x-yx-x^3+x^2+2x-100", ring);
        Polynom p2=new Polynom("z^7-3z^4+zx+x^3", ring);
        System.out.println(p1.toString(ring));
        System.out.println(p2.toString(ring));
        Polynom karRes=karatsubaMult(p1, p2, ring);
        Polynom trueRes=p1.multiply(p2, ring);
        System.out.println("karRes="+karRes.toString(ring));
        System.out.println("trueRes="+trueRes.toString(ring));
        Polynom test=trueRes.subtract(karRes, ring);
        System.out.println("subtractRes="+test.toString(ring));
    }
    
// mpirun -np 1 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes com.mathpar.parallel.ddp.P.karatsubaMult.Main 1 1
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        KaratsubaFactory f = new KaratsubaFactory();
        DispThread disp = new DispThread(0, f, 2, 10, args, null);
        KaratsubaT startTask = (KaratsubaT) disp.GetStartTask();
        int myrank = MPI.COMM_WORLD.getRank();
        if (myrank == 0) {
            System.out.println("a=" + startTask.a);
            System.out.println("b=" + startTask.b);
            System.out.println("ddpRes=" + startTask.c);
            Polynom trueRes = startTask.a.multiply(startTask.b, startTask.ring);
            System.out.println("trueRes=" + trueRes.toString(startTask.ring));
            Polynom sub = startTask.c.subtract(trueRes, startTask.ring);
            if (sub.isZero(startTask.ring)) {
                System.out.println("ok, ddpTime="+disp.GetExecuteTime());
            } else {
                System.out.println("error");
            }
        }
        MPI.Finalize();        
    }
}
