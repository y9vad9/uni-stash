/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.llp2.students;

import java.util.Random;
import mpi.*;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author averina
 */
public class TestAverina {
public static void main(String[] args) throws MPIException {
        MPI mpi=new MPI();
         MPI.Init(args);
        long time1=System.currentTimeMillis();

        int myrank = MPI.COMM_WORLD.getRank() ;
        int quant= Integer.parseInt(args[0]);//минимальный квант на который мы должны разбить полиномы
        Ring ring=Ring.ringR64xyzt;
        Random rnd=new Random();
        Polynom s4 = new Polynom();

       Polynom s1=new Polynom("0",ring);
       Polynom s2=new Polynom("0",ring);

        //объявляем полиномы
        //Polynom s1 = new Polynom("3*x + 2*x*x + 4*x*x*y + x*y + 5*y*y + 8*y*y*x + 2*x*x*x*x  +56*x*y*z + z + x + y + 5*x*z + 6*z*z + 8*z*y*y + 6*z*z*x*y +3*x*y*z + 2*x*x*y*z + 4*x*x*y*y*z + x*y*y*z + 5*y*y*y*z + 8*y*y*x*y*z + 2*x*x*x*x*y*z  + 56*x*y*z*y*z + z*y*z + x*y*z + y*y*z + 5*x*z*y*z + 6*z*z*y*z + 8*z*y*y*y*z + 6*z*z*x*y*y*z +14*z*z*z*z*z + 56*y*y*z*x*z + 44*x*z*z*y*y*y ", ring) ;
        //Polynom s2 = new Polynom("3*x + 2*x*x + 4*x*x*y + x*y + 5*y*y + 8*y*y*x + 2*x*x*x*x  +56*x*y*z + z + x + y + 5*x*z + 6*z*z + 8*z*y*y + 6*z*z*x*y +3*x*y*z + 2*x*x*y*z + 4*x*x*y*y*z + x*y*y*z + 5*y*y*y*z + 8*y*y*x*y*z + 2*x*x*x*x*y*z  + 56*x*y*z*y*z + z*y*z + x*y*z + y*y*z + 5*x*z*y*z + 6*z*z*y*z + 8*z*y*y*y*z + 6*z*z*x*y*y*z +14*z*z*z*z*z + 56*y*y*z*x*z + 44*x*z*z*y*y*y ", ring) ;

        int[] y1 = new int[]{6500,100,12};
        int[] y2 = new int[]{6500,100,12};
        if(myrank==0){
            s1 = s4.random(y1, rnd, ring).deleteZeroCoeff(ring);
            s2 = s4.random(y2, rnd, ring).deleteZeroCoeff(ring);
            System.out.println("myrank        =     "+ myrank);
            //System.out.println("s1="+s1.toString(ring));
             byte[] temp= null; //!!!!MPI.COMM_WORLD.Object_Serialize(new Object[]{s1},0,1,MPI.OBJECT);
             System.out.println("myrank after        =     "+ myrank);
             System.out.println("pol size="+temp.length);
         }

        MultPol gg = new MultPol();
        Polynom[] res=new Polynom[1];
        Polynom ss=s1.multiply(s2, ring);
        gg.MultPolynomPar_all(s1, s2, quant, mpi, res);

        if(myrank==0){
        long time2=System.currentTimeMillis()-time1;
        System.out.println("time= "+time2+" ms");
        //System.out.println("res="+res[0]);
        System.out.println("ss-res="+ss.subtract(res[0], ring));
        System.out.println("_______________________________________________________");}



    //!!!! MPI.Finalize();
    }
}
