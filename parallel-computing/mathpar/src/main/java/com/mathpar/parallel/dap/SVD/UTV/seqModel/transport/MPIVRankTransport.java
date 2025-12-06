package com.mathpar.parallel.dap.SVD.UTV.seqModel.transport;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

public class MPIVRankTransport extends MPITransport{
    private Function<Integer, Integer> realRankMapper;

    public MPIVRankTransport(Function<Integer, Integer> realRankMapper) {
        this.realRankMapper = realRankMapper;
    }

    @Override
    protected void sendMPI(ByteBuffer buf, int bufLength, int source, int destination, Tag tag) throws MPIException {
        int tagValue = tagValue(source, destination, tag);
        int realDestination = realRankMapper.apply(destination);

        MPI.COMM_WORLD.send(buf, bufLength, MPI.BYTE, realDestination, tagValue);
    }

    @Override
    protected Status iProbeMPI(int source, int destination, Tag tag) throws MPIException {
        int tagValue = tagValue(source, destination, tag);
        int realSource = realRankMapper.apply(source);

        return MPI.COMM_WORLD.iProbe(realSource, tagValue);
    }

    @Override
    protected void receiveMPI(ByteBuffer buf, int size, int source, int destination, Tag tag) throws MPIException {
        int tagValue = tagValue(source, destination, tag);
        int realSource = realRankMapper.apply(source);

        MPI.COMM_WORLD.recv(buf, size, MPI.BYTE, realSource, tagValue);
    }

    private int tagValue(int source, int destination, Tag tag){
        return Objects.hash(source, destination, tag.ordinal());
    }

}
