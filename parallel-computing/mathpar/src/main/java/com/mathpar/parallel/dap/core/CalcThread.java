/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templatesamin.outputData
 * and open the template in the editor.
 */
package com.mathpar.parallel.dap.core;

import com.mathpar.log.MpiLogger;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import mpi.MPI;
import mpi.MPIException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author alla
 * @param
 */
public class CalcThread implements Runnable {

    private final static MpiLogger LOGGER = MpiLogger.getLogger(CalcThread.class);

    public Thread thread;
    Ring ring;
    private ArrayList<Amin> pine;
    ArrayList<Drop>[] vokzal;
    volatile ArrayList<Drop> aerodromeResults;
    ArrayList<Drop> ownTrack;
    Element[] result;
    Drop currentDrop;
    static int myRank;
    volatile boolean finish;
    volatile boolean flToExit;
    volatile boolean IamFree;
    volatile int takenMyLowLevelDrops = 0;
    volatile boolean flagOfInputFunc = false;

    long currentMemory;
    static long counterCycle = 0;
    long calcWorkTime = 0;
    long calcWaitTime = 0;

    long c = 0;

    public CalcThread(ArrayList<Amin> p,
                      ArrayList<Drop> ownTr, Ring ring) throws MPIException {
        thread = new Thread(this, "CalcThread");
        thread.setPriority(1);
        flToExit = false;
        //isEmptyVokzal = true;
        finish = false;
        pine = p;
        ownTrack = ownTr;
        this.ring = ring;
        aerodromeResults = new ArrayList<>();
        vokzal = new ArrayList[21];
        myRank = MPI.COMM_WORLD.getRank();
        IamFree = false;
        for (int i = 0; i < vokzal.length; i++) {
            vokzal[i] = new ArrayList<Drop>();
        }

        thread.start();
    }

    public void DoneThread() {
        flToExit = true;
    }

    public void clear() {
        pine.clear();
        Arrays.stream(vokzal).forEach(ArrayList::clear);
        aerodromeResults.clear();
        ownTrack.clear();
    }


    public void putDropInTrack(Drop drop) {
        synchronized (ownTrack) {
            ownTrack.add(drop);
            if (drop.recNum < DispThread.trackLevel)
                DispThread.trackLevel = drop.recNum;
        }

    }
    public void putDropInVokzal(Drop drop) {
        //LOGGER.trace("put drop in vokzal rec " + drop.recNum);
        //LOGGER.info("put drop in vokzal rec = " + drop.recNum + ", id = " + drop.dropId + ", amin = " + drop.aminId);
        vokzal[drop.recNum].add(drop);

       // LOGGER.info("vokzal[drop.recNum] = " + vokzal[drop.recNum].get(vokzal[drop.recNum].size()-1));
       // LOGGER.info("mylevel " + DispThread.myLevel + " myLevelH = " + DispThread.myLevelH);
        if(drop.recNum == DispThread.myLevel) takenMyLowLevelDrops = 0;

        if (drop.recNum > DispThread.myLevelH || DispThread.myLevelH == 20) {
            DispThread.myLevelH = drop.recNum;
        }

        if(drop.recNum < DispThread.myLevel){
            takenMyLowLevelDrops = 0;
            DispThread.myLevel = drop.recNum;
        }

        if (DispThread.myLevel == 20 || DispThread.myLevel > DispThread.myLevelH) {
            DispThread.myLevel = DispThread.myLevelH;
        }
        //LOGGER.info("mylevel " + DispThread.myLevel + " myLevelH = " + DispThread.myLevelH);
    }

