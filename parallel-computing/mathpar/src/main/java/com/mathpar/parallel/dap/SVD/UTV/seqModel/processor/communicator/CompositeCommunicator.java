package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

import java.util.function.Function;
import java.util.stream.IntStream;

public class CompositeCommunicator extends Communicator {
    protected Transport globalTransport;
    protected Function<Integer, Integer> rankMapper;

    public CompositeCommunicator(Transport localTransport, Transport globalTransport, Function<Integer, Integer> rankMapper) {
        this(null, localTransport, globalTransport, rankMapper);
    }

    public CompositeCommunicator(Processor processor, Transport localTransport, Transport globalTransport, Function<Integer, Integer> rankMapper) {
        super(processor, localTransport);
        this.globalTransport = globalTransport;
        this.rankMapper = rankMapper;
    }

    @Override
    public void send(Object object, int destination, Transport.Tag tag) {
        int realDestination = rankMapper.apply(destination);
        int rank = processor.getRank();
        int realSource = rankMapper.apply(rank);

        if(realDestination == realSource){
            super.send(object, destination, tag);
        }else{
            globalTransport.sendObject(object, rank, destination, tag);
        }
    }

    @Override
    public void send(Object[] array, int destination, Transport.Tag tag) {
        int realDestination = rankMapper.apply(destination);
        int rank = processor.getRank();
        int realSource = rankMapper.apply(rank);

        if(realDestination == realSource){
            super.send(array, destination, tag);
        }else{
            globalTransport.sendObjectArray(array, rank, destination, tag);
        }
    }

    @Override
    public void send(Object[][] array, int destination, Transport.Tag tag) {
        int realDestination = rankMapper.apply(destination);
        int rank = processor.getRank();
        int realSource = rankMapper.apply(rank);

        if(realDestination == realSource){
            super.send(array, destination, tag);
        }else{
            globalTransport.sendObject2dArray(array, rank, destination, tag);
        }
    }

    @Override
    public Object receive(int source, Transport.Tag tag) {
        int realSource = rankMapper.apply(source);
        int rank = processor.getRank();
        int realDestination = rankMapper.apply(rank);

        if(realDestination == realSource){
            return super.receive(source, tag);
        }else{
            return globalTransport.receiveObject(source, rank, tag);
        }
    }

    @Override
    public Object[] receiveArray(int source, Transport.Tag tag) {
        int realSource = rankMapper.apply(source);
        int rank = processor.getRank();
        int realDestination = rankMapper.apply(rank);

        if(realDestination == realSource){
            return super.receiveArray(source, tag);
        }else{
            return globalTransport.receiveObjectArray(source, rank, tag);
        }
    }

    @Override
    public Object[][] receive2dArray(int source, Transport.Tag tag) {
        int realSource = rankMapper.apply(source);
        int rank = processor.getRank();
        int realDestination = rankMapper.apply(rank);

        if(realDestination == realSource){
            return super.receive2dArray(source, tag);
        }else{
            return globalTransport.receiveObject2dArray(source, rank, tag);
        }
    }

    @Override
    public Object scatter(Object[] data, int source) {
        // TODO fix scatter simulation
        int realSource = rankMapper.apply(source);
        int rank = processor.getRank();
        int realRank = rankMapper.apply(rank);

        int total = processor.getCoordinator().processorsTotal();
        int[] localRanks = IntStream.range(0, total)
                .filter(i -> rankMapper.apply(i) == realRank)
                .toArray();

        int[] otherRanks = IntStream.range(0, total)
                .filter(i -> rankMapper.apply(i) != realRank)
                .toArray();

        if(realSource == realRank){
            return super.scatter(data, source);
        }else{
            return globalTransport.scatter(data, source, rank);
        }
    }

    @Override
    public Object scatter(int source) {
        return scatter(null, source);
    }
}
