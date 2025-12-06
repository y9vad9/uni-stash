package com.mathpar.parallel.dap.SVD.UTV.seqModel.transport;

import java.util.*;

public class LocalTransport implements Transport{
    private int poolSize;
    private List<Message> messages;

    public LocalTransport(int poolSize){
        this.poolSize = poolSize;
        messages = new LinkedList<Message>();

    }

    @Override
    public Object receiveObject(int source, int destination, Transport.Tag tag){
        Object result = null;
        Message message = getMessage(source, destination, tag);

        if(message != null){
            result = ((ObjectMessage) message).data;
        }

        return result;
    }

    @Override
    public Object[] receiveObjectArray(int source, int destination, Transport.Tag tag){
        Object[] result = null;
        Message message = getMessage(source, destination, tag);

        if(message != null){
            result = ((ObjectArrayMessage) message).data;
        }

        return result;
    }

    public Object[][] receiveObject2dArray(int source, int destination, Transport.Tag tag){
        Object[][] result = null;
        Message message = getMessage(source, destination, tag);

        if(message != null){
            result = ((Object2dArrayMessage) message).data;
        }

        return result;
    }

    @Override
    public void sendObject(Object object, int source, int destination, Transport.Tag tag){
        sendObject(object, source, destination, tag, -1);
    }

    @Override
    public void sendObjectArray(Object[] array, int source, int destination, Transport.Tag tag){
        sendObjectArray(array, source, destination, tag, -1);
    }

    public void sendObject2dArray(Object[][] array, int source, int destination, Transport.Tag tag){
        sendObject2dArray(array, source, destination, tag, -1);
    }

    @Override
    public Object scatter(Object[] data, int source, int currentRank) {
        if(source == currentRank){
            for (int i = 0; i < data.length; i++) {
                if(i == currentRank){
                    continue;
                }

                sendObject(data[i], source, i, Tag.SCATTER);
            }
            return data[source];
        }

        return receiveObject(source, currentRank, Tag.SCATTER);
    }


    public void sendObject(Object object, int source, int destination, Transport.Tag tag, int iteration){
        Message message = new ObjectMessage(source, destination, tag, object);
        message.iteration = iteration;
        addMessage(message);
    }

    public void sendObjectArray(Object[] array, int source, int destination, Transport.Tag tag, int iteration){
        Message message = new ObjectArrayMessage(source, destination, tag, array);
        message.iteration = iteration;
        addMessage(message);
    }

    public void sendObject2dArray(Object[][] array, int source, int destination, Transport.Tag tag, int iteration){
        Message message = new Object2dArrayMessage(source, destination, tag, array);
        message.iteration = iteration;
        addMessage(message);
    }

    private void addMessage(Message message){
        messages.add(message);
    }

    private Message getMessage(int source, int destination, Transport.Tag tag){
        Message message = null;
        int messageIndex = messages.indexOf(new Message(source, destination, tag));

        if(messageIndex != -1){
            message = messages.remove(messageIndex);
        }

        return message;
    }

    static class Message{
        int source;
        int destination;
        Transport.Tag tag;
        int iteration;

        public Message(int source, int destination, Transport.Tag tag) {
            this.source = source;
            this.destination = destination;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof Message)) return false;
            Message message = (Message) o;
            return source == message.source &&
                    destination == message.destination &&
                    tag == message.tag;
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, destination, tag.ordinal());
        }
    }

    static class ObjectMessage extends Message{
        Object data;

        public ObjectMessage(int source, int destination, Transport.Tag tag, Object data) {
            super(source, destination, tag);
            this.data = data;
        }

    }

    static class ObjectArrayMessage extends Message{
        Object[] data;

        public ObjectArrayMessage(int source, int destination, Transport.Tag tag, Object[] data) {
            super(source, destination, tag);
            this.data = data;
        }
    }

    static class Object2dArrayMessage extends Message{
        Object[][] data;

        public Object2dArrayMessage(int source, int destination, Transport.Tag tag, Object[][] data) {
            super(source, destination, tag);
            this.data = data;
        }
    }
}
