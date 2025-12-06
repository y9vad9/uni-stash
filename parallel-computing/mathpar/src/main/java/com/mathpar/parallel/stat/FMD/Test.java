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
public class Test {
    public static void main(String[] args) throws Exception {
        int []tmpInt={4};
        Ring ring= new Ring("Z[x,y,z]");        
        
        String[] chMod = {"rm", "-R", "/tmp/mA/"};
        Process chmod = Runtime.getRuntime().exec(chMod);
        chmod.waitFor();
        String[] chMod1 = {"mkdir", "/tmp/mA/"};
        Process chmod1 = Runtime.getRuntime().exec(chMod);
        chmod1.waitFor();        
                        
        File f1 = new File("/tmp/mA/tmp1");              
        File f2 = new File("/tmp/mA/tmp2"); 
        File f3 = new File("/tmp/mA/tmp3"); 
        int nbits=9000,mSize=4,depth=1;
        FileMatrixD fm1=new FileMatrixD(f1,depth, mSize, mSize, nbits);
        FileMatrixD fm2=new FileMatrixD(f2,depth, mSize, mSize, nbits);
        FileMatrixD trueRes=fm1.multCU(fm2, f3);
        
        System.out.println(fm1.toMatrixD().toString());
        System.out.println("");
        System.out.println(fm2.toMatrixD().toString());
        System.out.println("");
        System.out.println(trueRes.toMatrixD().toString());
        
        ArrayList<NumberZ> mods=new ArrayList<NumberZ>();
        NumberZ lenComp=new NumberZ(nbits,new Random());
        NumberZ comp100=new NumberZ(100);
        NumberZ lenComparator=lenComp.multiply(lenComp).multiply(comp100);
        NumberZ product=new NumberZ(1);
        for (long i=1000000000; product.compareTo(lenComparator, ring)==-1; i++){
            NumberZ cur=new NumberZ(i);
            if (cur.isProbablePrime(1)){
                mods.add(cur);
                product=product.multiply(cur);
            }            
        }
        System.out.println(product.toString(ring));
        Newton.initRArray(mods);
        for (int i=0; i<mods.size(); i++){
        //    System.out.println(mods.get(i).toString(ring));
        }
        
        FileMatrixD []arA=new FileMatrixD[mods.size()];
        FileMatrixD []arB=new FileMatrixD[mods.size()];
        FileMatrixD []arC=new FileMatrixD[mods.size()];
        for (int i=0; i<mods.size(); i++){
            File curPathA=new File("/tmp/mA/modsA"+mods.get(i).toString(ring));
            arA[i]=fm1.copyByMod(curPathA, mods.get(i), ring);
            File curPathB=new File("/tmp/mA/modsB"+mods.get(i).toString(ring));
            arB[i]=fm2.copyByMod(curPathB, mods.get(i), ring);
            //System.out.println("modA for mod with i="+String.valueOf(i)+arA[i].toMatrixD().toString(ring));
            //System.out.println("modB for mod with i="+String.valueOf(i)+arB[i].toMatrixD().toString(ring));
            File curPathC=new File("/tmp/mA/modsC"+mods.get(i).toString(ring));
            arC[i]=arA[i].multCU(arB[i], curPathC, mods.get(i).longValue());
            //System.out.println("modC for mod with i="+String.valueOf(i)+arC[i].toMatrixD().toString(ring));
        }
        File modResF = new File("/tmp/mA/modRes"); 
        FileMatrixD modRes=new FileMatrixD(modResF,1);
        modRes.restoreByGarner(arC, mods);
        System.out.println("");
        System.out.println(modRes.toMatrixD().toString());
        if (!trueRes.toMatrixD().subtract(modRes.toMatrixD(), ring).isZero(ring)){
            System.out.println("FALSE");
        }
        else {
            System.out.println("TRUE");
        }
    }
}
