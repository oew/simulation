package com.ew.simulation;

import java.util.Map;
import java.util.Set;

/**
 * Encapsulation layer for Map creation, load and query.
 * 
 * @author everett
 * @since 1.0
 *
 */
public abstract class MapFactory {
  /**
   * Return the map for the simulation.
   * @return a map containing the keys and attributes for the simulation.
   */
  public abstract Map<String, Map> getSimulationMap();

  /**
   * Reduce the map based on a Query and return the resulting keys.
   * @param data the Simulation Map.
   * @param query A string query. 
   * @return the Set of keys that match the query.
   */
  public abstract Set<String> reduce(Map data, String query);

  /**
   * Load the Simulation Map.
   */
  public abstract void load();

  /**
   * Invoke the processor againt all the items in the map that match the filter.
   * @param data the Simulation Map.
   * @param query A string query. 
   * @param processor a processor object to execute on each entry. 
   * @return a Map of items that were updated.
   */
  public abstract Map<String, Map> invoke (Map data, String query, Object processor);
}
