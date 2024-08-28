/**
 * 
 */
package com.ericsson.eniq.alarmcfg.reports;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.distocraft.dc5000.repository.dwhrep.Alarmreportparameter;
import com.ericsson.eniq.alarmcfg.common.PromptNameValuePair;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.prompts.AlarmReportPrompt;
import com.ericsson.eniq.alarmcfg.prompts.DefaultAlarmReportPrompt;

/**
 * @author ecarbjo
 * 
 */
public class DWHAlarmReport extends AlarmReportAdapter implements Serializable{

  private static Logger log = Logger.getLogger(DWHAlarmReport.class.getName());

  /**
   * Creates a new report.
   * 
   * @param alarmInterface
   *          the parent interface for this report
   * @param id
   *          the id of this report
   */
  public DWHAlarmReport(final DatabaseSession databaseSession, final AlarmInterface alarmInterface, final String id) {
    this(databaseSession, id);
    this.alarmInterface = alarmInterface;
  }

  /**
   * Creates a new report with a random UUID.
   * 
   * @param alarmInterface
   *          the interface to attach this report to.
   */
  public DWHAlarmReport(final DatabaseSession databaseSession, final AlarmInterface alarmInterface) {
    this(databaseSession, alarmInterface, UUID.randomUUID().toString());
  }

  /**
   * Constructor only giving a reference to the db.
   * 
   * @param db
   */
  public DWHAlarmReport(final DatabaseSession db) {
    this(db, UUID.randomUUID().toString());
  }

  /**
   * Constructor for the class. Initializes class with class variables.
   * 
   * @param alarmInterface
   *          the parent interface of this report
   * @param id
   *          the id for this report
   * @param name
   *          the name of this report
   * @param status
   *          the status of this report (enabled/disabled)
   */
  public DWHAlarmReport(final DatabaseSession databaseSession, final AlarmInterface alarmInterface, final String id,
      final String name, final String status, final Boolean isReducedDelay) {
    this.databaseSession = databaseSession;
    this.id = id;
    this.name = name;
    this.status = status;
    this.alarmInterface = alarmInterface;
    this.isReducedDelay = isReducedDelay; 
  }

  /**
   * sets the database and the id
   * 
   * @param db
   * @param id
   */
  public DWHAlarmReport(final DatabaseSession db, final String id) {
    this.databaseSession = db;
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#update()
   */
  @Override
  public void update() {
    if (databaseSession != null) {
      try {
        final Alarmreport rockAlarmreport = databaseSession.getAlarmreport(this.id);
        if (rockAlarmreport == null) {
          // TODO what to do if alarm report does not exists anymore?
        } else {
          this.name = rockAlarmreport.getReportname();
          this.status = rockAlarmreport.getStatus();
        }
      } catch (DatabaseException e) {
        log.severe("Database error: " + e.getMessage());
      } catch (Exception e) {
        log.severe("Fatal error: " + e.getMessage());
      }
      updatePrompts();
    }
  }

  /**
   * Re-read all prompts of the report from the database.
   */
  private void updatePrompts() {
    if (databaseSession != null) {
      try {
        final List<Alarmreportparameter> rockAlarmreportparameters = databaseSession
            .getAlarmreportparameters(this.id);
        // disallow access to the vector while populating it.
        synchronized (prompts) {
          prompts.clear();
          for (Alarmreportparameter rockAlarmreportparameter : rockAlarmreportparameters) {
            final AlarmReportPrompt prompt = new DefaultAlarmReportPrompt(rockAlarmreportparameter.getName(),
                rockAlarmreportparameter.getValue(), -1);
            prompts.add(prompt);
          }
        }
      } catch (DatabaseException e) {
        log.severe("Database error: " + e.getMessage());
      } catch (Exception e) {
        log.severe("Fatal error: " + e.getMessage());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReportAdapter#saveStatus()
   */
  @Override
  protected void saveStatus() {
    if (databaseSession != null) {
      try {
        final Alarmreport rockAlarmreport = databaseSession.getAlarmreport(this.id);
        if (rockAlarmreport == null) {
          // TODO what to do if alarm report does not exists anymore?
        } else {
          rockAlarmreport.setStatus(this.status);
          databaseSession.saveAlarmreport(rockAlarmreport);
        }
      } catch (DatabaseException e) {
        log.severe("Database error: " + e.getMessage());
      } catch (Exception e) {
        log.severe("Fatal error: " + e.getMessage());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#getPrompts()
   */
  @Override
  public Enumeration<AlarmReportPrompt> getPrompts() {
    return prompts.elements();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.reports.AlarmReport#remove()
   */
  @Override
  public void remove() {
    if (databaseSession != null) {
      try {
        final Alarmreport rockAlarmreport = databaseSession.getAlarmreport(this.id);
        if (rockAlarmreport == null) {
          // TODO what to do if alarm report does not exists anymore?
        } else {
          databaseSession.removeAlarmreport(rockAlarmreport);
        }
      } catch (DatabaseException e) {
        log.severe("Database error: " + e.getMessage());
      } catch (Exception e) {
        log.severe("Fatal error: " + e.getMessage());
      }
    }
    this.alarmInterface.update();
  }

  @Override
  public String refresh(final List<PromptNameValuePair> nameValues) {
    // we don't do anything here because we don't refresh dwh reports
    return null;
  }
}
