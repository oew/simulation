package com.ew.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Class to convert objects to their serialized form and back.
 * 
 * @author Everett Williams
 * @since 1.0
 *
 */
public class SerializationHelper {

  /**
   * Convert an object to a byte array.
   * 
   * @param obj the object to serialize.
   * @return the byte array representation
   * @throws IOException error if the object can't be serialized
   */
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

  /**
   * Convert a byte array into a java object.
   * 
   * @param data the byte array.
   * @return a java object
   * @throws IOException error if the serialization fails.
   * @throws ClassNotFoundException error if the class does not exist in the classpath
   */
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
