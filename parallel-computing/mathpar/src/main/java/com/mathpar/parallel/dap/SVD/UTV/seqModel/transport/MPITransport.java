package com.mathpar.parallel.dap.SVD.UTV.seqModel.transport;

import com.mathpar.log.MpiLogger;
import mpi.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.IntStream;

public class MPITransport implements Transport {

    private MpiLogger LOGGER = MpiLogger.getLogger(MPITransport.class);


    @Override
    public Object receiveObject(int source, int destination, Tag tag) {
        ObjectMessage message = (ObjectMessage) receive(source, destination, tag);

        if(message != null) {
//            System.out.println("received " + message);
            return message.data;
        }

        return null;
    }

    @Override
    public Object[] receiveObjectArray(int source, int destination, Tag tag) {

        ObjectArrayMessage message = (ObjectArrayMessage) receive(source, destination, tag);

        if(message != null) {
//            System.out.println("received " + message);
            return message.data;
        }

        return null;
    }

    @Override
    public Object[][] receiveObject2dArray(int source, int destination, Tag tag) {
        Object2dArrayMessage message = (Object2dArrayMessage) receive(source, destination, tag);

        if(message != null) {
//            System.out.println("received " + message);
            return message.data;
        }

        return null;
    }

    @Override
    public void sendObject(Object o, int source, int destination, Tag tag) {
        send(new ObjectMessage(source, destination, tag, o));
    }

    @Override
    public void sendObjectArray(Object[] array, int source, int destination, Tag tag) {
        send(new ObjectArrayMessage(source, destination, tag, array));
    }

    @Override
    public void sendObject2dArray(Object[][] array, int source, int destination, Tag tag) {
        send(new Object2dArrayMessage(source, destination, tag, array));
    }

