/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.llp2.student.helloworldmpi;

import java.util.Random;

import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import mpi.*;
/**
 *
 * @author alexss
 */
/*
Запуск программы выполняется командой
 mpirun C java -cp /home/student/java_examples/helloworldmpi/build/classes/ -Djava.library.path=$LD_LIBRARY_PATH helloworld//!!!! MPI.Main



 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MPIException{
//        MPI mpi=new MPI();
//        //!!!! MPI.Init(args);
       Ring ring = new Ring("R[x,y,z]");
        Random rnd=new Random();
        int[] y1 = new int[]{10,70,12};

//        if(//!!!! MPI.COMM_WORLD.getRank()==0){
            Polynom s4 = new Polynom();

        Polynom p1 = s4.random(y1, rnd, ring).deleteZeroCoeff(ring);
        Polynom p2 = s4.random(y1, rnd, ring).deleteZeroCoeff(ring);
        Polynom[] mon=p1.last_sub_polynoms(3);
        for(int i=0;i<mon.length;i++){
        System.out.println("res1="+mon[i].toString(ring));}


        System.out.println("p1="+p1.toString(ring));
        System.out.println("p2="+p2.toString(ring));
        System.out.println("________________________________________________");

        Polynom[] newp2=p2.sub_monom_pol(mon, ring);
        for(int i=0;i<newp2.length;i++){
        System.out.println("res2="+newp2[i].toString(ring));}
//        //!!!! MPI.Finalize();
       // }


    }}


