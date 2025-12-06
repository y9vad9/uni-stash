package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.MessageType;

public class Message<T> implements Comparable<Message<T>> {
  private T data;
  private int source;
  private MessageType tag;
  private long timestamp;

  public Message() {
    timestamp = System.currentTimeMillis();
  }

  public T getData() {
    return data;
  }

  public Message<T> setData(T data) {
    this.data = data;
    return this;
  }

  public int getSource() {
    return source;
  }

  public Message<T> setSource(int source) {
    this.source = source;
    return this;
  }

  public MessageType getTag() {
    return tag;
  }

  public Message<T> setTag(MessageType tag) {
    this.tag = tag;
    return this;
  }


  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public int compareTo(Message<T> other) {
    if (data instanceof Comparable) {
      return ((Comparable) data).compareTo(other.data);
    }

    return (this.timestamp - other.timestamp < 0) ? -1
        : (this.timestamp - other.timestamp > 0) ? 1
        : 0;
  }

  @Override
  public String toString() {
    return "Message{" +
        "data=" + data + ", source=" + source + ", tag=" + tag + ", timestamp=" + timestamp + "}\n";
  }
}
