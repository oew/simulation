package com.ew.simulation;

import com.ew.capture.jmx.JmxDomainSimulation;
import com.ew.gson.GsonFactory;
import com.ew.util.FileHelper;
import com.ew.util.JsonHelper;
import com.ew.util.LogHelper;
import com.ew.util.TimeHelper;
import com.google.gson.GsonBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to load and run a simulation against a given Map Implementation.
 * 
 * @author Everett Williams
 * @since 1.0
 *
 */

public class MapSimulation {

  private static final Logger logger = LogManager.getLogger(MapSimulation.class);

  public List<MapFactory> maploaders;
  public List<TrendExecutor> trendExecutors;
  public long refreshMillis = 1000;
  public String classname;

  /**
   * Default constructor.
   */
  public MapSimulation() {
    classname = this.getClass().getName();
  }

  /**
   * Load and run the simulation.
   * 
   * @param args the first element contains the filename to load and execute.
   */
  public static void main(String[] args) {
    String location = "./auto.json";
    if (args.length > 0) {
      location = args[0];
    }
    MapSimulation ms = load(location);
    ms.run();
  }

  /** 
   * Create an empty simulation and save it to a file.
   * @param filename the filename to create.
   */
  public void createDefaultSim(String filename) {
    TrendExecutor te = new TrendExecutor();
    this.trendExecutors = new LinkedList<TrendExecutor>();
    this.trendExecutors.add(te);
    this.maploaders = new LinkedList<MapFactory>();
    MapFactory mf = new JmxDomainSimulation();
    this.maploaders.add(mf);
    this.save(filename);
  }

  /**
   * Save the simulation to disk.
   * @param file The file name to store the simulation.
   */
  public void save(String file) {
    String json;
    json = (new GsonBuilder().create()).toJson(this);
    FileHelper.append(file, json);
  }

  /**
   * Load the simulation from a JSON file.
   * @param file a JSON file.
   * @return the MapSimulation to execute.
   */
  public static MapSimulation load(String file) {
    String json = FileHelper.getText(file);
    Map mapData = (Map) GsonFactory.getGSon().fromJson(json, Object.class);
    // this is to implement polymorphism with GSON.
    // it allows the classname to be stored in the json but the TypeAdapter implementations
    // did not work.
    MapSimulation ms = (MapSimulation) JsonHelper.mapToClass(mapData);
    logger.info(LogHelper.format("SIM0100",ms));
    return ms;
  }

  /**
   * load the Map Structure and apply the Trends to the map.
   */
  public void run() {
    MapFactory mapDefault = null;
    TimeHelper th = new TimeHelper();
    long now = System.currentTimeMillis();
    for (MapFactory mapLoader : maploaders) {
      mapLoader.load();
      mapDefault = mapLoader;
    }

    logger.info(LogHelper.format("SIM0004", System.currentTimeMillis() - now));

    while (true) {
      now = System.currentTimeMillis();
      for (TrendExecutor te : trendExecutors) {
        te.executeTrends(mapDefault);
      }
      logger.info(LogHelper.format("SIM0006", (System.currentTimeMillis() - now)));
      try {
        th.sleep(refreshMillis, true);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Get a list simulation Map loaders.
   * @return a list of MapLoaders
   */
  public List<MapFactory> getMapLoaders() {
    if (maploaders == null) {
      maploaders = new LinkedList<MapFactory>();
    }
    return maploaders;
  }

  /**
   * Get the list of TrendExecutors for the simulation.
   * @return a list of TrendExecutors.
   */
  public List<TrendExecutor> getTrendExecutors() {
    if (trendExecutors == null) {
      trendExecutors = new LinkedList<TrendExecutor>();
    }
    return trendExecutors;
  }

  @Override
  public String toString() {
    return "MapSimulation [maploaders=" + maploaders.size() + ", TrendsExecutors="
        + trendExecutors.size() + ", Refresh Rates =  " + refreshMillis + "]";
  }

}
