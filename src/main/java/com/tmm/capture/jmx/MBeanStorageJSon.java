package com.tmm.capture.jmx;

import com.google.gson.Gson;
import com.tmm.gson.GsonFactory;
import com.tmm.util.FileHelper;
import com.tmm.util.SerializationHelper;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanInfo;

/**
 * Implement MBeanStorage to disk using Gson.
 * 
 * @author Everett Williams
 *
 */
public class MBeanStorageJSon implements MBeanStorage {

  @Override
  public void storeMBean(String location, String domain, int index, Map<String, Object> data,
      MBeanInfo info) {

    String fileBase = domain + formatIndex(index);

    String jsonData = jsonFromMap(data);
    FileHelper.append(location + fileBase + DATASUFFIX, jsonData);

    String jsonInfo = jsonFromMbi(info);
    FileHelper.append(location + fileBase + INFOSUFFIX, jsonInfo);
  }

  @Override
  public void storeMBeanNdx(String location, String domain, int index, Map<String, String> ndx) {
    String jsonNdx;

    jsonNdx = jsonFromMap(ndx);
    FileHelper.append(location + domain + INDEXSUFFIX, jsonNdx);
  }

  @Override
  public Map<String, String> getMBeanNdx(String location, String domain) {
    String file = location + domain + INDEXSUFFIX;

    String jsonNdx = FileHelper.getText(file);
    return mapFromJSon(jsonNdx);
  }

  @Override
  public MBeanInfo getMBeanInfo(String location, String domain, int index) {
    String fileBase = location + domain + formatIndex(index);
    String file = fileBase + INFOSUFFIX;

    String jsonMbi = FileHelper.getText(file);
    MBeanInfo mbi = mbiFromJSon(jsonMbi);
    return mbi;
  }

  @Override
  public Map<String, Object> getMBeanData(String location, String domain, int index) {
    String fileBase = location + domain + formatIndex(index);
    String file = fileBase + DATASUFFIX;
    String jsonMap = FileHelper.getText(file);
    return mapFromJSon(jsonMap);
  }
  
  /**
   * Convert a Map to JSon.
   * @param data a map to convert.
   * @return JSon containing the map.
   */
  public String jsonFromMap(Map data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
  
  /**
   * Convert a MBeanInfo Object into a JSonString.
   * @param info MBeanInfo to convert.
   * @return a String containing the MBeanInfo.
   */
  public String jsonFromMbi(MBeanInfo info) {
    Gson gson = new Gson();
    String json = gson.toJson(info);
    return json;
  }

  /**
   * Create MBeanInfo from a JSonString.
   * 
   * @param json JSon contain MBean Information
   * @return MBeanInformation that was stored to disk.
   */
  public MBeanInfo mbiFromJSon(String json) {
    Gson gson = new Gson();
    MBeanInfo fromDisk;
    fromDisk = gson.fromJson(json, MBeanInfo.class);

    // this is a hack to deal with an serialization issue of the MBeanInfo
    // JConsole reports a unmarshaled exception when using the one from disk
    return new MBeanInfo(fromDisk.getClassName(), fromDisk.getDescription(),
        fromDisk.getAttributes(), fromDisk.getConstructors(), fromDisk.getOperations(),
        fromDisk.getNotifications());
  }

  /**
   * Convert a JSon string into a HashMap.
   * 
   * @param data a JSon String
   * @return HashMap containing the JSon.
   */
  public Map mapFromJSon(String data) {
    Gson gson = new Gson();
    Map ret;
    ret = GsonFactory.getGSon().fromJson(data, HashMap.class);
    return ret;
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

  public static String INDEXSUFFIX = "-ndx.json";
  public static String INFOSUFFIX = "-nfo.json";
  public static String DATASUFFIX = "-dat.json";

}