    public void writeResultsToAmin(Drop drop) {
       // LOGGER.info("writeResultsToAmin + ");
        int aminId = drop.aminId;
        int dropId = drop.dropId;


        Amin amin = pine.get(aminId);
        //LOGGER.info("pine size ="+ pine.size());
        Drop aminDrop = Drop.getDropObject(amin.type, amin.config);
        aminDrop.key = amin.key;

        for (int i = 0; i < aminDrop.arcs[dropId + 1].length; i += 3) {

            int number = aminDrop.arcs[dropId + 1][i];
            int from = aminDrop.arcs[dropId + 1][i + 1];
            int to = aminDrop.arcs[dropId + 1][i + 2];


         //   LOGGER.info("number of from = " + (dropId+1) + "recnum = " + drop.recNum);
         //   LOGGER.info("number of dep = " + number + " to " +to+ " from "+from);

            if (aminDrop.arcs[number].length != 0) {
                Drop dependantDrop = amin.branch.get(number - 1);
                synchronized (dependantDrop) {

                    dependantDrop.inData[to] = drop.outData[from];
                  //  LOGGER.info("dependantDrop type = " + dependantDrop.type+ " to " +to+ " from "+from);
                    if (dependantDrop.hasFullInputData()){
                    //    LOGGER.info("putDropInVokzal");
                        putDropInVokzal(dependantDrop);}
                }
            } else {
                //LOGGER.info("resultForOutFunction");
                amin.resultForOutFunction[to] = drop.outData[from];
                /*LOGGER.info("amin = " + amin);
                LOGGER.info("amin key = " + amin.key);
                LOGGER.info("drop key = " + drop.key);
                LOGGER.info("amin id = " + amin.aminIdInPine + " amine type = " + amin.type);*/
                if (amin.hasFullOutput()) {
                   // if(myRank==1) LOGGER.info("go to putresultstoaminoutput "  + (System.currentTimeMillis()-DispThread.executeTime));
                    //LOGGER.info("putResultsToAminOutput");
                    putResultsToAminOutput(amin);
                   // if(myRank==1) LOGGER.info("after putresultstoaminoutput "  + (System.currentTimeMillis()-DispThread.executeTime));
                }
            }
        }

    }


    private void writeResultsAfterInpFunc(Drop drop, Amin curAmin, Element[] resInputFunc) {

        for (int i = 0; i < drop.arcs[0].length; i += 3) {
            int numOfDependantDrop = drop.arcs[0][i];
            int from = drop.arcs[0][i + 1];
            int to = drop.arcs[0][i + 2];
            Drop dependantDrop = curAmin.branch.get(numOfDependantDrop - 1);

           // LOGGER.info("dependantDrop = " + dependantDrop.type+ " to " +to+ " from "+from);
          //  LOGGER.info("dependantDrop.inData =  " + dependantDrop.inData);
            dependantDrop.inData[to] = resInputFunc[from];
           // LOGGER.info("dependantDrop.inData 2!!!="    + dependantDrop.inData);
           // LOGGER.info("dependantDrop.length = " + dependantDrop.inputDataLength );
            if (dependantDrop.hasFullInputData()) {
             //   LOGGER.info("putDropInVokzal");

                putDropInVokzal(dependantDrop);
                }
            }
    }

    private void addToAerodromeResults(Drop dropRes) {
        //LOGGER.info("addToAerodromeResults");
        synchronized (aerodromeResults) {
            // LOGGER.warn("put amin num = " + amin.aminIdInPine);
            aerodromeResults.add(dropRes);
        }
    }

