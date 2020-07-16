package com.ew.util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * A Helper class for JMX MBean functions.
 * 
 * @author Everett Williams
 * @since 1.0
 */
public class MBeanHelper {
  /**
   * Connect to a remote MBeanServer with the given connection string.
   * @param connection the connection string
   * @param user the user name (or blank)
   * @param password the password (or blank)
   * @return a connection to the MBeanServer.
   */
  public static MBeanServerConnection connect(String connection, String user, String password) {

    JMXServiceURL url;
    MBeanServerConnection mbs = null;
    try {
      url = new JMXServiceURL(connection);
      Hashtable env = new Hashtable();
      if (user != null && user.length() > 0) {
        env.put(javax.naming.Context.SECURITY_PRINCIPAL, user);
        env.put(javax.naming.Context.SECURITY_CREDENTIALS, password);
        String[] creds = new String[2];
        creds[0] = user;
        creds[1] = password;
        env.put(JMXConnector.CREDENTIALS, creds);
      }
      JMXConnector jmxc = JMXConnectorFactory.connect(url, env);

      mbs = jmxc.getMBeanServerConnection();

    } catch (MalformedURLException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return null;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
    return mbs;

  }

  /**
   * Connect to an MBeanServer.
   * @param ip the IP to connect.
   * @param port the port to connect.
   * @param user the user name (or blank).
   * @param password the password (or blank).
   * @return a connection to the MBeanServer.
   */
  public static MBeanServerConnection connect(String ip, String port, 
      String user, String password) {
    return connect("service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/jmxrmi", user, password);
  }

  /**
   * Return the local platform MBeanServer.
   * @return the Platform MBeanServer.
   */
  public static MBeanServer findMBeanServer() {
    return ManagementFactory.getPlatformMBeanServer();
  }
  
  /**
   * Extract a name part from an existing MBeanObject name (key=value) pairs key. 
   * @param key the object name.
   * @param part the part to extract
   * @return the string associated with the part of the key. 
   */
  public static String getNamePart(String key, String part) {
    String ret = "";
    try {
      ObjectName on = new ObjectName("Test:" + key);
      ret = on.getKeyProperty(part);
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
    }
    return ret;
  }
  
  /**
   * Find the MBeanAttributeInfo from the MBeanInfo by name.
   * @param nfo the MBeanInfo
   * @param name the Attribute name
   * @return the MBeanAttributeInfo.
   */
  public static MBeanAttributeInfo getAttributeInfo(MBeanInfo nfo, String name) {
    MBeanAttributeInfo[] mai = nfo.getAttributes();
    for (MBeanAttributeInfo ai : mai) {
     if (ai.getName().equals(name)) {
       return ai;
     }
    }
    return null;
  }

}
