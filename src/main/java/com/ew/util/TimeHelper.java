package com.ew.util;

import java.util.Date;

/**
 * Time Helper class.
 * @author Everett Williams
 * @since 1.0
 */
public class TimeHelper {
  
  /** 
   * Tester function of time wait.
   * @param args no used.
   */
  public static void main(String[] args) {
    TimeHelper th = new TimeHelper();
    while (true) {
      try {
        th.sleep(1000, true);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * Sleep function that aligns the to the milliseconds.
   * 
   * @param millis The number of milliseconds to sleep.
   * @param align true to align the completion on the milliseconds.
   * @throws InterruptedException throw of thread interrupted exception.
   */
  public void sleep(long millis, boolean align) throws InterruptedException {
    long now = System.currentTimeMillis();
    long diff = millis - (now % millis);
    long sleepFor = millis;
    if (align) {
      sleepFor = diff < millis ? diff : millis;
    }
    Thread.sleep(sleepFor);
  }
}
