package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;

public class MemoryManager {

    private static MpiLogger LOGGER = MpiLogger.getLogger(MemoryManager.class);

    public static void check(){
        check("");
    }

    public static String check(String prefix){
        long max = Runtime.getRuntime().maxMemory()/(1024*1024);
        long available = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = available - free;

        available = available/(1024*1024);
        used = used/(1024*1024);
        return String.format("%s Used memory: %d/%d/%d", prefix, used, available, max);
    }


    public static void runConditionalGarbageCollector(){
        long max = Runtime.getRuntime().maxMemory();
        long available = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = available - free;

        if(3*max/4 < used){
            Runtime.getRuntime().gc();
        }

    }

    public static void waitForGarbageCollectorDone() throws InterruptedException {
        long start = System.currentTimeMillis();
        long current=start, old = start;
        long available = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = available - free;

        System.gc();

//        while(current - old < 500){
//            old = current;
//            current = System.currentTimeMillis();
//
//            long availableMem = Runtime.getRuntime().totalMemory();
//            long freeMem = Runtime.getRuntime().freeMemory();
//            long currentMem = availableMem - freeMem;
//
//            if(used*0.25 > currentMem)
//                break;
//        }


    }
}
