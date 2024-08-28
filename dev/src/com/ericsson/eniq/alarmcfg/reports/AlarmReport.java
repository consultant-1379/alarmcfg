/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import java.util.Enumeration;
import java.util.List;

import com.ericsson.eniq.alarmcfg.common.PromptNameValuePair;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt;

/**
 * @author ecarbjo
 * 
 */
public interface AlarmReport {

  /**
   * Update the information for this AlarmReport (re-read it from the data
   * source)
   */
  void update();

  /**
   * @return the parent interface of this report.
   */
  AlarmInterface getInterface();
  
  /**
   * @return the reduced delay information of the this report
   */
  Boolean isReducedDelay();

  /**
   * @return the name of this report
   */
  String getName();

  /**
   * @return the status of this report (enabled/disabled)
   */
  boolean isEnabled();

  /**
   * Is this a valid report? In the case of the reports read from the DWH this
   * would mean that if this method returns true, the report is also present in
   * the BO repository, and hence it is a valid report.
   * 
   * @return true if the report(/report association) is valid, false otherwise.
   */
  boolean isValid();

  /**
   * Sets the validity of a report (see isValid())
   * 
   * @param isValid
   */
  void setValid(boolean isValid);

  /**
   * Enable or disable this report.
   * 
   * @param enable
   *          true if the report should be enabled.
   * @return true if the status changed, false otherwise.
   * @throws DatabaseException
   */
  boolean setEnabled(boolean enable) throws DatabaseException;

  /**
   * @return an enumeration of AlarmReportPrompt objects that contain for
   *         example the base table.
   */
  Enumeration<AlarmReportPrompt> getPrompts();

  /**
   * Retrieve the id of this report.
   * 
   * @return the id of this report.
   */
  String getId();

  /**
   * Remove the element from the database and the AlarmInterface.
   */
  void remove() throws DatabaseException;

  /**
   * Completely save the element, or create a new database row if needed.
   */
  void save() throws DatabaseException;

  /**
   * Adds a new prompt to the report.
   * 
   * @param alarmReportPrompt
   *          the report to add.
   */
  void setBaseTable(String tableName);

  /**
   * Refresh report with new prompt values
   */
  String refresh(List<PromptNameValuePair> nameValues);

}
