package com.ericsson.eniq.alarmcfg.authenticate;

import com.businessobjects.rebean.wi.ReportEngines;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;
import com.ericsson.eniq.alarmcfg.reports.BOAlarmReportLister;

/**
 * @author ecarbjo
 */
public class BOAuthSession implements AuthSession {

  /**
   * This is the auth session class for the boxi sdk.
   */
  private IEnterpriseSession boSession = null;

  /**
   * Just an empty constructor.
   */
  public BOAuthSession() {
  }

  /**
   * @param session
   *          the EnterpriseSession object that needs to be wrapped.
   */
  public BOAuthSession(final IEnterpriseSession session) {
    this.boSession = session;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.authenticate.AuthSession#getUserName()
   */
  public String getUserName() {
    if (boSession != null) {
      try {
        return boSession.getUserInfo().getUserName();
      } catch (SDKException e) {
      }
    }

    return null;
  }

  /**
   * setter for a new BO session object.
   * 
   * @param session
   *          the new session object to be wrapped.
   */
  public void setBOSession(final IEnterpriseSession session) {
    this.boSession = session;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.authenticate.AuthSession#isLoggedIn()
   */
  @Override
  public boolean isLoggedIn() {
    return boSession != null;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.alarmcfg.authenticate.AuthSession#getAlarmReportLister(com.ericsson.eniq.alarmcfg.database.DatabaseSession, com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface, java.lang.String)
   */
  @Override
  public AlarmReportLister getAlarmReportLister(final DatabaseSession dbSession, final AlarmInterface alarmInterface,
      final String[] alarmCategories, final Boolean[] isReducedDelay) {
    try {
      return new BOAlarmReportLister((IInfoStore) boSession.getService("InfoStore"), (ReportEngines) boSession
          .getService("ReportEngines"), dbSession, alarmInterface, alarmCategories, isReducedDelay);
    } catch (SDKException e) {
      e.printStackTrace();
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.authenticate.AuthSession#logout()
   */
  @Override
  public void logout() {
    if (boSession != null) {
      boSession.logoff();
      boSession = null;
    }
  }

}
