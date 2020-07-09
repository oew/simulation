package com.ew.simulation;

import com.ew.gson.GsonFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * <p>
 * TimeRangeTrend is a trend that changes based on a start and end range. The times are entered in
 * the format defined in range format. The format is defined by the <a href=
 * "https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
 * format string.
 * </p>
 * <p>
 *
 * </p>
 * <table>
 * <tr>
 * <th>format</th>
 * <th>Start</th>
 * <th>End</th>
 * <th>description</th>
 * </tr>
 * <tr>
 * <td>HH:mm:ss</td>
 * <td>12:00:00</td>
 * <td>13:00:00</td>
 * <td>Between 12pm and 1 pm every day</td>
 * </tr>
 * <tr>
 * <td>mm:ss</td>
 * <td>15:00</td>
 * <td>30:00</td>
 * <td>between 15 minutes and 30 minutes after every hour</td>
 * </tr>
 * <tr>
 * <td>EEE HH:mm:ss</td>
 * <td>Mon 8:00</td>
 * <td>Mon 9:00</td>
 * <td>Monday between 8am and 9 am</td>
 * </tr>
 * </table>
 * <p>
 * </p>
 * <p>
 * In combination with the TimeRangesTrend, predictable historical trends can be created.
 * </p>
 * 
 * @author Everett Williams
 * @version 1.0
 * 
 *
 */
public class TimeRangeTrend extends Trend {
  public String rangeStart;
  public String rangeEnd;
  public String rangeFormat;
  public Trend trend;

  /**
   * Default Constructor.
   */
  public TimeRangeTrend() {
    classname = this.getClass().getName();
  }

  /**
   * Assignment Constructor.
   * 
   * @param rangeStart the start range string. This must match the SimpleDateFormat of the
   *        rangeFormat.
   * @param rangeEnd the end range string. This must match the SimpleDateFormat of the rangeFormat.
   * @param rangeFormat a SimpleDateFormat String defining the format of the ranges.
   * @param trend a trend to apply if the range time is between startRange and endRange.
   */
  public TimeRangeTrend(String attribute, String rangeStart, String rangeEnd, String rangeFormat,
      Trend trend) {
    classname = this.getClass().getName();
    this.attribute = attribute;
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
    this.rangeFormat = rangeFormat;
    this.trend = trend;
  }

  /**
   * Prints an example JSon and validates that the JSon Serialization works.
   * 
   * @param args no arguments used.
   */
  public static void main(String[] args) {
    TimeRangeTrend trend = TimeRangeTrend.getDefaultClass();

    // Validate that the Serialization to Json works.
    String json = GsonFactory.getGSon().toJson(trend);
    System.out.println(json);
    TimeRangeTrend trend2 =
        (TimeRangeTrend) GsonFactory.getGSon().fromJson(json, TimeRangeTrend.class);
    System.out.println(trend2.checkTime(System.currentTimeMillis()));
  }

  /**
   * Check the given time to determine if it is in the Trend Range.
   * 
   * @param trendTime The time to check
   * @return true if the trendTime is between the start and end times.
   */
  public boolean checkTime(long trendTime) {
    try {
      SimpleDateFormat format = new SimpleDateFormat(rangeFormat);
      Date checkDate = new Date(trendTime);
      Date timeCurrent = format.parse(format.format(checkDate));
      Calendar calCheck = Calendar.getInstance();
      calCheck.setTime(timeCurrent);

      Date timeStart = new SimpleDateFormat(rangeFormat).parse(rangeStart);
      Calendar calStart = Calendar.getInstance();
      calStart.setTime(timeStart);

      Date timeEnd = new SimpleDateFormat(rangeFormat).parse(rangeEnd);
      Calendar calEnd = Calendar.getInstance();
      calEnd.setTime(timeEnd);

      if (timeStart.getTime() <= timeCurrent.getTime()
          && timeCurrent.getTime() <= timeEnd.getTime()) {
        return true;
      }

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Double nextDouble(long timestamp, Double prior) {
    if (this.checkTime(timestamp)) {
      return trend.nextDouble(timestamp, prior);
    }
    return prior;
  }

  @Override
  public Integer nextInteger(long timestamp, Integer prior) {
    if (this.checkTime(timestamp)) {
      return trend.nextInteger(timestamp, prior);
    }
    return prior;
  }

  @Override
  public Long nextLong(long timestamp, Long prior) {
    if (this.checkTime(timestamp)) {
      return trend.nextLong(timestamp, prior);
    }
    return prior;
  }

  @Override
  public String nextString(long timestamp) {
    if (this.checkTime(timestamp)) {
      return trend.nextString(timestamp);
    }
    return "";
  }

  @Override
  public long nextDatetime(long timestamp) {
    return timestamp;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("TimeRangeTrend [attribute=").append(attribute).append(", rangeStart =")
        .append(rangeStart).append(", rangeEnd =").append(rangeEnd).append(", rangeFormat =")
        .append(rangeFormat).append(", incremental=").append(isIncremental()).append(", Trend =")
        .append(trend.toString()).append("]");
    return sb.toString();
  }

  /**
   * Method to return a default class.
   * 
   * @return a new TimeRangeTrend.
   */
  public static TimeRangeTrend getDefaultClass() {
    final String attribute = "RefreshCount";

    UniformTrend subTrend = new UniformTrend(attribute, 100.0, 50.0, false);
    TimeRangeTrend trend =
        new TimeRangeTrend(attribute, "Mon 10:00:00", "Mon 11:00:00", "EEE HH:mm:ss", subTrend);
    return trend;
  }
}
