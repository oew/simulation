package com.ew.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationHelper {

  public static byte[] toByteArray(Object obj) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(out);
    os.writeObject(obj);
    try {
      byte[] ret = out.toByteArray();
      return ret;
    } catch (Exception nse) {
      System.out.println("Error serializing object " + obj.getClass().getName());
    }
    return null;
  }

  public static Object fromByteArray(byte[] data) throws IOException, ClassNotFoundException {
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    try {
      ObjectInputStream is = new ObjectInputStream(in);
      return is.readObject();
    } catch (EOFException e) {
      return null;
    }
  }

}