    private void putResultsToAminOutput(Amin amin) {
      // LOGGER.info("putResultsToAminOutput");
        Drop drop = (Drop.getDropObject(amin.type, amin.config));
        drop.key = amin.key;
        drop.inData = amin.inputData;
        //if(amin.parentAmin!=-1)
        //    drop.outData = pine.get(amin.parentAmin).branch.get(amin.parentDrop).outData;
       // if(myRank==1) LOGGER.info("bef outputfunc "  + (System.currentTimeMillis()-DispThread.executeTime));
        amin.outputData = drop.outputFunction(amin.resultForOutFunction, ring);
       // if(myRank==1) LOGGER.info("after outputfunc "  + (System.currentTimeMillis()-DispThread.executeTime));
        drop = null;

       /* if(((MatrixS)amin.outputData[0]).size==512&& amin.type==5) {
            LOGGER.info("time to calc AB = " + (System.currentTimeMillis()-c));
        }*/

        if (amin.parentAmin == -1 && myRank == 0 && Array.isEmptyArray(vokzal)) {
            //LOGGER.info("go finish");
            Drop resDrop = Drop.getDropObject(amin.type, amin.config);
            resDrop.outData = amin.outputData;
            finishWholeTask(resDrop);
            resDrop = null;
        } else if (amin.parentProc != myRank) {
            Drop resultAmine = Drop.doNewDrop(amin.type, amin.key, amin.config, amin.aminIdInPine, amin.parentDrop,
                    amin.parentProc, amin.recNumb, amin.inputData);
            resultAmine.outData = amin.outputData;
            resultAmine.setNumbOfMyAmine(amin.parentAmin);
            addToAerodromeResults(resultAmine);
        } else {
            Drop dr = pine.get(amin.parentAmin).branch.get(amin.parentDrop);
            dr.outData = amin.outputData;
            putDropInTrack(dr);

            int aminIndex = pine.indexOf(amin);
            if (aminIndex != -1)
                pine.set(aminIndex, null);

            if (currentMemory > DispThread.usedMemory)
                DispThread.usedMemory = currentMemory;
        }
        Runtime.getRuntime().gc();

    }

    private void finishWholeTask(Drop resDrop) {
        result = resDrop.recentCalc(ring);
        finish = true;
    }

    synchronized public Drop getTask(int thread) {
      // LOGGER.info("GET Task = "+thread);
        boolean empty = false;
     //   LOGGER.trace("mylevel " + DispThread.myLevel + " myLevelH = " + DispThread.myLevelH);

       // if(vokzal[DispThread.myLevelH].size() == 0) changeMyLevelH();
       // if(vokzal[DispThread.myLevel].size() == 0)changeMyLevel();
    //    LOGGER.trace("after mylevel " + DispThread.myLevel + " myLevelH = " + DispThread.myLevelH);
        if (DispThread.isEmptyVokzal()) {
      //     LOGGER.trace("Vokzal is empty");
            empty = true;
        }

        Drop drop = null;
        ArrayList<Drop> list;
        //boolean getFromTrack = false;

        if (thread == 0) {
            if (ownTrack.size() != 0) {
                // for (int i = 0; i < ownTrack.size(); i++) {
                drop = ownTrack.get(0);

                if(!drop.isItLeaf()&&Array.isEmpty(drop.outData)) {
                    flagOfInputFunc = true;
                    //if(myRank==8)LOGGER.info("set flagOfInputFunc = true");
                }

                deleteFromTrack(drop);
                // getFromTrack = true;
                //    break;
                // }
            } else if (/*!getFromTrack &&*/ !empty) {

             //   LOGGER.trace("after change DispThread.myLevelH = " + DispThread.myLevelH+ "empty vokzal " + Tools.isEmptyArray(vokzal));
                list = vokzal[DispThread.myLevelH];
                drop = list.get(0);
                list.remove(0);

                if(DispThread.myLevelH==DispThread.myLevel && drop!=null){
                    takenMyLowLevelDrops+=1;
                }
                if (vokzal[DispThread.myLevelH].size() == 0) {
                    changeMyLevelH();
                }
                if (drop.numberOfDaughterProc == -1) {
                    drop.numberOfDaughterProc = myRank;
                }

            }
        } else if (thread == 1 && !empty) {
            list = vokzal[DispThread.myLevel];
            if(list.size() == 0)  { changeMyLevel();  list = vokzal[DispThread.myLevel];}
            if(DispThread.isEmptyVokzal()) return null;
            drop = list.get(0);
            list.remove(0);
            if (vokzal[DispThread.myLevel].size() == 0) {
                changeMyLevel();
            }
        }
        return drop;
    }


    private void changeMyLevelH() {
        while (DispThread.myLevelH != 0 && DispThread.myLevelH >= DispThread.myLevel && vokzal[DispThread.myLevelH].size() == 0) {
            DispThread.myLevelH--;
        }
        if (vokzal[DispThread.myLevelH].size() == 0) {
            DispThread.myLevelH = 20;
            DispThread.myLevel = 20;
        }
    }

