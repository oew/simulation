package com.ew.util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class MBeanHelper {

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

  public static MBeanServerConnection connect(String ip, String port, String sUser, String sPass) {
    return connect("service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/jmxrmi", sUser, sPass);
  }

  public static MBeanServer findMBeanServer() {
    return ManagementFactory.getPlatformMBeanServer();
  }

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

}
