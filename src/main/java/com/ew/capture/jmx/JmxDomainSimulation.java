package com.ew.capture.jmx;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ew.simulation.MapFactory;
import com.ew.util.HazelcastHelper;
import com.ew.util.MBeanHelper;
import com.hazelcast.map.IMap;
import com.hazelcast.query.impl.predicates.LikePredicate;

public class JmxDomainSimulation extends MapFactory {
  public String location = "/home/everett/sstest/json/";
  public String sourceDomain = "Coherence";
  public String targetDomain = "ew";
  public String serialization = "JSON";
  public String classname = "";

  public JmxDomainSimulation() {
    classname = this.getClass().getName();
  }

  public static void main(String[] asArgs) {
    JmxDomainSimulation sim = new JmxDomainSimulation();
    sim.run(asArgs);
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private HazelcastHelper m_helper;

  public void run(String[] asArgs) {
    HazelcastHelper helper = getHelper();

    MBeanServer mbs = MBeanHelper.findMBeanServer();
    JmxSnapshot ss = new JmxSnapshot(serialization);

    String domain = sourceDomain;
    Map sharedData = helper.getMap("mbean-attributes");
    Map sharedInfo = helper.getMap("mbean-info");

    Map data = ss.getDomainData(domain, location);
    Map info = ss.getDomainInfo(domain, location);

    sharedData.putAll(data);

    for (Iterator mbeans = data.entrySet().iterator(); mbeans.hasNext();) {
      Entry mbean = (Entry) mbeans.next();
      String key = (String) mbean.getKey();
      Map map = (Map) mbean.getValue();
      MBeanInfo mbi = (MBeanInfo) info.get(key);
      String name = stripDomain(key, domain);

      MBeanWrapper mbw = new MBeanWrapper(domain, key, mbi, sharedData, this);
      ObjectName on;
      try {
        on = new ObjectName(targetDomain + ":" + name);
        mbs.registerMBean(mbw, on);
      } catch (MalformedObjectNameException e) {
        e.printStackTrace();
      } catch (InstanceAlreadyExistsException e) {
        e.printStackTrace();
      } catch (MBeanRegistrationException e) {
        e.printStackTrace();
      } catch (NotCompliantMBeanException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Strips the source domain from the key to register the MBean.
   * 
   * @param name The captured MBean name
   * @param domain the domain to remove.
   * @return a key property string of the MBean.
   */
  public String stripDomain(String name, String domain) {
    String sPrefix = "jmxdomain=" + domain + ",";
    if (name.startsWith(sPrefix)) {
      return name.replaceFirst(sPrefix, "");
    }
    return name;
  }

  @Override
  public Map getDataMap() {
    return getHelper().getMap("mbean-attributes");
  }

  public Map getInfoMap() {
    return getHelper().getMap("mbean-info");
  }

  public HazelcastHelper getHelper() {
    if (m_helper == null) {
      m_helper = new HazelcastHelper();
    }
    return m_helper;
  }

  @Override
  public Set<String> reduce(Map data, String query) {
    Set keys = null;
    if (query.contains("%")) {
      IMap iMap = (IMap) data;
      LikePredicate lp = new LikePredicate("__key", query);
      keys = iMap.keySet(lp);
      return keys;
    } else {
      keys = new HashSet<String>();
      keys.add(query);
    }
    return keys;
  }

  @Override
  public void load() {
    HazelcastHelper helper = getHelper();
    MBeanServer mbs = MBeanHelper.findMBeanServer();
    JmxSnapshot ss = new JmxSnapshot(serialization);

    String domain = sourceDomain;
    Map sharedData = helper.getMap("mbean-attributes");
    Map sharedInfo = helper.getMap("mbean-info");

    Map<String, Object> data = ss.getDomainData(domain, location);
    Map info = ss.getDomainInfo(domain, location);

    sharedData.putAll(data);

    for (Iterator mbeans = data.entrySet().iterator(); mbeans.hasNext();) {
      Entry mbean = (Entry) mbeans.next();
      String key = (String) mbean.getKey();
      Map map = (Map) mbean.getValue();
      MBeanInfo mbi = (MBeanInfo) info.get(key);
      String name = stripDomain(key, domain);

      MBeanWrapper mbw = new MBeanWrapper(domain, key, mbi, sharedData, this);
      ObjectName on;
      try {
        on = new ObjectName(targetDomain + ":" + name);
        mbs.registerMBean(mbw, on);
      } catch (MalformedObjectNameException e) {
        e.printStackTrace();
      } catch (InstanceAlreadyExistsException e) {
        e.printStackTrace();
      } catch (MBeanRegistrationException e) {
        e.printStackTrace();
      } catch (NotCompliantMBeanException e) {
        e.printStackTrace();
      }
    }
  }
}
