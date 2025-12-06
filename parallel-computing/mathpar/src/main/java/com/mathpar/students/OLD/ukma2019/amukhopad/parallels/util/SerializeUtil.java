package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class SerializeUtil {
  public static <T extends Serializable> byte[] writeObject(T data) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
      out.writeObject(data);
      out.flush();
      return compress(outputStream.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T readObject(byte[] bytes, Class<T> type) {
    ByteArrayInputStream inStream = new ByteArrayInputStream(decompress(bytes));
    try (ObjectInputStream objectInputStream = new ObjectInputStream(inStream)) {
      return (T) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] compress(byte[] in) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      DeflaterOutputStream defl = new DeflaterOutputStream(out);
      defl.write(in);
      defl.flush();
      defl.close();

      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] decompress(byte[] in) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InflaterOutputStream infl = new InflaterOutputStream(out);
      infl.write(in);
      infl.flush();
      infl.close();

      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
