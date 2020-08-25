package com.tmm.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to load log text from disk and format the log.
 * 
 * @author Everett Williams
 *
 */
public class LogHelper {
  
  public static void main(String[] args) {
    String out = format("SIM0003", "/home/everett/test.txt", 100);
    System.out.println(out);
  }
  
  /**
   * Format a log id with the objects provided.
   * @param id a uniquely identifiable log string.
   * @param objects the objects to replace in the log.
   * @return a String formated with the id and the context objects.
   */
  public static String format(String id, Object... objects) {
    String log = getLog(id);
    MessageFormat fmt = new MessageFormat(log);
    String out = fmt.format(log, objects);
    return id + ":" + out;
  }
 
  /**
   * Get the Log string from the ID.
   * @param id a unique identifier for the log.
   * @return a string (including format) for the log description.
   */
  public static String getLog(String id) {
    return getLogMap().get(id);
  }
  
  /**
   * Get the log information and return it as a map.
   * @return a map containing the logs for the system.
   */
  protected static Map<String, String> getLogMap() {
    if (logs == null) {
      logs = new HashMap();
      List<String> lines = FileHelper.getLines("./SimLogMap.txt");
      for (String line : lines) {
        String[] pair = line.split("~");
        if (pair.length > 1) {
          logs.put(pair[0], pair[1]);
        }
      }
    }
    return logs;
  }
  
  protected static Map<String, String> logs;

}
