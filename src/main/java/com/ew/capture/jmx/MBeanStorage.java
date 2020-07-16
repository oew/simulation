package com.ew.capture.jmx;

import java.util.Map;
import javax.management.MBeanInfo;

/**
 * an interface that will store MBean information to disk using different storage methods
 * (Serialization, JSON, XML, etc).
 * 
 * @author Everett Williams
 * @Date 2020-06-32
 */
public interface MBeanStorage {

  /**
   * Store an MBean Data map and MBeanInfo to disk.
   * 
   * @param location The directory to store the infromation.
   * @param domain the domain of the mbean
   * @param index the internal index of the mbean
   * @param data the MBean attributes
   * @param info the MBeanInfo.
   */
  public void storeMBean(String location, String domain, int index, Map<String, Object> data,
      MBeanInfo info);

  /**
   * Store the index MBeanName to file name index to disk.
   * 
   * @param location The directory to store the index.
   * @param domain The MBean Domain.
   * @param index the Index count.
   * @param ndx The index map.
   */
  public void storeMBeanNdx(String location, String domain, int index, Map<String, String> ndx);

  /**
   * Retrieve the MBean Index (name, file number) from Disk.
   * 
   * @param location The directory which the information is stored
   * @param domain The MBean domain.
   * @return a Map containing the MBean names and the file indexes of the snapshot.
   */
  public Map<String, String> getMBeanNdx(String location, String domain);

  /**
   * Retrieve the MBean Info from disk.
   * 
   * @param location the disk directory
   * @param domain the domain to retrieve
   * @param index the mbean index.
   * @return the MBeanInfo object which was captured.
   */
  public MBeanInfo getMBeanInfo(String location, String domain, int index);

  /**
   * Retrieve the MBean Attribute Map from Disk.
   * 
   * @param location the disk location.
   * @param domain the mbean domain
   * @param index the mbean index.
   * @return the capture MBean Attribute map.
   */
  public Map<String, Object> getMBeanData(String location, String domain, int index);

  /**
   * Format the index for the underlying file storage.
   * 
   * @param index the number of the mbean captured.
   * @return a string format for the index.
   */
  public String formatIndex(int index);

  /**
   * Convert a formated index (formatIndex) back into an integer.
   * 
   * @param index a formated index.
   * @return the capture index of the MBean.
   */
  public int getIndex(String index);
}
