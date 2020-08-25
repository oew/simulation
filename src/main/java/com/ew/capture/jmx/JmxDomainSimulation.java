package com.ew.capture.jmx;

import com.ew.simulation.MapFactory;
import com.ew.util.HazelcastHelper;
import com.ew.util.MBeanHelper;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.query.impl.predicates.LikePredicate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;


public class JmxDomainSimulation extends MapFactory {
  public String location = "/home/everett/sstest/json/";
  public String sourceDomain = "Coherence";
  public String targetDomain = "ew";
  public String serialization = "JSON";
  public String classname = "";

  /*
   * Default Constructor.
   */
  public JmxDomainSimulation() {
    classname = this.getClass().getName();
  }

  /**
   * Load a JMX Snapshot and apply Trends to that snapshot. This is mostly used for testing.
   * 
   * @param args not used.
   */
  public static void main(String[] args) {
    JmxDomainSimulation sim = new JmxDomainSimulation();
    sim.run(args);
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private HazelcastHelper helper;

  /**
   * Load the JMX Snapshot, register the MBeans and apply the trends.
   * 
   * @param args not used.
   */
  public void run(String[] args) {
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

      MBeanWrapper mbw = new MBeanWrapper(domain, key, mbi, this);
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
    String pre = "jmxdomain=" + domain + ",";
    if (name.startsWith(pre)) {
      return name.replaceFirst(pre, "");
    }
    return name;
  }

  @Override
  public Map<String, Map> getSimulationMap() {
    return getHelper().getMap("mbean-attributes");
  }

  /**
   * get the MBeanInfo as a Map.
   * 
   * @return a map with string key and MBeanInfo values
   */
  public Map getInfoMap() {
    return getHelper().getMap("mbean-info");
  }

  /**
   * Accessor method of the Hazelcast helper.
   * 
   * @return the Helper Object.
   */
  private HazelcastHelper getHelper() {
    if (helper == null) {
      helper = new HazelcastHelper();
    }
    return helper;
  }

  @Override
  public Set<String> reduce(Map data, String query) {
    HazelcastHelper helper = getHelper();
    return helper.reduce(data, query);
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
    sharedInfo.putAll(info);

    for (Iterator mbeans = data.entrySet().iterator(); mbeans.hasNext();) {
      Entry mbean = (Entry) mbeans.next();
      String key = (String) mbean.getKey();
      Map map = (Map) mbean.getValue();
      MBeanInfo mbi = (MBeanInfo) info.get(key);
      String name = stripDomain(key, domain);

      MBeanWrapper mbw = new MBeanWrapper(domain, key, mbi, this);
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

  @Override
  public Map<String, Map> invoke(Map data, String query, Object processor) {
    helper.invoke(data, query, processor);
    return null;
  }
}
