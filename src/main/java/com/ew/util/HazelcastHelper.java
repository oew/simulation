package com.ew.util;

import java.util.Map;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastHelper {

  public HazelcastHelper() {
    // TODO Auto-generated constructor stub
  }

  public HazelcastInstance getInstance(int iConType) {
    if (iConType == CLIENT_CONN) {
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.getNetworkConfig().addAddress("localhost", "localhost:5702");

      HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
      return instance;
    } else if (iConType == SERVER_CONN) {

      if (m_instance == null) {
        String sFile = "./hazelcast.xml";
        Config cfg;
        String sPort = System.getProperty("sl.hazelcast.port");

        try {
          cfg = new XmlConfigBuilder(sFile).build();
        } catch (Exception e) {
          cfg = new Config();
        }
        if (sPort != null && sPort.length() > 0) {

          NetworkConfig net = cfg.getNetworkConfig();
          JoinConfig jc = net.getJoin();
          MulticastConfig mcc = jc.getMulticastConfig();

          Integer iPort = Integer.valueOf(sPort);
          mcc.setMulticastPort(iPort);
          jc.setMulticastConfig(mcc);
          net.setJoin(jc);
          // cfg.setNetworkConfig(net);
        }
        m_instance = Hazelcast.newHazelcastInstance(cfg);
      }
      return m_instance;
    }
    return null;
  }

  protected HazelcastInstance getInstance() {
    if (m_instance == null) {
      m_instance = getInstance(HazelcastHelper.SERVER_CONN);
    }
    return m_instance;
  }

  public Map getMap(String sMapName) {
    return getInstance().getMap(sMapName);
  }

  protected HazelcastInstance m_instance;
  public static int CLIENT_CONN = 1;
  public static int SERVER_CONN = 2;
}
