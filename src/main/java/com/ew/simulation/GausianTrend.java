package com.ew.simulation;

import com.ew.gson.GsonFactory;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * GuasianTrend generates a normal distribution with a average (mu) and standard deviation (sigma).
 * The seed can be provided, or the reseed will automatically seed the distribution.
 *
 * <p>
 * Extends: {@link Trend}
 *
 * </p>
 *
 * @author Everett Williams
 * @since 1.0
 */
public class GausianTrend extends Trend {

  long seed;
  boolean reseed = true;
  double mu = 0.0D;
  double sigma = 0.0D;

  public static void main(String[] asArgs) {
    System.out.println(GausianTrend.getDefault().toJSon());
  }

  /**
   * Basic constructor.
   * 
   */
  public GausianTrend() {
    classname = this.getClass().getName();
  }

  /**
   * Assignment constructor.
   * 
   * @param mu The average for the trend
   * @param sigma The standard deviation for the trend
   * @param reseed if the trend should be seed automatically
   * @param attribute the attribute for the trend
   */
  public GausianTrend(Double mu, Double sigma, boolean reseed, String attribute) {
    this.classname = this.getClass().getName();
    this.mu = mu;
    this.sigma = sigma;
    this.reseed = reseed;
    this.attribute = attribute;
  }

  /**
   * Helper routine that takes a histogram of results and returns the percentage of results between
   * the number of standard deviations.
   *
   * @param result An array of histogram result set.
   * @param mu the expected average
   * @param sigma the test standard deviation
   * @param idev the number of standard deviations to check
   * @param total the total number of items in the result set.
   * @return the percentage of results between +-idev standard deviations (sigma).
   */
  public double pctSigma(int[] result, int mu, int sigma, int idev, int total) {
    int hits = 0;
    int start = mu - (sigma * idev);
    int end = mu + (sigma * idev);

    for (int i = start; i < end; i++) {
      hits += result[i];
    }
    return Double.valueOf(hits) / Double.valueOf(total);
  }

  @Override
  public Double nextDouble(long timestamp, Double prior) {
    if (isIncremental) {
      return prior + getRandom().nextGaussian(mu, sigma);
    } else {
      return getRandom().nextGaussian(mu, sigma);
    }
  }

  @Override
  public Integer nextInteger(long timestamp, Integer prior) {
    if (isIncremental) {
      return prior + (int) Math.round(getRandom().nextGaussian(mu, sigma));
    } else {
      return (int) Math.round(getRandom().nextGaussian(mu, sigma));
    }
  }

  @Override
  public Long nextLong(long timestamp, Long prior) {
    if (isIncremental) {
      return prior + (int) Math.round(getRandom().nextGaussian(mu, sigma));
    } else {
      return (Long) Math.round(getRandom().nextGaussian(mu, sigma));
    }
  }

  @Override
  public String nextString(long timestamp) {
    throw new UnsupportedOperationException("No String implementation for Normal Trends");

  }

  @Override
  public long nextDatetime(long timestamp) {
    throw new UnsupportedOperationException("No datetime implementation for Normal Trends");
  }

  /**
   * Returns a Uniform random number generator.
   *
   * @return a random number generator.
   */
  protected RandomDataGenerator getRandom() {
    if (rdg == null) {
      rdg = new RandomDataGenerator();
      if (reseed) {
        rdg.reSeed(System.currentTimeMillis());
      }
    }
    return rdg;
  }

  /**
   * Create a Json string from the current object.
   * 
   * @return a json string.
   */
  public String toJSon() {

    String json = GsonFactory.getGSon().toJson(this);
    return json;
  }

  /**
   * Instantiate an object from a Json string.
   * 
   * @param json a json string.
   * @return a GausianTrend object.
   */
  public static Trend fromJSon(String json) {
    Object ret;
    ret = GsonFactory.getGSon().fromJson(json, GausianTrend.class);
    return (Trend) ret;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("GausianWrapper [attribute=").append(attribute).append(", mu=").append(mu)
        .append(", sigma=").append(sigma).append(", incremental=").append(isIncremental())
        .append(", reseed=").append(reseed).append("]");
    return sb.toString();
  }

  /**
   * Create the default trend for examples and tests.
   * 
   * @return a GausianTrend.
   */
  public static GausianTrend getDefault() {
    return new GausianTrend(10.0D, 3.0D, true, "G1");
  }

  private RandomDataGenerator rdg;
}
