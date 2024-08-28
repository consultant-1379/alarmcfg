/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.businessobjects.rebean.wi.ReportEngines;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public class BOAlarmReportLister implements AlarmReportLister {

  private static final String ROOT_CHARACTER = "/";

  private static Logger log = Logger.getLogger(BOAlarmReportLister.class.getName());

  private final String[] alarmCategories;
  
  final private Boolean[] isReducedDelay;

  final private IInfoStore infoStore;

  final private ReportEngines reportEngines;

  final private AlarmInterface alarmInterface;

  final private DatabaseSession dbSession;

  public BOAlarmReportLister(final IInfoStore infoStore, final ReportEngines reportEngines,
      final DatabaseSession dbSession, final AlarmInterface alarmInterface, final String[] categories,
      final Boolean[] isReducedDelay) {
    this.infoStore = infoStore;
    this.dbSession = dbSession;
    this.alarmInterface = alarmInterface;
    this.reportEngines = reportEngines;
    this.alarmCategories = new String[categories.length];
    int i = 0;
    for (String category : categories) {
      this.alarmCategories[i] = removeRootCharacter(category);
      i++;
    }
    this.isReducedDelay = isReducedDelay;
  }
  
  private String removeRootCharacter(String categoryName) {
    // remove bo root character at the start of the category name
    if (categoryName.startsWith(ROOT_CHARACTER)) {
      categoryName = categoryName.substring(1);
    }
    return categoryName;
  }

  private BOCategory getBOCategory(final String boCategoryName) {

    BOCategory result = null;

    //final String[] categoryPath = alarmCategory.trim().split("/");
    final String[] categoryPath = boCategoryName.trim().split(ROOT_CHARACTER);

    try {

      final BOCategory corporateCategory = new BOCategory(infoStore);
      
      BOCategory current = corporateCategory;
      current.refreshCategories();

      boolean match = false;

      for (int ind = 0; ind < categoryPath.length; ind++) {
    	
        if ((ind == 0) || match ) {
        	
          match = false;          
          for (BOCategory tmp : current.getCategories()) {
        	  
            if (categoryPath[ind].equals(tmp.getName())) {
              current = tmp;                            
              current.refreshCategories();
              match = true;
              break;
            }
          }
        } else {
          break;
        }
      }

      if (match) {
        result = current;
      }

    } catch (Exception e) {
      log.severe(e.getMessage());
      e.printStackTrace();
    }    
    return result;
  }

  /**
   * Refresh alarm category and it's sub categories to get all alarm reports
   * 
   * @param category
   * @param list
   *          for collecting results
   */
  private void updateAlarmCategory(final BOCategory category, final List<BODocument<?>> documents) {
    try {
      documents.addAll(category.refreshDocuments());
      category.refreshCategories();
      for (BOCategory subcategory : category.getCategories()) {
        updateAlarmCategory(subcategory, documents);
      }
    } catch (Exception e) {
      log.severe(e.getMessage());
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.reports.AlarmReportLister#getAvailableReports()
   */
  @Override
  public Vector<AlarmReport> getAvailableReports() {
    final Vector<AlarmReport> reports = new Vector<AlarmReport>();
    int ind = 0;
    for (String alarmCategory : alarmCategories) {
      final BOCategory alarmBOCategory = getBOCategory(alarmCategory);
      if (alarmBOCategory == null) {
        log.warning("Alarm Category "+alarmCategory+" not found" );
      } else {
        final List<BODocument<?>> documents = new ArrayList<BODocument<?>>();
        updateAlarmCategory(alarmBOCategory, documents);
        for (BODocument<?> document : documents) {
          // create a new report representation.
          final BOAlarmReport report = new BOAlarmReport(dbSession, alarmInterface, reportEngines, this.isReducedDelay[ind], document);
          reports.add(report);
        }
      }
      ind++;
    }
    return reports;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReportLister#getReport(String
   * reportName)
   */
  @Override
  public AlarmReport getReport(final String reportName) {
    BOAlarmReport report = null;
    int ind = 0;
    for (String alarmCategory : alarmCategories) {
      final BOCategory alarmBOCategory = getBOCategory(alarmCategory);      
      if (alarmBOCategory == null) {
        log.warning(" Alarm Catagory "+alarmCategory + " not found.");
      } else {
        final List<BODocument<?>> documents = new ArrayList<BODocument<?>>();
        updateAlarmCategory(alarmBOCategory, documents);
        for (BODocument<?> document : documents) {
          if (document.getTitle().equals(reportName)) {
            // document exists, so create a new report representation.
            report = new BOAlarmReport(dbSession, alarmInterface, reportEngines, this.isReducedDelay[ind], document);
            break;
          }
        }
      }
      ind++;
    }
    return report;
  }

}
