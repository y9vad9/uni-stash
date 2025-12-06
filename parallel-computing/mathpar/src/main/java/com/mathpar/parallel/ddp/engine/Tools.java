/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.ddp.engine;
import java.util.ArrayList;

/**
 *
 * @author yuri
 */
public class Tools {
    public static ArrayList<Integer> ArrayListCreator(int ar[]){
        ArrayList<Integer> answ=new ArrayList<Integer>();
        for (int i=0; i<ar.length; i++)
            answ.add(ar[i]);
        return answ;
    }
    public static ArrayList<ArrayList<Integer> > ArrayListCreator(int ar[][]){
        ArrayList<ArrayList<Integer> > answ=new ArrayList<ArrayList<Integer> >();
        for (int i=0; i<ar.length; i++){
            answ.add(new ArrayList<Integer>());
            for (int j=0; j<ar[i].length; j++){
                answ.get(i).add(ar[i][j]);
            }
        }
        return answ;
    }
}
