package com.ericsson.eniq.alarmcfg.authenticate;

import com.ericsson.eniq.alarmcfg.database.DatabaseSession;
import com.ericsson.eniq.alarmcfg.interfaces.AlarmInterface;
import com.ericsson.eniq.alarmcfg.reports.AlarmReportLister;

/**
 * @author ecarbjo
 */
public class TestAuthSession implements AuthSession {

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.alarmcfg.authenticate.AuthSession#getUserName()
   */
  public String getUserName() {
    return "eniq_alarm";
  }

  @Override
  public boolean isLoggedIn() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void logout() {
    // TODO Auto-generated method stub

  }

  @Override
  public AlarmReportLister getAlarmReportLister(final DatabaseSession dbSession, final AlarmInterface alarmInterface,
      final String[] alarmCategory, final Boolean[] alarmCategoryTypes) {
    // TODO Auto-generated method stub
    return null;
  }

}
