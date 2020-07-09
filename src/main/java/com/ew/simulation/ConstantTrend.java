package com.ew.simulation;

import com.ew.gson.GsonFactory;

/**
 * <p>
 * A Trend that will return a constant number or string. Use with {@link TimeRangeTrend} and
 * {@link TimeRangesTrend} for stair step trends and modifying "state" strings.
 * </p>
 * <p>
 * Extends:
 * </p>
 * {@link Trend}
 * <p>
 * </p>
 * 
 * @author Everett Williams
 * @version 1.0
 */
public class ConstantTrend extends Trend {
  public Object value;

  /**
   * Prints an example trend JSON for simulation building.  
   * @param args not used.
   */
  public static void main(String[] args) {
    ConstantTrend t = new ConstantTrend("LastUpdated", 10000, true);
    String json = GsonFactory.getGSon().toJson(t);
    System.out.println(json);
  }

  /**
   * Default Constructor.
   */
  public ConstantTrend() {
    classname = this.getClass().getName();
  }

  /**
   * Assignment Constructor.
   * 
   * @param attribute the attribute to apply the trend.
   * @param value the value for the trend.
   * @param isIncremental if the trend applies the value incrementally to the prior value.
   */
  public ConstantTrend(String attribute, Object value, boolean isIncremental) {
    this.attribute = attribute;
    this.value = value;
    this.isIncremental = isIncremental;
  }

  @Override
  public Double nextDouble(long timestamp, Double prior) {
    if (isIncremental) {
      return Double.valueOf(value.toString()) + prior;
    }
    return Double.valueOf(value.toString());
  }

  @Override
  public Integer nextInteger(long timestamp, Integer prior) {
    if (isIncremental) {
      return Double.valueOf(value.toString()).intValue() + prior;
    }
    return Double.valueOf(value.toString()).intValue();
  }

  @Override
  public Long nextLong(long timestamp, Long prior) {
    if (isIncremental) {
      return Double.valueOf(value.toString()).longValue() + prior;
    }
    return Double.valueOf(value.toString()).longValue();
  }

  @Override
  public String nextString(long timestamp) {
    return value.toString();
  }

  @Override
  public long nextDatetime(long timestamp) {
    return Double.valueOf(value.toString()).longValue();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("ConstantTrend [attribute=").append(attribute).append(", value=").append(value)
        .append(", isIncremenatal=").append(isIncremental).append("]");
    return sb.toString();
  }
}
