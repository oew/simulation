package com.ew.simulation;

import com.ew.gson.GsonFactory;
import org.apache.commons.math3.random.RandomDataGenerator;


/**
 * <p>
 * UniformTrend generates a uniform distribution between the max and min. The seed can be provided,
 * or the reseed will automatically seed the distribution.
 * </p>
 * 
 * <p>
 * Extends:
 * </p>
 * {@link Trend}
 * <p>
 * </p>
 * 
 * @author Everett Williams
 * @since 1.0
 */
public class UniformTrend extends Trend {
  public long seed;
  public boolean reseed = true;
  public double max = 10.0D;
  public double min = 3.0D;

  public static void main(String[] asArgs) {

  }

  /**
   * Basic constructor.
   */
  public UniformTrend() {
    classname = this.getClass().getName();
  }

  /**
   * Initialization constructor.
   * 
   * @param attribute The attribute for the trend.
   * @param max The maximum for the trend
   * @param min The minimum for the trend
   * @param reseed if the trend should be reseeded.
   */
  public UniformTrend(String attribute, double max, double min, boolean reseed) {
    classname = this.getClass().getName();
    this.attribute = attribute;
    this.seed = System.currentTimeMillis();
    this.reseed = reseed;
    this.max = max;
    this.min = min;
  }

  @Override
  public Double nextDouble(long timestamp, Double prior) {
    if (isIncremental) {
      return prior + getRandom().nextUniform(min, max);
    } else {
      return getRandom().nextUniform(min, max);
    }
  }

  @Override
  public Integer nextInteger(long timestamp, Integer prior) {
    if (isIncremental) {
      return prior + getRandom().nextInt((int) Math.round(min), (int) Math.round(max));
    } else {
      return getRandom().nextInt((int) Math.round(min), (int) Math.round(max));
    }
  }

  @Override
  public Long nextLong(long timestamp, Long prior) {
    if (isIncremental) {
      return prior + getRandom().nextLong((int) Math.round(min), (int) Math.round(max));
    } else {
      return getRandom().nextLong((int) Math.round(min), (int) Math.round(max));
    }
  }

  @Override
  public String nextString(long timestamp) {
    throw new UnsupportedOperationException("UniformTrend does not support String Trends");
  }

  @Override
  public long nextDatetime(long timestamp) {
    throw new UnsupportedOperationException("UniformTrend does not support date time Trends");
  }

  /**
   * Build a random data generator and apply seed if necessary.
   * 
   * @return a Random number generator
   */
  protected RandomDataGenerator getRandom() {
    if (rdg == null) {
      rdg = new RandomDataGenerator();
      rdg.reSeed(this.seed);
    }
    if (reseed) {
      rdg.reSeed();
    }
    return rdg;
  }

  /**
   * Create a json serialization string.
   * 
   * @return a json string.
   */
  public String toJSon() {
    String json = GsonFactory.getGSon().toJson(this);
    return json;
  }

  /**
   * Create a UniformTrend from a json string.
   * 
   * @param json a json string.
   * @return a UniformTrend.
   */
  public static Trend fromJSon(String json) {
    Object ret;
    ret = GsonFactory.getGSon().fromJson(json, UniformTrend.class);
    return (Trend) ret;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("UniforTrend [attribute=").append(attribute).append(", max=").append(max)
        .append(", min=").append(min).append(", reseed=").append(reseed).append("]");
    return sb.toString();
  }

  private RandomDataGenerator rdg;
}
