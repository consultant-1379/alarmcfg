/**
 * Interface for accessing the authenticated sessions.
 */
package com.ericsson.eniq.alarmcfg.authenticate;

import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;

/**
 * @author ecarbjo
 * 
 */
public interface AuthSession {

  /**
   * Returns the user name of the current user.
   * 
   * @return the user name that is authenticated in this session.
   */
  String getUserName();

  /**
   * @return true if this is an authenticated session.
   */
  boolean isLoggedIn();

  /**
   * Do the necessary cleanup before logging off and killing the session.
   */
  void logout();

  /**
   * Get an AlarmReportLister associated with the current authenticated session.
   * This should return a suitable lister for each authentication method.
   * 
   * @param dbSession
   *          the database reference that the created alarm reports should use
   *          to save themselves.
   * @param alarmInterface
   *          the alarm interface that is the alarm interface that the alarm
   *          reports should be automatically attached to.
   * @param alarmCategory
   *          the BO category where alarm reports are stored
   * @param isReducedDelay 
   * 
   * @return a new AlarmReportLister
   */
  AlarmReportLister getAlarmReportLister(DatabaseSession dbSession, AlarmInterface alarmInterface, String[] alarmCategory, Boolean[] isReducedDelay);
  
}