    private void changeMyLevel() {
        DispThread.myLevel++;
        while (DispThread.myLevelH >= DispThread.myLevel && vokzal[DispThread.myLevel].size() == 0) {
            DispThread.myLevel++;
        }
        if (DispThread.myLevel > DispThread.myLevelH) {
            DispThread.myLevel = 20;
            DispThread.myLevelH = 20;
        }
        //if(changeTrack) changeTrackLevel();

    }

    private void changeTrackLevel() {
        //LOGGER.trace("ownTrack.size" + ownTrack.size());
        synchronized (ownTrack) {
            DispThread.trackLevel = ownTrack.size() == 0 ? 20 : ownTrack.stream().min(Comparator.comparing(Drop::getRecNum)).get().recNum;
        }
    }

    private void deleteFromTrack(Drop drop) {
        ownTrack.remove(drop);
        if(drop.recNum == DispThread.trackLevel)
            changeTrackLevel();
    }


    public void inputDataToAmin() throws MPIException {
        //LOGGER.info("inputDataToAmin = "+ currentDrop.type);
        Amin curAmin = null;
        Element[] resInputFunc;

        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory bef creating amine: " + DispThread.bytesToMegabytes(currentMemory));
        curAmin = new Amin(currentDrop, pine.size(), myRank);
        pine.add(curAmin);
        currentDrop.setNumbOfMyAmine(curAmin.aminIdInPine);
        //System.arraycopy(currentDrop.inData, 0, curAmin.inputData, 0, curAmin.inputData.size());
        curAmin.inputData = currentDrop.inData;
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory after creating amine: " + DispThread.bytesToMegabytes(currentMemory));
       // if(myRank==1) LOGGER.info("go to inputfunc "  + (System.currentTimeMillis()-DispThread.executeTime));

        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory bef input function: " + DispThread.bytesToMegabytes(currentMemory));
        resInputFunc = currentDrop.inputFunction(curAmin.inputData, curAmin , ring);
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory after input function: " + DispThread.bytesToMegabytes(currentMemory));

      //  if(myRank==1) LOGGER.info("after inputfunc "  + (System.currentTimeMillis()-DispThread.executeTime));

      //  if(myRank==1) LOGGER.info("go to writeResultsAfterInpFunc " + (System.currentTimeMillis()-DispThread.executeTime));
        writeResultsAfterInpFunc(currentDrop, curAmin, resInputFunc);
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory after writeResultsAfterInpFunc: " + DispThread.bytesToMegabytes(currentMemory));
        flagOfInputFunc = false;
       // if(myRank==1) LOGGER.info("after writeResultsAfterInpFunc "  + (System.currentTimeMillis()-DispThread.executeTime));

        currentDrop.independentCalc(ring, curAmin);
       // LOGGER.info("vokzal size!! = "+ vokzal[DispThread.myLevel].size());
    }


