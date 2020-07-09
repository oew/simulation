package com.ew.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.ew.capture.jmx.JmxDomainSimulation;
import com.ew.gson.GsonFactory;
import com.ew.util.FileHelper;
import com.ew.util.JsonHelper;
import com.ew.util.TimeHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to load and run a simulation against a given Map Implementation
 * 
 * @author everett
 *
 */

public class MapSimulation {

  private static final Logger logger = LogManager.getLogger(MapSimulation.class);

  public List<MapFactory> maploaders;
  public List<TrendExecutor> trendExecutors;
  public long refreshMillis = 1000;
  public String classname;

  public MapSimulation() {
    classname = this.getClass().getName();
  }

  public static void main(String[] asArgs) {
    String location = "./auto.json";
    if (asArgs.length > 0) {
      location = asArgs[0];
    }
    MapSimulation ms = load(location);
    ms.run();
  }

  public void simpleSim() {
    MapFactory mf = (MapFactory) new JmxDomainSimulation();
    TrendExecutor te = (TrendExecutor) new TrendExecutor();
    this.maploaders = new LinkedList<MapFactory>();
    this.trendExecutors = new LinkedList<TrendExecutor>();
    this.maploaders.add(mf);
    this.trendExecutors.add(te);
    this.save("./auto.json");
  }

  public void save(String file) {
    String json;
    json = (new GsonBuilder().create()).toJson(this);
    FileHelper.append(file, json);
  }

  public static MapSimulation load(String file) {
    String json = FileHelper.getText(file);
    Map mapData = (Map) GsonFactory.getGSon().fromJson(json, Object.class);
    MapSimulation ms = (MapSimulation) JsonHelper.mapToClass(mapData);
    logger.info(ms);
    return ms;
  }

  public void run() {
    MapFactory mapDefault = null;
    TimeHelper th = new TimeHelper();
    long now = System.currentTimeMillis();
    for (MapFactory mapLoader : maploaders) {
      mapLoader.load();
      mapDefault = mapLoader;
    }

    logger.info("Load time " + (System.currentTimeMillis() - now) + "ms");

    while (true) {
      now = System.currentTimeMillis();
      for (TrendExecutor te : trendExecutors) {
        te.executeTrends(mapDefault);
      }
      logger.info("Execution time " + (System.currentTimeMillis() - now) + "ms");
      try {
        th.sleep(refreshMillis, true);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public List<MapFactory> getMaploaders() {
    if (maploaders == null)
      maploaders = new LinkedList<MapFactory>();
    return maploaders;
  }

  public List<TrendExecutor> getTrendExecutors() {
    if (trendExecutors == null)
      trendExecutors = new LinkedList<TrendExecutor>();
    return trendExecutors;
  }

  public String toString() {
    return "MapSimulation [maploaders=" + maploaders.size() + ", TrendsExecutors="
        + trendExecutors.size() + "Refresh Rates =  " + refreshMillis + "]";
  }

}
