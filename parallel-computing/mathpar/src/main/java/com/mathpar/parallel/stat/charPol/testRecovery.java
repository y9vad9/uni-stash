/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.charPol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author andy
 */
public class testRecovery {
    public static void main(String[] args) throws Exception {
        Ring ring  = new Ring("Zp32[x,y,z]");
        Ring r  = new Ring("Z[x,y,z]");
        long[] modules=new long[]{901,907};
        int [][] v_mod=new int[][]{{0,1},
                                   {0,1,2}};
//rem=-660940196
//rem=-520851642
//rem=-585764069
//rem=-1749730387
//rem=-1835969705
//rem=-195357045
//rem=-1559380069
//rem=639066311

        Polynom rem1 = new Polynom("-x^7", ring);
        Polynom rem2 = new Polynom("-x^7", ring);
        Polynom rem3 = new Polynom("-x^7", ring);
        Polynom rem4 = new Polynom("-x^7", ring);
        Polynom rem5 = new Polynom("-x^7", ring);
        Polynom rem6 = new Polynom("-x^7", ring);
        Polynom rem7 = new Polynom("-x^7", ring);
        Polynom rem8 = new Polynom("-x^7", ring);
        Polynom[] f = Polynom.recoveryOfLin(new Element[]{rem1,rem2,rem3,rem4,rem5,rem6,rem7,rem8}, 
                1,1,1998585857,ring);
        //System.out.println("f=  "+f[0].toString(ring));
         File file = new File("/home/oxana/oxcana");
        FileOutputStream fileOut = new FileOutputStream(file);
        
        for (int i = 1; i < 4097; i++) {
            //System.out.println("node"+i+":2"); 
            String s = "node"+i+":2" + "\n";
            fileOut.write(s.getBytes());
        }
        fileOut.close();
        
     
        
        
     }
    
    
}
