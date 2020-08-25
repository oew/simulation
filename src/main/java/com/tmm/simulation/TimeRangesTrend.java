package com.tmm.simulation;

import java.util.LinkedList;
import java.util.List;
import com.tmm.gson.GsonFactory;


/**
 * <p>
 * A TimeRangesTrend is a list of TimeRangeTrend and a default trend. If the trend time is between a
 * time range trend that trend is applied. Otherwise the default trend is applied.
 * </p>
 * 
 * @author Everett Williams
 * @version 1.0
 * @date 05/21/2020
 *
 */

public class TimeRangesTrend extends Trend {
  public List<TimeRangeTrend> trends;
  public Trend defaultTrend;

  /**
   * Basic Constructor.
   */
  public TimeRangesTrend() {
    classname = this.getClass().getName();
  }

  /**
   * Prints an example Trend for help with configuration.
   * 
   * @param args no arguments used.
   */
  public static void main(String[] args) {
    TimeRangesTrend tr = TimeRangesTrend.getDefault();
    String json = GsonFactory.getGSon().toJson(tr);
    System.out.println(json);

  }

  /**
   * Get the default TimeRangesTrend.
   * 
   * @return a TimeRangesTrend for simple testing.
   */
  public static TimeRangesTrend getDefault() {
    TimeRangesTrend trendRange = new TimeRangesTrend();
    final String attribute = "SystemCpuLoad";
    trendRange.attribute = attribute;
    trendRange.isIncremental = false;

    UniformTrend subTrend = new UniformTrend(attribute, 100.0, 50.0, false);
    TimeRangeTrend trend =
        new TimeRangeTrend(attribute, "10:00:00", "11:00:00", "HH:mm:ss", subTrend);

    trendRange.trends = new LinkedList<TimeRangeTrend>();
    trendRange.trends.add(trend);
    trendRange.defaultTrend = new ConstantTrend(attribute, 10, false);
    return trendRange;
  }

  @Override
  public Double nextDouble(long timestamp, Double prior) {
    for (TimeRangeTrend t : trends) {
      if (t.checkTime(timestamp)) {
        return t.nextDouble(timestamp, prior);
      }
    }
    return defaultTrend.nextDouble(timestamp, prior);
  }

  @Override
  public Integer nextInteger(long timestamp, Integer prior) {
    for (TimeRangeTrend t : trends) {
      if (t.checkTime(timestamp)) {
        return t.nextInteger(timestamp, prior);
      }
    }
    return defaultTrend.nextInteger(timestamp, prior);
  }

  @Override
  public Long nextLong(long timestamp, Long prior) {
    for (TimeRangeTrend t : trends) {
      if (t.checkTime(timestamp)) {
        return t.nextLong(timestamp, prior);
      }
    }
    return defaultTrend.nextLong(timestamp, prior);
  }

  @Override
  public String nextString(long timestamp) {
    for (TimeRangeTrend t : trends) {
      if (t.checkTime(timestamp)) {
        return t.nextString(timestamp);
      }
    }
    return defaultTrend.nextString(timestamp);
  }

  @Override
  public long nextDatetime(long timestamp) {
    for (TimeRangeTrend t : trends) {
      if (t.checkTime(timestamp)) {
        return t.nextDatetime(timestamp);
      }
    }
    return defaultTrend.nextDatetime(timestamp);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("TimeRangesTrend [attribute=").append(attribute).append(", incremental=")
        .append(isIncremental()).append(", DefaultTrend = ").append(this.defaultTrend);
    sb.append(", Ranges  = [");
    for (TimeRangeTrend t : trends) {
      sb.append(t.toString()).append(", ");
    }
    sb.append("]]");
    return sb.toString();
  }
}
