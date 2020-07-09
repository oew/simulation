package com.ew.capture.jmx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import com.ew.util.FileHelper;
import com.ew.util.MBeanHelper;
import com.ew.util.SerializationHelper;
import com.google.gson.Gson;

public class JmxSnapshot {
  private static final Logger logger = LogManager.getLogger(JmxSnapshot.class.getName());

  public JmxSnapshot() {

  }

  public JmxSnapshot(String manager) {
    this.manager = manager;
  }

  String manager = "BINARY";

  public static void main(String[] args) {
    JmxSnapshot ss = new JmxSnapshot();
    ss.capture(args);
  }

  public void capture(String[] args) {
    String ip = args[0];
    String port = args[1];
    String filter = "-";
    String user = "";
    String password = "";
    String location = "./";

    if (args.length > 2) {
      filter = args[2];
      user = args[3].equals("-") ? "" : args[3];
      password = args[4].equals("-") ? "" : args[4];
      location = args[5];
    }

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
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public Map getDomainData(String domain, String location) {
    Map map = new HashMap();
    MBeanStorage storage = getStorage();

    Map dataFiles = storage.GetMBeanNdx(location, domain);

    for (Iterator iterFiles = dataFiles.entrySet().iterator(); iterFiles.hasNext();) {
      Entry e = (Entry) iterFiles.next();
      String prefix = (String) e.getKey();
      String key = (String) e.getValue();

      logger.info("Loading key = " + key);
      int index = Integer.valueOf(prefix.replaceFirst(domain, "")).intValue();

      Map data = storage.GetMBeanData(location, domain, index);

      map.put(key, data);
    }
    return map;
  }

  public Map getDomainInfo(String domain, String location) {
    Map map = new HashMap();
    MBeanStorage storage = getStorage();
    Map dataFiles = storage.GetMBeanNdx(location, domain);

    for (Iterator iterFiles = dataFiles.entrySet().iterator(); iterFiles.hasNext();) {
      Entry e = (Entry) iterFiles.next();

      String prefix = (String) e.getKey();
      String key = (String) e.getValue();
      String sndx = prefix.replaceFirst(domain, "");
      int index = Integer.valueOf(sndx).intValue();

      MBeanInfo info = storage.GetMBeanInfo(location, domain, index);
      map.put(key, info);
    }
    return map;

  }

  public void captureDomain(MBeanServerConnection mbsc, String domain, String location) {
    try {
      MBeanStorage storage = getStorage();

      String filter = domain + ":*";
      ObjectName onQuery = new ObjectName(filter);
      Set<ObjectName> setNames = mbsc.queryNames(onQuery, null);
      Map<String, String> mapMBeans = new HashMap<String, String>();

      Integer iFile = 1;
      DecimalFormat format = new DecimalFormat("00000000");

      for (ObjectName on : setNames) {
        String fileBase = domain + format.format(iFile);
        String mbeanName = "jmxdomain=" + on.getDomain() + "," + on.getKeyPropertyListString();
        logger.info("Saving MBean " + mbeanName);

        mapMBeans.put(fileBase, mbeanName);
        MBeanInfo mbi = mbsc.getMBeanInfo(on);

        Map mapData = objectNameToMap(mbsc, on, mbi);

        storage.StoreMBean(location, domain, iFile, mapData, mbi);
        iFile++;
      }

      storage.StoreMBeanNdx(location, domain, 0, mapMBeans);

    } catch (MalformedObjectNameException e) {
      // Incorrect Filter structure.
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstanceNotFoundException e) {
      // MBean Info Error
      e.printStackTrace();
    } catch (IntrospectionException e) {
      // MBean Info Error
      e.printStackTrace();
    } catch (ReflectionException e) {
      // MBean Info Error
      e.printStackTrace();
    }

  }

  /**
   * Convert the MBean Attributes to a Key, value pair Map
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
    } catch (ReflectionException e) {
    }
    return null;
  }

  public static Map listToMap(AttributeList list) {
    Map map = new HashMap();
    for (Iterator i = list.iterator(); i.hasNext();) {
      Attribute a = (Attribute) i.next();
      Object oAttrib = a.getValue();
      map.put(a.getName(), oAttrib);
    }
    return map;
  }

  public static Map getAttributes(MBeanServerConnection mbsc, ObjectName on, String[] attributes) {
    Map map = new HashMap();
    for (String name : attributes) {
      try {
        Object oVal = mbsc.getAttribute(on, name);
        map.put(name, oVal);
      } catch (Exception e) {
        map.put(name, null);
      }
    }

    return map;
  }

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
