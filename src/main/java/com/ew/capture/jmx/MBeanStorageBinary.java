package com.ew.capture.jmx;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

import javax.management.MBeanInfo;

import com.ew.util.FileHelper;
import com.ew.util.SerializationHelper;

/**
 * Implement MBeanStorage to disk using standard object serialization.
 * 
 * @author Everett Williams
 *
 */
public class MBeanStorageBinary implements MBeanStorage {

  @Override
  public void StoreMBean(String location, String domain, int index, Map<String, Object> data,
      MBeanInfo info) {
    String fileBase = domain + formatIndex(index);

    try {
      byte[] byteData = SerializationHelper.toByteArray(data);
      FileHelper.writeFile(location + fileBase + DATASUFFIX, byteData);

      byte[] byteInfo = SerializationHelper.toByteArray(info);
      FileHelper.writeFile(location + fileBase + INFOSUFFIX, byteInfo);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public MBeanInfo GetMBeanInfo(String location, String domain, int index) {
    String fileBase = domain + formatIndex(index);

    try {
      String file = location + fileBase + INFOSUFFIX;
      byte[] byteInfo = FileHelper.getBytes(file);
      MBeanInfo info = (MBeanInfo) SerializationHelper.fromByteArray(byteInfo);
      return info;

    } catch (IOException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Map<String, Object> GetMBeanData(String location, String domain, int index) {
    String fileBase = domain + formatIndex(index);

    try {
      String file = location + fileBase + DATASUFFIX;
      byte[] byteInfo = FileHelper.getBytes(file);
      Map<String, Object> data = (Map<String, Object>) SerializationHelper.fromByteArray(byteInfo);
      return data;

    } catch (IOException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  @Override
  public void StoreMBeanNdx(String location, String domain, int index, Map<String, String> ndx) {
    byte[] bytesNdx;
    try {
      bytesNdx = SerializationHelper.toByteArray(ndx);
      FileHelper.writeFile(location + domain + INDEXSUFFIX, bytesNdx);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public Map<String, String> GetMBeanNdx(String location, String domain) {
    String fileName = location + domain + INDEXSUFFIX;
    byte[] data = FileHelper.getBytes(fileName);
    Map<String, String> map;
    try {
      map = (Map<String, String>) SerializationHelper.fromByteArray(data);
      return map;
    } catch (ClassNotFoundException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String formatIndex(int index) {
    DecimalFormat format = new DecimalFormat("00000000");
    return format.format(index);
  }

  @Override
  public int getIndex(String index) {
    return Integer.valueOf(index).intValue();
  }

  public static String INDEXSUFFIX = ".sdx";
  public static String INFOSUFFIX = ".sin";
  public static String DATASUFFIX = ".sda";

}
