/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


/**
 *
 * @author ridkeim
 */
public class MapleReaderMatrix {
    static File f= new File("/home/ridkeim/enneper.map");
    public static MatrixS readMatrix() throws FileNotFoundException{
        FileInputStream st = new FileInputStream(f);
        Scanner a = new Scanner(st);
        ArrayList<NumberZ> row = new ArrayList();        
        a.next();
        a.next();
        String matrix = a.next();
        String[]  tokens = matrix.split("[\\[\\]]");
        ArrayList<String> rowsS = new ArrayList<>();
        for (String token : tokens) {
            if(token.length()>1) rowsS.add(token);                
        }
        Element[][] el  = new Element[rowsS.size()][];
        int[][] col = new int[rowsS.size()][];
        int iii = 0;
        for (String rowsS1 : rowsS) {
            String[] Elem = rowsS1.split("[,]");
            int t = 0;
            el[iii] = new Element[Elem.length];
            col[iii] = new int[Elem.length];
            for (String Elem1 : Elem) {
                el[iii][t] = new NumberZ(Elem1);
                col[iii][t] = t;
                t++;
            }
            iii++;
        }
        return new MatrixS(el, col);
        
//        ArrayList<String> rows = new ArrayList<>();
//        while(b.hasNext()){
//            rows.add(b.next());
//        }
        
        
//        int col = a.nextInt();
//        System.out.println(size);
//        
//        Element[][] ab = new Element[size][];
//        for (int i = 0; i < size; i++) {
//            
//        }
        
    };
    public static void main(String[] args) throws FileNotFoundException {
        MatrixS s = readMatrix();
        Long time = System.currentTimeMillis();
        MatrixS[] dec = ETD.ETDmodLDU(s);
        time = System.currentTimeMillis()-time;
        System.out.println("time="+time);
    }
    
}
