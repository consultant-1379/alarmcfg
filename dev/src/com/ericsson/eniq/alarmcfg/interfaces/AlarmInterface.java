/**
 * 
 */
package com.ericsson.eniq.alarmcfg.interfaces;

import java.util.List;

import com.ericsson.eniq.alarmcfg.reports.AlarmReport;

/**
 * @author ecarbjo
 * 
 */
public interface AlarmInterface {

  /**
   * Get the name of the interface. The name should be human readable so that it
   * can be shown in the UI.
   * 
   * @return the name of the interface
   */
  String getName();

  /**
   * Checks if this interface is a real interface or a representation such as
   * the "ALL" interface.
   * 
   * @return true if the interface is real, or false otherwise.
   */
  boolean isRealInterface();

  /**
   * Checks if this interface is a real interface or a representation such as
   * the "ALL" interface.
   * 
   * @return true if the interface is real, or false otherwise.
   */
  boolean isReducedDelay();

  /**
   * Checks if this interface is selected (in the UI or otherwise)
   * 
   * @return true if the interface is selected.
   */
  boolean isSelected();

  /**
   * Returns the id of this interface. This id is used to uniquely identify the
   * interface in the DWH database.
   * 
   * @return the id of this interface.
   */
  String getId();

  /**
   * Retrieves the alarm reports available for this interface.
   * 
   * @return a vector of reports.
   */
  List<AlarmReport> getReports();

  /**
   * (re-)reads the information for the instantiated ID from the data source
   * (database or other)
   */
  void update();

  /**
   * Get the description for this interface.
   * 
   * @return the description
   */
  String getDescription();

  /**
   * @param select
   *          the current interface.
   */
  void setSelected(boolean select);
}
