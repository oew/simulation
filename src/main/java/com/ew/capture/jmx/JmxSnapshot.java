package com.ew.capture.jmx;

import com.ew.util.MBeanHelper;
import com.ew.util.PropertiesHelper;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JmxSnapshot {
  private static final Logger logger = LogManager.getLogger(JmxSnapshot.class.getName());

  /**
   * Simple Constructor.
   */
  public JmxSnapshot() {

  }

  /**
   * Create a JMX Snapshot object.
   * 
   * @param manager Serialization manager.
   */
  public JmxSnapshot(String manager) {
    this.manager = manager;
  }

  String manager = "BINARY";

  /**
   * Capture a JMX MBeanServer to disk.
   * <p>Usage: JmxSnapshot ip port filter user pwd loc
   * </p>
   * ip : the IP address of the MBeanServer. </p>
   * <p>port : the port of the MBeanServer.</p>
   * <p>filter : the Domain to capture ("-" for none)</p>
   * <p>user : the JMX user name ("-" for none)</p>
   * <p> pwd : the JMX password ("-" for none)</p>
   * <p> loc : the location to send the capture ("./" is the default)</p>
   * 
   * @param args see usage.
   */
  public static void main(String[] args) {
    JmxSnapshot ss = new JmxSnapshot();
    ss.capture(args);
  }

  /**
   * Capture a domain from an MBean server to disk. 
   * <p>Usage: JmxSnapshot ip port filter user pwd loc
   * </p>
   * ip : the IP address of the MBeanServer. </p>
   * <p>port : the port of the MBeanServer.</p>
   * <p>filter : the Domain to capture ("-" for none)</p>
   * <p>user : the JMX user name ("-" for none)</p>
   * <p> pwd : the JMX password ("-" for none)</p>
   * <p> loc : the location to send the capture ("./" is the default)</p>
   * 
   * @param args see usage.
   */
  public void capture(String[] args) {
    String ip = PropertiesHelper.getArgument(args, "ip", 0, "localhost");
    String port = PropertiesHelper.getArgument(args, "port", 1, "9099");
    String filter = PropertiesHelper.getArgument(args, "filter", 2, "-");
    String user = PropertiesHelper.getArgument(args, "user", 3, "-");
    String password = PropertiesHelper.getArgument(args, "pwd", 4, "-");
    String location = PropertiesHelper.getArgument(args, "loc", 5, "./");


    MBeanServerConnection mbsc = MBeanHelper.connect(ip, port, user, password);
    if (mbsc != null) {
      try {
        if (filter.equals("-")) {
          String[] setDomains = mbsc.getDomains();
          for (String domain : setDomains) {
            logger.info("Capturing domain : " + domain);
            this.captureDomain(mbsc, domain, location);
          }
        } else {
          logger.info("Capturing domain : " + filter);
          this.captureDomain(mbsc, filter, location);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Retrieve MBean Attributes from disk.
   * 
   * @param domain the domain to retrieve.
   * @param location the disk location.
   * @return a map keyed by MBean Name and values of the MBean Attributes.
   */
  public Map getDomainData(String domain, String location) {
    Map map = new HashMap();
    MBeanStorage storage = getStorage();

    Map dataFiles = storage.getMBeanNdx(location, domain);

    for (Iterator iterFiles = dataFiles.entrySet().iterator(); iterFiles.hasNext();) {
      Entry e = (Entry) iterFiles.next();
      String prefix = (String) e.getKey();
      String key = (String) e.getValue();

      logger.info("Loading key = " + key);
      int index = Integer.valueOf(prefix.replaceFirst(domain, "")).intValue();

      Map data = storage.getMBeanData(location, domain, index);

      map.put(key, data);
    }
    return map;
  }

  /**
   * Get the MBeanInfo map for the domain from the specified location.
   * 
   * @param domain the domain to retrieve
   * @param location the disk location of the storage
   * @return a map keyed by MBean name and values of the MBeanInfo.
   */
  public Map getDomainInfo(String domain, String location) {
    Map map = new HashMap();
    MBeanStorage storage = getStorage();
    Map dataFiles = storage.getMBeanNdx(location, domain);

    for (Iterator iterFiles = dataFiles.entrySet().iterator(); iterFiles.hasNext();) {
      Entry e = (Entry) iterFiles.next();

      String prefix = (String) e.getKey();
      String key = (String) e.getValue();
      String sndx = prefix.replaceFirst(domain, "");
      int index = Integer.valueOf(sndx).intValue();

      MBeanInfo info = storage.getMBeanInfo(location, domain, index);
      map.put(key, info);
    }
    return map;

  }

  /**
   * capture the domain from the MBeanServer and store it in the specified location.
   * 
   * @param mbsc The MBeanSErverConnection.
   * @param domain The Domain to capture.
   * @param location The disk location to store the information.
   */
  public void captureDomain(MBeanServerConnection mbsc, String domain, String location) {
    try {
      MBeanStorage storage = getStorage();

      String filter = domain + ":*";
      ObjectName onQuery = new ObjectName(filter);
      Set<ObjectName> setNames = mbsc.queryNames(onQuery, null);
      Map<String, String> mapMBeans = new HashMap<String, String>();

      Integer fileNdx = 1;
      DecimalFormat format = new DecimalFormat("00000000");

      for (ObjectName on : setNames) {
        String fileBase = domain + format.format(fileNdx);
        String mbeanName = "jmxdomain=" + on.getDomain() + "," + on.getKeyPropertyListString();
        logger.info("Saving MBean " + mbeanName);

        mapMBeans.put(fileBase, mbeanName);
        MBeanInfo mbi = mbsc.getMBeanInfo(on);

        Map mapData = objectNameToMap(mbsc, on, mbi);

        storage.storeMBean(location, domain, fileNdx, mapData, mbi);
        fileNdx++;
      }

      storage.storeMBeanNdx(location, domain, 0, mapMBeans);

    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InstanceNotFoundException e) {
      e.printStackTrace();
    } catch (IntrospectionException e) {
      e.printStackTrace();
    } catch (ReflectionException e) {
      e.printStackTrace();
    }

  }

  /**
   * Convert the MBean Attributes to a Key, value pair Map.
   * 
   * @param mbs MBeanServer source
   * @param on Object Name to convert
   * @param mbi MBean info for the ObjectName
   * 
   * @return a Map containing the attribute name (keys) and Value.
   */
  public static Map objectNameToMap(MBeanServerConnection mbs, ObjectName on, MBeanInfo mbi) {
    MBeanAttributeInfo[] mai = mbi.getAttributes();

    String[] attributes = new String[mai.length];

    for (int j = 0; j < mai.length; j++) {
      attributes[j] = mai[j].getName();
    }

    AttributeList al;
    try {
      try {
        al = mbs.getAttributes(on, attributes);
        return listToMap(al);
      } catch (IOException e) {
        return getAttributes(mbs, on, attributes);
      }
    } catch (InstanceNotFoundException e) {
      e.printStackTrace();
    } catch (ReflectionException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Convert an attribute list to a Map for easy access.
   * 
   * @param list a MBean AttributeList.
   * @return a map key is attribue name, and value of the Attribue.
   */
  public static Map listToMap(AttributeList list) {
    Map map = new HashMap();
    for (Iterator i = list.iterator(); i.hasNext();) {
      Attribute a = (Attribute) i.next();
      Object attrib = a.getValue();
      map.put(a.getName(), attrib);
    }
    return map;
  }

  /**
   * Get the attributes for the MBean and map them to a Map with the object name and the value.
   * 
   * @param mbsc the MBeanServer Connection.
   * @param on the MBean ObjectName
   * @param attributes an array of attributes.
   * @return a map with the Attribute values.
   */
  public static Map getAttributes(MBeanServerConnection mbsc, ObjectName on, String[] attributes) {
    Map map = new HashMap();
    for (String name : attributes) {
      try {
        Object val = mbsc.getAttribute(on, name);
        map.put(name, val);
      } catch (Exception e) {
        map.put(name, null);
      }
    }
    return map;
  }

  /**
   * Create the Disk Storage Manager.
   * 
   * @return a JSon or Binary storage class.
   */
  public MBeanStorage getStorage() {
    if (manager.equals(STORAGEBINARY)) {
      return new MBeanStorageBinary();
    } else {
      return new MBeanStorageJSon();
    }
  }

  public static String STORAGEBINARY = "BINARY";
  public static String STORAGEJSON = "JSON";
}
