package com.mathpar.parallel.dap.test;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.DispThread;
import com.mathpar.parallel.dap.core.Drop;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;
import org.javatuples.Pair;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Parameter usage:
 * -size  matrix size; supports closed range (:) and sequence (,) values and their combinations. Range increment is 2^i
 * Example:
 * -size=128 produces next matrix sizes [128]
 * -size=32,128 produces next matrix sizes [32, 128]
 * -size=32:256 produces next matrix sizes [32, 64, 128, 256]
 * -size=32:128,512 produces next matrix sizes [32, 64, 128, 512]
 * <p>
 * -leaf   size of the leaf; supports closed range (:) and sequence (,) values and their combinations. Range increment is 2^i
 * <p>
 * -density  matrix density; supports closed range (:) and sequence (,) values and their combinations. Range increment is 10.
 * Example:
 * -density=30:60 produces [30, 40, 50, 60]
 * <p>
 * -maxbits  the maximum number of bits in numbers in the matrix;
 * supports closed range (:) and sequence (,) values and their combinations. Range increment is 2.
 * <p>
 * <p>
 * -count  the number of test per each combination of (size, leaf, density, maxBits)
 * <p>
 * -nocheck    disable the correct result check and max error finding
 * <p>
 * -accuracy   set ring R[] with accuracy X, machineEpsilonR X-20, and floatPos X+10
 * <p>
 * -seq or -sequential run sequential test on given size, density and accuracy
 *
 * -z set ring Z[]
 *
 * -leafdensity set density for leaf
 */

public abstract class DAPTest {
    public static final MpiLogger LOGGER = MpiLogger.getLogger(DAPTest.class);

    protected final int root = 0;
    protected int poolSize = 0;
    protected int rank;

    private int defaultDataSize = 128;
    private int defaultLeafSize = 32;
    protected double defaultDensity = 100; // => 100%
    private final int defaultMaxBits = 5;

    private int testsPerDataSize = 1;
    protected boolean checkResult = true;
    protected boolean sequential = false;

    protected int sleepTime = 1;
    protected int comm = 1;
    private int taskType = 0;
    private int key = 0;

    private double leafdensity = 0.1;

    protected Ring ring = new Ring("R64[]");

    private String reportFile;

    protected DAPTest(String reportFile, int taskType, int key) {
        this.reportFile = reportFile;
        this.taskType = taskType;
        this.key = key;
    }

    protected abstract Element[] initData(int size, double density, int maxBits, Ring ring);

    protected MatrixD[] initData(int size, int mod, Ring ring)
    {
        return new MatrixD[0];
    }

    protected abstract Pair<Boolean, Element> checkResult(DispThread dispThread, String[] args, Element[] initData, Element[] resultData, Ring ring);


    public void runTests(String[] args) throws MPIException, InterruptedException {
        List<Test> tests = getTests(args);

        MPI.Init(args);

        // int [] pool = new int[comm];

        int size = MPI.COMM_WORLD.getSize();

        if (comm > size) comm = size;

        List<Integer> pool = new ArrayList<>(comm);
        for (int i = 0; i < comm; i++) pool.add(i);
        mpi.Group g = MPI.COMM_WORLD.getGroup().incl(pool.stream().mapToInt(i -> i).toArray());
        Intracomm COMM = MPI.COMM_WORLD.create(g);
        Intracomm COMM2;
        rank = MPI.COMM_WORLD.getRank();

        LOGGER.info("freeMemory start program = " + DispThread.bytesToMegabytes(Runtime.getRuntime().freeMemory()));


        if (size != comm) {
            int rest = size - comm;
            List<Integer> secondcomm = new ArrayList<>(rest);
            for (int i = 0; i < rest; i++) secondcomm.add(comm + i);
            mpi.Group g2 = MPI.COMM_WORLD.getGroup().incl(secondcomm.stream().mapToInt(i -> i).toArray());
            COMM2 = MPI.COMM_WORLD.create(g2);
            //if(secondcomm.contains(rank)) LOGGER.info("myproc in COMM2 = " + COMM2.getRank());
        }

        if (pool.contains(rank)) {

            poolSize = COMM.getSize();
            DispThread disp = new DispThread(sleepTime, args, COMM, ring);

            tests.forEach(test -> runTest(disp, test, COMM, args));

            disp.counter.DoneThread();
            disp.counter.thread.join();
        }
        MPI.Finalize();
    }

