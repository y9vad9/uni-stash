/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.mathpar.students.ukma17i41.bosa.parallel.engine;
package com.mathpar.parallel.dap.core;

import com.mathpar.log.MpiLogger;
import com.mathpar.number.Array;
import com.mathpar.number.Element;

import java.util.ArrayList;

public class Amin {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(Amin.class);
    int parentProc;
    int parentAmin;
    int parentDrop;
    int type;
    byte[] config;
    int aminIdInPine;
    int recNumb;
    int key;
    ArrayList<Drop> branch;

    Element[] inputData;
    Element[] outputData;
    public Element[] resultForOutFunction;

    public Amin(Drop drop, int index, int myRank) {

       // Drop drop = Drop.getDropObject(type, config);

        this.type = drop.type;
        this.config = drop.config;
        this.key = drop.key;
        branch = drop.doAmin();
        setIndexToDrops(index, myRank, drop.recNum + 1);
        inputData = new Element[drop.inputDataLength];
        outputData = new Element[drop.outputDataLength];
        //LOGGER.info("resultForOutFunctionLength = " + drop.resultForOutFunctionLength);
        //LOGGER.info("aminId = " + drop.aminId);
        resultForOutFunction = new Element[drop.resultForOutFunctionLength];
        parentProc = drop.procId;
        parentAmin = drop.aminId;
        parentDrop = drop.dropId;
        recNumb = drop.recNum;
        aminIdInPine = index;

    }

    private void setIndexToDrops(int index, int myRank, int recNum) {
        for (int i = 0; i < branch.size(); i++) {
            branch.get(i).aminId = index;
            branch.get(i).dropId = i;
            branch.get(i).procId = myRank;
            branch.get(i).recNum = recNum;
            branch.get(i).setVars();
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (Drop i : branch) {
            str += type + " - "+i.dropId + ". "+Array.toString(i.outData) + "\n";
        }

        return str;
    }

    public boolean hasFullOutput(){

      /*  for (int j = 0; j < resultForOutFunction.length; j++) {
            LOGGER.info("resultForOutFunction[j] = "+ resultForOutFunction[j]);
        }*/

        for (int j = 0; j < resultForOutFunction.length; j++) {
            //LOGGER.info("resultForOutFunction[j] = "+ resultForOutFunction[j]);
            if (resultForOutFunction[j] == null) {
                return false;
            }
        }

        return true;
    }

    //public int GetMyDrop(Drop dp) {
    //    return branch.indexOf(dp);
    //}

}
