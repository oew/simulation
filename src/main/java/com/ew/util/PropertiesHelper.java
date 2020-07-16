package com.ew.util;

public class PropertiesHelper {
  /**
   * Returns the appropriate argument by name from a parameter string array.
   * 
   * @param args the String array from the command line
   * @param name the name of the argument.
   * @param defaultLoc the location of the argument.
   * @return the String which was passed.
   */
  public static String getArgument(String[] args, String name, int defaultLoc, String defaultVal) {
    final String pre = "-";
    final String delim = "=";
    for (String parm : args) {
      String label = pre+name+delim;
      if (parm.startsWith(label)) {
        return parm.substring(label.length());
      }
    }
    if (defaultLoc < args.length) {
      String parm = args[defaultLoc];
      if (parm.startsWith(pre)) {
        return defaultVal;
      } else {
        return parm;
      }
    }
    return defaultVal;
  }

}
