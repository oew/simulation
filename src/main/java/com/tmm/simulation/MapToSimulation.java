package com.tmm.simulation;

import com.tmm.capture.jmx.JmxDomainSimulation;
import com.tmm.gson.GsonFactory;
import com.tmm.util.FileHelper;
import com.tmm.util.LogHelper;
import com.tmm.util.MBeanHelper;
import com.tmm.util.PropertiesHelper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MapToSimulation loads a JMX Snapshot and generates a Simulation for each MBean and numeric
 * attribute.
 * 
 * @author Everett Williams
 * @since 1.0
 */
public class MapToSimulation {
  String source;
  private static final Logger logger = LogManager.getLogger(MapToSimulation.class);
  private Map<String, TrendExecutor> trendExcutorCache;

  /**
   * <p>
   * Create a default simulation from an existing MapSnapshot.
   * </p>
   * Usage: MapToSimulation location sourcedomain targetdomain outputfile loadOnly keypart
   *
   * <p>
   * location : the directory containing the snapshot
   * </p>
   * <p>
   * source domain : the domain snapshot file to create a simulation.
   * </p>
   * <p>
   * target domain : the new domain for the simulation. This is used to remap domains when using
   * multiple snapshots in a single simulation.
   * </p>
   * <p>
   * outputfile : the file to create.
   * </p>
   * <p>
   * loadOnly : if true only the map loaders will be created, if false, trends will be added
   * </p>
   * <p>
   * keypart : if all MBeans of a keypart have the same attributes and can use the same trends, this
   * will create TrendExecutor for each unique key part. This will create a much smaller simulation
   * file which is helpful for large snapshots.
   * </p>
   * 
   * @param args String array with arguments.
   */
  public static void main(String[] args) {
    new MapToSimulation().run(args);
    System.exit(1);
  }

  /**
   * Create the simulation from an existing snapshot.
   * 
   * @param args the command line arguments.
   */
  public void run(String[] args) {
    final String location = PropertiesHelper.getArgument(args, "loc", 0, "./");
    final String srcDomain = PropertiesHelper.getArgument(args, "src", 1, "%");
    final String targetDomain = PropertiesHelper.getArgument(args, "tgt", 2, "%");
    final String outFile = PropertiesHelper.getArgument(args, "out", 3, "auto.json");
    final String loadOnly = PropertiesHelper.getArgument(args, "loadOnly", 5, "false");
    final String keyPart = PropertiesHelper.getArgument(args, "keypart", 4, "");

    final String outputFile = location + outFile;
    if (FileHelper.exists(outputFile)) {
      logger.error("SIM0001: File " + outputFile + " exists no simulation created.");
      return;
    }

    JmxDomainSimulation sim = new JmxDomainSimulation();
    sim.location = location;
    sim.sourceDomain = srcDomain;
    sim.targetDomain = targetDomain;
    sim.serialization = "BINARY";
    sim.load();
    Map<String, Map> mapData = sim.getSimulationMap();
    Map mapInfo = sim.getInfoMap();

    JmxDomainSimulation simDef = new JmxDomainSimulation();
    simDef.location = location;
    simDef.sourceDomain = srcDomain;
    simDef.targetDomain = targetDomain;
    simDef.serialization = "BINARY";

    MapSimulation ms = new MapSimulation();
    ms.getMapLoaders().add(simDef);

    if (loadOnly.equals("true")) {
      logger.info(LogHelper.format("SIM0002", outputFile));
      ms.save(outputFile);
    } else {
      for (String key : mapData.keySet()) {
        Map mapAttributes = (Map) mapData.get(key);
        MBeanInfo nfo = (MBeanInfo) mapInfo.get(key);

        TrendExecutor te = getTrendExecutor(key, keyPart);
        if (te == null) {
          te = new TrendExecutor();
          te.query = key;
          for (Iterator att = mapAttributes.keySet().iterator(); att.hasNext();) {
            String attrib = (String) att.next();
            MBeanAttributeInfo ai = MBeanHelper.getAttributeInfo(nfo, attrib);
            if (!ai.isWritable()) {
              Object value = mapAttributes.get(attrib);
              if (value instanceof Number) {
                UniformTrend trend = new UniformTrend();
                trend.attribute = attrib;
                trend.isIncremental = false;
                trend.max = 100;
                trend.min = 50;
                trend.reseed = true;
                trend.classname = "UniformTrend";
                te.getTrends().add(trend);
              }
            }
          }
          if (te.getTrends().size() > 0) {
            if (keyPart.length() > 0) {
              putTrendExecutor(key, te, keyPart);
            }
            ms.getTrendExecutors().add(te);
          }
        }
      }

      long trendsToExecute = ms.getTrendExecutors().size();
      logger.info(LogHelper.format("SIM0003", outputFile, trendsToExecute));
      ms.save(outputFile);
    }
  }


  /**
   * Return a map TrendExecutors that are cached to prevent duplicates.
   * 
   * @return a map of strings and TrendExecutors
   */
  private Map<String, TrendExecutor> getTrendExecutorCache() {
    if (trendExcutorCache == null) {
      trendExcutorCache = new HashMap<String, TrendExecutor>();
    }
    return trendExcutorCache;
  }

  /**
   * Format the key for the simulation.
   * 
   * @param key the MBeanKey
   * @param keyPart the key part which to limit the simulation
   * @return The cache key for the MBean.
   * 
   */
  private String formatKey(String key, String keyPart) {
    if (keyPart.length() > 0) {
      return MBeanHelper.getNamePart(key, keyPart);
    } else {
      return key;
    }
  }

  /**
   * Get the trend executors for the key-keyPart combination. if the keyPart is blank, null is
   * returned.
   * 
   * @param key the MBean Key.
   * @param keyPart the type to limit the
   * @return
   */
  private TrendExecutor getTrendExecutor(String key, String keyPart) {
    return getTrendExecutorCache().get(formatKey(key, keyPart));
  }

  /**
   * Update the cache of TrendExecutors for the keyPart.
   * 
   * @param key The current MBeanKey.
   * @param te The TrendExcutor to cache.
   * @param keyPart The keyPart to limit the simulation too.
   */
  private void putTrendExecutor(String key, TrendExecutor te, String keyPart) {
    getTrendExecutorCache().put(formatKey(key, keyPart), te);
  }
}
