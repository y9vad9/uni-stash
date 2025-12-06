/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.stat.FMD;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.dense.FileMatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import mpi.MPI;

/**
 *
 * @author r1d1
 */

//mpirun -np 2 java -cp /home/r1d1/NetBeansProjects/mathpar/target/classes CalcMath.Test2
public class Test2 {
    public static void main(String[] args) throws Exception {       
        int []tmpInt={4};
        Ring ring= new Ring("Z[x,y,z]");        
        
        String[] chMod = {"rm", "-R", "/tmp/mA"};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", "/tmp/mA/"};
        Process chmod1 = Runtime.getRuntime().exec(chMod);
        chmod1.waitFor();        
                        
        File f1 = new File("/tmp/mA/tmp1");              
        File f2 = new File("/tmp/mA/tmp2");        
        File f3 = new File("/tmp/mA/tmp3");        
        int nbits=4,mSize=2,depth=1;        
        FileMatrixD fm1=new FileMatrixD(f1,depth, mSize, mSize, nbits);            
        FileMatrixD fm2=new FileMatrixD(f2,depth, mSize, mSize, nbits);            
        FileMatrixD res=fm1.subtract(fm2, f3);        
        
        System.out.println(fm1.toMatrixD().toString(ring));
        System.out.println(fm2.toMatrixD().toString(ring));
        System.out.println(res.toMatrixD().toString(ring));
        
    }
}
