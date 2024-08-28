/**
 * 
 */
package com.ericsson.eniq.alarmcfg.interfaces;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.ericsson.eniq.alarmcfg.authenticate.AuthSession;
import com.ericsson.eniq.alarmcfg.config.AlarmProperties;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.reports.AlarmReport;

/**
 * @author ecarbjo
 * 
 */
public class AllAlarmInterface extends DefaultAlarmInterface {

  public static final String ALL_NAME = "All";

  private AlarmInterfaceContainer container = null;

  /**
   * Empty contructor
   * 
   * @param databaseSession
   * @param lister
   * @param container
   */
  public AllAlarmInterface(final DatabaseSession databaseSession, final AuthSession authSession,
      final AlarmProperties alarmProperties, final AlarmInterfaceContainer container) {
    super(databaseSession, authSession, alarmProperties, ALL_NAME);
    this.container = container;
  }

  /**
   * Override the Default constructor so that we don't use the name.
   * 
   * @param databaseSession
   * @param lister
   * @param name
   */
  public AllAlarmInterface(final DatabaseSession databaseSession, final AuthSession authSession,
      final AlarmProperties alarmProperties, final String name) {
    super(databaseSession, authSession, alarmProperties, name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.DefaultAlarmInterface#isRealInterface
   * ()
   */
  @Override
  public boolean isRealInterface() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.alarmcfg.interfaces.DefaultAlarmInterface#getReports()
   */
  /**
   * This override will cause this pseudo interface to return ALL reports for
   * ALL interfaces in the container that this instance is bound to.
   */
  @Override
  public List<AlarmReport> getReports() {
    final Vector<AlarmReport> reports = new Vector<AlarmReport>();

    // get all reports from the container.
    final Enumeration<AlarmInterface> interfaces = container.getElements();
    while (interfaces.hasMoreElements()) {
      // add all the reports for all interfaces to the vector and return it.
      final AlarmInterface currentInterface = interfaces.nextElement();

      // we only add reports from REAL interfaces.
      if (currentInterface.isRealInterface()) {
        final List<AlarmReport> currentReports = currentInterface.getReports();
        for (AlarmReport currentReport : currentReports) {
          reports.add(currentReport);
        }
      }
    }
    return reports;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.interfaces.DefaultAlarmInterface#update()
   */
  @Override
  public void update() {
    // update all interfaces so that we get the newest available data from the
    // database.
    container.updateAll();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.interfaces.DefaultAlarmInterface#getId()
   */
  @Override
  public String getId() {
    return "All";
  }
}
