/**
 * 
 */
package com.ericsson.eniq.alarmcfg.interfaces;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Alarminterface;
import com.distocraft.dc5000.repository.dwhrep.Alarmreport;
import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.exceptions.DatabaseException;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;
import com.ericsson.eniq.alarmcfg.reports.DWHAlarmReport;

/**
 * @author ecarbjo A simple container for the alarm interfaces.
 */
public class DefaultAlarmInterface implements AlarmInterface {

  private static Logger log = Logger.getLogger(DefaultAlarmInterface.class.getName());

  private static final String REDUCED_DELAY = "Reduced Delay";

  final private DatabaseSession databaseSession;

  private String id;

  private String name = null;

  private String description = null;

  private boolean isSelected;

  private Boolean reducedDelay;

  final private List<AlarmReport> reports = new Vector<AlarmReport>();

  final private AuthSession authSession;

  final private AlarmProperties alarmProperties;

  /**
   * Default alarm interface constructor. Initializes an interface object with a
   * lister to validate reports against, and a database connection to actually
   * retrieve interfaces.
   * 
   * @param databaseSession
   *          the database session to use to retrieve reports and interfaces
   * @param authSession
   *          the lister that can be used to validate the reports that are
   *          retrieved from the database
   * @param alarmProperties
   * @param interfaceid
   *          the id of this interface.
   */
  public DefaultAlarmInterface(final DatabaseSession databaseSession, final AuthSession authSession,
      final AlarmProperties alarmProperties, final String interfaceid) {
    this.databaseSession = databaseSession;
    this.authSession = authSession;
    this.alarmProperties = alarmProperties;
    this.reducedDelay = false;
    init(interfaceid);
  }

  /**
   * Init the class.
   * 
   * @param name
   */
  private void init(final String id) {
    this.id = id;

    // if the id starts with AlarmInterface_ we remove this for the name (nicer
    // human readable)
    // and if the convention has changed, we just use the whole ID as the name.
    if (id.startsWith("AlarmInterface_")) {
      this.name = id.replaceFirst("^AlarmInterface_", "");
    } else {
      this.name = id;
    }
    // New RD interface name should be shown as "Reduced Delay"
    if (this.name.equals("RD")) {
      this.name = REDUCED_DELAY;
      this.reducedDelay = true;
    }
      
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.containers.AlarmInterface#getName()
   */
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.containers.AlarmInterface#isAllInterface()
   */
  @Override
  public boolean isRealInterface() {
    return true;
  }
  
  @Override
  public boolean isReducedDelay() {
    return reducedDelay;
  }
  

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.containers.AlarmInterface#isSelected()
   */
  @Override
  public boolean isSelected() {
    return isSelected;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface#setSelected(boolean)
   */
  @Override
  public void setSelected(final boolean isSelected) {
    this.isSelected = isSelected;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.containers.AlarmInterface#getId()
   */
  @Override
  public String getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface#getReports()
   */
  @Override
  public List<AlarmReport> getReports() {
    return reports;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface#update()
   */
  @Override
  public void update() {
    if (databaseSession != null) {
      try {
        final Alarminterface rockAlarminterface = databaseSession.getAlarminterface(this.id);
        if (rockAlarminterface == null) {
          // TODO what to do if alarminterface does not exists anymore?
        } else {
          this.setDescription(rockAlarminterface.getDescription());
        }
      } catch (DatabaseException e) {
        log.severe("Database error: " + e.getMessage());
      } catch (Exception e) {
        log.severe("Fatal error: " + e.getMessage());
      }
      updateReports();
    }
  }

  /**
   * Re-read all reports from the user database, and validate them against the
   * listers results.
   */
  private void updateReports() {
    if (databaseSession != null) {
      try {
        final AlarmReportLister scheduledReportLister = authSession.getAlarmReportLister(databaseSession, this,
            new String[] {alarmProperties.getProperty(AlarmProperties.BOALARMCATEGORY)}, new Boolean[] {false});
        final AlarmReportLister reducedDelayReportLister = authSession.getAlarmReportLister(databaseSession, this,
            new String[] {alarmProperties.getProperty(AlarmProperties.BOALARMCATEGORYRD)}, new Boolean[] {true});
        final List<AlarmReport> scheduledReports = scheduledReportLister.getAvailableReports();
        final List<AlarmReport> reducedDelayReports = reducedDelayReportLister.getAvailableReports();
        final List<Alarmreport> rockAlarmreports = databaseSession.getAlarmreports(this.id);

        // disallow access to the vector while populating it.
        synchronized (reports) {
          reports.clear();
          for (Alarmreport rockAlarmreport : rockAlarmreports) {
            final AlarmReport dwhAlarmReport = new DWHAlarmReport(databaseSession, this, rockAlarmreport.getReportid());
            dwhAlarmReport.update();
            
            // default the report to invalid.
            dwhAlarmReport.setValid(false);

            for (AlarmReport boAlarmReport : scheduledReports) {
              if (dwhAlarmReport.getName().equals(boAlarmReport.getName())) {
                // the report exists in the lister as well. The report is valid.
                dwhAlarmReport.setValid(true);
                // no reason to look any further. Break
                break;
              }
            }

            for (AlarmReport boAlarmReport : reducedDelayReports) {
              if (dwhAlarmReport.getName().equals(boAlarmReport.getName())) {
                // the report exists in the lister as well. The report is valid.
                dwhAlarmReport.setValid(true);
                // no reason to look any further. Break
                break;
              }
            }

            reports.add(dwhAlarmReport);
          }
        }
      } catch (DatabaseException e) {
        log.severe("Database error: " + e.getMessage());
      } catch (Exception e) {
        log.severe("Fatal error: " + e.toString() + " (cause : " + e.getCause().toString() + " message: "
            + e.getMessage());
      }
    }
  }

  /**
   * Sets the description for this interface explicitly.
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface#getDescription()
   */
  @Override
  public String getDescription() {
    return description;
  }

}
