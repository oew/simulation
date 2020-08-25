package com.ew.simulation;

import com.ew.gson.GsonFactory;
import com.google.gson.Gson;
import com.hazelcast.map.EntryProcessor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <p>
 * The Trend Executor executes a map reduce against a {@link MapFactory} and then applies the list
 * of {@link Trend} to the Map results set.
 * </p>
 * 
 * @author everett williams
 * @verion 1.0
 * @since 2020/05/19
 *
 */
public class TrendExecutor implements EntryProcessor, Serializable {
  public String query = "";
  public List<Trend> trends;
  public String classname;
  protected long time;

  /**
   * Main simply prints an example TrendExecutor for creating custom simulations.
   * 
   * @param asArgs none.
   */
  public static void main(String[] asArgs) {
    TrendExecutor te = new TrendExecutor();
    te.getDefaultTrends();
    String json = TrendExecutor.toJSon(te);
    System.out.println(json);
  }

  /**
   * Default constructor.
   */
  public TrendExecutor() {
    classname = this.getClass().getName();
  }

  /**
   * Execute the trends against a MapFactory.
   * @param factory the MapFactory that loaded the data.
   * @return a map with the new simulated data.
   */
  public Map executeTrends(MapFactory factory) {
    Map data = factory.getSimulationMap();
    time = System.currentTimeMillis();

    data = factory.invoke(data, query, this);
    return data;
  }

  /**
   * Create default test data.
   * @return a map of default data for testing.
   */
  private Map<String, Map> getDefaultData() {
    Map tmp1 = new HashMap();
    tmp1.put("G1", 100);
    tmp1.put("G2", 200);
    tmp1.put("U1", 10);
    Map<String, Map> ret = new HashMap<String, Map>();
    ret.put("A", tmp1);

    Map tmp2 = new HashMap();
    tmp2.put("G1", 150);
    tmp2.put("G2", 250);
    tmp2.put("U1", 15);
    ret.put("B", tmp2);

    Map tmp3 = new HashMap();
    tmp3.put("G1", 250);
    tmp3.put("G2", 550);
    tmp3.put("U1", 25);
    ret.put("C", tmp3);
    return ret;
  }

  /**
   * Create a list of Default Trends to Execute.  This is used for testing 
   * and default Trend Generation.
   */
  private void getDefaultTrends() {
    GausianTrend normalTrend = new GausianTrend(10.0D, 3.0D, true, "RefreshPredictionCount");
    UniformTrend uniformTrend = new UniformTrend("RefreshCount", 1000.0D, 940.0D, true);
    TimeRangesTrend tr = TimeRangesTrend.getDefault();

    getTrends().add(normalTrend);
    getTrends().add(uniformTrend);
    getTrends().add(tr);
  }

  /** 
   * Serialize the Object as JSon.
   * @param te The Trend Executor to serialize.
   * @return a JSon String.
   */
  public static String toJSon(TrendExecutor te) {
    Gson gson = GsonFactory.getGSon();
    String json = gson.toJson(te);
    return json;
  }

  /**
   * Create/Get the list of Trends for the executor.
   * 
   * @return a list of trends to execute against the query.
   */
  public List<Trend> getTrends() {
    if (trends == null) {
      trends = new LinkedList<Trend>();
    }
    return trends;
  }

  @Override
  public Object process(Entry entry) {
    Map<String, Object> values = (Map<String, Object>) entry.getValue();
    if (trends != null) {
      for (Trend trend : trends) {
        String attribute = trend.getAttribute();
        Object val = values.get(attribute);
        if (val instanceof Double) {
          Double next = trend.next(time, (Double) val);
          values.put(attribute, next);
        } else if (val instanceof Long) {
          Long next = trend.next(time, (Long) val);
          values.put(attribute, next);
        } else if (val instanceof Integer) {
          Integer next = trend.next(time, (Integer) val);
          values.put(attribute, next);
        } else if (val instanceof String) {
          String next = trend.next(time, (String) val);
          values.put(attribute, next);
        }
      }
  }
  entry.setValue(values);
  return entry;
 }
}
