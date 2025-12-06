/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.dap.core;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispThread {

    private final static MpiLogger LOGGER = MpiLogger.getLogger(DispThread.class);

    Intracomm COMM;
    ArrayList<Amin> pine;
    long sleepTime;
   public static long executeTime;
    static Integer myRank;
    public CalcThread counter;
    Thread disp;
    Set<Integer> freeProcs;
    ArrayList<ArrayList<Drop>>[] terminal;
    Map<Integer, ArrayList<Drop>> reftoTerminal;
    Map<Integer, ArrayList<Drop>> aerodrome;
    ArrayList<Drop> ownTrack;
    private Element[] result;
    private Object[] receivedResult;
    Queue<Integer> waitingFromOthers;
    List<Integer> waitingOutput;
    Queue<Integer> approvedOutput;
    boolean recv;
    int waitfrom;
    int childsLevel;
    int totalLevel;
    static boolean flagOfMyDeparture = false;
    int mode;
    static int myLevel;
    static int myLevelH;
    static int sentLevel;
    static int trackLevel;
    int firstParent;
    static long usedMemory;
    long currentMemory;
    // Map<Integer, Long> timecount;
    long sleepSendTime = 0;
    long receiveTaskTime = 0;
    int [] listcount;
    boolean sendFreeToDaughter;
    //int flagOfDelay;
    static String checkline = "";
    //static long timegettask = 0;
    int flagOfDaughterLevel = 0;
    int reservedLevel = 0;
    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public DispThread(long sTime, String[] args,  Intracomm c, Ring ring) throws MPIException {
        pine = new ArrayList<>();
        sleepTime = sTime;
        executeTime = 0;
        COMM =c;
        myRank = COMM.getRank();
        freeProcs = new HashSet<>();
        terminal = new ArrayList[21];
        childsLevel = 20;
        totalLevel = 20;
        trackLevel = 20;
        mode = 0;
        receivedResult = null;
        myLevel = 20;
        myLevelH = 20;
        sentLevel = 20;
        usedMemory = 0;
        firstParent = -1;
        sendFreeToDaughter = false;
        waitingFromOthers = new LinkedList<>();
        recv = false;
        waitingOutput = new LinkedList<>();
        approvedOutput = new LinkedList<>();

        for (int i = 0; i < terminal.length; i++) {
            terminal[i] = new ArrayList<>();
        }

        aerodrome = new HashMap<>();
        ownTrack = new ArrayList<>();
        reftoTerminal = new ConcurrentHashMap<>();
        waitfrom = myRank;
        disp = Thread.currentThread();
        disp.setPriority(10);
        disp.setName("disp");
        counter = new CalcThread(pine, ownTrack, ring);
        listcount = new int[21];
        for (int i = 0; i < listcount.length ; i++) {
            listcount[i] = 0;
        }

        //result = new ArrayList<>();
    }

    private void rootWork(int[] nodes, int startType, int key, byte[] config, Element[] data) {
        for (int i = 1; i < nodes.length; i++) {
            freeProcs.add(nodes[i]);
        }
        Drop drop = Drop.doNewDrop(startType, key, config, -1, 0, 0, 0, data);

        //drop.setVars();

        counter.putDropInTrack(drop);
    }

    private void exit() {
        mode = -1;
        //counter.DoneThread();
    }

    private void receiveTask(int prank) throws MPIException, IOException {
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory before recv drop: " + bytesToMegabytes(currentMemory));
        Drop drop = (Drop) Transport.recvObject(prank, COMM,Transport.Tag.TASK);
       // LOGGER.info("revc drop type " + drop.type + " from = " + prank + " size = "+ ((MatrixS)drop.inData[0]).size+ " time = " + (System.currentTimeMillis()-executeTime));
        if(receiveTaskTime==0)
            receiveTaskTime = System.currentTimeMillis() - executeTime;

        listcount[drop.recNum]+=1;


        if (firstParent == -1 && drop.procId != myRank && myRank != 0)//&=???
            firstParent = drop.procId;

        addParent(drop);
        counter.putDropInTrack(drop);
        flagOfMyDeparture = false;
        waitingFromOthers.remove(prank);
        recv = false;
        waitfrom = myRank;

        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory after recv drop: " + bytesToMegabytes(currentMemory));
        LOGGER.info(" after receiveTask freeMemory = " + bytesToMegabytes(Runtime.getRuntime().freeMemory()));
    }

    private void receiveFreeProcs(int cnt, int daughter) throws MPIException {
       // LOGGER.trace("bef recv free");
        int[] freeProcsAr = Transport.receiveIntArray(cnt, daughter,COMM, Transport.Tag.FREE_PROC);

        for (int i = 0; i < freeProcsAr.length; i++) {
            freeProcs.add(freeProcsAr[i]);

        }
      /*  LOGGER.info("recv free procs from  " + daughter + " ," + Arrays.toString(freeProcsAr) + "free size = "
                + freeProcs.size() + " totallevel = " + totalLevel + " mylevel = " + myLevel +" childslevel = "
                + childsLevel +  "tracklevel = " + trackLevel);
*/
    }

    private void procLevel(int cnt, Integer daughter) throws MPIException {
        int[] tmr = Transport.receiveIntArray(cnt, daughter,COMM, Transport.Tag.PROC_STATE);

        ArrayList list = reftoTerminal.get(daughter);


        terminal[tmr[0]].add(list);

        if (!terminal[tmr[1]].remove(list))
            terminal[20].remove(list);


        if (tmr[0] < childsLevel) {
            childsLevel = tmr[0];
        }
        else if(tmr[1]==childsLevel)
        {
            for (int l = 0; l < terminal.length; l++) {
                if (terminal[l].size()!=0) {
                    childsLevel = l;
                    break;
                } else if (l == terminal.length - 1) {
                    childsLevel = 20;
                }
            }
        }
    }

    private void endProgramme(int cntProc, int[] nodes) throws MPIException {

        result = counter.result;
        int[] tmpAr = {24};
        for (int j = 1; j < cntProc; j++) {
            Transport.iSendIntArray(tmpAr, nodes[j], COMM,Transport.Tag.FINAL);
        }
        mode = -1;
    }

    private void receiveResult(Integer daughter) throws MPIException, IOException {
        currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        LOGGER.info("Used memory before recv result: " + bytesToMegabytes(currentMemory));

        if (receivedResult != null) {
            deleteDaughter((int) receivedResult[0], (int) receivedResult[1], (Drop) receivedResult[2]);
            receivedResult = null;
        }
        Object[] tmp = Transport.recvObjects(4, daughter,COMM, Transport.Tag.RESULT);

        LOGGER.info("1after receiveResult currentMemory = " + bytesToMegabytes( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        LOGGER.info(" 1after receiveResult freeMemory = " + bytesToMegabytes(Runtime.getRuntime().freeMemory()));

        LOGGER.info("received result from daughter = " + daughter + " time = " + (System.currentTimeMillis()-executeTime));
        int amin = (int) tmp[1];
        int drop = (int) tmp[2];
        int level = (int) tmp[3];
        Drop currentDrop = pine.get(amin).branch.get(drop);
        currentDrop.outData = (Element[]) tmp[0];
        counter.putDropInTrack(currentDrop);
       // LOGGER.trace("track size = " + ownTrack.size());

        if (deleteDaughter(daughter, level, currentDrop)) {
            receivedResult = null;
        } else {
            receivedResult = new Object[3];
            receivedResult[0] = daughter;
            receivedResult[1] = level;
            receivedResult[2] = currentDrop;
        }

        waitingFromOthers.remove(daughter);
        recv = false;
        waitfrom = myRank;

        LOGGER.info("2after receiveResult currentMemory = " + bytesToMegabytes( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        LOGGER.info(" 2after receiveResult freeMemory = " + bytesToMegabytes(Runtime.getRuntime().freeMemory()));
    }


    private void addDaugter(int daughtProcs, Drop drop) {
        if (reftoTerminal.containsKey(daughtProcs)) {
            reftoTerminal.get(daughtProcs).add(drop);
        } else {
            ArrayList<Drop> history = new ArrayList<>();
            history.add(drop);
            terminal[20].add(history);

            reftoTerminal.put(daughtProcs, history);
        }
    }

    private boolean deleteDaughter(Integer daughter, int level, Drop drop) {
        ArrayList<Drop> list = reftoTerminal.get(daughter);
        if (list.size() == 1) {
            if (terminal[level].remove(list) || terminal[20].remove(list)) {
                reftoTerminal.remove(daughter);

                if (childsLevel == level && terminal[level].size() == 0) {
                    for (int l = childsLevel + 1; l < terminal.length; l++) {
                        if (terminal[level].size()!=0) {
                            childsLevel = l;
                            break;
                        } else if (l == terminal.length - 1) {
                            childsLevel = 20;
                        }
                    }
                }
                return true;
            }
        } else {
            Drop resRem = null;
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).equals(drop)) {
                    resRem = list.remove(j);
                    if (resRem != null)
                        return true;
                    break;
                }
            }

        }
        return false;
    }

    public void deleteParent(int procId, int aminId, int dropId) {
        ArrayList<Drop> list = aerodrome.get(procId);
        for (int j = 0; j < list.size(); j++) {
            if (list.get(j).dropId == dropId && list.get(j).aminId == aminId) {
                list.set(j, null);
                list.remove(j);
            }
        }
        if (list.isEmpty()) {
            aerodrome.remove(procId);
        }
    }

    public void addParent(Drop drop) {
        if (!aerodrome.keySet().contains(drop.procId)) {
            ArrayList<Drop> am = new ArrayList<Drop>();
            am.add(drop);
            aerodrome.put(drop.procId, am);
        } else {
            aerodrome.get(drop.procId).add(drop);
        }
    }

    public static boolean isEmptyVokzal() {
        return myLevel == 20 && myLevelH == 20;
    }


    private synchronized boolean sendDrops(int destination) throws MPIException, IOException {
        if (isEmptyVokzal()) {
            return false;
        }
        Drop drop = counter.getTask(1);

        if (drop != null) {
            sendDrop(drop, destination);
            return true;
        }
        return false;
    }

    private void sendDrop(Drop drop, int destination) throws IOException, MPIException {
        synchronized (drop) {
            Drop curTask;
            drop.numberOfDaughterProc = destination;
            addDaugter(destination, drop);
            curTask = Drop.doNewDrop(drop.type, drop.key, drop.config, drop.aminId, drop.dropId, drop.procId, drop.recNum, drop.inData);
            LOGGER.info("send drop to = " + destination  + " size = " +((MatrixS)curTask.inData[0]).size + " time = " + (System.currentTimeMillis()-executeTime) );

            Transport.sendObject(curTask, destination,COMM, Transport.Tag.TASK);

            curTask = null;
            Runtime.getRuntime().gc();
        }
    }


    private boolean sendDropOrRequest(int procToSend) throws MPIException, IOException {
        int proc=procToSend;
        boolean sent = false;
            if (!isWaitingForReceiving()) {
                if (myRank == 0) {
                   sent = sendDrops(procToSend);
                } else if (myRank % 2 == 0) {
                    proc = freeProcs.stream().filter(p -> p % 2 == 1).findAny().orElse(-1);
                    if (proc != -1)
                     sent = sendDrops(proc);
                }
            }
            if (myRank != 0 && (proc==-1 ||myRank % 2 == 1)) {
               if(sendRequestToApproveSending(procToSend)&&freeProcs.contains(procToSend))
                   freeProcs.remove(procToSend);
            }
            return sent;
    }

    private void sendRequestToSendDrops() throws IOException, MPIException {


        if (myLevel <= childsLevel && myLevel > 0 && counter.vokzal[myLevel].size() != 0) {

            LOGGER.info("bef sendRequestToSendDrops currentMemory = " +bytesToMegabytes( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            LOGGER.info(" bef sendRequestToSendDrops freeMemory = " + bytesToMegabytes(Runtime.getRuntime().freeMemory()));
            freeProcs.remove(myRank);
            flagOfMyDeparture = false;

            if (freeProcs.size() != 0) {
                int vokzalSize = counter.vokzal[myLevel].size();

                if (vokzalSize == 0 || isEmptyVokzal()) {
                    return;
                }


                //LOGGER.info("vokzalSize  = " + vokzalSize);
               // LOGGER.info("free size  = " + freeProcs.size());
              //  LOGGER.info("counter.takenMyLowLevelDrops  = " + counter.takenMyLowLevelDrops);

                int chunk = (freeProcs.size()+1) / (counter.vokzal[myLevel].size() + counter.takenMyLowLevelDrops) - 1;
                int remchunk = (freeProcs.size()+1) % (counter.vokzal[myLevel].size() + counter.takenMyLowLevelDrops);

                int procToSend;
                if (freeProcs.size() > vokzalSize) {
                    for (int i = 0; i < freeProcs.size()&&counter.vokzal[myLevel].size()!=0; i++) {
                        procToSend = (int) freeProcs.toArray()[0];
                       if(sendDropOrRequest(procToSend)){
                           if(chunk>0) {
                               if (remchunk > 0) {
                                   if(sendFreeProc(procToSend, chunk+1)) remchunk--; else return;
                               }else {

                                   if(!sendFreeProc(procToSend, chunk)) return;
                               }
                           }else if(remchunk>0){
                               if(sendFreeProc(procToSend, 1)) remchunk--; else return;
                           }
                           else if (freeProcs.contains(procToSend))
                               freeProcs.remove(procToSend);
                       }
                    }
                } else {
                    int dropsnum = ((vokzalSize + counter.takenMyLowLevelDrops) / (freeProcs.size() + 1));
                    int remainder = ((vokzalSize + counter.takenMyLowLevelDrops) % (freeProcs.size() + 1)) - 1;
                    Object[] freeProcsArr = freeProcs.toArray();

                    for (int i = 0; i < freeProcsArr.length && counter.vokzal[myLevel].size() != 0; i++) {
                        procToSend = (int) freeProcsArr[i];

                        for (int j = 0; j < dropsnum; j++)
                            sendDropOrRequest(procToSend);

                        if (remainder > 0) {
                            sendDropOrRequest(procToSend);
                            remainder--;
                        }

                        if (freeProcs.contains(procToSend))
                            freeProcs.remove(procToSend);
                    }
                }
            }
            currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            LOGGER.info("after sendRequestToSendDrops currentMemory = " + bytesToMegabytes( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            LOGGER.info(" after sendRequestToSendDrops freeMemory = " + bytesToMegabytes(Runtime.getRuntime().freeMemory()));
        }


    }


    private boolean sendFreeProc(int destination, int num) throws MPIException {
        if (freeProcs.contains(destination)) {
            freeProcs.remove(destination);
        }
        int vokzalSize = counter.vokzal[myLevel].size();
        if(vokzalSize>freeProcs.size()) return false;
       // LOGGER.info("vokzsize = " + vokzalSize + ", freesize = " + freeProcs.size() + "takecalc = " + counter.takenMyLowLevelDrops);
        int chunk=num;
        if(num==-1)  chunk = vokzalSize == 0&&counter.takenMyLowLevelDrops==0 ? freeProcs.size() : (freeProcs.size()) / (vokzalSize + counter.takenMyLowLevelDrops);
        Iterator<Integer> freeProcIterator = freeProcs.iterator();
        if (chunk >= 1) {
            int[] daughtProcs = new int[chunk];

            for (int j = 0; j < chunk; j++) {
                daughtProcs[j] = freeProcIterator.next();
                freeProcIterator.remove();
            }
            LOGGER.info("send free with drop to "+ destination + " " + Arrays.toString(daughtProcs));

            Transport.iSendIntArray(daughtProcs, destination,COMM, Transport.Tag.FREE_PROC);
        }
        return true;
    }


    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    private void sendFreeToDaughter() throws MPIException {
        int k, i = 0;
        int level = childsLevel;
        if(flagOfDaughterLevel == 0){
            flagOfDaughterLevel = 1;
        } else{
            flagOfDaughterLevel = 0;
            for(int j = childsLevel; j<terminal.length;j++)
            {
                if(terminal[j].size()!= 0) {level = j;break;}
            }
        }
        int tsize = terminal[level].size();
        while (freeProcs.size() != 0 && i < tsize) {
            k = freeProcs.size() < tsize ? 1 : freeProcs.size() / tsize;
            int[] procsToSend = new int[k];
            Iterator<Integer> iterator = freeProcs.iterator();
            for (int j = 0; j < k; j++) {
                procsToSend[j] = iterator.next();
                iterator.remove();
            }
            Object key = getKey(reftoTerminal, terminal[level].get(i));
            if (key != null) {
                int destination = (int) key;
                Transport.iSendIntArray(procsToSend, destination,COMM, Transport.Tag.FREE_PROC);
               // LOGGER.info("send free to daughter " + destination + ", " + Arrays.toString(procsToSend)+ " time = " + (System.currentTimeMillis()-executeTime));
            }
            i++;
        }
    }

    public boolean isEmptyTerminal() {
        for (int i = 0; i < terminal.length; i++) {
            for (int j = 0; j < terminal[i].size(); j++) {
                if (terminal[i].get(j).size() != 0)
                    return false;
            }
        }
        return true;
    }

    private void sendFreeProcs() throws MPIException, IOException {
       /* if(myRank == 0){
       // String line = "sendFreeToDaughter = "+sendFreeToDaughter + " ,myLevel = "+ myLevel + " childsLevel = "
       //         +childsLevel + " , isEmptyVokzal() = "+ isEmptyVokzal() + " trackLevel = " + trackLevel
       //         + " firstParent = " +firstParent + " , freeProcs.size = "+ freeProcs.size() + " ,terminal[childsLevel].size() =  " + terminal[childsLevel].size();
        if(!checkline.equals(line)) {
            checkline = line;
            LOGGER.info(checkline);
            for(int i = 0; i<terminal.length;i++)
            {
                if(terminal[i].size()!= 0) LOGGER.info("i = " + i + "  " + terminal[i].size());
            }
        }
        }*/
        if ((!sendFreeToDaughter||myRank==0)&& ((myLevel - childsLevel) >= 2
                || (isEmptyVokzal() && trackLevel == 20)) && terminal[childsLevel].size() != 0) {
            doMeFree();
            if (freeProcs.size() != 0) {
                sendFreeToDaughter();
                sendFreeToDaughter = true;

            }
        } else if (firstParent != -1 && isEmptyVokzal()&& trackLevel == 20 && myRank != 0) {
            //if(flagOfDelay!=0) { flagOfDelay--; /*LOGGER.info("flagOfDelay = " + flagOfDelay + " mylevel = " + myLevel+"tracklevel = " + trackLevel);*/return;}
            doMeFree();
            if (freeProcs.size() != 0) {
                int[] free = freeProcs.stream().mapToInt(Integer::intValue).toArray();
               /* LOGGER.info("send free to parent to " + firstParent + " " + freeProcs.toString() + " mylevel = "
                        + myLevel + " totallevel = "+totalLevel+" childslevel = " + childsLevel +  "tracklevel = " + trackLevel );
                for(int i = 0; i<terminal.length;i++)
                {
                    if(terminal[i].size()!= 0) LOGGER.info("i = " + i + "  " + terminal[i].size());
                }*/

               // LOGGER.info("send free to parent to " + firstParent + " " + freeProcs.toString()+ " time = " + (System.currentTimeMillis()-executeTime));
                Transport.iSendIntArray(free, firstParent,COMM, Transport.Tag.FREE_PROC);
                freeProcs.clear();
                sendFreeToDaughter = false;
            }
        }
    }


    private void doMeFree() {
        if (counter.IamFree && !flagOfMyDeparture && isEmptyVokzal() && trackLevel == 20) {
            // LOGGER.warn("do free !!!" );
            freeProcs.add(myRank);
            DispThread.flagOfMyDeparture = true;
        }
    }

    private void sendLevel() throws MPIException {
        // LOGGER.info("Try to send level");
        if (isEmptyVokzal() && childsLevel == 20 && counter.ownTrack.size() == 0) {
            totalLevel = 20;
        } else {
            totalLevel = Math.min(myLevel, Math.min(childsLevel, trackLevel));
        }

        if (totalLevel != sentLevel) {
            int[] state = {totalLevel, sentLevel};

            for (int i = 0; i < aerodrome.size(); i++) {
                // LOGGER.trace("send my level totalLevel = " + totalLevel + "  sentLevel = " + sentLevel + "to "+aerodrome.keySet().toArray()[i]);
                int destination = (int) aerodrome.keySet().toArray()[i];
                Transport.iSendIntArray(state, destination,COMM, Transport.Tag.PROC_STATE);
            }
            sentLevel = totalLevel;
        }
    }

    private void tagAction(Status info) throws MPIException, IOException, ClassNotFoundException {
        //if(myRank==1)
        //LOGGER.info("in tag action time " + (System.currentTimeMillis()-executeTime));
        int tagIndex = info.getTag();
        Transport.Tag tag = Transport.Tag.values()[tagIndex];
        int cnt = info.getCount(MPI.INT);
        int prank = info.getSource();
        switch (tag) {
            case TASK:
                receiveTask(prank);
                break;

            case FREE_PROC:
                receiveFreeProcs(cnt, prank);
                break;

            case PROC_STATE:
                procLevel(cnt, prank);
                break;

            case RESULT:
                receiveResult(prank);
                //     LOGGER.info("after recv res");
                break;
            case FINAL: {
                Transport.receiveIntArray(cnt, prank,COMM, Transport.Tag.FINAL);
                //LOGGER.info("recv end signal");
                exit();
                break;
            }

            case REQUEST_TO_APPROVE:
                receiveRequestToApproveReceiving(prank);
                break;

            case APPROVAL:
                receiveApprovalForSending(prank);
                break;

            case CANCEL:
                receiveCancel(prank);
                break;
        }
    }

    private boolean sendRequestToApproveSending(int prank) throws MPIException {
        // Pair<Integer, HardWorkType> sendingType = new Pair<>(prank, type);

        if (waitingOutput.contains(prank))
            return false;

        int[] data = new int[]{myRank};

        Transport.iSendIntArray(data, prank,COMM, Transport.Tag.REQUEST_TO_APPROVE);
       // LOGGER.info("send request to " + prank);

        waitingOutput.add(prank);
       // LOGGER.info("sendRequestToApproveSending to " + prank);

        return true;
    }

    private void receiveRequestToApproveReceiving(int prank) throws MPIException {
        int[] data = Transport.receiveIntArray(1, prank,COMM, Transport.Tag.REQUEST_TO_APPROVE);
        waitingFromOthers.add(data[0]);
        //LOGGER.info(String.format("receive Request To Approve Receiving from " + prank));
    }

    private void approveReceiving() throws MPIException {
        if (waitingFromOthers.isEmpty()) {
            recv = false;
            return;
        }
        if (waitfrom == waitingFromOthers.element() || -1 * waitfrom == waitingFromOthers.element()) return;

        recv = true;
        int[] data = new int[]{myRank};
        int destination = waitingFromOthers.element();
        //int destination = waitingFromOthers.poll();

        waitfrom = destination;
        Transport.iSendIntArray(data, destination, COMM,Transport.Tag.APPROVAL);
       // LOGGER.info(String.format("approveReceiving to " + destination + "recv = " + recv + " waitfrom" + waitfrom));

    }

    private void cancelSending(int destination) throws MPIException {
        int[] data = new int[]{destination};
        Transport.iSendIntArray(data, destination, COMM,Transport.Tag.CANCEL);
       // LOGGER.trace("send cancel to " + destination);
        freeProcs.add(destination);
    }

    private void receiveApprovalForSending(int prank) throws MPIException {
        int[] data = Transport.receiveIntArray(1, prank,COMM, Transport.Tag.APPROVAL);
        approvedOutput.add(data[0]);
       // LOGGER.trace("recv approve from " + prank);
    }

    private void receiveCancel(int prank) throws MPIException {
        int[] resp = Transport.receiveIntArray(1, prank,COMM, Transport.Tag.CANCEL);

        waitingFromOthers.remove(prank);
        recv = false;

        waitfrom = myRank;
       // LOGGER.trace("recv cancel from " + prank);
    }


    private boolean isWaitingForReceiving() {
        return recv;
    }


    private boolean sendResultsToParent(int parent) throws MPIException {
        synchronized (counter.aerodromeResults) {
            Iterator<Drop> iterator = counter.aerodromeResults.iterator();
            while (iterator.hasNext()) {
                Drop dropRes = iterator.next();

                int parentAmin = dropRes.aminId;
                if (!dropRes.isItLeaf()) {
                    pine.set(dropRes.aminId, null);
                    parentAmin = dropRes.getNumbOfMyAmine();
                }
                if (dropRes.procId == parent) {
                    Object[] res = {dropRes.outData, parentAmin, dropRes.dropId, sentLevel};
                    //long time = System.currentTimeMillis();
                    Transport.sendObjects(res, dropRes.procId,COMM, Transport.Tag.RESULT);
                    LOGGER.info("send result to " + dropRes.procId + " time = " + (System.currentTimeMillis()-executeTime));
                    iterator.remove();
                    deleteParent(dropRes.procId, parentAmin, dropRes.dropId);

                    currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                  //  if (myRank==0)
                    LOGGER.info("sendRequestsForResultsSending currentMemory = " + bytesToMegabytes(currentMemory));
                    LOGGER.info("sendRequestsForResultsSending freeMemory = " +bytesToMegabytes( Runtime.getRuntime().freeMemory()));
                    if (currentMemory > usedMemory)
                        usedMemory = currentMemory;
                    return true;
                }
            }
        }
        return false;
    }


    private void sendRequestsForResultsSending() throws MPIException {
        synchronized (counter.aerodromeResults) {
            // Iterator<Drop> iterator = counter.aerodromeResults.iterator();
            for (int i = 0; i < counter.aerodromeResults.size(); i++) {
                Drop dropResult = counter.aerodromeResults.get(i);
                if (!isWaitingForReceiving())
                    if (myRank == 0 || (myRank % 2 == 0 && dropResult.procId % 2 == 1)) {
                        sendResultsToParent(dropResult.procId);
                    } else {
                        //LOGGER.info("send request for sending result to " + dropResult.procId + " time = " + (System.currentTimeMillis()-executeTime));
                        sendRequestToApproveSending(dropResult.procId);
                    }
            }
        }
        }



    private void resetFields() throws MPIException {
        pine.clear();
        aerodrome.clear();
        reftoTerminal.clear();
        myRank = COMM.getRank();
        freeProcs.clear();
        childsLevel = 20;
        totalLevel = 20;
        mode = 0;
        myLevel = 20;
        myLevelH = 20;
        sentLevel = 20;
        counter.flToExit = false;
        counter.finish = false;
        firstParent = -1;
        counter.myRank = COMM.getRank();
        counter.IamFree = false;
        receivedResult = null;
        currentMemory = 0;
        usedMemory = 0;
        counter.clear();
        counter.currentMemory = 0;
        for (int i = 0; i < terminal.length; i++) {
            if (terminal[i].size() != 0)
                terminal[i].clear();
        }
        waitingFromOthers.clear();

        recv = false;
        waitingOutput.clear();
        approvedOutput.clear();
        trackLevel = 20;
        //countCycleDisp = 0;
        counter.counterCycle = 0;
        waitfrom = myRank;
        sleepSendTime = 0;
        for (int i = 0; i < listcount.length ; i++) {
            listcount[i] = 0;
        }
        receiveTaskTime=0;
        sendFreeToDaughter = false;
       // flagOfDelay = 0;
    }

    private void clear() {
//        LOGGER.info(String.format("DONE. Pine=%d/%d aerodrom=%d track=%d terminal=%d refs=%d",
//                pine.stream().filter(Objects::nonNull).count(),
//                pine.size(),
//                aerodrome.size(),
//                ownTrack.size(),
//
//                reftoTerminal.size()
//        ));
        pine.clear();
        aerodrome.clear();
        reftoTerminal.clear();
        freeProcs.clear();
        counter.clear();
        for (int i = 0; i < terminal.length; i++) {
            if (terminal[i].size() != 0)
                terminal[i].clear();
        }
        System.gc();
    }

    public void execute(int startType, int key, String[] args, Element[] data, Ring ring) throws InterruptedException, ClassNotFoundException, MPIException, IOException {
        execute(startType, key, new byte[0], args, data, ring);
    }

    public void execute(int startType, int key, byte[] config, String[] args, Element[] data, Ring ring) throws mpi.MPIException, InterruptedException, IOException, ClassNotFoundException {
        long oldTime, currentTime;

        int all = COMM.getSize();
        int[] nodes = new int[all];
        for (int i = 0; i < all; i++) {
            nodes[i] = i;
        }

        resetFields();
        int cntProc = nodes.length;

        int rootNumb = nodes[0];

        executeTime = System.currentTimeMillis();
       // LOGGER.info("executeTime = " +executeTime);
        if (myRank == rootNumb) {
            rootWork(nodes, startType, key, config, data);
        }



        oldTime = executeTime - 2000;
        /*if (myRank == rootNumb) {
            LOGGER.info("DDP start with total nodes=" + cntProc);
            LOGGER.info("sleep time = " + sleepTime);
            LOGGER.info("**freeProcs.size = " + freeProcs.size());
        }*/

        //LOGGER.info(" start with terminal =" + reftoTerminal.size());
        long startTime, endTime, allTime = 0;
        while (mode != -1) {
            startTime = System.currentTimeMillis();
            if (myRank == 0 && counter.finish) {
                endProgramme(cntProc, nodes);
            }

            Status info = null;
           // if (myRank == 1) LOGGER.info("bef probe time = " + (System.currentTimeMillis()-executeTime));
            do {
                info = Transport.probeAny(COMM);

                if (info != null) {
                   // if (myRank == 1) LOGGER.info("probe time = " + (System.currentTimeMillis()-executeTime));
                    tagAction(info);
                   // LOGGER.trace(String.format(" recv = " + recv + " waitfrom = " + waitfrom));
                    //LOGGER.trace(String.format(" waitingFromOthers = " + waitingFromOthers.toString()));
                    //LOGGER.trace(String.format(" waitingOutput = " + waitingOutput));
                    //LOGGER.trace(String.format(" approvedOutput = " + approvedOutput.toString()));
                }
            } while (info != null);


            if (waitfrom < 0) {
                recv = true;
                waitfrom = -1 * waitfrom;
                LOGGER.trace(" waitfrom<0 " + waitfrom + recv);
            }

            makeRequestsForSending();
            doLiteJob();


            if (!isWaitingForReceiving()) {
               // if (myRank == 1) LOGGER.trace(String.format("!isWaitingForReceiving in"));
                doHardWork();
                approveReceiving();
                //LOGGER.trace(String.format("!isWaitingForReceiving out"));
            }

            //периодичность диспетчера

            endTime = System.currentTimeMillis();
            disp.sleep(sleepTime);
            allTime += endTime - startTime;
        }

        clear();

        executeTime = System.currentTimeMillis() - executeTime;
        if (myRank == rootNumb) {
            LOGGER.info("DAP done. executeTime = " + executeTime);
            //LOGGER.info("Number of cycle of dispatcher = " + countCycleDisp);
            // LOGGER.info("Number of cycle of counter = " + counter.counterCycle);

            LOGGER.info("sleepSendTime dispatcher = " +  sleepSendTime);
            LOGGER.info("Time of working dispatcher = " + (allTime - sleepSendTime));
            //LOGGER.info(getResult()[0]);
            LOGGER.info("Used memory = " + getUsedMemory());

        }

        String s = "";
        for (int i = 0; i < listcount.length; i++) {
            if(listcount[i]!=0)
                s = s + " rec "+i + " - " + listcount[i] + ", ";
        }
        LOGGER.info("myRank = "+ myRank+ "\n allTime of working dispatcher = " + allTime
                +"\n allTime of working counter = " + counter.calcWorkTime
                +"\n allTime of waiting counter = " + counter.calcWaitTime
                +"\n execute time = " + executeTime
                +"\n time before receiving first task = " + receiveTaskTime
                +"\n number of receiving task " + s);
    }

    private void doLiteJob() throws IOException, MPIException {
        if (!(approvedOutput.isEmpty() && waitingFromOthers.isEmpty() && waitingOutput.isEmpty())) {
            if (isWaitingForReceiving() && approvedOutput.stream().allMatch(a -> a > myRank)
                    && waitingOutput.stream().allMatch(a -> a > myRank) && waitingFromOthers.stream().allMatch(a -> a > myRank)) {
                recv = false;
                waitfrom = -1 * waitfrom;
            }
        }


        if (aerodrome.size() != 0) {
            sendLevel();
        }
        if(!counter.flagOfInputFunc)
            sendFreeProcs();
    }

    private void makeRequestsForSending() throws MPIException, IOException {
        if (freeProcs.size() != 0) {
            sendRequestToSendDrops();
        }

        if (counter.aerodromeResults.size() != 0) {
            sendRequestsForResultsSending();
        }
    }


    private void doHardWork() throws IOException, MPIException {
        Iterator<Integer> iterator = approvedOutput.iterator();

        boolean sent;
        while (iterator.hasNext()) {

            Integer destination = iterator.next();

            sent = sendResultsToParent(destination);

            if (!sent){

                sent = sendDrops(destination);
                if(sent) sendFreeProc(destination, -1);
            }

            if (!sent) cancelSending(destination);

            waitingOutput.remove(destination);
            iterator.remove();
        }


    }

    public Element[] getResult() {
        return result;
    }

    public long getUsedMemory() {
        return usedMemory / (1024 * 1024);
    }

}
