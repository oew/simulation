package com.ew.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper Class for file manipulation.
 * 
 * @author Everett Williams
 * @since 1.0
 */
public class FileHelper {

  /**
   * Get a file handle from disk. Create the file if necessary.
   * @param fileName the file to open-create
   * @return the file handle
   */
  public static File getFile(String fileName) {
    File ret = new File(fileName);
    if (!ret.exists()) {
      try {
        ret.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return ret;
  }

  /**
   * Check if a file exists.
   * @param fileName the file name to check for existence.
   * @return true if the file exists.
   */
  public static boolean exists(String fileName) {
    File ret = new File(fileName);
    if (!ret.exists()) {
      return false;
    }
    return true;
  }

  /**
   * Return the file as a text string.
   * @param fileName a file name to open.
   * @return a text string from the file.
   */
  public static String getText(String fileName) {
    File file = getFile(fileName);
    List<String> lines = null;
    List<String> out = new LinkedList();

    if (!exists(fileName)) {
      return "";
    }

    try {
      lines = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF-8"));
    } catch (IOException e) {
      try {
        lines = Files.readAllLines(Paths.get(fileName), Charset.forName("ISO-8859-1"));
      } catch (IOException e2) {
        e2.printStackTrace();
      }
    }
    StringBuffer sb = new StringBuffer();
    for (String line : lines) {
      sb.append(line);
    }
    return sb.toString();
  }
  
  /**
   * Return the file as a List of Strings
   * @param fileName a file name to open.
   * @return a List<String> containing the file.
   */
  public static List<String> getLines(String fileName) {
    File file = getFile(fileName);
    List<String> lines = null;
    List<String> out = new LinkedList();

    if (!exists(fileName)) {
      return null;
    }

    try {
      lines = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF-8"));
    } catch (IOException e) {
      try {
        lines = Files.readAllLines(Paths.get(fileName), Charset.forName("ISO-8859-1"));
      } catch (IOException e2) {
        e2.printStackTrace();
      }
    }
    return lines;
  }
  /**
   * Get the files as a byte array.
   * @param fileName the file name to open and read.
   * @return the file as a byte array.
   */
  public static byte[] getBytes(String fileName) {
    File file = getFile(fileName);
    FileInputStream fis;
    try {
      fis = new FileInputStream(file);
      byte[] data = new byte[fis.available()];
      fis.read(data);
      return data;
    } catch (RuntimeException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Append to the end of a file.
   * @param fileName the file name.
   * @param text the string to append to the file.
   */
  public static void append(String fileName, String text) {
    try {
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
      out.println(text);
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get a buffered writer for the file.
   * @param fileName the file to open for writing 
   * @return the BufferedWriter for the file
   * @throws IOException if the file does not exist.
   */
  protected static BufferedWriter getWriter(String fileName) throws IOException {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
      return writer;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Write the data to a file.
   * @param name the file name.
   * @param data the bytes to write.
   */
  public static void writeFile(String name, byte[] data) {
    File file = FileHelper.getFile(name);

    if (!(file.length() > 0)) {
      PrintStream wps;
      try {
        wps = new PrintStream(file);
        wps.write(data);
        wps.flush();
        wps.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }
}
