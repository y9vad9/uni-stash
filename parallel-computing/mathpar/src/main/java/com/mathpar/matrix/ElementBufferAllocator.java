/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix;

import com.mathpar.number.Element;
import com.mathpar.number.Ring;

/**
 *
 * @author r1d1
 */
public class ElementBufferAllocator {
    public ElementBufferAllocator(int totalThreads, Ring ring){
        this.ring=ring;
        elementBuffer=new Element[totalThreads][];  
        usedColumns=new boolean[totalThreads][];
        usedColumnsStack=new int[totalThreads][];
    }
    
    public void checkBuffurSize(int ruquiredSize){        
        if (elementBuffer[0]==null || elementBuffer[0].length<ruquiredSize){
            int actualSize=0;
            if (elementBuffer[0]!=null){
                actualSize=elementBuffer[0].length;
            }
            int newSize=Math.max(actualSize*2, ruquiredSize);
            for (int i=0; i<elementBuffer.length; i++){
                elementBuffer[i]=new Element[newSize];                
                for (int j=0; j<newSize; j++){
                    elementBuffer[i][j]=ring.numberZERO;
                }
                usedColumns[i]=new boolean[newSize];
                usedColumnsStack[i]=new int[newSize];            
            }            
        }
    }
    
    public Element[] getElementBuffer(int threadNumb){
        return elementBuffer[threadNumb];
    }
    
    public boolean[] getBooleanBuffer(int threadNumb){
        return usedColumns[threadNumb];
    }
    
    public int[] getIntBuffer(int threadNumb){
        return usedColumnsStack[threadNumb];
    }
    
    public int getMaximalUsedThreads(){
        return elementBuffer.length;
    }
    public Ring getRing(){
        return ring;
    }
    
    public void setCounter(int value){        
        synchCounter=value;        
    }
    
    public synchronized int getNextCounterValue(){
        return synchCounter++;
    }        
    
    private Ring ring;
    private Element elementBuffer[][];
    private boolean usedColumns[][];
    private int usedColumnsStack[][];    
    private volatile int synchCounter;
     
}
