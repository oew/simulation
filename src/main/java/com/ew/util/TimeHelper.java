package com.ew.util;

import java.util.Date;

public class TimeHelper {
	
	public static void main(String[] args) {
		TimeHelper th = new TimeHelper();
		while (true) {
			try {
				th.sleep(1000, true);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

	/**
	 * Sleep function that aligns the to the milliseconds.
	 * 
	 * @param millis The number of milliseconds to sleep.
	 * @param align true to align the completion on the milliseconds.
	 * @throws InterruptedException 
	 */
	public void sleep(long millis, boolean align) throws InterruptedException {
		long now = System.currentTimeMillis();
		long diff = millis - (now % millis);
		long lSleepFor = millis;
		if (align) lSleepFor = diff < millis ? diff : millis;
		Thread.sleep(lSleepFor);
	}

}
