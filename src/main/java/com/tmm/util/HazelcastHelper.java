package com.tmm.util;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.query.impl.predicates.LikePredicate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to access Hazelcast maps.
 * 
 * @author Everett Williams
 * @since 1.0
 */
public class HazelcastHelper {

  /**
   * return a Hazelcast Instance.
   * 
   * @param iConType the connection type (client or server).
   * @return the Hazelcast Instance.
   */
  private HazelcastInstance getNewInstance() {
    if (instance == null) {
      String configFile = "./hazelcast.xml";
      Config cfg;
      String port = System.getProperty("sl.hazelcast.port");

      try {
        cfg = new XmlConfigBuilder(configFile).build();
      } catch (Exception e) {
        cfg = new Config();
      }
      if (port != null && port.length() > 0) {

        NetworkConfig net = cfg.getNetworkConfig();
        JoinConfig jc = net.getJoin();
        MulticastConfig mcc = jc.getMulticastConfig();

        mcc.setMulticastPort(Integer.valueOf(port));
        jc.setMulticastConfig(mcc);
        net.setJoin(jc);
      }
      instance = Hazelcast.newHazelcastInstance(cfg);
    }
    return instance;
  }

  /**
   * Get a hazelcast connection instance.
   * 
   * @return a hazelcast connection.
   */
  private HazelcastInstance getInstance() {
    if (instance == null) {
      instance = getNewInstance();
    }
    return instance;
  }

  /**
   * get a map of the specified name.
   * 
   * @param name the name of the map.
   * @return a Hazelcast Map Implementation.
   */
  public Map getMap(String name) {
    return getInstance().getMap(name);
  }

  /**
   * Apply a hazelcast query to a map.
   * 
   * @param data the map to reduce.
   * @param query the query to apply.
   * @return the Set of keys meeting the query.
   */
  public Set<String> reduce(Map data, String query) {
    Set keys = null;
    if (query.contains("%")) {
      IMap map = (IMap) data;
      LikePredicate lp = new LikePredicate("__key", query);
      keys = map.keySet(lp);
      return keys;
    } else {
      keys = new HashSet<String>();
      keys.add(query);
    }
    return keys;
  }

  /**
   * Apply a hazelcast query to a map.
   * 
   * @param data the map to reduce.
   * @param query the query to apply.
   * @return the Set of keys meeting the query.
   */
  public Map invoke(Map data, String query, Object processor) {
    EntryProcessor ep = (EntryProcessor) processor;
    Map ret = null;
    IMap map = (IMap) data;
    LikePredicate lp = new LikePredicate("__key", query);
    if (query.contains("%")) {
      ret = map.executeOnEntries(ep, lp);
    } else {
      Set<String> set = new HashSet<String>();
      set.add(query);
      ret = map.executeOnKeys(set, ep);
    }
    return ret;
  }

  protected HazelcastInstance instance;
  public static final int CLIENT_CONN = 1;
  public static final int SERVER_CONN = 2;
}
