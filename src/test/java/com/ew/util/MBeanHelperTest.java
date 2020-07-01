package com.ew.util;

import static org.junit.Assert.*;

import javax.management.MBeanServerConnection;

import org.junit.Test;

public class MBeanHelperTest {

	@Test
	public void testConnectStringStringStringString() {
		try {
			MBeanServerConnection mbsc = MBeanHelper.connect("localhost", "9097", "", "");
		} catch (Exception e) {
			fail("unable to connect to local MBeanServer on 9099");
		}
	}

}