    private void runTest(DispThread disp, Test test, Intracomm COMM, String[] args) {
        Element[] data = new Element[0];
        setLeafSize(test.leaf);
        setLeafDensity(test.leafdensity);
        long m = System.currentTimeMillis();
        if (rank == root) {
            //LOGGER.info("bef initData + " + m);
            data = initData(test.size, test.density, test.maxBits, test.ring);
            LOGGER.trace(MemoryManager.check("data init"));
        }

        Element[] finalData = data;
        IntStream.range(0, test.count)
                .forEach(i -> {

                    if (rank == root) {
                        LOGGER.info(String.format(
                                "Test.%d started! size=%d leaf=%d density=%f maxBits=%d",
                                i, test.size, test.leaf, test.density, test.maxBits
                        ));

                        LOGGER.trace(MemoryManager.check("test start"));
                        long t1 = System.currentTimeMillis();
                        test.startTime = t1;
                        //LOGGER.info("key = " + key);
                        long c = System.currentTimeMillis();
                        //LOGGER.info("bef start execute + " +c );
                        LOGGER.info("diff+ " + (c - m));
                        Element[] result = executeTest(disp, taskType, key, args, finalData, test.ring);
                        long t2 = System.currentTimeMillis();
                        LOGGER.trace(MemoryManager.check("test done"));

                        test.executionTime = t2 - t1;
                        test.maxUsedMemory = disp.getUsedMemory();

                        manageResult(disp, args, test, result, finalData);

                    } else {
                        // RUN other processors
                        executeTest(disp, taskType, key, args, new Element[0], test.ring);
                        runCheckResultOnOtherProcessors(test, disp, args);
                    }

//                    System.gc();

                    try {
                        COMM.barrier();
                    } catch (MPIException e) {
                        e.printStackTrace();
                    }
                });

    }

    private Element[] executeTest(DispThread dispThread, int taskType, int key, String[] args, Element[] data, Ring ring){
        if(sequential){
            if(rank == root)
                return sequentialExecute(data, ring);
            else
                return new Element[0];
        }

        return execute(dispThread, taskType, key, args, data, ring);
    }

    protected Element[] execute(DispThread dispThread, int taskType, int key, String[] args, Element[] data, Ring ring){

        return runTask(dispThread, taskType, key, args, data, ring);
    }

    protected int dispRunsOnOtherProc(){
        return 1;
    }

    protected void runCheckResultOnOtherProcessors(Test test, DispThread dispThread, String[] args){
        if(!test.checkResult) return;


        for (int i = 0; i < dispRunsOnOtherProc(); i++) {
            runTask(dispThread, taskType, key, args,  new Element[0], test.ring);
        }
    }

    protected Element[] sequentialExecute(Element[] data, Ring ring) {
        return new Element[0];
    }

    private void manageResult(DispThread dispThread, String[] args, Test test, Element[] result, Element[] initData) {

        if (test.checkResult) {
            Pair<Boolean, Element> check = checkResult(dispThread, args, initData, result, ring);
            test.isCorrect = check.getValue0();
            test.precision = check.getValue1();
        }
        saveTestResult(test, poolSize);
    }