    @Override
    public Object scatter(Object[] data, int root, int rank) {
        try {
            return scatterExtended(data, root, MPI.COMM_WORLD);
        } catch (MPIException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Object scatter(Object[] data, int source, Comm comm) throws MPIException, IOException, ClassNotFoundException {
        int rank = comm.getRank();
        int pool = comm.getSize();

        int[] sizes = new int[pool];
        int[] displs = new int[pool];
        int[] config = new int[2*pool];
        ByteBuffer buf = null;

        if(comm.getRank() == source) {
            if(data == null || data.length != pool) throw new IllegalArgumentException("Data array must be the same same as pool");

            LOGGER.info("Preparing data");
            buf = prepareData(data, sizes, displs);
            LOGGER.info("Data prepared");

            System.arraycopy(sizes, 0, config, 0, sizes.length);
            System.arraycopy(displs, 0, config, sizes.length, displs.length);
        }

        LOGGER.info("Bcast config");
        comm.bcast(config, config.length, MPI.INT, source);
        LOGGER.info("Config received");

        System.arraycopy(config, 0, sizes, 0, sizes.length);
        System.arraycopy(config, sizes.length, displs, 0, displs.length);

        int objectSize = sizes[rank];
        ByteBuffer rbuf = MPI.newByteBuffer(objectSize);

        LOGGER.info("Scattering data");
        comm.scatterv(buf, sizes, displs, MPI.BYTE, rbuf, objectSize, MPI.BYTE, source);
        LOGGER.info("Data received");

        byte[] arr = new byte[objectSize];
        rbuf.get(arr, 0, objectSize);

        return deserializeObject(arr);
    }


    private ByteBuffer prepareData(Object[] data, int[] sizes, int[] displs) throws IOException {
        ByteBuffer buf;
        int totalSize = 0;
        byte[][] bytes = new byte[data.length][];
        for (int i = 0; i < data.length; i++) {
            bytes[i] = serializeObject(data[i]);

            int currentSize = bytes[i].length;
            sizes[i] = currentSize;
            displs[i] = totalSize;
            totalSize += currentSize;
        }

        LOGGER.info("total data size = "+totalSize/(1024*1024)+ "MB or "+totalSize+"B");

        buf = MPI.newByteBuffer(totalSize);
        for (int obj = 0; obj < data.length; obj++) {
            buf.put(bytes[obj]);
        }

        return buf;
    }

    public Object scatterExtended(Object[] data, int source, Intracomm comm) throws MPIException, IOException, ClassNotFoundException {
        int poolSize = comm.getSize();
        int rank = comm.getRank();

        ScatterGroup[] groups = null;


        int configSize = 3*poolSize + 3;
        int[] globalConfig = new int[configSize*poolSize];

        if(comm.getRank() == source) {
            if(data == null || data.length != poolSize) throw new IllegalArgumentException("Data array size must be the same same as pool size");

//            LOGGER.info("Preparing data");
            groups = prepareData(data, source);
//            LOGGER.info("Data prepared");
            for(ScatterGroup group: groups)
                LOGGER.trace(group);

            globalConfig = createConfig(groups, poolSize);

//            LOGGER.info("Config = "+ Arrays.toString(globalConfig) + " size="+globalConfig.length);
        }

        comm.bcast(globalConfig, configSize*poolSize, MPI.INT, source);

//        LOGGER.info("Received Config");

        Map<Integer, Intracomm> communicators = createIntracommMap(globalConfig, poolSize, configSize, comm);

        int[] config = new int[configSize];
        System.arraycopy(globalConfig, configSize*rank, config, 0, configSize);

        int rankSize = config[0];
        int sizesSize = config[poolSize + 1];

        int[] ranks = new int[rankSize];
        System.arraycopy(config, 1, ranks, 0, rankSize);

        int[] sizes = new int[sizesSize];
        System.arraycopy(config, poolSize + 2, sizes, 0, sizesSize);
        int[] shifts = new int[sizesSize];
        System.arraycopy(config, 2*poolSize + 3, shifts, 0, sizesSize);

        config = null;

//        LOGGER.info("Config received ranks="+Arrays.toString(ranks) + " sizes="+Arrays.toString(sizes) + " shifts="+Arrays.toString(shifts));

        ByteBuffer rbuf = null;
        int objectSize = 0;
        Intracomm GROUP_COMM;
        int groupRank;



        if(comm.getRank() == source){
//            LOGGER.info("pre root scatter groups=" + Arrays.toString(groups));
            for (ScatterGroup g : groups) {
//                LOGGER.info("root Scatter before group create");
                GROUP_COMM = communicators.get(Arrays.hashCode(g.ranks));
                groupRank = GROUP_COMM.getRank();
//                LOGGER.info("Created new root group rank="+groupRank+"/"+GROUP_COMM.getSize());
//                if(GROUP_COMM.isNull()){
//                    LOGGER.error("******** create NULL root group communicator on "+comm.getRank());
//                }
                objectSize = sizes[groupRank];
                rbuf = MPI.newByteBuffer(objectSize);
//                LOGGER.info("root Scatter group");
                GROUP_COMM.scatterv(g.buf, g.sizes, g.shifts, MPI.BYTE, rbuf, objectSize, MPI.BYTE, 0);
//                LOGGER.info("Scatter root received rank");
            }
        }else{
//            LOGGER.info("Scatter group");
            GROUP_COMM = communicators.get(Arrays.hashCode(ranks));
            groupRank = GROUP_COMM.getRank();
//            LOGGER.info("Created new group rank="+comm.getRank()+"/"+GROUP_COMM.getSize());
            objectSize = sizes[groupRank];
            rbuf = MPI.newByteBuffer(objectSize);
//            if(GROUP_COMM.isNull()){
//                LOGGER.error("******** create NULL group communicator on "+comm.getRank());
//            }
            GROUP_COMM.scatterv(null, sizes, shifts, MPI.BYTE, rbuf, objectSize, MPI.BYTE, 0);
//            LOGGER.info("Scatter received rank="+comm.getRank());
        }


        byte[] arr = new byte[objectSize];
        rbuf.get(arr, 0, objectSize);

        return deserializeObject(arr);
    }




    private ScatterGroup[] prepareData(Object[] data, int root) throws IOException {
        final int count = 1;
        byte[][] bytes = new byte[data.length - count][];
        int[] sizes = new int[data.length - count];
        int[] shifts = new int[data.length - count];
        ArrayList<Integer> breaks = new ArrayList<>(data.length);
        breaks.add(0);


        byte[] rootData = serializeObject(data[root]);
        int rootDataSize = rootData.length;
        int totalSize = rootDataSize;

        for (int i = 0; i < bytes.length; i++) {

            int shift = i >= root? 1 : 0;

            bytes[i] = serializeObject(data[i + shift]);

            int currentSize = bytes[i].length;
            sizes[i] = currentSize;

            if(Integer.MAX_VALUE - totalSize <= currentSize){
                breaks.add(i);
                totalSize = rootDataSize;
            }

            shifts[i] = totalSize;
            totalSize += currentSize;
        }

        breaks.add(bytes.length);
        int[] breaksArr = breaks.stream().mapToInt(i -> i).toArray();

//        LOGGER.info("sizes "+Arrays.toString(sizes));
//        LOGGER.info("shifts "+Arrays.toString(shifts));
//        LOGGER.info("breaks "+Arrays.toString(breaksArr));
//        LOGGER.info("rootDataSize "+rootDataSize);

        return arrangeDataInGroups(root, rootData, bytes, sizes, shifts, breaksArr);
    }


    private ScatterGroup[] arrangeDataInGroups(int root, byte[] rootData, byte[][] data, int[] sizes, int[] shifts, int[] breaks){
//        LOGGER.info("Bytes = "+Arrays.stream(data).map(a -> Arrays.toString(a) + "\n").collect(Collectors.joining()));
        ScatterGroup[] groups = new ScatterGroup[breaks.length - 1];

        for (int b = 0; b < breaks.length - 1; b++) {
            int len = breaks[b + 1] - breaks[b];
            int totalLen = len + 1;

            int[] groupSizes = new int[totalLen];
//            if(rootData == null){
//                LOGGER.error("root data is null");
//            }
            groupSizes[0] = rootData.length;
            System.arraycopy(sizes, breaks[b], groupSizes, 1, len);

            int[] groupShifts = new int[totalLen];
            groupShifts[0] = 0;
            System.arraycopy(shifts, breaks[b], groupShifts, 1, len);

            int[] groupRanks = new int[totalLen];
            groupRanks[0] = root;
            int[] ranks = IntStream.range(breaks[b], breaks[b+1])
                    .filter(x -> x + 1 != root)
                    .map(x -> x >= root? x + 1 : x)
                    .toArray();
            System.arraycopy(ranks, 0, groupRanks, 1, ranks.length);


            int totalSize = groupShifts[groupShifts.length - 1] + groupSizes[groupSizes.length - 1];
            byte[] groupData = new byte[totalSize];
            System.arraycopy(rootData, 0, groupData, 0, rootData.length);
            for (int rankIndex = 1; rankIndex < groupRanks.length; rankIndex++) {
                int realRank = groupRanks[rankIndex];
                int dataRank = realRank > root ? realRank - 1 : realRank;
//                LOGGER.info(String.format("set data[%d] to real proc[%d]", dataRank, realRank));
                System.arraycopy(data[dataRank], 0, groupData, groupShifts[rankIndex], groupSizes[rankIndex]);
                data[dataRank] = null;
            }

            groups[b] = new ScatterGroup(groupRanks, groupData, groupSizes, groupShifts, totalSize);
//            LOGGER.info("Group created "+groups[b]);
        }

        return groups;
    }

    private int[] createConfig(ScatterGroup[] groups, int pool){
        final int item = 3*pool + 3;
        int[] config = new int[item*pool];

        for (ScatterGroup group : groups) {
            for (Integer rank : group.ranks) {
                config[item*rank] = group.ranks.length;
                System.arraycopy(group.ranks, 0, config, item*rank + 1, group.ranks.length);

                config[item*rank + pool + 1] = group.sizes.length;
                System.arraycopy(group.sizes, 0, config, item*rank + pool + 2, group.sizes.length);

                config[item*rank + 2*pool + 2] = group.shifts.length;
                System.arraycopy(group.shifts, 0, config, item*rank + 2*pool + 3, group.shifts.length);
            }
        }

        return config;
    }

    private List<int[]> rankGroups(int[] globalConfig, int pool, int configSize){
        List<int[]> set = new LinkedList();

        for (int i = 0; i < pool; i++) {
            int size = globalConfig[i*(configSize)];
            int[] ranks = new int[size];
            System.arraycopy(globalConfig, i*(configSize) + 1, ranks, 0, size);
            set.add(ranks);
        }

        return set;
    }

    private Map<Integer, Intracomm> createIntracommMap(int[] globalConfig, int pool, int configSize, Intracomm comm){
        Map<Integer, Intracomm> map = new HashMap<>();
        List<int[]> ranks = rankGroups(globalConfig, pool, configSize);

//        LOGGER.info("ranks "+ranks.stream().map(Arrays::toString).reduce("", (x, s) -> x + "\n" + s));

        ranks.forEach(r -> {
            try {
                int hash = Arrays.hashCode(r);
                if(!map.containsKey(hash)){
                    map.put(hash, comm.create(comm.getGroup().incl(r)));
                }
            } catch (MPIException e) {
                e.printStackTrace();
            }
        });

        return map;
    }

    private static class ScatterGroup{
        int[] ranks;
        ByteBuffer buf;
        int[] sizes;
        int[] shifts;
        int totalSize;

        ScatterGroup(int[] ranks, byte[] data, int[] sizes, int[] shifts, int totalSize) {
            this.ranks = ranks;
            this.sizes = sizes;
            this.shifts = shifts;
            this.totalSize = totalSize;
            this.buf = MPI.newByteBuffer(totalSize);
            this.buf.put(data);
        }

        @Override
        public String toString() {
            return "ScatterGroup{" +
                    "ranks=" + Arrays.toString(ranks) +
                    ", sizes=" + Arrays.toString(sizes) +
                    ", shifts=" + Arrays.toString(shifts) +
                    ", totalSize=" + totalSize +
                    '}';
        }
    }

    protected byte[] serializeObject(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);

        return bos.toByteArray();
    }

    protected Object deserializeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis);
        return in.readObject();
    }

    private void send(Message message){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);

            byte[] tmp = bos.toByteArray();
            ByteBuffer buf = MPI.newByteBuffer(tmp.length);
            buf.put(tmp);

            LOGGER.trace("send " + message);
            sendMPI(buf, tmp.length, message.source, message.destination, message.tag);
        } catch (MPIException | IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendMPI(ByteBuffer buf, int bufLength, int source, int destination, Tag tag) throws MPIException {
        MPI.COMM_WORLD.send(buf, bufLength, MPI.BYTE, destination, tag.ordinal());
    }

    private Object receive(int source, int destination, Tag  tag){
        try {
            Status st = iProbeMPI(source, destination, tag);

            if(st == null) return null;

            int size = st.getCount(MPI.BYTE);

            byte[] arr = new byte[size];
            ByteBuffer buff = MPI.newByteBuffer(size);

            receiveMPI(buff, size, source, destination, tag);

            buff.get(arr, 0, size);
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ObjectInput in = new ObjectInputStream(bis);
            Object object = in.readObject();
            LOGGER.trace("received "+object);

            return object;
        } catch (IOException | ClassNotFoundException | MPIException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected Status iProbeMPI(int source, int destination, Tag tag) throws MPIException {
        return MPI.COMM_WORLD.iProbe(source, tag.ordinal());
    }

    protected void receiveMPI(ByteBuffer buf, int size, int source, int destination, Tag tag) throws MPIException {
        MPI.COMM_WORLD.recv(buf, size, MPI.BYTE, source, tag.ordinal());
    }

    static abstract class Message implements Serializable{
        int source;
        int destination;
        Tag tag;

        Message(int source, int destination, Tag tag) {
            this.source = source;
            this.destination = destination;
            this.tag = tag;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "source=" + source +
                    ", destination=" + destination +
                    ", tag=" + tag +
                    '}';
        }
    }

    static class ObjectMessage extends Message{
        Object data;

        ObjectMessage(int source, int destination, Tag tag, Object data) {
            super(source, destination, tag);
            this.data = data;
        }

        @Override
        public String toString() {
            return "ObjectMessage{" +
                    "source=" + source +
                    ", destination=" + destination +
                    ", tag=" + tag;
        }
    }

    static class ObjectArrayMessage extends Message{
        Object[] data;

        ObjectArrayMessage(int source, int destination, Tag tag, Object[] data) {
            super(source, destination, tag);
            this.data = data;
        }

        @Override
        public String toString() {
            return "ObjectArrayMessage{" +
                    "source=" + source +
                    ", destination=" + destination +
                    ", tag=" + tag;
        }
    }

    static class Object2dArrayMessage extends Message{
        Object[][] data;

        Object2dArrayMessage(int source, int destination, Tag tag, Object[][] data) {
            super(source, destination, tag);
            this.data = data;
        }

        @Override
        public String toString() {
            return "Object2dArrayMessage{" +
                    "source=" + source +
                    ", destination=" + destination +
                    ", tag=" + tag;
        }
    }
}
