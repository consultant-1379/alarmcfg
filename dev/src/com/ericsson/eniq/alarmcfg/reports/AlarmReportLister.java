/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import java.util.List;

/**
 * @author ecarbjo
 * @author eheijun
 * @copyright Ericsson (c) 2009
 */
public interface AlarmReportLister {

  /**
   * Retrieves a list of reports from a data source and returns them.
   * 
   * @return all available reports
   */
  List<AlarmReport> getAvailableReports();

  /**
   * Retrieves a report from a data source and returns it.
   * 
   * @return alarmReport or null if report does not exists
   */
  AlarmReport getReport(String reportName);
}