    protected final Element[] runTask(DispThread dispThread, int taskType, int key, String[] args, Element[] data, Ring ring){
        try {
            dispThread.execute(taskType, key, args, data, ring);
        } catch (InterruptedException | IOException | MPIException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dispThread.getResult();
    }

//    protected int minLeafSize(int dataSize){
//        int min = Math.min(minLeafSize, minDataSize);
//        return min < dataSize/2? min: dataSize/4;
//    }
//
//    protected int maxLeafSize(int dataSize){
//        int max = Math.min(maxLeafSize, maxDataSize);
//        return max < dataSize/2? max: dataSize/4;
//    }

    private void setLeafSize(int leafSize) {
        Drop drop = (Drop)Drop.getDropObject(taskType, new byte[0]);
        drop.setLeafSize(leafSize);
        drop.doAmin().forEach(Drop -> Drop.setLeafSize(leafSize));
    }

    private void setLeafDensity(double ldensity) {
        Drop drop = (Drop)Drop.getDropObject(taskType, new byte[0]);
        drop.setLeafDensity(ldensity);
        drop.doAmin().forEach(Drop -> Drop.setLeafDensity(ldensity));
    }

    private List<Test> getTests(String[] args){
        final String sizeArg = "-size=";
        final String leafArg = "-leaf=";
        final String densityArg = "-density=";
        final String maxBitsArg = "-maxbits=";
        final String noCheckArg = "-nocheck";
        final String countArg = "-count=";
        final String accuracyArg = "-accuracy=";
        final String seqComputingArg = "-seq";
        final String ringZ = "-z";
        final String sleepTimeArg = "-sleeptime=";
        final String leafDensity = "-leafdensity=";
        final String newComm = "-comm=";

        List<Integer> sizes = new LinkedList<>();
        List<Integer> leaves = new LinkedList<>();
        List<Double> density = new LinkedList<>();
        List<Integer> maxBits = new LinkedList<>();

        Arrays.stream(args).forEach(arg -> {
            arg = arg.toLowerCase();

            if(arg.startsWith(sizeArg)){
                String value = getValue(arg);

                if(value != null){
                    sizes.addAll(getIntValues(value, x -> x * 2));
                }
            } else if (arg.startsWith(leafArg)) {
                String value = getValue(arg);

                if (value != null) {
                    leaves.addAll(getIntValues(value, x -> x * 2));
                }
            } else if (arg.startsWith(densityArg)) {
                String value = getValue(arg);

                if (value != null) {
                    density.addAll(getDoubleValues(value, x -> x + 10));
                }
            } else if (arg.startsWith(maxBitsArg)) {
                String value = getValue(arg);

                if (value != null) {
                    maxBits.addAll(getIntValues(value, x -> x + 2));
                }
            } else if (arg.equals(noCheckArg)) {
                checkResult = false;
            } else if (arg.startsWith(countArg)) {

                String value = getValue(arg);

                if (value != null) {
                    testsPerDataSize = Integer.parseInt(value);
                }
            } else if (arg.startsWith(accuracyArg)) {
                String value = getValue(arg);

                if (value != null) {
                    ring = new Ring("R[]");
                    int accuracy = Integer.parseInt(value);

                    ring.setAccuracy(accuracy);
                    ring.setMachineEpsilonR(accuracy - 5);
                    ring.setFLOATPOS(accuracy + 10);
                }
            } else if (arg.startsWith(seqComputingArg)) {
                sequential = true;
            }
            else if (arg.startsWith(ringZ)) {
                ring = new Ring("Z[]");
            }
            else if (arg.startsWith(sleepTimeArg)) {
                String value = getValue(arg);
                if (value != null) {
                    sleepTime = Integer.parseInt(value);
                }
            }
            else if (arg.startsWith(leafDensity)) {
                String value = getValue(arg);
                if (value != null) {
                    leafdensity = Double.parseDouble(value);
                }
            }else if (arg.startsWith(newComm)) {
                String value = getValue(arg);
                if (value != null) {
                    int val = Integer.parseInt(value);
                    if(val>0) comm = val;
                    else comm=1;
                }
            }
        });

        return generateTests(sizes, leaves, density, maxBits);
    }

    private List<Test> generateTests(List<Integer> sizes, List<Integer> leaves, List<Double> density, List<Integer> maxBits) {
        List<Test> tests = new LinkedList<>();

        setDefaultData(sizes, leaves, density, maxBits);

//        List<Integer> finalDensity = density
//                .stream()
//                .map(i -> 100 * i)
//                .collect(Collectors.toList());

        sizes.forEach(size -> leaves.forEach(leaf -> density.forEach(dens -> maxBits.forEach(maxB ->
                tests.add(new Test(size, leaf, leafdensity, dens, maxB, testsPerDataSize, checkResult, comm, ring))
        ))));

        return tests;
    }

    private void setDefaultData(List<Integer> sizes, List<Integer> leaves, List<Double> density, List<Integer> maxBits) {
        if (sizes.isEmpty()) {
            sizes.add(defaultDataSize);
        }

        if (leaves.isEmpty()) {
            leaves.add(defaultLeafSize);
        }

        if (density.isEmpty()) {
            density.add(defaultDensity);
        }

        if (maxBits.isEmpty()) {
            maxBits.add(defaultMaxBits);
        }
    }

    private String getValue(String source) {
        String[] values = source.split("=");

        if (values.length > 1) {
            return values[1];
        }

        return null;
    }

    private List<Integer> getIntValues(String values, IntFunction function) {

        String[] seqValues = values.split(",");

        return Arrays.stream(seqValues)
                .map(x -> parseRange(x, function))
                .flatMap(Collection::stream)
                .sorted()
                .collect(Collectors.toList());
    }

    private List<Double> getDoubleValues(String values, DoubleFunction function) {

        String[] seqValues = values.split(",");

        return Arrays.stream(seqValues)
                .map(x -> parseRange(x, function))
                .flatMap(Collection::stream)
                .sorted()
                .collect(Collectors.toList());
    }

    private List<Integer> parseRange(String values, IntFunction function) {
        List<Integer> integers = new LinkedList<>();
        String[] rangeValues = values.split(":");

        if (rangeValues.length > 1) {
            int v0 = Integer.parseInt(rangeValues[0]);
            int v1 = Integer.parseInt(rangeValues[1]);

            int min = Math.min(v0, v1);
            int max = Math.max(v0, v1);

            int current = min;

            while (current <= max) {
                integers.add(current);
                current = (int) function.apply(current);
            }

        } else {
            integers.add(Integer.parseInt(rangeValues[0]));
        }

        return integers;
    }

    private List<Double> parseRange(String values, DoubleFunction function) {
        List<Double> integers = new LinkedList<>();
        String[] rangeValues = values.split(":");

        if (rangeValues.length > 1) {
            double v0 = Double.parseDouble(rangeValues[0]);
            double v1 = Double.parseDouble(rangeValues[1]);

            double min = Math.min(v0, v1);
            double max = Math.max(v0, v1);

            double current = min;

            while (current <= max) {
                integers.add(current);
                current = (int) function.apply(current);
            }

        } else {
            integers.add(Double.parseDouble(rangeValues[0]));
        }

        return integers;
    }


    private void saveTestResult(Test test, int poolSize) {
        String proc = sequential ? "SEQUENTIAL" : String.valueOf(poolSize);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date(test.startTime);
        String strDate = sdf.format(now);

        Ring ring = new Ring(test.ring);
        ring.setFLOATPOS(100);
        String precisionStr = test.precision != null ? test.precision.toString(ring) : "NaN";

        String resultCheckStr = test.checkResult ? String.valueOf(test.isCorrect) : "UNCHECKED";
        String density = String.valueOf(test.density);

        List<String> report = Arrays.asList(
                strDate,
                proc,
                String.valueOf(test.size),
                String.valueOf(test.leaf),
                String.valueOf(test.leafdensity),
                density,
                String.valueOf(test.maxBits),
                String.valueOf(test.executionTime),
                resultCheckStr,
                precisionStr,
                String.valueOf(ring.getAccuracy()),
                String.valueOf(test.maxUsedMemory)
        );


       // LOGGER.info("go to save result");
        try {
            saveResultRecord(report);
        } catch (IOException e) {
            e.printStackTrace();
        }


        LOGGER.info(
            String.format((
                "Test proc=%s size=%d leafSize=%d density=%s maxBits=%d time=%d ms correct=%s error=%s accuracy=%d maxUsedMemory=%d sleepTime=%d ring = %s leafdensity = %f"),
                proc, test.size, test.leaf, density, test.maxBits, test.executionTime, resultCheckStr, precisionStr, ring.getAccuracy(), test.maxUsedMemory, sleepTime, ring.toString(), leafdensity)
        );

    }

    private void saveResultRecord(List<String> record) throws IOException {
        String fileName = reportFile + ".csv";
        FileWriter csvWriter = null;
        try {
            csvWriter = openFile(fileName);

            csvWriter.append(String.join(",", record));
            csvWriter.append("\n");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (csvWriter != null) {
                csvWriter.flush();
                csvWriter.close();
            }
        }


    }

    private FileWriter openFile(String name) throws IOException {
        final File file = getFile(name);

        boolean originalFileIsExist = file.exists() && file.isFile();
        FileWriter csvWriter = new FileWriter(name, originalFileIsExist);


        if (!originalFileIsExist) {
            csvWriter.write(getHeaderLine() + "\n");
        }

        return csvWriter;
    }

    private File getFile(String name) {
        File file = new File(name);
        try {
            Scanner myReader = new Scanner(file);

            if (myReader.hasNextLine()) {

                String header = myReader.nextLine();

                if (!header.equals(getHeaderLine())) {
                    LOGGER.warn("The structure of file is not the same, rename old one");
                    File oldFile = new File("old_" + name);
                    file.renameTo(oldFile);

                    file = new File(name);
                }

            }

        } catch (FileNotFoundException e) {
            LOGGER.warn("File " + name + " not found, create new one");
        }

        return file;
    }

    private String getHeaderLine() {
        StringBuilder sb = new StringBuilder();

        sb.append("startTime");
        sb.append(",");
        sb.append("proc");
        sb.append(",");
        sb.append("dataSize");
        sb.append(",");
        sb.append("leafSize");
        sb.append(",");
        sb.append("density");
        sb.append(",");
        sb.append("maxBits");
        sb.append(",");
        sb.append("executionTime");
        sb.append(",");
        sb.append("isCorrect");
        sb.append(",");
        sb.append("error");
        sb.append(",");
        sb.append("accuracy");
        sb.append(",");
        sb.append("memory(MB)");

        return sb.toString();
    }

    protected MatrixS matrix(int size, double density, int maxBits, Ring ring) {
        return new MatrixS(size, size, density, new int[]{maxBits}, new Random(System.currentTimeMillis()), ring.numberONE(), ring);
    }
    protected MatrixD matrix(int size, int mod, Ring ring) {
        return new MatrixD(size,size, mod, ring);
    }

    private static class Test {
        int size;
        int leaf;
        int comm;
        double density;
        int maxBits;
        int count;
        boolean checkResult;
        Ring ring;

        long startTime;

        double leafdensity;
        long executionTime;
        long maxUsedMemory;
        boolean isCorrect;
        Element precision;

        public Test(int size, int leaf, double leafdensity, double density, int maxBits, int count, boolean checkResult, int commnew, Ring ring) {
            this.size = size;
            this.leaf = leaf;
            this.density = density;
            this.maxBits = maxBits;
            this.count = count;
            this.checkResult = checkResult;
            this.ring = ring;
            this.leafdensity = leafdensity;
            this.comm = commnew;
        }
    }
}