    @Override
    public void run() {
        long calcWaitTimeStart, calcWaitTimeEnd, calcWorkTimeStart, calcWorkTimeEnd;
        calcWaitTimeStart = calcWaitTimeEnd = calcWorkTimeStart = calcWorkTimeEnd = System.currentTimeMillis();

        while (!flToExit) {
           // ++counterCycle;
          // if(myRank==0) LOGGER.info("in calc cycle owntrack size = "+ownTrack.size()+ " vokzalempty = " +  DispThread.isEmptyVokzal());
            if (ownTrack.size() == 0 && /*Tools.isEmptyArray(vokzal)*/DispThread.isEmptyVokzal()) {
                if (!IamFree) {
                    IamFree = true;
                }
                continue;
            } else {
                IamFree = false;
                calcWaitTimeEnd = System.currentTimeMillis();
                calcWaitTime+=calcWaitTimeEnd-calcWaitTimeStart;
            }
            try {
                calcWorkTimeStart = System.currentTimeMillis();
                ProcFunc();
                calcWorkTimeEnd = System.currentTimeMillis();
                calcWorkTime += calcWorkTimeEnd - calcWorkTimeStart;
                calcWaitTimeStart = System.currentTimeMillis();
            } catch (MPIException e) {
                e.printStackTrace();
            }
        }
    }
    private void ProcFunc() throws MPIException {

        //LOGGER.info("go to get task");
     //   if(myRank==1) LOGGER.info("go to get drop  "  + (System.currentTimeMillis()-DispThread.executeTime));
        currentDrop = getTask(0);
      //  if(myRank==1) LOGGER.info("after get drop "  + (System.currentTimeMillis()-DispThread.executeTime));
        if (currentDrop != null) {
         //   if(myRank==1) LOGGER.info("Get drop " + (System.currentTimeMillis()-DispThread.executeTime));
           // LOGGER.info("get drop id = " + currentDrop.dropId + "rec num = " + currentDrop.recNum + " amin  id = " + currentDrop.aminId);
            //LOGGER.info("currentdrop out data = " + Array.toString(currentDrop.outData));
            if (!Array.isEmpty(currentDrop.outData)) {
                //LOGGER.info("Drop result");

               // LOGGER.info("amin = " + currentDrop.aminId);

             //   if(myRank==1) LOGGER.info("go to write result to amine -- drop result " + (System.currentTimeMillis()-DispThread.executeTime));
                writeResultsToAmin(currentDrop);
               // if(myRank==1) LOGGER.info("after write result to amine "  + (System.currentTimeMillis()-DispThread.executeTime));

               /* LOGGER.info("isEmptyVokzal = "+ Array.isEmptyArray(vokzal));
                LOGGER.info("mylevel " + DispThread.myLevel + " myLevelH = " + DispThread.myLevelH);
                LOGGER.info("after writeResultsToAmin");*/

            } else {
                if (currentDrop.isItLeaf()) {
                   // LOGGER.info("Drop is leaf " + currentDrop.aminId + " id = "+ currentDrop.dropId);

                  //  if(myRank==1) LOGGER.info("drop is leaf go to seq calc " + (System.currentTimeMillis()-DispThread.executeTime));
                    currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    LOGGER.info("Used memory before sequentialCalc: " + DispThread.bytesToMegabytes(currentMemory));
                    currentDrop.sequentialCalc(ring);
                    currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    LOGGER.info("Used memory after sequentialCalc: " + DispThread.bytesToMegabytes(currentMemory));
                   // if(myRank==1) LOGGER.info("after seq calc " + (System.currentTimeMillis()-DispThread.executeTime));
                    if (currentDrop.aminId == -1 && myRank == 0) {
                        //LOGGER.info("go to finish whole task");
                        finishWholeTask(currentDrop);
                    } else if (currentDrop.procId == myRank) {

                        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        LOGGER.info("Used memory before writeResultsToAmin: " + DispThread.bytesToMegabytes(currentMemory));
                        writeResultsToAmin(currentDrop);
                        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        LOGGER.info("Used memory after writeResultsToAmin: " + DispThread.bytesToMegabytes(currentMemory));
                        //if(myRank==1) LOGGER.info("2after write result to amine " + (System.currentTimeMillis()-DispThread.executeTime));
                       //LOGGER.info("after writeResultsToAmin");

                    } else {
                       // LOGGER.info(" bef add aerodrome results");
                     //   if(myRank==1) LOGGER.info("go to addToAerodromeResults " + (System.currentTimeMillis()-DispThread.executeTime));
                        addToAerodromeResults(currentDrop);
                     //   if(myRank==1) LOGGER.info("after addToAerodromeResults " + (System.currentTimeMillis()-DispThread.executeTime));

                    }
                  //  LOGGER.trace("after drop is leaf vokzal empty = " + Tools.isEmptyArray(vokzal));
                } else {

                    inputDataToAmin();
                   // if(myRank==1) LOGGER.info("after inputDataToAmin " + (System.currentTimeMillis()-DispThread.executeTime));
                }
            }
        }


        return;
    }
}
