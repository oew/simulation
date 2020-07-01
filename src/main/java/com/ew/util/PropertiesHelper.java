package com.ew.util;

public class PropertiesHelper {
	/**
	 * Returns the appropriate 
	 * @param asArgs
	 * @param name
	 * @param defaultLoc
	 * @return
	 */
	public static String getArgument(String[] asArgs, String name, int defaultLoc) {
		return asArgs[defaultLoc];
	}
	

}
