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

  abstract public Map getDataMap();

  public Set<String> reduce(Map data, String query) {
    throw new UnsupportedOperationException();
  };

  public void load() {
    throw new UnsupportedOperationException();

  };
}
