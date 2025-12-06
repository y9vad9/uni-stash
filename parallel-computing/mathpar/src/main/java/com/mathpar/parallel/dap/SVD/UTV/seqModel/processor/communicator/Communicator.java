package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.communicator;

import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.LocalTransport;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.transport.Transport;

import java.util.List;

public class Communicator {

    protected Processor processor;
    protected Transport transport;

    public Communicator(Processor processor, Transport transport) {
        this.processor = processor;
        this.transport = transport;
    }

    public void send(Object object, int destination, Transport.Tag tag){
        transport.sendObject(object, processor.getRank(), destination, tag);
    }

    public void send(Object[] array, int destination, Transport.Tag tag){
        transport.sendObjectArray(array, processor.getRank(), destination, tag);
    }

    public void send(Object[][] array, int destination, Transport.Tag tag){
        transport.sendObject2dArray(array, processor.getRank(), destination, tag);
    }

    public Object receive(int source, Transport.Tag tag){
        return transport.receiveObject(source, processor.getRank(), tag);
    }

    public Object[] receiveArray(int source, Transport.Tag tag){
        return transport.receiveObjectArray(source, processor.getRank(), tag);
    }

    public Object[][] receive2dArray(int source, Transport.Tag tag){
        return transport.receiveObject2dArray(source, processor.getRank(), tag);
    }

    public Object scatter(Object[] data, int source){
        return transport.scatter(data, source, processor.getRank());
    }

    public Object scatter(int source){
        return transport.scatter(null, source, processor.getRank());
    }

    public void propagate(Object object, List<Integer> neighbors, Transport.Tag tag){
        for(Integer neighbor: neighbors){
            send(object, neighbor, tag);
        }
    }

    public void propagate(Object[] object, List<Integer> neighbors, Transport.Tag tag){
        for(Integer neighbor: neighbors){
            send(object, neighbor, tag);
        }
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
